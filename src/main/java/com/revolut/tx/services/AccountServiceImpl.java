package com.revolut.tx.services;

import com.revolut.tx.dal.datasource.H2dbDataSourceProvider;
import com.revolut.tx.exceptions.AlreadyExistException;
import com.revolut.tx.exceptions.InsufficientBalanceException;
import com.revolut.tx.exceptions.NoSuchAccountException;
import com.revolut.tx.model.Account;
import com.revolut.tx.model.TransactionDetails;
import com.revolut.tx.services.factory.BaseDao;
import com.revolut.tx.services.factory.ServiceFactory;
import com.revolut.tx.util.RevolutConstants;
import org.glassfish.hk2.utilities.reflection.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class AccountServiceImpl implements IAccountService {
    Logger LOG = Logger.getLogger();

    private ExecutorService executor = ServiceFactory.getInstance().executorService();

    private Lock lock = new ReentrantLock();

    @Override
    public Long getAccountBalance(String accountId) throws NoSuchAccountException {
        Long result = getAccount(accountId);
        return result != null ? result : 0L;
    }

    private <T> T exclusiveLock(Supplier<T> code) {
        try {
            if (lock.tryLock(4, TimeUnit.SECONDS)) {
                try {
                    return code.get();
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Unable to get lock Timed out");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Long getAccount(String accountId) {
        Future<Long> future = executor.submit(() -> {
            return exclusiveLock(() -> {
                return BaseDao.execute(connection -> {
                    Long response = null;
                    try (PreparedStatement statement = connection
                            .prepareStatement("SELECT balance " + "FROM account " + "WHERE accountId = ?")) {
                        statement.setString(1, accountId);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            response = resultSet.getLong(1);
                        }
                    }
                    return response;
                });
            });
        });
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long addAccountBalance(String accountId, Long balance) throws NoSuchAccountException {
        checkAccountExist(accountId);
        updateAccount(accountId, balance);
        return balance;
    }

    @Override
    public Long withDrawAmount(String accountId, Long balance)
            throws InsufficientBalanceException, NoSuchAccountException {
        checkAccountExist(accountId);
        Long fromBalance = getAccountBalance(accountId);
        if (fromBalance < balance)
            throw new InsufficientBalanceException(RevolutConstants.INSUFFICIENT_BALANCE + accountId);
        if (balance > 0 && fromBalance >= balance) {
            addAccountBalance(accountId, (-1) * balance);
        }
        return getAccountBalance(accountId);
    }

    private int updateAccount(String accountId, Long remainingBalance) {

        Future<Integer> future = executor.submit(() -> {
            return exclusiveLock(() -> {
                return BaseDao.execute(connection -> {
                    int response;
                    try (PreparedStatement statement = connection.prepareStatement(
                            "UPDATE account " + "SET balance = balance + ? " + "WHERE accountId = ?")) {
                        statement.setDouble(1, remainingBalance);
                        statement.setString(2, accountId);

                        response = statement.executeUpdate();
                    }
                    return response;
                });
            });
        });
        try {
            if (future.get() == null)
                throw new NoSuchAccountException(RevolutConstants.NO_SUCH_ACCOUNT + accountId);
            return future.get().intValue();
        } catch (ExecutionException | InterruptedException | NoSuchAccountException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void transferAccountBalance(TransactionDetails transactionDetails)
            throws NoSuchAccountException, InsufficientBalanceException {
        checkAccountExist(transactionDetails.getFromAccountId());
        Long fromBalance = getAccountBalance(transactionDetails.getFromAccountId());
        if (fromBalance < transactionDetails.getAmount())
            throw new InsufficientBalanceException(
                    RevolutConstants.INSUFFICIENT_BALANCE + transactionDetails.getFromAccountId());

        if (fromBalance >= transactionDetails.getAmount()) {
            addAccountBalance(transactionDetails.getFromAccountId(), (-1) * transactionDetails.getAmount());
            addAccountBalance(transactionDetails.getToAccountId(), transactionDetails.getAmount());
        }
    }

    private void checkAccountExist(String accId) throws NoSuchAccountException {
        Account receiver = getAccountDetails(accId);
        if (receiver == null)
            throw new NoSuchAccountException(RevolutConstants.NO_SUCH_ACCOUNT + accId);
    }

    @Override
    public int createAccount(String accountId, Long initialAmount) throws AlreadyExistException {
        Future<Integer> future = executor.submit(() -> {
            return exclusiveLock(() -> {
                return BaseDao.execute(connection -> {
                    try (PreparedStatement statement = connection
                            .prepareStatement("insert into account ( accountId, balance) " + "VALUES (?,?);");) {
                        statement.setString(1, accountId);
                        statement.setDouble(2, initialAmount);
                        return statement.executeUpdate();
                    }
                });
            });
        });
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Account getAccountDetails(String accountId) throws NoSuchAccountException {
        Long result = getAccount(accountId);
        if (result == null)
            throw new NoSuchAccountException(RevolutConstants.NO_SUCH_ACCOUNT + accountId);
        return new Account(accountId, result);
    }

}

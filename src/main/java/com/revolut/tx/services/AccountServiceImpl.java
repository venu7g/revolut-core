package com.revolut.tx.services;

import com.revolut.tx.dal.datasource.H2dbDataSourceProvider;
import com.revolut.tx.exceptions.AlreadyExistException;
import com.revolut.tx.exceptions.InsufficientBalanceException;
import com.revolut.tx.exceptions.NoSuchAccountException;
import com.revolut.tx.model.Account;
import com.revolut.tx.model.TransactionDetails;
import com.revolut.tx.util.RevolutConstants;
import org.glassfish.hk2.utilities.reflection.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AccountServiceImpl implements IAccountService {
	Logger LOG = Logger.getLogger();
	private H2dbDataSourceProvider datasource = H2dbDataSourceProvider.getInstance();

	@Override
	public Long getAccountBalance(String accountId) throws NoSuchAccountException {
		Long result =getAccount(accountId);
	          
		return result!=null?result:0L;
	}
	
	public Long getAccount(String accountId) {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Future<Long> future = executor.submit(() -> {
			return datasource.execute(connection -> {
				try (PreparedStatement statement = connection
						.prepareStatement("SELECT balance " + "FROM account " + "WHERE accountId = ?")) {
					statement.setString(1, accountId);
					ResultSet resultSet = statement.executeQuery();
					if (resultSet == null) {
						return null;
					}
					if (resultSet.next()) {
						return resultSet.getLong(1);
					}
				}
				return null;
			});
		});
		try {
			return future.get()!=null ? future.get():null;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		finally {
			executor.shutdown();
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
		ExecutorService executor = Executors.newFixedThreadPool(2);
		int result = 0;
		try {
			Future<Integer> future = executor.submit(() -> {
				return datasource.execute(connection -> {
					try (PreparedStatement statement = connection.prepareStatement(
							"UPDATE account " + "SET balance = balance + ? " + "WHERE accountId = ?")) {
						statement.setDouble(1, remainingBalance);
						statement.setString(2, accountId);

						return statement.executeUpdate();
					}
				});
			});
			if (future.get() == null)
				throw new NoSuchAccountException(RevolutConstants.NO_SUCH_ACCOUNT + accountId);
			result = future.get().intValue();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
		return result;
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
	public void createAccount(String accountId, Long initialAmount) throws AlreadyExistException {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Future<Integer> future = executor.submit(() -> {
			return datasource.execute(connection -> {
				try (PreparedStatement statement = connection
						.prepareStatement("insert into account ( accountId, balance) " + "VALUES (?,?);");) {
					statement.setString(1, accountId);
					statement.setDouble(2, initialAmount);
					return statement.executeUpdate();
				}
			});
		});
		executor.shutdown();
	}

	@Override
	public Account getAccountDetails(String accountId) throws NoSuchAccountException {
		Long result =getAccount(accountId);
		if(result==null)
			throw new NoSuchAccountException(RevolutConstants.NO_SUCH_ACCOUNT + accountId);
		return new Account(accountId, result);
	}

}

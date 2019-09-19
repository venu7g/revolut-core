package com.revolut.tx.services;

import com.revolut.tx.exceptions.AlreadyExistException;
import com.revolut.tx.exceptions.InsufficientBalanceException;
import com.revolut.tx.exceptions.NoSuchAccountException;
import com.revolut.tx.model.Account;
import com.revolut.tx.model.TransactionDetails;

public interface IAccountService {
    Long getAccountBalance(final String accountId) throws NoSuchAccountException,AlreadyExistException;
    Long addAccountBalance(final String accountId,final Long balance) throws NoSuchAccountException ;
    Long withDrawAmount(final String accountId,final Long balance) throws InsufficientBalanceException,NoSuchAccountException;
    void transferAccountBalance(final TransactionDetails transactionDetails) throws NoSuchAccountException, InsufficientBalanceException ;

    int createAccount(String accountId, Long initialAmount) throws AlreadyExistException;

    Account getAccountDetails(String accountId) throws NoSuchAccountException;
}

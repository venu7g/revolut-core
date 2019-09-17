package com.revolut.tx;

import com.revolut.tx.exceptions.AlreadyExistException;
import com.revolut.tx.exceptions.InsufficientBalanceException;
import com.revolut.tx.exceptions.NoSuchAccountException;
import com.revolut.tx.model.Account;
import com.revolut.tx.model.TransactionDetails;
import com.revolut.tx.services.IAccountService;
import com.revolut.tx.services.factory.ServiceFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionServiceTest {
    private static IAccountService accountService;
    private static Account senderAccount, recipientAccount;

    @BeforeClass
    public static void setUp(){
        accountService  = ServiceFactory.getInstance().accountService();
    }


    @AfterClass
    public static void tearDown(){
        accountService = null;
    }

    @Test
    public void testTransferAccountBalance() throws InsufficientBalanceException, NoSuchAccountException, AlreadyExistException {
        String senderAccountId = "1234";
        String recipientAccountId = "4567";
        Long initialBalance = 1000L;
        Long transactionAmount = 500L;

        accountService.createAccount(senderAccountId,initialBalance);
        accountService.createAccount(recipientAccountId,initialBalance);
        TransactionDetails transactionDetails = new TransactionDetails(transactionAmount,senderAccountId, recipientAccountId);

        accountService.transferAccountBalance(transactionDetails);
        Assert.assertEquals((initialBalance-transactionAmount),accountService.getAccountBalance(senderAccountId),0);
        Assert.assertEquals((initialBalance+transactionAmount),accountService.getAccountBalance(recipientAccountId),0);
    }

    @Test
    public void testTransferAccountBalance_NoSuchAccountException_1() throws AlreadyExistException {
        String senderAccountId = "081234";
        String recipientAccountId = "200379878";

        accountService.createAccount(senderAccountId,1000L);

        TransactionDetails transactionDetails = new TransactionDetails(500L,senderAccountId, recipientAccountId);
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.transferAccountBalance(transactionDetails));
    }

    @Test
    public void testTransferAccountBalance_NoSuchAccountException_2() throws AlreadyExistException {
        String senderAccountId = "2004";
        String recipientAccountId = "2005";

        accountService.createAccount(recipientAccountId,1000L);

        TransactionDetails transactionDetails = new TransactionDetails(500L,senderAccountId, recipientAccountId);
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.transferAccountBalance(transactionDetails));
    }

    @Test
    public void testTransferAccountBalance_InsufficientBalanceException() throws AlreadyExistException, NoSuchAccountException {
        String senderAccountId = "2007";
        String recipientAccountId = "2006";
        Long initialBalance = 1000L;
        Long transactionAmount = 2000L;
        accountService.createAccount(senderAccountId,initialBalance);
        senderAccount =  new Account(senderAccountId,initialBalance);
        accountService.createAccount(recipientAccountId,initialBalance);
        recipientAccount = new Account(recipientAccountId,initialBalance);
        TransactionDetails transactionDetails = new TransactionDetails(transactionAmount,senderAccountId, recipientAccountId);
        Assert.assertThrows(InsufficientBalanceException.class, () -> accountService.transferAccountBalance(transactionDetails));
    }
}

package com.revolut.tx;

import com.revolut.tx.exceptions.AlreadyExistException;
import com.revolut.tx.exceptions.InsufficientBalanceException;
import com.revolut.tx.exceptions.NoSuchAccountException;
import com.revolut.tx.model.Account;
import com.revolut.tx.services.IAccountService;
import com.revolut.tx.services.factory.ServiceFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AccountServiceTest {

    private static IAccountService accountService;

    @BeforeClass
    public static void setUp(){
        accountService  = ServiceFactory.getInstance().accountService();
    }

    @AfterClass
    public static void tearDown(){
        accountService = null;
    }

    @Test
    public void testCreateAndGetAccount() throws NoSuchAccountException, AlreadyExistException {
       String accountId = "9876";
        Long initialBalance = 1000L;
        accountService.createAccount(accountId,initialBalance);
        Account account = new Account(accountId,initialBalance);
        Account account1 = accountService.getAccountDetails(accountId);
        Assert.assertEquals(account.getAccountId(),account1.getAccountId());
    }


    @Test
    public void testGetAccountDetails_NoSuchAccountException(){
        String accountId = "098";
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.getAccountDetails(accountId));
    }

    @Test
    public void testGetAccountDetailsBalance() throws NoSuchAccountException, AlreadyExistException {
        String accountId = "9876";
        Long initialBalance = 1000L;
        accountService.createAccount(accountId,initialBalance);
        Assert.assertEquals(initialBalance,accountService.getAccountBalance(accountId));
    }

    @Test
    public void testGetAccountDetailsBalance_NoSuchAccountException(){
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.getAccountDetails("2348856"));
    }



    @Test
    public void withDrawAmount_NoSuchAccountException(){
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.withDrawAmount("283456",1000L));
    }

    @Test
    public void withDrawAmount_InsufficientBalanceException() throws AlreadyExistException {
        String accountId = "7777";
        Long balance = 1000L;
        Long withdrawnAmount = 2000L;
        accountService.createAccount(accountId,balance);
        Assert.assertThrows(InsufficientBalanceException.class, () -> accountService.withDrawAmount(accountId,withdrawnAmount));
    }

    @Test
    public void testAddAccountBalance() throws NoSuchAccountException, AlreadyExistException {
        String accountId = "90101";
        Long balance = 1000l;
        Long depositedAmount = 200l;

        accountService.createAccount(accountId,balance);
        accountService.addAccountBalance(accountId,depositedAmount);
        Long domain = balance + depositedAmount;
        Long dbresult = accountService.getAccountBalance(accountId);
        Assert.assertEquals(domain,dbresult);
    }

    @Test
    public void testAddAccountBalance_NoSuchAccountException() {
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.addAccountBalance("822333",1000L));
    }

}

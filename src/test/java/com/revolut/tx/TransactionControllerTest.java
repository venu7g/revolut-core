package com.revolut.tx;

import com.revolut.tx.exceptions.AlreadyExistException;
import com.revolut.tx.exceptions.NoSuchAccountException;
import com.revolut.tx.services.IAccountService;
import com.revolut.tx.services.factory.ServiceFactory;
import com.revolut.tx.util.RevolutConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

public class TransactionControllerTest {

    @ClassRule
    public static final ServerExternalResource externalResource = new ServerExternalResource();
    
    private static IAccountService accountService = ServiceFactory.getInstance().accountService();

    @BeforeClass
    public static void setUp() throws Exception {
        externalResource.before();
    }
    @AfterClass
    public static void tearDown() throws Exception {
        externalResource.after();
    }
    
    
    public void testCreateAccount(String accId,Long amt) throws IOException, AlreadyExistException {
    	
    	accountService.createAccount(accId, amt);
		/*
		 * HttpUriRequest request = new
		 * HttpPost(RevolutConstants.ACCOUNT_OPERATIONS_PATH +
		 * "create?accountId="+accId+"&amount="+amt); HttpResponse response =
		 * HttpClientBuilder.create().build().execute(request);
		 * Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode()
		 * );
		 */
    }


    @Test
    public void testTransaction() throws IOException, AlreadyExistException, NoSuchAccountException {
        String accountId1 = "1911";
        Long initialAmount1 = 1000L;
        String accountId2 = "2811";
        Long initialAmount2 = 1000L;
        Long transferAmount = 500L;

        testCreateAccount(accountId1,initialAmount1);
        testCreateAccount(accountId2,initialAmount2);

        HttpUriRequest request = new HttpPost(RevolutConstants.TRANSACTION_OPERATIONS_PATH +
                "?from=" + accountId1 +
                "&to=" + accountId2 +
                "&amount=" + String.valueOf(transferAmount));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testTransaction_InsufficientBalance() throws  IOException, AlreadyExistException {
        String accountId1 = "21009";
        Long initialAmount1 = 1000L;
        String accountId2 = "22009";
        Long initialAmount2 = 1000L;
        Long transferAmount = 2000L;

        testCreateAccount(accountId1,initialAmount1);
        testCreateAccount(accountId2,initialAmount2);


        HttpUriRequest request = new HttpPost(RevolutConstants.TRANSACTION_OPERATIONS_PATH +
                "?from=" + accountId1+
                "&to=" + accountId2+
                "&amount=" +  String.valueOf(transferAmount));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        String json = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("insufficient balance"+RevolutConstants.INSUFFICIENT_BALANCE + accountId1,json);
    }

    @Test
    public void testTransaction_InvalidTransferAmount() throws AlreadyExistException, IOException {
        String accountId1 = "2399";
        Long initialAmount1 = 1000L;
        String accountId2 = "2477";
        Long initialAmount2 = 1000L;
        Long transferAmount = -100L;

        testCreateAccount(accountId1,initialAmount1);
        testCreateAccount(accountId2,initialAmount2);


        HttpUriRequest request = new HttpPost(RevolutConstants.TRANSACTION_OPERATIONS_PATH +
                "?from=" + accountId1 +
                "&to=" + accountId2 +
                "&amount=" +  String.valueOf(transferAmount));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(RevolutConstants.NEGATIVE_INITIAL_BALANCE,EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testTransaction_NoSuchAccount() throws AlreadyExistException, IOException {
        String accountId1 = "2598";
        Long initialAmount1 = 1000L;
        String accountId2 = "2698";
        Long transferAmount = -100L;

        testCreateAccount(accountId1,initialAmount1);


        HttpUriRequest request = new HttpPost(RevolutConstants.TRANSACTION_OPERATIONS_PATH +
                "?from=" + accountId1 +
                "&to=" + accountId2 +
                "&amount=" +  String.valueOf(transferAmount));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(RevolutConstants.NEGATIVE_INITIAL_BALANCE,EntityUtils.toString(response.getEntity()));
    }

  
}

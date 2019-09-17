package com.revolut.tx;

import com.revolut.tx.model.Account;
import com.revolut.tx.util.JsonUtil;
import com.revolut.tx.util.RevolutConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
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


public class AccountControllerTest {

    @ClassRule
    public static final ServerExternalResource externalResource = new ServerExternalResource();

    @BeforeClass
    public static void setUp() throws Exception {
        externalResource.before();
    }
    @AfterClass
    public static void tearDown() throws Exception {
        externalResource.after();
    }

    @Test
    public void testCreateAccount() throws IOException {
        HttpUriRequest request = new HttpPost(RevolutConstants.ACCOUNT_OPERATIONS_PATH + "create?accountId=1579");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }


    @Test
    public void testCreateAccount_NegativeInitialAmount() throws IOException {
        HttpUriRequest request = new HttpPost(RevolutConstants.ACCOUNT_OPERATIONS_PATH + "create?accountId=1579&amount=-5");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(RevolutConstants.NEGATIVE_INITIAL_BALANCE,EntityUtils.toString(response.getEntity()));
    }


    @Test
    public void testCreateAccount_InvalidID_2() throws IOException {
        HttpUriRequest request = new HttpPost(RevolutConstants.ACCOUNT_OPERATIONS_PATH + "create");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(RevolutConstants.INVALID_ID + RevolutConstants.NULL_OR_EMPTY_ID,EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testGetAccount() throws IOException {
        String accountId = "1779";
        Long initialAmount = 1000L;
        Account account = new Account(accountId,initialAmount);

        HttpUriRequest request1 = new HttpPost(RevolutConstants.ACCOUNT_OPERATIONS_PATH + "create?accountId=" + accountId + "&amount=" + initialAmount);
        HttpClientBuilder.create().build().execute(request1);
        HttpUriRequest request2 = new HttpGet(RevolutConstants.ACCOUNT_OPERATIONS_PATH + accountId);
        HttpResponse response = HttpClientBuilder.create().build().execute(request2);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        String json = EntityUtils.toString(response.getEntity());
        Account receivedAccount = JsonUtil.getObject(json,Account.class);
        Assert.assertEquals(account,receivedAccount);
    }

    @Test
    public void testGetAccount_NoSuchAccount() throws IOException {
        String accountId = "1879";
        HttpUriRequest request = new HttpGet(RevolutConstants.ACCOUNT_OPERATIONS_PATH + accountId.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
    }


}

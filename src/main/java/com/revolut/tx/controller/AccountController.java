package com.revolut.tx.controller;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.revolut.tx.model.Account;
import com.revolut.tx.services.IAccountService;
import com.revolut.tx.services.factory.ServiceFactory;
import com.revolut.tx.util.JsonUtil;
import com.revolut.tx.util.RequestInputValidationUtil;


@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {


    private IAccountService accountService =  ServiceFactory.getInstance().accountService();

    @POST
    @Path("/create")
    public Response createAccount(@QueryParam("accountId") String accountId,
                           @QueryParam("amount") String initialAmount) {

        try {
            RequestInputValidationUtil.validateId(accountId);
            Long baseamount = RequestInputValidationUtil.validateInitialAmount(initialAmount);
            accountService.createAccount(accountId,baseamount);
            return Response.status(Response.Status.OK).build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") String accountId){
        try {
            RequestInputValidationUtil.validateId(accountId);
            Optional<Account> account  = Optional.ofNullable(accountService.getAccountDetails(accountId));
            return Response.status(Response.Status.OK).entity(JsonUtil.getJson(account.get())).build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}

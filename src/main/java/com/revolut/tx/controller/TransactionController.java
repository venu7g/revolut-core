package com.revolut.tx.controller;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.revolut.tx.exceptions.NoSuchAccountException;
import com.revolut.tx.model.TransactionDetails;
import com.revolut.tx.services.IAccountService;
import com.revolut.tx.services.factory.ServiceFactory;
import com.revolut.tx.util.RequestInputValidationUtil;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    private IAccountService accountService = ServiceFactory.getInstance().accountService();

    @POST
    public Response transfer(@QueryParam("from") String senderAccount,
                             @QueryParam("to") String recipientAccount,
                             @QueryParam("amount") String amount){
        try {
            RequestInputValidationUtil.validateId(senderAccount);
            RequestInputValidationUtil.validateId(recipientAccount);
            RequestInputValidationUtil.validateInitialAmount(amount);
            TransactionDetails transaction = new TransactionDetails(Long.parseLong(amount),senderAccount,recipientAccount);
            accountService.transferAccountBalance(transaction);
            return Response.ok().build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

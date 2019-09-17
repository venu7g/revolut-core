package com.revolut.tx.util;

import javax.ws.rs.BadRequestException;

public class RequestInputValidationUtil {
    public static String validateId(String id) throws BadRequestException{
        if(id == null || id.isEmpty()){
            throw new BadRequestException(RevolutConstants.INVALID_ID + RevolutConstants.NULL_OR_EMPTY_ID);
        }        
        return id;
    }

    public static Long validateInitialAmount(String initialAmount) throws BadRequestException{
        Long parsedInitialAmount;
        if(initialAmount == null || initialAmount.isEmpty()){
            return 0L;
        }else {
            try {
                parsedInitialAmount = Long.parseLong(initialAmount);
            }catch (Exception e){
                throw new BadRequestException(RevolutConstants.INVALID_INITIAL_BALANCE + initialAmount);
            }
        }
        if(parsedInitialAmount < 0){
            throw new BadRequestException(RevolutConstants.NEGATIVE_INITIAL_BALANCE);
        }
        return parsedInitialAmount;
    }

    public static Long validateTransferAmount(String transferAmount) throws BadRequestException{
        Long parsedAmount;

        if(transferAmount == null || transferAmount.isEmpty()){
            throw new BadRequestException(RevolutConstants.INVALID_TRANSFER_AMOUNT + transferAmount);
        }else {
            try {
                parsedAmount = Long.parseLong(transferAmount);
            }catch (Exception e){
                throw new BadRequestException(RevolutConstants.INVALID_TRANSFER_AMOUNT + transferAmount);
            }
        }
        if(parsedAmount < 0){
            throw new BadRequestException(RevolutConstants.NEGATIVE_TRANSFER_AMOUNT);
        }
        return parsedAmount;
    }
}

package com.revolut.tx.exceptions;


import com.revolut.tx.util.RevolutConstants;

public class AlreadyExistException extends Exception{
    public AlreadyExistException(Long accountId){
        super(RevolutConstants.ACCOUNT_ID_ALREADY_EXISTS + accountId);
    }
}

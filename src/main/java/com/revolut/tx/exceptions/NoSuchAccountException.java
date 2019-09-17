package com.revolut.tx.exceptions;

public class NoSuchAccountException extends Exception{
    public NoSuchAccountException(String accountId){
        super("Invalid Account" + accountId);
    }
}

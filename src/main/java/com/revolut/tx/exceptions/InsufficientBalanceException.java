package com.revolut.tx.exceptions;

public class InsufficientBalanceException extends Exception{
    public InsufficientBalanceException(String accountId){
        super("insufficient balance" + accountId);
    }
}

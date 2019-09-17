package com.revolut.tx.services.factory;

import com.revolut.tx.services.AccountServiceImpl;
import com.revolut.tx.services.IAccountService;

public class ServiceFactory {

    private static volatile ServiceFactory INSTANCE;
    private ServiceFactory(){}

    public static ServiceFactory getInstance(){
        if(INSTANCE == null){
            synchronized (ServiceFactory.class) {
                if(INSTANCE == null) {
                    INSTANCE = new ServiceFactory();
                }
            }
        }
        return INSTANCE;
    }
    public IAccountService accountService(){
        return new AccountServiceImpl();
    }
}

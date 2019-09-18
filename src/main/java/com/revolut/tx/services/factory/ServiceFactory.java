package com.revolut.tx.services.factory;

import com.revolut.tx.services.AccountServiceImpl;
import com.revolut.tx.services.IAccountService;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceFactory {

    private static volatile ServiceFactory INSTANCE;

    private ServiceFactory() {
    }

    private static ExecutorService executor = null;

    public static ServiceFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceFactory();
                }
            }
        }
        return INSTANCE;
    }

    public IAccountService accountService() {
        return new AccountServiceImpl();
    }

    public ExecutorService executorService() {
        if (Objects.isNull(executor)) {
            executor = Executors.newFixedThreadPool(4);
            addShutdownHook();
        }
        return executor;
    }

    public static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!Objects.isNull(executor)) {
                executor.shutdownNow();
            }
        }));
    }
}

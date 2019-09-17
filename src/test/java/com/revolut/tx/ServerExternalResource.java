package com.revolut.tx;

import com.revolut.tx.Application;
import org.junit.rules.ExternalResource;

public class ServerExternalResource extends ExternalResource {
    @Override
    protected void before() throws Exception {
        Application.initServer();
        Application.server.start();
    }

    @Override
    protected void after() {
        if (Application.server.isRunning()) {
            try {
                Application.server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


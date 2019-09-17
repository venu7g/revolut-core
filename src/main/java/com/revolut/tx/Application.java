package com.revolut.tx;

import com.revolut.tx.controller.AccountController;
import com.revolut.tx.controller.TransactionController;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

public class Application {
    public static Server server;

    public static void main(String[] args) throws Exception {
        initServer();
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    public static void initServer(){
        if(server == null){
            synchronized (Application.class) {
                if(server == null) {
                    QueuedThreadPool threadPool = new QueuedThreadPool(100, 10, 120);
                    server = new Server(threadPool);
                    ServerConnector connector = new ServerConnector(server);
                    connector.setPort(8777);
                    server.setConnectors(new Connector[] { connector });
                    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                    context.setContextPath("/");
                    server.setHandler(context);
                    ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
                    jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
                            AccountController.class.getCanonicalName() + "," +
                                    TransactionController.class.getCanonicalName());
                }
            }
        }
    }
}

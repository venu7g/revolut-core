package com.revolut.tx.dal.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.engine.Session;
import org.h2.mvstore.tx.Transaction;

public  class H2dbDataSourceProvider  {

    private static volatile H2dbDataSourceProvider INSTANCE;
    private  HikariConfig config = null;
    private  HikariDataSource ds = null;
    private H2dbDataSourceProvider(){
    	 config =  new HikariConfig();
    	 config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/db.sql'");
         config.setUsername("");
         config.setPassword("");
         config.addDataSourceProperty("cachePrepStmts", "true");
         config.addDataSourceProperty("prepStmtCacheSize", "250");
         config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         ds = new HikariDataSource(config);
    }

    public static H2dbDataSourceProvider getInstance(){
        if(INSTANCE == null){
            synchronized (H2dbDataSourceProvider.class) {
                if(INSTANCE == null) {
                    INSTANCE = new H2dbDataSourceProvider();
                }
            }
        }
        return INSTANCE;
    }
    

    public  Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

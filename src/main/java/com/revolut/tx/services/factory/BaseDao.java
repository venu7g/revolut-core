package com.revolut.tx.services.factory;

import com.revolut.tx.dal.datasource.ConnectionCallable;
import com.revolut.tx.dal.datasource.H2dbDataSourceProvider;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public interface BaseDao {

    static Logger log = Logger.getRootLogger();

    public static <T> T execute(ConnectionCallable<T> callback) {
        AtomicReference<T> result = new AtomicReference<>();
        try (Connection conn = H2dbDataSourceProvider.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                result.set(callback.execute(conn));
                conn.commit();
                log.debug("Transaction is committed successfully.");
            } catch (SQLException e) {
                printSQLException(e);
                if (conn != null) {
                    try {
                        log.debug("Transaction is being rolled back.");
                        conn.rollback();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return result.get();
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                log.debug("SQLState: " + ((SQLException) e).getSQLState());
                log.debug("Error Code: " + ((SQLException) e).getErrorCode());
                log.debug("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    log.debug("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}



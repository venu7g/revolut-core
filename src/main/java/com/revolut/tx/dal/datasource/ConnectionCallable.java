package com.revolut.tx.dal.datasource;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionCallable<T> {
    T execute(Connection connection) throws SQLException;
}

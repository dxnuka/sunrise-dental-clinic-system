package com.sunrise.dental.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnectionManager {

    private static final DBConnectionManager INSTANCE = new DBConnectionManager();

    private static final String URL =
            "jdbc:mysql://localhost:3306/sunrise_dental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "dental_app";
    private static final String DB_PASSWORD = "DentalApp#2026";

    private DBConnectionManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found on classpath.", e);
        }
    }

    public static DBConnectionManager getInstance() {
        return INSTANCE;
    }

    /** Every DAO calls this to get a fresh connection (try-with-resources closes it). */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}

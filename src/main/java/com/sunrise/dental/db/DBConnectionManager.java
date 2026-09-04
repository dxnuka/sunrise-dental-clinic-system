package com.sunrise.dental.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ---------------------------------------------------------------------------
 * DESIGN PATTERN: SINGLETON
 * ---------------------------------------------------------------------------
 * Only one DBConnectionManager instance ever exists for the whole web
 * application. This gives every DAO a single, consistent place to obtain
 * connections and to read DB configuration from - avoiding scattered
 * DriverManager.getConnection() calls with hard-coded URLs across the code.
 *
 * Thread-safety: getInstance() is synchronized and uses the classic
 * double-checked-locking-free "eager init on class load" approach, which is
 * safe under the JVM class-loading guarantees.
 * ---------------------------------------------------------------------------
 */
public final class DBConnectionManager {

    private static final DBConnectionManager INSTANCE = new DBConnectionManager();

    // ---- EDIT THESE THREE VALUES for your local MySQL Workbench instance ----
    private static final String URL =
            "jdbc:mysql://localhost:3306/sunrise_dental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "dental_app";
    private static final String DB_PASSWORD = "password";
    // ---------------------------------------------------------------------

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

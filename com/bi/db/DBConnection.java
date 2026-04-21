package com.bi.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DBConnection — DEPRECATED.
 *
 * This class previously provided raw JDBC connections to a local MySQL database.
 * All DB operations have been migrated to use the Integration team's ERP SDK via
 * {@link ERPClient}. This stub is kept only so that any remaining compilation
 * references (e.g. in unit tests that import this class) continue to compile.
 *
 * DO NOT add new calls to {@link #getConnection()} — use {@link ERPClient} instead.
 */
@Deprecated(since = "Phase-SDK-Migration", forRemoval = true)
public class DBConnection {

    private DBConnection() { /* utility class */ }

    /**
     * @deprecated Use {@link ERPClient#get()} and its facade methods instead.
     * @throws SQLException always — direct JDBC is no longer supported
     */
    @Deprecated
    public static Connection getConnection() throws SQLException {
        throw new SQLException(
                "[DBConnection] Direct JDBC is disabled. "
                + "Use ERPClient (erp-subsystem-sdk-1.0.0.jar) for all DB operations.");
    }
}

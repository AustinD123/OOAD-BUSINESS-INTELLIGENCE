package com.bi.db;

import com.erp.sdk.config.DatabaseConfig;
import com.erp.sdk.factory.SubsystemFactory;
import com.erp.sdk.subsystem.BusinessIntelligence;
import com.erp.sdk.subsystem.SubsystemName;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * ERPClient is the singleton wrapper around the Integration team's
 * erp-subsystem-sdk BusinessIntelligence facade.
 *
 * All DB operations in ReportServiceImpl, KPIServiceImpl, SecurityServiceImpl,
 * and DashboardServiceImpl must go through this class — never through raw JDBC.
 *
 * Properties file location: src/main/resources/application-rds.properties
 * JAR: lib/erp-subsystem-sdk-1.0.0.jar
 *
 * The facade is opened lazily on first use and intentionally kept open for the
 * lifetime of the application (it manages its own HikariCP pool internally).
 * Call ERPClient.close() in a shutdown hook if you need clean teardown.
 */
public class ERPClient {

    /** ERP username used for all BI operations (audit-logged on the shared RDS). */
    public static final String BI_USER = "integration_lead";

    /** Path to the RDS config properties file — relative to the project root. */
    private static final Path PROPS_PATH =
            Path.of("src", "main", "resources", "application-rds.properties");

    // --- singleton -------------------------------------------------------

    private static BusinessIntelligence instance = null;

    private ERPClient() { /* utility class */ }

    /**
     * Returns the shared BusinessIntelligence facade, initialising it on first call.
     *
     * @return open BusinessIntelligence facade
     * @throws RuntimeException if the SDK cannot connect (wraps the checked exception)
     */
    public static synchronized BusinessIntelligence get() {
        if (instance == null) {
            try {
                DatabaseConfig config = DatabaseConfig.fromProperties(PROPS_PATH);
                instance = (BusinessIntelligence) SubsystemFactory.create(
                        SubsystemName.BUSINESS_INTELLIGENCE, config);
                System.out.println("[ERPClient] BusinessIntelligence facade initialised (RDS connected).");
            } catch (Exception e) {
                throw new RuntimeException(
                        "[ERPClient] Failed to initialise ERP SDK: " + e.getMessage(), e);
            }
        }
        return instance;
    }

    /**
     * Closes the facade and releases connection pool resources.
     * Safe to call multiple times.
     */
    public static synchronized void close() {
        if (instance != null) {
            try {
                instance.close();
                System.out.println("[ERPClient] BusinessIntelligence facade closed.");
            } catch (Exception e) {
                System.err.println("[ERPClient] Warning: error closing facade: " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }

    // --- convenience delegates -------------------------------------------
    // These thin wrappers keep the call sites in the service classes clean.

    /**
     * Delegates to {@code bi.create(table, payload, BI_USER)}.
     *
     * @param table   canonical table name
     * @param payload column→value map (only writable columns)
     * @return the auto-generated primary key value (as a long)
     */
    public static long create(String table, Map<String, Object> payload) {
        return get().create(table, payload, BI_USER);
    }

    /**
     * Delegates to {@code bi.readAll(table, filters, BI_USER)}.
     *
     * @param table   canonical table name
     * @param filters column→value equality filters (pass empty map for all rows)
     * @return list of row maps
     */
    public static List<Map<String, Object>> readAll(String table, Map<String, Object> filters) {
        return get().readAll(table, filters, BI_USER);
    }

    /**
     * Delegates to {@code bi.readById(table, idColumn, idValue, BI_USER)}.
     *
     * @param table    canonical table name
     * @param idColumn the primary-key column name
     * @param idValue  the primary-key value
     * @return the matched row map, or null if not found
     */
    public static Map<String, Object> readById(String table, String idColumn, Object idValue) {
        return get().readById(table, idColumn, idValue, BI_USER);
    }

    /**
     * Delegates to {@code bi.update(table, idColumn, idValue, payload, BI_USER)}.
     *
     * @param table    canonical table name
     * @param idColumn the primary-key column name
     * @param idValue  the primary-key value
     * @param payload  column→value map of fields to update
     */
    public static void update(String table, String idColumn, Object idValue,
                              Map<String, Object> payload) {
        get().update(table, idColumn, idValue, payload, BI_USER);
    }
}

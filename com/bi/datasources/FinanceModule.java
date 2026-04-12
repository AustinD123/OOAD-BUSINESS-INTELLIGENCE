package com.bi.datasources;

import com.bi.enums.SourceType;
import com.bi.exceptions.DataSourceException;
import com.bi.interfaces.IDataSource;
import com.bi.util.Dataset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FinanceModule implements IDataSource to provide finance data from the Finance ERP system.
 * It manages connection lifecycle and exposes mock finance records for the ETL pipeline.
 */
public class FinanceModule implements IDataSource {
    private String moduleName = "FINANCE_MODULE";
    private Dataset financeData;
    private SourceType sourceType = SourceType.ERP_MODULE;
    private boolean connected = false;
    private List<Map<String, Object>> mockFinanceRecords;

    /**
     * Constructs a FinanceModule with initial fields.
     */
    public FinanceModule() {
        this.mockFinanceRecords = new ArrayList<>();
    }

    /**
     * Connects to the finance data source and populates mock finance records.
     * Throws DataSourceException if already connected.
     */
    @Override
    public void connect() {
        if (connected) {
            throw new DataSourceException("FinanceModule already connected");
        }
        connected = true;
        populateMockFinanceRecords();
        System.out.println("[FinanceModule] Connected. Loaded " + mockFinanceRecords.size() + " mock finance records.");
    }

    /**
     * Populates the mock finance records with sample transaction data.
     */
    private void populateMockFinanceRecords() {
        mockFinanceRecords.clear();

        Map<String, Object> record1 = new HashMap<>();
        record1.put("transactionId", "TXN001");
        record1.put("carModel", "Tesla Model 3");
        record1.put("amount", 4500000.00);
        record1.put("transactionType", "REVENUE");
        record1.put("date", LocalDateTime.of(2026, 4, 1, 10, 0));
        record1.put("department", "Sales");
        mockFinanceRecords.add(record1);

        Map<String, Object> record2 = new HashMap<>();
        record2.put("transactionId", "TXN002");
        record2.put("carModel", "BMW X5");
        record2.put("amount", 150000.00);
        record2.put("transactionType", "EXPENSE");
        record2.put("date", LocalDateTime.of(2026, 4, 2, 11, 30));
        record2.put("department", "Operations");
        mockFinanceRecords.add(record2);

        Map<String, Object> record3 = new HashMap<>();
        record3.put("transactionId", "TXN003");
        record3.put("carModel", "Audi A4");
        record3.put("amount", 2400000.00);
        record3.put("transactionType", "REVENUE");
        record3.put("date", LocalDateTime.of(2026, 4, 3, 9, 15));
        record3.put("department", "Sales");
        mockFinanceRecords.add(record3);

        Map<String, Object> record4 = new HashMap<>();
        record4.put("transactionId", "TXN004");
        record4.put("carModel", "Mercedes C-Class");
        record4.put("amount", 280000.00);
        record4.put("transactionType", "EXPENSE");
        record4.put("date", LocalDateTime.of(2026, 4, 4, 14, 45));
        record4.put("department", "IT");
        mockFinanceRecords.add(record4);

        Map<String, Object> record5 = new HashMap<>();
        record5.put("transactionId", "TXN005");
        record5.put("carModel", "Toyota Camry");
        record5.put("amount", 3000000.00);
        record5.put("transactionType", "REVENUE");
        record5.put("date", LocalDateTime.of(2026, 4, 5, 13, 20));
        record5.put("department", "Sales");
        mockFinanceRecords.add(record5);
    }

    /**
     * Validates that the finance data source is connected and has available data.
     * Throws DataSourceException if not connected.
     */
    @Override
    public boolean validateSource() {
        if (!connected) {
            throw new DataSourceException("FinanceModule not connected. Call connect() first.");
        }
        return !mockFinanceRecords.isEmpty();
    }

    /**
     * Retrieves finance data from the connected source as a Dataset.
     * Throws DataSourceException if data is not available.
     */
    @Override
    public Dataset getData() {
        if (!validateSource()) {
            throw new DataSourceException("FinanceModule data not available. Validate source first.");
        }
        Dataset dataset = new Dataset(
            "FINANCE-" + LocalDateTime.now(),
            mockFinanceRecords,
            "Finance data from FinanceModule"
        );
        System.out.println("[FinanceModule] getData() called. Returning dataset with " + mockFinanceRecords.size() + " records.");
        return dataset;
    }

    /**
     * Exports finance data by calling getData() internally.
     * This is the method the ETL layer calls.
     */
    public Dataset exportData() {
        return getData();
    }

    // Getters and setters for Phase 1 field compatibility
    public String getModule() {
        return moduleName;
    }

    public void setModule(String module) {
        this.moduleName = module;
    }

    public Dataset getFinanceData() {
        return financeData;
    }

    public void setFinanceData(Dataset financeData) {
        this.financeData = financeData;
    }
}

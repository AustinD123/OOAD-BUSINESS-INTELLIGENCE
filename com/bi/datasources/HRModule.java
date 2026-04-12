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
 * HRModule implements IDataSource to provide HR data from the HR ERP system.
 * It manages connection lifecycle and exposes mock HR records for the ETL pipeline.
 */
public class HRModule implements IDataSource {
    private String moduleName = "HR_MODULE";
    private Dataset hrData;
    private SourceType sourceType = SourceType.ERP_MODULE;
    private boolean connected = false;
    private String connectionString = "HR-ERP-LOCAL";
    private List<Map<String, Object>> mockHRRecords;

    /**
     * Constructs an HRModule with initial fields.
     */
    public HRModule() {
        this.mockHRRecords = new ArrayList<>();
    }

    /**
     * Connects to the HR data source and populates mock HR records.
     * Throws DataSourceException if already connected.
     */
    @Override
    public void connect() {
        if (connected) {
            throw new DataSourceException("HRModule already connected");
        }
        connected = true;
        populateMockHRRecords();
        System.out.println("[HRModule] Connected. Loaded " + mockHRRecords.size() + " mock HR records.");
    }

    /**
     * Populates the mock HR records with sample employee data.
     */
    private void populateMockHRRecords() {
        mockHRRecords.clear();

        Map<String, Object> record1 = new HashMap<>();
        record1.put("employeeId", "EMP001");
        record1.put("name", "Alice Johnson");
        record1.put("department", "Sales");
        record1.put("role", "Sales Manager");
        record1.put("joiningDate", LocalDateTime.of(2020, 1, 15, 9, 0));
        record1.put("salary", 85000.00);
        mockHRRecords.add(record1);

        Map<String, Object> record2 = new HashMap<>();
        record2.put("employeeId", "EMP002");
        record2.put("name", "Bob Smith");
        record2.put("department", "Finance");
        record2.put("role", "Finance Analyst");
        record2.put("joiningDate", LocalDateTime.of(2021, 3, 22, 9, 0));
        record2.put("salary", 72000.00);
        mockHRRecords.add(record2);

        Map<String, Object> record3 = new HashMap<>();
        record3.put("employeeId", "EMP003");
        record3.put("name", "Carol White");
        record3.put("department", "IT");
        record3.put("role", "Software Engineer");
        record3.put("joiningDate", LocalDateTime.of(2019, 6, 10, 9, 0));
        record3.put("salary", 95000.00);
        mockHRRecords.add(record3);

        Map<String, Object> record4 = new HashMap<>();
        record4.put("employeeId", "EMP004");
        record4.put("name", "David Brown");
        record4.put("department", "HR");
        record4.put("role", "HR Specialist");
        record4.put("joiningDate", LocalDateTime.of(2022, 2, 8, 9, 0));
        record4.put("salary", 68000.00);
        mockHRRecords.add(record4);

        Map<String, Object> record5 = new HashMap<>();
        record5.put("employeeId", "EMP005");
        record5.put("name", "Emily Davis");
        record5.put("department", "Operations");
        record5.put("role", "Operations Lead");
        record5.put("joiningDate", LocalDateTime.of(2018, 9, 1, 9, 0));
        record5.put("salary", 80000.00);
        mockHRRecords.add(record5);
    }

    /**
     * Validates that the HR data source is connected and has available data.
     * Throws DataSourceException if not connected.
     */
    @Override
    public boolean validateSource() {
        if (!connected) {
            throw new DataSourceException("HRModule not connected. Call connect() first.");
        }
        return !mockHRRecords.isEmpty();
    }

    /**
     * Retrieves HR data from the connected source as a Dataset.
     * Throws DataSourceException if data is not available.
     */
    @Override
    public Dataset getData() {
        if (!validateSource()) {
            throw new DataSourceException("HRModule data not available. Validate source first.");
        }
        Dataset dataset = new Dataset(
            "HR-" + LocalDateTime.now(),
            mockHRRecords,
            "HR data from HRModule"
        );
        System.out.println("[HRModule] getData() called. Returning dataset with " + mockHRRecords.size() + " records.");
        return dataset;
    }

    /**
     * Exports HR data by calling getData() internally.
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

    public Dataset getHrData() {
        return hrData;
    }

    public void setHrData(Dataset hrData) {
        this.hrData = hrData;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
}

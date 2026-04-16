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
 * SalesModule implements IDataSource to provide sales data from the Sales ERP system.
 * It manages connection lifecycle and exposes mock sales records for the ETL pipeline.
 */
public class SalesModule implements IDataSource {
    private String moduleName = "SALES_MODULE";
    private Dataset salesData;
    private SourceType sourceType = SourceType.ERP_MODULE;
    private boolean connected = false;
    private List<Map<String, Object>> mockSalesRecords;

    /**
     * Constructs a SalesModule with initial fields.
     */
    public SalesModule() {
        this.mockSalesRecords = new ArrayList<>();
    }

    /**
     * Connects to the sales data source and populates mock sales records.
     * Throws DataSourceException if already connected.
     */
    @Override
    public void connect() {
        if (connected) {
            throw new DataSourceException("SalesModule already connected");
        }
        connected = true;
        populateMockSalesRecords();
        System.out.println("[SalesModule] Connected. Loaded " + mockSalesRecords.size() + " mock sales records.");
    }

    /**
     * Populates the mock sales records with sample data.
     */
    private void populateMockSalesRecords() {
        mockSalesRecords.clear();

        Map<String, Object> record1 = new HashMap<>();
        record1.put("carModel", "Tesla Model 3");
        record1.put("unitsSold", 150);
        record1.put("revenue", 4500000.00);
        record1.put("salesDate", LocalDateTime.of(2026, 4, 1, 10, 30));
        record1.put("dealerId", "DLR001");
        mockSalesRecords.add(record1);

        Map<String, Object> record2 = new HashMap<>();
        record2.put("carModel", "BMW X5");
        record2.put("unitsSold", 85);
        record2.put("revenue", 3400000.00);
        record2.put("salesDate", LocalDateTime.of(2026, 4, 2, 14, 15));
        record2.put("dealerId", "DLR002");
        mockSalesRecords.add(record2);

        Map<String, Object> record3 = new HashMap<>();
        record3.put("carModel", "Audi A4");
        record3.put("unitsSold", 120);
        record3.put("revenue", 2400000.00);
        record3.put("salesDate", LocalDateTime.of(2026, 4, 3, 9, 45));
        record3.put("dealerId", "DLR003");
        mockSalesRecords.add(record3);

        Map<String, Object> record4 = new HashMap<>();
        record4.put("carModel", "Mercedes C-Class");
        record4.put("unitsSold", 95);
        record4.put("revenue", 3800000.00);
        record4.put("salesDate", LocalDateTime.of(2026, 4, 4, 11, 20));
        record4.put("dealerId", "DLR004");
        mockSalesRecords.add(record4);

        Map<String, Object> record5 = new HashMap<>();
        record5.put("carModel", "Toyota Camry");
        record5.put("unitsSold", 200);
        record5.put("revenue", 3000000.00);
        record5.put("salesDate", LocalDateTime.of(2026, 4, 5, 15, 0));
        record5.put("dealerId", "DLR005");
        mockSalesRecords.add(record5);
    }

    /**
     * Validates that the sales data source is connected and has available data.
     * Throws DataSourceException if not connected.
     */
    @Override
    public boolean validateSource() {
        if (!connected) {
            throw new DataSourceException("SalesModule not connected. Call connect() first.");
        }
        return !mockSalesRecords.isEmpty();
    }

    /**
     * Retrieves sales data from the connected source as a Dataset.
     * Throws DataSourceException if data is not available.
     */
    @Override
    public Dataset getData() {
        if (!validateSource()) {
            throw new DataSourceException("SalesModule data not available. Validate source first.");
        }
        Dataset dataset = new Dataset(
            "SALES-" + LocalDateTime.now(),
            mockSalesRecords,
            "Sales data from SalesModule"
        );
        System.out.println("[SalesModule] getData() called. Returning dataset with " + mockSalesRecords.size() + " records.");
        return dataset;
    }

    /**
     * Exports sales data by calling getData() internally.
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

    public Dataset getSalesData() {
        return salesData;
    }

    public void setSalesData(Dataset salesData) {
        this.salesData = salesData;
    }
}

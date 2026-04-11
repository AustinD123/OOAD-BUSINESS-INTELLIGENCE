package com.bi.models;

import java.time.LocalDateTime;

public class ETLService {
    private final int processedDataId;
    private Object cleanedData;
    private Object transformedData;
    private final int productionCount;
    private final int salesCount;
    private final LocalDateTime processingDate;
    private final String status;

    public ETLService(int processedDataId, Object cleanedData, Object transformedData, int productionCount,
                      int salesCount, LocalDateTime processingDate, String status) {
        this.processedDataId = processedDataId;
        this.cleanedData = cleanedData;
        this.transformedData = transformedData;
        this.productionCount = productionCount;
        this.salesCount = salesCount;
        this.processingDate = processingDate;
        this.status = status;
    }

    public int getProcessedDataId() {
        return processedDataId;
    }

    public Object getCleanedData() {
        return cleanedData;
    }

    public void setCleanedData(Object cleanedData) {
        this.cleanedData = cleanedData;
    }

    public Object getTransformedData() {
        return transformedData;
    }

    public void setTransformedData(Object transformedData) {
        this.transformedData = transformedData;
    }

    public int getProductionCount() {
        return productionCount;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public LocalDateTime getProcessingDate() {
        return processingDate;
    }

    public String getStatus() {
        return status;
    }
}

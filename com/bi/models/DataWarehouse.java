package com.bi.models;

import java.time.LocalDateTime;

public class DataWarehouse {
    private final int recordId;
    private final String carModel;
    private final String dataCategory;
    private Object storedData;
    private final LocalDateTime createdAt;

    public DataWarehouse(int recordId, String carModel, String dataCategory, Object storedData, LocalDateTime createdAt) {
        this.recordId = recordId;
        this.carModel = carModel;
        this.dataCategory = dataCategory;
        this.storedData = storedData;
        this.createdAt = createdAt;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getDataCategory() {
        return dataCategory;
    }

    public Object getStoredData() {
        return storedData;
    }

    public void setStoredData(Object storedData) {
        this.storedData = storedData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

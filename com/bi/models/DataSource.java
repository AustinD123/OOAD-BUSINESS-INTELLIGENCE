package com.bi.models;

import com.bi.enums.SourceType;
import com.bi.util.Dataset;
import java.time.LocalDateTime;

public class DataSource {
    private final int sourceId;
    private final String sourceName;
    private final SourceType sourceType;
    private final String carModel;
    private final LocalDateTime dataTimestamp;
    private final Object rawData;

    public DataSource(int sourceId, String sourceName, SourceType sourceType, String carModel,
                      LocalDateTime dataTimestamp, Object rawData) {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.carModel = carModel;
        this.dataTimestamp = dataTimestamp;
        this.rawData = rawData;
    }

    public int getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public String getCarModel() {
        return carModel;
    }

    public LocalDateTime getDataTimestamp() {
        return dataTimestamp;
    }

    public Object getRawData() {
        return rawData;
    }

    public Dataset getData() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean validateSource() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

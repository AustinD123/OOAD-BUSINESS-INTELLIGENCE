package com.bi.models;

import com.bi.enums.ChartType;

public class Visualization {
    private final String vizId;
    private final ChartType chartType;
    private final Object data;

    public Visualization(String vizId, ChartType chartType, Object data) {
        this.vizId = vizId;
        this.chartType = chartType;
        this.data = data;
    }

    public String getVizId() {
        return vizId;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public Object getData() {
        return data;
    }

    public void render() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void update() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

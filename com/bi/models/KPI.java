package com.bi.models;

import com.bi.enums.KPIStatus;

public class KPI {
    private final int kpiId;
    private final String kpiName;
    private double targetValue;
    private final double actualValue;
    private final KPIStatus status;

    public KPI(int kpiId, String kpiName, double targetValue, double actualValue, KPIStatus status) {
        this.kpiId = kpiId;
        this.kpiName = kpiName;
        this.targetValue = targetValue;
        this.actualValue = actualValue;
        this.status = status;
    }

    public int getKpiId() {
        return kpiId;
    }

    public String getKpiName() {
        return kpiName;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public double getActualValue() {
        return actualValue;
    }

    public KPIStatus getStatus() {
        return status;
    }

    public double calculate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public KPIStatus evaluate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

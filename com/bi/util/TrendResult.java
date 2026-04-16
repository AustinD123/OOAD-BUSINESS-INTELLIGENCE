package com.bi.util;

import java.time.LocalDateTime;
import java.util.List;

public class TrendResult {
    private String trendId;
    private String trendName;
    private String direction;
    private double changePercent;
    private List<Double> dataPoints;
    private LocalDateTime calculatedAt;

    /**
     * Creates an empty trend result.
     */
    public TrendResult() {
    }

    /**
     * Creates a fully populated trend result.
     *
     * @param trendId the trend identifier
     * @param trendName the trend label
     * @param direction the trend direction
     * @param changePercent the percentage change from first to last data point
     * @param dataPoints the ordered list of values
     * @param calculatedAt the trend computation timestamp
     */
    public TrendResult(String trendId, String trendName, String direction, double changePercent,
            List<Double> dataPoints, LocalDateTime calculatedAt) {
        this.trendId = trendId;
        this.trendName = trendName;
        this.direction = direction;
        this.changePercent = changePercent;
        this.dataPoints = dataPoints;
        this.calculatedAt = calculatedAt;
    }

    /**
     * Returns the trend identifier.
     *
     * @return the trend id
     */
    public String getTrendId() {
        return trendId;
    }

    /**
     * Sets the trend identifier.
     *
     * @param trendId the trend id
     */
    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    /**
     * Returns the trend name.
     *
     * @return the trend name
     */
    public String getTrendName() {
        return trendName;
    }

    /**
     * Sets the trend name.
     *
     * @param trendName the trend name
     */
    public void setTrendName(String trendName) {
        this.trendName = trendName;
    }

    /**
     * Returns the trend direction.
     *
     * @return the direction value
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets the trend direction.
     *
     * @param direction the direction value
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Returns the change percentage.
     *
     * @return the change percentage
     */
    public double getChangePercent() {
        return changePercent;
    }

    /**
     * Sets the change percentage.
     *
     * @param changePercent the change percentage
     */
    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    /**
     * Returns the ordered data points used for trend calculation.
     *
     * @return the data points list
     */
    public List<Double> getDataPoints() {
        return dataPoints;
    }

    /**
     * Sets the ordered data points used for trend calculation.
     *
     * @param dataPoints the data points list
     */
    public void setDataPoints(List<Double> dataPoints) {
        this.dataPoints = dataPoints;
    }

    /**
     * Returns when the trend was calculated.
     *
     * @return the trend calculation timestamp
     */
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    /**
     * Sets when the trend was calculated.
     *
     * @param calculatedAt the trend calculation timestamp
     */
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    /**
     * Returns a concise string for trend summary.
     *
     * @return summary string with id, name, and direction
     */
    @Override
    public String toString() {
        return trendId + " | " + trendName + " = " + direction;
    }
}

package com.bi.util;

import java.time.LocalDateTime;

public class ForecastResult {
    private String forecastId;
    private String metricName;
    private double currentValue;
    private double forecastedValue;
    private double growthRate;
    private LocalDateTime forecastDate;
    private int forecastPeriodDays;
    private String confidence;

    /**
     * Creates an empty forecast result.
     */
    public ForecastResult() {
    }

    /**
     * Creates a fully populated forecast result.
     *
     * @param forecastId the forecast identifier
     * @param metricName the forecast metric name
     * @param currentValue the current metric value
     * @param forecastedValue the projected metric value
     * @param growthRate the projected growth percentage
     * @param forecastDate the forecast generation timestamp
     * @param forecastPeriodDays the number of days projected ahead
     * @param confidence the confidence label
     */
    public ForecastResult(String forecastId, String metricName, double currentValue, double forecastedValue,
            double growthRate, LocalDateTime forecastDate, int forecastPeriodDays, String confidence) {
        this.forecastId = forecastId;
        this.metricName = metricName;
        this.currentValue = currentValue;
        this.forecastedValue = forecastedValue;
        this.growthRate = growthRate;
        this.forecastDate = forecastDate;
        this.forecastPeriodDays = forecastPeriodDays;
        this.confidence = confidence;
    }

    /**
     * Returns the forecast identifier.
     *
     * @return the forecast id
     */
    public String getForecastId() {
        return forecastId;
    }

    /**
     * Sets the forecast identifier.
     *
     * @param forecastId the forecast id
     */
    public void setForecastId(String forecastId) {
        this.forecastId = forecastId;
    }

    /**
     * Returns the metric name.
     *
     * @return the metric name
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * Sets the metric name.
     *
     * @param metricName the metric name
     */
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Returns the current value.
     *
     * @return the current value
     */
    public double getCurrentValue() {
        return currentValue;
    }

    /**
     * Sets the current value.
     *
     * @param currentValue the current value
     */
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Returns the forecasted value.
     *
     * @return the forecasted value
     */
    public double getForecastedValue() {
        return forecastedValue;
    }

    /**
     * Sets the forecasted value.
     *
     * @param forecastedValue the forecasted value
     */
    public void setForecastedValue(double forecastedValue) {
        this.forecastedValue = forecastedValue;
    }

    /**
     * Returns the growth rate percentage.
     *
     * @return the growth rate
     */
    public double getGrowthRate() {
        return growthRate;
    }

    /**
     * Sets the growth rate percentage.
     *
     * @param growthRate the growth rate
     */
    public void setGrowthRate(double growthRate) {
        this.growthRate = growthRate;
    }

    /**
     * Returns the forecast timestamp.
     *
     * @return the forecast date and time
     */
    public LocalDateTime getForecastDate() {
        return forecastDate;
    }

    /**
     * Sets the forecast timestamp.
     *
     * @param forecastDate the forecast date and time
     */
    public void setForecastDate(LocalDateTime forecastDate) {
        this.forecastDate = forecastDate;
    }

    /**
     * Returns the forecast period in days.
     *
     * @return the forecast period in days
     */
    public int getForecastPeriodDays() {
        return forecastPeriodDays;
    }

    /**
     * Sets the forecast period in days.
     *
     * @param forecastPeriodDays the forecast period in days
     */
    public void setForecastPeriodDays(int forecastPeriodDays) {
        this.forecastPeriodDays = forecastPeriodDays;
    }

    /**
     * Returns the confidence label.
     *
     * @return the confidence label
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * Sets the confidence label.
     *
     * @param confidence the confidence label
     */
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    /**
     * Returns a concise string for forecast summary.
     *
     * @return summary string with id, metric, and forecasted value
     */
    @Override
    public String toString() {
        return forecastId + " | " + metricName + " = " + forecastedValue;
    }
}

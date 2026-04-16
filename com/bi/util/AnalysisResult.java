package com.bi.util;

import java.time.LocalDateTime;
import java.util.Map;

public class AnalysisResult {
    private String analysisId;
    private String metricName;
    private double metricValue;
    private double productionEfficiency;
    private double defectRate;
    private LocalDateTime analysisDate;
    private String sourceDatasetId;
    private Map<String, Double> breakdown;

    /**
     * Creates an empty analysis result.
     */
    public AnalysisResult() {
    }

    /**
     * Creates a fully populated analysis result.
     *
     * @param analysisId the unique analysis identifier
     * @param metricName the analyzed metric name
     * @param metricValue the computed metric value
     * @param productionEfficiency the efficiency ratio between 0.0 and 1.0
     * @param defectRate the defect ratio between 0.0 and 1.0
     * @param analysisDate the date and time of analysis
     * @param sourceDatasetId the source dataset identifier
     * @param breakdown the grouped metric breakdown map
     */
    public AnalysisResult(String analysisId, String metricName, double metricValue, double productionEfficiency,
            double defectRate, LocalDateTime analysisDate, String sourceDatasetId, Map<String, Double> breakdown) {
        this.analysisId = analysisId;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.productionEfficiency = productionEfficiency;
        this.defectRate = defectRate;
        this.analysisDate = analysisDate;
        this.sourceDatasetId = sourceDatasetId;
        this.breakdown = breakdown;
    }

    /**
     * Returns the analysis identifier.
     *
     * @return the analysis id
     */
    public String getAnalysisId() {
        return analysisId;
    }

    /**
     * Sets the analysis identifier.
     *
     * @param analysisId the analysis id
     */
    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
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
     * Returns the computed metric value.
     *
     * @return the metric value
     */
    public double getMetricValue() {
        return metricValue;
    }

    /**
     * Sets the computed metric value.
     *
     * @param metricValue the metric value
     */
    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    /**
     * Returns the production efficiency ratio.
     *
     * @return the production efficiency
     */
    public double getProductionEfficiency() {
        return productionEfficiency;
    }

    /**
     * Sets the production efficiency ratio.
     *
     * @param productionEfficiency the production efficiency
     */
    public void setProductionEfficiency(double productionEfficiency) {
        this.productionEfficiency = productionEfficiency;
    }

    /**
     * Returns the defect rate ratio.
     *
     * @return the defect rate
     */
    public double getDefectRate() {
        return defectRate;
    }

    /**
     * Sets the defect rate ratio.
     *
     * @param defectRate the defect rate
     */
    public void setDefectRate(double defectRate) {
        this.defectRate = defectRate;
    }

    /**
     * Returns the analysis timestamp.
     *
     * @return the analysis date and time
     */
    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    /**
     * Sets the analysis timestamp.
     *
     * @param analysisDate the analysis date and time
     */
    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }

    /**
     * Returns the source dataset identifier.
     *
     * @return the source dataset id
     */
    public String getSourceDatasetId() {
        return sourceDatasetId;
    }

    /**
     * Sets the source dataset identifier.
     *
     * @param sourceDatasetId the source dataset id
     */
    public void setSourceDatasetId(String sourceDatasetId) {
        this.sourceDatasetId = sourceDatasetId;
    }

    /**
     * Returns the grouped metric breakdown.
     *
     * @return the breakdown map
     */
    public Map<String, Double> getBreakdown() {
        return breakdown;
    }

    /**
     * Sets the grouped metric breakdown.
     *
     * @param breakdown the breakdown map
     */
    public void setBreakdown(Map<String, Double> breakdown) {
        this.breakdown = breakdown;
    }

    /**
     * Returns a concise string for analysis summary.
     *
     * @return summary string in id | metric = value format
     */
    @Override
    public String toString() {
        return analysisId + " | " + metricName + " = " + metricValue;
    }
}

package com.bi.analytics;

import com.bi.exceptions.AnalyticsException;
import com.bi.interfaces.IAnalyticsService;
import com.bi.repository.DataRepositoryImpl;
import com.bi.util.AnalysisResult;
import com.bi.util.Dataset;
import com.bi.util.ForecastResult;
import com.bi.util.TrendResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AnalyticsServiceImpl provides in-memory analytics operations over repository datasets.
 */
public class AnalyticsServiceImpl implements IAnalyticsService {
    private DataRepositoryImpl repository;
    private List<AnalysisResult> analysisHistory = new ArrayList<>();
    private int analysisCounter = 0;

    /**
     * Constructs an analytics service with a repository reference.
     *
     * @param repository the data repository used by analytics workflows
     */
    public AnalyticsServiceImpl(DataRepositoryImpl repository) {
        this.repository = repository;
    }

    /**
     * Computes summary metrics from the given dataset.
     *
     * @param data the dataset to analyze
     * @return a populated analysis result
     * @throws AnalyticsException if dataset is null, missing data, or unsupported format
     */
    @Override
    public AnalysisResult analyze(Dataset data) {
        if (data == null) {
            throw new AnalyticsException("Cannot analyze null dataset.");
        }
        if (data.getData() == null) {
            throw new AnalyticsException("Dataset has no data to analyze: " + data.getId());
        }

        List<Map<String, Object>> records = safeCastToRecordList(data.getData(),
                "Data format not supported for analysis.");

        analysisCounter++;

        String metricKey = determineMetricKey(records);
        String metricName = resolveMetricName(metricKey);
        double metricValue = computeMetricValue(records, metricKey);
        double productionEfficiency = computeProductionEfficiency(records);
        double defectRate = 1.0 - productionEfficiency;
        Map<String, Double> breakdown = buildBreakdown(records, metricKey, metricValue);

        AnalysisResult result = new AnalysisResult(
                "ANALYSIS-" + String.format("%03d", analysisCounter),
                metricName,
                metricValue,
                productionEfficiency,
                defectRate,
                LocalDateTime.now(),
                data.getId(),
                breakdown);

        analysisHistory.add(result);
        System.out.println("[Analytics] Analysis complete: " + result);
        return result;
    }

    /**
     * Projects future metric values based on analyzed current values.
     *
     * @param data the dataset to forecast
     * @return a populated forecast result
     * @throws AnalyticsException if input is null or analysis stage fails
     */
    @Override
    public ForecastResult forecast(Dataset data) {
        if (data == null) {
            throw new AnalyticsException("Cannot forecast from null dataset.");
        }

        AnalysisResult analysisResult;
        try {
            analysisResult = analyze(data);
        } catch (AnalyticsException e) {
            throw new AnalyticsException("Forecast failed during analysis phase: " + e.getMessage(), e);
        }

        double currentValue = analysisResult.getMetricValue();
        double growthRate;
        if (currentValue > 500000) {
            growthRate = 8.5;
        } else if (currentValue > 100000) {
            growthRate = 12.0;
        } else if (currentValue > 0) {
            growthRate = 15.5;
        } else {
            growthRate = 0.0;
        }

        double forecastedValue = currentValue * (1 + growthRate / 100);
        String confidence;
        if (growthRate >= 12.0) {
            confidence = "HIGH";
        } else if (growthRate >= 8.0) {
            confidence = "MEDIUM";
        } else {
            confidence = "LOW";
        }

        ForecastResult result = new ForecastResult(
                "FORECAST-" + String.format("%03d", analysisCounter),
                analysisResult.getMetricName(),
                currentValue,
                forecastedValue,
                growthRate,
                LocalDateTime.now(),
                30,
                confidence);

        System.out.println("[Analytics] Forecast complete: " + result);
        return result;
    }

    /**
     * Computes directional trend information from dataset values.
     *
     * @param data the dataset to evaluate for trends
     * @return a populated trend result
     * @throws AnalyticsException if input is null, unsupported, or insufficient for trending
     */
    @Override
    public TrendResult getTrends(Dataset data) {
        if (data == null) {
            throw new AnalyticsException("Cannot compute trends from null dataset.");
        }

        List<Map<String, Object>> records = safeCastToRecordList(data.getData(),
                "Data format not supported for trend analysis.");

        List<Double> dataPoints = new ArrayList<>();
        for (Map<String, Object> record : records) {
            Double value = extractFirstNumeric(record, "revenue", "amount", "salary");
            dataPoints.add(value == null ? 0.0 : value);
        }

        if (dataPoints.size() <= 1) {
            throw new AnalyticsException("Insufficient data points for trend analysis.");
        }

        double first = dataPoints.get(0);
        double last = dataPoints.get(dataPoints.size() - 1);
        double changePercent;
        if (first == 0.0) {
            changePercent = 0.0;
        } else {
            changePercent = ((last - first) / first) * 100;
        }
        double roundedChangePercent = Math.round(changePercent * 100.0) / 100.0;

        String direction;
        if (roundedChangePercent > 2.0) {
            direction = "UPWARD";
        } else if (roundedChangePercent < -2.0) {
            direction = "DOWNWARD";
        } else {
            direction = "STABLE";
        }

        TrendResult result = new TrendResult(
                "TREND-" + String.format("%03d", analysisCounter),
                "Dataset Trend - " + data.getId(),
                direction,
                roundedChangePercent,
                dataPoints,
                LocalDateTime.now());

        System.out.println("[Analytics] Trend computed: " + result);
        return result;
    }

    /**
     * Returns all analyses performed so far.
     *
     * @return the analysis history list
     */
    public List<AnalysisResult> getAnalysisHistory() {
        return analysisHistory;
    }

    /**
     * Returns the latest analysis in history.
     *
     * @return the most recent analysis result
     * @throws AnalyticsException if no analyses exist
     */
    public AnalysisResult getLatestAnalysis() {
        if (analysisHistory.isEmpty()) {
            throw new AnalyticsException("No analyses have been run yet.");
        }
        return analysisHistory.get(analysisHistory.size() - 1);
    }

    /**
     * Casts generic dataset data into record list format with controlled failure.
     *
     * @param rawData the raw dataset payload
     * @param errorMessage message used when format is unsupported
     * @return list of record maps
     * @throws AnalyticsException if casting fails
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> safeCastToRecordList(Object rawData, String errorMessage) {
        try {
            return (List<Map<String, Object>>) rawData;
        } catch (ClassCastException e) {
            throw new AnalyticsException(errorMessage, e);
        }
    }

    /**
     * Determines the first supported metric key present across records.
     *
     * @param records the dataset records
     * @return matching metric key or null for fallback count metric
     */
    private String determineMetricKey(List<Map<String, Object>> records) {
        String[] candidates = { "revenue", "amount", "salary" };
        for (String key : candidates) {
            for (Map<String, Object> record : records) {
                Object value = record.get(key);
                if (value instanceof Number) {
                    return key;
                }
                if (value != null) {
                    try {
                        Double.parseDouble(value.toString());
                        return key;
                    } catch (NumberFormatException ignored) {
                        // Continue searching supported numeric key.
                    }
                }
            }
        }
        return null;
    }

    /**
     * Maps metric key to display metric name.
     *
     * @param metricKey the selected metric key
     * @return user-facing metric name
     */
    private String resolveMetricName(String metricKey) {
        if ("revenue".equals(metricKey)) {
            return "Total Revenue";
        }
        if ("amount".equals(metricKey)) {
            return "Total Transaction Amount";
        }
        if ("salary".equals(metricKey)) {
            return "Total Salary Expenditure";
        }
        return "Record Count";
    }

    /**
     * Computes metric value based on selected key or record count fallback.
     *
     * @param records the dataset records
     * @param metricKey selected metric key
     * @return computed metric value
     */
    private double computeMetricValue(List<Map<String, Object>> records, String metricKey) {
        if (metricKey == null) {
            return records.size();
        }

        double sum = 0.0;
        for (Map<String, Object> record : records) {
            Object value = record.get(metricKey);
            Double numericValue = toDouble(value);
            if (numericValue != null) {
                sum += numericValue;
            }
        }
        return sum;
    }

    /**
     * Computes production efficiency from processed flag prevalence.
     *
     * @param records the dataset records
     * @return production efficiency ratio
     */
    private double computeProductionEfficiency(List<Map<String, Object>> records) {
        if (records.isEmpty()) {
            return 0.85;
        }

        boolean hasProcessedKey = false;
        int processedTrueCount = 0;
        for (Map<String, Object> record : records) {
            if (record.containsKey("processed")) {
                hasProcessedKey = true;
                if (Boolean.TRUE.equals(record.get("processed"))) {
                    processedTrueCount++;
                }
            }
        }

        if (!hasProcessedKey) {
            return 0.85;
        }
        return (double) processedTrueCount / records.size();
    }

    /**
     * Builds grouped metric totals by car model or department when available.
     *
     * @param records the dataset records
     * @param metricKey selected metric key
     * @param metricValue computed total metric value
     * @return breakdown map by group
     */
    private Map<String, Double> buildBreakdown(List<Map<String, Object>> records, String metricKey, double metricValue) {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        String groupingKey = determineGroupingKey(records);

        if (groupingKey == null) {
            breakdown.put("ALL", metricValue);
            return breakdown;
        }

        for (Map<String, Object> record : records) {
            String groupName = String.valueOf(record.get(groupingKey));
            double valueForRecord;
            if (metricKey == null) {
                valueForRecord = 1.0;
            } else {
                Double numericValue = toDouble(record.get(metricKey));
                valueForRecord = numericValue == null ? 0.0 : numericValue;
            }
            breakdown.put(groupName, breakdown.getOrDefault(groupName, 0.0) + valueForRecord);
        }

        return breakdown;
    }

    /**
     * Determines the grouping key preference based on record structure.
     *
     * @param records the dataset records
     * @return "carModel", "department", or null if neither exists
     */
    private String determineGroupingKey(List<Map<String, Object>> records) {
        for (Map<String, Object> record : records) {
            if (record.containsKey("carModel")) {
                return "carModel";
            }
        }
        for (Map<String, Object> record : records) {
            if (record.containsKey("department")) {
                return "department";
            }
        }
        return null;
    }

    /**
     * Extracts first numeric value from ordered keys.
     *
     * @param record the source record
     * @param keys ordered keys to inspect
     * @return first numeric value found, or null
     */
    private Double extractFirstNumeric(Map<String, Object> record, String... keys) {
        for (String key : keys) {
            Double value = toDouble(record.get(key));
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Converts value to Double where possible.
     *
     * @param value input object
     * @return parsed double or null when conversion is not possible
     */
    private Double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}

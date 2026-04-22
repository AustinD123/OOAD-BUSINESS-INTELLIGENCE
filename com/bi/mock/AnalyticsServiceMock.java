package com.bi.mock;

import com.bi.interfaces.IAnalyticsService;
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
 * Mock implementation of IAnalyticsService.
 * Returns hardcoded monthly sales trend data for the Dashboard chart.
 */
public class AnalyticsServiceMock implements IAnalyticsService {

    /** Monthly sales figures used for the Dashboard bar chart. */
    public static final String[] MONTHS =
            {"Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar", "Apr"};
    public static final double[] SALES =
            {72000, 85000, 91000, 88000, 95000, 102000, 98000, 110000, 115000, 92000};

    @Override
    public AnalysisResult analyze(Dataset data) {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        for (int i = 0; i < MONTHS.length; i++) breakdown.put(MONTHS[i], SALES[i]);
        return new AnalysisResult(
                "A001", "Monthly Sales", 920000,
                0.87, 0.014,
                LocalDateTime.now(), "SALES_DS", breakdown);
    }

    @Override
    public ForecastResult forecast(Dataset data) {
        return new ForecastResult();
    }

    @Override
    public TrendResult getTrends(Dataset data) {
        // Box double[] manually to avoid type inference issue with Arrays.asList(double[])
        List<Double> points = new ArrayList<>();
        for (double v : SALES) points.add(v);
        return new TrendResult(
                "T001", "Sales Trend", "UPWARD", 12.5,
                points, LocalDateTime.now());
    }
}

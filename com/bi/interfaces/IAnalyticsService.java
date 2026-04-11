package com.bi.interfaces;

import com.bi.util.AnalysisResult;
import com.bi.util.Dataset;
import com.bi.util.ForecastResult;
import com.bi.util.TrendResult;

public interface IAnalyticsService {
    AnalysisResult analyze(Dataset data);

    ForecastResult forecast(Dataset data);

    TrendResult getTrends(Dataset data);
}

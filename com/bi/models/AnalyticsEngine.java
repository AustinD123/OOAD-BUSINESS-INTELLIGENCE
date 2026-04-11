package com.bi.models;

import com.bi.util.AnalysisResult;
import com.bi.util.ForecastResult;
import com.bi.util.TrendResult;
import java.time.LocalDateTime;

public class AnalyticsEngine {
    private final String analysisId;
    private final String modelType;
    private final LocalDateTime trainingDate;

    public AnalyticsEngine(String analysisId, String modelType, LocalDateTime trainingDate) {
        this.analysisId = analysisId;
        this.modelType = modelType;
        this.trainingDate = trainingDate;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public String getModelType() {
        return modelType;
    }

    public LocalDateTime getTrainingDate() {
        return trainingDate;
    }

    public AnalysisResult analyze() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ForecastResult forecast() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public TrendResult getTrends() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

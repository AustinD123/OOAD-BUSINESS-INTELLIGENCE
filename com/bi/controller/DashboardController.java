package com.bi.controller;

import com.bi.mock.AnalyticsServiceMock;
import com.bi.mock.KPIServiceMock;
import com.bi.util.AnalysisResult;
import com.bi.util.TrendResult;

/**
 * Controller for the Dashboard screen.
 * Pulls summary data from KPIServiceMock and AnalyticsServiceMock.
 */
public class DashboardController {

    private final KPIServiceMock    kpiService;
    private final AnalyticsServiceMock analyticsService;

    public DashboardController(KPIServiceMock kpiService, AnalyticsServiceMock analyticsService) {
        this.kpiService       = kpiService;
        this.analyticsService = analyticsService;
    }

    // ── KPI summary cards ────────────────────────────────────────
    public int   totalKPIs()      { return kpiService.getAllKPIs().size(); }
    public long  achievedKPIs()   { return kpiService.countAchieved(); }
    public long  notAchievedKPIs(){ return totalKPIs() - achievedKPIs(); }
    public double achievementRate(){ return totalKPIs() == 0 ? 0 : (achievedKPIs() * 100.0 / totalKPIs()); }

    // ── Sales trend for bar chart ─────────────────────────────────
    public String[] getTrendMonths()  { return AnalyticsServiceMock.MONTHS; }
    public double[] getTrendSales()   { return AnalyticsServiceMock.SALES;  }

    // ── Analytics result (shown in dashboard stats row) ───────────
    public AnalysisResult getAnalysis() {
        return analyticsService.analyze(null);
    }

    public TrendResult getTrend() {
        return analyticsService.getTrends(null);
    }
}

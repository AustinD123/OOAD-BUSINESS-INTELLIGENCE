package com.bi.controller;

import com.bi.enums.KPIStatus;
import com.bi.mock.KPIServiceMock;
import com.bi.models.KPI;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the KPI Table screen.
 * Uses KPIServiceMock (which implements IKPIService) to supply data to KPIPanel.
 */
public class KPIController {

    private final KPIServiceMock kpiService;

    public KPIController(KPIServiceMock kpiService) {
        this.kpiService = kpiService;
    }

    /** Returns all KPIs unfiltered. */
    public List<KPI> getAllKPIs() {
        return kpiService.getAllKPIs();
    }

    /** Returns only KPIs matching the given status filter string ("All", "Achieved", "Not Achieved"). */
    public List<KPI> getFilteredKPIs(String filter) {
        List<KPI> all = kpiService.getAllKPIs();
        return switch (filter) {
            case "Achieved"     -> all.stream().filter(k -> k.getStatus() == KPIStatus.ACHIEVED).collect(Collectors.toList());
            case "Not Achieved" -> all.stream().filter(k -> k.getStatus() == KPIStatus.NOT_ACHIEVED).collect(Collectors.toList());
            default             -> all;
        };
    }

    /** Returns achievement % for a single KPI. */
    public double getAchievementPct(KPI kpi) {
        if (kpi.getTargetValue() == 0) return 0;
        return Math.round((kpi.getActualValue() / kpi.getTargetValue()) * 100.0 * 10) / 10.0;
    }

    /** Summary counts for the dashboard cards. */
    public int totalKPIs()       { return kpiService.getAllKPIs().size(); }
    public long achievedCount()  { return kpiService.countAchieved(); }
    public long pendingCount()   { return totalKPIs() - achievedCount(); }
}

package com.bi.mock;

import com.bi.enums.KPIStatus;
import com.bi.interfaces.IKPIService;
import com.bi.models.KPI;
import com.bi.util.TargetSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of IKPIService.
 * Provides hardcoded car-manufacturing KPI data for demo.
 * getAllKPIs() is a helper not in the interface — used by controllers.
 */
public class KPIServiceMock implements IKPIService {

    private final List<KPI> kpiList = new ArrayList<>();

    public KPIServiceMock() {
        // Pre-loaded with the same demo entries used in the KPI table demo
        kpiList.add(new KPI(1, "Monthly Cars Produced",  5000,  4780, KPIStatus.NOT_ACHIEVED));
        kpiList.add(new KPI(2, "Engine Defect Rate",     2000,  1400, KPIStatus.ACHIEVED));
        kpiList.add(new KPI(3, "Sales Revenue (Lakhs)", 850000, 920000, KPIStatus.ACHIEVED));
        kpiList.add(new KPI(4, "Inventory Turnover",    12000,  9000, KPIStatus.NOT_ACHIEVED));
        kpiList.add(new KPI(5, "On-Time Delivery (%)",   9500,  9700, KPIStatus.ACHIEVED));
        kpiList.add(new KPI(6, "Worker Productivity",    8000,  7500, KPIStatus.NOT_ACHIEVED));
        kpiList.add(new KPI(7, "Paint Quality Pass (%)", 9800,  9600, KPIStatus.NOT_ACHIEVED));
        kpiList.add(new KPI(8, "Procurement Cost",      300000, 340000, KPIStatus.NOT_ACHIEVED));
    }

    /** Returns all KPIs — used by KPIController and DashboardController. */
    public List<KPI> getAllKPIs() {
        return new ArrayList<>(kpiList);
    }

    /** Counts how many KPIs have ACHIEVED status. */
    public long countAchieved() {
        return kpiList.stream().filter(k -> k.getStatus() == KPIStatus.ACHIEVED).count();
    }

    @Override
    public void calculateKPI(List<KPI> metrics, TargetSet targets) {
        // Mock: status already set in constructor
        System.out.println("[KPIServiceMock] calculateKPI called with " + metrics.size() + " metrics.");
    }

    @Override
    public void evaluateTarget() {
        System.out.println("[KPIServiceMock] evaluateTarget called.");
    }
}

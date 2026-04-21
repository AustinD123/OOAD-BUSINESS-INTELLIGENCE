package com.bi.kpi;

import com.bi.db.ERPClient;
import com.bi.enums.KPIStatus;
import com.bi.exceptions.KPIEvaluationException;
import com.bi.interfaces.IKPIService;
import com.bi.models.KPI;
import com.bi.util.TargetSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KPIServiceImpl implements IKPIService to calculate KPI values,
 * evaluate them against targets, and persist results to the shared RDS.
 *
 * Database access is performed exclusively through the Integration team's ERP SDK
 * (via ERPClient) — no direct JDBC.
 *
 * Canonical table used: kpis
 *   Writable columns: name, description, unit, category
 *   The kpis table is owned by Data Analytics; BI has Create/Read/Update access.
 *   We use `name` and `description` to store kpi_name and status details.
 */
public class KPIServiceImpl implements IKPIService {

    private final List<KPI> evaluatedKPIs = new ArrayList<>();
    private TargetSet activeTargetSet = null;

    /**
     * Calculates and evaluates each KPI against the provided target set,
     * then persists results to the shared RDS via the ERP SDK.
     *
     * @param metrics the list of KPI objects to calculate
     * @param targets the target configuration to evaluate against
     * @throws KPIEvaluationException if metrics or targets are null/empty
     */
    @Override
    public void calculateKPI(List<KPI> metrics, TargetSet targets) {
        if (metrics == null || metrics.isEmpty()) {
            throw new KPIEvaluationException("KPI metrics list cannot be null or empty.");
        }
        if (targets == null) {
            throw new KPIEvaluationException("TargetSet cannot be null.");
        }

        this.activeTargetSet = targets;
        evaluatedKPIs.clear();

        System.out.println("[KPIService] Calculating " + metrics.size()
                + " KPIs against target set: " + targets.getDescription());

        for (KPI kpi : metrics) {
            if (kpi == null) {
                System.err.println("[KPIService] Warning: Skipping null KPI entry.");
                continue;
            }

            double achievement = computeAchievement(kpi);
            KPIStatus status   = deriveStatus(achievement);

            System.out.printf("[KPIService] KPI '%s' | Actual: %.2f | Target: %.2f"
                            + " | Achievement: %.1f%% | Status: %s%n",
                    kpi.getKpiName(), kpi.getActualValue(), kpi.getTargetValue(),
                    achievement * 100, status);

            evaluatedKPIs.add(kpi);
            persistKPI(kpi, status);
        }

        System.out.println("[KPIService] KPI calculation complete. "
                + evaluatedKPIs.size() + " KPIs processed.");
    }

    /**
     * Evaluates all previously calculated KPIs and prints a summary report.
     *
     * @throws KPIEvaluationException if no KPIs have been calculated yet
     */
    @Override
    public void evaluateTarget() {
        if (evaluatedKPIs.isEmpty()) {
            throw new KPIEvaluationException(
                    "No KPIs have been calculated. Call calculateKPI() first.");
        }

        System.out.println("[KPIService] ===== KPI Target Evaluation Summary =====");

        int achieved = 0, notAchieved = 0, pending = 0;

        for (KPI kpi : evaluatedKPIs) {
            double achievement = computeAchievement(kpi);
            KPIStatus status   = deriveStatus(achievement);

            switch (status) {
                case ACHIEVED     -> achieved++;
                case NOT_ACHIEVED -> notAchieved++;
                default           -> pending++;
            }

            System.out.printf("[KPIService]  %-30s | %s (%.1f%%)%n",
                    kpi.getKpiName(), status, achievement * 100);
        }

        System.out.printf("[KPIService] Result -> Achieved: %d | Not Achieved: %d | Pending: %d%n",
                achieved, notAchieved, pending);
        System.out.println("[KPIService] ==========================================");
    }

    /** Returns the list of KPIs evaluated in the last calculateKPI() call. */
    public List<KPI> getEvaluatedKPIs() {
        return evaluatedKPIs;
    }

    // ─── private helpers ───────────────────────────────────────────────────────

    private double computeAchievement(KPI kpi) {
        if (kpi.getTargetValue() == 0) {
            return kpi.getActualValue() > 0 ? 1.0 : 0.0;
        }
        return kpi.getActualValue() / kpi.getTargetValue();
    }

    private KPIStatus deriveStatus(double achievement) {
        if (achievement >= 1.0)  return KPIStatus.ACHIEVED;
        if (achievement >= 0.75) return KPIStatus.PENDING;
        return KPIStatus.NOT_ACHIEVED;
    }

    /**
     * Persists a KPI and its evaluated status to the shared RDS via the ERP SDK.
     *
     * Maps our KPIStatus to a readable description stored in the `description` column.
     * Uses `name` for the KPI name and `category` for the status code.
     */
    private void persistKPI(KPI kpi, KPIStatus status) {
        // Map our 3-value enum to the DB status vocabulary
        String statusCode = switch (status) {
            case ACHIEVED     -> "ABOVE_TARGET";
            case PENDING      -> "ON_TARGET";
            case NOT_ACHIEVED -> "BELOW_TARGET";
        };

        String description = String.format(
                "Target=%.2f | Actual=%.2f | Status=%s",
                kpi.getTargetValue(), kpi.getActualValue(), statusCode);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("name",        kpi.getKpiName());
            payload.put("description", description);
            payload.put("unit",        "value");
            payload.put("category",    statusCode);

            long rdsId = ERPClient.create("kpis", payload);

            System.out.println("[KPIService] KPI persisted to RDS (id=" + rdsId
                    + "): " + kpi.getKpiName());

        } catch (Exception e) {
            throw new KPIEvaluationException(
                    "Failed to persist KPI '" + kpi.getKpiName() + "' to RDS: " + e.getMessage(), e);
        }
    }
}

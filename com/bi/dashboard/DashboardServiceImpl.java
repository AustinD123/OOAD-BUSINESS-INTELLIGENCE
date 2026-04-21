package com.bi.dashboard;

import com.bi.db.ERPClient;
import com.bi.enums.ChartType;
import com.bi.exceptions.DashboardLoadException;
import com.bi.interfaces.IDashboardService;
import com.bi.models.KPI;
import com.bi.models.Visualization;
import com.bi.util.AnalysisResult;
import com.bi.util.ChartData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashboardServiceImpl implements IDashboardService to render charts and widgets
 * for the BI dashboard and persist visualizations to the shared RDS.
 *
 * Database access is performed exclusively through the Integration team's ERP SDK
 * (via ERPClient) — no direct JDBC.
 *
 * Canonical table used: visualizations
 *   Writable columns: viz_id (VARCHAR 50), chart_type_id (BIGINT), visualization_data (JSON)
 */
public class DashboardServiceImpl implements IDashboardService {

    private final List<Visualization> renderedVisualizations = new ArrayList<>();
    private final List<String>        renderedWidgets        = new ArrayList<>();
    private int vizCounter = 0;

    /**
     * Renders a chart from the given ChartData and persists it to the shared RDS.
     *
     * @param chartData the chart configuration and data
     * @throws DashboardLoadException if chartData is null or rendering fails
     */
    @Override
    public void renderChart(ChartData chartData) {
        if (chartData == null) {
            throw new DashboardLoadException("ChartData cannot be null.");
        }
        if (chartData.getDescription() == null || chartData.getDescription().isBlank()) {
            throw new DashboardLoadException("ChartData must have a non-empty description.");
        }

        vizCounter++;
        ChartType chartType = resolveChartType(chartData.getDescription());
        String vizId = "VIZ-" + String.format("%03d", vizCounter);

        Visualization viz = new Visualization(vizId, chartType, chartData.getDescription());
        renderedVisualizations.add(viz);

        System.out.println("[DashboardService] Rendered chart: " + vizId
                + " | Type: " + chartType
                + " | Data: " + chartData.getDescription());

        persistVisualization(viz);
    }

    /**
     * Renders a summary widget showing KPI cards and alerts.
     */
    @Override
    public void renderWidget() {
        System.out.println("[DashboardService] Rendering widget panel...");
        String widget = buildWidgetSummary();
        renderedWidgets.add(widget);
        System.out.println(widget);
        System.out.println("[DashboardService] Widget rendered successfully.");
    }

    /**
     * Renders KPI cards for the dashboard given a list of KPIs.
     *
     * @param kpis the list of KPIs to display as cards
     * @throws DashboardLoadException if kpis list is null
     */
    public void renderKPICards(List<KPI> kpis) {
        if (kpis == null) {
            throw new DashboardLoadException("KPI list cannot be null for dashboard rendering.");
        }

        System.out.println("[DashboardService] ===== KPI CARDS =====");
        System.out.printf("%-30s | %-15s | %-15s | %s%n",
                "KPI Name", "Target", "Actual", "Status");
        System.out.println("-".repeat(80));

        for (KPI kpi : kpis) {
            double achievement = kpi.getTargetValue() == 0 ? 1.0
                    : kpi.getActualValue() / kpi.getTargetValue();
            String status = achievement >= 1.0 ? "ACHIEVED"
                    : achievement >= 0.75 ? "PENDING" : "NOT ACHIEVED";

            System.out.printf("%-30s | %-15.2f | %-15.2f | %s%n",
                    kpi.getKpiName(), kpi.getTargetValue(), kpi.getActualValue(), status);
        }
        System.out.println("[DashboardService] ====================");
    }

    /**
     * Renders a sales trend line chart from analysis results.
     *
     * @param results list of analysis results to plot as trend
     * @throws DashboardLoadException if results is null or empty
     */
    public void renderSalesTrend(List<AnalysisResult> results) {
        if (results == null || results.isEmpty()) {
            throw new DashboardLoadException(
                    "Analysis results cannot be null or empty for trend chart.");
        }

        System.out.println("[DashboardService] ===== SALES TREND CHART (LINE) =====");
        System.out.println("  Metric: " + results.get(0).getMetricName());
        System.out.println("  Data points:");

        double max = results.stream().mapToDouble(AnalysisResult::getMetricValue).max().orElse(1.0);
        for (AnalysisResult r : results) {
            int barLen = (int) ((r.getMetricValue() / max) * 40);
            String bar = "*".repeat(Math.max(1, barLen));
            System.out.printf("  [%s] %s %.2f%n", r.getAnalysisId(), bar, r.getMetricValue());
        }
        System.out.println("[DashboardService] ===================================");

        ChartData trendChart = new ChartData("Sales Trend Line Chart");
        renderChart(trendChart);
    }

    /**
     * Refreshes the dashboard by re-rendering all widgets.
     */
    public void refreshDashboard() {
        System.out.println("[DashboardService] Refreshing dashboard...");
        renderedWidgets.clear();
        renderWidget();
        System.out.println("[DashboardService] Dashboard refreshed.");
    }

    /** Returns all rendered visualizations. */
    public List<Visualization> getRenderedVisualizations() {
        return renderedVisualizations;
    }

    // ─── private helpers ───────────────────────────────────────────────────────

    private ChartType resolveChartType(String description) {
        String lower = description.toLowerCase();
        if (lower.contains("line") || lower.contains("trend")) return ChartType.LINE;
        if (lower.contains("pie")  || lower.contains("proportion")) return ChartType.PIE;
        if (lower.contains("scatter"))                               return ChartType.SCATTER;
        return ChartType.BAR;
    }

    private String buildWidgetSummary() {
        return """
                [DashboardService] ===== WIDGET PANEL =====
                  [ KPI Cards    ] Total Sales | Profit | Growth %
                  [ Sales Trend  ] Line chart across Q1 2026
                  [ Alerts       ] - Target not met for Monthly Sales
                                   - Inventory Low alert active
                [DashboardService] ==========================
                """;
    }

    /**
     * Persists a visualization to the shared RDS via the ERP SDK.
     *
     * The canonical `visualizations` table has: viz_id (VARCHAR 50), chart_type_id (BIGINT),
     * visualization_data (JSON). We store the chart type name and description as JSON.
     */
    private void persistVisualization(Visualization viz) {
        try {
            // Build a simple JSON string for visualization_data
            String jsonData = String.format(
                    "{\"chart_type\":\"%s\",\"description\":\"%s\"}",
                    viz.getChartType().name(),
                    viz.getData().toString().replace("\"", "\\\""));

            Map<String, Object> payload = new HashMap<>();
            payload.put("viz_id",             viz.getVizId());
            payload.put("visualization_data", jsonData);
            // chart_type_id: we pass the ordinal+1 as a stable numeric ID
            payload.put("chart_type_id", (long) (viz.getChartType().ordinal() + 1));

            ERPClient.create("visualizations", payload);

            System.out.println("[DashboardService] Visualization persisted to RDS: "
                    + viz.getVizId());

        } catch (Exception e) {
            // Non-fatal — the chart is still rendered to the console.
            System.err.println("[DashboardService] Warning: could not persist visualization to RDS: "
                    + e.getMessage());
        }
    }
}

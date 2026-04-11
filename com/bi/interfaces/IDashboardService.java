package com.bi.interfaces;

import com.bi.util.ChartData;

public interface IDashboardService {
    void renderChart(ChartData chartData);

    void renderWidget();
}

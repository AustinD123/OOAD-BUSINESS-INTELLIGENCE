package com.bi.models;

import java.time.LocalDateTime;
import java.util.List;

public class Dashboard {
    private final int dashboardId;
    private final String userName;
    private final List<Object> widgets;
    private final LocalDateTime lastUpdated;

    public Dashboard(int dashboardId, String userName, List<Object> widgets, LocalDateTime lastUpdated) {
        this.dashboardId = dashboardId;
        this.userName = userName;
        this.widgets = widgets;
        this.lastUpdated = lastUpdated;
    }

    public int getDashboardId() {
        return dashboardId;
    }

    public String getUserName() {
        return userName;
    }

    public List<Object> getWidgets() {
        return widgets;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void display() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void refresh() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

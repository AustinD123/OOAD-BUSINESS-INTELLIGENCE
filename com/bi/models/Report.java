package com.bi.models;

import com.bi.enums.ReportFormat;
import java.io.File;
import java.time.LocalDateTime;

public class Report {
    private final int reportId;
    private String reportName;
    private final String reportType;
    private final LocalDateTime generatedDate;
    private final ReportFormat format;
    private final Object reportData;

    public Report(int reportId, String reportName, String reportType, LocalDateTime generatedDate,
                  ReportFormat format, Object reportData) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.reportType = reportType;
        this.generatedDate = generatedDate;
        this.format = format;
        this.reportData = reportData;
    }

    public int getReportId() {
        return reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportType() {
        return reportType;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public Object getReportData() {
        return reportData;
    }

    public File generateReport(ReportFormat fmt) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void exportReport(ReportFormat fmt) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

package com.bi.interfaces;

import com.bi.util.ReportConfig;

public interface IReportService {
    void generateReport(ReportConfig config);

    void exportPDF();

    void exportExcel();
}

-- BI Sub-system (Phase 3) Database Schema
-- Project: OOAD Mini Project, Sub-system #17
-- Scope: Sales, HR, Finance source data plus BI repository, analytics, and query outputs.
-- Database target: MySQL 8+.

CREATE DATABASE IF NOT EXISTS bi
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bi;

START TRANSACTION;

CREATE TABLE IF NOT EXISTS source_types (
    source_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type_code VARCHAR(50) NOT NULL UNIQUE,
    type_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS report_formats (
    report_format_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    format_code VARCHAR(50) NOT NULL UNIQUE,
    format_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chart_types (
    chart_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chart_code VARCHAR(50) NOT NULL UNIQUE,
    chart_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS kpi_statuses (
    kpi_status_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    status_code VARCHAR(50) NOT NULL UNIQUE,
    status_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS data_sources (
    source_id INTEGER NOT NULL PRIMARY KEY,
    source_name VARCHAR(150) NOT NULL,
    source_type_id BIGINT NOT NULL,
    car_model VARCHAR(150),
    data_timestamp TIMESTAMP NOT NULL,
    raw_data JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_data_sources_source_type
        FOREIGN KEY (source_type_id) REFERENCES source_types(source_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sales_records (
    sales_record_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    source_id INTEGER,
    car_model VARCHAR(150) NOT NULL,
    units_sold INTEGER NOT NULL,
    revenue DECIMAL(18,2) NOT NULL,
    sales_date TIMESTAMP NOT NULL,
    dealer_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sales_records_source
        FOREIGN KEY (source_id) REFERENCES data_sources(source_id) ON DELETE SET NULL,
    CONSTRAINT chk_sales_units_sold CHECK (units_sold >= 0),
    CONSTRAINT chk_sales_revenue CHECK (revenue >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hr_records (
    hr_record_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    source_id INTEGER,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    employee_name VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role VARCHAR(150) NOT NULL,
    joining_date TIMESTAMP NOT NULL,
    salary DECIMAL(18,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hr_records_source
        FOREIGN KEY (source_id) REFERENCES data_sources(source_id) ON DELETE SET NULL,
    CONSTRAINT chk_hr_salary CHECK (salary >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS finance_transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    source_id INTEGER,
    car_model VARCHAR(150) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    department VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_finance_transactions_source
        FOREIGN KEY (source_id) REFERENCES data_sources(source_id) ON DELETE SET NULL,
    CONSTRAINT chk_finance_amount CHECK (amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS datasets (
    dataset_id VARCHAR(200) PRIMARY KEY,
    dataset_name VARCHAR(200),
    description VARCHAR(255),
    source_type_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payload JSON,
    CONSTRAINT fk_datasets_source_type
        FOREIGN KEY (source_type_id) REFERENCES source_types(source_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS data_warehouse (
    record_id INTEGER PRIMARY KEY,
    car_model VARCHAR(150),
    data_category VARCHAR(100) NOT NULL,
    stored_data JSON,
    created_at TIMESTAMP NOT NULL,
    dataset_id VARCHAR(200),
    CONSTRAINT fk_data_warehouse_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets(dataset_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS etl_jobs (
    processed_data_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    dataset_id VARCHAR(200),
    cleaned_data JSON,
    transformed_data JSON,
    production_count INTEGER NOT NULL DEFAULT 0,
    sales_count INTEGER NOT NULL DEFAULT 0,
    processing_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_etl_jobs_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets(dataset_id) ON DELETE SET NULL,
    CONSTRAINT chk_etl_production_count CHECK (production_count >= 0),
    CONSTRAINT chk_etl_sales_count CHECK (sales_count >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS analysis_results (
    analysis_id VARCHAR(20) PRIMARY KEY,
    metric_name VARCHAR(150) NOT NULL,
    metric_value DECIMAL(18,2) NOT NULL,
    production_efficiency DECIMAL(5,4) NOT NULL,
    defect_rate DECIMAL(5,4) NOT NULL,
    analysis_date TIMESTAMP NOT NULL,
    source_dataset_id VARCHAR(200) NOT NULL,
    breakdown JSON,
    CONSTRAINT fk_analysis_results_dataset
        FOREIGN KEY (source_dataset_id) REFERENCES datasets(dataset_id) ON DELETE CASCADE,
    CONSTRAINT chk_analysis_efficiency CHECK (production_efficiency >= 0 AND production_efficiency <= 1),
    CONSTRAINT chk_analysis_defect_rate CHECK (defect_rate >= 0 AND defect_rate <= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS forecast_results (
    forecast_id VARCHAR(20) PRIMARY KEY,
    metric_name VARCHAR(150) NOT NULL,
    current_value DECIMAL(18,2) NOT NULL,
    forecasted_value DECIMAL(18,2) NOT NULL,
    growth_rate DECIMAL(8,2) NOT NULL,
    forecast_date TIMESTAMP NOT NULL,
    forecast_period_days INTEGER NOT NULL,
    confidence VARCHAR(20) NOT NULL,
    source_analysis_id VARCHAR(20),
    CONSTRAINT fk_forecast_results_analysis
        FOREIGN KEY (source_analysis_id) REFERENCES analysis_results(analysis_id) ON DELETE SET NULL,
    CONSTRAINT chk_forecast_period CHECK (forecast_period_days > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS trend_results (
    trend_id VARCHAR(20) PRIMARY KEY,
    trend_name VARCHAR(200) NOT NULL,
    direction VARCHAR(20) NOT NULL,
    change_percent DECIMAL(8,2) NOT NULL,
    calculated_at TIMESTAMP NOT NULL,
    data_points JSON,
    source_dataset_id VARCHAR(200),
    CONSTRAINT fk_trend_results_dataset
        FOREIGN KEY (source_dataset_id) REFERENCES datasets(dataset_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS query_logs (
    query_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    query_parameters VARCHAR(500) NOT NULL,
    filter_type VARCHAR(100),
    execution_date TIMESTAMP NOT NULL,
    result_data JSON,
    dataset_id VARCHAR(200),
    CONSTRAINT fk_query_logs_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets(dataset_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS filter_sets (
    filter_set_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    filter_key VARCHAR(150) NOT NULL,
    filter_value VARCHAR(500) NOT NULL,
    filter_operator VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS dashboards (
    dashboard_id INTEGER PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    dashboard_payload JSON
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS visualizations (
    viz_id VARCHAR(50) PRIMARY KEY,
    chart_type_id BIGINT,
    visualization_data JSON,
    CONSTRAINT fk_visualizations_chart_type
        FOREIGN KEY (chart_type_id) REFERENCES chart_types(chart_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reports (
    report_id INTEGER PRIMARY KEY,
    report_name VARCHAR(200) NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    generated_date TIMESTAMP NOT NULL,
    format_id BIGINT,
    report_data JSON,
    CONSTRAINT fk_reports_format
        FOREIGN KEY (format_id) REFERENCES report_formats(report_format_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS kpis (
    kpi_id INTEGER PRIMARY KEY,
    kpi_name VARCHAR(200) NOT NULL,
    target_value DECIMAL(18,2) NOT NULL,
    actual_value DECIMAL(18,2) NOT NULL,
    status_id BIGINT,
    CONSTRAINT fk_kpis_status
        FOREIGN KEY (status_id) REFERENCES kpi_statuses(kpi_status_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS analytics_engines (
    analysis_id VARCHAR(50) PRIMARY KEY,
    model_type VARCHAR(150) NOT NULL,
    training_date TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_sales_records_car_model ON sales_records(car_model);
CREATE INDEX idx_sales_records_sales_date ON sales_records(sales_date);
CREATE INDEX idx_hr_records_department ON hr_records(department);
CREATE INDEX idx_hr_records_salary ON hr_records(salary);
CREATE INDEX idx_finance_transactions_car_model ON finance_transactions(car_model);
CREATE INDEX idx_finance_transactions_department ON finance_transactions(department);
CREATE INDEX idx_analysis_results_dataset ON analysis_results(source_dataset_id);
CREATE INDEX idx_trend_results_dataset ON trend_results(source_dataset_id);
CREATE INDEX idx_query_logs_execution_date ON query_logs(execution_date);

INSERT INTO source_types (type_code, type_name, description)
VALUES
    ('ERP_MODULE', 'ERP Module', 'Operational ERP source system'),
    ('CSV', 'CSV File', 'Comma-separated values source'),
    ('API', 'API', 'Remote API source')
ON DUPLICATE KEY UPDATE type_name = VALUES(type_name), description = VALUES(description);

INSERT INTO report_formats (format_code, format_name, description)
VALUES
    ('PDF', 'PDF', 'Portable Document Format'),
    ('EXCEL', 'Excel', 'Spreadsheet format'),
    ('CSV', 'CSV', 'Comma-separated values')
ON DUPLICATE KEY UPDATE format_name = VALUES(format_name), description = VALUES(description);

INSERT INTO chart_types (chart_code, chart_name, description)
VALUES
    ('BAR', 'Bar Chart', 'Bar-based visualization'),
    ('LINE', 'Line Chart', 'Trend visualization'),
    ('PIE', 'Pie Chart', 'Proportional visualization'),
    ('TABLE', 'Table', 'Tabular visualization')
ON DUPLICATE KEY UPDATE chart_name = VALUES(chart_name), description = VALUES(description);

INSERT INTO kpi_statuses (status_code, status_name, description)
VALUES
    ('ABOVE_TARGET', 'Above Target', 'Actual value exceeds target'),
    ('ON_TARGET', 'On Target', 'Actual value meets target'),
    ('BELOW_TARGET', 'Below Target', 'Actual value is below target')
ON DUPLICATE KEY UPDATE status_name = VALUES(status_name), description = VALUES(description);

COMMIT;

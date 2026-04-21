# Business Intelligence Sub-system — Team OREO (#17)

## Team and Course
- Team: OREO (Sub-system #17)
- Members: Evan Luke D'Souza · Archit Rishabh · Austin D'Costa
- Course: Object-Oriented Analysis & Design — AIML B

---

## Architecture Overview
BI sub-system of a larger ERP academic project. Ingests Sales, HR, Finance data via an ETL
pipeline; supports analytics, KPI tracking, report generation, dashboard rendering, and query
processing. All shared database operations go through the Integration team's ERP SDK (RDS).

---

## Build & Run Instructions

> **Prerequisites**
> - Java 17+
> - The two JARs must be on the classpath:
>   - `lib/erp-subsystem-sdk-1.0.0.jar` (Integration team SDK — already in `/lib`)
>   - `../mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar` *(only needed if running Phase 3 local DB demos; SDK does not require it)*
> - Properties file at `src/main/resources/application-rds.properties` (already present — do **not** edit)

### macOS / Linux — all commands run from inside `OOAD-BUSINESS-INTELLIGENCE/`

#### Compile everything
```bash
find com -name "*.java" | xargs javac -source 17 -target 17 \
  -cp ".:lib/erp-subsystem-sdk-1.0.0.jar"
```

#### Run Phase 4 demo (ReportService + KPIService + SecurityService)
```bash
java -cp ".:lib/erp-subsystem-sdk-1.0.0.jar" com.bi.Phase4Runner
```

#### Run Phase 5 console UI
```bash
java -cp ".:lib/erp-subsystem-sdk-1.0.0.jar" com.bi.ui.BIConsoleApp
```
Default login: `admin` / `admin123`

#### Run Phase 6 unit tests
```bash
java -cp ".:lib/erp-subsystem-sdk-1.0.0.jar" com.bi.test.BISystemTest
```

### Windows (PowerShell — semicolons for classpath separator)
```powershell
# Compile
Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } | `
  ForEach-Object { javac -source 17 -target 17 -cp ".;lib\erp-subsystem-sdk-1.0.0.jar" $_ }

# Run UI
java -cp ".;lib\erp-subsystem-sdk-1.0.0.jar" com.bi.ui.BIConsoleApp
```

---

## Database / SDK Notes (Phase SDK-Migration)

All Phase 4+ DB operations go through the **Integration team's ERP SDK**:

| File | Role |
|---|---|
| `lib/erp-subsystem-sdk-1.0.0.jar` | Integration team SDK JAR |
| `src/main/resources/application-rds.properties` | RDS connection config (do not edit) |
| `com/bi/db/ERPClient.java` | Our singleton wrapper around `BusinessIntelligence` facade |
| `com/bi/db/DBConnection.java` | **Deprecated stub** — throws immediately; kept only so old class references compile |

The `ERPClient` uses:
```java
SubsystemFactory.create(SubsystemName.BUSINESS_INTELLIGENCE, config)
```
and delegates all CRUD to the facade's `create / readAll / readById / update` methods
with `username = "integration_lead"`.

**Direct JDBC / `DriverManager.getConnection()` is disabled.**

---

## Canonical RDS Tables Used

| Service | Table | Operations |
|---|---|---|
| ReportServiceImpl | `reports` | create, update |
| KPIServiceImpl | `kpis` | create |
| SecurityServiceImpl | `users` | readAll (auth + permissions) |
| DashboardServiceImpl | `visualizations` | create |

---

## Package Structure
```
com.bi
├── BIDataPipeline          — wires ETL + repository + analytics
├── Phase4Runner            — Phase 4 demo main
├── analytics/              — AnalyticsServiceImpl
├── dashboard/              — DashboardServiceImpl
├── datasources/            — SalesModule, HRModule, FinanceModule
├── db/                     — ERPClient (SDK wrapper), DBConnection (deprecated stub)
├── enums/                  — ChartType, KPIStatus, ReportFormat, SourceType
├── etl/                    — ETLServiceImpl
├── exceptions/             — 10 custom runtime exceptions
├── interfaces/             — 9 service contracts
├── kpi/                    — KPIServiceImpl
├── models/                 — domain objects (KPI, Report, User, Visualization, …)
├── query/                  — QueryServiceImpl
├── report/                 — ReportServiceImpl
├── repository/             — DataRepositoryImpl
├── security/               — SecurityServiceImpl
├── test/                   — BISystemTest (58 unit tests, no framework)
├── ui/                     — BIConsoleApp (Phase 5 console UI)
└── util/                   — AnalysisResult, ChartData, Dataset, FilterSet, …
```

# Business Intelligence Sub-system (Phase 1)

## Sub-system Information
- Sub-system Number: 17
- Name: Business Intelligence (BI)
- Phase: 1 (Project Skeleton / Scaffolding)

## Team and Course
- Team: OREO (Team #17)
- Members:
  - Evan Luke D'Souza
  - Archit Rishabh
  - Austin D'Costa
- Course: Object-Oriented Analysis & Design
- Branch: AIML B

## Architecture Overview
This module is the BI sub-system of a larger distributed academic mini-project. It receives data from Sales, HR, and Finance systems, then supports ETL, repository storage, analytics, report generation, dashboard rendering, KPI tracking, and query processing.

Phase 1 contains only scaffolding:
- Package structure
- Interfaces
- Models and placeholders
- Enumerations
- Custom exceptions
- Data source module stubs

No business logic is implemented yet.

## Package Structure
- com.bi.enums: Enumerations used across the sub-system.
- com.bi.util: Shared placeholder/data-transfer classes.
- com.bi.exceptions: Custom runtime exceptions for BI workflows.
- com.bi.interfaces: Contracts for the 9 architecture components.
- com.bi.models: Domain model classes and method stubs.
- com.bi.datasources: Sales/HR/Finance module stubs implementing IDataSource.

## Interface Contracts
1. IDataSource
   - Fetches data, validates source quality, and handles source connection lifecycle.
2. IETLService
   - Defines ETL flow: extract, transform, and load.
3. IDataRepository
   - Stores, retrieves, and indexes datasets.
4. IAnalyticsService
   - Performs analysis, forecasting, and trend generation over datasets.
5. IReportService
   - Generates reports and exports to PDF/Excel formats.
6. IDashboardService
   - Renders charts/widgets for dashboard visualization.
7. IKPIService
   - Calculates KPI values and evaluates targets.
8. ISecurityService
   - Handles authentication, authorization, and permission retrieval.
9. IQueryService
   - Executes, filters, and parses BI queries.

## Custom Exceptions and Usage Intent
Core:
- DataSourceException: Raised for data source access/validation/connection errors.
- ETLProcessException: Raised during extraction/transformation/loading failures.
- DataNotFoundException: Raised when required dataset/records cannot be located.

Analytics and Processing:
- AnalyticsException: Raised for analysis or forecasting failures.
- InvalidQueryException: Raised when query parameters or filters are invalid.

Report and Dashboard:
- ReportGenerationException: Raised when report generation/export fails.
- DashboardLoadException: Raised when dashboard or widgets cannot be loaded.

KPI:
- KPIEvaluationException: Raised for KPI computation/evaluation errors.

Security:
- UnauthorizedAccessException: Raised for forbidden resource access attempts.
- AuthenticationFailedException: Raised for invalid user credential checks.

## Build and Run Instructions (Java 17)
### 1. Compile all source files
Run this command from the project root:

```bash
javac com/bi/enums/*.java com/bi/util/*.java com/bi/exceptions/*.java com/bi/models/*.java com/bi/interfaces/*.java com/bi/datasources/*.java
```

### 2. Run
Phase 1 has no executable main class by design (scaffolding only). Once a main class is added in a later phase, run it as:

```bash
java fully.qualified.MainClassName
```

Example pattern:

```bash
java com.bi.app.Main
```

## Current Status
- Phase 1 scaffolding completed.
- All files compile successfully with Java 17.
- Ready for Phase 2 implementation logic.

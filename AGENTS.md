# OOAD Business Intelligence Agent Guide

This repository is the BI subsystem (#17) for the OOAD mini-project. Keep changes small, phase-scoped, and consistent with the existing package structure.

## Authoritative References

- [README.md](README.md) for build/run commands, runtime entry points, and project overview.
- [OOAD-DATABASE-INTEGRATION/README_FOR_OTHER_TEAMS_RDS.md](OOAD-DATABASE-INTEGRATION/README_FOR_OTHER_TEAMS_RDS.md) for the shared RDS integration contract.
- [.github/prompts/bi-collab-guardrails.prompt.md](.github/prompts/bi-collab-guardrails.prompt.md) for collaboration guardrails and response expectations.

## Project Rules

- Use Java 17.
- Do not rename packages, classes, interfaces, enums, or exceptions unless explicitly requested.
- Do not change public method signatures unless explicitly requested.
- Avoid unrelated edits; keep changes reviewable and minimal.
- Treat interfaces as contracts only.
- Keep exception types as runtime exceptions with the existing constructor patterns.

## Database and SDK Rules

- All shared database access goes through [com/bi/db/ERPClient.java](com/bi/db/ERPClient.java).
- Do not add direct JDBC or `DriverManager` usage.
- Do not edit [src/main/resources/application-rds.properties](src/main/resources/application-rds.properties).
- Preserve the `integration_lead` username used by ERPClient unless the task explicitly says otherwise.
- [com/bi/db/DBConnection.java](com/bi/db/DBConnection.java) is a deprecated stub and should not regain JDBC behavior.

## Main Entry Points

- [com/bi/Phase4Runner.java](com/bi/Phase4Runner.java) for the Phase 4 service demo.
- [com/bi/ui/BIConsoleApp.java](com/bi/ui/BIConsoleApp.java) for the Phase 5 console UI.
- [com/bi/test/BISystemTest.java](com/bi/test/BISystemTest.java) for the standalone Phase 6 test suite.

## Verification Defaults

- Compile from the repository root with the SDK JAR on the classpath.
- Run the smallest relevant entry point for the change.
- Prefer the standalone test runner when validating behavior.

## Suggested Commands

```powershell
javac -source 17 -target 17 -cp ".;lib\erp-subsystem-sdk-1.0.0.jar" <java files>
java -cp ".;lib\erp-subsystem-sdk-1.0.0.jar" com.bi.ui.BIConsoleApp
java -cp ".;lib\erp-subsystem-sdk-1.0.0.jar" com.bi.test.BISystemTest
```

## Agent Behavior

- Read the README before making assumptions about build or runtime behavior.
- Prefer the existing prompt and docs over duplicating project background in code changes.
- If a task touches only one subsystem, do not broaden scope to unrelated modules.
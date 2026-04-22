# OREO BI - Compile & Run Script
# Run this from inside the OOAD-BUSINESS-INTELLIGENCE folder

# Step 1: Collect all .java files, excluding the ones that need the missing ERP SDK JAR
$sources = Get-ChildItem -Recurse -Filter "*.java" | Where-Object {
    $_.FullName -notlike "*\db\ERPClient.java" -and
    $_.FullName -notlike "*\dashboard\DashboardServiceImpl.java" -and
    $_.FullName -notlike "*\report\ReportServiceImpl.java" -and
    $_.FullName -notlike "*\kpi\KPIServiceImpl.java" -and
    $_.FullName -notlike "*\security\SecurityServiceImpl.java" -and
    $_.FullName -notlike "*\test\BISystemTest.java" -and
    $_.FullName -notlike "*\ui\BIConsoleApp.java" -and
    $_.FullName -notlike "*\Phase4Runner.java"
} | ForEach-Object { $_.FullName }

$sources | Out-File -FilePath sources.txt -Encoding ascii

# Step 2: Compile
& cmd /c 'javac -cp . @sources.txt'

# Step 3: Run
& cmd /c 'java -cp . com.bi.ui.MainFrame'

# bcjis-webapp WAR 빌드 후 Tomcat webapps 에 배포
# 사용 예:
#   .\deploy\deploy-app.ps1 -TomcatHome "D:\was\apache-tomcat-9.0.89"
param(
    [Parameter(Mandatory = $true)]
    [string]$TomcatHome,

    [string]$ContextName = "bcjis-webapp",

    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$WarName = "$ContextName.war"
$TargetWar = Join-Path $ProjectRoot "target\$WarName"
$DestWar = Join-Path $TomcatHome "webapps\$WarName"

if (-not $SkipBuild) {
    Write-Host "=== Maven 빌드 ==="
    & (Join-Path $ProjectRoot "scripts\build.ps1")
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

if (-not (Test-Path $TargetWar)) {
    Write-Error "WAR 없음: $TargetWar (먼저 build.ps1 실행)"
}

if (Test-Path $DestWar) {
    $bak = "$DestWar.bak.$(Get-Date -Format 'yyyyMMdd-HHmmss')"
    Copy-Item $DestWar $bak -Force
    Write-Host "기존 WAR 백업: $bak"
}

Write-Host "=== WAR 배포 ==="
Copy-Item $TargetWar $DestWar -Force
Write-Host "복사 완료: $DestWar"
Write-Host ""
Write-Host "Tomcat 을 재기동하면 애플리케이션이 반영됩니다."
Write-Host "DB 인덱스·메뉴 패치는 deploy-db.ps1 을 별도 실행하세요."

# 앱(WAR) + DB(인덱스·메뉴) 일괄 배포
param(
    [Parameter(Mandatory = $true)]
    [string]$TomcatHome,

    [Parameter(Mandatory = $true)]
    [string]$DbPassword,

    [string]$DbName = "bcjis",
    [string]$DbUser = "bcjisapp",
    [switch]$RunSeed
)

$ErrorActionPreference = "Stop"
$Here = $PSScriptRoot

$dbArgs = @{
    DbName = $DbName
    DbUser = $DbUser
    DbPassword = $DbPassword
    RunMenuPatch = $true
}
if ($RunSeed) { $dbArgs.RunSeed = $true }

& (Join-Path $Here "deploy-db.ps1") @dbArgs
& (Join-Path $Here "deploy-app.ps1") -TomcatHome $TomcatHome

Write-Host ""
Write-Host "=== deploy-all 완료 ==="
Write-Host "Tomcat 재기동 후 로그인·전년도예산/조서 적용[신규] 메뉴를 확인하세요."

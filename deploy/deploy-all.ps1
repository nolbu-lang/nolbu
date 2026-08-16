# 앱(WAR) + DB + globals 일괄 — run-ops-deploy 권장
# 하위 호환용. 운영은 deploy\run-ops-deploy.ps1 사용.
param(
    [Parameter(Mandatory = $true)]
    [string]$TomcatHome,

    [Parameter(Mandatory = $true)]
    [string]$DbPassword,

    [string]$DbName = "bcjis",
    [string]$DbUser = "bcjisapp",
    [string]$ContextName = "ROOT",
    [switch]$RunSeed
)

$ErrorActionPreference = "Stop"
$Here = $PSScriptRoot

Write-Host "run-ops-deploy.ps1 으로 위임합니다 (메뉴 PC동기화 + WAR + globals AI 병합)."
$argsHash = @{
    TomcatHome = $TomcatHome
    DbPassword = $DbPassword
    DbName = $DbName
    DbUser = $DbUser
    ContextName = $ContextName
}
& (Join-Path $Here "run-ops-deploy.ps1") @argsHash

# 예산안관리 메뉴(조서·집계 / 심사조서 보고항목선택) 일괄 적용
# 사용: .\scripts\apply-menu-budget-select.ps1 -DbPassword "비밀번호"
param(
    [string]$DbName = "bcjis",
    [string]$DbUser = "bcjisapp",
    [Parameter(Mandatory = $true)]
    [string]$DbPassword
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$Scripts = Join-Path $ProjectRoot "scripts"

if ($env:CUBRID) { $CubridBin = Join-Path $env:CUBRID "bin" } else { $CubridBin = "C:\CUBRID\bin" }
$csql = Join-Path $CubridBin "csql.exe"
if (-not (Test-Path $csql)) { Write-Error "csql.exe 없음: $csql" }

$env:CUBRID = Split-Path -Parent $CubridBin
$env:PATH = "$CubridBin;$env:PATH"

$sql = Join-Path $Scripts "patch-menu-budget-select-all.sql"
if (-not (Test-Path $sql)) { Write-Error "SQL 없음: $sql" }

Write-Host "=== 메뉴 패치 적용: patch-menu-budget-select-all.sql ==="
& $csql -u $DbUser -p $DbPassword $DbName -i $sql
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
& (Join-Path $Scripts "check-menu-budget-select.ps1") -DbName $DbName -DbUser $DbUser -DbPassword $DbPassword

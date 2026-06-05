# DB 후처리: 인덱스 + (선택) 채번 시드 + 메뉴 패치
# 사용 예:
#   .\deploy\deploy-db.ps1 -DbPassword "비밀번호"
#   .\deploy\deploy-db.ps1 -DbPassword "비밀번호" -RunSeed -RunMenuPatch
param(
    [string]$DbName = "bcjis",
    [string]$DbUser = "bcjisapp",
    [Parameter(Mandatory = $true)]
    [string]$DbPassword,

    [switch]$RunSeed,
    [switch]$RunMenuPatch,
    [switch]$SkipIndexes
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$Scripts = Join-Path $ProjectRoot "scripts"

if ($env:CUBRID) { $CubridBin = Join-Path $env:CUBRID "bin" } else { $CubridBin = "C:\CUBRID\bin" }
$csql = Join-Path $CubridBin "csql.exe"
if (-not (Test-Path $csql)) { Write-Error "csql.exe 없음: $csql" }

$env:CUBRID = Split-Path -Parent $CubridBin
$env:PATH = "$CubridBin;$env:PATH"

if (-not $SkipIndexes) {
    Write-Host "=== 인덱스 적용 (apply-indexes.ps1) ==="
    & (Join-Path $Scripts "apply-indexes.ps1")
}

if ($RunSeed) {
    Write-Host "=== TB_COMM_SEQ 시드 (seed-comm-seq.sql) ==="
    Write-Warning "운영 DB에서 이미 채번 중이면 DBA와 협의 후 실행하세요."
    & $csql -u $DbUser -p $DbPassword $DbName -i (Join-Path $Scripts "seed-comm-seq.sql")
}

if ($RunMenuPatch) {
    Write-Host "=== 메뉴 통합 (patch-menu-budget-copy.sql) ==="
    & $csql -u $DbUser -p $DbPassword $DbName -i (Join-Path $Scripts "patch-menu-budget-copy.sql")
}

Write-Host ""
Write-Host "DB 배포 스크립트 완료."

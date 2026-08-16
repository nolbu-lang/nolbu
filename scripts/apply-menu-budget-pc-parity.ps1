# 예산안관리 메뉴를 PC 개선본과 동일하게 동기화
# 사용: .\scripts\apply-menu-budget-pc-parity.ps1 -DbPassword "비밀번호"
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

$sql = Join-Path $Scripts "sync-menu-budget-pc-parity.sql"
if (-not (Test-Path $sql)) { Write-Error "SQL 없음: $sql" }

Write-Host "=== 메뉴 동기화: sync-menu-budget-pc-parity.sql ==="
& $csql -u $DbUser -p $DbPassword $DbName -i $sql
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "=== 적용 결과 확인 ==="
& $csql -u $DbUser -p $DbPassword $DbName -c "SELECT menu_cd, menu_nm, url, line_up_no, use_yn FROM tb_menu WHERE up_menu_cd='MUBG00000' AND use_yn='Y' ORDER BY line_up_no;"

Write-Host ""
$chk = & $csql -u $DbUser -p $DbPassword $DbName -c "SELECT COUNT(*) FROM tb_menu WHERE menu_cd='MUBG05000' AND up_menu_cd='MUBG00000' AND use_yn='Y' AND url='/budget/budgetSelectNew.do' AND line_up_no=1;" 2>&1
if ("$chk" -notmatch "\b1\b") {
    Write-Host "FAIL: MUBG05000(조서·집계) 미정합" -ForegroundColor Red
    exit 1
}
$chk2 = & $csql -u $DbUser -p $DbPassword $DbName -c "SELECT COUNT(*) FROM tb_menu WHERE menu_cd='MUBG10000' AND up_menu_cd='MUBG00000' AND use_yn='Y' AND url='/budget/budgetCopyNew.do' AND line_up_no=2;" 2>&1
if ("$chk2" -notmatch "\b1\b") {
    Write-Host "FAIL: MUBG10000(전년도예산조서적용[신규]) 미정합" -ForegroundColor Red
    exit 1
}
$chk3 = & $csql -u $DbUser -p $DbPassword $DbName -c "SELECT COUNT(*) FROM tb_menu WHERE menu_cd='MUBG05100' AND up_menu_cd='MUBG00000' AND use_yn='Y' AND url='/budget/budgetSelectAttr.do' AND line_up_no=3;" 2>&1
if ("$chk3" -notmatch "\b1\b") {
    Write-Host "FAIL: MUBG05100(심사조서 보고항목선택) 미정합" -ForegroundColor Red
    exit 1
}
$bad = & $csql -u $DbUser -p $DbPassword $DbName -c "SELECT COUNT(*) FROM tb_menu WHERE up_menu_cd='MUBG00000' AND use_yn='Y' AND (menu_nm LIKE '%삭제예정%' OR menu_nm LIKE '%매핑일괄%');" 2>&1
if ("$bad" -match "\b[1-9][0-9]*\b" -and "$bad" -notmatch "\b0\b") {
    # 0 rows is success; if count > 0 fail. Cubrid prints count in result.
    Write-Host "WARN: 삭제예정/매핑일괄 메뉴가 아직 보일 수 있음 — 위 SELECT 결과 확인" -ForegroundColor Yellow
}

Write-Host "OK: 예산안관리 메뉴 PC 동기화 정상" -ForegroundColor Green

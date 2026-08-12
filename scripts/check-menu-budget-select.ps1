# 예산안관리 메뉴(조서·집계 / 심사조서 보고항목선택) 배포 상태 점검
# 사용: .\scripts\check-menu-budget-select.ps1 -DbPassword "비밀번호"
param(
    [string]$DbName = "bcjis",
    [string]$DbUser = "bcjisapp",
    [Parameter(Mandatory = $true)]
    [string]$DbPassword
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot

if ($env:CUBRID) { $CubridBin = Join-Path $env:CUBRID "bin" } else { $CubridBin = "C:\CUBRID\bin" }
$csql = Join-Path $CubridBin "csql.exe"
if (-not (Test-Path $csql)) { Write-Error "csql.exe 없음: $csql" }

$env:CUBRID = Split-Path -Parent $CubridBin
$env:PATH = "$CubridBin;$env:PATH"

$checkSql = @"
SELECT menu_cd, up_menu_cd, menu_nm, url, use_yn
  FROM tb_menu
 WHERE menu_cd IN ('MUBG05000', 'MUBG05100')
    OR url LIKE '%budgetSelectNew%'
    OR url LIKE '%budgetSelectAttr%'
 ORDER BY menu_cd;
"@

$tmp = Join-Path $env:TEMP "check-menu-budget-select.sql"
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($tmp, $checkSql, $utf8NoBom)

Write-Host "=== 예산안관리 메뉴 점검 ==="
& $csql -u $DbUser -p $DbPassword $DbName -i $tmp

$ok = $true
$out = & $csql -u $DbUser -p $DbPassword $DbName -c "SELECT COUNT(*) FROM tb_menu WHERE menu_cd='MUBG05000' AND up_menu_cd='MUBG00000' AND use_yn='Y' AND url='/budget/budgetSelectNew.do';" 2>&1
if ($out -notmatch '\s+1\s*$') {
    Write-Host "MISSING: MUBG05000 (조서·집계 항목선택)" -ForegroundColor Red
    $ok = $false
}

$out2 = & $csql -u $DbUser -p $DbPassword $DbName -c "SELECT COUNT(*) FROM tb_menu WHERE menu_cd='MUBG05100' AND up_menu_cd='MUBG00000' AND use_yn='Y' AND url='/budget/budgetSelectAttr.do';" 2>&1
if ($out2 -notmatch '\s+1\s*$') {
    Write-Host "MISSING: MUBG05100 (심사조서 보고항목선택)" -ForegroundColor Red
    $ok = $false
}

$out3 = & $csql -u $DbUser -p $DbPassword $DbName -c "SELECT COUNT(*) FROM tb_menu WHERE use_yn='Y' AND up_menu_cd<>'MUBG00000' AND (url LIKE '%budgetSelectAttr%' OR menu_nm LIKE '%심사조서%보고항목%');" 2>&1
if ($out3 -match '\s+([1-9]\d*)\s*$') {
    Write-Host "WARN: 예산안관리 밖에 심사조서 보고항목 관련 메뉴가 남아 있음" -ForegroundColor Yellow
    $ok = $false
}

if ($ok) {
    Write-Host "OK: 메뉴 배치 정상" -ForegroundColor Green
} else {
    Write-Host "조치: .\scripts\apply-menu-budget-select.ps1 -DbPassword `"...`"" -ForegroundColor Yellow
    exit 1
}

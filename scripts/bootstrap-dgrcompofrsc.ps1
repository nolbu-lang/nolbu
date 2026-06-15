# TB_DGRCOMPOFRSC 보완 — loaddb 에 없는 재원 테이블을 전 회계연도에 채운다.
# 조정액 재원(ADJ_DEF_FRSC_AMT) 기준: 투자조서는 tot_frsc 비율로 국고+자체 분리, 그 외는 자체 단일행.
# 기존 bootstrap 단일행(시비 전액)은 repair 스크립트로 국고+자체 분리 보정.

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$CubridBin = $null
if ($env:CUBRID) { $CubridBin = Join-Path $env:CUBRID "bin" }
if (-not $CubridBin -or -not (Test-Path (Join-Path $CubridBin "csql.exe"))) {
    $CubridBin = "C:\CUBRID\bin"
}
$csql = Join-Path $CubridBin "csql.exe"
$User = "bcjisapp"
$Password = "bcjisapp00"
$DbName = "bcjis"

$yearSql = Join-Path $env:TEMP "bootstrap-frsc-years.sql"
[System.IO.File]::WriteAllText($yearSql, "SELECT fis_year FROM TB_FISYEAR ORDER BY fis_year;")
$yearOut = & $csql -u $User -p $Password --no-single-line -i $yearSql $DbName 2>&1 | Out-String
$years = [regex]::Matches($yearOut, "'(\d{4})'") | ForEach-Object { $_.Groups[1].Value } | Select-Object -Unique
if (-not $years -or $years.Count -eq 0) {
    $years = @("2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025","2026")
}

$yearTemplate = Join-Path $ScriptDir "bootstrap-dgrcompofrsc-year.sql"
if (-not (Test-Path $yearTemplate)) {
    Write-Error "bootstrap-dgrcompofrsc-year.sql 없음: $yearTemplate"
}

Write-Host "TB_DGRCOMPOFRSC 보완 대상 연도: $($years -join ', ')"

foreach ($year in $years) {
    Write-Host "TB_DGRCOMPOFRSC 보완 중: $year ..."
    $sql = [IO.File]::ReadAllText($yearTemplate).Replace("@YEAR@", $year)
    $tmp = Join-Path $env:TEMP "bootstrap-frsc-$year.sql"
    [IO.File]::WriteAllText($tmp, $sql)
    & $csql -u $User -p $Password --no-single-line -i $tmp $DbName
    if ($LASTEXITCODE -ne 0) {
        Write-Error "bootstrap 실패: $year"
    }
}

$repairSql = Join-Path $ScriptDir "repair-dgrcompofrsc-invest-split.sql"
if (Test-Path $repairSql) {
    Write-Host "기존 bootstrap 단일행 → 국고+자체 분리 보정 ..."
    & $csql -u $User -p $Password --no-single-line -i $repairSql $DbName
    if ($LASTEXITCODE -ne 0) {
        Write-Error "repair-dgrcompofrsc-invest-split.sql 실패"
    }
}

Write-Host "TB_DGRCOMPOFRSC 보완 완료"

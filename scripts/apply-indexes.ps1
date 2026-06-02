# create-indexes.sql 의 CREATE INDEX 문을 개별 실행 (중복/오류 시 건너뛰고 계속)
$ErrorActionPreference = "Continue"

$CubridBin = $null
if ($env:CUBRID) { $CubridBin = Join-Path $env:CUBRID "bin" }
if (-not $CubridBin -or -not (Test-Path (Join-Path $CubridBin "csql.exe"))) {
    $CubridBin = "C:\CUBRID\bin"
}
$env:CUBRID = Split-Path -Parent $CubridBin
$env:PATH = "$CubridBin;$env:PATH"

$DbName = "bcjis"
$User = "bcjisapp"
$Password = "bcjisapp00"
$csql = Join-Path $CubridBin "csql.exe"
$sqlFile = Join-Path $PSScriptRoot "create-indexes.sql"

if (-not (Test-Path $sqlFile)) {
    Write-Error "파일 없음: $sqlFile"
    exit 1
}

$lines = Get-Content $sqlFile -Encoding UTF8
$stmts = @()
$buf = ""
foreach ($line in $lines) {
    $t = $line.Trim()
    if ($t -eq "" -or $t.StartsWith("/*") -or $t.StartsWith("*") -or $t.StartsWith("=")) { continue }
    $buf += " " + $line
    if ($t.EndsWith(";")) {
        $stmts += $buf.Trim()
        $buf = ""
    }
}

$ok = 0; $skip = 0; $fail = 0
foreach ($stmt in $stmts) {
    if ($stmt -notmatch "^CREATE INDEX") { continue }
    $idxName = if ($stmt -match "CREATE INDEX (\S+)") { $Matches[1] } else { "?" }
    Write-Host "실행: $idxName ..."
    $out = & $csql -u $User -p $Password $DbName -c $stmt 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0 -and $out -match "Execute OK") {
        $ok++
    } elseif ($out -match "already defined") {
        Write-Host "  -> 이미 존재 (건너뜀)"
        $skip++
    } else {
        Write-Warning "  -> 실패: $out"
        $fail++
    }
}

Write-Host ""
Write-Host "인덱스 적용 완료: 성공=$ok, 건너뜀=$skip, 실패=$fail"

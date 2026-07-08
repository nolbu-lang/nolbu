# AI 예산도우미 — 운영 CUBRID 인덱스 존재 여부 점검
# 사용: .\scripts\check-ai-indexes.ps1 -DbPassword "비밀번호"
param(
    [string]$DbName = "bcjis",
    [string]$DbUser = "bcjisapp",
    [Parameter(Mandatory = $true)]
    [string]$DbPassword
)

$ErrorActionPreference = "Continue"
$CubridBin = $null
if ($env:CUBRID) { $CubridBin = Join-Path $env:CUBRID "bin" }
if (-not $CubridBin -or -not (Test-Path (Join-Path $CubridBin "csql.exe"))) {
    $CubridBin = "C:\CUBRID\bin"
}
$env:CUBRID = Split-Path -Parent $CubridBin
$env:PATH = "$CubridBin;$env:PATH"
$csql = Join-Path $CubridBin "csql.exe"
if (-not (Test-Path $csql)) {
    Write-Error "csql.exe 없음: $csql"
    exit 1
}

# AI 검색·재원·조인에 꼭 필요한 인덱스
$required = @(
    "ix_dgrcompo_te",
    "ix_dgrcompo_leaf",
    "ix_dgrcompo_dbiz",
    "ix_dgrcompo_dept",
    "ix_dgrcompo_compground",
    "ix_dgrcompofrsc_te",
    "ix_dgrbiz_dbiz",
    "ix_dgrbiz_dbiz_nm",
    "ix_dgrdept_dept",
    "ix_report010_te",
    "ix_report020_te",
    "ix_bgtdgr_year",
    "ix_yearfrsc_cd"
)

Write-Host "=== AI 검색용 인덱스 점검 (DB=$DbName) ==="
$sql = "SELECT index_name FROM db_index WHERE class_name IN ('tb_dgrcompo','tb_dgrcompofrsc','tb_dgrbiz','tb_dgrdept','tb_report010','tb_report020','tb_bgtdgr','tb_yearfrsc') ORDER BY 1;"
$out = & $csql -u $DbUser -p $DbPassword $DbName -c $sql 2>&1 | Out-String
$found = @()
foreach ($name in $required) {
    if ($out -match [regex]::Escape($name)) {
        Write-Host "  OK  $name"
        $found += $name
    } else {
        Write-Host "  MISSING  $name" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host ("결과: {0}/{1} 존재" -f $found.Count, $required.Count)
if ($found.Count -lt $required.Count) {
    Write-Host ""
    Write-Host "누락 인덱스가 있습니다. 운영에서 다음을 실행하세요:"
    Write-Host "  .\scripts\apply-indexes.ps1 -DbPassword `"비밀번호`""
    exit 2
}
Write-Host "AI 검색 핵심 인덱스가 모두 있습니다."
exit 0

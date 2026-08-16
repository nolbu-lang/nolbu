# 운영 서버 관리자 전달용 배포 패키지 생성
# 사용: .\scripts\package-deploy.ps1
#       .\scripts\package-deploy.ps1 -SkipBuild   (이미 빌드된 WAR 사용)
#
# 생성물: bcjis-배포-YYYYMMDD/ 폴더 + bcjis-배포-YYYYMMDD.zip

param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$DateTag = Get-Date -Format "yyyyMMdd"
$PackageName = "bcjis-배포-$DateTag"
$OutDir = Join-Path $ProjectRoot $PackageName
$ZipPath = Join-Path $ProjectRoot "$PackageName.zip"

if (-not $SkipBuild) {
    Write-Host "=== Maven WAR 빌드 ==="
    & (Join-Path $ProjectRoot "scripts\build.ps1")
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

$WarSrc = Join-Path $ProjectRoot "target\bcjis-webapp.war"
if (-not (Test-Path $WarSrc)) {
    Write-Error "WAR 없음: $WarSrc (build.ps1 실행 필요)"
}

if (Test-Path $OutDir) { Remove-Item $OutDir -Recurse -Force }
New-Item -ItemType Directory -Path $OutDir | Out-Null
New-Item -ItemType Directory -Path (Join-Path $OutDir "deploy") | Out-Null
New-Item -ItemType Directory -Path (Join-Path $OutDir "scripts") | Out-Null
New-Item -ItemType Directory -Path (Join-Path $OutDir "docs") | Out-Null

Write-Host "=== 패키지 조립: $PackageName ==="

Copy-Item $WarSrc (Join-Path $OutDir "bcjis-webapp.war") -Force

$deployFiles = @(
    "deploy\deploy-all.ps1",
    "deploy\deploy-app.ps1",
    "deploy\deploy-db.ps1",
    "deploy\run-ops-deploy.ps1",
    "deploy\merge-globals-ai.ps1",
    "deploy\README-관리자용.md",
    "deploy\README-운영배포-AI.md",
    "deploy\globals.properties.ai-snippet.example"
)
foreach ($f in $deployFiles) {
    $src = Join-Path $ProjectRoot $f
    if (Test-Path $src) {
        Copy-Item $src (Join-Path $OutDir $f) -Force
    }
}

$scriptFiles = @(
    "scripts\apply-indexes.ps1",
    "scripts\check-ai-indexes.ps1",
    "scripts\check-menu-budget-select.ps1",
    "scripts\apply-menu-budget-select.ps1",
    "scripts\apply-menu-budget-pc-parity.ps1",
    "scripts\create-indexes.sql",
    "scripts\patch-menu-budget-copy.sql",
    "scripts\patch-menu-budget-select-all.sql",
    "scripts\sync-menu-budget-pc-parity.sql",
    "scripts\reorder-menu-budget-select-copy.sql",
    "scripts\restore-menu-budget-copynew.sql",
    "scripts\seed-comm-seq.sql",
    "scripts\create-tb-bizdesc.sql",
    "scripts\alter-tb-bizdesc-bgt-dgr.sql",
    "scripts\alter-tb-bizdesc-office.sql",
    "scripts\add-menu-budget-select-split.sql",
    "scripts\rename-menu-attr-report-select.sql",
    "scripts\rename-menu-attr-to-class-item.sql",
    "scripts\rename-gov-sub-matching-to-direct.sql"
)
foreach ($f in $scriptFiles) {
    $src = Join-Path $ProjectRoot $f
    if (Test-Path $src) {
        Copy-Item $src (Join-Path $OutDir $f) -Force
    }
}

$guideName = "docs\운영서버_배포목록_$DateTag.md"
$docFiles = @(
    $guideName,
    "docs\운영서버_배포_미반영_원인분석_20260816.md",
    "docs\운영서버_배포목록_20260816.md",
    "docs\운영서버_배포_가이드_20260812.md",
    "docs\운영서버_배포목록_20260812.md",
    "docs\운영서버_배포_가이드_20260812.pdf",
    "docs\운영서버_배포목록_20260812.pdf",
    "docs\운영서버_배포_가이드_20260807.md",
    "docs\AI작업자_인수인계_참고사항.md",
    "docs\운영배포_종합개선보고서.md",
    "docs\업무서버_적용_가이드.md",
    "docs\개선사항_보고서.md",
    "docs\배포준비_시험점검_보고서.md",
    "docs\AI예산편성도우미_사용설명서.md",
    "docs\AI예산도우미_RAG구현보고서_및_하이퍼클로바연동방안.md"
)
foreach ($f in $docFiles) {
    $src = Join-Path $ProjectRoot $f
    if (Test-Path $src) {
        Copy-Item $src (Join-Path $OutDir $f) -Force
    }
}

$readme = @"
# bcjis 개선본 배포 패키지 ($DateTag)

## 전달 목적
예산편성심사정보시스템 — AI 예산도우미 + 심사정보 기능개선 (8/7·8/12 미반영 보완 + 2026-08-16)

## 중요 (이전 배포 실패 원인)
- 메뉴는 WAR가 아니라 DB(TB_MENU)입니다.
- 서버에 [삭제예정]/[매핑일괄테스트]가 보이면 메뉴 SQL 미적용입니다.
- 반드시 scripts\apply-menu-budget-pc-parity.ps1 을 실행하고 SELECT 결과를 회신하세요.
- patch-menu-budget-copy.sql 은 실행하지 마세요 (New 화면을 숨깁니다).

## 관리자가 받을 파일
1. bcjis-webapp.war
2. deploy/ (globals AI 스니펫)
3. scripts/ (인덱스 + 메뉴 PC동기화)
4. docs/운영서버_배포_미반영_원인분석_20260816.md
5. docs/운영서버_배포목록_20260816.md  ← 적용 체크리스트

## 빠른 시작
1. docs\운영서버_배포_미반영_원인분석_20260816.md 열람
2. docs\운영서버_배포목록_20260816.md 확인
3. 서버에서 한 줄 실행:
   .\deploy\run-ops-deploy.ps1 -TomcatHome "..." -DbPassword "..." -ContextName "ROOT"
4. (보도자료 reset 시) WAS→www.busan.go.kr:443 방화벽 허용 후 동일 명령 재실행

## GitHub
https://github.com/nolbu-lang/nolbu.git (main)
"@
$readmePath = Join-Path $OutDir "README-DEPLOY.txt"
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($readmePath, $readme, $utf8NoBom)

if (Test-Path $ZipPath) { Remove-Item $ZipPath -Force }
Compress-Archive -Path $OutDir -DestinationPath $ZipPath -Force

Write-Host ""
Write-Host "=== 패키지 생성 완료 ==="
Write-Host "폴더: $OutDir"
Write-Host "ZIP : $ZipPath"
Write-Host ""
Write-Host "서버 관리자에게 위 ZIP(또는 폴더)을 전달하세요."

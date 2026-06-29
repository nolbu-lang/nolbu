# bcjis 소스 변경분 패키지 생성
# Initial import(6d68363) 대비 변경·추가 파일을 동일 경로 구조로 묶음
# 사용: .\scripts\package-source-diff.ps1
#       .\scripts\package-source-diff.ps1 -BaseCommit fe8e9ee  (특정 커밋 이후만)

param(
    [string]$BaseCommit = "6d68363"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$DateTag = Get-Date -Format "yyyyMMdd"
# ASCII folder name (PowerShell script encoding safe); same role as bcjis-소스변경분-YYYYMMDD
$PackageName = "bcjis-source-diff-$DateTag"
$OutDir = Join-Path $ProjectRoot $PackageName
$ZipPath = Join-Path $ProjectRoot "$PackageName.zip"

Set-Location $ProjectRoot

$gitFiles = @(git diff --name-only "${BaseCommit}..HEAD" 2>$null | ForEach-Object { $_.Trim('"') })

# 패키지에 항상 포함할 추가 파일 (git diff에 없을 수 있음)
$extraFiles = @(
    "deploy/globals.properties.ai-snippet.example",
    "docs/운영배포_종합개선보고서.md"
)

$allFiles = New-Object System.Collections.Generic.List[string]
foreach ($f in $gitFiles) {
    if ($f -and (Test-Path (Join-Path $ProjectRoot $f))) {
        [void]$allFiles.Add($f)
    }
}
foreach ($f in $extraFiles) {
    if ((Test-Path (Join-Path $ProjectRoot $f)) -and -not $allFiles.Contains($f)) {
        [void]$allFiles.Add($f)
    }
}

# 제외: globals.properties, target, war, zip
$excludePattern = 'globals\.properties$|\.war$|target/|bcjis-배포|bcjis-소스변경분'
$filtered = @()
foreach ($f in $allFiles) {
    if ($f -notmatch $excludePattern) {
        $filtered += $f
    }
}

if (Test-Path $OutDir) { Remove-Item $OutDir -Recurse -Force }
New-Item -ItemType Directory -Path $OutDir | Out-Null

Write-Host "=== 소스 변경분 조립: $PackageName ($($filtered.Count) files) ==="

foreach ($rel in ($filtered | Sort-Object)) {
    $src = Join-Path $ProjectRoot $rel
    $dst = Join-Path $OutDir $rel
    $dstDir = Split-Path -Parent $dst
    if (-not (Test-Path $dstDir)) {
        New-Item -ItemType Directory -Path $dstDir -Force | Out-Null
    }
    Copy-Item $src $dst -Force
}

$headShort = git rev-parse --short HEAD
$readme = @"
# bcjis 소스 변경분 (예산편성 개선 + AI 예산도우미)

| 항목 | 내용 |
|------|------|
| 패키지명 | $PackageName |
| 기준 | Initial import ($BaseCommit) 대비 변경·추가 파일 |
| Git HEAD | $headShort |
| 용도 | 기존 프로젝트에 **동일 경로로 덮어쓴 뒤** Maven 빌드 |

---

## 1. 적용 방법

1. 운영/개발용 **기존 프로젝트 전체**를 백업합니다.
2. 이 ZIP을 압축 해제합니다.
3. 아래 파일을 **프로젝트 루트 기준 동일 경로**에 복사(덮어쓰기)합니다.
4. Maven 빌드: ``scripts\build.ps1`` 또는 ``mvn clean package -DskipTests``
5. ``target\bcjis-webapp.war`` 를 Tomcat에 배포합니다.
6. DB 스크립트(``scripts\``)는 WAR와 별도 실행 — ``deploy\README-관리자용.md`` 참고.
7. AI 기능: ``deploy\globals.properties.ai-snippet.example`` → 기존 globals.properties 에 **추가**.

---

## 2. 개선 범위

| 과제 | 주요 경로 |
|------|-----------|
| **예산편성 개선** | budget/BudgetCopyNew*, budgetCopy.js, AccessLogFilter, scripts/create-indexes.sql |
| **AI 예산도우미** | com/cs/bcjis/ai/*, webapp/ai/*, main.jsp (챗봇 include) |

---

## 3. 포함 파일 수

**$($filtered.Count)** 개 (자동 생성 목록 — ``scripts/package-source-diff.ps1``)

---

## 4. 주의

- ``globals.properties`` 는 **포함하지 않음** (서버별 DB·AI URL 유지)
- WAR 일괄 배포는 ``scripts\package-deploy.ps1`` 사용 권장
- 상세: ``docs/운영배포_종합개선보고서.md``

---

*생성일: $DateTag*
"@

$readmePath = Join-Path $OutDir "README-APPLY.txt"
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($readmePath, $readme, $utf8NoBom)

# 파일 목록 manifest
$manifestPath = Join-Path $OutDir "MANIFEST.txt"
[System.IO.File]::WriteAllLines($manifestPath, ($filtered | Sort-Object), $utf8NoBom)

if (Test-Path $ZipPath) { Remove-Item $ZipPath -Force }
Compress-Archive -Path $OutDir -DestinationPath $ZipPath -Force

Write-Host ""
Write-Host "=== 소스 변경분 패키지 완료 ==="
Write-Host "폴더: $OutDir"
Write-Host "ZIP : $ZipPath"
Write-Host "Files: $($filtered.Count)"

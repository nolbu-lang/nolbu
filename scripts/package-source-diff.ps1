# bcjis 소스 변경분 패키지 생성
# Initial import(6d68363) 대비 변경·추가 파일을 동일 경로 구조로 묶음
# 사용: .\scripts\package-source-diff.ps1
#       .\scripts\package-source-diff.ps1 -BaseCommit fe8e9ee  (특정 커밋 이후만)

param(
    # bcjis-소스변경분-20260629 (커밋 5e63f07) 이후 변경분
    [string]$BaseCommit = "5e63f07"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$DateTag = Get-Date -Format "yyyyMMdd"
$PackageName = "bcjis-source-diff-$DateTag"
$KoreanAlias = Join-Path $ProjectRoot ("bcjis-" + [string][char]0xC18C + [char]0xC2A4 + [char]0xBCC0 + [char]0xACBD + [char]0xBD84 + "-$DateTag")
$OutDir = Join-Path $ProjectRoot $PackageName
$ZipPath = Join-Path $ProjectRoot "$PackageName.zip"

Set-Location $ProjectRoot

$gitFiles = @(
    & git -c core.safecrlf=false diff --name-only $BaseCommit 2>$null
    & git ls-files --others --exclude-standard 2>$null
) | ForEach-Object { $_.Trim('"') } | Where-Object { $_ } | Select-Object -Unique

# 패키지에 항상 포함할 추가 파일 (git diff에 없을 수 있음)
$extraFiles = @(
    "deploy/globals.properties.ai-snippet.example",
    "deploy/README-운영배포-AI.md",
    "docs/운영서버_배포_가이드_$DateTag.md",
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
# docs/ 운영서버_배포_가이드_YYYYMMDD.md 등 날짜 접미 문서
$docDir = Join-Path $ProjectRoot "docs"
if (Test-Path $docDir) {
    Get-ChildItem $docDir -Filter "*_$DateTag.md" -File -ErrorAction SilentlyContinue | ForEach-Object {
        $rel = "docs/" + $_.Name
        if (-not $allFiles.Contains($rel)) { [void]$allFiles.Add($rel) }
    }
}
$deployAiReadme = Join-Path $ProjectRoot "deploy/README-운영배포-AI.md"
if (Test-Path $deployAiReadme) {
    $rel = "deploy/README-운영배포-AI.md"
    if (-not $allFiles.Contains($rel)) { [void]$allFiles.Add($rel) }
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
# bcjis 소스 변경분 ($DateTag)

| 항목 | 내용 |
|------|------|
| 패키지명 | $PackageName |
| 이전 패키지 | bcjis-소스변경분-20260629 (이미 적용된 경우 **이 폴더만** 덮어쓰기) |
| Git 기준 | 커밋 $BaseCommit 이후 변경분 |
| Git HEAD | $headShort |
| GitHub Compare | https://github.com/nolbu-lang/nolbu/compare/${BaseCommit}...main |

---

## 1. 이번 변경 요약

| 항목 | 내용 |
|------|------|
| 조정재원 표시 | AI 표·상세의 국비·시비·기타 — 심사조서 재원별 그리드와 동일 |
| 검색 속도 | 운영 DB: 좁은 사업명 검색, 연도 확장 축소, 전체연도 폴백 비활성 |
| UI | aiChat.jsp/js/css 캐시 버전 갱신 |
| 배포 | apply-indexes.ps1 DB 비밀번호 인자, globals 예시 보강 |

---

## 2. 적용 방법

1. **bcjis-소스변경분-20260629** 를 이미 반영한 프로젝트(또는 GitHub main)를 백업합니다.
2. 이 ZIP을 압축 해제합니다.
3. ``MANIFEST.txt`` 목록 파일을 **프로젝트 루트 기준 동일 경로**에 복사(덮어쓰기)합니다.
4. Maven 빌드: ``scripts\build.ps1`` 또는 ``mvn clean package -DskipTests``
5. ``target\bcjis-webapp.war`` → Tomcat ``webapps\`` (기존 폴더 삭제 후 교체)
6. DB: ``scripts\apply-indexes.ps1 -DbPassword "..."`` (미적용 시)
7. ``deploy\globals.properties.ai-snippet.example`` → globals.properties **추가** 후 Tomcat 재기동

상세: ``docs/운영서버_배포_가이드_$DateTag.md``

---

## 3. 포함 파일

**$($filtered.Count)** 개 — 전체 목록은 ``MANIFEST.txt``

---

## 4. 주의

- ``globals.properties`` 실제 운영 파일은 **포함하지 않음**
- WAR만 받을 경우: ``scripts\package-deploy.ps1`` 로 생성한 ``bcjis-배포-$DateTag`` 사용

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

# 한글 폴더명 별칭 (bcjis-소스변경분-YYYYMMDD)
if (Test-Path $KoreanAlias) { Remove-Item $KoreanAlias -Recurse -Force }
Copy-Item $OutDir $KoreanAlias -Recurse -Force
$KoreanZip = "$KoreanAlias.zip"
if (Test-Path $KoreanZip) { Remove-Item $KoreanZip -Force }
Compress-Archive -Path $KoreanAlias -DestinationPath $KoreanZip -Force

Write-Host ""
Write-Host "=== 소스 변경분 패키지 완료 ==="
Write-Host "폴더: $OutDir"
Write-Host "한글: $KoreanAlias"
Write-Host "ZIP : $ZipPath"
Write-Host "한글 ZIP: $KoreanZip"
Write-Host "Files: $($filtered.Count)"

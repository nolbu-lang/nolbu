# 운영 globals.properties 에 AI 필수 키를 병합(덮어쓰기·추가)
# - DB 접속 등 기존 키는 유지
# - AI 관련 키는 스니펫 값으로 갱신
# - WAR 배포 후 반드시 다시 실행 (WAR가 globals를 덮을 수 있음)
#
# 사용:
#   .\deploy\merge-globals-ai.ps1 -GlobalsPath "D:\was\...\WEB-INF\classes\csframework\bcjisProps\globals.properties"
param(
    [Parameter(Mandatory = $true)]
    [string]$GlobalsPath,

    [string]$SnippetPath = ""
)

$ErrorActionPreference = "Stop"
$Here = $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($SnippetPath)) {
    $SnippetPath = Join-Path $Here "globals.properties.ai-snippet.example"
}
if (-not (Test-Path $GlobalsPath)) { Write-Error "globals 없음: $GlobalsPath" }
if (-not (Test-Path $SnippetPath)) { Write-Error "스니펫 없음: $SnippetPath" }

function Read-PropMap([string]$path) {
    $map = New-Object 'System.Collections.Generic.Dictionary[string,string]' ([StringComparer]::OrdinalIgnoreCase)
    $lines = Get-Content -Path $path -Encoding UTF8
    foreach ($line in $lines) {
        $t = $line.Trim()
        if ($t.Length -lt 1 -or $t.StartsWith("#")) { continue }
        $idx = $t.IndexOf("=")
        if ($idx -lt 1) { continue }
        $k = $t.Substring(0, $idx).Trim()
        $v = $t.Substring($idx + 1).Trim()
        $map[$k] = $v
    }
    return $map
}

$bak = "$GlobalsPath.bak.ai.$(Get-Date -Format 'yyyyMMdd-HHmmss')"
Copy-Item -Force $GlobalsPath $bak
Write-Host "백업: $bak"

$existing = Read-PropMap $GlobalsPath
$snippet = Read-PropMap $SnippetPath

# 스니펫의 AI/Clova 관련 키만 병합 (DB 접속 Globals.DriverClassName 등은 스니펫에 없음)
foreach ($k in $snippet.Keys) {
    $existing[$k] = $snippet[$k]
}

# 법령 OC 강제 (증상: 키 미적용)
$existing["Globals.AiLawGoKrOc"] = "nolbu0326"
if (-not $existing.ContainsKey("Globals.AiLawGoKrBaseUrl") -or [string]::IsNullOrWhiteSpace($existing["Globals.AiLawGoKrBaseUrl"])) {
    $existing["Globals.AiLawGoKrBaseUrl"] = "https://www.law.go.kr/DRF/lawSearch.do"
}
if (-not $existing.ContainsKey("Globals.AiLawOrdinOrgCd") -or [string]::IsNullOrWhiteSpace($existing["Globals.AiLawOrdinOrgCd"])) {
    $existing["Globals.AiLawOrdinOrgCd"] = "6260000"
}
# 보도자료
if (-not $existing.ContainsKey("Globals.AiBusanHomepageBaseUrl") -or [string]::IsNullOrWhiteSpace($existing["Globals.AiBusanHomepageBaseUrl"])) {
    $existing["Globals.AiBusanHomepageBaseUrl"] = "https://www.busan.go.kr"
}
$existing["Globals.AiBusanHomepageTimeoutMs"] = "20000"
# 예산운용지침 품질
$existing["Globals.AiManualUseLlm"] = "true"
$existing["Globals.AiManualMaxPages"] = "3"
$existing["Globals.AiManualExcerptChars"] = "2200"
$existing["Globals.AiManualSummaryChars"] = "6500"
$existing["Globals.AiManualPromptChars"] = "3600"
$existing["Globals.AiManualMaxFiles"] = "4"
$existing["Globals.AiManualLlmMaxFiles"] = "2"
if (-not $existing.ContainsKey("Globals.AiManualStorePath") -or [string]::IsNullOrWhiteSpace($existing["Globals.AiManualStorePath"])) {
    $existing["Globals.AiManualStorePath"] = "C:/bcjis/upload/ai-manual/"
}

# 원본 파일에서 AI 블록 이전 줄은 보존하고, AI 키는 재기록하기 어려우므로
# 전체 파일을 "기존 non-AI 유지 + AI 키 통일"로 재구성하지 않고
# 원문 + 마커 블록 방식으로 끝에 최신 AI 블록을 붙인 뒤, 중복 키는 Java Properties가
# 보통 마지막 값을 씀 — 앱이 어떤 파서를 쓰는지에 따라 다름.
# → 안전하게: 원문에서 Globals.Ai* / Globals.Clova* / Globals.Gemini* 줄을 제거하고 스니펫을 맨 아래에 붙임.

$raw = Get-Content -Path $GlobalsPath -Encoding UTF8
$kept = New-Object System.Collections.Generic.List[string]
foreach ($line in $raw) {
    $t = $line.Trim()
    if ($t -match '^(Globals\.Ai|Globals\.Clova|Globals\.Gemini)') {
        continue
    }
    # 이전 배포 마커 구간 제거
    if ($t -match 'AI 예산도우미|법제처 Open API|시홈페이지\(보도자료') {
        continue
    }
    $kept.Add($line) | Out-Null
}

$snippetLines = Get-Content -Path $SnippetPath -Encoding UTF8
$out = New-Object System.Collections.Generic.List[string]
foreach ($l in $kept) { $out.Add($l) | Out-Null }
$out.Add("") | Out-Null
$out.Add("# ===== AI 예산도우미 (merge-globals-ai.ps1 $(Get-Date -Format 'yyyy-MM-dd HH:mm')) =====") | Out-Null
foreach ($l in $snippetLines) {
    if ($l.Trim().StartsWith("#---")) { continue }
    if ($l.Trim() -match '^# 운영 전 확인') { break }
    $out.Add($l) | Out-Null
}
# OC 최종 강제 한 줄 (스니펫에 있어도 재확인)
$out.Add("Globals.AiLawGoKrOc = nolbu0326") | Out-Null

$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllLines($GlobalsPath, $out.ToArray(), $utf8NoBom)

Write-Host "병합 완료: $GlobalsPath"
Write-Host "확인:"
Select-String -Path $GlobalsPath -Pattern 'AiLawGoKrOc|AiBusanHomepageBaseUrl|AiManualUseLlm|AiManualExcerptChars' |
    ForEach-Object { Write-Host ("  " + $_.Line.Trim()) }

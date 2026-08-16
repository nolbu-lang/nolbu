# =====================================================================
# 운영 서버 일괄 적용 (회신/피드백 없이 순서 실행)
# 개발자가 만든 bcjis-배포-YYYYMMDD 폴더에서 실행
#
# 예:
#   cd D:\deploy\bcjis-배포-20260816
#   .\deploy\run-ops-deploy.ps1 `
#       -TomcatHome "D:\was\apache-tomcat-9.0.89" `
#       -DbPassword "운영비밀번호" `
#       -ContextName "ROOT"
#
# ContextName:
#   - 서버 URL이 http://99.1.1.39:8080/main/... 이면 보통 ROOT
#   - http://.../bcjis-webapp/main/... 이면 bcjis-webapp
# =====================================================================
param(
    [Parameter(Mandatory = $true)]
    [string]$TomcatHome,

    [Parameter(Mandatory = $true)]
    [string]$DbPassword,

    [string]$DbName = "bcjis",
    [string]$DbUser = "bcjisapp",

    # ROOT 또는 bcjis-webapp
    [string]$ContextName = "ROOT",

    [string]$GlobalsPath = "",

    [switch]$SkipTomcatRestart,
    [switch]$SkipNetworkCheck
)

$ErrorActionPreference = "Stop"
$PackageRoot = Split-Path -Parent $PSScriptRoot
if (-not (Test-Path (Join-Path $PackageRoot "bcjis-webapp.war")) -and -not (Test-Path (Join-Path $PackageRoot "deploy"))) {
    # 소스 트리에서 실행하는 경우
    $PackageRoot = Split-Path -Parent $PSScriptRoot
}
$Scripts = Join-Path $PackageRoot "scripts"
$Deploy = Join-Path $PackageRoot "deploy"
$WarSrc = Join-Path $PackageRoot "bcjis-webapp.war"
if (-not (Test-Path $WarSrc)) {
    $WarSrc = Join-Path $PackageRoot "target\bcjis-webapp.war"
}

function Step([string]$msg) {
    Write-Host ""
    Write-Host "============================================================"
    Write-Host " $msg"
    Write-Host "============================================================"
}

function Resolve-WebappDir {
    param([string]$TomcatHome, [string]$ContextName)
    if ($ContextName -eq "ROOT") {
        return (Join-Path $TomcatHome "webapps\ROOT")
    }
    return (Join-Path $TomcatHome "webapps\$ContextName")
}

function Resolve-GlobalsPath {
    param([string]$WebappDir, [string]$Override)
    if (-not [string]::IsNullOrWhiteSpace($Override)) { return $Override }
    $p1 = Join-Path $WebappDir "WEB-INF\classes\csframework\bcjisProps\globals.properties"
    if (Test-Path $p1) { return $p1 }
    $p2 = Join-Path $WebappDir "WEB-INF\classes\globals.properties"
    if (Test-Path $p2) { return $p2 }
    return $p1
}

$webappDir = Resolve-WebappDir -TomcatHome $TomcatHome -ContextName $ContextName
$bin = Join-Path $TomcatHome "bin"

# ----- 0) 사전 확인 -----
Step "0/7 사전 확인"
if (-not (Test-Path $TomcatHome)) { Write-Error "TomcatHome 없음: $TomcatHome" }
if (-not (Test-Path $WarSrc)) { Write-Error "WAR 없음: $WarSrc" }
Write-Host "PackageRoot = $PackageRoot"
Write-Host "WAR         = $WarSrc"
Write-Host "WebappDir   = $webappDir"
Write-Host "ContextName = $ContextName"

if (-not $SkipNetworkCheck) {
    Step "0-1/7 외부망 점검 (법령·보도자료)"
    foreach ($hostName in @("www.law.go.kr", "www.busan.go.kr")) {
        try {
            $r = Test-NetConnection -ComputerName $hostName -Port 443 -WarningAction SilentlyContinue
            if ($r.TcpTestSucceeded) {
                Write-Host "OK  $hostName:443" -ForegroundColor Green
            } else {
                Write-Host "FAIL $hostName:443 — 방화벽에서 WAS→해당 호스트 443 허용 필요 (Connection reset 원인)" -ForegroundColor Yellow
            }
        } catch {
            Write-Host "WARN $hostName 점검 실패: $_" -ForegroundColor Yellow
        }
    }
}

# ----- 1) DB 인덱스 -----
Step "1/7 DB 인덱스 적용"
& (Join-Path $Scripts "apply-indexes.ps1") -DbName $DbName -DbUser $DbUser -DbPassword $DbPassword

# ----- 2) 메뉴 PC 동기화 (구 patch-menu-budget-copy 사용 금지) -----
Step "2/7 메뉴 PC 동기화 (sync-menu-budget-pc-parity)"
& (Join-Path $Scripts "apply-menu-budget-pc-parity.ps1") -DbName $DbName -DbUser $DbUser -DbPassword $DbPassword

# ----- 3) Tomcat 중지 -----
Step "3/7 Tomcat 중지"
if (-not $SkipTomcatRestart) {
    $stopBat = Join-Path $bin "shutdown.bat"
    if (Test-Path $stopBat) {
        Push-Location $bin
        cmd /c "shutdown.bat"
        Pop-Location
        Start-Sleep -Seconds 8
    } else {
        Write-Warning "shutdown.bat 없음 — Tomcat을 수동 중지한 뒤 Enter"
        Read-Host "중지 후 Enter"
    }
}

# ----- 4) WAR 교체 -----
Step "4/7 WAR 교체"
$destWar = if ($ContextName -eq "ROOT") {
    Join-Path $TomcatHome "webapps\ROOT.war"
} else {
    Join-Path $TomcatHome "webapps\$ContextName.war"
}
# ROOT는 exploded만 쓰는 경우가 많아 WAR+폴더 모두 처리
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
if (Test-Path $webappDir) {
    $bakDir = "$webappDir.bak.$stamp"
    Write-Host "기존 webapp 백업: $bakDir"
    Rename-Item $webappDir $bakDir
}
if (Test-Path $destWar) {
    Copy-Item $destWar "$destWar.bak.$stamp" -Force
}
# ROOT exploded 배포: WAR를 ROOT.war로 복사 후 Tomcat이 풀도록 하거나, 수동 압축 해제
Copy-Item $WarSrc $destWar -Force
Write-Host "WAR 복사: $destWar"

# work 캐시 삭제
$work = Join-Path $TomcatHome "work\Catalina"
if (Test-Path $work) {
    Remove-Item -Recurse -Force $work -ErrorAction SilentlyContinue
    Write-Host "work\Catalina 삭제"
}

# ----- 5) Tomcat 기동 (WAR explode 대기) -----
Step "5/7 Tomcat 기동 + explode 대기"
if (-not $SkipTomcatRestart) {
    $startBat = Join-Path $bin "startup.bat"
    if (Test-Path $startBat) {
        Push-Location $bin
        cmd /c "startup.bat"
        Pop-Location
    } else {
        Write-Warning "startup.bat 없음 — Tomcat을 수동 기동한 뒤 Enter"
        Read-Host "기동 후 Enter"
    }
    # explode 대기
    $globalsProbe = Resolve-GlobalsPath -WebappDir $webappDir -Override ""
    $deadline = (Get-Date).AddMinutes(3)
    while ((Get-Date) -lt $deadline) {
        if (Test-Path $globalsProbe) { break }
        Start-Sleep -Seconds 3
    }
    if (-not (Test-Path $globalsProbe)) {
        Write-Warning "globals 파일이 아직 없습니다. WAR explode 후 GlobalsPath를 지정해 merge를 다시 실행하세요."
        Write-Warning "예상 경로: $globalsProbe"
    }
}

# ----- 6) globals AI 병합 (WAR 덮어쓰기 이후 — 핵심) -----
Step "6/7 globals AI 병합 (법령OC·보도자료·운용지침) — WAR 이후 필수"
$gPath = Resolve-GlobalsPath -WebappDir $webappDir -Override $GlobalsPath
if (-not (Test-Path $gPath)) {
    Write-Error "globals.properties 없음: $gPath  — ContextName/경로 확인 후 -GlobalsPath 로 지정"
}
& (Join-Path $Deploy "merge-globals-ai.ps1") -GlobalsPath $gPath -SnippetPath (Join-Path $Deploy "globals.properties.ai-snippet.example")

# 매뉴얼 폴더
$manualDir = "C:\bcjis\upload\ai-manual"
if (-not (Test-Path $manualDir)) {
    New-Item -ItemType Directory -Force -Path $manualDir | Out-Null
    Write-Host "매뉴얼 폴더 생성: $manualDir"
}

# ----- 7) globals 반영 위해 재기동 -----
Step "7/7 globals 반영을 위한 Tomcat 재기동"
if (-not $SkipTomcatRestart) {
    Push-Location $bin
    if (Test-Path (Join-Path $bin "shutdown.bat")) { cmd /c "shutdown.bat"; Start-Sleep -Seconds 8 }
    if (Test-Path (Join-Path $bin "startup.bat")) { cmd /c "startup.bat" }
    Pop-Location
}

Step "완료"
Write-Host @"

다음을 브라우저에서 확인하세요 (Ctrl+F5, 재로그인).
1) 예산안관리: 조서·집계 → 전년도[신규] → 보고항목선택
2) AI 일반자료검색-법령조례: OC 오류 없이 검색
3) AI 보도자료/고시공고: Connection reset 없이 목록
4) AI 예산운용지침: 금액·기준 포함 요약

보도자료가 여전히 reset 이면 WAS→www.busan.go.kr:443 방화벽 허용이 필요합니다.
(0단계 네트워크 점검 FAIL 항목 참고)

"@

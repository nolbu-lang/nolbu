# Maven 빌드 (WAR 생성)
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot

$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
$mavenHome = Get-ChildItem "C:\tools" -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -like "apache-maven*" } | Select-Object -First 1
if ($mavenHome) {
    $env:Path = "$($mavenHome.FullName)\bin;" + $env:Path
}

Set-Location $ProjectRoot
$settings = Join-Path $PSScriptRoot "maven-settings.xml"
mvn -s $settings clean package -DskipTests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$war = Join-Path $ProjectRoot "target\bcjis-webapp.war"
Write-Host ""
Write-Host "빌드 완료: $war"
Write-Host "Tomcat webapps 폴더에 복사 후 Tomcat을 시작하세요."

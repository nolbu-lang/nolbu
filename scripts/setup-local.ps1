# 로컬 개발 환경 준비 (JDK/Maven은 별도 설치)
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$PropsDir = Join-Path $ProjectRoot "src\main\resources\csframework\bcjisProps"
$Example = Join-Path $PropsDir "globals.properties.example"
$Target = Join-Path $PropsDir "globals.properties"

if (-not (Test-Path $Target)) {
    Copy-Item $Example $Target
    Write-Host "globals.properties 생성됨"
} else {
    Write-Host "globals.properties 이미 존재"
}

New-Item -ItemType Directory -Force -Path "C:\bcjis\upload" | Out-Null
New-Item -ItemType Directory -Force -Path "C:\bcjis\download\excel" | Out-Null
Write-Host "C:\bcjis\upload, C:\bcjis\download\excel 폴더 준비 완료"

Write-Host ""
Write-Host "Next: install CUBRID, then run scripts\import-cubrid-data.ps1"

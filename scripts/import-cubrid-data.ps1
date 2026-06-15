# CUBRID loaddb 로 테이블 데이터 적재
# 사전 조건: CUBRID 설치, DB 'bcjis' 생성, 테이블 DDL 생성 완료
$ErrorActionPreference = "Stop"

$CubridBin = $null
$candidates = @()
if ($env:CUBRID) { $candidates += (Join-Path $env:CUBRID "bin") }
$candidates += "C:\CUBRID\bin"
$candidates += "C:\Program Files\CUBRID\bin"
$candidates += "C:\Program Files (x86)\CUBRID\bin"
foreach ($c in $candidates) {
    if ($c -and (Test-Path (Join-Path $c "loaddb.exe"))) { $CubridBin = $c; break }
}
if (-not $CubridBin) {
    Write-Error "CUBRID bin 경로를 찾을 수 없습니다. CUBRID 설치 후 CUBRID\bin 을 PATH에 추가하세요."
}

# loaddb 가 의존 DLL을 찾도록 환경 설정
$env:CUBRID = Split-Path -Parent $CubridBin
$env:PATH = "$CubridBin;$env:PATH"

$DataDir = "C:\Users\COMTREE\Documents\심사정보시스템\테이블\테이블"
$DbName = "bcjis"
$User = "bcjisapp"
$Password = "bcjisapp00"

# 마스터 → 예산 → 조서 순 (FK 고려)
$files = @(
    "bcjis_objects_TB_COMMCD",
    "bcjis_objects_TB_COMMCDDETL",
    "bcjis_objects_TB_FISYEAR",
    "bcjis_objects_TB_BGTDGR",
    "bcjis_objects_TB_YEARFRSC",
    "bcjis_objects_TB_YEARFISFG",
    "bcjis_objects_TB_DGRDEPT",
    "bcjis_objects_TB_DGRBIZ",
    "bcjis_objects_TB_DGRTEMNGMOK",
    "bcjis_objects_TB_DGRCOMPO",
    "bcjis_objects_TB_REPORT010",
    "bcjis_objects_TB_REPORT010_H",
    "bcjis_objects_TB_REPORT020",
    "bcjis_objects_TB_REPORT020_H",
    "bcjis_objects_TB_TRACELOG",
    "bcjis_objects_TB_REPORT070_O"
)

$cubrid = Join-Path $CubridBin "cubrid.exe"
foreach ($f in $files) {
    $path = Join-Path $DataDir $f
    if (-not (Test-Path $path)) {
        Write-Warning "건너뜀 (없음): $f"
        continue
    }
    Write-Host "적재 중: $f ..."
    & $cubrid loaddb -C -u $User -p $Password --no-user-specified-name --data-file $path $DbName
    if ($LASTEXITCODE -ne 0) {
        Write-Error "loaddb 실패: $f (exit $LASTEXITCODE)"
    }
}
Write-Host "데이터 적재 완료"

# 성능 개선용 인덱스 생성 (loaddb 는 인덱스/PK 를 만들지 않으므로 적재 후 반드시 수행)
$applyIdx = Join-Path $PSScriptRoot "apply-indexes.ps1"
if (Test-Path $applyIdx) {
    Write-Host "인덱스 생성 중: apply-indexes.ps1 ..."
    & $applyIdx
    Write-Host "인덱스 생성 단계 완료"
} else {
    Write-Warning "인덱스 스크립트를 찾을 수 없습니다: $applyIdx"
}

# TB_COMM_SEQ 시드 (loaddb 후 채번 테이블이 비어 있으면 모든 ajax가 실패)
$csql = Join-Path $CubridBin "csql.exe"
$seedSql = Join-Path $PSScriptRoot "seed-comm-seq.sql"
if (Test-Path $seedSql) {
    Write-Host "TB_COMM_SEQ 시드 적재: $seedSql ..."
    & $csql -u $User -p $Password --no-single-line -i $seedSql $DbName
    Write-Host "TB_COMM_SEQ 시드 완료"
} else {
    Write-Warning "시드 스크립트를 찾을 수 없습니다: $seedSql"
}

# TB_DGRCOMPOFRSC — loaddb 패키지에 없음. 비어 있으면 조정액 기준 보완(로컬 검증용)
$bootstrapFrscPs1 = Join-Path $PSScriptRoot "bootstrap-dgrcompofrsc.ps1"
if (Test-Path $bootstrapFrscPs1) {
    Write-Host "TB_DGRCOMPOFRSC 보완: bootstrap-dgrcompofrsc.ps1 ..."
    & $bootstrapFrscPs1
}

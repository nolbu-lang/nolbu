# 로컬 PC 실행 가이드

## 1. 설치 프로그램

| 프로그램 | 상태 | 비고 |
|---------|------|------|
| JDK 8 (Temurin) | PC에 설치됨 | `java -version` 확인 |
| Maven 3.9.6 | `C:\tools\apache-maven-3.9.6` | PATH에 `bin` 추가 권장 |
| CUBRID | **직접 설치 필요** | https://www.cubrid.org/downloads |
| Apache Tomcat 9 | **직접 설치 필요** | https://tomcat.apache.org/download-90.cgi |

## 2. DB 데이터 적재

`테이블\테이블` 폴더 파일은 **CUBRID loaddb 형식** (`%class "tb_..."`) 입니다.

1. CUBRID 설치 후 DB `bcjis` 생성
2. 운영/개발 서버에서 받은 **테이블 DDL**로 스키마 생성 (이 폴더에는 데이터만 있음)
3. PowerShell에서:

```powershell
cd "C:\Users\COMTREE\Documents\심사정보시스템\08. 개발 소스코드"
.\scripts\import-cubrid-data.ps1
```

## 3. 설정 파일

```powershell
.\scripts\setup-local.ps1
```

`globals.properties`는 `localhost:33000/bcjis` 로 설정되어 있습니다. 비밀번호는 CUBRID 계정에 맞게 수정하세요.

## 4. 빌드

```powershell
.\scripts\build.ps1
```

성공 시 `target\bcjis-webapp.war` 생성.

## 5. Tomcat 실행

1. `bcjis-webapp.war`를 Tomcat `webapps`에 복사
2. Tomcat `bin\startup.bat` 실행
3. 브라우저: `http://localhost:8080/bcjis-webapp/`

## 테이블 ↔ 화면

| 파일 | 화면 |
|------|------|
| TB_REPORT010 | 경상사업심사조서 (reportCd 010) |
| TB_REPORT020 | 투자사업심사조서 (reportCd 020) |
| TB_DGRCOMPO | 예산편성 연계 데이터 |

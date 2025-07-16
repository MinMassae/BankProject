# 🏦 BankProject — 개인은행 시스템

---

## 📘 프로젝트 개요

Spring Boot 기반으로 구현한 간단한 은행 시스템
회원가입부터 로그인, 계좌 개설, 이체, OTP 인증, 배치처리, 그리고 실시간 환율 적용까지 포함된 종합 프로젝트
서버는 API 서버(포트 8080)와 VIEW 서버(포트 8081)로 분리되어 운영

---

## ⚙️ 사용 기술 스택

| 분류       | 기술 |
|------------|------|
| 개발 언어   | Java 17 |
| 프레임워크 | Spring Boot, Spring Security |
| DB 연동    | MyBatis |
| View 템플릿 | Thymeleaf |
| 인증 방식   | JWT (API), Spring Security 세션 (VIEW) |
| DB          | MySQL |
| 배치        | Spring Batch |
| 환율        | NAVER 금융 환율 크롤링/REST API 호출 |
| 기타        | OTP, BCrypt, fetch 기반 API 통신 |

---

## 🌐 서버 구성

| 서버       | 포트  | 설명 |
|------------|------|------|
| API 서버   | 8080 | REST API 제공 및 JWT 인증 처리 |
| VIEW 서버  | 8081 | Thymeleaf 뷰 렌더링 및 세션 기반 화면 UI 제공 |

---

## 🚀 주요 기능 흐름 

### 1. 회원가입 (POST `/api/members`)
- 사용자 입력: ID, 비밀번호, 이름
- 비밀번호는 BCrypt 암호화 후 저장
- 주요 컴포넌트:
  - `MemberController` → 사용자의 HTTP 요청 처리
  - `MemberService` → 회원가입 비즈니스 로직 수행
  - `MemberMapper.xml` → MyBatis SQL 매핑
  - `Member` 엔티티 테이블에 저장

### 2. 로그인 (POST `/api/auth/login`)
- ID/비밀번호 인증 → JWT 토큰 생성 (`JwtUtil`)
- 클라이언트는 JWT를 `localStorage`에 저장
- 이후 요청 시 `Authorization: Bearer <token>` 헤더 포함
- 토큰 검증은 `JwtAuthenticationFilter`에서 수행

### 3. 실시간 환율 조회
- 로그인 후 계좌 조회 시, 잔액 외에 통화별 잔액 환율 기준 금액 표시
- NAVER 환율 API/크롤링을 통해 실시간 달러・엔 환율 데이터 획득
- 주요 컴포넌트:
  - `ExchangeRateService` (HTTP GET → JSON 파싱)
  - `ExchangeRateController` → API로 환율 반환
  - Thymeleaf 뷰에서 `fetch('/api/exchange-rate')` 호출 후 화면에 표시
  - 한국수출입은행 API를 이용하여 최근 환율과 그래프를 작성하였으나, API문제가 있어 자주 에러가 발생하여 NAVER로 변경

### 4. 계좌 개설 (POST `/api/accounts`)
- 로그인 상태에서 계좌 신청 가능
- 필수 입력: 직업, 사용 목적, PIN 입력
- 시스템에서 고유 계좌번호 생성 후 DB 저장(110- 으로 시작하는)
- 주요 컴포넌트:
  - `AccountController`, `AccountService`, `AccountMapper.xml`
  - `account` 테이블 저장

### 5. 계좌 조회 (GET `/api/me`)
- JWT 검증 완료 시 사용자 정보 + 계좌 목록 반환
- 뷰 측에서 계좌가 없으면 “계좌 없음” 표시, 있으면 리스트 렌더링

### 6. 이체 준비 (VIEW: `/transfer`)
- 뷰에서 입금 계좌 입력, 이체 금액 및 PIN 입력
- 이체 조건 불만족 시(OTP 인증이 안되어 있으면) OTP 인증을 위해 main페이지로 돌아감

### 7. OTP 인증 (POST `/api/otp/verify`)
- OTP 입력값 + 서버 저장된 OTP 일치 여부 비교
- 성공 시 JWT 기반 또는 세션 기반으로 OTP 인증 성공 상태 세션 설정

### 8. 계좌 이체 (POST `/api/transfer`)
- OTP 인증 확인 → `TransferService`에서 비즈니스 로직 실행:
  1. 출금 계좌 PIN 검증
  2. 잔액 확인 (잔여 > 이체 금액)
  3. `transfer_history` 테이블에 이체 내역 저장
  4. `account` 테이블 잔액 업데이트

### 9. 이체 내역 조회 (GET `/api/transfer/history`)
- 출금/입금 둘 다 반영된 `transfer_history`의 `findByAccountNo` SQL 호출
- 뷰에서는 시간순 정렬하여 리스트 출력

### 10. OTP 배치 처리 (Scheduled)
- OTP 번호는 사용자가 인증 요청 시 OTP번호를 보여줌.
- OTP 번호는 데이터베이스 안에서 10초마다 갱신됨
- 보안 강화를 위해 로그아웃 시 인증 된 OTP는 삭제 됨.

#### ⛏ 동작 흐름
1. OTP 번호 6자리 난수 생성 -> 10초마다 다른 번호로 갱신
2. `OtpService`에서 서버 메모리 상의 Map에 저장 (사용자 기준)
3. 인증 시도 시 해당 Map에서 일치 여부 확인

#### 📁 관련 클래스
- `OtpService.java`: OTP 생성 및 검증
- `OtpController.java`: OTP API 엔드포인트 처리
- `@Scheduled`가 포함된 OTP 만료 처리 메서드

---

## 🛡️ 인증 및 보안

- 로그인 시 JWT 발급 → 이후 모든 API 요청에 헤더 기반 인증
- JWT 유효성 및 복호화는 `JwtAuthenticationFilter`에서 수행 → `SecurityContextHolder`에 Authentication 저장
- VIEW 서버는 Spring Security 세션 기반 필터 적용
- 민감 요청(이체/OTP)은 PIN 인증 + OTP 인증을 거쳐야 처리

---

## 🗂️ DB 스키마 요약

| 테이블              | 설명 |
|---------------------|------|
| `members`           | 회원 ID, 암호, 닉네임, 주소, PIN 저장 |
| `account`           | 계좌번호, 잔액, 직업, 목적, PIN |
| `transfer_history`  | 송금/수금 이체 내역 |
| `interest_history`  | 이자 계산 결과 기록 |
| `report_history`    | 분석/리포트 결과 기록 |
| `account_history`   | 입출금 거래내역 |

---

## 🎯 실행 방법 요약

```bash
# 1. DB 설정
# 2. API 서버 (8080) 실행
./gradlew build
java -jar build/libs/bank-api.jar

# 3. VIEW 서버 (8081) 실행
java -jar build/libs/bank-view.jar

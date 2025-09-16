# 🏄 SURF 프로젝트 코드 컨벤션 가이드

이 문서는 SURF 프로젝트의 일관된 코드 품질과 효율적인 협업을 위한 컨벤션을 정의합니다.

## 1. 개발 컨벤션 (Development Convention)

### 1.1 PR(Pull Request) 컨벤션
PR 제목은 PR의 목적을 명확히 드러내야 합니다.
* **형식:** `[커밋 타입] 작업 내용`
* **예시:** `[FEAT] 카카오 소셜 로그인 구현`
* **주요 커밋 타입:**
    * `FEAT`: 새로운 기능 추가
    * `FIX`: 버그 수정
    * `CHORE`: 빌드 스크립트, 패키지 관리자 등 기타 변경 사항
    * `DOCS`: 문서 수정
    * `STYLE`: 코드 포맷팅, 세미콜론 누락 등 (기능 변경 없음)
    * `REFACT`: 코드 리팩토링
    * `TEST`: 테스트 코드 추가/수정

### 1.2 코드 계층 구조
애플리케이션의 계층을 명확히 구분하여 응집도를 높이고 의존성을 낮춥니다.
* **Controller → Facade → Service → Repository** 순으로 구현합니다.
* **Controller:** HTTP 요청과 응답을 처리하는 역할만 담당합니다.
* **Facade:** 여러 Service의 로직을 조합하여 복잡한 비즈니스 시나리오를 처리합니다.
* **Service:** 단일 책임 원칙(SRP)을 준수하여 특정 도메인 로직을 수행합니다. 기능을 최대한 분리하여 작성합니다.
* **Repository:** 데이터베이스 접근을 전담하며, 데이터 영속성(Persistence) 관련 작업을 처리합니다.

### 1.3 커스텀 예외 처리
일관된 예외 처리를 위해 아래 규칙을 따릅니다.
* **커스텀 예외 네이밍:** `[도메인] + [상태] + Exception` 형식으로 명명합니다.
    * **예시:** `MemberNotFoundException`, `ProductStockoutException`
* **예외 설명 메시지:** 사용자가 이해하기 쉬운 메시지를 제공합니다.
    * **형식:** "존재하지 않는 [도메인명]입니다."
    * **예시:** `new MemberNotFoundException("존재하지 않는 [멤버]입니다.")`

### 1.4 패키지 구조
패키지는 도메인 중심으로 구성하며, 공통 로직은 별도의 패키지에서 관리합니다.
* `domain` 패키지와 `global` 패키지를 같은 레벨에 둡니다.
    * **`domain`**: 각 도메인(예: `member`, `product`)별로 관련된 클래스(Controller, Service, Repository 등)를 관리합니다.
    * **`global`**: 프로젝트 전반에 걸쳐 사용되는 공통 클래스(예: `ExceptionHandler`, `JWTUtil`)를 관리합니다.

---

## 2. 문서화 및 기타 (Documentation and Others)

### 2.1 API 문서화
* **Swagger:** API의 핵심 정보(엔드포인트, 요청/응답 형식)만 간결하게 작성합니다.
* **Notion:** Swagger에 담기 어려운 상세한 API 명세, 설계 의도 등을 포함하여 팀원들과 공유합니다.
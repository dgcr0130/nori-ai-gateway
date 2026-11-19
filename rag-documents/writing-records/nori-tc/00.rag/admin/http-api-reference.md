# HTTP API 작성 기록

## 대상
- objectKey: `00.rag/admin/http-api-reference.md`
- 구획: admin `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 참조
- 검색 원문 SHA-256: `40ba6fb6b84e98bfc1cd770a6182ae000fe3597fc9110e00ab231af0b0638c1c`
- 줄 수: 788
- H2 절 수: 59
- 확인일: 2026-11-19 (한국 시간)
- 역할: 독립 작성 세션에서 직접 조사·작성·자체검사했다. 위임·제품 시험·서버 접속·업로드·Git 변경 명령은 실행하지 않았다.

## 원천 판본
- nori-tc-ui-backend 최초 작성 판본: `985d17c604dd`. 본문에 인용한 45개 파일의 `git status --short -- <정확한 경로들>` 출력은 비어 있었고 종료코드는 0이었다.
- 제품 수정 D의 현재 작업 트리 원천(2026-11-19): JpaModelDetailCommandPort.java는 수정 상태이며 SHA-256 `aa567bd80d4277dc0839572ed14fb63a0337e2a50ea415074448045488c41d74`, PlcRegisterOrder.java는 새 파일이며 SHA-256 `241e8e34c68b25713a47362cabe3e7574400445f71e33dc4ad1aa7c47d716d6a`이다. 정확한 경로는 해당 절의 사실 대조 표에 기록했다. 이 두 파일은 본문의 commit 원천을 덮지 않고 SHA 원천으로 추가했다.
- ModelDetailController.java·UiApiExceptionHandler.java는 현재 파일을 다시 읽고 해당 경로의 git status 출력이 비어 있음을 확인했다. 이들의 기존 `985d17c604dd` 원천은 유지했다.
- nori-tc-simulator: `5e9b02e3ae16`. 본문에 인용한 16개 파일의 `git status --short -- <정확한 경로들>` 출력은 비어 있었고 종료코드는 0이었다.
- nori-tc-exec: `f1d5e0a8e727`. I=`docs/completion-report/04-interface-spec.md` §3 L108~128·§4.6 L190~305·§4.7 L307~325를 읽고 코드와 대조했다. 해당 경로의 작업 트리 변경은 없었다.
- nori-ai-gateway: `70cf6cd84db6`. 작성 규칙·문서 목록 전체를 읽었다. 두 경로의 작업 트리 변경은 없었다.
- 읽기 전용 Git 조회는 저장소별 정확한 `-c safe.directory=<저장소 절대경로>`를 현재 명령에만 지정했다. 전역 설정을 변경하지 않았다. Git의 사용자 ignore 파일 접근 경고는 있었으며, 각 조회의 종료코드·표준 출력을 별도로 확인했다.
- 기존 작업 상태: main, origin/main보다 8개 커밋 앞섬, rag-documents/ 미추적. worktree는 현재 제품 저장소 하나였다. 기존 문서는 읽기만 했다.

## 절별 사실 대조
본문의 각 문장과 표 자료 행을 아래에 그대로 식별했다. API 행은 해당 절의 실제 컨트롤러에서 합성한 메서드·경로와 대조하고, 요청 바인딩·반환 타입·반환문을 읽었다. 소유 문서 안내는 확정 문서 목록의 해당 objectKey 행과 대조했다. 타입명만 표기한 곳은 DTO 전체 필드 계약을 제공한다는 뜻이 아니다.

### 1. HTTP-UI와 HTTP-SIM의 호출 경계
- 절 대조: LiveMapController와 HttpSimulatorOrchestratorClient의 실제 호출 경로·JSON 직렬화 경계 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L15 | HTTP-UI는 브라우저가 ui-backend에 보내는 `/api` 요청이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~122 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L46~139 | LiveMapController와 HttpSimulatorOrchestratorClient의 실제 호출 경로·JSON 직렬화 경계 확인 |
| L15 | HTTP-SIM은 ui-backend가 Simulator orchestrator를 부르거나 Simulator 내부 서버끼리 주고받는 요청이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~122 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L46~139 | LiveMapController와 HttpSimulatorOrchestratorClient의 실제 호출 경로·JSON 직렬화 경계 확인 |
| L17 | 데모의 브라우저 경로는 `/api/live-map/demo-runs`, orchestrator 경로는 `/api/demo-runs`다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~122 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L46~139 | LiveMapController와 HttpSimulatorOrchestratorClient의 실제 호출 경로·JSON 직렬화 경계 확인 |
| L17 | 아래 경로는 서버 기준 상대 경로다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~122 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L46~139 | LiveMapController와 HttpSimulatorOrchestratorClient의 실제 호출 경로·JSON 직렬화 경계 확인 |
| L17 | 표의 입력은 요청 본문 타입이며, 조회 조건·헤더·파일은 별도로 표시한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~122 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L46~139 | LiveMapController와 HttpSimulatorOrchestratorClient의 실제 호출 경로·JSON 직렬화 경계 확인 |

### 2. HTTP-UI 성공·실패 본문
- 절 대조: ApiResponse record 네 필드 및 success/error 생성자의 true/false·null 배치 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L24 | HTTP-UI의 `ApiResponse`는 다음 네 필드를 갖는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/ApiResponse.java @ 985d17c604dd ; ApiResponse L16~48 | ApiResponse record 네 필드 및 success/error 생성자의 true/false·null 배치 확인 |
| L24 | 아래 HTTP-UI 표의 결과 타입은 `data` 안에 들어간다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/ApiResponse.java @ 985d17c604dd ; ApiResponse L16~48 | ApiResponse record 네 필드 및 success/error 생성자의 true/false·null 배치 확인 |
| L28 | \| success \| true \| false \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/ApiResponse.java @ 985d17c604dd ; ApiResponse L16~48 | ApiResponse record 네 필드 및 success/error 생성자의 true/false·null 배치 확인 |
| L29 | \| data \| 결과 또는 null \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/ApiResponse.java @ 985d17c604dd ; ApiResponse L16~48 | ApiResponse record 네 필드 및 success/error 생성자의 true/false·null 배치 확인 |
| L30 | \| errorCode \| null \| 오류 코드 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/ApiResponse.java @ 985d17c604dd ; ApiResponse L16~48 | ApiResponse record 네 필드 및 success/error 생성자의 true/false·null 배치 확인 |
| L31 | \| errorMsg \| null \| 오류 문구 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/ApiResponse.java @ 985d17c604dd ; ApiResponse L16~48 | ApiResponse record 네 필드 및 success/error 생성자의 true/false·null 배치 확인 |

### 3. HTTP-UI 오류 처리와 본문 없는 예외
- 절 대조: 예외 처리기의 상태 변환·인증 진입점·AsyncResultController의 notFound().build() 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L41 | 입력·본문 검증 실패는 400, 인증 진입점의 거부는 401, 업무 권한 거부 예외는 403, 대상 부재 예외는 404, 충돌 예외는 409, 조정 저장소·카탈로그 예외는 503, 미분류 예외는 500으로 공통 본문을 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L89~216 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L231~332 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; UiAuthenticationEntryPoint L68~81 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; AsyncResultController L85~107 | 예외 처리기의 상태 변환·인증 진입점·AsyncResultController의 notFound().build() 확인 |
| L43 | `GET /api/async/{id}`에서 작업이 없으면 본문 없는 404다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L89~216 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L231~332 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; UiAuthenticationEntryPoint L68~81 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; AsyncResultController L85~107 | 예외 처리기의 상태 변환·인증 진입점·AsyncResultController의 notFound().build() 확인 |
| L43 | 오류 코드별 뜻과 사용자 조치는 `00.rag/user/error-message-and-code-reference.md`가 소유한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L89~216 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L231~332 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; UiAuthenticationEntryPoint L68~81 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; AsyncResultController L85~107 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L66 | 예외 처리기의 상태 변환·인증 진입점·AsyncResultController의 notFound().build() 확인 |

### 4. 인증 쿠키와 CSRF 요청 계약
- 절 대조: 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L54 | HTTP-UI는 설정된 인증 쿠키에서 인증값을 읽는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276 | 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인 |
| L54 | 기본 쿠키 식별자는 `TC_UI_AUTH`다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276 | 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인 |
| L54 | CSRF 발급 경로는 `GET /api/auth/csrf`이며 성공하면 data는 null이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276 | 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인 |
| L56 | CSRF는 활성화되어 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276 | 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인 |
| L56 | 쓰기 요청은 기본 `XSRF-TOKEN` 쿠키의 원문 값을 `X-XSRF-TOKEN` 헤더로 보낸다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276 | 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인 |
| L56 | 쿠키·헤더 식별자는 설정으로 바뀔 수 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276 | 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인 |
| L56 | 로그인 POST도 CSRF 검사 예외로 등록하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276 | 쿠키 추출·CSRF 저장소·raw 토큰 핸들러·기본 식별자·csrf 발급 메서드 확인 |

### 5. 공개 경로와 보호 API의 권한 검사
- 절 대조: 공개 매처와 isAuthorized의 초기화·카탈로그·매칭·anyMatch 조건 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L64 | 공개 경로에는 `POST /api/auth/login`, `GET /api/auth/csrf`, GET의 `/api/actuator/health`와 하위 경로가 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L131~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; UiApiPermissionCache L142~185 | 공개 매처와 isAuthorized의 초기화·카탈로그·매칭·anyMatch 조건 확인 |
| L64 | OPTIONS 요청도 허용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L131~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; UiApiPermissionCache L142~185 | 공개 매처와 isAuthorized의 초기화·카탈로그·매칭·anyMatch 조건 확인 |
| L66 | 일반 보호 API는 인증 뒤 활성 API 권한 목록에서 URI·HTTP 메서드를 대조한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L131~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; UiApiPermissionCache L142~185 | 공개 매처와 isAuthorized의 초기화·카탈로그·매칭·anyMatch 조건 확인 |
| L66 | 매칭 권한 중 하나를 보유해야 통과한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L131~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; UiApiPermissionCache L142~185 | 공개 매처와 isAuthorized의 초기화·카탈로그·매칭·anyMatch 조건 확인 |
| L66 | 경로 미등록·권한 초기화 실패·권한 카탈로그 불일치도 차단한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L131~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; UiApiPermissionCache L142~185 | 공개 매처와 isAuthorized의 초기화·카탈로그·매칭·anyMatch 조건 확인 |
| L66 | 경로별 권한 코드 표는 `00.rag/user/permission-code-reference.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L131~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; UiApiPermissionCache L142~185 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L72 | 공개 매처와 isAuthorized의 초기화·카탈로그·매칭·anyMatch 조건 확인 |

### 6. 쿠키를 보내는 교차 출처 요청
- 절 대조: CORS 허용 출처·메서드·헤더·credentials 설정 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L73 | HTTP-UI의 CORS는 설정된 허용 Origin 목록을 사용하며 `allowCredentials=true`다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L246~260 | CORS 허용 출처·메서드·헤더·credentials 설정 확인 |
| L73 | 허용 메서드는 GET·POST·PUT·PATCH·DELETE·OPTIONS다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L246~260 | CORS 허용 출처·메서드·헤더·credentials 설정 확인 |
| L73 | 요청 헤더는 전체 허용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L246~260 | CORS 허용 출처·메서드·헤더·credentials 설정 확인 |
| L73 | 허용 Origin 목록이 비면 교차 출처 허용 목록도 비게 된다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L246~260 | CORS 허용 출처·메서드·헤더·credentials 설정 확인 |

### 7. 로그인·로그아웃과 현재 사용자 API
- 절 대조: AuthController의 자격 전달·Set-Cookie·로그아웃 만료·사용자 반환 및 LoginRequest 검증 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L82 | 아래 성공 응답은 HTTP 200이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L96~138 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L152~240 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LoginRequest.java @ 985d17c604dd ; LoginRequest L11~18 | AuthController의 자격 전달·Set-Cookie·로그아웃 만료·사용자 반환 및 LoginRequest 검증 확인 |
| L82 | 로그인 입력은 빈 값이 아닌 userId·password를 받는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L96~138 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L152~240 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LoginRequest.java @ 985d17c604dd ; LoginRequest L11~18 | AuthController의 자격 전달·Set-Cookie·로그아웃 만료·사용자 반환 및 LoginRequest 검증 확인 |
| L86 | \| POST /api/auth/login \| LoginRequest \| LoginResponse와 인증 쿠키 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L96~140 | public ResponseEntity<ApiResponse<LoginResponse>> login( @Valid @RequestBody final LoginRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L87 | \| POST /api/auth/logout \| 본문 없음 \| null과 쿠키 만료 응답 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L152~202 | public ResponseEntity<ApiResponse<Void>> logout()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L88 | \| GET /api/auth/me \| 본문 없음 \| MeResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L212~241 | public ResponseEntity<ApiResponse<MeResponse>> me()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L90 | 화면 사용 절차는 `00.rag/user/login-and-screen-basics.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L96~138 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L152~240 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LoginRequest.java @ 985d17c604dd ; LoginRequest L11~18 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L24 | AuthController의 자격 전달·Set-Cookie·로그아웃 만료·사용자 반환 및 LoginRequest 검증 확인 |

### 8. 인증 API가 직접 반환하는 401
- 절 대조: 인증 실패 catch·인증 상태 검사·삭제 쿠키 헬퍼가 401 ApiResponse.error를 반환한다.
- 사람 검사: 제목으로 오류 대상을 식별함 / 지정 커밋과 일치 / 조건·HTTP 상태·코드가 절 안에 있음 / 기존 성공 응답 표와 오류 코드 뜻풀이를 복제하지 않음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L97 | 아래 분기는 컨트롤러가 401 UNAUTHORIZED를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L133~138 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L156~176 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L214~228 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L288~292 | 인증 실패 catch·인증 상태 검사·삭제 쿠키 헬퍼가 401 ApiResponse.error를 반환한다. |
| L101 | \| POST /api/auth/login \| UiAuthenticationException으로 인증이 실패했다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L133~138 | 인증 실패 catch·인증 상태 검사·삭제 쿠키 헬퍼가 401 ApiResponse.error를 반환한다. |
| L102 | \| POST /api/auth/logout \| 인증 정보가 없거나 credentials가 비정상·공백이거나 principal 타입이 다르다. 삭제 쿠키도 함께 보낸다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L156~176 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L288~292 | 인증 실패 catch·인증 상태 검사·삭제 쿠키 헬퍼가 401 ApiResponse.error를 반환한다. |
| L103 | \| GET /api/auth/me \| 인증 정보가 없거나 principal 타입이 다르다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L214~228 | 인증 실패 catch·인증 상태 검사·삭제 쿠키 헬퍼가 401 ApiResponse.error를 반환한다. |

### 9. HTTP-UI 목록의 offset·limit 계약
- 절 대조: 공통 페이지 해석기의 기본·경계와 PagedResponse count 설명·호출 소비자 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L116 | `PagedResponse`를 쓰는 설비·모델 목록의 조회 조건은 offset·limit다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiPageRequestSupport.java @ 985d17c604dd ; UiPageRequestSupport L27~73 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/model/PagedResponse.java @ 985d17c604dd ; PagedResponse L28~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~112 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L46~53 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L73~91 L223~244 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L71~89 L182~203 L254~275 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; PermissionController L63~81 | 공통 페이지 해석기의 기본·경계와 PagedResponse count 설명·호출 소비자 확인 |
| L116 | 같은 공통 해석기를 쓰는 사용자·그룹·권한·소속 목록에도 적용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiPageRequestSupport.java @ 985d17c604dd ; UiPageRequestSupport L27~73 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/model/PagedResponse.java @ 985d17c604dd ; PagedResponse L28~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~112 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L46~53 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L73~91 L223~244 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L71~89 L182~203 L254~275 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; PermissionController L63~81 | 공통 페이지 해석기의 기본·경계와 PagedResponse count 설명·호출 소비자 확인 |
| L120 | \| offset \| 기본 0, 0 이상인 시작 위치 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiPageRequestSupport.java @ 985d17c604dd ; UiPageRequestSupport L27~73 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/model/PagedResponse.java @ 985d17c604dd ; PagedResponse L28~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~112 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L46~53 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L73~91 L223~244 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L71~89 L182~203 L254~275 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; PermissionController L63~81 | 공통 페이지 해석기의 기본·경계와 PagedResponse count 설명·호출 소비자 확인 |
| L121 | \| limit \| 기본 100, 1~500인 조회 건수 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiPageRequestSupport.java @ 985d17c604dd ; UiPageRequestSupport L27~73 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/model/PagedResponse.java @ 985d17c604dd ; PagedResponse L28~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~112 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L46~53 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L73~91 L223~244 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L71~89 L182~203 L254~275 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; PermissionController L63~81 | 공통 페이지 해석기의 기본·경계와 PagedResponse count 설명·호출 소비자 확인 |
| L122 | \| PagedResponse \| items·offset·limit·count를 반환하며 count는 전체 건수다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiPageRequestSupport.java @ 985d17c604dd ; UiPageRequestSupport L27~73 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/model/PagedResponse.java @ 985d17c604dd ; PagedResponse L28~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~112 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L46~53 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L73~91 L223~244 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L71~89 L182~203 L254~275 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; PermissionController L63~81 | 공통 페이지 해석기의 기본·경계와 PagedResponse count 설명·호출 소비자 확인 |

### 10. 설비 목록·기본·관리 상세 조회
- 절 대조: 설비 목록·기본·관리 조회의 바인딩과 서비스/포트 호출·반환 타입 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L129 | HTTP-UI 설비 조회의 성공은 200이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~150 | 설비 목록·기본·관리 조회의 바인딩과 서비스/포트 호출·반환 타입 확인 |
| L133 | \| GET /api/eqp \| 선택 offset·limit \| PagedResponse의 EqpInfoResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L98~113 | public ResponseEntity<ApiResponse<PagedResponse<EqpInfoResponse>>> list( @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L134 | \| GET /api/eqp/{eqpId} \| 경로 eqpId \| EqpInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L121~132 | public ResponseEntity<ApiResponse<EqpInfoResponse>> get( @PathVariable final String eqpId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L135 | \| GET /api/eqp/{eqpId}/manage \| 경로 eqpId \| EqpManageDetailResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L140~151 | public ResponseEntity<ApiResponse<EqpManageDetailResponse>> getManageDetail( @PathVariable final String eqpId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L137 | `GET /api/eqp/{eqpId}`·`GET /api/eqp/{eqpId}/manage`는 설비가 없으면 컨트롤러가 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L125~128 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L144~147 | 단건·관리 상세 포트 조회의 Optional 부재 분기에서 404 NOT_FOUND를 직접 반환한다. |

### 11. 여러 설비의 관리 상세와 선택지 조회
- 절 대조: eqpIds 쉼표 분리·trim·빈 값 제거·LinkedHashSet 중복 제거·200 한도 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L146 | \| GET /api/eqp/manage-batch \| 필수 eqpIds \| EqpManageDetailBatchResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L167~196 | public ResponseEntity<ApiResponse<EqpManageDetailBatchResponse>> getManageDetailBatch( @RequestParam(name = "eqpIds") final String eqpIds )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L147 | \| GET /api/eqp/options \| 없음 \| EqpManageOptionsResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L203~207 | public ResponseEntity<ApiResponse<EqpManageOptionsResponse>> getOptions()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L149 | manage-batch의 eqpIds는 쉼표 구분 문자열이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L165~206 | eqpIds 쉼표 분리·trim·빈 값 제거·LinkedHashSet 중복 제거·200 한도 확인 |
| L149 | 앞뒤 공백·빈 항목·중복을 제거한 뒤 1~200개를 허용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L165~206 | eqpIds 쉼표 분리·trim·빈 값 제거·LinkedHashSet 중복 제거·200 한도 확인 |

### 12. 설비 파라미터 버전과 런타임 조회
- 절 대조: 버전 목록·런타임 조회의 Optional 부재 404와 반환 데이터 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L158 | \| GET /api/eqp/{eqpId}/param-versions \| 경로 eqpId \| 문자열 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L215~226 | public ResponseEntity<ApiResponse<List<String>>> getParamVersions( @PathVariable final String eqpId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L159 | \| GET /api/eqp/{eqpId}/runtime-state \| 경로 eqpId \| EqpRuntimeStateResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L234~251 | public ResponseEntity<ApiResponse<EqpRuntimeStateResponse>> getRuntimeState( @PathVariable final String eqpId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L161 | 두 조회 모두 대상 설비가 없으면 404를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L215~250 | 버전 목록·런타임 조회의 Optional 부재 404와 반환 데이터 확인 |

### 13. 설비 등록·수정·삭제의 접수 계약
- 절 대조: 세 변경 경로의 선택 헤더·기본 생성과 OperationAcceptResponse 조립·202 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L171 | \| POST /api/eqp \| EqpCreateRequest \| OperationAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L260~270 | public ResponseEntity<ApiResponse<OperationAcceptResponse>> create( @Valid @RequestBody final EqpCreateRequest request, @RequestHeader(name = IDEMPOTENCY_KEY_HEADER, required = false) final String idempotencyKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L172 | \| PUT /api/eqp/{eqpId} \| EqpUpdateRequest \| OperationAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L280~297 | public ResponseEntity<ApiResponse<OperationAcceptResponse>> update( @PathVariable final String eqpId, @Valid @RequestBody final EqpUpdateRequest request, @RequestHeader(name = IDEMPOTENCY_KEY_HEADER, required = false) final String idempotencyKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L173 | \| DELETE /api/eqp/{eqpId} \| 없음 \| OperationAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L305~313 | public ResponseEntity<ApiResponse<OperationAcceptResponse>> delete( @PathVariable final String eqpId, @RequestHeader(name = IDEMPOTENCY_KEY_HEADER, required = false) final String idempotencyKey )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L175 | 세 요청은 선택 헤더 `Idempotency-Key`를 받으며 없거나 공백이면 서버가 새 값을 만든다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L260~312 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L457~490 | 세 변경 경로의 선택 헤더·기본 생성과 OperationAcceptResponse 조립·202 확인 |
| L175 | 접수 결과에는 operationId·state·phase·errorCode·errorMsg가 들어간다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L260~312 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L457~490 | 세 변경 경로의 선택 헤더·기본 생성과 OperationAcceptResponse 조립·202 확인 |
| L177 | `PUT /api/eqp/{eqpId}`는 설비가 없으면 202 접수 전에 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L288~296 | 수정 대상 조회가 비면 acceptUpdate 호출 전에 404 NOT_FOUND를 반환한다. |

### 14. 설비 통신 명령의 요청과 응답
- 절 대조: 통신 명령의 interfaceType·uiMessage 전달 및 accepted 여부별 HTTP 응답 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L188 | \| POST /api/eqp/{eqpId}/start \| EqpLifecycleRequest \| 202 AsyncAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L322~339 | public ResponseEntity<ApiResponse<AsyncAcceptResponse>> start( @PathVariable final String eqpId, @Valid @RequestBody final EqpLifecycleRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L189 | \| POST /api/eqp/{eqpId}/end \| EqpLifecycleRequest \| 202 AsyncAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L348~365 | public ResponseEntity<ApiResponse<AsyncAcceptResponse>> end( @PathVariable final String eqpId, @Valid @RequestBody final EqpLifecycleRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L190 | \| POST /api/eqp/{eqpId}/reconnect \| EqpLifecycleRequest \| 202 AsyncAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L377~394 | public ResponseEntity<ApiResponse<AsyncAcceptResponse>> reconnect( @PathVariable final String eqpId, @Valid @RequestBody final EqpLifecycleRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L192 | 본문은 빈 값이 아닌 interfaceType과 선택 uiMessage를 받는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L322~392 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L493~499 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpLifecycleRequest.java @ 985d17c604dd ; EqpLifecycleRequest L18~24 | 통신 명령의 interfaceType·uiMessage 전달 및 accepted 여부별 HTTP 응답 확인 |
| L192 | 접수 거부는 서비스가 정한 HTTP 상태와 실패 본문을 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L322~392 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L493~499 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpLifecycleRequest.java @ 985d17c604dd ; EqpLifecycleRequest L18~24 | 통신 명령의 interfaceType·uiMessage 전달 및 accepted 여부별 HTTP 응답 확인 |

### 15. PLC 폴링 그룹의 제어 경로
- 절 대조: 폴링 그룹의 경로 변수 전달·별도 본문 부재·202 접수 응답 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L202 | \| POST /api/eqp/{eqpId}/polling-group/{groupMessageName}/start \| 경로 eqpId·groupMessageName \| 202 AsyncAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L405~421 | public ResponseEntity<ApiResponse<AsyncAcceptResponse>> startPollingGroup( @PathVariable final String eqpId, @PathVariable final String groupMessageName )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L203 | \| POST /api/eqp/{eqpId}/polling-group/{groupMessageName}/end \| 경로 eqpId·groupMessageName \| 202 AsyncAcceptResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L430~446 | public ResponseEntity<ApiResponse<AsyncAcceptResponse>> endPollingGroup( @PathVariable final String eqpId, @PathVariable final String groupMessageName )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L205 | 두 요청에는 별도 요청 본문이 없다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L405~444 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L493~499 | 폴링 그룹의 경로 변수 전달·별도 본문 부재·202 접수 응답 확인 |
| L205 | 화면 조작은 `00.rag/user/equipment-commands-and-polling.md`가 다룬다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L405~444 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L493~499 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L28 | 폴링 그룹의 경로 변수 전달·별도 본문 부재·202 접수 응답 확인 |

### 16. 비동기 조회와 조정 관리 조회
- 절 대조: 비동기 조회의 terminal 분기·조정 조회 서비스 공급자 부재 503 및 관리 응답 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L215 | \| GET /api/async/{id} \| 경로 id \| OperationStatusResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd L69~108 | public ResponseEntity<ApiResponse<OperationStatusResponse>> getResult(@PathVariable final String id)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L216 | \| GET /api/coordination/operations/{operationId} \| 경로 operationId \| 200 OperationAdminResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd L76~95 | public ResponseEntity<ApiResponse<OperationAdminResponse>> findOperation( @PathVariable final String operationId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L218 | 비동기 조회는 진행 중 202, 종결 시 200을 쓴다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; AsyncResultController L69~107 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; CoordinationAdminController L76~94 | 비동기 조회의 terminal 분기·조정 조회 서비스 공급자 부재 503 및 관리 응답 확인 |
| L218 | 두 조회 모두 조정 조회 서비스가 없는 구성에서는 503 COORDINATION_DISABLED를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd L73~82 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd L80~92 | 조회 서비스 부재는 503 COORDINATION_DISABLED이며 관리 대상 Optional 부재는 404 OPERATION_NOT_FOUND다. |
| L218 | 운영상 결과 해석은 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; AsyncResultController L69~107 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; CoordinationAdminController L76~94 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L47 | 비동기 조회의 terminal 분기·조정 조회 서비스 공급자 부재 503 및 관리 응답 확인 |
| L220 | 조정 관리 조회에서 작업이 없으면 컨트롤러가 404 OPERATION_NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd L73~82 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd L80~92 | 조회 서비스 부재는 503 COORDINATION_DISABLED이며 관리 대상 Optional 부재는 404 OPERATION_NOT_FOUND다. |

### 17. 설비 파라미터의 조회·체크아웃
- 절 대조: 체크아웃 상태·version 조회 조건·sourceVersion 전달·반환 배열 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L230 | \| GET /api/eqp/{eqpId}/checkout-status \| 경로 eqpId \| EqpCheckoutStatusResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L83~102 | public ResponseEntity<ApiResponse<EqpCheckoutStatusResponse>> getCheckoutStatus( @PathVariable final String eqpId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L231 | \| GET /api/eqp/{eqpId}/params \| 필수 조회 조건 version \| EqpParamResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L111~126 | public ResponseEntity<ApiResponse<List<EqpParamResponse>>> getParams( @PathVariable final String eqpId, @RequestParam(name = "version") final String version )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L232 | \| POST /api/eqp/{eqpId}/checkout \| EqpCheckoutRequest의 sourceVersion \| EqpParamResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L139~164 | public ResponseEntity<ApiResponse<List<EqpParamResponse>>> checkout( @PathVariable final String eqpId, @Valid @RequestBody final EqpCheckoutRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L234 | checkout-status 조회에서 설비가 없으면 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L91~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L159~162 | 체크아웃 상태의 설비 부재는 404 NOT_FOUND, checkout catch는 409 ALREADY_CHECKED_OUT을 직접 반환한다. |
| L234 | checkout 요청의 EqpAlreadyCheckedOutException은 컨트롤러가 409 ALREADY_CHECKED_OUT으로 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L91~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L159~162 | 체크아웃 상태의 설비 부재는 404 NOT_FOUND, checkout catch는 409 ALREADY_CHECKED_OUT을 직접 반환한다. |

### 18. 설비 파라미터 저장·체크인·취소
- 절 대조: 파라미터 저장 항목 변환·description 전달·취소 호출·null 결과 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L245 | \| PUT /api/eqp/{eqpId}/edit-params \| EqpParamSaveRequest의 params \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L177~194 | public ResponseEntity<ApiResponse<Void>> saveEditParams( @PathVariable final String eqpId, @Valid @RequestBody final EqpParamSaveRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L246 | \| POST /api/eqp/{eqpId}/checkin \| EqpCheckinRequest의 description \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L229~242 | public ResponseEntity<ApiResponse<Void>> checkin( @PathVariable final String eqpId, @Valid @RequestBody final EqpCheckinRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L247 | \| DELETE /api/eqp/{eqpId}/checkout \| 본문 없음 \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L205~217 | public ResponseEntity<ApiResponse<Void>> undoCheckout( @PathVariable final String eqpId, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L249 | params는 null을 허용하지 않으며 항목은 paramName·paramValue·description을 갖는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; EqpParamController L177~241 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpParamSaveRequest.java @ 985d17c604dd ; EqpParamSaveRequest L14~36 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCheckinRequest.java @ 985d17c604dd ; EqpCheckinRequest L8~11 | 파라미터 저장 항목 변환·description 전달·취소 호출·null 결과 확인 |
| L249 | 편집 절차는 `00.rag/user/equipment-model-parameter-and-jar-update.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; EqpParamController L177~241 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpParamSaveRequest.java @ 985d17c604dd ; EqpParamSaveRequest L14~36 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCheckinRequest.java @ 985d17c604dd ; EqpCheckinRequest L8~11 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L29 | 파라미터 저장 항목 변환·description 전달·취소 호출·null 결과 확인 |

### 19. Gateway·Business 인스턴스 조회
- 절 대조: 인스턴스 조회 포트와 응답 생성자의 필드 순서 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L258 | \| GET /api/eqp/gateway-instances \| 없음 \| RuntimeInstanceResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd L42~46 | public ResponseEntity<ApiResponse<List<RuntimeInstanceResponse>>> gatewayInstances()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L259 | \| GET /api/eqp/business-instances \| 없음 \| RuntimeInstanceResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd L56~60 | public ResponseEntity<ApiResponse<List<RuntimeInstanceResponse>>> businessInstances()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L261 | 응답 타입에는 sessionId·podName·podIp·state·protocolVersion·registeredAtEpochMs·updatedAtEpochMs가 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd ; RuntimeInstanceController L27~73 | 인스턴스 조회 포트와 응답 생성자의 필드 순서 확인 |

### 20. Gateway 소유권과 Business 배정 조회
- 절 대조: Gateway 임대·Business 배정 조회 포트와 응답 타입 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L271 | \| GET /api/eqp/gateway-shard-leases \| 없음 \| GatewayShardLeaseResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd L38~48 | public ResponseEntity<ApiResponse<List<GatewayShardLeaseResponse>>> list()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L272 | \| GET /api/eqp/business-app-assignments \| 없음 \| BusinessAppAssignmentResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/BusinessAppAssignmentController.java @ 985d17c604dd L38~49 | public ResponseEntity<ApiResponse<List<BusinessAppAssignmentResponse>>> list()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L274 | 운영 조회·인계는 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd ; GatewayShardLeaseController L22~47 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/BusinessAppAssignmentController.java @ 985d17c604dd ; BusinessAppAssignmentController L23~48 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L47 | Gateway 임대·Business 배정 조회 포트와 응답 타입 확인 |

### 21. 배포 이력과 등록 후보 조회
- 절 대조: 배포 후보·이력의 선택 조회 조건·날짜 역전 거부 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L283 | \| GET /api/deploys \| modelName·requestedBy·fromDate·toDate·terminalOnly·offset·limit \| DeployHistoryPageResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L135~162 | public ResponseEntity<ApiResponse<DeployHistoryPageResponse>> getHistory( @RequestParam(name = "modelName", required = false) final String modelName, @RequestParam(name = "requestedBy", required = false) final String requestedBy, @RequestParam(name = "fromDate", required = false) final String fromDate, @RequestParam(name = "toDate", required = false) final String toDate, @RequestParam(name = "terminalOnly", required = false) final Boolean terminalOnly, @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L284 | \| GET /api/deploys/register/targets \| modelName·modelVersionKey·eqpId·lineKey·offset·limit \| DeployRegisterTargetPageResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L102~121 | public ResponseEntity<ApiResponse<DeployRegisterTargetPageResponse>> getRegisterTargets( @RequestParam(name = "modelName", required = false) final String modelName, @RequestParam(name = "modelVersionKey", required = false) final Long modelVersionKey, @RequestParam(name = "eqpId", required = false) final String eqpId, @RequestParam(name = "lineKey", required = false) final Long lineKey, @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L286 | 날짜 종료 조건이 시작 조건보다 빠르면 400으로 거부한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L102~161 | 배포 후보·이력의 선택 조회 조건·날짜 역전 거부 확인 |

### 22. 배포 생성과 등록 시나리오 미리보기
- 절 대조: 생성·미리보기·카탈로그 경로 및 요청 DTO 필수 검증 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L297 | \| POST /api/deploys \| DeployCreateRequest \| 202 DeployCreateResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L171~182 | public ResponseEntity<ApiResponse<DeployCreateResponse>> create( @Valid @RequestBody final DeployCreateRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L298 | \| POST /api/deploys/register/scenario-preview \| DeployRegisterScenarioPreviewRequest \| 200 DeployRegisterScenarioPreviewResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L190~198 | public ResponseEntity<ApiResponse<DeployRegisterScenarioPreviewResponse>> previewRegisterScenario( @Valid @RequestBody final DeployRegisterScenarioPreviewRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L299 | \| GET /api/deploys/scenarios \| 선택 조회 조건 eventType \| 200 DeployScenarioCatalogResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L206~227 | public ResponseEntity<ApiResponse<DeployScenarioCatalogResponse>> getScenarios( @RequestParam(name = "eventType", required = false) final String eventType )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L301 | 생성 본문은 deployName·eventType·commonRequest·한 건 이상 targets를 요구한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L171~226 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/DeployCreateRequest.java @ 985d17c604dd ; DeployCreateRequest L17~59 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/DeployRegisterScenarioPreviewRequest.java @ 985d17c604dd ; DeployRegisterScenarioPreviewRequest L14~46 | 생성·미리보기·카탈로그 경로 및 요청 DTO 필수 검증 확인 |
| L301 | 미리보기도 commonRequest와 한 건 이상 targets를 요구한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L171~226 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/DeployCreateRequest.java @ 985d17c604dd ; DeployCreateRequest L17~59 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/DeployRegisterScenarioPreviewRequest.java @ 985d17c604dd ; DeployRegisterScenarioPreviewRequest L14~46 | 생성·미리보기·카탈로그 경로 및 요청 DTO 필수 검증 확인 |

### 23. 배포 실행·보류·취소 API
- 절 대조: 배포 start·hold·cancel 서비스 호출과 200 응답 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L310 | \| POST /api/deploys/{deployKey}/start \| 경로 deployKey \| DeployActionResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L252~259 | public ResponseEntity<ApiResponse<DeployActionResponse>> start( @PathVariable final long deployKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L311 | \| POST /api/deploys/{deployKey}/hold \| 경로 deployKey \| DeployActionResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L268~275 | public ResponseEntity<ApiResponse<DeployActionResponse>> hold( @PathVariable final long deployKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L312 | \| POST /api/deploys/{deployKey}/cancel \| 경로 deployKey \| DeployActionResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L236~243 | public ResponseEntity<ApiResponse<DeployActionResponse>> cancel( @PathVariable final long deployKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L314 | 실행 조건과 사용 절차는 `00.rag/user/deployment-workflow.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L236~274 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L32 | 배포 start·hold·cancel 서비스 호출과 200 응답 확인 |

### 24. 배포 재접수·삭제·요약 조회
- 절 대조: 재배포 202·삭제 200·요약 조회 200 및 결과 타입 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L323 | \| POST /api/deploys/{deployKey}/redeploy \| 경로 deployKey \| 202 DeployCreateResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L284~291 | public ResponseEntity<ApiResponse<DeployCreateResponse>> redeploy( @PathVariable final long deployKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L324 | \| DELETE /api/deploys/{deployKey} \| 경로 deployKey \| 200 DeployActionResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L300~307 | public ResponseEntity<ApiResponse<DeployActionResponse>> delete( @PathVariable final long deployKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L325 | \| GET /api/deploys/{deployKey} \| 경로 deployKey \| 200 DeploySummaryResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L315~321 | public ResponseEntity<ApiResponse<DeploySummaryResponse>> getSummary( @PathVariable final long deployKey )의 매핑·입력 바인딩·호출·반환문을 대조했다. |

### 25. 배포 대상별 진행 조회
- 절 대조: 대상 조회의 page=0·size=50 기본 전달·필터 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L332 | `GET /api/deploys/{deployKey}/targets`는 200의 DeployTargetPageResponse를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L333~348 | 대상 조회의 page=0·size=50 기본 전달·필터 확인 |
| L332 | 경로 deployKey 외에 page·size·status·eqpId 조회 조건을 선택으로 받는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L333~348 | 대상 조회의 page=0·size=50 기본 전달·필터 확인 |
| L332 | 생략한 page는 0, size는 50으로 서비스에 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L333~348 | 대상 조회의 page=0·size=50 기본 전달·필터 확인 |

### 26. 배포 시나리오 관리 API
- 절 대조: 관리 validate/reload의 선택 본문·reason 및 status/validationErrors 조립 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L339 | 아래 호출의 성공 응답은 200이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; DeployScenarioAdminController L52~117 | 관리 validate/reload의 선택 본문·reason 및 status/validationErrors 조립 확인 |
| L339 | validate·reload는 본문을 생략하거나 reason을 담은 DeployScenarioAdminActionRequest를 보낸다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; DeployScenarioAdminController L52~117 | 관리 validate/reload의 선택 본문·reason 및 status/validationErrors 조립 확인 |
| L343 | \| POST /api/admin/deploy-scenarios/validate \| DeployScenarioValidationResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd L52~68 | public ResponseEntity<ApiResponse<DeployScenarioValidationResponse>> validate( @RequestBody(required = false) final DeployScenarioAdminActionRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L344 | \| POST /api/admin/deploy-scenarios/reload \| DeployScenarioReloadResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd L77~95 | public ResponseEntity<ApiResponse<DeployScenarioReloadResponse>> reload( @RequestBody(required = false) final DeployScenarioAdminActionRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L345 | \| GET /api/admin/deploy-scenarios/catalog \| DeployScenarioCatalogResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd L102~118 | public ResponseEntity<ApiResponse<DeployScenarioCatalogResponse>> catalog()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L347 | validate·reload 결과의 status·validationErrors도 함께 읽는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; DeployScenarioAdminController L52~117 | 관리 validate/reload의 선택 본문·reason 및 status/validationErrors 조립 확인 |

### 27. DLQ와 격리·복구 작업 조회
- 절 대조: DLQ·격리·Repair Task의 포트/서비스 조회와 결과 목록 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L357 | \| GET /api/dlq/gateway \| Gateway DLQ 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L81~89 | public ResponseEntity<ApiResponse<List<?>>> listGatewayDlq()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L358 | \| GET /api/dlq/business \| Business DLQ 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L129~137 | public ResponseEntity<ApiResponse<List<?>>> listBusinessDlq()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L359 | \| GET /api/dlq/quarantine \| Gateway 격리 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L177~185 | public ResponseEntity<ApiResponse<List<?>>> listGatewayQuarantine()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L360 | \| GET /api/dlq/repair-task \| EqpRepairTaskResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L200~210 | public ResponseEntity<ApiResponse<List<EqpRepairTaskResponse>>> listRepairTasks()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L362 | 화면 조회는 `00.rag/user/dlq-quarantine-and-repair-task.md`가 다룬다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; DlqController L81~89 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; DlqController L129~209 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L33 | DLQ·격리·Repair Task의 포트/서비스 조회와 결과 목록 확인 |

### 28. DLQ 항목 삭제 API
- 절 대조: 두 DLQ 삭제의 boolean을 공통 성공 data로 반환하는 코드 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L371 | \| DELETE /api/dlq/gateway/{dlqId} \| 경로 dlqId \| 삭제 여부 Boolean \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L100~115 | public ResponseEntity<ApiResponse<Boolean>> deleteGatewayDlq( @PathVariable final String dlqId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L372 | \| DELETE /api/dlq/business/{dlqId} \| 경로 dlqId \| 삭제 여부 Boolean \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L148~163 | public ResponseEntity<ApiResponse<Boolean>> deleteBusinessDlq( @PathVariable final String dlqId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |

### 29. 통신 로그 조회 API
- 절 대조: 통신 로그 type 기본 all과 switch의 eqp/mes/default 분기 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L381 | \| GET /api/comm-logs/{eqpId} \| 선택 조회 조건 type \| CommLogEntry 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd L35~46 | public ResponseEntity<ApiResponse<List<CommLogEntry>>> getCommLogs( @PathVariable final String eqpId, @RequestParam(defaultValue = "all") final String type )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L383 | 통신 로그 type은 기본 all이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; CommLogController L35~45 | 통신 로그 type 기본 all과 switch의 eqp/mes/default 분기 확인 |
| L383 | eqp는 설비 로그, mes는 MES 로그를 고르고 나머지 값은 전체 조회로 처리한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; CommLogController L35~45 | 통신 로그 type 기본 all과 switch의 eqp/mes/default 분기 확인 |

### 30. 라인 목록과 Live-Map 데이터
- 절 대조: 라인·Live-Map 조회의 서비스 호출과 응답 변환 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L393 | \| GET /api/line \| 없음 \| LineResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd L38~44 | public ResponseEntity<ApiResponse<List<LineResponse>>> list()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L394 | \| GET /api/live-map \| 없음 \| LiveMapResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L47~52 | public ResponseEntity<ApiResponse<LiveMapResponse>> getLiveMap()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L396 | 화면에서 읽는 방법은 `00.rag/user/live-map-status.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; LineController L24~43 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~51 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L25 | 라인·Live-Map 조회의 서비스 호출과 응답 변환 확인 |

### 31. Live-Map 배치와 구조물 조회
- 절 대조: lineKey 필수 바인딩·배치 저장 DTO·구조물 목록 반환 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L406 | \| GET /api/live-map/layout \| 필수 조회 조건 lineKey \| LiveMapLayoutResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd L48~55 | public ResponseEntity<ApiResponse<LiveMapLayoutResponse>> getLayout( @RequestParam final long lineKey )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L407 | \| PUT /api/live-map/layout \| lineKey와 LiveMapLayoutPutRequest \| LiveMapLayoutResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd L57~67 | public ResponseEntity<ApiResponse<LiveMapLayoutResponse>> putLayout( @RequestParam final long lineKey, @RequestBody final LiveMapLayoutPutRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L408 | \| GET /api/live-map/layout/objects \| 없음 \| LiveMapObjectResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd L69~74 | public ResponseEntity<ApiResponse<List<LiveMapObjectResponse>>> listObjects()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L410 | 배치 저장 본문에는 canvasJson·placements가 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; LiveMapLayoutController L34~74 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapLayoutPutRequest.java @ 985d17c604dd ; LiveMapLayoutPutRequest L13~22 | lineKey 필수 바인딩·배치 저장 DTO·구조물 목록 반환 확인 |

### 32. Live-Map 구조물 저장·삭제
- 절 대조: 구조물 저장 요청 필드 전달·삭제의 null 응답 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L420 | \| POST /api/live-map/layout/objects \| LiveMapObjectSaveRequest \| LiveMapObjectResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd L76~84 | public ResponseEntity<ApiResponse<LiveMapObjectResponse>> createObject( @RequestBody final LiveMapObjectSaveRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L421 | \| PUT /api/live-map/layout/objects/{objectKey} \| LiveMapObjectSaveRequest \| LiveMapObjectResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd L86~95 | public ResponseEntity<ApiResponse<LiveMapObjectResponse>> updateObject( @PathVariable final long objectKey, @RequestBody final LiveMapObjectSaveRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L422 | \| DELETE /api/live-map/layout/objects/{objectKey} \| 없음 \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd L97~103 | public ResponseEntity<ApiResponse<Void>> deleteObject( @PathVariable final long objectKey )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L424 | 저장 본문은 objectName·objectKind·shapeJson을 갖는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; LiveMapLayoutController L76~102 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapObjectSaveRequest.java @ 985d17c604dd ; LiveMapObjectSaveRequest L11~15 | 구조물 저장 요청 필드 전달·삭제의 null 응답 확인 |
| L424 | 화면 편집은 `00.rag/user/live-map-layout-editing.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; LiveMapLayoutController L76~102 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapObjectSaveRequest.java @ 985d17c604dd ; LiveMapObjectSaveRequest L11~15 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L26 | 구조물 저장 요청 필드 전달·삭제의 null 응답 확인 |

### 33. 브라우저의 데모 생성·목록·상세
- 절 대조: 브라우저 데모 생성·목록·상세의 타입과 testType/scenarioId/eqpIds 전달 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L434 | \| POST /api/live-map/demo-runs \| LiveMapSimulatorRunCreateRequest \| SimulatorRunResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L56~63 | public ResponseEntity<ApiResponse<SimulatorRunResponse>> startSimulatorRun( @Valid @RequestBody final LiveMapSimulatorRunCreateRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L435 | \| GET /api/live-map/demo-runs \| 없음 \| SimulatorRunResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L69~72 | public ResponseEntity<ApiResponse<List<SimulatorRunResponse>>> listSimulatorRuns()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L436 | \| GET /api/live-map/demo-runs/{runId} \| 경로 runId \| SimulatorRunResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L74~79 | public ResponseEntity<ApiResponse<SimulatorRunResponse>> getSimulatorRun( @PathVariable final String runId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L438 | 생성 본문은 testType·scenarioId·eqpIds를 받는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L56~78 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapSimulatorRunCreateRequest.java @ 985d17c604dd ; LiveMapSimulatorRunCreateRequest L12~19 | 브라우저 데모 생성·목록·상세의 타입과 testType/scenarioId/eqpIds 전달 확인 |
| L438 | orchestrator 생성 본문의 mode·targets와 구별한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L56~78 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapSimulatorRunCreateRequest.java @ 985d17c604dd ; LiveMapSimulatorRunCreateRequest L12~19 | 브라우저 데모 생성·목록·상세의 타입과 testType/scenarioId/eqpIds 전달 확인 |

### 34. 브라우저의 데모 종료 요청
- 절 대조: 전체·대상별 stop의 경로 변수·본문 부재·결과 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L447 | \| POST /api/live-map/demo-runs/{runId}/stop \| 경로 runId \| SimulatorRunResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L81~86 | public ResponseEntity<ApiResponse<SimulatorRunResponse>> stopSimulatorRun( @PathVariable final String runId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L448 | \| POST /api/live-map/demo-runs/{runId}/targets/{eqpId}/stop \| 경로 runId·eqpId \| SimulatorRunResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L88~94 | public ResponseEntity<ApiResponse<SimulatorRunResponse>> stopSimulatorTarget( @PathVariable final String runId, @PathVariable final String eqpId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L450 | 데모 사용은 `00.rag/user/demo-execution.md`, 실패 확인은 `00.rag/user/demo-troubleshooting.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L81~93 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L35 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L60 | 전체·대상별 stop의 경로 변수·본문 부재·결과 확인 |

### 35. 브라우저의 시나리오 파일 API
- 절 대조: 시나리오의 mode 조회·multipart file 읽기·빈 파일 거부·UTF-8 변환·삭제 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L459 | \| GET /api/live-map/demo-runs/scenarios \| 선택 조회 조건 mode \| SimulatorScenarioCatalogItem 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L96~101 | public ResponseEntity<ApiResponse<List<SimulatorScenarioCatalogItem>>> getSimulatorScenarios( @RequestParam(required = false) final String mode )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L460 | \| POST /api/live-map/demo-runs/scenarios \| multipart/form-data의 file \| SimulatorScenarioCatalogItem \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L107~113 | public ResponseEntity<ApiResponse<SimulatorScenarioCatalogItem>> uploadSimulatorScenario( @RequestParam("file") final MultipartFile file )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L461 | \| DELETE /api/live-map/demo-runs/scenarios/{scenarioId} \| 경로 scenarioId \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L119~123 | public ResponseEntity<ApiResponse<Void>> deleteSimulatorScenario(@PathVariable final String scenarioId)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L463 | 업로드 파일은 비어 있으면 거부하며 UTF-8 문자열로 읽어 orchestrator에 보낸다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L96~132 | 시나리오의 mode 조회·multipart file 읽기·빈 파일 거부·UTF-8 변환·삭제 확인 |

### 36. 모델 목록·버전 조회와 기본 저장
- 절 대조: 모델 목록 페이지·버전 조회·공통 저장 DTO의 소비자 호출 및 응답 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L473 | \| GET /api/model \| 선택 offset·limit \| PagedResponse의 ModelInfoResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L46~54 | public ResponseEntity<ApiResponse<PagedResponse<ModelInfoResponse>>> list( @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L474 | \| GET /api/model/{modelVersionKey} \| 경로 modelVersionKey \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L56~68 | public ResponseEntity<ApiResponse<ModelInfoResponse>> get( @PathVariable final long modelVersionKey )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L475 | \| POST /api/model \| ModelUpsertRequest \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd L49~69 | public ResponseEntity<ApiResponse<ModelInfoResponse>> create( @Valid @RequestBody final ModelUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L476 | \| PUT /api/model/{modelVersionKey} \| ModelUpsertRequest \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd L71~99 | public ResponseEntity<ApiResponse<ModelInfoResponse>> update( @PathVariable final long modelVersionKey, @Valid @RequestBody final ModelUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L478 | 단건 GET·PUT은 모델이 없으면 컨트롤러가 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L60~64 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd L76~80 | 단건 모델 조회·수정의 모델 부재 분기가 404 NOT_FOUND를 직접 반환한다. |

### 37. 루트 모델과 branch 생성·정보 변경
- 절 대조: 루트/branch/정보 변경의 요청 DTO·경로 변수·반환 모델 변환 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L487 | \| POST /api/model/roots \| ModelRootCreateRequest \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L56~69 | public ResponseEntity<ApiResponse<ModelInfoResponse>> createRoot( @Valid @RequestBody final ModelRootCreateRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L488 | \| PUT /api/model/{modelKey}/info \| ModelInfoUpdateRequest \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L71~82 | public ResponseEntity<ApiResponse<ModelInfoResponse>> updateModelInfo( @PathVariable final long modelKey, @Valid @RequestBody final ModelInfoUpdateRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L489 | \| POST /api/model/{modelKey}/branches \| ModelBranchCreateRequest \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L84~100 | public ResponseEntity<ApiResponse<ModelInfoResponse>> createBranch( @PathVariable final long modelKey, @Valid @RequestBody final ModelBranchCreateRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L491 | 등록 절차는 `00.rag/user/model-registration-and-branching.md`가 소유한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L56~99 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L30 | 루트/branch/정보 변경의 요청 DTO·경로 변수·반환 모델 변환 확인 |

### 38. 모델 버전 체크아웃·체크인과 부모 반영
- 절 대조: checkout/checkin/commit-parent 입력과 본문 부재 applyCommit=false 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L500 | \| POST /api/model/{modelVersionKey}/checkout \| 없음 \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L102~112 | public ResponseEntity<ApiResponse<ModelInfoResponse>> checkoutBranchVersion( @PathVariable final long modelVersionKey, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L501 | \| POST /api/model/{modelVersionKey}/checkin \| ModelCheckinRequest \| ModelInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L114~129 | public ResponseEntity<ApiResponse<ModelInfoResponse>> checkinBranchEditVersion( @PathVariable final long modelVersionKey, @Valid @RequestBody final ModelCheckinRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L502 | \| POST /api/model/{modelKey}/commit-parent \| 선택 ModelParentCommitRequest \| ModelParentCommitResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L131~144 | public ResponseEntity<ApiResponse<ModelParentCommitResponse>> commitParent( @PathVariable final long modelKey, @RequestBody(required = false) final ModelParentCommitRequest request, final Authentication authentication )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L504 | 부모 반영 본문이 없으면 applyCommit을 false로 처리한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L102~143 | checkout/checkin/commit-parent 입력과 본문 부재 applyCommit=false 확인 |

### 39. 모델 삭제 범위와 deprecated branch 삭제
- 절 대조: 삭제 scope 정규화·version/model 분기·deprecated 일괄 삭제 결과 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L514 | \| DELETE /api/model/{modelIdentifier} \| 선택 조회 조건 scope \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd L101~117 | public ResponseEntity<ApiResponse<Void>> delete( @PathVariable final long modelIdentifier, @RequestParam(name = "scope", required = false, defaultValue = "version") final String scope )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L515 | \| DELETE /api/model/{modelKey}/branches/deprecated \| 경로 modelKey \| ModelDeleteBatchResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L146~158 | public ResponseEntity<ApiResponse<ModelDeleteBatchResponse>> deleteDeprecatedBranches( @PathVariable final long modelKey )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L517 | scope의 기본은 version이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; ModelMutationController L101~116 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L146~157 | 삭제 scope 정규화·version/model 분기·deprecated 일괄 삭제 결과 확인 |
| L517 | version은 모델 버전 키로, model은 모델 키로 삭제한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; ModelMutationController L101~116 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L146~157 | 삭제 scope 정규화·version/model 분기·deprecated 일괄 삭제 결과 확인 |
| L517 | 이외 scope는 거부한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; ModelMutationController L101~116 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L146~157 | 삭제 scope 정규화·version/model 분기·deprecated 일괄 삭제 결과 확인 |

### 40. 모델 상세 데이터와 MDF 업로드
- 절 대조: 상세 JSON·MDF multipart·EDIT 검사·요청 및 결과 타입 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L527 | \| GET /api/model/{modelVersionKey}/details/{detailNode} \| 경로 두 값 \| ModelDetailDataResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L70~94 | public ResponseEntity<ApiResponse<ModelDetailDataResponse>> getDetail( @PathVariable final long modelVersionKey, @PathVariable final String detailNode )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L528 | \| PUT /api/model/{modelVersionKey}/details/{detailNode} \| JSON ModelDetailSaveRequest \| ModelDetailDataResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L55~128 | public ResponseEntity<ApiResponse<ModelDetailDataResponse>> saveDetailRows( @PathVariable final long modelVersionKey, @PathVariable final String detailNode, @Valid @RequestBody final ModelDetailSaveRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L529 | \| PUT /api/model/{modelVersionKey}/details/mdf \| multipart/form-data의 file·선택 mdfName \| ModelMdfContentResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L130~161 | public ResponseEntity<ApiResponse<ModelMdfContentResponse>> uploadMdf( @PathVariable final long modelVersionKey, @RequestParam("file") final MultipartFile file, @RequestParam(name = "mdfName", required = false) final String mdfName )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L531 | 상세 저장·MDF 업로드는 EDIT 버전만 받으며, 아니면 400 INVALID_MODEL_VERSION을 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L70~72 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L145~147 | 두 PUT에서 EDIT 검사가 실패하면 400 INVALID_MODEL_VERSION을 직접 반환한다. |

### 41. 모델 상세 API의 대상·노드 오류
- 절 대조: 조회·저장·MDF의 모델 부재 404, 조회의 지원 노드 검사 400, 저장의 mdf·workflow·미지원 노드 검사를 직접 대조했다. PLC 순서 값 검증 예외와 공통 오류 응답 매핑을 추가 확인했다. 보완 3에서 노드별 행 입력 검사 전반과 예외 메시지 응답까지 확인했다.
- 사람 검사: 제목으로 오류 대상을 식별함 / 지정 커밋과 일치 / 조건·HTTP 상태·코드가 절 안에 있음 / 기존 성공 응답 표와 오류 코드 뜻풀이를 복제하지 않음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L542 | 모델 상세 API는 다음 오류를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L75~90 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L64~78 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L96~104 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L123~125 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L139~143 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 L150~179 L211~528 L542~735 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 L12~22 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd L89~98 | 컨트롤러 직접 오류와 저장 포트의 노드별 행 입력 검사 예외가 공통 오류 처리기로 전달되는 경로를 함께 확인했다. |
| L544 | - `GET /api/model/{modelVersionKey}/details/{detailNode}`: 모델 부재는 404 NOT_FOUND, 지원하지 않는 노드는 400 INVALID_NODE다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L75~90 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L64~78 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L96~104 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L123~125 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L139~143 | 조회·저장·MDF의 모델 부재 404, 조회의 지원 노드 검사 400, 저장의 mdf·workflow·미지원 노드 검사를 직접 대조했다. |
| L545 | - `PUT /api/model/{modelVersionKey}/details/{detailNode}`: 모델 부재는 404 NOT_FOUND, mdf·미지원 노드는 400 INVALID_NODE다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L75~90 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L64~78 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L96~104 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L123~125 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L139~143 | 조회·저장·MDF의 모델 부재 404, 조회의 지원 노드 검사 400, 저장의 mdf·workflow·미지원 노드 검사를 직접 대조했다. |
| L545 | workflow 검증·노드별 행 입력 검사(필수 칸·형식·PLC 그룹·순서 값 등) 실패는 400 INVALID_REQUEST이며 응답 메시지에 사유를 담는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L75~90 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L64~78 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L96~104 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L123~125 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L139~143 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 L150~179 L211~528 L542~735 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 L12~22 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd L89~98 | workflow 직접 검증 메시지와 노드별 필수 칸·값 형식·PLC 그룹 참조·영역/자료형·레이아웃·순서 검사에서 발생하는 UiBadRequestException의 400 INVALID_REQUEST 및 예외 메시지 응답을 대조했다. |
| L546 | - `PUT /api/model/{modelVersionKey}/details/mdf`: 모델이 없으면 404 NOT_FOUND다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L75~90 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L64~78 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L96~104 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L123~125 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd L139~143 | 조회·저장·MDF의 모델 부재 404, 조회의 지원 노드 검사 400, 저장의 mdf·workflow·미지원 노드 검사를 직접 대조했다. |

### 42. Flow 생성과 목록 조회
- 절 대조: Flow 생성의 workflowName/messageName 전달·flowId 응답·목록 변환 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L556 | \| POST /api/model/{modelVersionKey}/flows \| ModelFlowCreateRequest \| ModelFlowCreatedResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd L65~76 | public ResponseEntity<ApiResponse<ModelFlowCreatedResponse>> createFlow( @PathVariable final long modelVersionKey, @RequestBody final ModelFlowCreateRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L557 | \| GET /api/model/{modelVersionKey}/flows \| 경로 modelVersionKey \| ModelFlowListResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd L84~90 | public ResponseEntity<ApiResponse<ModelFlowListResponse>> listFlows( @PathVariable final long modelVersionKey )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L559 | 생성 본문에는 workflowName·messageName이 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; ModelFlowController L44~89 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelFlowCreateRequest.java @ 985d17c604dd ; ModelFlowCreateRequest L16~19 | Flow 생성의 workflowName/messageName 전달·flowId 응답·목록 변환 확인 |
| L559 | 생성 결과는 flowId를 담는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; ModelFlowController L44~89 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelFlowCreateRequest.java @ 985d17c604dd ; ModelFlowCreateRequest L16~19 | Flow 생성의 workflowName/messageName 전달·flowId 응답·목록 변환 확인 |

### 43. Flow 한 건의 조회·저장·삭제
- 절 대조: Flow 상세·수정·삭제의 경로 변수·요청 타입·반환 데이터 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L568 | \| GET /api/model/{modelVersionKey}/flows/{flowId} \| 경로 두 값 \| ModelFlowDetailResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd L99~106 | public ResponseEntity<ApiResponse<ModelFlowDetailResponse>> getFlow( @PathVariable final long modelVersionKey, @PathVariable final long flowId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L569 | \| PUT /api/model/{modelVersionKey}/flows/{flowId} \| ModelFlowUpdateRequest \| ModelFlowDetailResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd L123~139 | public ResponseEntity<ApiResponse<ModelFlowDetailResponse>> updateFlow( @PathVariable final long modelVersionKey, @PathVariable final long flowId, @RequestBody final ModelFlowUpdateRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L570 | \| DELETE /api/model/{modelVersionKey}/flows/{flowId} \| 경로 두 값 \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd L148~155 | public ResponseEntity<ApiResponse<Void>> deleteFlow( @PathVariable final long modelVersionKey, @PathVariable final long flowId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L572 | 상세·Flow 편집 절차는 `00.rag/user/model-details-and-flow-editing.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; ModelFlowController L99~154 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L31 | Flow 상세·수정·삭제의 경로 변수·요청 타입·반환 데이터 확인 |

### 44. 사용자 조회와 저장 API
- 절 대조: 사용자 CRUD 조회·저장 facade 호출과 결과 타입 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L581 | \| GET /api/user \| 선택 offset·limit \| PagedResponse의 UserListItemResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L73~92 | public ResponseEntity<ApiResponse<PagedResponse<UserListItemResponse>>> list( @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L582 | \| GET /api/user/{userPk} \| 경로 userPk \| UserInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L100~116 | public ResponseEntity<ApiResponse<UserInfoResponse>> get( @PathVariable final long userPk )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L583 | \| POST /api/user \| UserUpsertRequest \| UserInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L124~133 | public ResponseEntity<ApiResponse<UserInfoResponse>> create( @Valid @RequestBody final UserUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L584 | \| PUT /api/user/{userPk} \| UserUpsertRequest \| UserInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L142~157 | public ResponseEntity<ApiResponse<UserInfoResponse>> update( @PathVariable final long userPk, @Valid @RequestBody final UserUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L586 | 단건 GET·PUT은 사용자가 없으면 컨트롤러가 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L108~112 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L149~153 | 단건 조회·수정 결과가 비면 404 NOT_FOUND를 직접 반환한다. |

### 45. 사용자 삭제·비밀번호 초기화·그룹 소속 조회
- 절 대조: 사용자 삭제·소속 조회·초기화 DTO 검증과 응답 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L597 | \| DELETE /api/user/{userPk} \| 경로 userPk \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L165~173 | public ResponseEntity<ApiResponse<Void>> delete( @PathVariable final long userPk )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L598 | \| POST /api/user/{userPk}/password/reset \| UserPasswordResetRequest \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L254~268 | public ResponseEntity<ApiResponse<Void>> resetPassword( @PathVariable final long userPk, @Valid @RequestBody final UserPasswordResetRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L599 | \| GET /api/user/{userPk}/group \| 선택 offset·limit \| PagedResponse의 UserGroupMembershipResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L223~245 | public ResponseEntity<ApiResponse<PagedResponse<UserGroupMembershipResponse>>> listGroupsByUser( @PathVariable final long userPk, @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L601 | 초기화 본문에는 빈 값이 아닌 newPassword와 선택 updatedBy가 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L165~172 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L223~267 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserPasswordResetRequest.java @ 985d17c604dd ; UserPasswordResetRequest L16~22 | 사용자 삭제·소속 조회·초기화 DTO 검증과 응답 확인 |
| L603 | `GET /api/user/{userPk}/group`·`POST /api/user/{userPk}/password/reset`은 사용자가 없으면 컨트롤러가 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L231~235 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L261~264 | 소속 조회에서 사용자 부재 또는 resetPassword=false이면 404 NOT_FOUND를 직접 반환한다. |

### 46. 사용자와 그룹의 연결 변경
- 절 대조: 사용자-그룹 경로 변수만 받는 연결 생성·해제와 결과 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L612 | \| POST /api/user/{userPk}/group/{groupId} \| 경로 userPk·groupId \| UserGroupMappingResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L182~195 | public ResponseEntity<ApiResponse<UserGroupMappingResponse>> addUserGroupMapping( @PathVariable final long userPk, @PathVariable final long groupId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L613 | \| DELETE /api/user/{userPk}/group/{groupId} \| 경로 userPk·groupId \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L204~213 | public ResponseEntity<ApiResponse<Void>> removeUserGroupMapping( @PathVariable final long userPk, @PathVariable final long groupId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L615 | 본문은 받지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L182~212 | 사용자-그룹 경로 변수만 받는 연결 생성·해제와 결과 확인 |
| L615 | 계정·그룹 조작은 `00.rag/user/user-group-and-permission-management.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L182~212 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L34 | 사용자-그룹 경로 변수만 받는 연결 생성·해제와 결과 확인 |

### 47. 그룹 기본 정보 API
- 절 대조: 그룹 기본 CRUD facade 호출·본문 타입·결과 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L624 | \| GET /api/group \| 선택 offset·limit \| PagedResponse의 GroupInfoResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L71~90 | public ResponseEntity<ApiResponse<PagedResponse<GroupInfoResponse>>> list( @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L625 | \| GET /api/group/{groupId} \| 경로 groupId \| GroupInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L98~114 | public ResponseEntity<ApiResponse<GroupInfoResponse>> get( @PathVariable final long groupId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L626 | \| POST /api/group \| GroupUpsertRequest \| GroupInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L122~131 | public ResponseEntity<ApiResponse<GroupInfoResponse>> create( @Valid @RequestBody final GroupUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L627 | \| PUT /api/group/{groupId} \| GroupUpsertRequest \| GroupInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L140~156 | public ResponseEntity<ApiResponse<GroupInfoResponse>> update( @PathVariable final long groupId, @Valid @RequestBody final GroupUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L628 | \| DELETE /api/group/{groupId} \| 경로 groupId \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L164~172 | public ResponseEntity<ApiResponse<Void>> delete( @PathVariable final long groupId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L630 | 단건 GET·PUT은 그룹이 없으면 컨트롤러가 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L106~110 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L148~152 | 단건 조회·수정의 그룹 부재 분기에서 404 NOT_FOUND를 직접 반환한다. |

### 48. 그룹의 권한·구성원 조회
- 절 대조: 그룹의 권한·구성원 페이지 조회 조건 및 결과 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L640 | \| GET /api/group/{groupId}/permission \| 선택 offset·limit \| PagedResponse의 GroupPermissionMappingResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L182~204 | public ResponseEntity<ApiResponse<PagedResponse<GroupPermissionMappingResponse>>> listPermissions( @PathVariable final long groupId, @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L641 | \| GET /api/group/{groupId}/user \| 선택 offset·limit \| PagedResponse의 GroupMemberResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L254~276 | public ResponseEntity<ApiResponse<PagedResponse<GroupMemberResponse>>> listUsersByGroup( @PathVariable final long groupId, @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L643 | 두 조회는 대상 그룹이 없으면 컨트롤러가 404 NOT_FOUND를 반환한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L190~194 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L262~266 | 권한·구성원 조회의 대상 그룹 부재 분기에서 404 NOT_FOUND를 직접 반환한다. |

### 49. 그룹과 권한의 연결 변경
- 절 대조: 그룹-권한 연결의 경로 변수·생성/삭제 호출과 결과 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L652 | \| POST /api/group/{groupId}/permission/{permId} \| 경로 groupId·permId \| GroupPermissionMappingResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L213~226 | public ResponseEntity<ApiResponse<GroupPermissionMappingResponse>> addPermissionMapping( @PathVariable final long groupId, @PathVariable final long permId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L653 | \| DELETE /api/group/{groupId}/permission/{permId} \| 경로 groupId·permId \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L235~244 | public ResponseEntity<ApiResponse<Void>> removePermissionMapping( @PathVariable final long groupId, @PathVariable final long permId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L655 | 두 요청 모두 본문을 받지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L213~243 | 그룹-권한 연결의 경로 변수·생성/삭제 호출과 결과 확인 |

### 50. 권한 데이터 조회·저장·삭제
- 절 대조: 권한 CRUD 바인딩·facade 호출·결과 확인 직접 반환 분기를 지정 커밋으로 추가 대조했다.
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L664 | \| GET /api/permission \| 선택 offset·limit \| PagedResponse의 PermissionInfoResponse 목록 \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd L63~82 | public ResponseEntity<ApiResponse<PagedResponse<PermissionInfoResponse>>> list( @RequestParam(name = "offset", required = false) final Integer offset, @RequestParam(name = "limit", required = false) final Integer limit )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L665 | \| GET /api/permission/{permId} \| 경로 permId \| PermissionInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd L90~106 | public ResponseEntity<ApiResponse<PermissionInfoResponse>> get( @PathVariable final long permId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L666 | \| POST /api/permission \| PermissionUpsertRequest \| PermissionInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd L114~124 | public ResponseEntity<ApiResponse<PermissionInfoResponse>> create( @Valid @RequestBody final PermissionUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L667 | \| PUT /api/permission/{permId} \| PermissionUpsertRequest \| PermissionInfoResponse \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd L133~149 | public ResponseEntity<ApiResponse<PermissionInfoResponse>> update( @PathVariable final long permId, @Valid @RequestBody final PermissionUpsertRequest request )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L668 | \| DELETE /api/permission/{permId} \| 경로 permId \| null \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd L157~165 | public ResponseEntity<ApiResponse<Void>> delete( @PathVariable final long permId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L670 | 단건 GET·PUT의 대상 부재는 404 NOT_FOUND다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd L98~102 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd L141~145 | 단건 조회·수정의 권한 데이터 부재 분기에서 404 NOT_FOUND를 직접 반환한다. |

### 51. HTTP-SIM 제어 인증과 401 본문
- 절 대조: 공통 내부 인증 필터의 헤더·상수시간 비교·401 JSON과 세 앱 빈 등록 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L681 | HTTP-SIM 제어 요청은 `X-Tc-Internal-Token` 헤더를 사용한다. | nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/security/InternalTokenAuthFilter.java @ 5e9b02e3ae16 ; InternalTokenAuthFilter L34~83 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; OrchestratorRuntimeConfiguration L155~160 / nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/EquipmentWorkerModuleConfiguration.java @ 5e9b02e3ae16 ; EquipmentWorkerModuleConfiguration L50~55 / nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamRuntimeConfiguration.java @ 5e9b02e3ae16 ; UpstreamRuntimeConfiguration L86~91 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L114~139 | 공통 내부 인증 필터의 헤더·상수시간 비교·401 JSON과 세 앱 빈 등록 확인 |
| L681 | orchestrator와 worker의 공통 필터는 공유 인증값이 없거나 다르면 401과 error·message 필드가 있는 JSON을 반환한다. | nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/security/InternalTokenAuthFilter.java @ 5e9b02e3ae16 ; InternalTokenAuthFilter L34~83 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; OrchestratorRuntimeConfiguration L155~160 / nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/EquipmentWorkerModuleConfiguration.java @ 5e9b02e3ae16 ; EquipmentWorkerModuleConfiguration L50~55 / nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamRuntimeConfiguration.java @ 5e9b02e3ae16 ; UpstreamRuntimeConfiguration L86~91 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L114~139 | 공통 내부 인증 필터의 헤더·상수시간 비교·401 JSON과 세 앱 빈 등록 확인 |
| L683 | 인증값의 교체 절차는 `00.rag/admin/security-operations.md`가 소유한다. | nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/security/InternalTokenAuthFilter.java @ 5e9b02e3ae16 ; InternalTokenAuthFilter L34~83 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; OrchestratorRuntimeConfiguration L155~160 / nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/EquipmentWorkerModuleConfiguration.java @ 5e9b02e3ae16 ; EquipmentWorkerModuleConfiguration L50~55 / nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamRuntimeConfiguration.java @ 5e9b02e3ae16 ; UpstreamRuntimeConfiguration L86~91 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L114~139 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L46 | 공통 내부 인증 필터의 헤더·상수시간 비교·401 JSON과 세 앱 빈 등록 확인 |

### 52. orchestrator의 Run 생성과 이력 조회
- 절 대조: Run 생성·이력 한도 200·DTO 검증 및 원시 결과 반환 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L691 | HTTP-SIM 성공 본문은 ApiResponse로 감싸지 않는다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L30~56 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/CreateRunRequest.java @ 5e9b02e3ae16 ; CreateRunRequest L20~42 | Run 생성·이력 한도 200·DTO 검증 및 원시 결과 반환 확인 |
| L695 | \| POST /api/demo-runs \| CreateRunRequest \| RunResponse \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 L42~45 | public ResponseEntity<RunResponse> createRun(@Valid @RequestBody final CreateRunRequest request)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L696 | \| GET /api/demo-runs \| 없음 \| RunResponse 목록, 이력 조회 한도 200 \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 L51~57 | public ResponseEntity<List<RunResponse>> listRuns()의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L698 | 생성 본문은 mode·scenarioId·비어 있지 않은 targets를 받는다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L30~56 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/CreateRunRequest.java @ 5e9b02e3ae16 ; CreateRunRequest L20~42 | Run 생성·이력 한도 200·DTO 검증 및 원시 결과 반환 확인 |
| L698 | 대상 항목은 eqpId·commInterface·commMode·bindPort·portIds를 갖는다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L30~56 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/CreateRunRequest.java @ 5e9b02e3ae16 ; CreateRunRequest L20~42 | Run 생성·이력 한도 200·DTO 검증 및 원시 결과 반환 확인 |

### 53. orchestrator의 Run 조회·종료·MES 활성화
- 절 대조: Run 조회·전체/대상 종료·활성화 메서드의 경로와 본문 부재 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L707 | \| GET /api/demo-runs/{runId} \| 경로 runId \| RunResponse \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 L85~88 | public ResponseEntity<RunResponse> getRun(@PathVariable final String runId)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L708 | \| POST /api/demo-runs/{runId}/stop \| 경로 runId \| RunResponse \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 L59~62 | public ResponseEntity<RunResponse> stopRun(@PathVariable final String runId)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L709 | \| POST /api/demo-runs/{runId}/targets/{eqpId}/stop \| 경로 두 값 \| RunResponse \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 L68~74 | public ResponseEntity<RunResponse> stopTarget( @PathVariable final String runId, @PathVariable final String eqpId )의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L710 | \| POST /api/demo-runs/{runId}/activate-upstream \| 경로 runId \| RunResponse \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 L80~83 | public ResponseEntity<RunResponse> activateUpstream(@PathVariable final String runId)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L712 | POST 세 경로는 별도 본문을 받지 않는다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L59~87 | Run 조회·전체/대상 종료·활성화 메서드의 경로와 본문 부재 확인 |

### 54. RunResponse와 대상별 결과 구조
- 절 대조: RunResponse·TargetResponse 생성자와 이력 변환의 빈 통계/목록 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L720 | RunResponse에는 runId·mode·state·targets·createdAt·scenarioId가 있다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/RunResponse.java @ 5e9b02e3ae16 ; RunResponse L17~48 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/TargetResponse.java @ 5e9b02e3ae16 ; TargetResponse L16~51 | RunResponse·TargetResponse 생성자와 이력 변환의 빈 통계/목록 확인 |
| L720 | createdAt은 생성 시각을 문자열로 반환한다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/RunResponse.java @ 5e9b02e3ae16 ; RunResponse L17~48 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/TargetResponse.java @ 5e9b02e3ae16 ; TargetResponse L16~51 | RunResponse·TargetResponse 생성자와 이력 변환의 빈 통계/목록 확인 |
| L722 | targets의 각 항목에는 eqpId·state·commInterface·completedCycleCount·erroredCycleCount·latencyStats·issuedLotRunIds가 있다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/RunResponse.java @ 5e9b02e3ae16 ; RunResponse L17~48 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/TargetResponse.java @ 5e9b02e3ae16 ; TargetResponse L16~51 | RunResponse·TargetResponse 생성자와 이력 변환의 빈 통계/목록 확인 |
| L724 | 이력 변환은 latencyStats를 빈 통계로, issuedLotRunIds를 빈 목록으로 채운다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/RunResponse.java @ 5e9b02e3ae16 ; RunResponse L17~48 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/TargetResponse.java @ 5e9b02e3ae16 ; TargetResponse L16~51 | RunResponse·TargetResponse 생성자와 이력 변환의 빈 통계/목록 확인 |
| L724 | 상태값 뜻은 `00.rag/user/state-reference.md`가 소유한다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/RunResponse.java @ 5e9b02e3ae16 ; RunResponse L17~48 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/TargetResponse.java @ 5e9b02e3ae16 ; TargetResponse L16~51 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L67 | RunResponse·TargetResponse 생성자와 이력 변환의 빈 통계/목록 확인 |

### 55. orchestrator 시나리오 카탈로그 API
- 절 대조: 시나리오 목록·원문 업로드 JSON·201·삭제 204 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L734 | \| GET /api/scenarios \| 선택 조회 조건 mode \| 200 ScenarioCatalogResponse 목록 \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 L68~71 | public List<ScenarioCatalogResponse> listScenarios(@RequestParam(required = false) final String mode)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L735 | \| POST /api/scenarios \| UploadScenarioRequest의 sourceText \| 201 ScenarioCatalogResponse \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 L73~96 | public ResponseEntity<ScenarioCatalogResponse> uploadScenario(@RequestBody final UploadScenarioRequest request)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L736 | \| DELETE /api/scenarios/{scenarioId} \| 경로 scenarioId \| 본문 없는 204 \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 L98~107 | public ResponseEntity<Void> deleteScenario(@PathVariable final String scenarioId)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L738 | HTTP-UI의 시나리오 업로드는 file을 받지만 이 API는 JSON 본문에 시나리오 원문을 받는다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L68~106 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/UploadScenarioRequest.java @ 5e9b02e3ae16 ; UploadScenarioRequest L9~10 | 시나리오 목록·원문 업로드 JSON·201·삭제 204 확인 |

### 56. HTTP-SIM 오류 본문의 종류
- 절 대조: 400 문자열 advice·409 충돌 응답 DTO·404 본문 부재 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L750 | \| IllegalArgumentException \| 400, 오류 문구 문자열 \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; OrchestratorExceptionAdvice L19~22 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L90~97 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 ; LeaseConflictResponse L10~18 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L109~123 | 400 문자열 advice·409 충돌 응답 DTO·404 본문 부재 확인 |
| L751 | \| Run 자원 점유 충돌 \| 409, rejected 목록의 eqpId·holdingRunId \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; OrchestratorExceptionAdvice L19~22 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L90~97 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 ; LeaseConflictResponse L10~18 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L109~123 | 400 문자열 advice·409 충돌 응답 DTO·404 본문 부재 확인 |
| L752 | \| 시나리오 충돌·기본 시나리오 삭제 거부 \| 409, code·message \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; OrchestratorExceptionAdvice L19~22 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L90~97 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 ; LeaseConflictResponse L10~18 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L109~123 | 400 문자열 advice·409 충돌 응답 DTO·404 본문 부재 확인 |
| L753 | \| Run·삭제할 시나리오 부재 \| 본문 없는 404 \| | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; OrchestratorExceptionAdvice L19~22 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L90~97 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 ; LeaseConflictResponse L10~18 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L109~123 | 400 문자열 advice·409 충돌 응답 DTO·404 본문 부재 확인 |
| L755 | 코드별 뜻은 `00.rag/user/error-message-and-code-reference.md`를 따른다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; OrchestratorExceptionAdvice L19~22 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L90~97 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 ; LeaseConflictResponse L10~18 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L109~123 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L66 | 400 문자열 advice·409 충돌 응답 DTO·404 본문 부재 확인 |

### 57. worker가 보내는 Lot 완료 보고
- 절 대조: LotEventController의 보고 필드 전달과 accepted().build() 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L763 | `POST /internal/runs/{runId}/lot-events`는 worker에서 orchestrator로 보내는 보고다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/LotEventController.java @ 5e9b02e3ae16 ; LotEventController L21~36 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LotEventRequest.java @ 5e9b02e3ae16 ; LotEventRequest L12~13 | LotEventController의 보고 필드 전달과 accepted().build() 확인 |
| L763 | JSON LotEventRequest의 eqpId·portId·errored·latencyMs를 서비스에 전달한다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/LotEventController.java @ 5e9b02e3ae16 ; LotEventController L21~36 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LotEventRequest.java @ 5e9b02e3ae16 ; LotEventRequest L12~13 | LotEventController의 보고 필드 전달과 accepted().build() 확인 |
| L763 | 응답은 본문 없는 202다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/LotEventController.java @ 5e9b02e3ae16 ; LotEventController L21~36 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LotEventRequest.java @ 5e9b02e3ae16 ; LotEventRequest L12~13 | LotEventController의 보고 필드 전달과 accepted().build() 확인 |

### 58. 설비 worker의 내부 주입·중단 경로
- 절 대조: 설비 worker controller의 두 주입·중단 및 서비스 호출·응답 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L770 | 대상 서버는 simulator worker-equipment다. | nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/api/EquipmentWorkerInternalController.java @ 5e9b02e3ae16 ; EquipmentWorkerInternalController L21~43 | 설비 worker controller의 두 주입·중단 및 서비스 호출·응답 확인 |
| L774 | \| POST /internal/inject \| EquipmentInjectRequest \| 200 EquipmentInjectResponse \| | nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/api/EquipmentWorkerInternalController.java @ 5e9b02e3ae16 L30~33 | public EquipmentInjectResponse inject(@RequestBody final EquipmentInjectRequest request)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L775 | \| POST /internal/plc/inject \| PlcInjectRequest \| 200 EquipmentInjectResponse \| | nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/api/EquipmentWorkerInternalController.java @ 5e9b02e3ae16 L35~38 | public EquipmentInjectResponse injectPlc(@RequestBody final PlcInjectRequest request)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L776 | \| POST /internal/abort \| 설비 worker의 AbortRequest.runId \| 본문 없는 204 \| | nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/api/EquipmentWorkerInternalController.java @ 5e9b02e3ae16 L40~44 | public ResponseEntity<Void> abort(@RequestBody final AbortRequest request)의 매핑·입력 바인딩·호출·반환문을 대조했다. |

### 59. 업스트림 worker의 내부 주입·중단 경로
- 절 대조: 업스트림 worker controller의 주입·중단 및 서비스 호출·응답 확인
- 사람 검사: 제목으로 조회 대상을 식별할 수 있음 / 코드와 일치 / 절 안에서 조건·답을 읽을 수 있음 / 다른 절의 표·설명 복제 없음.
| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L783 | 대상 서버는 simulator worker-upstream이다. | nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/api/UpstreamWorkerInternalController.java @ 5e9b02e3ae16 ; UpstreamWorkerInternalController L22~39 | 업스트림 worker controller의 주입·중단 및 서비스 호출·응답 확인 |
| L783 | 경로가 같은 설비 worker와 호출 대상을 구별한다. | nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/api/UpstreamWorkerInternalController.java @ 5e9b02e3ae16 ; UpstreamWorkerInternalController L22~39 | 업스트림 worker controller의 주입·중단 및 서비스 호출·응답 확인 |
| L787 | \| POST /internal/inject \| UpstreamInjectRequest \| 200 UpstreamInjectResponse \| | nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/api/UpstreamWorkerInternalController.java @ 5e9b02e3ae16 L31~34 | public UpstreamInjectResponse inject(@RequestBody final UpstreamInjectRequest request)의 매핑·입력 바인딩·호출·반환문을 대조했다. |
| L788 | \| POST /internal/abort \| 업스트림 worker의 AbortRequest.runId \| 본문 없는 204 \| | nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/api/UpstreamWorkerInternalController.java @ 5e9b02e3ae16 L36~40 | public ResponseEntity<Void> abort(@RequestBody final AbortRequest request)의 매핑·입력 바인딩·호출·반환문을 대조했다. |

## 매뉴얼과 코드가 다른 곳
| 항목 | 매뉴얼 | 코드 근거 | 본문 반영 |
|---|---|---|---|
| 비동기 조회 상태 | I §4.6 HTTP-UI-001(L194)은 성공을 HTTP 200으로 적는다. | AsyncResultController.java @ 985d17c604dd L106~107은 terminal 여부로 200/202를 선택한다. AsyncResultControllerTest.java L113~143의 기대값도 확인했다. | 진행 중 202·종결 200을 명시했다. |
| 실패 본문 예외 | I §3 L110은 ui-backend 실패를 ApiResponse로 설명한다. | AsyncResultController.java @ 985d17c604dd L85~89는 없는 작업에 notFound().build()를 반환한다. | 본문 없는 404를 명시했다. |
| 통신 로그 type | I §4.6 HTTP-UI-007(L200)은 eqp·mes·all 세 값만 쓴다고 설명한다. | CommLogController.java @ 985d17c604dd L38~44는 eqp·mes 이외 값을 default 전체 조회로 처리한다. | 미지원값을 거부한다고 쓰지 않고 실제 default 분기를 적었다. |
| 설비 worker의 대상 설명 | I §4.7 HTTP-SIM-011·013(L321·323)은 descript에서 업스트림 worker라고 부른다. | worker-equipment의 EquipmentWorkerInternalController.java @ 5e9b02e3ae16 L21~43은 EquipmentWorkerService를 호출한다. worker-upstream에는 별도 UpstreamWorkerInternalController가 있다. | 서버별 절을 나누고 Equipment/Upstream 요청·결과 타입을 구별했다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할
| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장의 실제 HTTP 호출 주소·호스트·포트 | 현장에 접속하지 않았고 실제 배포 입력을 확보하지 않았다. 상대 경로만 썼다. | TC 배포·네트워크 운영 담당 |
| 현장 배포 판본·API 제공 여부·응답 실측 | 로컬 HEAD 소스 조사이며 현장 판본과 실행 응답을 확인하지 않았다. | TC 배포 운영 담당 |
| 현장 사용자별 API 권한 부여 결과 | DB나 서버에 접속하지 않았다. 인가 알고리즘만 확인했다. | 계정·권한 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실
| 소유 objectKey | 본문에서 복제하지 않은 사실 |
|---|---|
| 00.rag/user/permission-code-reference.md | 경로별 필요 권한 코드 표·각 코드의 의미 |
| 00.rag/user/error-message-and-code-reference.md | 코드별 뜻·오류 문구 목록·사용자 조치 |
| 00.rag/user/state-reference.md | 상태 이름별 의미 표 |
| 00.rag/user/login-and-screen-basics.md | 로그인 화면 조작·세션 관련 사용자 흐름 |
| 00.rag/user/equipment-registration-and-settings.md / 00.rag/user/equipment-commands-and-polling.md / 00.rag/user/equipment-model-parameter-and-jar-update.md | 설비 화면 조작·업무별 입력 정책·실행 결과 해석 |
| 00.rag/user/deployment-workflow.md / 00.rag/user/model-registration-and-branching.md / 00.rag/user/model-details-and-flow-editing.md | 배포·모델·Flow 업무 절차 |
| 00.rag/user/demo-execution.md / 00.rag/user/demo-troubleshooting.md | 데모 선택·실행·복구 절차 |
| 00.rag/admin/security-operations.md / 00.rag/admin/configuration-reference.md | 자격 교체·쿠키/통신 설정의 전체 설정표·실제 프록시 배치 |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | 소유권 조회·복구 필요 작업의 운영 해석 |
- 기존 문서와의 대조: 현재 문서군의 HTTP·/api/·ApiResponse·CSRF·쿠키가 등장하는 관련 절을 읽고, 위 소유 범위의 세부 절차·코드/상태 표를 새 표로 복제하지 않았다. 같은 경로가 운영 절차에도 나오는 경우 본문에는 인터페이스 식별과 타입만 남겼다.
- permission-code-reference.md는 현재 문서군에 아직 없다. 확정 목록의 후속 문서 objectKey를 안내한 것이며, 존재하는 문서를 읽었다고 기록하지 않았다.

## 최초 작성 검사 결과(보완 전 이력)
- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/http-api-reference.md`
- 최종 검사 종료코드: 0. 대상 위반 0, CROSS VIOLATION 0, MASK 출력 0. 검사기와 실행 스크립트는 수정하지 않았다.
- 최종 출력 마지막 10줄:
```text
    L726-726 표 행 85  | 호출=POST /internal/inject | 입력=EquipmentInjectRequest | 결과=200 EquipmentInjectResponse
    L727-727 표 행 83  | 호출=POST /internal/plc/inject | 입력=PlcInjectRequest | 결과=200 EquipmentInjectResponse
    L728-728 표 행 73  | 호출=POST /internal/abort | 입력=설비 worker의 AbortRequest.runId | 결과=본문 없는 204
§ L730-740 [24] 업스트림 worker의 내부 주입·중단 경로   parent=226 children=3 sources=1
    L735-735 문단 66  | 대상 서버는 simulator worker-upstream이다. 경로가 같은 설비 worker와 호출 대상을 구별한다.
    L739-739 표 행 83  | 호출=POST /internal/inject | 입력=UpstreamInjectRequest | 결과=200 UpstreamInjectResponse
    L740-740 표 행 75  | 호출=POST /internal/abort | 입력=업스트림 worker의 AbortRequest.runId | 결과=본문 없는 204
SUMMARY sections=57 children=194 maxChild=169 maxParent=420
CORPUS docs=46/53 sections=757 part={admin=12, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=8, 트러블슈팅=12}
TOTAL files=46 targets=1 violations=0
```
- 별도 매핑 대조: HTTP-UI 컨트롤러의 112개, HTTP-SIM 컨트롤러의 15개 매핑 모두 본문에서 확인했다. /internal/inject·abort는 대상 서버별로 구별했다.
- 사람 검사: 최종 57절 전체를 읽었다. 절마다 제목·사실·독립 답·중복을 판정한 결과는 사실 대조 표 위에 남겼다. 제목의 불필요한 반복 틀, 빈 틀 문장, 이름 토막 뜻풀이, 오답 사례, 관리자 전용 사실의 user 문서 이동은 없었다.
- 본문 필드·숫자·상태·타입·경로는 인용 코드의 record·메서드·분기와 대조했다. Markdown 인코딩·NFC·BOM/CR/NUL·제목·표·마스킹은 검사기 결과와 Python 바이트 검사로 확인했다.
- 제품 테스트는 실행하지 않았다. 문서 작성 자체검사이며, 메인 세션의 독립 사실 검증이나 현장 API 실측을 대신하지 않는다.
- 읽은 관련 시험: AsyncResultControllerTest의 503/진행 202/종결 실패 200/부재 404 기대값과 OrchestratorExceptionAdviceTest의 IllegalArgumentException 400 문자열 기대값. 실행 PASS로 기록하지 않았다.

### 작성 중 발견·수정한 결함
- 첫 파일 저장은 Windows PowerShell의 문자열 파이프 입력 인코딩 때문에 한글이 물음표로 손실됐다. UTF-8 소스 파일을 직접 실행하는 방법으로 작성한 원문을 복원했다. 검사기를 바꾸거나 우회하지 않았다.
- 첫 검사 원문은 `SUMMARY sections=56 children=193 maxChild=215 maxParent=420`, `TOTAL files=46 targets=1 violations=201`이었다. 메타 키 물음표·표 머리글 중복·문단 215자·worker abort 행 중복을 포함했다. 긴 문단을 분리하고 worker별 입력 표기를 구별했다.
- 복원 후 검사 원문: `VIOLATION L615 원천 줄 범위 L34~92 (파일 83줄)` 및 `TOTAL files=46 targets=1 violations=1`. 실제 파일 길이에 맞춰 L34~83으로 고쳤다.
- DLQ 삭제와 통신 로그는 다른 질의이므로 절을 분리했다. 페이지 조회 소비자와 worker 보안 필터 빈 등록의 원천을 추가했다. 최종 원문 57절을 다시 검사했다.
- 읽기 보조 Python 명령 1건은 같은 파이프 인코딩으로 한글 정규식이 물음표가 되어 `re.error: nothing to repeat at position 1`로 실패했다. 파일 변경은 없었으며, 이 UTF-8 파일 기반 기록 스크립트에서 동일한 정확 경로의 status·SHA 대조를 완료했다.
- 작업 디렉터리 생성 명령은 기존 work 디렉터리가 있어 ResourceExists로 끝났다. 기존 디렉터리를 삭제하거나 초기화하지 않고, 본 세션 고유 접두사의 임시 파일만 사용했다.
- 보존 후 정리할 임시 검사 로그: `doc47-check-first.log`, SHA-256 `e33c4a79bd1a04244c3d28cd6f30276b217cab90f77a2cabbab1e656586552f3`. 필요한 원문 오류·최종 결과는 위에 보존했다.
- 보존 후 정리할 임시 검사 로그: `doc47-check-restored.log`, SHA-256 `5274aad416efc9d7a00774522546fa6018eed324bae6cddb1cbc90eeb4c0e7ed`. 필요한 원문 오류·최종 결과는 위에 보존했다.
- 보존 후 정리할 임시 검사 로그: `doc47-check-final.log`, SHA-256 `00c0314dca98f7c0c5e98dbd4bfe1f05949b7dbf4c7525326f7e42272a94c129`. 필요한 원문 오류·최종 결과는 위에 보존했다.

### 자원·미확인·인계
- 독립 Agent 호출 0회. 도구 호출·작성에 대한 provider 사용량과 비용은 확인할 수 없어 미확인이다. 제품 시험·백테스트·배포 비용은 발생시키지 않았다.
- 다음 작업은 메인 세션의 본문·작성 기록 대조다. 이 세션은 업로드나 다른 문서 작업을 시작하지 않는다.
- 정리 대상은 본 세션이 만든 work/doc47-restore.py·doc47-record.py·doc47-check-first.log·doc47-check-restored.log·doc47-check-final.log 다섯 파일뿐이다. 기록 생성 뒤 보호 속성·reparse·활성 프로세스 참조를 확인하고 native PowerShell 비강제 삭제 후 부재를 확인한다. 기존 work 부모는 보존한다.
- 실행 도구: Windows PowerShell 5.1.26100.9444, `$OutputEncoding.WebName=us-ascii`, Python 3.12.10, OpenJDK Temurin 21.0.9+10-LTS, Git 2.52.0.windows.1. PowerShell 기본 파이프 인코딩과 실제 한글 손실을 함께 확인했다.
- 검사기 실제 실행은 3회다. 총 요청 wall time은 별도 계측하지 않았으며 provider 토큰·요금도 미확인이다.
- 정리 전 다섯 파일의 절대경로·소유 용도·허용 Temp 영역 포함 여부를 확인했다. 파일 속성은 모두 Archive이며 보호 속성·reparse가 없었다. work부터 Temp까지 부모 경로의 보호 속성·reparse도 없었다.
- `Get-CimInstance Win32_Process` 명령줄 조회는 `HRESULT 0x80041003` 접근 거부였다. 첫 점검 명령이 출력한 activeReferences=0은 이 실패 때문에 전수 확인 근거로 인정하지 않는다. `Get-Process`로 bash·java 프로세스가 다른 작업에도 남아 있음을 확인했으며 종료하지 않았다. 이 세션의 Python 두 실행과 검사기 세 실행은 이미 종료코드를 회수했고, 다섯 파일 모두 FileShare.None의 배타적 읽기 열기·닫기에 성공했다. 타 세션 프로세스의 전체 명령줄은 미확인이다.
- 정리 완료: 아래 work 절대경로 안의 지정 다섯 파일만 native PowerShell `Remove-Item -LiteralPath <정확한 파일> -ErrorAction Stop`으로 삭제했다. Force·Recurse를 쓰지 않았다. 종료코드 0이며 각 `Test-Path -LiteralPath`는 False, work 잔여 파일 수는 0, 기존 work 부모는 True였다.
- 정리한 work 절대경로: `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-47-http-api-reference/work/`.

## fix-1 직접 반환 오류 보완·중단 후 재개 기록

- 사용자 지적: 예외 처리기의 공통 매핑만으로 컨트롤러의 직접 반환 분기를 찾을 수 없었다. 직접 반환하는 경로·조건·HTTP 상태·코드를 해당 절과 인접 오류 절에 추가했다. 코드의 의미·사용자 조치는 기존 소유 문서에 맡겼다.
- 최초 작성 원문은 SHA-256 `ed6bd60a4518cecf625f9d15edd7367cd04bd777695dbf3ac9fad5a6c0c54b03`, 740줄·57절이었다. 위 최초 작성 검사·도구·임시 자원 기록은 당시 이력이며 현재 검사 실행 경로를 뜻하지 않는다.
- 재개 시 원문은 SHA-256 `62935243740abadbecf36d746ccc2b996ad4428227a26813e36dfb2658d90675`, 785줄·59절로 보완돼 있었다. 작성 기록은 SHA-256 `2dd241b6df56ce37438b7854278eec311a0a5073d6bf22af9354a77333b61373`인 최초 작성본에 머물러 있었다.
- 재개 후 두 파일 전체를 처음부터 읽었다. 원문의 보완 내용은 지정 커밋과 일치해 보존했다. 재개 후 검색 원문의 바이트 변경은 없다. 작성 기록은 238개 기존 사실 행의 위치와 보완된 두 문장을 맞추고, 추가 사실 행·두 오류 절·최종 수치를 동기화했다.
- 원천 확인 명령: `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-backend -C C:/workspace_nori/nori-tc/nori-tc-ui-backend show 985d17c604dd:ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/<컨트롤러>.java`.
- 재대조한 컨트롤러: EqpController·EqpParamController·CoordinationAdminController·AuthController·AsyncResultController·GroupController·PermissionController·UserController·ModelQueryController·ModelMutationController·ModelDetailController. 실제 git show 명령은 파일마다 실행하고 각각 종료코드 0을 확인했다. controller/ 경로의 `git status --short -- <경로>` 출력도 비어 있었다.
- ModelDetailController 모델 부재는 일반 상세 저장 L64~68, MDF 업로드 L139~143이다. EDIT 거부는 L70~72·L145~147, mdf 거부는 L75~78, workflow 검증 실패는 L96~104, 지원 노드 검사는 L123~125다. ModelQueryController 상세 조회의 부재·노드 오류는 L75~90이다.
- 인증 오류 절은 AuthController의 인증 실패 catch, 로그아웃 세 거부 분기·쿠키 삭제 헬퍼, me의 두 거부 분기를 대조했다. 나머지 추가된 404·409·조정 조회 코드도 각 사실 행에 직접 반환 위치를 기록했다.
- 경로·메서드 집합은 기존 사실 행과 최종 원문이 모두 125개이며 양방향 차이는 0이다. 기존 성공 응답 표의 모든 행을 보존했다. 공통 계약·HTTP-SIM 원문은 재개 후 변경하지 않았다. 450자 제한 때문에 인증 오류와 모델 상세 오류를 인접 절로 나눈 기존 보완을 유지했다.
- 사람 검사: 재개 후 원문 59절을 모두 읽고, 변경 영향 13절의 직접 반환 조건을 다시 대조했다. 추가된 문장·표·목록은 사용처와 연결되며, 코드 뜻풀이·사용자 조치·빈 틀 문장을 늘리지 않았다. 제품 코드·다른 문서·설정은 고치지 않았다.

### 재개 후 최종 검사

- 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/admin/http-api-reference.md`
- 재개 후 검사기 실행은 2회다. 최종 실행은 종료코드 0이며 대상 위반·CROSS VIOLATION·MASK는 모두 0건이다. 검사기와 ContentSanitizer를 수정하지 않았다.
- 첫 실행은 원천 조회 111건을 `원천 commit:path 없음`으로 보고하고 종료코드 1이었다. 새 check.sh에는 옛 실행 스크립트의 Git safe.directory 환경 지정이 없었다. 같은 저장소의 plain git show도 `fatal: detected dubious ownership in repository at 'C:/workspace_nori/nori-tc/nori-tc-ui-backend'`와 종료코드 128로 거부됐다. checker 소스는 Git 비정상 종료를 원천 경로 부재로 표시한다(RagDocCheck.java L385~390·L404~412). 이 실패를 문서 합격 근거로 쓰지 않았다.
- 최종 실행은 해당 PowerShell 프로세스에만 `GIT_CONFIG_COUNT=2`, `GIT_CONFIG_KEY_0=safe.directory`, `GIT_CONFIG_VALUE_0=C:/workspace_nori/nori-tc/nori-tc-ui-backend`, `GIT_CONFIG_KEY_1=safe.directory`, `GIT_CONFIG_VALUE_1=C:/workspace_nori/nori-tc/nori-tc-simulator`를 지정한 뒤 같은 검사 명령을 실행했다. 읽는 두 저장소의 정확한 경로만 지정했으며 전역 Git 설정·검사 코드·검사 조건을 변경하지 않았다.
- 다음은 이번 최종 출력 마지막 10줄이다.
```text
    L771-771 표 행 85  | 호출=POST /internal/inject | 입력=EquipmentInjectRequest | 결과=200 EquipmentInjectResponse
    L772-772 표 행 83  | 호출=POST /internal/plc/inject | 입력=PlcInjectRequest | 결과=200 EquipmentInjectResponse
    L773-773 표 행 73  | 호출=POST /internal/abort | 입력=설비 worker의 AbortRequest.runId | 결과=본문 없는 204
§ L775-785 [24] 업스트림 worker의 내부 주입·중단 경로   parent=226 children=3 sources=1
    L780-780 문단 66  | 대상 서버는 simulator worker-upstream이다. 경로가 같은 설비 worker와 호출 대상을 구별한다.
    L784-784 표 행 83  | 호출=POST /internal/inject | 입력=UpstreamInjectRequest | 결과=200 UpstreamInjectResponse
    L785-785 표 행 75  | 호출=POST /internal/abort | 입력=업스트림 worker의 AbortRequest.runId | 결과=본문 없는 204
SUMMARY sections=59 children=212 maxChild=169 maxParent=449
CORPUS docs=50/53 sections=868 part={admin=14, user=36} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=50 targets=1 violations=0
```
- 최종 원문 SHA-256: `62935243740abadbecf36d746ccc2b996ad4428227a26813e36dfb2658d90675`. 최종 줄 수 785, 절 수 59. 사실 대조 행은 258개다.
- 임시 검사 로그 SHA-256: `4df79621c92319afcc05b6e358d67ebee835c802d99aa0427ca5690231092ff4`. 필요한 최종 출력은 위에 보존했다.
- 메인 세션의 기존 경로·메서드·공통 계약·HTTP-SIM·표본 타입 검증은 사용자 제공 유효 근거로 재사용했다. 이번 검사는 작성자의 보완 자체검사이며 후속 독립 검증 PASS나 제품 API 실측을 뜻하지 않는다.
- 제품 시험·네트워크·Docker·kubectl·ssh·업로드·git add/commit/push·타 프로세스 종료는 수행하지 않았다. 독립 Agent 호출 0회. provider 토큰·요금과 총 요청 wall time은 미측정이다.
- 옛 scratchpad 폴더와 임시 파일은 사용자 안내대로 없어졌으므로 재생성하거나 삭제하지 않았다. 현재 임시 자원은 `C:/workspace_nori/nori-tc/rag-step3-work/doc-47-http-api-reference/work/` 안의 `resume-update-record.py`·`resume-check.log`·`resume-check-final.log` 세 파일뿐이다. 필요한 근거를 이 기록에 보존한 뒤 소유·절대경로·속성·reparse·프로세스 사용 여부를 확인하고 비강제 native PowerShell로 정리한다.
- 첫 실패 로그 SHA-256: `905722c0812b5b606de8b231b8dd32dfddbb75f94980cbcee61f1e94717bc116`. 요약 원문: `TOTAL files=49 targets=1 violations=111`.
- 재개 후 정리 완료: 지정 work 안의 위 세 파일은 모두 Archive 속성이며 보호 속성·reparse가 없었다. work에서 허용된 rag-step3-work 루트까지 부모도 확인했다. 자체 검사기·Python의 종료코드를 회수한 뒤 세 파일 모두 FileShare.None 배타적 읽기에 성공했다.
- 세 파일을 native PowerShell `Remove-Item -LiteralPath <정확한 파일> -ErrorAction Stop`으로 비강제 삭제했다. 종료코드 0, 파일별 Test-Path=False, work 잔여 0개, 기존 work 부모 보존을 확인했다. 최종 기록의 258개 사실 행 모두 최종 원문 줄·본문과 일치하며 UTF-8·LF·NFC·BOM/CR/NUL 검사도 통과했다.

## 2026-11-19 제품 수정 D 반영

- 범위: 문서 47의 모델 상세 PUT 오류 계약과 그 도입문·H1 적용 판본·절 원천·작성 기록만 보완했다. 기존 성공 응답 표와 125개 경로·메서드 집합, 나머지 58절의 원문은 그대로다.
- 원인: PLC 순서 검증을 저장 경계에 추가하면서 허용값 위반이 UiBadRequestException으로 공통 처리기에 전달된다. 이 오류를 기존의 '컨트롤러 직접 반환' 도입문 아래에 그대로 넣으면 발생 경로가 틀려지므로, 도입문도 API 오류 응답 표현으로 맞췄다.
- 전체 원문 59절을 읽고 A~D의 영향을 확인했다. A의 Kafka 클라이언트 설정 전달, B의 Secret 생성 입력·linger, C의 Gateway 설비별 HSMS·Socket 실행 설정을 설명하는 문장은 이 문서에 없다. 따라서 이 문서에서 A~C로 바뀌는 문장은 없으며 해당 제품 변경을 조사·구현 범위로 늘리지 않았다.
- D 사실 확인: ModelDetailController.saveDetailRows L107~115가 저장 포트를 부른다. 현재 JpaModelDetailCommandPort.saveDetailRows L150~175의 plc-register 분기가 replacePlcRegisters를 호출하고, L494~497이 byteOrder·wordOrder를 정규화한다. PlcRegisterOrder.normalizeOptional L12~22는 빈 값을 null로 두고, 앞뒤 공백 제거·대문자 변환 후 두 enum 값 밖이면 UiBadRequestException을 던진다. L527~528의 기존 행 삭제·저장은 모든 검사 뒤다. UiApiExceptionHandler.handleUiBadRequest L89~98은 이 예외를 400 INVALID_REQUEST로 응답한다.
- 오류 문구도 PlcRegisterOrder.java L19~20에서 확인했다. 본문에는 소유 범위에 맞춰 상태·코드·입력 조건만 추가하고 문구 목록·뜻풀이·조치는 복제하지 않았다. 목록 철자·기본값·화면 선택·DB 열 길이·복사 경로를 이 HTTP 참조에 새로 늘리지 않았다.
- 관련 시험은 JpaModelDetailCommandPortTest.java @ sha256=8e52233b322fc24aebb446d39ad06128d9af053d5f481401ef238286dcf2749e L544~587을 읽었다. 대소문자·공백 정규화, 빈 값 null, 각 순서 칸의 목록 밖 값 거절 및 delete/upsert 미호출 기대값을 확인했다. 제품 시험은 실행하지 않았으며 메인 세션이 제공한 diff·시험 검증과 구별했다.
- 다른 문서 확인: user/plc-modbus-register-and-type-reference.md의 순서 값 입력·저장 제한 절과 user/model-details-and-flow-editing.md의 PLC Register 입력 절에는 이미 D의 현재 SHA 원천과 저장 전 거부가 반영돼 있었다. 이 세션에서 다른 문서를 고칠 필요가 있는 불일치는 확인하지 않았다.
- 제품 파일·다른 문서·작성 규칙·검사기는 수정하지 않았다. 현재 제품 작업 트리와 Gateway 저장소의 기존 사용자 변경도 보존했다.

### 이번 최종 검사

- 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/admin/http-api-reference.md`
- 앞선 환경 진단을 재사용하여 실행 프로세스에만 GIT_CONFIG_COUNT=2와 safe.directory 두 값(nori-tc-ui-backend·nori-tc-simulator의 정확한 절대경로)을 지정했다. 전역 Git 설정이나 검사 코드는 바꾸지 않았다.
- 검사기 실행 1회, 최종 종료코드 0. 대상 위반·CROSS VIOLATION·MASK 0건이다. 마지막 편집 후 실행했으며 다음은 원문 마지막 10줄이다.
```text
    L774-774 표 행 85  | 호출=POST /internal/inject | 입력=EquipmentInjectRequest | 결과=200 EquipmentInjectResponse
    L775-775 표 행 83  | 호출=POST /internal/plc/inject | 입력=PlcInjectRequest | 결과=200 EquipmentInjectResponse
    L776-776 표 행 73  | 호출=POST /internal/abort | 입력=설비 worker의 AbortRequest.runId | 결과=본문 없는 204
§ L778-788 [24] 업스트림 worker의 내부 주입·중단 경로   parent=226 children=3 sources=1
    L783-783 문단 66  | 대상 서버는 simulator worker-upstream이다. 경로가 같은 설비 worker와 호출 대상을 구별한다.
    L787-787 표 행 83  | 호출=POST /internal/inject | 입력=UpstreamInjectRequest | 결과=200 UpstreamInjectResponse
    L788-788 표 행 75  | 호출=POST /internal/abort | 입력=업스트림 worker의 AbortRequest.runId | 결과=본문 없는 204
SUMMARY sections=59 children=212 maxChild=186 maxParent=449
CORPUS docs=51/53 sections=873 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```
- 최종 검색 원문 SHA-256 `8f1fcbf96885a256c0ca51dd8d30df23529c0165f41c29310649e8fbfaeccf95`, 줄 수 788, 절 수 59, 사실 대조 258행이다. 기존 사실 대조 행은 위치를 맞추고, 변경된 두 행만 새 조건·원천으로 보완했다.
- 보완 전 검색 원문 SHA-256 `62935243740abadbecf36d746ccc2b996ad4428227a26813e36dfb2658d90675`, 작성 기록 SHA-256 `a168a41d78067e941a6a138c78c3c8888d06f2ae7196f1551a8147dbc84d79ed`를 기준으로 diff와 나머지 절·표 보존을 확인했다.
- 검사 로그 SHA-256 `e93f71155b6cb9cccc951d68f1a3fd5f14f4a0affcb5e601c148fc75aa386ec1`. 필요한 결과는 위에 보존했다. UTF-8·LF·NFC 및 BOM/CR/NUL 부재도 확인했다.
- 사람 검사: 바뀐 오류 절의 제목·독립 답·코드 조건·원천·기존 문장과의 정합성을 대조했다. PLC 순서 거부는 공통 예외 처리 경로이며, 기존 404·INVALID_NODE·workflow 직접 반환 계약을 바꾸지 않았다.
- 읽기 명령 두 건이 PowerShell 프로세스 생성 전에 CreateProcessAsUserW failed: 5 (액세스가 거부되었습니다.)로 시작되지 않았다. 종료코드·제품 동작으로 해석하지 않았다. 허가된 읽기를 짧은 명령으로 나누어 정상 실행했다. 거부 원인이 명령 길이나 특정 구문이라고 확정하지 않는다.
- 독립 Agent 호출 0회, 제품 시험·네트워크·업로드·Git 쓰기 0회다. 총 wall time·provider 사용량·요금은 미측정이다. 다음 작업은 메인 세션의 보완 확인이며 추가 제품·문서 작업은 시작하지 않는다.
- 소유 임시 파일은 현재 work 안의 product-d-before-source.md·product-d-before-record.md·product-d-record.py·product-d-check.log 네 파일이다. 결과·최종 해시를 보존한 뒤 소유·절대경로·보호 속성·reparse·프로세스 사용을 확인하고 native PowerShell로 비강제 삭제한다.
- 최종 사실 대조 258행의 문장이 지정된 원문 줄에 실제로 있는지 다시 검사했고 모두 일치했다. 검사 후 원문 SHA-256도 위 최종값과 같았다. 두 결과 파일의 UTF-8·LF·NFC를 확인했다.
- 위 대조용 최초 python -c 명령은 PowerShell 인자 전달에서 따옴표가 보존되지 않아 SyntaxError로 종료됐다(exit 1). 파일 변경은 없었으며, 같은 ASCII 검사 코드를 here-string 표준입력으로 전달해 정상 완료했다(exit 0).
- 정리 완료: work 및 바로 위 부모는 일반 디렉터리였고 네 소유 파일은 Archive 속성뿐이었다. 보호 속성·reparse가 없고 각 파일의 FileShare.None 읽기 열기가 성공했으며, 소유 검사기·Python 프로세스의 종료를 확인했다. 위 절대경로 네 파일에 Remove-Item -LiteralPath를 비강제 실행해 exit 0, 각 Test-Path=False를 확인했다. 기존 work 디렉터리는 보존했다.

## 2026-11-19 보완 3: 모델 상세 PUT의 행 입력 오류 범위

- 원인과 수정: 앞선 PUT 설명은 공통 입력 오류의 예를 PLC 순서 값으로 좁혀 적었다. 이번에는 workflow 검증과 노드별 행 입력 검사 실패를 400 INVALID_REQUEST의 원인으로 명시하고, 응답 메시지에 사유가 담긴다는 결과를 추가했다. 본문 예시는 필수 칸·형식·PLC 그룹·순서 값이며 전체 검사를 열거한 목록이 아니다.
- 실제 변경은 검색 원문 L537의 JpaModelDetailCommandPort 원천 줄 범위와 L545 PUT 오류 한 줄뿐이다. H1 적용 메타·다른 원천·도입 문장·다른 절·표는 그대로 보존했다. 줄 수 788·절 수 59·125개 경로/메서드 집합도 바뀌지 않았다. 동일 PUT 오류의 중복 설명을 문서 안에서 검색했으며 별도 수정할 곳은 없었다.
- 원천 판본 재확인: nori-tc-ui-backend의 JpaModelDetailCommandPort.java는 git status에서 수정 상태였고 SHA-256은 aa567bd80d4277dc0839572ed14fb63a0337e2a50ea415074448045488c41d74로 앞선 보완과 같다. ModelDetailController.java·UiApiExceptionHandler.java는 해당 경로의 git status 출력이 비어 있었고 기존 985d17c604dd 원천을 유지했다. 관련 읽기 전용 Git 명령은 exit 0이며 사용자 ignore 파일 접근 경고가 있었다.
- 직접 읽은 경로: ModelDetailController.saveDetailRows L59~125는 workflow 검증 메시지를 직접 400 INVALID_REQUEST로 반환하고 저장 포트를 호출한다. JpaModelDetailCommandPort.saveDetailRows L150~179가 노드별 replace 메서드를 호출한다. L211~528에서 requiredCell·정수/불리언/열거값 파싱·PLC 그룹 참조·순서 값·영역/자료형·레이아웃 검사를 확인했다. L542~735의 행 ID·필수 문자열·정수/실수/불리언/열거값 파싱은 UiBadRequestException으로 거부한다. 공통 처리기 L89~98은 ApiResponse.error(INVALID_REQUEST, exception.getMessage())를 반환한다.
- 사실 대조 표의 L545 두 문장을 현재 본문과 맞추고, 해당 절의 도입문 및 입력 오류 행에 확장한 원천 범위를 반영했다. 본문에 21곳 같은 구현 수량·오류 문구·사용자 조치는 추가하지 않았다. 오류 뜻과 조치 소유 문서는 기존 00.rag/user/error-message-and-code-reference.md를 유지한다.
- 보완 2의 결과 JSON에서 작성 기록 SHA를 옮겨 적을 때 c3 두 글자가 중복됐음을 이번 기준선 대조에서 발견했다. 현재 파일과 이번 보완 전 복사본의 실제 SHA는 b864229c31c4cca3128943a0acc87cb5c8c3dffff4e0a892077c1309ce07afed이다. 첫 작성 기록 갱신 미리보기는 잘못 옮긴 SHA와 달라 AssertionError(exit 1)로 중단됐고 파일 쓰기는 없었다. 기준선 검사 상수를 실제 해시와 맞춘 뒤 다시 대조했다.
- 관련 시험은 JpaModelDetailCommandPortTest.java @ sha256=8e52233b322fc24aebb446d39ad06128d9af053d5f481401ef238286dcf2749e L207~251·L419~507을 읽었다. workflow 행 ID의 부재/숫자 형식, PLC 그룹 이름 중복/형식, 레이아웃 및 그룹 부재 예외 기대값을 확인했다. 제품 시험 실행을 주장하지 않는다.
- 사람 검사: 변경 절만으로 PUT의 대상·실패 조건·상태·코드·메시지를 알 수 있고, 순서 값 하나로 원인을 제한하지 않는다. 응답 사유를 구현의 실제 반환 메시지와 대조했다. 본문 예시·공통 오류 절·다른 절의 역할이 겹치지 않으며 불필요한 절·조작 절차·뜻풀이를 추가하지 않았다. 다른 문서 수정이 필요한 불일치는 확인하지 않았다.
- 보완 2에서 메인 세션이 검증한 적용 메타·원천·직접 오류 분기는 사용자 제공 근거로 재사용했다. 최초 검사에서 PUT 목록 항목이 직렬화 후 201자로 상한 200자를 넘었다(exit 1, 위반 1건). 의미를 보존하며 '값 형식'을 '형식'으로 줄이고 마지막 검색 원문 편집 뒤 지정 검사기를 다시 실행했다(총 2회). 실행 프로세스의 safe.directory 환경값 두 개만 설정했으며 전역 Git 설정·제품 코드·다른 문서·규칙·검사기는 바꾸지 않았다.

### 최종 검사

- 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/admin/http-api-reference.md`
- 종료코드 0, 대상 위반·CROSS VIOLATION·MASK 0건이다. 마지막 출력 10줄:
```text
    L774-774 표 행 85  | 호출=POST /internal/inject | 입력=EquipmentInjectRequest | 결과=200 EquipmentInjectResponse
    L775-775 표 행 83  | 호출=POST /internal/plc/inject | 입력=PlcInjectRequest | 결과=200 EquipmentInjectResponse
    L776-776 표 행 73  | 호출=POST /internal/abort | 입력=설비 worker의 AbortRequest.runId | 결과=본문 없는 204
§ L778-788 [24] 업스트림 worker의 내부 주입·중단 경로   parent=226 children=3 sources=1
    L783-783 문단 66  | 대상 서버는 simulator worker-upstream이다. 경로가 같은 설비 worker와 호출 대상을 구별한다.
    L787-787 표 행 83  | 호출=POST /internal/inject | 입력=UpstreamInjectRequest | 결과=200 UpstreamInjectResponse
    L788-788 표 행 75  | 호출=POST /internal/abort | 입력=업스트림 worker의 AbortRequest.runId | 결과=본문 없는 204
SUMMARY sections=59 children=212 maxChild=199 maxParent=449
CORPUS docs=51/53 sections=874 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```
- 최종 검색 원문 SHA-256 `40ba6fb6b84e98bfc1cd770a6182ae000fe3597fc9110e00ab231af0b0638c1c`, 줄 수 788, 절 수 59, 사실 대조 258행이다. 모든 사실 행의 문장이 지정된 원문 줄에 실제로 존재하는지 검사했다.
- 보완 전 검색 원문 SHA-256 `8f1fcbf96885a256c0ca51dd8d30df23529c0165f41c29310649e8fbfaeccf95`, 작성 기록 SHA-256 `b864229c31c4cca3128943a0acc87cb5c8c3dffff4e0a892077c1309ce07afed`. 검사 로그 SHA-256 `62fb976602d4269343e35d8d6be60668d77c870e279671d988bb25a50abfbac9`. 필요한 결과는 이 기록에 보존했다.
- 최초 검사 로그 SHA-256 `825a6ff03a666f191488cbc216e3c3bc97fcdce6378eb4c7ec80d25cdecaf508`. 실패 요약: `VIOLATION L545 자식(목록 항목) 길이 201 (1~200)` 및 `TOTAL files=51 targets=1 violations=1`.
- 제품 시험·서버 접속·업로드·Git 쓰기·독립 Agent 호출은 하지 않았다. 총 wall time·provider 사용량·비용은 미측정이다. 현재 작업의 소유 임시 자원은 지정 work의 fix3-before-source.md·fix3-before-record.md·fix3-record.py·fix3-check.log·fix3-check-final.log 다섯 파일이며 증거 보존 뒤 비강제 native PowerShell로 정리한다.
- 최종 확인: 사실 대조 258행의 원문 위치가 모두 일치하고 검사한 원문의 SHA가 유지됐다. 두 결과 파일의 UTF-8·LF·NFC 및 BOM/CR/NUL 부재를 확인했다.
- 정리 완료: 지정 work와 rag-step3-work까지 부모는 일반 디렉터리이며, 위 다섯 소유 파일은 Archive 속성뿐이고 보호 속성·reparse가 없었다. 검사기·Python 종료코드를 회수한 뒤 각 파일의 FileShare.None 읽기 열기가 성공했다. 정확한 절대경로에 native PowerShell Remove-Item -LiteralPath를 비강제 실행해 exit 0, 파일별 Test-Path=False, work 잔여 0개·기존 부모 보존을 확인했다.

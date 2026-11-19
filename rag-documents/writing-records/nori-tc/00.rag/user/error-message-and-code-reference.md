# 오류 메시지·코드 참조 작성 기록

## 대상
- objectKey: `00.rag/user/error-message-and-code-reference.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 참조
- 검색 원문 SHA-256: `9c34fe65d58e9193b0c1005bd5b9f7ccd86f943e66845e8802e6a9e6d50f7e66`
- 검색 원문 줄 수: 474
- 검색 원문 절 수: 34
- 확인일: 2026-11-19 (한국 시간)
- 작성 범위: 최초 작성은 두 파일 생성, 이번 D 보완은 같은 두 파일만 수정했다. 업로드·커밋·제품 수정은 수행하지 않았다.

## 원천 판본

| 저장소 | HEAD 12자리 | 상태 확인 결과 |
|---|---|---|
| nori-tc-ui-backend | 985d17c604dd | ui 범위 및 인용 파일 28개를 지정한 status 조회 exit 0, 변경 출력 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | src·tests 범위 및 인용 파일 19개를 지정한 status 조회 exit 0, 변경 출력 없음 |
| nori-tc-exec | f1d5e0a8e727 | U 원천 파일을 지정한 status 조회 exit 0, 변경 출력 없음 |
| nori-ai-gateway | 70cf6cd84db6 | 작성 규칙·목록 파일 status 변경 출력 없음. 작업 시작 main ahead 8, 기존 rag-documents/는 untracked였다 |

원천 조회 명령은 `git -c safe.directory=<정확한 저장소 절대경로> -C <저장소> status --short -- <인용 파일 목록>`을 사용했다. 전역 Git 설정은 바꾸지 않았다. 최초 일반 Git 조회는 소유자 불일치로 실패했고, 해당 프로세스의 읽기 전용 경로 신뢰 설정으로 조회했다. 사용자 홈 ignore 파일 읽기 경고가 있었지만 상태 조회 exit 0과 변경 출력 없음을 구별해 기록한다.

필수 선독은 작성 규칙 전체, TC 목록 전체, U §10.1 L2513~2533, 관련 코드다. 처음 Get-Content의 기본 인코딩 출력이 깨져 UTF-8로 다시 읽었다. rg가 없어 Git grep·ls-files와 Python/PowerShell로 범위를 제한해 조회했다.

추가 대조한 기대값은 UiApiExceptionHandlerI18nTest L34~49·L64~68, UiApiExceptionHandlerStandardMvcTest L51~113이다. 테스트 코드를 읽었으며 제품 테스트를 실행한 것으로 기록하지 않는다.

이번 보완의 HEAD는 백엔드 985d17c604dd·프런트 9fe69a75dcd3이다. 위 변경 없음 기록은 최초 작성 당시 상태다. 이번에는 아래 파일의 현재 바이트를 직접 읽고 경로별 status를 확인했다. 변경되지 않은 기존 사실의 commit 원천은 보존했다.

| 추가 원천 | 상태 | SHA-256·현재 줄 |
|---|---|---|
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java | M | aa567bd80d4277dc0839572ed14fb63a0337e2a50ea415074448045488c41d74 ; saveDetailRows L150~174·replacePlcRegisters L477~528 |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java | ?? | 241e8e34c68b25713a47362cabe3e7574400445f71e33dc4ad1aa7c47d716d6a ; normalizeOptional L12~20 |
| nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx | M | 14e703baba62a7ee57c83b9d09981b48827689e9fa2c84403a01d789f704e026 ; 순서 선택 L65~81·L562~645 |

ModelDetailController.java·UiApiExceptionHandler.java·ModelPage.tsx·model.api.ts는 경로별 status 변경 출력이 없었다. 조회 exit 0이며 사용자 홈 ignore 읽기 경고는 별개다.

## 절별 사실 대조

각 표는 검색 원문의 모든 본문 문장 또는 표 자료 행을 해당 절의 코드·문구 근거와 연결한다. 인계·재조회 권고는 제품의 자동 동작을 주장하는 문장이 아니라 확인된 오류의 주체와 사용자가 보존할 값을 바탕으로 쓴 사용자 조치다. objectKey 연결은 확정 목록의 소유 범위와 관련 기존 문서를 대조했다. 같은 행 안의 여러 문장은 표 행 전체의 단일 조건·결과 묶음으로 대조했다.

### 인증 거부 UNAUTHORIZED

로그인 use case의 계정 부재·비밀번호 불일치·계정 상태 분기와 401 응답, 로그인 폼 표시를 구별했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L17 | UNAUTHORIZED는 서버의 인증 거부 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~115 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L130~139 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; commence L68~80 ; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 서버 오류 표시 L294~298 | 로그인 use case의 계정 부재·비밀번호 불일치·계정 상태 분기와 401 응답, 로그인 폼 표시를 구별했다. |
| L17 | 로그인에서는 다음 문구를 구별한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~115 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L130~139 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; commence L68~80 ; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 서버 오류 표시 L294~298 | 로그인 use case의 계정 부재·비밀번호 불일치·계정 상태 분기와 401 응답, 로그인 폼 표시를 구별했다. |
| L21 | \| 아이디 또는 비밀번호가 올바르지 않습니다. \| 계정 미존재 또는 비밀번호 불일치다. 입력한 아이디와 비밀번호를 확인한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~115 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L130~139 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; commence L68~80 ; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 서버 오류 표시 L294~298 | 로그인 use case의 계정 부재·비밀번호 불일치·계정 상태 분기와 401 응답, 로그인 폼 표시를 구별했다. |
| L22 | \| 사용 불가능한 계정입니다. \| 계정 상태가 ACTIVE가 아니다. 운영 관리자에게 계정 상태 확인을 요청한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~115 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L130~139 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; commence L68~80 ; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 서버 오류 표시 L294~298 | 로그인 use case의 계정 부재·비밀번호 불일치·계정 상태 분기와 401 응답, 로그인 폼 표시를 구별했다. |
| L23 | \| 인증이 필요합니다. \| 보호 요청에 인증이 성립하지 않았다. 로그인 상태를 확인한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~115 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L130~139 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; commence L68~80 ; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 서버 오류 표시 L294~298 | 로그인 use case의 계정 부재·비밀번호 불일치·계정 상태 분기와 401 응답, 로그인 폼 표시를 구별했다. |
| L25 | 점검 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~115 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L130~139 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; commence L68~80 ; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 서버 오류 표시 L294~298 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 현재 사용자 조회·인증 처리의 UNAUTHORIZED 문구

현재 사용자 조회의 인증 정보·principal 검사와 인증 필터의 예외 응답을 각각 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L35 | \| 인증 정보가 유효하지 않습니다. \| 현재 사용자 조회에서 인증 정보가 없거나 사용자 정보의 형식이 맞지 않는다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; me L212~228 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; 검증 예외 L196~200 | 현재 사용자 조회의 인증 정보·principal 검사와 인증 필터의 예외 응답을 각각 대조했다. |
| L36 | \| 인증 처리 중 오류가 발생했습니다. \| 인증 검증 중 예상하지 못한 예외가 발생했다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; me L212~228 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; 검증 예외 L196~200 | 현재 사용자 조회의 인증 정보·principal 검사와 인증 필터의 예외 응답을 각각 대조했다. |
| L38 | 두 문구는 UNAUTHORIZED와 401로 응답한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; me L212~228 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; 검증 예외 L196~200 | 현재 사용자 조회의 인증 정보·principal 검사와 인증 필터의 예외 응답을 각각 대조했다. |
| L38 | 로그인 중 비밀번호 불일치 문구와 구별한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; me L212~228 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; 검증 예외 L196~200 | 현재 사용자 조회의 인증 정보·principal 검사와 인증 필터의 예외 응답을 각각 대조했다. |
| L38 | 확인 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; me L212~228 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; 검증 예외 L196~200 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 로그인 보안 요청이 403으로 거부된 코드

403 변환은 로그인 전 토큰 발급과 로그인 제출 두 문맥으로 분리했다. 문구의 첫 문장만 인용했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L46 | 로그인 요청의 403을 화면에서 다음 코드로 바꾼다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L840~841 | 403 변환은 로그인 전 토큰 발급과 로그인 제출 두 문맥으로 분리했다. 문구의 첫 문장만 인용했다. |
| L46 | 문구는 첫 문장을 적는다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L840~841 | 403 변환은 로그인 전 토큰 발급과 로그인 제출 두 문맥으로 분리했다. 문구의 첫 문장만 인용했다. |
| L50 | \| LOGIN_CSRF_FORBIDDEN \| 「로그인 보안 토큰 발급이 차단되었습니다.」: 로그인 전 보안 토큰 발급 요청이 403이다. \| | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L840~841 | 403 변환은 로그인 전 토큰 발급과 로그인 제출 두 문맥으로 분리했다. 문구의 첫 문장만 인용했다. |
| L51 | \| LOGIN_FORBIDDEN \| 「로그인 보안 검증에 실패했습니다.」: 로그인 제출 요청이 403이다. \| | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L840~841 | 403 변환은 로그인 전 토큰 발급과 로그인 제출 두 문맥으로 분리했다. 문구의 첫 문장만 인용했다. |
| L53 | 페이지를 새로고침한 뒤 재시도하고, 계속되면 발생 시각과 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L840~841 | 403 변환은 로그인 전 토큰 발급과 로그인 제출 두 문맥으로 분리했다. 문구의 첫 문장만 인용했다. |
| L53 | 확인 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L840~841 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 로그아웃 보안 거부와 보안 토큰 누락

로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L65 | \| LOGOUT_CSRF_FORBIDDEN \| 로그아웃 전 발급 요청이 403이다. 「로그아웃 보안 토큰 발급이 차단되었습니다.」 \| | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218 ; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다. |
| L66 | \| LOGOUT_FORBIDDEN \| 로그아웃 제출 요청이 403이다. 「로그아웃 보안 검증에 실패했습니다.」 \| | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218 ; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다. |
| L67 | \| CSRF_TOKEN_MISSING \| 발급 요청 뒤 브라우저에서 토큰을 읽지 못했다. 「CSRF 토큰을 확인할 수 없습니다.」 \| | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218 ; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다. |
| L69 | 페이지를 새로고침하고 다시 시도한다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218 ; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다. |
| L69 | 계속되면 작업·시각·문구만 전달하고 토큰 값은 전달하지 않는다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218 ; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다. |
| L69 | 조작은 `00.rag/user/login-and-screen-basics.md`를 따른다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218 ; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |
| L71 | 로그아웃 코드는 내부에서 생성하지만 현재 상단바의 로그아웃 실패는 화면에 표시하지 않는다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218 ; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다. |

### 권한 거부 FORBIDDEN과 화면 접근 안내

대상별 예외의 FORBIDDEN과 화면 접근 안내, API 권한 캐시의 네 거부 분기를 분리했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L81 | FORBIDDEN은 서버가 대상별 인가를 거부할 때 403과 함께 보내는 코드다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiForbidden L190~198 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~185 ; nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 ; 접근 안내 L15~34 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 접근 안내 L1104~1106 | 대상별 예외의 FORBIDDEN과 화면 접근 안내, API 권한 캐시의 네 거부 분기를 분리했다. |
| L81 | 「접근 권한이 없습니다」는 화면 접근 검사에서 만드는 별도 안내다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiForbidden L190~198 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~185 ; nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 ; 접근 안내 L15~34 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 접근 안내 L1104~1106 | 대상별 예외의 FORBIDDEN과 화면 접근 안내, API 권한 캐시의 네 거부 분기를 분리했다. |
| L83 | 서버 요청의 403은 권한 미보유뿐 아니라 권한 경로 미등록·권한 정보 초기화 실패·불일치에도 발생한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiForbidden L190~198 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~185 ; nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 ; 접근 안내 L15~34 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 접근 안내 L1104~1106 | 대상별 예외의 FORBIDDEN과 화면 접근 안내, API 권한 캐시의 네 거부 분기를 분리했다. |
| L83 | 입력 오류로 단정하지 않고 화면·작업·표시된 권한 코드를 운영 관리자에게 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiForbidden L190~198 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~185 ; nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 ; 접근 안내 L15~34 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 접근 안내 L1104~1106 | 대상별 예외의 FORBIDDEN과 화면 접근 안내, API 권한 캐시의 네 거부 분기를 분리했다. |
| L85 | 코드별 권한은 `00.rag/user/permission-code-reference.md`, 진단은 `00.rag/user/login-permission-and-screen-troubleshooting.md`에서 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiForbidden L190~198 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~185 ; nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 ; 접근 안내 L15~34 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 접근 안내 L1104~1106 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 입력·요청 형식 오류 INVALID_REQUEST

Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L93 | INVALID_REQUEST는 입력 정책 위반·검증 실패·요청 해석 실패 등에 쓰인다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |
| L93 | 함께 나온 문구가 가리키는 입력을 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |
| L97 | \| 요청 본문 검증에 실패했습니다. \| 본문 검증 실패에 개별 필드 오류 문구가 없을 때다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |
| L98 | \| 요청 파라미터 검증에 실패했습니다. \| 파라미터 제약 위반에 개별 문구가 없을 때다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |
| L99 | \| 요청 본문 형식이 올바르지 않습니다. \| 서버가 요청 본문을 해석하지 못했다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |
| L100 | \| 지원하지 않는 요청입니다. \| 요청 방식·응답 형식·전송 형식·크기 제한 등의 표준 요청 오류다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |
| L102 | 화면에서 고칠 입력이 없으면 화면·조작·시각·응답 문구를 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |

### 공통 요청 오류의 형식·시간 초과 문구

타입 불일치의 항목명 삽입과 표준 4xx·503의 메시지/코드 변환을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L112 | \| 값의 형식이 올바르지 않습니다. \| 앞에 요청 항목이 붙는다. 경로·조회 조건의 타입 변환 실패이며 INVALID_REQUEST로 응답한다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleTypeMismatch·표준 상태 변환 L280~289 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L7~12 | 타입 불일치의 항목명 삽입과 표준 4xx·503의 메시지/코드 변환을 대조했다. |
| L113 | \| 요청이 올바르지 않습니다. \| 별도 문구를 정하지 않은 표준 4xx 요청 오류의 기본 안내다. 코드는 INVALID_REQUEST다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleTypeMismatch·표준 상태 변환 L280~289 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L7~12 | 타입 불일치의 항목명 삽입과 표준 4xx·503의 메시지/코드 변환을 대조했다. |
| L114 | \| 요청 처리 시간이 초과되었습니다. \| 표준 예외의 503을 변환하는 문구다. 이 경로의 코드는 INTERNAL_ERROR다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleTypeMismatch·표준 상태 변환 L280~289 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L7~12 | 타입 불일치의 항목명 삽입과 표준 4xx·503의 메시지/코드 변환을 대조했다. |
| L116 | HTTP 상태와 오류 문구를 함께 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleTypeMismatch·표준 상태 변환 L280~289 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L7~12 | 타입 불일치의 항목명 삽입과 표준 4xx·503의 메시지/코드 변환을 대조했다. |
| L116 | 업무별 시간 초과는 해당 업무의 오류 코드를 함께 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleTypeMismatch·표준 상태 변환 L280~289 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L7~12 | 타입 불일치의 항목명 삽입과 표준 4xx·503의 메시지/코드 변환을 대조했다. |

### 대상 없음 NOT_FOUND와 현재 상태 충돌 CONFLICT

UiNotFoundException·UiConflictException 및 표준 404 변환과 매뉴얼 조치를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L127 | \| NOT_FOUND \| 대상이나 요청 경로가 없을 때의 404다. 「요청한 리소스를 찾을 수 없습니다.」 또는 업무별 대상 부재 문구를 확인하고 목록을 다시 읽는다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 충돌·미존재 L107~115 L208~216 L404~424 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/UiConflictException.java @ 985d17c604dd ; 기본 코드 L26~42 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2520~2523 | UiNotFoundException·UiConflictException 및 표준 404 변환과 매뉴얼 조치를 대조했다. |
| L128 | \| CONFLICT \| 중복 등록·현재 상태와 맞지 않는 작업 등 기본 충돌 코드다. 409와 함께 나온 구체 문구를 기준으로 대상 상태를 확인한다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 충돌·미존재 L107~115 L208~216 L404~424 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/UiConflictException.java @ 985d17c604dd ; 기본 코드 L26~42 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2520~2523 | UiNotFoundException·UiConflictException 및 표준 404 변환과 매뉴얼 조치를 대조했다. |
| L130 | 업무와 무관하게 원인을 하나로 확정하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 충돌·미존재 L107~115 L208~216 L404~424 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/UiConflictException.java @ 985d17c604dd ; 기본 코드 L26~42 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2520~2523 | UiNotFoundException·UiConflictException 및 표준 404 변환과 매뉴얼 조치를 대조했다. |
| L130 | 공통 분류 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 충돌·미존재 L107~115 L208~216 L404~424 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/UiConflictException.java @ 985d17c604dd ; 기본 코드 L26~42 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2520~2523 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 설비 작업의 같은 요청 키·기준 상태 충돌

요청 내용 해시 충돌과 기준 revision 불일치를 생성 분기와 정의에 연결했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L141 | \| IDEMPOTENCY_CONFLICT \| 같은 Idempotency-Key에 다른 요청 내용이 도착했다. 앞선 요청이 반영됐을 수 있으므로 현재 설비 상태를 먼저 확인한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; accept·issueCandidateOrAbort L64~82 L109~136 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationConflictException.java @ 985d17c604dd ; 충돌 정의 L16~23 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 충돌 안내 L1109~1109 | 요청 내용 해시 충돌과 기준 revision 불일치를 생성 분기와 정의에 연결했다. |
| L142 | \| REVISION_CONFLICT \| 요청이 기준으로 삼은 상태와 현재 상태가 맞지 않는다. 최신 상태를 다시 조회한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; accept·issueCandidateOrAbort L64~82 L109~136 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationConflictException.java @ 985d17c604dd ; 충돌 정의 L16~23 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 충돌 안내 L1109~1109 | 요청 내용 해시 충돌과 기준 revision 불일치를 생성 분기와 정의에 연결했다. |
| L144 | 두 코드 모두 작업 접수의 409 충돌이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; accept·issueCandidateOrAbort L64~82 L109~136 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationConflictException.java @ 985d17c604dd ; 충돌 정의 L16~23 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 충돌 안내 L1109~1109 | 요청 내용 해시 충돌과 기준 revision 불일치를 생성 분기와 정의에 연결했다. |
| L144 | 오류 문구·작업 ID·설비 ID를 보존한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; accept·issueCandidateOrAbort L64~82 L109~136 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationConflictException.java @ 985d17c604dd ; 충돌 정의 L16~23 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 충돌 안내 L1109~1109 | 요청 내용 해시 충돌과 기준 revision 불일치를 생성 분기와 정의에 연결했다. |
| L144 | 결과 확인 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; accept·issueCandidateOrAbort L64~82 L109~136 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationConflictException.java @ 985d17c604dd ; 충돌 정의 L16~23 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 충돌 안내 L1109~1109 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 설비 작업 점유 RESOURCE_BUSY·EQUIPMENT_EXECUTING

설비 작업 점유와 배포 실행 점유의 다른 분기 및 공통 409 변환을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L154 | \| RESOURCE_BUSY \| 같은 설비를 다른 작업이 점유 중이다. 문구는 「같은 설비에 대해 다른 작업이 진행 중입니다.」로 시작한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 점유 거부 L118~126 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleOperationConflict L129~137 | 설비 작업 점유와 배포 실행 점유의 다른 분기 및 공통 409 변환을 대조했다. |
| L155 | \| EQUIPMENT_EXECUTING \| 배포 실행이 설비를 점유 중이다. 문구는 「이 설비에 대한 배포가 진행 중입니다. 완료 후 다시 시도하세요.」로 시작한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 점유 거부 L118~126 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleOperationConflict L129~137 | 설비 작업 점유와 배포 실행 점유의 다른 분기 및 공통 409 변환을 대조했다. |
| L157 | 409로 거부된다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 점유 거부 L118~126 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleOperationConflict L129~137 | 설비 작업 점유와 배포 실행 점유의 다른 분기 및 공통 409 변환을 대조했다. |
| L157 | 기존 작업·배포의 진행 결과를 먼저 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 점유 거부 L118~126 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleOperationConflict L129~137 | 설비 작업 점유와 배포 실행 점유의 다른 분기 및 공통 409 변환을 대조했다. |
| L157 | 명령 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`, 배포 확인은 `00.rag/user/deployment-workflow.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 점유 거부 L118~126 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleOperationConflict L129~137 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 미종결 작업 차단 RESOURCE_BLOCKED

미종결 차단 분기·blockedByOperationId 문구와 매뉴얼 Repair Task 확인 경로를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L165 | RESOURCE_BLOCKED는 미종결 작업이 설비 처리를 차단하는 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 차단 거부 L127~131 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2528~2528 | 미종결 차단 분기·blockedByOperationId 문구와 매뉴얼 Repair Task 확인 경로를 대조했다. |
| L165 | 「미종결 작업이 이 설비의 처리를 차단하고 있습니다.」 뒤에 차단한 작업 ID가 붙는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 차단 거부 L127~131 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2528~2528 | 미종결 차단 분기·blockedByOperationId 문구와 매뉴얼 Repair Task 확인 경로를 대조했다. |
| L167 | 설비 ID와 거부 문구를 보존하고 「처리실패」의 「Repair Task」 확인 경로인 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 차단 거부 L127~131 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2528~2528 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |
| L167 | 복구 처리는 운영 담당자에게 요청한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 차단 거부 L127~131 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2528~2528 | 미종결 차단 분기·blockedByOperationId 문구와 매뉴얼 Repair Task 확인 경로를 대조했다. |

### 작업 조회 불가 COORDINATION_UNAVAILABLE·COORDINATION_DISABLED

접근 불확실 예외와 조정 기능 없는 구성의 조회 503을 구별했다. 404로 해석하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L178 | \| COORDINATION_UNAVAILABLE \| 작업 기록 저장소에 접근할 수 없어 결과를 확정할 수 없다. 503으로 응답한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationUnavailableException.java @ 985d17c604dd ; 접근 불가 정의 L3~15 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCoordinationUnavailable L154~162 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 비활성 구성 L73~82 | 접근 불확실 예외와 조정 기능 없는 구성의 조회 503을 구별했다. 404로 해석하지 않았다. |
| L179 | \| COORDINATION_DISABLED \| 「이 구성은 조정 원장을 사용하지 않습니다.」: 작업 조회에 필요한 기능이 구성에 없어 503으로 거부한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationUnavailableException.java @ 985d17c604dd ; 접근 불가 정의 L3~15 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCoordinationUnavailable L154~162 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 비활성 구성 L73~82 | 접근 불확실 예외와 조정 기능 없는 구성의 조회 503을 구별했다. 404로 해석하지 않았다. |
| L181 | 작업 부재나 실패 확정으로 읽어 새 요청을 보내기 전에 운영 담당자에게 작업 ID·시각·문구를 전달한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationUnavailableException.java @ 985d17c604dd ; 접근 불가 정의 L3~15 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCoordinationUnavailable L154~162 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 비활성 구성 L73~82 | 접근 불확실 예외와 조정 기능 없는 구성의 조회 503을 구별했다. 404로 해석하지 않았다. |
| L181 | 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationUnavailableException.java @ 985d17c604dd ; 접근 불가 정의 L3~15 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCoordinationUnavailable L154~162 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 비활성 구성 L73~82 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 서버 정의 불일치 CATALOG_NOT_SYNCED

정의 불일치 예외·503 변환을 대조했다. 추가로 UiCatalogHeadSynchronizer.requireInSync L132~140의 실제 throw를 읽었다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L189 | CATALOG_NOT_SYNCED는 요청을 받은 서버의 정의 자료가 공유 기준과 달라 요청을 거부한 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCatalogNotSyncedException.java @ 985d17c604dd ; 정의 불일치 L5~17 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCatalogNotSynced L172~180 | 정의 불일치 예외·503 변환을 대조했다. 추가로 UiCatalogHeadSynchronizer.requireInSync L132~140의 실제 throw를 읽었다. |
| L189 | 응답은 503이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCatalogNotSyncedException.java @ 985d17c604dd ; 정의 불일치 L5~17 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCatalogNotSynced L172~180 | 정의 불일치 예외·503 변환을 대조했다. 추가로 UiCatalogHeadSynchronizer.requireInSync L132~140의 실제 throw를 읽었다. |
| L189 | 사용자 입력을 바꾸어 해결하는 오류가 아니다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCatalogNotSyncedException.java @ 985d17c604dd ; 정의 불일치 L5~17 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCatalogNotSynced L172~180 | 정의 불일치 예외·503 변환을 대조했다. 추가로 UiCatalogHeadSynchronizer.requireInSync L132~140의 실제 throw를 읽었다. |
| L191 | 운영 담당자에게 실행한 작업·대상·시각·전체 문구를 전달해 서버 정의의 반영 상태를 확인하도록 요청한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCatalogNotSyncedException.java @ 985d17c604dd ; 정의 불일치 L5~17 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCatalogNotSynced L172~180 | 정의 불일치 예외·503 변환을 대조했다. 추가로 UiCatalogHeadSynchronizer.requireInSync L132~140의 실제 throw를 읽었다. |
| L191 | 화면·권한 오류의 구별은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCatalogNotSyncedException.java @ 985d17c604dd ; 정의 불일치 L5~17 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCatalogNotSynced L172~180 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 설비 파라미터 잠금 ALREADY_CHECKED_OUT

EDIT 존재 검사·체크아웃 예외·409 및 편집자 유무에 따른 문구를 연결했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L200 | ALREADY_CHECKED_OUT은 설비 파라미터 체크아웃 때 EDIT 자료가 이미 있어 반환하는 409 코드다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 기존 EDIT 검사 L94~103 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; checkout L148~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/EqpAlreadyCheckedOutException.java @ 985d17c604dd ; buildMessage L11~11 L50~54 | EDIT 존재 검사·체크아웃 예외·409 및 편집자 유무에 따른 문구를 연결했다. |
| L200 | 문구는 「설비 파라미터가 이미 체크아웃 중입니다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 기존 EDIT 검사 L94~103 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; checkout L148~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/EqpAlreadyCheckedOutException.java @ 985d17c604dd ; buildMessage L11~11 L50~54 | EDIT 존재 검사·체크아웃 예외·409 및 편집자 유무에 따른 문구를 연결했다. |
| L200 | 현재 편집 사용자:」 뒤에 편집자가 붙거나, 편집자를 모르면 다른 사용자가 체크아웃 중이라는 안내다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 기존 EDIT 검사 L94~103 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; checkout L148~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/EqpAlreadyCheckedOutException.java @ 985d17c604dd ; buildMessage L11~11 L50~54 | EDIT 존재 검사·체크아웃 예외·409 및 편집자 유무에 따른 문구를 연결했다. |
| L202 | 현재 체크아웃 상태와 편집자를 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 기존 EDIT 검사 L94~103 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; checkout L148~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/EqpAlreadyCheckedOutException.java @ 985d17c604dd ; buildMessage L11~11 L50~54 | EDIT 존재 검사·체크아웃 예외·409 및 편집자 유무에 따른 문구를 연결했다. |
| L202 | 파라미터 작업 경로는 `00.rag/user/equipment-model-parameter-and-jar-update.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 기존 EDIT 검사 L94~103 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; checkout L148~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/EqpAlreadyCheckedOutException.java @ 985d17c604dd ; buildMessage L11~11 L50~54 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 설비 생성 준비 단계의 거부 코드

생성 준비 UNEXPECTED_STATE·REJECTED와 참여 서버 거부 응답의 null 코드 대체를 실제 abort 인자와 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L212 | \| DB_STAGE_UNEXPECTED \| 생성하려는 설비가 이미 다른 상태로 존재한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 생성 준비·중단 L200~220 L620~647 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 거부 응답 대체 코드 L174~185 | 생성 준비 UNEXPECTED_STATE·REJECTED와 참여 서버 거부 응답의 null 코드 대체를 실제 abort 인자와 대조했다. |
| L213 | \| DB_STAGE_REJECTED \| 생성 준비의 저장 처리가 거부됐다. 「요청 값이 올바르지 않아 설비를 생성할 수 없습니다.」로 안내한다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 생성 준비·중단 L200~220 L620~647 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 거부 응답 대체 코드 L174~185 | 생성 준비 UNEXPECTED_STATE·REJECTED와 참여 서버 거부 응답의 null 코드 대체를 실제 abort 인자와 대조했다. |
| L214 | \| PARTICIPANT_REJECTED \| 적용 확정 전 참여 서버가 검증을 거부했으나 응답에 오류 코드가 없다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 생성 준비·중단 L200~220 L620~647 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 거부 응답 대체 코드 L174~185 | 생성 준비 UNEXPECTED_STATE·REJECTED와 참여 서버 거부 응답의 null 코드 대체를 실제 abort 인자와 대조했다. |
| L216 | 입력과 기존 설비 상태를 담당자와 대조한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 생성 준비·중단 L200~220 L620~647 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 거부 응답 대체 코드 L174~185 | 생성 준비 UNEXPECTED_STATE·REJECTED와 참여 서버 거부 응답의 null 코드 대체를 실제 abort 인자와 대조했다. |
| L216 | 결과 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 생성 준비·중단 L200~220 L620~647 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 거부 응답 대체 코드 L174~185 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 작업 결과 코드 REPAIR_REQUIRED와 오류 상세

작업 상태와 오류 코드의 중복 이름을 구별했다. markRepairRequired의 오류 코드와 세부 오류 조합, 화면의 repairReason 우선을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L224 | REPAIR_REQUIRED는 적용 확정 뒤 실패를 복구 필요 결과로 기록할 때의 오류 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 적용 실패·복구 필요 확정 L416~426 L653~677 ; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 복구 결과 문구 L277~285 | 작업 상태와 오류 코드의 중복 이름을 구별했다. markRepairRequired의 오류 코드와 세부 오류 조합, 화면의 repairReason 우선을 대조했다. |
| L224 | 배포 적용 단계가 실패하면 세부 코드와 문구를 오류 상세에 함께 남긴다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 적용 실패·복구 필요 확정 L416~426 L653~677 ; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 복구 결과 문구 L277~285 | 작업 상태와 오류 코드의 중복 이름을 구별했다. markRepairRequired의 오류 코드와 세부 오류 조합, 화면의 repairReason 우선을 대조했다. |
| L224 | STEP_TIMEOUT 같은 코드가 최상위 errorCode 대신 상세 문구 안에 있을 수 있다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 적용 실패·복구 필요 확정 L416~426 L653~677 ; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 복구 결과 문구 L277~285 | 작업 상태와 오류 코드의 중복 이름을 구별했다. markRepairRequired의 오류 코드와 세부 오류 조합, 화면의 repairReason 우선을 대조했다. |
| L226 | 설비 명령 결과 창은 복구 사유가 있으면 사유를, 없으면 오류 문구를 표시한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 적용 실패·복구 필요 확정 L416~426 L653~677 ; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 복구 결과 문구 L277~285 | 작업 상태와 오류 코드의 중복 이름을 구별했다. markRepairRequired의 오류 코드와 세부 오류 조합, 화면의 repairReason 우선을 대조했다. |
| L226 | 상태값 전체는 `00.rag/user/state-reference.md`가 다룬다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 적용 실패·복구 필요 확정 L416~426 L653~677 ; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 복구 결과 문구 L277~285 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |
| L226 | 작업 ID·설비·사유를 보존하고 `00.rag/user/equipment-connection-and-command-troubleshooting.md`에서 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 적용 실패·복구 필요 확정 L416~426 L653~677 ; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 복구 결과 문구 L277~285 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 설비 직접 실행의 발행·응답 실패

직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L234 | 설비 직접 실행 경로의 오류 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L234 | TIMEOUT은 이 경로의 단계 응답 대기 실패를 뜻한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L238 | \| PUBLISH_FAILED \| 단계 명령 발행 실패 또는 비동기 작업 등록 중 예외다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L239 | \| TIMEOUT \| 「step 응답 대기 시간이 초과되었습니다.」: 단계 응답을 기한 안에 받지 못했다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L240 | \| STEP_INTERRUPTED \| 단계 실행 대기가 인터럽트됐다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L241 | \| STEP_CANCELED \| 단계 실행 대기가 취소됐다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L242 | \| STEP_EXECUTION_FAILED \| 단계 응답 처리 중 예외가 발생했다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L244 | 설비·작업 ID·문구를 담당자에게 전달한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| L244 | 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 설비 직접 실행 후 저장 확정 실패

단계 실행 뒤 finalizeSuccess에서 발생하는 예외 분기만 설명했다. 전체 저장 성공을 주장하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L251 | LOCAL_FINALIZATION_FAILED는 설비 직접 실행의 단계 처리를 마친 뒤 서버 내부의 최종 저장 처리에서 예외가 발생한 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; finalizeLocalSuccess L165~183 | 단계 실행 뒤 finalizeSuccess에서 발생하는 예외 분기만 설명했다. 전체 저장 성공을 주장하지 않았다. |
| L251 | 명령을 받은 대상의 상태와 서버에 저장된 결과를 함께 대조해야 한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; finalizeLocalSuccess L165~183 | 단계 실행 뒤 finalizeSuccess에서 발생하는 예외 분기만 설명했다. 전체 저장 성공을 주장하지 않았다. |
| L253 | 운영 담당자에게 설비 ID·실행 명령·시각·오류 문구를 넘긴다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; finalizeLocalSuccess L165~183 | 단계 실행 뒤 finalizeSuccess에서 발생하는 예외 분기만 설명했다. 전체 저장 성공을 주장하지 않았다. |
| L253 | 설비 명령 결과를 확인하는 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; finalizeLocalSuccess L165~183 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 모델 체크아웃 충돌 CHECKOUT_CONFLICT

모델 EDIT 편집자 검사·409 뒤 목록 재확인·checkout/checkin 오류 생성과 한국어 두 문구를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L261 | CHECKOUT_CONFLICT는 프런트엔드가 모델 EDIT 잠금을 확인해 만드는 코드다. | nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 잠금 검사·오류 생성 L46~82 L131~161 L177~185 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 잠금 문구 L676~677 | 모델 EDIT 편집자 검사·409 뒤 목록 재확인·checkout/checkin 오류 생성과 한국어 두 문구를 대조했다. |
| L261 | 다른 편집자이거나 현재 사용자 정보를 확인하지 못해 잠금 소유를 인정할 수 없을 때 발생한다. | nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 잠금 검사·오류 생성 L46~82 L131~161 L177~185 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 잠금 문구 L676~677 | 모델 EDIT 편집자 검사·409 뒤 목록 재확인·checkout/checkin 오류 생성과 한국어 두 문구를 대조했다. |
| L261 | 서버의 409 뒤 목록을 다시 확인해 같은 코드를 만들기도 한다. | nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 잠금 검사·오류 생성 L46~82 L131~161 L177~185 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 잠금 문구 L676~677 | 모델 EDIT 편집자 검사·409 뒤 목록 재확인·checkout/checkin 오류 생성과 한국어 두 문구를 대조했다. |
| L263 | 「다른 사용자가 체크아웃한 모델이라 수정할 수 없습니다.」 또는 편집자 뒤에 「님이 체크아웃한 모델이라 수정할 수 없습니다.」가 붙는다. | nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 잠금 검사·오류 생성 L46~82 L131~161 L177~185 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 잠금 문구 L676~677 | 모델 EDIT 편집자 검사·409 뒤 목록 재확인·checkout/checkin 오류 생성과 한국어 두 문구를 대조했다. |
| L265 | 모델 작업본·현재 사용자·편집자를 확인한다. | nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 잠금 검사·오류 생성 L46~82 L131~161 L177~185 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 잠금 문구 L676~677 | 모델 EDIT 편집자 검사·409 뒤 목록 재확인·checkout/checkin 오류 생성과 한국어 두 문구를 대조했다. |
| L265 | 편집 경로는 `00.rag/user/model-details-and-flow-editing.md`를 따른다. | nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 잠금 검사·오류 생성 L46~82 L131~161 L177~185 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 잠금 문구 L676~677 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 모델 저장 대상 오류 INVALID_MODEL_VERSION·INVALID_NODE

EDIT 아닌 저장/업로드와 MDF 일반 저장·지원 상세 응답 부재를 각각 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L275 | \| INVALID_MODEL_VERSION \| 「EDIT version만 저장할 수 있습니다.」 또는 「EDIT version만 업로드할 수 있습니다.」: EDIT가 아닌 버전에 저장·업로드를 요청했다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; 저장·업로드 대상 검사 L64~78 L123~125 L139~147 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 상세 노드 조회 L74~91 | EDIT 아닌 저장/업로드와 MDF 일반 저장·지원 상세 응답 부재를 각각 대조했다. |
| L276 | \| INVALID_NODE \| 「MDF는 upload API를 사용해 주세요.」: 일반 상세 저장으로 MDF를 보냈다. 「지원하지 않는 상세 노드입니다.」: 상세 응답을 지원하지 않는 항목이다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; 저장·업로드 대상 검사 L64~78 L123~125 L139~147 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 상세 노드 조회 L74~91 | EDIT 아닌 저장/업로드와 MDF 일반 저장·지원 상세 응답 부재를 각각 대조했다. |
| L278 | 모델과 버전·선택 항목을 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; 저장·업로드 대상 검사 L64~78 L123~125 L139~147 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 상세 노드 조회 L74~91 | EDIT 아닌 저장/업로드와 MDF 일반 저장·지원 상세 응답 부재를 각각 대조했다. |
| L278 | 정상 편집·MDF 업로드 경로는 `00.rag/user/model-details-and-flow-editing.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; 저장·업로드 대상 검사 L64~78 L123~125 L139~147 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 상세 노드 조회 L74~91 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### PLC 순서 값 저장 거부 INVALID_REQUEST

모델 상세 PUT 저장의 새 INVALID_REQUEST 사유이므로 이 문서 범위에 포함했다. 기존 일반 오류·모델 대상 오류 설명은 보존했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L289 | PLC 순서 값 검증 보완이 들어간 판본은 모델 상세 저장에서 「Byte Order」·「Word Order」를 앞뒤 공백 제거·대문자 변환 후 검사한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~20 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters L477~528 | 공백 제거·대문자 변환 뒤 enum 검사다. 적용은 보완 판본으로 한정했다. |
| L289 | 허용값 밖이면 400 INVALID_REQUEST로 거부한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiBadRequest L89~97 | UiBadRequestException을 400 INVALID_REQUEST로 변환한다. |
| L291 | 오류는 「PLC Register」로 시작하며 모델 버전 키·행·그룹·변수와 byteOrder(8열) 또는 wordOrder(9열), 거부된 값을 포함한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters L477~528 | orderContext와 byteOrder(8열)·wordOrder(9열) 인자를 대조했다. |
| L293 | 문구 끝은 「'은 허용되지 않습니다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~20 | 예외 문구의 고정 접미부 앞 문장을 대조했다. |
| L293 | 허용값: BIG_ENDIAN, LITTLE_ENDIAN (빈값은 기본 BIG_ENDIAN).」다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~20 | 같은 예외 문구의 허용값·빈값 안내를 문자 단위로 대조했다. |
| L295 | 기존 행을 바꾸기 전에 거부한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters L477~528 | 모든 행 검사 뒤에 기존 행 삭제·저장이 실행된다. |
| L295 | 오류가 가리킨 칸의 선택값을 고친다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; createPlcColumns L562~645 | 순서 칸의 Select 렌더와 옵션을 확인했다. |
| L295 | 편집은 `00.rag/user/model-details-and-flow-editing.md`를 따른다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 모델 상세와 Flow 편집 L31~31 | 편집 절차를 복제하지 않고 확정 소유 objectKey로 연결했다. |

### 배포 설비 중복 DEPLOY_ACTIVE_EQP_CONFLICT

충돌 조회 SQL이 READY·RUNNING을 모두 포함하는 것을 확인했다. 매뉴얼의 진행 중 배포 표현을 구체화했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L303 | DEPLOY_ACTIVE_EQP_CONFLICT는 같은 설비가 READY 또는 RUNNING인 배포에 포함돼 있다는 충돌 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; rejectWhenActiveEqpConflict L761~775 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; findActiveEqpConflicts L243~269 | 충돌 조회 SQL이 READY·RUNNING을 모두 포함하는 것을 확인했다. 매뉴얼의 진행 중 배포 표현을 구체화했다. |
| L303 | 실행 중인 배포뿐 아니라 실행을 기다리는 배포도 해당한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; rejectWhenActiveEqpConflict L761~775 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; findActiveEqpConflicts L243~269 | 충돌 조회 SQL이 READY·RUNNING을 모두 포함하는 것을 확인했다. 매뉴얼의 진행 중 배포 표현을 구체화했다. |
| L305 | 문구는 「이미 active Deploy에 포함된 설비입니다.」로 시작하며 설비 ID·기존 배포 번호·배포 상태가 붙는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; rejectWhenActiveEqpConflict L761~775 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; findActiveEqpConflicts L243~269 | 충돌 조회 SQL이 READY·RUNNING을 모두 포함하는 것을 확인했다. 매뉴얼의 진행 중 배포 표현을 구체화했다. |
| L305 | 기존 배포의 상태를 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; rejectWhenActiveEqpConflict L761~775 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; findActiveEqpConflicts L243~269 | 충돌 조회 SQL이 READY·RUNNING을 모두 포함하는 것을 확인했다. 매뉴얼의 진행 중 배포 표현을 구체화했다. |
| L305 | 배포 조회 경로는 `00.rag/user/deployment-workflow.md`다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; rejectWhenActiveEqpConflict L761~775 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; findActiveEqpConflicts L243~269 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 배포 실행 정보·응답 조회 오류

계획 조회 Optional.empty와 응답 조회 INFRA_ERROR를 별도 코드로 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L312 | 배포 작업을 묶어 실행하는 경로에서 다음 코드를 만든다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 계획·응답 조회 실패 L90~96 L157~172 | 계획 조회 Optional.empty와 응답 조회 INFRA_ERROR를 별도 코드로 대조했다. |
| L316 | \| DEPLOY_PLAN_NOT_FOUND \| 「실행할 Deploy target 정보를 찾을 수 없습니다.」: 실행 계획을 조회하지 못했다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 계획·응답 조회 실패 L90~96 L157~172 | 계획 조회 Optional.empty와 응답 조회 INFRA_ERROR를 별도 코드로 대조했다. |
| L317 | \| DEPLOY_TRACE_UNAVAILABLE \| 「step 응답 저장소를 조회하지 못했습니다.」: 단계 응답 저장소 조회가 실패했다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 계획·응답 조회 실패 L90~96 L157~172 | 계획 조회 Optional.empty와 응답 조회 INFRA_ERROR를 별도 코드로 대조했다. |
| L319 | 운영 담당자에게 배포 번호·설비 ID·현재 단계·시각·문구를 전달한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 계획·응답 조회 실패 L90~96 L157~172 | 계획 조회 Optional.empty와 응답 조회 INFRA_ERROR를 별도 코드로 대조했다. |
| L319 | 응답 대기와 조회 실패를 가르는 경로는 `00.rag/user/deployment-troubleshooting.md`다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 계획·응답 조회 실패 L90~96 L157~172 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 배포 단계 실패 STEP_TIMEOUT·STEP_FAILED

EXPIRED 등록·저장 기한 도달·실패 응답의 코드/문구 null 대체를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L328 | \| STEP_TIMEOUT \| 배포 작업을 묶어 실행하는 경로에서 응답 추적 등록이 사라지고 저장된 단계 기한에 도달했다. 문구는 「step 응답 대기 시간이 초과되었습니다.」다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 단계 실패 판정 L175~215 | EXPIRED 등록·저장 기한 도달·실패 응답의 코드/문구 null 대체를 대조했다. |
| L329 | \| STEP_FAILED \| 실패 응답이 도착했으나 응답의 오류 코드가 없을 때 쓴다. 오류 문구도 없으면 「step 응답이 실패로 도착했습니다.」를 쓴다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 단계 실패 판정 L175~215 | EXPIRED 등록·저장 기한 도달·실패 응답의 코드/문구 null 대체를 대조했다. |
| L331 | 실패 응답에 코드가 있으면 STEP_FAILED 대신 그 코드를 사용한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 단계 실패 판정 L175~215 | EXPIRED 등록·저장 기한 도달·실패 응답의 코드/문구 null 대체를 대조했다. |
| L331 | 배포·설비·단계·문구를 기록하고 `00.rag/user/deployment-troubleshooting.md`에서 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 단계 실패 판정 L175~215 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 처리실패 목록의 조회 오류 문구

네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L342 | \| Gateway DLQ 목록을 조회하지 못했습니다. \| Gateway DLQ 조회 \| | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58 ; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065 | 네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다. |
| L343 | \| Business DLQ 목록을 조회하지 못했습니다. \| Business DLQ 조회 \| | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58 ; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065 | 네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다. |
| L344 | \| 격리 설비 목록을 조회하지 못했습니다. \| 격리 설비 조회 \| | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58 ; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065 | 네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다. |
| L345 | \| Repair Task 목록을 조회하지 못했습니다. \| Repair Task 조회 \| | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58 ; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065 | 네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다. |
| L347 | 해석 가능한 실패 응답에 서버 문구가 있으면 그 문구를 사용한다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58 ; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065 | 네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다. |
| L347 | 오류 영역의 「재시도」는 같은 목록을 다시 조회한다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58 ; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065 | 네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다. |
| L347 | 실제 실패 원인과 권한 확인은 `00.rag/user/dlq-quarantine-troubleshooting.md`를 따른다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58 ; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### Repair Task에 남는 수동 복구·보상 발행 코드

복구 단계 MANUAL_ONLY/복구 action 부재와 보상 발행 예외의 Repair Task 기록을 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L355 | 설비 직접 실행의 복구 과정이 Repair Task에 기록하는 코드다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 수동 복구·발행 실패 L91~102 L119~143 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 복구 단계 MANUAL_ONLY/복구 action 부재와 보상 발행 예외의 Repair Task 기록을 확인했다. |
| L359 | \| MANUAL_ROLLBACK_REQUIRED \| 복구할 단계가 수동 복구 전용이거나 복구 동작이 없다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 수동 복구·발행 실패 L91~102 L119~143 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 복구 단계 MANUAL_ONLY/복구 action 부재와 보상 발행 예외의 Repair Task 기록을 확인했다. |
| L360 | \| COMPENSATION_PUBLISH_FAILED \| 이미 끝난 단계를 되돌리는 보상 명령을 발행하지 못했다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 수동 복구·발행 실패 L91~102 L119~143 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 복구 단계 MANUAL_ONLY/복구 action 부재와 보상 발행 예외의 Repair Task 기록을 확인했다. |
| L362 | 코드와 함께 「에러 메시지」를 보존해 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 수동 복구·발행 실패 L91~102 L119~143 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 복구 단계 MANUAL_ONLY/복구 action 부재와 보상 발행 예외의 Repair Task 기록을 확인했다. |
| L362 | 사용자 확인 경로는 `00.rag/user/dlq-quarantine-and-repair-task.md`다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 수동 복구·발행 실패 L91~102 L119~143 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### Repair Task의 보상 대기·응답 처리 실패

보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L370 | 설비 직접 실행의 보상 과정에서 다음 코드를 Repair Task에 남긴다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다. |
| L374 | \| COMPENSATION_TIMEOUT \| 보상 단계 응답 대기 시간이 초과됐다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다. |
| L375 | \| COMPENSATION_INTERRUPTED \| 보상 응답 대기가 인터럽트됐다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다. |
| L376 | \| COMPENSATION_CANCELED \| 보상 단계 대기가 취소됐다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다. |
| L377 | \| COMPENSATION_EXECUTION_FAILED \| 보상 단계 응답 처리 중 예외가 발생했다. \| | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다. |
| L379 | 설비 ID·Task ID·생성 시각·문구를 운영 담당자에게 넘긴다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다. |
| L379 | 기록 확인은 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180 ; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 데모 점유 충돌·실행 부재·호출 오류

시뮬레이터 호출의 409·404·다른 4xx 및 IO/Interrupted 예외를 UI 예외와 대조했다. 입력 오류 하나로 해석하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L389 | \| 설비가 이미 다른 Run 에 점유되어 있습니다. \| CONFLICT: 시뮬레이터 호출이 409로 거부됐다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 호출·상태 변환 L141~166 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorRunNotFoundException.java @ 985d17c604dd ; 미존재 예외 L5~12 | 시뮬레이터 호출의 409·404·다른 4xx 및 IO/Interrupted 예외를 UI 예외와 대조했다. 입력 오류 하나로 해석하지 않았다. |
| L390 | \| simulator orchestrator Run 미존재. \| NOT_FOUND: 시뮬레이터가 해당 실행 요청에 404로 응답했다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 호출·상태 변환 L141~166 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorRunNotFoundException.java @ 985d17c604dd ; 미존재 예외 L5~12 | 시뮬레이터 호출의 409·404·다른 4xx 및 IO/Interrupted 예외를 UI 예외와 대조했다. 입력 오류 하나로 해석하지 않았다. |
| L391 | \| simulator orchestrator 호출 중 오류가 발생했습니다: \| INVALID_REQUEST: 서버 간 호출의 입출력 실패나 인터럽트다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 호출·상태 변환 L141~166 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorRunNotFoundException.java @ 985d17c604dd ; 미존재 예외 L5~12 | 시뮬레이터 호출의 409·404·다른 4xx 및 IO/Interrupted 예외를 UI 예외와 대조했다. 입력 오류 하나로 해석하지 않았다. |
| L392 | \| simulator orchestrator 호출에 실패했습니다. \| INVALID_REQUEST: 404·409 외의 4xx 응답이다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 호출·상태 변환 L141~166 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorRunNotFoundException.java @ 985d17c604dd ; 미존재 예외 L5~12 | 시뮬레이터 호출의 409·404·다른 4xx 및 IO/Interrupted 예외를 UI 예외와 대조했다. 입력 오류 하나로 해석하지 않았다. |
| L394 | 실행 번호·설비·시각·문구를 보존한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 호출·상태 변환 L141~166 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorRunNotFoundException.java @ 985d17c604dd ; 미존재 예외 L5~12 | 시뮬레이터 호출의 409·404·다른 4xx 및 IO/Interrupted 예외를 UI 예외와 대조했다. 입력 오류 하나로 해석하지 않았다. |
| L394 | 확인 경로는 `00.rag/user/demo-troubleshooting.md`다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 호출·상태 변환 L141~166 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorRunNotFoundException.java @ 985d17c604dd ; 미존재 예외 L5~12 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 데모 시작·조회·종료의 기본 실패 문구

실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L405 | \| Demo 테스트 시작에 실패했습니다. \| 데모 실행 생성 \| | nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974 | 실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다. |
| L406 | \| Demo 시나리오 목록을 조회하지 못했습니다. \| 시나리오 목록 조회 \| | nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974 | 실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다. |
| L407 | \| Demo 테스트 정보를 처리하지 못했습니다. \| 실행 목록 또는 실행 상세 조회 \| | nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974 | 실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다. |
| L408 | \| Run 종료 요청에 실패했습니다. \| 실행 전체 또는 설비별 종료 \| | nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974 | 실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다. |
| L410 | API에서 사용할 서버 문구를 얻지 못했을 때의 대체 문구다. | nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974 | 실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다. |
| L410 | 시작 실패는 팝업, 종료 실패는 데모 창의 안내 영역에 표시한다. | nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974 | 실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다. |
| L410 | 실행 유무·실제 종료 결과는 `00.rag/user/demo-troubleshooting.md`에서 확인한다. | nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 공통 통신 UNKNOWN_ERROR와 INVALID_RESPONSE

createFailResponse의 null/빈 코드·응답 해석 실패 및 normalizeApiError의 응답 부재, 목록 구조 검사 분기를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L421 | \| UNKNOWN_ERROR \| 해석 가능한 실패 응답에 코드가 없거나, 실패 형식으로 해석하지 못했다. 응답이 없는 통신 예외도 이 코드로 정규화할 수 있다. \| | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse·normalizeApiError L19~40 L67~79 ; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 목록 계약 검사 L55~84 ; nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; normalizeModelListPayload L59~85 | createFailResponse의 null/빈 코드·응답 해석 실패 및 normalizeApiError의 응답 부재, 목록 구조 검사 분기를 대조했다. |
| L422 | \| INVALID_RESPONSE \| 목록 등에서 성공 데이터의 구조가 화면이 요구한 형태가 아니다. 예를 들어 인스턴스 목록이 배열이 아닐 때 발생한다. \| | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse·normalizeApiError L19~40 L67~79 ; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 목록 계약 검사 L55~84 ; nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; normalizeModelListPayload L59~85 | createFailResponse의 null/빈 코드·응답 해석 실패 및 normalizeApiError의 응답 부재, 목록 구조 검사 분기를 대조했다. |
| L424 | 응답 유무·상태·표시 문구를 함께 확인한다. | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse·normalizeApiError L19~40 L67~79 ; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 목록 계약 검사 L55~84 ; nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; normalizeModelListPayload L59~85 | createFailResponse의 null/빈 코드·응답 해석 실패 및 normalizeApiError의 응답 부재, 목록 구조 검사 분기를 대조했다. |
| L424 | 공통 통신 점검은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다. | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse·normalizeApiError L19~40 L67~79 ; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 목록 계약 검사 L55~84 ; nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; normalizeModelListPayload L59~85 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 서버 내부 오류 INTERNAL_ERROR

미분류 500·표준 5xx 코드 변환과 한국어 내부 오류 문구를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L432 | INTERNAL_ERROR는 공통 예외 처리기가 분류하지 못한 서버 예외에 500과 함께 보내는 코드다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 미분류·표준 오류 L324~332 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 내부 오류 문구 L13~13 | 미분류 500·표준 5xx 코드 변환과 한국어 내부 오류 문구를 대조했다. |
| L432 | 표준 서버 오류를 같은 코드로 변환하는 경우도 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 미분류·표준 오류 L324~332 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 내부 오류 문구 L13~13 | 미분류 500·표준 5xx 코드 변환과 한국어 내부 오류 문구를 대조했다. |
| L432 | 기본 한국어 문구는 「요청 처리 중 내부 오류가 발생했습니다.」다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 미분류·표준 오류 L324~332 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 내부 오류 문구 L13~13 | 미분류 500·표준 5xx 코드 변환과 한국어 내부 오류 문구를 대조했다. |
| L434 | 화면·직전 작업·대상·시각·전체 표시 문구를 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 미분류·표준 오류 L324~332 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 내부 오류 문구 L13~13 | 미분류 500·표준 5xx 코드 변환과 한국어 내부 오류 문구를 대조했다. |
| L434 | 분류 기준은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 미분류·표준 오류 L324~332 L404~424 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 내부 오류 문구 L13~13 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 작업 조회의 요청한 작업을 찾을 수 없습니다

작업 조회 404의 프런트 대체 문구와 서버 found.empty, 다른 실패의 fallback을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L443 | 「요청한 작업을 찾을 수 없습니다.」는 작업 상태 조회가 404일 때 화면이 NOT_FOUND 코드와 함께 만드는 문구다. | nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L19~50 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 조회 문구 L1107~1108 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업 조회 L85~107 | 작업 조회 404의 프런트 대체 문구와 서버 found.empty, 다른 실패의 fallback을 대조했다. |
| L443 | 서버가 해당 작업을 찾지 못한 결과와 연결한다. | nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L19~50 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 조회 문구 L1107~1108 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업 조회 L85~107 | 작업 조회 404의 프런트 대체 문구와 서버 found.empty, 다른 실패의 fallback을 대조했다. |
| L445 | 「작업 상태를 조회하지 못했습니다.」는 조회 실패를 구체 문구로 해석하지 못했을 때의 대체 안내다. | nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L19~50 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 조회 문구 L1107~1108 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업 조회 L85~107 | 작업 조회 404의 프런트 대체 문구와 서버 found.empty, 다른 실패의 fallback을 대조했다. |
| L445 | 두 안내 모두 명령 대상 설비의 연결 실패를 직접 뜻하지 않는다. | nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L19~50 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 조회 문구 L1107~1108 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업 조회 L85~107 | 작업 조회 404의 프런트 대체 문구와 서버 found.empty, 다른 실패의 fallback을 대조했다. |
| L447 | 새 명령을 보내기 전에 작업 ID와 설비 ID를 보존한다. | nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L19~50 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 조회 문구 L1107~1108 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업 조회 L85~107 | 작업 조회 404의 프런트 대체 문구와 서버 found.empty, 다른 실패의 fallback을 대조했다. |
| L447 | 결과 확인 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`다. | nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L19~50 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 조회 문구 L1107~1108 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업 조회 L85~107 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

### 로그인·로그아웃의 일반 실패 문구

호출 시점 fallback 키와 auth 오류 정규화를 대조했다. 로그아웃 실패의 화면 미표시를 명시했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L459 | \| 로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. \| 로그인 실패에서 사용할 구체 문구를 얻지 못했다. \| | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 기본 문구·실패 정규화 L15~19 L97~113 L177~218 ; nori-tc-ui-front/src/features/auth/types/auth.types.ts @ 9fe69a75dcd3 ; defaultAuthErrorMessage L38~43 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 일반 실패 문구 L837~838 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 호출 시점 fallback 키와 auth 오류 정규화를 대조했다. 로그아웃 실패의 화면 미표시를 명시했다. |
| L460 | \| 로그아웃 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. \| 로그아웃 실패에서 사용할 구체 문구를 얻지 못했다. \| | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 기본 문구·실패 정규화 L15~19 L97~113 L177~218 ; nori-tc-ui-front/src/features/auth/types/auth.types.ts @ 9fe69a75dcd3 ; defaultAuthErrorMessage L38~43 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 일반 실패 문구 L837~838 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 호출 시점 fallback 키와 auth 오류 정규화를 대조했다. 로그아웃 실패의 화면 미표시를 명시했다. |
| L462 | 문구만으로 비밀번호 불일치나 네트워크 장애를 확정하지 않는다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 기본 문구·실패 정규화 L15~19 L97~113 L177~218 ; nori-tc-ui-front/src/features/auth/types/auth.types.ts @ 9fe69a75dcd3 ; defaultAuthErrorMessage L38~43 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 일반 실패 문구 L837~838 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 호출 시점 fallback 키와 auth 오류 정규화를 대조했다. 로그아웃 실패의 화면 미표시를 명시했다. |
| L462 | 요청별 확인은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 기본 문구·실패 정규화 L15~19 L97~113 L177~218 ; nori-tc-ui-front/src/features/auth/types/auth.types.ts @ 9fe69a75dcd3 ; defaultAuthErrorMessage L38~43 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 일반 실패 문구 L837~838 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |
| L464 | 로그아웃 대체 문구는 API 내부에 있으나 현재 상단바에서 실패 안내로 표시하지 않는다. | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 기본 문구·실패 정규화 L15~19 L97~113 L177~218 ; nori-tc-ui-front/src/features/auth/types/auth.types.ts @ 9fe69a75dcd3 ; defaultAuthErrorMessage L38~43 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 일반 실패 문구 L837~838 ; nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35 | 호출 시점 fallback 키와 auth 오류 정규화를 대조했다. 로그아웃 실패의 화면 미표시를 명시했다. |

### 화면 표시 실패 안내

오류 경계의 렌더 실패 포착·한국어 표시·window.location.reload를 대조했다. 라우터 오류 경계 배선도 읽었다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L472 | 「화면을 표시하지 못했습니다.」는 화면 렌더링이나 화면 파일 로드가 실패했을 때 오류 경계가 표시하는 안내다. | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 렌더 오류 복구 L14~55 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 복구 문구 L477~479 | 오류 경계의 렌더 실패 포착·한국어 표시·window.location.reload를 대조했다. 라우터 오류 경계 배선도 읽었다. |
| L472 | 이 안내 자체에는 서버 errorCode가 없다. | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 렌더 오류 복구 L14~55 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 복구 문구 L477~479 | 오류 경계의 렌더 실패 포착·한국어 표시·window.location.reload를 대조했다. 라우터 오류 경계 배선도 읽었다. |
| L474 | 같은 영역의 「새로고침」은 페이지 전체를 다시 읽는다. | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 렌더 오류 복구 L14~55 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 복구 문구 L477~479 | 오류 경계의 렌더 실패 포착·한국어 표시·window.location.reload를 대조했다. 라우터 오류 경계 배선도 읽었다. |
| L474 | 재발하면 화면·시각·직전 조작을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 렌더 오류 복구 L14~55 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 복구 문구 L477~479 | 오류 경계의 렌더 실패 포착·한국어 표시·window.location.reload를 대조했다. 라우터 오류 경계 배선도 읽었다. |
| L474 | 상세 구별은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다. | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 렌더 오류 복구 L14~55 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 복구 문구 L477~479 | 소유 문서 objectKey를 확정 목록·기존 문서와 대조한 연결 안내 |

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼 | 실제 코드 | 검색 원문 반영 |
|---|---|---|---|
| UNKNOWN_ERROR | U L2533은 서버 응답 형식 불일치로 설명한다 | api-error.ts L19~40·L67~79는 null/빈 코드·응답 부재도 정규화한다 | 공통 통신 절에 범위를 확장해 원인을 단정하지 않았다 |
| DEPLOY_ACTIVE_EQP_CONFLICT | U L2524는 진행 중 배포로 설명한다 | DeployJdbcStoreSupport L253~254는 READY·RUNNING을 모두 조회한다 | 실행 대기 배포도 충돌 대상이라고 명시했다 |
| CATALOG_NOT_SYNCED 조치 | U L2531은 잠시 뒤 재시도를 우선 안내한다 | UiCatalogNotSyncedException L5~10은 서버 정의 불일치와 운영 반영이 필요한 조건을 설명하고, UiCatalogHeadSynchronizer L132~140은 불일치 시 거부한다 | 재시도만으로 회복한다고 쓰지 않고 담당자에게 반영 상태 확인을 요청하도록 썼다 |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 생략 이유 | 확인할 역할 |
|---|---|---|
| AUTH_FAILED의 실제 발생 조건과 사용자 조치 | ApiResponse.java L40의 설명 예시만 확인됐고 실제 응답 생성 근거를 확보하지 못했다. 이름으로 뜻을 만들지 않았다 | UI 백엔드 담당 |
| 현장에 배포된 판본과 실제 장애 원인 | 이번 조사는 저장소 HEAD 기준이며 서버·현장 상태를 조회하지 않았다 | TC 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 사실 | 소유 objectKey |
|---|---|
| 로그인 순서·입력 제한·프로필 수정·로그아웃 조작 | 00.rag/user/login-and-screen-basics.md |
| 증상을 가르는 인증·권한·화면 진단 전체 | 00.rag/user/login-permission-and-screen-troubleshooting.md |
| 메뉴·요청별 권한 코드 표와 실제 계정 부여 확인 | 00.rag/user/permission-code-reference.md |
| 설비 시작·종료·재연결·조회 복원·파라미터 편집 절차 | 00.rag/user/equipment-commands-and-polling.md / 00.rag/user/equipment-model-parameter-and-jar-update.md |
| 모델 편집·MDF 업로드·branch 관리 절차 | 00.rag/user/model-details-and-flow-editing.md / 00.rag/user/model-registration-and-branching.md |
| 배포 등록·실행·취소·재실행과 증상 판별 절차 | 00.rag/user/deployment-workflow.md / 00.rag/user/deployment-troubleshooting.md |
| Gateway·Business DLQ의 사유 분류 표와 격리 확인·정리 절차 | 00.rag/user/dlq-quarantine-and-repair-task.md / 00.rag/user/dlq-quarantine-troubleshooting.md |
| 데모 시작 대기·점유·종료·진행 상태의 진단 절차 | 00.rag/user/demo-troubleshooting.md |
| 상태값의 전체 뜻 | 00.rag/user/state-reference.md |
| 관리용 작업 조회의 OPERATION_NOT_FOUND 및 운영 조정의 복구 절차 | 00.rag/admin/coordination-troubleshooting.md |

관리용 CoordinationAdminController의 오류를 user 본문의 운영 접근 조치로 옮기지 않았다. Gateway·Business가 응답으로 전달하는 개별 실패 코드 전체를 이름만으로 해석해 복제하지 않았다. 설비 직접 실행의 PUBLISH_FAILED는 발생 경로를 한정해 DLQ의 같은 이름 분류와 혼동하지 않도록 했다.

## 검사 결과

아래 최초 작성 검사 출력은 당시 판본의 이력이다. 이번 보완의 검사 결과는 마지막 보완 기록에 따로 남긴다.

기계 검사 명령:
`bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/error-message-and-code-reference.md`

- 검사 실행 환경: Git Bash, OpenJDK Temurin 21.0.9+10-LTS, 검사기가 지정한 commonmark 0.32.0 및 현행 mask-v1.
- 최초 출력 회수 가능한 검사: exit 1, 데모 호출 오류 절의 부모 451자 1건. 문장을 줄여 해결했다.
- 그보다 앞선 동일 검사 호출은 실행 세션 식별자를 회수하지 못해 종료 결과 미확인이다. 별도 PASS 근거로 사용하지 않았다.
- 최종 검색 원문 편집 뒤 검사: exit 0, sections=33, children=117, maxChild=179, maxParent=445, violations=0. CROSS VIOLATION·MASK 출력 없음.
- 마지막 출력 10줄:
```text
    L442-442 표 행 76  | 대체 문구=로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. | 의미=로그인 실패에서 사용할 구체 문구를 얻지 못했다.
    L443-443 표 행 78  | 대체 문구=로그아웃 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. | 의미=로그아웃 실패에서 사용할 구체 문구를 얻지 못했다.
    L445-445 문단 107  | 문구만으로 비밀번호 불일치나 네트워크 장애를 확정하지 않는다. 요청별 확인은 00.rag/user/login-permission-and-screen-troubleshooting.md를 따른다.
    L447-447 문단 49  | 로그아웃 대체 문구는 API 내부에 있으나 현재 상단바에서 실패 안내로 표시하지 않는다.
§ L449-457 [11] 화면 표시 실패 안내   parent=225 children=2 sources=2
    L455-455 문단 89  | 「화면을 표시하지 못했습니다.」는 화면 렌더링이나 화면 파일 로드가 실패했을 때 오류 경계가 표시하는 안내다. 이 안내 자체에는 서버 errorCode가 없다.
    L457-457 문단 135  | 같은 영역의 「새로고침」은 페이지 전체를 다시 읽는다. 재발하면 화면·시각·직전 조작을 운영 담당자에게 전달한다. 상세 구별은 00.rag/user/login-permission-and-screen-troubleshooting.md를 따른다.
SUMMARY sections=33 children=117 maxChild=179 maxParent=445
CORPUS docs=43/53 sections=625 part={admin=9, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=5, 트러블슈팅=12}
TOTAL files=43 targets=1 violations=0
```

### 사람 검사

최초 33개 절과 이번 추가 절을 포함한 34개 절 전체를 다시 읽었다. 각 절을 제목만으로 질문을 아는지·원천과 맞는지·독립 답인지·다른 절/앞선 문서와 겹치는지 네 축으로 판정했다. 아래 근거는 위 사실 대조와 연결된다.

| 절 | 질문·원천·독립 답·중복 판정 | 판정 근거 |
|---|---|---|
| 인증 거부 UNAUTHORIZED | 적합·일치·독립·복제 없음 | 로그인 use case의 계정 부재·비밀번호 불일치·계정 상태 분기와 401 응답, 로그인 폼 표시를 구별했다. |
| 현재 사용자 조회·인증 처리의 UNAUTHORIZED 문구 | 적합·일치·독립·복제 없음 | 현재 사용자 조회의 인증 정보·principal 검사와 인증 필터의 예외 응답을 각각 대조했다. |
| 로그인 보안 요청이 403으로 거부된 코드 | 적합·일치·독립·복제 없음 | 403 변환은 로그인 전 토큰 발급과 로그인 제출 두 문맥으로 분리했다. 문구의 첫 문장만 인용했다. |
| 로그아웃 보안 거부와 보안 토큰 누락 | 적합·일치·독립·복제 없음 | 로그아웃 두 문맥·토큰 쿠키 부재를 대조했다. AppShellPage가 로그아웃 오류를 소비하므로 화면 표시를 단정하지 않았다. |
| 권한 거부 FORBIDDEN과 화면 접근 안내 | 적합·일치·독립·복제 없음 | 대상별 예외의 FORBIDDEN과 화면 접근 안내, API 권한 캐시의 네 거부 분기를 분리했다. |
| 입력·요청 형식 오류 INVALID_REQUEST | 적합·일치·독립·복제 없음 | Bean Validation·파라미터 제약·JSON 해석·표준 요청 오류의 기본 번들 및 코드 변환을 확인했다. |
| 공통 요청 오류의 형식·시간 초과 문구 | 적합·일치·독립·복제 없음 | 타입 불일치의 항목명 삽입과 표준 4xx·503의 메시지/코드 변환을 대조했다. |
| 대상 없음 NOT_FOUND와 현재 상태 충돌 CONFLICT | 적합·일치·독립·복제 없음 | UiNotFoundException·UiConflictException 및 표준 404 변환과 매뉴얼 조치를 대조했다. |
| 설비 작업의 같은 요청 키·기준 상태 충돌 | 적합·일치·독립·복제 없음 | 요청 내용 해시 충돌과 기준 revision 불일치를 생성 분기와 정의에 연결했다. |
| 설비 작업 점유 RESOURCE_BUSY·EQUIPMENT_EXECUTING | 적합·일치·독립·복제 없음 | 설비 작업 점유와 배포 실행 점유의 다른 분기 및 공통 409 변환을 대조했다. |
| 미종결 작업 차단 RESOURCE_BLOCKED | 적합·일치·독립·복제 없음 | 미종결 차단 분기·blockedByOperationId 문구와 매뉴얼 Repair Task 확인 경로를 대조했다. |
| 작업 조회 불가 COORDINATION_UNAVAILABLE·COORDINATION_DISABLED | 적합·일치·독립·복제 없음 | 접근 불확실 예외와 조정 기능 없는 구성의 조회 503을 구별했다. 404로 해석하지 않았다. |
| 서버 정의 불일치 CATALOG_NOT_SYNCED | 적합·일치·독립·복제 없음 | 정의 불일치 예외·503 변환을 대조했다. 추가로 UiCatalogHeadSynchronizer.requireInSync L132~140의 실제 throw를 읽었다. |
| 설비 파라미터 잠금 ALREADY_CHECKED_OUT | 적합·일치·독립·복제 없음 | EDIT 존재 검사·체크아웃 예외·409 및 편집자 유무에 따른 문구를 연결했다. |
| 설비 생성 준비 단계의 거부 코드 | 적합·일치·독립·복제 없음 | 생성 준비 UNEXPECTED_STATE·REJECTED와 참여 서버 거부 응답의 null 코드 대체를 실제 abort 인자와 대조했다. |
| 작업 결과 코드 REPAIR_REQUIRED와 오류 상세 | 적합·일치·독립·복제 없음 | 작업 상태와 오류 코드의 중복 이름을 구별했다. markRepairRequired의 오류 코드와 세부 오류 조합, 화면의 repairReason 우선을 대조했다. |
| 설비 직접 실행의 발행·응답 실패 | 적합·일치·독립·복제 없음 | 직접 실행의 발행·응답 대기 catch 분기 및 비동기 등록 오류를 대조했다. 작업 조회의 지연과 같은 의미로 쓰지 않았다. |
| 설비 직접 실행 후 저장 확정 실패 | 적합·일치·독립·복제 없음 | 단계 실행 뒤 finalizeSuccess에서 발생하는 예외 분기만 설명했다. 전체 저장 성공을 주장하지 않았다. |
| 모델 체크아웃 충돌 CHECKOUT_CONFLICT | 적합·일치·독립·복제 없음 | 모델 EDIT 편집자 검사·409 뒤 목록 재확인·checkout/checkin 오류 생성과 한국어 두 문구를 대조했다. |
| 모델 저장 대상 오류 INVALID_MODEL_VERSION·INVALID_NODE | 적합·일치·독립·복제 없음 | EDIT 아닌 저장/업로드와 MDF 일반 저장·지원 상세 응답 부재를 각각 대조했다. |
| PLC 순서 값 저장 거부 INVALID_REQUEST | 적합·일치·독립·복제 없음 | 정확한 오류 문구·400 코드·거부 칸·저장 전 거부를 대조했다. 선택 절차는 소유 문서로 연결했다. |
| 배포 설비 중복 DEPLOY_ACTIVE_EQP_CONFLICT | 적합·일치·독립·복제 없음 | 충돌 조회 SQL이 READY·RUNNING을 모두 포함하는 것을 확인했다. 매뉴얼의 진행 중 배포 표현을 구체화했다. |
| 배포 실행 정보·응답 조회 오류 | 적합·일치·독립·복제 없음 | 계획 조회 Optional.empty와 응답 조회 INFRA_ERROR를 별도 코드로 대조했다. |
| 배포 단계 실패 STEP_TIMEOUT·STEP_FAILED | 적합·일치·독립·복제 없음 | EXPIRED 등록·저장 기한 도달·실패 응답의 코드/문구 null 대체를 대조했다. |
| 처리실패 목록의 조회 오류 문구 | 적합·일치·독립·복제 없음 | 네 목록의 fallback 키·한국어 문구·실패 payload의 errorMsg 우선 및 오류 영역의 재시도를 확인했다. |
| Repair Task에 남는 수동 복구·보상 발행 코드 | 적합·일치·독립·복제 없음 | 복구 단계 MANUAL_ONLY/복구 action 부재와 보상 발행 예외의 Repair Task 기록을 확인했다. |
| Repair Task의 보상 대기·응답 처리 실패 | 적합·일치·독립·복제 없음 | 보상 대기의 Timeout/Interrupted/Cancellation/Execution 예외 기록과 표의 에러 코드·문구 열을 확인했다. |
| 데모 점유 충돌·실행 부재·호출 오류 | 적합·일치·독립·복제 없음 | 시뮬레이터 호출의 409·404·다른 4xx 및 IO/Interrupted 예외를 UI 예외와 대조했다. 입력 오류 하나로 해석하지 않았다. |
| 데모 시작·조회·종료의 기본 실패 문구 | 적합·일치·독립·복제 없음 | 실행 생성·목록/상세·종료의 API fallback 및 시작 팝업·종료 안내 영역의 오류 전달을 대조했다. |
| 공통 통신 UNKNOWN_ERROR와 INVALID_RESPONSE | 적합·일치·독립·복제 없음 | createFailResponse의 null/빈 코드·응답 해석 실패 및 normalizeApiError의 응답 부재, 목록 구조 검사 분기를 대조했다. |
| 서버 내부 오류 INTERNAL_ERROR | 적합·일치·독립·복제 없음 | 미분류 500·표준 5xx 코드 변환과 한국어 내부 오류 문구를 대조했다. |
| 작업 조회의 요청한 작업을 찾을 수 없습니다 | 적합·일치·독립·복제 없음 | 작업 조회 404의 프런트 대체 문구와 서버 found.empty, 다른 실패의 fallback을 대조했다. |
| 로그인·로그아웃의 일반 실패 문구 | 적합·일치·독립·복제 없음 | 호출 시점 fallback 키와 auth 오류 정규화를 대조했다. 로그아웃 실패의 화면 미표시를 명시했다. |
| 화면 표시 실패 안내 | 적합·일치·독립·복제 없음 | 오류 경계의 렌더 실패 포착·한국어 표시·window.location.reload를 대조했다. 라우터 오류 경계 배선도 읽었다. |

- 제목 34개는 서로 다른 코드 묶음·조건·주체를 직접 드러낸다. 번호만 달리 붙인 제목을 쓰지 않았다. 동일한 제목 또는 정보 없는 반복 제목을 발견하지 않았다.
- 본문·표 행은 코드의 생성·응답·화면 소비 조건에 연결했다. 이름 토막을 번역해 의미를 만들지 않았다.
- 표의 한국어 문구·대소문자·띄어쓰기를 원천과 대조했다. 동적 편집자·대상 식별값은 실제 값 대신 문구의 고정 부분과 조건을 썼다.
- 목록 오류의 재시도 단추, 오류 경계의 새로고침, 서버 문구 우선 조건을 확인했다. 로그아웃 코드·문구는 생성돼도 현재 화면이 표시하지 않는다는 소비 경계를 포함했다.
- 자체 및 교차 본문 중복은 검사기 0건이고, 사람이 읽은 의미 중복은 참조에 필수인 짧은 조건·확인 경로만 남겼다. 앞선 트러블슈팅의 상세 절차를 복제하지 않았다.
- user 검색 원문에는 admin objectKey·운영 접속 정보·내부 조작 명령·실제 개인정보·비밀값을 쓰지 않았다. 담당자 인계에 필요한 대상·시각·표시 결과만 안내했다.
- 오류 코드 의미를 쓰는 문장은 발생 분기와 대조했으며, 시험 미실행을 성공으로 표시하지 않았다.
- 상태·권한·사유 분류 전체 표는 소유 문서로 연결했다. 다른 문서 수정은 수행하지 않았다.

### 수행·정리 범위

- 하위 Agent·독립 AI CLI 호출: 0회. 지정된 독립 작성 세션에서 직접 조사·작성했다.
- 제품 빌드·시험·브라우저·Docker·kubectl·SSH·네트워크·업로드·Git 발행: 실행하지 않았다. 문서 검사는 실제 실행했다.
- 작업 전체 wall time과 provider 토큰/비용은 이 세션 도구가 확정 값을 제공하지 않아 미확인이다.
- 검사용 임시 파일·전용 디렉터리·서비스를 생성하지 않았다. 기록 집계 Python은 stdin으로만 실행하고 작성 기록에 직접 썼다. 삭제할 소유 임시 자원은 없다.
- 기존 다른 파일과 실행 중 프로세스를 변경·종료하지 않았다.
- 경로/소스 상태 집계 helper의 최초 Python 실행은 PowerShell stdin 인코딩 때문에 정규식에서 실패했다. OutputEncoding을 UTF-8로 지정한 후 조회를 완료했다. 실패 실행은 파일을 만들지 않았다.
- 사실 대조 표 레코드: 186개. 본문 문장은 문장 단위, 표는 자료 행 단위로 기록했다.
- 남은 작성 결함: 0건. 사용자 결정 필요: 0건. 외부 메인 세션의 독립 대조는 아직 수행된 것으로 기록하지 않는다.


## 2026-11-19 제품 결함 수정 D 보완 기록

- 변경 전 검색 원문 SHA-256: `11d0f6a6b3185b6f953e99882a8995a3d8a0566bad63aac2d42fc06be5bd09ce`.
- 변경 전 작성 기록 SHA-256: `a90455b7814412cbcf8b0bd8153a691062bd5fa0bfc9b1f821d2f358ace7fa6c`.
- 범위 판단: 이 문서는 모델 상세 저장의 코드·문구·확인할 칸을 소유하므로 D 거부 사유를 추가했다. 값 해석 표·선택 절차·복사 경로·V9 migration은 각 소유 문서의 범위다.
- 최소 수정: H1 적용 한 줄과 PLC 오류 절 하나만 추가했다. 확인일은 이미 2026-11-19여서 보존했다. 추가 절과 H1 적용 변경을 제외한 원문이 변경 전 문자열과 완전히 같음을 비교했다. 기존 33절과 원천 commit·줄은 그대로다.
- A~D 영향 확인: 문서 전체를 다시 읽었다. A의 Kafka 보안 설정 전달·B의 Secret 생성 linger·C의 HSMS/Socket 설정 적용을 서술한 기존 문장은 없었다. 일반 PUBLISH_FAILED 등의 발생 설명도 이 수정으로 거짓이 되지 않는다. A~C의 내부 설정·처리 오류 목록은 새로 추가하지 않았다.
- 실제 흐름 대조: ModelPage.handleSaveDetailRows L575~601 → model.api.ts L297~330의 상세 PUT → ModelDetailController L55~115 → JpaModelDetailCommandPort L150~174·L477~528 → PlcRegisterOrder L12~20 → UiApiExceptionHandler L89~97. 한국어 오류는 enum에서 조립되며 별도 i18n 번들 문자열이 아니다.
- 기대값 확인: JpaModelDetailCommandPortTest L544~585를 읽었다. 공백·대소문자 정규화, 빈 값 null 유지, 두 순서 칸의 잘못된 값에 대한 전체 요청 거부와 delete/upsert 미호출을 확인한다. 이번 문서 세션에서 제품 시험을 실행하지 않았다.
- 관련 문서: model-details-and-flow-editing.md L126~158은 이미 D의 거부·선택 조건을 보완했다. 이 참조에는 오류 식별 정보만 추가하고 절차 본문은 복제하지 않았다. 이번 영향 조사에서 다른 문서를 고칠 필요는 확인하지 못했다.
- 사람 검사: 34절의 제목·독립 답·중복·문장·권한 경계를 읽었다. 새 절은 변경 판본 조건과 원문 고정 접미부, 행·칸 식별, 기존 행 변경 전 거부만 설명한다. 허용값 해석 표를 복제하지 않았다.
- 검사기 위치: 최초 Temp 경로의 check.sh는 현재 없었다. 제공된 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`와 RagDocCheck.java를 읽고 수정 없이 사용했다.
- 최초 보완 검사: exit 1, 기존 원천 포함 81건의 commit:path 조회 실패. 직접 git show의 원문 오류는 `fatal: detected dubious ownership in repository at 'C:/workspace_nori/nori-tc/nori-tc-ui-front'`였다. RagDocCheck.java L389~390·L405~410이 git -C show 실패를 원천 부재로 보고하는 것을 확인했다.
- 재검사 조건: 기존 Temp 실행 스크립트에 있던 프로세스 한정 safe.directory 전달이 현재 스크립트에는 없다. 전역 설정·검사기·문서 근거를 바꾸지 않고, 실행 프로세스의 GIT_CONFIG_COUNT=3과 GIT_CONFIG_KEY_n=safe.directory·GIT_CONFIG_VALUE_n=백엔드/프런트/exec의 정확한 절대경로를 지정했다. 이 조건에서 같은 검사를 실행해 결과를 회수한다.
- 임시 자원: 검사용 파일·폴더·서비스를 새로 만들지 않았다. 기존 work와 다른 세션 기록은 보존했다. 하위 Agent·독립 CLI 호출은 0회, 제품 수정·다른 문서 변경·커밋·업로드는 없다.
- 기록 보완 helper 첫 실행은 문장 분할이 인용문 안 마침표도 나눠 예상 문장 수 assertion에서 멈췄다. 쓰기 전 assertion이므로 파일 변경은 없었다. 인용된 두 문장을 각각 같은 원천에 연결하여 보완했다.
- 최종 검사 결과: exit 0, 34절·자식 121개·최대 자식 179자·최대 부모 445자·위반 0건. CROSS VIOLATION·MASK 출력 없음.
- 최종 명령: 아래 프로세스 환경을 지정한 뒤 `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/error-message-and-code-reference.md`를 실행했다. 검사기·전역 Git 설정은 바꾸지 않았다.

```powershell
$env:GIT_CONFIG_COUNT='3'
$env:GIT_CONFIG_KEY_0='safe.directory'
$env:GIT_CONFIG_VALUE_0='C:/workspace_nori/nori-tc/nori-tc-ui-backend'
$env:GIT_CONFIG_KEY_1='safe.directory'
$env:GIT_CONFIG_VALUE_1='C:/workspace_nori/nori-tc/nori-tc-ui-front'
$env:GIT_CONFIG_KEY_2='safe.directory'
$env:GIT_CONFIG_VALUE_2='C:/workspace_nori/nori-tc/nori-tc-exec'
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/error-message-and-code-reference.md
```

최종 출력 마지막 10줄:

```text
    L459-459 표 행 76  | 대체 문구=로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. | 의미=로그인 실패에서 사용할 구체 문구를 얻지 못했다.
    L460-460 표 행 78  | 대체 문구=로그아웃 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. | 의미=로그아웃 실패에서 사용할 구체 문구를 얻지 못했다.
    L462-462 문단 107  | 문구만으로 비밀번호 불일치나 네트워크 장애를 확정하지 않는다. 요청별 확인은 00.rag/user/login-permission-and-screen-troubleshooting.md를 따른다.
    L464-464 문단 49  | 로그아웃 대체 문구는 API 내부에 있으나 현재 상단바에서 실패 안내로 표시하지 않는다.
§ L466-474 [11] 화면 표시 실패 안내   parent=225 children=2 sources=2
    L472-472 문단 89  | 「화면을 표시하지 못했습니다.」는 화면 렌더링이나 화면 파일 로드가 실패했을 때 오류 경계가 표시하는 안내다. 이 안내 자체에는 서버 errorCode가 없다.
    L474-474 문단 135  | 같은 영역의 「새로고침」은 페이지 전체를 다시 읽는다. 재발하면 화면·시각·직전 조작을 운영 담당자에게 전달한다. 상세 구별은 00.rag/user/login-permission-and-screen-troubleshooting.md를 따른다.
SUMMARY sections=34 children=121 maxChild=179 maxParent=445
CORPUS docs=51/53 sections=873 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

- 최종 문서·원천 guard: PLC 저장·검사·목록 파일 3개의 SHA-256을 다시 계산해 메타에 인용한 값과 일치함을 확인했다. 변경 전후 비교에서 H1 적용과 새 PLC 오류 절 외의 원문은 동일하다.
- 검사기 실행은 보완 범위에서 2회다. 첫 실행은 Git 원천 조회 실패, 두 번째는 위반 0건으로 완료했다. 제품 시험은 실행하지 않았다. 전체 작업 wall time·provider 토큰/비용은 미확인이다.

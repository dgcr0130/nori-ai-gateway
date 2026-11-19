# 로그인·권한·화면 오류 작성 기록

## 대상

- objectKey: `00.rag/user/login-permission-and-screen-troubleshooting.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/login-permission-and-screen-troubleshooting.md`
- 검색 원문 SHA-256: `7b39097837e43b28adc8ca40f6ff98d2d8c8fb7142600e9614b655efdd271748`
- 검색 원문: 13,082바이트, 148줄, H2 10절, 자식 30개
- 확인일: 2026-11-19 한국 시간
- 작성 범위: 로그인 실패·인증 확인 실패·권한 거부·API 실패 문구·화면 렌더 실패를 구별하는 경로. 업로드와 제품 수정은 수행하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 결과 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 본문 메타의 서로 다른 원천 파일 18개를 정확한 경로 인자로 `git status --short -- <경로...>` 확인. exit 0, 변경 출력 없음. |
| nori-tc-ui-backend | 985d17c604dd | 본문 메타의 원천 파일 7개를 같은 방식으로 확인. exit 0, 변경 출력 없음. |
| nori-tc-exec | f1d5e0a8e727 | 본문 메타의 원천 파일 3개를 같은 방식으로 확인. exit 0, 변경 출력 없음. |

쓰기 전에는 front의 src/app·auth·profile·eqp·shared·ko.json·관련 단위 시험, backend의 web-adapter·core 원천, exec의 사용자 매뉴얼·기본 권한 catalog·DB 시드 경로를 먼저 조회해 변경 출력이 없음을 확인했다. 작성 뒤에는 메타에 실제 사용한 28개 파일을 추출해 파일별 인자로 다시 확인했다. 변경 파일이 없어 파일 SHA 판본으로 대체한 원천은 없다.

최초 형제 저장소 Git 조회는 소유자 불일치로 거부됐다. 전역 설정은 변경하지 않았고, 이후 명시한 로컬 저장소마다 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C <저장소> ...`로 읽기 전용 조회했다. Git의 사용자 ignore 파일 접근 경고가 있었으나 위 경로 상태 조회는 exit 0이었다.

`335473ea68b49ff1ed1b1075046e0dee691a1d6d`의 변경 코드와 `tests/unit/api-void-endpoint-contract.test.ts` 기대값을 읽었다. 특히 Undo의 `resolveSuccessPayload<null>` 호출이 `assertSuccessResponse`로 바뀐 diff를 확인하고 현재 HEAD의 호출·성공 후 조회 갱신·백엔드 null 성공 응답과 대조했다. 커밋 메시지의 과거 시험 통과 수를 이번 세션의 시험 결과로 재사용하지 않았다.

먼저 작성 규칙과 문서 목록을 읽었다. 겹치는 기존 검색 원문은 `login-and-screen-basics.md`, `user-group-and-permission-management.md`, `equipment-model-parameter-and-jar-update.md`, `equipment-registration-and-settings.md`를 읽었다. 기본 로그인 문서의 입력 오류·인증 이동·메뉴·표시 실패 안내와, 권한 관리 문서의 실제 부여·조회 실패·저장 거부 설명을 대조했다.

## 절별 사실 대조

각 행은 검색 원문의 해당 줄 전체를 대조한다. 한 줄에 여러 문장이 있는 경우 확인 내용에 각 문장의 근거와 조치 성격을 함께 적었다. 사용자에게 증상·시각·조작·대상을 넘기도록 한 문장은 원천의 진단 분기에서 필요한 값을 정한 안내이며, 현장 장애를 관측했다는 주장이 아니다. 연결한 objectKey의 소유 범위는 `docs/tc-rag-document-list.md`의 확정 목록과 대조했다.

### 로그인 입력 문제와 계정 문제를 가르는 순서

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 18 | 입력 안내를 먼저 고치고 기본 절차 참조 | nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 L22~33; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 L200~298 | 입력 검사와 칸 아래 오류 렌더가 로그인 요청보다 앞선다. 조건·조작의 상세는 기존 기본 문서에 남겼다. |
| 20 | 동일한 인증 거부 문구로 세 원인을 확정할 수 없음 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd L87~115; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd L133~138; nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 L293~298 | 사용자 없음과 비밀번호 불일치가 같은 문구다. 아이디 오입력도 사용자 없음 또는 다른 계정 비밀번호 불일치로 귀결될 수 있어 입력 재확인을 안내했다. 서버 문구가 로그인 단추 아래 표시된다. |
| 22 | ACTIVE가 아닌 계정의 별도 거부·관리자 인계 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd L102~107 | 계정 상태가 ACTIVE가 아니면 비밀번호 검증 전에 별도 문구로 거부한다. 상태 확인은 관리자에게 맡기고 관리 절차를 복제하지 않았다. |

### 로그인 보안 검증 안내가 나올 때 확인할 요청

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 32 | 새로고침 후 재시도와 지속 증상 인계 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L839~841 | 세 문구 모두 새로고침 후 재시도를 안내한다. 내부 설정 조작은 쓰지 않고 지속 시각·문구 인계로 한정했다. |
| 34 | 로그인 전 보안 토큰 발급 요청의 403 | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 L27~35 L83~90 L129~130; nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 L42~43; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L840~840 | login-csrf 문맥의 403 변환과 선행 GET 경로, 실제 한국어 문구 시작 부분이 일치한다. |
| 35 | 로그인 제출 요청의 403 | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 L36~41 L65~75 L133~160; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L841~841 | POST 403에서 login-submit 안내로 변환한다. 문구 전체의 내부 설정 안내는 본문에 복제하지 않았다. |
| 36 | 발급 요청 뒤 보안 토큰 읽기 실패 | nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 L55~73; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L839~839 | 요청 완료 뒤 쿠키를 읽지 못하면 CSRF_TOKEN_MISSING을 던진다. 쿠키 실제 값은 기록하지 않았다. |
| 38 | 보안 안내만으로 자격·업무 권한 원인 확정 금지 | nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 L65~75; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd L123~125 | 로그인 공개 요청의 403을 문맥으로 변환하므로 계정 검증 결과와 일대일 대응하지 않는다. 내부 점검은 운영 담당 역할에 한정했다. |

### 업무 중 로그인 화면으로 돌아오면 세션 만료인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 48 | 공통 401 실패 처리와 현재 사용자 조회 실패의 이동 | nori-tc-ui-front/src/shared/lib/api-client.ts @ 9fe69a75dcd3 L41~51; nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 L19~35 | 실패 인터셉터의 401 이동과 인증 가드의 isError 또는 자료 없음 이동을 확인했다. 모든 API의 401이 인터셉터를 탄다고 일반화하지 않았다. |
| 50 | 세션 만료 외 원인 후보 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd L168~200; nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 L31~33 | 쿠키 없음, 무효 인증, 예기치 않은 인증 오류가 별도 경로다. 가드는 현재 사용자 조회 실패 원인 전체를 세션 만료로 제한하지 않는다. |
| 52 | 현재 사용자 조회 응답·시점 인계와 자격 값 제외 | nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 L13~26; nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 L21~33 | 조회 경로를 확인해 인계할 요청을 특정했다. 비밀번호·쿠키·보안 토큰 값을 넘기지 않도록 작성 규칙의 비밀값 제외를 적용했다. |

### 로그인은 성공했는데 현재 사용자 조회가 403일 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 63 | 로그인 뒤 /auth/me 403과 기본 AUTH_ME_PERM 연결 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L33~40; nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 L63~65 L134~145; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd L123~143 L251~282 | GET /api/auth/me는 공개 경로가 아니며 기본 활성 API 권한·기본 그룹 시드 연결을 확인했다. 현장 DB 권한 부여는 확인하지 않았다고 경계를 명시했다. |
| 65 | 별도 조회 거부로도 로그인 복귀·인계할 증거 | nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 L13~26; nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 L21~33 | /auth/me 요청을 별도로 보내며 실패가 인증 가드 이동을 유발한다. 로그인 성공과 후속 조회 거부를 분리해 전달하도록 했다. |

### 메뉴가 없거나 접근 권한 안내가 나올 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 76 | 현재 사용자 권한으로 화면 가드와 안내 표시 | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L146~152; nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 L19~40; nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 L25~34; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1104~1106 | 권한 목록으로 메뉴를 필터하고 화면은 안내를 렌더한다. 로그인 이동을 반환하지 않는 분기다. 문구와 필요한 코드 표시 위치를 확인했다. |
| 78 | 필요한 코드 인계·메뉴 전부 없을 때 안내 | nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 L27~32; nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L146~170 | 안내가 코드를 드러내며 접근 메뉴가 없으면 첫 경로로 보내 권한 가드가 안내한다. 현장 계정이 실제로 이 상태라고 주장하지 않았다. |
| 80 | 권한 코드·부여 방법은 소유 문서 참조 | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L34~34 L72~72 | 목록의 permission-code-reference와 user-group-and-permission-management 역할에 연결했다. 이 행은 동작이 아니라 문서 위치 안내다. |

### 화면은 열리지만 조회나 저장 요청이 403일 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 90 | 화면 가드와 서버 API 인가 분리 | nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 L19~40; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd L251~282; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd L190~198 | 화면의 permissionCode 판정과 요청 URI·메서드 인가는 각각 실행된다. 403에서 입력만 반복 수정하지 않도록 분기 안내했다. |
| 92 | API 403의 여러 원인 후보 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~185 | 초기화 실패·카탈로그 불일치·요청 권한 매칭 없음·보유 코드 불일치가 모두 false를 반환한다. 동일 거부 결과만으로 원인을 특정하지 않았다. |
| 94 | 요청 증거·기존 부여 안내를 운영 담당자에게 전달 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~185; nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L67~79 | 서버 판정에는 URI·메서드·사용자·권한 상태가 필요하고 오류에는 상태·서버 문구가 보존된다. 사용자는 내부 캐시 조작을 하지 않고 재현 정보를 전달한다. |

### 서버 오류와 입력·대상 문제를 어디서 가르는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 103 | 문구·코드 대조와 입력·대상·서버 오류 인계 구별 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd L89~115 L208~216 L324~332; nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40 | 입력 오류·충돌·미존재는 서로 다른 응답으로 변환한다. 미처리 예외는 500/INTERNAL_ERROR다. 본문에서 각 코드의 상세 사전은 복제하지 않았다. |
| 105 | 동일 문구라도 코드·상태 보존, 상세 사전 참조 | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L2515~2533 | 오류 코드와 오류 문구는 별도 값이며 문구가 없으면 기본값으로 바뀐다. 오류 코드 표의 소유 문서로 안내했다. |

### 기본 실패 문구만 나오면 네트워크 문제인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 115 | 기본 로그인 오류의 실제 문구·발생 범위 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L837~837; nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40; nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 L97~113 | 유효한 실패 응답의 빈 문구와 실패 봉투가 아닌 응답 모두 fallback을 쓴다. 네트워크만의 표시라고 단정하지 않았다. |
| 117 | 응답 유무로 조사 분리·10초 제한 | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L67~79; nori-tc-ui-front/src/shared/lib/api-client.ts @ 9fe69a75dcd3 L14~21 | Axios 오류의 response는 없을 수 있고 status도 선택값이다. timeout=10_000을 초 단위로 풀었다. 무응답의 원인을 네트워크로 확정하지 않고 조사 후보로 썼다. |
| 119 | UNKNOWN_ERROR의 원인 단정 금지·응답 유무 인계 | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40 L67~79 | 오류 코드가 없거나 정상 실패 봉투가 아닌 값 모두 UNKNOWN_ERROR를 만들 수 있다. 단일 원인 코드가 아니다. |

### 화면 표시 실패와 API 실패 문구를 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 128 | 화면 렌더·파일 로드 오류의 전용 안내 | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 L14~29 L35~55; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L477~479; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L72~79 | 렌더 예외를 오류 경계가 잡고 전용 안내를 보여 준다. API 함수의 실패 문구 표시와 다른 경로다. |
| 130 | 새로고침은 전체 페이지, 배포 원인은 후보 | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 L14~22 L49~54; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L478~479 | 단추가 window.location.reload를 실행한다. 렌더 오류도 같은 경계에 잡히므로 배포 원인 확정·복구 보장을 쓰지 않았다. |
| 132 | 재현 화면·조작·새로고침 뒤 증상 인계 | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 L31~55 | 오류 경계의 복구 조작과 화면 위치를 기준으로 재현 정보를 정했다. 내부 로그 접근은 지시하지 않았다. |

### Undo 결과와 실패 표시가 서로 맞지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 144 | 반복 실행 전에 최신 체크아웃·파라미터 재조회 | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L979~992 L1283~1300; nori-tc-ui-front/src/features/eqp/hooks/useEqpParamMutations.ts @ 9fe69a75dcd3 L53~59 | Undo가 실패하면 화면 오류 상태를 남기며 성공하면 편집을 종료하고 관련 조회를 무효화한다. 결과와 표시가 어긋날 때 대상 상태부터 재확인하도록 안내했다. |
| 146 | 수정 이후 success=true/data=null 성공과 조회 갱신 | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L529~545; nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L82~92; nori-tc-ui-front/src/features/eqp/hooks/useEqpParamMutations.ts @ 9fe69a75dcd3 L53~59; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd L205~216; nori-tc-ui-front/tests/unit/api-void-endpoint-contract.test.ts @ 9fe69a75dcd3 L83~85 L101~125 | 현재 호출은 data의 null을 거부하지 않는다. 백엔드가 null 성공을 반환하고 단위 시험도 해당 성공·실패 기대를 명시한다. 335473ea 실제 diff와 대조했다. 시험은 읽었으며 실행하지 않았다. |
| 148 | 배포 판본 확인 인계·체크아웃 사용법 참조 | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L979~992; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L529~545 | 현장 수정 포함을 선언하지 않고 운영 담당자에게 비교할 수정 식별자와 증상을 전달한다. 체크아웃 자체 절차는 기존 소유 문서에 남겼다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 확인한 코드 | 검색 원문에 쓴 내용 |
|---|---|---|---|
| UNKNOWN_ERROR의 범위 | U §10.1 L2533은 서버 응답 형식이 화면이 아는 형태가 아닌 경우로 설명한다. | `api-error.ts` L19~40은 정상 실패 봉투에서도 errorCode가 비면 UNKNOWN_ERROR를 넣고, L67~79는 response 없는 Axios 오류도 정규화한다. | 한 원인으로 확정할 수 없으며 응답 유무·상태·문구를 함께 확인한다. 기본 실패 문구도 네트워크 전용 표시가 아니다. |

U §10.3의 화면 접근 안내와 API의 403은 같은 층의 판정이 아니다. 매뉴얼의 화면 안내 사실은 현재 렌더 코드와 일치한다. 본문은 여기에 서버 권한 캐시·등록·카탈로그 차단 경로를 추가해 사용자가 권한 미부여로 단정하지 않도록 했다. 카탈로그 불일치 예외가 503/CATALOG_NOT_SYNCED로 변환되는 별도 경로도 확인했지만, API 권한 캐시의 불일치는 false로 거부하므로 둘을 하나의 응답이라고 쓰지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 역할 |
|---|---|---|
| 현장 배포에 335473ea 수정이 포함됐는지 | 로컬 HEAD와 수정 diff만 확인했다. 현장 서버·배포 자료에 접속하지 않았다. | 운영·배포 담당자 |
| 현장 계정·그룹의 실제 권한 및 캐시·카탈로그 상태 | 저장소의 기본 catalog·시드는 현장 데이터가 아니다. 403 하나로 차단 원인을 특정할 수 없다. | 운영 관리자·서버 운영 담당자 |
| 현장 세션 만료 시각과 네트워크 장애의 실제 원인 | 소스 분기만으로 실제 쿠키·요청·연결 상태를 확정할 수 없다. 고정 세션 만료 시간이나 네트워크 진단 결과를 만들지 않았다. | 운영 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 이 문서에서 복제하지 않은 사실 |
|---|---|
| `00.rag/user/login-and-screen-basics.md` | 로그인 최소 입력 길이·아이디 저장·로그아웃·본인 정보와 비밀번호 변경·공통 표 조작의 실제 절차 |
| `00.rag/user/user-group-and-permission-management.md` | 계정 상태 변경과 그룹 편입·권한 체크·저장의 구체 조작, 프로필 부서 누락 해결 절차 |
| `00.rag/user/permission-code-reference.md` | 화면과 작업별 전체 권한 코드 표. AUTH_ME_PERM만 현재 사용자 조회 403의 판별에 필요한 최소 조건으로 수록 |
| `00.rag/user/error-message-and-code-reference.md` | U §10.1의 전체 오류 코드 뜻·상세 조치 표. 배포·설비 작업별 충돌·차단 코드의 상세 의미 |
| `00.rag/user/equipment-model-parameter-and-jar-update.md` | Check Out·Undo·Check In 절차. 본문에는 수정 적용 판본과 상태 불일치 인계만 수록 |
| `00.rag/user/equipment-registration-and-settings.md` | Gateway 목록의 「다시 시도」, 설비별 목록·상세 조회 실패의 실제 조작. 공통 오류의 단추로 일반화하지 않음 |

다른 기존 문서를 수정하지 않았다. 다른 문서의 확정 결함으로 보고할 항목은 이번 대조에서 확인하지 않았다.

## 검사 결과

검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/login-permission-and-screen-troubleshooting.md
```

마지막 원문 편집 뒤 검사 결과: exit 0, 대상 위반 0, CROSS VIOLATION 출력 0, MASK 출력 0. H2 10개·자식 30개·최대 자식 152자·최대 부모 350자. 검사기 자체는 변경하지 않았다. Java는 Eclipse Adoptium OpenJDK 21.0.9+10-LTS였다.

마지막 출력 10줄:

```text
    L128-128 문단 95  | 「화면을 표시하지 못했습니다.」는 화면을 그리는 중 오류나 화면 파일을 불러오는 실패를 잡았을 때 나오는 안내다. 조회·저장 창 안에 나오는 API 실패 문구와 구별한다.
    L130-130 문단 89  | 안내 영역의 「새로고침」은 현재 페이지 전체를 다시 읽는다. 새 버전 배포 뒤 화면 파일 불일치는 원인 후보지만, 이 안내만으로 배포 문제라고 확정하지 않는다.
    L132-132 문단 57  | 다시 발생하면 운영 담당자에게 열려던 화면·시각·직전 조작·새로고침 뒤 같은 안내가 남는지를 전달한다.
§ L134-148 [26] Undo 결과와 실패 표시가 서로 맞지 않을 때   parent=344 children=3 sources=6
    L144-144 문단 87  | 설비 파라미터의 「Undo」 뒤 실패 문구와 실제 상태가 맞지 않으면 같은 작업을 반복하기 전에 화면을 새로고침해 체크아웃 상태와 파라미터를 다시 확인한다.
    L146-146 문단 103  | 335473ea 수정 이후 판본에서는 Undo 응답이 success=true이면 data가 null이어도 성공으로 처리하고 관련 조회를 갱신한다. 확인한 기준 소스에도 이 처리가 있다.
    L148-148 문단 152  | 운영 담당자에게 설비 ID·시각·실패 문구·새로고침 뒤 상태를 전달하고 프런트엔드 배포 판본에 335473ea 수정이 포함됐는지 확인을 요청한다. 체크아웃 조작은 00.rag/user/equipment-model-parameter-and-jar-update.md를 따른다.
SUMMARY sections=10 children=30 maxChild=152 maxParent=350
CORPUS docs=25/53 sections=420 part={admin=7, user=18} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=1}
TOTAL files=25 targets=1 violations=0
```

사람 검사: 40절 미만이므로 10절 전체를 읽었다. 아래 네 판정은 각각 제목만으로 질문 파악·원천 일치·독립 답·중복 소유 방지를 뜻한다.

| 절 | 제목 | 원천 | 독립 답 | 중복 소유 검사 |
|---|---|---|---|---|
| 로그인 입력 문제와 계정 문제를 가르는 순서 | 적합 | 적합 | 적합 | 최소 입력 길이를 다시 쓰지 않고 자격 오류와 상태 거부를 구별 |
| 로그인 보안 검증 안내가 나올 때 확인할 요청 | 적합 | 적합 | 적합 | 세 보안 안내를 요청 단계로 구별하며 로그인 절차 복제 없음 |
| 업무 중 로그인 화면으로 돌아오면 세션 만료인가 | 적합 | 적합 | 적합 | 증상 단정 금지와 응답 수집을 소유 |
| 로그인은 성공했는데 현재 사용자 조회가 403일 때 | 적합 | 적합 | 적합 | 로그인 직후 별도 요청의 권한 경계를 구체화 |
| 메뉴가 없거나 접근 권한 안내가 나올 때 | 적합 | 적합 | 적합 | 인증 이동과 화면 거부를 가르는 최소 조건만 쓰고 전체 메뉴표 제외 |
| 화면은 열리지만 조회나 저장 요청이 403일 때 | 적합 | 적합 | 적합 | API 거부의 후보·증거 인계를 소유하고 권한 부여 조작 제외 |
| 서버 오류와 입력·대상 문제를 어디서 가르는가 | 적합 | 적합 | 적합 | 입력·서버 조사 경로만 쓰고 전체 코드 사전 제외 |
| 기본 실패 문구만 나오면 네트워크 문제인가 | 적합 | 적합 | 적합 | fallback·응답 유무 판별을 소유 |
| 화면 표시 실패와 API 실패 문구를 구별하기 | 적합 | 적합 | 적합 | 기존 기본 문서의 새로고침 절차와 필요한 최소 단추만 공유하고 원인 구별 추가 |
| Undo 결과와 실패 표시가 서로 맞지 않을 때 | 적합 | 적합 | 적합 | 체크아웃 조작 대신 판본·응답·상태 불일치 인계만 수록 |

- 제목의 정확 중복과 불필요한 반복 틀 0건. 본문은 직접 작성했고 식별자만 바꾼 틀 생성은 하지 않았다.
- 30개 본문 조각 전체를 위 표로 원천 대조했다. 코드 이름을 나누어 뜻을 만든 설명·빈 틀 문장·오답 인용·미완 문장 없음.
- 기본 문서와 관리 문서의 절차를 복제하지 않았다. 공유된 화면 문구와 최소 판별 조건은 증상을 구별하는 데 필요한 범위다. 동일 자식 본문은 검사기에서도 0건이었다.
- user 문서에 admin objectKey·관리 문서 열람 지시·서버/DB/클러스터 조작·비밀값 없음. 권한 캐시 점검은 사용자 조작이 아닌 운영 담당자 인계 사유로만 썼다.
- 원문 문구를 다시 읽으며 첫 초안의 전각 마침표와 메타의 혼입 한자를 최종 검사 전에 바로잡았다. mask-v1 적용 뒤 의미 손실 출력 없음.
- `UNKNOWN_ERROR`를 형식 불일치만으로 설명하거나 403을 미부여만으로 설명하지 않도록 실제 분기를 반영했다. 현재 수정된 Undo를 과거 오판 증상처럼 서술하지 않았다.
- 제품 빌드·제품 단위 시험·실행 화면·현장 재현·업로드·검색 반영은 수행하지 않았다. 기존 시험은 기대값을 읽은 근거이며 이번 실행 PASS가 아니다.

검사용 임시 파일은 만들지 않았다. 작성 기록과 검색 원문 두 파일만 생성했다. 별도 Agent·네트워크·Docker·kubectl·ssh 호출과 다른 프로세스 종료, git add·commit·push는 수행하지 않았다. 소유 일회성 파일이 없어 삭제 대상과 잔여 경로가 없다.

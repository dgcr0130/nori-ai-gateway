# 오류 메시지·코드별 의미와 조치
```rag-meta
유형: 참조
적용: nori-tc-ui-front 9fe69a75dcd3(PLC 목록 파일 2476db5c5093)·nori-tc-ui-backend 985d17c604dd(PLC 저장 파일 d36c10ff8579·순서 검사 파일 d36c10ff8579) 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 인증 거부 UNAUTHORIZED
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~115
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L130~139
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; commence L68~80
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 서버 오류 표시 L294~298
```

UNAUTHORIZED는 서버의 인증 거부 코드다. 로그인에서는 다음 문구를 구별한다.

| 문구 | 발생 조건·확인 |
|---|---|
| 아이디 또는 비밀번호가 올바르지 않습니다. | 계정 미존재 또는 비밀번호 불일치다. 입력한 아이디와 비밀번호를 확인한다. |
| 사용 불가능한 계정입니다. | 계정 상태가 ACTIVE가 아니다. 운영 관리자에게 계정 상태 확인을 요청한다. |
| 인증이 필요합니다. | 보호 요청에 인증이 성립하지 않았다. 로그인 상태를 확인한다. |

점검 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

## 현재 사용자 조회·인증 처리의 UNAUTHORIZED 문구
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; me L212~228
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; 검증 예외 L196~200
```

| 인증 오류 문구 | 발생 조건 |
|---|---|
| 인증 정보가 유효하지 않습니다. | 현재 사용자 조회에서 인증 정보가 없거나 사용자 정보의 형식이 맞지 않는다. |
| 인증 처리 중 오류가 발생했습니다. | 인증 검증 중 예상하지 못한 예외가 발생했다. |

두 문구는 UNAUTHORIZED와 401로 응답한다. 로그인 중 비밀번호 불일치 문구와 구별한다. 확인 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`다.

## 로그인 보안 요청이 403으로 거부된 코드
```rag-meta
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L840~841
```

로그인 요청의 403을 화면에서 다음 코드로 바꾼다. 문구는 첫 문장을 적는다.

| 코드 | 문구·발생 조건 |
|---|---|
| LOGIN_CSRF_FORBIDDEN | 「로그인 보안 토큰 발급이 차단되었습니다.」: 로그인 전 보안 토큰 발급 요청이 403이다. |
| LOGIN_FORBIDDEN | 「로그인 보안 검증에 실패했습니다.」: 로그인 제출 요청이 403이다. |

페이지를 새로고침한 뒤 재시도하고, 계속되면 발생 시각과 문구를 운영 담당자에게 전달한다. 확인 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`다.

## 로그아웃 보안 거부와 보안 토큰 누락
```rag-meta
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 로그아웃 오류 변환 L42~54 L79~90 L177~218
원천: nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; issueCsrfToken L55~73
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보안 안내 L839~843
원천: nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35
```

| 화면 생성 코드 | 발생 조건·문구 첫 문장 |
|---|---|
| LOGOUT_CSRF_FORBIDDEN | 로그아웃 전 발급 요청이 403이다. 「로그아웃 보안 토큰 발급이 차단되었습니다.」 |
| LOGOUT_FORBIDDEN | 로그아웃 제출 요청이 403이다. 「로그아웃 보안 검증에 실패했습니다.」 |
| CSRF_TOKEN_MISSING | 발급 요청 뒤 브라우저에서 토큰을 읽지 못했다. 「CSRF 토큰을 확인할 수 없습니다.」 |

페이지를 새로고침하고 다시 시도한다. 계속되면 작업·시각·문구만 전달하고 토큰 값은 전달하지 않는다. 조작은 `00.rag/user/login-and-screen-basics.md`를 따른다.

로그아웃 코드는 내부에서 생성하지만 현재 상단바의 로그아웃 실패는 화면에 표시하지 않는다.

## 권한 거부 FORBIDDEN과 화면 접근 안내
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiForbidden L190~198
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~185
원천: nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 ; 접근 안내 L15~34
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 접근 안내 L1104~1106
```

FORBIDDEN은 서버가 대상별 인가를 거부할 때 403과 함께 보내는 코드다. 「접근 권한이 없습니다」는 화면 접근 검사에서 만드는 별도 안내다.

서버 요청의 403은 권한 미보유뿐 아니라 권한 경로 미등록·권한 정보 초기화 실패·불일치에도 발생한다. 입력 오류로 단정하지 않고 화면·작업·표시된 권한 코드를 운영 관리자에게 전달한다.

코드별 권한은 `00.rag/user/permission-code-reference.md`, 진단은 `00.rag/user/login-permission-and-screen-troubleshooting.md`에서 확인한다.

## 입력·요청 형식 오류 INVALID_REQUEST
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·검증·변환 L89~97 L232~314 L404~424
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L5~12
```

INVALID_REQUEST는 입력 정책 위반·검증 실패·요청 해석 실패 등에 쓰인다. 함께 나온 문구가 가리키는 입력을 확인한다.

| 공통 문구 | 발생 조건 |
|---|---|
| 요청 본문 검증에 실패했습니다. | 본문 검증 실패에 개별 필드 오류 문구가 없을 때다. |
| 요청 파라미터 검증에 실패했습니다. | 파라미터 제약 위반에 개별 문구가 없을 때다. |
| 요청 본문 형식이 올바르지 않습니다. | 서버가 요청 본문을 해석하지 못했다. |
| 지원하지 않는 요청입니다. | 요청 방식·응답 형식·전송 형식·크기 제한 등의 표준 요청 오류다. |

화면에서 고칠 입력이 없으면 화면·조작·시각·응답 문구를 운영 담당자에게 전달한다.

## 공통 요청 오류의 형식·시간 초과 문구
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleTypeMismatch·표준 상태 변환 L280~289 L404~424
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 기본 한국어 문구 L7~12
```

| 공통 문구 | 코드·발생 조건 |
|---|---|
| 값의 형식이 올바르지 않습니다. | 앞에 요청 항목이 붙는다. 경로·조회 조건의 타입 변환 실패이며 INVALID_REQUEST로 응답한다. |
| 요청이 올바르지 않습니다. | 별도 문구를 정하지 않은 표준 4xx 요청 오류의 기본 안내다. 코드는 INVALID_REQUEST다. |
| 요청 처리 시간이 초과되었습니다. | 표준 예외의 503을 변환하는 문구다. 이 경로의 코드는 INTERNAL_ERROR다. |

HTTP 상태와 오류 문구를 함께 운영 담당자에게 전달한다. 업무별 시간 초과는 해당 업무의 오류 코드를 함께 확인한다.

## 대상 없음 NOT_FOUND와 현재 상태 충돌 CONFLICT
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 충돌·미존재 L107~115 L208~216 L404~424
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/UiConflictException.java @ 985d17c604dd ; 기본 코드 L26~42
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2520~2523
```

| 코드 | 의미·확인 |
|---|---|
| NOT_FOUND | 대상이나 요청 경로가 없을 때의 404다. 「요청한 리소스를 찾을 수 없습니다.」 또는 업무별 대상 부재 문구를 확인하고 목록을 다시 읽는다. |
| CONFLICT | 중복 등록·현재 상태와 맞지 않는 작업 등 기본 충돌 코드다. 409와 함께 나온 구체 문구를 기준으로 대상 상태를 확인한다. |

업무와 무관하게 원인을 하나로 확정하지 않는다. 공통 분류 경로는 `00.rag/user/login-permission-and-screen-troubleshooting.md`다.

## 설비 작업의 같은 요청 키·기준 상태 충돌
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; accept·issueCandidateOrAbort L64~82 L109~136
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationConflictException.java @ 985d17c604dd ; 충돌 정의 L16~23
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 충돌 안내 L1109~1109
```

| 코드 | 발생 조건·확인 |
|---|---|
| IDEMPOTENCY_CONFLICT | 같은 Idempotency-Key에 다른 요청 내용이 도착했다. 앞선 요청이 반영됐을 수 있으므로 현재 설비 상태를 먼저 확인한다. |
| REVISION_CONFLICT | 요청이 기준으로 삼은 상태와 현재 상태가 맞지 않는다. 최신 상태를 다시 조회한다. |

두 코드 모두 작업 접수의 409 충돌이다. 오류 문구·작업 ID·설비 ID를 보존한다. 결과 확인 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`다.

## 설비 작업 점유 RESOURCE_BUSY·EQUIPMENT_EXECUTING
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 점유 거부 L118~126
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleOperationConflict L129~137
```

| 코드 | 발생 조건 |
|---|---|
| RESOURCE_BUSY | 같은 설비를 다른 작업이 점유 중이다. 문구는 「같은 설비에 대해 다른 작업이 진행 중입니다.」로 시작한다. |
| EQUIPMENT_EXECUTING | 배포 실행이 설비를 점유 중이다. 문구는 「이 설비에 대한 배포가 진행 중입니다. 완료 후 다시 시도하세요.」로 시작한다. |

409로 거부된다. 기존 작업·배포의 진행 결과를 먼저 확인한다. 명령 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`, 배포 확인은 `00.rag/user/deployment-workflow.md`를 따른다.

## 미종결 작업 차단 RESOURCE_BLOCKED
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 차단 거부 L127~131
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2528~2528
```

RESOURCE_BLOCKED는 미종결 작업이 설비 처리를 차단하는 코드다. 「미종결 작업이 이 설비의 처리를 차단하고 있습니다.」 뒤에 차단한 작업 ID가 붙는다.

설비 ID와 거부 문구를 보존하고 「처리실패」의 「Repair Task」 확인 경로인 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다. 복구 처리는 운영 담당자에게 요청한다.

## 작업 조회 불가 COORDINATION_UNAVAILABLE·COORDINATION_DISABLED
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationUnavailableException.java @ 985d17c604dd ; 접근 불가 정의 L3~15
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCoordinationUnavailable L154~162
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 비활성 구성 L73~82
```

| 코드 | 의미 |
|---|---|
| COORDINATION_UNAVAILABLE | 작업 기록 저장소에 접근할 수 없어 결과를 확정할 수 없다. 503으로 응답한다. |
| COORDINATION_DISABLED | 「이 구성은 조정 원장을 사용하지 않습니다.」: 작업 조회에 필요한 기능이 구성에 없어 503으로 거부한다. |

작업 부재나 실패 확정으로 읽어 새 요청을 보내기 전에 운영 담당자에게 작업 ID·시각·문구를 전달한다. 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`를 따른다.

## 서버 정의 불일치 CATALOG_NOT_SYNCED
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCatalogNotSyncedException.java @ 985d17c604dd ; 정의 불일치 L5~17
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleCatalogNotSynced L172~180
```

CATALOG_NOT_SYNCED는 요청을 받은 서버의 정의 자료가 공유 기준과 달라 요청을 거부한 코드다. 응답은 503이다. 사용자 입력을 바꾸어 해결하는 오류가 아니다.

운영 담당자에게 실행한 작업·대상·시각·전체 문구를 전달해 서버 정의의 반영 상태를 확인하도록 요청한다. 화면·권한 오류의 구별은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

## 설비 파라미터 잠금 ALREADY_CHECKED_OUT
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 기존 EDIT 검사 L94~103
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; checkout L148~162
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/exception/EqpAlreadyCheckedOutException.java @ 985d17c604dd ; buildMessage L11~11 L50~54
```

ALREADY_CHECKED_OUT은 설비 파라미터 체크아웃 때 EDIT 자료가 이미 있어 반환하는 409 코드다. 문구는 「설비 파라미터가 이미 체크아웃 중입니다. 현재 편집 사용자:」 뒤에 편집자가 붙거나, 편집자를 모르면 다른 사용자가 체크아웃 중이라는 안내다.

현재 체크아웃 상태와 편집자를 확인한다. 파라미터 작업 경로는 `00.rag/user/equipment-model-parameter-and-jar-update.md`를 따른다.

## 설비 생성 준비 단계의 거부 코드
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 생성 준비·중단 L200~220 L620~647
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 거부 응답 대체 코드 L174~185
```

| 작업 결과 코드 | 발생 조건 |
|---|---|
| DB_STAGE_UNEXPECTED | 생성하려는 설비가 이미 다른 상태로 존재한다. |
| DB_STAGE_REJECTED | 생성 준비의 저장 처리가 거부됐다. 「요청 값이 올바르지 않아 설비를 생성할 수 없습니다.」로 안내한다. |
| PARTICIPANT_REJECTED | 적용 확정 전 참여 서버가 검증을 거부했으나 응답에 오류 코드가 없다. |

입력과 기존 설비 상태를 담당자와 대조한다. 결과 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`를 따른다.

## 작업 결과 코드 REPAIR_REQUIRED와 오류 상세
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 적용 실패·복구 필요 확정 L416~426 L653~677
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 복구 결과 문구 L277~285
```

REPAIR_REQUIRED는 적용 확정 뒤 실패를 복구 필요 결과로 기록할 때의 오류 코드다. 배포 적용 단계가 실패하면 세부 코드와 문구를 오류 상세에 함께 남긴다. STEP_TIMEOUT 같은 코드가 최상위 errorCode 대신 상세 문구 안에 있을 수 있다.

설비 명령 결과 창은 복구 사유가 있으면 사유를, 없으면 오류 문구를 표시한다. 상태값 전체는 `00.rag/user/state-reference.md`가 다룬다. 작업 ID·설비·사유를 보존하고 `00.rag/user/equipment-connection-and-command-troubleshooting.md`에서 확인한다.

## 설비 직접 실행의 발행·응답 실패
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; 발행·대기 실패 L57~81 L112~162
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 비동기 등록 실패 L373~378
```

설비 직접 실행 경로의 오류 코드다. TIMEOUT은 이 경로의 단계 응답 대기 실패를 뜻한다.

| 코드 | 발생 조건 |
|---|---|
| PUBLISH_FAILED | 단계 명령 발행 실패 또는 비동기 작업 등록 중 예외다. |
| TIMEOUT | 「step 응답 대기 시간이 초과되었습니다.」: 단계 응답을 기한 안에 받지 못했다. |
| STEP_INTERRUPTED | 단계 실행 대기가 인터럽트됐다. |
| STEP_CANCELED | 단계 실행 대기가 취소됐다. |
| STEP_EXECUTION_FAILED | 단계 응답 처리 중 예외가 발생했다. |

설비·작업 ID·문구를 담당자에게 전달한다. 확인은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`를 따른다.

## 설비 직접 실행 후 저장 확정 실패
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRunner.java @ 985d17c604dd ; finalizeLocalSuccess L165~183
```

LOCAL_FINALIZATION_FAILED는 설비 직접 실행의 단계 처리를 마친 뒤 서버 내부의 최종 저장 처리에서 예외가 발생한 코드다. 명령을 받은 대상의 상태와 서버에 저장된 결과를 함께 대조해야 한다.

운영 담당자에게 설비 ID·실행 명령·시각·오류 문구를 넘긴다. 설비 명령 결과를 확인하는 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`다.

## 모델 체크아웃 충돌 CHECKOUT_CONFLICT
```rag-meta
원천: nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 잠금 검사·오류 생성 L46~82 L131~161 L177~185
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 잠금 문구 L676~677
```

CHECKOUT_CONFLICT는 프런트엔드가 모델 EDIT 잠금을 확인해 만드는 코드다. 다른 편집자이거나 현재 사용자 정보를 확인하지 못해 잠금 소유를 인정할 수 없을 때 발생한다. 서버의 409 뒤 목록을 다시 확인해 같은 코드를 만들기도 한다.

「다른 사용자가 체크아웃한 모델이라 수정할 수 없습니다.」 또는 편집자 뒤에 「님이 체크아웃한 모델이라 수정할 수 없습니다.」가 붙는다.

모델 작업본·현재 사용자·편집자를 확인한다. 편집 경로는 `00.rag/user/model-details-and-flow-editing.md`를 따른다.

## 모델 저장 대상 오류 INVALID_MODEL_VERSION·INVALID_NODE
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; 저장·업로드 대상 검사 L64~78 L123~125 L139~147
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 상세 노드 조회 L74~91
```

| 코드 | 문구·발생 조건 |
|---|---|
| INVALID_MODEL_VERSION | 「EDIT version만 저장할 수 있습니다.」 또는 「EDIT version만 업로드할 수 있습니다.」: EDIT가 아닌 버전에 저장·업로드를 요청했다. |
| INVALID_NODE | 「MDF는 upload API를 사용해 주세요.」: 일반 상세 저장으로 MDF를 보냈다. 「지원하지 않는 상세 노드입니다.」: 상세 응답을 지원하지 않는 항목이다. |

모델과 버전·선택 항목을 확인한다. 정상 편집·MDF 업로드 경로는 `00.rag/user/model-details-and-flow-editing.md`를 따른다.

## PLC 순서 값 저장 거부 INVALID_REQUEST
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~20
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters L477~528
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; saveDetailRows L55~115
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiBadRequest L89~97
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; createPlcColumns L562~645
```

PLC 순서 값 검증 보완이 들어간 판본은 모델 상세 저장에서 「Byte Order」·「Word Order」를 앞뒤 공백 제거·대문자 변환 후 검사한다. 허용값 밖이면 400 INVALID_REQUEST로 거부한다.

오류는 「PLC Register」로 시작하며 모델 버전 키·행·그룹·변수와 byteOrder(8열) 또는 wordOrder(9열), 거부된 값을 포함한다.

문구 끝은 「'은 허용되지 않습니다. 허용값: BIG_ENDIAN, LITTLE_ENDIAN (빈값은 기본 BIG_ENDIAN).」다.

기존 행을 바꾸기 전에 거부한다. 오류가 가리킨 칸의 선택값을 고친다. 편집은 `00.rag/user/model-details-and-flow-editing.md`를 따른다.

## 배포 설비 중복 DEPLOY_ACTIVE_EQP_CONFLICT
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; rejectWhenActiveEqpConflict L761~775
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; findActiveEqpConflicts L243~269
```

DEPLOY_ACTIVE_EQP_CONFLICT는 같은 설비가 READY 또는 RUNNING인 배포에 포함돼 있다는 충돌 코드다. 실행 중인 배포뿐 아니라 실행을 기다리는 배포도 해당한다.

문구는 「이미 active Deploy에 포함된 설비입니다.」로 시작하며 설비 ID·기존 배포 번호·배포 상태가 붙는다. 기존 배포의 상태를 확인한다. 배포 조회 경로는 `00.rag/user/deployment-workflow.md`다.

## 배포 실행 정보·응답 조회 오류
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 계획·응답 조회 실패 L90~96 L157~172
```

배포 작업을 묶어 실행하는 경로에서 다음 코드를 만든다.

| 코드 | 발생 조건 |
|---|---|
| DEPLOY_PLAN_NOT_FOUND | 「실행할 Deploy target 정보를 찾을 수 없습니다.」: 실행 계획을 조회하지 못했다. |
| DEPLOY_TRACE_UNAVAILABLE | 「step 응답 저장소를 조회하지 못했습니다.」: 단계 응답 저장소 조회가 실패했다. |

운영 담당자에게 배포 번호·설비 ID·현재 단계·시각·문구를 전달한다. 응답 대기와 조회 실패를 가르는 경로는 `00.rag/user/deployment-troubleshooting.md`다.

## 배포 단계 실패 STEP_TIMEOUT·STEP_FAILED
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 단계 실패 판정 L175~215
```

| 코드 | 발생 조건 |
|---|---|
| STEP_TIMEOUT | 배포 작업을 묶어 실행하는 경로에서 응답 추적 등록이 사라지고 저장된 단계 기한에 도달했다. 문구는 「step 응답 대기 시간이 초과되었습니다.」다. |
| STEP_FAILED | 실패 응답이 도착했으나 응답의 오류 코드가 없을 때 쓴다. 오류 문구도 없으면 「step 응답이 실패로 도착했습니다.」를 쓴다. |

실패 응답에 코드가 있으면 STEP_FAILED 대신 그 코드를 사용한다. 배포·설비·단계·문구를 기록하고 `00.rag/user/deployment-troubleshooting.md`에서 확인한다.

## 처리실패 목록의 조회 오류 문구
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·범주별 조회 L14~58
원천: nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·재시도 L127~132
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L132~136 L1062~1065
```

| 기본 문구 | 대상 |
|---|---|
| Gateway DLQ 목록을 조회하지 못했습니다. | Gateway DLQ 조회 |
| Business DLQ 목록을 조회하지 못했습니다. | Business DLQ 조회 |
| 격리 설비 목록을 조회하지 못했습니다. | 격리 설비 조회 |
| Repair Task 목록을 조회하지 못했습니다. | Repair Task 조회 |

해석 가능한 실패 응답에 서버 문구가 있으면 그 문구를 사용한다. 오류 영역의 「재시도」는 같은 목록을 다시 조회한다. 실제 실패 원인과 권한 확인은 `00.rag/user/dlq-quarantine-troubleshooting.md`를 따른다.

## Repair Task에 남는 수동 복구·보상 발행 코드
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 수동 복구·발행 실패 L91~102 L119~143
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30
```

설비 직접 실행의 복구 과정이 Repair Task에 기록하는 코드다.

| 코드 | 발생 조건 |
|---|---|
| MANUAL_ROLLBACK_REQUIRED | 복구할 단계가 수동 복구 전용이거나 복구 동작이 없다. |
| COMPENSATION_PUBLISH_FAILED | 이미 끝난 단계를 되돌리는 보상 명령을 발행하지 못했다. |

코드와 함께 「에러 메시지」를 보존해 운영 담당자에게 전달한다. 사용자 확인 경로는 `00.rag/user/dlq-quarantine-and-repair-task.md`다.

## Repair Task의 보상 대기·응답 처리 실패
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 응답 실패 L146~180
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 에러 코드·문구 표시 L14~30
```

설비 직접 실행의 보상 과정에서 다음 코드를 Repair Task에 남긴다.

| 코드 | 발생 조건 |
|---|---|
| COMPENSATION_TIMEOUT | 보상 단계 응답 대기 시간이 초과됐다. |
| COMPENSATION_INTERRUPTED | 보상 응답 대기가 인터럽트됐다. |
| COMPENSATION_CANCELED | 보상 단계 대기가 취소됐다. |
| COMPENSATION_EXECUTION_FAILED | 보상 단계 응답 처리 중 예외가 발생했다. |

설비 ID·Task ID·생성 시각·문구를 운영 담당자에게 넘긴다. 기록 확인은 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다.

## 데모 점유 충돌·실행 부재·호출 오류
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 호출·상태 변환 L141~166
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorRunNotFoundException.java @ 985d17c604dd ; 미존재 예외 L5~12
```

| 데모 오류 문구의 시작 | 코드·발생 조건 |
|---|---|
| 설비가 이미 다른 Run 에 점유되어 있습니다. | CONFLICT: 시뮬레이터 호출이 409로 거부됐다. |
| simulator orchestrator Run 미존재. | NOT_FOUND: 시뮬레이터가 해당 실행 요청에 404로 응답했다. |
| simulator orchestrator 호출 중 오류가 발생했습니다: | INVALID_REQUEST: 서버 간 호출의 입출력 실패나 인터럽트다. |
| simulator orchestrator 호출에 실패했습니다. | INVALID_REQUEST: 404·409 외의 4xx 응답이다. |

실행 번호·설비·시각·문구를 보존한다. 확인 경로는 `00.rag/user/demo-troubleshooting.md`다.

## 데모 시작·조회·종료의 기본 실패 문구
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 오류 문구 지정 L47~141 L187~204
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·종료 실패 표시 L318~370
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 기본 오류 L967~974
```

| 문구 | 실패한 요청 |
|---|---|
| Demo 테스트 시작에 실패했습니다. | 데모 실행 생성 |
| Demo 시나리오 목록을 조회하지 못했습니다. | 시나리오 목록 조회 |
| Demo 테스트 정보를 처리하지 못했습니다. | 실행 목록 또는 실행 상세 조회 |
| Run 종료 요청에 실패했습니다. | 실행 전체 또는 설비별 종료 |

API에서 사용할 서버 문구를 얻지 못했을 때의 대체 문구다. 시작 실패는 팝업, 종료 실패는 데모 창의 안내 영역에 표시한다. 실행 유무·실제 종료 결과는 `00.rag/user/demo-troubleshooting.md`에서 확인한다.

## 공통 통신 UNKNOWN_ERROR와 INVALID_RESPONSE
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse·normalizeApiError L19~40 L67~79
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 목록 계약 검사 L55~84
원천: nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; normalizeModelListPayload L59~85
```

| 화면 생성 코드 | 의미 |
|---|---|
| UNKNOWN_ERROR | 해석 가능한 실패 응답에 코드가 없거나, 실패 형식으로 해석하지 못했다. 응답이 없는 통신 예외도 이 코드로 정규화할 수 있다. |
| INVALID_RESPONSE | 목록 등에서 성공 데이터의 구조가 화면이 요구한 형태가 아니다. 예를 들어 인스턴스 목록이 배열이 아닐 때 발생한다. |

응답 유무·상태·표시 문구를 함께 확인한다. 공통 통신 점검은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

## 서버 내부 오류 INTERNAL_ERROR
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 미분류·표준 오류 L324~332 L404~424
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/resources/messages.properties @ 985d17c604dd ; 내부 오류 문구 L13~13
```

INTERNAL_ERROR는 공통 예외 처리기가 분류하지 못한 서버 예외에 500과 함께 보내는 코드다. 표준 서버 오류를 같은 코드로 변환하는 경우도 있다. 기본 한국어 문구는 「요청 처리 중 내부 오류가 발생했습니다.」다.

화면·직전 작업·대상·시각·전체 표시 문구를 운영 담당자에게 전달한다. 분류 기준은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

## 작업 조회의 요청한 작업을 찾을 수 없습니다
```rag-meta
원천: nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L19~50
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 조회 문구 L1107~1108
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업 조회 L85~107
```

「요청한 작업을 찾을 수 없습니다.」는 작업 상태 조회가 404일 때 화면이 NOT_FOUND 코드와 함께 만드는 문구다. 서버가 해당 작업을 찾지 못한 결과와 연결한다.

「작업 상태를 조회하지 못했습니다.」는 조회 실패를 구체 문구로 해석하지 못했을 때의 대체 안내다. 두 안내 모두 명령 대상 설비의 연결 실패를 직접 뜻하지 않는다.

새 명령을 보내기 전에 작업 ID와 설비 ID를 보존한다. 결과 확인 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`다.

## 로그인·로그아웃의 일반 실패 문구
```rag-meta
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; 기본 문구·실패 정규화 L15~19 L97~113 L177~218
원천: nori-tc-ui-front/src/features/auth/types/auth.types.ts @ 9fe69a75dcd3 ; defaultAuthErrorMessage L38~43
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 일반 실패 문구 L837~838
원천: nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 로그아웃 실패 소비 L27~35
```

| 대체 문구 | 의미 |
|---|---|
| 로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. | 로그인 실패에서 사용할 구체 문구를 얻지 못했다. |
| 로그아웃 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. | 로그아웃 실패에서 사용할 구체 문구를 얻지 못했다. |

문구만으로 비밀번호 불일치나 네트워크 장애를 확정하지 않는다. 요청별 확인은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

로그아웃 대체 문구는 API 내부에 있으나 현재 상단바에서 실패 안내로 표시하지 않는다.

## 화면 표시 실패 안내
```rag-meta
원천: nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 렌더 오류 복구 L14~55
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 복구 문구 L477~479
```

「화면을 표시하지 못했습니다.」는 화면 렌더링이나 화면 파일 로드가 실패했을 때 오류 경계가 표시하는 안내다. 이 안내 자체에는 서버 errorCode가 없다.

같은 영역의 「새로고침」은 페이지 전체를 다시 읽는다. 재발하면 화면·시각·직전 조작을 운영 담당자에게 전달한다. 상세 구별은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

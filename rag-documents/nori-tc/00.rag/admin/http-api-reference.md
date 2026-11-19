# HTTP API
```rag-meta
유형: 참조
적용: nori-tc-ui-backend 985d17c604dd 및 PLC 순서 저장 보완(JpaModelDetailCommandPort.java d36c10ff8579·PlcRegisterOrder.java d36c10ff8579)·nori-tc-simulator 5e9b02e3ae16 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## HTTP-UI와 HTTP-SIM의 호출 경계
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~122
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L46~139
```

HTTP-UI는 브라우저가 ui-backend에 보내는 `/api` 요청이다. HTTP-SIM은 ui-backend가 Simulator orchestrator를 부르거나 Simulator 내부 서버끼리 주고받는 요청이다.

데모의 브라우저 경로는 `/api/live-map/demo-runs`, orchestrator 경로는 `/api/demo-runs`다. 아래 경로는 서버 기준 상대 경로다. 표의 입력은 요청 본문 타입이며, 조회 조건·헤더·파일은 별도로 표시한다.

## HTTP-UI 성공·실패 본문
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/ApiResponse.java @ 985d17c604dd ; ApiResponse L16~48
```

HTTP-UI의 `ApiResponse`는 다음 네 필드를 갖는다. 아래 HTTP-UI 표의 결과 타입은 `data` 안에 들어간다.

| 필드 | 성공 응답 | 실패 응답 |
|---|---|---|
| success | true | false |
| data | 결과 또는 null | null |
| errorCode | null | 오류 코드 |
| errorMsg | null | 오류 문구 |

## HTTP-UI 오류 처리와 본문 없는 예외
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L89~216
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; UiApiExceptionHandler L231~332
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; UiAuthenticationEntryPoint L68~81
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; AsyncResultController L85~107
```

입력·본문 검증 실패는 400, 인증 진입점의 거부는 401, 업무 권한 거부 예외는 403, 대상 부재 예외는 404, 충돌 예외는 409, 조정 저장소·카탈로그 예외는 503, 미분류 예외는 500으로 공통 본문을 반환한다.

`GET /api/async/{id}`에서 작업이 없으면 본문 없는 404다. 오류 코드별 뜻과 사용자 조치는 `00.rag/user/error-message-and-code-reference.md`가 소유한다.

## 인증 쿠키와 CSRF 요청 계약
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; UiTokenAuthenticationFilter L169~208
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L106~128
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L191~218
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; UiAuthProperties L96~121
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L255~276
```

HTTP-UI는 설정된 인증 쿠키에서 인증값을 읽는다. 기본 쿠키 식별자는 `TC_UI_AUTH`다. CSRF 발급 경로는 `GET /api/auth/csrf`이며 성공하면 data는 null이다.

CSRF는 활성화되어 있다. 쓰기 요청은 기본 `XSRF-TOKEN` 쿠키의 원문 값을 `X-XSRF-TOKEN` 헤더로 보낸다. 쿠키·헤더 식별자는 설정으로 바뀔 수 있다. 로그인 POST도 CSRF 검사 예외로 등록하지 않는다.

## 공개 경로와 보호 API의 권한 검사
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L131~169
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; UiApiPermissionCache L142~185
```

공개 경로에는 `POST /api/auth/login`, `GET /api/auth/csrf`, GET의 `/api/actuator/health`와 하위 경로가 있다. OPTIONS 요청도 허용한다.

일반 보호 API는 인증 뒤 활성 API 권한 목록에서 URI·HTTP 메서드를 대조한다. 매칭 권한 중 하나를 보유해야 통과한다. 경로 미등록·권한 초기화 실패·권한 카탈로그 불일치도 차단한다. 경로별 권한 코드 표는 `00.rag/user/permission-code-reference.md`를 따른다.

## 쿠키를 보내는 교차 출처 요청
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; UiSecurityConfig L246~260
```

HTTP-UI의 CORS는 설정된 허용 Origin 목록을 사용하며 `allowCredentials=true`다. 허용 메서드는 GET·POST·PUT·PATCH·DELETE·OPTIONS다. 요청 헤더는 전체 허용한다. 허용 Origin 목록이 비면 교차 출처 허용 목록도 비게 된다.

## 로그인·로그아웃과 현재 사용자 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L96~138
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L152~240
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LoginRequest.java @ 985d17c604dd ; LoginRequest L11~18
```

아래 성공 응답은 HTTP 200이다. 로그인 입력은 빈 값이 아닌 userId·password를 받는다.

| 호출 | 입력 | 결과 |
|---|---|---|
| POST /api/auth/login | LoginRequest | LoginResponse와 인증 쿠키 |
| POST /api/auth/logout | 본문 없음 | null과 쿠키 만료 응답 |
| GET /api/auth/me | 본문 없음 | MeResponse |

화면 사용 절차는 `00.rag/user/login-and-screen-basics.md`를 따른다.

## 인증 API가 직접 반환하는 401
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; AuthController L133~176 L214~228 L288~292
```

아래 분기는 컨트롤러가 401 UNAUTHORIZED를 반환한다.

| 호출 | 직접 반환 조건 |
|---|---|
| POST /api/auth/login | UiAuthenticationException으로 인증이 실패했다. |
| POST /api/auth/logout | 인증 정보가 없거나 credentials가 비정상·공백이거나 principal 타입이 다르다. 삭제 쿠키도 함께 보낸다. |
| GET /api/auth/me | 인증 정보가 없거나 principal 타입이 다르다. |

## HTTP-UI 목록의 offset·limit 계약
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiPageRequestSupport.java @ 985d17c604dd ; UiPageRequestSupport L27~73
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/model/PagedResponse.java @ 985d17c604dd ; PagedResponse L28~52
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~112
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L46~53
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L73~91 L223~244
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L71~89 L182~203 L254~275
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; PermissionController L63~81
```

`PagedResponse`를 쓰는 설비·모델 목록의 조회 조건은 offset·limit다. 같은 공통 해석기를 쓰는 사용자·그룹·권한·소속 목록에도 적용한다.

| 항목 | 값·의미 |
|---|---|
| offset | 기본 0, 0 이상인 시작 위치 |
| limit | 기본 100, 1~500인 조회 건수 |
| PagedResponse | items·offset·limit·count를 반환하며 count는 전체 건수다. |

## 설비 목록·기본·관리 상세 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L98~150
```

HTTP-UI 설비 조회의 성공은 200이다.

| 호출 | 조회 조건 | 결과 |
|---|---|---|
| GET /api/eqp | 선택 offset·limit | PagedResponse의 EqpInfoResponse 목록 |
| GET /api/eqp/{eqpId} | 경로 eqpId | EqpInfoResponse |
| GET /api/eqp/{eqpId}/manage | 경로 eqpId | EqpManageDetailResponse |

`GET /api/eqp/{eqpId}`·`GET /api/eqp/{eqpId}/manage`는 설비가 없으면 컨트롤러가 404 NOT_FOUND를 반환한다.

## 여러 설비의 관리 상세와 선택지 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L165~206
```

| 호출 | 조회 조건 | 200 결과 |
|---|---|---|
| GET /api/eqp/manage-batch | 필수 eqpIds | EqpManageDetailBatchResponse |
| GET /api/eqp/options | 없음 | EqpManageOptionsResponse |

manage-batch의 eqpIds는 쉼표 구분 문자열이다. 앞뒤 공백·빈 항목·중복을 제거한 뒤 1~200개를 허용한다.

## 설비 파라미터 버전과 런타임 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L215~250
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/eqp/{eqpId}/param-versions | 경로 eqpId | 문자열 목록 |
| GET /api/eqp/{eqpId}/runtime-state | 경로 eqpId | EqpRuntimeStateResponse |

두 조회 모두 대상 설비가 없으면 404를 반환한다.

## 설비 등록·수정·삭제의 접수 계약
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L260~312
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L457~490
```

| 호출 | 본문 | 202 결과 |
|---|---|---|
| POST /api/eqp | EqpCreateRequest | OperationAcceptResponse |
| PUT /api/eqp/{eqpId} | EqpUpdateRequest | OperationAcceptResponse |
| DELETE /api/eqp/{eqpId} | 없음 | OperationAcceptResponse |

세 요청은 선택 헤더 `Idempotency-Key`를 받으며 없거나 공백이면 서버가 새 값을 만든다. 접수 결과에는 operationId·state·phase·errorCode·errorMsg가 들어간다.

`PUT /api/eqp/{eqpId}`는 설비가 없으면 202 접수 전에 404 NOT_FOUND를 반환한다.

## 설비 통신 명령의 요청과 응답
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L322~392
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L493~499
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpLifecycleRequest.java @ 985d17c604dd ; EqpLifecycleRequest L18~24
```

| 호출 | 본문 | 접수 결과 |
|---|---|---|
| POST /api/eqp/{eqpId}/start | EqpLifecycleRequest | 202 AsyncAcceptResponse |
| POST /api/eqp/{eqpId}/end | EqpLifecycleRequest | 202 AsyncAcceptResponse |
| POST /api/eqp/{eqpId}/reconnect | EqpLifecycleRequest | 202 AsyncAcceptResponse |

본문은 빈 값이 아닌 interfaceType과 선택 uiMessage를 받는다. 접수 거부는 서비스가 정한 HTTP 상태와 실패 본문을 반환한다.

## PLC 폴링 그룹의 제어 경로
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L405~444
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; EqpController L493~499
```

| 호출 | 입력 | 접수 결과 |
|---|---|---|
| POST /api/eqp/{eqpId}/polling-group/{groupMessageName}/start | 경로 eqpId·groupMessageName | 202 AsyncAcceptResponse |
| POST /api/eqp/{eqpId}/polling-group/{groupMessageName}/end | 경로 eqpId·groupMessageName | 202 AsyncAcceptResponse |

두 요청에는 별도 요청 본문이 없다. 화면 조작은 `00.rag/user/equipment-commands-and-polling.md`가 다룬다.

## 비동기 조회와 조정 관리 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; AsyncResultController L69~107
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; CoordinationAdminController L76~94
```

| 호출 | 입력 | 결과 |
|---|---|---|
| GET /api/async/{id} | 경로 id | OperationStatusResponse |
| GET /api/coordination/operations/{operationId} | 경로 operationId | 200 OperationAdminResponse |

비동기 조회는 진행 중 202, 종결 시 200을 쓴다. 두 조회 모두 조정 조회 서비스가 없는 구성에서는 503 COORDINATION_DISABLED를 반환한다. 운영상 결과 해석은 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다.

조정 관리 조회에서 작업이 없으면 컨트롤러가 404 OPERATION_NOT_FOUND를 반환한다.

## 설비 파라미터의 조회·체크아웃
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; EqpParamController L83~162
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCheckoutRequest.java @ 985d17c604dd ; EqpCheckoutRequest L9~11
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/eqp/{eqpId}/checkout-status | 경로 eqpId | EqpCheckoutStatusResponse |
| GET /api/eqp/{eqpId}/params | 필수 조회 조건 version | EqpParamResponse 목록 |
| POST /api/eqp/{eqpId}/checkout | EqpCheckoutRequest의 sourceVersion | EqpParamResponse 목록 |

checkout-status 조회에서 설비가 없으면 404 NOT_FOUND를 반환한다. checkout 요청의 EqpAlreadyCheckedOutException은 컨트롤러가 409 ALREADY_CHECKED_OUT으로 반환한다.

## 설비 파라미터 저장·체크인·취소
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; EqpParamController L177~241
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpParamSaveRequest.java @ 985d17c604dd ; EqpParamSaveRequest L14~36
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCheckinRequest.java @ 985d17c604dd ; EqpCheckinRequest L8~11
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| PUT /api/eqp/{eqpId}/edit-params | EqpParamSaveRequest의 params | null |
| POST /api/eqp/{eqpId}/checkin | EqpCheckinRequest의 description | null |
| DELETE /api/eqp/{eqpId}/checkout | 본문 없음 | null |

params는 null을 허용하지 않으며 항목은 paramName·paramValue·description을 갖는다. 편집 절차는 `00.rag/user/equipment-model-parameter-and-jar-update.md`를 따른다.

## Gateway·Business 인스턴스 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd ; RuntimeInstanceController L27~73
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/eqp/gateway-instances | 없음 | RuntimeInstanceResponse 목록 |
| GET /api/eqp/business-instances | 없음 | RuntimeInstanceResponse 목록 |

응답 타입에는 sessionId·podName·podIp·state·protocolVersion·registeredAtEpochMs·updatedAtEpochMs가 있다.

## Gateway 소유권과 Business 배정 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd ; GatewayShardLeaseController L22~47
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/BusinessAppAssignmentController.java @ 985d17c604dd ; BusinessAppAssignmentController L23~48
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/eqp/gateway-shard-leases | 없음 | GatewayShardLeaseResponse 목록 |
| GET /api/eqp/business-app-assignments | 없음 | BusinessAppAssignmentResponse 목록 |

운영 조회·인계는 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다.

## 배포 이력과 등록 후보 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L102~161
```

| 호출 | 선택 조회 조건 | 200 결과 |
|---|---|---|
| GET /api/deploys | modelName·requestedBy·fromDate·toDate·terminalOnly·offset·limit | DeployHistoryPageResponse |
| GET /api/deploys/register/targets | modelName·modelVersionKey·eqpId·lineKey·offset·limit | DeployRegisterTargetPageResponse |

날짜 종료 조건이 시작 조건보다 빠르면 400으로 거부한다.

## 배포 생성과 등록 시나리오 미리보기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L171~226
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/DeployCreateRequest.java @ 985d17c604dd ; DeployCreateRequest L17~59
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/DeployRegisterScenarioPreviewRequest.java @ 985d17c604dd ; DeployRegisterScenarioPreviewRequest L14~46
```

| 호출 | 본문·조건 | 결과 |
|---|---|---|
| POST /api/deploys | DeployCreateRequest | 202 DeployCreateResponse |
| POST /api/deploys/register/scenario-preview | DeployRegisterScenarioPreviewRequest | 200 DeployRegisterScenarioPreviewResponse |
| GET /api/deploys/scenarios | 선택 조회 조건 eventType | 200 DeployScenarioCatalogResponse |

생성 본문은 deployName·eventType·commonRequest·한 건 이상 targets를 요구한다. 미리보기도 commonRequest와 한 건 이상 targets를 요구한다.

## 배포 실행·보류·취소 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L236~274
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| POST /api/deploys/{deployKey}/start | 경로 deployKey | DeployActionResponse |
| POST /api/deploys/{deployKey}/hold | 경로 deployKey | DeployActionResponse |
| POST /api/deploys/{deployKey}/cancel | 경로 deployKey | DeployActionResponse |

실행 조건과 사용 절차는 `00.rag/user/deployment-workflow.md`를 따른다.

## 배포 재접수·삭제·요약 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L284~320
```

| 호출 | 입력 | 결과 |
|---|---|---|
| POST /api/deploys/{deployKey}/redeploy | 경로 deployKey | 202 DeployCreateResponse |
| DELETE /api/deploys/{deployKey} | 경로 deployKey | 200 DeployActionResponse |
| GET /api/deploys/{deployKey} | 경로 deployKey | 200 DeploySummaryResponse |

## 배포 대상별 진행 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; DeployController L333~348
```

`GET /api/deploys/{deployKey}/targets`는 200의 DeployTargetPageResponse를 반환한다. 경로 deployKey 외에 page·size·status·eqpId 조회 조건을 선택으로 받는다. 생략한 page는 0, size는 50으로 서비스에 전달한다.

## 배포 시나리오 관리 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; DeployScenarioAdminController L52~117
```

아래 호출의 성공 응답은 200이다. validate·reload는 본문을 생략하거나 reason을 담은 DeployScenarioAdminActionRequest를 보낸다.

| 호출 | 결과 |
|---|---|
| POST /api/admin/deploy-scenarios/validate | DeployScenarioValidationResponse |
| POST /api/admin/deploy-scenarios/reload | DeployScenarioReloadResponse |
| GET /api/admin/deploy-scenarios/catalog | DeployScenarioCatalogResponse |

validate·reload 결과의 status·validationErrors도 함께 읽는다.

## DLQ와 격리·복구 작업 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; DlqController L81~89
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; DlqController L129~209
```

| 호출 | 200 결과 |
|---|---|
| GET /api/dlq/gateway | Gateway DLQ 목록 |
| GET /api/dlq/business | Business DLQ 목록 |
| GET /api/dlq/quarantine | Gateway 격리 목록 |
| GET /api/dlq/repair-task | EqpRepairTaskResponse 목록 |

화면 조회는 `00.rag/user/dlq-quarantine-and-repair-task.md`가 다룬다.

## DLQ 항목 삭제 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; DlqController L100~162
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| DELETE /api/dlq/gateway/{dlqId} | 경로 dlqId | 삭제 여부 Boolean |
| DELETE /api/dlq/business/{dlqId} | 경로 dlqId | 삭제 여부 Boolean |

## 통신 로그 조회 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; CommLogController L35~45
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/comm-logs/{eqpId} | 선택 조회 조건 type | CommLogEntry 목록 |

통신 로그 type은 기본 all이다. eqp는 설비 로그, mes는 MES 로그를 고르고 나머지 값은 전체 조회로 처리한다.

## 라인 목록과 Live-Map 데이터
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; LineController L24~43
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L33~51
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/line | 없음 | LineResponse 목록 |
| GET /api/live-map | 없음 | LiveMapResponse |

화면에서 읽는 방법은 `00.rag/user/live-map-status.md`를 따른다.

## Live-Map 배치와 구조물 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; LiveMapLayoutController L34~74
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapLayoutPutRequest.java @ 985d17c604dd ; LiveMapLayoutPutRequest L13~22
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/live-map/layout | 필수 조회 조건 lineKey | LiveMapLayoutResponse |
| PUT /api/live-map/layout | lineKey와 LiveMapLayoutPutRequest | LiveMapLayoutResponse |
| GET /api/live-map/layout/objects | 없음 | LiveMapObjectResponse 목록 |

배치 저장 본문에는 canvasJson·placements가 있다.

## Live-Map 구조물 저장·삭제
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; LiveMapLayoutController L76~102
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapObjectSaveRequest.java @ 985d17c604dd ; LiveMapObjectSaveRequest L11~15
```

| 호출 | 본문 | 200 결과 |
|---|---|---|
| POST /api/live-map/layout/objects | LiveMapObjectSaveRequest | LiveMapObjectResponse |
| PUT /api/live-map/layout/objects/{objectKey} | LiveMapObjectSaveRequest | LiveMapObjectResponse |
| DELETE /api/live-map/layout/objects/{objectKey} | 없음 | null |

저장 본문은 objectName·objectKind·shapeJson을 갖는다. 화면 편집은 `00.rag/user/live-map-layout-editing.md`를 따른다.

## 브라우저의 데모 생성·목록·상세
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L56~78
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapSimulatorRunCreateRequest.java @ 985d17c604dd ; LiveMapSimulatorRunCreateRequest L12~19
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| POST /api/live-map/demo-runs | LiveMapSimulatorRunCreateRequest | SimulatorRunResponse |
| GET /api/live-map/demo-runs | 없음 | SimulatorRunResponse 목록 |
| GET /api/live-map/demo-runs/{runId} | 경로 runId | SimulatorRunResponse |

생성 본문은 testType·scenarioId·eqpIds를 받는다. orchestrator 생성 본문의 mode·targets와 구별한다.

## 브라우저의 데모 종료 요청
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L81~93
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| POST /api/live-map/demo-runs/{runId}/stop | 경로 runId | SimulatorRunResponse |
| POST /api/live-map/demo-runs/{runId}/targets/{eqpId}/stop | 경로 runId·eqpId | SimulatorRunResponse |

데모 사용은 `00.rag/user/demo-execution.md`, 실패 확인은 `00.rag/user/demo-troubleshooting.md`를 따른다.

## 브라우저의 시나리오 파일 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; LiveMapController L96~132
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/live-map/demo-runs/scenarios | 선택 조회 조건 mode | SimulatorScenarioCatalogItem 목록 |
| POST /api/live-map/demo-runs/scenarios | multipart/form-data의 file | SimulatorScenarioCatalogItem |
| DELETE /api/live-map/demo-runs/scenarios/{scenarioId} | 경로 scenarioId | null |

업로드 파일은 비어 있으면 거부하며 UTF-8 문자열로 읽어 orchestrator에 보낸다.

## 모델 목록·버전 조회와 기본 저장
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L46~68
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; ModelMutationController L49~98
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/model | 선택 offset·limit | PagedResponse의 ModelInfoResponse 목록 |
| GET /api/model/{modelVersionKey} | 경로 modelVersionKey | ModelInfoResponse |
| POST /api/model | ModelUpsertRequest | ModelInfoResponse |
| PUT /api/model/{modelVersionKey} | ModelUpsertRequest | ModelInfoResponse |

단건 GET·PUT은 모델이 없으면 컨트롤러가 404 NOT_FOUND를 반환한다.

## 루트 모델과 branch 생성·정보 변경
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L56~99
```

| 호출 | 본문 | 200 결과 |
|---|---|---|
| POST /api/model/roots | ModelRootCreateRequest | ModelInfoResponse |
| PUT /api/model/{modelKey}/info | ModelInfoUpdateRequest | ModelInfoResponse |
| POST /api/model/{modelKey}/branches | ModelBranchCreateRequest | ModelInfoResponse |

등록 절차는 `00.rag/user/model-registration-and-branching.md`가 소유한다.

## 모델 버전 체크아웃·체크인과 부모 반영
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L102~143
```

| 호출 | 본문 | 200 결과 |
|---|---|---|
| POST /api/model/{modelVersionKey}/checkout | 없음 | ModelInfoResponse |
| POST /api/model/{modelVersionKey}/checkin | ModelCheckinRequest | ModelInfoResponse |
| POST /api/model/{modelKey}/commit-parent | 선택 ModelParentCommitRequest | ModelParentCommitResponse |

부모 반영 본문이 없으면 applyCommit을 false로 처리한다.

## 모델 삭제 범위와 deprecated branch 삭제
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; ModelMutationController L101~116
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; ModelBranchController L146~157
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| DELETE /api/model/{modelIdentifier} | 선택 조회 조건 scope | null |
| DELETE /api/model/{modelKey}/branches/deprecated | 경로 modelKey | ModelDeleteBatchResponse |

scope의 기본은 version이다. version은 모델 버전 키로, model은 모델 키로 삭제한다. 이외 scope는 거부한다.

## 모델 상세 데이터와 MDF 업로드
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L70~93
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; ModelDetailController L55~160
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/model/{modelVersionKey}/details/{detailNode} | 경로 두 값 | ModelDetailDataResponse |
| PUT /api/model/{modelVersionKey}/details/{detailNode} | JSON ModelDetailSaveRequest | ModelDetailDataResponse |
| PUT /api/model/{modelVersionKey}/details/mdf | multipart/form-data의 file·선택 mdfName | ModelMdfContentResponse |

상세 저장·MDF 업로드는 EDIT 버전만 받으며, 아니면 400 INVALID_MODEL_VERSION을 반환한다.

## 모델 상세 API의 대상·노드 오류
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; ModelQueryController L70~90
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; ModelDetailController L55~125 L130~147
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; saveDetailRows·replacePlcRegisters L150~179 L211~528 L542~735
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~22
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiBadRequest L89~98
```

모델 상세 API는 다음 오류를 반환한다.

- `GET /api/model/{modelVersionKey}/details/{detailNode}`: 모델 부재는 404 NOT_FOUND, 지원하지 않는 노드는 400 INVALID_NODE다.
- `PUT /api/model/{modelVersionKey}/details/{detailNode}`: 모델 부재는 404 NOT_FOUND, mdf·미지원 노드는 400 INVALID_NODE다. workflow 검증·노드별 행 입력 검사(필수 칸·형식·PLC 그룹·순서 값 등) 실패는 400 INVALID_REQUEST이며 응답 메시지에 사유를 담는다.
- `PUT /api/model/{modelVersionKey}/details/mdf`: 모델이 없으면 404 NOT_FOUND다.

## Flow 생성과 목록 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; ModelFlowController L44~89
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelFlowCreateRequest.java @ 985d17c604dd ; ModelFlowCreateRequest L16~19
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| POST /api/model/{modelVersionKey}/flows | ModelFlowCreateRequest | ModelFlowCreatedResponse |
| GET /api/model/{modelVersionKey}/flows | 경로 modelVersionKey | ModelFlowListResponse |

생성 본문에는 workflowName·messageName이 있다. 생성 결과는 flowId를 담는다.

## Flow 한 건의 조회·저장·삭제
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; ModelFlowController L99~154
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/model/{modelVersionKey}/flows/{flowId} | 경로 두 값 | ModelFlowDetailResponse |
| PUT /api/model/{modelVersionKey}/flows/{flowId} | ModelFlowUpdateRequest | ModelFlowDetailResponse |
| DELETE /api/model/{modelVersionKey}/flows/{flowId} | 경로 두 값 | null |

상세·Flow 편집 절차는 `00.rag/user/model-details-and-flow-editing.md`를 따른다.

## 사용자 조회와 저장 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L73~156
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/user | 선택 offset·limit | PagedResponse의 UserListItemResponse 목록 |
| GET /api/user/{userPk} | 경로 userPk | UserInfoResponse |
| POST /api/user | UserUpsertRequest | UserInfoResponse |
| PUT /api/user/{userPk} | UserUpsertRequest | UserInfoResponse |

단건 GET·PUT은 사용자가 없으면 컨트롤러가 404 NOT_FOUND를 반환한다.

## 사용자 삭제·비밀번호 초기화·그룹 소속 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L165~172
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L223~267
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserPasswordResetRequest.java @ 985d17c604dd ; UserPasswordResetRequest L16~22
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| DELETE /api/user/{userPk} | 경로 userPk | null |
| POST /api/user/{userPk}/password/reset | UserPasswordResetRequest | null |
| GET /api/user/{userPk}/group | 선택 offset·limit | PagedResponse의 UserGroupMembershipResponse 목록 |

초기화 본문에는 빈 값이 아닌 newPassword와 선택 updatedBy가 있다.

`GET /api/user/{userPk}/group`·`POST /api/user/{userPk}/password/reset`은 사용자가 없으면 컨트롤러가 404 NOT_FOUND를 반환한다.

## 사용자와 그룹의 연결 변경
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; UserController L182~212
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| POST /api/user/{userPk}/group/{groupId} | 경로 userPk·groupId | UserGroupMappingResponse |
| DELETE /api/user/{userPk}/group/{groupId} | 경로 userPk·groupId | null |

본문은 받지 않는다. 계정·그룹 조작은 `00.rag/user/user-group-and-permission-management.md`를 따른다.

## 그룹 기본 정보 API
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L71~171
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/group | 선택 offset·limit | PagedResponse의 GroupInfoResponse 목록 |
| GET /api/group/{groupId} | 경로 groupId | GroupInfoResponse |
| POST /api/group | GroupUpsertRequest | GroupInfoResponse |
| PUT /api/group/{groupId} | GroupUpsertRequest | GroupInfoResponse |
| DELETE /api/group/{groupId} | 경로 groupId | null |

단건 GET·PUT은 그룹이 없으면 컨트롤러가 404 NOT_FOUND를 반환한다.

## 그룹의 권한·구성원 조회
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L182~203
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L254~275
```

| 호출 | 조회 조건 | 200 결과 |
|---|---|---|
| GET /api/group/{groupId}/permission | 선택 offset·limit | PagedResponse의 GroupPermissionMappingResponse 목록 |
| GET /api/group/{groupId}/user | 선택 offset·limit | PagedResponse의 GroupMemberResponse 목록 |

두 조회는 대상 그룹이 없으면 컨트롤러가 404 NOT_FOUND를 반환한다.

## 그룹과 권한의 연결 변경
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; GroupController L213~243
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| POST /api/group/{groupId}/permission/{permId} | 경로 groupId·permId | GroupPermissionMappingResponse |
| DELETE /api/group/{groupId}/permission/{permId} | 경로 groupId·permId | null |

두 요청 모두 본문을 받지 않는다.

## 권한 데이터 조회·저장·삭제
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; PermissionController L63~164
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/permission | 선택 offset·limit | PagedResponse의 PermissionInfoResponse 목록 |
| GET /api/permission/{permId} | 경로 permId | PermissionInfoResponse |
| POST /api/permission | PermissionUpsertRequest | PermissionInfoResponse |
| PUT /api/permission/{permId} | PermissionUpsertRequest | PermissionInfoResponse |
| DELETE /api/permission/{permId} | 경로 permId | null |

단건 GET·PUT의 대상 부재는 404 NOT_FOUND다.

## HTTP-SIM 제어 인증과 401 본문
```rag-meta
원천: nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/security/InternalTokenAuthFilter.java @ 5e9b02e3ae16 ; InternalTokenAuthFilter L34~83
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; OrchestratorRuntimeConfiguration L155~160
원천: nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/EquipmentWorkerModuleConfiguration.java @ 5e9b02e3ae16 ; EquipmentWorkerModuleConfiguration L50~55
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamRuntimeConfiguration.java @ 5e9b02e3ae16 ; UpstreamRuntimeConfiguration L86~91
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; HttpSimulatorOrchestratorClient L114~139
```

HTTP-SIM 제어 요청은 `X-Tc-Internal-Token` 헤더를 사용한다. orchestrator와 worker의 공통 필터는 공유 인증값이 없거나 다르면 401과 error·message 필드가 있는 JSON을 반환한다.

인증값의 교체 절차는 `00.rag/admin/security-operations.md`가 소유한다.

## orchestrator의 Run 생성과 이력 조회
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L30~56
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/CreateRunRequest.java @ 5e9b02e3ae16 ; CreateRunRequest L20~42
```

HTTP-SIM 성공 본문은 ApiResponse로 감싸지 않는다.

| 호출 | 입력 | 200 결과 |
|---|---|---|
| POST /api/demo-runs | CreateRunRequest | RunResponse |
| GET /api/demo-runs | 없음 | RunResponse 목록, 이력 조회 한도 200 |

생성 본문은 mode·scenarioId·비어 있지 않은 targets를 받는다. 대상 항목은 eqpId·commInterface·commMode·bindPort·portIds를 갖는다.

## orchestrator의 Run 조회·종료·MES 활성화
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L59~87
```

| 호출 | 입력 | 200 결과 |
|---|---|---|
| GET /api/demo-runs/{runId} | 경로 runId | RunResponse |
| POST /api/demo-runs/{runId}/stop | 경로 runId | RunResponse |
| POST /api/demo-runs/{runId}/targets/{eqpId}/stop | 경로 두 값 | RunResponse |
| POST /api/demo-runs/{runId}/activate-upstream | 경로 runId | RunResponse |

POST 세 경로는 별도 본문을 받지 않는다.

## RunResponse와 대상별 결과 구조
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/RunResponse.java @ 5e9b02e3ae16 ; RunResponse L17~48
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/TargetResponse.java @ 5e9b02e3ae16 ; TargetResponse L16~51
```

RunResponse에는 runId·mode·state·targets·createdAt·scenarioId가 있다. createdAt은 생성 시각을 문자열로 반환한다.

targets의 각 항목에는 eqpId·state·commInterface·completedCycleCount·erroredCycleCount·latencyStats·issuedLotRunIds가 있다.

이력 변환은 latencyStats를 빈 통계로, issuedLotRunIds를 빈 목록으로 채운다. 상태값 뜻은 `00.rag/user/state-reference.md`가 소유한다.

## orchestrator 시나리오 카탈로그 API
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L68~106
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/UploadScenarioRequest.java @ 5e9b02e3ae16 ; UploadScenarioRequest L9~10
```

| 호출 | 입력 | 결과 |
|---|---|---|
| GET /api/scenarios | 선택 조회 조건 mode | 200 ScenarioCatalogResponse 목록 |
| POST /api/scenarios | UploadScenarioRequest의 sourceText | 201 ScenarioCatalogResponse |
| DELETE /api/scenarios/{scenarioId} | 경로 scenarioId | 본문 없는 204 |

HTTP-UI의 시나리오 업로드는 file을 받지만 이 API는 JSON 본문에 시나리오 원문을 받는다.

## HTTP-SIM 오류 본문의 종류
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; OrchestratorExceptionAdvice L19~22
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 ; RunController L90~97
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 ; LeaseConflictResponse L10~18
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; ScenarioCatalogController L109~123
```

| 발생 경로 | HTTP 상태·본문 |
|---|---|
| IllegalArgumentException | 400, 오류 문구 문자열 |
| Run 자원 점유 충돌 | 409, rejected 목록의 eqpId·holdingRunId |
| 시나리오 충돌·기본 시나리오 삭제 거부 | 409, code·message |
| Run·삭제할 시나리오 부재 | 본문 없는 404 |

코드별 뜻은 `00.rag/user/error-message-and-code-reference.md`를 따른다.

## worker가 보내는 Lot 완료 보고
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/LotEventController.java @ 5e9b02e3ae16 ; LotEventController L21~36
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LotEventRequest.java @ 5e9b02e3ae16 ; LotEventRequest L12~13
```

`POST /internal/runs/{runId}/lot-events`는 worker에서 orchestrator로 보내는 보고다. JSON LotEventRequest의 eqpId·portId·errored·latencyMs를 서비스에 전달한다. 응답은 본문 없는 202다.

## 설비 worker의 내부 주입·중단 경로
```rag-meta
원천: nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/api/EquipmentWorkerInternalController.java @ 5e9b02e3ae16 ; EquipmentWorkerInternalController L21~43
```

대상 서버는 simulator worker-equipment다.

| 호출 | 입력 | 결과 |
|---|---|---|
| POST /internal/inject | EquipmentInjectRequest | 200 EquipmentInjectResponse |
| POST /internal/plc/inject | PlcInjectRequest | 200 EquipmentInjectResponse |
| POST /internal/abort | 설비 worker의 AbortRequest.runId | 본문 없는 204 |

## 업스트림 worker의 내부 주입·중단 경로
```rag-meta
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/api/UpstreamWorkerInternalController.java @ 5e9b02e3ae16 ; UpstreamWorkerInternalController L22~39
```

대상 서버는 simulator worker-upstream이다. 경로가 같은 설비 worker와 호출 대상을 구별한다.

| 호출 | 입력 | 결과 |
|---|---|---|
| POST /internal/inject | UpstreamInjectRequest | 200 UpstreamInjectResponse |
| POST /internal/abort | 업스트림 worker의 AbortRequest.runId | 본문 없는 204 |

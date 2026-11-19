# 권한 코드
```rag-meta
유형: 참조
적용: nori-tc-exec f1d5e0a8e727·nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/user-group-and-permission-management.md
```

## 권한 자료의 출처와 API 허용 범위
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 2026-09-14 운영 권한 목록 캡처 L1~295
원천: nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 등록 L41~49
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized·matchUri·matchMethod L142~258
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaApiPermissionPort.java @ 985d17c604dd ; 활성 API 권한 필터 L66~76
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 권한 후보 조회·표시 L40~86
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 목록 요청 L347~365
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 목록 응답 L63~81
```

이 문서의 권한 연결은 2026-09-14 운영 서버의 권한 목록 캡처 32건과 V8 migration이 추가하는 COORDINATION_ADMIN_READ를 근거로 한다. 개인·그룹의 현재 부여 현황은 나타내지 않는다.

설치마다 DB 권한 행이 다를 수 있다. 현재 등록은 「권한 그룹」의 「그룹 권한 관리」 후보 목록이나 GET /api/permission으로 확인한다.

API 범위의 「정확」은 경로 전체 일치, 「접두」는 적힌 문자열로 시작하는 경로, 「패턴」은 정규식 일치다. 「모든 메서드」는 GET·POST·PUT·DELETE 등을 구분하지 않는 등록이다.

서버의 경로·메서드 권한 검사는 요청과 맞는 활성 API 권한 중 하나라도 보유하면 통과한다. 맞는 등록이 없으면 차단한다. 메뉴 진입·입력·대상 상태 검사는 별개다.

## 모니터링 메뉴의 Live-Map과 처리실패
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 모니터링 항목 L49~70
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L1~34
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모니터링 화면 가드 L85~91
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1091~1103
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_READ·LIVE_MAP_READ L114~122 L232~240
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 네 범주 조회 L81~90 L129~138 L177~209
```

「모니터링」 메뉴의 표시·화면 진입과 기본 조회 API는 다음 코드에 연결된다.

| 권한 코드 | 메뉴·화면 | 등록 API 범위 |
|---|---|---|
| LIVE_MAP_READ | Live-Map | 정확 /api/live-map, GET |
| DLQ_READ | 처리실패 | 접두 /api/dlq, GET |

DLQ_READ는 처리 실패 목록뿐 아니라 격리 설비·Repair Task 조회도 포함한다.

## 설비와 통신 방식별 모델 화면
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 설비·모델 항목 L72~108
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비·모델 화면 가드 L93~101
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 권한 값 L12~22
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1092~1101
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; EQP_READ·MODEL_READ L124~132 L241~249
```

| 권한 코드 | 메뉴·화면 진입 | 등록 API 범위 |
|---|---|---|
| EQP_READ | 「설비」의 「설비」 | 접두 /api/eqp, GET |
| MODEL_READ | 「모델」의 「SECS」·「Socket」·「PLC」 | 접두 /api/model, GET |

설비나 모델의 변경 권한으로 조회 API가 허용돼도 메뉴·화면 진입은 이 표의 조회 코드를 따로 검사한다.

## 배포와 사용자·그룹 메뉴
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 배포·시스템 항목·메뉴 필터 L110~151
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 배포·사용자 화면 가드 L103~109
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L7~28
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1094~1103
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_READ·USER_READ L88~96 L277~285
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹 API 권한 L142~150
```

| 권한 코드 | 메뉴·화면 진입 | 등록 API 범위 |
|---|---|---|
| DEPLOY_READ | 「적용」의 「배포」 | 접두 /api/deploys, GET |
| USER_READ | 「시스템」의 「사용자·그룹」 | 접두 /api/user, GET |

USER_READ의 API 범위는 사용자 조회다. 「권한 그룹」의 그룹 목록은 GROUP_WRITE가 별도로 필요하다.

## 메뉴가 하나도 없는 계정의 진입 기준
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveVisibleAppNavigationGroups·resolveLoginLandingRoute L140~170
원천: nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 ; RequirePermission L19~40
```

메뉴 코드가 없는 항목은 숨기고, 남은 항목이 없는 메뉴 묶음도 숨긴다. 로그인 뒤에는 접근 가능한 첫 메뉴로 이동한다. 접근 가능한 메뉴가 하나도 없으면 Live-Map 경로에서 화면 권한 안내를 표시한다.

권한 부여 절차는 `00.rag/user/user-group-and-permission-management.md`, 권한 거부 진단은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

## 설비·모델 변경과 비동기 결과 조회
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ASYNC_READ·EQP_WRITE·MODEL_WRITE L14~22 L133~141 L250~258
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 변경·명령 L260~443
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; branch 작업 L56~157
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; Flow 작업 L65~154
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 파라미터 변경 L139~242
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 결과 조회 L39~89
```

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| EQP_WRITE | 설비 조회·등록·수정·삭제·명령·파라미터 변경 | 접두 /api/eqp, 모든 메서드 |
| MODEL_WRITE | 모델 조회·등록·수정·삭제·branch·Flow 변경 | 접두 /api/model, 모든 메서드 |
| ASYNC_READ | 접수한 비동기 작업 결과 조회 | 접두 /api/async, GET |

설비 명령을 보내는 권한과 비동기 결과를 읽는 권한은 서로 다르다.

## Live-Map 배치 조회와 편집
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 레이아웃 권한 L214~231
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; canEdit·canViewLayout·보기 단추 L64~86 L257~309
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보기 문구 L364~365
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; 배치·객체 처리 L34~106
```

Live-Map에 진입한 계정의 배치·구조물 기능은 다음과 같다.

| 권한 코드 | 화면 기능 | 등록 API 범위 |
|---|---|---|
| LIVE_MAP_LAYOUT_READ | 저장된 배치·구조물 조회, 「3D」 보기 | 접두 /api/live-map/layout, GET |
| LIVE_MAP_LAYOUT_WRITE | 배치·구조물 조회·편집, 「구조물 편집」 | 접두 /api/live-map/layout, 모든 메서드 |

두 코드 중 하나면 배치 보기를 허용하며, 편집은 LIVE_MAP_LAYOUT_WRITE를 검사한다.

## 현재 데모의 시나리오·진행 조회와 실행 제어
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 데모 실행 권한 L178~204
원천: nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; Run·시나리오 처리 L54~112
```

현재 Live-Map의 데모는 /api/live-map/demo-runs 경로를 사용한다.

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| LIVE_MAP_DEMO_RUN_READ | 시나리오 목록·실행 이력·진행 현황 조회 | 접두 /api/live-map/demo-runs, GET |
| LIVE_MAP_DEMO_RUN_WRITE | 테스트 시작·전체 종료·설비별 종료·시나리오 업로드 | 접두 /api/live-map/demo-runs, POST |

실행 절차는 `00.rag/user/demo-execution.md`에서 확인한다.

## 등록은 남아 있으나 현재 쓰지 않는 데모 권한
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 옛 데모 경로 등록 L160~177 L205~213
원천: nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 현재 처리 경로 L33~122
```

| 권한 코드 | 등록 API 범위 |
|---|---|
| LIVE_MAP_DEMO_JOB_READ | 접두 /api/live-map/demo-jobs, GET |
| LIVE_MAP_DEMO_JOB_WRITE | 접두 /api/live-map/demo-jobs, POST |
| LIVE_MAP_DEMO_SCENARIO_READ | 정확 /api/live-map/demo-scenarios, GET |

확인한 소스 판본의 서버·화면에는 이 경로를 처리하는 곳이 없어 세 코드를 부여해도 열리는 기능이 없다. 현재 데모는 LIVE_MAP_DEMO_RUN_READ·LIVE_MAP_DEMO_RUN_WRITE·LIVE_MAP_DEMO_RUN_SCENARIO_DELETE를 사용한다.

## 업로드한 데모 시나리오 삭제
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 시나리오 삭제 L187~195
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; canDeleteScenario·삭제 단추 L128~131 L835~858
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; deleteSimulatorScenario L115~122
```

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| LIVE_MAP_DEMO_RUN_SCENARIO_DELETE | 업로드한 데모 시나리오 삭제 | 접두 /api/live-map/demo-runs/scenarios, DELETE |

화면의 시나리오 삭제 기능도 이 코드를 검사한다. 데모 실행의 POST 권한과 삭제의 DELETE 권한은 구분된다.

## 배포 생성과 시나리오 사전 검토
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_PREVIEW·DEPLOY_WRITE L79~87 L97~105
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 생성·사전 검토 L550~568 L715~736
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; create·preview L171~202
```

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| DEPLOY_WRITE | 새 배포 생성 | 정확 /api/deploys, POST |
| DEPLOY_PREVIEW | 배포 시나리오 사전 검토 | 정확 /api/deploys/register/scenario-preview, POST |

사전 검토와 배포 생성은 다른 요청이므로 두 코드가 서로를 대신하지 않는다.

## 배포 실행·보류·취소·재실행과 삭제
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_ACTION·DEPLOY_DELETE L52~78
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 동작 L572~691
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 배포 동작 L236~309
```

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| DEPLOY_ACTION | 배포 실행·보류·취소·재실행 | 패턴 ^/api/deploys/[0-9]+/(start\|hold\|cancel\|redeploy)$, POST |
| DEPLOY_DELETE | 배포 삭제 | 패턴 ^/api/deploys/[0-9]+$, DELETE |

대상 배포 번호가 들어간 경로만 연결된다. 배포 생성과 시나리오 사전 검토는 DEPLOY_ACTION의 범위에 들어가지 않는다.

## 현재 계정 정보와 로그아웃
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 계정 공통 권한 L24~42
원천: nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 ; getMe L13~26
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; logout L176~194
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; logout·me L152~238
```

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| AUTH_ME_PERM | 로그인한 현재 계정·권한 정보 조회 | 정확 /api/auth/me, GET |
| AUTH_LOGOUT_PERM | 로그아웃 | 정확 /api/auth/logout, POST |

로그인 요청과 현재 계정 정보 조회는 별개다. 계정 정보 수정·비밀번호 변경은 /api/user 쪽 권한을 사용한다.

## 라인 선택 목록과 설비 통신 로그
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; COMM_LOG_READ·LINE_READ L43~51 L151~159
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; getLines L24~44
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; 로그 조회 L20~47
```

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| LINE_READ | 화면에서 선택할 라인 목록 조회 | 정확 /api/line, GET |
| COMM_LOG_READ | 선택 설비의 통신 기록 조회 | 접두 /api/comm-logs, GET |

화면을 여는 조회 코드와 라인·통신 기록을 불러오는 코드는 별도로 연결된다.

## 사용자 변경과 그룹 소속 변경
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; USER_INFO_WRITE·USER_WRITE L268~276 L286~294
원천: nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 사용자 변경 L160~250
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 멤버 추가·제거 L299~343
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 사용자·소속·비밀번호 처리 L124~269
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 추가 권한 검사 L57~107
```

| 권한 코드 | 허용 범위 |
|---|---|
| USER_INFO_WRITE | 접두 /api/user의 모든 메서드: 사용자 조회·등록·수정·삭제·비밀번호 변경·그룹 소속 변경 |
| USER_WRITE | 접두 /api/user의 모든 메서드: 사용자 조회·등록·수정·삭제·비밀번호 변경·그룹 소속 변경 |

권한 목록 캡처에서 두 코드 중 하나면 사용자 변경 API 권한을 충족한다. 대상 사용자의 권한 범위 등 서버의 추가 검사는 계속 적용된다.

## 그룹 관리와 권한 후보 목록
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; GROUP_WRITE·PERMISSION_WRITE L142~150 L259~267
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 후보·매핑 요청 L347~436
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 그룹·권한 매핑 L71~264
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 정의 처리 L63~164
```

| 권한 코드 | 허용 범위 |
|---|---|
| GROUP_WRITE | 접두 /api/group의 모든 메서드: 그룹·멤버 목록 조회, 그룹 등록·수정·삭제·그룹 권한 조회·부여·회수 |
| PERMISSION_WRITE | 접두 /api/permission의 모든 메서드: 권한 후보 조회와 권한 정의 등록·수정·삭제 |

「그룹 권한 관리」의 후보는 PERMISSION_WRITE로 읽고, 그룹에 권한을 연결하거나 해제하는 요청은 GROUP_WRITE로 검사한다.

## 메뉴 진입에 쓰이지 않는 서버 작업 권한
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ACTUATOR_READ·DEPLOY_ADMIN L6~13 L61~69
원천: nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 L41~56
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/ActuatorAccessAuthorizationManager.java @ 985d17c604dd ; authorize L69~107
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiActuatorSecurityProperties.java @ 985d17c604dd ; 기본 권한 코드 L46~46
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; validate·reload·catalog L29~112
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; findOperation L41~94
```

| 권한 코드 | 대상 작업 |
|---|---|
| ACTUATOR_READ | 기본 설정의 민감 운영 지표 조회. 인증·허용된 접속 범위도 검사한다. |
| DEPLOY_ADMIN | 배포 시나리오 정의 검증·다시 읽기·목록 조회 API |
| COORDINATION_ADMIN_READ | 접두 /api/coordination/operations의 GET: 조정 작업 관리 조회 |

이 코드는 메뉴 표시·화면 진입 코드가 아니다. 내부 운영 작업의 실행은 운영 담당자에게 맡긴다.

## DLQ 삭제 API 권한
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_DELETE L104~112
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; Gateway·Business 삭제 L100~115 L148~163
```

| 권한 코드 | 허용 작업 | 등록 API 범위 |
|---|---|---|
| DLQ_DELETE | Gateway·Business DLQ 항목 삭제 API | 접두 /api/dlq, DELETE |

이 코드는 「처리실패」 메뉴의 진입 권한과 다르다. DLQ 조회 코드는 GET에만 연결되므로 삭제 요청을 허용하지 않는다.

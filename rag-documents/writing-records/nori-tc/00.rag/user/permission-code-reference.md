# 권한 코드 작성 기록

## 대상

- objectKey: `00.rag/user/permission-code-reference.md`
- 구획·필요 권한: user `[AI_DOC_READ]`
- 유형: 참조
- 검색 원문 SHA-256: `4e892f173150b33bde8dd14bfa7c274d9dcf8d1f93bdd25182e192a0e05b246c`
- 검색 원문 줄 수: 286
- 절 수: 18
- 검색 조각: 58개, 권한 코드 표 행 33개
- 확인일: 2026-11-19 (한국 시간)
- 이 세션은 독립 작성·자체검사만 수행했다. 메인 세션의 독립 사실 검증과 업로드는 수행하지 않았다.

## 원천 판본

| 저장소 | HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-exec | f1d5e0a8e727 | 본문 원천 2파일과 추가 대조한 매뉴얼·시드·patch 경로 status exit 0, 변경 출력 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | 본문 원천 14파일 status exit 0, 변경 출력 없음 |
| nori-tc-ui-backend | 985d17c604dd | 본문 원천 22파일 status exit 0, 변경 출력 없음 |

조회 형식: `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C ../<저장소> status --short -- <원천 경로들>`. 최초 조회에서 Git 소유자 차이 오류를 확인한 뒤 읽기 전용 명령에만 정확한 저장소 경로를 지정했다. 전역 설정은 수정하지 않았다. 사용자 ignore 파일 접근 경고는 있었으며, 원천 경로 status 결과는 변경 없음이었다.

다음은 본문에서 사용한 원천의 판본과 줄 범위다. 같은 파일을 다른 절에서 사용한 경우 위치를 모았다.

| 원천 | 확인 위치 |
|---|---|
| nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 | 조정 조회 권한 등록 L41~49 / 조정 조회 권한 L41~56 |
| nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 | 2026-09-14 운영 권한 목록 캡처 L1~295 / DLQ_READ·LIVE_MAP_READ L114~122 L232~240 / EQP_READ·MODEL_READ L124~132 L241~249 / DEPLOY_READ·USER_READ L88~96 L277~285 / 그룹 API 권한 L142~150 / ASYNC_READ·EQP_WRITE·MODEL_WRITE L14~22 L133~141 L250~258 / 레이아웃 권한 L214~231 / 데모 실행 권한 L178~204 / 옛 데모 경로 등록 L160~177 L205~213 / 시나리오 삭제 L187~195 / DEPLOY_PREVIEW·DEPLOY_WRITE L79~87 L97~105 / DEPLOY_ACTION·DEPLOY_DELETE L52~78 / 계정 공통 권한 L24~42 / COMM_LOG_READ·LINE_READ L43~51 L151~159 / USER_INFO_WRITE·USER_WRITE L268~276 L286~294 / GROUP_WRITE·PERMISSION_WRITE L142~150 L259~267 / ACTUATOR_READ·DEPLOY_ADMIN L6~13 L61~69 / DLQ_DELETE L104~112 |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaApiPermissionPort.java @ 985d17c604dd | 활성 API 권한 필터 L66~76 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd | 결과 조회 L39~89 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd | logout·me L152~238 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd | 로그 조회 L20~47 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd | findOperation L41~94 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd | create·preview L171~202 / 배포 동작 L236~309 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd | validate·reload·catalog L29~112 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd | 네 범주 조회 L81~90 L129~138 L177~209 / Gateway·Business 삭제 L100~115 L148~163 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd | 변경·명령 L260~443 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd | 파라미터 변경 L139~242 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd | 그룹·권한 매핑 L71~264 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd | getLines L24~44 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd | Run·시나리오 처리 L54~112 / 현재 처리 경로 L33~122 / deleteSimulatorScenario L115~122 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd | 배치·객체 처리 L34~106 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd | branch 작업 L56~157 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd | Flow 작업 L65~154 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd | 권한 목록 응답 L63~81 / 권한 정의 처리 L63~164 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd | 사용자·소속·비밀번호 처리 L124~269 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/ActuatorAccessAuthorizationManager.java @ 985d17c604dd | authorize L69~107 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd | isAuthorized·matchUri·matchMethod L142~258 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd | 추가 권한 검사 L57~107 |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiActuatorSecurityProperties.java @ 985d17c604dd | 기본 권한 코드 L46~46 |
| nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 | RequirePermission L19~40 |
| nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 | 모니터링 화면 가드 L85~91 / 설비·모델 화면 가드 L93~101 / 배포·사용자 화면 가드 L103~109 |
| nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 | logout L176~194 |
| nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 | 배포 생성·사전 검토 L550~568 L715~736 / 배포 동작 L572~691 |
| nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 | 권한 목록 요청 L347~365 / 멤버 추가·제거 L299~343 / 권한 후보·매핑 요청 L347~436 |
| nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 | 권한 후보 조회·표시 L40~86 |
| nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 | 현재 데모 호출 L49~203 |
| nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 | canDeleteScenario·삭제 단추 L128~131 L835~858 |
| nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 | canEdit·canViewLayout·보기 단추 L64~86 L257~309 |
| nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 | getMe L13~26 |
| nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 | 사용자 변경 L160~250 |
| nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 | navigation L1091~1103 / navigation L1092~1101 / navigation L1094~1103 / 보기 문구 L364~365 |
| nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 | 모니터링 항목 L49~70 / 설비·모델 항목 L72~108 / 배포·시스템 항목·메뉴 필터 L110~151 / resolveVisibleAppNavigationGroups·resolveLoginLandingRoute L140~170 |
| nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 | 조회 권한 값 L1~34 / 권한 값 L12~22 / 조회 권한 값 L7~28 |

추가로 직접 읽고 대조한 원천:

- nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L2572~2610: 부록 A 전체.
- nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 L58~86: 공통·설비·모델·DLQ·구 데모·PAGE 기본 등록. 사용자 계정 비밀값 영역은 읽지 않았다.
- nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L14~39 / postgres_2026_06_02_add_live_map_layout_read_permission.sql L18~33: 라인·배치 기본 등록.
- nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 L31~59 / postgres_2026_06_22_add_live_map_demo_run_scenario_delete_permission.sql L28~42: 현재 데모 조회·제어·삭제 등록.
- nori-tc-exec/docs/db_table/patch/postgres_2026_08_04_add_user_read_permission.sql @ f1d5e0a8e727 L36~51 / postgres_2026_08_04_add_actuator_read_permission.sql L38~53: 조회·지표 등록.
- nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCacheTest.java @ 985d17c604dd L17~56: 숫자 배포 start 허용·preview 거부·잘못된 정규식 거부 기대값을 읽었다. 시험을 실행하지 않았다.
- nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd L113~152 / GroupAdminFacade.java L101~114: 추가 권한 검사 호출 확인.
- 프런트 eqp.api.ts의 설비 조회·변경 호출과 model.api.ts의 모델 조회·변경 호출, 서버 ModelMutationController·ModelQueryController의 대응 매핑을 대조했다. 모두 위 HEAD의 작업 트리 변경 없음 범위다.

## 절별 사실 대조

표의 문장·행은 최종 검색 원문의 비메타 본문을 그대로 옮겼다. 모든 자료 행과 일반 문단의 복수 문장을 해당 원천과 대조했다.

### 권한 자료의 출처와 API 허용 범위

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L21 | 이 문서의 권한 연결은 2026-09-14 운영 서버의 권한 목록 캡처 32건과 V8 migration이 추가하는 COORDINATION_ADMIN_READ를 근거로 한다. 개인·그룹의 현재 부여 현황은 나타내지 않는다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 2026-09-14 운영 권한 목록 캡처 L1~295 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 등록 L41~49 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized·matchUri·matchMethod L142~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaApiPermissionPort.java @ 985d17c604dd ; 활성 API 권한 필터 L66~76 ; nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 권한 후보 조회·표시 L40~86 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 목록 요청 L347~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 목록 응답 L63~81 | 캡처 L2~4의 날짜·조회 출처·count=32와 V8 L41~49 등록을 직접 확인했다. 캡처를 설치 기본 시드로 해석하지 않는다. |
| L23 | 설치마다 DB 권한 행이 다를 수 있다. 현재 등록은 「권한 그룹」의 「그룹 권한 관리」 후보 목록이나 GET /api/permission으로 확인한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 2026-09-14 운영 권한 목록 캡처 L1~295 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 등록 L41~49 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized·matchUri·matchMethod L142~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaApiPermissionPort.java @ 985d17c604dd ; 활성 API 권한 필터 L66~76 ; nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 권한 후보 조회·표시 L40~86 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 목록 요청 L347~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 목록 응답 L63~81 | 화면 후보는 서버 권한 목록 요청 결과를 표시하고 PermissionController는 현재 저장소 조회 결과를 응답한다. V1~V8에서 권한 INSERT는 V8 하나여서 캡처를 전체 설치 시드로 보장할 수 없다. |
| L25 | API 범위의 「정확」은 경로 전체 일치, 「접두」는 적힌 문자열로 시작하는 경로, 「패턴」은 정규식 일치다. 「모든 메서드」는 GET·POST·PUT·DELETE 등을 구분하지 않는 등록이다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 2026-09-14 운영 권한 목록 캡처 L1~295 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 등록 L41~49 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized·matchUri·matchMethod L142~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaApiPermissionPort.java @ 985d17c604dd ; 활성 API 권한 필터 L66~76 ; nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 권한 후보 조회·표시 L40~86 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 목록 요청 L347~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 목록 응답 L63~81 | matchUri의 startsWith·matches·equals와 matchMethod의 null/blank 무제한 처리를 대조함. |
| L27 | 서버의 경로·메서드 권한 검사는 요청과 맞는 활성 API 권한 중 하나라도 보유하면 통과한다. 맞는 등록이 없으면 차단한다. 메뉴 진입·입력·대상 상태 검사는 별개다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 2026-09-14 운영 권한 목록 캡처 L1~295 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 등록 L41~49 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized·matchUri·matchMethod L142~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaApiPermissionPort.java @ 985d17c604dd ; 활성 API 권한 필터 L66~76 ; nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 권한 후보 조회·표시 L40~86 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 목록 요청 L347~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 목록 응답 L63~81 | 활성 API 필터·anyMatch·매칭 없음 거부, 화면 가드와 컨트롤러 입력·대상 검사를 구분함. |

### 모니터링 메뉴의 Live-Map과 처리실패

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L39 | 「모니터링」 메뉴의 표시·화면 진입과 기본 조회 API는 다음 코드에 연결된다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 모니터링 항목 L49~70 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L1~34 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모니터링 화면 가드 L85~91 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1091~1103 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_READ·LIVE_MAP_READ L114~122 L232~240 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 네 범주 조회 L81~90 L129~138 L177~209 | 메뉴 모니터링 묶음과 화면 가드·기본 조회 경로를 대조함. |
| L43 | \| LIVE_MAP_READ \| Live-Map \| 정확 /api/live-map, GET \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 모니터링 항목 L49~70 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L1~34 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모니터링 화면 가드 L85~91 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1091~1103 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_READ·LIVE_MAP_READ L114~122 L232~240 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 네 범주 조회 L81~90 L129~138 L177~209 | LIVE_MAP_READ 상수 값·렌더 문구·정확 GET 경로가 일치함. |
| L44 | \| DLQ_READ \| 처리실패 \| 접두 /api/dlq, GET \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 모니터링 항목 L49~70 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L1~34 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모니터링 화면 가드 L85~91 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1091~1103 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_READ·LIVE_MAP_READ L114~122 L232~240 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 네 범주 조회 L81~90 L129~138 L177~209 | DLQ_READ 메뉴 표시·화면 가드·접두 GET 경로가 일치함. |
| L46 | DLQ_READ는 처리 실패 목록뿐 아니라 격리 설비·Repair Task 조회도 포함한다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 모니터링 항목 L49~70 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L1~34 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모니터링 화면 가드 L85~91 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1091~1103 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_READ·LIVE_MAP_READ L114~122 L232~240 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 네 범주 조회 L81~90 L129~138 L177~209 | DlqController의 gateway·business·quarantine·repair-task GET 네 경로를 확인함. |

### 설비와 통신 방식별 모델 화면

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L59 | \| EQP_READ \| 「설비」의 「설비」 \| 접두 /api/eqp, GET \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 설비·모델 항목 L72~108 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비·모델 화면 가드 L93~101 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 권한 값 L12~22 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1092~1101 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; EQP_READ·MODEL_READ L124~132 L241~249 | EQP_READ 값·설비 묶음/항목 문구·GET 경로를 확인함. |
| L60 | \| MODEL_READ \| 「모델」의 「SECS」·「Socket」·「PLC」 \| 접두 /api/model, GET \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 설비·모델 항목 L72~108 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비·모델 화면 가드 L93~101 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 권한 값 L12~22 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1092~1101 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; EQP_READ·MODEL_READ L124~132 L241~249 | MODEL_READ 하나가 SECS·Socket·PLC 세 라우트를 감싸며 GET /api/model 접두와 연결됨. |
| L62 | 설비나 모델의 변경 권한으로 조회 API가 허용돼도 메뉴·화면 진입은 이 표의 조회 코드를 따로 검사한다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 설비·모델 항목 L72~108 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비·모델 화면 가드 L93~101 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 권한 값 L12~22 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1092~1101 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; EQP_READ·MODEL_READ L124~132 L241~249 | 쓰기 권한의 method=null과 조회 코드가 고정된 라우터 가드를 각각 확인함. |

### 배포와 사용자·그룹 메뉴

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L76 | \| DEPLOY_READ \| 「적용」의 「배포」 \| 접두 /api/deploys, GET \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 배포·시스템 항목·메뉴 필터 L110~151 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 배포·사용자 화면 가드 L103~109 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L7~28 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1094~1103 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_READ·USER_READ L88~96 L277~285 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹 API 권한 L142~150 | 적용 > 배포 문구·DEPLOY_READ 가드·GET 접두를 대조함. |
| L77 | \| USER_READ \| 「시스템」의 「사용자·그룹」 \| 접두 /api/user, GET \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 배포·시스템 항목·메뉴 필터 L110~151 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 배포·사용자 화면 가드 L103~109 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L7~28 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1094~1103 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_READ·USER_READ L88~96 L277~285 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹 API 권한 L142~150 | 시스템 > 사용자·그룹 문구·USER_READ 가드·GET 접두를 대조함. |
| L79 | USER_READ의 API 범위는 사용자 조회다. 「권한 그룹」의 그룹 목록은 GROUP_WRITE가 별도로 필요하다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 배포·시스템 항목·메뉴 필터 L110~151 ; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 배포·사용자 화면 가드 L103~109 ; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; 조회 권한 값 L7~28 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; navigation L1094~1103 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_READ·USER_READ L88~96 L277~285 ; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹 API 권한 L142~150 | USER_READ는 /api/user이고 그룹 목록 /api/group은 GROUP_WRITE임. |

### 메뉴가 하나도 없는 계정의 진입 기준

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L87 | 메뉴 코드가 없는 항목은 숨기고, 남은 항목이 없는 메뉴 묶음도 숨긴다. 로그인 뒤에는 접근 가능한 첫 메뉴로 이동한다. 접근 가능한 메뉴가 하나도 없으면 Live-Map 경로에서 화면 권한 안내를 표시한다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveVisibleAppNavigationGroups·resolveLoginLandingRoute L140~170 ; nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 ; RequirePermission L19~40 | 항목 필터·빈 그룹 제거·첫 접근 경로·첫 경로 fallback과 가드를 확인함. |
| L89 | 권한 부여 절차는 `00.rag/user/user-group-and-permission-management.md`, 권한 거부 진단은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveVisibleAppNavigationGroups·resolveLoginLandingRoute L140~170 ; nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 ; RequirePermission L19~40 ; rag-documents/nori-tc/00.rag/user/user-group-and-permission-management.md @ sha256=ab997cede5665995310408e0f7699cd1854c7e6f468d7ca45a3b3d14f08b0133 L1~405 ; rag-documents/nori-tc/00.rag/user/login-permission-and-screen-troubleshooting.md @ sha256=7b39097837e43b28adc8ca40f6ff98d2d8c8fb7142600e9614b655efdd271748 L1~148 | 지정 소유 문서 두 개의 존재와 권한 부여·거부 진단 절을 읽고 절차를 복제하지 않음. |

### 설비·모델 변경과 비동기 결과 조회

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L103 | \| EQP_WRITE \| 설비 조회·등록·수정·삭제·명령·파라미터 변경 \| 접두 /api/eqp, 모든 메서드 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ASYNC_READ·EQP_WRITE·MODEL_WRITE L14~22 L133~141 L250~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 변경·명령 L260~443 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; branch 작업 L56~157 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; Flow 작업 L65~154 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 파라미터 변경 L139~242 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 결과 조회 L39~89 | 카탈로그의 EQP_WRITE null 메서드와 설비 CRUD·lifecycle·polling·파라미터 컨트롤러 및 프런트 API를 대조함. |
| L104 | \| MODEL_WRITE \| 모델 조회·등록·수정·삭제·branch·Flow 변경 \| 접두 /api/model, 모든 메서드 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ASYNC_READ·EQP_WRITE·MODEL_WRITE L14~22 L133~141 L250~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 변경·명령 L260~443 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; branch 작업 L56~157 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; Flow 작업 L65~154 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 파라미터 변경 L139~242 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 결과 조회 L39~89 | MODEL_WRITE null 메서드와 모델 CRUD·branch·Flow 컨트롤러 및 프런트 API를 대조함. |
| L105 | \| ASYNC_READ \| 접수한 비동기 작업 결과 조회 \| 접두 /api/async, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ASYNC_READ·EQP_WRITE·MODEL_WRITE L14~22 L133~141 L250~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 변경·명령 L260~443 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; branch 작업 L56~157 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; Flow 작업 L65~154 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 파라미터 변경 L139~242 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 결과 조회 L39~89 | ASYNC_READ GET 접두와 operationId/traceId 결과 조회 처리를 확인함. |
| L107 | 설비 명령을 보내는 권한과 비동기 결과를 읽는 권한은 서로 다르다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ASYNC_READ·EQP_WRITE·MODEL_WRITE L14~22 L133~141 L250~258 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 변경·명령 L260~443 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; branch 작업 L56~157 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; Flow 작업 L65~154 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 파라미터 변경 L139~242 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 결과 조회 L39~89 | 설비 명령 POST와 결과 GET의 경로·코드가 분리됨. |

### Live-Map 배치 조회와 편집

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L117 | Live-Map에 진입한 계정의 배치·구조물 기능은 다음과 같다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 레이아웃 권한 L214~231 ; nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; canEdit·canViewLayout·보기 단추 L64~86 L257~309 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보기 문구 L364~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; 배치·객체 처리 L34~106 | LiveMapPage는 라우터의 Live-Map 접근 검사 뒤 내부 보기·편집을 검사함. |
| L121 | \| LIVE_MAP_LAYOUT_READ \| 저장된 배치·구조물 조회, 「3D」 보기 \| 접두 /api/live-map/layout, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 레이아웃 권한 L214~231 ; nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; canEdit·canViewLayout·보기 단추 L64~86 L257~309 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보기 문구 L364~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; 배치·객체 처리 L34~106 | READ GET 경로와 canViewLayout·3D 단추를 확인함. |
| L122 | \| LIVE_MAP_LAYOUT_WRITE \| 배치·구조물 조회·편집, 「구조물 편집」 \| 접두 /api/live-map/layout, 모든 메서드 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 레이아웃 권한 L214~231 ; nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; canEdit·canViewLayout·보기 단추 L64~86 L257~309 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보기 문구 L364~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; 배치·객체 처리 L34~106 | WRITE null 메서드와 canEdit·구조물 편집 렌더 문구 및 배치/객체 CRUD를 확인함. |
| L124 | 두 코드 중 하나면 배치 보기를 허용하며, 편집은 LIVE_MAP_LAYOUT_WRITE를 검사한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 레이아웃 권한 L214~231 ; nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; canEdit·canViewLayout·보기 단추 L64~86 L257~309 ; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 보기 문구 L364~365 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; 배치·객체 처리 L34~106 | canViewLayout=canEdit OR READ이고 builder는 canEdit 조건임. |

### 현재 데모의 시나리오·진행 조회와 실행 제어

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L133 | 현재 Live-Map의 데모는 /api/live-map/demo-runs 경로를 사용한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 데모 실행 권한 L178~204 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; Run·시나리오 처리 L54~112 | 현재 프런트 API의 공통 demo-runs 경로와 서버 매핑이 일치함. |
| L137 | \| LIVE_MAP_DEMO_RUN_READ \| 시나리오 목록·실행 이력·진행 현황 조회 \| 접두 /api/live-map/demo-runs, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 데모 실행 권한 L178~204 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; Run·시나리오 처리 L54~112 | GET scenarios·목록·개별 run이 RUN_READ 접두 범위 안에 있음. |
| L138 | \| LIVE_MAP_DEMO_RUN_WRITE \| 테스트 시작·전체 종료·설비별 종료·시나리오 업로드 \| 접두 /api/live-map/demo-runs, POST \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 데모 실행 권한 L178~204 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; Run·시나리오 처리 L54~112 | POST 생성·전체 stop·대상별 stop·업로드가 RUN_WRITE 접두 범위 안에 있음. |
| L140 | 실행 절차는 `00.rag/user/demo-execution.md`에서 확인한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 데모 실행 권한 L178~204 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; Run·시나리오 처리 L54~112 ; rag-documents/nori-tc/00.rag/user/demo-execution.md @ sha256=c76cf0037a500734d3bd8d5dd9312eaeb2e003a1730d19ef2e574603c080f9b5 L1~257 | 소유 문서 demo-execution.md의 실행 절차 존재를 확인함. |

### 등록은 남아 있으나 현재 쓰지 않는 데모 권한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L151 | \| LIVE_MAP_DEMO_JOB_READ \| 접두 /api/live-map/demo-jobs, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 옛 데모 경로 등록 L160~177 L205~213 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 현재 처리 경로 L33~122 | 캡처의 LIVE_MAP_DEMO_JOB_READ 값·일치 방식·경로·메서드를 대조했다. 현재 기능 의미를 이름에서 추정하지 않고 등록 범위만 기록했다. |
| L152 | \| LIVE_MAP_DEMO_JOB_WRITE \| 접두 /api/live-map/demo-jobs, POST \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 옛 데모 경로 등록 L160~177 L205~213 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 현재 처리 경로 L33~122 | 캡처의 LIVE_MAP_DEMO_JOB_WRITE 값·일치 방식·경로·메서드를 대조했다. 현재 기능 의미를 이름에서 추정하지 않고 등록 범위만 기록했다. |
| L153 | \| LIVE_MAP_DEMO_SCENARIO_READ \| 정확 /api/live-map/demo-scenarios, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 옛 데모 경로 등록 L160~177 L205~213 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 현재 처리 경로 L33~122 | 캡처의 LIVE_MAP_DEMO_SCENARIO_READ 값·일치 방식·경로·메서드를 대조했다. 현재 기능 의미를 이름에서 추정하지 않고 등록 범위만 기록했다. |
| L155 | 확인한 소스 판본의 서버·화면에는 이 경로를 처리하는 곳이 없어 세 코드를 부여해도 열리는 기능이 없다. 현재 데모는 LIVE_MAP_DEMO_RUN_READ·LIVE_MAP_DEMO_RUN_WRITE·LIVE_MAP_DEMO_RUN_SCENARIO_DELETE를 사용한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 옛 데모 경로 등록 L160~177 L205~213 ; nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 현재 데모 호출 L49~203 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 현재 처리 경로 L33~122 | 지정 HEAD의 backend grep(test·md 제외)과 frontend src grep이 모두 일치 0건·exit 1이었다. 현재 demo-runs 호출·처리 코드와 RUN 권한 연결을 대조했다. |

### 업로드한 데모 시나리오 삭제

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L166 | \| LIVE_MAP_DEMO_RUN_SCENARIO_DELETE \| 업로드한 데모 시나리오 삭제 \| 접두 /api/live-map/demo-runs/scenarios, DELETE \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 시나리오 삭제 L187~195 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; canDeleteScenario·삭제 단추 L128~131 L835~858 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; deleteSimulatorScenario L115~122 | 카탈로그 DELETE 접두와 서버 deleteSimulatorScenario·프런트 deleteScenario를 대조함. |
| L168 | 화면의 시나리오 삭제 기능도 이 코드를 검사한다. 데모 실행의 POST 권한과 삭제의 DELETE 권한은 구분된다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 시나리오 삭제 L187~195 ; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; canDeleteScenario·삭제 단추 L128~131 L835~858 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; deleteSimulatorScenario L115~122 | canDeleteScenario 검사와 조건부 삭제 단추, RUN_WRITE POST와 DELETE의 분리를 확인함. |

### 배포 생성과 시나리오 사전 검토

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L179 | \| DEPLOY_WRITE \| 새 배포 생성 \| 정확 /api/deploys, POST \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_PREVIEW·DEPLOY_WRITE L79~87 L97~105 ; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 생성·사전 검토 L550~568 L715~736 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; create·preview L171~202 | 정확 POST /api/deploys와 프런트 생성 호출·서버 create를 확인함. |
| L180 | \| DEPLOY_PREVIEW \| 배포 시나리오 사전 검토 \| 정확 /api/deploys/register/scenario-preview, POST \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_PREVIEW·DEPLOY_WRITE L79~87 L97~105 ; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 생성·사전 검토 L550~568 L715~736 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; create·preview L171~202 | 정확 POST 사전 검토 경로와 프런트·서버 preview를 확인함. |
| L182 | 사전 검토와 배포 생성은 다른 요청이므로 두 코드가 서로를 대신하지 않는다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_PREVIEW·DEPLOY_WRITE L79~87 L97~105 ; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 생성·사전 검토 L550~568 L715~736 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; create·preview L171~202 | EXACT 매칭 두 경로가 서로 다름을 확인함. |

### 배포 실행·보류·취소·재실행과 삭제

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L193 | \| DEPLOY_ACTION \| 배포 실행·보류·취소·재실행 \| 패턴 ^/api/deploys/[0-9]+/(start\|hold\|cancel\|redeploy)$, POST \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_ACTION·DEPLOY_DELETE L52~78 ; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 동작 L572~691 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 배포 동작 L236~309 | 카탈로그 정규식·POST 및 서버 네 action 경로와 프런트 호출이 일치함. |
| L194 | \| DEPLOY_DELETE \| 배포 삭제 \| 패턴 ^/api/deploys/[0-9]+$, DELETE \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_ACTION·DEPLOY_DELETE L52~78 ; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 동작 L572~691 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 배포 동작 L236~309 | 숫자 배포 경로 정규식 DELETE 및 서버·프런트 삭제가 일치함. |
| L196 | 대상 배포 번호가 들어간 경로만 연결된다. 배포 생성과 시나리오 사전 검토는 DEPLOY_ACTION의 범위에 들어가지 않는다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_ACTION·DEPLOY_DELETE L52~78 ; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 동작 L572~691 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 배포 동작 L236~309 | 패턴 범위와 cache 단위시험의 start 허용·preview 거부 기대값을 읽음. |

### 현재 계정 정보와 로그아웃

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L208 | \| AUTH_ME_PERM \| 로그인한 현재 계정·권한 정보 조회 \| 정확 /api/auth/me, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 계정 공통 권한 L24~42 ; nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 ; getMe L13~26 ; nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; logout L176~194 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; logout·me L152~238 | 현재 principal의 userPk·userId·permissionCodes를 반환하며 GET me 권한과 일치함. |
| L209 | \| AUTH_LOGOUT_PERM \| 로그아웃 \| 정확 /api/auth/logout, POST \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 계정 공통 권한 L24~42 ; nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 ; getMe L13~26 ; nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; logout L176~194 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; logout·me L152~238 | POST logout·쿠키/로그아웃 처리와 프런트 호출을 대조함. |
| L211 | 로그인 요청과 현재 계정 정보 조회는 별개다. 계정 정보 수정·비밀번호 변경은 /api/user 쪽 권한을 사용한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 계정 공통 권한 L24~42 ; nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 ; getMe L13~26 ; nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; logout L176~194 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; logout·me L152~238 | 로그인·me·사용자 수정·reset 요청 경로를 각각 대조함. |

### 라인 선택 목록과 설비 통신 로그

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L222 | \| LINE_READ \| 화면에서 선택할 라인 목록 조회 \| 정확 /api/line, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; COMM_LOG_READ·LINE_READ L43~51 L151~159 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; getLines L24~44 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; 로그 조회 L20~47 | LINE_READ 정확 GET과 LineController의 활성 라인 목록을 확인함. |
| L223 | \| COMM_LOG_READ \| 선택 설비의 통신 기록 조회 \| 접두 /api/comm-logs, GET \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; COMM_LOG_READ·LINE_READ L43~51 L151~159 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; getLines L24~44 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; 로그 조회 L20~47 | COMM_LOG_READ GET 접두와 eqpId별 CommLogController 조회를 확인함. |
| L225 | 화면을 여는 조회 코드와 라인·통신 기록을 불러오는 코드는 별도로 연결된다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; COMM_LOG_READ·LINE_READ L43~51 L151~159 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; getLines L24~44 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; 로그 조회 L20~47 | 메뉴 권한 경로와 /api/line·/api/comm-logs 경로가 구분됨. |

### 사용자 변경과 그룹 소속 변경

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L238 | \| USER_INFO_WRITE \| 접두 /api/user의 모든 메서드: 사용자 조회·등록·수정·삭제·비밀번호 변경·그룹 소속 변경 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; USER_INFO_WRITE·USER_WRITE L268~276 L286~294 ; nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 사용자 변경 L160~250 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 멤버 추가·제거 L299~343 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 사용자·소속·비밀번호 처리 L124~269 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 추가 권한 검사 L57~107 | USER_INFO_WRITE null 메서드 /api/user와 사용자 CRUD·reset·소속 매핑을 확인함. |
| L239 | \| USER_WRITE \| 접두 /api/user의 모든 메서드: 사용자 조회·등록·수정·삭제·비밀번호 변경·그룹 소속 변경 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; USER_INFO_WRITE·USER_WRITE L268~276 L286~294 ; nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 사용자 변경 L160~250 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 멤버 추가·제거 L299~343 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 사용자·소속·비밀번호 처리 L124~269 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 추가 권한 검사 L57~107 | USER_WRITE도 같은 resource·method=null이며 동일 사용자 API 작업에 매칭됨. |
| L241 | 권한 목록 캡처에서 두 코드 중 하나면 사용자 변경 API 권한을 충족한다. 대상 사용자의 권한 범위 등 서버의 추가 검사는 계속 적용된다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; USER_INFO_WRITE·USER_WRITE L268~276 L286~294 ; nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 사용자 변경 L160~250 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 멤버 추가·제거 L299~343 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 사용자·소속·비밀번호 처리 L124~269 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 추가 권한 검사 L57~107 | 캡처의 두 /api/user·method=null 행과 cache anyMatch·추가 권한 검사 연결은 유지하며 자료의 출처 표현만 정정했다. |

### 그룹 관리와 권한 후보 목록

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L253 | \| GROUP_WRITE \| 접두 /api/group의 모든 메서드: 그룹·멤버 목록 조회, 그룹 등록·수정·삭제·그룹 권한 조회·부여·회수 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; GROUP_WRITE·PERMISSION_WRITE L142~150 L259~267 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 후보·매핑 요청 L347~436 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 그룹·권한 매핑 L71~264 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 정의 처리 L63~164 | GROUP_WRITE null 메서드와 그룹 CRUD·멤버 GET·그룹 권한 매핑을 대조함. |
| L254 | \| PERMISSION_WRITE \| 접두 /api/permission의 모든 메서드: 권한 후보 조회와 권한 정의 등록·수정·삭제 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; GROUP_WRITE·PERMISSION_WRITE L142~150 L259~267 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 후보·매핑 요청 L347~436 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 그룹·권한 매핑 L71~264 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 정의 처리 L63~164 | PERMISSION_WRITE null 메서드와 PermissionController의 후보 GET·정의 CRUD를 대조함. |
| L256 | 「그룹 권한 관리」의 후보는 PERMISSION_WRITE로 읽고, 그룹에 권한을 연결하거나 해제하는 요청은 GROUP_WRITE로 검사한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; GROUP_WRITE·PERMISSION_WRITE L142~150 L259~267 ; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 후보·매핑 요청 L347~436 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 그룹·권한 매핑 L71~264 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/PermissionController.java @ 985d17c604dd ; 권한 정의 처리 L63~164 | Group API의 후보 GET /permission과 POST/DELETE /group/.../permission/...를 각각 대조함. |

### 메뉴 진입에 쓰이지 않는 서버 작업 권한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L270 | \| ACTUATOR_READ \| 기본 설정의 민감 운영 지표 조회. 인증·허용된 접속 범위도 검사한다. \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ACTUATOR_READ·DEPLOY_ADMIN L6~13 L61~69 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 L41~56 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/ActuatorAccessAuthorizationManager.java @ 985d17c604dd ; authorize L69~107 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiActuatorSecurityProperties.java @ 985d17c604dd ; 기본 권한 코드 L46~46 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; validate·reload·catalog L29~112 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; findOperation L41~94 | 기본 requiredPermCode=ACTUATOR_READ, 별도 판단기의 IP·인증·코드 3조건을 확인함. |
| L271 | \| DEPLOY_ADMIN \| 배포 시나리오 정의 검증·다시 읽기·목록 조회 API \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ACTUATOR_READ·DEPLOY_ADMIN L6~13 L61~69 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 L41~56 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/ActuatorAccessAuthorizationManager.java @ 985d17c604dd ; authorize L69~107 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiActuatorSecurityProperties.java @ 985d17c604dd ; 기본 권한 코드 L46~46 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; validate·reload·catalog L29~112 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; findOperation L41~94 | DeployScenarioAdminController의 validate·reload·catalog가 실제 catalog service에 연결됨. |
| L272 | \| COORDINATION_ADMIN_READ \| 접두 /api/coordination/operations의 GET: 조정 작업 관리 조회 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ACTUATOR_READ·DEPLOY_ADMIN L6~13 L61~69 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 L41~56 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/ActuatorAccessAuthorizationManager.java @ 985d17c604dd ; authorize L69~107 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiActuatorSecurityProperties.java @ 985d17c604dd ; 기본 권한 코드 L46~46 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; validate·reload·catalog L29~112 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; findOperation L41~94 | V8의 PREFIX·GET과 CoordinationAdminController의 조회 서비스 호출을 확인함. |
| L274 | 이 코드는 메뉴 표시·화면 진입 코드가 아니다. 내부 운영 작업의 실행은 운영 담당자에게 맡긴다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; ACTUATOR_READ·DEPLOY_ADMIN L6~13 L61~69 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 권한 L41~56 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/ActuatorAccessAuthorizationManager.java @ 985d17c604dd ; authorize L69~107 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiActuatorSecurityProperties.java @ 985d17c604dd ; 기본 권한 코드 L46~46 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployScenarioAdminController.java @ 985d17c604dd ; validate·reload·catalog L29~112 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; findOperation L41~94 | 전체 메뉴 트리에 세 코드가 없음을 확인함. 운영 실행 방법·설정 접근은 수록하지 않음. |

### DLQ 삭제 API 권한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L284 | \| DLQ_DELETE \| Gateway·Business DLQ 항목 삭제 API \| 접두 /api/dlq, DELETE \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_DELETE L104~112 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; Gateway·Business 삭제 L100~115 L148~163 | DELETE 접두 권한과 Gateway·Business 삭제 컨트롤러의 실제 port.deleteDlq 호출을 확인함. |
| L286 | 이 코드는 「처리실패」 메뉴의 진입 권한과 다르다. DLQ 조회 코드는 GET에만 연결되므로 삭제 요청을 허용하지 않는다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DLQ_DELETE L104~112 ; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; Gateway·Business 삭제 L100~115 L148~163 | DLQ_READ는 GET이고 DELETE 행은 별도이며 메뉴는 DLQ_READ를 검사함. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼 | 코드·기본 데이터 | 작성 결과 |
|---|---|---|---|
| 그룹 권한 부여·회수 | U 부록 A L2607은 PERMISSION_WRITE를 그룹 권한 부여·회수로 설명한다. | 카탈로그 L142~150·L259~267, GroupController L213~243: 매핑 변경은 GROUP_WRITE, 후보/정의 API는 PERMISSION_WRITE다. | 두 권한을 요청 경로별로 구별했다. |
| 사용자·그룹 범위 | U L2609~2610은 USER_READ·USER_WRITE를 사용자와 그룹의 조회·변경으로 묶는다. | /api/user와 /api/group은 별도 접두다. 그룹 정의 조회·변경은 GROUP_WRITE, 사용자 소속 변경은 사용자 쓰기 코드다. | 사용자 조회·변경과 그룹 정의·권한 매핑을 분리했다. |
| 데모의 구 경로 코드 | U L2596~2601은 JOB_READ·JOB_WRITE·SCENARIO_READ를 현재 데모 화면에 연결한다. | 현재 프런트 live-map-demo-run.api.ts와 LiveMapController는 demo-runs 경로만 사용한다. 과거 demo-jobs·demo-scenarios 매핑은 현재 서버·프런트 사용처 검색에 나오지 않았다. | 현재 RUN_READ·RUN_WRITE·RUN_SCENARIO_DELETE 연결을 보존하고, 옛 세 코드는 등록 범위와 현재 사용처 없음으로 별도 수록했다. |
| 32개가 전부라는 설명 | U L2575는 32개가 그룹 권한 대화상자의 전부라고 한다. | JSON은 32개이나 V8 migration L46에 COORDINATION_ADMIN_READ가 별도로 있고 대응 컨트롤러도 있다. 화면 후보는 서버의 활성 권한을 조회한다. | 전체 32개라는 현장 단정을 쓰지 않고 V8의 현재 사용처가 있는 코드도 포함했다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 사용자·그룹별 실제 권한 부여, 실제 카탈로그 전체와 migration 적용 현황 | 현장 접속·DB 조회를 하지 않았다. 과거 운영 캡처와 V8 등록은 현재 현장 부여 근거가 아니다. | TC 권한 운영 관리자 |
| DASH_READ의 현재 화면 접근 의미 | sample seed L77은 PAGE 권한이지만 현재 프런트 DASH_READ_PERMISSION의 실제 값은 LIVE_MAP_READ다. 활성 API 적재도 PAGE를 제외한다. | TC UI·권한 개발 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 문서 objectKey | 넣지 않은 사실 |
|---|---|
| 00.rag/user/user-group-and-permission-management.md | 사용자·그룹 등록 입력, 그룹 소속 변경, 권한 후보 검색·체크·저장·부여 제한 절차 |
| 00.rag/user/login-permission-and-screen-troubleshooting.md | 401·403·세션·캐시 실패의 진단 순서, 오류 표시·인계 절차 |
| 00.rag/user/demo-execution.md | 대상·시나리오 선택, 테스트 시작·종료·업로드·삭제 조작 |
| 00.rag/user/deployment-workflow.md | 등록·사전 검토·실행·보류·취소·이력·삭제 조작 및 작업 상태 조건 |
| 00.rag/user/live-map-layout-editing.md | 배치·구조물의 편집·저장 방법과 객체 입력 조건 |
| 00.rag/user/equipment-registration-and-settings.md / equipment-commands-and-polling.md / equipment-model-parameter-and-jar-update.md | 설비의 입력·명령·파라미터 조작과 결과 확인 |
| 00.rag/user/model-registration-and-branching.md / model-details-and-flow-editing.md | 모델·branch·Flow의 실제 편집·체크인 조작 |
| 00.rag/user/message-results-and-communication-logs.md / state-reference.md / error-message-and-code-reference.md | 기록 조회 절차·상태값 표·오류값 표 |

기존 00.rag/user 문서에서 권한 문장·표를 전수 검색하고 위 소유 문서의 관련 절을 읽었다. 참조 문서는 코드와 범위가 주체이며, 업무 문서의 필수 선행 조건은 최소 교집합으로만 남긴다. 문장 복제나 절차 복제를 하지 않았다. 다른 문서를 수정하지 않았다.

## 검사 결과

현재 최종 판정은 마지막 「메인 검증 결함 2건 보완」에 기록한다. 아래 검사·재개 기록의 수량과 SHA는 각각 그 당시 판본의 이력이다.

아래 옛 경로의 검사는 중단 전 이력이다. 재개 후 새 경로에서 수행한 최종 검사는 마지막 「재개 후 최종 확인」에 기록한다.

- 검사기: 사용자 지정 Git Bash check.sh, Java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8, commonmark 0.32.0, 현재 ContentSanitizer mask-v1.
- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/permission-code-reference.md`
- 최종 실제 호출은 Git Bash 실행 파일 `C:/Program Files/Git/usr/bin/bash.exe`를 명시했고 검사기·인자는 동일하다.
- 최종 exit 0. 위반 0, CROSS VIOLATION 0, MASK 출력 0. sections=17, children=53, maxChild=137, maxParent=311.
- 최종 검사 출력의 검색 원문 SHA·줄 수가 이 기록의 대상 값과 일치한다. 기록은 검색 원문과 별개이며 검사 대상이 아니다.
- 카탈로그 32개 중 현재 사용처를 찾지 못한 데모 코드 3개를 제외하고 V8의 조정 조회 1개를 더한 30개 코드가 표에 정확히 한 번씩 존재함을 별도 대조했다. 카탈로그의 27개 행은 resource·메서드를 원문과 자동 대조했다. COORDINATION_ADMIN_READ는 V8 등록과 직접 대조하고, ACTUATOR_READ·DEPLOY_ADMIN은 별도 판단기·컨트롤러를 직접 읽었다.
- UTF-8 BOM 없음·LF·NFC·CR/NUL 없음 및 원천 줄 범위·HEAD·원천 파일 status를 별도 확인했다.

최종 검사기 마지막 출력 10줄:

```text
    L249-249 표 행 67  | 권한 코드=ACTUATOR_READ | 대상 작업=기본 설정의 민감 운영 지표 조회. 인증·허용된 접속 범위도 검사한다.
    L250-250 표 행 56  | 권한 코드=DEPLOY_ADMIN | 대상 작업=배포 시나리오 정의 검증·다시 읽기·목록 조회 API
    L251-251 표 행 87  | 권한 코드=COORDINATION_ADMIN_READ | 대상 작업=접두 /api/coordination/operations의 GET: 조정 작업 관리 조회
    L253-253 문단 54  | 이 코드는 메뉴 표시·화면 진입 코드가 아니다. 내부 운영 작업의 실행은 운영 담당자에게 맡긴다.
§ L255-265 [13] DLQ 삭제 API 권한   parent=156 children=2 sources=2
    L263-263 표 행 87  | 권한 코드=DLQ_DELETE | 허용 작업=Gateway·Business DLQ 항목 삭제 API | 기본 API 범위=접두 /api/dlq, DELETE
    L265-265 문단 68  | 이 코드는 「처리실패」 메뉴의 진입 권한과 다르다. DLQ 조회 코드는 GET에만 연결되므로 삭제 요청을 허용하지 않는다.
SUMMARY sections=17 children=53 maxChild=137 maxParent=311
CORPUS docs=48/53 sections=801 part={admin=13, user=35} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=10, 트러블슈팅=12}
TOTAL files=48 targets=1 violations=0
```

실행 수·실패 보존:

- 검사기 직접 호출 2회: 첫 호출은 실행 중 결과의 종료코드를 회수하지 못했으므로 성공으로 세지 않는다. 이어진 호출은 exit 1, 원천 줄 범위 위반 7건을 확인했다. 범위 밖 7건뿐 아니라 범위 안이지만 위치가 어긋난 catalog 참조도 실제 295줄 파일의 코드 위치로 모두 정정했다.
- 최종 로그 수집을 위한 Python 상대 bash 호출에서 UnicodeDecodeError(UTF-8에서 byte 0xa9), 결과 합성 TypeError가 났다. 이 호출은 검사 완료 근거로 쓰지 않는다. 실행 파일을 Git Bash 절대경로로 지정하고 stdout/stderr를 바이트로 회수한 뒤 UTF-8로 해독하여 위 최종 exit 0 로그를 얻었다.
- 최종 검사기 wall time: 23.079초. 전체 작성 wall time과 provider 사용량·비용은 미측정/미확인이다. 독립·하위 AI 호출은 0회다.
- 첫 초안의 한국어 접합 오류와 표 행의 비한국어 구두점은 마지막 검사 전 직접 정정했다. 미해소 사항은 없다.
- 제품 build·단위시험·서버 실행·네트워크 접속은 하지 않았다. 문서 형식과 정적 사실 대조만 검증했다.

사람 검사:

| 검사 축 | 판정·근거 |
|---|---|
| 제목 | 17개 전부 읽음. 동일 제목·불필요한 반복 제목 틀 없음. 기능·권한의 질문 범위가 제목에서 식별됨. |
| 사실·본문 중복 | 53개 본문 조각을 모두 원천과 대조함. USER_INFO_WRITE·USER_WRITE의 같은 범위는 별개 권한의 동일 매핑이라는 확인된 사실이며 코드가 각각 붙어 독립 행이 됨. 다른 문서와 동일 자식 본문 0건. |
| 빈 틀 문장·뜻풀이 | 코드 이름을 번역해 기능을 추정한 행 없음. 경로·메서드·사용처 또는 화면 가드로 모두 연결함. |
| 문장·마스킹 | 모든 문장을 다시 읽고 깨진 접합·잘린 문장·오답 인용 없음. mask-v1로 필요한 값 소실 없음. |
| 권한 경계 | user 본문에 admin objectKey·링크·관리 문서 열람 지시 없음. 서버 작업 코드는 권한의 대상만 설명하고 내부 운영 절차는 수록하지 않음. |

| 확인한 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·앞선 문서 중복 |
|---|---|---|---|---|
| 권한 자료의 출처와 API 허용 범위 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 모니터링 메뉴의 Live-Map과 처리실패 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 설비와 통신 방식별 모델 화면 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 배포와 사용자·그룹 메뉴 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 메뉴가 하나도 없는 계정의 진입 기준 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 설비·모델 변경과 비동기 결과 조회 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| Live-Map 배치 조회와 편집 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 현재 데모의 시나리오·진행 조회와 실행 제어 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 등록은 남아 있으나 현재 쓰지 않는 데모 권한 | 적합 | 적합 | 적합 | 등록과 현재 기능 없음 구분, 중복 없음 |
| 업로드한 데모 시나리오 삭제 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 배포 생성과 시나리오 사전 검토 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 배포 실행·보류·취소·재실행과 삭제 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 현재 계정 정보와 로그아웃 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 라인 선택 목록과 설비 통신 로그 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 사용자 변경과 그룹 소속 변경 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 그룹 관리와 권한 후보 목록 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| 메뉴 진입에 쓰이지 않는 서버 작업 권한 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |
| DLQ 삭제 API 권한 | 적합 | 적합 | 적합 | 코드별 사실 소유, 절차 복제 없음 |

검사에 사용한 임시 로그는 지정 work/final-check.log 하나다. 마지막 10줄·해시·판정·실측 시간을 이 기록에 보존한 뒤 정상 삭제 결과를 아래에 기록한다.
- 임시 로그 SHA-256: `e5d7ad06af75633538fdf5b2cb02d68a77ba32f6397ebb1da58bc43f1767c28d`

## 재개 후 최종 확인

- 2026-11-19 메인 세션의 중단 안내에 따라 검색 원문 265줄과 이 작성 기록 전체를 다시 읽었다. 검색 원문은 내용과 해시를 보존했다.
- 코드 재확인: 세 저장소 HEAD는 각각 f1d5e0a8e727·9fe69a75dcd3·985d17c604dd이며 본문 원천 37파일의 status 출력은 변경 없음이었다. 기본 권한 32개, V8, 메뉴 트리·라우터·한국어 문구·배치/삭제 화면 검사, 대응 API·추가 권한 검사·권한 일치 규칙을 다시 대조했다.
- 사실 대조 53행의 문장이 검색 원문의 해당 줄과 전부 일치한다. 문서 SHA·265줄·17절·30개 권한 코드 및 연결된 소유 문서 3개의 SHA도 일치한다.
- 작성 기록의 자동 대조 수량을 28개에서 실제 27개로 정정했다. 조정 조회 1개는 V8과 직접 대조한 행이며 카탈로그 자동 대조에 포함되지 않는다. 나머지 2개는 운영 지표 판단기·배포 시나리오 관리 컨트롤러로 직접 확인했다.
- 사람 검사: 17절 전부 다시 읽고 제목의 질문 식별·원천 일치·독립 답·중복 여부를 확인했다. 앞서 기록한 절별 판정은 유지하며 남은 지적은 없다.
- 최종 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/permission-code-reference.md`
- 실제 실행은 Git Bash 절대경로 `C:/Program Files/Git/usr/bin/bash.exe`를 사용했다. 검사기가 참조하는 저장소의 safe.directory는 자식 프로세스 환경에 각 정확한 경로로만 설정했다. 검사기·제품·전역 Git 설정은 수정하지 않았다.
- 재개 후 검사기 실행 1회, exit 0, 위반 0, CROSS VIOLATION 0, MASK 출력 0. sections=17, children=53, maxChild=137, maxParent=311. 다른 문서와의 중복 검사도 새 환경의 전체 49개 문서에 대해 수행됐다.
- 재개 후 독립·하위 AI 호출은 0회다. 제품 시험·build·서버 접속·업로드·Git add/commit/push는 수행하지 않았다.
- 재개 후 최종 검사 실측 wall time: 33.141초. 전체 작성 시간·provider 사용량·비용은 미확인이다.
- 재개 후 임시 로그: `C:/workspace_nori/nori-tc/rag-step3-work/doc-49-permission-code-reference/work/resume-final-check.log`, SHA-256 `300a784cbe42de53125d9bb2817c964177c07eea1ccdb6813a810ef1b8a81e3e`. 마지막 10줄과 판정을 이 기록에 보존한다.

새 경로 최종 검사기 마지막 출력 10줄:

```text
    L249-249 표 행 67  | 권한 코드=ACTUATOR_READ | 대상 작업=기본 설정의 민감 운영 지표 조회. 인증·허용된 접속 범위도 검사한다.
    L250-250 표 행 56  | 권한 코드=DEPLOY_ADMIN | 대상 작업=배포 시나리오 정의 검증·다시 읽기·목록 조회 API
    L251-251 표 행 87  | 권한 코드=COORDINATION_ADMIN_READ | 대상 작업=접두 /api/coordination/operations의 GET: 조정 작업 관리 조회
    L253-253 문단 54  | 이 코드는 메뉴 표시·화면 진입 코드가 아니다. 내부 운영 작업의 실행은 운영 담당자에게 맡긴다.
§ L255-265 [13] DLQ 삭제 API 권한   parent=156 children=2 sources=2
    L263-263 표 행 87  | 권한 코드=DLQ_DELETE | 허용 작업=Gateway·Business DLQ 항목 삭제 API | 기본 API 범위=접두 /api/dlq, DELETE
    L265-265 문단 68  | 이 코드는 「처리실패」 메뉴의 진입 권한과 다르다. DLQ 조회 코드는 GET에만 연결되므로 삭제 요청을 허용하지 않는다.
SUMMARY sections=17 children=53 maxChild=137 maxParent=311
CORPUS docs=49/53 sections=850 part={admin=14, user=35} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=11, 트러블슈팅=12}
TOTAL files=49 targets=1 violations=0
```

중단 전 정리 이력: CIM 프로세스 조회가 `Get-CimInstance : 액세스가 거부되었습니다.`, `HRESULT 0x80041003`으로 실패했다. 삭제 명령에 도달하기 전 조회 실패였으며 같은 CIM 호출을 반복하지 않았다. 구체적인 WMI 권한 원인은 미확인이다. 알려진 검사 프로세스 종료와 파일 독점 읽기·보호 속성을 확인한 뒤 비강제 native PowerShell Remove-Item이 exit 0으로 끝났고 당시 LogExists=False·WorkRemaining=0을 확인했다. 이후 메인 세션은 옛 작업 폴더가 모두 없어졌다고 안내했다. 외부 원천 확인을 위한 네트워크 접속은 하지 않았다.

- 재개 후 도구 판본: Temurin OpenJDK 21.0.9+10-LTS, GNU bash 5.2.37(1)-release (x86_64-pc-msys), Windows PowerShell 5.1.26100.9818, commonmark 0.32.0.
- 재개 후 임시 로그 정리 완료: 검사기 subprocess와 수집 명령의 exit 0을 회수하고, 정확한 허가 절대경로·세션 소유·용도·부모와 파일의 보호/reparse 속성·독점 읽기 가능 여부를 확인했다. 비강제 `Remove-Item -LiteralPath`가 exit 0으로 끝났다. `Test-Path` 결과 LogExists=False, 지정 work의 남은 파일 수 WorkRemaining=0이다. 미리 존재한 빈 work 디렉터리는 보존했다.

## 메인 검증 결함 2건 보완

- 허가 범위는 검색 원문과 작성 기록 두 파일이며 제품·검사기·다른 문서는 수정하지 않았다.
- 결함 1 원인: 저장소에 보관된 운영 권한 목록 캡처를 설치 기본 데이터로 잘못 해석했다. `nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L2~4`의 capturedAt·source·count를 다시 읽었다. 2026-09-14 운영 서버의 권한 목록 조회 캡처 32건이며 현재 현장 값이나 설치 시드 보장이 아니다.
- 캡처 사용처도 직접 확인했다: `nori-tc-exec/docs/scripts/check-manual-quotes.mjs @ f1d5e0a8e727 L41`은 매뉴얼 점검 입력이고, `docs/scripts/capture-manual-screens.mjs @ f1d5e0a8e727 L1217~1220`은 캡처를 읽어 화면 점검의 권한 목록으로 변환한다.
- `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-exec -C ../nori-tc-exec grep -n -E 'manual-permission-catalog|tc_ui_permission|INSERT' f1d5e0a8e727 -- docs/scripts/check-manual-quotes.mjs docs/scripts/capture-manual-screens.mjs db/migration`을 실행했다. V1~V8의 tc_ui_permission INSERT는 V8 L41뿐이고 V1은 테이블·제약 정의다. 나머지 전체 권한이 migration으로 설치된다고 설명하지 않는다. 문서·작성 기록에서 사용한 sample_data/patch는 참고 자료이며 전체 설치 migration으로 취급하지 않는다.
- 보완: 첫 절의 제목·출처·rag-meta를 캡처 32건과 V8의 COORDINATION_ADMIN_READ 등록으로 정정했다. 설치별 현재 등록은 「권한 그룹」의 「그룹 권한 관리」 후보나 GET /api/permission에서 확인하도록 적었다. 후보를 조회·표시하는 GroupPermissionsModal, group.api.ts와 서버 PermissionController를 직접 읽고 경로 status 변경 없음도 확인했다. 관련 표 머리글은 「기본 API 범위」에서 「등록 API 범위」로, 사용자 변경 절의 「기본 데이터」는 「권한 목록 캡처」로 맞췄다. 기존 코드·일치 방식·경로·메서드 값은 보존했다.
- 결함 2 원인: 현재 사용처가 없다는 이유로 등록된 권한 코드 자체를 누락했다. 세 코드를 캡처의 일치 방식·경로·메서드 그대로 별도 절에 복원하고, 확인한 판본에서는 부여해도 열리는 기능이 없으며 현재 데모는 RUN 계열을 사용함을 구분했다. 이 세 코드는 현재 omitted_facts 대상에서 제거했다.
- backend 재확인 명령: `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-backend -C ../nori-tc-ui-backend grep -n -E 'demo-jobs|demo-scenarios' 985d17c604dd -- ':!**/test/**' ':!**/*.md'`. stdout 0건, exit 1(검색 일치 없음).
- frontend 재확인 명령: `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-front -C ../nori-tc-ui-front grep -n -E 'demo-jobs|demo-scenarios|DEMO_JOB|DEMO_SCENARIO_READ' 9fe69a75dcd3 -- 'src/**'`. stdout 0건, exit 1(검색 일치 없음).
- 각 부재 주장은 위 저장소·HEAD·범위에 한정한다. 캡처 L160~177·L205~213의 등록과 현재 live-map-demo-run.api.ts L49~203·LiveMapController L33~122의 경로를 직접 대조했다. 메인 세션이 일치를 확인한 기존 권한 연결·일치 규칙·메뉴 필터·진입 검사·배치 보기/편집 조건은 변경하지 않았다.
- 검색 원문 변경 전 SHA-256: `20242b6adb5dda472830db3843a1366dd6d9d1c7655319f22f85d52d46523f40`.
- 검색 원문 변경 후 SHA-256: `4e892f173150b33bde8dd14bfa7c274d9dcf8d1f93bdd25182e192a0e05b246c`, 286줄, 18절, 58개 검색 조각, 권한 코드 33개. 현재 사실 대조 58행을 최종 원문 줄·문장과 전부 맞췄다. 캡처 32개 코드와 V8 1개가 표에 각각 한 번씩 있고 그중 30개 캡처 행의 resource·메서드는 자동 대조했다.
- 사람 검사: 전체 18절을 읽었다. 바뀐 출처·현재 등록 확인 문장은 관측 자료와 적용 범위를 구별하고, 새 데모 절은 등록과 현재 기능 없음에 독립적으로 답한다. 중복 절·틀 문장·근거 없는 이름 풀이·오답·관리 문서 열람 지시·미해소 마스킹은 없다.
- 최종 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/permission-code-reference.md`. Git Bash 절대경로로 실행했고 정확한 저장소 safe.directory만 자식 환경에 전달했다. 검사기 실행 1회, exit 0, 위반 0, CROSS VIOLATION 0, MASK 출력 0. sections=18, children=58, maxChild=153, maxParent=412.
- 검사 실측 wall time: 26.187초. 독립·하위 AI 호출 0회. 전체 보완 시간·provider 사용량·비용은 미확인이다.
- 임시 로그: `C:/workspace_nori/nori-tc/rag-step3-work/doc-49-permission-code-reference/work/repair-final-check.log`, SHA-256 `85b392cd8f727bd20b7cca375242662900c6654a63eb3e6a8e5cfbf733d911d7`. 아래 마지막 출력·판정·시간을 보존한 뒤 정리한다.

최종 검사기 마지막 출력 10줄:

```text
    L270-270 표 행 67  | 권한 코드=ACTUATOR_READ | 대상 작업=기본 설정의 민감 운영 지표 조회. 인증·허용된 접속 범위도 검사한다.
    L271-271 표 행 56  | 권한 코드=DEPLOY_ADMIN | 대상 작업=배포 시나리오 정의 검증·다시 읽기·목록 조회 API
    L272-272 표 행 87  | 권한 코드=COORDINATION_ADMIN_READ | 대상 작업=접두 /api/coordination/operations의 GET: 조정 작업 관리 조회
    L274-274 문단 54  | 이 코드는 메뉴 표시·화면 진입 코드가 아니다. 내부 운영 작업의 실행은 운영 담당자에게 맡긴다.
§ L276-286 [13] DLQ 삭제 API 권한   parent=156 children=2 sources=2
    L284-284 표 행 87  | 권한 코드=DLQ_DELETE | 허용 작업=Gateway·Business DLQ 항목 삭제 API | 등록 API 범위=접두 /api/dlq, DELETE
    L286-286 문단 68  | 이 코드는 「처리실패」 메뉴의 진입 권한과 다르다. DLQ 조회 코드는 GET에만 연결되므로 삭제 요청을 허용하지 않는다.
SUMMARY sections=18 children=58 maxChild=153 maxParent=412
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

- 보완 임시 로그 정리 완료: 검사기·수집 프로세스의 exit 0을 회수하고, 허가된 정확한 절대경로·이 세션 소유·부모와 파일의 보호/reparse 속성·독점 읽기 가능 여부를 확인했다. 비강제 native PowerShell `Remove-Item -LiteralPath`가 exit 0으로 끝났다. `Test-Path` 결과 LogExists=False다. WorkRemaining=1이며 남은 파일은 `C:/workspace_nori/nori-tc/rag-step3-work/doc-49-permission-code-reference/work/catalog.json`(7763바이트)이다. 이 세션이 만들지 않은 파일이므로 보존했다. 이 세션이 만든 보완 임시 파일의 잔여는 없다.

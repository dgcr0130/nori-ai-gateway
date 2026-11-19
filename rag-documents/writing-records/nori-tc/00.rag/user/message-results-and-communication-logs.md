# 메시지 전송 결과·통신 로그 조회 작성 기록

## 대상

- objectKey: `00.rag/user/message-results-and-communication-logs.md`
- 검색 원문: `rag-documents/nori-tc/00.rag/user/message-results-and-communication-logs.md`
- 구획·열람 권한: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문 SHA-256: `a502f04c22c26ebaceb4678ca68c13d4886c66a4eafd7b9b05c35c187c9b78a5`
- 최종 줄 수: 139. H2 절 수: 7. 직렬화 자식 수: 22.
- 확인일: 2026-11-18, 한국 시간 기준.
- 수행 범위: 독립 작성 세션이 직접 조사·작성·검사했다. 재위임·별도 AI 호출은 0회다. 업로드·제품 변경·Git add/commit/push는 하지 않았다.

## 원천 판본

| 저장소 | 확인한 현재 HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 원문 메타의 12개 고유 파일과 추가 확인 파일에 대해 status --short 출력 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 원문 메타의 6개 고유 파일과 추가 확인 파일에 대해 status --short 출력 없음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 원문 메타의 3개 고유 파일과 권한 시드 파일에 대해 status --short 출력 없음, exit 0 |
| nori-tc-gateway | d3636ab86763 | 원문 메타의 CommLogPort.java 및 조사한 gateway 소스 범위에서 status --short 출력 없음, exit 0 |
| nori-tc-business | 7db24f7466bd | 원문 메타의 CommLogPort.java 및 조사한 business 소스 범위에서 status --short 출력 없음, exit 0 |

판본은 `git -c safe.directory=<저장소 절대경로> -C <저장소> rev-parse --short=12 HEAD`로 확인했다. 경로별 상태는 `git -c safe.directory=<저장소 절대경로> -C <저장소> status --short -- <원천 파일 경로들>`로 묶어서 확인했다. 첫 Git 조회는 형제 저장소 소유자 검사로 실패했으며 이후 읽기 명령에만 해당 저장소의 safe.directory를 명시했다. 전역 설정을 바꾸지 않았다. Git은 사용자 전역 ignore 파일 접근 거부 경고를 냈지만, 원천 파일 상태 조회는 exit 0이었고 변경 경로는 없었다.

메타에 인용한 파일은 다음과 같다. 같은 파일을 여러 절에서 인용해도 한 번 센다.

- nori-tc-ui-front: `src/shared/lib/app-navigation.ts`, `src/features/deploy/components/DeployPage.tsx`, `src/features/deploy/components/DeployMonitorPanel.tsx`, `src/features/deploy/components/DeployMonitorViewModal.tsx`, `src/features/deploy/lib/deploy-utils.ts`, `src/features/deploy/api/deploy.api.ts`, `src/shared/lib/resolve-error-message.ts`, `src/features/live-map/components/LiveMapCommLogPanel.tsx`, `src/features/live-map/hooks/useCommLog.ts`, `src/features/live-map/api/comm-log.api.ts`, `src/components/ui/dialog.tsx`, `src/i18n/locales/ko.json`.
- nori-tc-ui-backend: `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java`, `ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java`, `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java`, `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java`, `ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/CommLogQueryService.java`, `ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java`。
- nori-tc-exec: `docs/completion-report/07-user-manual.md`, `docs/scripts/manual-permission-catalog.json`, `docs/db_table/patch/postgres_2026_05_07_add_comm_log_api_permission.sql`。
- nori-tc-gateway: `gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/port/CommLogPort.java`。
- nori-tc-business: `business/tc-business-core/src/main/java/com/nori/tc/business/core/messaging/CommLogPort.java`。

작성 규칙 `docs/rag-document-writing-rules.md`와 53개 문서 목록 `docs/tc-rag-document-list.md`는 전체를 읽었다. PowerShell 기본 인코딩으로 깨져 보인 초기 출력을 근거로 쓰지 않고 UTF-8을 지정해 다시 읽었다. rg가 설치되어 있지 않아 이후 검색은 git grep과 필요한 경로의 Select-String으로 수행했다.

추가 호출 흐름 확인:

- nori-tc-ui-front `src/app/Router.tsx` L82~105: Live-Map·배포 진입 권한. `src/shared/lib/app-navigation.ts` L114~124와 ko.json L1102: 배포 메뉴.
- nori-tc-ui-backend `UiSecurityConfig.java` L115~143: 공개 요청 외에는 DB 권한 목록으로 인가. `UiApiPermissionCache.java` L142~174: 일치 권한이 없으면 차단, 일치한 권한 중 보유한 것이 있어야 허용.
- nori-tc-exec `docs/db_table/sample_data/postgres_insert_sample_data.sql` L780: DEPLOY_READ 기본 GET 연결. 통신 로그 패치 L26~39와 catalog L42~50은 COMM_LOG_READ 연결이 같다. 실제 현장 DB나 계정의 권한은 조회하지 않았다.
- nori-tc-ui-backend `DeployService.java` L224~236·L382~397·L410~436: 실제 배포 존재 확인, 목록 조건 전달, 대상 조회의 page·size·상태 검사. 화면은 조회값과 대상 쪽 크기를 자동 구성하며 본문에서 생성 요청 DTO 계약을 끌어오지 않았다.
- nori-tc-gateway `GatewayCommLogRedisAdapter.java` L24~76: EQP 출처, 기록 시각, 기간·건수 제한. `GatewayCommandDispatcher.java` L254~268: SEND 기록은 송신 큐 투입 뒤 기록되므로 통신 로그의 SEND만으로 설비 처리 성공을 설명하지 않았다.
- nori-tc-business `BusinessCommLogRedisAdapter.java` L43~76, `BusinessWmEventKafkaSubscriber.java` L68~75: 같은 업무 통신 기록에 WM 수신도 들어간다. MES 출처 코드를 MES에서 온 기록만이라고 단정하는 뜻풀이를 하지 않았다.
- nori-tc-ui-backend `DeployControllerTest.java` L200~253·L256~300의 기대값을 읽었다. 기간·terminalOnly 전달과 기본 비종료 조회를 확인했다. 테스트를 실행한 것은 아니다.
- nori-tc-ui-front `tests/e2e/modal-visual-sweep-livemap.spec.ts` L35~77은 통신 로그를 빈 배열로 모의 응답하고 데모 창을 검사한다. 통신 로그 기능의 실측 성공 증거로 재사용하지 않았다.

## 절별 사실 대조

### 메시지 전송 기록을 배포 목록에서 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 27, 첫 문장 | 새 메시지 전송 전용 화면 단추 없음 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1106~1111; nori-tc-ui-front/src/features/deploy/lib/deploy-utils.ts @ 9fe69a75dcd3 L65~102 | src 전체의 CommLog·EQP_SEND_MESSAGE 검색 및 app·eqp·deploy의 send-message 변형 검색을 대조했다. 메시지 전송은 작업명 표시에서 사용하며 별도 전송 입력 UI는 찾지 못했다. |
| 27, 둘째 문장 | 기존 전송 결과를 배포 목록에서 찾음 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1111~1122; nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 L307~321 | 목록 Event Type과 View 진입이 실제 렌더된다. |
| 27, 셋째 문장 | 기본 권한 DEPLOY_READ | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L87~95; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | GET /api/deploys 하위 경로와 기본 권한 매칭을 확인했다. 현장 부여를 단정하지 않았다. |
| 29 | 배포 메뉴와 진행 중·종료 카드 선택 | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L114~124; nori-tc-ui-front/src/features/deploy/components/DeployPage.tsx @ 9fe69a75dcd3 L13~53; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd L337~348 | 메뉴 경로와 카드의 실제 패널, 종료 여부별 목록 조건을 대조했다. 한국어는 ko.json L154~155·L1102와 같다. |
| 30, 첫 문장 | 기간 칸과 조회 조작 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 L712~783; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L148~155; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L172~179 | 카드별 조회 제목, 날짜 입력의 aria-label, 조회 단추를 확인했다. |
| 30, 둘째 문장 | 기간은 선택 사항·생성일 기준 | nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 L740~758; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L135~158; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd L322~328 | 빈 날짜는 전송하지 않고 서버도 필수로 요구하지 않는다. 생성 시각 조건으로 비교한다. |
| 31 | Event Type EQP_SEND_MESSAGE 식별 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 L307~321; nori-tc-ui-front/src/features/deploy/lib/deploy-utils.ts @ 9fe69a75dcd3 L96~97 | 목록은 eventType 원문을 표시하고 작업명 연결은 메시지 전송이다. |

### 메시지 전송 작업의 대상별 결과 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 46 | 전송 줄의 오른쪽 클릭 View | nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 L923~1002 | 각 서버 상태·이력의 메뉴가 상세 열기를 연결한다. |
| 47, 첫 문장 | Deploy View의 대상 설비 찾기 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~234; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L329~375 | 창 제목·대상별 진행 현황·EQP ID 열을 확인했다. |
| 47, 둘째 문장 | 작업의 굵은 글씨 메시지 전송 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L153~159; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L237~245; nori-tc-ui-front/src/features/deploy/lib/deploy-utils.ts @ 9fe69a75dcd3 L96~97; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L333~333 | EQP_SEND_MESSAGE가 번역 키로 연결되고 굵은 첫 줄에 렌더된다. |
| 48, 첫 문장 | 결과·상세 상태·오류 요약 읽기 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L249~303; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L295~300 | 표시된 대상 행의 결과 열을 확인했다. 상태 코드의 정의를 복제하지 않았다. |
| 48, 둘째 문장 | 현재 Step·Step 상태·갱신 시각 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L269~315; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L298~301 | 실제 열과 표시 순서를 확인했다. |
| 50, 첫 문장 | 실패 문의에 배포 번호·설비·오류·갱신 시각 전달 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~315; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L333~338 | 화면에서 확인 가능한 값으로 인계 항목을 제한한 안내다. 내부 서버 접근을 요구하지 않는다. |
| 50, 둘째 문장 | 공통 결과 설명의 기존 소유 문서 안내 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/deployment-workflow.md @ sha256=a2b18e6614520bd24689f10899d457471a3e8f2bf55db31d6f0978021894f804 L272~284 | 「대상별 진행 현황에서 오류 설비와 단계를 찾기」가 공통 상태·열 설명을 소유한다. 동작 근거는 위 렌더 코드이며 기존 문서는 소유권 확인에만 썼다. |

### 메시지 전송 기록이 없거나 조회가 거부됐을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 68, 첫 문장 | 전송 줄이 없으면 범위 확대·양쪽 카드 조회 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 L472~494; nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 L712~783; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1140~1145 | 필터를 풀고 다른 완료 구분에서 찾는 사용 절차다. 재전송을 권하지 않는다. |
| 68, 둘째 문장 | 카드별 빈 목록 문구 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 L912~915; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L190~191 | 행 수 0일 때 두 문구를 분기한다. |
| 70, 첫 문장 | 역전된 기간은 서버 거부 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L145~150 | toDate가 fromDate 이전이면 UiBadRequestException이다. |
| 70, 둘째 문장 | 권한 거부일 때 DEPLOY_READ 확인 요청 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L87~95; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 입력 거부와 기본 API 권한 거부를 구별한다. |
| 72, 첫 문장 | 대상 상세 조회 실패 후 닫고 같은 전송 View 재조회 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L119~124; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L463~479; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L270~271; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1144~1145 | 대체 문구와 닫기 단추, 매뉴얼의 재조회 절차를 대조했다. |
| 72, 둘째 문장 | 서버 오류 문구 우선 | nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12; nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40 | 정규화된 서버 문구가 Error.message로 전달되어 표시된다. |
| 72, 셋째 문장 | 대상 정보가 없으면 다른 전송 기록 선택 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L357~364; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L294~294; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1145~1145 | 대상 배열이 빈 경우의 표시이며 매뉴얼의 선택 변경 조치와 같다. |

### 통신 로그의 조회 범위와 자동 갱신 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 88 | Live-Map 진입 경로는 기존 문서에 한 문장으로 연결 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~294 | 더보기가 목록 상태를 열고 같은 로그 배열을 넘긴다. 기존 live-map-status.md의 소유 범위를 유지한다. |
| 90, 첫 문장 | 선택 설비의 설비·업무 통신 병합 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~215; nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 L20~34; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd L35~45; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/CommLogQueryService.java @ 985d17c604dd L50~55 | 화면은 all로 요청하고 서비스가 두 조회 결과를 합친다. |
| 90, 둘째 문장 | 기본 로그 조회 권한 COMM_LOG_READ | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L42~50; nori-tc-exec/docs/db_table/patch/postgres_2026_05_07_add_comm_log_api_permission.sql @ f1d5e0a8e727 L26~39 | GET /api/comm-logs 하위 경로에 연결한다. |
| 92, 첫 문장 | 최근 1시간은 기본 설정이며 변경 가능 | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java @ 985d17c604dd L46~47; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java @ 985d17c604dd L67~73 | 설정값의 기본 1과 조회 하한 계산을 확인했다. 현장 설정을 확인한 것으로 쓰지 않았다. |
| 92, 둘째 문장 | 화면에 기간·출처 입력 없음 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L87~148; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~294 | 목록과 부모의 렌더 전체에 필터 입력이 없고 all 호출로 고정된다. API의 type 인자를 UI 선택 기능으로 쓰지 않았다. |
| 92, 셋째 문장 | 10초 자동 조회 | nori-tc-ui-front/src/features/live-map/hooks/useCommLog.ts @ 9fe69a75dcd3 L4~11 | refetchInterval 10000을 확인했다. |

### 통신 로그 목록에서 시각과 메시지 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 105, 첫 문장 | 제목 옆 설비 ID·건수 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L119~125; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L418~422 | eqpId와 logs.length 및 건 단위를 렌더한다. |
| 105, 둘째 문장 | 시간 오름차순·열기 및 건수 변경 시 최신으로 이동 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/CommLogQueryService.java @ 985d17c604dd L50~55; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L103~113 | timestampMs 정렬과 open·logs.length 의존 스크롤을 대조했다. |
| 107, 첫 문장 | 행의 시각·방향·출처·메시지명·내용 순서 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L33~39; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L53~83 | 시간 포맷과 다섯 표시 영역 순서를 확인했다. |
| 107, 둘째 문장 | RECV 수신·SEND 송신 | nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/port/CommLogPort.java @ d3636ab86763 L11~24; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/messaging/CommLogPort.java @ 7db24f7466bd L11~24 | Gateway·Business 기준의 방향 계약이다. 상대 설비 처리 성공을 의미한다고 쓰지 않았다. |
| 107, 셋째 문장 | EQP·MES 출처 코드 | nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 L6~12; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L75~82 | 타입의 두 코드와 그대로 표시하는 렌더를 대조했다. |
| 107, 넷째 문장 | 메시지명 없음의 표시 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L76~82 | messageName null이면 하이픈을 표시한다. |
| 109, 첫 문장 | 잘린 내용은 행 눌러 상세 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L62~83; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L282~294 | truncate된 행이 선택 상세로 연결된다. |
| 109, 둘째 문장 | 위로 스크롤하여 과거 기록 확인 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L103~145 | 최신은 아래이며 목록 컨테이너는 overflow-auto다. |

### 통신 로그 한 건의 전체 내용 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 121, 첫 문장 | 목록 행·최근 알림 개별 항목의 상세 진입 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L136~141; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L260~294 | 두 조작 모두 selectedEntry로 같은 상세를 연다. 별도 목록 진입 경로를 새로 만들지 않았다. |
| 121, 둘째 문장 | 상세 방향·출처·메시지명·eqpId·time·epoch | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L167~195 | 표시 명칭과 위치를 그대로 적었다. |
| 123, 첫 문장 | JSON 들여쓰기·그 외 원문 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L42~50 | JSON.parse 성공 시 stringify 2칸, 실패 시 원문을 돌려준다. |
| 123, 둘째 문장 | 빈 내용의 표시 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L197~202; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L423~423 | formatPayload 결과가 빈 문자열이면 payloadEmpty를 렌더한다. |
| 123, 셋째 문장 | 긴 상세 본문의 스크롤 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L164~202 | 최대 높이와 본문 overflow-auto를 확인했다. |
| 125, 첫 문장 | 오른쪽 위 X 닫기 | nori-tc-ui-front/src/components/ui/dialog.tsx @ 9fe69a75dcd3 L79~85; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L473~473 | 공통 DialogContent가 기본으로 X와 한국어 닫기 aria-label을 제공한다. |
| 125, 둘째 문장 | 통신 로그 인계 항목 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L167~202 | 화면에서 직접 읽을 수 있는 값과 증상만 전달하도록 안내했다. |

### 통신 로그 목록이 비어 있을 때 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 137, 첫 문장 | 빈 문구만으로 기록 부재·조회 실패 구별 불가 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L129~145; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~215; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java @ 985d17c604dd L67~97; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L420~420 | UI는 오류를 구독해 표시하지 않고 기본 배열을 쓰며 서버 저장소 조회 예외도 빈 목록으로 반환한다. |
| 137, 둘째 문장 | 오류 전용 안내·재시도 단추 없음과 자동 조회의 한계 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L87~148; nori-tc-ui-front/src/features/live-map/hooks/useCommLog.ts @ 9fe69a75dcd3 L4~11 | 목록의 전체 렌더에 오류·재시도 영역이 없다. 자동 재조회가 복구를 보장하지 않는다. |
| 139 | 빈 목록 지속 시 권한 확인·시각과 증상 인계 | nori-tc-exec/docs/db_table/patch/postgres_2026_05_07_add_comm_log_api_permission.sql @ f1d5e0a8e727 L26~39; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L119~145 | COMM_LOG_READ가 필요하지만 화면에서 원인을 구분하지 못하므로 권한 확인과 실제 관찰 정보를 요청한다. 권한 부재라고 단정하지 않았다. |

## 매뉴얼과 코드가 다른 곳

채택한 U §4.14의 조회 절차·표시 문구와 코드 사이에서 본문을 달리 써야 할 충돌은 발견하지 않았다. 통신 로그의 기간·상세 화면은 매뉴얼의 메시지 결과 절에 없는 내용이므로 실제 코드를 직접 근거로 추가했다. 매뉴얼이 통신 로그 기간 선택을 제공한다고 주장하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

목록의 질문에 답하는 데 필요한 사실 중 근거 부재로 남긴 항목은 없다. 사용자 결정 항목도 없다.

- Live-Map 외 별도 통신 로그 메뉴나 임의 기간·출처 검색 절차는 넣지 않았다. src 전체 검색에서 실제 사용은 LiveMapCommLogPanel 및 그 상세뿐이었고, API의 type 선택은 화면에 연결되지 않는다. 새로운 경로를 발명하지 않았다.
- 현장 배포 판본·로그 보관 설정·계정별 권한·실제 로그 유무는 조회하지 않았다. 본문은 확인한 소스와 기본 권한 데이터·기본 설정으로 한정한다. 현장 문의의 확인 역할은 운영 관리자·운영 담당자다.
- 로그 한 건의 SEND를 실제 설비 처리 완료라고 설명하지 않았다. 송신 큐 투입 시 기록되는 경로를 읽었으며 실제 메시지 처리 결과는 대상별 결과 화면에서 읽도록 구분했다.
- 제품·브라우저를 실행하지 않았으며 실측 화면 성공이나 현장 정상 동작을 주장하지 않는다.

## 다른 문서가 소유해 넣지 않은 사실

| 문서 objectKey | 보존한 소유 범위와 이번 문서의 최소 사용 |
|---|---|
| 00.rag/user/live-map-status.md | 설비 선택·최근 알림·더보기의 목록 진입 절차는 한 문장으로만 가리킨다. 열린 목록·상세·조회 범위는 이번 문서가 다룬다. |
| 00.rag/user/deployment-workflow.md | 생성·실행·Hold·삭제·Redeploy, 기본 필터값·쪽 이동·결과 상태 뜻·AS-IS/TO-BE를 반복하지 않았다. 전송 기록을 찾는 데 필요한 카드·기간·Event Type·View·결과 열·빈 목록/상세 실패의 최소 조건만 사용했다. |
| 00.rag/user/equipment-commands-and-polling.md | Start·End·Restart·Reconnect와 폴링 절차, 그 결과 대화상자를 다시 설명하지 않았다. |
| 00.rag/user/login-and-screen-basics.md | 공통 메뉴·표 조작과 명령 진행/결과 대화상자는 제외했다. |
| 00.rag/user/permission-code-reference.md | DEPLOY_READ·COMM_LOG_READ의 권한 체계·부여 절차·전체 뜻풀이는 넣지 않았다. 화면 조회의 필요한 조건만 사용했다. |
| 00.rag/user/state-reference.md | 배포·대상·작업 상태의 정의와 상태 전이표를 만들지 않았다. |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드 사전은 만들지 않고 조회 중 마주치는 문구의 최소 조작만 적었다. |
| 00.rag/admin/daily-checks-and-log-inspection.md | 사용자가 이동한 O §8.1의 서버 로그 조회는 전부 제외했다. 검색 원문에 admin objectKey나 관리 문서 열람 지시는 없다. |

관련 기존 문서로 live-map-status.md·deployment-workflow.md·equipment-commands-and-polling.md·login-and-screen-basics.md를 읽었고, equipment-registration-and-settings.md의 제목·통신 로그 관련 검색 결과도 대조했다. 기존 문서는 수정하지 않았다.

## 검사 결과

실행 명령(PowerShell에서 Git Bash를 직접 호출):

```powershell
& 'C:/Program Files/Git/usr/bin/bash.exe' 'C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh' --dump '00.rag/user/message-results-and-communication-logs.md'
```

- Git Bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS.
- 검사기 호출 2회, 각각 exit 0. 1차는 위반 0건 확인 후 원천 메타의 메뉴·한국어 표시 위치만 보강했다. 본문 변경 없이 마지막 편집 뒤 다시 실행했다.
- 마지막 검사 명령의 Stopwatch 실측 wall time: 10,295ms. 추가 AI 호출·provider 비용은 발생량 자료가 제공되지 않아 측정하지 않았다.
- 최종 결과: 절 7개, 자식 22개, 최대 자식 132자, 최대 부모 338자. violations=0, CROSS VIOLATION·MASK 출력 없음.
- 제품 코드 변경이 없는 문서 작업이므로 제품 빌드·테스트·브라우저 실행은 하지 않았다. 기계 검사 성공을 제품 동작 시험 성공으로 바꾸지 않았다.

마지막 실행 출력의 끝 10줄(도구가 표시한 LF/CRLF 차이 외 문구 그대로):

```text
    L121-121 문단 101  | 통신 로그 목록의 행이나 「최근 알림」의 개별 기록을 누르면 한 건의 상세가 열린다. 상세 위쪽에는 방향·출처·메시지명이 나오고, 아래에는 eqpId·time·epoch가 표시된다.
    L123-123 문단 99  | 본문이 JSON이면 들여쓰기를 넣어 표시하고, JSON이 아니면 원문을 표시한다. 내용이 비어 있으면 「(payload 없음)」이 나온다. 긴 내용은 상세 안을 스크롤해 읽는다.
    L125-125 문단 91  | 상세 오른쪽 위 X 모양의 「닫기」로 상세를 닫는다. 통신 로그 문의에는 설비 ID·time·epoch·방향·출처·메시지명과 화면에서 확인한 증상을 함께 전달한다.
§ L127-139 [22] 통신 로그 목록이 비어 있을 때 확인하기   parent=231 children=2 sources=6
    L137-137 문단 127  | 통신 로그 목록에 「최근 1시간 내 로그가 없습니다.」가 나오더라도 기록 부재와 조회 실패를 이 화면에서 구별할 수는 없다. 목록에는 오류 전용 안내나 「재시도」 단추가 없으며, 다음 자동 조회에서도 빈 목록이 계속될 수 있다.
    L139-139 문단 103  | 기록이 예상되는데 목록이 계속 비면 운영 관리자에게 COMM_LOG_READ 부여 여부를 확인하고, 운영 담당자에게 대상 설비 ID·통신이 있었던 시각·조회 시각·표시 문구를 전달한다.
SUMMARY sections=7 children=22 maxChild=132 maxParent=338
CORPUS docs=13/53 sections=237 part={user=13} type={사용 절차=13}
TOTAL files=13 targets=1 violations=0
CHECK_WALL_MS=10295
```

사람 검사는 전체 7개 절을 대상으로 했다.

| 절 제목 | 제목만으로 질문을 아는가 | 원천 일치 | 독립 답 | 다른 절·기존 문서와의 경계 |
|---|---|---|---|---|
| 메시지 전송 기록을 배포 목록에서 찾기 | 적합 | 일치 | 전송 기록 식별·권한·경로·조작을 포함 | 배포 공통 기능 중 조회의 최소 조건만 사용 |
| 메시지 전송 작업의 대상별 결과 확인하기 | 적합 | 일치 | EQP_SEND_MESSAGE부터 결과·인계까지 연결 | 상태 정의와 공통 상세 설명은 기존 배포 문서에 남김 |
| 메시지 전송 기록이 없거나 조회가 거부됐을 때 | 적합 | 일치 | 빈 목록·기간 거부·권한·상세 실패를 구별 | 메시지 전송 줄을 찾는 최소 실패 처리만 사용 |
| 통신 로그의 조회 범위와 자동 갱신 확인하기 | 적합 | 일치 | 선택 설비·필요 권한·기간·갱신 주기를 포함 | Live-Map 진입 절차는 한 문장으로만 가리킴 |
| 통신 로그 목록에서 시각과 메시지 구별하기 | 적합 | 일치 | 설비·건수·순서·행 내용·상세 열기를 설명 | 목록 읽기만 소유, 상세 내용은 다음 절이 소유 |
| 통신 로그 한 건의 전체 내용 읽기 | 적합 | 일치 | 두 상세 진입·본문·닫기·인계 값을 설명 | 최근 알림 목록 진입과 공통 표 조작을 반복하지 않음 |
| 통신 로그 목록이 비어 있을 때 확인하기 | 적합 | 일치 | 열린 목록의 한계와 넘길 정보를 설명 | Live-Map 요약 영역의 상태 판단 대신 열린 목록의 오류·재시도 부재를 설명 |

제목 정확 중복·불필요한 제목 틀·이름 토막 뜻풀이·정보 없는 틀 문장·본문 복제·잘린 문장·오답 사례·필요 값의 마스킹·관리 조치 혼입을 전수 확인했다. 미해소 지적은 0건이다. 단순 코드명으로 상대방의 업무 의미를 추정하지 않았다. 번호 목록은 원문 진행 번호를 1·2·3으로 적었다. 문단과 목록 자식마다 대상·조건·조작 또는 결과가 있다.

새 검사용 임시 파일·스크립트는 만들지 않았고 서비스·브라우저도 실행하지 않았다. 제공된 검사기만 실행했다. 지정 work 경로는 최종 확인 시 비어 있으며 이 세션이 만든 자원이 없어 삭제할 대상도 없다. 보존 산출물은 요청된 검색 원문과 이 작성 기록 두 파일이다.

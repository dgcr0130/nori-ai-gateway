# 데모 트러블슈팅 작성 기록

## 대상

- objectKey: `00.rag/user/demo-troubleshooting.md`
- 구획·필요 권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/demo-troubleshooting.md`
- 검색 원문 SHA-256: `bdea861a8f902d6fb4cb2b203b0b8df4ef2fc484d554617781f767cd89fd2d22`
- 최종 원문: 12,861바이트, 121줄, H1 1개, H2 8개, 본문 조각 23개
- 확인일: 2026-11-19, 한국 시간
- 범위: 실행 생성 응답 지연, 비동기 연결·MES 활성화 실패, Socket 명령 대기, 수정 시나리오의 이벤트 대기 위치, 오류 사이클, 실행 점유와 고아 워커의 포트 충돌 구별
- 이 세션은 직접 조사·작성했다. 독립·하위 Agent 호출, 제품 실행·시험, 서버 접속, 업로드, Git staging·commit·push는 하지 않았다.

## 원천 판본

Git 2.52.0.windows.1에서 저장소별 현재 HEAD를 확인했다. 아래 경로 범위를 `git -c safe.directory=<해당 저장소 절대경로> -C <저장소> status --short -- <경로>`로 확인했으며 파일 변경 행이 없었다. 모든 인용은 작업 트리 수정본이 아닌 아래 HEAD의 줄 번호이다.

| 저장소 | HEAD 12자리 | 작업 트리 확인 범위·결과 |
|---|---|---|
| nori-tc-simulator | 5e9b02e3ae16 | orchestrator/src, runtime/src, scenario-engine/src, worker-equipment/src, worker-upstream/src: 변경 행 없음 |
| nori-tc-ui-backend | 985d17c604dd | ui/adapter/tc-ui-web-adapter/src, ui/tc-ui-core/src: 변경 행 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | src/features/live-map, src/shared/lib/api-error.ts, src/i18n/locales/ko.json: 변경 행 없음 |
| nori-tc-exec | f1d5e0a8e727 | docs/scripts/manual-permission-catalog.json, docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql: 변경 행 없음 |

- 최초 형제 저장소 Git 조회는 `fatal: detected dubious ownership in repository`로 거부됐다. 전역 설정은 변경하지 않고 이후 읽기 전용 Git 명령에 해당 저장소의 `-c safe.directory=...`만 지정했다. status에는 사용자 전역 ignore 파일 접근 경고가 있었으나 종료코드 0이고 대상 파일 변경 행은 없었다.
- `rg`는 설치 경로에서 실행할 수 없어 이후 제품 코드 검색은 `git grep`·`git ls-files`와 PowerShell로 수행했다.
- 수정 원천 `42b3791f382f17bac0b9734d443c4c86ccb3c1eb`, `223220e2f51c6e4894f2854930a65c30ebf341d8`, `050c5f6b9306410acdfd76d13b22b49eac033a5c`의 diff를 읽고 해당 변경의 현재 HEAD 구현·관련 시험 원문을 대조했다. 커밋 메시지의 과거 성능 수치나 당시 시험 PASS는 이번 검증 결과로 사용하지 않았다.
- 작성 규칙 `docs/rag-document-writing-rules.md`와 목록 `docs/tc-rag-document-list.md`를 전체 읽었다. 기존 `demo-execution.md`, `socket-troubleshooting.md`, `equipment-connection-and-command-troubleshooting.md`, `live-map-troubleshooting.md`를 읽고 소유 범위를 대조했다. 통신 로그 문서와 로그인·권한 문서의 관련 절도 확인했다.

## 절별 사실 대조

같은 검색 원문 줄에 여러 문장이 있으면 문장별로 행을 나눴다. 본문 51문장을 51행에 대응시켰다. 인계 조언은 새 제품 기능으로 쓰지 않고 실제 확인 가능한 값과 진단 경로에 연결했다.

### 테스트 시작이 시간 초과됐는데 실행이 남았는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 18, 문장 1 | 시간 초과 뒤 재시도 전 진행 현황 갱신 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L198~208 L791~805 | 새로고침이 서버 실행 목록을 다시 읽으므로 남은 실행을 확인하는 조치로 제시했다. |
| 18, 문장 2 | 시각·시나리오·대상 일치 확인 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L917~925 L943~975 | 실행 번호·시나리오·시작 시각과 펼친 설비 행이 있다. |
| 18, 문장 3 | 응답 전에 실행 생성·설비 확보 가능 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L115~138; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd L92~109 | 서버는 설비 점유와 실행 생성을 먼저 수행한다. 백엔드 추적은 생성 응답 뒤이다. 실패 응답만으로 실행 부재를 확정하지 않았다. |
| 20, 문장 1 | 남은 실행 식별·불필요 여부 확인 뒤 종료 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L904~975 | 실행별·설비별 종료 기능이 있다. 다른 정상 실행을 임의 종료하도록 쓰지 않았다. |
| 20, 문장 2 | 목록 부재·종료 불가 시 인계 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/RunController.java @ 5e9b02e3ae16 L33~55; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L338~370 | 목록은 제한된 최근 이력이며 종료 실패도 가능하다. 목록에 없다고 실행 부재를 확정하지 않고 요청 증거를 넘기게 했다. |
| 20, 문장 3 | 갱신·종료 절차 소유 문서 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L344~378 L791~805 | 실제 조작과 기존 demo-execution.md의 갱신·종료 절을 대조하고 절차 재수록을 피했다. |
| 22, 문장 1 | 050c5f6b 이후 생성 대기 시간 분리 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd L46~67 L115~128 | createRun만 전용 대기 시간, 조회·종료는 공통 읽기 대기 시간을 사용한다. 해당 diff와 일치한다. |
| 22, 문장 2 | 현장 수정 포함·생성 응답 지연 확인 인계 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd L46~52 L144~148 | 수정으로 모든 지연·고아 실행이 사라진다고 보장하지 않고, 현장 판본과 응답 경로를 확인하는 요청으로 썼다. |

### 시작 응답 뒤 직접 종료하지 않았는데 중지됐을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 32, 문장 1 | 시작 뒤 자동 중지 시 연결·활성화 실패 조사 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd L117~150 | 두 비동기 경로가 실패를 기록하고 rollbackRun을 부른다. 원인을 단정하지 않고 확인 경로로 제시했다. |
| 32, 문장 2 | 모두 연결은 생성 응답 뒤 연결 확인·MES 활성화 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd L95~109 L117~130 | executor에서 연결 확인 후 activateUpstream을 호출한다. 화면 「Run 시작됨:」는 nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L980과 렌더 호출 L332~336에서 확인했다. |
| 32, 문장 3 | 설비만 연결도 비동기 연결 처리 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd L102~104 L138~150 | 생성 응답과 연결 처리 완료를 분리했다. |
| 34, 문장 1 | 연결·활성화 실패 시 중지·연결 해제 시도 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd L128~130 L147~150 L173~189 | stopRun과 추적된 설비의 해제를 호출하며 보상 실패도 가능해 '시도'로 썼다. |
| 34, 문장 2 | 비동기 원인은 서버 기록, 상태만으로 단계 확정 불가 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd L109~130 L146~150; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L318~340 | 비동기 예외는 시작 요청으로 전달하지 않고 서버에 기록한다. 화면의 시작 실패 창은 mutateAsync가 던진 경우에 열린다. |
| 36 | 실행·설비 결과를 연결·활성화 조사에 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L578~610 L917~925 | 화면에서 확보할 수 있는 실행 식별·연결 범위·시각·설비 상태·사이클 수로 한정했다. |

추가 기대값 확인: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacadeTest.java @ 985d17c604dd L103~118 L167~183은 연결 실패 후 MES 미활성화·stop 호출, PASSIVE 확인 실패 뒤 연결 해제를 검증한다. 시험은 실행하지 않고 원문을 읽었다.

### Socket 데모가 첫 명령을 기다리며 멈춰 보일 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 47, 문장 1 | 연결 범위와 첫 명령 수신 여부 구별 | nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 L38~60; nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ScenarioActionCompiler.java @ 5e9b02e3ae16 L78~96 | 모드와 첫 대기 여부에 따라 대기 정책이 달라진다. |
| 47, 문장 2 | 설비만 연결의 첫 대기 무기한·후속 대기 유한 | nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ScenarioActionCompiler.java @ 5e9b02e3ae16 L78~96; nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/transport/PortScenarioExecutor.java @ 5e9b02e3ae16 L168~196 | 첫 대기에만 0을 전달하고 runtime은 양수일 때만 timeout을 예약한다. 단순 정책 이름을 번역해 뜻을 붙이지 않았다. |
| 47, 문장 3 | 모두 연결은 첫 명령부터 유한 대기 | nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 L40~45; nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ScenarioActionCompiler.java @ 5e9b02e3ae16 L88~95 | FINITE에서는 첫 WAIT에도 유한 시간을 전달한다. |
| 49, 문장 1 | 식별값과 첫·후속 대기 구별을 인계 | nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/transport/PortScenarioExecutor.java @ 5e9b02e3ae16 L168~186; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L917~946 | runtime은 대기 명령·설비·포트를 로그에 남기며 사용자는 실행과 설비를 식별할 수 있다. 내부 로그 접근은 담당자에게 맡겼다. |
| 49, 문장 2 | 시작 전 대기와 후속 응답 정체를 구별 | nori-tc-simulator/scenario-engine/src/test/java/com/nori/tc/simulator/scenario/ScenarioActionCompilerTest.java @ 5e9b02e3ae16 L99~129 | 첫 대기 0·두 번째 양수, FINITE 첫 대기 양수 기대값을 읽었다. 실행 PASS라고 쓰지 않았다. |
| 51 | 마지막 송수신 조회의 문서 소유 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L578~610; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~294 | 데모 표에는 명령 세부 대신 상태·사이클 수가 있다. 별도 통신 로그 조회는 기존 message-results-and-communication-logs.md로 안내했다. |

화면 선택값 매핑은 nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorModeMapper.java @ 985d17c604dd L11~19, 한국어 문구는 nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L468~470과 대조했다.

### Normal Flow가 설비 조건 응답 뒤 진행되지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 61 | 시나리오·버전·마지막 메시지 기록 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L780~785; nori-tc-simulator/orchestrator/src/main/resources/scenario/normal-flow.scenario.md @ 5e9b02e3ae16 L13~17 | 선택 목록은 이름과 버전을 표시한다. 조건 응답이 해당 시나리오의 구별 위치다. |
| 63, 문장 1 | 수정 리소스 normal-flow v3·new-normal-flow v2 | nori-tc-simulator/orchestrator/src/main/resources/scenario/normal-flow.scenario.md @ 5e9b02e3ae16 L1~4; nori-tc-simulator/orchestrator/src/main/resources/scenario/new-normal-flow.scenario.md @ 5e9b02e3ae16 L1~4 | 42b3791f diff와 현재 헤더가 일치한다. 현장 등록 버전으로 쓰지 않았다. |
| 63, 문장 2 | 조건 응답 직후 포트 상태 변경 대기 제거 | nori-tc-simulator/orchestrator/src/main/resources/scenario/normal-flow.scenario.md @ 5e9b02e3ae16 L13~19; nori-tc-simulator/orchestrator/src/main/resources/scenario/new-normal-flow.scenario.md @ 5e9b02e3ae16 L9~14 | 현재 실행 단계에서 조건 응답 뒤 해당 대기가 없다. 과거의 무조건 정체를 현재 동작으로 서술하지 않았다. |
| 63, 문장 3 | 적재·반출 요청 뒤 대기는 유지 | nori-tc-simulator/orchestrator/src/main/resources/scenario/normal-flow.scenario.md @ 5e9b02e3ae16 L23~25 L49~51; nori-tc-simulator/orchestrator/src/main/resources/scenario/new-normal-flow.scenario.md @ 5e9b02e3ae16 L17~19 L38~40 | 서로 다른 대기 위치를 모두 실제 파일과 대조했다. |
| 65, 문장 1 | 실제 등록 원문의 대기 이벤트 대조 요청 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/DbScenarioProvider.java @ 5e9b02e3ae16 L27~34 | 실행은 repository의 원문을 읽어 파싱한다. 리소스 파일만 비교해서 현장을 판정하지 않았다. |
| 65, 문장 2 | 수정 소스 버전으로 현장 내용 확정 금지 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 L84~90 | 실제 배선은 DbScenarioProvider이고 현장 등록 원문은 이번 세션에서 읽지 않았다. |
| 65, 문장 3 | 모든 후속 대기 삭제 대신 멈춘 위치·판본 대조 | nori-tc-simulator/orchestrator/src/main/resources/scenario/normal-flow.scenario.md @ 5e9b02e3ae16 L16~25 L49~51 | 제거 위치와 유지 위치가 다르므로 포괄 삭제 조언을 배제했다. |

추가 대조: nori-tc-simulator/scenario-engine/src/test/resources/scenario/normal-flow.scenario.md의 수정 diff와 현재 리소스도 확인했다. 현재 업스트림 매칭은 nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/InMemoryUpstreamSessionRegistry.java @ 5e9b02e3ae16 L142~150에서 eventType·eqpId·correlationId를 함께 검사한다. 발행자 제거의 현재 Business 구현까지 추적하지 않았으므로 그 내부 원인 설명·과거 실측 사이클 수는 검색 원문에 넣지 않았다.

### 실행이 끝났는데 오류 수가 남아 있을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 76, 문장 1 | TERMINAL·COMPLETED에도 오류 수 확인 | nori-tc-simulator/orchestrator/src/test/java/com/nori/tc/simulator/orchestrator/AllModeOnceTerminationIntegrationTest.java @ 5e9b02e3ae16 L135~155; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L597~610 | 오류 완료 콜백 후 Run TERMINAL 기대값과 화면 오류 수 표시를 대조했다. |
| 76, 문장 2 | 오류 포트도 종료 집계, 전 포트 종료면 COMPLETED 가능 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/domain/TargetAggregate.java @ 5e9b02e3ae16 L119~135; nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L222~244 L630~633 | errorPortLot이 terminal을 표시하고 allPortsTerminal 뒤 설비를 COMPLETED로 만든다. |
| 78, 문장 1 | 오류 수·실행·대상·시각으로 실패 포트 조사 요청 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L578~610 L917~946; nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/transport/PortScenarioExecutor.java @ 5e9b02e3ae16 L176~193 | 화면의 집계값과 runtime 대기 실패의 포트 식별을 조사 경로로 연결했다. |
| 78, 문장 2 | 상태 전체 설명의 소유 문서 | docs/tc-rag-document-list.md의 상태값 행; nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L630~633 | 상태 용어 사전을 복제하지 않고 진단에 필요한 조건만 남겼다. |

### 다른 실행의 점유 때문에 새 데모를 시작하지 못할 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 88, 문장 1 | 충돌 실행 번호와 설비별 행 대조 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 L10~18; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L917~946 | 충돌 본문에 holdingRunId·eqpId가 있고 진행 현황에서 실행·설비를 구별할 수 있다. |
| 88, 문장 2 | 정상 실행과 이전 실패 뒤 남은 실행 구별 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd L92~109; nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L125~138 | 점유 중이라는 사실만으로 비정상 고아 실행을 확정하지 않았다. |
| 90, 문장 1 | 불필요 확인 뒤 전체 또는 설비별 종료 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L928~975 | 실행별·설비별 단추가 각각 존재한다. |
| 90, 문장 2 | 설비별 점유 해제·다른 대상 계속 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L292~318 | stopTarget은 선택 target만 중단·점유 해제하며 다른 target을 일괄 중단하지 않는다. |
| 90, 문장 3 | 종료 조작의 소유 문서 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L344~378 L928~975 | 기존 demo-execution.md의 종료 절을 대조하고 조작 순서를 복제하지 않았다. |
| 92, 문장 1 | 목록 부재·종료 비활성인데 점유 지속 시 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L935~963; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd L156~158 | 종료 상태·요청 중 단추 제한이 있으며 충돌 본문은 보존된다. 조작 불가 시 내부 조치를 사용자에게 요구하지 않았다. |
| 92, 문장 2 | 점유 거부와 포트 사용 충돌 구별 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L125~138 L368~385 | 설비 점유 확보 단계와 남은 워커 포트 정리 경로가 다르다. |

### 종료 뒤에도 포트 충돌로 새 데모가 실패할 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 103, 문장 1 | 담당자가 포트 충돌 확인 시 이전 상태·워커 사용 조사 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L368~385; nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/process/OrphanWorkerWatchdog.java @ 5e9b02e3ae16 L10~23 | 고아 세션 경로의 확인된 위험이다. 화면에 이 원문 오류가 반드시 보인다고 쓰지 않았다. |
| 103, 문장 2 | 워커 정리 요청 실패에도 종료 진행 가능 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L264~280 L565~600 | abort 예외를 기록하고 종료 전이·점유 해제를 계속한다. 종료 상태가 포트 정리 성공 증거는 아니다. |
| 105, 문장 1 | 223220e2 이후 종료 상태 재조정 시 중단 요청 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 L343~385 | active 메모리 실행이 저장된 terminal 상태로 재조정될 때 비종료 target의 abortInFlight를 부른다. 요청 성공을 보장하지 않았다. |
| 105, 문장 2 | 부모 감시를 켠 자식 워커 자기 종료 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/worker/WorkerPoolManager.java @ 5e9b02e3ae16 L67~78; nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/process/OrphanWorkerWatchdog.java @ 5e9b02e3ae16 L41~67 | spawn 인자에 감시를 켜고, 부모 onExit에서 종료 동작을 실행한다. |
| 105, 문장 3 | 독립 워커에는 기본 감시 없음 | nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/EquipmentWorkerModuleConfiguration.java @ 5e9b02e3ae16 L65~71; nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamRuntimeConfiguration.java @ 5e9b02e3ae16 L52~58 | 두 등록 경로 모두 parent-supervised=true 조건이다. 무조건 모든 실행 방식에서 정리된다고 쓰지 않았다. |
| 107, 문장 1 | 이전·새 실행과 종료·재시도 증거 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L917~925 L338~370 | 사용자에게 확보 가능한 식별·시각·문구를 요청했다. |
| 107, 문장 2 | 현장 수정·워커 방식 확인 요청 | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/worker/WorkerPoolManager.java @ 5e9b02e3ae16 L67~78; nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/EquipmentWorkerModuleConfiguration.java @ 5e9b02e3ae16 L65~71 | 판본·감시 조건을 확인하는 인계이며 사용자가 설정 파일이나 프로세스를 조작하게 하지 않았다. |

관련 시험 원문은 nori-tc-simulator/orchestrator/src/test/java/com/nori/tc/simulator/orchestrator/ReconcileAbortsOrphanWorkerSessionsTest.java @ 5e9b02e3ae16 L56~97, runtime/src/test/java/com/nori/tc/simulator/runtime/process/OrphanWorkerWatchdogTest.java @ 5e9b02e3ae16 L29~58, worker-equipment/src/test/java/com/nori/tc/simulator/worker/equipment/OrphanWorkerWatchdogWiringTest.java @ 5e9b02e3ae16 L20~53을 읽었다. 종료 상태 재조정 시 abort, 반복 재조정의 중복 호출 방지, active 실행 미중단, 감시 대상 종료 감지, 감시 조건부 등록을 대조했다. 이번 세션에서 시험 프로세스를 실행하지 않았다.

### 진행 현황 조회나 종료가 권한 거부로 막힐 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 117, 문장 1 | 권한 거부를 시나리오 대기와 분리·관리자 인계 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~185 | 입력·시나리오와 별도의 인가 단계가 있다. 403의 원인을 권한 미보유 하나로 확정하지 않았다. |
| 117, 문장 2 | 창 열림과 별개로 조회·종료 요청 인가 | nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 L31~59; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L69~93 | GET 조회와 POST 종료가 서로 다른 기본 권한 연결을 갖고 요청 인가를 받는다. |
| 119, 문장 1 | 기본 조회·종료 권한의 소유 문서 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L178~203; nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 L31~59 | READ·WRITE 코드와 연결을 demo-execution.md와 대조했다. 코드 표 전체를 복제하지 않았다. |
| 119, 문장 2 | 현장 권한·서버 반영 확인은 관리자 담당 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~185 | 저장소 기본 권한과 현재 principal·cache의 실제 판단을 구별했다. 현장 데이터는 조회하지 않았다. |
| 121, 문장 1 | 조회·종료 구별과 식별·시각·문구 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L198~219 L344~370 L875~878 L917~925 | 갱신·종료 오류는 메시지 영역에 나타난다. 존재하지 않는 「재시도」 단추를 제시하지 않았다. |
| 121, 문장 2 | 조회·종료 거부 시 실제 상태 확인 요청 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~185; nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 L198~208 | 인가 실패로 상태 확인·조치가 막힌 경우 담당자에게 확인하게 했다. 조회 오류를 실행 종료로 간주하지 않았다. |

## 매뉴얼과 코드가 다른 곳

이 대상의 지정 원천은 수정 커밋 3개이며 매뉴얼 절이 아니다. 매뉴얼과 코드의 충돌로 확정한 항목은 없다. 과거 커밋 메시지의 '고아 생성 방지'를 현재의 무조건 회수 보장으로 확대하지 않았고, 현재 생성 응답 전 예외와 추적 순서를 그대로 확인했다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 제외 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 배포 판본에 세 수정이 모두 포함됐다는 판정 | 현장 배포 정보·실행 판본을 조회하지 않았다. | 배포·운영 담당자 |
| 현장 normal-flow·new-normal-flow 등록 원문과 실제 대기 이벤트 | 소스 리소스와 등록 원문의 실행 경로만 확인했으며 현장 등록 자료는 없다. | 시나리오·운영 담당자 |
| 현장 생성·연결 대기 시간, 워커 실행 방식·감시 활성 상태 | 소스 기본값·조건만 있으며 실제 적용 설정은 확인하지 않았다. | 시뮬레이터·운영 담당자 |
| 개별 장애의 실제 원인·워커 잔존·포트 사용 주체 | 현장 실행·로그·포트 상태를 조사하지 않았다. | 시뮬레이터·운영 담당자 |
| 조건 응답 직후 이벤트 발행 제거의 현재 Business 내부 경로·과거 성능 개선 수치 | 커밋 설명만으로 현재 Business 동작이나 실측 효과를 입증하지 않았다. 수정 시나리오의 실제 대기 위치만 썼다. | Business·시나리오 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 넣지 않은 사실 |
|---|---|
| 00.rag/user/demo-execution.md | 창 진입·대상 추가·연결 범위 선택·시작·새로고침·전체/설비별 종료·파일 업로드/삭제의 순서, 시작·업로드 거부 문구 설명, 기본 권한 코드 표 |
| 00.rag/user/message-results-and-communication-logs.md | 통신 로그 진입·조회·본문 펼치기와 그 API 권한 |
| 00.rag/user/socket-troubleshooting.md | 문자셋·프레임 경계·종료 정규식·INITIALIZE 뒤 첫 메시지 전달의 진단 |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 설비 IP·Port·Comm Mode 확인 및 명령 결과 재조회 |
| 00.rag/user/live-map-troubleshooting.md | Live-Map 자체 조회 갱신·보기·배치 표시 문제 |
| 00.rag/user/state-reference.md | 상태값 전체 뜻풀이·다른 업무의 상태 목록 |
| 00.rag/user/permission-code-reference.md | 권한 코드 각각의 뜻과 다른 업무 권한 목록 |
| 00.rag/user/error-message-and-code-reference.md | 전체 오류 코드 사전. 이 문서는 대기·점유·정리 경로의 구별만 소유한다. |

## 검사 결과

실행 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/demo-troubleshooting.md
```

- 실제 실행 파일: `C:/Program Files/Git/usr/bin/bash.exe`, GNU bash 5.2.37(1)-release.
- 검사 JVM: Temurin OpenJDK 21.0.9+10-LTS.
- 검사기 실행 2회, 모두 exit 0·대상 위반 0·CROSS VIOLATION 0·MASK 출력 0. 첫 결과 뒤 본문 표현 1곳을 '중단'에서 '중단 요청'으로 정확하게 고치고 근거 메타를 추가했다. 최종 검사 SHA는 대상 절에 기록한 SHA와 일치한다.
- 첫 검사 준비 명령은 이미 있는 `work`에 New-Item을 호출해 `DirectoryExist`로 exit 1이었다. 이때 검사기는 시작하지 않았다. 기존 빈 폴더를 사용해 이후 2회를 실행했다.
- 최종 바이트 확인: UTF-8 엄격 디코딩 성공, BOM 없음, CR 없음, NUL 없음, NFC true. 최종 줄 수·절 수·SHA를 별도로 다시 계산했다.
- 제품 코드·설정이 바뀌지 않는 문서 작업이므로 Gradle build·제품 시험·통합 시험을 실행하지 않았다. 시험 원문 대조를 실행 PASS로 보고하지 않는다.

마지막 출력 10줄:

```text
    L103-103 문단 128  | 데모 종료 뒤 새 실행도 실패하고 운영 담당자가 Address already in use를 확인했다면, 이전 실행 상태와 남은 워커의 포트 사용을 함께 조사하도록 요청한다. 실행 종료는 워커 정리 요청이 실패해도 진행될 수 있다.
    L105-105 문단 140  | 시뮬레이터 223220e2 수정 이후에는 종료된 실행으로 상태를 맞출 때 남은 워커 세션의 중단을 요청한다. 시뮬레이터가 자식으로 띄워 부모 감시를 켠 워커는 부모 종료를 감지해 스스로 종료한다. 독립 실행 워커에는 같은 감시가 기본 적용되지 않는다.
    L107-107 문단 79  | 이전·새 실행 번호·설비 ID·종료와 재시도 시각·화면 오류를 전달한다. 현장 판본의 수정 포함 여부와 워커 실행 방식을 확인하도록 요청한다.
§ L109-121 [25] 진행 현황 조회나 종료가 권한 거부로 막힐 때   parent=297 children=3 sources=4
    L117-117 문단 91  | 진행 현황 조회나 종료가 권한 거부로 막히면, 시나리오 대기 초과와 구별해 운영 관리자에게 알린다. 데모 창이 열려 있어도 조회와 종료 요청은 각각 인가를 받는다.
    L119-119 문단 106  | 저장소 기본 권한 데이터의 조회·종료 권한 구분은 00.rag/user/demo-execution.md를 따른다. 현장 계정에 실제로 부여된 권한과 서버 반영 상태는 운영 관리자가 확인한다.
    L121-121 문단 98  | 조회인지 종료인지, 실행 번호·설비 ID·조작 시각·표시 문구를 함께 전달한다. 목록 갱신이나 종료가 거부된 상태에서는 남은 실행의 실제 상태를 담당자에게 확인하도록 요청한다.
SUMMARY sections=8 children=23 maxChild=183 maxParent=403
CORPUS docs=37/53 sections=516 part={admin=9, user=28} type={개념=4, 사용 절차=13, 알려진 제약=1, 운영=7, 트러블슈팅=12}
TOTAL files=37 targets=1 violations=0
```

사람 검사:

| 절 | 제목만으로 질문 식별 | 원천 일치 | 독립 답 | 사실 중복·권한 경계 |
|---|---|---|---|---|
| 테스트 시작이 시간 초과됐는데 실행이 남았는가 | 충족 | 충족 | 응답 전 생성 가능성과 재시도 전 확인·인계 제공 | 점유 충돌 뒤 추적과 증상 진입점을 구별, 조작 재수록 없음 |
| 시작 응답 뒤 직접 종료하지 않았는데 중지됐을 때 | 충족 | 충족 | 비동기 연결·활성화 실패 조사 경로 제공 | 시작 절차는 필요한 전제만 남김 |
| Socket 데모가 첫 명령을 기다리며 멈춰 보일 때 | 충족 | 충족 | 첫·후속 명령 대기 차이와 인계 제공 | 일반 Socket 프레임 진단을 복제하지 않음 |
| Normal Flow가 설비 조건 응답 뒤 진행되지 않을 때 | 충족 | 충족 | 제거·유지 위치와 등록본 대조 제공 | 수정 이전 장애를 현재 보편 동작으로 쓰지 않음 |
| 실행이 끝났는데 오류 수가 남아 있을 때 | 충족 | 충족 | 종료 상태와 오류 사이클 구별 제공 | 상태 사전 대신 해당 오판 방지 조건만 포함 |
| 다른 실행의 점유 때문에 새 데모를 시작하지 못할 때 | 충족 | 충족 | 점유 실행·설비 대조와 종료 가능성별 인계 제공 | 시작 거부 문구 설명·조작 순서 미복제 |
| 종료 뒤에도 포트 충돌로 새 데모가 실패할 때 | 충족 | 충족 | 워커 정리 실패·수정 조건·판본 인계 제공 | 내부 접속·설정 변경·프로세스 종료 지시 없음 |
| 진행 현황 조회나 종료가 권한 거부로 막힐 때 | 충족 | 충족 | 조회·제어 거부를 시나리오 실패와 분리 | 기본 권한 표는 소유 문서로 안내, 관리자 자료 복제 없음 |

8절 전부 읽었다. 제목 정확 중복·3회 이상 반복 틀, 같은 본문 복제, 빈 틀 문장, 이름 토막 뜻풀이, 조사 접합 오류·잘린 문장, 오답 인용, 필수 값 마스킹, admin objectKey·링크·관리 문서 열람 지시는 발견하지 않았다. 반복 인계값은 서로 다른 실패 경로를 식별하는 데 필요한 최소 정보이다. 현재 수정으로 현장 장애가 모두 해결됐다고 단정한 문장도 없다. 사용자 결정이 필요한 미해결 항목은 없다.

검사 원문 로그는 지정 work의 `check-initial.log`·`check-final.log`에만 생성했다. 최종 원문 SHA·검사 결과·마지막 출력은 이 기록에 보존했다.

정리 결과:

- 절대 부모 경로: `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-37-demo-troubleshooting/work/`.
- 두 로그는 이번 세션의 검사 출력이다. 정확한 절대경로가 위 지정 work 안의 일반 파일임을 확인했다. 파일 속성은 Archive만 있었고, work·작업 폴더에도 reparse가 없었다. 두 파일 모두 FileShare.None의 배타 읽기에 성공했다.
- 검사 도구 세션 두 개의 종료코드 0을 회수했다. Get-Process로 확인한 Java 프로세스 두 개는 이번 작업 이전 시작이었다. 다른 프로세스는 종료하지 않았다.
- 추가 프로세스 명령행 확인 `Get-CimInstance Win32_Process -ErrorAction Stop`은 `액세스가 거부되었습니다`, `HRESULT 0x80041003`으로 실패했다. 이 전수 조회를 성공으로 기록하지 않는다. 원인은 더 확정하지 않았으며 권한·정책·전역 설정을 변경하지 않았다. 소유 검사 프로세스 종료와 배타 파일 열림 결과로 두 소유 로그의 실제 사용 종료를 별도로 확인했다.
- PowerShell 5.1.26100.9444에서 `Remove-Item -LiteralPath <정확한 로그 경로> -ErrorAction Stop`을 두 파일에 각각 실행했다. Force·Recurse는 사용하지 않았다. 정상 삭제 명령 exit 0이며 각 경로의 Test-Path는 false, work 잔여 항목은 0개였다.
- 기존 work 폴더는 이 세션이 만들지 않았으므로 보존했다. 삭제한 로그 SHA-256은 initial `026cf176b52b3e28685be11178c3d250fa0e09505026878047c9d0198abfefe2`, final `7ad5f3e65f4d0699cec7c24c5136667bdbfcfe14b48787a7c21b3cb77895342a`이다.

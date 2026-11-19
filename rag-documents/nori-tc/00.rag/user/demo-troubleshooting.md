# 데모 대기 초과·실행 실패·남은 실행 확인
```rag-meta
유형: 트러블슈팅
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-simulator 5e9b02e3ae16·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/demo-execution.md
```

## 테스트 시작이 시간 초과됐는데 실행이 남았는가
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; createRun·호출 실패 L46~52 L115~148
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 생성 응답 뒤 추적 L81~109
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 생성·설비 확보 L115~138
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 목록 갱신·실행 식별 L198~208 L904~925
```

「테스트 시작」 요청이 시간 초과되면 재시도 전에 「시뮬레이션 진행 현황」을 갱신한다. 요청 시각·시나리오·대상 설비가 맞는 실행이 생겼는지 확인한다. 생성 응답을 받기 전에 서버가 실행을 만들고 설비를 확보했을 수 있다.

남은 실행이 있으면 실행 번호와 상태를 기록하고 불필요한 실행인지 확인한 뒤 종료한다. 목록에서 찾지 못하거나 종료하지 못하면 요청 시각·대상·실패 문구를 운영 담당자에게 넘긴다. 갱신·종료 조작은 `00.rag/user/demo-execution.md`를 따른다.

백엔드 050c5f6b 수정 이후 실행 생성은 조회·종료와 별도 대기 시간을 사용한다. 재발하면 운영 담당자에게 해당 수정의 포함 여부와 생성 응답 지연을 확인하도록 요청한다.

## 시작 응답 뒤 직접 종료하지 않았는데 중지됐을 때
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 비동기 연결·활성화·보상 L95~150 L173~189
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacadeTest.java @ 985d17c604dd ; 연결 실패·중지 기대값 L103~118 L167~183
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작 응답·실행·설비 결과 L318~340 L578~610 L917~925
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 문구 L980~980
```

「Run 시작됨:」 뒤 직접 종료하지 않았는데 실행이 중지되면 연결 확인·MES 활성화 단계의 실패를 확인한다. 「모두 연결」은 생성 응답 뒤에 설비 연결을 확인하고 MES를 활성화한다. 「설비만 연결」도 연결 처리를 뒤에서 수행한다.

연결 처리나 「모두 연결」의 MES 활성화가 실패하면 서버가 실행 중지와 연결 해제를 시도한다. 실패 사유는 시작 요청의 실패 창 대신 서버에 기록되므로, 실행 상태만으로 실패 단계를 확정할 수 없다.

실행 번호·연결 범위·시작 시각·설비별 「현재 상태」·「처리 결과 (완료/오류)」를 운영 담당자에게 넘겨 연결 확인과 MES 활성화 중 실패한 단계를 확인하도록 요청한다.

## Socket 데모가 첫 명령을 기다리며 멈춰 보일 때
```rag-meta
원천: nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 ; 모드별 대기 정책 L38~60
원천: nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ScenarioActionCompiler.java @ 5e9b02e3ae16 ; Socket 첫 명령·후속 명령 대기 L72~106
원천: nori-tc-simulator/scenario-engine/src/test/java/com/nori/tc/simulator/scenario/ScenarioActionCompilerTest.java @ 5e9b02e3ae16 ; 첫 대기·후속 대기 기대값 L99~129
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 연결 범위 문구 L468~470
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorModeMapper.java @ 985d17c604dd ; 화면 선택과 실행 모드 연결 L11~19
```

Socket 데모가 진행되지 않으면 선택한 연결 범위와 첫 명령 수신 여부를 구별한다. 「설비만 연결」은 첫 명령을 기한 없이 기다리며, 첫 명령을 받은 뒤의 다음 명령 대기에는 제한 시간이 있다. 「모두 연결」은 첫 명령부터 제한 시간이 있다.

운영 담당자에게 실행 번호·설비 ID·연결 범위·시나리오·멈춘 시각을 전달하고, 첫 명령 전 대기인지 후속 명령 대기인지 확인하도록 요청한다. 첫 명령을 기다리는 상태와, 시작된 사이클이 응답을 받지 못한 상태를 나누어 조사한다.

화면에서 마지막 송수신 메시지를 확인하는 경로는 `00.rag/user/message-results-and-communication-logs.md`를 따른다.

## Normal Flow가 설비 조건 응답 뒤 진행되지 않을 때
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/resources/scenario/normal-flow.scenario.md @ 5e9b02e3ae16 ; 수정 버전·조건 응답·후속 대기 L1~25 L49~51
원천: nori-tc-simulator/orchestrator/src/main/resources/scenario/new-normal-flow.scenario.md @ 5e9b02e3ae16 ; 수정 버전·조건 응답·후속 대기 L1~19 L38~40
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/DbScenarioProvider.java @ 5e9b02e3ae16 ; 실행 시 등록 원문 사용 L27~34
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시나리오 버전 표시 L780~785
```

Socket의 Normal Flow·New Normal Flow가 설비 조건 응답 뒤 정체되면 선택 목록의 시나리오·버전과 마지막 메시지를 기록한다.

시뮬레이터 42b3791f 수정 시나리오는 normal-flow v3·new-normal-flow v2이다. EQP_CONDITION_CHECK_REPLY 직후의 EQP_PORT_STATUS_CHG 대기를 제거했다. WO_CAR_LOAD·WO_CAR_UNLOAD_REQ 뒤의 EQP_PORT_STATUS_CHG 대기는 유지한다.

운영 담당자에게 실제 실행에 쓰는 등록 시나리오에서 어느 이벤트를 기다리는지 대조하도록 요청한다. 소스의 수정 버전만으로 현장 등록본의 내용까지 확정하지 않는다. 후속 대기를 일괄 제거하지 않고, 멈춘 위치와 수정 적용 여부를 먼저 구별한다.

## 실행이 끝났는데 오류 수가 남아 있을 때
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/domain/TargetAggregate.java @ 5e9b02e3ae16 ; 오류 포트 집계 L119~135
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 오류·완료 포트와 설비·실행 종료 L218~244 L613~633
원천: nori-tc-simulator/orchestrator/src/test/java/com/nori/tc/simulator/orchestrator/AllModeOnceTerminationIntegrationTest.java @ 5e9b02e3ae16 ; 오류 종료도 TERMINAL인 기대값 L135~155
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 상태·오류 수 표시 L597~610
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 결과 열 L438~445
```

「모두 연결」에서 실행이 TERMINAL이고 설비가 COMPLETED여도 「처리 결과 (완료/오류)」의 오류 수를 확인한다. 오류로 끝난 포트도 종료된 포트로 집계하므로, 모든 포트가 끝나면 설비는 COMPLETED가 될 수 있다.

오류 수가 있으면 실행 번호·설비 ID·완료 수·오류 수·시나리오·발생 시각을 운영 담당자에게 전달해 실패한 포트와 마지막 대기·송수신을 확인하도록 요청한다. 상태값 전체 설명은 `00.rag/user/state-reference.md`에서 다룬다.

## 다른 실행의 점유 때문에 새 데모를 시작하지 못할 때
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 점유 충돌 본문 보존 L152~158
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/dto/LeaseConflictResponse.java @ 5e9b02e3ae16 ; 점유 실행 번호·설비 ID L10~18
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 점유 확보·설비별 해제 L125~138 L292~318
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실행·대상·종료 조건 L904~975
```

점유 거부에 나온 실행 번호를 「시뮬레이션 진행 현황」에서 찾고 설비별 행과 대조한다. 정상 진행 중인 실행인지, 앞선 시작 실패 뒤 남은 실행인지 운영 담당자와 확인한다.

불필요한 실행으로 확인되면 전체 실행 또는 문제 설비만 종료한다. 설비별 종료는 해당 설비의 점유를 해제하며 같은 실행의 다른 설비는 계속 진행한다. 조작은 `00.rag/user/demo-execution.md`를 따른다.

실행을 찾지 못하거나 종료 단추가 비활성인데 점유가 계속되면 거부 문구의 실행 번호·설비 ID·확인 시각·목록 상태를 전달한다. 종료 뒤의 포트 사용 충돌은 실행 점유 거부와 구별해 알린다.

## 종료 뒤에도 포트 충돌로 새 데모가 실패할 때
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 고아 세션 정리·실패 처리 L343~385 L565~600
원천: nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/process/OrphanWorkerWatchdog.java @ 5e9b02e3ae16 ; 부모 감시·종료 L41~67
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/worker/WorkerPoolManager.java @ 5e9b02e3ae16 ; 부모 감시를 켜고 실행 L67~78
원천: nori-tc-simulator/worker-equipment/src/main/java/com/nori/tc/simulator/worker/equipment/EquipmentWorkerModuleConfiguration.java @ 5e9b02e3ae16 ; 감시 조건 L65~71
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamRuntimeConfiguration.java @ 5e9b02e3ae16 ; 감시 조건 L52~58
```

데모 종료 뒤 새 실행도 실패하고 운영 담당자가 Address already in use를 확인했다면, 이전 실행 상태와 남은 워커의 포트 사용을 함께 조사하도록 요청한다. 실행 종료는 워커 정리 요청이 실패해도 진행될 수 있다.

시뮬레이터 223220e2 수정 이후에는 종료된 실행으로 상태를 맞출 때 남은 워커 세션의 중단을 요청한다. 시뮬레이터가 자식으로 띄워 부모 감시를 켠 워커는 부모 종료를 감지해 스스로 종료한다. 독립 실행 워커에는 같은 감시가 기본 적용되지 않는다.

이전·새 실행 번호·설비 ID·종료와 재시도 시각·화면 오류를 전달한다. 현장 판본의 수정 포함 여부와 워커 실행 방식을 확인하도록 요청한다.

## 진행 현황 조회나 종료가 권한 거부로 막힐 때
```rag-meta
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 ; 조회·제어 기본 권한 연결 L31~59
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 L178~203
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 인가·기본 차단 L142~185
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 갱신·종료 오류 표시 L198~219 L344~370 L875~878
```

진행 현황 조회나 종료가 권한 거부로 막히면, 시나리오 대기 초과와 구별해 운영 관리자에게 알린다. 데모 창이 열려 있어도 조회와 종료 요청은 각각 인가를 받는다.

저장소 기본 권한 데이터의 조회·종료 권한 구분은 `00.rag/user/demo-execution.md`를 따른다. 현장 계정에 실제로 부여된 권한과 서버 반영 상태는 운영 관리자가 확인한다.

조회인지 종료인지, 실행 번호·설비 ID·조작 시각·표시 문구를 함께 전달한다. 목록 갱신이나 종료가 거부된 상태에서는 남은 실행의 실제 상태를 담당자에게 확인하도록 요청한다.

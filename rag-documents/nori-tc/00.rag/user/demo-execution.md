# 데모 실행
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-simulator 5e9b02e3ae16·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/demo-troubleshooting.md
```

## Live-Map에서 데모 시작 창 열기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; Live-Map 진입 L82~87
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; DASH_READ_PERMISSION L1~5
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 공통 데모 단추 L239~257
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 문구 L398~398
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 작업별 인가 L160~185
```

LIVE_MAP_READ로 「Live-Map」에 들어간 뒤, 상단 설비 집계 아래 왼쪽의 「데모 시작」을 누른다. 보기 전환과 관계없이 같은 자리에 있다.

「데모 시작」 단추가 보여도 시나리오 조회·테스트 시작·종료 권한은 서버에서 따로 검사한다.

## 데모 조회·시작·종료에 필요한 권한
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; Run 기본 권한 L178~203
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 ; 조회·제어 연결 L31~59
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 데모 API L56~112
```

저장소 기본 권한 데이터 기준으로 데모 조작에는 다음 권한이 필요하다.

| 데모 조작 | 필요한 권한 |
|---|---|
| 시나리오 목록·진행 현황 조회 | LIVE_MAP_DEMO_RUN_READ |
| 테스트 시작·전체 종료·설비별 종료·시나리오 업로드 | LIVE_MAP_DEMO_RUN_WRITE |

권한 거부가 나오면 운영 관리자에게 조작·발생 시각·화면 문구를 전달하고 계정 권한을 확인한다. 화면에서 대상이나 시나리오를 바꿔도 부족한 권한은 해결되지 않는다.

## 라인 트리에서 대상 설비 준비하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·추가·제거 L272~315
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 대상 표·입력 L551~575
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-demo.ts @ 9fe69a75dcd3 ; 검색·중복 제거 L103~165
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446
```

1. 「라인 트리」에서 설비를 체크한다. 라인·공정의 체크박스로 하위 설비를 한꺼번에 선택할 수 있다.
2. 필요한 경우 「설비 ID 또는 설비명으로 검색」에 입력한다. 앞뒤 공백·영문 대소문자를 구별하지 않는 부분 검색이다.
3. 가운데 오른쪽 화살표를 눌러 「대상 설비」로 옮긴다. 같은 설비는 중복 추가되지 않는다.
4. 「Eqp ID」·「공정」·「웨이퍼 모드」·「현재 상태」를 확인한다. 빼려는 설비는 그 행의 「삭제」 단추를 누른다.

대상이 비면 「좌측 트리에서 설비를 체크한 뒤 화살표 버튼으로 추가해 주세요.」가 나온다. 테스트에는 한 대 이상이 필요하다.

## 설비만 연결·MES만 연결·모두 연결 고르기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 초기 선택 L109~113
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 연결 선택값 L468~470
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorModeMapper.java @ 985d17c604dd ; toMode L11~21
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 연결 후 MES 활성화 L117~130
원천: nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 ; of L41~67
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 포트별 완료·반복 L228~259
```

「시뮬레이션 진행 현황」 오른쪽에서 연결 범위를 고른다. 처음 선택값은 「모두 연결」이다.

| 선택값 | 실행 범위와 반복 |
|---|---|
| 설비만 연결 | 설비 쪽 시나리오를 실행하고 중지할 때까지 반복한다. |
| MES만 연결 | MES 쪽 시나리오를 실행하고 중지할 때까지 반복한다. |
| 모두 연결 | 설비 연결을 확인한 뒤 MES 쪽을 실행한다. 각 포트의 처리를 한 번씩 수행한다. |

## 시나리오를 고르고 테스트 시작하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 자동 선택 L173~185
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작 요청 L318~340
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·시작 단추 L751~870
원천: nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunScenarios.ts @ 9fe69a75dcd3 ; 전체 목록 조회 L6~13
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 선택·시작 문구 L450~465
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 응답 문구 L980~980
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 실행 생성·비동기 연결 L81~109
```

1. 시나리오 목록에서 실행할 항목을 고른다. 목록에는 시나리오와 버전이 표시되고, 옆 배지에는 선택한 시나리오와 통신 방식이 나온다.
2. 시나리오 목록은 연결 범위별로 걸러지지 않는다. 대상 설비에 맞는 시나리오인지 확인한다. 기존 선택이 없으면 첫 항목이 자동 선택된다.
3. 「테스트 시작」을 누른다. 대상이 없거나 시나리오가 없으면 비활성화되며, 요청 중에는 「시작 요청 중...」으로 바뀐다.

성공 응답 뒤 「Run 시작됨:」과 실행 번호·대상 대수가 나온다. 「모두 연결」의 설비 연결 확인·MES 활성화는 이어서 진행되므로 「시뮬레이션 진행 현황」에서 결과를 확인한다.

## 입력 칸으로 고칠 수 없는 시작 거부 조건
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapSimulatorRunCreateRequest.java @ 985d17c604dd ; 필수값 L12~20
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorTargetAssembler.java @ 985d17c604dd ; 설비·포트 조회 L31~55
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택값 전달 L318~340
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 입력 화면 L625~870
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 점유 거부 문구 L152~166
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 시나리오 조회·대상 확보 L115~138
```

서버는 연결 범위·시나리오·한 개 이상의 빈 값 없는 설비 ID를 요구한다. 선택한 설비의 통신 방식·연결 모드·포트 정보는 등록 정보에서 가져오며 데모 창에는 수정 칸이 없다.

「설비를 찾을 수 없습니다.」 또는 「테스트 가능한 포트가 없습니다.」가 포함되면 설비 ID·발생 시각·표시 문구를 운영 담당자에게 전달한다.

테스트 시작에서 「설비가 이미 다른 Run 에 점유되어 있습니다.」가 나오면 문구에 포함된 설비와 실행 번호를 기록한다. 실행이 남은 이유를 확인하는 절차는 `00.rag/user/demo-troubleshooting.md`에서 다룬다.

## 실행 줄에서 진행 현황 읽기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실행 줄 L896~940
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-demo-run-history.ts @ 9fe69a75dcd3 ; 정렬·완료 집계 L18~34
원천: nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunList.ts @ 9fe69a75dcd3 ; 목록 갱신 L7~15
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 시각 L450~450
```

「시뮬레이션 진행 현황」에는 과거 실행과 실행 중인 항목이 함께 나오며, 새 실행은 아래쪽에 놓인다. 창이 열린 동안 3초 간격으로 다시 조회한다.

실행 줄에서 실행 번호·연결 범위·시나리오·「시작 시각」·상태를 읽는다. 상태 옆 숫자는 완료 설비 수 / 전체 설비 수이다. 처리 사이클 수는 실행 줄을 펼쳐 설비별로 확인한다.

## 실행 줄을 펼쳐 설비별 처리 결과 보기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 설비별 열 L578~611
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 펼치기·대상 표 L904~975
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 초기 접힘 L120~121
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 문구 L435~447
```

실행 줄의 화살표나 실행 번호 영역을 누르면 설비별 표가 열린다. 처음에는 접혀 있다.

표는 「Eqp ID」·「연결 유형」·「통신」·「현재 상태」·「처리 결과 (완료/오류)」·「종료」 순서이다. 「처리 결과 (완료/오류)」는 설비별 완료 사이클 수 / 오류 사이클 수를 표시한다.

## 전체 실행 또는 설비 한 대 중지하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 요청 L344~378
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 단추 조건 L928~968
원천: nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRun.ts @ 9fe69a75dcd3 ; 종료 상태 판정 L4~7
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 전체·설비별 중지 L201~243
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 종료 문구 L981~985
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 전체·설비별 중지 L264~320
```

1. 실행 하나의 모든 설비를 멈추려면 그 실행 줄의 「전체 종료」를 누른다.
2. 한 대만 멈추려면 실행 줄을 펼치고 해당 설비 행의 「종료」 단추를 누른다. 같은 실행의 다른 설비는 계속 진행한다.
3. 종료 요청 문구가 나온 뒤 실행과 설비의 「현재 상태」를 확인한다.

종료 요청 중이거나 이미 끝난 실행·설비의 종료 단추는 비활성화된다. 「전체 종료」는 다른 실행 줄까지 한꺼번에 중지하는 단추가 아니다.

## 데모 창을 닫았다가 진행 현황 다시 보기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 닫기·입력 초기화 L228~245
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 목록 재조회 L198~226
원천: nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunList.ts @ 9fe69a75dcd3 ; 서버 목록 조회 L7~15
```

「데모 시작」 창을 닫으면 대상 설비·검색어·연결 범위·시나리오 선택이 초기화된다. 창 닫기는 실행 중지 요청을 보내지 않는다.

「데모 시작」을 다시 열면 서버의 실행 목록을 다시 읽는다. 계속 실행 중인 항목을 찾아 상태를 확인하거나 종료한다.

## 시나리오 목록과 진행 현황 새로고침하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 두 목록 갱신 L198~209
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 새로고침 단추 L791~805
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 단추 문구 L451~453
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 빈 시나리오·시작 제한 L751~870
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시나리오 미선택 L465~465
```

시나리오 선택 목록 오른쪽의 원형 화살표 그림에 마우스를 올리면 「새로고침」이 나온다. 누르면 시나리오 목록과 실행 진행 현황을 함께 다시 조회한다. 조회 중에는 단추가 비활성화된다.

시나리오 목록이 비어 있으면 「시나리오 없음」·「시나리오 미선택」으로 표시되고 테스트를 시작할 수 없다.

## 시나리오 파일 업로드하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 업로드 결과 L380~396
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 파일 선택·단추 L808~832
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 업로드 문구 L456~462
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; UTF-8 읽기·빈 파일 거부 L107~132
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 검증·중복 거부 L73~95
```

1. 시나리오 목록 오른쪽의 위쪽 화살표 그림에 마우스를 올려 「시나리오 업로드」인지 확인하고 누른다.
2. UTF-8 시나리오 Markdown 파일을 선택한다. 파일 선택 창은 `.md`와 텍스트 파일을 표시하며, 화면에서 파일 내용은 검사하지 않는다.
3. 성공하면 「시나리오 업로드됨:」 뒤에 시나리오가 표시되고, 갱신한 목록에서 업로드한 항목이 선택된다.

서버는 빈 파일·시나리오 형식 오류·이미 등록된 시나리오 ID를 거부한다. 기존 ID의 파일을 다시 올려 덮어쓰는 방식은 제공하지 않는다.

## 업로드 실패 문구별로 파일 확인하기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 빈 파일·읽기 실패 L125~132
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 형식 검사·ID 중복·409 응답·ID 헤더 L73~133
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; 형식 오류의 400 응답 L18~23
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/ScenarioConflictException.java @ 5e9b02e3ae16 ; 중복 ID 문구 L9~10
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 업로드 전달 L89~92
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115
원천: nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 업로드 오류 전달 L145~170
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 업로드 실패·표시 L380~396
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003
```

업로드 실패는 「테스트 시작 실패」 창의 문구로 구별한다.

- 「업로드 파일이 비어 있습니다.」이면 내용이 있는 파일을 선택한다.
- 「업로드 파일을 읽지 못했습니다:」 뒤에는 읽기 실패 사유가 붙는다. 해당 문구와 파일을 운영 담당자에게 전달한다.
- 형식 오류는 「simulator orchestrator 호출에 실패했습니다. status=400」으로 표시된다. 시나리오 형식을 고쳐 다시 올린다.
- 「설비가 이미 다른 Run 에 점유되어 있습니다.」 뒤 본문에 SCENARIO_CONFLICT와 「이미 존재하는 시나리오입니다. scenarioId=」 및 ID가 있으면 ID 중복이다. 파일의 scenario-id를 새 ID로 바꿔 업로드한다.

## 올린 시나리오 삭제하기
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 삭제 기본 권한 L187~193
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_22_add_live_map_demo_run_scenario_delete_permission.sql @ f1d5e0a8e727 ; 삭제 권한 연결 L28~39
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 권한 판정 L127~131
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 처리 L399~415
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 단추 L835~855
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 결과 L459~463
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 기본 시나리오 보호 L98~106
```

저장소 기본 권한 데이터 기준으로 삭제에는 LIVE_MAP_DEMO_RUN_SCENARIO_DELETE가 필요하다. 이 권한이 없으면 「시나리오 삭제」 그림 단추가 나타나지 않는다.

목록에서 사용자 업로드 시나리오를 고르고 휴지통 그림의 「시나리오 삭제」를 누른다. 성공하면 「시나리오 삭제됨:」 뒤에 시나리오 ID가 나온다.

기본 시나리오를 선택하면 삭제 단추가 비활성화되고 마우스를 올렸을 때 「기본 시나리오는 삭제할 수 없습니다」가 나온다.

## 시작·업로드·삭제가 거부됐을 때 창 읽기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창·확인 L988~1003
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 우선·대체 문구 L19~77
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 제목 L464~464
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 기본 문구 L969~971
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 시나리오 전달·공통 변환 L89~168
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 보호 거부 응답 L98~119
원천: nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 업로드·삭제 오류 전달 L145~183
```

시작·업로드·삭제가 실패하면 「테스트 시작 실패」 창이 열린다. 서버 오류 문구가 있으면 그 문구를 표시하고, 없으면 조작에 따라 다음 문구를 표시한다.

- 테스트 시작: 「Demo 테스트 시작에 실패했습니다.」
- 업로드: 「시나리오 업로드에 실패했습니다.」
- 삭제: 「시나리오 삭제에 실패했습니다.」

실패 문구와 대상 설비·시나리오·조작 시각을 기록한 뒤 「확인」으로 닫는다. 업로드·삭제 오류에도 창 제목은 「테스트 시작 실패」로 표시된다.

업로드·삭제에서 「설비가 이미 다른 Run 에 점유되어 있습니다.」가 보이면 뒤에 붙은 응답 본문의 code로 구별한다. 삭제의 SEED_PROTECTED는 기본 시나리오 보호 거부이므로 삭제를 중단한다.

## 종료 실패나 실행 정체를 담당자에게 알리기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 실패 L344~370
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 결과 메시지 영역 L875~878
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 종료 실패 문구 L981~985
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 연결 실패 후 중지 L117~150
```

종료가 실패하면 진행 현황 영역의 메시지에 서버 문구가 나오고, 없으면 「Run 종료 요청에 실패했습니다.」가 나온다. 실행 번호·설비 ID·종료 요청 시각·표시 문구를 운영 담당자에게 전달한다.

시작 뒤 대기가 이어지거나 의도하지 않게 실행이 중지되면 실행 줄과 설비별 상태·완료/오류 수를 기록한다. 대기 초과·실행 실패·남은 실행의 원인 구별은 `00.rag/user/demo-troubleshooting.md`에서 다룬다.

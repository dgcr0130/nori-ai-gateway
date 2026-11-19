# 데모 실행 작성 기록

## 대상
- objectKey: `00.rag/user/demo-execution.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문 SHA-256: `c76cf0037a500734d3bd8d5dd9312eaeb2e003a1730d19ef2e574603c080f9b5`
- 검색 원문 줄 수: 257
- 검색 원문 H2 절 수: 16
- 확인일: 2026-11-18 (한국 시간)
- 완료 범위: 검색 원문과 작성 기록 두 파일 작성 및 자체 검사. 업로드·제품 변경·독립 공식 검증은 수행하지 않았다.

## 원천 판본
읽기 전 범위별 status를 확인하고 작성 기록 저장 직전에 아래 정확한 파일 목록을 다시 `git -c safe.directory=<저장소 절대경로> -C <저장소> status --short -- <파일들>`로 확인했다. 각 명령 exit 0, stdout 비어 있음이다. 네 저장소 HEAD는 본문 판본과 일치한다.

| 저장소 | HEAD 12자리 | 확인 결과 |
|---|---|---|
| nori-tc-exec | f1d5e0a8e727 | 아래 원천 파일 수정 상태 없음 |
| nori-tc-simulator | 5e9b02e3ae16 | 아래 원천 파일 수정 상태 없음 |
| nori-tc-ui-backend | 985d17c604dd | 아래 원천 파일 수정 상태 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | 아래 원천 파일 수정 상태 없음 |

Git의 소유자 검사에는 명령 단위 `-c safe.directory=<해당 저장소>`만 사용했다. 전역 설정은 바꾸지 않았다. status stderr에는 사용자 전역 ignore 파일 접근 거부 경고가 있었으며 이를 파일 수정으로 해석하지 않았다. 읽기 전용 명령 자체의 종료코드는 0이다.

### nori-tc-exec 확인 파일
- `docs/completion-report/07-user-manual.md`
- `docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql`
- `docs/db_table/patch/postgres_2026_06_22_add_live_map_demo_run_scenario_delete_permission.sql`
- `docs/scripts/manual-permission-catalog.json`

### nori-tc-simulator 확인 파일
- `orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java`
- `orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java`
- `orchestrator/src/test/java/com/nori/tc/simulator/orchestrator/ScenarioUploadTest.java`
- `scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java`

### nori-tc-ui-backend 확인 파일
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapSimulatorRunCreateRequest.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorModeMapper.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorTargetAssembler.java`
- `ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/LiveMapControllerSimulatorRunTest.java`
- `ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/security/LiveMapDemoRunListPermissionTest.java`
- `ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/security/LiveMapScenarioPermissionTest.java`
- `ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/simulator/SimulatorTargetAssemblerTest.java`

### nori-tc-ui-front 확인 파일
- `src/app/Router.tsx`
- `src/features/live-map/api/live-map-demo-run.api.ts`
- `src/features/live-map/components/LiveMapDemoModal.tsx`
- `src/features/live-map/components/LiveMapPage.tsx`
- `src/features/live-map/hooks/useSimulatorRun.ts`
- `src/features/live-map/hooks/useSimulatorRunList.ts`
- `src/features/live-map/hooks/useSimulatorRunScenarios.ts`
- `src/features/live-map/lib/live-map-demo-run-history.ts`
- `src/features/live-map/lib/live-map-demo.ts`
- `src/i18n/locales/ko.json`
- `src/shared/lib/api-error.ts`
- `src/shared/lib/permission.ts`
- `src/shared/lib/resolve-error-message.ts`
- `tests/unit/live-map-demo-run-history.test.ts`

의존 라이브러리의 refetch 오류 처리도 로컬 파일로 읽었다: `nori-tc-ui-front/node_modules/@tanstack/query-core/src/queryObserver.ts` L325~351, sha256=683c66fbabc7ef1adc4988a10e1328d0bbf539eb83913580c44a479db6ec1d62. throwOnError 미설정 시 catch(noop) 후 결과 객체를 반환한다. 이 파일은 Git HEAD 근거로 취급하지 않는다.

## 절별 사실 대조
각 행은 최종 검색 원문의 문장 하나 또는 표 자료 행 하나이다. 관찰 가능한 정보의 기록·운영 담당자 인계는 사용자 지정 user 범위에 따른 안내이며 제품 자동 기능으로 서술하지 않았다.

### Live-Map에서 데모 시작 창 열기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 19 | LIVE_MAP_READ로 「Live-Map」에 들어간 뒤, 상단 설비 집계 아래 왼쪽의 「데모 시작」을 누른다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; Live-Map 진입 L82~87<br>nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; DASH_READ_PERMISSION L1~5<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 공통 데모 단추 L239~257<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 문구 L398~398 | Router의 권한 상수와 공통 도구막대 렌더 위치 확인. |
| 19 | 보기 전환과 관계없이 같은 자리에 있다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; Live-Map 진입 L82~87<br>nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; DASH_READ_PERMISSION L1~5<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 공통 데모 단추 L239~257<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 문구 L398~398 | Router의 권한 상수와 공통 도구막대 렌더 위치 확인. |
| 21 | 「데모 시작」 단추가 보여도 시나리오 조회·테스트 시작·종료 권한은 서버에서 따로 검사한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 작업별 인가 L160~185 | 보호 API는 화면 표시와 별개로 API 권한 목록과 사용자 권한을 대조한다. |

### 데모 조회·시작·종료에 필요한 권한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 31 | 저장소 기본 권한 데이터 기준으로 데모 조작에는 다음 권한이 필요하다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; Run 기본 권한 L178~203<br>nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 ; 조회·제어 연결 L31~59 | 현장 DB 조회 없이 기본 데이터의 GET/POST 연결만 기록한다. |
| 35 | \| 시나리오 목록·진행 현황 조회 \| LIVE_MAP_DEMO_RUN_READ \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; Run 기본 권한 L178~203<br>nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 ; 조회·제어 연결 L31~59<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 데모 API L56~112 | GET PREFIX가 scenarios와 실행 목록을 함께 포함한다. |
| 36 | \| 테스트 시작·전체 종료·설비별 종료·시나리오 업로드 \| LIVE_MAP_DEMO_RUN_WRITE \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; Run 기본 권한 L178~203<br>nori-tc-exec/docs/db_table/patch/postgres_2026_06_16_add_live_map_demo_run_api_permissions.sql @ f1d5e0a8e727 ; 조회·제어 연결 L31~59<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 데모 API L56~112 | POST PREFIX가 생성·전체/개별 stop·업로드에 적용된다. |
| 38 | 권한 거부가 나오면 운영 관리자에게 조작·발생 시각·화면 문구를 전달하고 계정 권한을 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185 | 권한 판정은 입력값이 아니라 요청 경로·메서드·보유 코드로 결정된다. 인계 항목은 관찰 가능한 조작 정보다. |
| 38 | 화면에서 대상이나 시나리오를 바꿔도 부족한 권한은 해결되지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185 | 권한 판정은 입력값이 아니라 요청 경로·메서드·보유 코드로 결정된다. 인계 항목은 관찰 가능한 조작 정보다. |

### 라인 트리에서 대상 설비 준비하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 49 | 1. 「라인 트리」에서 설비를 체크한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·추가·제거 L272~315<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446 | 개별 선택·그룹 하위 ID 수집 확인. |
| 49 | 라인·공정의 체크박스로 하위 설비를 한꺼번에 선택할 수 있다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·추가·제거 L272~315<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446 | 개별 선택·그룹 하위 ID 수집 확인. |
| 50 | 2. 필요한 경우 「설비 ID 또는 설비명으로 검색」에 입력한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/features/live-map/lib/live-map-demo.ts @ 9fe69a75dcd3 ; 검색·중복 제거 L103~165<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446 | placeholder와 trim/lowercase/includes 확인. |
| 50 | 앞뒤 공백·영문 대소문자를 구별하지 않는 부분 검색이다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/features/live-map/lib/live-map-demo.ts @ 9fe69a75dcd3 ; 검색·중복 제거 L103~165<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446 | placeholder와 trim/lowercase/includes 확인. |
| 51 | 3. 가운데 오른쪽 화살표를 눌러 「대상 설비」로 옮긴다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·추가·제거 L272~315<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/features/live-map/lib/live-map-demo.ts @ 9fe69a75dcd3 ; 검색·중복 제거 L103~165 | 추가 handler 및 Eqp ID Map 중복 제거 확인. |
| 51 | 같은 설비는 중복 추가되지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·추가·제거 L272~315<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/features/live-map/lib/live-map-demo.ts @ 9fe69a75dcd3 ; 검색·중복 제거 L103~165 | 추가 handler 및 Eqp ID Map 중복 제거 확인. |
| 52 | 4. 「Eqp ID」·「공정」·「웨이퍼 모드」·「현재 상태」를 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·추가·제거 L272~315<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 대상 표·입력 L551~575<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446 | 열 순서와 행 삭제 handler 확인. |
| 52 | 빼려는 설비는 그 행의 「삭제」 단추를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·추가·제거 L272~315<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 대상 표·입력 L551~575<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446 | 열 순서와 행 삭제 handler 확인. |
| 54 | 대상이 비면 「좌측 트리에서 설비를 체크한 뒤 화살표 버튼으로 추가해 주세요.」가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446 | 빈 대상 안내와 시작 비활성화 확인. |
| 54 | 테스트에는 한 대 이상이 필요하다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 트리·대상 표 L625~714<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 문구 L426~446<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작 비활성화 L858~869 | 빈 대상 안내와 시작 비활성화 확인. |

### 설비만 연결·MES만 연결·모두 연결 고르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 66 | 「시뮬레이션 진행 현황」 오른쪽에서 연결 범위를 고른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 초기 선택 L109~113<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 연결 선택값 L468~470<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 연결 선택 위치 L718~747 | 초기값 E2E와 표시값 모두 연결, 렌더 위치 확인. |
| 66 | 처음 선택값은 「모두 연결」이다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 초기 선택 L109~113<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 연결 선택값 L468~470 | 초기값 E2E와 표시값 모두 연결, 렌더 위치 확인. |
| 70 | \| 설비만 연결 \| 설비 쪽 시나리오를 실행하고 중지할 때까지 반복한다. \| | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 연결 선택값 L468~470<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorModeMapper.java @ 985d17c604dd ; toMode L11~21<br>nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 ; of L41~67<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 포트별 완료·반복 L228~259 | EQUIPMENT_ONLY 활성 actor와 UNTIL_STOPPED 반복 사용처 확인. |
| 71 | \| MES만 연결 \| MES 쪽 시나리오를 실행하고 중지할 때까지 반복한다. \| | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 연결 선택값 L468~470<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorModeMapper.java @ 985d17c604dd ; toMode L11~21<br>nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 ; of L41~67<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 포트별 완료·반복 L228~259 | UPSTREAM_ONLY 활성 actor와 UNTIL_STOPPED 반복 사용처 확인. |
| 72 | \| 모두 연결 \| 설비 연결을 확인한 뒤 MES 쪽을 실행한다. 각 포트의 처리를 한 번씩 수행한다. \| | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 연결 선택값 L468~470<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorModeMapper.java @ 985d17c604dd ; toMode L11~21<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 연결 후 MES 활성화 L117~130<br>nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 ; of L41~67<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 포트별 완료·반복 L228~259 | ALL 연결 gate 뒤 MES 활성화와 ONCE의 포트별 terminal 집계 확인. |

### 시나리오를 고르고 테스트 시작하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 85 | 1. 시나리오 목록에서 실행할 항목을 고른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·시작 단추 L751~870<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 선택·시작 문구 L450~465 | 목록 name(v version)·선택 name/commInterface 렌더 확인. |
| 85 | 목록에는 시나리오와 버전이 표시되고, 옆 배지에는 선택한 시나리오와 통신 방식이 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·시작 단추 L751~870<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 선택·시작 문구 L450~465 | 목록 name(v version)·선택 name/commInterface 렌더 확인. |
| 86 | 2. 시나리오 목록은 연결 범위별로 걸러지지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 자동 선택 L173~185<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunScenarios.ts @ 9fe69a75dcd3 ; 전체 목록 조회 L6~13 | getScenarios에 mode 인자를 주지 않고 첫 항목 fallback 사용. |
| 86 | 대상 설비에 맞는 시나리오인지 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 자동 선택 L173~185<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunScenarios.ts @ 9fe69a75dcd3 ; 전체 목록 조회 L6~13 | getScenarios에 mode 인자를 주지 않고 첫 항목 fallback 사용. |
| 86 | 기존 선택이 없으면 첫 항목이 자동 선택된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 자동 선택 L173~185<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunScenarios.ts @ 9fe69a75dcd3 ; 전체 목록 조회 L6~13 | getScenarios에 mode 인자를 주지 않고 첫 항목 fallback 사용. |
| 87 | 3. 「테스트 시작」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·시작 단추 L751~870<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 선택·시작 문구 L450~465 | disabled 조건과 pending 번역 문구 확인. |
| 87 | 대상이 없거나 시나리오가 없으면 비활성화되며, 요청 중에는 「시작 요청 중...」으로 바뀐다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택·시작 단추 L751~870<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 선택·시작 문구 L450~465 | disabled 조건과 pending 번역 문구 확인. |
| 89 | 성공 응답 뒤 「Run 시작됨:」과 실행 번호·대상 대수가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작 요청 L318~340<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 응답 문구 L980~980<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 실행 생성·비동기 연결 L81~109 | 동기 createRun 응답과 백그라운드 연결 작업 분리 확인. MES 활성화는 모두 연결에 적용된다. |
| 89 | 「모두 연결」의 설비 연결 확인·MES 활성화는 이어서 진행되므로 「시뮬레이션 진행 현황」에서 결과를 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작 요청 L318~340<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 응답 문구 L980~980<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 실행 생성·비동기 연결 L81~109 | 동기 createRun 응답과 백그라운드 연결 작업 분리 확인. MES 활성화는 모두 연결에 적용된다. |

### 입력 칸으로 고칠 수 없는 시작 거부 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 101 | 서버는 연결 범위·시나리오·한 개 이상의 빈 값 없는 설비 ID를 요구한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapSimulatorRunCreateRequest.java @ 985d17c604dd ; 필수값 L12~20<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorTargetAssembler.java @ 985d17c604dd ; 설비·포트 조회 L31~55<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택값 전달 L318~340<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 입력 화면 L625~870 | 요청 DTO의 모든 필수 annotation, 등록 통신 설정과 portIds 추출 및 데모 입력 전체 확인. |
| 101 | 선택한 설비의 통신 방식·연결 모드·포트 정보는 등록 정보에서 가져오며 데모 창에는 수정 칸이 없다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/LiveMapSimulatorRunCreateRequest.java @ 985d17c604dd ; 필수값 L12~20<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorTargetAssembler.java @ 985d17c604dd ; 설비·포트 조회 L31~55<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 선택값 전달 L318~340<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 입력 화면 L625~870 | 요청 DTO의 모든 필수 annotation, 등록 통신 설정과 portIds 추출 및 데모 입력 전체 확인. |
| 103 | 「설비를 찾을 수 없습니다.」 또는 「테스트 가능한 포트가 없습니다.」가 포함되면 설비 ID·발생 시각·표시 문구를 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/SimulatorTargetAssembler.java @ 985d17c604dd ; 설비·포트 조회 L31~55 | 서버 예외 두 문구가 실제 존재한다. 현장에서 고칠 입력 칸이 없어 인계한다. |
| 105 | 테스트 시작에서 「설비가 이미 다른 Run 에 점유되어 있습니다.」가 나오면 문구에 포함된 설비와 실행 번호를 기록한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 점유 거부 문구 L152~166<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 시나리오 조회·대상 확보 L115~138 | 테스트 시작에서 나온 문구로 한정한다. 409 변환 문구와 simulator의 lease 확보 호출 확인. 원인 진단 절차는 범위에서 제외한다. |
| 105 | 실행이 남은 이유를 확인하는 절차는 `00.rag/user/demo-troubleshooting.md`에서 다룬다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 점유 거부 문구 L152~166<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 시나리오 조회·대상 확보 L115~138 | 테스트 시작에서 나온 문구로 한정한다. 409 변환 문구와 simulator의 lease 확보 호출 확인. 원인 진단 절차는 범위에서 제외한다. |

### 실행 줄에서 진행 현황 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 115 | 「시뮬레이션 진행 현황」에는 과거 실행과 실행 중인 항목이 함께 나오며, 새 실행은 아래쪽에 놓인다. | nori-tc-ui-front/src/features/live-map/lib/live-map-demo-run-history.ts @ 9fe69a75dcd3 ; 정렬·완료 집계 L18~34<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunList.ts @ 9fe69a75dcd3 ; 목록 갱신 L7~15 | createdAt 오름차순·서버 전체 목록·3,000ms refetch 확인. |
| 115 | 창이 열린 동안 3초 간격으로 다시 조회한다. | nori-tc-ui-front/src/features/live-map/lib/live-map-demo-run-history.ts @ 9fe69a75dcd3 ; 정렬·완료 집계 L18~34<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunList.ts @ 9fe69a75dcd3 ; 목록 갱신 L7~15 | createdAt 오름차순·서버 전체 목록·3,000ms refetch 확인. |
| 117 | 실행 줄에서 실행 번호·연결 범위·시나리오·「시작 시각」·상태를 읽는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실행 줄 L896~940<br>nori-tc-ui-front/src/features/live-map/lib/live-map-demo-run-history.ts @ 9fe69a75dcd3 ; 정렬·완료 집계 L18~34<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 시각 L450~450 | 헤더 배지의 COMPLETED 대상 수/전체 대상 수, 상세 사이클 열 확인. |
| 117 | 상태 옆 숫자는 완료 설비 수 / 전체 설비 수이다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실행 줄 L896~940<br>nori-tc-ui-front/src/features/live-map/lib/live-map-demo-run-history.ts @ 9fe69a75dcd3 ; 정렬·완료 집계 L18~34<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 시각 L450~450 | 헤더 배지의 COMPLETED 대상 수/전체 대상 수, 상세 사이클 열 확인. |
| 117 | 처리 사이클 수는 실행 줄을 펼쳐 설비별로 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실행 줄 L896~940<br>nori-tc-ui-front/src/features/live-map/lib/live-map-demo-run-history.ts @ 9fe69a75dcd3 ; 정렬·완료 집계 L18~34<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 시각 L450~450 | 헤더 배지의 COMPLETED 대상 수/전체 대상 수, 상세 사이클 열 확인. |

### 실행 줄을 펼쳐 설비별 처리 결과 보기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 127 | 실행 줄의 화살표나 실행 번호 영역을 누르면 설비별 표가 열린다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 펼치기·대상 표 L904~975<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 초기 접힘 L120~121 | expandedRunIds 초기 빈 Set과 헤더 클릭 handler 확인. |
| 127 | 처음에는 접혀 있다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 펼치기·대상 표 L904~975<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 초기 접힘 L120~121 | expandedRunIds 초기 빈 Set과 헤더 클릭 handler 확인. |
| 129 | 표는 「Eqp ID」·「연결 유형」·「통신」·「현재 상태」·「처리 결과 (완료/오류)」·「종료」 순서이다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 설비별 열 L578~611<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 펼치기·대상 표 L904~975<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 문구 L435~447 | 대상 열 5개와 action 열, count accessor 확인. |
| 129 | 「처리 결과 (완료/오류)」는 설비별 완료 사이클 수 / 오류 사이클 수를 표시한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 설비별 열 L578~611<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 펼치기·대상 표 L904~975<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 문구 L435~447 | 대상 열 5개와 action 열, count accessor 확인. |

### 전체 실행 또는 설비 한 대 중지하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 141 | 1. 실행 하나의 모든 설비를 멈추려면 그 실행 줄의 「전체 종료」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 요청 L344~378<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 단추 조건 L928~968<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 종료 문구 L981~985<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 전체·설비별 중지 L264~320 | group.runId로 stopRun 호출, 해당 실행만 대상. |
| 142 | 2. 한 대만 멈추려면 실행 줄을 펼치고 해당 설비 행의 「종료」 단추를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 요청 L344~378<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 단추 조건 L928~968<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 전체·설비별 중지 L201~243<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 전체·설비별 중지 L264~320 | runId+eqpId로 stopTarget 호출, 형제 target 상태를 변경하지 않는다. |
| 142 | 같은 실행의 다른 설비는 계속 진행한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 요청 L344~378<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 단추 조건 L928~968<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 전체·설비별 중지 L201~243<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 전체·설비별 중지 L264~320 | runId+eqpId로 stopTarget 호출, 형제 target 상태를 변경하지 않는다. |
| 143 | 3. 종료 요청 문구가 나온 뒤 실행과 설비의 「현재 상태」를 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 요청 L344~378<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 단추 조건 L928~968<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 종료 문구 L981~985 | 종료 요청 메시지와 갱신 상태 렌더를 확인하도록 안내한다. |
| 145 | 종료 요청 중이거나 이미 끝난 실행·설비의 종료 단추는 비활성화된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 요청 L344~378<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 단추 조건 L928~968<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRun.ts @ 9fe69a75dcd3 ; 종료 상태 판정 L4~7 | terminal 판정 및 요청 중 Set 기반 비활성화, 실행별 runId 호출 확인. |
| 145 | 「전체 종료」는 다른 실행 줄까지 한꺼번에 중지하는 단추가 아니다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 요청 L344~378<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 단추 조건 L928~968<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRun.ts @ 9fe69a75dcd3 ; 종료 상태 판정 L4~7 | terminal 판정 및 요청 중 Set 기반 비활성화, 실행별 runId 호출 확인. |

### 데모 창을 닫았다가 진행 현황 다시 보기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 154 | 「데모 시작」 창을 닫으면 대상 설비·검색어·연결 범위·시나리오 선택이 초기화된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 닫기·입력 초기화 L228~245 | handleClose는 선택 상태를 reset하고 종료 API를 호출하지 않는다. |
| 154 | 창 닫기는 실행 중지 요청을 보내지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 닫기·입력 초기화 L228~245 | handleClose는 선택 상태를 reset하고 종료 API를 호출하지 않는다. |
| 156 | 「데모 시작」을 다시 열면 서버의 실행 목록을 다시 읽는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 목록 재조회 L198~226<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunList.ts @ 9fe69a75dcd3 ; 서버 목록 조회 L7~15 | open 시 refetch 및 서버 목록 query 사용, 기존 실행을 표시한다. |
| 156 | 계속 실행 중인 항목을 찾아 상태를 확인하거나 종료한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 목록 재조회 L198~226<br>nori-tc-ui-front/src/features/live-map/hooks/useSimulatorRunList.ts @ 9fe69a75dcd3 ; 서버 목록 조회 L7~15 | open 시 refetch 및 서버 목록 query 사용, 기존 실행을 표시한다. |

### 시나리오 목록과 진행 현황 새로고침하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 167 | 시나리오 선택 목록 오른쪽의 원형 화살표 그림에 마우스를 올리면 「새로고침」이 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 두 목록 갱신 L198~209<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 새로고침 단추 L791~805<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 단추 문구 L451~453 | title과 시나리오/실행 목록 동시 refetch, isRefreshing disabled 확인. |
| 167 | 누르면 시나리오 목록과 실행 진행 현황을 함께 다시 조회한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 두 목록 갱신 L198~209<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 새로고침 단추 L791~805<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 단추 문구 L451~453 | title과 시나리오/실행 목록 동시 refetch, isRefreshing disabled 확인. |
| 167 | 조회 중에는 단추가 비활성화된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 두 목록 갱신 L198~209<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 새로고침 단추 L791~805<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 단추 문구 L451~453 | title과 시나리오/실행 목록 동시 refetch, isRefreshing disabled 확인. |
| 169 | 시나리오 목록이 비어 있으면 「시나리오 없음」·「시나리오 미선택」으로 표시되고 테스트를 시작할 수 없다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 빈 시나리오·시작 제한 L751~870<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시나리오 미선택 L465~465 | 빈 목록 SelectItem·선택 배지·시작 disabled 연결 확인. |

### 시나리오 파일 업로드하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 180 | 1. 시나리오 목록 오른쪽의 위쪽 화살표 그림에 마우스를 올려 「시나리오 업로드」인지 확인하고 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 파일 선택·단추 L808~832<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 업로드 문구 L456~462 | Upload 아이콘·tooltip·파일 input click 연결 확인. |
| 181 | 2. UTF-8 시나리오 Markdown 파일을 선택한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 업로드 결과 L380~396<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 파일 선택·단추 L808~832<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; UTF-8 읽기·빈 파일 거부 L107~132 | input accept와 multipart readBytes/UTF-8 디코딩, 내용 사전 검사 없음 확인. |
| 181 | 파일 선택 창은 `.md`와 텍스트 파일을 표시하며, 화면에서 파일 내용은 검사하지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 업로드 결과 L380~396<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 파일 선택·단추 L808~832<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; UTF-8 읽기·빈 파일 거부 L107~132 | input accept와 multipart readBytes/UTF-8 디코딩, 내용 사전 검사 없음 확인. |
| 182 | 3. 성공하면 「시나리오 업로드됨:」 뒤에 시나리오가 표시되고, 갱신한 목록에서 업로드한 항목이 선택된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 업로드 결과 L380~396<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 업로드 문구 L456~462 | 성공 시 refetch 후 created.scenarioId 선택 및 이름 메시지 확인. |
| 184 | 서버는 빈 파일·시나리오 형식 오류·이미 등록된 시나리오 ID를 거부한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; UTF-8 읽기·빈 파일 거부 L107~132<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 검증·중복 거부 L73~95 | 빈 파일 검사, parser 검증, existsByScenarioId 충돌 시 insert 전 거부 확인. |
| 184 | 기존 ID의 파일을 다시 올려 덮어쓰는 방식은 제공하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; UTF-8 읽기·빈 파일 거부 L107~132<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 검증·중복 거부 L73~95 | 빈 파일 검사, parser 검증, existsByScenarioId 충돌 시 insert 전 거부 확인. |

### 업로드 실패 문구별로 파일 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 201 | 업로드 실패는 「테스트 시작 실패」 창의 문구로 구별한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 업로드 전달 L89~92<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115<br>nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 업로드 오류 전달 L145~170<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 업로드 실패·표시 L380~396<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | 업로드 API→공통 HTTP 오류 변환→ApiResponse→ApiError→같은 실패 창으로 전달됨을 확인했다. |
| 203 | - 「업로드 파일이 비어 있습니다.」이면 내용이 있는 파일을 선택한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 빈 파일·읽기 실패 L125~132<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | 빈 파일 검사에서 정확한 문구로 거부하며 내용 있는 파일 선택을 안내한다. |
| 204 | - 「업로드 파일을 읽지 못했습니다:」 뒤에는 읽기 실패 사유가 붙는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 빈 파일·읽기 실패 L125~132<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | IOException 사유를 콜론 뒤에 덧붙여 전달한다. |
| 204 | 해당 문구와 파일을 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; 빈 파일·읽기 실패 L125~132<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | IOException 사유를 콜론 뒤에 덧붙여 전달한다. |
| 205 | - 형식 오류는 「simulator orchestrator 호출에 실패했습니다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 형식 검사·ID 중복·409 응답·ID 헤더 L73~133<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; 형식 오류의 400 응답 L18~23<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | parser 오류의 400 응답이 일반 status=400 문구로 변환되고 화면에 표시된다. |
| 205 | status=400」으로 표시된다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 형식 검사·ID 중복·409 응답·ID 헤더 L73~133<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; 형식 오류의 400 응답 L18~23<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | parser 오류의 400 응답이 일반 status=400 문구로 변환되고 화면에 표시된다. |
| 205 | 시나리오 형식을 고쳐 다시 올린다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 형식 검사·ID 중복·409 응답·ID 헤더 L73~133<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java @ 5e9b02e3ae16 ; 형식 오류의 400 응답 L18~23<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 오류 문구 응답 L89~115<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | parser 오류의 400 응답이 일반 status=400 문구로 변환되고 화면에 표시된다. |
| 206 | - 「설비가 이미 다른 Run 에 점유되어 있습니다.」 뒤 본문에 SCENARIO_CONFLICT와 「이미 존재하는 시나리오입니다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 형식 검사·ID 중복·409 응답·ID 헤더 L73~133<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/ScenarioConflictException.java @ 5e9b02e3ae16 ; 중복 ID 문구 L9~10<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 업로드 전달 L89~92<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | 기존 ID는 409 SCENARIO_CONFLICT와 실제 ID 사유를 반환한다. 409 공통 점유 접두문 뒤에 원문이 보존된다. 파일 scenario-id 헤더를 변경해야 새 ID로 등록된다. |
| 206 | scenarioId=」 및 ID가 있으면 ID 중복이다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 형식 검사·ID 중복·409 응답·ID 헤더 L73~133<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/ScenarioConflictException.java @ 5e9b02e3ae16 ; 중복 ID 문구 L9~10<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 업로드 전달 L89~92<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | 기존 ID는 409 SCENARIO_CONFLICT와 실제 ID 사유를 반환한다. 409 공통 점유 접두문 뒤에 원문이 보존된다. 파일 scenario-id 헤더를 변경해야 새 ID로 등록된다. |
| 206 | 파일의 scenario-id를 새 ID로 바꿔 업로드한다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 형식 검사·ID 중복·409 응답·ID 헤더 L73~133<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/ScenarioConflictException.java @ 5e9b02e3ae16 ; 중복 ID 문구 L9~10<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 업로드 전달 L89~92<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 오류 문구 변환 L152~168<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 보존 L19~77<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창 L988~1003 | 기존 ID는 409 SCENARIO_CONFLICT와 실제 ID 사유를 반환한다. 409 공통 점유 접두문 뒤에 원문이 보존된다. 파일 scenario-id 헤더를 변경해야 새 ID로 등록된다. |

### 올린 시나리오 삭제하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 219 | 저장소 기본 권한 데이터 기준으로 삭제에는 LIVE_MAP_DEMO_RUN_SCENARIO_DELETE가 필요하다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 삭제 기본 권한 L187~193<br>nori-tc-exec/docs/db_table/patch/postgres_2026_06_22_add_live_map_demo_run_scenario_delete_permission.sql @ f1d5e0a8e727 ; 삭제 권한 연결 L28~39<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 권한 판정 L127~131<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 단추 L835~855 | 삭제 기본 데이터의 DELETE PREFIX와 hasPermission 조건부 렌더 확인. |
| 219 | 이 권한이 없으면 「시나리오 삭제」 그림 단추가 나타나지 않는다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 삭제 기본 권한 L187~193<br>nori-tc-exec/docs/db_table/patch/postgres_2026_06_22_add_live_map_demo_run_scenario_delete_permission.sql @ f1d5e0a8e727 ; 삭제 권한 연결 L28~39<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 권한 판정 L127~131<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 단추 L835~855 | 삭제 기본 데이터의 DELETE PREFIX와 hasPermission 조건부 렌더 확인. |
| 221 | 목록에서 사용자 업로드 시나리오를 고르고 휴지통 그림의 「시나리오 삭제」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 처리 L399~415<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 단추 L835~855<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 결과 L459~463 | 선택 ID 삭제·refetch 및 ID 포함 완료 문구 확인. |
| 221 | 성공하면 「시나리오 삭제됨:」 뒤에 시나리오 ID가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 처리 L399~415<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 단추 L835~855<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 결과 L459~463 | 선택 ID 삭제·refetch 및 ID 포함 완료 문구 확인. |
| 223 | 기본 시나리오를 선택하면 삭제 단추가 비활성화되고 마우스를 올렸을 때 「기본 시나리오는 삭제할 수 없습니다」가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 삭제 단추 L835~855<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 결과 L459~463<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 기본 시나리오 보호 L98~106 | isSeed disabled·tooltip과 서버 seed flag 거부 확인. |

### 시작·업로드·삭제가 거부됐을 때 창 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 237 | 시작·업로드·삭제가 실패하면 「테스트 시작 실패」 창이 열린다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창·확인 L988~1003<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 우선·대체 문구 L19~77<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 제목 L464~464 | 세 handler가 startError에 넣고 같은 Dialog를 사용하며 ApiError는 서버 errorMsg를 우선한다. |
| 237 | 서버 오류 문구가 있으면 그 문구를 표시하고, 없으면 조작에 따라 다음 문구를 표시한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창·확인 L988~1003<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 우선·대체 문구 L19~77<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 제목 L464~464 | 세 handler가 startError에 넣고 같은 Dialog를 사용하며 ApiError는 서버 errorMsg를 우선한다. |
| 239 | - 테스트 시작: 「Demo 테스트 시작에 실패했습니다.」 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 기본 문구 L969~971 | 시작 fallback 한국어 문구 일치. |
| 240 | - 업로드: 「시나리오 업로드에 실패했습니다.」 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 기본 문구 L969~971 | 업로드 fallback 한국어 문구 일치. |
| 241 | - 삭제: 「시나리오 삭제에 실패했습니다.」 | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 기본 문구 L969~971 | 삭제 fallback 한국어 문구 일치. |
| 243 | 실패 문구와 대상 설비·시나리오·조작 시각을 기록한 뒤 「확인」으로 닫는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창·확인 L988~1003<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 제목 L464~464 | 공통 실패 창의 확인 단추와 제목을 확인했다. 인계 정보는 조작 대상과 시각이다. |
| 243 | 업로드·삭제 오류에도 창 제목은 「테스트 시작 실패」로 표시된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시작·시나리오 실패 L338~413<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실패 창·확인 L988~1003<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 제목 L464~464 | 공통 실패 창의 확인 단추와 제목을 확인했다. 인계 정보는 조작 대상과 시각이다. |

| 245 | 업로드·삭제에서 「설비가 이미 다른 Run 에 점유되어 있습니다.」가 보이면 뒤에 붙은 응답 본문의 code로 구별한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 시나리오 전달·공통 변환 L89~168<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 보호 거부 응답 L98~119<br>nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 업로드·삭제 오류 전달 L145~183 | 409 공통 점유 접두문 뒤 응답 본문의 code를 확인한다. 삭제의 SEED_PROTECTED는 기본 시나리오 보호이며 일반 화면에서는 단추도 비활성화된다. |
| 245 | 삭제의 SEED_PROTECTED는 기본 시나리오 보호 거부이므로 삭제를 중단한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 시나리오 전달·공통 변환 L89~168<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java @ 5e9b02e3ae16 ; 보호 거부 응답 L98~119<br>nori-tc-ui-front/src/features/live-map/api/live-map-demo-run.api.ts @ 9fe69a75dcd3 ; 업로드·삭제 오류 전달 L145~183 | 409 공통 점유 접두문 뒤 응답 본문의 code를 확인한다. 삭제의 SEED_PROTECTED는 기본 시나리오 보호이며 일반 화면에서는 단추도 비활성화된다. |

### 종료 실패나 실행 정체를 담당자에게 알리기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 255 | 종료가 실패하면 진행 현황 영역의 메시지에 서버 문구가 나오고, 없으면 「Run 종료 요청에 실패했습니다.」가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 실패 L344~370<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 결과 메시지 영역 L875~878<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 종료 실패 문구 L981~985 | 종료 실패는 startError popup이 아니라 startMessage 배지, 서버 문구 우선 및 fallback 확인. |
| 255 | 실행 번호·설비 ID·종료 요청 시각·표시 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 실패 L344~370<br>nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 결과 메시지 영역 L875~878<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 종료 실패 문구 L981~985 | 종료 실패는 startError popup이 아니라 startMessage 배지, 서버 문구 우선 및 fallback 확인. |
| 257 | 시작 뒤 대기가 이어지거나 의도하지 않게 실행이 중지되면 실행 줄과 설비별 상태·완료/오류 수를 기록한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 실패 L344~370<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 연결 실패 후 중지 L117~150 | 연결 실패 시 백그라운드 rollback 경로 확인. 진단 문서로의 소유 경계만 남긴다. |
| 257 | 대기 초과·실행 실패·남은 실행의 원인 구별은 `00.rag/user/demo-troubleshooting.md`에서 다룬다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 종료 실패 L344~370<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; 연결 실패 후 중지 L117~150 | 연결 실패 시 백그라운드 rollback 경로 확인. 진단 문서로의 소유 경계만 남긴다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 코드 | 본문 반영 |
|---|---|---|---|
| 업로드 실패 원인의 범위 | U §7.4.4 L2040은 파일 규칙 불일치로 설명한다. | LiveMapController L125~132의 빈 파일·읽기 실패, ScenarioCatalogController L73~95의 parser·ID 중복, API 권한 검사가 모두 실패를 만들 수 있다. | 형식 오류만 원인이라고 단정하지 않고 서버 검사와 공통 실패 표시를 분리했다. |
| 목록 조회 실패 문구의 화면 표시 | U §7.4.4 L2039는 「Demo 시나리오 목록을 조회하지 못했습니다.」와 새로고침을 안내한다. | LiveMapDemoModal L132~143는 query error를 받지 않으며 L198~209는 rejected promise만 확인한다. queryObserver L325~351은 기본 refetch 오류를 throw하지 않고 반환한다. | 해당 문구가 반드시 화면에 표시된다는 설명은 제외했다. 새로고침의 실제 조회 동작과 빈 목록 표시는 남겼다. |
| 실행 줄의 처리 건수 | U §7.4.3 L2017~2019는 실행 줄의 처리 건수로 설명한다. | live-map-demo-run-history.ts L18~34에서 COMPLETED 설비 수/전체 설비 수를 만든다. 별도 cycle count는 LiveMapDemoModal L605~609에서 표시한다. | 실행 줄은 완료 설비 수/전체 설비 수, 펼친 표는 완료/오류 사이클 수로 구별했다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

- 현장 배포 판본·시나리오 실제 목록·계정 실제 권한·현재 실행 상태는 조회하지 않았다. 운영 담당자가 소유하며 저장소 소스 기준 절차만 작성했다.
- 목록 조회 실패 기본 문구가 현재 화면에 직접 표시된다는 주장은 렌더 경로가 뒷받침하지 않아 제외했다. 화면 담당자가 query 오류의 표시 계약을 확인할 대상이다.
- 업로드 시 parser의 원문 line/key 오류가 화면까지 그대로 전달된다는 설명은 제외했다. HttpSimulatorOrchestratorClient L165~166은 400을 일반 status 문구로 변환한다. 화면·서버 연동 담당자가 소유한다.

## 다른 문서가 소유해 넣지 않은 사실

- `00.rag/user/demo-troubleshooting.md`: 대기 초과·실행 실패·남은 실행의 원인 분류, 고아 실행 판별·진단·복구. 이 문서는 화면 관찰·인계 정보와 주제 안내만 쓴다.
- `00.rag/user/equipment-commands-and-polling.md`: 설비 시작·종료·재시작·재연결과 PLC 폴링 조작. 데모 정상 경로에서 별도 수동 명령을 선행 조건으로 만들지 않았다.
- `00.rag/user/deployment-workflow.md`: 모델 등 변경의 배포 등록·실행·이력. 데모 절차에 배포를 추가하지 않았다.
- `00.rag/user/live-map-status.md`: Live-Map의 보기 전환·배치 조회·라인 목록 권한·설비 상태 해석. 이 문서는 진입에 필요한 LIVE_MAP_READ와 데모 단추 위치만 짧게 둔다.
- `00.rag/user/permission-code-reference.md`, `00.rag/user/state-reference.md`, `00.rag/user/error-message-and-code-reference.md`: 코드별 종합 뜻풀이. 본문은 해당 조작의 필수 권한·관찰 문구만 기재한다.
- 관련 기존 원문은 읽기 전용으로 확인했다. 다른 문서에서 수정이 필요하다고 확정한 사항은 없다.

## 최초 작성 검사 결과(보완 전 이력)

- 실행 환경: GNU bash 5.2.37(1)-release (x86_64-pc-msys), Java Temurin 21.0.9+10-LTS.
- 검사기 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/demo-execution.md`
- 1차: exit 1, 원천 줄 범위 4건. SimulatorModeMapper 22줄·요청 DTO 20줄·SimulatorTargetAssembler 55줄·이력 변환 34줄의 실제 길이에 맞게 인용을 수정했다. 원문 내용은 보존하고 필요한 코드 근거를 추가했다.
- 2차: exit 0. 이후 사람 대조에서 MES 활성화 설명을 「모두 연결」 조건으로 명시하고 최종 3차 검사를 수행했다. 3차: exit 0. 대상 위반 0, CROSS VIOLATION 0, MASK 출력 0. H2 15개, 자식 48개, 최대 자식 128자, 최대 부모 326자이다. 실행 당시 전체 검색 폴더 집계는 13개 파일이며 다른 세션의 문서를 이 세션 완료물로 세지 않는다.
- 최종 3차 검사 마지막 출력 10줄:
```text
    L214-214 목록 항목 32  | - 테스트 시작: 「Demo 테스트 시작에 실패했습니다.」
    L215-215 목록 항목 26  | - 업로드: 「시나리오 업로드에 실패했습니다.」
    L216-216 목록 항목 24  | - 삭제: 「시나리오 삭제에 실패했습니다.」
    L218-218 문단 79  | 실패 문구와 대상 설비·시나리오·조작 시각을 기록한 뒤 「확인」으로 닫는다. 업로드·삭제 오류에도 창 제목은 「테스트 시작 실패」로 표시된다.
§ L220-230 [23] 종료 실패나 실행 정체를 담당자에게 알리기   parent=238 children=2 sources=4
    L228-228 문단 109  | 종료가 실패하면 진행 현황 영역의 메시지에 서버 문구가 나오고, 없으면 「Run 종료 요청에 실패했습니다.」가 나온다. 실행 번호·설비 ID·종료 요청 시각·표시 문구를 운영 담당자에게 전달한다.
    L230-230 문단 128  | 시작 뒤 대기가 이어지거나 의도하지 않게 실행이 중지되면 실행 줄과 설비별 상태·완료/오류 수를 기록한다. 대기 초과·실행 실패·남은 실행의 원인 구별은 00.rag/user/demo-troubleshooting.md에서 다룬다.
SUMMARY sections=15 children=48 maxChild=128 maxParent=326
CORPUS docs=13/53 sections=237 part={user=13} type={사용 절차=13}
TOTAL files=13 targets=1 violations=0
```

### 최초 작성 사람 검사(보완 전 이력)

최초 작성 당시 15절 전부를 읽었다. 이후 메인 세션이 업로드 409 표시의 누락 1건을 확인했으므로 당시 자체검사만으로 현재 보완 완료를 판정하지 않는다. 각 절의 제목만으로 질문을 알 수 있는지, 원천 일치, 독립 답, 다른 절·앞선 문서 중복 여부를 아래에 대응했다.

| 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 중복·소유 경계 |
|---|---|---|---|---|
| Live-Map에서 데모 시작 창 열기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 데모 조회·시작·종료에 필요한 권한 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 라인 트리에서 대상 설비 준비하기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 설비만 연결·MES만 연결·모두 연결 고르기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 시나리오를 고르고 테스트 시작하기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 입력 칸으로 고칠 수 없는 시작 거부 조건 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 실행 줄에서 진행 현황 읽기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 실행 줄을 펼쳐 설비별 처리 결과 보기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 전체 실행 또는 설비 한 대 중지하기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 데모 창을 닫았다가 진행 현황 다시 보기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 시나리오 목록과 진행 현황 새로고침하기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 시나리오 파일 업로드하기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 올린 시나리오 삭제하기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 시작·업로드·삭제가 거부됐을 때 창 읽기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |
| 종료 실패나 실행 정체를 담당자에게 알리기 | 적합 | 위 사실표와 일치 | 적합 | 같은 사실의 답 본문 복제 없음 |

- 제목은 대상 조작별로 작성했으며 같은 제목·식별자를 지운 동일 반복 틀 3회 이상 없음이다. 본문의 표 머리글·값·콜론 구문을 읽고 mask-v1 출력과 대조해 값 손실을 발견하지 않았다.
- 본문 모든 문장을 읽어 이름 토막 뜻풀이·빈 틀 문장·잘린 문장·관리 전용 조치·admin objectKey·오답 사례가 없는지 확인했다. 검사 중 발견한 입력 오타는 최종 검사 전에 수정했다.
- 사용 절차의 각 번호는 원문에 실제 진행 번호로 적었다. 다른 문서와 같은 자식은 검사 결과 0건이고 의미상 겹치는 필수 진입 권한은 이 절차를 시작하는 최소 조건으로만 썼다.
- 제품 시험은 실행하지 않았다. 권한·라우팅·설비 변환·이력 정렬·시나리오 업로드 시험의 코드를 읽어 기대값을 대조했으며 시험 PASS로 보고하지 않는다. 브라우저 실행·네트워크·Docker·서버 접속은 하지 않았다.
- 독립 Agent 호출 0건. 제공된 세션 안에서 직접 조사·작성·검사했다. 최종 검사 wall time은 9.203초이다. provider 사용량·비용과 전체 작업 wall time은 별도 계측하지 않아 미확인이다.
- 검사용 임시 파일·디렉터리·서비스를 만들지 않았다. 일회성 파일 정리 대상과 잔여가 없다. 다음 작업은 메인 세션의 원천 대조이며 업로드는 범위 밖이다.

## 메인 세션 검증 결함 1건 보완(2026-11-18)

- 요청 범위: 업로드·삭제 409와 업로드 400/빈 파일/읽기 실패의 실제 표시 문구, 시작 점유 안내의 범위 및 직접 영향만 보완한다. 제품 코드·다른 문서·서버는 변경하지 않았다.
- 원인: 최초 작성은 simulator의 ID 중복 검사까지만 설명하고 ui-backend의 모든 409 공통 변환이 업로드·삭제에도 적용되는 화면 결과를 누락했다. 시작 거부 절의 안내도 조작 종류를 한정하지 않아 오인 가능성을 남겼다.
- 수정: 시작 점유 문구를 테스트 시작으로 한정하고, 업로드 실패 절을 추가해 빈 파일·읽기 실패·status=400·SCENARIO_CONFLICT의 문구와 새 ID 업로드 조치를 썼다. 기존 거부 창 절에는 응답 본문 code 확인과 SEED_PROTECTED의 보호 거부를 덧붙였다.
- 보완 전 검색 원문 SHA-256: `da87a0158756c88ae94259f8884d89740ac7a14a0398dfdb3857331d53f3adfc` (230줄·15절). 보완 후 SHA-256: `c76cf0037a500734d3bd8d5dd9312eaeb2e003a1730d19ef2e574603c080f9b5` (257줄·16절). 추가 절과 세 개의 거부 창 원천, 범위 한정 문장, code 안내 문장만 역적용하면 보완 전 SHA와 정확히 일치함을 확인했다.
- 보완 전 작성 기록 SHA-256: `575642f5feaada9c36eb74c03ab8b732cd031a631d8575c7728d1132fc836afa`. 기존 사실표 내용은 변경 문장과 줄 번호만 동기화하고 추가 사실표를 삽입했다. 최초 검사 출력은 보완 전 이력으로 보존했다.

### 보완 원천 판본 확인

- nori-tc-simulator: `git -c safe.directory=C:\workspace_nori\nori-tc\nori-tc-simulator -C C:\workspace_nori\nori-tc\nori-tc-simulator status --short -- <아래 파일>` exit 0, stdout 비어 있음. HEAD는 rag-meta의 12자리 판본과 일치한다.
  - `orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/OrchestratorExceptionAdvice.java`
  - `orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/api/ScenarioCatalogController.java`
  - `orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/ScenarioConflictException.java`
- nori-tc-ui-backend: `git -c safe.directory=C:\workspace_nori\nori-tc\nori-tc-ui-backend -C C:\workspace_nori\nori-tc\nori-tc-ui-backend status --short -- <아래 파일>` exit 0, stdout 비어 있음. HEAD는 rag-meta의 12자리 판본과 일치한다.
  - `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java`
  - `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java`
  - `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java`
- nori-tc-ui-front: `git -c safe.directory=C:\workspace_nori\nori-tc\nori-tc-ui-front -C C:\workspace_nori\nori-tc\nori-tc-ui-front status --short -- <아래 파일>` exit 0, stdout 비어 있음. HEAD는 rag-meta의 12자리 판본과 일치한다.
  - `src/features/live-map/api/live-map-demo-run.api.ts`
  - `src/features/live-map/components/LiveMapDemoModal.tsx`
  - `src/shared/lib/api-error.ts`

### 보완 최종 검사

- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/demo-execution.md`
- 종료코드 0, 위반 0건, CROSS VIOLATION 0건, MASK 출력 0건. SUMMARY sections=16 children=54 maxChild=138 maxParent=365
- 최종 검색 원문 편집 후 실행했다. 검사 wall time 11.000초. 보완 중 검사기 호출은 2회이며 첫 검사도 exit 0이었다. 그 뒤 형식 오류의 HTTP 400 원천을 명시하고 최종 검사했다.
- 마지막 출력 10줄:
```text
    L240-240 목록 항목 26  | - 업로드: 「시나리오 업로드에 실패했습니다.」
    L241-241 목록 항목 24  | - 삭제: 「시나리오 삭제에 실패했습니다.」
    L243-243 문단 79  | 실패 문구와 대상 설비·시나리오·조작 시각을 기록한 뒤 「확인」으로 닫는다. 업로드·삭제 오류에도 창 제목은 「테스트 시작 실패」로 표시된다.
    L245-245 문단 114  | 업로드·삭제에서 「설비가 이미 다른 Run 에 점유되어 있습니다.」가 보이면 뒤에 붙은 응답 본문의 code로 구별한다. 삭제의 SEED_PROTECTED는 기본 시나리오 보호 거부이므로 삭제를 중단한다.
§ L247-257 [23] 종료 실패나 실행 정체를 담당자에게 알리기   parent=238 children=2 sources=4
    L255-255 문단 109  | 종료가 실패하면 진행 현황 영역의 메시지에 서버 문구가 나오고, 없으면 「Run 종료 요청에 실패했습니다.」가 나온다. 실행 번호·설비 ID·종료 요청 시각·표시 문구를 운영 담당자에게 전달한다.
    L257-257 문단 128  | 시작 뒤 대기가 이어지거나 의도하지 않게 실행이 중지되면 실행 줄과 설비별 상태·완료/오류 수를 기록한다. 대기 초과·실행 실패·남은 실행의 원인 구별은 00.rag/user/demo-troubleshooting.md에서 다룬다.
SUMMARY sections=16 children=54 maxChild=138 maxParent=365
CORPUS docs=13/53 sections=238 part={user=13} type={사용 절차=13}
TOTAL files=13 targets=1 violations=0
```

### 보완 사람 검사

- 최종 16절을 모두 읽어 제목·독립 답·원천 일치·중복·마스킹·권한 경계를 확인했다. 변경한 시작 거부 절, 추가 업로드 실패 절, 거부 창 절의 문장을 코드와 다시 대조했다. 기존 정상 업로드·삭제 단추 절과 실패 시 표시·조치를 구별했다. 미해소 지적 0건이다.
- SCENARIO_CONFLICT·scenarioId=·scenario-id·status=400·SEED_PROTECTED 및 한국어 오류 문구가 직렬화 본문에 남아 있고 MASK 출력이 없음을 확인했다. 문구를 구현의 실제 결과로 설명했으며 설비 점유를 중복 업로드의 원인으로 단정하지 않는다.
- 자체검사에서 보완 완료로 판단한 것이며 메인 세션의 재검증을 수행했다고 기록하지 않는다. 제품 시험·브라우저·네트워크·업로드는 실행하지 않았다. 독립 Agent 호출 0건, provider 사용량·비용·전체 보완 wall time은 미확인이다.
- 검사용 임시 파일·서비스·디렉터리를 만들지 않아 정리 대상과 잔여는 없다.

# 설비 시작·종료·재연결·PLC 폴링 작성 기록

## 대상

- objectKey: `00.rag/user/equipment-commands-and-polling.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문: `rag-documents/nori-tc/00.rag/user/equipment-commands-and-polling.md`
- 검색 원문 SHA-256: 0db9769076d4a29029907142e1e6d91fdee79b4e9a7a0282af73655b91be5a28
- 검색 원문 줄 수: 252
- 절 수: 17
- 확인일: 2026-11-18(한국 시간)
- 이번 작업은 독립 작성 세션의 자체 검사다. 메인 세션의 원천 대조나 공식 검증을 대신하지 않는다. 제품 실행·업로드·커밋은 수행하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | status --short exit 0, cited files clean |
| nori-tc-ui-backend | 985d17c604dd | status --short exit 0, cited files clean |
| nori-tc-gateway | d3636ab86763 | status --short exit 0, cited files clean |
| nori-tc-exec | f1d5e0a8e727 | status --short exit 0, cited files clean |

각 원천을 인용하기 전에 `git -C <저장소> status --short -- <관련 경로>`를 확인했다. 최종 대조에서는 검색 원문과 작성 기록의 모든 원천 파일 경로를 모아 저장소별로 같은 명령을 다시 실행했다. 4개 저장소의 원천 30개 파일이 모두 clean이고 현재 HEAD와 일치했다. 작업 트리 수정이 없는 파일의 현재 HEAD를 사용한다.

처음 형제 저장소에서 `git rev-parse`를 실행했을 때 소유자가 현재 sandbox 계정과 다르다는 `detected dubious ownership` 오류로 exit 128이었다. 저장소나 전역 Git 설정을 고치지 않고 해당 읽기 명령에만 `git -c safe.directory=<확인한 저장소 절대경로>`를 적용했다. 이후 HEAD·status 조회는 exit 0이었다. `.config/git/ignore` 접근 경고가 있었으나 status 본문은 비었고 exit 0이었다. 지정 검사기도 자체 프로세스 한정 safe.directory를 이미 사용한다. 검사기는 수정하지 않았다.

매뉴얼은 nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L734~860, nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1040~1100 전체를 읽었다. 작성 규칙과 53개 목록은 전체를 읽었고, 겹치는 앞선 `equipment-registration-and-settings.md`의 권한·트리·행·결과 창 소유 범위를 확인했다. 나머지 기존 문서의 명령·폴링 관련 본문도 검색해 중복 여부를 확인했다.

관련 시험은 기대값을 읽었으며 실행한 PASS로 기록하지 않는다.

| 시험 원천 | 읽은 기대값 |
|---|---|
| nori-tc-ui-front/tests/unit/eqp-runtime-state.test.ts @ 9fe69a75dcd3 L44~79 | ACTIVE 상태별 메뉴와 PASSIVE 명령 숨김, 시작 의도와 연결의 네 조합 |
| nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/EqpControllerLifecycleTest.java @ 985d17c604dd L54~105 | start·reconnect가 각각의 서비스로 위임되고 202와 traceId 반환, end 접수 실패의 오류 응답 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimePlcGroupCommandTest.java @ d3636ab86763 L101~130 | 연결된 그룹 시작, 미연결 시작 거부, 미연결 그룹 중단 허용 |
| nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/EqpDirectScenarioServiceTest.java @ 985d17c604dd L312~383 | Gateway 성공 후 그룹 의도 추가·해제, 거부 시 의도 미변경, 빈 그룹 발행 전 거부 |

## 절별 사실 대조

검색 원문 줄 번호는 최종본의 행이다. 같은 줄의 여러 문장은 문장별로 나누어 대조했다. 사용자에게 제시한 인계 정보는 화면에 보이는 대상·입력·결과에 따른 절차 안내이며 실제 장애 원인이나 현장 상태의 관측으로 기록하지 않는다.

### 명령 실행과 결과 조회에 필요한 권한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 20 | 실행에는 기본 데이터의 EQP_WRITE 필요 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L123~140; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L764~768; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L322~444 | 변경 경로는 /api/eqp 아래 POST이며 전 메서드 PREFIX 권한에 연결된다. |
| 20 | 결과 읽기에는 ASYNC_READ 필요 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L15~23; nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 L19~47 | GET /api/async 경로의 기본 권한과 실제 호출을 대조했다. |
| 22 | 메뉴 표시는 권한 보장 아님 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L244~280; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~185 | 메뉴에는 권한 조건이 없고 서버가 요청별로 권한을 판단한다. |
| 22 | 거부 때 명령·결과 조회 권한 확인 및 인계 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~185; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd L277~282 | 입력 오류로 취급하지 않도록 요청 인가 경로에 맞춘 안내다. |

### 시작·종료 메뉴는 어느 표에서 여는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 31 | 설비 정보 행의 오른쪽 메뉴 열기 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L233~245 | ContextMenuTrigger가 표 행을 감싼다. |
| 31 | 상태별 네 명령 표시 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L246~280 | 네 visibility 조건에 따라 실제 영어 문구를 렌더한다. |
| 33 | Gateway 목록에서 시작·종료·재시작 | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 L360~411 | 목록 표 행에는 세 명령만 있다. |
| 33 | Reconnect는 한 대의 설비 정보 표 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L273~279; nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 L385~411 | 상세에는 재연결 항목이 있고 목록에는 없다. |
| 33 | 트리의 관리 메뉴와 구별 | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 L180~191 | 트리 설비 메뉴는 Info·Model Update 및 Delete다. 시작 명령 경로를 혼동하지 않도록 했다. |

### 연결 상태에 따라 어떤 명령이 보이는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 41 | ACTIVE는 연결·시작 의도로 분기 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L44~74 | ACTIVE 여부, LOCAL/REMOTE 연결, STARTED 비교를 사용한다. |
| 41 | PASSIVE에는 네 명령 없음 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L54~60; nori-tc-ui-front/tests/unit/eqp-runtime-state.test.ts @ 9fe69a75dcd3 L53~55 | 네 플래그를 모두 false로 반환한다. |
| 45 | 미연결·비STARTED는 Start | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73 | !connected와 !started를 사용하므로 ENDED만이라고 축소하지 않았다. |
| 46 | 미연결·STARTED는 End·Reconnect | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73; nori-tc-ui-front/tests/unit/eqp-runtime-state.test.ts @ 9fe69a75dcd3 L65~68 | 실제 행과 시험 기대값이 일치한다. |
| 47 | 연결되면 End·Restart | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73; nori-tc-ui-front/tests/unit/eqp-runtime-state.test.ts @ 9fe69a75dcd3 L70~78 | 연결된 경우 desiredState와 무관하게 두 플래그가 true다. |

### Eqp Start로 설비 시작하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 60 | Start 메뉴 선택 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L249~255; nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L54~73 | ACTIVE와 표시 조건이 적용된다. |
| 61 | ID 확인 후 동일 단추, 취소는 Cancel | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L65~100; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L602~607 | ID를 확인 문구에 넣고 실제 단추는 영어다. |
| 62 | 진행 후 시작 완료와 ID 확인 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L81~100; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L265~273; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L105~130; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L81~82; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L608~609 | 성공 시 message=null이고 ID가 들어간 완료 문구를 선택한다. |
| 64 | 서버는 이미 STARTED이면 시작 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L375~384 | ENDED일 때만 시작을 허용한다. |
| 64 | 미연결·STARTED는 재연결 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L228~233 | UI가 reconnect 요청으로 보낸다. |

### Eqp End로 설비 종료하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 77 | End 메뉴 선택 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L257~264; nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L54~73 | ACTIVE 설비에 상태별로 표시한다. |
| 78 | 대상 확인 후 End 단추 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L65~100 | ID 확인 문구와 End 실행 단추다. |
| 79 | 완료 제목 확인 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L42~46; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L83~84 | end 성공 제목이다. |
| 79 | 이미 ENDED이면 서버가 거부할 수 있음 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L435~441 | UI는 연결만으로 End를 보여도 서버는 STARTED를 요구한다. |

### Eqp Restart와 Eqp Reconnect는 어떻게 다른가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 90 | 연결 설비 종료 후 성공이면 시작, 다른 결과는 시작 안 함 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247 | endOutcome.kind가 PASS가 아닐 때 즉시 반환한다. |
| 91 | 미연결·STARTED에 재연결 한 번 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L228~233 | END+START 요청을 하지 않는다. |
| 93 | PLC 전체 End 성공 시 그룹 시작 의도 모두 제거 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd L460~465 | 성공한 runner.run 뒤 clearPollGroups를 호출한다. |
| 93 | Restart도 End를 거쳐 같은 영향 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd L460~465 | 실제 두 호출의 연결에 따른 필수 영향이다. |
| 93 | 재시작 후 필요한 그룹 다시 켜기 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd L460~465; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcGroupPollControl.java @ d3636ab86763 L24~31 | 전체 의도가 지워지므로 필요한 그룹을 명시해 시작하도록 안내한다. |

### Eqp Restart로 종료 후 다시 시작하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 106 | 연결 설비 Restart 메뉴 선택 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L54~73; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L265~272 | 재시작 노출 조건이다. |
| 107 | 대상 확인·Restart 단추 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L29~41; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L65~100 | 영어 제목·단추와 ID 포함 확인 문구다. |
| 108 | 두 단계 후 완료 제목 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L48~53; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L85~86 | 정상 연속 실행에서 두 단계 성공 뒤 완료 표시다. 지연 재조회 예외는 별도 절에서 소유한다. |
| 110 | 실패 본문 사유·작업 ID 기록 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L301~307; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L126~130; nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40 | 실패 사유와 있는 경우 ID를 렌더한다. |

### Eqp Reconnect로 끊긴 연결 다시 맺기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 123 | 미연결·시작 의도 유지 ACTIVE 선택 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L54~73 | 재연결 표시 조건이다. |
| 124 | 상세 표의 Reconnect 선택 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L273~279 | 실제 메뉴 위치다. |
| 125 | ID 확인 후 Reconnect 실행 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L29~41; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L65~100; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L228~233 | 이름이 같은 확인 단추에서 단일 reconnect 호출로 연결된다. |
| 126 | 완료 제목 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L54~59; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L87~88 | 재연결 성공 제목이다. |
| 128 | STARTED가 아니면 서버 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L704~712 | EQP_NOT_STARTED 검사를 확인했다. |
| 128 | 오래된 메뉴는 새로고침해 재확인 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73; nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L1221~1232 | 상세 조회의 runtimeState로 메뉴를 결정하므로 최신 상태 확인 안내를 썼다. |

### 시작·종료·재연결 결과가 확인 지연일 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 140 | 세 명령의 지연 제목과 동일 작업 재조회 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1151~1158; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L360~378 | 현재 operationId로 trackOperation만 다시 수행한다. |
| 140 | 지연이며 ID 있을 때만 재조회 표시 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L132~137 | 두 조건을 함께 검사한다. |
| 142 | 단추 없는 지연은 잠시 후 새로고침 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1117~1119; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L132~137 | 안내 문구와 재조회 표시 조건에 맞춘 사용 절차다. |
| 142 | 재조회 중 단추 비활성 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L133~135 | disabled=isRechecking이다. |
| 142 | 결과 확인 후 확인 단추로 닫기 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L138~140 | 확인 단추는 onOpenChange(false)를 호출한다. |

### Restart 확인 지연 뒤에는 무엇을 확인하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 152 | 종료·시작 어느 단계에서나 지연 가능 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L289~297 | 양 단계 모두 runLifecycleStep을 사용하고 같은 action으로 표시한다. |
| 152 | 재조회는 현재 단계만 조회 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L360~378 | 저장된 operationId의 결과만 추적한다. |
| 152 | 종료 지연 후 성공 조회해도 시작 안 함 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L360~378 | 최초 지연에서 함수가 반환되고 recheck에는 start dispatch가 없다. 런타임 실행 시험은 하지 않았다. |
| 154 | 재시작 완료 제목이어도 실제 연결 확인 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L265~273; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L368~372; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L48~53 | recheck의 PASS는 기존 restart action을 그대로 success로 만든다. |
| 154 | 종료로 남으면 현재 Start 메뉴 사용 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L379~384 | 최신 상태가 Start 조건인 경우의 절차다. |
| 154 | 단계 불명 때 대상·시각·ID·문구 인계 | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L48~53 | 화면 결과는 restart 제목과 현재 ID만 보여 단계 구별이 없으므로 필요한 관측값을 인계한다. |

### 명령이 실패하거나 운영 확인 필요로 끝났을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 165 | 네 명령 실패 제목·본문 서버 문구 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L81~88; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L301~307 | 실패 결과는 outcome.errorMsg를 사용한다. |
| 165 | 사유 없으면 기본 실패 문구 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L301~307; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L105~111 | hook fallback 및 결과 대화상자 fallback 경로를 확인했다. |
| 165 | 실패에는 확인만 표시 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L132~140 | 재조회는 delayed 전용이다. |
| 167 | 명령별 운영 확인 제목·인계 정보 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1151~1158; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L277~285; nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40 | 복구 사유 우선·작업 ID 표시와 운영 담당자 안내를 확인했다. |

### 화면에서 고칠 칸이 없는 명령 거부 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 179 | 선택 설비 ID·통신 방식 자동 전달 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L201~233 | eqpId는 경로, commInterface는 interfaceType으로 전달된다. |
| 179 | 서버는 설비 존재·필수 통신 방식 검사 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd L444~448; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpLifecycleRequest.java @ 985d17c604dd L18~23; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L322~325 | 기존 설비 조회 및 @Valid·@NotBlank를 대조했다. |
| 179 | 확인 창에는 수정 입력칸 없음 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L65~103 | 설명·오류·진행·두 단추만 있다. |
| 181 | 사용 불허 시 시작·재연결 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L375~377; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L704~706 | equipmentInfo.enabled를 검사한다. 화면 Enabled 색 점과 같은 뜻으로 설명하지 않았다. |
| 181 | 새로고침·대상 확인 후 지속 거부 인계 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 L65~103; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L336~347; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd L444~448 | 사용자가 입력을 고칠 수 없는 창이므로 현재 대상을 확인하고 오류를 전달하도록 안내했다. |

### PLC 폴링 그룹을 입력하기 전 준비할 것

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 192 | PLC만 정보 표 아래 Polling Group | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L1221~1243; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L80~82 | 조건부 렌더와 영역의 위치·제목을 확인했다. |
| 192 | 그룹 messageName 직접 입력 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L85~92; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcGroupPollControl.java @ d3636ab86763 L24~31 | 자유 입력값으로 등록 스펙 그룹을 찾는다. |
| 194 | 앞뒤 공백 제거 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L38~42 | trim() 결과를 발행한다. |
| 194 | 빈 그룹 오류 문구 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L38~42; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L518~519 | 공백만인 값도 빈값으로 거부한다. |
| 194 | 목록·사전 존재 검사 없고 모르면 모델 담당자 확인 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L35~70; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L85~113; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcGroupPollControl.java @ d3636ab86763 L24~31 | UI는 빈값만 검사하고 서버가 그룹 스펙을 찾는다. 실제 현장 그룹 값은 만들지 않았다. |

### Start로 PLC 그룹의 주기 읽기 켜기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 206 | 선행 시작·연결 확인, 사용 허용·의도·연결·그룹 검사 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L980~1005; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimePlcGroupCommandTest.java @ d3636ab86763 L101~121 | enabled, STARTED, active channel, startGroup 반환을 모두 확인했다. |
| 207 | 그룹 입력 후 Start | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L85~103; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L773~809; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L405~419 | UI→API→controller 요청 경로를 대조했다. |
| 208 | 입력·두 단추 비활성 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L85~113; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L419~422 | 가드와 mutation pending이 표시를 제어한다. |
| 210 | 성공 시 그룹에 정해진 주기로 읽기 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcGroupPollControl.java @ d3636ab86763 L24~31; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 L69~83 | 그룹의 periodMs로 타이머를 시작한다. |

### End로 PLC 그룹의 주기 읽기 끄기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 223 | 중단할 그룹 입력 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L59~65; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L85~92 | 동일 입력값을 End 핸들러에 넘긴다. |
| 224 | End 단추와 그룹 하나 대상 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L104~113; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L816~852; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L430~444 | 그룹명이 요청 경로에 포함된다. |
| 224 | 서버 성공 시 해당 그룹 읽기·의도 해제 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L1018~1028; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 L86~95; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd L380~391 | 타이머를 제거하고 성공 뒤 그룹 의도를 remove한다. |
| 226 | 미연결·미실행 그룹도 중단 허용 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L1018~1028; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 L86~95; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimePlcGroupCommandTest.java @ d3636ab86763 L124~130 | 시작의 연결 검사가 없으며 없는 타이머 제거도 허용한다. |
| 226 | 그룹 End와 설비 Eqp End 구별 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L257~263; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L104~113 | 서로 다른 단추·명령·대상이다. |

### PLC 폴링의 초록색 완료 문구를 읽을 때 주의할 점

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 236 | 그룹 포함 시작·중단 완료를 초록 표시 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L47~65; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L116~126; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L520~521 | 성공 분기의 실제 문자열과 색을 확인했다. |
| 236 | 실패·복구 필요·지연도 완료 표시 가능 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L47~65; nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 L124~142 | hook이 outcome을 상위로 반환하지 않고 dispatched만 반환한다. FAIL·REPAIR_REQUIRED·DELAYED는 예외가 아니라 정상 반환이므로 컴포넌트 성공 분기로 간다. 정적 흐름 확인이며 런타임 재현을 주장하지 않는다. |
| 238 | 재조회·작업 ID 표시 없음 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L75~128; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453 | 폴링 UI 전체에 해당 렌더가 없고 resume=null이다. |
| 238 | 실제 결과 필요 시 식별 정보로 운영 확인 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L35~70; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453 | 이 화면만으로 결과를 구분할 수 없으므로 보이는 대상·입력·문구·시각을 전달하는 안내다. |

### PLC 폴링 영역에 오류가 표시됐을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 250 | 요청·조회 예외는 빨간 오류 | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L789~809; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L832~852; nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 L133~135; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L67~70; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L116~126 | 예외가 전달되는 경우와 오류 렌더를 연결했다. |
| 250 | 전달 오류 우선, 없으면 기본 문구 | nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L522~522 | Error.message가 유효하면 그대로 쓰고 그 밖에는 fallback이다. |
| 252 | 별도 재시도 단추 없음 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L85~126 | Start·End와 결과 문단만 있다. |
| 252 | 권한 거부는 관리자 확인 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~185; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L15~23; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L132~140 | 명령 권한과 조회 권한을 구별한다. |
| 252 | 나머지는 실제 적용 확인 후 재명령 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L67~70 | 서버 결과가 화면에 손실되는 제한과 조회 예외 가능성을 고려한 인계 안내다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 코드 | 검색 원문 처리 |
|---|---|---|---|
| Start 메뉴 표시 | U 4.5.2는 미연결·종료 의도일 때 Start라고 한다. | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L67~73은 미연결·비STARTED를 사용한다. null·미상도 UI에서 Start일 수 있으며 서버는 ENDED만 허용한다(nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L379~384). | 메뉴 표는 ‘시작 의도가 아님’으로 쓰고 서버 거부 조건과 구별했다. |
| 폴링 완료 표시 | U 4.12.3·4.13.3은 성공 시 초록 완료라고 설명한다. | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453이 결과 kind를 상위로 전하지 않고 nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L47~65가 발행 여부로 완료 표시한다. | 완료 문구가 실제 성공의 충분한 근거가 되지 않는 제한과 인계 절차를 작성했다. |
| Restart 지연 후 재조회 | U 4.6.3은 종료 후 시작, U 3.4는 지연 시 다시 조회를 안내하나 두 흐름이 끊기는 예외는 설명하지 않는다. | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247에서 종료 지연이면 시작을 생략한다. nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L360~378의 재조회는 시작을 이어 실행하지 않으면서 기존 restart action의 성공 제목을 쓸 수 있다. | 종료 단계 지연 이후 현재 연결·Start 메뉴 확인을 명시했다. |
| 오류의 원인 단정 | U 4.5.4·4.7.4는 시작·재연결 실패 제목을 무응답·주소 오류 등 특정 원인과 묶는다. | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L301~307 및 nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L336~347은 여러 서버·요청 오류를 동일 명령 실패 제목으로 전달한다. | 제목만으로 원인을 단정하지 않고 실제 서버 문구와 대상·시각을 인계하도록 했다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 설비의 실제 그룹 값·읽기 주기·명령 성공 여부 | 로컬 소스와 시험 기대값은 현장 설정·실행 결과가 아니다. 실제 설비·서버 접속은 이번 작업에 허가되지 않았다. | 모델 담당자·설비 운영 담당자 |
| ‘설비 시작 실패’·‘설비 재연결 실패’ 제목만으로 무응답·주소 오류를 확정하는 설명 | 실패 제목은 여러 원인을 공유한다. 이 세션에서 특정 현장 원인을 관측하지 않았다. | 설비 연결·명령 진단 담당자 |
| 폴링의 ‘응답 대기 시간이 초과되었습니다.’를 현재 경로의 확정 결과 문구로 안내 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L513~514에는 문자열이 있으나 현재 nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453은 결과를 예외로 바꾸지 않는다. 현재 조회 경로는 nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 L124~142의 결과 kind를 반환한다. | UI 프런트엔드 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에서 확장하지 않은 내용 |
|---|---|
| `00.rag/user/equipment-registration-and-settings.md` | 화면 진입·트리 검색·목록 열·색 점의 전체 뜻, 등록·설정 변경·삭제, 관리 작업 결과 창과 접수 충돌 문구 |
| `00.rag/user/equipment-model-parameter-and-jar-update.md` | 모델·파라미터·Gateway/Business JAR 갱신 절차 |
| `00.rag/user/deployment-workflow.md` | 배포 등록·실행·이력·재실행 |
| `00.rag/user/model-details-and-flow-editing.md` | 모델 상세의 PLC Group 편집·그룹 정의 방법 |
| `00.rag/user/permission-code-reference.md` | 각 권한 코드의 전체 의미·부여 체계 |
| `00.rag/user/state-reference.md` | controlState·desiredState·eqpState 전체 값의 뜻과 상태 전이 |
| `00.rag/user/error-message-and-code-reference.md` | 오류 코드별 전체 발생 조건·뜻 |
| `00.rag/user/equipment-connection-and-command-troubleshooting.md`·`00.rag/user/plc-troubleshooting.md` | 통신 무응답·주소·전원·네트워크·폴링 TIMEOUT 등의 원인별 진단 |
| `00.rag/user/screen-behavior-limitations.md` | 화면 제한의 전체 목록. 이번 문서에는 해당 명령 성공 판정에 필수인 PLC 완료 표시와 Restart 지연의 현재 제한만 썼다. |

## 검사 결과

- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/equipment-commands-and-polling.md`
- 첫 검사: exit 0, 17절, 49자식, 위반 0건. 이후 사람 검사로 비한국어 연결어·전각 마침표를 수정하고, 설비 종료의 PLC 영향 설명 중복과 빈 안내 문장을 제거했다. 첫 검사를 최종본 결과로 재사용하지 않았다.
- 최종 검사 종료코드: 0
- 결과 수집 도중 한 번의 로컬 실행 오류가 있었다. Python에서 `subprocess.run(['bash', <위 check.sh>, '--dump', <objectKey>], capture_output=True, encoding='utf-8')`를 호출한 수집 스크립트가 stdout 디코딩 중 `UnicodeDecodeError: 'utf-8' codec can't decode byte 0xc5 in position 1: invalid continuation byte`를 냈고, 이어 `TypeError: unsupported operand type(s) for +: 'NoneType' and 'str'`로 exit 1이었다. 해당 호출의 검사기 종료코드·원문 stdout은 회수하지 못했으므로 성공 근거로 쓰지 않았다. 확인된 결함은 수집 경로의 디코딩 실패이며 그 바이트를 낸 원인·실행 파일은 당시 결과만으로 확정하지 않는다. 이후 Git Bash 실행 파일 절대경로 `C:/Program Files/Git/usr/bin/bash.exe`를 명시하고 bytes로 회수한 결과를 UTF-8로 해석했으며 검사기 exit 0과 아래 원문을 확인했다. 검사기 수정·설정 변경은 없었다.
- 최종 검사 마지막 10줄:

```text
    L226-226 문단 95  | 그룹 중단은 연결되지 않았거나 그룹 타이머가 이미 멈춘 경우에도 허용한다. 설비 전체를 종료하려면 폴링 영역의 「End」와 구별해 행 메뉴의 「Eqp End」를 사용한다.
§ L228-238 [29] PLC 폴링의 초록색 완료 문구를 읽을 때 주의할 점   parent=244 children=2 sources=4
    L236-236 문단 114  | 입력한 그룹 값과 함께 「시작이 완료되었습니다.」 또는 「중단이 완료되었습니다.」가 초록색으로 표시된다. 확인한 소스 판본에서는 서버 결과가 실패·복구 필요·확인 지연이어도 이 완료 문구가 나올 수 있다.
    L238-238 문단 129  | 폴링 영역에는 결과를 다시 조회하는 단추나 「작업 ID」 표시가 없다. 실제 시작·중단 여부가 필요한 경우 초록색 문구만으로 판단하지 말고 운영 담당자에게 설비 ID·그룹 값·명령·발생 시각·표시 문구를 보내 결과 확인을 요청한다.
§ L240-252 [21] PLC 폴링 영역에 오류가 표시됐을 때   parent=236 children=2 sources=6
    L250-250 문단 103  | 요청이나 결과 조회에서 오류가 발생하면 「Polling Group」 영역에 빨간 문구가 나온다. 전달된 오류 문구를 우선 표시하고 없으면 「폴링 그룹 명령에 실패했습니다.」를 표시한다.
    L252-252 문단 132  | 이 영역에는 별도의 「재시도」 단추가 없다. 권한 거부이면 운영 관리자에게 권한을 확인한다. 그 밖의 오류는 설비 ID·그룹 값·Start 또는 End·발생 시각·문구를 운영 담당자에게 전달해 실제 적용 결과를 확인한 뒤 다시 명령한다.
SUMMARY sections=17 children=48 maxChild=148 maxParent=281
CORPUS docs=5/53 sections=86 part={user=5} type={사용 절차=5}
TOTAL files=5 targets=1 violations=0
```

### 사람 검사 판정

모든 17절을 읽었다. 아래 ‘일치’는 소스·표시 문구와의 자체 대조이며 현장 실행 성공을 뜻하지 않는다.

| 절 제목 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·앞선 문서와 소유 충돌 |
|---|---|---|---|---|
| 명령 실행과 결과 조회에 필요한 권한 | 적합 | 일치 | 적합 | 명령 수행 최소 권한만 남김 |
| 시작·종료 메뉴는 어느 표에서 여는가 | 적합 | 일치 | 적합 | 앞선 트리 사용법 대신 명령 위치만 소유 |
| 연결 상태에 따라 어떤 명령이 보이는가 | 적합 | 일치 | 적합 | 전체 상태 뜻을 복제하지 않음 |
| Eqp Start로 설비 시작하기 | 적합 | 일치 | 적합 | 시작 조작·결과 소유 |
| Eqp End로 설비 종료하기 | 적합 | 일치 | 적합 | 종료 조작·결과 소유 |
| Eqp Restart와 Eqp Reconnect는 어떻게 다른가 | 적합 | 일치 | 적합 | 실행 방식·폴링 의도 영향 소유 |
| Eqp Restart로 종료 후 다시 시작하기 | 적합 | 일치 | 적합 | 재시작 조작·결과 소유 |
| Eqp Reconnect로 끊긴 연결 다시 맺기 | 적합 | 일치 | 적합 | 재연결 조작·결과 소유 |
| 시작·종료·재연결 결과가 확인 지연일 때 | 적합 | 일치 | 적합 | 명령별 제목·재조회 조건만 기재 |
| Restart 확인 지연 뒤에는 무엇을 확인하는가 | 적합 | 일치 | 적합 | 종료 지연 후 시작 누락만 소유 |
| 명령이 실패하거나 운영 확인 필요로 끝났을 때 | 적합 | 일치 | 적합 | 명령별 제목·인계 정보만 기재 |
| 화면에서 고칠 칸이 없는 명령 거부 조건 | 적합 | 일치 | 적합 | 자동 전달값·서버 검사를 다룸 |
| PLC 폴링 그룹을 입력하기 전 준비할 것 | 적합 | 일치 | 적합 | PLC 전용 영역·입력 검사 소유 |
| Start로 PLC 그룹의 주기 읽기 켜기 | 적합 | 일치 | 적합 | 그룹 시작 조건·조작 소유 |
| End로 PLC 그룹의 주기 읽기 끄기 | 적합 | 일치 | 적합 | 한 그룹 종료·설비 종료와 차이 소유 |
| PLC 폴링의 초록색 완료 문구를 읽을 때 주의할 점 | 적합 | 일치 | 적합 | 완료 표시 제한·인계 소유 |
| PLC 폴링 영역에 오류가 표시됐을 때 | 적합 | 일치 | 적합 | 요청·조회 예외·오류 단추 상태 소유 |

- 제목: 정확한 중복 없음. 식별자를 뺀 제목 모양에서 `Eqp …로 …하기` 계열 네 개는 독립 명령의 실제 화면 명칭과 목표 조작을 드러내기 위해 유지했다. ‘에 관한 짧은 확인’·토막 뜻풀이·정보 없는 제목은 없다.
- 본문: 같은 자식 본문·의미 중복을 대조했다. 조건은 독립 절의 실행에 필요한 만큼만 남겼다. PLC 전체 종료 영향은 비교 절 하나에서 소유한다. 오류·지연은 명령 제목과 실제 단추 조건에 한정하고 앞선 관리 작업의 답을 복제하지 않았다.
- 문장·빈 틀: 전각 마침표와 비한국어 연결어를 최종 편집에서 제거했다. PLC 시작 절의 ‘제한은 별도로 확인한다’는 정보 없는 문장을 삭제했다.
- 뜻풀이: STARTED·연결·폴링 의도는 메뉴·서비스·타이머의 사용처로 확인했다. 필드 이름을 단어별로 번역하지 않았다.
- 마스킹: 최종 검사에서 MASK 출력 0건. 값 마스킹으로 필요한 사실이 사라진 곳은 없었다.
- 권한 경계: user 원문에 admin objectKey·내부 접속·DB/Kafka/설정 파일 변경·운영 명령을 넣지 않았다. 권한 거부와 입력·상태 거부를 구별했다.
- 문서 편집 범위: 지정 검색 원문과 이 작성 기록만 만들었다. 다른 문서·제품 코드·설정·SPEC·검사기·목록은 바꾸지 않았다.
- 사실 대조 커버리지: 최종 본문 자식 48개 모두 작성 기록에 연결됨(48/48), 문장·행별 사실 대조 81행. 작성 기록의 원천 인용 179개 모두 실제 파일의 유효 줄 범위에 있고 미해결 표기·메타 임시값은 없다. 최종 최대 자식 148자, 최대 부모 281자다.
- 도구: Python 3.12.10; GNU bash, version 5.2.37(1)-release (x86_64-pc-msys); openjdk version "21.0.9" 2025-10-21 LTS; final checker wall time 4.203s
- 제품 시험·빌드·브라우저 실행·네트워크 접속은 하지 않았다. 문서 작업에 필요한 기계 검사와 소스·관련 시험 기대값 읽기만 수행했다.
- 외부 독립 Agent 호출 0회. provider 사용량·비용은 이 세션 도구에서 확인할 수 없어 미확인이다.
- 임시 검사 파일·브라우저·runner·서비스·컨테이너는 만들지 않았다. 검사 결과는 이 작성 기록에 보존했다. 정리할 세션 소유 임시 자원은 없다.
- 남은 문서 결함 0건, 사용자 결정 필요 0건. 제품 코드에서 확인한 제한은 문서에 반영했으며 제품 결함 수정 완료로 기록하지 않는다.

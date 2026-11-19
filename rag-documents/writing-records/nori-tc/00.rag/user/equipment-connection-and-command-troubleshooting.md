# 「설비 연결·명령 문제 확인」 작성 기록

## 대상

- objectKey: `00.rag/user/equipment-connection-and-command-troubleshooting.md`
- 구획·필요 권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/equipment-connection-and-command-troubleshooting.md`
- 검색 원문 SHA-256: `59cf3c7b3ad372b01aaa37b6807df4f6b99d7ff3800a580ef2bbbce8fe8a9cbe`
- 검색 원문: 11,895바이트, 126줄, H1 1개, H2 8개, 자식 23개
- 확인일: 2026-11-19, 한국 시간
- 작성 범위: 연결 상태·시작 의도·무응답·명령 결과와 조회 오류·확인 지연·운영 확인 필요·새로고침 복원·Gateway JAR reload 뒤 통신 재시작의 구별과 사용자 인계.
- 업로드·제품 수정·Git add/commit/push·서버 접근은 하지 않았다. 독립 호출·하위 Agent는 사용하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-front -C ../nori-tc-ui-front status --short -- gateway ui src tests docs/completion-report/07-user-manual.md docs/completion-report/06-operation-and-installation-guide.md docs/scripts/manual-permission-catalog.json docs/db_table/sample_data/postgres_insert_sample_data.sql` 종료 0, 변경 항목 없음. 인용한 src·tests 파일 포함. |
| nori-tc-ui-backend | 985d17c604dd | 같은 명령의 저장소를 ui-backend로 지정. 종료 0, 변경 항목 없음. 인용한 ui 파일 포함. |
| nori-tc-gateway | d3636ab86763 | 같은 명령의 저장소를 gateway로 지정. 종료 0, 변경 항목 없음. 인용한 gateway 파일 포함. |
| nori-tc-exec | f1d5e0a8e727 | 같은 명령의 저장소를 exec로 지정. 종료 0, 변경 항목 없음. U·O·기본 권한 catalog·SQL 포함. |

각 HEAD는 해당 저장소의 `git rev-parse --short=12 HEAD`로 읽었다. 최초 일반 Git 조회는 sandbox 사용자와 저장소 소유자 차이로 `detected dubious ownership` 오류였다. 이후 정확한 저장소에만 명령 단위 `-c safe.directory=<절대경로>`를 주어 읽었고 전역 Git 설정은 바꾸지 않았다. status 출력에는 사용자 전역 ignore 파일 접근 경고가 있었으며 명령 자체는 종료 0이었다.

수정 이력은 `git show --stat 1c14e091`, `git show 1c14e091 --format= -- gateway/adapter/tc-gateway-kafka-adapter/src/test`, `git show --stat e390bbec`로 확인했다. 이전 실패를 현재 동작으로 옮기지 않고 현재 HEAD의 구현과 시험 기대값을 읽었다. 제품 시험은 실행하지 않았다.

작성 규칙 전체와 문서 목록 전체를 읽었다. 겹치는 앞선 문서는 설비 등록·설정, 명령·폴링, 모델·파라미터·JAR 갱신, 메시지 결과·통신 로그, DLQ·격리·Repair Task, Live-Map 트러블슈팅, 로그인·권한·화면 트러블슈팅을 읽어 소유 범위를 대조했다.

## 절별 사실 대조

각 행은 원문의 해당 문단 안 모든 문장의 주장·확인 동작·인계 정보를 묶어 대조한 것이다. 사용자에게 권하는 대조·기록·인계는 원천이 제공하는 값과 확인 가능한 화면을 조합한 안내이며, 현장 원인이나 실제 복구 성공을 주장하지 않는다. objectKey 안내는 확정 문서 목록의 소유 범위와 앞선 문서의 실제 내용을 대조했다.

### 연결되지 않는 설비에서 먼저 확인할 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L19 | 라인·대상 선택, 통신 방식·모드·IP·Port 대조 | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L151~159 L239~243; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L111~145; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L2539~2543 | 라인 필터와 단건 설비 선택, 실제 렌더 열을 확인했다. 연결 전에 대상·등록값을 대조하도록 안내했다. |
| L21 | ACTIVE 주소 확인, PASSIVE 시작 메뉴 없음과 접속 대기 확인 인계 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L44~60; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L2541~2542; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2400~2401 | ACTIVE 아닌 설비는 명령 가시성이 모두 false다. PASSIVE 내부 경로·설정 파일을 사용자가 조작하도록 쓰지 않고 값 전달로 한정했다. |
| L23 | 등록 수정·시작의 소유 문서 안내 | nori-ai-gateway/docs/tc-rag-document-list.md의 설비 등록·설정 변경·삭제 및 설비 시작·종료·재연결·PLC 폴링 행; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L233~279 | 두 objectKey가 확정 목록과 실제 기존 파일에 존재하고 해당 절차를 소유한다. 조작을 재수록하지 않았다. |

### 주의 상태인데 시작 명령이 보이지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L35 | 노란 점은 끊김+시작 의도 또는 연결 중 RUN·IDLE 이외 상태 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L21~41; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L49~97 L139~145; nori-tc-ui-front/tests/unit/eqp-runtime-state.test.ts @ 9fe69a75dcd3 L29~43 | 단순 색→단일 원인으로 쓰지 않았다. REMOTE+알 수 없는 설비 상태도 attention이라는 시험 기대값을 읽었다. |
| L37 | Reconnect 메뉴로 시작 의도 유지·끊김을 구별, Restart 메뉴는 화면의 연결 판정 | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L50~74; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L249~279; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L375~393 L704~714 | 연결 여부와 desiredState를 함께 사용한다. 시작은 ENDED 조건, 재연결은 STARTED 조건이다. 메뉴는 연결 실측 그 자체가 아니라 화면 상태의 판정으로 표현했다. |
| L39 | 상태 변화 거부 시 최신 표시와 인계 | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L408~424; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L234~250; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L375~393 L704~714 | 화면은 별도 API로 상태를 받고 명령 처리 시 서버가 조건을 다시 검사한다. 불일치 원인을 단정하지 않고 관측한 메뉴·문구·시각을 넘긴다. |

### 연결 표시가 있는데 메시지 응답이 없을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L51 | 최근 알림의 마지막 메시지·시각, 더보기 통신 로그에서 송수신 비교 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L53~83 L211~294; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L396~420; nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 L20~34; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd L35~45 | 실제 단추·제목·시각·direction 렌더와 API를 확인했다. 연결 상태와 로그 조회 결과가 별도 입력임을 바탕으로 둘을 나누어 기록한다. |
| L53 | 한 대·여러 대·발생 시각을 함께 인계 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2393~2407; nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L239~243 | O의 영향 범위 구별을 인계 정보로만 사용했다. 여러 대라는 사실만으로 Gateway 장애를 확정하지 않는다. 설비 목록으로 대상을 구별할 수 있다. |
| L55 | 로그·권한과 SECS·Socket 진단의 소유 문서 | nori-ai-gateway/docs/tc-rag-document-list.md의 메시지 전송 결과·통신 로그 조회, SECS·HSMS, Socket 행; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L120~131 | 확정 objectKey와 통신 방식 열을 대조했다. 프로토콜 증상·해결 절차는 복제하지 않았다. |

### 명령 뒤 실패 창이 뜨면 연결 실패인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L69 | 실패 창은 명령 결과·요청·결과 조회 오류를 모두 포함, 두 조회 문구 구별 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L301~347 L360~378; nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 L19~50; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1107~1108; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd L69~107 | FAIL 결과와 catch의 failure 표시가 같은 결과 창으로 간다. 404는 전용 문구, 그 외 오류에는 서버 사유 또는 기본 조회 실패 문구가 사용된다. 문구 발생 원인을 연결 장애로 합치지 않았다. |
| L71 | 기본 데이터의 명령 EQP_WRITE·조회 ASYNC_READ를 각각 확인 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L15~23 L123~140; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L764~768; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L652~765; nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 L19~27 | POST /eqp와 GET /async가 별도 기본 권한에 연결된다. 현장 실제 권한 부여를 단정하지 않았다. |
| L73 | 조회 오류 후 재명령 전 실제 결과 확인 인계 | nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 L80~84 L124~142; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L360~378; nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40 | 조회 오류가 작업을 취소하거나 실패 확정하지 않는다. 오류의 발생 위치를 모르는 사용자는 관측 정보를 넘기도록 안내했다. 작업 ID는 표시된 경우만 기록한다. |

### 확인 지연과 운영 확인 필요에서 다음 조치가 다른 이유

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L83 | 지연이면 같은 작업을 재조회 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L289~297 L360~378; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L132~140 | delayed+작업 ID 조건에서만 다시 조회 단추가 있다. 새 명령을 발행하지 않고 trackOperation으로 같은 ID를 조회한다. |
| L85 | 운영 확인 필요는 자동 복구 미완료, 사유·ID 전달, 재조회 단추 없음 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L277~285; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L132~140; nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1110~1120 | repairReason 우선, 없으면 errorMsg를 쓰고 복구 안내가 표시된다. 결과 확인 창에서 운영 인계하도록 했으며 내부 복구 조치를 쓰지 않았다. |
| L87 | 재시작 부분 단계 안내는 명령 문서, 결과와 연결 표시를 동반 인계 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247 L360~378; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L139~145; nori-ai-gateway/rag-documents/nori-tc/00.rag/user/equipment-commands-and-polling.md의 Restart 확인 지연 절 | 종료 PASS에서만 시작 단계를 발행한다. 앞선 문서의 부분 단계 설명을 복제하지 않고 확인 경로만 안내했다. |

### 새로고침 뒤 진행 중 명령을 다시 찾을 수 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L96 | e390bbec 포함 판본의 명령 접수 정보 저장, 재진입 delayed 창 복원·재조회 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L187~196 L201~247 L390~409; nori-tc-ui-front/src/shared/lib/pending-operation-store.ts @ 9fe69a75dcd3 L60~78; nori-tc-ui-front/tests/unit/pending-operation-store.test.ts @ 9fe69a75dcd3 L40~69 | 해당 커밋의 변경 파일과 현재 구현을 대조했다. 저장된 lifecycle action·설비·operationId로 창을 복원한다. 자동 재명령·다중 진행 작업 전체 목록으로 쓰지 않았다. |
| L98 | 창 닫기·terminal에서 삭제, 접근 실패·깨진 기록 복원 안 됨, 새로고침 전 기록 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L160~164 L260~263; nori-tc-ui-front/src/shared/lib/pending-operation-store.ts @ 9fe69a75dcd3 L27~84; nori-tc-ui-front/tests/unit/pending-operation-store.test.ts @ 9fe69a75dcd3 L71~98; nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L37~40 | DELAYED 자체는 삭제하지 않는다. localStorage 접근 실패와 잘못된 구조는 null로 처리한다. 화면에 있는 정보만 사용자가 보존하도록 안내했다. |
| L100 | 복원 안 될 때 인계, 수정 포함 판본 확인 요청 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L390~409; nori-tc-ui-front/src/shared/lib/pending-operation-store.ts @ 9fe69a75dcd3 L44~70 | 복원 조건이 충족되지 않으면 창이 안 뜰 수 있다. 현장 판본을 읽을 수 있는 화면을 지어내지 않고 운영 담당자에게 포함 여부 확인을 요청한다. |

### Gateway JAR reload 뒤 연결이 돌아오지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L109 | 1c14e091 포함 판본의 갱신 중 통신 정지·재시작, reload 뒤 실패여도 추가 시도 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L768~824 L838~862; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeExecutor.java @ d3636ab86763 L113~142; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlServiceTest.java @ d3636ab86763 L1142~1211 | desiredState STARTED이고 통신 정지를 수행한 경우 복구를 호출한다. runtimeReloaded가 복구 배제 조건이 아니다. 시험은 startRuntimeIfPossible 2회를 기대한다. 실행 PASS로 보고하지 않는다. |
| L111 | 복구 후에도 원래 오류 throw, 연결과 갱신 결과 별도 확인 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L812~824 L849~885 | 복구 호출 뒤 `throw ex`가 남는다. 복구 재시도 성공을 갱신 성공으로 바꾸지 않는다. 복구 실패는 억제 예외·오류 로그를 남긴다. 연결 회복을 보장하지 않았다. |
| L113 | 미복구 때 설비·갱신·현재 표시 전달, 판본과 실패 단계 확인 요청 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L784~824 L863~885; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L139~145 L249~279 | reload와 통신 재시작이 분리된 처리다. 사용자가 내부 로그를 읽게 하지 않고 운영 담당자에게 단계 구별과 판본 확인을 맡겼다. |

### 연결 문제와 함께 격리·복구 작업 기록이 있을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L124 | 처리실패의 격리·Repair Task 확인과 소유 문서 안내 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L2544~2547; nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 L83~109; nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L48~58; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L177~209 | 실제 두 카드와 각 GET API를 대조했다. 조회 권한·목록 조작은 기존 DLQ 문서가 소유한다. REPAIR_REQUIRED와 Repair Task를 같은 상태로 정의하지 않았다. |
| L126 | 해당 기록과 명령 결과를 연결해 인계 | nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 L49~56; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L177~209; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L2545~2547 | 설비 식별로 항목을 구별한다. 기록이 존재한다는 사실만으로 현재 연결 장애의 확정 원인이라고 쓰지 않았다. 사유·시각과 결과를 함께 넘긴다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 코드 | 반영 |
|---|---|---|---|
| 노란 점으로 연결 실패 분기 | U L2540은 노란색이면 재연결 확인 순서로 보낸다. | eqp-runtime-state.util.ts L28~41은 연결 상태에서도 RUN·IDLE이 아니면 attention으로 표시한다. | 색만으로 끊김을 확정하지 않고 ACTIVE 행 메뉴의 Reconnect·Restart로 구별하도록 보완했다. |

U의 「DLQ 화면」은 실제 화면 명칭 「처리실패」로 썼다. 오류 목록 전체·상태값 사전·권한 코드 뜻은 여기서 재정의하지 않았다. O의 여러 대 무응답→공통 원인 우선 확인은 영향 범위를 함께 인계하는 안내로만 사용했고 확정 원인으로 쓰지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 판본에 두 수정이 포함됐다는 단정, 현장 최초 적용 버전 번호 | 저장소 HEAD만 확인했고 현장 배포 정보는 없다. 본문은 수정 포함 판본 조건과 운영 인계로 한정했다. | 프런트엔드·Gateway 배포 담당 |
| 현재 설비의 원인·연결 성공·JAR 갱신 성공, 현재 IP·Port·가용성 | 현장에 접속하지 않았고 실제 결과가 없다. 값이나 성공을 예시로 만들지 않았다. | 설비 운영 담당 |
| 여러 대 무응답이면 Gateway가 확정 원인이라는 주장 | 영향 범위만으로 원인을 입증할 수 없다. 영향 범위·동시 발생 시각을 인계하도록 썼다. | TC 운영 담당 |
| 같은 Port를 쓰는 설비가 둘이면 언제나 오류라는 일반화 | O는 중복 점검을 안내하나, 이를 모든 통신 모드의 동일한 오류 규칙으로 확인하지 않았다. 이 문서에는 등록값 대조만 남겼다. | Gateway·통신 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 넣지 않은 상세 |
|---|---|
| 00.rag/user/equipment-commands-and-polling.md | 시작·종료·Restart·Reconnect 조작 전체, 결과 제목 목록, 재시작 종료 지연 뒤 수동 시작, PLC 폴링 절차·표시 제약 |
| 00.rag/user/equipment-registration-and-settings.md | 등록·수정 입력 조건 전체와 상태색 사전·모든 열 설명 |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 모델·파라미터·JAR 선택과 적용 절차, 등록 이후 Gateway JAR 변경 화면 제약 |
| 00.rag/user/message-results-and-communication-logs.md | 로그 조회·권한·전체 보기·상세 조작, 메시지 전송 결과 조회 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 목록 권한·검색·정리·Repair Task 상세 필드 |
| 00.rag/user/secs-hsms-troubleshooting.md | HSMS 세션·SELECT·설비 식별 진단 |
| 00.rag/user/socket-troubleshooting.md | 프레임·매치·첫 메시지 유실 진단 |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드별 뜻과 모든 오류 문구의 참조 목록 |
| 00.rag/user/state-reference.md | 상태값의 전체 의미·종류 |
| 00.rag/user/permission-code-reference.md | 권한 코드별 의미·메뉴 연결 전체 |

O L2400~2401의 Gateway 내부 로그·Service·TCPRoute·주소 도달성 등 내부 접근 조치는 user 원문에서 제외하고 운영 담당자에게 대상·통신 모드·시각·화면 결과를 넘기도록 했다. admin objectKey나 관리 문서 열람 지시는 넣지 않았다.

## 검사 결과

검사 명령(3회 모두 동일):

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/equipment-connection-and-command-troubleshooting.md
```

도구: GNU bash 5.2.37(1)-release (x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS. 검사기는 수정하지 않았다.

| 회차 | 종료코드·결과 | 수정 |
|---|---|---|
| 1 | 1. `VIOLATION L26 원천 줄 범위 L29~94 (파일 80줄)` | 실제 읽은 시험 범위를 L29~80으로 정정. 격리 확인 원천도 실제 해당 행인 L2544~2547로 좁히고 상태·DLQ API 근거를 보강했다. |
| 2 | 1. `VIOLATION L55 자식(문단) 길이 204 (1~200)` | 프로토콜 문서 안내를 명확히 하며 늘어난 문장을 196자로 줄였다. |
| 최종 | 0. `TOTAL files=28 targets=1 violations=0` | 마지막 검색 원문 편집 후 검사. MASK 0건, CROSS VIOLATION 0건. |

최종 출력 마지막 10줄:

```text
§ L102-113 [34] Gateway JAR reload 뒤 연결이 돌아오지 않을 때   parent=343 children=3 sources=3
    L109-109 문단 120  | Gateway 1c14e091 수정이 포함된 판본은 시작 의도가 유지된 설비의 통신을 JAR 갱신 중 멈춘 뒤 재시작한다. reload가 성공했어도 통신 재시작이 실패하면 복구 경로에서 재시작을 한 번 더 시도한다.
    L111-111 문단 101  | 이 재시도 뒤에도 원래 갱신 오류는 실패로 전달된다. 연결이 돌아온 것과 JAR 갱신 작업의 성공을 따로 확인한다. JAR 갱신 실패만으로 연결도 계속 끊겨 있다고 판단하지 않는다.
    L113-113 문단 120  | 연결이 돌아오지 않으면 설비 ID·갱신 시각·갱신 결과·현재 색 점과 메뉴를 운영 담당자에게 전달한다. 현장 Gateway 판본의 수정 포함 여부와 reload·통신 재시작 중 실패한 단계를 확인해 달라고 요청한다.
§ L115-126 [27] 연결 문제와 함께 격리·복구 작업 기록이 있을 때   parent=227 children=2 sources=5
    L124-124 문단 131  | 연결 문제가 계속되면 「처리실패」의 「격리 설비」와 「Repair Task」에서 같은 설비의 기록이 있는지 확인한다. 조회 권한과 목록 확인은 00.rag/user/dlq-quarantine-and-repair-task.md를 따른다.
    L126-126 문단 95  | 대상 기록이 있으면 설비 ID·해당 기록의 사유·시각을 명령 결과와 함께 운영 담당자에게 전달한다. 연결 상태, 명령 결과, 격리·복구 기록을 함께 대조할 수 있게 한다.
SUMMARY sections=8 children=23 maxChild=196 maxParent=399
CORPUS docs=28/53 sections=446 part={admin=7, user=21} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=4}
TOTAL files=28 targets=1 violations=0
```

사람 검사: 40절 미만이므로 8절 전부를 읽었다. 아래의 판정은 작성자 자체 확인이며 메인 세션의 독립 원천 검증을 대신하지 않는다.

| 절 | 제목만으로 질문 식별 | 원천 일치 | 독립 답 | 중복·경계 판정 |
|---|---|---|---|---|
| 연결되지 않는 설비에서 먼저 확인할 값 | 적합 | 적합 | 적합 | 대상·통신 값만 남기고 등록·시작 절차를 소유 문서로 연결 |
| 주의 상태인데 시작 명령이 보이지 않을 때 | 적합 | 적합 | 적합 | 색을 단일 원인으로 해석하는 경로를 교정. 명령 전체 표 복제 없음 |
| 연결 표시가 있는데 메시지 응답이 없을 때 | 적합 | 적합 | 적합 | 연결·응답·영향 범위를 구분. 로그·프로토콜 상세 복제 없음 |
| 명령 뒤 실패 창이 뜨면 연결 실패인가 | 적합 | 적합 | 적합 | 요청/조회 오류와 명령 실패를 구분. 필요한 최소 기본 권한만 기재 |
| 확인 지연과 운영 확인 필요에서 다음 조치가 다른 이유 | 적합 | 적합 | 적합 | 재조회와 인계의 선택 조건만 기재. 명령별 실패 문구 목록 없음 |
| 새로고침 뒤 진행 중 명령을 다시 찾을 수 있는가 | 적합 | 적합 | 적합 | 저장·복원·삭제 조건과 판본 인계. 다른 절과 동일 본문 없음 |
| Gateway JAR reload 뒤 연결이 돌아오지 않을 때 | 적합 | 적합 | 적합 | 복구 재시도와 갱신 실패를 구별. 사용자 내부 접근 조치 없음 |
| 연결 문제와 함께 격리·복구 작업 기록이 있을 때 | 적합 | 적합 | 적합 | 기록 동반 인계만 소유. DLQ 상세 절차 복제 없음 |

제목 정확 중복·불필요한 동일 틀 0건, 정보 없는 틀 문장·이름 토막 뜻풀이·깨진 문장·오답 사례 0건으로 확인했다. 23개 자식 모두 마스킹 후 의미가 유지된다. 문서 본문에 표·목록이 없어 표 빈 칸·번호 오류는 해당하지 않는다. 권한 경계·admin objectKey·관리 문서 열람 지시 위반 0건으로 확인했다. 문서 간 같은 자식 본문은 최종 기계 검사에서도 0건이다. 진단에 필요한 최소 공통 조건 외에 앞선 사용 절차를 재수록하지 않았다.

검사용 임시 파일·브라우저·서비스·시험 컨테이너는 만들지 않았다. 검사 출력은 이 기록에 보존했다. 제품 build·단위/통합시험·현장 재현·검색 반영·정답률 측정은 실행하지 않았다. 다음 작업은 메인 세션의 이 두 파일 원천 대조이며 이 세션은 다른 문서나 업로드로 진행하지 않는다. provider 사용량·금액·요청 전체 wall time은 별도 측정하지 않아 미확인이다.

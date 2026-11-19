# Lot·Carrier·작업과 MES·WM 흐름 작성 기록

## 대상

- objectKey: `00.rag/user/lot-carrier-work-and-system-flow.md`
- 구획·권한: user `[AI_DOC_READ]`
- 유형: 개념
- 검색 원문: `rag-documents/nori-tc/00.rag/user/lot-carrier-work-and-system-flow.md`
- 검색 원문 SHA-256: `c79ae6a0617387dd40fdee36217e3af3099e9e63129e24cb298b51caf138ab88`
- 검색 원문 줄 수: 156
- 검색 원문 H2 절 수: 10
- 확인일: 2026-11-19, 한국 시간. 조사는 11월 18일 시작했으며 최종 파일 검사는 11월 19일 수행했다.
- 작성 범위: 개념과 업무 흐름의 소스 대조·문서 작성·자체 검사. 제품 시험·독립 공식 검증·업로드는 수행하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-business | 7db24f7466bd | 본문에서 인용한 코드 31개 경로의 `git status --short -- <경로들>` 출력 없음, exit 0. 관련 시험 경로도 최초 조사에서 `business/tc-business-core/src`·Kafka adapter의 src 범위로 확인했다. |
| nori-tc-gateway | d3636ab86763 | 본문에서 인용한 코드 5개 경로의 상태 출력 없음, exit 0. PLC 경로는 `gateway/tc-gateway-plc-core/src` 범위에서도 확인했다. |
| nori-tc-exec | f1d5e0a8e727 | `docs/completion-report/05-work-scenario.md`·`apps/tc-business-app/config/tc-business.properties` 상태 출력 없음, exit 0. 보조로 읽은 glossary 경로도 수정 상태가 아니었다. |

쓰기 전에 위 원천 범위를 확인했고, 최종 원천 목록으로도 상태를 다시 확인했다. 소유자 검사 때문에 최초 기본 git 조회는 실패했다. 후속 조회는 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C ../<저장소> ...`로 해당 읽기 명령에만 경로를 지정했다. 전역 설정은 변경하지 않았다. `C:/Users/DongGeon/.config/git/ignore` 접근 경고는 있었으나 대상 상태 조회는 exit 0이고 stdout은 비어 있었다.

작성 전 `docs/rag-document-writing-rules.md`와 `docs/tc-rag-document-list.md`를 읽었다. 업무 시나리오는 S2·S3·S5·S8 및 해당 메시지 흐름·작업 상태를 중심으로 코드를 대조했다. 등록·배포·사용자 관리·예외 조작 등 나머지 시나리오는 목록의 다른 소유 문서와 구분했다.

겹치는 기존 문서로 `live-map-status.md`, `message-results-and-communication-logs.md`, `model-details-and-flow-editing.md`, `equipment-commands-and-polling.md`, `demo-execution.md`, `getting-started.md`를 읽었다. 이 문서에는 해당 화면 조작·권한·조회 절차를 옮기지 않았다.

## 절별 사실 대조

### Lot·Carrier·작업은 무엇을 연결하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 21 | Lot은 가공 대상이며 작업 검색·수집 정보를 연결한다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L149~165; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd L46~95 | 시나리오의 Lot 가공과 코드의 matId/lotId를 통한 작업 검색·작업 ID별 수집 규격 저장을 대조했다. |
| 22 | Carrier는 Port의 반입·반출 대상이며 작업과 Port를 연결한다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L142~146, L178~185; nori-tc-business/business/tc-business-domain/src/main/java/com/nori/tc/business/domain/work/BusinessWorkCarrier.java @ 7db24f7466bd L8~17 | 반입·반출 시나리오와 workKey·carrierId·portId의 별도 보관을 확인했다. |
| 23 | 작업은 작업 ID에 Lot·Carrier·레시피·제어/공정 작업을 묶는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd L80~159 | workId·lots·carriers·controljobId·processjobId·recipeId로 생성 명령을 구성하고 생성 결과를 동기화한다. |
| 25 | 설비·Lot으로 먼저 검색하고 실패하면 Carrier로 검색한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkContextResolverSupport.java @ 7db24f7466bd L37~65 | lot 검색 결과가 있으면 반환하고, 없으면 같은 설비의 carrier 검색을 한다. |
| 25 | Lot·Carrier·작업 ID를 구별해 읽는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd L80~109 | workId·lotId·carrierId를 각각 다른 입력에서 읽는다. ID 값이 항상 서로 다르다고 주장하지 않았다. |

### MES 지시와 설비 보고는 어느 경로로 오가는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 37 | MES 지시는 Business·Gateway를 거쳐 설비로 간다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd L43~56; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L104~127; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 L239~250 | MES 구독, EQP 목적지 발행, Gateway의 설비 프레임 구성·전송 큐 연결을 확인했다. |
| 37 | Lot 작업 보고는 설비에서 Gateway·Business로 들어온다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 L79~140; nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 L6~18 | 파싱 메시지의 Business 발행과 Business의 EQP 이벤트 구독 경로를 대조했다. |
| 37 | Business는 작업 반영 후 목적지 정의에 따라 MES 또는 WM으로 보고한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd L40~52; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd L280~295 | 작업 시작 반영 뒤 발행 호출, MES/WM 목적지 분기를 확인했다. |
| 39 | 아래 설명은 모델의 흐름·액션·전송 정의를 전제로 한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L85~108 | 전송 정의가 없으면 발행하지 않으며 정의의 목적지를 선택한다. |
| 39 | 일반 메시지는 일치하는 모델 흐름이 있어야 액션을 실행한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L589~605 | 모델 없음·매칭 없음의 조기 반환 뒤 executor 호출을 확인했다. 연결 공통 보고는 뒤의 독립 절에서 구별했다. |

### 진행 가능 확인과 작업 예약은 어떻게 이어지는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 51 | SOCKET 대표 시나리오 순서이며 메시지 정의가 조건이다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L128~146; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L589~605 | 시나리오의 선행 조건과 실제 모델 매칭을 결합했다. 모든 설비에 강제되는 순서로 표현하지 않았다. |
| 53 | MES의 진행 가능 확인을 설비로 전달한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd L32~37 | MES 액션에서 MesToEqpPublishSupport를 호출한다. |
| 54 | 설비의 확인 응답을 MES에 전달한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/EqpConditionCheckReplyTcAction.java @ 7db24f7466bd L33~47; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd L280~323 | 응답 액션과 MES 대상 발행 경로를 확인했다. |
| 55 | 예약은 기존 설비·Lot 작업이 없으면 만들고 설비에 전달한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd L63~91, L141~163 | 기존 작업 검사, 생성 호출, 예약 원문 설비 전달을 확인했다. |
| 57 | 시작 요청이 먼저 와도 누락 작업을 생성한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd L37~51 | 같은 설비·Lot 검색 결과가 있으면 반환하고 없으면 createWork를 호출한다. |

### Carrier 반입과 Port 보고는 무엇을 알리는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 67 | WO_CAR_LOAD는 Carrier 반입 보고이다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L142~146; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarLoadTcAction.java @ 7db24f7466bd L28~37 | 시나리오의 반입 의미와 같은 이름의 EQP 액션을 대조했다. |
| 67 | Business는 지정 Port를 반입 상태로 바꾼다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarLoadTcAction.java @ 7db24f7466bd L28~37 | portId를 읽어 setLoad를 호출한다. 상태값 상세 목록은 복제하지 않았다. |
| 69 | MES 전송 정의가 WO_CAR_LOAD이면 반입 사실을 MES로 보낸다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L137~169 | MES 메시지 발행 후 메시지 정의 이름을 검사한다. |
| 69 | 별도 Port 상태 정의가 있으면 상태 보고를 예약한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L203~241 | EQP_PORT_STATUS_CHG를 별도로 선택하며 정의가 없으면 반환하고, 있으면 목적지에 따라 예약한다. |
| 69 | 반입 보고와 Port 보고의 보고 내용이 다르다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L151~168, L203~241 | 첫 메시지는 Carrier 반입 사실이고 부가 메시지는 Port 상태이다. 수신 시스템이 반드시 서로 다르다는 의미로 읽히지 않게 표현했다. |

### MES 시작 지시와 설비 시작·종료 보고는 어떻게 다른가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 80 | WO_EQP_START_REQ는 MES의 가공 시작 지시이다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L149~164; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd L37~56 | MES 액션과 시나리오의 지시 주체를 대조했다. |
| 80 | 모델의 설비 전송 정의에 따라 설비로 보낸다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L85~128 | 액션 자체의 직접 발행 대신 공통 자동 발행의 EQP 목적지 분기가 전송한다. |
| 82 | 설비의 STARTED·ENDED는 시작·종료 보고이다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd L40~52; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpEndedTcAction.java @ 7db24f7466bd L40~52 | 서로 다른 EQP 액션으로 등록되어 있다. |
| 82 | 대상 작업을 찾아 시작·종료를 반영한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd L42~49; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpEndedTcAction.java @ 7db24f7466bd L42~49 | 작업이 있을 때 각각 markStart·markEnd를 호출한다. |
| 82 | 시작 지시 수신과 실제 시작 보고를 구별한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd L37~56; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd L40~49 | 지시는 작업 존재 보완, 보고는 시작 반영으로 역할이 다르다. |

### Lot 수집 항목과 수집 결과는 MES와 어떻게 연결되는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 95 | 수집 흐름에서 MES에 규격을 요청한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReqTcAction.java @ 7db24f7466bd L10~24; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L85~108, L137~153 | 요청 액션은 공통 자동 발행에 맡긴다. 모델 정의가 조건이며 시작 요청만으로 항상 실행된다고 쓰지 않았다. |
| 95 | 응답 규격·항목을 설비·Lot 또는 Carrier로 찾은 작업에 연결한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd L46~95 | dcspecId·작업을 검사한 후 eqpId·woId·dcspecId로 수집 상태를 저장한다. |
| 97 | WO_WORK_INFORM 등 값 보고에서 연결된 항목을 모은다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoWorkInformTcAction.java @ 7db24f7466bd L29~42; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd L330~343; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L86~142 | EQP 액션과 dispatch 후 수집 hook, Flow별 연결 항목 조회·작업별 상태 누적 경로를 대조했다. |
| 99 | 수집 규격별 최종 값을 만들어 모델 목적지로 보낸다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd L58~94 | states 순회, resolveFinalValues, 규격별 메시지 구성·발행을 확인했다. |
| 99 | MES 대상 흐름의 결과 수신자는 MES이다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd L280~323 | MES/WM 목적지 선택과 MES publish 호출을 확인했다. |

### 가공 완료와 Carrier 반출 완료는 언제 구별하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 111 | WO_EQP_COMPLETE는 설비의 가공 완료 보고이다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L167~185; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd L40~52 | 시나리오와 EQP 처리기의 이름·주체를 대조했다. |
| 111 | Business는 작업 완료를 반영한 뒤 목적지로 보고한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd L42~52 | 대상 작업이 있으면 markCompleted 후 목적지 발행을 호출한다. |
| 111 | MES 완료 응답 처리기는 수신을 기록한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpCompleteReplyTcAction.java @ 7db24f7466bd L15~21 | 로그 외 작업 종료·삭제 호출이 없다. 액션 등록명은 WO_EQP_COMPLETE이고 입력 메시지는 모델 매칭으로 연결된다. |
| 113 | WO_CAR_UNLOAD_REQ는 이후 Carrier 반출 요청이다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L178~185; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd L46~77 | 대표 시나리오의 완료→반출 순서와 처리기를 확인했다. |
| 113 | Lot·Port 변경 성공 조건에서 반출 상태를 반영·전달한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd L50~77 | Lot 누락·Port 변경 실패 시 반환하고 성공 경로에서 publish한다. |
| 115 | WO_CAR_UNLOAD_COMP_REQ는 반출 완료이다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L182~185; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadCompReqTcAction.java @ 7db24f7466bd L49~77 | 메시지 의미와 전용 EQP 처리기를 대조했다. |
| 115 | Lot·Port 변경 성공 시 Carrier ID 제거·작업 닫기·정리를 한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadCompReqTcAction.java @ 7db24f7466bd L53~75; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd L129~132 | 선행 조건, 작업 검색, markClosed·deleteWork 및 IDLE 시 Carrier ID 제거를 확인했다. |
| 115 | 가공 완료와 반출 완료는 서로 다른 보고이다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd L40~52; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadCompReqTcAction.java @ 7db24f7466bd L49~77 | 처리 이벤트·상태 반영·정리 시점이 다르다. |

### WM은 Lot·설비 흐름에 어떻게 참여하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 127 | WM 이벤트를 Business가 받는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd L42~55 | WM 전용 KafkaListener와 WM 메시지 유형을 확인했다. |
| 127 | 설비 모델 흐름과 목적지 정의에 따라 WM으로 보낸다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L589~605; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L85~108 | 공통 모델 매칭·실행과 WM 발행 분기를 대조했다. |
| 129 | 일반 설비 보고의 목적지는 메시지 정의가 결정한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd L280~295 | MES·WM 분기는 targetType을 사용한다. |
| 129 | WM은 MES와 별도 경로이며 Lot으로 보고를 연결한다. | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 L6~18; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd L74~103 | 구독·발행 토픽이 각각 별도이며 WM 발행에서 lotId를 사용한다. 메시지 키·헤더 형식은 본문에 쓰지 않았다. |

### 연결 때의 Port 보고와 Lot 작업 보고는 무엇이 다른가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 140 | 연결 응답·끊김은 Business 공통 처리 경로이다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L578~605; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncProtocol.java @ 7db24f7466bd L14~26 | 두 전용 handler를 모델 조회보다 먼저 호출하고 반환한다. |
| 140 | Lot 모델 흐름과 별도로 Port별 상태 보고를 만든다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd L219~276; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionDisconnectPortStatusHandler.java @ 7db24f7466bd L112~145 | Port별 루프와 공통 보고 이벤트명을 확인했다. 실제 송신 조건은 같은 절 다음 문장에 적었다. |
| 142 | MES·WM 각각 전송 설정에 따라 보낸다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd L231~235; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionDisconnectPortStatusHandler.java @ 7db24f7466bd L123~127 | 대상별 boolean 조건을 확인했다. |
| 142 | 양쪽 설정이 켜지면 동일 Port 정보를 보낸다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd L226~235, L248~276 | 같은 data를 MES·WM 메시지 생성에 넘긴다. |
| 142 | Port 상태 보고와 특정 Lot 작업 보고를 구별한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd L285~293; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd L40~52 | 연결 data는 설비·Port·Port 상태이며 작업 완료 액션은 별도 대상 작업을 검색·반영한다. |

### PLC의 설비 값은 Lot 업무 메시지와 어떻게 다른가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 154 | PLC 값은 켜진 그룹의 주기로 읽는다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 L69~83, L158~178 | 그룹 타이머의 등록·주기 실행·재예약을 확인했다. |
| 154 | 응답을 변수 값과 그룹 메시지명으로 만든다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 L45~65 | 레지스터별 variableName→decode 결과를 body에 넣고 그룹 메시지명을 지정한다. |
| 154 | 발행 조건을 만족한 응답을 업무 처리로 넘긴다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 L153~171 | decode·map 후 shouldPublish 결과가 참이면 파싱 결과 목록에 추가한다. |
| 156 | MES 업무 메시지는 PLC 값 읽기와 별도 경로이다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L203~218; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd L43~56 | PLC 설비 값 채널과 MES 수신 경로를 구별했다. 완료 응답으로 Lot을 마감한다는 시나리오 문구는 채택하지 않았다. |
| 156 | Business는 대상 설비 모델 흐름을 적용한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L589~605 | 프로토콜별 고정 순서 대신 공통 모델 검색·매칭·실행이다. |
| 156 | SOCKET 보고 순서와 PLC 주기 읽기를 구별한다. | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L128~218; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 L45~65 | SOCKET의 명시적 업무 메시지와 PLC 그룹 응답의 차이를 대조했다. PLC가 항상 SOCKET 보고를 만들어 낸다고 쓰지 않았다. |

## 메시지·처리기·토픽 교차 확인

토픽 이름은 현장값으로 단정하지 않고 저장소 설정의 이름으로 대조했다. 검색 본문에는 메시지 이름과 업무 방향만 두었다.

| 방향 | 저장소 설정의 토픽 | 코드 연결 |
|---|---|---|
| MES → Business | dev.tc-evt | BusinessMesEventKafkaSubscriber L43~56 → BusinessRuntimeTaskSupport L589~605 |
| Business → MES | dev.mat-evt | BusinessMesCommandKafkaPublisher L76~114 |
| Business → Gateway | tc.eqp.commands | BusinessWorkflowMdfAutoPublisher L112~127 → GatewayCommandDispatcher L239~250 |
| Gateway → Business | tc.eqp.events | GatewayEqpEventKafkaPublisher L79~140 → BusinessEqpEventKafkaSubscriber |
| WM → Business | wm.tc.events | BusinessWmEventKafkaSubscriber L42~55 |
| Business → WM | tc.wm.events | BusinessWmCommandKafkaPublisher L74~103 |

위 표의 토픽 값은 `nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 L6~18`과 대조했다. 본문의 메시지명은 대응하는 `@TcAction`과 시나리오를 대조했다. `WO_EQP_COMPLETE_REPLY`는 처리 메서드·입력 이름과 등록 액션 이름이 같지 않으므로 모델 매칭을 조건으로 기술했다.

관련 시험은 실행하지 않고 기대값을 읽어 구현과 대조했다. 다음 근거는 테스트 PASS 실행 기록이 아니다.

| 시험 소스 | 읽은 기대값 |
|---|---|
| nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/workflow/action/MesInboundTcActionT6Test.java @ 7db24f7466bd L181~250, L397~432, L451~460 | 예약의 생성·설비 전달, 기존 작업 재사용, 시작 요청의 누락 작업 생성, 완료 응답 처리기의 예외 없는 반환 |
| nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/workflow/action/EqpInboundTcActionT7Test.java @ 7db24f7466bd L232~244, L269~296, L325~347, L529~558 | 반입·반출 상태, 지연 Port 보고 예약, 시작 반영, 반출 완료의 IDLE·closed·삭제 호출 |
| nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandlerTest.java @ 7db24f7466bd L261~301 | Port별 MES/WM 발행 순서, 동일 업무 data·보고 이벤트명 |
| nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/workflow/BusinessWorkflowPlcNonSecsPathTest.java @ 7db24f7466bd L49~80 | PLC 입력의 메시지명 기반 모델 매칭 경로 |

## 매뉴얼과 코드가 다른 곳

| 주제 | 업무 시나리오 | 코드 | 검색 원문 반영 |
|---|---|---|---|
| MES 완료 응답과 Lot 마감 | nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 L217은 WO_EQP_COMPLETE_REPLY에 대해 Lot 마감을 서술한다. | WoEqpCompleteReplyTcAction @ 7db24f7466bd L15~21은 수신 로그만 기록한다. WoCarUnloadCompReqTcAction L53~75가 Lot·Port 조건 확인 후 찾은 작업을 닫고 정리한다. | L111은 완료 응답 수신 기록, L115는 반출 완료 시 작업 닫기·정리로 나눴다. PLC 절에도 완료 응답이 Lot을 마감한다고 쓰지 않았다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 제외 근거 | 확인할 담당 역할 |
|---|---|---|
| WO_EQP_COMPLETE_REPLY 자체가 Lot을 마감한다는 단계 | 원천 시나리오에는 있으나 대응 MES 액션에 마감 처리가 없다. 위 충돌 표의 코드대로 제외했다. | TC 업무 모델 담당 |
| WO_EQP_START_REQ 수신만으로 시작 응답 → 수집 규격 요청 → 설비 시작 명령이 항상 고정 순서로 실행된다는 주장 | WoEqpStartReqTcAction L37~56은 작업 존재 보완이며 자동 발행은 BusinessWorkflowMdfAutoPublisher L85~108의 모델 전송 정의에 달려 있다. 특정 현장 모델의 완전한 흐름을 코드 상수만으로 확정할 수 없다. | TC 업무 모델 담당 |
| 모든 현장의 현재 MES·WM 적용 모델·설비 진행 상태 | 이번 확인은 현재 저장소 소스와 설정이며 현장 접속·상태 조회를 수행하지 않았다. | 현장 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 제외한 상세 |
|---|---|
| 00.rag/user/tc-components-and-terms.md | Gateway·Business·Shard 등 구성요소의 일반 정의. 본문에는 전달 경로에 필요한 주체만 적었다. |
| 00.rag/user/live-map-status.md | 선택 설비·포트·Carrier 표시와 화면 조작 |
| 00.rag/user/message-results-and-communication-logs.md | 통신 로그 열기·방향·상세 표시·권한 |
| 00.rag/user/model-details-and-flow-editing.md | 흐름·액션·메시지 정의를 화면에서 편집하는 절차 |
| 00.rag/user/equipment-commands-and-polling.md | 연결 명령·PLC 그룹 시작/중단의 화면 절차 |
| 00.rag/user/state-reference.md | 설비·Port·작업의 전체 상태값과 전이별 의미 |
| 00.rag/user/flow-action-and-message-reference.md | 액션 전체 목록·선택값·개별 옵션. 이 문서는 업무 순서를 설명하는 데 필요한 메시지만 사용한다. |
| 00.rag/admin/kafka-topic-and-message-reference.md | 토픽·헤더·키·본문·검증·직렬화 형식. 관리자 문서 경로와 열람 지시를 user 검색 원문에 넣지 않았다. |

## 검사 결과

기계 검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/lot-carrier-work-and-system-flow.md
```

최종 실행은 확인일을 2026-11-19로 갱신한 마지막 편집 이후 수행했으며 exit 0이다. 최종 원문 15,666바이트·156줄·10절·27자식, 최대 자식 155자·최대 부모 365자이다. MASK·CROSS VIOLATION을 포함한 위반은 0건이다. 출력의 코퍼스 수는 그 실행 시점의 공유 작업공간 수치이다.

마지막 출력 10줄:

```text
    L129-129 문단 93  | 일반 설비 보고의 목적지는 모델의 메시지 정의가 MES 또는 WM으로 정한다. WM 연동은 MES와 별도 수신·발신 경로를 사용하며 Lot을 기준으로 보고를 연결한다.
§ L131-142 [33] 연결 때의 Port 보고와 Lot 작업 보고는 무엇이 다른가   parent=257 children=2 sources=5
    L140-140 문단 135  | 연결 시 EQP_PORT_STATUS_SYNC_REP 응답과 연결 끊김의 EQP_DISCONNECTED는 Business의 공통 처리 경로로 들어온다. Lot 작업의 모델 흐름과 별도로 Port별 EQP_PORT_STATUS_CHG를 만든다.
    L142-142 문단 121  | 연결 보고는 MES·WM 각각의 전송 설정에 따라 보낸다. 두 대상이 모두 켜져 있으면 같은 Port 정보를 양쪽에 보낸다. Port 상태 보고는 Carrier 반입·가공 완료 같은 특정 Lot 작업 보고와 구별한다.
§ L144-156 [30] PLC의 설비 값은 Lot 업무 메시지와 어떻게 다른가   parent=246 children=2 sources=6
    L154-154 문단 118  | PLC에서는 TC가 켜진 폴링 그룹의 주기에 따라 설비 값을 읽는다. Gateway는 응답 값을 모델의 변수로 풀고 그룹의 메시지명을 붙인다. 변화가 있는 값 등 발행 조건을 만족한 응답이 업무 처리로 넘어간다.
    L156-156 문단 127  | MES의 Lot 예약·시작 지시는 PLC 값 읽기와 별도 경로로 Business에 들어온다. Business는 대상 설비의 모델 흐름을 적용한다. SOCKET의 Carrier·작업 보고 순서와 PLC의 주기 값 읽기를 구별한다.
SUMMARY sections=10 children=27 maxChild=155 maxParent=365
CORPUS docs=17/53 sections=285 part={user=17} type={개념=4, 사용 절차=13}
TOTAL files=17 targets=1 violations=0
```

도구 확인: Python 3.12 설치 경로의 `python.exe`로 SHA-256·줄 수·NFC·BOM·CR·NUL 및 외국어 문자 범위를 검사했고, Git Bash의 `bash.exe`로 지정된 검사기를 실행했다. 검사기나 관련 규칙은 수정하지 않았다.

사람 검사는 전 10절을 읽어 제목·원천·독립 답·중복을 각각 확인했다.

| 절 | 제목만으로 질문 식별 | 원천 일치 | 독립 답 | 사실 소유·중복 |
|---|---|---|---|---|
| Lot·Carrier·작업은 무엇을 연결하는가 | 적합 | 위 행별 대조 | 적합 | 식별자 관계만 소유 |
| MES 지시와 설비 보고는 어느 경로로 오가는가 | 적합 | 처리기·발행 경로 대조 | 적합 | 전달 경로·모델 조건만 소유 |
| 진행 가능 확인과 작업 예약은 어떻게 이어지는가 | 적합 | MES/EQP 액션 대조 | 적합 | 예약 단계·순서 예외 소유 |
| Carrier 반입과 Port 보고는 무엇을 알리는가 | 적합 | Port 변경·부가 예약 대조 | 적합 | 반입 사실과 Port 보고의 구별 |
| MES 시작 지시와 설비 시작·종료 보고는 어떻게 다른가 | 적합 | 지시·보고 처리기 대조 | 적합 | 시작 지시와 실제 보고의 구별 |
| Lot 수집 항목과 수집 결과는 MES와 어떻게 연결되는가 | 적합 | 규격·누적·최종 보고 대조 | 적합 | 작업별 수집 연결만 소유 |
| 가공 완료와 Carrier 반출 완료는 언제 구별하는가 | 적합 | 완료·응답·반출 조건 대조 | 적합 | 완료와 작업 정리 시점 구별 |
| WM은 Lot·설비 흐름에 어떻게 참여하는가 | 적합 | 독립 구독·발행·목적지 대조 | 적합 | 일반 WM 참여 경로 소유 |
| 연결 때의 Port 보고와 Lot 작업 보고는 무엇이 다른가 | 적합 | 모델 이전 공통 처리·대상별 발행 대조 | 적합 | 연결 공통 보고만 소유 |
| PLC의 설비 값은 Lot 업무 메시지와 어떻게 다른가 | 적합 | 그룹 타이머·응답 변환 대조 | 적합 | 값 읽기와 MES 업무 메시지의 차이 |

제목은 원문 질문에 맞춘 10개이며 같은 제목과 불필요한 반복 틀은 없다. 검색 본문은 다른 절의 답을 복제하지 않았다. Lot·Carrier의 의미를 이름 토막 번역으로 만들지 않았고, 상태 코드 전체 표·화면 절차·내부 접근 조치는 넣지 않았다. 마스킹 전후 필요한 이름·메시지·식별 내용이 유지되는지 dump에서 확인했다. user 원문에 admin objectKey·관리 문서 열람 지시는 없다.

초안에 잘못 섞인 외국어 문장과 메타 표현은 자체검사 중 한국어로 바로잡았다. 마지막 파일에는 일본어·한자 범위 문자가 없음을 별도 검사했다. 원문은 BOM 없는 UTF-8·LF·NFC이며 CR·NUL은 없다.

검사용 임시 파일은 만들지 않았다. 검사기 출력은 실행 결과에서 직접 회수하여 이 기록에 보존한다. 실행 중 프로세스 종료·Docker·kubectl·SSH·네트워크·git add/commit/push·다른 문서 수정은 수행하지 않았다.

최종 보조 검사에서 검색 본문의 자식이 놓인 27개 원문 줄 모두에 사실 대조 행이 있음을 확인했다(누락 줄 없음). 두 파일의 UTF-8·BOM 없음·LF·NFC·CR/NUL 없음과 기록의 원문 SHA 일치를 확인했다. 지정 `work/`는 존재하지만 하위 항목이 없는 것을 확인했으며, 이 세션이 만들지 않은 빈 디렉터리는 보존했다. 새 독립 Agent 호출은 0회이며 provider 사용량·비용은 이 세션에서 확인할 수 없다.

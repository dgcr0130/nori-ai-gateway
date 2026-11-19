# Kafka 토픽·메시지 계약 작성 기록

## 대상

- objectKey: `00.rag/admin/kafka-topic-and-message-reference.md`
- 버킷·구획: `nori-tc`·admin, 필요한 권한 전부 `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 참조
- 확인일: 2026-11-19, 한국 시간
- 검색 원문: `rag-documents/nori-tc/00.rag/admin/kafka-topic-and-message-reference.md`
- 검색 원문 SHA-256·줄 수·절 수는 아래 최종 파일 계측에 기록한다.
- 범위: MES·WM·설비·UI의 토픽, 생산·소비 방향, 레코드 키, JSON 계약. 제품 수정·배포·업로드·커밋은 수행하지 않았다.

## 원천 판본

| 저장소 | 읽은 HEAD 12자리 | 사용 범위 |
|---|---|---|
| nori-ai-gateway | 70cf6cd84db6 | 작성 규칙 전체, 문서 목록 전체, 앞선 검색 원문 |
| nori-tc-exec | f1d5e0a8e727 | I §2·§4.1~4.5·§5의 Kafka 필드, 앱 설정 사본, 기본 시나리오 SQL |
| nori-tc-business | 7db24f7466bd | 토픽 바인딩, 구독·발행, 메시지 계약, 모델 발행, UI 처리기, 연결 Port 보고 |
| nori-tc-gateway | d3636ab86763 | 토픽 설정, 파티션 구독, 프로토콜별 메시지 매퍼, UI 처리기 |
| nori-tc-ui-backend | 985d17c604dd | 발행 매퍼, 명령 발행, 조정 발행 변환, 응답 소비 |
| nori-tc-common | 6fe71f6b2da6 | Kafka envelope·metadata·key 정책, UI 요청·응답 계약 |
| nori-common | ae6791e99e91 | 이벤트 식별자와 스키마 버전 검증 정책 |
| nori-tc-simulator | 5e9b02e3ae16 | 상위 시스템 작업자의 구독, 시나리오 발행 토픽 입력과 metadata 구성 |

최종 원천 상태 목록은 아래에 개별 경로로 기록한다. 각 파일에 `git -C <저장소> status --short -- <상대경로>`를 실행했고 종료코드 0, 변경 출력 없음인 원천만 commit 표기로 인용한다. 다른 OS 소유자로 인한 Git 소유자 경계는 각 읽기 명령에 해당 저장소의 절대경로만 `-c safe.directory=<절대경로>`로 전달했다. 전역 설정은 바꾸지 않았다.

`04-interface-spec.md`의 Kafka 필드 정의는 반복된 필드 행을 동일 내용별로 모아 읽었으며 MES·WM·UI-TASK·EQP-EVENT·EQP-COMMAND 항목을 대조했다. HTTP·설비 wire 규칙의 세부는 다른 소유 문서의 범위로 구분했다.

참고한 시험은 실행 결과가 아니라 기대값을 읽은 것이다. `BusinessKafkaInboundRecordMapperTest`, `BusinessMesCommandKafkaPublisherTest`, `BusinessWmCommandKafkaPublisherTest`, `GatewayEqpEventContractMapperTest`, `KafkaUiTaskCoordinationV2ContractTest`에서 WM 식별자·키, Port 한정 표기, source, UI 필수값·정렬·해시 및 JSON 표기 기대값을 확인했다. Gradle 제품 시험은 실행하지 않았다.


### 경로별 원천 상태 확인

| 원천 경로·판본 | 상태 명령 결과 |
|---|---|
| nori-ai-gateway/docs/rag-document-writing-rules.md @ 70cf6cd84db6 | exit 0·변경 출력 없음 |
| nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 | exit 0·변경 출력 없음 |
| nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaEventTypeNamingPolicy.java @ ae6791e99e91 | exit 0·변경 출력 없음 |
| nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaSchemaVersionPolicy.java @ ae6791e99e91 | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessPortIdWireFormat.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessEqpCommandKafkaMessage.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessMesCommandKafkaMessage.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessWmCommandKafkaMessage.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessEqpEventKafkaSubscriber.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiModelRuntimeCommandService.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisherTest.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisherTest.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 | exit 0·변경 출력 없음 |
| nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyEventType.java @ 6fe71f6b2da6 | exit 0·변경 출력 없음 |
| nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 | exit 0·변경 출력 없음 |
| nori-tc-common/messaging/tc-messaging-domain/src/main/java/com/nori/tc/messaging/domain/kafka/TcKafkaSources.java @ 6fe71f6b2da6 | exit 0·변경 출력 없음 |
| nori-tc-common/messaging/tc-messaging-domain/src/main/java/com/nori/tc/messaging/domain/kafka/key/KafkaMessageKeyResolver.java @ 6fe71f6b2da6 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.test.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-simulator-app/config/tc-messaging.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-simulator-app/config/tc-simulator.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.test.properties @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/docs/completion-report/04-interface-spec.md @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/HsmsBusinessPayloadMapper.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/PlcBusinessPayloadMapper.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayEqpCommandKafkaSubscriber.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayUiEventKafkaSubscriber.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 | exit 0·변경 출력 없음 |
| nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamWorkerService.java @ 5e9b02e3ae16 | exit 0·변경 출력 없음 |
| nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/transport/UpstreamCommandListener.java @ 5e9b02e3ae16 | exit 0·변경 출력 없음 |
| nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiBusinessEventKafkaPublisher.java @ 985d17c604dd | exit 0·변경 출력 없음 |
| nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd | exit 0·변경 출력 없음 |
| nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiKafkaContractMapper.java @ 985d17c604dd | exit 0·변경 출력 없음 |
| nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd | exit 0·변경 출력 없음 |
| nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd | exit 0·변경 출력 없음 |
| nori-tc-ui-backend/ui/tc-ui-domain/src/main/java/com/nori/tc/ui/domain/task/UiTaskStatus.java @ 985d17c604dd | exit 0·변경 출력 없음 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapperTest.java @ 7db24f7466bd | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventContractMapperTest.java @ d3636ab86763 | exit 0·변경 출력 없음 |
| nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/test/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskCoordinationV2ContractTest.java @ 6fe71f6b2da6 | exit 0·변경 출력 없음 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayKafkaContractSupport.java @ d3636ab86763 | exit 0·변경 출력 없음 |

## 절별 사실 대조

검색 원문의 모든 문장·자료 행·목록 항목을 아래 절별 표에 대응시킨다. 같은 줄에 여러 문장이 있으면 문장마다 행을 나눈다. 표 머리글은 해당 자료 행과 함께 읽었다. 원천 위치 칸은 실제 읽은 경로·HEAD·줄 범위이다.

### MES 연동 토픽의 방향과 키

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L17 | 제공 설정의 MES 토픽은 다음과 같다. | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; MES 토픽 L6~21 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L43~56 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; publish L76~114 | 구독 annotation·발행 토픽 설정·키 해석기를 연결해 MES 경계의 방향과 키를 확인했다. |
| L17 | Business는 `tc.business.kafka.mes-events-topic`을 구독하고 `tc.business.kafka.mes-commands-topic`으로 발행한다. | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; MES 토픽 L6~21 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L43~56 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; publish L76~114 | 구독 annotation·발행 토픽 설정·키 해석기를 연결해 MES 경계의 방향과 키를 확인했다. |
| L21 | \| dev.tc-evt \| MES→TC Business \| metadata.correlationId \| | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; MES 토픽 L6~21 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L43~56 / nori-tc-common/messaging/tc-messaging-domain/src/main/java/com/nori/tc/messaging/domain/kafka/key/KafkaMessageKeyResolver.java @ 6fe71f6b2da6 ; resolve L42~56 | 구독 annotation·발행 토픽 설정·키 해석기를 연결해 MES 경계의 방향과 키를 확인했다. |
| L22 | \| dev.mat-evt \| TC Business→MES \| metadata.correlationId \| | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; MES 토픽 L6~21 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; publish L76~114 / nori-tc-common/messaging/tc-messaging-domain/src/main/java/com/nori/tc/messaging/domain/kafka/key/KafkaMessageKeyResolver.java @ 6fe71f6b2da6 ; resolve L42~56 | 구독 annotation·발행 토픽 설정·키 해석기를 연결해 MES 경계의 방향과 키를 확인했다. |

### WM 연동 토픽과 Lot 키

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L32 | Business의 구독 키는 `tc.business.kafka.wm-events-topic`, 발행 키는 `tc.business.kafka.wm-commands-topic`이다. | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; WM 토픽 L9~18 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L42~55 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L74~103 | WM 전용 매핑과 명령 발행에서 lotId 키를 확인했다. 외부 WM 구현의 전체 계약으로 확대하지 않았다. |
| L32 | 제공 설정은 다음과 같다. | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; WM 토픽 L9~18 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L42~55 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L74~103 | WM 전용 매핑과 명령 발행에서 lotId 키를 확인했다. 외부 WM 구현의 전체 계약으로 확대하지 않았다. |
| L36 | \| wm.tc.events \| WM→TC Business \| data.lotId \| | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; WM 토픽 L9~18 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L42~55 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220 | WM 전용 매핑과 명령 발행에서 lotId 키를 확인했다. 외부 WM 구현의 전체 계약으로 확대하지 않았다. |
| L37 | \| tc.wm.events \| TC Business→WM \| 발행 명령의 lotId \| | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; WM 토픽 L9~18 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L74~103 | WM 전용 매핑과 명령 발행에서 lotId 키를 확인했다. 외부 WM 구현의 전체 계약으로 확대하지 않았다. |
| L39 | WM 수신 레코드는 키와 본문의 Lot ID가 같아야 한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220 | WM 전용 매핑과 명령 발행에서 lotId 키를 확인했다. 외부 WM 구현의 전체 계약으로 확대하지 않았다. |

### 설비 이벤트와 설비 명령 토픽

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L50 | 제공 설정의 설비 연동은 Gateway를 경계로 한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 설비 토픽 L90~103 / nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 설비 토픽 L6~15 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; publish L79~140 | Gateway 이벤트 생산과 Business 구독, 반대 방향 명령 발행·Gateway 수신 경계를 확인했다. |
| L50 | 설비의 통신 프레임은 Gateway가 Kafka 메시지로 바꾼다. | nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 설비 토픽 L90~103 / nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 설비 토픽 L6~15 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; publish L79~140 | Gateway 이벤트 생산과 Business 구독, 반대 방향 명령 발행·Gateway 수신 경계를 확인했다. |
| L54 | \| tc.eqp.events \| TC Gateway→TC Business \| 설비 ID \| | nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 설비 토픽 L90~103 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; publish L79~140 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessEqpEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L32~45 | Gateway 이벤트 생산과 Business 구독, 반대 방향 명령 발행·Gateway 수신 경계를 확인했다. |
| L55 | \| tc.eqp.commands \| TC Business→TC Gateway \| 설비 ID \| | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 설비 토픽 L6~15 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; publish L79~129 | Gateway 이벤트 생산과 Business 구독, 반대 방향 명령 발행·Gateway 수신 경계를 확인했다. |
| L57 | Gateway 설정은 `tc.messaging.kafka.topic.eqp-events`·`tc.messaging.kafka.topic.eqp-commands`, Business 설정은 `tc.business.kafka.eqp-events-topic`·`tc.business.kafka.eqp-commands-topic`이다. | nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 설비 토픽 L90~103 / nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 설비 토픽 L6~15 | Gateway 이벤트 생산과 Business 구독, 반대 방향 명령 발행·Gateway 수신 경계를 확인했다. |

### UI 요청과 처리 결과가 오가는 토픽

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L69 | 제공 설정의 UI 메시지는 모두 설비 ID를 레코드 키로 쓴다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; publishInternal L126~150 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiBusinessEventKafkaPublisher.java @ 985d17c604dd ; publish L66~92 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; publishResult L98~137 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; publishResult L83~104 | Backend 발행 두 경로와 Gateway·Business 응답 생산, Backend 응답 구독을 연결했다. |
| L73 | \| tc.ui.events.gateway \| TC UI Backend→TC Gateway \| | nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 토픽 L24~31 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; publishInternal L126~150 | Backend 발행 두 경로와 Gateway·Business 응답 생산, Backend 응답 구독을 연결했다. |
| L74 | \| tc.ui.events.business \| TC UI Backend→TC Business \| | nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 토픽 L24~31 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiBusinessEventKafkaPublisher.java @ 985d17c604dd ; publish L66~92 | Backend 발행 두 경로와 Gateway·Business 응답 생산, Backend 응답 구독을 연결했다. |
| L75 | \| tc.ui.commands \| TC Gateway·TC Business→TC UI Backend \| | nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 토픽 L24~31 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L109~120 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; publishResult L98~137 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; publishResult L83~104 | Backend 발행 두 경로와 Gateway·Business 응답 생산, Backend 응답 구독을 연결했다. |
| L77 | Backend의 `tc.ui.backend.kafka` 아래에서 `gateway-events-topic`·`business-events-topic`·`commands-topic`을 각각 바인딩한다. | nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 토픽 L24~31 | Backend 발행 두 경로와 Gateway·Business 응답 생산, Backend 응답 구독을 연결했다. |

### 격리 시험 설정의 토픽 표기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L86 | 제공된 `.test.properties` 사본은 기본 토픽 앞에 `test.`를 붙인 값을 사용한다. | nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~18 / nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.test.properties @ f1d5e0a8e727 ; 시험 토픽 L23~28 / nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~13 | 실제 test 설정 사본의 토픽 문자열을 대조했다. 현장 설정으로 표현하지 않았다. |
| L88 | - MES: `test.dev.tc-evt`·`test.dev.mat-evt`. | nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~18 / nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.test.properties @ f1d5e0a8e727 ; 시험 토픽 L23~28 | 실제 test 설정 사본의 토픽 문자열을 대조했다. 현장 설정으로 표현하지 않았다. |
| L89 | - WM: `test.wm.tc.events`·`test.tc.wm.events`. | nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~18 | 실제 test 설정 사본의 토픽 문자열을 대조했다. 현장 설정으로 표현하지 않았다. |
| L90 | - 설비: `test.tc.eqp.events`·`test.tc.eqp.commands`. | nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~18 / nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.test.properties @ f1d5e0a8e727 ; 시험 토픽 L23~28 | 실제 test 설정 사본의 토픽 문자열을 대조했다. 현장 설정으로 표현하지 않았다. |
| L91 | - UI: `test.tc.ui.events.gateway`·`test.tc.ui.events.business`·`test.tc.ui.commands`. | nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~18 / nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.test.properties @ f1d5e0a8e727 ; 시험 토픽 L23~28 / nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~13 | 실제 test 설정 사본의 토픽 문자열을 대조했다. 현장 설정으로 표현하지 않았다. |

### JSON 구조와 이벤트 식별자

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L99 | Business가 받는 JSON은 최상위 `metadata`와 `data`가 모두 객체여야 한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; doMap L92~143 | JSON 객체 필수 검사와 eventType 검증 정규식·런타임 메시지 식별자 매핑을 읽었다. |
| L99 | Business는 `metadata.eventType`을 모델 흐름을 찾는 메시지 식별자로 사용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; doMap L92~143 | JSON 객체 필수 검사와 eventType 검증 정규식·런타임 메시지 식별자 매핑을 읽었다. |
| L101 | 이벤트 식별자는 영문 대문자로 시작하고 대문자·숫자를 사용한다. | nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaEventTypeNamingPolicy.java @ ae6791e99e91 ; DEFAULT_EVENT_TYPE_PATTERN·validate L28~86 | JSON 객체 필수 검사와 eventType 검증 정규식·런타임 메시지 식별자 매핑을 읽었다. |
| L101 | 밑줄로 단어를 나눌 수 있으며 연속 밑줄이나 끝 밑줄은 허용하지 않는다. | nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaEventTypeNamingPolicy.java @ ae6791e99e91 ; DEFAULT_EVENT_TYPE_PATTERN·validate L28~86 | JSON 객체 필수 검사와 eventType 검증 정규식·런타임 메시지 식별자 매핑을 읽었다. |
| L101 | 검증식은 `^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$`이다. | nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaEventTypeNamingPolicy.java @ ae6791e99e91 ; DEFAULT_EVENT_TYPE_PATTERN·validate L28~86 | JSON 객체 필수 검사와 eventType 검증 정규식·런타임 메시지 식별자 매핑을 읽었다. |

### MES 수신 헤더와 설비 식별 필드

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L110 | MES 수신의 `metadata`에는 문자열 `eventType`·`timestamp`·`source`·`correlationId`가 필요하다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; createMesMetadata·normalizeSchemaVersion L149~162 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; 기본 버전 L335~340 | MES metadata 생성의 필수값·기본 버전과 실제 설비 ID 별칭 우선순위를 읽었다. |
| L110 | `schemaVersion`을 생략하거나 비우면 `v1`을 적용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; createMesMetadata·normalizeSchemaVersion L149~162 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; 기본 버전 L335~340 | MES metadata 생성의 필수값·기본 버전과 실제 설비 ID 별칭 우선순위를 읽었다. |
| L112 | 설비는 `data.eqpId`를 우선 읽고 `eqpid`·`equipmentId`도 읽는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; createEnvelope·resolveEqpId L253~329 | MES metadata 생성의 필수값·기본 버전과 실제 설비 ID 별칭 우선순위를 읽었다. |
| L112 | 본문에서 찾지 못하면 최상위의 같은 필드를 찾는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; createEnvelope·resolveEqpId L253~329 | MES metadata 생성의 필수값·기본 버전과 실제 설비 ID 별칭 우선순위를 읽었다. |
| L112 | MES 수신에서는 Kafka 키를 설비 ID의 대체값으로 사용하지 않는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; createEnvelope·resolveEqpId L253~329 | MES metadata 생성의 필수값·기본 버전과 실제 설비 ID 별칭 우선순위를 읽었다. |

### WM 수신에서 요구하는 필드

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L121 | TC Business의 WM 수신은 `metadata.eventType`·`metadata.source`를 검사한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; validateWmEnvelope L255~273 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; normalizeSchemaVersion L335~340 | WM 분기의 필수 필드와 비필수 헤더, lotId·eqpId 별칭을 확인했다. |
| L121 | `schemaVersion`은 없으면 `v1`을 적용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; validateWmEnvelope L255~273 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; normalizeSchemaVersion L335~340 | WM 분기의 필수 필드와 비필수 헤더, lotId·eqpId 별칭을 확인했다. |
| L121 | `correlationId`·`traceId`·`timestamp`는 이 수신 검사의 필수 필드가 아니다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; validateWmEnvelope L255~273 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; normalizeSchemaVersion L335~340 | WM 분기의 필수 필드와 비필수 헤더, lotId·eqpId 별칭을 확인했다. |
| L123 | `data`에서 Lot은 `lotId`·`lotid`·`LOTID` 순서로, 설비는 `eqpId`·`eqpid`·`equipmentId` 순서로 찾는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220 | WM 분기의 필수 필드와 비필수 헤더, lotId·eqpId 별칭을 확인했다. |
| L123 | Lot과 설비를 모두 찾아야 모델 처리로 넘긴다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220 | WM 분기의 필수 필드와 비필수 헤더, lotId·eqpId 별칭을 확인했다. |

### MES·WM으로 발행하는 헤더의 차이

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L133 | Business가 발행하는 두 JSON 헤더는 다음 문자열 필드를 사용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessMesCommandKafkaMessage.java @ 7db24f7466bd ; BusinessMesCommandKafkaMetadata L25~36 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessWmCommandKafkaMessage.java @ 7db24f7466bd ; BusinessWmCommandKafkaMetadata L28~39 | 외부로 직렬화하는 전용 record 구성요소와 발행 timestamp 생성식을 대조했다. |
| L137 | \| MES \| eventType·timestamp·source·correlationId \| 네 필드 모두 비어 있지 않아야 한다. \| | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessMesCommandKafkaMessage.java @ 7db24f7466bd ; BusinessMesCommandKafkaMetadata L25~36 | 외부로 직렬화하는 전용 record 구성요소와 발행 timestamp 생성식을 대조했다. |
| L138 | \| WM \| eventType·timestamp·source·workorderId \| 앞의 세 필드는 필수이며 workorderId는 비울 수 있다. \| | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessWmCommandKafkaMessage.java @ 7db24f7466bd ; BusinessWmCommandKafkaMetadata L28~39 | 외부로 직렬화하는 전용 record 구성요소와 발행 timestamp 생성식을 대조했다. |
| L140 | 두 발행용 헤더에는 `schemaVersion` 필드가 없다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessMesCommandKafkaMessage.java @ 7db24f7466bd ; BusinessMesCommandKafkaMetadata L25~36 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessWmCommandKafkaMessage.java @ 7db24f7466bd ; BusinessWmCommandKafkaMetadata L28~39 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; publish L79~114 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L77~103 | 외부로 직렬화하는 전용 record 구성요소와 발행 timestamp 생성식을 대조했다. |
| L140 | `timestamp`는 발행 시점의 `Instant.now().toString()`으로 만든다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessMesCommandKafkaMessage.java @ 7db24f7466bd ; BusinessMesCommandKafkaMetadata L25~36 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessWmCommandKafkaMessage.java @ 7db24f7466bd ; BusinessWmCommandKafkaMetadata L28~39 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; publish L79~114 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L77~103 | 외부로 직렬화하는 전용 record 구성요소와 발행 timestamp 생성식을 대조했다. |

### MES·WM 수신 source 허용값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L149 | TC Business는 설정된 MES·WM 수신 토픽에서 `metadata.source`를 다음 허용 목록과 대조한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; buildAllowlist L399~430 | 원본 source의 정규화와 토픽별 허용 목록을 구별해 확인했다. |
| L153 | \| MES→TC \| TC-EXTERNAL-MES-ADAPTER·TC-EXTERNAL-MES-ADAPTER-APP·WSC-CONSUMER \| | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; buildAllowlist L399~430 / nori-tc-common/messaging/tc-messaging-domain/src/main/java/com/nori/tc/messaging/domain/kafka/TcKafkaSources.java @ 6fe71f6b2da6 ; 외부 source L49~52 | 원본 source의 정규화와 토픽별 허용 목록을 구별해 확인했다. |
| L154 | \| WM→TC \| WM-BUSINESS·WM-BUSINESS-APP \| | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; buildAllowlist L399~430 | 원본 source의 정규화와 토픽별 허용 목록을 구별해 확인했다. |
| L156 | MES 수신은 검증 전에 source의 앞뒤 공백과 호환 접미사 `-APP`를 정리한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; normalizeSource L309~325 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; buildAllowlist L399~430 | 원본 source의 정규화와 토픽별 허용 목록을 구별해 확인했다. |
| L156 | WM 수신은 WM 전용 검증 경로를 사용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; normalizeSource L309~325 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; buildAllowlist L399~430 | 원본 source의 정규화와 토픽별 허용 목록을 구별해 확인했다. |

### TC 내부 메시지의 source 표기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L170 | \| Gateway의 설비 이벤트 \| TC-COMM-GATEWAY-APP \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; GATEWAY_SOURCE L22~22 | 선언된 설정값과 실제 producer metadata 조립 위치를 대조했다. |
| L171 | \| Business의 MES·WM·설비 명령 \| 제공 설정의 TC-BUSINESS-CORE \| | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 업무 발행 source L21~21 | 선언된 설정값과 실제 producer metadata 조립 위치를 대조했다. |
| L172 | \| Backend의 UI 요청 \| 제공 설정의 TC-UI-APP \| | nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 발행 source L65~65 | 선언된 설정값과 실제 producer metadata 조립 위치를 대조했다. |
| L173 | \| Gateway의 UI 응답 \| TC-GATEWAY-APP \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; GATEWAY_SOURCE L1~57 | 선언된 설정값과 실제 producer metadata 조립 위치를 대조했다. |
| L174 | \| Business의 UI 응답 \| 제공 설정의 TC-BUSINESS-APP \| | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; UI 응답 source L64~64 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyMetadata L179~201 | 선언된 설정값과 실제 producer metadata 조립 위치를 대조했다. |
| L176 | source는 메시지 헤더의 발행 주체 표기이다. | nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 업무 발행 source L21~21 / nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 발행 source L65~65 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; GATEWAY_SOURCE L22~22 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; GATEWAY_SOURCE L1~57 / nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; UI 응답 source L64~64 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyMetadata L179~201 | 선언된 설정값과 실제 producer metadata 조립 위치를 대조했다. |

### 모델이 정하는 업무 메시지 본문

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L185 | 일반 업무 메시지의 발행 대상은 모델의 메시지 전송 정의가 `EQP`·`MES`·`WM` 중에서 정한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108 | 모델 targetType 분기와 발행기의 putIfAbsent 처리를 확인했다. |
| L185 | 전송 정의가 만든 본문을 발행기에 넘긴다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108 | 모델 targetType 분기와 발행기의 putIfAbsent 처리를 확인했다. |
| L187 | MES 발행기는 본문에 `eqpId`가 없으면 추가한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L90~111 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L84~100 | 모델 targetType 분기와 발행기의 putIfAbsent 처리를 확인했다. |
| L187 | WM 발행기는 `eqpId`와 `lotId`가 없으면 추가한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L90~111 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L84~100 | 모델 targetType 분기와 발행기의 putIfAbsent 처리를 확인했다. |
| L187 | 그 밖의 본문 필드 묶음은 모델의 전송 정의에 따른다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L90~111 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L84~100 | 모델 targetType 분기와 발행기의 putIfAbsent 처리를 확인했다. |

### MES·WM 업무 헤더의 schemaVersion 허용값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L195 | TC Business는 MES·WM 업무 수신 헤더의 `schemaVersion`에 문자열 `v1` 또는 `1`을 허용한다. | nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaSchemaVersionPolicy.java @ ae6791e99e91 ; coordinationAwarePolicy·validate L37~176 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; 정책 구성 L82~105 | UI 조정 토픽 집합과 업무 토픽의 v1·1 허용 집합을 확인했다. |
| L195 | UI 조정 토픽에는 별도로 두 번째 버전도 허용하는 정책을 구성한다. | nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaSchemaVersionPolicy.java @ ae6791e99e91 ; coordinationAwarePolicy·validate L37~176 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; 정책 구성 L82~105 | UI 조정 토픽 집합과 업무 토픽의 v1·1 허용 집합을 확인했다. |

### MES·WM 송신의 portId 형식

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L204 | Business의 MES·WM 발행기는 `data.portId`가 있으면 문자열로 바꾸고 앞뒤 공백을 제거해 검사한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessPortIdWireFormat.java @ 7db24f7466bd ; withQualifiedPortId L18~52 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 변환 L90~93 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 변환 L84~88 | 소문자 portId만 변환하는 함수의 무값·기존 접두사·신규 접두사 분기를 읽었다. |
| L204 | 값이 비어 있거나 이미 `<eqpId>_P`로 시작하면 본문을 유지한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessPortIdWireFormat.java @ 7db24f7466bd ; withQualifiedPortId L18~52 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 변환 L90~93 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 변환 L84~88 | 소문자 portId만 변환하는 함수의 무값·기존 접두사·신규 접두사 분기를 읽었다. |
| L206 | 나머지 값은 `<eqpId>_P<portId>`로 바꾼다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessPortIdWireFormat.java @ 7db24f7466bd ; withQualifiedPortId L18~52 | 소문자 portId만 변환하는 함수의 무값·기존 접두사·신규 접두사 분기를 읽었다. |
| L206 | `portId`가 없으면 새로 만들지 않는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessPortIdWireFormat.java @ 7db24f7466bd ; withQualifiedPortId L18~52 | 소문자 portId만 변환하는 함수의 무값·기존 접두사·신규 접두사 분기를 읽었다. |

### 연결 Port 보고의 키는 보고 ID를 사용한다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L215 | 연결 때 Port 상태를 동기화한 뒤 발행하는 `EQP_PORT_STATUS_CHG`는 Port별 보고 ID를 사용한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd ; publishPortReports·publishToMes·publishToWm L219~276 | 생산 코드가 넘기는 reportId와 발행기 시험의 correlationId·lotId·key 기대값을 대조했다. |
| L217 | MES 발행은 보고 ID를 `metadata.correlationId`와 레코드 키에 넣는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd ; publishPortReports·publishToMes·publishToWm L219~276 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L93~139 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L154~188 | 생산 코드가 넘기는 reportId와 발행기 시험의 correlationId·lotId·key 기대값을 대조했다. |
| L217 | WM 발행은 보고 ID를 명령의 `lotId`로 넘겨 `data.lotId`와 레코드 키에 넣고 `metadata.workorderId`는 null로 둔다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd ; publishPortReports·publishToMes·publishToWm L219~276 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L93~139 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L154~188 | 생산 코드가 넘기는 reportId와 발행기 시험의 correlationId·lotId·key 기대값을 대조했다. |
| L217 | 이 연결 보고의 식별자는 실제 Lot ID와 구별한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd ; publishPortReports·publishToMes·publishToWm L219~276 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L93~139 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L154~188 | 생산 코드가 넘기는 reportId와 발행기 시험의 correlationId·lotId·key 기대값을 대조했다. |

### 기본 시나리오의 MES 지시와 TC 응답 식별자

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L227 | 제공된 `normal-flow` 시나리오는 다음 이벤트 식별자를 사용한다. | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 메시지 L36~55 | 제공 normal-flow의 MES 지시·응답 대기 쌍과 관련 업무 처리기를 대조했다. |
| L227 | 실제 송신은 설비에 연결한 모델 정의에 따른다. | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 메시지 L36~55 | 제공 normal-flow의 MES 지시·응답 대기 쌍과 관련 업무 처리기를 대조했다. |
| L231 | \| EQP_CONDITION_CHECK \| EQP_CONDITION_CHECK_REPLY \| 설비 진행 가능 확인 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 메시지 L36~55 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd ; handleEqpConditionCheck L32~45 | 제공 normal-flow의 MES 지시·응답 대기 쌍과 관련 업무 처리기를 대조했다. |
| L232 | \| WO_JOBRESERVE \| 별도 응답 대기 없음 \| 작업 예약 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 메시지 L36~55 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd ; handleWoJobreserve L63~91 | 제공 normal-flow의 MES 지시·응답 대기 쌍과 관련 업무 처리기를 대조했다. |
| L233 | \| WO_EQP_START_REQ \| WO_EQP_START_REPLY \| 가공 시작 지시와 결과 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 메시지 L36~55 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd ; handleWoEqpStartReq L37~56 | 제공 normal-flow의 MES 지시·응답 대기 쌍과 관련 업무 처리기를 대조했다. |

### 수집·완료 메시지의 MES 왕복 식별자

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L242 | 제공된 `normal-flow` 시나리오의 수집·완료 왕복은 다음 식별자를 사용한다. | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 수집·완료 L50~69 | 시나리오에 적힌 수집·완료 왕복 식별자를 확인했다. 범용 수신 스키마로 확대하지 않았다. |
| L246 | \| MAT_DCSPEC_REQ \| MAT_DCSPEC_REPLY \| 수집 규격과 항목 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 수집·완료 L50~69 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd ; handleMatDcspecReply L46~95 | 시나리오에 적힌 수집·완료 왕복 식별자를 확인했다. 범용 수신 스키마로 확대하지 않았다. |
| L247 | \| MAT_DATACOLL \| MAT_DATACOLL_REPLY \| 수집 결과와 수신 결과 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 수집·완료 L50~69 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd ; handleMatDatacoll L58~94 | 시나리오에 적힌 수집·완료 왕복 식별자를 확인했다. 범용 수신 스키마로 확대하지 않았다. |
| L248 | \| WO_EQP_COMPLETE \| WO_EQP_COMPLETE_REPLY \| 가공 완료와 수신 결과 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 수집·완료 L50~69 | 시나리오에 적힌 수집·완료 왕복 식별자를 확인했다. 범용 수신 스키마로 확대하지 않았다. |

### Carrier·Port 보고에 쓰는 TC 발행 식별자

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L256 | 제공된 `normal-flow` 시나리오에서 TC→MES 보고는 다음 이벤트 식별자를 사용한다. | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 보고 L46~75 | 제공 시나리오의 발행 식별자를 확인했다. 상세 업무 순서는 기존 user 문서에 남겼다. |
| L260 | \| WO_CAR_LOAD \| Carrier 반입 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 보고 L46~75 | 제공 시나리오의 발행 식별자를 확인했다. 상세 업무 순서는 기존 user 문서에 남겼다. |
| L261 | \| EQP_PORT_STATUS_CHG \| Port 상태 변경 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 보고 L46~75 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadReq·publishEqpPortStatusChange L46~96 | 제공 시나리오의 발행 식별자를 확인했다. 상세 업무 순서는 기존 user 문서에 남겼다. |
| L262 | \| WO_CAR_UNLOAD_REQ \| Carrier 반출 요청 \| | nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 보고 L46~75 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadReq·publishEqpPortStatusChange L46~96 | 제공 시나리오의 발행 식별자를 확인했다. 상세 업무 순서는 기존 user 문서에 남겼다. |
| L264 | Lot·Carrier·작업의 처리 순서는 `00.rag/user/lot-carrier-work-and-system-flow.md`가 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서 L25~78 | 제공 시나리오의 발행 식별자를 확인했다. 상세 업무 순서는 기존 user 문서에 남겼다. |

### 설비 메시지의 공통 헤더와 추적 ID

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L274 | Gateway가 발행하는 설비 이벤트와 Business가 발행하는 설비 명령의 `metadata`는 문자열 `eventType`·`timestamp`·`source`·`traceId`를 사용한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessEventMetadata L76~87 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessEqpCommandKafkaMessage.java @ 7db24f7466bd ; BusinessEqpCommandKafkaMetadata L29~40 | 설비 이벤트·명령 record의 필수 헤더와 MES·WM subscriber의 새 traceId 발급을 읽었다. |
| L274 | 네 필드 모두 비어 있지 않아야 한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessEventMetadata L76~87 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessEqpCommandKafkaMessage.java @ 7db24f7466bd ; BusinessEqpCommandKafkaMetadata L29~40 | 설비 이벤트·명령 record의 필수 헤더와 MES·WM subscriber의 새 traceId 발급을 읽었다. |
| L276 | MES·WM 수신 시 Business는 내부 처리용 `traceId`를 새로 발급한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; adaptRecord L65~66 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; adaptRecord L64~65 | 설비 이벤트·명령 record의 필수 헤더와 MES·WM subscriber의 새 traceId 발급을 읽었다. |
| L276 | MES 왕복의 `correlationId`와 내부 처리용 `traceId`를 구별한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; adaptRecord L65~66 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; adaptRecord L64~65 | 설비 이벤트·명령 record의 필수 헤더와 MES·WM subscriber의 새 traceId 발급을 읽었다. |

### SOCKET 설비 이벤트의 본문

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L284 | `tc.eqp.events`의 SOCKET 본문은 `data.eqpId`·`data.interfaceType`·`data.rawMessage`를 사용하며 세 값 모두 필수이다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveEventType L32~113 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessSocketEventData L93~102 | SOCKET 매퍼의 CMD 추출·원문 우선순위와 본문 생성자의 필수값 검사를 대조했다. |
| L284 | `interfaceType`은 `SOCKET`이다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveEventType L32~113 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessSocketEventData L93~102 | SOCKET 매퍼의 CMD 추출·원문 우선순위와 본문 생성자의 필수값 검사를 대조했다. |
| L286 | `metadata.eventType`은 원문 시작의 `CMD=` 다음 값을 우선 사용한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveEventType L32~113 | SOCKET 매퍼의 CMD 추출·원문 우선순위와 본문 생성자의 필수값 검사를 대조했다. |
| L286 | 원문에서 찾지 못하면 파싱된 메시지 식별자의 `CMD=` 다음 값을 사용한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveEventType L32~113 | SOCKET 매퍼의 CMD 추출·원문 우선순위와 본문 생성자의 필수값 검사를 대조했다. |
| L286 | 원문은 `rawLine`, `rawText`, 메시지 식별자와 본문 순서로 찾거나 구성한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveEventType L32~113 | SOCKET 매퍼의 CMD 추출·원문 우선순위와 본문 생성자의 필수값 검사를 대조했다. |

### SECS 설비 이벤트의 본문 인코딩

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L294 | `tc.eqp.events`의 SECS 본문은 `eqpId`·`interfaceType`·`secs2`·`rawMessage`와 선택값 `transactionId`를 `data`에 담는다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/HsmsBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveHsmsRawBodyBase64 L31~81 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessHsmsEventData·GatewayBusinessHsmsSecs2 L115~193 | SECS 매퍼의 바이트 Base64 변환과 필수 secs2·빈 본문 허용 검사를 확인했다. |
| L294 | `interfaceType`은 `SECS`이다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/HsmsBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveHsmsRawBodyBase64 L31~81 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessHsmsEventData·GatewayBusinessHsmsSecs2 L115~193 | SECS 매퍼의 바이트 Base64 변환과 필수 secs2·빈 본문 허용 검사를 확인했다. |
| L296 | `secs2`에는 `systemBytes`·`eventId`·`rawBody`가 있다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/HsmsBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveHsmsRawBodyBase64 L31~81 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessHsmsEventData·GatewayBusinessHsmsSecs2 L115~193 | SECS 매퍼의 바이트 Base64 변환과 필수 secs2·빈 본문 허용 검사를 확인했다. |
| L296 | SECS-II 바이트 본문은 Base64로 바꾸어 `secs2.rawBody`와 `rawMessage`에 함께 담는다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/HsmsBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveHsmsRawBodyBase64 L31~81 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessHsmsEventData·GatewayBusinessHsmsSecs2 L115~193 | SECS 매퍼의 바이트 Base64 변환과 필수 secs2·빈 본문 허용 검사를 확인했다. |
| L296 | 빈 바이트 본문은 빈 문자열로 표현할 수 있다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/HsmsBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveHsmsRawBodyBase64 L31~81 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessHsmsEventData·GatewayBusinessHsmsSecs2 L115~193 | SECS 매퍼의 바이트 Base64 변환과 필수 secs2·빈 본문 허용 검사를 확인했다. |

### PLC 변수는 data에 직접 펼쳐 발행한다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L305 | `tc.eqp.events`의 PLC 본문은 `data.eqpId`·`data.interfaceType`과 변수별 필드를 사용한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/PlcBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·toVariables L30~59 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessPlcEventData.variables L151~170 / nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 ; map L45~65 | PLC 응답 그룹명 전달, 맵 요구, @JsonAnyGetter 직렬화의 필드 위치를 연결했다. |
| L305 | `interfaceType`은 `PLC`이며 폴링 응답의 `metadata.eventType`은 그룹의 `groupMessageName`이다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/PlcBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·toVariables L30~59 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessPlcEventData.variables L151~170 / nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 ; map L45~65 | PLC 응답 그룹명 전달, 맵 요구, @JsonAnyGetter 직렬화의 필드 위치를 연결했다. |
| L307 | 변수 맵은 `@JsonAnyGetter`로 직렬화하므로 각 변수의 키와 값이 `data`에 직접 들어간다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/PlcBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·toVariables L30~59 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessPlcEventData.variables L151~170 | PLC 응답 그룹명 전달, 맵 요구, @JsonAnyGetter 직렬화의 필드 위치를 연결했다. |
| L307 | PLC 발행 매퍼는 파싱 본문이 맵이 아니면 거절한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/PlcBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·toVariables L30~59 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessPlcEventData.variables L151~170 | PLC 응답 그룹명 전달, 맵 요구, @JsonAnyGetter 직렬화의 필드 위치를 연결했다. |

### Business에서 Gateway로 보내는 설비 명령 본문

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L317 | `tc.eqp.commands`의 `data`에는 `eqpId`·`interfaceType`·`messageName`·`rawMessage`가 필요하다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessEqpCommandKafkaMessage.java @ 7db24f7466bd ; BusinessEqpCommandKafkaData L52~64 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성·resolveMessageName L108~121 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; resolveMessageName L201~205 | 명령 본문 생성·명령명 fallback과 소비자의 SECS 추가 조건을 대조했다. 생산자 null 한계도 숨기지 않았다. |
| L317 | `messageName`은 명령 속성에서 찾고 없으면 `eventType`을 사용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessEqpCommandKafkaMessage.java @ 7db24f7466bd ; BusinessEqpCommandKafkaData L52~64 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성·resolveMessageName L108~121 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; resolveMessageName L201~205 | 명령 본문 생성·명령명 fallback과 소비자의 SECS 추가 조건을 대조했다. 생산자 null 한계도 숨기지 않았다. |
| L319 | Gateway의 SECS 명령 수신은 `data.secs2`도 요구한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성·resolveMessageName L108~121 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; 필수 본문 검사 L195~237 | 명령 본문 생성·명령명 fallback과 소비자의 SECS 추가 조건을 대조했다. 생산자 null 한계도 숨기지 않았다. |
| L319 | 확인한 Business 발행기는 `secs2`를 null로 구성하므로 이 발행 경로는 SECS 수신의 추가 필수 조건을 충족하지 못한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성·resolveMessageName L108~121 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; 필수 본문 검사 L195~237 | 명령 본문 생성·명령명 fallback과 소비자의 SECS 추가 조건을 대조했다. 생산자 null 한계도 숨기지 않았다. |

### Gateway 대상 토픽의 파티션 지정

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L330 | `tc.eqp.commands` 발행기는 설비의 `route_partition`을 조회해 파티션을 명시한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; route_partition 지정 L104~129 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; publish L82~108 | 기본 발행의 조회 파티션, 소유 파티션 assign, 조정 이동의 양쪽 발행 분기를 읽었다. |
| L330 | 일반 UI Gateway 발행도 조회한 파티션을 사용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; route_partition 지정 L104~129 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; publish L82~108 | 기본 발행의 조회 파티션, 소유 파티션 assign, 조정 이동의 양쪽 발행 분기를 읽었다. |
| L332 | Gateway는 설비 명령과 UI 요청을 자신이 소유한 파티션에 `assign`하여 읽는다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayEqpCommandKafkaSubscriber.java @ d3636ab86763 ; bindingMode·assignedPartitions L114~139 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayUiEventKafkaSubscriber.java @ d3636ab86763 ; bindingMode·assignedPartitions L126~155 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publishToGateway L259~267 | 기본 발행의 조회 파티션, 소유 파티션 assign, 조정 이동의 양쪽 발행 분기를 읽었다. |
| L332 | UI 조정 작업에서 설비 담당 파티션을 옮길 때는 이전·목표 파티션 양쪽에 요청을 발행한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayEqpCommandKafkaSubscriber.java @ d3636ab86763 ; bindingMode·assignedPartitions L114~139 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayUiEventKafkaSubscriber.java @ d3636ab86763 ; bindingMode·assignedPartitions L126~155 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publishToGateway L259~267 | 기본 발행의 조회 파티션, 소유 파티션 assign, 조정 이동의 양쪽 발행 분기를 읽었다. |

### UI 요청의 기본 metadata와 data

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L341 | UI 요청은 `metadata`의 `eventType`·`timestamp`·`source`·`traceId`와 `data`의 `eqpId`·`interfaceType`을 필수 문자열로 사용한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata·KafkaUiTaskData L40~108 / nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskData L192~205 | 공유 UI record의 기본 필수값과 Backend v2 생성 분기를 확인했다. |
| L343 | 공유 UI 계약은 `schemaVersion`이 없거나 공백이면 문자열 `1`로 만든다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata·KafkaUiTaskData L40~108 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiKafkaContractMapper.java @ 985d17c604dd ; toMetadata L30~52 | 공유 UI record의 기본 필수값과 Backend v2 생성 분기를 확인했다. |
| L343 | Backend 발행 매퍼는 조정 정보가 있으면 문자열 `2`로 만든다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata·KafkaUiTaskData L40~108 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiKafkaContractMapper.java @ 985d17c604dd ; toMetadata L30~52 | 공유 UI record의 기본 필수값과 Backend v2 생성 분기를 확인했다. |
| L345 | `data.uiMessage`는 작업별 JSON이고 `data.equipmentProfile`은 설비 프로필 스냅샷이다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskData L192~205 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiKafkaContractMapper.java @ 985d17c604dd ; toMetadata L30~52 | 공유 UI record의 기본 필수값과 Backend v2 생성 분기를 확인했다. |
| L345 | 두 필드는 공통 생성자에서 필수로 검사하지 않는다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskData L192~205 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiKafkaContractMapper.java @ 985d17c604dd ; toMetadata L30~52 | 공유 UI record의 기본 필수값과 Backend v2 생성 분기를 확인했다. |

### UI 조정 요청에 추가되는 metadata

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L353 | 문자열 `schemaVersion=2`인 UI 계약은 다음 metadata를 추가로 요구한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata L40~86 | 문자열 2 분기의 추가 필수값과 실제 PREPARE·APPLY 발행 인자를 확인했다. |
| L357 | \| phase·deadlineAt \| 비어 있지 않은 문자열 \| | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata L40~86 | 문자열 2 분기의 추가 필수값과 실제 PREPARE·APPLY 발행 인자를 확인했다. |
| L358 | \| expectedBaseRevision·desiredRevision \| null이 아닌 Long \| | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata L40~86 | 문자열 2 분기의 추가 필수값과 실제 PREPARE·APPLY 발행 인자를 확인했다. |
| L359 | \| payloadSha256·resourceKeys \| 본문 해시 문자열과 리소스 키 목록 \| | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata L40~86 | 문자열 2 분기의 추가 필수값과 실제 PREPARE·APPLY 발행 인자를 확인했다. |
| L361 | Backend의 조정 발행은 `PREPARE`·`APPLY`를 phase로 사용하고 `operationId`·`executionId`도 헤더에 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publishPrepare·publishApply·publish L104~150 | 문자열 2 분기의 추가 필수값과 실제 PREPARE·APPLY 발행 인자를 확인했다. |

### UI 해시와 리소스 목록의 검증 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L369 | UI의 `schemaVersion=2`에서 `payloadSha256`은 64자의 소문자 16진수여야 한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; requireSha256Hex L233~244 | 해시 64자 소문자 hex 검사와 resourceKeys 엄격 오름차순·중복 거부를 읽었다. |
| L371 | `resourceKeys`는 비어 있지 않은 문자열 목록이며 공백 항목을 허용하지 않는다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; requireSortedResourceKeys L162~179 | 해시 64자 소문자 hex 검사와 resourceKeys 엄격 오름차순·중복 거부를 읽었다. |
| L371 | 항목은 문자열 비교 기준 오름차순이어야 하고 같은 키를 중복할 수 없다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; requireSortedResourceKeys L162~179 | 해시 64자 소문자 hex 검사와 resourceKeys 엄격 오름차순·중복 거부를 읽었다. |
| L371 | 누락·빈 목록·역순·중복이면 공유 UI 계약 생성 단계에서 거절한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; requireSortedResourceKeys L162~179 | 해시 64자 소문자 hex 검사와 resourceKeys 엄격 오름차순·중복 거부를 읽었다. |

### Gateway·Business에 공통 등록된 UI 이벤트

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L379 | 다음은 두 UI 수신 처리기에 모두 등록된 `metadata.eventType`이다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L114~196 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L78~123 | 두 수신 레지스트리의 네 이벤트 등록과 연결된 처리 호출을 대조했다. |
| L379 | 실제 요청 대상은 Backend 발행 경로가 정한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L114~196 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L78~123 | 두 수신 레지스트리의 네 이벤트 등록과 연결된 처리 호출을 대조했다. |
| L383 | \| EQP_CREATE \| 설비 런타임 생성 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L114~196 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L78~123 | 두 수신 레지스트리의 네 이벤트 등록과 연결된 처리 호출을 대조했다. |
| L384 | \| EQP_UPDATE \| 설비 런타임 갱신 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L114~196 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L78~123 | 두 수신 레지스트리의 네 이벤트 등록과 연결된 처리 호출을 대조했다. |
| L385 | \| EQP_MODEL_UPDATE \| 모델 변경 반영 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L114~196 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L78~123 | 두 수신 레지스트리의 네 이벤트 등록과 연결된 처리 호출을 대조했다. |
| L386 | \| EQP_DELETE \| 설비 런타임 제거 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L114~196 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L78~123 | 두 수신 레지스트리의 네 이벤트 등록과 연결된 처리 호출을 대조했다. |

### Gateway UI의 통신·직접 송신 이벤트

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L393 | Gateway UI 수신 처리기에 다음 이벤트가 등록되어 있다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L198~275 | Gateway 레지스트리의 통신·송신 이벤트 및 성공 응답 보류 등록을 확인했다. |
| L397 | \| EQP_START \| 설비 통신 시작 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L198~275 | Gateway 레지스트리의 통신·송신 이벤트 및 성공 응답 보류 등록을 확인했다. |
| L398 | \| EQP_END \| 설비 통신 종료 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L198~275 | Gateway 레지스트리의 통신·송신 이벤트 및 성공 응답 보류 등록을 확인했다. |
| L399 | \| EQP_RECONNECT \| 설비 통신 재연결 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L198~275 | Gateway 레지스트리의 통신·송신 이벤트 및 성공 응답 보류 등록을 확인했다. |
| L400 | \| EQP_SEND_MESSAGE \| uiMessage를 설비 송신 명령으로 변환 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L198~275 | Gateway 레지스트리의 통신·송신 이벤트 및 성공 응답 보류 등록을 확인했다. |
| L402 | `EQP_START`·`EQP_END`는 성공 응답을 연결 전환 결과까지 보류하는 방식으로 등록되어 있다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L198~275 | Gateway 레지스트리의 통신·송신 이벤트 및 성공 응답 보류 등록을 확인했다. |

### Gateway UI의 JAR·PLC 그룹 이벤트

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L412 | \| EQP_GATEWAY_JARFILE_UPDATE \| Gateway 플러그인 갱신 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L277~321 | JAR·PLC 그룹 이벤트 등록과 groupMessageName 비공백 검사 호출을 확인했다. |
| L413 | \| EQP_PLC_GROUP_START \| PLC 폴링 그룹 시작 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L277~321 | JAR·PLC 그룹 이벤트 등록과 groupMessageName 비공백 검사 호출을 확인했다. |
| L414 | \| EQP_PLC_GROUP_END \| PLC 폴링 그룹 종료 \| | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L277~321 | JAR·PLC 그룹 이벤트 등록과 groupMessageName 비공백 검사 호출을 확인했다. |
| L416 | 두 PLC 그룹 요청은 `data.uiMessage.groupMessageName`에 비어 있지 않은 그룹 식별자를 요구한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; startPlcGroup·endPlcGroup·requirePlcGroupMessageName L973~1040 | JAR·PLC 그룹 이벤트 등록과 groupMessageName 비공백 검사 호출을 확인했다. |

### Business UI의 파라미터·JAR 이벤트

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L426 | \| EQP_PARAMETER_UPDATE \| 설비 파라미터 갱신 \| | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L125~145 | Business 레지스트리와 파라미터 바인딩·자료 누락 실패 분기를 대조했다. |
| L427 | \| EQP_BUSINESS_JARFILE_UPDATE \| Business 플러그인 갱신 \| | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L125~145 | Business 레지스트리와 파라미터 바인딩·자료 누락 실패 분기를 대조했다. |
| L429 | 설비 파라미터 갱신은 설비 바인딩과 `uiMessage`의 파라미터 자료를 사용한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiModelRuntimeCommandService.java @ 7db24f7466bd ; handleEqpParameterUpdate L224~267 | Business 레지스트리와 파라미터 바인딩·자료 누락 실패 분기를 대조했다. |
| L429 | 공유 UI 생성자에서 `uiMessage`를 생략할 수 있어도 작업별 필수 자료가 없으면 처리에 실패한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiModelRuntimeCommandService.java @ 7db24f7466bd ; handleEqpParameterUpdate L224~267 | Business 레지스트리와 파라미터 바인딩·자료 누락 실패 분기를 대조했다. |

### 조정 발행에서 UI 이벤트가 바뀌는 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L438 | Backend의 조정 발행 경로는 Business에 `EQP_UPDATE`·`EQP_DELETE`만 그대로 보내고 나머지 작업 식별자는 `EQP_UPDATE`로 바꾼다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; 지원 집합 L48~71 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publish·wireEventTypeOf L150~165 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; wireEventTypeOf L235~240 | 조정 producer의 대상별 지원 집합과 EQP_UPDATE 대체 분기를 읽었다. |
| L440 | Gateway 참여가 필요한 조정은 `EQP_CREATE`·`EQP_UPDATE`·`EQP_MODEL_UPDATE`·`EQP_DELETE`·`EQP_GATEWAY_JARFILE_UPDATE`를 그대로 보낸다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; 지원 집합 L48~71 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publish·wireEventTypeOf L150~165 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; wireEventTypeOf L235~240 | 조정 producer의 대상별 지원 집합과 EQP_UPDATE 대체 분기를 읽었다. |
| L440 | 이 집합 밖 값은 `EQP_UPDATE`로 바꾼다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; 지원 집합 L48~71 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publish·wireEventTypeOf L150~165 / nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; wireEventTypeOf L235~240 | 조정 producer의 대상별 지원 집합과 EQP_UPDATE 대체 분기를 읽었다. |

### UI 처리 응답의 필드와 요청 연결

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L452 | UI 응답의 `metadata.eventType`은 요청 식별자에 `_REP`를 붙인 값이다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyEventType.java @ 6fe71f6b2da6 ; 응답 이벤트 L33~111 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; buildReplyMetadata L188~212 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyMetadata L179~201 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |
| L452 | 응답은 요청의 `traceId`를 유지하며 조정 요청이면 조정 metadata도 이어받는다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyEventType.java @ 6fe71f6b2da6 ; 응답 이벤트 L33~111 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; buildReplyMetadata L188~212 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyMetadata L179~201 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |
| L454 | `data`의 필수값은 `eqpId`·`interfaceType`·`STATUS`이다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~94 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |
| L454 | 실패 설명은 `ERRORMSG`·`ERRORCODE`로 전달한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~94 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |
| L454 | 결과 필드의 대문자 표기를 유지한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~94 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |
| L456 | `STATUS`는 `PASS`·`FAIL`을 사용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; parseStatusOrFail L328~343 / nori-tc-ui-backend/ui/tc-ui-domain/src/main/java/com/nori/tc/ui/domain/task/UiTaskStatus.java @ 985d17c604dd ; UiTaskStatus L19~30 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |
| L456 | Backend는 앞뒤 공백과 대소문자를 정리한 뒤 알 수 없는 값을 `FAIL`로 처리한다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; parseStatusOrFail L328~343 / nori-tc-ui-backend/ui/tc-ui-domain/src/main/java/com/nori/tc/ui/domain/task/UiTaskStatus.java @ 985d17c604dd ; UiTaskStatus L19~30 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |

### UI 응답에 조정 증거가 붙는 경우

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L465 | `data.commandState`가 있는 UI 응답은 `serviceSessionId`·`desiredRevision`·`payloadSha256`도 요구한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~70 | 조건부 증거 필수값·기본 응답 fallback·Backend operationId 결합 조건을 확인했다. |
| L465 | 해시는 64자의 소문자 16진수여야 한다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~70 | 조건부 증거 필수값·기본 응답 fallback·Backend operationId 결합 조건을 확인했다. |
| L465 | `ownerEpoch`·`appliedRevision`은 추가 필드이다. | nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~70 | 조건부 증거 필수값·기본 응답 fallback·Backend operationId 결합 조건을 확인했다. |
| L467 | Business 응답은 요청의 revision·해시 또는 조정 증거 제공 결과가 없으면 기본 응답만 만든다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyData L210~240 | 조건부 증거 필수값·기본 응답 fallback·Backend operationId 결합 조건을 확인했다. |
| L467 | 조정 증거 필드를 모든 UI 응답의 필수값으로 취급하지 않는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyData L210~240 | 조건부 증거 필수값·기본 응답 fallback·Backend operationId 결합 조건을 확인했다. |
| L469 | Backend는 `metadata.operationId`와 `data.commandState`가 모두 있을 때 조정 증거로 읽는다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; toCoordinationProof L303~317 | 조건부 증거 필수값·기본 응답 fallback·Backend operationId 결합 조건을 확인했다. |

### Simulator가 MES 역할로 사용하는 Kafka 경계

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L480 | Simulator의 상위 시스템 작업자는 `tc.simulator.upstream.kafka.command-topic`을 구독한다. | nori-tc-exec/apps/tc-simulator-app/config/tc-messaging.properties @ f1d5e0a8e727 ; MES 역할 토픽 L7~8 / nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/transport/UpstreamCommandListener.java @ 5e9b02e3ae16 ; onMessage L33~51 | Simulator 구독 설정과 시나리오 compiler에 실제 주입되는 발행 토픽, worker metadata 구성을 연결했다. |
| L480 | 제공 설정은 `dev.mat-evt`이다. | nori-tc-exec/apps/tc-simulator-app/config/tc-messaging.properties @ f1d5e0a8e727 ; MES 역할 토픽 L7~8 / nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/transport/UpstreamCommandListener.java @ 5e9b02e3ae16 ; onMessage L33~51 | Simulator 구독 설정과 시나리오 compiler에 실제 주입되는 발행 토픽, worker metadata 구성을 연결했다. |
| L482 | 시나리오의 MES 발행 토픽은 `tc.simulator.orchestrator.upstream-event-topic`에서 정하며 제공 설정은 `dev.tc-evt`이다. | nori-tc-exec/apps/tc-simulator-app/config/tc-simulator.properties @ f1d5e0a8e727 ; 시나리오 MES 발행 토픽 L16~16 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; scenarioActionCompiler L65~71 / nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamWorkerService.java @ 5e9b02e3ae16 ; MES 이벤트 구성 L240~290 | Simulator 구독 설정과 시나리오 compiler에 실제 주입되는 발행 토픽, worker metadata 구성을 연결했다. |
| L482 | 이벤트를 만들 때 `eventType`·`timestamp`·`source`·`correlationId`를 metadata에 넣는다. | nori-tc-exec/apps/tc-simulator-app/config/tc-simulator.properties @ f1d5e0a8e727 ; 시나리오 MES 발행 토픽 L16~16 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; scenarioActionCompiler L65~71 / nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamWorkerService.java @ 5e9b02e3ae16 ; MES 이벤트 구성 L240~290 | Simulator 구독 설정과 시나리오 compiler에 실제 주입되는 발행 토픽, worker metadata 구성을 연결했다. |

### 인증·장애·DLQ의 담당 문서

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L489 | Kafka 인증 설정과 교체는 `00.rag/admin/security-operations.md`, 전달 중단 진단은 `00.rag/admin/platform-troubleshooting.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서 | 목록이 정한 보안·진단·실패 조회 문서의 소유 범위와 objectKey를 확인했다. |
| L491 | 실패 보관 자료를 화면에서 찾는 방법은 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서 | 목록이 정한 보안·진단·실패 조회 문서의 소유 범위와 objectKey를 확인했다. |

## 매뉴얼과 코드가 다른 곳

공통 매뉴얼 원천은 `nori-tc-exec/docs/completion-report/04-interface-spec.md @ f1d5e0a8e727`이다. 다음은 검색 원문에 잘못된 예시를 복제하지 않기 위한 대조 기록이다.

| 항목 | 매뉴얼 위치·내용 | 실제 코드와 근거 | 검색 원문 반영 |
|---|---|---|---|
| 연결 보고의 상관 키 | I §2 L98~101은 MES correlationId를 lotId와 같은 값으로 묶고 WM 키를 lotId로 설명한다. | `ConnectionPortStatusSyncReplyHandler` L219~276은 Port별 reportId를 MES correlationId와 WM 명령 lotId에 넣는다. 두 발행기 시험의 연결 보고 기대값도 reportId이다. | 일반 키의 필드와 연결 보고의 실제 보고 ID를 구별했다. |
| MES 원문 필드의 대문자 표기 | I §4.1·§5 L136~141, L383~450은 EQPID 등 대문자 필드 묶음을 필수로 제시한다. | `BusinessKafkaInboundRecordMapper` L299~329은 eqpId·eqpid·equipmentId를 읽고 MES에는 키 fallback을 쓰지 않는다. 일반 발행 본문은 모델 전송 정의가 정한다. | 실제 수신 별칭과 모델 의존 본문을 적고 대문자 묶음을 보편적 JSON 스키마로 싣지 않았다. |
| source 예시 | I §5 L383·521·937·963의 예시에는 MES·UI·GATEWAY·BUSINESS 같은 표기가 있다. | `BusinessKafkaContractSupport` L399~443과 `GatewayKafkaContractSupport` L393~415는 구체적인 source 허용 목록을 사용한다. | 제공 앱 설정과 실제 매퍼의 발행 source 및 MES·WM 수신 허용 목록을 썼다. |
| WM schemaVersion 필수·조정 버전 설명 | I §4.2 L155와 §5 L509는 schemaVersion을 필수로 적고 2부터 조정 필드가 필요하다고 설명한다. | `BusinessKafkaContractSupport` L255~264·335~340은 생략값을 v1로 만든다. `KafkaSchemaVersionPolicy` L74~97과 Business 정책 구성 L95~96상 WM은 조정 토픽이 아니어서 v1·1만 허용한다. | 생략 기본값과 업무 토픽 버전 허용값을 명시했다. |
| PLC 변수 위치 | I §4.4 L182·§5 L962는 variables라는 필드를 표기한다. | `GatewayBusinessPlcEventData.variables` L167~170의 @JsonAnyGetter가 변수 맵을 data에 펼친다. `PlcBusinessPayloadMapper` L48~59는 맵을 요구한다. | 변수별 키를 data에 직접 넣는 직렬화를 적었다. |
| SECS secs2 조건 | I §4.4·§5 L181·951과 §4.5·§5 L188·970은 secs2를 선택값으로 표기한다. | 설비 이벤트 생성자는 secs2를 요구한다(`GatewayBusinessEventMessage` L122~128). 명령 수신도 SECS이면 필수다(`GatewayCommandDispatcherSupport` L226~237). `BusinessEqpCommandKafkaPublisher` L115~120은 현재 null로 구성한다. | 이벤트 본문 조건과 명령 생산·소비 사이의 확인된 한계를 각각 적었다. |
| UI 조정 필드의 조건 | I §4.3·§5 L162~174·525~533은 phase·revision·해시·deadline을 일반 필수로, resourceKeys를 선택으로 표기한다. | `KafkaUiTaskMessage` L69~86은 문자열 2일 때 추가 필수값을 검사한다. resourceKeys는 이 경우 비어 있지 않고 엄격한 오름차순이어야 한다(L162~179). | 기본 UI 계약과 schemaVersion 2 계약을 나누었다. |
| UI 응답 증거의 필수 조건 | I §5 L547~552는 commandState·serviceSessionId·payloadSha256을 일반 필수로 표기한다. | `KafkaUiTaskReplyMessage` L59~70은 commandState가 있을 때만 session·desiredRevision·해시를 요구한다. Business 발행기 L217~228은 증거가 없으면 기본 응답을 만든다. | 조건부 필수임을 명시하고 Backend의 operationId와 commandState 결합 조건도 적었다. |
| 조정 발행의 UI eventType | I §4.3 L162~165는 같은 이벤트가 Gateway·Business 양쪽으로 전달되는 형태로 정리한다. | `UiOperationCommandKafkaPublisher` L48~71·150~165·235~240은 Business 조정 요청을 EQP_UPDATE·EQP_DELETE로 제한하고 나머지를 EQP_UPDATE로 바꾼다. | 수신기에 등록된 이벤트 목록과 실제 조정 발행의 변환을 분리했다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 생략 이유 | 확인할 역할 |
|---|---|---|
| 현장 토픽 설정값·파티션 수·복제 계수·보존 기간·현재 offset·lag·실제 source | 클러스터·브로커에 접속하지 않았다. 본문 토픽값은 저장소에 제공된 설정 사본으로 한정했다. | Kafka·배포 운영 담당 |
| 현장 MES·WM의 전체 이벤트 목록과 메시지별 업무 필드·필수성 | TC의 일반 업무 메시지는 모델 정의에 의존한다. 현장의 승인된 모델·상대 시스템 계약은 확보하지 않았다. | MES·WM 연동 및 모델 담당 |
| SECS 이벤트 transactionId를 MBAP 번호로 풀이하는 설명 | I §5 L948의 풀이를 이 SECS 경로의 실제 생성·사용 코드로 입증하지 못했다. 필드 존재·선택 조건만 적었다. | 설비 프로토콜 개발 담당 |
| 제공 시나리오가 현장에 설치·활성화되었다는 주장 | SQL 사본과 코드만 읽었다. 실제 DB 및 연결 모델은 조회하지 않았다. | 배포·모델 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이번 문서에 복제하지 않은 내용 |
|---|---|
| 00.rag/admin/security-operations.md | Kafka 인증·자격·신뢰 파일 구성과 교체 절차 |
| 00.rag/admin/platform-troubleshooting.md | 브로커·발행기 실패, 연결·인증·적체 진단 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | DLQ·격리·Repair Task 조회 화면과 처리 인계 |
| 00.rag/user/lot-carrier-work-and-system-flow.md | Lot·Carrier 작업의 업무 순서와 상태 변화 |
| 00.rag/user/flow-action-and-message-reference.md | 모델 편집 화면에서 액션을 선택하는 의미와 사용처 |
| 00.rag/user/state-reference.md | 설비·Port·작업·조정 상태값의 상세 뜻 |
| 00.rag/admin/installation-and-service-lifecycle.md | Kafka 토픽 생성과 파티션 확장 운영 절차 |
| 00.rag/admin/configuration-reference.md | 토픽 바인딩을 제외한 앱·Helm·Secret 설정 표 |
| 00.rag/admin/http-api-reference.md | HTTP 요청·응답·경로 및 권한 계약 |

기존 겹침 문서는 위 소유 관계에 맞춰 읽었다. `tc-components-and-terms.md`·`tc-supported-scope.md`·설비 명령·모델 갱신·배포 사용 절차도 주제 겹침을 확인했으며, 화면 조작이나 상태 풀이를 이번 참조 문서에 옮기지 않았다.

## 검사 결과

검사 명령은 다음과 같다. 마지막 실행은 검색 원문 최종 편집 후 수행하며 아래 최종 검사 출력에 기록한다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/kafka-topic-and-message-reference.md
```

- 첫 초안 검사: 종료코드 0, 대상 위반 0, CROSS VIOLATION 0, 35절·112자식·최대 자식 157자·최대 부모 331자였다.
- 두 번째 검사: 종료코드 0, 대상 위반 0, 36절·113자식이었다. 검사 중 새로 보인 다른 HTTP 문서의 두 행에 CROSS VIOLATION 1건이 출력되었다. 대상 문서와의 중복은 아니었다.
- 마지막 검사: 종료코드 0, 대상 위반 0, CROSS VIOLATION 0, MASK 출력 0. 36절·113자식·최대 자식 174자·최대 부모 372자였다. 제목·구조·원천 commit:path·줄 범위·표·마스킹 검사를 통과했다. 다른 HTTP 문서의 중복도 마지막 검사에서 더 이상 검출되지 않았다.
- 다른 문서 원문 재조회: `http-api-reference.md` SHA-256 `f84e8c03e5bbb74154f57d9f6768933152e7c5ff1dd5e1f9caafa3b7bc29187c`의 L716·L728에서는 AbortRequest 앞에 각각 설비 worker·업스트림 worker라는 호출 대상이 붙어 있었다. 본 세션은 그 파일을 수정하지 않았다. 최종 전체 중복 판정은 아래 마지막 검사에 따른다.
- 도구: GNU bash 5.2.37(1)-release, Temurin OpenJDK 21.0.9+10-LTS, Git 2.52.0.windows.1, Python 3.12.10.
- 검증 범위는 문서 형식·원천 위치·본문 대조이다. 제품 시험, 독립 공식 검증, 검색 반입·정답률 측정은 하지 않았다.
- 독립 AI 호출 0건. 전체 세션 wall time·provider 사용량·비용은 이 세션 도구로 계측하지 않았다.
- 보조 조회에서 PATH에 rg가 없어 명령을 실행하지 못했다. 이후 Git 파일 목록·Python 파일 읽기로 범위를 좁혀 조회했다. Git 소유자 오류 뒤에는 저장소별 읽기 명령에만 safe.directory를 명시했다.
- 원천 상태 확인 보조 스크립트 첫 실행의 `re.error: nothing to repeat at position 1`은 Unicode 접두사 대신 명시적인 Unicode escape를 사용한 조회로 대체했다. 성공한 조회에서 실제 경로별 변경 없음과 HEAD를 확인했으며 실패한 조회를 원천 검증 성공으로 세지 않았다.

최종 검사 마지막 출력 10줄은 다음과 같다.

```text
    L469-469 문단 69  | Backend는 metadata.operationId와 data.commandState가 모두 있을 때 조정 증거로 읽는다.
§ L471-482 [32] Simulator가 MES 역할로 사용하는 Kafka 경계   parent=251 children=2 sources=5
    L480-480 문단 93  | Simulator의 상위 시스템 작업자는 tc.simulator.upstream.kafka.command-topic을 구독한다. 제공 설정은 dev.mat-evt이다.
    L482-482 문단 157  | 시나리오의 MES 발행 토픽은 tc.simulator.orchestrator.upstream-event-topic에서 정하며 제공 설정은 dev.tc-evt이다. 이벤트를 만들 때 eventType·timestamp·source·correlationId를 metadata에 넣는다.
§ L484-491 [16] 인증·장애·DLQ의 담당 문서   parent=186 children=2 sources=1
    L489-489 문단 111  | Kafka 인증 설정과 교체는 00.rag/admin/security-operations.md, 전달 중단 진단은 00.rag/admin/platform-troubleshooting.md에서 다룬다.
    L491-491 문단 74  | 실패 보관 자료를 화면에서 찾는 방법은 00.rag/user/dlq-quarantine-and-repair-task.md에서 다룬다.
SUMMARY sections=36 children=113 maxChild=174 maxParent=372
CORPUS docs=46/53 sections=757 part={admin=12, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=8, 트러블슈팅=12}
TOTAL files=46 targets=1 violations=0
```

사람 검사는 40절 미만이므로 최종 36절 전체를 대상으로 했다. 아래 절별 판정과 별도로 표 칸 수·필드 대소문자·조건부 필수성·source·레코드 키를 실제 생성·소비 위치와 대조했다. 제목을 번호나 식별자만 바꿔 반복한 틀은 사용하지 않았다. 공통 헤더 필드의 최소 언급은 경계별 필수값을 구별하는 데만 남겼다.


### 절 전체 사람 검사

| 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 중복·소유 범위 | 판정 근거 |
|---|---|---|---|---|---|
| MES 연동 토픽의 방향과 키 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 구독 annotation·발행 토픽 설정·키 해석기를 연결해 MES 경계의 방향과 키를 확인했다. |
| WM 연동 토픽과 Lot 키 | 적합 | 대조함 | 적합 | 소유 범위 구별 | WM 전용 매핑과 명령 발행에서 lotId 키를 확인했다. 외부 WM 구현의 전체 계약으로 확대하지 않았다. |
| 설비 이벤트와 설비 명령 토픽 | 적합 | 대조함 | 적합 | 소유 범위 구별 | Gateway 이벤트 생산과 Business 구독, 반대 방향 명령 발행·Gateway 수신 경계를 확인했다. |
| UI 요청과 처리 결과가 오가는 토픽 | 적합 | 대조함 | 적합 | 소유 범위 구별 | Backend 발행 두 경로와 Gateway·Business 응답 생산, Backend 응답 구독을 연결했다. |
| 격리 시험 설정의 토픽 표기 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 실제 test 설정 사본의 토픽 문자열을 대조했다. 현장 설정으로 표현하지 않았다. |
| JSON 구조와 이벤트 식별자 | 적합 | 대조함 | 적합 | 소유 범위 구별 | JSON 객체 필수 검사와 eventType 검증 정규식·런타임 메시지 식별자 매핑을 읽었다. |
| MES 수신 헤더와 설비 식별 필드 | 적합 | 대조함 | 적합 | 소유 범위 구별 | MES metadata 생성의 필수값·기본 버전과 실제 설비 ID 별칭 우선순위를 읽었다. |
| WM 수신에서 요구하는 필드 | 적합 | 대조함 | 적합 | 소유 범위 구별 | WM 분기의 필수 필드와 비필수 헤더, lotId·eqpId 별칭을 확인했다. |
| MES·WM으로 발행하는 헤더의 차이 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 외부로 직렬화하는 전용 record 구성요소와 발행 timestamp 생성식을 대조했다. |
| MES·WM 수신 source 허용값 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 원본 source의 정규화와 토픽별 허용 목록을 구별해 확인했다. |
| TC 내부 메시지의 source 표기 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 선언된 설정값과 실제 producer metadata 조립 위치를 대조했다. |
| 모델이 정하는 업무 메시지 본문 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 모델 targetType 분기와 발행기의 putIfAbsent 처리를 확인했다. |
| MES·WM 업무 헤더의 schemaVersion 허용값 | 적합 | 대조함 | 적합 | 소유 범위 구별 | UI 조정 토픽 집합과 업무 토픽의 v1·1 허용 집합을 확인했다. |
| MES·WM 송신의 portId 형식 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 소문자 portId만 변환하는 함수의 무값·기존 접두사·신규 접두사 분기를 읽었다. |
| 연결 Port 보고의 키는 보고 ID를 사용한다 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 생산 코드가 넘기는 reportId와 발행기 시험의 correlationId·lotId·key 기대값을 대조했다. |
| 기본 시나리오의 MES 지시와 TC 응답 식별자 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 제공 normal-flow의 MES 지시·응답 대기 쌍과 관련 업무 처리기를 대조했다. |
| 수집·완료 메시지의 MES 왕복 식별자 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 시나리오에 적힌 수집·완료 왕복 식별자를 확인했다. 범용 수신 스키마로 확대하지 않았다. |
| Carrier·Port 보고에 쓰는 TC 발행 식별자 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 제공 시나리오의 발행 식별자를 확인했다. 상세 업무 순서는 기존 user 문서에 남겼다. |
| 설비 메시지의 공통 헤더와 추적 ID | 적합 | 대조함 | 적합 | 소유 범위 구별 | 설비 이벤트·명령 record의 필수 헤더와 MES·WM subscriber의 새 traceId 발급을 읽었다. |
| SOCKET 설비 이벤트의 본문 | 적합 | 대조함 | 적합 | 소유 범위 구별 | SOCKET 매퍼의 CMD 추출·원문 우선순위와 본문 생성자의 필수값 검사를 대조했다. |
| SECS 설비 이벤트의 본문 인코딩 | 적합 | 대조함 | 적합 | 소유 범위 구별 | SECS 매퍼의 바이트 Base64 변환과 필수 secs2·빈 본문 허용 검사를 확인했다. |
| PLC 변수는 data에 직접 펼쳐 발행한다 | 적합 | 대조함 | 적합 | 소유 범위 구별 | PLC 응답 그룹명 전달, 맵 요구, @JsonAnyGetter 직렬화의 필드 위치를 연결했다. |
| Business에서 Gateway로 보내는 설비 명령 본문 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 명령 본문 생성·명령명 fallback과 소비자의 SECS 추가 조건을 대조했다. 생산자 null 한계도 숨기지 않았다. |
| Gateway 대상 토픽의 파티션 지정 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 기본 발행의 조회 파티션, 소유 파티션 assign, 조정 이동의 양쪽 발행 분기를 읽었다. |
| UI 요청의 기본 metadata와 data | 적합 | 대조함 | 적합 | 소유 범위 구별 | 공유 UI record의 기본 필수값과 Backend v2 생성 분기를 확인했다. |
| UI 조정 요청에 추가되는 metadata | 적합 | 대조함 | 적합 | 소유 범위 구별 | 문자열 2 분기의 추가 필수값과 실제 PREPARE·APPLY 발행 인자를 확인했다. |
| UI 해시와 리소스 목록의 검증 조건 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 해시 64자 소문자 hex 검사와 resourceKeys 엄격 오름차순·중복 거부를 읽었다. |
| Gateway·Business에 공통 등록된 UI 이벤트 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 두 수신 레지스트리의 네 이벤트 등록과 연결된 처리 호출을 대조했다. |
| Gateway UI의 통신·직접 송신 이벤트 | 적합 | 대조함 | 적합 | 소유 범위 구별 | Gateway 레지스트리의 통신·송신 이벤트 및 성공 응답 보류 등록을 확인했다. |
| Gateway UI의 JAR·PLC 그룹 이벤트 | 적합 | 대조함 | 적합 | 소유 범위 구별 | JAR·PLC 그룹 이벤트 등록과 groupMessageName 비공백 검사 호출을 확인했다. |
| Business UI의 파라미터·JAR 이벤트 | 적합 | 대조함 | 적합 | 소유 범위 구별 | Business 레지스트리와 파라미터 바인딩·자료 누락 실패 분기를 대조했다. |
| 조정 발행에서 UI 이벤트가 바뀌는 조건 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 조정 producer의 대상별 지원 집합과 EQP_UPDATE 대체 분기를 읽었다. |
| UI 처리 응답의 필드와 요청 연결 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 응답 enum·요청 metadata 유지·대문자 필드·STATUS 정규화 분기를 대조했다. |
| UI 응답에 조정 증거가 붙는 경우 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 조건부 증거 필수값·기본 응답 fallback·Backend operationId 결합 조건을 확인했다. |
| Simulator가 MES 역할로 사용하는 Kafka 경계 | 적합 | 대조함 | 적합 | 소유 범위 구별 | Simulator 구독 설정과 시나리오 compiler에 실제 주입되는 발행 토픽, worker metadata 구성을 연결했다. |
| 인증·장애·DLQ의 담당 문서 | 적합 | 대조함 | 적합 | 소유 범위 구별 | 목록이 정한 보안·진단·실패 조회 문서의 소유 범위와 objectKey를 확인했다. |

마스킹·금지 구조·빈 칸·빈 문장·제목 틀 반복은 최종 기계 검사와 전수 읽기를 함께 적용한다. 코드의 생산·소비 한계는 본문 조건으로 보존하며 해결된 제품 결함으로 판정하지 않는다.

## 임시 자원과 변경 범위

검사용 임시 파일·임시 스크립트·브라우저·컨테이너·네트워크·서비스를 만들지 않았다. 검사 출력은 프로세스 메모리와 이 작성 기록에 보존했다. 삭제할 소유 임시 자원이 없으므로 삭제 명령도 실행하지 않았다. 지정된 기존 work 폴더는 조회 시 비어 있었으며 보존했다. 제품 저장소와 앞선 검색 원문은 읽기만 했으며, 본 세션의 영속 산출물은 지정된 두 Markdown 파일뿐이다.

### 최종 파일 계측

- 검색 원문 SHA-256: `b4ddf1f418bc43869cbb0b13363b9f872d434446df197d110c1d1da415c76c72`
- 검색 원문 줄 수: 491
- 검색 원문 H2 절 수: 36
- 사실 대조 표 행: 166
- 경로별 원천 상태 확인: 69개, 모두 변경 출력 없음·exit 0
- UTF-8 BOM 없음, LF, CR·NUL 없음, NFC 확인: True

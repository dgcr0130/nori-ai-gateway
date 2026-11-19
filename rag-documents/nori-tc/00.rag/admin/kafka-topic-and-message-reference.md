# Kafka 토픽·메시지 계약
```rag-meta
유형: 참조
적용: nori-tc-exec f1d5e0a8e727·nori-tc-business 7db24f7466bd·nori-tc-gateway d3636ab86763·nori-tc-ui-backend 985d17c604dd·nori-tc-common 6fe71f6b2da6·nori-common ae6791e99e91·nori-tc-simulator 5e9b02e3ae16 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## MES 연동 토픽의 방향과 키
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; MES 토픽 L6~21
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L43~56
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; publish L76~114
원천: nori-tc-common/messaging/tc-messaging-domain/src/main/java/com/nori/tc/messaging/domain/kafka/key/KafkaMessageKeyResolver.java @ 6fe71f6b2da6 ; resolve L42~56
```

제공 설정의 MES 토픽은 다음과 같다. Business는 `tc.business.kafka.mes-events-topic`을 구독하고 `tc.business.kafka.mes-commands-topic`으로 발행한다.

| 토픽 | 생산자→소비자 | 레코드 키 |
|---|---|---|
| dev.tc-evt | MES→TC Business | metadata.correlationId |
| dev.mat-evt | TC Business→MES | metadata.correlationId |

## WM 연동 토픽과 Lot 키
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; WM 토픽 L9~18
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L42~55
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L74~103
```

Business의 구독 키는 `tc.business.kafka.wm-events-topic`, 발행 키는 `tc.business.kafka.wm-commands-topic`이다. 제공 설정은 다음과 같다.

| 토픽 | 생산자→소비자 | 레코드 키 |
|---|---|---|
| wm.tc.events | WM→TC Business | data.lotId |
| tc.wm.events | TC Business→WM | 발행 명령의 lotId |

WM 수신 레코드는 키와 본문의 Lot ID가 같아야 한다.

## 설비 이벤트와 설비 명령 토픽
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 설비 토픽 L90~103
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 설비 토픽 L6~15
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; publish L79~140
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessEqpEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L32~45
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; publish L79~129
```

제공 설정의 설비 연동은 Gateway를 경계로 한다. 설비의 통신 프레임은 Gateway가 Kafka 메시지로 바꾼다.

| 토픽 | 생산자→소비자 | 레코드 키 |
|---|---|---|
| tc.eqp.events | TC Gateway→TC Business | 설비 ID |
| tc.eqp.commands | TC Business→TC Gateway | 설비 ID |

Gateway 설정은 `tc.messaging.kafka.topic.eqp-events`·`tc.messaging.kafka.topic.eqp-commands`, Business 설정은 `tc.business.kafka.eqp-events-topic`·`tc.business.kafka.eqp-commands-topic`이다.

## UI 요청과 처리 결과가 오가는 토픽
```rag-meta
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 토픽 L24~31
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; publishInternal L126~150
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiBusinessEventKafkaPublisher.java @ 985d17c604dd ; publish L66~92
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L109~120
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; publishResult L98~137
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; publishResult L83~104
```

제공 설정의 UI 메시지는 모두 설비 ID를 레코드 키로 쓴다.

| 토픽 | 생산자→소비자 |
|---|---|
| tc.ui.events.gateway | TC UI Backend→TC Gateway |
| tc.ui.events.business | TC UI Backend→TC Business |
| tc.ui.commands | TC Gateway·TC Business→TC UI Backend |

Backend의 `tc.ui.backend.kafka` 아래에서 `gateway-events-topic`·`business-events-topic`·`commands-topic`을 각각 바인딩한다.

## 격리 시험 설정의 토픽 표기
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~18
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.test.properties @ f1d5e0a8e727 ; 시험 토픽 L23~28
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.test.properties @ f1d5e0a8e727 ; 시험 토픽 L11~13
```

제공된 `.test.properties` 사본은 기본 토픽 앞에 `test.`를 붙인 값을 사용한다.

- MES: `test.dev.tc-evt`·`test.dev.mat-evt`.
- WM: `test.wm.tc.events`·`test.tc.wm.events`.
- 설비: `test.tc.eqp.events`·`test.tc.eqp.commands`.
- UI: `test.tc.ui.events.gateway`·`test.tc.ui.events.business`·`test.tc.ui.commands`.

## JSON 구조와 이벤트 식별자
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; doMap L92~143
원천: nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaEventTypeNamingPolicy.java @ ae6791e99e91 ; DEFAULT_EVENT_TYPE_PATTERN·validate L28~86
```

Business가 받는 JSON은 최상위 `metadata`와 `data`가 모두 객체여야 한다. Business는 `metadata.eventType`을 모델 흐름을 찾는 메시지 식별자로 사용한다.

이벤트 식별자는 영문 대문자로 시작하고 대문자·숫자를 사용한다. 밑줄로 단어를 나눌 수 있으며 연속 밑줄이나 끝 밑줄은 허용하지 않는다. 검증식은 `^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$`이다.

## MES 수신 헤더와 설비 식별 필드
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; createEnvelope·resolveEqpId L253~329
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; createMesMetadata·normalizeSchemaVersion L149~162
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; 기본 버전 L335~340
```

MES 수신의 `metadata`에는 문자열 `eventType`·`timestamp`·`source`·`correlationId`가 필요하다. `schemaVersion`을 생략하거나 비우면 `v1`을 적용한다.

설비는 `data.eqpId`를 우선 읽고 `eqpid`·`equipmentId`도 읽는다. 본문에서 찾지 못하면 최상위의 같은 필드를 찾는다. MES 수신에서는 Kafka 키를 설비 ID의 대체값으로 사용하지 않는다.

## WM 수신에서 요구하는 필드
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; mapWm L174~220
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; validateWmEnvelope L255~273
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; normalizeSchemaVersion L335~340
```

TC Business의 WM 수신은 `metadata.eventType`·`metadata.source`를 검사한다. `schemaVersion`은 없으면 `v1`을 적용한다. `correlationId`·`traceId`·`timestamp`는 이 수신 검사의 필수 필드가 아니다.

`data`에서 Lot은 `lotId`·`lotid`·`LOTID` 순서로, 설비는 `eqpId`·`eqpid`·`equipmentId` 순서로 찾는다. Lot과 설비를 모두 찾아야 모델 처리로 넘긴다.

## MES·WM으로 발행하는 헤더의 차이
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessMesCommandKafkaMessage.java @ 7db24f7466bd ; BusinessMesCommandKafkaMetadata L25~36
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessWmCommandKafkaMessage.java @ 7db24f7466bd ; BusinessWmCommandKafkaMetadata L28~39
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; publish L79~114
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L77~103
```

Business가 발행하는 두 JSON 헤더는 다음 문자열 필드를 사용한다.

| 대상 | metadata 필드 | 필수 조건 |
|---|---|---|
| MES | eventType·timestamp·source·correlationId | 네 필드 모두 비어 있지 않아야 한다. |
| WM | eventType·timestamp·source·workorderId | 앞의 세 필드는 필수이며 workorderId는 비울 수 있다. |

두 발행용 헤더에는 `schemaVersion` 필드가 없다. `timestamp`는 발행 시점의 `Instant.now().toString()`으로 만든다.

## MES·WM 수신 source 허용값
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; normalizeSource L309~325
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; buildAllowlist L399~430
원천: nori-tc-common/messaging/tc-messaging-domain/src/main/java/com/nori/tc/messaging/domain/kafka/TcKafkaSources.java @ 6fe71f6b2da6 ; 외부 source L49~52
```

TC Business는 설정된 MES·WM 수신 토픽에서 `metadata.source`를 다음 허용 목록과 대조한다.

| 경계 | 허용 문자열 |
|---|---|
| MES→TC | TC-EXTERNAL-MES-ADAPTER·TC-EXTERNAL-MES-ADAPTER-APP·WSC-CONSUMER |
| WM→TC | WM-BUSINESS·WM-BUSINESS-APP |

MES 수신은 검증 전에 source의 앞뒤 공백과 호환 접미사 `-APP`를 정리한다. WM 수신은 WM 전용 검증 경로를 사용한다.

## TC 내부 메시지의 source 표기
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 업무 발행 source L21~21
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; UI 발행 source L65~65
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; GATEWAY_SOURCE L22~22
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; GATEWAY_SOURCE L1~57
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; UI 응답 source L64~64
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyMetadata L179~201
```

| 발행 경로 | metadata.source |
|---|---|
| Gateway의 설비 이벤트 | TC-COMM-GATEWAY-APP |
| Business의 MES·WM·설비 명령 | 제공 설정의 TC-BUSINESS-CORE |
| Backend의 UI 요청 | 제공 설정의 TC-UI-APP |
| Gateway의 UI 응답 | TC-GATEWAY-APP |
| Business의 UI 응답 | 제공 설정의 TC-BUSINESS-APP |

source는 메시지 헤더의 발행 주체 표기이다.

## 모델이 정하는 업무 메시지 본문
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L90~111
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성 L84~100
```

일반 업무 메시지의 발행 대상은 모델의 메시지 전송 정의가 `EQP`·`MES`·`WM` 중에서 정한다. 전송 정의가 만든 본문을 발행기에 넘긴다.

MES 발행기는 본문에 `eqpId`가 없으면 추가한다. WM 발행기는 `eqpId`와 `lotId`가 없으면 추가한다. 그 밖의 본문 필드 묶음은 모델의 전송 정의에 따른다.

## MES·WM 업무 헤더의 schemaVersion 허용값
```rag-meta
원천: nori-common/modules/nori-messaging-contract/src/main/java/com/nori/common/messaging/kafka/policy/KafkaSchemaVersionPolicy.java @ ae6791e99e91 ; coordinationAwarePolicy·validate L37~176
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/contract/BusinessKafkaContractSupport.java @ 7db24f7466bd ; 정책 구성 L82~105
```

TC Business는 MES·WM 업무 수신 헤더의 `schemaVersion`에 문자열 `v1` 또는 `1`을 허용한다. UI 조정 토픽에는 별도로 두 번째 버전도 허용하는 정책을 구성한다.

## MES·WM 송신의 portId 형식
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessPortIdWireFormat.java @ 7db24f7466bd ; withQualifiedPortId L18~52
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 변환 L90~93
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 변환 L84~88
```

Business의 MES·WM 발행기는 `data.portId`가 있으면 문자열로 바꾸고 앞뒤 공백을 제거해 검사한다. 값이 비어 있거나 이미 `<eqpId>_P`로 시작하면 본문을 유지한다.

나머지 값은 `<eqpId>_P<portId>`로 바꾼다. `portId`가 없으면 새로 만들지 않는다.

## 연결 Port 보고의 키는 보고 ID를 사용한다
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd ; publishPortReports·publishToMes·publishToWm L219~276
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L93~139
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/test/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisherTest.java @ 7db24f7466bd ; 연결 보고 기대값 L154~188
```

연결 때 Port 상태를 동기화한 뒤 발행하는 `EQP_PORT_STATUS_CHG`는 Port별 보고 ID를 사용한다.

MES 발행은 보고 ID를 `metadata.correlationId`와 레코드 키에 넣는다. WM 발행은 보고 ID를 명령의 `lotId`로 넘겨 `data.lotId`와 레코드 키에 넣고 `metadata.workorderId`는 null로 둔다. 이 연결 보고의 식별자는 실제 Lot ID와 구별한다.

## 기본 시나리오의 MES 지시와 TC 응답 식별자
```rag-meta
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 메시지 L36~55
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd ; handleEqpConditionCheck L32~45
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd ; handleWoJobreserve L63~91
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd ; handleWoEqpStartReq L37~56
```

제공된 `normal-flow` 시나리오는 다음 이벤트 식별자를 사용한다. 실제 송신은 설비에 연결한 모델 정의에 따른다.

| MES→TC eventType | TC→MES eventType | 구분 |
|---|---|---|
| EQP_CONDITION_CHECK | EQP_CONDITION_CHECK_REPLY | 설비 진행 가능 확인 |
| WO_JOBRESERVE | 별도 응답 대기 없음 | 작업 예약 |
| WO_EQP_START_REQ | WO_EQP_START_REPLY | 가공 시작 지시와 결과 |

## 수집·완료 메시지의 MES 왕복 식별자
```rag-meta
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 수집·완료 L50~69
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd ; handleMatDcspecReply L46~95
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd ; handleMatDatacoll L58~94
```

제공된 `normal-flow` 시나리오의 수집·완료 왕복은 다음 식별자를 사용한다.

| TC→MES eventType | MES→TC eventType | 교환 자료 |
|---|---|---|
| MAT_DCSPEC_REQ | MAT_DCSPEC_REPLY | 수집 규격과 항목 |
| MAT_DATACOLL | MAT_DATACOLL_REPLY | 수집 결과와 수신 결과 |
| WO_EQP_COMPLETE | WO_EQP_COMPLETE_REPLY | 가공 완료와 수신 결과 |

## Carrier·Port 보고에 쓰는 TC 발행 식별자
```rag-meta
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_08_11_fix_normal_flow_port_status_chg_await.sql @ f1d5e0a8e727 ; normal-flow 보고 L46~75
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadReq·publishEqpPortStatusChange L46~96
```

제공된 `normal-flow` 시나리오에서 TC→MES 보고는 다음 이벤트 식별자를 사용한다.

| eventType | 보고 자료 |
|---|---|
| WO_CAR_LOAD | Carrier 반입 |
| EQP_PORT_STATUS_CHG | Port 상태 변경 |
| WO_CAR_UNLOAD_REQ | Carrier 반출 요청 |

Lot·Carrier·작업의 처리 순서는 `00.rag/user/lot-carrier-work-and-system-flow.md`가 다룬다.

## 설비 메시지의 공통 헤더와 추적 ID
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessEventMetadata L76~87
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessEqpCommandKafkaMessage.java @ 7db24f7466bd ; BusinessEqpCommandKafkaMetadata L29~40
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; adaptRecord L65~66
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; adaptRecord L64~65
```

Gateway가 발행하는 설비 이벤트와 Business가 발행하는 설비 명령의 `metadata`는 문자열 `eventType`·`timestamp`·`source`·`traceId`를 사용한다. 네 필드 모두 비어 있지 않아야 한다.

MES·WM 수신 시 Business는 내부 처리용 `traceId`를 새로 발급한다. MES 왕복의 `correlationId`와 내부 처리용 `traceId`를 구별한다.

## SOCKET 설비 이벤트의 본문
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/SocketBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveEventType L32~113
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessSocketEventData L93~102
```

`tc.eqp.events`의 SOCKET 본문은 `data.eqpId`·`data.interfaceType`·`data.rawMessage`를 사용하며 세 값 모두 필수이다. `interfaceType`은 `SOCKET`이다.

`metadata.eventType`은 원문 시작의 `CMD=` 다음 값을 우선 사용한다. 원문에서 찾지 못하면 파싱된 메시지 식별자의 `CMD=` 다음 값을 사용한다. 원문은 `rawLine`, `rawText`, 메시지 식별자와 본문 순서로 찾거나 구성한다.

## SECS 설비 이벤트의 본문 인코딩
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/HsmsBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·resolveHsmsRawBodyBase64 L31~81
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessHsmsEventData·GatewayBusinessHsmsSecs2 L115~193
```

`tc.eqp.events`의 SECS 본문은 `eqpId`·`interfaceType`·`secs2`·`rawMessage`와 선택값 `transactionId`를 `data`에 담는다. `interfaceType`은 `SECS`이다.

`secs2`에는 `systemBytes`·`eventId`·`rawBody`가 있다. SECS-II 바이트 본문은 Base64로 바꾸어 `secs2.rawBody`와 `rawMessage`에 함께 담는다. 빈 바이트 본문은 빈 문자열로 표현할 수 있다.

## PLC 변수는 data에 직접 펼쳐 발행한다
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/PlcBusinessPayloadMapper.java @ d3636ab86763 ; toBusinessPayload·toVariables L30~59
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/contract/GatewayBusinessEventMessage.java @ d3636ab86763 ; GatewayBusinessPlcEventData.variables L151~170
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 ; map L45~65
```

`tc.eqp.events`의 PLC 본문은 `data.eqpId`·`data.interfaceType`과 변수별 필드를 사용한다. `interfaceType`은 `PLC`이며 폴링 응답의 `metadata.eventType`은 그룹의 `groupMessageName`이다.

변수 맵은 `@JsonAnyGetter`로 직렬화하므로 각 변수의 키와 값이 `data`에 직접 들어간다. PLC 발행 매퍼는 파싱 본문이 맵이 아니면 거절한다.

## Business에서 Gateway로 보내는 설비 명령 본문
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/contract/BusinessEqpCommandKafkaMessage.java @ 7db24f7466bd ; BusinessEqpCommandKafkaData L52~64
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; 본문 구성·resolveMessageName L108~121
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; resolveMessageName L201~205
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; 필수 본문 검사 L195~237
```

`tc.eqp.commands`의 `data`에는 `eqpId`·`interfaceType`·`messageName`·`rawMessage`가 필요하다. `messageName`은 명령 속성에서 찾고 없으면 `eventType`을 사용한다.

Gateway의 SECS 명령 수신은 `data.secs2`도 요구한다. 확인한 Business 발행기는 `secs2`를 null로 구성하므로 이 발행 경로는 SECS 수신의 추가 필수 조건을 충족하지 못한다.

## Gateway 대상 토픽의 파티션 지정
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessEqpCommandKafkaPublisher.java @ 7db24f7466bd ; route_partition 지정 L104~129
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; publish L82~108
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayEqpCommandKafkaSubscriber.java @ d3636ab86763 ; bindingMode·assignedPartitions L114~139
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayUiEventKafkaSubscriber.java @ d3636ab86763 ; bindingMode·assignedPartitions L126~155
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publishToGateway L259~267
```

`tc.eqp.commands` 발행기는 설비의 `route_partition`을 조회해 파티션을 명시한다. 일반 UI Gateway 발행도 조회한 파티션을 사용한다.

Gateway는 설비 명령과 UI 요청을 자신이 소유한 파티션에 `assign`하여 읽는다. UI 조정 작업에서 설비 담당 파티션을 옮길 때는 이전·목표 파티션 양쪽에 요청을 발행한다.

## UI 요청의 기본 metadata와 data
```rag-meta
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata·KafkaUiTaskData L40~108
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskData L192~205
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiKafkaContractMapper.java @ 985d17c604dd ; toMetadata L30~52
```

UI 요청은 `metadata`의 `eventType`·`timestamp`·`source`·`traceId`와 `data`의 `eqpId`·`interfaceType`을 필수 문자열로 사용한다.

공유 UI 계약은 `schemaVersion`이 없거나 공백이면 문자열 `1`로 만든다. Backend 발행 매퍼는 조정 정보가 있으면 문자열 `2`로 만든다.

`data.uiMessage`는 작업별 JSON이고 `data.equipmentProfile`은 설비 프로필 스냅샷이다. 두 필드는 공통 생성자에서 필수로 검사하지 않는다.

## UI 조정 요청에 추가되는 metadata
```rag-meta
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskMetadata L40~86
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publishPrepare·publishApply·publish L104~150
```

문자열 `schemaVersion=2`인 UI 계약은 다음 metadata를 추가로 요구한다.

| 필드 | 타입·조건 |
|---|---|
| phase·deadlineAt | 비어 있지 않은 문자열 |
| expectedBaseRevision·desiredRevision | null이 아닌 Long |
| payloadSha256·resourceKeys | 본문 해시 문자열과 리소스 키 목록 |

Backend의 조정 발행은 `PREPARE`·`APPLY`를 phase로 사용하고 `operationId`·`executionId`도 헤더에 전달한다.

## UI 해시와 리소스 목록의 검증 조건
```rag-meta
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; requireSortedResourceKeys L162~179
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskMessage.java @ 6fe71f6b2da6 ; requireSha256Hex L233~244
```

UI의 `schemaVersion=2`에서 `payloadSha256`은 64자의 소문자 16진수여야 한다.

`resourceKeys`는 비어 있지 않은 문자열 목록이며 공백 항목을 허용하지 않는다. 항목은 문자열 비교 기준 오름차순이어야 하고 같은 키를 중복할 수 없다. 누락·빈 목록·역순·중복이면 공유 UI 계약 생성 단계에서 거절한다.

## Gateway·Business에 공통 등록된 UI 이벤트
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L114~196
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L78~123
```

다음은 두 UI 수신 처리기에 모두 등록된 `metadata.eventType`이다. 실제 요청 대상은 Backend 발행 경로가 정한다.

| eventType | 처리 대상 |
|---|---|
| EQP_CREATE | 설비 런타임 생성 |
| EQP_UPDATE | 설비 런타임 갱신 |
| EQP_MODEL_UPDATE | 모델 변경 반영 |
| EQP_DELETE | 설비 런타임 제거 |

## Gateway UI의 통신·직접 송신 이벤트
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L198~275
```

Gateway UI 수신 처리기에 다음 이벤트가 등록되어 있다.

| eventType | 호출하는 처리 |
|---|---|
| EQP_START | 설비 통신 시작 |
| EQP_END | 설비 통신 종료 |
| EQP_RECONNECT | 설비 통신 재연결 |
| EQP_SEND_MESSAGE | uiMessage를 설비 송신 명령으로 변환 |

`EQP_START`·`EQP_END`는 성공 응답을 연결 전환 결과까지 보류하는 방식으로 등록되어 있다.

## Gateway UI의 JAR·PLC 그룹 이벤트
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskProcessorRegistry.java @ d3636ab86763 ; buildSpecs L277~321
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; startPlcGroup·endPlcGroup·requirePlcGroupMessageName L973~1040
```

| eventType | 처리 대상 |
|---|---|
| EQP_GATEWAY_JARFILE_UPDATE | Gateway 플러그인 갱신 |
| EQP_PLC_GROUP_START | PLC 폴링 그룹 시작 |
| EQP_PLC_GROUP_END | PLC 폴링 그룹 종료 |

두 PLC 그룹 요청은 `data.uiMessage.groupMessageName`에 비어 있지 않은 그룹 식별자를 요구한다.

## Business UI의 파라미터·JAR 이벤트
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskProcessorRegistry.java @ 7db24f7466bd ; buildSpecs L125~145
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiModelRuntimeCommandService.java @ 7db24f7466bd ; handleEqpParameterUpdate L224~267
```

| eventType | 처리 대상 |
|---|---|
| EQP_PARAMETER_UPDATE | 설비 파라미터 갱신 |
| EQP_BUSINESS_JARFILE_UPDATE | Business 플러그인 갱신 |

설비 파라미터 갱신은 설비 바인딩과 `uiMessage`의 파라미터 자료를 사용한다. 공유 UI 생성자에서 `uiMessage`를 생략할 수 있어도 작업별 필수 자료가 없으면 처리에 실패한다.

## 조정 발행에서 UI 이벤트가 바뀌는 조건
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; 지원 집합 L48~71
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; publish·wireEventTypeOf L150~165
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiOperationCommandKafkaPublisher.java @ 985d17c604dd ; wireEventTypeOf L235~240
```

Backend의 조정 발행 경로는 Business에 `EQP_UPDATE`·`EQP_DELETE`만 그대로 보내고 나머지 작업 식별자는 `EQP_UPDATE`로 바꾼다.

Gateway 참여가 필요한 조정은 `EQP_CREATE`·`EQP_UPDATE`·`EQP_MODEL_UPDATE`·`EQP_DELETE`·`EQP_GATEWAY_JARFILE_UPDATE`를 그대로 보낸다. 이 집합 밖 값은 `EQP_UPDATE`로 바꾼다.

## UI 처리 응답의 필드와 요청 연결
```rag-meta
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyEventType.java @ 6fe71f6b2da6 ; 응답 이벤트 L33~111
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~94
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayUiCommandKafkaPublisher.java @ d3636ab86763 ; buildReplyMetadata L188~212
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyMetadata L179~201
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; parseStatusOrFail L328~343
원천: nori-tc-ui-backend/ui/tc-ui-domain/src/main/java/com/nori/tc/ui/domain/task/UiTaskStatus.java @ 985d17c604dd ; UiTaskStatus L19~30
```

UI 응답의 `metadata.eventType`은 요청 식별자에 `_REP`를 붙인 값이다. 응답은 요청의 `traceId`를 유지하며 조정 요청이면 조정 metadata도 이어받는다.

`data`의 필수값은 `eqpId`·`interfaceType`·`STATUS`이다. 실패 설명은 `ERRORMSG`·`ERRORCODE`로 전달한다. 결과 필드의 대문자 표기를 유지한다.

`STATUS`는 `PASS`·`FAIL`을 사용한다. Backend는 앞뒤 공백과 대소문자를 정리한 뒤 알 수 없는 값을 `FAIL`로 처리한다.

## UI 응답에 조정 증거가 붙는 경우
```rag-meta
원천: nori-tc-common/messaging/kafka/tc-messaging-kafka-contract/src/main/java/com/nori/tc/messaging/kafka/contract/KafkaUiTaskReplyMessage.java @ 6fe71f6b2da6 ; KafkaUiTaskReplyData L39~70
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessUiTaskReplyPublisher.java @ 7db24f7466bd ; buildReplyData L210~240
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; toCoordinationProof L303~317
```

`data.commandState`가 있는 UI 응답은 `serviceSessionId`·`desiredRevision`·`payloadSha256`도 요구한다. 해시는 64자의 소문자 16진수여야 한다. `ownerEpoch`·`appliedRevision`은 추가 필드이다.

Business 응답은 요청의 revision·해시 또는 조정 증거 제공 결과가 없으면 기본 응답만 만든다. 조정 증거 필드를 모든 UI 응답의 필수값으로 취급하지 않는다.

Backend는 `metadata.operationId`와 `data.commandState`가 모두 있을 때 조정 증거로 읽는다.

## Simulator가 MES 역할로 사용하는 Kafka 경계
```rag-meta
원천: nori-tc-exec/apps/tc-simulator-app/config/tc-messaging.properties @ f1d5e0a8e727 ; MES 역할 토픽 L7~8
원천: nori-tc-exec/apps/tc-simulator-app/config/tc-simulator.properties @ f1d5e0a8e727 ; 시나리오 MES 발행 토픽 L16~16
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; scenarioActionCompiler L65~71
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/transport/UpstreamCommandListener.java @ 5e9b02e3ae16 ; onMessage L33~51
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamWorkerService.java @ 5e9b02e3ae16 ; MES 이벤트 구성 L240~290
```

Simulator의 상위 시스템 작업자는 `tc.simulator.upstream.kafka.command-topic`을 구독한다. 제공 설정은 `dev.mat-evt`이다.

시나리오의 MES 발행 토픽은 `tc.simulator.orchestrator.upstream-event-topic`에서 정하며 제공 설정은 `dev.tc-evt`이다. 이벤트를 만들 때 `eventType`·`timestamp`·`source`·`correlationId`를 metadata에 넣는다.

## 인증·장애·DLQ의 담당 문서
```rag-meta
원천: nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서
```

Kafka 인증 설정과 교체는 `00.rag/admin/security-operations.md`, 전달 중단 진단은 `00.rag/admin/platform-troubleshooting.md`에서 다룬다.

실패 보관 자료를 화면에서 찾는 방법은 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 다룬다.

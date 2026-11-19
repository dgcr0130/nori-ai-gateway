# Lot·Carrier·작업과 MES·WM 흐름
```rag-meta
유형: 개념
적용: nori-tc-business 7db24f7466bd·nori-tc-gateway d3636ab86763·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## Lot·Carrier·작업은 무엇을 연결하는가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; createWork L80~159
원천: nori-tc-business/business/tc-business-domain/src/main/java/com/nori/tc/business/domain/work/BusinessWorkLot.java @ 7db24f7466bd ; BusinessWorkLot L8~15
원천: nori-tc-business/business/tc-business-domain/src/main/java/com/nori/tc/business/domain/work/BusinessWorkCarrier.java @ 7db24f7466bd ; BusinessWorkCarrier L8~17
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkContextResolverSupport.java @ 7db24f7466bd ; resolveWorkEntry L37~65
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd ; handleMatDcspecReply L46~95
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 5~7
```

| 대상 | 작업 흐름에서의 역할 |
|---|---|
| Lot | 가공 대상이다. TC는 Lot ID로 설비의 작업을 찾고 수집 정보를 연결한다. |
| Carrier | 설비의 Port로 반입·반출하는 대상이다. 작업에는 Carrier ID와 Port ID를 연결한다. |
| 작업 | 작업 ID를 기준으로 Lot·Carrier·레시피·제어 작업·공정 작업을 묶는 처리 단위이다. |

TC는 설비 ID와 Lot ID로 작업을 먼저 찾고, 찾지 못하면 Carrier ID로 찾는다. Lot ID·Carrier ID·작업 ID는 각각 구별해 읽는다.

## MES 지시와 설비 보고는 어느 경로로 오가는가
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L43~56
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~127
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; dispatchBusinessCommand L149~250
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; publish L79~140
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; executeNonUiTask L589~605
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; 토픽 방향 L6~18
```

설비로 보내는 MES 지시는 MES → TC Business → TC Gateway → 설비 순서로 전달한다. Lot 작업 보고는 설비 → TC Gateway → TC Business로 들어온다. Business는 작업을 반영하고 전송 정의에 따라 MES 또는 WM에 보고한다.

아래 Lot 흐름은 설비에 연결된 모델에 해당 메시지의 흐름·액션·전송 정의가 있는 경우를 설명한다. 일반 업무 메시지는 모델과 일치하는 흐름이 있어야 액션을 실행한다.

## 진행 가능 확인과 작업 예약은 어떻게 이어지는가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd ; handleEqpConditionCheck L32~37
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/EqpConditionCheckReplyTcAction.java @ 7db24f7466bd ; handleEqpConditionCheckReply L33~47
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd ; handleWoJobreserve L63~91
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd ; publishDemoWoJobreserveCommand L141~163
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd ; handleWoEqpStartReq L37~51
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 5
```

해당 메시지 정의가 있는 SOCKET 모델의 대표 흐름은 다음 순서로 진행한다.

1. MES가 `EQP_CONDITION_CHECK`로 설비의 진행 가능 여부를 묻고 TC가 설비로 전달한다.
2. 설비의 `EQP_CONDITION_CHECK_REPLY`를 TC가 MES에 전달한다.
3. MES가 `WO_JOBRESERVE`로 작업을 예약한다. TC는 같은 설비·Lot의 작업이 없으면 만들고 예약을 설비로 전달한다.

예약보다 먼저 `WO_EQP_START_REQ`가 들어오는 경우에도 TC는 같은 설비·Lot의 작업이 없으면 만든다.

## Carrier 반입과 Port 보고는 무엇을 알리는가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarLoadTcAction.java @ 7db24f7466bd ; handleWoCarLoad L28~37
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishMesCommand L137~169
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishEqpPortStatusChange L199~241
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 5
```

SOCKET 흐름에서 설비의 `WO_CAR_LOAD`는 Carrier 반입 보고이다. Business는 보고에 지정된 Port를 반입 상태로 바꾼다.

MES 전송 정의가 `WO_CAR_LOAD`인 흐름에서는 TC가 반입 사실을 MES에 보낸다. 별도 `EQP_PORT_STATUS_CHG` 정의가 있으면 Port 상태 보고를 예약한다. 반입 보고와 Port 상태 보고는 보고 내용이 다르다.

## MES 시작 지시와 설비 시작·종료 보고는 어떻게 다른가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd ; handleWoEqpStartReq L37~56
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishEqpCommand L112~128
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd ; handleWoEqpStarted L40~52
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpEndedTcAction.java @ 7db24f7466bd ; handleWoEqpEnded L40~52
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 6
```

SOCKET 흐름에서 `WO_EQP_START_REQ`는 MES의 가공 시작 지시이다. TC는 모델의 설비 전송 정의에 따라 시작 지시를 설비로 보낸다.

설비의 `WO_EQP_STARTED`는 시작 보고이고, `WO_EQP_ENDED`는 종료 보고이다. Business는 대상 작업을 찾아 각각 시작·종료를 반영한다. 시작 지시 수신과 설비의 실제 시작 보고는 구별한다.

## Lot 수집 항목과 수집 결과는 MES와 어떻게 연결되는가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReqTcAction.java @ 7db24f7466bd ; handleMatDcspecReq L10~24
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd ; handleMatDcspecReply L46~95
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd ; collectIfAny L86~142
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoWorkInformTcAction.java @ 7db24f7466bd ; handleWoWorkInform L29~42
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; collectIfAny 호출 L330~343
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd ; handleMatDatacoll L58~94
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 11
```

수집 흐름이 정의된 모델에서는 TC가 `MAT_DCSPEC_REQ`로 MES에 수집 규격을 요청한다. `MAT_DCSPEC_REPLY`의 규격과 항목은 설비·Lot 또는 Carrier로 찾은 작업에 연결한다.

SOCKET 설비의 `WO_WORK_INFORM` 등 값 보고에서 Business는 모델 흐름에 연결된 수집 항목 값을 모은다.

`MAT_DATACOLL` 처리에서는 해당 작업의 수집 규격별 최종 값을 만들어 모델에서 정한 보고 대상으로 보낸다. MES 대상 흐름에서는 MES가 수집 결과를 받는다.

## 가공 완료와 Carrier 반출 완료는 언제 구별하는가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd ; handleWoEqpComplete L40~52
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpCompleteReplyTcAction.java @ 7db24f7466bd ; handleWoEqpCompleteReply L15~21
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadReq L46~77
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadCompReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadCompReq L49~77
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; clearCarrierIdWhenIdle L129~132
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 7
```

SOCKET 흐름에서 설비의 `WO_EQP_COMPLETE`는 가공 완료 보고이다. Business는 작업 완료를 반영하고 모델의 목적지로 보고한다. MES의 `WO_EQP_COMPLETE_REPLY` 처리기는 응답 수신을 기록한다.

이후 설비의 `WO_CAR_UNLOAD_REQ`는 Carrier 반출 요청이다. Lot이 있고 Port 변경이 성공하면 Business는 반출 상태를 반영하고 모델의 목적지로 요청을 전달한다.

설비의 `WO_CAR_UNLOAD_COMP_REQ`는 반출 완료이다. Lot이 있고 Port 변경이 성공하면 Port의 Carrier ID를 지우고, 찾은 작업을 닫은 뒤 작업 정리를 수행한다. 가공 완료와 반출 완료는 서로 다른 보고이다.

## WM은 Lot·설비 흐름에 어떻게 참여하는가
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L42~55
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd ; publishInternal L280~295
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessWmCommandKafkaPublisher.java @ 7db24f7466bd ; publish L74~103
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.properties @ f1d5e0a8e727 ; WM 별도 경로 L7~18
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 14
```

WM이 보낸 이벤트도 TC Business가 받는다. Business는 대상 설비의 모델 흐름을 실행하고, 전송 정의의 목적지가 WM이면 WM으로 메시지를 보낸다.

일반 설비 보고의 목적지는 모델의 메시지 정의가 MES 또는 WM으로 정한다. WM 연동은 MES와 별도 수신·발신 경로를 사용하며 Lot을 기준으로 보고를 연결한다.

## 연결 때의 Port 보고와 Lot 작업 보고는 무엇이 다른가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 우선 처리 L578~605
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncReplyHandler.java @ 7db24f7466bd ; publishPortReports L219~276
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionDisconnectPortStatusHandler.java @ 7db24f7466bd ; publishPortReports L112~145
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/connection/ConnectionPortStatusSyncProtocol.java @ 7db24f7466bd ; 연결 이벤트·보고 상수 L14~29
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 4·13·14
```

연결 시 `EQP_PORT_STATUS_SYNC_REP` 응답과 연결 끊김의 `EQP_DISCONNECTED`는 Business의 공통 처리 경로로 들어온다. Lot 작업의 모델 흐름과 별도로 Port별 `EQP_PORT_STATUS_CHG`를 만든다.

연결 보고는 MES·WM 각각의 전송 설정에 따라 보낸다. 두 대상이 모두 켜져 있으면 같은 Port 정보를 양쪽에 보낸다. Port 상태 보고는 Carrier 반입·가공 완료 같은 특정 Lot 작업 보고와 구별한다.

## PLC의 설비 값은 Lot 업무 메시지와 어떻게 다른가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 ; startGroup L69~83
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 ; tick L158~178
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 ; map L45~65
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; processFrame L153~171
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; executeNonUiTask L589~605
원천: nori-tc-exec/docs/completion-report/05-work-scenario.md @ f1d5e0a8e727 ; 슬라이드 8~9
```

PLC에서는 TC가 켜진 폴링 그룹의 주기에 따라 설비 값을 읽는다. Gateway는 응답 값을 모델의 변수로 풀고 그룹의 메시지명을 붙인다. 변화가 있는 값 등 발행 조건을 만족한 응답이 업무 처리로 넘어간다.

MES의 Lot 예약·시작 지시는 PLC 값 읽기와 별도 경로로 Business에 들어온다. Business는 대상 설비의 모델 흐름을 적용한다. SOCKET의 Carrier·작업 보고 순서와 PLC의 주기 값 읽기를 구별한다.

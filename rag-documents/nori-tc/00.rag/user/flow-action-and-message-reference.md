# Flow 액션·메시지 종류
```rag-meta
유형: 참조
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-business 7db24f7466bd 소스의 기본 액션 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/model-details-and-flow-editing.md
```

## Flow의 메시지명과 액션명은 무엇을 정하는가
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Message Name·Action Name L298~314 L393~401
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; 액션 실행 L260~304
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; resolveCandidates L147~158
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/resolution/ActionResolutionPolicy.java @ 7db24f7466bd ; resolve L67~104
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목
```

| Flow 항목 | 역할 |
|---|---|
| Message Name | Flow가 처리할 수신 메시지명을 고른다. |
| Action Name | 일치한 Flow에서 실행할 액션을 자유 입력한다. |

이 문서는 기본 액션 기준이다. 설비별 Business 플러그인에 같은 종류·이름의 액션이 등록되면 플러그인 액션을 먼저 실행한다. Flow 편집·저장 방법은 `00.rag/user/model-details-and-flow-editing.md`에서 확인한다.

## Message Name 후보는 모델의 어느 정의에서 오는가
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 후보 L240~270 L574~583
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd ; 모델별 메시지 조회 L127~155 L248~257
```

「Message Name」 후보는 선택한 모델 버전의 정의에서 가져온다. 고정된 공통 메시지명 목록은 아니다.

| 모델 통신 방식 | 설비 측 후보의 원천 | 함께 포함하는 원천 |
|---|---|---|
| SECS | SECS Message | MES Message |
| SOCKET | Socket Message | MES Message |
| PLC | PLC Group의 Message Name | MES Message |

같은 메시지명이 여러 원천에 있으면 「Message Name」 목록에는 한 번만 표시한다.

## 수신 메시지 종류에 따라 어느 액션을 실행하는가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/api/action/BusinessWorkflowActionMessageType.java @ 7db24f7466bd ; from L23~42
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessWorkflowCoreActionRegistry.java @ 7db24f7466bd ; 실행기 등록 L51~65
```

기본 액션은 액션명과 수신 메시지 종류를 함께 대조해 실행한다. 아래의 「설비 측」·「MES 측」은 이 구분을 뜻한다.

| 수신 조건 | 액션을 찾는 종류 |
|---|---|
| 설비 메시지이며 SECS 모델 | SECS |
| 설비 메시지이며 SOCKET·PLC 모델 | SOCKET |
| MES 또는 WM 메시지 | MES |

설비 공통 액션은 SECS·SOCKET 양쪽에 등록한다. 같은 액션명도 MES 측 처리와 설비 측 처리가 다를 수 있다.

## 액션의 송신과 MDF 자동 송신은 어떻게 연결되는가
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; 액션 후 자동 발행 L298~313
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd ; publishInternal·publishMes L272~324
```

액션 실행이 정상 종료하고 송신 처리 완료 표시가 없으면 「Action Data Index」에 지정한 MDF 메시지의 목적지에 따라 설비·MES·WM으로 자동 송신한다. MDF 메시지 지정이나 모델의 MDF 정의가 없으면 이 자동 송신을 하지 않는다.

아래 표의 「보고」는 MDF 정의에 따른 송신이다. 설비 측 공통 보고의 목적지는 MES 또는 WM이며, MES 보고에는 Lot ID가 필요하다. 액션명만으로 송신 완료를 뜻하지 않는다.

## 로그 기록과 MES 공통 송신 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreSecsActionExecutor.java @ 7db24f7466bd ; logOnly L21~33
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreSocketActionExecutor.java @ 7db24f7466bd ; logOnly L21~33
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreMesActionExecutor.java @ 7db24f7466bd ; 기본 액션 L47~77
```

| 액션 | 수신 측 | 기본 처리 |
|---|---|---|
| LOG_ONLY | 설비·MES 측 | Flow 실행 사실을 로그에 기록한다. 액션 자체의 직접 송신은 없다. |
| PUBLISH_MES_COMMAND | MES 측 | MES 명령 메시지를 조립해 MES로 보낸다. MDF 정의가 있으면 그 정의로 내용을 만든다. |

`LOG_ONLY`를 선택한 Flow도 MDF 자동 송신 조건을 만족하면 후속 송신이 발생한다.

## 설비에 조회·응답을 전달하는 MES 측 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/ToolConditionRequestTcAction.java @ 7db24f7466bd ; handleToolConditionRequest L61~75
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd ; handleEqpConditionCheck L32~46
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/CarrierIdReadReplyTcAction.java @ 7db24f7466bd ; handleCarrierIdReadReply L38~52
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/MesToEqpPublishSupport.java @ 7db24f7466bd ; publish L84~107
```

MES 측의 다음 액션은 선택한 MDF로 설비 명령을 만든다. 설비 통신 방식은 대상 모델에서 가져온다.

| 액션 | 기본 처리 |
|---|---|
| TOOL_CONDITION_REQUEST | 수신한 설비 조건 요청을 설비로 전달한다. |
| EQP_CONDITION_CHECK | 수신한 설비 조건 확인 요청을 설비로 전달한다. |
| CARRIERID_READ_REPLY | 수신한 Carrier ID 읽기 응답을 설비로 전달한다. |

MDF 송신 정의가 없으면 각 액션의 직접 송신을 건너뛴다.

## 설비의 조건 응답·준비·작업 정보를 보고하는 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ToolConditionReplyTcAction.java @ 7db24f7466bd ; handleToolConditionReply L29~44
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/EqpConditionCheckReplyTcAction.java @ 7db24f7466bd ; handleEqpConditionCheckReply L33~48
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ReadyToWorkTcAction.java @ 7db24f7466bd ; handleReadyToWork L29~44
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkInformTcAction.java @ 7db24f7466bd ; handleWorkInform L29~44
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoWorkInformTcAction.java @ 7db24f7466bd ; handleWoWorkInform L29~43
```

설비 측의 다음 액션은 보고 메시지를 조립해 전달한다.

| 액션 | 기본 처리 |
|---|---|
| TOOL_CONDITION_REPLY | 수신한 조건 응답의 이벤트명으로 보고한다. |
| EQP_CONDITION_CHECK_REPLY | 수신한 조건 확인 응답의 이벤트명으로 보고한다. |
| READY_TO_WORK | 수신한 준비 보고의 이벤트명을 유지해 전달한다. |
| WORK_INFORM | 보고 이벤트명을 WORK_INFORM으로 정해 전달한다. |
| WO_WORK_INFORM | 수신한 작업 정보의 이벤트명을 유지해 전달한다. |

이 액션들은 작업 생성이나 작업 상태 변경을 직접 수행하지 않는다.

## Carrier 반입·반출 요청에서 Port를 바꾸는 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierLoadedTcAction.java @ 7db24f7466bd ; handleCarrierLoaded L28~37
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarLoadTcAction.java @ 7db24f7466bd ; handleWoCarLoad L28~37
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierIdReadTcAction.java @ 7db24f7466bd ; handleCarrierIdRead L36~65
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/UnloadRequestTcAction.java @ 7db24f7466bd ; handleUnloadRequest L36~65
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 상태 상수·전이·Port 검사 L24~26 L57~80 L99~106
```

설비 측 액션이며 수신값의 Port ID를 사용한다.

| 액션 | 기본 처리 |
|---|---|
| CARRIER_LOADED·WO_CAR_LOAD | Port를 LOAD로 바꾼다. 직접 보고는 없으며 후속 송신은 MDF 자동 송신에 따른다. |
| CARRIERID_READ | Lot ID가 있고 Port를 LOAD로 바꾸는 데 성공하면 보고한다. |
| UNLOAD_REQUEST | Lot ID가 있고 Port를 UNLOAD로 바꾸는 데 성공하면 보고한다. |

Port ID가 비어 있으면 Port 상태 변경을 수행하지 못한다.

## Carrier 검증과 작업 생성 응답을 잇는 설비 측 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierVerificationTcAction.java @ 7db24f7466bd ; handleCarrierVerification L29~50
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessjobcreateReplyTcAction.java @ 7db24f7466bd ; handleProcessjobcreateReply L37~57
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ControljobcreateReplyTcAction.java @ 7db24f7466bd ; handleControljobcreateReply L29~44
```

| 액션 | 기본 처리 |
|---|---|
| CARRIER_VERIFICATION | 외부 검증 없이 CARRIER_VERIFICATION_PASS 이벤트로 보고한다. |
| PROCESSJOBCREATE_REPLY | MDF가 있으면 CONTROLJOBCREATE 명령을 설비로 보낸다. |
| CONTROLJOBCREATE_REPLY | WORK_START_REPLY 이벤트로 보고한다. |

## 작업 시작·종료·완료를 반영하는 설비 측 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkStartedTcAction.java @ 7db24f7466bd ; handleWorkStarted L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd ; handleWoEqpStarted L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkEndedTcAction.java @ 7db24f7466bd ; handleWorkEnded L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpEndedTcAction.java @ 7db24f7466bd ; handleWoEqpEnded L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkCompletedTcAction.java @ 7db24f7466bd ; handleWorkCompleted L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd ; handleWoEqpComplete L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; 상태 전이·역행 방지 L68~98 L254~305
```

대상 작업이 있으면 아래 상태 전이를 적용하고 보고한다. 작업을 찾지 못하면 상태 변경 없이 보고를 시도한다.

| 액션 | 상태를 바꾸는 대상과 목표 |
|---|---|
| WORK_STARTED·WO_EQP_STARTED | 작업·제어 작업을 start로 바꾼다. |
| WORK_ENDED·WO_EQP_ENDED | 작업·제어 작업을 end로 바꾼다. |
| WORK_COMPLETED·WO_EQP_COMPLETE | 작업·제어 작업·공정 작업을 completed로 바꾼다. |

진행된 상태를 되돌리지 않는 상태 전이 규칙을 적용한다.

## 공정 시작·종료를 반영하는 설비 측 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessStartedTcAction.java @ 7db24f7466bd ; handleProcessStarted L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessEndedTcAction.java @ 7db24f7466bd ; handleProcessEnded L40~61
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; markProcessStart·markProcessEnd L75~84
```

| 액션 | 대상 작업이 있을 때의 기본 처리 |
|---|---|
| PROCESS_STARTED | 공정 작업과 작업·제어 작업에 start 전이를 적용하고 보고한다. |
| PROCESS_ENDED | 공정 작업에 end 전이를 적용하고 보고한다. |

두 설비 측 액션은 대상 작업을 찾지 못하면 상태 변경 없이 보고를 시도한다.

## Carrier 반출 완료 때 작업을 닫는 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierUnloadRequestTcAction.java @ 7db24f7466bd ; handleCarrierUnloadRequest L49~89
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadCompReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadCompReq L49~89
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; markClosed L103~105
```

| 설비 측 액션 | 기본 처리 |
|---|---|
| CARRIER_UNLOAD_REQUEST·WO_CAR_UNLOAD_COMP_REQ | Port를 IDLE로 바꾸고 대상 작업·제어 작업에 closed 전이를 적용한다. 작업 삭제 처리를 호출한 뒤 보고한다. |

Lot ID가 없거나 Port 상태 변경에 실패하면 이후 처리를 진행하지 않는다. 대상 작업이 없으면 작업 상태 변경은 건너뛴다.

## WO_CAR_UNLOAD_REQ가 만드는 반출·Port 보고
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadReq·publishEqpPortStatusChange L46~110
```

설비 측 `WO_CAR_UNLOAD_REQ`는 Lot ID가 있고 Port를 UNLOAD로 바꾸는 데 성공하면 반출 요청을 보고한다. 직접 보고가 이루어진 뒤에는 `EQP_PORT_STATUS_CHG`에 반출 상태를 실어 지연 보고를 예약한다.

`WO_CAR_UNLOAD_REQ`의 직접 처리에서는 작업을 삭제하지 않는다. 작업을 닫고 삭제하는 `WO_CAR_UNLOAD_COMP_REQ`와 구별한다.

## MES의 작업 지시를 받는 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkOrderRequestTcAction.java @ 7db24f7466bd ; handleWorkOrderRequest L50~73
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd ; handleWoEqpStartReq L37~57
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd ; handleWoJobreserve·후속 송신 L63~91 L103~144
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; 생성 필수 입력 L80~108
```

| MES 측 액션 | 기본 처리 |
|---|---|
| WORK_ORDER_REQUEST | 작업을 생성하고 MDF가 있으면 WORK_ORDER_REPLY로 응답한다. |
| WO_EQP_START_REQ | 같은 설비·Lot의 작업이 없을 때 작업을 생성한다. 설비 송신은 MDF 자동 송신에 따른다. |
| WO_JOBRESERVE | Port의 Carrier ID 기록을 시도하고, 같은 설비·Lot의 작업이 없으면 생성한다. MDF가 있으면 MES로 응답하며 WO_JOBRESERVE 원문 명령도 설비 송신 처리에 넘긴다. |

작업 생성에는 작업 ID·레시피 ID와 Lot ID 또는 Carrier ID가 필요하다.

## 작업 생성·삭제를 재사용하는 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/tcWork/CreateWorkTcAction.java @ 7db24f7466bd ; createWork L21~45
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/tcWork/DeleteWorkTcAction.java @ 7db24f7466bd ; deleteWork L21~45
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; createWork L80~159
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleDeleteSupport.java @ 7db24f7466bd ; deleteWork·deleteResolvedWork L60~119
```

| MES 측 액션 | 기본 처리 |
|---|---|
| CREATE_WORK | 수신한 작업 정보로 Lot·Carrier·제어 작업·공정 작업을 묶어 생성한다. |
| DELETE_WORK | 설비의 Lot ID 또는 Carrier ID로 대상 작업을 찾아 수집 상태 정리를 시도하고 작업을 삭제한다. |

두 액션은 다른 업무 액션에서도 호출하는 공통 처리이다. 삭제 대상 작업이 없으면 삭제하지 않는다.

## 수집 규격 응답으로 수집 상태를 준비하는 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/DcspecreqReplyTcAction.java @ 7db24f7466bd ; handleDcspecreqReply L70~114
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd ; handleMatDcspecReply L46~98
```

| MES 측 액션 | 기본 처리 |
|---|---|
| DCSPECREQ_REPLY | 대상 작업의 첫 공정 작업 ID에 dcspecValue의 항목을 연결해 수집 상태를 초기화한다. |
| MAT_DCSPEC_REPLY | 대상 작업 ID·dcspecId별로 dcolItemList를 사용해 수집 상태를 초기화한다. |

대상 작업이 없으면 초기화하지 않는다. `DCSPECREQ_REPLY`는 공정 작업 ID가 없을 때, `MAT_DCSPEC_REPLY`는 dcspecId 또는 작업 ID가 없을 때도 초기화하지 않는다.

## 수집 결과를 조립해 보고하는 설비 측 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/DatacollTcAction.java @ 7db24f7466bd ; reportDatacoll L102~183
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd ; handleMatDatacoll·resolveDatacollTarget L58~99 L118~155
```

| 액션 | 기본 처리 |
|---|---|
| DATACOLL | 공정 작업의 수집 상태에 수집·계산 규칙을 적용해 dcspecValue로 보고하고, 직접 보고 성공 뒤 수집 상태 삭제를 시도한다. |
| MAT_DATACOLL | 대상 작업의 수집 규격마다 최종값을 계산해 dcspecId·dcolItemList로 보고한다. 이 액션은 수집 상태를 직접 삭제하지 않는다. |

`DATACOLL`은 작업·공정 작업 ID·correlationId·수집 상태가 있어야 직접 보고한다. `MAT_DATACOLL`은 대상 수집 상태가 없으면 보고를 건너뛴다.

## 기록만 남기는 MES 측 요청·응답 액션
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/DatacollReplyTcAction.java @ 7db24f7466bd ; handleDatacollReply L23~29
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkCompletedReplyTcAction.java @ 7db24f7466bd ; handleWorkCompletedReply L23~29
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpCompleteReplyTcAction.java @ 7db24f7466bd ; handleWoEqpCompleteReply L15~22
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkStartRequestTcAction.java @ 7db24f7466bd ; handleWorkStartRequest L21~27
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReqTcAction.java @ 7db24f7466bd ; handleMatDcspecReq L19~25
```

아래 MES 측 액션의 기본 메서드는 실행 기록만 남긴다. 후속 송신은 MDF 자동 송신에 따른다.

| 액션 | 직접 처리의 범위 |
|---|---|
| DATACOLL_REPLY·WORK_COMPLETED_REPLY | 응답 처리 기록을 남긴다. |
| WO_EQP_COMPLETE | 응답 처리 기록을 남긴다. 설비 측의 같은 액션명과 달리 작업 상태를 바꾸지 않는다. |
| WORK_START_REQUEST·MAT_DCSPEC_REQ | 요청 처리 기록을 남기며 직접 송신하지 않는다. |


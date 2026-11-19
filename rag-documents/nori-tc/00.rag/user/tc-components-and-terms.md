# TC 구성 요소·용어
```rag-meta
유형: 개념
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-common 6fe71f6b2da6 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## Gateway와 Business는 어떤 일을 맡는가
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 연결 시작 분기 L310~330
원천: nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java @ d3636ab86763 ; 수신·송신 처리 L180~250
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 모델 매칭·액션 실행 L585~605
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관점 카드·목록 렌더 L1125~1209
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점 표시 L33~43
```

Gateway는 설비 연결과 메시지 수신·송신을 맡는 구성 요소다. Business는 받은 메시지를 모델의 조건에 맞추고 해당 액션을 실행하는 구성 요소다.

「설비」 화면의 「Gateway」·「Business」 카드는 두 구성 요소를 기준으로 자료를 나누어 보여 준다. Gateway 관점에서는 설비 목록을, Business 관점에서는 해당 설비들이 참조하는 모델 목록을 본다.

## Shard group과 Route Partition은 어떻게 연결되는가
```rag-meta
원천: nori-tc-common/common/tc-common-shard/src/main/java/com/nori/tc/common/shard/SharedShardGroupProperties.java @ 6fe71f6b2da6 ; 그룹별 파티션 검사 L56~90
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 그룹 획득·담당 파티션 반영 L206~236
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; 설비별 파티션 지정 L86~108
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 그룹 분류 L160~210
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Route Partition 열 L131~138
```

Shard group은 Gateway 인스턴스가 맡는 Kafka 파티션의 묶음이다. 「설비」 화면에서 Gateway 쪽 설비를 그룹으로 나누는 기준으로 사용한다.

「Route Partition」은 설비에 보낼 메시지의 파티션 번호다. 설비의 번호가 Shard group의 담당 파티션에 포함되면 그 그룹에 분류한다.

## Lease·Pod·spare는 무엇을 구별하는가
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 결과와 소유 기한 L206~236
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자·대기 인스턴스 판정 L55~97
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; Pod·spare 표시 L290~338
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 소유·대기 안내 L595~598
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd ; 그룹과 소유자 조회 L38~47
```

| 용어 | 뜻과 만나는 위치 |
|---|---|
| Lease | Gateway가 특정 Shard group을 맡는 기한 있는 소유권이다. 설비 화면의 그룹 점유 현황을 설명할 때 사용한다. |
| Pod | Gateway 같은 프로그램을 실행하는 배포 단위다. 담당 Pod를 확인할 수 있으면 설비 트리의 그룹 아래에 식별 문구를 표시한다. |
| spare | 동기화를 마쳤지만 Shard group을 맡지 않은 대기 Gateway 인스턴스다. 설비 트리에서 「spare」로 표시한다. |

Shard group은 담당 구역이고 Pod는 실행 단위이므로 서로 다른 개념이다.

## 설비와 라인은 무엇인가
```rag-meta
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/eqp/TcEqp.java @ 6fe71f6b2da6 ; 설비 식별·소속·통신 정보 L41~55
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; EQPID·통신 열 L111~138
원천: nori-tc-exec/docs/Architecture/00-system/05-glossary.md @ f1d5e0a8e727 ; 제조 도메인의 EQP·LINE
```

설비(EQP)는 TC가 식별하고 통신하는 장비 한 대다. 「설비」 화면의 「EQPID」는 그 장비를 구별하는 설비 ID다.

라인은 설비가 소속되는 묶음이다. 구조 문서의 LINE은 이 소속 단위를 가리킨다. 설비마다 소속 라인과 통신 정보가 연결된다.

## 모델과 모델 버전은 무엇이 다른가
```rag-meta
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/eqp/TcEqp.java @ 6fe71f6b2da6 ; 모델 버전 참조 L41~54
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMessageFlow.java @ 6fe71f6b2da6 ; 버전별 메시지·조건·액션 L30~39
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Model·Model Version 표시 L147~151
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 버전 상세 복제 L348~362
```

모델은 설비 메시지와 처리 규칙 등을 묶어 관리하는 정의다. 모델 버전은 그 정의를 특정 판으로 구별한 것이다. 설비는 사용할 모델 버전을 참조한다.

설비 정보의 「Model」은 모델을, 「Model Version」은 연결한 판을 보여 준다. 같은 모델이라도 연결한 버전이 다르면 사용할 정의가 달라질 수 있다.

## root·branch·suffix는 모델에서 무엇을 뜻하는가
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; branch 복제 L229~263
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; branch 명명 L184~187
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 모델 트리 L156~250
원천: nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; suffix 입력 L133~190
```

| 용어 | 뜻 |
|---|---|
| root | 모델 트리에서 branch의 기준이 되는 부모 모델이다. |
| branch | root의 선택 버전 내용을 복제해 따로 편집하는 모델이다. |
| suffix | 「Branch Model Create」에서 branch를 구별하도록 입력하는 이름 조각이다. 부모 모델명·suffix·사용자 아이디를 밑줄로 이어 branch 이름을 만든다. |

모델 생성·복제 절차는 `00.rag/user/model-registration-and-branching.md`에서 다룬다.

## Check Out과 Check In은 어떤 편집 경계인가
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 모델 작업본과 확정 L278~362
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 파라미터 편집·확정 L84~141
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 저장된 EDIT의 버전 생성 L224~261
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 편집 단추 L790~836
```

「Check Out」은 기준 버전에서 편집용 작업본을 여는 것이고, 「Check In」은 서버에 저장된 작업본을 새 버전으로 확정하는 것이다. 모델 상세와 설비 파라미터에서 사용하는 말이다.

모델 편집 절차는 `00.rag/user/model-details-and-flow-editing.md`, 설비 파라미터 편집 절차는 `00.rag/user/equipment-model-parameter-and-jar-update.md`에서 다룬다.

## parent commit은 어느 모델에 반영하는가
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 부모 새 버전 복제 L421~462
원천: nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; commit-parent 요청 L465~513
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; Parent Model Commit 메뉴 L183~190
```

parent commit은 branch의 최신 내용을 부모 root의 새 버전으로 옮기는 작업이다. 모델 트리의 branch 메뉴에서는 「Parent Model Commit」으로 만난다.

branch에서 Check In으로 branch 버전을 확정하는 것과 반영 대상이 다르다. parent commit 절차는 `00.rag/user/model-registration-and-branching.md`에서 다룬다.

## Workflow와 Flow는 메시지 처리에서 무엇인가
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; 그룹과 메시지 표시 L50~109
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Flow 식별·조건·액션·수집 L274~464
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowQueryPort.java @ 985d17c604dd ; 목록·상세·수집 연결 L63~117
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; 후보 조건 평가 L64~117
```

모델 상세의 「Message Flows」에서 Workflow는 하위 메시지별 Flow를 묶는 이름이다. Flow는 그 이름과 메시지의 조합에 대해 처리 조건·실행 액션·수집 항목을 정의한 한 건이다.

목록의 상위 줄은 묶음 이름, 하위 줄은 메시지 이름이다. 하위 메시지에 대응하는 Flow 카드에서 조건과 할 일을 본다.

편집 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다.

## MDF는 어떤 파일인가
```rag-meta
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMdf.java @ 6fe71f6b2da6 ; MDF XML 원문 L5~20
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; 메시지 선택·대상별 발행 L85~108
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; MDF 표시 L942~978
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; saveMdf L186~205
```

MDF는 모델 버전에 연결하는 XML 메시지 서식이다. 모델 상세의 「Reference」 아래 「MDF」에서 만난다.

Business는 선택한 MDF 메시지 정의로 발행할 메시지를 구성하며, 정의의 대상에 따라 설비·MES·WM으로 보낸다. 업로드 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다.

## DCOP 수집 항목은 Flow와 어떤 관계인가
```rag-meta
원천: nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Dcop Itemes 표기 L310~321
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Collect 영역 L423~464
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowQueryPort.java @ 985d17c604dd ; 수집 항목 연결 조회 L91~117
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd ; Flow별 수집 대상 L86~108
```

DCOP 항목은 모델에서 정의하고 Flow에 연결하는 데이터 수집 항목이다. 모델 상세의 「Dcop Itemes」는 항목 정의를, Flow 카드의 「Collect」는 해당 Flow와의 연결을 다룬다.

Business는 Flow에 연결된 항목을 수집 대상으로 고른다. 정의와 연결을 편집하는 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다.

## 배포는 어떤 변경을 묶는 작업인가
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 대상별 변경값 조회 L271~325
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델·JAR·파라미터 입력 L354~373
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 성공값 반영 L25~76
```

배포는 대상 설비에 적용할 모델 버전·Business JAR·설비 파라미터 버전 변경을 묶어 실행하는 작업이다. 「배포」 화면에서 대상과 변경할 값을 함께 다룬다.

등록·실행·이력 확인 절차는 `00.rag/user/deployment-workflow.md`에서 다룬다.

## DLQ와 payload는 실패 조회에서 무엇을 뜻하는가
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 실패·payload 참조 열 L14~36
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; DLQ 카드·참조 문구 L1062~1084
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 실패 메시지 기록 L398~435
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; DLQ 조회 L95~128
```

DLQ는 처리에 실패한 메시지를 사유와 함께 따로 보관하는 곳이다. 「처리실패」 화면의 「Gateway DLQ」·「Business DLQ」로 처리 주체를 구별한다.

payload는 처리 대상 메시지의 내용이다. DLQ 표의 「payload 참조」는 그 내용을 가리키는 참조값이다. 실패 항목 조회 절차는 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 다룬다.

## 격리 설비와 Repair Task는 무엇이 다른가
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; 설비별 격리 사유 기록 L62~85
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpRepairTaskService.java @ 985d17c604dd ; 복구 작업 기록 L29~48
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 되돌림 실패 기록 L132~169
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 격리 설비 표시 L14~30
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 복구 항목 표시 L14~28
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 카드 표기 L125~128
```

「격리 설비」는 Gateway가 설비별로 남긴 격리 사유를 보여 주는 목록이다. 「Repair Task」는 되돌림 같은 복구 처리에 실패했을 때 시스템이 남기는 작업 기록이다.

두 목록은 「처리실패」 화면의 별도 카드다. 격리는 설비와 사유를, Repair Task는 복구할 작업과 액션을 중심으로 구별한다. 확인 절차는 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 다룬다.

## SECS와 HSMS는 어느 부분을 가리키는가
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; HSMS·SECS 처리 조립 L148~197
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; SECS 인터페이스 연결 L222~240
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 설정 L1112~1218
```

TC에서 SECS는 SECS-II 메시지를 주고받는 설비 통신 방식이다. HSMS는 이 메시지를 실어 나르는 통신 계층이다. Gateway는 HSMS 프레임을 추출한 뒤 SECS-II 메시지를 해석한다.

설비의 「Comm Interface」에서는 SECS를, 등록 창의 「SECS Settings」에서는 HSMS 관련 설정을 만난다. 메시지·타이머의 세부 뜻은 `00.rag/user/secs-hsms-message-and-timer-reference.md`에서 다룬다.

## SOCKET과 PLC는 어떤 통신 방식인가
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; SOCKET 프레임 처리 L201~260
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 ; 주기별 읽기 예약 L69~84
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java @ d3636ab86763 ; 데이터 영역 읽기 요청 L94~125
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Comm Interface L877~877
원천: nori-tc-exec/docs/Architecture/00-system/05-glossary.md @ f1d5e0a8e727 ; 설비 통신
```

설비 정보의 「Comm Interface」는 통신 방식이다. SOCKET은 설비가 보내는 바이트를 프레임으로 구분해 해석하는 방식이고, PLC는 설비 제어기의 데이터 영역을 정한 주기로 읽는 방식이다.

프레임 규칙은 `00.rag/user/socket-framing-and-parsing-reference.md`, PLC 데이터 항목은 `00.rag/user/plc-modbus-register-and-type-reference.md`에서 다룬다.

## Comm Mode는 어느 쪽의 접속 방향인가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/ConnectionMode.java @ d3636ab86763 ; Gateway 기준 연결 모드 L3~19
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 접속·수신 대기 분기 L310~330
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Comm Mode 표시 L131~138
```

「Comm Mode」는 Gateway를 기준으로 누가 연결을 먼저 여는지 구별하는 값이다. 설비 정보 표에서 통신 방식인 「Comm Interface」와 별도로 표시한다.

| 값 | 접속 방향 |
|---|---|
| ACTIVE | Gateway가 설비에 연결을 건다. |
| PASSIVE | Gateway가 접속을 기다리고 설비가 연결을 건다. |

설정 입력 절차는 `00.rag/user/equipment-registration-and-settings.md`에서 다룬다.

## 설비 정보의 Port는 무엇을 가리키는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Port 열 L138~138
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; EQP Port 입력 L937~946
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; 연결 대상 포트 L222~259
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 연결 모드별 경로 L310~330
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayPassiveListenerRegistry.java @ d3636ab86763 ; EQP Port로 리스너 구분 L66~76
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayPassiveListenerRegistry.java @ d3636ab86763 ; 리스너 열기 L377~388
```

설비 정보 표의 「Port」와 등록·수정 창의 「EQP Port」는 설비 연결에 사용하는 네트워크 포트 번호다.

Comm Mode가 ACTIVE이면 Gateway가 설비 주소와 이 번호로 접속한다. PASSIVE이면 Gateway가 이 번호로 리스너를 열어 설비 접속을 기다린다.

업무 문서에서 설비의 반입·반출구를 뜻하는 Port는 `00.rag/user/lot-carrier-work-and-system-flow.md`에서 다룬다.

## Revision은 모델 버전과 어떻게 구별하는가
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 기준 판 대조·충돌 L95~135
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd ; 확정 revision 기록 L40~62
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 부록 C Revision
```

Revision은 설비 같은 대상의 변경 상태를 구별하는 판 번호다. 변경 요청의 기준 상태가 유지되는지 대조할 때 사용하며, 오류 코드 `REVISION_CONFLICT`를 설명하는 문서에서 만난다.

모델 버전은 메시지·처리 정의의 판이고 Revision은 변경 대상의 상태를 대조하는 번호다. 오류별 조치는 `00.rag/user/error-message-and-code-reference.md`에서 다룬다.

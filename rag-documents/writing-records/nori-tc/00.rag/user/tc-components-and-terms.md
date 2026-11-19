# TC 구성 요소·용어 작성 기록

## 대상

- objectKey: `00.rag/user/tc-components-and-terms.md`
- 구획·권한: user, [AI_DOC_READ]
- 유형: 개념
- 검색 원문 SHA-256: `0cd73192a1a0387dfb678c1425d38ca297ad7c2212efa490c306f1821c9250b4`
- 검색 원문 줄 수: 254
- 절 수: 19
- 문장·표 자료 행 대조: 80행. 표 행은 한 행 안의 모든 문장을 함께 대조했다.
- 확인일: 2026-11-19 (한국 시간)
- 완료 범위: 지정 검색 원문과 이 작성 기록만 작성. 업로드·제품 변경·git add/commit/push를 실행하지 않았다.

## 원천 판본

작성 규칙 전체와 목록 전체를 먼저 읽었다. 규칙 SHA-256은 `273f44daeb41ffe7a63fbd2231c8b73f58464af19a6a580523e82ca5c578e13b`, 목록 SHA-256은 `45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b`다. 두 입력은 nori-ai-gateway HEAD 70cf6cd84db6에서 경로 지정 status 출력이 비어 있음을 확인했다.

다음 각 저장소에서 `git -c safe.directory=<저장소 절대경로> -C <저장소> rev-parse --short=12 HEAD`와 `status --short -- <아래 해당 파일 경로 전부>`를 실행했다. 각 경로 지정 status의 종료코드는 0이고 stdout은 비어 있었다. 따라서 해당 파일은 commit 판본을 썼다. Git은 사용자 전역 ignore 파일 접근 경고를 stderr에 냈으며 파일 내용은 열지 않았다. safe.directory는 조회 명령 한 번에만 지정했고 전역 설정을 변경하지 않았다.

| 저장소 | HEAD | 경로 지정 작업 트리 상태 |
|---|---|---|
| nori-tc-gateway | d3636ab86763 | 지정 12개 경로 변경 없음, exit 0 |
| nori-tc-business | 7db24f7466bd | 지정 5개 경로 변경 없음, exit 0 |
| nori-tc-ui-front | 9fe69a75dcd3 | 지정 17개 경로 변경 없음, exit 0 |
| nori-tc-common | 6fe71f6b2da6 | 지정 4개 경로 변경 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 지정 14개 경로 변경 없음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 지정 3개 경로 변경 없음, exit 0 |

전체 상태 조회에서 nori-tc-common의 CLAUDE.md가 수정 상태였으나 이 문서의 원천으로 쓰지 않았고 변경하지 않았다. nori-ai-gateway는 main이며 rag-documents/의 기존 미추적 파일들을 보존했다. worktree 조회는 현재 저장소 한 개를 표시했다.

확인한 원천 경로는 다음과 같다. 본문 절의 원천 외에 Pod 배포 템플릿과 관련 시험의 기대값을 포함한다.

### nori-tc-gateway @ d3636ab86763

- gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java
- gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java
- gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrapTest.java
- gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java
- gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java
- gateway/adapter/tc-gateway-redis-adapter/src/test/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManagerTest.java
- gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java
- gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java
- gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java
- gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/ConnectionMode.java
- gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java
- gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java

### nori-tc-business @ 7db24f7466bd

- business/adapter/tc-business-db-adapter/src/test/java/com/nori/tc/business/adapters/db/modelcache/BusinessMdfRuntimeParserTest.java
- business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java
- business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java
- business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java
- business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java

### nori-tc-ui-front @ 9fe69a75dcd3

- src/features/deploy/components/DeployRegisterPanel.tsx
- src/features/dlq/components/GatewayDlqPanel.tsx
- src/features/dlq/components/QuarantinePanel.tsx
- src/features/dlq/components/RepairTaskPanel.tsx
- src/features/eqp/components/EqpInfoTable.tsx
- src/features/eqp/components/EqpManageFormModal.tsx
- src/features/eqp/components/EqpPage.tsx
- src/features/eqp/components/EqpSidebar.tsx
- src/features/eqp/utils/eqp-group.util.ts
- src/features/model/api/model.api.ts
- src/features/model/components/BranchModelCreateModal.tsx
- src/features/model/components/FlowCard.tsx
- src/features/model/components/ModelDetailPanel.tsx
- src/features/model/components/ModelFlowSidebar.tsx
- src/features/model/components/ModelSidebar.tsx
- src/features/model/types/model.types.ts
- src/i18n/locales/ko.json

### nori-tc-common @ 6fe71f6b2da6

- common/tc-common-shard/src/main/java/com/nori/tc/common/shard/SharedShardGroupProperties.java
- db/tc-db-domain/src/main/java/com/nori/tc/db/domain/eqp/TcEqp.java
- db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMdf.java
- db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMessageFlow.java

### nori-tc-ui-backend @ 985d17c604dd

- ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java
- ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java
- ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowQueryPort.java
- ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java
- ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java
- ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java
- ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java
- ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java
- ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java
- ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java
- ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java
- ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java
- ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java
- ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpRepairTaskService.java

### nori-tc-exec @ f1d5e0a8e727

- deploy/helm/tc-gateway/templates/deployment.yaml
- docs/Architecture/00-system/05-glossary.md
- docs/completion-report/07-user-manual.md

원천 범위는 U 부록 C 전체(07-user-manual.md L2663~2699)와 구조 용어집 전체다. 본문은 U의 사용자 용어를 중심으로 구성했다. 구조 용어집의 상세 운영·코드 구조·외부 연동 계약은 아래 소유 문서 구분을 적용했다. 매뉴얼의 의미를 근거 없이 영어로 확장하지 않았다.

관련 시험은 실행 결과를 재사용한 것이 아니라 기대값을 읽어 코드 해석과 대조했다.

- GatewayNettyBootstrapTest L365~408: ACTIVE 시작 시 외부 테스트 서버로 연결되는 기대값.
- GatewayShardLeaseManagerTest L77~108: 그룹 획득 시 담당 파티션 반영, 모든 슬롯 점유 시 spare 유지 기대값. 화면 spare는 추가로 ACTIVE_SYNCED를 요구하므로 런타임 시험 상태와 화면 조건을 혼동하지 않았다.
- BusinessMdfRuntimeParserTest L130~145: WM 대상 메시지 정의를 읽어 WM/KAFKA로 해석하는 기대값.

## 절별 사실 대조

본문은 직접 작성했다. 다음 표는 최종 본문을 문장과 표 자료 행으로 나누어 원천 위치를 결속한 기록이며, 절 제목·답변 문장을 틀로 생성한 것이 아니다. objectKey 안내 문장은 확정 목록의 해당 행으로 대조했다.

### 1. Gateway와 Business는 어떤 일을 맡는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L18 | Gateway는 설비 연결과 메시지 수신·송신을 맡는 구성 요소다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 연결 시작 분기 L310~330 / nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java @ d3636ab86763 ; 수신·송신 처리 L180~250 | 연결 시작·프레임 수신/송신 경로와 모델 매칭 후 액션 실행, 카드별 렌더 분기를 대조했다. |
| L18 | Business는 받은 메시지를 모델의 조건에 맞추고 해당 액션을 실행하는 구성 요소다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 모델 매칭·액션 실행 L585~605 | 연결 시작·프레임 수신/송신 경로와 모델 매칭 후 액션 실행, 카드별 렌더 분기를 대조했다. |
| L20 | 「설비」 화면의 「Gateway」·「Business」 카드는 두 구성 요소를 기준으로 자료를 나누어 보여 준다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관점 카드·목록 렌더 L1125~1209 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점 표시 L33~43 | 연결 시작·프레임 수신/송신 경로와 모델 매칭 후 액션 실행, 카드별 렌더 분기를 대조했다. |
| L20 | Gateway 관점에서는 설비 목록을, Business 관점에서는 해당 설비들이 참조하는 모델 목록을 본다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관점 카드·목록 렌더 L1125~1209 | 연결 시작·프레임 수신/송신 경로와 모델 매칭 후 액션 실행, 카드별 렌더 분기를 대조했다. |

### 2. Shard group과 Route Partition은 어떻게 연결되는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L31 | Shard group은 Gateway 인스턴스가 맡는 Kafka 파티션의 묶음이다. | nori-tc-common/common/tc-common-shard/src/main/java/com/nori/tc/common/shard/SharedShardGroupProperties.java @ 6fe71f6b2da6 ; 그룹별 파티션 검사 L56~90 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 그룹 획득·담당 파티션 반영 L206~236 | 파티션 목록을 가진 그룹, 획득 시 담당 파티션 반영, 명시 파티션 발행과 화면의 포함 관계를 대조했다. |
| L31 | 「설비」 화면에서 Gateway 쪽 설비를 그룹으로 나누는 기준으로 사용한다. | nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 그룹 분류 L160~210 | 파티션 목록을 가진 그룹, 획득 시 담당 파티션 반영, 명시 파티션 발행과 화면의 포함 관계를 대조했다. |
| L33 | 「Route Partition」은 설비에 보낼 메시지의 파티션 번호다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/publish/UiGatewayEventKafkaPublisher.java @ 985d17c604dd ; 설비별 파티션 지정 L86~108 | 파티션 목록을 가진 그룹, 획득 시 담당 파티션 반영, 명시 파티션 발행과 화면의 포함 관계를 대조했다. |
| L33 | 설비의 번호가 Shard group의 담당 파티션에 포함되면 그 그룹에 분류한다. | nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 그룹 분류 L160~210 | 파티션 목록을 가진 그룹, 획득 시 담당 파티션 반영, 명시 파티션 발행과 화면의 포함 관계를 대조했다. |

### 3. Lease·Pod·spare는 무엇을 구별하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L46 | \| Lease \| Gateway가 특정 Shard group을 맡는 기한 있는 소유권이다. 설비 화면의 그룹 점유 현황을 설명할 때 사용한다. \| | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 결과와 소유 기한 L206~236 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd ; 그룹과 소유자 조회 L38~47 | lease 획득·기한과 sessionId 대조, ACTIVE_SYNCED 조건, Pod 식별값이 있을 때만 표시하는 분기를 확인했다. |
| L47 | \| Pod \| Gateway 같은 프로그램을 실행하는 배포 단위다. 담당 Pod를 확인할 수 있으면 설비 트리의 그룹 아래에 식별 문구를 표시한다. \| | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; Pod·spare 표시 L290~338 / nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Pod template·실행 컨테이너 L19~46 | lease 획득·기한과 sessionId 대조, ACTIVE_SYNCED 조건, Pod 식별값이 있을 때만 표시하는 분기를 확인했다. |
| L48 | \| spare \| 동기화를 마쳤지만 Shard group을 맡지 않은 대기 Gateway 인스턴스다. 설비 트리에서 「spare」로 표시한다. \| | nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자·대기 인스턴스 판정 L55~97 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; Pod·spare 표시 L290~338 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 소유·대기 안내 L595~598 | lease 획득·기한과 sessionId 대조, ACTIVE_SYNCED 조건, Pod 식별값이 있을 때만 표시하는 분기를 확인했다. |
| L50 | Shard group은 담당 구역이고 Pod는 실행 단위이므로 서로 다른 개념이다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 결과와 소유 기한 L206~236 / nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자·대기 인스턴스 판정 L55~97 / nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Pod template L19~46 | lease 획득·기한과 sessionId 대조, ACTIVE_SYNCED 조건, Pod 식별값이 있을 때만 표시하는 분기를 확인했다. |

### 4. 설비와 라인은 무엇인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L59 | 설비(EQP)는 TC가 식별하고 통신하는 장비 한 대다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/eqp/TcEqp.java @ 6fe71f6b2da6 ; 설비 식별·소속·통신 정보 L41~55 | 설비 ID·lineKey·통신 정보의 실제 레코드와 EQPID 렌더 값을 확인했다. |
| L59 | 「설비」 화면의 「EQPID」는 그 장비를 구별하는 설비 ID다. | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; EQPID·통신 열 L111~138 | 설비 ID·lineKey·통신 정보의 실제 레코드와 EQPID 렌더 값을 확인했다. |
| L61 | 라인은 설비가 소속되는 묶음이다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/eqp/TcEqp.java @ 6fe71f6b2da6 ; 설비 식별·소속·통신 정보 L41~55 | 설비 ID·lineKey·통신 정보의 실제 레코드와 EQPID 렌더 값을 확인했다. |
| L61 | 구조 문서의 LINE은 이 소속 단위를 가리킨다. | nori-tc-exec/docs/Architecture/00-system/05-glossary.md @ f1d5e0a8e727 ; 제조 도메인의 EQP·LINE | 설비 ID·lineKey·통신 정보의 실제 레코드와 EQPID 렌더 값을 확인했다. |
| L61 | 설비마다 소속 라인과 통신 정보가 연결된다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/eqp/TcEqp.java @ 6fe71f6b2da6 ; 설비 식별·소속·통신 정보 L41~55 | 설비 ID·lineKey·통신 정보의 실제 레코드와 EQPID 렌더 값을 확인했다. |

### 5. 모델과 모델 버전은 무엇이 다른가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L71 | 모델은 설비 메시지와 처리 규칙 등을 묶어 관리하는 정의다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMessageFlow.java @ 6fe71f6b2da6 ; 버전별 메시지·조건·액션 L30~39 | 설비의 modelVersionKey와 버전별 메시지·규칙 정의, 모델 표시 열을 확인했다. |
| L71 | 모델 버전은 그 정의를 특정 판으로 구별한 것이다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMessageFlow.java @ 6fe71f6b2da6 ; 버전별 메시지·조건·액션 L30~39 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 버전 상세 복제 L348~362 | 설비의 modelVersionKey와 버전별 메시지·규칙 정의, 모델 표시 열을 확인했다. |
| L71 | 설비는 사용할 모델 버전을 참조한다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/eqp/TcEqp.java @ 6fe71f6b2da6 ; 모델 버전 참조 L41~54 | 설비의 modelVersionKey와 버전별 메시지·규칙 정의, 모델 표시 열을 확인했다. |
| L73 | 설비 정보의 「Model」은 모델을, 「Model Version」은 연결한 판을 보여 준다. | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Model·Model Version 표시 L147~151 | 설비의 modelVersionKey와 버전별 메시지·규칙 정의, 모델 표시 열을 확인했다. |
| L73 | 같은 모델이라도 연결한 버전이 다르면 사용할 정의가 달라질 수 있다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMessageFlow.java @ 6fe71f6b2da6 ; 버전별 메시지·조건·액션 L30~39 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 버전 상세 복제 L348~362 | 설비의 modelVersionKey와 버전별 메시지·규칙 정의, 모델 표시 열을 확인했다. |

### 6. root·branch·suffix는 모델에서 무엇을 뜻하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L85 | \| root \| 모델 트리에서 branch의 기준이 되는 부모 모델이다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; branch 복제 L229~263 / nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 모델 트리 L156~250 | root 기준 복제, 별도 branch 생성, suffix 명명 식과 트리·대화상자 문구를 확인했다. |
| L86 | \| branch \| root의 선택 버전 내용을 복제해 따로 편집하는 모델이다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; branch 복제 L229~263 | root 기준 복제, 별도 branch 생성, suffix 명명 식과 트리·대화상자 문구를 확인했다. |
| L87 | \| suffix \| 「Branch Model Create」에서 branch를 구별하도록 입력하는 이름 조각이다. 부모 모델명·suffix·사용자 아이디를 밑줄로 이어 branch 이름을 만든다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; branch 명명 L184~187 / nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; suffix 입력 L133~190 | root 기준 복제, 별도 branch 생성, suffix 명명 식과 트리·대화상자 문구를 확인했다. |
| L89 | 모델 생성·복제 절차는 `00.rag/user/model-registration-and-branching.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/model-registration-and-branching.md L30~30 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 7. Check Out과 Check In은 어떤 편집 경계인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L99 | 「Check Out」은 기준 버전에서 편집용 작업본을 여는 것이고, 「Check In」은 서버에 저장된 작업본을 새 버전으로 확정하는 것이다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 모델 작업본과 확정 L278~362 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 파라미터 편집·확정 L84~141 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 저장된 EDIT의 버전 생성 L224~261 | 모델·파라미터의 EDIT 복사와 서버에 저장된 EDIT의 확정을 대조했다. 세부 빈 작업본 조건은 절차 문서가 소유한다. |
| L99 | 모델 상세와 설비 파라미터에서 사용하는 말이다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 모델 작업본과 확정 L278~362 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 파라미터 편집·확정 L84~141 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 편집 단추 L790~836 | 모델·파라미터의 EDIT 복사와 서버에 저장된 EDIT의 확정을 대조했다. 세부 빈 작업본 조건은 절차 문서가 소유한다. |
| L101 | 모델 편집 절차는 `00.rag/user/model-details-and-flow-editing.md`, 설비 파라미터 편집 절차는 `00.rag/user/equipment-model-parameter-and-jar-update.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/model-details-and-flow-editing.md L31~31 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/equipment-model-parameter-and-jar-update.md L29~29 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 8. parent commit은 어느 모델에 반영하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L110 | parent commit은 branch의 최신 내용을 부모 root의 새 버전으로 옮기는 작업이다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 부모 새 버전 복제 L421~462 | branch 최신 snapshot을 부모의 새 버전에 복제하는 경로와 메뉴 문구를 확인했다. |
| L110 | 모델 트리의 branch 메뉴에서는 「Parent Model Commit」으로 만난다. | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; Parent Model Commit 메뉴 L183~190 | branch 최신 snapshot을 부모의 새 버전에 복제하는 경로와 메뉴 문구를 확인했다. |
| L112 | branch에서 Check In으로 branch 버전을 확정하는 것과 반영 대상이 다르다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 부모 새 버전 복제 L421~462 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 모델 작업본과 확정 L278~362 | branch 최신 snapshot을 부모의 새 버전에 복제하는 경로와 메뉴 문구를 확인했다. |
| L112 | parent commit 절차는 `00.rag/user/model-registration-and-branching.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/model-registration-and-branching.md L30~30 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 9. Workflow와 Flow는 메시지 처리에서 무엇인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L122 | 모델 상세의 「Message Flows」에서 Workflow는 하위 메시지별 Flow를 묶는 이름이다. | nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; 그룹과 메시지 표시 L50~109 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowQueryPort.java @ 985d17c604dd ; 목록·상세·수집 연결 L63~117 | workflowName 그룹과 messageName 하위 줄, 단일 Flow의 조건·액션·수집 연결 및 런타임 매칭을 확인했다. |
| L122 | Flow는 그 이름과 메시지의 조합에 대해 처리 조건·실행 액션·수집 항목을 정의한 한 건이다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Flow 식별·조건·액션·수집 L274~464 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowQueryPort.java @ 985d17c604dd ; 목록·상세·수집 연결 L63~117 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; 후보 조건 평가 L64~117 | workflowName 그룹과 messageName 하위 줄, 단일 Flow의 조건·액션·수집 연결 및 런타임 매칭을 확인했다. |
| L124 | 목록의 상위 줄은 묶음 이름, 하위 줄은 메시지 이름이다. | nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; 그룹과 메시지 표시 L50~109 | workflowName 그룹과 messageName 하위 줄, 단일 Flow의 조건·액션·수집 연결 및 런타임 매칭을 확인했다. |
| L124 | 하위 메시지에 대응하는 Flow 카드에서 조건과 할 일을 본다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Flow 식별·조건·액션·수집 L274~464 | workflowName 그룹과 messageName 하위 줄, 단일 Flow의 조건·액션·수집 연결 및 런타임 매칭을 확인했다. |
| L126 | 편집 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/model-details-and-flow-editing.md L31~31 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 10. MDF는 어떤 파일인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L136 | MDF는 모델 버전에 연결하는 XML 메시지 서식이다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMdf.java @ 6fe71f6b2da6 ; MDF XML 원문 L5~20 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; saveMdf L186~205 | XML 저장·버전 연결, 실제 MDF 표시와 EQP/MES/WM 대상별 발행 분기를 확인했다. |
| L136 | 모델 상세의 「Reference」 아래 「MDF」에서 만난다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; MDF 표시 L942~978 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 노드 렌더 L872~890 / nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Dcop Itemes 표기 L310~321 | XML 저장·버전 연결, 실제 MDF 표시와 EQP/MES/WM 대상별 발행 분기를 확인했다. |
| L138 | Business는 선택한 MDF 메시지 정의로 발행할 메시지를 구성하며, 정의의 대상에 따라 설비·MES·WM으로 보낸다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; 메시지 선택·대상별 발행 L85~108 | XML 저장·버전 연결, 실제 MDF 표시와 EQP/MES/WM 대상별 발행 분기를 확인했다. |
| L138 | 업로드 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/model-details-and-flow-editing.md L31~31 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 11. DCOP 수집 항목은 Flow와 어떤 관계인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L148 | DCOP 항목은 모델에서 정의하고 Flow에 연결하는 데이터 수집 항목이다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Collect 영역 L423~464 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowQueryPort.java @ 985d17c604dd ; 수집 항목 연결 조회 L91~117 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd ; Flow별 수집 대상 L86~108 | 항목 정의와 Flow 연결을 따로 조회하고 연결된 항목만 수집 대상으로 고르는 경로를 확인했다. |
| L148 | 모델 상세의 「Dcop Itemes」는 항목 정의를, Flow 카드의 「Collect」는 해당 Flow와의 연결을 다룬다. | nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Dcop Itemes 표기 L310~321 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Collect 영역 L423~464 | 항목 정의와 Flow 연결을 따로 조회하고 연결된 항목만 수집 대상으로 고르는 경로를 확인했다. |
| L150 | Business는 Flow에 연결된 항목을 수집 대상으로 고른다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd ; Flow별 수집 대상 L86~108 | 항목 정의와 Flow 연결을 따로 조회하고 연결된 항목만 수집 대상으로 고르는 경로를 확인했다. |
| L150 | 정의와 연결을 편집하는 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/model-details-and-flow-editing.md L31~31 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 12. 배포는 어떤 변경을 묶는 작업인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L159 | 배포는 대상 설비에 적용할 모델 버전·Business JAR·설비 파라미터 버전 변경을 묶어 실행하는 작업이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 대상별 변경값 조회 L271~325 / nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델·JAR·파라미터 입력 L354~373 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 성공값 반영 L25~76 | 선택한 변경값과 배포 성공 후 모델·파라미터·Business JAR의 영속 확정을 확인했다. |
| L159 | 「배포」 화면에서 대상과 변경할 값을 함께 다룬다. | nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델·JAR·파라미터 입력 L354~373 | 선택한 변경값과 배포 성공 후 모델·파라미터·Business JAR의 영속 확정을 확인했다. |
| L161 | 등록·실행·이력 확인 절차는 `00.rag/user/deployment-workflow.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/deployment-workflow.md L32~32 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 13. DLQ와 payload는 실패 조회에서 무엇을 뜻하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L171 | DLQ는 처리에 실패한 메시지를 사유와 함께 따로 보관하는 곳이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 실패 메시지 기록 L398~435 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; DLQ 조회 L95~128 | 실패 메시지·사유 기록과 payload 참조 열을 확인했다. 원문 조회 조작·TTL·삭제는 이 문서에 넣지 않았다. |
| L171 | 「처리실패」 화면의 「Gateway DLQ」·「Business DLQ」로 처리 주체를 구별한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 실패·payload 참조 열 L14~36 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; DLQ 카드·참조 문구 L1062~1084 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; DLQ 카드 표기 L125~128 | 실패 메시지·사유 기록과 payload 참조 열을 확인했다. 원문 조회 조작·TTL·삭제는 이 문서에 넣지 않았다. |
| L173 | payload는 처리 대상 메시지의 내용이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 실패 메시지 기록 L398~435 | 실패 메시지·사유 기록과 payload 참조 열을 확인했다. 원문 조회 조작·TTL·삭제는 이 문서에 넣지 않았다. |
| L173 | DLQ 표의 「payload 참조」는 그 내용을 가리키는 참조값이다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 실패·payload 참조 열 L14~36 | 실패 메시지·사유 기록과 payload 참조 열을 확인했다. 원문 조회 조작·TTL·삭제는 이 문서에 넣지 않았다. |
| L173 | 실패 항목 조회 절차는 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/dlq-quarantine-and-repair-task.md L33~33 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 14. 격리 설비와 Repair Task는 무엇이 다른가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L185 | 「격리 설비」는 Gateway가 설비별로 남긴 격리 사유를 보여 주는 목록이다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; 설비별 격리 사유 기록 L62~85 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 격리 설비 표시 L14~30 | 설비별 격리 사유 저장과 되돌림 실패 시 시스템이 남기는 복구 작업 기록, 별도 카드 렌더를 대조했다. |
| L185 | 「Repair Task」는 되돌림 같은 복구 처리에 실패했을 때 시스템이 남기는 작업 기록이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpRepairTaskService.java @ 985d17c604dd ; 복구 작업 기록 L29~48 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 되돌림 실패 기록 L132~169 | 설비별 격리 사유 저장과 되돌림 실패 시 시스템이 남기는 복구 작업 기록, 별도 카드 렌더를 대조했다. |
| L187 | 두 목록은 「처리실패」 화면의 별도 카드다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 격리 설비 표시 L14~30 / nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 복구 항목 표시 L14~28 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 카드 표기 L125~128 | 설비별 격리 사유 저장과 되돌림 실패 시 시스템이 남기는 복구 작업 기록, 별도 카드 렌더를 대조했다. |
| L187 | 격리는 설비와 사유를, Repair Task는 복구할 작업과 액션을 중심으로 구별한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; 설비별 격리 사유 기록 L62~85 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpRepairTaskService.java @ 985d17c604dd ; 복구 작업 기록 L29~48 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 격리 설비 표시 L14~30 / nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 복구 항목 표시 L14~28 | 설비별 격리 사유 저장과 되돌림 실패 시 시스템이 남기는 복구 작업 기록, 별도 카드 렌더를 대조했다. |
| L187 | 확인 절차는 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/dlq-quarantine-and-repair-task.md L33~33 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 15. SECS와 HSMS는 어느 부분을 가리키는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L196 | TC에서 SECS는 SECS-II 메시지를 주고받는 설비 통신 방식이다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; HSMS·SECS 처리 조립 L148~197 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; SECS 인터페이스 연결 L222~240 | SECS 인터페이스에 HSMS 프레임 추출·SECS-II 해석 파이프라인을 연결하는 코드와 화면 구역을 확인했다. |
| L196 | HSMS는 이 메시지를 실어 나르는 통신 계층이다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; HSMS·SECS 처리 조립 L148~197 | SECS 인터페이스에 HSMS 프레임 추출·SECS-II 해석 파이프라인을 연결하는 코드와 화면 구역을 확인했다. |
| L196 | Gateway는 HSMS 프레임을 추출한 뒤 SECS-II 메시지를 해석한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; HSMS·SECS 처리 조립 L148~197 | SECS 인터페이스에 HSMS 프레임 추출·SECS-II 해석 파이프라인을 연결하는 코드와 화면 구역을 확인했다. |
| L198 | 설비의 「Comm Interface」에서는 SECS를, 등록 창의 「SECS Settings」에서는 HSMS 관련 설정을 만난다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 설정 L1112~1218 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; EQPID·통신 열 L111~138 | SECS 인터페이스에 HSMS 프레임 추출·SECS-II 해석 파이프라인을 연결하는 코드와 화면 구역을 확인했다. |
| L198 | 메시지·타이머의 세부 뜻은 `00.rag/user/secs-hsms-message-and-timer-reference.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/secs-hsms-message-and-timer-reference.md L63~63 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 16. SOCKET과 PLC는 어떤 통신 방식인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L209 | 설비 정보의 「Comm Interface」는 통신 방식이다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Comm Interface L877~877 | SOCKET 파이프라인과 PLC 주기 예약·그룹 데이터 영역 읽기 요청 송신을 확인했다. |
| L209 | SOCKET은 설비가 보내는 바이트를 프레임으로 구분해 해석하는 방식이고, PLC는 설비 제어기의 데이터 영역을 정한 주기로 읽는 방식이다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; SOCKET 프레임 처리 L201~260 / nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 ; 주기별 읽기 예약 L69~84 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java @ d3636ab86763 ; 데이터 영역 읽기 요청 L94~125 | SOCKET 파이프라인과 PLC 주기 예약·그룹 데이터 영역 읽기 요청 송신을 확인했다. |
| L211 | 프레임 규칙은 `00.rag/user/socket-framing-and-parsing-reference.md`, PLC 데이터 항목은 `00.rag/user/plc-modbus-register-and-type-reference.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/socket-framing-and-parsing-reference.md L64~64 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/plc-modbus-register-and-type-reference.md L65~65 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 17. Comm Mode는 어느 쪽의 접속 방향인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L220 | 「Comm Mode」는 Gateway를 기준으로 누가 연결을 먼저 여는지 구별하는 값이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/ConnectionMode.java @ d3636ab86763 ; Gateway 기준 연결 모드 L3~19 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 접속·수신 대기 분기 L310~330 | Gateway 기준 enum 설명을 실제 outbound/listener 분기와 대조했다. 구조 용어집의 반대 설명은 채택하지 않았다. |
| L220 | 설비 정보 표에서 통신 방식인 「Comm Interface」와 별도로 표시한다. | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Comm Mode 표시 L131~138 | Gateway 기준 enum 설명을 실제 outbound/listener 분기와 대조했다. 구조 용어집의 반대 설명은 채택하지 않았다. |
| L224 | \| ACTIVE \| Gateway가 설비에 연결을 건다. \| | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/ConnectionMode.java @ d3636ab86763 ; Gateway 기준 연결 모드 L3~19 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 접속·수신 대기 분기 L310~330 | Gateway 기준 enum 설명을 실제 outbound/listener 분기와 대조했다. 구조 용어집의 반대 설명은 채택하지 않았다. |
| L225 | \| PASSIVE \| Gateway가 접속을 기다리고 설비가 연결을 건다. \| | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/ConnectionMode.java @ d3636ab86763 ; Gateway 기준 연결 모드 L3~19 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 접속·수신 대기 분기 L310~330 | Gateway 기준 enum 설명을 실제 outbound/listener 분기와 대조했다. 구조 용어집의 반대 설명은 채택하지 않았다. |
| L227 | 설정 입력 절차는 `00.rag/user/equipment-registration-and-settings.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/equipment-registration-and-settings.md L27~27 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 18. 설비 정보의 Port는 무엇을 가리키는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L239 | 설비 정보 표의 「Port」와 등록·수정 창의 「EQP Port」는 설비 연결에 사용하는 네트워크 포트 번호다. | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Port 열 L138~138 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; EQP Port 입력 L937~946 | 두 화면이 같은 eqpPort를 표시·입력하는 것을 확인했다. 모드별 역할은 다음 두 행으로 구별했다. |
| L241 | Comm Mode가 ACTIVE이면 Gateway가 설비 주소와 이 번호로 접속한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 연결 모드별 경로 L310~330 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; 연결 대상 포트 L222~259 | ACTIVE가 connectActiveIfPossible 경로를 타고 실제 connect 호출에 eqpIp와 eqpPort를 전달한다. |
| L241 | PASSIVE이면 Gateway가 이 번호로 리스너를 열어 설비 접속을 기다린다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayNettyBootstrap.java @ d3636ab86763 ; 연결 모드별 경로 L310~330 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayPassiveListenerRegistry.java @ d3636ab86763 ; EQP Port로 리스너 구분 L66~76 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayPassiveListenerRegistry.java @ d3636ab86763 ; 리스너 열기 L377~388 | PASSIVE가 ensurePassiveListenerForEquipment를 호출하고 eqpPort로 만든 key.port()에 bind한다. 기존 Comm Mode 표의 접속 대기 설명과 일치한다. |
| L243 | 업무 문서에서 설비의 반입·반출구를 뜻하는 Port는 `00.rag/user/lot-carrier-work-and-system-flow.md`에서 다룬다. | nori-tc-exec/docs/Architecture/00-system/05-glossary.md @ f1d5e0a8e727 ; LOT / CARRIER / PORT L62~70 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/lot-carrier-work-and-system-flow.md L40~40 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

### 19. Revision은 모델 버전과 어떻게 구별하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L252 | Revision은 설비 같은 대상의 변경 상태를 구별하는 판 번호다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 기준 판 대조·충돌 L95~135 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd ; 확정 revision 기록 L40~62 | 기준 revision 대조와 확정 revision 저장 경로를 확인하고 모델 정의 버전과 구별했다. |
| L252 | 변경 요청의 기준 상태가 유지되는지 대조할 때 사용하며, 오류 코드 `REVISION_CONFLICT`를 설명하는 문서에서 만난다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 기준 판 대조·충돌 L95~135 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 부록 C Revision | 기준 revision 대조와 확정 revision 저장 경로를 확인하고 모델 정의 버전과 구별했다. |
| L254 | 모델 버전은 메시지·처리 정의의 판이고 Revision은 변경 대상의 상태를 대조하는 번호다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 기준 판 대조·충돌 L95~135 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd ; 확정 revision 기록 L40~62 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/model/TcModelMessageFlow.java @ 6fe71f6b2da6 ; 버전별 메시지·조건·액션 L30~39 | 기준 revision 대조와 확정 revision 저장 경로를 확인하고 모델 정의 버전과 구별했다. |
| L254 | 오류별 조치는 `00.rag/user/error-message-and-code-reference.md`에서 다룬다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 00.rag/user/error-message-and-code-reference.md L66~66 | 확정 목록의 문서명·소유 질문과 objectKey를 대조했다. 정의 이외의 조작은 소유 문서에 맡겼다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 원천의 설명 | 실제 코드·기대값 | 검색 원문 반영 |
|---|---|---|---|
| Comm Mode | 구조 용어집 §2 comm_mode는 ACTIVE를 Gateway bind, PASSIVE를 dial로 설명한다. U 부록 C L2685는 현재 코드의 방향과 일치한다. | ConnectionMode L3~15, GatewayNettyBootstrap L310~330은 ACTIVE outbound, PASSIVE listener다. GatewayNettyBootstrapTest L386~408도 ACTIVE outbound를 기대한다. | L220~225에서 Gateway 기준 접속 방향을 썼다. 구조 용어집의 반대 설명을 넣지 않았다. |
| MDF 대상 | U 부록 C L2694는 MES 보고 서식으로만 설명한다. | BusinessWorkflowMdfAutoPublisher L99~108은 EQP/MES/WM으로 분기한다. BusinessMdfRuntimeParserTest L130~145는 WM 대상도 기대한다. | L136~138에 XML 메시지 서식과 대상별 발행을 설명했다. |
| Repair Task 작성 주체 | U 부록 C L2683은 사람이 되돌려 달라고 남긴 기록으로 설명한다. | EqpDirectScenarioRollbackService L132~169가 되돌림 실패 시 기록을 호출하고 EqpRepairTaskService L29~48이 작업을 생성·저장한다. | L185에서 시스템이 남기는 복구 실패 작업 기록으로 정의했다. |

Workflow는 U 부록 C L2679의 짧은 설명만으로 메시지 한 건과 동일시하지 않았다. ModelFlowSidebar L50~109의 상위 workflowName / 하위 messageName 렌더와 Flow 상세 조회를 함께 확인해 묶음과 개별 Flow를 구별했다. 이 항목은 표기 보충이며 별도의 코드 수정 결함 판정을 만들지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| MDF 약어의 영문 전체 풀이 | 구조 용어집 §8도 코드에 풀이가 없다고 명시하며, 확인한 XML 저장·메시지 발행 코드도 영문 전체 명칭을 정의하지 않는다. XML과 실제 용도만 썼다. | 모델 계약 담당 |
| DCOP 약어의 영문 전체 풀이 | U는 수집 항목으로 설명하며, 구조 문서의 DCITEM 풀이를 DCOP에 그대로 적용할 근거가 없다. 확인한 코드의 항목 정의·Flow 연결·수집 역할만 썼다. | 데이터 수집·모델 계약 담당 |

현장 접속·배포 판본·현재 그룹 점유·실제 그룹 개수·실제 lease 시간은 조회하지 않았다. 저장소의 예시나 기본값을 현장 사실로 바꾸지 않았다. 제조사별 통신 계약을 추정하지 않았다. 이 부재는 본문 정의를 작성할 수 없는 제품 결정이 아니므로 사용자 결정 요청은 없다.

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 이번 본문에서 넣지 않은 내용 |
|---|---|
| 00.rag/user/equipment-registration-and-settings.md | 입력 필수값·권한·저장·수정·삭제 순서, Port와 통신 설정 제한값. |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 설비 파라미터 Save·Check In 세부 동작, 빈 작업본 조건, 적용과 JAR 변경 절차. |
| 00.rag/user/model-registration-and-branching.md | root·branch 생성·삭제·parent commit 조작, 입력 길이·권한·상태 전이. 용어 정의와 suffix 명명의 최소 의미만 남겼다. |
| 00.rag/user/model-details-and-flow-editing.md | 모델 편집 잠금·저장·체크인, MDF 업로드와 Flow·수집 항목 연결 조작. |
| 00.rag/user/deployment-workflow.md | 배포 대상 선택·시나리오 검토·등록·실행·이력·재실행 절차. |
| 00.rag/user/dlq-quarantine-and-repair-task.md | DLQ 표 세부값·정렬·TTL·삭제·실패 인계와 복구 확인. 격리의 재처리 차단·자동 해제 보장은 이 용어 정의에 추가하지 않았다. |
| 00.rag/user/lot-carrier-work-and-system-flow.md | MES·WM·Lot·Carrier·작업·반입/반출 Port의 업무 관계. |
| 00.rag/user/state-reference.md | 생산·제어·목표 상태, 배포·조정 상태 이름의 값별 뜻. |
| 00.rag/user/error-message-and-code-reference.md | REVISION_CONFLICT를 포함한 오류 코드별 조치. |
| 00.rag/user/permission-code-reference.md | RBAC·그룹 권한 코드별 의미. 계정·그룹 조작은 user-group-and-permission-management.md가 소유한다. |
| 00.rag/user/secs-hsms-message-and-timer-reference.md | SECS 메시지·HSMS 타이머 값과 상세 계약. |
| 00.rag/user/socket-framing-and-parsing-reference.md | socketType·구분자·정규식·프레임 파싱 조건. |
| 00.rag/user/plc-modbus-register-and-type-reference.md | PLC protocol·area·data type·poll group의 세부 값과 읽기 제약. |
| 00.rag/user/model-plugin-limitations.md | action 플러그인·JAR 적용 경계. 이 문서는 개발 SDK·애너테이션·빌드 좌표를 설명하지 않는다. |
| 00.rag/admin/kafka-topic-and-message-reference.md | Envelope·metadata·eventType·schemaVersion·traceId/correlationId와 토픽별 필드·라우팅 계약. |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | lease 갱신·fence·cluster epoch·인스턴스 식별자·조정 operation·revision·증빙·확정 절차의 내부 세부. 용어를 이해할 최소 뜻만 user에 남겼다. |
| 00.rag/admin/http-api-reference.md | UI task·dual response·비동기 조회 API·멱등 키·CSRF·서비스 사이 인증의 상세 계약. |
| 00.rag/admin/configuration-reference.md | 설정 키·TTL·group 매핑·스타터 조립·운영 입력값. |
| 00.rag/admin/database-table-reference.md | 저장 테이블·컬럼·제약·복원 단위. |

위 admin objectKey는 검색 대상이 아닌 이 작성 기록에만 남겼다. 검색 원문에는 admin objectKey·관리 문서 열람 지시가 없다. 구조 용어집의 헥사고날·port/adapter·ModuleConfiguration·starter·BOM·SDD는 화면 용어의 답에 필요하지 않은 개발 구조 설명이므로 본문으로 확장하지 않았다.

기존 RAG 문서의 중첩 주제 절과 새로 보인 getting-started.md를 읽어 정의와 절차를 구별했다. 이미 검증된 다른 RAG 문서에서 수정이 필요한 결함을 확정하지 않았으며 다른 파일을 고치지 않았다.

## 검사 결과

- 도구: GNU Bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS, Python 3.12.10, Git 2.52.0.windows.1.
- 지정 검사기 첫 실행: exit 0, violations=0. 첫 실행 뒤 출처 줄 범위 명시, Pod 표시 조건 보강, PLC 읽기 요청 근거 추가, 절차 소유 문서와 중복되는 빈 작업본 예외 문장 제거를 했다.
- 보완 전 검색 원문의 마지막 편집 뒤 지정 검사기를 다시 실행했다. 당시 exit 0, 실행 도구가 보고한 wall time 9.926638초였다. 당시 MASK·CROSS VIOLATION·VIOLATION 출력은 없었으나 이후 메인 세션이 Port의 PASSIVE 조건 누락을 발견했다. 최신 보완 결과는 아래 별도 절에 기록한다.
- 보완 전 검사 측정: sections=19, children=45, maxChild=147, maxParent=322. 대상 위반 0건. corpus 출력은 실행 시점의 자료 집계이며 이 세션이 그 문서 전체를 공식 검증했다는 뜻이 아니다.
- 검사기·작성 규칙을 변경하거나 우회하지 않았다.

실행 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/tc-components-and-terms.md
```

보완 전 검사 출력(전체 원문, 당시 SHA 기준):

```text
=== 00.rag/user/tc-components-and-terms.md  bytes=20773 lines=249 sha256=f3d57ded6f21eb309cb79011d026f05394faeffeef10dac4ea32d17bd726eeca
H1 L1 [11] TC 구성 요소·용어   meta={유형=[개념], 적용=[nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-common 6fe71f6b2da6 소스 기준], 확인일=[2026-11-18], 담당=[TC 문서 담당]}
§ L9-20 [28] Gateway와 Business는 어떤 일을 맡는가   parent=211 children=2 sources=5
    L18-18 문단 87  | Gateway는 설비 연결과 메시지 수신·송신을 맡는 구성 요소다. Business는 받은 메시지를 모델의 조건에 맞추고 해당 액션을 실행하는 구성 요소다.
    L20-20 문단 123  | 「설비」 화면의 「Gateway」·「Business」 카드는 두 구성 요소를 기준으로 자료를 나누어 보여 준다. Gateway 관점에서는 설비 목록을, Business 관점에서는 해당 설비들이 참조하는 모델 목록을 본다.
§ L22-33 [39] Shard group과 Route Partition은 어떻게 연결되는가   parent=176 children=2 sources=5
    L31-31 문단 90  | Shard group은 Gateway 인스턴스가 맡는 Kafka 파티션의 묶음이다. 「설비」 화면에서 Gateway 쪽 설비를 그룹으로 나누는 기준으로 사용한다.
    L33-33 문단 85  | 「Route Partition」은 설비에 보낼 메시지의 파티션 번호다. 설비의 번호가 Shard group의 담당 파티션에 포함되면 그 그룹에 분류한다.
§ L35-50 [26] Lease·Pod·spare는 무엇을 구별하는가   parent=322 children=4 sources=5
    L46-46 표 행 90  | 용어=Lease | 뜻과 만나는 위치=Gateway가 특정 Shard group을 맡는 기한 있는 소유권이다. 설비 화면의 그룹 점유 현황을 설명할 때 사용한다.
    L47-47 표 행 93  | 용어=Pod | 뜻과 만나는 위치=Gateway 같은 프로그램을 실행하는 배포 단위다. 담당 Pod를 확인할 수 있으면 설비 트리의 그룹 아래에 식별 문구를 표시한다.
    L48-48 표 행 90  | 용어=spare | 뜻과 만나는 위치=동기화를 마쳤지만 Shard group을 맡지 않은 대기 Gateway 인스턴스다. 설비 트리에서 「spare」로 표시한다.
    L50-50 문단 46  | Shard group은 담당 구역이고 Pod는 실행 단위이므로 서로 다른 개념이다.
§ L52-61 [12] 설비와 라인은 무엇인가   parent=140 children=2 sources=3
    L59-59 문단 68  | 설비(EQP)는 TC가 식별하고 통신하는 장비 한 대다. 「설비」 화면의 「EQPID」는 그 장비를 구별하는 설비 ID다.
    L61-61 문단 71  | 라인은 설비가 소속되는 묶음이다. 구조 문서의 LINE은 이 소속 단위를 가리킨다. 설비마다 소속 라인과 통신 정보가 연결된다.
§ L63-73 [18] 모델과 모델 버전은 무엇이 다른가   parent=174 children=2 sources=4
    L71-71 문단 83  | 모델은 설비 메시지와 처리 규칙 등을 묶어 관리하는 정의다. 모델 버전은 그 정의를 특정 판으로 구별한 것이다. 설비는 사용할 모델 버전을 참조한다.
    L73-73 문단 90  | 설비 정보의 「Model」은 모델을, 「Model Version」은 연결한 판을 보여 준다. 같은 모델이라도 연결한 버전이 다르면 사용할 정의가 달라질 수 있다.
§ L75-89 [33] root·branch·suffix는 모델에서 무엇을 뜻하는가   parent=271 children=4 sources=4
    L85-85 표 행 43  | 용어=root | 뜻=모델 트리에서 branch의 기준이 되는 부모 모델이다.
    L86-86 표 행 47  | 용어=branch | 뜻=root의 선택 버전 내용을 복제해 따로 편집하는 모델이다.
    L87-87 표 행 111  | 용어=suffix | 뜻=「Branch Model Create」에서 branch를 구별하도록 입력하는 이름 조각이다. 부모 모델명·suffix·사용자 아이디를 밑줄로 이어 branch 이름을 만든다.
    L89-89 문단 67  | 모델 생성·복제 절차는 00.rag/user/model-registration-and-branching.md에서 다룬다.
§ L91-101 [31] Check Out과 Check In은 어떤 편집 경계인가   parent=241 children=2 sources=4
    L99-99 문단 106  | 「Check Out」은 기준 버전에서 편집용 작업본을 여는 것이고, 「Check In」은 서버에 저장된 작업본을 새 버전으로 확정하는 것이다. 모델 상세와 설비 파라미터에서 사용하는 말이다.
    L101-101 문단 134  | 모델 편집 절차는 00.rag/user/model-details-and-flow-editing.md, 설비 파라미터 편집 절차는 00.rag/user/equipment-model-parameter-and-jar-update.md에서 다룬다.
§ L103-112 [27] parent commit은 어느 모델에 반영하는가   parent=228 children=2 sources=3
    L110-110 문단 104  | parent commit은 branch의 최신 내용을 부모 root의 새 버전으로 옮기는 작업이다. 모델 트리의 branch 메뉴에서는 「Parent Model Commit」으로 만난다.
    L112-112 문단 123  | branch에서 Check In으로 branch 버전을 확정하는 것과 반영 대상이 다르다. parent commit 절차는 00.rag/user/model-registration-and-branching.md에서 다룬다.
§ L114-126 [29] Workflow와 Flow는 메시지 처리에서 무엇인가   parent=240 children=3 sources=4
    L122-122 문단 111  | 모델 상세의 「Message Flows」에서 Workflow는 하위 메시지별 Flow를 묶는 이름이다. Flow는 그 이름과 메시지의 조합에 대해 처리 조건·실행 액션·수집 항목을 정의한 한 건이다.
    L124-124 문단 68  | 목록의 상위 줄은 묶음 이름, 하위 줄은 메시지 이름이다. 하위 메시지에 대응하는 Flow 카드에서 조건과 할 일을 본다.
    L126-126 문단 59  | 편집 절차는 00.rag/user/model-details-and-flow-editing.md에서 다룬다.
§ L128-138 [12] MDF는 어떤 파일인가   parent=195 children=2 sources=4
    L136-136 문단 65  | MDF는 모델 버전에 연결하는 XML 메시지 서식이다. 모델 상세의 「Reference」 아래 「MDF」에서 만난다.
    L138-138 문단 129  | Business는 선택한 MDF 메시지 정의로 발행할 메시지를 구성하며, 정의의 대상에 따라 설비·MES·WM으로 보낸다. 업로드 절차는 00.rag/user/model-details-and-flow-editing.md에서 다룬다.
§ L140-150 [25] DCOP 수집 항목은 Flow와 어떤 관계인가   parent=218 children=2 sources=4
    L148-148 문단 111  | DCOP 항목은 모델에서 정의하고 Flow에 연결하는 데이터 수집 항목이다. 모델 상세의 「Dcop Itemes」는 항목 정의를, Flow 카드의 「Collect」는 해당 Flow와의 연결을 다룬다.
    L150-150 문단 106  | Business는 Flow에 연결된 항목을 수집 대상으로 고른다. 정의와 연결을 편집하는 절차는 00.rag/user/model-details-and-flow-editing.md에서 다룬다.
§ L152-161 [18] 배포는 어떤 변경을 묶는 작업인가   parent=149 children=2 sources=3
    L159-159 문단 91  | 배포는 대상 설비에 적용할 모델 버전·Business JAR·설비 파라미터 버전 변경을 묶어 실행하는 작업이다. 「배포」 화면에서 대상과 변경할 값을 함께 다룬다.
    L161-161 문단 57  | 등록·실행·이력 확인 절차는 00.rag/user/deployment-workflow.md에서 다룬다.
§ L163-173 [30] DLQ와 payload는 실패 조회에서 무엇을 뜻하는가   parent=222 children=2 sources=4
    L171-171 문단 91  | DLQ는 처리에 실패한 메시지를 사유와 함께 따로 보관하는 곳이다. 「처리실패」 화면의 「Gateway DLQ」·「Business DLQ」로 처리 주체를 구별한다.
    L173-173 문단 130  | payload는 처리 대상 메시지의 내용이다. DLQ 표의 「payload 참조」는 그 내용을 가리키는 참조값이다. 실패 항목 조회 절차는 00.rag/user/dlq-quarantine-and-repair-task.md에서 다룬다.
§ L175-187 [27] 격리 설비와 Repair Task는 무엇이 다른가   parent=232 children=2 sources=6
    L185-185 문단 98  | 「격리 설비」는 Gateway가 설비별로 남긴 격리 사유를 보여 주는 목록이다. 「Repair Task」는 되돌림 같은 복구 처리에 실패했을 때 시스템이 남기는 작업 기록이다.
    L187-187 문단 133  | 두 목록은 「처리실패」 화면의 별도 카드다. 격리는 설비와 사유를, Repair Task는 복구할 작업과 액션을 중심으로 구별한다. 확인 절차는 00.rag/user/dlq-quarantine-and-repair-task.md에서 다룬다.
§ L189-198 [24] SECS와 HSMS는 어느 부분을 가리키는가   parent=261 children=2 sources=3
    L196-196 문단 113  | TC에서 SECS는 SECS-II 메시지를 주고받는 설비 통신 방식이다. HSMS는 이 메시지를 실어 나르는 통신 계층이다. Gateway는 HSMS 프레임을 추출한 뒤 SECS-II 메시지를 해석한다.
    L198-198 문단 147  | 설비의 「Comm Interface」에서는 SECS를, 등록 창의 「SECS Settings」에서는 HSMS 관련 설정을 만난다. 메시지·타이머의 세부 뜻은 00.rag/user/secs-hsms-message-and-timer-reference.md에서 다룬다.
§ L200-211 [23] SOCKET과 PLC는 어떤 통신 방식인가   parent=246 children=2 sources=5
    L209-209 문단 112  | 설비 정보의 「Comm Interface」는 통신 방식이다. SOCKET은 설비가 보내는 바이트를 프레임으로 구분해 해석하는 방식이고, PLC는 설비 제어기의 데이터 영역을 정한 주기로 읽는 방식이다.
    L211-211 문단 133  | 프레임 규칙은 00.rag/user/socket-framing-and-parsing-reference.md, PLC 데이터 항목은 00.rag/user/plc-modbus-register-and-type-reference.md에서 다룬다.
§ L213-227 [24] Comm Mode는 어느 쪽의 접속 방향인가   parent=249 children=4 sources=3
    L220-220 문단 95  | 「Comm Mode」는 Gateway를 기준으로 누가 연결을 먼저 여는지 구별하는 값이다. 설비 정보 표에서 통신 방식인 「Comm Interface」와 별도로 표시한다.
    L224-224 표 행 37  | 값=ACTIVE | 접속 방향=Gateway가 설비에 연결을 건다.
    L225-225 표 행 47  | 값=PASSIVE | 접속 방향=Gateway가 접속을 기다리고 설비가 연결을 건다.
    L227-227 문단 67  | 설정 입력 절차는 00.rag/user/equipment-registration-and-settings.md에서 다룬다.
§ L229-238 [22] 설비 정보의 Port는 무엇을 가리키는가   parent=185 children=2 sources=3
    L236-236 문단 100  | 설비 정보 표의 「Port」와 등록·수정 창의 「EQP Port」는 설비 연결에 사용하는 네트워크 포트 번호다. Gateway가 설비로 접속할 때 설비 주소와 이 번호를 사용한다.
    L238-238 문단 84  | 업무 문서에서 설비의 반입·반출구를 뜻하는 Port는 00.rag/user/lot-carrier-work-and-system-flow.md에서 다룬다.
§ L240-249 [26] Revision은 모델 버전과 어떻게 구별하는가   parent=226 children=2 sources=3
    L247-247 문단 110  | Revision은 설비 같은 대상의 변경 상태를 구별하는 판 번호다. 변경 요청의 기준 상태가 유지되는지 대조할 때 사용하며, 오류 코드 REVISION_CONFLICT를 설명하는 문서에서 만난다.
    L249-249 문단 115  | 모델 버전은 메시지·처리 정의의 판이고 Revision은 변경 대상의 상태를 대조하는 번호다. 오류별 조치는 00.rag/user/error-message-and-code-reference.md에서 다룬다.
SUMMARY sections=19 children=45 maxChild=147 maxParent=322
CORPUS docs=15/53 sections=263 part={user=15} type={개념=2, 사용 절차=13}
TOTAL files=15 targets=1 violations=0
```

사람 검사(모든 19절):

| 절 | 제목만으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·기존 문서와의 중복 판정 |
|---|---|---|---|---|
| Gateway와 Business는 어떤 일을 맡는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| Shard group과 Route Partition은 어떻게 연결되는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| Lease·Pod·spare는 무엇을 구별하는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| 설비와 라인은 무엇인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| 모델과 모델 버전은 무엇이 다른가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| root·branch·suffix는 모델에서 무엇을 뜻하는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| Check Out과 Check In은 어떤 편집 경계인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| parent commit은 어느 모델에 반영하는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| Workflow와 Flow는 메시지 처리에서 무엇인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| MDF는 어떤 파일인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| DCOP 수집 항목은 Flow와 어떤 관계인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| 배포는 어떤 변경을 묶는 작업인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| DLQ와 payload는 실패 조회에서 무엇을 뜻하는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| 격리 설비와 Repair Task는 무엇이 다른가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 정의에 필요한 최소 사실만 유지하고 조작은 소유 문서로 안내 |
| SECS와 HSMS는 어느 부분을 가리키는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| SOCKET과 PLC는 어떤 통신 방식인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| Comm Mode는 어느 쪽의 접속 방향인가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |
| 설비 정보의 Port는 무엇을 가리키는가 | 적합 | ACTIVE 접속·PASSIVE 리스너 경로 재대조 | 모드별 포트 역할과 만나는 위치를 함께 제공 | Comm Mode 절의 접속 방향과 대조했으며 절 구성은 유지 |
| Revision은 모델 버전과 어떻게 구별하는가 | 적합 | 위 사실 대조표로 확인 | 주체·정의·만나는 위치를 함께 제공 | 해당 관계의 정의를 이 절이 소유하며 다른 절에 답을 복제하지 않음 |

- H2 정확 중복·불필요한 제목 틀 반복·금지 접미 표현: 0건. 질문형이라는 이유로 서로 다른 명사를 지운 모든 제목을 하나의 틀로 세지 않았다. 대상과 비교 관계가 각각 달라야 한다는 기준으로 전부 읽었다.
- 본문 자식의 동일 문자열·다른 파일과의 동일 자식: 지정 검사기 위반 0건. 의미가 겹치는 정의는 새 조작 답으로 늘리지 않고 위 소유 범위로 제한했다.
- 정보 없는 반복 문장·이름 토막 번역·주어 소실·잘린 문장·오답 사례: 미해소 0건.
- 현재 mask-v1 적용 후 MASK 출력 0건. 머리글은 용어/뜻·값/접속 방향이며 이름·자격 값 표기가 아니다.
- 권한 경계: 실제 화면 조작·내부 서비스 접근 절차·admin 문서 안내를 검색 원문에 넣지 않았다. 개념 조회 문서에서 화면의 작업 권한을 사용 가능 권한으로 약속하지 않았다.
- 제품 빌드·시험·실행 UI 검증은 수행하지 않았다. 이 작업은 문서 작성·소스 대조이며 현장 동작 인증이나 독립 공식 검증이 아니다. 메인 세션의 직접 대조가 후속 검증이다.
- 독립/하위 AI 호출: 0회. provider 사용량·요금과 전체 세션 wall time은 제공되지 않아 미측정이다. 보완 전 지정 검사기 실행은 2회이며 첫 실행 9.8077041초, 당시 최종 실행 9.926638초다. 이번 Port 보완 검사 2회를 포함한 누계는 4회다.
- 검사용 임시 파일·디렉터리·서비스·브라우저는 만들지 않았다. 지정 work/ 아래를 포함해 이 세션의 삭제할 임시 자원은 없다.
- 보완 전 완료 보고의 한계: 자체검사에서 Port의 PASSIVE 조건 누락을 놓쳤고 메인 세션이 결함 1건을 확인했다. 그 최초 완료 주장은 아래 Port 보완 이력으로 정정한다. 다른 문서·원천·제품·설정·SPEC·검사기는 수정하지 않았다.

## 2026-11-19 메인 검증 Port 결함 보완

- 요청 범위: 검색 원문 Port 절과 그 직접 영향인 출처·작성 기록만 보완했다. 검색 원문의 다른 18절과 Comm Mode 표는 그대로 보존했다.
- 원인: 최초 작성 때 GatewayActiveReconnectCoordinator의 ACTIVE 접속만 Port의 뜻으로 일반화했다. PASSIVE 리스너 생성 경로를 함께 대조하지 못했다.
- 수정: 검색 원문 L239에서 공통 네트워크 포트를 정의하고, L241에서 ACTIVE의 설비 주소·포트 접속과 PASSIVE의 같은 번호 리스너 열기를 구별했다. 해당 절은 부모 241자·자식 3개로 450자 이하여서 절을 나누지 않았다.
- 추가 원천: `nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayPassiveListenerRegistry.java @ d3636ab86763`의 L66~76·L377~388을 읽어 eqpPort → PassiveListenerKey.port → bind의 실제 전달을 확인했다. GatewayNettyBootstrap L310~330과 GatewayActiveReconnectCoordinator L222~259도 다시 대조했다.
- 원천 판본: nori-tc-gateway HEAD는 d3636ab86763이었다. 위 세 Java 파일을 명시한 `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway -C ../nori-tc-gateway status --short -- <세 파일 경로>`는 exit 0, stdout 빈 값이었다. 원천은 작업 트리 변경이 없는 commit 판본이다.
- bind 주소: GatewayPassiveListenerRegistry L471~481의 requirePassiveListenerBindIp는 normalizeText(info.eqpIp())를 반환한다. 별도의 설정 키를 추정하지 않았고 본문에는 주소 공급원이나 설정 키를 추가하지 않았다. L76의 passiveBindIp는 이 메서드 반환값으로, 포트 역할의 구별은 주소 설정 키를 쓰지 않아도 완결된다.
- 이전 검색 원문 SHA-256: `f3d57ded6f21eb309cb79011d026f05394faeffeef10dac4ea32d17bd726eeca` (249줄·19절).
- 보완 검색 원문 SHA-256: `0cd73192a1a0387dfb678c1425d38ca297ad7c2212efa490c306f1821c9250b4` (254줄·19절).
- 사실 대조표: Port의 3행을 조건별 4행으로 보완했다. 본문 뒤 Revision의 내용은 그대로 두고 대조표 줄 번호만 L252·L254로 맞췄다. 전체 대조는 80행이다.
- 입력 재사용: 작성 규칙 SHA-256 `273f44daeb41ffe7a63fbd2231c8b73f58464af19a6a580523e82ca5c578e13b`와 목록 SHA-256 `45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b`가 보완 전과 같음을 확인했다.
- 패치 처리: apply_patch 두 차례가 잘못된 문맥으로 검증 단계에서 실패해 변경되지 않았다. 각각 Port 절과 작성 기록의 실제 줄을 다시 읽고, 전체 줄이 일치하는 변경 부분으로 맞춰 적용했다.
- 보완 후 사람 검사: Port의 모든 문장과 인용 범위를 재대조하고 Comm Mode의 ACTIVE·PASSIVE 표와 방향이 일치함을 확인했다. 기존 다른 절의 답·순서·표·권한 경계를 바꾸지 않았다.
- 보완 후 지정 검사: Port 본문 보완 뒤 1회와 확인일을 2026-11-19로 맞춘 뒤 최종 1회, 합계 2회 모두 exit 0. sections=19, children=46, maxChild=147, maxParent=322, violations=0. MASK·CROSS VIOLATION 출력은 없다. 이번 검사 전체 wall time은 도구 출력에 완전한 값으로 제공되지 않아 미측정이다.
- 검증 구분: 제품 빌드·시험·현장 접속·실행 화면 검증·후속 독립 검증은 하지 않았다. 위 결과는 문서 보완 자체검사다.
- 임시 자원: 검사 파일·디렉터리·상주 프로세스를 만들지 않았다.
- 보완 완료: 요청 결함 1건을 수정했고 자체검사에서 남은 결함 0건, 사용자 결정 0건이다.

최종 검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/tc-components-and-terms.md
```

최종 검사 출력 마지막 10줄:

```text
§ L229-243 [22] 설비 정보의 Port는 무엇을 가리키는가   parent=241 children=3 sources=6
    L239-239 문단 62  | 설비 정보 표의 「Port」와 등록·수정 창의 「EQP Port」는 설비 연결에 사용하는 네트워크 포트 번호다.
    L241-241 문단 93  | Comm Mode가 ACTIVE이면 Gateway가 설비 주소와 이 번호로 접속한다. PASSIVE이면 Gateway가 이 번호로 리스너를 열어 설비 접속을 기다린다.
    L243-243 문단 84  | 업무 문서에서 설비의 반입·반출구를 뜻하는 Port는 00.rag/user/lot-carrier-work-and-system-flow.md에서 다룬다.
§ L245-254 [26] Revision은 모델 버전과 어떻게 구별하는가   parent=226 children=2 sources=3
    L252-252 문단 110  | Revision은 설비 같은 대상의 변경 상태를 구별하는 판 번호다. 변경 요청의 기준 상태가 유지되는지 대조할 때 사용하며, 오류 코드 REVISION_CONFLICT를 설명하는 문서에서 만난다.
    L254-254 문단 115  | 모델 버전은 메시지·처리 정의의 판이고 Revision은 변경 대상의 상태를 대조하는 번호다. 오류별 조치는 00.rag/user/error-message-and-code-reference.md에서 다룬다.
SUMMARY sections=19 children=46 maxChild=147 maxParent=322
CORPUS docs=17/53 sections=285 part={user=17} type={개념=4, 사용 절차=13}
TOTAL files=17 targets=1 violations=0
```


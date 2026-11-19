# 상태값 문서 작성 기록

## 대상
- objectKey: `00.rag/user/state-reference.md`
- 구획·권한: user `[AI_DOC_READ]` / 유형: 참조
- 검색 원문 SHA-256: `bcc635d7e1900a203b5bfd79dbe0d37007b59dae57d01a82df5ad06bd28271ce`
- 검색 원문: 317줄·H2 17절·본문 자식 85개
- 확인일: 2026-11-19 (한국 시간)
- 작성 범위: 검색 원문과 이 작성 기록 두 파일. 제품·다른 문서 변경, 업로드, git add·commit·push를 하지 않았다. 독립·하위 Agent를 호출하지 않았다.

## 원천 판본
각 절에 기록한 모든 코드 파일에 대해 `git -c safe.directory=<해당 저장소 절대경로> -C <저장소> status --short -- <원천 경로들>`을 실행했다. 아래는 최종 원문 참조에서 추출한 정확한 경로별 상태 확인 결과다. 전역 Git 설정은 변경하지 않았다. 본문은 소스 기준이며 현장 배포 판본·현재 상태를 뜻하지 않는다.

| 저장소 | HEAD 12자리 | 원천 파일 작업 트리 상태 |
|---|---|---|
| nori-tc-business | 7db24f7466bd | 5개 참조 파일·exit 0·수정 출력 없음 |
| nori-tc-common | 6fe71f6b2da6 | 2개 참조 파일·exit 0·수정 출력 없음 |
| nori-tc-gateway | d3636ab86763 | 2개 참조 파일·exit 0·수정 출력 없음 |
| nori-tc-simulator | 5e9b02e3ae16 | 3개 참조 파일·exit 0·수정 출력 없음 |
| nori-tc-ui-backend | 985d17c604dd | 15개 참조 파일·exit 0·수정 출력 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | 9개 참조 파일·exit 0·수정 출력 없음 |

작업 시작: main이 origin/main보다 8개 커밋 앞서고 `?? rag-documents/`가 있었다. worktree는 현재 main 하나였다. 기존 파일을 사용자 작업으로 보존했다. rg가 PATH에 없어 git grep과 직접 경로 읽기를 사용했다. 형제 저장소의 Git 소유자 검사는 해당 명령의 저장소별 `-c safe.directory`로 읽었다. 자격 파일은 읽지 않았다.

## 절별 사실 대조
최종 원문 문단·표 자료 행을 각각 한 행으로 대응했다. 여러 문장이 있는 행은 원천들을 함께 대조했다. 코드 경로와 줄은 절의 rag-meta와 결속한다. 원문을 추출해 대조표에 옮겼으며 검색 원문의 제목·본문을 스크립트로 생성하지 않았다.

### 설비 연결을 나타내는 controlState

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L19 | 설비의 controlState는 실제 연결 상태를 나타낸다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/common/eqp/ControlState.java @ 6fe71f6b2da6 ; ControlState L3~18 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 연결·종료 기록 L93~145 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 채널 연결·끊김 기록 L163~215 | 연결 상태 필드와 온라인·오프라인 기록을 구별했다. |
| L23 | LOCAL·REMOTE \| 설비 화면이 연결됨으로 판정하는 값이다. Gateway는 시작 확정 또는 채널 연결 기록에 REMOTE를 쓴다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 연결·종료 기록 L93~145 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 채널 연결·끊김 기록 L163~215 / nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; 연결 판정 L12~15 / nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; isConnectedControlState L47~48 | 화면의 온라인 집합 및 Gateway의 REMOTE 기록을 확인했다. |
| L24 | OFFLINE \| Gateway가 종료 또는 비자발적 연결 끊김을 기록할 때 쓰는 값이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 연결·종료 기록 L93~145 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 채널 연결·끊김 기록 L163~215 | 정상 종료와 비자발적 끊김의 OFFLINE 기록을 확인했다. |
| L25 | DOWN·DISCONNECTED \| 화면에서 연결 끊김으로 분류한다. 새 설비 생성 시 SECS는 DOWN, 그 밖의 통신 방식은 DISCONNECTED로 기록한다. | nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; 연결 판정 L12~15 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; createInitialStateCommand L707~713 | 끊김 분류 및 통신 방식별 초기 상태 분기를 확인했다. |
| L27 | 연결 문제의 확인 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | 기존 연결·명령 문제 문서의 범위와 경로를 확인했다. |

### 설비 연결 의도인 desiredState

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L37 | desiredState는 설비 연결을 유지하려는 의도다. 현재 연결 여부는 controlState로 구별한다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/common/eqp/DesiredState.java @ 6fe71f6b2da6 ; DesiredState L3~16 | 실제 연결과 연결 의도 필드를 구별했다. |
| L41 | STARTED \| 시작 처리로 확정한 연결 의도다. 비자발적으로 연결이 끊겨도 현재 의도를 보존한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forStart·forEnd L93~145 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forOffline L187~215 | 시작 의도 확정 및 비자발적 끊김의 현재값 보존이다. |
| L42 | ENDED \| 종료 처리로 연결 의도를 해제한 값이며, 아직 시작하지 않은 새 설비의 기본값이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forStart·forEnd L93~145 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 신규 연결 의도 L715~722 | 종료 ENDED 및 신규 생성 커맨드의 ENDED를 확인했다. |
| L44 | 명령별 사용 절차는 `00.rag/user/equipment-commands-and-polling.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | 명령 사용 절차 문서의 범위와 경로를 확인했다. |

### 포트 집계·설비 등록·연결 종료의 eqpState

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L55 | eqpState는 설비 상태다. Business의 포트 집계는 controlState가 LOCAL 또는 REMOTE일 때 수행한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpStateTransitionSupport.java @ 7db24f7466bd ; 집계 조건·상태 저장 L73~104 | LOCAL·REMOTE에서만 집계하는 분기다. |
| L59 | RUN \| 포트 집계에 LOAD 또는 UNLOAD인 포트가 하나 이상 있다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpStateTransitionSupport.java @ 7db24f7466bd ; resolveTargetEqpState L116~125 | LOAD·UNLOAD 존재 시 RUN 반환이다. |
| L60 | IDLE \| 포트 집계에 LOAD·UNLOAD인 포트가 없다. Gateway의 시작 확정 때도 이 값을 기록한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpStateTransitionSupport.java @ 7db24f7466bd ; resolveTargetEqpState L116~125 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forStart·forEnd L93~145 | 집계 기본 IDLE과 시작 시 IDLE 기록이다. |
| L61 | DOWN \| SECS 설비의 생성 초기값이다. Gateway가 종료 또는 비자발적 연결 끊김을 기록할 때도 설정한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forOffline L195~215 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 초기 설비 상태 L707~713 | 종료·끊김 DOWN과 SECS 초기 DOWN이다. |
| L62 | SERVICE_UNAVAILABLE \| SECS 이외 설비를 새로 만들 때 기록하는 초기값이다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 초기 설비 상태 L707~713 | SECS 이외의 신규 생성 분기다. |

### Live-Map의 설비 RUN·IDLE·DOWN·ERROR

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L70 | Live-Map 설비 상태는 controlState와 eqpState를 조합한 표시값이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; resolveEquipmentStatus L178~195 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; 상태 응답 L65~87 | 설비 응답 status에 조합 결과를 넣는다. |
| L74 | RUN \| controlState가 LOCAL·REMOTE이고 eqpState가 RUN이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; resolveEquipmentStatus L178~195 | 연결 상태에서 RUN 분기다. |
| L75 | IDLE \| controlState가 LOCAL·REMOTE이고 eqpState가 IDLE이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; resolveEquipmentStatus L178~195 | 연결 상태에서 IDLE 분기다. |
| L76 | DOWN \| controlState가 OFFLINE·DOWN·DISCONNECTED이거나, LOCAL·REMOTE에서 eqpState가 DOWN·SERVICE_UNAVAILABLE이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; resolveEquipmentStatus L178~195 | 끊김 우선 판정 및 DOWN·SERVICE_UNAVAILABLE 매핑이다. |
| L77 | ERROR \| LOCAL·REMOTE에서 eqpState가 MAINTENANCE·PAUSE 또는 그 밖의 값이거나, controlState가 위 분류에 없다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; resolveEquipmentStatus L178~195 | MAINTENANCE·PAUSE·그 밖 값과 미지원 controlState의 ERROR 분기다. |
| L79 | 화면에서 설비를 찾고 표시를 읽는 방법은 `00.rag/user/live-map-status.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | Live-Map 상태 보기 문서의 범위와 경로를 확인했다. |

### 포트 원시 상태 LOAD·UNLOAD·IDLE·DOWN

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L91 | portState는 포트별 상태다. 다음은 해당 액션이 실행되는 경로에서 확인한 기록 조건이다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierLoadedTcAction.java @ 7db24f7466bd ; handleCarrierLoaded L28~36 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/UnloadRequestTcAction.java @ 7db24f7466bd ; handleUnloadRequest L36~50 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierUnloadRequestTcAction.java @ 7db24f7466bd ; handleCarrierUnloadRequest L49~63 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 전이값·캐리어 해제 L24~26 | 포트 ID별 상태 전이 호출이다. |
| L95 | LOAD \| CARRIER_LOADED 액션이 지정 포트에 기록하는 상태다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierLoadedTcAction.java @ 7db24f7466bd ; handleCarrierLoaded L28~36 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 전이값·캐리어 해제 L24~26 | CARRIER_LOADED의 setLoad 호출이다. |
| L96 | UNLOAD \| Lot ID가 있는 UNLOAD_REQUEST 액션이 지정 포트에 기록하는 상태다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/UnloadRequestTcAction.java @ 7db24f7466bd ; handleUnloadRequest L36~50 | Lot 누락 시 반환한 뒤 setUnload를 호출한다. |
| L97 | IDLE \| CARRIER_UNLOAD_REQUEST에서 Lot ID를 확인한 뒤 기록하는 값이다. IDLE 전이는 포트의 Carrier ID도 비운다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierUnloadRequestTcAction.java @ 7db24f7466bd ; handleCarrierUnloadRequest L49~63 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; clearCarrierIdWhenIdle L129~132 | Lot 검사 뒤 setIdle 및 Carrier ID 초기화다. |
| L98 | DOWN \| Gateway가 설비를 OFFLINE으로 기록할 때 모든 포트에 반영하는 값이다. | nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentStatePersistenceService.java @ d3636ab86763 ; 종료 후 포트 투영 L208~225 | OFFLINE 기록 경로에서 모든 포트를 DOWN으로 바꾼다. |

### Live-Map 포트의 RUN·IDLE·DOWN 변환

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L105 | Live-Map의 포트 상태는 원래 portState를 다음처럼 묶어 표시한다. 설비 상태와 포트 상태는 각각 계산한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toPortView·resolvePortStatus L197~220 | 포트 뷰는 portState로 계산하며 설비 상태와 별도다. |
| L109 | RUN \| LOAD·LOADED·UNLOAD | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toPortView·resolvePortStatus L197~220 | 세 값의 RUN 분기다. |
| L110 | IDLE \| IDLE·EMPTY·READY_TO_LOAD | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toPortView·resolvePortStatus L197~220 | 세 값의 IDLE 분기다. |
| L111 | DOWN \| DOWN·OUT_OF_SERVICE·UNKNOWN | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toPortView·resolvePortStatus L197~220 | 세 값의 DOWN 분기다. |
| L112 | IDLE \| 위 목록에 없는 값 또는 빈 값 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toPortView·resolvePortStatus L197~220 | 기타·빈 값의 default IDLE 분기다. |
| L114 | 표시 위치와 캐리어 확인 방법은 `00.rag/user/live-map-status.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | PORT 상태 표시와 캐리어 조회 문서 경로다. |

### 비동기 작업 operation의 진행·종결 상태

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L124 | 비동기 작업 조회의 status는 요청한 변경 작업의 결과다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UiOperationState L9~25 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; getResult L85~107 | 작업 조회 응답 status를 확인했다. |
| L128 | PENDING \| 진행 중이거나 복구 중이며 최종 결과가 확정되지 않았다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UiOperationState L9~25 | 진행·복구 중 비종결 상태다. |
| L129 | PASS \| 변경 결과 저장과 사용 차단 해제가 끝난 성공이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UiOperationState L9~25 | 결과 저장·장벽 개방까지 확정된 성공이다. |
| L130 | FAIL \| 확정된 실패 또는 되돌림 완료다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UiOperationState L9~25 | 확정 실패 또는 되돌림 완료다. |
| L131 | REPAIR_REQUIRED \| 보상 완료를 입증하지 못해 수동 복구가 필요한 상태다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UiOperationState L9~25 | 미입증 보상 및 수동 복구 필요를 뜻한다. |
| L133 | 작업을 찾은 조회 응답은 status가 PENDING이면 HTTP 202, 그 밖의 값이면 HTTP 200이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; getResult L85~107 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationStatusView.java @ 985d17c604dd ; terminal L50~52 | terminal()은 PENDING만 제외하므로 조회된 작업의 HTTP 대응은 PENDING=202, 그 밖=200이다. |

### 작업의 RECONCILING과 화면의 확인 지연

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L146 | phase의 RECONCILING \| 최종 결과가 없는 작업의 갱신 시각이 관찰 기한을 넘으면 조회에 표시한다. 이전 비동기 결과의 TIMEOUT도 PENDING·RECONCILING으로 변환한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationQueryService.java @ 985d17c604dd ; displayPhase·이전 결과 변환 L83~118 | 관찰 기한 초과와 이전 TIMEOUT 변환을 확인했다. |
| L147 | 화면의 「확인 지연」 \| 브라우저가 정해진 조회 횟수 안에 최종 상태를 받지 못해 DELAYED로 반환한 결과 표시다. | nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; pollOperationUntilTerminal L124~142 / nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; DELAYED 표시 L289~297 / nori-tc-ui-front/src/features/eqp/lib/eqp-operation-result-presentation.ts @ 9fe69a75dcd3 ; delayed L43~48 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; resultDelayed L1116~1116 | 조회 소진의 DELAYED 반환·결과 표시·한국어 문구 연결이다. |
| L149 | 재조회와 운영 인계는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | 기존 재조회·운영 인계 절을 확인했다. |

### 배포 전체의 PLANNING·READY·RUNNING

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L159 | 배포 전체 상태는 배포 번호 하나에 대한 진행 상태다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; 생성 초기 상태 L80~88 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; finishDeployCreation L146~193 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; startDeploy L531~563 | deployKey 단위 헤더 상태다. |
| L163 | PLANNING \| 배포 기록을 만든 초기 상태다. 실행 가능한 대상과 변경 전 자료를 준비하는 동안 사용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; 생성 초기 상태 L80~88 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; finishDeployCreation L146~193 | 초기 INSERT 및 변경 전 상태 확보 순서다. |
| L164 | READY \| 실행할 배포의 생성이 끝난 대기 상태다. Hold 뒤 남은 대상이 있어 다시 대기할 때도 사용한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; finishDeployCreation L146~193 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployServiceCommandSupport.java @ 985d17c604dd ; Hold 후 READY L103~116 | 생성 완료 및 Hold 후 남은 대상 대기다. |
| L165 | RUNNING \| 배포 실행을 접수하고 대상을 실행하는 상태다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; startDeploy L531~563 | RUNNING 기록 뒤 dispatch한다. |
| L167 | 실행과 Hold 조작은 `00.rag/user/deployment-workflow.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | 배포 실행·Hold 사용 절차의 소유 문서다. |

### 배포 전체의 완료·실패·취소 판정

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L178 | 진행·대기 대상이 없을 때 실패 집계는 FAILED·ROLLED_BACK·COMPENSATION_FAILED, 성공 집계는 COMPLETED·NO_CHANGE를 포함한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployServiceCommandSupport.java @ 985d17c604dd ; refreshDeployStatus L93~138 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/deploy/DeployTargetCounts.java @ 985d17c604dd ; 실패·성공 집계 L23~29 | 진행·대기 건수와 실패·성공 집계 정의다. |
| L182 | COMPLETED \| 실패 집계가 없는 종료다. 실행할 변경이 전혀 없어 생성 직후 끝나는 경우도 포함한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployServiceCommandSupport.java @ 985d17c604dd ; refreshDeployStatus L93~138 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 변경 없음·생성 실패 L150~174 | 실패 없는 종료와 전체 noChange의 생성 즉시 종료다. |
| L183 | PARTIAL_FAILED \| 실패 집계와 성공 집계가 모두 있다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployServiceCommandSupport.java @ 985d17c604dd ; refreshDeployStatus L93~138 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/deploy/DeployTargetCounts.java @ 985d17c604dd ; 실패·성공 집계 L23~29 | 실패와 성공 유사 집계가 공존한다. |
| L184 | FAILED \| 실패 집계만 있거나 배포 생성 완료에 실패했다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployServiceCommandSupport.java @ 985d17c604dd ; refreshDeployStatus L93~138 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; markDeployCreationFailed L197~209 | 성공 집계 없는 실패 또는 생성 완료 실패다. |
| L185 | CANCELED \| 배포 취소 처리가 확정됐다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; markDeployCanceled L764~772 | 대상 취소와 헤더 CANCELED 기록이다. |

### 배포 대상 설비의 PENDING·NO_CHANGE·RUNNING·COMPLETED

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L194 | 배포 대상 상태는 배포 안의 설비 한 대에 대한 값이다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; insertTargetState L191~200 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 응답 대기·대상 완료 L190~237 | deploy_target_key 단위 상태다. |
| L198 | PENDING \| 변경할 대상의 초기 대기 상태다. 완료 단계를 보존한 재개 대기에도 사용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; insertTargetState L191~200 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; markTargetReadyForResume L811~841 | 초기 변경 대상 또는 재개 체크포인트 대기다. |
| L199 | NO_CHANGE \| 해당 대상에 실행할 변경이 없어 처음부터 종료 처리한 상태다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; insertTargetState L191~200 | 변경 없음의 초기 종료 처리다. |
| L200 | RUNNING \| 대상의 현재 단계를 실행하며 응답을 기다리는 상태다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 응답 대기·대상 완료 L190~237 | 현재 단계 응답 대기를 저장한다. |
| L201 | COMPLETED \| 대상 설비의 배포 완료를 기록한 상태다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 응답 대기·대상 완료 L190~237 | 대상 COMPLETED 기록과 현재 단계 초기화다. |

### 배포 대상·단계의 실패와 되돌림 상태

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L213 | FAILED \| 대상 또는 단계의 실행 실패를 기록한 값이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployExecutor.java @ 985d17c604dd ; handleTargetFailure L289~312 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 되돌림·실패 기록 L270~403 | 실패 처리기가 대상 및 단계 FAILED를 저장한다. |
| L214 | ROLLING_BACK \| 대상과 현재 단계가 앞서 적용한 변경을 되돌리는 보상 처리 중이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployRollbackCoordinator.java @ 985d17c604dd ; 보상 실행·확정 L128~203 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 되돌림·실패 기록 L270~403 | 완료한 단계의 보상 실행과 ROLLING_BACK 저장이다. |
| L215 | ROLLED_BACK \| 대상의 보상 처리가 끝났다. 이때 현재 단계 정보는 비운다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployRollbackCoordinator.java @ 985d17c604dd ; 보상 실행·확정 L128~203 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 되돌림·실패 기록 L270~403 | 보상 완료 뒤 대상 ROLLED_BACK과 현재 단계 초기화다. |
| L216 | COMPENSATION_FAILED \| 대상과 현재 단계의 보상 자료 해석·명령 발행·응답 확인 등에 실패했다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployRollbackCoordinator.java @ 985d17c604dd ; 보상 실행·확정 L128~203 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 되돌림·실패 기록 L270~403 | 보상 자료·발행·응답 실패를 두 상태에 기록한다. |
| L217 | CANCELED \| 배포 취소로 PENDING·RUNNING·ROLLING_BACK인 대상을 중단하며 대상과 단계에 기록한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; cancelTargets L787~805 | 세 대상 상태를 취소 대상으로 골라 단계와 함께 CANCELED로 쓴다. |
| L219 | 실패 확인과 인계는 `00.rag/user/deployment-troubleshooting.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | 배포 대기·실패·취소 진단 소유 문서다. |

### 배포의 Step 상태 WAITING_REPLY와 COMPLETED

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L231 | 「Deploy View」의 「Step 상태」는 대상 설비의 현재 단계에 대한 값이다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 단계 표시 L280~293 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; colStepStatus L299~299 | 현재 단계 렌더 열과 한국어 문구 연결이다. |
| L235 | WAITING_REPLY \| 단계의 응답 추적과 대기 상태를 기록한 값이다. 명령 발행 직전에 설정한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; publishStep L246~258 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 단계 상태 기록 L190~208 | 발행 전 대기·추적 등록과 WAITING_REPLY 저장이다. |
| L236 | COMPLETED \| 성공 응답을 받은 단계의 완료 지점을 보존한 값이다. 대상 전체의 최종 완료는 별도로 확정한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 성공 단계 보존 L204~227 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 단계 완료 보존 L811~841 | 성공 단계 체크포인트와 대상 최종 완료를 구별한다. |
| L238 | 대상 상태가 PENDING이어도 마지막으로 마친 단계의 「Step 상태」는 COMPLETED일 수 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 단계 완료 보존 L811~841 | target PENDING과 step COMPLETED를 함께 기록한다. |

### 배포 대상의 결과 라벨과 상세 상태

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L247 | 「Deploy View」에서 「결과」는 대상 상태를 묶은 라벨이고, 「상세 상태」는 개별 상태값이다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 결과·상세 상태 열 L249~266 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 결과·상세 상태 L296~297 | 결과 변환과 상세 원시 라벨을 각각 표시한다. |
| L251 | PASS \| COMPLETED·NO_CHANGE | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; resolveTargetResultLabel L42~67 | 두 성공 유사 값을 PASS로 묶는다. |
| L252 | FAIL \| FAILED | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; resolveTargetResultLabel L42~67 | FAILED를 FAIL로 변환한다. |
| L253 | ROLLBACK \| ROLLING_BACK·ROLLED_BACK | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; resolveTargetResultLabel L42~67 | 되돌림 진행·완료를 ROLLBACK으로 묶는다. |
| L254 | REPAIR_REQUIRED \| COMPENSATION_FAILED·REPAIR_REQUIRED | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; resolveTargetResultLabel L42~67 | 두 복구 필요 값을 REPAIR_REQUIRED로 묶는다. |
| L256 | PENDING·RUNNING·RECONCILING·FINALIZING·CANCELED는 「결과」에도 같은 문자열로 표시한다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; resolveTargetResultLabel L42~67 | 다섯 값은 같은 문자열을 반환한다. |

### 해석할 수 없는 작업·배포 상태

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L275 | 비동기 작업 \| UNKNOWN은 서버가 원장 값을 해석하지 못한 값이다. 서버는 UNKNOWN에도 HTTP 200을 반환한다. 브라우저는 PASS·FAIL·REPAIR_REQUIRED만 최종 결과로 인정하고, 다른 값은 조회를 이어간다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UNKNOWN L23~24 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/RedisUiOperationLedgerAdapter.java @ 985d17c604dd ; toState L281~289 / nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; 미지원 상태·조회 L67~73 / nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; 조회 종료 L124~142 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationQueryService.java @ 985d17c604dd ; toStatusView L68~80 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationStatusView.java @ 985d17c604dd ; terminal L50~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; HTTP 응답 L92~107 | UNKNOWN을 그대로 전달하는 조회·HTTP 200 판정과, 브라우저의 세 최종 값만 인정하는 판정을 각각 확인했다. |
| L276 | 배포 전체·대상 \| 화면이 지원하지 않는 응답 상태는 표시용 UNKNOWN_SERVER_STATUS로 바꾸고 「알 수 없는 서버 상태」로 표시한다. | nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 대상 상태 변환 L162~162 / nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 상태 변환 L217~217 / nori-tc-ui-front/src/features/deploy/lib/deploy-monitor-format.ts @ 9fe69a75dcd3 ; resolveDeployStatusLabel L11~12 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; deploy.status.unknown L1167~1167 | 미지원 값 변환·라벨 함수·한국어 문구 연결이다. |

### 데모 실행 전체의 RUNNING·TERMINAL·CANCELED·FAILED

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L287 | 「시뮬레이션 진행 현황」의 실행 번호 하나에 대한 Run 상태다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실행 상태 표시 L918~925 | 실행 줄에서 group.state를 표시한다. |
| L291 | RUNNING \| 실행을 만들고 설비별 처리를 진행하는 상태다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; createRun L125~153 | 생성 시 RUNNING으로 만들고 대상 처리를 시작한다. |
| L292 | TERMINAL \| 모든 대상 설비가 COMPLETED로 끝나 실행 전체를 종결한 값이다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; resolveTerminalRunState L613~633 | 모든 대상 COMPLETED일 때 TERMINAL을 고른다. |
| L293 | CANCELED \| 전체 종료 요청으로 중단했거나, 모든 대상이 끝났을 때 COMPLETED가 아닌 대상이 포함된 값이다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; stopRun L263~280 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; resolveTerminalRunState L613~633 | 직접 종료 및 비완료 대상이 섞인 전체 종결 분기다. |
| L294 | FAILED \| 시작 중 워커 실행 요청에 실패했거나, 갱신이 오래 끊긴 실행의 점유를 회수한 값이다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; createRun L125~153 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; reclaim L155~168 | 생성 중 실행 요청 실패와 stale 실행 회수다. |

### 데모 대상 설비의 PENDING·RUNNING·COMPLETED·CANCELED·FAILED

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L307 | 데모 실행을 펼친 설비별 표의 「현재 상태」에 해당한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 설비 상태·완료 오류 수 L597~609 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; colStatus L438~438 | 설비별 상태 열과 한국어 문구다. |
| L311 | PENDING \| 실행에 대상을 등록하고 아직 포트 처리를 시작하지 않은 값이다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 초기·회수 상태 L64~73 | 대상 INSERT 초기 PENDING이다. |
| L312 | RUNNING \| 대상 설비의 포트 처리를 시작했다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/domain/TargetAggregate.java @ 5e9b02e3ae16 ; beginPortLot L99~108 | beginPortLot이 RUNNING을 쓴다. |
| L313 | COMPLETED \| 한 번 실행하는 모드에서 모든 포트의 처리가 완료 또는 오류로 끝났다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 설비 완료·중단 L218~275 | 완료·오류 포트 모두 종결로 세어 전체 종결 시 COMPLETED다. |
| L314 | CANCELED \| 진행 중인 대상 설비의 처리를 중단했다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 설비 완료·중단 L218~275 | 진행 대상을 중단하면 CANCELED다. |
| L315 | FAILED \| 실행 실패·점유 회수 때 아직 끝나지 않은 대상에도 기록하는 실패값이다. | nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 실패 전파 L155~168 / nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 종료 상태 전파 L99~113 | 실행 FAILED 전파와 회수는 미종결 대상에 적용한다. |
| L317 | 실행·중지는 `00.rag/user/demo-execution.md`, 오류 수 해석은 `00.rag/user/demo-troubleshooting.md`가 다룬다. | 관련문서 경로·본문 대조: 아래 기존 문서 SHA-256 목록 | 데모 절차와 오류 집계 진단의 소유 문서다. |

### 기존 문서 대조 판본
다음 문서의 본문을 읽어 중복·소유 범위를 확인했다. 아래 SHA-256은 이번 기록 생성 때 읽은 파일 바이트의 값이다.

| objectKey | SHA-256 |
|---|---|
| 00.rag/user/live-map-status.md | f64f5721df75316ff1cf6c3e1677009c9d2a6fda013d4a7c555e73191c33d119 |
| 00.rag/user/live-map-troubleshooting.md | db7e93e94fe7eb8cf1b895d21a791caee0122584e983792571130f5b77df5172 |
| 00.rag/user/equipment-commands-and-polling.md | 0db9769076d4a29029907142e1e6d91fdee79b4e9a7a0282af73655b91be5a28 |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 59cf3c7b3ad372b01aaa37b6807df4f6b99d7ff3800a580ef2bbbce8fe8a9cbe |
| 00.rag/user/deployment-workflow.md | a2b18e6614520bd24689f10899d457471a3e8f2bf55db31d6f0978021894f804 |
| 00.rag/user/deployment-troubleshooting.md | a16f22c238c5cbd7e42e492dde2bb7495d0674543356eed1304a3b65ec0ab137 |
| 00.rag/user/demo-execution.md | c76cf0037a500734d3bd8d5dd9312eaeb2e003a1730d19ef2e574603c080f9b5 |
| 00.rag/user/demo-troubleshooting.md | bdea861a8f902d6fb4cb2b203b0b8df4ef2fc484d554617781f767cd89fd2d22 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d |
| 00.rag/user/lot-carrier-work-and-system-flow.md | c79ae6a0617387dd40fdee36217e3af3099e9e63129e24cb298b51caf138ab88 |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | 2677c882740a639cc89cf5d6e7869936b3a1fbad95dad88ef61b119527440a0d |

추가로 오류 메시지 참조의 REPAIR_REQUIRED·TIMEOUT·STEP_TIMEOUT·보상 대기 오류 절과 설비 등록·갱신 문서의 상태 관련 본문을 검색해 읽었다. 오류 코드 표를 복제하지 않았다.

### 실제 호출 흐름과 기존 시험 기대값
- Live-Map: front `live-map.api.ts` L41~50 → backend `LiveMapController.java` L47~51 → `LiveMapQueryService` 설비·포트 분기. 데모: front `live-map-demo-run.api.ts` L90~120 → 같은 컨트롤러 L69~78 → `LiveMapSimulatorRunFacade.java` L192~198 → `HttpSimulatorOrchestratorClient.java` L55~62 → 실행·대상 상태.
- 비동기 작업: front `operation.api.ts` L19~48 → `AsyncResultController` L85~107 → `UiOperationQueryService` L58~118. `useEqpLifecycleCommands.ts` L265~307과 결과 표시 코드·한국어 문구도 대조했다.
- 배포: front `deploy.api.ts`의 상태 허용 집합·변환과 `DeployMonitorViewModal`의 결과·상세·Step 상태를 서버의 생성·실행·집계·저장·보상 경로와 대조했다.
- gateway `GatewayEquipmentStatePersistenceServiceTest.java` L41~102: 시작 REMOTE·IDLE·STARTED, 종료 OFFLINE·DOWN·ENDED, 비자발적 끊김의 의도 보존 기대값을 읽었다.
- business `EqpStateTransitionSupportTest.java` L25~54: LOAD 포트가 있으면 RUN, IDLE 포트만 있으면 IDLE이라는 기대값을 읽었다.
- ui-backend `LiveMapQueryServiceTest.java` L102~154: OFFLINE 우선 DOWN, MAINTENANCE→ERROR, SERVICE_UNAVAILABLE→DOWN 기대값을 읽었다.
- 같은 저장소 `UiOperationQueryAndContractTest.java` L44~75: 원장 PASS·FAIL 유지와 이전 PENDING 변환 기대값을 읽었다. `DeployServiceTest.java` L434~457·L494~509: 완료+되돌림 PARTIAL_FAILED, 보상 실패 FAILED·COMPENSATION_FAILED 기대값을 읽었다.
- simulator `AllModeOnceTerminationIntegrationTest.java` L135~155: 오류 포트가 있어도 실행 전체 TERMINAL이 되는 기대값을 읽었다.
- 위 내용은 코드·시험 기대값을 읽은 사실 대조다. 이번 세션에서 제품 시험을 실행한 결과가 아니다. 문서 작업으로 build·서비스 실행은 하지 않았다.

## 매뉴얼과 코드가 다른 곳
이번 문서의 원천은 목록에서 지정한 제품 코드다. 매뉴얼 상태 표를 동작의 근거로 사용하지 않았고, 매뉴얼·코드 충돌을 확정한 사항은 없다. 이전 `AsyncStatus`의 HTTP 408 설명과 현재 `AsyncResultController`의 동작 차이는 남아 있는 코드 설명의 차이다. 본문은 현재 조회 경로의 PENDING·RECONCILING 변환을 따른다.

## 원천이 없어 뺀 사실과 확인할 담당 역할
| 뺀 사실 | 생략 이유·확인 범위 | 담당 역할 |
|---|---|---|
| LOCAL·REMOTE의 현장 조작 권한 차이, MAINTENANCE·PAUSE의 제조사별 물리 뜻 | enum·화면 분류는 확인했으나 제조사 계약과 상세 생성 의미는 확정하지 못했다. 본문에는 확인한 연결 분류·표시 변환만 썼다. | 설비 모델·프로토콜 담당 |
| CarrierState의 CLAMPED·UNCLAMPED·OPENED·CLOSED·UNKNOWN 물리 뜻 | enum은 확인했지만 조사한 실제 업무·화면 경로에서 이를 결정하는 동작의 뜻을 확인하지 못했다. | 설비 모델 담당 |
| 데모 CREATED·STOPPING의 현행 발생 단계 풀이 | enum과 달리 실제 생성·저장 경로는 RUNNING, 중단은 CANCELED로 직접 기록한다. | 시뮬레이터 담당 |
| 배포 Step PENDING·SKIPPED·RUNNING·ROLLED_BACK의 발생 조건 | enum·front 허용 집합은 있으나 조사한 서버에서 명확한 생성 조건을 확인하지 못했다. ROLLED_BACK은 확인한 대상 상태만 썼다. | 배포 담당 |
| 배포 RECONCILING·FINALIZING·REPAIR_REQUIRED의 서버 발생 조건 | front 허용·표시는 확인했으나 서버 배포 enum·생성 경로에서 발생 조건은 확정하지 못했다. 본문에는 결과 라벨 변환만 썼다. | 배포·조정 담당 |
| 현장의 현재 상태·실제 장애·배포 판본 | 소스만 읽었으며 네트워크·운영 접근을 하지 않았다. | 현장 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실
| 소유 문서 objectKey | 복제하지 않은 내용 |
|---|---|
| 00.rag/user/live-map-status.md | 설비 선택·점 색·필터·집계·갱신·포트 확인 위치와 조작 |
| 00.rag/user/equipment-commands-and-polling.md | 명령 메뉴·조건·시작·종료·재연결·폴링 절차 |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 지연·운영 확인 필요의 재조회·인계·진단 |
| 00.rag/user/deployment-workflow.md | 생성·실행·Hold·View·재실행·이력·삭제·권한 |
| 00.rag/user/deployment-troubleshooting.md | 응답 추적·대기·실패·취소 대상 진단 |
| 00.rag/user/demo-execution.md | 시작·조회·전체 종료·설비별 종료 |
| 00.rag/user/demo-troubleshooting.md | 오류 수 해석·실행 실패·남은 실행 점검 |
| 00.rag/user/lot-carrier-work-and-system-flow.md | Lot·Carrier 업무 보고와 MES·WM 흐름 |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드·문구·발생 조건·조치 전체 표 |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | 조정 phase·state·소유권·장벽·보상의 운영 확인. user 원문에는 admin 경로·열람 지시를 넣지 않았다. |

## 검사 결과

### 최초 작성 검사(보완 전 판본)
- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/state-reference.md`
- 최종 실행 종료코드 0, 실측 wall time 23.976초. 직접 checker 호출은 초기 1회·최종 1회다. 초기 출력도 위반 0건이지만 종료코드를 회수하지 못했으므로 성공 종료의 근거는 최종 실행만 사용했다.
- 최종 결과: 17절·85자식·최대 자식 122자·최대 부모 449자. 대상 위반·CROSS VIOLATION·MASK 출력 0건. 마지막 출력 10줄은 다음과 같다.

```text
    L303-303 문단 31  | 데모 실행을 펼친 설비별 표의 「현재 상태」에 해당한다.
    L307-307 표 행 56  | 값=PENDING | 대상 설비의 뜻=실행에 대상을 등록하고 아직 포트 처리를 시작하지 않은 값이다.
    L308-308 표 행 40  | 값=RUNNING | 대상 설비의 뜻=대상 설비의 포트 처리를 시작했다.
    L309-309 표 행 62  | 값=COMPLETED | 대상 설비의 뜻=한 번 실행하는 모드에서 모든 포트의 처리가 완료 또는 오류로 끝났다.
    L310-310 표 행 44  | 값=CANCELED | 대상 설비의 뜻=진행 중인 대상 설비의 처리를 중단했다.
    L311-311 표 행 60  | 값=FAILED | 대상 설비의 뜻=실행 실패·점유 회수 때 아직 끝나지 않은 대상에도 기록하는 실패값이다.
    L313-313 문단 88  | 실행·중지는 00.rag/user/demo-execution.md, 오류 수 해석은 00.rag/user/demo-troubleshooting.md가 다룬다.
SUMMARY sections=17 children=85 maxChild=122 maxParent=449
CORPUS docs=43/53 sections=625 part={admin=9, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=5, 트러블슈팅=12}
TOTAL files=43 targets=1 violations=0
```

### 최초 작성 사람 검사 판정(보완 전 자기검사)
전 17절을 모두 읽었다. 각 절마다 제목만으로 질문을 식별하는지, 원천과 일치하는지, 독립 답인지, 다른 절·기존 문서의 소유 내용을 복제했는지 대조했다.

| 절 | 제목 | 원천 | 독립 답 | 소유·중복 |
|---|---|---|---|---|
| 설비 연결을 나타내는 controlState | 적합 | 적합 | 적합 | 적합 |
| 설비 연결 의도인 desiredState | 적합 | 적합 | 적합 | 적합 |
| 포트 집계·설비 등록·연결 종료의 eqpState | 적합 | 적합 | 적합 | 적합 |
| Live-Map의 설비 RUN·IDLE·DOWN·ERROR | 적합 | 적합 | 적합 | 적합 |
| 포트 원시 상태 LOAD·UNLOAD·IDLE·DOWN | 적합 | 적합 | 적합 | 적합 |
| Live-Map 포트의 RUN·IDLE·DOWN 변환 | 적합 | 적합 | 적합 | 적합 |
| 비동기 작업 operation의 진행·종결 상태 | 적합 | 적합 | 적합 | 적합 |
| 작업의 RECONCILING과 화면의 확인 지연 | 적합 | 적합 | 적합 | 적합 |
| 배포 전체의 PLANNING·READY·RUNNING | 적합 | 적합 | 적합 | 적합 |
| 배포 전체의 완료·실패·취소 판정 | 적합 | 적합 | 적합 | 적합 |
| 배포 대상 설비의 PENDING·NO_CHANGE·RUNNING·COMPLETED | 적합 | 적합 | 적합 | 적합 |
| 배포 대상·단계의 실패와 되돌림 상태 | 적합 | 적합 | 적합 | 적합 |
| 배포의 Step 상태 WAITING_REPLY와 COMPLETED | 적합 | 적합 | 적합 | 적합 |
| 배포 대상의 결과 라벨과 상세 상태 | 적합 | 적합 | 적합 | 적합 |
| 해석할 수 없는 작업·배포 상태 | 적합 | 적합 | 적합 | 적합 |
| 데모 실행 전체의 RUNNING·TERMINAL·CANCELED·FAILED | 적합 | 적합 | 적합 | 적합 |
| 데모 대상 설비의 PENDING·RUNNING·COMPLETED·CANCELED·FAILED | 적합 | 적합 | 적합 | 적합 |

- 제목은 모두 유일하며 식별자만 바꾼 불필요한 반복 틀·금지 제목 형태를 발견하지 않았다.
- 85개 본문 항목을 위 표로 대조했다. 원시 상태·표시 상태·집계 라벨의 주체를 구별했다. 동일 자식 본문·정보 없는 틀 문장·이름 토막 번역·잘린 문장·오답 인용의 미해소 지적은 없다.
- 최종 dump 전체에 MASK가 없고, 필요한 코드·상태가 보존됐다. 비밀값·개인 연락처·현장 접속 정보를 넣지 않았다.
- user 원문에 admin objectKey·링크·내부 관리 조치는 없다. 상태 참조이므로 화면 조작·API 실행 절차·권한 표는 추가하지 않았다.
- 다른 문서에서 수정이 필요한 결함을 이번 범위에서 확정하지 않았다.
- 검증 한계: 제품 build·단위/통합 시험·UI 구동·현장 상태·검색 반영·정답률은 검증하지 않았다. 업로드는 범위 밖이다.
- Actor·사용량: 이 작성 세션이 직접 수행했으며 외부 AI·하위 Agent 호출은 0회다. provider 토큰 사용량·비용·전체 작업 소요시간의 별도 측정값은 받지 못했다.
- 기록 생성 첫 시도는 PowerShell의 stdin 문자 인코딩 때문에 한국어 원천 키를 찾지 못해 IndexError로 중단됐다. 파일을 쓰기 전 실패했다. UTF-8 stdin을 명시하고 원천 수·행 수·인덱스를 검사한 뒤 이 기록을 생성했다. 검색 원문은 이 실패로 바뀌지 않았다.

## 임시 자원 정리
초기·최종 검사 로그 2개는 필요한 출력·해시·판정을 이 기록에 보존한 뒤 정리했다.

- 정확한 대상은 지정 작업 폴더 `work/check-initial.log`(14,197바이트)와 `work/check-final.log`(14,453바이트)였다. 둘 다 이 작성 세션이 만든 일회성 검사 출력이다. 지정 Temp 경로 안의 절대경로를 확인했다.
- 대상 속성은 Archive였으며 ReadOnly·Hidden·System·ReparsePoint가 없었다. work부터 허가된 Temp 루트 아래까지 상위 디렉터리는 Directory이며 재분석 지점이 없었다. 파일의 배타적 읽기 열기와 닫기가 둘 다 성공했다.
- `Get-CimInstance Win32_Process`의 runner 명령행 조회는 HRESULT 0x80041003, 「액세스가 거부되었습니다.」로 실패했다. 이 조회 실패를 삭제 거부로 분류하거나 권한을 바꾸어 재시도하지 않았다. 명령행을 통한 전 프로세스 참조 대조는 미확인이다.
- 정상 `Get-Process` 조회에서 Python 프로세스는 없었고 Java 2개는 모두 2026-11-16 시작이었다. 이 세션 checker는 최종 명령 exit 0으로 종료됐으며 초기 출력도 종결 요약까지 저장됐다. 다른 실행 중 프로세스는 종료하지 않았다. 위 수명·배타적 파일 열기 결과로 이 로그들의 활성 기록·파일 잠금이 남지 않았음을 확인했다.
- PowerShell 5.1.26100.9444에서 두 파일 각각 `Remove-Item -LiteralPath <검증한 절대경로> -ErrorAction Stop`을 실행했다. Force·Recurse를 쓰지 않았다. 명령 exit 0, 두 파일의 `Test-Path`는 False, work의 남은 항목 수는 0이었다. 기존 work 디렉터리는 보존했다.


## 메인 세션 검증 결함 1건의 보완 — 2026-11-19

- 결함: 최초 작성의 HTTP 설명은 브라우저가 인정하는 세 최종 값만 200에 대응시켜 UNKNOWN의 서버 응답을 빠뜨렸다. 서버의 terminal 판정을 호출한 View까지 대조하지 않은 것이 작성 원인이다. 최초 자기검사에서 이를 발견하지 못했고, 메인 검증 지적 후 아래와 같이 보완했다.
- 직접 원천을 다시 읽었다. Backend HEAD는 985d17c604dd, Front HEAD는 9fe69a75dcd3이며 해당 파일에 수정 출력은 없었다. UiOperationQueryService L68~80은 원장 상태를 그대로 전달한다. UiOperationStatusView L50~52는 PENDING 이외를 terminal로 판정하고 AsyncResultController L92~107이 그 판정으로 HTTP 200/202를 선택한다. Front operation-status.ts L67~73·L124~142는 PASS·FAIL·REPAIR_REQUIRED만 최종으로 인정해 UNKNOWN에서는 조회를 계속한다.
- 수정 범위: HTTP 대응 문단 1개와 미해석 상태 표 행 1개, 연결 원천 메타 4줄만 수정했다. 이외 원문은 보존했으며 절 분할은 필요하지 않았다. 재구성한 보완 전 바이트 SHA-256이 fee4c9c16ec32c8cd4e5bce903d8c2ce894ca01dc97e92a75794cbcdff3537da와 일치함을 확인한 diff로 범위를 검사했다.
- HTTP 일반 대응은 진행·종결 상태 절에 짧게 두고, UNKNOWN의 HTTP 200과 브라우저 판정의 차이는 미해석 상태 절에만 뒀다. 위 사실 대조표는 85개 행의 최종 줄 위치와 변경한 두 행의 문장·원천을 갱신했다. 기존 대조의 다른 83개 본문 항목은 동일함을 비교했다.
- 최초 자기검사·검사 로그는 위에서 보완 전 이력으로 보존했다. 이번 최종 원문은 아래 검사와 부분 재대조가 적용된다. 제품 코드·다른 문서·Git 인덱스는 수정하지 않았다.

### 보완 후 최종 검사

- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/state-reference.md`
- 보완 후 checker 호출 1회, 종료코드 0, 실측 18.910초다. 최종본은 317줄·17절·85자식, 최대 자식 142자·최대 부모 449자다. 위반·CROSS VIOLATION·MASK 출력은 모두 0건이다.
- 사람 검사: 변경한 두 절의 제목·본문·원천·독립 답·사실 중복을 다시 대조했다. 서버의 HTTP 상태와 브라우저의 결과 확정 기준을 구별했다. 미해소 지적은 없다. 전 원문의 기존 구조·내용이 나머지에서는 동일하다는 diff 대조와 기계 검사를 함께 확인했다.
- 최종 검사 마지막 출력 10줄:

```text
    L307-307 문단 31  | 데모 실행을 펼친 설비별 표의 「현재 상태」에 해당한다.
    L311-311 표 행 56  | 값=PENDING | 대상 설비의 뜻=실행에 대상을 등록하고 아직 포트 처리를 시작하지 않은 값이다.
    L312-312 표 행 40  | 값=RUNNING | 대상 설비의 뜻=대상 설비의 포트 처리를 시작했다.
    L313-313 표 행 62  | 값=COMPLETED | 대상 설비의 뜻=한 번 실행하는 모드에서 모든 포트의 처리가 완료 또는 오류로 끝났다.
    L314-314 표 행 44  | 값=CANCELED | 대상 설비의 뜻=진행 중인 대상 설비의 처리를 중단했다.
    L315-315 표 행 60  | 값=FAILED | 대상 설비의 뜻=실행 실패·점유 회수 때 아직 끝나지 않은 대상에도 기록하는 실패값이다.
    L317-317 문단 88  | 실행·중지는 00.rag/user/demo-execution.md, 오류 수 해석은 00.rag/user/demo-troubleshooting.md가 다룬다.
SUMMARY sections=17 children=85 maxChild=142 maxParent=449
CORPUS docs=44/53 sections=664 part={admin=10, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=6, 트러블슈팅=12}
TOTAL files=44 targets=1 violations=0
```

### 보완 검사 임시 자원

필요한 출력은 위에 보존했다. 이번에 만든 일회성 `work/check-repair-final.log`(14,476바이트)는 지정 Temp 안의 정확한 절대경로와 소유·용도를 확인했다. 상위 7개 디렉터리에 재분석 지점이 없고 파일 속성은 Archive로 보호 속성이 없었다. 이 세션 checker의 exit 0, Python 프로세스 0개 및 배타적 읽기 성공을 확인한 뒤 `Remove-Item -LiteralPath <검증한 절대경로> -ErrorAction Stop`으로 정상 삭제했다. 명령 exit 0, 파일 `Test-Path=False`, work 잔여 항목 0개다. 다른 실행 중 프로세스는 종료하지 않았다.

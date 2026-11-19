# TC 설비·포트·배포·작업 상태값
```rag-meta
유형: 참조
적용: nori-tc-common 6fe71f6b2da6·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-ui-backend 985d17c604dd·nori-tc-ui-front 9fe69a75dcd3·nori-tc-simulator 5e9b02e3ae16 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 설비 연결을 나타내는 controlState
```rag-meta
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/common/eqp/ControlState.java @ 6fe71f6b2da6 ; ControlState L3~18
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 연결·종료 기록 L93~145
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; 채널 연결·끊김 기록 L163~215
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; 연결 판정 L12~15
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; isConnectedControlState L47~48
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; createInitialStateCommand L707~713
```

설비의 controlState는 실제 연결 상태를 나타낸다.

| 값 | 코드에서 확인한 의미 |
|---|---|
| LOCAL·REMOTE | 설비 화면이 연결됨으로 판정하는 값이다. Gateway는 시작 확정 또는 채널 연결 기록에 REMOTE를 쓴다. |
| OFFLINE | Gateway가 종료 또는 비자발적 연결 끊김을 기록할 때 쓰는 값이다. |
| DOWN·DISCONNECTED | 화면에서 연결 끊김으로 분류한다. 새 설비 생성 시 SECS는 DOWN, 그 밖의 통신 방식은 DISCONNECTED로 기록한다. |

연결 문제의 확인 경로는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`가 다룬다.

## 설비 연결 의도인 desiredState
```rag-meta
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/common/eqp/DesiredState.java @ 6fe71f6b2da6 ; DesiredState L3~16
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forStart·forEnd L93~145
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forOffline L187~215
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 신규 연결 의도 L715~722
```

desiredState는 설비 연결을 유지하려는 의도다. 현재 연결 여부는 controlState로 구별한다.

| 값 | 의미와 기록 조건 |
|---|---|
| STARTED | 시작 처리로 확정한 연결 의도다. 비자발적으로 연결이 끊겨도 현재 의도를 보존한다. |
| ENDED | 종료 처리로 연결 의도를 해제한 값이며, 아직 시작하지 않은 새 설비의 기본값이다. |

명령별 사용 절차는 `00.rag/user/equipment-commands-and-polling.md`가 다룬다.

## 포트 집계·설비 등록·연결 종료의 eqpState
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpStateTransitionSupport.java @ 7db24f7466bd ; 집계 조건·상태 저장 L73~104
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpStateTransitionSupport.java @ 7db24f7466bd ; resolveTargetEqpState L116~125
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forStart·forEnd L93~145
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/port/EquipmentStatePersistenceCommand.java @ d3636ab86763 ; forOffline L195~215
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 초기 설비 상태 L707~713
```

eqpState는 설비 상태다. Business의 포트 집계는 controlState가 LOCAL 또는 REMOTE일 때 수행한다.

| 값 | 확인한 생성 조건 |
|---|---|
| RUN | 포트 집계에 LOAD 또는 UNLOAD인 포트가 하나 이상 있다. |
| IDLE | 포트 집계에 LOAD·UNLOAD인 포트가 없다. Gateway의 시작 확정 때도 이 값을 기록한다. |
| DOWN | SECS 설비의 생성 초기값이다. Gateway가 종료 또는 비자발적 연결 끊김을 기록할 때도 설정한다. |
| SERVICE_UNAVAILABLE | SECS 이외 설비를 새로 만들 때 기록하는 초기값이다. |

## Live-Map의 설비 RUN·IDLE·DOWN·ERROR
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; resolveEquipmentStatus L178~195
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; 상태 응답 L65~87
```

Live-Map 설비 상태는 controlState와 eqpState를 조합한 표시값이다.

| 표시값 | 판정 조건 |
|---|---|
| RUN | controlState가 LOCAL·REMOTE이고 eqpState가 RUN이다. |
| IDLE | controlState가 LOCAL·REMOTE이고 eqpState가 IDLE이다. |
| DOWN | controlState가 OFFLINE·DOWN·DISCONNECTED이거나, LOCAL·REMOTE에서 eqpState가 DOWN·SERVICE_UNAVAILABLE이다. |
| ERROR | LOCAL·REMOTE에서 eqpState가 MAINTENANCE·PAUSE 또는 그 밖의 값이거나, controlState가 위 분류에 없다. |

화면에서 설비를 찾고 표시를 읽는 방법은 `00.rag/user/live-map-status.md`가 다룬다.

## 포트 원시 상태 LOAD·UNLOAD·IDLE·DOWN
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierLoadedTcAction.java @ 7db24f7466bd ; handleCarrierLoaded L28~36
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/UnloadRequestTcAction.java @ 7db24f7466bd ; handleUnloadRequest L36~50
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierUnloadRequestTcAction.java @ 7db24f7466bd ; handleCarrierUnloadRequest L49~63
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 전이값·캐리어 해제 L24~26
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; clearCarrierIdWhenIdle L129~132
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentStatePersistenceService.java @ d3636ab86763 ; 종료 후 포트 투영 L208~225
```

portState는 포트별 상태다. 다음은 해당 액션이 실행되는 경로에서 확인한 기록 조건이다.

| 값 | 의미와 발생 조건 |
|---|---|
| LOAD | CARRIER_LOADED 액션이 지정 포트에 기록하는 상태다. |
| UNLOAD | Lot ID가 있는 UNLOAD_REQUEST 액션이 지정 포트에 기록하는 상태다. |
| IDLE | CARRIER_UNLOAD_REQUEST에서 Lot ID를 확인한 뒤 기록하는 값이다. IDLE 전이는 포트의 Carrier ID도 비운다. |
| DOWN | Gateway가 설비를 OFFLINE으로 기록할 때 모든 포트에 반영하는 값이다. |

## Live-Map 포트의 RUN·IDLE·DOWN 변환
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toPortView·resolvePortStatus L197~220
```

Live-Map의 포트 상태는 원래 portState를 다음처럼 묶어 표시한다. 설비 상태와 포트 상태는 각각 계산한다.

| 포트 표시값 | 원래 portState |
|---|---|
| RUN | LOAD·LOADED·UNLOAD |
| IDLE | IDLE·EMPTY·READY_TO_LOAD |
| DOWN | DOWN·OUT_OF_SERVICE·UNKNOWN |
| IDLE | 위 목록에 없는 값 또는 빈 값 |

표시 위치와 캐리어 확인 방법은 `00.rag/user/live-map-status.md`가 다룬다.

## 비동기 작업 operation의 진행·종결 상태
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UiOperationState L9~25
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; getResult L85~107
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationStatusView.java @ 985d17c604dd ; terminal L50~52
원천: nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; 최종 상태 판정 L67~73
```

비동기 작업 조회의 status는 요청한 변경 작업의 결과다.

| 값 | 의미 |
|---|---|
| PENDING | 진행 중이거나 복구 중이며 최종 결과가 확정되지 않았다. |
| PASS | 변경 결과 저장과 사용 차단 해제가 끝난 성공이다. |
| FAIL | 확정된 실패 또는 되돌림 완료다. |
| REPAIR_REQUIRED | 보상 완료를 입증하지 못해 수동 복구가 필요한 상태다. |

작업을 찾은 조회 응답은 status가 PENDING이면 HTTP 202, 그 밖의 값이면 HTTP 200이다.

## 작업의 RECONCILING과 화면의 확인 지연
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationQueryService.java @ 985d17c604dd ; displayPhase·이전 결과 변환 L83~118
원천: nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; pollOperationUntilTerminal L124~142
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; DELAYED 표시 L289~297
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-operation-result-presentation.ts @ 9fe69a75dcd3 ; delayed L43~48
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; resultDelayed L1116~1116
```

| 구분 | 뜻과 발생 조건 |
|---|---|
| phase의 RECONCILING | 최종 결과가 없는 작업의 갱신 시각이 관찰 기한을 넘으면 조회에 표시한다. 이전 비동기 결과의 TIMEOUT도 PENDING·RECONCILING으로 변환한다. |
| 화면의 「확인 지연」 | 브라우저가 정해진 조회 횟수 안에 최종 상태를 받지 못해 DELAYED로 반환한 결과 표시다. |

재조회와 운영 인계는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`가 다룬다.

## 배포 전체의 PLANNING·READY·RUNNING
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; 생성 초기 상태 L80~88
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; finishDeployCreation L146~193
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; startDeploy L531~563
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployServiceCommandSupport.java @ 985d17c604dd ; Hold 후 READY L103~116
```

배포 전체 상태는 배포 번호 하나에 대한 진행 상태다.

| 값 | 의미와 발생 조건 |
|---|---|
| PLANNING | 배포 기록을 만든 초기 상태다. 실행 가능한 대상과 변경 전 자료를 준비하는 동안 사용한다. |
| READY | 실행할 배포의 생성이 끝난 대기 상태다. Hold 뒤 남은 대상이 있어 다시 대기할 때도 사용한다. |
| RUNNING | 배포 실행을 접수하고 대상을 실행하는 상태다. |

실행과 Hold 조작은 `00.rag/user/deployment-workflow.md`가 다룬다.

## 배포 전체의 완료·실패·취소 판정
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployServiceCommandSupport.java @ 985d17c604dd ; refreshDeployStatus L93~138
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/deploy/DeployTargetCounts.java @ 985d17c604dd ; 실패·성공 집계 L23~29
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 변경 없음·생성 실패 L150~174
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; markDeployCreationFailed L197~209
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; markDeployCanceled L764~772
```

진행·대기 대상이 없을 때 실패 집계는 FAILED·ROLLED_BACK·COMPENSATION_FAILED, 성공 집계는 COMPLETED·NO_CHANGE를 포함한다.

| 배포 전체 값 | 판정 |
|---|---|
| COMPLETED | 실패 집계가 없는 종료다. 실행할 변경이 전혀 없어 생성 직후 끝나는 경우도 포함한다. |
| PARTIAL_FAILED | 실패 집계와 성공 집계가 모두 있다. |
| FAILED | 실패 집계만 있거나 배포 생성 완료에 실패했다. |
| CANCELED | 배포 취소 처리가 확정됐다. |

## 배포 대상 설비의 PENDING·NO_CHANGE·RUNNING·COMPLETED
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; insertTargetState L191~200
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 응답 대기·대상 완료 L190~237
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; markTargetReadyForResume L811~841
```

배포 대상 상태는 배포 안의 설비 한 대에 대한 값이다.

| 값 | 의미 |
|---|---|
| PENDING | 변경할 대상의 초기 대기 상태다. 완료 단계를 보존한 재개 대기에도 사용한다. |
| NO_CHANGE | 해당 대상에 실행할 변경이 없어 처음부터 종료 처리한 상태다. |
| RUNNING | 대상의 현재 단계를 실행하며 응답을 기다리는 상태다. |
| COMPLETED | 대상 설비의 배포 완료를 기록한 상태다. |

## 배포 대상·단계의 실패와 되돌림 상태
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployExecutor.java @ 985d17c604dd ; handleTargetFailure L289~312
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployRollbackCoordinator.java @ 985d17c604dd ; 보상 실행·확정 L128~203
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 되돌림·실패 기록 L270~403
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; cancelTargets L787~805
```

| 값 | 주체와 뜻 |
|---|---|
| FAILED | 대상 또는 단계의 실행 실패를 기록한 값이다. |
| ROLLING_BACK | 대상과 현재 단계가 앞서 적용한 변경을 되돌리는 보상 처리 중이다. |
| ROLLED_BACK | 대상의 보상 처리가 끝났다. 이때 현재 단계 정보는 비운다. |
| COMPENSATION_FAILED | 대상과 현재 단계의 보상 자료 해석·명령 발행·응답 확인 등에 실패했다. |
| CANCELED | 배포 취소로 PENDING·RUNNING·ROLLING_BACK인 대상을 중단하며 대상과 단계에 기록한다. |

실패 확인과 인계는 `00.rag/user/deployment-troubleshooting.md`가 다룬다.

## 배포의 Step 상태 WAITING_REPLY와 COMPLETED
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 성공 단계 보존 L204~227
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; publishStep L246~258
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 단계 상태 기록 L190~208
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 단계 완료 보존 L811~841
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 단계 표시 L280~293
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; colStepStatus L299~299
```

「Deploy View」의 「Step 상태」는 대상 설비의 현재 단계에 대한 값이다.

| 값 | 뜻 |
|---|---|
| WAITING_REPLY | 단계의 응답 추적과 대기 상태를 기록한 값이다. 명령 발행 직전에 설정한다. |
| COMPLETED | 성공 응답을 받은 단계의 완료 지점을 보존한 값이다. 대상 전체의 최종 완료는 별도로 확정한다. |

대상 상태가 PENDING이어도 마지막으로 마친 단계의 「Step 상태」는 COMPLETED일 수 있다.

## 배포 대상의 결과 라벨과 상세 상태
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; resolveTargetResultLabel L42~67
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 결과·상세 상태 열 L249~266
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 결과·상세 상태 L296~297
```

「Deploy View」에서 「결과」는 대상 상태를 묶은 라벨이고, 「상세 상태」는 개별 상태값이다.

| 결과 라벨 | 대상의 상세 상태 |
|---|---|
| PASS | COMPLETED·NO_CHANGE |
| FAIL | FAILED |
| ROLLBACK | ROLLING_BACK·ROLLED_BACK |
| REPAIR_REQUIRED | COMPENSATION_FAILED·REPAIR_REQUIRED |

PENDING·RUNNING·RECONCILING·FINALIZING·CANCELED는 「결과」에도 같은 문자열로 표시한다.

## 해석할 수 없는 작업·배포 상태
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationState.java @ 985d17c604dd ; UNKNOWN L23~24
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/RedisUiOperationLedgerAdapter.java @ 985d17c604dd ; toState L281~289
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationQueryService.java @ 985d17c604dd ; toStatusView L68~80
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationStatusView.java @ 985d17c604dd ; terminal L50~52
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; HTTP 응답 L92~107
원천: nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; 미지원 상태·조회 L67~73
원천: nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; 조회 종료 L124~142
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 대상 상태 변환 L162~162
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 배포 상태 변환 L217~217
원천: nori-tc-ui-front/src/features/deploy/lib/deploy-monitor-format.ts @ 9fe69a75dcd3 ; resolveDeployStatusLabel L11~12
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; deploy.status.unknown L1167~1167
```

| 주체 | 확인한 처리 |
|---|---|
| 비동기 작업 | UNKNOWN은 서버가 원장 값을 해석하지 못한 값이다. 서버는 UNKNOWN에도 HTTP 200을 반환한다. 브라우저는 PASS·FAIL·REPAIR_REQUIRED만 최종 결과로 인정하고, 다른 값은 조회를 이어간다. |
| 배포 전체·대상 | 화면이 지원하지 않는 응답 상태는 표시용 UNKNOWN_SERVER_STATUS로 바꾸고 「알 수 없는 서버 상태」로 표시한다. |

## 데모 실행 전체의 RUNNING·TERMINAL·CANCELED·FAILED
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; createRun L125~153
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; stopRun L263~280
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; resolveTerminalRunState L613~633
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; reclaim L155~168
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 실행 상태 표시 L918~925
```

「시뮬레이션 진행 현황」의 실행 번호 하나에 대한 Run 상태다.

| 값 | 실행 전체의 뜻 |
|---|---|
| RUNNING | 실행을 만들고 설비별 처리를 진행하는 상태다. |
| TERMINAL | 모든 대상 설비가 COMPLETED로 끝나 실행 전체를 종결한 값이다. |
| CANCELED | 전체 종료 요청으로 중단했거나, 모든 대상이 끝났을 때 COMPLETED가 아닌 대상이 포함된 값이다. |
| FAILED | 시작 중 워커 실행 요청에 실패했거나, 갱신이 오래 끊긴 실행의 점유를 회수한 값이다. |

## 데모 대상 설비의 PENDING·RUNNING·COMPLETED·CANCELED·FAILED
```rag-meta
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 초기·회수 상태 L64~73
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 실패 전파 L155~168
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 종료 상태 전파 L99~113
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/domain/TargetAggregate.java @ 5e9b02e3ae16 ; beginPortLot L99~108
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/RunOrchestrationService.java @ 5e9b02e3ae16 ; 설비 완료·중단 L218~275
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 설비 상태·완료 오류 수 L597~609
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; colStatus L438~438
```

데모 실행을 펼친 설비별 표의 「현재 상태」에 해당한다.

| 값 | 대상 설비의 뜻 |
|---|---|
| PENDING | 실행에 대상을 등록하고 아직 포트 처리를 시작하지 않은 값이다. |
| RUNNING | 대상 설비의 포트 처리를 시작했다. |
| COMPLETED | 한 번 실행하는 모드에서 모든 포트의 처리가 완료 또는 오류로 끝났다. |
| CANCELED | 진행 중인 대상 설비의 처리를 중단했다. |
| FAILED | 실행 실패·점유 회수 때 아직 끝나지 않은 대상에도 기록하는 실패값이다. |

실행·중지는 `00.rag/user/demo-execution.md`, 오류 수 해석은 `00.rag/user/demo-troubleshooting.md`가 다룬다.

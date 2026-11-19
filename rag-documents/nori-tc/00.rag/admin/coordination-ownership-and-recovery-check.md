# TC 조정 작업·소유권·복구 필요 확인
```rag-meta
유형: 운영
적용: nori-tc-exec f1d5e0a8e727·nori-tc-gateway d3636ab86763·nori-tc-ui-backend 985d17c604dd·nori-tc-ui-front 9fe69a75dcd3·nori-tc-common 6fe71f6b2da6 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/tc-components-and-terms.md
```

## 소유권과 조정 작업을 조회하려면 어떤 권한이 필요한가
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 조회 기본 연결 L124~130
원천: nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 기본 연결 L41~56
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185
```

기본 권한 데이터 기준으로 설비·Gateway 소유권 조회는 `EQP_READ`, 조정 작업 관리 조회는 `COORDINATION_ADMIN_READ`가 필요하다. 조정 조회 권한은 V8에서 ADMIN 그룹에 연결한다.

관리자 계정도 실제 API 권한 연결이 없으면 차단된다. 조회가 거부되면 계정 담당자는 요청 경로·HTTP 메서드·보유 권한·권한 목록 동기화 상태를 확인한다.

## 설비 화면에서 담당 Pod를 대조하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Gateway 선택·조회 결과 전달 L1135~1168
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색 필터·점유 표시·Pod 표시 L286~338
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Gateway 문구 L39~40
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 미점유·대기 표시 L594~598
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 설비 메뉴 L1092~1098
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Pod 선택 라벨 L16~23
```

「설비」 화면에서 「Gateway」를 선택하고 왼쪽 검색어를 비운다. 검색 중에는 설비가 없는 그룹과 대기 Pod가 숨겨질 수 있다.

그룹 아래의 Pod 식별 문구와 「미점유」·「spare」 표시를 확인한다. 클러스터 조회 권한이 있는 운영자는 다음 Pod 목록과 대조한다.

```sh
kubectl -n tc get pods -l app=tc-gateway -o wide
```

화면에 담당 Pod 문구가 없으면 소유권 조회 API와 런타임 인스턴스 API를 함께 확인한다.

## Gateway 소유권 API로 어느 그룹을 누가 맡는지 읽기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd ; 그룹별 조회 L38~47
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd ; Gateway 인스턴스 조회 L42~46
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 화면의 조회 요청 L172~216
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자와 Pod 대조 L55~97
```

인증된 운영자는 `GET /api/eqp/gateway-shard-leases`의 `data`에서 `groupId`·`ownedPartitions`·`ownerInstanceId`를 읽는다. 소유자가 없으면 `ownerInstanceId`는 null이다.

`GET /api/eqp/gateway-instances`의 `sessionId`와 소유자 식별자가 일치할 때 `podName`을 연결한다. 소유자 문자열을 잘라 Pod 이름을 추정하지 않는다. 화면도 이 일치 관계로 담당 Pod를 표시한다.

## 조정 lease와 이전 lease가 함께 남아 있을 때 확인 순서
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapter.java @ 985d17c604dd ; 조정 우선·이전 형식 보완 L62~118
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapterTest.java @ 985d17c604dd ; 우선순위 기대값 L96~140
```

조정 기능을 켠 Backend의 소유권 조회는 조정 lease를 먼저 읽는다. 양수 epoch와 비어 있지 않은 sessionId가 없으면 그 그룹에 한해 이전 형식의 lease를 확인한다.

조정 lease의 소유자가 있으면 이전 키의 잔여 값이 소유자를 덮지 않는다. Redis에서 조정 키 하나가 없다는 관측만으로 그룹 전체를 미점유라고 판정하지 않고 소유권 API 결과와 대조한다.

## Redis에서 Gateway 소유 세션과 epoch를 직접 읽기
```rag-meta
원천: nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; Gateway lease 키 L182~189
원천: nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 세션·epoch·만료 L25~31
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 조회 대상·인증 조건 L1860~1932
```

Redis 조회 권한으로 `REDIS_NS`·`REDIS_WORKLOAD`를 설치 대상에, `GROUP`을 조회할 그룹 ID에 지정한다. 아래는 논리 DB 0에서 인증 없이 조회 가능한 환경의 명령이다.

```sh
kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 HMGET "tc:coord:v1:gateway:shard:$GROUP:lease" sessionId epoch
```

두 반환값은 해당 조정 lease의 소유 세션과 epoch다. 인증이 필요하면 Redis 담당자의 인증 경로로 같은 항목을 조회한다. 자격값을 명령 인자에 넣지 않는다.

## Gateway 소유권 재배치 후 무엇을 확인하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재배치 명령 L1846~1854
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득·담당 파티션·재기동 L206~254
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 준비 상태 판정 L33~57
```

중단 영향을 확인하고 재배치할 Gateway Pod를 `POD`에 지정한다. 운영 변경 권한으로 `nori-tc-exec` 루트에서 실행한다.

```sh
kubectl -n tc delete pod "$POD"
sh ./deploy/scripts/verify-gateway-rollout.sh tc
```

검증 성공 뒤 소유권 API에서 모든 설정 그룹의 소유자와 담당 파티션을 다시 확인한다. 대기 인스턴스가 운용 중 빈 슬롯을 얻으면 통신부를 시작하기 위해 자체 재기동하므로 Pod 이름을 고정해 예상하지 않는다.

검증 실패나 미점유 그룹이 남으면 재배치 완료로 처리하지 않는다. replica·그룹 확장은 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다.

## 조정 초기 값을 발급하기 전에 맞출 조건
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 발급 대상·초기화 제한 L1934~1939
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전환 배포 선행 조건 L2624~2644
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 발급과 접속 입력 L4~32
```

운영 중 조정 기능을 처음 켜는 전환은 UI 변경 요청 중단·진행 중 작업 0·구 Pod 전체 종료·스키마 적용 뒤에 초기 값을 발급한다. Redis 영속 구성과 DB 백업도 먼저 준비한다.

전환 배포와 되돌림은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`, 진행 중 건수 조회는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다.

기존 원장을 버리고 초기화하는 작업은 별도 승인 대상이다. 기존 cluster Epoch 삭제를 일상 복구 절차에 섞지 않는다.

## coord-epoch.sh로 발급한 결과 판정하기
```rag-meta
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 접속·인자 L30~55
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조건부 발급·결과 L72~98
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전환 발급 순서 L2627~2631
```

구 Pod 종료·스키마 적용 뒤 SSH·Redis 작업 권한이 있는 운영자는 `K8S_SSH_KEY`·`K8S_NODE`·`REDIS_NS`·`REDIS_DEPLOY`를 설치 대상으로 지정한다. 저장소 기본 접속값을 그대로 사용하지 않는다.

`nori-tc-exec` 루트에서 `bash ./coord-epoch.sh issue 0`을 실행한다. `[ISSUED]`는 새 값 저장, `[SKIP]`은 기존 값 유지, `[ISSUED-BY-OTHER]`는 동시 발급자가 저장한 값 유지를 나타낸다.

스크립트는 Redis 인증 입력을 받지 않는다. 인증이 필요한 Redis에서는 직접 인증된 관리 경로를 사용한다. 실행 오류를 발급 성공으로 처리하지 않는다.

## SSH 경유 없이 Redis 초기 값을 발급하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 저장 후 값 대조 L85~92
```

구 Pod 종료·스키마 적용을 마친 운영자는 `REDIS_NS`·`REDIS_WORKLOAD`를 설치 대상으로 지정한다. 아래 명령은 Redis 인증이 필요 없는 환경에서만 실행한다.

```sh
kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- sh -c 'redis-cli -n 0 SET tc:coord:v1:cluster:epoch "$(cat /proc/sys/kernel/random/uuid)" NX'
```

Pod 안에서 UUID를 만들도록 홑따옴표를 유지한다. `NX`는 기존 값을 보존한다. 새 값 저장은 `OK`로 표시한다. 발급 뒤 다음 조회로 저장값을 확인한다. 오류·빈 값이면 앱 기동을 멈춘다.

```sh
kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 GET tc:coord:v1:cluster:epoch
```

## 미종결 작업 ID를 찾고 복구 대기 누락을 피하기
```rag-meta
원천: nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 미종결 인덱스 L90~96
원천: nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 종결 상태 인덱스 제거 L47~65
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정·로그 L665~688
```

Redis 조회 권한으로 설치 대상의 `REDIS_NS`·`REDIS_WORKLOAD`를 지정한다. 인증 없이 조회 가능한 환경에서 미종결 작업 ID 앞 20개를 읽는다.

```sh
kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 ZRANGE tc:coord:v1:operations:pending 0 19
```

`REPAIR_REQUIRED`로 확정된 작업은 이 인덱스에서 제거된다. 인덱스가 비어 있어도 복구 대기가 없다고 판정하지 않는다. 각 Backend Pod의 `UI_OPERATION_REPAIR_REQUIRED` 로그에 남은 `operationId`도 관리 API로 조회한다.

## 조정 작업 관리 API에서 결과를 읽기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 읽기 전용 관리 조회 L28~41
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 결과 상태 L76~95
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 응답 필드 L33~52
```

`COORDINATION_ADMIN_READ`가 있는 인증된 운영자는 대상 ID로 `GET /api/coordination/operations/{operationId}`를 요청한다. ID에는 실제 작업 ID를 넣는다.

HTTP 200은 작업이 존재한다는 뜻이며 처리 성공 판정은 `data.status`로 한다. `eqpId`·`eventType`으로 대상을 대조하고 `status`·`phase`·`repairReason`·`errorCode`·`errorMsg`를 함께 기록한다.

이 API는 읽기 전용이다. 강제 성공 처리나 장벽 개방을 요청하는 쓰기 기능은 제공하지 않는다.

## 작업이 없다는 응답과 조정 조회 불가를 구별하기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 비활성·미존재 분기 L80~94
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminControllerTest.java @ 985d17c604dd ; 오류 응답 기대값 L89~123
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 조정 조회 예외 응답 L154~162
```

조정 작업 관리 조회의 HTTP 상태와 `errorCode`를 함께 확인한다.

| 응답 | 확인과 조치 |
|---|---|
| 404·OPERATION_NOT_FOUND | 요청한 작업이 없다. 작업 ID와 조회 대상을 대조한다. |
| 503·COORDINATION_DISABLED | 조회 대상 Backend가 조정 원장을 사용하지 않는 구성이다. 배포 설정을 확인한다. |
| 503·COORDINATION_UNAVAILABLE | 조정 원장에 접근할 수 없다. 작업이 사라졌다고 판정하지 않고 Redis 접근 장애를 조사한다. |

조회 불가 상태에서는 작업 완료나 복구 성공을 확정하지 않는다.

## 장벽·참여자 적용·DB 확정을 함께 대조하기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 리소스·참여 증적 L64~102
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAdminQueryService.java @ 985d17c604dd ; 조회 시점 한계 L24~26
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; DB 확정 뒤 장벽 개방 L534~564
```

관리 응답의 `resources`에서 `openRevision`이 `committedRevision`보다 작으면 장벽이 닫힌 상태다. `blockedByOperationId`·`activeOperationId`로 관련 작업을 확인한다.

`participantProofs`의 `preparedRevision`만 있고 `appliedRevision`이 없으면 그 참여자는 준비 증적만 있다. Gateway 참여자의 `ownerEpoch`, 작업의 `desiredRevision`·`dbFinalized`도 함께 대조한다.

관리 조회는 여러 키를 순서대로 읽으므로 한 시점의 원자적 스냅샷이 아니다. 진행 중 값이 어긋나면 다시 조회하고 시각을 함께 남긴다.

## Backend replica 중 누가 복구 작업을 진행하는지 확인하기
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 미종결 발견·진행 L54~74
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 세션·lease 검사와 반납 L135~163
원천: nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 작업별 실행 lease L202~209
원천: nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 정보와 만료 L25~31
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 대상·인증 조건 L1860~1932
```

Redis 조회 권한으로 `REDIS_NS`·`REDIS_WORKLOAD`를 설치 대상으로, `OP`를 작업 ID로 지정한다. Backend는 실행 lease를 획득한 세션에서 미종결 작업을 진행한다. 아래는 논리 DB 0의 비인증 조회 예다.

```sh
kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 HMGET "tc:coord:v1:executor:$OP:lease" sessionId epoch
```

인증 환경은 Redis 담당자의 인증 경로를 사용한다. 실행 시도가 끝나면 lease를 반납하므로 조회 순간 키가 없다는 사실만으로 작업 유실을 판정하지 않는다. 관리 API의 작업 상태·갱신 시각도 함께 본다.

## 복구 필요 작업의 자동 보상 발급 여부 확인하기
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 대상 사유 L48~59
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; 중첩 금지·실패 처리 L70~94
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; 발급 로그 L142~144
```

조정 기능에서 `REPAIR_REQUIRED` 작업을 보면 `repairReason`을 읽는다. 자동 보상 대상은 `LAST_PARTICIPANT_EXPIRED`·`POST_COMMIT_PARTICIPANT_REJECT`·`APPLY_FAILED`다. `DB_STATE_UNEXPECTED`는 자동 보상 대상에서 제외한다.

`COMPENSATING`은 보상 발급 전에도 기록된다. 각 Backend Pod의 `UI_COMPENSATION_ISSUED` 로그에서 원 `operationId`와 `compensationId`를 연결한 뒤 보상 ID를 관리 API로 조회한다.

보상 발급 실패 시 복구 필요 신호가 남는다. 보상 작업이 다시 실패해도 보상의 보상을 자동 발급하지 않는다.

## 자동 보상이 끝난 뒤 원 작업의 복구 완료 판정
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 완료 뒤 원 작업 확정 L554~592
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 실행 예외 기록 L65~72
원천: nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 ; 대상·revision 검사·종결 L23~71
원천: nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/coordination/UiOperationExecutorTest.java @ 985d17c604dd ; 보상 확정 회귀 L241~268
```

보상 완료 확정 수정 이후 판본에서는 보상 작업의 DB 확정·장벽 개방·`PASS` 뒤 원 작업을 `FAIL`·`ROLLED_BACK`으로 확정한다. 보상 응답의 `compensatesRevision`과 원 작업의 `desiredRevision`도 대조한다.

운영자는 보상 ID와 원 작업 ID를 각각 재조회해 이 결과를 확인한다. 원 작업의 오류 기록은 유지되고 `repairReason`은 비워진다.

원 작업 확정이 거부 코드를 반환하면 `UI_OPERATION_ROLLBACK_NOT_CONFIRMED`가 남는다. 호출 예외는 그 로그 대신 `UI_OPERATION_ADVANCE_FAILED`가 남을 수 있다.

보상 성공만으로 원 작업의 복구를 확정하지 않는다. 상세 진단은 `00.rag/admin/coordination-troubleshooting.md`를 따른다.

## Redis 원장이 DB 확정 이력보다 뒤처져 설비가 차단될 때
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 원장 부재·후퇴 차단 L77~118
원천: nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicyTest.java @ d3636ab86763 ; 후퇴·유실·조회 실패 기대값 L97~139
```

원장 유실 차단 수정 이후 판본에서 Gateway 기동 로그의 `GW_LEDGER_REGRESSION`을 확인하면 설비 ID·Redis 확정 revision·DB 확정 revision을 보존한다.

DB 확정 이력이 양수인데 Redis 확정 자료가 없거나 Redis revision이 DB보다 낮으면 그 설비의 적재를 차단한다. Redis 조회 실패도 DB만으로 진행하지 않고 전파한다.

초기 값 발급이나 Pod 교체로 이 차단을 복구 완료로 처리하지 않는다. 원장·DB 대조와 복구 필요 판단을 조정 담당자에게 넘긴다. 상세 장애 진단은 `00.rag/admin/coordination-troubleshooting.md`가 담당한다.

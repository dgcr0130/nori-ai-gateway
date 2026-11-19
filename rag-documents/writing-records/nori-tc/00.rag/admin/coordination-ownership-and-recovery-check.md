# TC 조정 작업·소유권·복구 필요 확인 작성 기록

## 대상

- objectKey: `00.rag/admin/coordination-ownership-and-recovery-check.md`
- 구획: admin `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 운영
- 검색 원문 SHA-256: `2677c882740a639cc89cf5d6e7869936b3a1fbad95dad88ef61b119527440a0d`
- 검색 원문 줄 수: 255
- 절 수: 17
- 확인일: 2026-11-19 (한국 시간)
- 작업: 지정 두 파일 작성. 제품 수정·git add/commit/push·업로드·서버 접속은 수행하지 않았다.
- 작성·사실 대조는 이 독립 세션에서 직접 수행했다. 다른 Agent 호출 0회.

## 원천 판본

| 저장소 | 현재 HEAD | 최종 인용 파일 수 | 작업 트리 확인 |
|---|---|---:|---|
| nori-tc-common | 6fe71f6b2da6 | 4 | 인용 파일 경로를 명시한 git status --short 결과 공백·exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 6 | 인용 파일 경로를 명시한 git status --short 결과 공백·exit 0 |
| nori-tc-gateway | d3636ab86763 | 3 | 인용 파일 경로를 명시한 git status --short 결과 공백·exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 14 | 인용 파일 경로를 명시한 git status --short 결과 공백·exit 0 |
| nori-tc-ui-front | 9fe69a75dcd3 | 5 | 인용 파일 경로를 명시한 git status --short 결과 공백·exit 0 |

작업 전 관련 폴더·파일의 상태와 작성 후 인용 파일별 상태를 확인했다. git의 사용자 ignore 파일 접근 경고는 출력됐으나 명령은 exit 0이고 인용 파일의 변경 행은 없었다. 형제 저장소의 소유자 검사는 각 읽기 명령에만 정확한 safe.directory를 지정했다. 전역 git 설정을 변경하지 않았다.

필수 작성 규칙과 문서 목록 전체, O §7.3·§11.5, 지정 커밋 두 개의 실제 diff와 현재 HEAD 구현·관련 시험의 기대값을 읽었다. 시험의 코드를 읽은 것이며 제품 시험을 실행한 것은 아니다.

배포 교차 확인: nori-tc-exec의 deploy/helm/{tc-gateway,tc-business,tc-ui-backend}/templates/deployment.yaml의 Recreate·조정 환경변수·Pod UID, create-k8s-secrets.sh의 공유 shard 정의와 두 Secret 공급, coord-epoch.sh의 실제 인자와 NX 분기, .gitlab-ci.yml의 Gateway 전용 검증 호출, 앱 application.yaml의 외부 설정 import, deploy/docker/Dockerfile-ci의 app.jar·설정 디렉터리를 대조했다. 현장 적용값은 관측하지 않았다.

- Gateway 지정 원천: 62154da06163f89ee4f119799c9970cd447d0bb9. DB 확정 이력이 양수일 때 overlay 부재도 차단하도록 바꾼 diff와 wipedLedgerWithDbFinalizedRevisionIsFencedInsteadOfFallingBackToDatabase 시험을 확인했다.
- Backend 지정 원천: b8faaead3ea5bdf2047cfe642cf3afbec25a6a73. 보상 PASS 후 closeCompensatedOperation 연결과 passedCompensationClosesTheOperationItReverted·ordinaryPassDoesNotCloseAnythingElse 시험을 확인했다.
- 명령 예시는 실행할 조작·조회 대상으로 설명한 것이며 이 세션에서 kubectl·SSH·Docker·Redis·HTTP 호출을 실행하지 않았다.

## 절별 사실 대조

### 1. 소유권과 조정 작업을 조회하려면 어떤 권한이 필요한가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L17 | 기본 권한 데이터 기준으로 설비·Gateway 소유권 조회는 `EQP_READ`, 조정 작업 관리 조회는 `COORDINATION_ADMIN_READ`가 필요하다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 조회 기본 연결 L124~130<br>nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 기본 연결 L41~56 | 기본 EQP_READ의 GET prefix와 V8의 조정 GET 전용 권한·ADMIN 연결을 확인했다. |
| L17 | 조정 조회 권한은 V8에서 ADMIN 그룹에 연결한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 조회 기본 연결 L124~130<br>nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 기본 연결 L41~56 | 기본 EQP_READ의 GET prefix와 V8의 조정 GET 전용 권한·ADMIN 연결을 확인했다. |
| L19 | 관리자 계정도 실제 API 권한 연결이 없으면 차단된다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185 | 권한 캐시 초기화 실패·목록 동기화 불일치·경로/메서드 미등록·권한 미보유는 차단한다. |
| L19 | 조회가 거부되면 계정 담당자는 요청 경로·HTTP 메서드·보유 권한·권한 목록 동기화 상태를 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185 | 권한 캐시 초기화 실패·목록 동기화 불일치·경로/메서드 미등록·권한 미보유는 차단한다. |

### 2. 설비 화면에서 담당 Pod를 대조하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L31 | 「설비」 화면에서 「Gateway」를 선택하고 왼쪽 검색어를 비운다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Gateway 선택·조회 결과 전달 L1135~1168<br>nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색 필터·점유 표시·Pod 표시 L286~338<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Gateway 문구 L39~40<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 설비 메뉴 L1092~1098 | Gateway 카드 선택·검색 중 빈 그룹 제외를 렌더 코드에서 확인했다. |
| L31 | 검색 중에는 설비가 없는 그룹과 대기 Pod가 숨겨질 수 있다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Gateway 선택·조회 결과 전달 L1135~1168<br>nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색 필터·점유 표시·Pod 표시 L286~338<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Gateway 문구 L39~40<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 설비 메뉴 L1092~1098 | Gateway 카드 선택·검색 중 빈 그룹 제외를 렌더 코드에서 확인했다. |
| L33 | 그룹 아래의 Pod 식별 문구와 「미점유」·「spare」 표시를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색 필터·점유 표시·Pod 표시 L286~338<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 미점유·대기 표시 L594~598 | 미점유·spare 배지와 조건부 Pod 표시를 확인했다. |
| L33 | 클러스터 조회 권한이 있는 운영자는 다음 Pod 목록과 대조한다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색 필터·점유 표시·Pod 표시 L286~338<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 미점유·대기 표시 L594~598 | 미점유·spare 배지와 조건부 Pod 표시를 확인했다. |
| L36 | kubectl -n tc get pods -l app=tc-gateway -o wide | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Pod 선택 라벨 L16~23 | Deployment의 app 라벨을 이용한 읽기 예다. 현장 명령은 실행하지 않았다. |
| L39 | 화면에 담당 Pod 문구가 없으면 소유권 조회 API와 런타임 인스턴스 API를 함께 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Gateway 선택·조회 결과 전달 L1135~1168<br>nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색 필터·점유 표시·Pod 표시 L286~338<br>nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자와 Pod 대조 L55~97 | Pod 문구가 없으면 lease와 인스턴스의 session 연결을 확인하는 절차다. |

### 3. Gateway 소유권 API로 어느 그룹을 누가 맡는지 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L49 | 인증된 운영자는 `GET /api/eqp/gateway-shard-leases`의 `data`에서 `groupId`·`ownedPartitions`·`ownerInstanceId`를 읽는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd ; 그룹별 조회 L38~47 | Controller가 그룹·파티션·소유자를 data 배열로 변환한다. |
| L49 | 소유자가 없으면 `ownerInstanceId`는 null이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GatewayShardLeaseController.java @ 985d17c604dd ; 그룹별 조회 L38~47 | Controller가 그룹·파티션·소유자를 data 배열로 변환한다. |
| L51 | `GET /api/eqp/gateway-instances`의 `sessionId`와 소유자 식별자가 일치할 때 `podName`을 연결한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd ; Gateway 인스턴스 조회 L42~46<br>nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자와 Pod 대조 L55~97 | 인스턴스 sessionId와 소유자 식별자의 완전 일치로 Pod를 연결한다. |
| L51 | 소유자 문자열을 잘라 Pod 이름을 추정하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd ; Gateway 인스턴스 조회 L42~46<br>nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자와 Pod 대조 L55~97 | 인스턴스 sessionId와 소유자 식별자의 완전 일치로 Pod를 연결한다. |
| L51 | 화면도 이 일치 관계로 담당 Pod를 표시한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/RuntimeInstanceController.java @ 985d17c604dd ; Gateway 인스턴스 조회 L42~46<br>nori-tc-ui-front/src/features/eqp/utils/eqp-group.util.ts @ 9fe69a75dcd3 ; 소유자와 Pod 대조 L55~97 | 인스턴스 sessionId와 소유자 식별자의 완전 일치로 Pod를 연결한다. |

### 4. 조정 lease와 이전 lease가 함께 남아 있을 때 확인 순서

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L59 | 조정 기능을 켠 Backend의 소유권 조회는 조정 lease를 먼저 읽는다. | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapter.java @ 985d17c604dd ; 조정 우선·이전 형식 보완 L62~118<br>nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapterTest.java @ 985d17c604dd ; 우선순위 기대값 L96~140 | 유효한 session·양수 epoch 검사 뒤 미점유 그룹만 이전 형식으로 보완한다. |
| L59 | 양수 epoch와 비어 있지 않은 sessionId가 없으면 그 그룹에 한해 이전 형식의 lease를 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapter.java @ 985d17c604dd ; 조정 우선·이전 형식 보완 L62~118<br>nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapterTest.java @ 985d17c604dd ; 우선순위 기대값 L96~140 | 유효한 session·양수 epoch 검사 뒤 미점유 그룹만 이전 형식으로 보완한다. |
| L61 | 조정 lease의 소유자가 있으면 이전 키의 잔여 값이 소유자를 덮지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapter.java @ 985d17c604dd ; 조정 우선·이전 형식 보완 L62~118<br>nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapterTest.java @ 985d17c604dd ; 우선순위 기대값 L96~140 | 조정 소유자가 이전 키에 덮이지 않는 시험 기대값도 읽었다. |
| L61 | Redis에서 조정 키 하나가 없다는 관측만으로 그룹 전체를 미점유라고 판정하지 않고 소유권 API 결과와 대조한다. | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapter.java @ 985d17c604dd ; 조정 우선·이전 형식 보완 L62~118<br>nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/coordination/CoordinatedGatewayShardLeaseAdapterTest.java @ 985d17c604dd ; 우선순위 기대값 L96~140 | 조정 소유자가 이전 키에 덮이지 않는 시험 기대값도 읽었다. |

### 5. Redis에서 Gateway 소유 세션과 epoch를 직접 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L70 | Redis 조회 권한으로 `REDIS_NS`·`REDIS_WORKLOAD`를 설치 대상에, `GROUP`을 조회할 그룹 ID에 지정한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 조회 대상·인증 조건 L1860~1932 | 가이드의 namespace·exec 가능한 워크로드 지정과 인증 조건을 대조했다. |
| L70 | 아래는 논리 DB 0에서 인증 없이 조회 가능한 환경의 명령이다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 조회 대상·인증 조건 L1860~1932 | 가이드의 namespace·exec 가능한 워크로드 지정과 인증 조건을 대조했다. |
| L73 | kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 HMGET "tc:coord:v1:gateway:shard:$GROUP:lease" sessionId epoch | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; Gateway lease 키 L182~189<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 세션·epoch·만료 L25~31 | 실제 키 조립과 Lua가 저장하는 sessionId·epoch 필드를 확인했다. |
| L76 | 두 반환값은 해당 조정 lease의 소유 세션과 epoch다. | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 세션·epoch·만료 L25~31<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 조회 대상·인증 조건 L1860~1932 | 반환 순서와 인증 시 별도 경로 사용 조건이며 자격값은 쓰지 않았다. |
| L76 | 인증이 필요하면 Redis 담당자의 인증 경로로 같은 항목을 조회한다. | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 세션·epoch·만료 L25~31<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 조회 대상·인증 조건 L1860~1932 | 반환 순서와 인증 시 별도 경로 사용 조건이며 자격값은 쓰지 않았다. |
| L76 | 자격값을 명령 인자에 넣지 않는다. | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 세션·epoch·만료 L25~31<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 조회 대상·인증 조건 L1860~1932 | 반환 순서와 인증 시 별도 경로 사용 조건이며 자격값은 쓰지 않았다. |

### 6. Gateway 소유권 재배치 후 무엇을 확인하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L85 | 중단 영향을 확인하고 재배치할 Gateway Pod를 `POD`에 지정한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재배치 명령 L1846~1854 | 가이드의 재배치 조작을 중단 영향이 있는 운영 변경으로 썼다. |
| L85 | 운영 변경 권한으로 `nori-tc-exec` 루트에서 실행한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재배치 명령 L1846~1854 | 가이드의 재배치 조작을 중단 영향이 있는 운영 변경으로 썼다. |
| L88 | kubectl -n tc delete pod "$POD" | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재배치 명령 L1846~1854 | 가이드의 Pod 삭제 대상을 변수로 받는 예다. 실행하지 않았다. |
| L89 | sh ./deploy/scripts/verify-gateway-rollout.sh tc | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재배치 명령 L1846~1854<br>nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 준비 상태 판정 L33~57 | 실재하는 검증 스크립트와 namespace 인자를 대조했다. |
| L92 | 검증 성공 뒤 소유권 API에서 모든 설정 그룹의 소유자와 담당 파티션을 다시 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득·담당 파티션·재기동 L206~254 | 운용 중 대기 인스턴스의 획득은 graceful shutdown을 통해 재기동한다. |
| L92 | 대기 인스턴스가 운용 중 빈 슬롯을 얻으면 통신부를 시작하기 위해 자체 재기동하므로 Pod 이름을 고정해 예상하지 않는다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득·담당 파티션·재기동 L206~254 | 운용 중 대기 인스턴스의 획득은 graceful shutdown을 통해 재기동한다. |
| L94 | 검증 실패나 미점유 그룹이 남으면 재배치 완료로 처리하지 않는다. | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 준비 상태 판정 L33~57<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재배치 명령 L1846~1854 | 준비 판정만으로 모든 그룹 소유를 보증하지 않아 API 재대조를 요구했다. |
| L94 | replica·그룹 확장은 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다. | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 준비 상태 판정 L33~57<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재배치 명령 L1846~1854 | 준비 판정만으로 모든 그룹 소유를 보증하지 않아 API 재대조를 요구했다. |

### 7. 조정 초기 값을 발급하기 전에 맞출 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L103 | 운영 중 조정 기능을 처음 켜는 전환은 UI 변경 요청 중단·진행 중 작업 0·구 Pod 전체 종료·스키마 적용 뒤에 초기 값을 발급한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전환 배포 선행 조건 L2624~2644 | O §11.5의 발급 전 조건과 순서를 확인했다. |
| L103 | Redis 영속 구성과 DB 백업도 먼저 준비한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전환 배포 선행 조건 L2624~2644 | O §11.5의 발급 전 조건과 순서를 확인했다. |
| L105 | 전환 배포와 되돌림은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`, 진행 중 건수 조회는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전환 배포 선행 조건 L2624~2644<br>nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/upgrade-rollback-and-configuration-apply.md ; 조정 기능 전환 절<br>nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/daily-checks-and-log-inspection.md ; 끝나지 않은 조정 작업 수 절 | 이미 작성된 운영 문서의 소유 범위를 가리키는 안내다. |
| L107 | 기존 원장을 버리고 초기화하는 작업은 별도 승인 대상이다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 발급 대상·초기화 제한 L1934~1939<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 발급과 접속 입력 L4~32 | 스크립트에는 삭제 기능이 없고 원장 초기화는 별도 승인이라고 명시한다. |
| L107 | 기존 cluster Epoch 삭제를 일상 복구 절차에 섞지 않는다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 발급 대상·초기화 제한 L1934~1939<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 발급과 접속 입력 L4~32 | 스크립트에는 삭제 기능이 없고 원장 초기화는 별도 승인이라고 명시한다. |

### 8. coord-epoch.sh로 발급한 결과 판정하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L116 | 구 Pod 종료·스키마 적용 뒤 SSH·Redis 작업 권한이 있는 운영자는 `K8S_SSH_KEY`·`K8S_NODE`·`REDIS_NS`·`REDIS_DEPLOY`를 설치 대상으로 지정한다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 접속·인자 L30~55<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전환 발급 순서 L2627~2631 | 접속 환경변수 네 항목과 구 Pod 종료 후 발급 순서를 확인했다. |
| L116 | 저장소 기본 접속값을 그대로 사용하지 않는다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 접속·인자 L30~55<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전환 발급 순서 L2627~2631 | 접속 환경변수 네 항목과 구 Pod 종료 후 발급 순서를 확인했다. |
| L118 | `nori-tc-exec` 루트에서 `bash ./coord-epoch.sh issue 0`을 실행한다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조건부 발급·결과 L72~98 | issue의 DB 인자·NX 저장·세 결과 출력을 실제 분기와 대조했다. |
| L118 | `[ISSUED]`는 새 값 저장, `[SKIP]`은 기존 값 유지, `[ISSUED-BY-OTHER]`는 동시 발급자가 저장한 값 유지를 나타낸다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조건부 발급·결과 L72~98 | issue의 DB 인자·NX 저장·세 결과 출력을 실제 분기와 대조했다. |
| L120 | 스크립트는 Redis 인증 입력을 받지 않는다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 접속·인자 L30~55<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조건부 발급·결과 L72~98<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증 조건 L1904~1932 | 인증 인자가 없으므로 인증 환경에서는 별도 경로를 사용한다. 실행 오류를 성공으로 판정하지 않는다. |
| L120 | 인증이 필요한 Redis에서는 직접 인증된 관리 경로를 사용한다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 접속·인자 L30~55<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조건부 발급·결과 L72~98<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증 조건 L1904~1932 | 인증 인자가 없으므로 인증 환경에서는 별도 경로를 사용한다. 실행 오류를 성공으로 판정하지 않는다. |
| L120 | 실행 오류를 발급 성공으로 처리하지 않는다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 접속·인자 L30~55<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조건부 발급·결과 L72~98<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증 조건 L1904~1932 | 인증 인자가 없으므로 인증 환경에서는 별도 경로를 사용한다. 실행 오류를 성공으로 판정하지 않는다. |

### 9. SSH 경유 없이 Redis 초기 값을 발급하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L128 | 구 Pod 종료·스키마 적용을 마친 운영자는 `REDIS_NS`·`REDIS_WORKLOAD`를 설치 대상으로 지정한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 발급 전 상태 L2627~2631 | O §11.5의 발급 시점과 O §7.3의 직접 exec 절차에 근거한다. |
| L128 | 아래 명령은 Redis 인증이 필요 없는 환경에서만 실행한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 발급 전 상태 L2627~2631 | O §11.5의 발급 시점과 O §7.3의 직접 exec 절차에 근거한다. |
| L131 | kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- sh -c 'redis-cli -n 0 SET tc:coord:v1:cluster:epoch "$(cat /proc/sys/kernel/random/uuid)" NX' | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897 | 가이드의 Pod 내부 UUID 생성·NX SET을 변수로 지정한 예다. 실행하지 않았다. |
| L134 | Pod 안에서 UUID를 만들도록 홑따옴표를 유지한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 저장 후 값 대조 L85~92 | 인용부호·기존 값 보존·저장 뒤 GET 확인 절차를 대조했다. |
| L134 | `NX`는 기존 값을 보존한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 저장 후 값 대조 L85~92 | 인용부호·기존 값 보존·저장 뒤 GET 확인 절차를 대조했다. |
| L134 | 새 값 저장은 `OK`로 표시한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 저장 후 값 대조 L85~92 | 인용부호·기존 값 보존·저장 뒤 GET 확인 절차를 대조했다. |
| L134 | 발급 뒤 다음 조회로 저장값을 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 저장 후 값 대조 L85~92 | 인용부호·기존 값 보존·저장 뒤 GET 확인 절차를 대조했다. |
| L134 | 오류·빈 값이면 앱 기동을 멈춘다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897<br>nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 저장 후 값 대조 L85~92 | 인용부호·기존 값 보존·저장 뒤 GET 확인 절차를 대조했다. |
| L137 | kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 GET tc:coord:v1:cluster:epoch | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 직접 발급과 인용부호 L1882~1897 | 가이드의 논리 DB 0·cluster Epoch 키 조회다. 실행하지 않았다. |

### 10. 미종결 작업 ID를 찾고 복구 대기 누락을 피하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L147 | Redis 조회 권한으로 설치 대상의 `REDIS_NS`·`REDIS_WORKLOAD`를 지정한다. | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 미종결 인덱스 L90~96<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 종결 상태 인덱스 제거 L47~65 | 실제 미종결 zset에서 앞 20개를 읽는 한정된 운영 예다. |
| L147 | 인증 없이 조회 가능한 환경에서 미종결 작업 ID 앞 20개를 읽는다. | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 미종결 인덱스 L90~96<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 종결 상태 인덱스 제거 L47~65 | 실제 미종결 zset에서 앞 20개를 읽는 한정된 운영 예다. |
| L150 | kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 ZRANGE tc:coord:v1:operations:pending 0 19 | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 미종결 인덱스 L90~96<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 종결 상태 인덱스 제거 L47~65 | 실제 pending zset 키를 ZRANGE 0 19로 읽는 예다. 운영 환경에서는 실행하지 않았다. |
| L153 | `REPAIR_REQUIRED`로 확정된 작업은 이 인덱스에서 제거된다. | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 종결 상태 인덱스 제거 L47~65<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정·로그 L665~688 | REPAIR_REQUIRED 종결 전이는 ZREM한다. executor가 operationId를 로그에 남긴다. |
| L153 | 인덱스가 비어 있어도 복구 대기가 없다고 판정하지 않는다. | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 종결 상태 인덱스 제거 L47~65<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정·로그 L665~688 | REPAIR_REQUIRED 종결 전이는 ZREM한다. executor가 operationId를 로그에 남긴다. |
| L153 | 각 Backend Pod의 `UI_OPERATION_REPAIR_REQUIRED` 로그에 남은 `operationId`도 관리 API로 조회한다. | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 종결 상태 인덱스 제거 L47~65<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정·로그 L665~688 | REPAIR_REQUIRED 종결 전이는 ZREM한다. executor가 operationId를 로그에 남긴다. |

### 11. 조정 작업 관리 API에서 결과를 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L162 | `COORDINATION_ADMIN_READ`가 있는 인증된 운영자는 대상 ID로 `GET /api/coordination/operations/{operationId}`를 요청한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 읽기 전용 관리 조회 L28~41<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 결과 상태 L76~95<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 조회 기본 연결 L124~130<br>nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 기본 연결 L41~56 | GET 관리 경로와 필요한 권한을 대조했다. |
| L162 | ID에는 실제 작업 ID를 넣는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 읽기 전용 관리 조회 L28~41<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 결과 상태 L76~95<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 조회 기본 연결 L124~130<br>nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 조정 조회 기본 연결 L41~56 | GET 관리 경로와 필요한 권한을 대조했다. |
| L164 | HTTP 200은 작업이 존재한다는 뜻이며 처리 성공 판정은 `data.status`로 한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 결과 상태 L76~95<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 응답 필드 L33~52 | 존재 시 200과 DTO의 대상·상태·복구 사유·오류 필드를 확인했다. |
| L164 | `eqpId`·`eventType`으로 대상을 대조하고 `status`·`phase`·`repairReason`·`errorCode`·`errorMsg`를 함께 기록한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 결과 상태 L76~95<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 응답 필드 L33~52 | 존재 시 200과 DTO의 대상·상태·복구 사유·오류 필드를 확인했다. |
| L166 | 이 API는 읽기 전용이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 읽기 전용 관리 조회 L28~41 | Controller에 GET 매핑만 있고 강제 성공·개방을 하는 쓰기 조작이 없다. |
| L166 | 강제 성공 처리나 장벽 개방을 요청하는 쓰기 기능은 제공하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 읽기 전용 관리 조회 L28~41 | Controller에 GET 매핑만 있고 강제 성공·개방을 하는 쓰기 조작이 없다. |

### 12. 작업이 없다는 응답과 조정 조회 불가를 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L175 | 조정 작업 관리 조회의 HTTP 상태와 `errorCode`를 함께 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 비활성·미존재 분기 L80~94<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminControllerTest.java @ 985d17c604dd ; 오류 응답 기대값 L89~123<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 조정 조회 예외 응답 L154~162 | HTTP 상태와 errorCode를 Controller·handler·시험 기대값과 대조했다. |
| L179 | \| 404·OPERATION_NOT_FOUND \| 요청한 작업이 없다. 작업 ID와 조회 대상을 대조한다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 비활성·미존재 분기 L80~94<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminControllerTest.java @ 985d17c604dd ; 오류 응답 기대값 L89~123 | Optional.empty 분기가 404 OPERATION_NOT_FOUND를 반환한다. |
| L180 | \| 503·COORDINATION_DISABLED \| 조회 대상 Backend가 조정 원장을 사용하지 않는 구성이다. 배포 설정을 확인한다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 비활성·미존재 분기 L80~94<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminControllerTest.java @ 985d17c604dd ; 오류 응답 기대값 L89~123 | 조회 서비스 미등록이면 503 COORDINATION_DISABLED다. |
| L181 | \| 503·COORDINATION_UNAVAILABLE \| 조정 원장에 접근할 수 없다. 작업이 사라졌다고 판정하지 않고 Redis 접근 장애를 조사한다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminControllerTest.java @ 985d17c604dd ; 오류 응답 기대값 L89~123<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 조정 조회 예외 응답 L154~162 | 예외 handler와 시험이 503 COORDINATION_UNAVAILABLE를 확인한다. |
| L183 | 조회 불가 상태에서는 작업 완료나 복구 성공을 확정하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CoordinationAdminController.java @ 985d17c604dd ; 비활성·미존재 분기 L80~94<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 조정 조회 예외 응답 L154~162 | 관측 불가를 성공으로 바꾸지 않는 운영 절차다. |

### 13. 장벽·참여자 적용·DB 확정을 함께 대조하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L192 | 관리 응답의 `resources`에서 `openRevision`이 `committedRevision`보다 작으면 장벽이 닫힌 상태다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 리소스·참여 증적 L64~102 | ResourceHead의 open/committed 비교와 관련 작업 식별 필드를 확인했다. |
| L192 | `blockedByOperationId`·`activeOperationId`로 관련 작업을 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 리소스·참여 증적 L64~102 | ResourceHead의 open/committed 비교와 관련 작업 식별 필드를 확인했다. |
| L194 | `participantProofs`의 `preparedRevision`만 있고 `appliedRevision`이 없으면 그 참여자는 준비 증적만 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 리소스·참여 증적 L64~102<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; DB 확정 뒤 장벽 개방 L534~564 | 준비·적용 증적·ownerEpoch·DB 확정·목표 revision의 응답 항목을 대조했다. |
| L194 | Gateway 참여자의 `ownerEpoch`, 작업의 `desiredRevision`·`dbFinalized`도 함께 대조한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 리소스·참여 증적 L64~102<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; DB 확정 뒤 장벽 개방 L534~564 | 준비·적용 증적·ownerEpoch·DB 확정·목표 revision의 응답 항목을 대조했다. |
| L196 | 관리 조회는 여러 키를 순서대로 읽으므로 한 시점의 원자적 스냅샷이 아니다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAdminQueryService.java @ 985d17c604dd ; 조회 시점 한계 L24~26 | 순차 읽기이므로 원자적 스냅샷을 보장하지 않는 구현을 확인했다. |
| L196 | 진행 중 값이 어긋나면 다시 조회하고 시각을 함께 남긴다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAdminQueryService.java @ 985d17c604dd ; 조회 시점 한계 L24~26 | 순차 읽기이므로 원자적 스냅샷을 보장하지 않는 구현을 확인했다. |

### 14. Backend replica 중 누가 복구 작업을 진행하는지 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L207 | Redis 조회 권한으로 `REDIS_NS`·`REDIS_WORKLOAD`를 설치 대상으로, `OP`를 작업 ID로 지정한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 미종결 발견·진행 L54~74<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 세션·lease 검사와 반납 L135~163<br>nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 작업별 실행 lease L202~209<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 정보와 만료 L25~31<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 대상·인증 조건 L1860~1932 | reconciler는 미종결 작업을 찾고 활성 세션이 실행 lease를 얻은 뒤 전진한다. |
| L207 | Backend는 실행 lease를 획득한 세션에서 미종결 작업을 진행한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 미종결 발견·진행 L54~74<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 세션·lease 검사와 반납 L135~163<br>nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 작업별 실행 lease L202~209<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 정보와 만료 L25~31<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 대상·인증 조건 L1860~1932 | reconciler는 미종결 작업을 찾고 활성 세션이 실행 lease를 얻은 뒤 전진한다. |
| L207 | 아래는 논리 DB 0의 비인증 조회 예다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 미종결 발견·진행 L54~74<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 세션·lease 검사와 반납 L135~163<br>nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 작업별 실행 lease L202~209<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 정보와 만료 L25~31<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 대상·인증 조건 L1860~1932 | reconciler는 미종결 작업을 찾고 활성 세션이 실행 lease를 얻은 뒤 전진한다. |
| L210 | kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 HMGET "tc:coord:v1:executor:$OP:lease" sessionId epoch | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 작업별 실행 lease L202~209<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/lease_acquire.lua @ 6fe71f6b2da6 ; 소유 정보와 만료 L25~31 | 작업별 lease 키와 Lua의 sessionId·epoch 필드를 대조했다. 실행하지 않았다. |
| L213 | 인증 환경은 Redis 담당자의 인증 경로를 사용한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 세션·lease 검사와 반납 L135~163<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 대상·인증 조건 L1860~1932 | finally에서 반납하므로 순간적인 키 부재로 작업 유실을 추정하지 않는다. |
| L213 | 실행 시도가 끝나면 lease를 반납하므로 조회 순간 키가 없다는 사실만으로 작업 유실을 판정하지 않는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 세션·lease 검사와 반납 L135~163<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 대상·인증 조건 L1860~1932 | finally에서 반납하므로 순간적인 키 부재로 작업 유실을 추정하지 않는다. |
| L213 | 관리 API의 작업 상태·갱신 시각도 함께 본다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 세션·lease 검사와 반납 L135~163<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 대상·인증 조건 L1860~1932 | finally에서 반납하므로 순간적인 키 부재로 작업 유실을 추정하지 않는다. |

### 15. 복구 필요 작업의 자동 보상 발급 여부 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L223 | 조정 기능에서 `REPAIR_REQUIRED` 작업을 보면 `repairReason`을 읽는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 대상 사유 L48~59<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688 | 자동 보상 대상 세 사유의 집합과 DB_STATE_UNEXPECTED 제외를 확인했다. |
| L223 | 자동 보상 대상은 `LAST_PARTICIPANT_EXPIRED`·`POST_COMMIT_PARTICIPANT_REJECT`·`APPLY_FAILED`다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 대상 사유 L48~59<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688 | 자동 보상 대상 세 사유의 집합과 DB_STATE_UNEXPECTED 제외를 확인했다. |
| L223 | `DB_STATE_UNEXPECTED`는 자동 보상 대상에서 제외한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 대상 사유 L48~59<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688 | 자동 보상 대상 세 사유의 집합과 DB_STATE_UNEXPECTED 제외를 확인했다. |
| L225 | `COMPENSATING`은 보상 발급 전에도 기록된다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; 발급 로그 L142~144 | COMPENSATING 전이가 발급 호출보다 앞선다. 발급 로그에 원 ID와 보상 ID가 실린다. |
| L225 | 각 Backend Pod의 `UI_COMPENSATION_ISSUED` 로그에서 원 `operationId`와 `compensationId`를 연결한 뒤 보상 ID를 관리 API로 조회한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; 발급 로그 L142~144 | COMPENSATING 전이가 발급 호출보다 앞선다. 발급 로그에 원 ID와 보상 ID가 실린다. |
| L227 | 보상 발급 실패 시 복구 필요 신호가 남는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; 중첩 금지·실패 처리 L70~94 | 발급 예외는 null을 반환한다. compensatesRevision이 있으면 중첩 보상을 발급하지 않는다. |
| L227 | 보상 작업이 다시 실패해도 보상의 보상을 자동 발급하지 않는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 복구 필요 확정 후 발급 L665~688<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; 중첩 금지·실패 처리 L70~94 | 발급 예외는 null을 반환한다. compensatesRevision이 있으면 중첩 보상을 발급하지 않는다. |

### 16. 자동 보상이 끝난 뒤 원 작업의 복구 완료 판정

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L237 | 보상 완료 확정 수정 이후 판본에서는 보상 작업의 DB 확정·장벽 개방·`PASS` 뒤 원 작업을 `FAIL`·`ROLLED_BACK`으로 확정한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 완료 뒤 원 작업 확정 L554~592<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 ; 대상·revision 검사·종결 L23~71<br>nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/coordination/UiOperationExecutorTest.java @ 985d17c604dd ; 보상 확정 회귀 L241~268 | 보상 PASS 직후 markRolledBack을 호출하고 Lua가 대상과 revision을 검증한다. |
| L237 | 보상 응답의 `compensatesRevision`과 원 작업의 `desiredRevision`도 대조한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 완료 뒤 원 작업 확정 L554~592<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 ; 대상·revision 검사·종결 L23~71<br>nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/coordination/UiOperationExecutorTest.java @ 985d17c604dd ; 보상 확정 회귀 L241~268 | 보상 PASS 직후 markRolledBack을 호출하고 Lua가 대상과 revision을 검증한다. |
| L239 | 운영자는 보상 ID와 원 작업 ID를 각각 재조회해 이 결과를 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 완료 뒤 원 작업 확정 L554~592<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 ; 대상·revision 검사·종결 L23~71 | Lua가 FAIL·ROLLED_BACK으로 확정하고 repairReason을 비우며 오류를 보존한다. |
| L239 | 원 작업의 오류 기록은 유지되고 `repairReason`은 비워진다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 완료 뒤 원 작업 확정 L554~592<br>nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 ; 대상·revision 검사·종결 L23~71 | Lua가 FAIL·ROLLED_BACK으로 확정하고 repairReason을 비우며 오류를 보존한다. |
| L241 | 원 작업 확정이 거부 코드를 반환하면 `UI_OPERATION_ROLLBACK_NOT_CONFIRMED`가 남는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 원 작업 확정 호출·거부 코드 L578~592 | markRolledBack이 OK 아닌 코드를 반환한 뒤에만 거부 로그를 남긴다. |
| L241 | 호출 예외는 그 로그 대신 `UI_OPERATION_ADVANCE_FAILED`가 남을 수 있다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 원 작업 확정 호출·거부 코드 L578~592<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 실행 예외 기록 L65~72 | 확정 호출의 예외를 closeCompensatedOperation에서 잡지 않는다. reconciler가 advance 예외를 기록한다. |
| L243 | 보상 성공만으로 원 작업의 복구를 확정하지 않는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 원 작업 확정 호출·거부 코드 L578~592<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 실행 예외 기록 L65~72 | 보상 PASS 뒤에도 원 작업 확정의 거부·예외가 가능하므로 원 작업을 별도로 확인한다. |
| L243 | 상세 진단은 `00.rag/admin/coordination-troubleshooting.md`를 따른다. | nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/coordination-troubleshooting.md ; 보상 PASS 뒤 원 작업이 남고 확정 거부 로그도 없을 때 | 상세 예외 진단은 기존 소유 문서에 두고 설명을 복제하지 않았다. |

### 17. Redis 원장이 DB 확정 이력보다 뒤처져 설비가 차단될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L251 | 원장 유실 차단 수정 이후 판본에서 Gateway 기동 로그의 `GW_LEDGER_REGRESSION`을 확인하면 설비 ID·Redis 확정 revision·DB 확정 revision을 보존한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 원장 부재·후퇴 차단 L77~118<br>nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicyTest.java @ d3636ab86763 ; 후퇴·유실·조회 실패 기대값 L97~139 | 지정 62154da0 변경과 현재 HEAD의 GW_LEDGER_REGRESSION 로그를 대조했다. |
| L253 | DB 확정 이력이 양수인데 Redis 확정 자료가 없거나 Redis revision이 DB보다 낮으면 그 설비의 적재를 차단한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 원장 부재·후퇴 차단 L77~118<br>nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicyTest.java @ d3636ab86763 ; 후퇴·유실·조회 실패 기대값 L97~139 | DB 확정 양수와 자료 부재·DB보다 낮은 revision·Redis 읽기 실패를 분기와 시험으로 대조했다. |
| L253 | Redis 조회 실패도 DB만으로 진행하지 않고 전파한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 원장 부재·후퇴 차단 L77~118<br>nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicyTest.java @ d3636ab86763 ; 후퇴·유실·조회 실패 기대값 L97~139 | DB 확정 양수와 자료 부재·DB보다 낮은 revision·Redis 읽기 실패를 분기와 시험으로 대조했다. |
| L255 | 초기 값 발급이나 Pod 교체로 이 차단을 복구 완료로 처리하지 않는다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 원장 부재·후퇴 차단 L77~118 | Epoch 발급·Pod 교체가 이 차단 조건을 해소하지 않는다. 상세 원인별 진단은 다른 문서가 소유한다. |
| L255 | 원장·DB 대조와 복구 필요 판단을 조정 담당자에게 넘긴다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 원장 부재·후퇴 차단 L77~118 | Epoch 발급·Pod 교체가 이 차단 조건을 해소하지 않는다. 상세 원인별 진단은 다른 문서가 소유한다. |
| L255 | 상세 장애 진단은 `00.rag/admin/coordination-troubleshooting.md`가 담당한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 원장 부재·후퇴 차단 L77~118 | Epoch 발급·Pod 교체가 이 차단 조건을 해소하지 않는다. 상세 원인별 진단은 다른 문서가 소유한다. |

## 매뉴얼과 코드가 다른 곳

O §7.3·§11.5의 대상 운영 절차와 현재 코드 사이에 이 문서에서 반대로 쓴 명령·상태는 없다. 아래 원천 설명은 현재 구현 조건으로 보완했다.

| 구분 | 원천 설명 | 현재 코드·확인 근거 | 검색 원문에 쓴 처리 |
|---|---|---|---|
| 보상 진행 판별 | b8faaead 커밋 설명은 phase로 자동 보상 진행과 수동 복구 대기를 구분한다고 서술한다. | UiOperationExecutor L671~688은 보상 발급 전에 항상 COMPENSATING·REPAIR_REQUIRED로 전이한다. 비대상 사유·발급 실패도 그 phase를 유지한다. | phase 단독 판단 대신 UI_COMPENSATION_ISSUED의 compensationId와 관리 API를 대조한다. |
| 재배치 결과 | O §7.3 L1849는 새 Pod가 빈 자리를 잡는다고 간략히 설명한다. | GatewayShardLeaseManager L206~254는 여러 인스턴스가 획득을 시도하고 운용 중 대기 인스턴스가 획득하면 자체 재기동한다. | 특정 Pod가 소유한다고 예상하지 않고 소유권 API를 다시 조회한다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 배포 판본·현재 replica 수·현재 소유 Pod·미점유/복구 작업의 실제 수 | 현장에 접속하지 않았고 저장소 값은 실시간 상태가 아니다. | 배포·클러스터 운영 담당자 |
| 현장 Redis 접속값·실제 인증 방식·현재 cluster Epoch·영속/백업 성공 상태 | 현장 관측 자료가 없으며 자격값은 수록 대상이 아니다. | Redis·보안·인프라 담당자 |
| 복구 필요 작업을 강제 종결하거나 원장/DB를 수동 정합화하는 명령 | 관리 Controller는 읽기 전용이다. 보편적으로 승인된 수동 복구 쓰기 경로·안전한 자료별 재구성 절차를 확인하지 못했다. | 조정 기능 개발·DB·Redis 담당자 |
| 담당자의 개인 연락처·당번 | 원천이 없고 개인정보를 쓰지 않는다. | 운영 인계 담당 역할 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에서 복제하지 않은 사실 |
|---|---|
| 00.rag/user/tc-components-and-terms.md | Shard group·Lease·Pod·spare의 일반 정의 |
| 00.rag/admin/installation-and-service-lifecycle.md | 서비스 설치·시작·중지·확장 명령, 그룹/파티션/replica 설정 변경, 검증 스크립트의 상세 판정식 |
| 00.rag/admin/upgrade-rollback-and-configuration-apply.md | 전환 배포 전체 순서·이미지/화면 되돌림·기능 스위치 변경 조작. 이 문서에는 발급에 필요한 최소 조건만 둔다. |
| 00.rag/admin/backup-and-recovery.md | DB·Redis 자료 백업·복원 조작과 손실 대상 전체 |
| 00.rag/admin/daily-checks-and-log-inspection.md | 일상 cluster Epoch status·진행 중 건수·설정 일치 조회·일반 로그 수집 |
| 00.rag/admin/security-operations.md | Secret·인증서·계정 자격의 교체 절차 |
| 00.rag/admin/coordination-troubleshooting.md | 보상 확정 실패·원장 후퇴의 상세 원인별 장애 진단. 본문은 판별에 필요한 최소 신호와 운영 확인만 소유한다. |
| 00.rag/user/state-reference.md | 전체 상태값·phase 사전 |
| 00.rag/user/permission-code-reference.md | 전체 권한 코드의 뜻과 부여 범위 |
| 00.rag/user/error-message-and-code-reference.md | 전체 오류 문구 사전. 이 문서의 표에는 관리 조회 분기에 필요한 최소 응답만 남겼다. |

겹치는 기존 검색 원문은 설치·변경 반영·백업·일상 점검·보안 운영과 구성 요소·용어 문서를 직접 읽고 소유 범위를 대조했다. 다른 문서를 수정하지 않았다.

## 검사 결과

- 현재 문장·명령·표 행 대조 행: 105개. 최초 작성 때 17절 전체를 대조했고, 이번 보완에서는 원 작업 확정 절과 그 직접 영향을 다시 대조했다.
- 도구: GNU bash 5.2.37(1)-release(x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS.
- 최초 작성에서 실행한 검사기 호출: 정상 Git Bash 3회(초안 FAIL, 보완 PASS, 마지막 편집 후 최종 PASS). 최초 별칭 bash 기동 실패 1회를 별도 기록한다.
- 최초 작성판의 최종 Git Bash 검사 실측 wall time: 19.277초. 이 밖의 전체 작업 시간·provider 토큰·비용은 미측정이며 추정하지 않았다.

```text
C:/Program Files/Git/usr/bin/bash.exe C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/coordination-ownership-and-recovery-check.md
```

최초 작성판(검색 원문 SHA-256 36a8aa77663ecc25e43aa615105879d4e514dde08cd1287d54a3f867bd8e7bb4)의 검사 이력: exit 0. 당시 마지막 출력 10줄. 최신 보완판 결과는 아래 보완 기록에 둔다:

```text
    L236-236 문단 134  | 보상 완료 확정 수정 이후 판본에서는 보상 작업의 DB 확정·장벽 개방·PASS 뒤 원 작업을 FAIL·ROLLED_BACK으로 확정한다. 보상 응답의 compensatesRevision과 원 작업의 desiredRevision도 대조한다.
    L238-238 문단 78  | 운영자는 보상 ID와 원 작업 ID를 각각 재조회해 이 결과를 확인한다. 원 작업의 오류 기록은 유지되고 repairReason은 비워진다.
    L240-240 문단 110  | 원 작업 확정이 실패하면 UI_OPERATION_ROLLBACK_NOT_CONFIRMED 로그를 남기고 복구 필요 신호를 유지한다. 보상 작업의 성공만 보고 원 작업의 복구 완료를 확정하지 않는다.
§ L242-252 [34] Redis 원장이 DB 확정 이력보다 뒤처져 설비가 차단될 때   parent=350 children=3 sources=2
    L248-248 문단 107  | 원장 유실 차단 수정 이후 판본에서 Gateway 기동 로그의 GW_LEDGER_REGRESSION을 확인하면 설비 ID·Redis 확정 revision·DB 확정 revision을 보존한다.
    L250-250 문단 105  | DB 확정 이력이 양수인데 Redis 확정 자료가 없거나 Redis revision이 DB보다 낮으면 그 설비의 적재를 차단한다. Redis 조회 실패도 DB만으로 진행하지 않고 전파한다.
    L252-252 문단 136  | 초기 값 발급이나 Pod 교체로 이 차단을 복구 완료로 처리하지 않는다. 원장·DB 대조와 복구 필요 판단을 조정 담당자에게 넘긴다. 상세 장애 진단은 00.rag/admin/coordination-troubleshooting.md가 담당한다.
SUMMARY sections=17 children=53 maxChild=168 maxParent=443
CORPUS docs=24/53 sections=410 part={admin=7, user=17} type={개념=4, 사용 절차=13, 운영=7}
TOTAL files=24 targets=1 violations=0
```

- VIOLATION 0건·CROSS VIOLATION 0건·MASK 출력 0건. 최종 파일 구조·표·원천 commit:path·줄 범위 검사를 통과했다.
- 제목은 17개 모두 서로 다른 실제 질문·작업이다. 금지 틀 제목·같은 모양을 3회 반복하는 제목 0건이다.
- 17절 전체에 대해 제목의 질문 식별·원천 일치·독립 답·본문/앞선 문서 중복을 읽고 확인했다. 네 판정 모두 17/17 적합이다.
- 일반 정의·전체 배포 절차·일상 점검·상태/권한/오류 사전은 소유 문서로 분리했다. 필요한 최소 실행 조건은 조작과 같은 절에 두었다.
- 빈 틀 문장·이름 토막 뜻풀이·잘린 문장·오답 인용을 넣지 않았다. admin의 내부 조회·변경 권한을 user 범위로 옮기지 않았다.
- 최초 작성판 기계 검사의 mask-v1 적용 결과에 MASK 출력이 없고 당시 53개 자식 본문의 필요한 명령·식별자·값이 유지되는 것을 dump에서 확인했다.
- 제품 build·단위/통합 시험·현장 HTTP·kubectl·Redis·SSH·Docker는 실행하지 않았다. 원천 시험의 기대값 확인을 시험 실행 PASS로 기록하지 않았다.

절별 사람 검사 판정:

| 절 | 제목 질문 식별 | 원천 일치 | 독립 답 | 소유권·중복 |
|---|---|---|---|
| 소유권과 조정 작업을 조회하려면 어떤 권한이 필요한가 | 적합 | 적합 | 적합 | 적합 |
| 설비 화면에서 담당 Pod를 대조하기 | 적합 | 적합 | 적합 | 적합 |
| Gateway 소유권 API로 어느 그룹을 누가 맡는지 읽기 | 적합 | 적합 | 적합 | 적합 |
| 조정 lease와 이전 lease가 함께 남아 있을 때 확인 순서 | 적합 | 적합 | 적합 | 적합 |
| Redis에서 Gateway 소유 세션과 epoch를 직접 읽기 | 적합 | 적합 | 적합 | 적합 |
| Gateway 소유권 재배치 후 무엇을 확인하는가 | 적합 | 적합 | 적합 | 적합 |
| 조정 초기 값을 발급하기 전에 맞출 조건 | 적합 | 적합 | 적합 | 적합 |
| coord-epoch.sh로 발급한 결과 판정하기 | 적합 | 적합 | 적합 | 적합 |
| SSH 경유 없이 Redis 초기 값을 발급하기 | 적합 | 적합 | 적합 | 적합 |
| 미종결 작업 ID를 찾고 복구 대기 누락을 피하기 | 적합 | 적합 | 적합 | 적합 |
| 조정 작업 관리 API에서 결과를 읽기 | 적합 | 적합 | 적합 | 적합 |
| 작업이 없다는 응답과 조정 조회 불가를 구별하기 | 적합 | 적합 | 적합 | 적합 |
| 장벽·참여자 적용·DB 확정을 함께 대조하기 | 적합 | 적합 | 적합 | 적합 |
| Backend replica 중 누가 복구 작업을 진행하는지 확인하기 | 적합 | 적합 | 적합 | 적합 |
| 복구 필요 작업의 자동 보상 발급 여부 확인하기 | 적합 | 적합 | 적합 | 적합 |
| 자동 보상이 끝난 뒤 원 작업의 복구 완료 판정 | 적합 | 적합 | 적합 | 적합 |
| Redis 원장이 DB 확정 이력보다 뒤처져 설비가 차단될 때 | 적합 | 적합 | 적합 | 적합 |

검사 중 발견한 결함과 조치:

- 첫 정상 검사에서 직접 Epoch 발급 절의 부모 517자 초과 1건. 조건·SET·GET을 유지한 채 문장을 줄였고 최종 최대 부모 443자로 확인했다. 최종 최대 자식 168자, 절 17개·자식 53개다.
- 실행 조건 자체 확인 중 작업별 executor 조회의 Redis 대상 변수 지정, 두 발급 절의 구 Pod 종료·스키마 적용 조건을 명확히 했다. 마지막 변경 뒤 검사기를 다시 실행했다.
- 최초 subprocess의 bash 별칭 호출은 검사기 전에 exit 1로 끝났고 stdout/stderr 원문은 UTF-16LE로 다음과 같았다. 실행 경로 문제를 피하려고 사용자 지정 Git Bash의 실제 절대 경로로 후속 호출했다. WSL/서비스 설정은 변경하지 않았다.

```text
액세스가 거부되었습니다. 
오류 코드: Bash/Service/CreateInstance/E_ACCESSDENIED
```

이 기동 오류를 문서 검사 FAIL이나 자동 승인 reviewer 거부로 분류하지 않았다. 정확한 시스템 bash 실행 이미지까지 별도로 측정하지 않았으므로 확인한 오류 코드와 전달된 호출만 기록한다.

임시 로그의 원본 SHA-256 및 결과:

| 파일 | SHA-256 | 결과 |
|---|---|---|
| doc24-author-check-01.log | 03ff77e69e130261b9046d200b0f43df7b4dacc59787c4dc3539bc8bad983754 | 검사기 기동 전 exit 1 |
| doc24-author-check-02.log | 452713be34b14de98595b0aff44944ef9aff33318265c49d293bb493d3c720eb | 위반 1건·exit 1 |
| doc24-author-check-03.log | 04bc34ee76c2e1bb96fb3e022c8a2d6f81f98bb78e2bea14262930796ffac85e | 위반 0건·exit 0 |
| doc24-author-check-final.log | 2ade8304decdf8352f3df4a135a01607ce04b14e32595f8e25fccd7b028893d9 | 위반 0건·exit 0 |

초안 정상 검사 마지막 10줄:

```text
    L235-235 문단 78  | 운영자는 보상 ID와 원 작업 ID를 각각 재조회해 이 결과를 확인한다. 원 작업의 오류 기록은 유지되고 repairReason은 비워진다.
    L237-237 문단 110  | 원 작업 확정이 실패하면 UI_OPERATION_ROLLBACK_NOT_CONFIRMED 로그를 남기고 복구 필요 신호를 유지한다. 보상 작업의 성공만 보고 원 작업의 복구 완료를 확정하지 않는다.
§ L239-249 [34] Redis 원장이 DB 확정 이력보다 뒤처져 설비가 차단될 때   parent=350 children=3 sources=2
    L245-245 문단 107  | 원장 유실 차단 수정 이후 판본에서 Gateway 기동 로그의 GW_LEDGER_REGRESSION을 확인하면 설비 ID·Redis 확정 revision·DB 확정 revision을 보존한다.
    L247-247 문단 105  | DB 확정 이력이 양수인데 Redis 확정 자료가 없거나 Redis revision이 DB보다 낮으면 그 설비의 적재를 차단한다. Redis 조회 실패도 DB만으로 진행하지 않고 전파한다.
    L249-249 문단 136  | 초기 값 발급이나 Pod 교체로 이 차단을 복구 완료로 처리하지 않는다. 원장·DB 대조와 복구 필요 판단을 조정 담당자에게 넘긴다. 상세 장애 진단은 00.rag/admin/coordination-troubleshooting.md가 담당한다.
SUMMARY sections=17 children=54 maxChild=168 maxParent=517
VIOLATION L119 부모 길이 517 (1~450)
CORPUS docs=24/53 sections=410 part={admin=7, user=17} type={개념=4, 사용 절차=13, 운영=7}
TOTAL files=24 targets=1 violations=1
```

## 임시 자원 정리

검사 로그 4개는 이 세션이 지정 work 폴더에 생성했다. 위 결과·오류 원문·해시·최종 출력을 보존한 뒤 정리했다. 기존 작업 부모와 다른 세션 파일은 삭제하지 않았다.

- 정확한 대상 부모: `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-24-coordination-ownership-and-recovery-check/work/`.
- 대상 파일은 위 임시 로그 표의 4개뿐이다. work 폴더의 기존 항목은 처음 조회 시 0개였다.
- 사전 점검 명령의 `Get-CimInstance Win32_Process -ErrorAction Stop`은 exit 1이었다. 원문은 `액세스가 거부되었습니다.`, `PermissionDenied: (root\cimv2:Win32_Process:String) [Get-CimInstance], CimException`, `HRESULT 0x80041003,Microsoft.Management.Infrastructure.CimCmdlets.GetCimInstanceCommand`이다. 그 지점에서 멈췄으므로 삭제 명령은 아직 실행되지 않았다.
- 확인한 실패 범위는 CIM 프로세스 목록 조회다. 임시 파일 삭제 거부나 자동 승인 reviewer 거부로 단정하지 않았다. CIM 권한 부족의 상세 설정 원인은 미확인이다. 네트워크 금지 범위를 지켜 외부 문서 조회를 추가하지 않았다.
- 별도의 정상 파일 사용 확인으로 Python 3.12.10의 ctypes에서 Windows `Rstrtmgr.dll`의 `RmStartSession`·`RmRegisterResources`·`RmGetList`·`RmEndSession`을 호출했다. 반환은 모두 0, 필요한/반환된 프로세스 수 0/0, rebootReasons 0이었다. 종료·잠금 해제 API는 호출하지 않았다.
- work의 속성은 16(Directory), 네 파일은 모두 32(Archive)였다. ReadOnly·Hidden·System·ReparsePoint 비트가 없고 경로가 지정 부모 안인 것을 확인했다.
- PowerShell 5.1.26100.9444에서 검증한 네 절대경로 각각에 `Remove-Item -LiteralPath $cleanupPath -ErrorAction Stop`을 실행했다. Force·Recurse를 사용하지 않았다. 명령 exit 0이었다.
- 삭제 뒤 네 경로 모두 `Test-Path -LiteralPath`가 False, work의 잔여 항목 수는 0이었다. 원래 존재한 빈 work 폴더는 보존했다.

## 메인 세션 지적 1건 보완 (2026-11-19)

- 원인: 원 작업 확정의 거부 코드 반환 분기에서만 남기는 로그를 모든 확정 실패에 적용되는 것으로 서술했다. markRolledBack 호출 예외의 전파와 reconciler의 별도 로그 경로를 빠뜨렸다.
- 직접 수정: 기존 절을 유지하고 거부 코드 반환과 호출 예외를 구별했다. UiOperationReconciler L65~72를 rag-meta와 문장 대조 행에 추가했다. 상세 진단은 coordination-troubleshooting.md의 기존 소유 절을 읽고 문서 경로만 안내했다.
- 코드 재확인: nori-tc-ui-backend HEAD 985d17c604dd, UiOperationExecutor.java·UiOperationReconciler.java 두 경로의 git status --short 출력 공백·exit 0. Executor L554~561·L578~592와 Reconciler L65~72를 직접 읽었다.
- 검색 원문 이전 SHA-256: 36a8aa77663ecc25e43aa615105879d4e514dde08cd1287d54a3f867bd8e7bb4. 보완 후 SHA-256: 2677c882740a639cc89cf5d6e7869936b3a1fbad95dad88ef61b119527440a0d.
- 최종 검색 원문: 255줄·17절. 사실 대조 표 105행. 마지막 절의 줄 번호 이동도 반영했다.
- 변경 절 사람 검사: 제목의 질문 식별·원천 일치·독립 답·중복/소유권 경계를 재확인했다. 거부 코드와 예외를 혼동하는 문장을 제거했고 상세 진단 설명을 복제하지 않았다.
- 이번 보완의 검사기 실행: 지정 Git Bash 절대경로로 --dump 1회, exit 0, wall time 17.317초. 임시 검사 파일을 만들지 않고 출력을 회수해 기록했다.
- 이번 보완의 VIOLATION·CROSS VIOLATION·MASK 출력은 모두 0건이다. 제품 시험·현장 명령·서버 접속은 수행하지 않았다. 별도 Agent 호출 0회이며 provider 사용량·비용은 미확인이다.

최종 편집 뒤 검사 명령은 위에 기록한 동일 check.sh --dump 명령이다. 최신 출력 마지막 10줄:

```text
    L239-239 문단 78  | 운영자는 보상 ID와 원 작업 ID를 각각 재조회해 이 결과를 확인한다. 원 작업의 오류 기록은 유지되고 repairReason은 비워진다.
    L241-241 문단 115  | 원 작업 확정이 거부 코드를 반환하면 UI_OPERATION_ROLLBACK_NOT_CONFIRMED가 남는다. 호출 예외는 그 로그 대신 UI_OPERATION_ADVANCE_FAILED가 남을 수 있다.
    L243-243 문단 86  | 보상 성공만으로 원 작업의 복구를 확정하지 않는다. 상세 진단은 00.rag/admin/coordination-troubleshooting.md를 따른다.
§ L245-255 [34] Redis 원장이 DB 확정 이력보다 뒤처져 설비가 차단될 때   parent=350 children=3 sources=2
    L251-251 문단 107  | 원장 유실 차단 수정 이후 판본에서 Gateway 기동 로그의 GW_LEDGER_REGRESSION을 확인하면 설비 ID·Redis 확정 revision·DB 확정 revision을 보존한다.
    L253-253 문단 105  | DB 확정 이력이 양수인데 Redis 확정 자료가 없거나 Redis revision이 DB보다 낮으면 그 설비의 적재를 차단한다. Redis 조회 실패도 DB만으로 진행하지 않고 전파한다.
    L255-255 문단 136  | 초기 값 발급이나 Pod 교체로 이 차단을 복구 완료로 처리하지 않는다. 원장·DB 대조와 복구 필요 판단을 조정 담당자에게 넘긴다. 상세 장애 진단은 00.rag/admin/coordination-troubleshooting.md가 담당한다.
SUMMARY sections=17 children=54 maxChild=168 maxParent=443
CORPUS docs=36/53 sections=511 part={admin=9, user=27} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=12}
TOTAL files=36 targets=1 violations=0
```

보완 완료: 지적 1건 해소, 확인된 미해소 결함 0건. 기존 검사·정리 이력은 보존했다. 이번 보완에서 새로 만든 임시 자원은 없다.

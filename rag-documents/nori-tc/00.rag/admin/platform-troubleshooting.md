# TC 플랫폼 자원·인증서·기동 장애 진단
```rag-meta
유형: 트러블슈팅
적용: nori-tc-exec f1d5e0a8e727·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-common 6fe71f6b2da6·nori-tc-ui-front 9fe69a75dcd3 소스 기준, create-k8s-secrets.sh 1d2879d14406
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/admin/incident-evidence-handover.md
```

## Gateway가 Running인데 Ready가 아니면 장애인가
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; attemptAcquireBlocking L200~222
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayRuntimeHealthIndicator.java @ d3636ab86763 ; health L49~63
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; startup·readiness·liveness L63~95
```

조정 기능을 켠 Gateway에서는 그룹을 얻지 못한 대기 Pod와 실제 장애를 구별한다. 기동 로그의 `Instance entered spare mode`와 소유권 배정을 대조한다. 소유권 조회는 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다.

런타임 상태 `OUT_OF_SERVICE`는 소유권 준비 실패뿐 아니라 처리 포화에서도 나온다. Ready 해제만으로 정상 대기라고 단정하지 않는다.

대기 Pod 기동 검사 수정 이후 차트는 startup·liveness에서 `DOWN` 여부와 파일 갱신을 검사한다. readiness만 `UP`을 요구한다. 반복 재시작이면 적용 차트의 검사식과 앱 이미지 판본을 배포 담당자에게 넘긴다.

## Gateway 기동 검사가 계속 실패하면 무엇을 읽는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 마커 경로·시간 L68~74
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 마커 검사 L66~91
원천: nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeHealthMarkerWriter.java @ 6fe71f6b2da6 ; writeMarkerOnce·resolveStatusCode L112~138
```

운영 담당자는 적용한 `probes.markerPath`의 파일 존재·상태·수정 시각을 구별한다. 기본 경로는 `/app/run/runtime-health`이고 기본 `staleAfterSeconds`는 45다. 파일 부재와 갱신 중단도 기동 검사 실패 원인이다.

마커는 상태와 epoch 초를 한 줄에 기록한다. 판정 호출의 예외는 `DOWN`이 되고, 파일 쓰기 실패는 `런타임 health 마커 파일 기록 실패`로 드러난다. 상태만 보지 말고 이전 컨테이너 로그의 첫 오류도 확인한다.

마커를 쓰지 않는 옛 이미지와 새 차트 조합이면 이미지·차트 호환 문제로 인계한다. 로그 조회 명령은 `00.rag/admin/daily-checks-and-log-inspection.md`에서 확인한다.

## Kafka 발행기를 만들지 못하면 브로커 정지인가
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet L44~62
원천: nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 발행기 시간 설정 L44~53
```

`Failed to construct kafka producer`가 있으면 안쪽 `ConfigException`을 먼저 읽는다. 시간 관계 위반이면 브로커 복구보다 `tc-messaging.properties`의 발행기 설정을 조사한다.

`spring.kafka.producer.properties.` 뒤의 `delivery.timeout.ms`는 `request.timeout.ms + linger.ms` 이상이어야 한다. 별도로 앱 발행 대기 초×1000도 delivery 이상이어야 한다.

linger 명시 수정 이후 사본은 delivery 30000ms·request 30000ms·linger 0ms다. Business는 delivery 미설정·대기 부족에서 기동을 거절한다. 설정 변경은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`를 따른다.

## 새 이미지인데 Kafka 설정 결함이 다시 나타나는 이유
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 연결 L172~179
원천: nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 메시징 설정 읽기 L15~26
```

이 수정이 들어간 판본의 `create-k8s-secrets.sh`는 `spring.kafka.producer.properties.linger.ms=0`을 Gateway·Business·UI Backend 메시징 생성부에 명시한다. 이미 만든 Secret은 이 수정으로 바뀌지 않는다. 새 이미지로 교체해도 기존 Secret 값은 따로 확인한다.

운영 담당자는 `tc-gateway-config`·`tc-business-config`·`tc-ui-backend-config`의 `tc-messaging.properties`를 제한된 관리 경로에서 대조한다. 비밀값은 출력하지 않는다.

운영 중에는 생성 스크립트 재실행 대신 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`의 파일 키별 변경·재반영 절차로 넘긴다. 배포 담당자에게 이미지 태그와 누락된 설정 키를 전달한다.

## Kafka 메시지가 멈추면 연결·인증·적체를 어떻게 가르는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공·실패 L145~179
```

운영 담당자는 전달 중단 시각의 앱 로그에서 연결 실패·인증 실패와 발행 실패 예외를 구별한다. Business의 MES 발행 성공 로그는 partition·offset을 남긴다. 실패 로그와 같은 시각의 원인 예외를 Kafka 담당자에게 전달한다.

Kafka 담당자는 앱 접속 대상·보안 규약과 브로커 상태를 대조한다. 자격·신뢰 파일 문제면 `00.rag/admin/security-operations.md`로 넘기고, 브로커 정지가 확인되면 브로커 운영 절차로 복구한다.

연결 회복 뒤에는 소비 그룹의 LAG와 장애 구간 메시지 보관 범위를 함께 확인한다. 처리량 회복이나 LAG 0만으로 장애 구간의 모든 작업이 처리됐다고 판정하지 않는다. 보관·재처리 범위는 `00.rag/admin/backup-and-recovery.md`를 따른다.

## DB 조회 실패와 스키마 기동 실패를 구별하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 정지·연결 확인 L2351~2377
원천: nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 앱 스키마 검증 L89~93
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 외부 migration L50~68
```

운영 담당자는 조회·저장 실패가 난 앱과 새 Pod의 기동 오류를 나눠 수집한다. DB 담당자는 앱과 같은 접속 대상·자격·경로에서 `SELECT 1;` 결과와 오류를 확인한다. 접속 실패만으로 DB 서버 정지를 확정하지 않는다.

DB에 접속되는데 앱만 실패하면 앱의 자격·연결 수와 연결 풀 고갈 기록을 대조한다. 자격 변경은 `00.rag/admin/security-operations.md`, 설정 키는 `00.rag/admin/configuration-reference.md`를 따른다.

스키마 검증 오류는 접속 시험 성공과 별개다. 앱의 `ddl-auto=validate`와 외부 Flyway 적용 이력을 대조해 DB 담당자에게 넘긴다. 스키마 적용 순서는 `00.rag/admin/installation-and-service-lifecycle.md`에서 확인한다.

## V3 migration이 이미 있는 객체에서 실패하면 무엇을 대조하는가
```rag-meta
원천: nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 컬럼·테이블·인덱스 생성 L19~44
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; migration 입력 L61~68
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 새 보상 migration L2587~2605
```

V3에서 객체가 이미 있다는 오류가 나면 실행한 패키지의 SQL과 Flyway 이력, 기존 객체 정의를 DB 담당자가 대조한다. 대상은 `tc_user_info.password_changed_at`·`tc_user_password_history`·`idx_tc_user_password_history_user`다.

V3 중복 생성 방지 수정 이후 SQL은 세 객체 생성에 `IF NOT EXISTS`를 쓴다. 기존 객체의 형식까지 교정하는 구문은 아니므로 객체 존재만으로 올바른 스키마라고 판정하지 않는다.

적용 이력·실패 SQL 위치·객체 정의와 패키지 판본을 넘긴다. 적용된 V 파일 편집이나 이력 삭제로 재시도하지 않고 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`의 스키마 변경 절차로 인계한다.

## Redis 장애로 Gateway가 스스로 내려가는 경우
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 실패 L206~221
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431
```

조정 기능을 켠 Gateway에서 `GW_SHARD_SELF_FENCE`가 있으면 reason을 읽는다. `LEASE_ACQUIRE_REDIS_UNCERTAIN`은 획득 호출 실패, `LEASE_HEARTBEAT_REDIS_UNCERTAIN`은 갱신 호출 실패 경로다. 그룹을 얻지 못한 정상 대기와 구별한다.

이 경로는 세션·소유권을 무효화하고 종료를 요청한다. 운영 담당자는 원인 예외와 Redis 장애 시각을 맞춰 Redis 담당자에게 전달한다. 재시작 횟수 증가만 보고 메모리 부족으로 분류하지 않는다.

Redis 접근 회복 뒤에도 미점유 그룹이 남으면 `00.rag/admin/coordination-ownership-and-recovery-check.md`의 소유권 점검으로 넘긴다. 자료 손실이 있으면 `00.rag/admin/backup-and-recovery.md`로 구분해 인계한다.

## Redis에 접속되는데 조정 기능 기동이 거절될 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; 초기 값 필수 검사 L40~51
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실제 대기 한도 검사 L259~274
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 관계식·오류 L98~126
```

Gateway의 `cluster epoch is not issued yet`는 `tc:coord:v1:cluster:epoch` 미발급 경로다. 발급·손실 복구는 `00.rag/admin/coordination-ownership-and-recovery-check.md`로 넘긴다.

`coordination lease safety relation violated`는 실제 Redis 명령 대기 시간과 소유권 수명 관계가 맞지 않는 오류다. 오류의 lease 종류·시간값과 `spring.data.redis.timeout`을 대조한다.

`GW_COORDINATION_REDIS_TIMEOUT_MISMATCH`는 경고다. 선언값이 달라도 실제값으로 관계식을 검사한다. 대기 한도 준비는 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다.

## 여러 설비가 동시에 무응답이면 어느 경로를 조사하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설비 무응답 L2393~2407
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/service.yaml @ f1d5e0a8e727 ; selector·포트 연결 L11~20
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; listener·Service 연결 L1~21
```

여러 설비의 동시 무응답은 영향 설비와 담당 Gateway 그룹을 묶어 조사한다. Gateway 실행·소유권과 Kafka 전달 상태를 먼저 대조한다. 한 대의 등록값·명령 문제는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`에서 구별한다.

대기 연결 설비는 설치 담당자가 설비 포트와 Service의 port·targetPort, TCPRoute의 backendRefs.port·parentRefs.sectionName을 대조한다. 공통 listener나 연결 대상의 불일치는 개별 설비 재등록보다 경로 수정으로 인계한다.

Route·Service 변경과 기동 검증은 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다.

## 반복 재시작에서 메모리 부족을 확인하는 근거
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메모리 부족 L2409~2437
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 자원·JVM 입력 L54~60 L161~167
원천: nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; JAVA_OPTS 실행 L35~37
```

운영 담당자는 Pod 상세의 종료 사유와 직전 로그, 종료 전 메모리 사용량을 함께 확인한다. 메모리 부족 종료인지 기동 검사 실패·앱 예외에 따른 종료인지 구별한 뒤 조치한다. 재시작 전 로그 조회는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다.

배포 담당자에게 해당 컨테이너의 실제 메모리 상한과 `app.resources.limits.memory`·`requests.memory`를 전달한다. 제공 Gateway 차트의 JVM 옵션은 `MaxRAMPercentage=75.0`이며 Dockerfile은 JAVA_OPTS를 실행에 사용한다.

메모리 상한 변경은 환경별 값 파일에 반영하고 재배포한 뒤 사용량·재시작을 관찰한다. 적용·되돌림은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`를 따른다.

## 공간 부족 오류는 노드·이미지·DB 중 어디 문제인가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 디스크 포화 L2442~2454
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L172~179
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L166~173
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L161~168
```

로그·임시 파일 쓰기 실패면 해당 Pod가 있는 노드의 공간을, 이미지 내려받기·저장 실패면 레지스트리를, 여러 앱의 DB 쓰기 실패면 DB 저장소를 나눠 조사한다. 오류가 난 저장소와 실제 여유 공간을 함께 대조한다.

제공된 Gateway·Business·Backend 차트의 볼륨은 설정 Secret용이며 로그 보관 볼륨이 없다. 컨테이너 로그 파일과 외부 DB·레지스트리 저장소를 같은 삭제 대상으로 취급하지 않는다.

노드·레지스트리·DB 담당자에게 저장소 종류와 쓰기 오류를 각각 넘긴다. 필요한 장애 로그를 보존한 뒤 담당 저장소의 정상 회수 절차를 적용하고 쓰기 회복을 확인한다. 로그 위치는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다.

## 화면 인증서 신뢰 실패와 만료를 구별하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증서 진단 L2456~2475
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L7~21
```

화면 HTTPS 접속에서 신뢰 실패가 나면 접속 호스트·제시된 인증서 만료 시각·부모 Gateway의 인증서 참조를 대조한다. 신뢰 실패 문구만으로 만료를 확정하지 않는다. 만료 시각 조회는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다.

화면 차트의 HTTPRoute는 부모 Gateway와 호스트를 연결한다. 부모 Gateway 담당자에게 호스트·실패 시각·검증 오류와 인증서 참조 불일치 여부를 넘긴다. 인증서 내용과 개인 키는 인계 자료에 넣지 않는다.

만료 또는 참조 오류가 확인되면 `00.rag/admin/security-operations.md`의 교체 경로로 넘긴다. 교체 뒤에는 같은 화면 호스트의 HTTPS 접속과 연결 수립을 다시 확인한다.

## 연결·끊김 보고가 재실행될 때 중복 여부를 가르는 값
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652
원천: nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeConnectionShortCircuitTest.java @ 7db24f7466bd ; 같은·다른 참조와 발행 실패 기대값 L132~254
원천: nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89
```

연결 보고 중복 방지 수정 이후 판본은 payloadRef와 처리기 식별자를 묶어 완료 기록을 확인한다. 같은 입력의 완료 기록이 남아 있으면 `CHECKPOINT_SKIPPED`를 남기고 발행을 건너뛴다. payloadRef가 다르면 별도 메시지다.

현재 코드는 처리 성공 뒤에만 TTL이 있는 완료 기록을 저장한다. 운영 담당자는 재전달 전후의 payloadRef·handlerId·messageName·설비 ID와 완료 기록의 보존 여부를 대조해 Business 담당자에게 넘긴다. 원문 payload는 출력하지 않는다.

새 연결 사건과 같은 메시지의 재전달을 구별한다. 처리 뒤 저장 전 실패나 완료 기록 만료·손실까지 중복이 없다고 보장하는 구조는 아니다.

## Business 연결 보고가 Redis 오류나 입력 충돌로 멈출 때
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652
원천: nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 조회·충돌·기록 실패 L43~89
원천: nori-tc-business/business/adapter/tc-business-redis-adapter/src/test/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStoreTest.java @ 7db24f7466bd ; 실패 전파 기대값 L62~83 L110~118
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공 위치 L145~165
```

현재 판본의 `workflow checkpoint lookup failed`·`workflow checkpoint record failed`는 Redis 조회·기록 실패다. 기록 실패를 경고만 남기고 성공으로 끝내지 않으므로 Redis 접근 오류와 보고 처리 오류를 분리해 조사한다.

`connection report checkpoint input digest conflict`는 같은 식별자에 다른 입력이 들어온 경로다. 완료 기록을 지워 재실행하지 말고 payloadRef·handlerId와 충돌 로그를 Business 담당자에게 넘긴다.

Redis 저장 실패 전 발행이 이뤄졌는지는 별도로 확인한다. MES 발행 로그의 partition·offset과 재전달 기록을 대조한다. 과거 수정 커밋의 동작과 현장 판본을 같다고 단정하지 않고 이미지에 포함된 소스 판본을 인도 담당자에게 확인한다.

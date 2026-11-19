# TC 앱·Secret·Helm 설정 항목
```rag-meta
유형: 참조
적용: nori-tc-exec f1d5e0a8e727 및 create-k8s-secrets.sh 1d2879d14406·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-ui-backend 985d17c604dd·nori-tc-simulator 5e9b02e3ae16·nori-tc-ui-front 9fe69a75dcd3·nori-tc-common 6fe71f6b2da6·nori-common ae6791e99e91 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/admin/upgrade-rollback-and-configuration-apply.md
```

## 설정값의 출처와 변경 절차를 구별하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설정 계층·운영 항목 L1482~1556
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 앱 설정 생성 L98~139
```

이 문서의 수치는 저장소 설정이나 코드 기본값이다. 현장에 적용된 값은 설치한 Helm values와 대상 Secret의 파일 키에서 별도로 확인한다. Secret의 자격 값은 출력하거나 문서에 옮기지 않는다.

변경 반영·되돌림은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`, 자격·인증서 교체는 `00.rag/admin/security-operations.md`가 담당한다.

## 공통 설정은 어느 파일 키에서 찾는가
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 공통 파일·로그·그룹 정의 L22~55
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 파일 연결 L204~211
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business·Backend 연결 L299~305
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 연결 L410~418
원천: nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 ; 통신 기록 키 L77~84
```

Gateway·Business·Backend의 설정 Secret에서 파일 키와 역할을 구별한다. 리소스별 파일 목록과 자격 교체는 `00.rag/admin/security-operations.md`에서 찾는다.

| 파일 키 | 담당 설정 |
|---|---|
| tc-db.properties | DB 연결·JPA 설정 |
| tc-messaging.properties | 앱별 Kafka 연결·발행·소비 설정 |
| tc-redis.properties | Redis 연결·앱별 Redis 저장 설정 |
| tc-log.properties | 파일 로그·통신 기록 설정 |
| tc-shard.properties | Gateway·Backend가 읽는 그룹별 담당 파티션 |

## Gateway가 읽는 앱 설정과 순서
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 설정 가져오기 L19~26
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 앱별 키 L204~211
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 연결 L172~179
```

Gateway의 파일 가져오기 순서는 `tc-db.properties` → `tc-messaging.properties` → `tc-redis.properties` → `tc-log.properties` → `tc-shard.properties` → `tc-gateway.properties`이다.

앱 전용 `tc.gateway.*` 설정은 `tc-gateway.properties`에서 찾는다. 차트는 설정 파일을 `/app/config`에 연결하고 앱은 `config/` 상대경로로 읽는다. 파일 참조는 `optional:file:`이므로 파일 누락과 필수 값 검증 실패를 구별한다.

## Business가 읽는 앱 설정과 순서
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 설정 가져오기 L12~18
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 앱별 키 L299~305
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 연결 L166~173
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/config/BusinessCoreRuntimeProperties.java @ 7db24f7466bd ; 필수 런타임 검증 L214~222
```

Business는 `tc-db.properties` → `tc-messaging.properties` → `tc-redis.properties` → `tc-log.properties` → `tc-business.properties` 순으로 가져온다.

`tc.business.runtime.*`와 `tc.business.kafka.*`는 `tc-business.properties`에 있다. Business의 기본 가져오기 목록에는 `tc-shard.properties`가 없다. 파일 참조는 선택 사항이지만 필수 런타임 값이 없으면 설정 검증에서 기동을 거부한다.

## Backend의 프로파일 파일은 언제 읽는가
```rag-meta
원천: nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 가져오기 순서 L38~49
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 활성 프로파일 L85~94
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 빈 prod 파일·키 연결 L407~418
```

Backend는 `tc-db.properties` → `tc-messaging.properties` → `tc-redis.properties` → `tc-log.properties` → `tc-shard.properties` → `tc-ui-backend.properties` 순으로 가져온다.

마지막으로 `tc-ui-backend-${spring.profiles.active}.properties`를 가져온다.

Helm의 `app.profile`이 `SPRING_PROFILES_ACTIVE`로 전달된다. `prod`이면 파일 키는 `tc-ui-backend-prod.properties`이다. 생성 스크립트는 이 파일을 비워 두고 앱 설정은 `tc-ui-backend.properties`에 넣는다.

## Kafka 발행 대기와 전달 한도는 어떻게 맞추는가
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 발행값 L70~71
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 대기 L139~139
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 전달 한도 L226~227
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 대기 L263~263
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 전달 한도 L321~321
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 대기 L355~355
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayPublishTimeoutInvariantValidator.java @ d3636ab86763 ; validate L55~79
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet L43~62
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/config/UiPublishTimeoutInvariantValidator.java @ 985d17c604dd ; validate·정수 변환 L55~93
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/config/UiKafkaAdapterModuleConfiguration.java @ 985d17c604dd ; 검증기 등록 L14~21
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/config/UiKafkaPublishProperties.java @ 985d17c604dd ; 기본 대기·양수 검증 L22~49
```

| 설정 항목 | 저장소 값·역할 |
|---|---|
| tc.gateway.kafka.publish-timeout-seconds | 30초, Gateway 발행 완료 대기 |
| tc.business.kafka.publish-timeout-seconds | 30초, Business 발행 완료 대기 |
| tc.ui.backend.kafka.publish-timeout-seconds | 30초, Backend 발행 완료 대기, tc-ui-backend.properties |

전달 한도와의 관계는 「Kafka 전달 한도의 기동 거부 조건」에서 설명한다.

## Kafka 전달 한도의 기동 거부 조건
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 전달 한도 L70~70
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 전달 한도 L226~226
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 전달 한도 L321~321
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayPublishTimeoutInvariantValidator.java @ d3636ab86763 ; validate·정수 변환 L55~93
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet·정수 변환 L43~70
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/config/UiPublishTimeoutInvariantValidator.java @ 985d17c604dd ; validate·정수 변환 L55~93
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/config/UiKafkaAdapterModuleConfiguration.java @ 985d17c604dd ; 검증기 등록 L14~21
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 전달·linger 생성값 L70~73
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 전달·linger 생성값 L228~231
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 전달·linger 생성값 L325~328
```

| 설정 항목 | 저장소 값·역할 |
|---|---|
| spring.kafka.producer.properties.delivery.timeout.ms | 30000 ms, 세 앱의 producer 전달 한도 |
| spring.kafka.producer.properties.linger.ms | 0 ms, 이 수정이 들어간 판본의 세 앱 메시징 생성값 |

Gateway·Business·Backend는 전달 한도 미설정·빈 값·정수 변환 실패 또는 발행 대기(초×1000)가 전달 한도보다 작으면 기동을 거부한다. 전달 한도는 `tc-messaging.properties`에 있다.

linger 설정 추가는 새로 생성할 메시징 파일에 적용한다. 이미 만들어진 Secret의 값은 이 수정으로 바뀌지 않는다.

## Gateway 명령 파티션 수를 바꾸는 설정
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 명령 파티션 수 L127~127
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 개수 검사 L156~166
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 라우팅 일치 L197~227
```

`tc.gateway.kafka.commands-partition-count`는 `tc-gateway.properties`의 명령 파티션 수이며 생성 스크립트 값은 6이다. 명령 토픽의 실제 파티션 수와 다르면 Gateway가 기동을 거부한다. Gateway의 UI 이벤트 토픽도 명령 토픽과 같은 파티션 수여야 한다.

그룹별 담당 파티션과 변경 시 함께 맞출 대상은 `00.rag/admin/installation-and-service-lifecycle.md`, 토픽별 계약은 `00.rag/admin/kafka-topic-and-message-reference.md`가 담당한다.

## Gateway의 수신 대기량과 처리 스레드
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 런타임 입력 L98~104
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayRuntimeProperties.java @ d3636ab86763 ; 필수 값 검증 L77~111
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/runtime/mailbox/EquipmentMailboxRegistry.java @ d3636ab86763 ; 큐 생성 L200~210
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/processing/EquipmentProcessingCoordinator.java @ d3636ab86763 ; 처리 풀 L110~130
```

`tc-gateway.properties`의 두 항목은 누락되거나 0 이하이면 기동을 거부한다.

| 설정 항목 | 생성 스크립트 값 | 영향 |
|---|---|---|
| tc.gateway.runtime.inbound-queue-capacity | 2048개 | 설비별 수신 큐의 용량을 정한다. |
| tc.gateway.runtime.worker-threads | 8개 | 설비 메시지를 처리하는 공유 스레드 수를 정한다. |

## Gateway 소유권의 수명과 갱신 간격
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Lease 설정 L133~137
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayLeaseProperties.java @ d3636ab86763 ; validate L71~83
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 L206~233
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신 주기 L300~314
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 수명 연장 L357~368
```

| 설정 항목 | 생성 스크립트 값 | 영향 |
|---|---|---|
| tc.gateway.lease.ttl-ms | 15000 ms | 그룹 소유권 획득·연장 때 Redis에 전달하는 수명이다. |
| tc.gateway.lease.heartbeat-ms | 5000 ms | 소유 중 다음 갱신까지 기다리는 간격이다. |

두 값은 `tc-gateway.properties`에 둔다. 누락·0 이하 또는 갱신 간격이 수명 이상이면 기동을 거부한다. Redis 응답이 불확실하면 갱신 성공으로 간주하지 않고 처리를 차단한다.

## Gateway 접속 대기와 프로토콜 설정의 경계
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 접속 대기 L179~186
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; 연결 옵션 L232~245
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; HSMS·Socket 설정 L43~71
```

`tc.gateway.netty.connect-timeout-millis`는 `tc-gateway.properties`의 접속 대기 설정이다. 생성 스크립트 값은 5000 ms이며 Gateway가 먼저 연결하는 설비의 Netty 연결 옵션에 적용한다.

같은 파일의 HSMS 타이머·Socket 프레임 값은 `00.rag/user/secs-hsms-message-and-timer-reference.md`와 `00.rag/user/socket-framing-and-parsing-reference.md`가 설명한다.

## Redis 명령 대기를 바꾸면 기동에도 영향을 주는가
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실측 검증 L259~275
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 안전 관계 L98~126
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationConfiguration.java @ 7db24f7466bd ; 실측 검증 L132~146
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationProperties.java @ 7db24f7466bd ; 안전 관계 L125~138
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/config/UiRedisProperties.java @ 985d17c604dd ; 기본값·검증 L28~54
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/config/UiRedisConfiguration.java @ 985d17c604dd ; client 설정 L296~309
```

`spring.data.redis.timeout`은 `tc-redis.properties`의 Redis 명령 대기 한도이다. Backend 코드 기본값은 1초이며 실제 client에 전달한다.

조정 기능이 켜진 Gateway·Business는 실제 client의 명령 대기를 읽어 소유권 안전 관계를 검증한다. 명령 대기·자기 차단 시간·시계 오차 여유의 합이 소유권 수명에서 갱신 간격을 뺀 값 이상이면 기동을 거부한다. 단순히 긴 대기값으로 바꾸면 기동 조건도 달라진다.

## Business의 작업 대기 용량과 병렬 처리
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 런타임 값 L268~276
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/config/BusinessCoreRuntimeProperties.java @ 7db24f7466bd ; 필수 검증 L190~223
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeEngine.java @ 7db24f7466bd ; 큐 구성 L128~134
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeLifecycle.java @ 7db24f7466bd ; worker 풀 L95~112
```

`tc-business.properties`에서 작업 대기량과 실행 스레드를 따로 조절한다. 두 값은 누락·0 이하이면 기동을 거부한다.

| 설정 항목 | 생성 스크립트 값 | 영향 |
|---|---|---|
| tc.business.runtime.mailbox-capacity | 2048개 | 작업 메일박스의 대기 용량이다. |
| tc.business.runtime.worker-threads | 8개 | 업무 작업을 실행하는 스레드 수이다. |

## Business 작업 시간 제한은 즉시 종료를 보장하는가
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 작업 시간 L274~274
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/config/BusinessCoreRuntimeProperties.java @ 7db24f7466bd ; 시간 양수 검증 L214~222
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; executeWithTimeout L402~420
```

`tc.business.runtime.task-timeout-ms`는 `tc-business.properties`의 작업 시간 한도이며 생성 스크립트 값은 180000 ms이다. 누락·0 이하이면 기동을 거부한다.

시간 한도는 실행 중 worker에 인터럽트를 보내는 데 사용한다. 인터럽트에 반응하지 않는 I/O는 계속 worker를 점유할 수 있으므로 DB·Redis의 I/O 대기 설정도 영향을 준다. 이 값만 줄여 모든 작업을 그 시각에 강제 종료할 수는 없다.

## Business 재시도 정책을 공급하는 항목
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 재시도 값 L274~276
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/config/BusinessCoreRuntimeProperties.java @ 7db24f7466bd ; 필수 검증 L214~222
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeEngine.java @ 7db24f7466bd ; 재시도 정책 구성 L128~137
원천: nori-common/modules/nori-common-consumer-runtime/src/main/java/com/nori/common/consumer/runtime/FixedRetryPolicy.java @ ae6791e99e91 ; 최초 실행 포함·재시도 판정 L18~51
```

| 설정 항목 | 생성 스크립트 값 | 사용처 |
|---|---|---|
| tc.business.runtime.retry-max-attempts | 3 | 최초 실행을 포함한 최대 시도 수이다. |
| tc.business.runtime.retry-backoff-ms | 200 ms | 업무 작업 재시도 정책의 대기 간격으로 전달한다. |

두 항목은 `tc-business.properties`에 둔다. 최대 시도 수는 양수, 대기 간격은 0 이상이어야 한다. 실제 재시도 여부는 작업 처리 정책도 함께 결정한다.

## Backend 비동기 설비 요청의 대기 한도
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 비동기 대기 L361~363
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/config/UiDualRequestProperties.java @ 985d17c604dd ; 의미·검증 L9~56
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 대기 전달 L320~440
```

`tc.ui.backend.async.dual-request-timeout-ms`는 `tc-ui-backend.properties`에 두며 생성 스크립트 값과 코드 기본값은 10000 ms이다. 0 이하이면 기동을 거부한다.

설비 시작·종료·재접속·폴링 그룹 시작·종료의 비동기 결과 대기에 전달한다. 키에 `dual`이 있어도 현재 두 서버 응답을 함께 기다린다는 뜻은 아니다. 설비 생성·수정·삭제의 조정 작업은 이 설정의 대상이 아니다.

## 로그인 세션 시간과 인증 쿠키의 설정
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 인증 설정 L377~385
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; 기본값·보정 L75~111
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiAuthProperties.java @ 985d17c604dd ; 누락 처리 L129~165
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; 만료 시각 L115~126
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiAuthCookieFactory.java @ 985d17c604dd ; 쿠키 속성 L23~48
```

| 설정 항목 | 생성 스크립트 값 | 영향 |
|---|---|---|
| tc.ui.backend.auth.session-ttl-hours | 8시간 | 로그인 때 새 세션의 만료 시각을 계산한다. 0 이하이면 코드 기본값 8을 적용한다. |
| tc.ui.backend.auth.cookie-secure | true | 인증 쿠키의 Secure 속성을 정한다. 누락 시 true를 적용한다. |

두 항목은 `tc-ui-backend.properties`에 있다. 세션 수명 변경은 이미 발급한 세션의 만료 시각을 다시 계산하는 설정이 아니다.

## Backend 배포 요청의 대상 수 상한
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 배포 요청 값 L364~368
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/properties/UiDeployProperties.java @ 985d17c604dd ; 기본값·보정 L32~52
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployPlanningService.java @ 985d17c604dd ; 대상 수 검사 L60~75
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployPlanningService.java @ 985d17c604dd ; 등록 시나리오 미리보기 검사 L105~120
```

`tc.ui.backend.deploy.max-targets-per-request`는 `tc-ui-backend.properties`에 있으며 생성 스크립트 값과 코드 기본값은 100개이다.

배포 계획·등록 시나리오 미리보기를 만들 때 요청 대상 수가 상한보다 크면 거부한다. 0 이하이면 기본값 100으로 보정한다. 한 요청의 대상 수 제한이며 동시에 실행하는 대상 수를 정하는 값과 구별한다.

## Backend DB 연결 풀은 Helm의 어느 키로 바꾸는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; DB 풀 L41~45
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 환경변수 주입 L88~94
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.properties @ f1d5e0a8e727 ; 풀 설정 L67~72
```

Backend 차트는 다음 값을 `SPRING_DATASOURCE_HIKARI_*` 환경변수로 공급한다. 동일한 앱 설정 파일 값보다 환경변수가 우선한다.

| Helm 키 | 차트 기본값 | 대응 앱 설정 |
|---|---|---|
| datasource.hikari.maximumPoolSize | 20 | spring.datasource.hikari.maximum-pool-size |
| datasource.hikari.minimumIdle | 2 | spring.datasource.hikari.minimum-idle |
| datasource.hikari.idleTimeoutMs | 600000 ms | spring.datasource.hikari.idle-timeout |

## Gateway의 DB 연결 수는 어디에 있는가
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 풀 L98~100
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 환경변수·볼륨 L96~179
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 풀 설정 위치 L1553~1553
```

Gateway의 `spring.datasource.hikari.maximum-pool-size`는 생성 스크립트가 `tc-gateway.properties`에 15로 넣는다. Gateway 차트에는 Backend처럼 이 항목을 덮어쓰는 DB 풀 환경변수가 없다.

공통 `tc-db.properties`만 찾아 연결 수를 바꾸지 않는다. Pod 수 확대에 따른 전체 DB 연결 예산과 확장 절차는 `00.rag/admin/installation-and-service-lifecycle.md`가 담당한다.

## 파일 로그 보관량을 정하는 설정
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 로그 기본값 L33~44
원천: nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 ; 속성 연결 L21~29
원천: nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 ; rolling policy L55~60
```

`tc-log.properties`의 앱 로그 설정은 날짜·크기 기준 파일 순환 정책에 전달된다.

| 설정 항목 | 생성 스크립트 값 | 역할 |
|---|---|---|
| tc.logging.app.max-history | 14 | 날짜별 로그의 보관 기간 수이다. |
| tc.logging.app.max-size | 100MB | 파일 하나를 나누는 크기 기준이다. |
| tc.logging.app.total-size-cap | 5GB | 순환 로그의 총용량 상한이다. |

로그 조회와 컨테이너 밖 보관 여부 점검은 `00.rag/admin/daily-checks-and-log-inspection.md`에서 다룬다.

## Redis 통신 기록의 시간·건수 제한
```rag-meta
원천: nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 ; 통신 기록 키 L77~84
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/comm/GatewayCommLogRedisAdapter.java @ d3636ab86763 ; 기본값·정리 L32~73
원천: nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/comm/BusinessCommLogRedisAdapter.java @ 7db24f7466bd ; 기본값·정리 L32~73
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java @ 985d17c604dd ; 조회 시간 범위 L46~71
```

| 설정 항목 | 코드 기본값 | 영향 |
|---|---|---|
| tc.comm-log.retention-hours | 1시간 | Gateway·Business 적재 시 오래된 기록을 제거하고 Backend 조회의 시간 범위도 정한다. |
| tc.comm-log.max-size-per-eqp | 5000개 | Gateway·Business가 설비별 적재 기록의 초과분을 오래된 순서로 제거한다. |

시간 제한은 세 앱의 `tc-log.properties`를 맞춘다. 건수 제한의 사용처는 적재하는 두 앱이다. 시간·건수 중 어느 제한으로든 이미 제거한 기록은 값을 늘려도 복구되지 않는다.

## Simulator 제어 프로세스와 워커의 설정 목록
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; 두 가져오기 목록 L37~51
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 두 설정 전달 L54~62
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/worker/WorkerPoolManager.java @ 5e9b02e3ae16 ; 자식 명령 인자 L67~85
```

Simulator 차트의 `springConfigImport`는 제어 프로세스의 `SPRING_CONFIG_IMPORT`이다. 기본 목록은 `tc-db.properties`·`tc-log.properties`·`tc-simulator.properties`이다.

`workerPool.spawnConfigImport`는 자식 JVM의 `--spring.config.import`로 전달한다. 기본 목록은 `tc-log.properties`·`tc-messaging.properties`·`tc-simulator.properties`이다.

두 목록은 `/app/config/` 파일을 선택 참조한다. 제어 프로세스 목록만 바꾸면 자식 명령의 목록은 바뀌지 않는다.

## Simulator 워커 실행 파일을 지정하는 키
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; workerPool L37~41
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; JSON 설정 전달 L59~62
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/config/OrchestratorRuntimeConfiguration.java @ 5e9b02e3ae16 ; 등록 조건 L195~202
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/worker/WorkerPoolManager.java @ 5e9b02e3ae16 ; 워커 시작 L56~85
```

| Helm 키 | 차트 기본값 | 영향 |
|---|---|---|
| workerPool.enabled | true | 제어 프로세스의 워커 풀 사용 설정으로 전달한다. |
| workerPool.javaCommand | java | 자식 JVM 실행 명령이다. |
| workerPool.equipmentJarPath | /app/worker-equipment.jar | 설비 워커 실행 JAR이다. |
| workerPool.upstreamJarPath | /app/worker-upstream.jar | 상위 연동 워커 실행 JAR이다. |

## 화면 차트의 호스트와 HTTP 포트
```rag-meta
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 포트·Gateway L9~30
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 컨테이너 포트 L41~44
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 호스트·전달 포트 L7~21
```

화면 차트는 `nori-tc-ui-front/deploy/helm/tc-ui-front/`에 있다.

| Helm 키 | 적용 대상 |
|---|---|
| gateway.hostname | HTTPRoute의 화면 접속 호스트이다. |
| gateway.name·gateway.namespace | HTTPRoute가 연결되는 부모 Gateway를 정한다. |
| app.containerPort | 차트 기본 8080, 화면 컨테이너의 HTTP 포트이다. |
| service.port | 차트 기본 80, HTTPRoute가 전달하는 Service 포트이다. |

## 화면의 API 대상은 Helm 호스트와 같은 설정인가
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/api-client.ts @ 9fe69a75dcd3 ; baseURL L10~20
원천: nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; API 프록시 L39~56
원천: nori-tc-ui-front/deploy/docker/Dockerfile-ci @ 9fe69a75dcd3 ; nginx 설정 복사 L8~16
```

화면 API 클라이언트의 기본 경로는 `/api`이다. 제공 이미지의 nginx 설정이 이 경로를 Backend Service로 프록시한다. nginx 설정은 Dockerfile에서 이미지 안으로 복사한다.

화면 차트의 `gateway.hostname`은 브라우저가 들어오는 HTTPRoute 호스트를 정한다. Backend 프록시 대상은 차트의 호스트 값으로 바뀌지 않으므로 API 전달 경로를 바꾸려면 이미지에 포함되는 nginx 설정도 대조한다.

## 다섯 차트의 이미지 선택 입력
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 조합 L42~47
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 조합 L38~43
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 조합 L38~43
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 조합 L28~33
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 이미지 조합 L29~34
```

| Helm 키 | 역할 |
|---|---|
| global.registry | 이미지를 가져오는 저장소이다. |
| common.projectName | 이미지 경로의 프로젝트 부분이다. |
| app.name | Deployment·컨테이너·이미지 경로에 사용하는 앱 식별자이다. |
| app.imageTag | 실행 이미지의 태그를 정한다. |
| app.imagePullPolicy | 이미지 내려받기 정책을 정한다. |
| global.imagePullSecret | 이미지 내려받기에 참조하는 Secret 리소스이다. |

## 서버 환경 파일의 이미지·프로파일 차이
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values-dev.yaml @ f1d5e0a8e727 ; 개발값 L9~13
원천: nori-tc-exec/deploy/helm/tc-gateway/values-staging.yaml @ f1d5e0a8e727 ; 검증값 L9~13
원천: nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 운영값 L9~13
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 배포 입력 L334~422
```

Gateway·Business·Backend·Simulator는 기본 `values.yaml`과 dev·staging·prod 환경 파일을 갖는다. CI 배포는 기본 파일 다음 환경 파일을 적용하고 `app.imageTag`를 별도로 지정한다.

| 환경 파일 | app.profile | app.imagePullPolicy |
|---|---|---|
| values-dev.yaml | dev | Always |
| values-staging.yaml | staging | Always |
| values-prod.yaml | prod | IfNotPresent |

화면 차트는 환경별 파일 없이 기본 `values.yaml`을 사용한다.

## 메모리·CPU 값을 바꾸는 Helm 키
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 자원 연결 L54~60
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 자원 연결 L50~56
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 자원 연결 L54~60
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 자원 연결 L46~52
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 자원 연결 L45~51
```

다섯 앱 차트는 `app.resources` 아래 값을 컨테이너 자원 설정에 넣는다.

| Helm 키 | 설정 대상 |
|---|---|
| app.resources.requests.memory | 메모리 요청량 |
| app.resources.requests.cpu | CPU 요청량 |
| app.resources.limits.memory | 메모리 상한 |
| app.resources.limits.cpu | CPU 상한 |

`resources`는 `app` 아래에 있다. Backend의 최상위 `datasource`와 위치가 다르다.

## 환경별 서버 자원 기본값을 비교하기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 기본 자원 L32~38
원천: nori-tc-exec/deploy/helm/tc-gateway/values-dev.yaml @ f1d5e0a8e727 ; 개발 자원 L14~20
원천: nori-tc-exec/deploy/helm/tc-business/values-dev.yaml @ f1d5e0a8e727 ; 개발 자원 L14~20
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values-dev.yaml @ f1d5e0a8e727 ; 개발 자원 L13~19
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; 기본 자원 L27~34
```

아래는 저장소 값이며 현장의 사용량이 아니다. Gateway·Business·Backend의 staging·prod 환경 파일에는 자원 덮어쓰기가 없어 기본값을 쓴다. Simulator 환경 파일도 자원을 덮어쓰지 않는다.

| 대상·환경 | 메모리·CPU 요청 | 메모리·CPU 상한 |
|---|---|---|
| Gateway·Business·Backend dev | 256Mi·250m | 512Mi·1000m |
| Gateway·Business·Backend staging·prod | 512Mi·500m | 1Gi·2000m |
| Simulator 모든 환경 | 512Mi·500m | 2Gi·2000m |

## 추적 표본 비율과 전송 대상
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; OTLP·sampler L103~125
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; sampler L10~17
원천: nori-tc-exec/deploy/helm/tc-gateway/values-dev.yaml @ f1d5e0a8e727 ; 표본 인자 L3~7
원천: nori-tc-exec/deploy/helm/tc-gateway/values-staging.yaml @ f1d5e0a8e727 ; 표본 인자 L3~7
원천: nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 표본 인자 L3~7
```

네 서버 앱 차트는 `common.observability.otlpEndpoint`와 `common.observability.otlpProtocol`을 OTLP 전송 환경변수에 넣는다.

`common.observability.tracing.sampler`는 `OTEL_TRACES_SAMPLER`로, `common.observability.tracing.samplerArg`는 `OTEL_TRACES_SAMPLER_ARG`로 전달한다.

저장소의 sampler는 `parentbased_traceidratio`이고 환경별 인자는 dev `1`, staging `0.5`, prod `0.1`이다.

## 차트 로그 수준과 앱 로그 수준의 차이
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 로그 환경변수 L99~102
원천: nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; logging.level L28~35
원천: nori-tc-exec/apps/tc-business-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; logging.level L20~24
원천: nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; logging.level L51~59
원천: nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 ; 읽는 속성 L16~20
```

차트의 `common.observability.logLevel`·`common.observability.logFormat`은 `LOG_LEVEL`·`LOG_FORMAT` 환경변수로 전달된다. 공통 logback은 로그 수준을 `logging.level.root`·`logging.level.com.nori.tc`에서 읽는다.

세 앱의 기본 `application.yaml`에는 root `INFO`, `com.nori.tc` `DEBUG`가 있다. 앱 로그 수준을 조정할 때는 `logging.level.*`를 확인한다. 차트가 전달하는 로그 환경변수와 공통 logback이 읽는 속성을 구별한다.

## replica·자동 확장·통신 정책 설정의 담당 범위
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; replica L6~7
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 입력 L1~21
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/networkpolicy.yaml @ f1d5e0a8e727 ; 생성 조건 L1~13
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 조정 스위치 L137~141
```

`app.replicaCount`는 Pod 수, `autoscaling.enabled`는 HPA 생성 여부를 정한다. HPA 범위는 `autoscaling.minReplicas`·`autoscaling.maxReplicas`, CPU 목표는 `autoscaling.targetCPUUtilizationPercentage`이다.

서비스별 범위·확장 제약은 `00.rag/admin/installation-and-service-lifecycle.md`, Gateway의 `networkPolicy.enabled`와 허용 통신은 `00.rag/admin/security-operations.md`가 담당한다.

`coordination.enabled`는 세 서버의 조정 기능 환경변수로 전달한다. 전환 조건은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`에서 다룬다.

## Helm이 설정 Secret을 직접 만드는 조건
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; Secret 입력 L91~100
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/secret-config.yaml @ f1d5e0a8e727 ; 생성 조건·파일 키 L1~17
원천: nori-tc-exec/deploy/helm/tc-business/templates/secret-config.yaml @ f1d5e0a8e727 ; 생성 조건 L1~17
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/secret-config.yaml @ f1d5e0a8e727 ; 생성 조건 L1~17
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/secret-config.yaml @ f1d5e0a8e727 ; 생성 조건 L1~16
```

네 서버 차트의 `configSecret.create` 기본값은 false이다. true일 때만 Secret 템플릿을 생성한다. `configSecret.name`은 참조할 리소스, `configSecret.files`의 키는 Secret 안의 파일 키이다.

자격 값이나 파일 내용을 Helm 예제에 옮기지 않는다. 기존 Secret을 연결하는 설정과 Helm이 Secret을 생성하는 설정을 구별한다. 준비·교체 방법은 `00.rag/admin/security-operations.md`를 따른다.

## TC_TEST_ENV가 설정 목록에 미치는 영향
```rag-meta
원천: nori-tc-exec/exec/ubuntu/run-trace-tc-gateway-app.sh @ f1d5e0a8e727 ; 조건부 오버레이 L42~46
원천: nori-tc-exec/exec/ubuntu/run-trace-tc-business-app.sh @ f1d5e0a8e727 ; 조건부 오버레이 L42~46
원천: nori-tc-exec/exec/ubuntu/run-trace-tc-ui-backend-app.sh @ f1d5e0a8e727 ; 조건부 오버레이 L45~49
원천: nori-tc-exec/exec/ubuntu/run-trace-tc-simulator-app.sh @ f1d5e0a8e727 ; 제어 프로세스 오버레이 L33~38
```

`TC_TEST_ENV`는 `nori-tc-exec/exec/ubuntu/`의 `run-trace-tc-*-app.sh`가 읽는 실행 환경변수이다. 비어 있지 않으면 `.test.properties` 목록을 설정 가져오기의 뒤에 붙인다.

Gateway·Business·Backend에는 DB·Redis·앱별 messaging·앱 설정의 시험용 파일을 추가한다. Simulator 제어 프로세스에는 DB·Simulator 시험용 파일을 추가한다. 이 변수는 앱 바인딩 키나 운영 Helm values 키가 아니다.

## 격리 실행의 Redis 대상과 조정 기능
```rag-meta
원천: nori-tc-exec/config/tc-redis.test.properties @ f1d5e0a8e727 ; 논리 DB L28~30
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.test.properties @ f1d5e0a8e727 ; 조정 기본값 L26~26
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 조정 기본값 L37~37
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.test.properties @ f1d5e0a8e727 ; 조정 기본값 L31~31
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; DB 인자 L41~49
```

격리용 `tc-redis.test.properties`는 `spring.data.redis.database`를 1로 지정한다. 서버 주소·포트는 `TC_TEST_REDIS_HOST`·`TC_TEST_REDIS_PORT`로 덮어쓸 수 있다.

세 서버의 시험용 앱 파일은 `TC_COORDINATION_ENABLED`가 없으면 조정 기능을 false로 둔다. `coord-epoch.sh`의 두 번째 인자는 Redis 논리 DB이며 생략 기본값은 0이다. 조정 기능을 켠 검증에서는 격리 대상과 초기 값 대상이 같아야 한다.

## 격리 DB와 Kafka 이름을 바꾸는 파일
```rag-meta
원천: nori-tc-exec/config/tc-db.test.properties @ f1d5e0a8e727 ; DB 대상 L17~17
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.test.properties @ f1d5e0a8e727 ; 소비 그룹·토픽 L21~28
원천: nori-tc-exec/apps/tc-business-app/config/tc-business.test.properties @ f1d5e0a8e727 ; 앱별 토픽 L11~18
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-ui-backend.test.properties @ f1d5e0a8e727 ; 앱별 토픽 L11~13
```

`config/tc-db.test.properties`는 `spring.datasource.url`의 DB 대상을 바꾼다. DB 자격 값을 바꾸는 파일과 구별한다.

앱별 `tc-messaging.test.properties`는 `spring.kafka.consumer.group-id`와 공통 토픽 설정을 분리한다. 저장소 사본은 그룹에 `-test`, 토픽에 `test.`를 붙인다.

Business·Backend는 앱 전용 토픽 키도 읽으므로 각각 `tc-business.test.properties`·`tc-ui-backend.test.properties`를 함께 가져온다.

## 격리 Kafka 도구의 접속 대상 입력
```rag-meta
원천: nori-tc-exec/kafka-test-topics.sh @ f1d5e0a8e727 ; 입력 변수 L26~31
원천: nori-tc-exec/kafka-test-topics.sh @ f1d5e0a8e727 ; 원격 명령 전달 L57~66
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 격리 실행 L1664~1702
```

`kafka-test-topics.sh`는 앱 설정 파일과 별도로 아래 환경변수를 읽는다. 기본 대상은 설치 현장의 현재값을 뜻하지 않으므로 도구 실행 전 대상 입력을 구별한다.

| 환경변수 | 역할 |
|---|---|
| K8S_SSH_KEY | 노드 접속 키 파일 경로 |
| K8S_NODE | 명령을 전달할 노드 |
| KAFKA_NS | Kafka namespace |
| KAFKA_POD | Kafka 명령을 실행할 Pod |
| BOOTSTRAP | Kafka 명령의 bootstrap 주소 |
| REPLICATION_FACTOR | 격리 토픽 생성 시 복제 계수 |

토픽 이름·방향·메시지 계약은 `00.rag/admin/kafka-topic-and-message-reference.md`가 담당한다.

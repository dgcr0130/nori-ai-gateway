# TC 보안 운영
```rag-meta
유형: 운영
적용: nori-tc-exec f1d5e0a8e727·nori-tc-gateway d3636ab86763 및 GatewayKafkaClientProperties.java a5e5221853b7·nori-tc-ui-backend 985d17c604dd 및 UiKafkaConfiguration.java d36c10ff8579·nori-tc-business 7db24f7466bd·nori-tc-simulator 5e9b02e3ae16·nori-tc-ui-front 9fe69a75dcd3·nori-common ae6791e99e91 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 자격 교체 전에 누가 무엇을 준비하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 보안 운영 L2769~2782
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 자격 교체 L2882~2893
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 작업 권한 L294~316
```

보안 운영 담당자는 대상 namespace의 Secret 변경 권한과 자격 발급 담당자의 협조를 확보한다. DB·Kafka·Redis 자격은 각 서비스 담당자가 발급하며, 앱이 사용할 새 자격과 교체 시간을 함께 맞춘다.

화면 사용자 계정·그룹·권한 관리는 `00.rag/user/user-group-and-permission-management.md`에서 다룬다.

설정 파일 키의 추출·병합과 일반 재반영·되돌림 절차는 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`를 따른다.

## 서비스 자격은 어느 Secret의 파일 키에 두는가
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 파일 키 생성 L204~211
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; namespace L8~10
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 파일 키 L299~305
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 파일 키 L410~418
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; Simulator 파일 키 L527~537
```

기본 namespace `tc`의 자격 파일 키이다.

| Secret 리소스 | 파일 키 |
|---|---|
| tc-gateway-config | tc-db.properties·tc-messaging.properties·tc-redis.properties |
| tc-business-config | tc-db.properties·tc-messaging.properties·tc-redis.properties |
| tc-ui-backend-config | tc-db.properties·tc-messaging.properties·tc-redis.properties·tc-ui-backend.properties |
| tc-simulator-config | tc-db.properties·tc-messaging.properties·tc-simulator.properties |

## 신규 설치용 Secret 생성 스크립트에 무엇을 공급하는가
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 필수 환경변수 L8~26
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 내부 토큰 주입 L398~404
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 생성 호출 L204~211
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 생성 대상 L299~305
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 생성 대상 L410~418
```

`nori-tc-exec`의 `create-k8s-secrets.sh`는 신규 설치 때 Gateway·Business·Backend의 설정 Secret을 만든다. 기존 운영 Secret 교체에는 재실행하지 않는다.

설치 담당자는 `TC_DB_USER`·`TC_DB_PASSWORD`·`TC_UI_SIMULATOR_INTERNAL_TOKEN`을 실행 환경변수로 공급한다. 필요한 변수가 비어 있으면 스크립트가 중단한다. 자격 값은 명령 인자·터미널 출력·기록에 적지 않는다.

```sh
bash ./create-k8s-secrets.sh
```

## Simulator 설정 Secret을 처음 만드는 방법
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Simulator 별도 생성 L980~1048
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; 파일 키 L527~537
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 연결 L106~113
```

신규 설치 담당자는 접근을 제한한 작업 폴더 `TC_WORK`에 파일 네 개를 준비한다. DB·로그 파일은 생성된 Gateway Secret에서 가져온다. 메시징·Simulator 파일은 `apps/tc-simulator-app/config/` 사본을 설치 환경에 맞춰 편집한다.

Kafka 접속·인증 설정과 내부 토큰을 맞춘 뒤 `TC_WORK`로 이동하여 생성한다. 파일과 자격은 작업 후 삭제한다.

```sh
kubectl -n tc create secret generic tc-simulator-config --from-file=tc-db.properties --from-file=tc-log.properties --from-file=tc-messaging.properties --from-file=tc-simulator.properties
```

## DB 비밀번호를 바꾸면 어느 앱까지 교체하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 교체 L2882~2893
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; DB 설정 L22~26
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; DB 파일 L527~533
```

DB 담당자와 중단 시간을 맞춰 교체한다. 파일 안의 DB 계정 항목은 `spring.datasource.username`, 비밀번호 항목은 `spring.datasource.password`이다.

1. DB 담당자가 대상 DB의 비밀번호를 변경한다.
2. 네 서버 앱 설정 Secret의 `tc-db.properties`를 새 자격으로 갱신한다. Simulator도 포함한다.
3. 네 서버 앱을 재기동하고 DB 연결과 기동 오류를 확인한다.

신규 설치에 쓰는 환경변수 공급원도 새 자격으로 맞춘다. 한 앱이라도 이전 파일이 남으면 그 앱은 이전 자격으로 연결한다.

## Kafka 인증을 켜거나 교체할 때 받을 자료
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 보안 자료 L2769~2777
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 메시징 파일 L60~82
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 메시징 파일 L217~239
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 메시징 파일 L311~333
```

Kafka 담당자에게 보안 규약·인증 방식·앱용 계정 자격·인증 연결 Port·신뢰 저장소 파일과 파일 비밀번호·형식을 받는다. 자격과 파일은 화면이나 문서에 출력하지 않고 제한된 작업 폴더에서 다룬다.

제공된 생성 스크립트의 세 메시징 파일에는 Kafka 보안 항목이 없다. 인증을 사용하는 설치에서는 각 앱 Secret의 `tc-messaging.properties`와 다음 설치에 쓸 생성 입력에 보안 항목을 추가한다.

## Business·Backend 설정 사본의 Kafka 공통 인증 항목
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 공통 인증 항목 L19~21
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 공통 인증 항목 L24~26
원천: nori-tc-exec/apps/tc-business-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 메시징 파일 읽기 L12~18
원천: nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 메시징 파일 읽기 L38~45
```

Business·Backend의 메시징 설정 사본에는 다음 공통 인증 항목이 있다. Secret의 `tc-messaging.properties`를 준비할 때 대상 브로커의 규약과 자격을 맞춘다. 공통 설정과 별도로 만든 소비 연결은 구분한다.

| 설정 항목 | 공급하는 내용 |
|---|---|
| spring.kafka.properties.security.protocol | 보안 규약 |
| spring.kafka.properties.sasl.mechanism | 인증 방식 |
| spring.kafka.properties.sasl.jaas.config | 로그인 설정 |

## Business·Backend 설정 사본의 Kafka 신뢰 저장소 항목
```rag-meta
원천: nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 신뢰 저장소 항목 L22~24
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 신뢰 저장소 항목 L27~29
```

Business·Backend의 `tc-messaging.properties` 사본에서 신뢰 저장소 항목은 다음과 같다. Secret에 넣은 파일의 컨테이너 경로와 파일 비밀번호·형식을 맞춘다.

| 설정 항목 | 공급하는 내용 |
|---|---|
| spring.kafka.ssl.trust-store-location | 신뢰 저장소 파일 경로 |
| spring.kafka.ssl.trust-store-password | 파일 비밀번호 |
| spring.kafka.ssl.trust-store-type | 파일 형식 |

## Gateway Kafka 소비·관리 연결의 인증 키
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaClientProperties.java @ a5e5221853b7 ; 소비·관리 설정 바인딩 L20~31
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaClientProperties.java @ a5e5221853b7 ; buildConsumerProperties L72~90
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaClientProperties.java @ a5e5221853b7 ; buildAdminProperties・buildCommonProperties L132~150
```

이 수정이 들어간 판본의 Gateway는 공통 `spring.kafka.properties.` 뒤에 다음 인증 항목을 붙여 소비·관리 연결에 전달한다. `spring.kafka.security.protocol`도 읽는다.

연결별 `spring.kafka.consumer.properties.*`·`spring.kafka.admin.properties.*`에 같은 키가 있으면 공통 값보다 우선한다.

| 항목 | 공급하는 내용 |
|---|---|
| security.protocol | 브로커 보안 규약 |
| sasl.mechanism | 브로커 인증 방식 |
| sasl.jaas.config | 앱 계정의 로그인 설정 |

## Gateway Kafka 신뢰 저장소를 연결하는 키
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaClientProperties.java @ a5e5221853b7 ; buildConsumerProperties L72~90
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaClientProperties.java @ a5e5221853b7 ; buildAdminProperties・buildCommonProperties L132~150
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 신뢰 저장소 항목 L2734~2736
```

이 수정이 들어간 판본의 Gateway는 `spring.kafka.ssl.*`의 신뢰 저장소 설정도 소비·관리 연결에 전달한다. 연결별로 지정하려면 `spring.kafka.consumer.properties.`·`spring.kafka.admin.properties.` 뒤에 다음 항목을 붙인다.

| 항목 | 공급하는 내용 |
|---|---|
| ssl.truststore.location | 컨테이너에서 읽을 파일 경로 |
| ssl.truststore.password | 신뢰 저장소 파일 비밀번호 |
| ssl.truststore.type | 신뢰 저장소 파일 형식 |

## Backend의 Kafka 제어 메시지 수신에 인증을 적용할 수 있는가
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/config/UiKafkaConfiguration.java @ d36c10ff8579 ; 전용 소비 연결 L66~77
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/config/UiKafkaConfiguration.java @ d36c10ff8579 ; buildCommandsConsumerProperties L113~125
```

이 수정이 들어간 판본의 Backend `tc.ui.commands` 전용 소비 연결은 `spring.kafka.ssl.*`·`spring.kafka.security.*`·`spring.kafka.properties.*`를 읽는다.

같은 키가 있으면 `spring.kafka.consumer.properties.*`가 공통 값보다 우선한다. 그 뒤 bootstrap 주소·문자열 역직렬화·자동 커밋 끔을 고정한다. 교체 뒤 실제 제어 메시지 수신도 확인한다.

## Simulator Kafka 인증 설정은 어느 키로 바꾸는가
```rag-meta
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamWorkerProperties.java @ 5e9b02e3ae16 ; 설정 바인딩 L10~56
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/transport/UpstreamKafkaConfiguration.java @ 5e9b02e3ae16 ; 보안 설정 전달 L138~160
원천: nori-tc-exec/apps/tc-simulator-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 인증 설정 사본 L16~21
```

Simulator의 `tc-messaging.properties`에서는 다음 키로 Kafka 인증을 설정한다. 보안 규약이 비어 있으면 보안 설정을 클라이언트에 추가하지 않는다. 설치 때 사본의 규약을 대상 브로커와 맞춘다.

| 설정 항목 | 공급하는 내용 |
|---|---|
| tc.simulator.upstream.kafka.security-protocol | 브로커 보안 규약 |
| tc.simulator.upstream.kafka.sasl-mechanism | 인증 방식 |
| tc.simulator.upstream.kafka.sasl-jaas-config | 계정 로그인 설정 |

## Simulator Kafka 인증서 신뢰 파일의 설정
```rag-meta
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/application/UpstreamWorkerProperties.java @ 5e9b02e3ae16 ; 신뢰 저장소 속성 L49~56
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/transport/UpstreamKafkaConfiguration.java @ 5e9b02e3ae16 ; 경로 조건 L142~160
```

Simulator는 보안 규약과 신뢰 저장소 경로가 모두 비어 있지 않을 때 파일 설정을 전달한다. 파일 경로가 비어 있으면 아래 파일 설정을 전달하지 않는다.

| 설정 항목 | 공급하는 내용 |
|---|---|
| tc.simulator.upstream.kafka.ssl-truststore-location | 컨테이너 파일 경로 |
| tc.simulator.upstream.kafka.ssl-truststore-password | 파일 비밀번호 |
| tc.simulator.upstream.kafka.ssl-truststore-type | 파일 형식 |

## Kafka 신뢰 저장소 파일을 Secret에 넣거나 교체하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 파일 키 병합 L2756~2759
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 파일 patch L2784~2799
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 파일 연결 L172~179
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 파일 연결 L106~113
```

Secret 변경 담당자는 `TRUSTSTORE`에 받은 파일 경로, `TC_WORK`에 제한된 작업 폴더를 지정한다. Gateway 예는 다음과 같다. 파일 내용은 출력하지 않는다.

```sh
printf '{"data":{"kafka-truststore.p12":"%s"}}\n' "$(base64 -w0 "$TRUSTSTORE")" > "$TC_WORK/patch.json"
kubectl -n tc patch secret tc-gateway-config --type=merge --patch-file "$TC_WORK/patch.json"
```

파일은 `/app/config/kafka-truststore.p12`로 연결된다. 파일을 읽는 다른 앱의 설정 Secret에도 같은 키를 넣고 파일 경로 설정을 맞춘다. patch 파일은 작업 후 삭제한다.

## Kafka 자격·신뢰 파일 교체 후 확인할 것
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재기동·입력 보존 L2797~2812
원천: nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; Kafka 준비 판정 L44~50
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 관리 연결 점검 L65~81
원천: nori-tc-simulator/worker-upstream/src/main/java/com/nori/tc/simulator/worker/upstream/transport/UpstreamKafkaConfiguration.java @ 5e9b02e3ae16 ; 발행·수신 보안 설정 L95~122
원천: nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/KafkaAdminConnectivityHealthIndicator.java @ ae6791e99e91 ; health L25~51
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/TcGatewayAutoConfiguration.java @ d3636ab86763 ; kafkaHealthIndicator L123~131
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/TcBusinessAutoConfiguration.java @ 7db24f7466bd ; kafkaHealthIndicator L89~97
원천: nori-tc-ui-backend/ui/starter/tc-ui-backend-starter/src/main/java/com/nori/tc/ui/starter/TcUiBackendAutoConfiguration.java @ 985d17c604dd ; kafkaHealthIndicator L257~265
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/build.gradle.kts @ d3636ab86763 ; 공통 점검 의존 L13~17
원천: nori-tc-business/business/starter/tc-business-starter/build.gradle.kts @ 7db24f7466bd ; 공통 점검 의존 L36~40
원천: nori-tc-ui-backend/ui/starter/tc-ui-backend-starter/build.gradle.kts @ 985d17c604dd ; 공통 점검 의존 L44~52
원천: nori-tc-exec/apps/tc-business-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; Kafka 준비 판정 L33~39
원천: nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; Kafka 준비 판정 L68~74
```

Kafka 설정과 신뢰 파일을 바꾼 앱을 재기동한다. Gateway는 관리 연결의 토픽 점검과 Kafka 준비 상태를 확인하고, 메시지를 발행·수신하는 앱은 실제 송수신도 확인한다. Simulator의 상위 메시지 송수신도 포함한다.

인증·신뢰 오류나 송수신 실패가 남으면 교체 성공으로 처리하지 않는다. Kafka 담당자와 규약·계정·파일·연결 Port를 대조한다. 운영 Secret의 변경과 다음 설치에 사용할 입력을 함께 유지한다.

Gateway·Business·Backend의 Kafka 준비 상태 점검은 `nori-common-runtime-support:0.0.4`를 사용한다. 확인한 점검 구현은 bootstrap 주소만 전달하므로 인증 브로커 전환 전 점검 경로의 보안 설정 전달을 개발 담당자와 확인한다.

## Redis 비밀번호를 교체할 파일과 앱
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 인증 L2764~2767
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/config/UiRedisProperties.java @ 985d17c604dd ; 표준 속성 바인딩 L17~26
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/config/UiRedisConfiguration.java @ 985d17c604dd ; 비밀번호 전달 L280~294
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; 설정 파일 목록 L527~537
```

Redis 담당자와 교체 시간을 맞추고 Gateway·Business·Backend 설정 Secret의 `tc-redis.properties`에 `spring.data.redis.password`를 공급한다. Redis 계정을 구분하는 Backend 연결은 `spring.data.redis.username`도 읽는다.

변경한 세 앱을 재기동하여 Redis 연결과 담당 기능을 확인한다. Simulator 설정 Secret에는 Redis 파일이 없다. 다음 설치용 Redis 파일 생성 입력에도 인증 항목을 유지한다.

## Simulator 내부 제어 토큰은 양쪽을 함께 교체한다
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 토큰 주입 L398~404
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 양쪽 설정 키 L1034~1036
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/HttpSimulatorOrchestratorClient.java @ 985d17c604dd ; 토큰 전송 L126~134
원천: nori-tc-simulator/runtime/src/main/java/com/nori/tc/simulator/runtime/security/InternalTokenAuthFilter.java @ 5e9b02e3ae16 ; 값 검사·거부 L46~81
```

Backend의 `tc-ui-backend.properties`에 있는 `tc.ui.simulator.internal-token`과 Simulator의 `tc-simulator.properties`에 있는 `tc.simulator.internal.token`을 같은 새 값으로 갱신한다. 두 앱을 재기동하고 데모 제어 요청을 확인한다.

Simulator는 내부 토큰이 비어 있으면 기동 설정을 거절하고, 제어 요청의 토큰이 없거나 다르면 HTTP 401로 거절한다. 일치 여부를 확인할 때 토큰 값을 출력하지 않는다.

## 이미지 내려받기 자격을 교체하면 언제 쓰이는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; pull 자격 교체 L2882~2893
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; pull 참조 L42~47
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 화면 pull 참조 L29~34
```

레지스트리 담당자가 Harbor 자격을 바꾼 뒤, 설치 담당자가 배포 namespace의 이미지 pull Secret을 새 자격으로 갱신한다. 차트의 `global.imagePullSecret`이 참조하는 리소스를 대상으로 한다.

pull 자격은 다음 이미지 내려받기 때 쓰인다. 실행 중인 앱의 DB 자격처럼 설정 재기동을 요구하지 않는다. 다음 배포에서 이미지 내려받기 실패가 없는지 확인한다. 최초 등록과 차트 입력은 `00.rag/admin/installation-and-service-lifecycle.md`에서 다룬다.

## 화면 HTTPS 인증서 교체는 누가 담당하는가
```rag-meta
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway 연결 L1~21
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증서 소유 L391~416
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 교체 연계 L2456~2475
```

화면 차트는 HTTPRoute로 부모 Gateway를 참조한다. 화면 HTTPS 인증서의 발급·교체는 부모 Gateway 관리자가 수행한다. 인증서 Secret은 부모 Gateway와 같은 namespace에 두고 HTTPS listener가 참조하도록 관리한다.

교체 담당자는 발급받은 인증서의 호스트·만료 시각과 Gateway의 인증서 참조를 대조하고 화면 HTTPS 접속을 확인한다. 인증서 내용·개인 키·실제 만료일은 검색 문서에 넣지 않는다.

## Gateway 통신 정책에서 실제 허용하는 범위
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/networkpolicy.yaml @ f1d5e0a8e727 ; 통신 정책 L1~29
원천: nori-tc-exec/deploy/helm/tc-gateway/values-staging.yaml @ f1d5e0a8e727 ; 활성화 L15~16
원천: nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 활성화 L25~26
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 필요한 통신 대상 L2948~2959
```

제공된 Gateway의 staging·prod 값 파일은 NetworkPolicy를 켠다. 인바운드는 차트의 `gateway.namespace`에서 오는 Service 대상 TCP Port를 허용하고, 아웃바운드는 전체 허용이다. Kafka·Redis 자격을 설정하는 작업과 통신 대상을 제한하는 작업은 별개다.

통신 제한을 바꾸려면 차트의 `networkpolicy.yaml`을 검토한다. 필요한 대상은 DB·Kafka·Redis·관측 수집기·설비와 이름 풀이이다. 실제 연결 대상이 빠지지 않도록 배포 담당자와 맞춘다.

## 이미지 취약점 검사 실패를 배포 전에 확인하기
```rag-meta
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 이미지 검사 L299~331
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 배포 선행 조건 L334~336
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 서버 배포 선행 조건 L383~411
원천: nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; OS 패키지 갱신 L17~23
```

서버 이미지 CI의 `scan-image`는 Trivy로 HIGH·CRITICAL을 검사하고 검출 시 종료코드 1로 실패한다. `--ignore-unfixed`로 패치가 없는 항목은 제외한다. 배포 잡은 검사 잡의 성공을 선행 조건으로 둔다.

서버 Dockerfile은 이미지 빌드 때 OS 패키지를 갱신한다. 보안 담당자는 검사 결과를 확인하고 취약 의존성·기준 이미지 변경을 담당 개발자에게 전달한다. 검사 실패를 배포 성공으로 처리하지 않는다.

## 의존성 검사는 어떤 명령과 기준으로 실행하는가
```rag-meta
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 공통 루트 플러그인 L20~25
원천: nori-common/build-logic/src/main/kotlin/com/nori/common/gradle/RootProjectConventionsPlugin.kt @ ae6791e99e91 ; 보안 플러그인 적용 L28~33
원천: nori-common/build-logic/src/main/kotlin/com/nori/common/gradle/SecurityScanConventionsPlugin.kt @ ae6791e99e91 ; OWASP 설정 L28~69
원천: nori-tc-exec/config/owasp/suppressions.xml @ f1d5e0a8e727 ; 억제 만료 L49~57
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 수동 검사 L2979~2990
```

의존성 검사는 서버 이미지 검사와 별도다. 빌드 담당자가 `nori-tc-exec` 루트에서 실행한다. 취약점 자료를 내려받을 접근을 준비한다.

```sh
./gradlew dependencyCheckAggregate
```

공통 보안 플러그인은 CVSS 7.0 이상을 실패 기준으로 두고 `config/owasp/suppressions.xml`을 읽는다. 억제 항목에는 만료 조건이 있으므로 검사 실패 시 대상과 유효기간을 다시 검토한다. 검토한 오탐만 억제 목록으로 관리한다.

## 컨테이너 실행 계정과 권한 제한을 확인하기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 실행 권한 L37~53
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 실행 권한 L33~49
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 실행 권한 L33~49
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 실행 권한 L23~39
원천: nori-tc-ui-front/deploy/docker/Dockerfile-ci @ 9fe69a75dcd3 ; 이미지 실행 계정 L12~18
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 컨테이너 제한 L35~40
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 사용자·그룹 기본값 L8~9
```

네 서버 앱 차트는 `common.userId`·`common.groupId`와 `runAsNonRoot=true`를 지정한다. 제공 기본값은 사용자 3000·그룹 2000이다. 화면 이미지는 `USER nginx`로 실행한다.

다섯 앱의 컨테이너 정의는 권한 상승을 막고 Linux capability를 모두 제거한다. 설치 담당자는 namespace의 Pod 보안 제약과 배포 정의가 맞는지 확인한다. 이 값은 차트·이미지 정의이며 현장의 실행 상태를 뜻하지 않는다.

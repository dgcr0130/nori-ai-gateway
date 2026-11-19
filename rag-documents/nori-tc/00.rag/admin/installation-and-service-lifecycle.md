# TC 설치·기동·정지·확장
```rag-meta
유형: 운영
적용: nori-tc-exec f1d5e0a8e727 기반(생성 스크립트·V9는 1d2879d14406)·nori-tc-ui-front 9fe69a75dcd3·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 설치 전에 어떤 자원과 작업 권한을 준비하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 사전 준비·배포 순서 L294~316
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 접속 입력 L61~68
원천: nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 설정·준비 검사 L20~50
원천: nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; API 프록시 L43~48
```

설치 담당자는 Kubernetes·Helm 배포 권한, 이미지 내려받기 권한, PostgreSQL 스키마 적용 권한과 Kafka·Redis 사용 경로를 준비한다. 설치 명령은 작업 PC에서 실행한다.

제공된 화면 이미지의 API 프록시는 namespace `tc`의 `tc-ui-backend`를 가리킨다. 이 구성을 사용할 때 namespace는 `tc`로 준비한다.

DB 스키마·Kafka 토픽·설정 Secret·Redis 초기 값을 먼저 준비한 뒤 앱을 배포한다. 아래 명령의 이미지 태그와 화면 호스트는 설치 담당자가 확보한 값을 사용한다.

백업·복원은 `00.rag/admin/backup-and-recovery.md`에서 다룬다.

## 이미지 pull Secret과 레지스트리를 맞추는 방법
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 이미지 입력 L1~7
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 참조 L42~47
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 이미지 참조 L29~34
```

차트는 `global.imagePullSecret`이 가리키는 Secret으로 이미지를 받는다. 기본 리소스는 `harbor-pull-secret`이다. 배포할 namespace에 이미지 내려받기 자격을 먼저 등록한다.

설치 환경에 맞춰 Helm의 `global.registry`·`common.projectName`·`app.imageTag`를 지정한다. pull Secret 리소스를 다르게 만들었다면 `global.imagePullSecret`도 함께 지정한다.

## 설비와 화면을 받을 Gateway를 먼저 준비하는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 부모 Gateway·TCPRoute L129~146
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; 부모 listener 참조 L1~21
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L1~21
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Gateway와 listener L365~515
```

기본 차트는 `envoy-gateway-system`의 `nori-gateway`를 부모로 참조한다. 인프라 담당자는 TCP listener `tc-gw-6100`·`tc-gw-6200`·`tc-gw-6300`과 화면용 HTTPS listener를 준비한다.

부모 Gateway는 namespace `tc`의 Route를 받아들여야 한다. 설비용 listener는 각각 6100·6200·6300 Port를 사용한다. 화면의 `gateway.hostname`은 HTTPS 호스트·이름 풀이·로그인 허용 출처와 맞춘다.

## 앱 기동 전에 Kafka 토픽을 준비하는 방법
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 운영 토픽 생성 L516~583
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 토픽과 파티션 설정 L83~88
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 명령 파티션 수 L127~127
원천: nori-tc-exec/kafka-test-topics.sh @ f1d5e0a8e727 ; 격리 토픽만 허용 L67~80
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 파티션 수 강제 L156~227
```

Kafka 담당자는 앱 설정의 운영 토픽을 미리 만든다. 기본 명령 토픽 생성 예는 다음과 같다. Kafka 도구가 있는 환경에서 설치 대상 bootstrap 주소를 `KAFKA`에 넣고 실행한다. 복제 계수 3에는 브로커 3대 이상이 필요하다.

```sh
bin/kafka-topics.sh --bootstrap-server "$KAFKA" --create --if-not-exists --topic tc.eqp.commands --partitions 6 --replication-factor 3
```

생성 뒤 `--describe`로 실제 파티션 수를 확인한다. 이미 있는 토픽은 생성 명령이 건너뛴다. `kafka-test-topics.sh`는 `test.` 토픽 전용이므로 운영 토픽 생성에 사용하지 않는다.

## 신규 DB 스키마를 앱보다 먼저 적용하기
```rag-meta
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 설정 L51~68
원천: nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 스키마 검증 L89~93
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 스키마 적용 L585~712
원천: nori-tc-exec/db/migration/V9__widen_plc_register_order_columns.sql @ 1d2879d14406 ; V9 칼럼 확장 L16~18
```

스키마 적용은 `nori-tc-exec` 루트에서 수행한다. 형제 위치에 `nori-tc-common`을 준비하고, 설치 DB의 `TC_DB_URL`·`TC_DB_USER`·`TC_DB_PASSWORD`를 환경변수로 공급한다.

```sh
./gradlew flywayInfo
./gradlew flywayMigrate
./gradlew flywayInfo
```

마지막 출력에서 V1~V9 적용 상태를 확인한다. 빈 DB에서는 V1을 실행하고, 이력 없이 기존 테이블이 있으면 버전 1로 baseline 처리한다. 앱의 `ddl-auto=validate`는 스키마를 생성하지 않는다.

## 초기 계정 시드를 기존 운영 DB에 다시 넣어도 되는가
```rag-meta
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 초기화·계정 삽입 L12~25
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 계정 칼럼 L169~172
원천: nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 변경 시각 필수화 L20~29
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 초기 계정 준비 L714~813
```

`docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql`은 신규 DB에 한 번 적용하는 자료다. 계정·그룹·권한·세션을 비우므로 기존 운영 DB에 재실행하지 않는다.

V3 적용 뒤에는 시드 삽입문에 없는 `password_changed_at`이 필수다. DB 담당자는 이 칼럼에 `now()` 기본값을 임시로 부여하고 시드를 적용한 뒤 기본값을 제거한다. 계정은 새 비밀번호를 지정하고 활성화한 뒤 사용한다.

## 계정 시드 뒤 조정 조회 권한을 복원하기
```rag-meta
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 권한 초기화·ADMIN 부여 L18~25
원천: nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 권한과 ADMIN 연결 L32~58
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 시드 뒤 V8 적용 L782~812
```

V8 뒤에 초기 시드를 적용했다면 DB 담당자는 `db/migration/V8__add_coordination_admin_read_permission.sql`을 다시 적용한다. 이 파일은 충돌 시 기존 행을 유지하며 권한과 ADMIN 그룹 연결을 넣는다.

`COORDINATION_ADMIN_READ`와 ADMIN 그룹의 연결 건수가 1인지 확인한다. Flyway는 적용 완료한 V8을 자동으로 다시 실행하지 않으므로 시드 후 보완은 파일의 SQL 적용으로 수행한다.

## 앱 설정 Secret은 어떻게 준비하는가
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Secret 생성 L204~211
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business·UI 설정 생성 L299~305
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; UI 자격 검사·생성 L400~418
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L106~113
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 앱 설정 부트스트랩 L814~1064
```

`create-k8s-secrets.sh`는 `tc-gateway-config`·`tc-business-config`·`tc-ui-backend-config`를 만든다. 설치할 DB·Kafka·Redis·화면 허용 출처를 반영한 뒤 실행한다.

`TC_DB_USER`·`TC_DB_PASSWORD`·`TC_UI_SIMULATOR_INTERNAL_TOKEN`을 모두 준비하고 `bash ./create-k8s-secrets.sh`를 실행한다. 마지막 변수 검사는 앞의 두 Secret 생성 뒤에 있으므로 누락하면 일부만 만들어진다.

`tc-simulator-config`는 별도로 준비한다. 키는 `tc-db.properties`·`tc-log.properties`·`tc-messaging.properties`·`tc-simulator.properties`이다. 시뮬레이터와 UI의 제어 인증 값은 일치시킨다.

## 수정된 스크립트로 Kafka 메시징 설정 생성하기
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 메시징 생성 입력 L67~74
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 메시징 생성 입력 L226~232
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 메시징 생성 입력 L323~329
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58
원천: nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L44~53
원천: nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58
```

이 수정이 들어간 `create-k8s-secrets.sh`는 Gateway·Business·UI Backend 메시징 생성 입력 세 곳에 다음 값을 포함한다. 신규 Secret은 이 입력으로 생성한다.

```properties
spring.kafka.producer.properties.linger.ms=0
```

생성 뒤에는 각 Secret에서 이 설정을 확인한다. 증상과 진단은 `00.rag/admin/platform-troubleshooting.md`의 「새 이미지인데 Kafka 설정 결함이 다시 나타나는 이유」에서 다룬다.

## 생성된 Secret에서 linger 값만 확인하기
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 메시징 파일 키 L204~211
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 메시징 파일 키 L299~305
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 메시징 파일 키 L410~418
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 확인할 설정 L58~58
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Secret 파일 키 조회·복호화 L948~966
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 전용 명령 L206~213
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 전용 명령 L303~309
원천: nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 전용 명령 L416~424
```

이 수정 이전 판본으로 만든 Secret은 스크립트를 바꿔도 갱신되지 않는다. `s`에 `tc-gateway-config`·`tc-business-config`·`tc-ui-backend-config`를 하나씩 넣고 각각 실행한다. 메시징 파일 전체를 출력하지 않고 지정한 설정 줄만 출력한다.

```sh
kubectl -n tc get secret "$s" -o jsonpath='{.data.tc-messaging\.properties}' | base64 -d | grep -Fx 'spring.kafka.producer.properties.linger.ms=0'
```

세 Secret 모두 지정한 줄이 출력되는지 확인한다. 조회 오류나 출력 누락이 있으면 앱 배포를 멈추고 생성 입력과 결과를 다시 대조한다.

## Redis 대기 한도를 지정하지 않으면 기동하는가
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실제 대기 한도 검증 L259~274
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 안전 관계식 L98~127
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationConfiguration.java @ 7db24f7466bd ; 실제 대기 한도 검증 L132~145
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 설정 보완 L886~931
```

기본 조정 설정으로 설치할 때 세 앱의 `tc-redis.properties`에 다음 두 키를 넣는다. Secret 생성 스크립트에는 이 키가 없으므로 생성 전에 보완한다.

```properties
spring.data.redis.timeout=1s
spring.data.redis.connect-timeout=1s
```

Gateway·Business는 실제 Redis 명령 대기 한도로 소유권 안전 관계식을 검사한다. 관계식을 위반하면 기동이 실패하므로 앱의 조정 설정 선언값만 바꿔 검사를 통과시키지 않는다.

## 최초 설치에서 Redis 초기 값을 발급하는 시점
```rag-meta
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 인자·발급·덮어쓰기 방지 L34~98
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; 미발급 기동 거부 L40~51
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 초기 발급·인증 조건 L1858~1939
```

조정 기능을 켜는 최초 설치에서는 앱 기동 전에 `tc:coord:v1:cluster:epoch`를 발급한다. 스크립트는 SSH를 경유하므로 `K8S_SSH_KEY`·`K8S_NODE`·`REDIS_NS`·`REDIS_DEPLOY`를 설치 대상으로 지정한다.

```sh
bash ./coord-epoch.sh issue 0
```

기존 값은 유지한다. 스크립트에는 Redis 인증 입력이 없으므로 인증이 필요한 Redis에서는 이 명령을 사용하지 않는다. 다중 서버 소유권 확인은 `00.rag/admin/coordination-ownership-and-recovery-check.md`에서 다룬다.

## 설치 이미지가 없으면 어디서 만드는가
```rag-meta
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 이미지 빌드·시뮬레이터 분기 L196~298
원천: nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 실행 JAR L24~37
원천: nori-tc-exec/deploy/docker/Dockerfile-ci-simulator @ f1d5e0a8e727 ; 세 실행 JAR L30~47
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 이미지 준비 L1070~1235
```

설치 담당자는 `tc-gateway`·`tc-business`·`tc-ui-backend`·`tc-simulator`·`tc-ui-front` 이미지와 태그를 확보한다. 이미 등록된 이미지를 받으면 설치 중 빌드를 생략한다.

직접 만들 때 세 서버 앱은 `nori-tc-exec`의 `apps:<앱>-app:bootJar` 산출물을 `app.jar`로 두고 `deploy/docker/Dockerfile-ci`를 사용한다. 시뮬레이터 이미지는 별도 Dockerfile에 orchestrator·worker-equipment·worker-upstream JAR를 함께 넣는다.

CI에서 시뮬레이터 빌드는 `TRIGGER_APP=tc-simulator` 분기로 실행한다. 화면 이미지 빌드와 배포는 `nori-tc-ui-front` 저장소가 소유한다.

## 화면 이미지를 직접 만들 때 Backend가 먼저 필요한가
```rag-meta
원천: nori-tc-ui-front/deploy/docker/Dockerfile-ci @ 9fe69a75dcd3 ; 정적 산출물·설정 검사 L8~16
원천: nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; 프록시 이름 해석 L43~48
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 화면 이미지 직접 빌드 L1193~1222
```

화면 Dockerfile은 `dist/`를 복사하고 `nginx -t`로 설정을 검사한다. 프록시 대상 `tc-ui-backend.tc.svc.cluster.local`을 빌드 환경에서 해석할 수 있어야 한다.

클러스터에서 직접 빌드하면 Backend Service를 먼저 준비한다. 작업 PC에서 빌드할 때는 Docker 빌드에 `--add-host tc-ui-backend.tc.svc.cluster.local:127.0.0.1`을 준다. 이미 완성된 화면 이미지를 받는 설치에는 이 빌드 순서 제약이 없다.

## 다섯 서비스를 어떤 순서로 배포하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 배포 순서 L1237~1274
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L161~168
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L172~179
```

스키마·토픽·설정 Secret·초기 값과 이미지를 준비한 뒤 아래 순서로 배포한다. Secret을 붙이지 못하거나 앞 서비스의 기동 확인이 실패하면 다음 배포를 멈춘다.

1. `tc-ui-backend`를 배포하고 준비 상태를 확인한다.
2. `tc-business`를 배포하고 준비 상태를 확인한다.
3. `tc-gateway`를 배포하고 전용 검증 스크립트로 확인한다.
4. `tc-simulator`를 배포한다.
5. `nori-tc-ui-front` 저장소에서 `tc-ui-front`를 배포한다.

## Backend·Business·Simulator를 설치하거나 다시 기동하기
```rag-meta
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Simulator 배포 L334~347
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Business·Backend 배포 L383~422
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 준비 검사 없는 단일 Pod L1~113
```

`nori-tc-exec` 루트에서 설치할 이미지 태그를 `TAG`에 넣는다. 아래 `a`는 배포 순서대로 `tc-ui-backend`·`tc-business`·`tc-simulator`로 바꿔 각각 실행한다.

```sh
a=tc-ui-backend
c=deploy/helm/$a
helm upgrade --install "$a" "$c" --namespace tc -f "$c/values.yaml" -f "$c/values-prod.yaml" --set app.imageTag="$TAG" --wait --timeout 300s
```

Backend·Business는 준비 판정을 확인한다. Simulator 차트에는 준비·생존 검사가 없으므로 Helm 대기 성공만으로 제어 API의 정상 동작을 확정하지 않는다.

## Gateway는 왜 Helm 대기 옵션을 쓰지 않는가
```rag-meta
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Gateway 배포 L352~370
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 예비 Pod와 검증 L2~57
```

Gateway는 예비 Pod가 준비 상태가 되지 않을 수 있다. `nori-tc-exec` 루트에서 설치 이미지 태그를 `TAG`에 넣고 `--wait` 없이 배포한다.

```sh
c=deploy/helm/tc-gateway
helm upgrade --install tc-gateway "$c" --namespace tc -f "$c/values.yaml" -f "$c/values-prod.yaml" --set app.imageTag="$TAG" --timeout 300s
```

Helm 종료 뒤 `sh ./deploy/scripts/verify-gateway-rollout.sh tc`를 실행한다. `gateway rollout verified.` 출력과 종료코드 0을 확인한다. 실패하면 설치 성공으로 처리하지 않는다.

## Gateway 검증 스크립트는 무엇을 확인하는가
```rag-meta
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 설정 입력·통과 조건 L12~57
```

검증 스크립트는 기본 입력 `./config/tc-shard.properties`의 그룹 수를 읽는다. 이 파일은 배포한 Gateway 설정과 같아야 한다. 다른 경로는 namespace 뒤 두 번째 인자로 지정한다.

Pod가 하나 이상이고 모두 Running이며, Ready 수가 `min(Pod 수, 그룹 수)` 이상이면 통과한다. 10초 간격으로 최대 60회 확인한다.

이 검사는 원하는 replica 수나 모든 그룹의 소유자를 보증하지 않는다. Pod 수가 그룹 수보다 적어도 통과할 수 있으므로 배포 replica·그룹별 담당 여부도 함께 점검한다.

## 화면 서비스를 Helm으로 설치하기
```rag-meta
원천: nori-tc-ui-front/.gitlab-ci.yml @ 9fe69a75dcd3 ; 화면 배포 L151~167
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 앱·호스트 입력 L9~30
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 호스트 반영 L13~14
```

`nori-tc-ui-front` 루트에서 화면 이미지 태그를 `TAG`, HTTPS 화면 호스트를 `HOST`에 넣고 실행한다. 호스트에는 URL 경로를 넣지 않는다.

```sh
c=deploy/helm/tc-ui-front
helm upgrade --install tc-ui-front "$c" --namespace tc -f "$c/values.yaml" --set app.imageTag="$TAG" --set gateway.hostname="$HOST" --wait --timeout 300s
```

화면 차트에는 환경별 값 파일이 없으므로 기본 `values.yaml`을 사용하고 설치 환경의 호스트·이미지 입력을 지정한다.

## 설치 직후 Pod와 서비스 준비 상태 확인하기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L66~95
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L54~88
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; HTTP 검사 경로 L61~79
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 화면 HTTP 검사 L55~70
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 검증 L1330~1398
```

```sh
kubectl -n tc get pods
kubectl -n tc get deploy
```

Gateway·Business는 갱신된 상태 파일, Backend는 `/api/actuator/health/readiness`, 화면은 `/` 응답으로 준비 여부를 검사한다. Gateway 예비 Pod는 전용 판정으로 구별한다.

설치 담당자는 앱 기동 로그의 ERROR와 재시작 발생 여부를 확인한다. Simulator는 Pod 실행 상태에 더해 제어 API 확인을 따로 수행한다. 시뮬레이터 제어 인증이 거부되면 양쪽 설정의 인증 값 일치를 점검한다.

## Route가 설비·화면 연결을 받는지 확인하기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; 설비 Route L1~21
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 화면 Route L1~21
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Route·연결 검증 L1384~1478
```

배포 뒤 아래로 Route 상태를 조회한다. 각 Route의 부모 조건에서 `Accepted=True`·`ResolvedRefs=True`를 확인한다.

```sh
kubectl -n tc get tcproute tc-gw-6100 tc-gw-6200 tc-gw-6300 -o yaml
kubectl -n tc get httproute tc-ui-front -o yaml
```

Route 수락 뒤에는 설비 망에서 6100·6200·6300 Port 도달과 HTTPS 화면 로그인을 확인한다. Kafka·Redis 연결의 인증·암호화 구성이 설치 환경 정책과 맞는지도 확인한 뒤 설치를 마감한다.

## 재기동하면 서비스가 잠시 모두 내려가는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~15
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~14
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~11
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L9~13
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재기동 L1746~1760
```

Gateway·Business·Backend·Simulator 차트는 기존 Pod를 내린 뒤 새 Pod를 만드는 `Recreate` 전략이다. 설치한 서비스를 재기동하려면 서비스 중단 시간을 확보한다.

```sh
kubectl -n tc rollout restart deploy/tc-business
kubectl -n tc rollout status deploy/tc-business
```

대상 이름을 설치한 Deployment로 바꿔 사용한다. Gateway 재기동 뒤에는 일반 rollout 완료 대기 대신 전용 검증 스크립트를 사용한다. 버전·설정 변경 반영은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`에서 다룬다.

## 자동 확장이 있는 서비스를 정지하고 다시 올리기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 생성 L1~21
원천: nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L17~21
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L14~18
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 정지·복원 L1761~1776
```

운영 값으로 설치한 Gateway·Business·Backend를 정지할 때는 중단할 서비스의 HPA를 먼저 제거한 뒤 replica를 0으로 내린다. 다음은 Gateway 예다.

```sh
kubectl -n tc delete hpa tc-gateway
kubectl -n tc scale deploy/tc-gateway --replicas=0
```

대상 Pod가 사라졌는지 확인한다. 다시 올릴 때는 같은 이미지·설치 값을 사용한 서비스별 Helm 설치 명령을 실행한다. 자동 확장이 활성인 값으로 적용하면 HPA도 다시 생성한다.

## Simulator와 화면 서비스를 정지하기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica L1~13
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; replica 입력 L1~10
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 자동 확장 없는 정지 L1761~1776
```

제공된 차트의 Simulator·화면 서비스에는 HPA가 없다. 중단할 서비스만 replica를 0으로 내리고 대상 Pod가 사라졌는지 확인한다.

```sh
kubectl -n tc scale deploy/tc-simulator --replicas=0
kubectl -n tc scale deploy/tc-ui-front --replicas=0
```

재기동할 때는 설치에 사용한 값으로 각 서비스의 Helm 명령을 다시 적용한다. Simulator를 정지할 목적으로 Helm의 `app.replicaCount`에 0을 주면 단일 replica 검증에서 실패한다.

## 운영 값 파일의 replica와 자동 확장 범위
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 고정 replica L9~23
원천: nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L9~21
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L8~18
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica 강제 L1~10
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 기본 replica L9~14
```

아래는 저장소의 운영 값 파일을 적용할 때의 설정이며 현장의 현재 Pod 수가 아니다. 현재 적용값은 `kubectl -n tc get deploy,hpa`로 조회한다.

| 서비스 | replica 설정 | 자동 확장 범위 |
|---|---|---|
| tc-gateway | 3 | 최소 3·최대 3 |
| tc-business | 2 | 최소 2·최대 6 |
| tc-ui-backend | 2 | 최소 2·최대 6 |
| tc-simulator | 1 강제 | 없음 |
| tc-ui-front | 기본 1 | 없음 |

## Business·Backend를 부하에 맞춰 늘리는 조건
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21
원천: nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 파티션 상한·CPU 목표 L15~21
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; DB 연결 풀 L41~45
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; DB 풀 주입 L74~91
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 확장 제약 L1778~1796
```

운영 값의 Business·Backend는 CPU 이용률 목표 70으로 HPA가 replica를 조절한다. 수동 replica 변경을 유지하려면 HPA와 설치 값도 함께 맞춘다.

Business 상한은 구독 토픽의 파티션 수를 고려한다. 같은 consumer 그룹에서 파티션보다 consumer를 더 늘려도 추가 consumer는 처리할 파티션이 없다.

Backend는 replica별 DB 연결 풀을 사용한다. 기본 최대 풀 크기는 20이다. 상한을 높이기 전에 다른 서비스가 사용하는 연결까지 합쳐 DB 연결 예산을 확인한다.

## Gateway를 늘릴 때 Pod 수만 바꾸면 되는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 그룹 수와 예비 Pod L23~49
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 노드별 분리 L25~36
원천: nori-tc-exec/config/tc-shard.properties @ f1d5e0a8e727 ; 기본 그룹 배정 L21~24
원천: nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 최소·최대 고정 L15~23
```

Gateway 기본 그룹은 2개이며 예비 Pod를 1개 두는 구성이다. CPU 부하에 따라 늘리는 구성이 아니다. 그룹을 추가할 때 `app.replicaCount`와 HPA 최소·최대를 그룹 수+예비 1로 함께 맞춘다.

Gateway 차트는 같은 앱 Pod를 서로 다른 노드에 배치하도록 강제한다. 늘린 Pod를 놓을 노드가 부족하면 Pending으로 남으므로 사용 가능한 노드도 확인한다.

## Gateway 그룹과 Kafka 파티션을 확장할 때 맞출 값
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 토픽 파티션 일치 검사 L156~227
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayKafkaShardProperties.java @ d3636ab86763 ; 그룹 중복·범위·합집합 검사 L460~501
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 그룹 설정 동기화 L1803~1856
```

그룹의 담당 파티션은 중복 없이 전체를 덮어야 한다. `tc.eqp.commands`·`tc.ui.events.gateway`의 파티션 수와 `tc.gateway.kafka.commands-partition-count`를 같게 맞춘다. 불일치하면 Gateway가 기동을 거부한다.

그룹 변경은 Gateway·Backend Secret의 `tc-shard.properties`, 검증에 쓰는 `config/tc-shard.properties`, Secret 생성 스크립트의 그룹 정의를 함께 맞춘다. 토픽별 메시지 계약은 `00.rag/admin/kafka-topic-and-message-reference.md`에서 다룬다.

## Simulator는 여러 Pod로 확장할 수 있는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; replica 강제·교체 방식 L1~13
원천: nori-tc-exec/deploy/docker/Dockerfile-ci-simulator @ f1d5e0a8e727 ; 세 프로세스 이미지 구성 L1~5
```

Simulator 차트는 프로세스 내부 상태를 사용하는 구성이므로 `app.replicaCount`가 1이 아니면 렌더를 실패시킨다. worker JAR가 함께 들어 있어도 Pod replica를 늘리는 확장 방식으로 사용하지 않는다.


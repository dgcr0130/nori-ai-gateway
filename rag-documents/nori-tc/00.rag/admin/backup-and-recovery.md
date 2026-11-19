# TC 백업·복구
```rag-meta
유형: 운영
적용: nori-tc-exec f1d5e0a8e727·nori-tc-ui-backend 985d17c604dd·nori-tc-common 6fe71f6b2da6 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/admin/installation-and-service-lifecycle.md
```

## 어떤 운영 데이터를 먼저 백업하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 백업 대상 L2149~2158
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 네 앱의 공유 DB L51~68
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 모델·설비 정의 L94~480
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 작업·배포·권한 정의 L703~1085
```

DB 담당자는 네 서버 앱이 공유하는 PostgreSQL `nori_tc`를 최우선으로 백업한다. 사용자·권한·설비·모델·배포·작업 자료가 대상이다.

DB 외에도 Kafka 메시지, Redis의 조정·실패 보관 자료, 설치 설정과 운영 Secret을 구별해 보존한다. DB 백업 파일만으로 Kafka·Redis·Secret까지 복원하는 절차는 아니다.

## 업로드한 MDF와 플러그인 JAR도 DB 백업에 들어가는가
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; MDF 바이너리 L278~288
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; JAR 바이너리 L461~480
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; saveMdf L186~202
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarGatewayEntity.java @ 6fe71f6b2da6 ; jarFile L31~48
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarBusinessEntity.java @ 6fe71f6b2da6 ; jarFile L31~48
원천: nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 앱 실행 JAR L24~36
```

모델 MDF와 Gateway·Business 플러그인 JAR는 DB의 `BYTEA` 칼럼에 저장한다. 이 자료를 보존하려면 아래 테이블을 DB 백업에서 제외하지 않는다.

| 자료 | 테이블·칼럼 |
|---|---|
| 모델 MDF | tc_model_mdf.mdf_file |
| Gateway JAR | tc_jar_gateway.jar_file |
| Business JAR | tc_jar_business.jar_file |

이 파일들은 DB 복원으로 되돌린다. 앱 실행 이미지의 JAR와 플러그인 저장 자료를 구별한다.

## 스키마를 바꾸기 전에 DB 백업 파일 만들기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 백업 L2194~2217
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 외부 스키마 적용 L51~68
```

DB 담당자는 스키마 변경을 동반한 배포 전에 백업한다. PostgreSQL 도구가 있는 셸에서 대상 호스트·포트를 지정하고, `TC_DB_USER`와 `PGPASSWORD`를 환경변수로 공급한다. 비밀번호를 명령 인자에 적지 않는다.

```sh
pg_dump -h '<DB 호스트>' -p '<Port>' -U "$TC_DB_USER" -d nori_tc -Fc -f "nori_tc_$(date +%Y%m%d-%H%M%S).dump"
```

시각이 들어간 custom 형식 파일로 보존한다. 명령이 실패하면 배포를 진행하지 않는다. 백업 파일은 배포와 검증을 마칠 때까지 보관한다.

## DB 복원 전에 어떤 앱을 정지하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 복원 전 정지 L2219~2235
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 대상 L1~13
원천: nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 대상 L1~13
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 대상 L1~13
```

배포 담당자는 DB 복원 중 네 서버 앱이 자료를 쓰지 않도록 중단 시간을 확보한다. namespace `tc`에서 존재하는 Gateway·Business·Backend HPA를 먼저 제거한 뒤 네 앱을 정지한다. 예시는 세 HPA가 모두 있는 경우다.

```sh
kubectl -n tc delete hpa tc-gateway tc-business tc-ui-backend
```

```sh
kubectl -n tc scale deploy/tc-gateway deploy/tc-business deploy/tc-ui-backend deploy/tc-simulator --replicas=0
```

네 앱의 Pod가 모두 종료됐는지 확인한 뒤 복원한다. 정지·재기동의 일반 절차는 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다.

## 백업 파일로 기존 DB 내용을 되돌리기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 복원 L2219~2245
```

DB 담당자는 네 서버 앱을 정지한 상태에서 복원할 DB와 백업 시점을 대조한다. `TC_DB_USER`·`PGPASSWORD`는 백업 때와 같은 방식으로 공급한다.

```sh
pg_restore -h '<DB 호스트>' -p '<Port>' -U "$TC_DB_USER" -d nori_tc --clean --if-exists '<백업 파일>'
```

`--clean --if-exists`는 백업에 포함된 기존 객체를 제거하고 복원하므로 대상 DB를 확인하고 실행한다. 복원 오류가 있으면 앱을 올리지 않고 DB 담당자가 오류와 복원 결과를 확인한다.

## 복원한 DB의 스키마 이력을 어떻게 맞추는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 복원 뒤 이력 확인 L2244~2263
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 입력·migration 위치 L61~68
원천: nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; Hibernate 구조 검증 L89~93
```

DB 담당자는 복원 뒤 `nori-tc-exec` 루트에서 대상 DB의 `TC_DB_URL`·`TC_DB_USER`·`TC_DB_PASSWORD`를 공급한다. Flyway는 `PGPASSWORD` 대신 `TC_DB_PASSWORD`를 읽는다.

```sh
./gradlew flywayInfo
```

마지막 성공 버전을 기동할 앱 판본의 `db/migration`과 대조한다. 복원본이 낮으면 그 판본의 `flywayMigrate`를 수행하고 이력을 다시 확인한 뒤 기동한다. 앱은 `ddl-auto=validate`로 테이블 구조를 검증하므로 기동 성공만으로 이력 대조를 대신하지 않는다.

## 복원 뒤 조정 조회 권한도 확인해야 하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 복원 뒤 권한 확인 L2265~2268
원천: nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 확인 질의·권한 연결 L32~58
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 권한 초기화 L18~25
```

DB 담당자는 복원 뒤 `COORDINATION_ADMIN_READ`와 ADMIN 그룹의 연결 건수를 확인한다. V8 파일의 확인 질의 결과가 1이어야 한다. 과거에 초기 시드를 적용한 DB는 V8 성공 이력만으로 이 연결을 판정할 수 없다.

연결이 없으면 `00.rag/admin/installation-and-service-lifecycle.md`의 「계정 시드 뒤 조정 조회 권한을 복원하기」 절차로 보완한다. 권한을 보완하려고 초기 계정 시드를 다시 넣지 않는다.

## 설치 설정과 인도 자료는 무엇을 보존하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설정과 문서 백업 L2153~2158
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; migration 경로 L61~68
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 배포 입력 L334~422
원천: nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 앱 이미지 L24~36
```

설치 담당자는 인도 패키지의 차트·설정 파일·인도 문서를 보존한다. `deploy/helm/`의 설치 값, `config/`의 설정, `db/migration/`을 복원할 앱 판본과 함께 관리한다.

재배포에는 보존한 차트 값과 해당 앱 이미지 태그를 사용한다. 앱 실행 JAR는 이미지에 들어가고 운영 설정은 별도 공급하므로 이미지 확보와 설정 백업을 나누어 준비한다.

## 운영 Secret 네 개를 안전하게 백업하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Secret 백업 L2174~2185
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 생성 L204~211
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 생성 L299~305
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 생성 L409~418
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; Simulator Secret L527~537
```

Secret 조회 권한이 있는 담당자는 설치 완료 뒤와 운영 설정을 바꿀 때 네 설정 Secret을 보관한다. 접근을 제한한 작업 폴더를 `TC_WORK`에 지정하고 화면·로그로 내용을 출력하지 않는다.

```sh
kubectl -n tc get secret tc-gateway-config tc-business-config tc-ui-backend-config tc-simulator-config -o yaml > "$TC_WORK/tc-secrets-backup.yaml"
```

보관 파일에는 자격이 들어 있으므로 자격 보관소와 같은 수준으로 보호한다. 이미지 pull 자격의 재발급은 `00.rag/admin/security-operations.md`에서 다룬다.

## 보관한 Secret을 언제 복원하고 반영하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Secret 복원 L2185~2192
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L172~179
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L166~173
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L161~168
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L106~113
원천: nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 기동 설정 import L19~26
```

설정까지 잃었거나 백업 시점으로 되돌릴 때, 담당자는 복원할 앱에 맞는 Secret 사본을 고른다. 내보낸 YAML의 `resourceVersion`·`uid`·`creationTimestamp`를 제거한 사본을 적용한다.

```sh
kubectl apply -f "$TC_WORK/tc-secrets-backup.yaml"
```

같은 리소스의 설정을 되돌리는 작업이므로 현재 설정과 복원 대상을 먼저 대조한다. 네 서버 앱을 다시 기동해 설정을 읽힌다. 반영·실패 시 되돌림 방법은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`를 따른다.

## Kafka 메시지를 재처리할 수 있는 범위 찾기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 보관 범위·조회 L2153~2172
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 앱 메시징 설정 공급 L204~211
```

Kafka 담당자는 브로커 보관 설정을 확인해 남아 있는 메시지의 재처리 범위를 판단한다. namespace `kafka`의 대상 브로커 Pod를 `POD`에 넣고, 브로커 안에 Kafka 도구가 있는 구성에서 기본 보관 항목을 조회한다.

```sh
kubectl exec -n kafka "$POD" -- bin/kafka-configs.sh --bootstrap-server localhost:9092 --describe --entity-type brokers --entity-default | grep -i retention
```

이 명령의 대상은 기본 브로커 설정이다. 토픽별 실제 보존 범위는 Kafka 담당자가 함께 대조한다. 메시지 형식은 `00.rag/admin/kafka-topic-and-message-reference.md`에서 다룬다.

## Redis를 잃으면 어떤 자료가 함께 사라지는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 손실과 영속 구성 L2157~2162
원천: nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/store/OperationLedgerRedisStore.java @ 6fe71f6b2da6 ; 조정 작업 저장 L59~87
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; 실패 보관 자료 조회 L95~128
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/async/AsyncResultStoreService.java @ 985d17c604dd ; 비동기 결과 저장 L98~145
```

Redis에는 조정 작업 원장, 처리 실패로 보관한 메시지와 비동기 결과도 저장한다. Redis 자료를 전부 잃으면 앱 재기동만으로 이전 조정 작업의 증적과 보관 메시지가 복구되지 않는다.

인프라 담당자는 Redis 영속 구성을 준비하고 그 백업·복원 절차를 맡는다. DB 백업과 별도로 Redis 자료의 보존 여부를 인계받는다. 손실 범위를 확인할 때 진행 중 조정 작업과 실패 보관 자료도 포함한다.

## Redis 손실 뒤 앱 기동 전에 필요한 조정 초기 값
```rag-meta
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 발급 전제·기존 값 보존 L4~16
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조회·미발급 시 발급 L63~98
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/UiCoordinationContext.java @ 985d17c604dd ; 초기 값 필수 검사 L40~53
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 전체 손실 L2160~2162
```

조정 기능을 켠 앱은 Redis의 `tc:coord:v1:cluster:epoch`가 없으면 기동을 거부한다. Redis를 전부 잃어 초기 값이 없으면 구 Pod를 모두 종료한 뒤 운영 절차로 다시 발급해야 한다.

`coord-epoch.sh`의 `issue`는 값이 없을 때만 발급하며 기존 값을 덮어쓰지 않는다. 초기 값 재발급은 과거 작업 증적의 복원이 아니다. 접속 설정·발급·소유권 점검은 `00.rag/admin/coordination-ownership-and-recovery-check.md`가 소유한다.

## 데이터 복원 뒤 서비스를 어떤 순서로 확인하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 복원 뒤 재기동 L2224~2229
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 기동 순서 L1243~1267
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; Gateway 성공 판정 L33~57
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 앱별 배포 L334~422
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 검증 L1330~1398
```

배포 담당자는 DB 스키마·설정 Secret·조정 초기 값을 준비한 뒤 Backend·Business·Gateway·Simulator 순으로 다시 기동한다. 보존한 앱 판본과 설치 값을 사용하고 제거했던 자동 확장도 복원한다.

기동 명령과 서비스별 준비 판정은 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다. Gateway 검증이 실패하거나 앱 기동 오류가 있으면 복원 완료로 처리하지 않는다. 서버 준비 뒤 화면 로그인과 복원 대상 기능을 확인한다.

## 백업 주기와 최근 성공 여부는 어디서 확인하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 운영 합의·배포 전 백업 L2194~2199
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인프라 담당 범위 L2160~2166
```

정기 백업 주기와 보존 기간은 운영 합의로 정한다. 운영 담당자에게 합의된 주기·보존 기간과 백업 실행 기록을 인계받고, DB·Kafka·Redis 담당자에게 대상별 보존 범위를 확인한다.

최근 성공 여부는 실행 기록과 해당 시점의 보관 파일을 대조해 확인한다. 저장소에 백업 절차가 있다는 사실과 현장의 백업 성공을 구별한다. 복원에 사용할 사본의 시점과 대상 DB·앱 판본도 함께 기록한다.

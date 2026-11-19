# 설치·기동·정지·확장 작성 기록

## 대상

- objectKey: `00.rag/admin/installation-and-service-lifecycle.md`
- 구획: admin `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 운영
- 검색 원문 SHA-256: `2c142fd8dec2249c4e535007224e0e917f07a6bb38cff2dc3d8fa854386cd187`
- 검색 원문: 440줄, 29절
- 확인일: 2026-11-19 (한국 시간). 조사는 2026-11-18에 시작했고 작성 종료 시 날짜 변경을 반영했다.
- 작성: 이 독립 세션에서 조사·작성·검사했다. 다른 Agent·독립 CLI에 위임하지 않았다.
- 범위: 지정 검색 원문과 이 작성 기록만 생성했다. 업로드·제품 수정·git add/commit/push·Docker·kubectl·SSH 실행은 하지 않았다.

## 원천 판본

기존 사실의 기준 commit 인용은 보존했다. 이번 B·D 변경 사실은 현재 작업 트리의 생성 스크립트와 새 V9를 직접 읽고 파일 SHA-256으로 결속했다. 아래 표의 기존 판본과 이번 변경 파일을 구별한다.

| 저장소 | HEAD | 인용 파일 상태 |
|---|---|---|
| nori-tc-exec | f1d5e0a8e727 기반 | 기존 31개 인용 경로 및 새 V9. 생성 스크립트는 M, V9는 ??, 둘 다 SHA-256 원천 사용. 기존 변경되지 않은 사실의 commit 참조는 유지. |
| nori-tc-ui-front | 9fe69a75dcd3 | 6개 인용 경로: stdout 빈 값, exit 0 |
| nori-tc-gateway | d3636ab86763 | 5개 인용 경로: stdout 빈 값, exit 0 |
| nori-tc-business | 7db24f7466bd | 1개 인용 경로: stdout 빈 값, exit 0 |

이번 작업 트리 원천:

| 파일 | 현재 SHA-256 | 상태·직접 확인 |
|---|---|---|
| nori-tc-exec/create-k8s-secrets.sh | fb6002bcd77ad3b1e33b35058906931b9cf60193d6abac3adc6d8381877aeec4 | M. Gateway L73·Business L231·Backend L328의 linger.ms=0 및 생성 전용 호출 L206~213·L303~309·L416~424. |
| nori-tc-exec/db/migration/V9__widen_plc_register_order_columns.sql | 830cbdffb3c97789cb32f8f3942d5321be1f9196ce7c10e2db75fa5ea301f7d1 | ??. L16~18의 tc_model_plc_register.byte_order·word_order VARCHAR(20) 확장. |

실제 명령: `git -c safe.directory=<해당 저장소 절대경로> -C <저장소> rev-parse --short=12 HEAD`, `git -c safe.directory=<해당 저장소 절대경로> -C <저장소> status --short -- <인용 경로들>`. 전역 Git 설정은 바꾸지 않았다. 최초 형제 저장소 조회는 Windows 소유자 차이에 따른 dubious ownership으로 exit 128이었다. 저장소를 특정한 프로세스 한정 -c 옵션으로 읽었다. status는 전역 ignore 파일 Permission denied 경고를 냈으나 각 명령은 exit 0이며 대상 추적 파일 변경 출력은 없었다.

작성 규칙과 문서 목록 전체를 읽었고, 기존 문서의 겹치는 부분은 `tc-components-and-terms.md`의 Shard/Lease/spare 설명, `equipment-registration-and-settings.md`의 설비 배정, `demo-execution.md`의 실행 중지, `deployment-workflow.md`의 화면 배포, `getting-started.md`의 운영 관리자 진입과 대조했다. 화면의 설비 명령·데모 중지·모델 배포와 운영 Deployment 수명 관리를 구별했다.

추가로 읽은 관련 시험(실행하지 않음): GatewayKafkaOperationalInvariantCheckerTest의 같은 파티션 통과·다른 파티션 실패, GatewayKafkaShardPropertiesTest의 합집합 누락·중복·범위 초과 실패, GatewayCoordinationPropertiesTest의 실제 timeout 8000ms 거부. 시험 PASS를 재사용하거나 새 PASS로 기록하지 않았다.

## 절별 사실 대조

각 표는 본문의 모든 문단·목록 항목·표 자료 행·코드 블록을 원문 줄과 함께 전부 수록한다. 같은 행 안에 문장이 여러 개면 모두 대조 대상이다. 원천 위치 열에는 해당 절에서 실제 대조한 파일·판본·범위를 완전한 경로로 기록했다. 문서 간 소유권 안내는 사용자 지시의 objectKey 소유 경계에 근거한다.

### 1. 설치 전에 어떤 자원과 작업 권한을 준비하는가

운영 가이드의 작업 PC·사전 준비와 앱의 외부 설정 import, 화면 프록시 FQDN을 대조했다. 백업 문서 안내는 사용자가 지정한 소유 경계다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L17~17 | 설치 담당자는 Kubernetes·Helm 배포 권한, 이미지 내려받기 권한, PostgreSQL 스키마 적용 권한과 Kafka·Redis 사용 경로를 준비한다. 설치 명령은 작업 PC에서 실행한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 사전 준비·배포 순서 L294~316 ; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 접속 입력 L61~68 ; nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 설정·준비 검사 L20~50 ; nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; API 프록시 L43~48 | 운영 가이드의 작업 PC·사전 준비와 앱의 외부 설정 import, 화면 프록시 FQDN을 대조했다. 백업 문서 안내는 사용자가 지정한 소유 경계다. |
| L19~19 | 제공된 화면 이미지의 API 프록시는 namespace `tc`의 `tc-ui-backend`를 가리킨다. 이 구성을 사용할 때 namespace는 `tc`로 준비한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 사전 준비·배포 순서 L294~316 ; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 접속 입력 L61~68 ; nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 설정·준비 검사 L20~50 ; nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; API 프록시 L43~48 | 운영 가이드의 작업 PC·사전 준비와 앱의 외부 설정 import, 화면 프록시 FQDN을 대조했다. 백업 문서 안내는 사용자가 지정한 소유 경계다. |
| L21~21 | DB 스키마·Kafka 토픽·설정 Secret·Redis 초기 값을 먼저 준비한 뒤 앱을 배포한다. 아래 명령의 이미지 태그와 화면 호스트는 설치 담당자가 확보한 값을 사용한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 사전 준비·배포 순서 L294~316 ; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 접속 입력 L61~68 ; nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 설정·준비 검사 L20~50 ; nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; API 프록시 L43~48 | 운영 가이드의 작업 PC·사전 준비와 앱의 외부 설정 import, 화면 프록시 FQDN을 대조했다. 백업 문서 안내는 사용자가 지정한 소유 경계다. |
| L23~23 | 백업·복원은 `00.rag/admin/backup-and-recovery.md`에서 다룬다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 사전 준비·배포 순서 L294~316 ; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 접속 입력 L61~68 ; nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 설정·준비 검사 L20~50 ; nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; API 프록시 L43~48 | 운영 가이드의 작업 PC·사전 준비와 앱의 외부 설정 import, 화면 프록시 FQDN을 대조했다. 백업 문서 안내는 사용자가 지정한 소유 경계다. |

### 2. 이미지 pull Secret과 레지스트리를 맞추는 방법

실제 Deployment의 imagePullSecrets와 이미지 문자열을 values 입력에 대응했다. 레지스트리 실제값·자격은 기록하지 않았다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L32~32 | 차트는 `global.imagePullSecret`이 가리키는 Secret으로 이미지를 받는다. 기본 리소스는 `harbor-pull-secret`이다. 배포할 namespace에 이미지 내려받기 자격을 먼저 등록한다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 이미지 입력 L1~7 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 참조 L42~47 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 이미지 참조 L29~34 | 실제 Deployment의 imagePullSecrets와 이미지 문자열을 values 입력에 대응했다. 레지스트리 실제값·자격은 기록하지 않았다. |
| L34~34 | 설치 환경에 맞춰 Helm의 `global.registry`·`common.projectName`·`app.imageTag`를 지정한다. pull Secret 리소스를 다르게 만들었다면 `global.imagePullSecret`도 함께 지정한다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 이미지 입력 L1~7 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 참조 L42~47 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 이미지 참조 L29~34 | 실제 Deployment의 imagePullSecrets와 이미지 문자열을 values 입력에 대응했다. 레지스트리 실제값·자격은 기록하지 않았다. |

### 3. 설비와 화면을 받을 Gateway를 먼저 준비하는가

TCPRoute parentRefs·sectionName과 HTTPRoute parentRefs·hostname을 읽었다. 부모 Gateway 및 TLS 준비 절차는 O §4.2와 대조했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L44~44 | 기본 차트는 `envoy-gateway-system`의 `nori-gateway`를 부모로 참조한다. 인프라 담당자는 TCP listener `tc-gw-6100`·`tc-gw-6200`·`tc-gw-6300`과 화면용 HTTPS listener를 준비한다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 부모 Gateway·TCPRoute L129~146 ; nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; 부모 listener 참조 L1~21 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L1~21 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Gateway와 listener L365~515 | TCPRoute parentRefs·sectionName과 HTTPRoute parentRefs·hostname을 읽었다. 부모 Gateway 및 TLS 준비 절차는 O §4.2와 대조했다. |
| L46~46 | 부모 Gateway는 namespace `tc`의 Route를 받아들여야 한다. 설비용 listener는 각각 6100·6200·6300 Port를 사용한다. 화면의 `gateway.hostname`은 HTTPS 호스트·이름 풀이·로그인 허용 출처와 맞춘다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 부모 Gateway·TCPRoute L129~146 ; nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; 부모 listener 참조 L1~21 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L1~21 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Gateway와 listener L365~515 | TCPRoute parentRefs·sectionName과 HTTPRoute parentRefs·hostname을 읽었다. 부모 Gateway 및 TLS 준비 절차는 O §4.2와 대조했다. |

### 4. 앱 기동 전에 Kafka 토픽을 준비하는 방법

운영 토픽 생성 인자는 O §4.3과 대조하고 기본 명령 파티션 6은 Secret 생성 코드 L127로 확인했다. 격리 스크립트의 test. 강제와 기동 시 파티션 검사를 읽었다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L57~57 | Kafka 담당자는 앱 설정의 운영 토픽을 미리 만든다. 기본 명령 토픽 생성 예는 다음과 같다. Kafka 도구가 있는 환경에서 설치 대상 bootstrap 주소를 `KAFKA`에 넣고 실행한다. 복제 계수 3에는 브로커 3대 이상이 필요하다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 운영 토픽 생성 L516~583 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 토픽과 파티션 설정 L83~88 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 명령 파티션 수 L127~127 ; nori-tc-exec/kafka-test-topics.sh @ f1d5e0a8e727 ; 격리 토픽만 허용 L67~80 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 파티션 수 강제 L156~227 | 운영 토픽 생성 인자는 O §4.3과 대조하고 기본 명령 파티션 6은 Secret 생성 코드 L127로 확인했다. 격리 스크립트의 test. 강제와 기동 시 파티션 검사를 읽었다. |
| L59~61 | ```sh / bin/kafka-topics.sh --bootstrap-server "$KAFKA" --create --if-not-exists --topic tc.eqp.commands --partitions 6 --replication-factor 3 / ``` | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 운영 토픽 생성 L516~583 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 토픽과 파티션 설정 L83~88 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 명령 파티션 수 L127~127 ; nori-tc-exec/kafka-test-topics.sh @ f1d5e0a8e727 ; 격리 토픽만 허용 L67~80 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 파티션 수 강제 L156~227 | 운영 토픽 생성 인자는 O §4.3과 대조하고 기본 명령 파티션 6은 Secret 생성 코드 L127로 확인했다. 격리 스크립트의 test. 강제와 기동 시 파티션 검사를 읽었다. |
| L63~63 | 생성 뒤 `--describe`로 실제 파티션 수를 확인한다. 이미 있는 토픽은 생성 명령이 건너뛴다. `kafka-test-topics.sh`는 `test.` 토픽 전용이므로 운영 토픽 생성에 사용하지 않는다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 운영 토픽 생성 L516~583 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 토픽과 파티션 설정 L83~88 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 명령 파티션 수 L127~127 ; nori-tc-exec/kafka-test-topics.sh @ f1d5e0a8e727 ; 격리 토픽만 허용 L67~80 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 파티션 수 강제 L156~227 | 운영 토픽 생성 인자는 O §4.3과 대조하고 기본 명령 파티션 6은 Secret 생성 코드 L127로 확인했다. 격리 스크립트의 test. 강제와 기동 시 파티션 검사를 읽었다. |

### 5. 신규 DB 스키마를 앱보다 먼저 적용하기

Flyway 환경변수 우선 입력·migration 경로·baselineVersion=1·baselineOnMigrate와 ddl-auto=validate를 대조했다. 실행 결과를 측정한 것은 아니다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L73~73 | 스키마 적용은 `nori-tc-exec` 루트에서 수행한다. 형제 위치에 `nori-tc-common`을 준비하고, 설치 DB의 `TC_DB_URL`·`TC_DB_USER`·`TC_DB_PASSWORD`를 환경변수로 공급한다. | nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 설정 L51~68 ; nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 스키마 검증 L89~93 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 스키마 적용 L585~712 | Flyway 환경변수 우선 입력·migration 경로·baselineVersion=1·baselineOnMigrate와 ddl-auto=validate를 대조했다. 실행 결과를 측정한 것은 아니다. |
| L75~79 | ```sh / ./gradlew flywayInfo / ./gradlew flywayMigrate / ./gradlew flywayInfo / ``` | nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 설정 L51~68 ; nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 스키마 검증 L89~93 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 스키마 적용 L585~712 | Flyway 환경변수 우선 입력·migration 경로·baselineVersion=1·baselineOnMigrate와 ddl-auto=validate를 대조했다. 실행 결과를 측정한 것은 아니다. |
| L81~81 | 마지막 출력에서 V1~V9 적용 상태를 확인한다. 빈 DB에서는 V1을 실행하고, 이력 없이 기존 테이블이 있으면 버전 1로 baseline 처리한다. 앱의 `ddl-auto=validate`는 스키마를 생성하지 않는다. | nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Flyway 설정 L51~68 ; nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 스키마 검증 L89~93 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 스키마 적용 L585~712 ; nori-tc-exec/db/migration/V9__widen_plc_register_order_columns.sql @ 1d2879d14406 ; V9 칼럼 확장 L16~18 | Flyway 환경변수 우선 입력·migration 경로·baselineVersion=1·baselineOnMigrate와 ddl-auto=validate를 대조했다. V9의 두 칼럼 VARCHAR(20) 확장과 Gradle의 filesystem:db/migration 입력을 대조했다. 실제 DB 적용은 실행하지 않았다. |

### 6. 초기 계정 시드를 기존 운영 DB에 다시 넣어도 되는가

시드 TRUNCATE·CASCADE, INSERT 칼럼 목록과 V3의 NOT NULL을 읽었다. 시드 직행이 실패하는 원인을 확인하고 O §4.5의 임시 기본값 절차를 서술했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L91~91 | `docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql`은 신규 DB에 한 번 적용하는 자료다. 계정·그룹·권한·세션을 비우므로 기존 운영 DB에 재실행하지 않는다. | nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 초기화·계정 삽입 L12~25 ; nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 계정 칼럼 L169~172 ; nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 변경 시각 필수화 L20~29 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 초기 계정 준비 L714~813 | 시드 TRUNCATE·CASCADE, INSERT 칼럼 목록과 V3의 NOT NULL을 읽었다. 시드 직행이 실패하는 원인을 확인하고 O §4.5의 임시 기본값 절차를 서술했다. |
| L93~93 | V3 적용 뒤에는 시드 삽입문에 없는 `password_changed_at`이 필수다. DB 담당자는 이 칼럼에 `now()` 기본값을 임시로 부여하고 시드를 적용한 뒤 기본값을 제거한다. 계정은 새 비밀번호를 지정하고 활성화한 뒤 사용한다. | nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 초기화·계정 삽입 L12~25 ; nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 계정 칼럼 L169~172 ; nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 변경 시각 필수화 L20~29 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 초기 계정 준비 L714~813 | 시드 TRUNCATE·CASCADE, INSERT 칼럼 목록과 V3의 NOT NULL을 읽었다. 시드 직행이 실패하는 원인을 확인하고 O §4.5의 임시 기본값 절차를 서술했다. |

### 7. 계정 시드 뒤 조정 조회 권한을 복원하기

시드가 권한 정의를 초기화하는 범위와 V8의 두 INSERT ON CONFLICT DO NOTHING을 대조했다. count=1 질의 조건은 V8 L32~36에 있다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L102~102 | V8 뒤에 초기 시드를 적용했다면 DB 담당자는 `db/migration/V8__add_coordination_admin_read_permission.sql`을 다시 적용한다. 이 파일은 충돌 시 기존 행을 유지하며 권한과 ADMIN 그룹 연결을 넣는다. | nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 권한 초기화·ADMIN 부여 L18~25 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 권한과 ADMIN 연결 L32~58 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 시드 뒤 V8 적용 L782~812 | 시드가 권한 정의를 초기화하는 범위와 V8의 두 INSERT ON CONFLICT DO NOTHING을 대조했다. count=1 질의 조건은 V8 L32~36에 있다. |
| L104~104 | `COORDINATION_ADMIN_READ`와 ADMIN 그룹의 연결 건수가 1인지 확인한다. Flyway는 적용 완료한 V8을 자동으로 다시 실행하지 않으므로 시드 후 보완은 파일의 SQL 적용으로 수행한다. | nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 권한 초기화·ADMIN 부여 L18~25 ; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 ; 권한과 ADMIN 연결 L32~58 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 시드 뒤 V8 적용 L782~812 | 시드가 권한 정의를 초기화하는 범위와 V8의 두 INSERT ON CONFLICT DO NOTHING을 대조했다. count=1 질의 조건은 V8 L32~36에 있다. |

### 8. 앱 설정 Secret은 어떻게 준비하는가

실제 세 Secret 생성 구문과 세 번째 생성 전 자격 검사 순서를 확인했다. Simulator 설정 리소스·키는 가이드와 차트의 볼륨 참조를 대조했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L115~115 | `create-k8s-secrets.sh`는 `tc-gateway-config`·`tc-business-config`·`tc-ui-backend-config`를 만든다. 설치할 DB·Kafka·Redis·화면 허용 출처를 반영한 뒤 실행한다. | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Secret 생성 L204~211 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business·UI 설정 생성 L299~305 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; UI 자격 검사·생성 L400~418 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L106~113 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 앱 설정 부트스트랩 L814~1064 ; nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58 | 실제 세 Secret 생성 구문과 세 번째 생성 전 자격 검사 순서를 확인했다. Simulator 설정 리소스·키는 가이드와 차트의 볼륨 참조를 대조했다. |
| L117~117 | `TC_DB_USER`·`TC_DB_PASSWORD`·`TC_UI_SIMULATOR_INTERNAL_TOKEN`을 모두 준비하고 `bash ./create-k8s-secrets.sh`를 실행한다. 마지막 변수 검사는 앞의 두 Secret 생성 뒤에 있으므로 누락하면 일부만 만들어진다. | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Secret 생성 L204~211 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business·UI 설정 생성 L299~305 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; UI 자격 검사·생성 L400~418 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L106~113 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 앱 설정 부트스트랩 L814~1064 | 실제 세 Secret 생성 구문과 세 번째 생성 전 자격 검사 순서를 확인했다. Simulator 설정 리소스·키는 가이드와 차트의 볼륨 참조를 대조했다. |
| L119~119 | `tc-simulator-config`는 별도로 준비한다. 키는 `tc-db.properties`·`tc-log.properties`·`tc-messaging.properties`·`tc-simulator.properties`이다. 시뮬레이터와 UI의 제어 인증 값은 일치시킨다. | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Secret 생성 L204~211 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business·UI 설정 생성 L299~305 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; UI 자격 검사·생성 L400~418 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 볼륨 L106~113 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 앱 설정 부트스트랩 L814~1064 | 실제 세 Secret 생성 구문과 세 번째 생성 전 자격 검사 순서를 확인했다. Simulator 설정 리소스·키는 가이드와 차트의 볼륨 참조를 대조했다. |

### 9. 수정된 스크립트로 Kafka 메시징 설정 생성하기

현재 작업 트리의 생성 입력 Gateway L67~74·Business L226~232·Backend L323~329에서 delivery/request 30000과 linger.ms=0을 확인했다. 기존의 입력 누락·운영자 추가 편집 안내는 이 수정이 들어간 판본의 자동 포함 안내로 바꿨다. 앱 설정 사본의 0과 진단 문서 안내는 유지한다. 의존성 버전은 본문에 쓰지 않았다.

- 제목 질문 식별·원천 일치·독립 답·소유 경계: 적합. 신규 생성 조치 및 결과 확인만 소유하고 증상 진단을 복제하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L131~131 | 이 수정이 들어간 `create-k8s-secrets.sh`는 Gateway·Business·UI Backend 메시징 생성 입력 세 곳에 다음 값을 포함한다. 신규 Secret은 이 입력으로 생성한다. | nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 메시징 생성 입력 L67~74 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 메시징 생성 입력 L226~232 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 메시징 생성 입력 L323~329 ; nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58 ; nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L44~53 ; nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58 | 현재 작업 트리의 생성 입력 Gateway L67~74·Business L226~232·Backend L323~329에서 delivery/request 30000과 linger.ms=0을 확인했다. 기존의 입력 누락·운영자 추가 편집 안내는 이 수정이 들어간 판본의 자동 포함 안내로 바꿨다. 앱 설정 사본의 0과 진단 문서 안내는 유지한다. 의존성 버전은 본문에 쓰지 않았다. |
| L133~135 | ```properties / spring.kafka.producer.properties.linger.ms=0 / ``` | nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 메시징 생성 입력 L67~74 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 메시징 생성 입력 L226~232 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 메시징 생성 입력 L323~329 ; nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58 ; nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L44~53 ; nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58 | 현재 작업 트리의 생성 입력 Gateway L67~74·Business L226~232·Backend L323~329에서 delivery/request 30000과 linger.ms=0을 확인했다. 기존의 입력 누락·운영자 추가 편집 안내는 이 수정이 들어간 판본의 자동 포함 안내로 바꿨다. 앱 설정 사본의 0과 진단 문서 안내는 유지한다. 의존성 버전은 본문에 쓰지 않았다. |
| L137~137 | 생성 뒤에는 각 Secret에서 이 설정을 확인한다. 증상과 진단은 `00.rag/admin/platform-troubleshooting.md`의 「새 이미지인데 Kafka 설정 결함이 다시 나타나는 이유」에서 다룬다. | nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 메시징 생성 입력 L67~74 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 메시징 생성 입력 L226~232 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 메시징 생성 입력 L323~329 ; nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58 ; nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L44~53 ; nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58 | 현재 작업 트리의 생성 입력 Gateway L67~74·Business L226~232·Backend L323~329에서 delivery/request 30000과 linger.ms=0을 확인했다. 기존의 입력 누락·운영자 추가 편집 안내는 이 수정이 들어간 판본의 자동 포함 안내로 바꿨다. 앱 설정 사본의 0과 진단 문서 안내는 유지한다. 의존성 버전은 본문에 쓰지 않았다. |

### 10. 생성된 Secret에서 linger 값만 확인하기

create 구문의 tc-messaging.properties 파일 키를 세 Secret별로 대조하고 O §4.6의 조회·base64 복호화 방식을 읽었다. JSONPath는 해당 파일 키만 고르고 grep -Fx는 확인할 리터럴 한 줄만 출력한다. 운영 명령은 실행하지 않았다. base64 -d와 grep 필터는 메모리의 비민감 합성 입력으로 시험해 0 일치 시 exit 0, 다른 값과 키 누락은 각각 exit 1임을 확인했고 다른 설정 줄은 출력되지 않았다.

- 제목 질문 식별·원천 일치·독립 답·소유 경계: 적합. 신규 생성 조치 및 결과 확인만 소유하고 증상 진단을 복제하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L151~151 | 이 수정 이전 판본으로 만든 Secret은 스크립트를 바꿔도 갱신되지 않는다. `s`에 `tc-gateway-config`·`tc-business-config`·`tc-ui-backend-config`를 하나씩 넣고 각각 실행한다. 메시징 파일 전체를 출력하지 않고 지정한 설정 줄만 출력한다. | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 메시징 파일 키 L204~211 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 메시징 파일 키 L299~305 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 메시징 파일 키 L410~418 ; nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 확인할 설정 L58~58 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Secret 파일 키 조회·복호화 L948~966 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 전용 명령 L206~213 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 전용 명령 L303~309 ; nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 전용 명령 L416~424 | 수정 후에도 세 호출이 kubectl create secret이며 기존 Secret 갱신 명령이 없음을 확인했다. create 구문의 tc-messaging.properties 파일 키를 세 Secret별로 대조하고 O §4.6의 조회·base64 복호화 방식을 읽었다. JSONPath는 해당 파일 키만 고르고 grep -Fx는 확인할 리터럴 한 줄만 출력한다. 운영 명령은 실행하지 않았다. base64 -d와 grep 필터는 메모리의 비민감 합성 입력으로 시험해 0 일치 시 exit 0, 다른 값과 키 누락은 각각 exit 1임을 확인했고 다른 설정 줄은 출력되지 않았다. |
| L153~155 | ```sh / kubectl -n tc get secret "$s" -o jsonpath='{.data.tc-messaging\.properties}' \| base64 -d \| grep -Fx 'spring.kafka.producer.properties.linger.ms=0' / ``` | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 메시징 파일 키 L204~211 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 메시징 파일 키 L299~305 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 메시징 파일 키 L410~418 ; nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 확인할 설정 L58~58 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Secret 파일 키 조회·복호화 L948~966 | create 구문의 tc-messaging.properties 파일 키를 세 Secret별로 대조하고 O §4.6의 조회·base64 복호화 방식을 읽었다. JSONPath는 해당 파일 키만 고르고 grep -Fx는 확인할 리터럴 한 줄만 출력한다. 운영 명령은 실행하지 않았다. base64 -d와 grep 필터는 메모리의 비민감 합성 입력으로 시험해 0 일치 시 exit 0, 다른 값과 키 누락은 각각 exit 1임을 확인했고 다른 설정 줄은 출력되지 않았다. |
| L157~157 | 세 Secret 모두 지정한 줄이 출력되는지 확인한다. 조회 오류나 출력 누락이 있으면 앱 배포를 멈추고 생성 입력과 결과를 다시 대조한다. | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 메시징 파일 키 L204~211 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 메시징 파일 키 L299~305 ; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 메시징 파일 키 L410~418 ; nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 확인할 설정 L58~58 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Secret 파일 키 조회·복호화 L948~966 | create 구문의 tc-messaging.properties 파일 키를 세 Secret별로 대조하고 O §4.6의 조회·base64 복호화 방식을 읽었다. JSONPath는 해당 파일 키만 고르고 grep -Fx는 확인할 리터럴 한 줄만 출력한다. 운영 명령은 실행하지 않았다. base64 -d와 grep 필터는 메모리의 비민감 합성 입력으로 시험해 0 일치 시 exit 0, 다른 값과 키 누락은 각각 exit 1임을 확인했고 다른 설정 줄은 출력되지 않았다. |

### 11. Redis 대기 한도를 지정하지 않으면 기동하는가

Gateway·Business가 Lettuce 실제 command timeout을 가져와 검사하는 호출을 읽었다. Gateway 관계식과 관련 시험의 실패 기대값도 확인했다. 두 Redis 키의 부트스트랩 누락은 스크립트 검색으로 확인했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L167~167 | 기본 조정 설정으로 설치할 때 세 앱의 `tc-redis.properties`에 다음 두 키를 넣는다. Secret 생성 스크립트에는 이 키가 없으므로 생성 전에 보완한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실제 대기 한도 검증 L259~274 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 안전 관계식 L98~127 ; nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationConfiguration.java @ 7db24f7466bd ; 실제 대기 한도 검증 L132~145 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 설정 보완 L886~931 | Gateway·Business가 Lettuce 실제 command timeout을 가져와 검사하는 호출을 읽었다. Gateway 관계식과 관련 시험의 실패 기대값도 확인했다. 두 Redis 키의 부트스트랩 누락은 스크립트 검색으로 확인했다. |
| L169~172 | ```properties / spring.data.redis.timeout=1s / spring.data.redis.connect-timeout=1s / ``` | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실제 대기 한도 검증 L259~274 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 안전 관계식 L98~127 ; nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationConfiguration.java @ 7db24f7466bd ; 실제 대기 한도 검증 L132~145 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 설정 보완 L886~931 | Gateway·Business가 Lettuce 실제 command timeout을 가져와 검사하는 호출을 읽었다. Gateway 관계식과 관련 시험의 실패 기대값도 확인했다. 두 Redis 키의 부트스트랩 누락은 스크립트 검색으로 확인했다. |
| L174~174 | Gateway·Business는 실제 Redis 명령 대기 한도로 소유권 안전 관계식을 검사한다. 관계식을 위반하면 기동이 실패하므로 앱의 조정 설정 선언값만 바꿔 검사를 통과시키지 않는다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실제 대기 한도 검증 L259~274 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 안전 관계식 L98~127 ; nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationConfiguration.java @ 7db24f7466bd ; 실제 대기 한도 검증 L132~145 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Redis 설정 보완 L886~931 | Gateway·Business가 Lettuce 실제 command timeout을 가져와 검사하는 호출을 읽었다. Gateway 관계식과 관련 시험의 실패 기대값도 확인했다. 두 Redis 키의 부트스트랩 누락은 스크립트 검색으로 확인했다. |

### 12. 최초 설치에서 Redis 초기 값을 발급하는 시점

issue 인자 0, GET 선확인과 SET NX, SSH 경유 및 인증 입력 부재를 읽었다. Gateway context는 미발급 시 예외를 던진다. 소유권 상세는 사용자 지정 문서에 한 문장으로 위임했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L183~183 | 조정 기능을 켜는 최초 설치에서는 앱 기동 전에 `tc:coord:v1:cluster:epoch`를 발급한다. 스크립트는 SSH를 경유하므로 `K8S_SSH_KEY`·`K8S_NODE`·`REDIS_NS`·`REDIS_DEPLOY`를 설치 대상으로 지정한다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 인자·발급·덮어쓰기 방지 L34~98 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; 미발급 기동 거부 L40~51 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 초기 발급·인증 조건 L1858~1939 | issue 인자 0, GET 선확인과 SET NX, SSH 경유 및 인증 입력 부재를 읽었다. Gateway context는 미발급 시 예외를 던진다. 소유권 상세는 사용자 지정 문서에 한 문장으로 위임했다. |
| L185~187 | ```sh / bash ./coord-epoch.sh issue 0 / ``` | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 인자·발급·덮어쓰기 방지 L34~98 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; 미발급 기동 거부 L40~51 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 초기 발급·인증 조건 L1858~1939 | issue 인자 0, GET 선확인과 SET NX, SSH 경유 및 인증 입력 부재를 읽었다. Gateway context는 미발급 시 예외를 던진다. 소유권 상세는 사용자 지정 문서에 한 문장으로 위임했다. |
| L189~189 | 기존 값은 유지한다. 스크립트에는 Redis 인증 입력이 없으므로 인증이 필요한 Redis에서는 이 명령을 사용하지 않는다. 다중 서버 소유권 확인은 `00.rag/admin/coordination-ownership-and-recovery-check.md`에서 다룬다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 인자·발급·덮어쓰기 방지 L34~98 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; 미발급 기동 거부 L40~51 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 초기 발급·인증 조건 L1858~1939 | issue 인자 0, GET 선확인과 SET NX, SSH 경유 및 인증 입력 부재를 읽었다. Gateway context는 미발급 시 예외를 던진다. 소유권 상세는 사용자 지정 문서에 한 문장으로 위임했다. |

### 13. 설치 이미지가 없으면 어디서 만드는가

CI 이미지별 분기, Dockerfile app.jar 및 Simulator 세 JAR COPY를 확인했다. 직접 빌드의 라이브러리 준비와 prebuilt 경로는 O §5.1을 대조했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L199~199 | 설치 담당자는 `tc-gateway`·`tc-business`·`tc-ui-backend`·`tc-simulator`·`tc-ui-front` 이미지와 태그를 확보한다. 이미 등록된 이미지를 받으면 설치 중 빌드를 생략한다. | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 이미지 빌드·시뮬레이터 분기 L196~298 ; nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 실행 JAR L24~37 ; nori-tc-exec/deploy/docker/Dockerfile-ci-simulator @ f1d5e0a8e727 ; 세 실행 JAR L30~47 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 이미지 준비 L1070~1235 | CI 이미지별 분기, Dockerfile app.jar 및 Simulator 세 JAR COPY를 확인했다. 직접 빌드의 라이브러리 준비와 prebuilt 경로는 O §5.1을 대조했다. |
| L201~201 | 직접 만들 때 세 서버 앱은 `nori-tc-exec`의 `apps:<앱>-app:bootJar` 산출물을 `app.jar`로 두고 `deploy/docker/Dockerfile-ci`를 사용한다. 시뮬레이터 이미지는 별도 Dockerfile에 orchestrator·worker-equipment·worker-upstream JAR를 함께 넣는다. | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 이미지 빌드·시뮬레이터 분기 L196~298 ; nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 실행 JAR L24~37 ; nori-tc-exec/deploy/docker/Dockerfile-ci-simulator @ f1d5e0a8e727 ; 세 실행 JAR L30~47 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 이미지 준비 L1070~1235 | CI 이미지별 분기, Dockerfile app.jar 및 Simulator 세 JAR COPY를 확인했다. 직접 빌드의 라이브러리 준비와 prebuilt 경로는 O §5.1을 대조했다. |
| L203~203 | CI에서 시뮬레이터 빌드는 `TRIGGER_APP=tc-simulator` 분기로 실행한다. 화면 이미지 빌드와 배포는 `nori-tc-ui-front` 저장소가 소유한다. | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 이미지 빌드·시뮬레이터 분기 L196~298 ; nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 실행 JAR L24~37 ; nori-tc-exec/deploy/docker/Dockerfile-ci-simulator @ f1d5e0a8e727 ; 세 실행 JAR L30~47 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 이미지 준비 L1070~1235 | CI 이미지별 분기, Dockerfile app.jar 및 Simulator 세 JAR COPY를 확인했다. 직접 빌드의 라이브러리 준비와 prebuilt 경로는 O §5.1을 대조했다. |

### 14. 화면 이미지를 직접 만들 때 Backend가 먼저 필요한가

실제 화면 Dockerfile COPY dist·nginx -t와 nginx upstream FQDN을 읽고 O §5.1의 --add-host 인자를 대조했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L212~212 | 화면 Dockerfile은 `dist/`를 복사하고 `nginx -t`로 설정을 검사한다. 프록시 대상 `tc-ui-backend.tc.svc.cluster.local`을 빌드 환경에서 해석할 수 있어야 한다. | nori-tc-ui-front/deploy/docker/Dockerfile-ci @ 9fe69a75dcd3 ; 정적 산출물·설정 검사 L8~16 ; nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; 프록시 이름 해석 L43~48 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 화면 이미지 직접 빌드 L1193~1222 | 실제 화면 Dockerfile COPY dist·nginx -t와 nginx upstream FQDN을 읽고 O §5.1의 --add-host 인자를 대조했다. |
| L214~214 | 클러스터에서 직접 빌드하면 Backend Service를 먼저 준비한다. 작업 PC에서 빌드할 때는 Docker 빌드에 `--add-host tc-ui-backend.tc.svc.cluster.local:127.0.0.1`을 준다. 이미 완성된 화면 이미지를 받는 설치에는 이 빌드 순서 제약이 없다. | nori-tc-ui-front/deploy/docker/Dockerfile-ci @ 9fe69a75dcd3 ; 정적 산출물·설정 검사 L8~16 ; nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; 프록시 이름 해석 L43~48 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 화면 이미지 직접 빌드 L1193~1222 | 실제 화면 Dockerfile COPY dist·nginx -t와 nginx upstream FQDN을 읽고 O §5.1의 --add-host 인자를 대조했다. |

### 15. 다섯 서비스를 어떤 순서로 배포하는가

가이드의 순서 표와 실제 Secret 볼륨 참조를 대조했다. 순서는 매뉴얼의 운영 절차이고 CI 잡 간 의존으로 이 순서를 강제한다고 주장하지 않는다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L223~223 | 스키마·토픽·설정 Secret·초기 값과 이미지를 준비한 뒤 아래 순서로 배포한다. Secret을 붙이지 못하거나 앞 서비스의 기동 확인이 실패하면 다음 배포를 멈춘다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 배포 순서 L1237~1274 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L161~168 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L172~179 | 가이드의 순서 표와 실제 Secret 볼륨 참조를 대조했다. 순서는 매뉴얼의 운영 절차이고 CI 잡 간 의존으로 이 순서를 강제한다고 주장하지 않는다. |
| L225~225 | 1. `tc-ui-backend`를 배포하고 준비 상태를 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 배포 순서 L1237~1274 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L161~168 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L172~179 | 가이드의 순서 표와 실제 Secret 볼륨 참조를 대조했다. 순서는 매뉴얼의 운영 절차이고 CI 잡 간 의존으로 이 순서를 강제한다고 주장하지 않는다. |
| L226~226 | 2. `tc-business`를 배포하고 준비 상태를 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 배포 순서 L1237~1274 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L161~168 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L172~179 | 가이드의 순서 표와 실제 Secret 볼륨 참조를 대조했다. 순서는 매뉴얼의 운영 절차이고 CI 잡 간 의존으로 이 순서를 강제한다고 주장하지 않는다. |
| L227~227 | 3. `tc-gateway`를 배포하고 전용 검증 스크립트로 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 배포 순서 L1237~1274 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L161~168 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L172~179 | 가이드의 순서 표와 실제 Secret 볼륨 참조를 대조했다. 순서는 매뉴얼의 운영 절차이고 CI 잡 간 의존으로 이 순서를 강제한다고 주장하지 않는다. |
| L228~228 | 4. `tc-simulator`를 배포한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 배포 순서 L1237~1274 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L161~168 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L172~179 | 가이드의 순서 표와 실제 Secret 볼륨 참조를 대조했다. 순서는 매뉴얼의 운영 절차이고 CI 잡 간 의존으로 이 순서를 강제한다고 주장하지 않는다. |
| L229~229 | 5. `nori-tc-ui-front` 저장소에서 `tc-ui-front`를 배포한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 배포 순서 L1237~1274 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L161~168 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 필수 설정 볼륨 L172~179 | 가이드의 순서 표와 실제 Secret 볼륨 참조를 대조했다. 순서는 매뉴얼의 운영 절차이고 CI 잡 간 의존으로 이 순서를 강제한다고 주장하지 않는다. |

### 16. Backend·Business·Simulator를 설치하거나 다시 기동하기

세 deploy 잡의 Helm 옵션을 하나의 명령으로 표기했다. a·c는 문서 예제 셸 변수이며 각 값을 대입하면 저장소 잡의 release·chart·values 경로와 일치한다. Simulator 차트 전체에 probe가 없음을 읽었다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L238~238 | `nori-tc-exec` 루트에서 설치할 이미지 태그를 `TAG`에 넣는다. 아래 `a`는 배포 순서대로 `tc-ui-backend`·`tc-business`·`tc-simulator`로 바꿔 각각 실행한다. | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Simulator 배포 L334~347 ; nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Business·Backend 배포 L383~422 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 준비 검사 없는 단일 Pod L1~113 | 세 deploy 잡의 Helm 옵션을 하나의 명령으로 표기했다. a·c는 문서 예제 셸 변수이며 각 값을 대입하면 저장소 잡의 release·chart·values 경로와 일치한다. Simulator 차트 전체에 probe가 없음을 읽었다. |
| L240~244 | ```sh / a=tc-ui-backend / c=deploy/helm/$a / helm upgrade --install "$a" "$c" --namespace tc -f "$c/values.yaml" -f "$c/values-prod.yaml" --set app.imageTag="$TAG" --wait --timeout 300s / ``` | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Simulator 배포 L334~347 ; nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Business·Backend 배포 L383~422 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 준비 검사 없는 단일 Pod L1~113 | 세 deploy 잡의 Helm 옵션을 하나의 명령으로 표기했다. a·c는 문서 예제 셸 변수이며 각 값을 대입하면 저장소 잡의 release·chart·values 경로와 일치한다. Simulator 차트 전체에 probe가 없음을 읽었다. |
| L246~246 | Backend·Business는 준비 판정을 확인한다. Simulator 차트에는 준비·생존 검사가 없으므로 Helm 대기 성공만으로 제어 API의 정상 동작을 확정하지 않는다. | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Simulator 배포 L334~347 ; nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Business·Backend 배포 L383~422 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 준비 검사 없는 단일 Pod L1~113 | 세 deploy 잡의 Helm 옵션을 하나의 명령으로 표기했다. a·c는 문서 예제 셸 변수이며 각 값을 대입하면 저장소 잡의 release·chart·values 경로와 일치한다. Simulator 차트 전체에 probe가 없음을 읽었다. |

### 17. Gateway는 왜 Helm 대기 옵션을 쓰지 않는가

Gateway CI의 --wait 없는 호출과 전용 검증 후속 호출을 대조했다. 예비 Pod의 준비 상태와 모든 replica 대기를 구분했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L254~254 | Gateway는 예비 Pod가 준비 상태가 되지 않을 수 있다. `nori-tc-exec` 루트에서 설치 이미지 태그를 `TAG`에 넣고 `--wait` 없이 배포한다. | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Gateway 배포 L352~370 ; nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 예비 Pod와 검증 L2~57 | Gateway CI의 --wait 없는 호출과 전용 검증 후속 호출을 대조했다. 예비 Pod의 준비 상태와 모든 replica 대기를 구분했다. |
| L256~259 | ```sh / c=deploy/helm/tc-gateway / helm upgrade --install tc-gateway "$c" --namespace tc -f "$c/values.yaml" -f "$c/values-prod.yaml" --set app.imageTag="$TAG" --timeout 300s / ``` | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Gateway 배포 L352~370 ; nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 예비 Pod와 검증 L2~57 | Gateway CI의 --wait 없는 호출과 전용 검증 후속 호출을 대조했다. 예비 Pod의 준비 상태와 모든 replica 대기를 구분했다. |
| L261~261 | Helm 종료 뒤 `sh ./deploy/scripts/verify-gateway-rollout.sh tc`를 실행한다. `gateway rollout verified.` 출력과 종료코드 0을 확인한다. 실패하면 설치 성공으로 처리하지 않는다. | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Gateway 배포 L352~370 ; nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 예비 Pod와 검증 L2~57 | Gateway CI의 --wait 없는 호출과 전용 검증 후속 호출을 대조했다. 예비 Pod의 준비 상태와 모든 replica 대기를 구분했다. |

### 18. Gateway 검증 스크립트는 무엇을 확인하는가

검증 스크립트 실제 조건 TOTAL>0·RUNNING=TOTAL·READY>=min(TOTAL,GROUPS), 기본 파일 입력과 60회·sleep 10을 직접 대조했다. 전체 그룹 소유 보증으로 확대하지 않았다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L268~268 | 검증 스크립트는 기본 입력 `./config/tc-shard.properties`의 그룹 수를 읽는다. 이 파일은 배포한 Gateway 설정과 같아야 한다. 다른 경로는 namespace 뒤 두 번째 인자로 지정한다. | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 설정 입력·통과 조건 L12~57 | 검증 스크립트 실제 조건 TOTAL>0·RUNNING=TOTAL·READY>=min(TOTAL,GROUPS), 기본 파일 입력과 60회·sleep 10을 직접 대조했다. 전체 그룹 소유 보증으로 확대하지 않았다. |
| L270~270 | Pod가 하나 이상이고 모두 Running이며, Ready 수가 `min(Pod 수, 그룹 수)` 이상이면 통과한다. 10초 간격으로 최대 60회 확인한다. | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 설정 입력·통과 조건 L12~57 | 검증 스크립트 실제 조건 TOTAL>0·RUNNING=TOTAL·READY>=min(TOTAL,GROUPS), 기본 파일 입력과 60회·sleep 10을 직접 대조했다. 전체 그룹 소유 보증으로 확대하지 않았다. |
| L272~272 | 이 검사는 원하는 replica 수나 모든 그룹의 소유자를 보증하지 않는다. Pod 수가 그룹 수보다 적어도 통과할 수 있으므로 배포 replica·그룹별 담당 여부도 함께 점검한다. | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 설정 입력·통과 조건 L12~57 | 검증 스크립트 실제 조건 TOTAL>0·RUNNING=TOTAL·READY>=min(TOTAL,GROUPS), 기본 파일 입력과 60회·sleep 10을 직접 대조했다. 전체 그룹 소유 보증으로 확대하지 않았다. |

### 19. 화면 서비스를 Helm으로 설치하기

프론트 저장소의 deploy 잡·기본 values·HTTPRoute hostname을 대조했다. HOST는 URL이 아닌 hostname 필드 입력이다. 환경별 values 파일이 없는 것은 deploy 디렉터리 목록으로 확인했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L281~281 | `nori-tc-ui-front` 루트에서 화면 이미지 태그를 `TAG`, HTTPS 화면 호스트를 `HOST`에 넣고 실행한다. 호스트에는 URL 경로를 넣지 않는다. | nori-tc-ui-front/.gitlab-ci.yml @ 9fe69a75dcd3 ; 화면 배포 L151~167 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 앱·호스트 입력 L9~30 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 호스트 반영 L13~14 | 프론트 저장소의 deploy 잡·기본 values·HTTPRoute hostname을 대조했다. HOST는 URL이 아닌 hostname 필드 입력이다. 환경별 values 파일이 없는 것은 deploy 디렉터리 목록으로 확인했다. |
| L283~286 | ```sh / c=deploy/helm/tc-ui-front / helm upgrade --install tc-ui-front "$c" --namespace tc -f "$c/values.yaml" --set app.imageTag="$TAG" --set gateway.hostname="$HOST" --wait --timeout 300s / ``` | nori-tc-ui-front/.gitlab-ci.yml @ 9fe69a75dcd3 ; 화면 배포 L151~167 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 앱·호스트 입력 L9~30 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 호스트 반영 L13~14 | 프론트 저장소의 deploy 잡·기본 values·HTTPRoute hostname을 대조했다. HOST는 URL이 아닌 hostname 필드 입력이다. 환경별 values 파일이 없는 것은 deploy 디렉터리 목록으로 확인했다. |
| L288~288 | 화면 차트에는 환경별 값 파일이 없으므로 기본 `values.yaml`을 사용하고 설치 환경의 호스트·이미지 입력을 지정한다. | nori-tc-ui-front/.gitlab-ci.yml @ 9fe69a75dcd3 ; 화면 배포 L151~167 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 앱·호스트 입력 L9~30 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 호스트 반영 L13~14 | 프론트 저장소의 deploy 잡·기본 values·HTTPRoute hostname을 대조했다. HOST는 URL이 아닌 hostname 필드 입력이다. 환경별 values 파일이 없는 것은 deploy 디렉터리 목록으로 확인했다. |

### 20. 설치 직후 Pod와 서비스 준비 상태 확인하기

각 서비스 차트의 probe 구현과 경로를 대조했다. 기동 로그·Simulator 인증 확인은 O §5.3의 설치 검증 절차이며 현장에서 실행한 결과가 아니다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L299~302 | ```sh / kubectl -n tc get pods / kubectl -n tc get deploy / ``` | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L66~95 ; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L54~88 ; nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; HTTP 검사 경로 L61~79 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 화면 HTTP 검사 L55~70 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 검증 L1330~1398 | 각 서비스 차트의 probe 구현과 경로를 대조했다. 기동 로그·Simulator 인증 확인은 O §5.3의 설치 검증 절차이며 현장에서 실행한 결과가 아니다. |
| L304~304 | Gateway·Business는 갱신된 상태 파일, Backend는 `/api/actuator/health/readiness`, 화면은 `/` 응답으로 준비 여부를 검사한다. Gateway 예비 Pod는 전용 판정으로 구별한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L66~95 ; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L54~88 ; nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; HTTP 검사 경로 L61~79 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 화면 HTTP 검사 L55~70 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 검증 L1330~1398 | 각 서비스 차트의 probe 구현과 경로를 대조했다. 기동 로그·Simulator 인증 확인은 O §5.3의 설치 검증 절차이며 현장에서 실행한 결과가 아니다. |
| L306~306 | 설치 담당자는 앱 기동 로그의 ERROR와 재시작 발생 여부를 확인한다. Simulator는 Pod 실행 상태에 더해 제어 API 확인을 따로 수행한다. 시뮬레이터 제어 인증이 거부되면 양쪽 설정의 인증 값 일치를 점검한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L66~95 ; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 파일 준비 검사 L54~88 ; nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; HTTP 검사 경로 L61~79 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 화면 HTTP 검사 L55~70 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 검증 L1330~1398 | 각 서비스 차트의 probe 구현과 경로를 대조했다. 기동 로그·Simulator 인증 확인은 O §5.3의 설치 검증 절차이며 현장에서 실행한 결과가 아니다. |

### 21. Route가 설비·화면 연결을 받는지 확인하기

실제 Route 리소스와 이름을 가이드 검증 명령에 대입했다. -o yaml은 Secret 내용을 조회하지 않고 Route 조건을 확인한다. 보안 정책 대조는 설치 마감 절차다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L315~315 | 배포 뒤 아래로 Route 상태를 조회한다. 각 Route의 부모 조건에서 `Accepted=True`·`ResolvedRefs=True`를 확인한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; 설비 Route L1~21 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 화면 Route L1~21 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Route·연결 검증 L1384~1478 | 실제 Route 리소스와 이름을 가이드 검증 명령에 대입했다. -o yaml은 Secret 내용을 조회하지 않고 Route 조건을 확인한다. 보안 정책 대조는 설치 마감 절차다. |
| L317~320 | ```sh / kubectl -n tc get tcproute tc-gw-6100 tc-gw-6200 tc-gw-6300 -o yaml / kubectl -n tc get httproute tc-ui-front -o yaml / ``` | nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; 설비 Route L1~21 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 화면 Route L1~21 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Route·연결 검증 L1384~1478 | 실제 Route 리소스와 이름을 가이드 검증 명령에 대입했다. -o yaml은 Secret 내용을 조회하지 않고 Route 조건을 확인한다. 보안 정책 대조는 설치 마감 절차다. |
| L322~322 | Route 수락 뒤에는 설비 망에서 6100·6200·6300 Port 도달과 HTTPS 화면 로그인을 확인한다. Kafka·Redis 연결의 인증·암호화 구성이 설치 환경 정책과 맞는지도 확인한 뒤 설치를 마감한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; 설비 Route L1~21 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 화면 Route L1~21 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Route·연결 검증 L1384~1478 | 실제 Route 리소스와 이름을 가이드 검증 명령에 대입했다. -o yaml은 Secret 내용을 조회하지 않고 Route 조건을 확인한다. 보안 정책 대조는 설치 마감 절차다. |

### 22. 재기동하면 서비스가 잠시 모두 내려가는가

네 Deployment의 Recreate 구문과 O §7.1 재기동 명령을 확인했다. Gateway 후속 판정은 실제 CI·전용 스크립트를 우선했다. 업그레이드·설정 반영 상세는 넣지 않았다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L333~333 | Gateway·Business·Backend·Simulator 차트는 기존 Pod를 내린 뒤 새 Pod를 만드는 `Recreate` 전략이다. 설치한 서비스를 재기동하려면 서비스 중단 시간을 확보한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~15 ; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~14 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~11 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L9~13 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재기동 L1746~1760 | 네 Deployment의 Recreate 구문과 O §7.1 재기동 명령을 확인했다. Gateway 후속 판정은 실제 CI·전용 스크립트를 우선했다. 업그레이드·설정 반영 상세는 넣지 않았다. |
| L335~338 | ```sh / kubectl -n tc rollout restart deploy/tc-business / kubectl -n tc rollout status deploy/tc-business / ``` | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~15 ; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~14 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~11 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L9~13 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재기동 L1746~1760 | 네 Deployment의 Recreate 구문과 O §7.1 재기동 명령을 확인했다. Gateway 후속 판정은 실제 CI·전용 스크립트를 우선했다. 업그레이드·설정 반영 상세는 넣지 않았다. |
| L340~340 | 대상 이름을 설치한 Deployment로 바꿔 사용한다. Gateway 재기동 뒤에는 일반 rollout 완료 대기 대신 전용 검증 스크립트를 사용한다. 버전·설정 변경 반영은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`에서 다룬다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~15 ; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~14 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L6~11 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; Recreate 전략 L9~13 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 재기동 L1746~1760 | 네 Deployment의 Recreate 구문과 O §7.1 재기동 명령을 확인했다. Gateway 후속 판정은 실제 CI·전용 스크립트를 우선했다. 업그레이드·설정 반영 상세는 넣지 않았다. |

### 23. 자동 확장이 있는 서비스를 정지하고 다시 올리기

HPA 생성 조건과 운영 enabled=true, 매뉴얼의 HPA 제거 후 scale 0 및 Helm 재적용을 대조했다. 자동 확장 제거는 계획 정지 절차로 서술하고 0에서 HPA가 반드시 복귀시킨다고 단정하지 않았다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L350~350 | 운영 값으로 설치한 Gateway·Business·Backend를 정지할 때는 중단할 서비스의 HPA를 먼저 제거한 뒤 replica를 0으로 내린다. 다음은 Gateway 예다. | nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 생성 L1~21 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L17~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L14~18 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 정지·복원 L1761~1776 | HPA 생성 조건과 운영 enabled=true, 매뉴얼의 HPA 제거 후 scale 0 및 Helm 재적용을 대조했다. 자동 확장 제거는 계획 정지 절차로 서술하고 0에서 HPA가 반드시 복귀시킨다고 단정하지 않았다. |
| L352~355 | ```sh / kubectl -n tc delete hpa tc-gateway / kubectl -n tc scale deploy/tc-gateway --replicas=0 / ``` | nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 생성 L1~21 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L17~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L14~18 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 정지·복원 L1761~1776 | HPA 생성 조건과 운영 enabled=true, 매뉴얼의 HPA 제거 후 scale 0 및 Helm 재적용을 대조했다. 자동 확장 제거는 계획 정지 절차로 서술하고 0에서 HPA가 반드시 복귀시킨다고 단정하지 않았다. |
| L357~357 | 대상 Pod가 사라졌는지 확인한다. 다시 올릴 때는 같은 이미지·설치 값을 사용한 서비스별 Helm 설치 명령을 실행한다. 자동 확장이 활성인 값으로 적용하면 HPA도 다시 생성한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 생성 L1~21 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L17~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 운영 자동 확장 L14~18 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 정지·복원 L1761~1776 | HPA 생성 조건과 운영 enabled=true, 매뉴얼의 HPA 제거 후 scale 0 및 Helm 재적용을 대조했다. 자동 확장 제거는 계획 정지 절차로 서술하고 0에서 HPA가 반드시 복귀시킨다고 단정하지 않았다. |

### 24. Simulator와 화면 서비스를 정지하기

제공된 두 차트의 HPA 부재와 Deployment replica 입력을 대조했다. Simulator scale 서브리소스 정지와 Helm 렌더의 1 강제를 구별했다. 두 예제는 중단할 서비스만 실행한다는 조건이다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L366~366 | 제공된 차트의 Simulator·화면 서비스에는 HPA가 없다. 중단할 서비스만 replica를 0으로 내리고 대상 Pod가 사라졌는지 확인한다. | nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica L1~13 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; replica 입력 L1~10 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 자동 확장 없는 정지 L1761~1776 | 제공된 두 차트의 HPA 부재와 Deployment replica 입력을 대조했다. Simulator scale 서브리소스 정지와 Helm 렌더의 1 강제를 구별했다. 두 예제는 중단할 서비스만 실행한다는 조건이다. |
| L368~371 | ```sh / kubectl -n tc scale deploy/tc-simulator --replicas=0 / kubectl -n tc scale deploy/tc-ui-front --replicas=0 / ``` | nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica L1~13 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; replica 입력 L1~10 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 자동 확장 없는 정지 L1761~1776 | 제공된 두 차트의 HPA 부재와 Deployment replica 입력을 대조했다. Simulator scale 서브리소스 정지와 Helm 렌더의 1 강제를 구별했다. 두 예제는 중단할 서비스만 실행한다는 조건이다. |
| L373~373 | 재기동할 때는 설치에 사용한 값으로 각 서비스의 Helm 명령을 다시 적용한다. Simulator를 정지할 목적으로 Helm의 `app.replicaCount`에 0을 주면 단일 replica 검증에서 실패한다. | nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica L1~13 ; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; replica 입력 L1~10 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 자동 확장 없는 정지 L1761~1776 | 제공된 두 차트의 HPA 부재와 Deployment replica 입력을 대조했다. Simulator scale 서브리소스 정지와 Helm 렌더의 1 강제를 구별했다. 두 예제는 중단할 서비스만 실행한다는 조건이다. |

### 25. 운영 값 파일의 replica와 자동 확장 범위

운영 값 파일별 replica·HPA 최소/최대와 프론트 기본값을 행마다 확인했다. 실제 현장 Pod 수로 표현하지 않았다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L384~384 | 아래는 저장소의 운영 값 파일을 적용할 때의 설정이며 현장의 현재 Pod 수가 아니다. 현재 적용값은 `kubectl -n tc get deploy,hpa`로 조회한다. | nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 고정 replica L9~23 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L9~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L8~18 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica 강제 L1~10 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 기본 replica L9~14 | 운영 값 파일별 replica·HPA 최소/최대와 프론트 기본값을 행마다 확인했다. 실제 현장 Pod 수로 표현하지 않았다. |
| L388~388 | \| tc-gateway \| 3 \| 최소 3·최대 3 \| | nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 고정 replica L9~23 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L9~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L8~18 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica 강제 L1~10 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 기본 replica L9~14 | 운영 값 파일별 replica·HPA 최소/최대와 프론트 기본값을 행마다 확인했다. 실제 현장 Pod 수로 표현하지 않았다. |
| L389~389 | \| tc-business \| 2 \| 최소 2·최대 6 \| | nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 고정 replica L9~23 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L9~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L8~18 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica 강제 L1~10 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 기본 replica L9~14 | 운영 값 파일별 replica·HPA 최소/최대와 프론트 기본값을 행마다 확인했다. 실제 현장 Pod 수로 표현하지 않았다. |
| L390~390 | \| tc-ui-backend \| 2 \| 최소 2·최대 6 \| | nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 고정 replica L9~23 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L9~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L8~18 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica 강제 L1~10 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 기본 replica L9~14 | 운영 값 파일별 replica·HPA 최소/최대와 프론트 기본값을 행마다 확인했다. 실제 현장 Pod 수로 표현하지 않았다. |
| L391~391 | \| tc-simulator \| 1 강제 \| 없음 \| | nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 고정 replica L9~23 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L9~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L8~18 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica 강제 L1~10 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 기본 replica L9~14 | 운영 값 파일별 replica·HPA 최소/최대와 프론트 기본값을 행마다 확인했다. 실제 현장 Pod 수로 표현하지 않았다. |
| L392~392 | \| tc-ui-front \| 기본 1 \| 없음 \| | nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 고정 replica L9~23 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L9~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values-prod.yaml @ f1d5e0a8e727 ; 자동 확장 L8~18 ; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 단일 replica 강제 L1~10 ; nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 기본 replica L9~14 | 운영 값 파일별 replica·HPA 최소/최대와 프론트 기본값을 행마다 확인했다. 실제 현장 Pod 수로 표현하지 않았다. |

### 26. Business·Backend를 부하에 맞춰 늘리는 조건

HPA CPU Utilization 구현·목표 70과 Business 주석의 consumer/partition 상한을 확인했다. Backend 풀 최대 20은 values와 Deployment 환경변수 주입을 대조했다. 범위 숫자는 전 절의 표가 소유한다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L404~404 | 운영 값의 Business·Backend는 CPU 이용률 목표 70으로 HPA가 replica를 조절한다. 수동 replica 변경을 유지하려면 HPA와 설치 값도 함께 맞춘다. | nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 파티션 상한·CPU 목표 L15~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; DB 연결 풀 L41~45 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; DB 풀 주입 L74~91 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 확장 제약 L1778~1796 | HPA CPU Utilization 구현·목표 70과 Business 주석의 consumer/partition 상한을 확인했다. Backend 풀 최대 20은 values와 Deployment 환경변수 주입을 대조했다. 범위 숫자는 전 절의 표가 소유한다. |
| L406~406 | Business 상한은 구독 토픽의 파티션 수를 고려한다. 같은 consumer 그룹에서 파티션보다 consumer를 더 늘려도 추가 consumer는 처리할 파티션이 없다. | nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 파티션 상한·CPU 목표 L15~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; DB 연결 풀 L41~45 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; DB 풀 주입 L74~91 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 확장 제약 L1778~1796 | HPA CPU Utilization 구현·목표 70과 Business 주석의 consumer/partition 상한을 확인했다. Backend 풀 최대 20은 values와 Deployment 환경변수 주입을 대조했다. 범위 숫자는 전 절의 표가 소유한다. |
| L408~408 | Backend는 replica별 DB 연결 풀을 사용한다. 기본 최대 풀 크기는 20이다. 상한을 높이기 전에 다른 서비스가 사용하는 연결까지 합쳐 DB 연결 예산을 확인한다. | nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml @ f1d5e0a8e727 ; CPU 기반 확장 L1~21 ; nori-tc-exec/deploy/helm/tc-business/values-prod.yaml @ f1d5e0a8e727 ; 파티션 상한·CPU 목표 L15~21 ; nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; DB 연결 풀 L41~45 ; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; DB 풀 주입 L74~91 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 확장 제약 L1778~1796 | HPA CPU Utilization 구현·목표 70과 Business 주석의 consumer/partition 상한을 확인했다. Backend 풀 최대 20은 values와 Deployment 환경변수 주입을 대조했다. 범위 숫자는 전 절의 표가 소유한다. |

### 27. Gateway를 늘릴 때 Pod 수만 바꾸면 되는가

기본 그룹 설정 두 개와 차트의 group+spare 정책, requiredDuringScheduling의 hostname 분리를 읽었다. 표에 있는 replica 3을 다시 나열하지 않았다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L418~418 | Gateway 기본 그룹은 2개이며 예비 Pod를 1개 두는 구성이다. CPU 부하에 따라 늘리는 구성이 아니다. 그룹을 추가할 때 `app.replicaCount`와 HPA 최소·최대를 그룹 수+예비 1로 함께 맞춘다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 그룹 수와 예비 Pod L23~49 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 노드별 분리 L25~36 ; nori-tc-exec/config/tc-shard.properties @ f1d5e0a8e727 ; 기본 그룹 배정 L21~24 ; nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 최소·최대 고정 L15~23 | 기본 그룹 설정 두 개와 차트의 group+spare 정책, requiredDuringScheduling의 hostname 분리를 읽었다. 표에 있는 replica 3을 다시 나열하지 않았다. |
| L420~420 | Gateway 차트는 같은 앱 Pod를 서로 다른 노드에 배치하도록 강제한다. 늘린 Pod를 놓을 노드가 부족하면 Pending으로 남으므로 사용 가능한 노드도 확인한다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 그룹 수와 예비 Pod L23~49 ; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 노드별 분리 L25~36 ; nori-tc-exec/config/tc-shard.properties @ f1d5e0a8e727 ; 기본 그룹 배정 L21~24 ; nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 최소·최대 고정 L15~23 | 기본 그룹 설정 두 개와 차트의 group+spare 정책, requiredDuringScheduling의 hostname 분리를 읽었다. 표에 있는 replica 3을 다시 나열하지 않았다. |

### 28. Gateway 그룹과 Kafka 파티션을 확장할 때 맞출 값

두 토픽 partition 수 검사와 그룹 ID·파티션 중복·범위·합집합 검사를 읽었다. 관련 시험의 mismatch·누락·중복 예외 기대값을 확인했다. 네 설정 위치 동기화는 O §7.3과 대조했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L429~429 | 그룹의 담당 파티션은 중복 없이 전체를 덮어야 한다. `tc.eqp.commands`·`tc.ui.events.gateway`의 파티션 수와 `tc.gateway.kafka.commands-partition-count`를 같게 맞춘다. 불일치하면 Gateway가 기동을 거부한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 토픽 파티션 일치 검사 L156~227 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayKafkaShardProperties.java @ d3636ab86763 ; 그룹 중복·범위·합집합 검사 L460~501 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 그룹 설정 동기화 L1803~1856 | 두 토픽 partition 수 검사와 그룹 ID·파티션 중복·범위·합집합 검사를 읽었다. 관련 시험의 mismatch·누락·중복 예외 기대값을 확인했다. 네 설정 위치 동기화는 O §7.3과 대조했다. |
| L431~431 | 그룹 변경은 Gateway·Backend Secret의 `tc-shard.properties`, 검증에 쓰는 `config/tc-shard.properties`, Secret 생성 스크립트의 그룹 정의를 함께 맞춘다. 토픽별 메시지 계약은 `00.rag/admin/kafka-topic-and-message-reference.md`에서 다룬다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/config/GatewayKafkaOperationalInvariantChecker.java @ d3636ab86763 ; 토픽 파티션 일치 검사 L156~227 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayKafkaShardProperties.java @ d3636ab86763 ; 그룹 중복·범위·합집합 검사 L460~501 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 그룹 설정 동기화 L1803~1856 | 두 토픽 partition 수 검사와 그룹 ID·파티션 중복·범위·합집합 검사를 읽었다. 관련 시험의 mismatch·누락·중복 예외 기대값을 확인했다. 네 설정 위치 동기화는 O §7.3과 대조했다. |

### 29. Simulator는 여러 Pod로 확장할 수 있는가

Simulator 차트의 int replicaCount!=1 fail과 세 JAR 이미지 구성으로 다중 Pod 확장 제한을 대조했다. 일반적인 자원 증설 제안 문장은 원천 소유 사실이 아니므로 삭제했다.

- 제목만으로 질문 식별: 적합. 원천 일치: 적합. 절 단독 답: 적합. 중복 소유: 상세 사실의 소유 절을 유지하고 필요한 조건만 재언급했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L439~439 | Simulator 차트는 프로세스 내부 상태를 사용하는 구성이므로 `app.replicaCount`가 1이 아니면 렌더를 실패시킨다. worker JAR가 함께 들어 있어도 Pod replica를 늘리는 확장 방식으로 사용하지 않는다. | nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; replica 강제·교체 방식 L1~13 ; nori-tc-exec/deploy/docker/Dockerfile-ci-simulator @ f1d5e0a8e727 ; 세 프로세스 이미지 구성 L1~5 | Simulator 차트의 int replicaCount!=1 fail과 세 JAR 이미지 구성으로 다중 Pod 확장 제한을 대조했다. 일반적인 자원 증설 제안 문장은 원천 소유 사실이 아니므로 삭제했다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 실제 코드·정의 | 작성한 내용 |
|---|---|---|---|
| Gateway 재기동 뒤 판정 | O §7.1 L1750~1753은 rollout restart 뒤 rollout status 예제를 준다. | exec CI L359~370은 예비 Pod의 NotReady 때문에 --wait 없이 배포하고 전용 검증을 호출한다. verify-gateway-rollout.sh L43~50은 Ready 하한으로 판정한다. | Gateway는 일반 rollout 완료 대기 대신 전용 스크립트로 판정한다. |
| Gateway 검증 주석과 실제 비교식 | O §5.3의 설명은 하한을 적는다. 스크립트 머리글 L2와 CI 주석에는 Ready 수 == 그룹 수 표현이 남아 있다. | verify-gateway-rollout.sh L43~50은 READY>=min(TOTAL,GROUPS)이다. 원하는 replica나 전 그룹 소유까지 검사하지 않는다. | 실제 비교식을 쓰고 성공 판정의 한계를 함께 적었다. |
| CI 전체 시험 설명 | O §5.1 L1120~1135는 공통 라이브러리를 받아 전체 빌드·단위 시험으로 묶어 설명한다. | exec .gitlab-ci.yml L85~104는 TRIGGER_APP별 해당 앱 build를 선택하고 Simulator trigger의 이 test 잡을 제외한다. | 전 앱을 항상 시험한다는 문장을 쓰지 않고 이미지 준비·분기만 적었다. |

추가 대조: O §4.4 L588·L712는 마지막 migration을 V8로 설명한다. 현재 새 V9(830cbdffb3c97789cb32f8f3942d5321be1f9196ce7c10e2db75fa5ea301f7d1) L16~18이 추가되어 최신 설치의 적용 확인 범위는 V1~V9로 썼다.

일치하지만 설치 시 주의가 필요한 내용: 시드는 password_changed_at 누락과 계정 초기화가 있고, Secret 생성 스크립트에는 Redis 대기 한도가 없다. 가이드도 그 보완을 지시하므로 매뉴얼과 코드의 충돌로 세지 않았다. 본문은 이 사전 조건을 포함하며 제품·스크립트는 고치지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 제외 이유 | 담당 역할 |
|---|---|---|
| 현장의 현재 이미지 태그·서비스 판본·replica·실행 상태 | 현장 접속 없이 저장소만 읽었다. 운영 값 파일과 현재값을 구분하고 조회 방법만 적었다. | 설치·운영 담당 |
| 설치 대상의 실제 DB·Kafka·Redis·화면 주소 및 자격 | 설치 환경 입력이 제공되지 않았고 민감값을 기록할 범위가 아니다. 주소 변수·설정 키와 리소스 식별만 적었다. | 인프라·보안 담당 |
| 모든 서비스를 내려야 할 때의 현장 업무 drain 및 무손실 정지 보증 | O §7.1과 차트는 replica 정지·Recreate를 제공하나 현장 진행 작업·주문·설비의 전체 정지 대사를 확인하지 않았다. 임의 종료 순서나 무손실 보증을 만들지 않았다. | TC 운영·업무 담당 |
| 인증 Redis에서 coord-epoch.sh를 그대로 실행하는 절차 | 스크립트에 인증 입력이 없다. 무인증 형식의 명령 조건을 한정하고 인증 우회 명령이나 자격 예제를 만들지 않았다. | Redis 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에서 뺀 상세 |
|---|---|
| 00.rag/admin/upgrade-rollback-and-configuration-apply.md | 이미지 버전 변경·되돌림·Secret 키 갱신과 설정 반영 절차 |
| 00.rag/admin/backup-and-recovery.md | 백업 대상·주기·DB 복원·Redis 원장 복구 |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | 다중 서버 소유자·membership·복구 필요 상태의 상세 조회 및 Epoch 전환 |
| 00.rag/admin/kafka-topic-and-message-reference.md | 전체 토픽별 메시지·방향·계약. 여기에는 최초 생성 예와 확장 시 필수 파티션 조건만 남겼다. |
| 00.rag/admin/configuration-reference.md | 설정 키 전체의 의미·환경별 차이·자격 주입 참조 |
| 00.rag/admin/platform-troubleshooting.md | 「새 이미지인데 Kafka 설정 결함이 다시 나타나는 이유」의 증상·원인 진단. 여기에는 신규 Secret 생성 전 조치와 값만 확인하는 방법을 남겼다. |
| 00.rag/admin/security-operations.md | 자격·TLS 인증서 발급·교체·Secret 보안 운영 |
| 00.rag/user/user-group-and-permission-management.md | 설치 뒤 화면에서 사용자 계정·그룹을 관리하는 절차 |
| 00.rag/user/tc-components-and-terms.md | Shard·Lease·Pod·spare의 개념 정의와 화면 표시 |
| 00.rag/user/demo-execution.md | 화면에서 데모 실행 하나 또는 설비 한 대를 중지하는 조작 |
| 00.rag/user/deployment-workflow.md | 모델·설비 변경을 배포 화면에서 실행·조회하는 절차 |

## 메인 세션 지적 1건 보완 기록

- 원래 결함: 최초 자체검사에서 앱 설정 사본과 Secret 생성 입력의 linger 차이를 누락했다. 이전 기계 검사 PASS는 이 운영 사실의 완전성을 보증하지 않았다.
- 원인·수정: 생성 스크립트 실행 안내에 linger 선행 보완이 없었다. 기존 Secret 절의 첫 문장을 보완하고 450자 한도 때문에 입력 보완·생성 결과 확인을 두 절로 나눴다. 원래 나머지 문장·절은 보존했다.
- 보완 전 검색 원문 SHA-256: `ac9bbd4670458731b622333051e70498685178c998eb330f4c6bb20c1eeaa59b`. 보완 후 SHA-256: `bc1e72e4bcb40774328f9d915d3ef1fbdad66657132a5fc2e5b731aeacbac232`.
- 직접 영향: 사실 대조 6행 추가, 변경 첫 문장 갱신, 이후 줄 번호·절 순번 및 최종 수량·해시 동기화. 이전 검증 이력·수정 범위 밖 본문은 보존했다.
- 추가 원천 상태: exec HEAD f1d5e0a8e727. create-k8s-secrets.sh와 apps/tc-gateway-app/config/tc-messaging.properties·apps/tc-business-app/config/tc-messaging.properties·apps/tc-ui-backend-app/config/tc-messaging.properties를 지정한 git status --short는 stdout 빈 값, exit 0이었다.
- 값 대조: 생성부의 delivery.timeout.ms와 request.timeout.ms는 각각 30000이다. script 전체에서 linger 포함 줄 수는 0이다. 앱 사본은 Gateway L58·Business L53·Backend L58 모두 spring.kafka.producer.properties.linger.ms=0이다. 값 5나 라이브러리 버전·실패 증상의 설명은 검색 원문에 추가하지 않았다.
- 이번 검사 2회: 첫 실행 exit 1, 기존 Secret 부모 452자로 450자 초과 1건. 첫 문장만 줄여 수정한 뒤 마지막 실행 exit 0, 위반 0건. CROSS VIOLATION·MASK 출력 없음. 마지막 검사 17.808초.
- 오프라인 필터 검사 3건: target=0 출력·exit 0, target=5 출력 없음·exit 1, 키 누락 출력 없음·exit 1. 비대상 합성 설정은 출력하지 않았다. 실제 Secret·자격은 읽지 않았으며 kubectl·SSH·Docker·네트워크 접속을 실행하지 않았다.
- 소유 경계: 진단은 기존 platform-troubleshooting 문서의 지정 절로 한 문장 안내했다. 제품 스크립트·설정 및 다른 문서는 수정하지 않았다. 별도 Agent 호출 0회, provider 토큰·비용과 전체 소요 시간은 미계측이다.
- 정리: 임시 파일·프로세스·서비스를 남기지 않았다. 합성 입력과 출력은 메모리에서만 처리했고 검사 프로세스 종료를 확인했다.

## 제품 결함 수정 B·D 반영 기록 (2026-11-19)

- 대상 입력: 검색 원문 `bc1e72e4bcb40774328f9d915d3ef1fbdad66657132a5fc2e5b731aeacbac232`, 작성 기록 `d78c546c9f432fb43367b439b3c6e1b6de19fa993cc2ab73a38d8fa94b0f30d0`.
- 직접 변경: Flyway 확인 V1~V8을 V1~V9로 수정했다. 새 V9를 현재 파일 SHA-256으로 인용했다. 생성 스크립트의 linger 누락과 실행 전 추가 편집 안내를 삭제하고 이 수정이 들어간 판본은 세 입력에 0이 포함됨을 썼다. 기존 Secret은 스크립트 수정으로 갱신되지 않으므로 이전 판본으로 만든 Secret의 확인 조건을 명시했다. 확인 명령·설정 값·진단 소유 문서 안내는 보존했다.
- 범위 보존: 문서 29절 전체를 다시 읽었다. A의 Kafka 보안 전달과 C의 설비별 통신 적용 세부를 주장하는 본문은 없다. 일반 설치 권한·보안 구성 대조, 차트 준비 검사, 서비스 기동·정지·확장 설명은 바뀌지 않아 보존했다. V8 권한 시드 재적용 설명은 V9의 칼럼 확장과 별개여서 그대로 뒀다. H1 적용 메타만 기준 HEAD와 수정 파일 SHA-256의 혼합 판본임을 표시했다.
- 보존 검증: 허용한 문장·제목·원천 변경을 메모리에서 역적용한 바이트 SHA-256이 입력 검색 원문과 일치한다. H1과 네 절(스키마·Secret 준비·메시징 생성·기존 Secret 확인) 밖의 문장·표·원천 줄은 보존했다. 사실 대조 표의 이후 줄 번호만 이동분에 맞췄다.
- 이전 이력 구분: 위의 최초 linger 보완 기록에 남은 누락 0개·이전 파일 줄 번호·당시 전후 SHA는 당시 판본의 이력이다. 현재 생성 입력 사실 대조 표는 수정된 파일의 0 포함 상태를 기록한다.
- 다른 문서 발견: `00.rag/admin/platform-troubleshooting.md` L60은 현재 생성 입력 세 곳에 linger가 없다고 쓴다. B 수정 후 스크립트 L73·L231·L328과 달라 이전 판본 조건과 현재 생성 입력을 구분하는 보완이 필요하다. 이 세션은 해당 문서를 수정하지 않았다.
- 제품 스크립트·설정·다른 문서·검사기·작성 규칙은 수정하지 않았다. kubectl·SSH·Docker·네트워크 접속, migration 적용, git add·commit·push는 하지 않았다. 새 Agent 호출은 0회이며 전체 소요 시간·provider 사용량·비용은 미계측이다.
- 검사기 위치: 과거 Temp 경로는 존재하지 않음을 확인했다. 현재 제공된 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`를 읽고 그대로 실행했다. 검사기 변경이나 우회는 하지 않았다.

- 검사 환경 사전 확인: 첫 실행 exit 1, commit:path 조회 오류 105건. Git cat-file -e f1d5e0a8e727:build.gradle.kts는 기본 호출에서 dubious ownership으로 exit 128이고 해당 exec 저장소만 -c safe.directory로 지정하면 exit 0이었다. 전역 설정·검사기는 바꾸지 않고 검사 자식 프로세스에만 네 인용 저장소의 safe.directory 경로를 지정해 동일 검사를 다시 실행한다. 원천 검사나 길이·마스킹 검사는 생략하지 않는다.

## 검사 결과

- 이번 B·D 반영 검사: 총 2회. Git 소유자 환경 오류를 확인·한정한 뒤 마지막 실행 exit 0, 위반 0건, CROSS VIOLATION·MASK 없음. 마지막 검사 실측 52.240초. SUMMARY sections=29 children=85 maxChild=187 maxParent=426
- 이번 사람 검사: 29절 전체에서 A~D의 영향을 대조했다. 변경한 네 절은 질문·조건·독립 답·소유 경계를 다시 확인했고 나머지 25절의 본문·원천은 바이트 보존 근거로 재사용했다. 이전 판본 Secret 확인 명령은 동일하므로 앞서 기록한 오프라인 필터 시험 3건을 재사용했으며 실제 Secret 조회를 하지 않았다.

- 최종 검색 원문 SHA-256: `2c142fd8dec2249c4e535007224e0e917f07a6bb38cff2dc3d8fa854386cd187`
- 줄 수 440, H2 29, 사실 대조 표 자료 행 85.
- 최초 작성 검사기 실행 3회: 최초 exit 1(인용 범위 3건), 두 번째 exit 0, 마지막 편집 후 세 번째 exit 0. 최초의 잘못된 참조 L1~24(실제 21줄) 두 곳과 L154~170(실제 167줄) 한 곳을 실제 파일에 맞게 수정했다.
- 최초 작성 마지막 검사 소요 시간: 12.624초. 전체 조사 wall time·provider 토큰·비용은 별도 계측하지 않아 미확인이다. 위임 호출은 0회다.
- 검사기·작성 규칙·목록·제품 파일은 수정하지 않았다. 이전 작성 때는 다른 문서의 결함을 보고하지 않았으며 이번 발견 사항은 아래 B·D 반영 기록에 구분했다.
- 도구 실측: `java -version`은 OpenJDK Temurin 21.0.9+10-LTS, `bash --version`은 GNU bash 5.2.37(1)-release, `git --version`은 2.52.0.windows.1이다. 검사기는 고정 classpath의 commonmark 0.32.0 및 현재 ContentSanitizer mask-v1을 사용한다.

검사 명령(검사 자식 프로세스의 GIT_CONFIG_COUNT=4, GIT_CONFIG_KEY_0~3=safe.directory, GIT_CONFIG_VALUE_0~3=C:/workspace_nori/nori-tc/{nori-tc-exec,nori-tc-ui-front,nori-tc-gateway,nori-tc-business}; 저장소별 실제 값은 각각 독립 환경변수로 전달):

```text
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/admin/installation-and-service-lifecycle.md
```

마지막 출력 10줄:

```text
    L418-418 문단 121  | Gateway 기본 그룹은 2개이며 예비 Pod를 1개 두는 구성이다. CPU 부하에 따라 늘리는 구성이 아니다. 그룹을 추가할 때 app.replicaCount와 HPA 최소·최대를 그룹 수+예비 1로 함께 맞춘다.
    L420-420 문단 95  | Gateway 차트는 같은 앱 Pod를 서로 다른 노드에 배치하도록 강제한다. 늘린 Pod를 놓을 노드가 부족하면 Pending으로 남으므로 사용 가능한 노드도 확인한다.
§ L422-431 [33] Gateway 그룹과 Kafka 파티션을 확장할 때 맞출 값   parent=337 children=2 sources=3
    L429-429 문단 150  | 그룹의 담당 파티션은 중복 없이 전체를 덮어야 한다. tc.eqp.commands·tc.ui.events.gateway의 파티션 수와 tc.gateway.kafka.commands-partition-count를 같게 맞춘다. 불일치하면 Gateway가 기동을 거부한다.
    L431-431 문단 186  | 그룹 변경은 Gateway·Backend Secret의 tc-shard.properties, 검증에 쓰는 config/tc-shard.properties, Secret 생성 스크립트의 그룹 정의를 함께 맞춘다. 토픽별 메시지 계약은 00.rag/admin/kafka-topic-and-message-reference.md에서 다룬다.
§ L433-439 [28] Simulator는 여러 Pod로 확장할 수 있는가   parent=129 children=1 sources=2
    L439-439 문단 129  | Simulator 차트는 프로세스 내부 상태를 사용하는 구성이므로 app.replicaCount가 1이 아니면 렌더를 실패시킨다. worker JAR가 함께 들어 있어도 Pod replica를 늘리는 확장 방식으로 사용하지 않는다.
SUMMARY sections=29 children=85 maxChild=187 maxParent=426
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

이전 보완의 사람 검사: 최초 작성 때 27절을 읽었으며 이번 보완은 변경한 Secret 안내와 신규 2절을 전수 대조했다. 나머지 26절은 보완을 역적용한 검색 원문의 SHA-256이 직전 판본과 일치함을 확인해 기존 사실 대조를 재사용했다. 각 절의 네 판정은 사실 대조 표 앞에 남겼다. 제목은 서로 다르며 금지 접미사·숫자 구분용 제목·식별자를 바꾼 반복 틀을 사용하지 않았다. replica 범위 표와 확장 설명에서 불필요하게 중복된 범위 숫자를 제거했다. 설정·상태 설명은 사용처와 연결했고, 이름 토막 풀이·빈 틀 문장·오답 인용을 넣지 않았다. 마스킹 결과에 MASK 줄이 없으며 Secret 값·토큰·비밀번호·연락처·현장 접속 주소를 싣지 않았다. admin 소유 운영 조치만 작성했고 기존 user 문서의 화면 조작 설명을 복제하지 않았다. 최종 미해소 작성 결함 0건, 사용자 결정 요청 0건.

임시 자원 정리: 검사용 임시 파일·디렉터리·브라우저·컨테이너·서비스를 만들지 않았다. 검사 stdout은 이 기록에 필요한 결과를 보존했으며 삭제할 소유 임시 경로가 없다. 실행한 bash/Java 검사 프로세스는 subprocess 종료코드로 종료를 확인했다. 보존할 산출물은 지정 두 Markdown뿐이다.

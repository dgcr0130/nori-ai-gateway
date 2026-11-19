# 백업·복구 작성 기록

## 대상

- objectKey: `00.rag/admin/backup-and-recovery.md`
- 구획: admin, 필요 권한 `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 운영
- 검색 원문: `rag-documents/nori-tc/00.rag/admin/backup-and-recovery.md`
- 검색 원문 SHA-256: `d288b2078c77c75b9c41c7e7d57561bffd385c9bbe3f197bf8435c0c4584fb12`
- 검색 원문: 221줄, H1 1개, H2 15개, 자식 41개
- 확인일: 2026-11-19, 한국 시간
- 범위: O §9의 데이터 백업 대상·방법·복원 전 정지·복원 뒤 확인. 업로드나 운영 명령 실행은 하지 않았다.
- 최종 판정: 대상 원문 위반 0건, 전체 corpus 교차 중복 0건, MASK 0건, 종료코드 0. 아래 2차 검사 당시의 다른 문서 사이 교차 중복은 최종 3차 검사에서 해소됐음을 확인했다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 원천 작업 트리 확인 |
|---|---|---|
| nori-tc-exec | f1d5e0a8e727 | 본문이 인용한 20개 파일은 모두 tracked이고 `git status --short -- <경로>` 출력이 비어 있다. |
| nori-tc-ui-backend | 985d17c604dd | 본문이 인용한 4개 파일은 모두 tracked이고 수정 상태가 아니다. |
| nori-tc-common | 6fe71f6b2da6 | 본문이 인용한 3개 파일은 모두 tracked이고 수정 상태가 아니다. |

위 표의 합계 27개는 아래 최종 원천 목록의 실제 집계와 대조했다.

처음 형제 저장소 Git 조회는 `detected dubious ownership`으로 거절됐다. 소유자와 현재 sandbox 실행 사용자가 다르다는 원문을 확인했고, 저장소를 바꾸거나 전역 설정을 고치지 않고 각 읽기 명령에 해당 저장소만 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소>`로 지정했다. 뒤의 HEAD·status·tracked 조회는 모두 exit 0이었다. 공통 ignore 파일 접근 경고는 있었지만 대상 파일의 tracked 여부와 status는 별도로 확인했다.

작성 규칙 전체, 53개 문서 목록 전체, O §9 전체를 읽었다. 겹치는 기존 검색 원문 `installation-and-service-lifecycle.md`와 `upgrade-rollback-and-configuration-apply.md`를 읽고 기동·설정 갱신의 전체 절차는 복제하지 않았다. 나머지 기존 원문에서는 백업·복원·업로드 저장 위치와 관련된 본문을 검색했다.

추가로 MDF 업로드의 `ModelDetailController.uploadMdf` L130~160 → `JpaModelDetailCommandPort.saveMdf` L186~202, `ModelControllerTest.uploadMdfSavesValidatedXml` L258~288의 전달 bytes·응답 기대값을 읽었다. JAR의 JPA entity와 `TcJarGatewayJpaStore.upsert` L50~74, Redis 원장 저장과 `UiCoordinationRuntimeTest` L50~90의 초기 값 불일치 시 준비 거절 기대값도 읽었다. 이 시험을 실행한 것은 아니다.

## 절별 사실 대조

같은 줄에 여러 문장이 있는 행은 그 줄의 모든 문장을 대조했다. 명령 블록은 인자·환경변수·실행 조건을 함께 대조했다. 관련문서 안내는 목록의 소유 범위와 대조했다.

### 어떤 운영 데이터를 먼저 백업하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 18 | 공유 nori_tc를 우선 백업하고 사용자·권한·설비·모델·배포·작업을 포함한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2149~2158; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 L51~68; nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L94~480, L703~1085 | 공유 DB 주석, 모델·설비·작업·배포·사용자·권한 테이블을 확인했다. |
| 20 | Kafka·Redis·설정·Secret은 DB와 구별해 보존한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2153~2192; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 L61~68 | DB 도구의 대상과 별도 보관 자료를 구별했다. DB dump로 외부 자료가 복원된다고 쓰지 않았다. |

### 업로드한 MDF와 플러그인 JAR도 DB 백업에 들어가는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 32 | MDF와 두 JAR는 BYTEA 저장 자료이며 해당 테이블을 제외하지 않는다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L278~288, L461~480; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd L186~202 | MDF의 bytes 저장과 JAR의 DB 칼럼을 대조했다. |
| 36 | 모델 MDF는 tc_model_mdf.mdf_file이다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L278~288 | NOT NULL BYTEA 칼럼이다. |
| 37 | Gateway JAR는 tc_jar_gateway.jar_file이다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L461~470; nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarGatewayEntity.java @ 6fe71f6b2da6 L31~48 | 테이블과 byte[] 매핑이 일치한다. |
| 38 | Business JAR는 tc_jar_business.jar_file이다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L473~480; nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarBusinessEntity.java @ 6fe71f6b2da6 L31~48 | 테이블과 byte[] 매핑이 일치한다. |
| 40 | 파일 자료는 DB 복원 대상이며 앱 이미지 JAR와 구별한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L278~288, L461~480; nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 L24~36 | 실행 JAR는 이미지 COPY 대상이고 파일 자료는 DB 칼럼이다. |

### 스키마를 바꾸기 전에 DB 백업 파일 만들기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 48 | 스키마 변경 배포 전에 환경변수 자격으로 백업한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2194~2213 | 배포 직전 백업, 사용자 변수, 비밀번호 환경 주입을 대조했다. 자격값은 수록하지 않았다. |
| 50~52 | pg_dump의 대상·custom 형식·시각 파일명 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2211~2217 | 명령을 한 줄로 표기하고 호스트 표지만 바꿨다. -p·-U·-d·-Fc·-f를 보존했다. |
| 54 | custom 파일 보관, 실패 시 배포 중단, 배포 검증까지 보존 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2199~2217 | 필수 선행 백업이 실패했으면 배포를 진행하지 않는 조건으로 썼다. |

### DB 복원 전에 어떤 앱을 정지하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 64 | 중단 시간을 확보하고 존재하는 세 HPA 제거 뒤 네 앱 정지 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2219~2235; nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml @ f1d5e0a8e727 L1~13; nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 L1~13; nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml @ f1d5e0a8e727 L1~13 | 세 차트가 조건부 HPA를 만들고 앱 Deployment를 제어한다. 세 HPA가 모두 있는 예시임을 명시했다. |
| 66~68 | 세 HPA 삭제 명령 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2231~2235 | 리소스 이름과 namespace를 대조했다. |
| 70~72 | 네 앱 replica 0 명령 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2231~2235 | 네 Deployment를 빠짐없이 지정했다. |
| 74 | Pod 종료 확인 뒤 복원, 일반 절차는 설치 문서 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2221~2229; nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 L1~13 | 정지 선행 조건과 일반 수명 관리의 소유권을 구별했다. |

### 백업 파일로 기존 DB 내용을 되돌리기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 81 | 앱 정지 상태에서 DB·사본 시점과 자격을 맞춘다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2221~2245 | 복원 전 정지와 9.2의 자격 재사용을 확인했다. 대상·시점 대조는 사용자가 요구한 데이터 보호 확인 방법이다. |
| 83~85 | pg_restore 명령 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2237~2242 | --clean·--if-exists와 대상 DB를 보존했다. 실제 실행하지 않았다. |
| 87 | 기존 객체 제거를 동반하므로 대상 확인, 오류 시 기동 중단 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2224~2242 | 파괴적 복원 옵션과 후속 기동의 순서를 함께 설명했다. 임의 DB 삭제 명령은 추가하지 않았다. |

### 복원한 DB의 스키마 이력을 어떻게 맞추는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 96 | Flyway 입력 세 변수와 PGPASSWORD와의 차이 | nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 L61~68; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2244~2253 | 실제 getenv 입력을 확인했다. |
| 98~100 | flywayInfo 실행 | nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 L51~68; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2252~2259 | 루트 외부 Flyway 작업과 일치한다. |
| 102 | 기동할 판본과 이력 대조, 낮으면 그 판본까지 migration 후 확인 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2255~2263; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 L61~68 | 무조건 최신 패키지까지 올리는 절차로 쓰지 않았다. |
| 102 | 앱은 구조 검증이므로 이력 대조를 대신하지 않는다. | nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 L89~93 | ddl-auto=validate를 확인했다. 정확한 Flyway 버전 동일성 자동 검사를 주장하지 않았다. |

### 복원 뒤 조정 조회 권한도 확인해야 하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 111 | ADMIN·COORDINATION_ADMIN_READ 연결 count=1 확인, V8 이력만으로 불충분 | nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 L32~58; nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 L18~25; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2265~2268 | 확인 질의, 권한·그룹 INSERT, 시드 TRUNCATE를 대조했다. |
| 113 | 연결 누락은 설치 문서의 보완 절차, 초기 시드 재실행 금지 | nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 L18~25; nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql @ f1d5e0a8e727 L41~58 | 기존 설치 문서의 정확한 절 제목을 확인했다. 보완 SQL 전체는 복제하지 않았다. |

### 설치 설정과 인도 자료는 무엇을 보존하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 123 | 차트·설정·인도 문서·migration을 앱 판본과 보존 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2153~2158; nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 L334~422; nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 L61~68 | 실제 디렉터리와 배포·migration 소비 위치를 대조했다. |
| 125 | 차트 값·이미지 태그로 재배포, 앱 JAR와 운영 설정은 별도 | nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 L334~422; nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 L24~36; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L161~168 | 이미지 COPY와 Secret 볼륨을 구별했다. |

### 운영 Secret 네 개를 안전하게 백업하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 136 | 설치 완료·설정 변경 시 보호된 폴더로 네 설정 Secret 보관 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2174~2183; nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 L204~211, L299~305, L409~418; nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 L527~537 | 생성되는 세 Secret과 Simulator의 별도 Secret 이름을 확인했다. |
| 138~140 | 네 리소스를 YAML 파일로 내보낸다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2179~2185 | 원천의 리소스·출력 파일을 보존하고 metadata 제거를 복원 절로 옮겼다. raw YAML을 터미널로 출력하는 예가 아니다. |
| 142 | 자격 수준으로 보호, pull 자격 관리의 소유 문서 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2174~2177 | Secret 보관과 보안 운영의 경계를 확인했다. |

### 보관한 Secret을 언제 복원하고 반영하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 154 | 복원 사본을 고르고 세 metadata 필드를 제거 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2179~2192 | 원천 grep의 세 필드 이름을 보존했다. 이 필드가 있으면 항상 충돌한다는 일반화는 하지 않았다. |
| 156~158 | Secret YAML apply | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2187~2192 | 파일 기반 적용을 확인했다. |
| 160 | 현재 설정과 사본 대조, 재기동으로 반영, 설정 변경 문서로 연결 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L172~179; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 L166~173; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L161~168; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 L106~113; nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 L19~26; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2187~2188 | 네 앱의 필수 볼륨과 기동 import를 확인했다. 운영 설정을 실제로 읽거나 적용하지 않았다. |

### Kafka 메시지를 재처리할 수 있는 범위 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 168 | 보관 설정으로 재처리 범위 판단, 대상 Pod·도구 조건 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2164~2172 | 고정 현장 보존 기간을 쓰지 않고 원천 명령의 실행 조건을 썼다. |
| 170~172 | Kafka 기본 broker retention 조회 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2168~2172 | --entity-type brokers·--entity-default를 확인했다. Pod만 변수로 바꿨다. |
| 174 | 명령은 기본 브로커 설정이며 토픽별 범위 별도 대조 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2164~2172 | 기본 설정 조회를 실제 토픽별 보존 보장으로 확대하지 않았다. 메시지 계약은 목록의 별도 문서가 소유한다. |

### Redis를 잃으면 어떤 자료가 함께 사라지는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 184 | 원장·실패 메시지·비동기 결과가 Redis 자료이며 전체 손실 시 재기동 복원 불가 | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/store/OperationLedgerRedisStore.java @ 6fe71f6b2da6 L59~87; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd L95~128; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/async/AsyncResultStoreService.java @ 985d17c604dd L98~145; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2160~2162 | Redis 쓰기·읽기 경로를 확인했다. DB로 원장 전체가 복원된다고 쓰지 않았다. |
| 186 | 인프라 담당 영속 구성·보존 인계, 진행 중 작업·실패 자료 포함 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2157~2162; nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/store/OperationLedgerRedisStore.java @ 6fe71f6b2da6 L59~87 | 인프라 소유 절차는 맡길 역할과 인계 대상으로 한정했다. RDB/AOF 명령을 만들지 않았다. |

### Redis 손실 뒤 앱 기동 전에 필요한 조정 초기 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 196 | 초기 값이 없으면 조정 앱 기동 거절, 구 Pod 종료 후 재발급 | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/UiCoordinationContext.java @ 985d17c604dd L40~53; nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 L4~16; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2160~2162 | 현재 값 부재 예외와 스크립트의 중단 선행조건을 확인했다. |
| 198 | issue는 없을 때만 발급하며 과거 작업 증적 복원이 아니다. | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 L63~98; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2160~2162 | 기존 값 검사·SET NX·read-back만 하며 작업 원장 재구성은 하지 않는다. 접속값·발급 전체 절차는 별도 문서 소유다. |

### 데이터 복원 뒤 서비스를 어떤 순서로 확인하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 209 | 스키마·Secret·초기 값 준비 뒤 네 앱 순차 기동, 설치값·자동 확장 복원 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1243~1267, L2224~2229; nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 L334~422; nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml @ f1d5e0a8e727 L1~13 | 사전 의존과 앱 순서를 확인했다. 실제 replica 수를 단정하지 않았다. |
| 211 | 준비·기동 실패 시 완료로 처리하지 않고 서버 뒤 화면·기능 확인 | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 L33~57; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1330~1398 | 스크립트 exit 0/1과 설치 검증을 대조했다. 설치 검증의 전체 항목·명령은 복제하지 않았다. |

### 백업 주기와 최근 성공 여부는 어디서 확인하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 219 | 운영 합의의 주기·보존 기간·실행 기록과 대상별 보존 범위를 인계받는다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2194~2199, L2160~2166 | 원천은 운영 합의·브로커와 인프라 담당을 구별한다. 사용자가 요구한 확인 방법·담당 인계로 썼고 합의값을 만들지 않았다. |
| 221 | 최근 성공은 실행 기록·실제 파일로 대조하고 사본 시점·대상 판본 기록 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2213~2217, L2244~2263 | 시각 파일명·복원 판본 대조와 사용자의 현장 상태 확인 방법 요청을 연결했다. 현장 성공 사실을 주장하지 않았다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 확인한 코드·배포 정의 | 본문 반영 |
|---|---|---|---|
| 이력 버전과 앱 기동 검증 | O L2255~2256은 마지막 버전이 다르면 스키마 검증 단계에서 기동이 실패한다고 일반화한다. | `config/tc-db.properties` L89~93은 Hibernate `ddl-auto=validate`이다. `build.gradle.kts` L51~68은 앱 밖의 Flyway 작업을 정의한다. | Flyway 이력 대조와 앱의 테이블 구조 검증을 구별했다. 모든 버전 차이를 앱이 자동 검출한다는 주장은 넣지 않았다. |
| Kafka 재처리 범위 조회 | O L2164~2172는 보관 설정 확인용으로 기본 broker 설정 조회를 준다. | 제시된 명령 자체가 `--entity-default` 대상이다. `deploy/helm/`은 TC 앱 차트이며 브로커의 토픽별 보존 정책을 정의하지 않는다. | 기본 설정 조회라고 명시하고 실제 토픽별 보존 범위는 Kafka 담당 대조 대상으로 남겼다. |

Secret YAML의 세 metadata 필드 제거는 원천 절차대로 안내하되, 세 필드를 그대로 두면 모든 경우 충돌한다는 O L2185의 설명은 복제하지 않았다. 이 저장소의 chart·스크립트만으로 Kubernetes 서버의 모든 적용 충돌 조건을 증명할 수 없기 때문이다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장의 정기 백업 주기·보존 기간·최근 성공 시각·백업 파일 존재·복원 시험 성적 | 저장소에 현장 실행 기록이 없고 서버 접속을 하지 않았다. 확인 방법과 인계만 썼다. | 운영 책임자·DB 담당자 |
| Redis의 현재 RDB/AOF 설정·스냅샷 경로·완전 복원 명령 | TC 앱 차트와 epoch 스크립트는 Redis 서버의 영속 저장소를 배포하거나 복원하는 도구가 아니다. 원천은 영속 구성을 인프라 담당 소유로 둔다. | Redis 인프라 담당자 |
| Kafka의 실제 retention 값·남은 offset 범위·브로커 스냅샷/복제 복원 명령 | 기본 broker 조회 예만 있고 실제 토픽·브로커 운영 정의와 관측 결과가 없다. | Kafka 운영 담당자 |
| 전체 저장소·외부 볼륨에 존재할 수 있는 업로드 파일의 포괄적 백업 경로 | 확인한 것은 MDF와 플러그인 JAR의 DB 저장이다. 이 확인을 모든 파일·외부 볼륨으로 일반화하지 않았다. | 설치 담당자·자료 소유 제품 담당자 |
| 현재 앱 이미지·replica 수·Secret 내용·인증서·현장 접속값 | 읽기·출력·네트워크 접속이 허가되지 않았으며 문서에도 필요하지 않다. | 배포 담당자·보안 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 이 문서에서 복제하지 않은 사실 |
|---|---|
| 00.rag/admin/installation-and-service-lifecycle.md | 전체 Helm 기동 명령, 서비스별 준비 검사, 일반 정지·확장 절차, 시드 뒤 V8 재적용 SQL. 복원에 꼭 필요한 선행 정지·확인 조건만 남겼다. |
| 00.rag/admin/upgrade-rollback-and-configuration-apply.md | release 이력·이미지 되돌림, Secret 키별 patch·재기동 상세. 백업 사본의 복원과 재반영 경계만 썼다. |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | epoch 접속 환경·전체 발급 명령, 소유권·진행 작업 조회. 손실 후 기동 전제와 증적 복원과의 차이만 썼다. |
| 00.rag/admin/security-operations.md | pull 자격 재발급·계정/인증서 교체. 비밀값을 본문·메타·예제에 넣지 않았다. |
| 00.rag/admin/database-table-reference.md | 테이블별 전체 칼럼·관계. 백업 대상 식별에 필요한 MDF/JAR의 세 바이너리 칼럼만 남겼다. |
| 00.rag/admin/kafka-topic-and-message-reference.md | 메시지 스키마·토픽별 처리 계약·재처리 실행. 이 문서는 보존 범위의 확인까지만 다룬다. |
| 00.rag/admin/daily-checks-and-log-inspection.md | 일상 로그 조회·검증 명령 전체. 복원 뒤 실패 판정과 설치 검증 인계만 썼다. |
| 00.rag/user/permission-code-reference.md | COORDINATION_ADMIN_READ의 전체 권한 의미·부여 조건. 복원 뒤 연결 확인에 필요한 코드만 썼다. |

## 검사 결과

- 검사기: 제공된 `step3/tools/check.sh`, 수정하거나 우회하지 않았다.
- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/backup-and-recovery.md`
- 첫 검사: exit 1, 원천 범위 `coord-epoch.sh L63~100`가 실제 98줄을 넘은 1건. 실제 파일 끝으로 범위를 좁혔다. 함께 수행한 사람 검사에서 HPA 예시 조건과 출처 범위를 보완했다.
- 마지막 본문 편집 후 2차 검사: exit 0, 대상 violations=0, sections=15, children=41, maxChild=163, maxParent=415. MASK 출력은 없다.
- 2차 검사 당시 corpus에서 다른 문서끼리의 CROSS VIOLATION 1건이 출력됐다. `daily-checks-and-log-inspection.md L36`과 `upgrade-rollback-and-configuration-apply.md L114`의 `sh ./deploy/scripts/verify-gateway-rollout.sh tc`가 동일했다. 대상 원문에는 이 코드 블록이 없으며 두 문서를 수정하지 않았다. 병행 작업으로 다른 문서가 바뀐 것을 확인해 최종 3차 검사를 수행했고 교차 중복은 0건이었다.
- 최초 검사 실행 준비 중 이미 존재하는 work 디렉터리에 New-Item을 호출해 ResourceExists가 발생했다. 검사기는 시작되지 않았다. 기존 디렉터리를 그대로 사용했다.
- 첫 원천 목록 집계 시 Windows PowerShell stdin의 한국어 패턴 전달이 손실돼 0개로 나왔다. 0개 결과를 유효 증거로 쓰지 않았다. Unicode escape 패턴과 비어 있지 않음 assert로 고쳐 실제 인용 27개 파일을 전수 대조했다.
- 도구: GNU Bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS, Git 2.52.0.windows.1, Python 3.12.10, Windows PowerShell 5.1.26100.9444.

### 사람 검사

15절 전부를 읽었다. 각 절의 제목이 실제 백업 질문을 드러내는지, 원천과 맞는지, 절 단독으로 대상·선행조건·답을 이해할 수 있는지, 다른 절·앞선 문서와 사실을 중복 소유하는지 대조했다.

| 절 | 제목·질문 | 원천 일치 | 독립 답 | 중복 소유 |
|---|---|---|---|---|
| 어떤 운영 데이터를 먼저 백업하는가 | 적합 | 확인 | 대상·우선순위 명시 | 전체 대상 구별만 소유 |
| 업로드한 MDF와 플러그인 JAR도 DB 백업에 들어가는가 | 적합 | 확인 | 세 테이블·칼럼 명시 | 테이블 전체 설명 생략 |
| 스키마를 바꾸기 전에 DB 백업 파일 만들기 | 적합 | 확인 | 권한 역할·자격·명령·실패 조건 명시 | 백업 작업 고유 |
| DB 복원 전에 어떤 앱을 정지하는가 | 적합 | 확인 | 세 HPA 예시 조건·네 앱 정지 명시 | 일반 정지 설명은 연결 |
| 백업 파일로 기존 DB 내용을 되돌리기 | 적합 | 확인 | 정지 전제·복원 대상·명령·오류 조건 명시 | 복원 작업 고유 |
| 복원한 DB의 스키마 이력을 어떻게 맞추는가 | 적합 | 확인 | 대상 판본·외부 도구 입력 명시 | 설치 일반 절차의 최소 전제만 사용 |
| 복원 뒤 조정 조회 권한도 확인해야 하는가 | 적합 | 확인 | 확인 대상·기대 count·인계 명시 | SQL 재적용 전체는 설치 문서 |
| 설치 설정과 인도 자료는 무엇을 보존하는가 | 적합 | 확인 | 보존 자료와 이미지 구별 | 변경분 갱신 방법 생략 |
| 운영 Secret 네 개를 안전하게 백업하기 | 적합 | 확인 | 시점·리소스·보관 조건 명시 | 신규 생성 절차 생략 |
| 보관한 Secret을 언제 복원하고 반영하는가 | 적합 | 확인 | 사본 선정·정리·적용·반영 명시 | patch 상세는 설정 변경 문서 |
| Kafka 메시지를 재처리할 수 있는 범위 찾기 | 적합 | 확인 | 도구·Pod 조건과 기본 설정 한계 명시 | 메시지 계약 생략 |
| Redis를 잃으면 어떤 자료가 함께 사라지는가 | 적합 | 확인 | 손실 자료·영속 담당 구별 | 실패 처리 화면 사용법 생략 |
| Redis 손실 뒤 앱 기동 전에 필요한 조정 초기 값 | 적합 | 확인 | 부재 조건과 재발급 한계 명시 | 발급 전체 명령·소유권은 별도 소유 |
| 데이터 복원 뒤 서비스를 어떤 순서로 확인하는가 | 적합 | 확인 | 재기동 전제와 실패 시 중단 명시 | 전체 설치 검증 생략 |
| 백업 주기와 최근 성공 여부는 어디서 확인하는가 | 적합 | 확인 | 담당·실제 기록 대조 방식 명시 | 현장 성공값 생성 없음 |

제목 정확 중복과 식별자 제거 후 동일 모양 3회 이상은 없다. 정보 없는 틀 문장, 이름 토막 뜻풀이, 오답 인용, 조사 접합 오류, 일반 코드가 마스킹돼 의미가 사라지는 곳, user 구획으로의 관리 조치 유출은 대상 문서에서 발견하지 않았다. 원문 metadata와 본문의 비밀값·개인 연락처도 수록하지 않았다.

### 미실행과 인계

Docker·kubectl·ssh·DB 도구·Gradle 제품 시험·서버 접속·업로드는 실행하지 않았다. 원문에 적은 운영 명령은 저장소 절차·정의와 대조했으며 현장 실행 성공을 인증하지 않는다. 독립 Agent 호출은 0회다. provider 사용량·비용과 정확한 총 wall time은 이 세션 도구에서 회수하지 못해 미확인이다.

2차 검사 당시 대상 원문 자체는 작성·검사됐으나 corpus 교차 중복 1건 때문에 전체 교차 중복 0건 조건이 미충족이었다. 이후 다른 문서의 병행 변경을 확인했고, 같은 명령의 최종 3차 검사에서 조건 충족을 확인했다. 이 작성 세션은 소유 범위 밖 문서를 고치지 않았다. 남은 사용자 결정은 없다.

### 최종 원천 파일 무결성 목록

아래 모든 파일은 기록한 저장소 HEAD에서 tracked이고 status 출력이 비어 있음을 확인했다. SHA-256은 실제 작업 트리 파일 bytes의 값이다.

| 원천 파일 | commit | SHA-256 |
|---|---|---|
| nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/store/OperationLedgerRedisStore.java | 6fe71f6b2da6 | eb95a252bc4573fa2aefa7892ad5f8da60b77f979ecd0fd61738c5abda2b4245 |
| nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarBusinessEntity.java | 6fe71f6b2da6 | 70e9a9e0c40b92d221356c08fbadc3541794d9d3f8183d8979b05d1d9be0679b |
| nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarGatewayEntity.java | 6fe71f6b2da6 | 49afd9980ba3f0584cd678ce9024c920d72c84b1c1855cad96d0136aac2be59a |
| nori-tc-exec/.gitlab-ci.yml | f1d5e0a8e727 | aa649ea13bb1f9cc4e17dec1482b052c188d1863849b1e7d0b4eff3744d33a9d |
| nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml | f1d5e0a8e727 | d008a99cdb5d555e3f8ff2766e059424d6b25e51c621a608047f490b9aa833f8 |
| nori-tc-exec/build.gradle.kts | f1d5e0a8e727 | f0b140a617d57742f7fc258fd40945272cad8268790fe88fa0b5d3484cc242d7 |
| nori-tc-exec/config/tc-db.properties | f1d5e0a8e727 | 5b36a2c25653afc76b72adcc418ca3682192c3acbcaaf725bdde19629f0a4976 |
| nori-tc-exec/coord-epoch.sh | f1d5e0a8e727 | a25e173d9c79a9563ab2354bc1a91cd5beff1f9fb2221d0c2914c406d63f39b4 |
| nori-tc-exec/create-k8s-secrets.sh | f1d5e0a8e727 | 7223aa36403f3c60abeb36213dd7e48b0d8ed82f89c68d3567da1a1213fab621 |
| nori-tc-exec/db/migration/V1__baseline.sql | f1d5e0a8e727 | d93b11e88264f4a9c2c0283ceba093c03c6384cca4847368126bfdb614945d47 |
| nori-tc-exec/db/migration/V8__add_coordination_admin_read_permission.sql | f1d5e0a8e727 | 33ca4e0ff655de7394e967328966d1d989a210be0bf339d17dbdb652a748539c |
| nori-tc-exec/deploy/docker/Dockerfile-ci | f1d5e0a8e727 | 07e373d7d483d3977c9cc05e5968e5776bf92443f8cf2fa830888d3b387f981a |
| nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml | f1d5e0a8e727 | 7852546b573846bea009e9e821fae04867623c5ae0aeb94209be6045d4781ad3 |
| nori-tc-exec/deploy/helm/tc-business/templates/hpa.yaml | f1d5e0a8e727 | 319c361eeda1fd4c2173ceff9bede5bfff9e49faa3fd30cd10512a640977f08d |
| nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml | f1d5e0a8e727 | f3de15632e2ca3cccabd693ef714053a82eaca5e26a6963ea6c2e5fa52678fa0 |
| nori-tc-exec/deploy/helm/tc-gateway/templates/hpa.yaml | f1d5e0a8e727 | 319c361eeda1fd4c2173ceff9bede5bfff9e49faa3fd30cd10512a640977f08d |
| nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml | f1d5e0a8e727 | 8b49bdfccaf17638c7f9ca6d780b65c68e2106f24028e850b26ccd35027c7fea |
| nori-tc-exec/deploy/helm/tc-simulator/values.yaml | f1d5e0a8e727 | d8acfa2ed86eaef67522017d96546c6c28ded9a6fe9b80c3268d3f47f09e4e81 |
| nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml | f1d5e0a8e727 | a1d44e4ce812420b639b4485c1bb652129d8b127b68dcaceec6813cd2ec02e80 |
| nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml | f1d5e0a8e727 | 319c361eeda1fd4c2173ceff9bede5bfff9e49faa3fd30cd10512a640977f08d |
| nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh | f1d5e0a8e727 | 5b1659b2e6b771c0edd8d4155aa54f3f61de2b4a3e877fba2ae07e95288f2c15 |
| nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md | f1d5e0a8e727 | a74b1c4c649ca8cdf1d48994e3793a36bdda3f789b5c488088721eeb876d9755 |
| nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql | f1d5e0a8e727 | 0e29d5822bab56c60a4581acbef37014c815e5f83ccab03992ee4953aefeab2e |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java | 985d17c604dd | dcbfbc4879ca700d14ec0b4d30d59cf57d201654155b56a9d74ca596413fc3ad |
| nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/async/AsyncResultStoreService.java | 985d17c604dd | 973f801d2900da52d0bb901c8fa664cedab62402143da6bbd0cfb21e3dd864f8 |
| nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/UiCoordinationContext.java | 985d17c604dd | f7ebefa88bef34f1b2223575629c076f34435ac19c90c7ad94e32184015580a3 |
| nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java | 985d17c604dd | 7014731545b92b4501d701e302a25dcadf15ebeb0550a635a5c7a4f7433b8665 |

### 2차 검사 출력 10줄 보존

```text
§ L200-211 [26] 데이터 복원 뒤 서비스를 어떤 순서로 확인하는가   parent=282 children=2 sources=5
    L209-209 문단 126  | 배포 담당자는 DB 스키마·설정 Secret·조정 초기 값을 준비한 뒤 Backend·Business·Gateway·Simulator 순으로 다시 기동한다. 보존한 앱 판본과 설치 값을 사용하고 제거했던 자동 확장도 복원한다.
    L211-211 문단 155  | 기동 명령과 서비스별 준비 판정은 00.rag/admin/installation-and-service-lifecycle.md를 따른다. Gateway 검증이 실패하거나 앱 기동 오류가 있으면 복원 완료로 처리하지 않는다. 서버 준비 뒤 화면 로그인과 복원 대상 기능을 확인한다.
§ L213-221 [26] 백업 주기와 최근 성공 여부는 어디서 확인하는가   parent=220 children=2 sources=2
    L219-219 문단 105  | 정기 백업 주기와 보존 기간은 운영 합의로 정한다. 운영 담당자에게 합의된 주기·보존 기간과 백업 실행 기록을 인계받고, DB·Kafka·Redis 담당자에게 대상별 보존 범위를 확인한다.
    L221-221 문단 114  | 최근 성공 여부는 실행 기록과 해당 시점의 보관 파일을 대조해 확인한다. 저장소에 백업 절차가 있다는 사실과 현장의 백업 성공을 구별한다. 복원에 사용할 사본의 시점과 대상 DB·앱 판본도 함께 기록한다.
SUMMARY sections=15 children=41 maxChild=163 maxParent=415
CROSS VIOLATION 같은 자식 본문 [00.rag/admin/daily-checks-and-log-inspection.md L36, 00.rag/admin/upgrade-rollback-and-configuration-apply.md L114]: sh ./deploy/scripts/verify-gateway-rollout.sh tc
CORPUS docs=21/53 sections=362 part={admin=4, user=17} type={개념=4, 사용 절차=13, 운영=4}
TOTAL files=21 targets=1 violations=0
```

2차 종료코드: 0. 당시 대상 violations=0과 별개로 CROSS VIOLATION 1건을 관찰했다. 이 결과를 교차 중복 0건으로 기록하지 않았으며 최종 3차 결과와 구별해 보존한다.

### 최초 검사 원문 오류

```text
SUMMARY sections=15 children=41 maxChild=163 maxParent=408
VIOLATION L186 원천 줄 범위 L63~100 (파일 98줄) 'nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 조회·미발급 시 발급 L63~100'
TOTAL files=20 targets=1 violations=1
```

## 소유 임시 자원 정리

일회성 검사 로그와 원천 집계만 만들었다. 재사용 근거는 위 원천 SHA 목록·최초 오류·마지막 출력·결과 표로 먼저 보존했다. 다음 세 파일만 이 세션 소유 삭제 대상이다. 기존 작업 디렉터리는 삭제 대상이 아니다.

| 정확한 절대경로 | 용도 | 삭제 전 SHA-256 |
|---|---|---|
| C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-21-backup-and-recovery/work/check-first.log | 최초 검사 원문 | ed2d526e6e6da91ffcbd63f4c95affbfcb82ce0c3fec7505fa6e90e02569946a |
| C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-21-backup-and-recovery/work/check-final.log | 최종 검사 원문 | b00297bbcc22693e557a17dec078af064e38df9f260fa46f088fde5981c364b9 |
| C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-21-backup-and-recovery/work/source-audit.json | 인용 파일 판본·status·SHA 집계 | 017c0f85cc676df2b7f67b713af7b10dd2d3b958f540d4dbb52f8d3d20726d0e |

검사기 두 실행과 원천 집계 프로세스의 종료코드를 회수했다. 정확한 대상과 부모가 지정 작업 경로 안에 있음을 확인했고, 파일 세 개는 Archive만, 부모 두 디렉터리는 Directory만 있어 ReadOnly·Hidden·System·reparse 속성이 없다.

`Get-CimInstance Win32_Process -ErrorAction Stop`으로 프로세스 참조를 보려 했으나 Windows PowerShell 5.1.26100.9444에서 `액세스가 거부되었습니다`, `HRESULT 0x80041003`으로 실패했다. 프로세스 시작 전 정책 거절이 아니라 실행된 CIM 조회의 원문 오류다. 상세 권한 설정과 전체 프로세스 참조 목록은 미확인이다. 권한·정책은 바꾸지 않았다. 소유 프로세스 종료 확인에 더해 각 파일을 `FileMode.Open`·`FileAccess.Read`·`FileShare.None`으로 열고 즉시 닫는 비파괴 확인은 모두 성공했다. 파일 사용 잠금은 관찰되지 않았다. 정상 native PowerShell `Remove-Item -LiteralPath`를 강제·재귀 옵션 없이 사용할 대상으로 한정했다.

정리 결과: native 비강제 삭제가 exit 0으로 끝났고 세 정확한 경로의 `Test-Path -LiteralPath`가 모두 False였다. 기존 `work/`의 잔여 파일 수는 0이다. 기존 작업 디렉터리는 보존했다.

## 최종 3차 검사와 완료 상태

2차 검사 뒤 다른 문서의 병행 변경을 확인했다. 현재 corpus의 교차 중복 해소 여부를 확인하기 위해 같은 검사기 명령을 한 번 더 실행했다. 이 작성 세션의 검색 원문 SHA-256은 바뀌지 않았다. 검사 결과는 메모리에서 회수했으며 새 임시 파일을 만들지 않았다.

명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/backup-and-recovery.md`

마지막 출력 10줄:

```text
    L198-198 문단 163  | coord-epoch.sh의 issue는 값이 없을 때만 발급하며 기존 값을 덮어쓰지 않는다. 초기 값 재발급은 과거 작업 증적의 복원이 아니다. 접속 설정·발급·소유권 점검은 00.rag/admin/coordination-ownership-and-recovery-check.md가 소유한다.
§ L200-211 [26] 데이터 복원 뒤 서비스를 어떤 순서로 확인하는가   parent=282 children=2 sources=5
    L209-209 문단 126  | 배포 담당자는 DB 스키마·설정 Secret·조정 초기 값을 준비한 뒤 Backend·Business·Gateway·Simulator 순으로 다시 기동한다. 보존한 앱 판본과 설치 값을 사용하고 제거했던 자동 확장도 복원한다.
    L211-211 문단 155  | 기동 명령과 서비스별 준비 판정은 00.rag/admin/installation-and-service-lifecycle.md를 따른다. Gateway 검증이 실패하거나 앱 기동 오류가 있으면 복원 완료로 처리하지 않는다. 서버 준비 뒤 화면 로그인과 복원 대상 기능을 확인한다.
§ L213-221 [26] 백업 주기와 최근 성공 여부는 어디서 확인하는가   parent=220 children=2 sources=2
    L219-219 문단 105  | 정기 백업 주기와 보존 기간은 운영 합의로 정한다. 운영 담당자에게 합의된 주기·보존 기간과 백업 실행 기록을 인계받고, DB·Kafka·Redis 담당자에게 대상별 보존 범위를 확인한다.
    L221-221 문단 114  | 최근 성공 여부는 실행 기록과 해당 시점의 보관 파일을 대조해 확인한다. 저장소에 백업 절차가 있다는 사실과 현장의 백업 성공을 구별한다. 복원에 사용할 사본의 시점과 대상 DB·앱 판본도 함께 기록한다.
SUMMARY sections=15 children=41 maxChild=163 maxParent=415
CORPUS docs=21/53 sections=362 part={admin=4, user=17} type={개념=4, 사용 절차=13, 운영=4}
TOTAL files=21 targets=1 violations=0
```

종료코드 0, MASK_LINES=0, CROSS_LINES=0을 확인했다. 실제 검사기 실행은 총 3회이고 준비 실패는 실행 횟수에서 제외했다. 대상 문서 15절 전체 사람 검사와 모든 본문 사실 대조를 마쳤다. 작성으로 생긴 미해결 지적 0건, 사용자 결정 0건이다. 독립 공식 검증이나 현장 복원 시험을 통과했다고 기록하지 않는다.


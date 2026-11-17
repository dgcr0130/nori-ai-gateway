# nori-ai-gateway 개발 규칙

NORI 공용 AI Gateway(Java 21, Spring Boot 4, Gradle 멀티 모듈, 헥사고날)의 저장소 규칙이다. 전역 지침과 충돌하면 전역 지침을 우선하며, 이 문서는 저장소 구조와 검증 방법을 구체화한다.

## 1. SDD 정본

이 시스템의 SDD 문서는 워크스페이스 규칙대로 중앙 저장소 `../nori-sdd-specs/work-items/005-ai-gateway/` 에 둔다. 제품 시험과 증거 검증은 SPEC 파일·기준선 입력 없이 실행한다.

| 문서 | 경로 | 쓰기 |
|---|---|---|
| 승인 SPEC | `../nori-sdd-specs/work-items/005-ai-gateway/spec.md` | SPEC 소유자 개정 |
| Plan | `../nori-sdd-specs/work-items/005-ai-gateway/spec-plan.md` | root 가 허용한 Plan 개정 세션 |
| 구현 기록 | `../nori-sdd-specs/work-items/005-ai-gateway/implementation-log.md` | 구현 세션(절 추가) |

- Plan·구현 기록 본문의 2026-11-16 이전 경로(`docs/gates/…`, `evidence/…`, `gateway-eval` 등)는 Plan 첫 절의 경로 대응표로 읽는다.

- SPEC 은 이력을 지우지 않는다. 결정이 바뀌면 원문 옆에 날짜가 붙은 대체 문구(예: `2026-11-10 대체(…)`)를 넣고 §27.5 변경 목록에 행을 추가한다.
- 실행 단위는 Plan 의 phase(P01~P30)와 구조 정렬 단위(U1~U8)다. §6 의 순서를 따르고, 사용자가 지정한 단위에서 멈춘다.

## 2. 작업 시작 전 확인

1. `docs/README.md`, SPEC, Plan(첫 절 경로 대응표·§0 Plan 상태·§6·대상 단위), 구현 기록의 최근 절을 읽는다.
2. `git status --short --branch` 와 `git worktree list` 로 기존 변경·다른 worktree 를 확인한다. 기존 dirty 파일은 사용자 작업으로 취급하고 덮어쓰지 않는다.
3. 제품 시험은 SPEC 파일·기준선·SPEC 전용 환경변수 없이 실행한다(§5).
4. 대상 단위의 allowed_writes 밖 파일은 바꾸지 않는다.

## 3. 수용 결과 불변 (Plan §0 R1)

- 이미 만든 수용 결과(`acceptance/results/**`)의 레코드·`specSha`·`contentSha256`·원시 결과는 다시 쓰지 않는다. 새 레코드만 추가한다.
- 경로·catalog·classpath 등 제품 입력이 바뀌어 시험 fingerprint 가 달라지면 재판정이 아니라 재측정이다(Plan §0 R3).
- git 에서 제외한 로컬 진단 결과(`acceptance/results/inference-diagnosis/`)는 건드리지 않는다.
- 중앙 `spec-approvals.md`의 기존 승인 이력은 보존한다.

## 4. 모듈 책임

물리 위치는 `modules/` 아래, 논리 경로는 `settings.gradle.kts` 의 `projectDir` 매핑이다(Plan §3.2 2026-11-10 대응표, 정렬 단위 U3). Java 패키지는 모두 `com.nori.ai.gateway.*` 로 유지한다.

| task 경로 | 디렉터리 | 책임 |
|---|---|---|
| `:ai-gateway-core` | `modules/ai-gateway-core` | 순수 도메인·use case·port. Spring·JDBC·HTTP 무의존(ArchUnit) |
| `:adapter:ai-gateway-db-adapter` | `modules/adapter/ai-gateway-db-adapter` | JdbcClient 저장소·큐·Knowledge 저장소. 모든 일반 저장소 public 메서드 첫 인자 `VerifiedSystemScope` |
| `:adapter:ai-gateway-inference-adapter`(추론+tokenizer), `:adapter:ai-gateway-loki-adapter`, `:adapter:ai-gateway-tempo-adapter`, `:adapter:ai-gateway-gitlab-adapter`, P24 의 `:adapter:ai-gateway-kafka-adapter`·`:adapter:ai-gateway-object-storage-adapter` | `modules/adapter/<이름>` | 외부 시스템별 읽기 전용 adapter. adapter 사이 의존 0 |
| `:adapter:ai-gateway-source-support`(패키지 `integration.source`) | `modules/adapter/ai-gateway-source-support` | loki·tempo·gitlab adapter 가 함께 쓰는 Source HTTP·관측·접근 검사·TC 앱 목록. core 에만 의존하고, Source adapter 는 이 모듈과 core 에만 의존한다 |
| `:adapter:ai-gateway-tc-mock-adapter` | `modules/adapter/ai-gateway-tc-mock-adapter` | F1~F4 의 TC 업무 API·인가 API Mock(core port 구현) |
| `:apps:ai-gateway-app` | `modules/apps/ai-gateway-app` | 단일 조립 루트(Boot 앱·web·보안·Job 워커·시험 전용 hook). web 은 분리하지 않는다 |
| `:apps:ai-gateway-acceptance-app` | `modules/apps/ai-gateway-acceptance-app` | 배포 환경 대상 수용 하네스(application plugin·installDist, k8s Job 이미지) |
| `:apps:ai-gateway-acceptance-cli` | `modules/apps/ai-gateway-acceptance-cli` | 수용 기준 점검 CLI(`test-evidence`·`ingest`·`gate`·`record`·`perf`·`ci-evidence`). 패키지 `com.nori.ai.gateway.eval` |

- 금지 의존: core → 다른 모듈/Spring/JDBC, adapter ↔ adapter, tc-mock → db/integration/app, 수용 기준 점검 CLI(`eval` 패키지) → gateway 모듈. ArchUnit 시험이 강제한다.
- 빈 등록은 `@ComponentScan` 없이 각 모듈 `*ModuleConfiguration` 의 `@Import` 로 명시한다. 새 `@Component`·`@Service`·`@RestController` 를 추가하면 같은 모듈 `*ModuleConfiguration` 의 import 와 `@Import` 를 함께 바꾼다. core 타입 빈은 app 의 `CoreBeansConfiguration` `@Bean` 으로만 등록한다. `ModuleConfigurationCompletenessTest` 가 누락을 FAIL 로 잡는다.
- 운영 도구는 `operations/`, Dockerfile 은 `operations/deploy/docker/`, helm 차트는 `operations/deploy/helm/ai-gateway/`(U6 에서 `nori-tc-exec` 에서 이관), 배포 스크립트·runconfig 는 `operations/deploy/scripts/`, 배포 점검 스크립트는 `operations/deploy-checks/`, 배포 설정은 루트 `config/ai-gateway-*.properties`, runbook 은 `docs/runbooks/` 다.
- 수용 기준과 결과는 `acceptance/` 다: 필수 통합 시험 목록 `required-integration-tests.txt`, 질문 세트 `question-sets/`, 배포 입력·성능 기준 승인 기록 `approvals/`, 단계별 결과 `results/<단계 폴더>/`(F1 `gateway-foundation`, F2 `llm-integration`, F3 `rag-integration`, F4 `tc-integration`), 종료된 추론 진단 기록 `history/inference-diagnosis/`.

## 5. 검증 명령

Windows PowerShell, cwd `C:/workspace_nori/nori-tc/nori-ai-gateway`, 각 명령은 별도로 실행하고 exit code 를 각각 확인한다. PostgreSQL 을 쓰는 통합 시험은 `$env:AIGW_PG_TEST_IMAGE` 에 Plan 이 정한 고정 이미지(`이름:tag@sha256:<digest>`)를 설정한다.

- 모듈 단위 시험: `.\gradlew.bat :<module>:test --no-daemon`
- 통합 시험: `.\gradlew.bat :<module>:integrationTest -Paigw.requireDocker=true --no-daemon`(Docker 가 없으면 실패해야 하며 skip 을 PASS 로 보지 않는다)
- 시험 실행 증거: 각 Test task 명령 직후 별도로 `.\gradlew.bat :apps:ai-gateway-acceptance-cli:run --args="test-evidence verify --tasks <task 경로>"`(integrationTest 는 `-Paigw.requireDocker=true` 동반). 새 IT 를 만드는 모듈은 `acceptance/required-integration-tests.txt` 에 한 줄을 추가한다(Plan §4.1 N17).
- 전체: `.\gradlew.bat build --no-daemon`, 비한정 `.\gradlew.bat integrationTest -Paigw.requireDocker=true --no-daemon`
- 빌드 기반(U2 이후): `.\gradlew.bat check --no-daemon`(`verifyDependencyMatrix`), `.\gradlew.bat verifyBuildLogicResolutionMode --no-daemon`, lockfile 갱신 `.\gradlew.bat resolveAndLockAll --write-locks --no-daemon`
- 문서만 바꾼 작업에는 전체 build 를 관성적으로 돌리지 않는다. 실행하지 않은 검증을 통과했다고 보고하지 않는다.

## 6. DB 와 migration

- 운영 DB 는 공유 서버 192.168.0.150(k8s 밖, PostgreSQL 18.6)의 `nori_ai_gateway`, 포트 15432 직접 연결이다. 빌려 쓰는 서버이므로 서버 설정을 바꾸지 않고 서버 변경을 게이트로 두지 않는다. 서버 쪽 선행조건은 `docs/runbooks/db-server-prerequisites.md`(U5) 에 기록한다.
- DB 계정은 하나다. 계정 이름과 비밀번호는 코드·문서·차트·로그에 쓰지 않고 환경변수 `AI_GATEWAY_DB_USER`·`AI_GATEWAY_DB_PASSWORD`(k8s 는 Secret `ai-gateway-config`, CI 는 CI 변수)로만 주입한다. 명령행 인자로 비밀번호를 넘기지 않는다.
- 스키마 정본은 루트 `flyway/sql/` 의 순서대로 적용된 최종 상태다(U5 이전에는 `modules/adapter/ai-gateway-db-adapter/src/main/resources/db/migration/gateway/`). 이력 schema 는 `gw_flyway` 다. 적용은 루트 Gradle Flyway 플러그인과 CI `migrate` stage 가 하며, 앱은 `spring.flyway.enabled=false` + `SchemaVersionGuard`(빌드 때 앱 리소스로 복사한 `flyway/sql` 사본과 실제 이력 비교)다.
- migration 은 forward-only 다. 적용된 V 파일을 고치거나 지우거나 번호를 바꾸지 않는다. 새 V 번호는 Plan 의 phase 별 예약(§7 L-1·§8)을 따른다.
- 공유 서버에 대한 `flywayMigrate`, 수동 DDL/DML 은 CI `migrate` stage 또는 사용자의 명시 지시로만 한다. 로컬 확인은 격리된 시험 컨테이너에 한다.
- 자동 백업·복원은 없다. 백업·복원은 사용자가 요청할 때만 별도 작업으로 한다(SPEC §12.4).

## 7. nori-wm 과 의도적으로 다른 점

형제 시스템 `nori-wm-ui-backend` 의 구조(build-logic composite·`modules/` + `projectDir`·`config/`·`flyway/sql`·`operations/`·CI `migrate` stage)를 따른다. 다음 차이는 사용자 결정이므로 결함으로 보고하지 않는다.

- DB 계정이 하나다. migration 계정과 앱 계정을 나누지 않으며(nori-wm 의 `WM_DB_MIGRATION_USER ?: WM_DB_USER` 대체 경로와 같은 형태), 계정 이름도 CI 파일에 적지 않고 CI 변수로 받는다(nori-wm 은 yaml 에 적는다).
- 별도 domain 모듈과 web adapter 모듈을 두지 않는다(core Spring 무의존 유지, app 내부 의존 방향 — Plan §3.2).
- Flyway 이력 schema 는 `public` 이 아니라 `gw_flyway` 이고 `baselineOnMigrate` 를 쓰지 않는다(빈 DB 에서 시작).
- 수용 기준 체계(`acceptance/` 의 필수 통합 시험 목록·단계별 결과, `test-evidence verify`)는 이 저장소 고유다.

## 8. 완료 보고

변경한 동작과 이유, 변경 파일, 대상 phase·단위와 AC/leaf 상태, 실행한 명령과 결과(마지막 출력 인용, 시험 수·실패·skip), 실행하지 못한 검증과 이유, 남은 위험·사용자 결정 항목을 구분해 적는다. 체크박스는 실제 검증 근거가 있을 때만 채운다.

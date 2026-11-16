# NORI AI Gateway

NORI 공용 AI Gateway입니다. Java 21, Spring Boot 4, Gradle 9.2.1 멀티 모듈, 헥사고날 구조입니다.

## 디렉터리

| 위치 | 내용 |
|---|---|
| `modules/ai-gateway-core/` | 도메인·use case·port |
| `modules/adapter/` | 외부 시스템 adapter: DB, 추론·tokenizer, Loki, Tempo, GitLab, Source 공용, TC Mock |
| `modules/apps/ai-gateway-app/` | Gateway 앱(단일 조립 루트) |
| `modules/apps/ai-gateway-acceptance-app/` | 배포 환경 수용 시험 Job |
| `modules/apps/ai-gateway-acceptance-cli/` | 수용 기준 점검 CLI: SPEC 기준선 확인(`spec-guard`), 시험 증거 검증(`test-evidence verify`), 결과 수집·단계 판정(`ingest`·`gate`) |
| `config/` | 배포 설정 properties |
| `flyway/sql/` | DB migration |
| `acceptance/` | 수용 기준과 결과: SPEC 기준선, 필수 통합 시험 목록, 질문 세트, 승인 기록, 단계별 결과, 종료된 추론 진단 기록 |
| `operations/` | 배포(`deploy/{docker,helm,scripts}`), 배포 점검(`deploy-checks/`), 추론 서버·PostgreSQL·SeaweedFS·monitoring 도구 |
| `docs/` | API 계약·runbook·배포 선행조건([문서 색인](docs/README.md)) |

SDD 정본(SPEC·PLAN)은 중앙 저장소 `../nori-sdd-specs/work-items/005-ai-gateway/`에 있습니다.

## 로컬 명령

`com.nori:*` 의존은 GitLab Package Registry(`gitlabBaseUrl`·`GITLAB_TOKEN`) 또는 `useLocalMaven` Gradle property가 있을 때 로컬 Maven에서 받습니다. 시험과 점검 명령은 승인 SPEC 경로를 `AIGW_SPEC_FILE`로 받습니다.

```powershell
$env:AIGW_SPEC_FILE = 'C:/workspace_nori/nori-tc/nori-sdd-specs/work-items/005-ai-gateway/spec.md'
.\gradlew.bat :apps:ai-gateway-acceptance-cli:run --args="spec-guard"
.\gradlew.bat :<module>:test
.\gradlew.bat :apps:ai-gateway-acceptance-cli:run --args="test-evidence verify --tasks :<module>:test"
```

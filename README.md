# NORI AI Gateway

NORI 공용 AI Gateway입니다. Java 21, Spring Boot 4, Gradle 9.2.1 멀티 모듈, 헥사고날 구조입니다.

## 디렉터리

| 위치 | 내용 |
|---|---|
| `modules/ai-gateway-core/` | 도메인·use case·port |
| `modules/adapter/` | 외부 시스템 adapter: DB, 추론·tokenizer, Loki, Tempo, GitLab, Source 공용, TC Mock |
| `modules/apps/ai-gateway-app/` | Gateway 앱(단일 조립 루트) |
| `modules/apps/ai-gateway-acceptance-cli/` | 수용 기준 점검 CLI: 시험 증거 검증(`test-evidence verify`), 결과 수집·단계 판정(`ingest`·`gate`) |
| `config/` | 비밀 본문 없는 앱 YAML·DB placeholder 설정 |
| `flyway/sql/` | DB migration |
| `acceptance/` | 수용 기준과 결과: 필수 통합 시험 목록, 질문 세트, 승인 기록, 단계별 결과, 종료된 추론 진단 기록 |
| `operations/` | 배포(`deploy/{docker,helm}`), 추론 서버·PostgreSQL·SeaweedFS·monitoring 도구 |
| `docs/` | API 계약·runbook·배포 선행조건([문서 색인](docs/README.md)) |

SDD 정본(SPEC·PLAN)은 중앙 저장소 `../nori-sdd-specs/work-items/005-ai-gateway/`에 있습니다.

## 로컬 명령

`com.nori:*` 의존은 GitLab Package Registry(`gitlabBaseUrl`·`GITLAB_TOKEN`) 또는 `useLocalMaven` Gradle property가 있을 때 로컬 Maven에서 받습니다. 시험과 점검 명령은 SPEC 파일·기준선·SPEC 전용 환경변수 없이 실행합니다.

```powershell
.\gradlew.bat :<module>:test
.\gradlew.bat :apps:ai-gateway-acceptance-cli:run --args="test-evidence verify --tasks :<module>:test"
```

## 운영 배포

CI/CD는 형제 WM과 같은 `test`·`helm-validate`·`package`·`build`·`scan`·`migrate`·`deploy` 구성입니다. 통합 시험은 push 전에 로컬 Docker로 실행합니다. `migrate`는 main·tag에서 자동 실행되고 `deploy`는 수동입니다. Gateway는 `prod` 프로필 하나로 배포하며 앱 설정은 `config/ai-gateway.yaml`에서 ConfigMap으로 전달합니다. 운영 자격은 [runtime-credentials.md](docs/runbooks/runtime-credentials.md)를 따릅니다. 시험 hook은 로컬 Gradle 시험 전용이며 배포 차트에는 활성화 입력이 없습니다. 차트 렌더는 push 뒤 CI에서 확인합니다.

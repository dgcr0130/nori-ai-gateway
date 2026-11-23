# 배포 선행조건

배포 전에 확인해야 하는 외부 조건의 상태 기록입니다. 판정 원본은 각 절의 기록이며 운영 게이트 PASS를 뜻하지 않습니다.

## 배포 입력 승인 기록

운영 앱 설정 정본은 `config/ai-gateway.yaml`이며 CI가 ConfigMap으로 전달합니다. 운영자는 namespace `tc`의 Secret `ai-gateway-config`와 protected CI 변수 `AI_GATEWAY_DB_USER`·`AI_GATEWAY_DB_PASSWORD`를 준비합니다([운영 자격](runbooks/runtime-credentials.md)). main·tag push의 migrate는 공유 DB에 자동 적용되며 deploy는 수동입니다. 과거 [acceptance/approvals/](../acceptance/approvals/) 기록은 이력으로 보존하며 현재 배포 실행기의 입력으로 사용하지 않습니다.

앱 bootJar는 2026-11-23 실측 196,796,788바이트로 GitLab job artifact 기본 상한 100MB를 넘습니다. 그래서 `package`는 jar를 이 프로젝트 Generic 패키지 저장소의 `ai-gateway-app/ci/app.jar`로 올리고 SHA-256을 dotenv로 넘깁니다. `build`는 같은 파일을 내려받아 그 SHA-256과 대조한 뒤에만 이미지를 만듭니다. 거의 동시에 도는 파이프라인이 더 늦게 올렸으면 해시가 달라 `build`가 실패하며, 그때는 `package`부터 다시 실행합니다. 같은 이름 파일은 프로젝트 패키지 정리 정책(중복 1개 유지, 2026-11-23 설정, 약 12시간 주기)이 지웁니다.

## 관측 수신·경보 전달 (G-ALERT · G-OBS-READBACK, P14)

### 현재 판정

| 게이트 | 상태 | 근거 |
|---|---|---|
| G-ALERT | NOT_RUN (미확인) | 운영 monitoring의 alert rule 형식·수신처 읽기 전용 확인 기록이 없다. exec `docs/observability/03-alert-policy.md`는 Prometheus rule·Alertmanager 1차안이며 실제 배포는 후속이라고 적는다. |
| G-OBS-READBACK | NOT_RUN (미확인) | Tempo·Loki·metric 읽기 경로와 Alloy `alloy-gateway`의 OTLP logs 수신 파이프라인 확인 기록이 없다. |

따라서 `ai.gateway.otlp.logs.enabled=false`(stdout JSON만)를 유지하고, AC-24-AUDIT-ALLOW 배포 판정과 경보 leaf는 NOT_RUN이다. 로컬 인프로세스 수신기 시험은 export 경로·허용 속성의 로컬 검증이며 운영 수신·경보 전달 PASS가 아니다.

### 수집 경로 (Plan §10.1)

- metrics: `micrometer-registry-otlp` 명시 registry → `http://alloy-gateway.monitoring.svc:4318/v1/metrics` (`ai.gateway.otlp.metrics.enabled=true`). Boot 4.0.10 OTLP metrics 자동구성은 `spring-boot-opentelemetry`의 `OpenTelemetryProperties` 클래스를 조건으로 요구하며 그 모듈이 runtime에 없어 활성화되지 않는다.
- traces: 명시 `SdkTracerProvider` + `OtlpHttpSpanExporter` → `/v1/traces`(`ai.gateway.otlp.traces.enabled=true`일 때만 exporter 연결, 꺼져도 SDK trace는 생성). 만드는 span은 두 가지다: 정상 인증 `/v1/*` 요청마다 `ServiceTokenAuthFilter`가 원격 부모 없이 만드는 `aigw.request`(SERVER, 속성 0)와 직접 Source 요청의 `aigw.source.request`(CLIENT, `source_id`·`method`·`status_class`). javaagent·tracing bridge·표준 HTTP handler 없음.
- logs: stdout `AllowlistJsonEncoder`. OTLP `/v1/logs`는 G-OBS-READBACK 확인 후 `ai.gateway.otlp.logs.enabled=true`로만 켠다.

### §11.6 경보와 신호 소유

| 경보 | 신호 | 소유 phase | 상태 |
|---|---|---|---|
| AIGW-RETENTION-PURGE-FAILED | `aigw_retention_purge_failures_total` + 고정 로그 코드 `RETENTION_PURGE_FAILED` (정리 Job 실패, 또는 성공한 주기 뒤 남은 가장 오래된 대화 만료가 그 주기 Gateway Clock보다 24h 초과 — 정각은 초과 아님, 주기당 1회) | P14 | 규칙 후보 `operations/monitoring/aigw-alerts.yaml`. 운영 rule 전달은 G-ALERT 미확인으로 NOT_RUN. |
| AIGW-INFERENCE-NOT-READY-PERSISTENT | `aigw_inference_alert{state}` | P19 | state 값 집합 미정 — 규칙 없음 |
| AIGW-INFERENCE-CONTEXT-MISMATCH | `aigw_inference_alert{state}` | P19 | 규칙 없음 |
| AIGW-BACKUP-FAILED / AIGW-BACKUP-STALE | — (2026-11-12 철회(seq17), SPEC §12.4 — 이력: `aigw_backup_last_run_failed`, `aigw_backup_last_success_timestamp_seconds`) | — (이력: P15) | 철회 — 지표·규칙 제거 |
| AIGW-DB-UNAVAILABLE | readiness DB DOWN 2분 | P15 | 지표 신호 없음 — 규칙 없음 |
| AIGW-INGEST-FAILING | `aigw_ingest_events_total{outcome="FAILED"}` | P24 | 임계값 F3 측정 후 |
| AIGW-RESTORE-SOURCE-UNVERIFIED | — (2026-11-12 철회(seq17), SPEC §12.4 — 이력: 복원 원천 대조) | — (이력: P27) | 철회 |

## Source 선택자 로컬 검증 (P12)

현재 상태: CODE/LOCAL_TEST, 부모 독립 검토 대기입니다. 실제 Source 연결·배포 수용·ISO-15는 NOT_RUN입니다.

| Source | 고정 라벨·선택자 | 로컬 stub 관측 |
|---|---|---|
| Loki | `app=tc-gateway` AND `namespace=tc` | 31일 fixture의 하위 창 124개 |
| Loki | `app=tc-business` AND `namespace=tc` | 31일 fixture의 하위 창 124개 |
| Loki | `app=tc-ui-backend` AND `namespace=tc` | 31일 fixture의 하위 창 124개 |
| Loki | `app=tc-ui-front` AND `namespace=tc` | 31일 fixture의 하위 창 124개 |
| Tempo | 위 TC 서비스 4개 | TC span만 반환, WM 서비스 span 제외 |
| GitLab | 미승인 목록 0개 | 기본 거부, 실제 연결 0건 |

31일 fixture는 최신 6시간 하위 창부터 총 496건의 GET을 보냈습니다. 각 하위 요청의 앱 정확 일치와 namespace AND를 검사했고 namespace 단독 selector는 0건입니다. 총 줄 상한·하위 limit·마감으로 조회하지 못한 구간은 `missingRanges`로 구분했습니다. 보존 미확정·확정·범위 밖과 EMPTY를 별도로 확인했습니다.

운영 Connector의 앱·namespace·trace 허용 범위는 시스템 config를 사용합니다. 로컬 양성 fixture는 운영 허용 범위를 늘리지 않습니다.

Source 관측 속성과 meter 태그는 `source_id`, `method`, `status_class` 3개입니다. 검색어·URL·응답 본문·토큰을 이 문서에 보관하지 않습니다. Loki·Tempo의 Service 대상·Pod 포트는 2026-11-23 제공 실측에 따라 연결 선언에 반영합니다. 실제 조회 API 응답과 GitLab 조회 건수는 별도 확인 대상입니다.

## 운영 통신·기동

차트는 앱 설정의 backendUrl 보유 시스템마다 namespace·Pod selector를 받아 ingress와 backend 포트를 계산합니다. LLM·S3는 앱 설정의 명시적 IPv4·포트에서 /32 규칙을 만들고 DB·DNS·Loki·Tempo·OTLP는 values-prod.yaml의 공용 대상을 씁니다. Loki는 canary와 구별하도록 세 라벨 전체로 선택합니다. 클러스터 안 수용 실행 장치는 두지 않습니다.

startupProbe는 readiness와 분리된 `/actuator/health/liveness`를 10초·180회로 확인합니다. 문서 동기화 동안 readiness는 닫혀 있고 liveness는 그 의존성으로 닫지 않습니다. CI 배포 대기 시간 2100초는 startupProbe의 최대 1800초와 이후 readiness 전환 여유를 포함합니다. 메모리를 실측한 뒤 resources와 JVM RAM 비율을 확정하며 그 전에는 임의 값을 두지 않습니다.

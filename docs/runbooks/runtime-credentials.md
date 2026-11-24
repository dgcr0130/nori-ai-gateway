# 운영 런타임 자격

운영자가 namespace `tc`에 Secret `ai-gateway-config`를 준비합니다. 차트는 Secret을 만들지 않습니다(`configSecret.create=false`). Secret 전체와 앱 설정 ConfigMap을 projected volume 하나로 `/app/config`에 읽기 전용으로 마운트합니다. 값은 승인된 파일·stdin으로 공급하고 저장소·로그·명령행 리터럴에 남기지 않습니다.

| Secret 키 | 내용·소비자 |
|---|---|
| `ai-gateway-db.properties` | 저장소 `config/ai-gateway-db.properties`와 같은 placeholder 파일. 앱이 import합니다. |
| `AI_GATEWAY_DB_USER` | 단일 DB 계정 이름. 앱 env로 전달합니다. |
| `AI_GATEWAY_DB_PASSWORD` | 단일 DB 비밀번호. 앱 env로 전달합니다. |
| `tc-service-token-sha256` | TC 서비스 토큰의 소문자 SHA-256 64자리 파일 |
| `tc-actor-jwks.json` | TC actor 단언 검증용 공개 JWKS. 운영 키를 사용합니다. |
| `tc-gateway-token` | Gateway가 TC 표준 조회 API에 보내는 Bearer 토큰 |
| `access-key`·`secret-key` | 문서 원천 S3 읽기 자격. 각각 UTF-8 값 하나입니다. |
| `gitlab-read-token` | TC 코드 조회와 배포 계보 판독이 쓰는 GitLab 그룹 `norisystem/nori-tc` 액세스 토큰 `ai-gateway-tc-code-read`(Reporter, `read_api`, 만료 2027-11-24). 그룹 안 모든 프로젝트를 읽을 수 있으므로 만료 전에 같은 조건으로 교체합니다. |

Secret에 `ai-gateway.yaml` 키를 넣지 않습니다. 같은 projected volume의 ConfigMap 키와 경로가 충돌합니다. 앱 설정 정본은 저장소 `config/ai-gateway.yaml`이며 CI가 `--set-file appConfig=config/ai-gateway.yaml`로 차트에 전달합니다. ConfigMap `ai-gateway-app-config`가 이 파일을 공급하고 해시 변경 시 Pod가 재시작됩니다.

TC 자격 기본 경로는 `/app/config/tc-service-token-sha256`·`/app/config/tc-actor-jwks.json`·`/app/config/tc-gateway-token`입니다. 로컬에서는 `AI_GATEWAY_TC_SERVICE_TOKEN_SHA256_FILE`·`AI_GATEWAY_TC_ACTOR_JWKS_FILE`·`AI_GATEWAY_TC_GATEWAY_TOKEN_FILE`로 덮어쓸 수 있습니다. S3 기본 경로는 `/app/config/access-key`·`/app/config/secret-key`이며 `AI_GATEWAY_S3_ACCESS_KEY_FILE`·`AI_GATEWAY_S3_SECRET_KEY_FILE`로 바꿉니다. S3 기본 자격 provider chain은 쓰지 않습니다.

GitLab 읽기 토큰은 시스템의 `repositories`를 등록할 때만 준비합니다. 그때 공용 Source 선언의 토큰 파일 경로와 chart의 `netpol.targets.gitlab`도 함께 지정합니다. TC는 저장소 5개(project 24·26·28·31·32)를 등록했고 토큰 파일 경로는 `/app/config/gitlab-read-token`입니다. LLM이 자격을 요구하면 `ai.gateway.llm.apiKeyFile`을 지정하며, 생략하면 Authorization을 보내지 않습니다.

DB 계정 관리는 공유 서버 담당자 소관입니다([db-server-prerequisites.md](db-server-prerequisites.md)). 계정 값은 Secret의 두 키에만 두고 DB properties에는 값을 적지 않습니다. 변경 후 운영자가 Deployment를 재시작합니다. CI migration에는 별도로 protected CI 변수 `AI_GATEWAY_DB_USER`·`AI_GATEWAY_DB_PASSWORD`를 등록합니다. main·tag 파이프라인의 `migrate`는 자동 실행되며 앱 배포만 수동입니다.

문서 원천이 있으면 기동 시 자격 검사·원본 열거·추출·재임베딩·색인 활성화가 완료되어야 readiness가 열립니다. 동기화 실패 시 readiness가 닫힙니다. 운영 이미지는 tokenizer를 `/app/tokenizer/`에 포함하고 자동 다운로드를 금지합니다.

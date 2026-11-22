# AI Gateway 검증 수용 자격 runbook

대상: namespace `tc` 검증 환경 전용(Plan §11.7·H-2). 실행자 = G-VERIFY-CREDS 운영자, Linux bash, 이미 가진 namespace `tc` 권한만. 스크립트는 `--namespace` 를 받지 않는다. prod values 는 수용 Secret 들을 참조하지 않고 `ProductionSafetyGuard` 가 `test-` kid·hook 토큰을 거부한다. 실제 실행은 승인·게이트 뒤에만 한다(현재 NOT_RUN — 로컬 stub selftest 만 수행).

## 다루는 Secret

| Secret | 키 | 쪽 |
|---|---|---|
| `ai-gateway-acceptance-signer` | `signer-private.jwk`(EC P-256, kid `test-<UTC>`) | 수용 |
| `ai-gateway-acceptance-client` | `service-token`, `hook-token` | 수용 |
| `ai-gateway-acceptance-fixture-writer` | `access-key`, `secret-key`(F3·F4, SeaweedFS 관리자가 만든 값 — 스크립트는 존재만 확인) | 수용 |
| `ai-gateway-config` 의 `jwks.json` | 공개 JWK 만 | Gateway |
| `ai-gateway-config` 의 `service-tokens.json` | `{"<sha256 hex>":"TC"}` | Gateway |
| `ai-gateway-config` 의 `hook-token-sha256` | hook 토큰 해시 | Gateway |

`ai-gateway-config` 는 운영자가 먼저 만든다(`runtime-credentials.md`). 이 스크립트는 그 Secret 을 만들거나 지우지 않고 위 세 키만 `kubectl patch --type merge --patch-file /dev/stdin` 으로 바꾼다(DB 설정·계정·GitLab·SeaweedFS 키 불변, `kubectl apply` 의 last-applied annotation 에 값이 복제되지 않음). 수용 oracle 의 DB 자격은 앱과 같은 단일 계정(`AI_GATEWAY_DB_USER`·`AI_GATEWAY_DB_PASSWORD` 키 — 차트의 수용 Job 이 env·파일로 받음)이라 이 스크립트가 다루는 DB 역할·Secret 은 없다(Plan §11.7 2026-11-10).

값은 스크립트 메모리와 `kubectl apply -f -`·`kubectl patch --patch-file /dev/stdin` 의 stdin 으로만 다닌다. 출력·증거·로그에는 Secret 이름·항목 상태·고정 사유 코드만 남는다. 값을 확인하려고 `kubectl get secret -o yaml|json` 을 원문으로 출력하지 않는다.

## 명령

```bash
bash operations/deploy-checks/aigw-verify-credentials.sh provision --stage F1   # F1..F4
bash operations/deploy-checks/aigw-verify-credentials.sh check --stage F1
bash operations/deploy-checks/aigw-verify-credentials.sh rotate --stage F1
bash operations/deploy-checks/aigw-verify-credentials.sh cleanup
```

### check

항목(`actor-signer`·`service-token`·`hook-token`, F3·F4 는 `fixture-writer` 추가)마다 stdout 에 `<항목> MATCH|MISSING`, stderr 에 MISSING 사유 1줄을 쓴다. 사유는 `SECRET_ABSENT:<Secret>`·`KID_NOT_TEST_PREFIX`·`JWKS_HAS_PRIVATE_MEMBER`·`PUBLIC_KEY_MISMATCH`·`HASH_MISMATCH`·`FORMAT_INVALID` 중 하나다. 증거는 `acceptance/results/<단계 폴더>/deploy-checks/verify-credentials-check-<UTC>.json`(items·missingReasons)에 남는다.

- exit 0 = 전부 MATCH, 12 = 하나라도 MISSING.
- Secret 조회 오류는 부재가 아니다 → `SECRET_QUERY_FAILED:<이름들>`·exit 1(증거 미기록).
- check 는 Secret 대조일 뿐 실제 DB 접근 PASS 가 아니다. 수용 하네스의 `OracleQueries.verifyDatabaseBoundary` 가 suite 시작 전 `nori_ai_gateway` 고정 SELECT 를 확인한다.

### provision

1. 부작용 전 판정: Secret 조회(오류 = 실패), `ai-gateway-config` 부재 → `CONFIG_SECRET_MISSING` exit 2. 하나라도 실패하면 Secret 을 바꾸지 않는다.
2. 수용 Secret 2종(signer·client)이 모두 있으면 덮어쓰지 않고 check 만 한다. 일부만 있으면 `PROVISION_PARTIAL_STATE` exit 2(불일치는 rotate 로만 고친다).
3. 새 자격 한 벌을 만든다. 키는 `openssl ecparam -name prime256v1 -genkey` 파이프로 만들고, 토큰은 32바이트 CSPRNG 다. 수용 쪽 Secret 을 먼저 apply 하고, Gateway 쪽 공개키·해시를 나중에 `ai-gateway-config` 키로 patch 한다.
4. 존재하는 앱 Deployment(`ai-gateway`. 2026-11-12 철회(seq17) — 이력: `ai-gateway`·`ai-gateway-restore`)를 `rollout restart` 하고 status 를 확인한다(Gateway 는 기동 시 1회 로드). 그다음 check 결과로 종료한다.

### rotate

기존 signer·client Secret 이 있어야 한다(없으면 `ROTATE_REQUIRES_EXISTING` exit 2). provision 의 1·3·4 단계와 같고, Gateway 쪽에서는 이전 kid 공개키와 이전 서비스 토큰 해시만 빼고 새 값을 넣는다(그 밖 키·시스템 토큰 보존).

### cleanup(검증 종료 P29 뒤·환경 폐기 시)

한 단계라도 실패하면 그 자리에서 exit ≠0 이고, 뒤 단계는 하지 않는다.

1. 부작용 전: Secret 조회(오류 = 실패)와 신뢰 제거 결과 계산(`jwks.json`·`service-tokens.json` 형식 오류 → `JWKS_FORMAT_INVALID`·`SERVICE_TOKENS_FORMAT_INVALID`, 변경 0 — 수용 kid·해시가 남아 고친 뒤 재시도할 수 있다).
2. 수용 Job `ai-gateway-acceptance` 삭제(2026-11-12 철회(seq17) — 이력: `ai-gateway-acceptance`·`ai-gateway-restore-acceptance`)(새 수용 연결 차단, 다른 workload 무변경).
3. 수용 Secret 3종(signer·client·fixture-writer) 삭제.
4. Gateway 쪽 신뢰 제거: `ai-gateway-config` 의 `jwks.json` 에서 `test-` kid 와 수용 kid 를 빼고, `service-tokens.json` 에서 수용 토큰 해시만 빼며(남는 항목이 없으면 빈 집합으로 키 유지 — 앱 Pod 의 키 투영 유지), `hook-token-sha256` 키를 지운다.
5. 실행 중 앱 Deployment 마다 `<app> RESTART_OR_UNINSTALL_REQUIRED` 를 출력한다. 제거된 신뢰는 재기동 전까지 Gateway 메모리에 남으므로 운영자가 재배포 또는 uninstall 한다.

이미 없는 Secret·키는 멱등 성공이다. 같은 명령을 다시 실행해도 결과가 같다.

## 단계 게이트와의 관계

`aigw-stage-gate.sh` 는 배포 전에 `check --stage <행렬의 검증 자격 단계>` 를 부른다(F3-PROBE 는 F2). MISSING 이면 RUN ABORTED(`VERIFY_CREDENTIALS_MISSING`)·exit 12 이고 배포 0 이다. provision·rotate·cleanup 은 게이트가 부르지 않는 운영자 명령이다.

## 로컬 확인

```bash
bash operations/deploy-checks/aigw-verify-credentials-selftest.sh   # stub kubectl, 실제 자격·클러스터·DB 0
```

## 기계적 정답률 평가

제품 루트의 명령 형식은 다음과 같습니다.

```powershell
.\gradlew.bat :apps:ai-gateway-acceptance-cli:run --args="answer-evaluate --system TC --questions acceptance/question-sets/tc-answers-v1.jsonl --gateway http://localhost:8080" --no-daemon
```

`AI_GATEWAY_EVAL_SERVICE_TOKEN_FILE`과 `AI_GATEWAY_EVAL_SIGNER_FILE`은 자격 파일의 경로입니다. 상대 경로는 제품 루트 기준입니다. 토큰·개인키 본문을 명령행이나 로그에 넣지 않습니다. 평가 키는 로컬 시험 Gateway의 TC 공개 JWKS에만 등록합니다.

토큰 파일은 UTF-8의 Bearer 토큰 본문만 담습니다. Gateway와 같이 앞뒤 공백·끝 개행을 `strip()`하고 `[A-Za-z0-9._~+/-]+=*`를 검사합니다. `Bearer ` 접두사는 파일에 넣지 않습니다. JDK `SecureRandom`으로 32바이트 이상을 생성해 Base64URL로 인코딩하여 보호된 파일에 쓰고, 공백·끝 개행을 뺀 동일 토큰 bytes의 SHA-256 소문자 hex를 로컬 Gateway TC `serviceTokenSha256File`에 씁니다.

서명 파일은 UTF-8 JSON 객체 하나이며 다음 8개 필드만 둡니다. 누락·알 수 없는 필드·형식 오류는 첫 HTTP 요청 전 exit 2입니다.

| 필드 | 내용 |
|---|---|
| `privateJwk` | EC P-256 개인 JWK 객체. `kty=EC`, `crv=P-256`, `kid`, Base64URL의 `x`·`y`·`d`를 갖습니다. 기존 ES256·공개/개인키 쌍 검사를 적용합니다. |
| `issuer` | `config/ai-gateway.yaml`의 해당 시스템 issuer와 같은 문자열 |
| `subject` | 평가 사용자 ID, `[A-Za-z0-9_-]{1,64}` |
| `permissions` | 평가 사용자 부여 권한 배열, 각 `[A-Z0-9_]{1,64}` |
| `codeSha256` | 측정자가 실행한 로컬 Gateway jar 파일의 SHA-256 |
| `generationModelSha256` | Ollama `/api/tags`가 보고한 생성 모델 digest |
| `embeddingModelSha256` | 같은 응답의 임베딩 모델 digest |
| `configSha256` | 로컬 Gateway가 시험 DB의 활성 `kn.index_set.config_sha256`에 기록한 지문 |

JWK는 JDK 21의 `KeyPairGenerator.getInstance("EC")`에 `ECGenParameterSpec("secp256r1")`을 지정하여 생성합니다. `ECPublicKey.getW()`의 X/Y와 `ECPrivateKey.getS()`를 각각 부호 없는 32바이트로 맞추어 padding 없는 Base64URL로 인코딩합니다. 이 값을 `privateJwk`에 넣고, 같은 객체에서 개인 `d`를 뺀 공개 JWK만 `{"keys":[공개 JWK]}` 형태로 로컬 Gateway TC `actorJwksFile`에 씁니다. 실행 가능한 생성·서명 대조 예시는 CLI의 `EvaluationFilesTest`와 `JwkAssertionSignerTest`입니다. 자격은 파일에서 읽어 주입하며 키·토큰을 stdout으로 확인하지 않습니다. Gateway는 기동 때 자격을 읽으므로 해당 시험 파일을 지정한 뒤 기동합니다.

지문 네 개는 모두 64자리 소문자 hex입니다. CLI는 형식만 검사하며 실행 환경과 일치하는 값의 공급은 측정자가 담당합니다. jar는 `Get-FileHash -Algorithm SHA256 -LiteralPath <실행 jar 경로>`의 Hash를 소문자로 변환합니다. 모델 digest는 허가된 메인 측정 환경에서 Ollama `/api/tags` 응답의 `models[]`에서 실제 실행 모델(`qwen3-vl:30b-a3b-instruct-q4_K_M`, `bge-m3:latest`)의 `digest`를 읽습니다. 설정 지문은 Gateway 기동·문서 동기화 후 시험 DB에서 다음 읽기 전용 조회로 확인합니다. 공유 운영 DB를 조회하지 않습니다.

```sql
SELECT config_sha256
FROM kn.index_set
WHERE system_id = 'TC' AND state = 'ACTIVE';
```

CLI는 제품 루트 `config/ai-gateway.yaml`의 `ai.gateway.systems.<--system>`에서 issuer·reviewPermission과 tools·documents·apps·traces·repositories의 requiredPermissions 합집합을 읽습니다. 필요한 값의 `${...}`를 해석하지 않으며 문자열·배열 리터럴이 아니면 환경 오류입니다. 평가 사용자는 검토 권한과 이 합집합 전체를 가져야 합니다. 문항 permissions는 질문·대화·결과 요청에, 서명 파일의 전체 권한은 검토 문항 수집에 씁니다. 각 단언의 pdg는 정렬·중복 제거한 perm을 끝 개행 없이 LF로 이어 UTF-8 SHA-256으로 계산합니다.

원본 Markdown은 제품 루트 `rag-documents/<bucket>/<objectKey>`에서 읽습니다. 세트의 사실·자식 위치·원본 revision을 대조하고, 응답 결과 payload의 문서 위치(답변 인용과 근거 포함, 업무 조회 values 제외)에서 세트가 요구하는 문서와 bucket·objectKey가 같고 revision이 다른 위치가 하나라도 있으면 채점 없이 입력 오류 exit 2로 종료합니다. SeaweedFS를 CLI가 직접 읽지 않습니다. 로컬 파일은 시험 Gateway 기동에 사용한 원본과 같아야 합니다.

결과는 stdout JSON이고 합격은 exit 0, 기준 미달은 1, 입력·자료·환경 오류는 2입니다. 시험 결과는 중앙 [006 PLAN의 phase 9](../../../nori-sdd-specs/work-items/006-ai-gateway-redesign/spec-plan.md) 기록에서 확인합니다.

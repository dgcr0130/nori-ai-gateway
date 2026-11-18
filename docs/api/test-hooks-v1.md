# Verify 시험 훅 계약 v1

verify 프로필에서 Job 결속·취소·실행 목록·관측을 검증하는 계약입니다. 업무 응답은 Gateway 외부의 로컬 표준 HTTP 대역이 제공합니다.

모든 아래 경로는 `verify` 프로필과 `ai.gateway.test-hooks.enabled=true`에서만 등록됩니다. 필수 `ai.gateway.test-hooks.token-sha256-file`은 UTF-8 소문자 SHA-256 64자리(끝 LF 선택) 파일입니다. 부재·형식 오류는 입력 원인·내용을 노출하지 않고 기동 실패합니다. 요청의 `X-AIGW-Hook-Token`을 SHA-256으로 바꿔 constant-time 비교하며 누락·불일치·중복 헤더는 빈 404입니다.

`TestHookAuthFilter`는 raw Filter 빈이나 `@Component`가 아니며 단일 `FilterRegistrationBean`의 `/__test/*` REQUEST 등록만 사용합니다. 서비스 토큰·actor 검증은 `/v1/*`만 담당합니다. `/actuator/health/liveness`, `/actuator/health/readiness`는 둘 밖이며 상태 문자열만 반환합니다.

| 경로 | 입력·응답 |
|---|---|
| POST `/__test/arm` | `{actorSub, requestKey, scenario:{taskType, toolCalls?:[{toolId,arguments}], pauseAt?:[point], haltAt?:point, failSanitizer?:boolean}}` → 204, 중복 409 |
| POST `/__test/reset` | 비 terminal 0일 때만 204, 아니면 409 `{error:"ACTIVE_JOBS"}` |
| GET `/__test/clock` | `{now:UTC ISO8601, mode:SYSTEM 또는 FROZEN}` |
| POST `/__test/clock` | `{op:FREEZE_AT,at}` 또는 `{op:ADVANCE,seconds:양의 정수}` 또는 `{op:RESUME_SYSTEM}` → 현재 시계 |
| POST `/__test/pause` | `{point,op:HOLD 또는 RELEASE,jobId?:ID}` → 204, jobId 생략 시 전역 `*` |
| POST `/__test/sweep-now` | 시간 한도 sweeper 1회 → 204 |

`taskType`은 기존 닫힌 TaskType enum입니다. 정지 지점은 `ADMISSION_FAST_MISS`, `ADMISSION_AFTER_COMMIT`, `BEFORE_CLASSIFICATION`, `TOOL_IN_FLIGHT`, `BEFORE_COMPOSER`, `TERMINAL_PRECOMMIT`, `TERMINAL_POSTCOMMIT`입니다. P09 제품 실행은 접수·분류 전·종결 지점에 연결하고 Tool·Composer 실행 지점은 시험 fake/P13·P20에서 연결합니다. 전역 보류 지점은 `DISPATCH_HOLD`, `SWEEPER_HOLD`, `PURGE_HOLD`입니다. PURGE_HOLD는 감사·캐시 정리를 보류합니다. `haltAt`의 P09 실제 연결은 분류 전과 terminal 커밋 전·후이며 exit 137을 일으킵니다.

`requestKey`는 접수 요청의 기존 `Idempotency-Key`입니다. `(actorSub,requestKey)` 무장은 60초 절대 TTL이며 동일 키 재무장은 409입니다. 신규 Job INSERT 뒤 같은 접수 tx에서 commit 전에 jobId 결속을 예약합니다. 첫 워커는 afterCommit 깨우기보다 먼저 폴링으로 시작해도 결속을 봅니다. rollback은 결속·정지점을 제거하고 무장을 복원하며 commit은 소비를 확정합니다. 중복 202와 충돌 409는 결속을 호출하지 않습니다. 결속은 해당 Job만 대상으로 하며 다른 actor·Job으로 전파되지 않습니다.

reset은 무장·결속·정지점과 시계를 SYSTEM으로 초기화합니다. 아직 커밋 대기 중인 접수가 있으면 결속 보호를 위해 같은 409를 반환합니다. 위임의 재발급·연장·폐기는 수행하지 않습니다. `FREEZE_AT`의 임의 시각은 reset 직후 비 terminal 0일 때만 허용하며 이후에는 뒤로 이동할 수 없습니다. 초기 `ai.gateway.test-hooks.clock.{initial,mode}`는 Clock 생성 시 적용하므로 Router 이전에 확정됩니다. 무장·결속·시계는 프로세스 메모리이며 재시작하면 사라집니다.

SQL·URL·호스트·파일 경로 입력이나 토큰·위임·원문·Prompt·답변·벡터 반환은 허용하지 않습니다. 시험용 `/v1/query`, 결과 GET, 하네스 관측 endpoint와 P09 fake 포트는 integrationTest 산출물에만 있고 제품 런타임에 없습니다. 프로세스 시험의 재개 분류 입력은 시험 config가 명시 재주입하며 제품의 hook 상태 내구성을 주장하지 않습니다. 실제 Source·LLM·운영 수용 증거로 사용하지 않습니다.

## 실행 목록과 표준 HTTP 대역

`scenario.toolCalls`는 0~4개이며 생략하면 빈 목록입니다. 실제 분류를 대체하는 시험에서만 사용하고 입력 schema·권한·실행 예산·Dispatcher 검사는 그대로 거칩니다. `scenario.seam`은 `{kind,capabilityId?,sourceId?,ownerSystem?}`이며 기존 등록 도구·Source에만 적용됩니다. directive는 접수 tx와 Job별 결속 수명을 공유하며 singleton 등록 값을 바꾸지 않습니다. idle reset은 seam을 초기화합니다.

업무 대역 `StandardSystemStub`은 system adapter의 testFixtures에만 있습니다. `/internal/ai/v1/query`로 실제 HTTP 클라이언트를 검사하고 Gateway 제품에 제어 endpoint를 추가하지 않습니다. 수용 하네스의 `AIGW_BACKEND_FIXTURE_URL`·`AIGW_BACKEND_FIXTURE_TOKEN_FILE`은 별도 대역의 주소·제어 토큰 파일입니다. 대역의 `/__fixture/reset`·`/__fixture/scenario`·`/__fixture/delegations`·`/__fixture/calls`만 그 주소로 보내며, Gateway `/__test/*`와 인증을 분리합니다. 운영 backend 주소로 이 fixture 제어를 보내지 않습니다. 하네스 reset은 Gateway의 idle reset 성공 후에만 대역 호출 횟수·시나리오를 초기화하며, 등록 위임과 폐기 상태는 보존합니다.

## P15 추가 계약

| 경로 | 입력·응답 |
|---|---|
| POST `/__test/egress-probe` | `{targetId}` 만 사용(본문의 다른 필드·호스트·포트 무시). 차트가 렌더한 `{release}-egress-probe` ConfigMap 파일(`ai.gateway.test-hooks.egress-targets-file`, JSON 배열 `[{id,host,port,expect}]`)의 id 이면 Gateway Pod 안에서 TCP connect 1회(timeout 3s, 본문 송신 0) → 200 `{targetId, connected, errorClass}`(errorClass = null·`TIMEOUT`·`REFUSED`·`UNRESOLVED`·`NO_ROUTE`·`IO_ERROR`, 주소 반환 0). 목록 밖 id·targetId 부재 404 |
| GET `/__test/egress-probe` | (P21) 요청 본문 미사용. 같은 목록 파일의 대상을 파일 순서대로 200 `[{id, expect}]`(`expect` = `ALLOWED`·`BLOCKED`) 로만 돌려준다(호스트·포트·주소 반환 0, 연결 시도 0). 수용 AC-35-EGRESS 는 이 목록만 판정 입력으로 쓰며, 목록에 `BLOCKED` 가 하나 이상이고 양성 대조 id `inference-endpoint`(추론 endpoint)·`db-main`(주 DB) 가 `ALLOWED` 로 있어야 판정한다(없으면 NOT_RUN — runconfig fixture `operations/deploy/scripts/runconfig/fixtures/lint-verify.json` 과 같은 id) |
| POST `/__test/embedding-probe` | (`ai.gateway.inference.mode=enabled`에서 등록) 요청 본문 미사용. 실제 `InferenceClient.embed(["probe"])` 1회 → 200 `{dimension, modelDigest}`. 공통 API에서 digest를 조회하지 않으므로 `modelDigest`는 null이다(기존 nullable 응답 유지). 임베딩 실패 503 `{reason}`(`TRANSPORT`·`EMPTY_RESPONSE`). 벡터 값·입력·주소 반환 0 |
| POST `/__test/readiness-stub` | (`ai.gateway.inference.mode=enabled`, verify 전용) 정확히 `{kind}`(문자열). `MODEL_LOADING`·`INFERENCE_UNAVAILABLE`은 고정 준비 관측값을 주입하고 30초 주기에도 유지한다. `OFF`는 고정을 해제해 설정한 `/models`·최소 chat·embedding을 실제 검사한다. 전환 직후 1회 검사 → 200 `{state}`(`READY`·위 두 상태). 고정 두 kind는 외부 요청 0회이다. kind 부재·추가 필드·목록 밖 kind 400 |
| POST `/__test/arm`의 `scenario.llmScript` | (P20, 라벨 LLM_STUB, verify+enabled+`ai.gateway.inference.mode=enabled` 일 때만 효과) optional 비어 있지 않은 배열 `[{callType, content, finishReason, usage:{completionTokens, promptTokens?}}]`. `callType`=`CLASSIFY`·`PLAN`·`COMPOSER`, `content`=문자열 또는 null(빈 응답). `CLASSIFY` 의 정상 응답 fixture 는 여섯 키 JSON `{taskType,toolCalls,targets,timeWindow,searchQuery,retryQuery}`이며 추가·누락 키는 `CONTRACT_INVALID`로 거부된다(hook 은 내용을 검사하지 않음), `finishReason` 문자열, `completionTokens`·`promptTokens` 0 이상 정수(`promptTokens` 생략 시 그 호출의 실제 Gateway 계수). 결속 Job 의 추론 호출만 `ScriptedChatTransport` 가 순번대로 돌려주며 예산 assert·잘림 감지는 Client 에 그대로 남는다. 유형 불일치·소진은 그 호출의 전송 실패다. 무장 없는 Job·스크립트 없는 결속 Job 은 운영 전송(`liveChatTransport`)으로 간다. 스크립트 원문은 무장 본문(하네스 fixture)에서만 오고 로그·응답·증거에 싣지 않는다. 형식 위반 400 |
| GET `/__test/prompt-watch/{requestKey}` | (P20, 위와 같은 조건) 무장 `scenario.promptWatch={sentinels:[1~16개, 각 1~64 코드포인트]}` 이 결속된 Job 의 분류·Composer 조립 Prompt(PromptReducer 뒤·PreCallBudgetAssert 직전)에서 sentinel 이 든 호출 수만 메모리에 센다 → 200 `[{callType, sentinelIndex, matchCount}]`(관찰된 호출 유형마다 sentinel 전부). Prompt 원문·부분 문자열 반환 0. requestKey 의 결속 Job 이 정확히 1개가 아니거나 관찰이 없으면 404. 결속 Job terminal 뒤 60s 보관 후 삭제, idle reset 이 함께 지운다 |

hook 은 verify+enabled 조건에 더해 목록 파일 설정이 있을 때만 등록된다(차트 verify 렌더는 항상 마운트, 형식 위반 파일은 기동 실패). 판정(ALLOWED/BLOCKED 대조)은 수용 하네스 쪽 AC-35-EGRESS(P21) 몫이며, 대상 목록은 GET `/__test/egress-probe` 에서만 읽는다(수용 Job env 입력 없음).

DISPATCH_OWNER_OVERRIDE는 Plan 단계 원본을 유지하며 해당 Job의 지정 capability를 Dispatcher 직전에만 WM 소유로 투영합니다. 기존 OWNER_OVERRIDE의 Plan/Dispatch 동작은 유지합니다.

`GET /__test/source-observations/{jobId}`는 verify+enabled 및 기존 hook 토큰 인증 아래 실제 접수된 terminal Job의 Loki query_range 관측을 반환합니다. 실제 송신 직전 carrier에서 app·namespace·start/end를 정규화한 SHA-256과 건수·고정 상태만 메모리에 보존하며 URI·검색어·시간 원문·자격을 반환하거나 기록하지 않습니다. Job/capability별 완료 0회와 관측 부재를 구별하고, 미완료·오형식·상한 초과는 실패로 처리합니다. 다른 스레드·보존 탐침은 해당 Job에 섞지 않고 기존 idle reset 성공 시 함께 제거합니다. active reset 거부 시 관측은 보존합니다.

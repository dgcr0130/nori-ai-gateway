# AI Gateway API v1

이 문서는 승인된 Spec §9.1·§9.3·§12.2·§15.2·§15.3·§22.3과 Plan P10의 HTTP 계약을 설명합니다. 로컬 구현·시험은 운영 배포·수용 완료를 의미하지 않습니다.

모든 `/v1` 요청은 서비스 Bearer 토큰과 매 요청 새 actor 단언을 검증합니다. `X-Actor-Assertion` 또는 body `contextRef` 중 하나만 사용합니다. `dlg`는 `/v1/query`, `/v1/analyses`, `/v1/conversations/{id}/messages`의 POST에만 필요하며 다른 경로에는 넣지 않습니다. owner·actor는 검증된 단언과 서비스 바인딩에서 정하고 body로 선택하지 않습니다.

| 경로 | 성공 응답 |
|---|---|
| `POST /v1/conversations` | 201, 새 대화의 `id`, 소유 범위, 생성·수정 시각, 상태 |
| `GET /v1/conversations?after=&limit=20` | 200, `items[{id,title,createdAt,updatedAt,status}]`, `nextAfter` |
| `DELETE /v1/conversations/{id}` | 204, 활성 Job이 있으면 409 `CONVERSATION_ACTIVE` |
| `GET /v1/conversations/{id}` | 200, `conversation`과 `messages` |
| `POST /v1/query` | 202, `jobId`, `conversationId`, `messageId`, `queuePosition` |
| `POST /v1/conversations/{id}/messages` | 같은 202 형식, 경로의 대화에 후속 질문 접수 |
| `POST /v1/analyses` | 같은 202 형식, 대화가 없으면 접수와 원자 생성 |
| `GET /v1/analyses/{jobId}` | 200, Job 상태와 접근 가능한 완성 결과 |
| `POST /v1/analyses/{jobId}/cancel` | 204, 본인·boundSystem의 취소 가능한 Job |
| `GET /v1/analyses/{jobId}/events` | `text/event-stream`, 저장 진행 이벤트와 heartbeat |
| `GET /v1/analyses/{jobId}/evidence/{evidenceId}/source` | 200, 이용 가능한 원문의 `maskedText` |
| `GET /v1/analyses/{jobId}/blocks/{blockId}/export` | 200, 저장 결과에서 식별한 블록 |
| `GET /v1/audit/jobs/{jobId}` | 200, 본인·boundSystem Job의 보존 기간 내 감사 |
| `PUT /v1/answers/{answerId}/feedback` | 200, `{feedbackId,version,state}`; answerId는 Job ID |
| `GET /v1/reviews?state=OPEN&after=&limit=20` | 200, `{items,nextAfter}`; 현재 검토 권한·근거 권한 필수 |
| `GET /v1/reviews/{feedbackId}` | 200, 평가·영구 스냅샷·기존 조치·활성 `questionCase` 또는 null |
| `POST /v1/reviews/{feedbackId}/resolve` | 200, `{feedbackId,version,state}` |

접수 body는 `query`, 선택 `conversationId`, `context`, `resourceRefs`, `options`를 받습니다. 알려진 실행 입력만 snapshot에 보존하며 JWS·contextRef·dlg·HTTP header·클라이언트 신원은 포함하지 않습니다. `context.timezone`, `context.referenceTime`, 선택 `context.timeWindow{from,to}`는 기존 날짜 문법과 timezone·offset·나노초를 검증하고 원래 문자열을 보존합니다. `resourceRefs`의 각 항목은 `{type,ref}`이며 기존 `ResourceRef`로 실행 입력에 투영합니다. `options.allowTools/allowKnowledge`는 부재·null·boolean을 구분하고 미제공을 기본 true/false로 바꾸지 않습니다. 알려진 선택 필드의 부재·명시 null·값을 Job마다 독립적으로 보존합니다. `sourceVersions`는 새 wire 계약으로 추가하지 않습니다.

`Idempotency-Key`의 범위는 owner·actor입니다. 같은 키·같은 정규화 입력은 기존 `jobId`와 저장된 같은 `messageId`로 202를 반환하고 snapshot·위임·TTL을 갱신하지 않습니다. 질문·대화·경로 또는 `context/resourceRefs/options`의 부재·null·값이 달라지면 409 `{"error":"IDEMPOTENCY_KEY_CONFLICT"}`입니다. 준비 미완료·큐 포화는 503과 `Retry-After`를 반환하고 신규 Message·Job·대화를 남기지 않습니다. 접수 후 실패는 GET의 HTTP 200과 `jobStatus`·결과 상태로 조회합니다.

정상 결과 응답은 `jobId`, `jobStatus`, `createdAt`, `terminalAt`, `result`를 포함합니다. `result`는 `status`, `errorCode`, 저장 `payload`, `clarification`을 담으며 실행 중 결과는 null입니다. 현재 단언의 권한을 Source 안에서는 ANY, Source 사이에서는 AND로 판정하고 참조 문서는 모두 현재 `AVAILABLE`이어야 합니다. 실패·UNKNOWN·잘못된 의존 계약은 fail-closed입니다. 의존성 없는 P09 종결의 정확한 `{}`는 빈 의존으로 호환합니다. 결과 접근 거부는 아래 다섯 필드만 반환합니다.

```json
{"jobId":"...","jobStatus":"SUCCEEDED","createdAt":"...","terminalAt":"...","resultAccess":"DENIED"}
```

같은 평가를 결과 GET·각 결과 메시지의 대화 이력·Evidence 원문·블록 export·감사 조회에 적용합니다. 거부 시 Source·권한 코드·문서·개수·사유·답변 일부를 노출하지 않고 Tool·LLM 재호출이나 재생성을 하지 않습니다. 원문 참조의 owner·sourceRef·chunkId·expectedRevision은 저장 Evidence에서 읽으며 URL·query에서 revision을 받지 않습니다. `EvidenceSourceReader`가 비가용이면 미존재 Evidence와 같은 빈 404입니다. 현재 문서 상태는 활성 메모리 색인에서 revision·권한으로 검사합니다. 선택한 답변 근거는 같은 종결 트랜잭션의 영구 스냅샷에서 마스킹 본문을 읽고, 기존 근거는 `EvidenceSourceReader` 경로를 유지합니다. 과거 revision의 원본 파일을 별도로 보관하거나 새 revision 본문으로 옛 근거를 대체하지 않습니다.

타 owner·타 actor·부재 접근은 모두 HTTP 404, 빈 body입니다. 대화는 자동 만료·자동 삭제하지 않습니다. 목록은 현재 시스템·사용자로 제한하고 updatedAt/id 내림차순이며 limit은 1~100입니다. title은 가장 이른 user 메시지의 마스킹 본문 앞 100 Unicode 코드포인트이고 질문이 없으면 null입니다. 서버 커서는 시스템·사용자에 서명 결속하며 형식·범위 불일치·위조는 400입니다. 재기동 후 커서는 첫 쪽에서 다시 발급합니다. 질문 저장과 updatedAt 갱신은 같은 트랜잭션입니다. 삭제는 대화 행 잠금 후 QUEUED/RUNNING 부재를 확인하고 대화 묶음을 CASCADE 삭제합니다. 삭제 후 단건·메시지·결과·SSE·근거·내보내기·감사 경로는 404입니다. 영구 지식·스냅샷·피드백 계열은 대화 삭제와 분리합니다. 취소·180초 Job 수명·대기 제한·캐시 자체 만료·감사 90일 보존은 유지합니다.

SSE 연결·재연결은 매 요청 새 actor 단언을 사용합니다. `Last-Event-ID`는 같은 Job·owner·actor의 실제 저장 이벤트 ID일 때만 유효하며, 다르면 스트림을 시작하지 않고 빈 404를 반환합니다. 이후 ID 순으로 재생하며 `external_suppressed` 이벤트는 제외합니다. 전송 data의 허용 키는 `type,jobId,seq,queuePosition,toolId,status,jobStatus,errorCode`이고 답변 본문은 전송하지 않습니다. heartbeat 안전 초기값은 15초이며 `ai.gateway.sse.heartbeat`로 설정합니다. 열린 스트림의 이벤트별 TC 재인가는 F5b 중계 책임입니다.

`job.started`는 실제 dispatcher의 `QUEUED → RUNNING` 전이와 같은 트랜잭션에서 저장합니다. commit 뒤 같은 Job의 queued, started를 발행한 다음 worker를 실행합니다. 시작 전 종결이 먼저 이기면 started 행·발행·worker 실행이 없으며 rollback도 RUNNING과 started를 함께 취소합니다.

최종 결과 wrapper의 `payload.blocks[]`에는 서버가 확정한 배열 순서에 따른 additive `id`(`b0`, `b1`, …)가 있습니다. 전체 참조는 `(jobId,id)`이며 같은 dataRef를 가진 블록도 구분합니다. content 안의 업무 id는 유지하며 upstream `ResultBlock`·Tool envelope·Manifest에는 이 필드를 요구하지 않습니다. 최종 저장 직전에 `ResultBlockIds`를 사용하는 실제 workflow producer는 P13의 책임입니다. GET·대화 이력은 저장 id를 재발급하지 않고, Export는 해당 Job의 저장 id가 정확히 한 건일 때만 같은 블록을 반환합니다. 미존재·중복 id는 빈 404입니다. P10의 helper 기반 저장 fixture 시험은 P13 실제 workflow 생산/저장 검증을 대신하지 않습니다.

## P13 실행 결과 연결

terminal 상태·이벤트 집계의 정본은 승인 Spec §5.4의 표입니다. 실행기는 선택 단계의 `OPTIONAL_STEP_OMITTED`·`SKIPPED_NO_INPUT`을 실패 집계에서 제외하고, 실제 단계 결과를 `toolSummary`에 보존합니다. F1의 `disabled` 실행은 Composer를 호출하지 않으며 서술 부재만으로 실패나 `LLM_UNAVAILABLE`을 만들지 않습니다. 입력·계획 확인이 필요한 결과의 저장 payload는 `clarification{reason,fields}`만 포함합니다.

`options.allowTools/allowKnowledge`는 정확히 `false`인 경우에만 차단합니다. 필수 조회를 차단하면 자원·Tool·캐시 조회 전에 `PLAN_INVALID` 확인 요청으로 종결하며, 선택 Knowledge만 차단하면 해당 단계를 제외합니다. GQ 도움말은 두 값이 false여도 업무 조회 없이 생성합니다. 시간창을 이전 요청에서 이어받는 경우 같은 대화에서 바로 앞에 접수된 요청 한 건의 확정 창만 사용하고, 그 요청에 창이 없으면 더 오래된 창을 건너뛰어 고르지 않고 구간을 확인합니다.

실제 workflow의 최종 블록 배열을 확정한 후 `ResultBlockIds.assign`을 한 번 적용합니다. Evidence의 최종 저장 식별자와 모든 최종 참조는 현재 Job에 결속하고, 캐시 envelope의 원 ToolExecution 식별자·이전 Job 저장 결과는 바꾸지 않습니다. 결과·Evidence·사실 답변 스냅샷·assistant 메시지·Context·요약·Source 의존성과 terminal 상태·이벤트·감사는 같은 마지막 트랜잭션에서 저장합니다. 실행 중 이미 확정한 Tool 호출 이력은 마지막 저장의 실패로 지우지 않습니다.

## 사실 답변과 영구 스냅샷

사실 답변의 `result.payload.answer`는 `{kind,text,citations,sentences}`입니다. `kind`는 `ANSWER`, `PARTIAL`, `NO_EVIDENCE`이고 `sentences`는 대조를 통과한 `{text,citations:[번호]}`만 보존합니다. 인용 목록은 `{number,evidenceId,sourceType,location,title}`이며 문서 위치는 bucket·objectKey·revision·section·lineFrom·lineTo, 비문서 위치는 해당 조회 항목의 위치입니다. 화면용 text에는 문장 뒤 `[1]` 형식 인용을 붙입니다. 일반 도움말 답변도 같은 구조이며 `kind`는 `ANSWER`, `text`는 도움말 평문, `citations`·`sentences`는 빈 배열입니다.

문장은 1~20개, 문장 text는 1~1000 코드포인트·줄바꿈 금지, 문장별 인용은 1~6개입니다. 잘못된 인용·수치·식별자·이름을 포함한 문장은 제거합니다. 일부 제거 또는 통과 문장이 PARTIAL·STALE·CONFLICT 근거를 인용하면 kind는 PARTIAL입니다. 고정 문구는 그 순서로 각각 한 번 공개 text 끝에만 붙이고 sentences에는 넣지 않습니다. 통과 문장 0개나 불충분은 `{kind:"NO_EVIDENCE",text:"근거 없음",citations:[],sentences:[]}`입니다. 정상 자료 부족은 EMPTY·SUCCEEDED이며 장애·부분·절삭은 실제 집계를 보존합니다. 생성 장애는 블록·근거가 모두 없으면 FAILED, 하나라도 있으면 PARTIAL이며 errorCode는 LLM_UNAVAILABLE입니다. 미검증 답변·스냅샷은 저장하지 않습니다.

공개 answerId로 사용하는 식별자는 기존 Job ID입니다. 내부 스냅샷 ID를 API·SSE·내보내기·감사에 노출하지 않습니다. 마스킹 질문·답변·근거 본문과 SHA-256·위치·권한·설정 지문을 영구 보존하고 같은 Job 재종결은 같은 내용 해시만 재사용합니다. 다른 해시는 무결성 오류입니다. 스냅샷 저장 실패는 결과·근거·종결 감사·이벤트를 함께 롤백하며 commit 뒤에만 완료 이벤트를 발행합니다. 대화 삭제 후 스냅샷은 남지만 일반 열람 경로는 모두 404입니다. 시험 결과는 중앙 006 PLAN의 해당 phase 기록에서 확인합니다.

## 피드백과 검토

위 네 API도 서비스 인증과 새 사용자 단언을 사용하며 `dlg`를 받지 않습니다. 요청 본문은 UTF-8 64KiB 이하입니다. 문자열 길이는 Unicode 코드포인트로 계산하고 저장 전에 마스킹합니다. 오류 외피는 `{code,message,requestId}`이며 입력 오류 400, 검토 권한 부족 403, 없거나 열람할 수 없는 ID 404, 버전 충돌 409 `REVIEW_CONFLICT`, 스냅샷 없는 소유 Job 409 `ANSWER_NOT_READY`, DB 실패·확정 전 임베딩 장애 503입니다. 기존 API의 오류 외피는 유지합니다.

피드백 body는 `{rating:"UP"|"DOWN",comment:"0~2000자"}`입니다. 살아 있는 본인 대화에서 현재 열람할 수 있는 답변에만 평가를 씁니다. 같은 본문 재전송은 ID·version을 유지합니다. 최초 UP·빈 지적은 RESOLVED, DOWN 또는 지적이 있으면 OPEN입니다. 확정·기각 후 본문을 바꾸면 version을 1 늘려 OPEN으로 다시 열고 이전 검토 이력을 보존하며 이전 검증 답변·문항을 비활성화합니다. 대화 삭제 후 일반 평가 쓰기는 404이고 검토자는 영구 스냅샷을 계속 검토할 수 있습니다.

검토 목록·상세·조치는 config의 `reviewPermission`과 스냅샷 각 근거의 모든 permissions를 현재 단언에 요구합니다. 근거 권한이 빠진 항목은 목록에서 제외하고 상세·조치에서는 404입니다. 목록은 필터 뒤 createdAt·feedbackId 오름차순으로 정렬하고 limit은 1~100(기본 20)입니다. 서버 커서는 시스템·state에 결속한 마지막 항목의 시각·공개 피드백 ID를 담습니다. 형식·시스템·state 불일치는 400이며 마지막 쪽과 빈 목록의 nextAfter는 null입니다. 재기동 후 커서는 첫 쪽부터 발급합니다. items는 `{feedbackId,version,state,rating,comment,createdAt,updatedAt}`입니다.

상세는 `{feedback,answerId,question,answer,evidence,reviews,questionCase}`입니다. feedback은 목록 항목과 같고 reviews는 `{version,reviewerRef,cause,action,createdAt}` 이력입니다. questionCase는 현재 활성 문항 하나 또는 null이며 문항은 `id,systemId,question,answerable,expectedFacts,evidenceLocations,context,permissions,origin,feedbackId,reviewVersion,documentTargets,predecessorId`를 보존합니다. 검토 문항의 origin은 FEEDBACK, predecessorId는 null입니다. 이 API는 문항 저장·활성 상태를 제공하며 정답률 채점은 별도 평가 기능의 책임입니다.

재발 문항의 질문은 1~2000자 계약을 적용합니다. 이를 넘는 기존 질문은 자르거나 질문 접수 규칙을 바꾸지 않으며, 문항을 생성하는 문서 조치는 400으로 거절합니다. 문항 없는 메모·기각 처리는 같은 제한을 추가하지 않습니다.

RETRIEVAL·ANSWER의 locations는 정답 문항에 그대로 저장되므로 현재 자식 블록의 줄 범위를 사용합니다. 부모 전체 줄 범위를 정답 위치로 바꾸어 저장하지 않습니다. DOCUMENT의 보완 대상은 현재 부모 위치도 사용할 수 있습니다.

resolve body는 `{version,cause,action}`이며 OPEN인 제출 version 하나만 확정합니다. 성공하면 해당 version의 review와 원문 없는 REVIEW_RESOLVED 감사를 저장하고 feedback.version을 1 늘립니다.

| cause | action과 저장 효과 |
|---|---|
| DOCUMENT | `{note,locations}`; note 1~2000자·문서 위치 0~6개. 위치 0개면 문항 없음, 위치가 있으면 false 문항의 documentTargets로만 사용하고 사실·정답 위치는 빈 배열입니다. |
| RETRIEVAL | `{terms,correctAnswer,expectedFacts,locations}`; terms 1~20개(키 1~64자·풀이 1~256자), 문서 위치 1~6개·사실 1~20개(각 1~256자). 용어·검증 답변·true 문항을 저장합니다. |
| ANSWER | `{correctAnswer,expectedFacts,locations}`; RETRIEVAL과 같은 답변·사실·위치 규칙으로 검증 답변·true 문항을 저장합니다. |
| REJECT | `{note}`; note 1~2000자. REJECTED로 바꾸며 검색·문항을 만들지 않습니다. |

실시간 근거 전용 스냅샷은 모든 cause에서 `{note}`만 받습니다. 문서 조치는 현재 문서 위치·권한·revision과 사실 본문을 검증합니다. correctAnswer는 `{sentences:[{text,citations:[locations 번호]}]}`이며 기존 문장 대조기에 그대로 넣습니다. 1~20문장·문장 1~1000자·줄바꿈 금지·인용 1~6개를 적용하고 번호/구조 오류나 한 문장이라도 제거되면 400입니다. 위치 확인과 임베딩은 잠금 전에 끝내고 저장 시 version·현재 묶음/revision을 다시 검사합니다. review·version·답변·사전·문항·감사는 한 트랜잭션이며 실패하면 모두 취소합니다. 확정 DB 용어는 초기 config 값보다 우선하고 기존 설정 지문을 바꾸지 않습니다. DB 확정 후 평가·검토 응답은 커밋된 {feedbackId,version,state}의 200을 유지합니다. 메모리 반영 성공 중에는 접수를 닫지 않으며, 겹친 커밋은 최신 DB 상태를 다시 투영하고 실패로 응답하지 않습니다. 실제 메모리 반영 실패 때만 DB version으로 재구성될 때까지 새 질문 접수를 503으로 닫습니다. readiness 헬스와 이미 접수된 Job의 디스패치는 기동 준비 상태를 그대로 사용합니다. DOCUMENT 문항은 대상 문서 revision 변경·삭제 뒤 기동 활성화 트랜잭션에서 비활성화합니다. 원본 문서는 이 API가 수정하지 않습니다. 시험 결과는 중앙 006 PLAN의 phase 8 기록에서 확인합니다.

# NORI 공통 AI 영구 대화 및 TC 최초 연동 SPEC v2.0

> **대상 저장소:** `C:/workspace_nori/nori-ai-gateway`, `C:/workspace_nori/nori-tc-ui-backend`, `C:/workspace_nori/nori-tc-ui-front`  
> **정본 문서:** `nori-ai-gateway/docs/ai-gateway-to-nori-tc.md` — 기존 파일을 개정하며 대체 Spec을 만들지 않음  
> **작성일 / 개정일:** 2026-11-11 / 2026-11-12 (Asia/Seoul)  
> **버전 / 단계:** 2.0.2-draft / Specify — 영구 chat 신규 구현·운영 DDL·배포 미실행. 2026-11-12 사용자 추가 지시로 기존 백업 코드·배포·CI 제거만 별도 수행(§19.4)  
> **확정 방향:** 공통 AI가 영구 대화 정본을 소유하며 TC가 첫 연결 시스템이다. HRM·MES·WM을 같은 계약으로 확장한다.  
> **DB:** 기존 `nori_ai_gateway`의 `chat` 신규 schema + 기존 Gateway DataSource/트랜잭션 매니저 재사용

---

## 0. 문서 경계와 확정 결정

### 0.1 이번 재설계의 효력

2026-11-12 사용자는 공통 AI의 영구 대화 소유·Backend API 이용 방식을 채택하고 **기존 Gateway DB를 고려한 재설계**를 지시했다. 이에 따라 v1의 **TC 업무 DB 영구 대화 정본·TC Outbox/본문 복제·외부 아카이브 Receipt·TC가 구성하는 과거 문맥** 설계를 대체한다. 현재 요구는 이 v2 본문이며 v1 결정은 §19의 이력으로만 보존한다.

Gateway도 이번 Spec의 구현 대상이다. 기존 승인 Gateway Spec/Plan·기준선의 파일은 이번에 직접 개정하지 않는다. 해당 문서와의 증분 정합·허용 쓰기 범위·migration 번호는 후속 Gateway Plan/승인 기준선 절차에 연결해야 한다. 이 문서의 설계 승인이 기존 phase 구현/배포나 다른 시스템 온보딩 승인을 대신하지 않는다. 이번 산출물은 이 파일 하나이며 Plan·Dev report·별도 audit 문서를 만들지 않는다.

### 0.2 확정 저장 책임

| 영역 | 정본 소유자 | 수명 / 접근 경계 |
|---|---|---|
| 사용자별 대화·마스킹된 질문·표시 답변·결과 의존 정보·실행 요약 | **공통 AI / nori_ai_gateway.chat** | 자동 만료 없음. 각 시스템 Backend가 인증된 API로만 이용 |
| 실행 Conversation·Job·Tool·임시 문맥·캐시·Evidence | 기존 **nori_ai_gateway.gw** | 기존 30일 실행 Conversation 및 각 운영 보존 정책 유지 |
| Knowledge metadata·chunk·ledger·checkpoint | **kn은 현재 schema만 존재**, 테이블은 기존 Gateway Plan P23/P24의 구현 대상 | 영구 chat과 별도 수명. 테이블·원문/현재 문서 리더 구현 완료를 뜻하지 않음 |
| 사용자·세션·업무 권한·업무 데이터 | TC/HRM/MES/WM 각각의 Backend·업무 DB | 각 시스템이 현재 신원/권한의 정본 |
| TC 위임·세션 지문·알림 | TC Redis 및 TC 인증 영역 | 임시 상태. 영구 대화 본문 저장 금지 |

각 시스템 Backend에 Gateway DB 접속 계정/DataSource를 추가하지 않는다. TC의 `nori_tc`에 `tc_ai_conversation/message/execution/binding/outbox`를 만들지 않는다. 업무 감사에는 필요한 식별자·상태만 남길 수 있고 질문/답변을 중복 저장하지 않는다. 기존 업무 DB와 Gateway DB 사이 FK·SQL JOIN·분산 트랜잭션을 만들지 않는다.

### 0.3 최상위 불변식

| ID | 불변식 | 수용 관점 |
|---|---|---|
| INV-01 | Browser는 자기 시스템 Backend만 호출하고 Backend가 공통 AI API를 호출 | Browser 네트워크·자격 비노출 |
| INV-02 | 대화 소유권은 검증된 `(owner_system,owner_actor_ref)` | 동일 actor 문자열의 TC/HRM/MES/WM 간 격리 및 같은 시스템 타 사용자 IDOR |
| INV-03 | 시스템과 actor는 service Bearer+actor JWS에서 정하며 body로 바꿀 수 없음 | 서비스/issuer/key/sys 결속 |
| INV-04 | `chat` 영구 데이터는 `gw` 30일 만료·purge와 독립 | 실제 기존 purge 후 31일+ 조회·후속 질문 |
| INV-05 | 자동 만료 없는 영구 보관, 삭제/탈퇴/법정 의무는 별도 정책 | TTL·명시 삭제·용량 관리 |
| INV-06 | 보관 답변도 현재 Source 권한·현재 문서 가용성을 검사 | 회수/삭제/장애 시 fail-closed |
| INV-07 | Gateway가 검증·종결하고 영구 커밋한 결과만 최종 표시 | 원자 종결·SSE/GET 정합 |
| INV-08 | 동일 논리 질문은 실행 DB 삭제 후에도 새 Job으로 재실행되지 않음 | 영구 멱등 키·타임아웃/재기동 |
| INV-09 | 질문은 전송 전 마스킹, 원문·Prompt·자격·위임을 대화/로그/감사에 보관하지 않음 | 마스킹·비밀 음성 시험 |
| INV-10 | 기존 TC 업무 및 기존 Gateway `/v1` 실행 계약을 회귀시키지 않음 | Cookie/CSRF·Job/만료·복구 회귀 |

### 0.4 유지하는 사용자 확정 A — 전송 전 마스킹

사용자의 앞선 **“권장으로 진행”** 결정은 유지한다. 시스템 Backend는 Gateway 전송 전에 승인 sanitizer를 적용하고 `effectiveQuery`만 전송한다. Gateway도 같은 sanitizerVersion의 후조건을 검증해 USER·영속 접수 입력·실제 Job 질문을 일치시킨다. 마스킹 전 질문은 접수 처리 메모리에만 존재하며 DB/Redis/Outbox/로그/trace/감사에 저장하지 않는다. 단기 원문 암호화 보관 예외도 없다.

입력창의 민감정보 제거/의미 변경 가능성 안내와 `maskingApplied` 표시를 유지한다. 접수 후 Browser 원문 메모리는 서버 effectiveQuery로 교체하며, 추가 확인 클릭은 요구하지 않는다. 초기 호환 기준은 Gateway `ContentSanitizer`의 `mask-v1` 규칙/후조건이다. TC Core의 순수 처리와 Gateway 검증을 같은 호환 벡터로 검사하고, TC가 Gateway 모듈을 런타임 의존하지 않는다. 규칙 변경은 새 버전이며 기존 요청의 버전을 보존한다. sanitizer 실패 시 원문 전송으로 fallback하지 않는다.

---

## 1. 확인한 코드·DB 구조와 증거 한계

2026-11-12 로컬 작업 트리를 읽었다. Front HEAD=`9fe69a75dcd3277d9c7de535c8368bc860a0ec5d`, Backend HEAD=`985d17c604dda3430a181cbac953ef2cc70c314f`, Gateway HEAD=`9b13bf6439fba12ab48c7ff1b6de2a976ae97040`이다. Gateway 승인 문서에는 기존 미커밋 개정이 있으므로 코드 HEAD와 작업 트리를 함께 해석한다. 운영 DB catalog·실제 Flyway 이력·운영 데이터는 조회하지 않았다. 아래 schema 사실은 **현재 저장소 Migration과 실제 repository SQL**의 근거이며 라이브 적용 완료를 뜻하지 않는다.

| 확인 근거 | 확인된 사실 / 설계 영향 |
|---|---|
| `flyway/sql/V1__schemas_and_extension_assert.sql` | vector 0.8.7을 검사하고 gw·kn schema를 생성. 새 DB/확장 설치가 아닌 chat schema 추가로 설계 |
| `flyway/sql/` 전체 7파일, Gateway Plan §8·§16.6 | 현재 V1/V2/V3/V3_1/V4/V5/V7만 존재하고 **CREATE TABLE kn.*는 0개**. V10의 kn.document/kn.chunk/kn.ingest_ledger/kn.checkpoint/kn.document_fence/kn.catalog_state 6종은 계획이며 현재 DB Adapter에 Knowledge repository가 구현된 상태가 아님 |
| `V2__conversation_job_core.sql` | gw.conversation의 만료와 message/context/summary/job/cache의 CASCADE, job→result/evidence/event/tool CASCADE 존재. 영구 데이터를 이 부모의 자식으로 두면 안 됨 |
| `V3__audit_ops.sql`, `V3_1__auth_rejection_identity.sql` | 감사의 owner CHECK/인증 거절 NULL 정책·90일 수명 존재. MES 지원 시 audit 포함 기존 CHECK와 enum의 증분 정합 필요 |
| `V4__job_request_authority.sql`, `V5__job_request_input.sql`, `V7__audit_request_identity.sql` | 현재 요청 권한·message 결속·snapshot/감사 계약을 유지해야 함. 이미 적용된 V 파일 수정 금지 |
| `ConversationRepository`, `ScopedJdbc`, `AdmissionLookupRepository` | 30일 만료 SQL, owner/actor 스코프, gw.job에 종속된 멱등 조회. 영구 조회/멱등 저장소를 그대로 재사용할 수 없음 |
| `JobAdmissionService.accept()` | 자체 트랜잭션이며 외부 트랜잭션 진입을 거부. 새 원자 접수를 이 메서드의 바깥 tx로 감싸는 구현은 불가 |
| `JobTerminalCommitter`, `TerminalCommitObserver` | 결과/메시지/이벤트/감사의 단일 종결 tx와 beforeCommit/afterCommit 확장점 존재. 영구 결과도 같은 커밋에 참여해야 함 |
| `RetentionMaintenanceRepository`, `RetentionPurgeJob` | 활성 Job 없는 만료 gw.conversation을 batch 삭제하고 감사 90일 정리. chat을 purge 대상으로 확장하지 않음 |
| `OwnerSystem`, `ActorAssertionVerifier` | 현재 TC/WM/HRM enum과 고정 issuer=tc-ui-backend. MES 및 시스템별 issuer/key 신뢰는 아직 구현된 기능이 아님 |
| `CoreBeansConfiguration`, `config/source-permissions/tc-v1.json` | 현재 TC Source 권한/manifest를 조립. 다른 시스템은 registry 등록뿐 아니라 자기 Source/권한/READ Adapter 조립이 필요 |
| `GatewayAppModuleConfiguration`, `TcMockModuleConfiguration` | TC 업무/인가 Mock이 현재 명시적으로 import됨. 실 HTTP Adapter 추가만으로 교체되지 않으며 prod의 Mock/실 Adapter 배타 조립이 신규 작업 |
| `UiAuthSessionDbMapper`, `JpaSessionPort`, common `TcUiAuthSessionEntity` | DB `tc_ui_auth_session.token`은 원본이 아니라 SHA-256 hex 지문 PK. sessionRef는 이 지문을 사용하고 재해시·전체 세션 스캔·새 지문 인덱스를 전제하지 않음 |
| `ValidateTokenUseCase`, `UiTokenAuthenticationFilter`, `UiApiPermissionCache` | Redis 히트 시 주기적 세션 재검사는 있으나 매번 계정/grant를 새로 읽지 않음. ASYNC 인증 재사용·인증 장애 401·URL catalog 캐시가 있어 현행 인증만으로 AI 최신 접근/503 계약을 충족하지 않음 |
| `ResultResponseAssembler`, `ResultAccessEvaluator`, `FailClosedKnowledgeDocumentStateReader` | 현재 평가는 ALLOW/DENY 두 값, 문서 UNKNOWN·예외도 DENIED로 축약. 신규 chat의 DENIED/UNAVAILABLE 구별·실제 문서 상태 판정은 별도 구현 필요 |
| `DatabaseEmptinessProbe`, `RecoveryModeRouter` | 빈 DB 검사는 gw/kn만 조회. gw purge 후 chat만 남은 DB를 신규 DB로 오인하지 않도록 chat 포함 검사가 필요하며 초기 registry 4행은 업무 데이터와 구별해야 함 |
| `SchemaVersionGuard` | 클래스패스 최신 Migration과 DB 최신 성공 버전의 **정확한 일치**를 검사. 단순 구 이미지 rollback이 자동 호환된다고 가정하면 안 됨 |

관련 로컬 근거: [Gateway API](contracts/gateway-api-v1.md), [Gateway 인증](contracts/auth-v1.md), [기존 스키마](../flyway/sql/V2__conversation_job_core.sql), [purge](../modules/adapter/ai-gateway-db-adapter/src/main/java/com/nori/ai/gateway/persistence/maintenance/RetentionMaintenanceRepository.java), [종결 tx](../modules/adapter/ai-gateway-db-adapter/src/main/java/com/nori/ai/gateway/persistence/queue/JobTerminalCommitter.java), [Front Router](../../nori-tc-ui-front/src/app/Router.tsx), [TC 인증](../../nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java).

### 1.1 기존 DB의 구현 범위와 보존 경계

2026-11-12 재검증: V2는 `gw.conversation/message/context/conversation_summary/job/queue_control/job_event/result/evidence/tool_execution/cache_entry` 11개, V3는 `gw.audit_event/source_retention_probe/backup_run/instance_epoch` 4개를 만든다. V3_1/V4/V5/V7은 이 테이블의 열·제약 보완이다. kn 테이블과 chat 7테이블은 현재 Migration에 없다. Flyway 이력 테이블은 별도 `gw_flyway`에서 관리된다.

- conversation의 `(id,owner_system)`에서 message/context/summary/job/cache로, job의 `(id,owner_system)`에서 event/result/evidence/tool로 ON DELETE CASCADE한다. 기존 FK는 actor까지 포함하지 않으므로 runtime actor 격리는 `ScopedJdbc`의 부모 owner/actor 조건과 접수 검증이 보완한다. 기존 runtime FK를 신규 chat의 actor Composite FK와 동일하다고 표현하지 않는다.
- V5의 `job.request_message_id`는 `(message.id,conversation_id,owner_system)`의 지연 FK다. USER role/actor는 이 FK만으로 보장되지 않는다. V4 request_perm/request_pdg와 V5 request_input, V7 감사 원점(service_ref/trace_id)도 새 접수에서 유지한다. `gw.job.delegation_id`는 terminal 상태에서 NULL이어야 한다.
- runtime 만료는 `ConversationRepository.insert()`의 생성 시각+30×24h이며 touch/후속 질문은 expires_at을 연장하지 않는다. purge는 `expires_at <= now`와 QUEUED/RUNNING Job 부재를 재확인해 100행씩 `FOR UPDATE SKIP LOCKED`로 삭제한다. 감사는 FK 없이 독립 보관하며 90일 cutoff로 정리한다. Source 보존·cache expires_at·Knowledge 수명을 모두 30일로 일반화하지 않는다.
- 기존 인덱스는 conversation.expires_at, job(status,seq), job(conversation_id), owner+actor+idempotency_key의 부분 UNIQUE다. 기존 runtime 멱등 키는 conversation CASCADE로 없어지므로 영구 멱등성의 근거가 될 수 없다. chat 인덱스는 §6의 별도 설계다.

### 1.2 승인 문서와의 미해결 정합 경계

아래는 기존 사용자 결정을 다시 선택하는 항목이 아니다. 이번 문서만 고쳐 기존 Gateway 승인 계약까지 변경되었다고 간주하지 않으며, **후속 Plan 승인 전 정본 소유자의 증분 반영·기준선 정합**이 필요하다.

| 충돌/추가 범위 | 확인 근거 | 처리 경계 |
|---|---|---|
| 기존 F5b의 독립 `/ai` 페이지 ↔ D-07 overlay | Gateway 아키텍처 도입부·§19, Plan §13 인계 | 사용자 확정 overlay 유지. 기존 문서의 `/ai`를 이 작업에서 임의 수정하거나 두 화면을 함께 구현하지 않음 |
| 기존 30일 대화 조회/후속 질문 금지 ↔ D-01/03 영구 chat | Gateway Spec §12.2·계약 gateway-api-v1 | 기존 `/v1/conversations/analyses` TTL은 유지하고 신규 chat 경로의 수명·영구 문맥을 증분 계약으로 승인해야 함 |
| 기존 phase의 허용 쓰기·V4~V12 예약 ↔ 신규 chat/접수/종결/복구 변경 | Gateway Plan §7~§9·§16.3, JobAdmissionService/JobTerminalCommitter | 이 Spec을 기존 phase allowed_writes의 포괄 승인으로 사용하지 않음. 새 migration 번호와 변경 범위는 후속 승인 대상 |
| 고정 issuer·단일 JWKS 키 조회 ↔ 향후 시스템별 신뢰 | ActorAssertionVerifier의 iss 상수·credentials.key(kid), OwnerSystem 3값 | TC 최초 연동과 다른 시스템 확장을 구분. registry 4행만으로 MES/다른 issuer가 지원되지 않음 |

기존 검증 이력은 §19.2에 보존하며 이번 재실행 결과는 §19.3에만 추가한다. 기존 기준선·승인 기록·evidence는 편집하지 않는다.

재검증 중 seq16 기록은 처음 승인 대기였으나 후속 읽기에서 **2026-11-12 SPEC 소유자 승인 기록**으로 바뀐 것을 확인했다. 이 작업은 그 기록/기준선을 편집하지 않았으며, 해당 기록은 기존 아키텍처 seq16에 대한 것으로 이번 영구 chat Spec/충돌 해소 승인까지 뜻하지 않는다. 기준선 SHA 일치/spec-guard 성공 자체는 파일 결속 검사이지 승인·phase 재개 허가가 아니다. 관찰 시점과 승인 적용 범위를 구분한다.

---

## 2. 범위와 완료 조건

P0는 TC 실제 로그인 사용자가 기존 업무 화면의 플로팅 패널에서 실제 Tool/Source/LLM 답변을 받고, 공통 AI DB의 여러 대화를 영구 재조회·후속 질문할 수 있게 하는 것이다. 202 수신·Mock 화면·문서/DDL 존재만으로 완료가 아니다. TC Source는 처음 연결하는 구현이며 HRM/MES/WM의 연결 완료를 주장하지 않는다.

- 공통 AI: 시스템/actor 격리, 영구 대화·목록·페이징·멱등 접수·결과 원자 보관·현재 접근 검사·31일 후 문맥·SSE/API.
- TC Backend: Cookie/CSRF·현재 사용자/권한·서비스/JWS·위임·TC READ API·공유 전달 게이트·API/SSE 중계.
- TC Front: 기존 업무 라우트의 단일 overlay, 2열/모바일·다중 대화·재연결/폴링·안전한 renderer·ko/en/vi·로그아웃 정리.
- 비범위: 타 시스템 대화 합치기·사용자가 sys/owner 선택·업무 Mutation·공유/삭제/검색/파일 업로드·공용 Front SDK 선행 추상화·기존 Gateway 모델/RAG 자체 재설계.

---

## 3. 목표 아키텍처와 다중 시스템 경계

```text
TC Front  → TC Backend  ─┐
HRM Front → HRM Backend ─┤  service Bearer + actor JWS (검증된 system/actor)
MES Front → MES Backend ─┼→ 공통 AI Gateway → 기존 nori_ai_gateway DataSource
WM Front  → WM Backend  ─┘                    ├─ chat : 영구 대화/메시지/결과/멱등 실행
                                             ├─ gw   : 30일 실행 Conversation/Job/임시 기록
각 Backend ↔ 각자의 업무 DB·인증/권한/READ API     ├─ kn   : schema만 생성, Knowledge 테이블은 P23 예정
                                             └─ gw_flyway : 기존 Migration 이력
```

각 연결의 service 신원이 하나의 owner_system에 결속되고 actor는 그 시스템 내부의 불변 식별자다. TC는 `Long.toString(userPk)`, 다른 시스템은 동일 actor 문자열을 사용해도 owner_system이 다르면 별개다. `chat`은 사용자 이름·이메일·비밀번호·업무 사용자 테이블을 복제하지 않는다. actor는 현행 JWS의 `[A-Za-z0-9_-]{1,64}` 범위를 유지하며 사용자 표시명 변경으로 소유자가 바뀌지 않는다.

하나의 system code에는 하나의 인증 사용자 ID namespace만 연결한다. 서로 독립된 TC 설치 두 곳의 userPk를 같은 TC code에 합치지 않는다. 그런 복수 설치를 추가할 때는 고유 namespace·서비스 신뢰·Source 소유 계약을 먼저 확정하며, 이번 TC/HRM/MES/WM 단일 연결 설계에서 자동 지원한다고 보지 않는다.

system registry는 TC/HRM/MES/WM을 사전 등록하되 신규 chat 기능은 모두 DISABLED로 설치하고 TC만 계약/Source/보안/운영 Gate 후 활성화한다. 이는 기존 legacy API의 WM/HRM 허용 상태를 임의 변경하는 정책이 아니다. MES는 기존 Java enum·gw.conversation/audit CHECK·서비스 owner parser·Source/문서 참조 validator·issuer/key 설정까지 새 Migration/코드로 지원한 후 활성화한다. 단순 DB registry INSERT로 MES가 동작한다고 보지 않는다.

서비스 비밀/JWKS/Backend endpoint는 서버의 승인 설정이며 DB registry·Browser 입력으로 임의 URL/키를 지정하지 못한다. 시스템별 issuer·kid/JWKS·Bearer owner·Source Adapter를 명시적으로 묶고 TC의 기존 issuer 계약은 보존한다. 신뢰되지 않은 issuer/key 또는 sys 불일치는 Source/대화 조회 전에 거부한다. HRM/MES/WM은 TC를 가장하거나 TC Source Adapter로 fallback할 수 없다.

## 4. Frontend UX/컴포넌트 SPEC

### 4.1 플로팅 실행 버튼

- 위치: 기본 우하단, `bottom/right` 안전 여백과 safe-area 고려. 고정(`position: fixed`) 및 기존 화면과 시각적 충돌 방지.
- 가시성: **인증 완료 + AI 사용 permCode 보유 + 기능 활성화**인 경우에만 표시. `/login`에서는 미표시. Front 가림 여부와 무관하게 Backend는 매 API마다 인가.
- 이벤트: 클릭→패널 펼침; 패널 상단 최소화/닫기→버튼 상태. 닫기가 대화 삭제/Job 자동 취소를 의미하지 않는다.
- 접근성: 이름(`NORI AI 채팅 열기`), 키보드 포커스, `aria-expanded`, badge/진행상태를 색상에만 의존하지 않음.

### 4.2 데스크톱 패널

- 기존 페이지 위에 우측 하단 anchored overlay; 독립 페이지 라우팅 및 전체 화면 이동 금지.
- **데스크톱 기본 크기 설계값(확정 구현 값은 토큰으로 집중 관리):** 너비 `min(760px, calc(100vw - 32px))`, 높이 `min(680px, calc(100dvh - 32px))`, 최소 유효 너비 640px에서는 좌측 240px·우측 가변. 사용자 리사이즈/드래그는 P0 비범위.
- 2열 구조: 좌측 ~220~260px(대화 목록), 우측 나머지(대화 내용 및 하단 입력). 헤더에 제목·최소화·닫기. 자체 스크롤 영역 분리.
- 좌측: `새 대화`, `최근 대화 목록`, 선택 상태/마지막 활동 시간, 페이지네이션(하단 더 보기 또는 무한 스크롤), 빈 상태, 로딩/오류/재시도.
- 우측: 선택된 대화의 기존 메시지(시간순), 사용자/AI 메시지 구분, AI 진행 상태, 안전한 Markdown/구조화 블록/근거, 하단 질문 입력 및 전송/취소.
- 대화 목록 선택 후 선택된 대화의 메시지를 로드한다. 새 대화는 TC 서버에 Conversation 생성 후 우측 빈 상태를 보여준다.
- 기존 업무 페이지 위 Drawer/모달과 z-index 충돌을 시험한다. 채팅 오버레이가 다른 보안 확인/경고/프로필 모달을 부적절하게 가리지 않도록 중앙 레이어 정책을 둔다.

### 4.3 작은 화면

- 너비가 640px 미만이면 고정된 2열을 억지로 축소하지 않는다. 패널은 **전체 폭에 가까운 overlay**가 되며 좌측 목록은 내부 목록 뷰/슬라이드 패널로 전환한다. 동일 오버레이의 반응형 표현이며 `/ai` 페이지는 만들지 않는다.
- 640px 이상에서는 가능한 한 좌측/우측 동시 표시를 유지한다.
- 모바일 키보드/viewport resize/safe-area, 키보드가 입력창을 가리는 경우, 스크롤 잠금/복구를 시험한다.

### 4.4 메시지 렌더링과 동작

- 사용자의 질문은 제출 직후 `PENDING` 표시 가능하되, 서버 수락 결과와 연결해서만 `ACCEPTED`로 변경한다.
- 답변은 Gateway가 최종 검증한 `result`를 받은 뒤에만 표시. SSE에는 진행 상태/Heartbeat만 있고 `response.delta`는 없다는 **기존 계약을 준수**한다.
- Gateway Result `payload.blocks[]`는 계약 버전/타입 검증 후 결정론적으로 렌더링. 미지원 타입은 HTML 실행 없이 일반 안내/다운로드 대안으로 fail-closed 처리한다.
- Markdown은 raw HTML, Script, CSS 인젝션, `javascript:`/`data:` URL, 임의 iframe 금지. 안전한 링크만 열 수 있다. Source/Evidence는 Backend의 권한 검증 API를 통해서만 조회한다.
- 로딩·`QUEUED`·`RUNNING`·`SUCCEEDED`·`PARTIAL`·`FAILED`·`CANCELLED`·`TIMED_OUT`·`RESULT_DENIED`·`RECOVERING`은 서로 구분한다.
- 같은 Conversation에서 **동시에 실행 중인 사용자 질문은 P0에서 1건만** 허용한다. 중복 제출은 전송 버튼 비활성화와 Backend `409 AI_CONVERSATION_BUSY`로 이중 차단한다. 같은 `clientMessageId` 재요청은 이 충돌 판정보다 먼저 기존 실행으로 수렴시킨다.
- 취소를 클릭하면 `취소 요청 중`과 최종 `취소됨`을 구분한다. 다른 대화로 이동하거나 패널을 최소화하는 행동은 실행 취소를 뜻하지 않는다.
- 스크롤: 사용자가 하단에 있을 때만 자동 하단 이동; 이전 메시지를 읽는 중에는 위치 유지.
- 제출: Enter 전송·Shift+Enter 줄바꿈. IME 조합 중 Enter 오전송 금지. 빈 입력·공백·상한 초과 방지.
- 입력창에 `민감정보는 전송 전에 가려집니다. 가려진 내용에 따라 답변이 달라질 수 있습니다.`를 locale별로 안내한다. 접수 후 서버의 `effectiveQuery`로 USER 표시를 교체하고 `maskingApplied=true`이면 `민감정보를 가린 질문을 전송했습니다.`를 표시한다. 원문을 Query cache나 재시도 payload에 남기지 않는다.
- 언어: 기존 `ko`, `en`, `vi` locale 3종. 새 문자열은 번역 누락/원문 key 노출 없이 관리한다.
- 기본 지원: Tab/Shift+Tab, Escape(패널 최소화), 이전 포커스 복원, 스크린리더 읽기 순서, 명암/테마, `prefers-reduced-motion`.

### 4.5 Front 상태와 수명

- 서버 상태(대화 목록/메시지/작업 상태)는 TanStack Query. 로컬 UI 상태(패널 열림, 현재 선택 대화, 목록 표시, 미전송 입력)는 단일 Zustand store가 소유한다. Query key는 서버가 검증한 system code·actorRef(TC는 userPk 문자열)와 AI namespace를 포함하며 계정 전환 시 전부 제거한다. Browser에 임의 시스템 선택 기능은 제공하지 않는다.
- `AiChatHost`는 **경로 key가 없는 인증 공통 레이아웃**에 1회 마운트한다. 현행 `Router.tsx`의 `key={location.pathname}` ErrorBoundary/Suspense는 업무 `<Outlet>` 경계로 이동하고 Host는 그 형제에 둔다. 인증 레이아웃 자체에 pathname key를 두거나 각 `AppShellPage` 안에 Host를 배치하지 않는다. `/login`은 인증 레이아웃 밖을 유지하고 Host 오류는 별도 경계로 격리한다.
- 로그아웃·계정 전환·401 시: SSE 종료, 진행 폴링 정지, AI Query cache 제거, 선택 대화/입력값 폐기, 이전 사용자 데이터의 잔상 제거.
- 기존 AppProvider의 TanStack Query `localStorage` 영속 캐시는 allowlist 방식이므로, **AI 목록/본문/답변/원문/근거는 영속 대상에 추가하지 않는다**. 영구 보관은 서버 DB가 책임진다.
- Front 전체 API 기본 Axios timeout 10초에 장기 Gateway 작업을 묶지 않는다. 질문 접수와 상태 GET은 짧은 HTTP; 진행은 EventSource 또는 별도 지속 연결 클라이언트로 처리한다.
- 브라우저 탭 여러 개에서 동일 대화 작업을 보더라도 서버 저장/작업은 하나로 유지한다.
- 새로고침으로 메모리가 사라지면 목록의 `activeExecutionId`와 메시지의 `executionId`로 기존 작업을 복원한다. 원문/AI 본문을 localStorage/sessionStorage에 보관해 복원하지 않는다. 새 접수 응답 유실 시 사용자별 대화 이력 재조회로 기존 접수 여부를 먼저 확인한다.

### 4.6 권장 신규 Front 디렉터리(제안, 현재 구현 아님)

```text
src/features/ai-chat/
  api/ai-chat.api.ts
  components/AiChatHost.tsx
  components/AiChatLauncher.tsx
  components/AiChatPanel.tsx
  components/AiConversationList.tsx
  components/AiConversationView.tsx
  components/AiMessageList.tsx
  components/AiMessageComposer.tsx
  components/AiResultRenderer.tsx
  components/AiJobProgress.tsx
  hooks/useAiConversations.ts
  hooks/useAiMessages.ts
  hooks/useAiExecution.ts
  hooks/useAiEventStream.ts
  stores/ai-chat-ui.store.ts
  types/ai-chat.types.ts
  lib/ai-chat-state.ts
```

공통 `RequireAuth` 아래 인증 레이아웃에 `AiChatHost`를 연결하되 §4.5의 ErrorBoundary 수명 경계를 지킨다. 기존 페이지 URL은 유지한다. 실제 파일/이름 변경은 구현 시 코드 중복 여부를 검토하여 조정 가능하나 위 **컴포넌트 책임 분리**는 유지한다.

---

## 5. Backend와 Gateway의 책임

### 5.1 TC Backend — 업무 신원·권한과 API 중계

기존 Domain/Core/DB·Redis·Web Adapter/Starter를 유지한다. Core는 AI API client·위임/최신 접근 Port를 정의하고, 신규 `ui/adapter/tc-ui-ai-gateway-adapter`가 HTTP/JWS·계약 검증/계측을 구현한다. Web은 `/api/ai/**`와 내부 READ/Source API를 제공하고 starter/settings가 명시적 빈·모듈을 조립한다. 기존 DB Adapter는 **현재 세션/권한·업무 READ·공유 전달 게이트**에만 관여하며 대화 repository/reconciler/본문 Outbox를 추가하지 않는다.

질문 접수는 Backend가 전송 전 마스킹·최신 권한·위임을 확인한 뒤 Gateway 공통 접수 API를 호출한다. Backend 자체의 영속 202를 먼저 반환하지 않는다. Gateway의 원자 접수가 커밋된 202를 받은 뒤 Browser에 전달한다. 네트워크 결과가 불명이면 같은 clientMessageId로 재요청/조회하며 새 키를 자동 발급하지 않는다. 브라우저 연결이 끊겨도 Gateway worker가 같은 DB에서 영구 결과를 완성한다.

### 5.2 Gateway — 공통 영구 대화 use case

Core는 영구 소유권·턴/멱등 규칙·현재 접근 판정·문맥 선택 Port/UseCase를, DB Adapter는 `chat` repository·공유 tx를, app은 공통 Chat Controller/DTO·서비스/JWS 보안·Job 접수/종결 연결을 소유한다. 단일 DataSource/JdbcClient/PlatformTransactionManager와 기존 `gw_flyway`를 재사용한다. 새로운 TC/HRM/MES/WM 전용 대화 서비스·schema·테이블을 복제하지 않는다. 모듈 간 의존·명시적 ModuleConfiguration import 규칙과 ArchUnit 검증을 유지한다.

모든 chat 일반 repository public 메서드의 첫 인자는 `VerifiedSystemScope`다. SQL에서 owner_system+owner_actor_ref를 강제하고 부모 소유권을 함께 검사한다. 유지보수용 제한된 관리 Port를 사용자 repository와 구별한다. legacy `ScopedJdbc.CONVERSATION/JOB`는 gw의 만료 조건을 포함하므로 영구 조회에 그대로 사용하지 않는다.

### 5.3 서비스/JWS·위임

TC Backend→Gateway 서비스 Bearer와 `X-Actor-Assertion` ES256 JWS를 사용한다. TC는 iss=tc-ui-backend, aud=ai-gateway, sys=TC, sub=userPk 문자열, scope=READ, 현재 perm/pdg, `0 < exp-iat <= 60초`를 유지한다. body contextRef와 이중 전달하지 않는다. Browser가 서비스 자격·sys/sub/perm/pdg/dlg/history를 지정할 수 없다.

신규 `POST /v1/chat/conversations/{id}/messages`는 **Job 생성 경로**이므로 RouteClassifier의 dlg 필수 경로에 추가한다. 영구 대화 생성·목록·메시지/실행 GET·SSE·cancel은 비 Job이며 dlg 키 자체가 없어야 한다. legacy 경로의 분류는 유지한다. 동일 clientMessageId 재요청의 dlg/JWS는 새로 발급해도 기존 Job의 위임/TTL을 갱신하지 않는다.

TC Redis에 CSPRNG dlg와 `{userPk,sessionRef,scope=READ,expiresAt,revoked}` 및 세션 역색인을 원자·최대 300초로 관리한다. sessionRef는 서버 전용 세션 지문이며 token 원문을 복제하지 않는다. Gateway worker의 Source 호출은 매번 현재 dlg/세션/권한을 TC에서 검증한다. 로그아웃 후 새 READ는 금지한다. Job이 terminal이면 기존 정책대로 gw.job.delegation_id를 NULL 처리한다. chat 테이블/감사에는 dlg·세션 지문·JWS·서비스 토큰을 저장하지 않는다.

위임 만료는 발급 시각+300초와 실제 TC 세션 expiresAt 중 이른 시각을 넘지 않는다. 큐 대기 60초와 Job 전체 수명 180초는 **같은 접수 createdAt 기준**이며 60+180초의 실행권을 주지 않는다. 새 JWS의 만료는 기존 Job/위임 수명 연장의 근거가 아니다. 시스템 확장 시 service owner·issuer·kid/공개키를 결속하고 타 시스템 키로 인증되는 것을 거부해야 한다. 현재 전역 kid 조회와 고정 issuer는 이 시스템별 신뢰를 구현한 상태가 아니다.

### 5.4 실제 TC READ Source(업무 Tool 계약)

Gateway F1~F4의 TC Mock을 실제 TC로 교체하는 F5a 계열의 통합 구현이 필요하다. 기존 TC 업무 API를 내부적으로 재사용하되 **Gateway가 호출하는 READ 전용 경계**에 인증·인가·응답 스키마를 명확히 둔다.

| 업무 Source | 필수 역할 | 기존 확인 가능한 기반/남은 작업 |
|---|---|---|
| 설비 Snapshot | ID로 설비 상태/관측 시각 조회 | 기존 `EqpQueryPort`/`TcEqpState`/CONN 이력 재사용. 없는 processState를 추측하지 않고 PARTIAL 처리 |
| Work/PJ/LOT | 설비 활성 Work/LOT 조회 | `TcWork`/`TcWorkProcessJob`/`TcWorkLot`/PJ-LOT 연결이 근거. 실제 UI 조회 Port/Adapter는 신규 |
| 최근 이벤트 | 설비/시간창의 확인 가능한 상태 변경 이벤트 | `TcEqpStateHist`의 OPER/CONN 이력 재사용. 별도 알람/업무 이벤트 원천이 있는 것처럼 반환하지 않음 |
| DLQ | Gateway/Business Source + 설비 귀속 + 시간창 | 기존 DLQ/Redis 조회 활용, P13 4arg 설비범위 필터를 실제 원천 귀속에 적용 |
| 통신 로그 | 설비/Source/기간의 comm log | 기존 CommLog 조회 활용, 보존/잘림/빈 결과 구분 |
| 모델 | READ 범위 모델·노드·버전 | 기존 Model READ 활용, 임의 모델 변경 기능 노출 금지 |
| Resource resolve | EQP/MODEL ref를 검증된 자원으로 해석 | owner=TC 및 현재 권한 확인 |
| Source authorize | `sub + dlg + requestedPermCodes`에 대한 현재 허용/거부와 pdg | 신규 내부 HTTP API. 기존 Mock의 `AuthorizationResponse` 스키마 및 `project()` 투영 계약을 재사용 |

다음은 **신규 TC 내부 HTTP 계약 `tc-ai-source-v1`**이다. 현행 Endpoint의 존재를 주장하는 내용이 아니며, TC Backend 제공자와 별도 Gateway 실제 HTTP Adapter 소비자가 동일하게 구현할 기준이다. Public `ApiResponse`와 구별해 아래 원시 JSON을 사용한다. 모든 업무 연산은 READ-only이며 HTTP POST는 복합 조회 입력 전달을 위한 것이다.

| Method / 신규 경로 | `sub`,`dlg` 외 요청 필드 | 대응 Gateway Port / 필수 Source 권한 |
|---|---|---|
| `POST /internal/ai/v1/source-authorizations` | `requestedPermCodes: string[]` | `authorize` / 요청 권한과 현재 grant 교집합 |
| `POST /internal/ai/v1/eqp-snapshots` | `eqpId` | `getEqpSnapshot` / `EQP_READ` |
| `POST /internal/ai/v1/active-work-lots` | `eqpId` | `getActiveWorkLots` / `AI_WORKLOT_READ` |
| `POST /internal/ai/v1/recent-events` | `eqpId`,`timeWindow` | `getRecentEvents` / `AI_EVENT_READ` |
| `POST /internal/ai/v1/dlq` | `eqpId`,`sourceId`,`timeWindow` | **4인자** `listDlq` / `DLQ_READ` |
| `POST /internal/ai/v1/comm-logs` | `eqpId`,`sourceId`,`timeWindow` | `getCommLogs` / `COMM_LOG_READ` |
| `POST /internal/ai/v1/models` | 선택 `modelId` | `readModel` / `MODEL_READ` |
| `POST /internal/ai/v1/resources/resolve` | `type`,`ref` | `resolveResource` / `EQP`는 `EQP_READ`, `MODEL`은 `MODEL_READ` |

- 내부 경로는 **별도 service 인증 SecurityFilterChain**으로 보호한다. Gateway→TC 전용 Bearer 비밀을 TC→Gateway 비밀과 분리하고 서버에서 검증한다. Cookie 인증과 Browser 호출은 허용하지 않는다. CSRF 제외는 이 service 인증 경로에만 적용하며 `/api/ai/**`의 Cookie/CSRF를 유지한다. `sub`는 TC의 양수 long 십진 문자열, `dlg`는 현행 `[A-Za-z0-9_-]{22,128}`이며 TC Redis 결속의 소유자와 일치해야 한다. `sys` 입력은 받지 않고 TC 고정이다. 모든 READ는 인가 API를 앞서 호출했더라도 §5.8을 다시 적용한다.
- service chain과 AI Cookie 경계는 기존 `UiSecurityConfig`의 anyRequest URL 인가, 명시 import된 `UiTokenAuthenticationFilter`의 servlet/security 등록 범위까지 대조해 배선한다. 기존 Cookie 필터의 예외→401 처리나 ASYNC 인증 재사용이 service 경로/AI 최신 검사보다 먼저 적용되지 않도록 검증한다. chain 하나를 추가했다는 이유만으로 기존 필터가 배제되었다고 가정하지 않는다.
- 권한은 위 operation+HTTP method의 고정 매핑으로 판정하며 내부 POST를 기존 업무 쓰기 권한으로 해석하지 않는다. Source URI/permCode 매핑 정본은 Gateway `modules/apps/ai-gateway-app/src/main/resources/config/source-permissions/tc-v1.json`이다. `mockOnly=true`는 실제 연동 가능성의 증거가 아니며, 실제 Adapter·grant·Source capability 검증 후 별도 Gateway 변경으로 전환한다. manifest 밖 임의 Source/URL/권한을 요청할 수 없다.
- `timeWindow`는 `{from,to}`의 offset 있는 ISO-8601이며 `from < to`, **반개구간 `[from,to)`**로 조회하고 Port `TimeWindow(startAt,endAt)`에 손실 없이 투영한다. 무제한 전체 조회는 금지한다. 기본 내부 응답 상한은 1 MiB/최대 100개 항목이며, 잘림은 `PARTIAL`과 누락 필드/구간으로 표현하고 EMPTY로 바꾸지 않는다. 관측 시각은 실제 원천 관측/DB 읽기 시각이다.
- Source 인가 HTTP 200은 `{allowed: string[], denied: string[], pdg: string, evaluatedAt: string}`다. 두 집합은 중복 없이 서로소이며 합집합이 요청 집합과 같아야 한다. `pdg=SHA256(UTF8(catalogRevision + "\n" + join(codepoint-sort(distinct(allowed)),"\n")))`이고 끝 개행을 추가하지 않는다. 빈 allowed도 허용한다. Gateway는 스키마·집합 분할·시각·digest 형식을 검증해 `Allowed(allowed,pdg)`로 투영하며, denied가 있다고 `DelegationInvalid`로 바꾸지 않는다. 기존 Mock의 `AuthorizationResponse`/`project()` 및 pdg 벡터를 호환성 기준으로 재사용하되 불완전 집합 분할도 거부한다.
- 내부 API의 401은 service 자격 실패, 403 `DELEGATION_INVALID`는 위임/세션 무효, 400은 입력 계약 오류, 503은 DB/Redis/인가 인프라 장애다. Gateway 인가 Adapter는 403만 `DelegationInvalid`로, 401/400/503·timeout·파싱/스키마 오류는 `Unavailable`로 변환하고 본문을 허용하지 않는다. 업무 Source의 현재 권한 거부는 HTTP 200 `status=DENIED`이며 data는 null이다. service 자격이 유효하기 전에는 업무 조회를 수행하지 않는다.
- 업무 READ HTTP 200의 공통 필드는 `status,observedAt,data,missingFields,warnings,retentionMeta{missingRanges,unverifiedRanges}`이며 `TcReadResult<T>`에 대응한다. 상태는 현행 `ResultStatus`를 그대로 쓰며 **성공은 SUCCESS**다. HIT로 바꾸지 않는다. `SUCCESS/PARTIAL`은 data가 필요하고 PARTIAL은 missingFields 또는 missingRanges가 비어 있지 않아야 한다. 장애는 `SOURCE_UNAVAILABLE`, 확인된 0건만 `EMPTY`; 알려지지 않은 enum·필드 타입·과도한 크기는 계약 오류다.
- `data`는 해당 Port record의 필드명을 그대로 쓴다. Snapshot은 `eqpId,connectionState,controlState,processState,equipmentState`; Work/LOT는 `state,activeLots[{workId,pjId,lotId}],currentLot`; 이벤트는 `[{eventId,occurredAt,eventType}]`; DLQ는 `items[{entryRef,occurredAt,messageType}],completeness`; 통신 로그는 `items[{occurredAt,direction,messageType}],completeness`; 모델은 `[{modelId,nodeId,nodeType,children}]`; 리소스는 `[{type,ref}]`다. Optional 부재는 JSON null, 빈 목록은 []다. DLQ/통신 로그 completeness는 각각 현행 `DlqCompletenessEvaluator.Observation`/`TrimCompletenessEvaluator.KeyObservation`의 필드·제약을 보존하고 검증하지 않은 retention 설정을 true로 채우지 않는다.
- Snapshot의 equipmentState는 `TcEqpState.eqpState`, connectionState는 최신 CONN `toState`, controlState는 실제 controlState에서 온다. 현행 DB 모델에 독립 processState가 없으므로 null+`missingFields=["processState"]`인 PARTIAL로 표현한다. desiredState로 실제 연결/장비 상태를 대체하지 않는다. 원천 행 부재와 조회 장애는 구별한다.
- Work/PJ/LOT는 동일 설비의 `work_state=start AND end_time IS NULL`, 연결 PJ의 `processjob_state=start`, 실제 PJ-LOT 연결로 확인된 튜플만 활성 후보로 인정한다. 이는 Business `WorkStateTransitionSupport`의 start/end/completed/closed 전이와 대조한 기준이다. 후보가 정확히 1개면 ACTIVE+currentLot, 2개 이상이면 MULTI+currentLot=null이다. 전체 후보를 확인하지 못했거나 created/미등록 상태·연결 누락 때문에 현재 LOT를 결정할 수 없으면 UNKNOWN+빈 activeLots+누락 사유로 반환하며 임의 최신 LOT를 고르지 않는다. 확인된 모든 작업이 end/completed/closed이고 활성 후보가 없을 때만 ENDED다. 미확인/복수/종료 경로를 실제 fixture로 검증한다.
- 이벤트 `eventId=stateHistKey`의 십진 문자열, `occurredAt=changedAt`, `eventType=stateType`로 상태 이력을 투영한다. 확인된 OPER/CONN 이력 범위만 지원한다고 capability에 표시한다. 별도 알람/오류 이벤트 원천을 확인하지 못하면 그 범위를 조회했다고 주장하지 않는다. DLQ는 eqpId의 실제 원천 귀속을 확인해 필터하고, 임의 최신 100개를 먼저 읽은 뒤 설비별 완전 조회처럼 반환하지 않는다.
- 없는 기존 조회는 TC AI 전용 Core Port와 DB/Redis Adapter의 읽기 전용 조회로 추가한다. 공통 Store/업무 Mutation을 수정하거나 호출하지 않는다. 조회 기능·retention 증거가 없는 Source는 `SOURCE_UNAVAILABLE`로 차단하고 계약/실제 Source 수용 Gate를 통과시키지 않는다. 이 문서가 실제 데이터·배포 가용성을 만들어내지는 않는다.
- 재사용 한계: 현재 `CommLogRedisQueryAdapter`는 예외를 빈 목록으로, `GatewayDlqRedisService`/`BusinessDlqRedisService`는 SCAN 실패를 빈 목록·개별 역직렬화 실패를 건너뛰기로 처리한다. 이 반환값만 감싸서는 EMPTY/장애/부분 조회를 판별할 수 없다. AI 전용 읽기 경계에서 실패·누락·trim/TTL 증거를 보존해야 하며 일반 업무 API의 기존 정책을 이 단계에서 바꾸지는 않는다. manifest의 mockOnly=false도 실제 TC HTTP 연결의 증거가 아니다.

### 5.8 AI 최신 접근 판정과 공유 전달 게이트

**정본은 현재 TC DB의 세션 revoked/expiresAt·계정 ACTIVE·그룹/permission grant·활성 API catalog이며 Redis principal은 허용의 정본이 아니다.** AI 전용 최신 접근 Port를 추가해 매 JWS 발급, Public 조회/쓰기, 내부 Source 호출, SSE 이벤트/heartbeat에 이 조건을 검사한다. **2026-11-12 코드 근거로 정정:** `UiAuthSessionDbMapper.toStoreCommand()`가 원본을 SHA-256 hex로 변환하고 `JpaSessionPort`도 같은 지문으로 조회·폐기한다. 따라서 `sessionRef=UiAuthTokenFingerprint.sha256Hex(cookieToken)`을 기존 `tc_ui_auth_session.token` PK와 직접 비교한다. DB 열을 다시 해시하지 않고 원본 token이 DB에 있다고 가정하지 않는다. 새 지문 인덱스·전체 테이블 스캔 없이 UI DB Adapter의 전용 최신 조회를 추가하며 공통 인증 DTO/Store를 변경하지 않는다. 원본·지문·파라미터·개인키 로그는 금지한다.

- AI `/api/ai/**`용 Cookie 인증 경로는 최초 필터부터 이 판정을 사용하고, 기존 필터의 Redis 히트/ASYNC 인증 재사용만으로 허용하지 않는다. 유효하지 않은 세션은 401, 현재 사용권/Source 권한 부족은 403 또는 메시지 access=DENIED, 인프라/조회 불명은 503 또는 access=UNAVAILABLE다. 필터/URL 인가 캐시 단계에서 장애를 401/403으로 먼저 소거하지 않게 적용 범위를 명시적으로 분리한다. 일반 업무 API의 기존 응답 정책은 유지한다.
- **신규** DB 공유 게이트 `tc_ai_access_gate`를 둔다. 카탈로그 공통 행과 사용자 행을 고정 순서로 잠그며, AI 전송/인가의 공유 잠금과 logout·사용자 비활성·grant 변경·catalog 변경의 배타 잠금이 동일 DB에서 순서를 갖는다. 필요한 행이 없거나 잠금/revision 확인에 실패하면 503으로 차단하며 무잠금 허용하지 않는다. 변경 경로는 기존 Auth/사용자·그룹·권한 관리 흐름에 필요한 게이트 참여만 추가하고, 변경 commit 이후 시작되는 AI 전달이 옛 grant로 통과하지 않도록 한다. 영향을 받는 사용자 집합이 불명인 변경은 공통 catalog 배타 잠금으로 직렬화한다. 관리 SQL 등 게이트를 우회하는 권한 변경 작업 중에는 AI를 비활성화한다.
- 매 전달은 게이트 공유 잠금 아래 최신 세션/계정/grant/URL method를 다시 읽고, 권한 결과/응답 revision을 검증한 뒤 송신을 시작한다. SSE 한 이벤트/heartbeat의 write+flush까지 짧은 임계구역으로 결속하고 느린 연결은 bounded write timeout으로 닫는다. 게이트를 스트림 수명 전체나 LLM/Source 원격 대기 동안 잡지 않는다. 원격 접근 확인 중 잠금을 풀었다면 송신 전에 revision을 다시 확인하며 달라졌으면 결과를 폐기·재검증한다. 이미 전송을 시작한 바이트를 소급 회수한다고 주장하지 않는다.
- 로그아웃의 성공 경계는 게이트 배타 잠금 아래 DB revoke commit이다. Redis 위임 삭제가 실패해도 이후 Source/SSE는 DB 세션 검사에서 거부한다. 캐시 evict 실패는 경보/재정리하되 유효 세션으로 fallback하지 않는다. DB revoke 자체 실패는 성공 logout으로 응답하지 않고 AI는 fail-closed다. 세션별 SSE 종료 알림은 보조 수단이며 알림 유실도 다음 write/heartbeat의 DB 검사를 우회하지 못한다.
- JWS의 perm·pdg는 위 최신 조회와 같은 catalogRevision에서 계산한다. 인가/문서 접근의 ALLOW를 애플리케이션/브라우저에 장기 캐시하지 않는다. Front AI 조회는 `staleTime=0`, 패널/대화 진입 시 재조회하며 서버 401/403/UNAVAILABLE 시 기존 표시를 폐기한다. 새 GET/Export/인용 응답은 현재 접근을 다시 확인하고 `Cache-Control: no-store`를 사용한다.
- 이 게이트·Port는 현행 구현에 존재한다고 가정하지 않는다. 모든 관련 변경 경로의 참여 및 다중 Pod의 회수-before-send/send-before-회수 순서를 증명하기 전 `TC-AI-SECURITY`는 NOT_RUN/BLOCKED다. 요청·본문·Source·위임 비밀을 게이트/이벤트 행에 기록하지 않는다.

현행 `LogoutUseCase`는 DB revoke 후 Redis evict 실패를 별도 관측하며, Front `AppShellPage`/`AppTopBar`는 서버 logout 실패에도 로컬 정리·로그인 이동을 수행한다. **로컬 화면 정리를 DB revoke 성공의 증거로 삼지 않는다.** AI 스트림/입력/Query 정리는 이 공통 finally 경로와 401·계정 전환에 연결하고, 늦게 도착한 응답/재연결 콜백은 사용자·세션 generation 확인 후 폐기한다. AI 전용 경계는 기존 token prefix 로그와 예외 객체를 재사용해 지문/자격을 남기지 않는다.

---

## 6. 기존 DB를 재사용하는 영구 스키마 계약

### 6.1 schema·키·수명 원칙

**DB=nori_ai_gateway, 신규 schema=chat, 연결=기존 Gateway DataSource, 이력=기존 gw_flyway**다. gw·kn·gw_flyway 이름/기존 V 파일·기존 계정 정책은 유지한다. 신규 chat migration 번호는 후속 Plan에서 실제 최신 이력과 기존 phase 예약을 대조해 정하며 여기서 V8/V10 등 번호를 선점하지 않는다. Gateway 앱의 `spring.flyway.enabled=false`와 외부 Migration 조립을 유지한다.

- 영구 부모는 chat.conversation이다. **chat에서 gw/kn로 향하는 FK는 0개**다. runtime ID는 text 상관값으로만 보존한다. purge 뒤에도 식별자·멱등성·표시 결과가 남고, runtime 행이 있다고 가정한 JOIN을 영구 조회의 필수 조건으로 만들지 않는다.
- chat 내부는 owner_system/owner_actor_ref/conversation_id를 포함한 Composite FK로 다른 시스템·actor·대화 연결을 DB에서도 거부한다. TC 사용자 PK를 BIGINT 외부 FK로 저장하지 않고 공통 actor_ref 문자열을 쓴다.
- conversation/execution/message/result/binding/system은 TTL/expires_at 자동 삭제 없음. 진행 이벤트만 별도 30일 보존이 가능하며 이벤트 정리가 영구 부모/결과를 삭제하지 않는다. 명시적 삭제 기능은 P0 비범위이며 기본 FK는 RESTRICT/NO ACTION이다.
- 본문은 마스킹된 USER text와 검증된 ASSISTANT result로 역할별 한 곳씩 저장한다. runtime의 기존 단기 저장은 호환을 위해 유지하되 다른 시스템 DB로 영구 복제하지 않는다. vector/chunk·원시 Source·Prompt·모델 사고 내용은 chat으로 복제하지 않는다.

### 6.2 테이블 책임

| 테이블 | 책임 | 핵심 제약/조회 |
|---|---|---|
| `chat.system` | 공통 system code·chat 활성 상태·계약 버전 | code PK. TC/HRM/MES/WM 초기 DISABLED. 자격/endpoint 없음 |
| `chat.conversation` | 영구 사용자 스레드·제목·다음 turn_seq | owner+clientConversationId 유니크, 소유자별 활동 시각 keyset |
| `chat.execution` | 논리 질문·영구 멱등 키·확정 입력·실행/보관 요약 | owner+clientMessageId 유니크, 대화당 closed_at NULL 1개, runtime_job_id 비 NULL 유니크 |
| `chat.message` | USER effectiveQuery 또는 ASSISTANT 결과 식별 메타데이터 | 실행당 role 1개. USER text만 본문, ASSISTANT 본문은 result 한 곳 |
| `chat.result` | 영구 표시 payload·현재 접근 재검증용 result_source_deps | ASSISTANT 메시지/실행당 1개. 현재 문서에 대한 삭제 방해 FK 없음 |
| `chat.runtime_binding` | 영구 대화↔여러 30일 runtime Conversation 연결 이력 | 현재 binding 대화당 1개. runtime ID는 FK 없이 보존 |
| `chat.execution_event` | 공통 SSE 진행 상태와 durable event ID | 실행별 cursor, 질문/답변/자격 없는 상태 메타데이터. 30일 후 gap은 GET으로 회복 |

### 6.3 DDL 설계 계약

아래는 **설계 검증용 DDL**이며 실제 Flyway 파일/운영 적용물이 아니다. UUID와 시각은 서버가 부여한다. API/저장소 소유 검사와 schema/enum 검증은 DB 제약을 보완하며 대체하지 않는다. request_message의 순환 FK는 초기 부모/USER 원자 생성을 위해 명시적으로 지연한다. status/result enum은 현행 Core 정의와 계약 테스트로 대조하며 CHECK는 닫힌 최소 집합을 갖는다.

```sql
CREATE SCHEMA chat;

CREATE TABLE chat.system (
    code text PRIMARY KEY CHECK (code ~ '^[A-Z][A-Z0-9_]{1,31}$'),
    status text NOT NULL CHECK (status IN ('DISABLED','ENABLED')),
    contract_version text NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE chat.conversation (
    id uuid PRIMARY KEY,
    owner_system text NOT NULL REFERENCES chat.system(code) ON DELETE RESTRICT,
    owner_actor_ref varchar(64) NOT NULL CHECK (owner_actor_ref ~ '^[A-Za-z0-9_-]{1,64}$'),
    client_conversation_id uuid NOT NULL,
    title varchar(60) NOT NULL CHECK (length(btrim(title)) > 0),
    next_turn_seq bigint NOT NULL DEFAULT 1 CHECK (next_turn_seq > 0),
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    last_activity_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0 CHECK (version >= 0),
    UNIQUE (id, owner_system, owner_actor_ref),
    UNIQUE (owner_system, owner_actor_ref, client_conversation_id)
);
CREATE INDEX chat_conversation_owner_page_idx
    ON chat.conversation(owner_system, owner_actor_ref, last_activity_at DESC, id DESC);

CREATE TABLE chat.execution (
    id uuid PRIMARY KEY,
    conversation_id uuid NOT NULL,
    owner_system text NOT NULL,
    owner_actor_ref varchar(64) NOT NULL,
    turn_seq bigint NOT NULL CHECK (turn_seq > 0),
    client_message_id uuid NOT NULL,
    request_message_id uuid NOT NULL,
    request_role text NOT NULL DEFAULT 'USER' CHECK (request_role = 'USER'),
    client_input_sha256 bytea NOT NULL CHECK (octet_length(client_input_sha256) = 32),
    request_fields jsonb NOT NULL CHECK (jsonb_typeof(request_fields) = 'object'),
    sanitizer_version text NOT NULL,
    masking_applied boolean NOT NULL,
    context_snapshot jsonb NOT NULL CHECK (jsonb_typeof(context_snapshot) = 'object' AND octet_length(context_snapshot::text) <= 1048576),
    resolved_time_window jsonb CHECK (resolved_time_window IS NULL OR jsonb_typeof(resolved_time_window) = 'object'),
    runtime_job_id text NOT NULL CHECK (length(btrim(runtime_job_id)) > 0),
    runtime_conversation_id text NOT NULL CHECK (length(btrim(runtime_conversation_id)) > 0),
    runtime_request_message_id text NOT NULL UNIQUE CHECK (length(btrim(runtime_request_message_id)) > 0),
    gateway_job_status text NOT NULL CHECK (gateway_job_status IN ('QUEUED','RUNNING','SUCCEEDED','PARTIAL','FAILED','CANCELLED','TIMED_OUT')),
    terminal_error_code text,
    archive_status text NOT NULL CHECK (archive_status IN ('PENDING','ARCHIVED','STATUS_ONLY')),
    cancel_requested boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL,
    gateway_terminal_at timestamptz,
    closed_at timestamptz,
    version bigint NOT NULL DEFAULT 0 CHECK (version >= 0),
    UNIQUE (id, conversation_id, owner_system, owner_actor_ref, turn_seq),
    UNIQUE (owner_system, owner_actor_ref, client_message_id),
    UNIQUE (conversation_id, turn_seq),
    FOREIGN KEY (conversation_id, owner_system, owner_actor_ref)
        REFERENCES chat.conversation(id, owner_system, owner_actor_ref) ON DELETE RESTRICT,
    CHECK (jsonb_typeof(request_fields) = 'object' AND octet_length(request_fields::text) <= 1048576),
    CHECK ((closed_at IS NULL AND gateway_job_status IN ('QUEUED','RUNNING')
            AND archive_status = 'PENDING' AND gateway_terminal_at IS NULL AND terminal_error_code IS NULL)
        OR (closed_at IS NOT NULL AND gateway_job_status IN ('SUCCEEDED','PARTIAL','FAILED','CANCELLED','TIMED_OUT')
            AND archive_status IN ('ARCHIVED','STATUS_ONLY') AND gateway_terminal_at IS NOT NULL)),
    CHECK (gateway_terminal_at IS NULL OR gateway_terminal_at >= created_at),
    CHECK (closed_at IS NULL OR closed_at >= gateway_terminal_at)
);
CREATE UNIQUE INDEX chat_execution_active_idx
    ON chat.execution(owner_system, owner_actor_ref, conversation_id) WHERE closed_at IS NULL;
CREATE UNIQUE INDEX chat_execution_runtime_job_idx
    ON chat.execution(runtime_job_id) WHERE runtime_job_id IS NOT NULL;
CREATE INDEX chat_execution_open_idx ON chat.execution(created_at, id) WHERE closed_at IS NULL;

CREATE TABLE chat.message (
    id uuid PRIMARY KEY,
    execution_id uuid NOT NULL,
    conversation_id uuid NOT NULL,
    owner_system text NOT NULL,
    owner_actor_ref varchar(64) NOT NULL,
    turn_seq bigint NOT NULL,
    role text NOT NULL CHECK (role IN ('USER','ASSISTANT')),
    role_order smallint GENERATED ALWAYS AS (CASE WHEN role = 'USER' THEN 0 ELSE 1 END) STORED,
    text_masked text,
    created_at timestamptz NOT NULL,
    UNIQUE (execution_id, role),
    UNIQUE (id, execution_id, conversation_id, owner_system, owner_actor_ref, turn_seq, role),
    FOREIGN KEY (execution_id, conversation_id, owner_system, owner_actor_ref, turn_seq)
        REFERENCES chat.execution(id, conversation_id, owner_system, owner_actor_ref, turn_seq) ON DELETE RESTRICT,
    CHECK ((role = 'USER' AND text_masked IS NOT NULL AND length(btrim(text_masked)) > 0 AND octet_length(text_masked) <= 65536)
        OR (role = 'ASSISTANT' AND text_masked IS NULL))
);
CREATE INDEX chat_message_page_idx
    ON chat.message(owner_system, owner_actor_ref, conversation_id, turn_seq DESC, role_order DESC, id DESC);
ALTER TABLE chat.execution ADD CONSTRAINT chat_execution_user_fk
    FOREIGN KEY (request_message_id, id, conversation_id, owner_system, owner_actor_ref, turn_seq, request_role)
    REFERENCES chat.message(id, execution_id, conversation_id, owner_system, owner_actor_ref, turn_seq, role)
    DEFERRABLE INITIALLY DEFERRED;

CREATE TABLE chat.result (
    message_id uuid PRIMARY KEY,
    execution_id uuid NOT NULL UNIQUE,
    conversation_id uuid NOT NULL,
    owner_system text NOT NULL,
    owner_actor_ref varchar(64) NOT NULL,
    turn_seq bigint NOT NULL,
    message_role text NOT NULL DEFAULT 'ASSISTANT' CHECK (message_role = 'ASSISTANT'),
    result_status text NOT NULL CHECK (result_status IN ('SUCCESS','EMPTY','PARTIAL','OUT_OF_RETENTION','STALE','CONFLICT','DENIED','UNSUPPORTED','SOURCE_UNAVAILABLE','TIMEOUT','FAILED','ACCEPTED_ASYNC')),
    error_code text,
    display_payload jsonb NOT NULL CHECK (jsonb_typeof(display_payload) = 'object' AND octet_length(display_payload::text) <= 1048576),
    clarification jsonb CHECK (clarification IS NULL OR jsonb_typeof(clarification) = 'object'),
    result_source_deps jsonb NOT NULL,
    evidence_refs jsonb NOT NULL CHECK (jsonb_typeof(evidence_refs) = 'array' AND octet_length(evidence_refs::text) <= 1048576),
    schema_version text NOT NULL,
    sanitizer_version text NOT NULL,
    created_at timestamptz NOT NULL,
    FOREIGN KEY (message_id, execution_id, conversation_id, owner_system, owner_actor_ref, turn_seq, message_role)
        REFERENCES chat.message(id, execution_id, conversation_id, owner_system, owner_actor_ref, turn_seq, role) ON DELETE RESTRICT,
    CHECK (jsonb_typeof(result_source_deps) = 'object'
        AND result_source_deps ? 'sources' AND result_source_deps ? 'knowledgeDocuments'
        AND jsonb_typeof(result_source_deps->'sources') = 'array'
        AND jsonb_typeof(result_source_deps->'knowledgeDocuments') = 'array')
);

CREATE TABLE chat.runtime_binding (
    id uuid PRIMARY KEY,
    conversation_id uuid NOT NULL,
    owner_system text NOT NULL,
    owner_actor_ref varchar(64) NOT NULL,
    runtime_conversation_id text NOT NULL UNIQUE,
    runtime_expires_at timestamptz NOT NULL,
    status text NOT NULL CHECK (status IN ('CURRENT','EXPIRED','RETIRED')),
    created_at timestamptz NOT NULL,
    retired_at timestamptz,
    UNIQUE (runtime_conversation_id, conversation_id, owner_system, owner_actor_ref),
    FOREIGN KEY (conversation_id, owner_system, owner_actor_ref)
        REFERENCES chat.conversation(id, owner_system, owner_actor_ref) ON DELETE RESTRICT,
    CHECK (runtime_expires_at > created_at),
    CHECK (retired_at IS NULL OR retired_at >= created_at),
    CHECK ((status = 'CURRENT' AND retired_at IS NULL) OR (status <> 'CURRENT' AND retired_at IS NOT NULL))
);
CREATE UNIQUE INDEX chat_binding_current_idx
    ON chat.runtime_binding(owner_system, owner_actor_ref, conversation_id) WHERE status = 'CURRENT';
ALTER TABLE chat.execution ADD CONSTRAINT chat_execution_binding_fk
    FOREIGN KEY (runtime_conversation_id, conversation_id, owner_system, owner_actor_ref)
    REFERENCES chat.runtime_binding(runtime_conversation_id, conversation_id, owner_system, owner_actor_ref)
    ON DELETE RESTRICT;

CREATE TABLE chat.execution_event (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    execution_id uuid NOT NULL,
    conversation_id uuid NOT NULL,
    owner_system text NOT NULL,
    owner_actor_ref varchar(64) NOT NULL,
    turn_seq bigint NOT NULL,
    event_type text NOT NULL CHECK (event_type IN ('chat.status','chat.terminal')),
    status text NOT NULL CHECK (status IN ('QUEUED','RUNNING','CANCEL_REQUESTED','SUCCEEDED','PARTIAL','FAILED','CANCELLED','TIMED_OUT')),
    created_at timestamptz NOT NULL,
    FOREIGN KEY (execution_id, conversation_id, owner_system, owner_actor_ref, turn_seq)
        REFERENCES chat.execution(id, conversation_id, owner_system, owner_actor_ref, turn_seq) ON DELETE RESTRICT,
    CHECK ((event_type = 'chat.status' AND status IN ('QUEUED','RUNNING','CANCEL_REQUESTED'))
        OR (event_type = 'chat.terminal' AND status IN ('SUCCEEDED','PARTIAL','FAILED','CANCELLED','TIMED_OUT')))
);
CREATE INDEX chat_event_execution_page_idx
    ON chat.execution_event(owner_system, owner_actor_ref, execution_id, id);
CREATE UNIQUE INDEX chat_event_terminal_once_idx
    ON chat.execution_event(execution_id) WHERE event_type = 'chat.terminal';
CREATE INDEX chat_event_retention_idx ON chat.execution_event(created_at, id);

INSERT INTO chat.system(code, status, contract_version, created_at) VALUES
    ('TC','DISABLED','1.0',CURRENT_TIMESTAMP),
    ('HRM','DISABLED','1.0',CURRENT_TIMESTAMP),
    ('MES','DISABLED','1.0',CURRENT_TIMESTAMP),
    ('WM','DISABLED','1.0',CURRENT_TIMESTAMP);
```

### 6.4 추가 원자·데이터 계약

DB의 Composite FK/유니크는 연결·중복을 보호한다. 서로 다른 테이블의 행 존재를 CHECK로 가장하지 않는다. **ARCHIVED인 실행은 정확히 하나의 ASSISTANT+result, STATUS_ONLY는 result 없음, QUEUED/RUNNING은 closed_at 없음, 모든 접수는 USER 1개**를 동일 tx의 use case에서 보장하고 통합 시험으로 확인한다. 임의 SQL/유지보수로 이 불변식을 우회하지 않는다.

result_source_deps는 기존 `{sources:[{sourceId,permCodes}],knowledgeDocuments:[{ownerSystem,sourceRef,revision}]}`를 검증·보관한다. source별 perm ANY, source 간 AND 및 문서별 현재 상태를 유지한다. legacy의 정확한 빈 `{}`는 신규 chat 저장 경계에서 명시적 두 빈 배열로 정규화하고, 그 외 잘못된 deps는 허용하지 않는다. schema_version과 sanitizer_version은 표시/해석 호환성을 위한 값이며 Browser가 선언할 수 없다.

모든 Source URI·Knowledge ownerSystem·payload의 Source/Resource 참조는 현재 boundSystem과 일치해야 한다. 같은 이름의 permCode가 있다는 이유로 다른 시스템 Source 의존을 허용하지 않는다. 파서의 중복 key·미지원 schema·잘못된 enum·Source/deps 필드 타입은 저장/표시/문맥에서 fail-closed이며 raw JSON을 그대로 Browser에 넘기지 않는다.

runtime ID 세 개는 신뢰된 같은 tx에서 얻은 값만 저장하며 owner/actor 일치를 repository가 확인한다. 외부 body의 ID를 소유 증거로 삼지 않는다. FK를 생략한 이유는 purge 독립성이지 결속 검사 생략이 아니다. 현재 binding과 execution의 runtime_conversation_id도 같은 대화/owner/actor인지 검증한다.

2026-11-12 보완: 외부 runtime FK 대신 **chat.execution→chat.runtime_binding의 내부 Composite FK**를 둬 과거 binding까지 영구 대화·owner·actor에 결속한다. 접수 commit 시 runtime ID 3개와 QUEUED 상태는 필수이며 PENDING/null runtime execution을 먼저 커밋하지 않는다. 같은 tx에서 gw Job의 conversation_id/actor_ref/request_message_id와 gw USER role·binding의 실제 expires_at을 대조하고, 질문이 저장된 runtime message와 영구 USER effectiveQuery 및 runtime request_input이 같은 확정 입력인지 검사한다. terminal 연결은 공개 요청 scope가 아닌 잠긴 gw Job의 신뢰된 owner/actor/ID를 사용하고 만료된 runtime에도 적용해야 한다. legacy의 미만료 전용 `find()`로 observer 결속을 찾으면 만료 종결이 누락되므로 사용할 수 없다.

`context_snapshot`은 서버가 선택한 영구 USER/ASSISTANT message ID·각 schema version·선택 당시 입력 문맥 메타데이터만 보관하며 과거 본문을 재복제하지 않는다. `resolved_time_window`는 실제 분류·시간창 해석 후 해당 실행에 한 번 확정하고 runtime 요청 메시지의 resolved_time_window와 같은 tx에 기록한다. null은 창이 확정되지 않았음을 뜻하며 과거 임의 nonnull 창을 검색해 채우지 않는다. 두 JSON의 필드 allowlist·참조 소유권·정렬·크기·시각 형식은 저장 경계에서 검사한다.

`request_fields`는 허용된 client context/resourceRefs/options의 부재/null/값을 보존하고 query 본문은 USER 한 곳에 둔다. 최초 서버 referenceTime은 context_snapshot에 별도 기록한다. client context 객체가 있으면 이 시각을 runtime context.referenceTime에 넣고, context가 부재/null이면 이를 보존하며 timezone을 추측해 객체를 합성하지 않는다. 상대 시각 해석에 필요한 timezone/창이 없으면 기존 확인 질문으로 처리한다. runtime snapshot은 USER·request_fields·서버 snapshot에서 결정론적으로 투영하고 재요청마다 새 현재 시각을 넣지 않는다.

`evidence_refs`는 영구 결과 안의 ref와 `{evidenceId,ownerSystem,sourceRef,chunkId,expectedRevision}`의 대응 목록이다. 실제 `TerminalSpec.StoredEvidence`에서 검증한 참조만 같은 종결 tx에 보관하고 raw Source/Prompt/본문은 넣지 않는다. 원문 재조회가 없는 근거는 chunkId/expectedRevision을 모두 null로 표시하며 재조회 가능한 Knowledge 근거는 둘 다 필수다. evidenceId는 실행 내 유일하고 owner/Source/deps·payload의 인용 ref가 일치해야 한다. 현재 문서 deps만으로는 evidenceId→chunk/revision 매핑이 복원되지 않으므로 gw.evidence에만 의존하지 않는다. 원문 수명 이후에도 이 참조는 보존하되 새 revision으로 대체하지 않는다.

활성/terminal·archive 상태 조합과 이벤트 type/status는 위 CHECK로 제한한다. ARCHIVED의 ASSISTANT/result 존재 및 payload·deps·Evidence 대응은 여러 테이블/JSON을 대조해야 하므로 use case와 통합 검증의 책임이다. terminal 이벤트 UNIQUE는 이벤트 보존 기간 내 추가 방어이며, 정리 후 종결 1회성은 영구 execution.closed_at의 조건부 전이로 보장한다. 영구 ID·client key·owner·turn·runtime binding/입력 digest는 접수 후 변경하지 않는다.

역할별 본문·요청 크기 상한은 위 DB cap과 Gateway 입력 계약 중 더 좁은 값을 사용한다. 초과 시 임의 절삭/요약하지 않고 접수 전 400 또는 결과 계약 오류로 처리한다. 제목은 첫 effectiveQuery의 마스킹된 60자 축약 또는 새 대화이며 LLM을 추가 호출하지 않는다.

### 6.5 페이지네이션·운영 인덱스

대화 목록은 소유자 범위의 `(last_activity_at DESC,id DESC)` keyset, 기본 20/최대 50이다. 메시지는 소유 대화의 `(turn_seq DESC,role_order DESC,id DESC)` keyset, 기본 30/최대 100이며 화면은 오래된 순서로 재정렬한다. 커서는 버전·정렬 키를 검증하고 다른 소유 범위로 사용할 수 없게 한다. 목록에 과거 답변/본문 미리보기를 포함하지 않는다.

선택한 메시지 페이지의 ASSISTANT만 result에 batch 결합하고, 권한/문서 의존을 중복 제거해 재검증한다. 전체 대화 전문·전체 runtime history를 목록마다 읽거나 N+1 원격 권한 호출을 만들지 않는다. 1000개 대화/충분한 메시지 fixture에서 실행 계획·정렬·상한을 확인한다. 작은 테이블에서 PostgreSQL이 Seq Scan을 선택하는 것만으로 실패 판정하지 않고, owner 조건·접근 행 수·확장 시 인덱스 가능성을 증명한다.

---

## 7. TC Public API와 공통 Gateway API

TC의 `/api/ai/**`는 기존 `ApiResponse<T>{success,data,errorCode,errorMsg}`를 사용한다. Gateway `/v1/chat/**`는 신규 공통 원시 JSON 계약이며 legacy `/v1/query/conversations/analyses`의 DTO·TTL을 바꾸지 않는다. 공개 conversationId/messageId/executionId는 모두 영구 chat UUID다. runtime ID는 서버 내부 상관관계로 제한하고 Browser가 직접 runtime 경로를 호출하게 하지 않는다.

| Method | TC Public | Gateway 공통 | 역할 |
|---|---|---|---|
| GET | `/api/ai/bootstrap` | `/v1/chat/bootstrap` | 기능/계약 가용성. Backend의 AI_CHAT_USE 선행 검사 |
| POST | `/api/ai/conversations` | `/v1/chat/conversations` | clientConversationId로 멱등 새 영구 대화, 201/기존 200 |
| GET | `/api/ai/conversations` | `/v1/chat/conversations` | 소유 대화 keyset 목록 |
| GET | `/api/ai/conversations/{id}` | `/v1/chat/conversations/{id}` | 영구 메타데이터/activeExecutionId |
| GET | `/api/ai/conversations/{id}/messages` | `/v1/chat/conversations/{id}/messages` | 현재 접근을 검사한 메시지 페이지 |
| POST | `/api/ai/conversations/{id}/messages` | `/v1/chat/conversations/{id}/messages` | clientMessageId로 원자 접수, 202 |
| GET | `/api/ai/executions/{id}` | `/v1/chat/executions/{id}` | 영구 실행 요약/closed 상태 |
| GET | `/api/ai/executions/by-client-message/{clientMessageId}` | `/v1/chat/executions/by-client-message/{clientMessageId}` | owner+clientMessageId로 접수 응답 유실 복구. 새 Job/위임 생성 없음 |
| GET | `/api/ai/executions/{id}/events` | `/v1/chat/executions/{id}/events` | 공통 chat event ID 기반 SSE |
| POST | `/api/ai/executions/{id}/cancel` | `/v1/chat/executions/{id}/cancel` | 취소 요청 202, 완료 경쟁 보존 |
| GET | `/api/ai/executions/{id}/evidence/{ref}` | `/v1/chat/executions/{id}/evidence/{ref}` | 현재 접근/원본 수명 검사 후 masked source |
| GET | `/api/ai/executions/{id}/blocks/{blockId}/export` | `/v1/chat/executions/{id}/blocks/{blockId}/export` | 영구 result의 현재 허용된 block |

새 대화 요청은 `{clientConversationId: UUID}`만 허용하며 system/actor/owner를 받지 않는다. 질문 요청 예시는 다음과 같다.

```json
{
  "clientMessageId": "718bf682-a830-4f4d-bcd4-d04bbf6a2b56",
  "query": "EQP_001 상태와 최근 오류를 알려줘.",
  "sanitizerVersion": "mask-v1",
  "maskingApplied": false,
  "context": {"timezone": "Asia/Seoul"},
  "resourceRefs": [{"type": "EQP", "ref": "EQP_001"}]
}
```

위 질문 예시는 **TC Backend→Gateway** body다. Browser→TC는 `clientMessageId,query,context,resourceRefs,options`만 허용하고 sanitizerVersion/maskingApplied는 Backend가 산출한다. TC Front는 원문 query를 Backend에 보내고 Backend가 위 query를 effectiveQuery로 치환한다. 허용된 timezone과 options allowTools/allowKnowledge의 부재/null/boolean은 보존하되, **referenceTime은 Gateway가 최초 접수 때 한 번 확정**한다. Browser/Backend가 현재 시각을 매 재요청에 바꿔 넣지 않는다. Gateway runtime body는 이 확정 입력에서 생성한다.

신규 chat 입력의 context는 부재/null 또는 `{timezone: IANA zone, timeWindow?: null|{from,to}}`다. timezone은 context 객체가 있으면 필수이고 timeWindow는 offset 있는 시각과 from<to를 검증한다. resourceRefs/options도 부재/null을 보존하며 내부 필드는 기존 ResourceRef/boolean 계약을 따른다. referenceTime·sourceVersions·historyContext·sys/actor/owner·임의 DTO 필드는 거부한다. 현행 `ApiRequestInput.fields()`와 `JobRequestInput`은 context 객체가 있으면 referenceTime을 요구하므로 새 body를 그대로 넘길 수 없다. **신규 chat ingress가 referenceTime을 확정·주입한 runtime snapshot**을 기존 reader에 전달하며 legacy 입력 검증은 유지한다.

`maskingApplied`는 최초 Backend 입력과 effectiveQuery가 달라졌는지를 뜻한다. 기존 `ContentSanitizer.Sanitized.applied`는 내용 변경 여부와 무관하게 true이므로 그대로 매핑하지 않는다. 재요청에서 이미 마스킹된 query를 보내도 최초 저장 플래그를 false로 덮어쓰지 않는다. 저장 키는 body의 clientConversationId/clientMessageId이며, Front Axios 인터셉터가 매 호출 만드는 Idempotency-Key는 이 키를 대체하지 않는다. AI 호출에서는 해당 body UUID와 같은 헤더를 명시하고 Gateway chat은 헤더가 있으면 일치 여부를 검사한다.

Gateway 202의 필수 필드는 `{conversationId,executionId,userMessageId,effectiveQuery,maskingApplied,gatewayJobStatus,archiveStatus,terminal}`이고, TC는 그대로 data에 넣는다. 예시:

```json
{
  "success": true,
  "data": {
    "conversationId": "9dfd0791-c12c-4417-a751-12952d65032e",
    "executionId": "c3d5189e-7161-4ca5-9c33-366f37dfcfa8",
    "userMessageId": "c1bed0f7-47f9-425e-8185-157037e5ff1d",
    "effectiveQuery": "EQP_001 상태와 최근 오류를 알려줘.",
    "maskingApplied": false,
    "gatewayJobStatus": "QUEUED",
    "archiveStatus": "PENDING",
    "terminal": false
  },
  "errorCode": null,
  "errorMsg": null
}
```

같은 clientMessageId/같은 유효 입력이면 기존 execution/message를 반환한다. 종료·31일 경과·runtime purge 뒤에도 동일한 영구 ID와 현재 요약을 반환하며 새 Job을 만들지 않는다. 접수 202와 질문 결과 성공을 구별한다. 메시지 DTO는 §7.1을 따르며 ASSISTANT access=DENIED/UNAVAILABLE이면 result/text/blocks/evidence를 생략한다. Source 명칭/개수·문서 제목도 노출하지 않는다.

오류는 400 AI_INVALID_REQUEST, 422 AI_SANITIZATION_FAILED, 401 세션 무효, 403 AI_PERMISSION_DENIED, 타인/부재 모두 404, 같은 대화 동시 질문 409 AI_CONVERSATION_BUSY, 같은 멱등 키의 다른 입력 409 AI_IDEMPOTENCY_CONFLICT, 큐/가용성 503 AI_GATEWAY_UNAVAILABLE(또는 BUSY)+Retry-After, upstream 계약 502/503으로 정규화한다. Body/SQL/trace/자격 원문을 오류에 포함하지 않는다. runtime purge로 영구 대화 GET을 404로 바꾸지 않는다.

### 7.1 신규 응답 DTO·오류 경계

이 절은 구현할 공통 계약이며 현행 Controller의 존재를 뜻하지 않는다. 모든 식별자는 UUID 문자열, 시각은 offset 있는 ISO-8601, event ID는 정밀도 손실 없는 십진 **문자열**이다. Gateway DTO와 TC data의 필드·enum·null 의미는 같으며 SSE·export를 JSON ApiResponse로 감싸지 않는다.

| 응답 | 필수 계약 |
|---|---|
| bootstrap 200 | `enabled:boolean, contractVersion:string, ownerSystem:string, actorRef:string`. owner/actor는 검증 결과만 사용. DISABLED도 bootstrap은 enabled=false를 반환하되 다른 chat 데이터 API는 503 AI_FEATURE_DISABLED로 차단 |
| 대화 생성 201/재요청 200·상세 200 | `conversationId,clientConversationId,title,createdAt,updatedAt,lastActivityAt,activeExecutionId:UUID|null,version:정수`. 입력의 clientConversationId와 최초 대화 생성 결과 유지 |
| 대화 목록 200 | `{items:[대화 DTO],nextCursor:string|null}`. 본문 미리보기 없음 |
| 메시지 목록 200 | `{items:[{messageId,executionId,clientMessageId,turnSeq:십진문자열,role,state,access,createdAt,text?,result?}],nextCursor:string|null}`. USER의 text=effectiveQuery. ASSISTANT는 검증된 `result{status,errorCode,payload,clarification}`에 blocks 포함. 별도 text/blocks 복제 없음 |
| 접수 202·execution GET/키 조회 200 | §7의 접수 필드와 `clientMessageId,sanitizerVersion,cancelRequested,errorCode:null|string,createdAt,gatewayTerminalAt:null|시각,closedAt:null|시각,version`. 키 조회는 동일 owner/actor에만 허용하고 같은 복구 DTO를 반환하며 미존재/타인은 동일 404 |
| cancel | 진행 중 최초/반복 요청은 202와 execution DTO. 이미 terminal이면 200과 저장된 최종 DTO. terminal 경합의 승자 결과를 보존하며 runtime purge 후에도 새 Job 생성 없이 200 |
| Evidence 원문 | `{maskedText:string}` 200. 현재 접근 DENIED는 본문 없는 403 AI_PERMISSION_DENIED, 확인된 원문/해당 revision 소멸은 410 AI_EVIDENCE_UNAVAILABLE, 상태 불명/장애는 503 AI_GATEWAY_UNAVAILABLE. ref/실행의 미존재·타인은 404 |
| block export | 현재 인가된 저장 block JSON 200(`application/json`, no-store). 미존재/중복 blockId는 404, 접근 거부 403, 검사 장애 503. 원문 Evidence의 수명과 구별 |

USER state는 ACCEPTED, ASSISTANT state는 해당 gatewayJobStatus다. STATUS_ONLY 종결은 ASSISTANT/result를 합성하지 않고 execution 상태 UI로 보여준다. AVAILABLE은 현재 접근 허용을 뜻하며 실행 결과 SUCCESS와 구별한다. DENIED/UNAVAILABLE인 ASSISTANT는 result/text/blocks/evidence·Source/문서 상세를 모두 생략한다. cursor/limit 입력 오류는 400이며 소유권 없는 부모는 페이지가 비었다고 200을 반환하지 않는다.

Gateway 신규 chat 도메인 오류는 `{errorCode,errorMsg}`이며 TC는 `ApiResponse.error`로 변환한다. Gateway의 기존 인증 필터가 내는 `{message,requestId}`/빈 401·403·404, 기존 admission의 `{error}`도 동일 DTO라고 가정하지 않는다. TC→Gateway 서비스/JWS 실패는 Browser 세션 401로 바꾸지 않고 502 AI_GATEWAY_AUTH_FAILED로 변환·관측하며 실제 TC 세션 무효만 401이다. upstream 입력/키 충돌 400/409·가용성 503은 허용 오류코드로 매핑하고 미지원 enum/스키마·파싱 실패는 502 AI_GATEWAY_CONTRACT_INVALID다. 응답/commit 유실을 포함한 timeout은 503 AI_GATEWAY_UNAVAILABLE이며 새 키로 자동 접수하지 않는다. 큐 포화는 503 AI_GATEWAY_BUSY다. upstream Retry-After는 유효한 제한값만 전달한다.

Gateway 인증 거절 감사 실패는 현행 계약대로 503이다. TC도 이를 사용자 권한 없음/세션 만료로 축약하지 않는다. SSE header 송신 전 오류는 위 HTTP 계약을 쓰며 송신 후 오류는 JSON을 스트림에 섞지 않고 연결을 닫아 GET으로 재판정한다. 오류 안내 문구는 한국어이며 Source/SQL/자격/응답 원문은 포함하지 않는다.

---

## 8. 원자 접수·종결·재연결

### 8.1 하나의 DB 트랜잭션으로 질문 접수

공통 접수는 service/JWS/sys 활성·소유권·유효 입력 검사 후 **chat.execution의 영구 멱등 조회를 먼저** 한다. 같은 owner+clientMessageId의 digest가 같으면 기존 결과를 반환하고 다른 입력이면 409다. digest는 effectiveQuery·영구 conversationId·사용자 선택 timezone/resourceRefs/options의 부재/null/값을 정규화한 SHA-256이며, 서버가 만드는 referenceTime/runtime ID/JWS/dlg는 이 client digest에 넣지 않는다. 객체 key는 재귀 정렬한 compact UTF-8 JSON, 배열 순서는 유지한다. 기존 sanitizerVersion으로 비교하며 지원 불가 버전은 명시적으로 차단한다.

신규 접수는 같은 DB tx에서 대화 turn_seq/활성 실행을 잠그고, 현재 binding 검사·필요한 runtime Conversation 생성, chat USER/execution·확정 request_fields, gw message/job/event·기존 감사·binding을 함께 기록한다. Gateway readiness/queue 거부, FK/감사/질문 저장 실패는 모두 rollback하며 202를 반환하지 않는다. 앞서 별도 생성한 빈 영구 대화는 유지할 수 있다. 영구 접수 후 runtime Job을 나중에 보내는 TC Outbox/분산 보상 프로토콜은 없다.

현행 JobAdmissionService.accept는 자체 tx+외부 tx 거부 계약이므로 바깥에서 감싸 호출하지 않는다. 후속 Plan은 재사용 가능한 접수 저장 동작과 단일 tx 소유 지점을 분리하되 legacy API의 멱등/감사/queue 동작을 유지해야 한다. 모든 chat/runtime 경로의 잠금 순서를 일관되게 적용하고, 네트워크/LLM/TC 권한 원격 호출을 DB 잠금 안에서 기다리지 않는다. commit 뒤에만 dispatcher/브라우저에 수락을 알린다.

2026-11-12 보완: 현행 접수의 `READ_COMMITTED`·`gw.queue_control` 잠금과 **잠금 직후 멱등 재조회 → readiness/추론 상태 → 큐 깊이 검사** 순서를 유지한다. chat의 선조회 miss도 신규 확정이 아니며 같은 전역 큐 잠금 아래 영구 키를 다시 조회해야 한다. 같은 키의 다른 대화/입력은 active/busy보다 먼저 409로 판정하고, 같은 키의 같은 입력은 큐 포화/추론 readiness 실패·runtime 삭제에도 기존 접수로 수렴한다(현재 신원·AI 사용권·system Gate는 우회하지 않음). UNIQUE 위반 이후에는 실패 tx를 끝낸 다음 영구 키로 재조회하며 rollback-only tx에서 계속 쓰지 않는다.

동일 clientMessageId 재요청은 최초 sanitizerVersion의 유효 입력을 비교하고 최초 referenceTime/context snapshot·maskingApplied·runtime binding·dlg를 변경하지 않는다. Backend가 해당 sanitizer 버전을 지원하지 못하면 명시적 422로 거부하며 임의 새 버전으로 재접수하지 않는다. owner+clientMessageId 조회 API는 query 없이 기존 수락 상태를 복구하는 경로다. 새로고침 후 원문을 영속 캐시에서 복원하지 않고 대화/메시지의 clientMessageId·executionId로 재부착한다.

현재 terminal은 gw Job/Conversation을 먼저 잠근 뒤 observer를 호출한다. 신규 접수/취소/전이에서 chat 부모를 잠근 채 같은 runtime 행을 기다리면 terminal과 잠금 순서가 뒤집힐 수 있다. 공유 runtime 행→chat 부모/실행의 순서를 맞추고, 접수의 queue_control 선행 순서를 보존해야 한다. 사전 읽은 binding/대화 version은 잠금 후 재검사하며 변동 시 같은 입력으로 tx 재시도한다. 다중 Pod 동시 접수·terminal·cancel·purge의 교착/재시도 검증 없이 원자성을 충족했다고 판정하지 않는다.

### 8.2 원자 종결과 purge 독립성

같은 runtime 종결 tx에서 gw.result/assistant/Evidence/이벤트/감사와 **chat ASSISTANT/result/deps·execution 요약/closed_at·chat.terminal**을 함께 커밋한다. `JobTerminalCommitter`의 기존 tx/observer 또는 동등한 명시적 연결을 사용하고 afterCommit 비동기 복사를 보관 정본으로 삼지 않는다. chat 저장 실패면 runtime terminal도 rollback하고 정상 완료를 발행하지 않는다. legacy Job에 chat execution이 없으면 기존 종결을 유지한다.

`JobTerminalCommitter.commit()`도 외부 tx를 거부한다. `TerminalCommitObserver.beforeCommit(jobId)`는 Job UPDATE·result/event/audit 쓰기 **뒤, commit 전** 호출되므로 같은 manager/JDBC의 DB projection에 사용할 수 있지만, 이 인터페이스는 최종 TerminalSpec을 인자로 주지 않는다. observer는 같은 tx에 저장된 실제 상태·결과를 읽어야 하며 요청했던 결과가 취소/만료/시간초과 경쟁에서 승리했다고 추정하지 않는다. 원격 인가·문서 I/O·LLM 호출은 이 observer 안에서 하지 않는다. 일반 만료 경로는 `CommitData` 없이도 종결되므로 정상 답변용 callback에만 연결해서는 안 된다. beforeCommit 예외는 전체 rollback, afterCommit 예외는 이미 커밋된 결과의 rollback이 아니며 durable event/GET으로 회복한다.

실행 종료가 확인되고 결과가 없는 취소/실패/시간초과는 본문을 합성하지 않고 STATUS_ONLY+실제 terminal 상태·terminal_error_code로 원자 종결한다. 결과가 있으면 마스킹·스키마·deps 후조건이 검증된 표시 결과만 ARCHIVED로 저장한다. 보관 전 확정된 크기/스키마/마스킹 검증 실패는 안전한 계약 오류로 실패 종결하고 위험한 본문을 저장/재생성하지 않는다. DB I/O/commit 실패는 그와 구별해 전체 rollback·동일 Job 복구 대상으로 둔다. 접근권은 저장 사실과 별개로 **조회 때마다** 검사하므로 생성 당시 결과를 보관해도 현재 권한을 얻었다는 뜻이 아니다.

purge가 실행 Conversation을 지워도 chat FK/본문/멱등 키에 영향을 주지 않는다. 새 공통 접수의 활성 execution이 있는데 runtime Job이 없거나 terminal 불일치가 있으면 DB 정합성 장애로 경보·AI 접수를 차단하며 새 Job을 자동 생성하지 않는다. 소실된 결과를 LLM에 다시 물어 복구한 것처럼 표시하지 않는다.

| 실제 TerminalSpec 경로 | runtime 결과 | chat 종결 요구 |
|---|---|---|
| CANCELLED·EXPIRY_EDGE·RESTORE_EXPIRED | result 없음, 만료 원인은 외부 이벤트 suppressed | STATUS_ONLY, CANCELLED, ASSISTANT/result 없음. chat 메타 종결은 남기며 legacy 만료 이벤트를 외부에 재발행하지 않음 |
| QUEUE_TIMEOUT·WORKER_INTERRUPTED·JOB_TIMEOUT·START_GATE_* 및 restore 대응 | FAILED/TIMEOUT/DENIED/SOURCE_UNAVAILABLE result는 있으나 payload는 빈 객체이고 assistant가 없을 수 있음 | STATUS_ONLY, 실제 Job 상태/errorCode 보존. gw.result 행의 존재만으로 표시 답변을 합성하지 않음 |
| NORMAL의 검증된 답변·PARTIAL·clarification·안전한 결과 | 확정 payload/deps, 경로에 따라 CommitData 존재 | 표시 가능한 결과는 ARCHIVED+ASSISTANT/result 정확히 1개. status만 있고 표시 payload가 없으면 STATUS_ONLY |

보관 크기·스키마 검증은 terminal의 **첫 runtime 쓰기 전** 끝내야 한다. 검증 실패는 기존 허용 조합 `FAILED/FAILED/CONTRACT_INVALID`의 안전한 실패로 확정하고 STATUS_ONLY로 종결한다. beforeCommit DB 저장 실패를 CONTRACT_INVALID로 바꿔 별도 tx에 성공처럼 기록하지 않는다. 크기 초과를 발견한 observer가 이미 쓴 terminal 상태를 임의 변경해서도 안 된다.

### 8.2.1 진행 전이·재기동·저장 실패

- `QUEUED→RUNNING`은 현재 dispatcher의 gw 전이/`job.started`와 같은 tx에서 chat 상태·`chat.status`를 기록한다. RUNNING 취소 의사는 gw.cancel_requested_at과 chat.cancel_requested·CANCEL_REQUESTED 이벤트를 같은 tx에서 기록하고 commit 후 실제 중단을 신호한다. QUEUED 직접 취소가 먼저 이기면 중간 취소 이벤트를 합성하지 않고 terminal만 기록한다. terminal 이후 취소는 저장 최종 상태를 반환한다.
- 각 실행 행을 잠그거나 version 조건부 갱신해 상태/이벤트를 직렬화한다. `closed_at IS NULL`인 승자만 terminal을 기록하고 closed 뒤 재종결/재접수는 0건이다. durable 이벤트가 정리된 뒤에도 이 규칙을 유지한다. 이미 커밋된 terminal의 afterCommit 알림 유실·중복 발행은 동일 event ID와 GET으로 수렴한다.
- 현재 `StartupRecoveryRunner`는 만료 Job을 CANCELLED, RUNNING을 WORKER_INTERRUPTED, 큐 한도 초과 QUEUED를 QUEUE_TIMEOUT으로 `JobTerminalCommitter`에 종결한다. 이를 성공 답변 재생성/동일 LLM 재실행 복구로 바꾸지 않는다. chat projection은 이 경로·timeout sweeper·시작 인가 실패·restore 경로 전체에 참여해야 한다. 보존 중 QUEUED의 재개도 같은 확정 입력·ID를 사용한다.
- 현재 `JobWorker`는 terminal.commit이 실패했다고 자동 성공으로 바꾸거나 완성 결과를 재전송하는 보관 큐를 갖고 있지 않다. 신규 요구는 readiness 전/운영 복구에서 미종결 chat↔runtime 상태를 대조하고 동일 Job의 안전한 실패 종결 또는 기존 허용 종결을 완료하는 것이다. 저장 장애 동안 활성 execution을 닫거나 슬롯을 새 Job에 잘못 반납하지 않는다. 누락·불일치는 fail-closed/경보 대상이며 자동 새 Job 생성은 금지한다.
- runtime이 남아 있는 동안 Job/USER/owner/actor/binding의 대응과 terminal·archive 관계를 검사한다. 정상 purge 뒤에는 닫힌 영구 실행의 runtime 부재를 허용하며 대조 실패로 오인하지 않는다. 열려 있는 실행의 runtime 부재는 정상 purge 결과가 될 수 없다. binding의 EXPIRED/RETIRED는 기록 보존 상태이며 과거 execution의 binding FK를 제거하지 않는다.

### 8.3 SSE·폴링·Proxy

SSE는 chat.execution_event의 id를 사용하며 legacy gw.job_event id와 섞지 않는다. 진행 이벤트/heartbeat만 전송하고 답변 delta/본문을 넣지 않는다. terminal은 영구 커밋된 chat.terminal 이후 메시지 GET으로 읽는다. Last-Event-ID는 현재 owner/actor/실행의 실존 이벤트일 때만 재생한다. 타 실행/actor/system·미존재·30일 보존으로 삭제된 커서는 같은 정보 없는 404로 거부한다. 클라이언트는 자기 실행 GET으로 현재 상태를 다시 확인한 뒤 진행 중인 경우에만 커서를 초기화해 재연결한다. 이 과정에서 새 Job을 만들지 않는다.

TC Backend는 연결·재연결 및 매 write/heartbeat 전에 최신 세션·권한·공유 전달 게이트를 검사한다. 로그아웃/회수/인가 장애 시 새 전송을 차단한다. 이미 송신을 시작한 바이트의 소급 회수는 보장하지 않는다. disconnect는 실행 취소가 아니며 polling도 TC Public API만 사용한다.

실제 경로는 Browser→Envoy HTTPRoute→Front nginx→TC Backend→Gateway다. Front 정본 `deploy/docker/nginx.conf`의 SSE 전용 정규식 `^/api/ai/executions/[^/]+/events$`에 buffering/cache 해제와 장기 timeout을 적용하고, Backend upstream/Gateway/Envoy도 60초+ heartbeat·180초 Job을 검증한다. 기존 일반 /api 정책은 유지한다. Envoy 실제 소유 경로/설정은 운영 확인 전 PASS로 처리하지 않는다.

위 SSE 전용 nginx location은 **현재 파일에 없고 신규 요구**다. chat SSE의 Content-Type은 text/event-stream, Cache-Control은 no-store, heartbeat는 본문 없는 주석이며 초기 간격은 기존 Gateway와 같은 15초다. 저장 이벤트는 `event: chat.status|chat.terminal`, `id: 십진문자열`, `data:{executionId,status,version}`만 보내고 heartbeat에는 id를 부여하지 않는다. 같은 실행의 이벤트 생성은 상태 tx로 직렬화하며 전역 sequence의 간격을 유실로 해석하지 않는다. 각 Pod가 durable event를 tail/replay할 수 있어야 하며 로컬 afterCommit 알림만으로 타 Pod 전달을 보장했다고 주장하지 않는다.

`SseEmitter(timeout)`의 async 수명 제한은 실제 socket write/flush의 시간 상한과 다르다. §5.8의 DB 공유 잠금을 잡은 송신이 무한 대기하지 않도록 서버/전송 계층의 write 중단·timeout과 느린 클라이언트 시험을 확인해야 한다. 이벤트/응답 캐시가 끼어들거나 timeout 설정명만 존재하는 것을 bounded write 성공으로 판정하지 않는다.

TC는 Gateway의 runtime SSE를 그대로 프록시하지 않고 신규 chat cursor/DTO를 중계한다. 재연결마다 새 JWS를 발급하고 §5.8의 최신 TC 세션 검사는 매 write에 적용한다. 60초 JWS 수명을 Job 180초나 chat 수명으로 연장하지 않는다. Browser는 Cookie/credentials와 Last-Event-ID를 전달하고 상태코드를 읽을 수 있는 지속 연결 클라이언트를 사용한다. native EventSource만 사용할 경우 사용자 지정 cursor 헤더·HTTP 오류 분류를 제공한다고 가정하지 않는다. terminal 뒤 연결을 닫고 현재 메시지/실행 GET을 읽는다.

---

## 9. 31일 이후 후속 문맥과 현재 접근

영구 conversationId 하나에 여러 runtime binding이 연결된다. 유효한 CURRENT binding은 재사용하고 실제 Gateway expiresAt에 도달하거나 runtime이 이미 purge되면 EXPIRED로 전환해 같은 영구 대화에 새 runtime Conversation을 만든다. 잔여 수명이 현재 용량 프로필의 큐 대기+Job 예산보다 짧으면 기존 수명을 연장하지 않고 RETIRED로 전환해 새 binding을 준비한다. runtime의 owner/인가/서버 오류를 만료로 오인하지 않는다. binding ID/만료 시각·확정 context는 접수 tx에서 정하고 최초 접수 재요청 때 재선택하지 않는다.

Gateway가 자신의 chat 이력에서 현재 요청의 owner/actor·Source perm·Knowledge 문서 현재 상태를 검증한 턴만 읽는다. **TC가 historyContext/Receipt/본문을 재전송하는 API는 만들지 않는다.** 최신부터 완전한 턴 단위로 최대 20턴, 실제 tokenizer/질문/출력 예산의 더 좁은 범위에 맞춰 선택하고 정상 순서를 유지한다. 실행 snapshot에는 선택한 영구 message ID·schema version·확정 context를 기록해 같은 실행 재시도 중 바꾸지 않는다. 임의 텍스트 요약으로 질문/출처를 왜곡하지 않고 참조가 모호하면 확인 질문을 한다.

과거 답변도 기존 ResultAccessEvaluator/KnowledgeDocumentStateReader의 현재 접근 원칙을 재사용한다. 잘못된 deps·권한 회수·문서 삭제/revision 불일치는 DENIED, 인프라 불명은 UNAVAILABLE로 표시하고 본문을 숨긴다. 이전 ALLOW를 장기 캐시하지 않는다. TC는 최신 grant JWS 발급과 응답 전달 직전 게이트를 책임진다. 다른 Backend도 온보딩 전에 동등한 권한 신선도/회수 계약을 충족해야 한다.

구조화 block Export는 영구 chat.result를 현재 인가한 뒤 제공하므로 runtime purge와 독립이다. 원문 Evidence는 기존 SourceRef/revision 검증 API로 접근하며 원본이 사라졌으면 안전한 410/불가 안내를 제공한다. P0는 원문 Source를 영구 복제하거나 새 revision으로 옛 근거를 대체하지 않는다. 기존 kn 문서 삭제를 chat FK가 막지 않으며 과거 답변 노출은 현재 가용성으로 결정한다.

### 9.1 영구 문맥을 실행 입력으로 사용하는 조건

현재 `WorkflowExecutionAdapter`는 `MessageRepository.findPreviousResolvedTimeWindow()`로 **현재 runtime 대화의 바로 앞 gw.job.seq 요청**만 읽는다. 이 경로는 gw 만료/purge를 넘을 수 없고 20턴 영구 이력을 Prompt에 공급하지 않는다. 신규 chat 문맥 선택과 실제 분류·Prompt 입력 연결이 필요하며 테이블/조회 API만 추가해서 AC-017을 통과시키지 않는다. legacy의 직전 요청 창 규칙은 유지한다.

- chat의 순서는 전역 UUID·createdAt 대신 같은 영구 대화 turn_seq다. 후속 시간창은 바로 앞 turn의 확정 resolved_time_window만 계승 후보로 삼고, 그 turn이 STATUS_ONLY·미확정·현재 접근 거부이면 더 오래된 창을 건너뛰어 고르지 않고 확인 질문으로 처리한다. 자원 참조도 현재 Source resolve/권한을 통과해야 한다. resource/time 계승과 자연어 이력 20턴 선택을 같은 동작으로 혼동하지 않는다.
- 완전한 턴은 같은 실행의 USER+ARCHIVED ASSISTANT/result 쌍이며 현재 접근을 통과한 것만 선택한다. DENIED/UNAVAILABLE/잘못된 deps의 답변뿐 아니라 그 짝 USER와 그 답변에서 파생된 summary/context도 문맥에서 제외한다. 현재 질문은 그 자체의 검증된 effectiveQuery로 처리한다. 과거 raw gw.context/summary를 인가 우회 fallback으로 읽지 않는다.
- 원격 접근 검사는 접수 DB 잠금 밖에서 수행하고, 접수 시 대화 version·선택 ID/스키마·현재 binding을 재확인해 context_snapshot을 확정한다. 이후 worker가 Prompt/Tool/LLM을 시작하기 전에 현재 권한·문서 상태를 다시 검사한다. snapshot은 최초 허용을 영구 허용으로 만들지 않는다. 선택 턴의 접근이 달라졌으면 그 본문의 사용을 차단하고 기존 DENIED/SOURCE_UNAVAILABLE 실패 계약으로 종결하며, 확정 snapshot을 타 이력으로 교체하거나 새 Job을 만들지 않는다.
- 보관 답변·선택 ID를 실제 Prompt에 투영하는 reader는 chat 소유권/현재 접근을 검사하며 단순히 TTL 검사를 끄지 않는다. tokenizer 예산으로 턴을 덜 쓰는 경우 완전한 턴 단위로 결정하고 기록한다. 여러 runtime binding의 같은 과거 턴을 중복 포함하지 않는다.

### 9.2 현재 문서 판정과 원문 참조의 신규 구현 범위

현행 `KnowledgeDocumentStateReader.State`는 AVAILABLE/UNAVAILABLE/UNKNOWN이고 `ResultAccessEvaluator`는 AVAILABLE 외 전부 DENY로 축약한다. 신규 chat은 **확인된 삭제·revision 불일치·ACL 거부=DENIED**, 조회 장애·시간초과·상태 불명=UNAVAILABLE를 구별하는 결과가 필요하다. 기존 UNKNOWN을 삭제 확정으로 해석하지 않으며 legacy의 기존 HTTP 계약을 일괄 변경하지 않는다. kn schema만 있거나 fail-closed 리더만 조립된 환경에서 실제 문서 가용성이 확인되었다고 표시하지 않는다.

실제 현재 문서 리더는 승인 catalog 소속·현재 ACL·canonical/tombstone/revision과 필요한 현재 원천 확인을 결합해야 한다. SourceRef가 같은 것만으로 옛 revision을 이용 가능하다고 판단하지 않는다. P23/P24 테이블·원천 Adapter와 결합한 시험은 별도 선행조건이다. 영구 Evidence API는 §6.4 evidence_refs를 읽고 현재 result 접근과 정확한 revision을 검증한 뒤에만 원문 리더를 호출한다. 현재 `EvidenceSourceReader`의 Found/Unavailable 두 값과 기본 fail-closed 구현만으로는 §7.1의 확인된 소멸 410/검사 장애 503을 구별할 수 없으므로 typed 결과 확장/Adapter가 필요하다. legacy 원문 API의 빈 404를 그대로 410으로 치환하지 않는다.

---

## 10. 기존 Gateway와의 증분 정합

| 영역 | 재사용 | 추가/변경 책임 |
|---|---|---|
| DB/운영 | 기존 nori_ai_gateway, DataSource, JdbcClient, tx manager, gw_flyway, vector/kn | chat schema/7개 테이블·제약·인덱스 |
| 인증 | Bearer→owner, ES256/JWS/60초·scope·perm/pdg·dlg 규칙 | 새 chat Job route 분류, system registry 활성 Gate, 시스템별 issuer/key 신뢰 및 MES 확대 |
| 접수 | readiness/queue·sanitizer·gw job/message/event·감사 | 영구 멱등 조회/원자 chat 접수. legacy 트랜잭션 계약과 구별한 tx 소유 |
| 종결 | JobTerminalCommitter·기존 결과 검증·beforeCommit/afterCommit | 같은 tx의 chat projection/terminal event, 실패 rollback |
| 인가 | ResultAccessEvaluator·Source/Knowledge 현재 접근 | 영구 result 조회/Export/문맥에 TTL 없는 소유 검사·batch deps 평가 |
| 보존 | 기존 gw Conversation 30일·감사 90일·활성 Job 보호 | chat TTL 제외·이벤트만 30일·purge 후 영구 조회/멱등 시험 |
| TC Source | 기존 TcBusinessReadPort/TcSourceAuthorizationPort와 Mock 계약 | 실제 HTTP Adapter·Mock 배타 조립·prod safety 유지·TC 내부 API |

v1의 `archive-snapshot`, `archive-access/check(-batch)`, 외부 signed Receipt/장기 서명키 보존, Browser/TC가 제공하는 historyContext는 **현재 필수 인터페이스에서 제거**한다. 같은 신뢰 서비스/DB 내부의 영구 결과·deps를 API가 직접 검증하므로 별도 DB 간 증표 프로토콜을 만들 이유가 없다. 이력 이관/외부 Export가 나중에 필요하면 별도 Spec으로 다룬다.

기존 `/v1`만 사용하는 소비자는 종전 TTL/응답을 유지한다. `/v1/chat` 도입이 과거 gw 대화를 전부 자동 영구 전환하는 작업은 아니다. 현재 실제 영구 대화 테이블이 없고 데이터 이관 승인이 없으므로 cutover 이후 새 chat 접수부터 영구성을 보장한다. legacy 이력 import가 필요하면 소유권·deps·원문 마스킹·중복 키를 검증하는 별도 승인 작업이며 이미 purge된 기록을 복구할 수 있다고 주장하지 않는다.

---

## 11. 상태·실패 계약

| 필드 | 정본 / 의미 |
|---|---|
| `gatewayJobStatus` / `errorCode` | QUEUED/RUNNING/SUCCEEDED/PARTIAL/FAILED/CANCELLED/TIMED_OUT 및 안전한 terminal_error_code. runtime purge 뒤 chat의 최종 요약 유지 |
| `archiveStatus` | PENDING/ARCHIVED/STATUS_ONLY. runtime terminal과 영구 저장을 같은 tx로 바꾸므로 정상 종결 이후 별도 복사 대기 상태 없음 |
| `cancelRequested` | 사용자의 취소 의사. Gateway 실제 CANCELLED를 대체하지 않음 |
| `terminal` | closed_at != null. 영구 결과 또는 본문 없는 실제 종결의 commit을 뜻함 |
| 메시지 `access` | AVAILABLE/DENIED/UNAVAILABLE. 현재 요청의 접근 판정이며 저장 실행 상태를 소급 변경하지 않음 |

Job GET의 HTTP 200을 업무 성공으로 보지 않는다. result.status=SUCCESS와 jobStatus=SUCCEEDED도 별도 enum이다. transport timeout은 접수 여부 불명이며 동일 clientMessageId 재요청/목록 GET으로 회복한다. DB 장애로 terminal 커밋이 실패하면 기존 worker/recovery의 동일 Job 처리를 유지하고 허위 완료를 내보내지 않는다. 한 대화의 closed_at NULL 실행이 있는 동안 새 질문은 409지만 동일 clientMessageId 조회는 먼저 허용한다.

취소와 종결 경쟁은 같은 Job의 실제 마지막 커밋으로 결정한다. 종료된 실행에 cancel 재요청은 같은 영구 최종 상태로 수렴하며 새 Job을 만들지 않는다. 권한/세션 무효는 신규 질문/Source/전송을 막지만 이미 발생한 영구 기록을 다른 사용자에게 넘기거나 자동 삭제하지 않는다.

---

## 12. 보안·본문 저장·운영 소유

TC Cookie/CSRF/URL permCode·AI_CHAT_USE는 유지한다. 공개 API에서 sys/actor/service credential/권한/임의 history를 받지 않으며 gatewayUuid를 알더라도 모든 SQL/API에 현재 owner/actor를 강제한다. Backend→Gateway와 Gateway→Backend 서비스 비밀은 분리한다. TC 내부 service chain의 CSRF 제외를 공개 Cookie API로 확대하지 않는다.

일반 로그/감사/OTLP에 질문·답변·Source 원문·Prompt·모델 사고·Cookie/JWS/dlg/세션 지문을 남기지 않는다. 허용 상관값은 system/actor 내부 참조·영구 conversation/execution ID·runtime job ID·상태·소요 시간·오류 코드이며 개인정보 접근 통제를 적용한다. 메트릭 ID label은 금지한다. chat DB는 결과 민감도에 맞춰 접근 통제/암호화하며 각 시스템 Backend에 DB 권한을 배포하지 않는다.

DB가 같다는 이유로 tc/hrm/mes/wm 사용자가 교차 SQL 접근할 수 있는 구조를 만들지 않는다. 현재 공유 서버/단일 Gateway DB 계정이라는 승인 운영 정책은 유지하고, 서비스 repository scope·음성 시험·네트워크 경계로 완화한다. RLS/별도 계정/서버 설정 변경을 이번 Spec에 미승인 필수조건으로 추가하지 않는다.

---

## 13. 수용 기준과 추적

모든 제품 AC와 Gate는 실제 실행 전 NOT_RUN이다. 각 증거는 날짜/환경·세 저장소 SHA·계약/SchemaVersion·입력/기대/실제·명령/trace·마스킹 여부를 기록한다. Mock/계약·DDL 검증을 실제 TC 로그인/Tool/LLM 수용 PASS로 바꾸지 않는다. 31일 시험은 격리 DB/제어 Clock을 사용한다.

| ID | 입력/상황 | 반드시 확인할 결과 |
|---|---|---|
| AC-TC-AI-001 | 로그인·AI 사용권·업무 라우트 | 플로팅 버튼 1개, /login 미표시 |
| AC-TC-AI-002 | 최소화/재열기·업무 이동 | URL은 업무 이동만 변경, Host/선택/미전송 입력 유지 |
| AC-TC-AI-003 | 데스크톱 | 좌측 대화/우측 내용 2열, 독립 /ai/LNB 없음 |
| AC-TC-AI-004 | 390px/모바일 키보드 | 목록/내용 전환·입력/스크롤 손실 없음 |
| AC-TC-AI-005 | ko/en/vi 전환 | 상태/오류/마스킹 안내/근거 UI 번역 누락 없음 |
| AC-TC-AI-006 | Tab/Escape/IME/Shift+Enter | 포커스 복원·접근성·IME 오전송 없음 |
| AC-TC-AI-007 | 소유 대화 1000개·메시지 페이지 | keyset/상한/owner 조건·batch result 접근, 전체 본문 스캔/N+1 없음 |
| AC-TC-AI-008 | 실제 TC 로그인·실존 EQP 질문 | Browser→TC Backend→실 Gateway/Source/LLM→답변, **nori_ai_gateway.chat** USER/result 영구 저장 |
| AC-TC-AI-009 | 같은 스레드 후속 질문 | 같은 영구 대화, 현재 Source 권한·올바른 resource/time 문맥 |
| AC-TC-AI-010 | 실행 중 업무 이동/최소화/새로고침 | 같은 영구 execution/Job 복원, 새 Job 생성 없음 |
| AC-TC-AI-011 | 다중 Proxy 60초+ SSE | heartbeat/flush/timeout 정상, 답변 delta 없음 |
| AC-TC-AI-012 | SSE 단절·Backend/Gateway Pod 재시작 | 영구 event cursor/GET 재동기화, 중복 본문/Job 없음 |
| AC-TC-AI-013 | PARTIAL/FAILED/TIMED_OUT/CANCELLED/DENIED/503 | 실행·조회 접근·인프라 오류를 구별, 허위 성공 없음 |
| AC-TC-AI-014 | cancel/terminal 경합 | cancelRequested와 실제 최종 상태 구별·종결 1개 |
| AC-TC-AI-015 | TC 계정 A/B + TC/HRM/MES/WM 동일 actor fixture | 타인/타 시스템 목록·메시지·SSE·취소·블록 노출 없음. 비활성 시스템 활성 처리 없음 |
| AC-TC-AI-016 | 실제 기존 runtime purge 실행 | **gw 대화/Job/result 삭제 후 chat 대화/USER/result/멱등 키 보존**, CASCADE/FK 영향 없음 |
| AC-TC-AI-017 | 31일 후 같은 영구 대화의 후속 질문 | 새 runtime binding, Gateway 자체의 현재 허용 이력으로 문맥 복원, TC history 재전송 없음 |
| AC-TC-AI-018 | 보관 답변 후 권한 회수 commit | 이후 새 GET/Export/SSE write의 본문 비노출, DENIED/UNAVAILABLE 구별 |
| AC-TC-AI-019 | 문서 삭제/revision 변경·원천 장애 | 영구 답변/Export/문맥 fail-closed, kn 삭제를 chat FK가 막지 않음 |
| AC-TC-AI-020 | DB에서 타 owner/actor/role/대화로 자식 연결 시도 | Composite FK/유니크로 거부, 잘못된 deps/payload/schema 표시·문맥 포함 없음 |
| AC-TC-AI-021 | 민감정보 질문·같은 clientMessageId·runtime purge 뒤 재요청 | effectiveQuery만 저장/전송, 원문 로그/영속 없음, **영구 USER/execution/Job/ASSISTANT 중복 없음**, 다른 입력은 409 |
| AC-TC-AI-022 | 접수·종결의 commit 전/후 강제 실패·응답 유실 | 같은 DB tx 전부 rollback/commit, chat 저장 실패의 허위 terminal 없음, 재요청 같은 execution |
| AC-TC-AI-023 | 자동 백업 제거·사용자 요청 시에만 수동 백업 | 앱 기동·배포·CI에 백업 실행 없음, Helm CronJob 0, 백업 이미지·목적지 Secret·runconfig 입력·필수 게이트 없음 |
| AC-TC-AI-024 | logout 직후 SSE/heartbeat·계정 전환 | 전송/캐시 정리, 다음 사용자 잔여 본문 없음 |
| AC-TC-AI-025 | logout과 진행 Tool/Source READ 경합 | TC 위임 무효·새 READ 거부, Gateway의 사용자 세션 원문 취득 없음 |
| AC-TC-AI-026 | TC 권한 DB/Redis·Gateway DB/문서 장애 | 503/UNAVAILABLE fail-closed, 과거 본문 캐시 fallback·401 오분류 없음 |
| AC-TC-AI-027 | XSS/링크·sys/actor/history 위조·임의 Source | 스크립트/권한 확대/교차 시스템 Source/사용자 문맥 주입 차단 |
| AC-TC-AI-028 | 신규 schema/API/Proxy·비활성/rollback | 기존 TC 업무·legacy Gateway TTL/계약 회귀 없음, 새 DB 연결/TC 대화 테이블 없음 |
| AC-TC-AI-029 | runtime terminal tx의 영구 result 저장 실패 | 모든 terminal 쓰기 rollback, 후속 동일 Job 정합성 회복, 비동기 복사/Receipt 의존 없음 |
| AC-TC-AI-030 | AI 권한 없는 사용자·DISABLED 시스템·잘못된 issuer/key | 신뢰 경계에서 차단, 업무/Gateway 대화 조회·신규 Job 없음 |

| Gate | 필수 근거 | 미충족 시 |
|---|---|---|
| TC-AI-CONTRACT | 공통/TC API·JWS route·Source DTO·MES/issuer 비활성 경계 | 통합 불가 |
| TC-AI-UNIT | UI/state/마스킹·Core 소유권/문맥/상태·schema 해석 | E2E 불가 |
| TC-AI-INTEGRATION | 기존 Migration+chat 제약·원자 tx·purge·재시도·복구 | 실제 수용 불가 |
| TC-AI-LIVE-E2E | 실제 로그인·실 Source/LLM 답변·공통 DB 영구 재조회 | 제품 완료 불가 |
| TC-AI-SECURITY | 다중 시스템/actor IDOR·회수/세션/SSE·현재 문서·비밀 | 운영 활성화 불가 |
| TC-AI-OPS | 실제 migration/history·키/설정·Proxy·부하·호환 rollback | 운영 활성화 불가 |

추적: INV-01/03→015/025/027/030, INV-02→015/020, INV-04/05→016/017/023, INV-06→018/019/026, INV-07→008/013/014/022/029, INV-08→010/012/021/022, INV-09→005/021/025/027, INV-10→028이다.

2026-11-12 재검증으로 다음 검증 범위를 기존 AC에 추가한다. 이는 시험 설계 요구이며 실행 완료 표시가 아니다.

| 기존 AC | 추가로 필요한 실패/경계 검증 |
|---|---|
| 012/014/022/029 | QUEUED/RUNNING/cancel/terminal의 gw+chat 동시 rollback, beforeCommit 실패·afterCommit 알림 유실, 다중 Pod event tail, 이벤트 정리 후 terminal 중복 거부, 실제 잠금 순서·교착 재시도 |
| 016/020/021 | 내부 binding Composite FK의 타 대화/actor/system 결속 거부, NULL runtime/미종결 terminal 조합 거부, runtime purge 후 키 조회·Evidence 참조/Export 보존, 활성 Job purge 제외 |
| 009/017/018/019 | 2개 이상 runtime binding, 직전 turn 창 부재 때 오래된 창 건너뛰기 금지, 거부 턴의 USER/summary/context까지 Prompt 제외, snapshot 이후 회수 시 LLM 호출 차단 |
| 021/024/026/030 | sanitizer.applied와 실제 변경 flag 구별, Axios 자동 헤더와 body 키 일치, 구 버전 재요청·응답 유실 키 조회, 세션 지문 직접 PK 조회, 늦은 응답 generation 차단, upstream 인증 실패의 Browser 401 오분류 금지 |
| 023/028 | 자동 백업 예약·코드·배포·CI·자격 요구 제거, seed만 있는 새 DB 초기화·chat 업무 데이터가 있는 epoch 누락 거부, 전체 Migration 이력/DDL drift와 최신 버전 guard 차이, 혼합 worker 비활성 배포·호환 rollback |

이들 중 실제 애플리케이션 tx/보안/복구 항목은 설계 DDL 시험으로 PASS 처리할 수 없다. 실제 Knowledge 현재 판정·실 TC HTTP 연결·운영 정책/Proxy는 외부 선행조건이 확보된 환경에서 검증한다.

---

## 14. Migration·배포·rollback

Gateway의 `flyway/sql/`에 새 forward-only Migration을 추가하고 기존 schema/V 파일/checksum/운영 이력을 덮어쓰지 않는다. 설치 SQL은 빈 DB와 기존 gw/kn 데이터가 있는 DB 양쪽을 검증한다. 새 schema 때문에 기존 gw 테이블의 데이터를 비우거나 V5의 과거 bootstrap 제한을 다시 적용하지 않는다. MES CHECK 확장도 이미 적용된 V2/V3 수정이 아닌 새 Migration이다. **실제 운영 적용 전 catalog/이력·DDL drift·예약 버전 확인은 필수이며 이번 Spec 작업에서 라이브 DDL을 실행하지 않는다.**

Gateway 서비스 계정·기존 `AI_GATEWAY_DB_*` 환경변수를 재사용하며 chat 생성/운영 권한과 같은 서버의 다른 DB에 영향 없음은 실제 운영 증거로 확인한다. 서버 설정 변경·새 DB/계정 분리를 임의 선행조건으로 추가하지 않는다. TC 업무 DB에는 세션/권한 게이트와 AI 권한 mapping에 필요한 별도 비파괴 변경만 있을 수 있고, 소유는 기존 Exec 경계다.

순서는 공통 API/Source·schema 계약 정합 → AI 비활성 상태의 schema+Gateway 호환 코드/worker 전체 배포 → TC Backend/Front·nginx/HTTPRoute 배포 → 다중 Pod/실제 Source/로그인/31일·보안 Gate → TC 활성화다. HRM/MES/WM은 registry만으로 활성화하지 않고 각각 issuer/key·Source Adapter·실사용자 식별·권한/회수·운영 Gate를 추가한다. 현재 TC의 초기 실행/생성 슬롯=1·큐 깊이4·60초 대기·180초 Job 제한을 늘렸다고 가정하지 않는다. 공통 저장이 처리 용량 증가를 뜻하지 않으며 부하·시스템별 공정성은 후속 온보딩에서 측정한다.

현재 SchemaVersionGuard는 정확 버전 일치이므로 **새 Migration 이후 옛 이미지를 그대로 배포하는 rollback은 실패할 수 있다**. rollback 후보는 새 DB 버전과 호환되는 것으로 실제 검증한 이미지/설정이어야 한다. 없으면 새 이미지에서 chat 기능을 비활성화하고 forward fix한다. schema/version guard를 우회하거나 과거 이력을 편집하지 않는다.

SchemaVersionGuard가 검증하는 것은 성공한 version의 최댓값과 classpath 최댓값의 일치이며 **checksum·전체 이력 연속성·실제 열/제약 존재의 검증은 아니다**. CI의 기존 `flywayInfo → flywayMigrate → flywayInfo`와 별도로 Flyway validation/DDL drift·catalog 대조 근거가 필요하다. 빌드의 `flyway/sql` 리소스 복사, 외부 Migration 실행, 전체 Pod/worker의 schema 호환 상태를 묶어 확인한다. 기존 Pod는 이미 기동해 있어 새 guard를 즉시 재실행하지 않으므로 Migration 성공만으로 혼합 버전 worker의 안전성을 보장하지 않는다. 구 worker가 새 chat Job을 처리하지 않는 배포 경계를 검증한 뒤에만 chat을 활성화한다.

정보 노출·중복 실행·저장 불일치·기존 회귀 시 신규 chat 접수와 본문/SSE 전달부터 차단한다. 이미 접수된 Job은 새 키로 재실행하지 않고 호환 worker로 종결/조정한다. chat 테이블/멱등 키/결과를 DROP하거나 runtime만 먼저 삭제하는 rollback은 금지한다. schema 보정은 새 Migration이며 일반 rollback에 DB 시점 복원을 자동 수행하지 않는다.

**2026-11-12 사용자 확정 — 자동 백업 없음.** 백업은 사용자가 명시적으로 요청할 때만 별도 수동 작업으로 수행한다. 배포·기동·CI·정기 작업·수용 게이트에서 백업을 만들거나 백업 목적지·주기·보관 기간·RPO/RTO를 요구하지 않는다. 기존 자동 백업과 필수 백업/복원 리허설 요구는 철회한다. 이번 작업에서는 백업·복원·운영 배포를 실행하지 않는다.

`DatabaseEmptinessProbe`는 epoch 없는 DB의 INITIALIZE_NEW 판단에도 쓰인다. runtime purge 뒤 chat 업무 데이터만 남으면 신규 초기화를 거부해야 하며, 새 빈 DB의 chat.system 4개 seed 행은 기존 queue_control과 같은 bootstrap 상태로 구별한다. chat을 모두 무시하거나 seed 때문에 모든 빈 DB 초기화를 거부하는 양쪽 오류를 회귀 검증한다. 현재 `RecoveryModeRouter`는 실제 restore handler가 없거나 미완료이면 기동을 거부하므로 SQL 복원 성공이 제품 readiness 성공을 뜻하지 않는다.


---

## 15. 선행조건과 현재 미충족 상태

| ID | 선행조건 | 현재 상태/소유 |
|---|---|---|
| PRE-01 | 기존 DB live catalog/history·drift 및 chat Migration 예약 정합 | 미조회. Gateway 운영/Migration 소유 |
| PRE-02 | chat 7테이블·공통 영구 API·원자 접수/종결·purge 독립 | 신규 구현 필요. Gateway |
| PRE-03 | Gateway의 현재 문서/Source access·31일 이력 선택 | 현행 검증기를 재사용하되 영구 경로/Knowledge 실제 연결 시험 필요 |
| PRE-04 | 실제 TC READ/Source authorize·Gateway HTTP Adapter/Mock 배타 조립 | 신규 실제 연동 구현 필요. TC Backend+Gateway |
| PRE-05 | TC JWS/위임·최신 DB 인가·공유 전달 게이트·AI 권한 grant | 신규/통합 구현 필요. TC Backend+권한 운영 |
| PRE-06 | TC overlay·공통 API 소비·현재 접근별 cache/로그아웃 정리 | 신규 구현 필요. TC Front |
| PRE-07 | 호환 rollback 이미지·Proxy | 코드/실환경 보완·시험 필요. Gateway/TC 배포 소유 |
| PRE-08 | HRM/MES/WM issuer/enum/CHECK·Source/권한 계약 | 향후 온보딩. 지금 활성화/지원 완료 아님 |
| PRE-09 | AC 30개 및 6개 Gate 실제 증거 | 제품 NOT_RUN/BLOCKED |

§1.2의 기존 승인 정본·phase 허용 범위 정합과 위 운영 외부 정책 확인도 선행조건이다. 이번 재검증은 이들을 완료/승인으로 바꾸지 않는다. Gateway 신규 구현은 chat 7테이블/DTO/API·원자 접수 재사용 경계·종결 DB projection·진행 전이·영구 문맥/현재 접근·Evidence 참조·기동 정합성 보완이며, TC 신규 구현은 JWS/위임·최신 DB/공유 게이트·실 READ/실 HTTP Adapter·Public/SSE 중계, Front 신규 구현은 overlay 수명·API/renderer·재접속/캐시/로그아웃 연결이다. kn 테이블 구현은 기존 P23/P24 책임으로 구별한다.

---

## 16. 확정 결정과 대체 이력

| ID | 현재 결정 | 이유/효력 |
|---|---|---|
| D-01 | Gateway가 공통 영구 대화 정본, 기존 nori_ai_gateway에 chat schema | 사용자 재설계 지시. 각 시스템 중복 구현/DB 직접 접속 제거 |
| D-02 | TC 최초 연결, HRM/MES/WM은 같은 계약/다른 owner+actor | 공통 저장과 시스템 간 데이터 공유를 구별 |
| D-03 | 기존 gw 30일 runtime·kn·gw_flyway 유지, chat→gw/kn FK 없음 | CASCADE/purge 및 migration 호환 |
| D-04 | 같은 DataSource/tx에서 질문 접수와 최종 영구 결과를 원자 기록 | TC Outbox/비동기 archive 복제·Receipt 프로토콜 제거 |
| D-05 | 사용자 확정 A의 전송 전 마스킹 유지 | 원문 비보관·동일 질문/보관/재시도 |
| D-06 | 영구 멱등 키·owner Composite FK·현재 Source 접근 재검증 | purge 후 중복 Job·타 actor/system 노출 차단 |
| D-07 | Front overlay/2열·기존 인증·업무 라우트 유지 | 기존 UX 요구 유지 |
| D-08 | 자동 백업 없음, 사용자가 명시적으로 요청할 때만 수동 백업 | 2026-11-12 사용자 확정. 백업 코드·배포·CI·필수 게이트 제거 승인. Q-01 철회 |

v1 TC-D04/05/06/09/11 및 `tc_ai_*` 영구 본문·Outbox·GW-X1~3 Receipt/외부 history 결속은 **2026-11-12 D-01/03/04로 대체**되었다. v1의 경로/인증/마스킹/UX/실제 수용 요구는 새 책임 배치에 맞게 유지했다. 기존 Gateway 승인 Spec/Plan 자체를 이 문서 수정으로 승인/구현 완료 처리하지 않는다.

---

## 17. 구현 인수 체크리스트

- [ ] 기존 nori_ai_gateway DataSource/gw_flyway에 chat 설치, TC 업무 DB 대화 본문/추가 Gateway DB 연결 없음
- [ ] 공통 system/actor 소유권과 Composite FK·영구 멱등/활성 실행/현재 binding 유니크 제약
- [ ] Gateway 단일 접수 tx 및 단일 terminal tx의 chat/runtime/감사/이벤트 정합
- [ ] 실제 기존 purge 후 chat 대화/답변/멱등 보존 및 31일 후 후속 문맥
- [ ] 현재 Source/문서 접근 재검증·키/issuer/system 격리·TC 회수/세션 전달 게이트
- [ ] TC 실제 READ/위임·Gateway HTTP Adapter 및 Mock 배타 prod 조립
- [ ] 단일 AiChatHost·데스크톱 2열·모바일·ko/en/vi·접근성·안전한 renderer
- [ ] 전송 전 마스킹·원문/Prompt/자격 비보관·로그아웃 cache/스트림 정리
- [ ] 자동 백업 실행/예약·백업 이미지·목적지 자격·필수 게이트 없음, 호환 migration/rollback·실제 Proxy
- [ ] AC-TC-AI-001~030 및 6개 Gate 실제 실행·증거·PASS

---

## 18. 완료의 의미

완료는 **실제 TC 로그인 사용자가 업무 화면에서 실제 Gateway/Source/LLM 답변을 받고, 공통 AI DB의 자신의 대화를 영구 재조회·31일 후 후속 질문하며, 타 시스템/타 사용자 데이터는 볼 수 없는 상태**다. Spec/DDL 존재·격리 DB 제약 시험만으로 이 완료를 선언하지 않는다. TC가 첫 연결이고 HRM/MES/WM 연결 완료는 각각의 후속 온보딩 증거로 판정한다.

---

## 19. 검증·변경 보고

### 19.1 기존 지적의 재설계 반영

| 기존 지적 | v2 처리 |
|---|---|
| V-01 Gateway 기능/실제 Adapter 부재 | Gateway를 대상에 포함. 공통 Chat API·원자 저장·실제 TC HTTP Adapter를 명시 |
| V-02 TC Outbox/멱등 복구 간극 | TC Outbox 제거. Gateway의 영구 clientMessageId와 같은 DB 원자 접수로 대체 |
| V-03 권한 신선도/회수·오류 분류 | TC 및 각 온보딩 Backend의 최신 인가/공유 전달 게이트 유지 |
| V-04 Receipt/hash/history 결속 | 외부 Receipt/본문 재전송 제거. Gateway 자기 영구 데이터/검증된 deps에서 문맥 선택 |
| V-05 Host 재마운트 | pathname key 밖 인증 레이아웃·Host 수명 계약 유지 |
| V-06 Source HTTP/typed 상태 | 기존 Mock AuthorizationResponse/project 확인을 반영해 TC 실제 HTTP 계약 재사용 |
| V-07 상태/오류 모순 | 영구 terminal 원자 커밋·PENDING/ARCHIVED/STATUS_ONLY와 현재 access 분리 |
| V-08 배포/소유/rollback | 기존 DB/schema/Flyway/정확 버전 guard·backup/restore/purge를 직접 대조해 반영 |

### 19.2 실제 실행 검증과 한계

- 착수 시 실행: 로컬 코드·Migration·repository/transaction·purge·backup/restore 경로 대조. 기존 Gateway spec-guard는 exit 0, BUILD SUCCESSFUL, Spec SHA-256 `365825fb527182885e19e65c74440f6005cd66e848081aa39209a264af643741`로 통과했다. 본 v2의 승인/제품 수용 판정은 아니다.
- 실행: 문서의 설계 SQL을 추출해 기존 Migration SQL **V1/V2/V3/V3_1/V4/V5/V7 7개** 적용 후 추가했다. 신규 빈 PostgreSQL 18 컨테이너, 기존 로컬 pgvector 0.8.7 이미지 ID `sha256:5895949dfcdf308eb0cd31c950c93c14895c77f33fbedfecf1fa511ab1d4bcd2`를 고정 사용했다. 네트워크 none·호스트 포트/기존 DB/볼륨 연결 없음·합성 fixture만 사용했고 종료 후 이 작업 전용 컨테이너를 제거했다.
- 설계 검증 명령: `node C:/Users/DongGeon/AppData/Local/Temp/verify-central-chat-v2.cjs`. 최종 결과 **exit 0, SCHEMA_TEST_PASS|35, 실패 0**. 7테이블 생성, 4시스템 같은 actor/client key 분리, 타 시스템/actor/role/turn FK 거부, 영구 멱등/활성 실행/현재 binding/terminal event 유니크, 크기/deps/NULL 경계, runtime CASCADE 후 chat 결과·메시지·binding·멱등·실패 오류 요약 보존, 본문 없는 실패의 결과 비합성을 확인했다. runtime 삭제는 RetentionMaintenanceRepository와 같은 만료/활성 Job 제외 조건의 SQL로 실행했다. 이는 격리 **DDL/제약/삭제 관계** 검증이며 Flyway 실행기·실제 repository Java 경로·애플리케이션 원자 tx/인가/E2E의 PASS가 아니다.
- 변경 감시/중간 기준선 검사: 이 작업에서 편집하지 않은 기존 Gateway Spec/Plan의 해시가 작업 중 달라졌다. 비교 시 Spec은 `2e5d3658131063ab0a81440b3dd02ec2ecdadd7079fcb870cb55c55336ccf5f6`, Plan은 `421c2ee1930543b36b9ca9afe72efaff355f4a5a4d0e8fc0af5e99960afe7f6f`였고 기준선은 시작 값 그대로였다. 중간 spec-guard 재실행은 **SPEC_BASELINE_MISMATCH, Eval CLI exit 3 / Gradle exit 1**로 실패했다. 해당 파일/기준선은 덮어쓰지 않았고 불일치 상태에서 기존 구현 phase를 시작하지 않았다.
- 최종 기준선 재검사: 이후 이 작업 밖에서 기준선 파일도 변경된 것을 감지해 동일 명령을 다시 실행했다. Spec/기준선 SHA는 모두 `2e5d3658131063ab0a81440b3dd02ec2ecdadd7079fcb870cb55c55336ccf5f6`였고 **exit 0, BUILD SUCCESSFUL in 1m 11s**로 종료했다. 실행 도중 공통 build-logic의 Kotlin daemon 컴파일 오류/fallback 로그가 있었지만 최종 guard task는 통과했다. 이 작업에서 공통 코드를 수정하지 않았으며 전체 제품 build/수용 PASS로 해석하지 않는다.
- 문서 정적 검사 **22/22, exit 0**: 대상 저장소·20개 절·7개 DDL 테이블·runtime FK/CASCADE 부재·영구 멱등/Composite FK·Core ResultStatus enum 정합·예시 JSON 2개·로컬 링크 7개·내부 Source 경로 8개·AC 30개/불변식 10개/Gate 6개·미체크 인수 상태·UTF-8/LF 및 격리 schema 35건 결과를 대조했다. 검증한 설계 SQL SHA-256=`7453f97474c62b5aa4a302b6abf2b2722492d4fed14f82f1011a1e57460902b3`이다.
- 미실행: 운영 DB catalog/history/DDL·애플리케이션 단위/통합/실제 TC 로그인·Source/LLM·배포·운영 backup/restore. 모든 제품 AC/Gate는 NOT_RUN이며 미구현/외부 선행조건은 BLOCKED다.

### 19.3 2026-11-12 Specify 재검증·보완 결과

이번 절은 새 검증 기록이다. §19.2의 기존 실행 명령·건수·SHA·결과는 수정하지 않았으며 재실행한 것으로 간주하지 않는다. 코드 HEAD는 §1과 동일하고 조사 대상은 현재 작업 트리다. Gateway의 기존 Spec/Plan/기준선과 미추적 evidence·대상 Spec을 기존 변경으로 식별했으며 TC Backend/Front의 Git 상태는 시작·종료 모두 clean이었다. 참고한 common의 기존 미추적 package.json도 편집하지 않았다.

| 발견한 문제 | 이번 Spec 보완 |
|---|---|
| kn schema 생성과 Knowledge 테이블/리더 구현 혼동 | §0/1/3/9에 현재 gw 15테이블·kn 0테이블·chat 0테이블과 V10/P23/P24 예정 책임을 구별 |
| DB에 원본 세션 token이 있다고 가정 | §5.8을 현재 SHA-256 지문 PK 직접 조회로 정정. 주기적 세션 재검사·ASYNC 재사용·URL 캐시/401 실패 경계와 AI 신규 최신 게이트를 구별 |
| 외부 runtime FK 부재에 비해 영구 binding 결속이 약함 | 7테이블 수는 유지하고 내부 binding Composite FK·runtime ID/상태 NOT NULL·시간/상태/event CHECK를 설계 DDL에 추가 |
| runtime purge 뒤 Evidence 매핑/확정 시간창이 소실될 여지 | 영구 evidence_refs와 context_snapshot/resolved_time_window를 명시. 원문 비복제·현재 revision 검사·직전 turn 규칙 유지 |
| 접수/종결의 tx 확장 가능성과 현재 구현을 혼동 | 자체 tx/외부 tx 거부·전역 queue 잠금 안 영구 멱등 재조회·beforeCommit DB projection·만료/취소 승자 결과 및 afterCommit 유실 계약 명시 |
| terminal 외 RUNNING/취소·재기동·저장 실패의 영구 반영 누락 | 실제 전이 tx·복구 원인별 STATUS_ONLY/ARCHIVED·활성 execution 대조·잠금 순서/중복 종결 차단을 명시 |
| DTO·키 복구·sanitizer flag·upstream 인증/SSE 오류의 불명확성 | §7.1의 공통 DTO/오류 매핑·키 조회 API, 서버 referenceTime 투영, 실제 변경 flag·Axios 헤더/영구 키 구별·chat SSE 형식 보완 |
| 현재 문서 접근 DENIED/UNAVAILABLE를 기존 코드가 이미 구별한다고 오인할 여지 | 두 값 evaluator/UNKNOWN 기본 리더·Found/Unavailable 원문 리더의 한계와 신규 typed 처리·실 Adapter 필요성 명시 |
| Gateway와 TC 배선·Source 재사용·배포 안전성 누락 | 명시적 Mock import/실 Adapter 배타 조립·기존 Redis 실패 은폐·service/Cookie 필터 범위·느린 SSE write 검증을 추가 |
| schema guard/restore/빈 DB 판단을 과대평가 | 최신 version 검사와 checksum/drift 검사를 구별하고 chat 복원 oracle·epoch/seed 판단·혼합 worker/rollback 요구를 보완 |
| 기존 승인 문서의 /ai·TTL·phase 범위와 신규 확정 결정 충돌 | §1.2에 충돌을 직접 보고. 기존 확정 결정을 재선택하지 않으며 기존 정본/기준선의 영구 chat 증분 정합을 해소했다고 주장하지 않음 |

**이번에 실행한 검증:**

- 저장소 지침·Gateway 아키텍처 Spec/Plan·Dev report·인증/API 계약·Migration 7개와 실제 Controller/DTO·저장소·접수/종결/진행/복구·Source/Knowledge 빈 조립·TC 인증/세션/권한/로그아웃·Front 라우터/Axios/cache/nginx를 읽기 대조했다. common의 세션 매퍼/엔티티·설비 상태, Business의 Work 상태 전이, Exec의 DB migration/배포 파일은 읽기 전용으로 참고했다. Envoy HTTPRoute의 실제 운영 소유 파일과 live 설정은 확인하지 못했다.
- `spec-guard`: `./gradlew.bat :gateway-eval:run --args="spec-guard --spec C:/workspace_nori/nori-ai-gateway/docs/ai-rag-gateway-architecture.md --baseline docs/gates/spec-baseline.md"`를 `AIGW_SPEC_FILE`도 같은 경로로 설정해 실행했다. 최초 결과는 **SPEC_BASELINE_MISMATCH, Eval exit 3/Gradle exit 1**. 후속 동일 검사 결과는 **exit 0, BUILD SUCCESSFUL in 7s**, FILE_HASHED/LOCAL_FILE, SHA=`f3c2da0db088f88c51f5aa3d357b2e25075648416cfcb1cc9d974430fdf79d6b`다. 최초 검사 직전의 전체 해시 snapshot은 없으므로 두 결과 사이의 상태 변경 원인을 단정하지 않는다. 이 검사는 기존 Gateway 문서의 결속 검사이며 이번 Spec 승인·제품 검증이 아니다.
- 이후 기존 아키텍처/기준선/seq16 승인 기록의 추가 변경을 감지해 관련 TTL·/ai·Knowledge/백업 계약과 승인 표기를 다시 읽고 같은 guard를 재실행했다. **최종 exit 0, BUILD SUCCESSFUL in 1m 16s**, SHA=`7831c76984893fb3e82fc18bf232d11de4c65bb79fde493e066b3c50984b727a`다. 공통 nori-common/build-logic Kotlin daemon의 컴파일/복구 오류와 fallback 로그가 있었지만 최종 guard는 성공했다. 공통 코드 수정·전체 제품 build/수용 시험은 하지 않았다. seq16 승인 기록은 후속 조회에서 2026-11-12 사용자 승인으로 바뀌었으나 이 chat Spec의 승인은 아니다.
- `node C:/Users/DongGeon/AppData/Local/Temp/verify-ai-chat-spec-20261112.cjs`: 최종 **exit 0, STATIC_CHECK_PASS|27, DDL_CHECK_PASS|62**. 문서 UTF-8/LF·7테이블·runtime FK/CASCADE 부재·내부 binding FK·JSON 2개·로컬 링크 7개·ResultStatus 12값·AC 30/INV 10/Gate 6·미체크 상태와 코드의 지문 근거를 정적 대조했다.
- 격리 DB는 기존 로컬 고정 이미지 `sha256:5895949dfcdf308eb0cd31c950c93c14895c77f33fbedfecf1fa511ab1d4bcd2`의 **PostgreSQL 18.6/pgvector 0.8.7**이다. 신규 컨테이너에 vector 확장을 만들고 V1/V2/V3/V3_1/V4/V5/V7을 순서대로 SQL 적용한 뒤 문서 DDL을 적용했다. Flyway 실행기로 운영 이력을 적용한 시험은 아니다. 네트워크 none·호스트 포트 없음·기존 DB/볼륨 마운트 없음·합성 데이터만 사용했고, 이미지가 자동 만든 익명 볼륨 1개와 컨테이너는 종료 후 삭제를 확인했다.
- DDL 62건은 동일 actor/키의 4시스템 격리 및 TC의 두 actor 키 분리, actor/대화/turn/role/binding FK 거부, 활성 실행·영구 키·runtime ID·현재 binding·terminal event 중복 차단, NULL/빈값/JSON/크기·상태/시각 경계, 같은 DB SQL rollback, 기존 purge와 같은 만료/활성 제외 SQL 뒤 runtime CASCADE 및 영구 질문/결과/오류 요약/참조/키 보존, 과거 binding 유지·신규 CURRENT 전환을 확인했다. 설계 SQL SHA-256=`3297dd153a6b882e4eac146bc438d1acc9a8fd1831801c5be036635299368dfa`다.
- 중간 검증 스크립트에는 제약 우선순위·PostgreSQL RESTRICT SQLSTATE·fixture 실행 순서에 대한 잘못된 기대 때문에 실패가 있었다. 기대/fixture를 수정한 뒤 최종 62건을 다시 실행했다. 이를 애플리케이션 실패나 제품 AC 통과로 바꾸지 않는다.
- `git diff --check`는 exit 0이다. 대상 문서는 미추적이므로 이 명령만으로 대상 검증을 주장하지 않고 위 정적 검사로 대상 바이트/코드 블록도 확인했다. 보호 파일 해시 비교 구간에서 기존 Gateway Spec/Plan/기준선 및 Migration 7개는 동일했다. 편집하지 않은 `docs/gates/spec-approvals/16.md`는 `8a54cebc61bf2e4fb55326a6000daa2bae024c3be97ace0b5fe1adaa1ffe38fd`→`66c81809ce150896c7649e868a173dd95573acd1d01b016706b34ff4d9443d6a` 변경을 감지했으며 되돌리거나 편집하지 않았다.
- 위 비교 뒤 추가 관찰에서는 기존 Spec=`7831c76984893fb3e82fc18bf232d11de4c65bb79fde493e066b3c50984b727a`, 기준선 파일=`fa35106bff02da74c27e1411ae44b3e6170e6fabb636305adbf24f6cfe9fbc21`, 승인 기록16=`015a5dae336f1db7bba010905badba854d165b5dd22d84199cd9d919b85c6050`로 변경되었다. Plan과 Migration 7개는 최초 보호 snapshot과 같았다. 이 추가 변경도 이 작업의 편집에 포함하지 않으며 현재 작업 트리를 되돌리지 않았다. 보고 추가 뒤 `node C:/Users/DongGeon/AppData/Local/Temp/verify-ai-chat-spec-20261112.cjs --static-only`를 실행해 **STATIC_CHECK_PASS|27, exit 0**을 다시 확인했다. DDL 바이트는 62건 검증 시점과 동일하다.

**편집 범위:** 저장소 파일 1개 `C:/workspace_nori/nori-ai-gateway/docs/ai-gateway-to-nori-tc.md`, 저장소 밖 임시 검증 스크립트 1개 `C:/Users/DongGeon/AppData/Local/Temp/verify-ai-chat-spec-20261112.cjs`, **직접 편집 파일 총 2개**다. 다른 Spec/Plan·기준선/승인 기록·evidence·제품/테스트 코드·실제 Migration·운영 설정은 이 작업에서 편집하지 않았다. Gradle 실행의 생성물/캐시는 직접 편집 산출물이 아니다. 별도 audit·summary·Plan·Dev report 문서는 만들지 않았다.

**당시 미검증 사항(아래 §19.4가 이후 백업 정책을 대체):** 운영 DB catalog/history/DDL drift, 실제 Flyway migration/혼합 Pod 배포·rollback, Java/Spring 원자 tx/인가·서비스 배선·실제 잠금/취소/재기동, 실 TC 로그인/READ/Source/LLM·31일 후 제품 문맥·SSE/Proxy·보안 E2E·실 backup/restore와 RPO/RTO는 실행하지 않았다. 신규 제품 기능이 미구현이고 live 환경/운영 정책 근거가 이번 범위에 없기 때문이다. 모든 제품 AC·Gate/인수 체크는 NOT_RUN/미체크를 유지한다. 독립 DDL 시험은 이 항목들의 대체 증거가 아니다.

백업 관련 Q-01과 운영 백업 보존/RPO/RTO 결정 요청은 2026-11-12 사용자 지시로 철회했다. 자동 백업은 없으며 사용자 명시 요청 때만 별도 수동 백업한다. §1.2의 영구 chat 증분에 대한 승인 정본 정합과 운영 삭제/탈퇴·법정 보존 정책 확인은 별개로 남는다. 기존 승인·기준선·evidence는 이 결정으로 소급 수정하지 않는다.


### 19.4 2026-11-12 사용자 추가 지시 — 자동 백업 제거

사용자가 기존 코드·배포·CI까지 제거하도록 명시하여, 최초 Specify 전용 제한의 예외는 백업 기능 제거와 그에 직접 필요한 테스트/문서 정합 보정으로 한정했다. 영구 chat·TC 연동 신규 구현과 운영 DB/Migration·배포는 수행하지 않았다. 새 Plan·audit·대체 Spec은 만들지 않았다.

**확정 정책:** 자동 백업 없음. 사용자 명시 요청 때만 별도 수동 백업한다. 일일 백업·보관 기간·RPO/RTO 요구·Q-01 결정 요청과 백업/복원 필수 수용 leaf를 철회했다. 이전 §19.2~§19.3의 실행 기록은 당시 이력이며 현행 요구로 재사용하지 않는다.

**제거 범위:** 백업/복원 shell 및 전용 이미지, Helm 백업 CronJob·values/schema 입력, GitLab 이미지 빌드/스캔/digest 연결, 백업 DB 지표 bean·경보·경보 주입, 백업 목적지 Secret/승인 게이트와 자격 요구, 전용 IT 및 등록/빌드 입력, 수용 leaf 두 개와 restore stage/RTO 측정 생산을 제거했다. 공통 평가 레코드·수용 증거 writer 테스트는 유효한 Job 지연 측정 fixture로 바꿨다. 현재 구현 catalog는 leaf 212개·전체 235개이며 기존 이력 레코드의 해시는 소급 수정하지 않았다.

**남겨 둔 비실행 상태:** 기존 V3의 gw.backup_run 및 DB identity/epoch·RecoveryModeRouter/readiness 보호는 유지한다. 이들은 백업을 생성/예약하지 않으며 테이블 삭제·Migration 이력 조작은 이번 범위가 아니다. 별도 격리 DB 차트와 수동 복구 모드 guard의 lint는 백업/복원 실제 실행 증거가 아니다.

**편집 파일:** 저장소 내부 47개(수정 35개, 삭제 12개). 아래가 이번 작업의 전체 경로이며 기존 사용자 미추적 F0/evidence 파일은 제외한다.

| 작업 | 정확한 경로 |
|---|---|
| 수정 | `C:/workspace_nori/nori-ai-gateway/.gitlab-ci.yml` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/ci/integration-required.txt` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/docs/ai-gateway-to-nori-tc.md` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/docs/ai-rag-gateway-architecture-plan.md` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/docs/ai-rag-gateway-architecture.md` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/docs/runbooks/aigw-backup-restore.md` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/docs/runbooks/aigw-runtime-credentials.md` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/gateway-eval/src/main/resources/com/nori/ai/gateway/eval/leaf-catalog-v2.json` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/gateway-eval/src/test/java/com/nori/ai/gateway/eval/EvidenceRepositoryFsTest.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/gateway-eval/src/test/java/com/nori/ai/gateway/eval/LeafCatalogTest.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/gateway-eval/src/test/java/com/nori/ai/gateway/eval/RecordCliTest.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/gateway-eval/src/test/resources/policy/plan-catalog-contract.txt` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/modules/adapter/ai-gateway-db-adapter/src/integrationTest/java/com/nori/ai/gateway/persistence/MigrationApplyIT.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-acceptance-app/src/test/java/com/nori/ai/gateway/acceptance/EvidenceBlockWriterTest.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-app/build.gradle.kts` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-app/src/integrationTest/java/com/nori/ai/gateway/app/ops/backup/BackupColumnCoverageIT.java` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-app/src/integrationTest/java/com/nori/ai/gateway/app/ops/backup/BackupScriptIT.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-app/src/main/java/com/nori/ai/gateway/app/config/GatewayAppModuleConfiguration.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-app/src/main/java/com/nori/ai/gateway/app/observability/MetricsCatalog.java` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-app/src/main/java/com/nori/ai/gateway/app/ops/backup/BackupStatusMetrics.java` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/modules/apps/ai-gateway-app/src/test/java/com/nori/ai/gateway/app/observability/MetricLabelCardinalityTest.java` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/backup/aigw-backup.sh` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/backup/aigw-restore.sh` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/backup/job-columns-v1.txt` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/aigw-helm.sh` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/app_render.py` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/docker/Dockerfile-backup` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/helm/ai-gateway/templates/_helpers.tpl` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/helm/ai-gateway/templates/backup-cronjob.yaml` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/helm/ai-gateway/values.schema.json` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/helm/ai-gateway/values.yaml` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/runconfig-selftest.py` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/deploy/runconfig.py` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/gates/aigw-backup-alert-injection-selftest.sh` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/gates/aigw-backup-alert-injection.sh` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/gates/aigw-runtime-credentials-selftest.sh` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/gates/aigw-runtime-credentials.sh` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/gates/aigw-stage-gate-selftest.sh` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/gates/stage_gate.py` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/monitoring/aigw-backup-prometheusrule.yaml` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/runconfig/README.md` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/runconfig/fixtures/LINT-ONLY.md` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/runconfig/fixtures/lint-image-digests.json` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/runconfig/fixtures/lint-verify.json` |
| 삭제 | `C:/workspace_nori/nori-ai-gateway/operations/runconfig/negative-missing-backup.json` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/runconfig/negative-missing-pvc.json` |
| 수정 | `C:/workspace_nori/nori-ai-gateway/operations/runconfig/verify.schema.json` |

저장소 밖 직접 생성·편집한 파일은 아래 임시 스크립트 3개다(이 작업 전체 기준, 문서 재검증 1개 + 백업 제거/정책 정리 2개). 따라서 직접 편집·삭제 파일 합계는 50개다. Gradle build 산출물·컨테이너의 자동 임시 fixture/도구 이미지 계층은 직접 편집 파일에 포함하지 않는다.

- `C:/Users/DongGeon/AppData/Local/Temp/verify-ai-chat-spec-20261112.cjs`
- `C:/Users/DongGeon/AppData/Local/Temp/remove-ai-gateway-autobackup.cjs`
- `C:/Users/DongGeon/AppData/Local/Temp/update-ai-gateway-backup-policy.cjs`

**검증 범위와 제한:** 운영 DB 연결/변경·실제 백업/복원·배포·GitLab CI 실행은 하지 않았다. kubectl current-context가 설정되지 않아 이미 배포된 CronJob·Secret·PrometheusRule의 존재/삭제 여부는 확인하거나 변경하지 못했다. 현재 저장소에서 백업을 생성/예약하는 경로를 제거한 결과이며 실제 클러스터 변경 완료로 표현하지 않는다. 기존 chat AC의 제품 검증은 미완료 상태를 유지한다.

**실행한 검증(로컬 한정):**

| 검증 | 실행 명령/방법 | 결과 및 한계 |
|---|---|---|
| 배포 입력/실제 Helm 경계 | `python3 -B operations/deploy/runconfig-selftest.py` | PASS 23건. 실제 Helm, 임시 합성 승인 fixture만 사용 |
| 런타임 자격/배포 경계 | `bash operations/gates/aigw-runtime-credentials-selftest.sh` | PASS 40건. 실제 Helm·git, kubectl/upgrade/status는 stub이며 실제 Secret 접근 없음 |
| stage gate | `bash operations/gates/aigw-stage-gate-selftest.sh` | 최종 PASS 27건. helm/kubectl/gradlew는 stub. 철회된 restore suite 3개는 STAGE_INVALID·외부 호출 0 |
| Helm lint/렌더 | `bash operations/deploy/aigw-helm.sh --release <ai-gateway 또는 ai-gateway-restore> --mode lint --runconfig operations/runconfig/fixtures/lint-verify.json --image-digests operations/runconfig/fixtures/lint-image-digests.json --suite none`; DB 차트는 `--release ai-gateway-db-restore`와 image/suite 인자 생략 | 3개 release 모두 LOCAL_CHART_OK. 앱 배포 행렬의 CronJob 0·기존 보안 음성 guard 검증. 실제 배포·DB 복원 아님 |
| 폐기 입력 | 두 JSON Schema의 additionalProperties 경계 검사 | backup 입력 거부 확인. runconfig selftest에서도 옛 backup 필드 거부 확인 |
| 구문/형식 | Python AST 8개·runconfig JSON·GitLab YAML 파싱, 변경 shell 4개 각각 bash -n, 편집 파일 UTF-8/LF·git diff --check | 모두 통과. GitLab 서버의 CI lint/실제 pipeline 실행 아님 |
| chat Spec 정적 회귀 | `node C:/Users/DongGeon/AppData/Local/Temp/verify-ai-chat-spec-20261112.cjs --static-only` | STATIC_CHECK_PASS 27. 이전 §19.3의 격리 PostgreSQL DDL 62건 결과는 당시 실행 기록이며 이번 백업 제거로 DDL을 변경하거나 재실행하지 않음 |
| 초기 관련 Java/컴파일 | `:gateway-eval:test`의 LeafCatalogTest·RecordCliTest, `:apps:ai-gateway-app:test`의 MetricLabelCardinalityTest·ModuleConfigurationCompletenessTest, 앱·DB adapter의 `compileIntegrationTestJava` | 아키텍처 문서 개정 전 실행에서 12건 실패/skip 0 및 두 IT 소스 컴파일 통과, BUILD SUCCESSFUL. 삭제한 백업 IT는 실행하지 않음 |
| 승인 기준선 guard | `:gateway-eval:run --args="spec-guard --spec C:/workspace_nori/nori-ai-gateway/docs/ai-rag-gateway-architecture.md --baseline docs/gates/spec-baseline.md"` | 착수 때 exit 0. 정책 문서 수정 뒤 SPEC_BASELINE_MISMATCH(하위 CLI exit 3, Gradle exit 1). 기존 승인/기준선을 소급 변경하지 않아 생기는 미충족 상태이며 우회/승인 처리하지 않음 |

초기 좁은 Java 실행에서 철회된 leaf의 예외 코드 기대값 1건을 수정했고, 이후 전체 eval 회귀 시도에서는 남아 있던 restore 측정 fixture 1건을 확인해 유효한 Job 지연 측정으로 교체했다. 전체 eval 회귀에서는 현행 승인 Spec을 읽는 ArgLimitsSchemaTest·F0BoundaryCasesV2Test도 기준선 불일치로 실패하여 실행을 중단했다. 전체 회귀 완료/통과로 보고하지 않는다. 별도 최종 관련 클래스 재실행과 원시 JUnit·증거 검증 결과는 아래에 구분해 기록한다.

**최종 관련 Java 재실행:** `gradlew.bat :gateway-eval:test --tests com.nori.ai.gateway.eval.LeafCatalogTest --tests com.nori.ai.gateway.eval.RecordCliTest --tests com.nori.ai.gateway.eval.EvidenceRepositoryFsTest :apps:ai-gateway-app:test --tests com.nori.ai.gateway.app.observability.MetricLabelCardinalityTest --tests com.nori.ai.gateway.app.ModuleConfigurationCompletenessTest :apps:ai-gateway-acceptance-app:test --tests com.nori.ai.gateway.acceptance.EvidenceBlockWriterTest --continue --no-daemon`을 현행 아키텍처 Spec 경로의 AIGW_SPEC_FILE로 실행했다.

원시 JUnit XML 대조 결과는 eval 10건·app 6건·acceptance writer 4건, **합계 20건 실패/오류/skip 0**이다(2026-11-12 UTC 03:57~03:58, KST 12:57~12:58). 다만 세 모듈의 `testLeafEvidence`가 모두 SPEC_BASELINE_MISMATCH로 실패하여 **Gradle 전체는 BUILD FAILED, exit 1**이다. 증거 생성 생략·임시 승인 기준선으로 우회하지 않았으며 단위검사 통과와 공식 증거 검증 미충족을 구별한다.

직후 별도 `gradlew.bat :gateway-eval:run --args="test-evidence verify --tasks :gateway-eval:test,:apps:ai-gateway-app:test,:apps:ai-gateway-acceptance-app:test" --no-daemon`도 실행했다. 결과는 SPEC_BASELINE_MISMATCH(하위 CLI exit 3, Gradle exit 1, BUILD FAILED in 40s)로 TEST_EVIDENCE_VERIFIED에 도달하지 못했다. 보호된 baseline/seq16 승인 기록의 SHA-256은 착수 기록과 동일하며, 이 상태를 해결했다고 표기하지 않는다. 최신 아키텍처 문서의 SHA-256은 `731915ccd974dcf2b716b2cc8d1c65113acdf27a47cd61d910881e04e97efd3e`다.

**신규 구현/외부 확인:** 영구 chat 7테이블·접수/종결 원자 projection·소유권/현재 권한 재검증·TC Backend/Front 실제 연동은 여전히 신규 구현 사항이다. 운영 삭제/탈퇴·법정 보존, 실제 Source/issuer 계약과 chat 증분의 승인 정본 정합 확인은 남는다. 백업에 관한 추가 사용자 선택이나 승인 요청은 남기지 않는다. 자동 백업 제거를 새 영구 대화 기능의 제품 수용 또는 실환경 배포 완료로 간주하지 않는다.

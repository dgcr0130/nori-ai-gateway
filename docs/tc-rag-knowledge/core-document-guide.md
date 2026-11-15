# nori-tc LLM·RAG 운영 핵심 문서 준비 가이드

이 문서는 nori-tc를 LLM과 RAG로 조회·분석·진단하기 위해 어떤 문서가 필요한지, 기존 레포에서 어떤 자료를 가져올지, 무엇을 새로 작성할지, 문서를 어떻게 최신화해 SeaweedFS에 반입할지를 정리한다.

TC 전체에서 재사용할 시스템 구조·프로토콜·메시지·업무 흐름·진단 지식을 공통 문서로 관리한다. 기존 자료는 현행 코드와 대조해 보완하고 부족한 지식만 추가한다. 설비 한 대마다 문서나 RAG를 따로 만들지 않는다. 실제 계약 차이가 있는 경우에만 프로토콜·모델·버전 등의 적용 조건과 차이점을 기록한다. 정리한 문서는 소유권·적용 버전·권한을 확인한 승인 목록으로 관리한다.

상태: 사용자 요청에 따른 **문서 준비 가이드**. 문서 승인 목록, 지원 형식 확정, P22 구현 완료 또는 실제 색인 완료가 아니다. 기존 조사 목록을 이 문서로 옮겼으며 `docs/gates/f3/corpus-candidates.md`에 중복본을 두지 않는다.

조사일: 2026-11-13. 로컬 `nori-tc-*` 9개 레포를 기준으로 기존 공통 문서를 정적으로 대조·수정했다. 문서별 확인 범위와 기준 커밋은 [Architecture 인덱스](C:/workspace_nori/nori-tc-exec/docs/Architecture/README.md)에 기록한다. SeaweedFS·운영 DB·실제 배포 상태는 조회하지 않았고 빌드/기능 시험은 실행하지 않았다. 모든 문장의 정확성이나 현장 업무 계약을 전수 검증한 것은 아니며, 이번 수정이 반입 승인을 의미하지 않는다.

## 1. 사용자 결정과 이번 목록의 범위

| 시스템 | 사용할 버킷 | 이번 작업 범위 |
|---|---|---|
| nori-tc | nori-tc | TC의 최초 문서 후보 준비 |
| nori-mes | nori-mes | 향후 해당 시스템 온보딩 때 별도 문서/권한/catalog 준비 |
| nori-hrm | nori-hrm | 향후 해당 시스템 온보딩 때 별도 문서/권한/catalog 준비 |

버킷 분리와 함께 서버가 검증한 시스템 소유권, 문서 승인 목록, 검색 공간과 현재 사용자 인가를 유지한다. TC 채팅에서 MES/HRM 버킷을 검색하지 않는다. TC 문서가 MES와의 인터페이스를 설명하는 것과 MES 시스템 내부 문서를 TC에 가져오는 것은 다르다. `nori-mes`를 기존 `WM`과 같은 시스템으로 간주하거나 이번에 새 Connector/enum을 구현하지 않는다.

현재 문서는 아직 버킷에 넣지 않은 상태라는 사용자 설명을 기준으로 한다. 최초에는 TC 업무·통신·진단 지식을 준비하고, LLM/Gateway 운영 절차와 평가 정답 자료는 별도 관리한다. 이 후보 목록 자체는 검색 corpus에 넣지 않는다. 기동/재기동 때만 수집한다는 Spec seq18은 유지한다.

### 1.1 지식 관리 단위와 실제 설비 조회의 구분

- 문서의 기본 단위는 시스템 기능·프로토콜·메시지 계약·업무 절차·오류/진단 규칙이다. 설비 ID나 대표 설비를 문서 작성/색인 단위로 삼지 않는다.
- 같은 규칙을 사용하는 설비는 같은 승인 문서를 재사용한다. 같은 규칙의 설비가 추가되어도 그 설비용 문서를 복제하거나 공통 문서 전체를 다시 넣지 않는다.
- 실제 규칙이 다르면 공통 문서 안의 조건별 설명 또는 별도 계약 문서로 차이를 관리한다. 구분 기준은 의미 있는 계약/프로토콜/버전 차이이며 설비 대수가 아니다. 공통 설명을 장비별로 중복 작성하지 않는다.
- 설비 ID에 연결된 현재 모델·파라미터·상태·WORK/LOT·로그는 권한이 검증된 라이브 Source로 조회한다. 필요한 공통 지식의 적용 범위를 그 조회 결과와 대조하며, 동적 상태를 설비별 지식 문서로 복제하지 않는다.
- 시험은 기능·프로토콜·정상/오류 경로를 구분해 사례를 선정한다. 시험용 설비/질문을 선정하는 것은 검증 수단이며 제품을 그 설비로 한정하거나 문서 준비의 필수 첫 단계로 요구하는 것이 아니다.

## 2. 기존 자료에서 가져올 후보

표의 C 번호는 준비 목록 식별자이며 새 Source ID나 실제 저장 key가 아니다. 각 묶음은 한 파일로 합치라는 뜻이 아니며, 독립된 문서는 제목과 출처를 유지한다. 공통 원본 기준 경로는 `C:/workspace_nori/nori-tc-exec/docs/Architecture/`다. **그대로 업로드 승인을 뜻하는 항목은 없다.**

| ID | 우선순위 | 필요한 지식 | 실제 원본 | 복사 전 확인/보완 |
|---|---|---|---|---|
| C01 | 우선 | TC 역할·구성·종단 메시지 흐름 | [시스템 정의](C:/workspace_nori/nori-tc-exec/docs/Architecture/00-system/01-what-is-nori-tc.md), [런타임 구성](C:/workspace_nori/nori-tc-exec/docs/Architecture/00-system/03-runtime-topology.md), [종단 흐름](C:/workspace_nori/nori-tc-exec/docs/Architecture/00-system/04-end-to-end-dataflow.md) | gateway/business/ui 각 책임과 인바운드·아웃바운드·실패 경로를 현행 코드로 확인. 배포 수량/주소는 실제 운영값이라고 단정하지 않음. |
| C02 | 우선 | EQP·LOT·PORT·WORK·MODEL·통신 용어 | [용어집](C:/workspace_nori/nori-tc-exec/docs/Architecture/00-system/05-glossary.md) | ACTIVE/PASSIVE와 포트 공유 설명을 현행 코드에 맞게 정정. 현장 용어·별칭·약어는 N01에서 보완. |
| C03 | 우선 | 설비 START/END·연결·타임아웃·desired/runtime 상태 | [설비 라이프사이클](C:/workspace_nori/nori-tc-exec/docs/Architecture/20-gateway/03-equipment-context-and-lifecycle.md) | 요청 접수와 실제 완료를 구분. 상태 전이·타임아웃·재연결·소유권 fence를 코드와 확인. |
| C04 | 우선 | 메시지 종류·발행/구독 방향·키·필수 필드·응답 | [토픽/봉투](C:/workspace_nori/nori-tc-exec/docs/Architecture/90-contracts/01-kafka-topics-and-envelope.md), [eventType/payload](C:/workspace_nori/nori-tc-exec/docs/Architecture/90-contracts/02-kafka-event-types-and-payloads.md) | 현행 serializer/consumer와 버전·traceId/correlationId·필수 필드 대조. 여기서 다루는 것은 TC 업무 Kafka 계약이며, 후속으로 미룬 RAG 변경 알림 토픽과 다름. |
| C05 | 우선 | 워크플로우 매칭·필터·액션 선택·중복 방지 | [매칭](C:/workspace_nori/nori-tc-exec/docs/Architecture/30-business/04-workflow-matching.md), [실행/레지스트리](C:/workspace_nori/nori-tc-exec/docs/Architecture/30-business/05-workflow-dispatch-and-registry.md) | messageName·SECS 키·workflow_filter·param 우선순위·plugin/core 해석·checkpoint를 확인. 실제 모델 설정에 따라 분기가 달라짐을 명시. |
| C06 | 우선 | action_data_index·MDF·변환 규칙 | [MDF/필드 조립](C:/workspace_nori/nori-tc-exec/docs/Architecture/30-business/06-action-data-index-and-mdf.md) | 입력 위치·필드 우선순위·transform 실패·출력 형식과 식별자 보존을 현행 구현으로 확인. |
| C07 | 우선 | TCAction별 선행조건·상태 변경·발행 결과 | [코어 액션](C:/workspace_nori/nori-tc-exec/docs/Architecture/30-business/07-core-actions.md) | 액션 목록/개수와 개별 조건을 최신 코드로 갱신. placeholder/미구현 설명을 정상 업무 계약으로 쓰지 않음. |
| C08 | 우선 | WORK/CONTROLJOB/PROCESSJOB·DCSPEC·DCOP·DATACOLL | [수집/WORK](C:/workspace_nori/nori-tc-exec/docs/Architecture/30-business/09-datacoll-and-work.md), [Redis 키](C:/workspace_nori/nori-tc-exec/docs/Architecture/90-contracts/03-redis-key-namespaces.md) | legacy/MAT 경로·상태 전이·완료/정리·키 수명·반복 메시지 처리 대조. 업무 관점의 공통 정상 절차와 조건별 분기는 N03에서 보완. |
| C09 | 해당 프로토콜 사용 시 우선 | HSMS/SECS-II 세션·메시지·타이머 | [HSMS](C:/workspace_nori/nori-tc-exec/docs/Architecture/20-gateway/05-hsms-protocol.md) | 지원 범위와 raw body 처리 한계를 명시. 이 문서만으로 제조사별 SVID/CEID·장비 ICD가 충족된다고 보지 않음. |
| C10 | 해당 프로토콜 사용 시 우선 | SOCKET 프레이밍·인코딩·socketType·플러그인 | [SOCKET](C:/workspace_nori/nori-tc-exec/docs/Architecture/20-gateway/06-socket-protocol-and-plugins.md), [Gateway Action 안내](C:/workspace_nori/nori-tc-gateway-action/README.md) | ACTIVE/PASSIVE와 bindingSignature·JAR 호환·구분자/인코딩 확인. 공개 계약은 gateway의 action-contract가 정본이며 action 레포는 구현/보조 역할임을 유지. |
| C11 | 해당 프로토콜 사용 시 우선 | PLC/Modbus 폴링·주소·타입·timeout | [PLC](C:/workspace_nori/nori-tc-exec/docs/Architecture/20-gateway/07-plc-protocol.md) | 지원 PLC 프로토콜·레지스터 해석·그룹/주기·바이트/워드 순서를 확인. 실제 장비 주소표는 N05가 필요. |
| C12 | 우선 | traceId·eqpId·로그/메시지 상관·관측 경계 | [로깅/MDC](C:/workspace_nori/nori-tc-exec/docs/Architecture/10-common/10-logging-and-mdc.md), [HTTP 계약](C:/workspace_nori/nori-tc-exec/docs/Architecture/90-contracts/06-service-http-contracts.md) | 어떤 ID로 어느 구간을 추적할 수 있는지 설명. 실제 로그 원문/토큰을 넣지 않으며 현재 상태는 라이브 Source에서 확인. |
| C13 | 후속 보강 | 설비·모델·배포 관리 절차 | [설비 관리](C:/workspace_nori/nori-tc-exec/docs/Architecture/40-ui-backend/06-eqp-management.md), [배포 모델/시나리오](C:/workspace_nori/nori-tc-exec/docs/Architecture/40-ui-backend/07-deploy-model-and-scenario.md), [배포 실행](C:/workspace_nori/nori-tc-exec/docs/Architecture/40-ui-backend/08-deploy-execution.md) | 운영자의 조회/확인 절차를 추출. 변경/배포 방법 설명이 AI 실행 권한을 의미하지 않음. |
| C14 | 후속 보강 | 웹 화면·비동기 작업·권한 오류 안내 | [사용자 매뉴얼](C:/workspace_nori/nori-tc-exec/docs/completion-report/07-user-manual.md), [비동기 작업 추적](C:/workspace_nori/nori-tc-exec/docs/Architecture/50-ui-front/08-async-operation-tracking.md), [인증/권한](C:/workspace_nori/nori-tc-exec/docs/Architecture/40-ui-backend/03-authentication-and-authorization.md) | 현재 프런트 API·화면·권한과 대조 후 사용자용 부분만 채택. 매뉴얼의 브라우저/성능 등 서술을 이번에 실측 검증한 것은 아님. |
| C15 | 후속 보강 | 모델/JAR 플러그인 적용·버전 호환 | [JAR 생명주기](C:/workspace_nori/nori-tc-exec/docs/Architecture/70-action-sdk/06-plugin-jar-lifecycle.md), [Business Action 안내](C:/workspace_nori/nori-tc-business-action/README.md) | SDK/package/descriptor·digest·plugin→core→fail 규칙 확인. 실제 적용 JAR/모델은 N02에 결속. |
| C16 | 운영자 전용 후보 | 설정 의미·기동·배포/롤백·데이터 소유권 | [설정 파일](C:/workspace_nori/nori-tc-exec/docs/Architecture/80-exec/05-configuration-files.md), [DB 소유권](C:/workspace_nori/nori-tc-exec/docs/Architecture/90-contracts/05-database-tables-and-ownership.md), [운영/롤백 개요](C:/workspace_nori/nori-tc-exec/docs/Architecture/80-exec/10-operations-scripts-and-rollback.md), [롤백 절차](C:/workspace_nori/nori-tc-exec/docs/deploy/rollback-procedure.md) | 운영 실측값·관리 명령·백업/복원 절차를 별도 검토하고 운영자 권한으로 제한. TC 운영 문서를 AI Gateway 자동 백업/복원 승인으로 해석하지 않음. |

## 3. 새로 작성하거나 업무 관점으로 재구성할 문서

기존 코드/문서는 작성 근거가 될 수 있지만 현장 업무 의미·규칙의 적용 조건을 모두 제공하지 않는다. 코드에서 확인되지 않는 항목은 문서 소유자가 확인해야 한다. 아래 항목은 부족한 지식 영역을 보완하기 위한 것이며 설비/모델마다 문서를 나누라는 뜻이 아니다. 기존 승인 자료로 충분하면 재사용한다.

2번 작업에서는 기존 구조 문서와 실제 호출 흐름을 근거로 N01~N06을 `nori-tc-exec/docs/Architecture/95-business-knowledge/`에 공통 주제별 초안으로 작성하고 범위를 한정한 독립 정적 대조를 완료했다. 2026-11-16의 3번 작업에서는 기존 121개와 신규 6개를 함께 검색·비교하고, 발견한 충돌을 소스와 대조하여 정정했다. 실제 확인 범위는 [Architecture 통합 검토 기록](../../../nori-tc-exec/docs/Architecture/README.md)을 따른다. 아래 링크는 해당 정본의 작업본이며 업무 승인·SeaweedFS 반입 승인을 의미하지 않는다. 현장 용어·제조사 ICD·실제 적용 상태 등 코드로 확정할 수 없는 내용은 각 문서의 미확인 항목과 확인 책임에 남겼다. N07은 후속 제안으로 유지한다.

| ID | 공통 업무 문서 / 후속 제안 | 반드시 담을 내용 | 작성 근거/확인 책임 |
|---|---|---|---|
| N01 | [tc-business-terms.md](../../../nori-tc-exec/docs/Architecture/95-business-knowledge/tc-business-terms.md) | 현장 용어·별칭·정식 메시지/동작의 관계, 적용 모델/버전, 모호하거나 미지원인 용어. 특히 TKIN/TKOUT의 업무 정의와 어떤 조건에서 어떤 TC 메시지에 대응하는지. | C02와 현장 담당자 확인. 이번에 확인한 business main Java 검색에서 TKIN/TKOUT 문자열 매핑은 찾지 못했으므로 `TKIN = WO_EQP_START_REQ`로 추정하지 않음. |
| N02 | [tc-applicability-rules.md](../../../nori-tc-exec/docs/Architecture/95-business-knowledge/tc-applicability-rules.md) | 프로토콜·모델 기능·버전 등에 따라 공통 규칙이 달라지는 조건, 적용 문서/계약의 선택 기준과 효력 범위. | C05~C11/C15와 실제 승인 계약. 설비별 현재 모델 바인딩·설정은 라이브 Source로 조회하고, 이 문서는 그 결과를 해석할 공통 기준을 설명. 설비별 설정표를 지식 문서로 복제하지 않음. |
| N03 | [tc-normal-flows.md](../../../nori-tc-exec/docs/Architecture/95-business-knowledge/tc-normal-flows.md) | TC 공통 업무의 정상 절차와 조건별 분기. 각 단계의 시작 조건·송신자/수신자·메시지/필수 필드·기대 상태·완료 조건·예외·관측 항목. 타임아웃은 확인된 값만. | C03~C11과 액션 코드·테스트·업무 계약. 실제 모델 설정에 따라 선택되는 경로를 설명하고 설비별 같은 절차를 복제하지 않음. [시뮬레이터 예제](C:/workspace_nori/nori-tc-simulator/orchestrator/src/main/resources/scenario/new-normal-flow.scenario.md)는 작성 보조이며 모든 계약의 정답/시간 기준은 아님. |
| N04 | [tc-troubleshooting.md](../../../nori-tc-exec/docs/Architecture/95-business-knowledge/tc-troubleshooting.md) | 연결 실패, START 대기, 오류 없이 작업 정체, workflow 미매칭, 메시지 누락, DATACOLL 미완료, PORT/WORK 불일치, 배포 대기 등의 증상별 점검. 관측 사실→가능 원인→구별할 확인 항목→조치/담당자 순서. | C01~C13와 현재 실패 분기. 검증된 사례와 가설을 구분하고 재시작/재전송/삭제를 무조건 해결책으로 쓰지 않음. |
| N05 | [tc-protocol-contracts.md](../../../nori-tc-exec/docs/Architecture/95-business-knowledge/tc-protocol-contracts.md) | 프로토콜/계약 단위의 인터페이스 정의: SOCKET 명령/응답·프레임·인코딩, SECS 항목/이벤트, PLC 레지스터·단위·배율·endianness 등. 실제 차이가 있는 계약의 적용 firmware/model/version을 명시. | 제조사/고객/설비 담당자의 정본과 현재 플러그인/모델. 동일 계약은 여러 설비가 공유하며 설비마다 ICD를 만들지 않음. 필요한 기존 ICD가 있으면 재사용. 코드만으로 없는 계약을 추정하지 않음. |
| N06 | [tc-error-diagnostics.md](../../../nori-tc-exec/docs/Architecture/95-business-knowledge/tc-error-diagnostics.md) | 실제 errorCode/DLQ 사유·발생 조건·담당 모듈·사용자 증상·확인 항목·처리/재시도 정책. 오류 코드가 없어도 점검할 경로. | [Gateway DLQ 사유 코드](C:/workspace_nori/nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java), 각 모듈의 정규화 오류·검증/실패 분기·N05. enum이 존재한다는 사실과 특정 현장 근본 원인은 구분. |
| N07 | `tc-incident-cases.md` | 재현 조건·관측 근거·확정 원인·처리·검증 결과·적용 버전이 있는 승인 사례. | 실제 검증/운영 사례가 생기면 후속 작성. 로그 원문·사용자 정보·자격 값을 그대로 넣지 않음. 초기 준비의 필수 차단 항목으로 삼지 않음. |

먼저 C01~C08/C12와 TC가 지원하는 프로토콜의 공통 문서를 검토·보완하고, 부족한 용어·정상 흐름·적용 조건·진단 지식을 N01~N06의 주제별로 보완한다. 대표 설비 선정이나 설비별 문서 작성은 착수 조건이 아니다. 기존 공통 규칙으로 설명되지 않는 계약 차이가 있을 때만 해당 내용을 추가하고 이미 있는 공통 문서를 반복 반입하지 않는다. 코드/자료에서 확인되지 않은 계약의 의미나 실제 진단 품질을 확인된 것으로 쓰지 않는다.

## 4. RAG/LLM 운영을 위해 별도로 준비할 자료

다음 자료는 운영·설정·평가용이다. 일반 TC 사용자 검색 corpus에 자동 포함하지 않는다. 운영자 검색에 필요하면 별도 승인/권한으로 필요한 설명 부분만 등록한다.

| 준비 자료 | 기존 근거/현재 상태 | 추가할 내용 |
|---|---|---|
| 기동 동기화 운영 절차 | [Spec §8.3.1](C:/workspace_nori/nori-ai-gateway/docs/ai-rag-gateway-architecture.md:688), seq18. 구현 전. | 기동 순서·진행 상태·완료/실패 판정·다음 재기동 재처리·문서 추가/삭제·긴 기동 시 확인 절차. 실제 구현에 맞춰 P24/P25에서 완성. |
| 모델/검색/분할 프로필 | P22 산출 예정. `bge-m3`, cosine·Hybrid/Reranker 사용 방침은 확정, 실제 프로필 일부 미확정. | 생성/임베딩/재순위화 모델을 구분하고 버전/digest·차원·예산·분할/마스킹 규칙·후보/최종 개수·기준을 기록. 미측정 값을 확정하지 않음. |
| 추론 서버·DB 운영 안내 | [추론 운영 안내](C:/workspace_nori/nori-ai-gateway/docs/runbooks/inference-server-operations.md), [DB 선행조건](C:/workspace_nori/nori-ai-gateway/docs/runbooks/aigw-db-server-prerequisites.md) | 빌려 쓰는 서버의 변경 금지 범위·읽기 전용 확인·모델/DB 가용성 판정. 비밀값 없이 절차/Secret 참조만. |
| 문서 승인 catalog와 버킷/권한 설정 | P22 승인 목록 미작성. 후보와 승인본을 구분. | 실제 객체 key/sourceRef·시스템 owner·requiredPermission·검색 공간·revision 관리·문서/모델 적용 범위. 임의 permCode를 만들지 않고 Backend 정본에 매핑. |
| 평가 질문/정답 근거 목록 | P22 corpus-goldenset와 품질/성능 기준 제안 예정. | 정상·오류·무오류 정체·모호·권한 없음·문서 밖 질문의 기대 근거/유형. **평가 정답 자료는 검색 corpus에 넣지 않음.** 모델/기준 선정용 자료와 최종 품질 평가도 구분. |
| 품질·기동 시간·자원/실패 점검 | 실제 성능/품질 기준 미확정, 시험 도구 일부 후속. | 최종 갱신 시각·문서 수/성공/실패·검색 품질/지연·기동 시간·CPU/메모리·readiness·실패 시 담당자. 수치는 corpus 조사·실측을 근거로 정함. |

## 5. 발견 사항과 반입 전 확인 상태

1. **ACTIVE/PASSIVE 설명 정정.** C02 [용어집](C:/workspace_nori/nori-tc-exec/docs/Architecture/00-system/05-glossary.md)은 [ConnectionMode](C:/workspace_nori/nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/ConnectionMode.java)와 실제 Netty 분기에 맞춰 ACTIVE=outbound connect, PASSIVE=listener로 수정했다. PASSIVE 리스너 공유 키와 SOCKET bindingSignature 조건도 함께 정정했다.
2. **문서 기준 시점과 검증 깊이.** [Architecture 인덱스](C:/workspace_nori/nori-tc-exec/docs/Architecture/README.md)에 현행 기준 커밋과 문서별 대조 범위를 기록한다. 핵심 계약 대조와 참조·변경 영향 확인을 구분하며, 모든 문서의 모든 설명이 전수 검증됐다는 의미로 사용하지 않는다. 반입할 판본에는 source commit과 확인 날짜를 남긴다.
3. **옛 경로 링크 정정.** Gateway/Business app README의 `libs/comm`, `libs/business`, `docs/Architecture/business` 같은 이전 경로와 잘못된 진입 링크를 현행 경로로 정정했다. 앱 하위 과거 설계·계획은 현행 정본과 구분하며, 반입 후보의 최종 판본·링크는 승인 때 다시 확인한다.
4. **시나리오와 업무 규정 구분.** normal-flow 파일은 실제 시뮬레이터 자원이지만 특정 샘플이다. 모든 장비의 유일한 정상 흐름이나 운영 timeout으로 복사하지 않는다. `WO_EQP_START_REQ`의 실제 액션은 기존 WorkEntry 유무에 따라 CREATE_WORK를 재사용하고 발행은 공통 workflow 경로에 맡긴다. 메시지 이름만 보고 단일 직선 흐름을 만들지 않는다.
5. **정적 설명과 현재 관측 구분.** 문서에는 상태 의미·조회 방법·정상 조건을 넣는다. 현재 접속 여부·최신 WORK/LOT·최근 오류·실제 배포 버전은 라이브 Source로 확인하며 오래된 스냅샷을 현재 사실로 답하지 않는다.

## 6. 문서별 최소 작성 항목

- 문서 식별자·제목·소유 시스템·담당자/검토자·승인 상태.
- 적용 모델/프로토콜/버전·유효 시점. 공통 설명과 현장별 규칙을 구분.
- 정의 또는 정상 조건 → 처리 순서 → 기대 결과 → 실패/예외 → 확인 항목.
- 원본 레포·파일 경로·기준 commit·마지막 코드/업무 확인 날짜.
- 실제 저장 key와 검색 권한은 승인 catalog에서 관리. 제목만으로 권한을 추정하지 않음.
- 표는 헤더·단위·행 의미를 유지하고 절차는 선행 조건과 조치를 함께 작성.
- 비밀값·실제 개인 정보·환경변수 값은 제외. 비밀 파일을 복사하지 않음.
- 현재 권장 준비 형식은 UTF-8 Markdown. 지원 형식의 최종 확정은 실제 corpus 조사 뒤 별도로 수행하며 이 제안을 형식 승인으로 처리하지 않음.

## 7. 조사 기준과 검증 한계

| 레포 | 확인한 로컬 HEAD |
|---|---|
| nori-tc-common | 6fe71f6b2da6 |
| nori-tc-gateway | d3636ab86763 |
| nori-tc-gateway-action | f4fdfd6d27ce |
| nori-tc-business | 7db24f7466bd |
| nori-tc-business-action | 3552180fbd86 |
| nori-tc-ui-backend | 985d17c604dd |
| nori-tc-ui-front | 9fe69a75dcd3 |
| nori-tc-exec | f1d5e0a8e727 |
| nori-tc-simulator | 5e9b02e3ae16 |

문서 파일의 존재/추적 상태와 주요 샘플 호출 경로를 확인했다. gateway 연결 모드·상태 전이/사유 코드, business 매칭→dispatch/레지스트리·WORK 전이·시작 액션, common 메시징/MDC 문서, ui-backend 권한 캐시, front 요청/CSRF, 두 action 레포 안내, simulator 정상 흐름을 대조했다. 모든 문서·모든 코드·모든 모델을 검증한 것은 아니다.

최초 조사 결과는 기존 `docs/gates/f3/corpus-candidates.md`에서 이 가이드로 이동하고 `docs/README.md`에 진입 링크를 추가했다. 후속 1번 작업에서 기존 TC Architecture Markdown을 정정했다. 2번 작업에서는 N01~N06의 공통 업무 지식을 작성·코드 대조하고, 기존 DATACOLL 설명에 legacy/MAT 적용 조건을 보완했다. 3번 작업에서는 전체 127개의 문서 간 주제를 비교하고 MES 추적 식별자·필수값, START/END 확정과 상태 쓰기 책임, 액션 예외의 래핑·처분, 비동기 원장 우선 조회·레거시 fallback·HTTP 응답 설명을 정정했다. 단일 문서 제목과 절 계층도 보완했다. 이는 주요 경계의 정적 확인이며 모든 API·예제·메서드의 전수 검증이나 실제 RAG 검색 시험은 아니다. 소스 주석과 실제 동작의 차이, 업무·장비 계약의 미확인 항목은 Architecture 인덱스에 남겼다. 1~3번 작업에서는 제품 코드·소스 주석·Spec·Plan·기준선을 변경하지 않았다. 4번 작업에서는 승인 Spec seq18을 유지하며 기존 Plan을 개정했고, 사용자 승인 전 상태로 관리한다. 문서 복사·실제 업로드·버킷 생성·권한 부여·색인·모델 호출·운영 명령은 실행하지 않았다. 실제 문서 목록을 승인 catalog로 구체화하고 지원 형식·문서별 권한·평가 기준을 순서대로 정한다. 대표 설비나 모델을 먼저 지정해야 공통 문서를 검토할 수 있다는 선행조건은 두지 않는다.

## 8. 전체 문서 최신화·보완 원칙

### 8.1 기존 문서는 현행 코드와 업무 기준을 확인한 뒤 가져온다

1. C01~C16에서 필요한 문서를 고르고 해당 문서의 원본 레포·기준 commit을 기록한다.
2. 문서에 나온 클래스·설정·메시지·상태 전이·권한·API를 현재 코드와 대조한다. 호출자와 피호출자가 다른 레포에 있으면 양쪽 계약을 확인한다.
3. 실제 동작이 달라진 설명, 옛 경로, 예제와 운영값의 혼용, 중복·상충 문구를 정정한다. 코드에 없는 동작을 추정해 채우지 않는다.
4. N01~N06의 주제별 공통 자료로 선행조건·정상 절차·실패 분기·확인 방법을 보완한다. 실제 규칙 차이에는 적용 조건을 명시하고, 코드로 알 수 없는 업무 의미와 장비 계약은 담당자 확인을 받는다.
5. 문서별 검토자·확인 날짜·적용 버전을 남기고 승인한 판본만 SeaweedFS 반입 후보로 확정한다.

첫 반입 전에는 선택한 전체 문서를 점검한다. 이후에는 코드·메시지 계약·모델·플러그인·업무 절차가 바뀔 때 영향받는 문서를 함께 갱신한다. 일정 주기를 임의로 확정하는 대신 변경 시 담당자가 관련 문서를 확인하도록 한다.

### 8.2 복사본이 정본에서 뒤처지지 않게 관리한다

- 기본적으로 원본 레포의 검토된 문서를 정본으로 삼고 SeaweedFS에는 승인된 배포본을 둔다. 여러 복사본을 서로 다른 내용으로 독립 수정하지 않는다.
- 기존 문서를 그대로 활용할 수 있으면 원본의 제목·절·표·출처를 보존해 복사한다. 여러 자료를 합쳐 새 운영 문서로 작성했다면 근거 문서와 적용 범위를 명시한다.
- 새 업무 문서는 담당자와 정본 보관 위치를 정하고 관리한다. 이 가이드의 제안 파일명을 실제 생성/승인된 문서로 오인하지 않는다.
- 판본 교체 시 문서 식별자·적용 모델·버전·효력 시점을 관리한다. 과거 문서를 남길 때는 적용 버전을 분명히 하고 최신 문서처럼 검색되지 않도록 승인 목록과 검색 정책에 반영한다.
- 승인 목록에서 제외한 문서는 다음 Gateway 기동 동기화에서 검색 대상에서 제외되도록 관리한다. 원본 삭제/수정과 검색 색인 갱신이 즉시 같은 시각에 일어난다고 설명하지 않는다.

## 9. SeaweedFS 반입 기준

### 9.1 반입할 문서와 별도 관리할 자료

| 자료 | 관리 방법 |
|---|---|
| 검토·보완한 C01~C12 및 실제 사용하는 프로토콜 설명 | 승인 목록에 등록한 뒤 nori-tc 버킷으로 복사 가능 |
| 작성·검토·승인한 N01~N06 | 적용 모델·버전과 권한을 붙여 nori-tc 버킷에 반입 |
| C13~C16·N07 | 실제 필요와 열람 권한을 확인해 후속 반입. 운영자 자료는 일반 사용자 자료와 구분 |
| LLM/Gateway 운영 절차·모델 프로필·문서 catalog | 운영/설정 자료로 별도 관리. 검색에 필요한 설명만 별도 승인해 반입 |
| 평가 질문의 정답·goldenset·평가 점수·내부 채점 자료 | 검색 corpus에 반입하지 않음 |
| 비밀 파일·자격 값·실제 개인정보·원시 운영 로그/DB dump·작업 메모·미검증 초안 | 원문 그대로 반입하지 않음. 승인된 설명 문서로 정리할 필요가 있는지 별도 검토 |

문서를 버킷에 복사한 사실만으로 검색 승인이 끝나지는 않는다. 승인된 정확한 객체 key/sourceRef, ownerSystem, requiredPermission, 검색 공간, revision 관리 항목을 catalog에 등록해야 한다. 버킷 전체나 문서 제목만으로 자동 승인하지 않는다.

### 9.2 반입 전 확인 목록

- [ ] 문서가 현재 TC 코드/업무에 맞게 최신화되어 있다.
- [ ] 적용 모델·버전·프로토콜과 문서의 유효 범위가 명확하다.
- [ ] 원본 경로·기준 commit 또는 정본 판본·검토 날짜·담당자가 기록되어 있다.
- [ ] 정의·선행조건·절차·기대 결과·예외·확인 방법이 빠지지 않았다.
- [ ] 표 헤더·단위·출처·원문 위치와 링크를 확인했다.
- [ ] 비밀값·불필요 개인정보·미확인 운영 수치가 없다.
- [ ] 해당 문서의 열람 권한과 승인 catalog 항목을 확인했다.
- [ ] 파일 형식이 실제 구현에서 지원되는 형식인지 확인했다.
- [ ] 중복/구버전 문서와 상충하는 설명을 정리했다.

### 9.3 최초 운영 방식

검토한 문서를 nori-tc 버킷에 반입하고 catalog를 준비한 뒤, RAG가 활성화된 Gateway의 기동/재기동에서 색인을 생성·동기화한다. 변경 없는 정상 문서는 재임베딩하지 않고 신규·변경·삭제·손상을 반영한다. 동기화 성공 여부와 처리 집계는 실제 구현 후 확인해야 한다.

서비스 중 Kafka 자동 감지·주기 실행·채팅 갱신 명령은 최초 범위에 없다. 문서를 추가·수정·제외했을 때 색인 반영은 다음 기동 시점이며, 재기동은 정상 운영 절차에 따라 수행한다. Kafka 토픽과 SeaweedFS 변경 알림은 후속 구현이다.

## 10. 전체 진행 순서

1. **기존 TC 공통 문서 검증·수정:** 9개 TC 레포의 현행 코드·호출 흐름·계약과 기존 문서를 대조한다. 설비별 문서나 RAG를 만들지 않는다. 1번 작업에서 범위를 한정한 정적 문서 검토·정정을 수행했다.
2. **부족한 공통 업무 지식 보완:** N01~N06의 주제별 공통 초안 작성과 독립 코드 정적 대조를 수행했다. 코드에서 알 수 없는 용어·업무 의미·장비 계약은 문서별 미확인 항목으로 남겼으며 담당자 확인·승인이 필요하다.
3. **문서 간 통합 정합성 검증:** 2026-11-16에 기존 121개와 신규 6개의 용어·메시지·조건·상태·적용 버전·출처를 주제별로 비교하고, 발견한 충돌을 코드와 대조하여 원문에 정정했다. 범위를 한정한 통합 정적 검토를 완료했으며 업무 승인·실장비·검색 품질 검증은 별도 확인 사항으로 유지한다.
4. **Spec seq18에 맞게 Plan 개정·승인:** 2026-11-16에 [기존 Plan](../ai-rag-gateway-architecture-plan.md)의 P22~P25·시험·기동 배선·배포 계약을 개정했다. 독립 GPT-6.1 sol/high 에이전트 작성 후 root가 승인 Spec과 실제 코드·설정의 정적 정합성을 검토했다. 현재 `SEQ18_PLAN_APPROVAL_PENDING`으로 사용자 승인 대기이며 P22 구현은 시작하지 않았다. 1~3번 문서 준비와 병행할 수 있다.
5. **P22 준비·로컬 검증:** 문서 승인 catalog, 지원 형식·권한·초기 프로필·평가 자료와 로컬 측정을 준비한다. 모든 모델 프로필의 최종 실측 확정을 착수 조건으로 두지 않는다.
6. **승인 문서 SeaweedFS 적재:** 승인된 TC 문서를 `nori-tc` 버킷에 넣고 실제 객체 key와 catalog를 대조한다. 평가 정답은 검색 corpus에 넣지 않는다.
7. **P23→P24→P25 구현·로컬 검증:** 저장·검색, 기동 동기화, 실제 문서 기반 검증을 진행한다. P23의 제품/노드 측정 이후 Reranker 실행 계약과 품질 기준을 실제 P25 검증 전에 확정한다.
8. **F4 구현·로컬 검증:** 최초 배포에 필요한 나머지 코드와 로컬 검증을 완료한다.
9. **최초 배포·실환경 수용:** F1→F2→F3→F4 순서로 실환경 Gate를 검증한다. 로컬 통과를 실환경 통과로 표시하지 않는다.

이후 코드·모델·계약 변경 시 관련 공통 문서를 함께 갱신하고 승인 배포본을 교체한다. 최초 색인 반영은 다음 Gateway 기동/재기동 시 수행하며, Kafka 자동 갱신은 후속 구현으로 유지한다.

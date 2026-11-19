# Flow 액션·메시지 종류 작성 기록

## 대상

- objectKey: `00.rag/user/flow-action-and-message-reference.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 참조
- 검색 원문 SHA-256: `f8eae21d2ffc090ac8a61eb8e22f60ce85ec35f9f2816b8a9939b7f981544306`
- 검색 원문 줄 수: 284
- 절 수: 18
- 확인일: 2026-11-19 (한국 시간)
- 중단 후 두 대상 파일의 부재를 확인하고 새로 작성했다. 기존 다른 문서는 수정하지 않았다.

## 원천 판본

문서의 원천 파일을 쓰기 전에 저장소별 현재 HEAD와 `git status --short -- <원천 경로들>`을 확인했다. 아래 명령은 각 저장소를 명시한 `git -c safe.directory=<절대 저장소 경로> -C <저장소>`로 실행했다. 이 옵션은 호출 한 번의 읽기에만 적용했으며 전역 Git 설정은 변경하지 않았다.

| 저장소 | HEAD 12자리 | 원천 경로 상태 |
|---|---|---|
| nori-tc-business | 7db24f7466bd | 지정 원천 55개 경로 출력 없음(수정 없음) |
| nori-tc-exec | f1d5e0a8e727 | 지정 원천 1개 경로 출력 없음(수정 없음) |
| nori-tc-ui-backend | 985d17c604dd | 지정 원천 1개 경로 출력 없음(수정 없음) |
| nori-tc-ui-front | 9fe69a75dcd3 | 지정 원천 2개 경로 출력 없음(수정 없음) |

추가로 nori-tc-business-action HEAD 3552180fbd86의 TOOL_CONDITION_REQUEST·TOOL_CONDITION_REPLY 플러그인 구현과 COMMON_RELEASE_INDEPENDENCE_FIXTURE를 읽었다. 당시 저장소 상태 출력은 비어 있었다. nori-tc-common은 CLAUDE.md만 수정 상태였으며 이 문서의 동작 근거로 사용하지 않았다.

화면 후보의 호출 흐름은 ModelDetailPanel → useModelNodeDetail → model.api.ts의 GET /model/{modelVersionKey}/details/{node} → 모델 조회 처리 → JpaModelDetailQueryPort로 대조했다. FlowCard는 Message Name을 Select로, Action Name을 Input으로 렌더한다. 화면에 직접 쓰인 영문 라벨을 보존했고 한국어 문구를 임의로 만들지 않았다.

읽은 기준 자료: docs/rag-document-writing-rules.md 전체, docs/tc-rag-document-list.md 전체, nori-tc-exec/docs/completion-report/07-user-manual.md §5.4. 겹침 대조 대상: model-details-and-flow-editing.md, model-flow-troubleshooting.md, lot-carrier-work-and-system-flow.md, model-plugin-limitations.md, admin/kafka-topic-and-message-reference.md의 관련 절.

## 절별 사실 대조

### Flow의 메시지명과 액션명은 무엇을 정하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L21 | ／ Message Name ／ Flow가 처리할 수신 메시지명을 고른다. ／ | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Message Name·Action Name L298~314 L393~401 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; 액션 실행 L260~304 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; resolveCandidates L147~158 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/resolution/ActionResolutionPolicy.java @ 7db24f7466bd ; resolve L67~104 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L22 | ／ Action Name ／ 일치한 Flow에서 실행할 액션을 자유 입력한다. ／ | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Message Name·Action Name L298~314 L393~401 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; 액션 실행 L260~304 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; resolveCandidates L147~158 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/resolution/ActionResolutionPolicy.java @ 7db24f7466bd ; resolve L67~104 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L24 | 이 문서는 기본 액션 기준이다. 설비별 Business 플러그인에 같은 종류·이름의 액션이 등록되면 플러그인 액션을 먼저 실행한다. Flow 편집·저장 방법은 00.rag/user/model-details-and-flow-editing.md에서 확인한다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Message Name·Action Name L298~314 L393~401 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; 액션 실행 L260~304 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; resolveCandidates L147~158 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/resolution/ActionResolutionPolicy.java @ 7db24f7466bd ; resolve L67~104 ; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### Message Name 후보는 모델의 어느 정의에서 오는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L32 | 「Message Name」 후보는 선택한 모델 버전의 정의에서 가져온다. 고정된 공통 메시지명 목록은 아니다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 후보 L240~270 L574~583 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd ; 모델별 메시지 조회 L127~155 L248~257 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L36 | ／ SECS ／ SECS Message ／ MES Message ／ | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 후보 L240~270 L574~583 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd ; 모델별 메시지 조회 L127~155 L248~257 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L37 | ／ SOCKET ／ Socket Message ／ MES Message ／ | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 후보 L240~270 L574~583 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd ; 모델별 메시지 조회 L127~155 L248~257 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L38 | ／ PLC ／ PLC Group의 Message Name ／ MES Message ／ | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 후보 L240~270 L574~583 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd ; 모델별 메시지 조회 L127~155 L248~257 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L40 | 같은 메시지명이 여러 원천에 있으면 「Message Name」 목록에는 한 번만 표시한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 후보 L240~270 L574~583 ; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd ; 모델별 메시지 조회 L127~155 L248~257 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 수신 메시지 종류에 따라 어느 액션을 실행하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L48 | 기본 액션은 액션명과 수신 메시지 종류를 함께 대조해 실행한다. 아래의 「설비 측」·「MES 측」은 이 구분을 뜻한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/api/action/BusinessWorkflowActionMessageType.java @ 7db24f7466bd ; from L23~42 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessWorkflowCoreActionRegistry.java @ 7db24f7466bd ; 실행기 등록 L51~65 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L52 | ／ 설비 메시지이며 SECS 모델 ／ SECS ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/api/action/BusinessWorkflowActionMessageType.java @ 7db24f7466bd ; from L23~42 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessWorkflowCoreActionRegistry.java @ 7db24f7466bd ; 실행기 등록 L51~65 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L53 | ／ 설비 메시지이며 SOCKET·PLC 모델 ／ SOCKET ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/api/action/BusinessWorkflowActionMessageType.java @ 7db24f7466bd ; from L23~42 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessWorkflowCoreActionRegistry.java @ 7db24f7466bd ; 실행기 등록 L51~65 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L54 | ／ MES 또는 WM 메시지 ／ MES ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/api/action/BusinessWorkflowActionMessageType.java @ 7db24f7466bd ; from L23~42 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessWorkflowCoreActionRegistry.java @ 7db24f7466bd ; 실행기 등록 L51~65 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L56 | 설비 공통 액션은 SECS·SOCKET 양쪽에 등록한다. 같은 액션명도 MES 측 처리와 설비 측 처리가 다를 수 있다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/api/action/BusinessWorkflowActionMessageType.java @ 7db24f7466bd ; from L23~42 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessWorkflowCoreActionRegistry.java @ 7db24f7466bd ; 실행기 등록 L51~65 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 액션의 송신과 MDF 자동 송신은 어떻게 연결되는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L65 | 액션 실행이 정상 종료하고 송신 처리 완료 표시가 없으면 「Action Data Index」에 지정한 MDF 메시지의 목적지에 따라 설비·MES·WM으로 자동 송신한다. MDF 메시지 지정이나 모델의 MDF 정의가 없으면 이 자동 송신을 하지 않는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; 액션 후 자동 발행 L298~313 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd ; publishInternal·publishMes L272~324 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L67 | 아래 표의 「보고」는 MDF 정의에 따른 송신이다. 설비 측 공통 보고의 목적지는 MES 또는 WM이며, MES 보고에는 Lot ID가 필요하다. 액션명만으로 송신 완료를 뜻하지 않는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowDispatchingActionExecutor.java @ 7db24f7466bd ; 액션 후 자동 발행 L298~313 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~108 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/EqpToTargetPublishSupport.java @ 7db24f7466bd ; publishInternal·publishMes L272~324 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 로그 기록과 MES 공통 송신 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L78 | ／ LOG_ONLY ／ 설비·MES 측 ／ Flow 실행 사실을 로그에 기록한다. 액션 자체의 직접 송신은 없다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreSecsActionExecutor.java @ 7db24f7466bd ; logOnly L21~33 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreSocketActionExecutor.java @ 7db24f7466bd ; logOnly L21~33 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreMesActionExecutor.java @ 7db24f7466bd ; 기본 액션 L47~77 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L79 | ／ PUBLISH_MES_COMMAND ／ MES 측 ／ MES 명령 메시지를 조립해 MES로 보낸다. MDF 정의가 있으면 그 정의로 내용을 만든다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreMesActionExecutor.java @ 7db24f7466bd ; 기본 액션 L47~77 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L81 | LOG_ONLY를 선택한 Flow도 MDF 자동 송신 조건을 만족하면 후속 송신이 발생한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreSecsActionExecutor.java @ 7db24f7466bd ; logOnly L21~33 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreSocketActionExecutor.java @ 7db24f7466bd ; logOnly L21~33 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/core/BusinessCoreMesActionExecutor.java @ 7db24f7466bd ; 기본 액션 L47~77 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 설비에 조회·응답을 전달하는 MES 측 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L91 | MES 측의 다음 액션은 선택한 MDF로 설비 명령을 만든다. 설비 통신 방식은 대상 모델에서 가져온다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/ToolConditionRequestTcAction.java @ 7db24f7466bd ; handleToolConditionRequest L61~75 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd ; handleEqpConditionCheck L32~46 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/CarrierIdReadReplyTcAction.java @ 7db24f7466bd ; handleCarrierIdReadReply L38~52 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/MesToEqpPublishSupport.java @ 7db24f7466bd ; publish L84~107 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L95 | ／ TOOL_CONDITION_REQUEST ／ 수신한 설비 조건 요청을 설비로 전달한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/ToolConditionRequestTcAction.java @ 7db24f7466bd ; handleToolConditionRequest L61~75 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/MesToEqpPublishSupport.java @ 7db24f7466bd ; publish L84~107 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L96 | ／ EQP_CONDITION_CHECK ／ 수신한 설비 조건 확인 요청을 설비로 전달한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd ; handleEqpConditionCheck L32~46 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/MesToEqpPublishSupport.java @ 7db24f7466bd ; publish L84~107 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L97 | ／ CARRIERID_READ_REPLY ／ 수신한 Carrier ID 읽기 응답을 설비로 전달한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/CarrierIdReadReplyTcAction.java @ 7db24f7466bd ; handleCarrierIdReadReply L38~52 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/MesToEqpPublishSupport.java @ 7db24f7466bd ; publish L84~107 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L99 | MDF 송신 정의가 없으면 각 액션의 직접 송신을 건너뛴다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/ToolConditionRequestTcAction.java @ 7db24f7466bd ; handleToolConditionRequest L61~75 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/EqpConditionCheckTcAction.java @ 7db24f7466bd ; handleEqpConditionCheck L32~46 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/CarrierIdReadReplyTcAction.java @ 7db24f7466bd ; handleCarrierIdReadReply L38~52 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/MesToEqpPublishSupport.java @ 7db24f7466bd ; publish L84~107 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 설비의 조건 응답·준비·작업 정보를 보고하는 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L110 | 설비 측의 다음 액션은 보고 메시지를 조립해 전달한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ToolConditionReplyTcAction.java @ 7db24f7466bd ; handleToolConditionReply L29~44 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/EqpConditionCheckReplyTcAction.java @ 7db24f7466bd ; handleEqpConditionCheckReply L33~48 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ReadyToWorkTcAction.java @ 7db24f7466bd ; handleReadyToWork L29~44 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkInformTcAction.java @ 7db24f7466bd ; handleWorkInform L29~44 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoWorkInformTcAction.java @ 7db24f7466bd ; handleWoWorkInform L29~43 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L114 | ／ TOOL_CONDITION_REPLY ／ 수신한 조건 응답의 이벤트명으로 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ToolConditionReplyTcAction.java @ 7db24f7466bd ; handleToolConditionReply L29~44 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L115 | ／ EQP_CONDITION_CHECK_REPLY ／ 수신한 조건 확인 응답의 이벤트명으로 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/EqpConditionCheckReplyTcAction.java @ 7db24f7466bd ; handleEqpConditionCheckReply L33~48 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L116 | ／ READY_TO_WORK ／ 수신한 준비 보고의 이벤트명을 유지해 전달한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ReadyToWorkTcAction.java @ 7db24f7466bd ; handleReadyToWork L29~44 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L117 | ／ WORK_INFORM ／ 보고 이벤트명을 WORK_INFORM으로 정해 전달한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkInformTcAction.java @ 7db24f7466bd ; handleWorkInform L29~44 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L118 | ／ WO_WORK_INFORM ／ 수신한 작업 정보의 이벤트명을 유지해 전달한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkInformTcAction.java @ 7db24f7466bd ; handleWorkInform L29~44 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoWorkInformTcAction.java @ 7db24f7466bd ; handleWoWorkInform L29~43 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L120 | 이 액션들은 작업 생성이나 작업 상태 변경을 직접 수행하지 않는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ToolConditionReplyTcAction.java @ 7db24f7466bd ; handleToolConditionReply L29~44 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/EqpConditionCheckReplyTcAction.java @ 7db24f7466bd ; handleEqpConditionCheckReply L33~48 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ReadyToWorkTcAction.java @ 7db24f7466bd ; handleReadyToWork L29~44 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkInformTcAction.java @ 7db24f7466bd ; handleWorkInform L29~44 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoWorkInformTcAction.java @ 7db24f7466bd ; handleWoWorkInform L29~43 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### Carrier 반입·반출 요청에서 Port를 바꾸는 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L131 | 설비 측 액션이며 수신값의 Port ID를 사용한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierLoadedTcAction.java @ 7db24f7466bd ; handleCarrierLoaded L28~37 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarLoadTcAction.java @ 7db24f7466bd ; handleWoCarLoad L28~37 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierIdReadTcAction.java @ 7db24f7466bd ; handleCarrierIdRead L36~65 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/UnloadRequestTcAction.java @ 7db24f7466bd ; handleUnloadRequest L36~65 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 상태 상수·전이·Port 검사 L24~26 L57~80 L99~106 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L135 | ／ CARRIER_LOADED·WO_CAR_LOAD ／ Port를 LOAD로 바꾼다. 직접 보고는 없으며 후속 송신은 MDF 자동 송신에 따른다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierLoadedTcAction.java @ 7db24f7466bd ; handleCarrierLoaded L28~37 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarLoadTcAction.java @ 7db24f7466bd ; handleWoCarLoad L28~37 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 상태 상수·전이·Port 검사 L24~26 L57~80 L99~106 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L136 | ／ CARRIERID_READ ／ Lot ID가 있고 Port를 LOAD로 바꾸는 데 성공하면 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierIdReadTcAction.java @ 7db24f7466bd ; handleCarrierIdRead L36~65 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 상태 상수·전이·Port 검사 L24~26 L57~80 L99~106 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L137 | ／ UNLOAD_REQUEST ／ Lot ID가 있고 Port를 UNLOAD로 바꾸는 데 성공하면 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/UnloadRequestTcAction.java @ 7db24f7466bd ; handleUnloadRequest L36~65 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 상태 상수·전이·Port 검사 L24~26 L57~80 L99~106 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L139 | Port ID가 비어 있으면 Port 상태 변경을 수행하지 못한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierLoadedTcAction.java @ 7db24f7466bd ; handleCarrierLoaded L28~37 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarLoadTcAction.java @ 7db24f7466bd ; handleWoCarLoad L28~37 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierIdReadTcAction.java @ 7db24f7466bd ; handleCarrierIdRead L36~65 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/UnloadRequestTcAction.java @ 7db24f7466bd ; handleUnloadRequest L36~65 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/PortStateTransitionSupport.java @ 7db24f7466bd ; 상태 상수·전이·Port 검사 L24~26 L57~80 L99~106 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### Carrier 검증과 작업 생성 응답을 잇는 설비 측 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L150 | ／ CARRIER_VERIFICATION ／ 외부 검증 없이 CARRIER_VERIFICATION_PASS 이벤트로 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierVerificationTcAction.java @ 7db24f7466bd ; handleCarrierVerification L29~50 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L151 | ／ PROCESSJOBCREATE_REPLY ／ MDF가 있으면 CONTROLJOBCREATE 명령을 설비로 보낸다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessjobcreateReplyTcAction.java @ 7db24f7466bd ; handleProcessjobcreateReply L37~57 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L152 | ／ CONTROLJOBCREATE_REPLY ／ WORK_START_REPLY 이벤트로 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ControljobcreateReplyTcAction.java @ 7db24f7466bd ; handleControljobcreateReply L29~44 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 작업 시작·종료·완료를 반영하는 설비 측 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L165 | 대상 작업이 있으면 아래 상태 전이를 적용하고 보고한다. 작업을 찾지 못하면 상태 변경 없이 보고를 시도한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkStartedTcAction.java @ 7db24f7466bd ; handleWorkStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd ; handleWoEqpStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkEndedTcAction.java @ 7db24f7466bd ; handleWorkEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpEndedTcAction.java @ 7db24f7466bd ; handleWoEqpEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkCompletedTcAction.java @ 7db24f7466bd ; handleWorkCompleted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd ; handleWoEqpComplete L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; 상태 전이·역행 방지 L68~98 L254~305 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L169 | ／ WORK_STARTED·WO_EQP_STARTED ／ 작업·제어 작업을 start로 바꾼다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkStartedTcAction.java @ 7db24f7466bd ; handleWorkStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd ; handleWoEqpStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; 상태 전이·역행 방지 L68~98 L254~305 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L170 | ／ WORK_ENDED·WO_EQP_ENDED ／ 작업·제어 작업을 end로 바꾼다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkEndedTcAction.java @ 7db24f7466bd ; handleWorkEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpEndedTcAction.java @ 7db24f7466bd ; handleWoEqpEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; 상태 전이·역행 방지 L68~98 L254~305 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L171 | ／ WORK_COMPLETED·WO_EQP_COMPLETE ／ 작업·제어 작업·공정 작업을 completed로 바꾼다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkCompletedTcAction.java @ 7db24f7466bd ; handleWorkCompleted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd ; handleWoEqpComplete L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; 상태 전이·역행 방지 L68~98 L254~305 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L173 | 진행된 상태를 되돌리지 않는 상태 전이 규칙을 적용한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkStartedTcAction.java @ 7db24f7466bd ; handleWorkStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpStartedTcAction.java @ 7db24f7466bd ; handleWoEqpStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkEndedTcAction.java @ 7db24f7466bd ; handleWorkEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpEndedTcAction.java @ 7db24f7466bd ; handleWoEqpEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WorkCompletedTcAction.java @ 7db24f7466bd ; handleWorkCompleted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoEqpCompleteTcAction.java @ 7db24f7466bd ; handleWoEqpComplete L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; 상태 전이·역행 방지 L68~98 L254~305 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 공정 시작·종료를 반영하는 설비 측 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L184 | ／ PROCESS_STARTED ／ 공정 작업과 작업·제어 작업에 start 전이를 적용하고 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessStartedTcAction.java @ 7db24f7466bd ; handleProcessStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; markProcessStart·markProcessEnd L75~84 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L185 | ／ PROCESS_ENDED ／ 공정 작업에 end 전이를 적용하고 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessEndedTcAction.java @ 7db24f7466bd ; handleProcessEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; markProcessStart·markProcessEnd L75~84 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L187 | 두 설비 측 액션은 대상 작업을 찾지 못하면 상태 변경 없이 보고를 시도한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessStartedTcAction.java @ 7db24f7466bd ; handleProcessStarted L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/ProcessEndedTcAction.java @ 7db24f7466bd ; handleProcessEnded L40~61 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; markProcessStart·markProcessEnd L75~84 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### Carrier 반출 완료 때 작업을 닫는 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L198 | ／ CARRIER_UNLOAD_REQUEST·WO_CAR_UNLOAD_COMP_REQ ／ Port를 IDLE로 바꾸고 대상 작업·제어 작업에 closed 전이를 적용한다. 작업 삭제 처리를 호출한 뒤 보고한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierUnloadRequestTcAction.java @ 7db24f7466bd ; handleCarrierUnloadRequest L49~89 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadCompReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadCompReq L49~89 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; markClosed L103~105 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L200 | Lot ID가 없거나 Port 상태 변경에 실패하면 이후 처리를 진행하지 않는다. 대상 작업이 없으면 작업 상태 변경은 건너뛴다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/CarrierUnloadRequestTcAction.java @ 7db24f7466bd ; handleCarrierUnloadRequest L49~89 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadCompReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadCompReq L49~89 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkStateTransitionSupport.java @ 7db24f7466bd ; markClosed L103~105 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### WO_CAR_UNLOAD_REQ가 만드는 반출·Port 보고

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L207 | 설비 측 WO_CAR_UNLOAD_REQ는 Lot ID가 있고 Port를 UNLOAD로 바꾸는 데 성공하면 반출 요청을 보고한다. 직접 보고가 이루어진 뒤에는 EQP_PORT_STATUS_CHG에 반출 상태를 실어 지연 보고를 예약한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadReq·publishEqpPortStatusChange L46~110 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L209 | WO_CAR_UNLOAD_REQ의 직접 처리에서는 작업을 삭제하지 않는다. 작업을 닫고 삭제하는 WO_CAR_UNLOAD_COMP_REQ와 구별한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/WoCarUnloadReqTcAction.java @ 7db24f7466bd ; handleWoCarUnloadReq·publishEqpPortStatusChange L46~110 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### MES의 작업 지시를 받는 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L221 | ／ WORK_ORDER_REQUEST ／ 작업을 생성하고 MDF가 있으면 WORK_ORDER_REPLY로 응답한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkOrderRequestTcAction.java @ 7db24f7466bd ; handleWorkOrderRequest L50~73 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; 생성 필수 입력 L80~108 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L222 | ／ WO_EQP_START_REQ ／ 같은 설비·Lot의 작업이 없을 때 작업을 생성한다. 설비 송신은 MDF 자동 송신에 따른다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd ; handleWoEqpStartReq L37~57 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; 생성 필수 입력 L80~108 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L223 | ／ WO_JOBRESERVE ／ Port의 Carrier ID 기록을 시도하고, 같은 설비·Lot의 작업이 없으면 생성한다. MDF가 있으면 MES로 응답하며 WO_JOBRESERVE 원문 명령도 설비 송신 처리에 넘긴다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd ; handleWoJobreserve·후속 송신 L63~91 L103~144 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; 생성 필수 입력 L80~108 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L225 | 작업 생성에는 작업 ID·레시피 ID와 Lot ID 또는 Carrier ID가 필요하다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkOrderRequestTcAction.java @ 7db24f7466bd ; handleWorkOrderRequest L50~73 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpStartReqTcAction.java @ 7db24f7466bd ; handleWoEqpStartReq L37~57 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoJobreserveTcAction.java @ 7db24f7466bd ; handleWoJobreserve·후속 송신 L63~91 L103~144 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; 생성 필수 입력 L80~108 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 작업 생성·삭제를 재사용하는 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L237 | ／ CREATE_WORK ／ 수신한 작업 정보로 Lot·Carrier·제어 작업·공정 작업을 묶어 생성한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/tcWork/CreateWorkTcAction.java @ 7db24f7466bd ; createWork L21~45 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; createWork L80~159 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleDeleteSupport.java @ 7db24f7466bd ; deleteWork·deleteResolvedWork L60~119 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L238 | ／ DELETE_WORK ／ 설비의 Lot ID 또는 Carrier ID로 대상 작업을 찾아 수집 상태 정리를 시도하고 작업을 삭제한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/tcWork/DeleteWorkTcAction.java @ 7db24f7466bd ; deleteWork L21~45 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; createWork L80~159 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleDeleteSupport.java @ 7db24f7466bd ; deleteWork·deleteResolvedWork L60~119 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L240 | 두 액션은 다른 업무 액션에서도 호출하는 공통 처리이다. 삭제 대상 작업이 없으면 삭제하지 않는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/tcWork/CreateWorkTcAction.java @ 7db24f7466bd ; createWork L21~45 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/tcWork/DeleteWorkTcAction.java @ 7db24f7466bd ; deleteWork L21~45 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleCreateSupport.java @ 7db24f7466bd ; createWork L80~159 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/WorkLifecycleDeleteSupport.java @ 7db24f7466bd ; deleteWork·deleteResolvedWork L60~119 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 수집 규격 응답으로 수집 상태를 준비하는 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L250 | ／ DCSPECREQ_REPLY ／ 대상 작업의 첫 공정 작업 ID에 dcspecValue의 항목을 연결해 수집 상태를 초기화한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/DcspecreqReplyTcAction.java @ 7db24f7466bd ; handleDcspecreqReply L70~114 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L251 | ／ MAT_DCSPEC_REPLY ／ 대상 작업 ID·dcspecId별로 dcolItemList를 사용해 수집 상태를 초기화한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd ; handleMatDcspecReply L46~98 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L253 | 대상 작업이 없으면 초기화하지 않는다. DCSPECREQ_REPLY는 공정 작업 ID가 없을 때, MAT_DCSPEC_REPLY는 dcspecId 또는 작업 ID가 없을 때도 초기화하지 않는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/DcspecreqReplyTcAction.java @ 7db24f7466bd ; handleDcspecreqReply L70~114 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReplyTcAction.java @ 7db24f7466bd ; handleMatDcspecReply L46~98 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 수집 결과를 조립해 보고하는 설비 측 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L263 | ／ DATACOLL ／ 공정 작업의 수집 상태에 수집·계산 규칙을 적용해 dcspecValue로 보고하고, 직접 보고 성공 뒤 수집 상태 삭제를 시도한다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/DatacollTcAction.java @ 7db24f7466bd ; reportDatacoll L102~183 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L264 | ／ MAT_DATACOLL ／ 대상 작업의 수집 규격마다 최종값을 계산해 dcspecId·dcolItemList로 보고한다. 이 액션은 수집 상태를 직접 삭제하지 않는다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/DatacollTcAction.java @ 7db24f7466bd ; reportDatacoll L102~183 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd ; handleMatDatacoll·resolveDatacollTarget L58~99 L118~155 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L266 | DATACOLL은 작업·공정 작업 ID·correlationId·수집 상태가 있어야 직접 보고한다. MAT_DATACOLL은 대상 수집 상태가 없으면 보고를 건너뛴다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/DatacollTcAction.java @ 7db24f7466bd ; reportDatacoll L102~183 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/MatDatacollTcAction.java @ 7db24f7466bd ; handleMatDatacoll·resolveDatacollTarget L58~99 L118~155 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

### 기록만 남기는 MES 측 요청·응답 액션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L277 | 아래 MES 측 액션의 기본 메서드는 실행 기록만 남긴다. 후속 송신은 MDF 자동 송신에 따른다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/DatacollReplyTcAction.java @ 7db24f7466bd ; handleDatacollReply L23~29 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkCompletedReplyTcAction.java @ 7db24f7466bd ; handleWorkCompletedReply L23~29 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpCompleteReplyTcAction.java @ 7db24f7466bd ; handleWoEqpCompleteReply L15~22 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkStartRequestTcAction.java @ 7db24f7466bd ; handleWorkStartRequest L21~27 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReqTcAction.java @ 7db24f7466bd ; handleMatDcspecReq L19~25 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L281 | ／ DATACOLL_REPLY·WORK_COMPLETED_REPLY ／ 응답 처리 기록을 남긴다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/DatacollReplyTcAction.java @ 7db24f7466bd ; handleDatacollReply L23~29 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkCompletedReplyTcAction.java @ 7db24f7466bd ; handleWorkCompletedReply L23~29 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L282 | ／ WO_EQP_COMPLETE ／ 응답 처리 기록을 남긴다. 설비 측의 같은 액션명과 달리 작업 상태를 바꾸지 않는다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WoEqpCompleteReplyTcAction.java @ 7db24f7466bd ; handleWoEqpCompleteReply L15~22 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |
| L283 | ／ WORK_START_REQUEST·MAT_DCSPEC_REQ ／ 요청 처리 기록을 남기며 직접 송신하지 않는다. ／ | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/WorkStartRequestTcAction.java @ 7db24f7466bd ; handleWorkStartRequest L21~27 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/mes/MatDcspecReqTcAction.java @ 7db24f7466bd ; handleMatDcspecReq L19~25 | 실제 분기·호출·반환값 또는 화면 후보 구성과 본문의 조건·결과를 대조했다. |

절 전체를 읽고 제목의 질문 식별·원천 일치·독립 답·다른 절과의 역할 중복 없음으로 판정했다.

## 매뉴얼과 코드가 다른 곳

| 쟁점 | 매뉴얼 | 코드 | 검색 원문 반영 |
|---|---|---|---|
| MDF의 용도 | U §5.4.2는 MDF를 MES 보고 서식으로 설명한다. | BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd L85~108은 EQP·MES·WM 목적지로 분기한다. | 「액션의 송신과 MDF 자동 송신은 어떻게 연결되는가」에 설비·MES·WM 송신을 적었다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 담당 역할 |
|---|---|---|
| 현장별 추가 Business 플러그인 액션 이름과 실제 적용 여부 | 현장 JAR·등록 상태는 이번 읽기 범위의 원천으로 확인하지 않았다. | Business 플러그인 담당 |
| 제조사별 전체 수신 메시지명과 업무 판정 조건 | 후보는 모델별 정의이며 제조사 원본과 현장 모델을 확보하지 않았다. | 모델·설비 연동 담당 |
| COMMON_RELEASE_INDEPENDENCE_FIXTURE를 업무 액션 표에 넣는 것 | nori-tc-business-action/src/main/java/com/nori/tc/business/action/compat/CommonReleaseIndependentAction.java @ 3552180fbd86 L9~32는 공개 계약 독립성 검증용이며 execute는 traceId 평가만 한다. Flow 업무 처리 역할은 확인되지 않았다. | Business 플러그인 담당 |

이름만 선언되고 처리 역할을 확인하지 못한 기본 액션은 없었다. 기본 구현의 @TcAction 고유 이름 41개를 검색 원문과 대조해 누락 0개를 확인했다. 로그만 남기는 기본 구현도 그 범위로 명시했다.

## 다른 문서가 소유해 넣지 않은 사실

| 사실 | 소유 objectKey | 이번 문서의 경계 |
|---|---|---|
| 체크아웃·Flow 추가·필수 입력·저장·수집 항목 연결 | 00.rag/user/model-details-and-flow-editing.md | 조작 절차를 반복하지 않는다. 메시지명과 액션명 구분에 필요한 최소 화면 사실만 쓴다. |
| 계산·Collect 실패·수신 순서 진단 | 00.rag/user/model-flow-troubleshooting.md | 오류 진단·운영 조치는 넣지 않는다. |
| Lot·Carrier·작업 메시지의 진행 순서 | 00.rag/user/lot-carrier-work-and-system-flow.md | 순서 설명을 복제하지 않고 액션별 메서드의 처리 범위로 한정한다. |
| 토픽·방향·키·JSON 봉투·이벤트 왕복 표 | 00.rag/admin/kafka-topic-and-message-reference.md | 사용자 본문에 admin objectKey나 토픽 계약 표를 넣지 않는다. |
| 상태값의 독립적인 뜻 | 00.rag/user/state-reference.md | 액션이 적용하는 대상·목표 상태만 필요한 만큼 적는다. |
| 모델·JAR 적용 경계 | 00.rag/user/model-plugin-limitations.md | 적재·교체 절차와 제한 표를 복제하지 않는다. |

## 검사 결과

- 문장·표 자료 행별 사실 대조: 67개 기록. 표 머리글·메타는 사실 행 수에서 제외했다.
- 사람 검사: 18개 절 전체를 읽었다. 제목 질문 식별·원천 일치·독립 답·중복 여부를 절마다 확인했다.
- 제목: 정확 중복 및 불필요한 식별자 치환 틀 반복 0건. 빈 틀 문장·이름 토막 추정 풀이·잘린 문장·오답 사례 0건.
- 권한 경계: 검색 원문에 admin objectKey·내부 운영 조작 0건. 화면 조작 권한 표는 소유 문서에 맡겼다.
- 기계 검사는 구조·직렬화 길이·마스킹·원천·다른 문서와의 동일 자식 본문을 검사한다. 제품 시험·브라우저 실행·현장 적용 확인·검색 정답률 측정은 수행하지 않았다.
- 관련 시험 기대값은 읽었다(재실행하지 않음): nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/workflow/BusinessWorkflowPlcNonSecsPathTest.java @ 7db24f7466bd L50~80(PLC의 SOCKET 분기); action/MatDatacollTcActionTest.java L101~234(규격별 보고·상태 유지·WM 목적지); action/DcspecreqReplyTcActionTest.java L56~150(초기화·대상 누락); action/MesInboundTcActionT6Test.java 및 action/EqpInboundTcActionT7Test.java의 액션별 assertion.
- 초기 검사 두 프로세스 중 첫 결과는 회수하지 못했다. 두 번째 결과는 exit 1, 위반 60건으로 원천 commit:path 읽기 실패였다. 실제 git show 재현은 exit 1과 detected dubious ownership 오류를 반환했다. 인코딩·구조를 우회하거나 검사기 소스를 수정하지 않았다. Git 신뢰 경로는 후속 검사 프로세스의 GIT_CONFIG_COUNT/GIT_CONFIG_KEY_n/GIT_CONFIG_VALUE_n에 저장소별 safe.directory로만 전달했다.
- 준비 중 이미 존재하는 work 폴더에 New-Item을 호출한 한 번은 ResourceExists로 중단되어 검사기가 시작되지 않았다.

최종 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/flow-action-and-message-reference.md`

실행 도구: GNU bash 5.2.37(1)-release(x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS, Git 2.52.0.windows.1, 기록 조립 Python 3.12.10.

검사기 자식 Git 호출에만 프로세스 환경으로 다음 저장소들의 safe.directory를 지정했다: nori-tc-ui-front, nori-tc-ui-backend, nori-tc-business, nori-tc-business-action, nori-tc-common, nori-tc-exec, nori-tc-gateway, nori-tc-gateway-action, nori-tc-simulator, nori-sdd-specs, nori-ai-gateway. 값의 공통 부모는 `C:/workspace_nori/nori-tc/`이다. 검사기·판정식·소스·전역 설정은 변경하지 않았다.

- 보완 전 정상 검사: exit 0. 같은 자식 본문 위반·MASK 출력 없음.
- 마지막 검색 원문 편집 후 최종 검사: exit 0, 위반 0건, CROSS VIOLATION 0건, MASK 출력 0건.
- 최종 18절·67자식, 최대 자식 142자·최대 부모 352자. 검사 출력의 검색 원문 SHA가 대상 절에 기록한 최종 SHA와 같다.
- 검사기 실제 시작은 4회(초기 2회, 신뢰 경로 지정 후 2회)이다. 첫 실행의 종료 출력은 회수하지 못했으므로 성공으로 계산하지 않았다. 후속 검사 두 번만 exit 0으로 확인했다. 전체 검사 wall time은 출력 회수 시점과 프로세스 경과를 결속해 측정하지 못했다.

마지막 출력 10줄:

```text
    L264-264 표 행 102  | 액션=MAT_DATACOLL | 기본 처리=대상 작업의 수집 규격마다 최종값을 계산해 dcspecId·dcolItemList로 보고한다. 이 액션은 수집 상태를 직접 삭제하지 않는다.
    L266-266 문단 93  | DATACOLL은 작업·공정 작업 ID·correlationId·수집 상태가 있어야 직접 보고한다. MAT_DATACOLL은 대상 수집 상태가 없으면 보고를 건너뛴다.
§ L268-283 [22] 기록만 남기는 MES 측 요청·응답 액션   parent=276 children=4 sources=5
    L277-277 문단 55  | 아래 MES 측 액션의 기본 메서드는 실행 기록만 남긴다. 후속 송신은 MDF 자동 송신에 따른다.
    L281-281 표 행 65  | 액션=DATACOLL_REPLY·WORK_COMPLETED_REPLY | 직접 처리의 범위=응답 처리 기록을 남긴다.
    L282-282 표 행 78  | 액션=WO_EQP_COMPLETE | 직접 처리의 범위=응답 처리 기록을 남긴다. 설비 측의 같은 액션명과 달리 작업 상태를 바꾸지 않는다.
    L283-283 표 행 75  | 액션=WORK_START_REQUEST·MAT_DCSPEC_REQ | 직접 처리의 범위=요청 처리 기록을 남기며 직접 송신하지 않는다.
SUMMARY sections=18 children=67 maxChild=142 maxParent=352
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

최종 검사 원문 로그 SHA-256: `0f06b9608cab3c7ccd9d83d09749b173a1e81a97ddb19406a3ccbb67a76ef420`
보완 전 정상 검사 로그 SHA-256: `76eac7257e0d3577aaf73f6d86c7145766ec4ce54f9d60d29b335f2116f6b32c`


## 실행 범위와 자원

- 독립 Agent·Claude·다른 AI 호출 0회. 제품 코드·설정·SPEC·다른 검색 원문 수정 0개. git add·commit·push·업로드·원격 접속을 하지 않았다.
- 임시 파일은 지정 work 폴더의 이 세션 소유 파일만 사용한다. 검사 원문과 필요한 근거를 이 작성 기록으로 보존한 뒤 정상 삭제한다.
- 전체 요청의 정확한 wall time과 provider 토큰·비용은 중단을 포함해 측정하지 못했다. 미확인 값을 0으로 기록하지 않는다.

## 추가 호출 흐름 대조

- 후보 조회 API: nori-tc-ui-front/src/features/model/hooks/useModelNodeDetail.ts @ 9fe69a75dcd3 L10~16 → src/features/model/api/model.api.ts의 getModelNodeDetail → nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd L70~94 → controller/support/ModelDetailResponseSupport.java의 메시지·PLC 분기 → JpaModelDetailQueryPort. 조회 실패의 조작·권한 안내는 앞선 사용 절차 문서의 소유로 둔다.
- PUBLISH_MES_COMMAND의 내용 조립: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessMesCommandMessageBuilder.java @ 7db24f7466bd L333~363. correlationId를 필수 확인하고 MDF 조립 결과가 있으면 그 내용을, 없으면 수신 내용 기반 명령을 구성한다. 검색 원문의 「MDF 정의가 있으면 그 정의로 내용을 만든다」와 대조했다.
- WO_JOBRESERVE의 원문 전달: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/support/DemoSocketMirrorPublishSupport.java @ 7db24f7466bd L104~135. publishRawAlways는 데모 사용 설정을 검사하지 않고 SOCKET interface 명령을 발행한다. 따라서 검색 원문은 수신 프로토콜별 실제 설비 완료를 단정하지 않고 설비 송신 처리에 넘긴다고 적었다.
- 최종 문장 검토 중 PLC 출처 라벨에 관한 초안 문장을 삭제했다. FlowCard의 실제 messageOptions는 ModelDetailPanel L574~583에서 이름만 합친다. 다른 표용 label 생성 코드를 FlowCard 표시로 확대하지 않았다.

## 임시 자원 정리 완료

- 보존: 최종 검색 원문·사실 대조·원천 판본·검사 마지막 10줄·로그 SHA-256을 이 기록에 남겼다.
- 삭제 대상은 `C:/workspace_nori/nori-tc/rag-step3-work/doc-51-flow-action-and-message-reference/work/`의 이 세션 소유 check-final.log(16732바이트), check-last.log(16600바이트), write_record.py(12668바이트), finalize_record.py(4405바이트) 네 파일뿐이다. 기존 work 부모는 삭제하지 않았다.
- 정확한 절대경로와 부모 경로의 reparse 부재, 네 파일의 보호 속성 부재(모두 Archive), 배타적 파일 열기 성공을 확인했다. 이 세션의 기록 스크립트·최종 검사 프로세스는 종료코드 회수로 종료를 확인했다.
- 전체 시스템의 명령줄 조회는 Get-CimInstance Win32_Process가 액세스 거부되어 확인하지 못했다. 이를 잠금 확인 성공으로 바꾸지 않았으며, 네 파일의 배타적 열기로 현재 파일 사용 여부를 별도로 검사했다. 다른 프로세스는 종료하지 않았다.
- native PowerShell `Remove-Item -LiteralPath <각 절대경로> -ErrorAction Stop`을 Force·Recurse 없이 실행했다. exit 0, 네 파일의 Test-Path가 모두 false이며 work의 남은 항목은 0개였다.
- 작성 기록 조립 중 PowerShell stdin의 문자 인코딩 문제로 붙인 추가 문장이 깨진 것을 발견했다. UTF-8 스크립트로 작성 기록을 다시 조립하고 최종 기록의 깨진 문자·미완료 placeholder 부재를 확인했다. 검색 원문은 이 문제로 변경되지 않았다.

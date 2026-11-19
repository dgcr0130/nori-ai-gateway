# 모델·Flow 처리 작성 기록

## 대상

- objectKey: `00.rag/user/model-flow-troubleshooting.md`
- 구획과 필요한 권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/model-flow-troubleshooting.md`
- 검색 원문 SHA-256: `c768ab400b584c73b560e14c44cf5ebad0137aa5090d1e0ec6e80c6b11339da3`
- 검색 원문 줄 수: 125
- H2 절 수: 8
- 확인일: 2026-11-19, 한국 시간 기준
- 작성 범위: 계산 규칙 예외와 원값 유지 조건, Collect 미연결·미수집, 버전 복제의 연결 승계, 체크인 비교, 같은 설비의 지연 재시도 순서, 조회·저장 권한 거부의 구별.
- 독립 작성 세션에서 직접 조사·작성·자체검사했다. 다른 Agent 호출은 0회다. 메인 세션의 독립 대조 검증은 이 기록의 자체검사와 별개다.

## 원천 판본

`docs/rag-document-writing-rules.md`와 `docs/tc-rag-document-list.md` 전체를 읽고, 대상의 세 수정 커밋과 현재 구현·시험의 기대값을 대조했다. 화면 문구는 프런트 렌더 코드·한국어 문구, 응답 조립 코드와 함께 확인했다.

| 저장소 | 현재 HEAD 12자리 | 작업 트리 상태 확인 범위 | 결과 |
|---|---|---|---|
| nori-tc-business | 7db24f7466bd | business/tc-business-core/src | `git status --short -- <범위>` 출력에 변경 항목 없음 |
| nori-tc-ui-backend | 985d17c604dd | ui | `git status --short -- <범위>` 출력에 변경 항목 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | src | `git status --short -- <범위>` 출력에 변경 항목 없음 |
| nori-tc-exec | f1d5e0a8e727 | docs/scripts/manual-permission-catalog.json·docs/db_table·docs/completion-report/07-user-manual.md | `git status --short -- <범위>` 출력에 변경 항목 없음 |

읽기 전용으로 참조한 기존 검색 원문의 파일 SHA-256은 다음과 같다. 제품 동작의 1차 근거로 대체하지 않고 소유 범위·안내 경로 대조에만 사용했다.

| 기존 문서 objectKey | SHA-256 |
|---|---|
| 00.rag/user/model-details-and-flow-editing.md | 32dcb7ddbfbc7673815d04ab20b7753319c52d8af8f1a249c107df360745c173 |
| 00.rag/user/model-registration-and-branching.md | 2e615d27fcc0a0844f8a8276df4ab5966a2bbf6f5a7ac281c58d40cb355fb5c9 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d |

각 저장소 조회는 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C ../<저장소> ...`로 실행했다. 최초 기본 Git 조회는 sandbox 사용자와 저장소 소유자가 달라 `detected dubious ownership`으로 종료됐다. 읽기 대상의 정확한 경로를 해당 Git 명령에만 지정했고 전역 설정·소유권·권한은 바꾸지 않았다. 이후 HEAD 조회와 상태 조회는 exit 0이었다. Git의 공통 ignore 파일 접근 경고는 있었으며, 이를 작업 트리 변경이나 소스 수정으로 해석하지 않았다.

현재 제품 저장소는 `main...origin/main [ahead 8]`, HEAD `70cf6cd`이며 `rag-documents/`가 미추적 상태였다. `git worktree list`에는 현재 제품 경로 하나가 표시됐다. 기존 문서는 읽기만 했고 add·commit·push는 하지 않았다.

| 수정 이력 | 확인한 변경과 현재 코드의 연결 |
|---|---|
| nori-tc-business 274c7785 | 계산 규칙 예외를 원값 반환으로 삼키지 않고 항목·규칙을 담은 예외로 전파한다. 미지원 함수명의 기본 반환도 예외로 바뀌었다. 현재 DcopCollectionEngine L147~166, BusinessTransformSupport L66~87과 이름 오타 시험의 예외 기대값을 대조했다. |
| nori-tc-business 038334e8 | 지연 후 대기열 뒤에 다시 넣는 대신 같은 작업의 실행 안에서 대기·재시도한다. 현재 BusinessRuntimeTaskSupport L136~153·L367~399, BusinessRuntimeEngineTest L629~694의 선행→선행 재시도→후행 기대값을 확인했다. |
| nori-tc-ui-backend 88e93e02 | 복제 스냅샷에 Collect 연결을 포함하고 새 Flow·수집 항목 키로 연결을 다시 만든다. workflow 조회 응답에 정렬된 Collect 문자열을 추가한다. 현재 복제 코드·branch 시험·조회 정렬 시험과 프런트 체크인 비교 렌더를 확인했다. |

커밋의 과거 결함 설명은 본문의 현재 동작으로 쓰지 않았다. 현장 적용은 확인하지 않았으므로 본문은 수정 포함 판본이라는 조건과 운영 담당자에게 판본 대조를 요청하는 경로를 사용했다.

추가로 확인한 호출 흐름은 다음과 같다.

- DATACOLL: DatacollTcAction L144~168에서 최종값 계산 뒤 발행한다. MAT_DATACOLL도 MatDatacollTcAction L68~85에서 최종값 계산 뒤 해당 상태의 발행을 진행한다. 후자의 전체 반복을 원자적 처리라고 주장하지 않았다.
- 실행 예외: BusinessWorkflowDispatchingActionExecutor L297~346에서 실행 실패를 보존·전파하고, BusinessRuntimeTaskSupport L293~353·L692~714에서 실패 정책으로 넘긴다. 화면의 사유에 계산 규칙의 내부 원인이 항상 직접 표시된다고 쓰지 않았다.
- 자동 수집: DcopAutoCollector L98~120에서 Flow 연결과 해당 모델 버전의 항목을 찾고, L175~205에서 기존 작업·수집 상태를 찾는다. L254~297에서 값 누락·숫자 집계 조건을 처리한다.
- 체크인 비교: ModelPage L1067~1078 → model.api.ts L217~238 → ModelQueryController L70 이후 → ModelDetailResponseSupport의 workflow 응답 → model-diff.util.ts L38~99 → ModelCheckInDiffModal L125~175로 연결된다.
- 권한: UiApiPermissionCache L142~174의 기본 차단과 일치 권한 중 하나의 허용을 기본 catalog 및 SQL 시드의 모델 경로·HTTP method와 대조했다. 현장 DB 권한값으로 서술하지 않았다.

## 절별 사실 대조

각 표에서 같은 검색 원문 줄이 여러 번 나오면 그 줄의 문장들을 나누어 대조한 것이다. 사용자 조치·인계 문장은 코드가 제공하는 진단값에 근거한 안내이며, 자동으로 문제를 복구하는 제품 기능이라는 주장이 아니다.

### 수집 보고가 계산 규칙 오류로 멈췄는지 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 21 첫 문장 | 계산 예외가 나면 원값 보고를 계속하지 않고 전파한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L147~166; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/DatacollTcAction.java @ 7db24f7466bd L144~168 | 예외를 IllegalStateException으로 전파한다. 계산이 발행보다 앞선다. |
| 21 둘째 문장 | 실패 정책이 재시도·최종 실패 처리를 정한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L692~714 | decide 결과에 따라 RETRY·DLQ로 분기한다. 고정 횟수·간격은 쓰지 않았다. |
| 23 첫 문장 | 설비·시각 실패 기록과 예외 원인의 항목·규칙을 대조 요청한다 | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L18~38; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L158~164 | 화면은 설비·사유·발생 시각을 제공한다. 계산 예외는 dcopItemName·calculationRule을 포함한다. 내부 원인은 운영 담당자에게 확인을 요청한다. |
| 23 둘째 문장 | 실패 조회는 소유 문서로 안내한다 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/dlq-quarantine-and-repair-task.md; nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L18~38 | 기존 문서의 Business DLQ 조회 절을 읽었다. 해당 objectKey가 실제 존재하며 절차를 복제하지 않았다. |
| 25 | 모델 담당자에게 항목·계산 규칙·실패 정보를 전달한다 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd L89~103; nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 L307~320 | Dcop Itemes 표기와 두 열의 실제 이름을 확인했다. 예외의 항목·규칙과 화면값을 맞추도록 안내했다. |

### Calculation Rule이 있는데 값이 변하지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 35 첫 문장 | 규칙 원문과 변환 전 값을 확인한다 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd L89~103; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L89~98 | 화면 규칙값이 최종값 후처리에 쓰이는 입력이다. |
| 35 둘째 문장 | 빈 규칙·공백 규칙은 적용하지 않는다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L92~95 | null 및 isBlank 검사로 건너뛴다. 현재 시험 L222~237도 공백 규칙에서 원값을 기대한다. |
| 37 첫 문장 앞부분 | 지원하지 않는 함수명은 예외다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd L66~87 | default가 IllegalArgumentException이다. |
| 37 첫 문장 뒷부분 | add·sub 숫자 해석 불가 시 원값을 유지한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd L264~274 | 피연산자 또는 인자가 숫자로 변환되지 않으면 value를 반환한다. |
| 37 둘째 문장 | toint·tolong의 결과가 없으면 최종값 결정에서 원값이다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd L75~76; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L153~156 | 변환기는 null을 반환할 수 있고 계산 엔진은 null이면 currentValue를 반환한다. 모든 부적절한 숫자 입력을 예외로 처리한다고 쓰지 않았다. |
| 39 첫 문장 | 함수명 오류와 숫자 입력 문제를 나누어 확인한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd L66~87 L264~274 | 두 처리 결과가 다르므로 이름과 입력을 분리해 확인하는 진단 안내다. 함수명 오타가 현재 원값 보고의 정상 경로라는 주장은 하지 않는다. |
| 39 둘째 문장 | 규칙·입력·기대·실제값을 전달한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L147~165 | 규칙과 현재값이 함수 입력이며 변환 결과와 비교하도록 인계한다. 미확인 단위·목표값은 만들지 않았다. |

### Collect가 비어 수집 항목이 빠질 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 49 첫 문장 | Collect의 빈 표시로 연결 여부를 확인한다 | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 L432~463; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L810~814 | Collect의 rows는 draft.dcopItems이며 emptyState 문구는 「수집 항목이 없습니다.」다. |
| 49 둘째 문장 | Flow 연결이 없으면 자동 수집을 건너뛴다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L98~104; nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/datacoll/DcopAutoCollectorTest.java @ 7db24f7466bd L61~69 | 연결 없음에서 즉시 반환하며 시험은 수집 상태 갱신 호출이 없음을 기대한다. 시험은 읽었고 실행하지 않았다. |
| 51 첫 문장 | 정의와 Flow 연결을 나누어 확인한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L98~120; nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 L550~562 | 버전의 항목 풀과 별도의 Flow 연결이 있다. 선택된 키를 항목 풀에서 찾아 카드에 넣는다. |
| 51 둘째 문장 | 정의만 있는 상태로 계산 실패를 확정하지 않는다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L98~104 | 연결 없음은 예외 없이 수집을 생략하는 경로이므로 계산 예외와 구별한다. |
| 53 첫 문장 | 빠진 연결의 편집은 소유 문서로 안내한다 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/model-details-and-flow-editing.md; nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 L550~562 | 기존 문서의 Collect 연결·해제 절차를 읽고 상세 편집 순서는 복제하지 않았다. |
| 53 둘째 문장 | 항목·버전·Flow를 전달한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L98~120 | 현재 Flow 키 및 모델 버전별 항목 풀로 수집 대상을 정하므로 세 대상을 함께 식별하도록 안내한다. |

### Collect는 있는데 값이 비거나 갱신되지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 63 첫 문장 | 대응하는 수신값이 없으면 수집하지 않는다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L254~268 | 허용된 항목명 대체 조회까지 한 뒤 rawValue가 null이면 반환한다. VariableId 조회 하나만 실패해도 항상 생략한다고 쓰지 않았다. |
| 63 둘째 문장 | AVERAGE의 숫자 해석 불가 입력은 집계하지 않는다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L286~297 | toBigDecimalSafe 결과가 null이면 기존 집계에 추가하지 않는다. |
| 63 셋째 문장 | 미수집 항목 최종값은 빈 문자열이다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L78~86 | collectionState에 항목이 없으면 빈 문자열을 넣고 다음 항목으로 간다. |
| 65 첫 문장 | 수집 항목·변수·집계 규칙과 실제 수신값을 대조한다 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd L89~103; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L254~297 | 실제 표 이름과 사용 입력을 연결했다. |
| 65 둘째 문장 | 빈 값만으로 계산 규칙 오류를 확정하지 않는다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd L78~95 | 미수집의 빈 문자열 분기는 Calculation Rule 적용 이전에 종료된다. |
| 67 첫 문장 | 계속 미갱신이면 설비·시각·작업·모델 정보를 전달한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L111~142 L175~205 | 모델 버전·설비·작업 식별값이 수집 대상 상태와 연관된다. 내부 저장소를 사용자가 직접 조회하도록 하지 않는다. |
| 67 둘째 문장 | 담당자는 처리 Flow·수집 상태·작업 존재를 확인한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd L98~142 L175~205 | 항목 미매칭·작업 없음·수집 상태 없음 등 계산 전 생략 경로가 있다. |

### 버전을 바꾼 뒤 Collect만 달라졌을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 77 | 네 버전 복제 경로에서 Collect 연결을 승계한다 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L263~263 L314~314 L361~361 L461~461; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementDetailSupport.java @ 985d17c604dd L159~225 | 네 호출이 공통 복제 지원 코드로 이어지고 Flow·항목 키를 새 키로 치환한다. branch 시험 L220~255도 새 키·대상 버전을 기대한다. 손상된 원본 연결의 복구까지 보장하지 않았다. |
| 79 첫 문장 | 원본·대상 버전의 같은 Flow 연결을 비교한다 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementDetailSupport.java @ 985d17c604dd L159~225 | 복제 입력의 연결과 생성된 연결을 대조하는 안내다. |
| 79 둘째 문장 | Flow Name·Message Name·항목·두 버전을 넘긴다 | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 L274~317 L432~463 | 화면의 실제 식별 칸과 Collect 표시를 확인했다. |
| 81 첫 문장 | 원본만 연결이 있으면 판본과 복제 결과를 확인 요청한다 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementDetailSupport.java @ 985d17c604dd L159~225 | 소스의 수정 동작을 확인했고 현장 판본은 확인하지 않았다. 현재도 항상 소실된다는 과거 증상으로 서술하지 않았다. |
| 81 둘째 문장 | 버전 관리 조작은 소유 문서로 안내한다 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/model-registration-and-branching.md | 기존 branch 생성·Parent Model Commit 절차를 읽고 본문으로 복제하지 않았다. Check Out·Check In 상세는 관련 상세 편집 문서가 소유한다. |

### Collect 변경이 체크인 비교에 나타나지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 95 첫 문장 | 이전 확정 버전과 저장된 EDIT의 Collect 차이가 비교에 포함된다 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L1043~1078; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd L192~207 L378~405; nori-tc-ui-front/src/features/model/components/ModelCheckInDiffModal.tsx @ 9fe69a75dcd3 L125~175 | 서버가 Collect를 values와 열에 넣고 프런트가 동적 열·변경 셀을 렌더한다. 한국어 제목은 ko.json L695다. |
| 95 둘째 문장 | 이름순 정렬로 선택 순서만의 차이는 제거된다 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd L234~244; nori-tc-ui-front/src/features/model/lib/model-diff.util.ts @ 9fe69a75dcd3 L50~58 | 이름을 정렬한 뒤 쉼표 문자열로 만들고 값으로 비교한다. 조회 정렬 시험 L44~85도 정렬된 항목 목록을 기대한다. |
| 97 첫 문장 | 같은 Flow의 저장된 연결을 두 버전에서 비교한다 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L1043~1078 | 이전 버전 선정 및 두 버전의 노드별 조회를 확인했다. |
| 97 둘째 문장 | 비교는 서버 조회값이다 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L1067~1078; nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 L217~238 | 카드의 미저장 상태를 직접 비교하지 않고 GET 결과로 비교를 구성한다. |
| 99 첫 문장 | 실제 연결 차이 미표시는 판본·비교 결과 확인을 요청한다 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd L192~207 L378~405 | Collect 표시 수정의 현재 구현에 근거해 원본·대상·화면값을 인계하도록 안내했다. |
| 99 둘째 문장 | 저장·체크인 순서는 소유 문서로 안내한다 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/model-details-and-flow-editing.md; nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 L550~562 | 기존 저장·비교·체크인 절을 읽었다. Picker 저장과 서버 Save의 절차는 기존 소유 문서에 남겼다. |

### 같은 설비의 시작·종료 순서가 뒤바뀐 것처럼 보일 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 110 첫 문장 | 선행 이벤트를 같은 실행 안에서 지연 후 재시도한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L136~153 L367~399 | while 루프에서 backoff를 기다린 뒤 nextAttempt를 실행한다. 별도 재삽입으로 처리권을 넘기지 않는다. |
| 110 둘째 문장 | 같은 설비의 후행 이벤트가 재시도를 추월하지 않는다 | nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeEngineTest.java @ 7db24f7466bd L629~694 | 선행 0·후행 1을 넣고 실행 순서 0,0,1을 기대한다. 이 세션에서는 시험을 실행하지 않았다. |
| 112 첫 문장 | 설비와 두 이벤트의 식별값·관측 시각·순서를 기록한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L267~274 L389~399 | 설비·메시지·추적·위치가 처리 진단값이다. 사용자에게 내부 로그 접근을 지시하지 않고 이미 관측한 사건 정보를 인계한다. |
| 112 둘째 문장 | 실패 기록이 있으면 원본 위치·사유를 함께 넘긴다 | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L18~38; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1066~1076 | 토픽·파티션·오프셋·사유의 실제 표시 열을 확인했다. 모든 재시도가 DLQ에 남는다고 쓰지 않았다. |
| 114 첫 문장 | 수신·재시도 순서와 수정 포함 여부를 대조 요청한다 | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd L136~153 L389~399; nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeEngineTest.java @ 7db24f7466bd L629~694 | 수정이 다루는 재시도 순서와 수신 순서를 나누어 조사하도록 안내한다. |
| 114 둘째 문장 | 순서 보장을 같은 설비 대기열 내부로 한정한다 | nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeEngineTest.java @ 7db24f7466bd L629~694 | 같은 설비의 대기열 안 후행 이벤트 추월 방지가 근거이며 서로 다른 설비·외부 시스템 전역 순서로 확장하지 않았다. |

### 모델 조회·저장 거부를 처리 실패와 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 123 첫 문장 | 권한 거부와 업무 처리 실패를 분리한다 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | 요청 인가의 차단은 계산 엔진·수집 실행 결과 판정이 아니다. |
| 123 둘째 문장 | 기본 데이터상 GET은 MODEL_READ 또는 MODEL_WRITE, 변경은 MODEL_WRITE다 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L240~256; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L771~774; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | /api/model PREFIX·GET 및 method null(전체) 연결과 일치 권한 중 하나 허용을 대조했다. 실제 사용자 권한을 단정하지 않았다. |
| 125 첫 문장 | 등록 매칭 없음·권한 확인 불가도 차단한다 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~169 | 초기화 실패·catalog 불일치·일치 권한 없음에서 false를 반환한다. |
| 125 둘째 문장 | 수집 규칙을 고치기 전에 요청·대상·문구로 접근 확인을 요청한다 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174; nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L1079~1084 | 거부를 규칙 결함으로 단정하지 않는 인계 안내다. 기존 화면 오류 문구·재시도 단추 설명을 복제하지 않았다. |

## 매뉴얼과 코드가 다른 곳

이번 원천은 수정 커밋 세 개와 그 현재 코드·시험이다. 매뉴얼에서 새 동작 주장을 가져오지 않았으며, 이 문서 작성에서 보고할 매뉴얼·코드 충돌은 없다. 과거 커밋의 결함 설명을 현재 동작으로 잘못 옮기는 문제는 수정 포함 판본 조건으로 방지했다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 서비스에 세 수정이 적용됐다는 확정 | 배포 판본·현장 서비스에 접근하지 않았다. 현재 HEAD 포함 사실과 현장 적용은 다르다. | 운영 담당자 |
| 현장의 실제 재시도 횟수·간격과 장애의 확정 원인 | 운영 설정·실제 사건·원문 메시지를 확보하거나 재현하지 않았다. 커밋 설명의 예시 수치를 현재 현장값으로 쓰지 않았다. | 운영 담당자·모델 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 본문으로 복제하지 않은 사실 |
|---|---|
| 00.rag/user/model-details-and-flow-editing.md | 상세 탭 열기, Dcop Itemes 편집, Collect 추가·해제의 조작 순서, Save·Check In 절차, 필터·Action Data Index 편집, 이미 설명한 오류 표시와 단추 |
| 00.rag/user/model-registration-and-branching.md | branch 생성·Parent Model Commit 조작 및 버전 관리 입력 제한 |
| 00.rag/user/flow-action-and-message-reference.md | 액션·메시지 전체 목록과 각 역할. 이번 작성 시 해당 검색 원문은 아직 없으므로 목록의 소유 범위만 적용했다. |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 처리실패 메뉴·카드 조작, 열 전체 설명, 상태·사유 코드별 뜻, 삭제·재처리 가능 여부 |
| 00.rag/user/permission-code-reference.md | 권한 코드별 전체 의미. 본문에는 원인 구별에 필요한 기본 API 연결 조건만 남겼다. |

기존 관련 문서는 model-details-and-flow-editing·model-registration-and-branching·dlq-quarantine-and-repair-task를 읽었다. 본문은 조작을 재설명하지 않고 원인 구별에 필요한 화면값과 인계 경로를 소유한다. 다른 문서를 수정할 결함은 이 작성 범위에서 확인하지 않았다.

## 검사 결과

기계 검사는 Git Bash로 두 번 실행했다. 첫 실행은 122줄 판본, 마지막 실행은 원천 메타를 보완한 125줄 최종 판본이다. 두 번 모두 exit 0·violations=0이었다. 마지막 본문 편집 이후 다시 검사했다. 검사기를 바꾸거나 우회하지 않았다.

명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/model-flow-troubleshooting.md
```

실제 실행 파일은 `C:/Program Files/Git/bin/bash.exe`다. GNU bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS를 확인했다.

최종 검사 원문 출력의 마지막 10줄:

```text
§ L101-114 [29] 같은 설비의 시작·종료 순서가 뒤바뀐 것처럼 보일 때   parent=306 children=3 sources=5
    L110-110 문단 96  | 지연 재시도 순서 수정이 포함된 판본은 같은 설비의 선행 이벤트를 대기 후 그 자리에서 재시도한다. 뒤에 대기 중인 이벤트가 선행 이벤트의 재시도를 추월하지 않게 처리한다.
    L112-112 문단 113  | 시작·종료 순서가 이상하면 설비 ID와 두 이벤트의 식별값·관측 시각·실제 보인 순서를 기록한다. 실패 기록이 있으면 「Business DLQ」의 「토픽」·「파티션」·「오프셋」·「사유」도 함께 전달한다.
    L114-114 문단 95  | 운영 담당자에게 수신 순서와 재시도 실행 순서, 지연 재시도 수정의 현장 판본 포함 여부를 대조하도록 요청한다. 이 보장은 같은 설비의 대기열 안 재시도 순서에 한정한다.
§ L116-125 [24] 모델 조회·저장 거부를 처리 실패와 구별하기   parent=255 children=2 sources=3
    L123-123 문단 129  | 모델 상세 조회나 저장 요청이 권한 거부로 끝나면 계산·수집 실패 조사와 나누어 처리한다. 저장소 기본 API 권한 데이터에서 모델 조회는 MODEL_READ·MODEL_WRITE 중 하나, 변경은 MODEL_WRITE로 연결된다.
    L125-125 문단 125  | 권한 목록에 맞는 요청이 없거나 권한 정보를 확인할 수 없는 상태도 서버가 차단한다. 거부 문구만으로 수집 규칙을 바꾸지 않고, 운영 관리자에게 누른 단추·모델·버전·시각·전체 오류 문구를 전달해 접근 권한 확인을 요청한다.
SUMMARY sections=8 children=23 maxChild=146 maxParent=365
CORPUS docs=31/53 sections=466 part={admin=7, user=24} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=7}
TOTAL files=31 targets=1 violations=0
```

최종 검색 원문은 12,583바이트다. H1 1개, H2 8개, 자식 23개, 최대 자식 146자·최대 부모 365자다. MASK·CROSS VIOLATION 출력은 없었다. 금지 구조·메타·표·원천 위치·길이·인코딩의 미해소 위반은 0건이다. 검색 원문에는 자료 표·순서 목록을 사용하지 않았다.

사람 검사는 40절 미만이므로 아래 8절 전부를 읽고 수행했다. 각 행의 네 판정은 제목만으로 질문 식별·원천 일치·독립 답·중복 경계 순서다.

| 절 | 제목·원천·독립 답·중복 판정 | 확인한 경계 |
|---|---|---|
| 수집 보고가 계산 규칙 오류로 멈췄는지 구별하기 | 적합·일치·충족·적합 | 예외 발생 때의 중단·인계이며 모든 입력 오류가 실패한다는 주장이 아니다. |
| Calculation Rule이 있는데 값이 변하지 않을 때 | 적합·일치·충족·적합 | 빈 규칙·숫자 해석 불가의 원값 반환과 함수명 예외를 구별한다. |
| Collect가 비어 수집 항목이 빠질 때 | 적합·일치·충족·적합 | 정의와 연결을 구별하며 추가·저장 절차는 기존 문서로 보낸다. |
| Collect는 있는데 값이 비거나 갱신되지 않을 때 | 적합·일치·충족·적합 | 연결이 있는 상태의 값·집계·작업 상태를 다루므로 미연결 절과 다르다. |
| 버전을 바꾼 뒤 Collect만 달라졌을 때 | 적합·일치·충족·적합 | 복제 전후 연결 대조를 소유하며 현장 수정 적용은 인계한다. |
| Collect 변경이 체크인 비교에 나타나지 않을 때 | 적합·일치·충족·적합 | 저장값·비교 표시 문제를 다루며 체크인 조작 절차를 복제하지 않는다. |
| 같은 설비의 시작·종료 순서가 뒤바뀐 것처럼 보일 때 | 적합·일치·충족·적합 | 같은 설비의 재시도 추월 방지를 외부 시스템의 전역 순서 보장으로 넓히지 않는다. |
| 모델 조회·저장 거부를 처리 실패와 구별하기 | 적합·일치·충족·적합 | 조회·변경 API 권한은 최소 진단 조건이며 기존 실패 문구 설명은 복제하지 않는다. |

제목의 정확 중복·정보 없는 틀 반복, 본문 복제, 사용처 근거 없는 이름 뜻풀이, 잘린 문장·조사 오류, 오답 예시, admin objectKey·관리 문서 열람 지시는 전수 대조에서 미해소 0건이다. 23개 자식의 마스킹 전후 출력에서 필수 화면값·함수명·원문 경로가 보존되는 것을 읽었다. 의미가 같은 사실은 소유 절을 구별했고, 다른 문서의 값은 진단에 필요한 최소 조건만 사용했다.

제품 시험·빌드·화면 실행·현장 재현은 실행하지 않았다. 코드·시험 기대값 읽기와 문서 검사로 한정한 요청이므로 제품 동작을 새로 실측한 PASS로 보고하지 않는다. 서버 접속·업로드·배포도 하지 않았다.

## 자원 정리와 남은 작업

검사용 임시 파일·별도 스크립트·브라우저·컨테이너·서비스를 만들지 않았다. 지정 `work/`의 파일 목록은 비어 있음을 확인했다. 메인 세션이 준비한 작업 폴더와 기존 로그는 소유 삭제 대상이 아니므로 보존했다. 이번 세션이 정리할 소유 임시 자원은 없다.

완료된 것은 검색 원문 한 개와 이 작성 기록, 문서 자체검사다. 남은 사용자 결정은 없으며 현장 적용·실제 원인 확정은 본문의 인계 대상이다. 다음 작업은 메인 세션의 원천 직접 대조다. 새 증거 없이 제품 시험·업로드·다른 문서 수정으로 범위를 넓히지 않는다. 독립 AI 호출·provider 별도 사용량·추가 비용은 발생 내역이 없고, 이 세션의 전체 provider 사용량·금액·총 wall time은 별도 측정하지 않았다.

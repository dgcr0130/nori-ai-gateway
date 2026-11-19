# 모델 상세와 Flow 편집 작성 기록

## 대상
- objectKey: `00.rag/user/model-details-and-flow-editing.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문 SHA-256: `f1fe4b761becdbc76b3da54868d28b9d9d68f5ec79e97f45097e72d845a8625b`
- 검색 원문 줄 수: 449
- 절 수: 28
- 확인일: 2026-11-19 (한국 시간)
- 범위: 문서 8/53 하나의 조사·작성·자체검사. 독립 세션 재위임·제품 변경·커밋·업로드 없음.

## 원천 판본

아래 첫 표는 2026-11-18 확인 이력이다. 2026-11-19 PLC 순서 입력·저장 변경은 이어지는 파일별 SHA-256 표를 기준으로 한다. 바뀌지 않은 사실의 기준 commit과 줄 범위는 유지했다.

2026-11-18 최초 작성 당시에는 HEAD의 추적 파일을 직접 읽었다. 인용 원천과 추가 대조 파일마다 `git -c safe.directory=<해당 저장소 절대경로> -C <저장소> status --short -- <파일 목록>` 및 `git ... diff --exit-code HEAD -- <동일 목록>`을 확인했다. safe.directory는 이 읽기 명령 한 번에만 적용했고 전역 설정을 변경하지 않았다. 모든 status stdout은 비었고 status·diff의 종료코드는 0이었다.

| 저장소 | HEAD 12자리 | 확인 파일 수 | 작업 트리 결과 |
|---|---|---:|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 23 | 대상 파일 수정 없음; HEAD diff 없음 |
| nori-tc-exec | f1d5e0a8e727 | 3 | 대상 파일 수정 없음; HEAD diff 없음 |
| nori-tc-ui-backend | 985d17c604dd | 18 | 대상 파일 수정 없음; HEAD diff 없음 |
| nori-tc-common | 6fe71f6b2da6 | 3 | 대상 파일 수정 없음; HEAD diff 없음 |
| nori-tc-business | 7db24f7466bd | 3 | 대상 파일 수정 없음; HEAD diff 없음 |

2026-11-19 변경 원천 확인:

| 저장소·파일 | 기준 HEAD | 작업 트리 상태 | 현재 SHA-256·위치 |
|---|---|---|---|
| nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx | 9fe69a75dcd3 | M | 14e703baba62a7ee57c83b9d09981b48827689e9fa2c84403a01d789f704e026 L65~81·L562~645 |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java | 985d17c604dd | M | aa567bd80d4277dc0839572ed14fb63a0337e2a50ea415074448045488c41d74 L477~528 |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java | 985d17c604dd | ?? | 241e8e34c68b25713a47362cabe3e7574400445f71e33dc4ad1aa7c47d716d6a L12~20 |

UiApiExceptionHandler.java는 nori-tc-ui-backend @ 985d17c604dd의 변경 없는 파일이며 L89~98에서 UiBadRequestException을 400 INVALID_REQUEST로 바꾸는 것을 확인했다. PlcRegisterOrder.java L19~20의 원문은 `PLC Register <문맥> 값 '<입력값>'은 허용되지 않습니다. 허용값: BIG_ENDIAN, LITTLE_ENDIAN (빈값은 기본 BIG_ENDIAN).`이며 문맥에는 모델 버전 키·행·그룹·변수·byteOrder(8열) 또는 wordOrder(9열)가 들어간다. 오류 전문은 검색 원문에서 복제하지 않았다.

Git은 외부 저장소 첫 조회에서 dubious ownership을 반환하여 그 명령으로 판본을 확정하지 않았다. 저장소별 읽기 명령에 한정한 safe.directory 지정 뒤 HEAD·상태를 확인했다. status는 사용자 전역 ignore 파일 접근 거부 경고를 냈으며, 위 대상 추적 파일에 대한 HEAD diff exit 0도 함께 확인했다. 비밀 파일은 읽지 않았다.

전체를 읽은 기준 문서는 `docs/rag-document-writing-rules.md`, `docs/tc-rag-document-list.md`이다. 작업 원천은 `nori-tc-exec/docs/completion-report/07-user-manual.md` §5.4 L1283~1459이며 화면·다국어·API·서버·저장 구현으로 대조했다. 앞선 `model-registration-and-branching.md`와 `equipment-model-parameter-and-jar-update.md`의 겹치는 영역을 읽었다.

추가 대조한 시험은 실행 결과를 재사용한 것이 아니라 아래 기대값을 읽은 것이다.

| 시험 원천 | 읽고 확인한 기대값 |
|---|---|
| nori-tc-ui-front/tests/unit/model-flow-editor.test.ts @ 9fe69a75dcd3 L276~280 | Trigger는 SECS에서 true, Socket·PLC에서 false이다. |
| nori-tc-ui-front/tests/unit/model-detail-editor.test.ts @ 9fe69a75dcd3 L52~104 | 표준 조건의 parse/build 보존, 상대 경로 위반 입력의 raw 보존을 기대한다. |
| nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/ModelFlowCommandServiceTest.java @ 985d17c604dd L57~105, L140~159 | Flow 신규 이름·메시지 빈 값 및 non-EDIT 거부, 기존 액션 빈 값 거부를 기대한다. |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPortTest.java @ 985d17c604dd L416~455 | PLC 그룹 메시지명 중복 및 소문자 이름의 거부를 기대한다. |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaModelManagementPortTest.java @ 985d17c604dd L336~388 | 체크인 버전 자동 생성, 파라미터·메시지 복제와 EDIT 삭제를 검증한다. |

## 절별 사실 대조

아래 표는 최종 원문의 각 문장과 표 자료 행을 모두 대응시킨다. 열 이름은 원문의 표 머리글과 함께 읽었다. 조작·인계 권고는 확인한 화면 상태에서 사용자가 할 수 있는 조치이며, 운영 복구가 성공한다는 주장으로 쓰지 않았다.

### 1. 모델 버전의 상세 탭 열기

사람 검사: 트리 탐색을 반복하지 않고 표 행의 더블 클릭과 탭 선택부터 답한다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L18 | 「Model 정보」 표에서 편집할 버전의 행을 두 번 누른다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleOpenModelDetail L639~665 / nori-tc-ui-front/src/features/model/components/ModelInfoTable.tsx @ 9fe69a75dcd3 ; 행 더블 클릭 L174~182 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.1 절차 L1287~1298 | 코드·표시 문구와 일치 |
| L18 | 왼쪽 트리의 모델명을 두 번 누르는 조작과 구별한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleOpenModelDetail L639~665 / nori-tc-ui-front/src/features/model/components/ModelInfoTable.tsx @ 9fe69a75dcd3 ; 행 더블 클릭 L174~182 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.1 절차 L1287~1298 | 코드·표시 문구와 일치 |
| L19 | 열린 상세 탭을 눌러 작업할 모델·버전을 선택한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleOpenModelDetail L639~665 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 탭·Reference 렌더 L711~751 | 코드·표시 문구와 일치 |
| L19 | 「Reference」에서 표나 MDF를 고르고, 「Message Flows」에서 Flow를 고른다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleOpenModelDetail L639~665 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 탭·Reference 렌더 L711~751 | 코드·표시 문구와 일치 |

### 2. 통신 방식에 따라 다른 Reference 항목

사람 검사: 공통 항목과 통신 방식별 추가 항목을 실제 Reference 렌더 순서에 맞춰 구분한다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L27 | 상세 탭의 「Reference」에는 다음 항목이 표시된다. | nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; 상세 항목·표시 문구 L250~320 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록 L872~893 | 코드·표시 문구와 일치 |
| L27 | Flow는 별도의 「Message Flows」에서 선택한다. | nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; 상세 항목·표시 문구 L250~320 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록 L872~893 | 코드·표시 문구와 일치 |
| L31 | \| 공통 \| Model Parameter·MES Message·MDF·Dcop Itemes \| | nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; 상세 항목·표시 문구 L250~320 | 코드·표시 문구와 일치 |
| L32 | \| SECS 추가 항목 \| SECS Message·Variableides·ReportIdes·EventIdes \| | nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; 상세 항목·표시 문구 L250~320 | 코드·표시 문구와 일치 |
| L33 | \| Socket 추가 항목 \| Socket Message \| | nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; 상세 항목·표시 문구 L250~320 | 코드·표시 문구와 일치 |
| L34 | \| PLC 추가 항목 \| PLC Group·PLC Register \| | nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; 상세 항목·표시 문구 L250~320 | 코드·표시 문구와 일치 |

### 3. Check Out으로 EDIT 작업본 열기

사람 검사: 편집 가능한 branch·EDIT 복제·기본 변경 요청 권한을 함께 확인했다. 모든 저장 API가 소유자 검사까지 한다고 확대하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L44 | 폐기되지 않은 branch의 상세 탭에서 「Check Out」을 누른다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleCheckOut L991~1026 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkoutBranchVersion L278~319 | 코드·표시 문구와 일치 |
| L44 | 서버는 선택한 버전의 상세를 복사한 EDIT 작업본을 만들며, 본인 소유의 EDIT가 이미 있으면 그 작업본을 연다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleCheckOut L991~1026 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkoutBranchVersion L278~319 | 코드·표시 문구와 일치 |
| L46 | 체크아웃 뒤에는 EDIT 탭이 활성화되고 「Save」·「Undo」·「Check In」이 표시된다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleCheckOut L991~1026 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용·단추 조건 L327~349 | 코드·표시 문구와 일치 |
| L46 | root에는 「Check Out」이 없고 DEPRECATED branch에서는 비활성화된다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleCheckOut L991~1026 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용·단추 조건 L327~349 | 코드·표시 문구와 일치 |
| L48 | 저장소 기본 API 권한 데이터 기준으로 체크아웃·저장·업로드·Flow 삭제·체크인·Undo의 변경 요청에는 MODEL_WRITE가 필요하다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 변경 API 권한 L240~256 | 코드·표시 문구와 일치 |

### 4. 다른 편집자가 점유했거나 EDIT를 다시 열었을 때

사람 검사: 다른 소유자와 본인 EDIT 재진입을 나눴다. 명시적 Check Out 조건을 상태 동기화 코드로 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L59 | 다른 사용자가 EDIT를 점유하면 점유 안내와 함께 「Check Out」이 비활성화된다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 점유자·편집 상태 L279~307 / nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 기존 EDIT 확인 L128~168 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 편집 안내·점유 문구 L770~780 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; ensureEditableByCurrentUser L332~337 | 코드·표시 문구와 일치 |
| L59 | 편집자에게 작업 종료를 요청한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 점유자·편집 상태 L279~307 / nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 기존 EDIT 확인 L128~168 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 편집 안내·점유 문구 L770~780 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; ensureEditableByCurrentUser L332~337 | 확인한 증상·대상에 따른 사용자 인계 권고; 내부 접근 조치 없음 |
| L59 | 계속 막히면 모델·버전·발생 시각·표시 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 점유자·편집 상태 L279~307 / nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 기존 EDIT 확인 L128~168 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 편집 안내·점유 문구 L770~780 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; ensureEditableByCurrentUser L332~337 | 확인한 증상·대상에 따른 사용자 인계 권고; 내부 접근 조치 없음 |
| L61 | 본인 EDIT를 다시 열었어도 「읽기 전용입니다. 수정하려면 Check Out이 필요합니다.」가 나오면 「Check Out」을 눌러 편집을 재개한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 명시적 체크아웃 조건 L383~412 / nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 기존 EDIT 확인 L128~168 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 편집 안내·점유 문구 L770~780 | 코드·표시 문구와 일치 |
| L61 | 편집 가능 상태에서는 「편집 중입니다. Save로 저장하고 Check In으로 확정할 수 있습니다.」가 표시된다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 명시적 체크아웃 조건 L383~412 / nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 기존 EDIT 확인 L128~168 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 편집 안내·점유 문구 L770~780 | 코드·표시 문구와 일치 |

### 5. Reference 표의 행 추가·수정·삭제와 저장

사람 검사: 표의 작업 단위와 현재 표만 저장하는 분기를 확인했다. 표 변경을 전체 모델 자동 저장으로 설명하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L71 | 체크아웃한 상세 탭에서 「Reference」의 편집할 표를 선택한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 표 편집·저장 분기 L667~681 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 행 추가·표 렌더 L983~1116 | 코드·표시 문구와 일치 |
| L72 | 「행 추가」로 행을 만든다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 행 추가·표 렌더 L983~1116 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 입력·목록 셀 L421~529 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |
| L72 | 글자 셀은 두 번 눌러 수정하고, 선택 목록 셀은 한 번 눌러 고른다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 행 추가·표 렌더 L983~1116 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 입력·목록 셀 L421~529 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |
| L72 | 행 끝 휴지통 단추로 행을 제거한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 행 추가·표 렌더 L983~1116 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 입력·목록 셀 L421~529 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |
| L73 | 상세 탭 오른쪽 위 「Save」를 누른다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 표 편집·저장 분기 L667~681 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |
| L73 | 마우스를 올리면 「Reference 저장」이 표시된다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 표 편집·저장 분기 L667~681 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |
| L73 | 저장 응답의 행이 표에 다시 표시된다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 표 편집·저장 분기 L667~681 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |
| L75 | 「Save」는 현재 선택한 Reference 표를 저장한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 표 편집·저장 분기 L667~681 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |
| L75 | 여러 표를 바꿨으면 각 표를 선택해 저장한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 표 편집·저장 분기 L667~681 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602 | 코드·표시 문구와 일치 |

### 6. 파라미터와 메시지 표에서 비울 수 없는 칸

사람 검사: 화면 필수 별표의 부재를 서버 requiredCell 검사와 함께 적었다. 각 표의 표시 열에 대응했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L84 | Reference 표에는 필수 표시가 없지만 서버는 아래 칸의 빈 값을 거부한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 표 열 L37~53 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 필수 셀 L211~274 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1392~1393 | 코드·표시 문구와 일치 |
| L84 | 파라미터 값은 「Parameter Value」, 설명은 「Description」에서 수정한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 표 열 L37~53 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 필수 셀 L211~274 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1392~1393 | 코드·표시 문구와 일치 |
| L88 | \| Model Parameter \| Parameter Name \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 표 열 L37~53 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 필수 셀 L211~274 | 코드·표시 문구와 일치 |
| L89 | \| SECS Message \| SECS Message Name \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 표 열 L37~53 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 필수 셀 L211~274 | 코드·표시 문구와 일치 |
| L90 | \| Socket Message \| Socket Message Name \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 표 열 L37~53 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 필수 셀 L211~274 | 코드·표시 문구와 일치 |
| L91 | \| MES Message \| MES Message Name \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 표 열 L37~53 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 필수 셀 L211~274 | 코드·표시 문구와 일치 |

### 7. SECS의 변수·보고·이벤트 표 입력

사람 검사: 세 SECS 표의 필수 칸, VID Type 열거값, Enabled 변환값을 각각 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L101 | 체크아웃한 SECS 모델의 「Reference」에서 표를 고르고 다음 필수 칸을 채운 뒤 「Save」를 누른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; SECS 표 열 L54~70 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; SECS 표 저장 L280~323 | 코드·표시 문구와 일치 |
| L105 | \| Variableides \| VariableId·VID Type \| Description \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; SECS 표 열 L54~70 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; SECS 표 저장 L280~323 | 코드·표시 문구와 일치 |
| L106 | \| ReportIdes \| ReportId \| VariableIdes·Enabled·Description \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; SECS 표 열 L54~70 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; SECS 표 저장 L280~323 | 코드·표시 문구와 일치 |
| L107 | \| EventIdes \| EventId \| ReportIdes·Enabled·Description \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; SECS 표 열 L54~70 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; SECS 표 저장 L280~323 | 코드·표시 문구와 일치 |
| L109 | 「VID Type」은 SVID·DVID·ECID·CEID 중에서 입력한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; SECS 표 저장 L280~323 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; Enabled 입력 L627~637 / nori-tc-ui-backend/ui/tc-ui-domain/src/main/java/com/nori/tc/ui/domain/common/model/UiVariableIdType.java @ 985d17c604dd ; VID Type L6~11 | 코드·표시 문구와 일치 |
| L109 | 「Enabled」에 값을 적으면 true·false 또는 1·0, y·n, yes·no로 입력한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; SECS 표 저장 L280~323 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; Enabled 입력 L627~637 / nori-tc-ui-backend/ui/tc-ui-domain/src/main/java/com/nori/tc/ui/domain/common/model/UiVariableIdType.java @ 985d17c604dd ; VID Type L6~11 | 코드·표시 문구와 일치 |

### 8. PLC Group을 먼저 저장하기

사람 검사: PLC 그룹 필수값·이름 패턴·중복 거부·그룹 이름 변경 시 자식 삭제를 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L120 | 체크아웃한 PLC 모델의 「PLC Group」에서 「Message Name」·「Area」·「Start Address」·「Read Count」·「Poll Period (ms)」를 채운다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Group 열 L104~112 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 | 코드·표시 문구와 일치 |
| L120 | 주소·개수·주기는 정수로 입력하고 「Save」를 누른다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Group 열 L104~112 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 | 코드·표시 문구와 일치 |
| L122 | 「Message Name」은 대문자로 시작하며 대문자·숫자·밑줄로 구성한다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; PLC 입력·경고 L543~637 / nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Message Name 패턴 L292~302 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 | 코드·표시 문구와 일치 |
| L122 | 밑줄을 맨 끝에 쓰거나 연속해서 쓰지 않는다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; PLC 입력·경고 L543~637 / nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Message Name 패턴 L292~302 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 | 코드·표시 문구와 일치 |
| L122 | 중복도 허용하지 않는다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; PLC 입력·경고 L543~637 / nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Message Name 패턴 L292~302 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 | 코드·표시 문구와 일치 |
| L122 | 형식이 틀리면 셀에 빨간 테두리와 안내가 나오며 서버도 저장을 거부한다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; PLC 입력·경고 L543~637 / nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Message Name 패턴 L292~302 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 | 코드·표시 문구와 일치 |
| L124 | 기존 그룹의 「Message Name」을 바꾸거나 행을 삭제해 저장하면 그 그룹의 레지스터도 삭제된다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 그룹 삭제 시 레지스터 삭제 L315~362 | 코드·표시 문구와 일치 |
| L124 | 변경한 그룹의 「PLC Register」를 다시 정의한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 그룹 삭제 시 레지스터 삭제 L315~362 | 코드·표시 문구와 일치 |

### 9. PLC Register의 소속 그룹과 변수 입력

사람 검사: 존재하는 그룹·숫자·레이아웃의 기존 조건을 보존하고, 상세 저장의 순서 값 정규화·빈 값·잘못된 값의 변경 전 거부를 현재 코드로 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L135 | 「PLC Group」을 저장한 뒤 「PLC Register」에서 「Group」을 고르고 「Word Offset」·「Variable Name」·「Data Type」을 채워 「Save」를 누른다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Group 목록 조회 L314~325 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Register 열 L113~127 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 그룹·타입·레이아웃 검사 L467~524 | 코드·표시 문구와 일치 |
| L135 | 「Word Offset」은 정수이다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Group 목록 조회 L314~325 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Register 열 L113~127 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 그룹·타입·레이아웃 검사 L467~524 | 코드·표시 문구와 일치 |
| L137 | 필요한 「Length」·「Bit Index」·「Byte Index」는 정수, 「Scale」은 숫자로 입력한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Register 열 L113~127 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 그룹·타입·레이아웃 검사 L467~524 | 코드·표시 문구와 일치 |
| L137 | 「BCD Signed」·「Char Encoding」·「Description」도 같은 표에서 편집한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Register 열 L113~127 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 그룹·타입·레이아웃 검사 L467~524 | 코드·표시 문구와 일치 |
| L139 | 서버는 존재하는 그룹인지, 영역과 자료형이 맞는지, 읽기 범위를 벗어나거나 겹치는 레지스터가 있는지 검사한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 그룹·타입·레이아웃 검사 L467~524 | 코드·표시 문구와 일치 |
| L139 | 이 수정이 들어간 판본에서 「Byte Order」·「Word Order」는 공백 제거·대문자 변환 후 검사하고 빈 값은 비운다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters 순서 값 검사·저장 L477~528 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~20 | 코드·표시 문구와 일치 |
| L139 | 허용값 밖이면 기존 행 변경 전에 거부하므로 표시된 행·칸을 고친다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters 순서 값 검사·저장 L477~528 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~20 | 코드·표시 문구와 일치 |

### 10. PLC 표의 선택 목록에서 값 고르기

사람 검사: 기존 목록 행을 보존하고 Byte Order·Word Order의 두 허용값을 현재 렌더 코드와 대조했다. 빈 선택은 공통 안내로 설명한다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L150 | 체크아웃한 PLC 모델의 다음 셀은 목록에서 고른다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 값 L50~63 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 렌더 L614~637 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC 필수 입력 L424~488 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; PLC_ORDER_OPTIONS·순서 칸 판별 L65~81 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; createPlcColumns 목록 선택 L562~645 | 코드·표시 문구와 일치 |
| L150 | 목록의 「—」는 값을 비우지만 필수 칸을 비우면 저장이 거부된다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 값 L50~63 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 렌더 L614~637 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC 필수 입력 L424~488 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; PLC_ORDER_OPTIONS·순서 칸 판별 L65~81 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; createPlcColumns 목록 선택 L562~645 | 코드·표시 문구와 일치 |
| L154 | \| PLC Group의 Area \| COIL·DISCRETE_INPUT·INPUT_REGISTER·HOLDING_REGISTER \| | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 값 L50~63 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 렌더 L614~637 | 코드·표시 문구와 일치 |
| L155 | \| PLC Register의 Group \| 저장된 PLC Group의 Message Name \| | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 값 L50~63 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 렌더 L614~637 | 코드·표시 문구와 일치 |
| L156 | \| PLC Register의 Data Type \| BOOL·SINT8·USINT8·INT16·UINT16·INT32·UINT32·INT64·UINT64·FLOAT32·FLOAT64·BCD16·BCD32·BCD64·STRING·BYTES \| | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 값 L50~63 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 렌더 L614~637 | 코드·표시 문구와 일치 |
| L157 | \| PLC Register의 BCD Signed \| true·false \| | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 값 L50~63 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 렌더 L614~637 | 코드·표시 문구와 일치 |
| L158 | \| Byte Order·Word Order \| BIG_ENDIAN·LITTLE_ENDIAN \| | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; PLC_ORDER_OPTIONS·순서 칸 판별 L65~81 / nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; createPlcColumns 목록 선택 L562~645 | 코드·표시 문구와 일치 |

### 11. Dcop Itemes에서 수집 항목 정의하기

사람 검사: DCOP 정의의 저장과 Flow 연결을 구별했다. Collection Rule에 빈 선택이 없음을 코드로 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L169 | 체크아웃한 모델의 「Dcop Itemes」에서 「행 추가」를 누른다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; EventId·수집 규칙 L421~498 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; LAST 초기값 L541~554 | 코드·표시 문구와 일치 |
| L171 | 「Dcop Item Name」은 필수이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 열 L89~103 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; replaceDcopItems L378~406 | 코드·표시 문구와 일치 |
| L171 | 「VariableId」·「Calculation Rule」·「Order Rule」을 편집하고 「Save」로 정의를 저장한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 열 L89~103 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; replaceDcopItems L378~406 | 코드·표시 문구와 일치 |
| L171 | 「Order Rule」에 값을 넣으면 정수여야 한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 열 L89~103 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; replaceDcopItems L378~406 | 코드·표시 문구와 일치 |
| L173 | 「Collection Rule」은 LAST·FIRST·AVERAGE·MIN·MAX 중에서 고른다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; EventId·수집 규칙 L421~498 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; LAST 초기값 L541~554 | 코드·표시 문구와 일치 |
| L173 | 새 행의 초기값은 LAST이며 이 목록에는 빈 값 선택이 없다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; EventId·수집 규칙 L421~498 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; LAST 초기값 L541~554 | 코드·표시 문구와 일치 |
| L175 | Socket 표에는 「EventId」 열이 없다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; EventId·수집 규칙 L421~498 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; EventId 목록 L300~312 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 열 L89~103 | 코드·표시 문구와 일치 |
| L175 | SECS 표에서는 같은 모델의 「EventIdes」에 저장된 번호를 목록에서 고른다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; EventId·수집 규칙 L421~498 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; EventId 목록 L300~312 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 열 L89~103 | 코드·표시 문구와 일치 |
| L175 | 목록의 「—」로 비울 수 있다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; EventId·수집 규칙 L421~498 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; EventId 목록 L300~312 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 열 L89~103 | 코드·표시 문구와 일치 |

### 12. MDF XML 올리고 내용 확인하기

사람 검사: Upload가 직접 저장하는 경로와 UTF-8·XML 검사, 읽기 전용 표시를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L187 | 체크아웃한 상세 탭에서 「MDF」를 선택한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Upload·XML 표시 L753~783 | 코드·표시 문구와 일치 |
| L187 | 이 화면에서는 「Save」 대신 「Upload」가 표시된다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Upload·XML 표시 L753~783 | 코드·표시 문구와 일치 |
| L188 | 「Upload」로 비어 있지 않은 UTF-8 XML 파일을 고른다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 업로드 결과 L605~623 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; uploadMdf L130~160 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; saveMdf L186~205 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; UTF-8·XML 검사 L763~796 | 코드·표시 문구와 일치 |
| L188 | 업로드 요청 자체가 MDF를 저장한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 업로드 결과 L605~623 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; uploadMdf L130~160 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; saveMdf L186~205 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; UTF-8·XML 검사 L763~796 | 코드·표시 문구와 일치 |
| L189 | 화면의 XML 내용을 확인한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; XML 파일·내용 L942~978 | 코드·표시 문구와 일치 |
| L189 | XML 표시 칸은 직접 수정할 수 없다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; XML 파일·내용 L942~978 | 코드·표시 문구와 일치 |
| L191 | 인코딩이나 XML 형식 오류가 표시되면 파일을 수정해 다시 올린다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; UTF-8·XML 검사 L763~796 | 코드·표시 문구와 일치 |

### 13. Message Flows에서 기존 Flow 열기

사람 검사: 그룹명 아래 메시지를 고르는 흐름, 카드 순서와 Message Name 후보 공급원을 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L201 | 「Message Flows」의 Flow 이름을 눌러 펼치고 그 아래 메시지 이름을 누른다. | nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; 그룹 펼침·메시지 선택 L50~109 | 코드·표시 문구와 일치 |
| L203 | 오른쪽 카드에서 「Identity」·「Filter」·「Action」·「Collect」를 확인한다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Identity·Trigger·Filter L274~391 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action·Collect L393~464 | 코드·표시 문구와 일치 |
| L203 | SECS에는 「Trigger」도 표시된다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Identity·Trigger·Filter L274~391 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action·Collect L393~464 | 코드·표시 문구와 일치 |
| L203 | 아래 구역은 카드를 내려서 본다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Identity·Trigger·Filter L274~391 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action·Collect L393~464 | 코드·표시 문구와 일치 |
| L203 | 편집하려면 상세 탭을 체크아웃한다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Identity·Trigger·Filter L274~391 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action·Collect L393~464 | 코드·표시 문구와 일치 |
| L205 | 「Message Name」의 후보는 같은 모델의 통신 방식별 「SECS Message」·「Socket Message」·「PLC Group」 중 해당 항목과 「MES Message」에서 가져온다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 선택 후보 L246~270 | 코드·표시 문구와 일치 |
| L205 | 필요한 메시지가 없으면 Reference에서 정의를 저장한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 선택 후보 L246~270 | 코드·표시 문구와 일치 |

### 14. Add Flow로 새 Flow 만들기

사람 검사: MDF 선택 중 Add Flow 숨김, 신규 이름 필수와 취소, 성공 뒤 선택 전환을 확인했다. 동일 조합의 무조건 거부 주장은 제거했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L217 | 체크아웃한 상세 탭에서 MDF 이외의 Reference 항목을 고른 뒤 「Message Flows」 옆 「Add Flow」를 누른다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 추가 가능 조건 L223~235 / nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; Add Flow L136~152 | 코드·표시 문구와 일치 |
| L218 | 새 카드의 「Identity」에서 「Flow Name」을 적고 「Message Name」을 고른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/ModelFlowCommandService.java @ 985d17c604dd ; 필수 이름 L62~66 | 코드·표시 문구와 일치 |
| L218 | 두 칸 모두 비울 수 없다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/ModelFlowCommandService.java @ 985d17c604dd ; 필수 이름 L62~66 | 코드·표시 문구와 일치 |
| L219 | 필요한 내용을 채우고 상세 탭 오른쪽 위 「Save」를 누른다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 저장 L611~649 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; 신규 취소 L466~477 | 코드·표시 문구와 일치 |
| L219 | 저장이 성공하면 저장된 Flow가 선택된다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 저장 L611~649 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; 신규 취소 L466~477 | 코드·표시 문구와 일치 |
| L219 | 저장 전 카드 아래 「취소」는 새 카드 입력을 버린다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 저장 L611~649 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; 신규 취소 L466~477 | 코드·표시 문구와 일치 |
| L221 | 새 Flow를 만들 때는 기존 목록의 「Flow Name」과 「Message Name」을 확인해 다른 조합을 사용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 중복 검사 L65~96 | 코드·표시 문구와 일치 |

### 15. Flow 이름과 Action Name 저장 조건

사람 검사: 저장된 Flow Name의 화면 수정 불가와 기존 Action Name 필수, 신규 빈 액션의 POST-only 분기, 공통 길이 검사를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L234 | 저장한 Flow의 「Flow Name」은 수정할 수 없다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Flow Name 읽기 전용 L274~315 | 코드·표시 문구와 일치 |
| L234 | 이름을 바꾸려면 새 Flow를 만들고 기존 Flow를 삭제한다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Flow Name 읽기 전용 L274~315 | 코드·표시 문구와 일치 |
| L236 | 기존 Flow를 저장할 때 「Action Name」은 필수이다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action Name 입력 L393~401 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/ModelFlowCommandService.java @ 985d17c604dd ; 기존 Flow 필수값 L81~95 | 코드·표시 문구와 일치 |
| L236 | 카드의 자유 입력 칸에 실행할 액션을 적는다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action Name 입력 L393~401 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/ModelFlowCommandService.java @ 985d17c604dd ; 기존 Flow 필수값 L81~95 | 코드·표시 문구와 일치 |
| L238 | 새 Flow에서 「Action Name」을 비우고 「Save」를 누르면 이름과 메시지만 저장되고 액션은 TODO로 남는다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 Flow의 후속 저장 조건 L611~649 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 신규 액션 초기값 L47~79 | 코드·표시 문구와 일치 |
| L238 | 함께 입력했던 Trigger·Filter·Action Data Index는 저장되지 않는다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 Flow의 후속 저장 조건 L611~649 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 신규 액션 초기값 L47~79 | 코드·표시 문구와 일치 |
| L238 | 이 구역까지 저장하려면 「Action Name」도 채운다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 Flow의 후속 저장 조건 L611~649 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 신규 액션 초기값 L47~79 | 코드·표시 문구와 일치 |
| L240 | 서버 길이 제한은 Flow Name·Message Name 각각 1000자, Transaction Id 2000자, Flow Filter의 저장 JSON 4000자이다. | nori-tc-common/db/tc-db-core/src/main/java/com/nori/tc/db/core/model/upsert/UpsertTcModelMessageFlow.java @ 6fe71f6b2da6 ; 길이 검사 L29~51 / nori-tc-common/db/tc-db-core/src/main/java/com/nori/tc/db/core/model/upsert/ModelFieldLengthValidator.java @ 6fe71f6b2da6 ; 길이 상한 L10~13 | 코드·표시 문구와 일치 |

### 16. SECS Flow의 Trigger 입력

사람 검사: 화면 실제 문구와 SECS 분기를 보존했다. 후보 조회는 체크아웃한 SECS 모델에서 Dcop Itemes를 선택한 상태일 때 켜지는 것을 확인했다. Dcop Itemes → Flow → Event Id 선택과 EventIdes 저장·재선택·인계를 단계로 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L256 | SECS 카드의 「Trigger」에는 선택 항목 「Event Id」와 입력 칸 「Transaction Id」가 있다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Trigger 입력 L319~359 / nori-tc-ui-front/src/features/model/lib/model-flow-editor.ts @ 9fe69a75dcd3 ; SECS만 Trigger 표시 L168~170 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; matchesSecsKey L193~205 | 코드·표시 문구와 일치 |
| L256 | 「Transaction Id」를 비우면 모든 transactionId에 맞는다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Trigger 입력 L319~359 / nori-tc-ui-front/src/features/model/lib/model-flow-editor.ts @ 9fe69a75dcd3 ; SECS만 Trigger 표시 L168~170 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; matchesSecsKey L193~205 | 코드·표시 문구와 일치 |
| L256 | Socket·PLC 카드에는 「Trigger」가 없다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Trigger 입력 L319~359 / nori-tc-ui-front/src/features/model/lib/model-flow-editor.ts @ 9fe69a75dcd3 ; SECS만 Trigger 표시 L168~170 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; matchesSecsKey L193~205 | 코드·표시 문구와 일치 |
| L258 | 체크아웃한 SECS 모델에서 「Reference」가 「Dcop Itemes」일 때만 「Event Id」 후보를 불러온다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 현재 Reference·읽기 전용 판정 L218~226 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Event Id 후보 조건 L300~312 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록·선택 L872~893 | 코드·표시 문구와 일치 |
| L260 | 「Reference」에서 「Dcop Itemes」를 누른다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록·선택 L872~893 | 코드·표시 문구와 일치 |
| L261 | 「Message Flows」의 Flow 이름을 펼쳐 메시지 이름을 누른다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 선택 L495~498 / nori-tc-ui-front/src/features/model/stores/model-ui.store.ts @ 9fe69a75dcd3 ; Flow 선택은 Reference 노드를 바꾸지 않음 L238~243 | 코드·표시 문구와 일치 |
| L262 | 「Trigger」의 「Event Id」 목록에서 번호를 고른다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Trigger 입력 L319~359 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Event Id 후보 조건 L300~312 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 카드 후보 전달 L585~591 | 코드·표시 문구와 일치 |
| L264 | 번호가 없으면 「EventIdes」에 저장된 번호를 확인한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Event Id 후보 조건 L300~312 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록·선택 L872~893 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; Reference 저장 L575~602 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 선택 L495~498 / nori-tc-ui-front/src/features/model/stores/model-ui.store.ts @ 9fe69a75dcd3 ; Flow 선택은 Reference 노드를 바꾸지 않음 L238~243 | 코드·표시 문구와 일치 |
| L264 | 없으면 표에 저장한 뒤 Dcop Itemes와 Flow를 다시 선택한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Event Id 후보 조건 L300~312 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록·선택 L872~893 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; Reference 저장 L575~602 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 선택 L495~498 / nori-tc-ui-front/src/features/model/stores/model-ui.store.ts @ 9fe69a75dcd3 ; Flow 선택은 Reference 노드를 바꾸지 않음 L238~243 | 코드·표시 문구와 일치 |
| L264 | 그래도 없으면 모델·버전·저장값·화면 결과·시각을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Event Id 후보 조건 L300~312 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록·선택 L872~893 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; Reference 저장 L575~602 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 선택 L495~498 / nori-tc-ui-front/src/features/model/stores/model-ui.store.ts @ 9fe69a75dcd3 ; Flow 선택은 Reference 노드를 바꾸지 않음 L238~243 | 확인한 증상에 따른 사용자 인계 권고; 내부 접근 조치 없음 |

### 17. Flow Filter에서 조건 묶음 만들기

사람 검사: 조건·그룹 생성과 제거, 창 저장과 상세 Save를 구별했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L275 | 체크아웃한 Flow 카드의 「Filter」에서 「Flow Filter」 옆 「편집...」을 누른다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 카드 편집 반영 L514~529 | 코드·표시 문구와 일치 |
| L276 | 「조건 추가」로 조건 행을 만들고 「그룹 추가」로 하위 묶음을 만든다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156 / nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 그룹 조작 L568~634 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 필터 문구 L738~751 | 코드·표시 문구와 일치 |
| L276 | 묶음마다 「and」 또는 「or」를 고른다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156 / nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 그룹 조작 L568~634 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 필터 문구 L738~751 | 코드·표시 문구와 일치 |
| L276 | 휴지통 단추로 조건이나 하위 그룹을 제거한다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156 / nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 그룹 조작 L568~634 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 필터 문구 L738~751 | 코드·표시 문구와 일치 |
| L277 | 창의 「저장」으로 카드에 반영한다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156 / nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L219~228 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 카드 편집 반영 L514~529 | 코드·표시 문구와 일치 |
| L277 | 「취소」는 창의 변경을 버린다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156 / nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L219~228 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 카드 편집 반영 L514~529 | 코드·표시 문구와 일치 |
| L277 | 서버에 반영하려면 상세 탭의 「Save」도 누른다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156 / nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L219~228 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 카드 편집 반영 L514~529 | 코드·표시 문구와 일치 |

### 18. Flow Filter의 조건 한 줄 채우기

사람 검사: from·상대 경로·연산·expected의 모든 입력 조건을 서버 파서와 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L287 | 「from」은 data·metadata·param 중 고른다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; from·상대 경로 L275~295 | 코드·표시 문구와 일치 |
| L287 | 「path」에는 선택한 from 아래의 상대 경로를 적는다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; from·상대 경로 L275~295 | 코드·표시 문구와 일치 |
| L287 | data.·metadata.·param.을 경로 앞에 다시 붙이지 않는다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; from·상대 경로 L275~295 | 코드·표시 문구와 일치 |
| L289 | 「comparison」은 equals·not_equals·greater_than·greater_than_or_equal·less_than·less_than_or_equal·contains·in 중 고른다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 선택값 L3~13 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 조건 필수값 L138~168 | 코드·표시 문구와 일치 |
| L291 | 「path」와 「expected」는 비우지 않는다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 조건 필수값 L138~168 | 코드·표시 문구와 일치 |
| L291 | 「expected」에는 JSON 값으로 숫자·true·false·배열 등을 적고 문자열은 큰따옴표로 감싼다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 조건 필수값 L138~168 | 코드·표시 문구와 일치 |
| L291 | JSON 객체는 허용하지 않는다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 조건 필수값 L138~168 | 코드·표시 문구와 일치 |

### 19. transforms를 차례대로 추가하기

사람 검사: 한 줄당 변환 입력과 실제 순차 적용 코드를 연결했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L301 | Flow Filter 조건의 「transforms」에서 「transform 추가」로 한 줄씩 늘린다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowFilterEvaluator.java @ 7db24f7466bd ; 순차 적용·비교 L310~315 | 코드·표시 문구와 일치 |
| L301 | 비교할 값을 위에서 아래 순서로 변환한다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowFilterEvaluator.java @ 7db24f7466bd ; 순차 적용·비교 L310~315 | 코드·표시 문구와 일치 |
| L301 | 변환이 없으면 원래 값을 비교한다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowFilterEvaluator.java @ 7db24f7466bd ; 순차 적용·비교 L310~315 | 코드·표시 문구와 일치 |
| L303 | 인자가 없으면 trim처럼 함수명만 적는다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; transform 입력 표현 L184~224 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1405~1409 | 코드·표시 문구와 일치 |
| L303 | 인자가 있으면 add(2) 또는 split(",", 0)처럼 괄호 안에 적고 여러 인자는 쉼표로 나눈다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; transform 입력 표현 L184~224 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1405~1409 | 코드·표시 문구와 일치 |
| L303 | 문자열 인자는 큰따옴표로 감싼다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; transform 입력 표현 L184~224 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1405~1409 | 코드·표시 문구와 일치 |
| L303 | 각 줄의 휴지통 단추로 변환을 제거한다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; transform 입력 표현 L184~224 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1405~1409 | 코드·표시 문구와 일치 |

### 20. 공백·대소문자·숫자를 바꾸는 transform

사람 검사: 지원 함수 이름을 실제 switch와 연결했고, 숫자 예시 2의 연산도 해당 구현으로 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L311 | Flow Filter의 「transforms」에는 다음 함수명과 인자를 입력할 수 있다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 지원 변환 L66~87 | 코드·표시 문구와 일치 |
| L315 | \| trim \| 문자열 앞뒤 공백을 제거한다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 지원 변환 L66~87 | 코드·표시 문구와 일치 |
| L316 | \| lower·upper \| 문자열을 소문자·대문자로 바꾼다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 지원 변환 L66~87 | 코드·표시 문구와 일치 |
| L317 | \| length \| 문자열 길이로 바꾼다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 지원 변환 L66~87 | 코드·표시 문구와 일치 |
| L318 | \| toint·tolong \| 숫자로 해석할 수 있는 값을 정수로 바꾼다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 지원 변환 L66~87 | 코드·표시 문구와 일치 |
| L319 | \| add(2)·sub(2) \| 숫자에 2를 더하거나 뺀다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 지원 변환 L66~87 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; add·sub L264~274 | 코드·표시 문구와 일치 |

### 21. 문자열을 자르거나 덧붙이는 transform

사람 검사: 문자열 함수 인자와 0 기반 위치·끝 위치 제외를 실제 구현으로 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L327 | Flow Filter의 「transforms」에 다음과 같이 적는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 문자열 변환 L96~171 | 코드·표시 문구와 일치 |
| L327 | split의 조각 번호와 substring의 시작 위치는 0부터 센다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 문자열 변환 L96~171 | 코드·표시 문구와 일치 |
| L331 | \| split(",", 0) \| 쉼표로 나눈 첫 조각을 쓴다. 인자를 모두 비워도 같은 기준이다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 문자열 변환 L96~171 | 코드·표시 문구와 일치 |
| L332 | \| substring(0, 3) \| 시작 위치부터 끝 위치 직전까지 자른다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 문자열 변환 L96~171 | 코드·표시 문구와 일치 |
| L333 | \| substringleft(3)·substringright(3) \| 문자열 앞·뒤에서 지정 길이만큼 자른다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 문자열 변환 L96~171 | 코드·표시 문구와 일치 |
| L334 | \| addstring("X") \| 문자열 뒤에 인자를 붙인다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 문자열 변환 L96~171 | 코드·표시 문구와 일치 |
| L335 | \| extractportinfo("PORTID") \| Port 정보에서 지정 항목을 추출한다. \| | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; extractportinfo L177~210 | 코드·표시 문구와 일치 |

### 22. Action Data Index에서 보고 필드 연결하기

사람 검사: MDF 선택·XML 표시·필드 추가와 정렬·삭제를 확인했다. 빈 필드 생략과 같은 필드 뒤 값 우선은 직렬화 reduce로 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L347 | 체크아웃한 Flow 카드의 「Action Data Index」 옆 「편집...」을 누른다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; MDF 선택·미리보기 L247~300 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; MDF·필드 문구 L815~832 | 코드·표시 문구와 일치 |
| L347 | 「MDF Message」에서 서식을 고르고 「선택된 XML Preview」를 확인한다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; MDF 선택·미리보기 L247~300 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; MDF·필드 문구 L815~832 | 코드·표시 문구와 일치 |
| L348 | 「Fields」의 「field 추가」로 행을 만든다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 필드·순서·제거 L309~555 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 필드 직렬화 L606~644 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 필드 필수값 L171~205 | 코드·표시 문구와 일치 |
| L348 | 「Field」에 보고 필드명을 적고 「from」은 data·metadata·param에서 고른다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 필드·순서·제거 L309~555 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 필드 직렬화 L606~644 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 필드 필수값 L171~205 | 코드·표시 문구와 일치 |
| L348 | 「path」는 상대 경로로 적고 변환이 필요하면 「transforms」를 추가한다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 필드·순서·제거 L309~555 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 필드 직렬화 L606~644 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 필드 필수값 L171~205 | 코드·표시 문구와 일치 |
| L349 | 「순서」 손잡이를 끌거나 위·아래 단추로 순서를 바꾸고 「작업」의 휴지통으로 행을 제거한다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 필드·순서·제거 L309~555 | 코드·표시 문구와 일치 |
| L350 | 창의 「저장」 뒤 상세 탭의 「Save」를 누른다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L562~570 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 필드 직렬화 L606~644 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 필드 필수값 L171~205 | 코드·표시 문구와 일치 |
| L350 | 빈 「Field」 행은 저장 내용에서 빠지며 같은 필드명을 반복하면 뒤 행의 값이 남는다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L562~570 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 필드 직렬화 L606~644 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 필드 필수값 L171~205 | 코드·표시 문구와 일치 |
| L350 | 필드명을 구별하고 path를 채운다. | nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L562~570 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 필드 직렬화 L606~644 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 필드 필수값 L171~205 | 코드·표시 문구와 일치 |

### 23. Collect에 DCOP 항목 연결하고 해제하기

사람 검사: 저장된 Flow 선행 조건·후보 선택·화살표·연결 제거·창 저장 후 Save를 확인했다. DCOP 본체 삭제와 혼동하지 않는다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L361 | 체크아웃한 저장된 Flow의 「Collect」에서 「DCOP 추가」를 누른다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Collect·저장 전 제한 L432~463 | 코드·표시 문구와 일치 |
| L361 | 새 Flow는 먼저 「Save」해야 이 단추가 활성화된다. | nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Collect·저장 전 제한 L432~463 | 코드·표시 문구와 일치 |
| L362 | 「DCOP Item 선택」 창 왼쪽에서 항목을 검색·체크하고 가운데 화살표로 오른쪽 「현재 Flow 의 DCOP」에 추가한다. | nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 검색·추가 L113~170 / nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 선택·제거·저장 L212~360 | 코드·표시 문구와 일치 |
| L362 | 이미 연결된 항목은 「매핑됨」으로 표시된다. | nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 검색·추가 L113~170 / nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 선택·제거·저장 L212~360 | 코드·표시 문구와 일치 |
| L363 | 오른쪽 휴지통으로 한 항목을 해제하거나 「모두 제거」로 비운다. | nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 선택·제거·저장 L212~360 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 선택 결과는 카드에 반영 L534~563 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 연결 갱신 L137~139 | 코드·표시 문구와 일치 |
| L363 | 창의 「저장」 뒤 상세 탭의 「Save」를 눌러 연결 변경을 저장한다. | nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 선택·제거·저장 L212~360 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 선택 결과는 카드에 반영 L534~563 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 연결 갱신 L137~139 | 코드·표시 문구와 일치 |
| L365 | 후보가 없으면 「Reference」의 「Dcop Itemes」에서 항목을 먼저 저장한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 선택 결과는 카드에 반영 L534~563 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 연결 갱신 L137~139 | 코드·표시 문구와 일치 |
| L365 | 연결을 해제해도 Reference의 수집 항목 정의는 남는다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 선택 결과는 카드에 반영 L534~563 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 연결 갱신 L137~139 | 코드·표시 문구와 일치 |

### 24. Flow 삭제를 확정하거나 취소하기

사람 검사: 메뉴 Delete와 카드 삭제, 확인 창의 삭제·취소, 즉시 서버 반영과 연결만 삭제하는 범위를 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L377 | 체크아웃한 상세 탭의 「Message Flows」에서 메시지 행을 오른쪽 클릭해 「Delete」를 고른다. | nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; Delete 메뉴 L90~105 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; 삭제 단추 L259~269 | 코드·표시 문구와 일치 |
| L377 | 저장된 Flow 카드 머리글의 「삭제」도 같은 확인 창을 연다. | nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; Delete 메뉴 L90~105 / nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; 삭제 단추 L259~269 | 코드·표시 문구와 일치 |
| L379 | 「Flow 를 삭제하시겠습니까?」에서 「삭제」를 누르면 EDIT의 해당 Flow와 Collect 연결이 즉시 삭제된다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 삭제 요청·결과 L652~664 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 확인 창 L1122~1138 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; Flow·연결 삭제 L144~160 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 확인 문구 L791~792 | 코드·표시 문구와 일치 |
| L379 | 별도 「Save」는 필요하지 않다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 삭제 요청·결과 L652~664 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 확인 창 L1122~1138 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; Flow·연결 삭제 L144~160 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 확인 문구 L791~792 | 코드·표시 문구와 일치 |
| L379 | 「취소」는 삭제 요청을 보내지 않는다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 삭제 요청·결과 L652~664 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 확인 창 L1122~1138 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; Flow·연결 삭제 L144~160 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 확인 문구 L791~792 | 코드·표시 문구와 일치 |
| L381 | 삭제 성공 뒤 목록에서 해당 메시지 행이 사라진다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 삭제 요청·결과 L652~664 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; Flow·연결 삭제 L144~160 | 코드·표시 문구와 일치 |
| L381 | Reference의 DCOP 항목 정의는 삭제하지 않는다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 삭제 요청·결과 L652~664 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; Flow·연결 삭제 L144~160 | 코드·표시 문구와 일치 |

### 25. 저장한 변경을 Check In 전에 비교하기

사람 검사: 서버 저장 내용으로 비교하고 MDF를 제외하는 점, 이전 버전 없음·조회 실패 분기를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L392 | 바꾼 Reference 표와 Flow를 각각 「Save」한 뒤 「Check In」을 누른다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095 | 코드·표시 문구와 일치 |
| L392 | 비교와 체크인은 서버에 저장된 내용을 사용하므로 입력만 하고 저장하지 않은 값은 먼저 저장한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095 | 코드·표시 문구와 일치 |
| L394 | 이전 확정 버전이 있으면 「변경사항 미리보기」에서 추가·변경·삭제 건수와 값을 확인한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095 / nori-tc-ui-front/src/features/model/lib/model-page-helpers.ts @ 9fe69a75dcd3 ; MDF 제외 L34~37 / nori-tc-ui-front/src/features/model/components/ModelCheckInDiffModal.tsx @ 9fe69a75dcd3 ; 비교 표시·단추 L223~308 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 안내 L690~699 | 코드·표시 문구와 일치 |
| L394 | 「OK」는 버전 저장 창으로 넘어가고 「Cancel」은 비교 창을 닫는다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095 / nori-tc-ui-front/src/features/model/lib/model-page-helpers.ts @ 9fe69a75dcd3 ; MDF 제외 L34~37 / nori-tc-ui-front/src/features/model/components/ModelCheckInDiffModal.tsx @ 9fe69a75dcd3 ; 비교 표시·단추 L223~308 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 안내 L690~699 | 코드·표시 문구와 일치 |
| L394 | MDF는 이 비교에서 제외된다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095 / nori-tc-ui-front/src/features/model/lib/model-page-helpers.ts @ 9fe69a75dcd3 ; MDF 제외 L34~37 / nori-tc-ui-front/src/features/model/components/ModelCheckInDiffModal.tsx @ 9fe69a75dcd3 ; 비교 표시·단추 L223~308 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 안내 L690~699 | 코드·표시 문구와 일치 |
| L396 | 이전 버전이 없으면 비교 없이 저장 창을 연다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 조회 실패 L728~728 | 코드·표시 문구와 일치 |
| L396 | 비교 조회가 실패해도 오류 문구와 함께 저장 창으로 넘어가므로, 「Cancel」로 닫고 저장 내용과 조회 오류를 확인한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 조회 실패 L728~728 | 코드·표시 문구와 일치 |

### 26. Check In으로 새 버전 확정하기

사람 검사: Description 선택 입력·서버 버전 생성·EDIT 삭제·새 탭·Cancel과 Undo를 확인했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L407 | 「Save your changes?」 창의 「Description」에 변경 설명을 적고 「Save」를 누른다. | nori-tc-ui-front/src/features/model/components/ModelCheckInModal.tsx @ 9fe69a75dcd3 ; 설명·단추 L83~149 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkinBranchEditVersion L328~368 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelCheckinRequest.java @ 985d17c604dd ; 선택 설명 L8~11 | 코드·표시 문구와 일치 |
| L407 | 설명은 생략할 수 있다. | nori-tc-ui-front/src/features/model/components/ModelCheckInModal.tsx @ 9fe69a75dcd3 ; 설명·단추 L83~149 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkinBranchEditVersion L328~368 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelCheckinRequest.java @ 985d17c604dd ; 선택 설명 L8~11 | 코드·표시 문구와 일치 |
| L407 | 버전은 서버가 새로 생성하며 사용자가 버전명을 입력하는 칸은 없다. | nori-tc-ui-front/src/features/model/components/ModelCheckInModal.tsx @ 9fe69a75dcd3 ; 설명·단추 L83~149 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkinBranchEditVersion L328~368 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelCheckinRequest.java @ 985d17c604dd ; 선택 설명 L8~11 | 코드·표시 문구와 일치 |
| L409 | 성공하면 저장된 EDIT 내용을 복사한 새 branch 버전의 탭이 열리고 EDIT는 제거된다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 체크인 결과 L1148~1176 / nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 저장 요청 L171~188 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkinBranchEditVersion L328~368 | 코드·표시 문구와 일치 |
| L409 | 새 탭은 읽기 전용이다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 체크인 결과 L1148~1176 / nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 저장 요청 L171~188 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkinBranchEditVersion L328~368 | 코드·표시 문구와 일치 |
| L411 | 「Cancel」은 확정을 보류하고 EDIT 작업본을 남긴다. | nori-tc-ui-front/src/features/model/components/ModelCheckInModal.tsx @ 9fe69a75dcd3 ; 설명·단추 L83~149 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 체크인 결과 L1148~1176 | 코드·표시 문구와 일치 |
| L411 | 창의 「Undo」는 EDIT 작업본을 버리는 조작이다. | nori-tc-ui-front/src/features/model/components/ModelCheckInModal.tsx @ 9fe69a75dcd3 ; 설명·단추 L83~149 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 체크인 결과 L1148~1176 | 코드·표시 문구와 일치 |

### 27. Undo와 탭 닫기의 차이

사람 검사: Undo가 버전 삭제 요청을 보내는 것과 탭 닫기가 서버 요청을 보내지 않는 것을 대조했다. 확인 창이 없는 실제 경로를 명시했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L425 | 편집 전체를 버리려면 상세 탭의 「Undo」를 누른다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleUndoCheckIn L1098~1145 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Undo 단추 L753~767 / nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; 버전 삭제 요청 L587~603 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; 버전 삭제 L101~116 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelCrudPort.java @ 985d17c604dd ; 버전 삭제 L181~194 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 버전별 상세 연쇄 삭제 L121~362 | 코드·표시 문구와 일치 |
| L425 | 별도 확인 창 없이 EDIT와 그 안의 저장 내용까지 제거하고 같은 branch의 이전 확정 버전을 연다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleUndoCheckIn L1098~1145 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Undo 단추 L753~767 / nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; 버전 삭제 요청 L587~603 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; 버전 삭제 L101~116 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelCrudPort.java @ 985d17c604dd ; 버전 삭제 L181~194 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 버전별 상세 연쇄 삭제 L121~362 | 코드·표시 문구와 일치 |
| L427 | 돌아갈 이전 버전이 없으면 Undo를 거부하는 문구가 나온다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleUndoCheckIn L1098~1145 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Undo 거부 L732~734 | 코드·표시 문구와 일치 |
| L427 | 모델·버전과 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleUndoCheckIn L1098~1145 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Undo 거부 L732~734 | 확인한 증상·대상에 따른 사용자 인계 권고; 내부 접근 조치 없음 |
| L429 | 탭의 닫기 단추는 탭만 닫는다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 탭 닫기 L685~691 | 코드·표시 문구와 일치 |
| L429 | 서버의 EDIT 작업본과 점유를 해제하는 요청은 보내지 않는다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 탭 닫기 L685~691 | 코드·표시 문구와 일치 |
| L429 | 편집을 끝내려면 목적에 맞게 「Check In」 또는 「Undo」를 사용한다. | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 탭 닫기 L685~691 | 코드·표시 문구와 일치 |

### 28. 상세 조회나 저장이 실패했을 때 넘길 정보

사람 검사: 조회·저장 오류 영역과 고정 문구/서버 문구를 구별했다. raw JSON 상태와 사용자 역할 범위 안의 인계 정보를 적었다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L445 | 상세·Flow 조회 실패는 내용 영역에, Reference 저장·체크아웃 오류는 상세 탭에 표시된다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 작업 오류 L849~851 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 조회 오류 L916~940 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 조회 오류 L1026~1029 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 저장 오류 L594~609 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 문구 우선 L11~12 | 코드·표시 문구와 일치 |
| L445 | Flow 생성 요청이나 저장된 Flow 수정 요청의 오류는 카드 머리글에 표시된다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 작업 오류 L849~851 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 조회 오류 L916~940 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 조회 오류 L1026~1029 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 저장 오류 L594~609 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 문구 우선 L11~12 | 코드·표시 문구와 일치 |
| L445 | 저장·체크아웃 실패는 서버 문구가 있으면 그 문구를 표시한다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 작업 오류 L849~851 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 조회 오류 L916~940 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 조회 오류 L1026~1029 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 저장 오류 L594~609 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 문구 우선 L11~12 | 코드·표시 문구와 일치 |
| L445 | 이 영역에는 별도 「재시도」 단추가 없다. | nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 작업 오류 L849~851 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 조회 오류 L916~940 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 조회 오류 L1026~1029 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 저장 오류 L594~609 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 문구 우선 L11~12 | 코드·표시 문구와 일치 |
| L447 | Flow Filter·Action Data Index 창에 조건 표 대신 JSON 입력 칸과 원문 유지 안내가 나오면 기존 값을 편집기가 해석하지 못한 상태이다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; JSON 입력 L177~197 / nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; JSON 입력 L223~242 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 해석 실패 원문 보존 L495~604 | 코드·표시 문구와 일치 |
| L447 | 모델·버전·대상 Flow와 안내 문구를 모델 담당자에게 전달한다. | nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; JSON 입력 L177~197 / nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; JSON 입력 L223~242 / nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 해석 실패 원문 보존 L495~604 | 확인한 증상·대상에 따른 사용자 인계 권고; 내부 접근 조치 없음 |
| L449 | 입력 오류는 표시된 항목을 고쳐 저장한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; API 인가 L160~174 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.4 오류와 대처 L1447~1459 | 코드·표시 문구와 일치 |
| L449 | 권한 거부는 운영 관리자에게 요청 권한 확인을 맡긴다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; API 인가 L160~174 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.4 오류와 대처 L1447~1459 | 확인한 증상·대상에 따른 사용자 인계 권고; 내부 접근 조치 없음 |
| L449 | 해결되지 않으면 모델명·버전·Reference 항목 또는 Flow Name과 Message Name·누른 단추·시각·전체 오류 문구를 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; API 인가 L160~174 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.4 오류와 대처 L1447~1459 | 확인한 증상·대상에 따른 사용자 인계 권고; 내부 접근 조치 없음 |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 실제 코드 | 본문 반영 |
|---|---|---|---|
| Flow 카드 문구 | U L1364~1365의 EventId·TransactionId | FlowCard.tsx L326, L346은 Event Id·Transaction Id이다. | 화면에 있는 공백을 보존했다. |
| Action Name | U L1367은 선택 입력으로 설명한다. | ModelFlowCommandService.java L91~95는 수정 시 필수이다. ModelDetailPanel.tsx L611~649는 신규 액션이 빈 경우 후속 수정 요청을 생략한다. | 기존 Flow 필수와 신규 이름·메시지만 저장되는 경계를 구분했다. |
| 선택 목록의 빈 값 | U L1385는 모든 목록에 —가 있다고 한다. | modelDetailColumnDefs.tsx L482~498의 Collection Rule은 5개 규칙만 표시하며 빈 항목이 없다. | 수집 규칙에는 빈 선택이 없다고 썼다. |
| PLC 메시지명 | U L1387~1390은 빨간 안내가 저장을 막지 않는다고 한다. | 화면 Save 자체의 사전 차단과 별개로 JpaModelDetailCommandPort.java L427~434가 잘못된 형식과 중복을 거부한다. | 화면 안내와 서버 저장 거부를 함께 썼다. |
| 체크인 비교 | U L1321~1323, L1441~1442는 비교 뒤 저장 순서로 설명한다. | ModelPage.tsx L1052~1055는 이전 버전 없음, L1079~1084는 비교 실패 시 저장 창으로 진행한다. model-page-helpers.ts L34~37은 MDF를 비교에서 제외한다. | 예외 분기·오류 표시·MDF 제외를 적었다. |
| Flow 이름 중복 | U L1454는 동일 Flow Name 행 오류를 공통 안내로 든다. | 현재 Flow 카드에는 기존 평면 표의 중복 이름 표시 경로가 없다. JpaModelFlowCommandPort.java L65~96의 신규 저장은 공통 Store upsert이며 TcModelMessageFlowJpaStore.java의 resolveEntity는 같은 모델 버전·workflowName·messageName을 조회해 기존 엔티티를 재사용한다. | 이름만 같으면 무조건 거부된다는 문장을 쓰지 않았다. 새 Flow는 기존 목록과 다른 조합을 쓰도록 안내했다. |
| Event Id 후보 | U L1364는 SECS 선택 항목으로 안내한다. | ModelDetailPanel.tsx L218~226·L300~312·L872~893과 store L238~243을 대조했다. 화면에서 workflow는 선택할 수 없으며 Flow 선택도 Reference를 바꾸지 않는다. | 체크아웃한 SECS 모델에서 Dcop Itemes 선택 → Flow 선택 → Event Id 선택 순서를 썼다. 번호가 없으면 EventIdes 저장 여부를 확인하고 먼저 저장한 뒤 재선택하며, 계속 없으면 운영 담당자에게 인계한다. |
| PLC 순서 칸의 입력 | U @ f1d5e0a8e727 L1371~1383은 여섯 열만 선택 목록으로 안내하며 두 순서 칸은 포함하지 않는다. | modelDetailColumnDefs.tsx의 현재 SHA 판본 L65~81·L562~645는 Byte Order·Word Order를 목록으로 렌더한다. 상세 저장은 PlcRegisterOrder.normalizeOptional로 검사한다. | 두 칸을 선택 목록 표에 추가하고 상세 저장의 정규화·빈 값·허용값 밖 입력 거부를 반영했다. |

원문 §5.4의 다른 불완전한 안내도 코드로 보완했다. Reference 필수 칸, PLC 그룹 이름 변경 시 레지스터 삭제, DCOP 선택 창의 저장과 상세 Save 분리, Check In 전 각 표·Flow 저장, Undo의 즉시 버전 삭제와 탭 닫기 차이를 본문에 반영했다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 동일한 Flow Name이면 모든 Flow 저장 경로가 반드시 거부한다는 일반화 | 실제 카드 저장과 공통 JPA Store가 이를 보장하지 않는다. 평면 표 경고를 현재 카드의 서버 계약으로 옮기지 않았다. | 모델 편집 기능 개발 담당 |
| 현장 배포 판본·사용자별 실제 권한·현재 편집 점유자 | 이번 범위는 소스 대조이며 현장 서버·DB·계정 상태 조회를 하지 않았다. 본문에는 기본 API 권한 데이터라는 조건만 썼다. | 운영 관리자 |

원천 부족으로 문서의 주 질문에 답하지 못한 부분이나 사용자 결정이 필요한 항목은 없다. Event Id 후보의 현재 소스 조건은 확인한 범위로 문서화했고 실행 환경에서 재현했다고 주장하지 않았다.

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에서 반복하지 않은 내용 |
|---|---|
| 00.rag/user/model-registration-and-branching.md | 화면 진입 기본 권한 설명·Model 정보 트리 탐색·root 등록·Maker 수정·branch 생성·Parent Model Commit·모델 단위 삭제와 거부 조건 |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 설비 파라미터 편집·설비에 모델과 JAR 적용·적용 버전 확인 |
| 00.rag/user/deployment-workflow.md | 배포 등록·실행·결과 이력·재실행 |
| 00.rag/user/flow-action-and-message-reference.md | 액션·메시지 종류별 업무 의미·처리 역할의 상세 설명 |
| 00.rag/user/plc-modbus-register-and-type-reference.md | 영역·데이터 타입·주소·배율의 상세 의미와 레지스터 제약 |
| 00.rag/user/permission-code-reference.md | 권한 코드 전체의 의미·부여 방법 |
| 00.rag/user/error-message-and-code-reference.md | 오류 문자열 전체 목록·코드별 상세 뜻풀이 |
| 00.rag/user/model-flow-troubleshooting.md | 실행 중 계산·Collect·이벤트 순서 문제의 원인 진단 |

앞선 문서의 MODEL_WRITE 설명은 이번 편집 변경 요청의 최소 선행조건으로만 사용했다. UI 공통 표 조작·로그인 절차는 반복하지 않았다. Filter와 Action Data Index의 from 선택값은 각각의 창을 조작하는 최소 입력 조건으로만 포함했고, 변환 함수 설명은 이 문서의 변환 절에서 한 번만 소유한다.

## 검사 결과

아래는 2026-11-18 검사 이력이다. 이번 2026-11-19 최종 검사 결과는 마지막 보완 절에 별도로 남긴다. 당시 기계 검사 명령은 Git Bash에서 다음과 같다. 검사기를 고치지 않았고 mask-v1 적용 결과를 확인했다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/model-details-and-flow-editing.md
```

최초 작성에서 검사기를 4회 실행했고 이번 결함 보완에서 2회 추가 실행했다. 이번 마지막 실행 종료코드 0, command wall time 6.1874909초이다. 마지막 실행의 출력 끝 10줄은 다음과 같다.

```text
    L420-420 문단 90  | 편집 전체를 버리려면 상세 탭의 「Undo」를 누른다. 별도 확인 창 없이 EDIT와 그 안의 저장 내용까지 제거하고 같은 branch의 이전 확정 버전을 연다.
    L422-422 문단 60  | 돌아갈 이전 버전이 없으면 Undo를 거부하는 문구가 나온다. 모델·버전과 문구를 운영 담당자에게 전달한다.
    L424-424 문단 97  | 탭의 닫기 단추는 탭만 닫는다. 서버의 EDIT 작업본과 점유를 해제하는 요청은 보내지 않는다. 편집을 끝내려면 목적에 맞게 「Check In」 또는 「Undo」를 사용한다.
§ L426-444 [23] 상세 조회나 저장이 실패했을 때 넘길 정보   parent=426 children=3 sources=10
    L440-440 문단 162  | 상세·Flow 조회 실패는 내용 영역에, Reference 저장·체크아웃 오류는 상세 탭에 표시된다. Flow 생성 요청이나 저장된 Flow 수정 요청의 오류는 카드 머리글에 표시된다. 저장·체크아웃 실패는 서버 문구가 있으면 그 문구를 표시한다. 이 영역에는 별도 「재시도」 단추가 없다.
    L442-442 문단 127  | Flow Filter·Action Data Index 창에 조건 표 대신 JSON 입력 칸과 원문 유지 안내가 나오면 기존 값을 편집기가 해석하지 못한 상태이다. 모델·버전·대상 Flow와 안내 문구를 모델 담당자에게 전달한다.
    L444-444 문단 135  | 입력 오류는 표시된 항목을 고쳐 저장한다. 권한 거부는 운영 관리자에게 요청 권한 확인을 맡긴다. 해결되지 않으면 모델명·버전·Reference 항목 또는 Flow Name과 Message Name·누른 단추·시각·전체 오류 문구를 전달한다.
SUMMARY sections=28 children=105 maxChild=162 maxParent=449
CORPUS docs=8/53 sections=146 part={user=8} type={사용 절차=8}
TOTAL files=8 targets=1 violations=0
```

위반·CROSS VIOLATION·MASK 줄이 없었다. 기계 검사와 별도로 모든 본문을 읽고 제목·같은 사실 중복·빈 틀 문장·뜻풀이·문장·마스킹·user 권한 경계를 확인했다. 생성 도중 섞인 외국어 문장 2개를 한국어로 고쳤고, 후속 저장 실패까지 항상 오류가 보인다는 과도한 문장을 실제 오류 표시 경로로 좁혔다. 최종 미해소 지적은 0건이다.

| 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·앞선 문서와 중복 |
|---|---|---|---|---|
| 모델 버전의 상세 탭 열기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 트리 탐색을 반복하지 않고 표 행의 더블 클릭과 탭 선택부터 답한다. |
| 통신 방식에 따라 다른 Reference 항목 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 공통 항목과 통신 방식별 추가 항목을 실제 Reference 렌더 순서에 맞춰 구분한다. |
| Check Out으로 EDIT 작업본 열기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 편집 가능한 branch·EDIT 복제·기본 변경 요청 권한을 함께 확인했다. 모든 저장 API가 소유자 검사까지 한다고 확대하지 않았다. |
| 다른 편집자가 점유했거나 EDIT를 다시 열었을 때 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 다른 소유자와 본인 EDIT 재진입을 나눴다. 명시적 Check Out 조건을 상태 동기화 코드로 대조했다. |
| Reference 표의 행 추가·수정·삭제와 저장 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 표의 작업 단위와 현재 표만 저장하는 분기를 확인했다. 표 변경을 전체 모델 자동 저장으로 설명하지 않았다. |
| 파라미터와 메시지 표에서 비울 수 없는 칸 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 화면 필수 별표의 부재를 서버 requiredCell 검사와 함께 적었다. 각 표의 표시 열에 대응했다. |
| SECS의 변수·보고·이벤트 표 입력 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 세 SECS 표의 필수 칸, VID Type 열거값, Enabled 변환값을 각각 대조했다. |
| PLC Group을 먼저 저장하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; PLC 그룹 필수값·이름 패턴·중복 거부·그룹 이름 변경 시 자식 삭제를 확인했다. |
| PLC Register의 소속 그룹과 변수 입력 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 상세 저장의 순서 값 정규화·빈 값·기존 행 변경 전 거부를 현재 코드로 보완했다. |
| PLC 표의 선택 목록에서 값 고르기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 기존 목록을 보존하고 Byte Order·Word Order의 허용 목록을 현재 렌더 코드로 보완했다. |
| Dcop Itemes에서 수집 항목 정의하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; DCOP 정의의 저장과 Flow 연결을 구별했다. Collection Rule에 빈 선택이 없음을 코드로 확인했다. |
| MDF XML 올리고 내용 확인하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; Upload가 직접 저장하는 경로와 UTF-8·XML 검사, 읽기 전용 표시를 대조했다. |
| Message Flows에서 기존 Flow 열기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 그룹명 아래 메시지를 고르는 흐름, 카드 순서와 Message Name 후보 공급원을 확인했다. |
| Add Flow로 새 Flow 만들기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; MDF 선택 중 Add Flow 숨김, 신규 이름 필수와 취소, 성공 뒤 선택 전환을 확인했다. 동일 조합의 무조건 거부 주장은 제거했다. |
| Flow 이름과 Action Name 저장 조건 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 저장된 Flow Name의 화면 수정 불가와 기존 Action Name 필수, 신규 빈 액션의 POST-only 분기, 공통 길이 검사를 대조했다. |
| SECS Flow의 Trigger 입력 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 화면 실제 문구와 SECS 분기를 보존했다. 후보 조회는 체크아웃한 SECS 모델에서 Dcop Itemes를 선택한 상태일 때 켜지는 것을 확인했다. Dcop Itemes → Flow → Event Id 선택과 EventIdes 저장·재선택·인계를 단계로 대조했다. |
| Flow Filter에서 조건 묶음 만들기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 조건·그룹 생성과 제거, 창 저장과 상세 Save를 구별했다. |
| Flow Filter의 조건 한 줄 채우기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; from·상대 경로·연산·expected의 모든 입력 조건을 서버 파서와 대조했다. |
| transforms를 차례대로 추가하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 한 줄당 변환 입력과 실제 순차 적용 코드를 연결했다. |
| 공백·대소문자·숫자를 바꾸는 transform | 적합 | 적합 | 적합 | 답 본문 복제 없음; 지원 함수 이름을 실제 switch와 연결했고, 숫자 예시 2의 연산도 해당 구현으로 확인했다. |
| 문자열을 자르거나 덧붙이는 transform | 적합 | 적합 | 적합 | 답 본문 복제 없음; 문자열 함수 인자와 0 기반 위치·끝 위치 제외를 실제 구현으로 확인했다. |
| Action Data Index에서 보고 필드 연결하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; MDF 선택·XML 표시·필드 추가와 정렬·삭제를 확인했다. 빈 필드 생략과 같은 필드 뒤 값 우선은 직렬화 reduce로 대조했다. |
| Collect에 DCOP 항목 연결하고 해제하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 저장된 Flow 선행 조건·후보 선택·화살표·연결 제거·창 저장 후 Save를 확인했다. DCOP 본체 삭제와 혼동하지 않는다. |
| Flow 삭제를 확정하거나 취소하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 메뉴 Delete와 카드 삭제, 확인 창의 삭제·취소, 즉시 서버 반영과 연결만 삭제하는 범위를 확인했다. |
| 저장한 변경을 Check In 전에 비교하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 서버 저장 내용으로 비교하고 MDF를 제외하는 점, 이전 버전 없음·조회 실패 분기를 대조했다. |
| Check In으로 새 버전 확정하기 | 적합 | 적합 | 적합 | 답 본문 복제 없음; Description 선택 입력·서버 버전 생성·EDIT 삭제·새 탭·Cancel과 Undo를 확인했다. |
| Undo와 탭 닫기의 차이 | 적합 | 적합 | 적합 | 답 본문 복제 없음; Undo가 버전 삭제 요청을 보내는 것과 탭 닫기가 서버 요청을 보내지 않는 것을 대조했다. 확인 창이 없는 실제 경로를 명시했다. |
| 상세 조회나 저장이 실패했을 때 넘길 정보 | 적합 | 적합 | 적합 | 답 본문 복제 없음; 조회·저장 오류 영역과 고정 문구/서버 문구를 구별했다. raw JSON 상태와 사용자 역할 범위 안의 인계 정보를 적었다. |

사실 대조 표는 본문 자식 106개를 문장과 표 행 199개로 나누어 대응했다. 표 머리글·메타의 경로·판본·줄 범위도 대조했다. 40절 미만이므로 표본 추출 대신 28절 전부를 검사했다. 체크인 전 저장 원칙과 각 창의 저장 절차는 각각 사용자가 시작할 작업의 최소 조건으로만 적었다.

도구 버전:
- git version 2.52.0.windows.1
- GNU bash, version 5.2.37(1)-release (x86_64-pc-msys)
- openjdk version "21.0.9" 2025-10-21 LTS
- Python 3.12.10
버전 조회 보완: Python subprocess의 bare bash는 C:\Program Files\Git\usr\bin\bash.EXE로 해석되어 exit 1과 UTF-16LE 접근 거부 출력을 반환했다. 이 실패 출력을 Git Bash 버전으로 기록하지 않고, 실제 검사에 사용하는 C:/Program Files/Git/usr/bin/bash.exe --version의 exit 0 출력으로 교체했다. 시스템 설정·권한은 변경하지 않았다.

제품 build·단위시험·통합시험·브라우저 실행은 하지 않았다. 문서 검사 PASS를 제품 실행 검증이나 독립 공식 검증으로 표시하지 않았다. 별도 AI 호출·재위임 0회이며 provider 사용량·비용과 전체 작업 wall time은 별도로 계측하지 않았다. 일회성 파일·브라우저·서비스·컨테이너를 만들지 않아 정리 대상 임시 자원이 없다. 사용자 지정 work 폴더도 생성하지 않았다. 다른 문서는 수정하지 않았다.

완료: 검색 원문과 이 작성 기록 두 파일만 생성했다. 남은 문서 결함 0건, 사용자 결정 0건이다. 메인 세션의 원천 대조 검증은 별도이며 이 세션은 자체검사까지 완료했다.

## 메인 세션 지적 1건 보완 (2026-11-18)

- 원인: Event Id 후보 조회의 실제 조건을 Model Parameter에서 시작하는 한 사례로 좁혀 설명하고, 화면에서 가능한 해결 순서를 조사·기록하지 않았다. 최초 자체검사의 원천 대조가 후보 조회 조건과 Reference·Flow 선택 상태의 결합까지 확인하지 못했다.
- 최소 수정: 검색 원문의 「SECS Flow의 Trigger 입력」 절만 수정했다. 나머지 검색 원문을 원복한 구성의 SHA-256이 최초 본문 b32941ede2e9449bc745a220156bda77b2beda32fe75da26101398452acc1733과 일치함을 확인했다. 최초 본문은 434줄·28절이었다.
- 코드 재대조: nori-tc-ui-front @ 9fe69a75dcd3의 ModelDetailPanel.tsx L218~226·L300~312·L495~498·L585~591·L872~893, model-ui.store.ts L238~243, FlowCard.tsx L319~343을 읽었다. useModelNodeDetail.ts L10~15는 모델 키가 없으면 조회하지 않는 조건이고 ModelPage.tsx L575~602는 선택한 Reference 표를 저장하는 경로임도 확인했다.
- 상태 흐름: 체크아웃한 SECS 모델 → Dcop Itemes 선택으로 eventides 조회 활성화 → Flow 선택은 Reference 노드를 유지 → 조회한 번호를 Event Id 선택 목록으로 전달한다. workflow는 Reference 목록에서 제외되므로 화면 조작으로 선택하는 경로가 없다. 번호가 없으면 EventIdes에 먼저 저장하고 Dcop Itemes·Flow를 다시 선택한다.
- 영향: 해당 절의 원천과 10개 문장 대조 행, 수동 검사 행, 매뉴얼 차이 행 및 이후 원문 줄 위치를 갱신했다. 다른 절의 본문과 소유 범위는 보존했다.
- 이번 첫 검사: exit 1, 해당 부모 454자로 450자 상한을 4자 초과했다. 원문 SHA-256은 0b114b17548352de9e1692f84b5cf81ac75802ad437ccd7b563be9322ae1a962이며 command wall time은 6.4183498초였다. 새 인계 문장의 불필요한 표현 5자를 줄였고, 두 번째 검사에서 해당 부모 449자·전체 위반 0건·exit 0을 확인했다.
- 자체검사: 수정한 모든 문장을 원천에 대조했고 절 제목·독립 답·문장·중복·권한 경계를 확인했다. 최종 검사에 MASK·CROSS VIOLATION은 없었다. 기존 다른 절의 유효한 대조 기록을 재사용했으며 제품 실행 검증을 새로 수행했다고 주장하지 않는다.
- 원천 판본: 이번에 다시 읽은 프런트엔드 5개 파일의 현재 HEAD는 9fe69a75dcd3이다. 대상 경로 status --short의 stdout은 비었고 HEAD diff도 exit 0이었다. safe.directory는 읽기 명령 한 번에만 지정했다.
- 실행·정리: 재위임·제품 코드 변경·git add/commit/push·업로드·외부 접속·프로세스 종료를 하지 않았다. 검사용 임시 파일과 서비스도 만들지 않아 정리할 소유 임시 자원이 없다.
- 보완 완료: 요청 결함과 직접 영향의 미해소 항목 0건, 사용자 결정 0건이다. 메인 세션의 후속 검증 판정을 대신하지 않는다.

## 제품 결함 수정 D 반영 (2026-11-19)

- 허가 범위: 문서 8의 PLC 순서 칸 설명과 직접 기대는 저장 검사·선택 목록·판본 메타·작성 기록만 보완했다. 제품 코드와 다른 문서는 바꾸지 않았다.
- 변경 전: 검색 원문 SHA-256 32dcb7ddbfbc7673815d04ab20b7753319c52d8af8f1a249c107df360745c173, 444줄·28절이다. 위 수정만 역으로 적용한 본문이 이 SHA와 일치하여 다른 본문·표·기존 원천 줄이 보존됐음을 확인했다.
- 전체 영향 확인: 28절 전체를 다시 읽었다. Kafka 보안 설정 전달·Secret 생성 linger·설비별 HSMS/Socket 실행 설정을 단정하는 문장은 이 문서에 없으므로 A~C 때문에 바꿀 본문은 없었다. D의 모델 복사 경로에 저장 검사를 적용한다는 문장도 없어 기존 분기·체크아웃·체크인 설명은 보존했다.
- 원인과 반영: 순서 칸이 일반 편집 항목으로 묶인 문장에서 두 칸을 빼고 선택 목록 표에 추가했다. 상세 저장 시 공백 제거·대문자 변환·빈 값 비움과 허용값 밖이면 기존 행 삭제·저장 전 거부를 썼다. 행·칸을 확인해 수정하는 안내도 연결했다.
- 코드·시험 대조: 프런트의 createPlcColumns와 새 model-plc-order-columns.test.ts L38~82를 직접 읽었다. 선택 목록·빈 선택·기존 목록 밖 값 표시를 기대한다. 백엔드 replacePlcRegisters와 PlcRegisterOrder, JpaModelDetailCommandPortTest.java L544~585를 읽어 정규화·빈 값과 잘못된 값의 요청 전체 거부·삭제/저장 미호출 기대값을 확인했다. 이번 문서 세션에서는 제품 시험을 재실행하지 않았다.
- 시험 원천 판본: nori-tc-ui-front/tests/unit/model-plc-order-columns.test.ts @ 2476db5c5093 (??), nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPortTest.java @ d36c10ff8579 (M)이다.
- 소유 범위: 기존 목록 밖 값의 표시·Gateway의 순서 해석·빈 값의 런타임 기본값·V9 컬럼 길이 상세는 00.rag/user/plc-modbus-register-and-type-reference.md가 소유한다. 해당 문서 L186~205가 이미 D를 반영했음을 읽었고 이 문서에 같은 설명을 복제하지 않았다. 다른 문서에 추가 수정이 필요한 지적은 없었다.
- 자체 확인: 변경 문장·자료 행을 작업 트리 코드와 대조했고 숫자·식별자·사용자 화면 명칭을 확인했다. 수정 절 둘의 독립 답·중복·user 권한 경계를 확인했다. H1 적용은 기준 commit과 세 변경 파일 SHA를 구별했고 확인일을 2026-11-19로 맞췄다.
- 실행·정리: 독립 AI 호출·재위임 0회이다. 전체 작업 wall time·provider 사용량·비용은 별도로 계측하지 않았다. 일회성 검사용 파일·서비스·컨테이너를 만들지 않았으며 다른 세션의 자원을 종료하지 않았다. 검사기 완료 결과는 아래에 추가한다.

### 2026-11-19 최종 검사

검사기는 현재 위치 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`를 사용했다. 이전 Temp 위치는 존재하지 않아 호출하지 않았다. 검사기 소스·스크립트·설정 파일은 변경하지 않았다.

첫 실행은 exit 1, 원천 commit:path 조회 관련 위반 137건이었다. `git -C C:/workspace_nori/nori-tc/nori-tc-ui-front show 9fe69a75dcd3:src/features/model/components/ModelDetailPanel.tsx`를 따로 확인하니 exit 128과 `fatal: detected dubious ownership in repository at 'C:/workspace_nori/nori-tc/nori-tc-ui-front'`를 반환했다. 같은 조회를 `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-front -C ... show ...`로 실행하면 exit 0, stdout 49,999바이트였다. 따라서 commit 소실로 판단하거나 문서의 기존 원천을 바꾸지 않았다.

재실행은 Python subprocess의 자식 환경에만 safe.directory를 추가했다. 기존 GIT_CONFIG_COUNT=2를 보존하고 10개 저장소 범위를 2~11번 항목으로 추가하여 자식 GIT_CONFIG_COUNT=12를 전달했다. 키는 모두 safe.directory이며 값은 `C:/workspace_nori/nori-tc/<저장소>`이다. 저장소 목록: nori-ai-gateway·nori-common·nori-tc-business·nori-tc-common·nori-tc-exec·nori-tc-gateway·nori-tc-gateway-action·nori-tc-simulator·nori-tc-ui-backend·nori-tc-ui-front. 전역 Git 설정이나 부모 환경은 변경하지 않았다.

실제 subprocess 실행 인자 배열:

```text
["C:/Program Files/Git/usr/bin/bash.exe", "C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh", "--dump", "00.rag/user/model-details-and-flow-editing.md"]
```

이번 검사 호출 2회이다. 최종 실행 wall time 38.797309초, exit 0, stderr 없음, stdout SHA-256 5003c41e89c065bf5aedd5ede1d5f8d24745cd09161b37b29d113a11f81206ac이다. 첫 실행의 전체 wall time은 따로 계측하지 않아 미확인으로 남긴다.

최종 출력 끝 10줄:

```text
    L425-425 문단 90  | 편집 전체를 버리려면 상세 탭의 「Undo」를 누른다. 별도 확인 창 없이 EDIT와 그 안의 저장 내용까지 제거하고 같은 branch의 이전 확정 버전을 연다.
    L427-427 문단 60  | 돌아갈 이전 버전이 없으면 Undo를 거부하는 문구가 나온다. 모델·버전과 문구를 운영 담당자에게 전달한다.
    L429-429 문단 97  | 탭의 닫기 단추는 탭만 닫는다. 서버의 EDIT 작업본과 점유를 해제하는 요청은 보내지 않는다. 편집을 끝내려면 목적에 맞게 「Check In」 또는 「Undo」를 사용한다.
§ L431-449 [23] 상세 조회나 저장이 실패했을 때 넘길 정보   parent=426 children=3 sources=10
    L445-445 문단 162  | 상세·Flow 조회 실패는 내용 영역에, Reference 저장·체크아웃 오류는 상세 탭에 표시된다. Flow 생성 요청이나 저장된 Flow 수정 요청의 오류는 카드 머리글에 표시된다. 저장·체크아웃 실패는 서버 문구가 있으면 그 문구를 표시한다. 이 영역에는 별도 「재시도」 단추가 없다.
    L447-447 문단 127  | Flow Filter·Action Data Index 창에 조건 표 대신 JSON 입력 칸과 원문 유지 안내가 나오면 기존 값을 편집기가 해석하지 못한 상태이다. 모델·버전·대상 Flow와 안내 문구를 모델 담당자에게 전달한다.
    L449-449 문단 135  | 입력 오류는 표시된 항목을 고쳐 저장한다. 권한 거부는 운영 관리자에게 요청 권한 확인을 맡긴다. 해결되지 않으면 모델명·버전·Reference 항목 또는 Flow Name과 Message Name·누른 단추·시각·전체 오류 문구를 전달한다.
SUMMARY sections=28 children=106 maxChild=172 maxParent=449
CORPUS docs=51/53 sections=873 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

최종 결과는 28절·106자식·자식 최대 172자·부모 최대 449자이다. 새 내용이 들어간 PLC Register 절은 420자, 선택 목록 절은 443자이며 대상 문서의 위반·MASK·CROSS VIOLATION은 0건이다. 작성 기록은 본문 199개 문장·자료 행에 대응하며 기존 원천 줄과 다른 절 내용은 보존했다.

완료: 제품 결함 수정 D의 문서 반영과 직접 영향 보완을 끝냈다. 미해소 문서 결함 0건, 사용자 결정 0건이다. 제품 시험·현장 배포 상태를 검증했다고 주장하지 않는다. 검사 프로세스 두 건은 각각 exit 1·exit 0으로 종료됐고 일회성 파일·서비스는 생성하지 않았다.


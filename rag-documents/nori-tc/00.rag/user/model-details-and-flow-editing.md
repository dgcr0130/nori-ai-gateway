# 모델 상세와 Flow 편집
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3(PLC 목록 파일 2476db5c5093)·nori-tc-ui-backend 985d17c604dd(PLC 저장 파일 d36c10ff8579·순서 검사 파일 d36c10ff8579)·nori-tc-business 7db24f7466bd·nori-tc-common 6fe71f6b2da6·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/model-registration-and-branching.md
```

## 모델 버전의 상세 탭 열기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleOpenModelDetail L639~665
원천: nori-tc-ui-front/src/features/model/components/ModelInfoTable.tsx @ 9fe69a75dcd3 ; 행 더블 클릭 L174~182
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 탭·Reference 렌더 L711~751
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.1 절차 L1287~1298
```

1. 「Model 정보」 표에서 편집할 버전의 행을 두 번 누른다. 왼쪽 트리의 모델명을 두 번 누르는 조작과 구별한다.
2. 열린 상세 탭을 눌러 작업할 모델·버전을 선택한다. 「Reference」에서 표나 MDF를 고르고, 「Message Flows」에서 Flow를 고른다.

## 통신 방식에 따라 다른 Reference 항목
```rag-meta
원천: nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; 상세 항목·표시 문구 L250~320
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록 L872~893
```

상세 탭의 「Reference」에는 다음 항목이 표시된다. Flow는 별도의 「Message Flows」에서 선택한다.

| 통신 방식 | Reference 항목 |
|---|---|
| 공통 | Model Parameter·MES Message·MDF·Dcop Itemes |
| SECS 추가 항목 | SECS Message·Variableides·ReportIdes·EventIdes |
| Socket 추가 항목 | Socket Message |
| PLC 추가 항목 | PLC Group·PLC Register |

## Check Out으로 EDIT 작업본 열기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleCheckOut L991~1026
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용·단추 조건 L327~349
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkoutBranchVersion L278~319
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 변경 API 권한 L240~256
```

폐기되지 않은 branch의 상세 탭에서 「Check Out」을 누른다. 서버는 선택한 버전의 상세를 복사한 EDIT 작업본을 만들며, 본인 소유의 EDIT가 이미 있으면 그 작업본을 연다.

체크아웃 뒤에는 EDIT 탭이 활성화되고 「Save」·「Undo」·「Check In」이 표시된다. root에는 「Check Out」이 없고 DEPRECATED branch에서는 비활성화된다.

저장소 기본 API 권한 데이터 기준으로 체크아웃·저장·업로드·Flow 삭제·체크인·Undo의 변경 요청에는 MODEL_WRITE가 필요하다.

## 다른 편집자가 점유했거나 EDIT를 다시 열었을 때
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 점유자·편집 상태 L279~307
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 명시적 체크아웃 조건 L383~412
원천: nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 기존 EDIT 확인 L128~168
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 편집 안내·점유 문구 L770~780
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; ensureEditableByCurrentUser L332~337
```

다른 사용자가 EDIT를 점유하면 점유 안내와 함께 「Check Out」이 비활성화된다. 편집자에게 작업 종료를 요청한다. 계속 막히면 모델·버전·발생 시각·표시 문구를 운영 담당자에게 전달한다.

본인 EDIT를 다시 열었어도 「읽기 전용입니다. 수정하려면 Check Out이 필요합니다.」가 나오면 「Check Out」을 눌러 편집을 재개한다. 편집 가능 상태에서는 「편집 중입니다. Save로 저장하고 Check In으로 확정할 수 있습니다.」가 표시된다.

## Reference 표의 행 추가·수정·삭제와 저장
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 표 편집·저장 분기 L667~681
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 행 추가·표 렌더 L983~1116
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 입력·목록 셀 L421~529
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 행 변경·저장 L513~602
```

1. 체크아웃한 상세 탭에서 「Reference」의 편집할 표를 선택한다.
2. 「행 추가」로 행을 만든다. 글자 셀은 두 번 눌러 수정하고, 선택 목록 셀은 한 번 눌러 고른다. 행 끝 휴지통 단추로 행을 제거한다.
3. 상세 탭 오른쪽 위 「Save」를 누른다. 마우스를 올리면 「Reference 저장」이 표시된다. 저장 응답의 행이 표에 다시 표시된다.

「Save」는 현재 선택한 Reference 표를 저장한다. 여러 표를 바꿨으면 각 표를 선택해 저장한다.

## 파라미터와 메시지 표에서 비울 수 없는 칸
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 표 열 L37~53
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 필수 셀 L211~274
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1392~1393
```

Reference 표에는 필수 표시가 없지만 서버는 아래 칸의 빈 값을 거부한다. 파라미터 값은 「Parameter Value」, 설명은 「Description」에서 수정한다.

| Reference 표 | 반드시 입력할 칸 |
|---|---|
| Model Parameter | Parameter Name |
| SECS Message | SECS Message Name |
| Socket Message | Socket Message Name |
| MES Message | MES Message Name |

## SECS의 변수·보고·이벤트 표 입력
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; SECS 표 열 L54~70
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; SECS 표 저장 L280~323
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; Enabled 입력 L627~637
원천: nori-tc-ui-backend/ui/tc-ui-domain/src/main/java/com/nori/tc/ui/domain/common/model/UiVariableIdType.java @ 985d17c604dd ; VID Type L6~11
```

체크아웃한 SECS 모델의 「Reference」에서 표를 고르고 다음 필수 칸을 채운 뒤 「Save」를 누른다.

| 표 | 필수 칸 | 함께 편집하는 칸 |
|---|---|---|
| Variableides | VariableId·VID Type | Description |
| ReportIdes | ReportId | VariableIdes·Enabled·Description |
| EventIdes | EventId | ReportIdes·Enabled·Description |

「VID Type」은 SVID·DVID·ECID·CEID 중에서 입력한다. 「Enabled」에 값을 적으면 true·false 또는 1·0, y·n, yes·no로 입력한다.

## PLC Group을 먼저 저장하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; PLC 입력·경고 L543~637
원천: nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Message Name 패턴 L292~302
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Group 열 L104~112
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC Group 저장 L409~450
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 그룹 삭제 시 레지스터 삭제 L315~362
```

체크아웃한 PLC 모델의 「PLC Group」에서 「Message Name」·「Area」·「Start Address」·「Read Count」·「Poll Period (ms)」를 채운다. 주소·개수·주기는 정수로 입력하고 「Save」를 누른다.

「Message Name」은 대문자로 시작하며 대문자·숫자·밑줄로 구성한다. 밑줄을 맨 끝에 쓰거나 연속해서 쓰지 않는다. 중복도 허용하지 않는다. 형식이 틀리면 셀에 빨간 테두리와 안내가 나오며 서버도 저장을 거부한다.

기존 그룹의 「Message Name」을 바꾸거나 행을 삭제해 저장하면 그 그룹의 레지스터도 삭제된다. 변경한 그룹의 「PLC Register」를 다시 정의한다.

## PLC Register의 소속 그룹과 변수 입력
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Group 목록 조회 L314~325
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC Register 열 L113~127
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 그룹·타입·레이아웃 검사 L467~524
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters 순서 값 검사·저장 L477~528
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L12~20
```

「PLC Group」을 저장한 뒤 「PLC Register」에서 「Group」을 고르고 「Word Offset」·「Variable Name」·「Data Type」을 채워 「Save」를 누른다. 「Word Offset」은 정수이다.

필요한 「Length」·「Bit Index」·「Byte Index」는 정수, 「Scale」은 숫자로 입력한다. 「BCD Signed」·「Char Encoding」·「Description」도 같은 표에서 편집한다.

서버는 존재하는 그룹인지, 영역과 자료형이 맞는지, 읽기 범위를 벗어나거나 겹치는 레지스터가 있는지 검사한다. 이 수정이 들어간 판본에서 「Byte Order」·「Word Order」는 공백 제거·대문자 변환 후 검사하고 빈 값은 비운다. 허용값 밖이면 기존 행 변경 전에 거부하므로 표시된 행·칸을 고친다.

## PLC 표의 선택 목록에서 값 고르기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 값 L50~63
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; 목록 렌더 L614~637
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; PLC 필수 입력 L424~488
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; PLC_ORDER_OPTIONS·순서 칸 판별 L65~81
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; createPlcColumns 목록 선택 L562~645
```

체크아웃한 PLC 모델의 다음 셀은 목록에서 고른다. 목록의 「—」는 값을 비우지만 필수 칸을 비우면 저장이 거부된다.

| 칸 | 고르는 값 |
|---|---|
| PLC Group의 Area | COIL·DISCRETE_INPUT·INPUT_REGISTER·HOLDING_REGISTER |
| PLC Register의 Group | 저장된 PLC Group의 Message Name |
| PLC Register의 Data Type | BOOL·SINT8·USINT8·INT16·UINT16·INT32·UINT32·INT64·UINT64·FLOAT32·FLOAT64·BCD16·BCD32·BCD64·STRING·BYTES |
| PLC Register의 BCD Signed | true·false |
| Byte Order·Word Order | BIG_ENDIAN·LITTLE_ENDIAN |

## Dcop Itemes에서 수집 항목 정의하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 ; EventId·수집 규칙 L421~498
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; LAST 초기값 L541~554
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; EventId 목록 L300~312
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 열 L89~103
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; replaceDcopItems L378~406
```

체크아웃한 모델의 「Dcop Itemes」에서 「행 추가」를 누른다.

「Dcop Item Name」은 필수이다. 「VariableId」·「Calculation Rule」·「Order Rule」을 편집하고 「Save」로 정의를 저장한다. 「Order Rule」에 값을 넣으면 정수여야 한다.

「Collection Rule」은 LAST·FIRST·AVERAGE·MIN·MAX 중에서 고른다. 새 행의 초기값은 LAST이며 이 목록에는 빈 값 선택이 없다.

Socket 표에는 「EventId」 열이 없다. SECS 표에서는 같은 모델의 「EventIdes」에 저장된 번호를 목록에서 고른다. 목록의 「—」로 비울 수 있다.

## MDF XML 올리고 내용 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Upload·XML 표시 L753~783
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; XML 파일·내용 L942~978
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 업로드 결과 L605~623
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java @ 985d17c604dd ; uploadMdf L130~160
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; saveMdf L186~205
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; UTF-8·XML 검사 L763~796
```

1. 체크아웃한 상세 탭에서 「MDF」를 선택한다. 이 화면에서는 「Save」 대신 「Upload」가 표시된다.
2. 「Upload」로 비어 있지 않은 UTF-8 XML 파일을 고른다. 업로드 요청 자체가 MDF를 저장한다.
3. 화면의 XML 내용을 확인한다. XML 표시 칸은 직접 수정할 수 없다.

인코딩이나 XML 형식 오류가 표시되면 파일을 수정해 다시 올린다.

## Message Flows에서 기존 Flow 열기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; 그룹 펼침·메시지 선택 L50~109
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Identity·Trigger·Filter L274~391
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action·Collect L393~464
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 메시지 선택 후보 L246~270
```

「Message Flows」의 Flow 이름을 눌러 펼치고 그 아래 메시지 이름을 누른다.

오른쪽 카드에서 「Identity」·「Filter」·「Action」·「Collect」를 확인한다. SECS에는 「Trigger」도 표시된다. 아래 구역은 카드를 내려서 본다. 편집하려면 상세 탭을 체크아웃한다.

「Message Name」의 후보는 같은 모델의 통신 방식별 「SECS Message」·「Socket Message」·「PLC Group」 중 해당 항목과 「MES Message」에서 가져온다. 필요한 메시지가 없으면 Reference에서 정의를 저장한다.

## Add Flow로 새 Flow 만들기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 추가 가능 조건 L223~235
원천: nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; Add Flow L136~152
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 저장 L611~649
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; 신규 취소 L466~477
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/ModelFlowCommandService.java @ 985d17c604dd ; 필수 이름 L62~66
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 중복 검사 L65~96
```

1. 체크아웃한 상세 탭에서 MDF 이외의 Reference 항목을 고른 뒤 「Message Flows」 옆 「Add Flow」를 누른다.
2. 새 카드의 「Identity」에서 「Flow Name」을 적고 「Message Name」을 고른다. 두 칸 모두 비울 수 없다.
3. 필요한 내용을 채우고 상세 탭 오른쪽 위 「Save」를 누른다. 저장이 성공하면 저장된 Flow가 선택된다. 저장 전 카드 아래 「취소」는 새 카드 입력을 버린다.

새 Flow를 만들 때는 기존 목록의 「Flow Name」과 「Message Name」을 확인해 다른 조합을 사용한다.

## Flow 이름과 Action Name 저장 조건
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Flow Name 읽기 전용 L274~315
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Action Name 입력 L393~401
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 신규 Flow의 후속 저장 조건 L611~649
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/ModelFlowCommandService.java @ 985d17c604dd ; 기존 Flow 필수값 L81~95
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 신규 액션 초기값 L47~79
원천: nori-tc-common/db/tc-db-core/src/main/java/com/nori/tc/db/core/model/upsert/UpsertTcModelMessageFlow.java @ 6fe71f6b2da6 ; 길이 검사 L29~51
원천: nori-tc-common/db/tc-db-core/src/main/java/com/nori/tc/db/core/model/upsert/ModelFieldLengthValidator.java @ 6fe71f6b2da6 ; 길이 상한 L10~13
```

저장한 Flow의 「Flow Name」은 수정할 수 없다. 이름을 바꾸려면 새 Flow를 만들고 기존 Flow를 삭제한다.

기존 Flow를 저장할 때 「Action Name」은 필수이다. 카드의 자유 입력 칸에 실행할 액션을 적는다.

새 Flow에서 「Action Name」을 비우고 「Save」를 누르면 이름과 메시지만 저장되고 액션은 TODO로 남는다. 함께 입력했던 Trigger·Filter·Action Data Index는 저장되지 않는다. 이 구역까지 저장하려면 「Action Name」도 채운다.

서버 길이 제한은 Flow Name·Message Name 각각 1000자, Transaction Id 2000자, Flow Filter의 저장 JSON 4000자이다.

## SECS Flow의 Trigger 입력
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Trigger 입력 L319~359
원천: nori-tc-ui-front/src/features/model/lib/model-flow-editor.ts @ 9fe69a75dcd3 ; SECS만 Trigger 표시 L168~170
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 현재 Reference·읽기 전용 판정 L218~226
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Event Id 후보 조건 L300~312
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 선택 L495~498
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 카드 후보 전달 L585~591
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 목록·선택 L872~893
원천: nori-tc-ui-front/src/features/model/stores/model-ui.store.ts @ 9fe69a75dcd3 ; Flow 선택은 Reference 노드를 바꾸지 않음 L238~243
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; Reference 저장 L575~602
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowMatcherImpl.java @ 7db24f7466bd ; matchesSecsKey L193~205
```

SECS 카드의 「Trigger」에는 선택 항목 「Event Id」와 입력 칸 「Transaction Id」가 있다. 「Transaction Id」를 비우면 모든 transactionId에 맞는다. Socket·PLC 카드에는 「Trigger」가 없다.

체크아웃한 SECS 모델에서 「Reference」가 「Dcop Itemes」일 때만 「Event Id」 후보를 불러온다.

1. 「Reference」에서 「Dcop Itemes」를 누른다.
2. 「Message Flows」의 Flow 이름을 펼쳐 메시지 이름을 누른다.
3. 「Trigger」의 「Event Id」 목록에서 번호를 고른다.

번호가 없으면 「EventIdes」에 저장된 번호를 확인한다. 없으면 표에 저장한 뒤 Dcop Itemes와 Flow를 다시 선택한다. 그래도 없으면 모델·버전·저장값·화면 결과·시각을 운영 담당자에게 전달한다.

## Flow Filter에서 조건 묶음 만들기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건·그룹 추가와 저장 L113~156
원천: nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L219~228
원천: nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 그룹 조작 L568~634
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 카드 편집 반영 L514~529
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 필터 문구 L738~751
```

1. 체크아웃한 Flow 카드의 「Filter」에서 「Flow Filter」 옆 「편집...」을 누른다.
2. 「조건 추가」로 조건 행을 만들고 「그룹 추가」로 하위 묶음을 만든다. 묶음마다 「and」 또는 「or」를 고른다. 휴지통 단추로 조건이나 하위 그룹을 제거한다.
3. 창의 「저장」으로 카드에 반영한다. 「취소」는 창의 변경을 버린다. 서버에 반영하려면 상세 탭의 「Save」도 누른다.

## Flow Filter의 조건 한 줄 채우기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; 조건 칸 L377~468
원천: nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 선택값 L3~13
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 조건 필수값 L138~168
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; from·상대 경로 L275~295
```

「from」은 data·metadata·param 중 고른다. 「path」에는 선택한 from 아래의 상대 경로를 적는다. data.·metadata.·param.을 경로 앞에 다시 붙이지 않는다.

「comparison」은 equals·not_equals·greater_than·greater_than_or_equal·less_than·less_than_or_equal·contains·in 중 고른다.

「path」와 「expected」는 비우지 않는다. 「expected」에는 JSON 값으로 숫자·true·false·배열 등을 적고 문자열은 큰따옴표로 감싼다. JSON 객체는 허용하지 않는다.

## transforms를 차례대로 추가하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; transforms 입력 L474~548
원천: nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; transform 입력 표현 L184~224
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/matching/BusinessWorkflowFilterEvaluator.java @ 7db24f7466bd ; 순차 적용·비교 L310~315
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.2 입력 항목 L1405~1409
```

Flow Filter 조건의 「transforms」에서 「transform 추가」로 한 줄씩 늘린다. 비교할 값을 위에서 아래 순서로 변환한다. 변환이 없으면 원래 값을 비교한다.

인자가 없으면 trim처럼 함수명만 적는다. 인자가 있으면 add(2) 또는 split(",", 0)처럼 괄호 안에 적고 여러 인자는 쉼표로 나눈다. 문자열 인자는 큰따옴표로 감싼다. 각 줄의 휴지통 단추로 변환을 제거한다.

## 공백·대소문자·숫자를 바꾸는 transform
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 지원 변환 L66~87
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; add·sub L264~274
```

Flow Filter의 「transforms」에는 다음 함수명과 인자를 입력할 수 있다.

| 입력 | 값에 적용하는 변환 |
|---|---|
| trim | 문자열 앞뒤 공백을 제거한다. |
| lower·upper | 문자열을 소문자·대문자로 바꾼다. |
| length | 문자열 길이로 바꾼다. |
| toint·tolong | 숫자로 해석할 수 있는 값을 정수로 바꾼다. |
| add(2)·sub(2) | 숫자에 2를 더하거나 뺀다. |

## 문자열을 자르거나 덧붙이는 transform
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; 문자열 변환 L96~171
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; extractportinfo L177~210
```

Flow Filter의 「transforms」에 다음과 같이 적는다. split의 조각 번호와 substring의 시작 위치는 0부터 센다.

| 입력 | 값에 적용하는 변환 |
|---|---|
| split(",", 0) | 쉼표로 나눈 첫 조각을 쓴다. 인자를 모두 비워도 같은 기준이다. |
| substring(0, 3) | 시작 위치부터 끝 위치 직전까지 자른다. |
| substringleft(3)·substringright(3) | 문자열 앞·뒤에서 지정 길이만큼 자른다. |
| addstring("X") | 문자열 뒤에 인자를 붙인다. |
| extractportinfo("PORTID") | Port 정보에서 지정 항목을 추출한다. |

## Action Data Index에서 보고 필드 연결하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; MDF 선택·미리보기 L247~300
원천: nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 필드·순서·제거 L309~555
원천: nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L562~570
원천: nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 필드 직렬화 L606~644
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailWorkflowJsonParser.java @ 985d17c604dd ; 필드 필수값 L171~205
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; MDF·필드 문구 L815~832
```

1. 체크아웃한 Flow 카드의 「Action Data Index」 옆 「편집...」을 누른다. 「MDF Message」에서 서식을 고르고 「선택된 XML Preview」를 확인한다.
2. 「Fields」의 「field 추가」로 행을 만든다. 「Field」에 보고 필드명을 적고 「from」은 data·metadata·param에서 고른다. 「path」는 상대 경로로 적고 변환이 필요하면 「transforms」를 추가한다.
3. 「순서」 손잡이를 끌거나 위·아래 단추로 순서를 바꾸고 「작업」의 휴지통으로 행을 제거한다.
4. 창의 「저장」 뒤 상세 탭의 「Save」를 누른다. 빈 「Field」 행은 저장 내용에서 빠지며 같은 필드명을 반복하면 뒤 행의 값이 남는다. 필드명을 구별하고 path를 채운다.

## Collect에 DCOP 항목 연결하고 해제하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Collect·저장 전 제한 L432~463
원천: nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 검색·추가 L113~170
원천: nori-tc-ui-front/src/features/model/components/DcopItemPickerModal.tsx @ 9fe69a75dcd3 ; 선택·제거·저장 L212~360
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 선택 결과는 카드에 반영 L534~563
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; 연결 갱신 L137~139
```

1. 체크아웃한 저장된 Flow의 「Collect」에서 「DCOP 추가」를 누른다. 새 Flow는 먼저 「Save」해야 이 단추가 활성화된다.
2. 「DCOP Item 선택」 창 왼쪽에서 항목을 검색·체크하고 가운데 화살표로 오른쪽 「현재 Flow 의 DCOP」에 추가한다. 이미 연결된 항목은 「매핑됨」으로 표시된다.
3. 오른쪽 휴지통으로 한 항목을 해제하거나 「모두 제거」로 비운다. 창의 「저장」 뒤 상세 탭의 「Save」를 눌러 연결 변경을 저장한다.

후보가 없으면 「Reference」의 「Dcop Itemes」에서 항목을 먼저 저장한다. 연결을 해제해도 Reference의 수집 항목 정의는 남는다.

## Flow 삭제를 확정하거나 취소하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelFlowSidebar.tsx @ 9fe69a75dcd3 ; Delete 메뉴 L90~105
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; 삭제 단추 L259~269
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 삭제 요청·결과 L652~664
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 확인 창 L1122~1138
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelFlowCommandPort.java @ 985d17c604dd ; Flow·연결 삭제 L144~160
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 확인 문구 L791~792
```

체크아웃한 상세 탭의 「Message Flows」에서 메시지 행을 오른쪽 클릭해 「Delete」를 고른다. 저장된 Flow 카드 머리글의 「삭제」도 같은 확인 창을 연다.

「Flow 를 삭제하시겠습니까?」에서 「삭제」를 누르면 EDIT의 해당 Flow와 Collect 연결이 즉시 삭제된다. 별도 「Save」는 필요하지 않다. 「취소」는 삭제 요청을 보내지 않는다.

삭제 성공 뒤 목록에서 해당 메시지 행이 사라진다. Reference의 DCOP 항목 정의는 삭제하지 않는다.

## 저장한 변경을 Check In 전에 비교하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 서버 조회·비교 분기 L1036~1095
원천: nori-tc-ui-front/src/features/model/lib/model-page-helpers.ts @ 9fe69a75dcd3 ; MDF 제외 L34~37
원천: nori-tc-ui-front/src/features/model/components/ModelCheckInDiffModal.tsx @ 9fe69a75dcd3 ; 비교 표시·단추 L223~308
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 안내 L690~699
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 조회 실패 L728~728
```

바꾼 Reference 표와 Flow를 각각 「Save」한 뒤 「Check In」을 누른다. 비교와 체크인은 서버에 저장된 내용을 사용하므로 입력만 하고 저장하지 않은 값은 먼저 저장한다.

이전 확정 버전이 있으면 「변경사항 미리보기」에서 추가·변경·삭제 건수와 값을 확인한다. 「OK」는 버전 저장 창으로 넘어가고 「Cancel」은 비교 창을 닫는다. MDF는 이 비교에서 제외된다.

이전 버전이 없으면 비교 없이 저장 창을 연다. 비교 조회가 실패해도 오류 문구와 함께 저장 창으로 넘어가므로, 「Cancel」로 닫고 저장 내용과 조회 오류를 확인한다.

## Check In으로 새 버전 확정하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelCheckInModal.tsx @ 9fe69a75dcd3 ; 설명·단추 L83~149
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 체크인 결과 L1148~1176
원천: nori-tc-ui-front/src/features/model/hooks/useModelMutations.ts @ 9fe69a75dcd3 ; 저장 요청 L171~188
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; checkinBranchEditVersion L328~368
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelCheckinRequest.java @ 985d17c604dd ; 선택 설명 L8~11
```

「Save your changes?」 창의 「Description」에 변경 설명을 적고 「Save」를 누른다. 설명은 생략할 수 있다. 버전은 서버가 새로 생성하며 사용자가 버전명을 입력하는 칸은 없다.

성공하면 저장된 EDIT 내용을 복사한 새 branch 버전의 탭이 열리고 EDIT는 제거된다. 새 탭은 읽기 전용이다.

「Cancel」은 확정을 보류하고 EDIT 작업본을 남긴다. 창의 「Undo」는 EDIT 작업본을 버리는 조작이다.

## Undo와 탭 닫기의 차이
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 탭 닫기 L685~691
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleUndoCheckIn L1098~1145
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Undo 단추 L753~767
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Undo 거부 L732~734
원천: nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; 버전 삭제 요청 L587~603
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; 버전 삭제 L101~116
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelCrudPort.java @ 985d17c604dd ; 버전 삭제 L181~194
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 버전별 상세 연쇄 삭제 L121~362
```

편집 전체를 버리려면 상세 탭의 「Undo」를 누른다. 별도 확인 창 없이 EDIT와 그 안의 저장 내용까지 제거하고 같은 branch의 이전 확정 버전을 연다.

돌아갈 이전 버전이 없으면 Undo를 거부하는 문구가 나온다. 모델·버전과 문구를 운영 담당자에게 전달한다.

탭의 닫기 단추는 탭만 닫는다. 서버의 EDIT 작업본과 점유를 해제하는 요청은 보내지 않는다. 편집을 끝내려면 목적에 맞게 「Check In」 또는 「Undo」를 사용한다.

## 상세 조회나 저장이 실패했을 때 넘길 정보
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 작업 오류 L849~851
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 조회 오류 L916~940
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Reference 조회 오류 L1026~1029
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 저장 오류 L594~609
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 문구 우선 L11~12
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; API 인가 L160~174
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.4.4 오류와 대처 L1447~1459
원천: nori-tc-ui-front/src/features/model/components/FlowFilterEditorModal.tsx @ 9fe69a75dcd3 ; JSON 입력 L177~197
원천: nori-tc-ui-front/src/features/model/components/ActionDataIndexEditorModal.tsx @ 9fe69a75dcd3 ; JSON 입력 L223~242
원천: nori-tc-ui-front/src/features/model/lib/model-detail-editor.ts @ 9fe69a75dcd3 ; 해석 실패 원문 보존 L495~604
```

상세·Flow 조회 실패는 내용 영역에, Reference 저장·체크아웃 오류는 상세 탭에 표시된다. Flow 생성 요청이나 저장된 Flow 수정 요청의 오류는 카드 머리글에 표시된다. 저장·체크아웃 실패는 서버 문구가 있으면 그 문구를 표시한다. 이 영역에는 별도 「재시도」 단추가 없다.

Flow Filter·Action Data Index 창에 조건 표 대신 JSON 입력 칸과 원문 유지 안내가 나오면 기존 값을 편집기가 해석하지 못한 상태이다. 모델·버전·대상 Flow와 안내 문구를 모델 담당자에게 전달한다.

입력 오류는 표시된 항목을 고쳐 저장한다. 권한 거부는 운영 관리자에게 요청 권한 확인을 맡긴다. 해결되지 않으면 모델명·버전·Reference 항목 또는 Flow Name과 Message Name·누른 단추·시각·전체 오류 문구를 전달한다.

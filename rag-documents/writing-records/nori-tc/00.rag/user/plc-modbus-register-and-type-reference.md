# PLC·Modbus 레지스터·데이터 타입 작성 기록

## 대상

- objectKey: `00.rag/user/plc-modbus-register-and-type-reference.md`
- 구획: user, 필요한 문서 권한 `[AI_DOC_READ]`
- 유형: 참조
- 검색 원문: `rag-documents/nori-tc/00.rag/user/plc-modbus-register-and-type-reference.md`
- 검색 원문 SHA-256: `1d1fdcdd22bf9d398fd24f20a9780d89ebfd1be6c7649dfda48993ea605429e0`
- 최종 검색 원문: 243줄, H1 1개, H2 18개, 직렬화 자식 53개
- 확인일: 2026-11-19, 한국 시간
- 작성 범위: 영역·주소·타입·길이·비트/바이트 선택·바이트/워드 순서·배율의 해석 의미. 제품 수정·SDD·업로드·서버 접속·commit은 수행하지 않았다.

## 원천 판본

읽기 전에 작성 규칙과 TC 목록 전체를 확인했다. `git status --short --branch` 결과는 main, origin/main보다 8개 앞섬, `?? rag-documents/`였고 worktree는 현재 제품 저장소 하나였다. 기존 RAG 파일은 읽기만 했다.

초회와 메인 검증 결함 2건 보완 당시에는 각 형제 저장소에서 `git -c safe.directory=<정확한 저장소 절대경로> -C <저장소> rev-parse --short=12 HEAD`와 `status --short -- <확인 대상 경로들>`을 실행했다. safe.directory는 해당 읽기 명령에만 지정했고 전역 설정을 변경하지 않았다. 아래 표는 당시의 기준선 이력이다. 당시 대상들의 상태 출력은 모두 비어 있었고 종료코드는 모두 0이었다. Git은 사용자 전역 ignore 접근 불가 경고를 stderr에 출력했다. 제품 수정 D 반영 때의 작업 트리 원천은 이어지는 별도 표로 구분한다.

| 저장소 | HEAD | 확인한 경로 묶음 | 작업 트리 확인 |
|---|---|---|---|
| nori-tc-gateway | d3636ab86763 | plc-core의 main 코드·디코더 시험, plc-modbus, PlcModelSpecMapper와 시험, PlcContextSpecMapper | 지정 경로 변경 출력 없음 |
| nori-tc-common | 6fe71f6b2da6 | PlcDataType, plc 검증기와 시험, UpsertTcModelPlcRegister, TcModelPlcRegisterEntity | 지정 경로 변경 출력 없음 |
| nori-tc-exec | f1d5e0a8e727 | 인터페이스 명세, PLC 구조 문서, docs/db_table | 지정 경로 변경 출력 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | modelDetailColumnDefs, ModelDetailPanel, ModelPage, model.types, model.api, ko.json | 지정 경로 변경 출력 없음 |
| nori-tc-ui-backend | 985d17c604dd | ModelDetailController, ModelDetailResponseSupport, ModelDetailSaveRequest, JpaModelDetailCommandPort와 해당 시험 | 지정 경로 변경 출력 없음 |

2026-11-19 제품 수정 D 반영에서 직접 읽은 변경 원천은 다음과 같다. 상태와 SHA-256은 각 파일에 대해 `git status --short -- <경로>`와 파일 바이트 해시로 확인했으며 각 상태 조회 종료코드는 0이었다. HEAD는 front 9fe69a75dcd3, backend 985d17c604dd, exec f1d5e0a8e727, gateway d3636ab86763으로 유지됐다.

| 저장소/경로 | 상태 | SHA-256 |
|---|---|---|
| nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx | M | 14e703baba62a7ee57c83b9d09981b48827689e9fa2c84403a01d789f704e026 |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java | M | aa567bd80d4277dc0839572ed14fb63a0337e2a50ea415074448045488c41d74 |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java | ?? | 241e8e34c68b25713a47362cabe3e7574400445f71e33dc4ad1aa7c47d716d6a |
| nori-tc-exec/db/migration/V9__widen_plc_register_order_columns.sql | ?? | 830cbdffb3c97789cb32f8f3942d5321be1f9196ce7c10e2db75fa5ea301f7d1 |

Gateway의 PlcByteOrder·PlcWordOrder·PlcModelSpecMapper·PlcContextSpecMapper, UI Backend의 UiApiExceptionHandler·ModelDetailController, UI Front의 model.api.ts는 지정 경로의 변경 출력이 없어 기존 commit 판본을 유지했다.

확인한 주요 경로는 다음과 같다. 아래 절별 표에는 실제 근거의 전체 경로·판본·줄을 기재했다.

- `nori-tc-exec/docs/completion-report/04-interface-spec.md` §4.10 L363~371: 읽기 함수 4개와 예외 응답을 확인했다. 이번 문서에는 읽기 영역·함수 코드의 의미만 반영했다.
- `nori-tc-exec/docs/Architecture/20-gateway/07-plc-protocol.md`: 코덱·영역·타입·길이·순서·디코드·그룹·운영 흐름을 읽고 실제 코드와 대조했다. 런타임 내부 스케줄러·상관기·실패 추적·빈 조립 설명은 검색 원문에 옮기지 않았다.
- `nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx` 현재 파일 L65~66·L80~81·L562~570·L622~644: 제품 수정 D 이후 Byte Order·Word Order가 목록 선택이며 빈 값과 기존 목록 밖 값도 표시함을 확인했다. 해당 절에는 파일 SHA-256을 사용하고 다른 절의 변경되지 않은 기준 commit 원천은 보존했다.
- `nori-tc-ui-front/src/features/model/types/model.types.ts` L277~286, L315~316: PLC Group·PLC Register 표시값을 확인했다. `src/i18n/locales/ko.json`의 PLC 문구도 검색했고 이 표의 영어 열·옵션에 별도 한국어 번역이 적용된다고 쓰지 않았다.
- `nori-tc-ui-front/src/features/model/api/model.api.ts` L297~319 → `nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelDetailController.java` L55~115 → `JpaModelDetailCommandPort.java` L424~524: 상세 표 값이 저장 요청으로 전달되는 경로를 확인했다. 저장 절차·권한 표는 기존 문서의 소유이므로 복제하지 않았다.
- `nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelDetailSaveRequest.java`: rows·values의 필수 검사를 읽었다. 이번 검색 원문에는 저장 요청의 필수값 전체를 설명하는 절을 만들지 않았다.
- `nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/PlcModelSpecMapper.java` L35~60와 `gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java` L39~65: 그룹 주소·개수, 타입, 길이, 순서, 배율 등이 런타임 정의로 전달되는 것을 확인했다.

## 절별 사실 대조

표의 검색 원문 줄은 위 SHA의 최종 원문 기준이다. 같은 줄에 여러 문장이 있으면 문장별로 행을 나눴다. UI 열 표기는 공통으로 `nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd L104~127`과 프런트 렌더 `nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 9fe69a75dcd3 L565~568 L614~633`에 대조했다.

### PLC Group과 PLC Register가 나타내는 범위

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 17 | Group은 영역·주소·개수·주기를 묶는다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 L10~25 | readRequest와 periodMs를 한 그룹이 보유한다. |
| 17 | Register는 응답 변수 해석 규칙이다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcRegisterSpec.java @ d3636ab86763 L6~24 | 응답 블록 위치와 해석 옵션을 한 변수에 결속한다. |
| 17 | Variable Name은 값의 키, Message Name은 메시지 구분이다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 L51~64 | body.put(variableName, decode)와 MessageName(groupMessageName)을 확인했다. |
| 19 | 편집·폴링·진단은 기존 user 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md, 확정 문서의 모델 상세·설비 시작/종료/폴링·PLC 행; 기존 user 원문 세 파일 | 세 objectKey가 존재하며 각 문서의 소유 주제와 안내가 일치한다. 제품 동작의 근거로 링크만 사용하지 않았다. |

### Area별 읽기 대상·함수 코드·개수 단위

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 29 | Area가 영역과 함수를 정한다. | nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L35~36 L145~151 | encodeReadRequest가 영역으로 함수를 고른다. |
| 29 | 같은 주소라도 영역이 다르면 다른 칸이다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 L6~7 | 영역의 물리적 구별을 코드 주석에서 확인했다. |
| 29 | 표의 개수는 요청 허용 범위이며 워드는 16비트다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/codec/PlcReadRequest.java @ d3636ab86763 L24~33; nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcRegisterLayoutValidator.java @ 6fe71f6b2da6 L25~26 | 양수·영역 상한 검사와 16비트 워드 단위다. |
| 33 | COIL, 코일 비트, 0x01, 1~2000비트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 L11 L24~39; nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L147 | 비트 영역·상한 2000·함수 01이다. |
| 34 | DISCRETE_INPUT, 입력 비트, 0x02, 1~2000비트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 L12 L24~39; nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L148 | 비트 영역·상한 2000·함수 02이다. |
| 35 | HOLDING_REGISTER, 홀딩, 0x03, 1~125워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 L14 L34~39; nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L149 | 워드 영역·상한 125·함수 03이다. |
| 36 | INPUT_REGISTER, 입력 레지스터, 0x04, 1~125워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 L13 L34~39; nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L150 | 워드 영역·상한 125·함수 04이다. |

### Start Address와 Word Offset은 어디서 세는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 50 | Start Address는 0-based, 0~65535다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/codec/PlcReadRequest.java @ d3636ab86763 L14 L26~27 | 주소 정의와 0xFFFF 경계를 대조했다. |
| 50 | 주소 정수를 그대로 송신한다. | nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L49~53 | 주소 상·하위 바이트로 직접 기록하며 기준 보정이 없다. |
| 52 | Word Offset은 응답 첫 요소 0부터 세며 0 이상이다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcRegisterSpec.java @ d3636ab86763 L8~19 L39~42 | 응답 상대 위치 정의와 음수 거부를 확인했다. |
| 52 | 레지스터 영역은 워드, 비트 영역은 비트 위치다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 L24~29; nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcRegisterLayoutValidator.java @ 6fe71f6b2da6 L25~26 | 영역별 위치 단위가 다르다. |
| 54 | 확인한 Gateway는 Address Qualifier를 읽기 요청에 쓰지 않는다. | nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/PlcModelSpecMapper.java @ d3636ab86763 L39~43; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 L39~44 | DB·컨텍스트의 두 변환 경로가 qualifier를 전달하지 않는다. 제품 main 코드의 qualifier 검색도 관련 사용처가 없었다. |
| 54 | 주소는 Area와 Start Address로 정한다. | nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L35~53 L145~151; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd L104~112; nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcGroupEntity.java @ 6fe71f6b2da6 L65~66 | 화면 열·저장 필드는 존재하지만 Modbus는 영역 함수와 시작 주소로 요청 주소를 구성한다. |

### 레지스터가 그룹의 읽기 범위 안에 들어가는 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 63 | offset+점유 수는 Read Count 이하여야 한다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 L38~46 | end > window면 생성 예외다. |
| 63 | Gateway가 범위 초과 정의를 거부한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 L39~65; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 L38~46 | 이벤트 정의 변환도 같은 생성자 검사를 통과한다. |
| 65 | 저장 시 같은 비트·점유 워드 겹침 거부 | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcRegisterLayoutValidator.java @ 6fe71f6b2da6 L69~118; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd L514~524 | 저장 전 검증기가 비트/워드 중복을 검사한다. |
| 65 | 다른 Bit Index는 공존하며 전체 워드와 충돌한다. | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcRegisterLayoutValidator.java @ 6fe71f6b2da6 L83~118 | 비트 집합과 FULL_WORD 점유 분기를 확인했다. |

### BOOL과 Bit Index의 참·거짓 판정

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 74 | 비트 1/0은 true/false다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L107~117 | 선택 비트와 1을 비교한다. |
| 74 | COIL·DISCRETE_INPUT은 BOOL, Bit Index 빈칸 | nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcAreaDataTypeValidator.java @ 6fe71f6b2da6 L35~43 | 비-BOOL과 지정 bitIndex를 거부한다. |
| 74 | 응답 비트는 낮은 자리부터 푼다. | nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L133~138 | i%8만큼 우측 이동해 비트를 얻는다. |
| 76 | 워드 BOOL은 Bit Index 0~15를 읽는다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L113~117 | 범위 밖 거부와 선택 식을 확인했다. |
| 76 | 바이트 보정 후 선택, 빈칸은 비트 0 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L107~117 | normWord 후 bitIndex 또는 비트 0을 읽는다. |

### SINT8·USINT8과 Byte Index의 바이트 선택

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 84 | 값 하나가 1워드 중 한 바이트 사용 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L16~17; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L67~68 | 요소 폭은 1이고 selectByte를 호출한다. |
| 88 | SINT8은 부호 있는 8비트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L67 | Java byte로 부호를 해석한다. |
| 89 | USINT8은 부호 없는 8비트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L68 | 0xFF 마스크로 양수 값을 얻는다. |
| 91 | Byte Index 0/1/빈칸 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L98~104 | null→0, 0은 low·1은 high다. |
| 91 | Byte Order 보정이 먼저다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L98~104 | normWord 결과에서 바이트를 선택한다. |

### INT16·UINT16·INT32·UINT32의 부호와 워드 수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 99 | Data Type별 부호·크기와 값 하나의 폭 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L10~11 L18~21; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L69~72 | 선언과 실제 해석을 함께 대조했다. |
| 103 | INT16: signed 16, 1워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L18; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L69 | short 변환이다. |
| 104 | UINT16: unsigned 16, 1워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L19; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L70 | 0xFFFF 마스크다. |
| 105 | INT32: signed 32, 2워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L20; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L71 | 2워드 조립 후 int 변환이다. |
| 106 | UINT32: unsigned 32, 2워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L21; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L72 | 2워드 조립 후 32비트 마스크다. |

### INT64·UINT64의 해석과 큰 양수 보존

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 116 | INT64: signed 64, 4워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L22; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L73 | 4워드 long 조립이다. |
| 117 | UINT64: unsigned 64, 4워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L23; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L135~146 | unsigned 문자열로 BigInteger를 만든다. |
| 119 | 배율 없으면 2^63 이상 큰 정수 보존 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L137~146 | 음수 long 비트열을 부호 없는 큰 정수로 복원한다. |
| 119 | 배율 있으면 부동소수점 곱셈 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L143~144 | doubleValue와 scale을 곱한다. |

### FLOAT32·FLOAT64는 워드를 실수로 해석한다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 129 | FLOAT32: 32비트 실수, 2워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L24; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L75 | intBitsToFloat다. |
| 130 | FLOAT64: 64비트 실수, 4워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L25; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L76 | longBitsToDouble이다. |

### BCD16·BCD32·BCD64의 십진 숫자 해석

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 138 | 4비트마다 십진 한 자리 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L154~164 | 16비트 워드에서 니블 4개를 추출한다. |
| 138 | 자리를 이어 정수로 구성 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L179~187 | magnitude*10+d다. |
| 138 | 숫자 자리 9 초과 거부 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L180~184 | 숫자 루프의 무효 니블 예외다. |
| 142 | BCD16: 1워드·16비트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L26; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L77 | 1워드로 디코드한다. |
| 143 | BCD32: 2워드·32비트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L27; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L78 | 2워드로 디코드한다. |
| 144 | BCD64: 4워드·64비트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 L28; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L79 | 4워드로 디코드한다. |

### BCD Signed가 정하는 부호 자리

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 151 | BCD의 맨 앞 4비트 부호 여부 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L168~177 | 첫 니블 검사와 start=1이다. |
| 155 | true면 0x0 양수·0xF 음수, 나머지 거부 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L169~187 | 부호 검사 후 숫자 자리만 읽고 최종 부호를 적용한다. |
| 156 | false·null이면 전부 숫자 자리 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L166~185 | Boolean.TRUE.equals만 부호 모드를 켠다. |

### STRING의 길이·문자 인코딩·끝 채움 제거

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 163 | Length는 문자 수가 아닌 워드 수 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L190~198 | length를 wordCount로 쓰고 바이트 배열 크기는 두 배다. |
| 163 | 빈칸이면 1워드 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L191 | null 기본값 1이다. |
| 163 | Byte Order 후 상위·하위 바이트 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L194~197 | normWord 후 high, low로 넣는다. |
| 165 | Char Encoding은 문자셋 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L201~202 L220~227 | 설정으로 Charset을 구해 문자열을 만든다. |
| 165 | 빈칸 UTF-8, 미지원 문자셋 거부 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L220~227 | null/blank 기본값과 forName 예외 변환이다. |
| 165 | 변환 후 끝의 NUL만 제거 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L202~207 | 문자열 끝부터 NUL을 제거해 substring한다. |

### BYTES의 결과는 대문자 16진수 문자열이다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 173 | Length 워드 수·기본 1 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L210~213 | null 기본값과 반복 단위가 워드다. |
| 173 | 바이트 보정 후 대문자 hex 연결 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L214~217 | %02X%02X로 상위·하위 바이트를 출력한다. |
| 173 | DEAD·BEEF → DEADBEEF | nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/decode/PlcValueDecoderTest.java @ d3636ab86763 L146~150 | 예제와 같은 입력·length=2·결과를 기존 시험에서 읽었다. |

### 숫자형 Length는 배열 요소 수를 정한다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 182 | 빈칸·1은 단일 값 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L50~59 | length>1이 아니면 decodeScalar로 반환한다. |
| 182 | 1 초과는 같은 타입의 목록 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L51~57 | width씩 이동하며 decodeScalar 결과를 List에 넣는다. |
| 182 | 지정 길이는 양의 정수 | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L349 L366; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd L489 | INT와 null 또는 >0 CHECK, UI 정수 파싱을 확인했다. |
| 184 | 배열 점유 수는 요소 수×폭 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcRegisterSpec.java @ d3636ab86763 L62~66 | 고정 타입 length×wordWidth다. |
| 184 | 요소마다 동일한 순서·배율 적용 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L51~55 L62~79 | 같은 spec을 각 decodeScalar 호출에 전달한다. |

### Byte Order·Word Order의 입력 철자와 저장 제한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 199 | 이 수정이 들어간 판본은 —·BIG_ENDIAN·LITTLE_ENDIAN 중 선택 | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 L65~66 L80~81 L562~570 L622~644 | 두 순서 열을 PLC_ORDER_OPTIONS에 연결하고 Select로 렌더한다. |
| 199 | —는 값을 비운다. | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 L625~627 L634 | 빈 선택 항목을 빈 문자열로 전달한다. |
| 199 | 기존에 저장된 목록 밖 값도 항목 표시 | nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 L635~637 | 현재 rawValue가 목록 밖이면 그 문자열의 SelectItem을 추가한다. |
| 201 | 모델 상세 저장은 공백 제거·대문자 정규화 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 L494~497; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 L12~20 | 상세 PUT 처리에서 normalizeOptional을 호출하고 trim·Locale.ROOT 대문자 변환을 한다. 빈 값은 null이다. |
| 201 | 허용값 밖 입력은 삭제·저장 전에 400 INVALID_REQUEST | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 L494~497 L527~528; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 L8~20; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd L89~98 | valueOf 실패를 UiBadRequestException으로 바꾼다. 정규화 호출은 deleteAllByGroupKey·upsert보다 앞이며 handler가 400과 INVALID_REQUEST로 응답한다. |
| 203 | V9 적용 DB는 VARCHAR(20), LITTLE_ENDIAN 수용 | nori-tc-exec/db/migration/V9__widen_plc_register_order_columns.sql @ 1d2879d14406 L16~18 | 두 컬럼을 VARCHAR(20)으로 확장한다. 실제 현장 저장 성공을 주장하지 않는다. |
| 203 | 현장 V9 적용은 미확인 | 이 세션의 실행 범위: 로컬 파일·git 읽기와 문서 검사만 수행 | DB 접속·적용·조회를 실행하지 않았다. |
| 205 | Gateway 공백 제거·대소문자 무관·두 철자 해석 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcByteOrder.java @ d3636ab86763 L11~26; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcWordOrder.java @ d3636ab86763 L9~24 | 변경 없는 enum·fromText를 다시 읽고 기존 설명을 그대로 보존했다. |
| 205 | Gateway 빈칸 기본값·다른 철자의 변환 실패 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcByteOrder.java @ d3636ab86763 L22~26; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcWordOrder.java @ d3636ab86763 L20~24; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/PlcModelSpecMapper.java @ d3636ab86763 L56~57; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 L61~62 | null/blank는 BIG_ENDIAN, 그 외는 valueOf로 변환한다. 모델 상세 저장 검사와 런타임 해석을 구분했다. |

### Byte Order는 한 워드 안의 두 바이트에 적용한다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 213 | Byte Order는 Word Order와 독립 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcByteOrder.java @ d3636ab86763 L6~9 | 워드 내부와 워드 간 순서를 구분한다. |
| 217 | BIG_ENDIAN은 바꾸지 않음 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L87~92 | BIG이면 원 워드를 반환한다. 빈칸 기본값은 입력 철자 절이 소유한다. |
| 218 | LITTLE_ENDIAN은 두 바이트 교환 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L89~90 | 8비트 이동과 마스크로 교환한다. |

### Word Order는 여러 워드로 된 값의 상하위를 정한다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 226 | 정수·실수·BCD의 여러 워드 순서 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L71~79 L123~132 L156~164 | assemble과 BCD 루프에서 순서를 사용한다. |
| 230 | BIG_ENDIAN은 상위 먼저 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcWordOrder.java @ d3636ab86763 L6~7; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L125~130 | 앞 워드부터 상위로 조립한다. 빈칸 기본값은 입력 철자 절이 소유한다. |
| 231 | LITTLE_ENDIAN은 하위 먼저 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L127~130 L157~164 | 역순 인덱스로 조립한다. |
| 233 | 단일 워드에는 영향 없음 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcWordOrder.java @ d3636ab86763 L6~7; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L66~70 L123~132 | 한 워드 값은 순서 변경을 하지 않는다. |
| 233 | STRING·BYTES는 Word Order 미적용 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L190~217 | 두 메서드 모두 wordOffset+k로 순회하고 byteOrder만 사용한다. |

### Scale은 해석한 숫자에 곱하는 배율이다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 241 | 정수·실수·BCD 결과에 곱함 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L67~79 L231~242 | 숫자 분기만 scale 함수를 호출한다. |
| 241 | null이면 그대로, 지정하면 부동소수점 곱 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L231~242 | null 반환 분기와 Double 곱이다. |
| 241 | INT16 251×0.1=25.1 예제 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/decode/PlcValueDecoderTest.java @ d3636ab86763 L28~33 | 기존 시험의 입력·배율·기대값을 그대로 대조했다. |
| 243 | BOOL·STRING·BYTES에 배율 미적용 | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L42~47 L65~80 L107~117 | 문자열·바이트 조기 반환, BOOL은 scale 호출이 없다. |

## 매뉴얼과 코드가 다른 곳

검색 원문에 채택한 사실에서는 I §4.10과 PLC 구조 문서의 설명이 실제 읽기 함수·타입·기본 순서·디코드 로직과 일치했다. 코드와 다른 제조사 주소 체계나 현장 값을 매뉴얼에서 보완해 쓰지 않았다.

제품 수정 D 이전에는 제공 스키마와 런타임 값의 불일치를 확인했다. `nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L352~353`은 byte_order·word_order를 VARCHAR(10)으로 정의하고 `nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcRegisterEntity.java @ 6fe71f6b2da6 L76~80`도 length=10이다. 이 기록은 이전 원천의 이력이다. 이번 최종 원문은 V9의 VARCHAR(20) 확장과 UI·서버 검사를 기준으로 갱신했다. 이전의 자유 입력·10자 저장 거부 가능성·그에 따른 저장 확인 인계 문장은 더 이상 최종 원문의 현행 설명으로 사용하지 않는다. 현장 DB나 실제 저장 요청은 실행하지 않았다. 제품·스키마·다른 문서는 이 세션에서 수정하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 뺀 이유 | 확인할 담당 역할 |
|---|---|---|
| 제조사별 실제 주소표, 공정 단위, 지정 배율 | 승인된 제조사 원본이 없다. Scale 곱셈 동작만 기술했다. | 설비·PLC 모델 담당 |
| 제조사 표기 주소에서 Start Address로 바꾸는 구체 변환표 | 코드는 0-based 통신 주소를 그대로 보내며 제조사 주소표는 제공되지 않았다. | 제조사 인터페이스·PLC 모델 담당 |
| LITTLE_ENDIAN의 현장 저장 성공 및 적용 상태 | V9의 20자 확장은 원천으로 확인했으나 현장 적용 여부·DB 저장 요청은 실행하지 않았다. | 제품 스키마·모델 관리 담당 |
| BOOL 배열의 저장·레이아웃 전체 지원 보장 | 디코더는 length>1 목록을 만들지만 레지스터 영역 레이아웃 검증기는 BOOL 점유를 1워드로 처리한다. 숫자형 Length 설명으로 범위를 한정했다. | PLC 모델·Gateway 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 이 문서에 복제하지 않은 사실 |
|---|---|
| 00.rag/user/model-details-and-flow-editing.md | Check Out·Save, PLC Group 선행 저장, 필수 입력·선택 목록 전체, 저장 API 권한·거부 처리 절차 |
| 00.rag/user/equipment-commands-and-polling.md | 폴링 Start·End 조작, 명령 결과·권한·화면 완료 문구의 처리 |
| 00.rag/user/plc-troubleshooting.md | 연결 실패·TIMEOUT·디코드 실패·격리·미발행의 진단과 인계 절차 |
| 00.rag/user/equipment-registration-and-settings.md | Unit ID·연결 제한시간·재연결 설정의 입력 조건과 초기값 |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 모델 변경의 저장·생성·배포·적용 절차 |
| 00.rag/user/error-message-and-code-reference.md | 예외 문구와 오류 코드 전체의 의미 표 |

겹치는 기존 원문에서 PLC 부분을 확인했다. 영역·타입의 단순 선택지 전체 표를 다시 나열하지 않고 의미·워드 폭·해석 조건으로 작성했다. 그룹 소유 개념과 값 해석의 최소 문맥은 이 참조 문서의 독립 답에 필요한 범위만 사용했다.

## 초회 작성 검사 결과(보완 전 이력)

초회 작성에서 실행한 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/plc-modbus-register-and-type-reference.md
```

실행 환경: GNU bash 5.2.37(1)-release (x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS. 검사기 wrapper가 지정한 CommonMark 0.32.0 및 현재 ContentSanitizer를 그대로 사용했다. 검사기를 변경하지 않았다.

첫 실행은 위반 0건, 마지막 원천 메타 보완 후 두 번째 실행도 종료코드 0이었다. 최종 검사 출력의 마지막 10줄은 다음과 같다.

```text
    L199-199 문단 58  | Gateway의 「Word Order」는 여러 워드로 구성한 정수·실수·BCD 값의 워드 순서를 정한다.
    L203-203 표 행 62  | Word Order=BIG_ENDIAN | 먼저 오는 워드=상위 워드가 먼저 온다. 빈칸도 이 값으로 해석한다.
    L204-204 표 행 49  | Word Order=LITTLE_ENDIAN | 먼저 오는 워드=하위 워드가 먼저 온다.
    L206-206 문단 77  | 단일 워드 값의 해석에는 영향을 주지 않는다. STRING·BYTES는 「Word Order」를 적용하지 않고 응답 워드 순서대로 읽는다.
§ L208-216 [23] Scale은 해석한 숫자에 곱하는 배율이다   parent=166 children=2 sources=2
    L214-214 문단 123  | 「Scale」은 정수·실수·BCD 해석 결과에 곱하는 배율이다. 비어 있으면 해석한 숫자를 그대로 쓰고, 값이 있으면 곱한 부동소수점 결과를 쓴다. 예를 들어 INT16 원시 값 251에 0.1을 곱하면 25.1이 된다.
    L216-216 문단 42  | BOOL·STRING·BYTES의 해석에는 「Scale」을 적용하지 않는다.
SUMMARY sections=17 children=48 maxChild=161 maxParent=390
CORPUS docs=41/53 sections=572 part={admin=9, user=32} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=3, 트러블슈팅=12}
TOTAL files=41 targets=1 violations=0
```

출력 전수 검색 결과 `MASK` 0줄, `CROSS VIOLATION` 0줄이었다. 코퍼스 수는 검사 당시 출력이며 이 세션이 41문서를 작성하거나 검증했다는 뜻이 아니다. 최종 원문의 최대 자식은 161자, 최대 부모는 390자였다.

보조 줄 대조에서는 첫 스크립트가 Area 표 머리글 L31을 본문 사실로 잘못 포함해 assertion에 실패했다. 검색 원문 결함이 아니며 머리글을 다음 구분 행으로 식별하도록 보조 조회를 바로잡았다. 재실행에서 자식 본문 48줄이 작성 기록의 사실 표에 모두 대응하는지 확인했다. 지정 검사기는 변경하지 않았다.

초회 자체검사에서 17절을 읽고 아래처럼 판정했으나, 이후 메인 검증은 입력·저장 제약과 Address Qualifier 미사용 누락 2건을 지적했다. 아래 표는 당시 자체검사 이력이며 보완 완료의 근거로 대체 사용하지 않는다. 현재 18절의 보완 뒤 판정은 다음 절에 적었다.

| 절 제목 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·기존 문서와 답 복제 없음 |
|---|---|---|---|---|
| PLC Group과 PLC Register가 나타내는 범위 | P | P | P | P |
| Area별 읽기 대상·함수 코드·개수 단위 | P | P | P | P |
| Start Address와 Word Offset은 어디서 세는가 | P | P | P | P |
| 레지스터가 그룹의 읽기 범위 안에 들어가는 조건 | P | P | P | P |
| BOOL과 Bit Index의 참·거짓 판정 | P | P | P | P |
| SINT8·USINT8과 Byte Index의 바이트 선택 | P | P | P | P |
| INT16·UINT16·INT32·UINT32의 부호와 워드 수 | P | P | P | P |
| INT64·UINT64의 해석과 큰 양수 보존 | P | P | P | P |
| FLOAT32·FLOAT64는 워드를 실수로 해석한다 | P | P | P | P |
| BCD16·BCD32·BCD64의 십진 숫자 해석 | P | P | P | P |
| BCD Signed가 정하는 부호 자리 | P | P | P | P |
| STRING의 길이·문자 인코딩·끝 채움 제거 | P | P | P | P |
| BYTES의 결과는 대문자 16진수 문자열이다 | P | P | P | P |
| 숫자형 Length는 배열 요소 수를 정한다 | P | P | P | P |
| Byte Order는 한 워드 안의 두 바이트에 적용한다 | P | P | P | P |
| Word Order는 여러 워드로 된 값의 상하위를 정한다 | P | P | P | P |
| Scale은 해석한 숫자에 곱하는 배율이다 | P | P | P | P |

제목 정확 중복·불필요한 틀 반복, 빈 틀 문장, 이름 토막 뜻풀이, 깨진 조사·주어, 오답 인용, 관리 전용 조치·admin objectKey는 발견하지 않았다. 타입별 부호·폭 표는 같은 질문의 값 묶음으로 구성했고 숫자로 절을 분할하지 않았다. UINT64의 큰 정수 설명과 Scale 일반 설명은 각각 정수 보존과 배율 계약을 소유하며, BYTE/WORD 순서와 문자열 절은 각각 일반 순서 규칙과 타입별 적용 조건을 소유한다. 각 절의 필요한 최소 조건을 제외한 답 복제는 발견하지 않았다.

기존 시험은 실행하지 않고 기대값을 읽어 코드와 대조했다. PlcValueDecoderTest의 정수·순서·부호·BCD·문자열·배열·BOOL·BYTES·UINT64·범위 기대값, ModbusTcpCodecTest의 주소 송신·LSB 비트·함수 불일치 기대값, PlcModelSpecMapperTest의 주소·개수·순서 기본값을 확인했다. common의 영역/레이아웃 시험과 UI Backend의 범위 초과·미존재 그룹·유효 저장 시험도 읽었다. 이 읽기를 시험 PASS로 기록하지 않았다.

## 메인 검증 결함 2건 보완과 검사(제품 D 반영 전 이력)

당시 허가된 두 결함과 직접 영향만 수정했다. 보완 전 검색 원문 SHA는 `f2955554a627481e6097f4132933ed95ae1057672645e667a617a4f5b2723bf2`, 당시 보완 뒤 SHA는 `395dae5c5a67cf5d5fc9d88d8da814f32a5af45c2b26b0a1bb3cc64ea9a2406b`이다. 아래의 자유 입력·10자 제한 설명과 52자식 검사 결과는 제품 수정 D 이전의 이력이다.

| 결함 | 누락 원인 | 수정과 직접 영향 | 확인 결과 |
|---|---|---|---|
| 1 | 런타임 enum 해석과 화면 입력·저장 가능 범위를 분리한 채 입력 제약을 원문에서 생략했다. | 입력 철자와 저장 제한 절을 추가했다. BIG_ENDIAN 기본값은 이 절에서만 소유하도록 기존 두 순서 표의 빈칸 설명을 제거했다. | 자유 입력, 허용 철자·정규화, 변환 실패, 10자/13자 불일치, 모델 담당자 인계를 원문 L198~202와 사실 대조에 반영했다. |
| 2 | 값의 효과를 미확인으로만 분류해 확인 가능한 미사용 사실까지 생략했다. | 주소 설명에 Address Qualifier 미사용과 Area·Start Address 주소 결정을 추가했다. 기존 생략 목록의 해당 행을 제거했다. | 원문 L54를 두 스펙 변환 경로·코덱·화면 열·저장 엔티티에 대조했다. |

보완 직전 저장소 5개의 HEAD는 기존 판본과 같았고, 추가로 읽은 PlcEquipmentSpecReloader·PlcSpecConversionValidator·TcModelPlcGroupEntity를 포함한 관련 파일의 `git status --short -- <경로>` 출력은 모두 비어 있었다(각 종료코드 0). 전역 ignore 접근 경고는 이전과 동일했다.

`git grep -n ENDIAN -- ':(glob)**/src/main/**'`은 ui-backend와 common에서 각각 출력 0건·종료코드 1이었다. Gateway의 `git grep -n -i qualifier -- gateway ':(exclude)**/test/**'`는 무관한 Redis 통신 로그 adapter의 Spring Qualifier 2줄만 반환했다. DB·컨텍스트 매퍼와 Modbus 코덱을 직접 읽어 주소 보조값이 읽기 요청으로 전달되지 않음을 함께 확인했다.

PlcSpecConversionValidator L17~19는 컨텍스트 매퍼를 호출하고, PlcEquipmentSpecReloader L67~75는 변환 후에 bind한다. 본문은 확인한 변환 실패까지만 기술하며 설비 전체 적재·롤백·후속 명령 결과를 새로 단정하지 않는다.

위와 같은 검사기 명령을 최종 원문에 다시 실행했다. 보완 검사 종료코드는 0, MASK 출력은 0줄, CROSS VIOLATION 출력은 0줄이었다. 마지막 10줄은 다음과 같다.

```text
    L223-223 문단 58  | Gateway의 「Word Order」는 여러 워드로 구성한 정수·실수·BCD 값의 워드 순서를 정한다.
    L227-227 표 행 46  | Word Order=BIG_ENDIAN | 먼저 오는 워드=상위 워드가 먼저 온다.
    L228-228 표 행 49  | Word Order=LITTLE_ENDIAN | 먼저 오는 워드=하위 워드가 먼저 온다.
    L230-230 문단 77  | 단일 워드 값의 해석에는 영향을 주지 않는다. STRING·BYTES는 「Word Order」를 적용하지 않고 응답 워드 순서대로 읽는다.
§ L232-240 [23] Scale은 해석한 숫자에 곱하는 배율이다   parent=166 children=2 sources=2
    L238-238 문단 123  | 「Scale」은 정수·실수·BCD 해석 결과에 곱하는 배율이다. 비어 있으면 해석한 숫자를 그대로 쓰고, 값이 있으면 곱한 부동소수점 결과를 쓴다. 예를 들어 INT16 원시 값 251에 0.1을 곱하면 25.1이 된다.
    L240-240 문단 42  | BOOL·STRING·BYTES의 해석에는 「Scale」을 적용하지 않는다.
SUMMARY sections=18 children=52 maxChild=161 maxParent=390
CORPUS docs=43/53 sections=627 part={admin=9, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=5, 트러블슈팅=12}
TOTAL files=43 targets=1 violations=0
```

보완 뒤 18절 전체를 읽었다. 기존 17개 제목은 유지했고 새 제목은 「Byte Order·Word Order의 입력 철자와 저장 제한」이다. 전 절의 제목 식별성·원천 일치·독립 답·사실 소유를 확인했다. 새 공통 입력 절이 자유 입력·허용 철자·기본값·저장 제약을 소유하고 기존 Byte Order·Word Order 절은 각각 디코드 처리만 설명한다. 주소 절의 미사용 사실은 다른 절에 복제하지 않았다. 마스킹 후 BIG_ENDIAN·LITTLE_ENDIAN·Address Qualifier와 인계 정보가 보존되며 admin 경로·내부 조작을 추가하지 않았다. 보완 자체검사에서 남은 두 지적과 수정 파생 결함은 발견하지 않았다. 메인 세션의 독립 재대조는 별도다.

검색 원문의 기존 사실 대조 행은 줄 이동에 맞춰 결정적으로 갱신했고, 새 문장마다 대조 행을 추가했다. 최종 자식 본문 52줄과 작성 기록의 줄 대응을 재확인했다. 제품 코드·다른 문서·검사기를 수정하지 않았다.

## 제품 수정 D 반영과 최종 검사(2026-11-19)

대상 원문 전체를 다시 읽어 A~D의 영향을 확인했다. A의 Kafka 연결 보안 전달과 B의 Secret 생성 linger 설정은 이 원문이 다루지 않는다. C는 HSMS·Socket 실행 설정 변경이며 이 원문의 STRING 문자셋은 PLC 디코더의 계약이므로 변경 대상이 아니다. D에 해당하는 입력 철자·저장 제한 절과 H1 적용 메타만 수정했다. 제목 18개와 다른 17절의 본문·표·원천은 그대로 보존했다.

| 변경 전 사실 | 현재 원천에서 확인한 사실 | 반영 |
|---|---|---|
| Byte Order·Word Order 자유 입력 | 목록은 —·BIG_ENDIAN·LITTLE_ENDIAN이며 기존 목록 밖 값도 별도 항목으로 표시한다. | 원문 L199 |
| 임의 철자가 런타임까지 전달됨 | 상세 PUT 저장은 PlcRegisterOrder로 정규화·검사하고 기존 행 삭제·저장 전에 잘못된 입력을 거부한다. | 원문 L201 |
| 10자 저장 제한 때문에 LITTLE_ENDIAN 거부 가능 | V9 적용 DB는 두 컬럼이 VARCHAR(20)이다. 현장 적용 여부는 미확인이다. | 원문 L203 |
| Gateway 해석 규칙 | 기존 두 철자·공백 제거·대소문자 무관·빈칸 BIG_ENDIAN 규칙은 변경 없다. | 기존 두 문장을 원문 L205에 그대로 보존 |

이 검사는 모델 상세 PUT 경로에 한정했다. `JpaModelManagementDetailSupport.java`의 `clonePlcDetails` L255~272는 저장된 byteOrder·wordOrder를 그대로 복사함을 별도로 읽었으며, 모든 모델 복사 경로가 새 정규화 검사를 수행한다고 본문에 확대하지 않았다. 모델 상세 요청 전달은 `model.api.ts` L297~319 → `ModelDetailController.java` L55~115 → 현재 `JpaModelDetailCommandPort.replacePlcRegisters` L494~528로 대조했다.

`PlcRegisterOrder.normalizeOptional` L19~20의 오류 문구는 `PLC Register `와 모델·행·그룹·변수·열 문맥, 원래 입력값, `허용값: BIG_ENDIAN, LITTLE_ENDIAN (빈값은 기본 BIG_ENDIAN).`을 연결한다. 원문에는 오류 문구 표를 복제하지 않고 저장 거부의 조건·시점·응답 코드만 적었다. `JpaModelDetailCommandPortTest.java` L544~586의 정규화·빈 값·거부 시 delete/upsert 미호출 기대값과 `PlcRegisterOrderJpaCompatibilityTest.java`의 확장 컬럼 호환 기대값을 읽었다. 제품 시험을 실행하거나 메인 세션의 시험 결과를 이 세션의 PASS로 기록하지 않았다.

연관 원문 확인에서 `00.rag/user/model-details-and-flow-editing.md` L145~157의 PLC 선택 목록 표에는 새 Byte Order·Word Order 선택값이 아직 없다. 현재 프런트 원천의 선택 목록과 비교한 보완 필요 사항을 other_document_findings로 인계하며 해당 문서는 수정하지 않았다.

현재 검사기 경로는 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`다. 이전 Temp 경로는 Test-Path가 false였고 현재 작업 폴더에서 기존 검사기를 찾았다. 검사기 파일은 변경하지 않았다(check.sh SHA-256 `ea487ba231bee8b7f82d69afb2a4ed0f8c4c7015b957b0207abdbdecd03cc189`).

이번 첫 실행은 종료코드 1, `TOTAL files=51 targets=1 violations=49`였다. 마지막 출력에는 다음 오류가 포함됐다.

```text
VIOLATION L208 원천 commit:path 없음 'nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcByteOrder.java @ d3636ab86763 ; 값과 기본값 L6~14 L22~26'
```

원인은 검사기가 실행하는 Git 읽기의 소유자 확인 거부로 확인했다. Git 2.52.0.windows.1에서 `git -C ../nori-tc-gateway cat-file -e d3636ab86763:gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcByteOrder.java`는 종료코드 128과 `fatal: detected dubious ownership in repository at 'C:/workspace_nori/nori-tc/nori-tc-gateway'`를 출력했다. 같은 읽기에 `-c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway`를 명시하면 종료코드 0이었다. 검사기 `RagDocCheck.java` L405~412는 `git -C <repo> show ...`의 실패를 원천 없음으로 반환한다.

전역 Git 설정과 검사기 파일을 바꾸지 않고, 최종 검사 프로세스에만 `GIT_CONFIG_COUNT=5`와 `GIT_CONFIG_KEY_0`부터 `GIT_CONFIG_KEY_4`까지 각각 `safe.directory`, 대응하는 값으로 `C:/workspace_nori/nori-tc/` 아래의 `nori-tc-gateway`·`nori-tc-common`·`nori-tc-exec`·`nori-tc-ui-front`·`nori-tc-ui-backend` 절대경로를 지정했다. 원천 판본·줄·마스킹·구조 검사 항목은 그대로 실행했다.

```text
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/plc-modbus-register-and-type-reference.md
```

최종 검사 종료코드 0, MASK 0줄, CROSS VIOLATION 0줄이다. 마지막 10줄은 다음과 같다.

```text
    L226-226 문단 58  | Gateway의 「Word Order」는 여러 워드로 구성한 정수·실수·BCD 값의 워드 순서를 정한다.
    L230-230 표 행 46  | Word Order=BIG_ENDIAN | 먼저 오는 워드=상위 워드가 먼저 온다.
    L231-231 표 행 49  | Word Order=LITTLE_ENDIAN | 먼저 오는 워드=하위 워드가 먼저 온다.
    L233-233 문단 77  | 단일 워드 값의 해석에는 영향을 주지 않는다. STRING·BYTES는 「Word Order」를 적용하지 않고 응답 워드 순서대로 읽는다.
§ L235-243 [23] Scale은 해석한 숫자에 곱하는 배율이다   parent=166 children=2 sources=2
    L241-241 문단 123  | 「Scale」은 정수·실수·BCD 해석 결과에 곱하는 배율이다. 비어 있으면 해석한 숫자를 그대로 쓰고, 값이 있으면 곱한 부동소수점 결과를 쓴다. 예를 들어 INT16 원시 값 251에 0.1을 곱하면 25.1이 된다.
    L243-243 문단 42  | BOOL·STRING·BYTES의 해석에는 「Scale」을 적용하지 않는다.
SUMMARY sections=18 children=53 maxChild=161 maxParent=390
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

18절 전체의 제목·본문을 읽어 문장·사실 소유·권한 경계를 확인했다. 수정 절은 화면 선택, 상세 저장 검사, V9 적용 조건, Gateway 해석을 구분하며 다른 절의 디코드 처리 표를 반복하지 않는다. SHA-256은 메타에만 있고 본문에는 저장소 상태를 적지 않았다. 마스킹 후 세 선택 표시·오류 코드·VARCHAR(20)·V9 조건이 보존됐다. 작성 기록의 사실 대조 행도 최종 원문의 53개 자식 본문 줄과 대응시켜 재확인했다. 미확인 현장 V9 적용을 성공으로 기록하지 않았다.

## 미검증·종료와 정리

- 이 세션에서는 제품 코드를 변경하지 않아 Gradle build·단위 시험·통합 시험을 실행하지 않았다. 실제 화면 조작·현장 배포·DB 저장·제조사 설비 통신은 확인하지 않았다.
- 다음 작업은 메인 세션의 원천 대조다. 다른 문서 수정·제품 결함 수정·업로드를 이 세션에서 진행하지 않는다. 미확인 제조사 주소·배율과 저장 성공을 추정해 추가하지 않는다.
- 독립 Agent·외부 AI 호출은 0회다. 검사기는 초회 2회·메인 지적 보완 1회·제품 D 반영 2회로 누적 5회 실행했다. D 반영 첫 검사 종료코드는 1, 원인 확인 후 최종 검사는 0이다. 전체 벽시계 시간·provider 사용량·비용은 별도 계측하지 않아 미확인이다.
- 검사용 임시 파일·상주 프로세스·컨테이너를 만들지 않았다. 다섯 검사 프로세스는 모두 종료했고 결과·실패 근거를 위 기록에 보존했다. 지정 work 디렉터리는 기존에 존재함을 확인했으며 그 안에 이 세션 소유 자원을 생성하지 않아 삭제하지 않았다. 정리할 세션 소유 임시 자원은 없다.

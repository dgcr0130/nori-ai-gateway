# PLC·Modbus 레지스터·데이터 타입
```rag-meta
유형: 참조
적용: nori-tc-gateway d3636ab86763·nori-tc-common 6fe71f6b2da6 소스 및 nori-tc-ui-front modelDetailColumnDefs.tsx 2476db5c5093·nori-tc-ui-backend JpaModelDetailCommandPort.java d36c10ff8579·PlcRegisterOrder.java d36c10ff8579·nori-tc-exec V9__widen_plc_register_order_columns.sql 1d2879d14406 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/model-details-and-flow-editing.md
```

## PLC Group과 PLC Register가 나타내는 범위
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 ; 그룹 정의 L10~25
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcResponseMapper.java @ d3636ab86763 ; 변수와 메시지 구성 L51~64
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; PLC 표 항목 L104~127
```

PLC 모델의 「PLC Group」은 한 번에 읽을 영역·시작 주소·개수·주기를 묶는다. 「PLC Register」는 그룹 응답에서 변수 값을 해석하는 규칙이다. 「Variable Name」이 해석한 값의 키가 되고 그룹의 「Message Name」이 결과 메시지를 구별한다.

정의 편집은 `00.rag/user/model-details-and-flow-editing.md`, 폴링 조작은 `00.rag/user/equipment-commands-and-polling.md`, 연결·해석 문제 확인은 `00.rag/user/plc-troubleshooting.md`를 따른다.

## Area별 읽기 대상·함수 코드·개수 단위
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 ; 영역·단위·상한 L6~14 L24~39
원천: nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 ; 영역별 함수 코드 L145~151
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/codec/PlcReadRequest.java @ d3636ab86763 ; 읽기 개수 검사 L24~33
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcRegisterLayoutValidator.java @ 6fe71f6b2da6 ; 레지스터의 16비트 워드 단위 L25~26
```

Modbus TCP에서 「Area」는 읽을 메모리 영역과 함수 코드를 정한다. 같은 주소라도 영역이 다르면 다른 칸이다. 표의 개수는 한 요청의 「Read Count」 허용 범위이며, 워드는 16비트이다.

| Area | 읽기 대상 | 함수 코드 | Read Count |
|---|---|---|---|
| COIL | 코일 비트 | 0x01 | 1~2000비트 |
| DISCRETE_INPUT | 디스크리트 입력 비트 | 0x02 | 1~2000비트 |
| HOLDING_REGISTER | 홀딩 레지스터 | 0x03 | 1~125워드 |
| INPUT_REGISTER | 입력 레지스터 | 0x04 | 1~125워드 |

## Start Address와 Word Offset은 어디서 세는가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/codec/PlcReadRequest.java @ d3636ab86763 ; 시작 주소·범위 L10~15 L24~32
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcRegisterSpec.java @ d3636ab86763 ; 응답 내 위치 L8~19 L39~42
원천: nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 ; 영역·주소 전송 L35~53 L145~151
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/model/PlcArea.java @ d3636ab86763 ; 비트 영역의 위치 단위 L24~29
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/PlcModelSpecMapper.java @ d3636ab86763 ; 읽기 요청 구성 L39~43
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 ; 읽기 요청 구성 L39~44
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcGroupEntity.java @ 6fe71f6b2da6 ; 주소 보조값 저장 L65~66
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; 그룹 표시 열 L104~112
```

Modbus TCP의 「Start Address」는 0부터 세는 통신 주소이며 0~65535이다. Gateway는 입력한 정수를 읽기 요청의 주소로 그대로 보낸다.

「Word Offset」은 그룹 응답의 첫 요소를 0으로 세는 상대 위치이며 0 이상이다. HOLDING_REGISTER·INPUT_REGISTER에서는 워드 위치, COIL·DISCRETE_INPUT에서는 비트 위치를 뜻한다.

확인한 Gateway 판본은 「PLC Group」의 「Address Qualifier」를 Modbus 읽기 요청에 쓰지 않는다. 읽을 주소는 「Area」와 「Start Address」로만 정한다.

## 레지스터가 그룹의 읽기 범위 안에 들어가는 조건
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 ; 범위 검사 L38~46
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcRegisterLayoutValidator.java @ 6fe71f6b2da6 ; 중복·겹침·범위 검사 L69~118 L137~145
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ 985d17c604dd ; 저장 전 레이아웃 검증 L514~524
```

레지스터의 「Word Offset」과 차지하는 요소 수를 더한 값은 그룹의 「Read Count」 이하여야 한다. Gateway는 이 범위를 넘는 디코드 정의를 거부한다.

레지스터 저장은 같은 비트 위치의 중복이나 워드 점유 범위의 겹침도 거부한다. 레지스터 영역의 BOOL은 같은 워드에서 서로 다른 「Bit Index」를 사용할 수 있지만, 그 워드 전체를 쓰는 다른 변수와는 겹칠 수 없다.

## BOOL과 Bit Index의 참·거짓 판정
```rag-meta
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/plc/PlcAreaDataTypeValidator.java @ 6fe71f6b2da6 ; 비트 영역 타입 제한 L28~45
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; BOOL 해석 L107~117
원천: nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 ; 응답 비트 순서 L126~138
```

BOOL은 선택한 비트가 1이면 true, 0이면 false로 해석한다. COIL·DISCRETE_INPUT은 BOOL만 허용하며 「Bit Index」는 비워야 한다. Modbus 응답 비트는 바이트의 낮은 자리부터 순서대로 풀어 읽는다.

HOLDING_REGISTER·INPUT_REGISTER의 BOOL은 워드에서 「Bit Index」 0~15를 읽는다. 바이트 순서를 보정한 뒤 비트를 선택하며, 「Bit Index」가 비어 있으면 비트 0을 읽는다.

## SINT8·USINT8과 Byte Index의 바이트 선택
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 ; 요소 폭 L15~17
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 8비트 정수·바이트 선택 L67~68 L95~104
```

SINT8·USINT8은 값 하나마다 1워드를 차지하고 그 안의 한 바이트를 해석한다.

| Data Type | 선택한 바이트의 해석 |
|---|---|
| SINT8 | 부호 있는 8비트 정수로 해석한다. |
| USINT8 | 부호 없는 8비트 정수로 해석한다. |

「Byte Index」는 0이면 하위 바이트, 1이면 상위 바이트이며 비어 있으면 0이다. 「Byte Order」 보정이 먼저 적용되고 그 결과에서 바이트를 고른다.

## INT16·UINT16·INT32·UINT32의 부호와 워드 수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 ; 요소 폭 L18~21
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 정수 해석 L69~72
```

「Data Type」의 정수형은 응답 워드를 다음 크기와 부호로 해석한다. 워드 수는 값 하나 기준이다.

| Data Type | 값의 해석 | 워드 수 |
|---|---|---|
| INT16 | 부호 있는 16비트 정수 | 1워드 |
| UINT16 | 부호 없는 16비트 정수 | 1워드 |
| INT32 | 부호 있는 32비트 정수 | 2워드 |
| UINT32 | 부호 없는 32비트 정수 | 2워드 |

## INT64·UINT64의 해석과 큰 양수 보존
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 ; 요소 폭 L22~23
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 64비트 정수 L73~74 L135~146
```

| Data Type | 값의 해석 | 워드 수 |
|---|---|---|
| INT64 | 부호 있는 64비트 정수 | 값 하나에 4워드 |
| UINT64 | 부호 없는 64비트 정수 | 값 하나에 4워드 |

Gateway의 UINT64 디코드는 「Scale」이 없으면 2^63 이상인 양수도 큰 정수로 보존한다. 「Scale」이 있으면 배율을 곱한 부동소수점 값으로 바꾼다.

## FLOAT32·FLOAT64는 워드를 실수로 해석한다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 ; 요소 폭 L24~25
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 비트열을 실수로 해석 L75~76
```

| Data Type | 값의 해석 | 워드 수 |
|---|---|---|
| FLOAT32 | 조립한 32비트 비트열을 부동소수점 실수로 해석한다. | 값 하나에 2워드 |
| FLOAT64 | 조립한 64비트 비트열을 부동소수점 실수로 해석한다. | 값 하나에 4워드 |

## BCD16·BCD32·BCD64의 십진 숫자 해석
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcDataType.java @ d3636ab86763 ; 요소 폭 L26~28
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; BCD 해석 L152~187
```

BCD는 4비트마다 십진 숫자 한 자리를 담는 형식이다. Gateway는 각 숫자 자리를 이어 정수로 만든다. 숫자 자리에 9보다 큰 값이 있으면 해석을 거부한다.

| Data Type | 값 하나의 크기 |
|---|---|
| BCD16 | 1워드, 16비트 |
| BCD32 | 2워드, 32비트 |
| BCD64 | 4워드, 64비트 |

## BCD Signed가 정하는 부호 자리
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; BCD 부호 L166~187
```

「BCD Signed」는 BCD16·BCD32·BCD64의 맨 앞 4비트를 부호로 쓸지 정한다.

| BCD Signed | 맨 앞 4비트 처리 |
|---|---|
| true | 0x0은 양수, 0xF는 음수이며 나머지 값은 거부한다. 뒤의 자리만 숫자로 읽는다. |
| false 또는 빈칸 | 맨 앞 자리까지 모두 숫자로 읽는다. |

## STRING의 길이·문자 인코딩·끝 채움 제거
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 문자열 디코드·문자셋 L190~207 L220~227
```

STRING의 「Length」는 문자 수가 아니라 차지하는 워드 수이다. 비어 있으면 1워드를 읽는다. 각 워드에 「Byte Order」를 적용한 뒤 상위·하위 바이트 순으로 문자열을 구성한다.

「Char Encoding」은 문자 해석에 사용할 문자셋이다. 비어 있으면 UTF-8을 쓰며, 지원하지 않는 문자셋이면 해석을 거부한다. 전체 바이트를 문자로 바꾼 뒤 문자열 끝의 NUL 문자만 제거한다.

## BYTES의 결과는 대문자 16진수 문자열이다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 바이트 결과 L210~217
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/decode/PlcValueDecoderTest.java @ d3636ab86763 ; BYTES 기대값 L146~150
```

BYTES의 「Length」는 워드 수이며 비어 있으면 1워드이다. Gateway는 워드마다 「Byte Order」를 적용한 뒤 상위·하위 바이트를 대문자 16진수 문자열로 잇는다. 예를 들어 기본 순서에서 0xDEAD·0xBEEF 두 워드의 결과는 DEADBEEF이다.

## 숫자형 Length는 배열 요소 수를 정한다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcRegisterSpec.java @ d3636ab86763 ; 배열 점유 폭 L55~66
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 배열·단일 값 L50~59
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 길이 제약 L364~368
```

정수·실수·BCD 타입의 「Length」가 비어 있거나 1이면 단일 값을 만든다. 1보다 크면 같은 타입을 연속으로 해석한 값 목록을 만든다. 길이를 지정하면 양의 정수여야 한다.

숫자형 배열이 차지하는 워드 수는 「Length」에 타입별 값 하나의 워드 수를 곱한 값이다. 각 요소에 같은 바이트·워드 순서와 배율을 적용한다.

## Byte Order·Word Order의 입력 철자와 저장 제한
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/modelDetailColumnDefs.tsx @ 2476db5c5093 ; PLC_ORDER_OPTIONS·createPlcColumns L65~66 L80~81 L562~570 L622~644
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailCommandPort.java @ d36c10ff8579 ; replacePlcRegisters L494~497 L527~528
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/PlcRegisterOrder.java @ d36c10ff8579 ; normalizeOptional L8~20
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiBadRequest L89~98
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcByteOrder.java @ d3636ab86763 ; 허용 철자·기본값 L11~26
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcWordOrder.java @ d3636ab86763 ; 허용 철자·기본값 L9~24
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/PlcModelSpecMapper.java @ d3636ab86763 ; 변환 호출 L48~60
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 ; 변환 호출 L53~65
원천: nori-tc-exec/db/migration/V9__widen_plc_register_order_columns.sql @ 1d2879d14406 ; ALTER TABLE L16~18
```

이 수정이 들어간 판본의 「Byte Order」·「Word Order」는 「—」·BIG_ENDIAN·LITTLE_ENDIAN 중에서 고른다. 「—」는 값을 비운다. 기존에 저장된 목록 밖 값도 항목으로 표시된다.

모델 상세 저장은 앞뒤 공백을 제거하고 대문자로 맞춘다. 허용값 밖 입력은 기존 행 삭제·저장 전에 400 INVALID_REQUEST로 거부한다.

V9가 적용된 DB의 두 칸은 VARCHAR(20)이어서 LITTLE_ENDIAN을 담을 수 있다. 현장 V9 적용 여부는 확인하지 않았다.

Gateway는 앞뒤 공백을 제거하고 대소문자 구별 없이 BIG_ENDIAN·LITTLE_ENDIAN만 해석한다. 빈칸은 BIG_ENDIAN이며, 다른 철자는 런타임 스펙 변환에서 실패한다.

## Byte Order는 한 워드 안의 두 바이트에 적용한다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcByteOrder.java @ d3636ab86763 ; 값과 기본값 L6~14 L22~26
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 바이트 보정 L84~92
```

Gateway의 「Byte Order」 해석값은 다음과 같다. 「Word Order」와 별도로 한 워드 안의 두 바이트에 적용한다.

| Byte Order | 디코드 처리 |
|---|---|
| BIG_ENDIAN | 두 바이트를 바꾸지 않는다. |
| LITTLE_ENDIAN | 한 워드 안의 두 바이트를 서로 바꾼다. |

## Word Order는 여러 워드로 된 값의 상하위를 정한다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcWordOrder.java @ d3636ab86763 ; 값과 기본값 L4~12 L20~24
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 워드 조립·BCD·문자열·바이트 L123~132 L156~164 L190~217
```

Gateway의 「Word Order」는 여러 워드로 구성한 정수·실수·BCD 값의 워드 순서를 정한다.

| Word Order | 먼저 오는 워드 |
|---|---|
| BIG_ENDIAN | 상위 워드가 먼저 온다. |
| LITTLE_ENDIAN | 하위 워드가 먼저 온다. |

단일 워드 값의 해석에는 영향을 주지 않는다. STRING·BYTES는 「Word Order」를 적용하지 않고 응답 워드 순서대로 읽는다.

## Scale은 해석한 숫자에 곱하는 배율이다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 적용 대상·곱셈 L65~80 L231~242
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/decode/PlcValueDecoderTest.java @ d3636ab86763 ; 배율 기대값 L28~33
```

「Scale」은 정수·실수·BCD 해석 결과에 곱하는 배율이다. 비어 있으면 해석한 숫자를 그대로 쓰고, 값이 있으면 곱한 부동소수점 결과를 쓴다. 예를 들어 INT16 원시 값 251에 0.1을 곱하면 25.1이 된다.

BOOL·STRING·BYTES의 해석에는 「Scale」을 적용하지 않는다.

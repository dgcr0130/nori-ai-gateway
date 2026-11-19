# PLC 트러블슈팅 작성 기록

## 대상

- objectKey: `00.rag/user/plc-troubleshooting.md`
- 구획: user, 필요 권한 `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/plc-troubleshooting.md`
- 검색 원문 SHA-256: `fa9c73510f3b2ae3d6acfc3f3acc7ed5ffe167b9bc0cefae73470725526f548f`
- 줄 수: 114, H1: 1, H2: 7, 직렬화 자식: 24
- 확인일: 2026-11-19, 한국 시간
- 범위: PLC 연결 정보, 그룹 명령 결과와 실제 응답 만료의 구별, 모델 디코드 변환·적재 실패, 수신 해석·발행 생략의 구별. 제품 변경·커밋·업로드는 수행하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD | 작업 트리 상태 확인 |
|---|---|---|
| nori-tc-gateway | d3636ab86763 | 본문에 인용한 15개 파일 전부 `git status --short -- <경로>` 출력 없음, exit 0. |
| nori-tc-ui-backend | 985d17c604dd | 본문에 인용한 EqpManagementService.java·EqpManagementServiceTest.java 출력 없음, exit 0. 호출 경로의 EqpDirectScenarioService.java·EqpController.java·검증·인가 파일도 수정 없음 확인. |
| nori-tc-ui-front | 9fe69a75dcd3 | 본문에 인용한 EqpManageFormModal.tsx·EqpPollingGroupControl.tsx·useEqpLifecycleCommands.ts·eqp-runtime-state.util.ts·ko.json 출력 없음, exit 0. eqp.api.ts 등 화면 API 흐름도 함께 읽었다. |

소유자가 다른 읽기 전용 형제 저장소에는 명령별 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C <저장소>`를 사용했다. 전역 Git 설정을 바꾸지 않았다. 최초 Git 조회는 dubious ownership으로 실패했고, 이후 지정 저장소를 명시한 조회로 HEAD·상태·diff를 확인했다. 상태 조회에는 사용자 전역 ignore 파일 접근 경고가 있었으나 각 경로의 상태 명령은 exit 0이며 변경 출력은 없었다.

지정 이력 `nori-tc-gateway 4047d163·4f843d41`, `nori-tc-ui-backend 51181c7c`의 변경 본문과 관련 시험 기대값을 확인했다. 이력의 과거 결함 설명은 원인 탐색에만 사용하고 본문은 위 HEAD 코드로 대조했다. 특히 현재 Gateway의 실제 스펙 적재 실패는 예전 이력의 경고 후 성공 경로와 달리 컨텍스트 되돌림 후 실패로 처리된다.

작성 규칙 `docs/rag-document-writing-rules.md`와 목록 `docs/tc-rag-document-list.md` 전체를 읽었다. 겹치는 기존 문서는 설비 명령·폴링, 연결·명령 트러블슈팅, 설비 등록·설정, 모델·파라미터·JAR 갱신, 모델 상세·Flow 편집의 PLC 부분, 메시지·통신 로그, DLQ·격리·Repair Task, SECS·HSMS, Socket 문서다. 기존 문서는 수정하지 않았다.

## 절별 사실 대조

표의 인계·대조 요청은 해당 코드가 실제 소비하는 입력과 실패 분기를 사용자가 운영·모델 담당자에게 전달하도록 구성한 진단 권고다. 현장에서 그 원인이 발생했다고 단정한 문장이 아니다. 원문 한 줄에 여러 문장이 있는 경우 같은 행에서 모든 문장의 요지와 근거를 함께 대응했다.

### 설비 수정·모델 배포 뒤부터 PLC가 연결되지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 18 | 변경·연결 불가 시각을 기록하고 연결 정보 누락 여부를 인계한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/PlcRuntimeContextProvider.java @ d3636ab86763 L24~45; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimePlcConnectionSlotTest.java @ d3636ab86763 L58~81 | PlcInfo 없으면 create에서 예외. 생성·수정·모델 변경 이벤트별 슬롯 보존 시험을 읽고 변경 시점을 진단 단서로 선택했다. |
| 20 | 수정 이후 네 연결 항목 전달, 필수 연결 정보 부재 시 연결 준비 거부. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 L258~272 L391~406; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/PlcRuntimeContextProvider.java @ d3636ab86763 L31~44 | snapshot 네 값의 null 검사와 PlcInfo 생성, provider 소비를 대조했다. 4047d163 diff에서 이 전달의 추가를 확인했다. |
| 22 | 화면의 네 설정값·설비 ID를 전달하고 판본과 연결 정보 구성을 확인 요청한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 L1220~1265; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 L391~406 | 실제 렌더 라벨은 PLC Settings·PLC Protocol·Unit ID·Poll Timeout (ms)·Reconnect Max Retries. 현장 판본 확인은 운영 인계로 한정했다. |

### PLC 그룹 Start가 거부되면 연결과 그룹 중 무엇을 확인하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 33 | 운영 담당자가 확인한 결과 코드로 구별하고 완료 표시·권한 절차는 기존 문서를 참조한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L980~1005; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453 | 화면 훅이 PASS 여부와 별도로 dispatched를 반환함을 확인. 화면에서 모든 결과 코드를 볼 수 있다고 쓰지 않았다. 기존 equipment-commands-and-polling.md의 완료 표시·권한 절을 대조해 위임했다. |
| 37 | EQP_DISABLED이면 사용 허용을 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L985~987 | enabled 검사와 해당 오류 코드. |
| 38 | EQP_NOT_STARTED이면 시작 의도와 현재 메뉴를 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L989~992; nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 L44~74 | STARTED 전제와 연결·시작 의도에 따른 시작·재연결 메뉴 조건. |
| 39 | EQP_NOT_CONNECTED이면 연결을 먼저 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L993~1000 | 활성 채널 검사가 그룹 startGroup 호출보다 먼저다. |
| 40 | PLC_GROUP_NOT_FOUND이면 입력 그룹과 적용 디코드 스펙을 대조 요청한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L999~1004 | 입력 그룹 startGroup false이면 해당 코드와 decode spec 미적재 사유. |
| 42 | 입력값·설비 ID·명령 시각을 전달한다. | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L38~70 L80~113; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L517~522 | trim한 그룹과 eqpId로 Start·End 호출. placeholder는 Group messageName. 인계 필드는 이 입력을 기준으로 선택했다. |

### 폴링 명령 결과의 TIMEOUT은 PLC 응답 만료와 같은가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 52 | 명령 처리·기록의 TIMEOUT과 실제 응답의 POLLING_TIMEOUT을 구별한다. 비동기 예외도 TIMEOUT이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd L350~378; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 L79~108 | action 결과 TIMEOUT, 비동기 본문·결과 기록 예외 처리와 실제 pending 만료 처리는 별도 계층이다. |
| 54 | 수정 이후 통신 방식 조회, 정상 PASS·일반 실패 FAIL·미존재 사전 거부. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd L238~289 L394~410; nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/EqpManagementServiceTest.java @ 985d17c604dd L184~258 | requireInterfaceType를 접수 전에 호출하고 reply에 포함. PASS·FAIL·미존재 시험의 기대값을 읽었다. 51181c7c diff와 현재 코드가 대응한다. |
| 56 | 명령 입력·시각·화면 문구를 전달하고 판본·적용 결과 확인 뒤 재실행한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd L350~378; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd L337~367 | 명령 실행 후 결과 저장이 별도여서 결과 실패만으로 적용 여부를 단정할 수 없다. 실측 없이 현장 판본의 결함을 확정하지 않았다. |

### PLC가 응답하지 않아 재연결되거나 격리될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 67 | 격리 사유가 POLLING_TIMEOUT일 때 응답 만료 누적·응답 기한·기준 미만 재연결·도달 시 격리 요청을 확인한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java @ d3636ab86763 L101~115; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 L39~42 L79~135; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 L1245~1265 | 격리 요청의 TIMEOUT_REASON 값이 POLLING_TIMEOUT임을 다시 확인했다. pending deadline은 pollTimeoutMs, sweep별 실패 누적 후 활성 세션 기준과 비교. 설정 라벨과 threshold별 시험 기대값의 기존 대조를 유지했다. |
| 69 | 정상 응답 해석으로 연속 실패 초기화, 시각·설정값으로 지연과 반복 만료를 구별 요청한다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 L161~172; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 L97~108 | 응답 decode·map 성공 후 recordSuccess. 실제 지연 원인을 단정하거나 timeout 증대를 해결책으로 제시하지 않았다. |
| 71 | 격리 조회·설정 변경은 각각의 소유 문서를 참조한다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L14~30; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 L1220~1265 | 격리 열·설정 입력 렌더와 기존 dlq-quarantine-and-repair-task.md 및 equipment-registration-and-settings.md의 해당 절을 대조했다. 절차를 복제하지 않았다. |

### 모델 변경이 PLC 디코드 스펙 오류로 거부될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 81 | 같은 오류 코드에서 변환 실패와 적재 실패를 구별 요청한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L262~268 L320~328 | 두 실패 모두 EQP_PROFILE_PAYLOAD_INVALID. 메시지는 conversion failed와 reload failed로 다르다. |
| 83 | 활성 변경 전 검사, 영역·타입·순서·읽기 범위 검증, 사후 적재 실패 시 컨텍스트 되돌림·실패. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L262~328; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 L39~66; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 L38~46 | 사전 validateConvertible → upsert, enum·order 변환, wordOffset+wordSpan ≤ read count 검사. reload catch는 rollbackContextActivation 후 실패. GatewayUiRuntimePlcSpecReloadFailTest L89~142의 되돌림·사전 무변경 기대값을 읽었다. |
| 85 | 모델·버전·그룹·오류 전문을 인계하고 참조·반영 절차를 각 문서로 안내한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 L39~66; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 L38~46 | 변환이 소비하는 그룹과 레지스터 정보를 진단 단위로 선택했다. 선택 모델·버전과 배포 절차는 기존 equipment-model-parameter-and-jar-update.md의 소유 사실로 남겼다. |

### PLC 응답은 왔는데 값 해석이 실패할 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 98 | 파싱 실패 변수·워드 수·위치·길이·자료형·문자셋 대조, 지원하지 않는 문자셋의 실제 응답 시 실패. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 L32~80 L220~227; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcRegisterSpec.java @ d3636ab86763 L26~53; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 L53~66 | word window·타입 분기·Charset.forName 예외를 확인. 스펙 생성에서는 charEncoding을 검증하지 않고 보관한다. PlcInboundPipelineTest L135~150은 유효 그룹 생성 후 실제 응답 decode에서 잘못된 문자셋이 실패하는 기대값이다. |
| 100 | FRAMING_FAILED·PARSING_FAILED는 DLQ·격리를 요청하며, 프레임·Modbus 오류 응답을 대조한다. MAPPING_NOT_FOUND는 DLQ만 남기고 격리하지 않으며 그룹 스펙 적재를 확인한다. | nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 L58~86; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 L125~163 L197~203 L230~239; nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipelineTest.java @ d3636ab86763 L120~164 | 두 예외 분기의 failFrame은 publishDlq와 quarantine을 호출한다. 그룹 누락은 publishDlq 후 반환하며 격리 호출이 없다. 관련 시험의 DLQ·격리 및 격리 없음 기대값도 다시 읽었다. |
| 102 | 설비·그룹·시각·사유를 전달하고 기록·레지스터의 소유 문서를 참조한다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 L153~163 L197~222; nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L14~37 | 실패 레코드의 eqpId·시각·사유, 그룹 부재 detail, 모델 그룹 맥락을 인계 대상으로 선정했다. 조회 API는 front dlq.api.ts L39~52 → backend DlqController L81~88 L177~184도 확인했다. |

### PLC 응답이 있어도 업무 메시지가 늘지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 110 | 최초·변화 시 발행, 전 변수 동일이면 생략, 응답 수와 메시지 수 분리. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 L168~194; nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipelineTest.java @ d3636ab86763 L90~104 | 모든 변수의 shouldPublish를 합산하고 그룹 메시지 한 건을 선택한다. 동일 응답의 두 번째 결과는 빈 메시지 목록이라는 시험 기대값을 읽었다. |
| 112 | 대기 요청과 짝이 없으면 업무 메시지·DLQ·격리 없이 메트릭·로그만 남김, 동일 값 생략과 짝짓기 실패 대조 요청. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 L143~150 L175~179; nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipelineTest.java @ d3636ab86763 L107~116 | pending이 없으면 메트릭·조건부 debug 로그 후 반환하므로 메시지 추가·DLQ·격리를 호출하지 않음을 다시 확인했다. 기존 시각·요청 식별값·만료 대조 권고는 보존했다. |
| 114 | 예상 변화·마지막 정상 메시지·대상을 전달하고 화면 로그 절차를 참조한다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 L143~194; rag-documents/nori-tc/00.rag/user/message-results-and-communication-logs.md의 통신 로그 절 | 값 변화와 요청·응답 짝짓기의 진단에 필요한 비교 시점을 선정했다. 원시 Modbus 프레임이 화면 로그에 반드시 표시된다고 쓰지 않았다. |

## 매뉴얼과 코드가 다른 곳

지정 원천은 수정 커밋 세 건이며 해당 동작을 설명하는 매뉴얼 절이 지정되지 않았다. 이번 문서의 동작을 매뉴얼로 확정하지 않았고, 확인한 매뉴얼·코드 충돌은 없다.

커밋 당시와 현재 HEAD의 차이는 구별했다. 4f843d41 당시 설명의 사후 reload 실패 가드는 현재 소스에서 rollbackContextActivation과 실패 반환으로 바뀌어 있으므로 현재 동작을 그대로 썼다. 51181c7c 이전의 항상 TIMEOUT이나 4047d163 이전의 UI 이벤트 후 연결 슬롯 유실을 현재의 일반 동작으로 쓰지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 배포 판본에 4047d163·4f843d41·51181c7c 수정이 포함됐다는 단정 | 배포된 바이너리·배포 판본을 이번 읽기 전용 조사로 확인하지 않았다. | 운영 담당자 |
| 제조사별 실제 레지스터 주소·단위·배율 및 이번 현장 장애의 확정 원인 | 제조사 원본과 현장 요청·응답 자료가 제공되지 않았다. 추정 주소·설정 변경값·성공 보장을 만들지 않았다. | 설비·모델 담당자, 운영 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 소유 사실과 이 문서의 처리 |
|---|---|
| 00.rag/user/plc-modbus-register-and-type-reference.md | 영역·자료형·순서·배율·주소 규칙의 상세 참조. 본문은 실패를 가르는 항목만 제시한다. |
| 00.rag/user/equipment-commands-and-polling.md | Start·End 조작, 그룹 결과 화면의 한계, 명령·결과 조회 권한. 문구·단추 절차를 복제하지 않는다. |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 통신 방식 공통 연결·명령·확인 지연 문제. PLC 연결 슬롯·그룹·디코드 진단만 소유한다. |
| 00.rag/user/equipment-registration-and-settings.md | 수정 창 진입·저장·입력 제한·기본값. 네 설정 라벨은 진단 인계에 필요한 최소값만 사용한다. |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 모델·버전 배포·반영 절차. 디코드 스펙 실패 구별만 이 문서에 쓴다. |
| 00.rag/user/model-details-and-flow-editing.md | PLC Group·Register 편집·저장 절차. 표 입력 절차를 중복하지 않는다. |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 실패·격리 조회, 코드의 기본 설명, 권한, 해제·정리 인계. 본문에서는 진단 대조 대상만 추가한다. |
| 00.rag/user/message-results-and-communication-logs.md | 사용자 통신 로그 조회·권한·표시 한계. 폴링 응답 수와 업무 메시지 수 구별만 이 문서에서 답한다. |
| 00.rag/user/error-message-and-code-reference.md | 오류 문구·코드의 상세 참조. 이 문서는 다음 확인 대상을 가르는 데 필요한 코드만 사용한다. |

## 검사 결과

기계 검사 명령:

실행 도구: GNU Bash 5.2.37(1)-release (x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS, Windows PowerShell 5.1.26100.9444. 보조 파일·해시 대조는 Python 3.12.10으로 수행했다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/plc-troubleshooting.md
```

첫 실행: exit 1, 부모 한 절 532자로 위반 1건. 자식 최대 182자, CROSS VIOLATION·MASK 출력 없음. 그룹 거부 절 끝의 공통 연결 문서 안내를 제거해 부모 449자로 줄였다. 내용 소유권은 위 표에 보존했다. 두 번째 실행: exit 0, 위반 0건. 이후 실제 메뉴 근거와 한국어 입력 문구의 원천 메타 두 줄을 보완하고 최종 검사를 다시 실행했다.

최초 작성 당시 최종 검사: exit 0, 위반 0건, CROSS VIOLATION 0건, MASK 0건(두 표식 모두 출력 없음). 직렬화 자식 최대 182자·부모 최대 449자다. 당시 마지막 출력 10줄은 다음과 같다.

```text
    L98-98 문단 137  | PLC 응답 뒤 PARSING_FAILED가 확인되면 모델 담당자에게 실패 변수·응답 워드 수·변수 위치와 길이·자료형·문자셋을 대조하도록 요청한다. 지원하지 않는 문자셋은 모델 변환 검사를 지나도 실제 문자열 응답을 해석할 때 실패할 수 있다.
    L100-100 문단 123  | FRAMING_FAILED이면 운영 담당자에게 응답 프레임·Modbus 오류 응답 여부를 먼저 확인하도록 요청한다. MAPPING_NOT_FOUND이면 응답과 짝지어진 그룹의 디코드 스펙 적재 여부를 확인하도록 요청한다.
    L102-102 문단 156  | 설비 ID·그룹 값·실패 시각과 사유 전문을 함께 전달한다. 기록 조회는 00.rag/user/dlq-quarantine-and-repair-task.md, 레지스터 해석은 00.rag/user/plc-modbus-register-and-type-reference.md를 따른다.
§ L104-114 [27] PLC 응답이 있어도 업무 메시지가 늘지 않을 때   parent=353 children=3 sources=2
    L110-110 문단 100  | PLC 그룹은 최초 값이거나 변수 하나라도 달라질 때 업무 메시지를 만든다. 모든 변수가 직전 값과 같으면 발행을 생략한다. 응답 수와 업무 메시지 수를 같은 것으로 세지 않는다.
    L112-112 문단 137  | 응답에 대응하는 대기 요청을 찾지 못해도 업무 메시지를 만들지 않는다. 운영 담당자에게 동일 값으로 인한 발행 생략인지, 응답과 요청의 짝짓기 실패인지 확인하도록 요청한다. 응답 도착 시각·요청 식별값·대기 만료 여부를 함께 대조하도록 요청한다.
    L114-114 문단 114  | 설비 ID·그룹 값·값이 바뀌었다고 예상한 시각·마지막 정상 메시지를 전달한다. 화면 로그 확인은 00.rag/user/message-results-and-communication-logs.md를 따른다.
SUMMARY sections=7 children=24 maxChild=182 maxParent=449
CORPUS docs=31/53 sections=466 part={admin=7, user=24} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=7}
TOTAL files=31 targets=1 violations=0
```

최초 작성 당시 사람 검사: 7개 절을 모두 다시 읽었다. 원문 본문 20개 문단·표 자료 4행을 원천과 대조했다. 아래 네 판정은 각 절마다 수행했다. 이후 메인 검증에서 발견한 격리 결과 누락과 보완 검사는 아래 보완 이력에 구별해 기록한다.

| 절 | 제목으로 질문 식별 | 원천과 일치 | 절만 읽어 독립 답 | 다른 절·기존 문서와 소유권 구별 |
|---|---|---|---|---|
| 수정·배포 후 연결 불가 | 적합 | 연결 슬롯·provider·회귀 기대값 대조 | 시점·설정·판본 인계 포함 | 공통 연결 안내와 분리 |
| 그룹 Start 거부 | 적합 | 실제 거부 검사 순서 대조 | 코드별 다음 확인 대상 포함 | 조작·권한·완료 문구는 기존 문서 |
| 명령 TIMEOUT | 적합 | 비동기 결과·시험 기대값 대조 | 실제 응답 만료와 구별하고 판본 인계 | 일반 확인 지연 절차는 복제하지 않음 |
| 응답 만료·격리 | 적합 | deadline·실패 누적·정상 초기화 대조 | 재연결·격리 구별과 인계 포함 | 설정 기본값·조회 절차는 기존 문서 |
| 모델 디코드 스펙 오류 | 적합 | 사전 변환과 현재 사후 되돌림 대조 | 단계 구별·모델 담당자 인계 포함 | 배포 절차·자료형 참조는 기존 문서 |
| 수신 값 해석 실패 | 적합 | 값 해석·문자셋·Modbus 오류·그룹 조회 대조 | 실패 변수와 원시 응답 대조 요청 포함 | 코드 뜻풀이 대신 다음 확인 경로 |
| 응답 뒤 메시지 미증가 | 적합 | 동일 값 억제·짝짓기 실패 대조 | 둘을 구별할 시점·대상 포함 | 화면 로그 조회 절차와 분리 |

제목 정확 중복 0건. 실제 질문을 제목으로 썼으며 반복 틀 제목은 없다. 본문 복제·빈 틀 문장·이름 토막 뜻풀이·주어 소실·오답 인용은 발견하지 않았다. user 본문에 admin objectKey·관리 문서 열람 지시·직접 클러스터/DB/설정 파일 조작을 넣지 않았다. 현장 수정 적용을 단정하지 않았다.

보조 대조에서 본문 자식 24개와 사실 대조표의 줄 번호 24개가 전부 일치했다. 첫 보조 스크립트는 표 머리글 L35를 자식으로 잘못 세어 중단됐다. 표 자료 행만 고르는 조건으로 고친 뒤 24/24와 exit 0을 확인했다. 지정 검사기는 수정하지 않았다. 최종 원천 가드도 본문 인용 파일 22개(15·5·2)의 현재 HEAD·깨끗한 작업 트리를 재확인했다. 두 산출물의 UTF-8·BOM 없음·LF·NFC·NUL 없음을 확인했다.

제품 시험은 실행하지 않았다. 문서만 작성했으므로 기존 제품 시험의 기대값은 읽기 대조 근거이며 이번 실행 PASS로 보고하지 않는다. 지정 검사기 실행은 3회(최초 위반·수정 후 통과·원천 메타 최종 편집 후 통과)다. 독립 Agent·외부 AI 호출 0회, 네트워크·서버 접속 0회. 전체 wall time·provider 사용량·비용은 별도 측정하지 않아 미확인이다.

검사용 임시 파일·디렉터리·브라우저·서비스를 만들지 않았다. 지정 work 경로에 세션 소유 잔여 파일이 없으며 삭제 대상이 없다. 다른 문서·제품 소스·설정·SPEC·목록·검사기는 변경하지 않았다.

## 2026-11-19 메인 검증 결함 1건 보완

- 결함과 원인: 최초 문서는 프레임·값 변환 실패의 다음 확인 대상만 적고 DLQ·격리 결과를 누락했다. 응답 만료 절에만 격리 결과가 있어 PLC 격리를 POLLING_TIMEOUT으로 한정해 읽을 수 있었다. 최초 자체 검사의 이 누락은 메인 검증에서 발견됐다.
- 수정 범위: 원문 L67을 사유 코드 POLLING_TIMEOUT 조건으로 한정했다. L100에 FRAMING_FAILED·PARSING_FAILED의 DLQ·격리 요청과 MAPPING_NOT_FOUND의 DLQ만 발행·격리 없음 차이를 넣었다. L112에 요청과 짝짓지 못한 응답은 메시지·DLQ·격리 없이 관측만 한다는 직접 영향도 반영했다. 해당 원천 메타 L61·L89·L95와 사실 대조표 세 행을 갱신했다. 다른 원문 줄과 7개 절 구성은 보존했다.
- 원천 재확인: nori-tc-gateway HEAD d3636ab86763, PlcInboundPipeline.java·PlcPollTimeoutHandler.java·PlcInboundPipelineTest.java의 경로별 상태는 출력 없음·exit 0이었다. PlcInboundPipeline L125~158·L197~203·L230~239와 PlcPollTimeoutHandler L42·L79~118을 직접 다시 읽었다. 프레임·값 변환·매핑 누락 시험 기대값 L120~164도 다시 읽었다. 제품 시험을 실행한 결과로 보고하지 않는다.
- 판본: 보완 전 원문 SHA-256은 `9d0201bf43ca4a7bb71eaa68843fef772a8c218571af61f9a1f455c5d79e2ccb`, 보완 후는 `fa9c73510f3b2ae3d6acfc3f3acc7ed5ffe167b9bc0cefae73470725526f548f`다. 최종 원문은 114줄·7절·자식 24개로 유지됐다.
- 사람 검사: 최종 dump의 7절을 모두 읽었다. 사유별 결과는 응답 만료 절·수신 해석 절·짝짓기 절에서 각각 한 번만 소유한다. 변경 문장·원천·독립 답·중복·권한 경계를 다시 대조했고 미해소 지적은 없다. 격리 요청 자체의 성공을 보장하는 문장도 넣지 않았다.
- 이번 검사: 지정 명령을 3회 실행했다. 첫 보완안은 응답 해석 절 453자로 exit 1이었다. 중복 수식어를 줄여 447자로 맞춘 뒤 exit 0을 확인했다. 사유 코드 상수의 원천 줄 범위를 보완한 마지막 편집 뒤 다시 exit 0·위반 0건을 확인했다. CROSS VIOLATION·MASK 출력은 없으며 직렬화 전후 값이 유지된다. 전체 자식 최대 182자·부모 최대 449자다.
- 실행 범위: 두 Markdown만 수정했다. 별도 Agent·외부 AI·서버 호출·제품 시험·커밋·업로드·임시 파일 생성은 없다. 전체 wall time·provider 사용량·비용은 별도 계측하지 않아 미확인이다.

보완 최종 검사 명령은 위와 같으며, 마지막 출력 10줄은 다음과 같다.

```text
    L98-98 문단 137  | PLC 응답 뒤 PARSING_FAILED가 확인되면 모델 담당자에게 실패 변수·응답 워드 수·변수 위치와 길이·자료형·문자셋을 대조하도록 요청한다. 지원하지 않는 문자셋은 모델 변환 검사를 지나도 실제 문자열 응답을 해석할 때 실패할 수 있다.
    L100-100 문단 152  | FRAMING_FAILED·PARSING_FAILED는 DLQ 발행과 격리를 요청한다. FRAMING_FAILED이면 프레임·Modbus 오류 응답을 운영 담당자에게 확인한다. MAPPING_NOT_FOUND는 DLQ만 남기고 격리하지 않으며, 그룹 스펙 적재를 확인한다.
    L102-102 문단 156  | 설비 ID·그룹 값·실패 시각과 사유 전문을 함께 전달한다. 기록 조회는 00.rag/user/dlq-quarantine-and-repair-task.md, 레지스터 해석은 00.rag/user/plc-modbus-register-and-type-reference.md를 따른다.
§ L104-114 [27] PLC 응답이 있어도 업무 메시지가 늘지 않을 때   parent=366 children=3 sources=2
    L110-110 문단 100  | PLC 그룹은 최초 값이거나 변수 하나라도 달라질 때 업무 메시지를 만든다. 모든 변수가 직전 값과 같으면 발행을 생략한다. 응답 수와 업무 메시지 수를 같은 것으로 세지 않는다.
    L112-112 문단 150  | 응답에 대응하는 대기 요청을 찾지 못하면 업무 메시지·DLQ·격리 없이 메트릭·로그만 남긴다. 운영 담당자에게 동일 값으로 인한 발행 생략인지, 응답과 요청의 짝짓기 실패인지 확인하도록 요청한다. 응답 도착 시각·요청 식별값·대기 만료 여부를 함께 대조하도록 요청한다.
    L114-114 문단 114  | 설비 ID·그룹 값·값이 바뀌었다고 예상한 시각·마지막 정상 메시지를 전달한다. 화면 로그 확인은 00.rag/user/message-results-and-communication-logs.md를 따른다.
SUMMARY sections=7 children=24 maxChild=182 maxParent=449
CORPUS docs=32/53 sections=471 part={admin=7, user=25} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=8}
TOTAL files=32 targets=1 violations=0
```

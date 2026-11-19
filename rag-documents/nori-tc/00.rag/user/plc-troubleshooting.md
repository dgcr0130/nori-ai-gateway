# PLC 연결·폴링 TIMEOUT·디코드 문제 구별
```rag-meta
유형: 트러블슈팅
적용: nori-tc-gateway d3636ab86763·nori-tc-ui-backend 985d17c604dd·nori-tc-ui-front 9fe69a75dcd3 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/equipment-commands-and-polling.md
```

## 설비 수정·모델 배포 뒤부터 PLC가 연결되지 않을 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 ; 연결 정보 구성 L258~272 L391~406
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/PlcRuntimeContextProvider.java @ d3636ab86763 ; 연결 정보 검사 L24~45
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimePlcConnectionSlotTest.java @ d3636ab86763 ; 생성·수정·모델 변경 기대값 L58~81
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; PLC 설정 표시 L1220~1265
```

PLC가 설비 수정·모델 배포 뒤부터 연결되지 않으면 변경 시각과 연결 불가가 시작된 시각을 나란히 기록한다. 운영 담당자에게 PLC 연결 정보 누락으로 연결 준비가 실패했는지 확인을 요청한다.

Gateway 4047d163 수정 이후에는 설비 생성·수정·모델 변경 때 PLC 프로토콜·Unit ID·응답 제한시간·재연결 기준을 연결 정보에 담는다. 필수 연결 정보가 없으면 연결 준비를 거부한다.

설비 ID와 수정 창 「PLC Settings」의 「PLC Protocol」·「Unit ID」·「Poll Timeout (ms)」·「Reconnect Max Retries」를 전달한다. 운영 담당자에게 현장 Gateway 판본의 수정 포함 여부와 실제 연결 정보 구성을 대조하도록 요청한다.

## PLC 그룹 Start가 거부되면 연결과 그룹 중 무엇을 확인하는가
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 그룹 시작 검사 L980~1005
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 그룹 입력·명령 L38~70 L80~113
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 화면에 전달하는 결과 L424~453
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; 상태별 명령 메뉴 L44~74
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 입력 문구 L517~522
```

PLC 그룹 시작이 거부되면 운영 담당자가 확인한 명령 결과 코드로 다음 확인 대상을 나눈다. 화면의 완료 문구 처리와 명령·결과 조회 권한은 `00.rag/user/equipment-commands-and-polling.md`를 따른다.

| 결과 코드 | 다음 확인 대상 |
|---|---|
| EQP_DISABLED | 설비 사용 허용 여부를 운영 담당자에게 확인한다. |
| EQP_NOT_STARTED | 설비 시작 의도와 현재 시작·재연결 메뉴를 확인한다. |
| EQP_NOT_CONNECTED | 그룹 정의보다 PLC 연결 성립 여부를 먼저 확인한다. |
| PLC_GROUP_NOT_FOUND | 입력한 그룹 값과 적용 모델의 그룹 디코드 스펙을 모델 담당자에게 대조 요청한다. |

「Group messageName」 입력값·설비 ID·Start 시각을 함께 보낸다.

## 폴링 명령 결과의 TIMEOUT은 PLC 응답 만료와 같은가
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 그룹 통신 방식 조회 L238~289
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementService.java @ 985d17c604dd ; 결과 확정·예외 처리 L350~378 L394~410
원천: nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/EqpManagementServiceTest.java @ 985d17c604dd ; 성공·실패·미존재 기대값 L184~258
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 실제 응답 만료 L79~108
```

PLC 그룹 Start·End 명령의 TIMEOUT이면 운영 담당자에게 명령 처리·결과 기록 실패인지, 실제 PLC 읽기 응답 만료인 POLLING_TIMEOUT인지 구별하도록 요청한다. 명령 결과는 비동기 처리 예외로도 TIMEOUT이 될 수 있다.

UI Backend 51181c7c 수정 이후에는 설비 정보에서 통신 방식을 읽어 그룹 명령 결과를 만든다. 정상 명령은 PASS, 일반 실패는 사유를 담은 FAIL로 기록한다. 없는 설비는 비동기 접수 전에 거부한다.

설비 ID·그룹 값·Start 또는 End·발생 시각·화면 문구를 전달하고, 현장 UI Backend 판본의 수정 포함 여부와 실제 명령 적용 결과를 확인한 뒤 재실행한다.

## PLC가 응답하지 않아 재연결되거나 격리될 때
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java @ d3636ab86763 ; 응답 기한·송신 L101~130
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 만료·재연결·격리 L39~42 L79~135
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/test/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandlerTest.java @ d3636ab86763 ; 재연결·격리 기대값 L76~108
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 정상 응답 뒤 실패 누적 해제 L161~166
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 응답 제한·재연결 입력 L1245~1265
```

격리 기록의 사유 코드가 POLLING_TIMEOUT이면 응답 만료 누적을 확인한다. 응답 기한은 「Poll Timeout (ms)」를 사용한다. 응답 대기가 만료되면 연속 실패를 누적한다. 「Reconnect Max Retries」기준 미만이면 연결을 끊긴 상태로 바꾸고 재연결을 요청하며, 기준에 도달하면 격리를 요청한다.

정상 응답을 해석하면 연속 실패 누적을 초기화한다. 운영 담당자에게 마지막 정상 응답·만료·재연결 시각과 두 설정값을 전달해, 응답이 늦게 왔는지와 재연결 뒤에도 만료가 이어졌는지 확인하도록 요청한다.

격리 기록 조회는 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다. 설정 변경 절차는 `00.rag/user/equipment-registration-and-settings.md`를 따른다.

## 모델 변경이 PLC 디코드 스펙 오류로 거부될 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 변환 검증·적재 실패 처리 L262~328
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 ; 그룹·레지스터 변환 L39~66
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcGroupSpec.java @ d3636ab86763 ; 읽기 범위 검사 L38~46
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimePlcSpecReloadFailTest.java @ d3636ab86763 ; 거부·되돌림 기대값 L89~142
```

PLC 모델 변경에서 EQP_PROFILE_PAYLOAD_INVALID가 확인되면 운영 담당자에게 디코드 스펙 변환 실패와 실제 스펙 적재 실패 중 어느 단계인지 확인하도록 요청한다.

Gateway 4f843d41 수정 이후에는 활성 정보 변경 전에 변환을 검사한다. 영역·자료형·바이트/워드 순서가 변환 가능한지, 레지스터가 그룹의 읽기 범위 안인지 확인한다. 현재 소스는 검증 후 실제 적재가 실패해도 활성 컨텍스트를 되돌리고 실패로 처리한다.

모델 담당자에게 대상 설비·선택 모델과 버전·그룹 값·오류 전문·발생 시각을 전달한다. 값의 참조는 `00.rag/user/plc-modbus-register-and-type-reference.md`, 모델 반영 절차는 `00.rag/user/equipment-model-parameter-and-jar-update.md`를 따른다.

## PLC 응답은 왔는데 값 해석이 실패할 때
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 응답 실패·DLQ·격리 분기 L125~163 L197~203 L230~239
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 범위·자료형 해석 L32~80
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcValueDecoder.java @ d3636ab86763 ; 문자셋 검사 L220~227
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/decode/PlcRegisterSpec.java @ d3636ab86763 ; 스펙 생성·문자셋 보관 L26~53
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcContextSpecMapper.java @ d3636ab86763 ; 문자열 설정 전달 L53~66
원천: nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 ; 프레임·오류 응답 검사 L58~86
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipelineTest.java @ d3636ab86763 ; 프레임·값 변환·매핑 누락 기대값 L120~164
```

PLC 응답 뒤 PARSING_FAILED가 확인되면 모델 담당자에게 실패 변수·응답 워드 수·변수 위치와 길이·자료형·문자셋을 대조하도록 요청한다. 지원하지 않는 문자셋은 모델 변환 검사를 지나도 실제 문자열 응답을 해석할 때 실패할 수 있다.

FRAMING_FAILED·PARSING_FAILED는 DLQ 발행과 격리를 요청한다. FRAMING_FAILED이면 프레임·Modbus 오류 응답을 운영 담당자에게 확인한다. MAPPING_NOT_FOUND는 DLQ만 남기고 격리하지 않으며, 그룹 스펙 적재를 확인한다.

설비 ID·그룹 값·실패 시각과 사유 전문을 함께 전달한다. 기록 조회는 `00.rag/user/dlq-quarantine-and-repair-task.md`, 레지스터 해석은 `00.rag/user/plc-modbus-register-and-type-reference.md`를 따른다.

## PLC 응답이 있어도 업무 메시지가 늘지 않을 때
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 응답 짝짓기·변화 판정 L143~194
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/test/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipelineTest.java @ d3636ab86763 ; 동일 값·짝짓기 실패 기대값 L90~116
```

PLC 그룹은 최초 값이거나 변수 하나라도 달라질 때 업무 메시지를 만든다. 모든 변수가 직전 값과 같으면 발행을 생략한다. 응답 수와 업무 메시지 수를 같은 것으로 세지 않는다.

응답에 대응하는 대기 요청을 찾지 못하면 업무 메시지·DLQ·격리 없이 메트릭·로그만 남긴다. 운영 담당자에게 동일 값으로 인한 발행 생략인지, 응답과 요청의 짝짓기 실패인지 확인하도록 요청한다. 응답 도착 시각·요청 식별값·대기 만료 여부를 함께 대조하도록 요청한다.

설비 ID·그룹 값·값이 바뀌었다고 예상한 시각·마지막 정상 메시지를 전달한다. 화면 로그 확인은 `00.rag/user/message-results-and-communication-logs.md`를 따른다.

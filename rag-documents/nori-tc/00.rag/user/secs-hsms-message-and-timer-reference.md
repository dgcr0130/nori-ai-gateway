# SECS·HSMS 메시지·타이머
```rag-meta
유형: 참조
적용: nori-tc-gateway d3636ab86763 및 HsmsRuntimeContextProvider.java a5e5221853b7·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/secs-hsms-troubleshooting.md
```

## SxFy에서 Stream과 Function을 읽는 법
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/Secs2Message.java @ d3636ab86763 ; 생성자·messageName L21~32
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/BasicSecs2Decoder.java @ d3636ab86763 ; decode L30~46
```

Gateway의 SECS 메시지명은 `S{stream}F{function}`으로 만든다. 예를 들어 S6F11은 Stream 6·Function 11이다.

| 항목 | 허용 범위 | 메시지명에서의 자리 |
|---|---|---|
| Stream | 0~127 | S 뒤의 숫자 |
| Function | 0~255 | F 뒤의 숫자 |

기본 디코더는 Stream·Function·W-bit를 읽고 본문은 rawBody 바이트로 유지한다. 본문의 SECS-II 항목 구조는 이 디코더가 해석하지 않는다.

## W-bit와 systemBytes가 나타내는 것
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; W-bit·systemBytes 직렬화 L9~19
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; toBytes·parse L39~74
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 대기 해제 L141~179
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; 송신 헤더 생성 L89~99
```

| 항목 | Gateway에서의 의미 |
|---|---|
| W-bit | 응답을 기대하는 표시이다. Stream 바이트의 최상위 비트 0x80에 담고 Stream 숫자에서는 제외한다. |
| systemBytes | 헤더의 4바이트 상관값이다. 응답 대기는 수신 프레임의 같은 systemBytes로 해제한다. |

확인한 업무 명령 송신 인코더는 W-bit를 false로 고정한다. 수신 W-bit 보존과 송신 W-bit 설정은 구별한다.

## HSMS 프레임의 길이와 헤더 값
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsFrame.java @ d3636ab86763 ; 길이 검사·DATA 판별 L6~36
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; 범위·헤더 배치 L12~51
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsPType.java @ d3636ab86763 ; SECS_II L22~22
```

| 필드 | 값·단위 | 의미 |
|---|---|---|
| length | 10 + 본문 바이트 수 | 10바이트 헤더와 본문 길이이다. 앞의 4바이트 length 필드는 포함하지 않는다. |
| deviceId | 0~65535 | 헤더 앞 2바이트에 담는 Device ID이다. |
| pType | SECS_II = 0x00 | Gateway는 SType이 DATA이고 pType이 SECS_II인 프레임을 SECS-II 데이터로 판별한다. |
| sType | HsmsSType 코드 | 데이터와 제어 메시지 종류를 구별하는 헤더 값이다. |

## DATA와 SELECT의 SType 코드
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L12~20
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT·DATA 처리 L133~184
```

| 메시지 | SType 값 | 세션에서 수신했을 때 |
|---|---|---|
| DATA | 0 | SECS 데이터 처리 여부를 판정한다. SELECT 선행 조건이 켜져 있으면 SELECTED에서만 허용한다. |
| SELECT_REQ | 1 | SELECTED로 바꾸고 SELECT_RSP를 만든다. |
| SELECT_RSP | 2 | 같은 systemBytes의 제어 응답 대기를 지우고 SELECTED로 바꾼다. |

## DESELECT와 SEPARATE의 SType 코드
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L15~20
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 해제 처리 L148~174
```

| 메시지 | SType 값 | 세션에서 수신했을 때 |
|---|---|---|
| DESELECT_REQ | 3 | NOT_SELECTED로 바꾸고 DESELECT_RSP를 만든다. |
| DESELECT_RSP | 4 | 같은 systemBytes의 제어 응답 대기를 지우고 NOT_SELECTED로 바꾼다. |
| SEPARATE_REQ | 9 | NOT_SELECTED로 바꾸며 응답 프레임을 만들지 않는다. |

## LINKTEST와 REJECT의 SType 코드
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드·미정의 값 거절 L12~59
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; LINKTEST·REJECT 처리 L160~189
```

| 메시지 | SType 값 | 세션에서 수신했을 때 |
|---|---|---|
| LINKTEST_REQ | 5 | 연결 확인 요청에 LINKTEST_RSP를 만들고 선택 상태는 유지한다. |
| LINKTEST_RSP | 6 | 같은 systemBytes의 제어 응답 대기를 지우고 선택 상태는 유지한다. |
| REJECT_REQ | 7 | 데이터 처리를 허용하지 않으며 선택 상태는 유지한다. 별도 거절 사유 해석은 하지 않는다. |

정의되지 않은 SType 코드는 헤더 해석 중 오류로 거절한다.

## Gateway가 만드는 제어 프레임의 공통값
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsControlFrameFactory.java @ d3636ab86763 ; SELECT·DESELECT·LINKTEST 생성 L32~125
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 systemBytes 전달 L133~162
```

Gateway가 생성하는 SELECT_REQ·SELECT_RSP·DESELECT_RSP·LINKTEST_REQ·LINKTEST_RSP는 본문이 없고 length가 10이다. Stream·Function은 0, W-bit는 false, pType은 SECS_II이다.

제어 응답은 요청의 systemBytes를 그대로 사용한다.

## 타이머 기본값은 어떤 설정에서 오는가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 필수값 검사·설정 전달 L85~125
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; toSessionDefaults L293~325
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; HSMS 기본 설정 L45~54
```

이 문서의 T3·T5·T6·T7·T8·LINKTEST 기본값은 저장소에 제공된 Gateway 설정값이며 시간 단위는 ms이다.

타이머 다섯 값과 LINKTEST 주기는 모두 0보다 커야 한다. 설정 바인딩 클래스는 이 값의 누락을 기동 시 거부한다.

화면 입력과 저장 검증은 `00.rag/user/equipment-registration-and-settings.md`의 「SECS Settings의 입력 범위 확인하기」에서 다룬다.

## 설비 화면의 HSMS 값은 세션에 적용되는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325
```

설비별 HSMS 설정 적용 수정이 들어간 판본에서는 「SECS Settings」에 저장한 「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」을 세션 타이머로 쓴다. 저장 단위 초를 ms로 바꾼다.

「Link Test Enabled」·「Link Test Interval」·「Max Message Bytes」도 세션에 반영한다. Link Test Interval은 초를 ms로 바꾼다.

설비 HSMS 설정이 없으면 Gateway 기본값을 쓴다. 「Device ID」는 설비 값이 없을 때 Gateway 설정값을 쓴다.

잘못된 설정은 설비 ID·설정값이 담긴 오류로 실패한다.

화면 값과 실제 동작 시간이 다르면 운영 담당자에게 설비 ID·화면 입력값·관측한 만료 시점을 전달하고 Gateway 설정 확인을 요청한다.

## T3 데이터 응답 대기의 값과 등록 조건
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T3 L46~46
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; DATA 대기 해제·등록 L176~184
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; registerOutboundDataReplyExpectation L240~250
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; encode·헤더 생성 L29~99
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; T3 기대값 L137~168
```

| 설정 키 | 제공 기본값 |
|---|---|
| tc.gateway.hsms.timer.t3-reply-timeout-ms | 45000 ms |

T3는 상태 기계에 데이터 응답 대기를 등록한 시점부터 잰다. 같은 systemBytes의 DATA를 받으면 대기를 해제한다.

기준 소스에서 등록 메서드의 호출은 시험 코드에만 있다. 업무 명령 송신 경로는 T3 대기를 자동 등록하지 않는다.

## T5 설정값의 현재 사용 범위
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T5 L47~47
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; T5 전달 L56~86
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/config/HsmsTimerConfig.java @ d3636ab86763 ; T5 보관·검사 L14~26
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; scheduleReconnect L354~399
```

| 설정 키 | 제공 기본값 |
|---|---|
| tc.gateway.hsms.timer.t5-connect-timeout-ms | 10000 ms |

기준 소스의 T5는 설정 객체로 전달해 보관한다. T5 값으로 연결 대기나 재연결 간격을 계산하는 제품 사용처는 없다.

ACTIVE 재연결 예약은 별도의 Netty 재연결 지연 설정을 사용한다. T5 값을 재연결 간격으로 해석하지 않는다.

## T6 제어 요청의 응답 대기
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T6 L48~48
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT 대기 등록 L233~237
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 만료·LINKTEST 대기 등록 L264~342
```

| 설정 키 | 제공 기본값 |
|---|---|
| tc.gateway.hsms.timer.t6-control-timeout-ms | 5000 ms |

T6는 Gateway가 SELECT_REQ 또는 주기 LINKTEST_REQ를 만들며 등록하는 제어 응답 대기 시간이다. 등록 시각에 T6 값을 더한 기한까지 대기가 남아 있으면 만료로 판정한다.

## T7 선택되지 않은 연결의 대기
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T7 L49~49
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 연결 기준점 L107~115
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNotSelectedTimeout L283~292
```

| 설정 키 | 제공 기본값 |
|---|---|
| tc.gateway.hsms.timer.t7-not-selected-timeout-ms | 10000 ms |

T7는 연결 개시 시각을 기준으로 잰다. 세션이 SELECTED이면 만료를 판정하지 않는다. SELECTED가 아닌 상태에서 연결 개시 후 T7 이상 지났으면 만료를 보고하고 기준점을 지운다.

## T8 미완성 프레임의 대기
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T8 L50~50
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNetworkInterleaveTimeout L298~315
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 버퍼 잔량 전달 L63~68
```

| 설정 키 | 제공 기본값 |
|---|---|
| tc.gateway.hsms.timer.t8-network-interleave-timeout-ms | 5000 ms |

T8는 타이머 점검에서 버퍼에 남은 미완성 프레임을 처음 관측한 시각부터 잰다. 미완성 상태가 T8 이상 계속되면 만료를 보고한다. 미완성 프레임이 없다고 관측하면 기준점을 지운다.

## LINKTEST 사용 여부와 송신 주기
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; LINKTEST L52~54
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; appendLinktestRequest L321~342
```

| 설정 키 | 제공 기본값 | 역할 |
|---|---|---|
| tc.gateway.hsms.linktest-enabled | true | 주기 LINKTEST_REQ 생성 여부 |
| tc.gateway.hsms.linktest-interval-ms | 30000 ms | LINKTEST_REQ 생성 간격 |

주기 송신은 사용 설정이 켜져 있고 SELECTED인 세션에서 동작한다. 첫 점검은 기준 시각만 잡고, 이후 주기가 지나면 요청을 만든다. 마지막 데이터 수신 시각으로 주기를 재설정하지 않는다.

## 타이머 점검 주기와 증상 확인 경로
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; sessionTimerTickMs L68~74
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/GatewayHsmsRuntimeConfiguration.java @ d3636ab86763 ; 스케줄러 설정 L68~74
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerScheduler.java @ d3636ab86763 ; startSession L64~72
```

| 설정 키 | 코드 기본값 | 역할 |
|---|---|---|
| tc.gateway.hsms.session-timer-tick-ms | 1000 ms | 데이터 수신과 독립적으로 타이머를 점검하는 간격 |

타이머 점검은 고정 지연 방식으로 반복하므로 설정된 제한 시각과 실제 만료 감지 시각은 구별한다.

SELECT·S1F2 식별·대기 초과의 증상 확인은 `00.rag/user/secs-hsms-troubleshooting.md`, 모델 편집 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다.

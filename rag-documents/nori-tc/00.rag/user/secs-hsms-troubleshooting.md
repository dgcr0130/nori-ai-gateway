# SECS·HSMS 세션·SELECT·설비 식별 문제 해결
```rag-meta
유형: 트러블슈팅
적용: nori-tc-gateway d3636ab86763·nori-tc-ui-front 9fe69a75dcd3 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/secs-hsms-message-and-timer-reference.md
```

## TCP 연결 뒤에도 SECS 데이터가 처리되지 않을 때
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; onInboundFrame L133~184
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/pipeline/HsmsInboundPipeline.java @ d3636ab86763 ; drain L94~109
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 통신 정보 표시 L120~138
```

SECS 데이터가 처리되지 않으면 설비 정보 표의 「Comm Interface」가 SECS인지, 「Comm Mode」가 ACTIVE인지 PASSIVE인지 먼저 기록한다.

SELECT 선행 조건이 켜진 연결에서는 HSMS 세션이 SELECTED여야 수신 DATA가 처리된다. 운영 담당자에게 설비 ID·발생 시각·화면의 연결 표시를 전달하고, 같은 연결에서 SELECT가 완료됐는지 확인을 요청한다.

연결 자체의 실패나 명령 결과 문제는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`에서 구별한다.

## ACTIVE 설비에서 SELECT 응답을 기다리다 끊길 때
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 ; onConnected L61~89
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT 응답 처리 L141~145
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; createSelectReq L233~237
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; SELECT 응답·무응답 기대값 L46~78
```

ACTIVE에서는 Gateway가 연결 통지 후 SELECT_REQ를 보내고 T6 응답 대기를 등록한다. 운영 담당자에게 SELECT_REQ 송신 유무부터 확인하도록 요청한다.

송신 기록이 있으면 SELECT_RSP 수신 시각과 systemBytes를 요청과 대조하도록 요청한다. 같은 systemBytes의 응답은 대기를 해제하며, 대기가 만료되면 연결 종료 대상이 된다.

SELECT_REQ가 없으면 설비 ID·연결 시각·ACTIVE 표시를 전달해 세션 개시 처리와 배포 판본을 확인하도록 요청한다.

## PASSIVE 접속이 설비 ID 확인 단계에서 끝날 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 ; tryIdentify·ASCII 해석·식별값 추출 L117~229
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 식별 키 기본값 L59~66
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 식별 대기·종료 L519~540
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; scheduleBindTimeout L703~725
```

PASSIVE HSMS의 설비 식별은 S1F2 DATA 본문의 SECS-II ASCII 항목에서 설정된 키의 값을 찾는다. 기본 키는 EQPID이며 `EQPID=설비ID` 형태를 읽는다.

운영 담당자에게 S1F2 수신 여부, ASCII 항목 구조, 식별 키·값과 등록 설비 ID의 일치를 차례로 확인하도록 요청한다. 다른 메시지, 키 누락, 깨진 항목 구조는 식별 성공으로 이어지지 않는다. Device ID나 접속 포트로 설비 ID를 대신 식별하지 않는다.

식별이 끝나지 않은 채 제한 시간이 지나면 연결을 닫는다. 예상 설비 ID·접속 시각·PASSIVE 표시를 전달하고 BIND_TIMEOUT 발생 여부를 확인하도록 요청한다.

## S1F2에서 설비 ID를 읽었는데 연결이 거부될 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 ; bindInternal L235~303
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 바인딩 결과 처리 L594~614
```

PASSIVE HSMS에서 식별값을 읽은 뒤에도 설비 등록 정보 부재, 비활성 설비, 통신 방식·연결 모드 불일치, 소유권 문제, 중복 연결로 연결이 거부될 수 있다. 식별 성공과 설비에 연결을 배정하는 성공은 별개다.

운영 담당자에게 S1F2에서 읽은 설비 ID·기대 설비 ID·발생 시각을 함께 전달하고, 식별 실패인지 식별 후 연결 거부인지 먼저 구별하도록 요청한다. 후자이면 등록값이 SECS·PASSIVE인지와 연결 거부 결과를 확인하도록 요청한다.

화면 설정 조회·변경 절차는 `00.rag/user/equipment-registration-and-settings.md`에서 확인한다.

## PASSIVE 설비가 SELECT를 보냈는데도 진행되지 않을 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 ; 식별 전 control 소비 L93~122
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractorTest.java @ d3636ab86763 ; SELECT_REQ 소비 기대값 L85~95
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 ; PASSIVE 분기 L77~88
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT_REQ 처리 L133~138
```

PASSIVE에서 Gateway는 연결 통지 때 SELECT_REQ를 먼저 보내지 않는다. 설비가 보낸 SELECT_REQ를 세션에서 처리하면 SELECT_RSP를 만들고 SELECTED로 바뀐다.

설비 ID를 아직 모르는 식별 단계에서 받은 SELECT_REQ는 소비된다. 이때의 SELECT 수신과 설비 연결 배정 뒤의 SELECT 처리는 구별해야 한다.

운영 담당자에게 설비 ID·발생 시각을 전달하고 S1F2 식별, 연결 배정 성공, SELECT_REQ 수신의 선후 관계를 확인하도록 요청한다. SELECT가 식별 전에만 왔다면 설비 담당자와 식별·SELECT 순서를 대조하도록 요청한다.

## HSMS 대기 초과와 연결 종료의 원인을 가를 때
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsTimerKind.java @ d3636ab86763 ; 종료 여부 L10~22
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 만료 기록·채널 종료 L63~93
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; T7·T8·T3 기대값 L80~151
```

연결이 끊기거나 응답이 늦으면 운영 담당자에게 HSMS 만료 기록의 timer·systemBytes·elapsedMs와 발생 시각을 확인하도록 요청한다.

| 확인된 만료 | 다음 구별 경로 |
|---|---|
| T3_REPLY | 데이터 응답 대기를 확인한다. 이 만료만으로 연결을 닫지 않는다. |
| T6_CONTROL | SELECT 또는 LINKTEST 등의 요청과 응답을 대조한다. 연결 종료 대상이다. |
| T7_NOT_SELECTED | 연결 뒤 SELECT 처리 여부를 확인한다. 연결 종료 대상이다. |
| T8_NETWORK_INTERLEAVE | 미완성 프레임이 남은 경위를 확인한다. 연결 종료 대상이다. |

타이머 값의 단위·의미 참조는 `00.rag/user/secs-hsms-message-and-timer-reference.md`에서 확인한다.

## 데이터가 없는 동안 LINKTEST가 보이지 않거나 끊길 때
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerScheduler.java @ d3636ab86763 ; 독립 실행 L61~70
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; appendLinktestRequest L321~342
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; 무통신 LINKTEST·T6 기대값 L170~203
```

HSMS 타이머는 데이터 수신과 독립적으로 점검한다. LINKTEST는 사용 설정이 켜져 있고 세션이 SELECTED인 경우에 주기 요청을 만든다.

LINKTEST가 보이지 않으면 운영 담당자에게 사용 여부·SELECT 완료 여부·주기 경과를 먼저 대조하도록 요청한다. 송신 뒤 끊기면 LINKTEST_REQ와 LINKTEST_RSP의 systemBytes 및 T6 만료 기록을 확인하도록 요청한다.

사용자는 설비 ID·마지막 정상 통신 시각·끊긴 시각을 전달한다. 통신 로그를 화면에서 보는 절차는 `00.rag/user/message-results-and-communication-logs.md`에서 확인한다.

## 같은 HSMS 증상이 반복될 때 수정 판본을 확인하는 방법
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 ; ACTIVE 개시 L77~89
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerScheduler.java @ d3636ab86763 ; 독립 타이머 L61~70
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 ; S1F2 식별 L117~156
```

ACTIVE SELECT 개시와 독립 타이머는 수정 07c84fbc 이후 판본에서, PASSIVE S1F2 식별은 수정 9876b7f7 이후 판본에서 구현되어 있다.

증상이 반복되면 운영 담당자에게 두 수정의 포함 여부를 배포 판본과 대조하도록 요청한다. 설비 ID·발생 시각·연결 모드·화면 결과와 함께, SELECT 단계인지 S1F2 식별 단계인지 확인한 결과를 전달한다.

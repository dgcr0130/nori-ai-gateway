# Socket 프레임 손상·매치 반복·첫 메시지 유실 구별
```rag-meta
유형: 트러블슈팅
적용: nori-tc-gateway-action f4fdfd6d27ce·nori-tc-gateway d3636ab86763 및 설비별 Socket 설정 적용 수정 a5e5221853b7 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/socket-framing-and-parsing-reference.md
```

## 한글 수신 뒤부터 Socket 프레임 경계가 어긋날 때
```rag-meta
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; tryExtractOne·원본 바이트 계산 L68~154
원천: nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedByteBoundaryTest.java @ f4fdfd6d27ce ; 원본 보존·해석 실패·분할 수신 기대값 L27~66
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; tryExtractOne L116~154
```

기본 REGEX_DELIMITED와 action 공통 정규식 기반은 종료자까지의 원본 바이트 수만 소비한다. 한글 같은 여러 바이트 문자가 있어도 다음 프레임을 남긴다.

종료자 앞에 문자셋으로 해석할 수 없는 바이트가 있으면 프레임 추출이 실패한다. 수신 끝에서 문자가 덜 도착했고 종료자도 아직 없으면 다음 수신을 기다린다. 두 경우를 구별한다.

설비 ID·발생 시각·처음 깨진 메시지를 전달하고, 운영 담당자에게 수신 원본 바이트·실제 처리 문자셋·소비한 바이트 수를 대조하도록 요청한다. 플러그인이 공통 기반을 쓰는지와 action 8d5fac0b 수정 포함 여부도 확인을 요청한다.

## Socket 문자셋 오류가 준비 중 또는 첫 수신에서 날 때
```rag-meta
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; 문자셋 검사·LF 탐색 L28~74
원천: nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedCharsetGuardTest.java @ f4fdfd6d27ce ; 거부·허용 문자셋 기대값 L23~35
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; 생성자 검사 L54~64
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비 문자셋 공급 L38~63
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java @ a5e5221853b7 ; 문자셋 검증 L55~64
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 ; 내장 처리기 생성 L51~63
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ a5e5221853b7 ; 플러그인 우선·문자셋 전달 L41~71
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ a5e5221853b7 ; 수신 처리기 선택 L106~114 L246~253
원천: nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java @ d3636ab86763 ; 수신 실패·DLQ·격리 L208~243 L286~333
```

기본 LINE_DELIMITED와 action 공통 줄 단위 기반은 LF를 단일 0x0A 바이트로 표현하는 문자셋만 허용한다. 설비 문자셋 적용 수정이 들어간 판본의 내장 줄 단위 처리기는 UTF-16·UTF-16LE를 첫 업무 수신의 처리기 생성에서 거부하고 DLQ·격리 경로로 간다.

없는 문자셋 이름은 설비 ID가 포함된 오류로 문맥 생성에서 실패한다. 플러그인에는 설비 문자셋을 전달하지 않는다.

운영 담당자에게 설비 ID·실패 시각·문자셋 오류 문구를 전달하고, 선택한 프로토콜과 실제 처리 문자셋을 확인하도록 요청한다. 설비 송신 규격과 맞는 조합을 설비·모델 담당자와 대조한다.

action 공통 기반의 문자셋 검사는 8d5fac0b 수정 이후 동작이다. 프레임 방식과 해석 설정은 `00.rag/user/socket-framing-and-parsing-reference.md`에서 확인한다.

## 종료 정규식 매치가 반복되고 처리가 멈출 때
```rag-meta
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; 생성자 검증·위치 0 방어 L41~55 L89~105
원천: nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedEndRegexValidationTest.java @ f4fdfd6d27ce ; 빈 문자열·실제 입력 매치 기대값 L21~45
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; 생성자·추출 검사 L77~92 L137~154
```

기본 REGEX_DELIMITED와 action 공통 정규식 기반에서는 endRegex가 빈 문자열에도 매치되는지 확인한다. `\s*`·`\r?\n?`는 생성 시 거부된다.

`^(?=A)`처럼 빈 문자열 검사는 통과해도 실제 입력의 위치 0에서 길이 0으로 매치되는 패턴은 프레임 추출 시 실패한다. 이때 버퍼를 소비하지 않고 예외를 낸다. 생성 단계의 거부와 수신 중 실패를 구별한다.

운영 담당자에게 종료 정규식 원문·멈춤 직전 메시지·시각을 전달하고 매치 끝 위치와 버퍼 소비 여부를 확인하도록 요청한다. 모델 담당자에게 종료자 규칙 대조를 요청한다. action 공통 기반에서는 4a3484c7 수정 이후 위 방어가 적용되므로, 반복 증상에는 적용 JAR 판본과 공통 기반 사용 여부도 함께 확인한다.

## 연결 직후 첫 Socket 업무 메시지만 빠질 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; INITIALIZE 분리·연결 배정·재주입 L194~233 L276~304 L391~405 L876~892
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandlerConnectionSyncTest.java @ d3636ab86763 ; 첫 업무 프레임·단독 INITIALIZE 기대값 L212~240
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ a5e5221853b7 ; ACTIVE 초기화 문자셋·원본 분리 L211~239
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/SocketEqpIdExtractor.java @ a5e5221853b7 ; 설비 문자셋 조회 L81~90
```

ACTIVE Socket에서 설비 연결 배정 전에 `CMD=INITIALIZE`와 첫 업무 프레임이 같은 수신 묶음으로 왔는지 확인한다. 이 수정이 들어간 판본은 설비 문자셋과 CR·LF 구분을 쓴다.

Gateway 2d56c2b7 수정 이후 이 경로는 INITIALIZE 뒤의 CR·LF를 건너뛴 나머지를 보관하고, INITIALIZE_REP 전송과 연결 배정이 성공하면 업무 수신 처리에 넘긴다. INITIALIZE만 받은 경우는 넘길 업무 데이터가 없다.

설비 ID·연결 시각·빠진 첫 메시지·다음 정상 메시지를 운영 담당자에게 전달한다. 같은 수신 묶음이었는지, 연결 배정 뒤 남은 데이터가 전달됐는지, 현장 Gateway 판본에 수정이 포함됐는지 확인을 요청한다. 화면 로그 조회는 `00.rag/user/message-results-and-communication-logs.md`를 따른다.

## INITIALIZE 뒤 연결이 닫혀 Socket 업무가 오지 않을 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 응답 생성·송신·연결 거부 L240~290
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 나머지 보관 실패·재주입 실패 L391~405 L847~874
```

ACTIVE Socket의 INITIALIZE 경로는 응답 생성 실패, 응답 전송 실패, 연결 배정 거부 때 연결을 닫는다. 첫 업무 데이터 보관 공간이 넘치거나 배정 뒤 남은 데이터를 꺼내는 데 실패해도 연결을 닫는다.

운영 담당자에게 설비 ID·INITIALIZE 시각·연결이 닫힌 시각·화면 결과를 전달한다. 응답 전송 성공, 연결 배정 성공, 남은 데이터 전달 중 어디까지 진행됐는지 확인을 요청한다. 연결이 완료되기 전 실패와, 연결 뒤 첫 메시지만 빠진 증상을 구별한다.

연결·명령 결과 자체의 점검은 `00.rag/user/equipment-connection-and-command-troubleshooting.md`에서 확인한다.

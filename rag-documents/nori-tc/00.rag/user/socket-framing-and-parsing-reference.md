# Socket 프레임·파싱 규칙
```rag-meta
유형: 참조
적용: nori-tc-gateway d3636ab86763 기준과 설비별 Socket 설정 적용 수정 a5e5221853b7·nori-tc-gateway-action f4fdfd6d27ce·nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/socket-troubleshooting.md
```

## Socket Settings 입력값의 실제 반영 범위
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 화면 항목 L1269~1371
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; Socket 저장 L517~530
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java @ a5e5221853b7 ; 실행 프로필 공급 L26~34
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/model/EquipmentContextProfile.java @ d3636ab86763 ; SocketSettings L111~122
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 실행 설정 공급 L45~63
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 ; 설비 문자셋 적용 L51~63
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ a5e5221853b7 ; 설비 Keep Alive 적용 L299~307
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ a5e5221853b7 ; 접속 Keep Alive 설정 L233~243
```

이 수정이 들어간 판본에서 「Socket Protocol Type」은 socketType을 선택하고, 「Charset」·「Max Frame Size」는 내장 처리기의 문자셋·프레임 상한에 반영된다.

「Socket Settings」의 「Keep Alive Enabled」는 설비별 TCP SO_KEEPALIVE에 반영된다.

「Heartbeat Enabled」·「Heartbeat Interval」·「Read Timeout」·「Write Timeout」 저장값은 실행 설정에 쓰이지 않는다.

화면 값과 동작이 다르면 운영 담당자에게 설비 ID·화면 입력값·관측한 증상을 전달하고 실제 적용 설정·처리기 확인을 요청한다.

입력 규칙은 `00.rag/user/equipment-registration-and-settings.md`를 따른다.

## 프레임 방식 식별값과 적용 대상
```rag-meta
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; socketTypeRegistry L253~263
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/config/SocketTypeConfig.java @ d3636ab86763 ; socketType 의미 L4~17
```

socketType은 설비의 socket_protocol_type에 대응하는 프레임 방식 식별값이다. Gateway가 기본 등록하는 처리기는 다음과 같다.

| 식별값 | 프레임 종료 기준 |
|---|---|
| LINE_DELIMITED | LF 바이트를 찾는다. |
| REGEX_DELIMITED | 종료 정규식과 일치하는 부분을 찾는다. |

이 문서의 내장 처리 규칙은 내장 처리기를 사용할 때 적용한다. 플러그인 적용 조건은 `00.rag/user/model-plugin-limitations.md`에서 확인한다.

## LINE_DELIMITED 수신 경계
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; tryExtractOne L88~111
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; tryExtractOne L50~74
```

내장 LINE_DELIMITED와 action 공통 줄 단위 기반은 누적 데이터의 첫 LF(0x0A)까지를 한 프레임으로 추출한다. 반환하는 바이트에는 LF도 포함한다.

LF가 아직 없으면 프레임을 반환하지 않고 데이터를 남긴다. LF 뒤의 데이터는 다음 프레임 대상으로 남긴다. CR 단독은 이 방식의 프레임 종료자가 아니다.

## REGEX_DELIMITED의 종료 정규식
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java @ d3636ab86763 ; 종료 패턴 바인딩·검사 L12~46
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 ; 종료 패턴 공급 L253~263
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; 생성자·tryExtractOne L77~92 L116~154
```

내장 REGEX_DELIMITED의 endRegex는 `tc.gateway.socket.regex-end-pattern`에서 공급한다. null·빈 문자열·공백만 있는 값은 거부한다. Java 정규식으로 해석하며 빈 문자열에 매치되는 패턴도 거부한다.

수신 시 첫 매치의 끝까지를 프레임으로 삼고 종료 패턴에 맞은 부분도 포함한다. 종료 패턴을 아직 찾지 못한 정상 데이터는 남겨 둔다. 종료 패턴 뒤의 데이터는 다음 추출 대상으로 남긴다.

## 프레임 길이 상한의 단위와 공급값
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java @ d3636ab86763 ; maxFrameBytes 검사 L12~40
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 프레임 상한 공급 L45~63
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; 길이 비교 L102~109
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; 바이트 길이 비교 L145~152
```

| 항목 | 값·단위 | 의미 |
|---|---|---|
| maxFrameBytes | 0보다 큰 정수·바이트 | 내장 처리기가 추출할 한 프레임의 상한이다. 종료자 바이트도 포함해 비교한다. |

이 수정이 들어간 판본에서 maxFrameBytes에는 설비의 「Max Frame Size」가 공급된다. 설비 Socket 설정이 없는 경로는 `tc.gateway.socket.max-frame-bytes`를 쓴다.

Gateway 설정 바인딩 코드에 기본값은 없으며 누락·0 이하 값은 거부한다. 종료자를 찾은 프레임의 바이트 수가 상한과 같으면 허용하고 초과하면 추출을 실패시킨다.

## 내장 처리기의 메시지 이름·본문·원문 속성
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; decode L123~146
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; decode L223~244
원천: nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandlerTest.java @ f4fdfd6d27ce ; 기본 해석 기대값 L16~29
```

내장 처리기는 아래 정리 후 첫 공백 묶음에서 문자열을 나눈다. 첫 토큰이 messageName이고 나머지가 body이다. 나머지가 없으면 body는 빈 문자열이다.

| 처리기 | 문자열 정리 | 정리한 전체 문자열 속성 |
|---|---|---|
| LINE_DELIMITED | CR·LF를 모두 제거하고 trim()을 적용한다. | attributes.rawLine |
| REGEX_DELIMITED | trim()을 적용한다. | attributes.rawText |

REGEX_DELIMITED는 종료 정규식에 맞은 문자열을 별도로 삭제하지 않는다. endRegex가 `#END`이고 수신 프레임이 `EVT BODY#END`이면 body는 `BODY#END`이다.

## 줄 단위 송신의 LF 보정
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; encode L160~184
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; encode L78~94
원천: nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandlerTest.java @ f4fdfd6d27ce ; LF 부착 기대값 L16~30
```

내장 LINE_DELIMITED와 action 공통 줄 단위 기반의 기본 encode는 송신 문자열 끝이 LF이면 그대로 인코딩하고, LF로 끝나지 않으면 LF 하나를 붙인다. 이미 CR·LF로 끝나는 문자열에도 LF를 더 붙이지 않는다.

송신값 `PONG`은 `PONG\n`으로 인코딩한다. null 명령은 거부한다.

## 정규식 송신에서 종료자를 붙이는 조건
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; encode·endsWithRegexDelimiter L258~312
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; encode L165~188
```

내장 REGEX_DELIMITED와 action 공통 정규식 기반의 기본 encode는 다음 조건으로 송신 문자열을 처리한다.

| 조건 | 인코딩할 문자열 |
|---|---|
| 종료 정규식의 매치 끝이 문자열 끝에 닿음 | 송신 문자열을 유지한다. |
| 끝에서 매치되지 않고 정규식에서 고정 종료 문자열을 복원할 수 있음 | 복원한 종료 문자열을 뒤에 붙인다. |
| 끝에서 매치되지 않고 고정 종료 문자열을 복원할 수 없음 | 자동 부착 없이 송신 문자열을 유지한다. |

마지막 조건에서는 보낼 명령 자체에 필요한 종료자를 포함해야 한다.

## 자동 부착 가능한 종료 정규식의 문자 표기
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; REGEX_META_CHARS·tryResolveLiteralSuffixFromRegex L42~43 L328~388
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; 고정 종료 문자열 복원 L223~279
```

내장 REGEX_DELIMITED와 action 공통 정규식 기반은 일반 문자와 다음 이스케이프 표기를 고정 종료 문자열로 복원한다.

| 정규식 표기 | 복원 결과 |
|---|---|
| `\n`·`\r`·`\t` | 각각 LF·CR·탭이다. |
| `\\` | 역슬래시 한 문자이다. |
| `\xHH`·`\uHHHH` | 각각 16진수 2자리·4자리가 나타내는 문자이다. |

정규식 메타문자 `.^$|?*+()[]{}` 또는 위 목록 밖의 이스케이프가 있으면 자동 부착 문자열을 만들지 않는다. 예를 들어 `\r?\n`은 송신 종료자를 자동 부착하지 않는다.

## 빈 프레임을 허용할 때의 처리
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java @ d3636ab86763 ; allowEmptyFrame 바인딩·검사 L12~43
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ d3636ab86763 ; 실행 정책 공급 L50~54
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 ; 길이 검사·전용 예외 처리 L119~140
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/test/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipelineEmptyFramePolicyTest.java @ d3636ab86763 ; 빈 줄 정책 기대값 L34~58
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; 빈 줄 구별 L128~134
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; 빈 문자열 구별 L229~232
```

allowEmptyFrame에는 `tc.gateway.socket.allow-empty-frame`이 공급된다. true·false를 명시해야 하며 바인딩 코드에 기본값은 없다.

| 정책값 | 수신 처리 |
|---|---|
| false | 0바이트 프레임을 거부하고, decode의 EmptySocketFrameException도 실패로 전달한다. |
| true | 0바이트 프레임도 decode에 넘긴다. EmptySocketFrameException이면 그 프레임을 건너뛰고 다음 프레임을 처리한다. |

내장 처리기에서 공백·개행 정리 뒤 빈 문자열이 된 프레임은 EmptySocketFrameException으로 구별한다.

## 문자셋 기본값과 action 공통 해석의 범위
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; 문자셋 기본값 L43~63
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 ; 문자셋 기본값 L65~90
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; 문자셋·기본 해석 L24~37 L104~113
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce ; 문자셋·기본 해석 L37~55 L197~206
원천: nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/text/TokenizedTextDecodeSupport.java @ f4fdfd6d27ce ; decodeFirstToken L51~66
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비 문자셋 공급 L45~63
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java @ a5e5221853b7 ; 문자셋 조회·기본값 L54~64
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 ; 내장 처리기 문자셋 구성 L51~63
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ a5e5221853b7 ; 수신 처리기 선택 L106~109 L246~255
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/SocketEqpIdExtractor.java @ a5e5221853b7 ; 설비 식별 전 문자셋 L81~90
원천: nori-tc-gateway/gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/SocketTypeHandler.java @ d3636ab86763 ; decode 공개 계약 L32~39
```

내장 처리기와 action 공통 기반은 문자셋을 생략한 생성자나 null 문자셋에서 UTF-8을 사용한다.

이 수정이 들어간 판본의 내장 LINE·REGEX 처리기는 설비 문자셋으로 구성한다. 설비 Socket 설정이 없는 경로와 설비 식별 전 연결 초기 구간은 UTF-8을 쓴다.

action 공통 기반의 defaultTokenizedDecode가 사용하는 전체 문자열 속성은 rawText이다. 추가 속성에 같은 키가 있으면 추가 속성 값으로 덮어쓴다. 플러그인이 자체 decode를 구현하면 해석 규칙은 그 구현을 따른다.

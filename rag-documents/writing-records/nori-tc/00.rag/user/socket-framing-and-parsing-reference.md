# Socket 프레임·파싱 규칙 작성 기록

## 대상

| 항목 | 확인 결과 |
|---|---|
| objectKey | `00.rag/user/socket-framing-and-parsing-reference.md` |
| 구획·유형 | user `[AI_DOC_READ]`·참조 |
| 검색 원문 | `rag-documents/nori-tc/00.rag/user/socket-framing-and-parsing-reference.md` |
| 검색 원문 SHA-256 | `af11f9d9c1703dc19bec2737ef327b36a5ff48d0ae85be7f0c3cca4594f1a120` |
| 줄·절·자식 수 | 181줄·H2 11개·직렬화 자식 39개 |
| 파일 바이트 수 | 16483 |
| 확인일 | 2026-11-19 한국 시간 |
| 수행 범위 | 검색 원문과 이 작성 기록만 작성한다. 소스·설정·다른 RAG 문서·중앙 문서를 수정하지 않는다. 업로드·git add·commit·push를 수행하지 않는다. |

## 원천 판본

이 절의 기존 HEAD·변경 없음 기록은 최초 작성과 첫 보완 당시의 이력이다. 제품 수정 C를 반영한 현재 파일 판본은 마지막 절의 SHA-256 표로 구별한다. 바뀌지 않은 본문 사실의 commit 원천은 유지한다.

시작 시 `git status --short --branch`, `git worktree list`를 실행했다. 제품 저장소는 main, HEAD `70cf6cd`, origin/main보다 8개 앞섰고 `?? rag-documents/`가 있었다. 기존 문서는 읽기만 했다.

| 저장소 | HEAD 12자리 | 작업 트리 확인 결과 |
|---|---|---|
| nori-tc-gateway | d3636ab86763 | 아래에 기록한 socket·core 설정·starter 공급자·공개 계약·관련 시험 경로의 `git status --short -- <경로>` 출력이 비어 있었다. |
| nori-tc-gateway-action | f4fdfd6d27ce | `src` 범위의 `git status --short -- src` 출력이 비어 있었다. |
| nori-tc-exec | f1d5e0a8e727 | 아래 두 문서 경로의 `git status --short -- <경로>` 출력이 비어 있었다. |
| nori-tc-business | 7db24f7466bd | 범위를 구별하기 위해 구분자 사용 위치만 검색했다. 검색 원문 근거로 사용하지 않았다. |
| nori-tc-ui-front | 9fe69a75dcd3 | 최초 작성에서는 검색 원문 근거로 쓰지 않았다. 보완에서는 EqpManageFormModal.tsx의 실제 표시 문구·요청 필드와 eqp.api.ts의 API 호출을 읽고 두 파일 및 ko.json의 변경 상태가 없음을 확인했다. |
| nori-tc-ui-backend | 985d17c604dd | 보완에서 JpaEqpCrudPort.java의 저장과 EqpControllerCommandSupport.java의 요청 변환을 읽었다. 두 파일의 작업 트리 변경 항목은 없었다. |

다른 저장소의 첫 git 조회는 소유자 불일치로 실행되지 않았다. 이후 읽기 명령에 정확한 저장소의 `-c safe.directory=C:/workspace_nori/nori-tc/<저장소>`를 지정했다. 전역 Git 설정을 변경하지 않았다. 상태 조회에는 사용자 전역 ignore 파일 접근 경고가 있었지만 명령은 exit 0이었고 지정 경로의 변경 항목은 없었다.

작성 규칙 `docs/rag-document-writing-rules.md`와 목록 `docs/tc-rag-document-list.md`는 전체를 읽었다. 지정 원천은 다음과 같다.

- `nori-tc-exec/docs/completion-report/04-interface-spec.md @ f1d5e0a8e727 ; §4.9 L342~361`
- `nori-tc-exec/docs/Architecture/20-gateway/06-socket-protocol-and-plugins.md @ f1d5e0a8e727 ; 전체, 특히 §2·§4·§5`

Gateway의 작업 트리 상태를 확인한 범위는 다음과 같다.

- `gateway/tc-gateway-socket/src`
- `gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java`
- `gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java`
- `gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context`
- `gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/SocketTypeHandler.java`
- `gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/SocketTypeResolution.java`(기본 방식의 사용 범위만 확인했고 본문에는 채택하지 않았다.)

関連文書は `socket-troubleshooting.md`、`model-plugin-limitations.md`を全体で読み、`equipment-registration-and-settings.md`のSocket設定・数値初期値と、`model-details-and-flow-editing.md`のSocket関連部分も確認した。前二文書が持つ診断・適用制約を本書に複製しない。後二文書の画面操作を新しい手順として書かない。

## 절별 사실 대조

### Socket Settings 입력값의 실제 반영 범위

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 22 | 프로토콜 선택·설비 문자셋·프레임 상한 적용 | nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java @ a5e5221853b7 L26~34 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 L45~63 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 L51~63 | 저장된 SocketSettings가 실행 정보로 전달되고 내장 처리기의 문자셋·상한에 쓰인다. 수정이 들어간 판본이라는 조건을 본문에 둔다. |
| 24 | 설비 Keep Alive의 실제 적용 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ a5e5221853b7 L299~307 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ a5e5221853b7 L233~243 | 접속 설정과 설비 연결 배정에서 설비의 keepAliveEnabled 값을 TCP SO_KEEPALIVE로 공급한다. |
| 26 | Heartbeat·Read/Write Timeout의 실행 미사용 유지 | nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java @ a5e5221853b7 L30~33 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 L45~63 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/model/EquipmentContextProfile.java @ d3636ab86763 L111~122 | 제품 Java에서 네 getter를 검색했다. DB 저장값 복사·프로필 재구성에는 남지만 새 실행 소비는 없다. Keep Alive는 적용되므로 이 미사용 목록에서 뺐다. |
| 28 | 화면값·증상 인계와 실제 설정·처리기 확인 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 L45~63 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 L51~63 ; 사용자 인계 요구 | 설비 ID·화면 입력값·관측 증상을 전달하도록 유지하고, 확인 대상은 Gateway 공통 설정만이 아니라 실제 적용된 설비 설정·처리기로 맞췄다. |
| 30 | 입력 규칙의 소유 문서 안내 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/equipment-registration-and-settings.md ; Socket Settings에서 프로토콜과 숫자 채우기 | 해당 절의 실제 문구를 읽고 입력·검증 규칙을 복제하지 않고 objectKey만 안내했다. |

### 프레임 방식 식별값과 적용 대상

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 38 | socketType과 socket_protocol_type의 대응·기본 등록 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/config/SocketTypeConfig.java @ d3636ab86763 L4~17 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 L253~263 | 설정 record의 의미와 실제 내장 처리기 등록을 대조했다. |
| 42 | LINE_DELIMITED의 종료 기준 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L33~33 L88~111 | 상수와 LF 탐색 조건을 확인했다. |
| 43 | REGEX_DELIMITED의 종료 기준 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L42~42 L125~154 | 상수와 첫 정규식 매치 끝을 사용하는 코드를 확인했다. |
| 45 | 내장 규칙의 조건·플러그인 문서 위치 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 L31~62 ; nori-ai-gateway/docs/tc-rag-document-list.md의 확정 문서 표 | 내장과 플러그인을 별도로 해석한다. 목록과 실제 존재하는 user 문서의 소유 범위에 맞춰 위치만 안내한다. 플러그인 우선순위 설명은 복제하지 않았다. |

### LINE_DELIMITED 수신 경계

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 53 | 첫 LF까지·종료 바이트 포함 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L88~111 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L50~74 | 첫 0x0A에서 탐색을 멈추고 인덱스+1 바이트를 copy/discard한다. |
| 55 | LF 미도착·나머지 보존·CR 단독의 처리 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L93~109 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L56~74 | LF가 없을 때 null을 반환하며 discard하지 않는다. CR은 탐색 조건이 아니다. |

### REGEX_DELIMITED의 종료 정규식

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 64 | 종료 패턴 바인딩·생성 조건 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java @ d3636ab86763 L12~46 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java @ d3636ab86763 L253~263 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L77~92 | prefix·필드·getter 공급을 따라가고 Pattern.compile과 빈 문자열 매치 거절을 확인했다. |
| 66 | 첫 매치 끝까지 포함·미완성 데이터 보존 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L116~154 | 첫 find 결과의 end를 바이트 수로 계산한다. 정상 접두부에서 매치가 없으면 null이며 추출 뒤 나머지는 남긴다. 해석 불가 데이터의 진단은 다른 문서가 소유한다. |

### 프레임 길이 상한의 단위와 공급값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 78 | maxFrameBytes의 이름·양수·바이트·종료자 포함 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java @ d3636ab86763 L17~40 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L102~109 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L145~152 | 양수 검증과 종료자 포함 추출 바이트 수 비교를 확인했다. |
| 80 | 설비 프레임 상한 공급·설정 부재 경로의 기본값 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 L45~63 | SocketSettings가 있으면 maxFrameSizeBytes, 없으면 getMaxFrameBytes를 공급한다. |
| 82 | Gateway 값 검증·프레임 상한과 동일/초과 비교 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java @ d3636ab86763 L12~40 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L102~109 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L145~152 | Gateway 설정의 검증과 프레임 바이트 수 비교는 바뀌지 않았다. 기존 commit 원천을 보존하고 공급 조건과 문단을 분리했다. |

### 내장 처리기의 메시지 이름·본문·원문 속성

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 91 | 첫 토큰과 나머지·나머지 없는 경우 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L137~145 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L235~243 | 두 구현 모두 `split("\\s+", 2)`이며 두 번째 부분이 없을 때 빈 문자열을 넣는다. |
| 95 | 줄 단위 정리와 rawLine | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L128~145 | CR·LF 전체 replace 후 trim을 수행하고 rawLine에 저장한다. |
| 96 | 정규식 정리와 rawText | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L229~243 | trim만 수행하고 rawText에 저장한다. |
| 98 | 종료 문자열 보존·BODY#END 예시 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L223~244 ; nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandlerTest.java @ f4fdfd6d27ce L16~29 | 내장 코드에 종료자 삭제가 없다. 공통 기반 시험의 body 기대값도 BODY#END이다. 제조사 프레임 예시가 아닌 파서 예시이다. |

### 줄 단위 송신의 LF 보정

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 107 | LF 끝 유지·LF 자동 부착·CRLF 유지 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L160~184 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L78~94 | endsWith LF의 두 분기를 확인했다. CRLF도 LF로 끝나므로 유지된다. |
| 109 | PONG 예시·null 거절 | nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandlerTest.java @ f4fdfd6d27ce L16~30 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L78~94 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L160~184 | 시험의 PONG LF 기대값과 두 구현의 null 검사를 읽었다. 시험을 새로 실행했다는 의미는 아니다. |

### 정규식 송신에서 종료자를 붙이는 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 117 | 내장·공통 기본 encode 적용 범위 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L258~289 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L165~188 | 양쪽의 동일한 세 분기를 읽었다. |
| 121 | 문자열 끝의 매치가 있으면 유지 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L277~279 L301~312 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L176~178 L209~220 | matcher.end와 message.length의 일치로 판정한다. |
| 122 | 고정 종료 문자열 복원 가능 시 부착 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L280~282 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L179~181 | autoAppendSuffix가 null이 아니면 뒤에 붙인다. |
| 123 | 복원 불가 시 자동 부착 없음 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L283~285 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L182~184 | fallback은 원문 유지이다. |
| 125 | 복잡한 패턴의 명령에 종료자 포함 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L266~285 | 기본 encode가 종료자를 만들지 않는 분기와 주석의 명령 포함 조건을 대조했다. |

### 자동 부착 가능한 종료 정규식의 문자 표기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 133 | 일반 문자와 제한된 escape만 복원 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L328~388 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L223~279 | switch 허용 분기와 일반 문자 append를 읽었다. |
| 137 | LF·CR·탭 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L342~346 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L237~241 | n·r·t 분기의 실제 append 문자를 확인했다. |
| 138 | 역슬래시 한 문자 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L347~347 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L242~242 | 두 역슬래시 표기의 복원 결과를 확인했다. |
| 139 | x 2자리·u 4자리 16진수 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L348~368 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L243~263 | substring 길이, isHex 검사와 16진수 파싱을 확인했다. |
| 141 | 메타문자·미지원 escape의 자동 부착 제외·선택 CR 예시 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L43~43 L370~388 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L31~31 L265~279 | 상수 전체와 default/null 분기, 물음표 포함 시 복원하지 않는 조건을 확인했다. 정규식 자체의 지원 여부와 자동 부착 가능 여부를 구별했다. |

### 빈 프레임을 허용할 때의 처리

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 153 | allowEmptyFrame 공급·명시 필수·기본값 없음 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewaySocketProperties.java @ d3636ab86763 L12~43 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ d3636ab86763 L50~54 | nullable Boolean과 null 거절, isAllowEmptyFrame 공급을 확인했다. |
| 157 | false 정책 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 L119~133 ; nori-tc-gateway/gateway/tc-gateway-socket/src/test/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipelineEmptyFramePolicyTest.java @ d3636ab86763 L51~58 | 0길이 검사와 전용 예외 재던짐을 확인했고 시험 기대값을 읽었다. |
| 158 | true 정책 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 L119~140 ; nori-tc-gateway/gateway/tc-gateway-socket/src/test/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipelineEmptyFramePolicyTest.java @ d3636ab86763 L34~48 | true면 길이 검사를 통과해 decode를 호출한다. 전용 예외만 continue한다. 임의의 decode 오류까지 무시한다고 쓰지 않았다. |
| 160 | 내장 빈 문자열의 전용 예외 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L128~134 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L229~232 | 각 방식의 정리 후 isEmpty에서 전용 예외를 던진다. |

### 문자셋 기본값과 action 공통 해석의 범위

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 177 | 생성자 생략·null 문자셋 기본값 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L43~63 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L65~90 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L24~37 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L37~55 | 생성자의 UTF-8 기본값은 유지된다. 공유 등록의 기본 문자셋을 실제 설비 처리 문자셋으로 확대하지 않는다. |
| 179 | 설비 문자셋 적용·설정 부재 및 식별 전 UTF-8 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 L45~63 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java @ a5e5221853b7 L54~64 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 L51~63 ; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ a5e5221853b7 L106~109 L246~255 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/SocketEqpIdExtractor.java @ a5e5221853b7 L81~90 | 수신 선택은 설정의 charset을 전달해 내장 처리기를 구성한다. 설정이 없거나 설비 ID가 아직 없는 초기 조회는 UTF-8이다. |
| 181 | 공통 rawText·추가 속성 덮어쓰기·자체 decode | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L104~113 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L197~206 ; nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/text/TokenizedTextDecodeSupport.java @ f4fdfd6d27ce L51~66 ; nori-tc-gateway/gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/SocketTypeHandler.java @ d3636ab86763 L32~39 | 두 defaultTokenizedDecode의 위임과 rawText 이후 putAll 순서를 확인했다. 공개 decode는 구현자가 제공하므로 이를 모든 플러그인의 고정 규칙으로 확대하지 않았다. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 문서 | 코드 | 검색 원문 반영 |
|---|---|---|---|
| REGEX_DELIMITED 전체 문자열 속성 | nori-tc-exec/docs/completion-report/04-interface-spec.md @ f1d5e0a8e727 §4.9 SOCKET-002 L347은 argument를 rawLine으로 적는다. | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L229~243은 rawText에 저장한다. | L96에 attributes.rawText로 작성한다. LINE 방식의 rawLine과 구별한다. |

구조 문서 §2의 추천값 256KB·빈 프레임 false는 기준 HEAD의 `SocketTypeConfig.recommendedDefaults` L36~41과 일치한다. 현재 실행 공급은 수정 C에 따라 설비 프레임 상한을 우선하며 설비 Socket 설정이 없는 경로만 Gateway 상한을 사용한다. 빈 프레임 정책은 Gateway 설정에서 공급한다. 추천값 함수를 실제 공급 경로와 구별한다.

내장 두 처리기의 encode JavaDoc에는 종단문자 보정을 하지 않는다는 과거 설명이 남아 있으나 실제 메서드는 LF 또는 복원 가능한 종료자를 붙인다. 본문은 메서드 구현과 관련 시험 기대값에 맞췄다. 해당 제품 소스는 수정하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 역할 |
|---|---|---|
| 제조사별 명령·응답 필드·종료 문자열·길이 헤더 배치 | 제조사 원본이 없다. 일반 파서와 I §4.9의 업무 예시를 제조사 계약으로 확정하지 않는다. | 설비·제조사 프로토콜 담당 |
| 현장에 실제 적용된 문자셋·정규식·프레임 상한·빈 프레임 정책·플러그인 동작 | 현재 작업은 기준 HEAD와 명시한 파일 SHA-256의 코드 대조이다. 현장 설정과 배포 판본을 조회하지 않았다. | 운영·플러그인 담당 |
| 내장 길이 헤더 방식의 설정값과 파싱 계약 | 구조 문서에는 length-delimited가 비교 대상으로 언급되지만 실제 기본 등록은 LINE·REGEX 두 종류다. 별도 길이 처리기 계약의 원천은 확보하지 않았다. | Gateway·플러그인 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 사실 묶음 | 소유 objectKey | 경계 |
|---|---|---|
| 프레임 손상·0길이 매치·첫 업무 메시지 유실의 원인 구별과 인계 | `00.rag/user/socket-troubleshooting.md` | 본문에는 프레임 규칙의 최소 조건만 두고 증상별 진단과 수정 커밋 이력을 복제하지 않았다. |
| 플러그인 선택 우선순위·JAR 적재·SDK 호환성·교체 제약 | `00.rag/user/model-plugin-limitations.md` | 내장과 공통 기반의 적용 조건만 밝힌다. 관련 값 표와 정책 설명을 복제하지 않는다. |
| 설비 등록·수정과 Socket 화면 입력 절차 | `00.rag/user/equipment-registration-and-settings.md` | 화면 숫자 초기값·버튼·권한·저장 절차를 새로 쓰지 않는다. |
| 모델 표·MDF·Flow 편집 | `00.rag/user/model-details-and-flow-editing.md` | 프레임 결과 해석에 필요한 이름만 설명하고 화면 절차를 복제하지 않는다. |
| 업무 명령 흐름·MES·WM 관계 | `00.rag/user/lot-carrier-work-and-system-flow.md` | I §4.9의 업무 명령 행을 프레임 참조 표로 복사하지 않는다. |
| 운영 설정 변경·배포 설정의 전체 목록 | `00.rag/admin/configuration-reference.md` | 이 기록에만 소유 경계를 표시한다. 검색 원문에 admin 경로·열람 지시·파일 수정 절차를 넣지 않았다. |

## 최초 작성 검사 이력

지정 검사기를 수정하지 않고 세 번 실행했다. 첫 실행은 문서 내 messageName·body 표 행 중복 2건으로 exit 1이었다. 문자열 표현만 바꾸지 않고 공통 사실을 하나의 해석 절로 합쳐 소유 위치를 정리했다. 두 번째는 exit 0이었다. 마지막 편집에서 표 밖 인라인 코드의 불필요한 파이프 이스케이프를 제거하고 세 번째 최종 검사를 실행했다.

최종 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/socket-framing-and-parsing-reference.md
```

최종 종료코드: 0. 마지막 출력 10줄:

```text
    L129-129 문단 97  | allowEmptyFrame에는 tc.gateway.socket.allow-empty-frame이 공급된다. true·false를 명시해야 하며 바인딩 코드에 기본값은 없다.
    L133-133 표 행 78  | 정책값=false | 수신 처리=0바이트 프레임을 거부하고, decode의 EmptySocketFrameException도 실패로 전달한다.
    L134-134 표 행 93  | 정책값=true | 수신 처리=0바이트 프레임도 decode에 넘긴다. EmptySocketFrameException이면 그 프레임을 건너뛰고 다음 프레임을 처리한다.
    L136-136 문단 67  | 내장 처리기에서 공백·개행 정리 뒤 빈 문자열이 된 프레임은 EmptySocketFrameException으로 구별한다.
§ L138-151 [25] 문자셋 기본값과 action 공통 해석의 범위   parent=224 children=2 sources=7
    L149-149 문단 86  | 내장 처리기와 action 공통 기반은 문자셋을 생략한 생성자나 null 문자셋에서 UTF-8을 사용한다. 내장 처리기 등록은 문자셋 인자 없이 수행한다.
    L151-151 문단 137  | action 공통 기반의 defaultTokenizedDecode가 사용하는 전체 문자열 속성은 rawText이다. 추가 속성에 같은 키가 있으면 추가 속성 값으로 덮어쓴다. 플러그인이 자체 decode를 구현하면 해석 규칙은 그 구현을 따른다.
SUMMARY sections=10 children=32 maxChild=157 maxParent=367
CORPUS docs=40/53 sections=555 part={admin=9, user=31} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=2, 트러블슈팅=12}
TOTAL files=40 targets=1 violations=0
```

검사 환경: GNU bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS. MASK 출력·CROSS VIOLATION·대상 문서 위반은 최종 출력에 없다. 현재 코퍼스에서 확인한 결과이며 향후 작성 문서까지의 중복 검증을 뜻하지 않는다.

최초 작성의 사람 검사는 40절 미만이므로 당시 10절 전부를 읽었다. 다음 결과는 최초 작성 당시의 판정이며, 메인 세션에서 발견한 화면값 미반영 설명 누락과 이번 보완 결과는 아래에 별도로 남긴다.

| 절 제목 | 제목만으로 질문 식별 | 원천 대조 | 독립 답 | 사실 소유·중복 판정 |
|---|---|---|---|---|
| 프레임 방식 식별값과 적용 대상 | 적합 | 적합 | 적합 | 종류 식별과 위치 안내만 소유한다. |
| LINE_DELIMITED 수신 경계 | 적합 | 적합 | 적합 | 수신 경계만 설명한다. 송신은 별도 절이다. |
| REGEX_DELIMITED의 종료 정규식 | 적합 | 적합 | 적합 | 종료 패턴 공급과 경계를 설명한다. 장애 진단을 복제하지 않는다. |
| 프레임 길이 상한의 단위와 공급값 | 적합 | 적합 | 적합 | 실제 설정 공급과 바이트 상한을 소유한다. |
| 내장 처리기의 메시지 이름·본문·원문 속성 | 적합 | 적합 | 적합 | 공통 첫 토큰 분해는 한 번만 쓰고 정리·속성 차이를 행으로 구별한다. |
| 줄 단위 송신의 LF 보정 | 적합 | 적합 | 적합 | 송신 보정과 null 조건을 설명한다. |
| 정규식 송신에서 종료자를 붙이는 조건 | 적합 | 적합 | 적합 | 부착 여부의 세 분기만 설명한다. |
| 자동 부착 가능한 종료 정규식의 문자 표기 | 적합 | 적합 | 적합 | 위 분기에서 사용하는 문자 복원 목록을 소유한다. 부착 조건 표를 반복하지 않는다. |
| 빈 프레임을 허용할 때의 처리 | 적합 | 적합 | 적합 | Boolean별 처리와 전용 예외 범위를 소유한다. |
| 문자셋 기본값과 action 공통 해석의 범위 | 적합 | 적합 | 적합 | 생성자 기본값과 공통 rawText의 덮어쓰기 조건만 추가한다. |

최초 작성 전수 판정: 제목 정확 중복·불필요한 3회 이상 반복 틀·빈 틀 문장·이름 토막 뜻풀이·깨진 문장·미해소 마스킹·관리 조치 노출을 발견하지 않았다. 동일 사실을 가진 두 파싱 표는 첫 검사에서 합쳤고, 앞선 트러블슈팅과 겹치는 조건은 참조에 필요한 최소 규칙으로 제한했다. 당시 본문 32개 자식의 모든 문장과 표 자료 행을 위 표로 대조했다. 메인 세션은 이후 적용 범위 설명 누락 1건을 지적했으므로 이 판정을 그 누락의 부재 근거로 삼지 않는다.

제품 코드 시험·빌드·실서비스 재현·업로드는 실행하지 않았다. 관련 시험은 기대값을 읽은 근거이며 새 PASS로 기록하지 않는다. 메인 세션의 독립 원천 검증은 이 자체 확인과 별개이다. 독립 Agent 호출은 0회이며 모델 사용량·과금·전체 작업 wall time은 이 세션에서 계측하지 않았다.

검사용 임시 파일·전용 브라우저·컨테이너·서비스를 만들지 않았다. 삭제할 소유 임시 자원은 없다. 유효한 결과는 지정된 두 Markdown 파일에만 보존한다.

## 메인 세션 지적에 대한 첫 보완 이력

메인 세션에서 화면 Socket Settings 저장값과 실제 실행값의 차이를 설명하지 않은 결함 1건을 확인했다. 최초 작성은 런타임의 문자셋·상한 공급만 확인하고 사용자 화면 입력과의 연결 여부를 답하지 못했다. 이 요청에서는 두 파일만 수정하며 제품 동작은 변경하지 않는다.

보완 전 검색 원문 SHA-256은 `76468d5b5cd17542be54b85f7e24a1f99e7e8cb8f6dd1346ff09b0e24915db9b`이다. 보완 후 검색 원문 SHA-256은 대상 표의 `6fdcfeb9912f1ec8b25418e0201e62882a1141e618c23317160712eecec46f3f`이다. 원문 앞부분 L10~32에 적용 범위 절을 추가하고 H1 적용 판본에 UI 두 저장소를 추가했다. 기존 L23의 입력 절차 안내는 새 절로 옮겨 중복하지 않았다. 나머지 기존 검색 본문은 보존했다. 기존 사실 대조 32행은 본문 삽입으로 이동한 줄 번호만 각각 +24로 맞췄고, 새 절의 5개 문단을 대조표에 추가했다.

Gateway HEAD는 `d3636ab86763`으로 유지된다. 보완에서 다음 파일에 대해 `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway -C ../nori-tc-gateway status --short -- <경로>`를 실행했고 출력된 변경 항목은 없었다.

- `gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java`
- `gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java`
- `gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCommConfiguration.java`
- `gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/model/EquipmentContextProfile.java`
- `gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java`
- `gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java`

제품 코드 사용처 확인 명령:

```text
git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway -C ../nori-tc-gateway grep -n -E '\.(charset|heartbeatEnabled|heartbeatInterval|readTimeout|writeTimeout|maxFrameSizeBytes|keepAliveEnabled)\(' -- '*.java' ':(exclude)**/src/test/**'
```

exit 0. 결과는 `GatewayEquipmentService.java` L468~474와 `GatewayUiRuntimeControlSupport.java` L294~300의 호출이었다. 실행용 하위 프로필과 SocketRuntimeContextProvider에 해당 값이 공급되지 않는 것을 코드로 재확인했다.

화면에서 저장까지의 연결은 `EqpManageFormModal.tsx` L694~708·L778~793, `eqp.api.ts` L577~620, `EqpControllerCommandSupport.java` L115~132, `JpaEqpCrudPort.java` L517~530·L888~905도 읽었다. 표시명은 컴포넌트 안의 직접 지정이며 ko.json의 다른 문구로 바꾸지 않았다. 입력 규칙과 숫자 초기값은 기존 설비 등록 문서가 소유하므로 새로 옮겨 쓰지 않는다.

보완 후 검사기는 변경하지 않고 1회 실행했다. 명령은 최초 작성 이력의 `check.sh --dump 00.rag/user/socket-framing-and-parsing-reference.md`와 같다. 검색 원문의 마지막 편집 뒤 실행한 결과이며 종료코드는 0이다. 마지막 출력 10줄:

```text
    L153-153 문단 97  | allowEmptyFrame에는 tc.gateway.socket.allow-empty-frame이 공급된다. true·false를 명시해야 하며 바인딩 코드에 기본값은 없다.
    L157-157 표 행 78  | 정책값=false | 수신 처리=0바이트 프레임을 거부하고, decode의 EmptySocketFrameException도 실패로 전달한다.
    L158-158 표 행 93  | 정책값=true | 수신 처리=0바이트 프레임도 decode에 넘긴다. EmptySocketFrameException이면 그 프레임을 건너뛰고 다음 프레임을 처리한다.
    L160-160 문단 67  | 내장 처리기에서 공백·개행 정리 뒤 빈 문자열이 된 프레임은 EmptySocketFrameException으로 구별한다.
§ L162-175 [25] 문자셋 기본값과 action 공통 해석의 범위   parent=224 children=2 sources=7
    L173-173 문단 86  | 내장 처리기와 action 공통 기반은 문자셋을 생략한 생성자나 null 문자셋에서 UTF-8을 사용한다. 내장 처리기 등록은 문자셋 인자 없이 수행한다.
    L175-175 문단 137  | action 공통 기반의 defaultTokenizedDecode가 사용하는 전체 문자열 속성은 rawText이다. 추가 속성에 같은 키가 있으면 추가 속성 값으로 덮어쓴다. 플러그인이 자체 decode를 구현하면 해석 규칙은 그 구현을 따른다.
SUMMARY sections=11 children=37 maxChild=144 maxParent=435
CORPUS docs=43/53 sections=622 part={admin=9, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=5, 트러블슈팅=12}
TOTAL files=43 targets=1 violations=0
```

새 절의 자식 길이는 91·94·114·69·63자, 부모 길이는 435자이다. 최종 출력에 MASK·CROSS VIOLATION은 없다. 새 문단 5개와 기존 10절을 모두 읽어 제목·조건·원천·독립 답·권한 경계·의미 중복을 확인했다. 새 절은 화면 저장값의 미반영을 소유하고, 기존 상한·문자셋 절은 실제 공급·생성 규칙을 계속 소유한다. 입력 규칙과 사용자 인계 항목은 다른 절에 복제하지 않는다. 보완 자체 확인에서 남은 지적은 0건이다.

보존 확인은 메모리에서 새 절 24줄을 빼고 H1 적용 판본·이동한 입력 절차 안내만 원래대로 되돌린 바이트의 SHA-256을 계산했다. 결과가 보완 전 SHA-256 `76468d5b5cd17542be54b85f7e24a1f99e7e8cb8f6dd1346ff09b0e24915db9b`와 일치했다. 검사 파일을 만들지 않았고 실제 검색 원문을 되돌리지 않았다. 사실 대조 37행이 최종 검색 본문의 비어 있지 않은 줄을 참조하는 것도 확인했다.

이 보완에서도 제품 시험·빌드·현장 접속·업로드·git add·commit·push·Agent 위임을 하지 않았다. 검사용 임시 파일이나 실행 자원을 만들지 않았으며 정리할 잔여는 없다. 메인 세션의 보완본 재검증은 이 자체 확인과 구별한다.

## 제품 수정 C 반영과 최종 검사 결과

2026-11-19의 제품 수정 C를 현재 작업 트리에서 직접 확인했다. 보완 전 검색 원문 SHA-256은 `6fdcfeb9912f1ec8b25418e0201e62882a1141e618c23317160712eecec46f3f`, 보완 후는 대상 표의 `af11f9d9c1703dc19bec2737ef327b36a5ff48d0ae85be7f0c3cca4594f1a120`이다. 이전 절의 실행 미사용 판정은 당시 이력으로 남기고, 현재 사실 대조 표는 이번 본문에 맞게 갱신했다.

문서 전체를 읽고 수정 A·B·D가 다루는 Kafka 보안·Secret 생성·PLC 레지스터 표기를 본문에서 다루지 않음을 확인했다. 실제로 참이 아니게 된 설명은 Socket Settings의 적용 범위, 프레임 상한 공급, 내장 처리기 문자셋 구성이다. 이 세 절의 관련 문장과 H1 적용 판본만 고쳤다. 기존 파싱·프레임 경계·종료자 부착·빈 프레임 정책·공통 해석 보조의 뜻과 기준 commit 원천은 보존했다. 확인일은 2026-11-19를 유지했다.

### 현재 원천 판본

Gateway HEAD는 `d3636ab86763`이다. 아래 파일은 시작 시 `git status --short`에서 수정 상태였고, 읽은 현재 파일 바이트를 SHA-256으로 결속했다. 검색 원문에서 바뀐 사실을 설명하는 원천에는 이 판본과 현재 줄 범위를 쓴다.

| 파일 | 현재 파일 SHA-256 |
|---|---|
| nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java | 9681a97701f5365fd7d8d27a1175fa6d02ca8dde48f92bc9b1f7900559d3e245 |
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java | 5c80140c79228f305e9c7f4d217858c02109615af83b8cd46240bc2dae78ff85 |
| nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java | f44c0a52f07c9ff1e8bcc5a849b36abf3adf0a556485743f656f39f7bce81d5c |
| nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java | 9c0d83ba7bcece4ec267007699b90ccddd24cf280f5a9a73a1ce87f721c47fde |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java | a5cd01b549f093a716eb78120c6e50937c054bf003aa54ee6111be2b6b302472 |
| nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java | 9345b43b9167bb9ead74a1f425b1b6f96d6cc0af5d5646e0aa86e5e7dc096a6d |
| nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java | cc01f2954d144807afbfa05c3a24102a5e21d252701b6aec9b817d33cdf79f36 |
| nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java | ab03104531dc8d6a4e36e9136f426ed4549d4d16df5a807b5d54ab8888d95f72 |
| nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/SocketEqpIdExtractor.java | 41d3975805fc30cdf994db37791045adffa760982555676e40d34b485af35ec1 |

실제 코드 흐름은 SocketSubProfileLoader의 저장값 전달 → SocketRuntimeContextProvider의 상한·문자셋 구성 → SocketInboundPipeline·SocketTypeHandlerResolver·SocketTypeRegistry의 처리기 선택으로 확인했다. Keep Alive는 GatewayActiveReconnectCoordinator와 EqpBindingService의 SO_KEEPALIVE 사용을 확인했다. SocketOutboundFrameEncoder와 SocketEqpIdExtractor·SocketConnectionPortStatusSyncService가 설비 문자셋을 선택 경로에 전달하는 변경도 읽었다.

Heartbeat Enabled·Heartbeat Interval·Read Timeout·Write Timeout getter를 제품 Java에서 검색하고 시험 경로는 제외했다. 호출은 DB 행 복사·하위 프로필 적재·UI 프로필 재구성에 남으며 실행 소비는 추가되지 않았다. 플러그인 처리는 SocketTypeHandlerResolver L50~60에서 그대로 반환해 설비 문자셋으로 다시 만들지 않는다. 이 문서는 내장 처리기의 적용 설명만 변경하고 플러그인 제약 문서를 복제하지 않았다.

관련 시험은 새로 실행하지 않고 기대값을 읽었다.

| 시험 파일 | 읽은 판본과 기대값 |
|---|---|
| gateway/starter/tc-gateway-starter/src/test/java/com/nori/tc/gateway/starter/runtime/context/EquipmentCommunicationSettingsTest.java | sha256=4bb74d9a959d1ed03a0cce049b41c22c51d0b35c5dd95855268c4c4c3d23c3c5 ; L74~109의 설비 상한·EUC-KR 송수신·잘못된 문자셋/상한·플러그인 전용 종류 기대값을 읽었다. |
| gateway/tc-gateway-socket/src/test/java/com/nori/tc/gateway/socket/pipeline/SocketEquipmentCharsetTest.java | sha256=fefe2c47367133253fda2e27f4ca20eeebec1267203ba21e0d61fabbf15ccb70 ; 설비 문자셋 수신·상한 초과·기존 생성 계약·플러그인 문자셋 불변 기대값을 읽었다. |
| gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/EquipmentSocketOptionsTest.java | sha256=abd03f404ea22b9c159a8a1b9b3d9e6d15a42603b8c7b4ffa51bba277f82cf91 ; 설비 식별 후 문자셋 사용과 ACTIVE/PASSIVE Keep Alive true/false 기대값을 읽었다. |

### 검사 실행과 원천 조회 오류의 구분

원래 Temp 아래 검사기 경로는 현재 존재하지 않았다. 작업 자료의 이동된 검사기 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`와 실제 Java 코드를 읽고 그대로 실행했다. 검사기 코드는 바꾸지 않았다.

첫 실행은 exit 1, `TOTAL files=51 targets=1 violations=35`였다. 내용·길이·마스킹 문제가 아니라 모든 commit 원천 조회가 실패한 결과였다. 예시는 `원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 화면 항목 L1269~1371'`이다.

정확한 원인 확인을 위해 동일한 Git 객체를 조회했다.

- `git -C ../nori-tc-gateway cat-file -e d3636ab86763:gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/config/SocketTypeConfig.java`: 실제 종료코드 128, 원문 오류 `fatal: detected dubious ownership in repository at 'C:/workspace_nori/nori-tc/nori-tc-gateway'`.
- 같은 명령에 `-c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway`를 지정한 조회: 실제 종료코드 0. 객체 부재가 아닌 소유자 검사에 따른 조회 실패임을 구별했다.

재실행은 이 프로세스에만 GIT_CONFIG_COUNT=5와 safe.directory 키 5개를 전달했다. 지정한 경로는 `C:/workspace_nori/nori-tc/` 아래 nori-tc-gateway·nori-tc-gateway-action·nori-tc-ui-front·nori-tc-ui-backend·nori-tc-exec 다섯 저장소다. 전역 Git 설정·원천 문서·검사기·권한 정책을 변경하지 않았다.

최종 검사 명령:

```text
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/socket-framing-and-parsing-reference.md
```

최종 종료코드는 0이다. 검색 원문의 마지막 편집 뒤 실행한 마지막 출력 10줄:

```text
    L157-157 표 행 78  | 정책값=false | 수신 처리=0바이트 프레임을 거부하고, decode의 EmptySocketFrameException도 실패로 전달한다.
    L158-158 표 행 93  | 정책값=true | 수신 처리=0바이트 프레임도 decode에 넘긴다. EmptySocketFrameException이면 그 프레임을 건너뛰고 다음 프레임을 처리한다.
    L160-160 문단 67  | 내장 처리기에서 공백·개행 정리 뒤 빈 문자열이 된 프레임은 EmptySocketFrameException으로 구별한다.
§ L162-181 [25] 문자셋 기본값과 action 공통 해석의 범위   parent=295 children=3 sources=11
    L177-177 문단 59  | 내장 처리기와 action 공통 기반은 문자셋을 생략한 생성자나 null 문자셋에서 UTF-8을 사용한다.
    L179-179 문단 97  | 이 수정이 들어간 판본의 내장 LINE·REGEX 처리기는 설비 문자셋으로 구성한다. 설비 Socket 설정이 없는 경로와 설비 식별 전 연결 초기 구간은 UTF-8을 쓴다.
    L181-181 문단 137  | action 공통 기반의 defaultTokenizedDecode가 사용하는 전체 문자열 속성은 rawText이다. 추가 속성에 같은 키가 있으면 추가 속성 값으로 덮어쓴다. 플러그인이 자체 decode를 구현하면 해석 규칙은 그 구현을 따른다.
SUMMARY sections=11 children=39 maxChild=138 maxParent=408
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

최종 대상은 11절·39자식이며 최대 자식 138자·부모 408자다. MASK·CROSS VIOLATION은 없다. 11절 전체를 다시 읽었고, 바뀐 문단은 현재 코드·원천 SHA·줄 범위·조건을 직접 대조했다. 다른 절의 값·설명과 충돌하지 않으며 입력 규칙은 기존 소유 문서 안내를 유지한다. 자체 확인에서 미해소 지적은 0건이다.

### 다른 문서의 직접 영향

`00.rag/user/socket-troubleshooting.md` L23·L30은 줄 단위 문자셋 문제를 “시작되지 않을 때”로 설명한다. 처리기 생성자의 UTF-16 거절 자체는 유지되지만 수정 C에서는 설비 설정 문맥이 생성된 뒤, 첫 수신 처리에서 SocketInboundPipeline L107~108 → SocketTypeHandlerResolver L64~68 → SocketTypeRegistry L51~63으로 내장 처리기를 만들 때 거부된다. EqpSequentialProcessor L208~213의 수신 처리 호출은 L241~243의 예외·DLQ·격리 경로에 연결된다. 존재하지 않는 문자셋 이름은 GatewayEquipmentInfo L55~64와 SocketRuntimeContextProvider L45에서 문맥 생성에 실패하므로 두 실패 시점을 구별할 보완이 필요하다. 이 문서는 수정하지 않고 결과 JSON에 알린다.

이번에도 제품 코드·다른 문서·검사기·전역 설정을 수정하거나 git add·commit·push·업로드를 하지 않았다. 검사 2회와 Git 조회 대조를 수행했고 제품 시험·빌드는 실행하지 않았다. 독립 Agent 호출은 0회다. 전체 wall time·provider 사용량·비용은 별도 계측하지 않았다. 검사용 임시 파일이나 실행 서비스를 만들지 않았고 실행한 검사 프로세스는 종료되었다. 최종 결과는 지정한 두 Markdown 파일에만 보존한다.


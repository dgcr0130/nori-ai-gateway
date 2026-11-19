# Socket 작성 기록

## 대상

- objectKey: `00.rag/user/socket-troubleshooting.md`
- 검색 원문: `rag-documents/nori-tc/00.rag/user/socket-troubleshooting.md`
- 구획·권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 확인일: 2026-11-19, 한국 시간
- 검색 원문 SHA-256: `5b691981900938edabc025895c4ed6b481d6db3f45e18f87170bacf7efea2d0b`
- 검색 원문 크기: 9,489바이트, 81줄, H1 1개, H2 5개, 본문 자식 16개
- 보완 전 기준선: 검색 원문 `fa70c5420c82e94179e24e71351949ba0195b60e114f5181701f24b9900151d2`·71줄, 작성 기록 `fb19692c8cd2371d21ec6b8bdd23a9f9439d5e63d3a7dd12e8617dded2ba8798`·194줄과 실제 파일의 일치를 확인했다.
- 수행 범위: 지정 문서 하나의 조사·작성·자체검사. 독립 Agent 호출 0회. 제품 코드·설정·SPEC·다른 검색 문서 변경, git add·commit·push, 업로드·네트워크 접속은 수행하지 않았다.

## 원천 판본

아래 저장소 표와 수정 커밋 기록은 최초 작성 시점의 근거다. 2026-11-19 제품 수정 C 보완의 작업 트리 판본은 이어지는 별도 표로 구별한다.

작성 전에 `docs/rag-document-writing-rules.md`와 `docs/tc-rag-document-list.md` 전체를 읽었다. 수정 커밋 세 건의 diff와 추가된 시험을 읽고, 현재 HEAD의 실제 구현·호출 경로·시험 기대값을 별도로 대조했다. 과거 결함을 현재 동작으로 기술하지 않았다.

| 저장소 | 확인한 HEAD | 판본·작업 트리 확인 |
|---|---|---|
| nori-ai-gateway | `70cf6cd84db6` | main, origin/main 대비 ahead 8, 기존 `rag-documents/` 미추적 상태를 보존했다. worktree는 현재 제품 저장소 하나였다. 작성 규칙·목록의 `git status --short -- <경로>` 출력은 비어 있었다. |
| nori-tc-gateway-action | `f4fdfd6d27ce` | 아래 공통 기반 2개와 회귀 시험 3개의 경로 한정 `git status --short --` 출력이 비어 있었다. 본문은 commit 판본으로 인용했다. |
| nori-tc-gateway | `d3636ab86763` | 아래 기본 처리기 2개·채널 처리·연결 동기화 시험의 경로 한정 상태 출력이 비어 있었다. 보조로 읽은 수신 파이프라인·처리기 선택 코드도 상태 출력이 비어 있었다. |
| nori-tc-ui-front | `9fe69a75dcd3` | 기존 문서와 화면 명칭 중복을 살필 때 `EqpInfoTable.tsx` L113~143을 읽었으며, 해당 경로 상태 출력은 비어 있었다. 새 본문에는 이 화면의 조작·표시 사실을 넣지 않았다. |

명령은 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C ../<저장소> rev-parse --short=12 HEAD`, 같은 저장소의 `status --short -- <파일 목록>`, `show <수정 커밋> --`를 사용했다. 최초 형제 저장소 Git 조회는 다른 Windows 소유자라는 오류로 거부되어 결과 근거로 사용하지 않았다. 읽기 조회에만 정확한 저장소 경로의 일회성 `safe.directory`를 지정했다. 전역 설정은 바꾸지 않았다. Git의 사용자 ignore 파일 접근 경고가 있었으나 대상 추적 소스의 상태 조회는 exit 0이었다.

본문 근거 파일:

- `nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java`
- `nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java`
- `nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedByteBoundaryTest.java`
- `nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedEndRegexValidationTest.java`
- `nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedCharsetGuardTest.java`
- `nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java`
- `nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java`
- `nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java`
- `nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandlerConnectionSyncTest.java`

호출 관계 보조 대조:

- `nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 L92~156`: 실제 처리기를 선택한 뒤 프레임 추출과 해석을 반복한다. `null`은 추출 반복을 끝내며, 빈 프레임 허용과 해석 예외는 별도 경로다.
- `nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 L31~62`: 설비별 플러그인이 있으면 우선 선택하며, 없으면 기본 registry를 사용한다. 따라서 임의 플러그인 전체에 공통 기반의 수정 효과를 일반화하지 않았다.
- `GatewayChannelHandler.channelRead` → `remainderAfterInitializeCommand` → `respondInitializeRepAndBindActive` → `completeActiveBindAfterInitializeRep` → `replayUnboundPayloadAfterBind` → `GatewayIngressService.enqueueInbound`를 현재 코드에서 추적했다.

수정 이력의 사용 범위:

| 수정 커밋 | 읽은 diff·시험과 현재 코드에서 확인한 범위 |
|---|---|
| action `8d5fac0bff62` | 정규식 공통 기반이 원본 바이트 위치로 소비 길이를 구한다. 해석 불가 바이트와 끝의 미완성 문자를 구별한다. 줄 단위 공통 기반은 LF의 단일 바이트 여부를 생성 시 검사한다. |
| action `4a3484c7f970` | 빈 문자열에 매치되는 종료 정규식을 생성 시 거부한다. 실제 입력에서 매치 끝 위치가 0이면 프레임 대신 예외를 내고 버퍼를 소비하지 않는다. |
| gateway `2d56c2b7e947` | ACTIVE Socket의 INITIALIZE 뒤 나머지를 보관하고 연결 배정 후 재주입한다. 시험은 UTF-8의 INITIALIZE와 업무 프레임이 함께 온 경우와 INITIALIZE 단독을 구별한다. |

### 2026-11-19 제품 수정 C 보완 판본

시작 시 main·worktree 하나와 기존 제품·설정·문서의 여러 변경을 확인해 보존했다. Gateway HEAD는 `d3636ab86763`, action HEAD는 `f4fdfd6d27ce`였다. 변경 사실의 파일은 `git status --short -- <정확한 경로>`로 수정 상태를 확인한 뒤 `Get-FileHash -Algorithm SHA256`으로 현재 바이트를 결속했다. 아래 Gateway 파일은 작업 트리 수정 상태 M이다.

| Gateway 상대경로 | 현재 SHA-256 | 읽은 위치와 용도 |
|---|---|---|
| `gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java` | `5c80140c79228f305e9c7f4d217858c02109615af83b8cd46240bc2dae78ff85` | L38~63: 설비 문자셋을 문맥 설정으로 전달한다. |
| `gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java` | `a5cd01b549f093a716eb78120c6e50937c054bf003aa54ee6111be2b6b302472` | L55~64: 잘못된 문자셋은 설비 ID·입력 문자셋을 담은 예외로 실패한다. |
| `gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java` | `f44c0a52f07c9ff1e8bcc5a849b36abf3adf0a556485743f656f39f7bce81d5c` | L51~63: 설비 문자셋으로 내장 LINE·REGEX를 구성한다. |
| `gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java` | `9c0d83ba7bcece4ec267007699b90ccddd24cf280f5a9a73a1ce87f721c47fde` | L41~71: 플러그인이 있으면 문자셋을 넘기지 않고 반환한다. |
| `gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java` | `9345b43b9167bb9ead74a1f425b1b6f96d6cc0af5d5646e0aa86e5e7dc096a6d` | L106~114·L246~253: 수신 drain에서 처리기를 선택·생성한다. |
| `gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java` | `2242e0c28ce4929b2d3b8a71ab811a918c7b24403fc5116fed7f8cc276258203` | L211~239: ACTIVE 초기화는 설비 문자셋으로 읽고 디코더 소비 위치로 원본 나머지를 잘라 낸다. |
| `gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/SocketEqpIdExtractor.java` | `41d3975805fc30cdf994db37791045adffa760982555676e40d34b485af35ec1` | L81~90: 설비 ID가 없으면 UTF-8, 있으면 등록 설비 문자셋을 읽는다. |

읽은 시험 파일도 경로 한정 상태·해시를 확인했다. 시험은 이번 문서 세션에서 실행하지 않았다.

| 시험 파일 | 상태·SHA-256 | 읽은 기대값 |
|---|---|---|
| `gateway/starter/tc-gateway-starter/src/test/java/com/nori/tc/gateway/starter/runtime/context/EquipmentCommunicationSettingsTest.java` | 미추적, `4bb74d9a959d1ed03a0cce049b41c22c51d0b35c5dd95855268c4c4c3d23c3c5` | L74~104: 내장 LINE·REGEX의 EUC-KR 공급·해석·송신, 잘못된 문자셋의 문맥 생성 실패·설비 ID 포함. |
| `gateway/tc-gateway-socket/src/test/java/com/nori/tc/gateway/socket/pipeline/SocketEquipmentCharsetTest.java` | 미추적, `fefe2c47367133253fda2e27f4ca20eeebec1267203ba21e0d61fabbf15ccb70` | L25~44·L59~76: 수신 파이프라인의 설비 문자셋, 플러그인 우선·변경 없음. |
| `gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandlerConnectionSyncTest.java` | M, `25b2ab40d1759e6eb22bb2d672d114c5f0696a6027601d64a2dda3a28b0b181c` | L243~262: EUC-KR·UTF-16 초기화와 함께 온 업무 나머지의 원본 바이트 일치. |

`EqpSequentialProcessor.java`는 상태 출력이 비어 있어 HEAD `d3636ab86763`을 인용했다. L208~243의 업무 수신 예외 → L286~333의 PARSING_FAILED 기록·격리 시도·버퍼 비우기를 직접 대조했다. action 공통 기반 두 파일도 상태 출력이 비어 있어 기존 commit 원천을 유지했다. 초기화 응답 생성은 `SocketEqpIdExtractor.java` 현재 SHA-256 판본의 L330~347에서도 처리기를 선택한다. 이때의 생성 실패는 `GatewayChannelHandler`의 응답 생성 실패·연결 종료 분기이므로, DLQ·격리 설명은 첫 업무 수신에서 처리기를 만드는 경로로 한정했다.

이번 최소 변경은 H1 적용 메타, 문자셋 오류 절의 제목·거부 시점과 직접 연결된 진단 조건, ACTIVE 초기화의 UTF-8 문장과 그 원천 추가다. 본문 1·3·5절이 보완 전 문자열과 동일하고, 기존 `원천:` 줄 전부가 남아 있음을 메모리 대조로 확인했다. 1절의 해석 불가·미완성 문자 구별, 3절의 정규식 검사, 5절의 실패 시 연결 종료는 그대로다. 문서 전체에는 A의 Kafka 보안, B의 Secret linger, D의 PLC Order·migration, C의 Keep Alive·프레임 상한 수치를 주장하는 문장이 없어 그 범위는 고치지 않았다.

## 절별 사실 대조

명령형 문장은 코드가 자동으로 수행하는 사용자 기능이라는 뜻이 아니라, 해당 분기를 구별할 때 운영·설비·모델 담당자에게 요청할 진단 정보다. 소스의 실패 조건·입력·소비 위치에 근거해 필요한 정보만 골랐다. 아래 표는 검색 원문의 모든 본문 문장을 문장별로 대조한다.

### 한글 수신 뒤부터 Socket 프레임 경계가 어긋날 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 17 첫 문장 | 원본 바이트 수만 소비한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L97~105 L139~154; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L145~154 | 원본에서 복사하고 같은 바이트 길이만 discard한다. 부분 문자열 재인코딩 길이를 사용하지 않는다. |
| 17 둘째 문장 | 여러 바이트 문자가 있어도 다음 프레임을 남긴다. | nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedByteBoundaryTest.java @ f4fdfd6d27ce L27~40 | 한글 포함 첫 프레임의 원본 일치와 두 번째 프레임의 보존을 assert한다. 시험 소스의 기대값을 읽었으며 시험 실행 PASS를 주장하지 않았다. |
| 19 첫 문장 | 종료자 앞 해석 불가 바이트는 추출 실패다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L74~86; nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedByteBoundaryTest.java @ f4fdfd6d27ce L42~52 | 유효 접두부에 종료자가 없고 malformed이면 예외를 낸다. 실패 시 부분 소비하지 않는 기대값도 확인했다. |
| 19 둘째 문장 | 끝의 미완성 문자·종료자 미도달은 다음 수신 대기다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L77~86 L117~126; nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedByteBoundaryTest.java @ f4fdfd6d27ce L54~66 | decoder의 endOfInput=false와 추출 null, 버퍼 보존 기대값을 대조했다. |
| 19 셋째 문장 | 위 두 경우를 구별한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L77~86 L117~126 | 예외와 null의 실제 분기에 따른 진단 지침이다. |
| 21 첫 문장 | 설비·시각·메시지와 바이트·문자셋·소비 길이 대조를 인계한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L74~105 L117~154 | 입력 snapshot, charset, 계산된 frameByteLength가 경계 판정의 직접 입력·결과다. 사용자에게 내부 저장소·설정 파일 조작을 요구하지 않았다. |
| 21 둘째 문장 | 플러그인 공통 기반 사용·수정 포함 여부를 확인 요청한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L29~55 L97~105; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 L31~62 | abstract 기반의 수정이며 플러그인이 기본 처리기를 대체할 수 있다. 수정 `8d5fac0b` diff와 대조한 판본 인계다. |

### Socket 문자셋 오류가 준비 중 또는 첫 수신에서 날 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 36 첫 문장 | LF가 단일 0x0A인 문자셋만 허용한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L28~38; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L54~64 | LF 인코딩 길이 1 및 값 0x0A를 검사한다. |
| 36 둘째 문장 | 설비 문자셋 적용 수정 이후 내장 줄 단위 처리기는 UTF-16·UTF-16LE를 첫 업무 수신의 처리기 생성에서 거부하며 DLQ·격리 경로로 간다. | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ a5e5221853b7 L106~114 L246~253; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 L51~63; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 L54~64; nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java @ d3636ab86763 L208~243 L286~333 | 문맥은 Charset 객체를 담고, drain이 resolver·registry를 통해 LINE 생성자를 호출할 때 LF 가드가 실패한다. 예외는 PARSING_FAILED의 DLQ 발행·설비 격리 시도와 버퍼 비우기로 이어진다. 원본 action 시험의 두 문자셋 거부 기대값도 그대로 유효하다. |
| 38 첫 문장 | 존재하지 않는 문자셋은 설비 ID 포함 오류로 문맥 생성에서 실패한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 L38~63; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java @ a5e5221853b7 L55~64 | create에서 socketCharset을 먼저 호출한다. Charset.forName 실패는 설비 ID·문자셋 입력을 포함한 오류로 나온다. EquipmentCommunicationSettingsTest L94~104의 기대값과도 대조했다. |
| 38 둘째 문장 | 플러그인에는 설비 문자셋을 전달하지 않는다. | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ a5e5221853b7 L41~71 | 플러그인 lease가 있으면 handler를 그대로 반환하고 설비 문자셋을 받는 registry 호출은 하지 않는다. SocketEquipmentCharsetTest L59~76의 같은 handler 반환 기대값을 확인했다. |
| 40 첫 문장 | 실패 정보와 프로토콜·처리 문자셋 확인을 인계한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L28~38 | 오류 발생 조건인 문자셋·프레이밍 조합을 담당자가 확인하도록 요청하는 지침이다. |
| 40 둘째 문장 | 설비 송신 규격에 맞는 조합을 담당자와 대조한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L30~35 L56~74 | LF 바이트 탐색의 전제와 다른 송신 규격을 임의로 맞추지 않도록 인계했다. 특정 제조사 규격이나 추천 문자셋을 사실로 만들지 않았다. |
| 42 첫 문장 | 공통 기반 문자셋 검사는 수정 이후 동작이다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/line/AbstractLineDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L28~38 | 현재 코드와 수정 `8d5fac0b`의 생성자 변경을 대조했다. 현장 적용을 단정하지 않는다. |
| 42 둘째 문장 | 프레임·해석 설정은 별도 참조 문서 소유다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서의 Socket 프레임·파싱 규칙 행 | 확정 objectKey로 안내한다. 설정 키·모델 파싱 계약을 이 문서에 복제하지 않았다. |

### 종료 정규식 매치가 반복되고 처리가 멈출 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 51 첫 문장 | endRegex의 빈 문자열 매치 여부를 확인한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L41~55; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/regexDelimited/RegexDelimitedSocketTypeHandler.java @ d3636ab86763 L77~92 | 두 구현의 compiled.matcher(빈 문자열).find() 검사를 확인했다. |
| 51 둘째 문장 | 두 빈 매치 패턴은 생성 시 거부된다. | nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedEndRegexValidationTest.java @ f4fdfd6d27ce L21~27 | 본문에 사용한 정규식 두 개의 실제 생성자 거부 기대값을 대조했다. |
| 53 첫 문장 | 실제 입력 위치 0의 길이 0 매치는 추출 실패다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L89~95; nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedEndRegexValidationTest.java @ f4fdfd6d27ce L37~45 | ^(?=A)와 ABC 입력의 예외 기대값을 읽었다. 모든 위치의 zero-width 매치를 거부한다고 일반화하지 않았다. |
| 53 둘째 문장 | 그 실패에서는 버퍼를 소비하지 않는다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L89~104; nori-tc-gateway-action/src/test/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedEndRegexValidationTest.java @ f4fdfd6d27ce L41~45 | discard보다 예외가 앞서며 잔여 길이 3의 기대값이 있다. |
| 53 셋째 문장 | 생성 거부와 수신 중 실패를 구별한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L41~55 L89~95 | 검사 시점과 예외 발생 경로를 구별하는 지침이다. |
| 55 첫 문장 | 정규식·메시지·시각·매치 끝·소비 여부를 인계한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L74~105 | endPattern, matcher.end, discard가 실제 원인 판별 지점이다. |
| 55 둘째 문장 | 모델 담당자에게 종료자 규칙 대조를 요청한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L41~55 L77~95 | 프레임 규칙 입력을 대조하도록 하는 인계 지침이며, 화면에 endRegex 편집 칸이 있다고 쓰지 않았다. |
| 55 셋째 문장 | 수정 이후 방어·적용 JAR·공통 기반 여부를 확인한다. | nori-tc-gateway-action/src/main/java/com/nori/tc/gateway/action/support/regex/AbstractRegexDelimitedSocketTypeHandler.java @ f4fdfd6d27ce L41~55 L89~95; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 L31~62 | 수정 `4a3484c7` diff와 현재 방어를 대조했다. 임의 사용자 플러그인·배포 판본의 수정 포함을 단정하지 않는다. |

### 연결 직후 첫 Socket 업무 메시지만 빠질 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 65 첫 문장 | ACTIVE의 연결 배정 전 같은 수신 묶음인지 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L194~195 L366~405 | BOUND 분기 뒤 ACTIVE outbound Socket의 INITIALIZE 분기가 실행된다. |
| 65 둘째 문장 | 수정이 들어간 판본의 ACTIVE 초기화는 설비 문자셋과 CR·LF를 사용한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ a5e5221853b7 L211~239; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/SocketEqpIdExtractor.java @ a5e5221853b7 L81~90 | presetEqpId가 있는 ACTIVE는 등록 설비 문자셋을 조회한다. ID가 없는 초기 연결만 UTF-8이다. 토큰 직후 CR/LF를 건너뛰며 나머지는 원본 바이트에서 복사한다. GatewayChannelHandlerConnectionSyncTest L243~262의 EUC-KR·UTF-16 기대값도 대조했다. |
| 67 첫 문장 | 수정 후 나머지 보관·전송과 연결 배정 성공 후 전달한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L211~224 L252~267 L276~304 L391~405 L876~892 | 수정 `2d56c2b7` diff와 현재 호출 순서를 대조했다. enqueueInbound 호출까지가 기술한 범위이며 후속 업무 완료를 보장하지 않는다. |
| 67 둘째 문장 | INITIALIZE 단독이면 업무 전달 대상이 없다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandlerConnectionSyncTest.java @ d3636ab86763 L229~240 | INITIALIZE 단독 입력에서 enqueueInbound가 호출되지 않는 기대값을 확인했다. |
| 69 첫 문장 | 설비·연결 시각·빠진 메시지·다음 정상 메시지를 인계한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandlerConnectionSyncTest.java @ d3636ab86763 L212~226 | 같은 입력의 초기화·업무 분리와 후속 전달을 대조하기 위한 증상 정보다. |
| 69 둘째 문장 | 같은 수신 묶음·연결 후 전달·현장 수정 포함 확인을 요청한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L391~405 L299~304 L876~892 | 코드에서 원인을 가르는 수신·연결 배정·재주입 세 위치를 지목했다. 현장 판본 값은 알 수 없어 인계로 썼다. |
| 69 셋째 문장 | 화면 로그 조회 절차는 별도 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서의 메시지 전송 결과·통신 로그 조회 행 | 기존 `00.rag/user/message-results-and-communication-logs.md`의 소유 범위를 확인했다. 로그 화면이 원본 바이트까지 모두 제공한다고 쓰지 않았다. |

### INITIALIZE 뒤 연결이 닫혀 Socket 업무가 오지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 77 첫 문장 | 응답 생성·전송 실패·연결 배정 거부는 연결을 닫는다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L240~290 | encode catch, write 실패, BindResult 비정상의 close를 확인했다. |
| 77 둘째 문장 | 보관 공간 초과·배정 뒤 나머지 추출 실패도 종료한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L391~405 L847~874 | unboundInbox.offer 실패와 drainAllBytesAndClear 예외 경로에서 close한다. |
| 79 첫 문장 | 설비·초기화 시각·종료 시각·화면 결과를 인계한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L240~290 L391~405 | 최초 유실과 연결 종료를 분리하기 위한 인계 정보다. 화면의 새 기능이나 자동 진단을 주장하지 않는다. |
| 79 둘째 문장 | 전송·연결 배정·나머지 전달 중 진행 위치를 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L252~304 L876~892 | 송신 성공 listener 이후 bind, bind 성공 이후 replay, 마지막 enqueueInbound의 실제 순서다. |
| 79 셋째 문장 | 연결 완료 전 실패와 연결 후 첫 메시지만 빠짐을 구별한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L286~304 L391~405 | 앞의 종료 분기와 연결 성공 후 재주입 경로를 구별하는 지침이다. |
| 81 | 공통 연결·명령 문제는 별도 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서의 설비 연결·명령 행 | `00.rag/user/equipment-connection-and-command-troubleshooting.md`를 읽고 공통 명령·연결 절차를 복제하지 않았다. |

## 매뉴얼과 코드가 다른 곳

이 문서의 지정 원천은 수정 커밋 세 건과 관련 현재 제품 코드·시험이다. 매뉴얼의 동작 설명을 본문 근거로 채택하지 않았으므로 보고할 매뉴얼·코드 충돌은 없다. 매뉴얼 전부와 제품 전부의 일치를 확인했다는 뜻은 아니다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 배포 판본의 세 수정 포함 여부·적용 JAR·실제 문자셋·실제 오류 발생 원인 | 저장소 코드만으로 현장 판본·입력·상태를 확인할 수 없다. 본문에는 배포 적용을 단정하는 대신 원인별 판본·입력 대조를 인계했다. | 운영 담당자·설비 담당자 |
| 제조사별 종료자·문자셋·명령 규격과 특정 설정 조합의 권장값 | 해당 제조사 원본과 현장 입력을 제공받지 않았다. 임의로 UTF-8로 바꾸거나 재시작하면 해결된다고 쓰지 않았다. | 설비 담당자·모델 담당자 |
| 임의 사용자 플러그인의 공통 기반 상속·동일 방어 적용 | 플러그인이 기본 처리기를 대체할 수 있으며 현장 JAR 구현은 조사하지 않았다. 공통 기반 또는 기본 구현이라는 적용 조건만 썼다. | 플러그인 개발 담당자·운영 담당자 |
| INITIALIZE가 여러 수신 묶음으로 분할되거나 임의 바이너리를 동반해도 항상 무손실이라는 보장 | 수정 C의 분리 코드는 설비 문자셋으로 토큰을 찾고 CR/LF 뒤 나머지를 원본에서 복사한다. 읽은 추가 시험은 같은 묶음의 EUC-KR·UTF-16 텍스트다. 분할·임의 바이너리까지 보장을 확대하지 않았다. | Gateway 개발 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 넣지 않은 사실 |
|---|---|
| `00.rag/user/socket-framing-and-parsing-reference.md` | 프레임 방식·종료자·해석 설정의 전체 참조 계약. 본문에는 실패를 구별하는 최소 조건만 남겼다. 최초 작성 때는 미작성 상태였으며, 이번에는 현재 참조 문서의 설비 문자셋 반영·기본값 절과 대조했다. |
| `00.rag/user/equipment-connection-and-command-troubleshooting.md` | Enabled 색 점, 명령 결과·조회 오류·재연결·JAR reload의 공통 진단. |
| `00.rag/user/message-results-and-communication-logs.md` | 화면 로그 열기·자동 갱신·조회 범위·권한·오류 단추·전송 결과 확인 절차. |
| `00.rag/user/equipment-registration-and-settings.md` | Socket Settings 입력·저장 필수값·Socket Protocol 열 위치·API 권한·저장 실패 절차. |
| `00.rag/user/model-details-and-flow-editing.md` | Socket Message 표와 Message Flows의 편집·저장 절차. |
| `00.rag/user/dlq-quarantine-and-repair-task.md` | 프레임·파싱 사유 코드, 격리 설비·Repair Task 화면 조회와 조치. |
| `00.rag/user/error-message-and-code-reference.md` | 오류 문구·코드별 전체 뜻풀이. 정확한 오류 로그 문장 복제를 넣지 않고 발생 단계 대조에 집중했다. |

위 관련 기존 문서와 `secs-hsms-troubleshooting.md`도 읽고 Socket과 무관한 HSMS SELECT·타이머·설비 식별 경로를 넣지 않았다. 다른 문서에서 수정이 필요한 확정 결함은 발견하지 않았다.

## 검사 결과

### 최초 작성 검사 이력

기계 검사 명령(Git Bash 실행, cwd는 nori-ai-gateway):

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/socket-troubleshooting.md
```

실제 PowerShell 호출은 `& 'C:/Program Files/Git/usr/bin/bash.exe' '<위 check.sh 절대경로>' --dump 00.rag/user/socket-troubleshooting.md`였다. 도구는 PowerShell 5.1.26100.9444, Git 2.52.0.windows.1, GNU Bash 5.2.37(1)-release이다. 검사기는 제공된 것을 그대로 사용했다. Java 버전은 별도로 측정하지 않았다.

- 검사 호출 3회. 최초 호출은 실행 세션 반환을 출력하는 과정의 처리 오류로 결과를 회수하지 못해 성공 근거로 사용하지 않았다. 다음 호출은 exit 0, 위반 0건이었다. 이후 INITIALIZE의 UTF-8·CR/LF 조건을 명확히 편집했으므로 마지막 검사로 다시 확인했다.
- 최종 회수한 세션 2098의 검사 종료코드 0. SHA-256 `fa70c5420c82e94179e24e71351949ba0195b60e114f5181701f24b9900151d2`, 71줄, 5절, 15자식, maxChild 188, maxParent 440, violations 0.
- `CROSS VIOLATION` 출력 0건, `MASK` 출력 0건. 검사 출력에서 직렬화된 모든 문단과 원문을 비교했다.
- 마지막 편집 뒤 원문 SHA·줄 수·절 수를 별도 PowerShell `Get-FileHash`와 LF·H2 계산으로 다시 대조했다.
- 최종 검사 도구 대기 측정은 시작 호출 10.006초, 결과 회수 2.997초였다. 요청 전체 소요 시간·provider 사용량·비용은 별도로 측정하지 않아 미확인이다. 추가 독립 AI 호출은 없었다.
- 제품 회귀 시험은 코드·기대값을 읽었으며 실행하지 않았다. 문서 작업에 제품 빌드·Docker·네트워크·공식 Validation을 추가하지 않았다.

최종 검사 마지막 출력 10줄:

```text
    L55-55 문단 106  | ACTIVE Socket에서 설비 연결 배정 전에 CMD=INITIALIZE와 첫 업무 프레임이 같은 수신 묶음으로 왔는지 확인한다. 이 경로는 UTF-8 문자와 CR·LF 구분을 사용한다.
    L57-57 문단 144  | Gateway 2d56c2b7 수정 이후 이 경로는 INITIALIZE 뒤의 CR·LF를 건너뛴 나머지를 보관하고, INITIALIZE_REP 전송과 연결 배정이 성공하면 업무 수신 처리에 넘긴다. INITIALIZE만 받은 경우는 넘길 업무 데이터가 없다.
    L59-59 문단 188  | 설비 ID·연결 시각·빠진 첫 메시지·다음 정상 메시지를 운영 담당자에게 전달한다. 같은 수신 묶음이었는지, 연결 배정 뒤 남은 데이터가 전달됐는지, 현장 Gateway 판본에 수정이 포함됐는지 확인을 요청한다. 화면 로그 조회는 00.rag/user/message-results-and-communication-logs.md를 따른다.
§ L61-71 [38] INITIALIZE 뒤 연결이 닫혀 Socket 업무가 오지 않을 때   parent=358 children=3 sources=2
    L67-67 문단 123  | ACTIVE Socket의 INITIALIZE 경로는 응답 생성 실패, 응답 전송 실패, 연결 배정 거부 때 연결을 닫는다. 첫 업무 데이터 보관 공간이 넘치거나 배정 뒤 남은 데이터를 꺼내는 데 실패해도 연결을 닫는다.
    L69-69 문단 145  | 운영 담당자에게 설비 ID·INITIALIZE 시각·연결이 닫힌 시각·화면 결과를 전달한다. 응답 전송 성공, 연결 배정 성공, 남은 데이터 전달 중 어디까지 진행됐는지 확인을 요청한다. 연결이 완료되기 전 실패와, 연결 뒤 첫 메시지만 빠진 증상을 구별한다.
    L71-71 문단 88  | 연결·명령 결과 자체의 점검은 00.rag/user/equipment-connection-and-command-troubleshooting.md에서 확인한다.
SUMMARY sections=5 children=15 maxChild=188 maxParent=440
CORPUS docs=29/53 sections=451 part={admin=7, user=22} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=5}
TOTAL files=29 targets=1 violations=0
```

### 수정 C 반영 최종 검사

2026-11-19 마지막 검색 원문 편집 뒤 실행했다. 기존 임시 경로의 검사기는 존재하지 않았고, 이전된 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`를 읽어 같은 RagDocCheck 실행임을 확인했다. 검사기는 고치지 않았다.

```text
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/socket-troubleshooting.md
```

실제 호출은 PowerShell에서 `C:/Program Files/Git/usr/bin/bash.exe`로 위 스크립트를 실행했다. 검사기의 원천 commit 조회에 필요한 `GIT_CONFIG_COUNT=1`, `GIT_CONFIG_KEY_0=safe.directory`, `GIT_CONFIG_VALUE_0=*`를 해당 프로세스 환경에만 설정했다. 전역 Git 설정은 바꾸지 않았다.

- 이번 검사 첫 호출은 세션 76238에서 종료코드 0·위반 0건이었다. 그 뒤 DLQ·격리 분기를 첫 업무 수신으로 명확히 한정해 마지막 편집 후 두 번째 검사를 실행했다. 이전 PASS를 바뀐 본문의 검사로 재사용하지 않았다.
- 최종 세션 91649의 종료코드 0, 위반 0건, `CROSS VIOLATION`·`MASK` 출력 없음. 최종 SHA-256은 `5b691981900938edabc025895c4ed6b481d6db3f45e18f87170bacf7efea2d0b`, 81줄·5절·16자식, 최대 자식 188자·최대 부모 448자다. 마지막 편집 뒤 검사 결과를 회수하고 두 파일의 SHA-256·줄 수·절 수와 BOM·CR·NUL·NFC를 다시 확인했다.
- 보완 전·후 본문을 메모리에서 대조했다. 1·3·5절 문자열은 동일하고 기존 원천 줄 전부가 유지됐다. 새 사실은 작업 트리 SHA-256과 실제 줄에 연결했다. 작성 기록의 본문 줄 번호도 이번 81줄 판본에 맞췄다.
- 이번 문서 세션은 제품 시험을 다시 실행하지 않았다. 현재 코드와 시험 기대값을 읽은 자체 사실 대조이며 메인 세션의 실행 시험·독립 검증을 대신하지 않는다. 빌드·서비스·네트워크·업로드·커밋은 실행하지 않았다.
- 독립 AI 호출 0회. 첫 검사 시작 호출의 도구 대기는 10.005초였다. 마지막 검사 시작 호출은 10.010초, 첫 회수 호출은 5.005초로 측정됐다. 요청 전체 wall time·provider 사용량·비용은 별도 측정하지 않아 미확인이다.

최종 검사 마지막 출력 10줄:

```text
    L65-65 문단 110  | ACTIVE Socket에서 설비 연결 배정 전에 CMD=INITIALIZE와 첫 업무 프레임이 같은 수신 묶음으로 왔는지 확인한다. 이 수정이 들어간 판본은 설비 문자셋과 CR·LF 구분을 쓴다.
    L67-67 문단 144  | Gateway 2d56c2b7 수정 이후 이 경로는 INITIALIZE 뒤의 CR·LF를 건너뛴 나머지를 보관하고, INITIALIZE_REP 전송과 연결 배정이 성공하면 업무 수신 처리에 넘긴다. INITIALIZE만 받은 경우는 넘길 업무 데이터가 없다.
    L69-69 문단 188  | 설비 ID·연결 시각·빠진 첫 메시지·다음 정상 메시지를 운영 담당자에게 전달한다. 같은 수신 묶음이었는지, 연결 배정 뒤 남은 데이터가 전달됐는지, 현장 Gateway 판본에 수정이 포함됐는지 확인을 요청한다. 화면 로그 조회는 00.rag/user/message-results-and-communication-logs.md를 따른다.
§ L71-81 [38] INITIALIZE 뒤 연결이 닫혀 Socket 업무가 오지 않을 때   parent=358 children=3 sources=2
    L77-77 문단 123  | ACTIVE Socket의 INITIALIZE 경로는 응답 생성 실패, 응답 전송 실패, 연결 배정 거부 때 연결을 닫는다. 첫 업무 데이터 보관 공간이 넘치거나 배정 뒤 남은 데이터를 꺼내는 데 실패해도 연결을 닫는다.
    L79-79 문단 145  | 운영 담당자에게 설비 ID·INITIALIZE 시각·연결이 닫힌 시각·화면 결과를 전달한다. 응답 전송 성공, 연결 배정 성공, 남은 데이터 전달 중 어디까지 진행됐는지 확인을 요청한다. 연결이 완료되기 전 실패와, 연결 뒤 첫 메시지만 빠진 증상을 구별한다.
    L81-81 문단 88  | 연결·명령 결과 자체의 점검은 00.rag/user/equipment-connection-and-command-troubleshooting.md에서 확인한다.
SUMMARY sections=5 children=16 maxChild=188 maxParent=448
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

사람 검사: 40절 미만이므로 이번 보완에서도 5절 전부를 제목·질문·원천·독립 답·중복 측면에서 읽었다. 관련 `socket-framing-and-parsing-reference.md`의 설비 문자셋 반영·기본값 절과 `equipment-registration-and-settings.md`의 Socket 적용 범위도 읽었다. 직접 대조한 범위에서 다른 문서에 추가 수정이 필요한 확정 결함은 없었다.

| 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·기존 문서와의 중복 |
|---|---|---|---|---|
| 한글 수신 뒤부터 Socket 프레임 경계가 어긋날 때 | 적합: 여러 바이트 문자와 경계 손상 | 원본 소비·해석 불가·미완성 문자 분기 대조 | 입력·문자셋·소비량 인계까지 완결 | 종료자 전체 계약을 복제하지 않고 바이트 경계 진단을 소유 |
| Socket 문자셋 오류가 준비 중 또는 첫 수신에서 날 때 | 적합: 준비 중 오류와 첫 수신 오류 | 문자셋 이름 검증·처리기 생성 시 LF 가드·DLQ·격리 경로 대조 | 내장·플러그인 범위와 실패 정보 인계까지 완결 | 참조 문서의 전체 설정 설명을 복제하지 않고 진단에 필요한 조건만 사용 |
| 종료 정규식 매치가 반복되고 처리가 멈출 때 | 적합: 매치 반복 | 빈 문자열 생성 검사와 위치 0 추출 방어 대조 | 패턴·입력·버퍼 소비·JAR 인계까지 완결 | 프레임 손상과 반복을 다른 증거로 구별 |
| 연결 직후 첫 Socket 업무 메시지만 빠질 때 | 적합: 첫 메시지 누락 | ACTIVE·설비 문자셋·같은 수신 묶음·연결 후 전달 대조 | 수신·배정·전달·수정 판본 인계까지 완결 | 설비 ID를 이미 아는 ACTIVE와 식별 전 UTF-8 구간을 구별 |
| INITIALIZE 뒤 연결이 닫혀 Socket 업무가 오지 않을 때 | 적합: 초기화 뒤 종료 | 응답 생성·전송·배정·보관·추출 실패의 close 대조 | 진행 위치·시각·화면 결과 인계까지 완결 | 공통 연결·명령 절차는 기존 소유 문서로 안내 |

전수 사람 검사 판정:

- 제목 정확 중복과 불필요한 반복 틀 0건. 주체·증상별 질문을 사용했다.
- 같은 본문 복제·빈 틀 문장·이름 토막 뜻풀이·잘린 문장·오답 인용의 미해소 지적 0건. 기술 명칭은 실제 사용 조건·입력·결과에 연결했다.
- 마스킹 전후 문단의 의미와 정규식 표기가 유지됐다. 이름·자격 값 형태의 머리글을 사용하지 않았다.
- user 본문의 admin objectKey·내부 파일 조작 지시·관리 문서 열람 지시 0건. 내부 증거 확인은 운영 담당자에게 요청하는 인계로 썼다.
- 작성으로 생긴 미해소 결함 0건, 사용자 제품·범위 결정 요청 0건. 자체검사 결과이며 메인 세션의 독립 원천 대조를 대신하지 않는다.

정리: 최초 작성과 이번 C 보완에서 검사용 임시 파일·브라우저·컨테이너·서비스를 만들지 않았다. 최초 작성 당시 지정 작업 폴더의 `work/` 파일 열거 결과는 비어 있었다. 이번에는 이전된 작업 폴더의 기존 파일을 보존하고 검사 출력은 이 작성 기록에 남겼다. 이 세션이 삭제할 소유 일회성 파일은 없으며 검색 원문·작성 기록 두 파일을 보존한다.

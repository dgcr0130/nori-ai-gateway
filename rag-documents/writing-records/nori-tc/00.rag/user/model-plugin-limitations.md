# 모델·플러그인 적용 제약 작성 기록

## 대상

- objectKey: `00.rag/user/model-plugin-limitations.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 알려진 제약
- 검색 원문: `rag-documents/nori-tc/00.rag/user/model-plugin-limitations.md`
- 검색 원문 SHA-256: `26c4d038e964dfa3e13bf903a2c2ba574b140ac97f207b6bdffbbbfe3d4c2b5b`
- 검색 원문 줄 수: 142
- H2 절 수: 12
- 확인일: 2026-11-19 한국 시간
- 작성 범위: 목록 39행의 검색 원문과 이 기록 두 파일. 위임·제품 수정·업로드·커밋 없음.
- 최종 판정: COMPLETED. 대상 문서의 미해소 위반 0건, 최종 전체 CROSS VIOLATION 0건, 사용자 결정 0건. 세 번째 검사에서 보인 다른 문서 내부 중복 2건은 다른 세션의 파일 변경 후 네 번째 검사에서 사라졌다. 이 세션은 다른 문서를 수정하지 않았다.

## 원천 판본

| 저장소 | HEAD 12자리 | 확인 경로와 작업 트리 상태 |
|---|---|---|
| nori-ai-gateway | 70cf6cd84db6 | `docs/rag-document-writing-rules.md` 전체와 `docs/tc-rag-document-list.md` 전체를 읽음. 두 경로의 `git status --short -- <경로>` 출력 없음, exit 0. |
| nori-tc-ui-front | 9fe69a75dcd3 | 본문 메타의 3개 파일 및 `GatewayGroupTable.tsx`, `eqp.api.ts`, `ko.json`, `DeployRegisterPanel.tsx` 등 관련 화면·요청을 읽음. 대상 경로 status 출력 없음, exit 0. |
| nori-tc-ui-backend | 985d17c604dd | `EqpControllerCommandSupport.java`, `EqpManagementCommandValidator.java`, `JpaEqpOptionsQueryPort.java`, `EqpController.java`, `UiApiPermissionCache.java` 등 확인. 대상 경로 status 출력 없음, exit 0. |
| nori-tc-gateway | d3636ab86763 | 검색 원문 메타가 참조하는 15개 고유 파일과 `SocketInboundPipeline.java`, `SocketTypeRegistry.java` 확인. 대상 경로 status 출력 없음, exit 0. |
| nori-tc-exec | f1d5e0a8e727 | U §10.4와 구조 문서 `docs/Architecture/20-gateway/06-socket-protocol-and-plugins.md` 전체, `docs/scripts/manual-permission-catalog.json`의 관련 권한 확인. 대상 경로 status 출력 없음, exit 0. |
| nori-common | ae6791e99e91 | `modules/nori-plugin-support/src/main/java/com/nori/common/plugin/runtime/PluginArtifactValidationSupport.java` L1~148 확인. 대상 경로 status 출력 없음, exit 0. |

원천 메타의 파일들을 저장소별로 묶어 정확한 상대경로 인자로 `git -c safe.directory=<저장소 절대경로> -C <저장소> status --short -- <경로들>`를 실행했다. 참조 파일의 수정 상태가 없어 commit을 썼다. 관련 디렉터리 전체를 대상으로 한 사전 status도 변경 경로를 반환하지 않았다. 저장소 전체가 깨끗하다는 주장은 하지 않는다.

형제 저장소에서 기본 Git 호출은 소유자 불일치로 거부됐다. 전역 설정을 바꾸지 않고, 읽는 저장소 하나를 명시한 호출 한정 `-c safe.directory=...`로 조회했다. Git의 사용자 공통 ignore 파일 접근 경고는 계속 표시됐으며, 대상 파일 status의 exit code 0과 변경 경로 출력 없음은 별도로 확인했다.

기존 검색 문서는 `equipment-registration-and-settings.md`, `equipment-model-parameter-and-jar-update.md`, `model-registration-and-branching.md`, `model-details-and-flow-editing.md`, `model-flow-troubleshooting.md`, `socket-troubleshooting.md`, `screen-behavior-limitations.md`, `tc-supported-scope.md`의 관련 본문을 대조했다. 소유자가 있는 절차·상태 설명·프레임 규칙은 복제하지 않았다.

## 절별 사실 대조

인계 문장은 제품이 자동으로 수집·표시하는 기능이라는 주장이 아니다. 바로 앞에서 확인한 실패 조건을 담당자가 대조할 수 있게 제시한 사용자 인계 안내다. 실제 로그 조회나 설정 변경 명령은 넣지 않았다.

### 모델 후보와 JAR 갱신 경로의 제한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 18 첫 문장 | 통신 방식·개발/운영 구분에 따른 모델 후보 제한 | nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 L153~165 | develop/operate 목록 중 하나를 선택하고 통신 방식으로 필터링한다. 상세 상태 조합은 기존 등록 문서 소유로 유지했다. |
| 18 둘째 문장 | 설비 수정은 기존 모델·JAR 연결 유지 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd L44~65 | 요청의 선택값 대신 기존 modelVersionKey·gatewayJarKey·businessJarKey를 명령에 넣는다. |
| 20 | 선택/등록과 실제 갱신의 문서 위치 | nori-tc-ui-front/src/features/eqp/components/EqpModelBindingModal.tsx @ 9fe69a75dcd3 L210~228; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 L949~980 | 렌더된 선택 칸·수정 요청을 확인하고, 실제 존재하는 두 user 문서의 소유 절로 연결했다. 절차를 다시 쓰지 않았다. |

보충 대조: `eqp.api.ts` L602~620의 PUT 요청 → 서버 `toUpdateCommand`의 기존 연결 유지까지 확인했다. `JpaEqpOptionsQueryPort.java` L66~78은 DEVELOP/OPERATE 목록 분리, `EqpManagementCommandValidator.java` L84~104는 연결 모델 존재·통신 방식·상태 검사를 수행한다. 이 검사를 모든 배포 경로에 동일하게 적용한다고 확대하지 않았다.

### Gateway 기동만으로 모든 플러그인이 적용되지는 않는다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 28 첫 문장 | 기동 미리 읽기는 SOCKET만 대상 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L313~334 | commInterface가 SOCKET이 아니면 건너뛴다. 수동 reload의 모든 진입점까지 SOCKET 전용이라고 확대하지 않았다. |
| 28 둘째 문장 | 정책 비활성·JAR 미선택/부재·내용 없음이면 미적재 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L135~153 L336~367 | loadOnStartup false이면 반환하고, JAR 선택키 없음·조회 결과 없음·빈 내용은 skip한다. |
| 30 첫 문장 | 실패 시 기동 중단 또는 대상 skip | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L383~395 | failFastOnStartup true는 예외, false는 경고 뒤 다음 설비로 진행한다. |
| 30 둘째 문장 | 서비스 기동과 개별 적재 성공은 별개 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 L92~128 | 기동을 계속하지만 처리기 대여 결과가 비어 있는 기대값을 확인했다. 시험 실행 결과로 보고하지 않았다. |
| 32 | 대상·시각·파일·증상 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L323~394 | 설비별 적재 분기를 대조할 수 있는 식별 정보를 안내했다. 사용자가 설정을 조작하도록 하지 않았다. |

### 선택한 Socket Protocol을 JAR가 지원하지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 41 첫 문장 | 설비·프로토콜별 플러그인 조회 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 L32~58 | eqpId와 socketType으로 플러그인 대여를 요청한다. |
| 41 둘째 문장 | 플러그인이 내장 처리기에 우선 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 L44~68 | 플러그인이 있으면 즉시 반환해 core 조회로 가지 않는다. |
| 43 첫 문장 | 플러그인에 없으면 내장 조회, 둘 다 없으면 실패 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 L44~68; nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ d3636ab86763 L47~55 | registry.getRequired도 해당 종류가 없으면 예외를 낸다. |
| 43 둘째 문장 | 적재와 선택 종류 지원은 별개 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 L288~303 | 한 JAR의 두 종류 적재 성공과 다른 종류 대여 empty 기대값을 함께 확인했다. |
| 45 | 지원 종류 대조를 위한 인계 | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 L158~188; nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L182~190 | 화면의 정확한 열은 Socket Protocol이며, 실제 런타임 조회는 설비 ID·종류를 함께 쓴다. |

### 비어 있거나 크기·파일 대조를 통과하지 못한 JAR

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 53 첫 문장 | 빈 내용·크기 초과 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 L166~189 | null/빈 내용 검사 후 properties의 최대 크기를 공통 검증 함수로 검사한다. 고정 MB 수치는 쓰지 않았다. |
| 53 둘째 문장 | 기대 파일명/해시가 있으면 실제 내용 대조 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 L127~149 | 선택 입력은 정규화 후 null이 아닐 때 비교하며 불일치 시 예외를 낸다. 파일명은 원문 그대로의 비교가 아니라 코드 정규화를 거친 비교다. |
| 55 첫 문장 | 교체 전 검증·적재 실패면 이전 런타임 유지 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L281~289 | 새 runtime 생성 성공 뒤에만 swapRuntime을 호출한다. 교체 뒤 후속 검사 실패까지 자동 원복된다고 쓰지 않았다. |
| 55 둘째 문장 | 파일·시각·표시 결과를 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 L127~189 | 파일 크기와 locator 불일치의 구별에 필요한 정보를 안내했다. |

### 등록된 JAR도 허용 해시가 아니면 적용되지 않는다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 64 첫 문장 | 검사 활성 시 미등록 해시는 차단 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 L195~234 | 비활성 시 반환, 활성 시 정규화한 허용 목록 포함 여부 검사 후 예외를 낸다. |
| 64 둘째 문장 | 코드 기본값은 검사 사용 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeProperties.java @ d3636ab86763 L75~92 | enforceSha256Allowlist=true를 확인했다. 현장 설정값이라고 쓰지 않았다. |
| 64 셋째 문장 | 선택 목록 등재와 적재 허용은 별개 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 L57~62 L210~234 | 선택키로 파일을 얻더라도 실제 파일 해시 검사는 별도로 수행한다. |
| 66 첫 문장 | 해시 통과 후에도 후속 검사 실패 가능 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 L156~175 | 해시가 허용된 잘못된 JAR의 다음 단계 실패 기대값을 읽었다. |
| 66 둘째 문장 | 실패 단계와 승인 파일 대조 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 L57~62 L195~234 | 허용 목록을 해제하는 우회 지시는 넣지 않고, 대상 파일과 실패 단계를 대조하도록 했다. |

### SDK 호환성 위반은 정책에 따라 차단되거나 경고된다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 76 첫 문장 | SDK 메이저 1 | nori-tc-gateway/gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/GatewayActionApiVersion.java @ d3636ab86763 L13~15 | 현재 코드 상수다. 현장 JAR의 선언 버전을 뜻하지 않는다. |
| 76 둘째 문장 | 선언 오류·패키지/메이저 위반은 FAIL_FAST에서 차단 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginCompatibilitySupport.java @ d3636ab86763 L34~83 L108~135 | descriptor 해석 오류·canonical package·메이저 검사가 공통 위반 처리로 연결된다. |
| 78 첫 문장 | 기본 WARN_ONLY는 다음 적재 계속 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeProperties.java @ d3636ab86763 L94~97; nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginCompatibilitySupport.java @ d3636ab86763 L114~123 | 기본값과 경고 후 return을 각각 확인했다. |
| 78 둘째 문장 | 경고 후 계속은 호환성 통과가 아님 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 L210~265 | 동일 메이저 위반이 정책에 따라 호환 차단/후속 일반 적재 실패로 나뉘는 기대값이다. |
| 80 | JAR·Gateway 판본·시각·문구 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginCompatibilitySupport.java @ d3636ab86763 L59~80 L126~134 | 기대/실제 선언 및 대상 식별값을 비교할 수 있게 안내했다. |

### Socket 처리기가 없거나 같은 종류가 중복된 JAR

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 88 첫 문장 | 지원 구현 없음·빈 socketType 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginDiscoverySupport.java @ d3636ab86763 L40~79 L106~111 | 표준 또는 레거시 계약 구현을 지원하며, 적합 구현이 없거나 종류가 비면 예외다. |
| 88 둘째 문장 | 동일 종류 두 처리기는 JAR 전체 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginDiscoverySupport.java @ d3636ab86763 L82~103; nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 L308~338 | 중복 예외와 활성 runtime 수 0 기대값을 확인했다. |
| 90 첫 문장 | 다른 종류 복수 처리기는 허용 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 L270~303 | 하나의 JAR에서 서로 다른 두 종류가 대여되는 기대값을 읽었다. |
| 90 둘째 문장 | 구현 존재·중복을 담당자에게 확인 요청 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginDiscoverySupport.java @ d3636ab86763 L58~59 L89~111 | 해당 적재 실패 조건에 맞는 파일·프로토콜 식별값을 인계하도록 했다. |

### 수신 처리기가 있어도 명령 송신을 지원하지 않을 수 있다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 98 첫 문장 | 수신 구현만으로 적재 가능 | nori-tc-gateway/gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/SocketTypeHandler.java @ d3636ab86763 L41~48; nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginDiscoverySupport.java @ d3636ab86763 L40~79 | encode는 default이며 적재 시 송신 호출 시험을 수행하지 않는다. |
| 98 둘째 문장 | encode 기본 구현은 지원하지 않음 오류 | nori-tc-gateway/gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/SocketTypeHandler.java @ d3636ab86763 L41~48 | UnsupportedOperationException을 던진다. |
| 98 셋째 문장 | 빈 인코딩 결과 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/SocketOutboundFrameEncoder.java @ d3636ab86763 L63~87 | 플러그인 우선 resolver에서 고른 처리기의 encode 호출 뒤 바이트 길이 0을 거부한다. |
| 100 첫 문장 | 수신 성공과 송신 지원 분리 | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 L105~126; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/SocketOutboundFrameEncoder.java @ d3636ab86763 L63~87 | 수신은 추출·decode, 송신은 encode라는 별도 호출 경로다. |
| 100 둘째 문장 | 실패 명령·대상·파일·시각 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/SocketOutboundFrameEncoder.java @ d3636ab86763 L45~87 | 설비·종류·rawMessage를 받아 선택된 처리기로 송신을 인코딩한다. 메시지 내용 예제·비밀값은 넣지 않았다. |

### 시작된 설비의 Gateway JAR 교체는 통신을 멈췄다 이어 간다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 108 첫 문장 | 시작 상태에서 stop→reload→start | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L768~811 | desiredState STARTED일 때 통신 종료·reload·정보 갱신·통신 시작으로 이어진다. |
| 108 둘째 문장 | 종료 상태는 자동 시작 안 함 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlServiceTest.java @ d3636ab86763 L1239~1279 | ENDED 설비의 reload 확인과 connectionControlPort 호출 없음 기대값이다. |
| 110 첫 문장 | 무중단 교체로 취급 불가 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlServiceTest.java @ d3636ab86763 L1032~1050 | 실제 호출 기대값에 기존 채널 close와 stop/start가 있다. |
| 110 둘째 문장 | 작업 전 상태·파일·시각과 통신 영향 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L768~824 | 성공 순서뿐 아니라 실패 시 복구 함수로 넘기는 경로도 읽고, 항상 재시작 성공이라고 보장하지 않았다. |
| 110 셋째 문장 | 화면 경로와 runtime 교체 지원은 구별 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd L44~65; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L725~824 | 화면 수정에서 선택이 유지되는 것과 내부 갱신 처리 구현이 존재하는 것을 혼동하지 않았다. |

### PASSIVE Socket이 같은 수신 주소에서 다른 JAR를 쓰려는 경우

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 119 첫 문장 | 동일 수신 주소 공유 시 프로토콜·JAR 선택 일치 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 L584~639 L936~950 | 그룹 서명은 통신 방식·수신 IP·Port·socketType·JAR key이고, 동일 물리 listener 판정은 방식·IP·Port를 쓴다. |
| 119 둘째 문장 | 다른 JAR 선택으로 변경하면 교체 전 충돌 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L757~760; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlServiceTest.java @ d3636ab86763 L1385~1433 | 검사가 reload보다 먼저 실행되고, 충돌 시 mutationPort 호출 없음 기대값을 확인했다. 같은 파일명·내용이면 별도 JAR key도 같다고 가정하지 않았다. |
| 121 첫 문장 | 단일 설비 교체도 공유 조건 영향 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 L616~639 | 자기 설비를 제외한 컨텍스트들을 순회해 충돌을 검사한다. |
| 121 둘째 문장 | 공유 설비·현재/변경 파일·오류 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 L632~638 | 대상과 충돌 상대를 구별해 전달하도록 안내했다. 현장 IP·Port 값을 수록하지 않았다. |

### 이전 플러그인의 사용 종료를 기다리는 데는 한도가 있다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 131 첫 문장 | 새 runtime 교체 뒤 이전 처리기 종료 대기 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L430~463 | 새 맵 참조 교체 후 previousRuntime.drainAndCloseQuietly를 부른다. |
| 131 둘째 문장 | 기본 5,000ms·초과/중단에도 이전 로더 close | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeProperties.java @ d3636ab86763 L67~73; nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeModels.java @ d3636ab86763 L160~195 | 기본값, awaitDrain false, 인터럽트 처리, 결과와 무관한 close 호출을 각각 확인했다. |
| 133 첫 문장 | 교체 완료만으로 이전 작업 안전 종료 보장 불가 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 L458~473; nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeModels.java @ d3636ab86763 L160~169 | drain 결과를 교체 성공 조건으로 검사하지 않고 완료 로그로 이어진다. 모든 교체가 실패한다는 뜻으로 쓰지 않았다. |
| 133 둘째 문장 | 이전/새 파일과 교체 시점 실패 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginHandleDrainTest.java @ d3636ab86763 L32~86; nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeModels.java @ d3636ab86763 L163~166 | 반납 대기와 미반납 실패의 기대값 및 실패 로그를 읽었다. 로그 직접 접근은 운영 담당자에게 맡겼다. |

### PLC는 Gateway Socket 플러그인 교체 대상이 아니다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 140 첫 문장 | PLC의 Gateway JAR 갱신 거부 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L744~755 | PLC 조건에서 명시적 예외를 내며 reload로 가지 않는다. |
| 140 둘째 문장 | Socket 플러그인 JAR로 PLC 처리 교체 불가 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L750~755 | PLC codec은 컴파일 모듈이며 SOCKET JAR 핫로드와 무관하다는 주석과 실제 가드가 일치한다. |
| 142 | 바꾸려는 처리·대상·거부 결과 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 L725~755 | 미지원 갱신을 반복하는 대신 변경 범위를 담당자가 판정하도록 안내했다. |

## 매뉴얼과 코드가 다른 곳

| 원천 설명 | 코드 확인 | 작성 처리 |
|---|---|---|
| U §10.4는 Eqp Info Update와 Model Info Update에서 모델·두 JAR를 모두 선택할 수 있는 것으로 묶는다. | front `EqpManageFormModal.tsx` L949~980에서 Gateway Jar는 표시되지만 Model / Business는 create 전용이다. `EqpModelBindingModal.tsx` L210~228은 모델·Business JAR 요청을 만들고, backend `EqpControllerCommandSupport.java` L44~65는 기존 연결을 유지한다. | 틀린 화면 칸 목록은 옮기지 않았다. 첫 절에 선택과 적용의 최소 경계만 쓰고, 정확한 창별 차이·갱신 절차는 기존 user 문서로 연결했다. |
| 구조 문서 §1은 SDK 메이저 선언이 필요하다고 설명한다. | `GatewaySocketPluginCompatibilitySupport.java` L34~83 L108~135, 기본 WARN_ONLY에서 선언 누락도 경고 뒤 계속된다. | 선언 누락을 무조건 적재 거부로 쓰지 않았다. FAIL_FAST와 WARN_ONLY 조건을 본문에 함께 썼다. |
| 구조 문서 §3은 kafka 아웃바운드가 resolveRequiredOrDefault 변형을 쓴다고 설명한다. | `SocketOutboundFrameEncoder.java` L51~52에서 SocketTypeResolution으로 종류를 정한 후 L65~71에서 resolveRequired를 부른다. | 본문에는 실제 플러그인 우선 선택과 encode 호출만 썼다. 원천 구조 문서는 수정하지 않았으며 결과의 다른 문서 발견 사항으로 보고한다. |

U §10.4의 모델·JAR 적용 제한은 기준 HEAD에도 남아 있다. 이 조사에서 이미 해소된 것으로 확정해 제거한 제약은 없다. 구조 문서의 파일명 정리 설명은 nori-common의 실제 정규화 코드도 확인했다. 경로 제거·문자 치환·80자 절단·확장자 보정을 수행하므로, 길거나 확장자가 없는 파일을 무조건 거부한다는 제약을 만들어 쓰지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 Gateway·UI·플러그인의 실제 적용 판본과 적재 성공 여부 | 로컬 HEAD는 확인했지만 배포 현장·서버에 접속하지 않았다. | 운영 담당자·플러그인 담당자 |
| 특정 모델명·모델 버전·특정 제조사 JAR의 지원 조합 목록 | 일반 선택·적재 코드만으로 개별 JAR의 내용이나 제조사 계약을 확정할 수 없다. | 모델 담당자·제조사 연동 담당자 |
| 현장의 JAR 최대 바이트 수·허용 해시·호환 정책·미리 읽기 설정값 | 코드 기본값과 조건만 확인했다. 실제 운영 설정을 읽지 않았다. | 운영 담당자 |
| 플러그인 변경 시 무손실·무중단·항상 자동 복구된다는 보장 | stop/start, 단계별 실패·사용 종료 대기 한도가 존재해 그런 보장을 코드가 제공하지 않는다. | 운영 담당자·플러그인 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 반복하지 않은 사실 |
|---|---|
| `00.rag/user/equipment-registration-and-settings.md` | 모델 상태별 후보 상세, 등록 입력값·권한·서버 검사·Gateway Jar 최초 선택과 창별 표시 차이. 첫 절에는 적용 경계를 설명할 최소 조건만 남겼다. |
| `00.rag/user/equipment-model-parameter-and-jar-update.md` | Model Info Update 저장 한계의 상세, 모델·Business JAR 배포 절차, Upload 비활성, Gateway JAR 변경 화면 부재, 적용 결과 조회 위치. |
| `00.rag/user/model-registration-and-branching.md` | 모델 생성·branch·Parent Model Commit·삭제·상태 전이와 편집 제약. |
| `00.rag/user/model-details-and-flow-editing.md` | Flow·모델 상세 편집과 Check In/Undo 작업. |
| `00.rag/user/socket-framing-and-parsing-reference.md` | LF·정규식·문자셋·빈 프레임·최대 프레임 등 프레이밍/파싱 상세. 사전 목록 조회에는 원문이 없었으나 최종 검사 때 추가된 파일을 읽었다. 대상 문서에 규칙 본문을 복제하지 않았다. |
| `00.rag/user/socket-troubleshooting.md` | 멀티바이트 경계·빈 정규식 매치·첫 메시지 유실의 증상과 수정 판본. |
| `00.rag/user/screen-behavior-limitations.md` | 로그인 복구·그룹 권한 저장·폴링 표시·재시작 지연 안내. |
| `00.rag/user/permission-code-reference.md` | 권한 코드별 뜻. 이 문서는 화면 실행 절차를 추가하지 않아 권한 표도 복제하지 않았다. |
| `00.rag/user/error-message-and-code-reference.md` | 적재 오류 코드·문구 사전. 이 문서에는 발생 조건과 영향만 남겼다. |

## 검사 결과

명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/model-plugin-limitations.md
```

첫 실행은 exit 1, 원천 줄 범위 초과 2건이었다. `GatewayActionApiVersion.java`는 파일 59줄인데 L13~67로, `SocketTypeHandler.java`는 파일 49줄인데 L41~50으로 쓴 작성 오류였다. 원문을 번호와 함께 다시 읽어 L13~58과 L41~48로 각각 최소 정정했다. 검사기를 수정하지 않았다. 첫 오류를 제품 결함이나 공식 검증 실패로 분류하지 않았다.

마지막 원문 편집 후 두 번째 실행은 exit 0이었다. 마지막 출력 10줄은 다음과 같다.

```text
    L121-121 문단 110  | 한 설비만 바꾸려 해도 공유 설비 조건에 영향을 받는다. 운영 담당자에게 대상 설비 ID·공유하는 설비 ID·현재 파일과 변경할 파일·충돌 시각·표시 문구를 전달해 공유 조건을 대조하도록 요청한다.
§ L123-133 [30] 이전 플러그인의 사용 종료를 기다리는 데는 한도가 있다   parent=246 children=2 sources=4
    L131-131 문단 121  | 확인한 Gateway 판본은 새 플러그인으로 교체한 뒤 이전 처리기의 사용 종료를 기다린다. 대기 기본값은 5,000ms이며, 한도를 넘거나 대기가 중단되면 사용 중인 처리기가 남아 있어도 이전 JAR 로더를 닫는다.
    L133-133 문단 124  | 교체 완료만으로 이전 처리 중인 작업의 안전한 종료까지 보장되지 않는다. 교체 시점의 처리 실패는 운영 담당자에게 설비 ID·이전 파일과 새 파일·교체 시각·실패 메시지를 전달해 사용 종료 대기 결과를 확인하도록 요청한다.
§ L135-142 [35] PLC는 Gateway Socket 플러그인 교체 대상이 아니다   parent=177 children=2 sources=1
    L140-140 문단 95  | 확인한 Gateway 판본은 PLC 설비의 Gateway JAR 갱신 요청을 거부한다. Socket 플러그인 JAR를 바꾸는 방식으로 PLC 통신 처리를 교체할 수 없다.
    L142-142 문단 81  | PLC 변경이 필요하면 설비·모델 담당자에게 설비 ID·바꾸려는 통신 처리·대상 파일·거부 시각과 문구를 전달해 변경 범위를 확인하도록 요청한다.
SUMMARY sections=12 children=27 maxChild=145 maxParent=282
CORPUS docs=38/53 sections=530 part={admin=9, user=29} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 트러블슈팅=12}
TOTAL files=38 targets=1 violations=0
```

MASK 출력 0건, CROSS VIOLATION 출력 0건. 문서 제목 13자, 제목 최댓값 41자, 자식 최대 145자, 부모 최대 282자다. 문단 27개이며 표·순서 목록은 검색 원문에 사용하지 않았다.

위 결과는 두 번째 실행 당시의 결과다. 작성 기록을 저장한 뒤 최종 원문을 다시 검사한 세 번째 실행에서는 corpus가 38개에서 40개로 늘었고 외부 문서의 내부 중복 2건이 새로 나왔다. 대상 문서의 SHA-256·142줄·12절은 변하지 않았고 대상 위반은 여전히 0건이다. 최종 실행 exit 0만으로 CROSS VIOLATION 0건을 주장하지 않는다. 세 번째 실행의 Stopwatch 실측은 17,543ms다. 마지막 출력 10줄을 그대로 남긴다.

```text
    L131-131 문단 121  | 확인한 Gateway 판본은 새 플러그인으로 교체한 뒤 이전 처리기의 사용 종료를 기다린다. 대기 기본값은 5,000ms이며, 한도를 넘거나 대기가 중단되면 사용 중인 처리기가 남아 있어도 이전 JAR 로더를 닫는다.
    L133-133 문단 124  | 교체 완료만으로 이전 처리 중인 작업의 안전한 종료까지 보장되지 않는다. 교체 시점의 처리 실패는 운영 담당자에게 설비 ID·이전 파일과 새 파일·교체 시각·실패 메시지를 전달해 사용 종료 대기 결과를 확인하도록 요청한다.
§ L135-142 [35] PLC는 Gateway Socket 플러그인 교체 대상이 아니다   parent=177 children=2 sources=1
    L140-140 문단 95  | 확인한 Gateway 판본은 PLC 설비의 Gateway JAR 갱신 요청을 거부한다. Socket 플러그인 JAR를 바꾸는 방식으로 PLC 통신 처리를 교체할 수 없다.
    L142-142 문단 81  | PLC 변경이 필요하면 설비·모델 담당자에게 설비 ID·바꾸려는 통신 처리·대상 파일·거부 시각과 문구를 전달해 변경 범위를 확인하도록 요청한다.
SUMMARY sections=12 children=27 maxChild=145 maxParent=282
CROSS VIOLATION 같은 자식 본문 [00.rag/user/socket-framing-and-parsing-reference.md L71, 00.rag/user/socket-framing-and-parsing-reference.md L87]: 결과 항목=body | 들어가는 값=첫 토큰 뒤의 문자열이다. 뒤 문자열이 없으면 빈 문자열이다.
CROSS VIOLATION 같은 자식 본문 [00.rag/user/socket-framing-and-parsing-reference.md L69, 00.rag/user/socket-framing-and-parsing-reference.md L85]: 결과 항목=messageName | 들어가는 값=첫 토큰이다.
CORPUS docs=40/53 sections=556 part={admin=9, user=31} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=2, 트러블슈팅=12}
TOTAL files=40 targets=1 violations=0
```

이 두 중복은 대상 문서와 공유된 문장이 아니라 다른 파일 안의 두 절 사이 중복이었다. 소유 범위 밖이므로 이 세션은 고치거나 검사기를 우회하지 않았다. 세 번째 실행 뒤 파일을 읽었을 때 중복 행이 이미 정리된 다른 세션의 변경을 관측했다. 변경된 검사 입력에 대해 네 번째 검사를 실행했다. 반복 사유는 대상 문서 변경이 아니라 외부 문서의 실제 변경이다.

네 번째 최종 검사는 exit 0, CROSS VIOLATION·MASK 출력 0건이며 Stopwatch 실측 21,202ms다. 실행 직전 외부 문서 `socket-framing-and-parsing-reference.md`의 SHA-256은 `76468d5b5cd17542be54b85f7e24a1f99e7e8cb8f6dd1346ff09b0e24915db9b`였다. 마지막 출력 10줄은 다음과 같다.

```text
    L121-121 문단 110  | 한 설비만 바꾸려 해도 공유 설비 조건에 영향을 받는다. 운영 담당자에게 대상 설비 ID·공유하는 설비 ID·현재 파일과 변경할 파일·충돌 시각·표시 문구를 전달해 공유 조건을 대조하도록 요청한다.
§ L123-133 [30] 이전 플러그인의 사용 종료를 기다리는 데는 한도가 있다   parent=246 children=2 sources=4
    L131-131 문단 121  | 확인한 Gateway 판본은 새 플러그인으로 교체한 뒤 이전 처리기의 사용 종료를 기다린다. 대기 기본값은 5,000ms이며, 한도를 넘거나 대기가 중단되면 사용 중인 처리기가 남아 있어도 이전 JAR 로더를 닫는다.
    L133-133 문단 124  | 교체 완료만으로 이전 처리 중인 작업의 안전한 종료까지 보장되지 않는다. 교체 시점의 처리 실패는 운영 담당자에게 설비 ID·이전 파일과 새 파일·교체 시각·실패 메시지를 전달해 사용 종료 대기 결과를 확인하도록 요청한다.
§ L135-142 [35] PLC는 Gateway Socket 플러그인 교체 대상이 아니다   parent=177 children=2 sources=1
    L140-140 문단 95  | 확인한 Gateway 판본은 PLC 설비의 Gateway JAR 갱신 요청을 거부한다. Socket 플러그인 JAR를 바꾸는 방식으로 PLC 통신 처리를 교체할 수 없다.
    L142-142 문단 81  | PLC 변경이 필요하면 설비·모델 담당자에게 설비 ID·바꾸려는 통신 처리·대상 파일·거부 시각과 문구를 전달해 변경 범위를 확인하도록 요청한다.
SUMMARY sections=12 children=27 maxChild=145 maxParent=282
CORPUS docs=40/53 sections=555 part={admin=9, user=31} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=2, 트러블슈팅=12}
TOTAL files=40 targets=1 violations=0
```

확인한 도구 버전: GNU Bash 5.2.37(1)-release(x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS, Git 2.52.0.windows.1. 검사기 실행은 총 4회이며 독립 AI 호출은 없다. 세션 전체 시간과 첫 두 실행의 전체 wall time은 측정하지 않았다.

### 사람 검사 판정

40절 미만이므로 12절 전부를 읽었다. 다음 표의 질문·원천·독립 답·중복 판정은 모두 이 작성 세션의 자체검사이며 메인 세션의 독립 검증을 대신하지 않는다.

| 절 | 제목만으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·문서와 소유권 |
|---|---|---|---|---|
| 모델 후보와 JAR 갱신 경로의 제한 | 적합 | 적합 | 적용 경계와 소유 문서 연결 | 기존 절차의 최소 조건만 사용, 절차 본문 복제 없음 |
| Gateway 기동만으로 모든 플러그인이 적용되지는 않는다 | 적합 | 적합 | 기동 정책·미적재 영향·인계 완결 | 기동 단계만 소유 |
| 선택한 Socket Protocol을 JAR가 지원하지 않을 때 | 적합 | 적합 | 선택 우선순위·미지원 결과·인계 완결 | 프레임 규칙과 분리 |
| 비어 있거나 크기·파일 대조를 통과하지 못한 JAR | 적합 | 적합 | 파일 검증·이전 유지 범위·인계 완결 | 해시 허용 정책과 구별 |
| 등록된 JAR도 허용 해시가 아니면 적용되지 않는다 | 적합 | 적합 | 활성 조건·차단·인계 완결 | 파일 동일성 검사와 정책 허용을 구별 |
| SDK 호환성 위반은 정책에 따라 차단되거나 경고된다 | 적합 | 적합 | 정책별 영향·인계 완결 | 허용 해시와 SDK 호환성은 다른 검사 |
| Socket 처리기가 없거나 같은 종류가 중복된 JAR | 적합 | 적합 | 구현/중복 조건·영향·인계 완결 | 선택 종류 조회와 JAR 전체 검사 구별 |
| 수신 처리기가 있어도 명령 송신을 지원하지 않을 수 있다 | 적합 | 적합 | encode 한계·영향·인계 완결 | 명령 실행 절차를 추가하지 않음 |
| 시작된 설비의 Gateway JAR 교체는 통신을 멈췄다 이어 간다 | 적합 | 적합 | 시작/종료 조건·통신 영향·인계 완결 | 화면 갱신 절차 대신 runtime 영향만 소유 |
| PASSIVE Socket이 같은 수신 주소에서 다른 JAR를 쓰려는 경우 | 적합 | 적합 | 공유 조건·교체 전 거부·인계 완결 | 일반 주소 배정 설명을 복제하지 않음 |
| 이전 플러그인의 사용 종료를 기다리는 데는 한도가 있다 | 적합 | 적합 | 대기·초과 영향·인계 완결 | 통신 stop/start와 이전 처리기 자원 수명 구별 |
| PLC는 Gateway Socket 플러그인 교체 대상이 아니다 | 적합 | 적합 | 적용 제외·영향·인계 완결 | PLC 쓰기 명령·레지스터 참조를 복제하지 않음 |

제목 정확 중복 0건, 식별자만 바꾼 반복 제목 틀 0건으로 판정했다. 공통 판본 조건 문구는 조건을 독립 절마다 보존하기 위한 것이며 새 사실처럼 반복하지 않았다. 설명 없는 빈 틀 문장·이름 토막 뜻풀이·잘린 문장·오답 인용은 발견하지 않았다. 마스킹 뒤 필요한 값이 사라지는 곳과 user 본문의 admin objectKey·내부 조작 명령도 발견하지 않았다.

제품 시험은 실행하지 않았다. 관련 시험의 기대값을 읽은 것과 시험 PASS를 구별했다. 현장 적재·실제 통신·독립 공식 검증·정답률은 미검증이다. 문서 작업이므로 전체 Gradle build는 실행하지 않았다. 독립 AI 호출은 0회이며 provider 사용량·비용과 세션 전체 wall time은 도구에서 확인하지 못했다.

이 세션이 만든 검사용 임시 파일·디렉터리·서비스·브라우저는 없다. 지정 work 경로에 임시 파일을 만들지 않아 삭제 실행 대상이 없다. 기존 작업 폴더나 다른 세션 자원은 정리하지 않았다.

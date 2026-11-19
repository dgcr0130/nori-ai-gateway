# TC 지원 범위·하지 않는 일 작성 기록

## 대상

- objectKey: `00.rag/user/tc-supported-scope.md`
- 구획·필요 권한: user `[AI_DOC_READ]`
- 유형: 개념
- 검색 원문 SHA-256: `480c13f0942b50bb510121af006ff6cbda3ed4040c8fa84379a46227f873a885`
- 검색 원문 줄 수: 189
- 절 수: 12, 직렬화 자식 수: 31
- 확인일: 2026-11-19(한국 시간). 조사는 11월 18일에 시작했고 최종 확인은 날짜 변경 후 수행했다.
- 범위: 이 검색 원문과 작성 기록 두 파일만 생성한다. 제품 변경·위임·업로드·커밋은 수행하지 않았다.

## 원천 판본

각 저장소에 `git -c safe.directory=<정확한 저장소 절대경로> -C <저장소> rev-parse --short=12 HEAD`와 `status --short -- <원천 경로들>`을 실행했다. 프로세스별 읽기 옵션만 사용했으며 전역 Git 설정은 변경하지 않았다. 마지막 대조의 모든 status 명령은 exit 0·출력 없음이었다.

| 저장소 | HEAD | 최종 검색 원문에서 참조한 고유 파일 | 작업 트리 |
|---|---|---:|---|
| nori-tc-exec | f1d5e0a8e727 | 1 | 원천 파일 수정 없음 |
| nori-tc-business | 7db24f7466bd | 4 | 원천 파일 수정 없음 |
| nori-tc-ui-front | 9fe69a75dcd3 | 24 | 원천 파일 수정 없음 |
| nori-tc-ui-backend | 985d17c604dd | 11 | 원천 파일 수정 없음 |
| nori-tc-simulator | 5e9b02e3ae16 | 1 | 원천 파일 수정 없음 |
| nori-tc-gateway | d3636ab86763 | 11 | 원천 파일 수정 없음 |

작성 규칙 전체와 53개 목록을 읽었으며 두 안내 문서는 작업 트리 수정 없음도 확인했다. 구조 원천 전체를 읽고 각 지원·미지원 주장을 코드로 대조했다. 기존 문서에서는 주제별 소유 절·제약·연결할 objectKey를 확인했다. 원천 코드와 관련 시험은 읽었으며 제품 시험을 새로 실행한 것은 아니다.

초기 Git 조회는 형제 저장소의 소유자 검사로 exit 128이었다. 이후 위 프로세스별 옵션으로 읽기 명령이 exit 0임을 확인했다. `rg`가 PATH에 없어 Python pathlib·정규식과 Git 파일 목록으로 검색했다. 첫 PowerShell 읽기는 기본 인코딩으로 한국어 표시가 깨져 UTF-8로 다시 읽었다. 첫 Python 출력도 cp949 인코딩 오류로 exit 1이었고 `python -X utf8 -`로 다시 읽었다. 해당 실패 출력을 원천 검증 성공으로 세지 않았다.

## 절별 사실 대조

아래 표의 한 행은 검색 원문의 문장 또는 표 자료 행에 대응한다. 인계 문장은 제품에 없는 실행 절차를 만들지 않는 작성상 권고로 구별한다. objectKey 안내는 확정 목록의 소유 문서와 기존 검색 원문을 대조했다.

### 1. TC는 설비와 상위 시스템 사이에서 무엇을 지원하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 18 | TC가 설비 통신·MES·WM 메시지를 연결하고 모델 규칙으로 처리한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L43~56 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L42~55 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; executeNonUiTask L578~605 | 수신 리스너와 모델 매칭 후 액션 실행 경로를 대조했다. |
| 18 | 전송 정의의 목적지에 따라 설비·MES·WM에 전달한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~127 | targetType의 EQP·MES·WM 분기와 발행 호출을 확인했다. |
| 20 | 일반 업무 메시지는 모델·일치하는 흐름이 있어야 실행한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; executeNonUiTask L578~605 | 모델 부재·미매칭은 WORKFLOW_NOT_FOUND, 매칭 성공만 실행한다. |
| 20 | 연결 Port 보고는 별도 공통 경로다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; executeNonUiTask L578~605 | 상태 동기화·연결 해제 보고를 일반 모델 조회보다 먼저 처리한다. |
| 20 | Lot·Carrier 상세 흐름은 별도 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L40~40 | 목록의 소유 질문과 기존 문서의 MES 지시·설비 보고 절을 대조했다. |

### 2. 화면에서 관리할 수 있는 대상은 무엇인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 49 | 화면 관리 대상 요약과 진입·작업 권한 조건. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 업무 화면 라우트 L93~109 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 | 라우트 권한 가드와 요청별 API 인가를 구별했다. |
| 53 | 설비 등록·설정·연결 명령·파라미터 관리. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 설비 변경·명령 L577~845 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 등록·변경·명령 L260~314 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 파라미터 편집·저장 L139~193 | 등록 메뉴, 설비 HTTP 요청·접수, 파라미터 checkout·저장 호출을 확인했다. 연결 명령은 EqpController L322~443도 대조했다. |
| 54 | SECS·Socket·PLC 모델과 메시지·Flow 정의 관리. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 업무 화면 라우트 L93~109 / nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 모델 생성 L730~741 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 저장 L611~649 / nori-tc-ui-front/src/features/model/api/model-flow.api.ts @ 9fe69a75dcd3 ; Flow 생성·변경 L68~116 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; Flow 생성·변경 L65~145 | 세 scope의 모델 라우트, 모델 생성 mutation, Flow 생성·갱신 API와 서비스를 대조했다. |
| 55 | 배포 변경 등록·실행·진행·이력 조회. | nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 배포 생성 L595~605 / nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 실행·이력 메뉴 L934~1013 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 이력·생성 L135~181 | 화면 생성 mutation과 행 메뉴, 서버 getHistory·create를 확인했다. start는 DeployController L252~258을 확인했다. |
| 56 | 계정·그룹·소속·그룹 권한 관리. | nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 관리 화면 조립 L12~30 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 계정 저장 L159~197 / nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 그룹·소속·권한 요청 L219~431 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 계정 생성·변경 L124~156 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 그룹 관리 L122~155 | 관리 패널 전환, API 생성·갱신·멤버·권한 요청을 확인했다. UserController L182~211·GroupController L213~241에서 연결 처리를 대조했다. |
| 58 | 업무별 입력·절차를 처음 쓰는 사람 안내로 연결한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L38~38 | 기존 안내가 설비·모델·계정·배포·조회 문서로 연결하는 것을 확인했다. |

### 3. 상태와 실패는 어디까지 화면에서 조회하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 69 | Live-Map은 설비 상태, 처리실패는 실패·격리·복구 기록을 조회한다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모니터링 라우트 L85~90 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모니터링 문구 L1091~1097 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; getLiveMap L47~51 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 범주별 조회 L81~209 | 메뉴 표시와 라우트, 설비 뷰·각 범주 목록 응답을 대조했다. |
| 69 | 조회 기능과 화면 복구 실행은 구별한다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 요청 L35~58 / nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 새로고침·재시도 L101~132 | 조회 요청만 있고 공통 단추는 조회 갱신에 연결된다. |
| 71 | 상태 읽기·실패 열과 인계는 해당 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L25~25 / nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L33~33 | 각 문서의 소유 질문과 기존 절을 대조했다. |

### 4. 실제 설비나 MES 없이 시험하는 기능이 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 83 | 시뮬레이터로 설비·MES 역할 시험을 지원한다. | nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 ; of L38~60 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; startRun L81~109 | EQUIPMENT·UPSTREAM 활성 역할 집합과 실행 생성·활성화 분기를 대조했다. |
| 83 | Live-Map 데모에서 시나리오와 세 시험 범위를 고른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 데모 열기 L247~256 / nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시나리오 선택 L767~785 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 문구 L468~470 | 데모 열기 클릭, 시나리오 Select, 실제 한국어 범위 문구를 확인했다. |
| 85 | 데모는 선택한 시나리오 실행이다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; startSimulatorRun L56~62 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; startRun L81~109 | scenarioId가 실행 생성 요청에 전달된다. |
| 85 | 대상·시작·종료·결과의 상세는 데모 실행 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L35~35 | 기존 문서의 대상 준비·시작·중지·설비별 결과 절을 확인했다. |

### 5. 지원하는 설비 통신 방식은 무엇인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 96 | 통신 세 방식과 방식에 맞는 모델·설정 준비 조건. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/type/CommInterfaceType.java @ d3636ab86763 ; fromText L44~60 / nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 ; drain L92~126 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java @ d3636ab86763 ; fire L94~125 | 외부 통신 값은 세 종류이며 소켓 핸들러·PLC 그룹/코덱을 실제 사용한다. |
| 100 | SECS는 HSMS를 통한 SECS-II 처리다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/pipeline/HsmsInboundPipeline.java @ d3636ab86763 ; drain L94~129 | 프레임 추출→세션 처리→secs2Decoder.decode를 확인했다. |
| 101 | SOCKET은 선택한 프레임 규칙·해석기로 TCP 메시지를 처리한다. | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 ; drain L92~126 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/TcpTransportRuntimeProvider.java @ d3636ab86763 ; transport L14~24 | socketType 핸들러의 추출·decode와 TCP 제공 구현을 대조했다. |
| 102 | PLC는 MODBUS_TCP로 정한 영역을 주기적으로 읽는다. | nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 ; protocol·encodeReadRequest L18~54 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java @ d3636ab86763 ; fire L94~125 | MODBUS_TCP 키·읽기 요청 인코딩과 등록 그룹 읽기 발사를 대조했다. |
| 104 | 등록 방식·설정 입력을 설비 등록 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L27~27 | SECS·Socket·PLC 등록 창과 통신 설정을 소유한 문서임을 확인했다. |

### 6. UDP·직렬 통신이나 UNKNOWN으로 연결할 수 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 114 | 현재 전송 구현은 TCP이고 UDP·SERIAL은 미구현이다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/type/GatewayTransport.java @ d3636ab86763 ; GatewayTransport L4~22 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/TcpTransportRuntimeProvider.java @ d3636ab86763 ; transport L14~24 / nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/spi/TransportRuntimeProviderRegistryTest.java @ d3636ab86763 ; pocRegistersTcpOnly L36~44 | 운영 소스 구현 검색에서 TCP provider 한 개만 발견했다. 시험 코드도 UDP·SERIAL 미등록을 기대한다. 시험 실행 결과로 기록하지 않았다. |
| 114 | UNKNOWN은 식별 불가 표식이며 외부 프로토콜 입력이 아니다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/type/CommInterfaceType.java @ d3636ab86763 ; UNKNOWN·fromText L28~60 | UNKNOWN 설명과 fromText switch의 세 통신 방식 허용·나머지 거부를 확인했다. |
| 116 | 미지원 통신은 설비 사양과 요구 방식을 담당자에게 전달한다. | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/TcpTransportRuntimeProvider.java @ d3636ab86763 ; transport L14~24 | 인계 권고다. 사용자에게 구현되지 않은 설정·내부 조작을 안내하지 않는다. |
| 116 | 설정값 변경만으로 지원 범위를 늘릴 수 없다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/type/GatewayTransport.java @ d3636ab86763 ; GatewayTransport L4~22 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/TcpTransportRuntimeProvider.java @ d3636ab86763 ; transport L14~24 | enum 이름의 존재와 실제 제공 구현의 존재를 구별했다. |

### 7. PLC 값을 쓰는 명령도 지원하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 125 | PLC 기능은 읽기 폴링 범위다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/codec/PlcProtocolCodec.java @ d3636ab86763 ; 읽기 계약 L29~47 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/test/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireActionTest.java @ d3636ab86763 ; fireEncodesRegistersCorrelatorAndSends L68~77 | 읽기 계약과 읽기 프레임·발사 기대값을 확인했다. 소스상 코덱 구현은 ModbusTcpCodec 한 개다. |
| 125 | PLC 쓰기 송신 명령은 프레임 생성에서 거부한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/PlcOutboundFrameEncoder.java @ d3636ab86763 ; encode L21~28 | encode가 UnsupportedOperationException을 항상 던진다. |
| 127 | 쓰기 요청에는 설비·영역·하려는 제어를 인계한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/PlcOutboundFrameEncoder.java @ d3636ab86763 ; encode L21~28 | 미지원 제어를 사용자가 실행하게 하지 않는 인계 권고다. |
| 127 | 제공하는 폴링 조작은 설비 명령 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L28~28 | 기존 PLC 폴링 그룹 입력·Start·End 절을 확인했다. |

### 8. 메뉴가 안 보이거나 작업이 거부되면 미지원인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 136 | 메뉴 숨김과 서버 요청별 권한 검사. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveVisibleAppNavigationGroups L146~152 / nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; RequirePermission L82~109 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 | 보유 권한으로 메뉴를 걸러내고 서버가 매칭 API 권한 중 하나의 보유 여부를 확인한다. |
| 136 | 메뉴 부재·권한 거부를 기능 미지원으로 단정하지 않는다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveVisibleAppNavigationGroups L146~152 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 | 지원 기능이라도 계정별로 숨김·차단될 수 있다는 위 구현의 경계를 설명했다. |
| 138 | 화면·작업·시각·문구를 전달해 권한을 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 | 권한 오류 인계 권고이며 임의 기본 권한 코드나 현장 부여 상태를 만들지 않았다. |
| 138 | 업무별 코드는 권한 코드 문서가 소유한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L72~72 | 목록에서 권한 코드별 의미·요구 권한의 소유 문서를 대조했다. |

### 9. 화면에서 임의 메시지 전송을 시작할 수 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 148 | 임의 메시지 전송 시작 전용 단추가 없다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 전체 업무 라우트 L78~109 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 설비 명령 API L571~845 / nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 행 메뉴 L934~1013 | 전체 src 검색의 송신 진입점 부재와 설비·배포 메뉴를 대조했다. EQP_SEND_MESSAGE는 작업 표시 분기에서만 발견됐다. |
| 148 | 전송 필요 시 설비·메시지·목적을 담당자에게 전달한다. | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 설비 명령 API L571~845 | 화면에서 할 수 없는 전송의 인계 권고다. |
| 150 | 이미 생성된 전송 결과 조회는 지원한다. | nori-tc-ui-front/src/features/deploy/lib/deploy-utils.ts @ 9fe69a75dcd3 ; 메시지 전송 작업 표시 L90~99 / nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 행 메뉴 L934~1013 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 이력·생성 L135~181 | 메시지 작업 표시 분기와 View·이력 조회 경로를 대조했다. |
| 150 | 전송 결과 찾기는 전용 조회 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L36~36 | 기존 문서의 EQP_SEND_MESSAGE 결과 조회 절을 확인했다. |

### 10. 처리실패 화면에서 재처리와 격리 해제를 할 수 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 163 | 화면에 실패 메시지 재처리·격리 해제·Repair Task 복구 실행이 없다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 전용 API L35~58 / nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~108 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L103~112 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L69~108 / nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L94~103 | 네 패널의 단순 TableRow와 조회 전용 API를 확인했다. 삭제 서버 API를 재처리 기능이라고 쓰지 않았다. |
| 163 | 새로고침·조회 오류 재시도는 목록 재조회다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 새로고침·재시도 L101~132 | 두 단추 모두 onRefresh를 호출하고 패널의 onRefresh는 query.refetch다. |
| 165 | 범주·ID·시각·사유를 담당자에게 인계한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~108 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L69~108 | 목록에서 확인 가능한 항목을 골라 인계한다. 내부 복구 명령은 넣지 않았다. |
| 165 | 범주별 인계 정보는 실패 처리 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L33~33 | 기존 범주별 조회·인계 절과 소유 경계를 확인했다. |

### 11. 로그인 화면에서 계정을 직접 찾거나 복구할 수 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 174 | 아이디·비밀번호 찾기에는 복구 요청이 연결되지 않았다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99 | form submit은 preventDefault, 양쪽 단추는 onClose다. auth API에는 로그인·로그아웃만 있다. |
| 174 | 확인은 창을 닫고 이메일·재설정 링크 발송을 요청하지 않는다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 찾기·확인 문구 L20~32 | 확인 표시 문구와 type=button/onClose를 대조했다. |
| 176 | 계정 확인·초기화는 운영 관리자에게 요청한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 관리자 비밀번호 초기화 L254~268 | 관리자 초기화 API가 userAdminFacade.resetPassword로 연결된다. 인증정보를 인계 기록에 요구하지 않는다. |
| 176 | 로그인·찾기 경계는 공통 조작 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L24~24 | 기존 찾기 창 절과 관리자 요청 안내를 확인했다. |

### 12. 진행 중인 배포를 화면에서 취소할 수 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ 판본·줄) | 확인한 내용 |
|---|---|---|---|
| 187 | 서버 취소 처리는 있으나 화면 취소 단추는 미연결이다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 행 메뉴 L934~1013 / nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; cancelDeploy L623~645 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; cancel L236~242 | 상태별 전체 행 메뉴와 cancel API를 대조했다. cancelDeploy 호출식은 전체 src 검색에서 0건이었다. |
| 187 | 배포 번호·설비·상태·시각을 담당자에게 넘긴다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 행 메뉴 L934~1013 | 화면 취소 부재에 대한 인계 권고이며 서버 호출 절차는 넣지 않았다. |
| 189 | 서버도 진행 중인 배포만 취소하고 적용 확정 작업은 거부한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; cancelDeploy L457~478 | cancelDeploy의 isCancelable 검사와 같은 파일 L585~599의 committedOperationIds 거부를 대조했다. |
| 189 | 실행·Hold·취소 차이는 배포 절차 문서로 안내한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L32~32 | 기존 실행·Hold·취소 절의 소유권을 확인했다. |

## 매뉴얼과 코드가 다른 곳

필수 원천의 지원 프로토콜·TCP 전송·운영 화면·시뮬레이터 범위는 확인한 코드와 일치한다. 이번 문서에 반영해야 할 확정된 매뉴얼/코드 충돌은 발견하지 않았다.

로그인 찾기 창의 이메일 안내는 실제 요청과 연결되어 있지 않다. `nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L24~32`의 표시 문구와 `src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 L60~95`의 preventDefault/onClose를 대조했다. 사용자 매뉴얼 `nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L103~117`도 미연결임을 명시하므로 매뉴얼/코드 충돌로 중복 집계하지 않았다. 검색 원문은 실제 경계만 썼다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 원천·미확인 이유 | 담당 역할 |
|---|---|---|
| 모든 설비의 자체 생성 값은 DCITEM_COMMON뿐이고 MES 미기동이면 데이터 평면 전체가 비어 있다는 일반화 | 구조 원천의 「데이터 흐름의 방향」 서술이다. 코드만으로 모든 실제 설비가 만드는 값을 확정할 수 없다. PLC의 읽기 발사(`StarterPlcPollFireAction L94~125`)와 연결 보고 우선 처리(`BusinessRuntimeTaskSupport L578~586`)도 있으므로 특정 Lot 사이클 설명을 TC 전체에 확대하지 않았다. 본문은 일반 업무 모델 매칭 조건과 확인된 경로만 썼다. | TC 연동 담당·설비 담당 |

현장 배포 판본·연락처·계정별 실제 권한·실제 접속 상태는 조사하지 않았다. 본문의 적용은 저장소 소스 판본이며 현장 동작 인증이 아니다.

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이번 문서에서 복제하지 않은 내용 |
|---|---|
| 00.rag/user/getting-started.md | 역할별 첫 작업 순서와 문서 길잡이 |
| 00.rag/user/tc-components-and-terms.md | Gateway·Business·Shard·Lease·모델·Flow 등 용어 정의와 연결 방향 비교 |
| 00.rag/user/lot-carrier-work-and-system-flow.md | Lot·Carrier·Port·MES·WM 개별 메시지의 업무 순서와 처리 |
| 00.rag/user/equipment-registration-and-settings.md | SECS·Socket·PLC 등록 항목·입력 조건·설정 변경·삭제 절차 |
| 00.rag/user/equipment-commands-and-polling.md | 시작·종료·재연결·폴링 단추, 입력·실패·결과 조건 |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 모델·JAR 적용 경계와 파라미터 버전 편집·배포 절차 |
| 00.rag/user/model-registration-and-branching.md | root·branch 생성·Check Out·Check In·부모 반영·삭제 |
| 00.rag/user/model-details-and-flow-editing.md | 메시지·Flow·Filter·Collect·MDF 편집 절차와 필수 조건 |
| 00.rag/user/live-map-status.md | 화면 상태·설비 선택·통신 로그 진입과 실패 표시 |
| 00.rag/user/live-map-layout-editing.md | 공정 보드·3D 배치·구조물 편집·저장 |
| 00.rag/user/demo-execution.md | 대상 준비·시나리오 선택·모드별 반복·실행·종료·업로드·결과 |
| 00.rag/user/user-group-and-permission-management.md | 계정·멤버·그룹 권한의 생성·변경·삭제·초기화 절차 |
| 00.rag/user/permission-code-reference.md | 업무 권한별 코드·의미·대응. 이번 문서는 특정 코드별 요구 조건을 새로 만들지 않았다. |
| 00.rag/user/message-results-and-communication-logs.md | 전송 결과 검색·배포 상세·통신 로그 조회 절차 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 실패·격리·Repair Task의 열·사유·기록 정리·상세 인계 |
| 00.rag/user/login-and-screen-basics.md | 로그인·프로필·찾기 창·공통 메뉴·표 조작 |
| 00.rag/user/deployment-workflow.md | 배포 입력·실행·Hold·재실행·취소·이력·삭제 |
| 00.rag/user/state-reference.md | 상태값별 뜻. UNKNOWN은 지원 프로토콜이 아니라는 구별에 필요한 최소 설명만 남겼다. |
| 00.rag/user/secs-hsms-message-and-timer-reference.md | SECS 메시지와 HSMS 세션·타이머 상세 |
| 00.rag/user/socket-framing-and-parsing-reference.md | 소켓 프레임·종료·해석 규칙 상세 |
| 00.rag/user/plc-modbus-register-and-type-reference.md | PLC 데이터 영역·타입·주소·배율 상세 |

구조 원천의 모듈 의존·빈 등록·설비별 순차성·격리·샤드 소유권·Kafka 봉투와 필드 계약은 사용자 지원 범위의 답에 필요한 범위를 넘어 본문에 넣지 않았다. 이는 주제 소유권에 따른 제외이며 보장 범위를 새로 검증했다는 뜻이 아니다. 운영 접근이 필요한 원문 링크나 내부 복구 명령을 user 본문에 넣지 않았다.

앞선 문서가 소유한 화면 미제공 사실은 사용자 요청대로 경계만 모았다. 기존 절차·열 목록·상태 뜻·권한 표·오류 대처를 복사하지 않고 관련 objectKey로 연결했다. 이번 문서의 PLC 지원 요약과 PLC 쓰기 제한은 각각 지원 범위와 제한 질문에 필요한 최소 조건으로 나누었으며 동일 자식 본문은 없다.

## 검사 결과

실행 환경은 GNU bash 5.2.37(1)-release (x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS이다. 검사기 파일·클래스는 변경하지 않았다.

Git Bash 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/tc-supported-scope.md
```

초안 검사 1회와 검색 원문 최종 편집 후 검사 1회를 실행했다. 두 실행 모두 exit 0·violations=0이다. 최종 원문은 189줄·12절·31자식이고 최대 자식 154자·최대 부모 329자이다. MASK·CROSS VIOLATION 출력은 없었다. 최종 검사 마지막 10줄:

```text
    L165-165 문단 116  | 복구가 필요하면 범주·항목 ID 또는 설비 ID·발생 시각·사유를 운영 담당자에게 전달한다. 범주별 인계 정보는 00.rag/user/dlq-quarantine-and-repair-task.md에서 확인한다.
§ L167-176 [29] 로그인 화면에서 계정을 직접 찾거나 복구할 수 있는가   parent=184 children=2 sources=3
    L174-174 문단 86  | 「아이디 찾기」·「비밀번호 찾기」창에는 계정 복구 요청이 연결되어 있지 않다. 「확인」은 창을 닫으며 아이디 발송이나 재설정 링크 발송을 요청하지 않는다.
    L176-176 문단 97  | 계정 확인이나 비밀번호 초기화는 운영 관리자에게 요청한다. 로그인과 찾기 창의 사용 경계는 00.rag/user/login-and-screen-basics.md에서 확인한다.
§ L178-189 [24] 진행 중인 배포를 화면에서 취소할 수 있는가   parent=202 children=2 sources=5
    L187-187 문단 97  | 배포 취소의 서버 처리는 있지만 확인한 화면에는 배포 자체를 취소하는 단추가 연결되어 있지 않다. 취소 요청은 배포 번호·대상 설비·현재 상태·시각을 운영 담당자에게 넘긴다.
    L189-189 문단 104  | 서버도 진행 중인 배포만 취소하며 적용이 확정된 작업이 있으면 거부한다. 화면의 실행·Hold와 취소의 차이는 00.rag/user/deployment-workflow.md에서 확인한다.
SUMMARY sections=12 children=31 maxChild=154 maxParent=329
CORPUS docs=17/53 sections=285 part={user=17} type={개념=4, 사용 절차=13}
TOTAL files=17 targets=1 violations=0
```

위 CORPUS 수는 당시 실제 파일 수이며 이 요청의 문서 번호 18과 별개다. 메인 세션의 검증 완료나 전체 53개 완료를 뜻하지 않는다.

사람 검사는 40절 미만이므로 최종 12절 전부를 읽었다.

| 절 | 제목의 질문 명확성 | 원천 일치 | 독립 답 | 다른 절·앞선 문서와 소유권 |
|---|---|---|---|---|
| 1 TC 중개 범위 | 적합 | 수신·모델 매칭·발행 분기 일치 | 일반 처리와 연결 보고 예외를 함께 명시 | Lot 상세·용어 정의 제외 |
| 2 화면 관리 대상 | 적합 | 라우트·문구·API·처리 연결 확인 | 관리 범위와 권한 조건 포함 | 절차는 기존 업무 문서에 둠 |
| 3 상태·실패 조회 | 적합 | 조회 API·범주 응답 일치 | 조회와 복구 실행 구별 | 열·상태값 상세 제외 |
| 4 시뮬레이터 | 적합 | 시나리오·모드·활성 역할 일치 | 시험 범위·시나리오 조건 포함 | 반복·시작·종료 절차 제외 |
| 5 지원 통신 | 적합 | 세 프로토콜의 처리·읽기 구현 확인 | 적용 소스와 모델·설정 조건 명시 | 프로토콜 상세 규칙 제외 |
| 6 미지원 전송·UNKNOWN | 적합 | TCP 구현·미등록 기대값·외부 파싱 확인 | 한계와 담당자 인계 포함 | UNKNOWN의 상태 참조 전체 설명 제외 |
| 7 PLC 쓰기 | 적합 | 읽기 계약·송신 거부 일치 | 읽기 범위와 제어 요청 인계 포함 | 폴링 조작 절차 제외 |
| 8 권한과 미지원 구별 | 적합 | 메뉴 필터·서버 인가 일치 | 제품 미지원과 계정 차이 구별 | 권한 코드 표 제외 |
| 9 메시지 전송 시작 | 적합 | 화면·API 검색과 작업 표시 대조 | 미제공 시작과 제공 결과 조회 구별 | 조회 순서 제외 |
| 10 재처리·해제 | 적합 | 네 패널·조회 API·갱신 단추 대조 | 재시도의 대상이 조회임을 명시 | 복구 절차를 생성하지 않음 |
| 11 로그인 계정 복구 | 적합 | 찾기 창 미연결·확인 동작 일치 | 실제 기능과 관리자 요청 포함 | 로그인·초기화 절차 제외 |
| 12 배포 취소 | 적합 | 화면 미연결·서버 검사 일치 | 화면/서버 지원 범위와 제한 포함 | 배포 실행·Hold 절차 제외 |

전수 문장 검사에서 주어·조건·실행/조회 대상·조사와 한국어 문체를 확인했다. 제목의 정확 중복·무의미한 틀 반복·빈 틀 문장·이름 토막 뜻풀이·오답 사례·잘린 문장·user 본문의 admin 경로는 발견하지 않았다. 단추가 없는 기능은 검색 범위와 실제 렌더를 함께 확인했으며 단순 키워드 0건만으로 결론 내리지 않았다.

미지원 확인 검색은 Python pathlib와 정규식으로 수행했다. 운영 Java 소스에서 `implements TransportRuntimeProvider`는 TcpTransportRuntimeProvider 한 곳, `implements PlcProtocolCodec`는 ModbusTcpCodec 한 곳이었다. 프런트 `src` 전체에서 `cancelDeploy\s*\(` 호출식은 0건이고 API에는 cancelDeploy 함수 정의가 있다. `sendMessage\s*\(|send-message|EQP_SEND_MESSAGE`는 deploy-utils.ts L96의 작업 표시만 발견됐다. DLQ 렌더의 클릭·행 메뉴·재처리·해제 연결을 검색하고 네 패널 및 공통 셸을 읽어 조회 전용임을 확인했다. 로그인 auth API의 실제 요청은 로그인·로그아웃이며 찾기 창은 별도 요청을 하지 않는다.

UTF-8·BOM 없음·CR 없음·NUL 없음·NFC를 최종 바이트에서 다시 확인했다. 원천 52개 고유 파일의 저장소 HEAD와 각 경로 status를 최종 대조했고 모두 수정 없음이었다. 작성 원문 외의 제품 파일·기존 문서는 변경하지 않았다.

제품 build·시험·화면 실행·현장 접속은 수행하지 않았다. 관련 시험의 기대값 읽기와 RAG 문서 검사를 구별한다. 자체 사람 검사는 메인 세션의 독립 사실 검증을 대체하지 않는다.

독립 AI 호출·재위임은 0회다. 공급자 사용량·비용과 전체 요청 wall time은 별도 계측하지 않아 미확인이다. 다음 작업은 메인 세션의 이 두 파일 검증이며 업로드·다른 문서 수정은 포함하지 않는다.

검사용 임시 파일과 시험 프로세스는 만들지 않았다. 지정 작업 폴더의 기존 work 디렉터리는 마지막 조회에서 내용이 없었으며 이 세션이 만들지 않은 부모 디렉터리는 보존했다. 정리할 소유 임시 파일은 없다.

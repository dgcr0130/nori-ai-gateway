# TC 지원 범위·하지 않는 일
```rag-meta
유형: 개념
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-simulator 5e9b02e3ae16 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## TC는 설비와 상위 시스템 사이에서 무엇을 지원하는가
```rag-meta
원천: nori-tc-exec/docs/Architecture/00-system/01-what-is-nori-tc.md @ f1d5e0a8e727 ; 무엇을 중개하는가
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMesEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L43~56
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessWmEventKafkaSubscriber.java @ 7db24f7466bd ; onMessage L42~55
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; executeNonUiTask L578~605
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/dispatch/BusinessWorkflowMdfAutoPublisher.java @ 7db24f7466bd ; publishIfRequired L85~127
```

TC는 설비 통신과 MES·WM의 메시지를 연결하고, 설비에 연결한 모델의 처리 규칙에 따라 업무를 실행한다. 메시지 전송 정의의 목적지에 따라 설비·MES·WM으로 전달한다.

일반 업무 메시지는 사용할 모델과 일치하는 처리 흐름이 있어야 실행한다. 연결 시 Port 상태 보고는 별도 공통 처리 경로를 사용한다. Lot·Carrier 작업의 상세 흐름은 `00.rag/user/lot-carrier-work-and-system-flow.md`에서 다룬다.

## 화면에서 관리할 수 있는 대상은 무엇인가
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 업무 화면 라우트 L93~109
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 메뉴 연결 L79~135
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 문구 L1092~1103
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 설비 변경·명령 L577~845
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 모델 생성 L730~741
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 저장 L611~649
원천: nori-tc-ui-front/src/features/model/api/model-flow.api.ts @ 9fe69a75dcd3 ; Flow 생성·변경 L68~116
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 배포 생성 L595~605
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 실행·이력 메뉴 L934~1013
원천: nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 관리 화면 조립 L12~30
원천: nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 계정 저장 L159~197
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 그룹·소속·권한 요청 L219~431
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 등록·변경·명령 L260~314
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 연결 명령 L322~443
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 파라미터 편집·저장 L139~193
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd ; Flow 생성·변경 L65~145
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 이력·생성 L135~181
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 배포 실행 L252~258
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 계정 생성·변경 L124~156
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 그룹 소속 변경 L182~211
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 그룹 관리 L122~155
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 그룹 권한 변경 L213~241
```

TC 화면은 다음 대상을 관리한다. 메뉴 진입 권한과 각 작업의 권한이 있어야 사용할 수 있다.

| 화면 | 지원하는 업무 범위 |
|---|---|
| 설비 | 설비 등록·설정과 연결 명령, 설비 파라미터 관리 |
| 모델의 SECS·Socket·PLC | 통신 방식별 모델과 메시지·Flow 정의 관리 |
| 적용의 배포 | 대상 설비의 변경 등록·실행과 진행·이력 조회 |
| 시스템의 사용자·그룹 | 계정·그룹·소속과 그룹 권한 관리 |

화면별 입력 조건과 실행 절차는 `00.rag/user/getting-started.md`에서 업무에 맞는 문서를 찾아 확인한다.

## 상태와 실패는 어디까지 화면에서 조회하는가
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모니터링 라우트 L85~90
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모니터링 문구 L1091~1097
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 요청 L35~58
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; getLiveMap L47~51
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 범주별 조회 L81~209
```

「모니터링」의 「Live-Map」은 설비 상태를, 「처리실패」는 실패 메시지·격리 설비·복구 작업 기록을 조회하는 범위다. 조회 화면이 있다는 것과 그 화면에서 복구를 실행할 수 있다는 것은 구별한다.

상태를 읽는 방법은 `00.rag/user/live-map-status.md`, 실패 목록의 열과 인계 항목은 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 확인한다.

## 실제 설비나 MES 없이 시험하는 기능이 있는가
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 데모 열기 L247~256
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapDemoModal.tsx @ 9fe69a75dcd3 ; 시나리오 선택 L767~785
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 데모 문구 L468~470
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; startSimulatorRun L56~62
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/simulator/LiveMapSimulatorRunFacade.java @ 985d17c604dd ; startRun L81~109
원천: nori-tc-simulator/scenario-engine/src/main/java/com/nori/tc/simulator/scenario/ModeProfile.java @ 5e9b02e3ae16 ; of L38~60
```

TC는 시뮬레이터로 설비·MES 역할을 시험하는 데모를 지원한다. 「Live-Map」의 「데모 시작」에서 시나리오와 「설비만 연결」·「MES만 연결」·「모두 연결」 중 시험 범위를 고른다.

데모는 선택한 시나리오의 실행이다. 대상 준비·시작·종료와 결과 확인은 `00.rag/user/demo-execution.md`에서 다룬다.

## 지원하는 설비 통신 방식은 무엇인가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/type/CommInterfaceType.java @ d3636ab86763 ; fromText L44~60
원천: nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/pipeline/HsmsInboundPipeline.java @ d3636ab86763 ; drain L94~129
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/pipeline/SocketInboundPipeline.java @ d3636ab86763 ; drain L92~126
원천: nori-tc-gateway/gateway/tc-gateway-plc-modbus/src/main/java/com/nori/tc/gateway/plc/modbus/ModbusTcpCodec.java @ d3636ab86763 ; protocol·encodeReadRequest L18~54
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireAction.java @ d3636ab86763 ; fire L94~125
```

확인한 소스에서 설비 통신은 다음 세 방식으로 구분한다. 설비의 통신 방식에 맞는 모델과 설정을 준비해야 한다.

| 통신 방식 | 구현된 범위 |
|---|---|
| SECS | HSMS를 통한 SECS-II 메시지 처리 |
| SOCKET | 선택한 프레임 규칙과 해석기로 TCP 메시지 처리 |
| PLC | MODBUS_TCP로 정해진 데이터 영역을 주기적으로 읽기 |

등록할 통신 방식과 설정 입력은 `00.rag/user/equipment-registration-and-settings.md`에서 확인한다.

## UDP·직렬 통신이나 UNKNOWN으로 연결할 수 있는가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/type/GatewayTransport.java @ d3636ab86763 ; GatewayTransport L4~22
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/TcpTransportRuntimeProvider.java @ d3636ab86763 ; transport L14~24
원천: nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/spi/TransportRuntimeProviderRegistryTest.java @ d3636ab86763 ; pocRegistersTcpOnly L36~44
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/type/CommInterfaceType.java @ d3636ab86763 ; UNKNOWN·fromText L28~60
```

확인한 판본의 전송 구현은 TCP이며 UDP·SERIAL(직렬 통신)은 구현되어 있지 않다. UNKNOWN은 통신 방식을 판정하지 못했다는 표식으로, 등록할 통신 프로토콜이 아니다.

UDP·직렬 통신이 필요한 설비는 요구 통신 방식과 설비 사양을 운영 담당자에게 전달해 연동 개발 범위를 협의한다. 설정값을 바꾸는 작업만으로 지원 범위를 늘릴 수는 없다.

## PLC 값을 쓰는 명령도 지원하는가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/codec/PlcProtocolCodec.java @ d3636ab86763 ; 읽기 계약 L29~47
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/PlcOutboundFrameEncoder.java @ d3636ab86763 ; encode L21~28
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/test/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcPollFireActionTest.java @ d3636ab86763 ; fireEncodesRegistersCorrelatorAndSends L68~77
```

확인한 판본의 PLC 기능은 읽기 폴링 범위다. PLC에 값을 쓰는 송신 명령은 지원하지 않으며 송신 프레임 생성 단계에서 거부한다.

쓰기 제어가 필요하면 설비 ID·대상 데이터 영역·수행하려는 제어를 운영 담당자에게 전달한다. 화면에서 제공하는 폴링 조작은 `00.rag/user/equipment-commands-and-polling.md`에서 확인한다.

## 메뉴가 안 보이거나 작업이 거부되면 미지원인가
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveVisibleAppNavigationGroups L146~152
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; RequirePermission L82~109
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
```

메뉴는 계정의 진입 권한에 따라 숨겨지고 서버는 요청별 작업 권한을 검사한다. 메뉴가 없거나 권한 거부가 나온 경우에는 제품의 미지원 기능으로 단정하지 않는다.

사용하려던 화면·작업·발생 시각·표시 문구를 운영 관리자에게 전달해 권한을 확인한다. 업무별 권한 코드는 `00.rag/user/permission-code-reference.md`에서 다룬다.

## 화면에서 임의 메시지 전송을 시작할 수 있는가
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 전체 업무 라우트 L78~109
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 설비 명령 API L571~845
원천: nori-tc-ui-front/src/features/deploy/lib/deploy-utils.ts @ 9fe69a75dcd3 ; 메시지 전송 작업 표시 L90~99
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 행 메뉴 L934~1013
```

확인한 화면에는 임의 메시지를 입력해 전송을 시작하는 전용 단추가 없다. 전송을 요청해야 하면 대상 설비·보내려는 메시지·요청 목적을 운영 담당자에게 전달한다.

이미 생성된 전송 작업의 결과 조회는 지원한다. 결과를 찾는 방법은 `00.rag/user/message-results-and-communication-logs.md`에서 확인한다.

## 처리실패 화면에서 재처리와 격리 해제를 할 수 있는가
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 전용 API L35~58
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~108
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L103~112
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L69~108
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L94~103
원천: nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 새로고침·재시도 L101~132
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 조회·삭제 경계 L81~209
```

「처리실패」의 실패 메시지 재처리·격리 해제와 Repair Task 복구 실행은 화면에서 제공하지 않는다. 「새로고침」과 조회 오류 아래 「재시도」는 목록을 다시 읽는 단추다.

복구가 필요하면 범주·항목 ID 또는 설비 ID·발생 시각·사유를 운영 담당자에게 전달한다. 범주별 인계 정보는 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 확인한다.

## 로그인 화면에서 계정을 직접 찾거나 복구할 수 있는가
```rag-meta
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 찾기·확인 문구 L20~32
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 관리자 비밀번호 초기화 L254~268
```

「아이디 찾기」·「비밀번호 찾기」창에는 계정 복구 요청이 연결되어 있지 않다. 「확인」은 창을 닫으며 아이디 발송이나 재설정 링크 발송을 요청하지 않는다.

계정 확인이나 비밀번호 초기화는 운영 관리자에게 요청한다. 로그인과 찾기 창의 사용 경계는 `00.rag/user/login-and-screen-basics.md`에서 확인한다.

## 진행 중인 배포를 화면에서 취소할 수 있는가
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 행 메뉴 L934~1013
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; cancelDeploy L623~645
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; cancel L236~242
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; cancelDeploy L457~478
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; cancelCoordinatedOperations L585~599
```

배포 취소의 서버 처리는 있지만 확인한 화면에는 배포 자체를 취소하는 단추가 연결되어 있지 않다. 취소 요청은 배포 번호·대상 설비·현재 상태·시각을 운영 담당자에게 넘긴다.

서버도 진행 중인 배포만 취소하며 적용이 확정된 작업이 있으면 거부한다. 화면의 실행·Hold와 취소의 차이는 `00.rag/user/deployment-workflow.md`에서 확인한다.

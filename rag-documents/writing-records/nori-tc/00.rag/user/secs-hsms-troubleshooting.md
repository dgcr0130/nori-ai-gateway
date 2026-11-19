# SECS·HSMS 작성 기록

## 대상

- objectKey: `00.rag/user/secs-hsms-troubleshooting.md`
- 구획·권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 확인일: 2026-11-19 (한국 시간)
- 검색 원문: `rag-documents/nori-tc/00.rag/user/secs-hsms-troubleshooting.md`
- 검색 원문 SHA-256: `76695c0a03e491c181194c3a121dea85384da50704e0c878c1570d95ce4dccd6`
- 검색 원문 줄 수: 117, H2 절 수: 8, 직렬화 자식 수: 26
- 변경 범위: 위 검색 원문과 이 작성 기록만 신규 작성했다. 제품 코드·설정·다른 문서·Git 인덱스는 변경하지 않았다.

## 원천 판본

| 저장소 | 확인한 HEAD | 작업 트리 확인 |
|---|---|---|
| nori-tc-gateway | d3636ab86763 | 본문에서 인용한 파일과 읽은 관련 시험·조립 파일 총 16개에 `git status --short -- <경로>`를 실행했다. exit 0, stdout 빈 문자열이었다. |
| nori-tc-ui-front | 9fe69a75dcd3 | EqpInfoTable.tsx·EqpManageFormModal.tsx·eqp.api.ts 및 추가 호출 흐름 EqpPage.tsx·useEqpDetail.ts를 경로 지정 검사했다. exit 0, stdout 빈 문자열이었다. |
| nori-tc-ui-backend | 985d17c604dd | EqpController.java를 경로 지정 검사했다. exit 0, stdout 빈 문자열이었다. 화면 단건 조회 연결을 보조 확인했으며 본문에는 서버 API 사용 절차를 추가하지 않았다. |

처음 형제 저장소에 Git을 실행할 때 OS 사용자 소유권 차이로 `detected dubious ownership` 오류가 났다. 이후 조회 명령에만 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소>`를 사용했다. 전역 설정은 변경하지 않았다. 상태 조회에는 사용자 전역 ignore 파일 접근 경고가 있었으며, 경로 지정 상태 조회의 종료코드와 stdout은 위와 같다.

지정 원천 `07c84fbc8a8cdeec2399e61e9cb058fb9e482b06`·`9876b7f75e3bc63c63dbc73a0ac780ae6eb0ba00`의 변경 내용과 관련 현행 코드·시험 기대값을 대조했다. `git merge-base --is-ancestor <커밋> HEAD`는 두 커밋 모두 exit 0이었다. 커밋 제목만으로 현재 동작을 확정하지 않았다.

작성 전 `docs/rag-document-writing-rules.md`·`docs/tc-rag-document-list.md` 전체를 읽었다. 기존 검색 원문 중 설비 등록·설정, 설비 명령·폴링, 메시지 결과·통신 로그, TC 구성 요소·용어, 지원 범위의 관련 부분을 대조했다. 목록에만 있고 아직 본문이 없는 문서의 내용을 만들어 인용하지 않았다.

관련 시험은 `HsmsEqpIdExtractorTest`·`HsmsSessionTimerTest`·`HsmsSessionOpenerTest`·`HsmsSessionTimerSchedulerTest`·`StarterHsmsSessionTimerActionTest`의 기대값을 읽었다. 제품 시험은 실행하지 않았으며 시험 PASS로 기록하지 않는다.

화면 명칭은 EqpInfoTable.tsx에 직접 적힌 영문 렌더 문자열이다. 선택 설비의 EqpPage.tsx → useEqpDetail.ts → eqp.api.ts의 GET `/eqp/{eqpId}` → EqpController.get의 조회·응답 연결도 읽었다. 새 화면 조작·저장·API 권한 절차는 본문에 추가하지 않고 기존 사용 절차 문서로 연결했다.

## 절별 사실 대조

인계 요청은 각 행의 코드가 실제로 구별하는 값·단계를 사용자에게 필요한 조사 항목으로 정리한 것이다. 현장 로그 존재·실제 장애 원인·현장 수정 적용 여부를 확인했다는 뜻이 아니다. 원천 위치의 경로는 모두 저장소 루트 기준이다.

### TCP 연결 뒤에도 SECS 데이터가 처리되지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 17 | SECS 여부와 ACTIVE/PASSIVE를 화면에서 기록 | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L120~138 | Comm Interface·Comm Mode 열은 각 설비의 값을 표시한다. 명칭은 하드코딩된 렌더 문구다. |
| 19 첫 문장 | SELECT 선행 조건 아래 SELECTED에서 수신 DATA 처리 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L176~184; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/pipeline/HsmsInboundPipeline.java @ d3636ab86763 L99~109 | requireSelectBeforeData와 세션 상태로 처리 허용을 결정하고 파이프라인은 허용된 DATA만 해석한다. |
| 19 둘째 문장 | 설비·시각·연결 표시를 넘겨 같은 연결의 SELECT 조사 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L107~115, L133~184 | 연결 개시 때 상태가 초기화되고 수신 SELECT가 세션 상태를 바꾼다. 현재 연결과 SELECT 완료를 함께 조사하는 인계로 한정했다. |
| 21 | 공통 연결·명령 문제의 문서 소유권 안내 | nori-ai-gateway/docs/tc-rag-document-list.md, 확정 문서의 설비 연결·명령 행 | 사용자 확정 소유 범위와 objectKey를 대조했다. 대상 문서의 미작성 본문을 현재 사실로 인용하지 않았다. |

### ACTIVE 설비에서 SELECT 응답을 기다리다 끊길 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 31 첫 문장 | ACTIVE의 연결 통지 후 SELECT_REQ·T6 대기 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L61~89; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L233~237 | SECS·컨텍스트 검사 후 타이머를 시작하고 ACTIVE만 SELECT_REQ를 만든다. createSelectReq가 T6 pending을 등록한다. |
| 31 둘째 문장 | 송신 유무부터 조사 요청 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L71~89 | 세션 개시가 건너뛰어진 경우와 SELECT 송신 경로를 구별할 수 있다. 사용자가 내부 로그를 직접 읽도록 지시하지 않았다. |
| 33 첫 문장 | 응답 시각·systemBytes 대조 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L141~145, L233~237 | 송신 systemBytes로 pending을 저장하고 수신 응답의 systemBytes로 제거한다. |
| 33 둘째 문장 | 일치 응답의 대기 해제·만료 종료 대상 | nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 L46~78; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsTimerKind.java @ d3636ab86763 L15~16 | 동일 systemBytes 응답으로 pending 0, 무응답 T6 만료는 종료 대상이라는 기대값과 구현을 확인했다. |
| 35 | 송신 없는 경우 세션 개시·판본 인계 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L61~89 | 프로필·mailbox 조건과 ACTIVE 분기를 조사 항목으로 정리했다. 송신 부재만으로 특정 원인을 확정하지 않았다. |

### PASSIVE 접속이 설비 ID 확인 단계에서 끝날 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 45 첫 문장 | S1F2 DATA의 ASCII 항목에서 식별 키 조회 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L117~200 | DATA·S1F2 검사 후 ASCII 항목을 모아 키를 찾는다. |
| 45 둘째 문장 | 기본 키 EQPID와 key=value 형식 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 L59~66; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L212~229 | EQPID 기본값과 `키=` 뒤 값 추출을 확인했다. 한국어 설비ID는 자리 표시이며 제조사 전송 예제가 아니다. |
| 47 첫 문장 | 수신·구조·키·값·등록 ID 순서로 조사 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L117~229; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 L235~242 | 식별 입력과 추출 ID의 등록 정보 조회를 조사 순서로 정리했다. |
| 47 둘째 문장 | 다른 메시지·키 누락·깨진 항목은 식별 실패 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L136~156, L168~229; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractorTest.java @ d3636ab86763 L73~135 | 각 실패는 empty 또는 다음 프레임 탐색으로 이어진다. |
| 47 셋째 문장 | Device ID·포트는 설비 ID 대체 식별 근거 아님 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L20~25, L117~156 | 실제 추출은 payload 키·값을 사용하고 Device ID·포트를 조회하지 않는다. |
| 49 첫 문장 | 미식별 상태의 제한 시간 후 종료 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L538~540, L703~725 | 추출 empty면 바인딩하지 않고, UNBOUND 제한 시간 만료 시 채널을 닫는다. HSMS T7과 혼동하지 않았다. |
| 49 둘째 문장 | BIND_TIMEOUT 유무 인계 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L711~723 | BIND_TIMEOUT 로그 위치를 확인했다. 샘플링 조건이 있으므로 기록이 반드시 존재한다고 쓰지 않았다. |

### S1F2에서 설비 ID를 읽었는데 연결이 거부될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 57 첫 문장 | 식별 후에도 여러 바인딩 거부 원인 존재 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 L235~296 | 등록 부재·정적/동적 소유권·활성화·통신 방식·모드·중복 연결 검사를 확인했다. 모든 가능한 실패를 나열했다는 주장은 하지 않았다. |
| 57 둘째 문장 | 식별과 연결 배정 성공 구별 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L538~542, L594~614 | 추출 결과를 받은 뒤 별도 bindPassive와 성공/실패 처리가 이어진다. |
| 59 첫 문장 | 추출 ID·기대 ID·시각으로 단계 구별 요청 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L538~542, L594~614 | 추출 없음과 bind 결과 거부가 서로 다른 분기다. |
| 59 둘째 문장 | 등록 SECS·PASSIVE 및 거부 결과 조사 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 L95~108, L269~280 | bindPassive의 기대 모드와 통신 방식 불일치 검사를 읽었다. |
| 61 | 설정 조작의 기존 소유 문서 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/equipment-registration-and-settings.md, Eqp Info Update에서 설정 바꾸기 절; nori-ai-gateway/docs/tc-rag-document-list.md | 실제 기존 절과 objectKey를 대조하고 저장 절차·입력 제한·권한을 복제하지 않았다. |

### PASSIVE 설비가 SELECT를 보냈는데도 진행되지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 71 첫 문장 | PASSIVE는 Gateway에서 SELECT_REQ 개시 안 함 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L77~88; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/test/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpenerTest.java @ d3636ab86763 L94~105 | 타이머 시작 뒤 PASSIVE에서는 송신 없이 반환한다. |
| 71 둘째 문장 | 세션에서 SELECT_REQ 처리 시 응답·SELECTED | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L133~138 | SELECTED 변경과 SELECT_RSP 반환을 확인했다. wire 송신 성공을 보장한다고 쓰지 않았다. |
| 73 첫 문장 | 식별 전 SELECT_REQ 소비 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L93~122; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/test/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractorTest.java @ d3636ab86763 L85~95 | control 프레임은 식별 없이 소비되며 테스트가 빈 버퍼와 ID 없음으로 고정한다. |
| 73 둘째 문장 | 식별 전 수신과 배정 후 처리 구별 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L511~540, L594~614; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/pipeline/HsmsInboundPipeline.java @ d3636ab86763 L94~108 | 식별 파서와 배정된 세션 파이프라인은 다른 경로다. |
| 75 첫 문장 | S1F2·배정·SELECT 수신 선후 조사 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 L519~540, L594~614; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L77~88 | 서로 다른 단계의 성공 여부와 순서를 인계한다. |
| 75 둘째 문장 | 식별 전에만 SELECT가 오면 설비 담당자와 순서 대조 | nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L93~122 | 식별 전 소비 사실에 따른 진단 안내다. 제조사 계약이나 무조건 재송신하면 성공한다는 조치는 지어내지 않았다. |

### HSMS 대기 초과와 연결 종료의 원인을 가를 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 84 | 만료 로그의 종류·연관값·경과 시간·시각 대조 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 L63~93 | warn에 eqpId·timer·systemBytes·elapsedMs가 있으며 종료 필요 결과이면 채널을 닫는다. 발생 시각은 사건을 연결하기 위해 전달할 정보다. |
| 88 | T3는 데이터 응답 조사, 단독 연결 종료 아님 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsTimerKind.java @ d3636ab86763 L12~13; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 L137~151 | T3_REPLY(false) 및 종료 요구 없음 기대값을 대조했다. |
| 89 | T6는 제어 요청·응답 조사, 종료 대상 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsTimerKind.java @ d3636ab86763 L15~16; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L233~237, L335~339 | SELECT와 LINKTEST의 pending 등록 및 T6 종료 플래그를 확인했다. |
| 90 | T7는 SELECT 처리 조사, 종료 대상 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L283~292; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsTimerKind.java @ d3636ab86763 L18~19 | 연결 기준 시각 이후 SELECTED가 아닌 상태의 만료다. |
| 91 | T8는 미완성 프레임 조사, 종료 대상 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L298~315; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsTimerKind.java @ d3636ab86763 L21~22 | 잔여 프레임 관측 입력과 T8 만료·종료 플래그를 확인했다. 정확한 바이트 간 도착 시각을 측정한다고 과장하지 않았다. |
| 93 | 타이머 의미·단위 참조 문서 안내 | nori-ai-gateway/docs/tc-rag-document-list.md, SECS·HSMS 메시지·타이머 행 | 목록의 소유 문서와 objectKey를 대조했다. 상세 기본값·표준 의미 표를 복제하지 않았다. |

### 데이터가 없는 동안 LINKTEST가 보이지 않거나 끊길 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 102 첫 문장 | 데이터 수신과 독립적인 타이머 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerScheduler.java @ d3636ab86763 L61~70; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerSchedulerTest.java @ d3636ab86763 L21~34 | scheduleWithFixedDelay와 수신 없는 반복 tick 기대값을 확인했다. |
| 102 둘째 문장 | 사용 설정·SELECTED 조건의 LINKTEST | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L321~342 | 사용 안 함 또는 미선택이면 반환하고, 초기 기준점을 거쳐 주기가 지나면 요청을 생성한다. |
| 104 첫 문장 | 사용 여부·SELECT·주기부터 조사 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L326~339 | 실제 요청 생성 조건을 순서대로 대조하도록 썼다. |
| 104 둘째 문장 | LINKTEST 응답 systemBytes·T6 조사 | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 L165~167, L335~339; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 L189~203 | LINKTEST 응답의 pending 해제와 무응답 T6 만료를 확인했다. |
| 106 첫 문장 | 설비·마지막 정상 통신·단절 시각 인계 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 L63~93 | 시각·설비로 요청/응답/만료를 연결하기 위한 최소 인계다. 수집된 실제 시각을 제시한 것은 아니다. |
| 106 둘째 문장 | 통신 로그 화면 사용 절차 소유권 | nori-ai-gateway/rag-documents/nori-tc/00.rag/user/message-results-and-communication-logs.md, 통신 로그 조회 관련 절; nori-ai-gateway/docs/tc-rag-document-list.md | 기존 절·objectKey를 확인했다. HSMS 제어 프레임이 화면에 반드시 나온다는 주장은 하지 않았다. |

### 같은 HSMS 증상이 반복될 때 수정 판본을 확인하는 방법

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 115 | 수정 커밋별 구현 범위와 적용 조건 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L77~89; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerScheduler.java @ d3636ab86763 L61~70; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L117~156 | 07c84fbc·9876b7f7 변경 내용과 현행 구현을 대조했다. 두 커밋은 HEAD의 조상이다. |
| 117 첫 문장 | 운영 담당자에게 배포 판본의 수정 포함 대조 요청 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L77~89; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L117~156; 두 커밋의 merge-base 종료코드 | 저장소 포함과 현장 배포 포함을 구별했다. UI에 판본 확인 단추가 있다는 설명은 하지 않았다. |
| 117 둘째 문장 | 관측 정보와 SELECT/식별 단계 구분 결과 전달 | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/HsmsSessionOpener.java @ d3636ab86763 L61~89; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/HsmsEqpIdExtractor.java @ d3636ab86763 L117~156 | 반복 증상을 과거 결함으로 단정하지 않고 확인한 단계와 관측값을 인계하도록 썼다. |

## 매뉴얼과 코드가 다른 곳

목록이 지정한 직접 원천은 두 수정 커밋이며 특정 매뉴얼 절은 지정하지 않았다. 이번 본문에 채택한 사실에서 매뉴얼·코드 충돌로 별도 판정한 항목은 없다. 읽지 않은 매뉴얼 전체의 일치를 주장하지 않는다.

추가로 HsmsEqpIdExtractor.java L24~25와 해당 단위 시험은 식별 프레임 뒤 버퍼 보존을 말하지만, 현행 GatewayChannelHandler.java L847~851은 HSMS 바인딩 뒤 unboundInbox를 clear한다. 따라서 단위 파서의 버퍼 보존을 실제 업무 메시지 재처리 보장으로 확장하지 않았다. 제품 수정이나 다른 문서 수정은 수행하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 담당 역할 |
|---|---|---|
| 현장 배포에 두 수정이 포함됐다는 단정 | 저장소 HEAD와 조상 관계만 확인했고 현장 배포 자료를 읽지 않았다. 본문에는 운영 담당자에게 포함 여부를 대조하도록 하는 경로만 썼다. | 운영·배포 담당 |
| 제조사별 S1F2 항목 배열과 실제 설비의 SELECT 송신 순서 | 제조사 원본이나 실제 통신 자료가 없다. 코드가 허용하는 식별 형식만 썼다. | 설비 통신 담당 |
| 현장의 실제 타이머 값·세션 상태와 이번 장애의 확정 원인 | 현장 접근·재현·측정은 이번 작업 범위가 아니다. 진단 분기와 넘길 정보만 작성했다. | 운영·설비 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 이 문서에서 복제하지 않은 사실 |
|---|---|
| 00.rag/user/secs-hsms-message-and-timer-reference.md | 전체 메시지·타이머 의미표, 단위·기본값 나열. 이 문서에는 원인 구별과 연결 종료 영향에 필요한 최소 조건만 썼다. |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 통신 방식 공통의 연결·명령 접수·결과 실패 진단. |
| 00.rag/user/equipment-registration-and-settings.md | 설정 창 여는 순서, 저장, 필수 입력·범위, 권한과 실패 표시. |
| 00.rag/user/equipment-commands-and-polling.md | Eqp Start·End·Restart·Reconnect 사용 절차와 메뉴 노출 조건. |
| 00.rag/user/message-results-and-communication-logs.md | 통신 로그 목록·상세 조회, 갱신·빈 목록·조회 권한 조건. |
| 00.rag/user/tc-components-and-terms.md | 통신 계층 용어와 Comm Mode의 일반적인 접속 방향 설명. |

## 검사 결과

실행 도구는 GNU bash 5.2.37(1)-release (x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS, Python 3.12.10이다. 검사기는 제공된 check.sh와 RagDocCheck를 그대로 사용했다. 최종 두 파일을 바이트로 다시 읽어 BOM·CR·NUL 없음과 Unicode NFC를 확인했다.

검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/secs-hsms-troubleshooting.md
```

1차 실행 exit 1: 타이머 절 부모 길이가 455자로 450자 상한을 넘었다. 해당 절의 도입 문장만 줄였다. 마지막 원문 편집 뒤 2차 실행 exit 0: 부모 최대 449자·자식 최대 151자, 대상 위반 0건. MASK·CROSS VIOLATION 출력은 없었다. 검사기를 변경하지 않았다.

2차 실행 마지막 출력 10줄:

```text
§ L95-106 [33] 데이터가 없는 동안 LINKTEST가 보이지 않거나 끊길 때   parent=341 children=3 sources=3
    L102-102 문단 82  | HSMS 타이머는 데이터 수신과 독립적으로 점검한다. LINKTEST는 사용 설정이 켜져 있고 세션이 SELECTED인 경우에 주기 요청을 만든다.
    L104-104 문단 140  | LINKTEST가 보이지 않으면 운영 담당자에게 사용 여부·SELECT 완료 여부·주기 경과를 먼저 대조하도록 요청한다. 송신 뒤 끊기면 LINKTEST_REQ와 LINKTEST_RSP의 systemBytes 및 T6 만료 기록을 확인하도록 요청한다.
    L106-106 문단 117  | 사용자는 설비 ID·마지막 정상 통신 시각·끊긴 시각을 전달한다. 통신 로그를 화면에서 보는 절차는 00.rag/user/message-results-and-communication-logs.md에서 확인한다.
§ L108-117 [32] 같은 HSMS 증상이 반복될 때 수정 판본을 확인하는 방법   parent=210 children=2 sources=3
    L115-115 문단 92  | ACTIVE SELECT 개시와 독립 타이머는 수정 07c84fbc 이후 판본에서, PASSIVE S1F2 식별은 수정 9876b7f7 이후 판본에서 구현되어 있다.
    L117-117 문단 117  | 증상이 반복되면 운영 담당자에게 두 수정의 포함 여부를 배포 판본과 대조하도록 요청한다. 설비 ID·발생 시각·연결 모드·화면 결과와 함께, SELECT 단계인지 S1F2 식별 단계인지 확인한 결과를 전달한다.
SUMMARY sections=8 children=26 maxChild=151 maxParent=449
CORPUS docs=28/53 sections=446 part={admin=7, user=21} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=4}
TOTAL files=28 targets=1 violations=0
```

사람 검사에서는 40절 미만이므로 아래 8절 모두를 읽었다.

| 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 중복 소유 검사 |
|---|---|---|---|---|
| TCP 연결 뒤에도 SECS 데이터가 처리되지 않을 때 | 적합 | 조건부 DATA 처리와 일치 | 확인 대상·인계 포함 | 일반 연결 문제는 기존 소유 문서로 연결 |
| ACTIVE 설비에서 SELECT 응답을 기다리다 끊길 때 | 적합 | 개시·T6·응답 연관값과 일치 | 송신 없음/응답 없음 분기 | ACTIVE 개시 진단을 이 절에 소유 |
| PASSIVE 접속이 설비 ID 확인 단계에서 끝날 때 | 적합 | S1F2·키·제한 시간과 일치 | 입력 조건·인계 포함 | 식별 실패만 소유 |
| S1F2에서 설비 ID를 읽었는데 연결이 거부될 때 | 적합 | 식별 후 검사와 일치 | ID 비교·거부 조사 포함 | 설정 절차를 복제하지 않음 |
| PASSIVE 설비가 SELECT를 보냈는데도 진행되지 않을 때 | 적합 | 식별 전 소비와 세션 처리 구별 | 순서 조사·설비 담당 인계 포함 | PASSIVE SELECT 시점 진단만 소유 |
| HSMS 대기 초과와 연결 종료의 원인을 가를 때 | 적합 | 만료 종류·연결 종료 조건과 일치 | 로그 조사 값·분기 포함 | 참조표 전체를 복제하지 않음 |
| 데이터가 없는 동안 LINKTEST가 보이지 않거나 끊길 때 | 적합 | 독립 실행·조건·응답 대기와 일치 | 미송신/송신 뒤 단절 분기 | 타이머 개념 대신 무통신 진단에 한정 |
| 같은 HSMS 증상이 반복될 때 수정 판본을 확인하는 방법 | 적합 | 지정 수정과 현행 코드 대조 | 판본 대조·전달 정보 포함 | 수정 식별자·적용 조건을 이 절에서 소유 |

제목 정확 중복과 식별자 제거 후 3회 이상 반복 틀은 없었다. 동일 본문 자식 복제·정보 없는 틀 문장·이름 토막 뜻풀이·주어 소실·오답 인용·미해소 마스킹 손실은 발견하지 않았다. user 본문에 관리자 문서 objectKey나 내부 시스템 직접 조치 지시는 없다. 내부 확인이 필요한 항목은 모두 운영 담당자에게 넘길 조사 항목으로 썼다.

이번 검사는 작성자 자체 검사이며 메인 세션의 독립 검증이나 현장 통신 재현이 아니다. 제품 테스트·빌드·네트워크·서버·업로드는 실행하지 않았다. 독립 Agent 호출은 0회다. 도구별 실행 시간은 세션 출력에 있으며 전체 작업 시간·provider 사용량·비용은 별도 측정하지 않았다.

검사용 임시 파일을 만들지 않았으므로 정리할 소유 임시 파일은 없다. 생성한 두 파일은 요청된 유효 결과로 보존한다.

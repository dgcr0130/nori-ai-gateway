# SECS·HSMS 메시지·타이머 작성 기록

## 대상

- objectKey: `00.rag/user/secs-hsms-message-and-timer-reference.md`
- 버킷·구획·권한: nori-tc / user / [AI_DOC_READ]
- 유형: 참조
- 검색 원문 SHA-256: `6cfeb756eefd65c5396f679097100991d42b783c8eb953e55e9107659e28557f`
- 검색 원문: 235줄, H2 16절, 16749바이트
- 확인일: 2026-11-19 (한국 시간)
- 범위: 지정된 검색 원문과 작성 기록 두 파일. 업로드·제품 변경·git add/commit/push 없음.

## 원천 판본

아래 기존 확인 기록은 제품 수정 C 이전의 이력이다. 이번 작업 트리의 판본·상태는 이 절 끝의 수정 C 원천 표로 구분한다.

- nori-tc-gateway HEAD: `d3636ab86763`. 검색 원문에서 인용한 hsms 모듈 전체와 GatewayHsmsProperties, 런타임 context provider, runtime/hsms, HsmsOutboundFrameEncoder, HsmsSubProfileLoader, GatewayActiveReconnectCoordinator 경로를 쓰기 전에 git status --short로 확인했고 변경 행이 없었다.
- nori-tc-exec HEAD: `f1d5e0a8e727`. I 원천·구조 문서·Gateway 기본 설정 세 파일의 git status --short 결과 변경 행이 없었다.
- nori-ai-gateway의 규칙·목록 경로도 git status --short 결과 변경 행이 없었다. 기존 rag-documents 디렉터리는 untracked였으며 앞선 문서는 변경하지 않았다. git worktree list는 현재 main 하나였다.
- 형제 저장소 조회에는 명령별 `-c safe.directory=<정확한 저장소 절대경로>`를 사용했다. 전역 설정 변경은 없었다. status에 ignore 파일 접근 경고가 있었으나 종료코드는 0이고 대상 변경 행은 없었다.
- I §4.8 SECS-001~010과 구조 문서 `20-gateway/05-hsms-protocol.md` 전체를 읽고 코드와 대조했다.
- HsmsSessionTimerTest L46~220의 T6 경계·해제, T7, T8 기준점 리셋, T3 등록·해제, 무수신 LINKTEST 기대값을 읽었다. 제품 시험을 새로 실행하지는 않았다.
- HsmsSubProfileLoader L26~29도 읽어 DB에서 런타임으로 전달하는 값이 Device ID임을 확인했다.
- 작성 규칙·53개 목록을 전체 읽었다. 앞선 SECS·HSMS 문제 해결 문서 전체와 설비 등록·모델 상세·TC 용어·지원 범위의 관련 절을 읽었다. 메시지 조회·설비 모델 갱신 문서에서도 겹치는 기술값을 확인했다.

- 보완 원천: nori-tc-ui-front `9fe69a75dcd3`의 EqpManageFormModal.tsx L763~775·L1112~1218, nori-tc-ui-backend `985d17c604dd`의 JpaEqpCrudPort.java L467~490을 직접 읽고 각 경로의 git status --short에서 변경 행이 없음을 확인했다. GatewayEquipmentService L439~451·GatewayUiRuntimeControlSupport L274~286·HsmsRuntimeContextProvider L41~69·GatewayHsmsProperties L293~325도 같은 Gateway HEAD에서 다시 읽고 변경 행이 없음을 확인했다.

### 수정 C의 현재 원천

각 파일은 git status --short에서 M으로 확인했다. 기준 HEAD d3636ab86763과 구별해 현재 바이트 SHA-256을 사용한다.

| 저장소/경로 | 작업 트리 SHA-256 | 현재 줄 범위 |
|---|---|---|
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java | d140c3822df75d2fdd617b66109155200939ea5f53ef7c1cf6c402746fbdb0f3 | L41~86 |
| nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java | 7e3e501f651aeb2ca67679ba105884de34fe56d85d463e609b418bbfd5b33141 | L27~33 |
| nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java | 0b464a7edbaa409d6a422599eec2d65e8f120f178fb078fe4baef622feee7fc9 | L441~453 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java | ea2e78e28707ca1d32e84d01f72e787d09aee4075730acd982f9d0f4cb5b0d9b | L258~271 |

- GatewayActiveReconnectCoordinator도 수정 상태이나 재연결 지연의 의미는 유지된다. 해당 사실은 기존 HEAD 원천을 보존하고 현재 L389~401의 getActiveReconnectDelayMs 사용을 재확인했다.
- HSMS 상태 기계·HsmsTimerConfig·HsmsSessionConfig·HsmsOutboundFrameEncoder·GatewayHsmsProperties와 exec의 기본 설정 파일에는 대상 경로 status 변경 행이 없었다. 관련 기존 원천은 보존했다.
- EquipmentCommunicationSettingsTest.java L30~72의 초→ms·설비값 선택·기본값 대체·잘못된 설정 기대값을 직접 읽었다. 제품 시험은 다시 실행하지 않았으며 메인 세션의 시험을 자체 실행으로 기록하지 않는다.
- 작성 규칙 현재 본문을 다시 읽었고, 문서 전체를 A~D 변경과 대조했다. A의 Kafka 보안·B의 Secret 생성 스크립트·D의 PLC 순서값에 관한 본문은 이 문서에 없어 변경 대상이 아니다.

## 절별 사실 대조

문단은 문장별로, 표는 자료 행별로 대조한다. 각 표의 원천 위치는 검색 원문 rag-meta의 실제 코드 판본·줄 범위를 풀어서 기록한다. 부재 주장은 아래 전체 추적 소스 검색 결과도 함께 사용한다.

### SxFy에서 Stream과 Function을 읽는 법

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L16 | Gateway의 SECS 메시지명은 `S{stream}F{function}`으로 만든다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/Secs2Message.java @ d3636ab86763 ; 생성자·messageName L21~32; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/BasicSecs2Decoder.java @ d3636ab86763 ; decode L30~46 | messageName의 문자열 결합과 숫자 대입 예를 대조했다. |
| L16 | 예를 들어 S6F11은 Stream 6·Function 11이다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/Secs2Message.java @ d3636ab86763 ; 생성자·messageName L21~32; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/BasicSecs2Decoder.java @ d3636ab86763 ; decode L30~46 | messageName의 문자열 결합과 숫자 대입 예를 대조했다. |
| L20 | \| Stream \| 0~127 \| S 뒤의 숫자 \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/Secs2Message.java @ d3636ab86763 ; 생성자·messageName L21~32; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/BasicSecs2Decoder.java @ d3636ab86763 ; decode L30~46 | 생성자에서 stream 상한 127을 검사한다. |
| L21 | \| Function \| 0~255 \| F 뒤의 숫자 \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/Secs2Message.java @ d3636ab86763 ; 생성자·messageName L21~32; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/BasicSecs2Decoder.java @ d3636ab86763 ; decode L30~46 | 생성자에서 function 상한 255를 검사한다. |
| L23 | 기본 디코더는 Stream·Function·W-bit를 읽고 본문은 rawBody 바이트로 유지한다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/Secs2Message.java @ d3636ab86763 ; 생성자·messageName L21~32; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/BasicSecs2Decoder.java @ d3636ab86763 ; decode L30~46 | decode가 헤더 세 값과 원본 body를 Secs2Message에 전달한다. |
| L23 | 본문의 SECS-II 항목 구조는 이 디코더가 해석하지 않는다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/Secs2Message.java @ d3636ab86763 ; 생성자·messageName L21~32; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/secs/BasicSecs2Decoder.java @ d3636ab86763 ; decode L30~46 | decode가 헤더 세 값과 원본 body를 Secs2Message에 전달한다. |

### W-bit와 systemBytes가 나타내는 것

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L35 | \| W-bit \| 응답을 기대하는 표시이다. Stream 바이트의 최상위 비트 0x80에 담고 Stream 숫자에서는 제외한다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; W-bit·systemBytes 직렬화 L9~19; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; toBytes·parse L39~74; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 대기 해제 L141~179; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; 송신 헤더 생성 L89~99 | toBytes의 0x80 결합과 parse의 0x7F 분리를 대조했다. |
| L36 | \| systemBytes \| 헤더의 4바이트 상관값이다. 응답 대기는 수신 프레임의 같은 systemBytes로 해제한다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; W-bit·systemBytes 직렬화 L9~19; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; toBytes·parse L39~74; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 대기 해제 L141~179; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; 송신 헤더 생성 L89~99 | 헤더 6번 오프셋의 4바이트 쓰기와 pending 맵 제거 키를 대조했다. |
| L38 | 확인한 업무 명령 송신 인코더는 W-bit를 false로 고정한다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; W-bit·systemBytes 직렬화 L9~19; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; toBytes·parse L39~74; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 대기 해제 L141~179; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; 송신 헤더 생성 L89~99 | 송신 헤더 생성의 false 상수와 수신 wBit 보존을 대조했다. |
| L38 | 수신 W-bit 보존과 송신 W-bit 설정은 구별한다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; W-bit·systemBytes 직렬화 L9~19; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; toBytes·parse L39~74; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 대기 해제 L141~179; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; 송신 헤더 생성 L89~99 | 송신 헤더 생성의 false 상수와 수신 wBit 보존을 대조했다. |

### HSMS 프레임의 길이와 헤더 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L49 | \| length \| 10 + 본문 바이트 수 \| 10바이트 헤더와 본문 길이이다. 앞의 4바이트 length 필드는 포함하지 않는다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsFrame.java @ d3636ab86763 ; 길이 검사·DATA 판별 L6~36; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; 범위·헤더 배치 L12~51; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsPType.java @ d3636ab86763 ; SECS_II L22~22; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsFrameEncoder.java @ d3636ab86763 L28~41 | length 불변식과 인코더의 4+length 할당을 대조했다. |
| L50 | \| deviceId \| 0~65535 \| 헤더 앞 2바이트에 담는 Device ID이다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsFrame.java @ d3636ab86763 ; 길이 검사·DATA 판별 L6~36; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; 범위·헤더 배치 L12~51; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsPType.java @ d3636ab86763 ; SECS_II L22~22 | 생성자의 범위 검사와 offset 0의 UInt16 쓰기를 확인했다. |
| L51 | \| pType \| SECS_II = 0x00 \| Gateway는 SType이 DATA이고 pType이 SECS_II인 프레임을 SECS-II 데이터로 판별한다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsFrame.java @ d3636ab86763 ; 길이 검사·DATA 판별 L6~36; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; 범위·헤더 배치 L12~51; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsPType.java @ d3636ab86763 ; SECS_II L22~22 | isDataMessage의 두 조건과 SECS_II 상수를 확인했다. |
| L52 | \| sType \| HsmsSType 코드 \| 데이터와 제어 메시지 종류를 구별하는 헤더 값이다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsFrame.java @ d3636ab86763 ; 길이 검사·DATA 판별 L6~36; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsHeader.java @ d3636ab86763 ; 범위·헤더 배치 L12~51; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsPType.java @ d3636ab86763 ; SECS_II L22~22 | 헤더에 sType.code를 기록하며 enum과 수신 분기로 종류를 가른다. |

### DATA와 SELECT의 SType 코드

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L62 | \| DATA \| 0 \| SECS 데이터 처리 여부를 판정한다. SELECT 선행 조건이 켜져 있으면 SELECTED에서만 허용한다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L12~20; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT·DATA 처리 L133~184 | DATA enum 값과 requireSelectBeforeData 분기를 확인했다. |
| L63 | \| SELECT_REQ \| 1 \| SELECTED로 바꾸고 SELECT_RSP를 만든다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L12~20; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT·DATA 처리 L133~184 | SELECT_REQ enum 값·상태 변경·응답 생성을 확인했다. |
| L64 | \| SELECT_RSP \| 2 \| 같은 systemBytes의 제어 응답 대기를 지우고 SELECTED로 바꾼다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L12~20; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT·DATA 처리 L133~184 | SELECT_RSP enum 값·systemBytes 기반 삭제·상태 변경을 확인했다. |

### DESELECT와 SEPARATE의 SType 코드

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L74 | \| DESELECT_REQ \| 3 \| NOT_SELECTED로 바꾸고 DESELECT_RSP를 만든다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L15~20; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 해제 처리 L148~174 | DESELECT_REQ enum 값·상태 변경·응답 생성을 확인했다. |
| L75 | \| DESELECT_RSP \| 4 \| 같은 systemBytes의 제어 응답 대기를 지우고 NOT_SELECTED로 바꾼다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L15~20; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 해제 처리 L148~174 | DESELECT_RSP enum 값·systemBytes 기반 삭제·상태 변경을 확인했다. |
| L76 | \| SEPARATE_REQ \| 9 \| NOT_SELECTED로 바꾸며 응답 프레임을 만들지 않는다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드 L15~20; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 해제 처리 L148~174 | SEPARATE_REQ enum 값·상태 변경·denyData 반환을 확인했다. |

### LINKTEST와 REJECT의 SType 코드

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L86 | \| LINKTEST_REQ \| 5 \| 연결 확인 요청에 LINKTEST_RSP를 만들고 선택 상태는 유지한다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드·미정의 값 거절 L12~59; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; LINKTEST·REJECT 처리 L160~189 | LINKTEST_REQ 분기에는 상태 변경 없이 응답 생성만 있다. |
| L87 | \| LINKTEST_RSP \| 6 \| 같은 systemBytes의 제어 응답 대기를 지우고 선택 상태는 유지한다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드·미정의 값 거절 L12~59; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; LINKTEST·REJECT 처리 L160~189 | LINKTEST_RSP 분기는 pendingControl을 지우고 상태는 건드리지 않는다. |
| L88 | \| REJECT_REQ \| 7 \| 데이터 처리를 허용하지 않으며 선택 상태는 유지한다. 별도 거절 사유 해석은 하지 않는다. \| | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드·미정의 값 거절 L12~59; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; LINKTEST·REJECT 처리 L160~189 | REJECT_REQ는 enum에 있으나 별도 처리 없이 마지막 denyData로 간다. |
| L90 | 정의되지 않은 SType 코드는 헤더 해석 중 오류로 거절한다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java @ d3636ab86763 ; 코드·미정의 값 거절 L12~59; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; LINKTEST·REJECT 처리 L160~189 | 헤더 parse가 fromCode를 호출하며 미정의 값은 예외다. |

### Gateway가 만드는 제어 프레임의 공통값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L98 | Gateway가 생성하는 SELECT_REQ·SELECT_RSP·DESELECT_RSP·LINKTEST_REQ·LINKTEST_RSP는 본문이 없고 length가 10이다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsControlFrameFactory.java @ d3636ab86763 ; SELECT·DESELECT·LINKTEST 생성 L32~125; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 systemBytes 전달 L133~162 | 다섯 생성 함수의 공통 상수와 빈 body를 직접 대조했다. |
| L98 | Stream·Function은 0, W-bit는 false, pType은 SECS_II이다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsControlFrameFactory.java @ d3636ab86763 ; SELECT·DESELECT·LINKTEST 생성 L32~125; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 systemBytes 전달 L133~162 | 다섯 생성 함수의 공통 상수와 빈 body를 직접 대조했다. |
| L100 | 제어 응답은 요청의 systemBytes를 그대로 사용한다. | nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsControlFrameFactory.java @ d3636ab86763 ; SELECT·DESELECT·LINKTEST 생성 L32~125; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 응답 systemBytes 전달 L133~162 | 세 제어 요청 수신 분기가 h.systemBytes를 응답 팩터리에 전달한다. |

### 타이머 기본값은 어떤 설정에서 오는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L109 | 이 문서의 T3·T5·T6·T7·T8·LINKTEST 기본값은 저장소에 제공된 Gateway 설정값이며 시간 단위는 ms이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 필수값 검사·설정 전달 L85~125; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; toSessionDefaults L293~325; nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; HSMS 기본 설정 L45~54 | 제공 기본값 표의 출처와 ms 단위는 유지했다. Gateway 설정을 항상 사용한다는 문장만 제거했다. |
| L111 | 타이머 다섯 값과 LINKTEST 주기는 모두 0보다 커야 한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 필수값 검사·설정 전달 L85~125; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; toSessionDefaults L293~325; nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; HSMS 기본 설정 L45~54 | 기존 Gateway 설정 바인딩의 필수·양수 검증 사실을 보존했다. |
| L111 | 설정 바인딩 클래스는 이 값의 누락을 기동 시 거부한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 필수값 검사·설정 전달 L85~125; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; toSessionDefaults L293~325; nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; HSMS 기본 설정 L45~54 | 기존 Gateway 설정 바인딩의 필수·양수 검증 사실을 보존했다. |
| L113 | 화면 입력과 저장 검증은 `00.rag/user/equipment-registration-and-settings.md`의 「SECS Settings의 입력 범위 확인하기」에서 다룬다. | rag-documents/nori-tc/00.rag/user/equipment-registration-and-settings.md @ sha256=3286079dc33b2b262c4b2cedb1577d1404f259ca0a7525c318d0b56966ea1147 ; SECS Settings의 입력 범위 확인하기 | 기존 화면 입력·검증 문서 안내를 보존했다. |

### 설비 화면의 HSMS 값은 세션에 적용되는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L127 | 설비별 HSMS 설정 적용 수정이 들어간 판본에서는 「SECS Settings」에 저장한 「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」을 세션 타이머로 쓴다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | provider L63~65는 설비 저장값 다섯 개에 1000L을 곱해 HsmsTimerConfig를 생성한다. |
| L127 | 저장 단위 초를 ms로 바꾼다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | provider L63~65는 설비 저장값 다섯 개에 1000L을 곱해 HsmsTimerConfig를 생성한다. |
| L129 | 「Link Test Enabled」·「Link Test Interval」·「Max Message Bytes」도 세션에 반영한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | provider L66~67은 저장된 사용 여부·주기(1000L 변환)·최대 메시지 크기를 세션 설정에 전달한다. |
| L129 | Link Test Interval은 초를 ms로 바꾼다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | provider L66~67은 저장된 사용 여부·주기(1000L 변환)·최대 메시지 크기를 세션 설정에 전달한다. |
| L131 | 설비 HSMS 설정이 없으면 Gateway 기본값을 쓴다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | provider L41~43은 Device ID의 설비값 우선, L57~59는 HSMS 설정 부재 시 Gateway 기본값 사용이다. |
| L131 | 「Device ID」는 설비 값이 없을 때 Gateway 설정값을 쓴다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | provider L41~43은 Device ID의 설비값 우선, L57~59는 HSMS 설정 부재 시 Gateway 기본값 사용이다. |
| L133 | 잘못된 설정은 설비 ID·설정값이 담긴 오류로 실패한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | provider L68~69가 설정 검증·수량 변환·null 오류를 설비 ID와 설정값이 담긴 예외로 전환한다. |
| L135 | 화면 값과 실제 동작 시간이 다르면 운영 담당자에게 설비 ID·화면 입력값·관측한 만료 시점을 전달하고 Gateway 설정 확인을 요청한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력·표시 L1112~1218; nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; HSMS 저장 요청 L763~775; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; HSMS 저장 L467~490; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java @ a5e5221853b7 ; 설비 HSMS 설정 전달 L27~33; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ a5e5221853b7 ; 저장값 프로필 복사 L441~453; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ a5e5221853b7 ; 프로필 재구성 L258~271; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; Device ID 선택·설비 설정·기본값 L41~86; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; 세션 기본 설정 L293~325 | 인계 문장은 보존했다. 현재는 설비 저장값과 설정 적용 여부·판본까지 대조하는 근거로 사용한다. |

### T3 데이터 응답 대기의 값과 등록 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L148 | \| tc.gateway.hsms.timer.t3-reply-timeout-ms \| 45000 ms \| | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T3 L46~46; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; DATA 대기 해제·등록 L176~184; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; registerOutboundDataReplyExpectation L240~250; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; encode·헤더 생성 L29~99; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; T3 기대값 L137~168 | 제공 파일의 키·45000과 바인딩 검사를 대조했다. |
| L150 | T3는 상태 기계에 데이터 응답 대기를 등록한 시점부터 잰다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T3 L46~46; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; DATA 대기 해제·등록 L176~184; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; registerOutboundDataReplyExpectation L240~250; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; encode·헤더 생성 L29~99; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; T3 기대값 L137~168 | 대기 등록 기한과 DATA 수신 시 pendingData.remove를 확인했다. |
| L150 | 같은 systemBytes의 DATA를 받으면 대기를 해제한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T3 L46~46; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; DATA 대기 해제·등록 L176~184; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; registerOutboundDataReplyExpectation L240~250; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; encode·헤더 생성 L29~99; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; T3 기대값 L137~168 | 대기 등록 기한과 DATA 수신 시 pendingData.remove를 확인했다. |
| L152 | 기준 소스에서 등록 메서드의 호출은 시험 코드에만 있다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T3 L46~46; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; DATA 대기 해제·등록 L176~184; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; registerOutboundDataReplyExpectation L240~250; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; encode·헤더 생성 L29~99; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; T3 기대값 L137~168; 이 기록의 전체 추적 소스 git grep 명령 결과 | 전체 추적 소스 호출 검색과 송신 인코더 전체를 대조했다. |
| L152 | 업무 명령 송신 경로는 T3 대기를 자동 등록하지 않는다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T3 L46~46; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; DATA 대기 해제·등록 L176~184; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; registerOutboundDataReplyExpectation L240~250; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/HsmsOutboundFrameEncoder.java @ d3636ab86763 ; encode·헤더 생성 L29~99; nori-tc-gateway/gateway/tc-gateway-hsms/src/test/java/com/nori/tc/gateway/hsms/session/HsmsSessionTimerTest.java @ d3636ab86763 ; T3 기대값 L137~168; 이 기록의 전체 추적 소스 git grep 명령 결과 | 전체 추적 소스 호출 검색과 송신 인코더 전체를 대조했다. |

### T5 설정값의 현재 사용 범위

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L164 | \| tc.gateway.hsms.timer.t5-connect-timeout-ms \| 10000 ms \| | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T5 L47~47; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; T5 전달 L56~86; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/config/HsmsTimerConfig.java @ d3636ab86763 ; T5 보관·검사 L14~26; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; scheduleReconnect L354~399 | 제공 파일의 키·10000과 바인딩 검사를 대조했다. |
| L166 | 기준 소스의 T5는 설정 객체로 전달해 보관한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T5 L47~47; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; T5 전달 L56~86; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/config/HsmsTimerConfig.java @ d3636ab86763 ; T5 보관·검사 L14~26; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; scheduleReconnect L354~399; 이 기록의 전체 추적 소스 git grep 명령 결과 | T5 전달·보관 코드 및 전체 추적 소스의 부재 검색을 대조했다. |
| L166 | T5 값으로 연결 대기나 재연결 간격을 계산하는 제품 사용처는 없다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T5 L47~47; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; T5 전달 L56~86; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/config/HsmsTimerConfig.java @ d3636ab86763 ; T5 보관·검사 L14~26; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; scheduleReconnect L354~399; 이 기록의 전체 추적 소스 git grep 명령 결과 | T5 전달·보관 코드 및 전체 추적 소스의 부재 검색을 대조했다. |
| L168 | ACTIVE 재연결 예약은 별도의 Netty 재연결 지연 설정을 사용한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T5 L47~47; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; T5 전달 L56~86; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/config/HsmsTimerConfig.java @ d3636ab86763 ; T5 보관·검사 L14~26; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; scheduleReconnect L354~399 | reconnect scheduler가 nettyProperties.getActiveReconnectDelayMs를 사용한다. |
| L168 | T5 값을 재연결 간격으로 해석하지 않는다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T5 L47~47; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; T5 전달 L56~86; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/config/HsmsTimerConfig.java @ d3636ab86763 ; T5 보관·검사 L14~26; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ d3636ab86763 ; scheduleReconnect L354~399 | reconnect scheduler가 nettyProperties.getActiveReconnectDelayMs를 사용한다. |

### T6 제어 요청의 응답 대기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L179 | \| tc.gateway.hsms.timer.t6-control-timeout-ms \| 5000 ms \| | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T6 L48~48; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT 대기 등록 L233~237; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 만료·LINKTEST 대기 등록 L264~342 | 제공 파일의 키·5000과 바인딩 검사를 대조했다. |
| L181 | T6는 Gateway가 SELECT_REQ 또는 주기 LINKTEST_REQ를 만들며 등록하는 제어 응답 대기 시간이다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T6 L48~48; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT 대기 등록 L233~237; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 만료·LINKTEST 대기 등록 L264~342 | SELECT와 LINKTEST 등록 기한 및 expirePending 비교식을 대조했다. |
| L181 | 등록 시각에 T6 값을 더한 기한까지 대기가 남아 있으면 만료로 판정한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T6 L48~48; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; SELECT 대기 등록 L233~237; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 만료·LINKTEST 대기 등록 L264~342 | SELECT와 LINKTEST 등록 기한 및 expirePending 비교식을 대조했다. |

### T7 선택되지 않은 연결의 대기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L192 | \| tc.gateway.hsms.timer.t7-not-selected-timeout-ms \| 10000 ms \| | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T7 L49~49; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 연결 기준점 L107~115; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNotSelectedTimeout L283~292 | 제공 파일의 키·10000과 바인딩 검사를 대조했다. |
| L194 | T7는 연결 개시 시각을 기준으로 잰다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T7 L49~49; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 연결 기준점 L107~115; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNotSelectedTimeout L283~292 | 연결 개시 기준점·SELECTED 제외·경과 비교·기준점 소거를 확인했다. |
| L194 | 세션이 SELECTED이면 만료를 판정하지 않는다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T7 L49~49; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 연결 기준점 L107~115; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNotSelectedTimeout L283~292 | 연결 개시 기준점·SELECTED 제외·경과 비교·기준점 소거를 확인했다. |
| L194 | SELECTED가 아닌 상태에서 연결 개시 후 T7 이상 지났으면 만료를 보고하고 기준점을 지운다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T7 L49~49; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; 연결 기준점 L107~115; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNotSelectedTimeout L283~292 | 연결 개시 기준점·SELECTED 제외·경과 비교·기준점 소거를 확인했다. |

### T8 미완성 프레임의 대기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L205 | \| tc.gateway.hsms.timer.t8-network-interleave-timeout-ms \| 5000 ms \| | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T8 L50~50; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNetworkInterleaveTimeout L298~315; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 버퍼 잔량 전달 L63~68 | 제공 파일의 키·5000과 바인딩 검사를 대조했다. |
| L207 | T8는 타이머 점검에서 버퍼에 남은 미완성 프레임을 처음 관측한 시각부터 잰다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T8 L50~50; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNetworkInterleaveTimeout L298~315; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 버퍼 잔량 전달 L63~68 | 버퍼 잔량의 boolean 전달과 T8 세 분기를 확인했다. |
| L207 | 미완성 상태가 T8 이상 계속되면 만료를 보고한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T8 L50~50; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNetworkInterleaveTimeout L298~315; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 버퍼 잔량 전달 L63~68 | 버퍼 잔량의 boolean 전달과 T8 세 분기를 확인했다. |
| L207 | 미완성 프레임이 없다고 관측하면 기준점을 지운다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T8 L50~50; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; evaluateNetworkInterleaveTimeout L298~315; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 버퍼 잔량 전달 L63~68 | 버퍼 잔량의 boolean 전달과 T8 세 분기를 확인했다. |

### LINKTEST 사용 여부와 송신 주기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L217 | \| tc.gateway.hsms.linktest-enabled \| true \| 주기 LINKTEST_REQ 생성 여부 \| | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; LINKTEST L52~54; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; appendLinktestRequest L321~342 | 제공 파일의 true와 linktestEnabled 반환 조건을 확인했다. |
| L218 | \| tc.gateway.hsms.linktest-interval-ms \| 30000 ms \| LINKTEST_REQ 생성 간격 \| | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; LINKTEST L52~54; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; appendLinktestRequest L321~342 | 제공 파일의 30000과 elapsed 비교를 확인했다. |
| L220 | 주기 송신은 사용 설정이 켜져 있고 SELECTED인 세션에서 동작한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; LINKTEST L52~54; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; appendLinktestRequest L321~342 | 상태·사용 여부·첫 기준점·마지막 LINKTEST 송신 시각 갱신을 확인했다. |
| L220 | 첫 점검은 기준 시각만 잡고, 이후 주기가 지나면 요청을 만든다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; LINKTEST L52~54; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; appendLinktestRequest L321~342 | 상태·사용 여부·첫 기준점·마지막 LINKTEST 송신 시각 갱신을 확인했다. |
| L220 | 마지막 데이터 수신 시각으로 주기를 재설정하지 않는다. | nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; LINKTEST L52~54; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/session/HsmsSessionStateMachine.java @ d3636ab86763 ; appendLinktestRequest L321~342 | 상태·사용 여부·첫 기준점·마지막 LINKTEST 송신 시각 갱신을 확인했다. |

### 타이머 점검 주기와 증상 확인 경로

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L231 | \| tc.gateway.hsms.session-timer-tick-ms \| 1000 ms \| 데이터 수신과 독립적으로 타이머를 점검하는 간격 \| | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; sessionTimerTickMs L68~74; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/GatewayHsmsRuntimeConfiguration.java @ d3636ab86763 ; 스케줄러 설정 L68~74; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerScheduler.java @ d3636ab86763 ; startSession L64~72 | 1000L 초기값과 스케줄러 인자·fixed delay 호출을 대조했다. |
| L233 | 타이머 점검은 고정 지연 방식으로 반복하므로 설정된 제한 시각과 실제 만료 감지 시각은 구별한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; sessionTimerTickMs L68~74; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/GatewayHsmsRuntimeConfiguration.java @ d3636ab86763 ; 스케줄러 설정 L68~74; nori-tc-gateway/gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/runtime/HsmsSessionTimerScheduler.java @ d3636ab86763 ; startSession L64~72 | scheduleWithFixedDelay 호출과 tick에서의 관측 방식을 대조했다. |
| L235 | SELECT·S1F2 식별·대기 초과의 증상 확인은 `00.rag/user/secs-hsms-troubleshooting.md`, 모델 편집 절차는 `00.rag/user/model-details-and-flow-editing.md`에서 다룬다. | docs/tc-rag-document-list.md @ 70cf6cd84db6 ; 확정 문서 목록; rag-documents/nori-tc/00.rag/user/secs-hsms-troubleshooting.md @ sha256=76695c0a03e491c181194c3a121dea85384da50704e0c878c1570d95ce4dccd6 ; SECS 관련 절; rag-documents/nori-tc/00.rag/user/model-details-and-flow-editing.md @ sha256=32dcb7ddbfbc7673815d04ab20b7753319c52d8af8f1a249c107df360745c173 ; SECS 관련 절 | 목록의 소유 범위와 기존 두 검색 원문의 실제 절을 읽어 안내 경로를 대조했다. |


## 매뉴얼과 코드가 다른 곳

| 항목 | 문서 설명 | 확인한 코드와 검색 원문 반영 |
|---|---|---|
| 타이머 출처 | 구조 문서 L343~344는 운영값이 DB tc_eqp_hsms에서 온다고 적는다. | 수정 C 이전에는 Device ID만 전달됐으나 현재 HsmsSubProfileLoader L27~33과 provider L56~69에서 설비 HSMS 저장값을 전달·적용한다. 이번 변경으로 DB 설정 적용 사실은 구조 문서 설명과 일치한다. |
| T5 효과 | 구조 문서 L346~348은 T5를 재연결 코디네이터 관심사로 설명한다. | 전체 t5Connect/t5-connect 검색상 시간 계산 사용처가 없다. GatewayActiveReconnectCoordinator L389~399는 Netty 지연값을 사용한다. 검색 원문 L166~168은 보관되는 값과 실제 재연결을 구별한다. |
| 미정의 SType | I §4.8 SECS-003·005의 실패 응답은 알 수 없는 sType을 데이터 처리 불가로 본다고 적는다(L333·335). | HsmsHeader L72~83에서 HsmsSType.fromCode를 호출하며 fromCode L54~59는 예외를 던진다. REJECT_REQ처럼 정의된 제어의 미처리 분기와 구별해 검색 원문 L90에 헤더 해석 오류라고 적었다. |

T3의 자동 활성은 구조 문서가 보장하는 사실로 확대하지 않았다. 상태 기계에 등록 메서드가 있지만 전체 추적 코드에서 호출은 시험 두 곳뿐이며 송신 인코더 W-bit도 false이다. 본문 L150~152에 등록 조건과 현재 송신 경로의 한계를 함께 적었다.

부재 확인에 사용한 실제 읽기 전용 명령:

```powershell
git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway -C ../nori-tc-gateway grep -n -E 't5Connect|t5-connect|registerOutboundDataReplyExpectation|reconnect.*[Dd]elay' -- gateway
```

T5 결과는 설정 바인딩·양수 검증·로그·설정 객체 보관·런타임 전달이었다. registerOutboundDataReplyExpectation의 호출은 HsmsSessionTimerTest L143·159뿐이었다. GatewayActiveReconnectCoordinator L389·399는 getActiveReconnectDelayMs를 사용했다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 제조사별 SxFy 업무 의미·방향·본문 항목·응답 계약 | 제조사 원본이 제공되지 않았으므로 일반 헤더 코드로 확정할 수 없다. | 설비 연동 담당·제조사 계약 담당 |
| T5가 실제 연결 제한 시간 또는 재시도 간격으로 작동한다는 의미 | 값 선언과 설명은 있으나 기준 HEAD의 제품 시간 계산 사용처가 없다. 설정 키·값·보관 범위만 남겼다. | Gateway 개발 담당 |
| 현장 적용 타이머·배포 판본·설비별 실측 동작 | 소스와 제공 설정의 대조이며 원격 접속·현장 관측을 하지 않았다. | 운영 담당 |
| SxFy 숫자별 일반 표준 업무 의미와 모든 SECS-II 자료형 정의 | 기본 디코더는 번호와 rawBody만 보존한다. 별도 표준·제조사 원본 없이 내용을 보충하지 않았다. | 프로토콜·설비 연동 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 이 문서에서 복제하지 않은 내용 |
|---|---|
| 00.rag/user/secs-hsms-troubleshooting.md | S1F2 식별 키·설비 연결 배정, ACTIVE/PASSIVE SELECT 순서, 만료 후 연결 종료 여부와 증상 확인·인계 절차. 메시지 표의 전이는 코드값의 최소 의미만 다뤘다. |
| 00.rag/user/equipment-registration-and-settings.md | 화면 SECS Settings 입력·저장 범위·빈칸 기본값 표. 화면 빈칸 LINKTEST 값과 Gateway 제공값은 서로 다른 입력 경로이므로 복제하지 않았다. |
| 00.rag/user/model-details-and-flow-editing.md | Reference·SECS Message·Trigger 편집·저장 절차. objectKey로만 안내했다. |
| 00.rag/user/state-reference.md | 제품 전체 상태값 사전. 메시지의 직접 결과인 SELECTED/NOT_SELECTED 외에 상태 표를 확장하지 않았다. |
| 00.rag/admin/configuration-reference.md | 설정 파일 변경·주입 방법, Netty 재연결 지연 키·기본값 표. user 본문에는 admin objectKey·열람 지시를 넣지 않았다. |

## 최초 작성 검사 결과(보완 전 이력)

아래 15절·62행과 검사 출력은 보완 전 원문 SHA-256 `d792d47f937858613e6ccbc228ee3755ec6403dd271ea6628a06c6113bdb19ef`에 대한 이력이다. 메인 검증이 이후 설정 적용 설명 누락 1건을 발견했으며 현재 최종본의 보완 결과는 뒤 절에 따로 기록한다.

기계 검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/secs-hsms-message-and-timer-reference.md
```

- 마지막 검색 원문 편집 이후 초안 검사와 사실 대조 완료 후 최종 검사를 각각 실행했고 두 번 모두 종료코드 0이었다. 위반·CROSS VIOLATION·MASK 출력 없음.
- 검사기 실측: 15절·44자식, 최대 자식 148자·최대 부모 304자.
- 도구: GNU Bash 5.2.37(1)-release, Temurin OpenJDK 21.0.9+10-LTS, Python 3.12.10.
- 검사기·제품 코드를 수정하지 않았다. 문서 작성 작업이므로 제품 build·시험·서버 접속을 하지 않았다.
- 최종 검사의 마지막 출력 10줄:

```text
    L196-196 표 행 78  | 설정 키=tc.gateway.hsms.linktest-enabled | 제공 기본값=true | 역할=주기 LINKTEST_REQ 생성 여부
    L197-197 표 행 83  | 설정 키=tc.gateway.hsms.linktest-interval-ms | 제공 기본값=30000 ms | 역할=LINKTEST_REQ 생성 간격
    L199-199 문단 108  | 주기 송신은 사용 설정이 켜져 있고 SELECTED인 세션에서 동작한다. 첫 점검은 기준 시각만 잡고, 이후 주기가 지나면 요청을 만든다. 마지막 데이터 수신 시각으로 주기를 재설정하지 않는다.
§ L201-214 [19] 타이머 점검 주기와 증상 확인 경로   parent=280 children=3 sources=3
    L210-210 표 행 91  | 설정 키=tc.gateway.hsms.session-timer-tick-ms | 코드 기본값=1000 ms | 역할=데이터 수신과 독립적으로 타이머를 점검하는 간격
    L212-212 문단 54  | 타이머 점검은 고정 지연 방식으로 반복하므로 설정된 제한 시각과 실제 만료 감지 시각은 구별한다.
    L214-214 문단 133  | SELECT·S1F2 식별·대기 초과의 증상 확인은 00.rag/user/secs-hsms-troubleshooting.md, 모델 편집 절차는 00.rag/user/model-details-and-flow-editing.md에서 다룬다.
SUMMARY sections=15 children=44 maxChild=148 maxParent=304
CORPUS docs=40/53 sections=555 part={admin=9, user=31} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=2, 트러블슈팅=12}
TOTAL files=40 targets=1 violations=0
```

40절 미만이므로 15절 전체를 읽어 사람 검사를 했다.

| 절 제목 | 제목으로 질문 식별 | 원천과 일치 | 독립 답 | 사실 소유 경계 |
|---|---|---|---|---|
| SxFy에서 Stream과 Function을 읽는 법 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| W-bit와 systemBytes가 나타내는 것 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| HSMS 프레임의 길이와 헤더 값 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| DATA와 SELECT의 SType 코드 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| DESELECT와 SEPARATE의 SType 코드 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| LINKTEST와 REJECT의 SType 코드 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| Gateway가 만드는 제어 프레임의 공통값 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| 타이머 기본값은 어떤 설정에서 오는가 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| T3 데이터 응답 대기의 값과 등록 조건 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| T5 설정값의 현재 사용 범위 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| T6 제어 요청의 응답 대기 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| T7 선택되지 않은 연결의 대기 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| T8 미완성 프레임의 대기 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| LINKTEST 사용 여부와 송신 주기 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |
| 타이머 점검 주기와 증상 확인 경로 | 적합 | 적합 | 적합 | 진단·화면 절차 표 복제 없음 |

문장·표 행 대조 62행, 검색 자식 44개를 전수 확인했다.

- 제목의 정확 중복과 식별자 제거 후 3회 이상 반복되는 틀 없음. 타이머 제목은 등록 조건·사용 범위·제어 응답·연결 선택·미완성 프레임이라는 서로 다른 질문으로 나눴다.
- 빈 틀 문장·잘린 문장·이름 토막 뜻풀이 없음. enum 이름만 번역하지 않고 실제 분기와 설정 전달을 확인했다.
- 앞선 문서와의 동일 자식 검사 및 사실 소유 대조를 함께 했다. 증상 진단·입력 절차 표를 복제하지 않았다.
- mask-v1 적용 후 값 손실 없음. 표 머리글은 항목·필드·메시지·설정 키를 사용했다.
- user 본문에 내부 조작·admin 경로 안내·비밀값·접속 정보 없음. 설정 키와 제공값은 사용자가 요청한 메시지·타이머 참조 범위로만 수록했다.
- 작성 기록 표 생성의 첫 시도는 Python 정규식 `re.error: nothing to repeat at position 4`로 exit 1이었다. 당시 파일 쓰기 전 실패했다. PowerShell에서 stdin 스크립트를 넘길 때 UTF-8 OutputEncoding을 명시한 뒤, 같은 원문을 이용해 표 생성만 완료했다. 검색 원문과 검사기는 수정하지 않았다.
- 추가 독립 AI 호출 0회. 총 wall time·provider 사용량·비용은 별도 계측되지 않아 미확인이다.
- 검사용 임시 파일·서비스·브라우저를 만들지 않았다. 삭제 대상 없음.
- 남은 작성 결함 0건, 사용자 결정 항목 0건. 제품 동작을 실행 검증한 결과나 메인 세션의 독립 검증 PASS를 뜻하지 않는다.

## 메인 검증 결함 1건 보완(제품 수정 C 이전 이력)

이 절의 저장값 미적용 분석은 당시 판본에 대한 이력이다. 현재 적용 사실과 최종 판정은 아래 수정 C 반영 절을 따른다.

- 원인: 기존 L114는 Device ID와 타이머의 출처가 다르다고만 설명해, 화면에 저장한 타이머·LINKTEST·최대 프레임 값이 세션에 쓰이지 않는다는 직접적인 답이 빠졌다.
- 수정: 기존 L114를 입력·저장 검증 소유 문서 안내로 대체했다. 바로 뒤 L116~133에 독립 절을 추가해 저장과 세션 적용을 구별하고 Device ID 우선값·대체값, 운영 담당자에게 전달할 정보를 적었다.
- 보완 전 원문 SHA-256: `d792d47f937858613e6ccbc228ee3755ec6403dd271ea6628a06c6113bdb19ef`.
- 보완 전 작성 기록 SHA-256: `bcd2b83e8e7515d4ff68f14f807fa2b5f44539a86f975322552065e32148394e`.
- 보완 후 원문 SHA-256: `7703f4f2100d8bf693a5845c73e28786a78225afd69906e4e85ed7a02fd25794`.
- 영향 범위: 보완 부분만 되돌린 메모리상의 원문 SHA-256이 보완 전 원문 SHA-256과 일치함을 확인했다. 검색 원문 L114 및 신규 절만 변경했다. 뒤 절의 본문·원천은 보존하고 작성 기록의 검색 원문 줄 번호를 19줄씩 이동했다. 제품 저장소와 앞선 검색 원문은 수정하지 않았다.

저장된 HSMS 필드의 제품 사용처를 다시 확인한 명령:

```powershell
git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway -C ../nori-tc-gateway grep -n -E '\.(t3Timeout|t5Timeout|t6Timeout|t7Timeout|t8Timeout|linkTestEnabled|linkTestInterval|maxMsgBytes)\(' -- gateway ':!**/src/test/**'
```

결과는 GatewayEquipmentService L443~450의 DB 값 복사와 GatewayUiRuntimeControlSupport L279~286의 프로필 재구성뿐이었다. 세션 타이머·LINKTEST·최대 프레임 계산에는 호출이 없으며 provider L41~69 및 toSessionDefaults L293~325가 Gateway 설정을 사용한다.

### 보완 후 검사

- 동일 check.sh --dump 명령을 마지막 검색 원문 편집 뒤 실행했다. 종료코드 0, violations=0, CROSS VIOLATION·MASK 출력 없음.
- 최종 원문 16절·48자식, 최대 자식 148자·최대 부모 399자. 새 절은 부모 399자·자식 최대 142자로 제한 안에 있다.
- 사실 대조표는 최종 원문 줄 기준 65행이다. 화면 단위·저장 검증은 복제하지 않고 설비 등록 문서를 가리켰다.
- 원천의 실제 줄 범위·UI 표시 문구·저장 필드·세션 생성 인자를 다시 대조했다. Device ID 대체 조건은 화면 빈칸 입력 일반화가 아니라 설비 정보의 값 부재 조건으로 적었다.
- 사람 검사는 최종 16절 전체를 읽고 제목·독립 답·중복·마스킹·권한 경계를 확인했다. 기존 모호한 문장과 중복된 새 설명은 없으며 내부 조작·admin 문서 열람 지시를 넣지 않았다.
- 다음 출력은 보완 최종본의 검사 결과이다.

```text
    L215-215 표 행 78  | 설정 키=tc.gateway.hsms.linktest-enabled | 제공 기본값=true | 역할=주기 LINKTEST_REQ 생성 여부
    L216-216 표 행 83  | 설정 키=tc.gateway.hsms.linktest-interval-ms | 제공 기본값=30000 ms | 역할=LINKTEST_REQ 생성 간격
    L218-218 문단 108  | 주기 송신은 사용 설정이 켜져 있고 SELECTED인 세션에서 동작한다. 첫 점검은 기준 시각만 잡고, 이후 주기가 지나면 요청을 만든다. 마지막 데이터 수신 시각으로 주기를 재설정하지 않는다.
§ L220-233 [19] 타이머 점검 주기와 증상 확인 경로   parent=280 children=3 sources=3
    L229-229 표 행 91  | 설정 키=tc.gateway.hsms.session-timer-tick-ms | 코드 기본값=1000 ms | 역할=데이터 수신과 독립적으로 타이머를 점검하는 간격
    L231-231 문단 54  | 타이머 점검은 고정 지연 방식으로 반복하므로 설정된 제한 시각과 실제 만료 감지 시각은 구별한다.
    L233-233 문단 133  | SELECT·S1F2 식별·대기 초과의 증상 확인은 00.rag/user/secs-hsms-troubleshooting.md, 모델 편집 절차는 00.rag/user/model-details-and-flow-editing.md에서 다룬다.
SUMMARY sections=16 children=48 maxChild=148 maxParent=399
CORPUS docs=41/53 sections=574 part={admin=9, user=32} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=3, 트러블슈팅=12}
TOTAL files=41 targets=1 violations=0
```

- 제품 시험·업로드·git add/commit/push는 하지 않았다. 임시 파일·서비스를 만들지 않아 이번 보완의 정리 대상이 없다.
- 요청한 결함 1건을 보완했고 자체 확인 기준 남은 결함 0건·사용자 결정 0건이다. 메인 세션의 재검증 판정을 대신하지 않는다.

## 제품 수정 C 반영(2026-11-19)

- 보완 전 검색 원문 SHA-256: `7703f4f2100d8bf693a5845c73e28786a78225afd69906e4e85ed7a02fd25794`.
- 보완 전 작성 기록 SHA-256: `e6b122d904407ea6abc28022de3edbd10403d889408da2fca2d8d9947c05ec3e`.
- 보완 후 검색 원문 SHA-256: `6cfeb756eefd65c5396f679097100991d42b783c8eb953e55e9107659e28557f`.
- 수정 범위: H1 적용 판본, 기본값 절의 항상 Gateway 설정 사용 설명, 설비 화면 설정 적용 절, 변경된 provider의 T5 원천 메타와 직접 영향 기록만 보완했다. 그 외 문장·표·기존 commit 원천은 보존했다.
- 기본값 표의 수치와 Gateway 설정 바인딩의 양수 검증은 그대로다. 실제 세션은 설비 HSMS 설정이 있으면 저장값을 선택하고 없으면 기본값을 선택한다.
- T3 재확인: registerOutboundDataReplyExpectation 호출은 여전히 시험 L143·159뿐이다. 상태 기계 L176~184·L240~250을 다시 읽었으며 업무 송신의 자동 등록 경로는 추가되지 않았다.
- T5 재확인: 현재 provider가 저장된 t5Timeout을 ms로 변환해 보관하지만, 연결 대기·재연결 간격 계산에 사용하는 제품 경로는 없다. 재연결은 현재 coordinator L389~401에서도 별도 Netty 지연값을 사용한다.
- 관련 읽기 전용 명령: `git grep -n -E "registerOutboundDataReplyExpectation|t5ConnectTimeoutMs|t5-connect-timeout-ms|getActiveReconnectDelayMs" -- gateway` 및 제품 소스만의 동일 검색 결과를 대조했다.
- 현재 사실 대조표 68행을 최종 원문 줄로 연결했다. 이전 자체 검사·보완 출력은 당시 해시에 대한 이력으로 보존한다.
- 제품 시험·소스 변경·다른 문서 변경·커밋·업로드·네트워크 작업 없음. 검사용 임시 자원을 만들지 않았다.

### 이번 검사 실행의 Git 소유자 확인

- 첫 검사 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/secs-hsms-message-and-timer-reference.md`.
- 첫 결과는 exit 1, `TOTAL files=51 targets=1 violations=46`이었다. 46건은 모두 기존 commit:path를 읽지 못한 원천 오류였으며 예시는 다음과 같다.

```text
VIOLATION L184 원천 commit:path 없음 'nori-tc-exec/apps/tc-gateway-app/config/tc-gateway.properties @ f1d5e0a8e727 ; T7 L49~49'
```

- Git 2.52.0.windows.1에서 `git -C ../nori-tc-gateway show d3636ab86763:gateway/tc-gateway-hsms/src/main/java/com/nori/tc/gateway/hsms/frame/HsmsSType.java`를 직접 실행해 같은 읽기 실패의 원문을 확인했다.

```text
fatal: detected dubious ownership in repository at 'C:/workspace_nori/nori-tc/nori-tc-gateway'
```

- 같은 원천을 `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway -C ../nori-tc-gateway show ...`로 읽었을 때 exit 0이고 실제 파일 본문이 반환됐다. 원천이 없는 것으로 판정하지 않았다.
- 검사기·전역 Git config·저장소 config를 수정하지 않았다. 아래 환경변수는 재검사 프로세스에만 전달하며 기존 원천 검사·마스킹·중복 검사를 그대로 실행한다.

```powershell
$env:GIT_CONFIG_COUNT='4'
$env:GIT_CONFIG_KEY_0='safe.directory'
$env:GIT_CONFIG_VALUE_0='C:/workspace_nori/nori-tc/nori-tc-gateway'
$env:GIT_CONFIG_KEY_1='safe.directory'
$env:GIT_CONFIG_VALUE_1='C:/workspace_nori/nori-tc/nori-tc-exec'
$env:GIT_CONFIG_KEY_2='safe.directory'
$env:GIT_CONFIG_VALUE_2='C:/workspace_nori/nori-tc/nori-tc-ui-front'
$env:GIT_CONFIG_KEY_3='safe.directory'
$env:GIT_CONFIG_VALUE_3='C:/workspace_nori/nori-tc/nori-tc-ui-backend'
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/secs-hsms-message-and-timer-reference.md
```

### 수정 C 반영 최종 검사

- 마지막 검색 원문 편집 이후 위 명령을 실행한 최종 결과는 exit 0, violations=0이다. MASK·CROSS VIOLATION 출력은 없다.
- 16절·49자식, 최대 자식 148자·최대 부모 434자로 크기 제한을 충족한다. 새 적용 설명의 초→ms·설정 선택·오류 조건을 마스킹 후에도 읽을 수 있다.
- 문서 전체 16절을 다시 읽고 A~D에 따른 추가 변경 필요 여부를 확인했다. A·B·D 관련 사실은 없으며, T3 등록·T5 사용처 설명과 그 밖의 기존 본문은 유지했다.
- 사실 대조표 68행의 문장·표 행을 최종 원문의 실제 줄과 일대일 대조했다. 제목·독립 답·문장·중복·권한 경계의 미해소 지적은 없다.
- 변경 범위 대조: 기본값·설비 설정 적용 절을 제외한 앞부분은 H1 적용 줄 외 동일하고, 뒷부분은 T5 provider 원천 메타 외 동일함을 수정 전 원문과 직접 비교했다.
- 기대값을 읽은 추가 시험 파일의 판본은 EquipmentCommunicationSettingsTest.java sha256=4bb74d9a959d1ed03a0cce049b41c22c51d0b35c5dd95855268c4c4c3d23c3c5 L30~72이다. 시험을 새로 실행하지 않았다.
- 검사의 마지막 출력 10줄:

```text
    L217-217 표 행 78  | 설정 키=tc.gateway.hsms.linktest-enabled | 제공 기본값=true | 역할=주기 LINKTEST_REQ 생성 여부
    L218-218 표 행 83  | 설정 키=tc.gateway.hsms.linktest-interval-ms | 제공 기본값=30000 ms | 역할=LINKTEST_REQ 생성 간격
    L220-220 문단 108  | 주기 송신은 사용 설정이 켜져 있고 SELECTED인 세션에서 동작한다. 첫 점검은 기준 시각만 잡고, 이후 주기가 지나면 요청을 만든다. 마지막 데이터 수신 시각으로 주기를 재설정하지 않는다.
§ L222-235 [19] 타이머 점검 주기와 증상 확인 경로   parent=280 children=3 sources=3
    L231-231 표 행 91  | 설정 키=tc.gateway.hsms.session-timer-tick-ms | 코드 기본값=1000 ms | 역할=데이터 수신과 독립적으로 타이머를 점검하는 간격
    L233-233 문단 54  | 타이머 점검은 고정 지연 방식으로 반복하므로 설정된 제한 시각과 실제 만료 감지 시각은 구별한다.
    L235-235 문단 133  | SELECT·S1F2 식별·대기 초과의 증상 확인은 00.rag/user/secs-hsms-troubleshooting.md, 모델 편집 절차는 00.rag/user/model-details-and-flow-editing.md에서 다룬다.
SUMMARY sections=16 children=49 maxChild=148 maxParent=434
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

- 추가 독립 AI 호출 0회. provider 사용량·비용·요청 전체 wall time은 별도 계측하지 않아 미확인이다. 검사기 첫 실패와 원인을 구별해 보존했고, 최종 성공으로 실패 이력을 덮어쓰지 않았다.
- 임시 파일·서비스를 만들지 않았다. 제품·다른 문서·검사기·Git 영속 설정은 변경하지 않았고 커밋·업로드도 하지 않았다.
- 이번 보완의 자체 확인 기준 남은 결함 0건, 사용자 결정 0건이다. 현장 적용과 메인 세션의 독립 검증 판정을 뜻하지 않는다.

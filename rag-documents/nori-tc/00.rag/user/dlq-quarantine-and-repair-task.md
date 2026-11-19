# DLQ·격리 설비·Repair Task
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-common 6fe71f6b2da6·nori-tc-exec f1d5e0a8e727·nori-common ae6791e99e91 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/login-and-screen-basics.md
```

## 처리실패 현황과 범주별 목록 열기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; DlqPage L19~109
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 처리실패 문구 L117~128
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 8.1 현황 확인 L2064~2084
```

1. 좌측 메뉴의 「처리실패」를 열어 「Gateway DLQ」·「Business DLQ」·「격리 설비」·「Repair Task」 카드의 건수를 확인한다.
2. 확인할 카드를 누른다. 아래 영역이 선택한 범주의 목록으로 바뀐다.
3. 선택한 카드를 다시 누르면 「최근 장애」가 있는 대시보드로 돌아간다.

조회가 끝나기 전 카드 건수는 「—」로 표시한다.

## 처리실패 조회 권한과 라인 범위 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; useDlqLineScope L14~49
원천: nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 ; filterDlqEntriesByLine L22~38
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; matchMethod L253~258
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 조회 기본 권한 L114~158
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·DLQ 기본 권한 L764~770
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; LINE_READ L14~23
원천: nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 범주별 라인 필터 L41~57
```

저장소 기본 권한 데이터 기준으로 처리실패 조회에는 DLQ_READ가 필요하다. 라인별 표시에는 LINE_READ와 설비 조회 권한(EQP_READ·EQP_WRITE 중 하나)이 추가로 필요하다. 조회 권한이 거부되면 운영 관리자에게 범주·선택한 라인·발생 시각·오류 문구를 전달한다.

카드 건수·최근 장애·각 목록은 상단에서 고른 라인 기준으로 표시한다. 설비 ID가 없거나 조회한 설비 목록에서 찾을 수 없는 항목은 선택한 라인에 관계없이 표시한다. 선택한 라인을 해석하지 못하면 표시 목록이 비게 된다.

## 최근 장애에서 먼저 볼 항목 찾기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; 최근 장애 렌더 L48~92
원천: nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; DashboardPanel L101~174
원천: nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 조회 완료·오류 상태 L59~88
원천: nori-tc-ui-front/src/features/dlq/lib/dlq-feed.ts @ 9fe69a75dcd3 ; buildDashboardFeed·mapRepairTaskToFeed L58~85
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대시보드 문구 L119~124
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열 문구 L1071~1085
```

「최근 장애」는 네 범주의 항목을 합쳐 시간 역순으로 최대 20건을 표시한다. 「발생 시각」·「카테고리」·「설비」·「사유 코드」·「사유」로 확인할 범주를 고른다. Repair Task의 에러 코드와 에러 메시지도 이 표에서는 사유 코드와 사유에 표시한다.

조회 중에는 「불러오는 중입니다.」, 표시할 항목이 없으면 「최근 장애 없음」이 나온다. 대시보드는 조회 오류를 별도 표시하지 않으므로, 「최근 장애 없음」이나 건수 0이 보이면 범주 카드를 열어 목록의 오류 여부도 확인한다.

## 실패 목록에서 검색하고 쪽 바꾸기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151
원천: nori-tc-ui-front/src/features/dlq/lib/dlq-format.ts @ 9fe69a75dcd3 ; DLQ_PAGE_SIZE·filterDlqRows L4~26
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L43~95
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L48~100
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L37~93
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L35~91
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 목록 문구 L129~137
```

네 범주의 「키워드 검색」은 현재 라인 범위의 조회된 목록에서 모든 열의 표시값을 검색한다. 앞뒤 공백을 제거하고 영문 대소문자를 구별하지 않는 부분 일치 검색이다.

목록은 각 범주의 발생·격리·생성 시각이 최신인 순서다. 쪽당 건수는 처음 20건이며 20·50·100 중 고른다. 검색어나 쪽당 건수를 바꾸면 첫 쪽으로 이동한다.

표 위 「마지막 갱신」은 목록 데이터를 받은 시각이다. 현재 값을 다시 확인할 때 「새로고침」을 사용한다.

## Gateway DLQ에서 실패 상세 값 읽기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; buildGatewayDlqColumns L14~37
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Gateway 열 문구 L1067~1078
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchGatewayDlq L35~40
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listGatewayDlq L81~88
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L31~59
```

「Gateway DLQ」카드를 눌러 목록에서 상세 값을 확인한다. 열은 「ID」·「설비」·「IF」·「소켓」·「단계」·「사유 코드」·「사유」·「발생 시각」·「payload 참조」·「TTL」 순서다.

「ID」로 실패 항목을 구별하고, 「IF」·「소켓」으로 통신 구분을, 「단계」·「사유 코드」·「사유」로 실패 내용을 확인한다.

「payload 참조」는 원문 자료의 참조값이다. 원문 확인이 필요하면 「ID」·설비 ID·발생 시각·사유·payload 참조값을 운영 담당자에게 전달한다. 참조값이 없으면 「-」로 표시한다.

## Business DLQ에서 메시지 위치 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Business 열 문구 L1066~1077
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchBusinessDlq L42~46
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listBusinessDlq L129~136
원천: nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisBusinessDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L39~88
```

「Business DLQ」카드를 누른다. 열은 「ID」·「출처」·「단계」·「토픽」·「파티션」·「오프셋」·「설비」·「타입」·「이름」·「사유 코드」·「사유」·「발생 시각」·「payload 참조」·「TTL」 순서다. 오른쪽 열은 표를 가로로 밀어 확인한다.

「출처」·「단계」·메시지 「타입」·「이름」과 사유를 확인한다. 「토픽」·「파티션」·「오프셋」은 원본 메시지가 수신된 위치를 나타낸다.

운영 담당자에게 「ID」·설비 ID·발생 시각·사유와 세 위치값을 함께 전달한다. 원문 확인이 필요하면 「payload 참조」도 함께 전달한다.

## Business 수신 거부 항목의 원본 찾기
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; rejectToDlq L189~217
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; map·doMap L67~131
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundRejectedException.java @ 7db24f7466bd ; 생성자 L17~28
원천: nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 ; filterDlqEntriesByLine L22~38
```

「출처」가 BUSINESS_INBOUND, 「단계」가 PRE_DISPATCH이면 업무 실행에 넘기기 전 수신 자료 변환을 거부한 기록이다.

현재 수신 거부 생성 경로의 「사유 코드」는 INBOUND_MAPPING_REJECTED다. JSON 해석·필수값·메시지 계약 검사 실패를 이 코드로 기록하고, 상세 내용은 「사유」에서 읽는다.

「설비」는 항상 UNKNOWN_EQP이므로 실제 설비 ID로 이 기록을 찾을 수 없다. 「ID」·발생 시각·사유와 「토픽」·「파티션」·「오프셋」을 운영 담당자에게 전달해 원본 확인을 요청한다.

UNKNOWN_EQP가 조회한 설비 목록에 없으면 선택한 라인과 관계없이 표시한다.

## Business UI 요청의 실패 단계 구분하기
```rag-meta
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; report L60~98
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; normalizeReasonCode·normalizeEqpId L136~161
원천: nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/runtime/KafkaTaskExecutionPipeline.java @ ae6791e99e91 ; 라우팅 실패 L174~198
원천: nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/runtime/KafkaTaskExecutionPipeline.java @ ae6791e99e91 ; 처리 실패 L354~384
원천: nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/runtime/KafkaTaskExecutionPipeline.java @ ae6791e99e91 ; 응답 발행 실패 L437~455
원천: nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/constants/KafkaTaskPipelineStage.java @ ae6791e99e91 ; 단계 정의 L9~24
```

「출처」가 BUSINESS_UI_PIPELINE이면 UI 요청 처리 중 생긴 기록이다. 「설비」는 요청의 설비 ID이며 값이 없거나 공백이면 UNKNOWN_EQP다.

| 단계 | 사유 코드 | 실패 내용 |
|---|---|---|
| ROUTING | ROUTING_FAILED | 이벤트 종류가 없거나 처리기를 찾지 못했다. |
| PROCESS | PROCESS_FAILED | 처리기 실행 중 최종 예외·오류가 발생했다. |
| PUBLISH | PUBLISH_FAILED | 처리 결과의 응답 발행에 최종 실패했다. |

단계가 전달되지 않으면 UNKNOWN, 사유 코드가 없거나 공백이면 UNKNOWN_REASON으로 기록한다.

## Business 업무 실행의 최종 실패 분류 읽기
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; publishRuntimeDlq L767~803
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; resolveReasonMessage L836~840
원천: nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/types/TaskFailureCategory.java @ ae6791e99e91 ; 실패 분류 L8~40
원천: nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/runtime/TaskHandlingPolicyEvaluator.java @ ae6791e99e91 ; decide·resolveFinalCategory L60~126
```

「출처」가 BUSINESS_RUNTIME이고 「단계」가 PROCESS면 업무 실행 중의 실패 기록이다. 「설비」는 처리하던 메시지의 설비 ID다. 「사유 코드」는 최종 실패 분류다.

| 사유 코드 | 실패 분류의 뜻 |
|---|---|
| VALIDATION | 입력·계약 검증 실패다. |
| FILTER_EVAL | 워크플로우 필터 평가 실패다. |
| ACTION_EXEC | 액션 실행 실패다. |
| TIMEOUT | 처리 제한시간 초과다. |
| UNKNOWN | 다른 분류에 잡히지 않은 예외다. |

「사유」는 예외 메시지이며, 없거나 공백이면 Runtime task moved to DLQ로 표시한다.

## TTL로 기록의 유효기간 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L36~36
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L41~41
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L30~30
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; publish L70~97
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L65~83
원천: nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/dlq/RedisBusinessDlqPublisher.java @ 7db24f7466bd ; publish L61~89
```

「Gateway DLQ」·「Business DLQ」·「격리 설비」의 「TTL」은 기록을 저장할 때 설정한 유효기간을 초로 표시한다.

표시된 「TTL」은 시간이 지나도 줄어들지 않는다. 수치가 있는 기록은 저장 당시의 유효기간으로 만료되며, 만료 기한 설정이 없는 기록은 「-」로 표시한다.

만료 뒤 목록은 「새로고침」으로 확인한다. 실패 기록이나 격리 표시의 만료와 실제 처리의 복구 완료는 구분한다.

## 입력·설비 식별 사유 코드 확인하기
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; INVALID_INPUT·UNKNOWN_EQUIPMENT L23~31
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; validateEnvelope L114~170
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; resolveEquipmentOrPublishDlq L243~271
```

Gateway의 「사유 코드」를 아래와 대조하고, 같은 행의 「사유」로 누락 항목이나 설비 조회 결과를 확인한다.

| 사유 코드 | 확인한 발생 조건 |
|---|---|
| INVALID_INPUT | 필수 메시지 자료가 없거나 입력값이 올바르지 않다. |
| UNKNOWN_EQUIPMENT | 실행 중인 Gateway의 설비 목록에서 대상 설비 정보를 찾지 못했다. |

## 수신·송신 대기열 포화 구분하기
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; 대기열 사유 L33~37
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; OUTBOUND_QUEUE_OVERFLOW L70~74
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; handleQueueOverflow·safeQuarantine L398~469
```

Gateway의 「사유 코드」에서 수신과 송신 대기열을 구분한다.

| 사유 코드 | 확인할 대상 |
|---|---|
| INBOUND_QUEUE_OVERFLOW | 설비별 수신 대기열이 가득 차 입력을 넣지 못한 경우다. Gateway가 DLQ 기록과 격리 기록을 요청한다. |
| OUTBOUND_QUEUE_OVERFLOW | 설비별 송신 대기열이 가득 찬 경우의 격리 사유다. |

대기열 포화 항목을 확인할 때는 「격리 설비」카드도 열어 같은 설비의 기록이 있는지 확인한다.

## 프레임·파싱·PLC 매핑 사유 읽기
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; 프레임·파싱 정의 L44~52
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; MAPPING_NOT_FOUND L64~68
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; drain·processFrame L125~158
원천: nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java @ d3636ab86763 ; handleFailure L286~333
```

Gateway의 사유 코드와 같은 행의 「사유」를 함께 읽는다.

| 사유 코드 | 코드와 사용처에서 확인한 의미 |
|---|---|
| FRAMING_FAILED | 프레임 처리 실패다. PLC에서는 프레임 해석 오류를 이 코드로 기록한다. |
| PARSING_FAILED | 파싱·변환 실패 분류다. 공통 수신 처리 경로는 처리 중 예외도 이 코드로 기록한다. |
| MAPPING_NOT_FOUND | PLC 응답을 해석할 그룹 매핑이 없다. 확인한 PLC 경로는 DLQ만 기록하고 이 사유로 격리하지 않는다. |

## 라우팅·송신 실패 사유 확인하기
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; ROUTING_FAILED·PUBLISH_FAILED L54~62
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; buildOutboundFrameOrPublishDlq L350~388
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; 송신 실패 L287~298
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskDlqPublisher.java @ d3636ab86763 ; toDlqReasonCode L227~234
```

Gateway의 다음 사유 코드는 「단계」·「사유」와 함께 확인한다.

| 사유 코드 | 확인한 사용 조건 |
|---|---|
| ROUTING_FAILED | 처리 실패 분류다. 송신 프레임 생성에서 지원하지 않는 처리를 요청한 경우에도 기록한다. |
| PUBLISH_FAILED | 발행 실패 분류다. 설비 송신 경로의 예외도 이 코드로 기록한다. |

## DLQ 항목 정리와 재처리를 요청하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~107
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L76~112
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 API L30~59
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteGatewayDlq L100~114
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteBusinessDlq L148~162
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; deleteDlq L137~150
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd ; deleteDlq L128~141
```

확인한 판본의 「Gateway DLQ」·「Business DLQ」화면은 목록 조회용이며 삭제·재처리 단추나 행을 여는 상세 화면은 제공하지 않는다. 상세 값은 목록의 각 열에서 읽는다.

서버의 삭제 처리는 저장된 실패 기록만 제거하며 원래 메시지를 재전송하지 않는다.

정리나 재처리가 필요하면 운영 담당자에게 범주·「ID」·설비 ID·발생 시각·사유를 전달하고, 기록 정리와 원래 처리의 복구를 각각 요청한다. 정리 뒤 「새로고침」으로 대상 행의 유무를 확인한다.

## 격리 설비의 사유와 표시 만료 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 열·목록 렌더 L14~107
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 격리 열 문구 L1071~1079
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchQuarantine L48~52
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listGatewayQuarantine L177~184
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L62~85
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 8.4.1 절차 L2181~2192
```

1. 「격리 설비」카드를 누른다. 「설비」·「사유 코드」·「사유」·「격리 시각」·「TTL」을 확인한다.
2. 격리 해제가 필요하면 설비 ID·격리 시각·코드·사유·TTL 표시값을 운영 담당자에게 전달한다. 화면에는 격리 해제 단추가 없다.
3. 유효기간이 있는 격리 기록은 만료 뒤 「새로고침」으로 표시 여부를 확인한다. 「TTL」이 「-」이면 자동 만료 기한이 없는 기록이다.

격리 기록이 사라진 뒤 설비를 다시 사용할 수 있는지는 운영 담당자에게 복구 결과로 확인한다.

## Repair Task에서 복구할 작업 찾기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 열·정렬·목록 렌더 L14~103
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Repair Task 열 문구 L1080~1084
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/EqpRepairTaskResponse.java @ 985d17c604dd ; 응답 항목 L15~30
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listRepairTasks L200~209
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 실패 기록 L132~199
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd ; persistPollGroupMutation L380~401
```

「Repair Task」는 설비 명령을 되돌리는 처리에 실패했거나, PLC 폴링 변경을 Gateway에 반영한 뒤 저장에 실패한 작업 등을 기록한다.

1. 「Repair Task」카드를 누른다. 「Task ID」·「설비」·「커맨드」·「액션」·「에러 코드」·「에러 메시지」·「생성 시각」을 읽는다.
2. 「커맨드」로 실패한 작업을, 「액션」으로 필요한 복구 작업을 확인한다. 「에러 코드」·「에러 메시지」로 기록된 실패 내용을 확인한다.
3. 복구를 요청할 때 위 일곱 항목의 표시값을 운영 담당자에게 전달한다.

## Repair Task의 정리를 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L67~103
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchRepairTasks L54~58
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpRepairTaskService.java @ 985d17c604dd ; record·buildTaskId L29~82
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/repair/RedisEqpRepairTaskStore.java @ 985d17c604dd ; save·findAll L54~77
```

「Repair Task」목록에는 TTL 열이나 복구 실행·완료·삭제 단추가 없다. 복구와 기록 정리는 운영 담당자에게 요청한다.

같은 설비·커맨드·복구 액션의 실패가 반복되면 같은 Task ID에 다시 기록하므로, 한 행이 여러 차례의 실패를 나타낼 수 있다.

담당자의 처리 후 「새로고침」으로 남은 행을 확인한다. 행의 유무와 함께 담당자가 확인한 실제 복구 결과도 확인한다.

## 범주별 목록의 기본 실패 문구 구별하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·fetchDlqList L14~58
원천: nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류 표시 L127~132
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 범주별 오류 문구 L1062~1065
```

목록 응답을 해석하지 못하거나 실패 응답에 오류 설명이 없을 때는 범주별 기본 실패 문구를 표시한다.

| 범주 | 기본 실패 문구 |
|---|---|
| Gateway DLQ | Gateway DLQ 목록을 조회하지 못했습니다. |
| Business DLQ | Business DLQ 목록을 조회하지 못했습니다. |
| 격리 설비 | 격리 설비 목록을 조회하지 못했습니다. |
| Repair Task | Repair Task 목록을 조회하지 못했습니다. |

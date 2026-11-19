# 메트릭·대시보드·알람 작성 기록

## 대상

- objectKey: `00.rag/admin/metric-dashboard-and-alert-reference.md`
- 버킷·구획: nori-tc·admin. 필요한 권한 전부는 AI_DOC_READ·AI_ADMIN_DOC_READ이다.
- 유형: 참조
- 검색 원문 SHA-256: `efde3b4156bf57ad0397079ab8bc05387a1a859f4cff844aa4fc188d505da552`
- 검색 원문 줄 수: 640
- 검색 원문 절 수: 47
- 확인일: 2026-11-19 (한국 시간)
- 범위: 메트릭 등록명·단위·증가/갱신 조건, 제공 대시보드 조회식, 알람 정책안. 제품 변경·서버 접속·업로드·커밋은 수행하지 않았다.
- 중단 후 재개 때 검색 원문은 존재하고 작성 기록은 없었다. 원문 전체와 코드 근거를 다시 읽고 인용 범위를 보완했다.

## 원천 판본

경로 한정 status와 rev-parse를 재실행했다. 모든 인용 파일은 변경 행이 없으며 기존 HEAD와 같았다.

| 저장소 | HEAD 12자리 | 인용 파일 수 | 작업 트리 결과 |
|---|---|---:|---|
| nori-common | ae6791e99e91 | 1 | 경로 한정 status exit 0·변경 행 없음 |
| nori-tc-common | 6fe71f6b2da6 | 1 | 경로 한정 status exit 0·변경 행 없음 |
| nori-tc-exec | f1d5e0a8e727 | 7 | 경로 한정 status exit 0·변경 행 없음 |
| nori-tc-gateway | d3636ab86763 | 22 | 경로 한정 status exit 0·변경 행 없음 |
| nori-tc-business | 7db24f7466bd | 8 | 경로 한정 status exit 0·변경 행 없음 |
| nori-tc-ui-backend | 985d17c604dd | 11 | 경로 한정 status exit 0·변경 행 없음 |

사용한 명령 형식은 `git -c safe.directory=C:/workspace_nori/nori-tc/<repo> -C C:/workspace_nori/nori-tc/<repo> status --short -- <인용 파일들>`이다. 전역 config는 수정하지 않았다. 사용자 ignore 경로 접근 경고가 있었으나 대상 경로 변경 행은 없었다. 수정 파일용 sha256 판본 대체는 필요하지 않았다.

| 원천 파일 | HEAD | 재대조 시 파일 SHA-256 |
|---|---|---|
| nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java | ae6791e99e91 | daa2f451e7c951fd6118f57ea580b72cc44f15b9580951f06003ea50110b9c1f |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java | 7db24f7466bd | f44033a66bab0662157223179ee1c33fd190286652babed89f144b393e155da4 |
| nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMessageProcessingMetrics.java | 7db24f7466bd | 44a9de35b10d795d0221b687c049e3afc8853d62e5083ee3b3a9f22a096ae2b8 |
| nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java | 7db24f7466bd | 31d19de95f356df2f0ee8f8ea414c690f2993d187c00143ba8f500f8b4495921 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessBarrierWaitRegistry.java | 7db24f7466bd | 6d83c1724478bd605d6639a94fa215a2fe7925eaad1085e1ad172619429681ef |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java | 7db24f7466bd | 83169578dbf34528f473273e3673628f38996dc3dba6abc47e8956ead7e5e63c |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java | 7db24f7466bd | e67ac629561ab99470530612f696903faab88ac9123b2f8d3ec478def301a766 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationStatus.java | 7db24f7466bd | 016014aeef2965ed5291cf4288a6f5a6a9ef196acc272dc4f4c5334ff910bc48 |
| nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessDelayedPortStatusWorker.java | 7db24f7466bd | 531009c0f01e9a532c7643245a698b517adeab5ec7af44a45aedc25827b845ad |
| nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java | 6fe71f6b2da6 | b0e4d4411a088a56d55aba98cc5f57a33813e406ab4f6750ad3ba59c45dff83e |
| nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml | f1d5e0a8e727 | 2da37ee020ca7ee7bbc7bf7799f52361a272434bea13f58d9b2cc809ed9f0deb |
| nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md | f1d5e0a8e727 | a74b1c4c649ca8cdf1d48994e3793a36bdda3f789b5c488088721eeb876d9755 |
| nori-tc-exec/docs/observability/02-sli-slo-definition.md | f1d5e0a8e727 | 2374c24aa44527d6022b4d619bd29d867d28506bf5ef15a81bcb142c206a7bcd |
| nori-tc-exec/docs/observability/03-alert-policy.md | f1d5e0a8e727 | d924565a96e2835a43adcddc449714853440ddc64b1d9d872d5fb0c6b695e30c |
| nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json | f1d5e0a8e727 | 1af4610f8246a4fa20d605f3f5e714f1c00b09868feb1646ed65028b92f501cf |
| nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json | f1d5e0a8e727 | 5807a89b0b98ec70779f7188f04148a61645f81be0cc4e575bd2a6539a5d6a85 |
| nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json | f1d5e0a8e727 | dad3b939ffaf8d8d7f84c19e6bd77b2719e52933618860fc4fcb577ddd22c5fd |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java | d3636ab86763 | c0ec8606edb18dd28cbd45caae176fb96a894d1f0700e475c192c743d48865bd |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java | d3636ab86763 | f872f9c2e07a05e7b0e754ad0c0ed3c8d76aedb359ec6d8f32bcc2d7a8694429 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java | d3636ab86763 | 6150eb9efa7e614e3ffef1a30709134ecea506561a94e7f18ec8d54c5e91be79 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java | d3636ab86763 | 44ed2c9cceda0b26fe812618732cb262e507384b5ee4f2a5c5574b281f4d3da1 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiDeferredLifecycleReplyService.java | d3636ab86763 | 188447cbdb7eafa7aaaa8014bf47e26688ba2d3896e7410f7484fe80794efb09 |
| nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java | d3636ab86763 | 3b9cdffab4842607deb52148ac1bfd9fc7f480c3b4f955173cb556d52c45b53d |
| nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java | d3636ab86763 | 5ae9f3869d0a4c05832c117748cac0a751878dafd45b2cc759f2603805bcfc63 |
| nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/RedisGatewayCommandResultLedger.java | d3636ab86763 | de3e2892e285e38a682d40b1e120774a2cbae79b741a923e9f06123bfb5d70de |
| nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java | d3636ab86763 | edf76f6dbd32ac667862be7540edbe1c7a0d27ad40daa9ed5f317e14d8fd8061 |
| nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java | d3636ab86763 | e229f857bae3b03c8d37f0df8968e2c345ab104e55d0d9e602ebf190b5e635d9 |
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java | d3636ab86763 | 52ec7baff14f356f1b6bb98348d9bf0fde21c972cd2167a749e55e1466869f53 |
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java | d3636ab86763 | 7126decff5f82d9b38c9b16fdb1f84c72c1204e5246b599fa82037ca36f03d99 |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java | d3636ab86763 | 65b75cc45583f7fb0bd68506cdba6266bb4621e6d1b43df7fc42cb9bb79fc5bd |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java | d3636ab86763 | 16eb4459aaf9c731f6ef07cdbf5244ff0a21b66f0878612733e8ac129722d94e |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java | d3636ab86763 | 177cb9a97501104f48ed8bc8e05e4fefd614a9d76d260e79ae926d6c1a45bcf5 |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayFenceQuiescer.java | d3636ab86763 | 9cfa1b00438d9683852287a849d5297a0a3ca9d68dc2f3d9ee0178edcc42e6de |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayRouteMoveCoordinator.java | d3636ab86763 | e0f7676d61ac74e333048989b18407e5557badcaf580def558d1e14f66e7d3c8 |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayTransportOwnershipGuard.java | d3636ab86763 | aa2fe6db2390b22ac8c7f590fe7af01b6ea4b8a4e676216a7ce78e70c9a3f27c |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java | d3636ab86763 | ff6b6205f1f45db5746c002d762d8e58fb0829275da573d57706e702ebf432f1 |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java | d3636ab86763 | ddb6dec1c39717dbfe214accb076cfc38bedd500c47afdd989ecc25c9e1c9a28 |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java | d3636ab86763 | a8d1c55ee1ddb591286d03d9a9590fc83896337262559e302116993fb11bdfb1 |
| nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java | d3636ab86763 | 73665fb13c1aefe07c1298c46319f7e5940c6bdc5d13b9d64c6d756aaab9740e |
| nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java | 985d17c604dd | a491a5af143a4733e3c755681bbb7fe09ce60c11d04800b56b28554b5173e4a6 |
| nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java | 985d17c604dd | 9e5718126d6b98ff808ac82d7694fa90db62c745a089a04e9f30d76d1052f828 |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java | 985d17c604dd | 2d807c9095cc353051a31a65ed7dc85ad96a3f244ef4ec4aaa9de7aca9433094 |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiTokenRecheckWindowCache.java | 985d17c604dd | 1b5cd45a8f9666017249fceaae7447d89287c36740701c0a70d99f11c83dacd6 |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java | 985d17c604dd | 9a2d405127aa824429dfbf32573193dad5becab91833ebdeb96eff742a525d86 |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java | 985d17c604dd | 999998b6cac9d0ab1beb450398830138eebaf890f7669ea9a80500d6f99184de |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java | 985d17c604dd | 7bd7975f71a98809369c2c5fed156c508130db904287d0e9a3f07c6c8f6ac3ae |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java | 985d17c604dd | ea4c480ff2e1585d32ba37026180e475060282bc51cee229fae45b7eac9ca39b |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java | 985d17c604dd | 6b2f70e168bc8e27ec166e425573f77b7ae5aa72b28d5f4191a8af971305af12 |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LogoutUseCase.java | 985d17c604dd | e4ff5e96e655e0d6178eff6507d1fb101c203058b2deafeaaafa036dd87d57be |
| nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java | 985d17c604dd | e2a7884bb37a975bb9ffe3faef00facfb6e00899a12d00e87f92c5c894195450 |

시험 코드의 기대값도 읽었다. GatewayMetricsTest는 Netty 쓰기 실패 2회·바인딩 거절 1회·처리 Timer 1건, EquipmentConnectionMetricsTest는 설비별 1→0과 바인딩 전 관측 보존을 검사한다. 중단 전 BusinessCoordinationMetricsBinderTest의 state·대기/한도·revision·phase/proof·지연 작업 기대값도 읽었다. 제품 시험은 실행하지 않았으며 시험 PASS를 주장하지 않는다.

## 절별 사실 대조

문장·표 자료 행·PromQL 블록을 최종 검색 원문 줄과 연결했다. 원천 위치는 해당 사실의 등록부·사용처·조회식이 함께 제공하는 근거다. 문단에 여러 문장이 있으면 같은 원문 줄에 각각 별도 행을 둔다. 검색 원문은 사람이 작성했으며 이 표의 문장·줄 전사는 최종 파일에서 결정적으로 추출했다.

### 1. 등록명과 대시보드 조회명을 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L17 | 이 문서의 등록명은 Micrometer에 넘기는 문자열이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 메트릭 상수 L13~103 ; nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java @ 6fe71f6b2da6 ; 공통 이름 위임 L29~30 ; nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java @ ae6791e99e91 ; 이름 결합·정규화 L30~43 L105~118 ; nori-tc-exec/docs/observability/02-sli-slo-definition.md @ f1d5e0a8e727 ; 측정 PromQL (1차안) | 상수·공통 이름 결합·정규화와 SLI 조회명을 대조했다. 밑줄 세그먼트는 하이픈으로 정규화하며 milliseconds 식은 1000으로 나눈다. |
| L17 | PromQL 식에는 제공 대시보드의 조회명을 쓴다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 메트릭 상수 L13~103 ; nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java @ 6fe71f6b2da6 ; 공통 이름 위임 L29~30 ; nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java @ ae6791e99e91 ; 이름 결합·정규화 L30~43 L105~118 ; nori-tc-exec/docs/observability/02-sli-slo-definition.md @ f1d5e0a8e727 ; 측정 PromQL (1차안) | 상수·공통 이름 결합·정규화와 SLI 조회명을 대조했다. 밑줄 세그먼트는 하이픈으로 정규화하며 milliseconds 식은 1000으로 나눈다. |
| L17 | 예를 들어 `tc.message.processing`의 처리 시간 분포는 `tc_message_processing_milliseconds_bucket`으로 조회한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 메트릭 상수 L13~103 ; nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java @ 6fe71f6b2da6 ; 공통 이름 위임 L29~30 ; nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java @ ae6791e99e91 ; 이름 결합·정규화 L30~43 L105~118 ; nori-tc-exec/docs/observability/02-sli-slo-definition.md @ f1d5e0a8e727 ; 측정 PromQL (1차안) | 상수·공통 이름 결합·정규화와 SLI 조회명을 대조했다. 밑줄 세그먼트는 하이픈으로 정규화하며 milliseconds 식은 1000으로 나눈다. |
| L19 | Timer의 milliseconds 조회값을 초로 표시할 때는 1000으로 나눈다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 메트릭 상수 L13~103 ; nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java @ 6fe71f6b2da6 ; 공통 이름 위임 L29~30 ; nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java @ ae6791e99e91 ; 이름 결합·정규화 L30~43 L105~118 ; nori-tc-exec/docs/observability/02-sli-slo-definition.md @ f1d5e0a8e727 ; 측정 PromQL (1차안) | 상수·공통 이름 결합·정규화와 SLI 조회명을 대조했다. 밑줄 세그먼트는 하이픈으로 정규화하며 milliseconds 식은 1000으로 나눈다. |
| L19 | 카운터는 기록 호출의 누적 횟수이고, 게이지는 조회 시 제공하는 상태·수량이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 메트릭 상수 L13~103 ; nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java @ 6fe71f6b2da6 ; 공통 이름 위임 L29~30 ; nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java @ ae6791e99e91 ; 이름 결합·정규화 L30~43 L105~118 ; nori-tc-exec/docs/observability/02-sli-slo-definition.md @ f1d5e0a8e727 ; 측정 PromQL (1차안) | 상수·공통 이름 결합·정규화와 SLI 조회명을 대조했다. 밑줄 세그먼트는 하이픈으로 정규화하며 milliseconds 식은 1000으로 나눈다. |
| L19 | 표의 단위가 건인 누적값과 현재 수량을 구별한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 메트릭 상수 L13~103 ; nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java @ 6fe71f6b2da6 ; 공통 이름 위임 L29~30 ; nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java @ ae6791e99e91 ; 이름 결합·정규화 L30~43 L105~118 ; nori-tc-exec/docs/observability/02-sli-slo-definition.md @ f1d5e0a8e727 ; 측정 PromQL (1차안) | 상수·공통 이름 결합·정규화와 SLI 조회명을 대조했다. 밑줄 세그먼트는 하이픈으로 정규화하며 milliseconds 식은 1000으로 나눈다. |

### 2. Kafka 메시지 처리 시간은 어디부터 어디까지인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L30 | `tc.message.processing`은 Gateway의 레코드 처리 호출, Business의 수신 처리 호출, Backend의 명령 응답 처리 호출을 감싸는 Timer다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; handleRecord L76~85 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; Timer 등록·기록 L169~190 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMessageProcessingMetrics.java @ 7db24f7466bd ; Timer L27~53 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; onMessage L101~110 ; nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L94~125 | Gateway의 doHandleRecord 래핑, Business·Backend의 process와 finally sample.stop을 대조했다. 세 Timer의 histogram 등록 및 등록 전 Gateway 분기를 확인했다. |
| L30 | Kafka에 머문 전체 시간이나 설비 작업 완료까지의 시간을 뜻하지 않는다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; handleRecord L76~85 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; Timer 등록·기록 L169~190 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMessageProcessingMetrics.java @ 7db24f7466bd ; Timer L27~53 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; onMessage L101~110 ; nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L94~125 | Gateway의 doHandleRecord 래핑, Business·Backend의 process와 finally sample.stop을 대조했다. 세 Timer의 histogram 등록 및 등록 전 Gateway 분기를 확인했다. |
| L32 | 세 구현 모두 백분위 계산용 히스토그램을 등록한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; handleRecord L76~85 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; Timer 등록·기록 L169~190 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMessageProcessingMetrics.java @ 7db24f7466bd ; Timer L27~53 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; onMessage L101~110 ; nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L94~125 | Gateway의 doHandleRecord 래핑, Business·Backend의 process와 finally sample.stop을 대조했다. 세 Timer의 histogram 등록 및 등록 전 Gateway 분기를 확인했다. |
| L32 | Business와 Backend는 처리 호출이 끝날 때 finally에서 시간을 기록한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; handleRecord L76~85 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; Timer 등록·기록 L169~190 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMessageProcessingMetrics.java @ 7db24f7466bd ; Timer L27~53 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; onMessage L101~110 ; nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L94~125 | Gateway의 doHandleRecord 래핑, Business·Backend의 process와 finally sample.stop을 대조했다. 세 Timer의 histogram 등록 및 등록 전 Gateway 분기를 확인했다. |
| L32 | Gateway는 Timer 등록 전이면 시간을 기록하지 않고 처리만 수행한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; handleRecord L76~85 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; Timer 등록·기록 L169~190 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMessageProcessingMetrics.java @ 7db24f7466bd ; Timer L27~53 ; nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; onMessage L101~110 ; nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L94~125 | Gateway의 doHandleRecord 래핑, Business·Backend의 process와 finally sample.stop을 대조했다. 세 Timer의 histogram 등록 및 등록 전 Gateway 분기를 확인했다. |

### 3. 메시지 처리 지연 p95를 초로 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L41 | 제공 대시보드는 선택한 서비스의 최근 5분 처리 시간 분포를 합쳐 p95를 초로 표시한다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L64~75 | 세 JSON의 5m·0.95·le 집계·1000 나눗셈·2.0 임계를 대조했다. application 상수를 job 필터에 사용한다. |
| L41 | `$application`은 대시보드가 정한 서비스 값이며 조회 라벨은 `job`이다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L64~75 | 세 JSON의 5m·0.95·le 집계·1000 나눗셈·2.0 임계를 대조했다. application 상수를 job 필터에 사용한다. |
| L43~45 | histogram_quantile(0.95, sum(rate(tc_message_processing_milliseconds_bucket{job="$application"}[5m])) by (le)) / 1000 | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L64~75 | 세 JSON의 5m·0.95·le 집계·1000 나눗셈·2.0 임계를 대조했다. application 상수를 job 필터에 사용한다. |
| L47 | 표시 임계값은 2.0초다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L64~75 | 세 JSON의 5m·0.95·le 집계·1000 나눗셈·2.0 임계를 대조했다. application 상수를 job 필터에 사용한다. |
| L47 | 임계값 색상은 대시보드 정의이며 알람 발송 여부와는 별개다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L64~75 | 세 JSON의 5m·0.95·le 집계·1000 나눗셈·2.0 임계를 대조했다. application 상수를 job 필터에 사용한다. |

### 4. 설비별 연결 게이지의 1과 0

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L55 | `tc.gateway.equipment.connection.up`은 `eqp_id` 태그로 설비를 구별하는 무단위 게이지다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; markUp·markDown·등록 L42~82 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 ; 해제·바인딩 성공 L188~197 L329~330 | markUp=1·markDown=0·eqp_id·computeIfAbsent 및 EqpBindingService의 성공·해제 호출을 대조했다. |
| L55 | 바인딩 성공에서 1, 연결 해제에서 0을 기록한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; markUp·markDown·등록 L42~82 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 ; 해제·바인딩 성공 L188~197 L329~330 | markUp=1·markDown=0·eqp_id·computeIfAbsent 및 EqpBindingService의 성공·해제 호출을 대조했다. |
| L55 | 등록된 설비 전체를 미리 0으로 만드는 구현이 아니라 상태 기록이 들어온 설비에 계열을 만든다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; markUp·markDown·등록 L42~82 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 ; 해제·바인딩 성공 L188~197 L329~330 | markUp=1·markDown=0·eqp_id·computeIfAbsent 및 EqpBindingService의 성공·해제 호출을 대조했다. |
| L57 | 연결 기록이 없는 계열의 부재를 연결 실패율 0으로 해석하지 않는다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; markUp·markDown·등록 L42~82 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 ; 해제·바인딩 성공 L188~197 L329~330 | markUp=1·markDown=0·eqp_id·computeIfAbsent 및 EqpBindingService의 성공·해제 호출을 대조했다. |
| L57 | 관측된 연결 상태의 의미이며 업무 처리 성공을 뜻하지 않는다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; markUp·markDown·등록 L42~82 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 ; 해제·바인딩 성공 L188~197 L329~330 | markUp=1·markDown=0·eqp_id·computeIfAbsent 및 EqpBindingService의 성공·해제 호출을 대조했다. |

### 5. 설비 연결 가용률은 설비별 최근 상태 평균이다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L65 | Gateway 대시보드는 다음 식으로 각 연결 상태 계열의 최근 5분 평균을 표시한다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 연결 가용률 L47~59 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; 설비 태그 L79~82 | avg_over_time 식·5m·percentunit·0.995를 대조했다. 설비 간 평균을 내는 집계가 없고 설비 태그가 남는다. |
| L65 | 식에 설비 전체 평균을 내는 집계는 없다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 연결 가용률 L47~59 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; 설비 태그 L79~82 | avg_over_time 식·5m·percentunit·0.995를 대조했다. 설비 간 평균을 내는 집계가 없고 설비 태그가 남는다. |
| L67~69 | avg_over_time(tc_gateway_equipment_connection_up{job="$application"}[5m]) | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 연결 가용률 L47~59 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; 설비 태그 L79~82 | avg_over_time 식·5m·percentunit·0.995를 대조했다. 설비 간 평균을 내는 집계가 없고 설비 태그가 남는다. |
| L71 | 표시는 비율이며 0.995가 99.5%에 해당하는 표시 임계값이다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 연결 가용률 L47~59 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; 설비 태그 L79~82 | avg_over_time 식·5m·percentunit·0.995를 대조했다. 설비 간 평균을 내는 집계가 없고 설비 태그가 남는다. |
| L71 | `eqp_id`별 연결 기록을 구별해 읽는다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 연결 가용률 L47~59 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; 설비 태그 L79~82 | avg_over_time 식·5m·percentunit·0.995를 대조했다. 설비 간 평균을 내는 집계가 없고 설비 태그가 남는다. |

### 6. 화면 HTTP 응답 시간 p95

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L79 | Backend 설정은 `http.server.requests`의 히스토그램을 켠다. | nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; HTTP 히스토그램 L82~87 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 응답 시간 L32~43 | http.server.requests 히스토그램 활성화와 HTTP p95 식·1.0 임계·초 변환을 대조했다. 업무 전체 완료 시간을 재는 식이 아니다. |
| L79 | 제공 대시보드는 최근 5분 HTTP 요청 분포를 합쳐 p95를 초로 표시하며, 표시 임계값은 1.0초다. | nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; HTTP 히스토그램 L82~87 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 응답 시간 L32~43 | http.server.requests 히스토그램 활성화와 HTTP p95 식·1.0 임계·초 변환을 대조했다. 업무 전체 완료 시간을 재는 식이 아니다. |
| L81~83 | histogram_quantile(0.95, sum(rate(http_server_requests_milliseconds_bucket{job="$application"}[5m])) by (le)) / 1000 | nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; HTTP 히스토그램 L82~87 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 응답 시간 L32~43 | http.server.requests 히스토그램 활성화와 HTTP p95 식·1.0 임계·초 변환을 대조했다. 업무 전체 완료 시간을 재는 식이 아니다. |
| L85 | 화면에서 수행한 업무 전체의 완료 시간이 아니라 서버 HTTP 요청 지표를 사용한 값이다. | nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; HTTP 히스토그램 L82~87 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 응답 시간 L32~43 | http.server.requests 히스토그램 활성화와 HTTP p95 식·1.0 임계·초 변환을 대조했다. 업무 전체 완료 시간을 재는 식이 아니다. |

### 7. 서버 오류 비율의 분자와 분모

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L92 | Backend 대시보드의 5xx 비율은 최근 5분 요청 수 증가율에서 다음 분자를 분모로 나눈 값이다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59 | JSON의 5xx 비율 식을 분자·분모로 나눠 대조했다. status 정규식·5m·0.005와 percentunit을 확인했다. |
| L92 | 표시 임계값은 0.005이며 백분율로는 0.5%다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59 | JSON의 5xx 비율 식을 분자·분모로 나눠 대조했다. status 정규식·5m·0.005와 percentunit을 확인했다. |
| L94 | 분자는 HTTP 상태가 5로 시작하는 요청을 센다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59 | JSON의 5xx 비율 식을 분자·분모로 나눠 대조했다. status 정규식·5m·0.005와 percentunit을 확인했다. |
| L96~98 | sum(rate(http_server_requests_milliseconds_count{job="$application",status=~"5.."}[5m])) | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59 | JSON의 5xx 비율 식을 분자·분모로 나눠 대조했다. status 정규식·5m·0.005와 percentunit을 확인했다. |
| L100 | 분모는 같은 서비스의 전체 HTTP 요청을 센다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59 | JSON의 5xx 비율 식을 분자·분모로 나눠 대조했다. status 정규식·5m·0.005와 percentunit을 확인했다. |
| L102~104 | sum(rate(http_server_requests_milliseconds_count{job="$application"}[5m])) | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59 | JSON의 5xx 비율 식을 분자·분모로 나눠 대조했다. status 정규식·5m·0.005와 percentunit을 확인했다. |
| L106 | 4xx 응답은 이 분자에 포함하지 않는다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59 | JSON의 5xx 비율 식을 분자·분모로 나눠 대조했다. status 정규식·5m·0.005와 percentunit을 확인했다. |

### 8. 제공 Grafana 대시보드와 서비스 선택값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L115 | 대시보드 JSON은 `nori-tc-exec/docs/observability/grafana/`에 있다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~106 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~90 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~100 | 세 JSON의 Prometheus 입력·application 상수·job 필터와 실제 패널 목록을 대조했다. 현장 등록 여부는 주장하지 않았다. |
| L115 | 세 파일 모두 Prometheus 데이터 원천을 입력받으며, `application` 변수의 상수값을 `job` 필터에 사용한다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~106 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~90 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~100 | 세 JSON의 Prometheus 입력·application 상수·job 필터와 실제 패널 목록을 대조했다. 현장 등록 여부는 주장하지 않았다. |
| L119 | tc-gateway-dashboard.json \| tc-gateway \| 처리 지연·연결 가용률·Kafka 처리량·자원 | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~106 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~90 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~100 | 세 JSON의 Prometheus 입력·application 상수·job 필터와 실제 패널 목록을 대조했다. 현장 등록 여부는 주장하지 않았다. |
| L120 | tc-business-dashboard.json \| tc-business \| 처리 지연·Kafka 처리량·자원 | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~106 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~90 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~100 | 세 JSON의 Prometheus 입력·application 상수·job 필터와 실제 패널 목록을 대조했다. 현장 등록 여부는 주장하지 않았다. |
| L121 | tc-ui-backend-dashboard.json \| tc-ui-backend \| HTTP 응답 시간·5xx 비율·처리 지연·자원 | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~106 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~90 ; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~100 | 세 JSON의 Prometheus 입력·application 상수·job 필터와 실제 패널 목록을 대조했다. 현장 등록 여부는 주장하지 않았다. |

### 9. Kafka 처리량 패널의 전송과 소비

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L129 | Gateway와 Business의 Kafka 처리량 패널은 다음 계열의 최근 5분 증가율을 합산한다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L63~88 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L47~72 | 두 JSON의 생산자·소비자 계열, rate 5m·sum 및 send/s·records/s 범례를 대조했다. 해당 절은 제공 조회식의 의미만 설명한다. |
| L129 | 범례 단위는 초당 레코드다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L63~88 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L47~72 | 두 JSON의 생산자·소비자 계열, rate 5m·sum 및 send/s·records/s 범례를 대조했다. 해당 절은 제공 조회식의 의미만 설명한다. |
| L129 | 처리량 패널에는 업무 성공률 판정식이 없다. | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L63~88 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L47~72 | 두 JSON의 생산자·소비자 계열, rate 5m·sum 및 send/s·records/s 범례를 대조했다. 해당 절은 제공 조회식의 의미만 설명한다. |
| L133 | kafka_producer_record_send_total \| producer send/s \| 생산자 전송 증가율 | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L63~88 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L47~72 | 두 JSON의 생산자·소비자 계열, rate 5m·sum 및 send/s·records/s 범례를 대조했다. 해당 절은 제공 조회식의 의미만 설명한다. |
| L134 | kafka_consumer_fetch_manager_records_consumed_total \| consumer records/s \| 소비자 레코드 소비 증가율 | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L63~88 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L47~72 | 두 JSON의 생산자·소비자 계열, rate 5m·sum 및 send/s·records/s 범례를 대조했다. 해당 절은 제공 조회식의 의미만 설명한다. |

### 10. 메모리·CPU 패널에서 서로 다른 단위를 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L143 | 세 대시보드의 자원 패널은 `jvm_memory_used_bytes`에서 `area="heap"`인 값을 서비스별로 합산하고, `process_cpu_usage`를 함께 조회한다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L80~98 ; nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L87~104 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L71~88 | 세 JSON의 heap 선택자·합산·CPU 별도 식, 기본 bytes 및 CPU 단위 override 부재를 대조했다. |
| L145 | 힙 사용량 조회 단위는 bytes다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L80~98 ; nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L87~104 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L71~88 | 세 JSON의 heap 선택자·합산·CPU 별도 식, 기본 bytes 및 CPU 단위 override 부재를 대조했다. |
| L145 | CPU는 별도 계열인데 제공 JSON은 패널 기본 단위를 bytes로 지정하고 CPU용 단위 재정의를 두지 않는다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L80~98 ; nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L87~104 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L71~88 | 세 JSON의 heap 선택자·합산·CPU 별도 식, 기본 bytes 및 CPU 단위 override 부재를 대조했다. |
| L145 | CPU 선을 메모리 바이트 값으로 해석하지 않는다. | nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L80~98 ; nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L87~104 ; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L71~88 | 세 JSON의 heap 선택자·합산·CPU 별도 식, 기본 bytes 및 CPU 단위 override 부재를 대조했다. |

### 11. Gateway의 활성·바인딩 연결 수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L154 | 세 값은 Gateway 인스턴스가 관리하는 현재 연결 수 게이지이며 단위는 개다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 연결 이름 L58~66 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 연결 게이지 L149~157 L296~323 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 활성·바인딩·비활성화 L159~163 L286~297 L331~343 L464~468 L604~608 | Gauge 등록과 현재 수량 보관을 대조했다. 지정 커밋의 channelActive L162~163, 바인딩 성공 L296~297·L467~468·L607~608, channelInactive L335~341의 BOUND별 bound/unbound 감소와 active 감소를 직접 확인했다. |
| L158 | tc.gateway.connection.active \| 현재 활성 연결 수. 채널 활성화 때 증가하고 비활성화 때 감소한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 연결 이름 L58~66 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 연결 게이지 L149~157 L296~323 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 활성·바인딩·비활성화 L159~163 L286~297 L331~343 L464~468 L604~608 | Gauge 등록과 현재 수량 보관을 대조했다. 지정 커밋의 channelActive L162~163, 바인딩 성공 L296~297·L467~468·L607~608, channelInactive L335~341의 BOUND별 bound/unbound 감소와 active 감소를 직접 확인했다. |
| L159 | tc.gateway.connection.bound \| 현재 바인딩 연결 수. 바인딩 성공 때 증가하고 BOUND 상태로 비활성화될 때 감소한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 연결 이름 L58~66 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 연결 게이지 L149~157 L296~323 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 활성·바인딩·비활성화 L159~163 L286~297 L331~343 L464~468 L604~608 | Gauge 등록과 현재 수량 보관을 대조했다. 지정 커밋의 channelActive L162~163, 바인딩 성공 L296~297·L467~468·L607~608, channelInactive L335~341의 BOUND별 bound/unbound 감소와 active 감소를 직접 확인했다. |
| L160 | tc.gateway.connection.unbound \| 현재 미바인딩 연결 수. 활성화 때 증가한다. 바인딩 성공 때, 또는 BOUND가 아닌 상태로 비활성화될 때 감소한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 연결 이름 L58~66 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 연결 게이지 L149~157 L296~323 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 활성·바인딩·비활성화 L159~163 L286~297 L331~343 L464~468 L604~608 | Gauge 등록과 현재 수량 보관을 대조했다. 지정 커밋의 channelActive L162~163, 바인딩 성공 L296~297·L467~468·L607~608, channelInactive L335~341의 BOUND별 bound/unbound 감소와 active 감소를 직접 확인했다. |

### 12. Gateway가 바인딩을 거절하거나 기다리다 끝난 횟수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L169 | Gateway의 누적 카운터이며 단위는 건이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 바인딩 이름 L16~24 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 카운터 등록 L91~99 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 증가 조건 L658~680 L711~713 | FunctionCounter 등록과 중복·NOT_OWNED 결과, 만료 시 UNBOUND 검사 뒤 증가를 대조했다. 등록명의 하이픈도 실제 공통 코드로 확인했다. |
| L173 | tc.gateway.duplicate-eqp.reject.total \| 바인딩 결과가 중복 연결 거절일 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 바인딩 이름 L16~24 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 카운터 등록 L91~99 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 증가 조건 L658~680 L711~713 | FunctionCounter 등록과 중복·NOT_OWNED 결과, 만료 시 UNBOUND 검사 뒤 증가를 대조했다. 등록명의 하이픈도 실제 공통 코드로 확인했다. |
| L174 | tc.gateway.bind.not-owned.total \| 바인딩 결과가 소유권 없음일 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 바인딩 이름 L16~24 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 카운터 등록 L91~99 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 증가 조건 L658~680 L711~713 | FunctionCounter 등록과 중복·NOT_OWNED 결과, 만료 시 UNBOUND 검사 뒤 증가를 대조했다. 등록명의 하이픈도 실제 공통 코드로 확인했다. |
| L175 | tc.gateway.bind.timeout.total \| 바인딩 대기 만료 시 여전히 UNBOUND일 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 바인딩 이름 L16~24 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 카운터 등록 L91~99 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 증가 조건 L658~680 L711~713 | FunctionCounter 등록과 중복·NOT_OWNED 결과, 만료 시 UNBOUND 검사 뒤 증가를 대조했다. 등록명의 하이픈도 실제 공통 코드로 확인했다. |

### 13. Gateway 큐 깊이는 기록된 큐 크기의 합이다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L184 | 이름 끝의 total과 달리 두 값은 게이지다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 큐 이름 L67~72 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 게이지·기록·합산 L159~164 L326~357 L370~399 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 크기 기록·제거 L132~135 L183~185 L259~264 L290~295 | 설비별 마지막 AtomicInteger를 합산하는 Gauge다. offer 후 size 기록과 mailbox 제거 시 clearQueueDepth 호출을 대조했다. |
| L184 | 설비별로 마지막 기록한 큐 크기를 합산하며 단위는 건이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 큐 이름 L67~72 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 게이지·기록·합산 L159~164 L326~357 L370~399 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 크기 기록·제거 L132~135 L183~185 L259~264 L290~295 | 설비별 마지막 AtomicInteger를 합산하는 Gauge다. offer 후 size 기록과 mailbox 제거 시 clearQueueDepth 호출을 대조했다. |
| L188 | tc.gateway.queue.inbound.depth.total \| 설비별 수신 큐 깊이 기록 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 큐 이름 L67~72 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 게이지·기록·합산 L159~164 L326~357 L370~399 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 크기 기록·제거 L132~135 L183~185 L259~264 L290~295 | 설비별 마지막 AtomicInteger를 합산하는 Gauge다. offer 후 size 기록과 mailbox 제거 시 clearQueueDepth 호출을 대조했다. |
| L189 | tc.gateway.queue.outbound.depth.total \| 설비별 송신 큐 깊이 기록 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 큐 이름 L67~72 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 게이지·기록·합산 L159~164 L326~357 L370~399 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 크기 기록·제거 L132~135 L183~185 L259~264 L290~295 | 설비별 마지막 AtomicInteger를 합산하는 Gauge다. offer 후 size 기록과 mailbox 제거 시 clearQueueDepth 호출을 대조했다. |
| L191 | 설비 제거에서는 두 큐 깊이 기록을 지운다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 큐 이름 L67~72 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 게이지·기록·합산 L159~164 L326~357 L370~399 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 크기 기록·제거 L132~135 L183~185 L259~264 L290~295 | 설비별 마지막 AtomicInteger를 합산하는 Gauge다. offer 후 size 기록과 mailbox 제거 시 clearQueueDepth 호출을 대조했다. |
| L191 | 큐 객체를 매번 직접 조회하는 값과 구별한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 큐 이름 L67~72 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 게이지·기록·합산 L159~164 L326~357 L370~399 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 크기 기록·제거 L132~135 L183~185 L259~264 L290~295 | 설비별 마지막 AtomicInteger를 합산하는 Gauge다. offer 후 size 기록과 mailbox 제거 시 clearQueueDepth 호출을 대조했다. |

### 14. Gateway 큐가 넘친 횟수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L200 | Gateway의 큐 수용 실패를 누적하며 단위는 건이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 초과 이름 L25~30 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L100~105 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 수용 실패 L129~140 L180~190 | 수신·송신 offer의 false 분기에서 카운터를 증가시킨다. 큐 깊이 Gauge와 누적 실패 Counter를 구별했다. |
| L204 | tc.gateway.queue.inbound.overflow.total \| 수신 큐의 offer가 실패할 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 초과 이름 L25~30 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L100~105 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 수용 실패 L129~140 L180~190 | 수신·송신 offer의 false 분기에서 카운터를 증가시킨다. 큐 깊이 Gauge와 누적 실패 Counter를 구별했다. |
| L205 | tc.gateway.queue.outbound.overflow.total \| 송신 큐의 offer가 실패할 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 초과 이름 L25~30 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L100~105 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 수용 실패 L129~140 L180~190 | 수신·송신 offer의 false 분기에서 카운터를 증가시킨다. 큐 깊이 Gauge와 누적 실패 Counter를 구별했다. |

### 15. Gateway 소비 지연과 커밋 실패

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L216 | tc.gateway.kafka.consumer.lag.total \| 게이지·건 \| 토픽·파티션별 마지막 lag 샘플의 합 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; Kafka 이름 L31~33 L73~75 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록·합산 L106~108 L165~167 L362~388 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; 커밋 실패·샘플 L58~73 | onLagSample의 토픽·파티션 기록과 마지막 샘플 합산, onCommitFail의 카운터 증가를 대조했다. |
| L217 | tc.gateway.kafka.commit.fail.total \| 카운터·건 \| 커밋 실패 콜백 횟수 누적 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; Kafka 이름 L31~33 L73~75 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록·합산 L106~108 L165~167 L362~388 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; 커밋 실패·샘플 L58~73 | onLagSample의 토픽·파티션 기록과 마지막 샘플 합산, onCommitFail의 카운터 증가를 대조했다. |
| L219 | lag는 시간값이 아니다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; Kafka 이름 L31~33 L73~75 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록·합산 L106~108 L165~167 L362~388 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; 커밋 실패·샘플 L58~73 | onLagSample의 토픽·파티션 기록과 마지막 샘플 합산, onCommitFail의 카운터 증가를 대조했다. |
| L219 | 저장된 샘플을 합산하므로 처리 지연 Timer와 구별한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; Kafka 이름 L31~33 L73~75 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록·합산 L106~108 L165~167 L362~388 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; 커밋 실패·샘플 L58~73 | onLagSample의 토픽·파티션 기록과 마지막 샘플 합산, onCommitFail의 카운터 증가를 대조했다. |

### 16. 연결 없는 설비 명령과 이벤트 발행 결과

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L231 | tc.gateway.commands.drop.no-connection.total \| 연결을 찾지 못한 명령을 버리는 경로 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 명령·발행 이름 L13~15 L34~39 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L88~90 L109~114 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; 비연결 처리 L190~214 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; 발행 결과 L98~120 L152~169 | 활성 연결 없는 명령 분기, 이벤트 payload 변환·계약 검증·전송의 성공·실패 증가 위치를 대조했다. |
| L232 | tc.gateway.event.publish.success.total \| 설비 이벤트 Kafka 전송 성공 경로 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 명령·발행 이름 L13~15 L34~39 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L88~90 L109~114 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; 비연결 처리 L190~214 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; 발행 결과 L98~120 L152~169 | 활성 연결 없는 명령 분기, 이벤트 payload 변환·계약 검증·전송의 성공·실패 증가 위치를 대조했다. |
| L233 | tc.gateway.event.publish.fail.total \| 이벤트 변환·계약 검사·전송 실패 경로 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 명령·발행 이름 L13~15 L34~39 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L88~90 L109~114 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; 비연결 처리 L190~214 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; 발행 결과 L98~120 L152~169 | 활성 연결 없는 명령 분기, 이벤트 payload 변환·계약 검증·전송의 성공·실패 증가 위치를 대조했다. |

### 17. DLQ 발행과 파싱 실패 카운터를 해석하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L245 | tc.gateway.dlq.publish.total \| 건·누적 \| Redis 발행기와 명령 실패 처리부의 발행 후 기록 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L40~45 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L115~120 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; 증가 L96~101 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 발행 후 증가 L307~310 | Redis 발행 후 DLQ 증가 및 PARSING_FAILED의 decode 증가를 대조했다. 명령 실패 처리부도 port.publish 반환 후 DLQ를 증가시킨다. |
| L246 | tc.gateway.decode.fail.total \| 건·누적 \| Redis 발행기에서 사유가 PARSING_FAILED일 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L40~45 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L115~120 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; 증가 L96~101 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 발행 후 증가 L307~310 | Redis 발행 후 DLQ 증가 및 PARSING_FAILED의 decode 증가를 대조했다. 명령 실패 처리부도 port.publish 반환 후 DLQ를 증가시킨다. |
| L248 | 명령 실패 처리부가 Redis 발행기를 호출하면 DLQ 값은 두 위치에서 증가한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L40~45 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L115~120 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; 증가 L96~101 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 발행 후 증가 L307~310 | Redis 발행 후 DLQ 증가 및 PARSING_FAILED의 decode 증가를 대조했다. 명령 실패 처리부도 port.publish 반환 후 DLQ를 증가시킨다. |
| L248 | DLQ 카운터를 고유 메시지 수로 해석하지 않는다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L40~45 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L115~120 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; 증가 L96~101 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 발행 후 증가 L307~310 | Redis 발행 후 DLQ 증가 및 PARSING_FAILED의 decode 증가를 대조했다. 명령 실패 처리부도 port.publish 반환 후 DLQ를 증가시킨다. |

### 18. 후속 처리·네트워크 쓰기·입력 검증 실패

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L260 | tc.gateway.secondary.failure.total \| 원래 오류 뒤 복구·격리·DLQ 같은 후속 처리의 실패 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L49~57 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L124~132 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 후속 실패 L216~224 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 쓰기·검증 실패 L252~260 L539~550 | secondary 기록기, Netty write 리스너의 실패 검사, SOCKET 설비 ID 경계 검증 거절의 증가를 대조했다. |
| L261 | tc.gateway.netty.write.failed.total \| Netty 쓰기 완료 리스너에서 확인한 실패 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L49~57 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L124~132 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 후속 실패 L216~224 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 쓰기·검증 실패 L252~260 L539~550 | secondary 기록기, Netty write 리스너의 실패 검사, SOCKET 설비 ID 경계 검증 거절의 증가를 대조했다. |
| L262 | tc.gateway.input.validation.failed.total \| 수신 경계 입력 검증 실패 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L49~57 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L124~132 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 후속 실패 L216~224 ; nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 쓰기·검증 실패 L252~260 L539~550 | secondary 기록기, Netty write 리스너의 실패 검사, SOCKET 설비 ID 경계 검증 거절의 증가를 대조했다. |

### 19. HSMS 타이머 만료 횟수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L271 | `tc.gateway.hsms.timeout.total`은 HSMS 세션 타이머 검사 결과의 만료 항목마다 증가하는 카운터이며 단위는 건이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L46~48 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L121~123 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 만료 순회 L67~83 | tickTimers 결과 expiries의 각 항목마다 증가한다. 타이머 상세 값·뜻은 별도 참조 문서로 연결했다. |
| L271 | 한 번의 검사에 만료 항목이 여러 개면 각각 센다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L46~48 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L121~123 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 만료 순회 L67~83 | tickTimers 결과 expiries의 각 항목마다 증가한다. 타이머 상세 값·뜻은 별도 참조 문서로 연결했다. |
| L273 | 타이머 값과 의미는 `00.rag/user/secs-hsms-message-and-timer-reference.md`가 소유한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L46~48 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L121~123 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 만료 순회 L67~83 | tickTimers 결과 expiries의 각 항목마다 증가한다. 타이머 상세 값·뜻은 별도 참조 문서로 연결했다. |

### 20. PLC 응답 대기·상관관계·변경 억제

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L283 | Gateway PLC 누적 카운터이며 단위는 건이다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; PLC 이름 L78~86 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L133~141 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 만료 수 L79~85 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 매칭·변경 판정 L143~194 | 만료 pending 수만큼의 증가, pending 매칭 실패와 shouldPublish=false 분기를 대조했다. 발행 억제는 변화 판정 코드로 확인했다. |
| L287 | tc.gateway.plc.poll.timeout.total \| 만료한 응답 대기 항목마다 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; PLC 이름 L78~86 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L133~141 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 만료 수 L79~85 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 매칭·변경 판정 L143~194 | 만료 pending 수만큼의 증가, pending 매칭 실패와 shouldPublish=false 분기를 대조했다. 발행 억제는 변화 판정 코드로 확인했다. |
| L288 | tc.gateway.plc.correlation.miss.total \| 응답에 대응하는 대기 요청이 없을 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; PLC 이름 L78~86 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L133~141 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 만료 수 L79~85 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 매칭·변경 판정 L143~194 | 만료 pending 수만큼의 증가, pending 매칭 실패와 shouldPublish=false 분기를 대조했다. 발행 억제는 변화 판정 코드로 확인했다. |
| L289 | tc.gateway.plc.change.suppressed.total \| 변경 판정 결과 발행을 억제할 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; PLC 이름 L78~86 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L133~141 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 만료 수 L79~85 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 매칭·변경 판정 L143~194 | 만료 pending 수만큼의 증가, pending 매칭 실패와 shouldPublish=false 분기를 대조했다. 발행 억제는 변화 판정 코드로 확인했다. |
| L291 | 변경 억제는 오류 수가 아니라 값의 변화가 없어 발행하지 않은 횟수다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; PLC 이름 L78~86 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L133~141 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 만료 수 L79~85 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 매칭·변경 판정 L143~194 | 만료 pending 수만큼의 증가, pending 매칭 실패와 shouldPublish=false 분기를 대조했다. 발행 억제는 변화 판정 코드로 확인했다. |

### 21. PLC 격리 호출이 성공한 횟수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L301 | `tc.gateway.quarantine.total`은 PLC 프레임 실패 또는 연속 폴링 대기 초과의 격리 호출이 예외 없이 반환한 뒤 증가한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 격리 이름 L87~89 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L142~144 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 격리 L230~236 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 격리 L112~122 | 프레임 실패·폴링 대기 초과의 quarantine 호출 반환 뒤 증가한다. 현재 격리 집합 크기를 읽는 Gauge가 아니다. |
| L301 | 단위는 건이며 누적 카운터다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 격리 이름 L87~89 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L142~144 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 격리 L230~236 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 격리 L112~122 | 프레임 실패·폴링 대기 초과의 quarantine 호출 반환 뒤 증가한다. 현재 격리 집합 크기를 읽는 Gauge가 아니다. |
| L301 | 현재 격리된 설비 수와 구별한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 격리 이름 L87~89 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L142~144 ; nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 격리 L230~236 ; nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 격리 L112~122 | 프레임 실패·폴링 대기 초과의 quarantine 호출 반환 뒤 증가한다. 현재 격리 집합 크기를 읽는 Gauge가 아니다. |

### 22. Gateway 조정 세션과 소유권 게이지

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L311 | tc_gateway_coordination_session_state \| 무단위·state 태그가 현재 세션 상태와 같으면 1, 다르면 0 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 게이지 L46~64 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java @ d3636ab86763 ; 상태 공급 L123~150 | state별 0/1·ownerFence 유무·epoch 추출을 대조했다. 공급 값은 session과 ownerRegistry에서 읽는다. |
| L312 | tc_gateway_shard_owned \| 무단위·소유권 증적이 있으면 1, 없으면 0 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 게이지 L46~64 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java @ d3636ab86763 ; 상태 공급 L123~150 | state별 0/1·ownerFence 유무·epoch 추출을 대조했다. 공급 값은 session과 ownerRegistry에서 읽는다. |
| L313 | tc_gateway_shard_owner_epoch \| 순번·현재 소유권 epoch, 미보유 시 0 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 게이지 L46~64 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java @ d3636ab86763 ; 상태 공급 L123~150 | state별 0/1·ownerFence 유무·epoch 추출을 대조했다. 공급 값은 session과 ownerRegistry에서 읽는다. |

### 23. Gateway 소유권 수명과 차단 설비 수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L323 | tc_gateway_shard_lease_remaining_ms \| 게이지·ms \| 소유권 임대의 남은 시간 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 수명·차단 L61~64 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java @ d3636ab86763 ; 수명·집합 공급 L143~150 | leaseRemainingMs와 fencedEquipmentIds.size의 Gauge 공급 및 단위를 대조했다. |
| L324 | tc_gateway_coordination_fenced_resources \| 게이지·개 \| 차단된 설비 집합 크기 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 수명·차단 L61~64 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java @ d3636ab86763 ; 수명·집합 공급 L143~150 | leaseRemainingMs와 fencedEquipmentIds.size의 Gauge 공급 및 단위를 대조했다. |

### 24. Gateway 소유권 차단 사건 카운터

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L334 | 단위는 건이며 Gateway 누적 카운터다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록·증가 L66~79 L97~140 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayTransportOwnershipGuard.java @ d3636ab86763 ; 오래된 소유권 거절 L88~96 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 전이·자체 차단 L234~234 L411~411 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayFenceQuiescer.java @ d3636ab86763 ; 정지 대기 초과 L68~78 | 오래된 소유권 거절, ShardLeaseManager의 전이·자체 차단, Quiescer의 채널 종료 대기 실패 뒤 증가를 대조했다. |
| L338 | tc_gateway_coordination_stale_fenced_total \| 오래된 소유권의 처리 거절 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록·증가 L66~79 L97~140 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayTransportOwnershipGuard.java @ d3636ab86763 ; 오래된 소유권 거절 L88~96 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 전이·자체 차단 L234~234 L411~411 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayFenceQuiescer.java @ d3636ab86763 ; 정지 대기 초과 L68~78 | 오래된 소유권 거절, ShardLeaseManager의 전이·자체 차단, Quiescer의 채널 종료 대기 실패 뒤 증가를 대조했다. |
| L339 | tc_gateway_coordination_self_fence_total \| 스스로 처리 권한을 차단한 사건 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록·증가 L66~79 L97~140 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayTransportOwnershipGuard.java @ d3636ab86763 ; 오래된 소유권 거절 L88~96 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 전이·자체 차단 L234~234 L411~411 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayFenceQuiescer.java @ d3636ab86763 ; 정지 대기 초과 L68~78 | 오래된 소유권 거절, ShardLeaseManager의 전이·자체 차단, Quiescer의 채널 종료 대기 실패 뒤 증가를 대조했다. |
| L340 | tc_gateway_shard_owner_epoch_transition_total \| 소유권 epoch 전이 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록·증가 L66~79 L97~140 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayTransportOwnershipGuard.java @ d3636ab86763 ; 오래된 소유권 거절 L88~96 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 전이·자체 차단 L234~234 L411~411 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayFenceQuiescer.java @ d3636ab86763 ; 정지 대기 초과 L68~78 | 오래된 소유권 거절, ShardLeaseManager의 전이·자체 차단, Quiescer의 채널 종료 대기 실패 뒤 증가를 대조했다. |
| L341 | tc_gateway_self_fence_quiesce_timeout_total \| 자체 차단 뒤 처리 정지 대기의 시간 초과 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록·증가 L66~79 L97~140 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayTransportOwnershipGuard.java @ d3636ab86763 ; 오래된 소유권 거절 L88~96 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 전이·자체 차단 L234~234 L411~411 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayFenceQuiescer.java @ d3636ab86763 ; 정지 대기 초과 L68~78 | 오래된 소유권 거절, ShardLeaseManager의 전이·자체 차단, Quiescer의 채널 종료 대기 실패 뒤 증가를 대조했다. |

### 25. Gateway 명령 원장과 지연 응답 기록

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L352 | tc_gateway_command_result_replay_total \| 저장된 명령 결과를 재현할 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록 L72~77 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/RedisGatewayCommandResultLedger.java @ d3636ab86763 ; 재현·충돌 L50~65 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiDeferredLifecycleReplyService.java @ d3636ab86763 ; 재발행 L380~393 | 명령 원장의 REPLAY·CONFLICT 분기와 지연 응답 publishResult 성공 뒤 증가를 대조했다. |
| L353 | tc_gateway_command_idempotency_conflict_total \| 명령 원장에서 같은 식별자의 입력 충돌을 확인할 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록 L72~77 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/RedisGatewayCommandResultLedger.java @ d3636ab86763 ; 재현·충돌 L50~65 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiDeferredLifecycleReplyService.java @ d3636ab86763 ; 재발행 L380~393 | 명령 원장의 REPLAY·CONFLICT 분기와 지연 응답 publishResult 성공 뒤 증가를 대조했다. |
| L354 | tc_gateway_deferred_reply_republish_total \| 지연 lifecycle 응답을 재발행할 때 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록 L72~77 ; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/RedisGatewayCommandResultLedger.java @ d3636ab86763 ; 재현·충돌 L50~65 ; nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiDeferredLifecycleReplyService.java @ d3636ab86763 ; 재발행 L380~393 | 명령 원장의 REPLAY·CONFLICT 분기와 지연 응답 publishResult 성공 뒤 증가를 대조했다. |

### 26. Gateway 조정 증적의 거부 코드별 수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L362 | `tc_gateway_coordination_proof_rejected_total`은 조정 증적 거부를 `code` 태그별로 누적하는 건 단위 카운터다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 코드별 등록·기록 L81~90 L148~152 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayRouteMoveCoordinator.java @ d3636ab86763 ; 증적 거부 L173~190 | 코드별 Counter 등록과 null·RECORDED 제외를 대조했다. RouteMoveCoordinator의 거절 호출도 확인했다. |
| L362 | 성공 결과인 RECORDED는 등록·증가 대상에서 뺀다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 코드별 등록·기록 L81~90 L148~152 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayRouteMoveCoordinator.java @ d3636ab86763 ; 증적 거부 L173~190 | 코드별 Counter 등록과 null·RECORDED 제외를 대조했다. RouteMoveCoordinator의 거절 호출도 확인했다. |
| L362 | null 판정도 증가시키지 않는다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 코드별 등록·기록 L81~90 L148~152 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayRouteMoveCoordinator.java @ d3636ab86763 ; 증적 거부 L173~190 | 코드별 Counter 등록과 null·RECORDED 제외를 대조했다. RouteMoveCoordinator의 거절 호출도 확인했다. |
| L364 | 조정 사건의 진단은 `00.rag/admin/coordination-troubleshooting.md`에서 다룬다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 코드별 등록·기록 L81~90 L148~152 ; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayRouteMoveCoordinator.java @ d3636ab86763 ; 증적 거부 L173~190 | 코드별 Counter 등록과 null·RECORDED 제외를 대조했다. RouteMoveCoordinator의 거절 호출도 확인했다. |

### 27. Backend 명령 응답 수신과 해석 실패

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L373 | kafka.command.received \| 응답 해석 후 eventType 태그별 수신 기록 | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; 등록·호출·증가 L92~94 L143~157 L207~239 L393~407 | 응답 해석 후 eventType별 received 증가와 해석 예외의 parse_error 증가를 대조했다. 업무 성공 전의 수신 기록이다. |
| L374 | kafka.command.parse_error \| JSON 해석 또는 계약 변환 실패 처리 | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; 등록·호출·증가 L92~94 L143~157 L207~239 L393~407 | 응답 해석 후 eventType별 received 증가와 해석 예외의 parse_error 증가를 대조했다. 업무 성공 전의 수신 기록이다. |
| L376 | received는 응답을 업무 처리하기 전에 증가하므로 업무 성공 건수가 아니다. | nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; 등록·호출·증가 L92~94 L143~157 L207~239 L393~407 | 응답 해석 후 eventType별 received 증가와 해석 예외의 parse_error 증가를 대조했다. 업무 성공 전의 수신 기록이다. |

### 28. 로그인 성공·거절 사유의 계측

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L386 | ui.auth.login.success \| 건·세션 저장 후 증가 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L25~31 L61~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; 로그인 결과 L92~133 | 사용자 없음·비활성·비밀번호 검사 reason과 sessionPort.save 뒤 success를 대조했다. |
| L387 | ui.auth.login.failure \| 건·로그인 거절 시 reason별 증가 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L25~31 L61~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; 로그인 결과 L92~133 | 사용자 없음·비활성·비밀번호 검사 reason과 sessionPort.save 뒤 success를 대조했다. |
| L389 | failure의 reason은 `user_not_found`·`inactive_account`·`password_mismatch`이며 각각 사용자 없음·비활성 계정·비밀번호 불일치 검사에서 기록한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L25~31 L61~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; 로그인 결과 L92~133 | 사용자 없음·비활성·비밀번호 검사 reason과 sessionPort.save 뒤 success를 대조했다. |
| L389 | 두 값은 Backend 누적 카운터다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L25~31 L61~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; 로그인 결과 L92~133 | 사용자 없음·비활성·비밀번호 검사 reason과 sessionPort.save 뒤 success를 대조했다. |

### 29. 로그아웃 완료와 캐시 제거 실패의 차이

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L399 | ui.auth.logout.success \| DB 세션 폐기 뒤 로그아웃 흐름이 끝날 때 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L33~39 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LogoutUseCase.java @ 985d17c604dd ; 로그아웃 L62~84 | DB revoke 후 캐시 제거 예외 기록과 그 뒤 success 증가를 대조했다. 두 값은 서로 배타적이지 않다. |
| L400 | ui.auth.logout.cache.evict.failure \| 캐시 제거 호출에서 예외가 날 때 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L33~39 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LogoutUseCase.java @ 985d17c604dd ; 로그아웃 L62~84 | DB revoke 후 캐시 제거 예외 기록과 그 뒤 success 증가를 대조했다. 두 값은 서로 배타적이지 않다. |
| L402 | 캐시 제거 실패를 잡은 뒤에도 success를 증가시킬 수 있다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L33~39 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LogoutUseCase.java @ 985d17c604dd ; 로그아웃 L62~84 | DB revoke 후 캐시 제거 예외 기록과 그 뒤 success 증가를 대조했다. 두 값은 서로 배타적이지 않다. |
| L402 | 두 값은 서로 배타적인 성공·실패 분류가 아니다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L33~39 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LogoutUseCase.java @ 985d17c604dd ; 로그아웃 L62~84 | DB revoke 후 캐시 제거 예외 기록과 그 뒤 success 증가를 대조했다. 두 값은 서로 배타적이지 않다. |

### 30. 인증 검증의 캐시 hit와 miss

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L411 | Backend 누적 카운터이며 단위는 건이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L41~47 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 캐시 결과 L80~96 ; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java @ 985d17c604dd ; 등록·조회 L60~61 L75~118 | 인증 흐름 Optional 유무와 Redis 어댑터의 키 없음·자료 반환·타입 불일치·조회 예외를 각각 대조했다. |
| L411 | 인증 흐름과 Redis 어댑터가 각각 기록한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L41~47 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 캐시 결과 L80~96 ; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java @ 985d17c604dd ; 등록·조회 L60~61 L75~118 | 인증 흐름 Optional 유무와 Redis 어댑터의 키 없음·자료 반환·타입 불일치·조회 예외를 각각 대조했다. |
| L415 | ui.auth.token.cache.hit \| 인증 흐름이 캐시 조회 결과를 받았을 때 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L41~47 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 캐시 결과 L80~96 ; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java @ 985d17c604dd ; 등록·조회 L60~61 L75~118 | 인증 흐름 Optional 유무와 Redis 어댑터의 키 없음·자료 반환·타입 불일치·조회 예외를 각각 대조했다. |
| L416 | ui.auth.token.cache.miss \| 인증 흐름이 빈 결과를 받았을 때 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L41~47 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 캐시 결과 L80~96 ; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java @ 985d17c604dd ; 등록·조회 L60~61 L75~118 | 인증 흐름 Optional 유무와 Redis 어댑터의 키 없음·자료 반환·타입 불일치·조회 예외를 각각 대조했다. |
| L417 | session_cache.hit \| Redis 어댑터가 캐시 자료를 사용자 정보로 반환할 때 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L41~47 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 캐시 결과 L80~96 ; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java @ 985d17c604dd ; 등록·조회 L60~61 L75~118 | 인증 흐름 Optional 유무와 Redis 어댑터의 키 없음·자료 반환·타입 불일치·조회 예외를 각각 대조했다. |
| L418 | session_cache.miss \| Redis 키 없음·타입 불일치·조회 예외 경로 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L41~47 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 캐시 결과 L80~96 ; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java @ 985d17c604dd ; 등록·조회 L60~61 L75~118 | 인증 흐름 Optional 유무와 Redis 어댑터의 키 없음·자료 반환·타입 불일치·조회 예외를 각각 대조했다. |

### 31. 세션 검증 실패의 reason 태그

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L426 | `ui.auth.token.validation.failure`는 Backend의 검증 거절을 reason별로 누적하는 건 단위 카운터다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L49~51 L68~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 거절 L93~115 L154~162 | ValidateTokenUseCase의 세션 없음·사용자 없음·비활성·캐시 재검증 실패를 reason별로 대조했다. |
| L430 | invalid_session \| 유효한 DB 세션을 찾지 못함 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L49~51 L68~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 거절 L93~115 L154~162 | ValidateTokenUseCase의 세션 없음·사용자 없음·비활성·캐시 재검증 실패를 reason별로 대조했다. |
| L431 | missing_user \| 세션의 사용자를 찾지 못함 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L49~51 L68~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 거절 L93~115 L154~162 | ValidateTokenUseCase의 세션 없음·사용자 없음·비활성·캐시 재검증 실패를 reason별로 대조했다. |
| L432 | inactive_user \| 사용자가 활성 상태가 아님 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L49~51 L68~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 거절 L93~115 L154~162 | ValidateTokenUseCase의 세션 없음·사용자 없음·비활성·캐시 재검증 실패를 reason별로 대조했다. |
| L433 | revoked_or_expired \| 캐시 hit 뒤 DB 재검증에서 유효 세션 없음 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L49~51 L68~79 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 거절 L93~115 L154~162 | ValidateTokenUseCase의 세션 없음·사용자 없음·비활성·캐시 재검증 실패를 reason별로 대조했다. |

### 32. 재검증·최근 접속 갱신의 보조 지표

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L444 | ui.auth.token.recheck.invalidated \| 카운터·건·재검증에서 무효 세션의 캐시 제거 후 기록 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L53~58 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 기록 L154~165 L184~194 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiTokenRecheckWindowCache.java @ 985d17c604dd ; 게이지·크기 L51~54 L77~89 | 무효 세션 캐시 제거 뒤 invalidated, 비동기 최근 접속 갱신 예외, size 조회 전 만료 제거를 대조했다. |
| L445 | ui.auth.last-seen.update.failure \| 카운터·건·최근 접속 시각의 비동기 DB 갱신 예외 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L53~58 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 기록 L154~165 L184~194 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiTokenRecheckWindowCache.java @ 985d17c604dd ; 게이지·크기 L51~54 L77~89 | 무효 세션 캐시 제거 뒤 invalidated, 비동기 최근 접속 갱신 예외, size 조회 전 만료 제거를 대조했다. |
| L446 | ui.auth.token.recheck.cache.size \| 게이지·개·만료 항목 정리 뒤 재검증 시각 캐시 크기 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L53~58 L61~65 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 기록 L154~165 L184~194 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiTokenRecheckWindowCache.java @ 985d17c604dd ; 게이지·크기 L51~54 L77~89 | 무효 세션 캐시 제거 뒤 invalidated, 비동기 최근 접속 갱신 예외, size 조회 전 만료 제거를 대조했다. |

### 33. Backend 조정 작업 terminal 계열의 실제 증가 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L454 | `tc.ui.operation.terminal`은 result 태그별 건 단위 카운터다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L40~45 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 결과 기록 L554~560 L638~646 L671~683 | result별 Counter와 실제 증가 위치를 대조했다. REPAIR_REQUIRED는 전환 결과 코드 검사 전에 증가한다. |
| L458 | PASS \| 장벽 열기와 PASS 확정 호출이 OK를 반환 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L40~45 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 결과 기록 L554~560 L638~646 L671~683 | result별 Counter와 실제 증가 위치를 대조했다. REPAIR_REQUIRED는 전환 결과 코드 검사 전에 증가한다. |
| L459 | FAIL \| 커밋 전 중단 확정 후 임시 DB 상태 제거 완료 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L40~45 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 결과 기록 L554~560 L638~646 L671~683 | result별 Counter와 실제 증가 위치를 대조했다. REPAIR_REQUIRED는 전환 결과 코드 검사 전에 증가한다. |
| L460 | REPAIR_REQUIRED \| 수동 복구 전환을 요청한 뒤 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L40~45 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 결과 기록 L554~560 L638~646 L671~683 | result별 Counter와 실제 증가 위치를 대조했다. REPAIR_REQUIRED는 전환 결과 코드 검사 전에 증가한다. |
| L462 | REPAIR_REQUIRED는 전환 결과 코드가 OK인지 검사하기 전에 증가한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L40~45 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 결과 기록 L554~560 L638~646 L671~683 | result별 Counter와 실제 증가 위치를 대조했다. REPAIR_REQUIRED는 전환 결과 코드 검사 전에 증가한다. |
| L462 | 실제 확정된 고유 작업 수로 단정하지 않는다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L40~45 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 결과 기록 L554~560 L638~646 L671~683 | result별 Counter와 실제 증가 위치를 대조했다. REPAIR_REQUIRED는 전환 결과 코드 검사 전에 증가한다. |

### 34. Backend 참여자 재고정과 제외

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L472 | tc.ui.operation.participant.refreeze \| 커밋 전 만료 참여자를 반영한 재고정 성공 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L46~47 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 재고정·제외 L330~344 L493~505 | freezeParticipants·excludeExpiredParticipant의 OK 분기 뒤 증가를 대조했다. |
| L473 | tc.ui.operation.participant.exclusion \| 만료 참여자 제외 호출 성공 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L46~47 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 재고정·제외 L330~344 L493~505 | freezeParticipants·excludeExpiredParticipant의 OK 분기 뒤 증가를 대조했다. |

### 35. Backend 응답 증적과 최종 revision 기록 실패

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L484 | tc.ui.operation.reply.rejected \| 작업 없음·현재 작업과 불일치·오래된 소유권 등 응답 증적 거부 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L49~51 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 증적 거부 L88~111 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 기록 실패 L595~607 | proof의 작업 없음·작업 불일치·오래된 소유권 거절과 최종 revision 기록 false를 대조했다. |
| L485 | tc.ui.operation.finalized.revision.not.recorded \| 설비의 최종 revision 기록 호출이 false를 반환 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L49~51 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 증적 거부 L88~111 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 기록 실패 L595~607 | proof의 작업 없음·작업 불일치·오래된 소유권 거절과 최종 revision 기록 false를 대조했다. |

### 36. Backend 복구 스캔 수와 한 번의 전진 단계 수

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L496 | tc.ui.operation.recovery.backlog \| 게이지·건 \| 스캔 제한을 적용해 읽은 미종결 작업 목록 크기 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L54~55 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 제한 스캔 L54~60 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 단계 기록 L148~166 | 스캔 제한으로 읽은 pending 목록 크기를 Gauge에 기록한다. steps>0일 때만 전진 단계 수를 분포에 기록한다. |
| L497 | tc.ui.operation.steps \| 분포·회 \| 실행기 한 번의 전진 호출에서 진행한 단계 수 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L54~55 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 제한 스캔 L54~60 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 단계 기록 L148~166 | 스캔 제한으로 읽은 pending 목록 크기를 Gauge에 기록한다. steps>0일 때만 전진 단계 수를 분포에 기록한다. |
| L499 | backlog는 저장소 전체 미종결 건수를 무제한으로 센 값이 아니다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L54~55 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 제한 스캔 L54~60 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 단계 기록 L148~166 | 스캔 제한으로 읽은 pending 목록 크기를 Gauge에 기록한다. steps>0일 때만 전진 단계 수를 분포에 기록한다. |
| L499 | steps는 진행 단계가 0보다 클 때만 기록한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L54~55 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 제한 스캔 L54~60 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 단계 기록 L148~166 | 스캔 제한으로 읽은 pending 목록 크기를 Gauge에 기록한다. steps>0일 때만 전진 단계 수를 분포에 기록한다. |

### 37. Backend 보상 발급과 원 작업 되돌림

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L509 | tc.ui.operation.compensation.issued \| 보상 발급 호출이 작업 ID를 반환 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L52~53 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 되돌림·발급 L578~587 L682~687 | 보상 issuer가 ID를 반환한 경우와 markRolledBack의 OK 분기에서 각각 증가한다. |
| L510 | tc.ui.operation.rolled.back \| 보상 작업 PASS 뒤 원 작업 되돌림 확정이 OK를 반환 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L52~53 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 되돌림·발급 L578~587 L682~687 | 보상 issuer가 ID를 반환한 경우와 markRolledBack의 OK 분기에서 각각 증가한다. |
| L512 | 보상 발급 횟수와 되돌림 완료 횟수는 별도 사건이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L52~53 ; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 되돌림·발급 L578~587 L682~687 | 보상 issuer가 ID를 반환한 경우와 markRolledBack의 OK 분기에서 각각 증가한다. |

### 38. Business 조정 상태·적용 revision·차단량

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L520 | Business의 게이지다. | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L28~28 L61~74 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationStatus.java @ 7db24f7466bd ; 공급 값 L51~54 L83~103 | Business binder의 Gauge 등록과 session 상태·마지막 적용 revision·fencedCount·candidateRegistry.size를 대응시켰다. |
| L524 | tc_business_coordination_session_state \| 무단위·state 태그가 현재 세션 상태면 1, 아니면 0 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L28~28 L61~74 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationStatus.java @ 7db24f7466bd ; 공급 값 L51~54 L83~103 | Business binder의 Gauge 등록과 session 상태·마지막 적용 revision·fencedCount·candidateRegistry.size를 대응시켰다. |
| L525 | tc_business_coordination_applied_global_revision \| 순번·마지막 적용 전역 revision | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L28~28 L61~74 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationStatus.java @ 7db24f7466bd ; 공급 값 L51~54 L83~103 | Business binder의 Gauge 등록과 session 상태·마지막 적용 revision·fencedCount·candidateRegistry.size를 대응시켰다. |
| L526 | tc_business_coordination_fenced_resources \| 개·차단된 설비 수 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L28~28 L61~74 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationStatus.java @ 7db24f7466bd ; 공급 값 L51~54 L83~103 | Business binder의 Gauge 등록과 session 상태·마지막 적용 revision·fencedCount·candidateRegistry.size를 대응시켰다. |
| L527 | tc_business_coordination_prepared_candidates \| 개·candidate 저장소 크기 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L28~28 L61~74 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationStatus.java @ 7db24f7466bd ; 공급 값 L51~54 L83~103 | Business binder의 Gauge 등록과 session 상태·마지막 적용 revision·fencedCount·candidateRegistry.size를 대응시켰다. |

### 39. Business 장벽 대기 수와 수용 한도

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L537 | tc_business_coordination_barrier_waiting_tasks \| 게이지·건 \| 닫힌 장벽에서 기다리는 작업 수 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L75~78 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessBarrierWaitRegistry.java @ 7db24f7466bd ; 대기 수·한도 L35~69 | waitingCount·backlogLimit 및 양수일 때 waiting<limit 판정을 대조했다. |
| L538 | tc_business_coordination_barrier_backlog_limit \| 게이지·건 \| 새 레코드 가져오기를 멈추는 대기 수 한도 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L75~78 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessBarrierWaitRegistry.java @ 7db24f7466bd ; 대기 수·한도 L35~69 | waitingCount·backlogLimit 및 양수일 때 waiting<limit 판정을 대조했다. |
| L540 | 한도가 0 이하면 한도 없는 구성이다. | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L75~78 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessBarrierWaitRegistry.java @ 7db24f7466bd ; 대기 수·한도 L35~69 | waitingCount·backlogLimit 및 양수일 때 waiting<limit 판정을 대조했다. |
| L540 | 양수이면 대기 수가 한도 미만일 때만 더 가져올 수 있다. | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L75~78 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessBarrierWaitRegistry.java @ 7db24f7466bd ; 대기 수·한도 L35~69 | waitingCount·backlogLimit 및 양수일 때 waiting<limit 판정을 대조했다. |

### 40. Business prepare·apply 시도와 소요 시간

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L549 | Business 게이지는 `phase=prepare`·`phase=apply`로 나눈 누적 기록이다. | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 단계 등록 L80~83 L110~120 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 누적 L43~62 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 결과 기록 L142~142 L193~193 | prepare·apply의 시도·거절·누적 나노초와 binder의 초 변환을 대조했다. 호출부는 !advanced를 rejected로 넘긴다. |
| L549 | 백분위 분포가 아니다. | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 단계 등록 L80~83 L110~120 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 누적 L43~62 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 결과 기록 L142~142 L193~193 | prepare·apply의 시도·거절·누적 나노초와 binder의 초 변환을 대조했다. 호출부는 !advanced를 rejected로 넘긴다. |
| L553 | tc_business_coordination_phase_attempts_total \| 건·기록한 시도 수 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 단계 등록 L80~83 L110~120 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 누적 L43~62 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 결과 기록 L142~142 L193~193 | prepare·apply의 시도·거절·누적 나노초와 binder의 초 변환을 대조했다. 호출부는 !advanced를 rejected로 넘긴다. |
| L554 | tc_business_coordination_phase_rejections_total \| 건·진행하지 못한 시도 수 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 단계 등록 L80~83 L110~120 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 누적 L43~62 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 결과 기록 L142~142 L193~193 | prepare·apply의 시도·거절·누적 나노초와 binder의 초 변환을 대조했다. 호출부는 !advanced를 rejected로 넘긴다. |
| L555 | tc_business_coordination_phase_latency_seconds_sum \| 초·시도 소요 시간 합 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 단계 등록 L80~83 L110~120 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 누적 L43~62 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 결과 기록 L142~142 L193~193 | prepare·apply의 시도·거절·누적 나노초와 binder의 초 변환을 대조했다. 호출부는 !advanced를 rejected로 넘긴다. |

### 41. Business 증적 거부와 drain 검사 기록

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L564 | 두 계열은 건 단위 누적치를 게이지로 등록한다. | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L85~86 L99~106 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 기록 L70~79 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 거부·초과 L182~182 L241~241 L254~266 | proof code별 누적 Gauge를 확인했다. drained는 busy가 남으면 기록하며 해당 분기에 기한 비교가 없다. |
| L568 | tc_business_coordination_proof_rejections_total \| code 태그별 원장 증적 거부 수 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L85~86 L99~106 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 기록 L70~79 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 거부·초과 L182~182 L241~241 L254~266 | proof code별 누적 Gauge를 확인했다. drained는 busy가 남으면 기록하며 해당 분기에 기한 비교가 없다. |
| L569 | tc_business_coordination_drain_deadline_exceeded_total \| drain 검사에서 처리 중인 설비가 남아 기록한 횟수 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L85~86 L99~106 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 기록 L70~79 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 거부·초과 L182~182 L241~241 L254~266 | proof code별 누적 Gauge를 확인했다. drained는 busy가 남으면 기록하며 해당 분기에 기한 비교가 없다. |

### 42. Business 지연 작업의 획득·완료·실패

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L577 | Business의 건 단위 누적 게이지다. | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L87~92 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessDelayedPortStatusWorker.java @ 7db24f7466bd ; 작업 기록 L98~141 | claim 성공, payload 해석 예외·발행 예외·complete false, 발행 후 complete true의 각 기록을 대조했다. |
| L581 | tc_business_coordination_delayed_work_claimed_total \| 지연 작업을 획득해 처리 시작 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L87~92 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessDelayedPortStatusWorker.java @ 7db24f7466bd ; 작업 기록 L98~141 | claim 성공, payload 해석 예외·발행 예외·complete false, 발행 후 complete true의 각 기록을 대조했다. |
| L582 | tc_business_coordination_delayed_work_completed_total \| 발행 후 완료 확정 성공 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L87~92 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessDelayedPortStatusWorker.java @ 7db24f7466bd ; 작업 기록 L98~141 | claim 성공, payload 해석 예외·발행 예외·complete false, 발행 후 complete true의 각 기록을 대조했다. |
| L583 | tc_business_coordination_delayed_work_failed_total \| 처리·발행 또는 완료 확정 실패 | nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L87~92 ; nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessDelayedPortStatusWorker.java @ 7db24f7466bd ; 작업 기록 L98~141 | claim 성공, payload 해석 예외·발행 예외·complete false, 발행 후 complete true의 각 기록을 대조했다. |

### 43. 알람 정책안의 지연·오류 기준

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L591 | 다음은 `03-alert-policy.md`의 1차 정책안이다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 1차안 규칙·적용 범위 L8~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 경보 목표 L2050~2060 | 정책안의 세 지연·오류 행과 운영 가이드 목표 표를 대조했다. 지속·심각도는 정책 정의로만 썼다. |
| L591 | 현재 켜진 알람이나 실제 적용값을 나타내지 않는다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 1차안 규칙·적용 범위 L8~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 경보 목표 L2050~2060 | 정책안의 세 지연·오류 행과 운영 가이드 목표 표를 대조했다. 지속·심각도는 정책 정의로만 썼다. |
| L595 | Backend HTTP 응답 \| p95가 1.0초 초과 \| 5분 \| warning | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 1차안 규칙·적용 범위 L8~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 경보 목표 L2050~2060 | 정책안의 세 지연·오류 행과 운영 가이드 목표 표를 대조했다. 지속·심각도는 정책 정의로만 썼다. |
| L596 | Backend HTTP 오류 \| 5xx 비율이 0.5% 초과 \| 5분 \| critical | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 1차안 규칙·적용 범위 L8~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 경보 목표 L2050~2060 | 정책안의 세 지연·오류 행과 운영 가이드 목표 표를 대조했다. 지속·심각도는 정책 정의로만 썼다. |
| L597 | 세 서버의 Kafka 처리 \| p95가 2.0초 초과 \| 5분 \| warning | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 1차안 규칙·적용 범위 L8~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 경보 목표 L2050~2060 | 정책안의 세 지연·오류 행과 운영 가이드 목표 표를 대조했다. 지속·심각도는 정책 정의로만 썼다. |
| L599 | 운영 가이드도 응답 1.0초 이하·오류 0.005 이하·처리 2.0초 이하를 목표로 적는다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 1차안 규칙·적용 범위 L8~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 경보 목표 L2050~2060 | 정책안의 세 지연·오류 행과 운영 가이드 목표 표를 대조했다. 지속·심각도는 정책 정의로만 썼다. |

### 44. 연결 가용률 알람의 두 문서 기준

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L607 | 알람 1차 정책안은 설비 연결 가용률이 99.5% 미만으로 10분 지속되면 critical로 분류한다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 가용률·후속 배포 L13~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 지속 시간·가용률 L2050~2063 | 정책안 10m·99.5%·critical과 운영 가이드 5분·0.995를 구별했다. 계측 전 비활성은 정책안 L16이다. |
| L607 | 운영 가이드는 0.995 이상을 목표로 두되 목표 이탈 지속 시간을 5분으로 적는다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 가용률·후속 배포 L13~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 지속 시간·가용률 L2050~2063 | 정책안 10m·99.5%·critical과 운영 가이드 5분·0.995를 구별했다. 계측 전 비활성은 정책안 L16이다. |
| L609 | 두 문서의 지속 시간을 구별하며 실제 배포된 경보 값으로 단정하지 않는다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 가용률·후속 배포 L13~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 지속 시간·가용률 L2050~2063 | 정책안 10m·99.5%·critical과 운영 가이드 5분·0.995를 구별했다. 계측 전 비활성은 정책안 L16이다. |
| L609 | 정책안은 계측 의존 규칙을 계측 완료 전에는 비활성으로 두도록 정의한다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 가용률·후속 배포 L13~16 L33~35 ; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 지속 시간·가용률 L2050~2063 | 정책안 10m·99.5%·critical과 운영 가이드 5분·0.995를 구별했다. 계측 전 비활성은 정책안 L16이다. |

### 45. 알람 심각도와 중복 통지 간격의 정책안

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L616 | 1차 정책안에서 critical은 사용자 영향 또는 데이터 무결성 위협, warning은 합격선 근접·초과 상태로 분류한다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 심각도·중복 억제 L18~27 L33~35 | 정책안의 심각도 정의·세 통지 간격·억제 조건을 대조했다. 실제 수신 경로·연락처는 옮기지 않았다. |
| L616 | 아래 값도 실제 배포 설정이 아닌 정책안이다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 심각도·중복 억제 L18~27 L33~35 | 정책안의 심각도 정의·세 통지 간격·억제 조건을 대조했다. 실제 수신 경로·연락처는 옮기지 않았다. |
| L620 | group_wait \| 30s·최초 그룹 알림 대기 | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 심각도·중복 억제 L18~27 L33~35 | 정책안의 심각도 정의·세 통지 간격·억제 조건을 대조했다. 실제 수신 경로·연락처는 옮기지 않았다. |
| L621 | group_interval \| 5m·그룹의 새 알림 통지 간격 | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 심각도·중복 억제 L18~27 L33~35 | 정책안의 심각도 정의·세 통지 간격·억제 조건을 대조했다. 실제 수신 경로·연락처는 옮기지 않았다. |
| L622 | repeat_interval \| 4h·미해소 알림 재통지 간격 | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 심각도·중복 억제 L18~27 L33~35 | 정책안의 심각도 정의·세 통지 간격·억제 조건을 대조했다. 실제 수신 경로·연락처는 옮기지 않았다. |
| L624 | 정책안은 프로세스 다운 critical이 있을 때 같은 application의 SLO warning을 억제한다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 심각도·중복 억제 L18~27 L33~35 | 정책안의 심각도 정의·세 통지 간격·억제 조건을 대조했다. 실제 수신 경로·연락처는 옮기지 않았다. |

### 46. 프로세스 다운과 알림 묶음의 정책안

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L631 | 1차 정책안의 프로세스 다운 규칙은 `up == 0`이 1분 지속될 때 critical로 분류한다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 다운 규칙·그룹·후속 배포 L14~14 L22~27 L33~35 | 정책안 up==0·1m·critical과 group_by 키를 대조했다. 실제 TC 앱 등록값이나 배포 설정으로 표현하지 않았다. |
| L631 | 이 식은 정책 문서의 정의이며 TC 앱의 커스텀 등록 메트릭이 아니다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 다운 규칙·그룹·후속 배포 L14~14 L22~27 L33~35 | 정책안 up==0·1m·critical과 group_by 키를 대조했다. 실제 TC 앱 등록값이나 배포 설정으로 표현하지 않았다. |
| L633 | 정책안의 `group_by`는 `[alertname, application]`이다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 다운 규칙·그룹·후속 배포 L14~14 L22~27 L33~35 | 정책안 up==0·1m·critical과 group_by 키를 대조했다. 실제 TC 앱 등록값이나 배포 설정으로 표현하지 않았다. |
| L633 | 같은 프로세스의 같은 규칙을 한 묶음으로 통지하려는 정의다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 다운 규칙·그룹·후속 배포 L14~14 L22~27 L33~35 | 정책안 up==0·1m·critical과 group_by 키를 대조했다. 실제 TC 앱 등록값이나 배포 설정으로 표현하지 않았다. |
| L633 | 실제 배포된 rule·라우팅·수신 경로를 뜻하지 않는다. | nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 다운 규칙·그룹·후속 배포 L14~14 L22~27 L33~35 | 정책안 up==0·1m·critical과 group_by 키를 대조했다. 실제 TC 앱 등록값이나 배포 설정으로 표현하지 않았다. |

### 47. 관측 결과를 확인하고 장애를 진단하는 문서

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L640 | 일상 점검과 지표 수신 확인은 `00.rag/admin/daily-checks-and-log-inspection.md`가 소유한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메트릭 점검·장애 연결 L2082~2086 L2055~2060 | 목록의 소유 범위와 기존 일상 점검·플랫폼 장애 문서를 대조했다. 절차를 복제하지 않은 문서 위치 안내다. |
| L640 | Kafka·DB·메모리 등 플랫폼 장애 진단은 `00.rag/admin/platform-troubleshooting.md`에서 다룬다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메트릭 점검·장애 연결 L2082~2086 L2055~2060 | 목록의 소유 범위와 기존 일상 점검·플랫폼 장애 문서를 대조했다. 절차를 복제하지 않은 문서 위치 안내다. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 문서·주석 | 확인한 코드·설정 | 반영 |
|---|---|---|---|
| 계측 후속 표시 | 메시지 지연·설비 연결 대시보드 제목에 ★커스텀계측 후속이 남아 있다. | 세 앱 Timer와 EquipmentConnectionMetrics는 이미 등록하며 실제 처리·연결 경로에서 기록한다. | 계측 부재로 쓰지 않고 실제 구현 범위를 썼다. |
| drain 이름·주석 | BusinessCoordinationMetrics L75~79는 기한을 넘긴 대기로 설명한다. | BusinessCandidateReconciler L256~266은 busy가 남으면 증가하며 해당 분기에 기한 비교가 없다. | 실제 drain 검사 기록 횟수로 설명했다. |
| 수동 복구 확정 수 | UiCoordinationMetrics L88~90은 수동 복구 작업을 센다고 설명한다. | UiOperationExecutor L671~683은 전환의 OK 검사 전 증가한다. | 확정된 고유 작업 수로 단정하지 않았다. |
| 자원 패널 단위 | 패널 제목은 heap·CPU를 함께 표시한다. | 세 JSON의 기본 unit은 bytes이며 CPU 단위 override가 없다. | 힙 bytes와 CPU 별도 계열을 구별했다. |

문서 간 기준 차이: 알람 정책안 L13은 연결 가용률 99.5% 미만 10m, 운영 가이드 L2050~2058은 목표 0.995 이탈 5분이다. 두 문서의 지속 시간을 구별했고 실제 적용값으로 확정하지 않았다. SLI 정의의 job 라벨과 정책안의 application 그룹 키도 바꿔 쓰지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 생략 사실 | 이유·근거 | 담당 역할 |
|---|---|---|
| 현재 켜진 알람·적용 임계·통지 경로·연락처 | 실제 rule·라우팅·수신 설정을 조회하지 않았다. 정책안의 채널 식별자도 실제 경로로 옮기지 않았다. | 관측 인프라·알람 운영 담당 |
| 현장 데이터 원천 이름·현재 지표값·Pod별 계열 상태 | 소스·JSON만 확인했고 현장에 접속하지 않았다. | 관측 인프라 담당 |
| tc.gateway.reconnect.flap.total의 실제 증가 사건 | src/main의 incrementReconnectFlap 검색은 선언만 반환했다. 재연결마다 증가한다고 쓸 수 없다. | Gateway 유지보수 담당 |
| tc.ui.operation.digest.conflict·tc.ui.operation.executor.epoch의 운영 의미 | 등록은 있으나 src/main의 incrementIdempotencyConflict·recordExecutorEpoch 검색은 선언만 반환했다. | Backend 조정 기능 유지보수 담당 |
| 라이브러리 지표의 현장 노출·수집기 변환 보장 | Kafka·JVM·HTTP 절은 제공 JSON의 조회식·앱 히스토그램 설정을 설명한다. 실제 계열 존재나 모든 변환을 실측하지 않았다. | 관측 인프라 담당 |
| 모든 실패를 빠짐없이 세거나 고유 사건 수와 같다는 보장 | 실제 증가 지점만 확인했다. DLQ 중복 기록 및 REPAIR_REQUIRED 결과 검사 전 기록을 별도로 밝혔다. | 각 앱 유지보수 담당 |

생략 확인 검색은 각 저장소에서 `git grep -n`과 `-- *src/main/*.java`로 수행했다. 검색 결과를 이유로 제품을 수정하지 않았다.

## 다른 문서가 소유해 넣지 않은 사실

| 사실 | 소유 objectKey | 처리 |
|---|---|---|
| 일상 점검·수신 확인·로그 조회 | 00.rag/admin/daily-checks-and-log-inspection.md | 절차 대신 위치를 안내한다. |
| Kafka·DB·Redis·메모리 장애 판별 | 00.rag/admin/platform-troubleshooting.md | 진단·복구 절차를 복제하지 않는다. |
| 관측 endpoint·protocol·sampler 값 | 00.rag/admin/configuration-reference.md | O §8.2의 배포 대상 주소·설정표를 반복하지 않는다. |
| 설치·수집기 배포·설정 반영 | 00.rag/admin/installation-and-service-lifecycle.md·00.rag/admin/upgrade-rollback-and-configuration-apply.md | 참조 문서에 가져오기·접속·재배포 명령을 넣지 않는다. |
| HSMS 타이머 값과 뜻 | 00.rag/user/secs-hsms-message-and-timer-reference.md | 만료 항목당 증가만 설명한다. |
| 조정 상태·보상 작업의 복구 | 00.rag/admin/coordination-troubleshooting.md·00.rag/admin/coordination-ownership-and-recovery-check.md | enum·오류 코드 표·복구 절차를 복제하지 않는다. |
| 처리실패 화면·PLC 발행 진단 | 00.rag/user/dlq-quarantine-and-repair-task.md·00.rag/user/dlq-quarantine-troubleshooting.md·00.rag/user/plc-troubleshooting.md | 화면 카드/목록과 계측 사건을 구별하고 화면 절차는 반복하지 않는다. |

## 이전 작성본 검사 결과

아래 기계 검사와 최초 사람 검사는 보완 전 검색 원문 SHA-256 `21a2425a5f80ae2d075605a1ab7f3765d072a04e4b4d18adfea949fcec8ccf0f`의 이력이다. 메인 세션에서 발견한 감소 조건 누락은 아래 보완 기록과 갱신된 사실 대조 표로 정정한다.

- 최종 사실 대조는 47절·196개 문장/자료 행/코드 블록이다.
- 중단 전 검사 결과는 같은 thread의 실제 출력상 exit 1·8건(범위 6건·중복 본문 그룹 2건), 후속 exit 1·1건(범위)이다. 원문 중복 및 범위를 보완하고 실패 이력을 보존했다.
- 재개 뒤 Business 상태 공급과 자원 패널 인용 범위를 실제 위치로 보완했다. GatewayMetricNames 인자와 RuntimeMeterSupport 정규화로 표의 Gateway 등록명 23개를 대조해 미일치 0개를 확인했다. HSMS·격리·연결 게이지와 공통 Timer는 문단으로 설명하며 reconnect 계열은 생략했다.
- 도구: git 2.52.0.windows.1, GNU bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10.
- 검사기 소스 SHA-256: `1a4a95236f567c0996f30c481e92f1e54d4839526a68fd388abb0ee9fd2697d9`. 검사기·ContentSanitizer는 수정하지 않았다.
- 검색 원문 마지막 편집 뒤 검사기를 실행했다. exit 0, 47절·151자식, 최대 자식 151자·부모 373자, 대상 위반 0·CROSS VIOLATION 0·MASK 0이다.
- 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/admin/metric-dashboard-and-alert-reference.md`
- 위 명령의 Java 자식 git 조회에는 이 프로세스에만 GIT_CONFIG_COUNT=6과 safe.directory를 nori-common·nori-tc-common·nori-tc-exec·nori-tc-gateway·nori-tc-business·nori-tc-ui-backend의 정확한 절대경로로 설정했다. 전역 설정 파일은 바꾸지 않았다.
- 재개 뒤 최종 검사 1회, wall time 55.9647813초, 종료코드 0.

마지막 출력 10줄:

```text
    L622-622 표 행 46  | 항목=repeat_interval | 정책안 값·의미=4h·미해소 알림 재통지 간격
    L624-624 문단 62  | 정책안은 프로세스 다운 critical이 있을 때 같은 application의 SLO warning을 억제한다.
§ L626-633 [19] 프로세스 다운과 알림 묶음의 정책안   parent=202 children=2 sources=1
    L631-631 문단 93  | 1차 정책안의 프로세스 다운 규칙은 up == 0이 1분 지속될 때 critical로 분류한다. 이 식은 정책 문서의 정의이며 TC 앱의 커스텀 등록 메트릭이 아니다.
    L633-633 문단 108  | 정책안의 group_by는 [alertname, application]이다. 같은 프로세스의 같은 규칙을 한 묶음으로 통지하려는 정의다. 실제 배포된 rule·라우팅·수신 경로를 뜻하지 않는다.
§ L635-640 [23] 관측 결과를 확인하고 장애를 진단하는 문서   parent=145 children=1 sources=1
    L640-640 문단 145  | 일상 점검과 지표 수신 확인은 00.rag/admin/daily-checks-and-log-inspection.md가 소유한다. Kafka·DB·메모리 등 플랫폼 장애 진단은 00.rag/admin/platform-troubleshooting.md에서 다룬다.
SUMMARY sections=47 children=151 maxChild=151 maxParent=373
CORPUS docs=49/53 sections=850 part={admin=14, user=35} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=11, 트러블슈팅=12}
TOTAL files=49 targets=1 violations=0
```

### 사람 검사 판정

47절 전부의 제목·원천·독립 답·중복을 확인했다. 표본 40절보다 넓게 읽었으며 별도로 실제 무작위 표본 40절도 아래에 보존한다. 작성자 자체 확인으로 메인 세션 독립 검증을 대체하지 않는다.

| 절 번호·제목 | 질문 식별 | 원천 일치 | 독립 답 | 중복 |
|---|---|---|---|---|
| 1. 등록명과 대시보드 조회명을 구별하기 | 충족 | 충족 | 충족 | 없음 |
| 2. Kafka 메시지 처리 시간은 어디부터 어디까지인가 | 충족 | 충족 | 충족 | 없음 |
| 3. 메시지 처리 지연 p95를 초로 읽기 | 충족 | 충족 | 충족 | 없음 |
| 4. 설비별 연결 게이지의 1과 0 | 충족 | 충족 | 충족 | 없음 |
| 5. 설비 연결 가용률은 설비별 최근 상태 평균이다 | 충족 | 충족 | 충족 | 없음 |
| 6. 화면 HTTP 응답 시간 p95 | 충족 | 충족 | 충족 | 없음 |
| 7. 서버 오류 비율의 분자와 분모 | 충족 | 충족 | 충족 | 없음 |
| 8. 제공 Grafana 대시보드와 서비스 선택값 | 충족 | 충족 | 충족 | 없음 |
| 9. Kafka 처리량 패널의 전송과 소비 | 충족 | 충족 | 충족 | 없음 |
| 10. 메모리·CPU 패널에서 서로 다른 단위를 읽기 | 충족 | 충족 | 충족 | 없음 |
| 11. Gateway의 활성·바인딩 연결 수 | 충족 | 충족 | 충족 | 없음 |
| 12. Gateway가 바인딩을 거절하거나 기다리다 끝난 횟수 | 충족 | 충족 | 충족 | 없음 |
| 13. Gateway 큐 깊이는 기록된 큐 크기의 합이다 | 충족 | 충족 | 충족 | 없음 |
| 14. Gateway 큐가 넘친 횟수 | 충족 | 충족 | 충족 | 없음 |
| 15. Gateway 소비 지연과 커밋 실패 | 충족 | 충족 | 충족 | 없음 |
| 16. 연결 없는 설비 명령과 이벤트 발행 결과 | 충족 | 충족 | 충족 | 없음 |
| 17. DLQ 발행과 파싱 실패 카운터를 해석하기 | 충족 | 충족 | 충족 | 없음 |
| 18. 후속 처리·네트워크 쓰기·입력 검증 실패 | 충족 | 충족 | 충족 | 없음 |
| 19. HSMS 타이머 만료 횟수 | 충족 | 충족 | 충족 | 없음 |
| 20. PLC 응답 대기·상관관계·변경 억제 | 충족 | 충족 | 충족 | 없음 |
| 21. PLC 격리 호출이 성공한 횟수 | 충족 | 충족 | 충족 | 없음 |
| 22. Gateway 조정 세션과 소유권 게이지 | 충족 | 충족 | 충족 | 없음 |
| 23. Gateway 소유권 수명과 차단 설비 수 | 충족 | 충족 | 충족 | 없음 |
| 24. Gateway 소유권 차단 사건 카운터 | 충족 | 충족 | 충족 | 없음 |
| 25. Gateway 명령 원장과 지연 응답 기록 | 충족 | 충족 | 충족 | 없음 |
| 26. Gateway 조정 증적의 거부 코드별 수 | 충족 | 충족 | 충족 | 없음 |
| 27. Backend 명령 응답 수신과 해석 실패 | 충족 | 충족 | 충족 | 없음 |
| 28. 로그인 성공·거절 사유의 계측 | 충족 | 충족 | 충족 | 없음 |
| 29. 로그아웃 완료와 캐시 제거 실패의 차이 | 충족 | 충족 | 충족 | 없음 |
| 30. 인증 검증의 캐시 hit와 miss | 충족 | 충족 | 충족 | 없음 |
| 31. 세션 검증 실패의 reason 태그 | 충족 | 충족 | 충족 | 없음 |
| 32. 재검증·최근 접속 갱신의 보조 지표 | 충족 | 충족 | 충족 | 없음 |
| 33. Backend 조정 작업 terminal 계열의 실제 증가 조건 | 충족 | 충족 | 충족 | 없음 |
| 34. Backend 참여자 재고정과 제외 | 충족 | 충족 | 충족 | 없음 |
| 35. Backend 응답 증적과 최종 revision 기록 실패 | 충족 | 충족 | 충족 | 없음 |
| 36. Backend 복구 스캔 수와 한 번의 전진 단계 수 | 충족 | 충족 | 충족 | 없음 |
| 37. Backend 보상 발급과 원 작업 되돌림 | 충족 | 충족 | 충족 | 없음 |
| 38. Business 조정 상태·적용 revision·차단량 | 충족 | 충족 | 충족 | 없음 |
| 39. Business 장벽 대기 수와 수용 한도 | 충족 | 충족 | 충족 | 없음 |
| 40. Business prepare·apply 시도와 소요 시간 | 충족 | 충족 | 충족 | 없음 |
| 41. Business 증적 거부와 drain 검사 기록 | 충족 | 충족 | 충족 | 없음 |
| 42. Business 지연 작업의 획득·완료·실패 | 충족 | 충족 | 충족 | 없음 |
| 43. 알람 정책안의 지연·오류 기준 | 충족 | 충족 | 충족 | 없음 |
| 44. 연결 가용률 알람의 두 문서 기준 | 충족 | 충족 | 충족 | 없음 |
| 45. 알람 심각도와 중복 통지 간격의 정책안 | 충족 | 충족 | 충족 | 없음 |
| 46. 프로세스 다운과 알림 묶음의 정책안 | 충족 | 충족 | 충족 | 없음 |
| 47. 관측 결과를 확인하고 장애를 진단하는 문서 | 충족 | 충족 | 충족 | 없음 |

- 무작위 40절 번호: 1, 2, 3, 4, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 30, 31, 33, 34, 35, 36, 38, 39, 40, 41, 42, 43, 44, 45. 번호·제목 대응은 위 표와 같다.
- 제목 정확 중복·불필요한 반복 제목 틀·빈 틀 문장·이름 토막 뜻풀이·잘린 문장·미확인 조치·오답 인용의 미해소 지적은 없다. 필요한 단위는 표 머리글과 대상 문맥에 남겼다.
- 권한 경계: admin 문서 1개이며 운영 지표·정책을 다룬다. user 문서를 수정하거나 admin 사실을 user에 복제하지 않았다.
- 비밀값·개인 연락처·현장 접속 주소는 넣지 않았다. 최종 mask-v1 적용에서 MASK 출력은 없었다.

## 실행·정리

- 독립 Agent·Claude·추가 AI CLI 호출 0회. 본 세션이 직접 조사·작성했다.
- 제품 빌드·시험·서버 접속·Docker·kubectl·ssh·업로드·커밋을 실행하지 않았다. 소스 읽기와 문서 검사만 수행했다.
- 중단을 포함한 전체 wall time·provider 토큰 사용량·비용은 미확인이다. 확인한 최종 검사 시간과 별도로 구별한다.
- 작성 기록 전사 스크립트의 첫 메모리 실행은 PowerShell stdin의 us-ascii(CodePage 20127) 때문에 정규식 컴파일에서 실패했다. 파일이 생성되지 않았음을 확인하고 그 프로세스의 출력 인코딩만 UTF-8로 지정해 재실행했다. 제품·검사기·전역 config는 고치지 않았다.
- 검사용 임시 파일은 만들지 않았다. 지정 work 디렉터리는 이미 존재하며 조회 결과 비어 있었다. 사용자 지정 디렉터리는 삭제하지 않았다. 검색 원문과 작성 기록 외의 파일은 생성·수정하지 않았다.

## 메인 세션 검증 결함 1건 보완 (2026-11-19)

- 결함: 연결 수 게이지 표에서 active·bound의 감소 조건과 unbound의 비활성화 감소 조건을 빠뜨려 누적값처럼 읽힐 수 있었다. 최초 자체 사람 검사의 완전성 판단에서 놓친 항목이다.
- 원인: 등록부·증가 호출·바인딩 전환은 대조했으나 channelInactive의 수량 감소 분기를 표의 갱신 조건에 연결하지 않았다.
- 변경 범위: 검색 원문의 「Gateway의 활성·바인딩 연결 수」에서 rag-meta 한 줄·설명 한 줄·표 세 행만 바꾸고, 이 작성 기록의 해당 사실 대조 네 행·최종 SHA와 검사 이력을 동기화했다. 다른 절·메트릭·제품 파일은 보존했다.
- 직접 재확인: `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-gateway -C C:/workspace_nori/nori-tc/nori-tc-gateway show d3636ab86763:gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java`.
- 확인한 원문: L162~163에서 active·unbound 증가, L296~297·L467~468·L607~608에서 unbound 감소·bound 증가. L335~341에서 비활성화 당시 BOUND이면 bound 감소, 아니면 unbound 감소, 이어 active 감소다.
- 원천 작업 트리: 해당 파일 경로 한정 status exit 0·변경 행 없음. 제품 파일 수정은 하지 않았다.
- 영향: 현재 활성·바인딩·미바인딩 연결 수의 증가·감소를 한 표에서 답한다. 절 분할 없이 기존 47절·640줄을 유지했다.
- 사람 재검사: 세 행의 현재 수량·증가·감소 조건을 지정 커밋과 각각 대조했다. BOUND가 아닌 비활성화 조건을 bound의 감소 조건과 구별했고, 실제 증가 메서드가 있는 세 바인딩 경로를 원천에 포함했다.
- 최종 기계 검사 결과는 아래에 덧붙인다.

### 보완 후 최종 기계 검사

- 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/admin/metric-dashboard-and-alert-reference.md`
- 종료코드: 0
- 실측 wall time: 59.5513498초
- Git 조회에는 앞선 검사와 동일하게 이 프로세스에만 정확한 6개 저장소의 safe.directory를 지정했다. 전역 config·검사기는 변경하지 않았다.

마지막 출력 10줄:

```text
    L622-622 표 행 46  | 항목=repeat_interval | 정책안 값·의미=4h·미해소 알림 재통지 간격
    L624-624 문단 62  | 정책안은 프로세스 다운 critical이 있을 때 같은 application의 SLO warning을 억제한다.
§ L626-633 [19] 프로세스 다운과 알림 묶음의 정책안   parent=202 children=2 sources=1
    L631-631 문단 93  | 1차 정책안의 프로세스 다운 규칙은 up == 0이 1분 지속될 때 critical로 분류한다. 이 식은 정책 문서의 정의이며 TC 앱의 커스텀 등록 메트릭이 아니다.
    L633-633 문단 108  | 정책안의 group_by는 [alertname, application]이다. 같은 프로세스의 같은 규칙을 한 묶음으로 통지하려는 정의다. 실제 배포된 rule·라우팅·수신 경로를 뜻하지 않는다.
§ L635-640 [23] 관측 결과를 확인하고 장애를 진단하는 문서   parent=145 children=1 sources=1
    L640-640 문단 145  | 일상 점검과 지표 수신 확인은 00.rag/admin/daily-checks-and-log-inspection.md가 소유한다. Kafka·DB·메모리 등 플랫폼 장애 진단은 00.rag/admin/platform-troubleshooting.md에서 다룬다.
SUMMARY sections=47 children=151 maxChild=151 maxParent=373
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

검사 요약:

```text
SUMMARY sections=47 children=151 maxChild=151 maxParent=373
TOTAL files=51 targets=1 violations=0
```

- 보완 후 판정: 검사 종료코드 0, 위반 0·CROSS VIOLATION 0·MASK 0. 47절·151자식·최대 자식 151자·최대 부모 373자로 절 분할이 필요하지 않았다.
- 변경 범위 자체 확인: 검색 원문 변경 5줄만 메모리에서 이전 문구로 치환하면 보완 전 SHA-256이 정확히 재현됐다. 실제 파일 복원 없이 다른 본문 보존을 확인했다.
- 기록 정합성 자체 확인: 검색 원문 SHA·640줄·47절과 해당 사실 대조 네 행, 원천 줄 범위가 일치한다. 두 파일은 UTF-8 무BOM·LF·NFC다. 이번 보완에서 검사용 임시 파일·추가 Agent·제품 시험을 만들거나 실행하지 않았다.

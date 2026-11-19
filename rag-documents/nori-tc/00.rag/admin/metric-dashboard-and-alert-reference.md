# 메트릭·대시보드·알람
```rag-meta
유형: 참조
적용: nori-tc-exec f1d5e0a8e727·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd·nori-tc-ui-backend 985d17c604dd·nori-tc-common 6fe71f6b2da6·nori-common ae6791e99e91 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 등록명과 대시보드 조회명을 구별하기
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 메트릭 상수 L13~103
원천: nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeMetricNames.java @ 6fe71f6b2da6 ; 공통 이름 위임 L29~30
원천: nori-common/modules/nori-common-runtime-support/src/main/java/com/nori/common/runtime/support/RuntimeMeterSupport.java @ ae6791e99e91 ; 이름 결합·정규화 L30~43 L105~118
원천: nori-tc-exec/docs/observability/02-sli-slo-definition.md @ f1d5e0a8e727 ; 측정 PromQL (1차안)
```

이 문서의 등록명은 Micrometer에 넘기는 문자열이다. PromQL 식에는 제공 대시보드의 조회명을 쓴다. 예를 들어 `tc.message.processing`의 처리 시간 분포는 `tc_message_processing_milliseconds_bucket`으로 조회한다.

Timer의 milliseconds 조회값을 초로 표시할 때는 1000으로 나눈다. 카운터는 기록 호출의 누적 횟수이고, 게이지는 조회 시 제공하는 상태·수량이다. 표의 단위가 건인 누적값과 현재 수량을 구별한다.

## Kafka 메시지 처리 시간은 어디부터 어디까지인가
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; handleRecord L76~85
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; Timer 등록·기록 L169~190
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessMessageProcessingMetrics.java @ 7db24f7466bd ; Timer L27~53
원천: nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; onMessage L101~110
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; onMessage L94~125
```

`tc.message.processing`은 Gateway의 레코드 처리 호출, Business의 수신 처리 호출, Backend의 명령 응답 처리 호출을 감싸는 Timer다. Kafka에 머문 전체 시간이나 설비 작업 완료까지의 시간을 뜻하지 않는다.

세 구현 모두 백분위 계산용 히스토그램을 등록한다. Business와 Backend는 처리 호출이 끝날 때 finally에서 시간을 기록한다. Gateway는 Timer 등록 전이면 시간을 기록하지 않고 처리만 수행한다.

## 메시지 처리 지연 p95를 초로 읽기
```rag-meta
원천: nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43
원천: nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L32~43
원천: nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 지연 패널 L64~75
```

제공 대시보드는 선택한 서비스의 최근 5분 처리 시간 분포를 합쳐 p95를 초로 표시한다. `$application`은 대시보드가 정한 서비스 값이며 조회 라벨은 `job`이다.

```promql
histogram_quantile(0.95, sum(rate(tc_message_processing_milliseconds_bucket{job="$application"}[5m])) by (le)) / 1000
```

표시 임계값은 2.0초다. 임계값 색상은 대시보드 정의이며 알람 발송 여부와는 별개다.

## 설비별 연결 게이지의 1과 0
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; markUp·markDown·등록 L42~82
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ d3636ab86763 ; 해제·바인딩 성공 L188~197 L329~330
```

`tc.gateway.equipment.connection.up`은 `eqp_id` 태그로 설비를 구별하는 무단위 게이지다. 바인딩 성공에서 1, 연결 해제에서 0을 기록한다. 등록된 설비 전체를 미리 0으로 만드는 구현이 아니라 상태 기록이 들어온 설비에 계열을 만든다.

연결 기록이 없는 계열의 부재를 연결 실패율 0으로 해석하지 않는다. 관측된 연결 상태의 의미이며 업무 처리 성공을 뜻하지 않는다.

## 설비 연결 가용률은 설비별 최근 상태 평균이다
```rag-meta
원천: nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 연결 가용률 L47~59
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; 설비 태그 L79~82
```

Gateway 대시보드는 다음 식으로 각 연결 상태 계열의 최근 5분 평균을 표시한다. 식에 설비 전체 평균을 내는 집계는 없다.

```promql
avg_over_time(tc_gateway_equipment_connection_up{job="$application"}[5m])
```

표시는 비율이며 0.995가 99.5%에 해당하는 표시 임계값이다. `eqp_id`별 연결 기록을 구별해 읽는다.

## 화면 HTTP 응답 시간 p95
```rag-meta
원천: nori-tc-exec/apps/tc-ui-backend-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; HTTP 히스토그램 L82~87
원천: nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 응답 시간 L32~43
```

Backend 설정은 `http.server.requests`의 히스토그램을 켠다. 제공 대시보드는 최근 5분 HTTP 요청 분포를 합쳐 p95를 초로 표시하며, 표시 임계값은 1.0초다.

```promql
histogram_quantile(0.95, sum(rate(http_server_requests_milliseconds_bucket{job="$application"}[5m])) by (le)) / 1000
```

화면에서 수행한 업무 전체의 완료 시간이 아니라 서버 HTTP 요청 지표를 사용한 값이다.

## 서버 오류 비율의 분자와 분모
```rag-meta
원천: nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 5xx 비율 L48~59
```

Backend 대시보드의 5xx 비율은 최근 5분 요청 수 증가율에서 다음 분자를 분모로 나눈 값이다. 표시 임계값은 0.005이며 백분율로는 0.5%다.

분자는 HTTP 상태가 5로 시작하는 요청을 센다.

```promql
sum(rate(http_server_requests_milliseconds_count{job="$application",status=~"5.."}[5m]))
```

분모는 같은 서비스의 전체 HTTP 요청을 센다.

```promql
sum(rate(http_server_requests_milliseconds_count{job="$application"}[5m]))
```

4xx 응답은 이 분자에 포함하지 않는다.

## 제공 Grafana 대시보드와 서비스 선택값
```rag-meta
원천: nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~106
원천: nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~90
원천: nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 데이터 원천·서비스·패널 L2~100
```

대시보드 JSON은 `nori-tc-exec/docs/observability/grafana/`에 있다. 세 파일 모두 Prometheus 데이터 원천을 입력받으며, `application` 변수의 상수값을 `job` 필터에 사용한다.

| 파일 | 서비스 값 | 패널 묶음 |
|---|---|---|
| tc-gateway-dashboard.json | tc-gateway | 처리 지연·연결 가용률·Kafka 처리량·자원 |
| tc-business-dashboard.json | tc-business | 처리 지연·Kafka 처리량·자원 |
| tc-ui-backend-dashboard.json | tc-ui-backend | HTTP 응답 시간·5xx 비율·처리 지연·자원 |

## Kafka 처리량 패널의 전송과 소비
```rag-meta
원천: nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L63~88
원천: nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; Kafka 처리량 L47~72
```

Gateway와 Business의 Kafka 처리량 패널은 다음 계열의 최근 5분 증가율을 합산한다. 범례 단위는 초당 레코드다. 처리량 패널에는 업무 성공률 판정식이 없다.

| 조회 계열 | 패널 범례 | 의미 |
|---|---|---|
| kafka_producer_record_send_total | producer send/s | 생산자 전송 증가율 |
| kafka_consumer_fetch_manager_records_consumed_total | consumer records/s | 소비자 레코드 소비 증가율 |

## 메모리·CPU 패널에서 서로 다른 단위를 읽기
```rag-meta
원천: nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L80~98
원천: nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L87~104
원천: nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 자원 패널 L71~88
```

세 대시보드의 자원 패널은 `jvm_memory_used_bytes`에서 `area="heap"`인 값을 서비스별로 합산하고, `process_cpu_usage`를 함께 조회한다.

힙 사용량 조회 단위는 bytes다. CPU는 별도 계열인데 제공 JSON은 패널 기본 단위를 bytes로 지정하고 CPU용 단위 재정의를 두지 않는다. CPU 선을 메모리 바이트 값으로 해석하지 않는다.

## Gateway의 활성·바인딩 연결 수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 연결 이름 L58~66
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 연결 게이지 L149~157 L296~323
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 활성·바인딩·비활성화 L159~163 L286~297 L331~343 L464~468 L604~608
```

세 값은 Gateway 인스턴스가 관리하는 현재 연결 수 게이지이며 단위는 개다.

| 등록명 | 뜻·갱신 조건 |
|---|---|
| tc.gateway.connection.active | 현재 활성 연결 수. 채널 활성화 때 증가하고 비활성화 때 감소한다. |
| tc.gateway.connection.bound | 현재 바인딩 연결 수. 바인딩 성공 때 증가하고 BOUND 상태로 비활성화될 때 감소한다. |
| tc.gateway.connection.unbound | 현재 미바인딩 연결 수. 활성화 때 증가한다. 바인딩 성공 때, 또는 BOUND가 아닌 상태로 비활성화될 때 감소한다. |

## Gateway가 바인딩을 거절하거나 기다리다 끝난 횟수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 바인딩 이름 L16~24
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 카운터 등록 L91~99
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 증가 조건 L658~680 L711~713
```

Gateway의 누적 카운터이며 단위는 건이다.

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc.gateway.duplicate-eqp.reject.total | 바인딩 결과가 중복 연결 거절일 때 |
| tc.gateway.bind.not-owned.total | 바인딩 결과가 소유권 없음일 때 |
| tc.gateway.bind.timeout.total | 바인딩 대기 만료 시 여전히 UNBOUND일 때 |

## Gateway 큐 깊이는 기록된 큐 크기의 합이다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 큐 이름 L67~72
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 게이지·기록·합산 L159~164 L326~357 L370~399
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 크기 기록·제거 L132~135 L183~185 L259~264 L290~295
```

이름 끝의 total과 달리 두 값은 게이지다. 설비별로 마지막 기록한 큐 크기를 합산하며 단위는 건이다.

| 등록명 | 합산 대상 |
|---|---|
| tc.gateway.queue.inbound.depth.total | 설비별 수신 큐 깊이 기록 |
| tc.gateway.queue.outbound.depth.total | 설비별 송신 큐 깊이 기록 |

설비 제거에서는 두 큐 깊이 기록을 지운다. 큐 객체를 매번 직접 조회하는 값과 구별한다.

## Gateway 큐가 넘친 횟수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 초과 이름 L25~30
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L100~105
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; 큐 수용 실패 L129~140 L180~190
```

Gateway의 큐 수용 실패를 누적하며 단위는 건이다.

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc.gateway.queue.inbound.overflow.total | 수신 큐의 offer가 실패할 때 |
| tc.gateway.queue.outbound.overflow.total | 송신 큐의 offer가 실패할 때 |

## Gateway 소비 지연과 커밋 실패
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; Kafka 이름 L31~33 L73~75
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록·합산 L106~108 L165~167 L362~388
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/AbstractGatewayKafkaSubscriber.java @ d3636ab86763 ; 커밋 실패·샘플 L58~73
```

| 등록명 | 종류·단위 | 의미 |
|---|---|---|
| tc.gateway.kafka.consumer.lag.total | 게이지·건 | 토픽·파티션별 마지막 lag 샘플의 합 |
| tc.gateway.kafka.commit.fail.total | 카운터·건 | 커밋 실패 콜백 횟수 누적 |

lag는 시간값이 아니다. 저장된 샘플을 합산하므로 처리 지연 Timer와 구별한다.

## 연결 없는 설비 명령과 이벤트 발행 결과
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 명령·발행 이름 L13~15 L34~39
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L88~90 L109~114
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; 비연결 처리 L190~214
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/publish/GatewayEqpEventKafkaPublisher.java @ d3636ab86763 ; 발행 결과 L98~120 L152~169
```

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc.gateway.commands.drop.no-connection.total | 연결을 찾지 못한 명령을 버리는 경로 |
| tc.gateway.event.publish.success.total | 설비 이벤트 Kafka 전송 성공 경로 |
| tc.gateway.event.publish.fail.total | 이벤트 변환·계약 검사·전송 실패 경로 |

## DLQ 발행과 파싱 실패 카운터를 해석하기
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L40~45
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L115~120
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; 증가 L96~101
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 발행 후 증가 L307~310
```

| 등록명 | 단위 | 증가 지점 |
|---|---|---|
| tc.gateway.dlq.publish.total | 건·누적 | Redis 발행기와 명령 실패 처리부의 발행 후 기록 |
| tc.gateway.decode.fail.total | 건·누적 | Redis 발행기에서 사유가 PARSING_FAILED일 때 |

명령 실패 처리부가 Redis 발행기를 호출하면 DLQ 값은 두 위치에서 증가한다. DLQ 카운터를 고유 메시지 수로 해석하지 않는다.

## 후속 처리·네트워크 쓰기·입력 검증 실패
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L49~57
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L124~132
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandFailureSupport.java @ d3636ab86763 ; 후속 실패 L216~224
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayChannelHandler.java @ d3636ab86763 ; 쓰기·검증 실패 L252~260 L539~550
```

| 등록명 | 기록하는 실패(건·누적) |
|---|---|
| tc.gateway.secondary.failure.total | 원래 오류 뒤 복구·격리·DLQ 같은 후속 처리의 실패 |
| tc.gateway.netty.write.failed.total | Netty 쓰기 완료 리스너에서 확인한 실패 |
| tc.gateway.input.validation.failed.total | 수신 경계 입력 검증 실패 |

## HSMS 타이머 만료 횟수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 이름 L46~48
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L121~123
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/hsms/StarterHsmsSessionTimerAction.java @ d3636ab86763 ; 만료 순회 L67~83
```

`tc.gateway.hsms.timeout.total`은 HSMS 세션 타이머 검사 결과의 만료 항목마다 증가하는 카운터이며 단위는 건이다. 한 번의 검사에 만료 항목이 여러 개면 각각 센다.

타이머 값과 의미는 `00.rag/user/secs-hsms-message-and-timer-reference.md`가 소유한다.

## PLC 응답 대기·상관관계·변경 억제
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; PLC 이름 L78~86
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L133~141
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 만료 수 L79~85
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 매칭·변경 판정 L143~194
```

Gateway PLC 누적 카운터이며 단위는 건이다.

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc.gateway.plc.poll.timeout.total | 만료한 응답 대기 항목마다 |
| tc.gateway.plc.correlation.miss.total | 응답에 대응하는 대기 요청이 없을 때 |
| tc.gateway.plc.change.suppressed.total | 변경 판정 결과 발행을 억제할 때 |

변경 억제는 오류 수가 아니라 값의 변화가 없어 발행하지 않은 횟수다.

## PLC 격리 호출이 성공한 횟수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetricNames.java @ d3636ab86763 ; 격리 이름 L87~89
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/GatewayMetrics.java @ d3636ab86763 ; 등록 L142~144
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; 격리 L230~236
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/PlcPollTimeoutHandler.java @ d3636ab86763 ; 격리 L112~122
```

`tc.gateway.quarantine.total`은 PLC 프레임 실패 또는 연속 폴링 대기 초과의 격리 호출이 예외 없이 반환한 뒤 증가한다. 단위는 건이며 누적 카운터다. 현재 격리된 설비 수와 구별한다.

## Gateway 조정 세션과 소유권 게이지
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 게이지 L46~64
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java @ d3636ab86763 ; 상태 공급 L123~150
```

| 등록명 | 단위·의미 |
|---|---|
| tc_gateway_coordination_session_state | 무단위·state 태그가 현재 세션 상태와 같으면 1, 다르면 0 |
| tc_gateway_shard_owned | 무단위·소유권 증적이 있으면 1, 없으면 0 |
| tc_gateway_shard_owner_epoch | 순번·현재 소유권 epoch, 미보유 시 0 |

## Gateway 소유권 수명과 차단 설비 수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 수명·차단 L61~64
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationStatus.java @ d3636ab86763 ; 수명·집합 공급 L143~150
```

| 등록명 | 종류·단위 | 의미 |
|---|---|---|
| tc_gateway_shard_lease_remaining_ms | 게이지·ms | 소유권 임대의 남은 시간 |
| tc_gateway_coordination_fenced_resources | 게이지·개 | 차단된 설비 집합 크기 |

## Gateway 소유권 차단 사건 카운터
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록·증가 L66~79 L97~140
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayTransportOwnershipGuard.java @ d3636ab86763 ; 오래된 소유권 거절 L88~96
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 전이·자체 차단 L234~234 L411~411
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayFenceQuiescer.java @ d3636ab86763 ; 정지 대기 초과 L68~78
```

단위는 건이며 Gateway 누적 카운터다.

| 등록명 | 기록 대상 |
|---|---|
| tc_gateway_coordination_stale_fenced_total | 오래된 소유권의 처리 거절 |
| tc_gateway_coordination_self_fence_total | 스스로 처리 권한을 차단한 사건 |
| tc_gateway_shard_owner_epoch_transition_total | 소유권 epoch 전이 |
| tc_gateway_self_fence_quiesce_timeout_total | 자체 차단 뒤 처리 정지 대기의 시간 초과 |

## Gateway 명령 원장과 지연 응답 기록
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 등록 L72~77
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/RedisGatewayCommandResultLedger.java @ d3636ab86763 ; 재현·충돌 L50~65
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiDeferredLifecycleReplyService.java @ d3636ab86763 ; 재발행 L380~393
```

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc_gateway_command_result_replay_total | 저장된 명령 결과를 재현할 때 |
| tc_gateway_command_idempotency_conflict_total | 명령 원장에서 같은 식별자의 입력 충돌을 확인할 때 |
| tc_gateway_deferred_reply_republish_total | 지연 lifecycle 응답을 재발행할 때 |

## Gateway 조정 증적의 거부 코드별 수
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationMetrics.java @ d3636ab86763 ; 코드별 등록·기록 L81~90 L148~152
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayRouteMoveCoordinator.java @ d3636ab86763 ; 증적 거부 L173~190
```

`tc_gateway_coordination_proof_rejected_total`은 조정 증적 거부를 `code` 태그별로 누적하는 건 단위 카운터다. 성공 결과인 RECORDED는 등록·증가 대상에서 뺀다. null 판정도 증가시키지 않는다.

조정 사건의 진단은 `00.rag/admin/coordination-troubleshooting.md`에서 다룬다.

## Backend 명령 응답 수신과 해석 실패
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-kafka-adapter/src/main/java/com/nori/tc/ui/adapters/kafka/subscribe/UiCommandKafkaSubscriber.java @ 985d17c604dd ; 등록·호출·증가 L92~94 L143~157 L207~239 L393~407
```

| 등록명 | 증가 조건(건·누적) |
|---|---|
| kafka.command.received | 응답 해석 후 eventType 태그별 수신 기록 |
| kafka.command.parse_error | JSON 해석 또는 계약 변환 실패 처리 |

received는 응답을 업무 처리하기 전에 증가하므로 업무 성공 건수가 아니다.

## 로그인 성공·거절 사유의 계측
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L25~31 L61~79
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; 로그인 결과 L92~133
```

| 등록명 | 단위·조건 |
|---|---|
| ui.auth.login.success | 건·세션 저장 후 증가 |
| ui.auth.login.failure | 건·로그인 거절 시 reason별 증가 |

failure의 reason은 `user_not_found`·`inactive_account`·`password_mismatch`이며 각각 사용자 없음·비활성 계정·비밀번호 불일치 검사에서 기록한다. 두 값은 Backend 누적 카운터다.

## 로그아웃 완료와 캐시 제거 실패의 차이
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L33~39 L61~65
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LogoutUseCase.java @ 985d17c604dd ; 로그아웃 L62~84
```

| 등록명 | 증가 조건(건·누적) |
|---|---|
| ui.auth.logout.success | DB 세션 폐기 뒤 로그아웃 흐름이 끝날 때 |
| ui.auth.logout.cache.evict.failure | 캐시 제거 호출에서 예외가 날 때 |

캐시 제거 실패를 잡은 뒤에도 success를 증가시킬 수 있다. 두 값은 서로 배타적인 성공·실패 분류가 아니다.

## 인증 검증의 캐시 hit와 miss
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L41~47 L61~65
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 캐시 결과 L80~96
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/session/UiSessionCacheService.java @ 985d17c604dd ; 등록·조회 L60~61 L75~118
```

Backend 누적 카운터이며 단위는 건이다. 인증 흐름과 Redis 어댑터가 각각 기록한다.

| 등록명 | 증가 조건(건·누적) |
|---|---|
| ui.auth.token.cache.hit | 인증 흐름이 캐시 조회 결과를 받았을 때 |
| ui.auth.token.cache.miss | 인증 흐름이 빈 결과를 받았을 때 |
| session_cache.hit | Redis 어댑터가 캐시 자료를 사용자 정보로 반환할 때 |
| session_cache.miss | Redis 키 없음·타입 불일치·조회 예외 경로 |

## 세션 검증 실패의 reason 태그
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L49~51 L68~79
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 거절 L93~115 L154~162
```

`ui.auth.token.validation.failure`는 Backend의 검증 거절을 reason별로 누적하는 건 단위 카운터다.

| reason 값 | 기록 조건 |
|---|---|
| invalid_session | 유효한 DB 세션을 찾지 못함 |
| missing_user | 세션의 사용자를 찾지 못함 |
| inactive_user | 사용자가 활성 상태가 아님 |
| revoked_or_expired | 캐시 hit 뒤 DB 재검증에서 유효 세션 없음 |

## 재검증·최근 접속 갱신의 보조 지표
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiAuthMetrics.java @ 985d17c604dd ; 등록 L53~58 L61~65
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/ValidateTokenUseCase.java @ 985d17c604dd ; 기록 L154~165 L184~194
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/auth/UiTokenRecheckWindowCache.java @ 985d17c604dd ; 게이지·크기 L51~54 L77~89
```

| 등록명 | 종류·단위·의미 |
|---|---|
| ui.auth.token.recheck.invalidated | 카운터·건·재검증에서 무효 세션의 캐시 제거 후 기록 |
| ui.auth.last-seen.update.failure | 카운터·건·최근 접속 시각의 비동기 DB 갱신 예외 |
| ui.auth.token.recheck.cache.size | 게이지·개·만료 항목 정리 뒤 재검증 시각 캐시 크기 |

## Backend 조정 작업 terminal 계열의 실제 증가 조건
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L40~45
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 결과 기록 L554~560 L638~646 L671~683
```

`tc.ui.operation.terminal`은 result 태그별 건 단위 카운터다.

| result 값 | 증가 조건 |
|---|---|
| PASS | 장벽 열기와 PASS 확정 호출이 OK를 반환 |
| FAIL | 커밋 전 중단 확정 후 임시 DB 상태 제거 완료 |
| REPAIR_REQUIRED | 수동 복구 전환을 요청한 뒤 |

REPAIR_REQUIRED는 전환 결과 코드가 OK인지 검사하기 전에 증가한다. 실제 확정된 고유 작업 수로 단정하지 않는다.

## Backend 참여자 재고정과 제외
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L46~47
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 재고정·제외 L330~344 L493~505
```

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc.ui.operation.participant.refreeze | 커밋 전 만료 참여자를 반영한 재고정 성공 |
| tc.ui.operation.participant.exclusion | 만료 참여자 제외 호출 성공 |

## Backend 응답 증적과 최종 revision 기록 실패
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L49~51
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiReplyProofIngestService.java @ 985d17c604dd ; 증적 거부 L88~111
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 기록 실패 L595~607
```

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc.ui.operation.reply.rejected | 작업 없음·현재 작업과 불일치·오래된 소유권 등 응답 증적 거부 |
| tc.ui.operation.finalized.revision.not.recorded | 설비의 최종 revision 기록 호출이 false를 반환 |

## Backend 복구 스캔 수와 한 번의 전진 단계 수
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L54~55
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 제한 스캔 L54~60
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 단계 기록 L148~166
```

| 등록명 | 종류·단위 | 의미 |
|---|---|---|
| tc.ui.operation.recovery.backlog | 게이지·건 | 스캔 제한을 적용해 읽은 미종결 작업 목록 크기 |
| tc.ui.operation.steps | 분포·회 | 실행기 한 번의 전진 호출에서 진행한 단계 수 |

backlog는 저장소 전체 미종결 건수를 무제한으로 센 값이 아니다. steps는 진행 단계가 0보다 클 때만 기록한다.

## Backend 보상 발급과 원 작업 되돌림
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCoordinationMetrics.java @ 985d17c604dd ; 등록 L52~53
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 되돌림·발급 L578~587 L682~687
```

| 등록명 | 증가 조건(건·누적) |
|---|---|
| tc.ui.operation.compensation.issued | 보상 발급 호출이 작업 ID를 반환 |
| tc.ui.operation.rolled.back | 보상 작업 PASS 뒤 원 작업 되돌림 확정이 OK를 반환 |

보상 발급 횟수와 되돌림 완료 횟수는 별도 사건이다.

## Business 조정 상태·적용 revision·차단량
```rag-meta
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L28~28 L61~74
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationStatus.java @ 7db24f7466bd ; 공급 값 L51~54 L83~103
```

Business의 게이지다.

| 등록명 | 단위·의미 |
|---|---|
| tc_business_coordination_session_state | 무단위·state 태그가 현재 세션 상태면 1, 아니면 0 |
| tc_business_coordination_applied_global_revision | 순번·마지막 적용 전역 revision |
| tc_business_coordination_fenced_resources | 개·차단된 설비 수 |
| tc_business_coordination_prepared_candidates | 개·candidate 저장소 크기 |

## Business 장벽 대기 수와 수용 한도
```rag-meta
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L75~78
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessBarrierWaitRegistry.java @ 7db24f7466bd ; 대기 수·한도 L35~69
```

| 등록명 | 종류·단위 | 의미 |
|---|---|---|
| tc_business_coordination_barrier_waiting_tasks | 게이지·건 | 닫힌 장벽에서 기다리는 작업 수 |
| tc_business_coordination_barrier_backlog_limit | 게이지·건 | 새 레코드 가져오기를 멈추는 대기 수 한도 |

한도가 0 이하면 한도 없는 구성이다. 양수이면 대기 수가 한도 미만일 때만 더 가져올 수 있다.

## Business prepare·apply 시도와 소요 시간
```rag-meta
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 단계 등록 L80~83 L110~120
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 누적 L43~62
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 결과 기록 L142~142 L193~193
```

Business 게이지는 `phase=prepare`·`phase=apply`로 나눈 누적 기록이다. 백분위 분포가 아니다.

| 등록명 | 단위·의미 |
|---|---|
| tc_business_coordination_phase_attempts_total | 건·기록한 시도 수 |
| tc_business_coordination_phase_rejections_total | 건·진행하지 못한 시도 수 |
| tc_business_coordination_phase_latency_seconds_sum | 초·시도 소요 시간 합 |

## Business 증적 거부와 drain 검사 기록
```rag-meta
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L85~86 L99~106
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCoordinationMetrics.java @ 7db24f7466bd ; 기록 L70~79
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessCandidateReconciler.java @ 7db24f7466bd ; 거부·초과 L182~182 L241~241 L254~266
```

두 계열은 건 단위 누적치를 게이지로 등록한다.

| 등록명 | 의미 |
|---|---|
| tc_business_coordination_proof_rejections_total | code 태그별 원장 증적 거부 수 |
| tc_business_coordination_drain_deadline_exceeded_total | drain 검사에서 처리 중인 설비가 남아 기록한 횟수 |

## Business 지연 작업의 획득·완료·실패
```rag-meta
원천: nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessCoordinationMetricsBinder.java @ 7db24f7466bd ; 게이지 L87~92
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/coordination/BusinessDelayedPortStatusWorker.java @ 7db24f7466bd ; 작업 기록 L98~141
```

Business의 건 단위 누적 게이지다.

| 등록명 | 기록 조건 |
|---|---|
| tc_business_coordination_delayed_work_claimed_total | 지연 작업을 획득해 처리 시작 |
| tc_business_coordination_delayed_work_completed_total | 발행 후 완료 확정 성공 |
| tc_business_coordination_delayed_work_failed_total | 처리·발행 또는 완료 확정 실패 |

## 알람 정책안의 지연·오류 기준
```rag-meta
원천: nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 1차안 규칙·적용 범위 L8~16 L33~35
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 경보 목표 L2050~2060
```

다음은 `03-alert-policy.md`의 1차 정책안이다. 현재 켜진 알람이나 실제 적용값을 나타내지 않는다.

| 대상 | 조건 | 지속 | 심각도 |
|---|---|---|---|
| Backend HTTP 응답 | p95가 1.0초 초과 | 5분 | warning |
| Backend HTTP 오류 | 5xx 비율이 0.5% 초과 | 5분 | critical |
| 세 서버의 Kafka 처리 | p95가 2.0초 초과 | 5분 | warning |

운영 가이드도 응답 1.0초 이하·오류 0.005 이하·처리 2.0초 이하를 목표로 적는다.

## 연결 가용률 알람의 두 문서 기준
```rag-meta
원천: nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 가용률·후속 배포 L13~16 L33~35
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 지속 시간·가용률 L2050~2063
```

알람 1차 정책안은 설비 연결 가용률이 99.5% 미만으로 10분 지속되면 critical로 분류한다. 운영 가이드는 0.995 이상을 목표로 두되 목표 이탈 지속 시간을 5분으로 적는다.

두 문서의 지속 시간을 구별하며 실제 배포된 경보 값으로 단정하지 않는다. 정책안은 계측 의존 규칙을 계측 완료 전에는 비활성으로 두도록 정의한다.

## 알람 심각도와 중복 통지 간격의 정책안
```rag-meta
원천: nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 심각도·중복 억제 L18~27 L33~35
```

1차 정책안에서 critical은 사용자 영향 또는 데이터 무결성 위협, warning은 합격선 근접·초과 상태로 분류한다. 아래 값도 실제 배포 설정이 아닌 정책안이다.

| 항목 | 정책안 값·의미 |
|---|---|
| group_wait | 30s·최초 그룹 알림 대기 |
| group_interval | 5m·그룹의 새 알림 통지 간격 |
| repeat_interval | 4h·미해소 알림 재통지 간격 |

정책안은 프로세스 다운 critical이 있을 때 같은 application의 SLO warning을 억제한다.

## 프로세스 다운과 알림 묶음의 정책안
```rag-meta
원천: nori-tc-exec/docs/observability/03-alert-policy.md @ f1d5e0a8e727 ; 다운 규칙·그룹·후속 배포 L14~14 L22~27 L33~35
```

1차 정책안의 프로세스 다운 규칙은 `up == 0`이 1분 지속될 때 critical로 분류한다. 이 식은 정책 문서의 정의이며 TC 앱의 커스텀 등록 메트릭이 아니다.

정책안의 `group_by`는 `[alertname, application]`이다. 같은 프로세스의 같은 규칙을 한 묶음으로 통지하려는 정의다. 실제 배포된 rule·라우팅·수신 경로를 뜻하지 않는다.

## 관측 결과를 확인하고 장애를 진단하는 문서
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메트릭 점검·장애 연결 L2082~2086 L2055~2060
```

일상 점검과 지표 수신 확인은 `00.rag/admin/daily-checks-and-log-inspection.md`가 소유한다. Kafka·DB·메모리 등 플랫폼 장애 진단은 `00.rag/admin/platform-troubleshooting.md`에서 다룬다.

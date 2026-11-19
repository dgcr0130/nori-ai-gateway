# 일상 점검과 운영 로그 조회
```rag-meta
유형: 운영
적용: nori-tc-exec f1d5e0a8e727·nori-tc-common 6fe71f6b2da6·nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-business 7db24f7466bd 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 매일 Pod 실행 상태와 재시작을 확인하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 일상 점검 L2072~2112
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 실행·준비 검사 L61~95
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 앱 라벨 L12~19
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 앱 라벨 L12~19
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; 앱 라벨 L14~21
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/deployment.yaml @ 9fe69a75dcd3 ; 앱 라벨 L11~19
```

Kubernetes 조회 권한이 있는 운영 담당자는 매일 `tc` namespace의 Gateway·Business·Backend·Simulator·화면 Pod를 확인한다. 명령 예시는 Git Bash 등 셸에서 실행한다.

```sh
kubectl -n tc get pods
```

STATUS가 Running인지, RESTARTS가 이전 점검보다 늘었는지 확인한다. 반복 재시작은 이상으로 조사한다. 조정 기능을 켠 Gateway의 예비 Pod는 Running이어도 Ready가 아닐 수 있다.

## Gateway 준비 수를 점검할 때 실행할 검증
```rag-meta
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 입력과 판정 L12~57
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 배포 후 검증 호출 L364~370
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 준비 수 점검 L2078~2084
```

운영 담당자는 `nori-tc-exec` 루트에서 배포 설정과 일치하는 `config/tc-shard.properties`를 준비한 뒤 실행한다.

```sh
sh ./deploy/scripts/verify-gateway-rollout.sh tc ./config/tc-shard.properties
```

`gateway rollout verified.`와 종료코드 0을 확인한다. 실패하면 Pod 상태와 기동 로그를 조사한다. 성공 조건의 범위와 별도로 확인할 replica·그룹 배정은 `00.rag/admin/installation-and-service-lifecycle.md`에서 다룬다.

## 여러 Pod의 하루 운영 로그를 빠짐없이 조회하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Pod별 로그 조회 L1947~1960
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 하루 범위 L2099~2108
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; app 라벨 L16~23
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; app 라벨 L12~19
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; app 라벨 L12~19
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; app 라벨 L14~21
```

Pod 로그 조회 권한을 가진 운영 담당자는 APP에 `tc-gateway`·`tc-business`·`tc-ui-backend`·`tc-simulator`를 하나씩 넣어 실행한다. Deployment 하나의 로그만 읽으면 여러 replica의 기록을 모두 보지 못한다.

```sh
APP=tc-gateway
for p in $(kubectl -n tc get pod -l app="$APP" -o name); do
  echo "== $p"
  kubectl -n tc logs "$p" --since=24h
done
```

조회 범위는 현재 컨테이너에 남아 있는 기록이다. 조회 실패나 Pod 목록이 비어 있는 경우에는 로그 오류 0건으로 판정하지 않고 조회 대상과 명령 오류를 확인한다.

## 하루 로그에서 ERROR와 WARN을 어떻게 판정하는가
```rag-meta
원천: nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 ; 시각·수준 패턴 L29~36
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 배포용 로그 패턴 L33~45
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 일상 오류 판정 L2099~2108
```

제공된 공통 로그 설정을 적용한 Gateway·Business·Backend는 시각 다음에 수준을 표시한다. POD에 조회할 Pod를 넣고 수준 위치를 기준으로 거른다.

```sh
kubectl -n tc logs "$POD" --since=24h | grep -E '^[0-9-]+ [0-9:.]+ \[(ERROR|WARN)'
```

일상 점검 기준은 ERROR 0건이다. WARN은 ERROR 건수에 넣지 않으며 같은 경고가 계속 늘면 내용을 읽는다. ERROR가 있으면 같은 시각의 앞뒤 원문도 조회해 조사한다.

## 재시작 전 로그와 오류 앞뒤 원문을 읽기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 현재·이전 컨테이너 로그 L1947~1954
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 컨테이너 정의 L44~47
```

운영 담당자는 POD에 조사할 Pod를 넣는다. 첫 명령으로 필터 없이 현재 컨테이너의 하루 로그를 읽고, 재시작 원인을 찾을 때는 둘째 명령으로 이전 컨테이너 로그를 조회한다.

```sh
kubectl -n tc logs "$POD" --since=24h
kubectl -n tc logs "$POD" --previous
```

이전 컨테이너 로그를 얻지 못하면 명령 오류를 기록하고, 확보한 현재 로그와 재시작 시각을 기준으로 조사한다.

## 운영 로그 한 줄에서 시각과 추적 식별자를 찾기
```rag-meta
원천: nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 ; 출력 순서 L29~36
원천: nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 ; 공통 패턴 소비 L16~20
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 상관 키·전파 항목 L1974~1985
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 전파 설정 L168~171
```

공통 설정의 로그 순서는 서울 시각·수준·traceId·본문·앱과 기록 클래스·처리 스레드이다. 수준은 다섯 칸이라 WARN 뒤에는 공백이 붙는다.

요청 흐름을 찾을 때는 traceId와 발생 시각으로 Pod별 기록을 대조한다. 자재·설비 식별자는 `mat_id`·`eqp_id` 전파 설정과 구별한다. 공통 출력 패턴은 이 두 값을 별도 칸으로 출력하지 않는다.

## 컨테이너 안의 앱 로그와 설비 로그 위치
```rag-meta
원천: nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 작업 디렉터리 L15~15
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 로그 기준 경로 L33~45
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Gateway 파일 경로 L199~200
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Business 파일 경로 L294~295
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; Backend 파일 경로 L350~350
원천: nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 ; 앱·설비 파일 분리 L44~103
```

제공된 설정을 적용한 서버 세 앱은 표준 출력과 파일에 로그를 쓴다. 컨테이너의 앱 로그 디렉터리는 Gateway `/app/logs/app/gateway`, Business `/app/logs/app/business`, Backend `/app/logs/app/uibackend`이다.

유효한 eqpId가 있는 기록은 앱 파일에서 제외하고 `/app/logs/eqp/<설비 ID>/`에 쓴다. Gateway·Business의 설비 파일은 각각 `gateway.`·`business.`로 시작한다. 설비 관련 기록은 앱 파일만 찾아서는 빠질 수 있다.

## 파일 로그 보관 설정과 중앙 수집을 구별하기
```rag-meta
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 보관·압축 설정 L33~45
원천: nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 ; 파일별 롤링 정책 L55~90
원천: nori-tc-common/common/tc-common-logging/src/main/java/com/nori/tc/logging/LogCompressionScheduler.java @ 6fe71f6b2da6 ; 압축 설정·주기 L61~99
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 전용 볼륨 L172~179
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 전용 볼륨 L166~173
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 설정 전용 볼륨 L161~168
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 중앙 수집 요청 L1963~1972
```

배포 스크립트의 파일 로그 설정은 보관 14일·파일당 100MB·용량 상한 5GB이다.

5GB는 앱 로그와 설비별 로그의 롤링 대상마다 적용된다.

압축은 켜짐이며 2일이 지난 로그를 대상으로 60분 간격으로 검사한다. 제공된 세 서버 차트에는 로그 보관 볼륨이 없으므로 컨테이너 파일을 장기 보관 근거로 삼지 않는다.

장기 조회가 필요하면 인프라 담당자에게 대상 namespace·Pod·release, 필요한 보관 기간과 조회 권한을 전달해 중앙 수집 경로를 요청한다.

## 매일 화면 응답과 미인증 차단을 확인하기
```rag-meta
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 화면 호스트 L7~21
원천: nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 ; API 전달 L43~58
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 공개 경로·인증 거부 L123~154
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd ; 미인증 응답 L69~81
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 화면 접속 검사 L1379~1390
```

운영 담당자는 설치된 HTTPS 화면 호스트를 HOST에 넣는다. 인증 쿠키를 보내지 않고 API 응답을 확인한다.

```sh
curl -s -o /dev/null -w '%{http_code}\n' "https://$HOST/api/user"
```

401이면 미인증 차단이 동작한 것이다. 200이면 인증·인가 설정을 조사한다. 연결 실패와 인증서 신뢰 실패는 응답 코드 판정과 구별해 조사한다. 화면 가용성은 별도로 브라우저에서 정상 로그인 후 화면이 열리는지 확인한다.

## 설비 연결과 대시보드에서 매일 확인할 항목
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 연결·메트릭 일상 점검 L2082~2086
원천: nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 ; 연결·처리량·자원 패널 L32~104
원천: nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 ; 지연·처리량·자원 패널 L32~88
원천: nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 ; 응답·오류·자원 패널 L32~98
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/observability/metrics/EquipmentConnectionMetrics.java @ d3636ab86763 ; 설비별 연결 계측 L42~82
```

Grafana 조회 권한이 있는 운영 담당자는 설비 연결 상태와 연결 가용률, 메시지 처리 지연, Kafka 처리량, 화면 응답 시간·오류 비율, 메모리·CPU를 확인한다. 설비 상태를 화면에서 읽는 방법은 `00.rag/user/live-map-status.md`에서 확인한다.

지표의 해석·목표는 `00.rag/admin/metric-dashboard-and-alert-reference.md`에서 다룬다. 목표 초과와 지표 자체가 없는 경우를 구별한다. 값이 없으면 수집기·저장소 담당자에게 해당 서비스·지표·조회 기간을 전달해 수신 여부를 확인한다.

## 매일 노드 디스크와 외부 저장소를 점검하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 디스크 점검 L2088~2097
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 노드 분산·볼륨 L25~36
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L166~173
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L161~168
```

노드 조회 권한이 있는 운영 담당자는 NODE에 확인할 노드를 넣고 실행한다.

```sh
kubectl get nodes
kubectl describe node "$NODE" | grep -A5 'Conditions'
```

DiskPressure가 False인지 확인한다. 디스크 점검 대상은 노드뿐 아니라 이미지 저장소와 DB 저장소도 포함한다. 외부 저장소의 사용량·여유 공간은 각 저장소 담당자의 조회 방법으로 확인한다.

## 화면 인증서의 남은 기간을 확인하기
```rag-meta
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 외부 Gateway·호스트 L7~14
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 만료 시각 조회·요청 기준 L2456~2475
```

운영 담당자는 HOST에 HTTPS 화면 호스트를 넣고 인증서 만료 시각을 조회한다.

```sh
echo | openssl s_client -connect "$HOST:443" -servername "$HOST" 2>/dev/null | openssl x509 -noout -enddate
```

만료까지 한 달 미만이면 인증서를 관리하는 Gateway 담당자에게 교체를 요청한다. 인증서 교체 절차는 `00.rag/admin/security-operations.md`에서 확인한다.

## 조정 저장소의 cluster Epoch 존재를 조회하기
```rag-meta
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 실행 입력·status 처리 L30~70
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; Epoch 필수 조회 L43~51
원천: nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/coordination/BusinessCoordinationContext.java @ 7db24f7466bd ; Epoch 필수 조회 L47~57
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/UiCoordinationContext.java @ 985d17c604dd ; Epoch 필수 조회 L40~48
```

조정 기능을 사용하는 운영 담당자는 `nori-tc-exec` 루트에서 조회한다. 실행 전 K8S_SSH_KEY·K8S_NODE·REDIS_NS·REDIS_DEPLOY를 승인된 접속 대상으로 지정한다. 스크립트는 SSH를 거쳐 Redis를 조회한다.

```sh
bash ./coord-epoch.sh status
```

인자 생략 시 논리 DB 0을 조회한다. `(미발급)`이면 조정 기능을 쓰는 세 앱의 다음 기동이 실패한다. 조회 오류와 미발급을 구별하고, 발급·복구는 `00.rag/admin/coordination-ownership-and-recovery-check.md` 절차로 넘긴다.

## 세 서버의 조정 기능 설정이 같은지 조회하기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 조정 환경변수 L137~141
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 조정 환경변수 L130~134
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 조정 환경변수 L122~125
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 조정 설정 일치 L2118~2134
```

Deployment 조회 권한으로 세 서버의 조정 설정을 비교한다. APP과 KEY에 표의 한 행을 넣고 명령을 반복한다.

| APP | KEY |
|---|---|
| tc-gateway | TC_GATEWAY_COORDINATION_ENABLED |
| tc-business | TC_BUSINESS_COORDINATION_ENABLED |
| tc-ui-backend | TC_UI_BACKEND_COORDINATION_ENABLED |

```sh
kubectl -n tc get deploy "$APP" -o jsonpath="{.spec.template.spec.containers[0].env[?(@.name=='$KEY')].value}"
```

세 값이 같은지 확인한다. 한쪽만 다르면 참여 증적이 어긋나므로 조정 운영 담당자에게 전달한다.

## 끝나지 않은 조정 작업 수를 읽기
```rag-meta
원천: nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 진행 중 인덱스 L90~96
원천: nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/store/OperationLedgerRedisStore.java @ 6fe71f6b2da6 ; 인덱스 연결 L78~85
원천: nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 ; 완료 상태에서 인덱스 제거 L47~65
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 진행 중 작업 수 조회 L2136~2145
```

Redis 조회 권한이 있는 담당자는 REDIS_NS·REDIS_WORKLOAD에 운영 Redis의 namespace·워크로드를 지정한다. 인증이 필요한 환경에서는 승인된 인증 경로를 사용하고 자격값을 명령 인자에 쓰지 않는다.

```sh
kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 ZCARD tc:coord:v1:operations:pending
```

반환된 정수는 완료되지 않은 조정 작업 인덱스의 건수다. 조정 기능 전환 배포 전에는 0인지 확인한다. 작업의 복구 판단은 `00.rag/admin/coordination-ownership-and-recovery-check.md`에서 다룬다.

## 화면 조회가 필요한 기록의 담당 문서
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 메시지 전송 결과 L1102~1130
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 통신 로그 목록 L115~148
원천: nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; 처리실패 화면 L19~109
```

화면의 통신 로그·메시지 전송 결과는 `00.rag/user/message-results-and-communication-logs.md`, 처리실패 조회는 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 안내한다.

# TC 플랫폼 트러블슈팅 작성 기록

## 대상

- objectKey: `00.rag/admin/platform-troubleshooting.md`
- 구획: admin, 필요한 권한 전부 `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문 SHA-256: `1bcb843ce08cf04ea99e38f581dac58997a7d89d4fd16375066f031febc139a3`
- 검색 원문 줄 수: 206, 절 수: 15, 본문 문단 수: 45
- 확인일: 2026-11-19 (한국 시간)
- 범위: 원문·작성 기록 두 파일만 작성했다. 제품·설정·다른 문서·git index는 변경하지 않았다. 업로드·네트워크·Docker·kubectl·SSH 실행과 프로세스 종료는 하지 않았다.

## 원천 판본

최초 작성은 당시 HEAD 코드·차트·스크립트를 기준으로 대조했다. 이번 보완에서 변경된 create-k8s-secrets.sh는 아래 작업 트리 SHA-256을 기준으로 대조했다. 지정 수정 커밋의 diff는 결함 탐색에만 사용했으며 과거 운영 관측이나 과거 시험 PASS를 이번 결과로 가져오지 않았다.

| 저장소 | HEAD 12자리 | 본문 인용 파일 상태 |
|---|---|---|
| nori-tc-business | 7db24f7466bd | 대상 6파일 clean; exit 0 |
| nori-tc-common | 6fe71f6b2da6 | 대상 1파일 clean; exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 최초 대상 17파일 clean. 이번 create-k8s-secrets.sh는 M, 상태 조회 exit 0이며 작업 트리 해시를 적용했다. |
| nori-tc-gateway | d3636ab86763 | 대상 5파일 clean; exit 0 |
| nori-tc-ui-front | 9fe69a75dcd3 | 대상 1파일 clean; exit 0 |

각 저장소에 `git -c safe.directory=<정확한 저장소 절대경로> -C <저장소> status --short -- <인용 파일 전체>`를 실행했다. 최초 조회의 경로별 stdout은 모두 비어 있고 exit 0이었다. 이번 보완의 create-k8s-secrets.sh는 수정 상태(M)이며 파일 SHA-256은 fb6002bcd77ad3b1e33b35058906931b9cf60193d6abac3adc6d8381877aeec4다. 세 앱 메시징 설정 사본의 상태 조회는 stdout이 비고 exit 0이다. 전역 git 설정은 변경하지 않았다. 일부 명령의 사용자 전역 ignore 파일 접근 경고는 원천 파일 변경 출력과 구별했다.

최초 읽기 시 형제 저장소 git의 소유자 검사가 거절되어, 이후 읽기 명령에만 정확한 safe.directory를 지정했다. nori-tc-common 전체 상태에서 보인 기존 CLAUDE.md 수정은 인용 대상 밖이며 보존했다.

확인한 수정 이력은 nori-tc-exec 42ae1d79·ecba36d5·fa5cba71과 nori-tc-business 6e6ec61c다. 현재 runtime은 6e6ec61c의 warn 후 계속 처리보다 강화된 체크포인트 계약을 사용한다.

확인한 관련 시험은 RuntimeHealthMarkerWriterTest, GatewayRuntimeHealthIndicatorTest, 세 앱 PublishTimeoutInvariantConfigTest, BusinessRuntimeConnectionShortCircuitTest, RedisBusinessWorkflowCheckpointStoreTest다. 시험 코드를 읽어 기대값을 확인했으며 제품 시험은 실행하지 않았다.

## 절별 사실 대조

본문 45문단을 문장 단위로 나누어 대조했다. 인계·확인 권고는 코드가 인계 기능을 제공한다는 의미가 아니라 관측 가능한 분기와 기존 소유 문서를 바탕으로 정한 운영 권고다. objectKey는 확정 목록 및 기존 문서와 대조했다.

### Gateway가 Running인데 Ready가 아니면 장애인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L17 | 조정 기능을 켠 Gateway에서는 그룹을 얻지 못한 대기 Pod와 실제 장애를 구별한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; attemptAcquireBlocking L200~222 | 그룹 획득 실패의 정상 spare 로그와 획득 예외의 차이를 확인했다. 소유권 조회 절차는 기존 문서에 위임했다. |
| L17 | 기동 로그의 `Instance entered spare mode`와 소유권 배정을 대조한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; attemptAcquireBlocking L200~222 | 그룹 획득 실패의 정상 spare 로그와 획득 예외의 차이를 확인했다. 소유권 조회 절차는 기존 문서에 위임했다. |
| L17 | 소유권 조회는 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; attemptAcquireBlocking L200~222 | 그룹 획득 실패의 정상 spare 로그와 획득 예외의 차이를 확인했다. 소유권 조회 절차는 기존 문서에 위임했다. |
| L19 | 런타임 상태 `OUT_OF_SERVICE`는 소유권 준비 실패뿐 아니라 처리 포화에서도 나온다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayRuntimeHealthIndicator.java @ d3636ab86763 ; health L49~63 | health는 coordinationReady=false 또는 saturated=true일 때 OUT_OF_SERVICE다. |
| L19 | Ready 해제만으로 정상 대기라고 단정하지 않는다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayRuntimeHealthIndicator.java @ d3636ab86763 ; health L49~63 | health는 coordinationReady=false 또는 saturated=true일 때 OUT_OF_SERVICE다. |
| L21 | 대기 Pod 기동 검사 수정 이후 차트는 startup·liveness에서 `DOWN` 여부와 파일 갱신을 검사한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; startup·readiness·liveness L63~95 | Gateway startup/liveness는 != DOWN, readiness는 = UP이고 모두 파일 갱신 조건을 쓴다. 수정 포함 여부는 배포 담당자가 이미지·차트로 대조하도록 권고했다. |
| L21 | readiness만 `UP`을 요구한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; startup·readiness·liveness L63~95 | Gateway startup/liveness는 != DOWN, readiness는 = UP이고 모두 파일 갱신 조건을 쓴다. 수정 포함 여부는 배포 담당자가 이미지·차트로 대조하도록 권고했다. |
| L21 | 반복 재시작이면 적용 차트의 검사식과 앱 이미지 판본을 배포 담당자에게 넘긴다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; startup·readiness·liveness L63~95 | Gateway startup/liveness는 != DOWN, readiness는 = UP이고 모두 파일 갱신 조건을 쓴다. 수정 포함 여부는 배포 담당자가 이미지·차트로 대조하도록 권고했다. |

### Gateway 기동 검사가 계속 실패하면 무엇을 읽는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L30 | 운영 담당자는 적용한 `probes.markerPath`의 파일 존재·상태·수정 시각을 구별한다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 마커 경로·시간 L68~74<br>nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 마커 검사 L66~91 | values의 markerPath와 staleAfterSeconds 및 deployment의 파일 읽기·find 조건을 확인했다. |
| L30 | 기본 경로는 `/app/run/runtime-health`이고 기본 `staleAfterSeconds`는 45다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 마커 경로·시간 L68~74<br>nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 마커 검사 L66~91 | values의 markerPath와 staleAfterSeconds 및 deployment의 파일 읽기·find 조건을 확인했다. |
| L30 | 파일 부재와 갱신 중단도 기동 검사 실패 원인이다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 마커 경로·시간 L68~74<br>nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 마커 검사 L66~91 | values의 markerPath와 staleAfterSeconds 및 deployment의 파일 읽기·find 조건을 확인했다. |
| L32 | 마커는 상태와 epoch 초를 한 줄에 기록한다. | nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeHealthMarkerWriter.java @ 6fe71f6b2da6 ; writeMarkerOnce·resolveStatusCode L112~138 | writeMarkerOnce는 상태와 epoch 초를 기록하며 쓰기 실패를 전파한다. resolveStatusCode는 판정 예외를 DOWN으로 바꾼다. 로그 대조는 진단 권고다. |
| L32 | 판정 호출의 예외는 `DOWN`이 되고, 파일 쓰기 실패는 `런타임 health 마커 파일 기록 실패`로 드러난다. | nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeHealthMarkerWriter.java @ 6fe71f6b2da6 ; writeMarkerOnce·resolveStatusCode L112~138 | writeMarkerOnce는 상태와 epoch 초를 기록하며 쓰기 실패를 전파한다. resolveStatusCode는 판정 예외를 DOWN으로 바꾼다. 로그 대조는 진단 권고다. |
| L32 | 상태만 보지 말고 이전 컨테이너 로그의 첫 오류도 확인한다. | nori-tc-common/common/tc-common-runtime-support/src/main/java/com/nori/tc/common/runtime/support/RuntimeHealthMarkerWriter.java @ 6fe71f6b2da6 ; writeMarkerOnce·resolveStatusCode L112~138 | writeMarkerOnce는 상태와 epoch 초를 기록하며 쓰기 실패를 전파한다. resolveStatusCode는 판정 예외를 DOWN으로 바꾼다. 로그 대조는 진단 권고다. |
| L34 | 마커를 쓰지 않는 옛 이미지와 새 차트 조합이면 이미지·차트 호환 문제로 인계한다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 마커 경로·시간 L68~74<br>nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 마커 검사 L66~91 | values 주석은 마커를 쓰지 않는 옛 이미지와 차트의 호환 실패 및 배포 순서를 명시한다. 로그 명령은 기존 문서 소유로 뒀다. |
| L34 | 로그 조회 명령은 `00.rag/admin/daily-checks-and-log-inspection.md`에서 확인한다. | nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 마커 경로·시간 L68~74<br>nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 마커 검사 L66~91 | values 주석은 마커를 쓰지 않는 옛 이미지와 차트의 호환 실패 및 배포 순서를 명시한다. 로그 명령은 기존 문서 소유로 뒀다. |

### Kafka 발행기를 만들지 못하면 브로커 정지인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L43 | `Failed to construct kafka producer`가 있으면 안쪽 `ConfigException`을 먼저 읽는다. | nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63<br>nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 발행기 시간 설정 L44~53 | 현재 설정 주석과 시험은 발행기 생성 ConfigException을 설명한다. 오류의 안쪽 원인을 읽도록 권고했다. |
| L43 | 시간 관계 위반이면 브로커 복구보다 `tc-messaging.properties`의 발행기 설정을 조사한다. | nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63<br>nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 발행기 시간 설정 L44~53 | 현재 설정 주석과 시험은 발행기 생성 ConfigException을 설명한다. 오류의 안쪽 원인을 읽도록 권고했다. |
| L45 | `spring.kafka.producer.properties.` 뒤의 `delivery.timeout.ms`는 `request.timeout.ms + linger.ms` 이상이어야 한다. | nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63<br>nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet L44~62 | 시험이 delivery >= request + linger와 발행 대기 초×1000 >= delivery를 각각 검사한다. |
| L45 | 별도로 앱 발행 대기 초×1000도 delivery 이상이어야 한다. | nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63<br>nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet L44~62 | 시험이 delivery >= request + linger와 발행 대기 초×1000 >= delivery를 각각 검사한다. |
| L47 | linger 명시 수정 이후 사본은 delivery 30000ms·request 30000ms·linger 0ms다. | nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63<br>nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet L44~62<br>nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 발행기 시간 설정 L44~53 | 수정 사본은 30000/30000/0이고 Business 검증기는 delivery 누락·짧은 발행 대기를 기동 시 거부한다. 변경 절차를 복제하지 않았다. |
| L47 | Business는 delivery 미설정·대기 부족에서 기동을 거절한다. | nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63<br>nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet L44~62<br>nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 발행기 시간 설정 L44~53 | 수정 사본은 30000/30000/0이고 Business 검증기는 delivery 누락·짧은 발행 대기를 기동 시 거부한다. 변경 절차를 복제하지 않았다. |
| L47 | 설정 변경은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`를 따른다. | nori-tc-exec/apps/tc-business-app/src/test/java/com/nori/tc/apps/business/config/PublishTimeoutInvariantConfigTest.java @ f1d5e0a8e727 ; 시간 관계 검사 L23~63<br>nori-tc-business/business/starter/tc-business-starter/src/main/java/com/nori/tc/business/starter/BusinessPublishTimeoutInvariantValidator.java @ 7db24f7466bd ; afterPropertiesSet L44~62<br>nori-tc-exec/apps/tc-business-app/config/tc-messaging.properties @ f1d5e0a8e727 ; 발행기 시간 설정 L44~53 | 수정 사본은 30000/30000/0이고 Business 검증기는 delivery 누락·짧은 발행 대기를 기동 시 거부한다. 변경 절차를 복제하지 않았다. |

### 새 이미지인데 Kafka 설정 결함이 다시 나타나는 이유

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L60 | 이 수정이 들어간 판본의 `create-k8s-secrets.sh`는 `spring.kafka.producer.properties.linger.ms=0`을 Gateway·Business·UI Backend 메시징 생성부에 명시한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58<br>nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424 | 수정된 생성 스크립트 L73·L231·L328의 linger=0과 세 create secret 호출을 확인했다. 이 생성 코드는 이미 있는 Secret을 patch하거나 이미지 교체 때 자동 갱신하지 않는다. |
| L60 | 이미 만든 Secret은 이 수정으로 바뀌지 않는다. | nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58<br>nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424 | 수정된 생성 스크립트 L73·L231·L328의 linger=0과 세 create secret 호출을 확인했다. 이 생성 코드는 이미 있는 Secret을 patch하거나 이미지 교체 때 자동 갱신하지 않는다. |
| L60 | 새 이미지로 교체해도 기존 Secret 값은 따로 확인한다. | nori-tc-exec/apps/tc-gateway-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58<br>nori-tc-exec/apps/tc-ui-backend-app/config/tc-messaging.properties @ f1d5e0a8e727 ; linger 명시 L49~58<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424 | 수정된 생성 스크립트 L73·L231·L328의 linger=0과 세 create secret 호출을 확인했다. 이 생성 코드는 이미 있는 Secret을 patch하거나 이미지 교체 때 자동 갱신하지 않는다. |
| L62 | 운영 담당자는 `tc-gateway-config`·`tc-business-config`·`tc-ui-backend-config`의 `tc-messaging.properties`를 제한된 관리 경로에서 대조한다. | nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424<br>nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 연결 L172~179<br>nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 메시징 설정 읽기 L15~26 | 생성 명령의 세 Secret 리소스와 tc-messaging.properties 키를 확인했다. Gateway는 이를 /app/config에 연결하고 application.yaml에서 읽는다. 비밀 출력 금지는 사용자 지시다. |
| L62 | 비밀값은 출력하지 않는다. | nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424<br>nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 연결 L172~179<br>nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 외부 메시징 설정 읽기 L15~26 | 생성 명령의 세 Secret 리소스와 tc-messaging.properties 키를 확인했다. Gateway는 이를 /app/config에 연결하고 application.yaml에서 읽는다. 비밀 출력 금지는 사용자 지시다. |
| L64 | 운영 중에는 생성 스크립트 재실행 대신 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`의 파일 키별 변경·재반영 절차로 넘긴다. | nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424 | 생성 스크립트는 create secret 명령이다. 운영 키 병합·재반영은 기존 변경 운영 문서 소유이며 누락 키 인계를 권고했다. |
| L64 | 배포 담당자에게 이미지 태그와 누락된 설정 키를 전달한다. | nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Gateway 생성 입력·파일 키 L60~84 L206~213<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Business 생성 입력·파일 키 L219~243 L303~309<br>nori-tc-exec/create-k8s-secrets.sh @ 1d2879d14406 ; Backend 생성 입력·파일 키 L315~339 L416~424 | 생성 스크립트는 create secret 명령이다. 운영 키 병합·재반영은 기존 변경 운영 문서 소유이며 누락 키 인계를 권고했다. |

### Kafka 메시지가 멈추면 연결·인증·적체를 어떻게 가르는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L72 | 운영 담당자는 전달 중단 시각의 앱 로그에서 연결 실패·인증 실패와 발행 실패 예외를 구별한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349<br>nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공·실패 L145~179 | Business 발행 경로는 동기 send 성공 후 partition·offset을 기록하고 실패 원인 예외를 전파한다. O의 연결·인증 조사와 결합했다. |
| L72 | Business의 MES 발행 성공 로그는 partition·offset을 남긴다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349<br>nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공·실패 L145~179 | Business 발행 경로는 동기 send 성공 후 partition·offset을 기록하고 실패 원인 예외를 전파한다. O의 연결·인증 조사와 결합했다. |
| L72 | 실패 로그와 같은 시각의 원인 예외를 Kafka 담당자에게 전달한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349<br>nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공·실패 L145~179 | Business 발행 경로는 동기 send 성공 후 partition·offset을 기록하고 실패 원인 예외를 전파한다. O의 연결·인증 조사와 결합했다. |
| L74 | Kafka 담당자는 앱 접속 대상·보안 규약과 브로커 상태를 대조한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349 | O는 연결 대상·인증 오류 확인과 브로커 운영 주체의 복구를 정한다. 자격 조치는 보안 문서 소유다. |
| L74 | 자격·신뢰 파일 문제면 `00.rag/admin/security-operations.md`로 넘기고, 브로커 정지가 확인되면 브로커 운영 절차로 복구한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349 | O는 연결 대상·인증 오류 확인과 브로커 운영 주체의 복구를 정한다. 자격 조치는 보안 문서 소유다. |
| L76 | 연결 회복 뒤에는 소비 그룹의 LAG와 장애 구간 메시지 보관 범위를 함께 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349 | O는 LAG 확인과 보관 기간 초과 시 LAG만으로 누락을 판정할 수 없음을 설명한다. 실제 보관값은 쓰지 않았다. |
| L76 | 처리량 회복이나 LAG 0만으로 장애 구간의 모든 작업이 처리됐다고 판정하지 않는다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349 | O는 LAG 확인과 보관 기간 초과 시 LAG만으로 누락을 판정할 수 없음을 설명한다. 실제 보관값은 쓰지 않았다. |
| L76 | 보관·재처리 범위는 `00.rag/admin/backup-and-recovery.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Kafka 정지 진단 L2323~2349 | O는 LAG 확인과 보관 기간 초과 시 LAG만으로 누락을 판정할 수 없음을 설명한다. 실제 보관값은 쓰지 않았다. |

### DB 조회 실패와 스키마 기동 실패를 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L85 | 운영 담당자는 조회·저장 실패가 난 앱과 새 Pod의 기동 오류를 나눠 수집한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 정지·연결 확인 L2351~2377 | O의 동일 대상 SELECT 1 확인을 사용하되 접속 실패만으로 서버 정지 원인을 확정하지 않도록 진단 범위를 제한했다. |
| L85 | DB 담당자는 앱과 같은 접속 대상·자격·경로에서 `SELECT 1;` 결과와 오류를 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 정지·연결 확인 L2351~2377 | O의 동일 대상 SELECT 1 확인을 사용하되 접속 실패만으로 서버 정지 원인을 확정하지 않도록 진단 범위를 제한했다. |
| L85 | 접속 실패만으로 DB 서버 정지를 확정하지 않는다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 정지·연결 확인 L2351~2377 | O의 동일 대상 SELECT 1 확인을 사용하되 접속 실패만으로 서버 정지 원인을 확정하지 않도록 진단 범위를 제한했다. |
| L87 | DB에 접속되는데 앱만 실패하면 앱의 자격·연결 수와 연결 풀 고갈 기록을 대조한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 정지·연결 확인 L2351~2377 | O는 응답 후 앱 실패에서 자격·연결 수 확인을 지시한다. 연결 풀·자격 키 설명은 기존 문서 소유다. |
| L87 | 자격 변경은 `00.rag/admin/security-operations.md`, 설정 키는 `00.rag/admin/configuration-reference.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; DB 정지·연결 확인 L2351~2377 | O는 응답 후 앱 실패에서 자격·연결 수 확인을 지시한다. 연결 풀·자격 키 설명은 기존 문서 소유다. |
| L89 | 스키마 검증 오류는 접속 시험 성공과 별개다. | nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 앱 스키마 검증 L89~93<br>nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 외부 migration L50~68 | ddl-auto=validate와 외부 Gradle Flyway ownership을 대조했다. 스키마 적용 절차는 기존 문서에 위임했다. |
| L89 | 앱의 `ddl-auto=validate`와 외부 Flyway 적용 이력을 대조해 DB 담당자에게 넘긴다. | nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 앱 스키마 검증 L89~93<br>nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 외부 migration L50~68 | ddl-auto=validate와 외부 Gradle Flyway ownership을 대조했다. 스키마 적용 절차는 기존 문서에 위임했다. |
| L89 | 스키마 적용 순서는 `00.rag/admin/installation-and-service-lifecycle.md`에서 확인한다. | nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 앱 스키마 검증 L89~93<br>nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 외부 migration L50~68 | ddl-auto=validate와 외부 Gradle Flyway ownership을 대조했다. 스키마 적용 절차는 기존 문서에 위임했다. |

### V3 migration이 이미 있는 객체에서 실패하면 무엇을 대조하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L98 | V3에서 객체가 이미 있다는 오류가 나면 실행한 패키지의 SQL과 Flyway 이력, 기존 객체 정의를 DB 담당자가 대조한다. | nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 컬럼·테이블·인덱스 생성 L19~44<br>nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; migration 입력 L61~68 | 현재 V3에서 대상 컬럼·테이블·인덱스를 확인하고 패키지 SQL과 이력·실제 정의를 대조하도록 권고했다. |
| L98 | 대상은 `tc_user_info.password_changed_at`·`tc_user_password_history`·`idx_tc_user_password_history_user`다. | nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 컬럼·테이블·인덱스 생성 L19~44<br>nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; migration 입력 L61~68 | 현재 V3에서 대상 컬럼·테이블·인덱스를 확인하고 패키지 SQL과 이력·실제 정의를 대조하도록 권고했다. |
| L100 | V3 중복 생성 방지 수정 이후 SQL은 세 객체 생성에 `IF NOT EXISTS`를 쓴다. | nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 컬럼·테이블·인덱스 생성 L19~44 | 현재 SQL의 세 IF NOT EXISTS는 생성 생략이며 기존 정의를 교정하지 않는다. 현재 운영 객체가 같은지는 확인하지 않았다. |
| L100 | 기존 객체의 형식까지 교정하는 구문은 아니므로 객체 존재만으로 올바른 스키마라고 판정하지 않는다. | nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 컬럼·테이블·인덱스 생성 L19~44 | 현재 SQL의 세 IF NOT EXISTS는 생성 생략이며 기존 정의를 교정하지 않는다. 현재 운영 객체가 같은지는 확인하지 않았다. |
| L102 | 적용 이력·실패 SQL 위치·객체 정의와 패키지 판본을 넘긴다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 새 보상 migration L2587~2605 | O의 새 보상 migration 추가 원칙을 적용하고 이력 조작 대신 정식 스키마 변경 절차로 인계했다. |
| L102 | 적용된 V 파일 편집이나 이력 삭제로 재시도하지 않고 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`의 스키마 변경 절차로 인계한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 새 보상 migration L2587~2605 | O의 새 보상 migration 추가 원칙을 적용하고 이력 조작 대신 정식 스키마 변경 절차로 인계했다. |

### Redis 장애로 Gateway가 스스로 내려가는 경우

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L110 | 조정 기능을 켠 Gateway에서 `GW_SHARD_SELF_FENCE`가 있으면 reason을 읽는다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 실패 L206~221<br>nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | 획득·갱신 예외가 각각 다른 reason으로 selfFence를 호출하며 정상 spare와 별도 분기임을 확인했다. |
| L110 | `LEASE_ACQUIRE_REDIS_UNCERTAIN`은 획득 호출 실패, `LEASE_HEARTBEAT_REDIS_UNCERTAIN`은 갱신 호출 실패 경로다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 실패 L206~221<br>nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | 획득·갱신 예외가 각각 다른 reason으로 selfFence를 호출하며 정상 spare와 별도 분기임을 확인했다. |
| L110 | 그룹을 얻지 못한 정상 대기와 구별한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 획득 실패 L206~221<br>nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | 획득·갱신 예외가 각각 다른 reason으로 selfFence를 호출하며 정상 spare와 별도 분기임을 확인했다. |
| L112 | 이 경로는 세션·소유권을 무효화하고 종료를 요청한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | selfFence는 세션 fence·소유권 abandon 뒤 종료를 요청한다. 메모리 부족과 다른 종료 경로라는 진단 근거다. |
| L112 | 운영 담당자는 원인 예외와 Redis 장애 시각을 맞춰 Redis 담당자에게 전달한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | selfFence는 세션 fence·소유권 abandon 뒤 종료를 요청한다. 메모리 부족과 다른 종료 경로라는 진단 근거다. |
| L112 | 재시작 횟수 증가만 보고 메모리 부족으로 분류하지 않는다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | selfFence는 세션 fence·소유권 abandon 뒤 종료를 요청한다. 메모리 부족과 다른 종료 경로라는 진단 근거다. |
| L114 | Redis 접근 회복 뒤에도 미점유 그룹이 남으면 `00.rag/admin/coordination-ownership-and-recovery-check.md`의 소유권 점검으로 넘긴다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | 소유권 회복 확인·자료 손실 복원은 각각 기존 조정 운영·백업 문서 소유로 뒀다. |
| L114 | 자료 손실이 있으면 `00.rag/admin/backup-and-recovery.md`로 구분해 인계한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/lease/GatewayShardLeaseManager.java @ d3636ab86763 ; 갱신·소유권 차단·종료 L353~431 | 소유권 회복 확인·자료 손실 복원은 각각 기존 조정 운영·백업 문서 소유로 뒀다. |

### Redis에 접속되는데 조정 기능 기동이 거절될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L123 | Gateway의 `cluster epoch is not issued yet`는 `tc:coord:v1:cluster:epoch` 미발급 경로다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; 초기 값 필수 검사 L40~51 | GatewayCoordinationContext는 Epoch 조회 결과가 없으면 명시된 오류로 생성에 실패한다. 발급 절차는 기존 문서 소유다. |
| L123 | 발급·손실 복구는 `00.rag/admin/coordination-ownership-and-recovery-check.md`로 넘긴다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 ; 초기 값 필수 검사 L40~51 | GatewayCoordinationContext는 Epoch 조회 결과가 없으면 명시된 오류로 생성에 실패한다. 발급 절차는 기존 문서 소유다. |
| L125 | `coordination lease safety relation violated`는 실제 Redis 명령 대기 시간과 소유권 수명 관계가 맞지 않는 오류다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실제 대기 한도 검사 L259~274<br>nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 관계식·오류 L98~126 | 실제 Lettuce commandTimeout으로 heartbeat·shard-owner 관계식을 검사하고 위반 시 시간 항목을 포함한 예외를 던진다. |
| L125 | 오류의 lease 종류·시간값과 `spring.data.redis.timeout`을 대조한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/autoconfigure/GatewayCoordinationConfiguration.java @ d3636ab86763 ; 실제 대기 한도 검사 L259~274<br>nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 관계식·오류 L98~126 | 실제 Lettuce commandTimeout으로 heartbeat·shard-owner 관계식을 검사하고 위반 시 시간 항목을 포함한 예외를 던진다. |
| L127 | `GW_COORDINATION_REDIS_TIMEOUT_MISMATCH`는 경고다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 관계식·오류 L98~126 | 선언값 불일치는 WARN이며 실제값 관계식 위반과 별개다. 대기 한도 설치 방법은 기존 문서 소유다. |
| L127 | 선언값이 달라도 실제값으로 관계식을 검사한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 관계식·오류 L98~126 | 선언값 불일치는 WARN이며 실제값 관계식 위반과 별개다. 대기 한도 설치 방법은 기존 문서 소유다. |
| L127 | 대기 한도 준비는 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/GatewayCoordinationProperties.java @ d3636ab86763 ; 관계식·오류 L98~126 | 선언값 불일치는 WARN이며 실제값 관계식 위반과 별개다. 대기 한도 설치 방법은 기존 문서 소유다. |

### 여러 설비가 동시에 무응답이면 어느 경로를 조사하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L136 | 여러 설비의 동시 무응답은 영향 설비와 담당 Gateway 그룹을 묶어 조사한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설비 무응답 L2393~2407 | O는 여러 설비 동시 무응답에서 Gateway·공통 경로를 먼저 보도록 한다. 개별 설비 조작은 user 문서에 유지했다. |
| L136 | Gateway 실행·소유권과 Kafka 전달 상태를 먼저 대조한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설비 무응답 L2393~2407 | O는 여러 설비 동시 무응답에서 Gateway·공통 경로를 먼저 보도록 한다. 개별 설비 조작은 user 문서에 유지했다. |
| L136 | 한 대의 등록값·명령 문제는 `00.rag/user/equipment-connection-and-command-troubleshooting.md`에서 구별한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설비 무응답 L2393~2407 | O는 여러 설비 동시 무응답에서 Gateway·공통 경로를 먼저 보도록 한다. 개별 설비 조작은 user 문서에 유지했다. |
| L138 | 대기 연결 설비는 설치 담당자가 설비 포트와 Service의 port·targetPort, TCPRoute의 backendRefs.port·parentRefs.sectionName을 대조한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/service.yaml @ f1d5e0a8e727 ; selector·포트 연결 L11~20<br>nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; listener·Service 연결 L1~21 | Service port→targetPort 및 TCPRoute parentRefs.sectionName→backendRefs.port 연결을 확인해 대기 연결의 대조 항목을 정했다. |
| L138 | 공통 listener나 연결 대상의 불일치는 개별 설비 재등록보다 경로 수정으로 인계한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/service.yaml @ f1d5e0a8e727 ; selector·포트 연결 L11~20<br>nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; listener·Service 연결 L1~21 | Service port→targetPort 및 TCPRoute parentRefs.sectionName→backendRefs.port 연결을 확인해 대기 연결의 대조 항목을 정했다. |
| L140 | Route·Service 변경과 기동 검증은 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설비 무응답 L2393~2407<br>nori-tc-exec/deploy/helm/tc-gateway/templates/service.yaml @ f1d5e0a8e727 ; selector·포트 연결 L11~20<br>nori-tc-exec/deploy/helm/tc-gateway/templates/tcproute.yaml @ f1d5e0a8e727 ; listener·Service 연결 L1~21 | 설치 검증·경로 변경 절차는 lifecycle 문서가 소유하므로 objectKey 안내만 남겼다. |

### 반복 재시작에서 메모리 부족을 확인하는 근거

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L149 | 운영 담당자는 Pod 상세의 종료 사유와 직전 로그, 종료 전 메모리 사용량을 함께 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메모리 부족 L2409~2437 | O의 종료 사유·직전 로그·사용량 확인을 사용해 메모리 종료와 앞 절의 probe·앱 예외 종료를 구별하도록 했다. |
| L149 | 메모리 부족 종료인지 기동 검사 실패·앱 예외에 따른 종료인지 구별한 뒤 조치한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메모리 부족 L2409~2437 | O의 종료 사유·직전 로그·사용량 확인을 사용해 메모리 종료와 앞 절의 probe·앱 예외 종료를 구별하도록 했다. |
| L149 | 재시작 전 로그 조회는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메모리 부족 L2409~2437 | O의 종료 사유·직전 로그·사용량 확인을 사용해 메모리 종료와 앞 절의 probe·앱 예외 종료를 구별하도록 했다. |
| L151 | 배포 담당자에게 해당 컨테이너의 실제 메모리 상한과 `app.resources.limits.memory`·`requests.memory`를 전달한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 자원·JVM 입력 L54~60 L161~167<br>nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; JAVA_OPTS 실행 L35~37 | Gateway resources 값의 전달과 JAVA_OPTS 75.0, Dockerfile의 실제 exec 사용을 대조했다. 메모리 전체가 heap이라는 주장은 하지 않았다. |
| L151 | 제공 Gateway 차트의 JVM 옵션은 `MaxRAMPercentage=75.0`이며 Dockerfile은 JAVA_OPTS를 실행에 사용한다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 자원·JVM 입력 L54~60 L161~167<br>nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; JAVA_OPTS 실행 L35~37 | Gateway resources 값의 전달과 JAVA_OPTS 75.0, Dockerfile의 실제 exec 사용을 대조했다. 메모리 전체가 heap이라는 주장은 하지 않았다. |
| L153 | 메모리 상한 변경은 환경별 값 파일에 반영하고 재배포한 뒤 사용량·재시작을 관찰한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메모리 부족 L2409~2437 | O의 환경별 자원 변경·재배포·회복 관찰을 확인했다. 실제 적용 명령·되돌림은 기존 문서 소유다. |
| L153 | 적용·되돌림은 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 메모리 부족 L2409~2437 | O의 환경별 자원 변경·재배포·회복 관찰을 확인했다. 실제 적용 명령·되돌림은 기존 문서 소유다. |

### 공간 부족 오류는 노드·이미지·DB 중 어디 문제인가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L163 | 로그·임시 파일 쓰기 실패면 해당 Pod가 있는 노드의 공간을, 이미지 내려받기·저장 실패면 레지스트리를, 여러 앱의 DB 쓰기 실패면 DB 저장소를 나눠 조사한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 디스크 포화 L2442~2454 | O의 노드·레지스트리·DB 저장소 구분을 증상별 확인 순서로 재구성했다. 현장 포화를 확정하지 않았다. |
| L163 | 오류가 난 저장소와 실제 여유 공간을 함께 대조한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 디스크 포화 L2442~2454 | O의 노드·레지스트리·DB 저장소 구분을 증상별 확인 순서로 재구성했다. 현장 포화를 확정하지 않았다. |
| L165 | 제공된 Gateway·Business·Backend 차트의 볼륨은 설정 Secret용이며 로그 보관 볼륨이 없다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L172~179<br>nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L166~173<br>nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L161~168 | Gateway·Business·Backend 세 deployment의 볼륨은 config Secret뿐이다. 저장소별 소유권에 따라 정리 대상을 구별하도록 권고했다. |
| L165 | 컨테이너 로그 파일과 외부 DB·레지스트리 저장소를 같은 삭제 대상으로 취급하지 않는다. | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L172~179<br>nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L166~173<br>nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 볼륨 L161~168 | Gateway·Business·Backend 세 deployment의 볼륨은 config Secret뿐이다. 저장소별 소유권에 따라 정리 대상을 구별하도록 권고했다. |
| L167 | 노드·레지스트리·DB 담당자에게 저장소 종류와 쓰기 오류를 각각 넘긴다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 디스크 포화 L2442~2454 | O의 저장소 운영 주체별 회수·쓰기 회복을 따르고 사용자 지시대로 유효 증거 보존을 우선했다. 로그 경로 상세는 기존 문서 소유다. |
| L167 | 필요한 장애 로그를 보존한 뒤 담당 저장소의 정상 회수 절차를 적용하고 쓰기 회복을 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 디스크 포화 L2442~2454 | O의 저장소 운영 주체별 회수·쓰기 회복을 따르고 사용자 지시대로 유효 증거 보존을 우선했다. 로그 경로 상세는 기존 문서 소유다. |
| L167 | 로그 위치는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 디스크 포화 L2442~2454 | O의 저장소 운영 주체별 회수·쓰기 회복을 따르고 사용자 지시대로 유효 증거 보존을 우선했다. 로그 경로 상세는 기존 문서 소유다. |

### 화면 인증서 신뢰 실패와 만료를 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L175 | 화면 HTTPS 접속에서 신뢰 실패가 나면 접속 호스트·제시된 인증서 만료 시각·부모 Gateway의 인증서 참조를 대조한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증서 진단 L2456~2475<br>nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L7~21 | O의 접속 실패·만료 시각·Gateway 참조 확인을 조합했다. 신뢰 실패만으로 만료라는 원인을 확정하지 않았다. |
| L175 | 신뢰 실패 문구만으로 만료를 확정하지 않는다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증서 진단 L2456~2475<br>nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L7~21 | O의 접속 실패·만료 시각·Gateway 참조 확인을 조합했다. 신뢰 실패만으로 만료라는 원인을 확정하지 않았다. |
| L175 | 만료 시각 조회는 `00.rag/admin/daily-checks-and-log-inspection.md`를 따른다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증서 진단 L2456~2475<br>nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L7~21 | O의 접속 실패·만료 시각·Gateway 참조 확인을 조합했다. 신뢰 실패만으로 만료라는 원인을 확정하지 않았다. |
| L177 | 화면 차트의 HTTPRoute는 부모 Gateway와 호스트를 연결한다. | nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L7~21 | HTTPRoute가 parent Gateway와 hostname을 연결함을 확인했다. 비밀값 제외와 관측 결과 인계는 사용자 지시다. |
| L177 | 부모 Gateway 담당자에게 호스트·실패 시각·검증 오류와 인증서 참조 불일치 여부를 넘긴다. | nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L7~21 | HTTPRoute가 parent Gateway와 hostname을 연결함을 확인했다. 비밀값 제외와 관측 결과 인계는 사용자 지시다. |
| L177 | 인증서 내용과 개인 키는 인계 자료에 넣지 않는다. | nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 ; 부모 Gateway·호스트 L7~21 | HTTPRoute가 parent Gateway와 hostname을 연결함을 확인했다. 비밀값 제외와 관측 결과 인계는 사용자 지시다. |
| L179 | 만료 또는 참조 오류가 확인되면 `00.rag/admin/security-operations.md`의 교체 경로로 넘긴다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증서 진단 L2456~2475 | O의 인증서 교체 후 HTTPRoute 호스트 접속·연결 수립 회복 확인을 사용하고 교체 절차는 보안 문서에 뒀다. |
| L179 | 교체 뒤에는 같은 화면 호스트의 HTTPS 접속과 연결 수립을 다시 확인한다. | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 인증서 진단 L2456~2475 | O의 인증서 교체 후 HTTPRoute 호스트 접속·연결 수립 회복 확인을 사용하고 교체 절차는 보안 문서에 뒀다. |

### 연결·끊김 보고가 재실행될 때 중복 여부를 가르는 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L188 | 연결 보고 중복 방지 수정 이후 판본은 payloadRef와 처리기 식별자를 묶어 완료 기록을 확인한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeConnectionShortCircuitTest.java @ 7db24f7466bd ; 같은·다른 참조와 발행 실패 기대값 L132~254<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | 현재 runtime의 payloadRef×handlerId, 입력 digest 판정, COMPLETED skip 로그와 다른 payloadRef 시험 기대값을 대조했다. |
| L188 | 같은 입력의 완료 기록이 남아 있으면 `CHECKPOINT_SKIPPED`를 남기고 발행을 건너뛴다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeConnectionShortCircuitTest.java @ 7db24f7466bd ; 같은·다른 참조와 발행 실패 기대값 L132~254<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | 현재 runtime의 payloadRef×handlerId, 입력 digest 판정, COMPLETED skip 로그와 다른 payloadRef 시험 기대값을 대조했다. |
| L188 | payloadRef가 다르면 별도 메시지다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeConnectionShortCircuitTest.java @ 7db24f7466bd ; 같은·다른 참조와 발행 실패 기대값 L132~254<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | 현재 runtime의 payloadRef×handlerId, 입력 digest 판정, COMPLETED skip 로그와 다른 payloadRef 시험 기대값을 대조했다. |
| L190 | 현재 코드는 처리 성공 뒤에만 TTL이 있는 완료 기록을 저장한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | handling 뒤 markCompleted 호출 및 Redis setIfAbsent의 TTL 전달을 확인했다. 로그의 식별자로 사건을 대조하도록 권고했다. |
| L190 | 운영 담당자는 재전달 전후의 payloadRef·handlerId·messageName·설비 ID와 완료 기록의 보존 여부를 대조해 Business 담당자에게 넘긴다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | handling 뒤 markCompleted 호출 및 Redis setIfAbsent의 TTL 전달을 확인했다. 로그의 식별자로 사건을 대조하도록 권고했다. |
| L190 | 원문 payload는 출력하지 않는다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | handling 뒤 markCompleted 호출 및 Redis setIfAbsent의 TTL 전달을 확인했다. 로그의 식별자로 사건을 대조하도록 권고했다. |
| L192 | 새 연결 사건과 같은 메시지의 재전달을 구별한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | 발행과 완료 저장이 분리돼 있고 기록은 TTL이 있으므로 저장 전 장애·만료·자료 유실까지 영구 중복 방지를 주장하지 않았다. |
| L192 | 처리 뒤 저장 전 실패나 완료 기록 만료·손실까지 중복이 없다고 보장하는 구조는 아니다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 연결 보고 처리·완료 판정 L571~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 저장값·TTL L43~89 | 발행과 완료 저장이 분리돼 있고 기록은 TTL이 있으므로 저장 전 장애·만료·자료 유실까지 영구 중복 방지를 주장하지 않았다. |

### Business 연결 보고가 Redis 오류나 입력 충돌로 멈출 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L202 | 현재 판본의 `workflow checkpoint lookup failed`·`workflow checkpoint record failed`는 Redis 조회·기록 실패다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 조회·충돌·기록 실패 L43~89<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/test/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStoreTest.java @ 7db24f7466bd ; 실패 전파 기대값 L62~83 L110~118 | 현재 Redis adapter의 두 실패 예외와 runtime의 예외 미포착 전파를 확인했다. 예전 커밋의 경고 후 계속 처리와 구분했다. |
| L202 | 기록 실패를 경고만 남기고 성공으로 끝내지 않으므로 Redis 접근 오류와 보고 처리 오류를 분리해 조사한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 조회·충돌·기록 실패 L43~89<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/test/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStoreTest.java @ 7db24f7466bd ; 실패 전파 기대값 L62~83 L110~118 | 현재 Redis adapter의 두 실패 예외와 runtime의 예외 미포착 전파를 확인했다. 예전 커밋의 경고 후 계속 처리와 구분했다. |
| L204 | `connection report checkpoint input digest conflict`는 같은 식별자에 다른 입력이 들어온 경로다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 조회·충돌·기록 실패 L43~89 | claim은 같은 키의 다른 digest를 CONFLICT로 반환하고 runtime이 명시된 예외를 던진다. 기록 삭제로 우회하지 않도록 권고했다. |
| L204 | 완료 기록을 지워 재실행하지 말고 payloadRef·handlerId와 충돌 로그를 Business 담당자에게 넘긴다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652<br>nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/workflow/RedisBusinessWorkflowCheckpointStore.java @ 7db24f7466bd ; 조회·충돌·기록 실패 L43~89 | claim은 같은 키의 다른 digest를 CONFLICT로 반환하고 runtime이 명시된 예외를 던진다. 기록 삭제로 우회하지 않도록 권고했다. |
| L206 | Redis 저장 실패 전 발행이 이뤄졌는지는 별도로 확인한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652<br>nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공 위치 L145~165 | handling 선행·완료 저장 후행과 MES 발행 성공의 partition·offset 로그를 대조했다. 현장 이미지의 소스 판본은 확인하지 않아 인도로 조회하도록 했다. |
| L206 | MES 발행 로그의 partition·offset과 재전달 기록을 대조한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652<br>nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공 위치 L145~165 | handling 선행·완료 저장 후행과 MES 발행 성공의 partition·offset 로그를 대조했다. 현장 이미지의 소스 판본은 확인하지 않아 인도로 조회하도록 했다. |
| L206 | 과거 수정 커밋의 동작과 현장 판본을 같다고 단정하지 않고 이미지에 포함된 소스 판본을 인도 담당자에게 확인한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; 입력 충돌·완료 저장 L627~652<br>nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/publish/BusinessMesCommandKafkaPublisher.java @ 7db24f7466bd ; 발행 성공 위치 L145~165 | handling 선행·완료 저장 후행과 MES 발행 성공의 partition·offset 로그를 대조했다. 현장 이미지의 소스 판본은 확인하지 않아 인도로 조회하도록 했다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼·과거 설명 | 현재 코드·작성 판단 |
|---|---|---|
| DB 접속 실패의 원인 단정 | O L2374~2375는 응답 없음·접속 거부를 DB 정지로 단정하고 응답 후 앱 실패를 자격·연결 수 문제로 단정한다. | 실제 서버 상태는 조회하지 않았다. 본문은 SELECT 1 결과·오류와 앱 경로·자격·풀·스키마를 대조하도록 제한했고 접속 실패만으로 서버 정지를 확정하지 않는다. |
| Gateway 기동 검사 주석 | nori-tc-exec/deploy/helm/tc-gateway/values.yaml L67의 startup은 readiness와 같은 기준이라는 주석이 남아 있다. | 실제 deployment.yaml L71은 != DOWN, L81 readiness는 = UP이다. 주석 대신 실행식을 썼다. 원천 파일은 수정하지 않았다. |
| Redis 장애 영향 범위 | O L2381~2388은 Gateway lease와 Backend session·결과 중심이다. | Business의 현재 Redis 체크포인트 조회·기록 실패도 처리에 영향을 주므로 별도 절로 설명했다. O의 30~60초 회복 문구는 보장값으로 쓰지 않았다. |
| 연결 보고 기록 실패 | 과거 6e6ec61c diff는 기록 실패를 경고 후 계속 처리한다. | 현재 BusinessRuntimeTaskSupport L646~652 및 Redis store L43~89는 조회·기록 오류를 전파한다. 과거 설명은 현행 동작으로 옮기지 않았다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 제외 이유 | 확인할 역할 |
|---|---|---|
| 현재 배포 판본에 네 수정이 포함됐는지 | 현장 이미지·빌드 기록을 조회하지 않았다. | 배포·인도 담당자 |
| 현재 Kafka·DB·Redis 상태와 장애 시각·운영 자원 수치 | 로컬 원천만 확인했으며 서버·클러스터에 접속하지 않았다. | 플랫폼·DB·Kafka·Redis 운영 담당자 |
| 인증서 실제 만료일·현재 Secret 값·자격·인증서 내용·개인 연락처 | 원천으로 확보하지 않았고 비밀값·개인정보를 수록하지 않는다. | 보안·부모 Gateway 담당자 |
| Redis 재시작 뒤 30~60초 자동 회복 보장 | O L2391의 서술을 현장 설정·현재 장애 조건에서 검증하지 않았다. | Redis·Gateway 운영 담당자 |
| Kafka 보관 기간 내 전 메시지 자동 회복 보장 | 브로커·토픽 실제 보존 범위와 처리 결과를 확인하지 않았다. | Kafka·상위 시스템 담당자 |
| 인증서 신뢰 실패의 단일 원인 | 만료·호스트·참조·신뢰 문제를 현장에서 재현하지 않았다. | 보안·부모 Gateway 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에서 복제하지 않은 내용 |
|---|---|
| 00.rag/admin/daily-checks-and-log-inspection.md | Pod·로그 조회 명령, 파일 경로·보관, 인증서 만료 조회 명령, 조정 상태 조회 |
| 00.rag/admin/installation-and-service-lifecycle.md | 신규 설정·초기 값·migration·Helm 기동·중지·확장·검증 명령 |
| 00.rag/admin/upgrade-rollback-and-configuration-apply.md | Secret 키 추출·병합·재기동과 버전·스키마 되돌림 절차 |
| 00.rag/admin/security-operations.md | DB·Kafka·Redis 자격과 신뢰 저장소·인증서 교체 키·명령 |
| 00.rag/admin/backup-and-recovery.md | DB·Redis·Kafka 보관·복원·재처리 절차 |
| 00.rag/admin/configuration-reference.md | 설정 키 전수 의미와 기본값 참조 |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | 소유권 조회·재배치·Epoch 발급·정상 복구 확인 |
| 00.rag/admin/coordination-troubleshooting.md | 보상 미확정·원장 후퇴의 상세 분기 |
| 00.rag/admin/incident-evidence-handover.md | 공통 장애 증거 묶음·인계 항목 |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 한 설비의 화면 등록값·명령·응답 진단과 UI 조작 |

## 다른 문서에서 발견한 사항

- 최초 작성 때 인계한 설치 문서의 linger 보완 누락은 이력이다. 이번에 읽은 `00.rag/admin/installation-and-service-lifecycle.md`에는 별도 보완 절이 생겼지만 L130은 생성 입력 세 곳에 linger가 없다고 설명한다. 수정 B가 들어간 생성 스크립트는 세 곳에 0을 명시하므로, 이 판본 조건과 관련 원천 메타 보완을 메인 세션에 인계한다. 다른 문서는 수정하지 않았다.

## 사람 검사 판정

| 절 | 제목으로 질문 식별 | 원천 대조 | 독립 답 | 다른 절·기존 문서 중복 |
|---|---|---|---|---|
| Gateway가 Running인데 Ready가 아니면 장애인가 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| Gateway 기동 검사가 계속 실패하면 무엇을 읽는가 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| Kafka 발행기를 만들지 못하면 브로커 정지인가 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| 새 이미지인데 Kafka 설정 결함이 다시 나타나는 이유 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| Kafka 메시지가 멈추면 연결·인증·적체를 어떻게 가르는가 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| DB 조회 실패와 스키마 기동 실패를 구별하기 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| V3 migration이 이미 있는 객체에서 실패하면 무엇을 대조하는가 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| Redis 장애로 Gateway가 스스로 내려가는 경우 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| Redis에 접속되는데 조정 기능 기동이 거절될 때 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| 여러 설비가 동시에 무응답이면 어느 경로를 조사하는가 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| 반복 재시작에서 메모리 부족을 확인하는 근거 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| 공간 부족 오류는 노드·이미지·DB 중 어디 문제인가 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| 화면 인증서 신뢰 실패와 만료를 구별하기 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| 연결·끊김 보고가 재실행될 때 중복 여부를 가르는 값 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |
| Business 연결 보고가 Redis 오류나 입력 충돌로 멈출 때 | 적합 | 문장별 대조 완료 | 조건·확인·조치 포함 | 진단 최소 조건만 재사용, 절차 복제 없음 |

- 전체 15절을 모두 읽었다. 정확 제목 중복·식별자를 지운 불필요한 3회 이상 제목 틀·빈 틀 문장·설정 이름 토막 뜻풀이·잘린 문장·오답 인용은 발견하지 않았다.
- 본문은 admin 범위에 두고 user 문서는 화면 조작 사실을 복제하지 않고 경로만 안내한다. 개인 자격값·현장 주소·인증서 내용은 없다.
- 최초 형식 검사에서 줄 범위 1건과 부모 길이 2건을 발견해 해당 인용·문장만 고쳤다. 이후 본문 검사 exit 0 뒤 인용 범위를 보강했으며 마지막 편집 뒤 검사 결과를 아래에 별도 기록한다.
- 소유 임시 파일·폴더는 만들지 않았다. 읽기/작성 보조 Python은 stdin으로만 실행했다. 임시 삭제 명령도 실행하지 않았다. 필수 산출물 두 파일만 남긴다.
- 독립 AI 호출 0회, 제품 시험 0회, 문서 검사기 3회. provider 사용량·비용과 세션 전체 wall time은 제공받거나 별도 실측하지 않아 미확인이다.

## 최초 작성 검사 결과

- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/platform-troubleshooting.md`
- 도구: GNU Bash 5.2.37(1)-release, Temurin OpenJDK 21.0.9+10-LTS.
- 1차 exit 1: 원천 Service 줄 범위 L11~22가 실제 20줄을 넘은 1건, 부모 길이 456·470의 2건. 실제 범위를 L11~20으로 바로잡고 두 문단의 중복 표현을 줄였다.
- 2차 exit 0: 15절·45자식, 최대 자식 176자·부모 445자, 위반 0건.
- 마지막 인용 보강 뒤 3차 exit 0: 원천 줄 범위·형식·길이·마스킹·문서 간 동일 자식 검사 통과. VIOLATION·CROSS VIOLATION·MASK 출력은 모두 0건이다.
- 원문은 15절이며 45문단의 110문장을 작성 기록 표와 대조했다. 제품 기능 시험·현장 장애 재현이나 독립 공식 검증을 수행한 결과는 아니다.
- 마지막 출력 10줄:

```text
    L188-188 문단 138  | 연결 보고 중복 방지 수정 이후 판본은 payloadRef와 처리기 식별자를 묶어 완료 기록을 확인한다. 같은 입력의 완료 기록이 남아 있으면 CHECKPOINT_SKIPPED를 남기고 발행을 건너뛴다. payloadRef가 다르면 별도 메시지다.
    L190-190 문단 153  | 현재 코드는 처리 성공 뒤에만 TTL이 있는 완료 기록을 저장한다. 운영 담당자는 재전달 전후의 payloadRef·handlerId·messageName·설비 ID와 완료 기록의 보존 여부를 대조해 Business 담당자에게 넘긴다. 원문 payload는 출력하지 않는다.
    L192-192 문단 77  | 새 연결 사건과 같은 메시지의 재전달을 구별한다. 처리 뒤 저장 전 실패나 완료 기록 만료·손실까지 중복이 없다고 보장하는 구조는 아니다.
§ L194-206 [37] Business 연결 보고가 Redis 오류나 입력 충돌로 멈출 때   parent=440 children=3 sources=4
    L202-202 문단 154  | 현재 판본의 workflow checkpoint lookup failed·workflow checkpoint record failed는 Redis 조회·기록 실패다. 기록 실패를 경고만 남기고 성공으로 끝내지 않으므로 Redis 접근 오류와 보고 처리 오류를 분리해 조사한다.
    L204-204 문단 143  | connection report checkpoint input digest conflict는 같은 식별자에 다른 입력이 들어온 경로다. 완료 기록을 지워 재실행하지 말고 payloadRef·handlerId와 충돌 로그를 Business 담당자에게 넘긴다.
    L206-206 문단 141  | Redis 저장 실패 전 발행이 이뤄졌는지는 별도로 확인한다. MES 발행 로그의 partition·offset과 재전달 기록을 대조한다. 과거 수정 커밋의 동작과 현장 판본을 같다고 단정하지 않고 이미지에 포함된 소스 판본을 인도 담당자에게 확인한다.
SUMMARY sections=15 children=45 maxChild=176 maxParent=445
CORPUS docs=35/53 sections=503 part={admin=9, user=26} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=11}
TOTAL files=35 targets=1 violations=0
```

## 최초 작성 종료·인계

대상 문서 작성과 자체 검사를 완료했다. 사용자 결정 대기 항목은 없다. 다른 문서의 linger 보완 누락 1건은 위 인계 목록에 남기고 수정하지 않았다. 다음 단계는 메인 세션의 원천 직접 대조이며 업로드·제품 수정 권한으로 확대하지 않는다.

작업 보조 Python의 첫 stdin 실행은 PowerShell 기본 전달 인코딩 때문에 한글 정규식이 깨져 읽기 단계에서 종료됐다. OutputEncoding을 UTF-8로 명시한 뒤 같은 읽기·기록 명령을 완료했다. 이 실패 때 파일 생성·변경은 없었다.

지정 작업 폴더의 work/에 만든 파일은 0개이고 검사기는 기존 도구를 실행했다. 삭제할 소유 임시 자원이나 이 세션이 시작해 남겨둔 프로세스는 없다.


## 2026-11-19 제품 수정 B 반영

- 입력 원문 SHA-256은 `01b3de61823250c969d83418c40d7902aabcebeb3ed2323b6186e88dad2b258f`, 입력 작성 기록 SHA-256은 `26e92cd963641d0f0f9a33432f4c8c9b510d604b31b722787ac937b26c0ca2cc`였다. 기존 본문과 검증 이력을 재사용하고 변경된 문장·인용·기록만 보완했다.
- 원천 상태: nori-tc-exec HEAD f1d5e0a8e727 유지, `git status --short -- create-k8s-secrets.sh`는 ` M create-k8s-secrets.sh`, exit 0이다. 작업 트리 SHA-256은 `fb6002bcd77ad3b1e33b35058906931b9cf60193d6abac3adc6d8381877aeec4`다.
- 직접 확인: 생성 스크립트 L73·L231·L328이 모두 `spring.kafka.producer.properties.linger.ms=0`이다. 파일 키 연결은 L206~213·L303~309·L416~424의 create secret 명령에서 확인했다. 이 명령은 기존 Secret을 patch하지 않는다. 스크립트를 실행하거나 운영 Secret을 조회하지 않았다.
- 변경 범위: H1 적용 판본, 「새 이미지인데 Kafka 설정 결함이 다시 나타나는 이유」의 스크립트 원천 3행과 첫 문단만 수정했다. 다른 절·문장·표·원천 줄은 보존했다.
- A~D 영향 점검: 전체 15절을 다시 읽었다. A의 소비·관리 연결 보안 속성 전달 방식과 별도 관리 health 점검의 사용 여부를 단정하는 문장은 없다. C의 HSMS·Socket 설정 적용 여부를 설명하는 문장은 없고 설비 무응답 절은 Service·TCPRoute 대조만 다룬다. D의 PLC 입력·복사 규칙과 Flyway 마지막 버전 설명도 없다. 따라서 B 외의 본문 수정은 하지 않았다. L47의 사본 시간값·발행 대기 검사는 변경되지 않았으므로 보존했다.
- 사람 검사: 변경 문단의 판본 조건·신규 생성과 기존 Secret의 차이·후속 확인 경로를 확인했다. 기존 Secret 확인·키별 보완 지침은 그대로 유효하다. 제목·독립 답·중복·뜻풀이·문장·권한 경계에서 추가 결함을 발견하지 않았다.
- 검사기 위치: 최초 지시의 Temp 경로는 현재 존재하지 않고 기존 작업 도구 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`가 존재함을 확인했다. 검사기를 수정하지 않고 이 위치의 도구를 사용한다.
- 이번 보완은 독립 AI 호출 0회, 제품 시험 0회다. provider 사용량·비용과 전체 wall time은 미측정이며 임시 파일·폴더를 만들지 않았다.

### 이번 보완의 최종 검사

- 명령: `bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/admin/platform-troubleshooting.md`
- Git 읽기에 필요한 safe.directory는 코퍼스에서 확인한 저장소의 정확한 절대경로에만 검사 프로세스 환경변수로 지정했다. 검사기·전역 Git 설정을 변경하지 않았다.
- 이번 검사기 호출 1회, exit 0. 15절·45자식, 최대 자식 189자·부모 449자, VIOLATION·CROSS VIOLATION·MASK 출력 0건이다.
- 변경 보존 검사: H1 적용 1행·원천 3행·첫 문단 1행만 역치환한 메모리 바이트의 SHA-256이 최초 원문의 `01b3de61823250c969d83418c40d7902aabcebeb3ed2323b6186e88dad2b258f`로 복원됨을 확인했다. 역치환 결과를 파일로 저장하지 않았다. 관련 없는 모든 문장·원천 줄을 보존했다.
- 작성 기록의 L60 세 문장과 해당 절의 원천·판본 정보를 갱신했고 나머지 사실 대조표 103행은 재사용했다. 원문 줄 수 206·절 수 15·사실 대조 110행은 유지된다.
- 마지막 출력 10줄:

```text
    L188-188 문단 138  | 연결 보고 중복 방지 수정 이후 판본은 payloadRef와 처리기 식별자를 묶어 완료 기록을 확인한다. 같은 입력의 완료 기록이 남아 있으면 CHECKPOINT_SKIPPED를 남기고 발행을 건너뛴다. payloadRef가 다르면 별도 메시지다.
    L190-190 문단 153  | 현재 코드는 처리 성공 뒤에만 TTL이 있는 완료 기록을 저장한다. 운영 담당자는 재전달 전후의 payloadRef·handlerId·messageName·설비 ID와 완료 기록의 보존 여부를 대조해 Business 담당자에게 넘긴다. 원문 payload는 출력하지 않는다.
    L192-192 문단 77  | 새 연결 사건과 같은 메시지의 재전달을 구별한다. 처리 뒤 저장 전 실패나 완료 기록 만료·손실까지 중복이 없다고 보장하는 구조는 아니다.
§ L194-206 [37] Business 연결 보고가 Redis 오류나 입력 충돌로 멈출 때   parent=440 children=3 sources=4
    L202-202 문단 154  | 현재 판본의 workflow checkpoint lookup failed·workflow checkpoint record failed는 Redis 조회·기록 실패다. 기록 실패를 경고만 남기고 성공으로 끝내지 않으므로 Redis 접근 오류와 보고 처리 오류를 분리해 조사한다.
    L204-204 문단 143  | connection report checkpoint input digest conflict는 같은 식별자에 다른 입력이 들어온 경로다. 완료 기록을 지워 재실행하지 말고 payloadRef·handlerId와 충돌 로그를 Business 담당자에게 넘긴다.
    L206-206 문단 141  | Redis 저장 실패 전 발행이 이뤄졌는지는 별도로 확인한다. MES 발행 로그의 partition·offset과 재전달 기록을 대조한다. 과거 수정 커밋의 동작과 현장 판본을 같다고 단정하지 않고 이미지에 포함된 소스 판본을 인도 담당자에게 확인한다.
SUMMARY sections=15 children=45 maxChild=189 maxParent=449
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

### 이번 보완 종료·인계

수정 B의 문서 반영과 자체 검사를 완료했다. 대상 문서의 남은 결함 0건, 사용자 결정 0건이다. 설치 문서 L130의 이전 생성 입력 설명은 다른 문서 소유자에게 인계하고 직접 고치지 않았다. 현장 Secret 확인·제품 시험·업로드·커밋은 실행하지 않았다. 새로 만든 임시 자원이나 남은 검사 프로세스는 없다.


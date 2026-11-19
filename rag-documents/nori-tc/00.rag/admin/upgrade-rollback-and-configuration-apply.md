# TC 업그레이드·되돌림·설정 변경 반영
```rag-meta
유형: 운영
적용: nori-tc-exec f1d5e0a8e727·nori-tc-ui-front 9fe69a75dcd3 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 버전 변경 전에 무엇을 확보하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 업그레이드 준비 L2499~2516
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 작업 권한 L294~316
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 교체 전략 L8~15
```

배포 담당자는 Kubernetes·Helm 변경 권한과 이미지 접근 권한을 확보한다. 스키마 변경이 있으면 DB 적용 권한도 준비한다. 변경 전 Pod 상태와 기동 오류를 점검하고, 새 이미지 태그·기존 설치값·되돌릴 release 번호를 기록한다.

업그레이드 전 데이터베이스 백업은 `00.rag/admin/backup-and-recovery.md`의 절차로 수행한다.

최초 설치·기동·정지·확장의 구체 명령과 설치 검증은 `00.rag/admin/installation-and-service-lifecycle.md`를 따른다.

## 이미지 태그와 되돌릴 release 번호 찾기
```rag-meta
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 이미지 태그 입력 L104~110
원천: nori-tc-ui-front/.gitlab-ci.yml @ 9fe69a75dcd3 ; 화면 이미지 태그 L8~15
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; release 이력 조회 L2479~2497
```

서버 네 앱의 CI 이미지 태그는 파이프라인 번호이고, 화면은 `짧은 커밋 해시-파이프라인 번호`이다. 배포할 때 `app.imageTag`에 해당 태그를 지정한다.

Gateway의 예로 아래 이력에서 되돌릴 번호를 고른 뒤 `REV`에 넣고 그 번호의 값을 조회한다. 다른 서비스는 release 이름을 바꾼다.

```sh
helm history tc-gateway -n tc
helm get values tc-gateway -n tc --revision "$REV"
```

## 새 패키지에 유지할 설정과 배포 입력
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설치 환경별 수정 보존 L2518~2542
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 설정 파일 묶음 생성 L204~211
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/networkpolicy.yaml @ f1d5e0a8e727 ; 통신 정책 L1~29
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/service.yaml @ f1d5e0a8e727 ; 서비스 포트 L14~20
```

설치 환경에서 수정한 부분만 새 패키지와 대조해 유지한다. Secret 생성 스크립트의 DB·Kafka·Redis·허용 출처·추가 설정, 로그 설정, Simulator 접속 대상·제어 설정을 빠뜨리지 않는다.

신뢰 저장소 파일 키와 Kafka·Redis 인증 설정도 기존 Secret에 추가한 항목인지 대조한다. Secret·인증서 교체 자체는 `00.rag/admin/security-operations.md`가 담당한다.

NetworkPolicy, Route·Service 포트, 그룹별 파티션 배정, replica·자동 확장, 자원 상한·DB 연결 풀 변경분도 보존한다. 그룹 변경분은 작업 PC 설정·생성 스크립트·Gateway와 Backend Secret을 함께 맞춘다.

## 직접 빌드하는 환경에서 다시 확인할 수정분
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 빌드 환경 수정 보존 L2528~2542
원천: nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 ; 기준 이미지 L1~5
원천: nori-tc-ui-front/deploy/docker/Dockerfile-ci @ 9fe69a75dcd3 ; 화면 기준 이미지 L1~4
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; 취약점 검사 L299~331
```

새 패키지에서 이미지를 직접 만들 때는 설치 중 바꾼 Dockerfile·CI 기준 이미지, Gradle 미러, 화면 저장소의 `.npmrc`를 다시 대조한다. 작업 PC에만 둔 설정과 저장소 사본의 수정분을 구별한다.

검토한 오탐의 억제 목록도 유지한다. 기존 환경의 수정분이 사라지면 의존성·기준 이미지 내려받기나 취약점 검사가 다시 실패할 수 있으므로 이미지 배포 전에 확인한다.

## DB 스키마 변경이 있는 업그레이드의 순서
```rag-meta
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; 외부 Flyway와 DB 입력 L51~68
원천: nori-tc-exec/config/tc-db.properties @ f1d5e0a8e727 ; 앱의 스키마 검증 L89~93
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 스키마 동반 판정·정지 L2504~2514
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 스키마 적용 전 종료 L2544~2548
```

`nori-tc-exec` 루트에서 대상 DB의 `TC_DB_URL`·`TC_DB_USER`·`TC_DB_PASSWORD`를 환경변수로 공급하고 `./gradlew flywayInfo`의 마지막 성공 버전을 읽는다. 새 `db/migration`에 더 높은 버전이 있는지 비교한다.

새 migration이 있으면 Gateway·Business·Backend의 HPA를 먼저 제거하고 Simulator를 포함한 네 앱의 replica를 0으로 내려 구 Pod가 모두 사라진 뒤 적용한다.

```sh
./gradlew flywayMigrate
./gradlew flywayInfo
```

대상 버전의 성공을 확인한 뒤 새 이미지를 배포한다. 적용 실패 시 앱 배포를 멈춘다. 새 migration이 없으면 스키마 적용을 건너뛴다. 앱은 스키마를 생성하지 않고 검증한다.

## Backend·Business·Simulator 이미지 교체
```rag-meta
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Simulator 배포 L334~350
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Business·Backend 배포 L383~422
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 업그레이드 배포 순서 L2504~2514
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 서비스별 배포 순서 L1237~1274
```

`nori-tc-exec` 루트에서 `NEW_TAG`에 새 이미지 태그를 넣는다. `a`는 `tc-ui-backend`·`tc-business`·`tc-simulator` 중 대상이다. 설치 환경 수정분을 유지한 값 파일을 쓴다. 전체 갱신 순서는 Backend·Business·Gateway·Simulator·화면이다.

```sh
a=tc-ui-backend
c=deploy/helm/$a
helm upgrade --install "$a" "$c" -n tc -f "$c/values.yaml" -f "$c/values-prod.yaml" --set app.imageTag="$NEW_TAG" --wait --timeout 300s
```

Backend·Business·Simulator는 각각의 CI 수동 배포 잡으로도 교체한다. Helm이 실패하면 다음 release로 진행하지 않고 기동 로그와 설치 검증 결과를 확인한다.

## Gateway 이미지를 교체하고 판정하기
```rag-meta
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Gateway 배포 L352~370
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 입력·성공·실패 L12~57
```

`nori-tc-exec` 루트에서 `NEW_TAG`에 새 태그를 넣고 설치 환경 수정분을 유지한 값 파일로 실행한다. Gateway에는 `--wait`를 붙이지 않는다.

```sh
c=deploy/helm/tc-gateway
helm upgrade --install tc-gateway "$c" -n tc -f "$c/values.yaml" -f "$c/values-prod.yaml" --set app.imageTag="$NEW_TAG" --timeout 300s
```

```sh
sh ./deploy/scripts/verify-gateway-rollout.sh tc
```

검증 입력 `config/tc-shard.properties`를 배포 설정과 맞추고, `gateway rollout verified.`와 종료코드 0을 확인한다. 검증 실패 시 다음 배포를 멈춘다.

## 화면 이미지를 새 버전으로 교체하기
```rag-meta
원천: nori-tc-ui-front/.gitlab-ci.yml @ 9fe69a75dcd3 ; 화면 배포 L150~167
원천: nori-tc-ui-front/deploy/helm/tc-ui-front/values.yaml @ 9fe69a75dcd3 ; 설치 환경 입력 L1~30
원천: nori-tc-ui-front/deploy/docker/Dockerfile-ci @ 9fe69a75dcd3 ; 화면 산출물 L8~16
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 서버 뒤 화면 배포 L2631~2633
```

서버 갱신 뒤 `nori-tc-ui-front` 루트에서 `NEW_TAG`에 화면 이미지 태그를 넣는다. `values.yaml`에는 기존 설치 환경의 호스트·이미지 저장소 입력을 유지한다.

```sh
c=deploy/helm/tc-ui-front
helm upgrade --install tc-ui-front "$c" -n tc -f "$c/values.yaml" --set app.imageTag="$NEW_TAG" --wait --timeout 300s
```

화면 CI의 수동 배포 잡도 같은 차트를 사용한다. 배포 후 화면 로그인과 변경한 기능을 확인한다.

## 언제 업그레이드를 중단하고 되돌림을 검토하는가
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 되돌림 기준·관찰 L2506~2516
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 기동 실패 기준 L2550~2564
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 기동 검사 L85~89
원천: nori-tc-exec/deploy/helm/tc-business/values.yaml @ f1d5e0a8e727 ; 기동 검사 L81~85
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; 기동 검사 L74~79
```

기동·실행 중 오류, 설치 검증 실패, 이번 배포로 생긴 외부 연동 단절이나 재시작 반복이 있으면 되돌림을 검토한다. 새 필수 설정이 빠져 기동하지 못하면 해당 키를 설정에 추가하고 다시 반영한다.

기본 기동 검사는 Gateway·Business가 최초 지연 10초·간격 10초·실패 한도 60회, Backend가 간격 10초·실패 한도 30회이다. 배포 명령의 300초 제한과 기동 검사 설정을 구별해 로그를 확인한다.

변경 후 설치 검증을 다시 수행하고 하루 동안 일상 점검 항목을 관찰한다. 점검 항목은 `00.rag/admin/daily-checks-and-log-inspection.md`가 소유한다.

## 스키마 변경이 없을 때 앱 되돌리기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 일반 앱 되돌림 L2566~2585
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 교체 방식 L6~11
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; Helm 외부 스키마 도구 L51~68
```

스키마 변경이 없는 배포는 변경 전 기록한 release 번호로 되돌린다. 아래는 Backend 예이며 `REV`에 이전 번호를 넣는다.

```sh
helm history tc-ui-backend -n tc
helm rollback tc-ui-backend "$REV" -n tc --wait --timeout 300s
kubectl -n tc rollout status deploy/tc-ui-backend
```

되돌림은 해당 release의 이미지와 차트 값을 이전 판본으로 돌린다. DB 스키마는 별도로 관리하므로 함께 돌아가지 않는다. 완료 후 설치 검증을 다시 수행한다.

## Gateway를 이전 release로 되돌리기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; Gateway 되돌림 L2576~2585
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 판정 입력 L12~25
원천: nori-tc-exec/deploy/helm/tc-gateway/values-prod.yaml @ f1d5e0a8e727 ; 운영 배정 L9~23
```

Gateway를 되돌릴 때 `nori-tc-exec` 루트에서 `REV`에 이전 release 번호를 넣고 대기 옵션 없이 실행한다. 검증용 그룹 설정도 되돌릴 판본의 설정과 맞춘다.

```sh
helm rollback tc-gateway "$REV" -n tc --timeout 300s
sh ./deploy/scripts/verify-gateway-rollout.sh tc
```

기본 운영 배정은 그룹 2개와 예비 1개를 위한 replica 3이다. 되돌린 차트의 replica·자동 확장이 의도한 배정과 맞는지 확인하고 설치 검증을 다시 수행한다.

## 스키마까지 바뀐 배포의 되돌림 순서
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 스키마 되돌림 L2587~2614
원천: nori-tc-exec/build.gradle.kts @ f1d5e0a8e727 ; migration 위치 L61~68
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/hpa.yaml @ f1d5e0a8e727 ; 자동 확장 생성 L1~21
```

스키마 변경이 있으면 앱만 되돌리기 전에 이전 앱이 사용할 스키마를 복구한다.

1. 영향 앱의 자동 확장을 먼저 해제하고 replica를 0으로 내려 Pod 종료를 확인한다.
2. DB 담당자가 새 보상 migration을 적용하거나 백업 복원을 수행한다.
3. 이전 release 번호로 앱을 되돌리고 `kubectl -n tc get hpa`로 자동 확장 복원 여부를 확인한다.
4. 설치 검증을 다시 수행한다.

보상은 기존 V 파일을 수정하지 않고 `db/migration/V<다음 번호>__<설명>.sql`을 추가해 적용한다. V1은 고치지 않는다. 긴급 추가한 번호는 다음 패키지와 겹치지 않게 인도 담당자에게 알린다.

되돌림 뒤 현재 패키지의 새 이미지 태그로 배포하면 이전 판본이 다시 덮인다. 되돌릴 때는 선택한 이전 release 번호를 기준으로 작업한다.

## 조정 기능을 처음 켜는 판본으로 전환할 때
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 조정 기능 전환 L2616~2644
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 판본 혼용 금지 L8~15
원천: nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 ; 발급 절차 L63~92
```

조정 기능 전환은 구·신 규약의 Pod가 섞이지 않게 중단 시간에 수행한다. UI 변경 요청을 중단하고 진행 중 조정 작업이 0인지 확인한다. Redis 재기동 후에도 조정 자료가 보존되는 구성과 DB 백업을 먼저 준비한다.

네 서버 앱의 구 Pod를 모두 종료한 뒤 스키마를 적용하고, 조정 초기 값 발급을 마친 다음 새 서버 앱들을 올린다. 서버 준비 뒤 화면을 배포하고 설치 검증을 수행한다.

진행 중 작업 조회·초기 값 발급·소유권 확인 방법은 `00.rag/admin/coordination-ownership-and-recovery-check.md`에서 다룬다.

## 조정 기능 전환을 되돌릴 때 맞출 대상
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 조정 기능 되돌림 L2646~2652
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 기능 스위치 L137~141
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; 기능 스위치 L130~134
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 기능 스위치 L122~125
```

조정 기능 전환을 취소할 때 Gateway·Business·Backend 세 release와 화면을 함께 맞춘다. 한 서버만 기능을 끄거나 화면만 이전 판본으로 돌리지 않는다.

현재 앱 판본에서 조정 기능을 끄는 조치는 이전 이미지로 되돌리는 조치와 구별한다. 기능을 끌 때는 세 서버의 Helm 배포에 `--set coordination.enabled=false`를 함께 적용한다.

이전 이미지로 되돌려야 한다면 스키마 변경 여부를 먼저 판정하고 각 서비스의 이전 release 번호로 되돌린다. 기능 스위치 변경만으로 이미지와 스키마가 이전 판본이 되지는 않는다.

## ConfigMap과 Secret 중 어떤 설정을 바꾸는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 볼륨 L172~179
원천: nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 볼륨 L166~173
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 볼륨 L161~168
원천: nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 ; Secret 볼륨 L106~113
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 설정 Secret 지정 L91~100
원천: nori-tc-exec/deploy/helm/tc-business/values.yaml @ f1d5e0a8e727 ; 설정 Secret 지정 L87~95
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; 설정 Secret 지정 L81~91
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; 설정 Secret 지정 L527~537
```

제공된 네 서버 앱 차트는 `configSecret.name`의 Secret을 `/app/config`에 읽기 전용으로 연결한다. 앱 파일 설정을 공급하는 ConfigMap 연결은 정의되어 있지 않다.

설정 변경은 대상 앱의 Secret에서 해당 파일 키를 고친다. 기본 리소스는 `tc-gateway-config`·`tc-business-config`·`tc-ui-backend-config`·`tc-simulator-config`이다.

Helm의 자원·환경변수·이미지 입력을 바꾸려면 기존 설치값을 보존한 값 파일로 해당 release를 다시 배포한다. 설정 항목별 영향은 `00.rag/admin/configuration-reference.md`가 담당한다.

## 운영 Secret의 파일 키 하나만 갱신하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 키별 설정 변경 L2654~2683
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 신규 Secret 생성 호출 L204~211
```

Secret 변경 권한이 있는 담당자가 대상 파일 키만 꺼내 편집하고 병합한다. 변경 전 내용도 안전하게 보존한다. Gateway의 `tc-redis.properties`를 바꿀 때는 접근을 제한한 작업 폴더를 `TC_WORK`에 지정하고 아래처럼 파일로 받는다.

```sh
kubectl -n tc get secret tc-gateway-config -o go-template='{{index .data "tc-redis.properties"}}' | base64 -d > "$TC_WORK/tc-redis.properties"
```

운영 변경에 신규 설치용 `create-k8s-secrets.sh`를 재실행하지 않는다. 이 스크립트는 정해진 파일 묶음으로 Secret을 생성하는 명령이며 기존 Secret의 한 키를 수정하는 도구가 아니다.

## 편집한 설정 파일을 노출하지 않고 병합하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 파일 기반 patch L2669~2683
```

Gateway 설정 변경 담당자는 접근을 제한한 `TC_WORK`의 `tc-redis.properties`를 편집한 뒤 patch 파일을 만든다. base64 문자열도 자격을 담을 수 있으므로 값을 명령 인자나 기록에 붙이지 않는다.

```sh
printf '{"data":{"tc-redis.properties":"%s"}}\n' "$(base64 -w0 "$TC_WORK/tc-redis.properties")" > "$TC_WORK/patch.json"
```

```sh
kubectl -n tc patch secret tc-gateway-config --type=merge --patch-file "$TC_WORK/patch.json"
```

변경한 키 외의 설정은 유지한다. 편집 파일과 patch 파일은 작업 중에만 보관하고 작업 후 삭제한다. 같은 설정이 신규 설치 때도 유지되도록 Secret 생성 입력에도 변경을 반영한다.

## Secret 변경을 실행 중인 앱에 반영하기
```rag-meta
원천: nori-tc-exec/apps/tc-gateway-app/src/main/resources/application.yaml @ f1d5e0a8e727 ; 기동 설정 import L15~26
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 설정 재반영 L2685~2695
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 전체 교체 L8~15
```

Secret 파일 변경 뒤에는 그 설정을 읽는 앱을 재기동한다. 앱은 기동 때 외부 설정을 읽는다. Gateway 예는 `nori-tc-exec` 루트에서 실행한다.

```sh
kubectl -n tc rollout restart deploy/tc-gateway
sh ./deploy/scripts/verify-gateway-rollout.sh tc
```

다른 서버 앱은 대상 Deployment를 재기동하고 `kubectl -n tc rollout status deploy/<대상 앱>`으로 완료를 확인한다. 네 앱은 기존 Pod를 내린 뒤 새 Pod를 만드는 방식이므로 중단 시간을 확보한다.

반영 뒤에는 기동 오류·준비 상태와 변경한 기능을 확인한다. 설정만 잘못 바꾼 경우 이전 파일 내용으로 해당 키를 다시 병합하고 같은 앱을 재기동해 검증한다.

## Helm 되돌림으로 운영 Secret도 복원되는가
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/values.yaml @ f1d5e0a8e727 ; 외부 Secret 기본값 L91~100
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/secret-config.yaml @ f1d5e0a8e727 ; 조건부 Secret 생성 L1~17
원천: nori-tc-exec/deploy/helm/tc-business/values.yaml @ f1d5e0a8e727 ; 외부 Secret 기본값 L87~95
원천: nori-tc-exec/deploy/helm/tc-ui-backend/values.yaml @ f1d5e0a8e727 ; 외부 Secret 기본값 L81~91
원천: nori-tc-exec/deploy/helm/tc-simulator/values.yaml @ f1d5e0a8e727 ; 외부 Secret 기본값 L527~537
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 키별 변경과 재기동 L2654~2695
```

제공된 기본 차트 값은 `configSecret.create=false`라 외부에서 만든 Secret을 참조한다. 외부 Secret에 직접 병합한 설정은 Helm release 이력에 포함되지 않는다.

이미지를 되돌릴 때 이전 앱이 요구하는 파일 키와 값도 맞춘다. 설정 변경 전 보존한 내용으로 필요한 키를 별도로 복원한 뒤 재기동한다. Helm 값으로 Secret 생성을 켜서 관리한 설치는 해당 release의 Secret 정의도 함께 대조한다.

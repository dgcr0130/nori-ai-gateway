# 장애 문의 때 넘길 근거·인계
```rag-meta
유형: 운영
적용: nori-tc-exec f1d5e0a8e727·nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-common 6fe71f6b2da6 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/admin/daily-checks-and-log-inspection.md
```

## 장애 문의에 공통으로 묶을 내용
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 대응 절차의 구조 L2278~2302
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 대상 오류·시각 L229~317
```

문의를 받는 운영 담당자는 발생 시각·영향받은 설비와 서비스·화면에서 한 조작·표시된 오류 문구를 한 묶음으로 정리한다. 화면을 확인한 시각도 함께 적어 장애 발생 시각과 구별한다.

화면 캡처와 로그를 넘기기 전에 비밀번호·토큰·인증서 내용·개인 연락처를 제거한다. 설정 문제는 Secret·ConfigMap의 리소스명과 키 이름까지만 적고 비밀값은 넘기지 않는다.

## DLQ 문의에서 실패 항목을 특정할 값
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 실패 열 L14~36
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 실패·메시지 위치 열 L15~41
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열 문구 L1066~1078
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; DLQ 조회 L81~88
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; Business 조회 L129~136
```

처리실패 문의를 받은 담당자는 「Gateway DLQ」인지 「Business DLQ」인지 먼저 구별한다. 해당 행의 「ID」·「설비」·「발생 시각」·「단계」·「사유 코드」·「사유」를 묶는다.

Gateway 항목에는 「IF」·「소켓」을, Business 항목에는 「출처」·「토픽」·「파티션」·「오프셋」을 덧붙인다. 원문 조사가 필요하면 표시된 「payload 참조」를 함께 남긴다.

화면 조회 방법은 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 확인한다.

## 격리와 Repair Task 문의를 구별하는 근거
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 격리 열 L14~30
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 복구 작업 열 L14~28
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열 문구 L1071~1084
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 격리·복구 작업 조회 L177~209
```

「격리 설비」 문의에는 설비 ID와 「격리 시각」·「사유 코드」·「사유」·「TTL」 표시값을 묶는다.

「Repair Task」 문의에는 「Task ID」와 「설비」·「커맨드」·「액션」·「에러 코드」·「에러 메시지」·「생성 시각」을 묶는다. 설비가 같아도 격리 기록과 복구 작업 기록은 구별해 전달한다.

두 목록을 찾는 경로는 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 확인한다.

## 배포나 메시지 전송 실패의 대상과 단계 남기기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 대상별 결과 L229~317
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 배포 번호·표 L329~375
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 상세 조회 오류 L463~479
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상별 열 문구 L291~301
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 상세·대상 조회 L315~348
```

「Deploy View」에서 배포 번호와 실패한 「EQP ID」를 짝지어 기록한다. 같은 행의 「작업」·「결과」·「상세 상태」·「현재 Step」·「Step 상태」·「오류 요약」·「갱신 시각」을 함께 넘긴다.

상세를 조회하지 못하면 배포 번호·조회 시각과 화면의 오류 문구를 남긴다. 상세 확인 경로는 `00.rag/user/deployment-workflow.md`, 메시지 전송 결과를 찾는 경로는 `00.rag/user/message-results-and-communication-logs.md`에서 확인한다.

## 통신 문의에서 같은 메시지를 찾게 하는 값
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 대상·빈 목록 L115~148
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 통신 상세 표시 L151~201
원천: nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 ; 통신 기록 필드·조회 L6~34
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; 통신 조회 L35~45
```

통신 문의를 받은 담당자는 「통신 로그」 상세의 eqpId·time·epoch와 위쪽의 방향·출처·메시지명을 함께 기록한다. epoch는 상세에 표시된 값을 그대로 옮긴다.

기록이 보이지 않는 문의에는 통신이 있었던 시각과 조회 시각·대상 설비·화면 문구를 남긴다. 목록과 상세를 확인하는 방법은 `00.rag/user/message-results-and-communication-logs.md`에서 다룬다.

## 서버 로그와 Pod 상태를 넘길 때 붙일 조회 위치
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 공통 장애 확인 L2281~2302
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; namespace·앱 라벨 L3~23
원천: nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 ; 배포 로그 패턴 L33~45
원천: nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 ; 로그 패턴 소비 L16~40
원천: nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 ; 실제 검사·결과 L16~57
```

Pod·로그 조회 권한이 있는 운영 담당자는 namespace·release·Pod와 조회 기간을 적는다. 파일 로그를 넘기면 파일 경로도 붙인다. ERROR·WARN 원문과 앞뒤 기록, 확보한 traceId·로그 시각을 함께 남긴다.

Pod 상태·재시작 증가와 종료 사유도 기록한다. Gateway 검증을 실행했다면 명령·종료코드·출력을 첨부한다. 조회·검증 실패도 오류 원문으로 남긴다.

로그 위치와 조회 명령은 `00.rag/admin/daily-checks-and-log-inspection.md`에서 확인한다.

## 새 Pod 기동 실패나 계속되는 서버 오류를 인계하기
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 기동 실패·앱 오류 L2312~2321
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 태그 L44~46
원천: nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 ; Gateway 배포 입력 L364~370
```

새 Pod가 기동하지 못하면 운영 담당자는 대상 서비스·Pod와 기동 로그의 첫 오류를 묶는다. 배포와 관련된 문의에는 적용한 이미지 태그와 변경 시각도 기록한다.

기동 뒤 서버 오류가 계속되면 오류가 난 서비스의 로그를 넘긴다. 되돌림을 수행한 경우에는 되돌린 판본·시각과 이후 같은 증상이 남는지도 함께 전달한다. 변경·되돌림 절차는 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`에서 확인한다.

## 대응을 마친 뒤 인계 기록에 남길 다섯 항목
```rag-meta
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 대응 후 기록 L2278~2279
```

대응을 마친 운영 담당자는 발생 시각·영향 범위·근본 원인·복구 조치·재발 방지를 기록한다. 근본 원인은 확인한 근거와 연결하고, 아직 확인하지 못한 원인은 미확인으로 구별한다.

실행한 조치와 복구 확인 결과를 구별해 남긴다. 후속 조사로 넘기는 경우에는 남은 증상과 확보한 화면·로그의 위치를 함께 전달한다.

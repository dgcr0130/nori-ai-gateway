# 배포 대기·취소 대상 문제 확인
```rag-meta
유형: 트러블슈팅
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/deployment-workflow.md
```

## 배포가 대기하는지 상세 조회가 막혔는지 구별하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 조회·오류 L98~124 L229~317 L463~479
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 상세·대상 API L774~817
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 진행 현황 문구 L291~301
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 상세·대상 조회 L315~348
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; API 인가 L160~185
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 조회 권한 L88~94
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 조회 권한 시드 L780~780
```

배포가 끝나지 않으면 「Deploy View」의 「대상별 진행 현황」에서 같은 「EQP ID」의 「현재 Step」·「Step 상태」·「갱신 시각」을 조회 시각과 함께 기록해 이전 관측과 비교한다.

상세 조회 오류가 함께 나오면 오류 문구와 조회 시각을 먼저 운영 담당자에게 넘긴다. 기본 권한 데이터상 상세·대상 조회에는 DEPLOY_READ가 필요하므로 권한 거부는 배포 단계 대기와 구별한다.

화면을 열고 다시 조회하는 절차는 `00.rag/user/deployment-workflow.md`에서 확인한다.

## 응답 추적 등록이 사라지면 언제 대기를 끝내는가
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/registry/DeployTraceRedisAdapter.java @ 985d17c604dd ; 등록·응답 구별 L127~163
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 대기 판정·기한 저장 L137~198 L246~257
원천: nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/DeployApplyDriverTest.java @ 985d17c604dd ; 기한 전후 기대값 L199~224
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/registry/DeployTraceExpiryLookupIT.java @ 985d17c604dd ; 등록 만료·늦은 응답 L61~90
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/DeployTargetPageResponse.java @ 985d17c604dd ; 대상 응답 항목 L20~30
```

12005ecf 수정이 포함된 판본에서 배포 작업을 묶어 처리하는 경로는 응답 추적 등록이 살아 있는 대기와 등록이 사라진 경우를 구별한다. 등록이 사라져도 저장된 단계 기한 전에는 대기하며, 기한에 도달하면 STEP_TIMEOUT으로 실패를 판정한다. 기한 값이 없으면 계속 대기한다.

「Deploy View」에는 단계 기한이 표시되지 않는다. 같은 단계가 계속 남으면 운영 담당자에게 배포 번호·설비 ID·현재 단계·관측 시각을 넘기고, 저장된 단계 기한과 응답 추적 등록, 현장 판본의 수정 포함 여부를 대조해 달라고 요청한다.

등록 만료 뒤 도착한 응답만으로는 사라진 추적 등록이 복구되지 않는다.

## 응답을 못 받은 경우와 응답 조회가 실패한 경우 구별하기
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd ; 조회·수신 실패 분기 L163~215
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/registry/DeployTraceRedisAdapter.java @ 985d17c604dd ; 대기·조회 오류 L143~163
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 대상 오류·요약 결과 L295~303 L401~411
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표시 항목 L300~306
```

배포 작업을 묶어 처리하는 경로에서 DEPLOY_TRACE_UNAVAILABLE은 응답 저장소 조회 실패로 판정한 경우다. 등록이 살아 있으나 필요한 응답이 덜 모인 대기와 구별한다. 실패 응답이 도착하면 수신된 오류 코드·문구를 사용하며, 각각 값이 없을 때 STEP_FAILED와 대체 문구를 사용한다.

사용자는 「오류 요약」과 「요약」의 「결과 코드」·결과 문구 중 표시된 내용을 기록해 담당자에게 전달한다. 응답 조회 실패인지 수신된 실패인지 대조할 수 있도록 배포 번호·설비 ID·현재 단계·발생 시각을 함께 넘긴다.

코드별 뜻의 전체 목록은 `00.rag/user/error-message-and-code-reference.md`, 상태값 뜻은 `00.rag/user/state-reference.md`가 다룬다.

## NO_CHANGE가 섞인 배포의 취소 대상 확인하기
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 취소·접수 입력 L590~599 L616~633
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/DeployOperationCoordinator.java @ 985d17c604dd ; 동일 그룹·참여 대상 L97~137 L156~184 L272~291
원천: nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/coordination/DeployCommitBoundaryCancelTest.java @ 985d17c604dd ; 변경 없는 대상·공유 모델 회귀 L93~145
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 설비·상세 상태 L229~265
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 상태 문구 L297~297
```

취소가 의도한 대상에 적용되지 않으면 「대상별 진행 현황」의 「상세 상태」에서 NO_CHANGE인 설비와 실행 중인 설비를 나누어 기록한다.

c336bd8b 수정이 포함된 판본에서 배포 작업을 묶어 처리하는 경로는 변경 없는 대상을 실행 접수와 취소 양쪽의 작업 묶음에서 제외한다. 변경 없는 대상이 같은 모델 버전을 공유해도 취소할 작업의 식별이 달라지지 않도록 처리한다.

취소를 처리한 운영 담당자에게 배포 번호와 두 종류의 설비 ID, 취소 시각·결과를 넘긴다. 현장 판본의 수정 포함 여부와 실행 때 접수한 작업·취소 때 찾은 작업이 같은지 확인해 달라고 요청한다.

## 취소 거부 응답만으로 적용 확정을 알 수 있는가
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 취소 검사·거부 응답 L457~478 L585~599
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/DeployOperationCoordinator.java @ 985d17c604dd ; 취소 결과 분류 L120~137
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 중단 거부 조건 L620~650
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 행 메뉴 L934~1013
```

작업을 묶어 처리하는 배포의 취소가 거부되면 실제 적용이 확정된 경우와 취소할 작업을 찾지 못했거나 작업이 이미 끝난 경우를 담당자가 구별해야 한다. 서버는 중단에 실패한 작업 ID도 committedOperationIds에 담아 거부 응답을 만들므로 문구만으로 원인을 확정하지 않는다.

취소를 처리한 담당자에게 응답의 deployKey·committedOperationIds·전체 거부 문구와 요청 시각을 보관해 달라고 요청한다. 화면의 대상별 결과와 대조해 적용 완료 여부와 후속 조치를 확인한다.

취소 요청 경로와 「Hold」 조작은 `00.rag/user/deployment-workflow.md`에서 확인한다.

# 메시지 전송 결과·통신 로그 조회
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/deployment-workflow.md
```

## 메시지 전송 기록을 배포 목록에서 찾기
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.14 메시지 전송 결과 조회 L1102~1130
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 배포 메뉴 L114~124
원천: nori-tc-ui-front/src/features/deploy/components/DeployPage.tsx @ 9fe69a75dcd3 ; 카드별 화면 L13~53
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; Event Type·조회 L307~321
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 조회 입력·단추 L712~783
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; getDeployHistory L740~771
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; getHistory L135~161
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; 카드별 상태 조건 L300~348
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; DEPLOY_READ L87~95
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 배포 조회 문구 L172~181
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 단추·배포 카드 L148~155
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 배포 메뉴 L1102~1102
```

메시지 전송을 새로 요청하는 전용 화면 단추는 없다. 이미 생성된 메시지 전송 결과를 배포 목록에서 찾는다. 저장소 기본 권한 데이터 기준으로 목록·상세 조회에는 DEPLOY_READ가 필요하다.

1. 좌측 메뉴의 「배포」를 열고, 진행 중인 전송은 「배포」 카드, 끝난 전송은 「배포 이력」 카드에서 찾는다.
2. 「배포 조회」 또는 「배포 이력 조회」의 「시작 기간」·「종료 기간」을 정하고 「조회」를 누른다. 기간 입력은 선택 사항이며 생성일을 기준으로 조회한다.
3. 표의 「Event Type」이 EQP_SEND_MESSAGE인 줄을 찾는다.

## 메시지 전송 작업의 대상별 결과 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; View 행 메뉴 L934~1002
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 상세 조회·작업명 L98~159
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 대상별 결과 L229~317
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 배포 번호·대상 표시 L329~375
원천: nori-tc-ui-front/src/features/deploy/lib/deploy-utils.ts @ 9fe69a75dcd3 ; resolveDeployTaskLabel L65~102
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 상세·대상 API L774~805
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; getSummary·getTargets L315~348
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상별 열 L291~301
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메시지 전송 L333~333
```

1. EQP_SEND_MESSAGE 줄을 마우스 오른쪽 단추로 누르고 「View」를 고른다.
2. 「Deploy View」의 「대상별 진행 현황」에서 「EQP ID」로 설비를 찾는다. 「작업」의 굵은 글씨가 「메시지 전송」인지 확인한다.
3. 같은 설비 행의 「결과」·「상세 상태」·「오류 요약」을 읽는다. 단계까지 확인해야 하면 「현재 Step」·「Step 상태」·「갱신 시각」도 함께 확인한다.

메시지 전송 실패를 문의할 때는 창 위쪽의 배포 번호, 대상 설비 ID, 오류 요약과 갱신 시각을 운영 담당자에게 전달한다. 결과 상태와 공통 상세 항목 설명은 「배포 등록·실행·이력·재실행」에서 확인한다.

## 메시지 전송 기록이 없거나 조회가 거부됐을 때
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 조회 오류 L254~256
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 빈 목록 L912~918
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 상세 오류·대상 없음 L119~124
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 대상 없음 L357~364
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 오류·닫기 L463~479
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 기간 검사 L145~158
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 서버 문구 우선 L11~12
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록 L190~192
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류 L270~271
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대상 없음 L294~294
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 배포 조회 기본 권한 L87~95
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
```

메시지 전송 줄이 없으면 기간을 넓히고 모델·사용자 조건을 풀어 「배포」와 「배포 이력」 양쪽에서 조회한다. 「표시할 배포 데이터가 없습니다.」·「완료된 배포 이력이 없습니다.」는 해당 목록이 빈 경우의 표시다.

종료 기간이 시작 기간보다 빠르면 서버가 거부한다. 권한 거부 문구이면 운영 관리자에게 DEPLOY_READ 부여 여부를 확인한다.

「Deploy 실패 상세를 조회하지 못했습니다.」이면 「닫기」 뒤 같은 메시지 전송 줄의 「View」를 다시 연다. 서버 오류 문구가 있으면 그 문구를 우선 표시한다. 「확인 가능한 대상 정보가 없습니다.」이면 다른 전송 기록을 고른다.

## 통신 로그의 조회 범위와 자동 갱신 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 목록·진입 L115~148
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 최근 알림·더보기 L211~294
원천: nori-tc-ui-front/src/features/live-map/hooks/useCommLog.ts @ 9fe69a75dcd3 ; 자동 조회 L4~11
원천: nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 ; getLogs L20~34
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; getCommLogs L35~45
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/CommLogQueryService.java @ 985d17c604dd ; getAllLogs L50~55
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java @ 985d17c604dd ; 기본 조회 범위 L46~47
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java @ 985d17c604dd ; 범위 적용 L67~73
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; COMM_LOG_READ L42~50
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 통신 로그 표시 문구 L396~423
```

Live-Map의 「선택 설비」 아래 「최근 알림」에서 「더보기」로 「통신 로그」 목록을 여는 경로는 「Live-Map 상태 보기」에서 안내한다.

열린 목록은 선택 설비의 설비 통신·업무 통신 기록을 함께 보여 준다. 저장소 기본 권한 데이터 기준으로 조회에는 COMM_LOG_READ가 필요하다.

기본 설정의 조회 범위는 최근 1시간이며 서버 설정에 따라 달라질 수 있다. 이 화면에는 조회 기간이나 출처를 바꾸는 입력 칸이 없다. 로그는 10초 간격으로 다시 조회한다.

## 통신 로그 목록에서 시각과 메시지 구별하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; formatTime·ClickableLogRow L33~84
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 목록 자동 스크롤·건수 L103~145
원천: nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 ; 출처 코드 L6~12
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/CommLogQueryService.java @ 985d17c604dd ; 시간순 병합 L50~55
원천: nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/port/CommLogPort.java @ d3636ab86763 ; 통신 방향 L11~24
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/messaging/CommLogPort.java @ 7db24f7466bd ; 통신 방향 L11~24
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 건수 단위 L422~422
```

「통신 로그」 제목 옆에서 대상 설비 ID와 건수를 확인한다. 기록은 과거부터 최신 순서이며 목록을 열거나 건수가 바뀌면 맨 아래 최신 기록으로 이동한다.

각 행은 시:분:초.밀리초, 통신 방향, 출처 코드, 메시지명, 내용 일부 순서다. RECV는 수신, SEND는 송신이다. 출처 코드는 EQP 또는 MES로 표시된다. 메시지명이 없으면 `-`를 표시한다.

메시지명이나 내용이 잘려 보이면 그 행을 눌러 상세를 연다. 오래된 기록을 보려면 목록을 위로 스크롤한다.

## 통신 로그 한 건의 전체 내용 읽기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; formatPayload L42~50
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; CommLogDetailModal L151~207
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 최근 알림에서 상세 L260~294
원천: nori-tc-ui-front/src/components/ui/dialog.tsx @ 9fe69a75dcd3 ; 닫기 단추 L79~85
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 내용 없음 L423~423
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 닫기 L473~473
```

통신 로그 목록의 행이나 「최근 알림」의 개별 기록을 누르면 한 건의 상세가 열린다. 상세 위쪽에는 방향·출처·메시지명이 나오고, 아래에는 eqpId·time·epoch가 표시된다.

본문이 JSON이면 들여쓰기를 넣어 표시하고, JSON이 아니면 원문을 표시한다. 내용이 비어 있으면 「(payload 없음)」이 나온다. 긴 내용은 상세 안을 스크롤해 읽는다.

상세 오른쪽 위 X 모양의 「닫기」로 상세를 닫는다. 통신 로그 문의에는 설비 ID·time·epoch·방향·출처·메시지명과 화면에서 확인한 증상을 함께 전달한다.

## 통신 로그 목록이 비어 있을 때 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 빈 목록 L129~145
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 조회 상태 사용 L211~215
원천: nori-tc-ui-front/src/features/live-map/hooks/useCommLog.ts @ 9fe69a75dcd3 ; 재조회 간격 L4~11
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/comm/CommLogRedisQueryAdapter.java @ 985d17c604dd ; 빈 결과·실패 처리 L67~97
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_05_07_add_comm_log_api_permission.sql @ f1d5e0a8e727 ; 조회 권한 L26~39
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 로그 문구 L420~420
```

통신 로그 목록에 「최근 1시간 내 로그가 없습니다.」가 나오더라도 기록 부재와 조회 실패를 이 화면에서 구별할 수는 없다. 목록에는 오류 전용 안내나 「재시도」 단추가 없으며, 다음 자동 조회에서도 빈 목록이 계속될 수 있다.

기록이 예상되는데 목록이 계속 비면 운영 관리자에게 COMM_LOG_READ 부여 여부를 확인하고, 운영 담당자에게 대상 설비 ID·통신이 있었던 시각·조회 시각·표시 문구를 전달한다.

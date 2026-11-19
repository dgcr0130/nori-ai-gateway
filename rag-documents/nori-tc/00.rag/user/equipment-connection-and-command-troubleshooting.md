# 설비 연결·명령 문제 확인
```rag-meta
유형: 트러블슈팅
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/equipment-commands-and-polling.md
```

## 연결되지 않는 설비에서 먼저 확인할 값
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.2 설비 연결 실패 확인 순서 L2535~2548
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 10.5 설비 무응답 L2393~2404
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인별 대상 L151~159 L239~243
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 통신 정보 열 L111~145
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; ACTIVE 명령 제한 L44~60
```

상단바의 라인이 대상 설비의 라인인지 먼저 확인한다. 「설비」에서 대상 설비를 고른 뒤 「설비 정보」의 「Comm Interface」·「Comm Mode」·「IP」·「Port」를 등록하려던 값과 대조한다.

ACTIVE이면 접속 대상인 「IP」·「Port」를 확인한다. PASSIVE에는 시작 메뉴가 없으므로 운영 담당자에게 설비 ID·「Comm Mode」·「Port」를 전달해 접속 대기 상태를 확인한다.

등록값 수정은 `00.rag/user/equipment-registration-and-settings.md`, 시작 조작은 `00.rag/user/equipment-commands-and-polling.md`를 따른다.

## 주의 상태인데 시작 명령이 보이지 않을 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 색 점·행 메뉴 L49~97 L233~279
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; 상태·명령 판정 L21~74
원천: nori-tc-ui-front/tests/unit/eqp-runtime-state.test.ts @ 9fe69a75dcd3 ; 주의·명령 기대값 L29~80
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 상태 조회 L408~424
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 상태 응답 L234~250
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 시작·재연결 조건 L375~393 L704~714
```

「Enabled」의 노란색 점만으로 연결 끊김을 확정하지 않는다. 시작 의도는 유지되지만 연결이 끊긴 경우와, 연결 중이어도 설비 상태가 RUN·IDLE이 아닌 경우가 모두 노란색이다.

ACTIVE 설비의 「설비 정보」 행 메뉴에서 「Eqp Reconnect」가 보이면 시작 의도가 유지된 채 끊긴 경우다. 「Eqp Start」를 다시 보내는 대신 재연결 경로를 사용한다. 「Eqp Restart」가 보이면 화면이 연결 상태로 판정한 경우다.

메뉴를 연 뒤 상태가 달라져 명령이 거부되면 화면을 새로고침한다. 계속 불일치하면 설비 ID·색 점·보이는 메뉴·거부 문구·시각을 운영 담당자에게 전달한다.

## 연결 표시가 있는데 메시지 응답이 없을 때
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.2 최근 메시지 확인 L2546~2548
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 10.5 영향 범위 L2393~2407
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 시각·방향·최근 알림 L53~83 L211~294
원천: nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 ; 조회 API L20~34
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; 로그 조회 L35~45
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그 문구 L396~420
```

Live-Map에서 대상 설비를 고르고 「최근 알림」의 마지막 메시지와 시각을 확인한다. 「더보기」로 「통신 로그」를 열어 송수신 방향과 메시지를 비교한다. 연결 표시와 메시지 응답 여부를 나누어 기록한다.

한 대만 무응답인지, 여러 설비가 같은 시각부터 무응답인지 함께 기록해 운영 담당자에게 전달한다. 설비별 증상과 공통 영향 범위를 나누는 단서로 사용한다.

로그 조회·권한은 `00.rag/user/message-results-and-communication-logs.md`를 따른다. SECS·Socket 진단은 「Comm Interface」에 맞춰 `00.rag/user/secs-hsms-troubleshooting.md` 또는 `00.rag/user/socket-troubleshooting.md`에서 확인한다.

## 명령 뒤 실패 창이 뜨면 연결 실패인가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 실패·조회 오류 처리 L301~347 L360~378
원천: nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; 조회 오류·지연 구분 L80~84 L124~142
원천: nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 작업 조회 L19~50
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 결과 응답 L69~107
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 명령·조회 기본 권한 L15~23 L123~140
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 권한 시드 L764~768
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 실패 문구 L1107~1108
```

실패 창에는 명령 결과의 실패뿐 아니라 요청·결과 조회 오류도 표시된다. 본문이 「작업 상태를 조회하지 못했습니다.」나 「요청한 작업을 찾을 수 없습니다.」이면 조회 문제로 구별해 기록한다.

권한 거부이면 저장소 기본 권한 데이터 기준으로 명령의 EQP_WRITE와 결과 조회의 ASYNC_READ를 운영 관리자에게 확인한다. 명령 실행과 결과 읽기의 권한은 별도로 검사된다.

조회 오류 뒤 같은 명령을 반복하기 전에 운영 담당자에게 설비 ID·명령·시각·오류 문구·표시된 「작업 ID」를 전달해 실제 적용 결과를 확인한다.

## 확인 지연과 운영 확인 필요에서 다음 조치가 다른 이유
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 결과 분기·재조회 L277~297 L360~378
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 다시 조회 조건 L132~140
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 지연·복구 안내 L28~40
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·복구 문구 L1110~1120
```

「확인 지연」은 결과가 아직 확인되지 않은 경우이므로 새 명령보다 같은 작업의 결과 확인을 먼저 한다. 「작업 ID」가 있으면 결과 창의 「다시 조회」를 사용한다.

「운영 확인 필요」이면 자동 복구가 끝나지 않은 중단이다. 결과 창의 사유와 「작업 ID」를 보존해 운영 담당자에게 상태 확인을 요청한다. 이 결과에는 「다시 조회」 단추가 표시되지 않는다.

재시작의 종료·시작 중 어느 단계가 지연됐는지에 따른 확인은 `00.rag/user/equipment-commands-and-polling.md`를 따른다. 명령 창의 결과와 설비의 현재 연결 표시를 함께 전달한다.

## 새로고침 뒤 진행 중 명령을 다시 찾을 수 있는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 접수 저장·삭제·복원 L160~164 L187~196 L260~263 L390~409
원천: nori-tc-ui-front/src/shared/lib/pending-operation-store.ts @ 9fe69a75dcd3 ; 저장소 접근·기록 L27~84
원천: nori-tc-ui-front/tests/unit/pending-operation-store.test.ts @ 9fe69a75dcd3 ; 기록 복원·접근 실패 기대값 L40~98
```

프런트엔드 e390bbec 수정이 포함된 판본은 시작·종료·재시작·재연결의 접수 정보를 브라우저에 저장한다. 저장된 기록이 있으면 설비 화면을 다시 열 때 「확인 지연」 결과 창으로 복원하며 「다시 조회」로 결과를 읽는다.

결과 창을 닫거나 최종 결과를 확인하면 복원 기록을 지운다. 브라우저 저장소 접근 실패나 깨진 기록은 복원되지 않는다. 새로고침하거나 창을 닫기 전에 설비 ID·명령·시각·표시된 「작업 ID」를 기록한다.

새로고침 뒤 창이 돌아오지 않으면 기록한 정보를 운영 담당자에게 전달한다. 현장 프런트엔드 판본에 재조회 복원 수정이 포함됐는지도 확인을 요청한다.

## Gateway JAR reload 뒤 연결이 돌아오지 않을 때
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 갱신·복구 L768~824 L838~885
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeExecutor.java @ d3636ab86763 ; 통신 재시작·복구 L113~142
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlServiceTest.java @ d3636ab86763 ; reload 성공 뒤 재시작 실패 기대값 L1142~1211
```

Gateway 1c14e091 수정이 포함된 판본은 시작 의도가 유지된 설비의 통신을 JAR 갱신 중 멈춘 뒤 재시작한다. reload가 성공했어도 통신 재시작이 실패하면 복구 경로에서 재시작을 한 번 더 시도한다.

이 재시도 뒤에도 원래 갱신 오류는 실패로 전달된다. 연결이 돌아온 것과 JAR 갱신 작업의 성공을 따로 확인한다. JAR 갱신 실패만으로 연결도 계속 끊겨 있다고 판단하지 않는다.

연결이 돌아오지 않으면 설비 ID·갱신 시각·갱신 결과·현재 색 점과 메뉴를 운영 담당자에게 전달한다. 현장 Gateway 판본의 수정 포함 여부와 reload·통신 재시작 중 실패한 단계를 확인해 달라고 요청한다.

## 연결 문제와 함께 격리·복구 작업 기록이 있을 때
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.2 격리·복구 작업 확인 L2544~2547
원천: nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; 격리·복구 카드 L83~109
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 카드 문구 L127~128
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 격리·복구 조회 L48~58
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 격리·복구 목록 L177~209
```

연결 문제가 계속되면 「처리실패」의 「격리 설비」와 「Repair Task」에서 같은 설비의 기록이 있는지 확인한다. 조회 권한과 목록 확인은 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다.

대상 기록이 있으면 설비 ID·해당 기록의 사유·시각을 명령 결과와 함께 운영 담당자에게 전달한다. 연결 상태, 명령 결과, 격리·복구 기록을 함께 대조할 수 있게 한다.

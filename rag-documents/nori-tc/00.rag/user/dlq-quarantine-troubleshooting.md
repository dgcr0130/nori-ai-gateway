# DLQ·격리 조회와 처리 문제 점검
```rag-meta
유형: 트러블슈팅
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/dlq-quarantine-and-repair-task.md
```

## 목록 조회 오류와 행에 기록된 실패 구별하기
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·빈 목록 분기 L115~137
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 사유 열 L21~29
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 사유 열 L27~34
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 격리 사유 열 L17~28
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 복구 작업 오류 열 L17~28
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 재시도·열 문구 L132~136 L1071~1083
```

「처리실패」에서 표 대신 오류 문구와 「재시도」가 나타나면 목록 조회 문제로 구별한다. 행 안의 「사유」나 「에러 메시지」가 보이면 조회된 기록의 처리 실패 내용을 확인한다.

범주와 오류가 나온 위치를 먼저 기록한다. 목록 조회 오류를 원래 메시지 처리의 실패 사유로 옮겨 적지 않는다. 행을 읽을 수 있으면 기록 확인·처리 요청 절차는 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다.

## 재시도 뒤에도 같은 조회 오류가 남을 때
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 오류 선택·재조회 L64~82
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 오류 선택·재조회 L69~87
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 오류 선택·재조회 L62~80
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 오류 선택·재조회 L60~78
원천: nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; 라인·설비 오류 L14~48
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 오류 설명 선택 L14~32
원천: nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류 문구·재시도 L127~132
```

범주 목록의 오류 영역에는 해당 목록 조회뿐 아니라 라인·설비 조회 오류도 표시한다. 범주별 기본 문구 대신 다른 오류 설명이 나올 수 있으므로 화면 문구 전체를 확인한다.

「재시도」와 목록 위 「새로고침」은 해당 범주만 다시 조회한다. 라인·설비 조회를 함께 다시 실행하지 않으므로, 같은 오류가 남으면 목록 재조회만 반복하지 않는다.

운영 담당자에게 범주·선택한 라인·조회 시각·문구와 재시도 후 결과를 전달하고, 범주 목록과 라인·설비 조회 중 실패한 요청을 구별해 달라고 요청한다.

## 조회가 403으로 거부되면 어느 권한을 확인하는가
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 네 범주 조회 L35~58
원천: nori-tc-ui-front/src/shared/lib/api-client.ts @ 9fe69a75dcd3 ; API 기본 경로 L14~21
원천: nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; 라인 조회 L22~29
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 설비 조회 L149~162
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 보호 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 기본 차단·권한 검사 L142~174 L253~258
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 조회 권한 L114~158
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 기본 권한 시드 L764~770
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 조회 권한 L14~23
```

403이 확인되면 운영 관리자와 실패한 GET 요청을 아래 기본 권한 데이터에 대조한다. 현장에 실제 부여된 권한은 별도로 확인한다.

| 조회 경로 | 기본 권한 조건 |
|---|---|
| /api/dlq 아래 네 범주 | DLQ_READ |
| /api/line | LINE_READ |
| /api/eqp | EQP_READ·EQP_WRITE 중 하나 |

서버는 권한 목록 초기화 실패·권한 정보 불일치·경로 미등록 때도 차단한다. 403만으로 사용자 권한 누락을 확정하지 않는다. 범주·실패 경로·시각·문구를 넘기고, 로그인·권한 공통 점검은 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

## 카드에는 건수가 있는데 검색한 행이 없을 때
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; 카드 건수 L47~101
원천: nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 카드용 라인 필터 L41~57
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 검색·빈 목록 L49~69
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 검색·빈 목록 L54~74
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 검색·빈 목록 L43~67
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 검색·빈 목록 L41~65
원천: nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 ; 선택 라인 필터 L22~38
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·검색 문구 L133~135
```

카드 건수에는 목록의 「키워드 검색」을 적용하지 않는다. 카드가 0보다 큰데 「검색 결과가 없습니다.」가 나오면 검색어를 비워 같은 범주의 행이 나타나는지 확인한다.

검색어 없이 「표시할 항목이 없습니다.」가 나오면 상단에서 선택한 라인도 확인한다. 선택한 라인을 해석하지 못한 경우에도 목록이 비게 된다. 선택·검색 조건을 정리한 뒤 「새로고침」으로 비교한다.

계속 비어 있으면 운영 담당자에게 범주·라인·검색어 유무·카드 건수·목록 결과를 함께 전달한다. 카드와 목록 사용 방법은 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 확인한다.

## 오류 없이 DLQ·격리 기록이 계속 보이지 않을 때
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; 대시보드 표시 분기 L150~171
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 성공 응답의 비배열 처리 L14~27
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; 항목 건너뛰기·빈 반환 L95~128 L166~197 L213~233
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd ; 항목 건너뛰기·빈 반환 L86~119 L154~174
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/config/UiRedisConfiguration.java @ 985d17c604dd ; 수정 경위·조회 형식 L206~248
원천: nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/dlq/DlqRedisTemplateSerializationModeTest.java @ 985d17c604dd ; 저장 형식 조회 기대값 L82~141
```

기록이 예상되는데 「최근 장애 없음」만 보이면 범주 목록을 열어 조회 오류부터 구별한다. 대시보드는 조회 오류를 별도로 표시하지 않는다.

필터를 확인해도 「Gateway DLQ」·「Business DLQ」·「격리 설비」가 비면 운영 담당자에게 저장된 기록과 조회 결과의 대조를 요청한다. 현재 코드도 일부 조회 실패를 빈 목록이나 해당 항목 생략으로 처리하므로, 화면만으로 기록 부재를 확정할 수 없다.

1d0e4b2b 수정 이후 판본은 저장 자료를 읽는 형식 불일치를 고쳤다. 운영 담당자에게 백엔드 배포 판본의 수정 포함 여부도 확인을 요청한다. 확인한 기준 소스에는 수정이 포함되어 있다.

## 격리 해제·재처리 단추를 찾을 수 없을 때
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~108
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L76~113
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L69~108
원천: nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L67~104
원천: nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 전용 호출 L30~59
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; 삭제·격리·복구 조회 L100~114 L148~209
```

목록은 읽히는데 정리·재처리·격리 해제·복구 실행을 할 수 없다면, 먼저 확인한 판본의 네 목록이 조회용이라는 점을 구별한다. 처리 단추가 없는 현상을 조회 권한 거부와 같은 문제로 판단하지 않는다.

조치가 필요하면 `00.rag/user/dlq-quarantine-and-repair-task.md`의 해당 범주 절차로 운영 담당자에게 요청한다. 별도 화면에서 조작이 거부된 경우에는 그 화면·누른 단추·대상·응답 문구를 함께 전달해 목록 조회 문제와 나누어 확인한다.

## TTL이 줄지 않거나 격리 표시가 남을 때
```rag-meta
원천: nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; TTL 표시·재조회 L25~30 L78~80
원천: nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; 유효기간 저장 L62~85
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사유·격리 시각 문구 L1071~1079
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 격리 조치 요청 L2181~2192
```

「격리 설비」의 「TTL」이 그대로여도 시간 표시 정지를 뜻하지 않는다. 표시값은 저장 때의 유효기간이며 남은 시간을 계속 계산하는 값이 아니다.

격리 표시가 남으면 「새로고침」 뒤 같은 설비의 「격리 시각」·「사유」가 이전과 같은지 확인한다. 새 격리 기록인지, 기존 기록이 남은 것인지 운영 담당자가 대조하도록 요청한다.

해제·복구 요청과 만료 기한이 없는 표시의 확인은 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다. 표시 유무와 실제 복구 결과는 담당자에게 각각 확인한다.

# DLQ·격리 작성 기록

## 대상

- objectKey: `00.rag/user/dlq-quarantine-troubleshooting.md`
- 구획·권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/dlq-quarantine-troubleshooting.md`
- 검색 원문 SHA-256: `5fc939d526cbead7e85d5a9a03f4a8056168d63ca3ec1fc2b5445c65e061e9c2`
- 검색 원문 줄 수: 124
- 절 수: 7
- 확인일: 2026-11-19 한국 시간
- 작업 범위: 위 검색 원문과 이 작성 기록만 신규 작성한다. 제품 코드·기존 문서 변경, 위임, 커밋, 업로드, 네트워크 작업을 수행하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 결과 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 아래 조회 범위에서 수정·미추적 경로 출력 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 아래 조회 범위에서 수정·미추적 경로 출력 없음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 아래 조회 범위에서 수정·미추적 경로 출력 없음, exit 0 |
| nori-tc-gateway | d3636ab86763 | RedisQuarantineStore.java 경로 출력 없음, exit 0 |

판본은 각 저장소에서 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C ../<저장소> rev-parse --short=12 HEAD`로 읽었다. 소유자 검사 예외는 읽기 명령 한 번에만 지정했으며 전역 설정을 바꾸지 않았다.

쓰기 전 같은 명령 접두사로 `status --short -- <경로들>`을 실행한 범위는 다음과 같다.

- front: `src/features/dlq`, `src/i18n/locales/ko.json`, `src/shared/lib/api-client.ts`, `src/shared/hooks/useLineList.ts`, `src/shared/api/line.api.ts`, `src/features/eqp/hooks/useEqpList.ts`, `src/features/eqp/api/eqp.api.ts`, `tests/unit/dlq-line-scope.test.ts`.
- backend: `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java`, 같은 web-adapter의 `security` 디렉터리, redis-adapter의 `src/main/java/com/nori/tc/ui/adapters/redis/dlq`, `config/UiRedisConfiguration.java`, `repair/RedisEqpRepairTaskStore.java`, `src/test/java/com/nori/tc/ui/adapters/redis/dlq`, web-adapter의 `DlqControllerSecurityTest.java`·`DlqControllerRepairTaskTest.java`.
- exec: `docs/completion-report/07-user-manual.md`, `docs/scripts/manual-permission-catalog.json`, `docs/db_table/sample_data/postgres_insert_sample_data.sql`, `docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql`.
- gateway: `gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java`.

status 조회에는 사용자 전역 ignore 파일 접근 거부 경고가 있었으나, 각 명령은 exit 0이고 위 제품 원천의 변경 항목은 출력되지 않았다. 원천은 commit 판본으로 표기했다. 원천 파일을 수정하지 않았다.

backend의 `git ... show --stat 1d0e4b2b`, 수정된 Java 코드 diff, 현재 설정·서비스·회귀 시험을 읽었다. `git ... merge-base --is-ancestor 1d0e4b2b HEAD`는 exit 0이었다. 커밋 메시지의 과거 운영 실측과 build 성공 주장을 이번 세션의 실측으로 기록하지 않았다.

`docs/rag-document-writing-rules.md`와 `docs/tc-rag-document-list.md` 전체를 읽었다. U §8 L2043~2256의 현황·두 DLQ·격리·Repair Task 범위를 확인했다. 기존 사용 절차 문서를 전부 읽고, 공통 오류 점검·인계 문서 및 겹치는 문서의 해당 절을 대조했다.

## 절별 사실 대조

각 행은 최종 검색 원문의 본문 문장 또는 표 자료 행을 대조한다. 권고 문장은 해당 코드가 제공하는 관측값과 실패 분기를 근거로 한 확인 순서이며 실행 성공 보장이 아니다.

### 목록 조회 오류와 행에 기록된 실패 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 20 첫 문장 | 표 대신 오류와 재시도이면 조회 문제로 구별 | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 L115~137; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L132~136 | isError 분기에 오류 문자열·재시도 단추를 렌더한다. 빈 목록 분기와 다르다. |
| 20 둘째 문장 | 행의 사유·에러 메시지는 기록 내용 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L21~29; nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L27~34; nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L17~28; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 L17~28; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1071~1083 | 사유·오류 열은 행 자료의 accessor이며 목록 조회 오류는 별도 영역이다. |
| 22 첫째·둘째 문장 | 범주·오류 위치를 기록해 조회 오류와 처리 실패를 분리 | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 L127~137; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 L21~28 | 화면에서 서로 다른 오류의 위치를 구별할 수 있다. 이를 인계 확인 순서로 제시했다. |
| 22 셋째 문장 | 행의 확인·처리 요청 절차는 소유 문서 안내 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L14~37; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L81~88 L129~136 L177~209 | 기존 `00.rag/user/dlq-quarantine-and-repair-task.md`의 각 목록·조치 요청 절과 대조해 해당 절차를 복제하지 않았다. |

### 재시도 뒤에도 같은 조회 오류가 남을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 35 첫 문장 | 목록 또는 라인·설비 조회 오류가 같은 영역에 표시 | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 L14~48; nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L64~79 | 범주 query.isError와 lineScope.isError를 합친다. query.error가 Error이면 우선하며 아니면 라인·설비 errorMessage를 쓴다. 다른 세 패널도 같은 연결을 확인했다. |
| 35 둘째 문장 | 기본 실패 문구 외의 설명도 그대로 확인 | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L14~32; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 L127~132 | 실패 envelope의 errorMsg가 있으면 Error.message에 쓰며 shell은 전달된 오류 설명을 렌더한다. HTTP 실패 본문의 모든 문구가 언제나 렌더된다고 확장하지 않았다. |
| 37 두 문장 | 재시도·새로고침은 범주만 재조회 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L80~82; nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L85~87; nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L78~80; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 L76~78; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 L101~109 L130~132 | 두 단추는 동일 onRefresh를 쓰고 각 패널은 해당 query.refetch만 호출한다. lineScope에는 refetch를 호출하지 않는다. |
| 39 | 범주·라인·시각·문구·재시도 결과를 이용해 실패 요청을 구별 | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 L14~48; nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L64~82 | 별도 query의 오류가 합쳐지는 근거에 맞춘 사용자 인계 권고다. 새로운 인계 양식을 만들지 않았다. |

### 조회가 403으로 거부되면 어느 권한을 확인하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 54 두 문장 | 실패 GET 경로와 기본 권한을 대조하고 현장 부여와 구별 | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L30~58; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L114~158 | 네 범주 API는 GET이다. 저장소 기본 목록은 현장 DB·사용자의 실제 권한 증거가 아니다. |
| 58 | /api/dlq 아래 네 범주에 DLQ_READ | nori-tc-ui-front/src/shared/lib/api-client.ts @ 9fe69a75dcd3 L14~21; nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L35~58; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L114~122; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L769~770 | 네 경로는 /api/dlq 접두사 아래이며 GET PREFIX 권한이 DLQ_READ다. DELETE 권한과 섞지 않았다. |
| 59 | /api/line에 LINE_READ | nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 L22~29; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L150~158; nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L14~23 | /api/line GET EXACT 기본 연결과 시드를 함께 확인했다. |
| 60 | /api/eqp에 EQP_READ 또는 EQP_WRITE | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L149~162; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L123~140; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L764~767; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L172~174 L253~258 | 한 개 이상의 매칭 권한으로 허용하며 EQP_WRITE의 메서드 null은 모든 HTTP 메서드에 매칭된다. |
| 62 첫째·둘째 문장 | 초기화 실패·권한 정보 불일치·경로 미등록 차단, 403만으로 개인 권한 누락 단정 금지 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd L142~143; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | initializationFailed·catalogGuard.inSync 실패·matching.isEmpty가 모두 거부로 이어진다. |
| 62 셋째 문장 | 실패 경로·시각·문구 인계와 공통 점검 안내 | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L35~58; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | `00.rag/user/login-permission-and-screen-troubleshooting.md`의 API 403 절이 소유하는 상세 원인 점검은 반복하지 않았다. |

### 카드에는 건수가 있는데 검색한 행이 없을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 76 첫 문장 | 카드 건수는 키워드 검색을 반영하지 않음 | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 L47~101; nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 L41~57 | 카드의 entries 길이는 라인 필터 결과이며 개별 패널의 검색어 상태를 받지 않는다. |
| 76 둘째 문장 | 카드 양수·검색 결과 없음이면 검색어를 비워 대조 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L49~69; nori-tc-ui-front/src/features/dlq/lib/dlq-format.ts @ 9fe69a75dcd3 L14~26; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L133~135 | 비어 있지 않은 검색어가 있을 때 searchNoResult를 선택한다. 검색어가 공백이면 필터가 원본 목록을 돌려준다. 다른 세 패널의 동일 분기도 읽었다. |
| 78 세 문장 | 검색 없는 빈 목록에서는 라인·선택 해석을 확인하고 재조회 | nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 L22~38; nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 L19~35; nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L68~82 | selectedLineKey가 null이면 []이다. 선택·검색 조건을 정리한 뒤 재조회하는 비교 순서를 제시했으며 자동 복구를 보장하지 않았다. |
| 80 두 문장 | 카드·목록 비교값 인계와 절차 소유 문서 안내 | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 L47~101; nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L49~96 | 카드와 패널이 다른 필터를 적용하는 근거에 맞추어 인계할 관측값을 정했다. 검색 사용 절차는 기존 사용 절차 문서에 남겼다. |

### 오류 없이 DLQ·격리 기록이 계속 보이지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 92 두 문장 | 최근 장애 없음에서 범주 목록 오류를 확인 | nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 L101~174; nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 L64~80 | 오류 상태를 집계하지만 DashboardPanel은 isError를 렌더 분기에 쓰지 않는다. 범주 패널에는 오류 분기가 있다. |
| 94 첫 문장 | 필터 이후에도 비면 저장 자료와 조회 결과 대조 요청 | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd L95~128 L166~197; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd L86~119 | 조회된 항목만 화면으로 반환되므로 저장 자료와 결과의 대조는 운영 담당자에게 요청하도록 했다. 내부 접속 방법은 싣지 않았다. |
| 94 둘째 문장 | 현재 코드도 일부 실패를 빈 반환·항목 생략으로 처리 | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd L120~128 L190~197 L230~233; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd L111~119 L171~174; nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L23~24 | SCAN의 DataAccessException은 []로, 개별 항목 변환 실패는 건너뛰기로 처리한다. 프런트도 success=true의 비배열 data를 []로 처리한다. 모든 실패가 반드시 같은 결과라는 주장은 하지 않았다. |
| 96 첫 문장 | 1d0e4b2b 이후 저장·읽기 형식 불일치 수정 | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/config/UiRedisConfiguration.java @ 985d17c604dd L206~248; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd L73~80; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd L64~71; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/dlq/DlqRedisTemplateSerializationModeTest.java @ 985d17c604dd L82~141 | 해당 커밋 diff의 공용 typeless 템플릿 신설·두 서비스 주입 변경과 현재 코드를 대조했다. 시험은 운영 직렬화기로 저장 형식의 바이트를 Map으로 읽고 두 DLQ 목록에 포함하는 기대값을 가진다. 시험 실행은 하지 않았다. |
| 96 둘째·셋째 문장 | 현장 수정 포함은 문의, 기준 HEAD에는 포함 | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/config/UiRedisConfiguration.java @ 985d17c604dd L229~248 | 현재 코드와 `merge-base --is-ancestor 1d0e4b2b HEAD` exit 0을 대조했다. 현장 배포 판본을 확인했다는 주장은 하지 않았다. |

### 격리 해제·재처리 단추를 찾을 수 없을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 108 두 문장 | 조회 가능한 목록에서 처리 단추 부재와 권한 거부를 분리 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L71~108; nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L76~113; nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L69~108; nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 L67~104; nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L30~59 | 네 화면은 조회·검색·쪽 이동만 연결하고 처리 단추는 렌더하지 않는다. 서버 DLQ 삭제 API 존재와 화면 제공 기능을 혼동하지 않았다. |
| 110 첫 문장 | 조치 요청은 해당 소유 절차 안내 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L100~114 L148~209 | 기존 사용 절차의 정리·격리·Repair Task 인계 절과 대조했다. HTTP 호출·운영 내부 조작을 새 절차로 작성하지 않았다. |
| 110 둘째 문장 | 별도 화면의 처리 거부는 실제 화면·조작·대상·응답으로 인계 | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L30~59; nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 L88~110 | 확인한 목록이 처리 요청을 보내지 않는다는 근거로 실패 위치를 가르는 권고다. 별도 화면에서 어떤 거부가 발생한다거나 격리 때문에 거부된다고 단정하지 않았다. |

### TTL이 줄지 않거나 격리 표시가 남을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 120 두 문장 | 격리 TTL은 저장 유효기간이며 잔여시간 계수가 아님 | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 L62~85; nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L25~30 | 저장 시 같은 ttlSeconds를 메타데이터와 만료 Duration에 넣고, 화면은 row.ttlSeconds를 문자열로 표시한다. 경과시간 차감이 없다. |
| 122 두 문장 | 갱신 뒤 격리 시각·사유를 이전 관측과 비교해 새 기록과 잔여 기록 구별 요청 | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L17~30 L78~80; nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 L69~82 | 같은 설비 키에 격리 시각과 사유를 넣어 저장한다. 갱신된 관측값의 비교를 권고했으며, 같은 값이면 반드시 같은 기록이라고 확정하지 않았다. |
| 124 두 문장 | 해제 요청·기한 없는 표시 절차 안내, 실제 복구와 표시를 별도로 확인 | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 L65~82; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L2181~2192; nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L78~80 | 양수가 아닌 유효기간은 null 메타데이터와 만료 없는 저장으로 분기한다. 격리 표시 조회는 복구 실행 결과를 제공하지 않는다. 기존 사용 절차 문서로 상세를 넘겼다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 현재 코드 | 검색 원문 반영 |
|---|---|---|---|
| 대시보드 조회 오류 표시 | U §8.1.4 L2088~2091은 「목록을 조회하지 못했습니다.」를 현황 화면의 오류로 설명한다. | DashboardPanel.tsx @ 9fe69a75dcd3 L150~171은 대기·빈 피드·표만 구별하며 entries.isError를 표시하지 않는다. | L92에서 범주 목록을 열어 오류를 구별하도록 했다. |
| 빈 목록을 정상 상태로 판정 | U §8.2.4 L2133~2135와 §8.4.4 L2215~2218은 「표시할 항목이 없습니다.」를 항목 없음·정상 상태로 연결한다. | GatewayDlqRedisService.java @ 985d17c604dd L120~128 L190~197 L230~233과 BusinessDlqRedisService.java L111~119 L171~174는 일부 실패를 빈 반환·항목 생략으로 처리한다. 라인·검색 필터도 적용된다. | L76~96에서 필터·조회 실패·수정 적용 판본을 가르는 경로로 작성했다. |
| TTL의 뜻 | U §8 L2049~2051·§8.2 L2125·§8.3 L2168·§8.4 L2209는 남은 초로 설명한다. | RedisQuarantineStore.java @ d3636ab86763 L65~82는 저장 때 유효기간을 메타데이터로 넣고 QuarantinePanel.tsx @ 9fe69a75dcd3 L30은 그 값을 그대로 표시한다. | L120에서 격리 표시의 유효기간과 남은 시간 계수를 구별했다. DLQ TTL 전반의 설명은 기존 사용 절차 문서가 소유하므로 복제하지 않았다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 백엔드에 1d0e4b2b 수정이 적용됐다는 확정 | 로컬 현재 HEAD와 포함 관계만 확인했다. 현장 배포 정보·서버에 접근하지 않았다. | 배포·운영 담당자 |
| 특정 현장 빈 목록의 실제 원인과 현재 저장 건수 | 소스의 실패 분기만으로 현장 필터·저장 상태·접근 실패 중 어느 원인인지 판정할 수 없다. | 운영 담당자 |
| 현장 사용자의 실제 권한 부여·서버 권한 정보 상태 | 기본 카탈로그·SQL 시드와 인가 코드만 확인했다. | 운영 관리자 |
| 격리 표시 만료만으로 메시지 처리·Live-Map 갱신까지 자동 복구된다는 보장 | U L2211의 일반화와 화면 조회만으로 실제 복구 완료를 입증하지 못했다. 이 문서는 조회·조작 문제의 구별만 소유한다. | 설비·Gateway 운영 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에 복제하지 않은 내용 |
|---|---|
| 00.rag/user/dlq-quarantine-and-repair-task.md | 카드 열기·검색·쪽 이동 전체 절차, 열 전체 목록, 사유 코드별 의미, TTL 일반 참조, 삭제·재처리·격리 해제·Repair Task 인계 절차, 네 범주 기본 실패 문구 표. 본문에는 진단에 필요한 최소 조건과 해당 문서 경로만 사용했다. |
| 00.rag/admin/incident-evidence-handover.md | 운영 담당자가 수집하는 범주별 전체 증거 묶음·서버 로그·Pod 정보·연락 절차. user 원문에는 admin objectKey와 관리 문서 열람 지시를 넣지 않았다. |
| 00.rag/user/login-permission-and-screen-troubleshooting.md | 인증 만료·로그인 복귀·서버 인가 실패의 일반 원인과 상세 점검. 이 문서는 실패한 목록·라인·설비 경로별 최소 권한 대조만 포함했다. |
| 00.rag/user/permission-code-reference.md | 권한 코드별 뜻·메뉴·계정 관리 전반. 이 문서에서는 조회 거부를 구별하는 기본 조건만 사용했다. |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 설비 명령 무응답·연결 실패·격리·복구 기록을 명령 결과와 대조하는 진단. |
| 00.rag/user/plc-troubleshooting.md | PLC 폴링 만료 누적·재연결·격리 요청 조건 및 프로토콜 실패별 진단. |
| 00.rag/user/model-flow-troubleshooting.md | Business 처리·계산·Collect 실패와 메시지 순서 분석. |
| 00.rag/user/tc-components-and-terms.md | DLQ·payload·격리와 Repair Task의 개념 정의. |
| 00.rag/user/login-and-screen-basics.md | 공통 표 표시·재시도·화면 이동 절차. |

겹치는 문서는 소유 범위를 읽기만 했고 수정하지 않았다. 다른 문서의 확정 결함으로 보고할 항목은 없다.

## 검사 결과

검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/dlq-quarantine-troubleshooting.md
```

PowerShell에서 Git Bash 실행 파일 `C:/Program Files/Git/bin/bash.exe`를 지정해 실행했다. 검사기를 수정하거나 우회하지 않았다. 도구는 GNU bash 5.2.37(1)-release, Temurin OpenJDK 21.0.9+10-LTS다. 첫 실행과 마지막 원문 편집 후 최종 재실행 모두 exit 0, 위반 0건이었다. 첫 실행 이후 권한 검사 근거 줄 범위를 보완하고 격리 절의 문구 원천을 명시했다. 본문·줄 수는 유지했다.

마지막 출력 10줄:

```text
§ L98-110 [23] 격리 해제·재처리 단추를 찾을 수 없을 때   parent=261 children=2 sources=6
    L108-108 문단 111  | 목록은 읽히는데 정리·재처리·격리 해제·복구 실행을 할 수 없다면, 먼저 확인한 판본의 네 목록이 조회용이라는 점을 구별한다. 처리 단추가 없는 현상을 조회 권한 거부와 같은 문제로 판단하지 않는다.
    L110-110 문단 149  | 조치가 필요하면 00.rag/user/dlq-quarantine-and-repair-task.md의 해당 범주 절차로 운영 담당자에게 요청한다. 별도 화면에서 조작이 거부된 경우에는 그 화면·누른 단추·대상·응답 문구를 함께 전달해 목록 조회 문제와 나누어 확인한다.
§ L112-124 [23] TTL이 줄지 않거나 격리 표시가 남을 때   parent=293 children=3 sources=4
    L120-120 문단 81  | 「격리 설비」의 「TTL」이 그대로여도 시간 표시 정지를 뜻하지 않는다. 표시값은 저장 때의 유효기간이며 남은 시간을 계속 계산하는 값이 아니다.
    L122-122 문단 99  | 격리 표시가 남으면 「새로고침」 뒤 같은 설비의 「격리 시각」·「사유」가 이전과 같은지 확인한다. 새 격리 기록인지, 기존 기록이 남은 것인지 운영 담당자가 대조하도록 요청한다.
    L124-124 문단 111  | 해제·복구 요청과 만료 기한이 없는 표시의 확인은 00.rag/user/dlq-quarantine-and-repair-task.md를 따른다. 표시 유무와 실제 복구 결과는 담당자에게 각각 확인한다.
SUMMARY sections=7 children=21 maxChild=170 maxParent=375
CORPUS docs=34/53 sections=488 part={admin=8, user=26} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=10}
TOTAL files=34 targets=1 violations=0
```

위 블록은 최종 재실행에서 회수한 검사기 출력의 마지막 10줄이다. 대상 검사는 제목 1~60자·자식 1~200자·부모 1~450자, rag-meta·원천 위치·표 칸·금지 구조·mask-v1·문서 간 동일 자식 본문을 포함한다. MASK·CROSS VIOLATION 출력은 없었다. 검색 원문의 바이트를 별도로 읽어 BOM=false·CR=false·NUL=false·NFC=true, 124줄·7절을 확인했다. 첫 검사와 최종 검사의 전체 자료 수는 다른 문서 추가에 따라 달랐으며, 이 세션은 대상 한 문서만 썼다.

사람 검사 대상은 7절 전부다.

| 절 | 제목으로 질문 식별 | 원천과 일치 | 독립 답 | 중복·권한 경계 판정 |
|---|---|---|---|---|
| 목록 조회 오류와 행에 기록된 실패 구별하기 | 통과: 표시 위치로 두 실패를 구별 | 통과 | 통과: 조회 오류와 기록 내용의 다음 확인 경로 제시 | 절차·문구 표 복제 없음 |
| 재시도 뒤에도 같은 조회 오류가 남을 때 | 통과: 재조회 범위가 핵심 | 통과 | 통과: 실패한 세 종류의 요청을 구별해 인계 | 라인·설비 오류의 재조회 한계만 소유 |
| 조회가 403으로 거부되면 어느 권한을 확인하는가 | 통과: 실패 경로에 따른 확인 | 통과 | 통과: 기본 데이터와 현장 부여를 분리 | 코드 전체 뜻풀이·관리 조작 없음 |
| 카드에는 건수가 있는데 검색한 행이 없을 때 | 통과: 관측 불일치로 시작 | 통과 | 통과: 검색·라인·재조회 비교와 인계 | 사용 절차 전체 반복 없음 |
| 오류 없이 DLQ·격리 기록이 계속 보이지 않을 때 | 통과: 오류 없는 빈 목록 증상 | 통과 | 통과: 필터 이후 판본·조회 대조 요청 | 과거 결함을 현재 확정 원인으로 쓰지 않음 |
| 격리 해제·재처리 단추를 찾을 수 없을 때 | 통과: 조작 불가와 조회 거부 구별 | 통과 | 통과: 화면 제공 기능과 인계 경로 제시 | 직접 API 조작·admin 안내 없음 |
| TTL이 줄지 않거나 격리 표시가 남을 때 | 통과: 고정 수치·남은 표시 증상 | 통과 | 통과: 시각·사유 비교와 복구 결과 확인 | 진단에 필요한 TTL 최소 사실만 사용 |

전수 문장 검사에서 제목 정확 중복·불필요한 3회 이상 반복 제목 틀·같은 자식 본문·정보 없는 틀 문장·이름 토막 뜻풀이·조사 접합 오류·잘린 문장·오답 인용·미해소 마스킹·관리 전용 조치 0건으로 판정했다. 부모별 조건·조치가 문맥 헤더와 함께 읽히는지 확인했다. 동일 주제의 최소 조건은 진단 목적에 필요한 것만 재사용하고, 사용 절차·인계 값의 전체 목록을 다시 쓰지 않았다.

제품 시험·브라우저 실행·현장 재현은 수행하지 않았다. 관련 시험은 기대값을 읽었으며 이 기록의 검사 통과는 제품 시험이나 메인 세션의 독립 검증 통과를 의미하지 않는다. 독립 Agent 호출은 0회다. provider 사용량·비용·전체 작업 wall time은 미측정이다.

검사용 임시 파일·상주 프로세스·컨테이너를 만들지 않았다. 두 차례 검사 프로세스는 각각 종료코드 0을 회수했다. 두 유효한 결과 파일은 보존한다. 업로드·git add·commit·push를 수행하지 않았다.

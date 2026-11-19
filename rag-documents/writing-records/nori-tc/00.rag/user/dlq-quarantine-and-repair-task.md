# DLQ·격리 설비·Repair Task 작성 기록

## 대상

- objectKey: `00.rag/user/dlq-quarantine-and-repair-task.md`
- 구획: user, 필요한 문서 열람 권한 `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문: `rag-documents/nori-tc/00.rag/user/dlq-quarantine-and-repair-task.md`
- 검색 원문 SHA-256: `5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d`
- 검색 원문 줄 수: 312
- 검색 원문 절 수: 19
- 확인일: 2026-11-18, 한국 시간
- 작성: 이 독립 세션에서 직접 조사·작성·자체검사했다. 다른 Agent 호출은 0회다.
- 실행 범위: 검색 원문과 이 작성 기록 두 파일만 생성했다. 제품 실행·시험·업로드·커밋·서버 접속은 하지 않았다.
- 기존 상태: 제품 저장소는 main, HEAD `70cf6cd84db6`, origin/main보다 8개 커밋 앞선 상태이며 기존 `rag-documents/`는 untracked였다. git worktree list는 이 저장소 작업 경로 한 곳을 표시했다. 기존 문서는 수정하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 원천 파일 상태 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 인용한 14개 파일 모두 status 출력 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 인용한 10개 파일 모두 status 출력 없음, exit 0 |
| nori-tc-gateway | d3636ab86763 | 인용한 9개 파일 모두 status 출력 없음, exit 0 |
| nori-tc-business | 7db24f7466bd | 인용한 6개 파일 모두 status 출력 없음, exit 0 |
| nori-tc-common | 6fe71f6b2da6 | 인용한 2개 파일 모두 status 출력 없음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 인용한 4개 파일 모두 status 출력 없음, exit 0 |
| nori-common | ae6791e99e91 | 인용한 4개 파일 모두 status 출력 없음, exit 0 |

작성 전에 해당 디렉터리들의 상태를 확인했고, 보완 최종 검색 원문의 메타에서 추출한 정확한 인용 경로 49개도 저장소별로 묶어 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C ../<저장소> status --short -- <원천 경로들>`로 다시 확인했다. 기존 판본을 보존했고 새 원천 nori-common의 HEAD는 `rev-parse --short=12 HEAD`로 확인했다.

첫 형제 저장소 Git 조회는 소유자 차이로 거부됐다. 이후 읽기 명령 한정 `-c safe.directory=<정확한 저장소 절대경로>`를 지정했다. 전역 Git 설정을 변경하지 않았다. 각 status는 exit 0과 변경 출력 없음을 확인했지만, 사용자 전역 ignore 파일에 대한 Permission denied 경고가 있었다. 미추적 파일 전부에 대한 보증으로 확대하지 않고 인용한 추적 파일의 수정 상태 확인으로만 사용했다.

작성 규칙 `docs/rag-document-writing-rules.md`와 소유 범위 목록 `docs/tc-rag-document-list.md`는 전체를 읽었다. 두 파일의 최종 status 출력도 없었다. 앞선 검색 원문 중 `login-and-screen-basics.md` 전체와, DLQ·격리·Repair 검색으로 겹치는 `deployment-workflow.md`의 배포 결과 안내를 읽었다.

## 절별 사실 대조

아래 표는 메타를 제외한 검색 원문의 모든 문장과 표 자료 행을 대조한다. 같은 줄에 여러 문장이 있으면 문장별로 행을 나눴다. 인계 권고는 해당 화면에서 실제 확보 가능한 값과 조작에 한정했다.

### 처리실패 현황과 범주별 목록 열기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L17 | 좌측 메뉴의 「처리실패」를 열어 「Gateway DLQ」·「Business DLQ」·「격리 설비」·「Repair Task」 카드의 건수를 확인한다. | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; DlqPage L19~109 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 처리실패 문구 L117~128 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 8.1 현황 확인 L2064~2084 | 네 카드의 표시명과 건수 렌더, 매뉴얼 진입 절차를 확인했다. |
| L18 | 확인할 카드를 누른다. | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; DlqPage L19~109 | toggle과 renderPanel이 선택 범주를 목록으로 바꾸는 것을 확인했다. |
| L18 | 아래 영역이 선택한 범주의 목록으로 바뀐다. | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; DlqPage L19~109 | toggle과 renderPanel이 선택 범주를 목록으로 바꾸는 것을 확인했다. |
| L19 | 선택한 카드를 다시 누르면 「최근 장애」가 있는 대시보드로 돌아간다. | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; DlqPage L19~109 | 같은 범주 재선택은 null로 바꾸고 DashboardPanel을 렌더한다. |
| L21 | 조회가 끝나기 전 카드 건수는 「—」로 표시한다. | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; DlqPage L19~109 | isSettled가 false이면 count가 em dash를 반환한다. |

### 처리실패 조회 권한과 라인 범위 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L36 | 저장소 기본 권한 데이터 기준으로 처리실패 조회에는 DLQ_READ가 필요하다. | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; useDlqLineScope L14~49 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; matchMethod L253~258 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 조회 기본 권한 L114~158 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·DLQ 기본 권한 L764~770 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; LINE_READ L14~23 | 라인·설비 보조 조회와 기본 권한의 URL/메서드 연결을 대조했다. EQP_WRITE의 메서드 null도 GET에 매칭된다. 권한 거부 때 넘길 항목은 이 화면의 식별 정보로 한정했다. |
| L36 | 라인별 표시에는 LINE_READ와 설비 조회 권한(EQP_READ·EQP_WRITE 중 하나)이 추가로 필요하다. | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; useDlqLineScope L14~49 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; matchMethod L253~258 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 조회 기본 권한 L114~158 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·DLQ 기본 권한 L764~770 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; LINE_READ L14~23 | 라인·설비 보조 조회와 기본 권한의 URL/메서드 연결을 대조했다. EQP_WRITE의 메서드 null도 GET에 매칭된다. 권한 거부 때 넘길 항목은 이 화면의 식별 정보로 한정했다. |
| L36 | 조회 권한이 거부되면 운영 관리자에게 범주·선택한 라인·발생 시각·오류 문구를 전달한다. | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; useDlqLineScope L14~49 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; matchMethod L253~258 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 조회 기본 권한 L114~158 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·DLQ 기본 권한 L764~770 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; LINE_READ L14~23 | 라인·설비 보조 조회와 기본 권한의 URL/메서드 연결을 대조했다. EQP_WRITE의 메서드 null도 GET에 매칭된다. 권한 거부 때 넘길 항목은 이 화면의 식별 정보로 한정했다. |
| L38 | 카드 건수·최근 장애·각 목록은 상단에서 고른 라인 기준으로 표시한다. | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; useDlqLineScope L14~49 / nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 ; filterDlqEntriesByLine L22~38 / nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 범주별 라인 필터 L41~57 | 공유 hook의 네 범주 필터와 개별 패널 필터를 확인했다. 라인 미해석은 빈 배열, eqpId 부재·조회 목록 미매핑은 모든 라인에서 통과한다. |
| L38 | 설비 ID가 없거나 조회한 설비 목록에서 찾을 수 없는 항목은 선택한 라인에 관계없이 표시한다. | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; useDlqLineScope L14~49 / nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 ; filterDlqEntriesByLine L22~38 / nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 범주별 라인 필터 L41~57 | 공유 hook의 네 범주 필터와 개별 패널 필터를 확인했다. 라인 미해석은 빈 배열, eqpId 부재·조회 목록 미매핑은 모든 라인에서 통과한다. |
| L38 | 선택한 라인을 해석하지 못하면 표시 목록이 비게 된다. | nori-tc-ui-front/src/features/dlq/hooks/useDlqLineScope.ts @ 9fe69a75dcd3 ; useDlqLineScope L14~49 / nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 ; filterDlqEntriesByLine L22~38 / nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 범주별 라인 필터 L41~57 | 공유 hook의 네 범주 필터와 개별 패널 필터를 확인했다. 라인 미해석은 빈 배열, eqpId 부재·조회 목록 미매핑은 모든 라인에서 통과한다. |

### 최근 장애에서 먼저 볼 항목 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L50 | 「최근 장애」는 네 범주의 항목을 합쳐 시간 역순으로 최대 20건을 표시한다. | nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; 최근 장애 렌더 L48~92 / nori-tc-ui-front/src/features/dlq/lib/dlq-feed.ts @ 9fe69a75dcd3 ; buildDashboardFeed·mapRepairTaskToFeed L58~85 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대시보드 문구 L119~124 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열 문구 L1071~1085 | 피드는 네 범주를 합쳐 시간 내림차순으로 20건을 자른다. Repair Task의 errorCode/errorMessage가 피드의 reasonCode/reasonMessage로 매핑된다. |
| L50 | 「발생 시각」·「카테고리」·「설비」·「사유 코드」·「사유」로 확인할 범주를 고른다. | nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; 최근 장애 렌더 L48~92 / nori-tc-ui-front/src/features/dlq/lib/dlq-feed.ts @ 9fe69a75dcd3 ; buildDashboardFeed·mapRepairTaskToFeed L58~85 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대시보드 문구 L119~124 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열 문구 L1071~1085 | 피드는 네 범주를 합쳐 시간 내림차순으로 20건을 자른다. Repair Task의 errorCode/errorMessage가 피드의 reasonCode/reasonMessage로 매핑된다. |
| L50 | Repair Task의 에러 코드와 에러 메시지도 이 표에서는 사유 코드와 사유에 표시한다. | nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; 최근 장애 렌더 L48~92 / nori-tc-ui-front/src/features/dlq/lib/dlq-feed.ts @ 9fe69a75dcd3 ; buildDashboardFeed·mapRepairTaskToFeed L58~85 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대시보드 문구 L119~124 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열 문구 L1071~1085 | 피드는 네 범주를 합쳐 시간 내림차순으로 20건을 자른다. Repair Task의 errorCode/errorMessage가 피드의 reasonCode/reasonMessage로 매핑된다. |
| L52 | 조회 중에는 「불러오는 중입니다.」, 표시할 항목이 없으면 「최근 장애 없음」이 나온다. | nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; DashboardPanel L101~174 / nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 조회 완료·오류 상태 L59~88 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대시보드 문구 L119~124 | 대시보드는 isSettled·feed.length로 로딩/빈 화면을 나누며 isError를 렌더하지 않는다. 범주 패널은 오류 분기가 있으므로 그 목록을 확인하도록 썼다. |
| L52 | 대시보드는 조회 오류를 별도 표시하지 않으므로, 「최근 장애 없음」이나 건수 0이 보이면 범주 카드를 열어 목록의 오류 여부도 확인한다. | nori-tc-ui-front/src/features/dlq/components/DashboardPanel.tsx @ 9fe69a75dcd3 ; DashboardPanel L101~174 / nori-tc-ui-front/src/features/dlq/hooks/useDlqCategoryEntries.ts @ 9fe69a75dcd3 ; 조회 완료·오류 상태 L59~88 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대시보드 문구 L119~124 | 대시보드는 isSettled·feed.length로 로딩/빈 화면을 나누며 isError를 렌더하지 않는다. 범주 패널은 오류 분기가 있으므로 그 목록을 확인하도록 썼다. |

### 실패 목록에서 검색하고 쪽 바꾸기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L65 | 네 범주의 「키워드 검색」은 현재 라인 범위의 조회된 목록에서 모든 열의 표시값을 검색한다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151 / nori-tc-ui-front/src/features/dlq/lib/dlq-format.ts @ 9fe69a75dcd3 ; DLQ_PAGE_SIZE·filterDlqRows L4~26 | trim·toLowerCase·각 열 accessor의 includes를 확인했다. 네 패널이 라인 필터 뒤 이 함수를 사용한다. |
| L65 | 앞뒤 공백을 제거하고 영문 대소문자를 구별하지 않는 부분 일치 검색이다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151 / nori-tc-ui-front/src/features/dlq/lib/dlq-format.ts @ 9fe69a75dcd3 ; DLQ_PAGE_SIZE·filterDlqRows L4~26 | trim·toLowerCase·각 열 accessor의 includes를 확인했다. 네 패널이 라인 필터 뒤 이 함수를 사용한다. |
| L67 | 목록은 각 범주의 발생·격리·생성 시각이 최신인 순서다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151 / nori-tc-ui-front/src/features/dlq/lib/dlq-format.ts @ 9fe69a75dcd3 ; DLQ_PAGE_SIZE·filterDlqRows L4~26 / nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L43~95 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L48~100 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L37~93 / nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L35~91 | 네 패널의 시각 내림차순 정렬, 초기 20, 공통 선택값 20/50/100, 검색/쪽 크기 변경의 setPage(1)을 대조했다. |
| L67 | 쪽당 건수는 처음 20건이며 20·50·100 중 고른다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151 / nori-tc-ui-front/src/features/dlq/lib/dlq-format.ts @ 9fe69a75dcd3 ; DLQ_PAGE_SIZE·filterDlqRows L4~26 / nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L43~95 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L48~100 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L37~93 / nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L35~91 | 네 패널의 시각 내림차순 정렬, 초기 20, 공통 선택값 20/50/100, 검색/쪽 크기 변경의 setPage(1)을 대조했다. |
| L67 | 검색어나 쪽당 건수를 바꾸면 첫 쪽으로 이동한다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151 / nori-tc-ui-front/src/features/dlq/lib/dlq-format.ts @ 9fe69a75dcd3 ; DLQ_PAGE_SIZE·filterDlqRows L4~26 / nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L43~95 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L48~100 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L37~93 / nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L35~91 | 네 패널의 시각 내림차순 정렬, 초기 20, 공통 선택값 20/50/100, 검색/쪽 크기 변경의 setPage(1)을 대조했다. |
| L69 | 표 위 「마지막 갱신」은 목록 데이터를 받은 시각이다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151 / nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L43~95 | lastUpdatedAt은 query.dataUpdatedAt이다. 갱신 단추는 refetch를 호출한다. |
| L69 | 현재 값을 다시 확인할 때 「새로고침」을 사용한다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 검색·갱신·쪽 이동 L78~151 / nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 정렬·검색·쪽 이동 L43~95 | lastUpdatedAt은 query.dataUpdatedAt이다. 갱신 단추는 refetch를 호출한다. |

### Gateway DLQ에서 실패 상세 값 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L80 | 「Gateway DLQ」카드를 눌러 목록에서 상세 값을 확인한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; buildGatewayDlqColumns L14~37 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Gateway 열 문구 L1067~1078 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchGatewayDlq L35~40 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listGatewayDlq L81~88 | 카드가 선택한 패널의 10개 열을 렌더 순서대로 대조했다. 목록 GET은 listAllDlq 결과를 반환한다. |
| L80 | 열은 「ID」·「설비」·「IF」·「소켓」·「단계」·「사유 코드」·「사유」·「발생 시각」·「payload 참조」·「TTL」 순서다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; buildGatewayDlqColumns L14~37 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Gateway 열 문구 L1067~1078 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchGatewayDlq L35~40 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listGatewayDlq L81~88 | 카드가 선택한 패널의 10개 열을 렌더 순서대로 대조했다. 목록 GET은 listAllDlq 결과를 반환한다. |
| L82 | 「ID」로 실패 항목을 구별하고, 「IF」·「소켓」으로 통신 구분을, 「단계」·「사유 코드」·「사유」로 실패 내용을 확인한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; buildGatewayDlqColumns L14~37 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L31~59 | 항목 ID, 통신 구분, 처리 단계, 사유 필드를 대조했다. 제조사 의미나 프로토콜별 지원값은 추가하지 않았다. |
| L84 | 「payload 참조」는 원문 자료의 참조값이다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; buildGatewayDlqColumns L14~37 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L31~59 | 원문 참조 필드와 null 표시 '-'를 확인했다. 인계 항목은 화면에 표시되는 값으로 한정했다. |
| L84 | 원문 확인이 필요하면 「ID」·설비 ID·발생 시각·사유·payload 참조값을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; buildGatewayDlqColumns L14~37 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L31~59 | 원문 참조 필드와 null 표시 '-'를 확인했다. 인계 항목은 화면에 표시되는 값으로 한정했다. |
| L84 | 참조값이 없으면 「-」로 표시한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; buildGatewayDlqColumns L14~37 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L31~59 | 원문 참조 필드와 null 표시 '-'를 확인했다. 인계 항목은 화면에 표시되는 값으로 한정했다. |

### Business DLQ에서 메시지 위치 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L95 | 「Business DLQ」카드를 누른다. | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Business 열 문구 L1066~1077 | 14개 열의 순서와 공통 표 컨테이너의 overflow-x-auto(DlqTableShell.tsx L115)를 대조했다. |
| L95 | 열은 「ID」·「출처」·「단계」·「토픽」·「파티션」·「오프셋」·「설비」·「타입」·「이름」·「사유 코드」·「사유」·「발생 시각」·「payload 참조」·「TTL」 순서다. | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Business 열 문구 L1066~1077 | 14개 열의 순서와 공통 표 컨테이너의 overflow-x-auto(DlqTableShell.tsx L115)를 대조했다. |
| L95 | 오른쪽 열은 표를 가로로 밀어 확인한다. | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Business 열 문구 L1066~1077 | 14개 열의 순서와 공통 표 컨테이너의 overflow-x-auto(DlqTableShell.tsx L115)를 대조했다. |
| L97 | 「출처」·「단계」·메시지 「타입」·「이름」과 사유를 확인한다. | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisBusinessDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L39~88 | 출처·단계·메시지 필드와 원본 수신 topic/partition/offset의 필드 계약을 확인했다. |
| L97 | 「토픽」·「파티션」·「오프셋」은 원본 메시지가 수신된 위치를 나타낸다. | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisBusinessDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L39~88 | 출처·단계·메시지 필드와 원본 수신 topic/partition/offset의 필드 계약을 확인했다. |
| L99 | 운영 담당자에게 「ID」·설비 ID·발생 시각·사유와 세 위치값을 함께 전달한다. | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisBusinessDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L39~88 | 인계 대상은 같은 행에서 확인한 ID·설비·시각·사유·세 위치값·원문 참조다. 직접 내부 조회 절차는 쓰지 않았다. |
| L99 | 원문 확인이 필요하면 「payload 참조」도 함께 전달한다. | nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; buildBusinessDlqColumns L15~42 / nori-tc-common/db/tc-db-domain/src/main/java/com/nori/tc/db/domain/redis/dlq/RedisBusinessDlqEntry.java @ 6fe71f6b2da6 ; 항목 필드 L39~88 | 인계 대상은 같은 행에서 확인한 ID·설비·시각·사유·세 위치값·원문 참조다. 직접 내부 조회 절차는 쓰지 않았다. |

### Business 수신 거부 항목의 원본 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L109 | 「출처」가 BUSINESS_INBOUND, 「단계」가 PRE_DISPATCH이면 업무 실행에 넘기기 전 수신 자료 변환을 거부한 기록이다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; rejectToDlq L189~217 | 고정 출처·단계는 업무 실행에 넘기기 전 거부 경로에서 생성된다. |
| L111 | 현재 수신 거부 생성 경로의 「사유 코드」는 INBOUND_MAPPING_REJECTED다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; rejectToDlq L189~217 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; map·doMap L67~131 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundRejectedException.java @ 7db24f7466bd ; 생성자 L17~28 | 거부 예외 생성은 mapper 한 곳이며 변환 예외를 INBOUND_MAPPING_REJECTED로 감싼다. 예외 생성자의 빈 코드 기본값도 같다. |
| L111 | JSON 해석·필수값·메시지 계약 검사 실패를 이 코드로 기록하고, 상세 내용은 「사유」에서 읽는다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; rejectToDlq L189~217 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessKafkaInboundRecordMapper.java @ 7db24f7466bd ; map·doMap L67~131 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundRejectedException.java @ 7db24f7466bd ; 생성자 L17~28 | 거부 예외 생성은 mapper 한 곳이며 변환 예외를 INBOUND_MAPPING_REJECTED로 감싼다. 예외 생성자의 빈 코드 기본값도 같다. |
| L113 | 「설비」는 항상 UNKNOWN_EQP이므로 실제 설비 ID로 이 기록을 찾을 수 없다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; rejectToDlq L189~217 | UNKNOWN_EQP를 고정하고 원본 topic/partition/offset을 그대로 기록한다. 인계값은 이 화면의 해당 행에서 확보한다. |
| L113 | 「ID」·발생 시각·사유와 「토픽」·「파티션」·「오프셋」을 운영 담당자에게 전달해 원본 확인을 요청한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; rejectToDlq L189~217 | UNKNOWN_EQP를 고정하고 원본 topic/partition/offset을 그대로 기록한다. 인계값은 이 화면의 해당 행에서 확보한다. |
| L115 | UNKNOWN_EQP가 조회한 설비 목록에 없으면 선택한 라인과 관계없이 표시한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/subscribe/BusinessInboundKafkaSubscriberSupport.java @ 7db24f7466bd ; rejectToDlq L189~217 / nori-tc-ui-front/src/features/dlq/lib/dlq-line-scope.ts @ 9fe69a75dcd3 ; filterDlqEntriesByLine L22~38 | 설비 목록 미매핑 항목의 모든 라인 통과 분기와 대조했다. 라인 미해석 상태는 기존 빈 목록 조건을 유지한다. |

### Business UI 요청의 실패 단계 구분하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L127 | 「출처」가 BUSINESS_UI_PIPELINE이면 UI 요청 처리 중 생긴 기록이다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; report L60~98 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; normalizeReasonCode·normalizeEqpId L136~161 | 출처는 고정, 설비 값은 request.data.eqpId의 trim 결과다. null·공백은 UNKNOWN_EQP다. |
| L127 | 「설비」는 요청의 설비 ID이며 값이 없거나 공백이면 UNKNOWN_EQP다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; report L60~98 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; normalizeReasonCode·normalizeEqpId L136~161 | 출처는 고정, 설비 값은 request.data.eqpId의 trim 결과다. null·공백은 UNKNOWN_EQP다. |
| L131 | ROUTING — ROUTING_FAILED — 이벤트 종류가 없거나 처리기를 찾지 못했다. | nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/runtime/KafkaTaskExecutionPipeline.java @ ae6791e99e91 ; 라우팅 실패 L174~198 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/constants/KafkaTaskPipelineStage.java @ ae6791e99e91 ; 단계 정의 L9~24 | 이벤트 종류 null 또는 처리기 사양 부재일 때 ROUTING·ROUTING_FAILED를 전달한다. |
| L132 | PROCESS — PROCESS_FAILED — 처리기 실행 중 최종 예외·오류가 발생했다. | nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/runtime/KafkaTaskExecutionPipeline.java @ ae6791e99e91 ; 처리 실패 L354~384 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/constants/KafkaTaskPipelineStage.java @ ae6791e99e91 ; 단계 정의 L9~24 | 최종 예외 및 처리 중 Error 경로가 PROCESS·PROCESS_FAILED를 전달한다. |
| L133 | PUBLISH — PUBLISH_FAILED — 처리 결과의 응답 발행에 최종 실패했다. | nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/runtime/KafkaTaskExecutionPipeline.java @ ae6791e99e91 ; 응답 발행 실패 L437~455 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/pipeline/constants/KafkaTaskPipelineStage.java @ ae6791e99e91 ; 단계 정의 L9~24 | 응답 발행 재시도 소진 시 PUBLISH·PUBLISH_FAILED를 전달한다. |
| L135 | 단계가 전달되지 않으면 UNKNOWN, 사유 코드가 없거나 공백이면 UNKNOWN_REASON으로 기록한다. | nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; report L60~98 / nori-tc-business/business/adapter/tc-business-kafka-adapter/src/main/java/com/nori/tc/business/adapters/kafka/ui/BusinessUiTaskDlqReporter.java @ 7db24f7466bd ; normalizeReasonCode·normalizeEqpId L136~161 | 단계 null의 UNKNOWN과 빈 사유 코드의 UNKNOWN_REASON 보정을 확인했다. |

### Business 업무 실행의 최종 실패 분류 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L146 | 「출처」가 BUSINESS_RUNTIME이고 「단계」가 PROCESS면 업무 실행 중의 실패 기록이다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; publishRuntimeDlq L767~803 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/runtime/TaskHandlingPolicyEvaluator.java @ ae6791e99e91 ; decide·resolveFinalCategory L60~126 | 출처 BUSINESS_RUNTIME·단계 PROCESS·record.eqpId·decision.finalCategory().name()을 저장한다. |
| L146 | 「설비」는 처리하던 메시지의 설비 ID다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; publishRuntimeDlq L767~803 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/runtime/TaskHandlingPolicyEvaluator.java @ ae6791e99e91 ; decide·resolveFinalCategory L60~126 | 출처 BUSINESS_RUNTIME·단계 PROCESS·record.eqpId·decision.finalCategory().name()을 저장한다. |
| L146 | 「사유 코드」는 최종 실패 분류다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; publishRuntimeDlq L767~803 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/runtime/TaskHandlingPolicyEvaluator.java @ ae6791e99e91 ; decide·resolveFinalCategory L60~126 | 출처 BUSINESS_RUNTIME·단계 PROCESS·record.eqpId·decision.finalCategory().name()을 저장한다. |
| L150 | VALIDATION — 입력·계약 검증 실패다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/types/TaskFailureCategory.java @ ae6791e99e91 ; 실패 분류 L8~40 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/runtime/TaskHandlingPolicyEvaluator.java @ ae6791e99e91 ; decide·resolveFinalCategory L60~126 | 입력·계약 검증 enum 정의와 연결 포트 동기화 검증 예외의 VALIDATION 분류를 확인했다. |
| L151 | FILTER_EVAL — 워크플로우 필터 평가 실패다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/types/TaskFailureCategory.java @ ae6791e99e91 ; 실패 분류 L8~40 | 필터 평가 예외를 FILTER_EVAL로 분류한다. |
| L152 | ACTION_EXEC — 액션 실행 실패다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/types/TaskFailureCategory.java @ ae6791e99e91 ; 실패 분류 L8~40 | 액션 실행 예외를 ACTION_EXEC으로 분류한다. |
| L153 | TIMEOUT — 처리 제한시간 초과다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/types/TaskFailureCategory.java @ ae6791e99e91 ; 실패 분류 L8~40 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/runtime/TaskHandlingPolicyEvaluator.java @ ae6791e99e91 ; decide·resolveFinalCategory L60~126 | 시간 초과 예외와 timeoutTriggered를 최종 TIMEOUT으로 확정한다. |
| L154 | UNKNOWN — 다른 분류에 잡히지 않은 예외다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processAttempt L277~353 / nori-common/modules/nori-common-task-execution/src/main/java/com/nori/common/task/execution/policy/types/TaskFailureCategory.java @ ae6791e99e91 ; 실패 분류 L8~40 | 전용 예외 분류에 잡히지 않은 Exception은 UNKNOWN이다. |
| L156 | 「사유」는 예외 메시지이며, 없거나 공백이면 Runtime task moved to DLQ로 표시한다. | nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; publishRuntimeDlq L767~803 / nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; resolveReasonMessage L836~840 | 예외 메시지를 사용하고 null·공백이면 Runtime task moved to DLQ로 보정한다. |

### TTL로 기록의 유효기간 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L168 | 「Gateway DLQ」·「Business DLQ」·「격리 설비」의 「TTL」은 기록을 저장할 때 설정한 유효기간을 초로 표시한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L36~36 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L41~41 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L30~30 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; publish L70~97 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L65~83 / nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/dlq/RedisBusinessDlqPublisher.java @ 7db24f7466bd ; publish L61~89 | 세 writer가 설정값을 TTL 메타와 실제 만료기간에 함께 사용하고, UI는 메타 숫자를 그대로 표시한다. |
| L170 | 표시된 「TTL」은 시간이 지나도 줄어들지 않는다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L36~36 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L41~41 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L30~30 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; publish L70~97 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L65~83 / nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/dlq/RedisBusinessDlqPublisher.java @ 7db24f7466bd ; publish L61~89 | 조회 시 남은 TTL을 다시 계산하는 코드가 없고 null을 '-'로 표시한다. 양수일 때만 저장 만료기간을 준다. |
| L170 | 수치가 있는 기록은 저장 당시의 유효기간으로 만료되며, 만료 기한 설정이 없는 기록은 「-」로 표시한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L36~36 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L41~41 / nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; TTL 표시 L30~30 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; publish L70~97 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L65~83 / nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/dlq/RedisBusinessDlqPublisher.java @ 7db24f7466bd ; publish L61~89 | 조회 시 남은 TTL을 다시 계산하는 코드가 없고 null을 '-'로 표시한다. 양수일 때만 저장 만료기간을 준다. |
| L172 | 만료 뒤 목록은 「새로고침」으로 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; publish L70~97 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L65~83 / nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/dlq/RedisBusinessDlqPublisher.java @ 7db24f7466bd ; publish L61~89 | writer는 기록의 저장과 만료만 수행한다. 원래 실패 작업을 복구하는 처리는 이 만료 경로에 없다. 목록의 재조회로 기록 유무를 확인하도록 썼다. |
| L172 | 실패 기록이나 격리 표시의 만료와 실제 처리의 복구 완료는 구분한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/dlq/RedisDlqPublisher.java @ d3636ab86763 ; publish L70~97 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L65~83 / nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/dlq/RedisBusinessDlqPublisher.java @ 7db24f7466bd ; publish L61~89 | writer는 기록의 저장과 만료만 수행한다. 원래 실패 작업을 복구하는 처리는 이 만료 경로에 없다. 목록의 재조회로 기록 유무를 확인하도록 썼다. |

### 입력·설비 식별 사유 코드 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L181 | Gateway의 「사유 코드」를 아래와 대조하고, 같은 행의 「사유」로 누락 항목이나 설비 조회 결과를 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; validateEnvelope L114~170 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; resolveEquipmentOrPublishDlq L243~271 | 사유 코드를 생성할 때 누락 필드 설명·설비 조회 상태를 reasonMessage에 함께 전달한다. |
| L185 | INVALID_INPUT — 필수 메시지 자료가 없거나 입력값이 올바르지 않다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; INVALID_INPUT·UNKNOWN_EQUIPMENT L23~31 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; validateEnvelope L114~170 | metadata/data/eqpId 누락·interfaceType 오류를 INVALID_INPUT으로 기록하는 분기를 확인했다. |
| L186 | UNKNOWN_EQUIPMENT — 실행 중인 Gateway의 설비 목록에서 대상 설비 정보를 찾지 못했다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; INVALID_INPUT·UNKNOWN_EQUIPMENT L23~31 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; resolveEquipmentOrPublishDlq L243~271 | runtimeCatalog.find 결과가 found가 아니면 UNKNOWN_EQUIPMENT를 기록한다. |

### 수신·송신 대기열 포화 구분하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L195 | Gateway의 「사유 코드」에서 수신과 송신 대기열을 구분한다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; 대기열 사유 L33~37 / nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; OUTBOUND_QUEUE_OVERFLOW L70~74 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; handleQueueOverflow·safeQuarantine L398~469 | 수신 포화 enum 값과 송신 포화 문자열이 표준 enum 값과 일치하는 것을 확인했다. |
| L199 | INBOUND_QUEUE_OVERFLOW — 설비별 수신 대기열이 가득 차 입력을 넣지 못한 경우다. Gateway가 DLQ 기록과 격리 기록을 요청한다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; 대기열 사유 L33~37 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; handleQueueOverflow·safeQuarantine L398~469 | handleQueueOverflow의 DLQ publish 및 quarantine 호출을 확인했다. 기록 요청을 실제 저장 성공으로 단정하지 않았다. |
| L200 | OUTBOUND_QUEUE_OVERFLOW — 설비별 송신 대기열이 가득 찬 경우의 격리 사유다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; OUTBOUND_QUEUE_OVERFLOW L70~74 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; handleQueueOverflow·safeQuarantine L398~469 | safeQuarantine은 OUTBOUND_QUEUE_OVERFLOW 문자열을 격리 사유로 전달한다. |
| L202 | 대기열 포화 항목을 확인할 때는 「격리 설비」카드도 열어 같은 설비의 기록이 있는지 확인한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/application/ingress/GatewayIngressService.java @ d3636ab86763 ; handleQueueOverflow·safeQuarantine L398~469 | 수신 포화는 격리 호출을 동반하고 송신 포화도 격리 기록 경로를 사용한다. 같은 설비 기록의 확인 조작으로 제한했다. |

### 프레임·파싱·PLC 매핑 사유 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L212 | Gateway의 사유 코드와 같은 행의 「사유」를 함께 읽는다. | nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; drain·processFrame L125~158 / nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java @ d3636ab86763 ; handleFailure L286~333 | 두 처리 경로에서 코드와 예외 메시지를 함께 기록한다. 코드를 확정 원인 전체로 확대하지 않았다. |
| L216 | FRAMING_FAILED — 프레임 처리 실패다. PLC에서는 프레임 해석 오류를 이 코드로 기록한다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; 프레임·파싱 정의 L44~52 / nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; drain·processFrame L125~158 | PLC의 PlcProtocolCodecException은 FRAMING_FAILED로 기록된다. |
| L217 | PARSING_FAILED — 파싱·변환 실패 분류다. 공통 수신 처리 경로는 처리 중 예외도 이 코드로 기록한다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; 프레임·파싱 정의 L44~52 / nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; drain·processFrame L125~158 / nori-tc-gateway/gateway/tc-gateway-engine/src/main/java/com/nori/tc/gateway/engine/usecase/EqpSequentialProcessor.java @ d3636ab86763 ; handleFailure L286~333 | PLC 변환 오류의 PARSING_FAILED와 공통 처리 예외의 대표 PARSING_FAILED를 모두 대조했다. |
| L218 | MAPPING_NOT_FOUND — PLC 응답을 해석할 그룹 매핑이 없다. 확인한 PLC 경로는 DLQ만 기록하고 이 사유로 격리하지 않는다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; MAPPING_NOT_FOUND L64~68 / nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/pipeline/PlcInboundPipeline.java @ d3636ab86763 ; drain·processFrame L125~158 | 그룹 조회가 null이면 MAPPING_NOT_FOUND DLQ만 발행하고 return한다. 이 분기는 quarantine을 호출하지 않는다. |

### 라우팅·송신 실패 사유 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L228 | Gateway의 다음 사유 코드는 「단계」·「사유」와 함께 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; buildOutboundFrameOrPublishDlq L350~388 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; 송신 실패 L287~298 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskDlqPublisher.java @ d3636ab86763 ; toDlqReasonCode L227~234 | 기록 시 단계와 상세 예외 메시지를 함께 넘기는 것을 확인했다. |
| L232 | ROUTING_FAILED — 처리 실패 분류다. 송신 프레임 생성에서 지원하지 않는 처리를 요청한 경우에도 기록한다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; ROUTING_FAILED·PUBLISH_FAILED L54~62 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcherSupport.java @ d3636ab86763 ; buildOutboundFrameOrPublishDlq L350~388 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskDlqPublisher.java @ d3636ab86763 ; toDlqReasonCode L227~234 | UnsupportedOperationException이 ROUTING_FAILED로 기록되며 UI task의 PROCESS_FAILED도 같은 코드로 매핑된다. |
| L233 | PUBLISH_FAILED — 발행 실패 분류다. 설비 송신 경로의 예외도 이 코드로 기록한다. | nori-tc-gateway/gateway/tc-gateway-domain/src/main/java/com/nori/tc/gateway/domain/dlq/DlqReasonCode.java @ d3636ab86763 ; ROUTING_FAILED·PUBLISH_FAILED L54~62 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/GatewayCommandDispatcher.java @ d3636ab86763 ; 송신 실패 L287~298 / nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiTaskDlqPublisher.java @ d3636ab86763 ; toDlqReasonCode L227~234 | PUBLISH_FAILED 매핑과 설비 송신 예외의 같은 코드 사용을 함께 확인해 Kafka 발행 실패로만 한정하지 않았다. |

### DLQ 항목 정리와 재처리를 요청하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L246 | 확인한 판본의 「Gateway DLQ」·「Business DLQ」화면은 목록 조회용이며 삭제·재처리 단추나 행을 여는 상세 화면은 제공하지 않는다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~107 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L76~112 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 API L30~59 | 두 패널의 전체 렌더와 API 객체에는 조회만 있고 행 상세/삭제/재처리 조작이 없다. |
| L246 | 상세 값은 목록의 각 열에서 읽는다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~107 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L76~112 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 API L30~59 | 두 패널의 전체 렌더와 API 객체에는 조회만 있고 행 상세/삭제/재처리 조작이 없다. |
| L248 | 서버의 삭제 처리는 저장된 실패 기록만 제거하며 원래 메시지를 재전송하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteGatewayDlq L100~114 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteBusinessDlq L148~162 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; deleteDlq L137~150 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd ; deleteDlq L128~141 | DELETE controller는 deleteDlq만 호출하고 두 구현은 해당 기록 키 delete만 수행한다. 메시지 재발행 호출은 없다. |
| L250 | 정리나 재처리가 필요하면 운영 담당자에게 범주·「ID」·설비 ID·발생 시각·사유를 전달하고, 기록 정리와 원래 처리의 복구를 각각 요청한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~107 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L76~112 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 API L30~59 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteGatewayDlq L100~114 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteBusinessDlq L148~162 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; deleteDlq L137~150 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd ; deleteDlq L128~141 | 직접 API 실행 대신 화면 식별값을 운영 담당자에게 전달하도록 했다. 정리 결과 확인은 목록 refetch 범위다. |
| L250 | 정리 뒤 「새로고침」으로 대상 행의 유무를 확인한다. | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L71~107 / nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L76~112 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; 조회 API L30~59 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteGatewayDlq L100~114 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; deleteBusinessDlq L148~162 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/GatewayDlqRedisService.java @ 985d17c604dd ; deleteDlq L137~150 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/dlq/BusinessDlqRedisService.java @ 985d17c604dd ; deleteDlq L128~141 | 직접 API 실행 대신 화면 식별값을 운영 담당자에게 전달하도록 했다. 정리 결과 확인은 목록 refetch 범위다. |

### 격리 설비의 사유와 표시 만료 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L262 | 「격리 설비」카드를 누른다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 열·목록 렌더 L14~107 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 격리 열 문구 L1071~1079 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchQuarantine L48~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listGatewayQuarantine L177~184 | 격리 조회 API와 패널의 다섯 열·카드 표시명을 대조했다. |
| L262 | 「설비」·「사유 코드」·「사유」·「격리 시각」·「TTL」을 확인한다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 열·목록 렌더 L14~107 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 격리 열 문구 L1071~1079 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchQuarantine L48~52 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listGatewayQuarantine L177~184 | 격리 조회 API와 패널의 다섯 열·카드 표시명을 대조했다. |
| L263 | 격리 해제가 필요하면 설비 ID·격리 시각·코드·사유·TTL 표시값을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 열·목록 렌더 L14~107 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 8.4.1 절차 L2181~2192 | 격리 패널에 해제 조작이 없으며 매뉴얼도 관리자 요청을 안내한다. 인계값은 같은 행의 다섯 필드로 제한했다. |
| L263 | 화면에는 격리 해제 단추가 없다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 열·목록 렌더 L14~107 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 8.4.1 절차 L2181~2192 | 격리 패널에 해제 조작이 없으며 매뉴얼도 관리자 요청을 안내한다. 인계값은 같은 행의 다섯 필드로 제한했다. |
| L264 | 유효기간이 있는 격리 기록은 만료 뒤 「새로고침」으로 표시 여부를 확인한다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 열·목록 렌더 L14~107 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L62~85 | 양수 TTL은 만료 저장, 비양수는 만료 없는 저장과 null 메타다. 목록의 '-' 및 refetch와 대조했다. |
| L264 | 「TTL」이 「-」이면 자동 만료 기한이 없는 기록이다. | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 ; 열·목록 렌더 L14~107 / nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L62~85 | 양수 TTL은 만료 저장, 비양수는 만료 없는 저장과 null 메타다. 목록의 '-' 및 refetch와 대조했다. |
| L266 | 격리 기록이 사라진 뒤 설비를 다시 사용할 수 있는지는 운영 담당자에게 복구 결과로 확인한다. | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/quarantine/RedisQuarantineStore.java @ d3636ab86763 ; quarantine L62~85 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 8.4.1 절차 L2181~2192 | 기록 만료를 설비 정상화로 확정하지 않고 운영 담당자의 실제 복구 확인으로 안내했다. 자동 재시작 지시는 넣지 않았다. |

### Repair Task에서 복구할 작업 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L278 | 「Repair Task」는 설비 명령을 되돌리는 처리에 실패했거나, PLC 폴링 변경을 Gateway에 반영한 뒤 저장에 실패한 작업 등을 기록한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioRollbackService.java @ 985d17c604dd ; 보상 실패 기록 L132~199 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd ; persistPollGroupMutation L380~401 | 보상 발행/응답/대기 실패의 recordRepairTask와 Gateway 반영 뒤 폴링 저장 실패의 repairTaskService.record를 확인했다. |
| L280 | 「Repair Task」카드를 누른다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 열·정렬·목록 렌더 L14~103 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Repair Task 열 문구 L1080~1084 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/EqpRepairTaskResponse.java @ 985d17c604dd ; 응답 항목 L15~30 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listRepairTasks L200~209 | 응답 DTO의 일곱 필드가 표의 일곱 열로 표시된다. 조회 API도 같은 DTO를 반환한다. |
| L280 | 「Task ID」·「설비」·「커맨드」·「액션」·「에러 코드」·「에러 메시지」·「생성 시각」을 읽는다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 열·정렬·목록 렌더 L14~103 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Repair Task 열 문구 L1080~1084 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/EqpRepairTaskResponse.java @ 985d17c604dd ; 응답 항목 L15~30 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd ; listRepairTasks L200~209 | 응답 DTO의 일곱 필드가 표의 일곱 열로 표시된다. 조회 API도 같은 DTO를 반환한다. |
| L281 | 「커맨드」로 실패한 작업을, 「액션」으로 필요한 복구 작업을 확인한다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 열·정렬·목록 렌더 L14~103 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/EqpRepairTaskResponse.java @ 985d17c604dd ; 응답 항목 L15~30 | commandType·repairAction·errorCode·errorMessage의 DTO 설명과 렌더 accessor를 대조했다. |
| L281 | 「에러 코드」·「에러 메시지」로 기록된 실패 내용을 확인한다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 열·정렬·목록 렌더 L14~103 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/EqpRepairTaskResponse.java @ 985d17c604dd ; 응답 항목 L15~30 | commandType·repairAction·errorCode·errorMessage의 DTO 설명과 렌더 accessor를 대조했다. |
| L282 | 복구를 요청할 때 위 일곱 항목의 표시값을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 열·정렬·목록 렌더 L14~103 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/EqpRepairTaskResponse.java @ 985d17c604dd ; 응답 항목 L15~30 | 화면 일곱 필드를 인계 항목으로 지정했다. 직접 복구 명령이나 내부 저장소 접근은 넣지 않았다. |

### Repair Task의 정리를 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L292 | 「Repair Task」목록에는 TTL 열이나 복구 실행·완료·삭제 단추가 없다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L67~103 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchRepairTasks L54~58 | 목록에는 TTL이나 상태 변경 조작이 없다. API 객체도 Repair Task는 GET만 제공한다. |
| L292 | 복구와 기록 정리는 운영 담당자에게 요청한다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L67~103 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchRepairTasks L54~58 | 목록에는 TTL이나 상태 변경 조작이 없다. API 객체도 Repair Task는 GET만 제공한다. |
| L294 | 같은 설비·커맨드·복구 액션의 실패가 반복되면 같은 Task ID에 다시 기록하므로, 한 행이 여러 차례의 실패를 나타낼 수 있다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpRepairTaskService.java @ 985d17c604dd ; record·buildTaskId L29~82 / nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/repair/RedisEqpRepairTaskStore.java @ 985d17c604dd ; save·findAll L54~77 | taskId는 eqpId:commandType:repairAction으로 생성되고 hash의 같은 field에 put한다. 반복 기록 시 행 수가 실패 횟수와 같다고 쓰지 않았다. |
| L296 | 담당자의 처리 후 「새로고침」으로 남은 행을 확인한다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L67~103 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchRepairTasks L54~58 | 갱신 단추는 조회만 수행하므로 조회 결과와 실제 복구 확인을 구분했다. |
| L296 | 행의 유무와 함께 담당자가 확인한 실제 복구 결과도 확인한다. | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 ; 목록 렌더 L67~103 / nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; fetchRepairTasks L54~58 | 갱신 단추는 조회만 수행하므로 조회 결과와 실제 복구 확인을 구분했다. |

### 범주별 목록의 기본 실패 문구 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L305 | 목록 응답을 해석하지 못하거나 실패 응답에 오류 설명이 없을 때는 범주별 기본 실패 문구를 표시한다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·fetchDlqList L14~58 / nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류 표시 L127~132 | 공통 응답 형식 불일치 또는 success가 true가 아닌 응답에 errorMsg가 없으면 fallbackMessage로 Error를 만든다. shell은 Error.message를 표시한다. |
| L309 | Gateway DLQ — Gateway DLQ 목록을 조회하지 못했습니다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·fetchDlqList L14~58 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 범주별 오류 문구 L1062~1065 | fetchGatewayDlq의 기본 오류 키와 실제 한국어 문자열을 확인했다. |
| L310 | Business DLQ — Business DLQ 목록을 조회하지 못했습니다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·fetchDlqList L14~58 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 범주별 오류 문구 L1062~1065 | fetchBusinessDlq의 기본 오류 키와 실제 한국어 문자열을 확인했다. |
| L311 | 격리 설비 — 격리 설비 목록을 조회하지 못했습니다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·fetchDlqList L14~58 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 범주별 오류 문구 L1062~1065 | fetchQuarantine의 기본 오류 키와 실제 한국어 문자열을 확인했다. |
| L312 | Repair Task — Repair Task 목록을 조회하지 못했습니다. | nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 ; unwrapArrayPayload·fetchDlqList L14~58 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 범주별 오류 문구 L1062~1065 | fetchRepairTasks의 기본 오류 키와 실제 한국어 문자열을 확인했다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼·선언 문구 | 확인한 코드 | 검색 원문에 쓴 내용 |
|---|---|---|---|
| TTL 표시 | U §8 L2049~2051·L2125·L2168·L2209는 자동 삭제까지 남은 초로 설명한다. | Gateway RedisDlqPublisher L70~97·RedisQuarantineStore L65~83, Business RedisBusinessDlqPublisher L61~89는 저장 당시 설정 초를 메타에 복사한다. UI는 메타를 그대로 표시하며 남은 시간을 조회하지 않는다. | 저장 당시 유효기간(초)이며 표시값은 줄어들지 않는다고 썼다. |
| 대시보드 조회 실패 | U §8.1.4 L2088~2091은 조회 실패 문구 표시와 새로고침을 안내한다. | DashboardPanel L101~174는 isSettled와 feed.length만으로 표시를 나누고 useDlqCategoryEntries L64~80의 isError를 표시하지 않는다. | 최근 장애 없음·0건만으로 판단하지 말고 범주 목록에서 오류를 확인하도록 했다. |
| PLC 매핑 누락의 격리 | DlqReasonCode.java L64~68 주석은 단발 DLQ·지속 시 quarantine이라고 설명한다. | PlcInboundPipeline L153~158은 그룹 매핑이 없으면 DLQ 발행 뒤 반환한다. 관련 시험 L154~164도 quarantine 없음이 기대값이다. | 확인한 PLC 경로는 이 사유로 격리하지 않는다고 명시했다. 이는 매뉴얼 대조 외에 발견한 코드 주석과 구현의 차이다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 확인한 근거와 제외 이유 | 확인할 담당 역할 |
|---|---|---|
| PAYLOAD_TOO_LARGE·BASE64_DECODE_FAIL·REASSEMBLY_OVERFLOW의 현재 발생 조건과 화면 조치 | DlqReasonCode.java @ d3636ab86763 L12~42에 정의가 있다. 저장소 전체 Java 검색에서 이 세 값은 enum 선언과 GatewayCommandFailureSupport L363~373의 분류 매핑만 확인됐다. 실제 이 코드를 생성하는 발행 경로가 확인되지 않아 주석만으로 현재 동작을 만들어 쓰지 않았다. | Gateway 처리 담당 |
| 격리된 모든 설비의 메시지 처리 중지와 Live-Map 상태 불변 | U L2211은 단정하지만 RedisQuarantineStore L62~85는 기록 저장만 한다. QuarantinePort L23은 기록 요청 인터페이스이며, 일부 오류 경로의 채널 종료만으로 모든 격리의 차단·화면 불변을 확인할 수 없다. | Gateway·Live-Map 담당 |
| 격리 기록 만료 직후 반드시 설비를 시작하는 절차 | U L2190~2192의 일괄 재시작 지침에 대해, 기록 만료가 실제 연결·처리 상태를 어떻게 복구하는지 코드 근거가 없다. 만료 뒤 기록 유무 확인과 운영 담당자의 복구 결과 확인만 썼다. | 설비 운영·Gateway 담당 |
| 사용자 화면에서 실행하는 삭제·재처리·격리 해제·Repair Task 복구/완료 | 현재 렌더·API에는 그런 조작이 없다. 가상의 절차를 쓰지 않고 실제 제한과 담당자 인계를 적었다. | UI·운영 담당 |
| 현장 사용자 권한·배포 판본·현재 장애 건수·실제 복구 결과 | 읽은 자료는 로컬 소스와 기본 권한 데이터다. 현장 시스템에는 접속하지 않았다. | 현장 운영 담당 |

세 enum 값은 없는 값으로 취급한 것이 아니라, 정의와 분류 매핑은 확인했으나 현재 발생 경로가 미확인인 값으로 보존했다. 사용자 결정을 요구하는 빈틈은 없고, 확인된 일반 화면 절차로 목록의 질문에 답할 수 있다.

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에 복제하지 않은 사실 |
|---|---|
| 00.rag/user/login-and-screen-basics.md | 예전 /dlq/* 주소의 /dlq 이동, 공통 조회 진입 권한, 일반 빈 결과·서버 오류 설명·재시도·새로고침 조작 설명. 이번 문서는 보조 조회 권한과 범주별 기본 오류 문자열 등 다른 점만 보완했다. |
| 00.rag/user/permission-code-reference.md | 각 권한 코드의 전체 의미·역할별 부여 방법. 이번 문서는 이 화면 조회에 필요한 최소 조건만 썼다. |
| 00.rag/user/state-reference.md | 설비·작업 상태값 전체의 의미. Repair Task의 액션은 표시 위치와 인계값으로만 다뤘다. |
| 00.rag/user/error-message-and-code-reference.md | 일반 오류 메시지 전체와 원인별 조치 사전. 목록에서 이 문서가 소유한 DlqReasonCode 사용 조건과 화면별 기본 문구만 썼다. |
| 00.rag/user/dlq-quarantine-troubleshooting.md | 역직렬화 실패·목록 누락 등의 내부 원인 진단과 수정 판본. 현재 문서는 화면 조회·정리 요청을 소유한다. |
| 00.rag/user/equipment-commands-and-polling.md | 설비 Start·End·Reconnect·폴링 명령의 실행 절차. 만료를 재시작 성공으로 연결하지 않았다. |
| 00.rag/user/deployment-workflow.md | 배포 결과의 REPAIR_REQUIRED 표시·배포 번호별 진행 확인. 이번 문서는 별도 Repair Task 목록의 일곱 표시 항목을 다뤘다. |

다른 문서에서 별도로 보고할 확정 결함은 발견하지 않았다. 기존 문서는 수정하지 않았다.

## 최초 작성 검사 결과(보완 전)

- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/dlq-quarantine-and-repair-task.md`
- 실제 실행은 Git for Windows의 `C:/Program Files/Git/bin/bash.exe`를 사용했다.
- 최종 검색 원문 편집 뒤 실행: exit 0, violations=0, CROSS VIOLATION 출력 0, MASK 출력 0.
- 최종 값: 16절·53자식, 최대 자식 159자·최대 부모 309자. 최장 제목 26자, H1 21자. 본문 소유가 같은 인계 안내를 사유 코드 절마다 반복하던 초안을 줄였다.
- 최초 검사도 exit 0이었으나, 이후 의미 중복 검토·권한 seed 원천 보강·범주별 오류 문구 보완을 수행하고 최종 검사를 다시 실행했다. 초안의 비한국어 혼입은 첫 검사 전에 제거했다.
- 도구: PowerShell 5.1.26100.9444, GNU bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS, Git 2.52.0.windows.1. 검사기의 commonmark 0.32.0과 기존 mask-v1 적용을 사용했고 검사기를 수정하지 않았다.
- 위 사실 대조는 83개 문장/표 행으로 나눠 모든 본문 자식 53개를 확인했다.
- 관련 시험 원문을 읽고 기대값만 대조했다. 시험 실행 PASS로 기록하지 않는다: ui-front tests/unit/dlq-line-scope.test.ts @ 9fe69a75dcd3 L28~61, Gateway PlcInboundPipelineTest.java @ d3636ab86763 L119~164, GatewayIngressServiceTest.java @ d3636ab86763 L101~120, RedisDlqPublisherTest.java @ d3636ab86763 L49~106, ui-backend DlqControllerRepairTaskTest.java @ 985d17c604dd L50~84. DlqControllerSecurityTest는 실제 권한 통합 시험이 아니라 URL 라우팅 시험임을 원문 L20~37에서 구분했다.
- 제품 build·브라우저·Docker 시험은 문서 작성 범위여서 실행하지 않았다. 현장 판본과 정상화 결과는 미검증이다.
- 검사용 임시 파일·브라우저·서비스·컨테이너·독립 AI 프로세스를 만들지 않았다. 지정 work 폴더에 남길 소유 임시 자원이 없으므로 삭제 실행도 없다.
- 독립 AI 호출 0회이며 provider 사용량·비용은 이 세션에서 조회되지 않았다. 검사 실행은 3회다. 명령 응답의 wall time은 최초 8.330초, 중간 7.568초, 권한 문장 명확화 뒤 최종 7.355초로 확인했다.

마지막 출력 10줄:

```text
    L239-239 문단 65  | 담당자의 처리 후 「새로고침」으로 남은 행을 확인한다. 행의 유무와 함께 담당자가 확인한 실제 복구 결과도 확인한다.
§ L241-255 [21] 범주별 목록의 기본 실패 문구 구별하기   parent=262 children=5 sources=3
    L248-248 문단 56  | 목록 응답을 해석하지 못하거나 실패 응답에 오류 설명이 없을 때는 범주별 기본 실패 문구를 표시한다.
    L252-252 표 행 53  | 범주=Gateway DLQ | 기본 실패 문구=Gateway DLQ 목록을 조회하지 못했습니다.
    L253-253 표 행 55  | 범주=Business DLQ | 기본 실패 문구=Business DLQ 목록을 조회하지 못했습니다.
    L254-254 표 행 41  | 범주=격리 설비 | 기본 실패 문구=격리 설비 목록을 조회하지 못했습니다.
    L255-255 표 행 53  | 범주=Repair Task | 기본 실패 문구=Repair Task 목록을 조회하지 못했습니다.
SUMMARY sections=16 children=53 maxChild=159 maxParent=309
CORPUS docs=10/53 sections=187 part={user=10} type={사용 절차=10}
TOTAL files=10 targets=1 violations=0
```

### 최초 작성 사람 검사 판정(보완 전)

16절 전부를 읽었다. 무작위 표본 추출로 줄이지 않았다. 각 절의 제목 질문 식별·원천 일치·독립 답·소유 중복 여부를 다음과 같이 확인했다.

| 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 중복·경계 |
|---|---|---|---|---|
| 처리실패 현황과 범주별 목록 열기 | 카드 명칭·진입 질문 식별 | toggle/null 렌더 대조 | 목록 전환과 복귀로 완결 | 공통 메뉴 설명은 복제하지 않음 |
| 처리실패 조회 권한과 라인 범위 확인하기 | 보조 조회 권한·필터 질문 식별 | seed·PREFIX·전체 메서드 일치 | 권한 거부 인계 포함 | 권한 코드 전체 사전은 제외 |
| 최근 장애에서 먼저 볼 항목 찾기 | 최근 항목 선정 질문 식별 | 통합 피드 20건·오류 분기 대조 | 빈 표시의 확인 경로 포함 | 범주 목록 상세는 각 소유 절에 둠 |
| 실패 목록에서 검색하고 쪽 바꾸기 | 검색·쪽 이동 질문 식별 | 표시값 accessor·쪽 크기 대조 | 검색 범위·정렬·갱신 시각 포함 | 공통 표 복사·빈 결과 안내는 제외 |
| Gateway DLQ에서 실패 상세 값 읽기 | Gateway 상세 열 질문 식별 | 10개 열·원문 참조 계약 대조 | 대상·열 순서·인계값 포함 | 상세 화면 조작을 만들지 않음 |
| Business DLQ에서 메시지 위치 확인하기 | Business 메시지 위치 질문 식별 | 14개 열·수신 위치 계약 대조 | 가로 이동·세 위치값 포함 | Kafka 내부 조회 절차는 제외 |
| TTL로 기록의 유효기간 확인하기 | TTL 의미 질문 식별 | 세 writer와 UI 표시값 대조 | 저장 기간·null·만료 조회 포함 | 격리 절에는 필요한 최소 만료 조건만 공유 |
| 입력·설비 식별 사유 코드 확인하기 | 입력·설비 사유 질문 식별 | 생성 분기 두 종류 대조 | 메시지 사유와 함께 읽도록 함 | 선언만 확인한 세 enum 값 제외 |
| 수신·송신 대기열 포화 구분하기 | 대기열 방향 질문 식별 | 수신 DLQ/격리·송신 격리 대조 | 같은 설비 격리 조회 연결 | 다른 코드 묶음과 발생 조건이 다름 |
| 프레임·파싱·PLC 매핑 사유 읽기 | 프레임·변환·매핑 질문 식별 | PLC 예외·공통 예외·매핑 분기 대조 | 각 코드의 해석 범위 명확 | PLC 장애 일반 진단은 제외 |
| 라우팅·송신 실패 사유 확인하기 | 라우팅·송신 사유 질문 식별 | 지원 안 됨·송신 예외·task 변환 대조 | 사유·단계와 코드 해석 연결 | PUBLISH_FAILED를 Kafka로만 제한하지 않음 |
| DLQ 항목 정리와 재처리를 요청하기 | 정리·재처리 가능 여부 식별 | 조회 UI와 키 삭제 구현 대조 | 한계·운영 인계·행 확인 포함 | 실제 내부 API 실행 절차 제외 |
| 격리 설비의 사유와 표시 만료 확인하기 | 격리 표시·해제 요청 질문 식별 | 5개 열·유효기간 저장 대조 | 만료 기록과 복구 확인 구분 | 일괄 처리 차단·자동 재시작 단정 제외 |
| Repair Task에서 복구할 작업 찾기 | 복구 작업 발견 질문 식별 | record 호출 두 경로·7개 DTO 대조 | 커맨드·액션·오류 인계 포함 | 배포 결과 상태 해석은 배포 문서 소유 |
| Repair Task의 정리를 확인하기 | 복구 기록 정리 질문 식별 | 조회 전용 UI·안정 taskId/hash 대조 | 반복 기록과 실제 복구 확인 포함 | 액션 코드 사전과 내부 정리 명령 제외 |
| 범주별 목록의 기본 실패 문구 구별하기 | 범주별 오류 식별 질문 명확 | API fallback과 한국어 문구 대조 | 표 행마다 범주·오류 문자열 명시 | 공통 재시도·오류 설명은 복제하지 않음 |

문서 전체의 제목 정확 중복과 불필요한 반복 제목 틀은 없었다. 표는 코드 묶음과 범주별 오류 문자열의 질문 경계로 나눴다. 본문은 조회·표시·조건·인계의 확인 사실로 한정하고, 코드 이름을 토막 내 번역하지 않았다. 실제 사용처가 없는 세 enum 값은 제외 기록에 남겼다. 한국어 문체·조사·주체·완결성을 전부 읽었고, 사실을 추가하지 않는 상투 문장이나 오답 인용을 넣지 않았다.

표에서 '이름'을 머리글로 사용해 값을 잃게 하지 않았다. Business의 화면 열 「이름」은 문장 안 열 순서로만 적었다. mask-v1 전후 소실이 없음을 검사 출력으로 확인했다. user 본문에 admin objectKey·링크·관리 문서 열람 지시·내부 접근 명령을 넣지 않았다. 사용자에게는 화면 값과 운영 담당자에게 넘길 정보만 안내했다.

완료 판정은 문서 작성과 자체검사의 범위다. 메인 세션의 독립 사실 검증이나 실제 RAG 업로드·검색 반영을 완료했다고 기록하지 않는다.

## 메인 세션 검증 결함 1건 보완

- 요청 범위: Business DLQ의 출처·단계·사유 코드·설비 표시값의 뜻과 수신 거부 원본 인계만 보완한다.
- 원인: 최초 작성은 화면 열·조회 API·저장 DTO를 대조했으나 BusinessDlqMessage의 실제 생성 경로에서 각 값이 채워지는 조건을 추적하지 않아 Business 분류 의미를 누락했다.
- 수정: 기존 Business 목록 절 뒤에 수신 거부·UI 요청·업무 실행의 세 절만 추가했다. H1 적용 판본에 실제 사용한 공통 라이브러리를 추가했다. 기존 절과 본문은 보존했다.
- 직접 영향: 기존 사실 대조표의 L101 이후 검색 원문 줄 참조를 57줄 이동했다. 추가 16자식은 문장·표 행 21개로 대조했다. 최종 대조표 104행이 본문 자식 69개를 덮는다.
- 변경 전 검색 원문 SHA-256: `7e96e15b8609f09ad056be2e4ca8d4aecd7594a1b875bf97e640cb424ac9f2cf`
- 변경 전 작성 기록 SHA-256: `c4d62bcee375f4269e65a3be00be5650185df1d67d1792d69c2a2e407a9f1d08`
- 변경 후 검색 원문 SHA-256: `5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d`
- 보존 확인: 추가 세 절과 H1의 nori-common 판본을 역으로 제외한 메모리 문자열의 SHA-256이 변경 전 검색 원문 SHA와 일치했다. 나머지 원문은 바이트 단위로 보존됐다.

### 생성 경로와 코드값 확인

실제로 실행한 주요 읽기 전용 검색:

```text
git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-business -C ../nori-tc-business grep -n -F 'new BusinessDlqMessage(' -- 'business/**/src/main/**/*.java'
git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-business -C ../nori-tc-business grep -n -E 'BusinessInboundRejectedException|TaskFailureCategory|TaskHandlingDecision' -- 'business/**/src/main/**/*.java'
git -c safe.directory=C:/workspace_nori/nori-tc/nori-common -C ../nori-common grep -n -E 'dlqReporter.report|reportDlq|KafkaTaskPipelineReasonKeys' -- 'modules/nori-common-task-execution/src/main/**/*.java'
```

- BusinessDlqMessage 생성은 BusinessInboundKafkaSubscriberSupport L199, BusinessUiTaskDlqReporter L82, BusinessRuntimeTaskSupport L787 세 곳이었다.
- BusinessInboundRejectedException 생성은 BusinessKafkaInboundRecordMapper L78 한 곳이다. 변환 예외는 INBOUND_MAPPING_REJECTED로 감싼다. 같은 예외를 재전파하는 catch와 예외 생성자 L17~28의 빈 코드 기본값도 확인했다.
- UI 리포터에 전달되는 실제 조합은 공통 KafkaTaskExecutionPipeline의 ROUTING/ROUTING_FAILED, PROCESS/PROCESS_FAILED, PUBLISH/PUBLISH_FAILED다. reporter의 UNKNOWN·UNKNOWN_REASON은 값이 전달되지 않았을 때의 기본값으로 구분했다.
- TaskFailureCategory @ nori-common ae6791e99e91 L8~40의 여섯 값을 읽었다. BusinessRuntimeTaskSupport L293~353의 실패 분류는 VALIDATION·FILTER_EVAL·ACTION_EXEC·TIMEOUT·UNKNOWN 다섯 가지다. TaskHandlingPolicyEvaluator L122~126은 timeoutTriggered이면 최종 TIMEOUT으로 정한다.
- WORKFLOW_NOT_FOUND는 enum에 있으나 실패 코드 표에서 제외했다. BusinessRuntimeTaskSupport L277~292는 정상 처리 후 종결하고 evaluator L62~70도 정상 continue를 반환하므로 현재 업무 실행 DLQ의 발생 코드로 제시하지 않았다.
- UNKNOWN_TRACE는 subscriber L212와 UI reporter L166~170에서 확인했다. 화면에 trace 열이 없으므로 trace 조회 조작은 만들지 않았다.
- Runtime 지원 객체의 정책 타입이 TaskHandlingPolicyEvaluator임을 필드·생성자 및 BusinessRuntimeEngine L130~136의 실제 조립에서 확인했다.
- 추가한 값 중 발생 조건을 확인하지 못해 생략한 값은 없다. 최초 작성의 Gateway 세 enum 값 등 기존 제외 기록은 유지한다.

### 보완 뒤 검사 결과

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/dlq-quarantine-and-repair-task.md
```

- 실제 실행 경로는 `C:/Program Files/Git/bin/bash.exe`다.
- 최종 검색 원문 편집 뒤 1회 실행했다. exit 0, violations=0, CROSS VIOLATION 출력 0, MASK 출력 0.
- 최종 312줄·19절·69자식, 최대 자식 159자·부모 369자다. 추가 절은 각각 부모 341·352·369자, 최장 제목은 27자다.
- 검사 출력에서 새 출처·단계·사유 코드·UNKNOWN_EQP·UNKNOWN_REASON이 소실 없이 남았다. 코드가 지워지는 이름/자격 값 형식의 표 머리글을 사용하지 않았다.
- 관련 시험은 원문 기대값만 읽었다: BusinessInboundKafkaSubscriberRejectTest @ 7db24f7466bd L35~57의 거부 시 submit 0회, BusinessKafkaInboundRecordMapperTest L86~137의 WM 계약 거부, BusinessUiTaskDlqReportEndToEndTest L84~102의 BUSINESS_UI_PIPELINE·PROCESS·PROCESS_FAILED 기록. 실행 시험 PASS로 기록하지 않는다.
- 독립 AI 호출·제품 시험·서버 접속·커밋·업로드·임시 자원 생성은 하지 않았다. 정리할 소유 임시 자원도 없다.
- 보완 검사 wall time은 명령 응답 기준 8.071초다. provider 사용량·비용은 미확인이다.
- 작성 기록 patch의 첫 적용은 뒤쪽 줄 참조 수정 뒤 앞쪽 메타를 찾는 hunk 순서로 실패했다. 대상 메타·첫 대조 행·삽입 위치를 다시 읽고 원문 순서로 맞춘 patch를 적용했다. 첫 실패는 파일을 변경하지 않았으며 검사기나 정책을 바꾸지 않았다.

마지막 출력 10줄:

```text
    L296-296 문단 65  | 담당자의 처리 후 「새로고침」으로 남은 행을 확인한다. 행의 유무와 함께 담당자가 확인한 실제 복구 결과도 확인한다.
§ L298-312 [21] 범주별 목록의 기본 실패 문구 구별하기   parent=262 children=5 sources=3
    L305-305 문단 56  | 목록 응답을 해석하지 못하거나 실패 응답에 오류 설명이 없을 때는 범주별 기본 실패 문구를 표시한다.
    L309-309 표 행 53  | 범주=Gateway DLQ | 기본 실패 문구=Gateway DLQ 목록을 조회하지 못했습니다.
    L310-310 표 행 55  | 범주=Business DLQ | 기본 실패 문구=Business DLQ 목록을 조회하지 못했습니다.
    L311-311 표 행 41  | 범주=격리 설비 | 기본 실패 문구=격리 설비 목록을 조회하지 못했습니다.
    L312-312 표 행 53  | 범주=Repair Task | 기본 실패 문구=Repair Task 목록을 조회하지 못했습니다.
SUMMARY sections=19 children=69 maxChild=159 maxParent=369
CORPUS docs=10/53 sections=190 part={user=10} type={사용 절차=10}
TOTAL files=10 targets=1 violations=0
```

### 보완 뒤 사람 검사

19절 전부를 읽었다. 기존 16절 본문은 보존 해시와 이전 사실 대조를 재사용했다. 새 세 절과 기존 라인 범위·Business 위치 안내의 연결을 직접 대조했다.

| 추가 절 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 중복·경계 |
|---|---|---|---|---|
| Business 수신 거부 항목의 원본 찾기 | 수신 거부의 원본 인계 질문 | 고정 출처·단계·코드·설비와 위치값 대조 | 설비 ID 대신 원본 위치로 인계 | UNKNOWN_EQP가 설비 목록에 없을 때 모든 선택 라인에서 통과하는 기존 조건 유지 |
| Business UI 요청의 실패 단계 구분하기 | UI 단계별 실패 질문 | 실제 세 조합과 기본값 대조 | 출처·설비·단계별 이유 포함 | Gateway 코드 설명을 복사하지 않고 UI 처리의 사용처로 설명 |
| Business 업무 실행의 최종 실패 분류 읽기 | 업무 실행 최종 분류 질문 | enum·예외 분류·정책·생성 코드 대조 | 출처·단계·설비·코드·사유 포함 | 정상 WORKFLOW_NOT_FOUND를 실패 코드로 제시하지 않음 |

새 사실은 이름의 번역 대신 실제 생성·분류 경로로 대조했다. 추가 세 절은 제목만으로 대상이 드러나고, 기존 Business 열 안내와 함께 읽어도 조건이 충돌하지 않는다. user 본문에 내부 접근 명령·admin 문서 안내를 추가하지 않았다. 결함 1건은 자체검사 기준으로 보완했으며 메인 세션의 재검증 판정은 아직 없다.


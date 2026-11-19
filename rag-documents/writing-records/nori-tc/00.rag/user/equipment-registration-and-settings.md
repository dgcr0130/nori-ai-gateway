# 작성 기록: 설비 등록·설정 변경·삭제

## 대상

- objectKey: `00.rag/user/equipment-registration-and-settings.md`
- 구획·필요 권한: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문 SHA-256: `1e6dfaac3f60de57f6e8e0523b56d29e1e81849edf088c4207ce67619bd16ae1`
- 검색 원문 줄 수: 445
- 검색 원문 절 수: 27
- 확인일: 2026-11-19 (한국 시간)
- 범위: 독립 작성 세션이 직접 조사·작성·자체 검사한 문서 한 개. 다른 Agent 호출·제품 코드 변경·git add/commit/push·업로드·서버 접속은 수행하지 않았다.

## 원천 판본

다음 표는 최초 작성·이전 보완 시점의 원천 판본 확인 이력이다. 당시 명령별 safe.directory만 지정했고 전역 설정은 바꾸지 않았다. 2026-11-19 제품 수정 C 반영 때 작업 트리 변경 파일을 다시 확인한 결과와 SHA-256 판본은 마지막 「제품 수정 C 반영」 절에 따로 기록한다. 바뀌지 않은 사실의 기존 commit 원천은 보존한다.

```text
git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C C:/workspace_nori/nori-tc/<저장소> rev-parse --short=12 HEAD
git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C C:/workspace_nori/nori-tc/<저장소> status --short -- <인용한 정확한 경로 목록>
```

| 저장소 | 이전 확인 HEAD | 당시 인용한 고유 파일 수 | 당시 status 확인 |
|---|---|---:|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 25 | 출력 없음·exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 13 | 출력 없음·exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 5 | 출력 없음·exit 0 |
| nori-tc-common | 6fe71f6b2da6 | 2 | 출력 없음·exit 0 |
| nori-tc-gateway | d3636ab86763 | 7 | 출력 없음·exit 0 |

원천별 정확한 경로는 아래 각 절의 표에 모두 실었다. status stderr에는 전역 ignore 파일 접근 경고가 있었으며 파일 변경 출력과 구분했다. 형제 저장소의 소유자가 현재 샌드박스 사용자와 달라 기본 Git 읽기가 거부된 것은 아래 검사 경위에 보존했다.

사전 읽기: `docs/rag-document-writing-rules.md` 전체와 `docs/tc-rag-document-list.md` 전체, U=`nori-tc-exec/docs/completion-report/07-user-manual.md` §4.1~4.4(L469~732)를 읽었다. 프론트의 화면 렌더·실제 한국어 문구·API, 백엔드 요청 DTO·중첩 DTO·controller·검증기·조정 접수·DB 저장 경로·기본값, 공통 저장소 검증·기본 스키마·권한 catalog/시드를 대조했다.

기존 `login-and-screen-basics.md`, `live-map-status.md`, `live-map-layout-editing.md`의 관련 절도 읽고 경계를 대조했다. 제품 저장소는 main이며 시작 시 기존 `rag-documents/`가 미추적 상태였다. 앞선 문서를 고치지 않았다.

읽은 관련 시험은 실행 PASS로 세지 않았다. 프론트 `tests/unit/eqp-operation-acceptance.test.ts` L96~134의 202 접수 결과·요청 키 기대값, 백엔드 `JpaEqpCrudPortTest.java` L974~1091의 PASSIVE 충돌·미등록 라인 저장 차단 기대값, `EqpManagementCommandValidatorPlcTest.java` L32~57의 PLC 조합 수용·거부 기대값을 읽었다. 각 저장소 판본은 위와 같다.

## 절별 사실 대조

각 문장과 표 자료 행을 아래 표에 하나씩 대응시켰다. 한 원문 줄에 여러 문장이 있으면 문장별 행으로 나눴다. 각 행에는 해당 절의 확인한 원천 위치 전부를 병기했으며, 화면의 조작·조건·처리·결과를 함께 대조했다. 인계 안내는 실제 담당자나 현장 성공을 가정하지 않고 확인한 입력 칸·실패 결과에 대응하는 사용자 행동이다.

### 1. 설비 작업에 필요한 권한 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L24 | 설비 화면에 들어가려면 EQP_READ가 필요하다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 화면 권한 L93~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 기본 권한 L123~158 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 결과 조회 기본 권한 L15~23 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·결과 조회 권한 시드 L764~768 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 권한 시드 L14~23 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~27 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 권한으로 숨기지 않는 작업 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368 | 화면 EQP_READ 가드와 DB API 인가(anyMatch), 기본 catalog·시드, 작업 메뉴에 쓰기 권한 숨김 조건이 없는 것을 대조했다. 실제 계정 권한은 조회하지 않았다. |
| L24 | 저장소 기본 권한 데이터 기준으로 등록·수정·삭제 요청에는 EQP_WRITE, 라인 목록에는 LINE_READ, 요청 뒤 결과 조회에는 ASYNC_READ가 필요하다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 화면 권한 L93~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 기본 권한 L123~158 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 결과 조회 기본 권한 L15~23 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·결과 조회 권한 시드 L764~768 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 권한 시드 L14~23 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~27 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 권한으로 숨기지 않는 작업 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368 | 화면 EQP_READ 가드와 DB API 인가(anyMatch), 기본 catalog·시드, 작업 메뉴에 쓰기 권한 숨김 조건이 없는 것을 대조했다. 실제 계정 권한은 조회하지 않았다. |
| L26 | 설비 API의 조회는 EQP_READ·EQP_WRITE 중 하나로 허용되지만, 화면 진입에는 EQP_READ가 따로 필요하다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 화면 권한 L93~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 기본 권한 L123~158 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 결과 조회 기본 권한 L15~23 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·결과 조회 권한 시드 L764~768 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 권한 시드 L14~23 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~27 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 권한으로 숨기지 않는 작업 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368 | 화면 EQP_READ 가드와 DB API 인가(anyMatch), 기본 catalog·시드, 작업 메뉴에 쓰기 권한 숨김 조건이 없는 것을 대조했다. 실제 계정 권한은 조회하지 않았다. |
| L28 | 등록·수정·삭제 메뉴는 EQP_WRITE가 없어도 표시되며, 실행하면 서버가 권한을 검사한다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 화면 권한 L93~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 기본 권한 L123~158 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 결과 조회 기본 권한 L15~23 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·결과 조회 권한 시드 L764~768 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 권한 시드 L14~23 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~27 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 권한으로 숨기지 않는 작업 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368 | 화면 EQP_READ 가드와 DB API 인가(anyMatch), 기본 catalog·시드, 작업 메뉴에 쓰기 권한 숨김 조건이 없는 것을 대조했다. 실제 계정 권한은 조회하지 않았다. |
| L30 | 권한 거부가 나오면 입력값을 바꾸기 전에 운영 관리자에게 실행한 작업과 거부 문구를 전달해 권한을 확인한다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 화면 권한 L93~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 기본 권한 L123~158 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 결과 조회 기본 권한 L15~23 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·결과 조회 권한 시드 L764~768 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 권한 시드 L14~23 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~27 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 권한으로 숨기지 않는 작업 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368 | 화면 EQP_READ 가드와 DB API 인가(anyMatch), 기본 catalog·시드, 작업 메뉴에 쓰기 권한 숨김 조건이 없는 것을 대조했다. 실제 계정 권한은 조회하지 않았다. |
| L30 | 현장 계정의 실제 부여 권한은 기본 데이터와 다를 수 있다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 화면 권한 L93~95 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 기본 권한 L123~158 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 결과 조회 기본 권한 L15~23 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·결과 조회 권한 시드 L764~768 / nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 권한 시드 L14~23 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~27 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 권한으로 숨기지 않는 작업 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368 | 화면 EQP_READ 가드와 DB API 인가(anyMatch), 기본 catalog·시드, 작업 메뉴에 쓰기 권한 숨김 조건이 없는 것을 대조했다. 실제 계정 권한은 조회하지 않았다. |

### 2. Gateway와 Business에서 작업 대상 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L42 | 1. 상단바에서 대상 라인을 고른 뒤 좌측 메뉴의 「설비」를 연다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L42 | 목록은 선택 라인에 속한 설비로 좁혀진다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L43 | 2. 설비를 등록하거나 고치려면 「Gateway」 카드를 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L43 | 좌측 「설비 정보」에서 그룹을 펼치고 설비 ID를 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L44 | 3. 「Eqp 검색」에는 설비 ID 일부를 입력한다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L44 | 영문 대소문자를 구별하지 않으며 검색 중에는 일치하는 설비가 있는 그룹만 펼쳐 보인다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L46 | 「Business」 카드는 참조 모델을 보는 관점이다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L46 | 좌측 제목은 「모델 정보」, 검색 칸은 「business_app 검색」으로 바뀌며 그룹 이름으로 검색한다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |
| L46 | 설비 등록·수정·삭제 메뉴는 「Gateway」에서 연다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488 | 선택 라인 필터, 그룹 펼침·설비 ID 선택, 두 관점의 검색 대상과 한국어 문구를 대조했다. Business 트리에는 설비 작업 메뉴가 없다. |

### 3. 목록 표와 한 대의 상세에서 확인할 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L57 | 「Gateway」 목록의 열은 「EQPID」·「Business App」·「Comm Interface」·「Comm Mode」·「Socket Protocol」·「Route Partition」·「IP」·「Port」·「Enabled」·「Model」·「Model Version」·「Gateway Jarfile」·「Business Jarfile」 순서다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 열 L124~195 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 열 L111~154 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 목록·상세·높이 조절 L1186~1247 / nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 파라미터 표 제목 L130~141 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 제목 L48~50 | 목록 열 배열의 순서, 한 대의 상세 열 배열·JAR 고정 대시, 위아래 패널과 표 제목·구분선·스크롤 구조를 대조했다. |
| L59 | 설비 한 대를 누르면 위쪽 「설비 정보」와 아래쪽 「설비 파라미터」가 열린다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 열 L124~195 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 열 L111~154 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 목록·상세·높이 조절 L1186~1247 / nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 파라미터 표 제목 L130~141 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 제목 L48~50 | 목록 열 배열의 순서, 한 대의 상세 열 배열·JAR 고정 대시, 위아래 패널과 표 제목·구분선·스크롤 구조를 대조했다. |
| L59 | 사이의 가로선을 끌어 높이를 조절한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 열 L124~195 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 열 L111~154 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 목록·상세·높이 조절 L1186~1247 / nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 파라미터 표 제목 L130~141 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 제목 L48~50 | 목록 열 배열의 순서, 한 대의 상세 열 배열·JAR 고정 대시, 위아래 패널과 표 제목·구분선·스크롤 구조를 대조했다. |
| L61 | 한 대의 「설비 정보」에는 「Socket Protocol」 열이 없고 두 Jarfile 열은 「—」로 표시된다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 열 L124~195 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 열 L111~154 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 목록·상세·높이 조절 L1186~1247 / nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 파라미터 표 제목 L130~141 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 제목 L48~50 | 목록 열 배열의 순서, 한 대의 상세 열 배열·JAR 고정 대시, 위아래 패널과 표 제목·구분선·스크롤 구조를 대조했다. |
| L61 | Socket 설정과 JAR 파일은 「Gateway」 목록 표에서 확인한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 열 L124~195 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 열 L111~154 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 목록·상세·높이 조절 L1186~1247 / nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 파라미터 표 제목 L130~141 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 제목 L48~50 | 목록 열 배열의 순서, 한 대의 상세 열 배열·JAR 고정 대시, 위아래 패널과 표 제목·구분선·스크롤 구조를 대조했다. |
| L61 | 오른쪽 열이 가려지면 표를 가로로 스크롤한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 열 L124~195 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 열 L111~154 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 목록·상세·높이 조절 L1186~1247 / nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 파라미터 표 제목 L130~141 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 제목 L48~50 | 목록 열 배열의 순서, 한 대의 상세 열 배열·JAR 고정 대시, 위아래 패널과 표 제목·구분선·스크롤 구조를 대조했다. |


### 4. 한 대의 설비 정보에서 모델이 —로 표시될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L76 | 한 대의 「설비 정보」의 「Model」·「Model Version」은 연결 모델이 없거나 모델 조회 결과가 없으면 「—」로 표시된다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 모델 조회 조건 L160~164 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 조회 결과 전달 L1221~1230 / nori-tc-ui-front/src/features/eqp/hooks/useEqpModelInfo.ts @ 9fe69a75dcd3 ; 모델 정보 조회 L9~16 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 모델 단건 API L389~402 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 모델 값 대체 표시 L147~151 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 연결 모델 부재 L56~67 | 양수 모델 키일 때만 별도 조회하며 반환 데이터가 없으면 EqpInfoTable은 두 값을 대시로 대체한다. 이 쿼리 참조 3곳은 생성·modelInfo 전달·isFetching뿐이어서 별도 오류 문구 렌더가 없다. |
| L76 | 조회가 실패해도 별도 오류 문구는 나오지 않는다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 모델 조회 조건 L160~164 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 조회 결과 전달 L1221~1230 / nori-tc-ui-front/src/features/eqp/hooks/useEqpModelInfo.ts @ 9fe69a75dcd3 ; 모델 정보 조회 L9~16 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 모델 단건 API L389~402 / nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 모델 값 대체 표시 L147~151 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 연결 모델 부재 L56~67 | 양수 모델 키일 때만 별도 조회하며 반환 데이터가 없으면 EqpInfoTable은 두 값을 대시로 대체한다. 이 쿼리 참조 3곳은 생성·modelInfo 전달·isFetching뿐이어서 별도 오류 문구 렌더가 없다. |
| L78 | 저장소 기본 권한 데이터 기준으로 MODEL_READ·MODEL_WRITE가 모두 없으면 모델 조회가 거부된다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 보유 권한 검사 L160~174 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 모델 API 기본 권한 L771~774 | 기본 시드의 MODEL_READ GET PREFIX와 MODEL_WRITE 전체 메서드 매핑 및 보유 권한 anyMatch를 대조했다. 둘 다 없으면 모델 API는 거부되므로 실제 계정 권한을 운영 관리자에게 확인하는 절차를 썼다. |
| L78 | 두 열이 「—」이면 운영 관리자에게 모델 조회 권한을 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 보유 권한 검사 L160~174 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 모델 API 기본 권한 L771~774 | 기본 시드의 MODEL_READ GET PREFIX와 MODEL_WRITE 전체 메서드 매핑 및 보유 권한 anyMatch를 대조했다. 둘 다 없으면 모델 API는 거부되므로 실제 계정 권한을 운영 관리자에게 확인하는 절차를 썼다. |
| L80 | 값은 「Gateway」 목록 표의 「Model」·「Model Version」에서도 확인한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록의 모델 값 L179~183 | Gateway 목록의 같은 두 열은 관리 상세의 modelBinding에서 읽는다. 모델 단건 조회 권한에 종속된 상세 표와 구분해 목록에서 확인하도록 안내했다. |
| L80 | 목록 표는 모델 단건 조회와 별도로 설비 관리 상세의 모델 정보를 표시한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록의 모델 값 L179~183 | Gateway 목록의 같은 두 열은 관리 상세의 modelBinding에서 읽는다. 모델 단건 조회 권한에 종속된 상세 표와 구분해 목록에서 확인하도록 안내했다. |

### 5. Business의 모델 표가 비어 있을 때 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L92 | 「Business」 표에서는 「Model Name」·「Model Version」·「status」·「description」·「updated_by」·「updated_at」을 확인한다. | nori-tc-ui-front/src/features/eqp/components/BusinessGroupModelTable.tsx @ 9fe69a75dcd3 ; 모델 열 L73~85 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 모델 조회 L149~150 / nori-tc-ui-front/src/features/model/hooks/useModelList.ts @ 9fe69a75dcd3 ; useModelList L9~15 / nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; getModelList L176~194 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 조회 권한 L240~257 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 모델 권한 시드 L771~774 | Business의 모델 표 렌더, useModelList에서 /model로 이어지는 조회와 기본 MODEL_READ·MODEL_WRITE 매핑을 대조했다. |
| L94 | 저장소 기본 권한 데이터 기준으로 이 표의 모델 목록 조회에는 MODEL_READ·MODEL_WRITE 중 하나가 필요하다. | nori-tc-ui-front/src/features/eqp/components/BusinessGroupModelTable.tsx @ 9fe69a75dcd3 ; 모델 열 L73~85 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 모델 조회 L149~150 / nori-tc-ui-front/src/features/model/hooks/useModelList.ts @ 9fe69a75dcd3 ; useModelList L9~15 / nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; getModelList L176~194 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 조회 권한 L240~257 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 모델 권한 시드 L771~774 | Business의 모델 표 렌더, useModelList에서 /model로 이어지는 조회와 기본 MODEL_READ·MODEL_WRITE 매핑을 대조했다. |
| L94 | 모델 표가 비어 있으면 선택 라인과 그룹을 확인하고, 모델 조회 권한도 운영 관리자에게 확인한다. | nori-tc-ui-front/src/features/eqp/components/BusinessGroupModelTable.tsx @ 9fe69a75dcd3 ; 모델 열 L73~85 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 모델 조회 L149~150 / nori-tc-ui-front/src/features/model/hooks/useModelList.ts @ 9fe69a75dcd3 ; useModelList L9~15 / nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; getModelList L176~194 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 조회 권한 L240~257 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 모델 권한 시드 L771~774 | Business의 모델 표 렌더, useModelList에서 /model로 이어지는 조회와 기본 MODEL_READ·MODEL_WRITE 매핑을 대조했다. |

### 6. Enabled 색 점과 그룹 아래 표시 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L103 | 「Enabled」의 점에 마우스를 올려 표시 문구를 읽는다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598 | 색 점의 클래스·title 및 i18n, 미점유·spare badge, podName이 없을 때 spare에 sessionId를 쓰는 분기를 대조했다. 현장 승계 실측 주장은 쓰지 않았다. |
| L103 | 초록은 「RUN」, 파랑은 「IDLE」, 노랑은 「주의 상태」, 회색은 「오프라인」으로 표시된다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598 | 색 점의 클래스·title 및 i18n, 미점유·spare badge, podName이 없을 때 spare에 sessionId를 쓰는 분기를 대조했다. 현장 승계 실측 주장은 쓰지 않았다. |
| L105 | Gateway 그룹 아래에는 점유 Pod 이름을 표시한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598 | 색 점의 클래스·title 및 i18n, 미점유·spare badge, podName이 없을 때 spare에 sessionId를 쓰는 분기를 대조했다. 현장 승계 실측 주장은 쓰지 않았다. |
| L105 | 대기 노드에서는 Pod 이름이 없으면 세션 식별자를 표시한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598 | 색 점의 클래스·title 및 i18n, 미점유·spare badge, podName이 없을 때 spare에 sessionId를 쓰는 분기를 대조했다. 현장 승계 실측 주장은 쓰지 않았다. |
| L105 | 「미점유」가 나오면 점유 인스턴스가 없으므로 그룹과 설비 ID를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598 | 색 점의 클래스·title 및 i18n, 미점유·spare badge, podName이 없을 때 spare에 sessionId를 쓰는 분기를 대조했다. 현장 승계 실측 주장은 쓰지 않았다. |
| L107 | 「spare」는 살아 있으나 그룹을 점유하지 않은 대기 Pod 표시다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598 | 색 점의 클래스·title 및 i18n, 미점유·spare badge, podName이 없을 때 spare에 sessionId를 쓰는 분기를 대조했다. 현장 승계 실측 주장은 쓰지 않았다. |
| L107 | 점유 Pod이 빠지면 대기 Pod이 슬롯을 이어받는다는 안내를 마우스로 확인할 수 있다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598 | 색 점의 클래스·title 및 i18n, 미점유·spare badge, podName이 없을 때 spare에 sessionId를 쓰는 분기를 대조했다. 현장 승계 실측 주장은 쓰지 않았다. |

### 7. 설비 목록이나 관리 상세를 불러오지 못했을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L120 | Gateway 목록 아래에 「설비 상세 정보를 불러오지 못했습니다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 오류·다시 시도 L425~443 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 오류 문구 선택 L1063~1088 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 목록 오류 L349~354 / nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구·대체 문구 L19~77 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L484~491 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류·단추 L55~57 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 관리 상세 오류 대처 L698~698 | 조회 오류 문구의 대체 조건과 렌더 위치, 같은 영역의 다시 시도 핸들러, 관리 상세 오류의 닫기·재열기 절차를 대조했다. |
| L120 | 자동 재시도가 모두 실패했습니다.」가 나오면 옆의 「다시 시도」를 누른다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 오류·다시 시도 L425~443 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 오류 문구 선택 L1063~1088 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 목록 오류 L349~354 / nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구·대체 문구 L19~77 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L484~491 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류·단추 L55~57 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 관리 상세 오류 대처 L698~698 | 조회 오류 문구의 대체 조건과 렌더 위치, 같은 영역의 다시 시도 핸들러, 관리 상세 오류의 닫기·재열기 절차를 대조했다. |
| L122 | 좌측 목록의 조회 실패에는 서버 오류 문구가 우선 나오며, 문구가 없으면 「설비 목록을 조회하지 못했습니다.」가 나온다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 오류·다시 시도 L425~443 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 오류 문구 선택 L1063~1088 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 목록 오류 L349~354 / nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구·대체 문구 L19~77 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L484~491 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류·단추 L55~57 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 관리 상세 오류 대처 L698~698 | 조회 오류 문구의 대체 조건과 렌더 위치, 같은 영역의 다시 시도 핸들러, 관리 상세 오류의 닫기·재열기 절차를 대조했다. |
| L122 | 화면을 새로고침해 다시 확인한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 오류·다시 시도 L425~443 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 오류 문구 선택 L1063~1088 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 목록 오류 L349~354 / nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구·대체 문구 L19~77 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L484~491 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류·단추 L55~57 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 관리 상세 오류 대처 L698~698 | 조회 오류 문구의 대체 조건과 렌더 위치, 같은 영역의 다시 시도 핸들러, 관리 상세 오류의 닫기·재열기 절차를 대조했다. |
| L124 | 수정 창에서 「설비 관리 상세 정보를 조회하지 못했습니다.」가 나오면 창을 닫고 다시 연다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 오류·다시 시도 L425~443 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 오류 문구 선택 L1063~1088 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 목록 오류 L349~354 / nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구·대체 문구 L19~77 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L484~491 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류·단추 L55~57 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 관리 상세 오류 대처 L698~698 | 조회 오류 문구의 대체 조건과 렌더 위치, 같은 영역의 다시 시도 핸들러, 관리 상세 오류의 닫기·재열기 절차를 대조했다. |
| L124 | 반복되면 라인·설비 ID·발생 시각·화면 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 오류·다시 시도 L425~443 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 오류 문구 선택 L1063~1088 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 목록 오류 L349~354 / nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구·대체 문구 L19~77 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L484~491 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류·단추 L55~57 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 관리 상세 오류 대처 L698~698 | 조회 오류 문구의 대체 조건과 렌더 위치, 같은 영역의 다시 시도 핸들러, 관리 상세 오류의 닫기·재열기 절차를 대조했다. |

### 8. 새 설비 등록 창 열기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L135 | 1. 「Gateway」 관점의 좌측 트리에서 설비 노드가 없는 빈 곳을 마우스 오른쪽 단추로 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L274~279 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 통신 방식 선택 L357~368 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 창·스크롤 L817~839 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록 구역 구성 L971~1372 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 하단 단추 L1385~1403 | Gateway 트리 컨테이너의 등록 메뉴, 세 통신 방식의 대소문자·호출 값, 등록 창 구역·스크롤·고정 footer를 대조했다. |
| L136 | 2. 「SECS Eqp Create」·「Socket Eqp Create」·「PLC Eqp Create」 중 등록할 통신 방식을 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L274~279 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 통신 방식 선택 L357~368 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 창·스크롤 L817~839 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록 구역 구성 L971~1372 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 하단 단추 L1385~1403 | Gateway 트리 컨테이너의 등록 메뉴, 세 통신 방식의 대소문자·호출 값, 등록 창 구역·스크롤·고정 footer를 대조했다. |
| L137 | 3. 등록 창에서 「공통 정보」·「Model / Business」·「Log Policy」·통신 방식별 설정 구역을 아래로 스크롤하며 채운다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L274~279 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 통신 방식 선택 L357~368 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 창·스크롤 L817~839 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록 구역 구성 L971~1372 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 하단 단추 L1385~1403 | Gateway 트리 컨테이너의 등록 메뉴, 세 통신 방식의 대소문자·호출 값, 등록 창 구역·스크롤·고정 footer를 대조했다. |
| L139 | 「Save」와 「Cancel」은 창 아래쪽에 고정된다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L274~279 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 통신 방식 선택 L357~368 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 창·스크롤 L817~839 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록 구역 구성 L971~1372 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 하단 단추 L1385~1403 | Gateway 트리 컨테이너의 등록 메뉴, 세 통신 방식의 대소문자·호출 값, 등록 창 구역·스크롤·고정 footer를 대조했다. |
| L139 | 등록 요청 전 그만두려면 「Cancel」을 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L274~279 / nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 통신 방식 선택 L357~368 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 창·스크롤 L817~839 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록 구역 구성 L971~1372 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 하단 단추 L1385~1403 | Gateway 트리 컨테이너의 등록 메뉴, 세 통신 방식의 대소문자·호출 값, 등록 창 구역·스크롤·고정 footer를 대조했다. |

### 9. 공통 정보의 필수값 채우기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L151 | \| EQP ID \| 필수다. 저장소 기본 스키마 기준 64자 이내이며 등록 뒤 수정할 수 없다. \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434 | 화면 필수 검사·읽기 전용 분기·숫자 파싱, 요청 DTO의 필수/양수/0 하한, 기본 스키마 길이·포트 제한을 대조했다. 기본 스키마를 현장 적용 상태로 단정하지 않았다. |
| L152 | \| 라인 \| 등록된 라인을 목록에서 고른다. \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434 | 화면 필수 검사·읽기 전용 분기·숫자 파싱, 요청 DTO의 필수/양수/0 하한, 기본 스키마 길이·포트 제한을 대조했다. 기본 스키마를 현장 적용 상태로 단정하지 않았다. |
| L153 | \| Comm Interface \| 등록 메뉴에서 고른 방식으로 고정된다. \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434 | 화면 필수 검사·읽기 전용 분기·숫자 파싱, 요청 DTO의 필수/양수/0 하한, 기본 스키마 길이·포트 제한을 대조했다. 기본 스키마를 현장 적용 상태로 단정하지 않았다. |
| L154 | \| Comm Mode \| ACTIVE·PASSIVE 중 고른다. PLC는 ACTIVE로 고정된다. \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434 | 화면 필수 검사·읽기 전용 분기·숫자 파싱, 요청 DTO의 필수/양수/0 하한, 기본 스키마 길이·포트 제한을 대조했다. 기본 스키마를 현장 적용 상태로 단정하지 않았다. |
| L155 | \| Route Partition \| 0 이상 정수다. \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434 | 화면 필수 검사·읽기 전용 분기·숫자 파싱, 요청 DTO의 필수/양수/0 하한, 기본 스키마 길이·포트 제한을 대조했다. 기본 스키마를 현장 적용 상태로 단정하지 않았다. |
| L156 | \| EQP IP \| 필수다. 저장소 기본 스키마 기준 45자 이내로 입력한다. \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434 | 화면 필수 검사·읽기 전용 분기·숫자 파싱, 요청 DTO의 필수/양수/0 하한, 기본 스키마 길이·포트 제한을 대조했다. 기본 스키마를 현장 적용 상태로 단정하지 않았다. |
| L157 | \| EQP Port \| 필수 정수다. 저장소 기본 스키마 기준 1~65535로 입력한다. \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434 | 화면 필수 검사·읽기 전용 분기·숫자 파싱, 요청 DTO의 필수/양수/0 하한, 기본 스키마 길이·포트 제한을 대조했다. 기본 스키마를 현장 적용 상태로 단정하지 않았다. |

### 10. 등록할 모델과 JAR 고르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L169 | 「Is Dev」를 「사용」으로 켜면 DEVELOP, 「미사용」으로 끄면 OPERATE 모델 중 통신 방식이 일치하는 모델만 나온다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |
| L169 | 「허용 모델 상태」와 「선택 상태」로 조건을 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |
| L171 | 「Model Name」·「Model Version」은 필수다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |
| L171 | 조건에 맞는 첫 모델과 버전이 자동 선택될 수 있으므로 원하는 값인지 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |
| L171 | 옵션이 없다는 안내가 나오면 「Is Dev」를 확인하거나 모델 담당자에게 모델 준비를 요청한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |
| L173 | 선택 사항인 「Gateway Jar」·「Business Jar」는 등록된 목록에서 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |
| L173 | 사용하지 않으면 「미선택」으로 둔다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |
| L173 | 등록 창에는 파일 업로드 칸이 없으므로 필요한 JAR가 목록에 없으면 운영 담당자에게 등록을 요청한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578 | 개발/운영·통신 방식 모델 필터, 첫 모델·버전 자동 선택, 필수 버전, 상태 안내·토글 문구, JAR 목록·미선택·업로드 컨트롤 부재를 대조했다. |

### 11. Log Policy 입력과 보관 일수 제한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L183 | 「Log Level」은 INFO·DEBUG·TRACE·WARN·ERROR 중 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 로그 입력 L1064~1110 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 로그 초기값 L19~26 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpLogJpaStore.java @ 6fe71f6b2da6 ; 보관 일수 검증 L142~148 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 로그 빈값 보완 L929~959 | 로그 선택지·초기값, 화면의 0 하한과 저장소의 1 하한, 수정 로그 정책의 기존 값 보완을 각각 대조했다. |
| L183 | 등록 창의 초기값은 INFO다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 로그 입력 L1064~1110 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 로그 초기값 L19~26 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpLogJpaStore.java @ 6fe71f6b2da6 ; 보관 일수 검증 L142~148 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 로그 빈값 보완 L929~959 | 로그 선택지·초기값, 화면의 0 하한과 저장소의 1 하한, 수정 로그 정책의 기존 값 보완을 각각 대조했다. |
| L185 | 「Log Retention Day」는 정수로 입력하며 초기값은 7이다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 로그 입력 L1064~1110 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 로그 초기값 L19~26 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpLogJpaStore.java @ 6fe71f6b2da6 ; 보관 일수 검증 L142~148 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 로그 빈값 보완 L929~959 | 로그 선택지·초기값, 화면의 0 하한과 저장소의 1 하한, 수정 로그 정책의 기존 값 보완을 각각 대조했다. |
| L185 | 화면은 0 이상을 허용하지만 저장 단계는 1 이상을 요구하므로 보관 일수는 1 이상으로 입력한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 로그 입력 L1064~1110 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 로그 초기값 L19~26 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpLogJpaStore.java @ 6fe71f6b2da6 ; 보관 일수 검증 L142~148 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 로그 빈값 보완 L929~959 | 로그 선택지·초기값, 화면의 0 하한과 저장소의 1 하한, 수정 로그 정책의 기존 값 보완을 각각 대조했다. |
| L187 | 「Log Path」는 로그 경로를 적는 선택 칸이다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 로그 입력 L1064~1110 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 로그 초기값 L19~26 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpLogJpaStore.java @ 6fe71f6b2da6 ; 보관 일수 검증 L142~148 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 로그 빈값 보완 L929~959 | 로그 선택지·초기값, 화면의 0 하한과 저장소의 1 하한, 수정 로그 정책의 기존 값 보완을 각각 대조했다. |
| L187 | 수정할 때 로그 보관 일수나 경로를 비우면 기존 값으로 보완된다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 로그 입력 L1064~1110 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 로그 초기값 L19~26 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpLogJpaStore.java @ 6fe71f6b2da6 ; 보관 일수 검증 L142~148 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 로그 빈값 보완 L929~959 | 로그 선택지·초기값, 화면의 0 하한과 저장소의 1 하한, 수정 로그 정책의 기존 값 보완을 각각 대조했다. |

### 12. SECS Settings의 입력 범위 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L198 | SECS 설비의 「SECS Settings」에서 「Device ID」·「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」·「Link Test Interval」·「Max Message Bytes」를 입력한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력 L1112~1218 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 숫자 검사 L679~692 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpHsmsJpaStore.java @ 6fe71f6b2da6 ; 저장 검증 L302~312 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Device ID 범위 L495~503 | SECS 입력 칸과 토글을 모두 대조했다. 화면 0 하한과 저장소 양수 검사, 기본 스키마 Device ID 상한을 구분했다. |
| L198 | 「Link Test Enabled」는 「사용」·「미사용」으로 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력 L1112~1218 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 숫자 검사 L679~692 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpHsmsJpaStore.java @ 6fe71f6b2da6 ; 저장 검증 L302~312 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Device ID 범위 L495~503 | SECS 입력 칸과 토글을 모두 대조했다. 화면 0 하한과 저장소 양수 검사, 기본 스키마 Device ID 상한을 구분했다. |
| L200 | 숫자 칸은 선택 입력이다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력 L1112~1218 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 숫자 검사 L679~692 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpHsmsJpaStore.java @ 6fe71f6b2da6 ; 저장 검증 L302~312 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Device ID 범위 L495~503 | SECS 입력 칸과 토글을 모두 대조했다. 화면 0 하한과 저장소 양수 검사, 기본 스키마 Device ID 상한을 구분했다. |
| L200 | 값을 넣으면 정수여야 한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력 L1112~1218 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 숫자 검사 L679~692 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpHsmsJpaStore.java @ 6fe71f6b2da6 ; 저장 검증 L302~312 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Device ID 범위 L495~503 | SECS 입력 칸과 토글을 모두 대조했다. 화면 0 하한과 저장소 양수 검사, 기본 스키마 Device ID 상한을 구분했다. |
| L200 | 「Device ID」는 저장소 기본 스키마 기준 0~32767, 나머지 숫자는 저장 단계에서 1 이상이어야 한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력 L1112~1218 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 숫자 검사 L679~692 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpHsmsJpaStore.java @ 6fe71f6b2da6 ; 저장 검증 L302~312 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Device ID 범위 L495~503 | SECS 입력 칸과 토글을 모두 대조했다. 화면 0 하한과 저장소 양수 검사, 기본 스키마 Device ID 상한을 구분했다. |
| L200 | 화면의 0 이상 검사만 통과해도 저장이 거부될 수 있다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력 L1112~1218 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 숫자 검사 L679~692 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpHsmsJpaStore.java @ 6fe71f6b2da6 ; 저장 검증 L302~312 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Device ID 범위 L495~503 | SECS 입력 칸과 토글을 모두 대조했다. 화면 0 하한과 저장소 양수 검사, 기본 스키마 Device ID 상한을 구분했다. |
| L202 | 「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」과 「Link Test Interval」은 초, 「Max Message Bytes」는 바이트 단위로 입력한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 타이머 단위 변환 L56~69 | 현재 공급자가 다섯 타이머와 Link Test Interval에 1000L을 곱하고 maxMsgBytes를 maxFrameBytes로 넘기는 것을 확인했다. 화면 표기·저장 기본값은 보존했다. |

### 13. SECS 저장값과 실제 통신 설정 구분하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L213 | 설비별 HSMS 설정 적용 수정이 들어간 판본에서는 「Device ID」와 저장된 「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」으로 세션 설정을 구성한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 HSMS 세션 설정 L41~69 | Device ID 선택과 설비별 HSMS 설정을 세션 설정으로 전달하는 흐름을 직접 읽었다. T3 대기 등록·T5 실행 사용처가 추가됐다고 쓰지 않았다. |
| L215 | 「Link Test Enabled」·「Link Test Interval」·「Max Message Bytes」도 세션의 LINKTEST·최대 프레임 설정으로 쓴다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 HSMS 세션 설정 L41~69 | 설비별 LINKTEST 사용·주기·프레임 상한을 넘기는 분기와 설정 부재 시 toSessionDefaults 분기를 확인했다. |
| L215 | 설비별 HSMS 설정이 없으면 Gateway 기본값을 쓴다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 HSMS 세션 설정 L41~69 | 설비별 LINKTEST 사용·주기·프레임 상한을 넘기는 분기와 설정 부재 시 toSessionDefaults 분기를 확인했다. |
| L217 | 각 타이머의 실제 사용 범위와 기본값 출처는 「SECS·HSMS 메시지·타이머」(`00.rag/user/secs-hsms-message-and-timer-reference.md`)에서 확인한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 HSMS 세션 설정 L41~69 | 타이머별 실제 사용 범위·기본값 상세의 소유 objectKey를 유지했다. 참조 문서 L110·L127~131의 이전 설명은 다른 문서 발견 사항에 남겼다. |

### 14. Socket Settings에서 프로토콜과 숫자 채우기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L231 | Socket 설비는 「Socket Settings」의 「Socket Protocol Type」을 서버가 제공한 목록에서 반드시 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 입력 L1269~1371 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 숫자 검사 L694~708 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 프로토콜 필수 검사 L888~904 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Socket 저장 제한 L506~529 | Socket 프로토콜의 별표 부재·서버 필수 검사, 서버 제공 목록·Charset, 숫자 범위·토글과 기본 스키마 프레임 양수 조건을 대조했다. |
| L231 | 별표가 없어도 비워 저장하면 서버가 거부한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 입력 L1269~1371 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 숫자 검사 L694~708 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 프로토콜 필수 검사 L888~904 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Socket 저장 제한 L506~529 | Socket 프로토콜의 별표 부재·서버 필수 검사, 서버 제공 목록·Charset, 숫자 범위·토글과 기본 스키마 프레임 양수 조건을 대조했다. |
| L231 | 「Charset」에는 문자셋을 입력한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 입력 L1269~1371 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 숫자 검사 L694~708 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 프로토콜 필수 검사 L888~904 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Socket 저장 제한 L506~529 | Socket 프로토콜의 별표 부재·서버 필수 검사, 서버 제공 목록·Charset, 숫자 범위·토글과 기본 스키마 프레임 양수 조건을 대조했다. |
| L233 | 「Heartbeat Interval」·「Read Timeout」·「Write Timeout」은 선택 입력이며 값을 넣으면 0 이상 정수로 쓴다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 입력 L1269~1371 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 숫자 검사 L694~708 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 프로토콜 필수 검사 L888~904 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Socket 저장 제한 L506~529 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 프레임 상한 공급 L45~62 | 화면·저장 검증 조건은 보존하고, 설비별 maxFrameSizeBytes가 maxFrameBytes로 전달되는 코드를 읽어 바이트 단위를 보충했다. |
| L233 | 「Max Frame Size」는 저장소 기본 스키마 기준 1 이상 정수로, 바이트 단위로 쓴다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 입력 L1269~1371 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 숫자 검사 L694~708 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 프로토콜 필수 검사 L888~904 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Socket 저장 제한 L506~529 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 프레임 상한 공급 L45~62 | 화면·저장 검증 조건은 보존하고, 설비별 maxFrameSizeBytes가 maxFrameBytes로 전달되는 코드를 읽어 바이트 단위를 보충했다. |
| L235 | 「Heartbeat Enabled」·「Keep Alive Enabled」는 각각 「사용」·「미사용」으로 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 입력 L1269~1371 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 숫자 검사 L694~708 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 프로토콜 필수 검사 L888~904 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Socket 저장 제한 L506~529 | Socket 프로토콜의 별표 부재·서버 필수 검사, 서버 제공 목록·Charset, 숫자 범위·토글과 기본 스키마 프레임 양수 조건을 대조했다. |

### 15. Charset 저장과 Gateway 통신 실패 구분하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L246 | 「Charset」은 자유 입력이다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Charset 자유 입력 L1293~1300 | Charset이 Input으로 렌더되며 목록·문자셋 이름 검사가 없는 것을 확인했다. |
| L246 | 저장 서버는 문자셋 이름을 검사하지 않고 앞뒤 공백만 제거해 저장하며, 빈 값은 UTF-8로 저장한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; Charset 저장값 결정 L888~904 | 빈 값은 UTF-8, 그 밖에는 trim만 적용해 저장한다. 저장 거부와 Gateway 실행 문맥 생성 실패를 구별했다. |
| L248 | 설비별 Socket 설정 적용 수정이 들어간 판본의 Gateway는 지원하지 않는 문자셋 이름이면 그 설비의 Socket 실행 문맥을 만들 때 설비 ID와 설정값을 담은 오류로 실패한다. | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java @ a5e5221853b7 ; 문자셋 이름 오류 L55~64 / nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; Socket 실행 문맥 생성 L38~62 | create L45의 socketCharset 호출이 문맥 생성보다 먼저 실행된다. Charset.forName 오류는 설비 ID와 입력 설정값을 담아 던지며 화면 표시로 일반화하지 않았다. |
| L250 | 내장 LINE_DELIMITED에는 LF가 단일 0x0A 바이트인 문자셋을 쓴다. | nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; LF 문자셋 조건 L54~63 | 기존 LF 문자셋 조건 문장은 바꾸지 않고 새 절로 함께 옮겼다. |

### 16. Socket 저장값과 실제 통신 설정 구분하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L266 | 설비별 Socket 설정 적용 수정이 들어간 판본은 「Socket Protocol Type」·「Max Frame Size」·「Keep Alive Enabled」를 실행에 반영한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 Socket 설정 L45~62 / nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 ; 내장 처리기 문자셋 적용 L51~63 / nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ a5e5221853b7 ; 플러그인 처리기 우선 L41~71 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ a5e5221853b7 ; ACTIVE Keep Alive 적용 L232~239 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ a5e5221853b7 ; PASSIVE Keep Alive 적용 L299~307 | 설비별 프레임 상한, ACTIVE·PASSIVE SO_KEEPALIVE, 내장 LINE·REGEX 문자셋 적용과 플러그인 우선 반환을 각각 확인했다. |
| L266 | 「Charset」은 내장 LINE_DELIMITED·REGEX_DELIMITED 처리기에 반영하며 플러그인 처리기에는 전달하지 않는다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 Socket 설정 L45~62 / nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 ; 내장 처리기 문자셋 적용 L51~63 / nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ a5e5221853b7 ; 플러그인 처리기 우선 L41~71 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ a5e5221853b7 ; ACTIVE Keep Alive 적용 L232~239 / nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ a5e5221853b7 ; PASSIVE Keep Alive 적용 L299~307 | 설비별 프레임 상한, ACTIVE·PASSIVE SO_KEEPALIVE, 내장 LINE·REGEX 문자셋 적용과 플러그인 우선 반환을 각각 확인했다. |
| L268 | 「Heartbeat Enabled」·「Heartbeat Interval」·「Read Timeout」·「Write Timeout」은 같은 판본에서 저장만 되며 Gateway 실행 설정에 쓰이지 않는다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 Socket 설정 L45~62 / nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/model/EquipmentContextProfile.java @ d3636ab86763 ; SocketSettings 필드 L111~122 | 현재 공급자와 명시적 호출 검색을 함께 대조했다. Heartbeat·Read/Write Timeout은 저장·복사만 하며, Keep Alive는 이제 실행에 반영하므로 미적용 열거에서 뺐다. |
| L270 | 문자셋 적용 범위와 프레임 상한의 상세는 「Socket 프레임·파싱 규칙」(`00.rag/user/socket-framing-and-parsing-reference.md`)에서 확인한다. | nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 Socket 설정 L45~62 / nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 ; 내장 처리기 문자셋 적용 L51~63 / nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ a5e5221853b7 ; 플러그인 처리기 우선 L41~71 | 새 적용 경계에 맞춰 참조 안내의 범위를 조정했다. 참조 문서 L24~28·L82의 이전 설명은 다른 문서 발견 사항에 남겼다. |

### 17. PLC Settings에서 변경할 수 있는 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L280 | PLC 설비의 「Comm Mode」는 ACTIVE, 「PLC Settings」의 「PLC Protocol」은 MODBUS_TCP, 「Transport」는 TCP로 고정된다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; PLC 입력 L1220~1267 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 고정·초기값 L14~18 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 초기값 L52~58 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; PLC 검증 L162~190 | PLC 고정 모드·프로토콜·전송 계층, Unit ID 상한·하한과 나머지 숫자의 양수 검사·초기값을 대조했다. |
| L282 | 선택 입력인 「Unit ID」는 0~255 정수, 「Poll Timeout (ms)」와 「Reconnect Max Retries」는 1 이상 정수로 넣는다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; PLC 입력 L1220~1267 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 고정·초기값 L14~18 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 초기값 L52~58 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; PLC 검증 L162~190 | PLC 고정 모드·프로토콜·전송 계층, Unit ID 상한·하한과 나머지 숫자의 양수 검사·초기값을 대조했다. |
| L282 | 등록 창의 초기값은 각각 1·3000·3이다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; PLC 입력 L1220~1267 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 고정·초기값 L14~18 / nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 초기값 L52~58 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; PLC 검증 L162~190 | PLC 고정 모드·프로토콜·전송 계층, Unit ID 상한·하한과 나머지 숫자의 양수 검사·초기값을 대조했다. |

### 18. 통신 설정의 숫자 칸을 비우면 적용되는 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L291 | 통신 설정의 빈 숫자 칸은 등록과 수정 모두 기본값으로 채워진다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 빈 숫자 처리 L239~258 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 통신값 요청 L763~806 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 통신 기본값 L871~926 | 빈 숫자를 null로 전달하는 파싱과 수정 요청, 등록·수정에서 공통으로 쓰는 프로토콜 기본값 보완 함수를 필드별로 대조했다. |
| L291 | 수정 중 기존 통신값을 유지하려면 숫자 칸을 지우지 않는다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 빈 숫자 처리 L239~258 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 통신값 요청 L763~806 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 통신 기본값 L871~926 | 빈 숫자를 null로 전달하는 파싱과 수정 요청, 등록·수정에서 공통으로 쓰는 프로토콜 기본값 보완 함수를 필드별로 대조했다. |
| L295 | \| SECS Settings \| Device ID 0, T3 Timeout 45, T5 Timeout 10, T6 Timeout 5, T7 Timeout 10, T8 Timeout 5, Link Test Interval 60, Max Message Bytes 10485760 \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 빈 숫자 처리 L239~258 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 통신값 요청 L763~806 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 통신 기본값 L871~926 | 빈 숫자를 null로 전달하는 파싱과 수정 요청, 등록·수정에서 공통으로 쓰는 프로토콜 기본값 보완 함수를 필드별로 대조했다. |
| L296 | \| Socket Settings \| Heartbeat Interval 30, Read Timeout 0, Write Timeout 0, Max Frame Size 8192 \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 빈 숫자 처리 L239~258 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 통신값 요청 L763~806 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 통신 기본값 L871~926 | 빈 숫자를 null로 전달하는 파싱과 수정 요청, 등록·수정에서 공통으로 쓰는 프로토콜 기본값 보완 함수를 필드별로 대조했다. |
| L297 | \| PLC Settings \| Unit ID 1, Poll Timeout (ms) 3000, Reconnect Max Retries 3 \| | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 빈 숫자 처리 L239~258 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 통신값 요청 L763~806 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 통신 기본값 L871~926 | 빈 숫자를 null로 전달하는 파싱과 수정 요청, 등록·수정에서 공통으로 쓰는 프로토콜 기본값 보완 함수를 필드별로 대조했다. |

### 19. 등록 요청을 저장하고 완료 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L311 | 1. 등록 창의 값을 확인한 뒤 「Save」를 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 저장·오류·진행 L1374~1403 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 결과 판정 L138~196 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 등록 뒤 선택 L743~755 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록 결과 문구 L1121~1126 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; create L260~269 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; createEqp L577~599 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 완료 메시지·확인 단추 L94~127 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 라인 필터 L239~243 | Save 뒤 입력 오류·진행 표시, PASS 결과·성공 팝업·선택 핸들러와 확인 단추, 선택 라인 필터를 대조했다. |
| L311 | 입력 오류가 나오면 창 안의 문구가 가리키는 칸을 고친다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 저장·오류·진행 L1374~1403 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 결과 판정 L138~196 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 등록 뒤 선택 L743~755 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록 결과 문구 L1121~1126 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; create L260~269 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; createEqp L577~599 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 완료 메시지·확인 단추 L94~127 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 라인 필터 L239~243 | Save 뒤 입력 오류·진행 표시, PASS 결과·성공 팝업·선택 핸들러와 확인 단추, 선택 라인 필터를 대조했다. |
| L312 | 2. 요청이 접수되면 창 안의 진행 표시를 보며 기다린다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 저장·오류·진행 L1374~1403 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 결과 판정 L138~196 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 등록 뒤 선택 L743~755 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록 결과 문구 L1121~1126 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; create L260~269 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; createEqp L577~599 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 완료 메시지·확인 단추 L94~127 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 라인 필터 L239~243 | Save 뒤 입력 오류·진행 표시, PASS 결과·성공 팝업·선택 핸들러와 확인 단추, 선택 라인 필터를 대조했다. |
| L313 | 3. 「설비 등록 완료」와 대상 설비 ID를 확인하고 「확인」을 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 저장·오류·진행 L1374~1403 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 결과 판정 L138~196 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 등록 뒤 선택 L743~755 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록 결과 문구 L1121~1126 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; create L260~269 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; createEqp L577~599 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 완료 메시지·확인 단추 L94~127 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 라인 필터 L239~243 | Save 뒤 입력 오류·진행 표시, PASS 결과·성공 팝업·선택 핸들러와 확인 단추, 선택 라인 필터를 대조했다. |
| L313 | 등록 성공 뒤에는 새 설비가 선택된다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 저장·오류·진행 L1374~1403 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 결과 판정 L138~196 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 등록 뒤 선택 L743~755 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록 결과 문구 L1121~1126 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; create L260~269 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; createEqp L577~599 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 완료 메시지·확인 단추 L94~127 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 라인 필터 L239~243 | Save 뒤 입력 오류·진행 표시, PASS 결과·성공 팝업·선택 핸들러와 확인 단추, 선택 라인 필터를 대조했다. |
| L315 | 등록한 라인이 현재 상단바의 선택 라인과 다르면 그 라인으로 바꿔 트리를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 저장·오류·진행 L1374~1403 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 결과 판정 L138~196 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 등록 뒤 선택 L743~755 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록 결과 문구 L1121~1126 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; create L260~269 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; createEqp L577~599 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 완료 메시지·확인 단추 L94~127 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 라인 필터 L239~243 | Save 뒤 입력 오류·진행 표시, PASS 결과·성공 팝업·선택 핸들러와 확인 단추, 선택 라인 필터를 대조했다. |

### 20. Eqp Info Update에서 설정 바꾸기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L329 | 1. 「Gateway」 좌측 트리의 대상 설비 ID를 마우스 오른쪽 단추로 눌러 「Eqp Info Update」를 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296 | 수정 메뉴의 대상 ID, 수정 가능한 필드·읽기 전용 필드·필수 DTO, PUT API와 controller·완료 문구를 대조했다. |
| L330 | 2. 현재 값이 열린 뒤 「라인」·「Comm Mode」·「Is Dev」·「Route Partition」·「EQP IP」·「EQP Port」와 로그·통신 설정을 고친다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296 | 수정 메뉴의 대상 ID, 수정 가능한 필드·읽기 전용 필드·필수 DTO, PUT API와 controller·완료 문구를 대조했다. |
| L330 | 「EQP ID」·「Comm Interface」는 읽기 전용이다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296 | 수정 메뉴의 대상 ID, 수정 가능한 필드·읽기 전용 필드·필수 DTO, PUT API와 controller·완료 문구를 대조했다. |
| L331 | 3. 「Save」를 누르고 진행 표시 뒤 「설비 수정 완료」를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296 | 수정 메뉴의 대상 ID, 수정 가능한 필드·읽기 전용 필드·필수 DTO, PUT API와 controller·완료 문구를 대조했다. |
| L331 | 저장 전 취소는 「Cancel」을 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296 | 수정 메뉴의 대상 ID, 수정 가능한 필드·읽기 전용 필드·필수 DTO, PUT API와 controller·완료 문구를 대조했다. |
| L333 | 수정에서도 라인·연결 모드·주소·포트·배정 번호는 필수다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296 | 수정 메뉴의 대상 ID, 수정 가능한 필드·읽기 전용 필드·필수 DTO, PUT API와 controller·완료 문구를 대조했다. |
| L333 | 다른 라인으로 옮겼으면 상단바에서 옮긴 라인을 골라 설비를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296 | 수정 메뉴의 대상 ID, 수정 가능한 필드·읽기 전용 필드·필수 DTO, PUT API와 controller·완료 문구를 대조했다. |

### 21. 수정 창에서 모델과 JAR를 바꾸려는 경우

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L345 | 「Eqp Info Update」에는 「Model / Business」 구역이 표시되지 않아 「Model Name」·「Model Version」·「Business Jar」를 고르는 칸이 없다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록에서만 모델 구역 표시 L971~1062 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Gateway Jar 선택 L949~967 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd ; 기존 연결값 유지 L44~65 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 요청 항목 L25~58 / nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델·Business JAR 배포 입력 L354~373 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 배포 성공값 반영 L25~76 | Model / Business 구역의 create 조건, 남아 있는 Gateway Jar 컨트롤과 서버의 기존 연결값 유지, 배포 입력과 성공 시 모델·Business JAR 반영 경로를 대조했다. |
| L347 | 「Gateway Jar」는 목록에서 다른 값을 고를 수 있지만 이 수정 요청은 기존 Gateway JAR를 유지한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록에서만 모델 구역 표시 L971~1062 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Gateway Jar 선택 L949~967 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd ; 기존 연결값 유지 L44~65 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 요청 항목 L25~58 / nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델·Business JAR 배포 입력 L354~373 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 배포 성공값 반영 L25~76 | Model / Business 구역의 create 조건, 남아 있는 Gateway Jar 컨트롤과 서버의 기존 연결값 유지, 배포 입력과 성공 시 모델·Business JAR 반영 경로를 대조했다. |
| L347 | 모델·Business JAR 변경은 별도의 배포 작업으로 진행하고, Gateway JAR 변경은 운영 담당자에게 대상 설비와 필요한 파일을 전달한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록에서만 모델 구역 표시 L971~1062 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Gateway Jar 선택 L949~967 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd ; 기존 연결값 유지 L44~65 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 요청 항목 L25~58 / nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델·Business JAR 배포 입력 L354~373 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 배포 성공값 반영 L25~76 | Model / Business 구역의 create 조건, 남아 있는 Gateway Jar 컨트롤과 서버의 기존 연결값 유지, 배포 입력과 성공 시 모델·Business JAR 반영 경로를 대조했다. |

### 22. 서버가 등록·수정을 거부할 때 확인할 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L358 | 등록에서는 같은 설비 ID가 이미 있거나, 모델이 없거나 통신 방식·개발/운영 조건이 맞지 않으면 서버가 거부한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12 | 중복 ID·모델 존재/상태/방식·라인 등록·선택 JAR 존재 검사를 대조했다. 입력 오류 문구 전달을 확인했으며 IDEMPOTENCY_CONFLICT의 별도 번역 분기 때문에 모든 서버 문구가 그대로 나온다고 일반화하지 않았다. |
| L358 | 라인과 선택한 JAR도 서버에 등록되어 있어야 한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12 | 중복 ID·모델 존재/상태/방식·라인 등록·선택 JAR 존재 검사를 대조했다. 입력 오류 문구 전달을 확인했으며 IDEMPOTENCY_CONFLICT의 별도 번역 분기 때문에 모든 서버 문구가 그대로 나온다고 일반화하지 않았다. |
| L360 | 오류에 중복 설비가 나오면 기존 설비를 찾아 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12 | 중복 ID·모델 존재/상태/방식·라인 등록·선택 JAR 존재 검사를 대조했다. 입력 오류 문구 전달을 확인했으며 IDEMPOTENCY_CONFLICT의 별도 번역 분기 때문에 모든 서버 문구가 그대로 나온다고 일반화하지 않았다. |
| L360 | 라인·모델·JAR를 찾지 못한다는 안내이면 목록을 다시 불러와 고른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12 | 중복 ID·모델 존재/상태/방식·라인 등록·선택 JAR 존재 검사를 대조했다. 입력 오류 문구 전달을 확인했으며 IDEMPOTENCY_CONFLICT의 별도 번역 분기 때문에 모든 서버 문구가 그대로 나온다고 일반화하지 않았다. |
| L360 | 필요한 항목이 없으면 운영 담당자에게 준비를 요청한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12 | 중복 ID·모델 존재/상태/방식·라인 등록·선택 JAR 존재 검사를 대조했다. 입력 오류 문구 전달을 확인했으며 IDEMPOTENCY_CONFLICT의 별도 번역 분기 때문에 모든 서버 문구가 그대로 나온다고 일반화하지 않았다. |
| L362 | 입력·등록 항목 오류는 서버 문구를 입력 창에 표시한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12 | 중복 ID·모델 존재/상태/방식·라인 등록·선택 JAR 존재 검사를 대조했다. 입력 오류 문구 전달을 확인했으며 IDEMPOTENCY_CONFLICT의 별도 번역 분기 때문에 모든 서버 문구가 그대로 나온다고 일반화하지 않았다. |
| L362 | 수정 시 기존 모델·JAR의 문제처럼 창에서 고칠 수 없는 거부이면 설비 ID·발생 시각·문구를 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41 / nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12 | 중복 ID·모델 존재/상태/방식·라인 등록·선택 JAR 존재 검사를 대조했다. 입력 오류 문구 전달을 확인했으며 IDEMPOTENCY_CONFLICT의 별도 번역 분기 때문에 모든 서버 문구가 그대로 나온다고 일반화하지 않았다. |

### 23. PASSIVE 주소를 함께 쓰는 설비의 배정 충돌

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L371 | PASSIVE 설비는 같은 통신 방식과 IP·Port를 쓰는 다른 설비와 「Route Partition」 배정이 충돌하면 저장이 거부될 수 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; PASSIVE 검사 L425~459 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/EqpManagementDbSupport.java @ 985d17c604dd ; 같은 주소·그룹 판정 L342~392 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPortTest.java @ 985d17c604dd ; 충돌 시 저장 차단 기대값 L974~1059 | PASSIVE 같은 방식·주소·Port 및 Socket 프로토콜 비교와 shard-group 충돌 검사를 대조했다. 모든 Route Partition 번호가 같아야 한다고 확대하지 않았다. |
| L371 | Socket에서는 「Socket Protocol Type」도 같은 경우를 검사한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; PASSIVE 검사 L425~459 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/EqpManagementDbSupport.java @ 985d17c604dd ; 같은 주소·그룹 판정 L342~392 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPortTest.java @ 985d17c604dd ; 충돌 시 저장 차단 기대값 L974~1059 | PASSIVE 같은 방식·주소·Port 및 Socket 프로토콜 비교와 shard-group 충돌 검사를 대조했다. 모든 Route Partition 번호가 같아야 한다고 확대하지 않았다. |
| L373 | 배정 충돌 안내에 상대 설비 ID가 나오면 대상과 상대 설비의 「EQP IP」·「EQP Port」·「Route Partition」을 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; PASSIVE 검사 L425~459 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/EqpManagementDbSupport.java @ 985d17c604dd ; 같은 주소·그룹 판정 L342~392 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPortTest.java @ 985d17c604dd ; 충돌 시 저장 차단 기대값 L974~1059 | PASSIVE 같은 방식·주소·Port 및 Socket 프로토콜 비교와 shard-group 충돌 검사를 대조했다. 모든 Route Partition 번호가 같아야 한다고 확대하지 않았다. |
| L373 | 배정값을 정할 수 없으면 두 설비 ID와 입력값·오류 문구를 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; PASSIVE 검사 L425~459 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/EqpManagementDbSupport.java @ 985d17c604dd ; 같은 주소·그룹 판정 L342~392 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPortTest.java @ 985d17c604dd ; 충돌 시 저장 차단 기대값 L974~1059 | PASSIVE 같은 방식·주소·Port 및 Socket 프로토콜 비교와 shard-group 충돌 검사를 대조했다. 모든 Route Partition 번호가 같아야 한다고 확대하지 않았다. |

### 24. Eqp Delete에서 대상 확인하고 삭제하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L388 | 1. 「Gateway」 좌측 트리에서 삭제할 설비 ID를 마우스 오른쪽 단추로 눌러 「Eqp Delete」를 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·단추 L49~86 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 삭제 후 선택 정리 L743~755 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 삭제 성공 반영 L148~161 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutations.ts @ 9fe69a75dcd3 ; 삭제 성공 뒤 목록 갱신 L78~84 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; deleteEqp L627~647 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; delete L305~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620 | 설비 노드의 삭제 진입, 대상 ID·종료 실패 시 중단 경고·Delete/Cancel, PASS 뒤 선택 정리·목록 무효화·삭제 완료 제목을 대조했다. 경고 문구를 실제 종료 성공의 보장으로 쓰지 않았다. |
| L389 | 2. 「Eqp Delete」 창에 나온 설비 ID와 경고를 읽는다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·단추 L49~86 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 삭제 후 선택 정리 L743~755 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 삭제 성공 반영 L148~161 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutations.ts @ 9fe69a75dcd3 ; 삭제 성공 뒤 목록 갱신 L78~84 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; deleteEqp L627~647 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; delete L305~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620 | 설비 노드의 삭제 진입, 대상 ID·종료 실패 시 중단 경고·Delete/Cancel, PASS 뒤 선택 정리·목록 무효화·삭제 완료 제목을 대조했다. 경고 문구를 실제 종료 성공의 보장으로 쓰지 않았다. |
| L389 | 창은 삭제 전 설비 실행을 종료하려고 시도하며 실패하면 삭제가 중단된다고 안내한다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·단추 L49~86 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 삭제 후 선택 정리 L743~755 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 삭제 성공 반영 L148~161 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutations.ts @ 9fe69a75dcd3 ; 삭제 성공 뒤 목록 갱신 L78~84 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; deleteEqp L627~647 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; delete L305~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620 | 설비 노드의 삭제 진입, 대상 ID·종료 실패 시 중단 경고·Delete/Cancel, PASS 뒤 선택 정리·목록 무효화·삭제 완료 제목을 대조했다. 경고 문구를 실제 종료 성공의 보장으로 쓰지 않았다. |
| L390 | 3. 삭제하려면 「Delete」, 요청 전 취소하려면 「Cancel」을 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·단추 L49~86 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 삭제 후 선택 정리 L743~755 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 삭제 성공 반영 L148~161 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutations.ts @ 9fe69a75dcd3 ; 삭제 성공 뒤 목록 갱신 L78~84 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; deleteEqp L627~647 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; delete L305~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620 | 설비 노드의 삭제 진입, 대상 ID·종료 실패 시 중단 경고·Delete/Cancel, PASS 뒤 선택 정리·목록 무효화·삭제 완료 제목을 대조했다. 경고 문구를 실제 종료 성공의 보장으로 쓰지 않았다. |
| L391 | 4. 진행 표시가 끝난 뒤 「설비 삭제 완료」를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·단추 L49~86 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 삭제 후 선택 정리 L743~755 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 삭제 성공 반영 L148~161 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutations.ts @ 9fe69a75dcd3 ; 삭제 성공 뒤 목록 갱신 L78~84 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; deleteEqp L627~647 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; delete L305~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620 | 설비 노드의 삭제 진입, 대상 ID·종료 실패 시 중단 경고·Delete/Cancel, PASS 뒤 선택 정리·목록 무효화·삭제 완료 제목을 대조했다. 경고 문구를 실제 종료 성공의 보장으로 쓰지 않았다. |
| L391 | 성공하면 목록이 갱신되어 대상 설비가 사라진다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L180~191 / nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·단추 L49~86 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 삭제 후 선택 정리 L743~755 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 삭제 성공 반영 L148~161 / nori-tc-ui-front/src/features/eqp/hooks/useEqpMutations.ts @ 9fe69a75dcd3 ; 삭제 성공 뒤 목록 갱신 L78~84 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; deleteEqp L627~647 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; delete L305~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620 | 설비 노드의 삭제 진입, 대상 ID·종료 실패 시 중단 경고·Delete/Cancel, PASS 뒤 선택 정리·목록 무효화·삭제 완료 제목을 대조했다. 경고 문구를 실제 종료 성공의 보장으로 쓰지 않았다. |

### 25. 등록·수정·삭제 결과가 지연되거나 중단됐을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L402 | 「설비 등록 확인 지연」·「설비 수정 확인 지연」·「설비 삭제 확인 지연」이 나오면 「작업 ID」가 있는 결과 창의 「다시 조회」를 누른다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 작업별 결과·재조회 L31~49 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 결과 단추 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 결과 본문 L28~40 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·중단 문구 L1110~1134 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 다른 작업·배포와 충돌 L117~135 | 작업별 지연 제목과 operationId가 있을 때만 재조회 단추를 표시하는 조건, repair 안내·작업 ID, 배포·다른 작업·미종결 차단의 구별을 대조했다. |
| L402 | 같은 등록·수정·삭제를 새로 요청하기 전에 현재 결과를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 작업별 결과·재조회 L31~49 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 결과 단추 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 결과 본문 L28~40 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·중단 문구 L1110~1134 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 다른 작업·배포와 충돌 L117~135 | 작업별 지연 제목과 operationId가 있을 때만 재조회 단추를 표시하는 조건, repair 안내·작업 ID, 배포·다른 작업·미종결 차단의 구별을 대조했다. |
| L406 | 작업 제목에 「중단 — 운영 확인 필요」가 나오면 운영 담당자에게 설비 ID·발생 시각·실행한 작업·결과 문구·「작업 ID」를 전달한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 작업별 결과·재조회 L31~49 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 결과 단추 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 결과 본문 L28~40 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·중단 문구 L1110~1134 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 다른 작업·배포와 충돌 L117~135 | 작업별 지연 제목과 operationId가 있을 때만 재조회 단추를 표시하는 조건, repair 안내·작업 ID, 배포·다른 작업·미종결 차단의 구별을 대조했다. |
| L408 | 배포나 다른 작업이 진행 중이라는 거부 문구이면 먼저 그 작업의 결과를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 작업별 결과·재조회 L31~49 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 결과 단추 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 결과 본문 L28~40 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·중단 문구 L1110~1134 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 다른 작업·배포와 충돌 L117~135 | 작업별 지연 제목과 operationId가 있을 때만 재조회 단추를 표시하는 조건, repair 안내·작업 ID, 배포·다른 작업·미종결 차단의 구별을 대조했다. |
| L408 | 배포 완료 후 다시 시도하고, 미종결 작업이 차단한다는 안내가 계속되면 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 작업별 결과·재조회 L31~49 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 결과 단추 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 결과 본문 L28~40 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·중단 문구 L1110~1134 / nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 다른 작업·배포와 충돌 L117~135 | 작업별 지연 제목과 operationId가 있을 때만 재조회 단추를 표시하는 조건, repair 안내·작업 ID, 배포·다른 작업·미종결 차단의 구별을 대조했다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 코드 | 본문 반영 |
|---|---|---|---|
| 수정 창의 모델·Business JAR | U L672~690은 선택 칸이 있으나 반영되지 않는다고 설명한다. | EqpManageFormModal L971~1062는 Model / Business를 create에서만 렌더한다. EqpUpdateRequest L25~58에 모델·JAR 필드가 없다. | 세 선택 칸이 없다고 쓰고 Gateway Jar만 남지만 서버가 기존 값을 유지함을 구분했다. |
| 통신 설정의 빈 숫자 | U L617~620은 수정 시 지금 값을 유지한다고 설명한다. | EqpManageFormModal L763~806은 빈 숫자를 null로 전달하고 JpaEqpCrudPort L871~926은 수정에서도 기본값을 쓴다. | 등록·수정 공통 기본값 표와 칸을 지우지 말라는 안내를 넣었다. 로그 정책의 기존 값 보완과 구분했다. |
| SECS 숫자의 0 | U L624는 모두 0 이상이라고 설명한다. | TcEqpHsmsJpaStore L306~312는 timeout·link interval·message bytes에 양수를 요구한다. 기본 스키마 L495는 Device ID 0~32767이다. | 화면 검사와 저장 제한을 구분했다. |
| 로그 보관 일수 | U L614는 0 이상이라고 설명한다. | TcEqpLogJpaStore L146~147은 1 이상을 검사한다. | 화면은 0을 허용해도 저장은 1 이상이라고 썼다. |
| Port·Socket 프레임·PLC Unit ID | U L597·625·626은 상한 또는 더 엄격한 저장 범위를 빠뜨린다. | 기본 스키마 L433은 포트 1~65535, L528은 프레임 양수다. EqpManagementCommandValidator L182~189는 Unit ID 0~255와 나머지 PLC 숫자의 양수를 검사한다. | 스키마 수치는 저장소 기본 스키마 기준임을 명시하고 서버 범위도 반영했다. |
| 그룹 아래 표시 | U L536은 회색 글씨를 Pod 이름으로 설명한다. | EqpSidebar L296~300은 spare에서 podName이 없으면 sessionId를 사용한다. | 점유 Pod과 대기 노드의 대체 표시를 구분했다. |
| 등록 뒤 선택 시점 | U L638~639는 확인을 누르면 새 설비가 선택된다고 설명한다. | EqpPage L743~755는 PASS 뒤 onTerminal에서 선택한다. | 등록 성공 뒤 선택된다고 썼으며 확인 단추 자체가 선택을 발생시킨다고 쓰지 않았다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 담당 역할 |
|---|---|---|
| 제조사별 올바른 IP·Port·Device ID·시간값·JAR 조합 | 제조사 계약·현장 설비 사양 원본이 없다. 입력 범위와 소스 기본값만 썼다. | 설비 담당·모델 담당 |
| 현장 계정 권한·실제 사용 가능한 라인·JAR | 기본 자료와 소스만 확인했으며 현장 계정·DB·서버를 조회하지 않았다. | 운영 관리자 |
| Socket Heartbeat·Read/Write Timeout의 단위·통신 효과 일반화 | 현재 실행 설정에서 쓰지 않는 네 항목의 이름만으로 통신 효과를 짓지 않았다. HSMS 다섯 타이머·Link Test Interval의 초 단위는 제품 수정 C의 변환 코드를 확인해 본문에 추가했다. 전체 프로토콜 의미는 별도 문서 소유다. | 프로토콜 문서 담당 |
| 모든 삭제 실패에 선행 End·재시도를 권하는 안내 | U L729만으로 모든 실패가 종료 명령으로 해결된다고 입증되지 않는다. PASSIVE 등의 명령 조건은 별도 문서 소유다. | 설비 명령 문서 담당·운영 담당 |
| 실패 제목만으로 판단하는 실제 되돌림 상태·동일 요청 재실행 안전성 | 이번에 확인한 UI 결과 분기는 서버 오류·작업 ID를 표시하며 예외 경로도 실패 창을 연다. 화면 분기만으로 실제 설비·보상 결과와 안전한 재실행을 확정하지 않았다. | 운영 담당·조정 작업 담당 |
| 요청 키 충돌 때 앞선 요청이 실제 성공했는지에 대한 판정 | 사용자에게 보이는 안내와 현재 값 확인 절차만 썼다. 프론트 주석의 인과 설명을 실제 성공 판정 근거로 사용하지 않았다. | 운영 담당·조정 작업 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 복제하지 않은 답 |
|---|---|
| 00.rag/user/login-and-screen-basics.md | 로그인·상단바 라인 선택 상세·공통 표 조작·공통 결과 대화상자 네 상태의 전체 설명. 여기서는 설비 작업의 최소 진입·결과 조건만 유지했다. |
| 00.rag/user/live-map-status.md | Live-Map의 상태·포트·로그·3D 조회. 설비 화면의 Enabled 점과 혼합하지 않았다. |
| 00.rag/user/live-map-layout-editing.md | 지도에 미배치 설비를 추가·배치·삭제하는 작업. 설비 자체의 등록·삭제와 구별했다. |
| 00.rag/user/equipment-commands-and-polling.md | Start·End·Restart·Reconnect·PLC 폴링의 대상 조건과 실행법. |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 모델·파라미터·JAR 갱신 상세 경로. 여기서는 설정 수정에서 바뀌지 않는 경계만 설명했다. |
| 00.rag/user/deployment-workflow.md | 모델·Business JAR 배포 등록·실행·이력 전체 절차. |
| 00.rag/user/state-reference.md | 상태 코드의 내부 조건·의미 전체. 여기서는 점과 tooltip의 현재 표기만 설명했다. |
| 00.rag/user/permission-code-reference.md | 권한 코드 전체 뜻과 권한 관리법. 여기서는 이 작업의 API 조건만 설명했다. |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드별 전수 뜻풀이. 여기서는 직접 조작·입력 조건·인계에 필요한 문구만 설명했다. |
| 00.rag/user/secs-hsms-message-and-timer-reference.md | 프로토콜·타이머의 의미 전체. |
| 00.rag/user/socket-framing-and-parsing-reference.md | 프레임·파싱·프로토콜 의미 전체. |
| 00.rag/user/plc-modbus-register-and-type-reference.md | PLC 레지스터·타입 의미 전체. |

## 초안 검사 결과(보완 전 이력)

검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/equipment-registration-and-settings.md
```

Windows PowerShell에서 Git Bash 실행 파일을 호출했다. 해당 명령 프로세스에만 GIT_CONFIG_COUNT=4와 네 safe.directory 경로(C:/workspace_nori/nori-tc/nori-tc-ui-front, nori-tc-ui-backend, nori-tc-exec, nori-tc-common)를 지정했다. 검사기·기준·전역 설정·소유자·접근 권한은 바꾸지 않았다. 마지막 원문 편집 뒤 실행한 명령의 종료코드는 0이다.

마지막 출력 10줄:

```text
    L313-313 목록 항목 84  | 2. 「Eqp Delete」 창에 나온 설비 ID와 경고를 읽는다. 창은 삭제 전 설비 실행을 종료하려고 시도하며 실패하면 삭제가 중단된다고 안내한다.
    L314-314 목록 항목 44  | 3. 삭제하려면 「Delete」, 요청 전 취소하려면 「Cancel」을 누른다.
    L315-315 목록 항목 59  | 4. 진행 표시가 끝난 뒤 「설비 삭제 완료」를 확인한다. 성공하면 목록이 갱신되어 대상 설비가 사라진다.
§ L317-330 [25] 등록·수정·삭제 결과가 지연되거나 중단됐을 때   parent=293 children=3 sources=5
    L326-326 문단 115  | 「설비 등록 확인 지연」·「설비 수정 확인 지연」·「설비 삭제 확인 지연」이 나오면 「작업 ID」가 있는 결과 창의 「다시 조회」를 누른다. 같은 등록·수정·삭제를 새로 요청하기 전에 현재 결과를 확인한다.
    L328-328 문단 76  | 작업 제목에 「중단 — 운영 확인 필요」가 나오면 운영 담당자에게 설비 ID·발생 시각·실행한 작업·결과 문구·「작업 ID」를 전달한다.
    L330-330 문단 100  | 배포나 다른 작업이 진행 중이라는 거부 문구이면 먼저 그 작업의 결과를 확인한다. 배포 완료 후 다시 시도하고, 미종결 작업이 차단한다는 안내가 계속되면 운영 담당자에게 전달한다.
SUMMARY sections=21 children=69 maxChild=192 maxParent=429
CORPUS docs=4/53 sections=67 part={user=4} type={사용 절차=4}
TOTAL files=4 targets=1 violations=0
```

최종 검사: 21절·69자식, 최대 자식 192자·최대 부모 429자. 대상 위반 0건, CROSS VIOLATION 0건, MASK 0건. H1/H2 구조·메타·원천 판본 경로·줄 범위·표·인코딩·NFC·마스킹을 검사했다.

검사 중 수정: 부모 길이 459자를 의미가 유지되는 표 머리글 축약으로 해소했다. 원천 줄 범위 한 곳이 실제 파일 41줄을 넘은 것은 resolveAcceptanceErrorMessage L31~41로 정정했다. 작업 트리의 원천 파일은 바꾸지 않았다.

원천 검사 도구에서 확인한 사항: RagDocCheck.java L389~390·L405~413은 git show 실패 원문을 표시하지 않고 commit:path 없음으로 합친다. 직접 `git -C ../nori-tc-ui-front show 9fe69a75dcd3:src/features/eqp/components/EqpSidebar.tsx`를 실행했을 때 프로세스는 시작했으나 exit 128과 `fatal: detected dubious ownership in repository`를 반환했다. 정확한 저장소의 읽기를 명령 프로세스에만 지정하자 원천 검사도 통과했다. 도구는 고치지 않았으며 메인 세션에 별도 보고한다.

중간 출력 처리 실패도 구분한다. 두 번째 호출은 검사 출력 파일을 저장한 뒤 Python stdout의 cp949가 em dash를 표현하지 못해 `UnicodeEncodeError`로 끝났다. 이어 작성 기록 조립은 PowerShell Tee-Object의 UTF-16 로그를 UTF-8로 읽어 `UnicodeDecodeError: invalid start byte 0xff`가 났으며 그 호출은 파일을 쓰기 전에 끝났다. 로그 BOM으로 UTF-16/UTF-8을 구분한 기록 조립으로 해결했다. 제품 오류나 검사 PASS로 재분류하지 않았다.

최초 실패의 마지막 출력 10줄도 보존한다:

```text
VIOLATION L286 원천 commit:path 없음 'nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80'
VIOLATION L286 원천 commit:path 없음 'nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620'
VIOLATION L302 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 작업별 결과·재조회 L31~49'
VIOLATION L302 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 결과 단추 L119~127'
VIOLATION L302 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 결과 본문 L28~40'
VIOLATION L302 원천 commit:path 없음 'nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·중단 문구 L1110~1134'
VIOLATION L302 원천 commit:path 없음 'nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 다른 작업·배포와 충돌 L117~135'
VIOLATION L200 부모 길이 459 (1~450)
CORPUS docs=4/53 sections=67 part={user=4} type={사용 절차=4}
TOTAL files=4 targets=1 violations=102
```

중간 검사에서 원천 읽기를 해결한 뒤 남았던 줄 범위 오류의 마지막 출력:

```text
SUMMARY sections=21 children=69 maxChild=192 maxParent=429
VIOLATION L264 원천 줄 범위 L34~45 (파일 41줄) 'nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L34~45'
CORPUS docs=4/53 sections=67 part={user=4} type={사용 절차=4}
TOTAL files=4 targets=1 violations=1
```

사람 검사: 모든 21절을 직접 읽었다. 각 제목의 질문·원천 일치·독립 답·다른 절/앞선 문서와 겹침을 아래처럼 판정했다. 미해소 지적은 없다. 글자 수 검사만으로 판정하지 않았다.

| 절 | 질문을 제목에서 식별 | 원천 대조 | 독립 답 | 중복·소유 |
|---|---|---|---|---|
| 설비 작업에 필요한 권한 확인하기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| Gateway와 Business에서 작업 대상 찾기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 목록 표와 한 대의 상세에서 확인할 값 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 한 대의 설비 정보에서 모델이 —로 표시될 때 | 적합 | 완료 | 표시 조건·권한·대체 확인 경로·인계 포함 | Business 목록 설명과 상세의 표시 조건 구분 |
| Business의 모델 표가 비어 있을 때 확인하기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| Enabled 색 점과 그룹 아래 표시 읽기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 설비 목록이나 관리 상세를 불러오지 못했을 때 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 새 설비 등록 창 열기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 공통 정보의 필수값 채우기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 등록할 모델과 JAR 고르기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| Log Policy 입력과 보관 일수 제한 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| SECS Settings의 입력 범위 확인하기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| SECS 저장값과 실제 통신 설정 구분하기 | 적합 | 완료 | 판본·미적용 항목·참조 경로 포함 | 입력·저장 검증과 통신 적용을 구분하고 수치 상세 미복제 |
| Socket Settings에서 프로토콜과 숫자 채우기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| Socket 저장값과 실제 통신 설정 구분하기 | 적합 | 완료 | 판본·미적용 항목·참조 경로 포함 | 입력·저장 검증과 실행 적용을 구분·실제 문자셋과 상한 미복제 |
| PLC Settings에서 변경할 수 있는 값 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 통신 설정의 숫자 칸을 비우면 적용되는 값 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 등록 요청을 저장하고 완료 확인하기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| Eqp Info Update에서 설정 바꾸기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 수정 창에서 모델과 JAR를 바꾸려는 경우 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 서버가 등록·수정을 거부할 때 확인할 조건 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| PASSIVE 주소를 함께 쓰는 설비의 배정 충돌 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| Eqp Delete에서 대상 확인하고 삭제하기 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 등록·수정·삭제 결과가 지연되거나 중단됐을 때 | 적합 | 완료 | 대상·조건·조작/확인값 포함 | 필요한 조건만 유지·상세 소유 분리 |
| 설비 등록·수정·삭제 실패 창에서 읽고 전달할 정보 | 적합 | 완료 | 실패 제목·본문·작업 ID·확인·인계 포함 | 공통 명령 결과 설명과 작업별 실패 안내 구분 |
| 같은 요청 키나 기준 상태 변경으로 거부됐을 때 | 적합 | 완료 | 거부 문구·Cancel·현재 값 재조회·인계 포함 | 기존 다른 작업·배포 거부와 구분 |

제목 전수 측정: 정확 중복 0건, 영문·숫자 토막을 지운 동일 모양 3회 이상 반복 0건.

본문 전수 판정: 정보 없는 틀 문장·이름 토막 뜻풀이·잘린 문장·틀린 주장·오답 인용·미해소 마스킹을 남기지 않았다. user 본문에 admin objectKey·관리 문서 열람 지시·클러스터/DB/설정 파일 조치를 넣지 않았다. 기계 검사의 본문 중복 검사와 별도로 의미상 소유를 비교했다.

PLC 등록 초기 표시와 빈칸 저장 기본값은 숫자는 같아도 조건이 다르다. PLC의 고정 Comm Mode는 공통 입력 안내와 PLC 입력에 필요한 최소 조건으로만 반복했다. 기존 공통 결과 설명은 복제하지 않고 작업별 제목·조치만 남겼다.

현재 문장·표 행 대조 표는 150행이다. Charset 거부 시점 보완으로 바뀐 문단의 사실·근거와 전체 원문 줄 번호를 동기화했다. 원문 비메타 자료 줄 89개를 모두 대응했다. 이전 보완의 수량·출력은 아래 이력으로 보존했다.

실행하지 않은 검증: 실제 브라우저 UI·현장 계정·서버 요청·DB 저장·Gateway 통신·제품 단위/통합 시험·빌드·RAG 업로드·검색 정답률 측정. 완료 범위는 문서 검사와 소스·관련 시험 기대값 대조이며 운영 실행 검증이 아니다.

## 실행 범위와 임시 자원 정리

영속 산출물은 검색 원문과 이 작성 기록 두 개다. 임시 출력은 지정 work/의 check-first.txt·check-second.txt·check-final.txt 세 파일뿐이다. 실패 경위·필요한 원문 출력·최종 검사 결과는 위에 보존했다. 이 세 소유 파일에 대해서만 정확한 절대경로·보호 속성·reparse point·활성 프로세스 참조를 확인하고 비강제 native PowerShell Remove-Item -LiteralPath로 정리한 뒤 부재를 확인한다. 사전 존재한 work 폴더와 상위 입력·실행 기록은 보존한다.

독립 AI 추가 호출 0회, 제품 실행 0회. provider 사용량·비용은 이 세션의 도구 결과에 없어 미확인이다. 검사 명령 소요시간은 도구의 실측 로그에 남았으며 시간 제한으로 판정하지 않았다.

정리 실측: PowerShell 5.1.26100.9444에서 세 파일과 work 부모에 ReadOnly·Hidden·System·ReparsePoint 속성이 없음을 확인했다. `Get-CimInstance Win32_Process -ErrorAction Stop`는 프로세스가 시작된 뒤 `HRESULT 0x80041003, 액세스가 거부되었습니다`로 실패하여 전체 프로세스 명령행 참조 검사는 완료하지 못했다. 권한·정책·소유자를 바꾸지 않았다. 이 세션이 호출한 검사기·로그 작성 프로세스는 모두 종료 결과를 회수했고, 삭제 전 각 파일을 FileAccess.Read·FileShare.None으로 독점 열어 사용 중인 파일 공유 충돌이 없음을 확인한 뒤 즉시 닫았다. 그 후 경로·속성을 다시 검사하고 `Remove-Item -LiteralPath <정확한 절대경로> -ErrorAction Stop` 비강제 정상 삭제를 실행했다. 삭제 명령 exit 0, 세 경로의 Test-Path는 모두 False이며 work 잔여 파일 0개다. 강제 옵션·재귀 삭제·다른 인터프리터 삭제·프로세스 종료를 쓰지 않았다.

삭제한 정확한 소유 임시 경로:

- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/check-first.txt
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/check-second.txt
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/check-final.txt

초안 작성 당시 자체 보고: 문서 작성·자체 검사 완료, 미해소 문서 결함 0건, 사용자 결정 필요 0건. 이후 메인 검증에서 안내 누락 2건을 지적받았으며 아래 보완 이력과 구분한다. 다음 문서 작성·업로드·제품 실행·공식 독립 검증은 실행하지 않았다.

실행 수량: 검사기 호출은 총 5회다. 최초 1회는 길이·Git 원천 읽기 오류, 두 번째는 원천 줄 범위 오류가 남은 중간 결과다. 이후 원문 수정에 대응해 실행한 3회는 각각 exit 0이며 마지막 실행의 도구 실측 wall time은 4.07027초다. 최초 장기 실행은 세션 반환 뒤 완료를 회수했으므로 전체 wall time을 추정하지 않는다. 기록 조립 중 인코딩 실패는 검사기 호출 수에 포함하지 않았다.

## 2026-11-18 메인 검증 결함 2건 보완

### 보완 범위와 원인

- 결함 1: 결과 분기를 완료·지연·운영 확인 중단 위주로 묶으면서 failure 결과의 별도 제목·본문·확인 단추·인계를 누락했다. 새 실패 안내 절에 해당 표시와 인계만 추가했다.
- 결함 2: 접수 거부를 다른 작업·배포·미종결 차단 위주로 묶어 요청 키 충돌·기준 상태 충돌의 현재 값 확인 절차와 작업 ID 없는 지연 분기를 누락했다. 거부 안내 절을 추가하고 기존 지연 절에는 한 문장만 추가했다.
- 실패 뒤 실제 보상·되돌림 상태와 같은 요청 재실행 안전성은 확인하지 못했으므로 본문에서 단정하지 않았다. 요청 키 충돌의 앞선 성공 여부도 프론트 주석으로 확정하지 않았다.
- 기존 원문 330줄은 삭제·교체 없이 모두 유지했다. diff는 기존 L328 앞에 2줄 추가와 문서 끝의 37줄 추가, 합계 39줄 추가다. 범위 밖 제품 코드·다른 문서는 수정하지 않았다.
- 보완 전 원문 SHA-256: `3b45249d1e9d01697e43aa4de7653231a9eb4bf83c61930966aabd13c23529f3`
- 보완 후 원문 SHA-256: `d2dc324f53a59f48752f09918797458b36038bc5c30dfc7b0232825bf685442f`
- 보완 전 작성 기록 SHA-256: `a6c5929cb51a9e0c629d30fe229c3be6d328170be2a53fb2eed62543ff949337`
- 최종 원문: 369줄·23절·76자식. 기존 사실 대조 121행의 문구·원천을 보존하고 줄 번호만 대응시켰다. 새 사실 대조 10행을 아래에 추가하여 총 131행이다.
- 보완에 사용한 원천의 현재 HEAD는 기존 판본과 같으며, 원천 43개 고유 파일의 경로별 git status 출력 없음·exit 0을 재확인했다. 프론트 인용 파일은 useEqpManageDetail.ts가 추가되어 24개다.

### 추가·변경 사실 대조

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L404 | 확인 지연 창에 「작업 ID」와 「다시 조회」가 없으면 잠시 후 화면을 새로고침해 결과를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L31~40 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1117~1117 | 재조회 단추는 delayed이고 operationId가 있을 때만 렌더된다. ID가 없으면 단추·ID 표시도 없으며 delayedHint가 잠시 뒤 재조회 또는 화면 새로고침을 안내한다. |
| L421 | 「설비 등록 실패」·「설비 수정 실패」·「설비 삭제 실패」가 나오면 결과 본문의 서버 문구와 표시된 「작업 ID」를 읽는다. | nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 L189~196 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 L31~72 L94~117 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L79~80 L620~620 L1121~1132 | 서버 errorMsg·operationId가 failure 창에 전달된다. 제목·기본 문구 매핑, message의 nullish 대체와 ID의 조건부 표시를 대조했다. |
| L421 | 서버 문구가 전달되지 않으면 설비 ID가 포함된 기본 실패 문구가 나온다. | nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 L189~196 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 L31~72 L94~117 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L79~80 L620~620 L1121~1132 | 서버 errorMsg·operationId가 failure 창에 전달된다. 제목·기본 문구 매핑, message의 nullish 대체와 ID의 조건부 표시를 대조했다. |
| L423 | 실패 창에는 「다시 조회」가 없고 「확인」만 있다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40 | 실패에서는 다시 조회 조건이 성립하지 않고 확인 단추만 렌더된다. 닫기 전 보이는 식별정보·문구를 기록하여 인계하는 사용자 절차다. |
| L423 | 「확인」으로 닫기 전에 설비 ID·발생 시각·실행한 작업·결과 문구·표시된 「작업 ID」를 기록해 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 L119~127 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40 | 실패에서는 다시 조회 조건이 성립하지 않고 확인 단추만 렌더된다. 닫기 전 보이는 식별정보·문구를 기록하여 인계하는 사용자 절차다. |
| L440 | 「같은 요청 키로 다른 내용이 접수되어 거부되었습니다.」로 시작하는 안내나 「요청이 기준으로 삼은 상태가 이미 바뀌었습니다.」가 나오면 같은 등록·수정·삭제를 다시 요청하기 전에 현재 값을 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd L67~77 L132~135 / nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 L31~41 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1109~1109 | 두 접수 거부 코드의 발생·문구와 IDEMPOTENCY_CONFLICT만 번역하는 분기를 확인했다. 원인이 모두 같다고 합치거나 앞선 성공을 단정하지 않았다. |
| L442 | 1. 입력 창의 「Cancel」을 누르고 「Gateway」 트리에서 대상 설비 ID가 있는지 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 L1385~1394 / nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L67~75 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L474~487 | 접수 거부 후에는 요청 실행 중이 아니므로 Cancel로 닫는 경로를 사용할 수 있다. create·update·delete의 실제 단추 문구를 확인했다. |
| L443 | 2. 설비 ID가 있으면 「Eqp Info Update」를 다시 연다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L388~403 L474~487 L1419~1440 / nori-tc-ui-front/src/features/eqp/hooks/useEqpManageDetail.ts @ 9fe69a75dcd3 L9~16 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L290~305 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 L541~550 | 닫으면 관리 대상·enabled가 해제되고 info로 다시 열면 enabled=true·staleTime=0인 관리 상세 조회가 연결된다. 조회 결과 detail 변경 시 form 초기값이 갱신된다. 조회 완료 뒤 값을 비교하도록 썼다. |
| L443 | 현재 값 조회가 끝난 뒤 요청 전에 고친 값과 비교한다. | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L388~403 L474~487 L1419~1440 / nori-tc-ui-front/src/features/eqp/hooks/useEqpManageDetail.ts @ 9fe69a75dcd3 L9~16 / nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L290~305 / nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 L541~550 | 닫으면 관리 대상·enabled가 해제되고 info로 다시 열면 enabled=true·staleTime=0인 관리 상세 조회가 연결된다. 조회 결과 detail 변경 시 form 초기값이 갱신된다. 조회 완료 뒤 값을 비교하도록 썼다. |
| L445 | 대상이 없거나 현재 값을 읽지 못하면 설비 ID·발생 시각·실행한 작업·거부 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L290~305 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L388~403 | 설비 부재나 조회 오류를 성공·삭제 완료로 단정하지 않고 대상 ID·거부 문구·작업·시각을 운영 역할에 인계한다. |

### 보완 검사 결과

검사 명령은 메인 세션이 수정한 check.sh를 그대로 호출했다. 별도 safe.directory 환경값을 주거나 검사기를 수정하지 않았다. 이전 기록의 Git 오류 표시 결함은 보완 전 이력이며 현재 미해결 항목이 아니다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/equipment-registration-and-settings.md
check.sh sha256=3485078f9b1fb33e64da977935656d62634185cb42e5b5201367ddf3ba806fd4
```

이 보완 요청의 검사기 호출 1회, exit 0, 도구 실측 wall time 4.0746677초다. 마지막 출력 10줄:

```text
    L345-345 문단 111  | 「설비 등록 실패」·「설비 수정 실패」·「설비 삭제 실패」가 나오면 결과 본문의 서버 문구와 표시된 「작업 ID」를 읽는다. 서버 문구가 전달되지 않으면 설비 ID가 포함된 기본 실패 문구가 나온다.
    L347-347 문단 98  | 실패 창에는 「다시 조회」가 없고 「확인」만 있다. 「확인」으로 닫기 전에 설비 ID·발생 시각·실행한 작업·결과 문구·표시된 「작업 ID」를 기록해 운영 담당자에게 전달한다.
§ L349-369 [26] 같은 요청 키나 기준 상태 변경으로 거부됐을 때   parent=303 children=4 sources=11
    L364-364 문단 111  | 「같은 요청 키로 다른 내용이 접수되어 거부되었습니다.」로 시작하는 안내나 「요청이 기준으로 삼은 상태가 이미 바뀌었습니다.」가 나오면 같은 등록·수정·삭제를 다시 요청하기 전에 현재 값을 확인한다.
    L366-366 목록 항목 57  | 1. 입력 창의 「Cancel」을 누르고 「Gateway」 트리에서 대상 설비 ID가 있는지 확인한다.
    L367-367 목록 항목 71  | 2. 설비 ID가 있으면 「Eqp Info Update」를 다시 연다. 현재 값 조회가 끝난 뒤 요청 전에 고친 값과 비교한다.
    L369-369 문단 61  | 대상이 없거나 현재 값을 읽지 못하면 설비 ID·발생 시각·실행한 작업·거부 문구를 운영 담당자에게 전달한다.
SUMMARY sections=23 children=76 maxChild=192 maxParent=429
CORPUS docs=4/53 sections=69 part={user=4} type={사용 절차=4}
TOTAL files=4 targets=1 violations=0
```

기계 검사: 위반 0건·CROSS VIOLATION 0건·MASK 0건, 최대 자식 192자·최대 부모 429자. 마지막 원문 편집 뒤 검사했다.

사람 검사: 23절의 제목·본문을 읽고 실패 안내·접수 거부·지연·중단의 경계와 독립 답을 확인했다. 기존 다른 작업·배포 거부 문단은 보존했고 새 절에는 두 누락 거부만 넣었다. 새 절의 서버 결과 문구 대체 조건·작업 ID 조건·확인/재조회 단추·Cancel/관리 상세 재조회·읽은 값 비교·인계 항목을 실제 렌더와 호출에 대조했다. 확인 지연의 ID 없는 분기도 해당 절 안에서 완결된다.

기존 공통 화면 문서의 네 상태 설명은 복제하지 않았다. 실패 절은 등록·수정·삭제 각각의 실패 제목과 실패 창의 문구·단추 조건을 소유한다. 지연·중단 절은 원래 제목과 문단을 유지했다. 관리자 전용 조치·비밀값·오답 인용·이름 토막 뜻풀이는 추가하지 않았다.

검증 경계: 자체 보완 검사이며 메인 세션의 후속 독립 검증을 대신하지 않는다. 브라우저·서버·DB·설비 통신·제품 시험·빌드·업로드는 실행하지 않았다. 추가 AI 호출 0회, provider 사용량·비용은 미확인이다.

### 보완 임시 자원

보완 전 원문 비교용 repair-before.md와 보완 검사 출력 repair-check.txt만 지정 work/에 만들었다. 기준 SHA·변경 범위·출력 근거는 이 기록에 보존했다. 두 파일만 보호 속성·reparse·배타 읽기 가능 여부와 소유 호출 종료를 확인한 뒤 비강제 native PowerShell로 정리하고 부재를 확인한다. 상위 작업 폴더와 사전 존재 파일은 삭제하지 않는다.

보완 정리 실측: 소유 검사·비교 프로세스의 종료 결과를 회수한 뒤 아래 두 절대경로와 work 부모가 허가한 폴더 안에 있고 ReadOnly·Hidden·System·ReparsePoint 속성이 없음을 확인했다. 각 파일을 Read/FileShare.None으로 독점 열어 파일 공유 충돌이 없음을 확인하고 즉시 닫았다. 이전 WMI 접근 거부를 재시도하거나 권한을 바꾸지 않았다. 비강제 native PowerShell `Remove-Item -LiteralPath`로 두 파일을 삭제했다. 명령 exit 0, 두 Test-Path 모두 False, 지정 work 잔여 파일 0개다.

- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/repair-before.md
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/repair-check.txt

보완 최종 상태: 지적된 2건의 안내 누락을 수정하고 관련 자체 검사에서 미해소 문서 결함 0건, 사용자 결정 필요 0건을 확인했다. 실패 뒤 실제 설비의 되돌림 결과·같은 요청 재실행 안전성은 미확인으로 남기고 본문에서 제외했다. 메인 세션의 독립 검증은 이 자체 판정과 별도다.

## 2026-11-18 추가 검증의 모델 표시 조건 보완

### 결함과 최소 수정 범위

한 대의 설비 상세에서 Model·Model Version을 별도 모델 API로 읽는다는 차이를 놓치고 표의 열 구성만 설명하여 모델 조회 권한 부재·조회 실패·연결 모델 부재의 대시 표시와 무오류 안내를 누락했다. 기존 목록·상세 절은 부모 389자로 새 안내를 모두 합치면 450자를 넘으므로 바로 다음에 표시 조건 절을 분리했다. 기존 원문 369줄은 삭제·교체 없이 전부 보존했고 직접 영향인 새 절 19줄만 추가했다. 다른 문서는 고치지 않았다.

새 절은 모델 조회 결과가 없는 경우를 조건으로 썼다. 권한 부재·조회 실패만으로 항상 캐시 데이터까지 사라진다고 확대하지 않았다. 값이 대시이면 운영 관리자에게 모델 조회 권한을 확인하고, 모델 단건 조회와 다른 관리 상세를 쓰는 Gateway 목록의 같은 열에서도 값을 확인하도록 안내했다. Gateway 목록에서 언제나 값이 나온다고 보장하지 않았다.

- 보완 전 검색 원문 SHA-256: `d2dc324f53a59f48752f09918797458b36038bc5c30dfc7b0232825bf685442f`
- 보완 후 검색 원문 SHA-256: `a38e750151ef61a8f1fcea26d711944c1316daddbd65bc26b6d2d71ab7e46f09`
- 보완 전 작성 기록 SHA-256: `e275795b30338f4a1b0e5933ffa3a556df0c484ec8c4c0ec020de2a43d6d2b97`
- 최종 검색 원문: 388줄·24절·79자식. 사실 대조는 기존 131행 보존 및 줄 번호 동기화 + 새 절 6행으로 총 137행이다. 새 행은 위 절별 사실 대조의 4절에 추가했다.
- 현재 인용 원천 45개 고유 파일의 git status stdout 없음·exit 0과 저장소 HEAD 일치를 확인했다. useEqpModelInfo.ts가 추가되어 프론트 25개, ModelQueryController.java가 추가되어 백엔드 13개다. exec 5개·common 2개는 유지했다.
- 처음 추정한 ModelController.java 경로는 존재하지 않았다. 파일 목록에서 실제 ModelQueryController.java를 찾고 get L56~67의 모델 부재 404 처리까지 확인했다. 존재하지 않는 경로는 문서나 원천으로 쓰지 않았다.
- 관련 모델·파라미터·JAR 갱신 문서의 모델 조회 권한 안내도 읽었으나 수정하지 않았다. 그 문서는 편집·갱신의 작업 권한, 이 절은 한 대의 상세 표가 오류 문구 없이 대시를 표시하는 조건과 목록 표의 다른 공급 경로를 소유한다.

### 최종 검사

수정된 check.sh를 별도 환경 설정 없이 그대로 실행했다. 이 보완 요청에서 검사기 호출 1회, exit 0, 도구 실측 wall time 5.33671초다. 마지막 원문 편집 뒤 검사했다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/equipment-registration-and-settings.md
```

마지막 출력 10줄:

```text
    L364-364 문단 111  | 「설비 등록 실패」·「설비 수정 실패」·「설비 삭제 실패」가 나오면 결과 본문의 서버 문구와 표시된 「작업 ID」를 읽는다. 서버 문구가 전달되지 않으면 설비 ID가 포함된 기본 실패 문구가 나온다.
    L366-366 문단 98  | 실패 창에는 「다시 조회」가 없고 「확인」만 있다. 「확인」으로 닫기 전에 설비 ID·발생 시각·실행한 작업·결과 문구·표시된 「작업 ID」를 기록해 운영 담당자에게 전달한다.
§ L368-388 [26] 같은 요청 키나 기준 상태 변경으로 거부됐을 때   parent=303 children=4 sources=11
    L383-383 문단 111  | 「같은 요청 키로 다른 내용이 접수되어 거부되었습니다.」로 시작하는 안내나 「요청이 기준으로 삼은 상태가 이미 바뀌었습니다.」가 나오면 같은 등록·수정·삭제를 다시 요청하기 전에 현재 값을 확인한다.
    L385-385 목록 항목 57  | 1. 입력 창의 「Cancel」을 누르고 「Gateway」 트리에서 대상 설비 ID가 있는지 확인한다.
    L386-386 목록 항목 71  | 2. 설비 ID가 있으면 「Eqp Info Update」를 다시 연다. 현재 값 조회가 끝난 뒤 요청 전에 고친 값과 비교한다.
    L388-388 문단 61  | 대상이 없거나 현재 값을 읽지 못하면 설비 ID·발생 시각·실행한 작업·거부 문구를 운영 담당자에게 전달한다.
SUMMARY sections=24 children=79 maxChild=192 maxParent=429
CORPUS docs=6/53 sections=104 part={user=6} type={사용 절차=6}
TOTAL files=6 targets=1 violations=0
```

기계 검사 위반 0건·CROSS VIOLATION 0건·MASK 0건. 최대 자식 192자·최대 부모 429자, 새 절 부모 296자다. 기존 본문은 전부 유지됐고 관련 없는 절은 추가·재작성하지 않았다.

사람 검사: 새 절의 제목·표시 조건·독립 답·중복을 판독하고 모든 문장을 실제 조회·인가·렌더 경로와 대조했다. 나머지 23절의 제목·본문은 보존된 문장과 기존 소스 근거를 재사용하여 전체 24절의 소유 경계를 확인했다. 기존 Business 절은 모델 목록 조회, 새 절은 한 대 상세의 표시 조건이므로 답을 복제하지 않았다. 오류를 표시하지 않는 상황과 화면의 대시를 결합했고 상태·권한 코드 이름을 임의 풀이하지 않았다.

실제 브라우저·계정 권한·서버 요청·DB 조회·제품 시험·빌드·업로드는 실행하지 않았다. 현장 모델 연결 여부와 캐시 상태는 미확인이다. 이번 자체 보완 판정은 메인 세션의 후속 독립 검증을 대신하지 않는다. 추가 AI 호출 0회, provider 사용량·비용은 미확인이다.

### 임시 자원

지정 work/에만 만든 비교 원문 model-display-before.md와 검사 출력 model-display-check.txt의 필요한 기준 SHA·diff·검사 출력은 위에 보존했다. 두 소유 파일은 경로·보호 속성·reparse·독점 읽기 가능 여부와 소유 호출 종료를 확인한 뒤 비강제 native PowerShell로 정상 삭제하고 부재를 확인한다.

추가 보완 정리 실측: 검사·비교 호출이 종료됐음을 회수한 뒤 두 소유 파일 및 work 부모의 정확한 절대경로와 ReadOnly·Hidden·System·ReparsePoint 속성 부재를 확인했다. 각 파일을 Read/FileShare.None으로 독점 열고 즉시 닫아 파일 공유 충돌이 없음을 확인했다. 이전 WMI 접근 거부는 재시도하지 않았다. 비강제 native PowerShell `Remove-Item -LiteralPath`로 아래 두 파일을 정상 삭제했다. 명령 exit 0, 두 Test-Path 모두 False, work 잔여 파일 0개다.

- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/model-display-before.md
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/model-display-check.txt

추가 보완 최종 상태: 모델 값 대시 표시의 조건 누락 1건을 보완했고 자체 검사에서 미해소 문서 결함 0건·사용자 결정 필요 0건이다. 원천·작성 기록의 줄 대응·SHA·절 수를 최종본에 동기화했다. 다른 문서·제품 코드·설정은 수정하지 않았다.

## 2026-11-19 SECS 저장값과 세션 적용 경계 보완

### 결함과 직접 영향

입력·저장 검증만 대조하고 실제 HSMS 세션 생성 경로의 공급자 차이를 확인하지 않아, 사용자가 저장한 타이머·LINKTEST·최대 메시지 크기가 통신에 적용된다고 이해할 여지를 남겼다. 제품 코드는 수정하지 않고 SECS 입력 절에 Device ID만 반영된다는 판본 조건을 추가했다. 기존 부모 317자에 미적용 항목·참조 안내를 모두 넣으면 450자를 넘으므로 직접 이어지는 적용 경계 절을 분리했다. 기존 입력·숫자 제한 문장은 그대로 보존했다.

- HsmsRuntimeContextProvider L41~69에서 info.hsmsDeviceId()의 선택과 Gateway 설정의 toSessionDefaults만 세션 설정으로 변환하는 흐름을 확인했다.
- GatewayHsmsProperties L301~325는 전달받은 equipmentDeviceId 외의 다섯 타이머·LINKTEST 사용 여부/주기·최대 프레임을 프로퍼티에서 읽는다. GatewayEquipmentService L439~451과 GatewayUiRuntimeControlSupport L274~287은 설비 저장값을 프로필로 복사·재구성한다.
- Gateway 제품 Java에서 `.t3Timeout()`·`.t5Timeout()`·`.t6Timeout()`·`.t7Timeout()`·`.t8Timeout()`·`.linkTestEnabled()`·`.linkTestInterval()`·`.maxMsgBytes()` 명시적 호출을 git grep으로 검색했다. 결과는 위 두 프로필 복사·재구성 위치였다. 주석이나 이름의 풀이를 근거로 미적용을 단정하지 않고 실제 세션 공급 경로와 대조했다.

재사용 범위: 작성 규칙 SHA-256 273f44daeb41ffe7a63fbd2231c8b73f58464af19a6a580523e82ca5c578e13b, 문서 목록 SHA-256 45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b가 앞선 전체 읽기 판본과 같음을 확인했다. 미변경 문장·원천·이전 대조 근거는 재사용하고 이번 결함과 세션 공급 경로만 추가 확인했다.

- 보완 전 검색 원문 SHA-256: `a38e750151ef61a8f1fcea26d711944c1316daddbd65bc26b6d2d71ab7e46f09`
- 보완 후 검색 원문 SHA-256: `66d9cf3485a438525e118018d429d10042abd7dad69b989f83b59b6b96ce0635`
- 보완 전 작성 기록 SHA-256: `971edeef64c3e1778f270cec2b3e7604998daff5248c1e87e5cea7478366ed98`
- 최종 검색 원문: 406줄·25절·83자식. 기존 사실 대조 137행을 보존하여 줄 번호를 동기화하고 새 문장 4행을 절별 표에 추가했다. 총 141행 모두 최종 원문 줄에 대응한다.
- diff: H1의 적용 판본에 Gateway를 추가하고 확인일을 2026-11-19로 갱신했다. Gateway 원천 메타 1줄과 안내·새 절 17줄을 추가했다. 기존 본문 문장은 삭제·교체하지 않았다.
- 인용 원천의 현재 HEAD 및 정확한 경로별 git status를 확인했다. front 25개·backend 13개·exec 5개·common 2개·gateway 4개, 총 49개 고유 파일 모두 status stdout 없음·exit 0이었다. gateway HEAD는 d3636ab86763이다.

### 사실 소유와 검사

타이머 값·단위·출처의 상세는 `00.rag/user/secs-hsms-message-and-timer-reference.md`가 소유한다. 해당 문서를 읽고 표시 제목과 objectKey를 확인했으며 수치·설정 키를 복제하지 않았다. 다른 문서는 수정하지 않았다. 이번 범위에서 원천 부재로 생략한 새 사실이나 사용자 결정 항목은 없다.

검사 명령을 별도 환경 설정이나 도구 수정 없이 그대로 실행했다. 이번 요청에서 검사기 호출은 1회, 최종 exit 0이다. 최초 도구 반환은 대기 중 10.0160669초에 세션 ID를 반환했고 이후 완료를 회수했으므로 전체 wall time을 추산하지 않는다. 추가 AI 호출 0회이며 provider 사용량·비용은 미확인이다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/equipment-registration-and-settings.md
```

마지막 출력 10줄:

```text
    L382-382 문단 111  | 「설비 등록 실패」·「설비 수정 실패」·「설비 삭제 실패」가 나오면 결과 본문의 서버 문구와 표시된 「작업 ID」를 읽는다. 서버 문구가 전달되지 않으면 설비 ID가 포함된 기본 실패 문구가 나온다.
    L384-384 문단 98  | 실패 창에는 「다시 조회」가 없고 「확인」만 있다. 「확인」으로 닫기 전에 설비 ID·발생 시각·실행한 작업·결과 문구·표시된 「작업 ID」를 기록해 운영 담당자에게 전달한다.
§ L386-406 [26] 같은 요청 키나 기준 상태 변경으로 거부됐을 때   parent=303 children=4 sources=11
    L401-401 문단 111  | 「같은 요청 키로 다른 내용이 접수되어 거부되었습니다.」로 시작하는 안내나 「요청이 기준으로 삼은 상태가 이미 바뀌었습니다.」가 나오면 같은 등록·수정·삭제를 다시 요청하기 전에 현재 값을 확인한다.
    L403-403 목록 항목 57  | 1. 입력 창의 「Cancel」을 누르고 「Gateway」 트리에서 대상 설비 ID가 있는지 확인한다.
    L404-404 목록 항목 71  | 2. 설비 ID가 있으면 「Eqp Info Update」를 다시 연다. 현재 값 조회가 끝난 뒤 요청 전에 고친 값과 비교한다.
    L406-406 문단 61  | 대상이 없거나 현재 값을 읽지 못하면 설비 ID·발생 시각·실행한 작업·거부 문구를 운영 담당자에게 전달한다.
SUMMARY sections=25 children=83 maxChild=192 maxParent=429
CORPUS docs=41/53 sections=574 part={admin=9, user=32} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=3, 트러블슈팅=12}
TOTAL files=41 targets=1 violations=0
```

마지막 원문 편집 뒤 검사기 위반 0건·CROSS VIOLATION 0건·MASK 0건을 확인했다. 최대 자식 192자·최대 부모 429자다. 25절 전체의 제목·본문 소유 경계를 읽고, 추가 문장별로 코드·참조 문서를 직접 대조했다. 미변경 입력 절은 보존하면서 저장 검증과 실제 통신 적용이 다른 것임을 명시했으며, 판본을 본문에도 적었다. 이전 절이나 참조 문서와 같은 자식 본문을 추가하지 않았다.

실제 브라우저·설비 통신·현장 타이머·제품 시험·빌드·배포·업로드는 실행하지 않았다. 문서 보완 자체 검사와 메인 세션의 후속 독립 검증을 구분한다.

### 임시 자원

지정 work/에만 비교용 hsms-values-before.md와 검사 출력 hsms-values-check.txt를 만들었다. 기준 SHA·diff 범위·필요한 검사 출력은 위에 보존했다. 두 소유 파일만 경로·보호 속성·reparse·독점 읽기 가능 여부와 소유 호출 종료를 확인한 뒤 비강제 native PowerShell로 정리하고 부재를 확인한다.

SECS 보완 정리 실측: 검사·비교 호출의 종료 결과를 회수한 뒤 아래 두 소유 파일과 work 부모의 정확한 절대경로·작업공간 범위 및 ReadOnly·Hidden·System·ReparsePoint 속성 부재를 확인했다. 각 파일을 Read/FileShare.None으로 독점 열어 파일 공유 충돌이 없음을 확인하고 즉시 닫았다. 이전 WMI 접근 거부는 재시도하지 않았다. 비강제 native PowerShell `Remove-Item -LiteralPath`로 정상 삭제했다. 명령 exit 0, 두 Test-Path 모두 False, 지정 work 잔여 파일 0개다.

- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/hsms-values-before.md
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/hsms-values-check.txt

SECS 보완 최종 상태: 지적된 저장·통신 적용 경계 누락 1건을 수정하고 자체 검사에서 미해소 문서 결함 0건·사용자 결정 필요 0건을 확인했다. 최종 원문 SHA·줄 수·절 수·사실 대조 행을 동기화했다. 제품 코드·다른 문서·설정·서버는 변경하지 않았다. 메인 세션의 후속 독립 검증은 별도다.

## 2026-11-19 Socket 저장값과 실행 적용 경계 보완

### 결함과 직접 영향

입력·저장 검증만 설명해 Socket 설정값 전체가 설비 통신에 적용된다고 이해할 여지를 남겼다. 실제 실행 프로필·설정 공급자를 대조하여 기존 Socket 입력 절에 Socket Protocol Type만 반영된다는 판본 조건을 추가했다. 입력 절의 기존 부모 313자에 모든 미적용 항목과 참조 경로까지 넣으면 450자를 넘으므로 바로 다음에 적용 경계 절을 분리했다. 제품 코드는 수정하지 않았다.

SocketSubProfileLoader L25~31은 저장 행에서 socketProtocolType만 넘긴다. SocketRuntimeContextProvider L45~54는 profile.socketType, Gateway 설정의 getMaxFrameBytes·isAllowEmptyFrame으로 SocketTypeConfig를 생성한다. EquipmentContextProfile L111~122, GatewayEquipmentService L464~475, GatewayUiRuntimeControlSupport L289~301의 일곱 필드를 각각 대조하여 저장·프로필 복사와 실행 적용을 구분했다.

제품 Java의 `.charset()`·`.heartbeatEnabled()`·`.heartbeatInterval()`·`.readTimeout()`·`.writeTimeout()`·`.maxFrameSizeBytes()`·`.keepAliveEnabled()` 명시적 호출을 git grep으로 검색했다. 결과는 위 프로필 복사·재구성 두 위치였다. 선언 이름이나 주석만으로 미적용을 단정하지 않고 실행 설정 생성 코드까지 확인했다.

기존 규칙·문서 목록의 SHA-256은 앞선 전체 읽기와 동일하다(규칙 273f44daeb41ffe7a63fbd2231c8b73f58464af19a6a580523e82ca5c578e13b, 목록 45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b). 미변경 문장·원천·이전 대조 근거는 재사용했다.

- 보완 전 검색 원문 SHA-256: `66d9cf3485a438525e118018d429d10042abd7dad69b989f83b59b6b96ce0635`
- 보완 후 검색 원문 SHA-256: `7c972d52fe65a14b67756c45a7c131b9601e8c03eab7ed109559acda1f9e4a65`
- 보완 전 작성 기록 SHA-256: `8c7e20f24027619c5f8b59772c0982363c95e635c74a3221c646ed60cd69a295`
- 최종 원문: 426줄·26절·87자식. 기존 141개 사실 대조 행을 보존하여 줄 번호를 동기화하고 새 문장 4행을 추가해 총 145행이다.
- diff는 Socket 원천 메타 2줄, 경계 안내와 새 절 18줄의 추가뿐이다. 기존 원문 406줄을 삭제·교체하지 않았다. 특히 SECS 입력·적용 경계 두 절은 비교 전후 완전 일치함을 확인했다.
- 인용 원천의 현재 HEAD와 경로별 git status를 확인했다. front 25개·backend 13개·exec 5개·common 2개·gateway 7개, 총 52개 고유 파일 모두 status stdout 없음·exit 0이다. Gateway HEAD는 d3636ab86763이다.

### 사실 소유와 검사

실제 문자셋·프레임 상한 및 출처의 상세는 `00.rag/user/socket-framing-and-parsing-reference.md`가 소유한다. 해당 문서를 읽고 제목·objectKey를 확인했다. 사용자 지시가 요구한 입력값의 적용 경고만 유지하고 실제 문자셋·수치·설정 키·파싱 규칙은 복제하지 않았다. 다른 문서는 수정하지 않았다. 이번 범위의 새 원천 부재 사실이나 사용자 결정 항목은 없다.

수정된 check.sh를 별도 환경 설정 없이 그대로 실행했다. 이번 요청의 검사기 호출은 1회이며 최종 exit 0이다. 최초 도구 반환은 대기 중 10.0073864초에 세션 ID를 반환했고 이후 완료를 회수했으므로 전체 wall time을 추산하지 않는다. 추가 AI 호출 0회, provider 사용량·비용은 미확인이다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/equipment-registration-and-settings.md
```

마지막 출력 10줄:

```text
    L402-402 문단 111  | 「설비 등록 실패」·「설비 수정 실패」·「설비 삭제 실패」가 나오면 결과 본문의 서버 문구와 표시된 「작업 ID」를 읽는다. 서버 문구가 전달되지 않으면 설비 ID가 포함된 기본 실패 문구가 나온다.
    L404-404 문단 98  | 실패 창에는 「다시 조회」가 없고 「확인」만 있다. 「확인」으로 닫기 전에 설비 ID·발생 시각·실행한 작업·결과 문구·표시된 「작업 ID」를 기록해 운영 담당자에게 전달한다.
§ L406-426 [26] 같은 요청 키나 기준 상태 변경으로 거부됐을 때   parent=303 children=4 sources=11
    L421-421 문단 111  | 「같은 요청 키로 다른 내용이 접수되어 거부되었습니다.」로 시작하는 안내나 「요청이 기준으로 삼은 상태가 이미 바뀌었습니다.」가 나오면 같은 등록·수정·삭제를 다시 요청하기 전에 현재 값을 확인한다.
    L423-423 목록 항목 57  | 1. 입력 창의 「Cancel」을 누르고 「Gateway」 트리에서 대상 설비 ID가 있는지 확인한다.
    L424-424 목록 항목 71  | 2. 설비 ID가 있으면 「Eqp Info Update」를 다시 연다. 현재 값 조회가 끝난 뒤 요청 전에 고친 값과 비교한다.
    L426-426 문단 61  | 대상이 없거나 현재 값을 읽지 못하면 설비 ID·발생 시각·실행한 작업·거부 문구를 운영 담당자에게 전달한다.
SUMMARY sections=26 children=87 maxChild=192 maxParent=429
CORPUS docs=43/53 sections=626 part={admin=9, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=5, 트러블슈팅=12}
TOTAL files=43 targets=1 violations=0
```

마지막 원문 편집 뒤 검사기 위반 0건·CROSS VIOLATION 0건·MASK 0건을 확인했다. 최대 자식 192자·최대 부모 429자다. 26절의 제목·본문 소유 경계를 읽고 새 문장 모두를 원천과 대조했다. 나머지 25절은 보존된 문장과 판본·status가 같은 기존 근거를 재사용했다. 입력·저장 조건과 실행 적용의 차이를 판본과 함께 썼고 실제 문자셋·상한의 상세는 참조 문서에 남겼다.

실제 브라우저·설비 통신·현장 Socket 설정·제품 시험·빌드·배포·업로드는 실행하지 않았다. 문서 자체 보완 판정과 메인 세션의 후속 독립 검증은 별도다.

### 임시 자원

지정 work/에만 비교용 socket-values-before.md와 검사 출력 socket-values-check.txt를 만들었다. 기준 SHA·diff·필요한 검사 출력은 이 기록에 보존했다. 두 소유 파일만 경로·보호 속성·reparse·독점 읽기 가능 여부와 소유 호출 종료를 확인한 뒤 비강제 native PowerShell로 정리하고 부재를 확인한다.

Socket 보완 정리 실측: 검사·비교 호출 종료를 회수한 뒤 아래 두 소유 파일 및 work 부모의 정확한 절대경로·허가된 작업공간 범위와 ReadOnly·Hidden·System·ReparsePoint 속성 부재를 확인했다. 각 파일을 Read/FileShare.None으로 독점 열어 파일 공유 충돌이 없음을 확인하고 즉시 닫았다. 이전 WMI 접근 거부는 재시도하지 않았다. 비강제 native PowerShell `Remove-Item -LiteralPath`로 정상 삭제했다. 명령 exit 0, 두 Test-Path 모두 False, 지정 work 잔여 파일 0개다.

- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/socket-values-before.md
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/socket-values-check.txt

Socket 보완 최종 상태: 지적된 저장·실행 적용 경계 누락 1건을 수정하고 자체 검사에서 미해소 문서 결함 0건·사용자 결정 필요 0건을 확인했다. 최종 원문 SHA·줄 수·절 수·사실 대조 행을 동기화했다. 직전 SECS 보완과 나머지 본문은 보존했으며 제품 코드·다른 문서·설정·서버는 변경하지 않았다. 메인 세션의 후속 독립 검증은 별도다.

## 2026-11-19 제품 수정 C 반영

### 범위와 변경 원인

사용자가 검증한 제품 수정 A~D의 길잡이를 받은 뒤 검색 원문 전체 26절을 다시 읽었다. HSMS·Socket 미적용 안내가 새 실행 경로와 달라진 것이 원인이다. 본문의 변경은 SECS 입력·적용 경계, Socket 입력·적용 경계 네 절에 한정했다. 나머지 22절은 보완 전 파일과 절 전체가 완전 일치함을 difflib와 직접 읽기로 확인했다. A의 Kafka 연결 보안 전달, B의 Secret 생성 linger, D의 PLC Register Byte Order·Word Order·Flyway 버전 설명은 이 문서에 없다. PLC Settings의 연결·폴링 입력 사실은 D의 모델 레지스터 저장 변경과 다른 대상이므로 보존했다.

- 보완 전 원문 SHA-256: `7c972d52fe65a14b67756c45a7c131b9601e8c03eab7ed109559acda1f9e4a65`
- 보완 전 작성 기록 SHA-256: `76afd33a628fca2fac027937d8fea6a47e804632fdeb8dec3b6da73e925bd8d7`
- 보완 후 원문 SHA-256: `3286079dc33b2b262c4b2cedb1577d1404f259ca0a7525c318d0b56966ea1147`
- 보완 후 원문: 432줄·26절·87자식, 사실 대조 148행. 87개 비메타 자료 줄이 사실 대조 표에 모두 대응하며, 각 표의 문장이 해당 원문 줄에 실제로 있는 것을 확인했다.
- 바뀐 본문은 보완 전 L202·L213·L215·L217·L231·L235·L247·L249·L251이다. H1 적용 메타와 이 네 절의 새 사실에 필요한 원천 메타를 함께 맞췄다. 확인일은 이미 2026-11-19였으므로 유지했다.

### 현재 작업 트리 원천 판본

Gateway HEAD를 다시 조회한 값은 d3636ab86763이다. 아래 파일은 정확한 경로별 status 명령 exit 0과 SHA-256을 쓰기 전·대조 때 확인했다. 수정 파일은 원천 메타와 바뀐 사실 대조 행에서 파일 SHA 판본으로 인용했다. 새 시험 파일의 `??`는 추적 전 파일을 뜻하며 시험을 실행했다는 뜻이 아니다. 바뀌지 않은 사실에 붙은 기존 commit 원천은 사용자 지시대로 유지했다. 위쪽의 이전 전체 clean 판정은 당시 이력이며 현재 작업 트리 전체가 clean이라는 주장이 아니다.

| 파일 | 현재 파일 판본 | status |
|---|---|---|
| nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java | sha256=cc01f2954d144807afbfa05c3a24102a5e21d252701b6aec9b817d33cdf79f36 | M·exit 0 |
| nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java | sha256=ab03104531dc8d6a4e36e9136f426ed4549d4d16df5a807b5d54ab8888d95f72 | M·exit 0 |
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java | sha256=d140c3822df75d2fdd617b66109155200939ea5f53ef7c1cf6c402746fbdb0f3 | M·exit 0 |
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java | sha256=5c80140c79228f305e9c7f4d217858c02109615af83b8cd46240bc2dae78ff85 | M·exit 0 |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java | sha256=a5cd01b549f093a716eb78120c6e50937c054bf003aa54ee6111be2b6b302472 | M·exit 0 |
| nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java | sha256=9c0d83ba7bcece4ec267007699b90ccddd24cf280f5a9a73a1ce87f721c47fde | M·exit 0 |
| nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java | sha256=f44c0a52f07c9ff1e8bcc5a849b36abf3adf0a556485743f656f39f7bce81d5c | M·exit 0 |
| nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/HsmsSubProfileLoader.java | sha256=7e3e501f651aeb2ca67679ba105884de34fe56d85d463e609b418bbfd5b33141 | M·exit 0 |
| nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java | sha256=9681a97701f5365fd7d8d27a1175fa6d02ca8dde48f92bc9b1f7900559d3e245 | M·exit 0 |
| nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java | sha256=0b464a7edbaa409d6a422599eec2d65e8f120f178fb078fe4baef622feee7fc9 | M·exit 0 |
| nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java | sha256=ea2e78e28707ca1d32e84d01f72e787d09aee4075730acd982f9d0f4cb5b0d9b | M·exit 0 |
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/test/java/com/nori/tc/gateway/starter/runtime/context/EquipmentCommunicationSettingsTest.java | sha256=4bb74d9a959d1ed03a0cce049b41c22c51d0b35c5dd95855268c4c4c3d23c3c5 | ??·exit 0 |
| nori-tc-gateway/gateway/tc-gateway-socket/src/test/java/com/nori/tc/gateway/socket/pipeline/SocketEquipmentCharsetTest.java | sha256=fefe2c47367133253fda2e27f4ca20eeebec1267203ba21e0d61fabbf15ccb70 | ??·exit 0 |

LineDelimitedSocketTypeHandler.java는 정확한 경로별 status stdout 없음·exit 0을 확인했다. SHA-256은 f52611e1cc7234ae8f542ad051f3fe5db2fa99f52e48f92657b97c90073b8bdb이며 L54~63의 LF 조건은 기준 commit d3636ab86763 원천으로 썼다.

### 호출 흐름과 사실별 대조 보충

- HsmsSubProfileLoader L27~33은 저장된 타이머·Link Test·최대 메시지 크기를 모두 가져오고, GatewayEquipmentService L293~308은 HSMS·Socket 설정을 GatewayEquipmentInfo로 넘긴다. HsmsRuntimeContextProvider L41~69는 Device ID를 고르고, 설정이 있으면 타이머 다섯 값과 Link Test 주기에 1000L을 곱해 세션 설정으로 넘긴다. 설정이 없으면 toSessionDefaults를 쓴다. Max Message Bytes가 maxFrameBytes로 전달되는 것과 HsmsSessionConfig의 필드·검증을 읽어 바이트 단위를 확인했다. 따라서 타이머의 입력은 초로 썼다. 세션 설정 전달을 T3 대기 등록이나 T5 실행 사용처 추가로 확대하지 않았다.
- SocketSubProfileLoader L27~33과 GatewayEquipmentService L293~308을 거쳐 SocketRuntimeContextProvider L45~62가 설비 프레임 상한과 문자셋을 넘긴다. GatewayEquipmentInfo L55~64의 Charset.forName 실패는 설비 ID를 포함한 오류로 전달된다. 입력 단계의 저장 거부와 이 통신 설정 구성 실패를 구별했다.
- SocketTypeRegistry L51~63은 내장 LINE·REGEX에만 문자셋을 적용한다. SocketTypeHandlerResolver L41~71은 플러그인 대여 처리기가 있으면 먼저 반환하여 설비 문자셋을 전달하지 않는다. SocketInboundPipeline L106~127은 설비 문자셋으로 처리기를 선택하고 설비 maxFrameBytes로 프레임을 추출한다. RegexDelimitedSocketTypeHandler L94~97은 종료 정규식을 유지해 문자셋을 바꾼 처리기를 만든다.
- LineDelimitedSocketTypeHandler L54~63은 LF의 바이트 길이 1과 값 0x0A를 검사한다. 본문에는 입력에 필요한 문자셋 조건만 보충하고 수신 장애 진단·격리 절차를 복제하지 않았다. 문자셋을 구성 시 거부하는 경우와 LINE 처리기 생성 시 거부하는 경우를 같은 시점으로 쓰지 않았다.
- GatewayActiveReconnectCoordinator L232~239의 ACTIVE SO_KEEPALIVE와 EqpBindingService L299~307의 PASSIVE 설비 확정 뒤 SO_KEEPALIVE 적용을 각각 읽었다. 따라서 Keep Alive Enabled를 미적용 목록에서 뺐다.
- 제품 Java의 heartbeatEnabled()·heartbeatInterval()·readTimeout()·writeTimeout() 호출을 git grep으로 확인했다. GatewayEquipmentService L471~474, SocketSubProfileLoader L32, GatewayUiRuntimeControlSupport L279~282의 복사와 현재 실행 설정 공급자를 함께 대조해 네 항목의 미적용 안내를 유지했다.
- EquipmentCommunicationSettingsTest L30~103의 초→ms 변환·설비 상한·LINKTEST 설정·설정 부재·잘못된 값·문자셋 decode/encode 기대값, SocketEquipmentCharsetTest L25~74의 설비 문자셋·상한·플러그인 불변 기대값을 읽었다. 제품 시험·빌드를 실행하거나 메인 세션의 시험을 이 세션의 PASS로 기록하지 않았다.

이번 변경으로 추가 확인한 단위는 입력 절에만 두고, 통신 적용 범위는 다음 적용 경계 절에만 두었다. 실제 타이머 의미·T3/T5 사용 범위·문자셋별 파싱·구체 프레임 상한은 두 참조 문서 소유로 남겼다. 새 매뉴얼 불일치는 단정하지 않았으며 기존 매뉴얼 대조 표를 보존했다. 현장 설치에 이 수정이 반영됐는지는 조회하지 않았고 본문에 적용을 단정하지 않았다.

### 다른 문서 발견 사항

| 문서·줄 | 발견 사항 | 현재 제품 근거 |
|---|---|---|
| 00.rag/user/secs-hsms-message-and-timer-reference.md L110·L127~131 | 실제 연결이 Gateway 설정만 쓴다는 설명과 Device ID 외 저장값 미적용 설명은 수정이 들어간 판본에 맞춰 구분해야 한다. | HsmsRuntimeContextProvider의 현재 SHA 판본 L56~69는 설비 설정을 우선 사용한다. |
| 00.rag/user/socket-framing-and-parsing-reference.md L24~28·L82 | 문자셋·프레임 상한·Keep Alive 미적용 및 프레임 상한을 Gateway 설정에서만 공급한다는 설명은 수정 판본에 맞춰 구분해야 한다. | SocketRuntimeContextProvider L45~62, SocketTypeRegistry L51~63, SocketTypeHandlerResolver L41~71, ACTIVE·PASSIVE SO_KEEPALIVE 적용 코드. |

참조 문서의 기존 commit 판본 설명 자체를 당시 코드와 모순이라고 판단하지 않았다. 새 수정 반영 필요 사항으로 보고했으며 다른 문서는 고치지 않았다.

### 검사 결과와 남은 조건

규칙 파일은 현재 SHA-256 f9857ff7ad9cbdb46b47c229319ee7fba9d123d13c42a1f92dff1a337d97f6db 판본을 다시 읽었다. 목록 SHA-256은 45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b로 기존 읽기와 같다. 문서 한 개의 보완과 필요한 자체 검사만 수행했다. 추가 AI·독립 Agent 호출은 0회다. provider 사용량·비용과 전체 wall time은 미확인이다.

처음 지정된 Temp의 tools/check.sh는 Test-Path False이다. 현재 작업 폴더 아래에서 발견한 검사기는 C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh 하나였다. 이 파일을 별도 저장소·환경 설정 없이 그대로 실행했다.

```text
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/equipment-registration-and-settings.md
```

명령 exit 1이며 출력의 원문 SHA가 보완 후 최종 SHA와 일치한다. 마지막 출력 10줄:

```text
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 요청 키 충돌 안내 L1109~1109'
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Cancel 단추 L1385~1394'
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 창 Cancel 단추 L67~75'
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관리 상세 조회 조건 L388~403'
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관리 창 닫기·다시 열기 L474~487'
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/hooks/useEqpManageDetail.ts @ 9fe69a75dcd3 ; 관리 상세 재조회 L9~16'
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; getEqpManageDetail L290~305'
VIOLATION L413 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 조회값으로 입력 갱신 L541~550'
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=153
```

출력 전체를 세어 153건 모두 `원천 commit:path 없음`인 것을 확인했다. 길이·구조·SHA 원천·줄 범위의 다른 위반은 없으며 MASK 0건·CROSS VIOLATION 0건이다. SUMMARY는 sections=26 children=87 maxChild=192 maxParent=443이다. 두 산출물은 UTF-8 BOM 없음·LF·NFC를 확인했다.

현재 검사기 RagDocCheck.java L403~412의 Git 호출은 `git -C`만 넣고 stderr를 버린다. 해당 사본에는 앞서 고쳤다고 안내받은 프로세스 전용 safe.directory 및 Git stderr 보완이 없다. 실제 `git -C C:/workspace_nori/nori-tc/nori-tc-ui-front show 9fe69a75dcd3:src/app/Router.tsx`를 재현한 exit는 128이고 stderr는 `fatal: detected dubious ownership in repository`이다. 같은 명령에 명령별 safe.directory를 준 읽기는 exit 0·139줄로 성공했다. 따라서 Git 거부를 파일 부재로 확정하지 않았다. 검사기·클래스·Git 전역 설정을 고치거나 검사를 우회하지 않았다.

- check.sh SHA-256: ea487ba231bee8b7f82d69afb2a4ed0f8c4c7015b957b0207abdbdecd03cc189
- RagDocCheck.java SHA-256: 1a4a95236f567c0996f30c481e92f1e54d4839526a68fd388abb0ee9fd2697d9
- classes/RagDocCheck.class SHA-256: 3bc9dd269677b2469c11b1a9fc2e14ba0f3eed7c3b1b179a6f0f5610039db98a

제목·본문 중복·빈 틀·사용처 근거·문장·마스킹·user 권한 경계를 26절 전체에 대해 읽었다. 바뀐 네 절은 각각 제목에서 입력 조건 또는 저장·실행 경계 질문이 드러나고 독립 답이며, 코드와 맞고 수치 참조의 소유 범위를 침범하지 않는 것으로 판정했다. 나머지 22절은 보존된 절별 사실 대조와 이번 전체 읽기를 재사용했다. 사람 검사에서 새 미해소 문서 결함은 찾지 못했으나 필수 기계 검사 exit 0을 충족하지 못했으므로 전체 완료로 판정하지 않는다. 수정된 검사기 경로를 사용자에게 질문했고, 이 문서 외 파일을 수정해 해결하지 않는다.

실제 브라우저·현장 설비 통신·설치 판본·제품 시험·배포·업로드는 확인하지 않았다. 메인 세션의 후속 독립 검증은 별도이다. 필요한 다음 작업은 안전 경로·stderr 보완이 들어간 정식 검사기로 동일 원문 SHA를 재검사하는 것이다. 현재의 같은 Git 거부를 반복 실행하거나 원천 commit을 임의 SHA로 바꾸지 않는다.

### 이번 요청의 임시 자원

지정된 원래 work 경로에 아래 두 파일만 만들었다. 비교 SHA·변경 범위·검사 요약·마지막 10줄·Git 실패 원인은 위에 보존했다. 두 파일의 용도와 최종 해시는 다음과 같다. 검사 프로세스는 종료코드 1을 회수했으며, 파일별 보호 속성·reparse·독점 읽기를 확인한 뒤 비강제 native PowerShell로 이 두 소유 파일만 정리한다. 이전 WMI 접근 거부 경로는 재시도하지 않는다.

- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/product-c-before.md — 원문 변경 범위 대조용 사본, sha256=7c972d52fe65a14b67756c45a7c131b9601e8c03eab7ed109559acda1f9e4a65
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/product-c-check.txt — 검사기의 전체 출력, sha256=42b232da29b625aef530ec4a51107070168f1c2b9872bd518ca2dac2d1fddbf6

정리 실측: 두 소유 파일과 부모의 보호 속성·ReparsePoint 부재, 정확한 절대경로·부모 범위, FileShare.None 독점 읽기 가능 여부를 확인했다. 종료한 소유 검사·비교 호출의 파일만 비강제 Remove-Item -LiteralPath로 삭제했다. 명령 exit 0, 두 Test-Path False, work 잔여 0개이다. 작업 폴더 부모나 다른 세션의 파일은 삭제하지 않았다.

이번 보완의 최종 상태는 PARTIAL이다. 제품 수정 C에 맞춘 본문·메타·148개 사실 대조를 완료했고 새 문서 결함은 발견하지 못했지만, 현재 검사기의 Git 원천 확인 153건·exit 1이 남아 필수 위반 0건 조건은 미충족이다. 수정된 검사기 위치가 확인되면 같은 원문 SHA를 재검사해야 한다. 제품 코드·다른 문서·검사기는 수정하지 않았다. 사용자 제품 결정이 필요한 항목은 없다.

## 2026-11-19 보완 6: Charset 저장과 거부 시점 정정

### 원인과 최소 변경

보완 5의 L237은 Gateway의 실행 문맥 생성 실패를 「설비 통신 설정 구성 시 거부」라고만 적어 저장 시 문자셋 이름을 검사한다는 뜻으로 읽힐 여지가 있었다. 화면 자유 입력·서버 저장·Gateway 오류를 구분해 썼다. L231은 문자셋 입력을 설명할 뿐 이름 검사를 주장하지 않으므로 보존했다.

변경은 L237 한 줄을 Charset 절로 교체한 것뿐이다. 기존 입력 절 본문은 해당 문단을 빼면 322자이고, 정정한 내용은 228자여서 합치면 450자를 넘는다. 해당 문단만 「Charset 저장과 Gateway 통신 실패 구분하기」로 나눴다. 기존 LF 조건 문장은 글자를 바꾸지 않고 새 절에 두었다. 기존 원천 줄과 나머지 25절은 완전 일치함을 비교했다. 새 절의 원천 5줄만 추가했다. H1 메타·다른 입력·SECS·Socket 적용 경계 설명은 보존했다.

- 보완 전 원문 SHA-256: `3286079dc33b2b262c4b2cedb1577d1404f259ca0a7525c318d0b56966ea1147`
- 보완 전 작성 기록 SHA-256: `dd27e8d3e37dc87566fb1c18df29daf92271216494944feccb3d0dcfab982f6e`
- 보완 후 원문 SHA-256: `1e6dfaac3f60de57f6e8e0523b56d29e1e81849edf088c4207ce67619bd16ae1`
- 최종 원문: 445줄·27절·89자식. 사실 대조 표는 150행이다. 비메타 자료 줄 89개 모두에 대조 행이 있고 각 문장이 해당 원문 줄에 실제로 있는 것을 확인했다. 새 절 뒤의 줄 번호·절 번호와 대상 요약을 동기화하고 이전 보완 이력은 보존했다.

### 현재 원천 판본과 호출 흐름

아래 파일별로 현재 HEAD와 정확한 경로의 status를 확인했다. Git은 명령별 safe.directory만 지정했고 전역 설정은 바꾸지 않았다. 규칙·목록은 직전 읽기와 SHA가 같아 재사용했다. 규칙 SHA는 f9857ff7ad9cbdb46b47c229319ee7fba9d123d13c42a1f92dff1a337d97f6db, 목록 SHA는 45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b이다.

| 파일 | 확인한 판본·작업 트리 | 직접 확인한 내용 |
|---|---|---|
| nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx | 9fe69a75dcd3·status stdout 없음·exit 0; sha256=ce305bff778d8e80fa87572d352060c5038bae7ca8af0eb1302d5b8f9e17f5e7 | L1293~1300은 Input이며 문자셋 이름 검사 조건이 없다. L697·L781은 선택 텍스트로 요청한다. |
| nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java | 985d17c604dd·status stdout 없음·exit 0; sha256=c03cebbf3124ae02bd5886f1dbff348892c2021b40ef49ced667861e5f7d05fa | L888~904는 이름 검사 없이 앞뒤 공백 제거·빈 값 UTF-8을 적용하고 L517~530은 resolved.charset을 저장한다. |
| nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java | d3636ab86763·M·exit 0; sha256=a5cd01b549f093a716eb78120c6e50937c054bf003aa54ee6111be2b6b302472 | L55~64의 Charset.forName 실패를 설비 ID와 설정값을 포함하는 오류로 전달한다. |
| nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java | d3636ab86763·M·exit 0; sha256=5c80140c79228f305e9c7f4d217858c02109615af83b8cd46240bc2dae78ff85 | create L38~62 중 L45가 socketCharset을 호출한다. 실행 문맥 생성 전이며 뒤의 try 블록 밖에서 실패한다. |
| nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java | d3636ab86763·status stdout 없음·exit 0 | L54~63의 LF 문자셋 조건은 기존 문장과 같아 그대로 보존했다. |

EqpManagementRequestSupport.SocketSettingsRequest L66~75의 charset은 String이며 문자셋 이름 annotation 검사는 없다. EqpManagementCommandValidator의 charset 검색에는 결과가 없다. 실행 문맥 생성의 직접 호출처는 다음 세 파일을 읽었다. 세 경로는 d3636ab86763·status stdout 없음·exit 0이었다.

- nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/runtime/mailbox/EquipmentMailboxRegistry.java @ d3636ab86763 ; newMailbox L205~208의 contextFactory.create
- nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/DefaultEquipmentRuntimeContextFactory.java @ d3636ab86763 ; create L75~89의 providerRegistry.create
- nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/EquipmentRuntimeContextProviderRegistry.java @ d3636ab86763 ; create L44~51의 provider.create

이 경로 뒤 SocketRuntimeContextProvider L45 → GatewayEquipmentInfo.socketCharset L55~64로 이어지는 것을 확인했다. 본문에는 Socket 실행 문맥을 만들 때 오류로 실패한다고만 썼다. EquipmentCommunicationSettingsTest L95~103도 다시 읽어 잘못된 문자셋에서 create가 IllegalArgumentException을 던지고 설비 ID와 입력값을 포함하는 기대값을 확인했다. 시험 자체는 실행하지 않았다.

### 원천이 없어 쓰지 않은 화면 표시와 사람 검사

문자셋 오류가 특정 화면 동작의 어느 결과 창·문구로 표시되는지는 위 생성 호출만으로 확인하지 않았다. 새 UI 표시나 실패 뒤 재실행 안전성은 쓰지 않았다. 추가 확인 담당은 Gateway·UI 연동 담당이다. 실제 브라우저·설비 연결·현장 판본은 확인하지 않았다.

27절의 제목·본문을 기존 사실 대조와 보존 diff로 읽었다. 새 절은 저장 성공과 실행 실패를 구분하는 독립 답이고, 입력 절은 입력 조작을 설명한다. 새 절의 네 사실을 절별 표에 연결했으며 중복 답·빈 틀·뜻풀이·문장 오류·마스킹·user 권한 경계에서 새 미해소 결함은 발견하지 못했다. 다른 문서는 변경하지 않았다. 메인 세션의 독립 검증과 이 자체검사는 구분한다.

### 최종 기계 검사

마지막 원문 편집 뒤 검사기를 1회 실행했다. 별도 Git 환경이나 저장소 설정을 덧붙이지 않았고 검사기·제품 코드·Git 전역 설정은 수정하지 않았다. 추가 AI·하위 Agent 호출은 0회이며 provider 사용량·비용과 전체 wall time은 미확인이다.

```text
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/equipment-registration-and-settings.md
```

exit 1이다. 마지막 출력 10줄:

```text
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 요청 키 충돌 안내 L1109~1109'
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Cancel 단추 L1385~1394'
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 창 Cancel 단추 L67~75'
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관리 상세 조회 조건 L388~403'
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관리 창 닫기·다시 열기 L474~487'
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/hooks/useEqpManageDetail.ts @ 9fe69a75dcd3 ; 관리 상세 재조회 L9~16'
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; getEqpManageDetail L290~305'
VIOLATION L426 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 조회값으로 입력 갱신 L541~550'
CORPUS docs=51/53 sections=874 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=156
```

SUMMARY sections=27 children=89 maxChild=192 maxParent=443

156건 모두 원천 commit:path 부재 표시이며 MASK 0건·CROSS VIOLATION 0건이다. 현재 검사기의 safe.directory·stderr 누락 결함은 직전 기록과 동일한 코드로 남아 있다. 새 절에서 기준 commit 원천 3개를 추가하여 같은 Git 거부가 3건 늘었다. Git 거부를 원천 파일 부재로 확정하지 않았으며 본문·형식 합격과 필수 기계 검사 전체 합격을 구분한다. 필수 위반 0건·exit 0을 달성하지 못했으므로 최종 상태는 PARTIAL이다. 해결되지 않은 조건은 수정된 정식 검사기로 이 최종 원문 SHA를 다시 검사하는 것이다. 사용자 제품 결정 항목은 없다.

### 보완 6 임시 자원

기준 SHA·보존 diff 범위·최종 검사 출력과 실패 원인을 위 기록에 보존했다. 지정 work 안에 이 세션이 만든 아래 두 파일만 정리한다. 검사 호출 exit 1을 회수했고 다른 세션·제품·정본 파일은 삭제 대상이 아니다.

- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/charset6-before.md — sha256=3286079dc33b2b262c4b2cedb1577d1404f259ca0a7525c318d0b56966ea1147
- C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-04-equipment-registration-and-settings/work/charset6-check.txt — sha256=344f12aa4a9d883858b59b2fafca4ff56f5b853828d00f2ac5f14db34a2e2ce0

보완 6 정리 실측: 정확한 절대경로·부모 범위와 파일·부모의 ReadOnly/Hidden/System/ReparsePoint 속성 부재를 확인했다. 두 소유 파일을 FileShare.None으로 독점 읽기 후 닫고, 비강제 native PowerShell Remove-Item -LiteralPath로 삭제했다. 명령 exit 0, 두 Test-Path False, work 잔여 0개다. 이전 WMI 거부 경로는 재시도하지 않았다.

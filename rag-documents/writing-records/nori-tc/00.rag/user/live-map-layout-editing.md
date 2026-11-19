# Live-Map 배치·구조물 편집 작성 기록

## 대상

- objectKey: `00.rag/user/live-map-layout-editing.md`
- 구획과 열람 권한: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문: `rag-documents/nori-tc/00.rag/user/live-map-layout-editing.md`
- 검색 원문 SHA-256: `89748cd897769d6de4c21e583acb361b3f3993d2abd0665eeb902abdd74f52df`
- 줄 수: 263. H1 1개, H2 19개. 확인일: 2026-11-18 한국 시간.
- 범위: U §7.3 및 사용자가 이 문서에 배정한 U §7.1 절차 6~9와 해당 입력·저장 결과. 제품 실행·업로드·커밋은 하지 않았다.
- 이 세션이 작성·자체검사했다. 독립 공식 검증 결과로 표현하지 않는다.

## 원천 판본

| 저장소 | HEAD 12자리 | 확인 결과 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 인용 파일 대상 `git status --short -- <경로>` exit 0, 변경 출력 없음 |
| nori-tc-ui-backend | 985d17c604dd | 인용 파일 대상 같은 명령 exit 0, 변경 출력 없음 |
| nori-tc-exec | f1d5e0a8e727 | 인용 파일 대상 같은 명령 exit 0, 변경 출력 없음 |

검색 원문이 인용한 서로 다른 파일 27개의 작업 트리 텍스트와 `git show <commit>:<path>`를 LF 기준으로 대조했고 불일치가 없었다. 수정 파일이 없어서 파일 SHA 판본 대신 HEAD를 사용했다.

형제 저장소에서 최초 Git 조회는 현재 샌드박스 사용자와 저장소 소유자가 달라 `detected dubious ownership`으로 종료됐다. 이후 읽기 명령에 해당 저장소의 정확한 절대경로를 명령 범위 `-c safe.directory=<경로>`로 지정했다. 검사기 하위 Git 조회에는 같은 세 경로만 프로세스 환경 `GIT_CONFIG_COUNT=3`, `GIT_CONFIG_KEY_0~2=safe.directory`, `GIT_CONFIG_VALUE_0~2=<각 저장소 절대경로>`로 전달했다. 전역 Git 설정 파일은 수정하지 않았다. Git은 사용자 ignore 파일 접근 경고를 냈으나 후속 status는 exit 0이었고, 별도의 HEAD 텍스트 대조도 수행했다.

필수 안내인 `docs/rag-document-writing-rules.md`와 `docs/tc-rag-document-list.md` 전체, U §7.1~7.3, 기존 두 검색 원문을 읽었다. 화면 렌더·한국어 문구 → layout API → 컨트롤러·요청 DTO → 서비스 입력 검사 → JDBC 저장·기본 스키마 순서로 대조했다. 추가 자동 배치 조회의 설비 API와 기본 권한도 확인했다.

관련 시험은 `LiveMapLayoutPermissionTest`, `LiveMapLayoutServiceTest`, `LiveMapObjectServiceTest`, `tests/unit/live-map-flow.model.test.ts`, `tests/unit/live-map-3d-model.test.ts`의 해당 기대값을 읽었다. 시험을 실행해 PASS를 얻었다는 뜻이 아니다.

## 절별 사실 대조

아래 표는 최종 검색 원문의 모든 본문 문장을 문장별로 나눠 기록한다. 같은 줄에 여러 문장이 있으면 문장 순번을 붙인다. 코드가 직접 수행하는 동작과, 화면에서 관찰할 값을 이용해 작성한 사용자 확인·인계 안내를 구분한다.

최종 본문 53개 줄의 132개 문장을 대조했다. UI 문구는 각 절의 ko.json 원천과 대조했고 공통 「확인」은 ko.json L471이다.

### 배치와 구조물을 편집할 계정 준비하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L19 문장 1 | Live-Map의 배치·객체 변경에는 LIVE_MAP_LAYOUT_WRITE가 필요하다. | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L81~86<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L217~217 L880~889 | 권한 조건에 따른 편집 도구 렌더를 대조했다. |
| L19 문장 2 | 조회만 가능한 계정에는 편집 단추가 나타나지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L81~86<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L217~217 L880~889 | 권한 조건에 따른 편집 도구 렌더를 대조했다. |
| L21 문장 1 | 저장소 기본 권한 데이터는 편집 권한을 ADMIN 그룹에 연결한다. | nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L33~39<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L213~230<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 기본 ADMIN 연결과 API 인가를 대조했다. 현장 확인은 사용자 안내이다. |
| L21 문장 2 | 저장·객체 생성·수정·삭제 API도 이 권한으로 검사한다. | nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L33~39<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L213~230<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 기본 ADMIN 연결과 API 인가를 대조했다. 현장 확인은 사용자 안내이다. |
| L21 문장 3 | 현장 계정의 권한이나 권한 거부 결과는 운영 관리자에게 확인한다. | nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L33~39<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L213~230<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 기본 ADMIN 연결과 API 인가를 대조했다. 현장 확인은 사용자 안내이다. |

### 공정 그룹을 옮기거나 크기 바꾸기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L31 문장 1 | 편집 권한자가 상단바에서 편집할 라인을 고르고 「공정 흐름」을 연다. | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L105~112 L257~273 | 선택 라인을 배치 키로 해석하고 보기를 전환하는 코드를 대조했다. |
| L32 문장 1 | 보드 왼쪽 세로 막대의 연필 단추 「배치 편집」을 켠다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L880~887<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L393~393 | 연필 도구의 한국어 문구와 토글을 대조했다. |
| L33 문장 1 | 그룹 카드 머리글을 끌어 위치를 바꾼다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L259~327 L742~746 L854~870 | 그룹 머리글 이동과 오른쪽 아래 손잡이를 대조했다. |
| L33 문장 2 | 오른쪽 아래 손잡이를 끌어 크기를 바꾼다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L259~327 L742~746 L854~870 | 그룹 머리글 이동과 오른쪽 아래 손잡이를 대조했다. |
| L35 문장 1 | 이동과 크기는 보드 격자에 맞춰 조정된다. | nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L262~313<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~562 | 격자 보정·미배정 적용 및 저장 도구를 확인했다. |
| L35 문장 2 | 「미배정」 카드도 이동·크기 조절이 가능하다. | nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L262~313<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~562 | 격자 보정·미배정 적용 및 저장 도구를 확인했다. |
| L35 문장 3 | 변경을 확정하려면 보드 머리글의 「저장」을 사용한다. | nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L262~313<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~562 | 격자 보정·미배정 적용 및 저장 도구를 확인했다. |

### 공정 그룹을 추가하고 표시 속성 정하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L45 문장 1 | 「배치 편집」을 켠 공정 보드에서 「그룹 추가」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L522~534<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L384~390<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L369~378 | 추가 단추와 새 그룹 생성의 실제 연결을 확인했다. |
| L45 문장 2 | 「새 그룹」 카드가 생긴다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L522~534<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L384~390<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L369~378 | 추가 단추와 새 그룹 생성의 실제 연결을 확인했다. |
| L46 문장 1 | 그룹 카드의 연필 단추를 눌러 「그룹 편집」을 연다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~783 L913~923<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L370~370 | 연필에서 그룹 편집 대화상자가 열리는 경로를 확인했다. |
| L47 문장 1 | 「그룹 이름」·「부제」·「색상」을 정하고 「확인」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowGroupDialog.tsx @ 9fe69a75dcd3 L33~112<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L373~376 L471~471 | 입력 항목·기본 색 선택·확인 단추를 대조했다. |
| L47 문장 2 | 색상에서 「기본」을 고르면 기본 색을 쓴다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowGroupDialog.tsx @ 9fe69a75dcd3 L33~112<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L373~376 L471~471 | 입력 항목·기본 색 선택·확인 단추를 대조했다. |
| L49 문장 1 | 「그룹 이름」이 공백뿐이면 「확인」을 누를 수 없다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowGroupDialog.tsx @ 9fe69a75dcd3 L106~111<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~795 L547~562 | 공백 검사·미배정의 도구 제외·보드 저장을 확인했다. |
| L49 문장 2 | 「미배정」 카드에는 표시 속성 편집 단추가 없다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowGroupDialog.tsx @ 9fe69a75dcd3 L106~111<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~795 L547~562 | 공백 검사·미배정의 도구 제외·보드 저장을 확인했다. |
| L49 문장 3 | 그룹 편집 뒤 보드의 「저장」으로 확정한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowGroupDialog.tsx @ 9fe69a75dcd3 L106~111<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~795 L547~562 | 공백 검사·미배정의 도구 제외·보드 저장을 확인했다. |

### 설비를 공정 그룹에 배정하거나 그룹 지우기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L59 문장 1 | 「배치 편집」 중 설비 항목을 다른 그룹 카드로 끌어 놓으면 소속 그룹이 바뀐다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L415~490<br>nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L360~380 | 다중 선택·드롭 배정·배정 해제를 대조했다. |
| L59 문장 2 | 여러 설비는 Ctrl을 누른 채 눌러 선택하고, 선택한 설비 하나를 끌어 함께 옮긴다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L415~490<br>nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L360~380 | 다중 선택·드롭 배정·배정 해제를 대조했다. |
| L59 문장 3 | 「미배정」으로 옮기면 그룹 배정이 해제된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L415~490<br>nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L360~380 | 다중 선택·드롭 배정·배정 해제를 대조했다. |
| L61 문장 1 | 그룹 카드의 휴지통 단추를 누르고 삭제 확인을 확정하면 그룹이 지워진다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~795 L952~966<br>nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L387~403 | 확인 후 그룹 삭제와 배정·화살표 제거를 대조했다. |
| L61 문장 2 | 소속 설비는 「미배정」으로 이동하고 그 그룹에 연결된 화살표도 지워진다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~795 L952~966<br>nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L387~403 | 확인 후 그룹 삭제와 배정·화살표 제거를 대조했다. |
| L61 문장 3 | 「미배정」 그룹은 삭제할 수 없다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~795 L952~966<br>nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L387~403 | 확인 후 그룹 삭제와 배정·화살표 제거를 대조했다. |
| L61 문장 4 | 보드의 「저장」으로 변경을 확정한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L768~795 L952~966<br>nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L387~403 | 확인 후 그룹 삭제와 배정·화살표 제거를 대조했다. |

### 공정 그룹 사이 화살표를 추가·수정·삭제하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L70 문장 1 | 「배치 편집」 중 「화살표 추가」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L535~546<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L379~379 | 화살표 추가 단추와 그룹 수 조건을 확인했다. |
| L70 문장 2 | 그룹 카드가 둘 이상 있어야 누를 수 있다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L535~546<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L379~379 | 화살표 추가 단추와 그룹 수 조건을 확인했다. |
| L71 문장 1 | 서로 다른 「시작 그룹」·「도착 그룹」을 고른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowArrowDialog.tsx @ 9fe69a75dcd3 L54~67 L224~229<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L382~383 | 시작·도착 그룹 선택과 확인 활성 조건을 확인했다. |
| L71 문장 2 | 같은 그룹이면 「확인」이 비활성이다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowArrowDialog.tsx @ 9fe69a75dcd3 L54~67 L224~229<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L382~383 | 시작·도착 그룹 선택과 확인 활성 조건을 확인했다. |
| L72 문장 1 | 「색상」·「굵기」·「선 종류」를 고르고 「확인」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowArrowDialog.tsx @ 9fe69a75dcd3 L26~32 L117~205<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L384~389 | 색상·네 굵기 견본·세 선 종류를 확인했다. |
| L72 문장 2 | 굵기는 네 가지 선 견본, 선 종류는 「실선」·「파선」·「점선」에서 고른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapFlowArrowDialog.tsx @ 9fe69a75dcd3 L26~32 L117~205<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L384~389 | 색상·네 굵기 견본·세 선 종류를 확인했다. |
| L74 문장 1 | 기존 화살표를 누르면 「화살표 편집」이 열린다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L688~699 L925~949 L547~562<br>nori-tc-ui-front/src/features/live-map/components/LiveMapFlowArrowDialog.tsx @ 9fe69a75dcd3 L210~229 | 기존 화살표 선택에서 수정·삭제 후 저장하는 경로를 확인했다. |
| L74 문장 2 | 속성을 고치거나 「화살표 삭제」를 누른 뒤 보드의 「저장」으로 확정한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L688~699 L925~949 L547~562<br>nori-tc-ui-front/src/features/live-map/components/LiveMapFlowArrowDialog.tsx @ 9fe69a75dcd3 L210~229 | 기존 화살표 선택에서 수정·삭제 후 저장하는 경로를 확인했다. |

### 공정 보드 저장 결과 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L86 문장 1 | 「배치 편집」 중 보드 머리글 오른쪽 「저장」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~562 | 변경 점 표시와 저장 비활성 조건을 확인했다. |
| L86 문장 2 | 변경이 있으면 단추에 작은 점이 붙는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~562 | 변경 점 표시와 저장 비활성 조건을 확인했다. |
| L86 문장 3 | 변경이 없거나 저장 중이면 누를 수 없다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~562 | 변경 점 표시와 저장 비활성 조건을 확인했다. |
| L88 문장 1 | 저장 성공 시 점이 사라지고 「저장」이 비활성으로 돌아간다. | nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L45~57<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~565 | 저장 후 초안 해제·점 제거와 3D 배치 보존을 확인했다. |
| L88 문장 2 | 별도 성공 문구는 나오지 않는다. | nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L45~57<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~565 | 저장 후 초안 해제·점 제거와 3D 배치 보존을 확인했다. |
| L88 문장 3 | 공정 보드 저장은 기존 3D 배치를 유지한다. | nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L45~57<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~565 | 저장 후 초안 해제·점 제거와 3D 배치 보존을 확인했다. |
| L90 문장 1 | 저장 실패 문구는 「저장」 옆에 표시된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L563~565<br>nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L21~28<br>nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1802~1802 | 오류 위치·같은 저장 단추·라인 전환 초안 폐기를 확인했다. 재저장은 조작 안내이다. |
| L90 문장 2 | 문구를 확인하고 다시 저장한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L563~565<br>nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L21~28<br>nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1802~1802 | 오류 위치·같은 저장 단추·라인 전환 초안 폐기를 확인했다. 재저장은 조작 안내이다. |
| L90 문장 3 | 저장하기 전에 라인을 바꾸거나 화면을 떠나면 편집 내용을 잃는다. | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L563~565<br>nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L21~28<br>nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1802~1802 | 오류 위치·같은 저장 단추·라인 전환 초안 폐기를 확인했다. 재저장은 조작 안내이다. |

### 3D에서 설비·구조물을 선택하고 이동하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L100 문장 1 | 편집 권한자가 대상 라인의 「3D」 보기에서 오른쪽 위 「편집」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L429~429 L1164~1182<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1001~1001 | 편집 권한 검사와 3D 편집 토글을 대조했다. |
| L101 문장 1 | 설비 이름표나 구조물을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L312~327 L719~725 L1099~1103 | 구조물·설비 이름표의 선택과 Shift 토글을 확인했다. |
| L101 문장 2 | Shift를 누른 채 누르면 여러 배치를 선택하거나 선택에서 뺀다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L312~327 L719~725 L1099~1103 | 구조물·설비 이름표의 선택과 Shift 토글을 확인했다. |
| L102 문장 1 | 선택한 배치를 끌어 격자에 맞춰 옮긴다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L803~839 | 선택 집합에 상대 이동량을 적용하는 코드를 확인했다. |
| L102 문장 2 | 여러 개를 선택하면 함께 이동한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L803~839 | 선택 집합에 상대 이동량을 적용하는 코드를 확인했다. |
| L103 문장 1 | 화살표 키로 미세 이동한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L90~92 L759~793 | 일반·Shift 화살표의 서로 다른 이동 폭을 확인했다. |
| L103 문장 2 | Shift와 화살표 키를 함께 누르면 더 크게 움직인다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L90~92 L759~793 | 일반·Shift 화살표의 서로 다른 이동 폭을 확인했다. |
| L105 문장 1 | 입력 칸이나 선택 목록에 초점이 있으면 화살표 키는 배치를 움직이지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L764~768 L411~412<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1018~1018 | 입력 요소의 키보드 예외와 화면 이동 안내를 확인했다. |
| L105 문장 2 | 화면 자체는 오른쪽 단추로 끌어 이동한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L764~768 L411~412<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1018~1018 | 입력 요소의 키보드 예외와 화면 이동 안내를 확인했다. |

### 3D 선택 배치의 회전·층·템플릿·설비 연결 바꾸기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L114 문장 1 | 3D 편집 중 배치를 선택하면 아래 선택 막대가 열린다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L950~978 L1334~1368 L1413~1415<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1010~1017 | 선택 막대·전체 선택에 대한 회전·층·삭제 적용을 확인했다. |
| L114 문장 2 | 「반시계 15° 회전」·「시계 15° 회전」과 층 번호 「1」·「2」는 선택한 배치 전체에 적용된다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L950~978 L1334~1368 L1413~1415<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1010~1017 | 선택 막대·전체 선택에 대한 회전·층·삭제 적용을 확인했다. |
| L114 문장 3 | 「삭제」도 선택한 배치 전체를 맵에서 뺀다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L950~978 L1334~1368 L1413~1415<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1010~1017 | 선택 막대·전체 선택에 대한 회전·층·삭제 적용을 확인했다. |
| L116 문장 1 | 「객체 템플릿 교체」는 같은 종류의 객체 중에서 고른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L688~717 L958~968 L1370~1411<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109 | 같은 종류 후보·설비 전용 연결·마지막 선택 대상·연결 필수를 확인했다. |
| L116 문장 2 | 「실설비 바인딩」은 설비 배치에서만 나타나며 현재 라인의 설비를 고른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L688~717 L958~968 L1370~1411<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109 | 같은 종류 후보·설비 전용 연결·마지막 선택 대상·연결 필수를 확인했다. |
| L116 문장 3 | 두 목록의 변경은 마지막으로 선택한 배치 하나에 적용된다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L688~717 L958~968 L1370~1411<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109 | 같은 종류 후보·설비 전용 연결·마지막 선택 대상·연결 필수를 확인했다. |
| L116 문장 4 | 설비 배치를 저장하려면 실제 설비 연결을 유지한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L688~717 L958~968 L1370~1411<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109 | 같은 종류 후보·설비 전용 연결·마지막 선택 대상·연결 필수를 확인했다. |

### 3D에 구조물이나 미배치 설비 추가하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L125 문장 1 | 3D 편집 도구의 「— 객체 —」에서 구조물을 고르고 「추가」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L845~862 L1257~1278<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1043~1045 | 객체 선택 목록과 미선택 오류를 확인했다. |
| L125 문장 2 | 구조물을 고르지 않으면 「추가할 객체를 선택하세요.」가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L845~862 L1257~1278<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1043~1045 | 객체 선택 목록과 미선택 오류를 확인했다. |
| L127 문장 1 | 설비는 「— 미배치 설비 —」에서 고르고 「설비 추가」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L547~563 L888~918 L1293~1318<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1051~1055 | 현재 라인 미배치 후보·빈 선택·모든 설비 배치됨 분기를 확인했다. |
| L127 문장 2 | 선택하지 않으면 「추가할 미배치 설비를 선택하세요.」가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L547~563 L888~918 L1293~1318<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1051~1055 | 현재 라인 미배치 후보·빈 선택·모든 설비 배치됨 분기를 확인했다. |
| L127 문장 3 | 현재 라인에 이미 배치한 설비는 후보에서 빠지고, 남은 설비가 없으면 「모든 설비 배치됨」으로 표시된다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L547~563 L888~918 L1293~1318<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1051~1055 | 현재 라인 미배치 후보·빈 선택·모든 설비 배치됨 분기를 확인했다. |
| L129 문장 1 | 새 배치는 현재 보고 있는 층에 놓이며 「전체」 보기에서는 1층에 놓인다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L854~858 L897~912 L1015~1041 | 새 객체·설비의 추가 층 및 위치 조정·저장 경로를 확인했다. |
| L129 문장 2 | 추가한 뒤 위치를 조정하고 저장한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L854~858 L897~912 L1015~1041 | 새 객체·설비의 추가 층 및 위치 조정·저장 경로를 확인했다. |

### 빈 층에 기본 구조 채우기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L139 문장 1 | 3D 편집 중 「기본 구조 채우기」를 누르면 바닥·외벽·기둥·파티션을 현재 층에 함께 추가한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L864~886 L950~978<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1048~1050 | 기본 구조 생성·층·개수 표시와 개별 조정 경로를 확인했다. |
| L139 문장 2 | 「전체」 보기에서는 1층에 추가한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L864~886 L950~978<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1048~1050 | 기본 구조 생성·층·개수 표시와 개별 조정 경로를 확인했다. |
| L139 문장 3 | 완료 문구에 추가 개수가 표시되며, 각 배치는 끌기·회전·삭제로 조정할 수 있다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L864~886 L950~978<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1048~1050 | 기본 구조 생성·층·개수 표시와 개별 조정 경로를 확인했다. |
| L141 문장 1 | 대상 층에 구조물이 하나라도 있으면 단추가 비활성이고, 마우스를 올리면 이미 기본 구조가 있다는 안내가 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L587~595 L1280~1290 L1015~1041 | 현재 구조물 존재 조건·비활성·툴팁·저장 경로를 확인했다. |
| L141 문장 2 | 직접 추가한 구조물이 있어도 같은 조건이 적용된다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L587~595 L1280~1290 L1015~1041 | 현재 구조물 존재 조건·비활성·툴팁·저장 경로를 확인했다. |
| L141 문장 3 | 구조 배치를 확정하려면 「저장」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L587~595 L1280~1290 L1015~1041 | 현재 구조물 존재 조건·비활성·툴팁·저장 경로를 확인했다. |

### gateway_app 기준으로 설비를 다시 배치하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L151 문장 1 | 3D 편집 중 「gateway_app 자동 배치」를 누르면 현재 라인의 설비 전체를 담당 gateway_app 기준 층과 열에 다시 놓는다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L565~581 L920~945 | 라인 전체 설비 재배치·구조물 유지·층 미분류 처리를 확인했다. |
| L151 문장 2 | 기존 설비 배치도 교체하며 구조물 배치는 유지한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L565~581 L920~945 | 라인 전체 설비 재배치·구조물 유지·층 미분류 처리를 확인했다. |
| L151 문장 3 | 층을 분류하지 못한 설비는 1층에 놓는다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L565~581 L920~945 | 라인 전체 설비 재배치·구조물 유지·층 미분류 처리를 확인했다. |
| L153 문장 1 | 배치한 설비 수가 안내 문구에 나온다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L945~947 L486~500 L1015~1041<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1060~1060 | 개수 문구·실행 전 스냅샷·되돌리기·저장 경로를 확인했다. |
| L153 문장 2 | 위치를 확인해 끌기로 조정하고 「저장」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L945~947 L486~500 L1015~1041<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1060~1060 | 개수 문구·실행 전 스냅샷·되돌리기·저장 경로를 확인했다. |
| L153 문장 3 | 실행 직전 배치로 돌아가려면 저장 전에 「되돌리기」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L945~947 L486~500 L1015~1041<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1060~1060 | 개수 문구·실행 전 스냅샷·되돌리기·저장 경로를 확인했다. |

### 자동 배치 단추가 비활성일 때 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L164 문장 1 | 「gateway_app 자동 배치」는 현재 라인에 조회된 설비가 있고 설비 목록·담당 정보 조회가 모두 성공해야 활성화된다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L433~436 L584~585 L1323~1326 | 추가 두 조회 성공 및 라인 설비 존재 조건을 확인했다. |
| L166 문장 1 | 저장소 기본 권한 데이터 기준으로 두 추가 조회에는 EQP_READ·EQP_WRITE 중 하나가 필요하다. | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L149~184<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L123~139<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 추가 조회 API와 기본 권한 ANY-of를 확인했다. 인계 정보는 사용자 안내이다. |
| L166 문장 2 | 편집 권한만으로 두 조회 권한이 충족되지는 않는다. | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L149~184<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L123~139<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 추가 조회 API와 기본 권한 ANY-of를 확인했다. 인계 정보는 사용자 안내이다. |
| L166 문장 3 | 단추가 계속 비활성이면 선택 라인·발생 시각·단추 상태를 운영 담당자에게 전달하고 조회 권한도 확인한다. | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L149~184<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L123~139<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 추가 조회 API와 기본 권한 ANY-of를 확인했다. 인계 정보는 사용자 안내이다. |

### 3D 변경을 되돌리고 저장·종료하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L176 문장 1 | 3D 편집 중 「되돌리기」 또는 Ctrl+Z로 직전 편집을 취소한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L465~523 L1183~1196 | 실행 취소·입력 요소 예외·저장 응답 이후 이력 초기화를 확인했다. |
| L176 문장 2 | 입력 칸·선택 목록에 초점이 있을 때는 배치의 Ctrl+Z가 작동하지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L465~523 L1183~1196 | 실행 취소·입력 요소 예외·저장 응답 이후 이력 초기화를 확인했다. |
| L176 문장 3 | 저장 뒤에는 이전 편집으로 되돌릴 수 없다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L465~523 L1183~1196 | 실행 취소·입력 요소 예외·저장 응답 이후 이력 초기화를 확인했다. |
| L178 문장 1 | 오른쪽 위 「저장」을 누르고 「레이아웃이 저장되었습니다.」를 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1028~1041 L1197~1209<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1046~1046 | 저장 성공·비활성 표시와 공정 그룹 메타 보존을 확인했다. |
| L178 문장 2 | 저장 중에는 단추가 비활성이다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1028~1041 L1197~1209<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1046~1046 | 저장 성공·비활성 표시와 공정 그룹 메타 보존을 확인했다. |
| L178 문장 3 | 3D 저장은 기존 공정 그룹 배치를 유지한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1028~1041 L1197~1209<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1046~1046 | 저장 성공·비활성 표시와 공정 그룹 메타 보존을 확인했다. |
| L180 문장 1 | 「편집 종료」는 편집 도구를 닫으며 자동 저장하지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1170~1182 L1197~1209 | 종료는 편집 토글만 바꾸며 저장을 호출하지 않는다. 이동 전 저장은 사용자 안내이다. |
| L180 문장 2 | 저장을 마친 뒤 편집을 종료하고 다른 보기나 화면으로 이동한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1170~1182 L1197~1209 | 종료는 편집 토글만 바꾸며 저장을 호출하지 않는다. 이동 전 저장은 사용자 안내이다. |

### 배치 저장이 거부됐을 때 확인할 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L191 문장 1 | 설비 배치는 실제 설비 연결이 필수이며, 설비가 저장 대상 라인에 속해야 한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1391~1411<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1044~1044 | 설비 연결 필수·라인 소속·구조물 연결 금지와 빈 목록 항목을 확인했다. |
| L191 문장 2 | 「실설비 바인딩」을 「— 설비 —」로 비워 둔 경우 현재 라인의 설비를 다시 고른다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1391~1411<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1044~1044 | 설비 연결 필수·라인 소속·구조물 연결 금지와 빈 목록 항목을 확인했다. |
| L191 문장 3 | 구조물에는 설비를 연결할 수 없다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1391~1411<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1044~1044 | 설비 연결 필수·라인 소속·구조물 연결 금지와 빈 목록 항목을 확인했다. |
| L193 문장 1 | 저장 시 존재하지 않는 객체·설비, 다른 라인의 설비는 서버가 거부한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L76~109<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 존재·라인 검사와 API 인가를 확인했다. 화면 대상·시각·문구의 인계는 사용자 안내이다. |
| L193 문장 2 | 화면에서 직접 고칠 수 없으면 선택 라인·설비 ID·대상 객체·발생 시각·표시된 오류 문구를 운영 담당자에게 전달한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L76~109<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 존재·라인 검사와 API 인가를 확인했다. 화면 대상·시각·문구의 인계는 사용자 안내이다. |
| L193 문장 3 | 권한 거부이면 입력 변경 대신 편집 권한을 확인한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L76~109<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 존재·라인 검사와 API 인가를 확인했다. 화면 대상·시각·문구의 인계는 사용자 안내이다. |

### 구조물 편집의 출발 객체 고르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L203 문장 1 | 편집 권한자가 화면 오른쪽 위 「구조물 편집」을 연다. | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L289~319<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L97~103 L176~219<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1020~1021 | 편집 진입·점선 기성 모양·같은 명칭 대체 조건을 확인했다. |
| L203 문장 2 | 왼쪽 「객체」에서 점선 테두리의 「기성 · STRUCTURE」 항목을 누르면 새 객체의 출발 모양을 불러온다. | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L289~319<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L97~103 L176~219<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1020~1021 | 편집 진입·점선 기성 모양·같은 명칭 대체 조건을 확인했다. |
| L203 문장 3 | 같은 명칭의 저장 객체가 있으면 기성 항목 대신 저장 객체가 표시된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L289~319<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L97~103 L176~219<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1020~1021 | 편집 진입·점선 기성 모양·같은 명칭 대체 조건을 확인했다. |
| L205 문장 1 | 실선 테두리의 저장 객체를 누르면 그 객체를 수정한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L137~147 L225~242 L272~279<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1257~1278 | 기존 객체 로드·신규 초기화·종류 표시와 별도 3D 배치를 확인했다. |
| L205 문장 2 | 빈 모양에서 시작하려면 「신규」를 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L137~147 L225~242 L272~279<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1257~1278 | 기존 객체 로드·신규 초기화·종류 표시와 별도 3D 배치를 확인했다. |
| L205 문장 3 | 「종류」는 STRUCTURE로 표시되며 선택 항목이 없다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L137~147 L225~242 L272~279<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1257~1278 | 기존 객체 로드·신규 초기화·종류 표시와 별도 3D 배치를 확인했다. |
| L205 문장 4 | 이 화면에서 객체 모양을 저장한 뒤 3D 편집에서 맵에 배치한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L137~147 L225~242 L272~279<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1257~1278 | 기존 객체 로드·신규 초기화·종류 표시와 별도 3D 배치를 확인했다. |

### 구조물의 도형 크기·위치·색상 정하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L218 문장 1 | 「이름」을 입력하고 「종류」 오른쪽 그림 단추에 마우스를 올려 「박스 추가」·「실린더 추가」를 구별해 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L263~288<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1027~1031 | 이름 칸과 종류 오른쪽 그림 단추의 한국어 title을 확인했다. |
| L219 문장 1 | 박스 줄의 W·H·D로 가로·높이·깊이를, X·Y·Z로 위치를 정한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L111~125 L311~316<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DObject.tsx @ 9fe69a75dcd3 L11~19<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L368~368 | 박스 치수·위치·격자 1단위와 높이 변경의 바닥 보정을 확인했다. |
| L219 문장 2 | 단위는 3D 격자 한 칸이다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L111~125 L311~316<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DObject.tsx @ 9fe69a75dcd3 L11~19<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L368~368 | 박스 치수·위치·격자 1단위와 높이 변경의 바닥 보정을 확인했다. |
| L219 문장 3 | 박스·실린더의 H를 바꾸면 바닥 위치를 유지하며 높이가 바뀐다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L111~125 L311~316<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DObject.tsx @ 9fe69a75dcd3 L11~19<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L368~368 | 박스 치수·위치·격자 1단위와 높이 변경의 바닥 보정을 확인했다. |
| L220 문장 1 | 「색상」을 고르고 오른쪽 미리보기로 모양을 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L317~328 L341~357 | 색 입력·미리보기 위치·도형 하나 제거를 확인했다. |
| L220 문장 2 | 도형 줄의 휴지통은 그 도형만 제거한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L317~328 L341~357 | 색 입력·미리보기 위치·도형 하나 제거를 확인했다. |
| L222 문장 1 | 「도형」 옆 숫자로 현재 도형 수를 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L156~158 L299~300 | 현재 개수 렌더와 도형 하나 이상 검사를 확인했다. |
| L222 문장 2 | 저장하려면 도형이 하나 이상 필요하다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L156~158 L299~300 | 현재 개수 렌더와 도형 하나 이상 검사를 확인했다. |
| L224 문장 1 | 실린더는 W가 지름, H가 높이이며 D 변경은 모양에 반영되지 않는다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DObject.tsx @ 9fe69a75dcd3 L15~20<br>nori-tc-ui-front/src/features/live-map/lib/live-map-3d.model.ts @ 9fe69a75dcd3 L104~140<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L356~356 | 실린더의 W·H만 사용하는 렌더와 표준 명칭 색 덮어쓰기를 확인했다. |
| L224 문장 2 | 객체 명칭이 「벽」·「파티션」·「바닥」·「기둥」이면 선택한 도형 색 대신 정해진 표시 색을 사용한다. | nori-tc-ui-front/src/features/live-map/components/LiveMap3DObject.tsx @ 9fe69a75dcd3 L15~20<br>nori-tc-ui-front/src/features/live-map/lib/live-map-3d.model.ts @ 9fe69a75dcd3 L104~140<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L356~356 | 실린더의 W·H만 사용하는 렌더와 표준 명칭 색 덮어쓰기를 확인했다. |

### 만든 구조물을 저장하거나 기존 객체 수정하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L235 문장 1 | 새 구조물은 「저장」, 목록에서 불러온 기존 객체는 「수정」을 누른다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L150~173 L289~298<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapObjectService.java @ 985d17c604dd L67~71<br>nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L1149~1157 | 신규·수정 분기·공백 검사와 기본 PostgreSQL 제약을 확인했다. |
| L235 문장 2 | 「이름」은 공백만 입력할 수 없고 앞뒤 공백은 제거된다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L150~173 L289~298<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapObjectService.java @ 985d17c604dd L67~71<br>nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L1149~1157 | 신규·수정 분기·공백 검사와 기본 PostgreSQL 제약을 확인했다. |
| L235 문장 3 | 저장소의 PostgreSQL 기본 스키마는 객체 명칭을 100자 이내·중복 없이 저장하도록 제한한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L150~173 L289~298<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapObjectService.java @ 985d17c604dd L67~71<br>nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L1149~1157 | 신규·수정 분기·공백 검사와 기본 PostgreSQL 제약을 확인했다. |
| L237 문장 1 | 요청이 성공하면 입력 폼과 도형이 비워지고 객체 목록을 다시 조회한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L162~165 L137~147<br>nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L50~69<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1257~1278 | 성공 콜백·초안 초기화·목록 재조회·재선택과 배치 분리를 확인했다. |
| L237 문장 2 | 목록에서 저장한 객체를 다시 골라 내용을 확인한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L162~165 L137~147<br>nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L50~69<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1257~1278 | 성공 콜백·초안 초기화·목록 재조회·재선택과 배치 분리를 확인했다. |
| L237 문장 3 | 구조물 저장은 재사용할 모양을 저장하는 작업이며, 라인에 놓을 위치는 3D의 배치 저장에서 정한다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L162~165 L137~147<br>nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L50~69<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1257~1278 | 성공 콜백·초안 초기화·목록 재조회·재선택과 배치 분리를 확인했다. |

### 구조물 입력·저장이 실패했을 때 처리하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L248 문장 1 | 「객체 이름을 입력하세요.」가 나오면 「이름」을 채운다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L137~158<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1023~1023 L1038~1039 | 두 입력 거절과 옛 형식 차단 및 확인된 신규 경로를 대조했다. |
| L248 문장 2 | 「도형을 하나 이상 추가하세요.」가 나오면 도형 단추로 도형을 만든다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L137~158<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1023~1023 L1038~1039 | 두 입력 거절과 옛 형식 차단 및 확인된 신규 경로를 대조했다. |
| L248 문장 3 | 옛 형식 객체를 고를 때 3D(v2) 편집 불가 안내가 나오면 「신규」로 새 객체를 만든다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L137~158<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1023~1023 L1038~1039 | 두 입력 거절과 옛 형식 차단 및 확인된 신규 경로를 대조했다. |
| L250 문장 1 | 요청 실패는 서버 오류 문구가 있으면 그 문구를 표시한다. | nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 L52~52 L93~119<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40 L49~79<br>nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L166~167 L334~338<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1041~1044 L1438~1441 | 서버/기본 문구 전달·표시 위치를 확인했다. 재시도·인계는 사용자 안내이다. |
| L250 문장 2 | 서버 문구가 없으면 `live-map layout request failed`가 나올 수 있다. | nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 L52~52 L93~119<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40 L49~79<br>nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L166~167 L334~338<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1041~1044 L1438~1441 | 서버/기본 문구 전달·표시 위치를 확인했다. 재시도·인계는 사용자 안내이다. |
| L250 문장 3 | 입력 폼 아래나 3D 오른쪽 아래의 문구를 확인하고 같은 「저장」·「수정」을 다시 누른다. | nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 L52~52 L93~119<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40 L49~79<br>nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L166~167 L334~338<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1041~1044 L1438~1441 | 서버/기본 문구 전달·표시 위치를 확인했다. 재시도·인계는 사용자 안내이다. |
| L250 문장 4 | 계속 실패하면 대상·시각·문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 L52~52 L93~119<br>nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~40 L49~79<br>nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12<br>nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L166~167 L334~338<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1041~1044 L1438~1441 | 서버/기본 문구 전달·표시 위치를 확인했다. 재시도·인계는 사용자 안내이다. |

### 저장된 구조물 객체를 삭제하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L261 문장 1 | 「구조물 편집」의 저장 객체 항목 오른쪽 휴지통을 누르면 확인 대화상자 없이 삭제 요청을 보낸다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L225~253<br>nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L71~79 | 확인 없는 삭제 요청과 성공 이후 목록 갱신을 확인했다. |
| L261 문장 2 | 성공하면 객체 목록을 다시 조회해 항목이 사라진다. | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L225~253<br>nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L71~79 | 확인 없는 삭제 요청과 성공 이후 목록 갱신을 확인했다. |
| L263 문장 1 | 저장소의 PostgreSQL 기본 스키마에서는 객체를 삭제하면 그 객체를 사용하는 맵 배치도 함께 삭제된다. | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L1193~1194<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L971~978 | 기본 스키마 연결 삭제와 3D 선택 배치 삭제를 대조했다. 잔여 인계는 사용자 안내이다. |
| L263 문장 2 | 맵에서 한 배치만 빼려면 3D 편집의 「삭제」를 사용한다. | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L1193~1194<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L971~978 | 기본 스키마 연결 삭제와 3D 선택 배치 삭제를 대조했다. 잔여 인계는 사용자 안내이다. |
| L263 문장 3 | 객체가 목록에 계속 남으면 객체 명칭·발생 시각·화면 결과를 운영 담당자에게 전달한다. | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L1193~1194<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L971~978 | 기본 스키마 연결 삭제와 3D 선택 배치 삭제를 대조했다. 잔여 인계는 사용자 안내이다. |


## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 원문 위치와 설명 | 확인한 코드 | 검색 원문에 쓴 내용 |
|---|---|---|---|
| 구조물 저장 성공 표시 | U L1967~1969: 생성·수정 성공 문구가 나온다고 설명 | LiveMapObjectEditor.tsx L83~89, L162~165: 성공 문구 설정 직후 resetDraft가 status를 null로 초기화 | 폼 초기화·객체 목록 재조회·객체를 다시 골라 내용 확인으로 설명. 성공 문구가 남는다고 쓰지 않음 |
| 요청 실패 기본 문구 | U L1979: 「레이아웃 저장 실패」 | layout.api.ts L52, L67~79, L93~119의 정규화가 Error에 영문 기본 문구를 넣고, resolve-error-message.ts L11~12는 Error.message를 우선 사용 | 서버 문구가 있으면 그대로, 없으면 `live-map layout request failed`가 나올 수 있다고 설명 |
| 기본 구조 사용 조건 | U L1934: 층마다 한 번만 사용 | LiveMap3DScene.tsx L587~595, L867~885, L1280~1290: 현재 층에 STRUCTURE 배치가 있는지 검사 | 직접 배치한 구조물을 포함해 대상 층에 구조물이 하나라도 있으면 비활성이라고 설명. 평생 한 번이라는 뜻으로 쓰지 않음 |
| 자동 배치의 무대상 조건 | U L1983: 이미 모두 배치된 경우라고 설명 | LiveMap3DScene.tsx L923~927, L1325: 현재 라인 설비 수가 0일 때 차단. 기존 배치 여부는 조건이 아님 | 현재 라인 설비와 두 조회 성공 조건을 설명하고, 기존 설비도 다시 배치됨을 명시 |
| 실린더 치수 | U L1954~1955: 도형 W·H·D를 가로·높이·깊이로 일괄 설명 | LiveMap3DObject.tsx L15~18: 박스는 x/y/z 사용, 실린더는 x/2 반지름과 y 높이만 사용 | 박스의 W·H·D와 실린더 W 지름·H 높이·D 미반영을 구별 |
| 구조물 색상 | U L1916, L1956은 색 선택만 설명 | LiveMapObjectEditor.tsx L356, live-map-3d.model.ts L104~140, LiveMap3DObject.tsx L20: 표준 구조물 명칭은 색상 override | 벽·파티션·바닥·기둥 명칭은 정해진 표시 색 사용이라는 작업상 최소 조건 추가 |
| 이름 제한 | U L1951은 자유 입력으로 설명 | LiveMapObjectService.java L67~71 및 PostgreSQL 기본 DDL L1149, L1157 | 공백 금지·trim, PostgreSQL 기본 스키마의 100자·유일성 조건을 구별해 설명 |
| 삭제의 영향 | U L1906~1907은 목록 즉시 삭제만 설명 | LiveMapObjectJdbcStorePort.java L78~85 및 PostgreSQL 기본 DDL L1193~1194 | 객체 삭제와 3D 한 배치 삭제의 차이, 기본 스키마의 연결 배치 삭제 영향 설명 |

위 경로는 절별 사실 대조 표에 적은 저장소와 판본을 따른다. 현장 배포나 스키마 적용 상태를 확인한 기록이 아니다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 사용자·그룹에 실제 부여된 편집·추가 조회 권한 | 저장소 기본 권한 연결만 읽었고 현장 계정 데이터는 조회하지 않음 | TC 운영 관리자 |
| 현장 배포 판본과 객체 명칭 제약·삭제 규칙의 실제 적용 상태 | 현재 소스와 기본 DDL을 대조했으며 서버 접속은 작업에서 금지됨 | TC 배포·운영 담당 |
| 통신·서버 장애별 원인이나 재시도 성공 보장 | 화면/API 오류 전달 경로만으로 실제 장애 원인을 확정할 수 없음 | TC 운영 담당 |

위 사실은 검색 원문에서 추정으로 채우지 않았다. 조회·저장 절차 자체를 작성할 원천은 충분했으므로 사용자 결정 요청은 없다.

## 다른 문서가 소유해 넣지 않은 사실

| 문서 objectKey | 이 문서에서 제외하거나 최소화한 사실 |
|---|---|
| `00.rag/user/live-map-status.md` | 상태 색·상단 집계·실시간 조회·선택 설비 상세·3D 층 보기·개수 상자·「변경」 수 읽는 법. 편집 대상 라인과 보기 선택만 최소 전제로 사용 |
| `00.rag/user/login-and-screen-basics.md` | 로그인·세션·상단바 공통 사용법·공통 표 조작·화면 오류 새로고침 |
| `00.rag/user/permission-code-reference.md` | 전체 권한 코드 뜻과 부여 체계. 실제 편집 및 자동 배치 조회의 필요한 코드만 사용 |
| `00.rag/user/live-map-troubleshooting.md` | Live-Map 전반의 표시·조작 문제 진단. 현재 작업의 입력·저장 실패와 사용자 인계 정보만 설명 |
| `00.rag/user/error-message-and-code-reference.md` | 오류 전체 목록과 내부 코드 해설. 현재 폼 조작에 필요한 실제 메시지와 기본 문구만 설명 |
| `00.rag/user/screen-behavior-limitations.md` | 화면 제한 전반. 성공 문구 초기화·실린더 D·표준 색 덮어쓰기는 잘못 조작하지 않도록 현재 절차의 최소 조건으로만 설명 |
| `00.rag/user/demo-execution.md` | 데모 실행·시나리오 선택·설비 시작과 종료 |
| `00.rag/user/equipment-registration-and-settings.md` | 설비 등록·영구 설정 변경·설비 자체 삭제. 이 문서의 삭제는 공정 그룹·배치·구조물 객체에 한정 |

기존 검색 원문 두 파일을 읽은 범위에서 별도 수정이 필요한 사실 결함은 확인하지 않았다.

## 검사 결과

- 도구: Git Bash 5.2.37(1)-release, Git 2.52.0.windows.1, OpenJDK Temurin 21.0.9+10, 검사기 check.sh에 지정된 commonmark/commonmark-ext-gfm-tables 0.32.0.
- 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/live-map-layout-editing.md`
- 실제 검사 실행: 최초 exit 1, 원천 줄 범위 초과 4건. useLayout.ts와 layout.api.ts의 실제 줄 번호를 다시 읽어 수정했다. 검사기는 변경하지 않았다.
- 마지막 편집 후 재실행: exit 0, 위반 0건. CROSS VIOLATION·MASK 출력 없음. 절 19개·자식 53개·자식 최대 168자·부모 최대 361자.
- 대상 문서의 263줄·SHA-256은 마지막 편집 후 다시 측정했다. 인용 파일 27개의 status·HEAD 텍스트 일치도 재확인했다.
- 최초 Get-Content는 기본 인코딩 때문에 한글이 깨져 UTF-8로 다시 읽었다. 한 번의 Python 본문 출력도 cp949 출력 오류가 나서 `python -X utf8`로 재실행했고 최종 대조 명령은 exit 0이었다. 함수 별칭 `R` 충돌과 존재하지 않는 추정 경로 조회는 즉시 확인해 실제 경로·Read-Lines 함수로 읽었다. 확인하지 못한 경로를 원천으로 사용하지 않았다.
- 제품 코드를 바꾸지 않았고 제품 빌드·시험·UI 실행·네트워크 접근은 하지 않았다. 관련 시험 소스의 기대값 대조와 문서 검사기의 실행을 구별한다.

마지막 검사 출력 10줄:

```text
    L237-237 문단 124  | 요청이 성공하면 입력 폼과 도형이 비워지고 객체 목록을 다시 조회한다. 목록에서 저장한 객체를 다시 골라 내용을 확인한다. 구조물 저장은 재사용할 모양을 저장하는 작업이며, 라인에 놓을 위치는 3D의 배치 저장에서 정한다.
§ L239-250 [22] 구조물 입력·저장이 실패했을 때 처리하기   parent=292 children=2 sources=5
    L248-248 문단 123  | 「객체 이름을 입력하세요.」가 나오면 「이름」을 채운다. 「도형을 하나 이상 추가하세요.」가 나오면 도형 단추로 도형을 만든다. 옛 형식 객체를 고를 때 3D(v2) 편집 불가 안내가 나오면 「신규」로 새 객체를 만든다.
    L250-250 문단 168  | 요청 실패는 서버 오류 문구가 있으면 그 문구를 표시한다. 서버 문구가 없으면 live-map layout request failed가 나올 수 있다. 입력 폼 아래나 3D 오른쪽 아래의 문구를 확인하고 같은 「저장」·「수정」을 다시 누른다. 계속 실패하면 대상·시각·문구를 운영 담당자에게 전달한다.
§ L252-263 [16] 저장된 구조물 객체를 삭제하기   parent=226 children=2 sources=5
    L261-261 문단 83  | 「구조물 편집」의 저장 객체 항목 오른쪽 휴지통을 누르면 확인 대화상자 없이 삭제 요청을 보낸다. 성공하면 객체 목록을 다시 조회해 항목이 사라진다.
    L263-263 문단 142  | 저장소의 PostgreSQL 기본 스키마에서는 객체를 삭제하면 그 객체를 사용하는 맵 배치도 함께 삭제된다. 맵에서 한 배치만 빼려면 3D 편집의 「삭제」를 사용한다. 객체가 목록에 계속 남으면 객체 명칭·발생 시각·화면 결과를 운영 담당자에게 전달한다.
SUMMARY sections=19 children=53 maxChild=168 maxParent=361
CORPUS docs=3/53 sections=46 part={user=3} type={사용 절차=3}
TOTAL files=3 targets=1 violations=0
```

사람 검사 판정:

| 절 제목 | 질문 식별 | 원천 일치 | 독립 답 | 중복·경계 판정 |
|---|---|---|---|---|
| 배치와 구조물을 편집할 계정 준비하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 편집 계정·API 조건만 소유 |
| 공정 그룹을 옮기거나 크기 바꾸기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 보드 이동·크기 작업 경계 |
| 공정 그룹을 추가하고 표시 속성 정하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 그룹 표시 속성 경계 |
| 설비를 공정 그룹에 배정하거나 그룹 지우기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 공정 배정·그룹 삭제 경계 |
| 공정 그룹 사이 화살표를 추가·수정·삭제하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 그룹 사이 화살표 경계 |
| 공정 보드 저장 결과 확인하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 공정 보드 저장 결과 소유 |
| 3D에서 설비·구조물을 선택하고 이동하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 3D 선택·이동 경계 |
| 3D 선택 배치의 회전·층·템플릿·설비 연결 바꾸기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 선택 막대의 적용 범위 소유 |
| 3D에 구조물이나 미배치 설비 추가하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 현재 층에 새 배치 추가 경계 |
| 빈 층에 기본 구조 채우기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 현재 구조물 존재 조건 소유 |
| gateway_app 기준으로 설비를 다시 배치하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 자동 배치 실행의 영향 소유 |
| 자동 배치 단추가 비활성일 때 확인하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 자동 배치 조회·권한 조건 소유 |
| 3D 변경을 되돌리고 저장·종료하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 3D 저장·실행 취소 결과 소유 |
| 배치 저장이 거부됐을 때 확인할 조건 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 서버 거절 조건·사용자 인계 소유 |
| 구조물 편집의 출발 객체 고르기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 재사용 객체의 출발점 소유 |
| 구조물의 도형 크기·위치·색상 정하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 도형 치수·색 입력에 필요한 제한 |
| 만든 구조물을 저장하거나 기존 객체 수정하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 객체 저장 결과·기본 제약 소유 |
| 구조물 입력·저장이 실패했을 때 처리하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 현재 입력·저장 실패만 다룸 |
| 저장된 구조물 객체를 삭제하기 | 적합 | 대조 완료 | 조건·조작·결과 확인 가능 | 객체 삭제와 한 배치 삭제 구별 |

- 19절 전부를 읽었다. 질문·원천 일치·독립 답·중복 여부 네 축에서 미해소 지적 0건이다. 절 사이 「저장」 안내는 각 작업을 끝내는 데 필요한 최소 동작이며, 저장 결과의 상세 설명은 공정 보드 저장·3D 저장·객체 저장 절이 각각 소유한다.
- 제목 정확 중복 0건, 대상 명칭을 지운 반복 틀 제목 3회 이상 해당 없음. 정보 없는 빈 틀 문장·이름 토막 뜻풀이·잘린 문장·오답 사례를 발견하지 않았다.
- 기존 두 검색 원문과 문장·의미를 대조했다. 조회 사실을 새 답으로 복제하지 않았고 기계 검사에서도 같은 자식 본문 중복이 없었다.
- mask-v1 적용에서 MASK 출력이 없었다. 이름·자격 값의 콜론/등호 표기를 피했고, user 본문에 admin objectKey·내부 접근 조치를 넣지 않았다.
- 관리 전용 작업을 안내하지 않고 화면 대상·발생 시각·실행 조작·관찰 문구를 인계하도록 썼다.
- 검사 로그의 필요한 실패 내용과 마지막 출력을 이 기록에 보존했다. 작업에서 만든 일회성 로그 파일 두 개만 지정 work 폴더에서 비강제 PowerShell 정상 삭제했다. 기존 폴더나 다른 세션 파일은 삭제하지 않았다.

### 내부 검사 오류의 보존과 정리 전 확인

최초 검사 오류 원문은 다음과 같다. 이 네 위치를 실제 파일 줄과 재대조해 수정했고 마지막 검사에서 0건을 확인했다.

```text
VIOLATION L221 원천 줄 범위 L58~80 (파일 79줄) 'nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 ; 객체 목록 갱신 L58~80'
VIOLATION L221 원천 줄 범위 L104~138 (파일 136줄) 'nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; 생성·수정 L104~138'
VIOLATION L234 원천 줄 범위 L104~138 (파일 136줄) 'nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; 요청 실패 기본 문구 L60~60 L104~138'
VIOLATION L247 원천 줄 범위 L83~92 (파일 79줄) 'nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 ; 삭제 후 목록 갱신 L83~92'
```

- 작성 기록 표를 만들 때 Windows PowerShell의 외부 프로세스 stdin 인코딩으로 일부 표 머리글·확인 열이 물음표로 바뀐 결함을 발견했다. `$OutputEncoding`을 UTF-8로 지정한 뒤 그 부분만 복구했다. 최종 132개 문장 행을 검색 원문의 실제 문장과 다시 대조해 일치함을 확인했다. 검색 원문 파일은 이 오류로 바뀌지 않았다.
- 기록 정합성 자체검사: 19개 절별 표, 본문 132문장과 표의 정확 일치, 검색 원문 SHA, 두 파일의 UTF-8/BOM 없음/LF/NFC를 확인했다. exit 0.
- 실제 독립 AI 호출 0회. 검사기 실행 2회. 두 검사 명령의 도구 wall time은 각각 3.885503초와 3.7505455초였다. 세션 전체 사용량·provider 비용은 별도 측정되지 않았다.
- 정리 대상은 이 세션이 stdout을 저장한 `work/check-first.txt`·`work/check-final.txt` 두 파일뿐이다. 파일은 Archive 속성이고 ReadOnly·Hidden·System·ReparsePoint가 없다. 지정 work 아래의 절대경로와 Temp까지 부모 reparse 부재를 확인했다. 두 파일 모두 FileShare.None 배타적 열기에 성공했다.
- 광범위 명령행 참조 확인을 위한 `Get-CimInstance Win32_Process -ErrorAction Stop`은 로컬 CIM 접근 거부(HRESULT 0x80041003)로 실패했다. 이 제한은 변경하지 않았다. 확인된 검사 프로세스는 동기 호출에서 이미 종료됐고, 로그는 종료 후 PowerShell이 만든 출력 파일로 다른 프로세스의 입력에 사용하지 않았다. `Get-Process java,bash`의 좁은 조회와 두 로그의 배타적 열기를 추가 확인했다. 다른 bash·IDE Java 프로세스는 종료하거나 변경하지 않았다. 모든 프로세스의 명령행을 확인했다는 주장은 하지 않는다.
- 정리 실행: 검증한 두 절대경로에만 `Remove-Item -LiteralPath <각 파일> -ErrorAction Stop`을 실행했다. Force·Recurse 없이 exit 0으로 끝났다. 각각 `Test-Path -LiteralPath`가 False였고, 기존 work 폴더의 잔여 항목 수도 0이었다. 검사 결과·원문 오류는 이 작성 기록에 보존했다.

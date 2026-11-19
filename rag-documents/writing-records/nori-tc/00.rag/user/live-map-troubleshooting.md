# Live-Map 트러블슈팅 작성 기록

## 대상

- objectKey: `00.rag/user/live-map-troubleshooting.md`
- 구획·권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/live-map-troubleshooting.md`
- 검색 원문 SHA-256: `db7e93e94fe7eb8cf1b895d21a791caee0122584e983792571130f5b77df5172`
- 검색 원문 줄 수: 151
- H2 절 수: 10
- 확인일: 2026-11-19, 한국 시간
- 범위: 설비 누락·선택 불가, 상태와 조회 갱신의 구별, 배치 조회·편집 권한, 3D 표시 실패, 저장 비활성·거부, 객체 목록·형식·저장 실패의 확인 경로. 업로드·제품 변경·git add/commit/push는 수행하지 않았다.
- 자체 대조 기록이며 메인 세션의 독립 검증을 대신하지 않는다.

## 원천 판본

| 저장소 | 확인 HEAD | 작업 트리 상태 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-front -C ../nori-tc-ui-front status --short -- src/features/live-map src/shared/components/ErrorBoundary.tsx src/shared/lib src/i18n/locales/ko.json tests/unit` 결과 변경 행 없음. 추가로 `src/shared/api/line.api.ts`도 변경 행 없음. |
| nori-tc-ui-backend | 985d17c604dd | 같은 형태의 읽기 명령으로 `status --short -- ui` 결과 변경 행 없음. 관련 controller·request DTO·service·DB adapter·security·시험 파일 포함. |
| nori-tc-exec | f1d5e0a8e727 | 같은 형태의 읽기 명령으로 `status --short -- docs/completion-report/07-user-manual.md docs/scripts/manual-permission-catalog.json docs/db_table` 결과 변경 행 없음. |

각 HEAD는 `git ... rev-parse --short=12 HEAD`로 확인했다. Git은 개인 전역 ignore 파일에 접근하지 못한다는 경고를 출력했으나 상태·HEAD 조회는 성공했다. 형제 저장소의 첫 일반 Git 조회는 소유자 불일치로 거부되어, 이후 정확한 저장소에 한정한 명령별 `-c safe.directory=...`로 읽었다. 전역 Git 설정을 바꾸지 않았다.

현재 저장소는 main, HEAD `70cf6cd`, 기존 `rag-documents/` 미추적 상태이며 worktree 목록은 현재 저장소 하나였다. 이 세션은 두 대상 파일이 없음을 확인한 뒤 새로 작성했다. 다른 문서의 내용을 변경하지 않았다.

먼저 `docs/rag-document-writing-rules.md` 전체와 `docs/tc-rag-document-list.md` 전체를 읽었다. 최초 PowerShell 기본 인코딩 출력이 깨져 UTF-8 지정으로 다시 읽었다. U §7 L1774~2041을 읽고 주의·오류 항목을 코드와 대조했다. 겹치는 기존 문서는 `live-map-status.md`, `live-map-layout-editing.md`, `message-results-and-communication-logs.md`, `login-permission-and-screen-troubleshooting.md`를 읽었고, `demo-execution.md`의 데모 소유 경계·실패 경로도 확인했다. `demo-troubleshooting.md`는 아직 로컬 파일이 없으며 확정 목록의 후속 문서 경로로 안내했다.

## 절별 사실 대조

표의 조치·인계 문장은 확인한 화면 조작과 오류 조건을 토대로 작성한 점검 순서다. 현장에서 수행·재현했다는 뜻이 아니다. 정상 절차 문서의 경로는 확정 목록과 실제 기존 파일을 대조했다. 메타의 판본·확인일은 위 Git 조회와 `Get-Date` 결과를 기준으로 했다.

### 공정 흐름에서 설비가 없거나 흐려서 선택되지 않을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 19 | 상단바의 라인과 대상 설비의 라인 비교 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L105~112 L136~141 | 선택 라인 코드로 공정 보드 설비를 제한한다. 라인 비교를 첫 확인으로 배치했다. |
| 20 | 검색을 비우고 전체 상태로 바꿔 선택 | nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L20~41; nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L568~592; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L361~362 | 검색어가 비고 상태 ALL이면 두 조건을 통과한다. 실제 한국어 입력 표시를 확인했다. |
| 20 | 조건 밖 설비는 흐려지고 선택 불가 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L152~187 L335~345; nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L840~846; nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 L34~48 | 필터 결과 ID 집합이 보드로 전달되며 dimmed 단추는 disabled와 opacity-30을 사용한다. |
| 21 | 조건 해제 뒤 사용 설정 확인 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaLiveMapPrefetchPort.java @ 985d17c604dd L85~98 | 서버 prefetch가 enabled 설비만 남긴다. 설정 변경 조작은 이 문서에서 복제하지 않았다. |
| 21 | Live-Map은 사용 설정이 켜진 설비만 포함 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaLiveMapPrefetchPort.java @ 985d17c604dd L88~92 | `filter(UiEqp::enabled)`를 확인했다. 연결 상태와 동일시하지 않았다. |
| 21 | 등록 정보 확인 경로 | docs/tc-rag-document-list.md의 설비 등록·설정 변경·삭제 행; rag-documents/nori-tc/00.rag/user/equipment-registration-and-settings.md | 목록에서 정한 user objectKey와 실제 파일 존재를 확인했다. |
| 23 | 누락 증상의 인계 값 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L88~112 L136~187 | 설비·라인·검색·상태는 누락을 가르는 입력이다. 확인 시각과 함께 넘기는 점검 지침이다. |

### 공정 흐름에는 있지만 3D에만 설비가 없을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 33 | 3D 층 선택을 전체로 바꾸기 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1215~1248; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1004~1006 | 전체 단추는 floorView를 all로 바꾼다. |
| 33 | 전체에서 나타나면 층 선택으로 누락된 경우 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L652~656 L691~699 | 층 조건이 배치와 설비 이름표를 제한한다. 다른 입력이 같은 상태에서의 확인 경로다. |
| 35 | 같은 라인의 공정 보드와 비교 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L136~141 L321~345 | 동일 라인의 두 보기 비교를 지침으로 정했다. |
| 35 | 상태 조회와 저장 배치는 별개 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L426~431 L465~469 L597~614; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L41~55 | 실설비와 저장 layout은 다른 조회다. 3D working은 placements로 만들며 미저장 라인은 빈 배치를 반환한다. |
| 37 | 편집 권한자는 미배치 후보 확인 후 정상 절차 이용 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L547~563 L1293~1317; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1051~1055 | 현재 라인 설비에서 이미 배치한 키를 빼 후보를 만든다. 편집의 추가·저장 설명은 기존 문서가 소유한다. |
| 37 | 저장한 설비 누락의 인계 항목 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L465~469 L652~656 L1015~1044 | 라인·층·저장 여부를 서로 구별할 수 있도록 인계한다. 실제 소실 원인은 확정하지 않는다. |

### 상태가 그대로인 것과 조회 갱신이 끊긴 것을 가르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 48 | 실시간 숫자 변화와 상단 오류를 함께 보기 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L189~217 L240~245; nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L507~518; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L391~391 | 숫자는 dataUpdatedAt을 반영한 시각과 now 차이다. 오류는 별도 isError로 표시한다. |
| 48 | 숫자가 커지는 현상은 데이터 갱신 지연으로 기록 | nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L103~109; nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L203~217 | elapsed 값의 관측 해석이며 네트워크·설비 장애 원인 판정으로 확대하지 않았다. |
| 50 | 숫자는 갱신되나 상태가 같은 경우 설비·상태 기록 | nori-tc-ui-front/src/features/live-map/hooks/useLiveMap.ts @ 9fe69a75dcd3 L4~10; nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L128~130 L167~173 L213~217 | 주기 조회 및 데이터 갱신 시각과 응답 설비 상태는 다른 값이다. |
| 50 | 조회 갱신이 설비 상태 변경을 보장하지 않음 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd L42~49 L62~87 L178~195 | 조회가 상태를 변경하지 않고 runtimeState를 화면 상태로 변환해 반환한다. 상태값 뜻 전체는 복제하지 않았다. |
| 50 | 최근 알림 확인과 로그 빈 표시의 소유 경로 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~294; rag-documents/nori-tc/00.rag/user/message-results-and-communication-logs.md의 통신 로그 목록이 비어 있을 때 확인하기 절 | logs 기본값·isFetching만 사용하고 별도 오류 렌더가 없음을 직접 확인했다. 앞선 문서의 빈 표시 설명은 복제하지 않았다. |
| 52 | 조회 지연·실패 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L189~217 L240~245 | 관측할 수 있는 라인·설비·시각·숫자 변화·오류를 인계하는 지침이다. |
| 52 | 데모 실행 정체는 다른 문서 소유 | docs/tc-rag-document-list.md의 데모 트러블슈팅 행; rag-documents/nori-tc/00.rag/user/demo-execution.md의 종료 실패나 실행 정체를 담당자에게 알리기 절 | 확정 소유 경계로 연결했다. 아직 없는 후속 문서의 본문을 확인했다고 쓰지 않았다. |

### 배치가 비거나 보기 전환·편집 단추가 없을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 63 | 상태 조회와 배치 조회·편집 권한 구별 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L81~86 L257~306; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L213~239 | 상태 API와 layout API에 다른 권한 매핑이 있고 전환·편집의 노출 조건도 다르다. |
| 65 | 기본 상태 조회·라인 조회 권한 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L150~158 L231~239; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L775~775; nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L18~23 | LIVE_MAP_READ·LINE_READ의 기본 매핑을 catalog와 SQL에서 함께 확인했다. 현장 부여 상태로 표현하지 않았다. |
| 65 | 배치 조회는 READ/WRITE 중 하나, 편집은 WRITE | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L213~230; nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_live_map_layout_read_permission.sql @ f1d5e0a8e727 L18~25; nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L18~23; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | GET과 전 메서드 매핑, anyMatch 인가를 확인했다. |
| 67 | 라인을 찾지 못하면 배치 조회 비활성 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L105~112; nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L16~23; nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 L22~30; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd L38~43 | 선택 코드를 조회 라인 목록에서 찾고 null이면 enabled=false다. |
| 67 | 응답이 없을 때 기본 공정 배치 사용 | nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L18~33 | layoutQuery.data가 없거나 파싱하지 못하면 기본 flow를 사용한다. 빈 화면이 자료 삭제의 증거라고 쓰지 않았다. |
| 67 | 권한·조회 결과 인계 요청 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | 인가는 미등록·초기화·동기화 실패에도 차단된다. 단추 부재나 거부 하나로 현장 원인을 확정하지 않고 작업·라인을 넘긴다. |

### 3D 영역에만 화면 표시 실패가 나올 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 76 | 3D 표시 안내와 상단 상태 조회 오류 구별 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L240~245 L321~333; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L477~479 | 서로 다른 위치·조건으로 렌더한다. |
| 76 | 3D 별도 오류 경계 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L321~333 | live-map-3d 경계는 해당 Suspense/scene을 감싼다. 전체 UI나 조회 성공까지 보장하지 않았다. |
| 78 | 영역의 새로고침 후 두 보기 비교 | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 L41~55; nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L257~288 | reload 단추와 경계 바깥 전환 단추를 확인해 비교 순서를 제시했다. |
| 78 | 안내만으로 브라우저/배포 원인 확정 불가 | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 L14~18 L27~55 | 렌더 오류 및 lazy 실패가 같은 안내로 표시된다. 내부 원인은 사용자 영역에 표시하지 않는다. |
| 80 | 재발 인계·공통 문서 연결 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L257~333; rag-documents/nori-tc/00.rag/user/login-permission-and-screen-troubleshooting.md의 화면 표시 실패와 API 실패 문구를 구별하기 절 | Live-Map에 특화한 보기 간 비교 결과를 넘기고 공통 오류 경계 설명은 복제하지 않았다. |

### 저장을 누를 수 없는 것과 요청 실패를 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 90 | 공정 보드 비활성일 때 점·진행 표시 확인 | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L547~565 | dirty 점·isSaving 회전 아이콘을 확인했다. |
| 90 | 변경 없음 또는 저장 중이면 단추 비활성 | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L551~561 | disabled={!isDirty 또는 isSaving} 조건이다. |
| 90 | 3D는 배치 저장/객체 생성 중 비활성 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1197~1209 | saveLayout.isPending 또는 createObject.isPending 조건을 확인했다. |
| 92 | 실패 문구의 위치 구별 | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L563~565; nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1438~1441 | 각각 저장 단추 옆과 absolute bottom-4 right-4 상태 영역이다. |
| 92 | 정상 저장·성공 확인의 소유 문서 연결 | rag-documents/nori-tc/00.rag/user/live-map-layout-editing.md의 공정 보드 저장 결과 확인하기·3D 변경을 되돌리고 저장·종료하기 절 | 성공 문구·점 제거 설명을 이 문서에서 반복하지 않았다. |
| 94 | 라인 전환 전 오류 기록 | nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L23~28 | lineKey 변경 시 draft를 null로 만든다. |
| 94 | 반복 실패 인계 항목 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L410~416; nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1015~1044 | 실제 오류 위치와 원래 조작·대상을 연결하도록 안내했다. 재시도 성공이나 안전을 보장하지 않았다. |

### 공정 그룹만 바꿨는데 설비·객체 오류로 저장이 막힐 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 104 | 공정 보드 저장은 기존 3D 배치 동반·각 배치 검증 | nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L44~58; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd L57~66; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L63~73 | 기존 placements를 함께 보내며 서버가 모두 validatePlacement한다. |
| 104 | 그룹만 수정했어도 객체/설비 부재·다른 라인으로 거부 가능 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L85~109 | 화면 수정 위치가 아닌 전체 요청 배치 검증 경로에서 예외가 발생한다. 특정 현장에서 발생했다고 쓰지 않았다. |
| 106 | 그룹 재편집 대신 오류 대상·라인 대조 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L410~416; nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L563~565; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L85~109 | 표시되는 message의 objectKey·eqpKey·lineKey가 직접 진단 대상이다. |
| 108 | 화면에서 못 찾는 배치를 인계 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L85~109 | 사용자에게 내부 자료 수정 대신 원래 조작과 메시지 식별자를 넘기게 했다. |
| 108 | 기존 3D 배치 검증도 요청 | nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 L44~58 | 공정 보드 저장에 동반되는 배치를 조사 범위로 요청한다. |

### 3D 저장에서 설비 연결 오류와 형식 오류를 가르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 119 | 연결·라인 오류에서 대상 설비의 실설비 바인딩 확인 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1391~1411; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L89~109 | 설비에만 바인딩 목록을 표시하며 서버는 연결 필수와 동일 라인을 검사한다. |
| 119 | 비어 있으면 현재 라인 설비 선택 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L547~550 L964~968 L1391~1411; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1015~1015 L1044~1044 | 빈 선택은 eqpKey=null이고 후보는 선택 라인의 설비다. |
| 121 | 객체/설비 부재·구조물 연결·스키마 버전은 별도 검사 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L76~109; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapSchemaSupport.java @ 985d17c604dd L23~30 | 양수 키·대상 존재·종류별 연결·라인·JSON 최상위 정수 버전 검사를 모두 읽었다. 일부만 필수 조건이라고 쓰지 않았다. |
| 121 | 형식 입력 칸이 없어 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1015~1044 L1251~1415 | 화면은 구조물/설비 선택·변환 조작을 제공하며 schema version 직접 편집 칸은 없다. |
| 123 | 권한 거부는 관리자 확인 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L222~230; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | 입력 검증과 인가 차단 경로를 분리했다. 403을 권한 미보유 하나로 확정하지 않는다. |
| 123 | 저장 결과 확인 절차 연결 | rag-documents/nori-tc/00.rag/user/live-map-layout-editing.md의 3D 변경을 되돌리고 저장·종료하기 절 | 기존 정상 절차로 연결했다. |

### 구조물이 목록에 없거나 불러오기가 중단될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 132 | 저장 객체가 비었을 때 빈 안내 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L176~180 L222~241; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1022~1024 | objects는 조회 결과에서 STRUCTURE만 남기며 data 부재도 빈 배열로 처리한다. |
| 132 | 조회 오류 전용 안내 없음·이전 목록 소실시 조회 확인 요청 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L62~67 L176~360; nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L29~35 | 렌더 전체에서 objectsQuery.error/isError를 쓰지 않는다. status는 별도 조작으로 설정하므로 조회 실패와 빈 목록을 단정하지 않는다. |
| 134 | 기성 항목만 빠지면 같은 명칭 저장 객체 확인 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L176~180 L202~241 | 저장 STRUCTURE 명칭 집합으로 builtin 목록에서 중복을 제외한다. |
| 134 | 저장 객체가 기성 항목 대신 표시 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L176~180 L202~241 | 같은 조건의 결과를 짧게 명시했다. |
| 136 | 3D(v2) 안내면 불러오기 중단 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L137~147; nori-tc-ui-front/src/features/live-map/lib/live-map-3d.model.ts @ 9fe69a75dcd3의 isLiveMap3DShape; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1023~1023 | v=2와 prims 배열이 아니면 status 설정 후 return해 기존 폼을 교체하지 않는다. |
| 136 | 신규 경로 또는 기존 객체 보존 필요시 인계 | rag-documents/nori-tc/00.rag/user/live-map-layout-editing.md의 구조물 입력·저장이 실패했을 때 처리하기 절; nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L137~147 L188~200 | 정상 신규 작성 설명을 복제하거나 자동 변환을 약속하지 않고 객체와 문구를 넘긴다. |

### 구조물 저장이 입력 안내에서 멈추거나 서버에서 거부될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 147 | 이름·도형 안내는 요청 전 검사 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L150~173; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1038~1039 | 빈 trim 이름·도형 0이면 mutate 전 return한다. |
| 147 | 이름·도형 수 확인 후 정상 입력 절차 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L281~300; rag-documents/nori-tc/00.rag/user/live-map-layout-editing.md의 만든 구조물을 저장하거나 기존 객체 수정하기 절 | 실제 이름 필드와 도형 수 표시를 대상으로 한다. |
| 149 | 서버 거부는 폼 아래 문구 분류 후 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L166~173 L334~338; nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 L93~119; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapObjectService.java @ 985d17c604dd L32~71; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapSchemaSupport.java @ 985d17c604dd L23~30 | 화면 검사 이외에 서버의 종류·스키마·기존 객체 검사가 있다. 다른 원인도 가능하므로 이 세 가지를 원인의 전부로 한정하지 않았다. |
| 149 | 권한 거부는 편집 권한 확인 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L222~230; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | 객체 생성/수정도 layout prefix의 변경 요청이다. |
| 151 | 폼 초기화 뒤 목록에서 저장 대상 확인 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L150~173 L225~241; nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 L50~69 | 성공 callback이 초안을 비우고 mutation 성공은 객체 조회를 무효화한다. 목록 재확인으로 결과를 판별한다. |
| 151 | 저장 성공 뒤 폼과 상태 문구 함께 비움 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L83~89 L162~165 | setStatus(doneKey) 직후 resetDraft()가 setStatus(null)을 수행한다. 성공 문구가 유지된다고 쓰지 않았다. |
| 151 | 목록에도 없을 때 인계 | nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 L169~173 L225~241 | 생성/수정 조작 종류·명칭·시각·화면 결과를 넘기는 점검 지침이다. 저장 실패나 유실을 단정하지 않는다. |

관련 시험은 기대값을 읽었으며 실행하지 않았다. `nori-tc-ui-front/tests/unit/live-map-line-scope.test.ts`의 선택 라인·검색 AND 및 미일치 빈 결과, `tests/unit/error-boundary.test.ts`의 루트·라우트·3D 경계와 reload, `nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/LiveMapLayoutServiceTest.java` L49~138의 연결 필수·구조물 연결 금지·없는 객체/설비·다른 라인·스키마 거부 및 미저장 빈 배치 기대값을 대조했다. `JpaLiveMapPrefetchPortTest.java`의 enabled=false 제외 기대값과 `LiveMapObjectServiceTest.java`의 빈 이름·미지원 종류·스키마·없는 객체 거부도 읽었다. 읽기 확인을 시험 PASS로 기록하지 않는다.

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼 | 확인한 코드 | 검색 원문 처리 |
|---|---|---|---|
| 빈 통신 로그와 연결 진단 | U §7.1.4 L1837~1838은 빈 문구를 기록 부재로 보고 Enabled에서 연결 상태 확인·시작을 안내하며 조회 실패 문구도 제시한다. | LiveMapCommLogPanel.tsx L211~294는 logs 기본값·isFetching만 사용해 오류를 따로 렌더하지 않는다. JpaLiveMapPrefetchPort.java L88~92의 enabled는 조회 대상 필터다. | 상태 갱신과 통신 근거를 구별하고 로그 빈 표시는 기존 `message-results-and-communication-logs.md`의 판별·인계 절로 연결했다. 로그 부재만으로 설비 시작을 권하지 않았다. |
| 배치 저장 실패 문구 | U §7.3.4 L1979는 「레이아웃 저장 실패」를 제시한다. | layout.api.ts L52~52 L67~79의 기본값은 `live-map layout request failed`이며 api-error.ts L19~40 L67~79는 서버 문구를 우선한다. LiveMap3DScene.tsx L1042~1044도 Error.message를 우선한다. | 문구가 고정이라고 쓰지 않고 표시 위치·연결/대상/형식/권한 분류로 진단한다. 문구 반복 설명은 기존 정상 절차 문서에 맡겼다. |
| 구조물 생성·수정 성공 표시 | U §7.3.3 L1967은 성공 문구가 나온다고 설명한다. | LiveMapObjectEditor.tsx L162~165는 성공 문구 설정 직후 resetDraft를 호출하고 L83~89는 status도 null로 만든다. | L151에 폼과 상태가 함께 비워지므로 목록 재확인을 안내했다. 현장 UI 재현 결과로 확대하지 않았다. |
| 자동 배치 대상 없음의 설명 | U §7.3.4 L1983은 이미 모두 놓였기 때문이라고 설명한다. | LiveMap3DScene.tsx L920~948은 현재 라인 전체 설비를 대상으로 하며 없을 때만 해당 분기다. L1325에서 라인 설비 없음 또는 추가 조회 미성공이면 단추 자체가 비활성이다. | 자동 배치 정상 동작·비활성 조건은 기존 `live-map-layout-editing.md`가 소유하므로 이 문서에 같은 실패 설명을 복제하지 않았다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 배포 판본과 이 증상의 실제 재현 여부 | 사용자 지정 부재이며 로컬 소스만 확인했다. 현장 접속·브라우저 실행을 하지 않았다. | 현장 운영 담당자·TC 제품 담당자 |
| 특정 현장에서 상태가 멈춘 원인·표시 실패 원인·저장 오류의 실제 발생 원인 및 복구 보장 | 실제 요청 결과·시각별 상태·재현 증거가 없어 코드 분기만으로 하나의 장애 원인을 확정할 수 없다. | TC 운영·개발 담당자 |
| 현장 계정의 권한과 실제 라인·설비 사용 설정·배치·통신 로그 | 저장소 기본 catalog/SQL만 확인했고 사용자 DB나 운영 자료는 읽지 않았다. | 운영 관리자·설비 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 생략한 답 사실·이 문서에서의 경계 |
|---|---|
| `00.rag/user/live-map-status.md` | 정상 진입·검색·상태 점·포트·로그 열기·줌·층별 집계 전체. 진단에 필요한 라인·필터·층·조회 입력만 짧게 사용한다. |
| `00.rag/user/live-map-layout-editing.md` | 그룹/화살표 편집·기본 구조 채우기·자동 배치·추가 목록·회전·키보드·저장 성공 결과·구조물 작성·삭제 절차. 선택 누락 안내, 자동 배치 비활성 등 이미 설명한 조작 조건의 재수록을 피했다. |
| `00.rag/user/message-results-and-communication-logs.md` | 로그 기간·출처·주기·개별 내용과 빈 표시의 한계·권한·인계. 이 문서는 상태와 조회 갱신을 먼저 구별한 뒤 해당 진단 경로로 보낸다. |
| `00.rag/user/login-permission-and-screen-troubleshooting.md` | 로그인·세션·일반 403 원인·공통 오류 경계. Live-Map 특유의 보기/상태 조회/배치 조회 구별에 한정한다. |
| `00.rag/user/demo-execution.md`, `00.rag/user/demo-troubleshooting.md` | U §7.4의 데모 시작·목록·업로드·실행 정체·종료. 이번 문서는 데모 오류를 Live-Map 화면 표시 장애로 해석하지 않고 정체 진단 경로만 연결한다. |
| `00.rag/user/state-reference.md`, `00.rag/user/permission-code-reference.md`, `00.rag/user/error-message-and-code-reference.md` | 전체 상태값 뜻·권한 코드 뜻·오류 코드 사전. 이번 문서는 증상을 가르는 최소 권한과 조건만 사용한다. |
| `00.rag/user/equipment-registration-and-settings.md` | 설비 사용 설정 변경·등록 절차. 누락 진단에서 조회 대상 제한을 설명하고 변경 절차를 연결한다. |

다른 기존 문서에 대해 수정이 필요한 신규 결함은 확인하지 않았다. 위 매뉴얼 차이는 이 기록에 남겼으며 원본 매뉴얼과 기존 RAG 문서는 수정하지 않았다.

## 검사 결과

명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/live-map-troubleshooting.md
```

실제 실행은 PowerShell에서 `C:/Program Files/Git/bin/bash.exe`를 명시했다. 첫 검사 exit 0, 마지막 원문 편집 뒤 재검사 exit 0이다. 검사기를 수정하거나 우회하지 않았다. 검사 출력은 메모리로 받아 아래에 마지막 10줄을 보존했다.

```text
    L132-132 문단 133  | 「구조물 편집」의 「저장된 사용자 객체 없음」은 저장 객체 목록이 비었을 때 나오는 표시다. 객체 조회 오류 전용 안내가 따로 없으므로 이전에 있던 객체가 모두 사라졌다면 새 객체를 만들기 전에 운영 담당자에게 목록 조회 확인을 요청한다.
    L134-134 문단 65  | 점선 기성 항목만 사라졌다면 같은 명칭의 저장 객체가 있는지 살핀다. 저장 객체가 같은 명칭의 기성 항목을 대신한다.
    L136-136 문단 152  | 객체를 눌렀을 때 3D(v2) 편집 불가 안내가 나오면 불러오기가 중단된 경우다. 새 구조물 작성 경로는 00.rag/user/live-map-layout-editing.md를 따르고, 기존 객체 보존이 필요하면 객체 명칭·안내 문구·발생 시각을 운영 담당자에게 전달한다.
§ L138-151 [31] 구조물 저장이 입력 안내에서 멈추거나 서버에서 거부될 때   parent=347 children=3 sources=5
    L147-147 문단 114  | 이름이나 도형을 요구하는 입력 안내이면 요청 전 검사에서 멈춘 경우다. 「이름」과 「도형」 수를 먼저 확인하고 00.rag/user/live-map-layout-editing.md의 입력 조건대로 고친다.
    L149-149 문단 108  | 입력을 갖췄는데 실패 문구가 나오면 폼 아래 문구가 권한 거부인지, 대상 객체 부재인지, 형식 오류인지 구별해 운영 담당자에게 전달한다. 권한 거부는 운영 관리자에게 편집 권한 확인을 요청한다.
    L151-151 문단 123  | 저장 뒤 폼이 비워졌다면 「객체」 목록에서 저장 대상을 다시 골라 확인한다. 저장 성공 뒤에는 폼과 상태 문구가 함께 비워진다. 목록에도 없으면 객체 명칭·저장 또는 수정 조작·시각·화면 결과를 운영 담당자에게 전달한다.
SUMMARY sections=10 children=31 maxChild=165 maxParent=375
CORPUS docs=26/53 sections=430 part={admin=7, user=19} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=2}
TOTAL files=26 targets=1 violations=0
```

최종 검사 출력에서 MASK 줄 0, CROSS VIOLATION 줄 0을 직접 계수했다. corpus 수는 검사 당시 기존 파일을 포함한 관측값이며 이 세션이 26개를 작성·검증했다는 뜻이 아니다. 대상은 한 파일이다. 최대 자식 165자, 최대 부모 375자다. 검사기의 원천 commit:path·줄 범위 검사도 위반 0이다.

도구 판본: GNU bash 5.2.37(1)-release(x86_64-pc-msys), OpenJDK Temurin 21.0.9+10-LTS, Git 2.52.0.windows.1. `java -version`은 stderr로 판본을 출력하여 Windows PowerShell에서 NativeCommandError 형식으로 표시됐지만 검사 Java 프로세스의 실패가 아니며 실제 검사 종료코드는 0이었다.

사람 검사는 40절 미만이므로 10절 전체를 읽었다.

| 절 | 제목만으로 증상 식별 | 원천 일치 | 절 단독 답 | 다른 절·문서 중복 |
|---|---|---|---|---|
| 공정 흐름에서 설비가 없거나 흐려서 선택되지 않을 때 | 적합 | 라인·필터·enabled 대조 | 순서와 인계가 함께 있음 | 정상 검색 전체 대신 누락 분기에 제한 |
| 공정 흐름에는 있지만 3D에만 설비가 없을 때 | 적합 | 층·별도 placements 대조 | 비교·추가 경로·인계 있음 | 집계/정상 배치 절차 복제 없음 |
| 상태가 그대로인 것과 조회 갱신이 끊긴 것을 가르기 | 적합 | dataUpdatedAt·오류 렌더 대조 | 두 관측 결과로 분기 | 로그 빈 표시 답은 소유 문서 연결 |
| 배치가 비거나 보기 전환·편집 단추가 없을 때 | 적합 | 노출·조회 활성·기본 권한 대조 | 단추 부재와 기본 배치의 원인 후보 구별 | 권한 사전 대신 필요한 최소 조건 |
| 3D 영역에만 화면 표시 실패가 나올 때 | 적합 | 별도 경계·reload 대조 | 공정 보드와 3D 비교 및 인계 | 공통 렌더 실패 설명 반복 없음 |
| 저장을 누를 수 없는 것과 요청 실패를 구별하기 | 적합 | 비활성·오류 위치·초안 폐기 대조 | 비활성과 요청 후 실패 분기 | 정상 성공 문구·저장 절차는 기존 문서 연결 |
| 공정 그룹만 바꿨는데 설비·객체 오류로 저장이 막힐 때 | 적합 | 동반 placements·서버 검사 대조 | 보드 수정과 3D 원인 구별·인계 | 이 결합 진단은 기존 정상 절차와 구별 |
| 3D 저장에서 설비 연결 오류와 형식 오류를 가르기 | 적합 | 바인딩·서버 검사·권한 대조 | 화면 수정 가능/인계 분기 | 상세 오류 사전이나 정상 배치 절차 복제 없음 |
| 구조물이 목록에 없거나 불러오기가 중단될 때 | 적합 | 미조회 빈 값·기성 숨김·형식 return 대조 | 조회·중복 명칭·불러오기 분기 | 객체 작성 전체 대신 기존 객체 보존 인계 |
| 구조물 저장이 입력 안내에서 멈추거나 서버에서 거부될 때 | 적합 | 요청 전 검사·서버 검사·resetDraft 대조 | 입력/서버 거부/성공 후 초기화 구별 | 정상 입력 조건·도형 작성은 연결 |

제목 정확 중복·불필요한 3회 이상 틀 반복·본문 복제·정보 없는 틀 문장·이름 토막 뜻풀이·깨진 문장·미해소 마스킹·관리 전용 답 사실은 최종본에서 확인하지 못했다. 작성 중 발견한 비한국어 오타 두 곳은 첫 검사 전에 고쳤다. 다른 문서와 같은 자식 본문은 기계 검사 0이며 의미가 겹치는 최소 조건은 위 소유 경계로 한정했다. user 본문에 admin objectKey나 내부 접속·변경 지시를 넣지 않았다.

제품 빌드·단위/통합 시험·실제 UI 및 현장 재현·검색 품질 실측은 수행하지 않았다. 로컬 파일의 기계 검사와 소스 대조를 이 검증들과 혼동하지 않는다. 독립 Agent 호출 0회이며 다른 Actor를 사용하지 않았다. 기계 검사 2회. 두 검사 명령의 실제 경과 시간은 도구가 초기 반환 대기 약 10초와 완료 후 poll을 나누어 보여 전체 실행 시간을 별도로 확정하지 않았다. 전체 작업 wall time·provider 사용량·비용은 미측정이다.

검사용 임시 파일·브라우저·서비스·컨테이너는 만들지 않았다. 검사 출력은 본 기록에 보존했으며 별도 정리 대상이 없다. 작성 기록과 검색 원문 두 파일만 새로 만들었다.

마지막 파일 점검에서 두 파일 모두 엄격한 UTF-8 해석 성공, BOM·CR·NUL 없음, NFC=true, 줄 끝 공백 0, 뜻하지 않은 일본어·한자 문자 0을 확인했다. 검색 원문은 SHA-256과 151줄·10절이 위 대상 기록과 일치했다. 검색 원문의 admin objectKey 참조 0, 기록에 적은 검색 원문 해시의 일치=true도 확인했다. 지정 `doc-27-live-map-troubleshooting/work/`는 존재하며 숨김 항목을 포함한 자식 수 0이었다. 이 세션이 만들지 않은 빈 작업 디렉터리는 보존했다.

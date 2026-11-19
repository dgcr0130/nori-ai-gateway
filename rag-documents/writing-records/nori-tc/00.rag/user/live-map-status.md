# Live-Map 상태 보기 작성 기록

## 대상

- objectKey: `00.rag/user/live-map-status.md`
- 구획: user, 필요한 문서 열람 권한 [AI_DOC_READ]
- 유형: 사용 절차
- 검색 원문: `rag-documents/nori-tc/00.rag/user/live-map-status.md`
- 검색 원문 SHA-256: `f64f5721df75316ff1cf6c3e1677009c9d2a6fda013d4a7c555e73191c33d119`
- 최종 줄 수: 171
- H2 절 수: 9
- 확인일: 2026-11-18 (한국 시간)
- 범위: 사용자 매뉴얼 §7.1~7.2의 상태 조회·보기 조작. 배치 편집·로그 상세 조작은 아래 소유권 구분을 적용했다.
- 독립 작성 세션에서 직접 조사·작성·자체 검사했다. 다른 Agent 호출·서버 접속·제품 시험·업로드·Git 변경 명령은 실행하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 결과 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 최종 rag-meta가 인용한 23개 파일에 대한 git status --short 출력이 비어 있음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 최종 rag-meta가 인용한 7개 파일에 대한 git status --short 출력이 비어 있음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 최종 rag-meta가 인용한 6개 파일에 대한 git status --short 출력이 비어 있음, exit 0 |

실행한 판본 확인은 저장소별 `git -c safe.directory=<해당 절대 저장소 경로> -C <저장소> rev-parse --short=12 HEAD`와 `git -c safe.directory=<해당 절대 저장소 경로> -C <저장소> status --short -- <실제 인용 파일 목록>`이다. 수정 상태 파일이 없어 파일 SHA 판본으로 대체한 원천은 없다.

처음 일반 Git 호출은 `fatal: detected dubious ownership in repository`와 소유자 DongGeon / 실행 사용자 CodexSandboxOffline의 차이를 보고했다. 경로를 확인한 후 호출에만 세 저장소의 safe.directory를 적용했다. 전역 Git 설정·저장소 설정은 바꾸지 않았다. Git의 사용자 전역 ignore 접근 경고는 있었지만 최종 원천 상태 명령은 exit 0이었다.

처음 제품 저장소 상태는 `main...origin/main [ahead 8]`, `?? rag-documents/`였고 worktree 목록은 현재 제품 저장소 하나였다. 앞선 검색 원문은 `login-and-screen-basics.md` 하나였으며 전체를 읽었다. 기존 파일은 수정하지 않았다.

필수 작성 규칙과 53개 문서 목록 전체를 읽었다. 매뉴얼 L1774~1882와 화면 렌더·한국어 문구·조회 API·서버 처리·기본 권한 데이터·관련 시험의 기대값을 대조했다. 실제 실행 화면이나 현장 DB를 조회했다는 뜻은 아니다.

## 절별 사실 대조

아래 표는 검색 원문의 모든 본문 문장·목록 항목을 다룬다. 같은 원문 줄에 여러 문장이 있으면 사실별로 행을 나눴다. 인계 안내는 사용자 지시의 증상·시각·대상·화면 결과 기준을 적용했으며 제품이 자동으로 수집한다는 주장은 하지 않았다.

### Live-Map 열기와 배치 조회 권한 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L29 | 계정 권한·Live-Map 메뉴 진입 | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L82~87<br>nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 L1~5<br>nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L63~68<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1096~1096 | 화면 경로에 LIVE_MAP_READ를 연결하며 메뉴 표시 문구가 Live-Map이다. |
| L29 | 처음 보이는 공정 흐름 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L73~86<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L364~366 | 초기 viewMode가 viewer이며 한국어 문구가 공정 흐름이다. |
| L31 | 상태·라인·배치의 기본 API 권한 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L150~158<br>nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L213~239<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 상태 EXACT GET, 라인 EXACT GET, 배치 PREFIX GET/전 메서드의 권한을 각각 확인했다. 매칭된 권한 중 하나를 보유해야 한다. |
| L31 | 현장 계정 권한은 운영 관리자에게 확인 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1776~1780 | 소스 기본 데이터와 현장 부여값을 구별하는 인계 안내이다. 현장 권한을 조회하지 않았다. |
| L33 | 보기 전환 단추와 구조물 편집 노출 조건 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L81~86<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L257~306 | canViewLayout이 READ 또는 WRITE이고, builder 단추는 canEdit일 때만 렌더한다. |

### 공정 흐름에서 설비를 찾아 고르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L45 | 선택 라인의 그룹·설비 ID 조회 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L136~150<br>nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 L50~58 | 선택 라인 노드로 그룹 카드를 만들고 설비 행에는 eqpId를 표시한다. |
| L45 | 그룹 미배정 설비의 위치 | nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 L240~258<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L494~495<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L368~368 | 유효한 그룹 배정이 없으면 미배정 그룹으로 분류하고 번역 문구를 적용한다. |
| L46 | 검색 입력 대상 | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L568~575<br>nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L24~39 | 검색 칸과 eqpId·eqpName·lineLabel·clusterName의 부분 포함 검사를 확인했다. |
| L46 | 공백·대소문자 처리 | nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L24~37 | 입력은 trim/toLowerCase, 대상 필드는 toLowerCase/includes를 적용한다. |
| L47 | 상태 선택값과 검색 조건 결합 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L116~125<br>nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L27~39 | 전체 상태·RUN·IDLE·ERROR·DOWN을 제공하며 라인·상태·검색어를 AND로 결합한다. |
| L48 | 설비 ID 선택 후 상세 열기 | nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 L34~43<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L172~178<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L897~908 | 비활성 표시가 아닌 노드의 onSelect가 상세의 설비를 갱신한다. |
| L50 | 조건 밖 설비의 흐린 표시와 선택 금지 | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L840~846<br>nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 L42~48 | visibleEquipmentIds에 없으면 dimmed, disabled 및 opacity-30을 적용한다. |
| L50 | 선택 설비가 필터에서 빠질 때 대체 선택 | nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L85~101<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L197~201 | 현재 선택이 보이는 노드에 없으면 ERROR 우선, 다음 첫 노드, 다음 null 순으로 대체한다. |
| L50 | 일치 설비가 없으면 선택 해제 | nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L96~100<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L197~201 | 빈 목록의 결과 null을 선택 상태에 반영한다. |

### 상단 집계와 실시간 갱신 표시 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L64 | TOTAL의 집계 범위 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L152~165<br>nori-tc-ui-front/src/features/live-map/components/LiveMapHeader.tsx @ 9fe69a75dcd3 L35~55<br>nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L46~77 | 검색·상태·라인을 적용한 visibleEquipmentNodes를 집계해 상단에 전달한다. |
| L64 | RUN·IDLE·ERROR·DOWN 상태별 수 | nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L49~76<br>nori-tc-ui-front/src/features/live-map/components/LiveMapHeader.tsx @ 9fe69a75dcd3 L9~18 | 각 노드의 status별로 카운터를 올린다. |
| L64 | 검색어를 지우고 전체 상태로 복원 | nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L24~39 | 빈 검색어는 모두 일치하고 ALL 상태는 상태 제한을 적용하지 않는다. |
| L66 | 사용 설정이 켜진 설비가 조회 대상 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaLiveMapPrefetchPort.java @ 985d17c604dd L85~98 | 조회 목록을 UiEqp.enabled로 거른다. 등록 목록의 모든 레코드를 집계한다고 쓰지 않았다. |
| L66 | 등록 대수와 표시 대수 구별 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaLiveMapPrefetchPort.java @ 985d17c604dd L88~92<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L152~165 | 서버의 활성 필터와 화면의 추가 필터가 적용되므로 비활성 포함 등록 대수와 같다고 단정할 수 없다. |
| L68 | 5초 간격 조회 | nori-tc-ui-front/src/features/live-map/hooks/useLiveMap.ts @ 9fe69a75dcd3 L4~10 | refetchInterval=5_000을 확인했다. 실제 네트워크 도착 간격을 실측했다는 의미는 아니다. |
| L68 | 실시간 뒤 경과 시간의 기준 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L189~217<br>nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L106~109<br>nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L511~515<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L391~391 | dataUpdatedAt을 markSynced에 전달하고 Date.now와의 차이를 초·소수 한 자리로 표시한다. |
| L68 | 설비 상태 변경 시각과 구별 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L213~217<br>nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 L106~109 | 표시 입력은 조회 데이터 갱신 시각이며 설비 상태 변경 시각 필드를 쓰지 않는다. |

### 선택 설비에서 상태와 포트 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L83 | 설비 점과 포트 점의 위치 | nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 L50~68 | 첫 줄 점은 설비 status, 아래 점 목록은 각 port status를 사용한다. |
| L83 | 설비 점의 네 색과 ERROR 점 깜빡임 | nori-tc-ui-front/src/features/live-map/lib/live-map-status-theme.ts @ 9fe69a75dcd3 L21~52<br>nori-tc-ui-front/src/index.css @ 9fe69a75dcd3 L105~138<br>nori-tc-ui-front/src/index.css @ 9fe69a75dcd3 L188~188<br>nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 L51~54 | RUN success(초록), IDLE sky(파랑), ERROR danger(빨강), DOWN muted-foreground(회색)이며 ERROR는 animate-pulse이다. |
| L85 | 선택 설비의 표시 항목 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L38~42<br>nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L49~89<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L395~411 | 설비 ID·상태 코드와 공정·라인·웨이퍼 모드·실시간 상태의 실제 렌더 및 한국어 문구를 대조했다. |
| L87 | 서버 알림 우선 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L28~36 | alertMessage가 null이 아니면 기본 상태 안내보다 먼저 쓴다. |
| L87 | ERROR의 실제 문구 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd L65~88 | ERROR 응답의 alertMessage는 설비 상태값을 확인해 주세요.이다. |
| L87 | 알림이 없을 때 기본 안내 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L28~36<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L414~417 | ERROR/IDLE/DOWN/그 외 순서로 기본 안내를 선택한다. 상태 의미의 상세 표는 복제하지 않았다. |
| L89 | RUN 포트 수 / 전체 포트 수 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L92~103 | RUN 필터 개수와 portItems.length를 순서대로 렌더한다. |
| L89 | 포트별 ID·상태·캐리어 표시 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L105~128<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd L197~210 | 포트 라벨과 statusLabel은 포트 ID·정규화 상태이며 carrierId도 출력한다. |
| L89 | 캐리어 미지정 표시 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L119~126<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd L205~207 | null/빈 캐리어는 화면에서 —로 표시한다. |
| L91 | 닫기 후 설비 재선택 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L53~62<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L172~182 | 닫기는 dismissed를 켜고 설비 선택은 dismissed를 해제한다. |

### 최근 알림에서 통신 로그로 이동하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L104 | 최근 알림의 위치와 로그 내용 | nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 L134~136<br>nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L224~274<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L396~418 | 상세 하단에 통신 로그 패널을 붙이며 최근 알림 문구와 통신 기록을 렌더한다. |
| L104 | 더보기 후 통신 로그 목록 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L235~243<br>nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L282~287<br>nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L115~145 | 단추는 목록 대화상자를 열고 현재 eqpId와 로그를 전달한다. |
| L104 | 로그 조회 기본 권한 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L42~50 | GET /api/comm-logs PREFIX의 COMM_LOG_READ를 확인했다. 현장 부여값으로 쓰지 않았다. |
| L106 | 목록이 비었을 때 문구 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~213<br>nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L250~253<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L420~420 | data 기본값은 빈 배열이고 길이가 0이면 최근 1시간 내 로그가 없습니다.를 출력한다. |
| L106 | 조회 실패와 통신 중단을 빈 문구만으로 구별 불가 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~213<br>nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L224~279 | 패널은 isError/error를 사용하지 않아 초기 실패도 데이터 없는 표시와 구별되지 않는다. |
| L106 | 담당자에게 넘길 정보 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L211~215<br>nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1877~1882 | 선택 설비에 매달린 조회 결과를 증상·시각·화면 결과로 인계한다는 사용자 작업 지침을 적용했다. 내부 조치는 넣지 않았다. |

### 공정 보드를 확대하거나 기본 배율로 돌리기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L116 | 확대·축소 단추와 결과 | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L891~893<br>nori-tc-ui-front/src/features/live-map/stores/live-map-ui.store.ts @ 9fe69a75dcd3 L49~52<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L401~404 | 왼쪽 레일에 세 단추를 두며 배율 증감과 기본값 복원을 연결한다. |
| L116 | 기본 배율 복원 | nori-tc-ui-front/src/features/live-map/stores/live-map-ui.store.ts @ 9fe69a75dcd3 L3~9<br>nori-tc-ui-front/src/features/live-map/stores/live-map-ui.store.ts @ 9fe69a75dcd3 L50~50 | resetZoom은 DEFAULT_ZOOM_LEVEL=1로 되돌린다. |
| L118 | 편집 전환·저장 없이 배율 변경 | nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 L880~893<br>nori-tc-ui-front/src/features/live-map/stores/live-map-ui.store.ts @ 9fe69a75dcd3 L49~52 | 확대·축소 레일은 편집 권한 분기 밖이고 배율은 화면 상태만 바꾼다. |

### 3D에서 층과 시점을 바꾸며 설비 선택하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L131 | 3D 전환 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L81~86<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L257~288 | 배치 조회 가능 조건 아래에서 3D 단추로 scene3d 모드를 고른다. |
| L132 | 회전·화면 이동·확대 축소 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L411~413<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1428~1435<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1019~1019 | 비편집 OrbitControls와 실제 조작 안내를 함께 대조했다. 실브라우저 조작 시험은 하지 않았다. |
| L133 | 층 단추의 실제 순서 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L96~102<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1215~1248 | FLOOR_LEVELS=[1,2]를 역순 렌더한 2F·1F와 마지막 전체 단추이다. |
| L133 | 전체 층의 적층 표시 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L330~350<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L395~407 | 전체는 floor 기준 높이를 쓰고 단일층은 같은 바닥 높이로 표시한다. |
| L134 | 설비 이름표로 선택 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1094~1125 | 이름표는 eqpId·statusLabel을 출력하며 pointerdown에서 selectPlacement를 호출한다. |
| L134 | 편집 중이 아닐 때 설비 상세 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L701~713<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1420~1425 | 실설비와 연결된 선택 노드의 상세는 !editing일 때만 렌더한다. |
| L136 | 공정 흐름으로 복귀 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L259~273 | 공정 흐름 단추가 viewer 모드로 전환한다. |

### 3D 합계와 상단 설비 수가 다른 이유 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L148 | 선택 층의 합계·설비 수 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L652~673<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1134~1152<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L991~994 | 현재 층으로 필터한 배치 전체 수와 kind=EQUIPMENT 수를 출력한다. |
| L148 | 구조물 종류별 항목 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L658~668<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1145~1149 | 놓인 STRUCTURE의 objectName별 집계이므로 없는 종류는 나오지 않는다. |
| L150 | 조회 모드에 저장된 배치를 사용 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L451~469<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L597~614<br>nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd L36~54 | 서버 배치로 working을 만들며 미저장 라인은 빈 placements를 반환한다. |
| L150 | 미배치 설비 자동 표시 없음 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L559~563<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L597~614 | resolved는 working 배치만 순회한다. 설비 목록을 전부 자동 배치하지 않는다. |
| L150 | 공정 필터와 3D 층의 집계 범위 구별 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L152~165<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L652~673 | 상단은 visibleEquipmentNodes, 3D는 resolved와 floorView를 사용하며 서로의 필터가 입력되지 않는다. |
| L152 | 개수 안내 툴팁 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1136~1139<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L991~991 | title 속성에 실제 개수 안내 문구를 연결한다. |
| L152 | 편집 중 변경 수 | nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L616~650<br>nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 L1153~1158 | 기준 배치 대비 추가·삭제·수정 수를 editing일 때만 출력한다. |

### 상태 조회나 3D 화면 표시가 실패했을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L167 | 상단 상태 조회 오류 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L240~245 | liveMapQuery.isError일 때 오류 영역을 렌더한다. |
| L167 | 서버 문구 우선과 대체 문구 | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~77<br>nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L358~358 | 서버 errorMsg를 Error.message에 담고 없으면 한국어 fallback을 사용한다. |
| L167 | 새로고침·권한 요청 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1877~1882<br>nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 매뉴얼의 상태 조회 실패 대처를 유지하고 권한 거부는 입력 수정 대신 운영 관리자에게 인계한다. |
| L169 | 3D 표시 실패 안내와 같은 영역의 새로고침 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L321~333<br>nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 L41~55<br>nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L477~479 | 3D 경계의 두 문구와 window.location.reload 단추를 확인했다. |
| L171 | 지속 실패 시 전달 정보 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1877~1882<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L105~110<br>nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L224~226 | 지속 시 관리자에게 알리는 원천에 사용자 지정 인계 항목을 적용했다. 내부 시스템 접근을 안내하지 않았다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 코드 | 검색 원문에 쓴 내용 |
|---|---|---|---|
| 상단 집계 범위 | U L1782~1783은 선택 라인의 설비 수로 설명한다. | LiveMapPage L152~165와 selectors L20~77은 라인·검색어·상태 필터를 모두 적용한다. | 조회한 설비 중 세 조건을 만족하는 수라고 썼다. |
| 설비 행의 표시값 | U L1792~1793은 설비 이름으로 설명한다. | LiveMapMachineNode L50~58은 eqpId를 표시한다. 서버 toEquipmentView도 eqpName에 eqpId를 넣는다(L72~75). | 클릭 대상과 표시값을 설비 ID라고 썼다. |
| 상태 선택의 전체 항목 | U L1811은 선택값에 「전체」를 적는다. | LiveMapPage L116~125와 ko.json L362는 ALL을 「전체 상태」로 표시한다. | 실제 표시인 「전체 상태」를 썼다. |
| ERROR 상세 문구 | U L1831은 「점검 필요」를 네 기본 상태 문구 중 하나로 설명한다. | TooltipCard L28~36은 서버 alertMessage 우선이다. QueryService L87은 ERROR에 「설비 상태값을 확인해 주세요.」를 보낸다. | 실제 서버 문구와 알림 우선 조건을 썼다. |
| 통신 로그 조회 실패 표시 | U L1837~1838은 빈 목록을 기록 부재로 확정하고 「통신 로그를 조회하지 못했습니다.」 표시를 안내한다. | CommLogPanel L211~213은 data=[]와 isFetching만 사용하고 L250~253은 빈 문구를 표시한다. API는 오류를 던지지만 패널은 isError를 렌더하지 않는다. | 빈 문구로 연결 상태를 단정하지 않는 최소 조건과 인계 정보를 썼다. 매뉴얼의 오류 문구를 실제 패널 표시로 쓰지 않았다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 뺀 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 계정의 실제 권한 부여값, 현장 배포 판본, 현재 라인·설비의 실제 상태 | 이번 작업은 저장소 파일 대조이다. 현장 계정·배포·관측값의 원천이 없고 네트워크 조회는 허가 범위 밖이다. | TC 운영 담당자 |

원천에서 확인 가능한 조회 절차는 완성했으므로 사용자 결정이 필요한 항목은 없다. 소스 판본·기본 권한 데이터라는 적용 조건을 본문에 유지했다.

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 넣지 않은 사실·경계 |
|---|---|
| 00.rag/user/login-and-screen-basics.md | 상단바에서 라인을 고르는 상세 순서·기본 선택 동작, 로그인·메뉴 공통 사용법. 여기에는 Live-Map의 범위 조건만 남겼다. |
| 00.rag/user/live-map-layout-editing.md | U §7.1.1의 배치 편집 켜기, 그룹 이동·크기 변경·추가·삭제·화살표 편집·저장과 §7.2의 편집 도구 상세. 목록의 배치·구조물 편집 문서 소유 질문에 따라 이동 대상으로 기록하고 이 문서는 보기 조작만 다뤘다. 실제 이동·수정은 하지 않았다. |
| 00.rag/user/message-results-and-communication-logs.md | 통신 로그 행의 송수신·내용 읽기, 로그 상세 대화상자와 메시지 결과 조회. 여기에는 「최근 알림」·「더보기」 진입과 표시 실패를 오해하지 않을 최소 조건만 넣었다. |
| 00.rag/user/state-reference.md | 원시 제어·설비 상태를 RUN·IDLE·ERROR·DOWN으로 묶는 서버 분기와 포트 원시 상태별 정규화의 상세 의미. 이 문서는 화면에서 상태 점·코드를 읽는 데 필요한 표시만 소유한다. |
| 00.rag/user/permission-code-reference.md | 권한 코드별 전체 뜻·기능 목록. 여기에는 조회에 필요한 코드와 보기 단추 조건만 적었다. |
| 00.rag/user/live-map-troubleshooting.md | 배치 누락·갱신 문제의 원인별 진단과 편집 실패 대처. 이 문서는 조회·화면 표시 실패 시 보이는 단추와 최소 인계만 적었다. |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드·문구 전체 목록과 원인별 해설. 이 문서에는 Live-Map에서 직접 관찰하는 기본 오류 표시만 적었다. |
| 00.rag/user/demo-execution.md | 데모 시작·시뮬레이션 실행·종료. U §7.4 범위이므로 넣지 않았다. |

앞선 문서 발견 사항: `login-and-screen-basics.md` L150~163의 「상단바에서 조회할 라인 고르기」는 실제 라인 조회 API 권한 LINE_READ를 설명하지 않는다. 이 세션에서 확인한 기본 데이터는 `nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L150~158`, DB 패치는 `docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 L17~23`이고 API는 `line.api.ts @ 9fe69a75dcd3 L22~30`이다. 수정하지 않고 메인 검증 세션에 인계한다.

## 검사 결과

검사기 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/live-map-status.md
```

최종 검사 프로세스에만 적용한 Git 읽기 설정은 다음과 같다. 동일한 정본 검사기를 그대로 실행했으며 검사 로직·classpath·마스킹을 바꾸지 않았다.

```text
GIT_CONFIG_COUNT=3
GIT_CONFIG_KEY_0=safe.directory
GIT_CONFIG_VALUE_0=C:/workspace_nori/nori-tc/nori-tc-ui-front
GIT_CONFIG_KEY_1=safe.directory
GIT_CONFIG_VALUE_1=C:/workspace_nori/nori-tc/nori-tc-ui-backend
GIT_CONFIG_KEY_2=safe.directory
GIT_CONFIG_VALUE_2=C:/workspace_nori/nori-tc/nori-tc-exec
```

최종 검사 마지막 출력 10줄:

```text
    L148-148 문단 110  | 3D 오른쪽 위 상자는 선택한 층에 그려진 배치를 센다. 「합계」는 구조물과 설비 배치를 합한 수, 「설비」는 설비 배치 수이다. 「바닥」·「벽」 같은 구조물 항목은 해당 층에 놓인 종류만 나온다.
    L150-150 문단 116  | 조회 모드의 3D에는 저장된 배치가 표시된다. 배치하지 않은 설비는 자동으로 나타나지 않는다. 공정 보드의 검색·상태 필터는 3D 배치에 적용되지 않으며, 층 선택은 페이지 상단 TOTAL에 적용되지 않는다.
    L152-152 문단 64  | 개수 상자에 마우스를 올리면 집계 기준 안내를 읽을 수 있다. 「변경」은 편집 중에만 나타나는 미저장 변경 수이다.
§ L154-171 [23] 상태 조회나 3D 화면 표시가 실패했을 때   parent=274 children=3 sources=9
    L167-167 문단 135  | 상태 조회가 실패하면 화면 상단에 오류 문구가 나온다. 서버가 보낸 오류 문구가 있으면 그 문구를, 없으면 「라이브 맵 정보를 조회하지 못했습니다.」를 표시한다. 화면을 새로고침하고, 권한 거부 문구이면 운영 관리자에게 조회 권한을 요청한다.
    L169-169 문단 79  | 3D 영역에 「화면을 표시하지 못했습니다.」와 「새 버전이 배포된 경우 새로고침하면 복구됩니다.」가 나오면 같은 영역의 「새로고침」을 누른다.
    L171-171 문단 58  | 새로고침 뒤에도 같으면 선택 라인·설비 ID·보기 종류·발생 시각·화면 문구를 운영 담당자에게 전달한다.
SUMMARY sections=9 children=31 maxChild=169 maxParent=406
CORPUS docs=2/53 sections=27 part={user=2} type={사용 절차=2}
TOTAL files=2 targets=1 violations=0
```

최종 exit code는 0이다. VIOLATION·CROSS VIOLATION·MASK 출력은 각각 0건이다. H2 9개·자식 31개이며 최대 자식 169자, 최대 부모 406자이다. 위 숫자는 정본 검사기 최종 출력에서 확인했다.

도구 판본: GNU Bash 5.2.37(1)-release (x86_64-pc-msys), Temurin OpenJDK 21.0.9+10-LTS, git 2.52.0.windows.1이다.

초기 검사에서는 Git 소유권 검사로 `git show`가 실패해 검사기가 원천 commit:path 없음 65건으로 표시했다. 실제 객체는 경로별 Git 읽기 설정으로 존재를 대조했다. 환경 조건을 맞춘 검사에서는 원천 조회를 생략하지 않고 위반 0건을 확인했다. 자체 검사 중 잘못 적은 zoom 원천 범위 L53~57은 실제 함수 L50~52로 바로잡았다. 인라인 원천 확인 보조 명령의 첫 Python 정규식 실행은 PowerShell stdin 인코딩 영향으로 실패했으며 ASCII 패턴으로 바꾼 실행에서 36개 인용 파일의 상태와 최종 해시를 확인했다. 실패한 명령을 성공으로 세지 않았다.

관련 시험은 실행하지 않고 기대값만 읽었다: 프런트 `live-map-line-scope.test.ts`의 라인·검색 AND/빈 라인, `live-map-layout.test.ts`의 ERROR 우선 선택·빈 스코프 null, `live-map-data.test.ts`의 포트 캐리어 보존, 백엔드 `LiveMapQueryServiceTest.java`의 상태·라인 표시, `LiveMapLayoutPermissionTest.java`의 READ/WRITE 인가, `LiveMapLayoutServiceTest.java`의 미저장 라인 빈 배치이다. 문서 작업이므로 제품 build·테스트·브라우저 실행은 하지 않았다.

### 사람 검사 판정

전 9절을 읽었다. 아래 네 판정은 제목만으로 질문 파악 / 원천 일치 / 독립 답 / 중복 경계 순서이다.

| 절 | 제목·원천·독립 답·중복 판정 |
|---|---|
| Live-Map 열기와 배치 조회 권한 확인하기 | 모두 적합. 조회 권한을 기본 데이터 조건으로 한정했다. |
| 공정 흐름에서 설비를 찾아 고르기 | 모두 적합. 필터 결과의 흐린 표시와 선택 대체를 실제 코드대로 구별했다. |
| 상단 집계와 실시간 갱신 표시 읽기 | 모두 적합. 검색·상태·라인 조건과 조회 갱신 시각 기준을 밝혔다. |
| 선택 설비에서 상태와 포트 확인하기 | 모두 적합. 색·코드·서버 알림·포트 숫자를 읽는 답이며 원시 상태 정의는 복제하지 않았다. |
| 최근 알림에서 통신 로그로 이동하기 | 모두 적합. 진입과 권한·빈 표시의 한계만 포함하고 로그 상세 업무를 복제하지 않았다. |
| 공정 보드를 확대하거나 기본 배율로 돌리기 | 모두 적합. 저장 없이 조회 배율을 바꾸는 조작을 완결했다. |
| 3D에서 층과 시점을 바꾸며 설비 선택하기 | 모두 적합. 실제 2F·1F·전체 단추와 비편집 상세 조건을 썼다. |
| 3D 합계와 상단 설비 수가 다른 이유 확인하기 | 모두 적합. 서로 다른 집계 입력과 미배치 설비를 구별했다. |
| 상태 조회나 3D 화면 표시가 실패했을 때 | 모두 적합. 서버 문구 우선·대체 문구·같은 영역 새로고침 단추·인계 정보를 확인했다. |

제목 정확 중복은 0건이며 식별자를 지운 같은 제목 틀의 3회 이상 반복은 없다. 같은 자식 본문과 사실의 불필요한 반복, 정보 없는 틀 문장, 사용처 없는 상태 뜻풀이, 잘린 문장·오답 인용·조사 접합 오류를 발견하지 않았다. 문서 본문에는 표가 없고 순서 목록은 각 작업의 실제 1~4 진행 번호로 썼다. user 본문에 admin objectKey·링크·내부 조치는 없다. 마스킹 후 필요한 본문 값이 사라진 곳은 없었다. 서술형 인계는 사용자가 화면에서 알 수 있는 대상·시각·결과에 한정했다.

검색 원문을 마지막으로 편집한 뒤 검사기를 재실행하고 최종 해시·줄 수·절 수를 다시 셌다. 이 기록은 작성자 자체 확인이며 메인 세션의 독립 대조나 실제 검색 반영·정답률 검증을 대신하지 않는다.

임시 자원은 지정 work 폴더의 이번 세션 검사 출력 `session-live-map-check.txt` 한 파일이다. 필요한 결과는 이 기록에 보존했다. 임시 출력 SHA-256은 `3afd9f2a5c57c05d067399b7e55bd5b6d1e481323b2a402a9f834e58812b3946`이다.

정리 전 절대경로·이번 세션 소유·검사 출력 용도를 확인했다. 파일은 Archive, work와 작업 폴더는 Directory이며 ReadOnly·Hidden·System·ReparsePoint 속성과 링크가 없었다. `Get-CimInstance Win32_Process`는 `액세스가 거부되었습니다.`, `HRESULT 0x80041003`으로 실패하여 명령줄 참조를 전수 확인하지 못했다. 권한을 바꾸거나 다른 프로세스를 종료하지 않았다. 읽기/쓰기 FileShare.None 독점 열기·닫기는 성공해 파일을 공유 중인 열린 핸들이 없음을 확인했다. 검사 프로세스 종료 후 현재 세션의 WriteAllText로 만든 출력이며 해당 기록 명령도 종료됐다. 다른 bash·java·python 프로세스가 있다는 이유로 그 프로세스를 이 파일의 사용자로 단정하지 않았다. 비강제 native PowerShell 정상 삭제 결과는 아래에 덧붙인다.

`Remove-Item -LiteralPath C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-02-live-map-status/work/session-live-map-check.txt -ErrorAction Stop`을 Force·Recurse 없이 실행해 exit 0이었다. 이어 `Test-Path -LiteralPath`는 False, work의 파일 수는 0이었다. 작업 시작 전부터 존재한 work 폴더는 보존했다. 이 세션 소유 임시 파일의 잔여는 없다.


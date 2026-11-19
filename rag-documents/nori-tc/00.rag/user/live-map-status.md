# Live-Map 상태 보기
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/message-results-and-communication-logs.md
```

## Live-Map 열기와 배치 조회 권한 확인하기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; Router L82~87
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; DASH_READ_PERMISSION L1~5
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 초기 보기·canViewLayout L73~86
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 보기 전환 단추 L257~306
원천: nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 ; useLiveMapFlow L18~33
원천: nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; getLineList L22~30
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; list L38~43
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd ; getLiveMap L33~52
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 배치·상태 조회 기본 권한 L213~239
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 라인 조회 기본 권한 L150~158
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; LIVE_MAP_READ L775~775
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_live_map_layout_read_permission.sql @ f1d5e0a8e727 ; 배치 조회 권한 L19~25
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 배치 권한 L17~23
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 7.1.1 절차 L1789~1793
```

LIVE_MAP_READ 권한이 있는 계정으로 좌측 메뉴의 「Live-Map」을 연다. 처음에는 「공정 흐름」 보기가 열린다.

저장소 기본 권한 데이터 기준으로 상태 조회에는 LIVE_MAP_READ, 라인 목록 조회에는 LINE_READ, 저장된 공정 그룹·3D 배치 조회에는 LIVE_MAP_LAYOUT_READ·LIVE_MAP_LAYOUT_WRITE 중 하나가 필요하다. 현장 계정에 부여된 권한은 운영 관리자에게 확인한다.

배치 조회 권한이 있으면 「공정 흐름」·「3D」 전환 단추가 나타난다. 「구조물 편집」은 LIVE_MAP_LAYOUT_WRITE가 있을 때 나타난다.

## 공정 흐름에서 설비를 찾아 고르기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 필터·선택 L116~187
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 ; filterLiveMapEquipmentNodes·resolveSelectedLiveMapEquipment L20~101
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 검색·상태 선택 L568~592
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 ; LiveMapMachineNode L34~69
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 ; resolveLiveMapFlowGroupId L240~247
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 검색·미배정 문구 L361~368
```

1. 상단바에서 선택한 라인의 공정 그룹과 설비 ID를 살펴본다. 그룹에 배정되지 않은 설비는 「미배정」에 모인다.
2. 「설비명 검색」에 설비 ID·설비명·라인명·공정을 입력한다. 앞뒤 공백과 영문 대소문자를 구별하지 않는 부분 검색이다.
3. 「전체 상태」에서 RUN·IDLE·ERROR·DOWN 중 하나를 골라 검색어와 함께 범위를 좁힌다.
4. 흐리게 표시되지 않은 설비 ID를 누르면 오른쪽에 「선택 설비」가 열린다.

검색·상태 조건에서 벗어난 설비는 보드에 흐리게 남고 누를 수 없다. 선택 설비가 조건에서 벗어나면 조건에 맞는 ERROR 설비, 없으면 첫 설비가 선택된다. 조건에 맞는 설비가 없으면 선택이 해제된다.

## 상단 집계와 실시간 갱신 표시 읽기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; summary·동기화 L152~217
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapHeader.tsx @ 9fe69a75dcd3 ; LiveMapHeader L9~55
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 ; summarizeLiveMapEquipmentNodes·formatLiveMapSyncLabel L46~109
원천: nori-tc-ui-front/src/features/live-map/hooks/useLiveMap.ts @ 9fe69a75dcd3 ; useLiveMap L4~10
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 실시간 표시 L507~518
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaLiveMapPrefetchPort.java @ 985d17c604dd ; prefetch L85~98
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 집계·실시간 문구 L360~360
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실시간 문구 L391~391
```

상단의 TOTAL은 조회한 설비 중 선택 라인·검색어·상태 조건을 모두 만족하는 설비 수이다. RUN·IDLE·ERROR·DOWN은 그 대상의 상태별 수이다. 라인 전체를 세려면 검색어를 지우고 「전체 상태」를 고른다.

Live-Map 조회 대상은 사용 설정이 켜진 설비이다. 비활성 설비를 포함한 등록 대수와 상단 집계가 같다고 보지 않는다.

설비 상태는 5초 간격으로 다시 조회한다. 공정 보드 제목 옆 「실시간」 뒤의 초 단위 값은 마지막 조회 데이터 갱신 뒤 경과 시간이다. 설비에서 마지막으로 상태가 바뀐 시각을 뜻하지 않는다.

## 선택 설비에서 상태와 포트 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapTooltipCard.tsx @ 9fe69a75dcd3 ; LiveMapTooltipCard L27~136
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 ; 상태 점 L50~68
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-status-theme.ts @ 9fe69a75dcd3 ; 설비·포트 상태 색 L21~52
원천: nori-tc-ui-front/src/index.css @ 9fe69a75dcd3 ; 상태 색 L105~138
원천: nori-tc-ui-front/src/index.css @ 9fe69a75dcd3 ; 어두운 화면의 회색 L188~188
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 상세 닫기·다시 선택 L172~182
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toEquipmentView L65~88
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapQueryService.java @ 985d17c604dd ; toPortView L197~210
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 표시 문구 L394~417
```

공정 보드의 설비 ID 옆 점은 설비 상태, 아래 작은 점들은 포트 상태를 나타낸다. 설비 점은 RUN 초록·IDLE 파랑·ERROR 빨강·DOWN 회색이며 ERROR 점은 깜빡인다.

설비를 선택하면 「선택 설비」에서 설비 ID와 상태 코드, 「공정」·「라인」·「웨이퍼 모드」를 확인한다.

「실시간 상태」는 서버 알림이 있으면 그 문구를 우선 표시한다. ERROR 설비에는 「설비 상태값을 확인해 주세요.」가 표시된다. 알림이 없으면 상태에 따른 안내 문구를 표시한다.

「PORT 상태」 오른쪽 숫자는 RUN 포트 수 / 전체 포트 수이다. 포트별로 포트 ID·상태 코드·캐리어 ID를 읽는다. 캐리어 ID가 없으면 「—」로 표시한다.

상세 오른쪽 위 「닫기」를 누르면 상세가 닫힌다. 설비 ID를 다시 누르면 상세가 열린다.

## 최근 알림에서 통신 로그로 이동하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; LiveMapCommLogPanel L211~294
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; CommLogListModal L115~145
원천: nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 ; getLogs L20~34
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd ; getCommLogs L35~45
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 통신 로그 기본 권한 L42~50
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_05_07_add_comm_log_api_permission.sql @ f1d5e0a8e727 ; 통신 로그 권한 L20~39
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 최근 알림·통신 로그 문구 L396~420
```

「선택 설비」 아래의 「최근 알림」에서 통신 기록을 살펴본다. 「더보기」를 누르면 선택 설비의 「통신 로그」 목록이 열린다. 저장소 기본 권한 데이터 기준으로 로그 조회에는 COMM_LOG_READ가 필요하다.

목록이 비어 있으면 「최근 1시간 내 로그가 없습니다.」가 나온다. 이 영역은 조회 실패 여부를 별도로 표시하지 않으므로 빈 문구만으로 설비 연결 상태를 판단하지 않는다. 확인이 필요하면 선택 설비 ID·발생 시각·화면 결과를 운영 담당자에게 전달한다.

## 공정 보드를 확대하거나 기본 배율로 돌리기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 확대·축소 단추 L891~893
원천: nori-tc-ui-front/src/features/live-map/stores/live-map-ui.store.ts @ 9fe69a75dcd3 ; 배율 상수 L3~9
원천: nori-tc-ui-front/src/features/live-map/stores/live-map-ui.store.ts @ 9fe69a75dcd3 ; 확대·축소 동작 L50~52
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 배율 단추 문구 L401~404
```

「공정 흐름」 보드 왼쪽 세로 막대에서 「맵 확대」를 누르면 설비·그룹을 크게, 「맵 축소」를 누르면 작게 본다. 「보드 기본 배율로 맞추기」를 누르면 기본 배율로 돌아간다.

조회할 때 배율을 바꾸는 데는 「배치 편집」을 켜거나 「저장」을 누를 필요가 없다.

## 3D에서 층과 시점을 바꾸며 설비 선택하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 보기 전환 L257~288
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; FLOOR_LEVELS L96~102
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; OrbitControls L411~413
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 설비 선택 L1094~1125
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 층 선택 L1215~1248
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 상세·조작 안내 L1420~1435
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 3D 보기 안내 L1019~1019
```

1. 배치 조회 권한이 있는 계정으로 화면 오른쪽 위 「3D」를 누른다.
2. 화면을 드래그해 회전하고, 오른쪽 단추로 드래그하거나 두 손가락으로 화면을 이동한다. 휠로 확대·축소한다.
3. 왼쪽의 「2F」·「1F」·「전체」 중 하나를 눌러 볼 층을 고른다. 「전체」에서는 층을 쌓아 보여 준다.
4. 설비 ID와 상태 코드가 적힌 이름표를 누르면 설비가 선택된다. 편집 중이 아닐 때 오른쪽에 설비 상세가 열린다.

공정 보드로 돌아가려면 화면 오른쪽 위 「공정 흐름」을 누른다.

## 3D 합계와 상단 설비 수가 다른 이유 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 저장된 배치 읽기 L451~469
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 상단 집계·보기별 조립 L152~165
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 배치·층별 집계 L597~673
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 개수 상자 L1134~1158
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd ; getLayout L36~54
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 개수 안내 L991~994
```

3D 오른쪽 위 상자는 선택한 층에 그려진 배치를 센다. 「합계」는 구조물과 설비 배치를 합한 수, 「설비」는 설비 배치 수이다. 「바닥」·「벽」 같은 구조물 항목은 해당 층에 놓인 종류만 나온다.

조회 모드의 3D에는 저장된 배치가 표시된다. 배치하지 않은 설비는 자동으로 나타나지 않는다. 공정 보드의 검색·상태 필터는 3D 배치에 적용되지 않으며, 층 선택은 페이지 상단 TOTAL에 적용되지 않는다.

개수 상자에 마우스를 올리면 집계 기준 안내를 읽을 수 있다. 「변경」은 편집 중에만 나타나는 미저장 변경 수이다.

## 상태 조회나 3D 화면 표시가 실패했을 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 조회 오류·3D 오류 경계 L240~245
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; ErrorBoundary L321~333
원천: nori-tc-ui-front/src/features/live-map/api/live-map.api.ts @ 9fe69a75dcd3 ; getLiveMap L41~50
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse·normalizeApiError L19~77
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; resolveErrorMessage L11~12
원천: nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 오류 안내·새로고침 L41~55
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L358~358
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 표시 오류 L477~479
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 7.2.4 오류와 대처 L1877~1882
```

상태 조회가 실패하면 화면 상단에 오류 문구가 나온다. 서버가 보낸 오류 문구가 있으면 그 문구를, 없으면 「라이브 맵 정보를 조회하지 못했습니다.」를 표시한다. 화면을 새로고침하고, 권한 거부 문구이면 운영 관리자에게 조회 권한을 요청한다.

3D 영역에 「화면을 표시하지 못했습니다.」와 「새 버전이 배포된 경우 새로고침하면 복구됩니다.」가 나오면 같은 영역의 「새로고침」을 누른다.

새로고침 뒤에도 같으면 선택 라인·설비 ID·보기 종류·발생 시각·화면 문구를 운영 담당자에게 전달한다.

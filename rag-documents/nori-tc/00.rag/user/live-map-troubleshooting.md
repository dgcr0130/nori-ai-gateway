# Live-Map 표시·조작 문제 확인
```rag-meta
유형: 트러블슈팅
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/live-map-status.md
```

## 공정 흐름에서 설비가 없거나 흐려서 선택되지 않을 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 라인·필터·선택 L136~187
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 ; filterLiveMapEquipmentNodes L20~41
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapMachineNode.tsx @ 9fe69a75dcd3 ; dimmed L34~48
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaLiveMapPrefetchPort.java @ 985d17c604dd ; prefetch L85~98
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 검색·상태 문구 L361~368
```

1. 상단바의 라인이 대상 설비의 라인인지 확인한다.
2. 「설비명 검색」을 비우고 상태 목록을 「전체 상태」로 바꿔 다시 선택한다. 검색·상태 조건에서 벗어난 설비는 흐리게 남고 선택되지 않는다.
3. 조건을 풀어도 설비가 없으면 사용 설정을 확인한다. Live-Map 조회는 사용 설정이 켜진 설비만 포함한다. 등록 정보 확인 경로는 `00.rag/user/equipment-registration-and-settings.md`를 따른다.

계속 누락되면 설비 ID·선택 라인·검색어·상태 조건·확인 시각을 운영 담당자에게 전달한다.

## 공정 흐름에는 있지만 3D에만 설비가 없을 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 배치 읽기·미배치 후보 L465~469 L547~563
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 층별 표시·이름표 L597~614 L652~656 L691~699
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 층 선택 L1215~1248
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd ; getLayout L41~55
```

3D에서 먼저 왼쪽 층 선택을 「전체」로 바꾼다. 설비가 나타나면 층 선택 때문에 보이지 않았던 경우다.

「전체」에서도 없으면 같은 라인의 공정 보드와 비교한다. 공정 보드의 설비 조회와 3D의 저장 배치는 별개이므로, 공정 보드에 있다는 사실만으로 3D 배치까지 저장됐다고 볼 수 없다.

편집 권한자는 「— 미배치 설비 —」에서 대상이 남아 있는지 확인하고 `00.rag/user/live-map-layout-editing.md`의 추가·저장 절차를 따른다. 이미 저장한 설비가 사라졌다면 설비 ID·라인·선택 층·직전 저장 여부를 운영 담당자에게 전달한다.

## 상태가 그대로인 것과 조회 갱신이 끊긴 것을 가르기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 동기화·조회 오류 L189~217 L240~245
원천: nori-tc-ui-front/src/features/live-map/hooks/useLiveMap.ts @ 9fe69a75dcd3 ; useLiveMap L4~10
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 실시간 표시 L507~518
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-selectors.ts @ 9fe69a75dcd3 ; formatLiveMapSyncLabel L103~109
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 ; 조회 상태·목록 L211~294
```

공정 보드의 「실시간」 뒤 숫자가 다시 작아지는지와 화면 상단의 조회 오류를 함께 확인한다. 숫자가 계속 커지면 마지막 조회 데이터가 오래 갱신되지 않은 증상으로 기록한다.

숫자는 갱신되는데 상태만 같으면 설비 ID와 「선택 설비」의 상태를 기록한다. 조회 갱신은 설비 상태 변경을 보장하지 않는다. 「최근 알림」도 함께 살피되 로그의 빈 표시를 판별하는 경로는 `00.rag/user/message-results-and-communication-logs.md`를 따른다.

조회 오류가 계속되면 라인·설비 ID·발생 시각·실시간 숫자의 변화·오류 문구를 운영 담당자에게 전달한다. 데모 실행 정체는 `00.rag/user/demo-troubleshooting.md`에서 다룬다.

## 배치가 비거나 보기 전환·편집 단추가 없을 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 권한·라인·전환 L81~112 L257~306
원천: nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 ; 조회 활성 조건 L16~23
원천: nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 ; 기본 배치 L18~33
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 L150~158 L213~239
```

상태는 보이는데 저장된 배치가 비거나 「3D」·「구조물 편집」이 없으면 상태 조회와 배치 조회·편집 권한을 나누어 확인한다.

저장소 기본 권한 데이터에서 상태 조회는 LIVE_MAP_READ, 라인 조회는 LINE_READ를 요구한다. 배치 조회는 LIVE_MAP_LAYOUT_READ·LIVE_MAP_LAYOUT_WRITE 중 하나, 편집은 LIVE_MAP_LAYOUT_WRITE를 요구한다.

라인을 찾지 못하면 배치 조회가 실행되지 않고, 공정 보드는 배치 응답이 없으면 기본 배치를 사용한다. 빈 배치만으로 저장 자료가 없어졌다고 판단하지 않는다. 운영 관리자에게 선택 라인·사라진 단추·조회 또는 저장 중 거부된 작업을 알려 권한과 조회 결과 확인을 요청한다.

## 3D 영역에만 화면 표시 실패가 나올 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 조회 오류·3D 경계 L240~245 L321~333
원천: nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; 오류 경계·새로고침 L14~55
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 표시 실패 L477~479
```

3D 영역의 「화면을 표시하지 못했습니다.」와 페이지 상단의 상태 조회 오류는 구별한다. 3D는 별도 오류 영역으로 감싸져 있어 3D 표시 실패가 상단 상태 조회 실패를 뜻하지는 않는다.

3D 오류 영역의 「새로고침」으로 페이지를 다시 읽은 뒤 「공정 흐름」과 「3D」에서 각각 표시되는지 비교한다. 안내만으로 브라우저 문제나 배포 문제 중 하나를 확정하지 않는다.

재발하면 선택 라인·발생 시각·직전 조작·3D에서만 실패하는지·새로고침 전후 결과를 운영 담당자에게 전달한다. 공통 복구 안내는 `00.rag/user/login-permission-and-screen-troubleshooting.md`를 따른다.

## 저장을 누를 수 없는 것과 요청 실패를 구별하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 저장 활성·오류 L547~565
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 저장 처리·단추·결과 L1015~1044 L1197~1209 L1438~1441
원천: nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 ; 초안 폐기·저장 L23~28 L44~58
원천: nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; putLayout L67~79
```

공정 보드의 「저장」이 비활성이면 변경 표시 점과 저장 중 표시를 확인한다. 변경이 없거나 요청 중이면 누를 수 없다. 3D의 「저장」도 배치 저장·객체 생성 요청 중에는 비활성이다.

누른 뒤 실패한 경우에는 공정 보드의 「저장」 옆과 3D 오른쪽 아래 중 어느 곳에 문구가 나왔는지 기록한다. 저장 조작과 성공 결과는 `00.rag/user/live-map-layout-editing.md`를 따른다.

공정 보드에서 라인을 바꾸면 미저장 초안이 사라지므로, 실패 문구를 먼저 기록한다. 계속 실패하면 보기 종류·선택 라인·대상·시각·문구를 운영 담당자에게 전달한다.

## 공정 그룹만 바꿨는데 설비·객체 오류로 저장이 막힐 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 ; 기존 3D 배치 동반 저장 L44~58
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 저장 오류 전달 L410~416
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; putLayout L57~66
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd ; 전체 배치 검증 L63~116
```

공정 보드 저장은 공정 그룹뿐 아니라 기존 3D 배치도 함께 보내고 서버는 각 배치를 검사한다. 그룹 이동만 했어도 기존 배치의 객체·설비가 없어졌거나 설비가 다른 라인에 속하면 저장이 거부될 수 있다.

「저장」 옆 문구가 객체 부재·설비 부재·라인 소속을 가리키면 그룹 위치를 반복 변경하지 말고, 선택 라인과 문구에 나온 대상을 대조한다.

오류가 가리키는 배치를 화면에서 확인할 수 없으면 선택 라인·그룹 작업·오류의 대상 식별자·발생 시각을 운영 담당자에게 전달한다. 기존 3D 배치 검증도 함께 요청한다.

## 3D 저장에서 설비 연결 오류와 형식 오류를 가르기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 바인딩·저장·선택 목록 L964~968 L1015~1044 L1391~1411
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd ; validatePlacement·requireLineKey L76~116
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapSchemaSupport.java @ 985d17c604dd ; requireSchemaVersion L23~30
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 오류 문구 보존 L19~40 L67~79
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 바인딩 문구 L1015~1015 L1044~1044
```

3D 저장 실패 문구가 설비 연결 필수·라인 소속을 가리키면 대상 설비 배치를 고르고 「실설비 바인딩」을 확인한다. 「— 설비 —」로 비었다면 현재 라인의 설비를 고른다.

객체·설비가 존재하지 않는다는 문구, 구조물에 설비가 연결됐다는 문구, 스키마 버전 문구는 별도 서버 검사 결과다. 형식 오류를 고치는 입력 칸은 배치 화면에 없으므로 오류 문구와 대상을 운영 담당자에게 넘긴다.

권한 거부는 배치 내용 변경으로 해결할 조건이 아니므로 운영 관리자에게 편집 권한을 확인한다. 조치 뒤 저장 결과를 확인하는 절차는 `00.rag/user/live-map-layout-editing.md`를 따른다.

## 구조물이 목록에 없거나 불러오기가 중단될 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 조회·형식 검사·목록 L62~67 L137~147 L176~180 L202~241
원천: nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 ; 객체 조회 L29~35
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·형식 문구 L1020~1025
```

「구조물 편집」의 「저장된 사용자 객체 없음」은 저장 객체 목록이 비었을 때 나오는 표시다. 객체 조회 오류 전용 안내가 따로 없으므로 이전에 있던 객체가 모두 사라졌다면 새 객체를 만들기 전에 운영 담당자에게 목록 조회 확인을 요청한다.

점선 기성 항목만 사라졌다면 같은 명칭의 저장 객체가 있는지 살핀다. 저장 객체가 같은 명칭의 기성 항목을 대신한다.

객체를 눌렀을 때 3D(v2) 편집 불가 안내가 나오면 불러오기가 중단된 경우다. 새 구조물 작성 경로는 `00.rag/user/live-map-layout-editing.md`를 따르고, 기존 객체 보존이 필요하면 객체 명칭·안내 문구·발생 시각을 운영 담당자에게 전달한다.

## 구조물 저장이 입력 안내에서 멈추거나 서버에서 거부될 때
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 결과·표시 L83~89 L150~173 L289~300 L334~338
원천: nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; 생성·수정 L93~119
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapObjectService.java @ 985d17c604dd ; 생성·수정·이름 검사 L32~71
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapSchemaSupport.java @ 985d17c604dd ; requireSchemaVersion L23~30
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력·저장 안내 L1038~1042
```

이름이나 도형을 요구하는 입력 안내이면 요청 전 검사에서 멈춘 경우다. 「이름」과 「도형」 수를 먼저 확인하고 `00.rag/user/live-map-layout-editing.md`의 입력 조건대로 고친다.

입력을 갖췄는데 실패 문구가 나오면 폼 아래 문구가 권한 거부인지, 대상 객체 부재인지, 형식 오류인지 구별해 운영 담당자에게 전달한다. 권한 거부는 운영 관리자에게 편집 권한 확인을 요청한다.

저장 뒤 폼이 비워졌다면 「객체」 목록에서 저장 대상을 다시 골라 확인한다. 저장 성공 뒤에는 폼과 상태 문구가 함께 비워진다. 목록에도 없으면 객체 명칭·저장 또는 수정 조작·시각·화면 결과를 운영 담당자에게 전달한다.

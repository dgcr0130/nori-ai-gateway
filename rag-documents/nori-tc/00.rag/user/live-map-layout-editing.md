# Live-Map 배치·구조물 편집
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/live-map-status.md
```

## 배치와 구조물을 편집할 계정 준비하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; canEdit·보기 전환 L81~86 L289~306
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; isEditing·편집 단추 L217~217 L880~889
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 배치 API 기본 권한 L213~230
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 기본 그룹 연결 L33~39
```

Live-Map의 배치·객체 변경에는 LIVE_MAP_LAYOUT_WRITE가 필요하다. 조회만 가능한 계정에는 편집 단추가 나타나지 않는다.

저장소 기본 권한 데이터는 편집 권한을 ADMIN 그룹에 연결한다. 저장·객체 생성·수정·삭제 API도 이 권한으로 검사한다. 현장 계정의 권한이나 권한 거부 결과는 운영 관리자에게 확인한다.

## 공정 그룹을 옮기거나 크기 바꾸기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 선택 라인·그룹 변경 L105~112 L347~374
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 그룹 이동·크기 조절 L259~327 L742~746 L854~870 L880~887
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 ; moveLiveMapFlowGroup·resizeLiveMapFlowGroup L262~313
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 배치 편집 L393~393
```

1. 편집 권한자가 상단바에서 편집할 라인을 고르고 「공정 흐름」을 연다.
2. 보드 왼쪽 세로 막대의 연필 단추 「배치 편집」을 켠다.
3. 그룹 카드 머리글을 끌어 위치를 바꾼다. 오른쪽 아래 손잡이를 끌어 크기를 바꾼다.

이동과 크기는 보드 격자에 맞춰 조정된다. 「미배정」 카드도 이동·크기 조절이 가능하다. 변경을 확정하려면 보드 머리글의 「저장」을 사용한다.

## 공정 그룹을 추가하고 표시 속성 정하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; onCreateGroup L384~390
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 그룹 추가·편집 L522~534 L768~783 L913~923
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapFlowGroupDialog.tsx @ 9fe69a75dcd3 ; 그룹 입력·확인 L33~112
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 문구 L369~378
```

1. 「배치 편집」을 켠 공정 보드에서 「그룹 추가」를 누른다. 「새 그룹」 카드가 생긴다.
2. 그룹 카드의 연필 단추를 눌러 「그룹 편집」을 연다.
3. 「그룹 이름」·「부제」·「색상」을 정하고 「확인」을 누른다. 색상에서 「기본」을 고르면 기본 색을 쓴다.

「그룹 이름」이 공백뿐이면 「확인」을 누를 수 없다. 「미배정」 카드에는 표시 속성 편집 단추가 없다. 그룹 편집 뒤 보드의 「저장」으로 확정한다.

## 설비를 공정 그룹에 배정하거나 그룹 지우기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 다중 선택·배정 L415~490 L824~848
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 그룹 삭제 L768~795 L952~966
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-flow.model.ts @ 9fe69a75dcd3 ; assignLiveMapFlowEquipment·removeLiveMapFlowGroup L360~403
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 삭제 확인 L371~372
```

「배치 편집」 중 설비 항목을 다른 그룹 카드로 끌어 놓으면 소속 그룹이 바뀐다. 여러 설비는 Ctrl을 누른 채 눌러 선택하고, 선택한 설비 하나를 끌어 함께 옮긴다. 「미배정」으로 옮기면 그룹 배정이 해제된다.

그룹 카드의 휴지통 단추를 누르고 삭제 확인을 확정하면 그룹이 지워진다. 소속 설비는 「미배정」으로 이동하고 그 그룹에 연결된 화살표도 지워진다. 「미배정」 그룹은 삭제할 수 없다. 보드의 「저장」으로 변경을 확정한다.

## 공정 그룹 사이 화살표를 추가·수정·삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 추가 조건·화살표 선택 L535~546 L688~699 L925~949
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapFlowArrowDialog.tsx @ 9fe69a75dcd3 ; 끝점·스타일·확인 L26~32 L54~67 L101~229
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화살표 문구 L379~389
```

1. 「배치 편집」 중 「화살표 추가」를 누른다. 그룹 카드가 둘 이상 있어야 누를 수 있다.
2. 서로 다른 「시작 그룹」·「도착 그룹」을 고른다. 같은 그룹이면 「확인」이 비활성이다.
3. 「색상」·「굵기」·「선 종류」를 고르고 「확인」을 누른다. 굵기는 네 가지 선 견본, 선 종류는 「실선」·「파선」·「점선」에서 고른다.

기존 화살표를 누르면 「화살표 편집」이 열린다. 속성을 고치거나 「화살표 삭제」를 누른 뒤 보드의 「저장」으로 확정한다.

## 공정 보드 저장 결과 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapBoard.tsx @ 9fe69a75dcd3 ; 저장·오류 표시 L547~565
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 저장 오류 전달 L410~416
원천: nori-tc-ui-front/src/features/live-map/hooks/useLiveMapFlow.ts @ 9fe69a75dcd3 ; 초안·저장 L18~61
원천: nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; putLayout L67~79
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; putLayout L57~66
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 공정 보드 저장 L1802~1802 L1822~1826
```

「배치 편집」 중 보드 머리글 오른쪽 「저장」을 누른다. 변경이 있으면 단추에 작은 점이 붙는다. 변경이 없거나 저장 중이면 누를 수 없다.

저장 성공 시 점이 사라지고 「저장」이 비활성으로 돌아간다. 별도 성공 문구는 나오지 않는다. 공정 보드 저장은 기존 3D 배치를 유지한다.

저장 실패 문구는 「저장」 옆에 표시된다. 문구를 확인하고 다시 저장한다. 저장하기 전에 라인을 바꾸거나 화면을 떠나면 편집 내용을 잃는다.

## 3D에서 설비·구조물을 선택하고 이동하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 선택·키보드 이동·드래그 L719~725 L744~839
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 설비 이름표·편집 시작 L1094~1112 L1164~1182
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 구조물 선택·화면 이동 L312~327 L411~412
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 편집·조작 문구 L1000~1001 L1018~1018
```

1. 편집 권한자가 대상 라인의 「3D」 보기에서 오른쪽 위 「편집」을 누른다.
2. 설비 이름표나 구조물을 누른다. Shift를 누른 채 누르면 여러 배치를 선택하거나 선택에서 뺀다.
3. 선택한 배치를 끌어 격자에 맞춰 옮긴다. 여러 개를 선택하면 함께 이동한다.
4. 화살표 키로 미세 이동한다. Shift와 화살표 키를 함께 누르면 더 크게 움직인다.

입력 칸이나 선택 목록에 초점이 있으면 화살표 키는 배치를 움직이지 않는다. 화면 자체는 오른쪽 단추로 끌어 이동한다.

## 3D 선택 배치의 회전·층·템플릿·설비 연결 바꾸기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 층·후보·선택 기준 L97~97 L547~550 L688~717
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 선택 변경 L950~978 L1334~1415
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 선택 도구 문구 L1010~1017 L1044~1044
```

3D 편집 중 배치를 선택하면 아래 선택 막대가 열린다. 「반시계 15° 회전」·「시계 15° 회전」과 층 번호 「1」·「2」는 선택한 배치 전체에 적용된다. 「삭제」도 선택한 배치 전체를 맵에서 뺀다.

「객체 템플릿 교체」는 같은 종류의 객체 중에서 고른다. 「실설비 바인딩」은 설비 배치에서만 나타나며 현재 라인의 설비를 고른다. 두 목록의 변경은 마지막으로 선택한 배치 하나에 적용된다. 설비 배치를 저장하려면 실제 설비 연결을 유지한다.

## 3D에 구조물이나 미배치 설비 추가하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 미배치 후보 L547~563
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 구조물·설비 추가 L845~862 L888~918 L1251~1318
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 추가 문구 L1043~1045 L1051~1055
```

3D 편집 도구의 「— 객체 —」에서 구조물을 고르고 「추가」를 누른다. 구조물을 고르지 않으면 「추가할 객체를 선택하세요.」가 나온다.

설비는 「— 미배치 설비 —」에서 고르고 「설비 추가」를 누른다. 선택하지 않으면 「추가할 미배치 설비를 선택하세요.」가 나온다. 현재 라인에 이미 배치한 설비는 후보에서 빠지고, 남은 설비가 없으면 「모든 설비 배치됨」으로 표시된다.

새 배치는 현재 보고 있는 층에 놓이며 「전체」 보기에서는 1층에 놓인다. 추가한 뒤 위치를 조정하고 저장한다.

## 빈 층에 기본 구조 채우기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 구조물 존재 검사 L587~595
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 기본 구조 추가 L864~886 L1280~1290
원천: nori-tc-ui-front/tests/unit/live-map-3d-model.test.ts @ 9fe69a75dcd3 ; 기본 구조 구성 기대값 L60~79
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 기본 구조 문구 L1048~1050 L1056~1057
```

3D 편집 중 「기본 구조 채우기」를 누르면 바닥·외벽·기둥·파티션을 현재 층에 함께 추가한다. 「전체」 보기에서는 1층에 추가한다. 완료 문구에 추가 개수가 표시되며, 각 배치는 끌기·회전·삭제로 조정할 수 있다.

대상 층에 구조물이 하나라도 있으면 단추가 비활성이고, 마우스를 올리면 이미 기본 구조가 있다는 안내가 나온다. 직접 추가한 구조물이 있어도 같은 조건이 적용된다. 구조 배치를 확정하려면 「저장」을 누른다.

## gateway_app 기준으로 설비를 다시 배치하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 층 분류·조회 조건 L565~585
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 자동 배치 L920~948 L1319~1330
원천: nori-tc-ui-front/tests/unit/live-map-3d-model.test.ts @ 9fe69a75dcd3 ; 층 배치·미분류 설비 기대값 L81~114
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 자동 배치 문구 L1058~1060
```

3D 편집 중 「gateway_app 자동 배치」를 누르면 현재 라인의 설비 전체를 담당 gateway_app 기준 층과 열에 다시 놓는다. 기존 설비 배치도 교체하며 구조물 배치는 유지한다. 층을 분류하지 못한 설비는 1층에 놓는다.

배치한 설비 수가 안내 문구에 나온다. 위치를 확인해 끌기로 조정하고 「저장」을 누른다. 실행 직전 배치로 돌아가려면 저장 전에 「되돌리기」를 누른다.

## 자동 배치 단추가 비활성일 때 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 조회·활성 조건 L433~436 L584~585 L1323~1326
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 설비·담당 정보 조회 L149~184
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 조회 기본 권한 L123~139
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비 API 권한 L764~767
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174
```

「gateway_app 자동 배치」는 현재 라인에 조회된 설비가 있고 설비 목록·담당 정보 조회가 모두 성공해야 활성화된다.

저장소 기본 권한 데이터 기준으로 두 추가 조회에는 EQP_READ·EQP_WRITE 중 하나가 필요하다. 편집 권한만으로 두 조회 권한이 충족되지는 않는다. 단추가 계속 비활성이면 선택 라인·발생 시각·단추 상태를 운영 담당자에게 전달하고 조회 권한도 확인한다.

## 3D 변경을 되돌리고 저장·종료하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 실행 취소 L465~523
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 저장·편집 종료 L1015~1044 L1170~1208
원천: nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 ; 저장 응답 반영 L38~47
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 저장·실행 취소 문구 L996~1003 L1046~1046
```

3D 편집 중 「되돌리기」 또는 Ctrl+Z로 직전 편집을 취소한다. 입력 칸·선택 목록에 초점이 있을 때는 배치의 Ctrl+Z가 작동하지 않는다. 저장 뒤에는 이전 편집으로 되돌릴 수 없다.

오른쪽 위 「저장」을 누르고 「레이아웃이 저장되었습니다.」를 확인한다. 저장 중에는 단추가 비활성이다. 3D 저장은 기존 공정 그룹 배치를 유지한다.

「편집 종료」는 편집 도구를 닫으며 자동 저장하지 않는다. 저장을 마친 뒤 편집을 종료하고 다른 보기나 화면으로 이동한다.

## 배치 저장이 거부됐을 때 확인할 조건
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapLayoutService.java @ 985d17c604dd ; 저장 검증 L63~116
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 바인딩·저장 오류 L964~968 L1041~1044 L1391~1411
원천: nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; 오류 정규화 L74~88
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 오류 문구 L19~40 L49~79
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 설비 선택 L1044~1044
```

설비 배치는 실제 설비 연결이 필수이며, 설비가 저장 대상 라인에 속해야 한다. 「실설비 바인딩」을 「— 설비 —」로 비워 둔 경우 현재 라인의 설비를 다시 고른다. 구조물에는 설비를 연결할 수 없다.

저장 시 존재하지 않는 객체·설비, 다른 라인의 설비는 서버가 거부한다. 화면에서 직접 고칠 수 없으면 선택 라인·설비 ID·대상 객체·발생 시각·표시된 오류 문구를 운영 담당자에게 전달한다. 권한 거부이면 입력 변경 대신 편집 권한을 확인한다.

## 구조물 편집의 출발 객체 고르기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 구조물 편집 진입 L289~319
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 초안·불러오기 L83~103 L137~180
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 객체 목록 L188~242
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 객체 목록 문구 L1020~1028
```

편집 권한자가 화면 오른쪽 위 「구조물 편집」을 연다. 왼쪽 「객체」에서 점선 테두리의 「기성 · STRUCTURE」 항목을 누르면 새 객체의 출발 모양을 불러온다. 같은 명칭의 저장 객체가 있으면 기성 항목 대신 저장 객체가 표시된다.

실선 테두리의 저장 객체를 누르면 그 객체를 수정한다. 빈 모양에서 시작하려면 「신규」를 누른다. 「종류」는 STRUCTURE로 표시되며 선택 항목이 없다. 이 화면에서 객체 모양을 저장한 뒤 3D 편집에서 맵에 배치한다.

## 구조물의 도형 크기·위치·색상 정하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 도형 추가·높이 보정 L91~135
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 도형 단추·입력·미리보기 L281~328 L341~357
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DObject.tsx @ 9fe69a75dcd3 ; 크기·색상 렌더 L11~20
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 격자 한 칸 L368~368
원천: nori-tc-ui-front/src/features/live-map/lib/live-map-3d.model.ts @ 9fe69a75dcd3 ; 기성 구조물·표시 색 L104~140
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 도형 문구 L1029~1037
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 구조물 치수 단위 L1953~1956
```

1. 「이름」을 입력하고 「종류」 오른쪽 그림 단추에 마우스를 올려 「박스 추가」·「실린더 추가」를 구별해 누른다.
2. 박스 줄의 W·H·D로 가로·높이·깊이를, X·Y·Z로 위치를 정한다. 단위는 3D 격자 한 칸이다. 박스·실린더의 H를 바꾸면 바닥 위치를 유지하며 높이가 바뀐다.
3. 「색상」을 고르고 오른쪽 미리보기로 모양을 확인한다. 도형 줄의 휴지통은 그 도형만 제거한다.

「도형」 옆 숫자로 현재 도형 수를 확인한다. 저장하려면 도형이 하나 이상 필요하다.

실린더는 W가 지름, H가 높이이며 D 변경은 모양에 반영되지 않는다. 객체 명칭이 「벽」·「파티션」·「바닥」·「기둥」이면 선택한 도형 색 대신 정해진 표시 색을 사용한다.

## 만든 구조물을 저장하거나 기존 객체 수정하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 입력 검사·저장·초기화 L83~89 L150~173 L289~300
원천: nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 ; 객체 목록 갱신 L50~69
원천: nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; 생성·수정 L93~119
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/LiveMapObjectService.java @ 985d17c604dd ; 이름·생성·수정 L32~71
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 이름 길이·중복 제한 L1147~1159
```

새 구조물은 「저장」, 목록에서 불러온 기존 객체는 「수정」을 누른다. 「이름」은 공백만 입력할 수 없고 앞뒤 공백은 제거된다. 저장소의 PostgreSQL 기본 스키마는 객체 명칭을 100자 이내·중복 없이 저장하도록 제한한다.

요청이 성공하면 입력 폼과 도형이 비워지고 객체 목록을 다시 조회한다. 목록에서 저장한 객체를 다시 골라 내용을 확인한다. 구조물 저장은 재사용할 모양을 저장하는 작업이며, 라인에 놓을 위치는 3D의 배치 저장에서 정한다.

## 구조물 입력·저장이 실패했을 때 처리하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 형식·입력 검사·오류 표시 L137~173 L334~338
원천: nori-tc-ui-front/src/features/live-map/components/LiveMap3DScene.tsx @ 9fe69a75dcd3 ; 배치 저장 오류 L1041~1044 L1438~1441
원천: nori-tc-ui-front/src/features/live-map/api/layout.api.ts @ 9fe69a75dcd3 ; 요청 실패 기본 문구 L52~52 L93~119
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; resolveErrorMessage L11~12
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 오류·옛 형식 L1023~1023 L1038~1040
```

「객체 이름을 입력하세요.」가 나오면 「이름」을 채운다. 「도형을 하나 이상 추가하세요.」가 나오면 도형 단추로 도형을 만든다. 옛 형식 객체를 고를 때 3D(v2) 편집 불가 안내가 나오면 「신규」로 새 객체를 만든다.

요청 실패는 서버 오류 문구가 있으면 그 문구를 표시한다. 서버 문구가 없으면 `live-map layout request failed`가 나올 수 있다. 입력 폼 아래나 3D 오른쪽 아래의 문구를 확인하고 같은 「저장」·「수정」을 다시 누른다. 계속 실패하면 대상·시각·문구를 운영 담당자에게 전달한다.

## 저장된 구조물 객체를 삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapObjectEditor.tsx @ 9fe69a75dcd3 ; 객체 삭제 L225~253
원천: nori-tc-ui-front/src/features/live-map/hooks/useLayout.ts @ 9fe69a75dcd3 ; 삭제 후 목록 갱신 L71~79
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapLayoutController.java @ 985d17c604dd ; deleteObject L97~102
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/LiveMapObjectJdbcStorePort.java @ 985d17c604dd ; delete L78~85
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 객체 참조 삭제 규칙 L1193~1194
```

「구조물 편집」의 저장 객체 항목 오른쪽 휴지통을 누르면 확인 대화상자 없이 삭제 요청을 보낸다. 성공하면 객체 목록을 다시 조회해 항목이 사라진다.

저장소의 PostgreSQL 기본 스키마에서는 객체를 삭제하면 그 객체를 사용하는 맵 배치도 함께 삭제된다. 맵에서 한 배치만 빼려면 3D 편집의 「삭제」를 사용한다. 객체가 목록에 계속 남으면 객체 명칭·발생 시각·화면 결과를 운영 담당자에게 전달한다.

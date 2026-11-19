# 처음 쓰는 사람 안내
```rag-meta
유형: 개념
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
```

## 독자별 업무를 시작하기 전 공통 안내
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 1.1 목적과 대상 독자 L5~18
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveVisibleAppNavigationGroups L146~153
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~185
```

설비 담당·모델 담당·운영 관리자는 먼저 `00.rag/user/login-and-screen-basics.md`에서 로그인과 메뉴·표 조작을 확인한다.

시작 화면의 진입 권한과 실제 작업 권한을 구별한다. 조회·변경에 추가로 필요한 권한은 각 업무 문서에서 저장소 기본 권한 데이터 기준으로 확인한다.

## 설비 담당이 등록·설정과 명령을 시작하는 곳
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 설비 담당 L12~16
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 설비 메뉴 L78~89
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; EQP_READ_PERMISSION L12~16
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 진입 L93~95
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 설비 메뉴 문구 L1092~1099
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·설정 메뉴 L164~191
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 목록·명령 요청 L145~162 L650~661
```

설비 담당은 EQP_READ가 필요한 「설비」 메뉴에서 대상 설비의 등록과 설정을 먼저 살펴본다.

1. 설비를 새로 등록하거나 설정을 고칠 때는 `00.rag/user/equipment-registration-and-settings.md`를 따른다.
2. 등록한 설비의 시작·종료·재연결이나 PLC 폴링이 필요하면 `00.rag/user/equipment-commands-and-polling.md`로 이어간다.

## 모델 담당이 모델과 Flow 작업을 시작하는 곳
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 모델 담당 L12~16
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 모델 메뉴 L90~113
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; MODEL_READ_PERMISSION L18~22
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 통신 방식별 모델 진입 L97~101
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 메뉴 문구 L1093~1101
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 통신 방식별 목록·버전 열기 L193~209 L639~660
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; Flow 조회 연결 L199~209
```

모델 담당은 「모델」에서 통신 방식에 맞는 「SECS」·「Socket」·「PLC」를 고른다. 세 화면의 진입 권한은 MODEL_READ이다.

1. 모델 등록과 branch 준비는 `00.rag/user/model-registration-and-branching.md`에서 시작한다.
2. 작업할 모델·버전을 정한 뒤 상세와 Flow 편집은 `00.rag/user/model-details-and-flow-editing.md`로 이어간다.

## 운영 관리자가 계정과 권한 작업을 시작하는 곳
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 운영 관리자 L12~18
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 시스템 메뉴 L126~138
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; USER_READ_PERMISSION L7~10
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 사용자 화면 진입 L107~109
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관리 카드·메뉴 문구 L89~95 L1095~1103
원천: nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 계정·그룹 작업 영역 L12~18 L25~54
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 그룹을 통한 활성 권한 조회 L82~118
```

운영 관리자는 USER_READ가 필요한 「시스템」의 「사용자·그룹」에서 계정과 소속 그룹·권한을 먼저 점검한다.

「사용자 계정」과 「권한 그룹」에서 할 일과 작업별 권한은 `00.rag/user/user-group-and-permission-management.md`를 따른다.

## 모델 담당과 운영 관리자가 배포로 이어가는 곳
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 배포를 쓰는 독자 L12~16
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 배포 메뉴 L114~125
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; DEPLOY_READ_PERMISSION L24~28
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 배포 진입 L103~105
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 적용·배포 문구 L1094~1102
원천: nori-tc-ui-front/src/features/deploy/components/DeployPage.tsx @ 9fe69a75dcd3 ; 등록·배포·이력 작업 영역 L13~52
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 배포 조회·작업 권한 L51~113
```

모델 담당이 변경을 배포하거나 운영 관리자가 배포 진행·이력을 확인할 때는 「적용」의 「배포」로 이어간다. 화면 진입에는 DEPLOY_READ가 필요하다.

배포 등록부터 실행·이력·재실행까지는 `00.rag/user/deployment-workflow.md`를 따른다. 기본 권한 데이터에서 생성·실행·삭제는 조회와 별도 권한으로 연결되어 있다.

## 설비 담당과 운영 관리자가 상태·실패를 살펴보는 곳
```rag-meta
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 설비 담당·운영 관리자 L12~16
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 모니터링 메뉴 L60~77
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; LIVE_MAP_READ·DLQ_READ L1~5 L30~34
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 상태·실패 화면 진입 L85~91
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모니터링 메뉴 문구 L1091~1097
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 상태 조회 화면 L65~73 L103~109
원천: nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 ; 실패 범주 연결 L19~35
```

- 설비 담당은 상태를 볼 때 LIVE_MAP_READ가 필요한 「모니터링」의 「Live-Map」에서 시작한다. 상태 읽는 방법은 `00.rag/user/live-map-status.md`를 따른다.
- 설비 담당과 운영 관리자는 처리 실패·격리·복구 작업을 찾을 때 「모니터링」의 「처리실패」로 이어간다. 진입 권한은 DLQ_READ이며 `00.rag/user/dlq-quarantine-and-repair-task.md`에서 조회와 인계 방법을 확인한다.

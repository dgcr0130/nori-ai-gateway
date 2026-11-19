# 처음 쓰는 사람 안내 작성 기록

## 대상

- objectKey: `00.rag/user/getting-started.md`
- 검색 원문: `rag-documents/nori-tc/00.rag/user/getting-started.md`
- 구획·열람 권한: user, `[AI_DOC_READ]`
- 유형: 개념
- 검색 원문 SHA-256: `b4d8d8f01cf913c83390a0402a60dbf72fa1088dd595264c76e666074ed94c94`
- 최종 원문: 95줄, H1 1개, H2 6개, 자식 14개, 7,449바이트
- 확인일: 2026-11-18, 한국 시간
- 소유 범위: 설비 담당·모델 담당·운영 관리자별 첫 작업과 시작 화면, 후속 업무 문서 선택. 상세 조작·입력값·상태 설명은 기존 소유 문서에 둔다.
- 작성은 이 세션에서 직접 수행했다. Agent 위임·업로드·서버 접속·제품 변경·git add/commit/push는 수행하지 않았다.

## 원천 판본

| 저장소 | 확인한 HEAD 12자리 | 작업 트리 확인 결과 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 아래 코드·문구·시험 파일에 대해 `git status --short -- <파일 경로들>`을 실행했고 변경 항목 출력이 없었다. |
| nori-tc-ui-backend | 985d17c604dd | 아래 보안·권한·컨트롤러 파일에 대해 같은 방식으로 확인했고 변경 항목 출력이 없었다. |
| nori-tc-exec | f1d5e0a8e727 | 사용자 매뉴얼·권한 catalog·시드 파일에 대해 같은 방식으로 확인했고 변경 항목 출력이 없었다. |
| nori-ai-gateway | 70cf6cd84db6 | 작성 규칙·목록은 변경 항목 출력이 없었다. 기존 `rag-documents/`는 시작 때부터 untracked였으며 기존 문서는 수정하지 않았다. |

형제 저장소의 첫 Git 조회에서 소유자 차이로 거부되어, 후속 조회에는 해당 저장소 절대경로의 `-c safe.directory=...`를 명령 단위로 사용했다. 전역 설정은 수정하지 않았다. Git은 사용자 전역 ignore 파일 접근 경고를 출력했으나 대상 경로의 status 명령은 exit 0이었다. 판본 근거는 HEAD와 각 원천 경로의 변경 상태이며 파일 시각을 근거로 쓰지 않았다.

확인한 원천 경로는 다음과 같다. 모두 위 저장소 HEAD 기준이며 작업 트리 수정 파일이 아니므로 검색 원문 메타에는 commit을 썼다.

- nori-tc-ui-front: `src/shared/lib/app-navigation.ts`, `src/shared/lib/permission.ts`, `src/app/Router.tsx`, `src/shared/components/AppLnbSidebar.tsx`, `src/i18n/locales/ko.json`.
- nori-tc-ui-front: `src/features/eqp/components/EqpPage.tsx`, `src/features/eqp/components/EqpSidebar.tsx`, `src/features/eqp/api/eqp.api.ts`.
- nori-tc-ui-front: `src/features/model/components/ModelPage.tsx`, `src/features/model/components/ModelDetailPanel.tsx`, `src/features/model/api/model.api.ts`, `src/features/model/api/model-flow.api.ts`.
- nori-tc-ui-front: `src/features/user-management/components/UserManagementPage.tsx`, `src/features/user/api/user.api.ts`, `src/features/deploy/components/DeployPage.tsx`, `src/features/deploy/components/DeployRegisterPanel.tsx`, `src/features/deploy/api/deploy.api.ts`.
- nori-tc-ui-front: `src/features/dlq/components/DlqPage.tsx`, `src/features/dlq/api/dlq.api.ts`, `src/features/live-map/components/LiveMapPage.tsx`, `src/features/live-map/api/live-map.api.ts`.
- nori-tc-ui-front: `tests/unit/app-navigation-visibility.test.ts`, `tests/unit/eqp-model-deploy-permission-gating.test.ts`의 메뉴 필터·권한 상수·라우트 가드 기대값을 읽었다. 시험을 실행한 것은 아니다.
- nori-tc-ui-backend: `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java`, 같은 디렉터리의 `UiApiPermissionCache.java`.
- nori-tc-ui-backend: `ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java`.
- nori-tc-ui-backend: `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/`의 `EqpController.java`, `ModelQueryController.java`, `ModelBranchController.java`, `ModelMutationController.java`, `ModelFlowController.java`, `DeployController.java`, `UserController.java`, `DlqController.java`, `LiveMapController.java`.
- nori-tc-exec: `docs/completion-report/07-user-manual.md` §1.1 전체, `docs/scripts/manual-permission-catalog.json`, `docs/db_table/sample_data/postgres_insert_sample_data.sql`의 관련 권한 연결.

작성 규칙과 문서 목록은 전체를 읽었다. SHA-256은 각각 `273f44daeb41ffe7a63fbd2231c8b73f58464af19a6a580523e82ca5c578e13b`, `45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b`이다.

겹치는 기존 문서는 시작 화면·권한·업무 진입 절을 읽어 본문 소유권을 대조했다. 연결 문서의 실제 존재와 현재 SHA-256은 다음과 같다.

| objectKey | SHA-256 |
|---|---|
| 00.rag/user/login-and-screen-basics.md | d2797a9c03f40c92310691305fd266d1cfd5c8dbadc887ee430046ccb07d8beb |
| 00.rag/user/equipment-registration-and-settings.md | a38e750151ef61a8f1fcea26d711944c1316daddbd65bc26b6d2d71ab7e46f09 |
| 00.rag/user/equipment-commands-and-polling.md | 0db9769076d4a29029907142e1e6d91fdee79b4e9a7a0282af73655b91be5a28 |
| 00.rag/user/model-registration-and-branching.md | 2e615d27fcc0a0844f8a8276df4ab5966a2bbf6f5a7ac281c58d40cb355fb5c9 |
| 00.rag/user/model-details-and-flow-editing.md | 32dcb7ddbfbc7673815d04ab20b7753319c52d8af8f1a249c107df360745c173 |
| 00.rag/user/user-group-and-permission-management.md | d6076faa0617c83c6cca5785573af27b9270f6e3ca4120c719d48f718e7c5cd0 |
| 00.rag/user/deployment-workflow.md | a2b18e6614520bd24689f10899d457471a3e8f2bf55db31d6f0978021894f804 |
| 00.rag/user/live-map-status.md | f64f5721df75316ff1cf6c3e1677009c9d2a6fda013d4a7c555e73191c33d119 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d |

## 절별 사실 대조

독자별 순서는 이 안내에서 선택한 읽기·작업 진입 순서이다. 제품이 역할명에 따라 화면 순서를 자동 지정한다고 서술하지 않았다. 매뉴얼 §1.1의 세 독자와 모든 주요 화면을 포함했으며, 배포와 처리실패는 여러 독자의 공통 진입점이므로 각각 한 절에서만 소유한다.

### 독자별 업무를 시작하기 전 공통 안내

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 17 | 세 독자 모두 로그인·메뉴·표 공통 안내부터 확인한다. | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L5~18; nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L24~25 | 세 독자가 로그인 계정의 권한 범위에서 화면을 사용한다. 연결 문서의 로그인·메뉴·표 조작 절과 실제 파일 존재를 확인했다. |
| 19 첫 문장 | 화면 진입과 작업 권한을 구별한다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L85~109; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd L142~143; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~185 | 화면 가드 외에 서버가 요청 경로·메서드와 보유 권한을 대조하며 일치 권한이 없으면 거부한다. |
| 19 둘째 문장 | 작업별 추가 권한은 기본 데이터 기준으로 각 업무 문서에서 확인한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L51~158 L240~293; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L764~788 | 조회와 쓰기·실행 권한 연결의 차이를 확인했다. 각 링크의 권한 절을 확인했으며 현장 DB 값으로 서술하지 않았다. |

### 설비 담당이 등록·설정과 명령을 시작하는 곳

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 32 | 설비 담당의 첫 화면과 EQP_READ 진입 조건 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L12~16; nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L78~89; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 L12~16; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1092~1099; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L93~95 | 실제 메뉴 문구는 「설비」, 경로는 /eqp, 화면 진입은 EQP_READ 가드이다. |
| 34 | 설비 등록·설정 소유 문서로 연결 | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 L164~191; nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L145~162; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L98~113 | 설비 선택·수정 메뉴와 목록 조회 처리를 확인했다. 등록·설정 문서의 해당 절과 objectKey를 대조했고 입력 절차는 옮기지 않았다. |
| 35 | 등록 대상의 명령·PLC 폴링 소유 문서로 연결 | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L650~661 L688~701 L730~743 L769~786; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L322~332 L348~358 L377~387 L405~420 | 시작·종료·재연결·PLC 폴링 요청이 해당 서버 처리로 이어진다. 실제 실행 조건·결과 판정은 명령 문서 소유이다. |

### 모델 담당이 모델과 Flow 작업을 시작하는 곳

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 48 첫 문장 | 모델 아래 통신 방식별 시작 메뉴 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L12~16; nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L90~113; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1093~1101; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L97~101; nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L193~209 | SECS·Socket·PLC 메뉴가 각 scope의 ModelPage로 이어지고 통신 방식으로 목록을 나눈다. |
| 48 둘째 문장 | 세 모델 화면 진입에 MODEL_READ 필요 | nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 L18~22; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L97~101 | 세 라우트가 같은 MODEL_READ 가드 안에 있다. |
| 50 | 모델 등록·branch 소유 문서로 연결 | nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 L386~396 L439~452; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L56~62 L84~92 | root 생성·branch 생성의 호출·처리 연결과 해당 기존 문서의 시작 절을 확인했다. |
| 51 | 모델·버전을 정한 뒤 상세·Flow 문서로 연결 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L639~665; nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 L199~209; nori-tc-ui-front/src/features/model/api/model-flow.api.ts @ 9fe69a75dcd3 L28~63; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelFlowController.java @ 985d17c604dd L123~138 | 선택 모델 버전으로 상세 탭과 Flow 조회가 연결되고 서버가 Flow 수정 요청을 처리한다. 편집 조작은 기존 문서로 안내한다. |

### 운영 관리자가 계정과 권한 작업을 시작하는 곳

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 64 | 운영 관리자 시작 메뉴와 USER_READ 조건 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L12~18; nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L126~138; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 L7~10; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L107~109; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1095~1103 | 「시스템」의 「사용자·그룹」은 /user로 연결되고 USER_READ 가드가 있다. |
| 66 | 사용자 계정·권한 그룹 작업의 소유 문서 | nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 L12~18 L25~54; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L89~95; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd L82~118 | 카드별 사용자·그룹 작업 영역과 그룹을 통한 활성 권한 조회를 확인했다. 계정·그룹·멤버·권한 상세 절차는 기존 문서 소유이다. |

### 모델 담당과 운영 관리자가 배포로 이어가는 곳

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 79 첫 문장 | 두 독자의 배포 작업 시작 위치 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L12~16; nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L114~125; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1094~1102; nori-tc-ui-front/src/features/deploy/components/DeployPage.tsx @ 9fe69a75dcd3 L13~52 | 「적용」 아래 「배포」로 진입하고 등록·배포·이력 작업 영역을 제공한다. |
| 79 둘째 문장 | 배포 화면은 DEPLOY_READ 필요 | nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 L24~28; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L103~105 | /deploy 진입의 권한 코드 값과 가드를 확인했다. |
| 81 첫 문장 | 배포 등록·실행·이력·재실행은 기존 문서 소유 | nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 L554~554 L576~576 L651~651 L740~758; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L135~156 L172~172 L253~253 L285~285 | 생성·실행·재실행 요청과 이력 조회 경로, 대응 서버 메서드 및 기존 문서 절을 대조했다. 상태·조작 절차는 복제하지 않았다. |
| 81 둘째 문장 | 기본 데이터에서 생성·실행·삭제와 조회 권한이 분리됨 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L51~113; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L780~784; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~185 | DEPLOY_READ·WRITE·ACTION·DELETE의 경로·메서드 연결 차이를 확인했다. 현장 권한 데이터가 같다고 주장하지 않았다. |

### 설비 담당과 운영 관리자가 상태·실패를 살펴보는 곳

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 94 첫 문장 | 설비 담당의 상태 조회 시작 메뉴와 권한 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L12~16; nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L60~69; nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 L1~5; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L85~87; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1091~1096 | 「모니터링」의 「Live-Map」은 /live-map이며 LIVE_MAP_READ가 화면 진입 조건이다. |
| 94 둘째 문장 | 상태 읽기는 Live-Map 문서로 연결 | nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 L65~73 L103~109; nori-tc-ui-front/src/features/live-map/api/live-map.api.ts @ 9fe69a75dcd3 L40~50; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LiveMapController.java @ 985d17c604dd L33~52 | 상태 화면이 실제 조회 API와 연결된다. 상태 표시·배치 조회 권한의 상세는 기존 문서로 안내한다. |
| 95 첫 문장 | 설비 담당·운영 관리자의 실패·격리·복구 조회 시작 위치 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L12~16; nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 L70~77; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1091~1097; nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 L19~35 | 메뉴는 「처리실패」이며 Gateway DLQ·Business DLQ·격리 설비·Repair Task 영역을 연결한다. |
| 95 둘째 문장 | DLQ_READ 진입 조건과 조회·인계 문서 | nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 L30~34; nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L89~91; nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L30~46; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L81~88 L129~136 | 화면 진입과 실제 목록 조회 연결을 확인했다. 복구 실행 방법을 추가하지 않고 기존 문서의 조회·운영 담당자 인계로 연결한다. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼 | 코드 | 검색 원문 반영 |
|---|---|---|---|
| DLQ 시작 메뉴 표기 | U §1.1 L14·16은 주요 화면을 DLQ라고 부른다. U §1.3에서는 좌측 메뉴 문구가 「처리실패」라고 별도로 설명한다. | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1091~1097 및 app-navigation.ts L70~77은 「모니터링」 아래 「처리실패」로 연결한다. | 원문 L95에 실제 메뉴 문구를 썼다. 독자 구분이나 동작 자체의 충돌로 확대하지 않았다. |

「모델」 아래의 통신 방식과 「적용」·「시스템」 상위 메뉴는 §1.1의 짧은 화면 목록에 덧붙인 코드 기반 위치 설명이다. 매뉴얼 §1.1이 이 메뉴를 부정하는 것은 아니므로 별도 충돌로 세지 않았다. 독자별 업무에만 열리는 전용 권한이라고 서술하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 계정별 실제 권한·그룹 부여, 현장에 배포된 제품 판본 | 저장소 코드와 기본 권한 데이터는 확인했으나 현장 DB·화면·서비스에는 접속하지 않았다. 본문 적용은 소스 기준으로 한정했다. | TC 계정·배포 운영 관리자 |

U §1.1의 독자별 주요 화면 중 원천 부재 때문에 빠진 항목은 없다. 독자별 첫 작업의 순서는 이 안내의 읽기 순서이며 제품의 강제 업무 순서나 로그인 직후 자동 이동 순서로 주장하지 않았다.

## 다른 문서가 소유해 넣지 않은 사실

| 문서 objectKey | 해당 문서에 남긴 사실 |
|---|---|
| 00.rag/user/login-and-screen-basics.md | 로그인 입력·오류·로그아웃·프로필·메뉴 펼침·표 조작 절차 |
| 00.rag/user/equipment-registration-and-settings.md | 설비 등록·설정·삭제의 입력 조건·조작·검사·결과 |
| 00.rag/user/equipment-commands-and-polling.md | 명령 메뉴·상태별 실행 조건·폴링 조작·비동기 결과 조회 |
| 00.rag/user/model-registration-and-branching.md | root·branch 생성·삭제·비교의 조작과 조건 |
| 00.rag/user/model-details-and-flow-editing.md | 상세 탭·편집 작업본·Flow 입력·저장·버전 작업 |
| 00.rag/user/deployment-workflow.md | 배포 대상 선택·생성·실행·이력·재실행의 조작·상태·오류 |
| 00.rag/user/user-group-and-permission-management.md | 계정·그룹·멤버·권한 부여·회수와 개별 작업 권한 |
| 00.rag/user/live-map-status.md | 상태·포트·배치 보기·필터·선택 설비 읽기 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 실패·격리·Repair Task 목록 읽기·조회 오류·인계 정보 |
| 00.rag/user/permission-code-reference.md | 권한 코드 각각의 뜻과 전체 대응표. 이 문서에는 시작 화면 진입 조건만 최소 표기했다. |

## 검사 결과

기계 검사 명령은 다음과 같으며 Git Bash 실행 파일은 `C:/Program Files/Git/usr/bin/bash.exe`를 사용했다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/getting-started.md
```

기계 검사는 총 3회 실행했고 모두 exit 0이었다. 최초 검사 뒤 인용 범위를 정정하고, 작성 기록을 작성한 뒤 원문을 한 번 더 검사했다. 최종 검사의 마지막 10줄은 다음과 같다.

```text
    L66-66 문단 90  | 「사용자 계정」과 「권한 그룹」에서 할 일과 작업별 권한은 00.rag/user/user-group-and-permission-management.md를 따른다.
§ L68-81 [25] 모델 담당과 운영 관리자가 배포로 이어가는 곳   parent=191 children=2 sources=7
    L79-79 문단 86  | 모델 담당이 변경을 배포하거나 운영 관리자가 배포 진행·이력을 확인할 때는 「적용」의 「배포」로 이어간다. 화면 진입에는 DEPLOY_READ가 필요하다.
    L81-81 문단 104  | 배포 등록부터 실행·이력·재실행까지는 00.rag/user/deployment-workflow.md를 따른다. 기본 권한 데이터에서 생성·실행·삭제는 조회와 별도 권한으로 연결되어 있다.
§ L83-95 [28] 설비 담당과 운영 관리자가 상태·실패를 살펴보는 곳   parent=252 children=2 sources=7
    L94-94 목록 항목 109  | - 설비 담당은 상태를 볼 때 LIVE_MAP_READ가 필요한 「모니터링」의 「Live-Map」에서 시작한다. 상태 읽는 방법은 00.rag/user/live-map-status.md를 따른다.
    L95-95 목록 항목 142  | - 설비 담당과 운영 관리자는 처리 실패·격리·복구 작업을 찾을 때 「모니터링」의 「처리실패」로 이어간다. 진입 권한은 DLQ_READ이며 00.rag/user/dlq-quarantine-and-repair-task.md에서 조회와 인계 방법을 확인한다.
SUMMARY sections=6 children=14 maxChild=142 maxParent=252
CORPUS docs=15/53 sections=263 part={user=15} type={개념=2, 사용 절차=13}
TOTAL files=15 targets=1 violations=0
```

대상 위반 0건, CROSS VIOLATION 출력 0건, MASK 출력 0건이다. 최대 자식 142자·최대 부모 252자로 제한 안이다. 실행 도구는 GNU bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS, Windows PowerShell 5.1.26100.9444이다.

### 사람 검사

40절 미만이므로 무작위 일부 표본 대신 6개 절을 전부 읽었다.

| 절 | 제목으로 질문 식별 | 원천 일치 | 절 단독 답 | 다른 절·문서와 중복 |
|---|---|---|---|---|
| 독자별 업무를 시작하기 전 공통 안내 | 공통 선행 안내를 구별한다. | 세 독자와 화면·서버 권한 경계에 맞는다. | 공통 안내 objectKey와 권한 확인 경로를 제공한다. | 로그인·메뉴 상세 절차를 복제하지 않는다. |
| 설비 담당이 등록·설정과 명령을 시작하는 곳 | 설비 담당의 첫 작업을 드러낸다. | 실제 메뉴·가드·명령 호출과 일치한다. | 시작 메뉴·진입 조건·후속 문서 순서가 있다. | 설비 입력·명령 상태·결과를 다시 설명하지 않는다. |
| 모델 담당이 모델과 Flow 작업을 시작하는 곳 | 모델 담당의 첫 작업을 드러낸다. | 통신 방식별 라우트·가드·상세 연결에 맞는다. | 시작 메뉴·모델 준비·Flow 편집 연결을 완결한다. | 편집·branch 상세 절차를 재작성하지 않는다. |
| 운영 관리자가 계정과 권한 작업을 시작하는 곳 | 관리자 계정 작업의 시작점을 드러낸다. | 시스템 메뉴·카드·가드에 맞는다. | 계정·그룹 작업의 소유 문서를 바로 고를 수 있다. | 계정·권한 조작과 필수값을 복제하지 않는다. |
| 모델 담당과 운영 관리자가 배포로 이어가는 곳 | 두 독자의 배포 공통 경로를 드러낸다. | 메뉴·가드·작업 영역·기본 권한 데이터에 맞는다. | 배포 화면과 연결 문서·조회와 작업 권한의 경계를 제공한다. | 두 독자 절에 배포 안내를 반복하지 않는다. |
| 설비 담당과 운영 관리자가 상태·실패를 살펴보는 곳 | 상태·실패 조회의 독자와 목적을 구별한다. | Live-Map·처리실패 메뉴와 가드에 맞는다. | 각 조회 목적별 메뉴·권한·문서를 고를 수 있다. | 상태값·실패 코드·복구 조작을 복제하지 않는다. |

제목 6개는 서로 다르다. 연속된 두 독자 시작 절은 대상·업무가 서로 다르고, 같은 독자·질문을 이름만 바꿔 늘린 절은 없다. 제목의 불필요한 틀 반복, 정보 없는 틀 문장, 이름 토막 뜻풀이, 잘린 문장, 오답 인용, user의 admin 참조는 남기지 않았다. 처음 작성한 상태 조회 항목의 언어와 권한 상수 인용 범위를 최종 대조 중 바로잡았다. 본문은 한국어 -한다 문체로 다시 전부 읽었고 마스킹 후 의미 손실은 없다. 상세 절차를 링크로 가리킨 것은 사용자 지정 소유 범위이며, 시작 메뉴·독자·진입 권한은 본문에서 독립적으로 답한다.

### 다른 문서 발견 사항

`00.rag/user/user-group-and-permission-management.md` L13의 `permission.ts ; USER_READ 값 L6~9` 인용은 상수 선언 줄을 포함하지 않는다. 실제 `nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3`에서 USER_READ_PERMISSION 선언은 L10이다. 문서 본문의 권한 값이 잘못됐다는 뜻은 아니며 원천 위치 범위의 보완 대상이다. 소유 범위 밖이므로 기존 문서는 수정하지 않았다.

### 미실행 검증과 임시 자원

제품 빌드·제품 단위/통합 시험·실제 웹 화면·현장 DB 권한·검색 반영·정답률은 실행 또는 검증하지 않았다. 문서 작성 범위에서 코드·문구·기본 데이터·기존 시험 기대값·문서 검사기를 대조한 자체 확인이며 메인 세션의 원천 직접 검증을 대체하지 않는다. 별도 AI·CLI Agent 호출은 0회이고 provider 사용량·비용은 이 환경에서 확인하지 못했다.

검사용 임시 파일을 만들지 않았다. 지정된 기존 `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-15-getting-started/work/`는 `Get-ChildItem -Force`로 빈 상태를 확인했고 소유를 새로 취득한 자원이 아니므로 삭제하지 않았다. 이 세션에서 정리할 소유 임시 파일·실행 중 시험 프로세스는 없다. 지정한 검색 원문과 이 작성 기록 두 파일만 만들었다.

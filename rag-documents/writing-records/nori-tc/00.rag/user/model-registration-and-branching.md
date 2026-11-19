# 모델 등록·branch·삭제 작성 기록

## 대상

- objectKey: `00.rag/user/model-registration-and-branching.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문: `rag-documents/nori-tc/00.rag/user/model-registration-and-branching.md`
- 검색 원문 SHA-256: `2e615d27fcc0a0844f8a8276df4ab5966a2bbf6f5a7ac281c58d40cb355fb5c9`
- 검색 원문 줄 수: 249
- 절 수: 14
- 확인일: 2026-11-18
- 범위: U §5.1~5.3·§5.5. Parent Model Commit을 포함한다. 상세·Flow 편집 조작은 제외한다.
- 작성 규칙과 목록 전체를 읽었다. 기존 문서의 겹치는 부분은 `login-and-screen-basics.md`, `equipment-model-parameter-and-jar-update.md` 전체와 `equipment-registration-and-settings.md`의 모델 조회·권한 부분을 대조했다.
- 다른 Agent·독립 AI 호출 없이 이 세션에서 작성했다. 제품 실행·업로드·git add·commit·push는 하지 않았다.

## 원천 판본

| 저장소 | HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 보완 후 본문 원천 18개 경로를 모아 `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-front -C ../nori-tc-ui-front status --short -- <18개 경로>` 실행. exit 0, 변경 행 없음 |
| nori-tc-ui-backend | 985d17c604dd | 보완 후 본문 원천 7개 경로를 같은 방식으로 명시해 status 확인. exit 0, 변경 행 없음 |
| nori-tc-exec | f1d5e0a8e727 | 매뉴얼·권한 catalog·권한 시드·DDL의 4개 경로를 명시해 status 확인. exit 0, 변경 행 없음 |
| nori-tc-common | 6fe71f6b2da6 | 삭제 저장소의 위임 경로를 추가 조사했다. TcModelJpaStore.java·TcModelJpaCommandSupport.java는 개별 status 출력에 변경 행 없음. 이 저장소는 본문 rag-meta 판본에 사용하지 않았다 |

처음 형제 저장소의 일반 git 명령은 현재 실행 사용자와 저장소 소유자가 달라 `detected dubious ownership`으로 실패했다. 전역 설정은 수정하지 않았고, 읽는 저장소 한 곳을 각 명령의 `-c safe.directory=<정확한 경로>`로 지정했다. 이후 rev-parse와 status가 exit 0이었다. 사용자 전역 ignore 파일 접근 경고는 남았지만 명시한 원천 경로의 변경 출력은 없었다.

본문의 원천 29개 파일은 모두 위 HEAD 기준이다. 추가로 API 호출부의 `/api` 접두사, 관리 mutation의 목록 갱신, 서버 controller→port→DB adapter 흐름, 공통 모델 삭제 SQL의 `DELETE FROM tc_model WHERE model_key = :modelKey`, DDL의 branch·version 연쇄 삭제를 읽었다. 매뉴얼만으로 동작을 확정하지 않았다.

관련 시험은 `nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaModelManagementPortTest.java @ 985d17c604dd`의 기대값을 읽었다. root 기본값 L91~125, 선택한 버전 복제 L259~309, commit의 새 parent 및 branch 폐기 L512~555, 최신 상태 기준 일괄 삭제 L560~578, 모델명 1000자 L583~602, parent 버전 100자 L607~629, 하위 branch 설비 참조 시 root 삭제 차단 L634~647을 확인했다. 시험 실행이나 PASS 재사용 주장이 아니다.

## 절별 사실 대조

각 표의 검색 원문 줄은 보완 후 최종 249줄 파일 기준이다. 한 줄의 여러 문장은 행을 나누어 대조했다. 조작 실패 후 인계 항목은 현장 연락처를 가정하지 않고, 실제 화면 대상·조작·시각·표시 결과를 기록하도록 작성한 안내다.

### 모델 화면과 변경 작업에 필요한 권한

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 21 첫 문장 | 세 모델 화면 진입 MODEL_READ | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L97~101; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1099~1101 | RequirePermission으로 세 경로를 감싸며 표시 문구는 SECS·Socket·PLC다 |
| 21 둘째 문장 | 비교 조회까지 MODEL_WRITE 필요 | nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 L386~632; nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L240~256 | 비교 조회는 POST commit-parent다. 기본 MODEL_READ는 GET, MODEL_WRITE는 전체 메서드 PREFIX다 |
| 23 첫 문장 | 메뉴 표시와 서버 인가 구별 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L182~246; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 메뉴의 권한 숨김 조건은 없고, 서버는 일치한 보유 권한이 있어야 허용한다 |
| 23 둘째 문장 | 거부 시 모델·조작·시각·문구 인계 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L757~797; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~174 | 모델별 작업 창의 거부 결과를 운영 관리자에게 전달하는 안내다 |
| 23 셋째 문장 | 기본 데이터와 현장 권한 구별 | nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L771~774 | 저장소 시드만 확인했으며 현장 DB나 계정은 조회하지 않았다 |

### Model 정보 트리에서 모델과 버전 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 37 첫 문장 | 통신 방식별 화면 선택 | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 L97~101 | 각 경로가 같은 ModelPage에 서로 다른 scope를 전달한다 |
| 37 둘째 문장 | 미선택 표의 모델별 최신 항목 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L193~209; L1234~1250 | 조회된 목록을 scope별로 걸러 이름별 최신 대표만 표시한다. 전체 현장 모델이 모두 조회된다고 쓰지 않았다 |
| 38 첫 문장 | root 펼침과 모델 선택 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L157~170; L205~250 | root 클릭은 선택과 펼침 토글, branch 클릭은 모델 선택이다 |
| 38 둘째 문장 | 선택한 모델의 버전들 표시 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L185~191; L1252~1259 | 같은 modelName의 행을 오른쪽 표에 전달한다 |
| 39 첫 문장 | 검색 칸에 이름 일부 입력 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L266~274; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L140 | 실제 placeholder는 Model Name 검색이다 |
| 39 둘째 문장 | 대소문자 무관·검색 root 펼침 | nori-tc-ui-front/src/features/model/lib/model-tree.util.ts @ 9fe69a75dcd3 L53~58; L152~162; nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L141~142; L249~250 | 검색어와 모델명을 소문자로 비교하며 검색 중 펼침을 강제한다 |
| 41 첫 문장 | 여섯 열 순서 | nori-tc-ui-front/src/features/model/components/ModelInfoTable.tsx @ 9fe69a75dcd3 L86~130 | 실제 header와 순서를 전부 대조했다 |
| 41 둘째 문장 | branch의 DEPRECATED 표시 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L44~45; L175~178; nori-tc-ui-front/src/features/model/lib/model-tree.util.ts @ 9fe69a75dcd3 L65~77; L129~133 | 최신 대표의 상태로 이름 옆 배지를 표시한다 |
| 43 | 폭 조절과 축소 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L256~264; nori-tc-ui-front/src/shared/layout/InPageSidebar.tsx @ 9fe69a75dcd3 L45~85; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L143~144 | resize handle과 펼침·축소 aria-label을 확인했다 |

### root 모델을 새로 등록하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 56 첫 문장 | 트리 빈 곳 오른쪽 클릭 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L279~307 | 트리 컨테이너의 ContextMenu다 |
| 56 둘째 문장 | 통신 방식별 Create 메뉴 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L64~68; L302~307 | scopeTitle 뒤에 Model Create를 붙인다 |
| 57 첫 문장 | 기본 정보의 Model Name·Maker | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L94~124; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L687~689 | 필수 모델명·선택 Maker만 렌더한다. 설명 칸은 없다 |
| 57 둘째 문장 | Create 제출·Cancel 닫기 | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L142~149 | 실제 단추명과 handler를 대조했다 |
| 59 첫 문장 | 모델명 필수·1000자 | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L59~70; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelRootCreateRequest.java @ 985d17c604dd L17~24 | 화면 trim·빈 값 검사, 서버 NotBlank·Size(max=1000)를 함께 확인했다 |
| 59 둘째 문장 | 기존 모델명 거부 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L127~129 | 모든 모델에서 같은 이름이 있으면 충돌 오류를 낸다 |
| 59 셋째 문장 | Maker 선택·32자 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelRootCreateRequest.java @ 985d17c604dd L24; nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L79~94 | DTO는 선택이고 DB가 nullable VARCHAR(32)로 제한한다. 화면 자체가 32자를 선검사한다고 쓰지 않았다 |
| 61 첫 문장 | 생성 뒤 창 닫힘·새 모델 선택 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L730~756 | 반환된 version key 선택 후 모달 open을 false로 만든다 |
| 61 둘째 문장 | 버전 자동·OPERATE | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L131~144 | baseline version을 생성하고 OPERATE로 저장한다 |

### PLC 모델 등록 창의 제목 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 72 첫 문장 | PLC 메뉴와 창 제목 차이 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L64~68; L302~307; nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L48~52 | 생성 창 제목 분기는 SOCKET과 그 외뿐이라 PLC에도 SECS 제목이 나온다 |
| 72 둘째 문장 | 생성 요청은 PLC | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L697~705; L734~739 | 메뉴의 interfaceType을 상태에 넣고 commInterface로 전송한다. PLC 화면에서 시작했는지 확인하도록 안내했다 |

### 모델 화면의 Model Info Update로 Maker 수정하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 86 | root 오른쪽 메뉴 선택 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L225~232 | Model Info Update는 root 노드 메뉴다 |
| 87 첫 문장 | Maker 수정 후 Save | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L118~124; L142~149; nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L83 | 실제 입력·단추, DB 길이를 대조했다 |
| 87 둘째 문장 | 빈 Maker 제거 | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L68~70; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L180~190 | 빈 문자열은 null로 전달·저장한다 |
| 87 셋째 문장 | Cancel 닫기 | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L142~144 | 모달 open 변경을 호출한다 |
| 89 첫 문장 | Model Name 읽기 전용 | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L105~113 | update 모드 readOnly다 |
| 89 둘째 문장 | 성공 닫기·기존 버전 상태 설명 보존 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L742~756; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L172~194 | 수정은 Maker만 전달하고 서버는 기존 나머지 필드를 유지한다 |
| 91 | 설비의 동명 메뉴와 구별 | nori-tc-ui-front/src/features/eqp/components/EqpModelBindingModal.tsx @ 9fe69a75dcd3 L210~228; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd L71~81 | 설비 창은 설비 update request를 조립하고, 모델 창은 root model info에 Maker를 보낸다. 설비 갱신 세부는 앞선 문서에 둔다 |

### root 버전을 복제해 branch 만들기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 107 | root의 Branch Model Create | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L225~233 | 실제 메뉴와 대상 모델 전달을 확인했다 |
| 108 첫 문장 | Source Version 선택 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L150~169; nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L211~219 | 해당 root modelKey의 버전만 목록에 넣는다 |
| 108 둘째 문장 | Source Description·suffix 입력 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L174~193 | 선택한 source 설명과 suffix 입력을 렌더한다 |
| 109 첫 문장 | 최종 이름·길이·Create | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L196~208; L231~241; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L707~710 | 실제 미리보기와 길이 표시 문구, 생성 단추를 대조했다 |
| 109 둘째 문장 | Cancel 취소 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L227~229 | 제출 없이 창을 닫는다 |
| 111 첫 문장 | 밑줄로 연결한 최종 이름 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L73~89; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L184~187 | parent_suffix_userId를 실제 코드에서 확인했다 |
| 111 둘째 문장 | 최종 이름 1000자 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L23; L91~94; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L477~480 | 화면과 서버가 최종 조합 이름의 제한을 확인한다 |
| 113 첫 문장 | 성공 닫기·새 branch 선택 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L782~795; nori-tc-ui-front/src/features/model/lib/model-tree.util.ts @ 9fe69a75dcd3 L93~103 | 생성한 branch 선택과 parentModel 기반 트리 구성을 확인했다 |
| 113 둘째 문장 | 선택 버전 복제·자동 branch 버전·DEVELOP 상태 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L235~263 | source 버전 선택 후 새 버전을 부여하고 L256에서 DEVELOP로 저장하며 상세를 복제한다 |
| 113 셋째 문장 | 생성 직후 읽기 전용 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L297~317; nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L386~403; L1285~1297; nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 L223~232 | 날짜·순번의 baseline은 EDIT가 아니다. ModelPage는 EDIT이면서 명시적으로 checkout한 branch만 편집 모드를 허용하고, 패널은 편집 모드가 아니면 읽기 전용으로 판정한다. 편집 조작은 쓰지 않았다 |

### branch 생성 입력이 거부되거나 Create를 누를 수 없을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 124 첫 문장 | suffix 공백 금지 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L63; L96~100 | trim한 suffix가 비면 필수 안내를 표시한다 |
| 124 둘째 문장 | 길면 축소·중복이면 다른 suffix | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L703; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L240~243 | 길이 초과 안내와 동일 branch명 충돌을 대조했다 |
| 126 첫 문장 | Create 비활성 세 조건 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L231~238 | currentUserId 부재·이름 초과·선택 버전 부재를 검사한다. 처리 중에도 비활성이다 |
| 126 둘째 문장 | 사용자 정보 문제 인계 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L701; L708 | 사용자 정보 확인 안내를 근거로 창의 증상·시각을 전달하도록 썼다. 화면에 없는 수정 칸을 만들지 않았다 |
| 128 첫 문장 | source 존재·root 소속 검사 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L274~294 | 버전 존재·같은 modelKey·root 여부를 모두 검사한다 |
| 128 둘째 문장 | 새로고침 후 같은 root의 버전 재선택 | nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L150~169; L227~229; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1195~1201 | source 선택 기능과 조회 갱신 절차를 연결한 사용자 안내다 |

### Parent Model Commit으로 root의 새 버전 만들기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 141 첫 문장 | branch의 Parent Model Commit | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L182~189 | branch 메뉴에서 대상 모델을 넘긴다 |
| 141 둘째 문장 | 양쪽 최신 버전 비교 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L426~438 | branch·parent 최신 snapshot으로 diff를 생성한다 |
| 142 | 추가·변경·삭제 검토 | nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L348~419; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L720~722 | Diff Preview 및 세 종류 표 제목을 확인했다 |
| 143 첫 문장 | 새 Parent Version·Commit | nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L319~332; L435~437 | 버전 입력과 commit handler를 확인했다 |
| 143 둘째 문장 | Cancel 닫기 | nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L427~433 | 창 닫기 handler를 확인했다 |
| 145 첫 문장 | 새 root 버전·창 닫힘 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L843~861; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L448~461 | 새 parent 버전 생성과 branch snapshot 복사 뒤 창을 닫는다 |
| 145 둘째 문장 | branch 전 버전 DEPRECATED | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L462; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L212~230 | 해당 branch modelKey의 모든 버전 상태를 변경한다 |
| 145 셋째 문장 | 기존 root 버전 보존 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L448~459 | 기존 version key 수정이 아니라 null 키로 새 버전을 저장한다 |
| 147 | diff 없어도 새 버전 생성 가능 | nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L364~367; L435~437; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L719 | diff 없음은 Commit 비활성 조건이 아니다 |

### parent 버전 입력과 Commit 비활성 조건

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 157 첫 문장 | trim 후 필수·100자 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L484~504 | normalizeRequiredText와 length >100 거부를 확인했다 |
| 157 둘째 문장 | 같은 root의 기존 버전 거부 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L441~445 | parent modelName+version 중복을 검사한다 |
| 157 셋째 문장 | 길이·기존 버전 확인 안내 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L441~445; nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L327~332 | 실패 조건을 사용자가 수정할 수 있는 입력으로 연결했다 |
| 159 첫 문장 | 폐기·현재 branch 편집 시 메뉴 비활성 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L943~955 | DEPRECATED 검사 후 활성 탭의 modelKey와 isEditMode를 확인한다 |
| 159 둘째 문장 | 편집 종료 뒤 진행 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L949~953 | 활성 편집 상태가 비활성 조건이므로 편집 종료 안내를 붙였다. 편집 종료 절차는 복제하지 않았다 |
| 161 | 로딩·요청 처리 중 Commit 비활성 | nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L435~437 | isPreviewLoading 또는 isCommitPending 조건이다 |

### branch 하나 또는 root 전체 삭제하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 174 첫 문장 | 삭제 대상 오른쪽 클릭 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L165~198; L211~245 | branch·root 각각 context menu가 있다 |
| 174 둘째 문장 | Branch Model Delete·Model Delete | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L191~196; L240~245 | 메뉴 이름과 대상 전달을 확인했다 |
| 175 첫 문장 | 대상·범위 확인 후 Delete | nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L37~60; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L680~683 | 실제 대상 이름 포함 확인 문구와 단추를 대조했다 |
| 175 둘째 문장 | Cancel 취소 | nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L60~61 | cancelText가 Cancel이다 |
| 177 첫 문장 | branch 전체 버전 삭제 | nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 L608~625; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd L101~116; nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L102~117 | scope=model로 모델을 삭제하며 모델 버전 FK도 cascade다 |
| 177 둘째 문장 | root와 연결 branch 삭제 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L205~220; nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L79~94 | 대상 검사를 하고 모델 삭제를 호출한다. parent_model FK는 ON DELETE CASCADE다 |
| 179 첫 문장 | 성공 창 닫힘·목록 제거 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L890~936; nori-tc-ui-front/src/features/model/hooks/useModelManagementMutations.ts @ 9fe69a75dcd3 L98~102 | 성공 후 목록 무효화·확인 창 닫기를 확인했다 |
| 179 둘째 문장 | 삭제 버전 탭 정리 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L903~923; nori-tc-ui-front/src/features/model/stores/model-ui.store.ts @ 9fe69a75dcd3 L245~280 | 삭제된 version key 집합으로 openedTabs와 선택 상태를 정리한다 |

### DEPRECATED branch를 root별로 일괄 삭제하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 191 | root의 Branch Deprecated Model Delete | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L234~238 | root 노드 메뉴다 |
| 192 첫 문장 | 확인 문구·Delete All | nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L30~60; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L678~679 | root 이름과 최신 상태 기준 문구, 별도 Delete All 단추를 확인했다 |
| 192 둘째 문장 | Cancel 취소 | nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L61 | 취소 단추를 확인했다 |
| 194 첫 문장 | 최신 상태가 폐기인 branch만 전 버전 삭제 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L385~399; nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 L102~117 | 최신 대표 map에서 parent·DEPRECATED로 걸러 modelKey 단위 삭제한다 |
| 194 둘째 문장 | root·나머지 branch 유지 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L385~400 | 선택된 하위 branch 목록만 순회한다 |
| 194 셋째 문장 | 성공 후 트리 확인 | nori-tc-ui-front/src/features/model/hooks/useModelManagementMutations.ts @ 9fe69a75dcd3 L91~95; nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L901~908 | 목록을 다시 읽고 실제 삭제된 모델 키만 제거한다 |
| 196 | 폐기 버전이 없으면 메뉴 비활성 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L983~987 | UI는 하위 버전 중 하나라도 DEPRECATED가 있는지를 본다. 서버의 최신 상태 기준과 구별했다 |

### 삭제 메뉴가 비활성화되거나 설비 참조로 거부될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 208 첫 문장 | 같은 branch 편집 중 삭제 비활성 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L958~966 | isEditMode 및 활성 모델 키를 비교한다 |
| 208 둘째 문장 | root·하위 branch 편집 중 root 삭제 비활성 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L969~980 | 활성 모델 키 또는 parentModel로 검사한다 |
| 208 셋째 문장 | 편집 종료 뒤 삭제 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L960~977 | 편집 상태가 차단 조건이므로 작업 종료 안내를 붙였다 |
| 210 첫 문장 | 설비 참조 거부 문구와 조건 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L190~208 | 모든 삭제 대상 버전과 설비 참조를 대조하고 「해당 모델을 참조 중인 EQP가 있어 삭제할 수 없습니다.」를 낸다. 본문 문구를 그대로 대조했다 |
| 210 둘째 문장 | root 하위 branch도 검사 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L209~215; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L340~347 | cascade 모델 집합으로 참조 검사를 수행한다 |
| 212 | 데이터 참조 오류의 서버 문구 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L205~220; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L396~410 | 「해당 모델을 참조 중인 데이터가 있어 삭제할 수 없습니다.」를 그대로 기록했다. 기존 UiConflictException은 보존하고 DB 참조 충돌은 대체 문구로 변환한다 |
| 214 첫 문장 | 일괄 삭제의 공통 참조 검사 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L377~399 | 삭제 대상으로 고른 branch 집합에 ensureNoEqpReference를 호출한다 |
| 214 둘째 문장 | 하나라도 참조되면 전체 거부 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L396~399 | 전체 대상의 검사 이후에만 삭제 루프가 시작되므로 검사에서 거부되면 어떤 branch도 지우지 않는다 |

### 삭제가 거부된 모델을 참조하는 설비 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 226 첫 문장 | 삭제 거부 문구에 설비 ID 없음 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd L207~208; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L220 | 두 오류 문구 모두 고정 문자열로 설비 ID를 포함하지 않는다 |
| 226 둘째 문장 | Gateway 목록의 모델·버전으로 설비 찾기 | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L1186~1197; nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 L179~184 | Gateway 선택 시 설비 목록을 렌더하고 Model·Model Version 열에 연결된 모델명·버전을 표시한다 |
| 226 셋째 문장 | root 삭제 시 하위 branch 사용 설비 확인 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd L209~215; nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 L179~184 | root와 하위 branch를 모두 검사하므로 각 삭제 대상 모델을 목록 열과 대조하도록 안내했다 |
| 228 첫 문장 | 설비 담당과 조율·모델 변경은 배포 | nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 L354~363; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd L25~51 | 선택 모델 버전을 배포 입력에 담고 성공 후 설비 버전을 확정한다. 실제 배포 상세 절차는 앞선 문서 소유로 남겼다 |
| 228 둘째 문장 | 모델·시각·메뉴·거부 문구 인계 | nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L37~71 | 화면에 있는 대상과 오류를 중심으로 인계하도록 작성했다 |

### 모델 목록 조회나 관리 요청이 실패했을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 245 첫 문장 | 빈 목록의 방식·검색어 확인 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L126~128; L266~288; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L141 | 조회 scope·검색어로 걸러진 roots가 비면 같은 빈 안내를 표시한다 |
| 245 둘째 문장 | 빈 안내와 오류 동시 표시 가능 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L286~299 | 빈 결과·loading·error는 독립 분기다 |
| 245 셋째 문장 | 재시도 단추 없음·새로고침 | nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 L286~307; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1195~1201 | 오류 옆은 텍스트뿐이다. 매뉴얼의 화면 새로고침 안내를 유지했다 |
| 247 첫 문장 | 작업 창의 오류 표시 | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L129~138; nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L214~223; nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L336~345; nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L68~71 | 등록·수정·branch·commit·삭제 각 오류 영역을 모두 확인했다 |
| 247 둘째 문장 | 서버 오류 우선 표시 | nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 L11~12; nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 L19~34; L49~58 | 서버 errorMsg를 Error.message로 전달하고 비어 있지 않으면 fallback보다 먼저 쓴다 |
| 247 셋째 문장 | 별도 재시도 없음 | nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 L142~149; nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 L227~241; nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L427~437; nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 L60~61 | 각 창은 기존 제출·취소 단추를 제공한다 |
| 249 첫 문장 | 비교 실패 후 Cancel로 닫고 다시 열기 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L804~830; nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 L427~433; nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1503~1505 | 다시 열면 비교 요청을 새로 수행한다 |
| 249 둘째 문장 | 지속 실패의 인계 항목 | nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 L757~797; L815~817; L862~863; L935~936 | 실제 모델·버전과 실행한 조작·발생 시각·표시 문구를 전달하는 안내다. 현장 원인을 단정하지 않았다 |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 코드 | 검색 원문 반영 |
|---|---|---|---|
| root의 설명 입력 | U §5.2 L1214·L1229~1231에서 제조사와 설명을 입력한다고 안내 | ModelCreateOrUpdateModal.tsx L103~126은 Model Name·Maker만 표시하고 root DTO도 설명을 받지 않음 | 설명 입력 절차를 쓰지 않고 실제 Maker 문구를 사용 |
| PLC 생성 창 제목 | U §5.2 L1212~1213은 통신 방식에 맞는 생성 이름으로 안내 | ModelSidebar.tsx L302~307 메뉴는 PLC Model Create지만 ModelCreateOrUpdateModal.tsx L48~52 창 제목에는 PLC 분기가 없음 | 메뉴와 창 제목을 별도 절에서 정확히 구별하고 요청 통신 방식은 PLC임을 추적 |
| 입력 길이 | U §5.2 L1229는 root 이름을 자유 입력, §5.5 L1487은 parent 버전을 자유 입력으로 안내 | root DTO max=1000, 지원 코드 parent version max=100, DDL maker VARCHAR(32) | 모델명·Maker·parent 버전 제한을 본문에 명시 |
| 삭제 오류의 원인 | U §5.5 L1506은 일반 삭제 실패를 설비 사용으로 설명하고 §4.8로 안내 | 서버는 설비 참조 외에도 대상 없음·입력·충돌 등으로 거부할 수 있고, 설비 화면 Model Info Update는 실제 모델 연결 변경을 반영하지 않음(앞선 문서 소유) | 설비 참조 문구가 나온 조건에만 모델 변경 조율을 안내. 모든 실패의 원인을 설비 사용으로 단정하지 않음 |
| 목록 없음의 의미 | U §5.1 L1199는 통신 방식 모델 부재로 설명 | ModelSidebar.tsx L126~128·L286~299는 검색 필터 결과 부재에도 빈 문구를 내며 오류와 동시에 표시 가능 | 통신 방식·검색어·별도 오류 문구를 함께 확인하도록 안내 |
| 오류 대체 문구 | U §5.5 L1504~1505의 기본 실패 문구와 실제 로케일 표현 일부가 다름 | ko.json L727·L737과 API 오류 우선 표시를 확인 | 매뉴얼의 옛 문구를 나열하지 않고 실제 창의 서버 문구 확인 방법을 기록 |

## 원천이 없어 뺀 사실과 확인할 담당 역할

목록 질문에 답하는 데 필요한 조사 범위에서 원천 부재로 뺀 사실은 없다. 현장 배포 판본·실제 모델 목록·계정별 부여 권한은 조회하지 않았다. 본문은 확인한 소스와 기본 권한 데이터 기준이며 현장 상태를 주장하지 않는다. 현장 상태의 확인 역할은 TC 운영 담당자다.

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 문서에서 제외한 내용 |
|---|---|
| 00.rag/user/model-details-and-flow-editing.md | 상세 열기·Flow 편집·Check Out·Save·Check In·Undo의 조작 순서. branch 생성 직후 읽기 전용과 관리 메뉴 차단에 필요한 최소 편집 조건만 남김 |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 설비 화면의 Model Info Update, 실제 설비 모델 변경 배포 방법. 여기서는 root Maker 수정과 다른 대상이라는 사실만 구별 |
| 00.rag/user/deployment-workflow.md | 배포 등록·실행·결과 확인 전체 절차 |
| 00.rag/user/permission-code-reference.md | MODEL_READ·MODEL_WRITE 등 코드별 상세 정의와 권한 목록. 여기서는 모델 화면·요청에 필요한 조건만 사용 |
| 00.rag/user/state-reference.md | OPERATE·DEVELOP·DEPRECATED의 상태값 전체 뜻풀이. 여기서는 생성·commit·삭제 대상 판단에 필요한 실제 상태만 사용 |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드·문구별 전체 원인 목록. 여기서는 실제 작업 입력 오류·창 표시·인계만 안내 |
| 00.rag/user/login-and-screen-basics.md | 전체 좌측 메뉴 구성, 공통 표 조작·복사·로그인·언어 전환 |

## 최초 작성 검사 결과(보완 전 이력)

실행 명령:

`bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/model-registration-and-branching.md`

실제 실행 파일은 `C:/Program Files/Git/usr/bin/bash.exe`다. 검사기는 수정하지 않았다. 첫 실행은 223줄 초안에서 exit 0·위반 0이었고, sidebar 조작 보완·중복 사실 제거 뒤 최종 227줄에서 다시 exit 0이었다. 최종 결과는 13절·46자식, 최대 자식 143자·최대 부모 434자, 전체 7개 문서와의 중복 검사 포함 위반 0이다. MASK·CROSS VIOLATION 출력은 없었다.

최종 검사 출력 마지막 10줄:

```text
    L202-202 문단 138  | 현재 편집 중인 활성 탭이 같은 branch이면 「Branch Model Delete」가 비활성화된다. 활성 탭이 삭제할 root 또는 그 하위 branch의 편집 상태이면 「Model Delete」가 비활성화된다. 편집 작업을 마친 뒤 삭제한다.
    L204-204 문단 86  | 서버는 삭제 범위의 모든 모델 버전 중 설비가 참조하는 버전이 있으면 삭제를 거부한다. root 삭제 때는 하위 branch에 대한 설비 참조도 검사한다.
    L206-206 문단 86  | 설비 참조로 거부되면 설비 담당자와 사용 모델 변경을 조율한다. 직접 해결할 수 없으면 모델명·발생 시각·실행 메뉴·거부 문구를 운영 담당자에게 전달한다.
§ L208-227 [23] 모델 목록 조회나 관리 요청이 실패했을 때   parent=313 children=3 sources=11
    L223-223 문단 106  | 「표시할 모델이 없습니다.」이면 통신 방식과 검색어를 확인한다. 조회 오류 문구도 함께 보이면 조회 실패를 먼저 확인한다. 목록 오류에는 별도 「재시도」 단추가 없으므로 화면을 새로고침한다.
    L225-225 문단 100  | 등록·수정·branch 생성·commit·삭제 오류는 각 작업 창에 표시된다. 서버가 보낸 오류 문구가 있으면 기본 안내 대신 그 문구를 보여 준다. 별도 「재시도」 단추는 없다.
    L227-227 문단 105  | parent commit 비교 조회가 실패하면 「Cancel」로 창을 닫고 다시 연다. 실패가 계속되면 모델명·기준 버전·누른 메뉴나 단추·발생 시각·표시 문구를 운영 담당자에게 전달한다.
SUMMARY sections=13 children=46 maxChild=143 maxParent=434
CORPUS docs=7/53 sections=117 part={user=7} type={사용 절차=7}
TOTAL files=7 targets=1 violations=0
```

사람 검사는 40절 미만이므로 13절 전부 읽었다. 다음 표의 네 판정은 제목 질문 식별·원천 일치·독립 답·중복 방지 순이다.

| 절 | 제목 질문 식별 | 원천 일치 | 독립 답 | 중복 방지 |
|---|---|---|---|---|
| 모델 화면과 변경 작업에 필요한 권한 | 적합 | 적합 | 적합 | 권한 정의 대신 필요한 조건만 소유 |
| Model 정보 트리에서 모델과 버전 찾기 | 적합 | 적합 | 적합 | 목록 찾기와 표시 소유 |
| root 모델을 새로 등록하기 | 적합 | 적합 | 적합 | root 생성 소유 |
| PLC 모델 등록 창의 제목 확인하기 | 적합 | 적합 | 적합 | PLC 제목 차이만 소유 |
| 모델 화면의 Model Info Update로 Maker 수정하기 | 적합 | 적합 | 적합 | 설비 동명 메뉴와 구별 |
| root 버전을 복제해 branch 만들기 | 적합 | 적합 | 적합 | 정상 생성 순서·최종 이름 규칙 소유 |
| branch 생성 입력이 거부되거나 Create를 누를 수 없을 때 | 적합 | 적합 | 적합 | 길이 수치 반복 없이 실패 조건·조치만 보완 |
| Parent Model Commit으로 root의 새 버전 만들기 | 적합 | 적합 | 적합 | 정상 commit·결과 소유 |
| parent 버전 입력과 Commit 비활성 조건 | 적합 | 적합 | 적합 | 버전 제약·메뉴 차단 소유 |
| branch 하나 또는 root 전체 삭제하기 | 적합 | 적합 | 적합 | 개별 모델 삭제 범위 소유 |
| DEPRECATED branch를 root별로 일괄 삭제하기 | 적합 | 적합 | 적합 | 최신 상태 선별·일괄 삭제 소유 |
| 삭제 메뉴가 비활성화되거나 설비 참조로 거부될 때 | 적합 | 적합 | 적합 | 설비 참조 조건은 이 절에 모음 |
| 모델 목록 조회나 관리 요청이 실패했을 때 | 적합 | 적합 | 적합 | 일반 오류 렌더·재조회·인계 소유 |

제목 정확 중복과 정보 없는 반복 틀, 본문 복제, 빈 틀 문장, 이름 토막 뜻풀이는 발견하지 않았다. 조사·주어·단위·실제 UI 표기 및 마스킹 결과를 전수 대조했다. user 본문에 admin objectKey·내부 접근 조치·비밀값·오답 인용은 없다. 자체검사이며 메인 세션의 독립 검증을 대신하지 않는다.

준비 과정에서 이미 존재하는 빈 work 폴더에 `New-Item -ItemType Directory -ErrorAction Stop`을 호출해 `ResourceExists`로 검사 시작 전에 멈춘 적이 있다. 폴더를 다시 만들지 않고 존재 확인 후 검사했다. 이 시도를 검사 실행으로 세지 않았다.

최종 파일 바이트·줄 수·인코딩·원천 판본 및 임시 로그 정리 결과는 아래 최종 확인에 기록한다.

## 최초 작성의 파일·정리 확인(보완 전 이력)

검색 원문은 BOM 없는 UTF-8·LF이며 CR·NUL 없음, NFC=true를 바이트 검사로 확인했다. 작성 기록도 같은 인코딩·줄끝 조건을 확인했다. `git diff --check -- <대상 두 경로>`는 exit 0이었다. 두 파일은 untracked이므로 이 명령만으로 전체 내용을 검사했다고 해석하지 않으며, 위 전수 읽기와 별도 바이트 검사를 함께 수행했다. 도구 버전은 GNU bash 5.2.37(1)-release, OpenJDK 21.0.9였다.

이 세션의 일회성 검사 로그는 다음 두 파일뿐이며, 필요한 결과는 위 검사 결과에 보존했다.

- `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-07-model-registration-and-branching/work/check-initial.log`
- `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-07-model-registration-and-branching/work/check-final.log`

정리 전 확인: 지정 work 경로 아래 정확한 두 절대경로이며, 두 파일은 Archive 속성만 있고 work 폴더는 Directory 속성만 있었다. ReadOnly·Hidden·System·ReparsePoint 속성과 LinkType은 없었다. 두 로그 모두 공유 없는 읽기 열기에 성공해 열린 핸들을 닫았다. 검사 두 호출은 이미 종료했고 장기 실행 세션을 만들지 않았다. `Get-CimInstance Win32_Process`의 전체 명령행 조회는 HRESULT 0x80041003 접근 거부로 실패했다. 이를 전체 프로세스 참조 없음으로 기록하지 않았다. 대신 `Get-Process -Name java,bash`의 접근 가능한 시작 시각과 검사 호출 종료, 로그의 배타적 열기 결과를 확인했다. 다른 세션의 프로세스는 종료하지 않았다.

정리 명령은 위 두 파일 각각에 `Remove-Item -LiteralPath <절대경로> -ErrorAction Stop`을 사용했고, Force·Recurse 옵션은 쓰지 않았다. exit 0, 두 파일의 `Test-Path`가 모두 False였다. `Get-ChildItem -LiteralPath <work> -Force`의 잔여 항목은 0개다. 원래 존재하던 work 디렉터리는 보존했다. 임시 자원 미정리는 없다.

최초 작성은 검색 원문 13절 및 이 작성 기록 두 파일을 완료로 보고했으나, 이후 메인 세션의 원천 대조에서 아래 두 결함이 확인됐다. 당시 기계 검사·자체검사 기록은 그 범위의 이력이며 사실 완전성 검증을 대신하지 못했다. 실제 반입은 실행하지 않았다. provider 사용량·비용과 세션 전체 wall time은 별도 측정 자료가 없어 미확인이다.

## 메인 세션 지적 2건 보완과 최종 검사

보완 전 검색 원문 SHA-256은 `9406345070a708372b2b94d5da3340a483e3d23ed2a0dd0effc96616f677b9ef`이며, 보완 후는 대상 절의 최종 SHA-256에 기록했다. 처음 검증의 누락을 인정하고 다음 직접 영향만 수정했다.

| 결함 | 원인 | 수정과 확인 |
|---|---|---|
| 1. 삭제 거부 문구·참조 설비 확인 경로 누락 | 참조 검사와 일반 인계만 설명하고 실제 고정 오류 문구, 일괄 삭제의 전체 거부 순서, 설비 목록의 모델 열을 연결하지 않았다 | 기존 삭제 제한 절을 유지하고 두 서버 문구와 삭제 루프 전 공통 검사 순서를 명시했다. 별도 한 절에 설비 ID 부재·Gateway의 Model 및 Model Version 열·배포 변경·인계를 모았다 |
| 2. 새 branch 상태 누락·읽기 전용 근거 부정확 | 생성 후 선택 handler를 읽기 전용 판정 근거로 사용했고 생성 명령의 DEVELOP 값을 빠뜨렸다 | DEVELOP 저장을 명시했다. 날짜·순번 초기 버전 생성→EDIT만 편집 허용→패널의 읽기 전용 판정으로 근거를 교체했다. 상세·Flow 편집 조작은 추가하지 않았다 |

검색 원문의 기존 13절 중 두 절만 바뀌었으며 나머지 11절은 보완 전 본문·메타와 문자열로 일치함을 확인했다. 새 절 한 개는 결함 1의 직접 영향으로 분리했다. 관련 없는 코드·문서·설정은 고치지 않았다. 위 사실 대조표는 추가·수정한 문장까지 최종 줄 번호로 맞췄다. 최초 작성 검사의 227줄 출력은 보완 전 이력으로 구별해 보존했다.

기계 검사는 위에 적은 같은 `check.sh --dump 00.rag/user/model-registration-and-branching.md` 명령을 Git Bash에서 실행했다. exit 0, 14절·49자식, 최대 자식 143자·최대 부모 434자, violations=0이다. MASK·CROSS VIOLATION 출력은 없었다. 오류 문구의 EQP와 데이터 참조 표현, DEVELOP 상태, Model 및 Model Version 열이 마스킹 후에도 보존되는지 dump와 대조했다. 검사기·작성 규칙은 수정하지 않았다.

보완 후 검사 출력 마지막 10줄:

```text
§ L216-228 [22] 삭제가 거부된 모델을 참조하는 설비 찾기   parent=232 children=2 sources=6
    L226-226 문단 136  | 삭제 거부 문구에는 설비 ID가 없다. 「설비」 화면의 「Gateway」 목록에서 「Model」·「Model Version」 열을 보고 삭제 대상 모델을 쓰는 설비를 찾는다. root 삭제가 거부됐으면 하위 branch를 쓰는 설비도 확인한다.
    L228-228 문단 95  | 설비 담당자와 사용 모델 변경을 조율하며, 설비의 모델 변경은 「배포」로 한다. 직접 해결할 수 없으면 모델명·발생 시각·실행 메뉴·거부 문구를 운영 담당자에게 전달한다.
§ L230-249 [23] 모델 목록 조회나 관리 요청이 실패했을 때   parent=313 children=3 sources=11
    L245-245 문단 106  | 「표시할 모델이 없습니다.」이면 통신 방식과 검색어를 확인한다. 조회 오류 문구도 함께 보이면 조회 실패를 먼저 확인한다. 목록 오류에는 별도 「재시도」 단추가 없으므로 화면을 새로고침한다.
    L247-247 문단 100  | 등록·수정·branch 생성·commit·삭제 오류는 각 작업 창에 표시된다. 서버가 보낸 오류 문구가 있으면 기본 안내 대신 그 문구를 보여 준다. 별도 「재시도」 단추는 없다.
    L249-249 문단 105  | parent commit 비교 조회가 실패하면 「Cancel」로 창을 닫고 다시 연다. 실패가 계속되면 모델명·기준 버전·누른 메뉴나 단추·발생 시각·표시 문구를 운영 담당자에게 전달한다.
SUMMARY sections=14 children=49 maxChild=143 maxParent=434
CORPUS docs=7/53 sections=118 part={user=7} type={사용 절차=7}
TOTAL files=7 targets=1 violations=0
```

사람 검사에서는 최종 14절 전부 읽었다. 제목 질문 식별·독립 답·빈 틀 문장·중복·문장·권한 경계를 다시 확인했고 미해소 지적은 없다. 변경한 두 기존 절과 새 절의 근거는 위 사실 대조표에서 다시 확인했다. 새 설비 찾기 절의 제목·원천 일치·독립 답은 적합하며, 배포 상세와 모델 편집 절차를 복제하지 않았다. 자체검사 결과이며 메인 세션의 후속 검증을 실행했다고 보고하지 않는다.

이번 보완에서는 검사 출력과 보완 전 원문을 세션 메모리에서 대조해 임시 파일을 만들지 않았다. 지정 work 폴더도 잔여 항목 0개임을 확인했다. 정리할 새 소유 임시 자원은 없다. 제품 실행 시험·서버 접속·업로드·git add·commit·push는 하지 않았다.


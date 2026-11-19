# 모델 등록·branch·삭제
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/model-details-and-flow-editing.md
```

## 모델 화면과 변경 작업에 필요한 권한
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 모델 진입 L97~101
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 문구 L1092~1101
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 작업 메뉴 L182~246
원천: nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; 관리 API L386~632
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L160~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 API 권한 L240~256
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 기본 권한 L771~774
```

「모델」의 「SECS」·「Socket」·「PLC」 화면 진입에는 MODEL_READ가 필요하다. 저장소 기본 권한 데이터 기준으로 등록·수정·branch 생성·Parent Model Commit의 비교 조회와 실행·삭제에는 MODEL_WRITE가 필요하다.

변경 메뉴가 보여도 요청 권한이 없으면 서버가 거부한다. 권한 거부이면 운영 관리자에게 모델명·누른 메뉴·발생 시각·거부 문구를 전달한다. 기본 권한 데이터는 현장 계정의 권한 부여 상태를 보장하지 않는다.

## Model 정보 트리에서 모델과 버전 찾기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 펼침·선택·검색 L126~180
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; root 선택·검색 칸 L205~274
원천: nori-tc-ui-front/src/features/model/lib/model-tree.util.ts @ 9fe69a75dcd3 ; 검색어 처리 L53~58
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 목록 구성 L185~209
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 표 대상 L1234~1259
원천: nori-tc-ui-front/src/features/model/components/ModelInfoTable.tsx @ 9fe69a75dcd3 ; 표시 열 L86~130
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 트리 문구 L138~144
원천: nori-tc-ui-front/src/shared/layout/InPageSidebar.tsx @ 9fe69a75dcd3 ; 축소·폭 조절 L45~85
```

1. 통신 방식에 맞는 「SECS」·「Socket」·「PLC」를 연다. 모델을 고르기 전에는 오른쪽 「Model 정보」 표에 조회된 모델별 최신 항목이 나온다.
2. 왼쪽 「Model 정보」 트리에서 root 이름을 눌러 branch를 펼치고, 작업할 모델명을 누른다. 오른쪽 표에서 선택한 모델의 버전들을 확인한다.
3. 「Model Name 검색」에 이름 일부를 입력한다. 대소문자를 구별하지 않으며 검색 중에는 검색 결과의 root가 펼쳐진다.

표의 열은 「Model Name」·「Model Version」·「status」·「description」·「updated_by」·「updated_at」 순서다. 최신 상태가 DEPRECATED인 branch는 트리 이름 옆에도 DEPRECATED가 표시된다.

트리의 좌우 폭은 경계를 끌어 조절하고, 「사이드바 축소」로 접는다.

## root 모델을 새로 등록하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 생성 메뉴 L279~307
원천: nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 ; 입력·검사 L59~124
원천: nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 ; 단추 L142~149
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 생성 후 선택 L730~756
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/ModelRootCreateRequest.java @ 985d17c604dd ; 필수·길이 검사 L17~24
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 생성 L121~149
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 제조사 길이 L79~94
```

1. 「Model 정보」 트리의 빈 곳을 오른쪽 클릭한다. 화면에 맞는 「SECS Model Create」·「Socket Model Create」·「PLC Model Create」를 고른다.
2. 「기본 정보」의 「Model Name」을 입력하고, 필요한 경우 「Maker」를 입력한다. 「Create」를 누르면 등록하고, 「Cancel」을 누르면 창을 닫는다.

「Model Name」은 공백만으로 입력할 수 없고 1000자 이하여야 한다. 이미 있는 모델명은 사용할 수 없다. 「Maker」는 선택이며 32자 이내로 입력한다.

등록에 성공하면 창이 닫히고 새 root가 선택된다. root 버전은 자동으로 생성되고 「status」는 OPERATE로 저장된다.

## PLC 모델 등록 창의 제목 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 통신 방식별 메뉴 L64~68
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 생성 대상 전달 L302~307
원천: nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 ; 창 제목 L48~52
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 통신 방식 보관 L697~705
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 생성 요청 L734~739
```

확인한 소스 판본에서 「PLC」 화면의 「PLC Model Create」를 누르면 창 제목은 「SECS Model Create」로 표시된다. 생성 요청의 통신 방식은 PLC로 전달되므로, PLC 모델을 만들 때는 「PLC」 화면에서 연 메뉴인지 확인한다.

## 모델 화면의 Model Info Update로 Maker 수정하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; root 메뉴 L225~232
원천: nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 ; 수정 입력 L103~124
원천: nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 ; 저장·취소 L142~149
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 수정 요청·결과 L742~756
원천: nori-tc-ui-front/src/features/eqp/components/EqpModelBindingModal.tsx @ 9fe69a75dcd3 ; 설비 화면의 별도 요청 L210~228
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelBranchController.java @ 985d17c604dd ; 모델 정보 수정 L71~81
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; Maker만 변경 L172~194
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Maker 길이 L79~94
```

1. 모델 화면 왼쪽 트리의 root 이름을 오른쪽 클릭하고 「Model Info Update」를 고른다.
2. 「Maker」를 32자 이내로 고친 뒤 「Save」를 누른다. 제조사 값을 비우면 저장할 때 제거한다. 「Cancel」은 창을 닫는다.

「Model Name」은 읽기 전용이다. 저장에 성공하면 창이 닫히며, root의 기존 버전·상태·설명은 유지된다.

설비 화면에서 같은 이름으로 여는 메뉴와는 대상과 처리 내용이 다르다.

## root 버전을 복제해 branch 만들기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; branch 생성 메뉴 L225~233
원천: nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; 이름 조합·검사 L61~119
원천: nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; 입력·미리보기 L144~208
원천: nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; 생성 단추 L227~241
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 생성 후 선택 L782~795
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; EDIT 작업본만 편집 허용 L386~403
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용 판정 L223~232
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 복제 L229~267
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; EDIT가 아닌 초기 버전 생성 L297~317
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 생성 안내 L704~710
```

1. 트리의 root 이름을 오른쪽 클릭하고 「Branch Model Create」를 고른다.
2. 「Source Version」에서 복제할 root 버전을 고른다. 「Source Description」에서 선택한 버전의 설명을 확인하고 「suffix」를 입력한다.
3. 「최종 Model Name Preview」와 남은 길이를 확인한 뒤 「Create」를 누른다. 「Cancel」은 생성을 취소한다.

branch 이름은 부모 모델명·suffix·로그인 사용자 아이디 사이에 밑줄을 넣어 만든다. 완성된 이름은 1000자 이하여야 한다.

성공하면 창이 닫히고 root 아래 새 branch가 선택된다. 선택한 root 버전의 내용을 복제하고 branch 버전은 자동 생성하며 「status」는 DEVELOP로 저장된다. 생성 직후는 읽기 전용이다.

## branch 생성 입력이 거부되거나 Create를 누를 수 없을 때
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; 필수 검사 L96~119
원천: nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; 비활성 조건 L231~238
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 안내 L700~703
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 중복 거부 L235~243
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; 복제 기준 검사 L274~294
```

「suffix」는 공백만으로 입력할 수 없다. 최종 이름이 너무 길면 suffix를 줄이고, 동일한 branch 이름이 이미 있으면 다른 suffix를 사용한다.

로그인 사용자 정보를 확인하지 못하거나 복제 버전이 선택되지 않았거나 최종 이름이 길이 제한을 넘으면 「Create」가 비활성화된다. 사용자 정보를 확인하지 못하는 상태가 계속되면 운영 담당자에게 발생 시각과 창의 표시 문구를 전달한다.

서버는 복제 버전이 존재하고 선택한 root에 속하는지도 검사한다. 복제 기준 오류가 나오면 창을 닫고 목록을 새로고침한 뒤 같은 root의 「Source Version」을 다시 고른다.

## Parent Model Commit으로 root의 새 버전 만들기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; branch 메뉴 L182~196
원천: nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 ; 필수 검사 L258~266
원천: nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 ; 버전 입력 L319~332
원천: nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 ; 비교 표시·단추 L348~437
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 성공 후 닫기 L843~863
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 비교·commit L421~462
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비교 문구 L713~722
```

1. 트리의 branch 이름을 오른쪽 클릭하고 「Parent Model Commit」을 고른다. branch 최신 버전과 parent 최신 버전의 비교를 불러온다.
2. 「Diff Preview」의 「추가 항목」·「변경 항목」·「삭제 항목」을 검토한다.
3. 「새 Parent Version」을 확인하거나 고친 뒤 「Commit」을 누른다. 「Cancel」은 창을 닫는다.

성공하면 창이 닫히고 branch 최신 내용으로 root의 새 버전이 생긴다. commit한 branch의 모든 버전 상태는 DEPRECATED로 바뀐다. root의 이전 버전을 덮어쓰지는 않는다.

비교 항목이 없어도 새 parent 버전을 생성할 수 있다.

## parent 버전 입력과 Commit 비활성 조건
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 중복 버전 거부 L441~445
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; 버전 필수·길이 L484~504
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 메뉴 비활성 L943~955
원천: nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 ; 단추 비활성 L427~437
```

「새 Parent Version」은 앞뒤 공백을 제거한 뒤 비어 있지 않아야 하며 100자 이하여야 한다. 같은 root에 이미 있는 버전은 사용할 수 없다. 입력 오류이면 길이와 기존 버전을 확인해 고친다.

DEPRECATED branch이거나 현재 편집 중인 활성 탭이 같은 branch이면 「Parent Model Commit」 메뉴가 비활성화된다. 편집 중이면 branch 편집 작업을 마친 뒤 진행한다.

비교를 불러오는 중이거나 commit 요청 처리 중에는 창의 「Commit」을 누를 수 없다.

## branch 하나 또는 root 전체 삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L191~245
원천: nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 확인 창 L30~71
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 삭제·탭 정리 L890~936
원천: nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; 모델 단위 삭제 L608~625
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelMutationController.java @ 985d17c604dd ; 모델 단위 처리 L101~116
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 모델 삭제 L205~220
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; branch·버전 연쇄 삭제 L79~117
```

1. 트리에서 삭제할 모델명을 오른쪽 클릭한다. branch 하나는 「Branch Model Delete」, root 전체는 「Model Delete」를 고른다.
2. 확인 창에 표시된 모델명과 삭제 범위를 읽고 「Delete」를 누른다. 「Cancel」은 삭제를 취소한다.

branch 삭제는 그 branch의 모든 버전을 지운다. root 삭제는 root와 연결된 branch를 함께 지운다.

성공하면 확인 창이 닫히고 삭제된 모델은 목록에서 사라진다. 삭제된 버전의 열린 탭도 정리된다.

## DEPRECATED branch를 root별로 일괄 삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 일괄 삭제 메뉴 L234~238
원천: nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; Delete All·Cancel L30~61
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 결과 반영 L901~908
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 비활성 조건 L983~987
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 삭제 대상·참조 검사 L377~410
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 확인 문구 L678~679
```

1. 트리의 root 이름을 오른쪽 클릭하고 「Branch Deprecated Model Delete」를 고른다.
2. 선택한 root와 연결된 branch를 정리한다는 확인 문구를 읽고 「Delete All」을 누른다. 「Cancel」은 일괄 삭제를 취소한다.

서버는 최신 상태가 DEPRECATED인 branch만 골라 모든 버전을 삭제한다. root와 최신 상태가 다른 branch는 남는다. 성공 뒤 트리에서 삭제 대상이 사라졌는지 확인한다.

목록에 DEPRECATED 상태의 하위 branch 버전이 하나도 없으면 메뉴가 비활성화된다.

## 삭제 메뉴가 비활성화되거나 설비 참조로 거부될 때
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 편집 중 삭제 제한 L958~980
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; 설비 참조 검사 L190~208
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; 참조 오류 변환 L396~410
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 개별 삭제 거부 문구 L205~220
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 일괄 삭제 전 참조 검사 L377~410
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 5.5.4 오류와 대처 L1501~1507
```

현재 편집 중인 활성 탭이 같은 branch이면 「Branch Model Delete」가 비활성화된다. 활성 탭이 삭제할 root 또는 그 하위 branch의 편집 상태이면 「Model Delete」가 비활성화된다. 편집 작업을 마친 뒤 삭제한다.

「해당 모델을 참조 중인 EQP가 있어 삭제할 수 없습니다.」는 삭제 범위의 모델 버전을 쓰는 설비가 있다는 뜻이다. root 삭제는 하위 branch도 검사한다.

「해당 모델을 참조 중인 데이터가 있어 삭제할 수 없습니다.」는 서버가 데이터 참조 오류를 처리할 때 표시한다.

「Branch Deprecated Model Delete」도 삭제 전에 같은 설비 참조 검사를 한다. 참조가 하나라도 있으면 어느 branch도 삭제하지 않고 전체 요청을 거부한다.

## 삭제가 거부된 모델을 참조하는 설비 찾기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementSupport.java @ 985d17c604dd ; 설비 ID 없는 거부 문구 L190~208
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 데이터 참조 거부 문구 L205~220
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Gateway 목록 표시 L1186~1197
원천: nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 모델·버전 열 L179~184
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델 배포 입력 L354~363
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 배포 성공 후 모델 확정 L25~51
```

삭제 거부 문구에는 설비 ID가 없다. 「설비」 화면의 「Gateway」 목록에서 「Model」·「Model Version」 열을 보고 삭제 대상 모델을 쓰는 설비를 찾는다. root 삭제가 거부됐으면 하위 branch를 쓰는 설비도 확인한다.

설비 담당자와 사용 모델 변경을 조율하며, 설비의 모델 변경은 「배포」로 한다. 직접 해결할 수 없으면 모델명·발생 시각·실행 메뉴·거부 문구를 운영 담당자에게 전달한다.

## 모델 목록 조회나 관리 요청이 실패했을 때
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelSidebar.tsx @ 9fe69a75dcd3 ; 빈 목록·오류 표시 L286~299
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 관리 오류 L757~797
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; 비교 조회 실패 L804~817
원천: nori-tc-ui-front/src/features/model/components/ModelCreateOrUpdateModal.tsx @ 9fe69a75dcd3 ; 오류·단추 L129~149
원천: nori-tc-ui-front/src/features/model/components/BranchModelCreateModal.tsx @ 9fe69a75dcd3 ; 오류·단추 L214~241
원천: nori-tc-ui-front/src/features/model/components/ParentModelCommitModal.tsx @ 9fe69a75dcd3 ; 오류 L336~345
원천: nori-tc-ui-front/src/features/model/components/ModelDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 오류 L68~71
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 문구 우선 L11~12
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 목록 문구 L140~142
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 목록·비교 조회 대처 L1195~1201
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 비교 조회 대처 L1503~1507
```

「표시할 모델이 없습니다.」이면 통신 방식과 검색어를 확인한다. 조회 오류 문구도 함께 보이면 조회 실패를 먼저 확인한다. 목록 오류에는 별도 「재시도」 단추가 없으므로 화면을 새로고침한다.

등록·수정·branch 생성·commit·삭제 오류는 각 작업 창에 표시된다. 서버가 보낸 오류 문구가 있으면 기본 안내 대신 그 문구를 보여 준다. 별도 「재시도」 단추는 없다.

parent commit 비교 조회가 실패하면 「Cancel」로 창을 닫고 다시 연다. 실패가 계속되면 모델명·기준 버전·누른 메뉴나 단추·발생 시각·표시 문구를 운영 담당자에게 전달한다.

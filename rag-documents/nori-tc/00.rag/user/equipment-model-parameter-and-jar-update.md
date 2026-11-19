# 설비 모델·파라미터·JAR 갱신
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/deployment-workflow.md
```

## 파라미터 편집에 필요한 권한 준비하기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 진입 L93~95
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 파라미터 요청 L479~565
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L160~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 권한 L123~158
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비 권한 시드 L764~767
```

「설비」 화면 진입에는 EQP_READ가 필요하다. 저장소 기본 권한 데이터 기준으로 파라미터의 「Check Out」·「Save」·「Check In」·「Undo」 요청에는 EQP_WRITE, 라인 목록 조회에는 LINE_READ가 필요하다.

권한 거부이면 운영 관리자에게 설비 ID·실행한 단추·시각·거부 문구를 보내 계정 권한을 확인한다. 기본 데이터는 현장 계정에 부여된 권한을 보장하지 않는다.

## 갱신 배포를 만들고 실행할 권한 확인하기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 배포 진입 L103~105
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 배포 API 권한 L51~104
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 배포 권한 시드 L780~783
원천: nori-tc-ui-front/src/features/deploy/hooks/useDeployEqpParamVersions.ts @ 9fe69a75dcd3 ; 설비 버전 조회 L9~16
원천: nori-tc-ui-front/src/features/deploy/hooks/useDeployRegisterOptions.ts @ 9fe69a75dcd3 ; 모델·JAR 옵션 조회 L9~16
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 버전 API L365~382
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 조회 L123~139
```

「배포」 화면을 열려면 DEPLOY_READ가 필요하다. 저장소 기본 권한 데이터 기준으로 시나리오 사전 검토는 DEPLOY_PREVIEW, 배포 생성은 DEPLOY_WRITE, 실행은 DEPLOY_ACTION이 필요하다.

배포의 모델·JAR 선택 목록과 설비 파라미터 버전 목록을 읽으려면 기본 권한 데이터 기준으로 EQP_READ·EQP_WRITE 중 하나도 필요하다.

## Model Info Update에서 저장해도 모델이 유지되는 이유
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 오른쪽 메뉴 L180~188
원천: nori-tc-ui-front/src/features/eqp/components/EqpModelBindingModal.tsx @ 9fe69a75dcd3 ; 제출 L210~230
원천: nori-tc-ui-front/src/features/eqp/components/EqpModelBindingModal.tsx @ 9fe69a75dcd3 ; 선택 칸 L284~350
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 창 설명 문구 L580~580
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 수정 요청 L115~146
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 일반 수정 L602~620
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필드 L25~60
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd ; 기존 연결 유지 L44~65
```

「Gateway」 트리의 설비를 오른쪽 클릭하면 「Model Info Update」를 열 수 있다. 「Model Name」·「Model Version」·「Business Jar」를 고르는 칸은 있지만, 확인한 소스 판본의 저장 처리는 기존 모델·모델 버전·설비 파라미터 버전·Gateway JAR·Business JAR를 유지한다.

모델과 Business JAR를 바꾸려면 「Model Info Update」 대신 「배포」 화면의 「등록」 카드에서 변경할 값을 지정한다.

## 설비에 적용할 모델과 모델 버전 지정하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델 목록 L183~209
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델 입력 L1263~1300
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 요청에 모델 포함 L354~373
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 문구 L211~214
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/resources/deploy-scenarios/register-model-only.yaml @ 985d17c604dd ; Model 변경 L3~6
```

1. 「배포」의 「등록」 카드에서 바꿀 설비를 「선택된 설비」에 담는다. 한 대를 바꿀 때도 배포를 사용한다.
2. 「설정 및 배포」에서 「신규 Model 선택」을 고른 뒤 「신규 Model Version 선택」을 고른다. 모델을 먼저 고르기 전에는 버전 칸을 누를 수 없다.
3. 모델만 바꾸면 「시나리오 선택」에서 「Model 변경」을 사용한다. 모델·버전 목록은 선택한 설비의 통신 방식에 맞춰 좁혀진다.

## Check Out으로 설비 파라미터 편집 시작하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 체크아웃 L801~823
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 버전·단추 L1255~1343
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 기준 복사 L84~141
```

1. 「설비」에서 대상 설비를 선택하고 아래쪽 파라미터 영역의 버전 선택 칸에서 편집 기준 버전을 고른다.
2. 「Check Out」을 눌러 편집을 시작한다. 선택한 버전의 값을 복사해 편집한다. 기존 버전이 없는 설비도 빈 표에서 시작할 수 있다.
3. 편집 중에는 기준 버전 선택 칸이 비활성화되고 「Undo」·「Save」·「Check In」이 표시된다.

버전 선택은 편집·조회할 기준을 고르는 조작이며, 설비에 적용할 버전 지정은 배포에서 한다.

## 파라미터 행을 수정하고 Save로 임시 저장하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 입력·삭제·추가 L53~150
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 이름 검사·저장 L887~973
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 저장 비활성 조건 L1097~1103
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 파라미터 문구 L50~53
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열 문구 L68~69
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 임시 저장 L156~183
```

1. 편집 중인 「설비 파라미터」에서 「행 추가」를 누르고 「Param Name」·「Param Value」·「설명」을 입력한다. 기존 행은 칸에서 고치고, 지울 행은 「작업」 열의 휴지통 단추를 누른다.
2. 「Save」를 눌러 편집 내용을 임시 저장한다. 임시 저장만으로 새 버전이나 설비 적용값이 생기지는 않는다.

「Param Name」은 앞뒤 공백을 뺀 뒤 비어 있거나 중복되면 안 된다. 「Param Value」·「설명」은 생략할 수 있다. 행이 없거나 이름이 비었거나 중복되면 「Save」가 비활성화된다.

## Check In 비교 화면을 보고 새 버전 만들기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/CheckInModal.tsx @ 9fe69a75dcd3 ; 비교·버전·설명 L75~146
원천: nori-tc-ui-front/src/features/eqp/components/CheckInModal.tsx @ 9fe69a75dcd3 ; 취소·저장 L157~175
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 취소는 창 닫기 L1408~1416
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 체크인 L998~1016
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpParamMutations.ts @ 9fe69a75dcd3 ; 버전 목록 갱신 L61~70
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 버전·비교 문구 L585~589
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 버전 생성 L224~263
```

1. 파라미터를 「Save」로 저장한 뒤 「Check In」을 누른다.
2. 「Diff Preview」에서 「구분」과 함께 표시된 변경 행을 확인한다. 변경 행이 없으면 「이전 버전과 비교하여 변경된 설비 파라미터가 없습니다.」가 나온다.
3. 「Description」의 「변경 내용 설명 (선택)」을 필요하면 적고 창의 「Save」를 누른다. 「Version」은 서버가 `YY.MM.DD.0000` 형식으로 자동 생성한다.

서버에 편집 행이 있는 체크인이 성공하면 편집이 끝나고 새 버전이 버전 목록에 추가된다. 창의 「Cancel」은 체크인 창만 닫는다.

## Save를 거치지 않고 Check In하면 어떤 내용이 남는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 별도 임시 저장 L961~970
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설명만 전송·로컬 비교 L998~1033
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 체크아웃 기준 복사 L84~141
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; Save로 편집 행 교체 L156~183
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 편집 행으로 버전 생성 L224~263
```

확인한 소스 판본의 「Diff Preview」는 화면에서 편집한 행을 기준 버전과 비교한다. 체크인은 체크아웃 때 서버에 복사된 기준 값이나 마지막 「Save」로 저장한 값으로 새 버전을 만든다. 저장하지 않은 화면 수정은 새 버전에 들어가지 않는다.

화면 수정까지 반영하려면 마지막 수정 뒤 파라미터 영역의 「Save」를 먼저 누른다. 「Check In」 창의 「Save」는 설명만 보내며 파라미터 행을 저장하지 않는다.

서버에 편집 행이 하나도 없으면 새 버전 없이 끝난다. 빈 기준에서 체크아웃하고 「Save」 없이 체크인한 경우가 이에 해당한다. 체크인 뒤 버전 목록을 확인한다.

## Undo로 파라미터 편집 전체 취소하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Undo 처리 L979~992
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 편집 중 단추 L1283~1326
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 임시 행 삭제 L196~203
```

편집을 버리려면 파라미터 영역의 「Undo」를 누른다. 성공하면 화면에서 고친 행과 「Save」로 임시 저장했던 행을 모두 버리고 편집을 끝낸다. 기존 확정 버전은 유지한다.

「Undo」는 편집 중에만 나타난다. 체크인으로 이미 만든 버전을 취소하는 단추는 아니다.

## 편집 중 표시나 체크아웃 거부가 나타날 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 편집자 안내 L1053~1061
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 체크아웃 비활성 L1090~1096
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 체크아웃 오류 L817~819
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 이미 편집 중이면 거부 L98~104
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; 충돌 응답 L159~162
```

파라미터 버전 상자 아래에 편집자 아이디와 「님이 편집 중」이 표시된다. 아이디를 받지 못하면 「편집 중」으로 나온다. 본인이 편집 중이어도 표시되므로 아이디로 편집자를 구별한다.

다른 사용자가 편집 중이면 「Check Out」이 비활성화될 수 있고, 서버가 이미 체크아웃된 요청을 거부할 수 있다. 편집자와 작업 종료를 조율한다. 편집자를 확인할 수 없으면 설비 ID·시각·표시 문구를 운영 담당자에게 전달한다.

## 파라미터 저장·체크인·되돌리기가 실패했을 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 저장·Undo·체크인 오류 L971~1016
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 영역 오류 L1375~1384
원천: nori-tc-ui-front/src/features/eqp/components/CheckInModal.tsx @ 9fe69a75dcd3 ; 창의 오류와 단추 L151~175
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 우선순위 L11~12
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpParamCommandPort.java @ 985d17c604dd ; 설비 존재 검사 L269~289
```

파라미터 작업이 실패하면 영역이나 「Check In」 창에 오류 문구가 표시된다. 전달된 오류 문구가 있으면 기본 실패 안내 대신 그 문구를 표시한다. 이 위치에는 별도 「재시도」 단추가 없다.

서버가 설비를 찾을 수 없다고 안내하면 설비 목록을 새로고침해 대상을 확인한다. 계속 실패하면 설비 ID·기준 버전·누른 단추·발생 시각·오류 문구를 운영 담당자에게 전달한다.

## 새 파라미터 버전을 설비별 배포 입력으로 고르기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 설비별 버전 요청 L376~398
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 버전 열 L943~953
원천: nori-tc-ui-front/src/features/deploy/components/SelectedParamVersionCell.tsx @ 9fe69a75dcd3 ; 선택·오류 L45~87
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 버전 칸 문구 L219~221
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/resources/deploy-scenarios/register-eqp-param-only.yaml @ 985d17c604dd ; Eqp Param 변경 L3~6
```

「배포」의 「등록」 카드에서 대상 설비를 「선택된 설비」에 담고, 각 행의 「Eqp Param Version」에서 체크인한 버전을 고른다. 파라미터만 바꾸면 「시나리오 선택」의 「Eqp Param 변경」을 사용한다.

버전을 새로 고르지 않으면 배포 요청은 그 설비의 기존 적용 버전을 사용한다. 새 버전이 있다는 이유만으로 최신 버전을 자동 적용하지 않는다.

목록이 비면 「사용 가능한 버전이 없습니다.」가 나온다. 조회 오류는 버전 칸 아래에 표시되므로, 빈 목록과 오류 문구를 구별한다.

## Business JAR를 바꾸려면 어떤 파일을 고르는가
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 파일 요청 L354~373
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 파일 선택·Upload L1303~1330
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 파일 입력·업로드 L214~216
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/resources/deploy-scenarios/register-business-jar-only.yaml @ 985d17c604dd ; Business Jar 변경 L3~6
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployPlanningService.java @ 985d17c604dd ; 미지정 파일 유지 L679~683
```

「배포」의 「등록」 카드에서 대상 설비를 담고 「설정 및 배포」의 「Business Jar File 선택」에서 등록된 파일을 고른다. 파일만 바꾸면 「시나리오 선택」에서 「Business Jar 변경」을 사용한다.

Business JAR를 선택하지 않으면 기존 파일을 유지한다. 여러 설비를 선택하면 고른 파일은 공통 배포 입력이 된다.

「Upload」는 비활성화되어 이 화면에서 파일을 올릴 수 없다. 필요한 파일이 목록에 없으면 운영 담당자에게 대상 설비 ID와 필요한 파일을 보내 등록을 요청한다.

## Gateway JAR는 등록 후 화면에서 바꿀 수 있는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Gateway Jar 선택 L949~967
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd ; 등록 JAR·수정 유지 L18~65
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 배포 입력 종류 L354~398
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 설정 입력 L1262~1330
```

확인한 소스 판본에서 Gateway JAR는 새 설비 등록 창의 「Gateway Jar」에서 정한다. 이미 등록된 설비의 Gateway JAR를 바꾸는 화면 경로는 없다. 배포의 「설정 및 배포」에도 Gateway JAR 선택 칸이 없다.

등록 후 Gateway JAR 변경이 필요하면 운영 담당자에게 설비 ID·현재 파일·필요한 파일을 전달한다. 새 설비를 등록하는 전체 절차는 「설비 등록·설정 변경·삭제」에서 다룬다.

## 갱신 배포의 등록과 실제 반영은 언제 나뉘는가
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 시나리오 사전 검토 L490~518
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterScenarioModal.tsx @ 9fe69a75dcd3 ; 확정 단추 L235~244
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 확정 문구 L263~263
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 생성 뒤 실행 대기 L120~185
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 성공 후 버전 확정 L25~76
```

모델·파라미터 버전·Business JAR 입력을 정한 뒤 「배포」를 누르면 「시나리오 선택」이 열린다. 변경할 항목을 함께 처리하는 시나리오를 고르고 「선택한 시나리오로 Deploy」를 누르면 배포가 생성된다.

변경이 있는 배포는 생성 후 실행 대기 상태가 된다. 「배포」 카드에서 실행하고 대상 설비의 배포가 성공해야 모델·파라미터 버전·Business JAR가 반영된다. 여러 설비를 배포하면 설비별 결과를 확인한다.

## 갱신한 모델·파라미터·JAR를 어느 값으로 확인하는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 모델·파일 표시 L179~193
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 단건 JAR 공란 L147~154
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 적용 버전 L405~427
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 적용 상자 L1348~1365
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 적용 상자 문구 L61~63
```

배포 성공 뒤 설비 화면을 새로고침하고 대상 설비의 값을 확인한다.

| 갱신 대상 | 확인 위치 |
|---|---|
| 모델·모델 버전 | 「Gateway」 설비 목록의 「Model」·「Model Version」 열 |
| 설비 파라미터 | 「현재 적용된 설비 파라미터」 상자의 「버전」 |
| Business JAR | 「Gateway」 설비 목록의 「Business Jarfile」 열 |
| 등록한 Gateway JAR | 「Gateway」 설비 목록의 「Gateway Jarfile」 열 |

한 대의 「설비 정보」 표에서는 JAR 열이 항상 「—」로 표시된다. 파라미터 체크인으로 새 버전이 생겨도 배포 전에는 「현재 적용된 설비 파라미터」의 버전이 바뀌지 않는다.

## 배포 입력이 같거나 선택 정보가 사라졌을 때
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델 정보 검사 L462~473
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 변경 없는 설비 제외 L490~507
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 생성 오류 L694~704
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 오류 문구 L226~229
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployPlanningService.java @ 985d17c604dd ; 실제 변경 비교 L139~179
```

「변경 사항이 있는 설비가 없습니다.」이면 선택한 모델 버전·Business JAR·설비별 파라미터 버전이 현재 값과 다른지 확인한다. 변경 없는 설비는 시나리오 사전 검토에서 제외된다.

「선택한 Model Version 정보를 확인할 수 없습니다.」이면 모델과 버전을 목록에서 다시 고른다. 「Deploy 생성 실패」이면 창의 상세 문구를 읽고, 해결할 수 없으면 설비 ID·선택한 모델 버전·파라미터 버전·파일·발생 시각·문구를 운영 담당자에게 전달한다.

# 배포 등록·실행·이력·재실행
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/equipment-model-parameter-and-jar-update.md
```

## 배포 화면에서 필요한 작업 권한 확인하기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 배포 진입 L103~105
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 배포 API 권한 L51~104
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 배포 시드 L780~784
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L160~185
```

「배포」 화면 진입은 DEPLOY_READ로 제한된다. 아래는 저장소 기본 권한 데이터의 연결이며 현장 계정의 실제 권한 부여와는 구별한다.

| 배포 조작 | 필요한 권한 |
|---|---|
| 대상·목록·상세·시나리오 조회 | DEPLOY_READ |
| 시나리오 사전 검토 | DEPLOY_PREVIEW |
| 새 배포 생성 | DEPLOY_WRITE |
| 실행·Hold·재실행 요청 | DEPLOY_ACTION |
| 배포 삭제 | DEPLOY_DELETE |

권한 거부가 나오면 운영 관리자에게 조작한 메뉴·시각·설비 ID 또는 배포 번호·거부 문구를 전달한다. 화면을 열 권한과 실행·삭제 권한은 따로 확인한다.

## 모델·사용자 선택 목록을 불러올 권한
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 옵션 조회 L186~192
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 라인 조건 L126~139
원천: nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; 라인 API L22~29
원천: nori-tc-ui-front/src/features/deploy/hooks/useDeployRegisterOptions.ts @ 9fe69a75dcd3 ; 설비 옵션 재사용 L9~15
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 옵션 API L346~359
원천: nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 사용자 목록 L110~128
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 추가 조회 권한 L123~139
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 라인 조회 권한 L150~157
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 사용자 권한 L267~293
```

기본 권한 데이터 기준으로 모델·JAR 선택 목록 조회에는 EQP_READ·EQP_WRITE 중 하나가 필요하다. 「배포」·「배포 이력」의 사용자 선택 목록에는 USER_READ·USER_WRITE·USER_INFO_WRITE 중 하나가 필요하다.

등록 대상은 상단에서 선택한 라인 안에서 조회한다. 기본 권한 데이터에서 라인 목록은 LINE_READ가 필요하며, 라인을 확인할 수 없으면 대상 조회도 시작되지 않는다.

배포 목록은 열리지만 선택 목록에 오류가 나오면 오류 문구와 비어 있는 선택 칸을 운영 관리자에게 알린다.

## 등록 카드에서 배포할 설비 찾기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployPage.tsx @ 9fe69a75dcd3 ; 카드 전환 L13~52
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 조회 L708~725
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 선택 라인 조건 L126~139
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 조회 화면 L1029~1175
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 문구 L148~205
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 대상 조회 L102~120
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaDeployRegisterQueryPort.java @ 985d17c604dd ; 라인·조회 조건 L62~86
```

1. 좌측 「배포」 메뉴를 열고 「등록」 카드를 누른다. 「등록」·「배포」·「배포 이력」 중 선택한 카드의 내용만 열린다.
2. 「대상 설비 조회」에서 「전체 Model」·「전체 Version」·「Eqp ID 검색...」 조건을 정하고 「조회」를 누른다. 조건은 생략할 수 있다.
3. 결과 표에서 설비를 체크하고 「선택에 추가」를 누른다. 머리글 체크는 현재 조회 결과 중 선택 가능한 설비를 한꺼번에 고른다.

조회 전에는 「검색 조건을 설정한 뒤 조회 버튼을 눌러 주세요.」, 결과가 없으면 「검색 결과가 없습니다.」가 표시된다.

## 선택된 설비를 빼거나 등록 내용을 초기화하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 추가 L756~779
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 초기화 L719~725
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 전체 초기화 L815~827
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 선택 표 L907~955
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 제거 단추 L1196~1257
원천: nori-tc-ui-front/src/features/deploy/stores/deploy-ui.store.ts @ 9fe69a75dcd3 ; 등록 상태 초기화 L513~524
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 선택 안내 L239~242
```

「선택된 설비」에는 「Eqp ID」·「Model」·「Selected Version」·「Business Jar File」·「Interface」·「Eqp Param Version」·「Remove」가 나온다. 「Remove」의 휴지통은 그 설비만 뺀다. 비어 있으면 「아직 선택된 설비가 없습니다.」가 표시된다.

추가 뒤에는 「선택한 설비를 Selected Equipments에 추가했습니다. active 설비와 중복 설비는 제외됩니다.」가 표시된다. 「추가할 설비를 먼저 선택해 주세요.」이면 조회 결과를 체크한다.

「대상 설비 조회」의 「초기화」는 조회 조건과 체크를 지운다. 「설정 및 배포」의 「초기화」는 선택 설비·변경할 값·조회 조건과 조회 여부까지 지운다.

## 활성 배포에 포함된 설비가 선택되지 않을 때
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 선택 제외 L246~269
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 체크 차단 L728~737
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 경고 표시 L854~879
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 선택 잔여 경고 L1179~1193
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 충돌 검사 L761~775
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 경고 L203~206
```

다른 진행 중 배포에 묶인 설비는 「ACTIVE」 또는 상태가 붙은 「ACTIVE READY」·「ACTIVE RUNNING」 표와 안내가 나오며 체크할 수 없다. 「이미 활성 배포에 포함된 설비입니다.」가 나오면 기존 배포 결과를 먼저 확인한다.

선택 후 다른 배포에 포함된 설비는 「선택된 설비」에 경고와 함께 남을 수 있다. 「Remove」로 빼고 다시 시도한다. 서버 거부 문구에 설비 ID·기존 배포 번호·상태가 있으면 함께 운영 담당자에게 전달한다.

## 통신 방식이 섞였거나 100대를 넘겼을 때
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/lib/deploy-register-selection.ts @ 9fe69a75dcd3 ; 개수·통신 방식 L8~38
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 추가 개수 검사 L768~773
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 생성 전 검사 L435~488
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 배포 비활성 L1233~1249
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 제한 문구 L224~240
```

배포할 설비는 한 대 이상이고 한 번에 최대 100대다. 「한 번에 최대 100대까지 배포할 수 있습니다. 선택을 줄여 주세요.」이면 「Remove」로 수를 줄여 나눠 등록한다.

「Interface」가 서로 다른 설비를 함께 담으면 「배포」가 비활성화된다. 「서로 다른 Interface 의 설비는 한 번에 Deploy 할 수 없습니다.」이면 같은 통신 방식끼리 나눈다.

「설비를 먼저 선택해 주세요.」이면 조회 결과를 체크한 뒤 「선택에 추가」로 대상 표에 담는다.

## 시나리오 선택 창에서 배포 생성하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 미리 선택할 시나리오 L490~518
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterScenarioModal.tsx @ 9fe69a75dcd3 ; 선택 표 L41~134
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterScenarioModal.tsx @ 9fe69a75dcd3 ; 요약·단추 L155~244
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시나리오 문구 L244~263
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployPlanningService.java @ 985d17c604dd ; 후보 계산 L182~195
```

1. 「선택된 설비」와 「설정 및 배포」의 변경할 값을 확인하고 「배포」를 누른다.
2. 「시나리오 선택」에서 「배포 대상」 대수·「감지된 변경」·「권장 시나리오」와 사유를 읽는다.
3. 표의 「시나리오명」·「코드」·「포함 변경」·「단계 수」·「설명」을 비교하고 「선택」의 동그라미를 고른다. 「권장」 표가 붙은 후보가 먼저 선택된다.
4. 「선택한 시나리오로 Deploy」를 누른다. 생성을 그만두려면 「닫기」로 창을 닫는다.

「Deploy 에 사용할 시나리오를 먼저 선택해 주세요.」이면 후보를 선택한다. 후보가 없으면 생성 단추가 비활성화된다.

## 시나리오 사전 검토나 생성 요청이 거부됐을 때
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 사전 검토 오류 L524~534
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 생성 오류 L694~704
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 사전 검토 API L714~736
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployPlanningService.java @ 985d17c604dd ; 후보 없음 L120~125
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployPlanningService.java @ 985d17c604dd ; 변경·시나리오 재검사 L548~568
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 문구 L231~235
```

사전 검토 실패는 「시나리오 Preview 실패」 창에 표시된다. 서버 문구가 있으면 그 문구가 나오며, 대체 문구는 「시나리오 preview를 조회하지 못했습니다.」 또는 「시나리오 preview를 생성하지 못했습니다.」다.

「선택 가능한 시나리오가 없습니다.」이거나 서버가 시나리오 부재·변경 조합 미포함을 알리면 대상과 변경할 값을 확인하고 사전 검토부터 다시 한다. 계속 거부되면 대상 설비·변경 항목·시나리오 코드·시각·문구를 운영 담당자에게 전달한다.

생성 요청 거부는 「Deploy 생성 실패」 창에서 읽는다. 서버 문구가 없으면 「Deploy 생성에 실패했습니다.」가 나온다.

## 화면에 입력 칸이 없는 생성 필수값 오류
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/DeployCreateRequest.java @ 985d17c604dd ; 필수값 L17~58
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 자동 생성 요청 L400~432
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 검증 요청 L171~181
```

서버는 배포명·이벤트 종류·공통 요청·통신 방식·한 건 이상의 대상과 대상별 설비 ID를 필수로 검사한다. 배포명과 이벤트 종류는 화면이 자동으로 채워 보내며 사용자가 적는 칸은 없다.

「deployName은 필수입니다.」·「eventType은 필수입니다.」·「commonRequest는 필수입니다.」처럼 화면에서 고칠 칸이 없는 오류이면 운영 담당자에게 선택 설비·시나리오·시각·전체 오류 문구를 전달한다. 대상 누락 오류이면 「선택된 설비」부터 확인한다.

## 생성 결과 창에서 배포 번호와 상세 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 생성 뒤 상세 조회 L595~693
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 결과 창 단추 L967~1008
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 조회 실패 안내 L234~243
```

「Deploy 생성 완료」 창에서 배포 번호와 상태를 읽고 「Deploy 탭으로 이동」을 누르면 「배포」 카드로 전환된다. 「닫기」는 창만 닫는다.

「생성 직후 상세 조회에 실패했습니다. Deploy 탭에서 상태를 확인해 주세요.」가 나오면 표시된 배포 번호를 보관하고 「Deploy 탭으로 이동」으로 상태를 확인한다.

생성 뒤 조회에서 실패 결과가 확인되면 「Deploy 처리 실패」 창에 결과와 대상 오류가 나온다. 같은 이동 단추로 상세를 확인하고 배포 번호·설비 ID·오류 문구를 운영 담당자에게 전달한다.

## 배포와 배포 이력을 조건으로 조회하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 조회·초기화 L472~494
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 조회 화면 L712~783
원천: nori-tc-ui-front/src/features/deploy/lib/deploy-ui-state.ts @ 9fe69a75dcd3 ; 기본 기간 L27~37
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; 목록 구분 L300~348
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 문구 L172~191
```

「배포」의 「배포 조회」 또는 「배포 이력」의 「배포 이력 조회」에서 「전체 Model」·「전체 사용자」·「시작 기간」·「종료 기간」을 정하고 「조회」를 누른다. 사용자는 배포를 만든 사람, 기간은 생성일 기준이다.

「배포」 목록은 PLANNING·READY·RUNNING, 「배포 이력」 목록은 COMPLETED·PARTIAL_FAILED·FAILED·CANCELED를 조회한다.

두 카드의 조회 조건은 따로 유지된다. 기본 기간은 오늘과 7일 전 사이이며 「초기화」는 모델·사용자 조건을 비우고 기간을 기본값으로 돌린다.

## 목록이 비었거나 다른 쪽의 배포를 찾을 때
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 표 열 L307~351
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 빈 목록·쪽 이동 L912~1049
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 기간 검사 L145~158
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록 문구 L190~192
```

「표시할 배포 데이터가 없습니다.」 또는 「완료된 배포 이력이 없습니다.」이면 카드와 조회 기간·모델·사용자 조건을 확인하고 범위를 넓혀 「조회」한다. 「toDate는 fromDate보다 빠를 수 없습니다.」이면 종료 기간을 시작 기간 이후로 고친다.

목록 아래 「이전」·「다음」으로 쪽을 옮긴다. 표의 「Deploy Name」·「Event Type」·「Status」·「Target Count」·「Completed」·「Failed」·「Requested By」·「Created At」로 배포와 집계를 구별한다.

끝난 배포는 「배포 이력」에서 찾고, 계속 찾지 못하면 배포 번호·실행 시각·조회 조건을 운영 담당자에게 전달한다.

## READY 배포를 실행하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 실행 처리 L620~635
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 메뉴 L950~996
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 실행 API L573~595
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 실행 L531~575
```

DEPLOY_ACTION으로 실행할 수 있는 계정에서 「배포」 카드의 「SERVER」 줄을 선택한다. 상태가 READY이면 줄을 오른쪽 클릭해 「Deploy」를 고른다. 실행 접수 뒤 상태는 RUNNING으로 바뀌며 서버 안내 문구가 표시된다.

PLANNING의 메뉴에는 「View」·「Delete」만 나온다. READY에는 「View」·「Deploy」·「Delete」, RUNNING에는 「View」·「Hold」가 나온다. 그 밖의 서버 상태에는 「View」와 비활성 「동작 없음」이 나온다.

「READY 상태의 Deploy만 실행할 수 있습니다.」라는 거부 문구이면 줄의 최신 상태를 다시 확인한다.

실행 실패는 서버 문구를 우선 표시하고 없으면 「Deploy 실행에 실패했습니다.」가 나온다. 배포 번호·시각·상태·문구를 운영 담당자에게 전달한다.

## Hold로 진행을 보류하고 다시 시작하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 보류 요청 L638~653
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 보류 메뉴 L978~988
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 보류 검사·응답 L649~679
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployExecutor.java @ 985d17c604dd ; 단계 경계 보류 L128~142
원천: nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/DeployServiceTest.java @ 985d17c604dd ; 보류·재개 기대값 L190~263
```

DEPLOY_ACTION이 있는 계정에서 RUNNING인 「SERVER」 줄을 오른쪽 클릭해 「Hold」를 누른다. 접수되면 「Hold 요청됨」으로 바뀌고 같은 메뉴를 다시 누를 수 없다.

접수 안내는 「현재 step 완료 후 Deploy가 대기 상태로 전환됩니다.」다. 즉시 정지 완료로 읽지 않고 상태를 확인한다. READY로 돌아오고 「Hold 요청됨」이 사라졌으면 「Deploy」로 다시 시작한다.

서버는 RUNNING 이외의 상태와 중복 Hold를 거부한다. 「hold 요청이 남아 있는 Deploy는 실행할 수 없습니다.」이면 최신 상태를 확인하고 계속되면 배포 번호·시각·문구를 운영 담당자에게 전달한다.

Hold 실패는 서버 문구를 우선 표시하고 없으면 「Deploy hold 요청에 실패했습니다.」가 나온다. 계속 실패하면 배포 번호·요청 시각·상태·문구를 운영 담당자에게 전달한다.

## 배포 취소를 요청하려면 어디서 확인하는가
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 전체 행 메뉴 L934~1013
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 취소 처리 L236~242
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 취소 검사 L457~478
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 커밋된 작업 거부 L585~599
```

확인한 판본의 「배포」·「배포 이력」 행 메뉴에는 배포 자체를 취소하는 단추가 없다. 실행 중인 배포를 취소해야 하면 배포 번호·대상 설비·현재 상태·시각을 운영 담당자에게 전달한다.

서버 취소 처리는 진행 중인 배포에 한하며 이미 적용이 확정된 작업이 있으면 거부한다. 취소가 거부되었다면 거부 문구도 함께 전달하고 적용 결과를 확인한다. 「Hold」 접수와 배포 취소 완료를 구별한다.

## LOCAL 줄의 Deploy를 눌렀을 때 생기는 결과
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 서버 줄 식별 L301~317
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 로컬 처리 L496~519
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 로컬 메뉴 L998~1011
원천: nori-tc-ui-front/src/features/deploy/stores/deploy-ui.store.ts @ 9fe69a75dcd3 ; 로컬 복제 L410~467
```

「Deploy Name」 옆의 「SERVER」는 서버에 저장된 줄을 가리킨다. 「LOCAL」 줄의 메뉴에는 「View」·「Deploy」·「Delete」가 나오지만 「Deploy」는 브라우저 안에 READY인 복제 줄을 만든다.

「Deploy #번호 를 대기 상태 로컬 항목으로 다시 생성했습니다.」는 설비 실행 결과가 아니다. 실제 설비에 적용하려면 「등록」에서 서버 배포를 생성하고 「SERVER」 줄을 실행한다.

「선택한 Deploy 를 다시 실행할 수 없습니다.」이면 로컬 원본이나 대상 정보가 없는 경우이므로 「등록」에서 대상을 다시 고른다.

## View로 배포의 상세 결과 열기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 보기 단추 L822~853
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 이력 메뉴 L934~949
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 상세 화면 L329~386
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 작업·닫기 L416~479
```

「배포」 목록에서 줄을 누르면 위쪽 「현재 선택:」에 배포 번호와 상태가 나타난다. 띠의 「보기」 또는 행을 오른쪽 클릭한 「View」로 「Deploy View」를 연다.

「배포 이력」에는 「보기」 띠가 없으므로 행의 오른쪽 클릭 메뉴에서 「View」를 고른다.

실행 중인 서버 배포를 선택하면 「실시간 상태 동기화 중...」 또는 「실시간 상태를 반영하고 있습니다.」가 표시된다.

「Deploy View」는 「대상별 진행 현황」·「요약」·「작업」 순서다. 아래 내용은 창 안을 내려 확인하고 「닫기」로 창을 닫는다.

## 대상별 진행 현황에서 오류 설비와 단계를 찾기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 결과 표시 L42~67
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 대상 열 L229~317
원천: nori-tc-ui-front/src/features/deploy/lib/deploy-utils.ts @ 9fe69a75dcd3 ; 작업 표시 L56~102
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 열·작업 문구 L291~334
```

「대상별 진행 현황」에서 「EQP ID」로 설비를 찾고 「결과」·「상세 상태」·「현재 Step」·「Step 상태」·「오류 요약」·「갱신 시각」을 함께 읽는다. 「오류 요약」에는 오류 문장이 우선 나오고 없으면 코드, 둘 다 없으면 `-`가 나온다.

「작업」의 굵은 줄은 작업 종류, 아래 줄은 시나리오명이다. 시나리오명을 찾지 못하면 코드나 `-`가 나온다. 작업 연결이 없으면 「Register Deploy」 또는 「Deploy 작업」이 표시될 수 있다.

「결과」의 PASS는 COMPLETED 또는 NO_CHANGE에서 나온다. REPAIR_REQUIRED가 보이면 배포 번호·설비 ID·단계·오류·갱신 시각을 운영 담당자에게 전달한다.

## 요약과 작업 상자에서 요청 내용 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 요약 L379~413
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 요청 정보 L445~452
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 집계·시각 문구 L302~316
```

「요약」에는 전체 대상·완료·실패·보상 실패 대수가 나온다. 모델·Business JAR 정보와 서버의 결과 제목·「결과 코드」·결과 문구도 있는 값만 표시된다.

「작업」 상자 아래에는 「작업명」·「시나리오 코드」·「요청자」·「생성 시각」·「시작 시각」·「종료 시각」이 나온다. 시나리오 설명이 있으면 「시나리오 설명」도 표시된다.

실패를 문의할 때는 배포 번호와 이 요청 정보, 실패한 설비의 「오류 요약」을 함께 전달한다.

## AS-IS와 TO-BE에는 어떤 값이 표시되는가
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 비교 값 L174~227
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 비교 표시 L421~443
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 변경 전 정보 L275~326
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 여러 값 요약 L361~368
```

「작업」의 「AS-IS」에는 확인 가능한 통신 방식·모델·모델 버전·Business JAR·설비 파라미터 버전이 나온다. 설비별 값이 다르면 「대상별 상이:」로 묶일 수 있다. 저장된 변경 전 값이 없으면 현재 설비 정보로 채워질 수 있다.

「TO-BE」에는 배포에 표시된 모델·버전·Business JAR와 시나리오가 있는 값만 나온다. 「TO-BE」는 변경 예정 정보다.

AS-IS 자료가 없으면 「변경 전 상세 값을 확인할 수 없습니다.」, TO-BE 자료가 없으면 「변경 예정 정보가 없습니다.」가 나온다.

## 목록·상세 조회가 실패했을 때 다시 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 조회 오류 L446~464
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 오류 표시 L799~813
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 상세 오류 L119~124
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 상세 오류·닫기 L463~479
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 서버 문구 우선 L11~12
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 대체 문구 L264~271
```

조회 오류는 서버가 보낸 문구를 우선 표시한다. 서버 문구가 없으면 「Deploy 목록을 조회하지 못했습니다.」·「Deploy 정보를 조회하지 못했습니다.」·「Deploy 대상 상태를 조회하지 못했습니다.」 등의 대체 문구가 나온다.

목록은 「조회」로 다시 확인한다. 「Deploy View」의 「Deploy 상세 정보를 조회하지 못했습니다.」·「Deploy 실패 상세를 조회하지 못했습니다.」이면 「닫기」 뒤 같은 배포의 「View」를 다시 연다.

계속 실패하면 배포 번호·조회 또는 실패 시각·오류 문구를 운영 담당자에게 전달한다.

## 배포 이력의 Redeploy로 새 배포 만들기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 재실행 L496~552
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 확인창 L694~703
원천: nori-tc-ui-front/src/components/ui/confirm-dialog.tsx @ 9fe69a75dcd3 ; 기본 단추 L138~154
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 원본 재계획 L509~521
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 변경 없는 생성 L146~188
```

1. DEPLOY_ACTION이 있는 계정에서 「배포 이력」의 원본 줄을 오른쪽 클릭해 「Redeploy」를 고른다.
2. 「선택한 Deploy를 다시 실행하시겠습니까?」에서 「확인」을 누른다. 「취소」는 재실행 요청을 보내지 않고 창을 닫는다.
3. 생성 뒤 「배포」 카드로 전환되면 새 번호와 상태를 확인한다. READY이면 새 「SERVER」 줄의 「Deploy」로 실행한다.

서버는 원본 요청으로 현재 상태를 다시 계산한다. 실행할 변경이 모두 없으면 새 배포가 바로 COMPLETED로 끝나므로 「배포 이력」에서 확인한다.

## 재실행 요청이 거부되거나 실패했을 때
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 종료 검사 L509~521
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 설비 충돌 L761~775
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 오류 처리 L496~552
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 재실행 요청 L648~666
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 거부 문구 L275~280
```

「진행 중 Deploy 는 다시 실행할 수 없습니다.」 또는 서버의 「종료된 Deploy만 다시 실행할 수 있습니다.」이면 원본이 끝난 뒤 「배포 이력」에서 다시 확인한다.

다른 활성 배포가 같은 설비를 사용 중이면 새 배포 생성도 거부된다. 기존 배포 결과를 확인한다. 「재실행할 Deploy 요청 정보를 찾을 수 없습니다.」이면 원본 번호와 문구를 운영 담당자에게 전달한다.

그 밖의 실패는 서버 문구를 읽는다. 서버 문구가 없으면 「Deploy 재실행에 실패했습니다.」가 나온다. 원본 번호·요청 시각·문구를 함께 전달한다.

## 실행 전 배포나 끝난 이력 삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 삭제 조건 L79~89
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 삭제 요청 L555~618
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 삭제 확인창 L667~692
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 삭제 L689~718
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L287~289
```

DEPLOY_DELETE가 있는 계정에서 「배포」의 PLANNING·READY 줄 또는 「배포 이력」의 끝난 줄을 오른쪽 클릭해 「Delete」를 고른다.

「Deploy 를 삭제하시겠습니까?」 또는 「Deploy 이력을 삭제하시겠습니까?」에서 대상과 번호를 확인한다. 「이 작업은 되돌릴 수 없습니다.」라는 안내를 읽고 「삭제」로 확정한다. 「취소」는 창만 닫는다.

성공하면 줄이 목록에서 제거된다. 「LOCAL」 줄의 「Delete」는 브라우저 목록 항목을 제거한다.

## 삭제가 거부됐거나 배포 줄을 찾지 못할 때
```rag-meta
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 없는 줄·진행 중 거부 L555~569
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorPanel.tsx @ 9fe69a75dcd3 ; 삭제 오류 L596~610
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 삭제 API L670~691
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 삭제 거부 L689~701
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 오류 문구 L280~284
```

「실행 중 Deploy 는 삭제할 수 없습니다.」 또는 서버의 「실행 전 또는 종료된 Deploy만 삭제할 수 있습니다.」이면 RUNNING이 끝난 뒤 이력에서 삭제한다.

「선택한 Deploy 정보를 찾을 수 없습니다.」이면 목록을 다시 조회한다. 삭제 요청 실패는 서버 문구를 우선 표시하며 대체 문구는 「Deploy 이력 삭제에 실패했습니다.」 또는 「Deploy 삭제에 실패했습니다.」다.

계속 거부되면 배포 번호·삭제 시각·현재 상태·문구를 운영 담당자에게 전달한다.

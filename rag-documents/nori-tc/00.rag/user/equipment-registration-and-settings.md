# 설비 등록·설정 변경·삭제
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-common 6fe71f6b2da6·nori-tc-exec f1d5e0a8e727·nori-tc-gateway d3636ab86763 및 설비별 통신 설정 적용 수정 a5e5221853b7 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/equipment-model-parameter-and-jar-update.md
```

## 설비 작업에 필요한 권한 확인하기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 설비 화면 권한 L93~95
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L160~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비·라인 기본 권한 L123~158
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 결과 조회 기본 권한 L15~23
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 설비·결과 조회 권한 시드 L764~768
원천: nori-tc-exec/docs/db_table/patch/postgres_2026_06_02_add_line_read_and_layout_write_permission.sql @ f1d5e0a8e727 ; 라인 권한 시드 L14~23
원천: nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~27
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 권한으로 숨기지 않는 작업 메뉴 L180~191
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L357~368
```

설비 화면에 들어가려면 EQP_READ가 필요하다. 저장소 기본 권한 데이터 기준으로 등록·수정·삭제 요청에는 EQP_WRITE, 라인 목록에는 LINE_READ, 요청 뒤 결과 조회에는 ASYNC_READ가 필요하다.

설비 API의 조회는 EQP_READ·EQP_WRITE 중 하나로 허용되지만, 화면 진입에는 EQP_READ가 따로 필요하다.

등록·수정·삭제 메뉴는 EQP_WRITE가 없어도 표시되며, 실행하면 서버가 권한을 검사한다.

권한 거부가 나오면 입력값을 바꾸기 전에 운영 관리자에게 실행한 작업과 거부 문구를 전달해 권한을 확인한다. 현장 계정의 실제 부여 권한은 기본 데이터와 다를 수 있다.

## Gateway와 Business에서 작업 대상 찾기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인 범위 L151~155
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 설비 필터 L239~243
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색·그룹 펼침 L92~161
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 설비 선택·관점별 트리 L164~288
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관점·검색 문구 L33~54
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 4.1.1 절차 L479~488
```

1. 상단바에서 대상 라인을 고른 뒤 좌측 메뉴의 「설비」를 연다. 목록은 선택 라인에 속한 설비로 좁혀진다.
2. 설비를 등록하거나 고치려면 「Gateway」 카드를 누른다. 좌측 「설비 정보」에서 그룹을 펼치고 설비 ID를 누른다.
3. 「Eqp 검색」에는 설비 ID 일부를 입력한다. 영문 대소문자를 구별하지 않으며 검색 중에는 일치하는 설비가 있는 그룹만 펼쳐 보인다.

「Business」 카드는 참조 모델을 보는 관점이다. 좌측 제목은 「모델 정보」, 검색 칸은 「business_app 검색」으로 바뀌며 그룹 이름으로 검색한다. 설비 등록·수정·삭제 메뉴는 「Gateway」에서 연다.

## 목록 표와 한 대의 상세에서 확인할 값
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 열 L124~195
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 열 L111~154
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 목록·상세·높이 조절 L1186~1247
원천: nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 파라미터 표 제목 L130~141
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 표 제목 L48~50
```

「Gateway」 목록의 열은 「EQPID」·「Business App」·「Comm Interface」·「Comm Mode」·「Socket Protocol」·「Route Partition」·「IP」·「Port」·「Enabled」·「Model」·「Model Version」·「Gateway Jarfile」·「Business Jarfile」 순서다.

설비 한 대를 누르면 위쪽 「설비 정보」와 아래쪽 「설비 파라미터」가 열린다. 사이의 가로선을 끌어 높이를 조절한다.

한 대의 「설비 정보」에는 「Socket Protocol」 열이 없고 두 Jarfile 열은 「—」로 표시된다. Socket 설정과 JAR 파일은 「Gateway」 목록 표에서 확인한다. 오른쪽 열이 가려지면 표를 가로로 스크롤한다.

## 한 대의 설비 정보에서 모델이 —로 표시될 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 모델 조회 조건 L160~164
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 조회 결과 전달 L1221~1230
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpModelInfo.ts @ 9fe69a75dcd3 ; 모델 정보 조회 L9~16
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 모델 단건 API L389~402
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 모델 값 대체 표시 L147~151
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/ModelQueryController.java @ 985d17c604dd ; 연결 모델 부재 L56~67
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 보유 권한 검사 L160~174
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 모델 API 기본 권한 L771~774
원천: nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록의 모델 값 L179~183
```

한 대의 「설비 정보」의 「Model」·「Model Version」은 연결 모델이 없거나 모델 조회 결과가 없으면 「—」로 표시된다. 조회가 실패해도 별도 오류 문구는 나오지 않는다.

저장소 기본 권한 데이터 기준으로 MODEL_READ·MODEL_WRITE가 모두 없으면 모델 조회가 거부된다. 두 열이 「—」이면 운영 관리자에게 모델 조회 권한을 확인한다.

값은 「Gateway」 목록 표의 「Model」·「Model Version」에서도 확인한다. 목록 표는 모델 단건 조회와 별도로 설비 관리 상세의 모델 정보를 표시한다.

## Business의 모델 표가 비어 있을 때 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/BusinessGroupModelTable.tsx @ 9fe69a75dcd3 ; 모델 열 L73~85
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 모델 조회 L149~150
원천: nori-tc-ui-front/src/features/model/hooks/useModelList.ts @ 9fe69a75dcd3 ; useModelList L9~15
원천: nori-tc-ui-front/src/features/model/api/model.api.ts @ 9fe69a75dcd3 ; getModelList L176~194
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 조회 권한 L240~257
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 모델 권한 시드 L771~774
```

「Business」 표에서는 「Model Name」·「Model Version」·「status」·「description」·「updated_by」·「updated_at」을 확인한다.

저장소 기본 권한 데이터 기준으로 이 표의 모델 목록 조회에는 MODEL_READ·MODEL_WRITE 중 하나가 필요하다. 모델 표가 비어 있으면 선택 라인과 그룹을 확인하고, 모델 조회 권한도 운영 관리자에게 확인한다.

## Enabled 색 점과 그룹 아래 표시 읽기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 색 점 L54~108
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 그룹 표시 L289~345
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상태·그룹 표시 L590~598
```

「Enabled」의 점에 마우스를 올려 표시 문구를 읽는다. 초록은 「RUN」, 파랑은 「IDLE」, 노랑은 「주의 상태」, 회색은 「오프라인」으로 표시된다.

Gateway 그룹 아래에는 점유 Pod 이름을 표시한다. 대기 노드에서는 Pod 이름이 없으면 세션 식별자를 표시한다. 「미점유」가 나오면 점유 인스턴스가 없으므로 그룹과 설비 ID를 운영 담당자에게 전달한다.

「spare」는 살아 있으나 그룹을 점유하지 않은 대기 Pod 표시다. 점유 Pod이 빠지면 대기 Pod이 슬롯을 이어받는다는 안내를 마우스로 확인할 수 있다.

## 설비 목록이나 관리 상세를 불러오지 못했을 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 오류·다시 시도 L425~443
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 오류 문구 선택 L1063~1088
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 목록 오류 L349~354
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구·대체 문구 L19~77
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 조회 오류 L484~491
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 상세 오류·단추 L55~57
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 관리 상세 오류 대처 L698~698
```

Gateway 목록 아래에 「설비 상세 정보를 불러오지 못했습니다. 자동 재시도가 모두 실패했습니다.」가 나오면 옆의 「다시 시도」를 누른다.

좌측 목록의 조회 실패에는 서버 오류 문구가 우선 나오며, 문구가 없으면 「설비 목록을 조회하지 못했습니다.」가 나온다. 화면을 새로고침해 다시 확인한다.

수정 창에서 「설비 관리 상세 정보를 조회하지 못했습니다.」가 나오면 창을 닫고 다시 연다. 반복되면 라인·설비 ID·발생 시각·화면 문구를 운영 담당자에게 전달한다.

## 새 설비 등록 창 열기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 등록 메뉴 L274~279
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 통신 방식 선택 L357~368
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 창·스크롤 L817~839
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록 구역 구성 L971~1372
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 하단 단추 L1385~1403
```

1. 「Gateway」 관점의 좌측 트리에서 설비 노드가 없는 빈 곳을 마우스 오른쪽 단추로 누른다.
2. 「SECS Eqp Create」·「Socket Eqp Create」·「PLC Eqp Create」 중 등록할 통신 방식을 고른다.
3. 등록 창에서 「공통 정보」·「Model / Business」·「Log Policy」·통신 방식별 설정 구역을 아래로 스크롤하며 채운다.

「Save」와 「Cancel」은 창 아래쪽에 고정된다. 등록 요청 전 그만두려면 「Cancel」을 누른다.

## 공통 정보의 필수값 채우기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 필수 검사·요청 L635~678
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 공통 입력 L844~947
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpCreateRequest.java @ 985d17c604dd ; 필수 검사 L33~62
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 기본 스키마 길이·포트 제한 L403~434
```

| 입력 칸 | 등록·저장 조건 |
|---|---|
| EQP ID | 필수다. 저장소 기본 스키마 기준 64자 이내이며 등록 뒤 수정할 수 없다. |
| 라인 | 등록된 라인을 목록에서 고른다. |
| Comm Interface | 등록 메뉴에서 고른 방식으로 고정된다. |
| Comm Mode | ACTIVE·PASSIVE 중 고른다. PLC는 ACTIVE로 고정된다. |
| Route Partition | 0 이상 정수다. |
| EQP IP | 필수다. 저장소 기본 스키마 기준 45자 이내로 입력한다. |
| EQP Port | 필수 정수다. 저장소 기본 스키마 기준 1~65535로 입력한다. |

## 등록할 모델과 JAR 고르기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 모델 자동 선택 L552~582
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; JAR·모델 선택 L949~1061
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 모델 필터 L152~185
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델 검증 L84~105
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 모델 안내 L561~571
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 스위치 표시 L577~578
```

「Is Dev」를 「사용」으로 켜면 DEVELOP, 「미사용」으로 끄면 OPERATE 모델 중 통신 방식이 일치하는 모델만 나온다. 「허용 모델 상태」와 「선택 상태」로 조건을 확인한다.

「Model Name」·「Model Version」은 필수다. 조건에 맞는 첫 모델과 버전이 자동 선택될 수 있으므로 원하는 값인지 확인한다. 옵션이 없다는 안내가 나오면 「Is Dev」를 확인하거나 모델 담당자에게 모델 준비를 요청한다.

선택 사항인 「Gateway Jar」·「Business Jar」는 등록된 목록에서 고른다. 사용하지 않으면 「미선택」으로 둔다. 등록 창에는 파일 업로드 칸이 없으므로 필요한 JAR가 목록에 없으면 운영 담당자에게 등록을 요청한다.

## Log Policy 입력과 보관 일수 제한
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 로그 입력 L1064~1110
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; 로그 초기값 L19~26
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpLogJpaStore.java @ 6fe71f6b2da6 ; 보관 일수 검증 L142~148
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 로그 빈값 보완 L929~959
```

「Log Level」은 INFO·DEBUG·TRACE·WARN·ERROR 중 고른다. 등록 창의 초기값은 INFO다.

「Log Retention Day」는 정수로 입력하며 초기값은 7이다. 화면은 0 이상을 허용하지만 저장 단계는 1 이상을 요구하므로 보관 일수는 1 이상으로 입력한다.

「Log Path」는 로그 경로를 적는 선택 칸이다. 수정할 때 로그 보관 일수나 경로를 비우면 기존 값으로 보완된다.

## SECS Settings의 입력 범위 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; SECS 입력 L1112~1218
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 숫자 검사 L679~692
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/eqp/TcEqpHsmsJpaStore.java @ 6fe71f6b2da6 ; 저장 검증 L302~312
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Device ID 범위 L495~503
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 타이머 단위 변환 L56~69
```

SECS 설비의 「SECS Settings」에서 「Device ID」·「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」·「Link Test Interval」·「Max Message Bytes」를 입력한다. 「Link Test Enabled」는 「사용」·「미사용」으로 고른다.

숫자 칸은 선택 입력이다. 값을 넣으면 정수여야 한다. 「Device ID」는 저장소 기본 스키마 기준 0~32767, 나머지 숫자는 저장 단계에서 1 이상이어야 한다. 화면의 0 이상 검사만 통과해도 저장이 거부될 수 있다.

「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」과 「Link Test Interval」은 초, 「Max Message Bytes」는 바이트 단위로 입력한다.

## SECS 저장값과 실제 통신 설정 구분하기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; SECS 값 저장 L475~490
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ d3636ab86763 ; HSMS 저장값 복사 L439~451
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 ; HSMS 프로필 재구성 L274~287
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/HsmsRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 HSMS 세션 설정 L41~69
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/config/props/GatewayHsmsProperties.java @ d3636ab86763 ; toSessionDefaults L301~325
```

설비별 HSMS 설정 적용 수정이 들어간 판본에서는 「Device ID」와 저장된 「T3 Timeout」·「T5 Timeout」·「T6 Timeout」·「T7 Timeout」·「T8 Timeout」으로 세션 설정을 구성한다.

「Link Test Enabled」·「Link Test Interval」·「Max Message Bytes」도 세션의 LINKTEST·최대 프레임 설정으로 쓴다. 설비별 HSMS 설정이 없으면 Gateway 기본값을 쓴다.

각 타이머의 실제 사용 범위와 기본값 출처는 「SECS·HSMS 메시지·타이머」(`00.rag/user/secs-hsms-message-and-timer-reference.md`)에서 확인한다.

## Socket Settings에서 프로토콜과 숫자 채우기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 입력 L1269~1371
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Socket 숫자 검사 L694~708
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 프로토콜 필수 검사 L888~904
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; Socket 저장 제한 L506~529
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java @ d3636ab86763 ; 실행 프로필 공급 L25~31
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 프레임 상한 공급 L45~62
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java @ a5e5221853b7 ; 문자셋 검증 L55~64
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; LF 문자셋 조건 L54~63
```

Socket 설비는 「Socket Settings」의 「Socket Protocol Type」을 서버가 제공한 목록에서 반드시 고른다. 별표가 없어도 비워 저장하면 서버가 거부한다. 「Charset」에는 문자셋을 입력한다.

「Heartbeat Interval」·「Read Timeout」·「Write Timeout」은 선택 입력이며 값을 넣으면 0 이상 정수로 쓴다. 「Max Frame Size」는 저장소 기본 스키마 기준 1 이상 정수로, 바이트 단위로 쓴다.

「Heartbeat Enabled」·「Keep Alive Enabled」는 각각 「사용」·「미사용」으로 고른다.

## Charset 저장과 Gateway 통신 실패 구분하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Charset 자유 입력 L1293~1300
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; Charset 저장값 결정 L888~904
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/db/GatewayEquipmentInfo.java @ a5e5221853b7 ; 문자셋 이름 오류 L55~64
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; Socket 실행 문맥 생성 L38~62
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/types/lineDelimited/LineDelimitedSocketTypeHandler.java @ d3636ab86763 ; LF 문자셋 조건 L54~63
```

「Charset」은 자유 입력이다. 저장 서버는 문자셋 이름을 검사하지 않고 앞뒤 공백만 제거해 저장하며, 빈 값은 UTF-8로 저장한다.

설비별 Socket 설정 적용 수정이 들어간 판본의 Gateway는 지원하지 않는 문자셋 이름이면 그 설비의 Socket 실행 문맥을 만들 때 설비 ID와 설정값을 담은 오류로 실패한다.

내장 LINE_DELIMITED에는 LF가 단일 0x0A 바이트인 문자셋을 쓴다.

## Socket 저장값과 실제 통신 설정 구분하기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; Socket 값 저장 L517~530
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/SocketSubProfileLoader.java @ d3636ab86763 ; 실행 프로필 공급 L25~31
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/context/SocketRuntimeContextProvider.java @ a5e5221853b7 ; 설비별 Socket 설정 L45~62
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeRegistry.java @ a5e5221853b7 ; 내장 처리기 문자셋 적용 L51~63
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ a5e5221853b7 ; 플러그인 처리기 우선 L41~71
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/GatewayActiveReconnectCoordinator.java @ a5e5221853b7 ; ACTIVE Keep Alive 적용 L232~239
원천: nori-tc-gateway/gateway/adapter/tc-gateway-netty-adapter/src/main/java/com/nori/tc/gateway/adapters/netty/EqpBindingService.java @ a5e5221853b7 ; PASSIVE Keep Alive 적용 L299~307
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/context/model/EquipmentContextProfile.java @ d3636ab86763 ; SocketSettings 필드 L111~122
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java @ d3636ab86763 ; Socket 저장값 복사 L464~475
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 ; Socket 프로필 재구성 L289~301
```

설비별 Socket 설정 적용 수정이 들어간 판본은 「Socket Protocol Type」·「Max Frame Size」·「Keep Alive Enabled」를 실행에 반영한다. 「Charset」은 내장 LINE_DELIMITED·REGEX_DELIMITED 처리기에 반영하며 플러그인 처리기에는 전달하지 않는다.

「Heartbeat Enabled」·「Heartbeat Interval」·「Read Timeout」·「Write Timeout」은 같은 판본에서 저장만 되며 Gateway 실행 설정에 쓰이지 않는다.

문자셋 적용 범위와 프레임 상한의 상세는 「Socket 프레임·파싱 규칙」(`00.rag/user/socket-framing-and-parsing-reference.md`)에서 확인한다.

## PLC Settings에서 변경할 수 있는 값
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; PLC 입력 L1220~1267
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 고정·초기값 L14~18
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; PLC 초기값 L52~58
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; PLC 검증 L162~190
```

PLC 설비의 「Comm Mode」는 ACTIVE, 「PLC Settings」의 「PLC Protocol」은 MODBUS_TCP, 「Transport」는 TCP로 고정된다.

선택 입력인 「Unit ID」는 0~255 정수, 「Poll Timeout (ms)」와 「Reconnect Max Retries」는 1 이상 정수로 넣는다. 등록 창의 초기값은 각각 1·3000·3이다.

## 통신 설정의 숫자 칸을 비우면 적용되는 값
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 빈 숫자 처리 L239~258
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 통신값 요청 L763~806
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 통신 기본값 L871~926
```

통신 설정의 빈 숫자 칸은 등록과 수정 모두 기본값으로 채워진다. 수정 중 기존 통신값을 유지하려면 숫자 칸을 지우지 않는다.

| 통신 구역 | 빈칸 기본값 |
|---|---|
| SECS Settings | Device ID 0, T3 Timeout 45, T5 Timeout 10, T6 Timeout 5, T7 Timeout 10, T8 Timeout 5, Link Test Interval 60, Max Message Bytes 10485760 |
| Socket Settings | Heartbeat Interval 30, Read Timeout 0, Write Timeout 0, Max Frame Size 8192 |
| PLC Settings | Unit ID 1, Poll Timeout (ms) 3000, Reconnect Max Retries 3 |

## 등록 요청을 저장하고 완료 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 저장·오류·진행 L1374~1403
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 결과 판정 L138~196
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 등록 뒤 선택 L743~755
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록 결과 문구 L1121~1126
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; create L260~269
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; createEqp L577~599
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 완료 메시지·확인 단추 L94~127
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 선택 라인 필터 L239~243
```

1. 등록 창의 값을 확인한 뒤 「Save」를 누른다. 입력 오류가 나오면 창 안의 문구가 가리키는 칸을 고친다.
2. 요청이 접수되면 창 안의 진행 표시를 보며 기다린다.
3. 「설비 등록 완료」와 대상 설비 ID를 확인하고 「확인」을 누른다. 등록 성공 뒤에는 새 설비가 선택된다.

등록한 라인이 현재 상단바의 선택 라인과 다르면 그 라인으로 바꿔 트리를 확인한다.

## Eqp Info Update에서 설정 바꾸기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 수정 메뉴 L180~190
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 수정 요청 L729~809
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 식별자 읽기 전용 L844~880
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 수정 호출 L764~770
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 결과 L1127~1132
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 필수값 L25~58
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; updateEqp L602~621
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; update L280~296
```

1. 「Gateway」 좌측 트리의 대상 설비 ID를 마우스 오른쪽 단추로 눌러 「Eqp Info Update」를 고른다.
2. 현재 값이 열린 뒤 「라인」·「Comm Mode」·「Is Dev」·「Route Partition」·「EQP IP」·「EQP Port」와 로그·통신 설정을 고친다. 「EQP ID」·「Comm Interface」는 읽기 전용이다.
3. 「Save」를 누르고 진행 표시 뒤 「설비 수정 완료」를 확인한다. 저장 전 취소는 「Cancel」을 누른다.

수정에서도 라인·연결 모드·주소·포트·배정 번호는 필수다. 다른 라인으로 옮겼으면 상단바에서 옮긴 라인을 골라 설비를 확인한다.

## 수정 창에서 모델과 JAR를 바꾸려는 경우
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 등록에서만 모델 구역 표시 L971~1062
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Gateway Jar 선택 L949~967
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd ; 기존 연결값 유지 L44~65
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpUpdateRequest.java @ 985d17c604dd ; 수정 요청 항목 L25~58
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 모델·Business JAR 배포 입력 L354~373
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployEqpFinalizationService.java @ 985d17c604dd ; 배포 성공값 반영 L25~76
```

「Eqp Info Update」에는 「Model / Business」 구역이 표시되지 않아 「Model Name」·「Model Version」·「Business Jar」를 고르는 칸이 없다.

「Gateway Jar」는 목록에서 다른 값을 고를 수 있지만 이 수정 요청은 기존 Gateway JAR를 유지한다. 모델·Business JAR 변경은 별도의 배포 작업으로 진행하고, Gateway JAR 변경은 운영 담당자에게 대상 설비와 필요한 파일을 전달한다.

## 서버가 등록·수정을 거부할 때 확인할 조건
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpManagementCommandValidator.java @ 985d17c604dd ; 모델·중복·존재 검사 L84~117
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; 라인 검사 L416~422
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; JAR 존재 검사 L988~997
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L31~41
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 메시지 L11~12
```

등록에서는 같은 설비 ID가 이미 있거나, 모델이 없거나 통신 방식·개발/운영 조건이 맞지 않으면 서버가 거부한다. 라인과 선택한 JAR도 서버에 등록되어 있어야 한다.

오류에 중복 설비가 나오면 기존 설비를 찾아 확인한다. 라인·모델·JAR를 찾지 못한다는 안내이면 목록을 다시 불러와 고른다. 필요한 항목이 없으면 운영 담당자에게 준비를 요청한다.

입력·등록 항목 오류는 서버 문구를 입력 창에 표시한다. 수정 시 기존 모델·JAR의 문제처럼 창에서 고칠 수 없는 거부이면 설비 ID·발생 시각·문구를 운영 담당자에게 전달한다.

## PASSIVE 주소를 함께 쓰는 설비의 배정 충돌
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPort.java @ 985d17c604dd ; PASSIVE 검사 L425~459
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/EqpManagementDbSupport.java @ 985d17c604dd ; 같은 주소·그룹 판정 L342~392
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaEqpCrudPortTest.java @ 985d17c604dd ; 충돌 시 저장 차단 기대값 L974~1059
```

PASSIVE 설비는 같은 통신 방식과 IP·Port를 쓰는 다른 설비와 「Route Partition」 배정이 충돌하면 저장이 거부될 수 있다. Socket에서는 「Socket Protocol Type」도 같은 경우를 검사한다.

배정 충돌 안내에 상대 설비 ID가 나오면 대상과 상대 설비의 「EQP IP」·「EQP Port」·「Route Partition」을 확인한다. 배정값을 정할 수 없으면 두 설비 ID와 입력값·오류 문구를 운영 담당자에게 전달한다.

## Eqp Delete에서 대상 확인하고 삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 삭제 메뉴 L180~191
원천: nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·단추 L49~86
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 삭제 후 선택 정리 L743~755
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 삭제 성공 반영 L148~161
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpMutations.ts @ 9fe69a75dcd3 ; 삭제 성공 뒤 목록 갱신 L78~84
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; deleteEqp L627~647
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; delete L305~312
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 제목 L79~80
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 경고 L617~620
```

1. 「Gateway」 좌측 트리에서 삭제할 설비 ID를 마우스 오른쪽 단추로 눌러 「Eqp Delete」를 고른다.
2. 「Eqp Delete」 창에 나온 설비 ID와 경고를 읽는다. 창은 삭제 전 설비 실행을 종료하려고 시도하며 실패하면 삭제가 중단된다고 안내한다.
3. 삭제하려면 「Delete」, 요청 전 취소하려면 「Cancel」을 누른다.
4. 진행 표시가 끝난 뒤 「설비 삭제 완료」를 확인한다. 성공하면 목록이 갱신되어 대상 설비가 사라진다.

## 등록·수정·삭제 결과가 지연되거나 중단됐을 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 작업별 결과·재조회 L31~49
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 결과 단추 L119~127
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 결과 본문 L28~40
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연·중단 문구 L1110~1134
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 다른 작업·배포와 충돌 L117~135
```

「설비 등록 확인 지연」·「설비 수정 확인 지연」·「설비 삭제 확인 지연」이 나오면 「작업 ID」가 있는 결과 창의 「다시 조회」를 누른다. 같은 등록·수정·삭제를 새로 요청하기 전에 현재 결과를 확인한다.

확인 지연 창에 「작업 ID」와 「다시 조회」가 없으면 잠시 후 화면을 새로고침해 결과를 확인한다.

작업 제목에 「중단 — 운영 확인 필요」가 나오면 운영 담당자에게 설비 ID·발생 시각·실행한 작업·결과 문구·「작업 ID」를 전달한다.

배포나 다른 작업이 진행 중이라는 거부 문구이면 먼저 그 작업의 결과를 확인한다. 배포 완료 후 다시 시도하고, 미종결 작업이 차단한다는 안내가 계속되면 운영 담당자에게 전달한다.

## 설비 등록·수정·삭제 실패 창에서 읽고 전달할 정보
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpMutationCommands.ts @ 9fe69a75dcd3 ; 실패 결과 전달 L189~196
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 실패 제목·기본 문구 L31~72
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; 본문 문구 선택·결과 단추 L94~127
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 문구·작업 ID 표시 L28~40
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 등록·수정 실패 문구 L1121~1132
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 실패 제목 L79~80
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 실패 기본 문구 L620~620
```

「설비 등록 실패」·「설비 수정 실패」·「설비 삭제 실패」가 나오면 결과 본문의 서버 문구와 표시된 「작업 ID」를 읽는다. 서버 문구가 전달되지 않으면 설비 ID가 포함된 기본 실패 문구가 나온다.

실패 창에는 「다시 조회」가 없고 「확인」만 있다. 「확인」으로 닫기 전에 설비 ID·발생 시각·실행한 작업·결과 문구·표시된 「작업 ID」를 기록해 운영 담당자에게 전달한다.

## 같은 요청 키나 기준 상태 변경으로 거부됐을 때
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 요청 키 충돌 L67~77
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationAcceptanceService.java @ 985d17c604dd ; 기준 상태 충돌 L132~135
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-acceptance-error.ts @ 9fe69a75dcd3 ; 접수 거부 문구 선택 L31~41
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 요청 키 충돌 안내 L1109~1109
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Cancel 단추 L1385~1394
원천: nori-tc-ui-front/src/features/eqp/components/EqpDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 창 Cancel 단추 L67~75
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관리 상세 조회 조건 L388~403
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 관리 창 닫기·다시 열기 L474~487
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpManageDetail.ts @ 9fe69a75dcd3 ; 관리 상세 재조회 L9~16
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; getEqpManageDetail L290~305
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; 조회값으로 입력 갱신 L541~550
```

「같은 요청 키로 다른 내용이 접수되어 거부되었습니다.」로 시작하는 안내나 「요청이 기준으로 삼은 상태가 이미 바뀌었습니다.」가 나오면 같은 등록·수정·삭제를 다시 요청하기 전에 현재 값을 확인한다.

1. 입력 창의 「Cancel」을 누르고 「Gateway」 트리에서 대상 설비 ID가 있는지 확인한다.
2. 설비 ID가 있으면 「Eqp Info Update」를 다시 연다. 현재 값 조회가 끝난 뒤 요청 전에 고친 값과 비교한다.

대상이 없거나 현재 값을 읽지 못하면 설비 ID·발생 시각·실행한 작업·거부 문구를 운영 담당자에게 전달한다.

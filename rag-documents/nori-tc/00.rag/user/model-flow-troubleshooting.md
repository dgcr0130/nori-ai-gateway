# 모델·Flow 처리
```rag-meta
유형: 트러블슈팅
적용: nori-tc-business 7db24f7466bd·nori-tc-ui-backend 985d17c604dd·nori-tc-ui-front 9fe69a75dcd3·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/model-details-and-flow-editing.md
```

## 수집 보고가 계산 규칙 오류로 멈췄는지 구별하기
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd ; resolveFinalValues·applyCalculationRule L78~166
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/action/eqp/DatacollTcAction.java @ 7db24f7466bd ; reportDatacoll L144~168
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; handleFailure L692~714
원천: nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/datacoll/DcopCollectionEngineTest.java @ 7db24f7466bd ; resolveFinalValues_whenCalculationRuleNameIsUnknown_shouldFailClosed L202~219
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 설비·사유·시각 열 L18~38
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 표 열 L89~103
원천: nori-tc-ui-front/src/features/model/types/model.types.ts @ 9fe69a75dcd3 ; Dcop Itemes 표시 L307~320
```

계산 규칙 실패 처리 수정이 포함된 판본에서는 Calculation Rule 적용 중 예외가 나면 변환 전 값으로 보고를 계속하지 않고 실패를 전파한다. 재시도·최종 실패 처리는 업무 실행의 실패 정책을 따른다.

보고가 멈추면 「Business DLQ」의 해당 설비·시각 기록을 확인하고, 운영 담당자에게 예외 원인에 수집 항목과 계산 규칙이 남았는지 확인을 요청한다. 조회 절차는 `00.rag/user/dlq-quarantine-and-repair-task.md`를 따른다.

계산 규칙 오류로 확인되면 모델 담당자에게 모델·버전, 「Dcop Itemes」의 「Dcop Item Name」·「Calculation Rule」, 실패 시각과 사유를 전달한다.

## Calculation Rule이 있는데 값이 변하지 않을 때
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd ; 계산 적용 조건·반환 L89~165
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; applyTransform L66~87
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/workflow/internal/support/BusinessTransformSupport.java @ 7db24f7466bd ; transformAddSub L264~274
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 표 열 L89~103
```

수집값이 그대로이면 「Dcop Itemes」의 「Calculation Rule」과 변환 전 값을 함께 확인한다. 계산 규칙이 비어 있거나 공백뿐이면 계산을 적용하지 않는다.

지원하지 않는 함수명은 예외를 내지만, add·sub는 값이나 인자를 숫자로 읽지 못하면 원래 값을 반환한다. toint·tolong의 숫자 변환 결과가 없을 때도 최종값 결정 과정에서 원래 값을 유지한다.

따라서 값이 그대로인 증상은 함수명 오류와 숫자 입력 문제로 나누어 확인한다. 모델 담당자에게 규칙 원문·입력값·기대한 결과·실제 결과를 전달한다.

## Collect가 비어 수집 항목이 빠질 때
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Collect 표시 L432~463
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; Collect 안내 L810~814
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd ; collectIfAny L98~120
원천: nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/datacoll/DcopAutoCollectorTest.java @ 7db24f7466bd ; shouldSkipWhenJoinTableHasNoBinding L61~69
```

Flow 카드의 「Collect」에 「수집 항목이 없습니다.」가 보이면 그 Flow에 필요한 항목이 연결됐는지 확인한다. 자동 수집은 현재 Flow의 연결을 조회하며, 연결이 없으면 수집을 건너뛴다.

「Dcop Itemes」에 정의가 있는지와 「Collect」에 연결이 있는지를 나누어 확인한다. 정의만 있는 상태를 계산 규칙 실패로 판단하지 않는다.

연결이 빠졌으면 모델 담당자가 `00.rag/user/model-details-and-flow-editing.md`의 Collect 편집 절차로 보완한다. 항목·모델 버전·Flow를 함께 전달한다.

## Collect는 있는데 값이 비거나 갱신되지 않을 때
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd ; collectIfAny·updateLegacyState L111~205
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopAutoCollector.java @ 7db24f7466bd ; collectSingleItem·updateCollectionState L254~297
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/datacoll/DcopCollectionEngine.java @ 7db24f7466bd ; 미수집 항목 L78~86
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; DCOP 표 열 L89~103
```

Collect 연결이 있어도 대응하는 수신값을 찾지 못하면 수집을 건너뛴다. AVERAGE는 숫자로 읽지 못한 수신값을 집계하지 않는다. 수집 상태에 없는 항목의 최종값은 빈 문자열이다.

모델 담당자에게 「Dcop Item Name」·「VariableId」·「Collection Rule」과 실제 수신값의 대조를 요청한다. 빈 값만으로 Calculation Rule 오류를 확정하지 않는다.

연결과 입력이 맞는데도 갱신되지 않으면 설비 ID·발생 시각·작업 식별값과 모델·버전을 운영 담당자에게 전달한다. 담당자는 처리한 Flow와 수집 상태·작업 정보의 존재를 확인한다.

## 버전을 바꾼 뒤 Collect만 달라졌을 때
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementDetailSupport.java @ 985d17c604dd ; cloneSnapshotToVersion·cloneMessageFlowDcopItems L159~225
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelManagementPort.java @ 985d17c604dd ; 버전 복제 호출 L263~461
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/test/java/com/nori/tc/ui/adapter/db/JpaModelManagementPortTest.java @ 985d17c604dd ; createBranchModelClonesMessageFlowDcopBindings L220~255
원천: nori-tc-ui-front/src/features/model/components/FlowCard.tsx @ 9fe69a75dcd3 ; Flow Name·Message Name L274~317
```

Collect 승계 수정이 포함된 판본은 branch 생성·Check Out·Check In·Parent Model Commit에서 복제 원본의 Flow와 수집 항목 연결을 새 버전에 옮긴다.

버전 전환 뒤 수집만 빠졌으면 원본 버전과 대상 버전에서 같은 Flow의 「Collect」를 비교한다. 「Flow Name」·「Message Name」·연결 항목과 두 버전을 모델 담당자에게 전달한다.

원본에는 연결이 있고 대상에는 없으면 운영 담당자에게 Collect 승계 수정의 현장 판본 포함 여부와 복제 결과 확인을 요청한다. 버전 관리 조작은 `00.rag/user/model-registration-and-branching.md`를 따른다.

## Collect 변경이 체크인 비교에 나타나지 않을 때
```rag-meta
원천: nori-tc-ui-front/src/features/model/components/ModelPage.tsx @ 9fe69a75dcd3 ; handleRequestCheckIn L1043~1078
원천: nori-tc-ui-front/src/features/model/components/ModelCheckInDiffModal.tsx @ 9fe69a75dcd3 ; 동적 열·변경값 L125~175
원천: nori-tc-ui-front/src/features/model/lib/model-diff.util.ts @ 9fe69a75dcd3 ; computeNodeDiff·buildCheckInDiffSections L38~99
원천: nori-tc-ui-front/src/features/model/components/ModelCheckInDiffModal.tsx @ 9fe69a75dcd3 ; 비교 창 L223~283
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; diffTitle L695~698
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; workflow 응답 L192~207
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/ModelDetailResponseSupport.java @ 985d17c604dd ; Collect L378~405
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaModelDetailQueryPort.java @ 985d17c604dd ; Collect 항목 정렬 L234~244
```

Collect 비교 표시 수정이 포함된 판본에서는 이전 확정 버전과 저장된 EDIT의 연결 항목 차이가 「변경사항 미리보기」의 「Collect」열에 포함된다. 항목은 이름순으로 정렬하므로 선택 순서만 바꾼 것은 연결 항목 차이가 아니다.

연결을 바꿨는데 비교가 없으면 현재 EDIT와 비교 기준 버전의 같은 Flow에 저장된 연결 항목을 대조한다. 비교는 서버 조회값을 사용한다.

연결 항목이 실제로 다른데 표시되지 않으면 두 버전·Flow·Collect 표시값을 운영 담당자에게 넘겨 비교 표시 수정의 포함 여부를 확인한다. 저장·체크인 순서는 `00.rag/user/model-details-and-flow-editing.md`를 따른다.

## 같은 설비의 시작·종료 순서가 뒤바뀐 것처럼 보일 때
```rag-meta
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; processTask L136~153
원천: nori-tc-business/business/tc-business-core/src/main/java/com/nori/tc/business/core/runtime/BusinessRuntimeTaskSupport.java @ 7db24f7466bd ; awaitRetryBackoff L367~399
원천: nori-tc-business/business/tc-business-core/src/test/java/com/nori/tc/business/core/runtime/BusinessRuntimeEngineTest.java @ 7db24f7466bd ; shouldNotLetNextEventOvertakeRetryOfSameEquipment L629~694
원천: nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 ; 메시지 위치·사유 열 L18~38
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 위치 열 문구 L1066~1076
```

지연 재시도 순서 수정이 포함된 판본은 같은 설비의 선행 이벤트를 대기 후 그 자리에서 재시도한다. 뒤에 대기 중인 이벤트가 선행 이벤트의 재시도를 추월하지 않게 처리한다.

시작·종료 순서가 이상하면 설비 ID와 두 이벤트의 식별값·관측 시각·실제 보인 순서를 기록한다. 실패 기록이 있으면 「Business DLQ」의 「토픽」·「파티션」·「오프셋」·「사유」도 함께 전달한다.

운영 담당자에게 수신 순서와 재시도 실행 순서, 지연 재시도 수정의 현장 판본 포함 여부를 대조하도록 요청한다. 이 보장은 같은 설비의 대기열 안 재시도 순서에 한정한다.

## 모델 조회·저장 거부를 처리 실패와 구별하기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 모델 API 권한 L240~256
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 기본 권한 시드 L771~774
```

모델 상세 조회나 저장 요청이 권한 거부로 끝나면 계산·수집 실패 조사와 나누어 처리한다. 저장소 기본 API 권한 데이터에서 모델 조회는 MODEL_READ·MODEL_WRITE 중 하나, 변경은 MODEL_WRITE로 연결된다.

권한 목록에 맞는 요청이 없거나 권한 정보를 확인할 수 없는 상태도 서버가 차단한다. 거부 문구만으로 수집 규칙을 바꾸지 않고, 운영 관리자에게 누른 단추·모델·버전·시각·전체 오류 문구를 전달해 접근 권한 확인을 요청한다.

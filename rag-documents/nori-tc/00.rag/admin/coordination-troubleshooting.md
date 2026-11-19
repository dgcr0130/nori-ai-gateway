# 조정 작업의 보상 미확정·원장 후퇴 진단
```rag-meta
유형: 트러블슈팅
적용: nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-common 6fe71f6b2da6·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/admin/coordination-ownership-and-recovery-check.md
```

## 보상이 시작되지 않은 경우와 원 작업 확정 실패를 가르기
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; issueFor·issue L70~144
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; closeCompensatedOperation L578~592
```

운영 담당자는 복구 필요 상태가 남은 원 작업에 대해 Backend 로그의 보상 발급 실패, 보상 작업의 진행 상태, 원 작업 확정 거부 순서로 확인한다. 보상 ID가 확보되면 원 작업과 보상 작업을 구별해 조회한다.

작업 조회·권한·정상 복구 확인은 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다. 진단 결과의 공통 인계 항목은 `00.rag/admin/incident-evidence-handover.md`를 따른다.

## 보상 발급 기록이 없을 때 어느 실패 로그를 찾는가
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd ; 발급 거부·예외 L70~139
```

자동 보상 대상 사유인데 보상 발급을 확인하지 못하면 원 작업 ID로 Backend 로그를 찾는다. 아래 구별 결과를 조정 담당자에게 넘긴다.

| 로그 | 구별할 원인·다음 확인 |
|---|---|
| UI_COMPENSATION_ISSUE_FAILED | 발급 중 예외다. 같은 로그의 예외 원문을 조사한다. |
| UI_COMPENSATION_DIGEST_CONFLICT | 같은 보상 ID의 내용 충돌이다. 원 작업·보상 ID를 대조한다. |
| UI_COMPENSATION_CANDIDATE_REJECTED | 보상 후보 발급 거부다. code·conflict를 확인한다. |
| UI_COMPENSATION_SKIPPED_NESTED | 실패한 작업 자체가 보상이다. 추가 자동 보상 대신 수동 복구로 인계한다. |

## 보상 작업이 진행 중이면 어느 단계에서 멈췄는가
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 참여자 대기 L223~227
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; DB 확정·장벽 개방 L528~564
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 진행 필드 L33~52
```

보상 작업이 PENDING이면 관리 조회의 phase·dbFinalized·participantProofs와 같은 보상 ID의 Backend 로그를 대조한다. UI_OPERATION_NO_PARTICIPANT는 처리 가능한 세션이 없어 준비를 기다리는 경로다.

FINALIZING에서 UI_OPERATION_DB_FINALIZE_PROOF_REJECTED가 나오면 DB 확정 증적 기록의 code를, UI_OPERATION_BARRIER_OPEN_REJECTED가 나오면 장벽 개방의 code를 확인한다. 원 작업 확정 거부와는 다른 단계다.

DB_STATE_UNEXPECTED로 복구 필요 상태가 되면 DB가 예상 이전 상태나 목표 상태에 맞지 않아 덮어쓰기를 멈춘 경우다. DB 대조를 조정·DB 담당자에게 넘긴다.

## 원 작업과 보상 작업의 연결이 다르다고 거부될 때
```rag-meta
원천: nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 ; 대상·revision 검사 L29~40
원천: nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 ; 작업 키 L60~68
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd ; 공개 필드 L33~52
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 확정 거부 로그 L578~592
```

UI_OPERATION_ROLLBACK_NOT_CONFIRMED의 code가 아래 값이면 대상 연결을 대조한다.

| code | 확인할 값 |
|---|---|
| COMPENSATION_TARGET_MISMATCH | 보상의 compensatesOperationId와 원 작업 ID |
| COMPENSATION_REVISION_MISMATCH | 보상의 compensatesRevision과 원 작업의 desiredRevision |

compensatesOperationId는 관리 API 응답에 없다. Redis 조회 권한이 있는 담당자가 `tc:coord:v1:operation:{보상 작업 ID}` 해시에서 읽는다. 실제 보상 ID로 바꾸어 조회하고, 연결이 어긋난 두 작업을 조정 담당자에게 넘긴다.

## 보상 완료 확정의 전제조건이 거부되면 무엇을 대조하는가
```rag-meta
원천: nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 ; epoch·존재·상태 검사 L10~58
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 확정 거부 로그 L578~592
```

확정 거부 로그의 code와 당시 두 작업 상태를 대조해 조정 담당자에게 넘긴다.

| code | 확인 대상 |
|---|---|
| EPOCH_MISMATCH | Redis cluster epoch의 존재와 실행기가 사용한 epoch의 일치 여부 |
| NOT_FOUND | 원 작업 원장의 존재 |
| COMPENSATION_NOT_FOUND | 보상 작업 원장의 존재 |
| COMPENSATION_NOT_PASSED | 보상 작업의 status가 PASS인지 |
| NOT_REPAIRABLE | 원 작업의 status가 REPAIR_REQUIRED인지 |
| NOT_COMPENSATING | 원 작업의 phase가 COMPENSATING인지 |

같은 보상·대상의 FAIL·ROLLED_BACK 재확인은 OK다. 원장 상태·epoch를 직접 덮어쓰지 않는다.

## 보상 PASS 뒤 원 작업이 남고 확정 거부 로그도 없을 때
```rag-meta
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 종결 작업 제외 L151~158
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; PASS 뒤 원 작업 확정 L554~592
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd ; 실행 예외 로그 L65~72
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationView.java @ 985d17c604dd ; 종결 상태 L71~74
```

b8faaead 수정이 포함된 판본에서는 보상의 PASS 직후 compensatesOperationId가 있을 때 원 작업 확정을 호출한다. 보상 ID의 UI_OPERATION_PASS 뒤 UI_OPERATION_ADVANCE_FAILED와 예외 원문이 있는지 확인한다. 확정 호출이 예외로 끝나면 거부 코드 로그까지 도달하지 못할 수 있다.

보상이 PASS인 뒤에는 실행기가 종결 작업을 건너뛴다. 다음 주기나 재시작이 원 작업 확정을 다시 해 준다고 기대하지 않고, 보상의 대상 연결 값·예외·적용 판본을 조정 담당자에게 넘긴다.

## committed overlay가 없는데 왜 후퇴로 차단되는가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; overlay 부재·후퇴 검사 L77~114
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayFinalizedRevisionDbAdapter.java @ d3636ab86763 ; DB 조회 L14~19 L41~47
원천: nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicyTest.java @ d3636ab86763 ; 원장 유실 회귀 L115~129
```

62154da0 수정이 포함된 판본에서 GW_LEDGER_REGRESSION의 redisCommittedRevision이 `(없음)`이면 DB의 같은 설비 `tc_eqp.last_finalized_revision`이 양수인지 대조한다. 양수이면 원장 유실·후퇴로 판정한 경로다. 특정 유실 원인까지 이 로그가 증명하지는 않는다.

redisCommittedRevision이 숫자이면 dbFinalizedRevision보다 낮은지 비교한다. 두 분기의 값을 구별해 조정·DB 담당자에게 넘긴다. 기본 복구 확인 절차는 `00.rag/admin/coordination-ownership-and-recovery-check.md`를 따른다.

## 설비 적재 생략이 모두 원장 후퇴를 뜻하는가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 생성 중·삭제·조회 L79~134 L143~150
원천: nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicyTest.java @ d3636ab86763 ; 조회 실패·생성 중 기대값 L132~165
```

Gateway가 설비를 적재하지 않으면 같은 설비의 기동 로그를 먼저 구별한다.

| 관측 | 다음 확인 |
|---|---|
| GW_OVERLAY_UNCOMMITTED_CREATE | committed overlay와 양수 DB 확정 이력이 없고 같은 설비의 미종결 작업이 있는 경로다. 생성 작업 진행을 조회한다. |
| GW_OVERLAY_TOMBSTONE | 삭제 표시가 있는 확정 자료다. 삭제 작업과 revision을 대조한다. |
| overlay 조회 예외 | 조회 실패를 후퇴 판정과 구별하고 Redis 접근 예외를 조사한다. |

overlay·양수 DB 확정 이력·같은 설비의 미종결 작업이 모두 없으면 DB 상태로 적재하는 경로다. 조정 담당자는 적재 생략이라는 결과만으로 원장 유실을 확정하지 않는다.

## 후퇴 차단이 예상과 다르면 DB 확정 이력이 읽혔는가
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; 이력 없는 비교 L109~118
원천: nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayFinalizedRevisionDbAdapter.java @ d3636ab86763 ; DB 값 공급 L36~47
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 이력 기록 실패 L595~607
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd ; 기록 결과 대조 L48~62
```

committed overlay가 있는데 GW_LEDGER_REGRESSION_CHECK_INERT가 나오면 DB 확정 이력을 읽지 못해 후퇴 비교가 동작하지 않은 경로다. GW_LEDGER_REGRESSION_CHECK_ARMED는 DB 조회 어댑터 생성 로그이므로 설비별 이력 존재도 따로 확인한다.

조정·DB 담당자는 같은 설비의 `tc_eqp.last_finalized_revision`과 Backend의 UI_FINALIZED_REVISION_NOT_RECORDED를 대조한다. 후자는 확정 revision 기록 실패를 알리는 로그다. 경고 문구만으로 컬럼 부재를 확정하지 않고 실제 행·기록 경로를 확인한다.

## 현장 판본에 두 수정이 포함됐는지 확인하기
```rag-meta
원천: nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 지정 L44~47
원천: nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 ; 이미지 지정 L40~43
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd ; 보상 확정 연결 L554~592
원천: nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 ; overlay 부재 차단 L77~92
```

배포 담당자는 Backend와 Gateway에 적용한 이미지를 각각 빌드 기록의 소스 커밋과 연결한다. 두 Helm chart는 app.imageTag를 이미지 태그에 사용한다. 태그 문자열만으로 수정 포함 여부를 판정하지 않는다.

Backend는 b8faaead의 보상 완료 확정 연결, Gateway는 62154da0의 overlay 부재 후퇴 검사가 포함됐는지 확인한다. 배포 변경 절차는 `00.rag/admin/upgrade-rollback-and-configuration-apply.md`를 따른다. 판본 대조 결과를 장애 진단과 함께 인계한다.

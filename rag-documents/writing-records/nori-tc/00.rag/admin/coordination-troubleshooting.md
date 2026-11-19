# 조정 작업 문서 작성 기록

## 대상

- objectKey: `00.rag/admin/coordination-troubleshooting.md`
- 구획: admin, 필요한 권한 `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/admin/coordination-troubleshooting.md`
- 검색 원문 SHA-256: `8591fbd417b67d671a1f0f91fabf40cad89e9f4ea254affab08a19d6980b1eac`
- 검색 원문 줄 수: 144, 절 수: 10, 바이트 수: 12378
- 확인일: 2026-11-19(한국 시간)
- 독립 작성 세션에서 직접 조사·작성·자체검사했다. 다른 Agent 호출, 업로드, 접속, 제품 코드 변경, git add·commit·push는 하지 않았다. 메인 세션의 원천 대조 검증은 별개다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-gateway | d3636ab86763 | 본문에 인용한 정책·DB 어댑터·회귀 시험 파일의 status 출력 없음 |
| nori-tc-ui-backend | 985d17c604dd | 본문에 인용한 실행기·발급기·조회 DTO·재조정기·상태 뷰·DB 어댑터의 status 출력 없음 |
| nori-tc-common | 6fe71f6b2da6 | 본문에 인용한 Lua·작업 키 정의 파일의 status 출력 없음 |
| nori-tc-exec | f1d5e0a8e727 | 본문에 인용한 두 배포 chart의 status 출력 없음 |

쓰기 전 `git -c safe.directory=<저장소 절대경로> -C <저장소> status --short -- <대상 경로>`를 실행했다. 원천을 다시 대조한 뒤 rag-meta의 고유 인용 파일 13개에도 같은 검사를 실행했고 모두 종료코드 0·status 출력 없음이었다. Git이 사용자 전역 ignore 파일 읽기 권한 경고를 stderr에 출력한 사실은 남긴다. 전역 설정을 바꾸지 않았으며 대상 tracked 파일의 변경 출력은 없었다.

62154da0·b8faaead의 실제 diff와 시험 변경을 읽었다. 현재 동작은 위 HEAD 코드로 다시 확인했다. 특히 Gateway의 overlay 부재 분기는 후속 커밋의 커밋 전 생성 작업 제외까지 읽었고, 과거 diff만으로 현재 적재 동작을 쓰지 않았다. 보상 확정은 RedisUiOperationLedgerAdapter.markRolledBack → OperationLedgerRedisStore.markRolledBack → operation_rolled_back.lua까지 추적했다.

관련 시험은 코드를 읽어 기대값만 확인했다. `CoordinatedGatewayBootstrapOverlayPolicyTest`의 원장 유실·숫자 후퇴·조회 예외·커밋 전 생성 분기, `UiOperationExecutorTest`의 보상 PASS 연결·일반 PASS 비간섭, `CompensationClaimHandoverTest`의 대상·revision·상태 거부와 멱등 기대값을 확인했다. 시험을 새로 실행하거나 기존 시험 결과를 이번 PASS로 재사용하지 않았다.

작성 규칙 `docs/rag-document-writing-rules.md`와 목록 `docs/tc-rag-document-list.md` 전체를 읽었다. 겹치는 원문은 조정 운영·장애 인계·배포 트러블슈팅 문서를 읽고, 나머지 기존 문서에서 보상·후퇴 진단 식별자의 중복도 검색했다.

## 절별 사실 대조

다음 표는 최종 검색 원문의 각 문장과 표 자료 행을 대응시킨다. 절차 참조 문장은 사용자 지정 소유 범위 및 실제 존재하는 대상 문서와 대조했다. 확인 순서·인계 권고는 확인한 코드 분기를 구별하기 위한 문서 작성상의 안내이며, 원격 실행 결과를 뜻하지 않는다.

### 보상이 시작되지 않은 경우와 원 작업 확정 실패를 가르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 16, 첫 문장 | 발급 실패·보상 진행·원 작업 확정 거부를 나누어 확인 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L70~144; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L528~592 | 발급기 거부와 보상의 실행·종결 뒤 원 작업 확정이 서로 다른 호출이다. |
| 16, 둘째 문장 | 원 작업·보상 작업을 구별해 조회 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L142~144; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd L33~52 | 발급 로그에 원 ID·보상 ID가 각각 있고 조회 응답은 operationId 단위다. |
| 18, 첫 문장 | 조회·권한·정상 복구 절차 소유 문서 참조 | nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/coordination-ownership-and-recovery-check.md L10~19 L155~196 L215~240 | 실제 문서의 관리 조회·장벽·보상 발급·완료 확인 절을 읽었다. 사용자 소유 범위에 따라 반복하지 않는다. |
| 18, 둘째 문장 | 공통 인계 문서 참조 | nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/incident-evidence-handover.md ; 장애 문의에 공통으로 묶을 내용·서버 로그와 Pod 상태를 넘길 때 붙일 조회 위치 | 실제 문서의 공통 증거·인계 절을 읽었다. 문서 경로를 그대로 안내한다. |

### 보상 발급 기록이 없을 때 어느 실패 로그를 찾는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 25, 첫 문장 | 자동 보상 대상인데 발급 미확인이면 원 작업 ID 로그 확인 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L665~688; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L70~144 | 대상 사유 필터·발급 호출 뒤 각 실패 로그가 failed.operationId를 남긴다. 대상 사유 목록 자체는 운영 문서에 맡겼다. |
| 25, 둘째 문장 | 구별 결과를 담당자에게 전달 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L70~144 | 아래 네 코드 분기에 따라 원인 구별 결과를 인계하는 안내다. |
| 29 | ISSUE_FAILED는 예외와 그 원문 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L87~94 | catch가 원 ID·보상 ID와 ex를 함께 로그에 남긴다. |
| 30 | DIGEST_CONFLICT는 동일 보상 ID 내용 충돌 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L113~122 | CreateResult.CONFLICT_DIGEST에서 두 ID를 기록하고 발급을 끝낸다. |
| 31 | CANDIDATE_REJECTED의 code·conflict | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L125~139 | 거부 코드와 conflictingOperationId를 기록하고 보상을 abort한다. |
| 32 | SKIPPED_NESTED는 실패한 보상에서 추가 보상 생략 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L73~80 | compensatesRevision이 있으면 로그를 남기고 null을 반환한다. 자동 보상 대신 인계하도록 안내했다. |

### 보상 작업이 진행 중이면 어느 단계에서 멈췄는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 41, 첫 문장 | PENDING 보상의 phase·dbFinalized·participantProofs 대조 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd L33~52; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L170~184 | 조회 응답 필드가 존재하며 실행기는 현재 phase로 분기한다. |
| 41, 둘째 문장 | NO_PARTICIPANT는 처리 가능한 세션 대기 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L223~227 | 참여자 집합이 비면 로그를 남기고 준비를 전진시키지 않는다. |
| 43, 첫 문장 | FINALIZING의 DB 증적·장벽 개방 거부 구별 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L528~564 | recordDbFinalized 거부와 openBarrierAndPass 거부의 로그·code가 별개다. |
| 43, 둘째 문장 | 원 작업 확정 거부와 단계 차이 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L554~592 | 원 작업 확정은 장벽 개방 OK 뒤 호출된다. |
| 45, 첫 문장 | DB_STATE_UNEXPECTED에서 덮어쓰기 중단 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L534~541 | finalizer가 UNEXPECTED_STATE면 복구 필요로 전환하며 증적·장벽 개방으로 진행하지 않는다. |
| 45, 둘째 문장 | 실제 DB 대조 담당자 인계 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L538~541 | 이전·목표 상태에 맞지 않는 DB 분기를 원인으로 확인하는 안내다. |

### 원 작업과 보상 작업의 연결이 다르다고 거부될 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 55 | 확정 거부 code에서 대상 연결 검사 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L578~592; nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L29~40 | 원장 반환 코드가 확정 거부 로그에 전달된다. |
| 59 | TARGET_MISMATCH의 비교 값 | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L29~33 | 보상 해시 compensatesOperationId와 호출의 원 작업 ID를 비교한다. |
| 60 | REVISION_MISMATCH의 비교 값 | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L35~40 | 보상 compensatesRevision의 비어 있지 않음과 원 desiredRevision 일치를 검사한다. |
| 62, 첫 문장 | compensatesOperationId의 API 미노출 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd L33~52 L111~148 | record 필드와 from 변환 모두 해당 필드를 담지 않는다. |
| 62, 둘째 문장 | Redis 보상 작업 해시에서 직접 읽기 | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 L15~15 L60~68; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiCompensationIssuer.java @ 985d17c604dd L175~181 | 작업 키는 tc:coord:v1:operation: 뒤 ID이며 발급 시 compensatesOperationId를 저장한다. |
| 62, 셋째 문장 | 실제 ID를 넣어 두 작업 연결 불일치 인계 | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 L66~68; nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L29~40 | 문자열은 실제 ID 자리임을 명시했고, 비교 대상 두 작업을 함께 넘기도록 안내한다. |

### 보상 완료 확정의 전제조건이 거부되면 무엇을 대조하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 70 | 거부 code와 당시 상태의 대조·인계 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L578~592; nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L12~58 | code를 남기는 위치와 아래 상태 거부 분기를 확인했다. |
| 74 | EPOCH_MISMATCH | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L12~15 | Redis epoch 부재 또는 호출 epoch 불일치다. |
| 75 | NOT_FOUND | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L16~18 | 원 작업 해시 부재다. |
| 76 | COMPENSATION_NOT_FOUND | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L19~21 | 보상 작업 해시 부재다. |
| 77 | COMPENSATION_NOT_PASSED | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L23~27; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd L115~117 | 저장 필드 state가 PASS인지 검사하며 관리 응답 명칭은 status다. |
| 78 | NOT_REPAIRABLE | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L42~54; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/OperationAdminResponse.java @ 985d17c604dd L115~117 | REPAIR_REQUIRED가 아니면 거부하며 FAIL·ROLLED_BACK 멱등 예외는 아래 문장으로 설명했다. |
| 79 | NOT_COMPENSATING | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L55~58 | 원 phase가 COMPENSATING인지 검사한다. |
| 81, 첫 문장 | 같은 완료 확인의 OK | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L23~48 | 보상 PASS·대상·revision 검사 뒤 원 상태 FAIL·phase ROLLED_BACK이면 OK다. |
| 81, 둘째 문장 | 상태·epoch 직접 덮어쓰기 배제 | nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_rolled_back.lua @ 6fe71f6b2da6 L12~58 | 모든 전제조건을 통과할 때만 종결하는 경로를 보존하는 운영 안내다. 임의 수동 쓰기 명령은 만들지 않았다. |

### 보상 PASS 뒤 원 작업이 남고 확정 거부 로그도 없을 때

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 91, 첫 문장 | b8faaead 이후 PASS 직후 연결 값이 있으면 원 작업 확정 호출 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L554~583; nori-tc-ui-backend commit b8faaead3ea5 diff | 수정 diff와 현재 HEAD의 호출 위치·null 분기를 모두 읽었다. |
| 91, 둘째 문장 | PASS 뒤 ADVANCE_FAILED와 예외 대조 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L554~560; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd L65~72 | PASS 로그 이후 close 호출이 있으며 실행 예외는 reconciler가 operationId와 ex로 기록한다. |
| 91, 셋째 문장 | 호출 예외 시 거부 코드 로그에 도달하지 못할 수 있음 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L583~592; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd L65~72 | markRolledBack 호출을 감싼 catch가 없다. non-OK 반환과 예외 전파를 구별했다. 실제 현장 예외가 발생했다고 주장하지 않는다. |
| 93, 첫 문장 | PASS 작업은 실행기에서 종결로 제외 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L151~158; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/model/UiOperationView.java @ 985d17c604dd L71~74 | readOperation이 terminal이면 advance 루프를 끝내며 PASS가 terminal이다. |
| 93, 둘째 문장 | 자동 재확정 기대 대신 대상 연결·예외·판본 인계 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L151~158 L578~592 | 확정 재시도 보장은 없으므로 원 작업을 미확정 상태로 인계하도록 안내한다. |

### committed overlay가 없는데 왜 후퇴로 차단되는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 102, 첫 문장 | 62154da0 이후 overlay 부재 로그와 DB 확정 이력 대조 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L77~92; nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayFinalizedRevisionDbAdapter.java @ d3636ab86763 L14~19 L41~47; nori-tc-gateway commit 62154da06163 diff | 부재 표시와 양수 이력 조건을 실제 분기에서 확인했다. DB 조회 공급 컬럼도 확인했다. |
| 102, 둘째 문장 | 양수 이력이 있으면 원장 유실·후퇴 판정 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L81~92 | fence 후 skip하고 GW_LEDGER_REGRESSION을 남긴다. |
| 102, 셋째 문장 | 특정 유실 원인은 로그만으로 확정하지 않음 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L79~92 | 비교값으로 판단하는 분기이고 삭제·복원·만료 원인 이력은 이 로그에 없다. |
| 104, 첫 문장 | 숫자 revision이 DB보다 낮은 분기 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L109~114 | overlay.revision < dbFinalized 조건을 확인했다. |
| 104, 둘째 문장 | 부재·숫자 분기 값을 구별해 인계 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L87~114 | 로그 필드 이름과 부재 표기를 보존한 진단 안내다. |
| 104, 셋째 문장 | 기본 복구 절차 참조 | nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/coordination-ownership-and-recovery-check.md ; Redis 원장이 DB 확정 이력보다 뒤처져 설비가 차단될 때 | 기존 문서에 이미 있는 복구 필요 확인 절차를 복제하지 않았다. |

### 설비 적재 생략이 모두 원장 후퇴를 뜻하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 112 | 같은 설비의 적재 생략 로그부터 구별 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L87~125 | 후퇴·생성 중·삭제 표시가 각기 다른 로그·skip 분기다. |
| 116 | UNCOMMITTED_CREATE 조건과 생성 작업 진행 대조 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L81~105 L143~150 | overlay 없음·양수 이력 아님 뒤 같은 eqpId의 nonterminal 작업을 발견하면 skip한다. |
| 117 | TOMBSTONE과 revision 확인 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L121~125 | deleted 표시가 있는 확정 overlay를 적재하지 않고 revision을 로그에 남긴다. |
| 118 | 조회 예외와 후퇴 판정 구별 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L79~80; nori-tc-gateway/gateway/tc-gateway-core/src/test/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicyTest.java @ d3636ab86763 L132~140 | overlay 조회 예외는 전파되며 후퇴 비교값으로 바꾸지 않는다. |
| 120, 첫 문장 | 세 조건 부재 때 DB 상태 적재 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L81~105 L143~150 | 양수 이력이 없고 대상 미종결 작업이 없으면 DB 상태·revision 0으로 load한다. |
| 120, 둘째 문장 | 적재 생략만으로 원장 유실 단정 배제 | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L87~125 | 생성 중과 삭제 표시도 skip하므로 로그 분기를 확인하는 안내다. |

### 후퇴 차단이 예상과 다르면 DB 확정 이력이 읽혔는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 130, 첫 문장 | overlay 존재·DB 이력 empty의 INERT | nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L109~118 | committed overlay 존재 분기에서 dbFinalized.isEmpty면 경고한다. |
| 130, 둘째 문장 | ARMED는 어댑터 생성 로그 | nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayFinalizedRevisionDbAdapter.java @ d3636ab86763 L36~47 | 생성자에서 출력한다. 개별 설비 이력은 별도 조회 메서드가 읽는다. |
| 132, 첫 문장 | DB 컬럼과 Backend 기록 실패 대조 | nori-tc-gateway/gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayFinalizedRevisionDbAdapter.java @ d3636ab86763 L14~19 L41~47; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L595~607 | 비교값 공급 위치와 기록 실패 경고의 eqpId·revision을 확인했다. |
| 132, 둘째 문장 | NOT_RECORDED는 기록 실패 신호 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L598~607 | recordFinalizedRevision의 false에서 경고를 출력한다. |
| 132, 셋째 문장 | 경고만으로 컬럼 부재 단정 배제 | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd L48~62 | 갱신 결과 0 후 재조회가 없거나 revision이 부족해도 false다. 실제 행 부재와 기록 경로를 대조하도록 썼다. |

### 현장 판본에 두 수정이 포함됐는지 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 142, 첫 문장 | 적용 이미지를 빌드 소스와 대조 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L44~47; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L40~43 | 실제 배포 이미지의 서비스별 태그 입력을 확인했다. 소스 포함 여부는 배포 담당자 빌드 기록과 연결하도록 안내한다. |
| 142, 둘째 문장 | 두 chart의 app.imageTag 사용 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L46~46; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L42~42 | 두 이미지 템플릿을 직접 대조했다. |
| 142, 셋째 문장 | 태그만으로 수정 포함 판단 배제 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L46~46; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L42~42 | chart는 전달된 태그를 사용할 뿐 소스 커밋 포함을 증명하지 않는다. |
| 144, 첫 문장 | 두 수정의 제품·효과 대응 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L554~592; nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L77~92; 두 수정 커밋 diff | Backend 보상 확정 연결과 Gateway 부재 후퇴 검사를 현재 코드에 대조했다. |
| 144, 둘째 문장 | 배포 변경 절차 참조 | nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/upgrade-rollback-and-configuration-apply.md ; 이미지 태그와 되돌릴 release 번호 찾기·Backend·Business·Simulator 이미지 교체·Gateway 이미지를 교체하고 판정하기 | 실제 존재하는 배포 변경 문서로 안내한다. 변경 명령은 새로 싣지 않았다. |
| 144, 셋째 문장 | 판본 대조 결과의 인계 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L44~47; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L40~43; 사용자 지정 현장 판본 미확인 조건 | 적용 이미지는 현장 조사가 필요하므로 이번 소스 확인 결과와 구별해 인계하도록 안내한다. |

## 매뉴얼과 코드가 다른 곳

대상 행의 원천은 두 수정 커밋이다. 운영 가이드를 동작 근거로 옮기지 않았고 운영 명령을 새로 수록하지 않았다. 이번 범위에서 매뉴얼과 코드의 동작 충돌로 확정해 본문에 반영한 항목은 없다. 소스 주석의 컬럼 부재 설명은 실제 false 반환 조건보다 좁으므로, 문서에는 코드가 확인하는 기록 실패까지만 썼다.

다른 문서에서 확인한 표현 범위: `00.rag/admin/coordination-ownership-and-recovery-check.md` L240은 원 작업 확정 실패 시 UI_OPERATION_ROLLBACK_NOT_CONFIRMED가 남는다고 일반적으로 서술한다. nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L583의 markRolledBack 호출 예외는 L590 로그 이전에 전파되고 nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationReconciler.java @ 985d17c604dd L65~72에서 UI_OPERATION_ADVANCE_FAILED로 기록된다. non-OK 반환 경로와 예외 경로의 구별이 빠진 표현으로 메인 세션에 전달한다. 기존 문서는 수정하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인 담당 역할 |
|---|---|---|
| 현장 Backend·Gateway 이미지에 두 수정이 들어 있는지 | 서버·클러스터·빌드 서비스에 접속하지 않았다. 현재 소스 HEAD가 현장 판본을 증명하지 않는다. | 배포 담당자 |
| 현장의 실제 보상 작업 상태·원장 유실 원인·DB 이력 값 | 현장 원장·DB·로그를 조회하지 않았다. 코드의 비교 분기로 실제 장애 원인을 단정하지 않는다. | 조정·DB·Redis 담당자 |
| 미확정 원 작업을 강제로 닫는 수동 복구 명령 | 이번에 확인한 관리 조회 API는 읽기 경로이고 검증된 수동 복구 쓰기 절차를 확보하지 않았다. | 조정 담당자 |

위 부재는 코드 기반 진단 질문에 답하는 데 필요한 제품 결정을 막지 않으므로 사용자 결정 요청은 없다.

## 다른 문서가 소유해 넣지 않은 사실

| 사실 | 소유 objectKey | 이 문서의 처리 |
|---|---|---|
| 소유권 구조·초기 값 발급·실행 lease·조회 권한·조회 오류·정상 보상 완료 확인 | 00.rag/admin/coordination-ownership-and-recovery-check.md | 절차를 반복하지 않고 확인 경로와 필요한 최소 분기 조건만 썼다. |
| 공통 장애 인계 자료·비밀값 제거·Pod·로그 수집 명령 | 00.rag/admin/incident-evidence-handover.md | 인계 자료 목록을 재복제하지 않았다. |
| 배포 변경·되돌림 조작 | 00.rag/admin/upgrade-rollback-and-configuration-apply.md | 이미지·소스 대조만 남기고 조작 절차는 참조했다. |
| 상태값·오류 코드 전반의 뜻 | 00.rag/user/state-reference.md·00.rag/user/error-message-and-code-reference.md | 이 증상의 원인 구별에 필요한 상태·거부 코드만 조건과 함께 썼다. |

## 검사 결과

- 검사기: 사용자 지정 `step3/tools/check.sh`, 수정·우회하지 않았다.
- 실행 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/coordination-troubleshooting.md`
- 도구: GNU bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS.
- 최초 검사: exit 1, 부모 510자·자식 202자 두 위반. 제5절의 안내 문장만 짧게 고치고 제6절의 같은 뜻 표현을 줄였다. 상태·코드 행을 빼거나 검사 한도를 바꾸지 않았다.
- 마지막 원문 편집 뒤 검사: exit 0, sections=10, children=35, maxChild=191, maxParent=444, violations=0. CROSS VIOLATION=0, MASK 출력=0을 전체 출력에서 확인했다.
- 마지막 출력 10줄:

```text
    L120-120 문단 98  | overlay·양수 DB 확정 이력·같은 설비의 미종결 작업이 모두 없으면 DB 상태로 적재하는 경로다. 조정 담당자는 적재 생략이라는 결과만으로 원장 유실을 확정하지 않는다.
§ L122-132 [29] 후퇴 차단이 예상과 다르면 DB 확정 이력이 읽혔는가   parent=344 children=2 sources=4
    L130-130 문단 169  | committed overlay가 있는데 GW_LEDGER_REGRESSION_CHECK_INERT가 나오면 DB 확정 이력을 읽지 못해 후퇴 비교가 동작하지 않은 경로다. GW_LEDGER_REGRESSION_CHECK_ARMED는 DB 조회 어댑터 생성 로그이므로 설비별 이력 존재도 따로 확인한다.
    L132-132 문단 174  | 조정·DB 담당자는 같은 설비의 tc_eqp.last_finalized_revision과 Backend의 UI_FINALIZED_REVISION_NOT_RECORDED를 대조한다. 후자는 확정 revision 기록 실패를 알리는 로그다. 경고 문구만으로 컬럼 부재를 확정하지 않고 실제 행·기록 경로를 확인한다.
§ L134-144 [23] 현장 판본에 두 수정이 포함됐는지 확인하기   parent=309 children=2 sources=4
    L142-142 문단 129  | 배포 담당자는 Backend와 Gateway에 적용한 이미지를 각각 빌드 기록의 소스 커밋과 연결한다. 두 Helm chart는 app.imageTag를 이미지 태그에 사용한다. 태그 문자열만으로 수정 포함 여부를 판정하지 않는다.
    L144-144 문단 179  | Backend는 b8faaead의 보상 완료 확정 연결, Gateway는 62154da0의 overlay 부재 후퇴 검사가 포함됐는지 확인한다. 배포 변경 절차는 00.rag/admin/upgrade-rollback-and-configuration-apply.md를 따른다. 판본 대조 결과를 장애 진단과 함께 인계한다.
SUMMARY sections=10 children=35 maxChild=191 maxParent=444
CORPUS docs=34/53 sections=488 part={admin=8, user=26} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=10}
TOTAL files=34 targets=1 violations=0
```

사람 검사: 10절 전부를 읽었고 제목의 질문 식별·원천 일치·독립 답·소유 범위 중복 여부를 각각 판정했다.

| 절 제목 | 질문 식별 | 원천 일치 | 독립 답 | 중복·소유권 |
|---|---|---|---|---|
| 보상이 시작되지 않은 경우와 원 작업 확정 실패를 가르기 | 통과 | 통과 | 통과 | 절차·인계는 기존 문서로 연결 |
| 보상 발급 기록이 없을 때 어느 실패 로그를 찾는가 | 통과 | 통과 | 통과 | 발급 거부 분기의 구별 소유 |
| 보상 작업이 진행 중이면 어느 단계에서 멈췄는가 | 통과 | 통과 | 통과 | 진행 중·확정 전 단계를 구별 |
| 원 작업과 보상 작업의 연결이 다르다고 거부될 때 | 통과 | 통과 | 통과 | 연결 값의 불일치에 한정 |
| 보상 완료 확정의 전제조건이 거부되면 무엇을 대조하는가 | 통과 | 통과 | 통과 | 존재·epoch·상태 거부에 한정 |
| 보상 PASS 뒤 원 작업이 남고 확정 거부 로그도 없을 때 | 통과 | 통과 | 통과 | 예외·종결 건 제외 경로 소유 |
| committed overlay가 없는데 왜 후퇴로 차단되는가 | 통과 | 통과 | 통과 | 기존 문서의 정상 복구 절차를 반복하지 않음 |
| 설비 적재 생략이 모두 원장 후퇴를 뜻하는가 | 통과 | 통과 | 통과 | 생성 중·삭제·조회 예외와 구별 |
| 후퇴 차단이 예상과 다르면 DB 확정 이력이 읽혔는가 | 통과 | 통과 | 통과 | DB 이력 부재·기록 실패 구별 |
| 현장 판본에 두 수정이 포함됐는지 확인하기 | 통과 | 통과 | 통과 | 배포 조작 대신 이미지와 소스 대조만 안내 |

제목 10개는 정확 중복이 없고 식별자 제거 후 같은 틀이 3회 반복되는 제목은 없다. 빈 틀 문장·이름 토막 뜻풀이·조사 접합 오류·틀린 주장·오답 인용·사용자 문서로 관리 전용 답 복제는 발견하지 않았다. 전수 사실 대조표와 검사기의 직렬화·마스킹 결과를 함께 확인했다. 마지막 원문 SHA·줄 수는 위 대상 절의 값으로 고정했다.

일회성 검사 로그 `check-initial.log`·`check-final.log`는 이 기록에 최초 실패 위치·원인 및 최종 출력을 보존한 뒤 정상 삭제했다. 절대경로 부모는 `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-35-coordination-troubleshooting/work/`이다. 이 세션이 만든 두 로그만 대상으로 삼았으며 부모 경로가 Temp 아래인지와 조상 경로의 reparse 부재를 확인했다. 두 파일은 Archive 속성만 있었고 ReadOnly·Hidden·System·ReparsePoint는 없었다.

검사 실행은 각각 exit 1·exit 0으로 종료됐고 로그 저장 호출도 끝난 상태였다. 일반 프로세스 목록 조회 `Get-CimInstance Win32_Process -ErrorAction Stop`는 `액세스가 거부되었습니다`, `HRESULT 0x80041003`으로 실패했다. 이 조회를 권한 변경으로 재시도하지 않았다. 두 정확한 로그를 FileAccess.Read·FileShare.None으로 각각 열어 모두 EXCLUSIVE_READ_OK를 확인한 뒤 핸들을 닫았다. 이어 native PowerShell `Remove-Item -LiteralPath <각 로그의 절대경로>`를 -Force·-Recurse 없이 실행했다. 삭제 명령 종료코드 0이며 두 파일의 Test-Path=False와 work_children=0을 확인했다. work 폴더 자체는 삭제하지 않았다.

제품 시험·원격 현장 재현·독립 공식 검증은 실행하지 않았다. provider 사용량·비용·세션 전체 wall time은 별도 측정값이 없어 미확인이다.

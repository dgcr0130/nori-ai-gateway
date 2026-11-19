# 배포 트러블슈팅 작성 기록

## 대상

- objectKey: `00.rag/user/deployment-troubleshooting.md`
- 구획·권한: user `[AI_DOC_READ]`
- 유형: 트러블슈팅
- 검색 원문: `rag-documents/nori-tc/00.rag/user/deployment-troubleshooting.md`
- 검색 원문 SHA-256: `a16f22c238c5cbd7e42e492dde2bb7495d0674543356eed1304a3b65ec0ab137`
- 줄 수: 83
- 절 수: H2 5개, 자식 15개
- 확인일: 2026-11-19(한국 시간)
- 소유 범위: 응답 추적 등록 소멸에 따른 대기 판정, 변경 없는 대상이 섞인 취소 작업 식별, 취소 거부 원인 구별과 사용자 인계.
- 작성·자체검사만 수행했다. 독립 검증·업로드·제품 시험·git add·commit·push는 수행하지 않았다.

## 원천 판본

| 저장소 | 읽은 HEAD | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-backend | 985d17c604dda3430a181cbac953ef2cc70c314f | 인용한 core·redis·db·web src 경로를 status --short로 확인한 뒤 인용 파일별로 재확인했다. 인용 11파일 모두 stdout 빈 값, exit 0이었다. |
| nori-tc-ui-front | 9fe69a75dcd3277d9c7de535c8368bc860a0ec5d | src/features/deploy·src/i18n/locales/ko.json을 확인한 뒤 인용 파일별로 재확인했다. 인용 4파일 모두 stdout 빈 값, exit 0이었다. |
| nori-tc-exec | f1d5e0a8e727941ef12a1170582e1ce50624aa71 | 권한 catalog·시드 파일을 경로 지정 status --short로 확인했다. 인용 2파일 모두 stdout 빈 값, exit 0이었다. |
| nori-ai-gateway | 70cf6cd84db6921cadb3468063d94b648c7f4bce | 작성 규칙·목록의 status --short는 stdout 빈 값이었다. 시작 상태는 main, ahead 8, 미추적 rag-documents/였고 worktree는 현재 저장소 하나였다. |

소유자 차이로 형제 저장소의 일반 git 조회가 처음 거부되었다. 이후 읽기 명령에만 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C <저장소> ...`를 사용했다. 전역 설정은 바꾸지 않았다. status는 사용자 전역 ignore 파일 접근 경고를 냈지만 종료코드는 0이었고 지정 파일의 변경 출력은 없었다.

수정 커밋 `12005ecf93057cc616f39f812fddf850e5e84116`·`c336bd8bba775fe319de220a8fc9e2c3b9b81444`의 메시지·관련 diff를 읽었다. 동작은 위 HEAD의 구현·회귀 시험 기대값으로 대조했다. 커밋 메시지에 적힌 과거 시험 통과 수를 이번 실행 결과로 사용하지 않았다.

인용한 제품 파일 17개는 각 파일의 status 결과와 SHA-256을 실제 명령으로 확인했다. 아래 사실표는 현재 HEAD 줄 번호를 쓴다. 원천 경로는 `C:/workspace_nori/nori-tc/` 아래이다.

## 절별 사실 대조

### 배포가 대기하는지 상세 조회가 막혔는지 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 21 | 같은 설비의 단계·갱신 시각을 관측 시각과 비교한다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~317 L329~375; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L291~301 | Deploy View 제목과 대상별 표, EQP ID·현재 Step·Step 상태·갱신 시각이 실제 렌더된다. 비교 기록은 이 값으로 사용자가 할 수 있는 진단 조치다. |
| 23 첫 문장 | 조회 오류는 문구·조회 시각으로 인계한다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L119~124 L463~479; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 L774~817 | 상세·대상 요청 실패를 각각 표시한다. 배포 단계의 처리 결과와 별개 요청이다. 오류 재조회 단추 설명은 사용 절차 소유라 반복하지 않았다. |
| 23 둘째 문장 | 기본 상세·대상 조회 권한은 DEPLOY_READ다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L88~94; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L780~780; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L160~185 | 기본 GET prefix 권한과 미등록·미보유 차단을 확인했다. 현장 계정 권한이라고 쓰지 않았다. |
| 25 | 화면 열기·재조회는 사용 절차 소유다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L32~32; nori-ai-gateway/rag-documents/nori-tc/00.rag/user/deployment-workflow.md L256~272 L312~326 | 앞선 문서의 상세 열기·조회 실패 절을 읽고 objectKey로 안내했다. 앞선 검색 원문은 미추적 파일이므로 제품 소스 commit을 부여하지 않았다. |

### 응답 추적 등록이 사라지면 언제 대기를 끝내는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 36 첫 문장 | 수정 포함 판본의 조정 경로는 등록 존속과 소멸을 구별한다. | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/registry/DeployTraceRedisAdapter.java @ 985d17c604dd L127~163; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd L137~171 | expectedSources가 없으면 EXPIRED, 응답 일부가 없으면 PENDING이다. 드라이버는 두 상태를 다른 분기로 처리한다. 12005ecf diff의 추가 분기와 일치한다. |
| 36 둘째 문장 | 등록 소멸 시 저장된 기한 전 대기, 기한 도달 시 STEP_TIMEOUT이다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd L183~198 L246~257; nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/DeployApplyDriverTest.java @ 985d17c604dd L199~224 | 단계 발행 시 시작 시각+timeoutMs를 저장한다. now.isBefore(deadline)만 대기이므로 기한과 같은 시각도 실패 분기다. 기한 전후 시험의 기대값을 읽었다. 시험을 실행하지는 않았다. |
| 36 셋째 문장 | 기한이 없으면 대기한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd L188~190 | deadline == null도 WAITING이다. 모든 등록 만료가 반드시 시간 내 종료된다고 확대하지 않았다. |
| 38 첫 문장 | 화면에 단계 기한을 제공하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/response/DeployTargetPageResponse.java @ 985d17c604dd L20~30; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~317 | 대상 응답·표 열에 기한 항목이 없다. 갱신 시각을 기한으로 해석하지 않았다. |
| 38 둘째 문장 | 관측 정보를 넘겨 저장 기한·등록·판본을 담당자가 대조하도록 요청한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd L163~198; nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd L614~626 L1139~1149; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~317 L333~338 | 서버가 읽는 current_step_timeout_at과 화면에서 수집 가능한 설비·단계·배포 번호를 확인했다. 내부 접근 조작 대신 인계할 정보만 썼다. |
| 40 | 늦은 응답만으로 등록이 복구되지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/registry/DeployTraceRedisAdapter.java @ 985d17c604dd L69~96; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/test/java/com/nori/tc/ui/adapters/redis/registry/DeployTraceExpiryLookupIT.java @ 985d17c604dd L81~90 | 등록 metadata가 없으면 record가 return하며, 만료 후 응답을 기록해도 EXPIRED라는 시험 기대값이다. 실제 Redis 시험은 실행하지 않았다. |

### 응답을 못 받은 경우와 응답 조회가 실패한 경우 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 50 첫째·둘째 문장 | 조회 불가 코드와 등록 존속 중 응답 미수집 대기를 구별한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd L163~171; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/registry/DeployTraceRedisAdapter.java @ 985d17c604dd L143~163 | INFRA_ERROR는 DEPLOY_TRACE_UNAVAILABLE 실패, 응답 부족은 PENDING→WAITING이다. 일반 화면 HTTP 조회 오류와도 별개인 서버 판정임을 조건으로 썼다. |
| 50 셋째 문장 | 수신 실패 코드·문구를 사용하고 각각 없으면 대체한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd L201~215 | success=false일 때 firstErrorCode가 null이면 STEP_FAILED, firstErrorMessage가 null이면 대체 문구다. 두 값은 독립 검사다. |
| 52 첫 문장 | 표시된 대상 오류와 요약 결과를 보관한다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L295~303 L379~411; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L162~162 L300~306 | 대상은 message 우선·code 대체이고, 요약은 값이 있는 resultCode·resultMessage를 표시한다. 특정 내부 코드가 화면에 반드시 그대로 표시된다고 쓰지 않았다. |
| 52 둘째 문장 | 배포·설비·단계·시각을 인계한다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~317 L333~338; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployApplyDriver.java @ 985d17c604dd L163~215 | 조회 실패와 수신 실패를 담당자가 원천과 대조할 식별 정보를 제안했다. 현장 실패의 실제 원인을 확정하지 않았다. |
| 54 | 오류 코드·상태값 전체 설명은 다른 문서가 소유한다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L66~67 | 목록의 소유권을 가리킨다. 두 참조 문서가 이미 작성·업로드됐다는 뜻으로 쓰지 않았다. |

### NO_CHANGE가 섞인 배포의 취소 대상 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 65 | 변경 없는 설비와 실행 설비를 나눠 기록한다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~265; nori-tc-ui-front/src/features/deploy/lib/deploy-monitor-format.ts @ 9fe69a75dcd3 L11~12; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L297~297 | 상세 상태는 서버 상태 문자열을 표시하므로 NO_CHANGE로 구분 가능하다. PASS만 비교하라고 하지 않았다. |
| 67 첫 문장 | 수정 포함 판본은 접수·취소의 같은 선별을 적용한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd L590~594 L616~633; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/DeployOperationCoordinator.java @ 985d17c604dd L97~137 L156~184 L288~291 | 양쪽이 계획 전체를 넘기며 group에서 planDraft.noChange를 제외한다. 실행 중 targetStatus로 새로 선별하지 않는다. c336bd8b 변경과 일치한다. |
| 67 둘째 문장 | 같은 모델을 공유하는 변경 없는 대상도 취소 식별을 바꾸지 않는다. | nori-tc-ui-backend/ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/coordination/DeployCommitBoundaryCancelTest.java @ 985d17c604dd L93~145; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/DeployOperationCoordinator.java @ 985d17c604dd L272~303 | 변경 없는 대상 혼합·공유 모델의 두 회귀 기대값이 접수된 작업 ID와 같은 취소 ID를 확인한다. 전체 취소 성공을 보장하는 문장으로 확대하지 않았다. |
| 69 첫 문장 | 취소 시각·결과와 두 종류의 설비 ID를 넘긴다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~265 L333~338; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd L590~599 | 취소 요청을 수행한 담당자에게 화면 대상과 요청 결과를 결속하도록 안내했다. 화면에 취소 단추가 있다고 쓰지 않았다. |
| 69 둘째 문장 | 판본 및 접수·취소 작업 동일성을 확인 요청한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/DeployOperationCoordinator.java @ 985d17c604dd L103~105 L127~135 L272~291 | 동일 그룹의 대표 대상에서 작업 ID를 도출한다. 현장 판본과 실제 작업 ID는 미확인이라 사용자 조작 대신 담당자 대조로 썼다. |

### 취소 거부 응답만으로 적용 확정을 알 수 있는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 79 첫 문장 | 실제 적용 확정·작업 부재·작업 종료를 구별한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L620~650 | 작업 부재·terminal·committedOrLater·abort 반환 실패 모두 false가 될 수 있다. 거부를 실제 커밋의 단독 증거로 취급하지 않았다. |
| 79 둘째 문장 | 중단 실패 ID도 committedOperationIds에 들어간다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/DeployOperationCoordinator.java @ 985d17c604dd L125~137; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd L593~599 | abortBeforeCommit=false면 committed 목록에 추가하고 서비스는 그 목록으로 거부 문구를 만든다. |
| 81 첫 문장 | 응답의 배포 번호·작업 ID·거부 문구·시각을 보관 요청한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd L595~599 | 거부 응답 문구에 deployKey와 committedOperationIds가 포함된다. 사용자가 내부 저장소에 접근하도록 안내하지 않았다. |
| 81 둘째 문장 | 화면 대상 결과와 실제 적용·후속 조치를 대조한다. | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~317; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L626~650 | 거부 원인 구별을 위한 인계다. 거부 뒤 모든 대상이 취소되었다거나 전혀 바뀌지 않았다고 단정하지 않았다. |
| 83 | 취소 경로와 Hold 조작은 사용 절차 소유다. | nori-ai-gateway/docs/tc-rag-document-list.md @ 70cf6cd84db6 L32~32; nori-ai-gateway/rag-documents/nori-tc/00.rag/user/deployment-workflow.md L216~240 | 사용 절차의 Hold·취소 절을 읽고 해당 objectKey만 가리켰다. |

## 매뉴얼과 코드가 다른 곳

이 문서의 지정 원천은 두 수정 커밋이다. 사용자 매뉴얼을 동작 근거로 사용하지 않았으므로 매뉴얼 전체와 코드의 일치 여부는 판정하지 않았다. 기록할 매뉴얼·코드 충돌은 없다.

주의한 원천 설명: DeployService의 취소 주석만으로 모든 작업의 취소가 원자적이라고 쓰지 않았다. 실제 cancelBeforeCommit 반복문은 작업별 중단을 순서대로 수행하며 false를 분류한다. 이 문서는 거부 응답의 원인 구별만 설명한다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 담당 역할 |
|---|---|---|
| 현장 판본에 12005ecf·c336bd8b 수정이 실제 들어 있는지 | 로컬 HEAD는 읽었지만 현장 배포 판본은 조회하지 않았다. | TC 배포·운영 담당 |
| 특정 배포가 끝날 시각·실제 단계 기한·현재 등록 상태 | 대상 현장 원장과 관측값이 없다. 코드의 기한 계산을 현장값으로 대체하지 않았다. | TC 운영 담당 |
| 현재 취소 거부가 실제 적용 확정 때문인지, 어떤 작업이 취소됐는지 | 현장 요청·응답·작업 원장을 읽지 않았다. | 취소 요청을 처리한 운영 담당 |
| 현장 계정의 실제 배포 권한 | 저장소 기본 catalog·시드만 확인했다. | 사용자·권한 운영 관리자 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 이 문서에서 반복하지 않은 사실 |
|---|---|
| 00.rag/user/deployment-workflow.md | 등록·실행·재실행·상세 열기·조회 실패 재조회 조작, 취소 단추 부재와 Hold 절차, 각 실패 문구 전체 설명 |
| 00.rag/user/state-reference.md | 배포·대상·단계 상태값의 전체 뜻과 분류 |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드·문구의 전체 목록과 개별 뜻풀이 |
| 00.rag/user/permission-code-reference.md | 권한 코드의 전체 뜻·연결 목록. 본문은 조회 거부 구별에 필요한 DEPLOY_READ만 썼다. |
| 00.rag/user/login-permission-and-screen-troubleshooting.md | 일반 로그인·세션·업무 권한 오류의 원인 구별 |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 설비 연결·명령 결과의 별도 진단 |
| 00.rag/user/tc-supported-scope.md | 제품 지원 범위와 화면 취소 기능 제공 여부 |

## 검사 결과

최종 검색 원문 편집 뒤 다음 명령을 실행했다. 종료코드 0이며 검사기는 수정하지 않았다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/deployment-troubleshooting.md
```

최종 실행의 마지막 출력 10줄:

```text
    L65-65 문단 78  | 취소가 의도한 대상에 적용되지 않으면 「대상별 진행 현황」의 「상세 상태」에서 NO_CHANGE인 설비와 실행 중인 설비를 나누어 기록한다.
    L67-67 문단 130  | c336bd8b 수정이 포함된 판본에서 배포 작업을 묶어 처리하는 경로는 변경 없는 대상을 실행 접수와 취소 양쪽의 작업 묶음에서 제외한다. 변경 없는 대상이 같은 모델 버전을 공유해도 취소할 작업의 식별이 달라지지 않도록 처리한다.
    L69-69 문단 110  | 취소를 처리한 운영 담당자에게 배포 번호와 두 종류의 설비 ID, 취소 시각·결과를 넘긴다. 현장 판본의 수정 포함 여부와 실행 때 접수한 작업·취소 때 찾은 작업이 같은지 확인해 달라고 요청한다.
§ L71-83 [26] 취소 거부 응답만으로 적용 확정을 알 수 있는가   parent=344 children=3 sources=4
    L79-79 문단 160  | 작업을 묶어 처리하는 배포의 취소가 거부되면 실제 적용이 확정된 경우와 취소할 작업을 찾지 못했거나 작업이 이미 끝난 경우를 담당자가 구별해야 한다. 서버는 중단에 실패한 작업 ID도 committedOperationIds에 담아 거부 응답을 만들므로 문구만으로 원인을 확정하지 않는다.
    L81-81 문단 119  | 취소를 처리한 담당자에게 응답의 deployKey·committedOperationIds·전체 거부 문구와 요청 시각을 보관해 달라고 요청한다. 화면의 대상별 결과와 대조해 적용 완료 여부와 후속 조치를 확인한다.
    L83-83 문단 63  | 취소 요청 경로와 「Hold」 조작은 00.rag/user/deployment-workflow.md에서 확인한다.
SUMMARY sections=5 children=15 maxChild=171 maxParent=401
CORPUS docs=32/53 sections=471 part={admin=7, user=25} type={개념=4, 사용 절차=13, 운영=7, 트러블슈팅=8}
TOTAL files=32 targets=1 violations=0
```

검사 결과: 대상 위반 0, CROSS VIOLATION 0, MASK 출력 0. 직렬화한 H1 17자, H2 26·26·31·28·26자, 부모 283·338·401·320·344자, 자식 최대 171자였다. 최종 파일은 8192바이트, 83줄이며 BOM·CR·NUL·NFC·메타·원천 위치 검사를 통과했다.

실행 환경: Windows PowerShell 5.1.26100.9444, GNU bash 5.2.37(1)-release, Temurin OpenJDK 21.0.9+10-LTS. 검사기는 제공된 commonmark 0.32.0 classpath로 실행되었다. 검사기 호출은 총 2회였고 첫 검사도 위반 0이었으며 문장 표현을 보완한 최종본을 재검사했다. 두 실행 모두 exit 0이었다.

사람 검사: 40절 미만이므로 아래 5절 전부를 읽었다. 제목을 코드 번호만 바꾸는 틀로 만들지 않았고, 제목 정확 중복·불필요한 반복 틀·본문 복제·정보 없는 틀 문장·이름 토막 뜻풀이·깨진 문장·오답 인용·관리자 문서 열람 지시의 미해소 지적은 0건이다.

| 절 | 제목만으로 질문을 아는가 | 원천과 맞는가 | 독립 답인가 | 다른 절·앞선 문서와 중복 판정 |
|---|---|---|---|---|
| 배포가 대기하는지 상세 조회가 막혔는지 구별하기 | 예: 대기와 조회 실패 구별 | 예: 상세 요청·표·권한 기본값 대조 | 예: 관측과 인계 정보 포함 | 조작 순서는 사용 절차로 보내고 진단용 값만 남겼다. |
| 응답 추적 등록이 사라지면 언제 대기를 끝내는가 | 예: 등록 소멸과 종료 기준 | 예: 기한 null·기한 전·기한 도달 분기 대조 | 예: 화면에 없는 기한의 확인 주체 포함 | 등록 만료 판정은 이 절만 소유한다. |
| 응답을 못 받은 경우와 응답 조회가 실패한 경우 구별하기 | 예: 서버 조회·수신 실패 구별 | 예: INFRA_ERROR·PENDING·실패 응답 대조 | 예: 결과와 인계 정보 포함 | 오류 전체 뜻풀이·재조회 절차는 다른 문서 소유로 남겼다. |
| NO_CHANGE가 섞인 배포의 취소 대상 확인하기 | 예: 변경 없는 대상 혼합 취소 | 예: 공통 group 선별과 두 회귀 기대값 대조 | 예: 판본·식별 대조 요청 포함 | 실행·취소 절차 대신 대상 식별 진단을 썼다. |
| 취소 거부 응답만으로 적용 확정을 알 수 있는가 | 예: 거부의 증거 한계 | 예: false 반환·목록 분류 대조 | 예: 보관할 응답과 후속 확인 포함 | 취소의 일반 가능 상태·Hold 차이 전체는 반복하지 않았다. |

범위·정리: 지정 검색 원문과 이 작성 기록만 만들었다. 검사용 임시 파일·프로세스·서비스를 만들지 않았으므로 세션 소유 삭제 대상은 없다. 제공된 work 폴더와 검사 도구는 기존 자원이라 건드리지 않았다. 외부 AI 호출 0회, provider 사용량·비용 및 전체 세션 wall time은 별도 계측하지 않아 미확인이다. 제품 빌드·단위/통합 시험·실행 화면·현장 적용은 미실행이며 코드와 시험 기대값을 읽은 자체검사와 구분한다.

# 공통 FAQ·오해 정정 작성 기록

## 대상

- objectKey: `00.rag/user/common-faq.md`
- 검색 원문: `rag-documents/nori-tc/00.rag/user/common-faq.md`
- 구획·권한: user `[AI_DOC_READ]`
- 유형: FAQ
- 확인일: 2026-11-19(한국 시간)
- 검색 원문 SHA-256: `46c142cfc4b20f168463052f25a89d470081e943106be71a7f5d889570c8451f`
- 검색 원문: 53줄, H1 1개, H2 3개, 4,846바이트
- 사용자 확정 목록 52행의 공통 질문만 작성했다. 실제 문의 빈도 순위로 선정한 질문이 아니다.
- 완료 범위: 검색 원문과 이 작성 기록. 업로드·제품 변경·커밋·독립 Agent 호출은 수행하지 않았다.

## 원천 판본

작성 규칙 `docs/rag-document-writing-rules.md`와 전체 목록 `docs/tc-rag-document-list.md`를 읽었다. 공통 FAQ 선정 결정은 목록의 공통 FAQ 행과 마지막 문단, 이번 사용자 요청으로 확인했다. 아래 제품 파일의 코드를 직접 읽고 답 사실을 대조했다.

| 저장소 | HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 아래 열거한 원천 경로의 `git status --short -- <경로>` 출력 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 아래 열거한 원천 경로의 `git status --short -- <경로>` 출력 없음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | `docs/completion-report/07-user-manual.md` 상태 출력 없음, exit 0 |
| nori-ai-gateway | 70cf6cd84db6 | 시작 시 main, origin/main보다 8커밋 앞섬, `?? rag-documents/`; worktree는 현재 main 한 개 |

형제 저장소 Git 조회의 첫 시도는 소유자 불일치로 거부됐다. 이후 해당 경로에 한정한 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C <저장소 절대경로> rev-parse --short=12 HEAD`와 같은 형식의 `status --short -- <원천 경로들>`을 실행했다. 영속 Git 설정은 변경하지 않았다. 결과는 위 표와 같으며 개인 ignore 파일 접근 경고가 있었지만 원천 경로 상태 조회 종료코드는 0이었다.

프런트엔드에서 상태를 확인한 파일:

- `src/features/eqp/hooks/useEqpLifecycleCommands.ts`
- `src/features/eqp/components/EqpLifecycleResultDialog.tsx`
- `src/features/eqp/components/EqpOperationResultBody.tsx`
- `src/features/eqp/components/EqpPage.tsx`
- `src/features/eqp/api/eqp.api.ts`
- `src/shared/api/operation.api.ts`
- `src/shared/lib/operation-status.ts`
- `src/features/deploy/components/DeployRegisterPanel.tsx`
- `src/features/deploy/components/DeployMonitorViewModal.tsx`
- `src/features/deploy/api/deploy.api.ts`
- `src/features/deploy/lib/deploy-register-utils.ts`
- `src/i18n/locales/ko.json`
- `tests/unit/operation-polling-contract.test.ts`

백엔드에서 상태를 확인한 파일:

- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java`
- `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java`
- `ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationQueryService.java`
- `ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java`
- `ui/tc-ui-core/src/test/java/com/nori/tc/ui/core/service/DeployServiceTest.java`

호출 흐름은 설비 시작 POST → 접수 식별값 반환 → `runLifecycleStep`의 결과 조회 → 성공 결과 창, 배포 POST → `createDeploy`와 `finishDeployCreation` → 생성 결과 창과 번호·상태 표시로 확인했다. 결과 창 닫기는 각각 이벤트 연결과 실제 핸들러까지 읽었다. 재조회는 작업별 `/async/{id}`와 배포별 요약·대상 조회가 서로 다른 입력을 받는 것을 확인했다.

관련 시험은 실행하지 않고 기대값을 읽었다. 프런트엔드 `operation-polling-contract.test.ts` L77~94는 최종 결과 판정과 PASS 반환, L134~140은 조회 횟수 소진 시 DELAYED 반환을 기대한다. 백엔드 `DeployServiceTest.java` L97~126은 생성 직후 READY·시작 시각 없음·미종결을 기대한다. 실제 기계 검사는 아래 별도 기록한다.

## 절별 사실 대조

### 접수·생성·완료는 화면마다 무엇을 확인하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 22 첫 문장 | 설비 시작 접수 뒤 결과 별도 조회 | nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 L652~682; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L187~219; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L322~337 L493~499 | 접수 응답의 traceId를 반환하고 그 값으로 결과를 조회한다. 접수 성공만으로 결과 창 성공을 정하지 않는다. |
| 22 둘째 문장 | 설비 시작 완료는 시작 결과 성공 표시 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L265~274; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L32~41 L104~123; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L81~82 | PASS를 success로 옮기며 start의 성공 제목은 「설비 시작 완료」다. 재시작의 별도 제약으로 일반화하지 않았다. |
| 22 셋째 문장 | 배포 생성 완료는 배포 기록 생성의 결과 | nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 L664~693; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L235~238; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L171~181 | 생성 응답 뒤 createSuccessTitle을 표시하며 생성과 설비 적용의 완료 대상을 구별할 근거가 있다. |
| 24 첫 문장 | 배포 생성 결과의 번호와 상태 | nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 L683~690 L979~982; nori-tc-ui-front/src/features/deploy/lib/deploy-register-utils.ts @ 9fe69a75dcd3 L128~134; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L338~338 | 생성 결과의 deployKey·deployStatus를 「Deploy #… · 상태 …」로 구성한다. |
| 24 둘째 문장 | 변경 유무에 따른 생성 뒤 상태 | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd L120~194 L531~575 | 변경 없는 대상을 뺀 실행 대상이 없으면 COMPLETED, 있으면 READY를 반환한다. 실행은 별도 startDeploy가 처리한다. |
| 24 셋째 문장 | 시작 결과와 생성 결과를 구별해 기록 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L265~274; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd L146~194 | 앞선 두 종류의 결과를 구별하라는 사용자 관찰 지침이다. 새로운 상태값이나 자동 기록 기능을 주장하지 않는다. |

### 같은 설비의 작업 ID와 배포 번호는 어떻게 구별하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 38 첫 문장 | 작업 ID의 조회 대상 | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40; nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 L19~47; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd L69~107; nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationQueryService.java @ 985d17c604dd L58~80 L109~136 | 화면은 값이 있는 작업 ID를 표시하고 결과 조회는 그 작업 식별값으로 수행한다. 기존 명령의 traceId도 조회 계약에서 처리한다. 내부 명칭 차이는 본문에 늘리지 않았다. |
| 38 둘째 문장 | 배포 번호와 배포 안의 설비 구별 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~234 L329~350; nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 L774~803; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L315~348 | Deploy View의 # 뒤 값은 deployKey, 표의 EQP ID는 target.eqpId다. 요약과 대상 조회는 배포 번호를 받는다. |
| 40 첫 문장 | 문의 번호의 종류를 함께 표기 | nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1110~1110; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L333~338 | 표시되는 두 종류의 식별값을 잘못 합치지 않도록 문의에 종류를 적는 작성 지침이다. 번호 간 자동 대응 관계는 주장하지 않는다. |
| 40 둘째 문장 | 설비·명령·시각·결과를 번호와 묶어 인계 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L201~233 L258~274; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~234 L329~350 | 명령 결과는 action·eqpId·operationId, 배포 결과는 배포 번호와 대상 설비로 연결된다. 발생 시각은 사용자가 관찰한 시각을 뜻하며 화면에 별도 필드가 있다고 주장하지 않는다. |

### 결과 창의 확인·닫기와 작업 취소는 어떻게 다른가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 51 첫 문장 | 확인·닫기의 실제 조작 대상 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L132~140; nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L1515~1529; nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 L315~317 L984~1008; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L66~66 L473~473 | 결과 창의 확인은 open=false, 배포 생성 결과 닫기는 closeDeployDialog로 연결된다. 한국어 표시 문구도 대조했다. |
| 51 둘째 문장 | 닫기에서 작업 취소 요청 없음 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L160~165; nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 L315~317 | 명령 결과 닫기는 복원 기록과 결과 창 상태를 정리한다. 배포 결과 닫기는 창 상태만 비운다. 두 핸들러에서 취소 API를 부르지 않는다. 요청 전 확인 창의 취소나 다른 편집 창으로 일반화하지 않았다. |
| 53 첫 문장 | 닫기 전 식별값·결과 보관 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L160~165; nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 L28~40; nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 L315~317 L683~693 | 닫는 조작과 서버 작업 중단이 다르므로 표시된 정보를 먼저 보관하라는 사용자 지침이다. 자동 증거 보존 기능을 가정하지 않았다. |
| 53 둘째 문장 | 중단 필요 시 진행 상태와 가능 여부 인계 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L160~165; nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 L315~317 | 닫기로 취소되지 않는 두 결과를 함께 다루는 인계 지침이다. 특정 작업의 취소 가능·불가능을 단정하거나 내부 운영 명령을 사용자에게 시키지 않는다. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼 | 코드 | 검색 원문 반영 |
|---|---|---|---|
| 배포 생성 뒤 상태의 조건 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1574~1575는 생성된 배포를 아직 실행 전이라고 설명한다. | nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd L132~194는 실행할 변경이 없으면 생성 시 COMPLETED, 있으면 READY를 반환한다. | L24에서 변경 유무에 따른 차이를 최소 조건으로 명시했다. 현장 재현이나 모든 생성 요청의 동일 결과를 주장하지 않는다. |

시작 성공 제목은 U §4.5.3 L773과 실제 렌더·한국어 문구가 일치한다. 그 밖의 매뉴얼 전체가 코드와 일치한다고 판정하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 역할 |
|---|---|---|
| 실제 문의 빈도·자주 묻는 순위 | 목록이 실제 빈도 기록의 부재를 명시한다. 예상 공통 질문으로만 선정했다. | TC 문서 담당·사용자 지원 담당 |
| 현장 판본·현재 작업의 성공 여부·취소 가능 여부 | 이번 작업은 저장소 코드 대조이며 현장 접속이나 실행을 하지 않았다. 코드만으로 개별 작업의 현장 결과를 알 수 없다. | TC 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이 FAQ에 넣지 않은 답 |
|---|---|
| `00.rag/user/equipment-commands-and-polling.md` | 명령별 메뉴 조건·실행 단계·재연결과 재시작 차이·PLC 폴링 조작 |
| `00.rag/user/equipment-connection-and-command-troubleshooting.md` | 확인 지연 재조회·조회 오류와 작업 실패의 구별·브라우저 재조회 기록 복원 |
| `00.rag/user/deployment-workflow.md` | 배포 등록·실행·Hold·재실행·삭제·상세 조회 단계와 권한. 생성 결과의 번호·상태는 업무 사이 비교에 필요한 최소 식별 조건만 사용했다. |
| `00.rag/user/deployment-troubleshooting.md` | 배포 단계 대기·취소 거부·NO_CHANGE 대상의 상세 진단 |
| `00.rag/user/state-reference.md` | 상태별 전체 뜻·전이·집계표. READY·COMPLETED는 생성과 실행을 구별하는 데 필요한 조건만 남겼다. |
| `00.rag/user/permission-code-reference.md` | 권한 코드와 API 연결·메뉴 권한의 뜻. FAQ는 실행 절차를 새로 안내하지 않는다. |
| `00.rag/user/screen-behavior-limitations.md` | 아이디 찾기·권한 편집·비밀번호 초기화·PLC 폴링·재시작 재조회 제약. 모든 확인·닫기 단추에 대한 포괄 규칙을 만들지 않았다. |
| `00.rag/user/equipment-model-parameter-and-jar-update.md` | 저장·버전 생성·배포에 따른 변경 반영 경로. 목록에서 이 문서의 소유임을 확인해 FAQ로 옮기지 않았다. |

## 검사 결과

도구 판본은 Git Bash 5.2.37(1)-release, Java Temurin 21.0.9+10-LTS다. 문서 전용 작업이므로 제품 빌드·제품 시험·현장 UI 실행은 수행하지 않았다.

첫 실행 명령:

```text
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/common-faq.md
```

첫 실행은 exit 1, 대상 위반 21건이었다. 21건 모두 `원천 commit:path 없음`이었다. 원문 오류의 한 예는 다음과 같다.

```text
VIOLATION L11 원천 commit:path 없음 'nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; runLifecycleStep·성공 결과 L187~219 L265~274'
TOTAL files=51 targets=1 violations=21
```

검사기 `RagDocCheck.java` L385~412를 읽어 commit 원문 검사가 별도 Git 프로세스를 띄우며 실패 시 원문 조회를 null로 처리함을 확인했다. 앞선 직접 Git 조회에서도 같은 저장소들이 `fatal: detected dubious ownership in repository`로 거부됐다. 경로별 `git -c safe.directory=...` 조회는 HEAD와 깨끗한 작업 트리를 확인했다. 이 환경 차이를 해결하기 위해 두 번째 검사 프로세스에만 다음 Git 설정을 환경변수로 전달했다. 검사기·원천·영속 Git 설정은 수정하지 않았고 commit 검사를 생략하지 않았다.

```powershell
$env:GIT_CONFIG_COUNT='2'
$env:GIT_CONFIG_KEY_0='safe.directory'
$env:GIT_CONFIG_VALUE_0='C:/workspace_nori/nori-tc/nori-tc-ui-front'
$env:GIT_CONFIG_KEY_1='safe.directory'
$env:GIT_CONFIG_VALUE_1='C:/workspace_nori/nori-tc/nori-tc-ui-backend'
bash C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh --dump 00.rag/user/common-faq.md
```

사람 검사 대상은 40절 미만이므로 전체 3절이다.

| 절 | 제목만으로 질문 식별 | 원천 일치 | 독립 답 | 다른 절·앞선 문서와의 중복 판단 |
|---|---|---|---|---|
| 접수·생성·완료는 화면마다 무엇을 확인하는가 | 적합. 서로 다른 업무의 완료 범위를 묻는다. | 접수·조회·생성 후 상태 분기를 대조했다. | 시작 결과와 배포 생성 결과의 차이 및 변경 없는 예외를 한 절에서 읽는다. | 업무별 절차·상태 사전은 복제하지 않고 비교에 필요한 두 결과만 썼다. |
| 같은 설비의 작업 ID와 배포 번호는 어떻게 구별하는가 | 적합. 동일 설비의 서로 다른 결과 식별값을 묻는다. | 렌더·한국어 문구·조회 API의 입력·서버 응답을 대조했다. | 식별값 종류·대상 설비·문의 방법을 한 절에서 읽는다. | 상태 뜻이나 개별 화면 조회 순서를 복제하지 않고 업무 사이 인계 관계만 설명한다. |
| 결과 창의 확인·닫기와 작업 취소는 어떻게 다른가 | 적합. 결과 창 닫기와 서버 작업 취소의 차이를 묻는다. | 두 단추의 이벤트와 실제 닫기 핸들러를 대조했다. | 닫기의 영향·기록·중단 필요 시 인계까지 한 절에서 읽는다. | 편집 취소·명령 재조회 복원·배포 취소 절차는 복제하지 않고 두 결과 창의 공통 경계만 설명한다. |

원문 본문 6문단의 모든 문장을 위 표의 14개 행으로 대조했다. 제목 정확 중복 0건, H3 이상 0건, admin objectKey 참조 0건을 PowerShell 정규식 검사로 확인했다. 제목 모양이 3회 이상 반복되는 틀이 없고, 내용 없는 틀 문장·이름 토막 뜻풀이·잘린 문장·오답 인용·비밀값 표기가 없음을 전체 문장으로 읽어 확인했다. 입력값을 받거나 명령을 새로 실행하는 FAQ 절은 없으며 업무별 필수 입력·권한·오류 문구를 축약 재작성하지 않았다.

검색 원문은 BOM·CR·NUL이 없고 NFC이며 LF 53줄이다. 검사 준비 중 발견한 본문의 한자 오타와 잘못 짚은 제목 근거 줄은 실제 문구·제목 매핑을 읽고 첫 검사 전에 바로잡았다. 다른 파일은 수정하지 않았다.

일회성 검사 파일은 만들지 않았다. 지정된 기존 `doc-52-common-faq/work/`의 항목 수가 0임을 확인했다. 기존 작업 폴더·진행 파일은 이 세션의 정리 대상으로 삼지 않았다.

두 번째 실행이 최종 기계 검사다. 검색 원문의 마지막 편집 뒤 같은 검사기를 재실행하여 exit 0을 확인했다. 대상 위반 0건, CROSS VIOLATION 0건, MASK 출력 0건이다. H2는 3개, 자식 6개, 최대 자식 126자, 최대 부모 242자다. 첫 검사와 최종 검사의 검색 원문 SHA-256은 위 대상 절의 값으로 동일하다. 최종 검사 마지막 10줄은 다음과 같다.

```text
    L24-24 문단 126  | 배포 생성 결과에는 배포 번호와 상태가 표시된다. 실행할 변경이 있으면 READY에서 실행을 기다리고, 변경이 전혀 없으면 생성 단계에서 COMPLETED로 끝날 수 있다. 설비 시작 결과와 배포 생성 결과를 구별해 기록한다.
§ L26-40 [30] 같은 설비의 작업 ID와 배포 번호는 어떻게 구별하는가   parent=212 children=2 sources=8
    L38-38 문단 109  | 설비 명령 결과 창의 「작업 ID」는 개별 명령 결과를 찾는 값이다. 「Deploy View」 위쪽의 # 뒤 번호는 배포 한 건을 찾는 값이며, 배포 안의 대상 설비는 「EQP ID」로 구별한다.
    L40-40 문단 102  | 설비 명령과 배포를 함께 문의할 때는 번호 옆에 「작업 ID」 또는 배포 번호라고 적는다. 설비 ID·조작한 명령·발생 시각·각 화면의 결과를 번호와 짝지어 운영 담당자에게 전달한다.
§ L42-53 [27] 결과 창의 확인·닫기와 작업 취소는 어떻게 다른가   parent=210 children=2 sources=5
    L51-51 문단 85  | 설비 명령 결과 창의 「확인」과 배포 생성 결과 창의 「닫기」는 결과 창을 닫는 조작이다. 접수한 명령이나 생성한 배포를 취소하는 요청을 보내지 않는다.
    L53-53 문단 124  | 진행 결과를 보관하려면 닫기 전에 「작업 ID」 또는 배포 번호와 표시 결과를 기록한다. 닫은 뒤 실행 중단이 필요하면 설비 ID·번호·요청 시각·현재 결과를 운영 담당자에게 전달해 진행 상태와 중단 가능 여부를 확인한다.
SUMMARY sections=3 children=6 maxChild=126 maxParent=242
CORPUS docs=51/53 sections=872 part={admin=14, user=37} type={FAQ=1, 개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=12, 트러블슈팅=12}
TOTAL files=51 targets=1 violations=0
```

검사 호출은 총 2회이며 동일한 거부를 그대로 반복하지 않고 두 번째 호출의 Git 조회 환경만 수정했다. 두 프로세스 모두 종료코드를 회수했다. 완료 결과를 확인한 로컬 시각은 2026-11-19 07:23:35 +09:00이다. 전체 요청 wall time·provider 사용량·비용은 별도로 측정하지 않아 미확인이다. 독립 AI 호출은 0회다. 남은 대상 문서 결함과 사용자 결정 항목은 없으며, 현장 동작·실제 검색 반영·정답률은 이 작성 검사로 검증하지 않았다.

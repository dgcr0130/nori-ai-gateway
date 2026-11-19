# 화면 안내와 실제 동작이 다른 곳 — 작성 기록

## 대상

| 항목 | 확인값 |
|---|---|
| objectKey | `00.rag/user/screen-behavior-limitations.md` |
| 구획·열람 권한 | user · `[AI_DOC_READ]` |
| 유형 | 알려진 제약 |
| 검색 원문 | `rag-documents/nori-tc/00.rag/user/screen-behavior-limitations.md` |
| 검색 원문 SHA-256 | `6fa649987c8184558335114189c319020c896e7e73876a185c1de5d2d4eb6696` |
| 줄 수·절 수 | 80줄 · H2 5절 |
| 확인일 | 2026-11-19 한국 시간 |
| 작성 범위 | 지정된 검색 원문과 이 작성 기록만 생성. 제품 변경·독립 호출·업로드·커밋 없음 |

작성 규칙과 53개 문서 목록 전체, U §10.4를 읽었다. 앞선 user 작성 기록의 매뉴얼/코드 차이를 후보로 살피고, 채택한 제한은 현재 화면 렌더·한국어 문구·API·서버 처리와 직접 대조했다. 이 기록은 자체 확인이며 메인 세션의 독립 검증을 대신하지 않는다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-front | `9fe69a75dcd3` | 전체 status 출력 없음. 최종 본문 인용 파일 10개를 경로 지정 status로 다시 확인, 출력 없음·exit 0 |
| nori-tc-ui-backend | `985d17c604dd` | 전체 status 출력 없음. 최종 본문 인용 파일 5개를 경로 지정 status로 다시 확인, 출력 없음·exit 0 |
| nori-tc-exec | `f1d5e0a8e727` | 전체 status 출력 없음. 최종 본문 인용 파일 2개를 경로 지정 status로 다시 확인, 출력 없음·exit 0 |

명령은 저장소별 `git -c safe.directory=<해당 저장소 절대경로> -C <해당 저장소 절대경로> rev-parse --short=12 HEAD` 및 `status --short -- <대상 상대경로들>`이다. 전역 Git 설정은 바꾸지 않았다. 경로별 확인 목록은 검색 원문의 모든 원천 행에서 중복을 제거한 17개 파일이다. 최초 safe.directory 없는 형제 저장소 조회는 dubious ownership으로 실패했으며, 이후 명령별 해당 경로 지정으로 읽었다. Git의 사용자 전역 ignore 파일 접근 경고는 있었지만 명령은 exit 0, 대상 상태 표준 출력은 비어 있었다.

현재 제품 저장소는 main, `70cf6cd`이며 기존 `rag-documents/`는 untracked였다. 제품 worktree는 하나였다. 기존 문서는 수정하지 않았다. 적용 표시는 확인한 소스 판본이며 현장 배포 판본 확인이 아니다.

## 절별 사실 대조

### 아이디·비밀번호 찾기의 확인은 메일 발송이 아니다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 9, 16 앞 문장 | 찾기 창 입력은 서버로 전송되지 않음 | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 L23~99 | RecoveryModal의 입력은 제출 요청에 연결되지 않는다. form submit은 preventDefault이며 인증 API 함수 호출이 없다. |
| 16 뒤 문장 | 확인은 창 닫기이며 복구 접수가 아님 | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 L75~95; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L20~32 | 확인과 취소 모두 onClose. 이메일 발송 안내는 한국어 문구에 있지만 요청 동작은 없다. 틀린 발송 안내는 검색 원문에 인용하지 않았다. |
| 18 앞 문장 | 반복 제출로도 요청이 생기지 않음 | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 L60~95 | 반복 클릭도 같은 닫기 동작이다. 서버 장애나 메일 지연을 원인으로 제시하지 않았다. |
| 18 뒤 문장 | 관리자에게 아이디 확인·초기화 요청 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L103~140 | U §2.2의 대체 경로다. AuthController.java @ 985d17c604dd의 /login L96, /logout L152, /me L212, /csrf L255 매핑도 읽었으며 로그인 찾기 창에서 호출하는 복구 경로는 없다. |

### 그룹 권한 보유 수에는 저장하지 않은 체크도 들어간다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 20, 35 앞 문장 | 권한 보유 수는 미저장 체크를 포함 | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 L67~75 L115~126 L214~221; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L948 | grantedActiveCount가 pendingChanges를 우선 반영하며 화면은 그 수를 권한 보유로 렌더한다. |
| 35 뒤 문장 | 체크만으로 서버 변경 없음, 저장 없이 닫으면 폐기 | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 L49~64 L128~141 L186~201 | toggle은 Map만 수정하고 닫힘 시 Map을 초기화한다. 저장 중 닫기 차단도 확인했다. 문장은 저장하지 않은 변경을 대상으로 한정했다. |
| 37 첫 문장 | 저장 시 변경 요청 전송 | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 L143~162 L300~307; nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 L400~436; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd L213~243 | 저장에서 각 권한별 POST 또는 DELETE를 보낸다. hooks/useGroupMutations.ts L100~124의 API 연결과 성공 후 재조회도 읽었다. |
| 37 나머지 문장 | 일부 성공은 반영되고 실패분만 남음, 전체 취소 아님 | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 L156~183; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd L101~114 | 독립 요청의 Promise.allSettled 결과 중 rejected만 stillPending에 남긴다. Facade는 요청별 upsert/delete를 호출한다. 전체 변경을 묶는 단일 서버 요청은 아니다. |
| 39 첫 문장 | 기본 권한 연결 | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 L142~148 L259~265; nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 L787~788; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd L142~174 | /api/group은 GROUP_WRITE, /api/permission은 PERMISSION_WRITE, 전체 메서드 매핑이다. group.api.ts L347~396의 실제 조회 경로와 비교했다. UiSecurityConfig.java L142~143은 보호 요청에 권한 캐시를 적용한다. 현장 DB 권한은 확인하지 않았다. |
| 39 둘째 문장 | 부여할 권한을 관리자도 보유해야 함 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd L57~66; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd L101~105 | 대상 권한을 조회하고 actor 보유 집합에 없으면 거부한다. GROUP_WRITE만으로 모든 권한을 부여할 수 있다고 쓰지 않았다. |
| 39 마지막 문장 | 권한 거부와 미저장을 구별해 인계 | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 L128~141 L179~181 L280~287 | 체크 단계에는 요청이 없지만 저장 실패는 errorMessage로 렌더된다. 그룹·시각·문구 인계는 사용자 작성 규칙에 따른 최소 안내다. |

### 비밀번호 초기화 창의 안내는 요청 결과와 따로 표시된다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 41, 49 첫 문장 | 요청 중·실패에도 완료형 설명 표시 | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 L72~104 L115~125; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L872~878 | Description은 resetStatus와 무관하다. 한국어 완료형 문장을 오답 인용으로 검색 본문에 옮기지 않았다. |
| 49 뒤 문장 | 임시 값 존재는 서버 적용 증거 아님 | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 L71~87 L128~145; nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 L231~253; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd L254~267 | 임시 값은 초기 렌더 때 생성된다. 서버 요청이 성공해야 success 상태가 되며 대상 부재 시 서버는 오류를 반환한다. |
| 51 앞 두 문장 | 진행·오류를 구별하고 오류 시 값을 전달하지 않음 | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 L147~158; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L878~881 | pending 문구와 errorMessage는 별도 상태로 렌더된다. 값 전달·안전한 초기화 절차 전체는 기존 사용자 관리 문서에 남겼다. |
| 51 마지막 문장 | 문의에 비밀번호 제외 | 사용자 작성 지시 §4·§8, docs/rag-document-writing-rules.md의 비밀값 제외 규칙 | 제품 동작 주장 아닌 안전한 인계 안내다. 실제 비밀번호나 예시 비밀번호는 읽거나 기록하지 않았다. |

### PLC 폴링의 초록색 안내로는 처리 결과를 구별할 수 없다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 53, 62 첫 문장 | PLC 폴링에서 실패·복구 필요·확인 지연에도 초록 표시 가능 | nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 L1235~1243; nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L38~72 L116~125; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453; nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 L124~142 | PLC 조건으로 영역을 렌더한다. 결과 종류는 호출부에서 전달되지 않고 dispatched만 반환되어 결과 판정에 쓰인다. FAIL·REPAIR_REQUIRED·DELAYED는 값 반환이며 예외가 아니다. |
| 62 뒤 문장 | 발행 여부로 완료 안내를 결정 | nori-tc-ui-front/src/shared/lib/async-command-guard.ts @ 9fe69a75dcd3 L25~37; nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L424~453 | guard는 command가 예외 없이 반환하면 dispatched=true를 준다. 결과의 성공 여부와 구별된다. |
| 64 | 실제 결과 확인에 필요한 인계 정보 | nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 L35~72 L80~125; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd L405~445 | 설비 ID·그룹으로 Start/End를 요청하고 해당 그룹과 결과 문구를 렌더한다. eqp.api.ts L773~852의 경로·traceId 반환도 읽었다. 수동 내부 조회 명령이나 자동 재시도를 제시하지 않았다. |

operation-polling-contract.test.ts @ nori-tc-ui-front 9fe69a75dcd3 L77~140의 terminal·DELAYED 반환 기대값도 읽었다. 이는 소스 대조이며 이 세션에서 제품 시험을 실행한 PASS가 아니다.

### Eqp Restart의 종료 단계가 지연되면 재조회가 시작을 이어 주지 않는다

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 66, 78 첫 문장 | 종료 단계 지연이면 시작 단계 미발행 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L236~247; nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 L265~272 | 실제 메뉴는 Eqp Restart다. endOutcome가 PASS가 아니면 반환하여 start 호출을 건너뛴다. |
| 78 뒤 문장 | 다시 조회는 기존 작업만 읽고 시작을 이어주지 않음 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L360~378; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1112 | recheck는 trackOperation만 호출하며 성공 시 조회 캐시를 갱신한다. 시작 mutation 호출은 없다. 실제 단추 문구는 「다시 조회」다. |
| 80 앞 문장 | 종료 성공을 재시작 완료 제목으로 표시 가능 | nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 L258~274 L360~371; nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L48~53 L103~119; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L85 | 기존 action=restart를 그대로 성공 상태에 적용한다. 제목에 사용한 문구가 실제 시작 발행을 증명하지 않는 조건을 한정했다. |
| 80 뒤 문장 | 지연·재조회 경과와 작업 ID를 인계 | nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 L126~139; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1110~1112 | 결과 본문에 operationId를 넘기고 지연+ID 존재 조건에서 재조회 단추가 보인다. 세부 조작 순서·시작 재실행은 기존 명령 문서에 맡겼다. |

## 매뉴얼과 코드가 다른 곳

| 후보 | 매뉴얼·화면 안내 | 현재 코드 대조 | 본문 처리 |
|---|---|---|---|
| U §10.4 설비 수정 선택 칸 | U L2568은 Eqp Info Update에서도 Model Name·Model Version·Business Jar를 고를 수 있는 것처럼 묶어 설명 | EqpManageFormModal.tsx @ 9fe69a75dcd3 L971~1062는 해당 구역을 create에서만 표시한다. L949~967의 Gateway Jar 선택은 남지만 EqpControllerCommandSupport.java @ 985d17c604dd L44~65는 기존 모델·JAR를 유지한다. | 과거의 모든 선택 칸이 그대로 존재한다고 쓰지 않음. 선택 칸 부재로 사라진 부분과 남은 서버 제한을 구분해 모델·플러그인 제약 소유 문서로 넘김 |
| Model Info Update 설명 | ko.json @ 9fe69a75dcd3 L580은 연결 모델 버전·Business JAR 변경을 안내 | EqpModelBindingModal.tsx L223~230은 buildEqpUpdateRequest를 사용하고, EqpUpdateRequest.java @ 985d17c604dd L25~58에는 모델·JAR 변경 필드가 없다. 서버는 기존 연결을 유지한다. | 현존 제한이나 다른 소유 문서 범위이므로 이 본문에는 수록하지 않음 |
| 그룹 권한 체크 | U §10.4 L2569는 저장이 필요하다고 올바르게 설명한다. ko.json L943의 화면 설명은 즉시 반영이라고 안내 | GroupPermissionsModal.tsx L128~183은 draft 후 저장하며 L115~123의 보유 수도 draft를 포함 | 틀린 화면 문장은 인용하지 않고 미저장 수·부분 반영·저장 확정의 차이를 수록 |
| 로그인 찾기 | U §10.4 L2570 및 §2.2는 메일 미발송을 설명하지만 ko.json L25~27의 화면 안내는 메일 발송을 예고 | LoginPage.tsx L60~95에는 전송 없이 닫기 동작만 존재 | 접수 자체가 없다는 제한과 관리자 요청만 수록 |
| 비밀번호 초기화 안내 | U §9.2.3 L2352~2356은 완료형 안내를 결과 확인 흐름에 둔다 | UserPasswordResetModal.tsx L119~125는 요청 상태와 무관하게 설명을 렌더하고 L147~158은 진행·오류를 별도 표시 | 완료형 설명을 실제 성공 신호로 쓰지 않도록 제한·영향만 수록 |
| PLC 폴링 완료 표시 | U §4.12.3 L1059~1061·§4.13.3은 성공 시 초록 완료로 설명 | useEqpLifecycleCommands.ts L424~453은 outcome을 UI에 전달하지 않고 dispatched만 반환 | 초록 안내가 성공·실패·지연을 구별하지 못하는 범위를 수록 |
| Eqp Restart 지연 | U §4.6.3은 종료 후 시작을 설명하고 §3.4는 지연 결과 재조회를 안내하지만 종료 지연 뒤의 연결 중단은 설명하지 않는다 | useEqpLifecycleCommands.ts L236~247·L360~378은 종료 지연 뒤 시작을 이어주지 않는다 | 종료 단계 지연이라는 조건과 표시의 한계만 수록 |

모델/JAR 제한 전체가 해소됐다고 판단하지 않았다. 특히 Model Info Update의 서버 미반영은 여전히 확인되었다. 해소 근거로 남긴 것은 U의 Eqp Info Update 선택 칸 전부 존속 설명 중 create 전용 구역으로 바뀐 부분뿐이다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장의 적용 이미지·커밋과 각 제한의 실제 재현 여부 | 소스 HEAD와 저장소 파일만 확인했다. 서버 접속·브라우저 실행·현장 배포 조회는 수행하지 않았다. | TC 운영 담당·화면 개발 담당 |
| 모든 화면 차이의 완전한 목록·해소 일정 | U §10.4 및 앞선 작성 기록의 후보를 대상으로 작성했으며 제품 전 화면 조사나 확정 일정 원천이 없다. | TC 제품 담당·화면 개발 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 후보·겹치는 내용 | 소유 문서 objectKey | 이 문서에서의 경계 |
|---|---|---|
| 모델·JAR 미반영, Gateway JAR 변경 경계, 플러그인 적용 조건 | `00.rag/user/model-plugin-limitations.md` | U §10.4의 해당 행은 현재 코드로 대조했지만 이 문서에는 넣지 않았다. |
| 모델·파라미터·JAR 갱신의 실제 배포 절차, Check In 전 Save 누락·비교 화면 차이 | `00.rag/user/equipment-model-parameter-and-jar-update.md` | 기존 절차가 소유. 이 문서에서 배포나 버전 생성 절차를 반복하지 않는다. |
| 설비 수정 숫자 기본값·범위, 단건 정보의 모델/JAR 표시, 수정 창 구성 | `00.rag/user/equipment-registration-and-settings.md` | 필수 입력·허용값과 표시 해석이므로 절차 문서에 유지한다. |
| 찾기 창을 여닫는 절차, 프로필 부서 필수·비밀번호 정책·로그아웃·라인 조회·셀 조작 | `00.rag/user/login-and-screen-basics.md` | 본문에는 미전송 제한과 최소 대체 요청만 남겼다. 나머지는 로그인/화면 사용 절차가 소유한다. |
| 초기화·권한 관리의 전체 조작, 그룹 활성 체크·중복 등록·삭제 | `00.rag/user/user-group-and-permission-management.md` | 제한 이해에 필요한 최소 저장·거부 조건만 남겼다. 그룹 활성·중복·삭제의 의미와 작업은 복제하지 않는다. |
| Eqp Start/End/Restart/Reconnect·PLC 그룹의 실행·결과 확인·재실행 절차 | `00.rag/user/equipment-commands-and-polling.md` | 완료 표시의 제한과 영향·인계만 수록하고 정상 명령 순서나 단계별 복구 경로는 수록하지 않았다. |
| DLQ TTL·대시보드 조회 오류·빈 목록의 조건 | `00.rag/user/dlq-quarantine-and-repair-task.md`, `00.rag/user/dlq-quarantine-troubleshooting.md` | 후보 대조 결과 표시 의미·증상 진단 소유 사실이므로 재수록하지 않았다. |
| 데모 목록 조회 오류 표시·실행 건수·업로드 오류 | `00.rag/user/demo-execution.md`, `00.rag/user/demo-troubleshooting.md` | 조회·실행 결과 확인 및 오류 진단의 소유 범위를 유지했다. |
| Live-Map 저장 성공 문구·기본 구조·자동 배치·치수·색상·삭제·통신 로그 오류 | `00.rag/user/live-map-layout-editing.md`, `00.rag/user/live-map-status.md`, `00.rag/user/live-map-troubleshooting.md` | 화면 표시·편집 조건·증상별 확인 경로를 기존 문서에 남겼다. |
| 모델 편집 Action Name·선택 값·PLC 메시지명·Flow 체크인·후보 조건 | `00.rag/user/model-details-and-flow-editing.md`, `00.rag/user/model-registration-and-branching.md` | 모델 편집 작업의 계약·제한값으로 이미 설명되어 있으며 별도 화면 제약 요약으로 복제하지 않는다. |
| 배포 초기화·무변경 재실행, 오류 문구 범위·메뉴 이름·상태 색·용어·업무 흐름·통신 수정 | `00.rag/user/deployment-workflow.md`, `00.rag/user/deployment-troubleshooting.md`, `00.rag/user/login-permission-and-screen-troubleshooting.md`, `00.rag/user/equipment-connection-and-command-troubleshooting.md`, `00.rag/user/getting-started.md`, `00.rag/user/tc-components-and-terms.md`, `00.rag/user/lot-carrier-work-and-system-flow.md`, `00.rag/user/secs-hsms-troubleshooting.md`, `00.rag/user/socket-troubleshooting.md`, `00.rag/user/plc-troubleshooting.md`, `00.rag/user/model-flow-troubleshooting.md` | 앞선 작성 기록의 후보에서 실제 동작 설명·명칭 정정·서버 진단에 해당하는 내용을 제외했다. 현재 결함이나 새 제약이라고 재선언하지 않았다. |

기존 문서의 동일 절차를 복제하지 않았으며, 주어진 소유 범위상 제한 설명에 꼭 필요한 조건은 짧게 남겼다. 기존 문서 수정이 필요한 새 확정 결함은 이번 대조에서 발견하지 않았다.

## 검사 결과

최종 검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/screen-behavior-limitations.md
```

실행은 Git Bash `C:/Program Files/Git/usr/bin/bash.exe`를 명시했다. 최종 실행 exit 0. 출력 10줄은 아래와 같다. maxChild=155, maxParent=340, H2 최장 43자로 모두 상한 이내다. MASK·CROSS VIOLATION·대상 위반은 0건이다.

```text
    L51-51 문단 136  | 「서버에 비밀번호 변경을 적용하는 중...」과 오류 표시를 기준으로 결과를 구별한다. 오류가 있으면 표시된 임시 값을 전달하지 않고, 운영 관리자에게 대상 사용자·요청 시각·오류 문구를 전달한다. 임시 비밀번호 자체는 문의 내용에 넣지 않는다.
§ L53-64 [32] PLC 폴링의 초록색 안내로는 처리 결과를 구별할 수 없다   parent=236 children=2 sources=5
    L62-62 문단 126  | 확인한 판본에서 PLC 설비의 「Polling Group」은 「Start」·「End」의 처리 결과가 실패·복구 필요·확인 지연이어도 초록색 완료 안내를 표시할 수 있다. 명령을 발행했다는 결과만 표시 판단에 사용하기 때문이다.
    L64-64 문단 109  | 실제 폴링 시작·중단 여부를 확인해야 하면 운영 담당자에게 설비 ID·입력한 그룹 값·Start 또는 End·요청 시각·화면 문구를 전달한다. 이 영역의 초록색만으로 폴링 상태를 확정할 수 없다.
§ L66-80 [43] Eqp Restart의 종료 단계가 지연되면 재조회가 시작을 이어 주지 않는다   parent=237 children=2 sources=8
    L78-78 문단 104  | 확인한 판본에서 「Eqp Restart」의 종료 단계가 확인 지연으로 끝나면 시작 단계는 발행되지 않는다. 이후 「다시 조회」로 종료 성공을 받아도 시작 단계는 자동으로 이어지지 않는다.
    L80-80 문단 132  | 이 경로에서는 종료 성공을 재시작 완료 제목으로 표시할 수 있어 설비가 다시 시작됐다고 오인할 수 있다. 운영 담당자에게 설비 ID·Eqp Restart 요청 시각·확인 지연 뒤 재조회 여부·표시된 작업 ID·현재 화면 결과를 전달한다.
SUMMARY sections=5 children=11 maxChild=155 maxParent=340
CORPUS docs=37/53 sections=516 part={admin=9, user=28} type={개념=4, 사용 절차=13, 알려진 제약=1, 운영=7, 트러블슈팅=12}
TOTAL files=37 targets=1 violations=0
```

검사 호출은 총 3회다. 최초 실행은 세션 핸들을 보존하지 못해 종료 결과를 회수하지 않았고 합격 근거로 쓰지 않았다. 본문 편집 후 중간 검사와 최종 검사는 각각 exit 0을 회수했다. 최초 실행 추적을 위한 Win32_Process 조회는 접근 거부되어 결과를 얻지 못했으며 프로세스 종료·권한 변경을 하지 않았다. 재실행은 본문을 수정한 새 입력에 대한 검사다. 제품 빌드·제품 단위 시험·브라우저·원격 서비스는 실행하지 않았다.

### 사람 검사

5절 전부를 읽었다. 각 절의 제목 질문·원천 일치·독립 답·소유 범위를 다음과 같이 판정했다.

| 절 | 제목만으로 질문 인식 | 원천 일치·독립 답 | 중복·범위 판단 |
|---|---|---|---|
| 찾기 창 | 메일 미발송·접수 부재를 식별한다. | 전송 없음·확인 닫기·관리자 대체 경로를 함께 확인했다. | 로그인 절차를 옮기지 않고 U §10.4의 제한만 요약했다. |
| 권한 보유 수 | 체크와 저장 여부의 혼동을 식별한다. | 집계·닫기·부분 성공·기본 권한·부여 가드를 대조했다. | 단추 진입·검색·권한 코드 뜻을 반복하지 않았다. |
| 초기화 설명 | 설명 문장과 요청 결과가 다름을 식별한다. | 무조건 설명 렌더와 별도 진행·오류 분기를 대조했다. | 비밀번호 생성·복사·정책 절차는 복제하지 않았다. |
| PLC 완료 안내 | 초록색만으로 결과를 알 수 없는 조건을 식별한다. | 호출 결과 손실·발행 여부·초록색 렌더를 연결했다. | 그룹 정의·시작 순서·서버 실패 진단은 복제하지 않았다. |
| Eqp Restart 재조회 | 종료 지연 이후 자동 시작이 없는 조건을 식별한다. | 조건부 start 호출과 기존 operation 재조회·제목 매핑을 연결했다. | 재연결 비교·시작 재실행의 정상 절차는 복제하지 않았다. |

정확히 같은 제목 0쌍, 이름만 바꾼 제목 틀 3회 반복 0건, 자식 본문 복제 0건이다. 11개 문단 모두 주체·조건·제한·영향 또는 인계에 기여하며 빈 틀 문장·이름 토막 뜻풀이·미확인 숫자·오답 인용은 남기지 않았다. user 본문에 admin objectKey·링크·DB/설정 접근 조치는 없다. mask-v1 검사 후 필요한 문구·코드가 보존됐다.

자체 대조에서 「결과 다시 조회」를 실제 「다시 조회」로 정정하고, 메뉴를 실제 「Eqp Restart」로 정정했다. 권한 부여 가드와 기본 권한 조건도 서버 코드로 확인해 본문·원천에 반영한 뒤 최종 검사를 다시 했다. 남은 작성 결함 0건, 사용자 결정 0건이다.

### 임시 자원·미검증

검사 출력은 지정 work 폴더의 `check-output.txt`·`check-final.txt`에만 임시로 보관했고 최종 출력과 종료 결과를 이 기록에 보존했다. 제품 소스·재사용 시험·기존 유효 산출물·세션이 원래 갖고 있던 prompt/events/result 등은 삭제 대상이 아니다.

정리한 정확한 경로:

- `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-38-screen-behavior-limitations/work/check-output.txt`
- `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-38-screen-behavior-limitations/work/check-final.txt`

두 파일은 이 세션이 만든 검사 출력이다. 삭제 전 절대경로가 지정 work 안인지 확인했다. 상위 경로의 reparse point가 없고 두 파일은 Archive 속성만 있었다. 각 출력의 검사 프로세스는 exit 0으로 종료했고 파일을 FileShare.None으로 독점 열기·닫기해 활성 핸들 잠금이 없음을 확인했다. native PowerShell `Remove-Item -LiteralPath <각 정확한 경로>`로 비강제·비재귀 삭제하여 exit 0, 각 `Test-Path -LiteralPath`는 False였다. 최종 work 폴더 열거 결과는 비어 있었다. 원래 존재하던 work 폴더는 유지했다.

실제 현장 UI 재현·메일 시스템·사용자별 DB 권한·배포 판본은 미검증이다. 확인된 것은 저장소 코드·시험 기대값의 정적 대조와 문서 검사다. 별도 AI 호출 0회, provider 사용량·비용 및 전체 작업 wall time은 별도 계측하지 않아 미확인이다.

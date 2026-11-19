# 로그인·메뉴·화면 공통 조작 작성 기록

## 대상

- objectKey: `00.rag/user/login-and-screen-basics.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문 SHA-256: `d2797a9c03f40c92310691305fd266d1cfd5c8dbadc887ee430046ccb07d8beb`
- 검색 원문 줄 수: 302
- 절 수: 18
- 확인일: 2026-11-18 (한국 시간)
- 이 세션이 직접 조사·작성·자체검사했다. 다른 Agent 호출, 제품 변경, git add·commit·push, 업로드는 하지 않았다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-front -C ../nori-tc-ui-front status --short -- src tests`: 상태 행 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-ui-backend -C ../nori-tc-ui-backend status --short -- ui`: 상태 행 없음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-exec -C ../nori-tc-exec status --short -- docs/completion-report/07-user-manual.md`: 상태 행 없음, exit 0 |

판본은 각 저장소의 `rev-parse --short=12 HEAD`로 확인했다. 인용한 추적 파일은 위 상태 검사 범위에 속하며, 작성 전과 최종 원문 작성 뒤 검사했다. 전역 ignore 파일 읽기 Permission denied 경고는 있었으나 대상 상태 행은 없고 종료코드는 0이다. 전역 설정은 수정하지 않았다.

최초 형제 저장소 Git 조회는 소유자 차이로 dubious ownership 오류가 발생했다. 이후 해당 저장소 safe.directory를 읽기 명령에만 지정했다. 검사기 자식 Git에는 같은 세 저장소 설정을 GIT_CONFIG_COUNT=3 환경으로 전달했다. 저장소·전역 config 파일과 검사기는 수정하지 않았다.

`docs/rag-document-writing-rules.md`와 `docs/tc-rag-document-list.md` 전체, U §2~3 전체를 읽었다. 시작 시 `rag-documents/nori-tc/00.rag/`가 없어 앞선 검색 문서는 없었다.

관련 시험 기대값도 읽었다. front `tests/unit/login-landing.test.ts`, `app-navigation-visibility.test.ts`, `logout-client-state-cleanup.test.ts`는 권한별 첫 메뉴·빈 그룹 제거·로그아웃 캐시 정리를 다룬다. backend `PasswordPolicyValidatorTest.java`와 `UiPasswordPolicyServiceTest.java`는 길이·복잡도와 최근 이력 재사용을 다룬다. 시험을 실행하거나 과거 PASS를 재사용한 것은 아니다.

## 절별 사실 대조

본문의 모든 문장·표 자료 행을 하나씩 대응시켰다. 같은 원문 줄에 여러 문장이 있으면 문장별 행으로 나누고 원문 줄 번호를 반복했다. 문장·행 요지 칸은 실제 본문을 그대로 포함한다. 운영 담당자에게 증상·시각·대상·화면 결과를 전달하도록 하는 문장은 이번 사용자 지시와 화면 표시를 근거로 한 안내이며 자동 인계 기능을 뜻하지 않는다.

### 로그인 화면에서 업무 화면으로 들어가기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L20 | 1. 운영자가 안내한 TC 주소를 열고 「아이디」와 「비밀번호」를 입력한다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L182~290 / nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema·useLogin L22~78 / nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; login L129~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L96~139 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 문구 L12~22 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 2.1.1 절차 L68~73 | 화면 입력과 zod trim/min 조건, API 요청·서버 로그인, 운영자 주소 안내를 대조했다. |
| L20 | 아이디는 앞뒤 공백을 뺀 뒤 2자 이상, 비밀번호는 8자 이상이어야 로그인 요청을 보낸다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L182~290 / nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema·useLogin L22~78 / nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; login L129~169 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L96~139 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 문구 L12~22 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 2.1.1 절차 L68~73 | 화면 입력과 zod trim/min 조건, API 요청·서버 로그인, 운영자 주소 안내를 대조했다. |
| L21 | 2. 비밀번호 오른쪽 눈 모양 단추로 「비밀번호 표시」와 「비밀번호 숨김」을 전환한다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L182~290 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 문구 L12~22 | 눈 단추의 type 및 표시·숨김 문구 전환을 확인했다. |
| L22 | 3. 「로그인」을 누른다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L182~290 / nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema·useLogin L22~78 / nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; login L129~169 / nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveLoginLandingRoute L146~170 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L96~139 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 문구 L12~22 | 성공 후 me 권한에 따라 첫 메뉴 경로로 이동한다. |
| L22 | 성공하면 좌측 메뉴 순서에서 계정이 접근할 수 있는 첫 화면으로 이동한다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L182~290 / nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema·useLogin L22~78 / nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; login L129~169 / nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveLoginLandingRoute L146~170 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L96~139 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 문구 L12~22 | 성공 후 me 권한에 따라 첫 메뉴 경로로 이동한다. |

### 같은 브라우저에 아이디 기억시키기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L30 | 로그인 화면에서 「아이디 저장」을 켜고 「로그인」을 누르면 입력한 아이디의 앞뒤 공백을 제거해 이 브라우저에 저장한다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; SAVED_USER_ID_KEY·handleSubmit L102~161 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 아이디 저장 L19~19 | 제출 직전에 저장하며 API 성공을 기다리지 않는다. mount 때 저장값을 채운다. |
| L30 | 로그인 성공 전에도 저장되며, 다음에 로그인 화면을 열면 아이디가 채워진다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; SAVED_USER_ID_KEY·handleSubmit L102~161 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 아이디 저장 L19~19 | 제출 직전에 저장하며 API 성공을 기다리지 않는다. mount 때 저장값을 채운다. |
| L32 | 저장한 아이디를 지우려면 「아이디 저장」을 끄고 「로그인」을 누른다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; SAVED_USER_ID_KEY·handleSubmit L102~161 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 아이디 저장 L19~19 | 체크를 끈 제출은 저장 키를 제거한다. |

### 로그인 입력 오류와 인증이 풀린 화면 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L45 | 아이디나 비밀번호가 비었거나 최소 길이에 못 미치면 입력 칸 아래 안내를 보고 고친다. | nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema L22~33 / nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L200~298 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 입력 검사 L833~836 | 칸별 오류와 서버 오류 렌더를 대조했다. |
| L45 | 서버가 로그인을 거부하면 로그인 화면에 오류 문구가 표시된다. | nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema L22~33 / nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L200~298 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 입력 검사 L833~836 | 칸별 오류와 서버 오류 렌더를 대조했다. |
| L47 | 로그인하지 않았거나 현재 사용자 인증을 확인하지 못하면 보호된 업무 화면 대신 로그인 화면으로 이동한다. | nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 ; RequireAuth L19~35 | me 조회 오류·사용자 없음이면 로그인으로 이동한다. |
| L47 | 다시 로그인한다. | nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 ; RequireAuth L19~35 | me 조회 오류·사용자 없음이면 로그인으로 이동한다. |
| L49 | 로그인 후 「접근 권한이 없습니다」가 보이면 운영 관리자에게 사용할 화면의 접근 권한을 요청한다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveLoginLandingRoute L161~170 / nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 ; RequirePermission L19~41 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 안내 L1104~1106 | 조회 권한이 없으면 화면 내 안내를 표시하며 안내 문구는 관리자 권한 요청이다. |

### 아이디나 비밀번호를 잊었을 때 요청하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L58 | 아이디를 잊었거나 비밀번호 초기화가 필요하면 운영 관리자에게 요청한다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 2.2 아이디·비밀번호 찾기 L103~140 | 복구 창에 서버 호출이 없고 매뉴얼은 관리자 요청을 안내한다. |
| L58 | 로그인 화면의 「아이디 찾기」·「비밀번호 찾기」는 입력을 서버로 보내는 기능이 연결되어 있지 않다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 2.2 아이디·비밀번호 찾기 L103~140 | 복구 창에 서버 호출이 없고 매뉴얼은 관리자 요청을 안내한다. |
| L60 | 찾기 대화상자를 닫으려면 「확인」·「취소」 또는 Esc를 누른다. | nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99 / nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; Escape 처리 L129~145 | 두 단추의 onClose와 Escape 처리를 확인했다. |

### 사용을 끝내고 로그아웃하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L71 | 화면 오른쪽 위 「로그아웃」을 누른다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; handleLogout·로그아웃 단추 L46~120 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그아웃 L2~3 | 로그아웃의 pending·spinner·disabled 조건을 확인했다. |
| L71 | 처리 중에는 로그아웃 단추에 회전 표시가 나타나고 다시 누를 수 없다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; handleLogout·로그아웃 단추 L46~120 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그아웃 L2~3 | 로그아웃의 pending·spinner·disabled 조건을 확인했다. |
| L73 | 서버 요청의 성공 여부와 관계없이 브라우저의 조회 캐시·배포 임시 선택·보안 토큰을 정리하고 로그인 화면으로 이동한다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; handleLogout·로그아웃 단추 L46~120 / nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; logout L176~218 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; logout L152~201 / nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 서버 로그아웃 호출 L22~40 | 서버 오류도 finally에서 브라우저 상태를 정리한다. 화면 이동은 서버 성공 보장이 아니다. |
| L73 | 서버 로그아웃 요청이 실패해도 화면에 오류 문구 없이 로그인 화면으로 이동한다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; handleLogout·로그아웃 단추 L46~120 / nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 서버 로그아웃·프로필 조립 L22~40 | 서버 로그아웃 catch는 콘솔 기록만 하고 finally는 오류 문구 렌더 없이 정리·로그인 이동을 수행한다. |

### 사용자 정보 수정에서 기본 정보 저장하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L100 | 저장소 기본 권한 기준으로 사용자 관리 API 조회에는 USER_READ·USER_INFO_WRITE·USER_WRITE 중 하나, 저장에는 USER_INFO_WRITE·USER_WRITE 중 하나가 필요하다. | nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; getUserDetail L91~105 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; updateUser L185~203 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 경로·메서드 검사 L224~258 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 사용자 API 기본 권한 L267~295 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; USER_INFO_WRITE 초기 데이터 L786~786 | GET/PUT 호출과 PREFIX·메서드 일치 및 anyMatch를 대조했다. 기본 목록 GET은 세 코드 중 하나, 저장은 두 쓰기 코드 중 하나다. 현장 DB 값으로 단정하지 않는다. |
| L102 | 1. 오른쪽 위 사람 모양의 「사용자 프로필 열기」를 눌러 「사용자 정보 수정」을 연다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; 프로필 열기 L111~120 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 프로필 문구 L845~858 | 프로필 단추 콜백과 대화상자 제목을 확인했다. |
| L103 | 2. 「COMPANY」·「USER_NAME」·「EMAIL」을 고친다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; UserUpsertRequest L32~56 | CSS uppercase·readOnly와 DTO NotBlank/Email을 대조했다. 부서 렌더 칸은 없다. |
| L103 | 회사·이름·이메일·부서는 서버 필수값이며 이메일 형식도 검사한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; UserUpsertRequest L32~56 | company·department·userName·email의 NotBlank와 email의 Email 검사를 확인했다. |
| L103 | 「USER_ID」와 부서는 여기서 고칠 수 없다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleSaveProfile·프로필 입력 L129~153 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 취소·Esc L53~119 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 조회한 부서 값 L74~83 | user_id는 readOnly이고 부서 입력은 렌더하지 않는다. 부서는 조회값을 유지해 저장 요청에 보낸다. |
| L104 | 3. 아래쪽 「저장하기」를 누른다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleSaveProfile·프로필 입력 L129~153 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; updateUser L185~203 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; update L142~154 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 프로필 문구 L845~858 | PUT 성공 시 닫고 catch 시 대화상자에 오류를 표시한다. |
| L104 | 저장이 성공하면 대화상자가 닫힌다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleSaveProfile·프로필 입력 L129~153 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; updateUser L185~203 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; update L142~154 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 프로필 문구 L845~858 | PUT 성공 시 닫고 catch 시 대화상자에 오류를 표시한다. |
| L104 | 실패하면 대화상자 안의 오류 문구를 확인한다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleSaveProfile·프로필 입력 L129~153 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; updateUser L185~203 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; update L142~154 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 프로필 문구 L845~858 | PUT 성공 시 닫고 catch 시 대화상자에 오류를 표시한다. |
| L106 | 부서가 비어 저장이 거부되면 운영 관리자에게 「사용자·그룹」의 「부서」를 채워 달라고 요청한다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleSaveProfile·프로필 입력 L129~153 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 취소·Esc L53~119 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; UserUpsertRequest L32~56 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 조회한 부서 값 L74~83 / nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 부서 저장·입력 L118~171 / nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 부서 입력 L203~211 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 부서 문구 L893~893 / nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 사용자 관리 화면 연결 L12~15 / nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 사용자 수정 창 L99~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자·그룹 메뉴 L1103~1103 | 조회 부서 값이 비면 DTO 필수 검사로 실패한다. 사용자·그룹의 사용자 관리 패널은 부서 입력·수정 요청을 제공하므로 운영 관리자에게 보완을 요청하도록 했다. 그 화면의 전체 수정 절차는 복제하지 않았다. |
| L108 | 저장 중이 아닐 때 「취소」나 Esc로 닫을 수 있다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 취소·Esc L53~119 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; getUserDetail L91~105 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; updateUser L185~203 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 프로필 문구 L845~858 | isBusy 중 닫기 차단과 조회·저장 오류 표시를 확인했다. 인계는 사용자 지시의 화면·결과 범위이다. |
| L108 | 권한 거부 시 운영 관리자에게 권한을 요청한다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleSaveProfile·프로필 입력 L129~153 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 취소·Esc L53~119 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; getUserDetail L91~105 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; updateUser L185~203 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 조회한 부서 값 L74~83 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 경로·메서드 검사 L224~258 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 사용자 API 기본 권한 L267~295 | 미등록 경로나 권한 미보유는 차단되므로 반복 입력 대신 권한 요청으로 인계한다. |

### 프로필에서 새 비밀번호 적용하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L125 | 저장소의 기본 권한 데이터 기준으로 USER_INFO_WRITE·USER_WRITE 중 하나가 있어야 프로필에서 비밀번호를 바꿀 수 있다. | nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; resetPassword L231~253 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 경로·메서드 검사 L224~258 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 사용자 API 기본 권한 L267~295 | POST /user/{userPk}/password/reset은 기본 목록의 USER_INFO_WRITE 또는 USER_WRITE가 필요하다. 해당 코드의 GET 적용도 함께 확인했다. |
| L127 | 1. 「사용자 정보 수정」의 「PASSWORD」 오른쪽 「변경하기」를 누른다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 비밀번호 단추 L255~287 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 문구 L847~858 | 편집 상태가 입력 disabled와 단추 문구를 바꾼다. |
| L127 | 입력 칸이 활성화되고 오른쪽 단추가 「저장하기」로 바뀐다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 비밀번호 단추 L255~287 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 문구 L847~858 | 편집 상태가 입력 disabled와 단추 문구를 바꾼다. |
| L128 | 2. 새 비밀번호를 10~128자로 입력한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; validate L22~46 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; REUSE_HISTORY_LIMIT·encodeChangedPassword L21~75 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; resetPassword L143~153 | 서버의 10~128자·공백·3종 검사와 최근 이력 5개 비교를 확인했다. |
| L128 | 공백 없이 소문자·대문자·숫자·특수문자 중 3종 이상을 포함하고 최근 5회 내 사용한 비밀번호는 피한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; validate L22~46 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; REUSE_HISTORY_LIMIT·encodeChangedPassword L21~75 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; resetPassword L143~153 | 서버의 10~128자·공백·3종 검사와 최근 이력 5개 비교를 확인했다. |
| L129 | 3. 「PASSWORD」 오른쪽 「저장하기」를 누른다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleResetPassword L156~182 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 비밀번호 단추 L255~287 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; resetPassword L231~253 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; resetPassword L143~153 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 문구 L847~858 | 전용 비밀번호 요청 성공 시 입력·편집 상태를 지우고 성공 문구를 표시한다. |
| L129 | 성공하면 「비밀번호가 변경되었습니다.」가 나타나고 비밀번호 입력 상태가 닫힌다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleResetPassword L156~182 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 비밀번호 단추 L255~287 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; resetPassword L231~253 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; resetPassword L143~153 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 문구 L847~858 | 전용 비밀번호 요청 성공 시 입력·편집 상태를 지우고 성공 문구를 표시한다. |
| L131 | 화면은 앞뒤 공백을 제거한 뒤 8자 이상인지만 먼저 검사한다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleResetPassword L156~182 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; validate L22~46 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 문구 L847~858 | 클라이언트 trim/min8과 서버 min10의 차이 및 오류 표시를 확인했다. |
| L131 | 8~9자는 서버가 거부한다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleResetPassword L156~182 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; validate L22~46 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 문구 L847~858 | 클라이언트 trim/min8과 서버 min10의 차이 및 오류 표시를 확인했다. |
| L131 | 비밀번호 정책 위반이면 표시된 조건에 맞춰 다시 입력한다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleResetPassword L156~182 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 비밀번호 단추 L255~287 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; validate L22~46 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; REUSE_HISTORY_LIMIT·encodeChangedPassword L21~75 | 재입력은 길이·복잡도·재사용 등 정책 위반에 한정한다. catch 오류의 정책 조건을 확인하도록 안내했다. |
| L133 | 권한 거부이면 운영 관리자에게 비밀번호 변경이나 권한 부여를 요청한다. | nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleResetPassword L156~182 / nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 비밀번호 단추 L255~287 / nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; resetPassword L231~253 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 경로·메서드 검사 L224~258 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 사용자 API 기본 권한 L267~295 | 비밀번호 POST의 권한 거부는 새 비밀번호 재입력으로 해소되지 않으므로 운영 관리자에게 변경 또는 권한 부여를 요청한다. |

### 화면 언어와 밝기 바꾸기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L146 | 상단바의 지구 모양 「언어 전환」을 눌러 「한국어」·「English」·「Tiếng Việt」 중 하나를 고른다. | nori-tc-ui-front/src/i18n/LanguageToggle.tsx @ 9fe69a75dcd3 ; LanguageToggle L33~79 / nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 로그인 화면 언어 단추 L302~305 / nori-tc-ui-front/src/i18n/languages.ts @ 9fe69a75dcd3 ; LANGUAGES L5~9 / nori-tc-ui-front/src/i18n/index.ts @ 9fe69a75dcd3 ; 언어 저장 L17~29 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 언어·테마 문구 L4~9 | 언어 단추·세 값·로그인 하단 위치·브라우저 저장을 확인했다. |
| L146 | 로그인 화면에서는 카드 아래쪽의 언어 단추를 쓴다. | nori-tc-ui-front/src/i18n/LanguageToggle.tsx @ 9fe69a75dcd3 ; LanguageToggle L33~79 / nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 로그인 화면 언어 단추 L302~305 / nori-tc-ui-front/src/i18n/languages.ts @ 9fe69a75dcd3 ; LANGUAGES L5~9 / nori-tc-ui-front/src/i18n/index.ts @ 9fe69a75dcd3 ; 언어 저장 L17~29 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 언어·테마 문구 L4~9 | 언어 단추·세 값·로그인 하단 위치·브라우저 저장을 확인했다. |
| L146 | 선택한 언어는 이 브라우저에 저장된다. | nori-tc-ui-front/src/i18n/LanguageToggle.tsx @ 9fe69a75dcd3 ; LanguageToggle L33~79 / nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 로그인 화면 언어 단추 L302~305 / nori-tc-ui-front/src/i18n/languages.ts @ 9fe69a75dcd3 ; LANGUAGES L5~9 / nori-tc-ui-front/src/i18n/index.ts @ 9fe69a75dcd3 ; 언어 저장 L17~29 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 언어·테마 문구 L4~9 | 언어 단추·세 값·로그인 하단 위치·브라우저 저장을 확인했다. |
| L148 | 상단바의 「다크 모드로 전환」 또는 「라이트 모드로 전환」 단추를 눌러 밝기를 바꾼다. | nori-tc-ui-front/src/shared/components/ThemeToggle.tsx @ 9fe69a75dcd3 ; ThemeToggle L9~25 / nori-tc-ui-front/src/shared/stores/theme.store.ts @ 9fe69a75dcd3 ; toggleTheme L38~52 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 언어·테마 문구 L4~9 | 테마 문구·DOM 클래스 전환·브라우저 저장을 확인했다. |
| L148 | 선택한 밝기도 이 브라우저에 저장된다. | nori-tc-ui-front/src/shared/components/ThemeToggle.tsx @ 9fe69a75dcd3 ; ThemeToggle L9~25 / nori-tc-ui-front/src/shared/stores/theme.store.ts @ 9fe69a75dcd3 ; toggleTheme L38~52 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 언어·테마 문구 L4~9 | 테마 문구·DOM 클래스 전환·브라우저 저장을 확인했다. |

### 상단바에서 조회할 라인 고르기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L165 | 저장소 기본 권한 데이터 기준으로 라인 목록 조회에는 LINE_READ가 필요하다. | nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; getLineList L22~30 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 라인 조회 기본 권한 L150~158 | GET /line과 서버 보호 경로 인가를 대조했다. 기본 목록의 GET /api/line EXACT 행은 LINE_READ이며, 미등록 또는 미보유는 거부된다. 현장 DB 값은 조회하지 않았다. |
| L167 | 상단바의 「라인 스코프 선택」을 누르고 조회할 라인을 고른다. | nori-tc-ui-front/src/shared/components/LineScopeSelector.tsx @ 9fe69a75dcd3 ; LineScopeSelector L19~114 / nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; getLineList L22~30 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; list L38~43 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; eqpItems L241~244 / nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L105~140 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 라인 단추 L11~11 | 라인 API·선택 레이블·설비 및 Live-Map 필터 소비자를 대조했다. |
| L167 | 고른 라인 이름이 단추에 표시된다. | nori-tc-ui-front/src/shared/components/LineScopeSelector.tsx @ 9fe69a75dcd3 ; LineScopeSelector L19~114 / nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; getLineList L22~30 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; list L38~43 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; eqpItems L241~244 / nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L105~140 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 라인 단추 L11~11 | 라인 API·선택 레이블·설비 및 Live-Map 필터 소비자를 대조했다. |
| L167 | 설비 목록과 Live-Map은 선택한 라인 소속 설비로 범위를 좁힌다. | nori-tc-ui-front/src/shared/components/LineScopeSelector.tsx @ 9fe69a75dcd3 ; LineScopeSelector L19~114 / nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; getLineList L22~30 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; list L38~43 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L151~155 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; eqpItems L241~244 / nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L105~140 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 라인 단추 L11~11 | 라인 API·선택 레이블·설비 및 Live-Map 필터 소비자를 대조했다. |
| L169 | 라인 목록을 받은 뒤 선택값이 없거나 목록에 없는 값이면 목록의 첫 라인을 선택한다. | nori-tc-ui-front/src/shared/components/LineScopeSelector.tsx @ 9fe69a75dcd3 ; LineScopeSelector L19~114 | 유효 선택이 없을 때 응답 첫 항목을 고르고 항목이 없으면 빈 레이블이 된다. 인계는 사용자 지시의 증상·시각 범위이다. |
| L169 | 목록 데이터가 없는 상태에서 조회가 거부되면 단추의 라인 이름과 선택 목록이 비어 있고, 권한 오류 문구는 단추에 표시되지 않는다. | nori-tc-ui-front/src/shared/components/LineScopeSelector.tsx @ 9fe69a75dcd3 ; LineScopeSelector L19~114 / nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; getLineList L22~30 / nori-tc-ui-front/src/shared/hooks/useLineList.ts @ 9fe69a75dcd3 ; useLineList L12~17 | data가 없으면 options=[]이고 currentOption이 없어 단추 레이블이 빈 문자열이다. 오류 상태를 단추에 렌더하는 분기가 없으며 빈 listbox가 열린다. 기존 목록 데이터가 남은 재조회 실패까지 같은 결과라고 단정하지 않는다. |
| L171 | 권한이 없어 라인 목록 조회가 거부되면 운영 관리자에게 조회 권한을 요청한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 라인 조회 기본 권한 L150~158 | 조회 권한 미보유에 따른 거부는 새로고침만으로 해결한다고 쓰지 않고, 이번 사용자 지시에 따라 운영 관리자에게 권한 요청으로 인계한다. |
| L171 | 라인 선택에 문제가 계속되면 화면과 발생 시각을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/shared/components/LineScopeSelector.tsx @ 9fe69a75dcd3 ; LineScopeSelector L19~114 | 유효 선택이 없을 때 응답 첫 항목을 고르고 항목이 없으면 빈 레이블이 된다. 인계는 사용자 지시의 증상·시각 범위이다. |

### 좌측 메뉴에서 원하는 업무 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L180 | 좌측 메뉴에서 그룹 이름을 눌러 접거나 편 뒤 항목을 눌러 화면을 연다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 렌더 L281~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 그룹 토글·항목 이동·권한 필터·빈 그룹 제거를 확인했다. |
| L180 | 메뉴 순서는 다음과 같으며 조회 권한이 있는 항목만 보인다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 렌더 L281~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 그룹 토글·항목 이동·권한 필터·빈 그룹 제거를 확인했다. |
| L180 | 보이는 항목이 없는 그룹은 숨겨진다. | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 렌더 L281~312 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 그룹 토글·항목 이동·권한 필터·빈 그룹 제거를 확인했다. |
| L184 | \| 모니터링 \| Live-Map·처리실패 \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 모니터링의 두 항목을 확인했다. |
| L185 | \| 설비 \| 설비 \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 설비의 항목을 확인했다. |
| L186 | \| 모델 \| SECS·Socket·PLC \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 모델의 세 항목 순서를 확인했다. |
| L187 | \| 적용 \| 배포 \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 적용의 배포 항목을 확인했다. |
| L188 | \| 시스템 \| 사용자·그룹 \| | nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103 | 시스템의 사용자·그룹 문구를 확인했다. |

### 메뉴를 좁히고 현재 위치 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L199 | 좌측 로고 오른쪽 「메뉴 접기」를 누르면 그룹 아이콘만 남는다. | nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 접기·그룹 아이콘 L230~277 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 단추 L1087~1089 | 펼침·접힘 문구와 rail 아이콘 pointer/click 처리를 확인했다. |
| L199 | 아이콘에 마우스를 올리거나 아이콘을 누르면 옆으로 하위 메뉴가 열린다. | nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 접기·그룹 아이콘 L230~277 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 단추 L1087~1089 | 펼침·접힘 문구와 rail 아이콘 pointer/click 처리를 확인했다. |
| L199 | 「메뉴 펼치기」를 누르면 넓은 메뉴로 돌아간다. | nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 접기·그룹 아이콘 L230~277 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 단추 L1087~1089 | 펼침·접힘 문구와 rail 아이콘 pointer/click 처리를 확인했다. |
| L201 | 상단바의 Home › 그룹 › 화면 표시로 현재 위치를 확인한다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; 현재 위치·모바일 메뉴 L70~87 / nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveAppNavigationLocation L188~197 / nori-tc-ui-front/src/shared/layout/PageLocation.tsx @ 9fe69a75dcd3 ; PageLocation L20~51 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 단추 L1087~1089 | 위치의 세 segment·없는 경로 생략·모바일 메뉴 단추를 확인했다. |
| L201 | 메뉴에 없는 화면에서는 위치 표시를 생략한다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; 현재 위치·모바일 메뉴 L70~87 / nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveAppNavigationLocation L188~197 / nori-tc-ui-front/src/shared/layout/PageLocation.tsx @ 9fe69a75dcd3 ; PageLocation L20~51 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 단추 L1087~1089 | 위치의 세 segment·없는 경로 생략·모바일 메뉴 단추를 확인했다. |
| L201 | 좁은 화면에서는 상단바의 「메뉴 열기」로 좌측 메뉴를 연다. | nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; 현재 위치·모바일 메뉴 L70~87 / nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveAppNavigationLocation L188~197 / nori-tc-ui-front/src/shared/layout/PageLocation.tsx @ 9fe69a75dcd3 ; PageLocation L20~51 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 단추 L1087~1089 | 위치의 세 segment·없는 경로 생략·모바일 메뉴 단추를 확인했다. |

### 예전 즐겨찾기 주소로 화면 열기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L209 | 예전 주소를 열면 아래 화면으로 이동한다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 / nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 현재 화면 인증·권한 검사 L82~109 | Navigate 목적지는 인증·권한 가드 안이다. |
| L209 | 이동한 화면에도 로그인과 조회 권한 검사가 적용된다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 / nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 현재 화면 인증·권한 검사 L82~109 | Navigate 목적지는 인증·권한 가드 안이다. |
| L213 | \| /dlq/dashboard·/dlq/gateway·/dlq/business·/dlq/quarantine·/dlq/repair-task \| /dlq \| | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 | DLQ 예전 경로 5개를 확인했다. |
| L214 | \| /eqp/business·/eqp/gateway \| /eqp \| | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 | 설비 예전 경로 2개를 확인했다. |
| L215 | \| /eqp/live-map \| /live-map \| | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 | Live-Map 예전 경로를 확인했다. |
| L216 | \| /model \| /model/secs \| | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 | 모델 예전 경로를 확인했다. |
| L217 | \| /deploy/register·/deploy/deploy·/deploy/history \| /deploy \| | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 | 배포 예전 경로 3개를 확인했다. |
| L218 | \| /user/users·/user/groups \| /user \| | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126 | 사용자 예전 경로 2개를 확인했다. |

### 표에서 셀 범위를 골라 복사하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L229 | 1. 공통 표의 셀을 누르고 끌어 사각형 범위를 선택한다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 선택·복사 L226~259 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 셀 선택 표시 L443~473 / nori-tc-ui-front/src/shared/hooks/useGridRangeSelection.ts @ 9fe69a75dcd3 ; normalizeRange·범위 선택 L18~91 | pointer 선택·사각형 정규화·테두리와 배경 스타일을 대조했다. |
| L229 | 선택 범위는 셀의 테두리와 배경으로 확인한다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 선택·복사 L226~259 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 셀 선택 표시 L443~473 / nori-tc-ui-front/src/shared/hooks/useGridRangeSelection.ts @ 9fe69a75dcd3 ; normalizeRange·범위 선택 L18~91 | pointer 선택·사각형 정규화·테두리와 배경 스타일을 대조했다. |
| L230 | 2. Ctrl+C를 누른다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; buildTsv L83~101 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 선택·복사 L226~259 | copy가 accessor 값을 탭·줄바꿈으로 직렬화한다. |
| L230 | 열은 탭, 행은 줄바꿈으로 나누어 복사하므로 스프레드시트에 붙여 넣을 수 있다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; buildTsv L83~101 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 선택·복사 L226~259 | copy가 accessor 값을 탭·줄바꿈으로 직렬화한다. |
| L232 | 셀 안의 입력 상자나 단추를 조작할 때는 셀 범위 선택 대신 입력 요소의 기본 동작이 적용된다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; isNativeInteractiveTarget L69~81 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 선택·복사 L226~259 | 입력·선택·텍스트·단추 요소는 기본 동작을 우선한다. |

### 표마다 다른 편집·붙여넣기·행 메뉴 쓰기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L245 | 모델 상세 표의 편집 가능한 셀은 두 번 눌러 입력 상태로 바꾼다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집·붙여넣기·Esc L262~306 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집 가능 셀 L443~445 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용 판정 L226~232 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 표 L1080~1110 | 더블클릭 편집·anchor 기준 붙여넣기·editable 필터·Escape 상태 종료를 확인했다. |
| L245 | Ctrl+V는 선택을 시작한 셀에서부터 편집 가능한 칸에만 붙인다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집·붙여넣기·Esc L262~306 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집 가능 셀 L443~445 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용 판정 L226~232 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 표 L1080~1110 | 더블클릭 편집·anchor 기준 붙여넣기·editable 필터·Escape 상태 종료를 확인했다. |
| L245 | 읽기 전용인 칸은 바뀌지 않는다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집·붙여넣기·Esc L262~306 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집 가능 셀 L443~445 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용 판정 L226~232 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 표 L1080~1110 | 더블클릭 편집·anchor 기준 붙여넣기·editable 필터·Escape 상태 종료를 확인했다. |
| L245 | Esc는 셀 편집을 끝내며, 편집 중이 아니면 범위 선택을 해제한다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집·붙여넣기·Esc L262~306 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집 가능 셀 L443~445 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용 판정 L226~232 / nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 표 L1080~1110 | 더블클릭 편집·anchor 기준 붙여넣기·editable 필터·Escape 상태 종료를 확인했다. |
| L247 | 설비 파라미터 표는 「Check Out」을 누른 편집 상태에서 셀이 바로 입력 상자로 나타난다. | nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 편집 입력 L46~105 / nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Check Out·파라미터 표 L1326~1392 | Check Out과 isEditMode 전달·즉시 Input 렌더를 대조했다. 저장 세부는 소유 문서 범위이다. |
| L249 | 행 메뉴가 있는 표에서는 행을 마우스 오른쪽 단추로 눌러 실행할 작업을 고른다. | nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 행 메뉴 L240~260 | 설비 행의 ContextMenuTrigger와 실행 항목 렌더를 확인했다. |

### 목록 검색과 쪽 이동으로 자료 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L259 | 검색 칸이 있는 목록에서는 검색어를 입력해 표시 대상을 좁힌다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색어 상태·필터 L92~125 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.3 표 공통 조작 L351~389 | 검색어 상태가 설비 트리 필터에 들어간다. |
| L259 | 예를 들어 설비 좌측 목록은 입력한 검색어로 목록을 걸러낸다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색어 상태·필터 L92~125 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.3 표 공통 조작 L351~389 | 검색어 상태가 설비 트리 필터에 들어간다. |
| L259 | 결과가 없으면 검색어를 줄이거나 지운다. | nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색어 상태·필터 L92~125 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.3 표 공통 조작 L351~389 | 검색어 상태가 설비 트리 필터에 들어간다. |
| L261 | 쪽 이동 도구가 있는 표에서는 아래쪽 쪽 번호·앞뒤 화살표·쪽당 건수 선택을 사용한다. | nori-tc-ui-front/src/shared/layout/PagePagination.tsx @ 9fe69a75dcd3 ; PagePagination L60~158 | 쪽 번호·양쪽 화살표·건수 선택·범위와 총건수를 확인했다. |
| L261 | 현재 표시 범위와 전체 건수도 표 아래에서 확인한다. | nori-tc-ui-front/src/shared/layout/PagePagination.tsx @ 9fe69a75dcd3 ; PagePagination L60~158 | 쪽 번호·양쪽 화살표·건수 선택·범위와 총건수를 확인했다. |
| L263 | 업무 화면의 공통 표는 열 제목을 눌러 정렬하거나 열 경계를 끌어 너비를 바꾸는 기능을 켜 두지 않았다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 정렬·너비 기본값 L171~179 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.3 표 공통 조작 L351~389 | 공통 기본값은 false이다. src 전체 sortable/resizable 검색에서 생산 화면의 true 활성화가 없고 dev fixture에만 있음을 확인했다. |

### 표가 비었거나 자료를 불러오는 중일 때 보기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L274 | 공통 표가 처음 자료를 불러올 때는 열 구성을 유지한 회색 뼈대 행이 나타난다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 L398~420 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 분기 L570~581 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 표 L474~475 | 동일 columns의 skeleton과 로딩 후 빈 표 기본값을 확인했다. |
| L274 | 조회가 끝나고 행이 없으면 빈 목록 안내가 나타난다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 L398~420 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 분기 L570~581 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 표 L474~475 | 동일 columns의 skeleton과 로딩 후 빈 표 기본값을 확인했다. |
| L274 | 기본 문구는 「표시할 데이터가 없습니다.」이며 화면마다 문구가 다를 수 있다. | nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 L398~420 / nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 분기 L570~581 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 표 L474~475 | 동일 columns의 skeleton과 로딩 후 빈 표 기본값을 확인했다. |
| L276 | 처리실패 목록의 「검색 결과가 없습니다.」는 검색어를 조정해 확인한다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·검색 결과 L88~151 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 목록 문구 L131~137 | 처리실패의 검색·오류 문구와 새로고침 콜백을 대조했다. |
| L276 | 조회 실패 때는 서버 오류 문구 또는 「목록을 조회하지 못했습니다.」가 나타난다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·검색 결과 L88~151 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 목록 문구 L131~137 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 표 L474~475 | errorMessage가 있으면 서버 문구를, 없으면 dlq.table.loadError를 그린다. |
| L276 | 오류 문구 아래 「재시도」나 상단 「새로고침」을 눌러 다시 조회한다. | nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·검색 결과 L88~151 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 목록 문구 L131~137 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 표 L474~475 | 오류 아래 재시도와 상단 새로고침이 모두 onRefresh를 호출한다. |

### 설비 명령의 진행과 결과 대화상자 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L289 | 설비 명령을 실행한 뒤에는 대화상자의 진행 문장과 결과를 확인한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationProgress.tsx @ 9fe69a75dcd3 ; EqpOperationProgress L31~50 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; EqpOperationResultDialog L81~131 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 결과 문구 L1110~1118 | 진행 문구·결과 레이블·확인으로 닫기를 대조했다. |
| L289 | 결과에는 「PASS」·「FAIL」·「복구 필요」·「확인 지연」이 표시된다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationProgress.tsx @ 9fe69a75dcd3 ; EqpOperationProgress L31~50 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; EqpOperationResultDialog L81~131 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 결과 문구 L1110~1118 | 진행 문구·결과 레이블·확인으로 닫기를 대조했다. |
| L289 | 「확인」은 결과 대화상자를 닫는다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationProgress.tsx @ 9fe69a75dcd3 ; EqpOperationProgress L31~50 / nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; EqpOperationResultDialog L81~131 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 결과 문구 L1110~1118 | 진행 문구·결과 레이블·확인으로 닫기를 대조했다. |
| L291 | 「확인 지연」이고 「작업 ID」가 있으면 「다시 조회」가 나타난다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; EqpOperationResultDialog L81~131 / nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; recheckLifecycleOperation L360~380 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L18~51 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; getResult L65~107 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 결과 문구 L1110~1118 | delayed와 non-null 작업 ID에만 재조회 단추가 있고 동일 ID를 조회한다. |
| L291 | 「다시 조회」를 눌러 같은 작업의 결과를 다시 읽는다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; EqpOperationResultDialog L81~131 / nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; recheckLifecycleOperation L360~380 / nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L18~51 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; getResult L65~107 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 결과 문구 L1110~1118 | delayed와 non-null 작업 ID에만 재조회 단추가 있고 동일 ID를 조회한다. |
| L293 | 실패나 복구 필요 안내가 계속되면 설비 ID·실행한 명령·발생 시각·결과 문구와 화면에 표시된 「작업 ID」를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; EqpOperationResultBody L20~44 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 결과 문구 L1110~1118 | 복구 안내와 작업 ID 표시를 확인했다. 대상·시각·결과 인계는 사용자 지시이다. |

### 화면 자체를 표시하지 못했을 때 새로고침하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L302 | 「화면을 표시하지 못했습니다.」가 나타나면 안내 아래 「새로고침」을 누른다. | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; render L35~55 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 오류 문구 L477~479 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.4.4 오류와 대처 L435~442 | 오류 경계 문구·reload와 매뉴얼 대처를 대조했다. 인계 항목은 사용자 지시의 화면 조치 범위이다. |
| L302 | 같은 화면에서 다시 발생하면 화면 이름·발생 시각·직전에 한 조작·표시 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; render L35~55 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 오류 문구 L477~479 / nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.4.4 오류와 대처 L435~442 | 오류 경계 문구·reload와 매뉴얼 대처를 대조했다. 인계 항목은 사용자 지시의 화면 조치 범위이다. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼 | 코드와 작성 결과 |
|---|---|---|
| 기본 정보 필수 조건 | U L190~194는 COMPANY·USER_NAME·EMAIL을 선택으로 적는다. | backend UserUpsertRequest.java @ 985d17c604dd L32~50은 세 값과 department를 NotBlank, email을 Email로 검사한다. 보완본은 부서까지 서버 필수임과 부서가 비어 저장이 거부될 때 사용자·그룹 화면의 부서 보완을 운영 관리자에게 요청하도록 명시했다. front UserProfileModal.tsx L140~146은 조회한 부서도 전송하지만 편집 칸은 없다. |
| 비밀번호 저장 단추 | U L179~180은 변경하기를 누른 뒤 같은 단추를 다시 누른다고 적는다. | front UserProfileModal.tsx @ 9fe69a75dcd3 L269~286에서 문구가 저장하기로 바뀐다. 본문은 PASSWORD 오른쪽 저장하기로 적었다. |
| 비밀번호 재사용 범위 | U L216은 직전에 쓴 값으로 설명한다. | backend UiPasswordPolicyService.java @ 985d17c604dd L21~22, L63~75는 최근 이력 5개를 검사한다. 본문은 최근 5회로 썼다. |
| 라인 선택 전제 | U L236은 항상 라인 하나가 선택되어 있다고 적는다. | front LineScopeSelector.tsx @ 9fe69a75dcd3 L42~49, L62~78은 목록이 없으면 선택을 만들지 않고 빈 레이블을 그린다. 본문은 목록 수신 이후의 첫 항목 선택과 빈 목록을 구분했다. |
| 로그아웃 중 도구 | U L282는 모든 도구가 잠시 눌리지 않는다고 적는다. | front AppTopBar.tsx @ 9fe69a75dcd3 L91~117은 logout·profile에 pending disabled를 주며 line/theme/language에는 전달하지 않는다. 본문은 로그아웃 단추 잠금만 썼다. |
| 셀 편집의 Esc | U L357은 편집 취소로 적는다. | front SelectableTable.tsx @ 9fe69a75dcd3 L295~306은 editing 상태를 지우며 값 복구는 없다. modelDetailColumnDefs.tsx @ 9fe69a75dcd3 L171~176은 onChange마다 값을 반영한다. 본문은 편집 종료로 표현했다. |
| 로그아웃 실패 표시 | U L166~167은 로그아웃 오류 문구를 화면 메시지로 제시한다. | front AppShellPage.tsx @ 9fe69a75dcd3 L28~34는 서버 실패를 console.warn으로 기록한 뒤 정리를 계속하며 화면 오류로 렌더하지 않는다. 보완본은 서버 실패도 화면 오류 문구 없이 로그인으로 이동함을 명시했다. |
| 프로필 권한 조건 | U §2.4 L169~218에는 사용자 관리 API의 기본 권한 조건이 없다. | UiSecurityConfig.java @ 985d17c604dd L142~143과 UiApiPermissionCache.java L142~174, L224~258은 매핑·보유 권한으로 인가한다. manual-permission-catalog.json @ f1d5e0a8e727 L267~295는 GET 세 코드와 변경 두 코드를 연결한다. 본문은 저장소 기본 데이터 기준과 권한 거부 시 인계를 추가했다. |
| 처리실패 목록 조회 실패 | U L389는 기본 오류 문구와 새로고침만 안내한다. | DlqTableShell.tsx @ 9fe69a75dcd3 L101~110, L127~132는 서버 문구 또는 기본 문구, 오류 아래 재시도와 상단 새로고침을 제공한다. 본문에 반영했다. |

| 라인 조회 권한 | U §2.5 L220~249에는 LINE_READ 조건과 권한 거부 때의 단추 표시가 없다. | manual-permission-catalog.json @ f1d5e0a8e727 L150~158과 UiApiPermissionCache.java @ 985d17c604dd L142~174를 확인했다. LineScopeSelector.tsx @ 9fe69a75dcd3 L19~114의 빈 데이터 렌더 범위를 본문에 추가하고 권한 요청으로 인계했다. |

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 접속 주소·배포 판본·로그인 만료 시간 | 소스가 현장값을 입증하지 않는다. 운영자가 안내한 주소로만 표현하고 만료 시간 숫자는 쓰지 않았다. | TC 운영 담당 |
| 재로그인 후 미저장 작업을 그대로 이어갈 수 있다는 보장 | U L158~160의 작업 이어가기를 모든 화면의 편집 상태 복원으로 입증하지 못했다. 다시 로그인한다는 절차만 남겼다. | TC UI 담당 |
| 최초 선택 라인이 라인 코드 사전순 첫 값이라는 단정 | front는 응답 첫 항목을 고른다. 읽은 LineQueryService·JpaLineQueryPort는 정렬을 하위 저장소에 위임하고 직접 정렬하지 않는다. 하위 저장소 정렬은 이 조사에서 확인하지 않았다. 목록 첫 라인까지만 썼다. | TC DB·UI 백엔드 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 objectKey | 복제하지 않은 범위 |
|---|---|
| 00.rag/user/permission-code-reference.md | 화면·작업별 전체 권한 코드표와 코드별 뜻. 본문은 메뉴 및 프로필 조회·저장·비밀번호 변경에 필요한 최소 권한 조건만 둔다. |
| 00.rag/user/login-permission-and-screen-troubleshooting.md | 로그인·권한 실패 상세 진단 순서. |
| 00.rag/user/error-message-and-code-reference.md | 보안 오류·서버 오류의 전체 문구별 의미와 대처표. |
| 00.rag/user/screen-behavior-limitations.md | 찾기 대화상자의 입력 칸과 미연결 기능 상세. 본문은 관리자 요청에 필요한 최소 경계만 유지한다. |
| 00.rag/user/state-reference.md | U §3.4의 전체 진행 단계·결과값 정의. 본문은 결과 확인·재조회에 필요한 표시만 유지한다. |
| 00.rag/user/user-group-and-permission-management.md | 계정 등록·타인 비밀번호 초기화·그룹 관리의 전체 절차. 본문은 프로필의 빈 부서·권한 거부를 관리자에게 넘기는 최소 안내만 둔다. |
| 00.rag/user/model-details-and-flow-editing.md | 모델 잠금·필드별 편집·저장 절차. |
| 00.rag/user/equipment-model-parameter-and-jar-update.md | 설비 파라미터 저장·버전·배포 절차. |
| 00.rag/user/equipment-connection-and-command-troubleshooting.md | 명령 실패·중복 요청·복구 필요의 상세 진단. |
| 00.rag/user/live-map-status.md·00.rag/user/live-map-layout-editing.md | 전용 보기·편집 기능과 버튼별 권한 조건. |

## 검사 결과

- 도구: Git 2.52.0.windows.1, GNU Bash 5.2.37(1)-release, OpenJDK Temurin 21.0.9+10-LTS. `java -version`의 표준 오류 출력이 PowerShell에서 NativeCommandError 모양으로 표시되었으나 실제 버전과 검사기 정상 실행을 확인했다.
- 최초 검사 exit 1: 원천 줄 범위 4건. RequirePermission L1~80/실제 41줄, PageLocation L1~100/실제 51줄, DlqTableShell L1~180/실제 157줄, EqpOperationResultBody L21~47/실제 44줄. 각각 원문을 다시 읽고 해당 실제 범위로 수정했다. 길이·마스킹 위반은 없었다.
- 직전 작성본 자체 기계 검사(메인 대조 전)는 exit 0, sections=18, children=54, maxChild=133, maxParent=415였다. 이후 메인 세션은 의미·조건 누락 6건을 확인했다. 기계 검사 통과를 사실 완전성 입증으로 확대했던 자체검사의 한계를 보완했다.
- 이번 보완 중 프로필 부모 길이 469자와 453자로 각 exit 1이었다. 기존 절을 유지하고 관련 문장만 줄인 최종본은 exit 0, sections=18, children=58, maxChild=133, maxParent=449, violations=0이다. MASK와 CROSS VIOLATION 출력은 없다. 직전 보완 당시 corpus는 1/53이었다. 이번 추가 보완은 corpus 2/53의 대상 1개 검사와 문서 간 중복 검사로 구분한다.
- 기록 생성 첫 시도는 PowerShell stdin 인코딩 때문에 한글 원천 접두어 비교가 일치하지 않아 IndexError로 파일 쓰기 전에 종료됐다. OutputEncoding UTF-8을 명시하고 한글 Unicode 값 전달을 확인한 뒤 기록만 작성했다.
- 검사기 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/login-and-screen-basics.md
```

해당 프로세스의 Git 환경만 세 원천 저장소 safe.directory로 지정했다. 검사기·클래스·소스를 수정하지 않았다. 최종 검사 마지막 출력 10줄:

```text
    L276-276 문단 124  | 처리실패 목록의 「검색 결과가 없습니다.」는 검색어를 조정해 확인한다. 조회 실패 때는 서버 오류 문구 또는 「목록을 조회하지 못했습니다.」가 나타난다. 오류 문구 아래 「재시도」나 상단 「새로고침」을 눌러 다시 조회한다.
§ L278-293 [23] 설비 명령의 진행과 결과 대화상자 확인하기   parent=245 children=3 sources=7
    L289-289 문단 99  | 설비 명령을 실행한 뒤에는 대화상자의 진행 문장과 결과를 확인한다. 결과에는 「PASS」·「FAIL」·「복구 필요」·「확인 지연」이 표시된다. 「확인」은 결과 대화상자를 닫는다.
    L291-291 문단 68  | 「확인 지연」이고 「작업 ID」가 있으면 「다시 조회」가 나타난다. 「다시 조회」를 눌러 같은 작업의 결과를 다시 읽는다.
    L293-293 문단 76  | 실패나 복구 필요 안내가 계속되면 설비 ID·실행한 명령·발생 시각·결과 문구와 화면에 표시된 「작업 ID」를 운영 담당자에게 전달한다.
§ L295-302 [24] 화면 자체를 표시하지 못했을 때 새로고침하기   parent=101 children=1 sources=3
    L302-302 문단 101  | 「화면을 표시하지 못했습니다.」가 나타나면 안내 아래 「새로고침」을 누른다. 같은 화면에서 다시 발생하면 화면 이름·발생 시각·직전에 한 조작·표시 문구를 운영 담당자에게 전달한다.
SUMMARY sections=18 children=60 maxChild=133 maxParent=449
CORPUS docs=2/53 sections=27 part={user=2} type={사용 절차=2}
TOTAL files=2 targets=1 violations=0
```

최초 사람 자체검사는 다음 18절을 읽었으나 메인 세션에서 6건이 추가 확인되었다. 아래 표의 수정 5절은 이번에 원천·문장·독립성·중복을 다시 읽고 판정했다. 나머지 13절은 메인이 코드 일치를 확인한 문장과 기존 대조표를 보존하고 좌표만 갱신했다.

| 절 제목 | 제목만으로 질문 식별 | 원천과 일치 | 독립 답 | 다른 절·앞선 문서 중복 |
|---|---|---|---|---|
| 로그인 화면에서 업무 화면으로 들어가기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 같은 브라우저에 아이디 기억시키기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 로그인 입력 오류와 인증이 풀린 화면 확인하기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 아이디나 비밀번호를 잊었을 때 요청하기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 사용을 끝내고 로그아웃하기 | 적합 | 이번 보완 원천 재대조 | 적합 | 다른 절과 새 사실 중복 없음; 앞선 검색 문서 없음 |
| 사용자 정보 수정에서 기본 정보 저장하기 | 적합 | 이번 보완 원천 재대조 | 적합 | 다른 절과 새 사실 중복 없음; 앞선 검색 문서 없음 |
| 프로필에서 새 비밀번호 적용하기 | 적합 | 이번 보완 원천 재대조 | 적합 | 다른 절과 새 사실 중복 없음; 앞선 검색 문서 없음 |
| 화면 언어와 밝기 바꾸기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 상단바에서 조회할 라인 고르기 | 적합 | LINE_READ·빈 목록 분기 재대조 | 적합 | Live-Map 문서는 최소 선행 권한만 제시하며 라인 조작·실패 표시는 이 절에서 소유 |
| 좌측 메뉴에서 원하는 업무 찾기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 메뉴를 좁히고 현재 위치 확인하기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 예전 즐겨찾기 주소로 화면 열기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 표에서 셀 범위를 골라 복사하기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 표마다 다른 편집·붙여넣기·행 메뉴 쓰기 | 적합 | 이번 보완 원천 재대조 | 적합 | 다른 절과 새 사실 중복 없음; 앞선 검색 문서 없음 |
| 목록 검색과 쪽 이동으로 자료 찾기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 표가 비었거나 자료를 불러오는 중일 때 보기 | 적합 | 이번 보완 원천 재대조 | 적합 | 다른 절과 새 사실 중복 없음; 앞선 검색 문서 없음 |
| 설비 명령의 진행과 결과 대화상자 확인하기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |
| 화면 자체를 표시하지 못했을 때 새로고침하기 | 적합 | 대조 완료 | 적합 | 소유 사실 중복 없음; 앞선 검색 문서 없음 |

제목의 정확 중복과 불필요한 반복 틀, 본문 복제, 빈 틀 문장, 이름 토막 뜻풀이, 잘린 문장, 오답 사례, 필요한 값의 마스킹, admin 경로·내부 조치 지시의 미해소 지적은 없다. 본문 모든 문장·표 행의 원천 대응을 위 표에 유지했다. 이번 수정 사실은 새로 읽고 대조했으며 영향 없는 사실은 기존 대조와 메인 확인을 보존했다. UI 명칭은 CSS uppercase를 포함한 렌더와 ko.json 기준이다.

기계·사람 검사는 작성 자체검사다. 제품 build·시험 실행·실제 브라우저·서버/API 호출·현장 적용·검색 반영·정답률 실측은 하지 않았다. 메인 세션의 독립 대조 결과와 구분한다.

## 임시 자원과 남은 범위

검사용 임시 파일·브라우저·서비스·컨테이너를 만들지 않았다. 기존 작업 폴더의 work 디렉터리는 최종 조회에서 비어 있었으며 이 세션이 만든 자원이 아니므로 삭제하지 않았다. 보존 산출물은 지정한 두 Markdown 파일이다. 업로드와 다음 문서 작성은 하지 않았다.

독립 Agent 호출 0회. 개별 도구 명령에 노출된 실행 시간 이외의 전체 wall time·provider 토큰·사용량·비용은 미확인이다. 남은 순서는 메인 세션의 원천 대조다. 이 세션에서 후속 문서·업로드를 자동 시작하지 않는다.

## 메인 검증 6건 보완 기록

- 보완 전 검색 원문: SHA-256 `018c2075333ecc720d0c8ba975497bf60f9abac14b1bfbf025bf29c16e72f4f0`, 270줄. 보완 전 작성 기록 SHA-256 `885ebe77246a0016ab84810651e828a8ee3f2aa2817eed6ae5b1d6194517ca00`.
- 기존 원문 18절 중 지적이 있는 5절만 수정했고 나머지 13절은 메타·본문을 보존했다. 기존 문장 전체를 다시 생성하지 않았다. 작성 기록은 해당 사실 행, 줄 좌표, 영향받은 소유 범위·매뉴얼 차이·검사 결과를 동기화했다.
- 이번 추가 원천도 front 9fe69a75dcd3, backend 985d17c604dd, exec f1d5e0a8e727이다. front `src`, backend `ui`, exec `docs/scripts/manual-permission-catalog.json`·`docs/db_table/sample_data`의 Git 상태 행이 없고 exit 0임을 확인했다.
- 기본 권한 JSON은 USER_READ·USER_INFO_WRITE·USER_WRITE 세 API 매핑을 담고, 읽은 SQL 시드 L786은 USER_INFO_WRITE의 PREFIX /api/user·전체 메서드 매핑을 담는다. 모든 시드 파일에 세 매핑이 각각 있다고 주장하지 않았다. 현장 DB를 조회하거나 실제 부여 현황을 단정하지 않았다.

| 결함 | 누락·오류 원인 | 최소 보완 | 관련 확인 |
|---|---|---|---|
| 1 | 기록에서 확인한 department 필수 검사를 화면 저장 실패 결과와 연결하지 않았다. | 부서 필수·프로필 수정 불가·비어 저장 실패 시 사용자·그룹의 부서 보완 요청을 추가했다. | UserUpsertRequest·UserProfileModal·UserUpsertModal의 조회값/렌더/요청, 실제 부서 i18n과 부모 연결을 재확인했다. |
| 2 | 프로필 API의 인가 소비자와 기본 권한 데이터 대조를 본문 조건에서 빠뜨렸다. | 저장소 기본 권한에 따른 조회·저장·비밀번호 변경 조건과 권한 거부 인계를 추가하고 정책 위반 재입력과 나눴다. | user.api·UiSecurityConfig·UiApiPermissionCache·기본 권한 JSON·SQL 시드를 재확인했다. |
| 3 | 로그아웃 성공 판단의 추상적 주의만 적고 실제 오류 미표시를 전달하지 않았다. | 서버 실패에도 화면 오류 없이 로그인으로 이동한다고 명시했다. | AppShellPage catch와 AppTopBar finally를 재확인했다. |
| 4 | 구체 사실 없이 업무 화면 확인을 반복했다. | 해당 한 문장만 삭제했다. | 남은 Check Out·즉시 입력 상자 사실을 EqpPage·EqpParamTable에서 재확인했다. |
| 5 | 행 메뉴 존재만 반복하는 예시였다. | 해당 예시 한 문장만 삭제했다. | 남은 행 우클릭 조작은 EqpInfoTable의 ContextMenuTrigger와 대조했다. 명령 절차는 추가하지 않았다. |
| 6 | 기본 오류와 상단 단추만 보고 오류 영역의 서버 문구·재시도 분기를 누락했다. | 서버/기본 문구와 오류 아래 재시도·상단 새로고침을 명시했다. | DlqTableShell L101~110·L127~132, ko.json L131~137을 재확인했다. |

새 문장은 정책·권한·부서 실패 조건 또는 정확한 화면 표시·단추 조작에 한정했다. 권한 코드는 세부 뜻풀이를 추가하지 않았으며 각 절의 독립 답에 필요한 최소 조건만 담았다. 재조회·부서 보완의 전체 업무 절차는 복제하지 않았다. 임시 파일은 만들지 않았다.

이번 보완 최종 자체검사: 문장·표 행 106건과 본문 블록 58개의 대응·줄 좌표·검색 원문 SHA-256 일치를 확인했다. 검사기 exit 0, 위반·MASK·CROSS VIOLATION 각 0건이며 절 18개·자식 58개·자식 최대 133자·부모 최대 449자이다. 검색 원문 294줄, SHA-256 `83816b9bf4f5b9f0098253fa245e6b9157edcb635162fad1330cedc326090f86`이다. 메인 세션의 후속 독립 판정은 아직 수행하지 않았다.

## 라인 목록 조회 권한 추가 보완 기록

- 보완 전 원문 SHA-256 `83816b9bf4f5b9f0098253fa245e6b9157edcb635162fad1330cedc326090f86`, 294줄과 작성 기록 SHA-256 `823f8c82b8b6f3af30e570ce95e7d67b1af1c58bc339af2980a0a3bfb2b49471`에서 이어갔다.
- 원인: 라인 API와 화면 필터를 확인했지만 그 요청의 기본 권한 연결을 라인 선택 절의 선행 조건과 실패 조치로 연결하지 않았다.
- 추가로 다시 읽은 원천: front의 line.api.ts L22~30, useLineList.ts L12~17, LineScopeSelector.tsx L19~114, line-scope.store.ts L16~19, api-client.ts L44~51; backend의 UiSecurityConfig.java L142~143, UiApiPermissionCache.java L142~174·L224~258; exec의 manual-permission-catalog.json L150~158, U §2.5 L220~249.
- 판본은 front 9fe69a75dcd3, backend 985d17c604dd, exec f1d5e0a8e727을 다시 확인했다. front src/shared·backend security·exec 기본 권한 JSON 경로의 Git 상태 행이 없고 exit 0이었다.
- 조치: 저장소 기본 권한 데이터의 LINE_READ 조건, 목록 데이터가 없는 상태에서 거부될 때의 빈 단추 레이블·선택 목록과 단추 내 오류 미표시, 운영 관리자 권한 요청을 추가했다. 이전 조회 데이터가 남은 실패까지 같은 표시라고 쓰지 않았다. 설비·Live-Map의 실패 표시 범위로 확대하지 않았다.
- 범위: 검색 원문의 라인 선택 절 한 곳만 바꾸고 다른 17절은 보존했다. 기존 대조표는 유지하면서 새 문장 3건과 전체 줄 좌표·최종 파일 정보를 동기화했다. 본문 18절, 문장·표 행 대조 108건, 본문 블록 60개이다.
- 사람 검사: 수정한 절의 제목·독립 답·원천·마스킹·중복을 다시 확인했다. 기존 live-map-status.md를 읽었으며 해당 문서는 Live-Map 시작의 최소 선행 권한만 함께 제시한다. 라인 선택 조작·권한 거부 단추 표시·권한 요청은 이 문서에서 설명한다. 다른 문서는 수정하지 않았다.
- 최종 검사: exit 0, 위반·MASK·CROSS VIOLATION 0건. 해당 절 부모 349자, 파일 자식 최대 133자·부모 최대 449자, 18절·자식 60개이다. corpus는 2/53·27절이며 targets=1이다. 다른 문서 전체의 사실 검증 완료를 뜻하지 않는다.
- 최종 검색 원문: 302줄, SHA-256 `d2797a9c03f40c92310691305fd266d1cfd5c8dbadc887ee430046ccb07d8beb`. 직전 6건 보완 기록의 수량·SHA는 해당 회차 이력으로 보존한다.
- 검사용 임시 파일이나 서비스는 만들지 않았으며 제품 실행·현장 권한 조회·업로드는 하지 않았다. 이번 자체검사는 메인 세션의 후속 독립 검증과 구분한다.

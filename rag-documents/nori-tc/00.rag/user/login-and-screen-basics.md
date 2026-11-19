# 로그인·메뉴·화면 공통 조작
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
```

## 로그인 화면에서 업무 화면으로 들어가기
```rag-meta
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L182~290
원천: nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema·useLogin L22~78
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; login L129~169
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveLoginLandingRoute L146~170
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L96~139
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 문구 L12~22
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 2.1.1 절차 L68~73
```

1. 운영자가 안내한 TC 주소를 열고 「아이디」와 「비밀번호」를 입력한다. 아이디는 앞뒤 공백을 뺀 뒤 2자 이상, 비밀번호는 8자 이상이어야 로그인 요청을 보낸다.
2. 비밀번호 오른쪽 눈 모양 단추로 「비밀번호 표시」와 「비밀번호 숨김」을 전환한다.
3. 「로그인」을 누른다. 성공하면 좌측 메뉴 순서에서 계정이 접근할 수 있는 첫 화면으로 이동한다.

## 같은 브라우저에 아이디 기억시키기
```rag-meta
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; SAVED_USER_ID_KEY·handleSubmit L102~161
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 아이디 저장 L19~19
```

로그인 화면에서 「아이디 저장」을 켜고 「로그인」을 누르면 입력한 아이디의 앞뒤 공백을 제거해 이 브라우저에 저장한다. 로그인 성공 전에도 저장되며, 다음에 로그인 화면을 열면 아이디가 채워진다.

저장한 아이디를 지우려면 「아이디 저장」을 끄고 「로그인」을 누른다.

## 로그인 입력 오류와 인증이 풀린 화면 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema L22~33
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; LoginPage L200~298
원천: nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 ; RequireAuth L19~35
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveLoginLandingRoute L161~170
원천: nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 ; RequirePermission L19~41
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 입력 검사 L833~836
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 안내 L1104~1106
```

아이디나 비밀번호가 비었거나 최소 길이에 못 미치면 입력 칸 아래 안내를 보고 고친다. 서버가 로그인을 거부하면 로그인 화면에 오류 문구가 표시된다.

로그인하지 않았거나 현재 사용자 인증을 확인하지 못하면 보호된 업무 화면 대신 로그인 화면으로 이동한다. 다시 로그인한다.

로그인 후 「접근 권한이 없습니다」가 보이면 운영 관리자에게 사용할 화면의 접근 권한을 요청한다.

## 아이디나 비밀번호를 잊었을 때 요청하기
```rag-meta
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; Escape 처리 L129~145
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 2.2 아이디·비밀번호 찾기 L103~140
```

아이디를 잊었거나 비밀번호 초기화가 필요하면 운영 관리자에게 요청한다. 로그인 화면의 「아이디 찾기」·「비밀번호 찾기」는 입력을 서버로 보내는 기능이 연결되어 있지 않다.

찾기 대화상자를 닫으려면 「확인」·「취소」 또는 Esc를 누른다.

## 사용을 끝내고 로그아웃하기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; handleLogout·로그아웃 단추 L46~120
원천: nori-tc-ui-front/src/shared/components/AppShellPage.tsx @ 9fe69a75dcd3 ; 서버 로그아웃·프로필 조립 L22~40
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; logout L176~218
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; logout L152~201
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그아웃 L2~3
```

화면 오른쪽 위 「로그아웃」을 누른다. 처리 중에는 로그아웃 단추에 회전 표시가 나타나고 다시 누를 수 없다.

서버 요청의 성공 여부와 관계없이 브라우저의 조회 캐시·배포 임시 선택·보안 토큰을 정리하고 로그인 화면으로 이동한다. 서버 로그아웃 요청이 실패해도 화면에 오류 문구 없이 로그인 화면으로 이동한다.

## 사용자 정보 수정에서 기본 정보 저장하기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; 프로필 열기 L111~120
원천: nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleSaveProfile·프로필 입력 L129~153
원천: nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 취소·Esc L53~119
원천: nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 대문자 렌더·저장 단추 L216~334
원천: nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; getUserDetail L91~105
원천: nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; updateUser L185~203
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; UserUpsertRequest L32~56
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; update L142~154
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 프로필 문구 L845~858
원천: nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 조회한 부서 값 L74~83
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 부서 저장·입력 L118~171
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 부서 입력 L203~211
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 부서 문구 L893~893
원천: nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 사용자 관리 화면 연결 L12~15
원천: nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 사용자 수정 창 L99~105
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자·그룹 메뉴 L1103~1103
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 경로·메서드 검사 L224~258
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 사용자 API 기본 권한 L267~295
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; USER_INFO_WRITE 초기 데이터 L786~786
```

저장소 기본 권한 기준으로 사용자 관리 API 조회에는 USER_READ·USER_INFO_WRITE·USER_WRITE 중 하나, 저장에는 USER_INFO_WRITE·USER_WRITE 중 하나가 필요하다.

1. 오른쪽 위 사람 모양의 「사용자 프로필 열기」를 눌러 「사용자 정보 수정」을 연다.
2. 「COMPANY」·「USER_NAME」·「EMAIL」을 고친다. 회사·이름·이메일·부서는 서버 필수값이며 이메일 형식도 검사한다. 「USER_ID」와 부서는 여기서 고칠 수 없다.
3. 아래쪽 「저장하기」를 누른다. 저장이 성공하면 대화상자가 닫힌다. 실패하면 대화상자 안의 오류 문구를 확인한다.

부서가 비어 저장이 거부되면 운영 관리자에게 「사용자·그룹」의 「부서」를 채워 달라고 요청한다.

저장 중이 아닐 때 「취소」나 Esc로 닫을 수 있다. 권한 거부 시 운영 관리자에게 권한을 요청한다.

## 프로필에서 새 비밀번호 적용하기
```rag-meta
원천: nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; handleResetPassword L156~182
원천: nori-tc-ui-front/src/features/profile/components/UserProfileModal.tsx @ 9fe69a75dcd3 ; 비밀번호 단추 L255~287
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; validate L22~46
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; REUSE_HISTORY_LIMIT·encodeChangedPassword L21~75
원천: nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; resetPassword L231~253
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; resetPassword L143~153
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 문구 L847~858
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 경로·메서드 검사 L224~258
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 사용자 API 기본 권한 L267~295
```

저장소의 기본 권한 데이터 기준으로 USER_INFO_WRITE·USER_WRITE 중 하나가 있어야 프로필에서 비밀번호를 바꿀 수 있다.

1. 「사용자 정보 수정」의 「PASSWORD」 오른쪽 「변경하기」를 누른다. 입력 칸이 활성화되고 오른쪽 단추가 「저장하기」로 바뀐다.
2. 새 비밀번호를 10~128자로 입력한다. 공백 없이 소문자·대문자·숫자·특수문자 중 3종 이상을 포함하고 최근 5회 내 사용한 비밀번호는 피한다.
3. 「PASSWORD」 오른쪽 「저장하기」를 누른다. 성공하면 「비밀번호가 변경되었습니다.」가 나타나고 비밀번호 입력 상태가 닫힌다.

화면은 앞뒤 공백을 제거한 뒤 8자 이상인지만 먼저 검사한다. 8~9자는 서버가 거부한다. 비밀번호 정책 위반이면 표시된 조건에 맞춰 다시 입력한다.

권한 거부이면 운영 관리자에게 비밀번호 변경이나 권한 부여를 요청한다.

## 화면 언어와 밝기 바꾸기
```rag-meta
원천: nori-tc-ui-front/src/i18n/LanguageToggle.tsx @ 9fe69a75dcd3 ; LanguageToggle L33~79
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 로그인 화면 언어 단추 L302~305
원천: nori-tc-ui-front/src/i18n/languages.ts @ 9fe69a75dcd3 ; LANGUAGES L5~9
원천: nori-tc-ui-front/src/i18n/index.ts @ 9fe69a75dcd3 ; 언어 저장 L17~29
원천: nori-tc-ui-front/src/shared/components/ThemeToggle.tsx @ 9fe69a75dcd3 ; ThemeToggle L9~25
원천: nori-tc-ui-front/src/shared/stores/theme.store.ts @ 9fe69a75dcd3 ; toggleTheme L38~52
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 언어·테마 문구 L4~9
```

상단바의 지구 모양 「언어 전환」을 눌러 「한국어」·「English」·「Tiếng Việt」 중 하나를 고른다. 로그인 화면에서는 카드 아래쪽의 언어 단추를 쓴다. 선택한 언어는 이 브라우저에 저장된다.

상단바의 「다크 모드로 전환」 또는 「라이트 모드로 전환」 단추를 눌러 밝기를 바꾼다. 선택한 밝기도 이 브라우저에 저장된다.

## 상단바에서 조회할 라인 고르기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/LineScopeSelector.tsx @ 9fe69a75dcd3 ; LineScopeSelector L19~114
원천: nori-tc-ui-front/src/shared/api/line.api.ts @ 9fe69a75dcd3 ; getLineList L22~30
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/LineController.java @ 985d17c604dd ; list L38~43
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L151~155
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; eqpItems L241~244
원천: nori-tc-ui-front/src/features/live-map/components/LiveMapPage.tsx @ 9fe69a75dcd3 ; 라인별 설비 필터 L105~140
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 라인 단추 L11~11
원천: nori-tc-ui-front/src/shared/hooks/useLineList.ts @ 9fe69a75dcd3 ; useLineList L12~17
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 라인 조회 기본 권한 L150~158
```

저장소 기본 권한 데이터 기준으로 라인 목록 조회에는 LINE_READ가 필요하다.

상단바의 「라인 스코프 선택」을 누르고 조회할 라인을 고른다. 고른 라인 이름이 단추에 표시된다. 설비 목록과 Live-Map은 선택한 라인 소속 설비로 범위를 좁힌다.

라인 목록을 받은 뒤 선택값이 없거나 목록에 없는 값이면 목록의 첫 라인을 선택한다. 목록 데이터가 없는 상태에서 조회가 거부되면 단추의 라인 이름과 선택 목록이 비어 있고, 권한 오류 문구는 단추에 표시되지 않는다.

권한이 없어 라인 목록 조회가 거부되면 운영 관리자에게 조회 권한을 요청한다. 라인 선택에 문제가 계속되면 화면과 발생 시각을 운영 담당자에게 전달한다.

## 좌측 메뉴에서 원하는 업무 찾기
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; APP_NAVIGATION_GROUPS·resolveVisibleAppNavigationGroups L59~152
원천: nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 렌더 L281~312
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 표시 문구 L1091~1103
```

좌측 메뉴에서 그룹 이름을 눌러 접거나 편 뒤 항목을 눌러 화면을 연다. 메뉴 순서는 다음과 같으며 조회 권한이 있는 항목만 보인다. 보이는 항목이 없는 그룹은 숨겨진다.

| 그룹 | 열 수 있는 화면 |
|---|---|
| 모니터링 | Live-Map·처리실패 |
| 설비 | 설비 |
| 모델 | SECS·Socket·PLC |
| 적용 | 배포 |
| 시스템 | 사용자·그룹 |

## 메뉴를 좁히고 현재 위치 확인하기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/AppLnbSidebar.tsx @ 9fe69a75dcd3 ; 메뉴 접기·그룹 아이콘 L230~277
원천: nori-tc-ui-front/src/shared/components/AppTopBar.tsx @ 9fe69a75dcd3 ; 현재 위치·모바일 메뉴 L70~87
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; resolveAppNavigationLocation L188~197
원천: nori-tc-ui-front/src/shared/layout/PageLocation.tsx @ 9fe69a75dcd3 ; PageLocation L20~51
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 메뉴 단추 L1087~1089
```

좌측 로고 오른쪽 「메뉴 접기」를 누르면 그룹 아이콘만 남는다. 아이콘에 마우스를 올리거나 아이콘을 누르면 옆으로 하위 메뉴가 열린다. 「메뉴 펼치기」를 누르면 넓은 메뉴로 돌아간다.

상단바의 Home › 그룹 › 화면 표시로 현재 위치를 확인한다. 메뉴에 없는 화면에서는 위치 표시를 생략한다. 좁은 화면에서는 상단바의 「메뉴 열기」로 좌측 메뉴를 연다.

## 예전 즐겨찾기 주소로 화면 열기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 예전 경로 이동 L112~126
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 현재 화면 인증·권한 검사 L82~109
```

예전 주소를 열면 아래 화면으로 이동한다. 이동한 화면에도 로그인과 조회 권한 검사가 적용된다.

| 예전 화면 주소 | 현재 화면 주소 |
|---|---|
| /dlq/dashboard·/dlq/gateway·/dlq/business·/dlq/quarantine·/dlq/repair-task | /dlq |
| /eqp/business·/eqp/gateway | /eqp |
| /eqp/live-map | /live-map |
| /model | /model/secs |
| /deploy/register·/deploy/deploy·/deploy/history | /deploy |
| /user/users·/user/groups | /user |

## 표에서 셀 범위를 골라 복사하기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; buildTsv L83~101
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; isNativeInteractiveTarget L69~81
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 선택·복사 L226~259
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 셀 선택 표시 L443~473
원천: nori-tc-ui-front/src/shared/hooks/useGridRangeSelection.ts @ 9fe69a75dcd3 ; normalizeRange·범위 선택 L18~91
```

1. 공통 표의 셀을 누르고 끌어 사각형 범위를 선택한다. 선택 범위는 셀의 테두리와 배경으로 확인한다.
2. Ctrl+C를 누른다. 열은 탭, 행은 줄바꿈으로 나누어 복사하므로 스프레드시트에 붙여 넣을 수 있다.

셀 안의 입력 상자나 단추를 조작할 때는 셀 범위 선택 대신 입력 요소의 기본 동작이 적용된다.

## 표마다 다른 편집·붙여넣기·행 메뉴 쓰기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집·붙여넣기·Esc L262~306
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 편집 가능 셀 L443~445
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 읽기 전용 판정 L226~232
원천: nori-tc-ui-front/src/features/model/components/ModelDetailPanel.tsx @ 9fe69a75dcd3 ; 상세 표 L1080~1110
원천: nori-tc-ui-front/src/features/eqp/components/EqpParamTable.tsx @ 9fe69a75dcd3 ; 편집 입력 L46~105
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Check Out·파라미터 표 L1326~1392
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 행 메뉴 L240~260
```

모델 상세 표의 편집 가능한 셀은 두 번 눌러 입력 상태로 바꾼다. Ctrl+V는 선택을 시작한 셀에서부터 편집 가능한 칸에만 붙인다. 읽기 전용인 칸은 바뀌지 않는다. Esc는 셀 편집을 끝내며, 편집 중이 아니면 범위 선택을 해제한다.

설비 파라미터 표는 「Check Out」을 누른 편집 상태에서 셀이 바로 입력 상자로 나타난다.

행 메뉴가 있는 표에서는 행을 마우스 오른쪽 단추로 눌러 실행할 작업을 고른다.

## 목록 검색과 쪽 이동으로 자료 찾기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 검색어 상태·필터 L92~125
원천: nori-tc-ui-front/src/shared/layout/PagePagination.tsx @ 9fe69a75dcd3 ; PagePagination L60~158
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 정렬·너비 기본값 L171~179
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.3 표 공통 조작 L351~389
```

검색 칸이 있는 목록에서는 검색어를 입력해 표시 대상을 좁힌다. 예를 들어 설비 좌측 목록은 입력한 검색어로 목록을 걸러낸다. 결과가 없으면 검색어를 줄이거나 지운다.

쪽 이동 도구가 있는 표에서는 아래쪽 쪽 번호·앞뒤 화살표·쪽당 건수 선택을 사용한다. 현재 표시 범위와 전체 건수도 표 아래에서 확인한다.

업무 화면의 공통 표는 열 제목을 눌러 정렬하거나 열 경계를 끌어 너비를 바꾸는 기능을 켜 두지 않았다.

## 표가 비었거나 자료를 불러오는 중일 때 보기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 L398~420
원천: nori-tc-ui-front/src/shared/components/SelectableTable/SelectableTable.tsx @ 9fe69a75dcd3 ; 로딩·빈 표 분기 L570~581
원천: nori-tc-ui-front/src/features/dlq/components/DlqTableShell.tsx @ 9fe69a75dcd3 ; 오류·검색 결과 L88~151
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 목록 문구 L131~137
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 표 L474~475
```

공통 표가 처음 자료를 불러올 때는 열 구성을 유지한 회색 뼈대 행이 나타난다. 조회가 끝나고 행이 없으면 빈 목록 안내가 나타난다. 기본 문구는 「표시할 데이터가 없습니다.」이며 화면마다 문구가 다를 수 있다.

처리실패 목록의 「검색 결과가 없습니다.」는 검색어를 조정해 확인한다. 조회 실패 때는 서버 오류 문구 또는 「목록을 조회하지 못했습니다.」가 나타난다. 오류 문구 아래 「재시도」나 상단 「새로고침」을 눌러 다시 조회한다.

## 설비 명령의 진행과 결과 대화상자 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationProgress.tsx @ 9fe69a75dcd3 ; EqpOperationProgress L31~50
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultDialog.tsx @ 9fe69a75dcd3 ; EqpOperationResultDialog L81~131
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; EqpOperationResultBody L20~44
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; recheckLifecycleOperation L360~380
원천: nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; getOperationStatus L18~51
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; getResult L65~107
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 결과 문구 L1110~1118
```

설비 명령을 실행한 뒤에는 대화상자의 진행 문장과 결과를 확인한다. 결과에는 「PASS」·「FAIL」·「복구 필요」·「확인 지연」이 표시된다. 「확인」은 결과 대화상자를 닫는다.

「확인 지연」이고 「작업 ID」가 있으면 「다시 조회」가 나타난다. 「다시 조회」를 눌러 같은 작업의 결과를 다시 읽는다.

실패나 복구 필요 안내가 계속되면 설비 ID·실행한 명령·발생 시각·결과 문구와 화면에 표시된 「작업 ID」를 운영 담당자에게 전달한다.

## 화면 자체를 표시하지 못했을 때 새로고침하기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; render L35~55
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 오류 문구 L477~479
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 3.4.4 오류와 대처 L435~442
```

「화면을 표시하지 못했습니다.」가 나타나면 안내 아래 「새로고침」을 누른다. 같은 화면에서 다시 발생하면 화면 이름·발생 시각·직전에 한 조작·표시 문구를 운영 담당자에게 전달한다.

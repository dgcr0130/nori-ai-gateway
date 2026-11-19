# 로그인·권한·화면 오류
```rag-meta
유형: 트러블슈팅
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/login-and-screen-basics.md
```

## 로그인 입력 문제와 계정 문제를 가르는 순서
```rag-meta
원천: nori-tc-ui-front/src/features/auth/hooks/useLogin.ts @ 9fe69a75dcd3 ; buildLoginSchema·serverErrorMessage L22~33 L105~113
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; 입력 오류·서버 오류 표시 L200~298
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/usecase/LoginUseCase.java @ 985d17c604dd ; execute L87~116
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AuthController.java @ 985d17c604dd ; login L96~139
```

입력 칸 아래 안내만 나오면 먼저 입력을 고친다. 입력 조건과 로그인 조작은 `00.rag/user/login-and-screen-basics.md`를 따른다.

로그인 단추 아래 「아이디 또는 비밀번호가 올바르지 않습니다.」가 나오면 아이디 오입력·존재하지 않는 계정·비밀번호 불일치를 문구만으로 구별할 수 없다. 아이디를 확인하고 비밀번호를 다시 입력한다.

「사용 불가능한 계정입니다.」이면 계정 상태가 ACTIVE가 아닌 경우다. 비밀번호를 바꾸어 반복하기보다 운영 관리자에게 계정 상태 확인을 요청한다. 계정 관리 절차는 `00.rag/user/user-group-and-permission-management.md`를 따른다.

## 로그인 보안 검증 안내가 나올 때 확인할 요청
```rag-meta
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; buildForbiddenAuthResponse·login L27~41 L65~90 L129~160
원천: nori-tc-ui-front/src/shared/lib/csrf.ts @ 9fe69a75dcd3 ; requestCsrfCookie·issueCsrfToken L42~73
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 보안 안내 L839~841
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 공개 로그인 경로 L123~125
```

로그인 보안 안내이면 페이지를 새로고침한 뒤 다시 로그인한다. 계속되면 운영 담당자에게 발생 시각과 문구를 전달한다.

- 「로그인 보안 토큰 발급이 차단되었습니다.」로 시작하면 로그인에 앞선 `GET /api/auth/csrf`가 403으로 거부된 경로다.
- 「로그인 보안 검증에 실패했습니다.」로 시작하면 `POST /api/auth/login`이 403으로 거부된 경로다.
- 「CSRF 토큰을 확인할 수 없습니다.」로 시작하면 발급 요청 뒤 브라우저에서 보안 토큰을 읽지 못한 경로다.

이 안내만으로 비밀번호 불일치나 업무 권한 부족을 확정하지 않는다. 내부 설정 점검은 운영 담당자에게 맡긴다.

## 업무 중 로그인 화면으로 돌아오면 세션 만료인가
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/api-client.ts @ 9fe69a75dcd3 ; 응답 인터셉터 L41~51
원천: nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 ; RequireAuth L19~35
원천: nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 ; getMe L13~26
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiTokenAuthenticationFilter.java @ 985d17c604dd ; 인증 실패 처리 L168~200
```

업무 중 로그인 화면으로 돌아오면 먼저 다시 로그인한다. 공통 통신 처리가 401 실패를 받거나, 보호 화면 진입 때 현재 사용자 조회가 실패하면 로그인 화면으로 이동한다.

이동 현상만으로 세션 만료를 확정할 수 없다. 인증 쿠키 누락·세션 무효·계정 비활성·인증 처리 오류도 원인 후보이며 현재 사용자 조회 자체의 실패도 구별해야 한다.

로그인 직후에도 되돌아오면 운영 담당자에게 발생 시각·열려던 화면·로그인 성공 직후인지와 `GET /api/auth/me`의 응답 상태를 전달한다. 비밀번호·쿠키·보안 토큰 값은 전달하지 않는다.

## 로그인은 성공했는데 현재 사용자 조회가 403일 때
```rag-meta
원천: nori-tc-ui-front/src/app/RequireAuth.tsx @ 9fe69a75dcd3 ; 현재 사용자 조회 실패 L21~33
원천: nori-tc-ui-front/src/features/profile/api/profile.api.ts @ 9fe69a75dcd3 ; getMe L13~26
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 공개 경로·보호 요청 인가 L123~143 L251~282
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 내 정보 조회 기본 권한 L33~40
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_user_ui_sample_data.sql @ f1d5e0a8e727 ; 내 정보 조회 권한·기본 그룹 연결 L63~65 L134~145
```

로그인 성공 응답 뒤 `GET /api/auth/me`가 403이면 운영 관리자에게 현재 사용자 조회 권한을 확인해 달라고 요청한다. 저장소 기본 권한 데이터는 이 요청에 AUTH_ME_PERM을 연결한다. 현장 부여 상태를 뜻하지는 않는다.

현재 사용자 조회는 로그인 성공과 별개 요청이므로, 이 조회의 권한 거부도 로그인 화면으로 돌아오는 원인이 된다. 발생 시각·로그인 성공 여부·현재 사용자 조회의 403 결과를 함께 전달한다.

## 메뉴가 없거나 접근 권한 안내가 나올 때
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 메뉴 필터·로그인 진입 L140~170
원천: nori-tc-ui-front/src/app/RequirePermission.tsx @ 9fe69a75dcd3 ; RequirePermission L19~40
원천: nori-tc-ui-front/src/shared/components/AccessDeniedNotice.tsx @ 9fe69a75dcd3 ; 필요한 권한 표시 L15~34
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 안내 L1104~1106
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.3 권한 안내 화면 L2549~2559
```

현재 사용자 정보의 권한 목록으로 메뉴와 화면 접근을 판단한다. 「접근 권한이 없습니다」와 「필요한 권한」이 보이면 화면 접근 검사에서 막힌 것이다. 로그인 화면으로 돌아가는 인증 실패와 구별한다.

운영 관리자에게 열려던 화면과 「필요한 권한」의 코드를 전달한다. 메뉴가 전혀 없는 경우에도 로그인 성공 뒤 접근 권한 안내가 나올 수 있다.

화면별 권한은 `00.rag/user/permission-code-reference.md`, 부여 절차는 `00.rag/user/user-group-and-permission-management.md`에서 확인한다.

## 화면은 열리지만 조회나 저장 요청이 403일 때
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L251~282
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; isAuthorized L142~185
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; handleUiForbidden L190~198
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; normalizeApiError L67~79
```

화면 접근과 서버 요청의 권한 검사는 별개다. 화면이 열려도 조회·저장 요청은 거부될 수 있다. 403이면 입력을 바꾸기 전에 운영 관리자에게 실행한 작업의 권한 확인을 요청한다.

API 인가에는 권한 미보유 외에도 요청에 맞는 권한 등록 없음·권한 캐시 초기화 실패·권한 카탈로그 불일치라는 차단 경로가 있다. 403 하나만으로 어느 원인인지 구별할 수 없다.

운영 담당자에게 화면·누른 단추·대상·시각·응답 상태와 표시 문구를 전달한다. 이미 권한을 부여받았다고 안내받았다면 그 사실도 전달해 서버의 권한 반영 상태를 함께 확인하도록 한다.

## 서버 오류와 입력·대상 문제를 어디서 가르는가
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·충돌·대상·서버 예외 L89~115 L208~216 L324~332
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse L19~40
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 10.1 화면 오류 메시지와 대처 L2513~2533
```

조회·저장 실패에서는 표시 문구와 응답의 errorCode를 함께 확인한다. 입력·대상 부재·현재 상태 충돌이면 문구가 가리키는 입력이나 대상을 해당 업무 화면에서 확인한다. INTERNAL_ERROR와 500 응답이면 운영 담당자에게 서버 처리 오류로 전달한다.

문구가 같아 보여도 코드와 응답 상태가 다를 수 있으므로 화면·조작·대상·시각을 함께 기록한다. 코드별 상세 조치와 배포·설비 작업의 차단 조건은 `00.rag/user/error-message-and-code-reference.md`를 따른다.

## 기본 실패 문구만 나오면 네트워크 문제인가
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; createFailResponse·normalizeApiError L19~40 L67~79
원천: nori-tc-ui-front/src/shared/lib/api-client.ts @ 9fe69a75dcd3 ; 통신 제한시간 L14~21
원천: nori-tc-ui-front/src/features/auth/api/auth.api.ts @ 9fe69a75dcd3 ; normalizeAuthError L97~113
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 로그인 기본 실패 문구 L837~837
```

「로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.」 같은 기본 실패 문구만으로 네트워크 장애를 확정할 수 없다. 서버 실패 응답에 문구가 없거나 응답 형식을 해석하지 못한 경우에도 기본 문구가 나온다.

운영 담당자와 요청을 확인할 때 HTTP 응답 상태가 있는지부터 구별한다. 응답 상태가 없으면 네트워크·요청 시간 초과 등을 조사하고, 응답이 있으면 상태와 errorCode·errorMsg를 대조한다. 공통 통신 제한시간은 10초다.

UNKNOWN_ERROR는 원인을 하나로 정하는 코드가 아니다. 화면·시각·조작과 응답 유무를 함께 전달한다.

## 화면 표시 실패와 API 실패 문구를 구별하기
```rag-meta
원천: nori-tc-ui-front/src/shared/components/ErrorBoundary.tsx @ 9fe69a75dcd3 ; ErrorBoundary L14~55
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 화면 표시 실패 안내 L477~479
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 화면 경계 L72~79
```

「화면을 표시하지 못했습니다.」는 화면을 그리는 중 오류나 화면 파일을 불러오는 실패를 잡았을 때 나오는 안내다. 조회·저장 창 안에 나오는 API 실패 문구와 구별한다.

안내 영역의 「새로고침」은 현재 페이지 전체를 다시 읽는다. 새 버전 배포 뒤 화면 파일 불일치는 원인 후보지만, 이 안내만으로 배포 문제라고 확정하지 않는다.

다시 발생하면 운영 담당자에게 열려던 화면·시각·직전 조작·새로고침 뒤 같은 안내가 남는지를 전달한다.

## Undo 결과와 실패 표시가 서로 맞지 않을 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; undoEqpCheckout L529~545
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; assertSuccessResponse L82~92
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpParamMutations.ts @ 9fe69a75dcd3 ; Undo 성공 후 조회 갱신 L53~59
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; Undo 처리·표시 L979~992 L1283~1300
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpParamController.java @ 985d17c604dd ; undoCheckout L205~216
원천: nori-tc-ui-front/tests/unit/api-void-endpoint-contract.test.ts @ 9fe69a75dcd3 ; Undo 성공 응답 기대값 L83~85 L101~125
```

설비 파라미터의 「Undo」 뒤 실패 문구와 실제 상태가 맞지 않으면 같은 작업을 반복하기 전에 화면을 새로고침해 체크아웃 상태와 파라미터를 다시 확인한다.

335473ea 수정 이후 판본에서는 Undo 응답이 success=true이면 data가 null이어도 성공으로 처리하고 관련 조회를 갱신한다. 확인한 기준 소스에도 이 처리가 있다.

운영 담당자에게 설비 ID·시각·실패 문구·새로고침 뒤 상태를 전달하고 프런트엔드 배포 판본에 335473ea 수정이 포함됐는지 확인을 요청한다. 체크아웃 조작은 `00.rag/user/equipment-model-parameter-and-jar-update.md`를 따른다.

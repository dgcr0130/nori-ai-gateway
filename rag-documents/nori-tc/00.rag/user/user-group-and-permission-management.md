# 사용자·그룹·권한 관리
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-common 6fe71f6b2da6·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/login-and-screen-basics.md
```

## 사용자 계정과 권한 그룹 화면 열기
```rag-meta
원천: nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 사용자 화면 권한 검사 L107~109
원천: nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; USER_READ 값 L7~10
원천: nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 시스템 메뉴 L126~138
원천: nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 카드 전환 L12~18 L25~108
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관리 화면 문구 L89~108 L1091~1103
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 그룹을 통한 권한 조회 L82~118
```

운영 관리자는 USER_READ가 있어야 「시스템」의 「사용자·그룹」을 열 수 있다. 화면 제목은 「사용자·그룹 관리」이다.

「사용자 계정」 카드는 계정 작업을, 「권한 그룹」 카드는 그룹·멤버·권한 작업을 연다. 카드를 바꾸면 아래 작업 영역이 바뀐다.

사용자 권한은 소속 그룹에 연결된 활성 권한을 모아 정한다. 사용자에게 업무 권한을 주려면 그 권한을 그룹에 부여하고 사용자를 그룹에 넣는다.

## 계정 작업과 그룹 작업에 필요한 권한 확인하기
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 멤버·권한 요청 L279~436
```

다음은 저장소 기본 권한 데이터 기준이다. 화면 진입에는 별도로 USER_READ가 필요하며 현장 권한 연결은 다를 수 있다.

| 화면 작업 | 필요한 작업 권한 |
|---|---|
| 사용자 목록·상세 조회 | USER_READ·USER_INFO_WRITE·USER_WRITE 중 하나 |
| 사용자 등록·수정·삭제·비밀번호 초기화 | USER_INFO_WRITE·USER_WRITE 중 하나 |
| 그룹 목록·상세·멤버 목록 조회, 그룹 등록·수정·삭제 | GROUP_WRITE |
| 그룹에 멤버 추가·제거 | USER_INFO_WRITE·USER_WRITE 중 하나 |
| 그룹 권한 목록 조회·부여·회수 | GROUP_WRITE |
| 권한 후보 목록 조회 | PERMISSION_WRITE |

## 사용자 목록에서 계정과 그룹 소속 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록·쪽 이동 L16~16 L35~85 L161~190
원천: nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 사용자 집계 L29~34 L65~95
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 집계 문구 L98~106
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 목록과 그룹 연결 L61~87
```

「사용자 계정」의 표는 「userId」·「userName」·「group」·「company」·「department」·「액션」 순이다. 「group」에는 소속 그룹 코드가 표시되며 소속이 없으면 「-」가 나온다.

계정은 한 쪽에 20개씩 본다. 아래 「Prev」·「Next」로 이동한다. 사용자 목록에는 검색 칸이 없다.

「전체 사용자」는 전체 건수이다. 「현재 페이지 활성」은 ACTIVE인 계정 수, 「현재 페이지 확인 필요」는 나머지 계정 수이다. 「현재 페이지 그룹 연결」은 보이는 사용자들의 그룹 연결 건수 합계이다.

## 새 사용자 계정 등록하기
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 등록 단추 L53~61
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 입력 문구 L882~900
원천: nori-tc-ui-front/src/features/user/api/user.api.ts @ 9fe69a75dcd3 ; 생성 요청 L162~179
원천: nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 등록 후 목록 갱신 L23~31
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 등록 L124~132
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; 필수값·이메일 검사 L32~56
```

USER_INFO_WRITE·USER_WRITE 중 하나가 있는 운영 관리자가 등록한다. 기존 계정과 다른 새 아이디를 준비한다.

1. 「사용자 계정」의 「사용자 등록」을 누른다.
2. 「회사」·「부서」·「이름」·「아이디」·「이메일」·「비밀번호 (필수)」를 채운다. 이메일은 서버가 형식도 검사한다.
3. 「상태」를 고른다. 기본값은 ACTIVE이며 LOCKED·DISABLED·DELETED도 선택할 수 있다.
4. 「등록」을 누른다. 성공하면 창이 닫히고 목록이 다시 조회된다. 그만두려면 「취소」를 누른다.

## 계정 필수값이나 이메일 입력이 거부될 때
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·오류 표시 L118~176 L294~311
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 실패 문구 L860~888
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; 이메일 검사 L48~50
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 첫 입력 오류 반환 L231~247
```

사용자 등록·수정 창에서 다음 문구가 나오면 입력을 고친 뒤 「등록」 또는 「저장」을 누른다.

| 표시 문구 | 할 일 |
|---|---|
| 필수 입력값(회사, 부서, 이름, 아이디, 이메일)을 모두 채워주세요. | 다섯 칸의 빈 값과 공백만 넣은 값을 채운다. |
| 등록 시 비밀번호는 필수입니다. | 등록할 비밀번호를 입력한다. |
| email 형식이 올바르지 않습니다. | 「이메일」을 올바른 이메일 형식으로 고친다. |
| 수정 대상 사용자를 확인할 수 없습니다. | 창을 닫고 화면을 새로고침해 대상을 다시 고른다. |

## 다른 사용자의 부서와 계정 정보 수정하기
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 수정 진입 L96~105 L149~156
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290
원천: nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 수정 후 갱신 L38~48
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 문구 L882~900
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 수정 L104~107 L156~167
```

USER_INFO_WRITE·USER_WRITE 중 하나가 있는 운영 관리자가 「사용자 계정」에서 수정한다.

1. 대상 행의 연필 단추를 누르거나 행을 두 번 누른다.
2. 「사용자 수정」에서 「회사」·「부서」·「이름」·「이메일」·「상태」를 고친다. 「아이디」는 수정할 수 없다.
3. 「저장」을 누른다. 성공하면 창이 닫히고 목록이 다시 조회된다.

본인 정보 저장이 부서 누락으로 막힌 사용자의 요청을 받으면 운영 관리자가 이 창의 「부서」를 채운다. 「비밀번호 (미입력 시 유지)」를 비워 두면 기존 비밀번호를 유지한다. 새 값을 넣으면 함께 변경한다.

## 관리자가 입력한 비밀번호가 거부될 때
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; 비밀번호 검사 L22~46
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; 신규·변경 검사 L50~75
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 검사 연결 L95~101 L143~153 L156~167
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 화면 검사 L130~156 L175~176
```

사용자 등록·수정에서 비밀번호를 입력하면 서버가 정책을 검사한다. 10~128자로 쓰고 공백 없이 소문자·대문자·숫자·특수문자 중 3종 이상을 포함한다. 기존 사용자 변경에는 최근 5회 재사용 제한도 적용한다.

「비밀번호는 10자 이상 128자 이하여야 합니다.」나 「비밀번호에는 공백 문자를 사용할 수 없습니다.」가 나오면 해당 조건을 고친다.

「비밀번호는 소문자/대문자/숫자/특수문자 중 3종 이상을 포함해야 합니다.」 또는 「최근 5회 내 사용한 비밀번호는 재사용할 수 없습니다.」가 나오면 다른 비밀번호를 입력한다.

## 기존 아이디나 그룹 코드로 다시 등록하지 않기
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminAssembler.java @ 985d17c604dd ; 아이디 정규화 L205~237
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 생성 저장 L95~101
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 생성 저장 L74~76
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserInfoJpaStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L72~84 L273~277
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserGroupJpaStore.java @ 6fe71f6b2da6 ; 기존 그룹 갱신 L63~79 L193~200
원천: nori-tc-common/db/mybatis/tc-db-mybatis-common-schema/src/main/java/com/nori/tc/db/mybatis/common/store/user/TcUserInfoMybatisStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L60~102
원천: nori-tc-common/db/mybatis/tc-db-mybatis-common-schema/src/main/java/com/nori/tc/db/mybatis/common/store/user/TcUserGroupMybatisStore.java @ 6fe71f6b2da6 ; 기존 그룹 갱신 L48~66 L165~170
```

새 계정은 기존 아이디와 대소문자만 다른 아이디도 피한다. 확인한 저장소 구현은 아이디의 앞뒤 공백을 제거하고 소문자로 비교해 기존 계정을 찾아 갱신한다. 같은 아이디 등록으로 기존 계정 정보와 비밀번호가 바뀔 수 있다.

같은 그룹 코드로 「그룹 등록」을 실행해도 기존 그룹을 찾아 갱신할 수 있다. 기존 계정·그룹을 고칠 때는 목록의 수정 단추를 사용한다.

## 사용자와 그룹 입력 길이 맞추기
```rag-meta
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 칸 L190~245
원천: nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 칸 L136~169
```

사용자·그룹 등록과 수정의 입력 길이는 저장소 기본 스키마 기준으로 다음 범위에 맞춘다. 화면 입력 칸에는 이 최대 길이를 제한하는 처리가 없다.

| 입력 항목 | 최대 길이 |
|---|---|
| 사용자 「회사」·「부서」·「이름」 각각 | 100자 |
| 사용자 「아이디」 | 50자 |
| 사용자 「이메일」 | 255자 |
| 그룹 「그룹 코드」 | 50자 |
| 그룹 「그룹 이름」 | 100자 |
| 그룹 「설명」 | 1000자 |

## 사용자 계정 삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 삭제 진입 L116~125
원천: nori-tc-ui-front/src/features/user/components/UserDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·실패 L29~63
원천: nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 삭제 후 목록 갱신 L54~62
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L864~871
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 삭제 L109~111
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaUserCrudPort.java @ 985d17c604dd ; 삭제 처리 L198~228
```

USER_INFO_WRITE·USER_WRITE 중 하나가 있는 운영 관리자가 「사용자 계정」에서 삭제한다.

1. 대상 아이디를 확인하고 행의 휴지통 단추를 누른다.
2. 「사용자 삭제」의 대상과 되돌릴 수 없다는 안내를 읽고 「삭제」를 누른다. 중단하려면 「취소」를 누른다.
3. 성공하면 창이 닫히고 사용자 목록이 갱신된다.

실패하면 창 안의 서버 문구 또는 「사용자 삭제에 실패했습니다.」를 확인한다. 대상 아이디·발생 시각·표시 문구를 운영 담당자에게 전달한다.

## 열쇠 단추로 다른 사용자의 비밀번호 초기화하기
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 열쇠 단추 L106~115
원천: nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 문구 L872~881
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 초기화 L143~153
```

USER_INFO_WRITE·USER_WRITE 중 하나가 있어야 한다. 관리자는 대상 사용자의 권한도 모두 보유해야 한다.

1. 「사용자 계정」에서 대상 행의 열쇠 단추를 누른다. 「비밀번호 초기화」를 열면 임시 비밀번호를 만들고 서버에 변경을 바로 요청한다.
2. 「서버에 비밀번호 변경을 적용하는 중...」이 사라지고 오류가 없는지 확인한다. 초기화했다는 안내 문장은 요청 중에도 표시된다.
3. 「복사」를 눌러 「클립보드에 복사되었습니다.」를 확인하고 안전한 방법으로 본인에게 전달한다.
4. 「닫기」를 누른다. 닫은 창의 비밀번호는 다시 볼 수 없다.

## 비밀번호 초기화 거부와 복사 실패 처리하기
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 오류·복사·재표시 L38~48 L71~111 L147~170
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 실패 문구 L878~881
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 초기화 대상 제한 L95~105
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 초기화 대상 부재 L254~267
```

「본인보다 넓은 권한을 가진 사용자의 비밀번호는 재설정할 수 없습니다.」 뒤에 대상 번호가 나오면, 대상의 모든 권한을 보유한 관리자에게 초기화를 요청한다.

서버 문구 또는 「비밀번호 초기화에 실패했습니다. 잠시 후 다시 시도해 주세요.」가 나오면 표시된 임시 값을 전달하지 않는다. 대상 부재이면 목록을 다시 조회하고 권한 거부이면 권한을 확인한다.

「클립보드 복사에 실패했습니다. 직접 선택해 복사해 주세요.」이면 값을 직접 선택해 복사한다. 창을 다시 열면 새 초기화 요청이 발생하므로 같은 값을 조회하는 용도로 다시 열지 않는다.

## 권한 그룹 목록과 작업 단추 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록·단추·쪽 이동 L24~24 L52~137 L195~224
원천: nori-tc-ui-front/src/features/group/components/GroupAdminPanel.tsx @ 9fe69a75dcd3 ; 집계 L34~37 L81~105
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 집계·단추 L107~116 L954~957
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹 조회 기본 권한 L141~149
```

기본 권한 기준으로 GROUP_WRITE가 있어야 「권한 그룹」의 목록을 조회한다. 표는 「groupCode」·「groupName」·「description」·「isActive」·「updatedAt」·「액션」 순이다.

「액션」에는 방패 「권한 관리」, 사람 여럿 「멤버 관리」, 연필 「그룹 수정」, 휴지통 「그룹 삭제」가 있다.

그룹도 한 쪽에 20개씩 보며 아래 「Prev」·「Next」로 이동한다. 「전체 그룹」은 전체 건수이고 「현재 페이지 활성」·「현재 페이지 비활성」은 보이는 그룹만 센다.

## 새 권한 그룹 만들기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupAdminPanel.tsx @ 9fe69a75dcd3 ; 등록 단추 L69~77
원천: nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202
원천: nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 목록 갱신 L24~32
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 입력 문구 L911~922
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/GroupUpsertRequest.java @ 985d17c604dd ; 필수값 L19~29
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 생성 L74~76
```

GROUP_WRITE가 있는 운영 관리자가 기존 그룹과 다른 코드를 준비한다.

1. 「권한 그룹」에서 「그룹 등록」을 누른다.
2. 필수인 「그룹 코드」·「그룹 이름」을 채운다. 「설명」은 선택이다.
3. 「활성화 (사용자에게 부여 가능)」의 체크 상태를 정한다. 처음에는 켜져 있다.
4. 「등록」을 누른다. 성공하면 창이 닫히고 그룹 목록이 갱신된다. 그만두려면 「취소」를 누른다.

새 그룹의 업무 권한은 그룹 행의 「권한 관리」에서 별도로 부여한다.

## 그룹 설명과 활성 표시 고치기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 상태·수정 진입 L69~76 L116~125 L182~189
원천: nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 L63~69 L109~122 L138~179
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정·활성 문구 L911~921
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 그룹 편입 검사 L113~119
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 권한 조회 L82~118
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 편입 시 권한 검사 L77~84 L108~115
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserGroupMemberJpaStore.java @ 6fe71f6b2da6 ; 편입 저장·입력 검사 L78~99 L306~315
```

GROUP_WRITE가 있으면 그룹 행의 「그룹 수정」을 누르거나 행을 두 번 눌러 「그룹 이름」·「설명」·활성 체크를 고친 뒤 「저장」한다. 「그룹 코드」는 화면에서 고칠 수 없다.

활성 체크를 끄면 표의 「isActive」는 INACTIVE로 나온다. 확인한 편입·권한 조회 경로는 그룹의 활성 여부로 차단하지 않는다. 사용자에게서 그룹 권한을 거두려면 「멤버 관리」에서 소속을 제거한다.

## 권한 그룹 삭제하기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인 L29~63
원천: nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 삭제 후 갱신 L53~61
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L907~907 L923~925
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 삭제 L85~87
원천: nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 그룹 삭제 연결 규칙 L1040~1069
```

GROUP_WRITE가 있는 운영 관리자가 삭제한다. 저장소의 기본 스키마에서는 그룹 삭제 시 그룹의 멤버 연결과 권한 연결도 함께 삭제된다.

1. 「권한 그룹」에서 대상 행의 휴지통 「그룹 삭제」를 누른다.
2. 「그룹 삭제」 창에서 대상 코드와 되돌릴 수 없다는 안내를 확인하고 「삭제」를 누른다. 중단하려면 「취소」를 누른다.
3. 성공하면 창이 닫히고 목록이 갱신된다.

실패하면 창 안의 서버 문구 또는 「그룹 삭제에 실패했습니다.」와 대상 코드·시각을 운영 담당자에게 전달한다.

## 그룹 필수값이나 저장이 거부될 때
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 오류 L88~123 L184~202
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 오류 L904~916
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 수정 대상 부재 L140~155
```

「필수 입력값(그룹 코드, 그룹 이름)을 모두 채워주세요.」이면 두 칸을 채운다. 「수정 대상 그룹을 확인할 수 없습니다.」 또는 「수정할 그룹을 찾을 수 없습니다.」이면 창을 닫고 화면을 새로고침해 대상을 다시 고른다.

생성·저장 실패에는 서버 문구가 우선 표시된다. 서버 문구가 없으면 「그룹 생성에 실패했습니다.」·「그룹 정보 저장에 실패했습니다.」·「저장에 실패했습니다. 잠시 후 다시 시도해 주세요.」가 나올 수 있다.

입력을 고쳐도 실패하면 그룹 코드·발생 시각·누른 단추·표시 문구를 운영 담당자에게 전달한다.

## 그룹에 사용자 추가하기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 멤버 관리 진입 L105~115
원천: nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 멤버 추가 L300~322
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 문구 L929~940
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 편입 L113~119
```

GROUP_WRITE로 그룹·멤버를 조회하고 USER_INFO_WRITE·USER_WRITE 중 하나로 멤버를 추가한다. 관리자는 그룹의 권한도 모두 보유해야 한다.

1. 「권한 그룹」의 대상 행에서 사람 여럿 모양 「멤버 관리」를 누른다.
2. 「그룹 멤버 추가」의 「userId 또는 이름으로 검색」으로 찾는다. 검색은 대소문자를 구별하지 않는다.
3. 추가할 사용자 체크를 켜고 「추가」를 누른다.
4. 「추가 …건 / 실패 …건」을 확인한다. 실패가 있으면 「첫 오류」도 읽는다.

이미 소속된 사용자는 체크가 켜져 있고 체크를 바꿀 수 없다. 선택한 사용자가 없으면 「추가」가 비활성화된다.

## 그룹에서 사용자 소속 제거하기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 제거·결과 표시 L100~111 L199~234
원천: nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 제거 후 갱신 L84~93
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 제거 요청 L324~345
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 제거 문구 L909~909 L935~935
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 소속 제거 L122~124
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 그룹별 권한 합집합 L82~118
```

GROUP_WRITE로 멤버 목록을 조회하고 USER_INFO_WRITE·USER_WRITE 중 하나로 소속을 제거한다.

「권한 그룹」 행의 「멤버 관리」를 열고 기존 멤버 줄의 「멤버 제거」를 누른다. 이 단추는 제거 요청을 바로 보낸다. 성공하면 멤버 목록과 사용자 목록이 다시 조회되고 기존 멤버 표시가 풀린다. 다른 소속 그룹에도 같은 권한이 있으면 사용자는 그 권한을 계속 가진다.

실패하면 창 안의 서버 문구 또는 「그룹 멤버 제거에 실패했습니다.」를 확인한다. 대상 아이디·그룹 코드·발생 시각·문구를 운영 담당자에게 전달한다. 끝나면 「닫기」를 누른다.

## 그룹 멤버 추가가 거부되거나 사용자가 안 보일 때
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 대상·선택 검사·실패·빈 목록 L121~158 L189~197 L241~262
원천: nori-tc-ui-front/src/features/group/hooks/useGroups.ts @ 9fe69a75dcd3 ; 멤버 조회 범위 L34~39
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 오류 L903~909 L934~940
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 편입 제한 L77~84
```

「본인이 보유하지 않은 권한을 가진 그룹에는 사용자를 편입할 수 없습니다.」 뒤에 그룹 번호가 나오면 그룹의 모든 권한을 보유한 관리자에게 추가를 요청한다.

「추가할 사용자를 선택해 주세요.」이면 신규 사용자를 체크한다. 「대상 그룹을 확인할 수 없습니다.」이면 창을 닫고 그룹을 다시 고른다. 추가 실패의 첫 오류는 서버 문구 또는 「그룹 멤버 추가에 실패했습니다.」이다.

「표시할 사용자가 없습니다.」이면 검색어를 지워 확인한다. 멤버 조회는 처음 500건만 사용하므로 큰 그룹의 표시 누락은 아이디·그룹 코드·시각과 함께 운영 담당자에게 전달한다.

## 그룹에 줄 권한 찾아보기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 활성 권한·검색·묶음·표시 L78~122 L205~272
원천: nori-tc-ui-front/src/features/group/lib/permission-category.ts @ 9fe69a75dcd3 ; 묶음 순서 L46~55
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 문구 L941~965
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹·권한 기본 권한 L141~149 L258~266
```

기본 권한 기준으로 GROUP_WRITE와 PERMISSION_WRITE가 있어야 그룹의 보유 권한과 후보 목록을 함께 조회한다.

「권한 그룹」 행의 방패 「권한 관리」를 누른다. 「권한 코드 / 이름 / 경로로 검색」은 실제로 권한 코드·권한 이름·설명을 대소문자 구별 없이 검색한다. 활성 권한만 후보로 나온다.

목록의 묶음은 「설비 (EQP)」·「모델 (MODEL)」·「배포 (DEPLOY)」·「장애 / DLQ」·「LIVE MAP」·「사용자 · 그룹 · 권한 관리」·「공통 기능」·「기타」 순이며 항목이 있는 묶음만 보인다.

권한에는 이름·코드·허용 방식이 표시되고, 설명이 있으면 함께 나온다. 아래쪽 권한은 창 안 목록을 내려서 찾는다.

## 그룹 권한을 부여하거나 회수하고 저장하기
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 부여·회수 요청 L400~436
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 부여·회수 L101~114
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 부여 제한 L57~66
```

GROUP_WRITE로 그룹 권한을 변경하며, 부여할 권한은 관리자 본인도 보유해야 한다. 후보 조회에는 PERMISSION_WRITE도 필요하다.

1. 「그룹 권한 관리」에서 줄 권한은 체크를 켜고 거둘 권한은 끈다. 체크 변경은 아직 서버에 저장되지 않는다.
2. 「저장」을 누른다. 바뀐 것이 없으면 「저장」은 비활성화된다. 모두 성공하면 창이 닫힌다.
3. 「취소」나 창 닫기는 아직 저장하지 않은 변경을 버린다.

오른쪽 위 「… / … 권한 보유」는 미저장 체크도 포함한다. 저장은 권한별 요청으로 처리하므로 일부만 성공할 수 있다.

## 권한 저장이 일부 실패하거나 부여가 거부될 때
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 일부 실패 보존 L143~183
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 실패 대체 문구 L400~436
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 문구 L910~910 L949~953
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 부여 거부 L57~66
```

「본인이 보유하지 않은 권한은 부여할 수 없습니다.」 뒤에 권한 코드가 나오면 해당 권한을 보유한 관리자에게 부여를 요청한다.

「저장」 후 일부 실패하면 창이 열려 있고 실패한 변경만 미저장 상태로 남는다. 서버 문구 또는 「권한 부여에 실패했습니다.」·「권한 회수에 실패했습니다.」가 표시된다. 원인을 해결한 뒤 「저장」하면 남은 변경을 요청한다.

이때 「취소」해도 이미 성공한 변경은 되돌아가지 않는다. 그룹 코드·권한 코드·시각·표시 문구를 기록해 운영 담당자에게 전달한다.

## 목록이나 권한 후보가 비어 있을 때 확인하기
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록 기본값·빈 표 L35~42 L139~147
원천: nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록 기본값·빈 표 L43~50 L174~181
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 조회·빈 목록·오류 표시 L43~44 L78~91 L229~237 L280~287
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·조회 실패 문구 L105~115 L947~951
원천: nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 조회 오류 L347~398
```

「표시할 사용자가 없습니다.」·「표시할 그룹이 없습니다.」가 나와도 목록에는 조회 오류를 별도로 표시하는 영역이나 「재시도」 단추가 없다. 화면을 새로고침해도 비어 있으면 대상 화면·시각을 운영 담당자에게 전달한다.

권한 창의 「표시할 권한이 없습니다.」이면 검색어를 지운다. 권한 조회 실패도 이 창의 오류 영역에는 직접 표시되지 않는다. 창을 다시 열어도 같으면 그룹 코드와 시각을 전달해 GROUP_WRITE·PERMISSION_WRITE 부여 여부를 확인한다.

## 저장 오류와 작업 권한 거부를 구별해 전달하기
```rag-meta
원천: nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 우선 L19~40 L49~79
원천: nori-tc-ui-front/src/shared/lib/notify.ts @ 9fe69a75dcd3 ; 오류 알림 L14~17
원천: nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 저장 오류 L175~176 L294~298
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 오류 L862~888
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·충돌·대상 권한 거부 L89~115 L190~198
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaUserCrudPort.java @ 985d17c604dd ; 저장 충돌 L88~100
```

사용자 등록·수정 실패는 서버가 보낸 문구를 우선 오류 알림과 창 안에 표시한다. 서버 문구가 없으면 「사용자 생성에 실패했습니다.」·「사용자 정보 저장에 실패했습니다.」 같은 작업별 문구가 나온다.

필수값·이메일·비밀번호 조건 오류는 입력을 고친다. 「사용자 저장 중 충돌이 발생했습니다.」이면 아이디와 이메일의 기존 사용 여부를 운영 담당자에게 확인한다. 일반 생성 실패만으로 아이디 중복이라고 단정하지 않는다.

권한 부족이나 대상 권한 범위 거부이면 입력을 바꾸어 반복하지 않고 권한을 가진 관리자에게 요청한다. 화면·대상 아이디 또는 그룹 코드·발생 시각·실행한 조작·표시 문구를 전달하며 비밀번호는 전달 기록에 넣지 않는다.

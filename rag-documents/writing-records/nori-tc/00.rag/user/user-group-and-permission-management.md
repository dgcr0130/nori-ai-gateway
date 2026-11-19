# 사용자·그룹·권한 관리 작성 기록

## 대상

- objectKey: `00.rag/user/user-group-and-permission-management.md`
- 구획: user `[AI_DOC_READ]`
- 유형: 사용 절차
- 검색 원문 SHA-256: `ab997cede5665995310408e0f7699cd1854c7e6f468d7ca45a3b3d14f08b0133`
- 검색 원문 줄 수: 405
- H2 절 수: 25
- 확인일: 2026-11-18 (한국 시간)
- 작성 범위: 검색 원문과 이 작성 기록 두 파일. 위임·제품 변경·업로드·git add/commit/push 없음.

## 원천 판본

| 저장소 | HEAD 12자리 | 인용 원천 파일 상태 |
|---|---|---|
| nori-tc-ui-front | 9fe69a75dcd3 | 인용한 24개 경로 status --short 출력 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 인용한 15개 경로 status --short 출력 없음, exit 0 |
| nori-tc-common | 6fe71f6b2da6 | 인용한 5개 경로 status --short 출력 없음, exit 0 |
| nori-tc-exec | f1d5e0a8e727 | 인용한 3개 경로 status --short 출력 없음, exit 0 |

쓰기에 앞서 관련 경로의 상태를 확인했고 최종 원문에 인용한 47개 고유 경로를 다시 확인했다. 사용한 명령은 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C ../<저장소> status --short -- <인용 경로들>`이다. commit은 같은 명령 접두사로 `rev-parse --short=12 HEAD`를 조회했다. 인용 파일은 모두 clean이므로 파일 SHA 판본 대체는 없었다.

초기 safe.directory 인자 없는 형제 저장소 git 조회는 프로세스가 시작된 뒤 exit 128, `fatal: detected dubious ownership in repository`로 끝났다. 명령별 정확한 저장소 경로에만 safe.directory를 지정해 읽기 전용 조회를 완료했다. 전역 Git 설정은 변경하지 않았다. 성공한 status의 stderr에는 `warning: unable to access`와 사용자 전역 ignore 경로의 `Permission denied`가 있었으나 exit 0, 대상 tracked 경로 변경 출력은 없었다.

먼저 작성 규칙·53개 문서 목록 전체, U §9(L2258~2508), 앞선 로그인·공통 화면 문서를 읽었다. 추가된 문서도 제목을 검색해 사용자·그룹 계정 관리와 겹치는 본문이 있는지 확인했다. 원문 동작은 프론트 렌더·한국어 문구→요청 API→서버 DTO·Facade·인가·저장소 순서로 대조했다.

관련 시험의 기대값은 `nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuardTest.java` L66~119(부여·편입·초기화 허용/거부), `PasswordPolicyValidatorTest.java` L31~60(강도·공백·빈 값·길이), `UiPasswordPolicyServiceTest.java` L30~84(5회 재사용 범위)를 읽었다. 시험을 새로 실행했다는 뜻이 아니다.

## 절별 사실 대조

각 표는 최종 검색 원문의 모든 본문 문장과 표 자료 행을 대응시킨다. 한 줄의 여러 문장은 문장별로 나누고, 목록의 진행 번호도 원문 순서를 확인했다. 인계·재조회 지시는 확인한 화면 결과에 따른 사용자 조치이며 장애 원인을 확정하는 주장이 아니다. 작업 권한이 등장하는 행은 아래 개별 원천 외에 「계정 작업과 그룹 작업에 필요한 권한 확인하기」의 기본 데이터와 인가 코드도 함께 대조했다.

### 사용자 계정과 권한 그룹 화면 열기

라우트 가드·메뉴·카드 렌더와 그룹 기반 활성 권한 조회를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L20 | 운영 관리자는 USER_READ가 있어야 「시스템」의 「사용자·그룹」을 열 수 있다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 사용자 화면 권한 검사 L107~109 / nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; USER_READ 값 L7~10 / nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 시스템 메뉴 L126~138 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관리 화면 문구 L89~108 L1091~1103 | 라우트 가드·메뉴·카드 렌더와 그룹 기반 활성 권한 조회를 대조했다. |
| L20 | 화면 제목은 「사용자·그룹 관리」이다. | nori-tc-ui-front/src/app/Router.tsx @ 9fe69a75dcd3 ; 사용자 화면 권한 검사 L107~109 / nori-tc-ui-front/src/shared/lib/permission.ts @ 9fe69a75dcd3 ; USER_READ 값 L7~10 / nori-tc-ui-front/src/shared/lib/app-navigation.ts @ 9fe69a75dcd3 ; 시스템 메뉴 L126~138 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관리 화면 문구 L89~108 L1091~1103 | 라우트 가드·메뉴·카드 렌더와 그룹 기반 활성 권한 조회를 대조했다. |
| L22 | 「사용자 계정」 카드는 계정 작업을, 「권한 그룹」 카드는 그룹·멤버·권한 작업을 연다. | nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 카드 전환 L12~18 L25~108 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관리 화면 문구 L89~108 L1091~1103 | 라우트 가드·메뉴·카드 렌더와 그룹 기반 활성 권한 조회를 대조했다. |
| L22 | 카드를 바꾸면 아래 작업 영역이 바뀐다. | nori-tc-ui-front/src/features/user-management/components/UserManagementPage.tsx @ 9fe69a75dcd3 ; 카드 전환 L12~18 L25~108 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 관리 화면 문구 L89~108 L1091~1103 | 라우트 가드·메뉴·카드 렌더와 그룹 기반 활성 권한 조회를 대조했다. |
| L24 | 사용자 권한은 소속 그룹에 연결된 활성 권한을 모아 정한다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 그룹을 통한 권한 조회 L82~118 | 라우트 가드·메뉴·카드 렌더와 그룹 기반 활성 권한 조회를 대조했다. |
| L24 | 사용자에게 업무 권한을 주려면 그 권한을 그룹에 부여하고 사용자를 그룹에 넣는다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 그룹을 통한 권한 조회 L82~118 | 라우트 가드·메뉴·카드 렌더와 그룹 기반 활성 권한 조회를 대조했다. |

### 계정 작업과 그룹 작업에 필요한 권한 확인하기

기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L35 | 다음은 저장소 기본 권한 데이터 기준이다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |
| L35 | 화면 진입에는 별도로 USER_READ가 필요하며 현장 권한 연결은 다를 수 있다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |
| L39 | \| 사용자 목록·상세 조회 \| USER_READ·USER_INFO_WRITE·USER_WRITE 중 하나 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |
| L40 | \| 사용자 등록·수정·삭제·비밀번호 초기화 \| USER_INFO_WRITE·USER_WRITE 중 하나 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |
| L41 | \| 그룹 목록·상세·멤버 목록 조회, 그룹 등록·수정·삭제 \| GROUP_WRITE \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |
| L42 | \| 그룹에 멤버 추가·제거 \| USER_INFO_WRITE·USER_WRITE 중 하나 \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |
| L43 | \| 그룹 권한 목록 조회·부여·회수 \| GROUP_WRITE \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |
| L44 | \| 권한 후보 목록 조회 \| PERMISSION_WRITE \| | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 기본 권한 연결 L141~149 L258~293 / nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 관리 기본 권한 L786~788 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd ; 요청 인가 L142~143 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 권한 대조 L142~185 L224~258 | 기본 PREFIX·메서드 연결을 서버 anyMatch 인가와 대조했다. 사용자별 현장 권한은 주장하지 않는다. |

### 사용자 목록에서 계정과 그룹 소속 확인하기

렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L54 | 「사용자 계정」의 표는 「userId」·「userName」·「group」·「company」·「department」·「액션」 순이다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록·쪽 이동 L16~16 L35~85 L161~190 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |
| L54 | 「group」에는 소속 그룹 코드가 표시되며 소속이 없으면 「-」가 나온다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록·쪽 이동 L16~16 L35~85 L161~190 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |
| L56 | 계정은 한 쪽에 20개씩 본다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록·쪽 이동 L16~16 L35~85 L161~190 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |
| L56 | 아래 「Prev」·「Next」로 이동한다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록·쪽 이동 L16~16 L35~85 L161~190 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |
| L56 | 사용자 목록에는 검색 칸이 없다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록·쪽 이동 L16~16 L35~85 L161~190 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |
| L58 | 「전체 사용자」는 전체 건수이다. | nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 사용자 집계 L29~34 L65~95 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 집계 문구 L98~106 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |
| L58 | 「현재 페이지 활성」은 ACTIVE인 계정 수, 「현재 페이지 확인 필요」는 나머지 계정 수이다. | nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 사용자 집계 L29~34 L65~95 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 집계 문구 L98~106 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |
| L58 | 「현재 페이지 그룹 연결」은 보이는 사용자들의 그룹 연결 건수 합계이다. | nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 사용자 집계 L29~34 L65~95 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 집계 문구 L98~106 | 렌더 열 순서·그룹 칩·20개 쪽 이동·집계 계산을 대조했다. |

### 새 사용자 계정 등록하기

필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L71 | USER_INFO_WRITE·USER_WRITE 중 하나가 있는 운영 관리자가 등록한다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L71 | 기존 계정과 다른 새 아이디를 준비한다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L73 | 「사용자 계정」의 「사용자 등록」을 누른다. | nori-tc-ui-front/src/features/user/components/UserAdminPanel.tsx @ 9fe69a75dcd3 ; 등록 단추 L53~61 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 입력 문구 L882~900 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L74 | 「회사」·「부서」·「이름」·「아이디」·「이메일」·「비밀번호 (필수)」를 채운다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 입력 문구 L882~900 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; 필수값·이메일 검사 L32~56 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L74 | 이메일은 서버가 형식도 검사한다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 입력 문구 L882~900 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; 필수값·이메일 검사 L32~56 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L75 | 「상태」를 고른다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L75 | 기본값은 ACTIVE이며 LOCKED·DISABLED·DELETED도 선택할 수 있다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L76 | 「등록」을 누른다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 / nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 등록 후 목록 갱신 L23~31 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L76 | 성공하면 창이 닫히고 목록이 다시 조회된다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 / nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 등록 후 목록 갱신 L23~31 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |
| L76 | 그만두려면 「취소」를 누른다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 등록 입력·저장 L76~86 L118~176 L190~311 / nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 등록 후 목록 갱신 L23~31 | 필수 입력·상태 기본값·서버 이메일 검사·생성 후 목록 무효화를 대조했다. |

### 계정 필수값이나 이메일 입력이 거부될 때

화면 선행 검사와 서버 첫 validation 문구를 대조했다. 새로고침은 대상을 다시 고르는 화면 조치이다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L86 | 사용자 등록·수정 창에서 다음 문구가 나오면 입력을 고친 뒤 「등록」 또는 「저장」을 누른다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·오류 표시 L118~176 L294~311 | 화면 선행 검사와 서버 첫 validation 문구를 대조했다. 새로고침은 대상을 다시 고르는 화면 조치이다. |
| L90 | \| 필수 입력값(회사, 부서, 이름, 아이디, 이메일)을 모두 채워주세요. \| 다섯 칸의 빈 값과 공백만 넣은 값을 채운다. \| | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·오류 표시 L118~176 L294~311 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 실패 문구 L860~888 | 화면 선행 검사와 서버 첫 validation 문구를 대조했다. 새로고침은 대상을 다시 고르는 화면 조치이다. |
| L91 | \| 등록 시 비밀번호는 필수입니다. \| 등록할 비밀번호를 입력한다. \| | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·오류 표시 L118~176 L294~311 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 실패 문구 L860~888 | 화면 선행 검사와 서버 첫 validation 문구를 대조했다. 새로고침은 대상을 다시 고르는 화면 조치이다. |
| L92 | \| email 형식이 올바르지 않습니다. \| 「이메일」을 올바른 이메일 형식으로 고친다. \| | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/UserUpsertRequest.java @ 985d17c604dd ; 이메일 검사 L48~50 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 첫 입력 오류 반환 L231~247 | 화면 선행 검사와 서버 첫 validation 문구를 대조했다. 새로고침은 대상을 다시 고르는 화면 조치이다. |
| L93 | \| 수정 대상 사용자를 확인할 수 없습니다. \| 창을 닫고 화면을 새로고침해 대상을 다시 고른다. \| | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·오류 표시 L118~176 L294~311 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 실패 문구 L860~888 | 화면 선행 검사와 서버 첫 validation 문구를 대조했다. 새로고침은 대상을 다시 고르는 화면 조치이다. |

### 다른 사용자의 부서와 계정 정보 수정하기

연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L104 | USER_INFO_WRITE·USER_WRITE 중 하나가 있는 운영 관리자가 「사용자 계정」에서 수정한다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L106 | 대상 행의 연필 단추를 누르거나 행을 두 번 누른다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 수정 진입 L96~105 L149~156 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L107 | 「사용자 수정」에서 「회사」·「부서」·「이름」·「이메일」·「상태」를 고친다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 문구 L882~900 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L107 | 「아이디」는 수정할 수 없다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 문구 L882~900 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L108 | 「저장」을 누른다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 / nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 수정 후 갱신 L38~48 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L108 | 성공하면 창이 닫히고 목록이 다시 조회된다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 / nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 수정 후 갱신 L38~48 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L110 | 본인 정보 저장이 부서 누락으로 막힌 사용자의 요청을 받으면 운영 관리자가 이 창의 「부서」를 채운다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 문구 L882~900 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 수정 L104~107 L156~167 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L110 | 「비밀번호 (미입력 시 유지)」를 비워 두면 기존 비밀번호를 유지한다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 문구 L882~900 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 수정 L104~107 L156~167 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |
| L110 | 새 값을 넣으면 함께 변경한다. | nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 저장·입력 L89~99 L157~174 L190~290 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정 문구 L882~900 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 수정 L104~107 L156~167 | 연필·행 더블클릭·아이디 disabled·부서 입력·비밀번호 null 유지·목록 갱신을 대조했다. |

### 관리자가 입력한 비밀번호가 거부될 때

신규·변경 정책 호출과 실제 오류 문자열을 대조했다. 프로필 조작 순서는 복제하지 않았다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L120 | 사용자 등록·수정에서 비밀번호를 입력하면 서버가 정책을 검사한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; 비밀번호 검사 L22~46 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; 신규·변경 검사 L50~75 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 검사 연결 L95~101 L143~153 L156~167 | 신규·변경 정책 호출과 실제 오류 문자열을 대조했다. 프로필 조작 순서는 복제하지 않았다. |
| L120 | 10~128자로 쓰고 공백 없이 소문자·대문자·숫자·특수문자 중 3종 이상을 포함한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; 비밀번호 검사 L22~46 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; 신규·변경 검사 L50~75 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 검사 연결 L95~101 L143~153 L156~167 | 신규·변경 정책 호출과 실제 오류 문자열을 대조했다. 프로필 조작 순서는 복제하지 않았다. |
| L120 | 기존 사용자 변경에는 최근 5회 재사용 제한도 적용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; 비밀번호 검사 L22~46 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; 신규·변경 검사 L50~75 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 검사 연결 L95~101 L143~153 L156~167 | 신규·변경 정책 호출과 실제 오류 문자열을 대조했다. 프로필 조작 순서는 복제하지 않았다. |
| L122 | 「비밀번호는 10자 이상 128자 이하여야 합니다.」나 「비밀번호에는 공백 문자를 사용할 수 없습니다.」가 나오면 해당 조건을 고친다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; 비밀번호 검사 L22~46 | 신규·변경 정책 호출과 실제 오류 문자열을 대조했다. 프로필 조작 순서는 복제하지 않았다. |
| L124 | 「비밀번호는 소문자/대문자/숫자/특수문자 중 3종 이상을 포함해야 합니다.」 또는 「최근 5회 내 사용한 비밀번호는 재사용할 수 없습니다.」가 나오면 다른 비밀번호를 입력한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/PasswordPolicyValidator.java @ 985d17c604dd ; 비밀번호 검사 L22~46 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPasswordPolicyService.java @ 985d17c604dd ; 신규·변경 검사 L50~75 | 신규·변경 정책 호출과 실제 오류 문자열을 대조했다. 프로필 조작 순서는 복제하지 않았다. |

### 기존 아이디나 그룹 코드로 다시 등록하지 않기

Facade→Assembler→JPA/MyBatis 저장 흐름에서 동일 정규화 아이디·그룹 코드의 기존 행 갱신을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L137 | 새 계정은 기존 아이디와 대소문자만 다른 아이디도 피한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminAssembler.java @ 985d17c604dd ; 아이디 정규화 L205~237 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 생성 저장 L95~101 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserInfoJpaStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L72~84 L273~277 / nori-tc-common/db/mybatis/tc-db-mybatis-common-schema/src/main/java/com/nori/tc/db/mybatis/common/store/user/TcUserInfoMybatisStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L60~102 | Facade→Assembler→JPA/MyBatis 저장 흐름에서 동일 정규화 아이디·그룹 코드의 기존 행 갱신을 대조했다. |
| L137 | 확인한 저장소 구현은 아이디의 앞뒤 공백을 제거하고 소문자로 비교해 기존 계정을 찾아 갱신한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminAssembler.java @ 985d17c604dd ; 아이디 정규화 L205~237 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 생성 저장 L95~101 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserInfoJpaStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L72~84 L273~277 / nori-tc-common/db/mybatis/tc-db-mybatis-common-schema/src/main/java/com/nori/tc/db/mybatis/common/store/user/TcUserInfoMybatisStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L60~102 | Facade→Assembler→JPA/MyBatis 저장 흐름에서 동일 정규화 아이디·그룹 코드의 기존 행 갱신을 대조했다. |
| L137 | 같은 아이디 등록으로 기존 계정 정보와 비밀번호가 바뀔 수 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminAssembler.java @ 985d17c604dd ; 아이디 정규화 L205~237 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 생성 저장 L95~101 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserInfoJpaStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L72~84 L273~277 / nori-tc-common/db/mybatis/tc-db-mybatis-common-schema/src/main/java/com/nori/tc/db/mybatis/common/store/user/TcUserInfoMybatisStore.java @ 6fe71f6b2da6 ; 기존 아이디 갱신 L60~102 | Facade→Assembler→JPA/MyBatis 저장 흐름에서 동일 정규화 아이디·그룹 코드의 기존 행 갱신을 대조했다. |
| L139 | 같은 그룹 코드로 「그룹 등록」을 실행해도 기존 그룹을 찾아 갱신할 수 있다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 생성 저장 L74~76 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserGroupJpaStore.java @ 6fe71f6b2da6 ; 기존 그룹 갱신 L63~79 L193~200 / nori-tc-common/db/mybatis/tc-db-mybatis-common-schema/src/main/java/com/nori/tc/db/mybatis/common/store/user/TcUserGroupMybatisStore.java @ 6fe71f6b2da6 ; 기존 그룹 갱신 L48~66 L165~170 | Facade→Assembler→JPA/MyBatis 저장 흐름에서 동일 정규화 아이디·그룹 코드의 기존 행 갱신을 대조했다. |
| L139 | 기존 계정·그룹을 고칠 때는 목록의 수정 단추를 사용한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 생성 저장 L74~76 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserGroupJpaStore.java @ 6fe71f6b2da6 ; 기존 그룹 갱신 L63~79 L193~200 / nori-tc-common/db/mybatis/tc-db-mybatis-common-schema/src/main/java/com/nori/tc/db/mybatis/common/store/user/TcUserGroupMybatisStore.java @ 6fe71f6b2da6 ; 기존 그룹 갱신 L48~66 L165~170 | Facade→Assembler→JPA/MyBatis 저장 흐름에서 동일 정규화 아이디·그룹 코드의 기존 행 갱신을 대조했다. |

### 사용자와 그룹 입력 길이 맞추기

기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L148 | 사용자·그룹 등록과 수정의 입력 길이는 저장소 기본 스키마 기준으로 다음 범위에 맞춘다. | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 / nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 칸 L190~245 / nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 칸 L136~169 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |
| L148 | 화면 입력 칸에는 이 최대 길이를 제한하는 처리가 없다. | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 / nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 칸 L190~245 / nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 칸 L136~169 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |
| L152 | \| 사용자 「회사」·「부서」·「이름」 각각 \| 100자 \| | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |
| L153 | \| 사용자 「아이디」 \| 50자 \| | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |
| L154 | \| 사용자 「이메일」 \| 255자 \| | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |
| L155 | \| 그룹 「그룹 코드」 \| 50자 \| | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |
| L156 | \| 그룹 「그룹 이름」 \| 100자 \| | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |
| L157 | \| 그룹 「설명」 \| 1000자 \| | nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 사용자·그룹 입력 길이 L974~992 L1024~1035 | 기본 PostgreSQL VARCHAR 길이와 화면 Input의 maxLength 부재를 대조했다. 현장 스키마 판정은 아니다. |

### 사용자 계정 삭제하기

휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L169 | USER_INFO_WRITE·USER_WRITE 중 하나가 있는 운영 관리자가 「사용자 계정」에서 삭제한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 삭제 L109~111 | 휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다. |
| L171 | 대상 아이디를 확인하고 행의 휴지통 단추를 누른다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 삭제 진입 L116~125 | 휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다. |
| L172 | 「사용자 삭제」의 대상과 되돌릴 수 없다는 안내를 읽고 「삭제」를 누른다. | nori-tc-ui-front/src/features/user/components/UserDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·실패 L29~63 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L864~871 | 휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다. |
| L172 | 중단하려면 「취소」를 누른다. | nori-tc-ui-front/src/features/user/components/UserDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·실패 L29~63 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L864~871 | 휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다. |
| L173 | 성공하면 창이 닫히고 사용자 목록이 갱신된다. | nori-tc-ui-front/src/features/user/components/UserDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·실패 L29~63 / nori-tc-ui-front/src/features/user/hooks/useUserMutations.ts @ 9fe69a75dcd3 ; 삭제 후 목록 갱신 L54~62 | 휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다. |
| L175 | 실패하면 창 안의 서버 문구 또는 「사용자 삭제에 실패했습니다.」를 확인한다. | nori-tc-ui-front/src/features/user/components/UserDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·실패 L29~63 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L864~871 | 휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다. |
| L175 | 대상 아이디·발생 시각·표시 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/user/components/UserDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인·실패 L29~63 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L864~871 | 휴지통·확인/취소·성공 닫기·실패 문구·목록 갱신을 대조했다. |

### 열쇠 단추로 다른 사용자의 비밀번호 초기화하기

열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L185 | USER_INFO_WRITE·USER_WRITE 중 하나가 있어야 한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 초기화 L143~153 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L185 | 관리자는 대상 사용자의 권한도 모두 보유해야 한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 초기화 L143~153 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L187 | 「사용자 계정」에서 대상 행의 열쇠 단추를 누른다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 열쇠 단추 L106~115 / nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L187 | 「비밀번호 초기화」를 열면 임시 비밀번호를 만들고 서버에 변경을 바로 요청한다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 열쇠 단추 L106~115 / nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L188 | 「서버에 비밀번호 변경을 적용하는 중...」이 사라지고 오류가 없는지 확인한다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 문구 L872~881 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L188 | 초기화했다는 안내 문장은 요청 중에도 표시된다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 문구 L872~881 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L189 | 「복사」를 눌러 「클립보드에 복사되었습니다.」를 확인하고 안전한 방법으로 본인에게 전달한다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 문구 L872~881 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L190 | 「닫기」를 누른다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 문구 L872~881 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |
| L190 | 닫은 창의 비밀번호는 다시 볼 수 없다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 자동 요청·복사·표시 L64~171 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 문구 L872~881 | 열자마자 요청·pending 표시·성공 안내의 무조건 렌더·복사·언마운트 상태 소멸을 대조했다. |

### 비밀번호 초기화 거부와 복사 실패 처리하기

서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L200 | 「본인보다 넓은 권한을 가진 사용자의 비밀번호는 재설정할 수 없습니다.」 뒤에 대상 번호가 나오면, 대상의 모든 권한을 보유한 관리자에게 초기화를 요청한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 초기화 대상 제한 L95~105 | 서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다. |
| L202 | 서버 문구 또는 「비밀번호 초기화에 실패했습니다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 오류·복사·재표시 L38~48 L71~111 L147~170 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 실패 문구 L878~881 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 초기화 대상 부재 L254~267 | 서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다. |
| L202 | 잠시 후 다시 시도해 주세요.」가 나오면 표시된 임시 값을 전달하지 않는다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 오류·복사·재표시 L38~48 L71~111 L147~170 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 실패 문구 L878~881 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 초기화 대상 부재 L254~267 | 서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다. |
| L202 | 대상 부재이면 목록을 다시 조회하고 권한 거부이면 권한을 확인한다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 오류·복사·재표시 L38~48 L71~111 L147~170 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 실패 문구 L878~881 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; 초기화 대상 부재 L254~267 | 서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다. |
| L204 | 「클립보드 복사에 실패했습니다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 오류·복사·재표시 L38~48 L71~111 L147~170 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 실패 문구 L878~881 | 서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다. |
| L204 | 직접 선택해 복사해 주세요.」이면 값을 직접 선택해 복사한다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 오류·복사·재표시 L38~48 L71~111 L147~170 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 실패 문구 L878~881 | 서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다. |
| L204 | 창을 다시 열면 새 초기화 요청이 발생하므로 같은 값을 조회하는 용도로 다시 열지 않는다. | nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 오류·복사·재표시 L38~48 L71~111 L147~170 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 초기화 실패 문구 L878~881 | 서버 대상 권한 검사·대상 부재·복사 실패·다시 열 때 재요청을 대조했다. |

### 권한 그룹 목록과 작업 단추 확인하기

그룹 열·아이콘 title·표시 순서·쪽 크기·집계 범위를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L214 | 기본 권한 기준으로 GROUP_WRITE가 있어야 「권한 그룹」의 목록을 조회한다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록·단추·쪽 이동 L24~24 L52~137 L195~224 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹 조회 기본 권한 L141~149 | 그룹 열·아이콘 title·표시 순서·쪽 크기·집계 범위를 대조했다. |
| L214 | 표는 「groupCode」·「groupName」·「description」·「isActive」·「updatedAt」·「액션」 순이다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록·단추·쪽 이동 L24~24 L52~137 L195~224 / nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹 조회 기본 권한 L141~149 | 그룹 열·아이콘 title·표시 순서·쪽 크기·집계 범위를 대조했다. |
| L216 | 「액션」에는 방패 「권한 관리」, 사람 여럿 「멤버 관리」, 연필 「그룹 수정」, 휴지통 「그룹 삭제」가 있다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록·단추·쪽 이동 L24~24 L52~137 L195~224 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 집계·단추 L107~116 L954~957 | 그룹 열·아이콘 title·표시 순서·쪽 크기·집계 범위를 대조했다. |
| L218 | 그룹도 한 쪽에 20개씩 보며 아래 「Prev」·「Next」로 이동한다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록·단추·쪽 이동 L24~24 L52~137 L195~224 / nori-tc-ui-front/src/features/group/components/GroupAdminPanel.tsx @ 9fe69a75dcd3 ; 집계 L34~37 L81~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 집계·단추 L107~116 L954~957 | 그룹 열·아이콘 title·표시 순서·쪽 크기·집계 범위를 대조했다. |
| L218 | 「전체 그룹」은 전체 건수이고 「현재 페이지 활성」·「현재 페이지 비활성」은 보이는 그룹만 센다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록·단추·쪽 이동 L24~24 L52~137 L195~224 / nori-tc-ui-front/src/features/group/components/GroupAdminPanel.tsx @ 9fe69a75dcd3 ; 집계 L34~37 L81~105 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 집계·단추 L107~116 L954~957 | 그룹 열·아이콘 title·표시 순서·쪽 크기·집계 범위를 대조했다. |

### 새 권한 그룹 만들기

필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L230 | GROUP_WRITE가 있는 운영 관리자가 기존 그룹과 다른 코드를 준비한다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L232 | 「권한 그룹」에서 「그룹 등록」을 누른다. | nori-tc-ui-front/src/features/group/components/GroupAdminPanel.tsx @ 9fe69a75dcd3 ; 등록 단추 L69~77 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 입력 문구 L911~922 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L233 | 필수인 「그룹 코드」·「그룹 이름」을 채운다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 입력 문구 L911~922 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/GroupUpsertRequest.java @ 985d17c604dd ; 필수값 L19~29 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L233 | 「설명」은 선택이다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 입력 문구 L911~922 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/GroupUpsertRequest.java @ 985d17c604dd ; 필수값 L19~29 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L234 | 「활성화 (사용자에게 부여 가능)」의 체크 상태를 정한다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 입력 문구 L911~922 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L234 | 처음에는 켜져 있다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 입력 문구 L911~922 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L235 | 「등록」을 누른다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 / nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 목록 갱신 L24~32 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L235 | 성공하면 창이 닫히고 그룹 목록이 갱신된다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 / nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 목록 갱신 L24~32 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L235 | 그만두려면 「취소」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 등록·입력·취소 L53~60 L88~122 L136~202 / nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 목록 갱신 L24~32 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |
| L237 | 새 그룹의 업무 권한은 그룹 행의 「권한 관리」에서 별도로 부여한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 생성 L74~76 | 필수값·선택 설명·활성 기본 true·등록/취소·목록 갱신을 대조했다. |

### 그룹 설명과 활성 표시 고치기

그룹 코드 disabled·활성 표시와 편입/권한 조회의 그룹 활성 차단 부재를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L250 | GROUP_WRITE가 있으면 그룹 행의 「그룹 수정」을 누르거나 행을 두 번 눌러 「그룹 이름」·「설명」·활성 체크를 고친 뒤 「저장」한다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 상태·수정 진입 L69~76 L116~125 L182~189 / nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 L63~69 L109~122 L138~179 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정·활성 문구 L911~921 | 그룹 코드 disabled·활성 표시와 편입/권한 조회의 그룹 활성 차단 부재를 대조했다. |
| L250 | 「그룹 코드」는 화면에서 고칠 수 없다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 상태·수정 진입 L69~76 L116~125 L182~189 / nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 수정 L63~69 L109~122 L138~179 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 수정·활성 문구 L911~921 | 그룹 코드 disabled·활성 표시와 편입/권한 조회의 그룹 활성 차단 부재를 대조했다. |
| L252 | 활성 체크를 끄면 표의 「isActive」는 INACTIVE로 나온다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 상태·수정 진입 L69~76 L116~125 L182~189 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 그룹 편입 검사 L113~119 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 권한 조회 L82~118 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 편입 시 권한 검사 L77~84 L108~115 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserGroupMemberJpaStore.java @ 6fe71f6b2da6 ; 편입 저장·입력 검사 L78~99 L306~315 | 그룹 코드 disabled·활성 표시와 편입/권한 조회의 그룹 활성 차단 부재를 대조했다. |
| L252 | 확인한 편입·권한 조회 경로는 그룹의 활성 여부로 차단하지 않는다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 상태·수정 진입 L69~76 L116~125 L182~189 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 그룹 편입 검사 L113~119 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 권한 조회 L82~118 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 편입 시 권한 검사 L77~84 L108~115 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserGroupMemberJpaStore.java @ 6fe71f6b2da6 ; 편입 저장·입력 검사 L78~99 L306~315 | 그룹 코드 disabled·활성 표시와 편입/권한 조회의 그룹 활성 차단 부재를 대조했다. |
| L252 | 사용자에게서 그룹 권한을 거두려면 「멤버 관리」에서 소속을 제거한다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 상태·수정 진입 L69~76 L116~125 L182~189 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 그룹 편입 검사 L113~119 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 권한 조회 L82~118 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 편입 시 권한 검사 L77~84 L108~115 / nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/user/TcUserGroupMemberJpaStore.java @ 6fe71f6b2da6 ; 편입 저장·입력 검사 L78~99 L306~315 | 그룹 코드 disabled·활성 표시와 편입/권한 조회의 그룹 활성 차단 부재를 대조했다. |

### 권한 그룹 삭제하기

삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L263 | GROUP_WRITE가 있는 운영 관리자가 삭제한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 삭제 L85~87 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 그룹 삭제 연결 규칙 L1040~1069 | 삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다. |
| L263 | 저장소의 기본 스키마에서는 그룹 삭제 시 그룹의 멤버 연결과 권한 연결도 함께 삭제된다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 삭제 L85~87 / nori-tc-exec/docs/db_table/create_all_table/postgres_create_all_table.sql @ f1d5e0a8e727 ; 그룹 삭제 연결 규칙 L1040~1069 | 삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다. |
| L265 | 「권한 그룹」에서 대상 행의 휴지통 「그룹 삭제」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인 L29~63 | 삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다. |
| L266 | 「그룹 삭제」 창에서 대상 코드와 되돌릴 수 없다는 안내를 확인하고 「삭제」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인 L29~63 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L907~907 L923~925 | 삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다. |
| L266 | 중단하려면 「취소」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인 L29~63 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L907~907 L923~925 | 삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다. |
| L267 | 성공하면 창이 닫히고 목록이 갱신된다. | nori-tc-ui-front/src/features/group/components/GroupDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인 L29~63 / nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 삭제 후 갱신 L53~61 | 삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다. |
| L269 | 실패하면 창 안의 서버 문구 또는 「그룹 삭제에 실패했습니다.」와 대상 코드·시각을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/group/components/GroupDeleteConfirmDialog.tsx @ 9fe69a75dcd3 ; 삭제 확인 L29~63 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 삭제 문구 L907~907 L923~925 | 삭제 확인·갱신·실패 표시와 기본 스키마의 두 ON DELETE CASCADE를 대조했다. |

### 그룹 필수값이나 저장이 거부될 때

필수 검사·대상 null 검사·서버 NOT_FOUND 문구와 저장 오류 우선순위를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L278 | 「필수 입력값(그룹 코드, 그룹 이름)을 모두 채워주세요.」이면 두 칸을 채운다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 오류 L88~123 L184~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 오류 L904~916 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 수정 대상 부재 L140~155 | 필수 검사·대상 null 검사·서버 NOT_FOUND 문구와 저장 오류 우선순위를 대조했다. |
| L278 | 「수정 대상 그룹을 확인할 수 없습니다.」 또는 「수정할 그룹을 찾을 수 없습니다.」이면 창을 닫고 화면을 새로고침해 대상을 다시 고른다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 오류 L88~123 L184~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 오류 L904~916 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 수정 대상 부재 L140~155 | 필수 검사·대상 null 검사·서버 NOT_FOUND 문구와 저장 오류 우선순위를 대조했다. |
| L280 | 생성·저장 실패에는 서버 문구가 우선 표시된다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 오류 L88~123 L184~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 오류 L904~916 | 필수 검사·대상 null 검사·서버 NOT_FOUND 문구와 저장 오류 우선순위를 대조했다. |
| L280 | 서버 문구가 없으면 「그룹 생성에 실패했습니다.」·「그룹 정보 저장에 실패했습니다.」·「저장에 실패했습니다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 오류 L88~123 L184~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 오류 L904~916 | 필수 검사·대상 null 검사·서버 NOT_FOUND 문구와 저장 오류 우선순위를 대조했다. |
| L280 | 잠시 후 다시 시도해 주세요.」가 나올 수 있다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 오류 L88~123 L184~202 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 오류 L904~916 | 필수 검사·대상 null 검사·서버 NOT_FOUND 문구와 저장 오류 우선순위를 대조했다. |
| L282 | 입력을 고쳐도 실패하면 그룹 코드·발생 시각·누른 단추·표시 문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/group/components/GroupUpsertModal.tsx @ 9fe69a75dcd3 ; 입력 검사·저장 오류 L88~123 L184~202 | 필수 검사·대상 null 검사·서버 NOT_FOUND 문구와 저장 오류 우선순위를 대조했다. |

### 그룹에 사용자 추가하기

검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L293 | GROUP_WRITE로 그룹·멤버를 조회하고 USER_INFO_WRITE·USER_WRITE 중 하나로 멤버를 추가한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 편입 L113~119 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L293 | 관리자는 그룹의 권한도 모두 보유해야 한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 편입 L113~119 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L295 | 「권한 그룹」의 대상 행에서 사람 여럿 모양 「멤버 관리」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 멤버 관리 진입 L105~115 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 문구 L929~940 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L296 | 「그룹 멤버 추가」의 「userId 또는 이름으로 검색」으로 찾는다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 문구 L929~940 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L296 | 검색은 대소문자를 구별하지 않는다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 문구 L929~940 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L297 | 추가할 사용자 체크를 켜고 「추가」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L298 | 「추가 …건 / 실패 …건」을 확인한다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 문구 L929~940 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L298 | 실패가 있으면 「첫 오류」도 읽는다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 문구 L929~940 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L300 | 이미 소속된 사용자는 체크가 켜져 있고 체크를 바꿀 수 없다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |
| L300 | 선택한 사용자가 없으면 「추가」가 비활성화된다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 검색·추가·결과 L67~83 L121~162 L176~277 | 검색 조건·기존 체크 disabled·선택 시 추가·allSettled 결과·첫 오류를 대조했다. |

### 그룹에서 사용자 소속 제거하기

제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L312 | GROUP_WRITE로 멤버 목록을 조회하고 USER_INFO_WRITE·USER_WRITE 중 하나로 소속을 제거한다. | nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 제거 요청 L324~345 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/UserAdminFacade.java @ 985d17c604dd ; 소속 제거 L122~124 | 제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다. |
| L314 | 「권한 그룹」 행의 「멤버 관리」를 열고 기존 멤버 줄의 「멤버 제거」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 제거·결과 표시 L100~111 L199~234 / nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 제거 후 갱신 L84~93 | 제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다. |
| L314 | 이 단추는 제거 요청을 바로 보낸다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 제거·결과 표시 L100~111 L199~234 / nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 제거 후 갱신 L84~93 | 제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다. |
| L314 | 성공하면 멤버 목록과 사용자 목록이 다시 조회되고 기존 멤버 표시가 풀린다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 제거·결과 표시 L100~111 L199~234 / nori-tc-ui-front/src/features/group/hooks/useGroupMutations.ts @ 9fe69a75dcd3 ; 제거 후 갱신 L84~93 | 제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다. |
| L314 | 다른 소속 그룹에도 같은 권한이 있으면 사용자는 그 권한을 계속 가진다. | nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaPermissionPort.java @ 985d17c604dd ; 그룹별 권한 합집합 L82~118 | 여러 소속 그룹에서 모은 활성 권한을 Set으로 반환하므로 다른 그룹의 같은 권한은 남는다. |
| L316 | 실패하면 창 안의 서버 문구 또는 「그룹 멤버 제거에 실패했습니다.」를 확인한다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 제거·결과 표시 L100~111 L199~234 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 제거 문구 L909~909 L935~935 | 제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다. |
| L316 | 대상 아이디·그룹 코드·발생 시각·문구를 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 제거·결과 표시 L100~111 L199~234 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 제거 문구 L909~909 L935~935 | 제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다. |
| L316 | 끝나면 「닫기」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 제거·결과 표시 L100~111 L199~234 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 제거 문구 L909~909 L935~935 | 제거 단추 즉시 요청·조회 무효화·오류 인라인·닫기를 대조했다. |

### 그룹 멤버 추가가 거부되거나 사용자가 안 보일 때

편입 권한 가드·선택/대상 검사·빈 목록·멤버 조회 offset=0 limit=500을 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L326 | 「본인이 보유하지 않은 권한을 가진 그룹에는 사용자를 편입할 수 없습니다.」 뒤에 그룹 번호가 나오면 그룹의 모든 권한을 보유한 관리자에게 추가를 요청한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 편입 제한 L77~84 | 편입 권한 가드·선택/대상 검사·빈 목록·멤버 조회 offset=0 limit=500을 대조했다. |
| L328 | 「추가할 사용자를 선택해 주세요.」이면 신규 사용자를 체크한다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 대상·선택 검사·실패·빈 목록 L121~158 L189~197 L241~262 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 오류 L903~909 L934~940 | 편입 권한 가드·선택/대상 검사·빈 목록·멤버 조회 offset=0 limit=500을 대조했다. |
| L328 | 「대상 그룹을 확인할 수 없습니다.」이면 창을 닫고 그룹을 다시 고른다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 대상·선택 검사·실패·빈 목록 L121~158 L189~197 L241~262 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 오류 L903~909 L934~940 | 편입 권한 가드·선택/대상 검사·빈 목록·멤버 조회 offset=0 limit=500을 대조했다. |
| L328 | 추가 실패의 첫 오류는 서버 문구 또는 「그룹 멤버 추가에 실패했습니다.」이다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 대상·선택 검사·실패·빈 목록 L121~158 L189~197 L241~262 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 오류 L903~909 L934~940 | 편입 권한 가드·선택/대상 검사·빈 목록·멤버 조회 offset=0 limit=500을 대조했다. |
| L330 | 「표시할 사용자가 없습니다.」이면 검색어를 지워 확인한다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 대상·선택 검사·실패·빈 목록 L121~158 L189~197 L241~262 / nori-tc-ui-front/src/features/group/hooks/useGroups.ts @ 9fe69a75dcd3 ; 멤버 조회 범위 L34~39 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 오류 L903~909 L934~940 | 편입 권한 가드·선택/대상 검사·빈 목록·멤버 조회 offset=0 limit=500을 대조했다. |
| L330 | 멤버 조회는 처음 500건만 사용하므로 큰 그룹의 표시 누락은 아이디·그룹 코드·시각과 함께 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/group/components/GroupAddMembersModal.tsx @ 9fe69a75dcd3 ; 대상·선택 검사·실패·빈 목록 L121~158 L189~197 L241~262 / nori-tc-ui-front/src/features/group/hooks/useGroups.ts @ 9fe69a75dcd3 ; 멤버 조회 범위 L34~39 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 멤버 오류 L903~909 L934~940 | 편입 권한 가드·선택/대상 검사·빈 목록·멤버 조회 offset=0 limit=500을 대조했다. |

### 그룹에 줄 권한 찾아보기

활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L340 | 기본 권한 기준으로 GROUP_WRITE와 PERMISSION_WRITE가 있어야 그룹의 보유 권한과 후보 목록을 함께 조회한다. | nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹·권한 기본 권한 L141~149 L258~266 | 활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다. |
| L342 | 「권한 그룹」 행의 방패 「권한 관리」를 누른다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 활성 권한·검색·묶음·표시 L78~122 L205~272 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 문구 L941~965 | 활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다. |
| L342 | 「권한 코드 / 이름 / 경로로 검색」은 실제로 권한 코드·권한 이름·설명을 대소문자 구별 없이 검색한다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 활성 권한·검색·묶음·표시 L78~122 L205~272 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 문구 L941~965 | 활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다. |
| L342 | 활성 권한만 후보로 나온다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 활성 권한·검색·묶음·표시 L78~122 L205~272 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 문구 L941~965 | 활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다. |
| L344 | 목록의 묶음은 「설비 (EQP)」·「모델 (MODEL)」·「배포 (DEPLOY)」·「장애 / DLQ」·「LIVE MAP」·「사용자 · 그룹 · 권한 관리」·「공통 기능」·「기타」 순이며 항목이 있는 묶음만 보인다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 활성 권한·검색·묶음·표시 L78~122 L205~272 / nori-tc-ui-front/src/features/group/lib/permission-category.ts @ 9fe69a75dcd3 ; 묶음 순서 L46~55 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 권한 문구 L941~965 | 활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다. |
| L346 | 권한에는 이름·코드·허용 방식이 표시되고, 설명이 있으면 함께 나온다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 활성 권한·검색·묶음·표시 L78~122 L205~272 | 활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다. |
| L346 | 아래쪽 권한은 창 안 목록을 내려서 찾는다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 활성 권한·검색·묶음·표시 L78~122 L205~272 | 활성 후보만 필터·코드/이름/설명 검색·빈 카테고리 제거·대문자 스타일·허용 방식 표시를 대조했다. |

### 그룹 권한을 부여하거나 회수하고 저장하기

체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L356 | GROUP_WRITE로 그룹 권한을 변경하며, 부여할 권한은 관리자 본인도 보유해야 한다. | nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 부여·회수 요청 L400~436 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 부여·회수 L101~114 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 부여 제한 L57~66 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L356 | 후보 조회에는 PERMISSION_WRITE도 필요하다. | nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 부여·회수 요청 L400~436 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/application/admin/GroupAdminFacade.java @ 985d17c604dd ; 부여·회수 L101~114 / nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 부여 제한 L57~66 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L358 | 「그룹 권한 관리」에서 줄 권한은 체크를 켜고 거둘 권한은 끈다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L358 | 체크 변경은 아직 서버에 저장되지 않는다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L359 | 「저장」을 누른다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L359 | 바뀐 것이 없으면 「저장」은 비활성화된다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L359 | 모두 성공하면 창이 닫힌다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L360 | 「취소」나 창 닫기는 아직 저장하지 않은 변경을 버린다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L362 | 오른쪽 위 「… / … 권한 보유」는 미저장 체크도 포함한다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |
| L362 | 저장은 권한별 요청으로 처리하므로 일부만 성공할 수 있다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 체크·보유 수·저장·취소 L67~75 L115~191 L290~307 | 체크 draft·변경 없음 비활성·권한별 요청·전건 성공 닫기·취소 폐기·미저장 포함 집계를 대조했다. |

### 권한 저장이 일부 실패하거나 부여가 거부될 때

권한 부여 가드·실패 변경만 보존·성공분 유지·서버 우선 오류·재저장 범위를 대조했다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L372 | 「본인이 보유하지 않은 권한은 부여할 수 없습니다.」 뒤에 권한 코드가 나오면 해당 권한을 보유한 관리자에게 부여를 요청한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 부여 거부 L57~66 | 권한 부여 가드·실패 변경만 보존·성공분 유지·서버 우선 오류·재저장 범위를 대조했다. |
| L374 | 「저장」 후 일부 실패하면 창이 열려 있고 실패한 변경만 미저장 상태로 남는다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 일부 실패 보존 L143~183 / nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 실패 대체 문구 L400~436 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 문구 L910~910 L949~953 | 권한 부여 가드·실패 변경만 보존·성공분 유지·서버 우선 오류·재저장 범위를 대조했다. |
| L374 | 서버 문구 또는 「권한 부여에 실패했습니다.」·「권한 회수에 실패했습니다.」가 표시된다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 일부 실패 보존 L143~183 / nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 실패 대체 문구 L400~436 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 문구 L910~910 L949~953 | 권한 부여 가드·실패 변경만 보존·성공분 유지·서버 우선 오류·재저장 범위를 대조했다. |
| L374 | 원인을 해결한 뒤 「저장」하면 남은 변경을 요청한다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 일부 실패 보존 L143~183 / nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 권한 실패 대체 문구 L400~436 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 실패 문구 L910~910 L949~953 | 권한 부여 가드·실패 변경만 보존·성공분 유지·서버 우선 오류·재저장 범위를 대조했다. |
| L376 | 이때 「취소」해도 이미 성공한 변경은 되돌아가지 않는다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 일부 실패 보존 L143~183 | 권한 부여 가드·실패 변경만 보존·성공분 유지·서버 우선 오류·재저장 범위를 대조했다. |
| L376 | 그룹 코드·권한 코드·시각·표시 문구를 기록해 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 일부 실패 보존 L143~183 | 권한 부여 가드·실패 변경만 보존·성공분 유지·서버 우선 오류·재저장 범위를 대조했다. |

### 목록이나 권한 후보가 비어 있을 때 확인하기

목록 기본 빈 배열과 query 오류의 렌더 부재를 끝까지 확인했다. 검색 해제/재조회·인계는 화면 조치이다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L387 | 「표시할 사용자가 없습니다.」·「표시할 그룹이 없습니다.」가 나와도 목록에는 조회 오류를 별도로 표시하는 영역이나 「재시도」 단추가 없다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록 기본값·빈 표 L35~42 L139~147 / nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록 기본값·빈 표 L43~50 L174~181 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·조회 실패 문구 L105~115 L947~951 | 목록 기본 빈 배열과 query 오류의 렌더 부재를 끝까지 확인했다. 검색 해제/재조회·인계는 화면 조치이다. |
| L387 | 화면을 새로고침해도 비어 있으면 대상 화면·시각을 운영 담당자에게 전달한다. | nori-tc-ui-front/src/features/user/components/UserListTable.tsx @ 9fe69a75dcd3 ; 목록 기본값·빈 표 L35~42 L139~147 / nori-tc-ui-front/src/features/group/components/GroupListTable.tsx @ 9fe69a75dcd3 ; 목록 기본값·빈 표 L43~50 L174~181 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·조회 실패 문구 L105~115 L947~951 | 목록 기본 빈 배열과 query 오류의 렌더 부재를 끝까지 확인했다. 검색 해제/재조회·인계는 화면 조치이다. |
| L389 | 권한 창의 「표시할 권한이 없습니다.」이면 검색어를 지운다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 조회·빈 목록·오류 표시 L43~44 L78~91 L229~237 L280~287 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·조회 실패 문구 L105~115 L947~951 / nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 조회 오류 L347~398 | 목록 기본 빈 배열과 query 오류의 렌더 부재를 끝까지 확인했다. 검색 해제/재조회·인계는 화면 조치이다. |
| L389 | 권한 조회 실패도 이 창의 오류 영역에는 직접 표시되지 않는다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 조회·빈 목록·오류 표시 L43~44 L78~91 L229~237 L280~287 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·조회 실패 문구 L105~115 L947~951 / nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 조회 오류 L347~398 | 목록 기본 빈 배열과 query 오류의 렌더 부재를 끝까지 확인했다. 검색 해제/재조회·인계는 화면 조치이다. |
| L389 | 창을 다시 열어도 같으면 그룹 코드와 시각을 전달해 GROUP_WRITE·PERMISSION_WRITE 부여 여부를 확인한다. | nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 조회·빈 목록·오류 표시 L43~44 L78~91 L229~237 L280~287 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 빈 목록·조회 실패 문구 L105~115 L947~951 / nori-tc-ui-front/src/features/group/api/group.api.ts @ 9fe69a75dcd3 ; 조회 오류 L347~398 | 목록 기본 빈 배열과 query 오류의 렌더 부재를 끝까지 확인했다. 검색 해제/재조회·인계는 화면 조치이다. |

### 저장 오류와 작업 권한 거부를 구별해 전달하기

서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다.

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L401 | 사용자 등록·수정 실패는 서버가 보낸 문구를 우선 오류 알림과 창 안에 표시한다. | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 우선 L19~40 L49~79 / nori-tc-ui-front/src/shared/lib/notify.ts @ 9fe69a75dcd3 ; 오류 알림 L14~17 / nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 저장 오류 L175~176 L294~298 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 오류 L862~888 | 서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다. |
| L401 | 서버 문구가 없으면 「사용자 생성에 실패했습니다.」·「사용자 정보 저장에 실패했습니다.」 같은 작업별 문구가 나온다. | nori-tc-ui-front/src/shared/lib/api-error.ts @ 9fe69a75dcd3 ; 서버 문구 우선 L19~40 L49~79 / nori-tc-ui-front/src/shared/lib/notify.ts @ 9fe69a75dcd3 ; 오류 알림 L14~17 / nori-tc-ui-front/src/features/user/components/UserUpsertModal.tsx @ 9fe69a75dcd3 ; 저장 오류 L175~176 L294~298 / nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 사용자 오류 L862~888 | 서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다. |
| L403 | 필수값·이메일·비밀번호 조건 오류는 입력을 고친다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·충돌·대상 권한 거부 L89~115 L190~198 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaUserCrudPort.java @ 985d17c604dd ; 저장 충돌 L88~100 | 서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다. |
| L403 | 「사용자 저장 중 충돌이 발생했습니다.」이면 아이디와 이메일의 기존 사용 여부를 운영 담당자에게 확인한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·충돌·대상 권한 거부 L89~115 L190~198 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaUserCrudPort.java @ 985d17c604dd ; 저장 충돌 L88~100 | 서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다. |
| L403 | 일반 생성 실패만으로 아이디 중복이라고 단정하지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·충돌·대상 권한 거부 L89~115 L190~198 / nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JpaUserCrudPort.java @ 985d17c604dd ; 저장 충돌 L88~100 | 서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다. |
| L405 | 권한 부족이나 대상 권한 범위 거부이면 입력을 바꾸어 반복하지 않고 권한을 가진 관리자에게 요청한다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·충돌·대상 권한 거부 L89~115 L190~198 | 서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다. |
| L405 | 화면·대상 아이디 또는 그룹 코드·발생 시각·실행한 조작·표시 문구를 전달하며 비밀번호는 전달 기록에 넣지 않는다. | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/UiApiExceptionHandler.java @ 985d17c604dd ; 입력·충돌·대상 권한 거부 L89~115 L190~198 | 서버 오류 문자열 보존→알림→인라인 표시와 입력/충돌/권한 오류의 구분을 대조했다. 인계에는 자격값을 제외한다. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼 | 코드 대조 | 검색 원문 반영 |
|---|---|---|---|
| 이메일 형식 | U §9.1.2 L2304 자유 입력 | UserUpsertRequest L48~50 @Email, 입력 오류가 반환됨 | 형식 검사와 실제 거부 문구를 적음 |
| 중복 아이디 등록 | U §9.1.4 L2328 아이디 중복이면 생성 실패 | UserAdminFacade create→JPA resolveEntity L273~277 및 MyBatis L86~101은 기존 아이디 갱신 | 기존 계정·비밀번호 갱신 가능성을 알리고 새 아이디 사용 |
| 중복 그룹 코드 등록 | U §9.3.4 L2410 코드가 이미 있으면 생성 실패 | GroupAdminFacade create→JPA resolveEntity L193~200 및 MyBatis resolveGroupId L165~170은 기존 그룹 갱신 | 기존 코드로 재등록하지 않고 목록의 수정 사용 |
| 그룹 활성 체크 | U §9.3.2 L2395 끄면 새 사용자에게 줄 수 없음 | 편입 가드는 권한 집합만 검사하고 저장·권한 조회도 그룹 isActive로 차단하지 않음 | INACTIVE 표시와 멤버 제거 절차를 구별 |
| 그룹 삭제 실패 | U §9.3.4 L2411 소속 사용자를 먼저 뺌 | 삭제 Facade는 그룹 삭제 호출, 기본 PostgreSQL 스키마 L1050~1051·1066~1067은 연결 CASCADE | 기본 스키마 조건으로 연결 삭제를 적고 실패는 문구 인계 |
| 초기화 성공 안내 | U §9.2.3 L2352~2356 성공 안내로 설명 | UserPasswordResetModal L119~125는 요청 상태와 무관하게 성공형 설명 표시, pending·error는 별도 | 적용 중 표시 소멸과 오류 부재를 확인하고 전달 |
| 권한 조회 실패 표시 | U §9.5.4 L2506~2507 조회 실패 문구 표시 | GroupPermissionsModal은 조회 query 오류를 errorMessage로 옮기지 않음 | 빈 표시·닫고 다시 열기·권한 확인 인계를 적음 |
| 권한 항목 배치 | U §9.5.3 L2495~2497 이름·코드·설명을 세 줄로 설명 | GroupPermissionsModal L257~272는 이름·코드가 같은 줄, 설명은 선택, 허용 방식은 옆 | 줄 수를 고정하지 않고 표시 요소만 적음 |

화면 안내 자체의 불일치도 확인했다. `ko.json` L943은 체크 즉시 반영이라 안내하지만 `GroupPermissionsModal` L128~183은 미저장 변경 후 저장한다. U §9.5.1도 이 차이를 이미 설명하므로 매뉴얼의 올바른 저장 설명을 따랐다. 잘못된 안내 문장은 검색 원문에 복제하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현장 계정·그룹별 실제 권한, 현장 스키마·배포 판본 | 로컬 소스·기본 시드만 확인했으며 서버에 접속하지 않음 | 운영 관리자·배포 담당 |
| 그룹 변경 직후 모든 로그인 사용자의 권한이 즉시 바뀌고 새로고침만으로 메뉴가 갱신된다는 보장 | U §9.4.3 L2442~2443만으로 인증 캐시·배포 상태의 실제 반영 시점을 확정할 수 없어 미수록 | UI 백엔드 인증·권한 담당 |
| 모든 생성·삭제 실패를 중복 아이디·중복 코드·멤버 잔존으로 확정 | 일반 실패 문구는 여러 원인에서 사용되고 저장소는 중복 등록을 갱신하기도 함 | UI 백엔드 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 소유 사실 | 이 문서와의 경계 |
|---|---|---|
| 00.rag/user/login-and-screen-basics.md | 로그인·아이디 기억·로그아웃·분실 요청·본인 프로필 수정·본인 비밀번호 변경·공통 메뉴/표/예전 주소 | 본인 조작 순서는 반복하지 않고 관리자의 부서 수정만 설명. 비밀번호 정책 수치는 관리자 입력에 필요한 최소 조건 |
| 00.rag/user/permission-code-reference.md | 전체 업무 권한 코드의 정의·메뉴별 대응 | 이 문서는 계정·그룹 작업의 기본 권한 연결만 다룸 |
| 00.rag/user/error-message-and-code-reference.md | 전체 오류 메시지·오류 코드의 참조 | 이 작업의 화면·서버 거부 문구와 화면 조치만 수록 |
| 00.rag/user/login-permission-and-screen-troubleshooting.md | 공통 로그인·인가·화면 오류 진단 | 계정 작업 실패의 필요한 인계만 수록 |
| 00.rag/user/state-reference.md | 상태값의 일반 의미 | 상태 선택지와 집계 조건만 쓰고 상태별 의미 풀이 제외 |

다른 문서에 대해 수정이 필요한 확정 결함은 발견하지 않았다. 시스템 내부 조치와 관리 전용 문서 경로는 검색 원문에 넣지 않았다.

## 검사 결과

- 검사 명령: `bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/user/user-group-and-permission-management.md`
- 도구: Git 2.52.0.windows.1, Python 3.12.10, GNU Bash 5.2.37(1)-release, Temurin OpenJDK 21.0.9+10-LTS.
- 기계 검사 3회 완료: 첫 작성본 exit 0·24절·95자식·위반 0, 중간본 exit 0·25절·101자식·위반 0, 마지막 검색 원문 편집 후 exit 0·25절·101자식·위반 0. CROSS VIOLATION·MASK 출력 없음. 최종 최대 자식 150자·부모 434자.
- 사실 대조 기록: 본문 문장·표 자료 행 179개 행. 인용 원천 47개 고유 파일 clean 확인.
- 제품 빌드·단위 시험·브라우저 실행·독립 공식 검증·현장 적용은 실행하지 않음. 문서 작성 자체 검사와 코드 대조 결과이다.
- 검사기 소스·설정은 수정하지 않았고 임시 파일은 만들지 않았다. 검사 출력은 아래에 보존한다.

마지막 검사 출력 10줄:

```text
§ L378-389 [24] 목록이나 권한 후보가 비어 있을 때 확인하기   parent=261 children=2 sources=5
    L387-387 문단 120  | 「표시할 사용자가 없습니다.」·「표시할 그룹이 없습니다.」가 나와도 목록에는 조회 오류를 별도로 표시하는 영역이나 「재시도」 단추가 없다. 화면을 새로고침해도 비어 있으면 대상 화면·시각을 운영 담당자에게 전달한다.
    L389-389 문단 140  | 권한 창의 「표시할 권한이 없습니다.」이면 검색어를 지운다. 권한 조회 실패도 이 창의 오류 영역에는 직접 표시되지 않는다. 창을 다시 열어도 같으면 그룹 코드와 시각을 전달해 GROUP_WRITE·PERMISSION_WRITE 부여 여부를 확인한다.
§ L391-405 [25] 저장 오류와 작업 권한 거부를 구별해 전달하기   parent=354 children=3 sources=6
    L401-401 문단 111  | 사용자 등록·수정 실패는 서버가 보낸 문구를 우선 오류 알림과 창 안에 표시한다. 서버 문구가 없으면 「사용자 생성에 실패했습니다.」·「사용자 정보 저장에 실패했습니다.」 같은 작업별 문구가 나온다.
    L403-403 문단 120  | 필수값·이메일·비밀번호 조건 오류는 입력을 고친다. 「사용자 저장 중 충돌이 발생했습니다.」이면 아이디와 이메일의 기존 사용 여부를 운영 담당자에게 확인한다. 일반 생성 실패만으로 아이디 중복이라고 단정하지 않는다.
    L405-405 문단 121  | 권한 부족이나 대상 권한 범위 거부이면 입력을 바꾸어 반복하지 않고 권한을 가진 관리자에게 요청한다. 화면·대상 아이디 또는 그룹 코드·발생 시각·실행한 조작·표시 문구를 전달하며 비밀번호는 전달 기록에 넣지 않는다.
SUMMARY sections=25 children=101 maxChild=150 maxParent=434
CORPUS docs=13/53 sections=237 part={user=13} type={사용 절차=13}
TOTAL files=13 targets=1 violations=0
```

사람 검사: 40절 미만이므로 25절 전부를 읽었다. 다음 표의 네 축은 각각 제목만으로 질문 식별, 원천 일치, 독립 답, 중복 소유 여부이다. 작업 권한·비밀번호 정책의 최소 선행조건 외에 다른 문서의 절차를 복제하지 않았다.

| 절 | 질문 식별 | 원천 일치 | 독립 답 | 중복 소유 |
|---|---|---|---|---|
| 사용자 계정과 권한 그룹 화면 열기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 계정 작업과 그룹 작업에 필요한 권한 확인하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 사용자 목록에서 계정과 그룹 소속 확인하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 새 사용자 계정 등록하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 계정 필수값이나 이메일 입력이 거부될 때 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 다른 사용자의 부서와 계정 정보 수정하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 관리자가 입력한 비밀번호가 거부될 때 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 기존 아이디나 그룹 코드로 다시 등록하지 않기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 사용자와 그룹 입력 길이 맞추기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 사용자 계정 삭제하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 열쇠 단추로 다른 사용자의 비밀번호 초기화하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 비밀번호 초기화 거부와 복사 실패 처리하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 권한 그룹 목록과 작업 단추 확인하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 새 권한 그룹 만들기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 그룹 설명과 활성 표시 고치기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 권한 그룹 삭제하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 그룹 필수값이나 저장이 거부될 때 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 그룹에 사용자 추가하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 그룹에서 사용자 소속 제거하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 그룹 멤버 추가가 거부되거나 사용자가 안 보일 때 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 그룹에 줄 권한 찾아보기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 그룹 권한을 부여하거나 회수하고 저장하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 권한 저장이 일부 실패하거나 부여가 거부될 때 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 목록이나 권한 후보가 비어 있을 때 확인하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |
| 저장 오류와 작업 권한 거부를 구별해 전달하기 | 적합 | 대조 완료 | 조건·조작·결과 또는 인계 포함 | 고유 작업·실패 분기로 분리 |

- 제목 정확 중복 0, 식별자만 바꾼 틀 제목 0. 각 제목은 화면 진입·목록·입력·저장·권한·실패의 실제 질문을 구분한다.
- 본문은 공통 사실의 소유 위치를 정했다. 앞선 로그인 문서의 로그인·프로필·자기 비밀번호 조작은 반복하지 않았다. 동일 자식 본문은 검사기에서도 0건이다.
- 빈 틀 문장·코드 이름 토막 뜻풀이·잘린 문장·오답 사례·admin 구획 안내를 남기지 않았다. 숫자·화면 문구는 렌더와 한국어 문구 및 처리 코드에서 확인한 범위만 썼다.
- 정책 미충족은 입력 수정, 작업 권한 거부는 권한 보유 관리자 요청, 조회 오류 비표시는 재조회/증상 인계로 구별했다. 사용자 문서에 내부 설정 변경 절차는 없다.
- mask-v1 적용 후 의미가 사라지는 MASK 출력 없음. 표 머리글은 이름·자격값 형식을 피했다. 비밀값·실제 계정·연락처·접속 정보는 없다.
- 부분 저장의 취소는 성공분을 되돌리지 않는 점, 초기화 안내가 요청 중에도 뜨는 점, 그룹 활성 체크가 권한 회수 수단이 아닌 점을 다시 읽어 확인했다.
- 작성 중 자체 대조로 선택 미존재와 신규 선택 미존재 조건을 구별하고, 오류 알림 공통 문장의 범위를 사용자 등록·수정으로 좁혔다. 최종 검색 원문 검사에서 미해소 결함 0건이다.
- 하위 Agent·독립 AI 호출 0회. 현재 세션의 provider 사용량·비용과 전체 wall time은 측정되지 않아 미확인이다. 외부 서비스 호출은 하지 않았다.
- 일회성 파일·프로세스·브라우저를 만들지 않았다. work 폴더에 이 세션이 정리할 소유 임시 파일은 없다.

최종 검사 호출 회수 주의: 앞선 두 PowerShell 경유 검사와 명시적 Git Bash 경로 검사 1회는 exit 0을 회수했다. 별도의 Python subprocess에서 bare `bash`로 출력 수집을 시도한 1회는 UTF-8 디코딩 오류(`UnicodeDecodeError: byte 0xc5 in position 1`)와 `NoneType.splitlines`로 수집기 exit 1이었다. 그 호출의 checker 완료 여부는 미확인으로 두며 PASS에 합산하지 않는다. 이후 확인된 Git Bash 절대경로와 바이트 수집으로 최종 검사 결과를 회수했다. 해당 실패 수집기는 산출물을 변경하지 않았다.

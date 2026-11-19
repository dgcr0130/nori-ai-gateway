# 일상 점검과 운영 로그 조회 작성 기록

## 대상

- objectKey: `00.rag/admin/daily-checks-and-log-inspection.md`
- 검색 원문: `rag-documents/nori-tc/00.rag/admin/daily-checks-and-log-inspection.md`
- 구획: admin, 필요한 권한 전부 `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 운영
- 검색 원문 SHA-256: `2fe518b71cdd85ff956b4cafb5f60535791588597640d6ba097c09c4c7cb1096`
- 검색 원문 줄 수: 261, H1 1개, H2 16개, 직렬화 자식 47개
- 확인일: 2026-11-19 한국 시간
- 범위: O §8.1·§8.3·§8.4. 운영 담당의 일상 조회와 판단 기준만 작성했다. 업로드·접속·배포·설정 변경·제품 시험은 실행하지 않았다.
- 작성·자체검사만 완료했다. 메인 세션의 원천 대조와 독립 검증을 대신하지 않는다. 독립 Agent 호출은 0회이며 provider 사용량·비용은 확인하지 않았다.

## 원천 판본

작성 규칙 `docs/rag-document-writing-rules.md`와 목록 `docs/tc-rag-document-list.md` 전체를 읽었다. 처음 PowerShell 기본 인코딩으로 읽은 한글이 깨져 UTF-8로 다시 읽었고, 출력이 잘린 구간은 나누어 읽었다. 관련 기존 검색 원문인 설치·기동·정지·확장, 업그레이드·되돌림·설정 변경 반영, Live-Map 상태 보기, 메시지 전송 결과·통신 로그 조회, DLQ·격리 설비·Repair Task의 겹치는 내용을 읽어 소유 범위를 대조했다.

| 저장소 | HEAD 12자리 | 확인한 작업 트리 상태 |
|---|---|---|
| nori-tc-exec | f1d5e0a8e727 | 본문 원천 파일 15개에 대한 status 출력 없음, exit 0 |
| nori-tc-common | 6fe71f6b2da6 | 본문 원천 파일 5개에 대한 status 출력 없음, exit 0 |
| nori-tc-ui-front | 9fe69a75dcd3 | 본문 원천 파일 5개에 대한 status 출력 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 본문 원천 파일 3개에 대한 status 출력 없음, exit 0 |
| nori-tc-gateway | d3636ab86763 | 본문 원천 파일 2개에 대한 status 출력 없음, exit 0 |
| nori-tc-business | 7db24f7466bd | 본문 원천 파일 1개에 대한 status 출력 없음, exit 0 |

`git -C <repo> rev-parse --short=12 HEAD`와 `git -C <repo> status --short -- <원천 경로들>`을 사용했다. 형제 저장소의 최초 명령은 `fatal: detected dubious ownership`으로 실패했다. 사용자 소유의 지정된 저장소에 한해 각 Git 호출에 `-c safe.directory=C:/workspace_nori/nori-tc/<repo>`를 주어 읽었다. 전역 설정은 바꾸지 않았다. 이후 status에 나온 `unable to access .../.config/git/ignore: Permission denied` 경고와 exit 0·경로 변경 출력 없음은 구별했다. 새로 추가한 `UiAuthenticationEntryPoint.java`도 별도 status로 변경 없음과 exit 0을 확인했다.

검색 원문의 각 `원천` 행에 파일별 commit과 실제 줄 범위를 기록했다. 작업 트리 수정 파일을 근거로 사용하지 않아 파일 SHA 대체 표기는 없다. 추가로 로그 필터·압축 스케줄러·연결 게이지 시험의 기대값을 읽었으나 제품 시험을 실행한 결과로 보고하지 않는다.

## 절별 사실 대조

표의 검색 원문 줄은 위 SHA의 최종본 기준이다. 실행 전 권한 확보, 조회 오류와 정상값의 구별, 담당 문서 연결은 운영 절차이다. 현장의 현재 상태를 관측한 결과가 아니다. 문장에 포함된 objectKey의 소유 범위는 `docs/tc-rag-document-list.md` 및 사용자 지정 범위와도 대조했다.

### 매일 Pod 실행 상태와 재시작을 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 19 | 운영 담당의 매일 다섯 서비스 조회 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2072~2086 | 매일 점검 정책, tc namespace와 앱별 차트 라벨을 함께 대조했다. 셸 명령임을 본문에 명시했다. |
| 22 | Pod 목록 명령 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1330~1338 | 배포된 Pod 목록의 읽기 전용 조회다. 실행하지 않았다. |
| 25 | Running·재시작 증가·반복 재시작 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2078~2112 | 누적 수와 반복 증가를 구별해 점검한다. 현재 재시작 수는 쓰지 않았다. |
| 25 | Gateway 예비 Pod는 Ready가 아닐 수 있음 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L61~95 | startup/liveness는 DOWN이 아닌지, readiness는 UP인지 검사한다. 조정 기능 사용 조건을 유지했다. |

### Gateway 준비 수를 점검할 때 실행할 검증

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 34 | 실행 루트·배포 설정과 같은 그룹 파일 | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 L12~25 | namespace와 선택적 두 번째 파일 인자를 받으며 로컬 파일의 id= 행을 센다. |
| 37 | 그룹 파일을 명시한 명령 | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 L12~25 | 두 인자를 실제 인터페이스와 대조했다. CI 호출은 nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 L364~370이다. |
| 40 | 성공 출력·exit 0 | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 L33~57 | 성공 문자열과 종료코드를 확인했다. 실패 경로는 Pod 상태를 출력하고 exit 1이다. |
| 40 | 실패 조사·판정 한계의 담당 문서 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2078~2084 | 준비 수 이상 시 조사한다. 설치 문서 L224~233이 min(Pod 수, 그룹 수) 하한과 replica·그룹별 추가 점검을 이미 소유해 복제하지 않았다. |

### 여러 Pod의 하루 운영 로그를 빠짐없이 조회하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 52 | 네 서버 앱의 라벨을 하나씩 조회 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L16~23; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 L12~19; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L12~19; nori-tc-exec/deploy/helm/tc-simulator/templates/deployment.yaml @ f1d5e0a8e727 L14~21 | app 라벨이 각 차트 app.name을 사용한다. 공통 서버 로그와 화면 nginx 로그 형식을 혼합하지 않았다. |
| 52 | Deployment 조회만으로 모든 replica를 읽지 못함 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1947~1960 | 매뉴얼이 Pod별 라벨 조회를 요구한다. |
| 55~59 | 선택 앱의 모든 Pod에서 최근 24시간 조회 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1957~1960, L2107~2108 | Pod 루프와 --since=24h를 결합하고 출력마다 Pod를 표시했다. |
| 62 | 남아 있는 현재 기록으로 범위 제한 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1963~1965 | 로컬 파일과 중앙 보관을 구별하며 과거 Pod의 기록을 보장하지 않는다. |
| 62 | 빈 대상·조회 실패는 오류 0건이 아님 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2078~2080, L2099~2108 | 판정 전에 실제 로그 조회가 있어야 한다는 절차로 썼다. |

### 하루 로그에서 ERROR와 WARN을 어떻게 판정하는가

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 71 | 공통 패턴 적용 조건·시각 다음 수준 | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 L33~45; nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 L29~36 | 배포 생성 입력과 공통 설정 모두 같은 패턴이다. |
| 74 | 줄 시작 시각과 실제 수준을 고정한 필터 | nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 L36~36 | 날짜·밀리초 시각·대괄호 수준 순서와 맞췄다. 아래 합성 로그 grep 검사에서 WARN·ERROR 2개만 선택했다. |
| 77 | ERROR 0건을 일상 기준으로 사용 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2104~2108 | 제품 보증값이 아닌 매뉴얼의 운영 기준이다. |
| 77 | WARN은 ERROR 건수에서 제외·반복 경고 읽기 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2104~2105 | WARN 0건을 요구하지 않는다. |
| 77 | ERROR 앞뒤 원문으로 조사 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1364~1375, L1947~1950 | 오류 직전 맥락을 함께 보는 목적과 필터 없는 조회 명령을 대조했다. |

### 재시작 전 로그와 오류 앞뒤 원문을 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 85 | 조사할 POD를 지정·현재와 이전을 구별 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1947~1954 | 현재 logs와 --previous를 구별했다. |
| 88~89 | 필터 없는 하루 조회·이전 컨테이너 조회 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1948~1949, L2107~2108 | 대상은 Deployment 대신 조사할 Pod 하나다. |
| 92 | 이전 로그 실패 시 오류 기록·확보한 기록으로 조사 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1943~1965, L2078~2079 | 이전 로그의 존재를 보증하지 않는 실패 분기다. |

### 운영 로그 한 줄에서 시각과 추적 식별자를 찾기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 102 | 서울 시각부터 스레드까지의 출력 순서 | nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 L36~36; nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 L16~20 | Asia/Seoul·traceId·APP_NAME·logger{0}·PROCESSED=thread를 대조했다. |
| 102 | WARN 뒤 공백 | nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 L36~36 | %-5level 패턴이다. |
| 104 | traceId·시각으로 요청 기록 연결 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1974~1982 | 상관 키의 목적을 패턴의 MDC traceId와 대조했다. |
| 104 | 자재·설비 baggage와 출력 칸 구별 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L168~171; nori-tc-exec/config/tc-log.properties @ f1d5e0a8e727 L36~36 | mat_id·eqp_id 전파는 정의되어 있으나 공통 패턴의 별도 칸에는 없다. |

### 컨테이너 안의 앱 로그와 설비 로그 위치

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 116 | 세 앱의 콘솔·파일 동시 기록 | nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 L99~104 | root가 CONSOLE·APP_FILE·EQP_SIFT를 참조한다. |
| 116 | Gateway·Business·Backend 앱 디렉터리 | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 L199~200, L294~295, L350~350; nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 L15~15 | 상대 경로를 WORKDIR /app와 결합했다. apps 각 전용 properties의 동일 설정도 읽었다. |
| 118 | 유효 eqpId가 있으면 앱 파일 제외·설비 파일 기록 | nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 L44~90 | 실제 필터 AppMdcAbsenceFilter.java L36~62·EqpMdcPresenceFilter.java L40~67도 읽어 null·공백·unknown 판정을 확인했다. |
| 118 | gateway.·business. 파일 접두사 | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 L199~200, L294~295; nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 L87~90 | prefix와 eqpId 사이 구분자는 점이다. 앱 파일만으로 설비 기록을 전부 찾을 수 없다. |

### 파일 로그 보관 설정과 중앙 수집을 구별하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 131 | 배포 생성 입력의 14일·100MB·5GB | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 L36~41 | APP·EQP 설정값을 읽었다. 현장 적용값이라고 쓰지 않았다. |
| 133 | 5GB는 롤링 대상마다 적용 | nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 L55~90 | APP rollingPolicy와 eqpId마다 생성하는 EQP appender 각각의 totalSizeCap이다. |
| 135 | 압축 켜짐·2일·60분 | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 L42~44; nori-tc-common/common/tc-common-logging/src/main/java/com/nori/tc/logging/LogCompressionScheduler.java @ 6fe71f6b2da6 L61~99 | 설정을 스케줄러가 읽고 고정 지연으로 실행한다. TcLogAutoConfiguration.java L15~33의 enabled 조건도 확인했다. |
| 135 | 로그 보관 볼륨 없음·장기 보관과 구별 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L172~179; nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 L166~173; nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L161~168 | 세 Deployment의 볼륨은 설정 Secret만이다. |
| 137 | 중앙 수집 요청 내용 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1963~1972 | 대상·기간·Pod/release 구분을 요청한다. 실제 수집·저장 상태는 주장하지 않았다. |

### 매일 화면 응답과 미인증 차단을 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 148 | 설치 호스트·미인증 API 조회 | nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 L7~21; nori-tc-ui-front/deploy/docker/nginx.conf @ 9fe69a75dcd3 L43~58 | 설치 호스트에서 /api/가 Backend로 전달된다. 현장 호스트를 넣지 않았다. |
| 151 | 쿠키 없는 curl 명령 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1386~1390 | URL 호스트를 변수로 바꾸고 status code를 출력한다. |
| 154 | 401 차단·200이면 인가 조사 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiSecurityConfig.java @ 985d17c604dd L123~154; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiAuthenticationEntryPoint.java @ 985d17c604dd L69~81 | /api/user는 공개 허용 목록에 없고 미인증 엔트리포인트는 401을 쓴다. |
| 154 | 연결·신뢰 실패 구별·로그인 화면 확인 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L1379~1390, L2082~2086 | API 차단 검사와 로그인 성공 확인을 분리했다. 화면 조작 절차는 재수록하지 않았다. |

### 설비 연결과 대시보드에서 매일 확인할 항목

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 165 | 연결·지연·처리량·화면·자원 점검 | nori-tc-exec/docs/observability/grafana/tc-gateway-dashboard.json @ f1d5e0a8e727 L32~104; nori-tc-exec/docs/observability/grafana/tc-business-dashboard.json @ f1d5e0a8e727 L32~88; nori-tc-exec/docs/observability/grafana/tc-ui-backend-dashboard.json @ f1d5e0a8e727 L32~98 | JSON의 실제 패널과 expr를 확인했다. 연결 게이지는 EquipmentConnectionMetrics.java L42~82와 시험 L19~35의 up/down 기대값을 읽었다. 지표 뜻은 복제하지 않았다. |
| 165 | 설비 화면 조회 담당 문서 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2083~2083 | 화면 연결 상태도 확인하되 Live-Map 문서의 화면 절차는 복제하지 않았다. |
| 167 | 해석·목표 소유 문서와 연결 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2084~2084; docs/tc-rag-document-list.md의 메트릭·대시보드·알람 행 | 지표 의미와 수치 기준은 참조 문서가 소유한다. |
| 167 | 목표 초과와 미수집을 구별·담당자에게 조회 정보 전달 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2021~2030, L2084~2084 | 수집기·저장소 소유가 외부임을 확인하고 실제 수신 상태는 쓰지 않았다. |

### 매일 노드 디스크와 외부 저장소를 점검하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 177, 180~181 | 노드 지정·조건 조회 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2092~2097 | 저장소의 조회 명령을 NODE 변수로 표현했다. |
| 184 | DiskPressure False 점검 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2097~2097 | 현재 False라고 쓰지 않고 판정 방법만 썼다. |
| 184 | 노드·이미지·DB 저장소와 각 담당 조회 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2088~2090 | 외부 둘은 패키지 밖이다. 세 서버 차트의 설정 전용 볼륨 정의도 대조했다. |

### 화면 인증서의 남은 기간을 확인하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 192, 195 | 화면 호스트의 만료 시각만 출력 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2467~2469; nori-tc-ui-front/deploy/helm/tc-ui-front/templates/httproute.yaml @ 9fe69a75dcd3 L7~14 | SNI·접속 호스트를 같은 변수로 지정하고 x509 -enddate를 쓴다. 인증서 내용을 문서에 넣지 않았다. |
| 198 | 한 달 미만이면 Gateway 담당에게 교체 요청 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2472~2475 | 한 달 기준은 운영 정책이다. 교체 작업은 보안 운영 문서로 연결했다. |

### 조정 저장소의 cluster Epoch 존재를 조회하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 208 | 루트·접속 입력 네 가지·SSH 경유 | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 L30~60 | 환경변수와 SSH→kubectl exec→redis-cli 흐름이다. 기본 주소·키 파일 경로는 본문에 옮기지 않았다. |
| 211 | status 인자 | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 L63~70 | issue를 실행하거나 일상 조치로 쓰지 않았다. |
| 214 | 인자 생략 시 DB 0·미발급 표시 | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 L43~44, L63~70 | 빈 조회값의 표시를 확인했다. |
| 214 | 조정 세 앱의 다음 기동 실패 | nori-tc-gateway/gateway/adapter/tc-gateway-redis-adapter/src/main/java/com/nori/tc/gateway/adapters/redis/coordination/GatewayCoordinationContext.java @ d3636ab86763 L47~51; nori-tc-business/business/adapter/tc-business-redis-adapter/src/main/java/com/nori/tc/business/adapters/redis/coordination/BusinessCoordinationContext.java @ 7db24f7466bd L50~55; nori-tc-ui-backend/ui/adapter/tc-ui-redis-adapter/src/main/java/com/nori/tc/ui/adapters/redis/coordination/UiCoordinationContext.java @ 985d17c604dd L40~48 | 세 클래스의 current().orElseThrow를 확인했다. |
| 214 | 조회 오류·미발급 구별·복구 문서 연결 | nori-tc-exec/coord-epoch.sh @ f1d5e0a8e727 L53~70; docs/tc-rag-document-list.md의 조정 작업 문서 행 | 조회 결과를 무조건 성공으로 판정하지 않는다. 발급·복구는 이번 본문에서 하지 않는다. |

### 세 서버의 조정 기능 설정이 같은지 조회하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 224 | Deployment에서 선택한 환경변수만 조회 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2123~2134 | 전체 env나 Secret 내용을 조회하는 명령을 넣지 않았다. |
| 228 | Gateway 앱·키 대응 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L137~141 | TC_GATEWAY_COORDINATION_ENABLED를 coordination.enabled로 주입한다. |
| 229 | Business 앱·키 대응 | nori-tc-exec/deploy/helm/tc-business/templates/deployment.yaml @ f1d5e0a8e727 L130~134 | TC_BUSINESS_COORDINATION_ENABLED를 주입한다. |
| 230 | Backend 앱·키 대응 | nori-tc-exec/deploy/helm/tc-ui-backend/templates/deployment.yaml @ f1d5e0a8e727 L122~125 | TC_UI_BACKEND_COORDINATION_ENABLED를 주입한다. |
| 233 | 변수로 필터한 jsonpath | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2123~2125 | 고정 키 예제를 APP·KEY 변수로 표현했다. 실제 kubectl은 실행하지 않았다. |
| 236 | 세 값 일치·다르면 담당자에게 전달 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2118~2121 | 한쪽만 기능을 꺼 참여 증적을 어긋나게 하지 않는 운영 정책이다. 변경 명령을 추가하지 않았다. |

### 끝나지 않은 조정 작업 수를 읽기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 246 | 실제 Redis 워크로드 지정·인증 선행조건 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2140~2145 | 현장 접속값과 비밀번호를 쓰지 않았다. 승인된 인증이 준비된 담당자에게만 조회를 안내한다. |
| 249 | 논리 DB 0의 ZCARD 조회 | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/CoordinationKeys.java @ 6fe71f6b2da6 L15~15, L90~96; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2140~2142 | 실제 네임스페이스 상수와 pending 키를 조합한 값이 명령과 일치한다. |
| 252 | 완료되지 않은 조정 인덱스의 건수 | nori-tc-common/common/tc-common-coordination/src/main/java/com/nori/tc/common/coordination/store/OperationLedgerRedisStore.java @ 6fe71f6b2da6 L78~85; nori-tc-common/common/tc-common-coordination/src/main/resources/coordination/operation_transition.lua @ 6fe71f6b2da6 L47~65 | pending 갱신·terminal 제거를 읽었다. 모든 업무 작업 수라는 뜻으로 확대하지 않았다. |
| 252 | 전환 배포 전 0·복구 판단 연결 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2136~2137 | 일상 조회 수치 자체를 일률적으로 0이어야 한다고 쓰지 않았다. |

### 화면 조회가 필요한 기록의 담당 문서

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 261 | 메시지 결과·통신 로그·처리실패 소유 연결 | nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 L1102~1130; nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L115~148; nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 L19~109 | 사용자 지정대로 한 문장으로 가리키고 버튼·API·권한·조회 범위 본문은 복제하지 않았다. |

## 매뉴얼과 코드가 다른 곳

| 항목 | 매뉴얼·설명 | 코드 대조와 반영 |
|---|---|---|
| 로그 수준 필터 | O L2100~2108은 수준만 거른다고 설명하면서 `grep -E '\[(ERROR\|WARN)'` 예제를 든다. | `tc-log.properties` L36의 `%msg`에도 대괄호 문자열이 들어갈 수 있어 위치를 고정하지 않은 검색은 본문도 선택한다. 본문 L74는 시각과 수준 위치를 고정했다. 합성 로그 대조로 INFO 본문 제외를 확인했다. |
| 파일 용량 범위 | O L1983은 파일 보관을 `14 일 · 파일당 100MB · 합계 5GB`로 요약한다. | logback-spring.xml L55~90의 APP rollingPolicy와 eqpId마다 생성하는 EQP rollingPolicy의 상한을 구별해 L131~133에 썼다. Pod 전체 합계로 설명하지 않았다. |

추가 확인: 검증 스크립트 첫 주석·CI 설명의 Ready 수 등식과 실제 L43~48의 하한 비교는 다르다. 기존 설치 문서가 이미 실제 조건·한계를 올바르게 설명하므로 이 문서에서는 해당 문서로 연결했다. 다른 문서의 결함으로 보고하지 않았다. 그 주석을 근거로 판정을 바꾸지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 현재 버전·replica·Pod 상태·재시작 수·로그 오류 건수·Epoch·진행 중 작업 수 | 클러스터와 Redis에 접속하지 않았다. | TC 운영 담당 |
| 중앙 로그 수집이 실제 활성인지, 저장소·보관 기간·조회 화면 주소와 권한 부여 현황 | 차트·가이드는 구성 입력과 요청 방법만 밝힌다. 실제 적용 상태는 확인하지 않았다. | 관측 인프라 담당 |
| 현재 Grafana 설치·데이터 원천·실제 지표·알람 및 통지 상태 | 패키지 JSON은 실제 등록·수신의 근거가 아니다. | 관측 인프라 담당 |
| 노드·이미지 저장소·DB 저장소의 현재 사용률·여유 공간과 임계치 | 현장 조회와 저장소별 운영 계약이 없다. | Kubernetes·레지스트리·DB 담당 |
| 현장 HTTPS 호스트·인증서 만료일·내용·개인 연락처·자격값 | 현장 접속하지 않았고 민감정보는 수록 범위가 아니다. | Gateway·보안 운영 담당 |
| Redis 인증을 포함한 현장 접속 상세 | 조회 코드만으로 설치별 인증 방식을 확정하지 않았다. | Redis·보안 운영 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 문서 objectKey | 넣지 않은 사실 |
|---|---|
| 00.rag/user/message-results-and-communication-logs.md | 전송 결과 화면의 기간·목록·상세, 통신 로그의 화면 경로·보관 범위·표시·권한 |
| 00.rag/user/dlq-quarantine-and-repair-task.md | 처리실패 카드·목록·격리 해제·Repair Task 조작 |
| 00.rag/user/live-map-status.md | 설비 선택·상태·포트 화면 조작과 화면 조회 권한 |
| 00.rag/admin/installation-and-service-lifecycle.md | 설치·확장·기동 명령과 Gateway 검증 스크립트의 정확한 하한·한계 |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | Epoch 발급·소유권·복구 판단과 상세 운영 조치 |
| 00.rag/admin/metric-dashboard-and-alert-reference.md | 지표 뜻·수치 기준·대시보드 가져오기·알람 정책 |
| 00.rag/admin/security-operations.md | 인증서·Secret 교체·인증 자격 운영 |
| 00.rag/admin/platform-troubleshooting.md | 자원·플랫폼 장애별 원인 분기와 복구 조치 |
| 00.rag/admin/incident-evidence-handover.md | 장애 문의 때의 전체 근거 묶음·담당 인계 |
| 00.rag/admin/configuration-reference.md | 설정 키별 전체 의미와 변경 영향 |

## 검사 결과

기계 검사는 아래 명령을 두 번 실행했다. 검사기는 바꾸지 않았다. Git Bash는 GNU bash 5.2.37(1)-release, Java는 OpenJDK Temurin 21.0.9+10-LTS이다. 검사 명령 실행의 총 wall time과 provider 비용은 측정하지 않았다.

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/daily-checks-and-log-inspection.md
```

첫 검사 exit 1: 구조·길이·마스킹 위반은 없었으나 기존 업그레이드 문서와 코드 자식 1개가 같았다. 원문 오류와 수정은 다음과 같다.

```text
CROSS VIOLATION 같은 자식 본문 [00.rag/admin/daily-checks-and-log-inspection.md L36, 00.rag/admin/upgrade-rollback-and-configuration-apply.md L114]: sh ./deploy/scripts/verify-gateway-rollout.sh tc
TOTAL files=21 targets=1 violations=1
```

검증 스크립트가 지원하는 두 번째 인자로 점검 기준 파일을 명시했다. 설치·업그레이드 문서는 고치지 않았다. 마지막 원문 편집 후 두 번째 검사를 실행해 exit 0을 확인했다. 마지막 출력 10줄은 다음과 같다.

```text
    L236-236 문단 53  | 세 값이 같은지 확인한다. 한쪽만 다르면 참여 증적이 어긋나므로 조정 운영 담당자에게 전달한다.
§ L238-252 [18] 끝나지 않은 조정 작업 수를 읽기   parent=362 children=3 sources=4
    L246-246 문단 127  | Redis 조회 권한이 있는 담당자는 REDIS_NS·REDIS_WORKLOAD에 운영 Redis의 namespace·워크로드를 지정한다. 인증이 필요한 환경에서는 승인된 인증 경로를 사용하고 자격값을 명령 인자에 쓰지 않는다.
    L248-250 코드 100  | kubectl -n "$REDIS_NS" exec "$REDIS_WORKLOAD" -- redis-cli -n 0 ZCARD tc:coord:v1:operations:pending
    L252-252 문단 133  | 반환된 정수는 완료되지 않은 조정 작업 인덱스의 건수다. 조정 기능 전환 배포 전에는 0인지 확인한다. 작업의 복구 판단은 00.rag/admin/coordination-ownership-and-recovery-check.md에서 다룬다.
§ L254-261 [20] 화면 조회가 필요한 기록의 담당 문서   parent=138 children=1 sources=3
    L261-261 문단 138  | 화면의 통신 로그·메시지 전송 결과는 00.rag/user/message-results-and-communication-logs.md, 처리실패 조회는 00.rag/user/dlq-quarantine-and-repair-task.md에서 안내한다.
SUMMARY sections=16 children=47 maxChild=158 maxParent=400
CORPUS docs=21/53 sections=362 part={admin=4, user=17} type={개념=4, 사용 절차=13, 운영=4}
TOTAL files=21 targets=1 violations=0
```

보조 확인: Bash stdin으로 공통 패턴 모양의 합성 INFO·WARN·ERROR 세 줄을 넣었다. INFO 본문에 `[ERROR]`를 넣고 본문 L74와 같은 grep 식을 적용했다. `test "$count" -eq 2`가 exit 0이었고 출력은 WARN·ERROR 두 줄과 `FILTER_PROBE PASS selected=2; INFO body excluded`였다. 네트워크나 제품 로그를 사용한 시험이 아니다.

사람 검사는 원문 16절 전부를 읽었다. 아래 절별 네 열은 각각 판정이다.

| 절 | 제목으로 질문 식별 | 원천 일치 | 절 독립 답 | 중복·소유 범위 |
|---|---|---|---|---|
| 매일 Pod 실행 상태와 재시작을 확인하기 | 적합 | 대조 완료 | 권한·명령·판정 포함 | 현재 수치 없음 |
| Gateway 준비 수를 점검할 때 실행할 검증 | 적합 | 대조 완료 | 루트·입력·성공·실패 포함 | 상세 판정은 설치 문서 연결 |
| 여러 Pod의 하루 운영 로그를 빠짐없이 조회하기 | 적합 | 대조 완료 | 앱·라벨·기간·실패 포함 | 현재 남은 기록으로 범위 제한 |
| 하루 로그에서 ERROR와 WARN을 어떻게 판정하는가 | 적합 | 대조 완료 | 패턴 조건·필터·판정 포함 | 오류 뜻 참조를 복제하지 않음 |
| 재시작 전 로그와 오류 앞뒤 원문을 읽기 | 적합 | 대조 완료 | 현재·이전 조회와 실패 포함 | 이전 기록 존재 보증 없음 |
| 운영 로그 한 줄에서 시각과 추적 식별자를 찾기 | 적합 | 대조 완료 | 실제 순서·대조 방법 포함 | 이름 번역으로 의미를 만들지 않음 |
| 컨테이너 안의 앱 로그와 설비 로그 위치 | 적합 | 대조 완료 | 앱 경로·설비 분리 포함 | Redis 통신 로그와 구별 |
| 파일 로그 보관 설정과 중앙 수집을 구별하기 | 적합 | 대조 완료 | 설정 조건·범위·요청 포함 | 실제 보관 상태 미주장 |
| 매일 화면 응답과 미인증 차단을 확인하기 | 적합 | 대조 완료 | 호스트·요청·코드·실패 포함 | 로그인 조작 복제 없음 |
| 설비 연결과 대시보드에서 매일 확인할 항목 | 적합 | 대조 완료 | 조회권한·항목·값 없음 분기 | 지표 뜻·목표는 참조 연결 |
| 매일 노드 디스크와 외부 저장소를 점검하기 | 적합 | 대조 완료 | 노드·명령·판정·담당 포함 | 실제 여유 공간 미주장 |
| 화면 인증서의 남은 기간을 확인하기 | 적합 | 대조 완료 | 조회·한 달·교체 요청 포함 | 인증서 교체는 보안 문서 연결 |
| 조정 저장소의 cluster Epoch 존재를 조회하기 | 적합 | 대조 완료 | 접속 입력·명령·미발급·오류 포함 | 발급·복구 절차 복제 없음 |
| 세 서버의 조정 기능 설정이 같은지 조회하기 | 적합 | 대조 완료 | 앱·키·명령·판정 포함 | 세 표 행 독립 해석 가능 |
| 끝나지 않은 조정 작업 수를 읽기 | 적합 | 대조 완료 | 대상·인증 선행조건·명령·해석 포함 | 복구 상태 뜻은 연결만 함 |
| 화면 조회가 필요한 기록의 담당 문서 | 적합 | 대조 완료 | 담당 문서를 한 문장으로 지정 | 화면 답 복제 없음 |

제목 정확 중복·불필요한 반복 틀, 본문 복제, 정보 없는 틀 문장, 이름 토막 뜻풀이, 잘린 문장·오답 사례, 마스킹으로 사라진 사실, user 구획에 관리 답을 새로 쓴 경우를 전수 대조해 이 문서의 미해소 지적 0건으로 판정했다. 기계 검사에서 MASK·CROSS VIOLATION 0건을 확인했다. 소스 설정값과 운영 정책을 현장 관측값으로 바꾸지 않았다.

## 임시 자원과 종료 범위

검사용 임시 파일은 지정된 작업 폴더의 `work/check-first.log`와 `work/check-final.log` 두 개였다. 필요한 실패 원문·최종 출력·판정을 이 기록에 보존한 뒤 정리했다. 작업 폴더는 `C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-22-daily-checks-and-log-inspection/work`이다.

정리 전 `Get-CimInstance Win32_Process`의 시스템 전체 프로세스 명령줄 조회는 `액세스가 거부되었습니다.`, HRESULT `0x80041003`으로 실패했다. 그 뒤 출력된 빈 배열 수를 활성 참조 0건의 근거로 사용하지 않았다. 검사 세션 37547·84210은 각각 종료코드 0으로 종료가 확인됐고, 두 로그를 `[IO.File]::Open(..., Read, FileShare.None)`으로 열어 배타적 읽기에 성공한 뒤 즉시 닫았다. 전체 프로세스 명령줄의 완전한 대조는 미확인이다.

최초에는 사전검사와 아래 변수 기반 삭제를 한 PowerShell 호출에 넣었으나 프로세스 시작 전에 거부됐다. 반환 오류에는 `CreateProcess`, `Rejected`와 `rejected: blocked by policy`가 있었으며 도구 출력의 중간 명령 부분은 `945 chars truncated`로 생략됐다. 자동 승인 reviewer가 원인이라고 단정하지 않는다. 명확한 거부 분류·evaluator trace는 확인하지 않았다. 네트워크 접속 금지 범위 때문에 공식 웹 소스와의 추가 대조는 수행하지 않았다.

```powershell
$work='C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/doc-22-daily-checks-and-log-inspection/work'
$paths=@("$work/check-first.log","$work/check-final.log")
foreach($p in $paths){Remove-Item -LiteralPath $p -ErrorAction Stop; Write-Output "FILE_ABSENT=$(-not (Test-Path -LiteralPath $p)) $p"}
if(@(Get-ChildItem -LiteralPath $work -Force).Count -eq 0){Remove-Item -LiteralPath $work -ErrorAction Stop}
```

같은 복합 명령을 반복하지 않았다. 읽기 전용 사전검사를 별도 호출로 완료해 work와 두 파일의 절대경로·소유 용도를 확인했다. work는 Directory, 로그 둘은 Archive였고 모두 ReadOnly·Hidden·System·ReparsePoint가 false, LinkType은 null이었다. Resolve-Path 결과도 지정된 작업 폴더 안이었다. PowerShell 표시 버전은 `5.1.26100.9444`였다.

사전검사 후 삭제 대상 두 개를 변수·루프 없이 `Remove-Item -LiteralPath '<작업 폴더>/work/check-first.log', '<작업 폴더>/work/check-final.log'`의 완전한 절대경로 인자로 고정해 native PowerShell 비강제 삭제를 실행했고 exit 0이었다. `Test-Path`에서 두 파일 모두 False, 남은 하위 항목 0개를 확인했다. 이어 빈 work 디렉터리 자체도 완전한 절대경로의 `Remove-Item -LiteralPath`로 삭제해 exit 0이었다. `-Force`·`-Recurse`, 다른 셸·삭제 인터프리터, 정책·권한 변경을 사용하지 않았다. 마지막 `Test-Path`로 work 부재를 확인했다.

지정한 검색 원문·작성 기록 두 파일만 최종 산출물이다. git add·commit·push, 다른 문서 수정, 제품·설정·SPEC 변경, Docker·kubectl·ssh 실행, 서버 접속·업로드, 다른 프로세스 종료는 수행하지 않았다. 다음 작업은 메인 세션의 이 두 파일 원천 대조이다.

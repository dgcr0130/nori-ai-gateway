# TC DB 테이블 작성 기록

## 대상

- objectKey: `00.rag/admin/database-table-reference.md`
- 버킷·구획: `nori-tc`·admin, 필요 권한 `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 참조
- 검색 원문: `rag-documents/nori-tc/00.rag/admin/database-table-reference.md`
- 검색 원문 SHA-256: `74987e18617c7655be805ef93812544e26583727ff9573efe7d10f3abf503cb9`
- 검색 원문 줄 수: 381, H2 절 수: 26, 자식 수: 78
- 확인일: 2026-11-19(한국 시간)
- 독립 작성 세션이 직접 조사·작성·검사했다. 다른 Agent 호출은 0회이며 별도 공식 검증·업로드·제품 실행은 수행하지 않았다.

## 원천 판본

| 저장소 | HEAD 12자리 | 작업 트리 확인 |
|---|---|---|
| nori-tc-exec | f1d5e0a8e727 | 전체 status에 변경 없음. 원문 메타가 참조하는 6파일의 경로 제한 status도 exit 0·출력 없음. |
| nori-tc-common | 6fe71f6b2da6 | 기존 CLAUDE.md 변경을 보존했다. 원문 메타가 참조하는 45파일의 경로 제한 status는 exit 0·출력 없음. |
| nori-tc-ui-backend | 985d17c604dd | 전체 status에 변경 없음. 참조하는 8파일의 경로 제한 status도 exit 0·출력 없음. |
| nori-tc-simulator | 5e9b02e3ae16 | 전체 status에 변경 없음. 참조하는 2파일의 경로 제한 status도 exit 0·출력 없음. |
| nori-tc-gateway | d3636ab86763 | 확정 revision의 실제 소비자 1파일을 추가로 읽었다. 해당 경로의 status는 exit 0·출력 없음. |

실행한 경로 확인 형식은 `git -c safe.directory=<저장소 절대경로> -C <저장소 절대경로> status --short -- <읽은 파일 경로들>`이다. 최초 형제 저장소 조회는 sandbox 계정과 소유자 차이로 `fatal: detected dubious ownership`이 발생했다. 같은 Git 명령의 읽기 조회에만 해당 저장소를 명시했고 전역 config 파일은 변경하지 않았다. Git이 전역 ignore 파일 접근 경고를 출력한 점과, 경로 제한 status의 종료코드 0 및 변경 출력 없음은 구별했다.

`docs/rag-document-writing-rules.md`와 `docs/tc-rag-document-list.md`를 전체 읽었다. 정보 모델 `nori-tc-exec/docs/completion-report/03-information-model.md`의 테이블·칼럼·관계·제약 표를 migration V1~V8과 대조했다. 실제 JPA 엔티티는 exec이 아닌 common의 `db/jpa/tc-db-jpa-common-schema/`에 있고 모델 버전·배포·Live-Map·감사·비밀번호 이력·시뮬레이터의 SQL도 함께 읽었다.

겹치는 기존 검색 원문은 백업·복구, 조정 작업·소유권·복구 필요 확인, 상태값 문서를 읽고 비교했다. 다른 기존 문서의 TC 테이블 표기도 검색해 별도 표의 복제가 없는지 확인했다. 기존 문서는 수정하지 않았다.

## 매뉴얼과 코드가 다른 곳

이번 원문에 채택한 테이블명·기본 키·저장 칼럼·관계의 범위에서는 정보 모델과 migration·매핑 사이의 충돌을 발견하지 않았다. 정보 모델의 기준정보/현재/이력 분류를 모든 테이블의 수명 보장이나 삭제 정책으로 확장하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 담당 역할 |
|---|---|---|
| 현장 DB 행·자료량·현재 적용 migration·실제 백업 성공 여부와 시각 | 현장에 접속하지 않았으며 소스만으로 현재 상태를 확정할 수 없다. | DB 운영 담당 |
| map_role의 업무별 값 목록·현장별 Lot 역할, 정책 문자열의 현장 의미 | migration과 매핑은 저장 구조를 밝히지만 해당 업무 의미 전체를 확정하지 않는다. | TC 업무·Business 담당 |
| Flyway 관리 테이블의 현장 존재·현재 이력 행 | 이번 목록은 정보 모델과 migration이 정의한 TC 업무 테이블이다. 현장 Flyway 적용 결과를 읽지 않았다. | DB 배포 담당 |

## 다른 문서가 소유해 넣지 않은 사실

| 소유 문서 objectKey | 이번 원문에서 복제하지 않은 내용 |
|---|---|
| 00.rag/admin/backup-and-recovery.md | pg_dump·pg_restore·정지·복원·스키마 이력 대조 절차, 파일 본문 BYTEA 칼럼 표, DB 밖 백업 절차 |
| 00.rag/admin/coordination-ownership-and-recovery-check.md | Redis 조정 키 목록·관리 API 조회·원장 후퇴와 복구 판단 절차 |
| 00.rag/user/state-reference.md | 설비·포트·배포·시뮬레이터의 상태값별 의미·표시 조건 |
| 00.rag/user/permission-code-reference.md | 개별 권한 코드의 뜻과 화면 작업별 권한 표 |
| 00.rag/user/plc-modbus-register-and-type-reference.md | PLC 데이터 타입별 해석과 주소·배율 규칙 |
| 00.rag/user/socket-framing-and-parsing-reference.md | Socket 프레임 처리 규칙과 설정 사용 조건 |
| 00.rag/admin/kafka-topic-and-message-reference.md | 토픽별 방향·메시지 형식·연동 계약 |

## 최초 작성 검사 결과(보완 전)

기계 검사 명령:

```text
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/database-table-reference.md
```

Windows에서는 확인한 Git Bash `C:/Program Files/Git/bin/bash.exe`로 위 스크립트를 실행했다. 검사기를 수정하지 않았다. 1차 실행은 exit 1, 줄 범위 위반 7건이었다. 원문 SHA는 `b398aec55483aff8bde6ebd232619afcd21aa50f5bc285aafe572221bbf8498f`였다.

| 1차 지적 | 확인과 수정 |
|---|---|
| V1 L94~1253·L1223~1253 | 실제 파일은 1239줄이다. 전체 업무 정의는 L94~1238, 시나리오 CREATE와 키를 포함한 범위는 L1220~1238로 고쳤다. |
| V3 L37~50 두 곳 | 실제 파일은 44줄이다. 이력 CREATE·칼럼·기본 키·외래 키·인덱스가 있는 L32~44로 고쳤다. |
| V6 L37~117·L57~117 | 실제 파일은 95줄이다. 전체 L31~95, 점유·대상 L51~95로 고쳤다. 실행 단독 참조도 run_id를 포함한 L31~49로 정밀화했다. |
| V7 L39~43 | 실제 파일은 38줄이다. 실제 ALTER·COMMENT가 있는 L34~38로 고쳤다. |

사람 대조에서 V3 변경 시각의 ADD COLUMN을 포함하도록 L20~29로 정밀화하고, 카탈로그 절에 배포 측 참조 칼럼 근거를 추가했다. 확정 revision 절에는 실제 조회·기록 adapter를 추가했다. 로그 설정 문장의 불필요한 비교와 계정 절의 작성 원칙 문장을 삭제했다. 최종 편집 뒤 2차 실행은 exit 0이었다.

최종 검사 마지막 출력 10줄:

```text
    L364-364 표 행 144  | 테이블=tc_simulator_scenario | 기본 키=simulator_scenario_key | 담는 자료=scenario_id·버전·통신 방식·지원 모드·source_text 원문·사용 여부·기본 제공 여부. scenario_id는 중복을 금지한다.
    L365-365 표 행 83  | 테이블=tc_sim_run | 기본 키=run_id | 담는 자료=실행 모드·scenario_id·상태·소유 인스턴스·마지막 heartbeat 시각.
    L367-367 문단 50  | tc_sim_run.scenario_id에는 시나리오 테이블을 향한 외래 키 제약이 없다.
§ L369-381 [20] 시뮬레이터의 설비 점유와 대상별 결과   parent=309 children=3 sources=3
    L378-378 표 행 110  | 테이블=tc_sim_run_lease | 기본 키=sim_run_lease_key | 담는 자료=run_id별 설비 점유와 released 여부. 미해제 행은 같은 eqp_key의 중복을 금지한다.
    L379-379 표 행 118  | 테이블=tc_sim_run_target | 기본 키=sim_run_target_key | 담는 자료=run_id별 설비 상태·통신 방식·완료와 오류 사이클 수. run_id·eqp_key 조합은 중복을 금지한다.
    L381-381 문단 79  | 두 테이블의 run_id는 실행을 참조한다. 두 테이블의 eqp_key는 문자열 설비 ID이며 tc_eqp의 숫자 기본 키를 참조하지 않는다.
SUMMARY sections=26 children=78 maxChild=144 maxParent=373
CORPUS docs=47/53 sections=784 part={admin=13, user=34} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=9, 트러블슈팅=12}
TOTAL files=47 targets=1 violations=0
```

최종 검사에서 VIOLATION·CROSS VIOLATION·MASK 출력은 없었다. 도구는 check.sh의 CommonMark 0.32.0 및 현재 ContentSanitizer mask-v1을 사용했다. 본문 기준 최대 자식 144자·부모 373자이며 모든 절은 200/450자 제한 안이다.

별도 메모리 내 Python 대조는 migration의 CREATE TABLE 블록 60개와 검색 원문의 표 행 60개를 연결했다. 모든 기본 키를 실제 PRIMARY KEY 정의와 비교했고 `DDL_tables=60 document_rows=60 unique_rows=60`, `missing=[] extra=[]`, `PK_AND_TABLE_COVERAGE_PASS`를 exit 0으로 확인했다. 검사 코드를 파일로 만들지 않았다.

작성 기록 완성 뒤 본문 줄과 사실 대조 표의 줄을 다시 비교해 `FACT_RECORD_COVERAGE_PASS rows=78 sections=26`을 exit 0으로 확인했다. 두 파일 모두 BOM 없는 UTF-8·LF·NFC이고 CR·NUL이 없음을 검사해 `ENCODING_PASS UTF8_noBOM_LF_NFC_noNUL`을 확인했다. 기록에 적은 검색 원문 SHA와 실제 바이트 SHA도 일치했다.

시험 코드는 실행하지 않고 기대값을 읽었다. `nori-tc-simulator/orchestrator/src/test/java/com/nori/tc/simulator/orchestrator/JdbcReleaseLeasePartialUniqueIntegrationTest.java`는 해제한 설비를 새 실행이 다시 점유할 수 있고 다른 설비 점유와 기존 실행은 유지된다는 기대값을 확인했다. Backend의 `JdbcPasswordHistoryPortTest.java`는 사용자별 최신순 이력·다른 사용자 분리·빈 이력을 확인하는 시험임을 읽었다. common의 `TcEqpFinalizedRevisionIT.java`는 일반 설비 수정이 확정 revision을 보존하고 없는 설비는 갱신되지 않는다는 기대값을 읽었다. 이를 이번 세션의 제품 시험 PASS로 기록하지 않는다.

문서 작업이므로 Gradle build·제품 통합 시험·DB 적용·서버 접속은 실행하지 않았다. provider 사용량·요금과 전체 세션 wall time은 도구에서 확인할 수 없어 미측정이다. 검사기 실행은 2회이며 각각 도구가 반환한 최초 대기 시간은 약 10초다. 최초 대기 시간은 실제 전체 실행 시간으로 환산하지 않았다.

임시 파일·브라우저·컨테이너·서비스를 만들지 않았다. 검사용 중간 자료는 메모리에서만 처리했으므로 이 세션의 삭제 대상은 없다. 다음 작업은 메인 세션의 원천 대조이며, 업로드는 이번 허가 범위 밖이다.

## 최초 작성 사람 검사 판정(보완 전)

26절이므로 표본을 추리지 않고 전부 읽었다. 아래 네 판정은 각각 제목만으로 질문 식별·원천 일치·독립 답변·다른 절 및 기존 문서와의 사실 소유 구별을 뜻한다.

| 절 | 제목 | 원천 | 독립 답 | 소유 구별 | 확인 초점 |
|---|---|---|---|---|---|
| 테이블 목록과 DB 백업 범위 | 적합 | 일치 | 충족 | 충족 | 업무 테이블의 전체 백업 포함 범위만 소유하고 명령은 운영 문서로 연결한다. |
| 모델 이름·버전·공유 파라미터 | 적합 | 일치 | 충족 | 충족 | 모델 이름과 버전 행의 키·중복 금지 단위를 구별한다. |
| 모델별 SECS·Socket·MES 메시지 목록 | 적합 | 일치 | 충족 | 충족 | 메시지명 정의의 저장 위치이며 연동 메시지 형식은 옮기지 않는다. |
| 모델의 변수·리포트·이벤트 정의 | 적합 | 일치 | 충족 | 충족 | 변수 종류를 포함한 유일 조합과 report/event 참조 문자열을 대조한다. |
| 메시지 흐름과 데이터 수집 항목의 연결 | 적합 | 일치 | 충족 | 충족 | 정의 두 종류와 연결 행을 구별하며 액션별 동작 표는 복제하지 않는다. |
| PLC 읽기 그룹과 응답 해석 정의 | 적합 | 일치 | 충족 | 충족 | 그룹 키·레지스터 키·밀리초 주기를 대조하고 타입별 뜻은 옮기지 않는다. |
| 모델 첨부 파일과 설비 플러그인 자료 | 적합 | 일치 | 충족 | 충족 | 행 키와 연결을 소유하고 백업 문서의 BYTEA 칼럼 표는 재수록하지 않는다. |
| 라인과 설비 기본정보의 조회 키 | 적합 | 일치 | 충족 | 충족 | 설비 ID와 숫자 행 키, 모델 버전과 라인 참조를 구별한다. |
| 설비별 HSMS·Socket·PLC 통신 설정 | 적합 | 일치 | 충족 | 충족 | 설정 행의 설비당 유일성을 설명하고 타이머·타입 값 표는 옮기지 않는다. |
| Socket 규격 목록과 설비 로그 설정 | 적합 | 일치 | 충족 | 충족 | 저장된 규격 문구와 로그 설정 칼럼만 설명하며 런타임 해석을 추정하지 않는다. |
| 설비 파라미터 값·버전 설명·전역 값 | 적합 | 일치 | 충족 | 충족 | 세 유일 조합과 버전 설명의 저장 위치를 대조한다. |
| 설비 현재 상태·포트 상태·변경 이력 | 적합 | 일치 | 충족 | 충족 | 현재 값과 변경 전후 이력을 구별하고 상태값 뜻은 기존 참조로 연결한다. |
| 설비의 DB 확정 이력을 보존하는 칼럼 | 적합 | 일치 | 충족 | 충족 | V7 조건·NULL 의미와 실제 adapter 조회·기록을 확인하고 Redis 키는 복제하지 않는다. |
| 작업 원본과 작업별 파라미터 | 적합 | 일치 | 충족 | 충족 | 설비별 작업 ID와 작업별 파라미터명 유일성을 확인한다. |
| 작업의 Carrier·슬롯·Lot 자료 | 적합 | 일치 | 충족 | 충족 | work_key·work_carrier_key와 슬롯·Lot 중복 금지 범위를 확인한다. |
| Control Job·Process Job과 Lot 연결 | 적합 | 일치 | 충족 | 충족 | 작업→Control Job→Process Job과 Lot 연결의 실제 키를 대조한다. |
| 배포 전체와 대상별 실행 명세 | 적합 | 일치 | 충족 | 충족 | 목표·단계 저장과 eqp_key의 nullable·외래 키 부재를 확인한다. |
| 배포 대상의 진행 상태와 이전 값 | 적합 | 일치 | 충족 | 충족 | 현재 상태 행과 별도 스냅샷 행·각 기본 키를 구별한다. |
| 배포 시나리오 카탈로그의 판본 기록 | 적합 | 일치 | 충족 | 충족 | 카탈로그 자체 행과 배포의 판본 칼럼을 대조하며 외래 키로 오인하지 않는다. |
| Kafka 발행 대기와 전송 시도 기록 | 적합 | 일치 | 충족 | 충족 | 큐 행과 시도 이력의 연결을 대조하고 토픽·메시지 계약 표는 옮기지 않는다. |
| 사용자 계정과 로그인 세션 | 적합 | 일치 | 충족 | 충족 | 세션 token은 칼럼 식별자만 표기하며 실제 값은 없다. |
| 사용자 그룹·소속·권한 연결 | 적합 | 일치 | 충족 | 충족 | 사용자·그룹·권한 연결 키를 대조하고 개별 권한 뜻은 옮기지 않는다. |
| 권한 정의·감사 기록·비밀번호 이력 | 적합 | 일치 | 충족 | 충족 | 저장 구조·해시 이력 조회와 자격 값 부재를 확인한다. |
| Live-Map 객체·캔버스·배치 | 적합 | 일치 | 충족 | 충족 | JSON 저장 칼럼과 라인당 캔버스 하나·선택적 설비 연결을 확인한다. |
| 시뮬레이터 시나리오 원문과 실행 기록 | 적합 | 일치 | 충족 | 충족 | source_text와 실행의 scenario_id 저장을 확인하며 FK를 만들지 않는다. |
| 시뮬레이터의 설비 점유와 대상별 결과 | 적합 | 일치 | 충족 | 충족 | 부분 유일 인덱스·문자열 설비 ID·실행 참조를 대조한다. |

모든 제목을 읽어 의미 없는 접미사 반복·이름 토막 뜻풀이·번호 분할을 발견하지 않았다. 식별자를 지워 같은 제목 모양이 세 번 이상 반복되는 틀도 없다. 본문 표의 테이블 행은 각 테이블을 한 번씩 소유한다. 초기 표 설명과 백업 범위는 하나의 절에 두었다. admin 구획이며 user 문서로 관리 조치가 유출되는 변경은 없다. 마스킹 전후 사실 소실·잘린 문장·미해소 지적은 이 검사 범위에서 0건이다.

## 절별 사실 대조(보완 반영 최종본)

아래 표는 최종 검색 원문의 본문 문단 18개와 테이블 자료 행 60개를 각각 실제 원천에 대응한다. 형식 안내와 문서 연결 문장도 제외하지 않았다. 코드의 칼럼 매핑만으로 현장 값이나 미확인 업무 의미를 추가하지 않았다.

설비 확정 이력 문장의 소비자도 직접 확인했다. `nori-tc-gateway/gateway/tc-gateway-core/src/main/java/com/nori/tc/gateway/core/coordination/CoordinatedGatewayBootstrapOverlayPolicy.java @ d3636ab86763 L77~114`는 DB 확정 revision을 읽고, Redis overlay가 없는데 DB 이력이 양수이거나 Redis revision이 DB보다 작으면 설비 적재를 차단한다. 원문은 비교 기준 칼럼만 설명하고 장애·복구 절차를 중복하지 않는다. Backend의 `ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/coordination/UiOperationExecutor.java @ 985d17c604dd L595~608`에서 확정 revision 기록 포트를 호출하는 경로와 common의 `TcEqpJpaRepository.java @ 6fe71f6b2da6 L54~76` 조건부 갱신도 읽었다.

### 테이블 목록과 DB 백업 범위

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L19 | 아래에 열거한 `public`의 TC 업무 테이블은 `nori_tc` 전체 DB 백업에 포함하는 대상이다. 모델·설비 기준정보와 함께 작업·현재 상태·발행 대기·배포·인증·이력·시뮬레이터 자료도 보존한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; public 업무 테이블 L94~1238<br>nori-tc-exec/db/migration/V2__add_ui_audit_log.sql @ f1d5e0a8e727 ; 감사 테이블 L12~25<br>nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 비밀번호 이력 L32~44<br>nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 실행 테이블 L31~95<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전체 DB 백업 L2194~2217 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |
| L21 | 표의 기본 키는 행을 식별하는 칼럼이다. 담는 자료 칸에는 조회에 필요한 연결 칼럼과 중복 금지 조합을 함께 적는다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; public 업무 테이블 L94~1238<br>nori-tc-exec/db/migration/V2__add_ui_audit_log.sql @ f1d5e0a8e727 ; 감사 테이블 L12~25<br>nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 비밀번호 이력 L32~44<br>nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 실행 테이블 L31~95<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전체 DB 백업 L2194~2217 | 문서의 표 규약이다. 각 자료 행의 실제 기본 키 및 연결·중복 제약을 아래 전수 대조했다. |
| L23 | 백업 생성·복원 절차는 `00.rag/admin/backup-and-recovery.md`에서 다룬다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; public 업무 테이블 L94~1238<br>nori-tc-exec/db/migration/V2__add_ui_audit_log.sql @ f1d5e0a8e727 ; 감사 테이블 L12~25<br>nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 비밀번호 이력 L32~44<br>nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 실행 테이블 L31~95<br>nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전체 DB 백업 L2194~2217<br>nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/backup-and-recovery.md @ sha256=d288b2078c77c75b9c41c7e7d57561bffd385c9bbe3f197bf8435c0c4584fb12 L1~221 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 모델 이름·버전·공유 파라미터

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L34 | tc_model / model_key / 모델명·통신 방식·제조사·상위 모델명. model_name은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L94~110<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/model/TcModelJpaSql.java @ 6fe71f6b2da6 L16~91 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L35 | tc_model_version / model_version_key / model_key에 속한 버전·상태·설명. model_key와 model_version 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L117~133<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/model/TcModelJpaSql.java @ 6fe71f6b2da6 L16~91 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L36 | tc_model_param / model_param_key / model_version_key별 param_name·param_value. 같은 버전 안의 파라미터명은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L138~150<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelParamEntity.java @ 6fe71f6b2da6 L39~52 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 모델별 SECS·Socket·MES 메시지 목록

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L48 | tc_model_secs_message / secs_msg_key / 모델 버전별 SECS 메시지명 secs_msg_name과 설명. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L154~165<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelSecsMessageEntity.java @ 6fe71f6b2da6 L45~55 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L49 | tc_model_socket_message / socket_msg_key / 모델 버전별 Socket 메시지명 socket_msg_name과 설명. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L170~181<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelSocketMessageEntity.java @ 6fe71f6b2da6 L42~52 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L50 | tc_model_mes_message / mes_msg_key / 모델 버전별 MES 메시지명 mes_msg_name과 설명. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L186~197<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMesMessageEntity.java @ 6fe71f6b2da6 L45~55 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L52 | 세 테이블은 각각 `model_version_key`로 모델 버전에 연결하며 같은 버전 안의 메시지명 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 메시지 정의 L154~199<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelSecsMessageEntity.java @ 6fe71f6b2da6 ; SECS 매핑 L45~55<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelSocketMessageEntity.java @ 6fe71f6b2da6 ; Socket 매핑 L42~52<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMesMessageEntity.java @ 6fe71f6b2da6 ; MES 매핑 L45~55 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 모델의 변수·리포트·이벤트 정의

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L64 | tc_model_variableid / variable_key / model_version_key별 변수 ID·종류. 버전·variable_id_type·variable_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L201~214<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelVariableIdEntity.java @ 6fe71f6b2da6 L46~60 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L65 | tc_model_reportid / report_key / model_version_key별 report_id·variable_id·사용 여부. 버전 안의 report_id는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L219~233<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelReportIdEntity.java @ 6fe71f6b2da6 L41~57 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L66 | tc_model_eventid / event_key / model_version_key별 event_id·report_id·사용 여부. 버전 안의 event_id는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L238~252<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelEventIdEntity.java @ 6fe71f6b2da6 L45~61 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 메시지 흐름과 데이터 수집 항목의 연결

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L79 | tc_model_message_flow / message_flow_key / 모델 버전별 workflow_name·message_name·필터·액션·입력 색인. model_version_key·workflow_name·message_name 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L257~273<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMessageFlowEntity.java @ 6fe71f6b2da6 L50~75 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L272의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L80 | tc_model_dcop_item / dcop_item_key / 모델 버전별 수집 항목명·변수 ID·수집 규칙·계산 규칙·순서. model_version_key·dcop_item_name 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L293~309<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelDcopItemEntity.java @ 6fe71f6b2da6 L47~70 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L307의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L81 | tc_model_message_flow_dcop_item / mfdi_key / message_flow_key와 dcop_item_key를 연결한다. 두 키 조합의 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L314~327<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMessageFlowDcopItemEntity.java @ 6fe71f6b2da6 L50~63 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### PLC 읽기 그룹과 응답 해석 정의

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L92 | tc_model_plc_group / group_key / model_version_key별 메시지명·읽기 영역·시작 주소·개수·poll_period_ms. 주기는 밀리초다. model_version_key·message_name 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L333~353<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcGroupEntity.java @ 6fe71f6b2da6 L46~72 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L348의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L93 | tc_model_plc_register / register_key / group_key별 변수명·워드 위치·데이터 타입·길이·바이트와 워드 순서·배율. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L358~384<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcRegisterEntity.java @ 6fe71f6b2da6 L51~92 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L95 | 그룹은 모델 버전에, 레지스터는 그룹에 연결한다. 그룹 안의 `variable_name` 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; PLC 정의 L332~386<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcGroupEntity.java @ 6fe71f6b2da6 ; 그룹 매핑 L46~72<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcRegisterEntity.java @ 6fe71f6b2da6 ; 레지스터 매핑 L51~92 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 모델 첨부 파일과 설비 플러그인 자료

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L108 | tc_model_mdf / mdf_key / 모델 버전에 첨부한 MDF 자료. model_version_key가 유일해 버전당 한 행이다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L278~289<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMdfEntity.java @ 6fe71f6b2da6 L33~46 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L109 | tc_jar_gateway / gateway_jar_key / Gateway 플러그인 파일 자료. tc_eqp.gateway_jar_key가 참조한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L461~471<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarGatewayEntity.java @ 6fe71f6b2da6 L35~48 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L110 | tc_jar_business / business_jar_key / Business 플러그인 파일 자료. tc_eqp.business_jar_key가 참조한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L473~483<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarBusinessEntity.java @ 6fe71f6b2da6 L35~48 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L112 | 파일 본문 칼럼과 복원 범위의 구별은 `00.rag/admin/backup-and-recovery.md`의 MDF·JAR 절에서 다룬다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; MDF 정의 L278~291<br>nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 플러그인과 설비 참조 L461~491<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMdfEntity.java @ 6fe71f6b2da6 ; 파일 매핑 L33~46<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarGatewayEntity.java @ 6fe71f6b2da6 ; Gateway 파일 매핑 L35~48<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarBusinessEntity.java @ 6fe71f6b2da6 ; Business 파일 매핑 L35~48<br>nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/backup-and-recovery.md @ sha256=d288b2078c77c75b9c41c7e7d57561bffd385c9bbe3f197bf8435c0c4584fb12 L1~221 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 라인과 설비 기본정보의 조회 키

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L123 | tc_line / line_key / 라인 코드·표시 문구·설명·사용 여부. line_code는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L402~414<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/line/TcLineEntity.java @ 6fe71f6b2da6 L49~62 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L124 | tc_eqp / eqp_key / 설비 ID·라인·통신 방식·주소·모델 버전·파라미터 버전·플러그인 키·사용 여부. eqp_id는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L418~450<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpEntity.java @ 6fe71f6b2da6 L53~94 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L126 | `tc_eqp.line_key`는 라인, `model_version_key`는 모델 버전을 참조한다. 설비 ID인 `eqp_id`와 DB 행 키인 `eqp_key`를 구별한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 라인·설비 L402~459<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/line/TcLineEntity.java @ 6fe71f6b2da6 ; 라인 매핑 L49~62<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpEntity.java @ 6fe71f6b2da6 ; 설비 매핑 L53~94 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 설비별 HSMS·Socket·PLC 통신 설정

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L138 | tc_eqp_hsms / eqp_key / HSMS 장치 번호·타이머·링크 확인·최대 메시지 크기. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L493~519<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpHsmsEntity.java @ 6fe71f6b2da6 L40~68 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L139 | tc_eqp_socket / eqp_key / Socket 프레이밍 규격 코드·문자 집합·heartbeat·읽기와 쓰기 제한·최대 프레임 크기. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L521~545<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpSocketEntity.java @ 6fe71f6b2da6 L42~67 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L140 | tc_eqp_plc / eqp_key / PLC 하위 프로토콜·전송 방식·unit ID·폴링 제한·재연결 횟수·활성 폴링 그룹. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L549~569<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpPlcEntity.java @ 6fe71f6b2da6 L39~64 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L142 | 세 테이블은 각각 `eqp_key`가 `tc_eqp`를 참조하는 기본 키여서 설비당 한 행이다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 설비 통신 L493~569<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpHsmsEntity.java @ 6fe71f6b2da6 ; HSMS 매핑 L40~68<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpSocketEntity.java @ 6fe71f6b2da6 ; Socket 매핑 L42~67<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpPlcEntity.java @ 6fe71f6b2da6 ; PLC 매핑 L39~64 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### Socket 규격 목록과 설비 로그 설정

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L155 | tc_eqp_socket_protocol_type / socket_protocol_type / Socket 규격의 표시 문구·시작 규칙·끝 규칙·정규식·설명. tc_eqp_socket이 규격 코드로 참조한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L391~400<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpSocketProtocolTypeEntity.java @ 6fe71f6b2da6 L31~47 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L156 | tc_eqp_log / eqp_key / 설비별 로그 레벨·보관 일수·저장 경로 설정. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L610~622<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpLogEntity.java @ 6fe71f6b2da6 L36~47 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 설비 파라미터 값·버전 설명·전역 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L168 | tc_eqp_param / eqp_param_key / 설비별 param_name·param_version·param_value. eqp_key·param_name·param_version 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L648~662<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpParamEntity.java @ 6fe71f6b2da6 L42~61 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L169 | tc_eqp_param_version / eqp_param_version_key / 설비별 param_version과 버전 전체 설명. eqp_key·param_version 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L667~681<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpParamVersionEntity.java @ 6fe71f6b2da6 L34~50 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L170 | tc_eqp_global / eqp_global_key / 설비별 전역 param_name·param_value. eqp_key·param_name 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L685~696<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpGlobalEntity.java @ 6fe71f6b2da6 L44~54 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 설비 현재 상태·포트 상태·변경 이력

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L183 | tc_eqp_state / eqp_key / 설비의 현재 제어 상태·운전 상태·연결 의도·사유. tc_eqp를 참조한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L571~587<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpStateEntity.java @ 6fe71f6b2da6 L51~73 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L184 | tc_eqp_port_status / eqp_port_status_key / eqp_key별 포트·Carrier의 현재 상태. eqp_key·port_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L624~642<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpPortStatusEntity.java @ 6fe71f6b2da6 L51~76 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L185 | tc_eqp_state_hist / state_hist_key / eqp_key별 변경 전·후 상태·변경 시각·종류·사유를 담는 이력. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L589~603<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpStateHistEntity.java @ 6fe71f6b2da6 L43~66 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L187 | 상태값의 뜻은 `00.rag/user/state-reference.md`에서 다룬다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 상태·이력 L571~608<br>nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 포트 상태 L624~646<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpStateEntity.java @ 6fe71f6b2da6 ; 상태 매핑 L51~73<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpPortStatusEntity.java @ 6fe71f6b2da6 ; 포트 매핑 L51~76<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpStateHistEntity.java @ 6fe71f6b2da6 ; 이력 매핑 L43~66<br>nori-ai-gateway/rag-documents/nori-tc/00.rag/user/state-reference.md @ sha256=bcc635d7e1900a203b5bfd79dbe0d37007b59dae57d01a82df5ad06bd28271ce L1~317 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 설비의 DB 확정 이력을 보존하는 칼럼

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L197 | V7을 반영한 스키마의 `tc_eqp.last_finalized_revision`은 DB 확정이 끝난 마지막 revision을 담는다. `NULL`은 확정 이력이 없음을 나타낸다. | nori-tc-exec/db/migration/V7__add_tc_eqp_last_finalized_revision.sql @ f1d5e0a8e727 ; 확정 이력 칼럼 L15~23<br>nori-tc-exec/db/migration/V7__add_tc_eqp_last_finalized_revision.sql @ f1d5e0a8e727 ; 칼럼 추가 L34~38<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpEntity.java @ 6fe71f6b2da6 ; 확정 revision 매핑 L96~104<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd ; 확정 이력 조회·기록 L40~63 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |
| L199 | 이 칼럼은 Redis 조정 자료가 DB보다 과거인지 비교할 때 쓰므로 설비 기본정보와 함께 보존한다. Redis 원장 키와 복구 판단은 `00.rag/admin/coordination-ownership-and-recovery-check.md`에서 다룬다. | nori-tc-exec/db/migration/V7__add_tc_eqp_last_finalized_revision.sql @ f1d5e0a8e727 ; 확정 이력 칼럼 L15~23<br>nori-tc-exec/db/migration/V7__add_tc_eqp_last_finalized_revision.sql @ f1d5e0a8e727 ; 칼럼 추가 L34~38<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpEntity.java @ 6fe71f6b2da6 ; 확정 revision 매핑 L96~104<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd ; 확정 이력 조회·기록 L40~63<br>nori-ai-gateway/rag-documents/nori-tc/00.rag/admin/coordination-ownership-and-recovery-check.md @ sha256=2677c882740a639cc89cf5d6e7869936b3a1fbad95dad88ef61b119527440a0d L1~255 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 작업 원본과 작업별 파라미터

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L210 | tc_work / work_key / eqp_key별 작업 ID·작업자 ID·공정 순번·상태·시작과 종료 시각·MES 원문. eqp_key·work_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L703~721<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkEntity.java @ 6fe71f6b2da6 L63~88 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L211 | tc_work_param / work_param_key / work_key별 param_name·param_value. 같은 작업 안의 파라미터명은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L727~738<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkParamEntity.java @ 6fe71f6b2da6 L40~50 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 작업의 Carrier·슬롯·Lot 자료

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L223 | tc_work_carrier / work_carrier_key / work_key별 Carrier ID·포트·슬롯맵·전체/양품/불량 수량. work_key·carrier_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L742~760<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkCarrierEntity.java @ 6fe71f6b2da6 L50~72 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L756의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L224 | tc_work_carrier_slot / carrier_slot_key / work_carrier_key별 슬롯 번호·상태·Lot ID. 같은 Carrier 행 안의 slot_no는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L766~779<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkCarrierSlotEntity.java @ 6fe71f6b2da6 L43~56 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L225 | tc_work_lot / work_lot_key / work_key별 Lot ID·Carrier ID·상위 Lot ID·챔버 ID. work_key·lot_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L784~797<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkLotEntity.java @ 6fe71f6b2da6 L40~56 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### Control Job·Process Job과 Lot 연결

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L237 | tc_work_controljob / control_job_key / work_key에 속한 controljob_id와 상태. work_key·controljob_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L803~815<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkControlJobEntity.java @ 6fe71f6b2da6 L50~60 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L814의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L238 | tc_work_processjob / process_job_key / control_job_key에 속한 processjob_id·상태·recipe_id. control_job_key·processjob_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L820~833<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkProcessJobEntity.java @ 6fe71f6b2da6 L48~61 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L832의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L239 | tc_work_processjob_lot_map / pj_lot_map_key / process_job_key와 work_lot_key의 연결·역할·방향. 두 키 조합의 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L838~854<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkProcessjobLotMapEntity.java @ 6fe71f6b2da6 L54~68 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 배포 전체와 대상별 실행 명세

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L249 | tc_deploy / deploy_key / 명령 종류·전체 상태·공통 목표·시나리오 카탈로그 버전·대상 집계. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L862~886<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd L66~188 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L250 | tc_deploy_target_plan / deploy_target_key / deploy_key별 설비 ID·목표·변경 차이·확정 실행 단계. deploy_key·eqp_id 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L892~912<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd L66~188 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L252 | `tc_deploy_target_plan.eqp_key`는 비어 있을 수 있으며 설비 테이블 외래 키 제약이 없다. 대상 명세의 `eqp_id`도 함께 대조한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 배포·대상 명세 L862~917<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; 배포와 대상 저장 L66~188 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 배포 대상의 진행 상태와 이전 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L263 | tc_deploy_target_state / deploy_target_key / 대상의 현재 단계·시도 횟수·추적 ID·오류·시작과 종료 시각. 대상 명세와 같은 키를 쓴다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L919~944<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd L172~240 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L264 | tc_deploy_target_snapshot / deploy_target_snapshot_key / deploy_target_key별 이전 상태 JSON·범위·단계 코드·캡처 시각. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L950~965<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd L409~490 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L266 | 두 테이블의 `deploy_target_key`는 `tc_deploy_target_plan`을 참조한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 배포 진행·스냅샷 L919~969<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 진행 갱신 L172~240<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 스냅샷 저장·조회 L409~490 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 배포 시나리오 카탈로그의 판본 기록

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L277 | tc_deploy_scenario_catalog_meta / catalog_version / 카탈로그 체크섬·시나리오 건수·적재 시각·적재 주체·다시 읽은 사유. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L971~980<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployScenarioCatalogMetaJdbcPort.java @ 985d17c604dd L27~80 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L279 | `tc_deploy.scenario_catalog_version`에는 배포 계획이 사용한 카탈로그 판본을 적는다. 두 테이블 사이에는 외래 키 제약이 없다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 카탈로그 기록 L971~983<br>nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 배포 카탈로그 판본 L862~887<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployScenarioCatalogMetaJdbcPort.java @ 985d17c604dd ; 최근 판본 조회·추가 L27~80 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### Kafka 발행 대기와 전송 시도 기록

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L290 | tc_msg_send_queue / msg_key / Kafka 토픽·메시지 본문·헤더·발행 상태·재시도·점유 정보. topic·idempotency_key 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1111~1130<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/outbox/TcMsgSendQueueEntity.java @ 6fe71f6b2da6 L63~95 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L291 | tc_msg_send_log / send_log_key / msg_key별 시도 번호·결과·Kafka 파티션과 오프셋·오류·시각. msg_key는 발행 대기 행을 참조한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1136~1152<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/outbox/TcMsgSendLogEntity.java @ 6fe71f6b2da6 L44~70 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 사용자 계정과 로그인 세션

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L304 | tc_user_info / user_pk / 소속·로그인 ID·정규화 ID·비밀번호 해시·계정 상태·최종 비밀번호 변경 시각. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L988~1007<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserInfoEntity.java @ 6fe71f6b2da6 L51~80 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L305 | tc_ui_auth_session / token / user_pk에 속한 로그인 세션의 발급·만료·마지막 사용 시각과 폐기 여부. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1090~1102<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUiAuthSessionEntity.java @ 6fe71f6b2da6 L35~51 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L307 | `tc_user_info.user_id_norm`과 `email`은 각각 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 사용자 L988~1011<br>nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 로그인 세션 L1090~1106<br>nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 변경 시각 L20~29<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserInfoEntity.java @ 6fe71f6b2da6 ; 계정 매핑 L51~80<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUiAuthSessionEntity.java @ 6fe71f6b2da6 ; 세션 매핑 L35~51 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 사용자 그룹·소속·권한 연결

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L319 | tc_user_group / group_id / 그룹 코드·표시 문구·설명·사용 여부. group_code는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1038~1050<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserGroupEntity.java @ 6fe71f6b2da6 L49~62 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L320 | tc_user_group_member / ugm_key / user_pk와 group_id의 소속 연결·부여 시각·부여 주체. 두 키 조합의 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1054~1067<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserGroupMemberEntity.java @ 6fe71f6b2da6 L50~63 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L321 | tc_user_group_permission / ugp_key / group_id와 perm_id의 권한 연결·부여 시각·부여 주체. 두 키 조합의 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1072~1085<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserGroupPermissionEntity.java @ 6fe71f6b2da6 L48~61 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 권한 정의·감사 기록·비밀번호 이력

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L335 | tc_ui_permission / perm_id / 권한 코드·대상 경로·자원 종류·경로 매칭 방식·HTTP 메서드·사용 여부. perm_code는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1013~1033<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUiPermissionEntity.java @ 6fe71f6b2da6 L58~85 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L1029의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L336 | tc_ui_audit_log / audit_pk / 행위 주체·행위·대상·결과·발생 시각·부가 정보. | nori-tc-exec/db/migration/V2__add_ui_audit_log.sql @ f1d5e0a8e727 L11~20<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JdbcAuditLogPort.java @ 985d17c604dd L27~49 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L337 | tc_user_password_history / history_pk / user_pk별 과거 비밀번호 해시와 변경 시각. 재사용 검사에서 최근 이력을 읽는다. | nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 L32~40<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JdbcPasswordHistoryPort.java @ 985d17c604dd L22~57 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L339 | 개별 권한 코드의 뜻은 `00.rag/user/permission-code-reference.md`에서 다룬다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 권한 정의 L1013~1036<br>nori-tc-exec/db/migration/V2__add_ui_audit_log.sql @ f1d5e0a8e727 ; 감사 기록 L12~25<br>nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 비밀번호 이력 L32~44<br>nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUiPermissionEntity.java @ 6fe71f6b2da6 ; 권한 매핑 L58~85<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JdbcAuditLogPort.java @ 985d17c604dd ; 감사 기록 저장 L27~49<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JdbcPasswordHistoryPort.java @ 985d17c604dd ; 해시 이력 조회·저장 L22~57<br>nori-ai-gateway/rag-documents/nori-tc/00.rag/user/permission-code-reference.md @ sha256=131fbac48bca68717480747cef989758c643e8440c5577aa1cd7689036c8ace8 L1~261 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### Live-Map 객체·캔버스·배치

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L350 | tc_live_map_object / object_key / 재사용 객체의 표시 문구·종류·shape_json·사용 여부. object_name은 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1161~1174<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/LiveMapObjectJdbcStorePort.java @ 985d17c604dd L39~71 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. 보완에서 V1 L1171의 UNIQUE 제약과 추가 문장을 직접 대조했다. |
| L351 | tc_live_map_layout / layout_key / line_key별 canvas_json. line_key가 유일해 라인당 한 행이다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1179~1190<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/LiveMapLayoutJdbcStorePort.java @ 985d17c604dd L41~122 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L352 | tc_live_map_placement / placement_key / layout_key 위의 object_key·설비 연결 eqp_key·배치 transform_json. 설비 연결은 비어 있을 수 있다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1195~1211<br>nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/LiveMapLayoutJdbcStorePort.java @ 985d17c604dd L41~122 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |

### 시뮬레이터 시나리오 원문과 실행 기록

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L364 | tc_simulator_scenario / simulator_scenario_key / scenario_id·버전·통신 방식·지원 모드·source_text 원문·사용 여부·기본 제공 여부. scenario_id는 중복을 금지한다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 L1220~1238<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/JdbcScenarioRepository.java @ 5e9b02e3ae16 L29~54 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L365 | tc_sim_run / run_id / 실행 모드·scenario_id·상태·소유 인스턴스·마지막 heartbeat 시각. | nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 L32~45<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 L44~74 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L367 | `tc_sim_run.scenario_id`에는 시나리오 테이블을 향한 외래 키 제약이 없다. | nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 시나리오 L1220~1238<br>nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 실행 L31~49<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/JdbcScenarioRepository.java @ 5e9b02e3ae16 ; 원문·목록 조회 L29~54<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 실행 저장 L44~74 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

### 시뮬레이터의 설비 점유와 대상별 결과

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| L378 | tc_sim_run_lease / sim_run_lease_key / run_id별 설비 점유와 released 여부. 미해제 행은 같은 eqp_key의 중복을 금지한다. | nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 L53~64<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 L60~74<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 L118~138 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L379 | tc_sim_run_target / sim_run_target_key / run_id별 설비 상태·통신 방식·완료와 오류 사이클 수. run_id·eqp_key 조합은 중복을 금지한다. | nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 L75~92<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 L60~74<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 L118~138 | 기본 키는 PRIMARY KEY와 일치한다. 자료 칼럼·연결·본문에 적은 유일 제약을 DDL 및 매핑·SQL과 대조했다. |
| L381 | 두 테이블의 `run_id`는 실행을 참조한다. 두 테이블의 `eqp_key`는 문자열 설비 ID이며 `tc_eqp`의 숫자 기본 키를 참조하지 않는다. | nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 점유·대상 L51~95<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 점유·대상 저장 L60~74<br>nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 대상 갱신·점유 해제 L118~138 | 문단의 각 조건·칼럼 관계와 연결 문서의 실제 존재·소유 절을 대조했다. |

## 2026-11-19 유일 제약 누락 보완

메인 세션이 발견한 결함 1건은 8개 테이블의 유일 제약 누락이다. 최초 자체 검사는 테이블·기본 키와 본문에 채택한 제약의 정확성을 대조했으나, 모든 UNIQUE 정의를 본문의 중복 금지 서술에 역방향으로 연결하지 못했다. 최초 기록의 전수 정합성 판정은 이 누락을 찾지 못한 검사 범위의 한계가 있었으며, 해당 판정을 보완 완료 근거로 재사용하지 않는다.

중단 후 검색 원문과 작성 기록을 처음부터 다시 읽었다. 두 파일은 보완 전 SHA를 유지했고 누락 문장은 반영되지 않은 상태였다. `git -c safe.directory=C:/workspace_nori/nori-tc/nori-tc-exec -C ../nori-tc-exec show f1d5e0a8e727:db/migration/V1__baseline.sql`로 8개 제약을 직접 다시 읽었다. 같은 커밋의 V1~V8을 모두 읽어 UNIQUE 제약 41개와 UNIQUE INDEX 1개를 다시 셌다. migration 파일의 작업 트리는 변경 없이 해당 커밋 내용과 일치했다.

검색 원문 L79·80·92·223·237·238·335·350에만 중복 금지 조건을 추가했다. 표의 테이블명·기본 키·기존 칼럼 설명·외래 키·NULL 서술은 보존했다. 8개 제약 줄은 각 절의 기존 rag-meta 범위 안에 있으므로 원천 범위와 절 구성은 바꾸지 않았다. 8개 추가 문장만 제거하면 보완 전 검색 원문 SHA `21d275b86c05a23b290ce97839ceb9690a0f10c9de50bdcd3dde58ec69e8ffff`가 재현됨을 확인했다(`ONLY_EIGHT_ADDITIONS_PASS`). 최종본은 381줄·26절·본문 자식 78개를 유지한다.

사람 검사는 추가한 8개 조건을 실제 UNIQUE의 칼럼 순서·조합과 대조하고 변경된 6절 전체를 다시 읽었다. 제목·독립 답변·다른 절과의 소유 경계를 유지하며, 42개 정의를 아래 표의 본문 위치에 각각 연결했다. 추가 누락은 0건이다. 이는 문서 서술의 검사이며 현장 DB 적용·제품 실행 시험의 성공을 뜻하지 않는다.

새 검사기 경로는 `C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh`다. 기본 실행은 exit 1·`violations=101`이었고 모든 지적은 `원천 commit:path 없음`이었다. 원천이 사라진 것으로 판단하지 않았다. `RagDocCheck.java` L385~412는 자식 Git 명령이 실패하면 stderr를 버리고 같은 문구를 반환한다. 실제 `git -C ../nori-tc-exec cat-file -e f1d5e0a8e727:db/migration/V1__baseline.sql`은 `fatal: detected dubious ownership`을 출력했다. 새 check.sh에는 최초 작성 때의 프로세스 한정 safe.directory 설정이 없음을 확인했다.

검사기·전역 Git 설정·제품 파일은 수정하지 않았다. 재실행 프로세스의 `GIT_CONFIG_COUNT=4`와 `GIT_CONFIG_KEY_0`~`GIT_CONFIG_KEY_3`를 `safe.directory`로 지정하고 각 `GIT_CONFIG_VALUE`에는 exec·common·ui-backend·simulator의 정확한 저장소 절대경로를 공급했다. 이 설정은 검사기의 Git 읽기 자식 프로세스에만 적용한다.

변경 비교용 원문 전체를 명령 인자로 넘기려던 한 호출은 Windows `CreateProcessAsUserW failed: 206 (파일 이름이나 확장명이 너무 깁니다.)`로 프로세스 시작 전에 실패했다. 파일을 만들지 않았고, 8개 추가 문장만 메모리에서 제거한 뒤 이전 SHA와 대조하는 짧은 명령으로 확인했다. 검사용 임시 파일·별도 Agent·서버 접속·제품 변경·커밋은 없다.

## 유일 제약 전체와 최종 원문 대응

원천 판본은 모두 `nori-tc-exec @ f1d5e0a8e727`이다. 아래 각 원천 줄이 해당 절 rag-meta의 migration 줄 범위에 포함됨도 확인했다. 전체는 V1의 UNIQUE 40개, V6의 UNIQUE 1개·부분 UNIQUE INDEX 1개이며 V2~V5·V7·V8에는 추가 정의가 없다.

| 테이블 | 중복 금지 칼럼·조건 | 원천 위치 | 검색 원문 위치 |
|---|---|---|---|
| tc_model | model_name | db/migration/V1__baseline.sql L106 | L34 |
| tc_model_version | model_key, model_version | db/migration/V1__baseline.sql L131 | L35 |
| tc_model_param | model_version_key, param_name | db/migration/V1__baseline.sql L149 | L36 |
| tc_model_secs_message | model_version_key, secs_msg_name | db/migration/V1__baseline.sql L164 | L52 |
| tc_model_socket_message | model_version_key, socket_msg_name | db/migration/V1__baseline.sql L180 | L52 |
| tc_model_mes_message | model_version_key, mes_msg_name | db/migration/V1__baseline.sql L196 | L52 |
| tc_model_variableid | model_version_key, variable_id_type, variable_id | db/migration/V1__baseline.sql L212 | L64 |
| tc_model_reportid | model_version_key, report_id | db/migration/V1__baseline.sql L231 | L65 |
| tc_model_eventid | model_version_key, event_id | db/migration/V1__baseline.sql L250 | L66 |
| tc_model_message_flow | model_version_key, workflow_name, message_name | db/migration/V1__baseline.sql L272 | L79 |
| tc_model_mdf | model_version_key | db/migration/V1__baseline.sql L288 | L108 |
| tc_model_dcop_item | model_version_key, dcop_item_name | db/migration/V1__baseline.sql L307 | L80 |
| tc_model_message_flow_dcop_item | message_flow_key, dcop_item_key | db/migration/V1__baseline.sql L326 | L81 |
| tc_model_plc_group | model_version_key, message_name | db/migration/V1__baseline.sql L348 | L92 |
| tc_model_plc_register | group_key, variable_name | db/migration/V1__baseline.sql L378 | L95 |
| tc_line | line_code | db/migration/V1__baseline.sql L412 | L123 |
| tc_eqp | eqp_id | db/migration/V1__baseline.sql L439 | L124 |
| tc_eqp_port_status | eqp_key, port_id | db/migration/V1__baseline.sql L638 | L184 |
| tc_eqp_param | eqp_key, param_name, param_version | db/migration/V1__baseline.sql L661 | L168 |
| tc_eqp_param_version | eqp_key, param_version | db/migration/V1__baseline.sql L680 | L169 |
| tc_eqp_global | eqp_key, param_name | db/migration/V1__baseline.sql L695 | L170 |
| tc_work | eqp_key, work_id | db/migration/V1__baseline.sql L719 | L210 |
| tc_work_param | work_key, param_name | db/migration/V1__baseline.sql L737 | L211 |
| tc_work_carrier | work_key, carrier_id | db/migration/V1__baseline.sql L756 | L223 |
| tc_work_carrier_slot | work_carrier_key, slot_no | db/migration/V1__baseline.sql L777 | L224 |
| tc_work_lot | work_key, lot_id | db/migration/V1__baseline.sql L796 | L225 |
| tc_work_controljob | work_key, controljob_id | db/migration/V1__baseline.sql L814 | L237 |
| tc_work_processjob | control_job_key, processjob_id | db/migration/V1__baseline.sql L832 | L238 |
| tc_work_processjob_lot_map | process_job_key, work_lot_key | db/migration/V1__baseline.sql L852 | L239 |
| tc_deploy_target_plan | deploy_key, eqp_id | db/migration/V1__baseline.sql L910 | L250 |
| tc_user_info | user_id_norm | db/migration/V1__baseline.sql L1004 | L307 |
| tc_user_info | email | db/migration/V1__baseline.sql L1005 | L307 |
| tc_ui_permission | perm_code | db/migration/V1__baseline.sql L1029 | L335 |
| tc_user_group | group_code | db/migration/V1__baseline.sql L1048 | L319 |
| tc_user_group_member | user_pk, group_id | db/migration/V1__baseline.sql L1066 | L320 |
| tc_user_group_permission | group_id, perm_id | db/migration/V1__baseline.sql L1084 | L321 |
| tc_msg_send_queue | topic, idempotency_key | db/migration/V1__baseline.sql L1127 | L290 |
| tc_live_map_object | object_name | db/migration/V1__baseline.sql L1171 | L350 |
| tc_live_map_layout | line_key | db/migration/V1__baseline.sql L1187 | L351 |
| tc_simulator_scenario | scenario_id | db/migration/V1__baseline.sql L1235 | L364 |
| tc_sim_run_target | run_id, eqp_key | db/migration/V6__add_sim_run_tables.sql L89 | L379 |
| tc_sim_run_lease | eqp_key（released = FALSE인 행） | db/migration/V6__add_sim_run_tables.sql L67 | L378 |

## 보완 후 최종 검사 결과

최종 검사 명령은 다음과 같다. 환경변수는 이 PowerShell 프로세스와 자식 검사기에만 전달했다.

```powershell
$repos=@('nori-tc-exec','nori-tc-common','nori-tc-ui-backend','nori-tc-simulator')
$env:GIT_CONFIG_COUNT='4'
for($i=0;$i -lt $repos.Count;$i++){
  [Environment]::SetEnvironmentVariable("GIT_CONFIG_KEY_$i",'safe.directory','Process')
  [Environment]::SetEnvironmentVariable("GIT_CONFIG_VALUE_$i",("C:/workspace_nori/nori-tc/"+$repos[$i]),'Process')
}
& 'C:/Program Files/Git/bin/bash.exe' 'C:/workspace_nori/nori-tc/rag-step3-work/tools/check.sh' --dump 00.rag/admin/database-table-reference.md
```

이번 보완의 검사기 실행은 2회다. 첫 실행의 소유권 조회 실패를 보존하고, 동일 문서 바이트를 대상으로 실행 환경을 맞춘 두 번째 실행에서 exit 0을 확인했다. 검사 마지막 출력 10줄은 다음과 같다.

```text
    L364-364 표 행 144  | 테이블=tc_simulator_scenario | 기본 키=simulator_scenario_key | 담는 자료=scenario_id·버전·통신 방식·지원 모드·source_text 원문·사용 여부·기본 제공 여부. scenario_id는 중복을 금지한다.
    L365-365 표 행 83  | 테이블=tc_sim_run | 기본 키=run_id | 담는 자료=실행 모드·scenario_id·상태·소유 인스턴스·마지막 heartbeat 시각.
    L367-367 문단 50  | tc_sim_run.scenario_id에는 시나리오 테이블을 향한 외래 키 제약이 없다.
§ L369-381 [20] 시뮬레이터의 설비 점유와 대상별 결과   parent=309 children=3 sources=3
    L378-378 표 행 110  | 테이블=tc_sim_run_lease | 기본 키=sim_run_lease_key | 담는 자료=run_id별 설비 점유와 released 여부. 미해제 행은 같은 eqp_key의 중복을 금지한다.
    L379-379 표 행 118  | 테이블=tc_sim_run_target | 기본 키=sim_run_target_key | 담는 자료=run_id별 설비 상태·통신 방식·완료와 오류 사이클 수. run_id·eqp_key 조합은 중복을 금지한다.
    L381-381 문단 79  | 두 테이블의 run_id는 실행을 참조한다. 두 테이블의 eqp_key는 문자열 설비 ID이며 tc_eqp의 숫자 기본 키를 참조하지 않는다.
SUMMARY sections=26 children=78 maxChild=163 maxParent=415
CORPUS docs=49/53 sections=850 part={admin=14, user=35} type={개념=4, 사용 절차=13, 알려진 제약=2, 운영=7, 참조=11, 트러블슈팅=12}
TOTAL files=49 targets=1 violations=0
```

최종 출력의 VIOLATION·CROSS VIOLATION·MASK는 모두 0건이다. 마스킹 후 8개 유일 제약의 칼럼·조건이 보존됐음을 추가된 표 행과 검사 dump에서 대조했다. 최대 자식 163자·부모 415자로 200/450자 제한 안이며, 분할 없이 26절을 유지한다. 원문 제목과 표 구조·기본 키·관계 서술은 그대로다.

전체 41개 UNIQUE와 1개 부분 UNIQUE INDEX에 대한 `UNIQUE_COVERAGE_PASS constraints=41 unique_indexes=1 covered=42 missing=0 source_ranges=42`를 확인했다. 각 조합·조건의 의미는 실제 DDL 및 본문을 직접 대조했으며, 자동 검사의 문구 존재 확인만으로 의미 일치를 판단하지 않았다. 검색 원문의 78개 본문 줄과 작성 기록의 대조 행을 다시 연결하고 추가된 8개 요지를 동기화했다.

검색 원문 최종 SHA-256은 `74987e18617c7655be805ef93812544e26583727ff9573efe7d10f3abf503cb9`이며 381줄·26절·78자식이다. 재측정된 결과이며 보완 전 검사 이력과 구별한다. 사용량·요금과 중단 전후 전체 작업 시간은 미측정이다. 임시 파일이나 실행 자원을 만들지 않았으므로 이번 보완의 정리 대상은 없다.


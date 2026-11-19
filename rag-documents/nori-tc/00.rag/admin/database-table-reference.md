# TC DB 테이블
```rag-meta
유형: 참조
적용: nori-tc-exec f1d5e0a8e727·nori-tc-common 6fe71f6b2da6·nori-tc-ui-backend 985d17c604dd·nori-tc-simulator 5e9b02e3ae16 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/admin/backup-and-recovery.md
```

## 테이블 목록과 DB 백업 범위
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; public 업무 테이블 L94~1238
원천: nori-tc-exec/db/migration/V2__add_ui_audit_log.sql @ f1d5e0a8e727 ; 감사 테이블 L12~25
원천: nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 비밀번호 이력 L32~44
원천: nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 실행 테이블 L31~95
원천: nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 ; 전체 DB 백업 L2194~2217
```

아래에 열거한 `public`의 TC 업무 테이블은 `nori_tc` 전체 DB 백업에 포함하는 대상이다. 모델·설비 기준정보와 함께 작업·현재 상태·발행 대기·배포·인증·이력·시뮬레이터 자료도 보존한다.

표의 기본 키는 행을 식별하는 칼럼이다. 담는 자료 칸에는 조회에 필요한 연결 칼럼과 중복 금지 조합을 함께 적는다.

백업 생성·복원 절차는 `00.rag/admin/backup-and-recovery.md`에서 다룬다.

## 모델 이름·버전·공유 파라미터
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 모델 기본 정의 L94~152
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/store/model/TcModelJpaSql.java @ 6fe71f6b2da6 ; 모델·버전 조회와 저장 L16~91
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelParamEntity.java @ 6fe71f6b2da6 ; 파라미터 매핑 L39~52
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_model | model_key | 모델명·통신 방식·제조사·상위 모델명. model_name은 중복을 금지한다. |
| tc_model_version | model_version_key | model_key에 속한 버전·상태·설명. model_key와 model_version 조합은 중복을 금지한다. |
| tc_model_param | model_param_key | model_version_key별 param_name·param_value. 같은 버전 안의 파라미터명은 중복을 금지한다. |

## 모델별 SECS·Socket·MES 메시지 목록
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 메시지 정의 L154~199
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelSecsMessageEntity.java @ 6fe71f6b2da6 ; SECS 매핑 L45~55
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelSocketMessageEntity.java @ 6fe71f6b2da6 ; Socket 매핑 L42~52
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMesMessageEntity.java @ 6fe71f6b2da6 ; MES 매핑 L45~55
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_model_secs_message | secs_msg_key | 모델 버전별 SECS 메시지명 secs_msg_name과 설명. |
| tc_model_socket_message | socket_msg_key | 모델 버전별 Socket 메시지명 socket_msg_name과 설명. |
| tc_model_mes_message | mes_msg_key | 모델 버전별 MES 메시지명 mes_msg_name과 설명. |

세 테이블은 각각 `model_version_key`로 모델 버전에 연결하며 같은 버전 안의 메시지명 중복을 금지한다.

## 모델의 변수·리포트·이벤트 정의
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 변수·리포트·이벤트 L201~255
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelVariableIdEntity.java @ 6fe71f6b2da6 ; 변수 매핑 L46~60
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelReportIdEntity.java @ 6fe71f6b2da6 ; 리포트 매핑 L41~57
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelEventIdEntity.java @ 6fe71f6b2da6 ; 이벤트 매핑 L45~61
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_model_variableid | variable_key | model_version_key별 변수 ID·종류. 버전·variable_id_type·variable_id 조합은 중복을 금지한다. |
| tc_model_reportid | report_key | model_version_key별 report_id·variable_id·사용 여부. 버전 안의 report_id는 중복을 금지한다. |
| tc_model_eventid | event_key | model_version_key별 event_id·report_id·사용 여부. 버전 안의 event_id는 중복을 금지한다. |

## 메시지 흐름과 데이터 수집 항목의 연결
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 흐름 정의 L257~276
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 수집과 연결 L293~330
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMessageFlowEntity.java @ 6fe71f6b2da6 ; 흐름 매핑 L50~75
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelDcopItemEntity.java @ 6fe71f6b2da6 ; 수집 매핑 L47~70
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMessageFlowDcopItemEntity.java @ 6fe71f6b2da6 ; 연결 매핑 L50~63
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_model_message_flow | message_flow_key | 모델 버전별 workflow_name·message_name·필터·액션·입력 색인. model_version_key·workflow_name·message_name 조합은 중복을 금지한다. |
| tc_model_dcop_item | dcop_item_key | 모델 버전별 수집 항목명·변수 ID·수집 규칙·계산 규칙·순서. model_version_key·dcop_item_name 조합은 중복을 금지한다. |
| tc_model_message_flow_dcop_item | mfdi_key | message_flow_key와 dcop_item_key를 연결한다. 두 키 조합의 중복을 금지한다. |

## PLC 읽기 그룹과 응답 해석 정의
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; PLC 정의 L332~386
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcGroupEntity.java @ 6fe71f6b2da6 ; 그룹 매핑 L46~72
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelPlcRegisterEntity.java @ 6fe71f6b2da6 ; 레지스터 매핑 L51~92
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_model_plc_group | group_key | model_version_key별 메시지명·읽기 영역·시작 주소·개수·poll_period_ms. 주기는 밀리초다. model_version_key·message_name 조합은 중복을 금지한다. |
| tc_model_plc_register | register_key | group_key별 변수명·워드 위치·데이터 타입·길이·바이트와 워드 순서·배율. |

그룹은 모델 버전에, 레지스터는 그룹에 연결한다. 그룹 안의 `variable_name` 중복을 금지한다.

## 모델 첨부 파일과 설비 플러그인 자료
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; MDF 정의 L278~291
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 플러그인과 설비 참조 L461~491
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/model/TcModelMdfEntity.java @ 6fe71f6b2da6 ; 파일 매핑 L33~46
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarGatewayEntity.java @ 6fe71f6b2da6 ; Gateway 파일 매핑 L35~48
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/jar/TcJarBusinessEntity.java @ 6fe71f6b2da6 ; Business 파일 매핑 L35~48
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_model_mdf | mdf_key | 모델 버전에 첨부한 MDF 자료. model_version_key가 유일해 버전당 한 행이다. |
| tc_jar_gateway | gateway_jar_key | Gateway 플러그인 파일 자료. tc_eqp.gateway_jar_key가 참조한다. |
| tc_jar_business | business_jar_key | Business 플러그인 파일 자료. tc_eqp.business_jar_key가 참조한다. |

파일 본문 칼럼과 복원 범위의 구별은 `00.rag/admin/backup-and-recovery.md`의 MDF·JAR 절에서 다룬다.

## 라인과 설비 기본정보의 조회 키
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 라인·설비 L402~459
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/line/TcLineEntity.java @ 6fe71f6b2da6 ; 라인 매핑 L49~62
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpEntity.java @ 6fe71f6b2da6 ; 설비 매핑 L53~94
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_line | line_key | 라인 코드·표시 문구·설명·사용 여부. line_code는 중복을 금지한다. |
| tc_eqp | eqp_key | 설비 ID·라인·통신 방식·주소·모델 버전·파라미터 버전·플러그인 키·사용 여부. eqp_id는 중복을 금지한다. |

`tc_eqp.line_key`는 라인, `model_version_key`는 모델 버전을 참조한다. 설비 ID인 `eqp_id`와 DB 행 키인 `eqp_key`를 구별한다.

## 설비별 HSMS·Socket·PLC 통신 설정
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 설비 통신 L493~569
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpHsmsEntity.java @ 6fe71f6b2da6 ; HSMS 매핑 L40~68
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpSocketEntity.java @ 6fe71f6b2da6 ; Socket 매핑 L42~67
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpPlcEntity.java @ 6fe71f6b2da6 ; PLC 매핑 L39~64
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_eqp_hsms | eqp_key | HSMS 장치 번호·타이머·링크 확인·최대 메시지 크기. |
| tc_eqp_socket | eqp_key | Socket 프레이밍 규격 코드·문자 집합·heartbeat·읽기와 쓰기 제한·최대 프레임 크기. |
| tc_eqp_plc | eqp_key | PLC 하위 프로토콜·전송 방식·unit ID·폴링 제한·재연결 횟수·활성 폴링 그룹. |

세 테이블은 각각 `eqp_key`가 `tc_eqp`를 참조하는 기본 키여서 설비당 한 행이다.

## Socket 규격 목록과 설비 로그 설정
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; Socket 규격 L391~400
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 규격 참조 L534~538
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 로그 설정 L610~622
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpSocketProtocolTypeEntity.java @ 6fe71f6b2da6 ; 규격 매핑 L31~47
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpLogEntity.java @ 6fe71f6b2da6 ; 로그 설정 매핑 L36~47
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_eqp_socket_protocol_type | socket_protocol_type | Socket 규격의 표시 문구·시작 규칙·끝 규칙·정규식·설명. tc_eqp_socket이 규격 코드로 참조한다. |
| tc_eqp_log | eqp_key | 설비별 로그 레벨·보관 일수·저장 경로 설정. |

## 설비 파라미터 값·버전 설명·전역 값
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 설비 파라미터 L648~698
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpParamEntity.java @ 6fe71f6b2da6 ; 값 매핑 L42~61
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpParamVersionEntity.java @ 6fe71f6b2da6 ; 버전 매핑 L34~50
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpGlobalEntity.java @ 6fe71f6b2da6 ; 전역 값 매핑 L44~54
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_eqp_param | eqp_param_key | 설비별 param_name·param_version·param_value. eqp_key·param_name·param_version 조합은 중복을 금지한다. |
| tc_eqp_param_version | eqp_param_version_key | 설비별 param_version과 버전 전체 설명. eqp_key·param_version 조합은 중복을 금지한다. |
| tc_eqp_global | eqp_global_key | 설비별 전역 param_name·param_value. eqp_key·param_name 조합은 중복을 금지한다. |

## 설비 현재 상태·포트 상태·변경 이력
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 상태·이력 L571~608
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 포트 상태 L624~646
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpStateEntity.java @ 6fe71f6b2da6 ; 상태 매핑 L51~73
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpPortStatusEntity.java @ 6fe71f6b2da6 ; 포트 매핑 L51~76
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpStateHistEntity.java @ 6fe71f6b2da6 ; 이력 매핑 L43~66
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_eqp_state | eqp_key | 설비의 현재 제어 상태·운전 상태·연결 의도·사유. tc_eqp를 참조한다. |
| tc_eqp_port_status | eqp_port_status_key | eqp_key별 포트·Carrier의 현재 상태. eqp_key·port_id 조합은 중복을 금지한다. |
| tc_eqp_state_hist | state_hist_key | eqp_key별 변경 전·후 상태·변경 시각·종류·사유를 담는 이력. |

상태값의 뜻은 `00.rag/user/state-reference.md`에서 다룬다.

## 설비의 DB 확정 이력을 보존하는 칼럼
```rag-meta
원천: nori-tc-exec/db/migration/V7__add_tc_eqp_last_finalized_revision.sql @ f1d5e0a8e727 ; 확정 이력 칼럼 L15~23
원천: nori-tc-exec/db/migration/V7__add_tc_eqp_last_finalized_revision.sql @ f1d5e0a8e727 ; 칼럼 추가 L34~38
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/eqp/TcEqpEntity.java @ 6fe71f6b2da6 ; 확정 revision 매핑 L96~104
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/UiFinalizedRevisionDbAdapter.java @ 985d17c604dd ; 확정 이력 조회·기록 L40~63
```

V7을 반영한 스키마의 `tc_eqp.last_finalized_revision`은 DB 확정이 끝난 마지막 revision을 담는다. `NULL`은 확정 이력이 없음을 나타낸다.

이 칼럼은 Redis 조정 자료가 DB보다 과거인지 비교할 때 쓰므로 설비 기본정보와 함께 보존한다. Redis 원장 키와 복구 판단은 `00.rag/admin/coordination-ownership-and-recovery-check.md`에서 다룬다.

## 작업 원본과 작업별 파라미터
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 작업·파라미터 L703~740
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkEntity.java @ 6fe71f6b2da6 ; 작업 매핑 L63~88
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkParamEntity.java @ 6fe71f6b2da6 ; 파라미터 매핑 L40~50
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_work | work_key | eqp_key별 작업 ID·작업자 ID·공정 순번·상태·시작과 종료 시각·MES 원문. eqp_key·work_id 조합은 중복을 금지한다. |
| tc_work_param | work_param_key | work_key별 param_name·param_value. 같은 작업 안의 파라미터명은 중복을 금지한다. |

## 작업의 Carrier·슬롯·Lot 자료
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; Carrier·슬롯·Lot L742~801
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkCarrierEntity.java @ 6fe71f6b2da6 ; Carrier 매핑 L50~72
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkCarrierSlotEntity.java @ 6fe71f6b2da6 ; 슬롯 매핑 L43~56
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkLotEntity.java @ 6fe71f6b2da6 ; Lot 매핑 L40~56
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_work_carrier | work_carrier_key | work_key별 Carrier ID·포트·슬롯맵·전체/양품/불량 수량. work_key·carrier_id 조합은 중복을 금지한다. |
| tc_work_carrier_slot | carrier_slot_key | work_carrier_key별 슬롯 번호·상태·Lot ID. 같은 Carrier 행 안의 slot_no는 중복을 금지한다. |
| tc_work_lot | work_lot_key | work_key별 Lot ID·Carrier ID·상위 Lot ID·챔버 ID. work_key·lot_id 조합은 중복을 금지한다. |

## Control Job·Process Job과 Lot 연결
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; Job과 Lot 연결 L803~857
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkControlJobEntity.java @ 6fe71f6b2da6 ; Control Job 매핑 L50~60
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkProcessJobEntity.java @ 6fe71f6b2da6 ; Process Job 매핑 L48~61
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/work/TcWorkProcessjobLotMapEntity.java @ 6fe71f6b2da6 ; Lot 연결 매핑 L54~68
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_work_controljob | control_job_key | work_key에 속한 controljob_id와 상태. work_key·controljob_id 조합은 중복을 금지한다. |
| tc_work_processjob | process_job_key | control_job_key에 속한 processjob_id·상태·recipe_id. control_job_key·processjob_id 조합은 중복을 금지한다. |
| tc_work_processjob_lot_map | pj_lot_map_key | process_job_key와 work_lot_key의 연결·역할·방향. 두 키 조합의 중복을 금지한다. |

## 배포 전체와 대상별 실행 명세
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 배포·대상 명세 L862~917
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStoreSupport.java @ 985d17c604dd ; 배포와 대상 저장 L66~188
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_deploy | deploy_key | 명령 종류·전체 상태·공통 목표·시나리오 카탈로그 버전·대상 집계. |
| tc_deploy_target_plan | deploy_target_key | deploy_key별 설비 ID·목표·변경 차이·확정 실행 단계. deploy_key·eqp_id 조합은 중복을 금지한다. |

`tc_deploy_target_plan.eqp_key`는 비어 있을 수 있으며 설비 테이블 외래 키 제약이 없다. 대상 명세의 `eqp_id`도 함께 대조한다.

## 배포 대상의 진행 상태와 이전 값
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 배포 진행·스냅샷 L919~969
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 진행 갱신 L172~240
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployJdbcStorePort.java @ 985d17c604dd ; 스냅샷 저장·조회 L409~490
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_deploy_target_state | deploy_target_key | 대상의 현재 단계·시도 횟수·추적 ID·오류·시작과 종료 시각. 대상 명세와 같은 키를 쓴다. |
| tc_deploy_target_snapshot | deploy_target_snapshot_key | deploy_target_key별 이전 상태 JSON·범위·단계 코드·캡처 시각. |

두 테이블의 `deploy_target_key`는 `tc_deploy_target_plan`을 참조한다.

## 배포 시나리오 카탈로그의 판본 기록
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 카탈로그 기록 L971~983
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 배포 카탈로그 판본 L862~887
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/DeployScenarioCatalogMetaJdbcPort.java @ 985d17c604dd ; 최근 판본 조회·추가 L27~80
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_deploy_scenario_catalog_meta | catalog_version | 카탈로그 체크섬·시나리오 건수·적재 시각·적재 주체·다시 읽은 사유. |

`tc_deploy.scenario_catalog_version`에는 배포 계획이 사용한 카탈로그 판본을 적는다. 두 테이블 사이에는 외래 키 제약이 없다.

## Kafka 발행 대기와 전송 시도 기록
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 발행 대기·시도 L1111~1155
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/outbox/TcMsgSendQueueEntity.java @ 6fe71f6b2da6 ; 대기 매핑 L63~95
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/outbox/TcMsgSendLogEntity.java @ 6fe71f6b2da6 ; 시도 매핑 L44~70
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_msg_send_queue | msg_key | Kafka 토픽·메시지 본문·헤더·발행 상태·재시도·점유 정보. topic·idempotency_key 조합은 중복을 금지한다. |
| tc_msg_send_log | send_log_key | msg_key별 시도 번호·결과·Kafka 파티션과 오프셋·오류·시각. msg_key는 발행 대기 행을 참조한다. |

## 사용자 계정과 로그인 세션
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 사용자 L988~1011
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 로그인 세션 L1090~1106
원천: nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 변경 시각 L20~29
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserInfoEntity.java @ 6fe71f6b2da6 ; 계정 매핑 L51~80
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUiAuthSessionEntity.java @ 6fe71f6b2da6 ; 세션 매핑 L35~51
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_user_info | user_pk | 소속·로그인 ID·정규화 ID·비밀번호 해시·계정 상태·최종 비밀번호 변경 시각. |
| tc_ui_auth_session | token | user_pk에 속한 로그인 세션의 발급·만료·마지막 사용 시각과 폐기 여부. |

`tc_user_info.user_id_norm`과 `email`은 각각 중복을 금지한다.

## 사용자 그룹·소속·권한 연결
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 그룹과 연결 L1038~1088
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserGroupEntity.java @ 6fe71f6b2da6 ; 그룹 매핑 L49~62
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserGroupMemberEntity.java @ 6fe71f6b2da6 ; 소속 매핑 L50~63
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUserGroupPermissionEntity.java @ 6fe71f6b2da6 ; 권한 연결 매핑 L48~61
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_user_group | group_id | 그룹 코드·표시 문구·설명·사용 여부. group_code는 중복을 금지한다. |
| tc_user_group_member | ugm_key | user_pk와 group_id의 소속 연결·부여 시각·부여 주체. 두 키 조합의 중복을 금지한다. |
| tc_user_group_permission | ugp_key | group_id와 perm_id의 권한 연결·부여 시각·부여 주체. 두 키 조합의 중복을 금지한다. |

## 권한 정의·감사 기록·비밀번호 이력
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 권한 정의 L1013~1036
원천: nori-tc-exec/db/migration/V2__add_ui_audit_log.sql @ f1d5e0a8e727 ; 감사 기록 L12~25
원천: nori-tc-exec/db/migration/V3__password_expiry_reuse.sql @ f1d5e0a8e727 ; 비밀번호 이력 L32~44
원천: nori-tc-common/db/jpa/tc-db-jpa-common-schema/src/main/java/com/nori/tc/db/jpa/common/entity/user/TcUiPermissionEntity.java @ 6fe71f6b2da6 ; 권한 매핑 L58~85
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JdbcAuditLogPort.java @ 985d17c604dd ; 감사 기록 저장 L27~49
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/JdbcPasswordHistoryPort.java @ 985d17c604dd ; 해시 이력 조회·저장 L22~57
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_ui_permission | perm_id | 권한 코드·대상 경로·자원 종류·경로 매칭 방식·HTTP 메서드·사용 여부. perm_code는 중복을 금지한다. |
| tc_ui_audit_log | audit_pk | 행위 주체·행위·대상·결과·발생 시각·부가 정보. |
| tc_user_password_history | history_pk | user_pk별 과거 비밀번호 해시와 변경 시각. 재사용 검사에서 최근 이력을 읽는다. |

개별 권한 코드의 뜻은 `00.rag/user/permission-code-reference.md`에서 다룬다.

## Live-Map 객체·캔버스·배치
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; Live-Map L1160~1217
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/LiveMapObjectJdbcStorePort.java @ 985d17c604dd ; 객체 저장 L39~71
원천: nori-tc-ui-backend/ui/adapter/tc-ui-db-adapter/src/main/java/com/nori/tc/ui/adapter/db/LiveMapLayoutJdbcStorePort.java @ 985d17c604dd ; 배치 조회·저장 L41~122
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_live_map_object | object_key | 재사용 객체의 표시 문구·종류·shape_json·사용 여부. object_name은 중복을 금지한다. |
| tc_live_map_layout | layout_key | line_key별 canvas_json. line_key가 유일해 라인당 한 행이다. |
| tc_live_map_placement | placement_key | layout_key 위의 object_key·설비 연결 eqp_key·배치 transform_json. 설비 연결은 비어 있을 수 있다. |

## 시뮬레이터 시나리오 원문과 실행 기록
```rag-meta
원천: nori-tc-exec/db/migration/V1__baseline.sql @ f1d5e0a8e727 ; 시나리오 L1220~1238
원천: nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 실행 L31~49
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/application/JdbcScenarioRepository.java @ 5e9b02e3ae16 ; 원문·목록 조회 L29~54
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 실행 저장 L44~74
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_simulator_scenario | simulator_scenario_key | scenario_id·버전·통신 방식·지원 모드·source_text 원문·사용 여부·기본 제공 여부. scenario_id는 중복을 금지한다. |
| tc_sim_run | run_id | 실행 모드·scenario_id·상태·소유 인스턴스·마지막 heartbeat 시각. |

`tc_sim_run.scenario_id`에는 시나리오 테이블을 향한 외래 키 제약이 없다.

## 시뮬레이터의 설비 점유와 대상별 결과
```rag-meta
원천: nori-tc-exec/db/migration/V6__add_sim_run_tables.sql @ f1d5e0a8e727 ; 점유·대상 L51~95
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 점유·대상 저장 L60~74
원천: nori-tc-simulator/orchestrator/src/main/java/com/nori/tc/simulator/orchestrator/adapter/lease/JdbcRunLeaseRepository.java @ 5e9b02e3ae16 ; 대상 갱신·점유 해제 L118~138
```

| 테이블 | 기본 키 | 담는 자료 |
|---|---|---|
| tc_sim_run_lease | sim_run_lease_key | run_id별 설비 점유와 released 여부. 미해제 행은 같은 eqp_key의 중복을 금지한다. |
| tc_sim_run_target | sim_run_target_key | run_id별 설비 상태·통신 방식·완료와 오류 사이클 수. run_id·eqp_key 조합은 중복을 금지한다. |

두 테이블의 `run_id`는 실행을 참조한다. 두 테이블의 `eqp_key`는 문자열 설비 ID이며 `tc_eqp`의 숫자 기본 키를 참조하지 않는다.

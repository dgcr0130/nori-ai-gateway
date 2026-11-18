# TC RAG 문서 목록

## 목록의 범위와 읽는 법

2026-11-18 사용자가 확정한 53개 문서와 user 39개·admin 14개 배정을 보존한다. 본문 작성·반입은 다음 작업이다. 파일명은 [작성 규칙](rag-document-writing-rules.md)에 따라 내용으로 정한 objectKey이며 현재 업로드된 객체를 뜻하지 않는다. 버킷은 `nori-tc`이다.

user는 `[AI_DOC_READ]`, admin은 `[AI_DOC_READ, AI_ADMIN_DOC_READ]` 전부를 요구하는 목표 구획이다. 업무 권한별로 더 나누지 않는다. 메시지 조회 문서는 사용자 매뉴얼의 화면 조회만 유지하고 운영 가이드 §8.1의 로그 조회는 admin의 일상 점검으로 옮긴다는 2026-11-18 사용자 확정을 반영한다. 목록의 문서 수는 바꾸지 않는다.

원천 칸은 2026-11-18 TC 문서 목록 조사·사용자 확정의 근거를 보존한 것이다. 커밋은 당시 조사한 수정 이력이며 소속 저장소와 commit 객체의 존재를 확인했다. 이 목록 작성에서 그 수정의 효과를 새로 검증했다는 뜻이 아니다. 원천의 항목 개수·현장 적용 상태를 현재 사실로 재선언하지 않는다. 실제 본문 작성 때 코드·사용처와 다시 대조한다. 원천이 없는 사실은 검색 본문에서 생략하고 이 목록의 미확인 칸에 남긴다.

| 약칭 | 원천 위치 |
|---|---|
| U | `nori-tc-exec/docs/completion-report/07-user-manual.md` |
| O | `nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md` |
| I | `nori-tc-exec/docs/completion-report/04-interface-spec.md` |
| 구조 문서 | `nori-tc-exec/docs/Architecture/` 아래 표에 적은 경로 |
| 업무 시나리오 | `nori-tc-exec/docs/completion-report/05-work-scenario.md` |
| 006 SPEC | `nori-sdd-specs/work-items/006-ai-gateway-redesign/spec.md` |

## 확정 문서

| 문서 | objectKey | 구획 | 유형 | 답하는 질문·필요 이유 | 원천 | 원천이 없는 부분 |
|---|---|---|---|---|---|---|
| 로그인·메뉴·화면 공통 조작 | `00.rag/user/login-and-screen-basics.md` | user | 사용 절차 | 어떻게 로그인하고 비밀번호를 바꾸며 메뉴와 표를 조작하는가. 모든 화면 사용의 시작점이다. | U §2~3 | 알려진 부재 없음 |
| Live-Map 상태 보기 | `00.rag/user/live-map-status.md` | user | 사용 절차 | 라인·설비 상태를 Live-Map에서 어떻게 읽는가. 설비 담당의 조회에 필요하다. | U §7.1~7.2 | 알려진 부재 없음 |
| Live-Map 배치·구조물 편집 | `00.rag/user/live-map-layout-editing.md` | user | 사용 절차 | 설비 배치와 구조물을 어떻게 편집하고 저장하는가. 편집 권한자의 작업을 구별한다. | U §7.3 | 알려진 부재 없음 |
| 설비 등록·설정 변경·삭제 | `00.rag/user/equipment-registration-and-settings.md` | user | 사용 절차 | 설비를 등록·변경·삭제하는 화면 절차는 무엇인가. 설비 담당의 기본 작업이다. | U §4.1~4.4 | 알려진 부재 없음 |
| 설비 시작·종료·재연결·PLC 폴링 | `00.rag/user/equipment-commands-and-polling.md` | user | 사용 절차 | 시작·종료·재연결·폴링은 어디서 하며 Restart와 Reconnect는 어떻게 다른가. 명령 혼동을 줄인다. | U §4.5~4.7·§4.12~4.13 | 알려진 부재 없음 |
| 설비 모델·파라미터·JAR 갱신 | `00.rag/user/equipment-model-parameter-and-jar-update.md` | user | 사용 절차 | 저장·버전 생성·배포 중 무엇을 해야 변경이 반영되는가. 갱신 경로의 혼동을 다룬다. | U §4.8~4.11 | 알려진 부재 없음 |
| 모델 등록·branch·삭제 | `00.rag/user/model-registration-and-branching.md` | user | 사용 절차 | 모델과 branch를 어떻게 만들고 삭제하는가. 모델 담당의 버전 관리에 필요하다. | U §5.1~5.3·§5.5 | 알려진 부재 없음 |
| 모델 상세와 Flow 편집 | `00.rag/user/model-details-and-flow-editing.md` | user | 사용 절차 | 모델 상세에서 Flow를 어떻게 편집하는가. 모델 담당이 쓰는 편집 작업을 설명한다. | U §5.4 | 알려진 부재 없음 |
| 배포 등록·실행·이력·재실행 | `00.rag/user/deployment-workflow.md` | user | 사용 절차 | 변경을 배포하고 이력을 확인하거나 재실행하려면 무엇을 하는가. 변경 반영 경로이다. | U §6 | 알려진 부재 없음 |
| DLQ·격리 설비·Repair Task | `00.rag/user/dlq-quarantine-and-repair-task.md` | user | 사용 절차 | 처리 실패·격리 설비·Repair Task를 어디서 확인하고 정리하는가. 실패 처리 화면의 사용에 필요하다. | U §8; DlqReasonCode.java | 알려진 부재 없음 |
| 사용자·그룹·권한 관리 | `00.rag/user/user-group-and-permission-management.md` | user | 사용 절차 | 계정·그룹·권한을 화면에서 어떻게 관리하는가. 운영 관리자의 계정 작업이다. | U §9 | 알려진 부재 없음 |
| 데모 실행 | `00.rag/user/demo-execution.md` | user | 사용 절차 | 시뮬레이터 데모를 어떻게 실행하는가. 화면 데모 절차를 안내한다. | U §7.4 | 알려진 부재 없음 |
| 메시지 전송 결과·통신 로그 조회 | `00.rag/user/message-results-and-communication-logs.md` | user | 사용 절차 | 설비 메시지 전송 결과와 통신 로그를 화면에서 어디서 보는가. 사용자 조회 경로를 찾게 한다. | U §4.14. O §8.1은 후속 결정에 따라 일상 점검으로 이동한다. | 알려진 부재 없음 |
| AI 대화창 사용법 | `00.rag/user/ai-chat-usage.md` | user | 사용 절차 | 어떻게 질문하고 근거를 읽으며 틀린 답을 신고하는가. 피드백의 진입 경로이다. | 006 SPEC 4.8의 목표 계약 | 실제 화면·연동 동작은 해당 구현 뒤 확인해야 한다. SPEC만으로 현재 동작을 확정하지 않는다. |
| 처음 쓰는 사람 안내 | `00.rag/user/getting-started.md` | user | 개념 | 설비 담당·모델 담당·운영 관리자는 어디서 시작하는가. 독자별 첫 작업을 구별한다. | U §1.1 | 알려진 부재 없음 |
| TC 구성 요소·용어 | `00.rag/user/tc-components-and-terms.md` | user | 개념 | Gateway·Business·Shard는 무엇인가. 다른 문서를 이해하는 데 필요한 용어이다. | U 부록 C; 구조 문서 `00-system/05-glossary.md` | 원천이 뜻을 밝히지 않은 용어는 이름으로 해석하지 않는다. |
| Lot·Carrier·작업과 MES·WM 흐름 | `00.rag/user/lot-carrier-work-and-system-flow.md` | user | 개념 | Lot·Carrier·작업은 MES·WM 흐름과 어떤 관계인가. 보고 흐름을 이해하는 데 필요하다. | 업무 시나리오 | 알려진 부재 없음 |
| TC 지원 범위·하지 않는 일 | `00.rag/user/tc-supported-scope.md` | user | 개념 | TC가 지원하는 범위와 지원하지 않는 기능은 무엇인가. 없는 기능을 있다고 답하는 일을 막는다. | 구조 문서 `00-system/01-what-is-nori-tc.md` | 알려진 부재 없음 |
| 설치·기동·정지·확장 | `00.rag/admin/installation-and-service-lifecycle.md` | admin | 운영 | 설치한 서비스를 어떻게 기동·정지·확장하는가. 설치·운영 담당의 서비스 관리에 필요하다. | O §4~5·§7 | 현장 설치·배포 판본과 실제 운영값은 확인하지 않았다. |
| 업그레이드·되돌림·설정 변경 반영 | `00.rag/admin/upgrade-rollback-and-configuration-apply.md` | admin | 운영 | 버전을 올리거나 되돌리고 설정을 반영하려면 무엇을 하는가. 변경 운영 절차를 구별한다. | O §11 | 현장 배포 판본과 적용 설정은 확인하지 않았다. |
| 백업·복구 | `00.rag/admin/backup-and-recovery.md` | admin | 운영 | 무엇을 백업하고 어떻게 복원하는가. 데이터 보호 절차에 필요하다. | O §9 | 현장의 백업 주기·실제 백업 상태는 저장소 절차만으로 알 수 없다. |
| 일상 점검 | `00.rag/admin/daily-checks-and-log-inspection.md` | admin | 운영 | 매일 무엇을 점검하고 운영 로그를 어떻게 조회하는가. 일상 점검과 내부 로그 확인의 경로이다. | O §8.3~8.4; 후속 결정으로 옮긴 O §8.1 | 현장 관측값·로그 보관·수집의 실제 적용 상태는 확인하지 않았다. |
| 보안 운영 | `00.rag/admin/security-operations.md` | admin | 운영 | Secret·인증서·계정 자격을 어떻게 관리하고 교체하는가. 보안 운영 담당의 작업이다. | O §12 | 현장 자격·인증서 만료일 등 실제 값은 없다. 비밀값은 수록하지 않는다. |
| 조정 작업·소유권·복구 필요 확인 | `00.rag/admin/coordination-ownership-and-recovery-check.md` | admin | 운영 | 다중 서버의 소유권과 복구 필요 상태를 어떻게 확인하는가. 조정 운영 절차가 필요하다. | O §7.3·§11.5; nori-tc-gateway 62154da0; nori-tc-ui-backend b8faaead | 현장 배포 판본·현재 소유권 상태는 확인하지 않았다. |
| 장애 문의 때 넘길 근거·인계 | `00.rag/admin/incident-evidence-handover.md` | admin | 운영 | 장애 문의 때 어떤 근거를 넘겨야 하는가. 담당자가 조사할 수 있는 인계를 돕는다. | O §10.1 | 실제 담당자·연락처·당번은 원천에 없다. |
| 로그인·권한·화면 오류 | `00.rag/user/login-permission-and-screen-troubleshooting.md` | user | 트러블슈팅 | 로그인·권한·화면 오류를 어떻게 구별하는가. 화면 사용 실패의 점검 경로이다. | U §10.1·§10.3; nori-tc-ui-front 335473ea | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| Live-Map | `00.rag/user/live-map-troubleshooting.md` | user | 트러블슈팅 | Live-Map 표시·조작 문제는 어떻게 확인하는가. 화면의 주의 상황을 다룬다. | U §7의 주의 사항 | 현장 판본과 증상 재현 여부는 확인하지 않았다. |
| 설비 연결·명령 | `00.rag/user/equipment-connection-and-command-troubleshooting.md` | user | 트러블슈팅 | 연결 실패·무응답·명령 결과 문제에서 무엇을 확인하는가. 연결과 비동기 작업 실패를 구별한다. | U §10.2; O §10.5; nori-tc-gateway 1c14e091; nori-tc-ui-front e390bbec | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. O의 내부 접근 조치는 user 본문으로 옮기지 않는다. |
| SECS·HSMS | `00.rag/user/secs-hsms-troubleshooting.md` | user | 트러블슈팅 | HSMS 세션·SELECT·설비 식별 문제를 어떻게 확인하는가. 프로토콜 증상 구별에 필요하다. | nori-tc-gateway 07c84fbc·9876b7f7 | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| Socket | `00.rag/user/socket-troubleshooting.md` | user | 트러블슈팅 | 프레임 손상·매치 반복·첫 메시지 유실을 어떻게 구별하는가. Socket 처리 실패를 다룬다. | nori-tc-gateway-action 8d5fac0b·4a3484c7; nori-tc-gateway 2d56c2b7 | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| PLC | `00.rag/user/plc-troubleshooting.md` | user | 트러블슈팅 | PLC 연결·폴링 TIMEOUT·디코드 문제는 어떻게 확인하는가. PLC 증상을 분리한다. | nori-tc-gateway 4047d163·4f843d41; nori-tc-ui-backend 51181c7c | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| 모델·Flow 처리 | `00.rag/user/model-flow-troubleshooting.md` | user | 트러블슈팅 | 계산 규칙·Collect 바인딩·이벤트 순서 문제는 어떻게 구별하는가. 모델 처리 실패에 필요하다. | nori-tc-business 274c7785·038334e8; nori-tc-ui-backend 88e93e02 | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| 배포 | `00.rag/user/deployment-troubleshooting.md` | user | 트러블슈팅 | 배포 대기·취소 대상 문제에서 무엇을 확인하는가. 배포 작업의 실패 경로이다. | nori-tc-ui-backend 12005ecf·c336bd8b | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| DLQ·격리 | `00.rag/user/dlq-quarantine-troubleshooting.md` | user | 트러블슈팅 | DLQ 조회·격리 처리 문제는 어떻게 점검하는가. 실패 조회와 처리 증상을 구별한다. | U §8; nori-tc-ui-backend 1d0e4b2b | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| 조정 작업 | `00.rag/admin/coordination-troubleshooting.md` | admin | 트러블슈팅 | 조정 작업의 보상 완료가 확정되지 않으면 무엇을 확인하는가. 조정 장애의 진단 경로이다. | nori-tc-gateway 62154da0; nori-tc-ui-backend b8faaead | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| TC 플랫폼 (Kafka·DB·Redis·메모리·디스크·인증서·기동) | `00.rag/admin/platform-troubleshooting.md` | admin | 트러블슈팅 | 플랫폼 자원·인증서·기동 장애를 어떻게 구별하는가. 화면 밖 운영 진단에 필요하다. | O §10.2~10.8; nori-tc-exec 42ae1d79·ecba36d5·fa5cba71; nori-tc-business 6e6ec61c | 현장 판본에 수정이 포함됐는지와 실제 운영 자원 상태는 확인하지 않았다. |
| 데모 | `00.rag/user/demo-troubleshooting.md` | user | 트러블슈팅 | 데모 대기 초과·실행 실패·고아 실행은 어떻게 확인하는가. 데모 실행 문제를 다룬다. | nori-tc-simulator 42b3791f·223220e2; nori-tc-ui-backend 050c5f6b | 현장 판본에 수정이 포함됐는지는 확인하지 않았다. |
| 화면 안내와 실제 동작이 다른 곳 | `00.rag/user/screen-behavior-limitations.md` | user | 알려진 제약 | 안내대로 해도 반영되지 않는 기능은 무엇인가. 조작 실수와 제품 제약을 구별한다. | U §10.4 | 실제 적용 판본의 제한 존속 여부는 작성 때 코드와 대조한다. |
| 모델·플러그인 적용 제약 | `00.rag/user/model-plugin-limitations.md` | user | 알려진 제약 | 모델·JAR 선택과 플러그인 적용에는 어떤 제한이 있는가. 모델 사용자의 적용 경계를 다룬다. | U §10.4; 구조 문서 `20-gateway/06-socket-protocol-and-plugins.md` | 실제 적용 판본·모델·플러그인 조건은 추가 확인해야 한다. |
| SECS·HSMS 메시지·타이머 | `00.rag/user/secs-hsms-message-and-timer-reference.md` | user | 참조 | SECS 메시지와 HSMS 타이머는 무엇을 뜻하는가. 모델 정의 때 정확한 값을 찾게 한다. | I §4.8; 구조 문서 `20-gateway/05-hsms-protocol.md` | 제조사별 설비 계약은 해당 원본 없이 확정하지 않는다. |
| Socket 프레임·파싱 규칙 | `00.rag/user/socket-framing-and-parsing-reference.md` | user | 참조 | 구분자·종료 규칙·프레임 해석은 어떻게 정하는가. Socket 모델 설정에 필요하다. | I §4.9; 구조 문서 `20-gateway/06-socket-protocol-and-plugins.md` | 제조사별 명령·응답 계약은 해당 원본 없이 확정하지 않는다. |
| PLC·Modbus 레지스터·데이터 타입 | `00.rag/user/plc-modbus-register-and-type-reference.md` | user | 참조 | 레지스터와 데이터 타입은 무엇을 뜻하는가. PLC 모델의 선택값을 확인한다. | I §4.10; 구조 문서 `20-gateway/07-plc-protocol.md`; PlcDataType | 제조사별 실제 주소표·단위·배율은 해당 원본 없이 확정하지 않는다. |
| 오류 메시지·코드별 의미와 조치 | `00.rag/user/error-message-and-code-reference.md` | user | 참조 | 붙여 넣은 오류 문구·코드는 무엇이며 무엇을 확인하는가. 정확한 문구로 질문할 때 필요하다. | U §10.1; 2026-11-18 TC 문서 목록 조사에서 확인한 화면 오류 문구·오류 코드의 제품 코드 | 코드에서도 의미·발생 조건을 확인할 수 없는 항목은 생략한다. |
| 상태값 | `00.rag/user/state-reference.md` | user | 참조 | 설비·포트·배포·작업의 상태 이름은 무엇을 뜻하는가. 상태의 주체를 구별한다. | 2026-11-18 TC 문서 목록 조사에서 확인한 상태값의 제품 코드 | 선언 이름만 있고 사용처에서도 뜻을 확인하지 못한 항목은 생략한다. |
| 설정 항목 (앱 설정·Secret·Helm values) | `00.rag/admin/configuration-reference.md` | admin | 참조 | 운영 설정을 바꾸면 무엇에 영향을 주는가. 앱·Secret·Helm 설정을 함께 찾는다. | O §6.1~6.5 | 현장 실제 설정값과 코드에서도 의미를 확인하지 못한 항목은 쓰지 않는다. |
| Kafka 토픽·메시지 계약 | `00.rag/admin/kafka-topic-and-message-reference.md` | admin | 참조 | MES·WM·설비 연동 메시지의 형식과 방향은 무엇인가. 내부 연동 계약 확인에 필요하다. | I §2·§4.1~4.5·§5; 2026-11-18 TC 문서 목록 조사에서 확인한 토픽의 제품 코드 | 현장 실제 토픽 설정값은 확인하지 않았다. |
| HTTP API | `00.rag/admin/http-api-reference.md` | admin | 참조 | HTTP-UI·HTTP-SIM 인터페이스는 무엇인가. 요청·응답 계약을 찾게 한다. | I §3·§4.6~4.7 | 현장의 실제 호출 주소·배포 판본은 확인하지 않았다. |
| DB 테이블 | `00.rag/admin/database-table-reference.md` | admin | 참조 | 어떤 테이블에 어떤 자료가 있으며 백업 대상은 무엇인가. 데이터 확인의 기준이다. | `nori-tc-exec/docs/completion-report/03-information-model.md`; 2026-11-18 TC 문서 목록 조사에서 확인한 테이블의 제품 코드 | 현장 데이터·적용 migration 상태와 코드에서 밝히지 않은 의미는 확정하지 않는다. |
| 권한 코드 | `00.rag/user/permission-code-reference.md` | user | 참조 | 메뉴와 작업을 쓰려면 어떤 권한이 필요한가. 메뉴가 없는 사용자의 질문에도 답한다. | U 부록 A; 2026-11-18 TC 문서 목록 조사에서 확인한 권한 코드의 제품 코드 | 현장 사용자·그룹별 실제 부여 현황은 확인하지 않았다. |
| 메트릭·대시보드·알람 | `00.rag/admin/metric-dashboard-and-alert-reference.md` | admin | 참조 | 지표의 의미와 알람 기준은 무엇인가. 관측 결과 해석에 필요하다. | O §8.2; 2026-11-18 TC 문서 목록 조사에서 확인한 메트릭; `nori-tc-exec/docs/observability/03-alert-policy.md`의 정책안 | 현재 켜진 알람·통지 경로는 확인하지 않았다. 정책안은 실제 적용값이 아니다. |
| Flow 액션·메시지 종류 | `00.rag/user/flow-action-and-message-reference.md` | user | 참조 | Flow에서 고르는 액션과 메시지는 무엇을 하는가. 모델 편집 선택의 의미를 찾는다. | U §5.4; 2026-11-18 TC 문서 목록 조사에서 확인한 모델 처리 액션·메시지 종류의 제품 코드 | 이름만 있고 사용처에서도 역할을 확인하지 못한 항목은 생략한다. |
| 공통 FAQ·오해 정정 | `00.rag/user/common-faq.md` | user | FAQ | 접수와 완료 등 여러 업무에 걸친 혼동을 어떻게 구별하는가. 업무 절이 소유하지 않는 공통 질문을 다룬다. | 2026-11-18 사용자가 확정한 공통 FAQ 선정 결정; 답 사실은 관련 업무 코드·문서와 작성 때 대조한다. | 실제 문의 빈도 기록은 없다. 예상 FAQ이며 업무 문서의 답을 복제하지 않는다. |
| 답변 오류의 원인별 교정과 평가 절차 | `00.rag/user/answer-correction-and-evaluation.md` | user | 사용 절차 | 검토 권한자가 AI 위젯의 검토 탭에서 원인을 구별하고 어떻게 교정하는가. 교정 결과의 정답 세트 문항 등록까지 다룬다. | 006 SPEC 4.8의 목표 계약 | 실제 검토 탭·교정 동작은 구현 뒤 확인해야 한다. 현장 운영 완료로 쓰지 않는다. |

## 유형 배정과 원천의 빈틈

2026-11-18 TC 문서 목록 조사·사용자 확정의 분류를 사용 절차·개념·운영·트러블슈팅·알려진 제약·참조·FAQ로 대응한다. 지식 품질 문서는 AI 위젯 검토 탭의 원인별 교정과 그 결과로 범위를 한정하여 유형을 사용 절차로 정한다. 006 SPEC 4.8이 원천이며 7절의 검토 화면 결정과 같이 화면 작업이므로 user 배정을 유지한다. 검토 권한 자체를 일반 열람 권한과 혼동하지 않는다.

| 유형 | user | admin | 합계 |
|---|---:|---:|---:|
| 사용 절차 | 15 | 0 | 15 |
| 개념 | 4 | 0 | 4 |
| 운영 | 0 | 7 | 7 |
| 트러블슈팅 | 10 | 2 | 12 |
| 알려진 제약 | 2 | 0 | 2 |
| 참조 | 7 | 5 | 12 |
| FAQ | 1 | 0 | 1 |
| 합계 | 39 | 14 | 53 |

2026-11-18 TC 문서 목록 조사에서 확인한 원천 부재에는 트러블슈팅의 현장 수정 적용 판본, 장애 인계의 실제 연락처, 실제 알람·통지 경로, 운영 현장값, 코드로도 의미가 확인되지 않는 참조 항목, FAQ의 실제 문의 빈도가 포함된다. 이 목록은 그 부재를 각 문서와 연결한다. “알려진 부재 없음”은 모든 사실의 검증 완료나 현장값 확보를 뜻하지 않는다.

## SeaweedFS 검색 원문에 넣지 않는 자료

오답 사례와 평가 문항·검토 기록은 검색 원문 밖에서 관리한다. 006 SPEC 4.7의 대조는 글자만 확인하므로 틀린 문장이 검색 근거가 될 수 있다. 문서에는 확인된 올바른 사실만 둔다.

서비스 토큰·서명 파일을 사용하는 `acceptance-cli answer-evaluate` 평가 명령의 실행 절차는 TC 검색 원문에 넣지 않는다. 지식 품질 문서는 검토 탭의 교정 조작과 교정 결과의 문항 등록만 설명하며, 교정 유형·근거 위치에 따라 문항이 등록되지 않는 조건도 006 SPEC 4.8에 맞춰 구별한다.

AutoLI TCP/IP와 iMas3000 SECS/GEM 설비 사양은 제조사 원본이 없어 이 53개에 포함하지 않는다. 제조사 원본을 확보한 뒤 작성·대조를 거쳐 추가한다. 코드와 일반 프로토콜 설명으로 제조사 계약을 지어내지 않는다.

영역별 FAQ 파일은 따로 늘리지 않고 해당 업무 문서 안에서 고유한 질문을 다룬다. 다른 절이 이미 답하는 내용은 복제하지 않는다. 이는 2026-11-18 사용자가 확정한 공통 FAQ 하나와 업무 문서 내 FAQ 결정에 따른다.

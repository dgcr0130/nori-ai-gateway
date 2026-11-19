# 장애 문의 때 넘길 근거·인계 작성 기록

## 대상

- objectKey: `00.rag/admin/incident-evidence-handover.md`
- 구획: admin, 필요한 권한 전부 `[AI_DOC_READ, AI_ADMIN_DOC_READ]`
- 유형: 운영
- 검색 원문: `rag-documents/nori-tc/00.rag/admin/incident-evidence-handover.md`
- 검색 원문 SHA-256: `7577feed331b3f04d776a9953763ea71da5a10401b478e0b4836db6cb0083157`
- 최종본: 8,660바이트, 107줄, H2 8절, 직렬화 자식 19개
- 확인일: 2026-11-19(한국 시간)
- 작성 범위: 장애 문의를 받은 운영 담당자의 근거 정리·인계. 화면 조회, 서버 로그 조회, 복구 조치의 실행 절차는 소유 문서로 연결한다.

## 원천 판본

| 저장소 | 현재 HEAD 12자리 | 사용 파일 작업 트리 확인 |
|---|---|---|
| nori-tc-exec | f1d5e0a8e727 | 아래 경로에 대한 `git status --short -- <경로>` 출력 없음, exit 0 |
| nori-tc-ui-front | 9fe69a75dcd3 | 아래 경로에 대한 `git status --short -- <경로>` 출력 없음, exit 0 |
| nori-tc-ui-backend | 985d17c604dd | 아래 경로에 대한 `git status --short -- <경로>` 출력 없음, exit 0 |
| nori-tc-common | 6fe71f6b2da6 | 아래 경로에 대한 `git status --short -- <경로>` 출력 없음, exit 0 |

제품 원천은 작성 전 현재 HEAD와 경로별 작업 트리 상태를 확인했다. 다른 Windows 사용자 소유 저장소에 대한 최초 일반 git 조회는 dubious ownership 오류로 실패했다. 이후 명령별 `git -c safe.directory=C:/workspace_nori/nori-tc/<저장소> -C <저장소> ...`로 읽기 조회했다. 전역 설정은 변경하지 않았다. 개인 Git ignore 파일 접근 경고는 있었으나 대상 조회는 exit 0이며 변경 목록은 비어 있었다.

작업 트리 상태를 확인한 사용 파일:

- nori-tc-exec: `docs/completion-report/06-operation-and-installation-guide.md`, `deploy/helm/tc-gateway/templates/deployment.yaml`, `deploy/helm/tc-business/templates/deployment.yaml`, `deploy/helm/tc-ui-backend/templates/deployment.yaml`, `deploy/helm/tc-simulator/templates/deployment.yaml`, `deploy/scripts/verify-gateway-rollout.sh`, `.gitlab-ci.yml`, `config/tc-log.properties`, `create-k8s-secrets.sh`, `deploy/docker/Dockerfile-ci`.
- nori-tc-ui-front: `src/features/dlq/components/`의 조회 화면, `src/features/dlq/api/dlq.api.ts`, `src/features/deploy/components/DeployMonitorViewModal.tsx`, `src/features/deploy/api/deploy.api.ts`, `src/features/live-map/components/LiveMapCommLogPanel.tsx`, `src/features/live-map/api/comm-log.api.ts`, `src/i18n/locales/ko.json`, `deploy/helm/tc-ui-front/templates/deployment.yaml`.
- nori-tc-ui-backend: `ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/`의 `DlqController.java`, `DeployController.java`, `CommLogController.java`; `ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/CommLogQueryService.java`.
- nori-tc-common: `common/tc-common-logging/src/main/resources/logback-spring.xml`.
- 관련 시험 원문: nori-tc-ui-backend `ui/adapter/tc-ui-web-adapter/src/test/java/com/nori/tc/ui/adapters/web/controller/`의 `DlqControllerRepairTaskTest.java`, `DlqControllerQuarantineTest.java`, `DeployControllerTest.java`. 별도 경로 상태 조회도 출력 없음, exit 0이다. Repair Task 일곱 필드, 격리 다섯 필드, 배포 대상 오류 필드의 기대값을 읽었다. 시험을 실행한 것은 아니다.

먼저 읽은 규칙·목록과 중복 대조한 주요 문서의 세션 확인 해시:

| 파일 | SHA-256 |
|---|---|
| docs/rag-document-writing-rules.md | 273f44daeb41ffe7a63fbd2231c8b73f58464af19a6a580523e82ca5c578e13b |
| docs/tc-rag-document-list.md | 45e7c7ed2a3cc83a516893339fa45326b4a322849372c019fc8fe98e5685970b |
| rag-documents/nori-tc/00.rag/user/dlq-quarantine-and-repair-task.md | 5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d |
| rag-documents/nori-tc/00.rag/user/message-results-and-communication-logs.md | a502f04c22c26ebaceb4678ca68c13d4886c66a4eafd7b9b05c35c187c9b78a5 |
| rag-documents/nori-tc/00.rag/user/deployment-workflow.md | a2b18e6614520bd24689f10899d457471a3e8f2bf55db31d6f0978021894f804 |
| rag-documents/nori-tc/00.rag/admin/daily-checks-and-log-inspection.md | 2fe518b71cdd85ff956b4cafb5f60535791588597640d6ba097c09c4c7cb1096 |

규칙·목록은 전체를 읽었다. 화면과 관련된 범위는 앞선 문서와 대조하고, 현재 제품 코드에서 표시 필드·API·서버 응답을 직접 확인했다. 업그레이드 문서의 되돌림 소유 절도 읽고 연결했다. 기존 검색 원문은 untracked 작업물로 읽기만 했으며 제품 동작의 1차 근거로 대체하지 않았다.

## 절별 사실 대조

아래 표는 본문의 모든 문장을 다룬다. 인계할 항목을 정하는 문장은 확인된 표시값·운영 가이드와 이번 사용자 지시를 조합한 작성 규범이며, 자동 수집 기능이 있다는 주장이 아니다. 보안 제거 지침과 미확인 원인 구분은 사용자 지시로 정한 정책이며 제품 코드 동작으로 꾸미지 않았다.

### 장애 문의에 공통으로 묶을 내용

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 16 첫 문장 | 발생 시각·영향 대상·조작·오류 문구를 묶는다 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2278~2302; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~317 | O의 발생 시각·영향 범위와 코드의 설비별 작업·오류 표시를 인계 항목으로 정리했다. |
| 16 둘째 문장 | 화면 확인 시각과 발생 시각 구별 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2278~2279; nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L307~314 | 발생·갱신 기록과 사람이 조회한 시각을 혼동하지 않도록 하는 인계 규범이다. |
| 18 첫 문장 | 캡처·로그에서 비밀값·인증서 내용·개인 연락처 제거 | 사용자 요청 §0 소유 범위·보안 제한, docs/rag-document-writing-rules.md 「절과 문장을 쓰는 기준」 | 이번 문서에 적용하도록 명시된 정책이다. 자동 마스킹이나 시스템의 유출 방지 보장을 주장하지 않는다. |
| 18 둘째 문장 | Secret·ConfigMap은 리소스명과 키까지만 전달 | 사용자 요청 §0 소유 범위 | 실제 Secret 값을 조회하지 않았다. 정책 문장으로 기록했다. |

### DLQ 문의에서 실패 항목을 특정할 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 29 첫 문장 | Gateway와 Business 범주 구별 | nori-tc-ui-front/src/features/dlq/components/DlqPage.tsx @ 9fe69a75dcd3 L21~32; nori-tc-ui-front/src/features/dlq/api/dlq.api.ts @ 9fe69a75dcd3 L35~46 | 서로 다른 패널·조회 경로를 사용한다. |
| 29 둘째 문장 | ID·설비·발생 시각·단계·사유 코드·사유 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L14~36; nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L15~41; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1066~1077 | 두 패널의 공통 표시 필드와 한국어 열 표기를 확인했다. |
| 31 첫 문장 | 범주별 IF·소켓 또는 출처·메시지 위치 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L19~20; nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L19~23; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1066~1078 | 실제 열에 있는 값을 범주에 맞춰 덧붙인다. |
| 31 둘째 문장 | 원문 조사용 payload 참조 | nori-tc-ui-front/src/features/dlq/components/GatewayDlqPanel.tsx @ 9fe69a75dcd3 L30~34; nori-tc-ui-front/src/features/dlq/components/BusinessDlqPanel.tsx @ 9fe69a75dcd3 L35~39 | 표시된 참조값을 남긴다. 원문 열람·재처리 기능을 추가로 주장하지 않는다. |
| 33 | 조회 절차 소유 문서 안내 | rag-documents/nori-tc/00.rag/user/dlq-quarantine-and-repair-task.md @ sha256=5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d ; Gateway·Business DLQ 절 | 존재하는 확정 objectKey이며 조회 절차를 복제하지 않았다. |

### 격리와 Repair Task 문의를 구별하는 근거

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 43 | 격리 인계 다섯 값 | nori-tc-ui-front/src/features/dlq/components/QuarantinePanel.tsx @ 9fe69a75dcd3 L14~30; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1071~1079 | 화면의 설비·격리 시각·사유 코드·사유·TTL이다. TTL 뜻은 여기서 풀이하지 않았다. |
| 45 첫 문장 | Repair Task 일곱 값 | nori-tc-ui-front/src/features/dlq/components/RepairTaskPanel.tsx @ 9fe69a75dcd3 L14~28; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L1080~1084; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L200~209 | 필드·한국어 문구와 서버 DTO 매핑 경로를 확인했다. |
| 45 둘째 문장 | 같은 설비의 격리와 복구 작업 구별 | nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DlqController.java @ 985d17c604dd L177~209 | 격리 조회와 복구 작업 조회는 서로 다른 메서드·반환 경로다. |
| 47 | 목록 경로 소유 문서 안내 | rag-documents/nori-tc/00.rag/user/dlq-quarantine-and-repair-task.md @ sha256=5311baf3c41419dca70c40c002d42f830fe9f974244fdb9c0ac4863d4528d03d ; 격리 설비·Repair Task 절 | 실제 경로와 소유 범위 확인. |

### 배포나 메시지 전송 실패의 대상과 단계 남기기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 58 첫 문장 | 배포 번호와 실패 EQP ID 연결 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L229~234 L329~338 | 창 상단 번호와 대상 행 설비를 대응시킨다. |
| 58 둘째 문장 | 대상 행 작업·결과·상태·단계·오류·갱신 시각 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L236~317; nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 L295~301; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd L333~348 | 실제 표시 열과 서버 대상 응답 경로를 확인했다. |
| 60 첫 문장 | 상세 실패 시 번호·조회 시각·화면 문구 | nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 L119~124 L463~479 | 상세 조회 오류를 창에 표시한다. 버튼 조작 절차는 소유 문서에 남겼다. |
| 60 둘째 문장 | 배포·메시지 전송 조회 소유 문서 | rag-documents/nori-tc/00.rag/user/deployment-workflow.md @ sha256=a2b18e6614520bd24689f10899d457471a3e8f2bf55db31d6f0978021894f804 ; View·대상별 진행 현황 절; rag-documents/nori-tc/00.rag/user/message-results-and-communication-logs.md @ sha256=a502f04c22c26ebaceb4678ca68c13d4886c66a4eafd7b9b05c35c187c9b78a5 ; 메시지 전송 절 | 각 문서의 조회 소유 범위를 확인했다. |

### 통신 문의에서 같은 메시지를 찾게 하는 값

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 70 첫 문장 | eqpId·time·epoch·방향·출처·메시지명 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L151~194; nori-tc-ui-front/src/features/live-map/api/comm-log.api.ts @ 9fe69a75dcd3 L6~34; nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/CommLogController.java @ 985d17c604dd L35~45 | 상세의 상단 표시와 조회 API·서버 응답 흐름을 확인했다. |
| 70 둘째 문장 | epoch 표시값 그대로 기록 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L187~194 | time은 포맷한 timestampMs, epoch는 timestampMs 그대로다. 조정 cluster Epoch와 연결하지 않았다. |
| 72 첫 문장 | 기록 없을 때 통신·조회 시각·설비·문구 | nori-tc-ui-front/src/features/live-map/components/LiveMapCommLogPanel.tsx @ 9fe69a75dcd3 L115~148 | 대상 설비와 빈 목록 표시가 있으며, 기록 부재의 원인을 단정하지 않는 인계 항목으로 정했다. |
| 72 둘째 문장 | 조회 소유 문서 | rag-documents/nori-tc/00.rag/user/message-results-and-communication-logs.md @ sha256=a502f04c22c26ebaceb4678ca68c13d4886c66a4eafd7b9b05c35c187c9b78a5 ; 통신 목록·상세·빈 목록 절 | 조회 경로만 연결한다. |

### 서버 로그와 Pod 상태를 넘길 때 붙일 조회 위치

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 83 첫 문장 | 조회 권한·namespace·release·Pod·기간 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2281~2302 L1969~1972; nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L3~23 | 장애 공통 조회와 실제 namespace·앱 라벨을 대조했다. 현재 현장값은 쓰지 않는다. |
| 83 둘째 문장 | 파일 경로 첨부 | nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 L21~35 L44~47; nori-tc-exec/deploy/docker/Dockerfile-ci @ f1d5e0a8e727 L15~15 | 파일 로그가 있으며 위치는 설정에 연결된다. 실제 경로 조회는 소유 문서로 안내한다. |
| 83 셋째 문장 | ERROR·WARN·앞뒤·traceId·시각 | nori-tc-exec/create-k8s-secrets.sh @ f1d5e0a8e727 L33~45; nori-tc-common/common/tc-common-logging/src/main/resources/logback-spring.xml @ 6fe71f6b2da6 L16~40; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2287~2292 | 배포 패턴에 시각·수준·traceId가 들어간다. 값이 실제 확보된 경우만 기록하도록 썼다. |
| 85 첫 문장 | 상태·재시작 증가·종료 사유 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2285~2302 | Pod 상태와 Last State를 확인하는 원천이다. |
| 85 둘째·셋째 문장 | Gateway 검증 명령·exit·출력과 조회 실패 원문 | nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 L16~57 | 스크립트의 입력·출력·성공/실패 종료를 읽었다. 실행 사실이 있을 때 결과를 전달한다. 조회 실패는 성공으로 바꾸지 않는 인계 규범이다. |
| 87 | 로그 위치·조회 소유 문서 | rag-documents/nori-tc/00.rag/admin/daily-checks-and-log-inspection.md @ sha256=2fe518b71cdd85ff956b4cafb5f60535791588597640d6ba097c09c4c7cb1096 ; Pod별 로그·이전 로그·파일 위치 절 | 명령·경로·보관 기간의 본문은 복제하지 않았다. |

### 새 Pod 기동 실패나 계속되는 서버 오류를 인계하기

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 96 첫 문장 | 서비스·Pod·기동 로그 첫 오류 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2312~2316 | 새 Pod 기동 실패는 첫 오류를 읽게 한다. 특정 원인을 일률적으로 확정하지 않는다. |
| 96 둘째 문장 | 적용 이미지 태그·변경 시각 | nori-tc-exec/deploy/helm/tc-gateway/templates/deployment.yaml @ f1d5e0a8e727 L44~46; nori-tc-exec/.gitlab-ci.yml @ f1d5e0a8e727 L364~370 | 배포 입력 IMAGE_TAG가 chart app.imageTag를 거쳐 이미지에 반영된다. 변경 시각은 인계 기록 항목이다. |
| 98 첫째·둘째 문장 | 지속 오류 로그, 되돌린 판본·시각·잔여 증상 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2318~2321 | 오류 서비스 로그와 되돌림 후에도 지속되는 증상을 넘기는 흐름을 확인했다. 되돌림 실행을 지시하지 않는다. |
| 98 셋째 문장 | 변경·되돌림 소유 문서 안내 | rag-documents/nori-tc/00.rag/admin/upgrade-rollback-and-configuration-apply.md @ sha256=72c761af5058e94a1655215ca493e0a2f330fd8b38a8c43fc59a72157f6b997a ; 언제 업그레이드를 중단하고 되돌림을 검토하는가·앱 되돌리기 절 | 파일·절의 존재와 소유 범위를 읽어 확인했다. |

### 대응을 마친 뒤 인계 기록에 남길 다섯 항목

| 검색 원문 줄 | 문장·행 요지 | 원천 위치(저장소/경로 @ commit Lx~y) | 확인한 내용 |
|---|---|---|---|
| 105 첫 문장 | 발생 시각·영향 범위·근본 원인·복구 조치·재발 방지 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2278~2279 | 가이드의 대응 후 기록 다섯 항목을 빠짐없이 보존했다. |
| 105 둘째 문장 | 근거에 연결한 원인과 미확인 구별 | 사용자 공통 지시 §0·rules90; nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2278~2279 | 확인된 원인과 미확인을 구별하는 기록 규범이다. |
| 107 첫 문장 | 조치와 복구 확인 결과 구별 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2274~2279 | 조치와 복구 확인을 다른 칸으로 정의한다. |
| 107 둘째 문장 | 잔여 증상·화면·로그 위치 인계 | nori-tc-exec/docs/completion-report/06-operation-and-installation-guide.md @ f1d5e0a8e727 L2318~2321; 사용자 요청 §0 소유 범위 | 지속 장애의 로그 인계와 증상별 근거 인계를 정리했다. |

## 매뉴얼과 코드가 다른 곳

| 주제 | 매뉴얼 | 코드 | 검색 원문 반영 |
|---|---|---|---|
| Gateway 검증의 보장 범위 | O L2298~2302는 검증 스크립트로 Lease 배치가 정상인지 본다고 설명한다. | `nori-tc-exec/deploy/scripts/verify-gateway-rollout.sh @ f1d5e0a8e727 L24~50`은 Pod 수>0, 전부 Running, Ready 수가 min(Pod 수, 설정 group 수) 이상인지만 검사한다. Lease/owner 원장을 직접 조회하지 않는다. | L85에 명령·종료코드·출력을 첨부하도록만 썼다. 성공을 소유권 전체 검증으로 표현하지 않았다. |

원천 O §10.1의 조회 명령·재기동 명령·승인 설명은 인계 문서의 실행 절차로 복제하지 않았다. 재기동·되돌림 판단과 플랫폼 원인 진단은 각각 소유 문서 범위다. 앞선 문서를 수정할 별도 결함은 이번 조사에서 확정하지 않았다.

## 원천이 없어 뺀 사실과 확인할 담당 역할

| 뺀 사실 | 이유 | 확인할 담당 역할 |
|---|---|---|
| 실제 담당자·개인 연락처·당번·문의 채널 | O §10.1에는 구체적인 담당·연락 정보가 없으며 사용자도 부재를 확정했다. | TC 운영 책임자 |
| 현장 현재 버전·replica 수·Pod 상태·실제 traceId·로그 위치·복구 완료 여부 | 현장에 접속하지 않았고 소스 상태를 현장 관측으로 바꿀 수 없다. 필요한 기록 항목과 소유 문서만 안내한다. | 현장 운영 담당자 |
| 현장 중앙 로그 수집 URL·조회 권한·보관 현황 | 실제 적용·접속 경로가 확인되지 않았다. | 인프라·로그 운영 담당자 |

## 다른 문서가 소유해 넣지 않은 사실

| objectKey | 소유 사실·이번 문서의 경계 |
|---|---|
| 00.rag/user/dlq-quarantine-and-repair-task.md | 카드 진입·조회·필터·권한·TTL 의미·실패 코드·정리 요청 절차. 인계에 필요한 표시값만 최소 사용했다. |
| 00.rag/user/message-results-and-communication-logs.md | 전송 기록 찾기·통신 상세 열기·조회 범위·자동 갱신·빈 목록 처리. 조회 절차를 복제하지 않았다. |
| 00.rag/user/deployment-workflow.md | 생성·실행·상세·재실행·삭제·권한·오류 재조회. 대상별 인계 값만 최소 사용했다. |
| 00.rag/admin/daily-checks-and-log-inspection.md | 서버 로그 명령·실제 설정상 파일 경로·보관 설정·점검 절차. 여기서는 조회 위치를 기록하게 하고 경로를 연결한다. |
| 00.rag/admin/upgrade-rollback-and-configuration-apply.md | 이미지 교체·설정 반영·중단·되돌림 실행 절차. 수행한 변경과 결과의 인계만 썼다. |
| 00.rag/admin/platform-troubleshooting.md | O §10.2~10.8의 원인별 진단·복구. O §10.1의 첫 오류·지속 오류 인계까지만 담았다. |
| 00.rag/user/state-reference.md | 상태값별 의미. 인계할 표시값을 적되 의미 표를 재작성하지 않았다. |
| 00.rag/user/error-message-and-code-reference.md | 오류 코드별 뜻·조치. 실제 표시 문구를 전달하도록만 썼다. |

## 검사 결과

기계 검사 명령:

```sh
bash C:/Users/DongGeon/AppData/Local/Temp/claude/f968bd326959d05a/bb866f2d-77aa-4fd3-ba82-fbab3fa29bb7/scratchpad/step3/tools/check.sh --dump 00.rag/admin/incident-evidence-handover.md
```

최종 검색 원문 편집 뒤 실행한 검사 exit 0. 대상 위반 0건, CROSS VIOLATION 0건, MASK 출력 0건. 검사기는 수정하지 않았다. Git Bash 5.2.37(1)-release, Java Temurin 21.0.9+10이다.

마지막 출력 10줄:

```text
    L87-87 문단 69  | 로그 위치와 조회 명령은 00.rag/admin/daily-checks-and-log-inspection.md에서 확인한다.
§ L89-98 [29] 새 Pod 기동 실패나 계속되는 서버 오류를 인계하기   parent=253 children=2 sources=3
    L96-96 문단 91  | 새 Pod가 기동하지 못하면 운영 담당자는 대상 서비스·Pod와 기동 로그의 첫 오류를 묶는다. 배포와 관련된 문의에는 적용한 이미지 태그와 변경 시각도 기록한다.
    L98-98 문단 161  | 기동 뒤 서버 오류가 계속되면 오류가 난 서비스의 로그를 넘긴다. 되돌림을 수행한 경우에는 되돌린 판본·시각과 이후 같은 증상이 남는지도 함께 전달한다. 변경·되돌림 절차는 00.rag/admin/upgrade-rollback-and-configuration-apply.md에서 확인한다.
§ L100-107 [24] 대응을 마친 뒤 인계 기록에 남길 다섯 항목   parent=173 children=2 sources=1
    L105-105 문단 99  | 대응을 마친 운영 담당자는 발생 시각·영향 범위·근본 원인·복구 조치·재발 방지를 기록한다. 근본 원인은 확인한 근거와 연결하고, 아직 확인하지 못한 원인은 미확인으로 구별한다.
    L107-107 문단 73  | 실행한 조치와 복구 확인 결과를 구별해 남긴다. 후속 조사로 넘기는 경우에는 남은 증상과 확보한 화면·로그의 위치를 함께 전달한다.
SUMMARY sections=8 children=19 maxChild=167 maxParent=288
CORPUS docs=23/53 sections=393 part={admin=6, user=17} type={개념=4, 사용 절차=13, 운영=6}
TOTAL files=23 targets=1 violations=0
```

사람 검사(8절, 표본 추출 없이 전부):

| 절 제목 | 제목으로 질문 식별 | 원천 일치 | 독립 답 | 중복·소유 경계 |
|---|---|---|---|---|
| 장애 문의에 공통으로 묶을 내용 | 적합 | 정책과 제품 근거 구별 | 적합 | 공통 인계·보안만 소유 |
| DLQ 문의에서 실패 항목을 특정할 값 | 적합 | 렌더·문구·API 확인 | 적합 | 조회 절차는 연결 |
| 격리와 Repair Task 문의를 구별하는 근거 | 적합 | 렌더·서버·관련 시험 기대값 확인 | 적합 | 기록 종류별 식별값만 사용 |
| 배포나 메시지 전송 실패의 대상과 단계 남기기 | 적합 | 렌더·문구·서버 확인 | 적합 | 조회 조작·상태 뜻은 제외 |
| 통신 문의에서 같은 메시지를 찾게 하는 값 | 적합 | 상세·API 확인 | 적합 | 조회 절차·시간 단위 풀이 제외 |
| 서버 로그와 Pod 상태를 넘길 때 붙일 조회 위치 | 적합 | chart·스크립트·로그 패턴 대조 | 적합 | 명령·경로 목록 재수록 없음 |
| 새 Pod 기동 실패나 계속되는 서버 오류를 인계하기 | 적합 | O·배포 입력 대조 | 적합 | 원인 진단·되돌림 실행 제외 |
| 대응을 마친 뒤 인계 기록에 남길 다섯 항목 | 적합 | O 다섯 항목 대조 | 적합 | 대응 후 기록만 소유 |

전체 제목 8개는 질문별로 다르며 불필요한 틀 반복은 없다. 동일 본문 자식과 다른 절의 답을 복제한 부분은 없다. 인계용 필드의 재언급은 주제별 최소 식별 조건에 한정했다. 빈 틀 문장·이름 토막 풀이·잘린 문장·오답 인용은 없다. admin 문서이며 user 문서에 관리 접근을 추가하지 않았다. 실제 비밀값·접속값은 쓰지 않았고 mask-v1 후 필요한 필드명이 보존됨을 dump로 확인했다. 미해소 작성 결함 0건이다.

검사기 호출은 초안 2회·최종본 1회였다. 최초 호출은 대기 세션 정보를 회수하지 못해 결과·종료코드가 미확인이고, 두 번째 초안과 최종본은 종료코드 0을 직접 회수했다. 재실행은 불필요한 중복 호출이었으며 최종 판정에는 마지막 최종본 검사만 사용했다. 확인 종료 시 `Get-Process -Name java`에는 11월 16일 시작한 기존 Java 두 개만 보였으며 이 세션의 검사 Java는 남아 있지 않았다. 프로세스 상세 CIM 조회는 접근 거부였으므로 그 조회로 프로세스 수명을 확인했다고 하지 않는다. 다른 프로세스는 종료하지 않았다.

제품 시험·빌드·실제 UI 실행·독립 공식 검증·현장 조사·네트워크 접속·업로드는 실행하지 않았다. 위임 호출 0회이며 총 wall time·provider 사용량·비용은 미계측이다. 검사용 임시 파일·디렉터리는 만들지 않았다. 생성 산출물은 지정된 검색 원문과 이 기록 두 파일뿐이다. 다음 작업은 메인 세션의 원천 대조이며, 이 작성 결과로 업로드·현장 복구 성공을 주장하지 않는다.

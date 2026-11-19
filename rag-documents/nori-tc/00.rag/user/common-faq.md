# 공통 FAQ·오해 정정
```rag-meta
유형: FAQ
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/deployment-workflow.md
```

## 접수·생성·완료는 화면마다 무엇을 확인하는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; runLifecycleStep·성공 결과 L187~219 L265~274
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 시작 결과 제목 L32~41 L104~123
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 완료·배포 생성 문구 L81~82 L235~238
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 생성 결과와 상태 표시 L664~693 L967~982
원천: nori-tc-ui-front/src/features/deploy/lib/deploy-register-utils.ts @ 9fe69a75dcd3 ; 배포 번호·상태 문자열 L128~134
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 배포 번호·상태 문구 L338~338
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 시작 접수 L322~337 L493~499
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/DeployService.java @ 985d17c604dd ; 생성 뒤 상태 L146~194
```

설비 시작은 요청을 접수한 뒤 결과를 따로 조회한다. 「설비 시작 완료」는 시작 결과의 성공 표시다. 「Deploy 생성 완료」는 배포 기록을 만든 결과이므로, 완료라는 글자와 함께 무엇을 끝냈는지 읽는다.

배포 생성 결과에는 배포 번호와 상태가 표시된다. 실행할 변경이 있으면 READY에서 실행을 기다리고, 변경이 전혀 없으면 생성 단계에서 COMPLETED로 끝날 수 있다. 설비 시작 결과와 배포 생성 결과를 구별해 기록한다.

## 같은 설비의 작업 ID와 배포 번호는 어떻게 구별하는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 작업 ID 표시 L28~40
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 명령별 대상·결과 연결 L201~233 L258~274
원천: nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 작업별 조회 L19~47
원천: nori-tc-ui-front/src/features/deploy/components/DeployMonitorViewModal.tsx @ 9fe69a75dcd3 ; 대상 설비와 배포 번호 L229~234 L329~350
원천: nori-tc-ui-front/src/features/deploy/api/deploy.api.ts @ 9fe69a75dcd3 ; 번호별 배포·대상 조회 L774~803
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 ID 문구 L1110~1110
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/AsyncResultController.java @ 985d17c604dd ; 작업별 응답 L69~107
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/DeployController.java @ 985d17c604dd ; 배포별·대상별 응답 L315~348
```

설비 명령 결과 창의 「작업 ID」는 개별 명령 결과를 찾는 값이다. 「Deploy View」 위쪽의 # 뒤 번호는 배포 한 건을 찾는 값이며, 배포 안의 대상 설비는 「EQP ID」로 구별한다.

설비 명령과 배포를 함께 문의할 때는 번호 옆에 「작업 ID」 또는 배포 번호라고 적는다. 설비 ID·조작한 명령·발생 시각·각 화면의 결과를 번호와 짝지어 운영 담당자에게 전달한다.

## 결과 창의 확인·닫기와 작업 취소는 어떻게 다른가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 확인 단추 L132~140
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 결과 창 닫기 L160~165
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; 결과 창 닫기 연결 L1514~1530
원천: nori-tc-ui-front/src/features/deploy/components/DeployRegisterPanel.tsx @ 9fe69a75dcd3 ; 생성 결과 창 닫기 L315~317 L984~1008
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 확인·닫기 문구 L66~66 L473~473
```

설비 명령 결과 창의 「확인」과 배포 생성 결과 창의 「닫기」는 결과 창을 닫는 조작이다. 접수한 명령이나 생성한 배포를 취소하는 요청을 보내지 않는다.

진행 결과를 보관하려면 닫기 전에 「작업 ID」 또는 배포 번호와 표시 결과를 기록한다. 닫은 뒤 실행 중단이 필요하면 설비 ID·번호·요청 시각·현재 결과를 운영 담당자에게 전달해 진행 상태와 중단 가능 여부를 확인한다.

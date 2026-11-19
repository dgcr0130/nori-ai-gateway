# 설비 시작·종료·재연결·PLC 폴링
```rag-meta
유형: 사용 절차
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-18
담당: TC 문서 담당
관련문서: 00.rag/user/equipment-registration-and-settings.md
```

## 명령 실행과 결과 조회에 필요한 권한
```rag-meta
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 명령·조회 권한 L15~23
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 설비 권한 L123~140
원천: nori-tc-exec/docs/db_table/sample_data/postgres_insert_sample_data.sql @ f1d5e0a8e727 ; 기본 권한 시드 L764~768
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청 인가 L142~185
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 명령 메뉴 L244~280
원천: nori-tc-ui-front/src/shared/api/operation.api.ts @ 9fe69a75dcd3 ; 결과 조회 L19~47
```

설비 화면에 진입한 사용자가 시작·종료·재시작·재연결·PLC 폴링을 실행하려면 저장소 기본 권한 데이터 기준으로 EQP_WRITE가 필요하다. 요청 뒤 결과를 읽으려면 ASYNC_READ도 필요하다.

명령 메뉴가 보여도 실행 권한이 보장되지는 않는다. 권한 거부 문구가 나오면 설비 ID·실행한 명령·발생 시각·문구를 운영 관리자에게 보내 명령 권한과 결과 조회 권한을 확인한다.

## 시작·종료 메뉴는 어느 표에서 여는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 상세 행 메뉴 L233~280
원천: nori-tc-ui-front/src/features/eqp/components/GatewayGroupTable.tsx @ 9fe69a75dcd3 ; 목록 행 메뉴 L360~411
원천: nori-tc-ui-front/src/features/eqp/components/EqpSidebar.tsx @ 9fe69a75dcd3 ; 트리 행 메뉴 L180~191
```

설비 한 대를 선택한 뒤 「설비 정보」 표의 행을 마우스 오른쪽 단추로 누른다. 상태에 맞는 「Eqp Start」·「Eqp End」·「Eqp Restart」·「Eqp Reconnect」가 나타난다.

「Gateway」의 설비 목록 표 행에서도 시작·종료·재시작을 고를 수 있다. 재연결은 한 대의 「설비 정보」 표에서 고른다. 좌측 트리의 설비 등록·수정·삭제 메뉴와 명령을 여는 자리를 구별한다.

## 연결 상태에 따라 어떤 명령이 보이는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; 명령 표시 판정 L44~74
원천: nori-tc-ui-front/tests/unit/eqp-runtime-state.test.ts @ 9fe69a75dcd3 ; 상태별 기대값 L44~79
```

「Comm Mode」가 ACTIVE인 설비는 연결 여부와 시작 의도에 따라 메뉴가 달라진다. PASSIVE 설비에는 네 명령이 표시되지 않는다.

| 설비 조건 | 「설비 정보」의 명령 |
|---|---|
| 연결이 끊겼고 시작 의도가 아님 | Eqp Start |
| 연결이 끊겼고 시작 의도가 유지됨 | Eqp End·Eqp Reconnect |
| 연결된 상태 | Eqp End·Eqp Restart |

## Eqp Start로 설비 시작하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 ; 확인 창 L29~41
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 ; 대상·단추 L65~100
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 시작 실행 L201~219
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 시작 결과 제목 L81~82
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 완료 본문 L105~130
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; start L322~338
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 시작 조건 L359~397
```

1. ACTIVE 설비의 행 메뉴에서 「Eqp Start」를 고른다.
2. 「Eqp Start」 창의 설비 ID를 확인하고 같은 이름의 「Eqp Start」 단추를 누른다. 요청 전 취소는 「Cancel」을 누른다.
3. 창 안의 진행 표시가 끝난 뒤 「설비 시작 완료」와 대상 설비 ID를 확인한다.

서버는 시작 의도가 이미 유지되는 설비의 시작을 거부한다. 연결이 끊겼어도 시작 의도가 유지되는 설비는 재연결 메뉴를 사용한다.

## Eqp End로 설비 종료하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 ; 확인·단추 L65~100
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 종료 실행 L221~226
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 종료 결과 제목 L83~84
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 종료 제목·본문 L42~46
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; end L348~364
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd ; 종료 뒤 폴링 의도 해제 L437~468
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 종료 조건 L417~452
```

1. ACTIVE 설비의 행 메뉴에서 「Eqp End」를 고른다.
2. 「Eqp End」 창의 설비 ID를 확인한 뒤 「Eqp End」 단추를 누른다.
3. 「설비 종료 완료」를 확인한다. 이미 시작 의도가 해제된 설비는 서버가 종료를 거부할 수 있다.

## Eqp Restart와 Eqp Reconnect는 어떻게 다른가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 재연결·재시작 L228~247
원천: nori-tc-ui-front/src/features/eqp/utils/eqp-runtime-state.util.ts @ 9fe69a75dcd3 ; 표시 조건 L67~73
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd ; 종료의 폴링 영향 L460~465
```

| 명령 | 대상과 실행 방식 |
|---|---|
| Eqp Restart | 연결된 설비를 종료하고, 종료 성공을 확인한 뒤 시작한다. 종료가 실패·중단·확인 지연이면 시작으로 넘어가지 않는다. |
| Eqp Reconnect | 시작 의도가 유지되지만 연결이 끊긴 설비에 재연결 명령 한 번을 보낸다. |

PLC 설비는 「Eqp End」 성공 시 켜 두었던 모든 폴링 그룹의 시작 의도가 지워진다. 「Eqp Restart」도 종료 단계를 거치므로 같은 영향을 받는다. 설비를 다시 시작한 뒤 필요한 그룹을 다시 켠다.

## Eqp Restart로 종료 후 다시 시작하기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 재시작 메뉴 L265~272
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 ; 재시작 확인·단추 L29~41
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 ; 확인 조작 L65~100
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 두 단계 실행 L236~247
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 재시작 결과 L85~86
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 재시작 결과 본문 L105~130
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 작업 ID L28~40
```

1. 연결된 ACTIVE 설비의 행 메뉴에서 「Eqp Restart」를 고른다.
2. 「Eqp Restart」 창의 설비 ID를 확인한 뒤 「Eqp Restart」 단추를 누른다.
3. 종료와 시작을 기다리고 「설비 재시작 완료」를 확인한다.

「설비 재시작 실패」가 나오면 결과 본문의 사유를 읽고, 표시된 「작업 ID」와 함께 기록한다.

## Eqp Reconnect로 끊긴 연결 다시 맺기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; 재연결 메뉴 L273~279
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 ; 확인 조작 L65~100
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 재연결 실행 L228~233
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 재연결 결과 L87~88
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 재연결 결과 제목 L54~59
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; reconnect L377~393
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 재연결 조건 L689~716
```

1. 시작 의도가 유지되지만 연결이 끊긴 ACTIVE 설비를 선택한다.
2. 한 대의 「설비 정보」 표 행에서 「Eqp Reconnect」를 고른다.
3. 「Eqp Reconnect」 창의 설비 ID를 확인하고 「Eqp Reconnect」 단추를 누른다.
4. 「설비 재연결 완료」를 확인한다.

서버는 시작 의도가 유지되지 않는 설비의 재연결을 거부한다. 메뉴를 연 뒤 상태가 바뀌었다면 화면을 새로고침해 현재 명령을 확인한다.

## 시작·종료·재연결 결과가 확인 지연일 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 제목·조회 단추 L32~59
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 다시 조회 조건 L132~140
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 지연 결과·재조회 L289~297
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 재조회 처리 L360~378
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 지연 안내 L1110~1119
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 명령별 제목 L1151~1158
```

「Start 확인 지연」·「End 확인 지연」·「Reconnect 확인 지연」이면 결과 창의 「다시 조회」로 같은 작업 결과를 읽는다. 「다시 조회」는 확인 지연이고 「작업 ID」가 있을 때만 표시된다.

「다시 조회」가 없는 지연 창이면 잠시 후 화면을 새로고침한다. 재조회 중에는 「다시 조회」가 비활성화된다. 결과를 확인한 뒤 「확인」으로 닫는다.

## Restart 확인 지연 뒤에는 무엇을 확인하는가
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 종료 지연이면 시작 생략 L236~247
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 재조회는 현재 작업만 추적 L360~378
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 재시작 제목 L48~53
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 재시작 지연 제목 L1155~1156
```

「Restart 확인 지연」은 종료 단계나 시작 단계의 결과 확인이 늦어진 경우다. 「다시 조회」는 현재 단계만 조회한다. 종료 단계가 지연됐다면 나중에 종료 성공을 읽어도 시작을 자동으로 이어 실행하지 않는다.

종료 단계 지연 뒤에는 「설비 재시작 완료」로 바뀌어도 실제 설비 연결을 새로고침해 확인한다. 설비가 종료 상태로 남으면 현재 메뉴의 「Eqp Start」로 시작한다. 어느 단계인지 알 수 없으면 설비 ID·시각·「작업 ID」·표시 문구를 운영 담당자에게 전달한다.

## 명령이 실패하거나 운영 확인 필요로 끝났을 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 서버 사유·실패 문구 L277~307
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 요청 오류 L336~347
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 본문·단추 L105~140
원천: nori-tc-ui-front/src/features/eqp/components/EqpOperationResultBody.tsx @ 9fe69a75dcd3 ; 작업 ID·안내 L28~40
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 중단 제목 L1151~1158
```

「설비 시작 실패」·「설비 종료 실패」·「설비 재시작 실패」·「설비 재연결 실패」이면 결과 본문의 서버 문구를 읽는다. 서버 사유가 없으면 기본 실패 문구가 나온다. 실패 창에는 「확인」만 표시된다.

「Start 중단 — 운영 확인 필요」·「End 중단 — 운영 확인 필요」·「Restart 중단 — 운영 확인 필요」·「Reconnect 중단 — 운영 확인 필요」이면 운영 담당자에게 설비 ID·발생 시각·명령·결과 문구·표시된 「작업 ID」를 전달한다.

## 화면에서 고칠 칸이 없는 명령 거부 조건
```rag-meta
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/dto/request/EqpLifecycleRequest.java @ 985d17c604dd ; 필수 통신 방식 L18~23
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 통신 방식 자동 전달 L201~210
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleActionDialog.tsx @ 9fe69a75dcd3 ; 수정 입력칸 없는 확인 창 L65~103
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd ; 설비 조회·검사 L437~449
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 시작의 사용 허용 검사 L375~393
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 재연결의 사용 허용 검사 L704~714
```

명령 요청의 설비 ID와 통신 방식은 선택한 설비에서 자동으로 전달된다. 서버는 설비 존재 여부와 필수 통신 방식을 확인한다. 확인 창에는 이 값을 고치는 입력 칸이 없다.

시작·재연결은 서버에서 사용이 허용되지 않은 설비도 거부한다. 설비 없음·통신 방식 누락·사용 불가 안내이면 화면을 새로고침해 대상을 다시 확인한다. 계속 거부되면 설비 ID·명령·시각·서버 문구를 운영 담당자에게 전달한다.

## PLC 폴링 그룹을 입력하기 전 준비할 것
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; PLC 전용 표시 L1235~1243
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 입력 검사·영역 L35~42
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 입력·단추 L80~113
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 입력 문구 L517~522
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcGroupPollControl.java @ d3636ab86763 ; 등록된 그룹 조회 L24~31
```

「Comm Interface」가 PLC인 설비를 고르면 「설비 정보」 표 아래에 「Polling Group」이 나타난다. 「Group messageName」 칸에는 대상 설비에 등록된 그룹의 messageName을 직접 입력한다.

앞뒤 공백은 제거된다. 빈칸이면 「그룹 messageName 을 입력해 주세요.」가 표시된다. 목록 선택이나 그룹 존재 여부의 사전 검사는 없으므로 사용할 그룹을 모르면 모델 담당자에게 대상 설비와 그룹 값을 확인한다.

## Start로 PLC 그룹의 주기 읽기 켜기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 시작·초록 표시 L38~56
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 입력·시작 단추 L85~103
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 시작 API L773~809
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 그룹 시작 L405~419
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 시작 검사 L980~1005
원천: nori-tc-gateway/gateway/starter/tc-gateway-starter/src/main/java/com/nori/tc/gateway/starter/runtime/plc/StarterPlcGroupPollControl.java @ d3636ab86763 ; 그룹 주기 사용 L24~31
```

1. PLC 설비를 먼저 시작하고 연결된 상태인지 확인한다. 서버는 사용 허용·시작 의도·활성 연결·등록된 그룹을 검사한다.
2. 「Polling Group」의 「Group messageName」에 켤 그룹 값을 입력하고 「Start」를 누른다.
3. 처리 중에는 입력 칸과 「Start」·「End」가 비활성화된다.

서버에서 시작이 성공하면 그룹에 정해진 주기로 읽는다.

## End로 PLC 그룹의 주기 읽기 끄기
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 중단 요청 L59~65
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; End 단추 L104~113
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 중단 API L816~852
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 그룹 종료 L430~444
원천: nori-tc-ui-backend/ui/tc-ui-core/src/main/java/com/nori/tc/ui/core/service/EqpDirectScenarioService.java @ 985d17c604dd ; 그룹별 의도 해제 L380~391
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 중단 처리 L1018~1028
원천: nori-tc-gateway/gateway/tc-gateway-plc-core/src/main/java/com/nori/tc/gateway/plc/runtime/PlcPollScheduler.java @ d3636ab86763 ; 그룹 타이머 제거 L86~95
```

1. PLC 설비의 「Polling Group」에서 끌 그룹 값을 「Group messageName」에 입력한다.
2. 「End」를 누른다. 요청 대상은 입력한 그룹 하나다. 서버에서 중단이 성공하면 그 그룹의 주기 읽기와 시작 의도를 해제한다.

그룹 중단은 연결되지 않았거나 그룹 타이머가 이미 멈춘 경우에도 허용한다. 설비 전체를 종료하려면 폴링 영역의 「End」와 구별해 행 메뉴의 「Eqp End」를 사용한다.

## PLC 폴링의 초록색 완료 문구를 읽을 때 주의할 점
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 폴링 결과 전달 L424~453
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 완료 문구 선택 L47~70
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 초록색 결과 L116~126
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 완료 문구 L520~521
```

입력한 그룹 값과 함께 「시작이 완료되었습니다.」 또는 「중단이 완료되었습니다.」가 초록색으로 표시된다. 확인한 소스 판본에서는 서버 결과가 실패·복구 필요·확인 지연이어도 이 완료 문구가 나올 수 있다.

폴링 영역에는 결과를 다시 조회하는 단추나 「작업 ID」 표시가 없다. 실제 시작·중단 여부가 필요한 경우 초록색 문구만으로 판단하지 말고 운영 담당자에게 설비 ID·그룹 값·명령·발생 시각·표시 문구를 보내 결과 확인을 요청한다.

## PLC 폴링 영역에 오류가 표시됐을 때
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 오류 표시 L67~70
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 결과 영역 L116~126
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; API 실패 처리 L789~809
원천: nori-tc-ui-front/src/features/eqp/api/eqp.api.ts @ 9fe69a75dcd3 ; 중단 실패 처리 L832~852
원천: nori-tc-ui-front/src/shared/lib/resolve-error-message.ts @ 9fe69a75dcd3 ; 오류 문구 선택 L1~12
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 폴링 실패 기본 문구 L510~522
```

요청이나 결과 조회에서 오류가 발생하면 「Polling Group」 영역에 빨간 문구가 나온다. 전달된 오류 문구를 우선 표시하고 없으면 「폴링 그룹 명령에 실패했습니다.」를 표시한다.

이 영역에는 별도의 「재시도」 단추가 없다. 권한 거부이면 운영 관리자에게 권한을 확인한다. 그 밖의 오류는 설비 ID·그룹 값·Start 또는 End·발생 시각·문구를 운영 담당자에게 전달해 실제 적용 결과를 확인한 뒤 다시 명령한다.

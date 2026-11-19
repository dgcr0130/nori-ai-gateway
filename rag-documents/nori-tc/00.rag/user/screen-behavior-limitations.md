# 화면 안내와 실제 동작이 다른 곳
```rag-meta
유형: 알려진 제약
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-exec f1d5e0a8e727 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
```

## 아이디·비밀번호 찾기의 확인은 메일 발송이 아니다
```rag-meta
원천: nori-tc-ui-front/src/features/auth/components/LoginPage.tsx @ 9fe69a75dcd3 ; RecoveryModal L23~99
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 찾기 창 문구 L20~32
원천: nori-tc-exec/docs/completion-report/07-user-manual.md @ f1d5e0a8e727 ; 2.2 아이디·비밀번호 찾기 L103~140
```

확인한 소스 판본에서 「아이디 찾기」·「비밀번호 찾기」의 입력값은 서버로 전송되지 않는다. 「확인」을 누르면 창만 닫히므로 이메일 발송이나 비밀번호 변경이 접수된 상태가 아니다.

찾기 창을 반복해서 제출해도 복구 요청이 생기지 않는다. 아이디 확인이나 비밀번호 초기화는 운영 관리자에게 직접 요청한다.

## 그룹 권한 보유 수에는 저장하지 않은 체크도 들어간다
```rag-meta
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 변경분·집계·저장 L49~75
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 집계·저장·닫기 L115~201
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 집계 표시·단추 L214~221
원천: nori-tc-ui-front/src/features/group/components/GroupPermissionsModal.tsx @ 9fe69a75dcd3 ; 저장 단추 L290~308
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 그룹 권한 문구 L941~948
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 저장 문구 L723~723
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/GroupController.java @ 985d17c604dd ; 권한 변경 요청 L213~243
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiPrivilegeEscalationGuard.java @ 985d17c604dd ; 부여 가능한 권한 검사 L57~66
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/security/UiApiPermissionCache.java @ 985d17c604dd ; 요청별 보유 권한 검사 L142~174
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 그룹·권한 API 기본 권한 L142~148
원천: nori-tc-exec/docs/scripts/manual-permission-catalog.json @ f1d5e0a8e727 ; 권한 목록 기본 권한 L259~265
```

확인한 판본의 「그룹 권한 관리」에서 체크와 「권한 보유」 수는 저장 전 변경도 표시한다. 수가 바뀌어도 서버의 권한은 아직 바뀌지 않으며, 저장 없이 닫으면 변경을 버린다.

권한 변경은 「저장」으로 확정한다. 일부 요청만 실패하면 성공한 변경은 이미 반영되고 실패한 변경만 창에 남는다. 전체 변경이 한꺼번에 취소되지는 않는다.

저장소 기본 권한 데이터 기준으로 권한 목록 조회에는 PERMISSION_WRITE, 그룹 권한 조회·변경에는 GROUP_WRITE가 필요하다. 부여할 권한은 관리자 본인도 보유해야 한다. 권한 거부는 체크 미저장과 구별해 운영 관리자에게 대상 그룹·시각·표시 문구를 전달한다.

## 비밀번호 초기화 창의 안내는 요청 결과와 따로 표시된다
```rag-meta
원천: nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 초기화 요청·상태 L64~104
원천: nori-tc-ui-front/src/features/user/components/UserPasswordResetModal.tsx @ 9fe69a75dcd3 ; 설명·진행·오류 표시 L115~169
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 비밀번호 초기화 문구 L872~881
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/UserController.java @ 985d17c604dd ; resetPassword L254~267
```

확인한 판본의 「비밀번호 초기화」는 요청 중이거나 실패한 상태에도 초기화 완료형 설명을 표시한다. 화면에 임시 비밀번호가 보이는 것만으로 서버에 적용됐다고 판단할 수 없다.

「서버에 비밀번호 변경을 적용하는 중...」과 오류 표시를 기준으로 결과를 구별한다. 오류가 있으면 표시된 임시 값을 전달하지 않고, 운영 관리자에게 대상 사용자·요청 시각·오류 문구를 전달한다. 임시 비밀번호 자체는 문의 내용에 넣지 않는다.

## PLC 폴링의 초록색 안내로는 처리 결과를 구별할 수 없다
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpPage.tsx @ 9fe69a75dcd3 ; PLC 전용 영역 L1235~1243
원천: nori-tc-ui-front/src/features/eqp/components/EqpPollingGroupControl.tsx @ 9fe69a75dcd3 ; 발행 여부에 따른 완료 표시 L38~72
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 처리 결과 대신 발행 여부 반환 L424~453
원천: nori-tc-ui-front/src/shared/lib/operation-status.ts @ 9fe69a75dcd3 ; 실패·복구 필요·확인 지연 반환 L124~142
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/EqpController.java @ 985d17c604dd ; 폴링 명령 접수 L405~445
```

확인한 판본에서 PLC 설비의 「Polling Group」은 「Start」·「End」의 처리 결과가 실패·복구 필요·확인 지연이어도 초록색 완료 안내를 표시할 수 있다. 명령을 발행했다는 결과만 표시 판단에 사용하기 때문이다.

실제 폴링 시작·중단 여부를 확인해야 하면 운영 담당자에게 설비 ID·입력한 그룹 값·Start 또는 End·요청 시각·화면 문구를 전달한다. 이 영역의 초록색만으로 폴링 상태를 확정할 수 없다.

## Eqp Restart의 종료 단계가 지연되면 재조회가 시작을 이어 주지 않는다
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/components/EqpInfoTable.tsx @ 9fe69a75dcd3 ; Eqp Restart 메뉴 L265~272
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 종료 뒤 시작 조건 L236~247
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 성공 표시와 재조회 L258~274
원천: nori-tc-ui-front/src/features/eqp/hooks/useEqpLifecycleCommands.ts @ 9fe69a75dcd3 ; 재조회는 기존 작업만 조회 L360~378
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; Restart 제목 L48~53
원천: nori-tc-ui-front/src/features/eqp/components/EqpLifecycleResultDialog.tsx @ 9fe69a75dcd3 ; 제목과 재조회 단추 L103~139
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 재시작 완료 제목 L85~86
원천: nori-tc-ui-front/src/i18n/locales/ko.json @ 9fe69a75dcd3 ; 작업 ID·다시 조회 문구 L1110~1112
```

확인한 판본에서 「Eqp Restart」의 종료 단계가 확인 지연으로 끝나면 시작 단계는 발행되지 않는다. 이후 「다시 조회」로 종료 성공을 받아도 시작 단계는 자동으로 이어지지 않는다.

이 경로에서는 종료 성공을 재시작 완료 제목으로 표시할 수 있어 설비가 다시 시작됐다고 오인할 수 있다. 운영 담당자에게 설비 ID·Eqp Restart 요청 시각·확인 지연 뒤 재조회 여부·표시된 작업 ID·현재 화면 결과를 전달한다.

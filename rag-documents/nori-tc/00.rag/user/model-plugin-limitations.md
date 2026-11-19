# 모델·플러그인 적용 제약
```rag-meta
유형: 알려진 제약
적용: nori-tc-ui-front 9fe69a75dcd3·nori-tc-ui-backend 985d17c604dd·nori-tc-gateway d3636ab86763 소스 기준
확인일: 2026-11-19
담당: TC 문서 담당
관련문서: 00.rag/user/equipment-model-parameter-and-jar-update.md
```

## 모델 후보와 JAR 갱신 경로의 제한
```rag-meta
원천: nori-tc-ui-front/src/features/eqp/lib/eqp-management.util.ts @ 9fe69a75dcd3 ; getFilteredEqpModelOptions L153~165
원천: nori-tc-ui-backend/ui/adapter/tc-ui-web-adapter/src/main/java/com/nori/tc/ui/adapters/web/controller/support/EqpControllerCommandSupport.java @ 985d17c604dd ; toUpdateCommand L44~65
원천: nori-tc-ui-front/src/features/eqp/components/EqpManageFormModal.tsx @ 9fe69a75dcd3 ; Gateway Jar·등록 전용 구역 L949~980
원천: nori-tc-ui-front/src/features/eqp/components/EqpModelBindingModal.tsx @ 9fe69a75dcd3 ; handleSave L210~228
```

확인한 설비 화면 판본에서 모델 후보는 설비의 통신 방식과 개발·운영 구분에 제한된다. 설비 수정의 저장은 기존 모델·JAR 연결을 유지하므로 선택 칸이 보이는 것과 적용 가능 여부를 구별한다.

선택 조건과 등록은 `00.rag/user/equipment-registration-and-settings.md`, 실제 모델·JAR 갱신 경로는 `00.rag/user/equipment-model-parameter-and-jar-update.md`에서 확인한다.

## Gateway 기동만으로 모든 플러그인이 적용되지는 않는다
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 ; initialize·preloadAllFromDb L135~153 L295~395
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 ; 기동 정책 기대값 L68~128
```

확인한 Gateway 판본의 기동 시 미리 읽기는 SOCKET 설비만 대상으로 한다. 미리 읽기가 꺼져 있거나 선택한 Gateway JAR가 없거나 파일 내용이 비어 있으면 플러그인을 올리지 않는다.

적재 실패 시 즉시 중단하는 정책이면 기동이 실패하고, 계속 진행하는 정책이면 실패한 설비의 플러그인을 건너뛴다. 서비스가 기동됐다는 사실만으로 개별 플러그인의 적용을 확인할 수 없다.

운영 담당자에게 설비 ID·기동 시각·선택한 파일·발생 증상을 전달해 대상 플러그인의 적재 결과를 확인하도록 요청한다.

## 선택한 Socket Protocol을 JAR가 지원하지 않을 때
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-socket/src/main/java/com/nori/tc/gateway/socket/socketType/core/SocketTypeHandlerResolver.java @ d3636ab86763 ; resolveRequired L32~68
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 ; leaseHandler L182~190
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 ; 여러 처리기·없는 종류 L270~303
```

확인한 Gateway 판본은 설비 ID와 선택한 Socket Protocol에 맞는 플러그인 처리기를 먼저 찾는다. 같은 종류의 내장 처리기가 있어도 플러그인이 우선한다.

선택한 종류가 플러그인에 없으면 내장 처리기를 찾고, 내장 처리기도 없으면 처리가 실패한다. JAR 적재 성공과 선택한 프로토콜 지원 여부는 별개다.

모델·플러그인 담당자에게 설비 ID·Socket Protocol 값·선택한 JAR·실패 시각을 전달해 지원하는 종류를 대조하도록 요청한다.

## 비어 있거나 크기·파일 대조를 통과하지 못한 JAR
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 ; buildPluginRuntime·verifyExpectedJarLocator·validateJarBytes L50~62 L127~189
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 ; reloadByEqpId L231~289
```

확인한 Gateway 판본은 내용이 없는 JAR와 운영에서 정한 최대 바이트 수를 넘는 JAR를 적재하지 않는다. 갱신 요청에 기대 파일명이나 SHA-256이 있으면 실제 파일과 대조하고, 불일치하면 교체하지 않는다.

새 파일의 검증·적재가 실패해 교체 전 단계에서 끝나면 기존 플러그인은 유지된다. 운영 담당자에게 설비 ID·요청한 파일·실패 시각·표시 문구를 전달하고 파일 크기와 요청 대상 일치를 확인하도록 요청한다.

## 등록된 JAR도 허용 해시가 아니면 적용되지 않는다
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginArtifactSupport.java @ d3636ab86763 ; verifyJarHashAllowlistOrThrow L195~234
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeProperties.java @ d3636ab86763 ; 해시 검사 기본값 L75~97
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 ; 해시 차단·후속 적재 실패 L133~175
```

확인한 Gateway 판본에서 SHA-256 허용 목록 검사가 켜져 있으면 목록에 없는 JAR는 적재가 차단된다. 코드 기본값은 검사 사용이다. 파일 선택 목록에 있다는 사실만으로 허용된 플러그인이 되지는 않는다.

허용 해시를 통과해도 처리기 검사 등 후속 단계에서 실패할 수 있다. 운영 담당자에게 설비 ID·파일·실패 시각·화면 결과를 전달하고, 승인된 파일의 해시와 실제 적재 실패 단계를 대조하도록 요청한다.

## SDK 호환성 위반은 정책에 따라 차단되거나 경고된다
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginCompatibilitySupport.java @ d3636ab86763 ; verifyCompatibility·handleViolation L34~83 L108~135
원천: nori-tc-gateway/gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/GatewayActionApiVersion.java @ d3636ab86763 ; SDK 메이저·표준 패키지·호환 판정 L13~58
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeProperties.java @ d3636ab86763 ; 호환 정책 기본값 L94~97
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 ; 정책별 기대값 L210~265
```

확인한 Gateway 판본의 SDK 메이저는 1이다. 플러그인의 호환 선언 누락·해석 실패, 표준 패키지 불일치, 메이저 불일치는 FAIL_FAST 정책에서 적재를 차단한다.

기본 정책인 WARN_ONLY는 호환성 위반을 경고하고 다음 적재 검사를 계속한다. 경고 후 계속된 것을 호환성 검증 통과로 해석할 수 없다.

플러그인 담당자에게 JAR 판본·대상 Gateway 판본·실패 시각·표시 문구를 전달해 호환 선언과 적용 정책을 대조하도록 요청한다.

## Socket 처리기가 없거나 같은 종류가 중복된 JAR
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginDiscoverySupport.java @ d3636ab86763 ; discoverHandlersBySocketType·registerHandler L32~111
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManagerTest.java @ d3636ab86763 ; 복수 종류 허용·동일 종류 중복 거부 L270~338
```

확인한 Gateway 판본은 JAR 안에 지원하는 SocketTypeHandler 구현이 하나도 없거나 처리기의 socketType이 비어 있으면 적재를 거부한다. 같은 socketType을 두 처리기가 선언해도 JAR 전체의 적재가 실패한다.

서로 다른 socketType 처리기를 한 JAR에 여러 개 넣는 것은 허용한다. 플러그인 담당자에게 파일·적용할 Socket Protocol·실패 시각을 전달해 처리기 존재와 같은 종류의 중복 선언을 확인하도록 요청한다.

## 수신 처리기가 있어도 명령 송신을 지원하지 않을 수 있다
```rag-meta
원천: nori-tc-gateway/gateway/tc-gateway-action-contract/src/main/java/com/nori/tc/gateway/action/SocketTypeHandler.java @ d3636ab86763 ; encode 기본 구현 L41~48
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/subscribe/SocketOutboundFrameEncoder.java @ d3636ab86763 ; encodeSocketRawMessage L63~87
```

확인한 Gateway 판본에서 수신용 플러그인은 송신 인코딩을 구현하지 않아도 적재될 수 있다. 선택된 처리기가 encode를 구현하지 않으면 명령 송신 때 지원하지 않는 기능 오류가 발생한다. 인코딩 결과가 빈 바이트 배열이어도 송신을 거부한다.

수신 성공만으로 명령 송신 지원을 판단하지 않는다. 플러그인 담당자에게 설비 ID·Socket Protocol·JAR·보내려던 명령·발생 시각을 전달해 송신 구현을 확인하도록 요청한다.

## 시작된 설비의 Gateway JAR 교체는 통신을 멈췄다 이어 간다
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; updateGatewayJarfileRuntime L768~824
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlServiceTest.java @ d3636ab86763 ; 시작된 설비·종료된 설비 기대값 L980~1050 L1239~1279
```

확인한 Gateway 판본에서 Gateway JAR 갱신 처리가 실행되면, 시작 상태의 설비는 통신을 멈추고 플러그인을 다시 읽은 뒤 통신을 시작한다. 종료 상태의 설비는 플러그인만 다시 읽으며 자동으로 시작하지 않는다.

무중단 파일 교체로 취급할 수 없다. 운영 담당자에게 대상 설비·교체 파일·작업 시각·교체 전 시작 여부를 전달해 통신 영향과 작업 후 상태를 확인하도록 요청한다. 화면의 갱신 경로와 런타임 교체 지원은 구별한다.

## PASSIVE Socket이 같은 수신 주소에서 다른 JAR를 쓰려는 경우
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlSupport.java @ d3636ab86763 ; resolvePassiveBindingGroup·validatePassiveBindingGroupCompatibility L584~639
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; 교체 전 검사 L757~760
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/test/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlServiceTest.java @ d3636ab86763 ; JAR 변경 충돌 기대값 L1385~1433
```

확인한 Gateway 판본에서 같은 수신 IP·Port를 공유하는 PASSIVE Socket 설비는 Socket Protocol과 Gateway JAR 선택이 맞아야 한다. 같은 수신 주소를 쓰는 다른 설비와 JAR 선택이 달라지면 교체 전에 충돌로 거부한다.

한 설비만 바꾸려 해도 공유 설비 조건에 영향을 받는다. 운영 담당자에게 대상 설비 ID·공유하는 설비 ID·현재 파일과 변경할 파일·충돌 시각·표시 문구를 전달해 공유 조건을 대조하도록 요청한다.

## 이전 플러그인의 사용 종료를 기다리는 데는 한도가 있다
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeManager.java @ d3636ab86763 ; swapRuntime L423~473
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeModels.java @ d3636ab86763 ; drainAndCloseQuietly·awaitDrain L160~195
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/main/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginRuntimeProperties.java @ d3636ab86763 ; 대기 기본값 L67~73
원천: nori-tc-gateway/gateway/adapter/tc-gateway-plugin-adapter/src/test/java/com/nori/tc/gateway/adapters/plugin/socket/GatewaySocketPluginHandleDrainTest.java @ d3636ab86763 ; 반납 대기·한도 초과 기대값 L32~86
```

확인한 Gateway 판본은 새 플러그인으로 교체한 뒤 이전 처리기의 사용 종료를 기다린다. 대기 기본값은 5,000ms이며, 한도를 넘거나 대기가 중단되면 사용 중인 처리기가 남아 있어도 이전 JAR 로더를 닫는다.

교체 완료만으로 이전 처리 중인 작업의 안전한 종료까지 보장되지 않는다. 교체 시점의 처리 실패는 운영 담당자에게 설비 ID·이전 파일과 새 파일·교체 시각·실패 메시지를 전달해 사용 종료 대기 결과를 확인하도록 요청한다.

## PLC는 Gateway Socket 플러그인 교체 대상이 아니다
```rag-meta
원천: nori-tc-gateway/gateway/adapter/tc-gateway-kafka-adapter/src/main/java/com/nori/tc/gateway/adapters/kafka/ui/GatewayUiRuntimeControlService.java @ d3636ab86763 ; PLC 갱신 거부 L744~755
```

확인한 Gateway 판본은 PLC 설비의 Gateway JAR 갱신 요청을 거부한다. Socket 플러그인 JAR를 바꾸는 방식으로 PLC 통신 처리를 교체할 수 없다.

PLC 변경이 필요하면 설비·모델 담당자에게 설비 ID·바꾸려는 통신 처리·대상 파일·거부 시각과 문구를 전달해 변경 범위를 확인하도록 요청한다.

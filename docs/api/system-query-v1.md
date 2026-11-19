# 시스템 표준 조회 v1

등록 시스템의 backendUrl에 `POST /internal/ai/v1/query`를 1회 보냅니다. 시스템 token 파일은 Bearer 자격으로, 검증한 subject·위임은 `X-Actor-Subject`·`X-Delegation-Id`로 전달합니다. `X-Request-Id`와 body.jobId는 실행 Job ID입니다. 다른 시스템의 자격·주소를 선택할 수 없습니다.

요청은 `{version:"1",systemId,jobId,toolId,subject,delegationId,permissionDigest,deadline,arguments,targets}`입니다. 입력 시간은 요청 timezone으로 해석한 UTC ISO8601로 변환합니다. 응답은 `{version:"1",systemId,jobId,toolId,status,observedAt,data,missingFields,warnings,retentionMeta,errorCode}`이며 identity와 상태별 HTTP·필드를 검증합니다. 자료는 `data.items:[{ref,text,values}]`이고 config outputSchema는 단건 values에 적용합니다. backend가 최종 리소스 권한을 확인합니다.

요청 UTF-8 64KiB, 응답 1MiB 한도, 연결 최대 2초와 도구 deadline을 적용하며 재시도하지 않습니다. 전송 전 만료·취소는 송신하지 않습니다. 상태·JSON·필드·크기 오류를 성공 0건으로 바꾸지 않습니다. 관측 자료는 마스킹 후 공통 결과·블록·감사로 전달하며 업무 응답을 합치거나 해석하지 않습니다.

도구는 시스템 config tools와 apps·traces·repositories가 활성화하는 logs.search·trace.search·git.searchCode로 구성합니다. 한 번 분류한 실행 목록 0~4개를 실행하며 공통 호출 예산 12회·동일 도구 3회, 문서 회차 2회·보완 1회를 유지합니다. 추론 활성 모드에서 KNOWLEDGE_QUERY·INCIDENT_ANALYSIS는 같은 예산으로 규칙 문서 검색을 실행하고 LIVE_QUERY는 선택한 비문서 조회만 사용합니다. 예산 안에 선택한 전체 근거 단위가 충분하지 않으면 작성 호출 없이 answer.kind=NO_EVIDENCE로 반환하며 원천 장애·부분 상태를 보존합니다.

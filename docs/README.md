# 문서

이 저장소의 외부 계약·운영 절차·배포 선행조건 문서입니다. SDD 정본(SPEC·PLAN)은 중앙 저장소에 있습니다.

| 문서 | 내용 |
|---|---|
| [api/](api/) | 외부 계약: Gateway API(`gateway-api-v1`), 인증(`auth-v1`), TC AI 읽기 API(`tc-ai-read-api-v1`), 시험 hook(`test-hooks-v1`) |
| [runbooks/](runbooks/) | 운영 절차: DB 서버 선행조건, 런타임 자격, 검증 자격, 추론 서버 운영 |
| [deployment-prerequisites.md](deployment-prerequisites.md) | 배포 선행조건 상태: 배포 입력 승인 기록 위치, 관측 수신·경보 전달, Source 선택자 |
| [stack-compatibility.md](stack-compatibility.md) | 고정 기술 스택 호환 확인 기록 |
| [rag-document-guide.md](rag-document-guide.md) | TC RAG 지식 문서 준비 가이드(원본 목록·신규 작성 문서·SeaweedFS 반입 기준). 승인 SPEC·PLAN이나 실제 수집 완료를 대신하지 않습니다. |

## SDD 문서 (중앙 저장소)

[nori-sdd-specs/work-items/005-ai-gateway/](../../nori-sdd-specs/work-items/005-ai-gateway/)

| 문서 | 내용 |
|---|---|
| [spec.md](../../nori-sdd-specs/work-items/005-ai-gateway/spec.md) | 승인 SPEC. 바이트가 바뀌면 기준선 사슬 [acceptance/spec-baseline.md](../acceptance/spec-baseline.md)에 root 승인 항목이 추가될 때까지 `spec-guard`가 실패합니다. |
| [spec-plan.md](../../nori-sdd-specs/work-items/005-ai-gateway/spec-plan.md) | 구현 PLAN. 첫 절에 2026-11-16 구조 정리의 옛 경로 → 새 경로 대응표가 있습니다. |
| [implementation-log.md](../../nori-sdd-specs/work-items/005-ai-gateway/implementation-log.md) | 구현 기록 |
| [spec-approvals.md](../../nori-sdd-specs/work-items/005-ai-gateway/spec-approvals.md) | 기준선 seq 2~18 승인 근거 |
| [design-review.md](../../nori-sdd-specs/work-items/005-ai-gateway/design-review.md) | 2026-11-13 설계 재검증 보고서 2개(권고) |

TC 연동(공통 AI 영구 대화·TC 최초 연동) SPEC은 [004-tc-ai-integration/spec.md](../../nori-sdd-specs/work-items/004-tc-ai-integration/spec.md)(v2.0)이고, 이전 v1.1은 같은 폴더의 `spec-v1.md`입니다.

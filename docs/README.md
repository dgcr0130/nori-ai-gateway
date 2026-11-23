# 문서

이 저장소의 외부 계약·운영 절차·배포 선행조건 문서입니다. SDD 정본(SPEC·PLAN)은 중앙 저장소에 있습니다.

| 문서 | 내용 |
|---|---|
| [api/](api/) | 외부 계약: Gateway API(`gateway-api-v1`), 인증(`auth-v1`), 시스템 표준 조회 API(`system-query-v1`), 시험 hook(`test-hooks-v1`) |
| [runbooks/](runbooks/) | 운영 절차: DB 서버 선행조건, 운영 런타임 자격, 추론 서버 운영 |
| [deployment-prerequisites.md](deployment-prerequisites.md) | 배포 선행조건 상태: 배포 입력 승인 기록 위치, 관측 수신·경보 전달, Source 선택자 |
| [stack-compatibility.md](stack-compatibility.md) | 고정 기술 스택 호환 확인 기록 |
| [rag-document-writing-rules.md](rag-document-writing-rules.md) | 모든 시스템의 RAG 원문 Markdown 작성·검사·갱신 규칙 |
| [tc-rag-document-list.md](tc-rag-document-list.md) | 2026-11-18 사용자가 확정한 TC RAG 문서 53개의 경로·구획·유형·필요 이유·원천·미확인 부분 |

## SDD 문서 (중앙 저장소)

[nori-sdd-specs/work-items/005-ai-gateway/](../../nori-sdd-specs/work-items/005-ai-gateway/)

| 문서 | 내용 |
|---|---|
| [spec.md](../../nori-sdd-specs/work-items/005-ai-gateway/spec.md) | 승인 SPEC. 제품 시험과 증거 검증은 SPEC 파일·기준선 입력 없이 실행합니다. |
| [spec-plan.md](../../nori-sdd-specs/work-items/005-ai-gateway/spec-plan.md) | 구현 PLAN. 첫 절에 2026-11-16 구조 정리의 옛 경로 → 새 경로 대응표가 있습니다. |
| [implementation-log.md](../../nori-sdd-specs/work-items/005-ai-gateway/implementation-log.md) | 구현 기록 |
| [spec-approvals.md](../../nori-sdd-specs/work-items/005-ai-gateway/spec-approvals.md) | 기준선 seq 2~18 승인 근거 |
| [design-review.md](../../nori-sdd-specs/work-items/005-ai-gateway/design-review.md) | 2026-11-13 설계 재검증 보고서 2개(권고) |

TC 연동(공통 AI 영구 대화·TC 최초 연동) SPEC은 [004-tc-ai-integration/spec.md](../../nori-sdd-specs/work-items/004-tc-ai-integration/spec.md)(v2.0)이고, 이전 v1.1은 같은 폴더의 `spec-v1.md`입니다.

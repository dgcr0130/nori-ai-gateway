@AGENTS.md

# Claude Code SDD 진입 규칙

이 시스템의 SDD 정본은 중앙 저장소 `../nori-sdd-specs/work-items/005-ai-gateway/`에 있다. SDD를 요청받으면 중앙 `AGENTS.md`·`README.md`와 다음 문서를 읽는다.

1. `docs/README.md`
2. 승인 SPEC `../nori-sdd-specs/work-items/005-ai-gateway/spec.md`
3. PLAN `../nori-sdd-specs/work-items/005-ai-gateway/spec-plan.md`(첫 절 경로 대응표, §0 문서 메타·Plan 상태, §6 실행 순서, 대상 phase 또는 정렬 단위 U1~U8)
4. 구현 기록 `../nori-sdd-specs/work-items/005-ai-gateway/implementation-log.md`

제품 시험과 증거 검증은 SPEC 파일·기준선·SPEC 전용 환경변수 없이 실행한다(`AGENTS.md` §5).

사용자가 요청한 phase·단계에서 멈춘다. 프로젝트 구조·코딩·빌드·시험 규칙은 `AGENTS.md` 를 따른다.

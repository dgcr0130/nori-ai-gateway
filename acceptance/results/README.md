# 수용 결과

단계별 수용 시험 결과의 정본입니다. 정제 JSON과 단계별 판정 보고서 `acceptance-report.md`만 보관합니다. 원시 OTR XML, SPEC 본문, 질문·답변·Prompt·자격 원문은 넣지 않습니다. 아직 실행되지 않은 업무 AC/ISO는 NOT_RUN입니다.

## 단계 폴더

| 폴더 | 단계 | 레코드의 `stage` 값 |
|---|---|---|
| `gateway-foundation/` | Gateway 기반 | F1 |
| `llm-integration/` | LLM 실연동 | F2 |
| `rag-integration/` | RAG 실연동 | F3 |
| `tc-integration/` | TC 통합 | F4 |
| `tc-integration-performance/` | TC 통합 성능 측정 로그 | F4(성능) |
| `inference-diagnosis/` | 종료된 추론 진단(로컬 전용, git 제외) | F0 |

폴더 대응은 `EvidenceRepositoryFs`(수용 기준 점검 CLI)와 `operations/deploy-checks/result_folders.py`가 같은 값을 씁니다.

## 읽는 범위

판정은 단계 폴더 아래 `runs/**`, `measurements/**`, `(?:AC|ISO)-[0-9]{2}(?:-[A-Z0-9]+)*` 문법의 leaf 폴더, 직접 생산 호환 경로 `perf/**`, `user-run/**`의 모든 JSON을 읽습니다. 파일명이나 `kind` 유무로 건너뛰지 않으며 파싱·해시·레코드 검증을 그대로 적용합니다.

`deploy-checks/**`(배포·자격·rollback 점검 기록), 단계 폴더 바로 아래의 로그·런타임 classpath 목록 같은 운영 기록은 판정 레코드가 아닙니다. `ops/**`의 운영 원본은 명시적으로 ingest한 뒤 정본 저장 경로에서 읽습니다. 저장된 레코드의 바이트·파일명·해시는 변경하지 않습니다.

#!/usr/bin/env bash
# 단계 게이트(Plan P15·§4.1·§11.9). 사용: --stage <suite> --image-digests <f> --runconfig <f> --test-timeout-seconds <n> [suite 전용 인자]
# stdout = 새 부모 회차면 AIGW_PARENT_RUN_ID=<p> 한 줄. exit 0 은 모든 단계 실행·기록일 뿐 게이트 PASS 가 아니다(acceptance-report.md). 본체는 stage_gate.py.
set -euo pipefail
set +x
export PYTHONDONTWRITEBYTECODE=1
[[ -f operations/deploy/scripts/runconfig/verify.schema.json && -f operations/deploy-checks/stage_gate.py ]] || { printf '%s\n' AI_ROOT_REQUIRED >&2; exit 2; }
exec python3 -B operations/deploy-checks/stage_gate.py "$@"

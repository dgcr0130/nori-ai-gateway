"""수용 결과 저장 위치. 단계 식별값(F1 등)은 레코드 데이터이고 폴더 이름은 단계 내용으로 정한다.

Java EvidenceRepositoryFs(STAGE_FOLDERS·RESULTS_ROOT)와 같은 대응을 쓴다. F4-PERF 는 성능 측정 로그 전용 폴더다.
"""
import pathlib

RESULTS_ROOT = pathlib.Path('acceptance/results')
STAGE_FOLDERS = {
    'F0': 'inference-diagnosis',
    'F1': 'gateway-foundation',
    'F2': 'llm-integration',
    'F3': 'rag-integration',
    'F4': 'tc-integration',
    'F4-PERF': 'tc-integration-performance',
}


def stage_dir(stage):
    """단계 결과 폴더(레포 루트 기준 상대 경로). 알 수 없는 단계는 KeyError 로 거부한다."""
    return RESULTS_ROOT / STAGE_FOLDERS[stage]

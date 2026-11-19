package com.nori.ai.gateway.core.knowledge;

/** 후보 폭과 충분성 값은 검색 품질 보증이 아닌 설정 계약입니다. */
public record SearchSettings(int semanticCandidates, int keywordCandidates, int identifierCandidates, int rrfK,
        int evidenceLimit, int verifiedCandidates, double verifiedMinCosine, double minSemanticCosine,
        int coreTermLimit, int retryCount) {
    public SearchSettings {
        range(semanticCandidates,1,100); range(keywordCandidates,1,100); range(identifierCandidates,1,100);
        range(rrfK,1,100); range(evidenceLimit,1,10); range(verifiedCandidates,1,10); range(coreTermLimit,1,8);
        if(retryCount!=1 || !Double.isFinite(verifiedMinCosine) || verifiedMinCosine<0 || verifiedMinCosine>1
                || !Double.isFinite(minSemanticCosine) || minSemanticCosine<0 || minSemanticCosine>1)
            throw new IllegalArgumentException("검색 설정이 잘못되었습니다");
    }
    private static void range(int value,int min,int max) { if(value<min||value>max)throw new IllegalArgumentException("검색 설정이 잘못되었습니다"); }
    public static SearchSettings defaults() { return new SearchSettings(20,20,20,60,6,3,.90,.35,4,1); }
}

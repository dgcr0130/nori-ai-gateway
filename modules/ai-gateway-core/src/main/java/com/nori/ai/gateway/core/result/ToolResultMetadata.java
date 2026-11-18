package com.nori.ai.gateway.core.result;

import com.nori.ai.gateway.core.scope.OwnedReference;
import java.time.Instant;
import java.util.List;
public record ToolResultMetadata(OwnedReference sourceRef, String sourceFreshnessPolicy, boolean truncated, List<MissingRange> missingRanges,List<MissingRange> unverifiedRanges) {
    public ToolResultMetadata(OwnedReference sourceRef,String policy,boolean truncated,List<MissingRange> ranges){this(sourceRef,policy,truncated,ranges,List.of());}
    public record MissingRange(Instant from, Instant to) {}
    public ToolResultMetadata { missingRanges = List.copyOf(missingRanges); unverifiedRanges=unverifiedRanges==null?List.of():List.copyOf(unverifiedRanges); }
}

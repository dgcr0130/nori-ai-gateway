package com.nori.ai.gateway.core.feedback;

import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.util.Map;

@FunctionalInterface
public interface ReviewedGlossary {
    Map<String,String> current(VerifiedSystemScope scope,Map<String,String> initial);
}

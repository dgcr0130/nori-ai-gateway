package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.plan.*;
import com.nori.ai.gateway.core.port.SystemQueryPort;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.time.Clock;
import java.util.*;
/** 업무 자료의 해석·결합 없이 표준 응답을 공통 실행 결과로 투영합니다. */
public final class BusinessToolExecutor implements ToolExecutor {
    private final SystemQueryPort systems;private final Clock clock;
    public BusinessToolExecutor(SystemQueryPort systems,Clock clock){this.systems=systems;this.clock=clock;}
    @Override public ToolKind toolKind(){return ToolKind.BUSINESS;}
    @Override public ToolResultEnvelope execute(PlanStep step,CapabilityManifest manifest,VerifiedSystemScope scope,ExecutionContext context){
        if(!(context.resolver() instanceof WorkflowResourceResolver resolver))throw new IllegalArgumentException("작업 실행 결속이 없습니다");
        resolver.beforeRead();var response=systems.query(new SystemQueryPort.Request(scope,manifest,step.arguments(),step.resources(),resolver.delegatedCall().dlg(),resolver.timezone(scope),resolver.cancellation()));resolver.beforeRead();
        List<?> items=response.data()==null?List.of():SchemaContracts.JSON.convertValue(response.data().path("items"),List.class);
        var result=ToolResults.result(manifest,scope,response.status(),items,response.observedAt(),response.missingFields(),response.warnings().stream().map(Warning::valueOf).toList(),response.missingRanges().stream().map(value->new ToolResultMetadata.MissingRange(value.from(),value.to())).toList(),null);
        var metadata=new ToolResultMetadata(result.metadata().sourceRef(),result.metadata().sourceFreshnessPolicy(),response.missingFields().contains("truncatedItems"),result.metadata().missingRanges(),response.unverifiedRanges().stream().map(value->new ToolResultMetadata.MissingRange(value.from(),value.to())).toList());
        boolean invalid="PLAN_INVALID".equals(response.errorCode());
        return new ToolResultEnvelope(result.toolExecutionId(),result.capabilityId(),result.contractVersion(),result.schemaDigest(),result.ownerSystem(),result.status(),result.dataRef(),result.evidenceRefs(),result.observedAt(),result.sourceRevision(),result.missingFields(),result.warnings(),invalid?ErrorCode.PLAN_INVALID:result.errorCode(),result.traceId(),metadata,invalid?new Clarification(ErrorCode.PLAN_INVALID,List.of("arguments")):null,result.blocks(),result.claims(),result.evidence(),result.citations(),result.composerInputs());
    }
}

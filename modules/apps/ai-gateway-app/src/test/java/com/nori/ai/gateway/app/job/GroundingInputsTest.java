package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.workflow.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class GroundingInputsTest {
    static final Instant NOW=Instant.parse("2026-11-19T00:00:00Z");
    final VerifiedSystemScope scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("READ","EXTRA"),"pdg",NOW.minusSeconds(1),NOW.plusSeconds(60),"job");
    final JobContext job=new JobContext("job","conversation",scope,"d".repeat(22),new CancellationToken(),InferenceMode.ENABLED,Map.of("taskType","LIVE_QUERY","toolCalls",List.of(),"targets",List.of()),new JobRequestInput("message",1,"설비 상태 T3",Map.of()));
    final List<CapabilityManifest> tools=List.of(tool("fixture.system",ToolKind.BUSINESS),tool("logs.search",ToolKind.LOG),tool("trace.search",ToolKind.TRACE),tool("git.searchCode",ToolKind.GIT));
    CapabilityManifest tool(String id,ToolKind kind){return ConfiguredTool.create(OwnerSystem.TC,id,"시험 조회",kind,SchemaContracts.parse("{\"type\":\"object\"}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("READ"),10000);}
    GroundingInputs inputs(){return inputs(List.of("READ"));}
    GroundingInputs inputs(List<String> sourcePermissions){
        var config=new GatewaySystemsProperties.SystemConfig("fixture","digest","jwks",null,null,List.of(),List.of(),List.of(new GatewaySystemsProperties.App("app","ns",sourcePermissions)),List.of(),List.of(new GatewaySystemsProperties.Trace("service","ns",sourcePermissions)),"REVIEW",Map.of());
        return new GroundingInputs(new GatewaySystemsProperties(Map.of("TC",config)),new CapabilityRegistry(tools,Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy()),new ContentSanitizer());
    }
    WorkflowOutcome outcome(List<List<Map<String,Object>>> rows) {
        var stages=new ArrayList<WorkflowOutcome.Stage>();for(int i=0;i<rows.size();i++) {
            var result=ToolResults.result(tools.get(i),scope,ResultStatus.SUCCESS,rows.get(i),NOW,List.of(),Set.of(),List.of(),null);
            stages.add(new WorkflowOutcome.Stage("call-"+(i+1),result,true,rows.get(i).size()));
        }
        return new WorkflowOutcome(stages,List.of(),List.of(),new ResultSourceDeps(List.of(),List.of()),List.of(),List.of(),Optional.empty());
    }
    @Test void sourceUnitsKeepArrayOrderLocationsValuesMaskingAndCanonicalBodies() {
        var system=List.<Map<String,Object>>of(Map.of("ref","EQP-002","text","설비 상태 T3 operator@example.com","values",Map.of("id","one")),Map.of("ref","EQP-001","text","별도 항목","values",Map.of("id","two")));
        var logs=List.<Map<String,Object>>of(Map.of("app","app","namespace","ns","occurredAt",NOW.minusSeconds(1).toString(),"text","오래된 줄"),Map.of("app","app","namespace","ns","occurredAt",NOW.toString(),"text","최근 줄"));
        var span1=Map.<String,Object>of("service","service","namespace","ns","spanId","s","name","두번째","startTimeUnixNano","10","endTimeUnixNano","11","serviceVersion","");
        var span2=new HashMap<>(span1);span2.put("startTimeUnixNano","2");span2.put("endTimeUnixNano","3");span2.put("name","첫번째");
        var traces=List.<Map<String,Object>>of(Map.of("traceId","t","spans",List.of(span1,span2)));
        var git=List.<Map<String,Object>>of(Map.of("sourceRepo","repo","path","b.java","ref","main","text","코드 B","codeRefKind","CURRENT_APPROVED_REF","deployedSourceCommit","UNKNOWN","missingEvidence",List.of()),Map.of("sourceRepo","repo","path","a.java","ref","main","text","코드 A"));
        var found=inputs().collect(job,outcome(List.of(system,logs,traces,git)));assertThat(found.invalidSteps()).isEmpty();assertThat(found.units()).hasSize(8);
        assertThat(found.units().get(0).body()).contains("설비 상태 T3").doesNotContain("operator@example.com","two");assertThat(found.units().get(0).values().path("id").asString()).isEqualTo("one");
        assertThat(found.units().get(1).location().path("ref").asString()).isEqualTo("EQP-001");
        assertThat(found.units().get(2).body()).isEqualTo("최근 줄");assertThat(found.units().get(3).body()).isEqualTo("오래된 줄");
        assertThat(found.units().get(4).location().path("startTimeUnixNano").asString()).isEqualTo("2");assertThat(found.units().get(4).location().path("serviceVersion").asString()).isEmpty();
        assertThat(found.units().get(6).location().path("path").asString()).isEqualTo("a.java");assertThat(found.units().get(7).body()).contains("CURRENT_APPROVED_REF","UNKNOWN","missingEvidence");
        assertThat(found.units()).allSatisfy(u->{assertThat(u.permissions()).containsExactly("READ");assertThat(u.status()).isEqualTo("SUCCESS");assertThat(u.observedAt()).isEqualTo(NOW.toString());});
    }
    @Test void missingRequiredLocationOrBodyRejectsTheStepAndZeroItemsIsZeroUnits() {
        assertThat(inputs().collect(job,outcome(List.of(List.of()))).units()).isEmpty();
        for(String field:List.of("ref","text","values")) {
            var row=new HashMap<String,Object>(Map.of("ref","EQP-001","text","설비 상태","values",Map.of()));row.remove(field);
            var found=inputs().collect(job,outcome(List.of(List.of(row))));assertThat(found.units()).isEmpty();assertThat(found.invalidSteps()).containsExactly("call-1");
        }
    }
    @Test void malformedLogTimeAndGitProvenanceAreContractFailuresRatherThanUsableEvidence() {
        var log=Map.<String,Object>of("app","app","namespace","ns","occurredAt","invalid-time","text","설비 상태 T3");
        var invalidLog=inputs().collect(job,outcome(List.of(List.of(),List.of(log))));assertThat(invalidLog.units()).isEmpty();assertThat(invalidLog.invalidSteps()).containsExactly("call-2");
        var git=Map.<String,Object>of("sourceRepo","repo","path","a.java","ref","main","text","설비 상태 T3","missingEvidence",3);
        var invalidGit=inputs().collect(job,outcome(List.of(List.of(),List.of(),List.of(),List.of(git))));assertThat(invalidGit.units()).isEmpty();assertThat(invalidGit.invalidSteps()).containsExactly("call-4");
    }
    @Test void sourcePermissionOrderComesFromTheConfigArrayAndMembershipStillRequiresAllPermissions() {
        var log=Map.<String,Object>of("app","app","namespace","ns","occurredAt",NOW.toString(),"text","설비 상태 T3");
        var found=inputs(List.of("EXTRA","READ")).collect(job,outcome(List.of(List.of(),List.of(log))));
        assertThat(found.invalidSteps()).isEmpty();assertThat(found.units()).singleElement().satisfies(u->assertThat(u.permissions()).containsExactly("EXTRA","READ"));
        assertThat(EvidenceSelection.json(found.units())).contains("\"permissions\":[\"EXTRA\",\"READ\"]");
        assertThat(inputs(List.of("EXTRA","MISSING")).collect(job,outcome(List.of(List.of(),List.of(log)))).invalidSteps()).containsExactly("call-2");
    }
    @Test void gitOrderingUsesUtf8BytesForPathsRatherThanUtf16CodeUnits() {
        String earlier="src/\uE000.java",later="src/\uD800\uDC00.java";
        var rows=List.<Map<String,Object>>of(Map.of("sourceRepo","repo","path",later,"ref","main","text","설비"),Map.of("sourceRepo","repo","path",earlier,"ref","main","text","설비"));
        var found=inputs().collect(job,outcome(List.of(List.of(),List.of(),List.of(),rows)));
        assertThat(found.invalidSteps()).isEmpty();assertThat(found.units()).extracting(u->u.location().path("path").asString()).containsExactly(earlier,later);
    }
}

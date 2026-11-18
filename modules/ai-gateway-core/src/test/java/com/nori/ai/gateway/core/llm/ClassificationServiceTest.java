package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ErrorCode;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.node.ObjectNode;
import java.util.*;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static org.assertj.core.api.Assertions.*;

class ClassificationServiceTest {
    static final CapabilityManifest TOOL=ConfiguredTool.create(SCOPE.boundSystem(),"lookup","조회",ToolKind.BUSINESS,
        SchemaContracts.JSON.valueToTree(Map.of("type","object","additionalProperties",false,"properties",Map.of("targetRef",Map.of("type","string","minLength",1,"maxLength",64)),"required",List.of("targetRef"))),SchemaContracts.JSON.valueToTree(Map.of("type","object")),List.of("EQP_READ"),10000);
    static RegistryView view(){return new RegistryView(SCOPE.boundSystem(),List.of(TOOL));}
    static ObjectNode wire(){return (ObjectNode)SchemaContracts.parse("{\"taskType\":\"LIVE_QUERY\",\"toolCalls\":[],\"targets\":[],\"timeWindow\":null,\"searchQuery\":\"질문\",\"retryQuery\":\"두 번째 질문\"}");}
    static ClassificationService.Request request(String question){return new ClassificationService.Request(SCOPE,"conversation","job-1",new CancellationToken(),new JobRequestInput("message",1,question,Map.of("context",Map.of("timezone","UTC","referenceTime",NOW.toString()))));}
    static ClassificationService service(ScriptedClient client){return service(client,new ClassificationService.Context(view(),List.of(),Map.of(),2),messages->100,attempt->{});}
    static ClassificationService service(ScriptedClient client,ClassificationService.Context context,PromptTokenCounting counter,LlmCallAudit audit){return new ClassificationService(client,TokenBudgetPolicy.current(),CODEPOINTS,counter,new ExpiryCheckpoint(),LlmCallObserver.NONE,audit,"분류 지시",ignored->context);}
    @Test void sixFieldsArePreservedAndExactlyOneCallUsesTheRegisteredTools(){
        var node=wire();node.withArray("toolCalls").addObject().put("toolId","lookup").set("arguments",SchemaContracts.parse("{\"targetRef\":\"EQP_001\"}"));node.withArray("targets").addObject().put("type","EQP").put("ref","EQP_001");node.put("searchQuery","EQP_001 질문");
        var client=new ScriptedClient(reply(node.toString()));var result=(ClassificationService.Classified)service(client).classify(request("EQP_001 질문"));
        assertThat(result.calls()).isOne();assertThat(result.classification().toolCalls()).hasSize(1);assertThat(result.classification().jobClassification().keySet()).containsExactlyInAnyOrder("taskType","toolCalls","targets","timeWindow","searchQuery","retryQuery");
        assertThat(client.sent).singleElement().satisfies(sent->{assertThat(sent.maxTokens()).isEqualTo(2048);var input=SchemaContracts.parse(sent.messages().get(1).content());assertThat(input.path("tools").get(0).path("inputSchema")).isEqualTo(TOOL.inputSchema());});
    }
    @Test void invalidJsonUnknownKeysToolsTargetsAndFiveCallsFailWithoutRetry(){
        List<String> invalid=new ArrayList<>(List.of("not json",wire()+" {}"));
        for(String key:wire().propertyNames()){var copy=wire();copy.remove(key);invalid.add(copy.toString());}
        var unknown=wire();unknown.put("systemId","WM");invalid.add(unknown.toString());
        var tool=wire();tool.withArray("toolCalls").addObject().put("toolId","unknown").set("arguments",SchemaContracts.parse("{}"));invalid.add(tool.toString());
        var target=wire();target.withArray("targets").addObject().put("type","EQP").put("ref","invented");invalid.add(target.toString());
        var over=wire();for(int i=0;i<5;i++)over.withArray("toolCalls").addObject().put("toolId","lookup").set("arguments",SchemaContracts.parse("{}"));invalid.add(over.toString());
        for(String text:invalid){var client=new ScriptedClient(reply(text));assertThat(service(client).classify(request("질문"))).as(text).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));assertThat(client.sent).hasSize(1);}
    }
    @Test void missingRequiredInputReturnsOnlyTheMissingFieldsAndDoesNotInventArguments(){var node=wire();node.withArray("toolCalls").addObject().put("toolId","lookup").set("arguments",SchemaContracts.parse("{}"));var client=new ScriptedClient(reply(node.toString()));assertThat(service(client).classify(request("질문"))).isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("targetRef")));assertThat(client.sent).hasSize(1);}
    @Test void exactLiteralTargetsUseOnlyTheHistoryThatWasActuallySent(){var node=wire();node.withArray("targets").addObject().put("type","EQP").put("ref","OLD");node.put("searchQuery","OLD 질문");var context=new ClassificationService.Context(view(),List.of(new ClassificationService.HistoryPair("OLD 질문","답"),new ClassificationService.HistoryPair("NEW 질문","답")),Map.of(),1);var client=new ScriptedClient(reply(node.toString()));assertThat(service(client,context,messages->100,attempt->{}).classify(request("질문"))).isInstanceOf(ClassificationService.Rejected.class);assertThat(client.sent.getFirst().messages().get(1).content()).doesNotContain("OLD").contains("NEW");}
    @Test void tokenOverflowDropsOldestPairsThenStopsBeforeSendingIfTheBaseIsTooLarge(){var context=new ClassificationService.Context(view(),List.of(new ClassificationService.HistoryPair("OLD","답"),new ClassificationService.HistoryPair("NEW","답")),Map.of(),2);var client=new ScriptedClient(reply(wire().toString()));assertThat(service(client,context,messages->messages.toString().contains("OLD")?6000:100,attempt->{}).classify(request("질문"))).isInstanceOf(ClassificationService.Classified.class);assertThat(client.sent.getFirst().messages().toString()).contains("NEW").doesNotContain("OLD");var absent=new ScriptedClient();assertThat(service(absent,context,messages->6000,attempt->{}).classify(request("질문"))).isEqualTo(new ClassificationService.BudgetExceeded(0));assertThat(absent.sent).isEmpty();}
    @Test void transportBudgetAndMalformedRepliesLeaveExactlyOneTextFreeAudit(){for(var reply:List.of(reply("secret malformed"),new InferenceClient.CallFailed(InferenceClient.FailureReason.TRANSPORT,"java.io.IOException",java.time.Duration.ofMillis(7),120),InferenceClient.ChatOutcome.budgetExceeded(6000))){var records=new ArrayList<LlmCallAudit.Attempt>();var client=new ScriptedClient(reply);service(client,new ClassificationService.Context(view(),List.of(),Map.of(),2),messages->100,records::add).classify(request("질문"));assertThat(records).hasSize(1);assertThat(records.toString()).doesNotContain("secret malformed","질문");}}
    @Test void cancellationBeforeAssemblyMakesNoCall(){var client=new ScriptedClient();var request=request("질문");request.cancellation().signal(CancellationToken.Reason.CANCELLED);assertThatThrownBy(()->service(client).classify(request)).isInstanceOf(IllegalStateException.class);assertThat(client.sent).isEmpty();}
    @Test void timezoneAndHalfOpenTimeWindowAreValidated(){for(String from:List.of("2026-02-30T00:00Z","2026-10-04T00:00Z","2026-10-03T00:00:00.1234567890Z")){var node=wire();node.set("timeWindow",SchemaContracts.JSON.valueToTree(Map.of("from",from,"to","2026-10-04T00:00Z")));assertThat(service(new ScriptedClient(reply(node.toString()))).classify(request("질문"))).isInstanceOf(ClassificationService.Rejected.class);}}
}

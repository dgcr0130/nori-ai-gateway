package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.SystemConfigFixtures;
import com.nori.ai.gateway.app.config.SystemTools;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.llm.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.ErrorCode;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.integration.tokenizer.HfTokenizerCounter;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ClassificationFixtureRegressionTest {
    @Test void needsCompletionCountCoversActualWireAndNullSubjectStillClarifies() {
        var now=Instant.parse("2026-11-06T00:00:00Z");
        var scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("EQP_READ"),"a".repeat(64),now.minusSeconds(1),now.plusSeconds(60),"job");
        var registry=new CapabilityRegistry(SystemTools.manifests(SystemConfigFixtures.systems()),Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
        try(var tokenizer=new HfTokenizerCounter()) {
            for(String question:List.of("상태 조회","여기서 뭘 물어볼 수 있어?","S-ONE 무엇을 물을 수 있나")) {
                var wire=SchemaContracts.JSON.createObjectNode();wire.putArray("needs").addObject().put("intent",question.equals("상태 조회")?"status":"help").putNull("subject");
                String content=wire.toString();int actual=tokenizer.countText(content);int underReported=actual-1;
                for(boolean accurate:List.of(false,true)) {
                    InferenceClient client=new InferenceClient(){public ChatOutcome chat(CallType type,List<ChatMessage> messages,int max){return new Completed(content,"stop",100,accurate?actual:underReported,Duration.ZERO,100);}public List<float[]> embed(List<String> inputs){throw new UnsupportedOperationException();}};
                    var service=new ClassificationService(client,TokenBudgetPolicy.current(),tokenizer::countText,messages->100,new ExpiryCheckpoint(),LlmCallObserver.NONE,attempt->{},"분류",request->new ClassificationService.Context(registry.view(scope),List.of(),Map.of(),2,List.of(),com.nori.ai.gateway.app.SystemConfigFixtures.intents()));
                    var result=service.classify(new ClassificationService.Request(scope,"conversation","job",new CancellationToken(),new JobRequestInput("message",1,question,Map.of())));
                    if(!accurate)assertThat(result).isEqualTo(new ClassificationService.Rejected(ErrorCode.CONTRACT_INVALID,1,List.of()));
                    else if(question.equals("상태 조회"))assertThat(result).isEqualTo(new ClassificationService.Rejected(ErrorCode.PLAN_INVALID,1,List.of("eqp")));
                    else assertThat(result).isInstanceOf(ClassificationService.Classified.class);
                }
            }
        }
    }
}

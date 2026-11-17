package com.nori.ai.gateway.integration.inference;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import java.time.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class InferenceCancellationTest {
    static final List<ChatMessage> MESSAGES=List.of(new ChatMessage(ChatMessage.Role.SYSTEM,"지시"),new ChatMessage(ChatMessage.Role.USER,"질문"));
    @Test void passesRemainingJobTimeAndSerializesGenerationUntilTheCancelledCallActuallyEnds() throws Exception {
        var config=new InferenceModelConfiguration();var endpoint=new InferenceEndpointConfig(InferenceFixtures.settings("http://127.0.0.1:1/v1"));
        var sdk=config.inferenceOpenAiClient(endpoint);var slots=new GenerationSemaphore(1);
        var entered=new CountDownLatch(1);var finish=new CountDownLatch(1);var interrupted=new CountDownLatch(1);var sends=new AtomicInteger();
        var remaining=new AtomicReference<Duration>();
        ChatTransport transport=new ChatTransport(){
            public Response send(CallType t,List<ChatMessage> m,int max){throw new AssertionError();}
            public Response send(CallType t,List<ChatMessage> m,int max,Duration timeout){
                remaining.set(timeout);sends.incrementAndGet();entered.countDown();
                try{finish.await();}catch(InterruptedException cancelled){interrupted.countDown();try{finish.await();}catch(InterruptedException again){throw new IllegalStateException(again);}}
                return new Response("답변","stop",50,5);
            }
        };
        var client=new SpringAiOpenAiInferenceClient(transport,config.openAiEmbeddingModel(sdk,endpoint),new PreCallBudgetAssert(m->50,TokenBudgetPolicy.current()),new TruncationDetector(),slots,endpoint);
        var token=new CancellationToken();token.deadline(Instant.EPOCH.plusSeconds(7),Clock.fixed(Instant.EPOCH,ZoneOffset.UTC));
        var executor=Executors.newVirtualThreadPerTaskExecutor();
        try{
            var first=executor.submit(()->client.chat(CallType.CLASSIFY,MESSAGES,2048,token));
            assertThat(entered.await(5,TimeUnit.SECONDS)).isTrue();assertThat(remaining.get()).isEqualTo(Duration.ofSeconds(7));
            token.signal(CancellationToken.Reason.CANCELLED);assertThat(interrupted.await(5,TimeUnit.SECONDS)).isTrue();
            assertThat(slots.availablePermits()).isZero();
            var secondToken=new CancellationToken();secondToken.signal(CancellationToken.Reason.CANCELLED);
            assertThat(client.chat(CallType.CLASSIFY,MESSAGES,2048,secondToken)).isInstanceOf(InferenceClient.CallFailed.class);
            assertThat(sends).hasValue(1);assertThat(first.isDone()).isFalse();
            finish.countDown();assertThat(first.get(5,TimeUnit.SECONDS)).isInstanceOf(InferenceClient.CallFailed.class);
            assertThat(slots.availablePermits()).isOne();
        }finally{finish.countDown();executor.close();sdk.close();}
    }
}

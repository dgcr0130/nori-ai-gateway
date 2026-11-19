package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.app.job.*;
import com.nori.ai.gateway.app.observability.AuditRecorder;
import com.nori.ai.gateway.core.authz.*;
import com.nori.ai.gateway.core.blocks.*;
import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.cache.ToolResultCache;
import com.nori.ai.gateway.core.dispatch.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.llm.*;
import com.nori.ai.gateway.core.plan.PlanValidator;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.*;
import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.nori.ai.gateway.core.scope.*;
import com.nori.ai.gateway.core.seam.*;
import com.nori.ai.gateway.core.time.TimeWindowResolver;
import com.nori.ai.gateway.core.workflow.*;
import com.nori.ai.gateway.integration.InferenceAdapterModuleConfiguration;
import com.nori.ai.gateway.integration.inference.InferenceReadinessService;
import com.nori.ai.gateway.integration.testing.InferenceFixtures;
import com.nori.ai.gateway.integration.tokenizer.*;
import com.nori.ai.gateway.persistence.repository.*;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HybridSearchHttpTest {
    @org.junit.jupiter.api.io.TempDir java.nio.file.Path temp;
    @TestFactory Stream<DynamicTest> actualClassificationSearchSelectionAndComposerUseOnlyLocalHttp() {
        return Stream.of("retry","insufficient","embedding-error","dimension-error","question-dimension-error","count-error","verified","same-query","live","general","seven","large","all-removed","partial","http-fail","non-json","truncated-all-removed","source-http-fail","source-non-json","stale-log")
            .map(mode->DynamicTest.dynamicTest(mode,()->run(mode)));
    }
    private void run(String mode)throws Exception {
        var sent=new CopyOnWriteArrayList<String>();var audits=new ArrayList<LlmCallAudit.Attempt>();
        try(var counter=new HfTokenizerCounter(InferenceFixtures.settings("http://127.0.0.1:1/v1"))) {
            var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
            server.createContext("/",exchange->{
                String path=exchange.getRequestURI().getPath(),raw=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
                if(path.endsWith("/models")){byte[] bytes="{\"data\":[]}".getBytes(StandardCharsets.UTF_8);exchange.sendResponseHeaders(200,bytes.length);exchange.getResponseBody().write(bytes);exchange.close();return;}
                sent.add(path+" "+raw);int status=200;String response;
                if(path.equals("/internal/ai/v1/query")) {
                    var input=SchemaContracts.JSON.readTree(raw);var body=SchemaContracts.JSON.createObjectNode().put("version","1").put("systemId","TC").put("jobId",input.path("jobId").asString()).put("toolId","asset.read").put("status","SUCCESS").put("observedAt",Instant.now().toString());
                    var items=body.putObject("data").putArray("items");for(int i=1;i<=(mode.equals("seven")?7:mode.equals("truncated-all-removed")?2:1);i++)items.addObject().put("ref","item-"+i).put("text",(mode.equals("large")||mode.equals("truncated-all-removed")&&i==1)?"뺡".repeat(3990)+" 설비 상태 T3":"설비 상태 T3 항목 "+i).putObject("values").put("position",i);
                    body.putArray("missingFields");body.putArray("warnings");body.putObject("retentionMeta").putArray("missingRanges");((tools.jackson.databind.node.ObjectNode)body.get("retentionMeta")).putArray("unverifiedRanges");body.putNull("errorCode");response=body.toString();
                } else if(path.endsWith("/embeddings")) {
                    if(mode.equals("embedding-error")){status=503;response="{\"error\":{\"message\":\"private-error\"}}";}
                    else {
                        var requested=SchemaContracts.JSON.readTree(raw).path("input");var body=SchemaContracts.JSON.createObjectNode().put("object","list").put("model","bge-m3");var data=body.putArray("data");
                        int count=mode.equals("count-error")?requested.size()-1:requested.size();
                        for(int i=0;i<count;i++) {
                            String input=requested.get(i).asString();
                            String vector=mode.equals("dimension-error")||mode.equals("question-dimension-error")&&i==1?"[1]":input.equals("설비 T3")?"[1,0]":input.equals("상태 T3")?"[-1,0]":"[0,1]";
                            data.addObject().put("object","embedding").put("index",i).set("embedding",SchemaContracts.JSON.readTree(vector));
                        }
                        body.putObject("usage").put("prompt_tokens",1).put("total_tokens",1);response=body.toString();
                    }
                } else {
                    var request=SchemaContracts.JSON.readTree(raw);var messages=new ArrayList<ChatMessage>();for(var message:request.path("messages"))messages.add(new ChatMessage(ChatMessage.Role.valueOf(message.path("role").asString().toUpperCase(Locale.ROOT)),message.path("content").asString()));
                    boolean classify=sent.stream().filter(s->s.startsWith("/v1/chat/completions")).count()==1;
                    String task=Set.of("live","seven","large","truncated-all-removed","source-http-fail","source-non-json","stale-log").contains(mode)?"LIVE_QUERY":mode.equals("general")?"GENERAL_QUERY":"KNOWLEDGE_QUERY";
                    String query=mode.equals("same-query")?"설비 상태 T3":"설비 T3";
                    String text=classify?"{\"taskType\":\""+task+"\",\"toolCalls\":[],\"targets\":[],\"timeWindow\":null,\"searchQuery\":\""+query+"\",\"retryQuery\":\"상태 T3\"}":"{\"sentences\":[{\"text\":\"설비 상태 T3\",\"citations\":[1,2]}]}";
                    if(!classify&&!mode.equals("retry")&&!mode.equals("general"))text="{\"sentences\":[{\"text\":\"설비 상태 T3\",\"citations\":[1]}]}";
                    if(classify&&Set.of("seven","large","truncated-all-removed","source-http-fail","source-non-json","stale-log").contains(mode)) {
                        var wire=(tools.jackson.databind.node.ObjectNode)SchemaContracts.JSON.readTree(text);wire.putArray("toolCalls").addObject().put("toolId","asset.read").putObject("arguments");text=wire.toString();
                    }
                    if(!classify&&Set.of("all-removed","truncated-all-removed").contains(mode))text="{\"sentences\":[{\"text\":\"없는이름 99\",\"citations\":[1]}]}";
                    if(!classify&&mode.equals("partial"))text="{\"sentences\":[{\"text\":\"설비 상태 T3\",\"citations\":[1]},{\"text\":\"99\",\"citations\":[1]}]}";
                    int prompt=counter.countText(InferenceFixtures.renderer().render(messages)),completion=counter.countText(text);
                    var body=SchemaContracts.JSON.createObjectNode().put("id","local").put("object","chat.completion").put("created",1).put("model","qwen3:4b");
                    body.putArray("choices").addObject().put("index",0).put("finish_reason","stop").set("message",SchemaContracts.JSON.createObjectNode().put("role","assistant").put("content",text));
                    body.putObject("usage").put("prompt_tokens",prompt).put("completion_tokens",completion).put("total_tokens",prompt+completion);response=body.toString();
                    if(!classify&&Set.of("http-fail","source-http-fail").contains(mode)){status=503;response="{\"error\":\"private-error\"}";}
                    if(!classify&&Set.of("non-json","source-non-json").contains(mode))response="invalid";
                }
                byte[] bytes=response.getBytes(StandardCharsets.UTF_8);exchange.getResponseHeaders().add("Content-Type","application/json");exchange.sendResponseHeaders(status,bytes.length);exchange.getResponseBody().write(bytes);exchange.close();
            });server.start();
            try(var context=new AnnotationConfigApplicationContext();var index=new MemoryDocumentKeywordIndex()) {
                var values=new HashMap<>(InferenceFixtures.properties());values.put("ai.gateway.inference.mode","enabled");values.put("ai.gateway.llm.baseUrl","http://127.0.0.1:"+server.getAddress().getPort()+"/v1");
                context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("local",values));
                var policy=TokenBudgetPolicy.current();context.registerBean(TokenBudgetPolicy.class,()->policy);
                context.registerBean(PreCallBudgetAssert.class,()->new PreCallBudgetAssert(context.getBean(PromptTokenCounting.class),policy));context.registerBean(TruncationDetector.class,TruncationDetector::new);
                context.registerBean(GenerationSemaphore.class,()->new GenerationSemaphore(1));context.registerBean(Clock.class,Clock::systemUTC);
                context.registerBean(io.micrometer.core.instrument.MeterRegistry.class,io.micrometer.core.instrument.simple.SimpleMeterRegistry::new);
                context.register(InferenceAdapterModuleConfiguration.class);context.refresh();context.getBean(InferenceReadinessService.class).stop();
                var now=Instant.now();var scope=new VerifiedSystemScope(OwnerSystem.TC,"service",new ActorRef("actor"),"actor",Set.of("READ"),"a".repeat(64),now.minusSeconds(60),now.plusSeconds(3600),"job");
                var first=doc("a.md",Set.of("verified","same-query","all-removed","partial","http-fail","non-json").contains(mode)?"설비 상태 T3":"설비 T3",1);var second=doc("b.md",mode.equals("insufficient")?"통신 T3":"상태 T3",-1);
                var exactId=new UUID(2,1);var answers=mode.equals("verified")?List.of(
                    new DocumentCorpus.Answer(exactId,"설비 상태 T3",List.of(first.parents().getFirst().location()),Set.of("READ"),new float[]{0,1}),
                    new DocumentCorpus.Answer(new UUID(2,2),"다른 질문",List.of(first.parents().getFirst().location()),Set.of("READ"),new float[]{1,0})):List.<DocumentCorpus.Answer>of();
                var bundle=new DocumentCorpus.Bundle(UUID.randomUUID(),"bge-m3",2,"b".repeat(64),List.of(first,second),answers);
                try(var prepared=index.prepare(scope,bundle)){index.install(scope,prepared);}
                var store=mock(DocumentIndexRepository.class);when(store.searchState(any())).thenReturn(Optional.of(bundle));
                var config=new CoreBeansConfiguration();var client=context.getBean(InferenceClient.class);var expiry=new ExpiryCheckpoint();var business=ConfiguredTool.create(OwnerSystem.TC,"asset.read","설비 상태 조회",mode.equals("stale-log")?ToolKind.LOG:ToolKind.BUSINESS,SchemaContracts.parse("{\"type\":\"object\",\"additionalProperties\":false}"),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("READ"),10000);
                var versions=new ContractVersionPolicy();var registry=new CapabilityRegistry(Set.of("seven","large","truncated-all-removed","source-http-fail","source-non-json","stale-log").contains(mode)?List.of(business):List.of(),Set.of(ImplementationPhase.PHASE_1),versions);
                var inputs=mock(ClassificationInputs.class);when(inputs.load(any())).thenReturn(new ClassificationService.Context(registry.view(scope),List.of(),Map.of(),2));
                var classification=config.classificationService(client,policy,context.getBean(HfTokenizerCounter.class),context.getBean(PromptTokenCounting.class),expiry,Optional.empty(),audits::add,inputs);
                var request=new JobRequestInput("message",1,"설비 상태 T3",Map.of());var cancellation=new CancellationToken();
                var result=classification.classify(new ClassificationService.Request(scope,"conversation","job",cancellation,request));assertThat(result).isInstanceOf(ClassificationService.Classified.class);
                var classified=((ClassificationService.Classified)result).classification();var job=new JobContext("job","conversation",scope,"d".repeat(22),cancellation,InferenceMode.ENABLED,classified.jobClassification(),request);
                var systems=new GatewaySystemsProperties(Map.of("TC",new GatewaySystemsProperties.SystemConfig("fixture","digest","jwks",null,null,List.of(),List.of(),mode.equals("stale-log")?List.of(new GatewaySystemsProperties.App("fixture","local",List.of("READ"))):List.of(),List.of(),List.of(),"REVIEW",Map.of())));
                var groundingInputs=new GroundingInputs(systems,registry,new ContentSanitizer());var settings=new SearchSettings(1,1,1,60,6,3,.9,.35,4,1);
                var delegate=config.documentSearch(store,index,client);var observedRounds=new ArrayList<DocumentSearch.Round>();
                var observedSearch=new DocumentSearch(){
                    public Round search(VerifiedSystemScope current,String query,List<String> targets,SearchSettings policy){return search(current,query,query,targets,policy);}
                    @Override public Round search(VerifiedSystemScope current,String query,String question,List<String> targets,SearchSettings policy){var found=delegate.search(current,query,question,targets,policy);observedRounds.add(found);return found;}
                };
                var grounding=config.workflowGrounding(observedSearch,index,settings,new EvidenceSelection(context.getBean(HfTokenizerCounter.class)::countText,policy.evidenceTokens()),groundingInputs,Clock.systemUTC(),expiry);
                var validator=new PlanValidator(registry,new PlanInjectionSeam.NoOp(),Clock.systemUTC(),12,12);var permissions=new SourcePermissionMap(Map.of(business.sourceAllowlistRef(),new SourcePermissionMap.Entry(Set.of("READ"),false)));var authorization=new SourceAuthorizationStep(permissions,(source,current)->false);
                var credential=temp.resolve("gateway-token");java.nio.file.Files.writeString(credential,"local-fixture-token");
                var systemQuery=new com.nori.ai.gateway.integration.system.HttpSystemQueryAdapter(new com.nori.ai.gateway.integration.system.SystemQuerySettings(Map.of(OwnerSystem.TC,new com.nori.ai.gateway.integration.system.SystemQuerySettings.Endpoint(URI.create("http://127.0.0.1:"+server.getAddress().getPort()),credential))),Clock.systemUTC(),new ContentSanitizer());
                ToolExecutor businessExecutor=mode.equals("stale-log")?new ToolExecutor() {
                    @Override public ToolKind toolKind(){return ToolKind.LOG;}
                    @Override public ToolResultEnvelope execute(com.nori.ai.gateway.core.plan.PlanStep step,CapabilityManifest manifest,VerifiedSystemScope current,ExecutionContext execution){return staleLog(manifest,current,now);}
                }:new BusinessToolExecutor(systemQuery,Clock.systemUTC());
                var executor=new com.nori.ai.gateway.core.workflow.WorkflowExecutor(registry,validator,new ToolDispatcher(registry,validator,new ToolResultContractValidator(versions),new DispatchInjectionSeam.NoOp(),List.of(businessExecutor),Clock.systemUTC()),authorization,expiry,Clock.systemUTC(),mock(ToolResultCache.class),new BlockBuilder(new UiSchemaValidator()),mock(com.nori.ai.gateway.core.workflow.WorkflowExecutor.ExecutionRecorder.class),new LogSummarizer(),new GeneralQueryHelpBuilder());
                var composer=new ComposerService(client,new PromptReducer(context.getBean(PromptTokenCounting.class),policy),policy,context.getBean(HfTokenizerCounter.class)::countText,expiry,LlmCallObserver.NONE,audits::add,"제공된 근거만 사용합니다.",index);
                var adapter=new WorkflowExecutionAdapter(registry,new WorkflowPlanBuilder(registry),executor,new WorkflowResourceResolverFactory(permissions,registry,expiry),new TimeWindowResolver(),mock(MessageRepository.class),expiry,new ContentSanitizer(),new TerminalOutcomeAssembler(),new ConversationSummaryBuilder(),new com.nori.ai.gateway.core.evidence.EvidenceAssembler(),mock(AuditRecorder.class),Optional.of(composer),new LogSummarizer(),Optional.empty(),Optional.of(grounding));
                var terminal=adapter.execute(job);
                long embeddings=sent.stream().filter(s->s.startsWith("/v1/embeddings")).count(),chats=sent.stream().filter(s->s.startsWith("/v1/chat/completions")).count();
                assertThat(audits.stream().filter(a->a.callType()==CallType.CLASSIFY)).hasSize(1);assertThat(embeddings).isLessThanOrEqualTo(2);
                var embeddingRequests=sent.stream().filter(s->s.startsWith("/v1/embeddings ")).map(s->SchemaContracts.JSON.readTree(s.substring("/v1/embeddings ".length())).path("input")).toList();
                for(int i=0;i<embeddingRequests.size();i++) {
                    var expected=mode.equals("same-query")?List.of("설비 상태 T3"):List.of(i==0?"설비 T3":"상태 T3","설비 상태 T3");
                    assertThat(embeddingRequests.get(i)).isEqualTo(SchemaContracts.JSON.valueToTree(expected));
                }
                switch(mode) {
                    case "retry"->{assertThat(embeddings).isEqualTo(2);assertThat(chats).isEqualTo(2);assertThat(SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("answer")).path("text").asString()).isEqualTo("설비 상태 T3[1][2]");assertThat(terminal.commitData().orElseThrow().sourceDeps().knowledgeDocuments()).hasSize(2);}
                    case "seven"->{assertThat(embeddings).isZero();assertThat(chats).as("status=%s payload=%s paths=%s",terminal.resultStatus(),terminal.resultPayload(),sent.stream().map(v->v.split(" ")[0]).toList()).isEqualTo(2);var saved=terminal.commitData().orElseThrow().snapshot().orElseThrow();assertThat(saved.evidence()).hasSize(7);for(int i=0;i<7;i++){assertThat(saved.evidence().get(i).path("number").asInt()).isEqualTo(i+1);assertThat(saved.evidence().get(i).path("bodyMasked").asString()).isEqualTo("설비 상태 T3 항목 "+(i+1));}assertThat(terminal.resultPayload()).doesNotContainKey("missingFields");}
                    case "large"->{assertThat(embeddings).isZero();assertThat(chats).isEqualTo(1);assertThat(terminal.resultStatus()).isEqualTo(ResultStatus.PARTIAL);assertThat(terminal.jobStatus()).isEqualTo(JobStatus.PARTIAL);assertThat(terminal.eventType()).isEqualTo("job.partial");assertThat(terminal.resultPayload().get("missingFields")).isEqualTo(List.of("truncatedItems"));assertThat(SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("answer")).path("kind").asString()).isEqualTo("NO_EVIDENCE");}
                    case "all-removed","truncated-all-removed"->{
                        assertThat(chats).isEqualTo(2);boolean truncated=mode.equals("truncated-all-removed");
                        assertThat(terminal.resultStatus()).isEqualTo(truncated?ResultStatus.PARTIAL:ResultStatus.EMPTY);assertThat(terminal.jobStatus()).isEqualTo(truncated?JobStatus.PARTIAL:JobStatus.SUCCEEDED);assertThat(terminal.eventType()).isEqualTo(truncated?"job.partial":"job.completed");assertThat(terminal.errorCode()).isNull();
                        var answer=SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("answer"));assertThat(answer.path("kind").asString()).isEqualTo("NO_EVIDENCE");assertThat(answer.path("text").asString()).isEqualTo("근거 없음");assertThat(answer.path("sentences")).isEmpty();assertThat(answer.path("citations")).isEmpty();
                        if(truncated){assertThat(embeddings).isZero();assertThat(terminal.resultPayload().get("missingFields")).isEqualTo(List.of("truncatedItems"));var saved=terminal.commitData().orElseThrow().snapshot().orElseThrow();assertThat(saved.evidence()).singleElement().satisfies(unit->{assertThat(unit.path("number").asInt()).isEqualTo(1);assertThat(unit.path("location").path("ref").asString()).isEqualTo("item-2");assertThat(unit.path("bodyMasked").asString()).isEqualTo("설비 상태 T3 항목 2");});}else assertThat(terminal.resultPayload()).doesNotContainKey("missingFields");
                    }
                    case "partial"->{assertThat(chats).isEqualTo(2);assertThat(terminal.resultStatus()).isEqualTo(ResultStatus.SUCCESS);assertThat(terminal.jobStatus()).isEqualTo(JobStatus.SUCCEEDED);assertThat(terminal.eventType()).isEqualTo("job.completed");assertThat(terminal.errorCode()).isNull();var answer=SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("answer"));assertThat(answer.path("kind").asString()).isEqualTo("PARTIAL");assertThat(answer.path("text").asString()).isEqualTo("설비 상태 T3[1]");assertThat(answer.path("sentences")).hasSize(1);assertThat(terminal.resultPayload()).doesNotContainKey("missingFields");}
                    case "stale-log"->{assertThat(embeddings).isZero();assertThat(chats).isEqualTo(2);assertThat(terminal.resultStatus()).isEqualTo(ResultStatus.STALE);assertThat(terminal.jobStatus()).isEqualTo(JobStatus.SUCCEEDED);assertThat(terminal.eventType()).isEqualTo("job.completed");assertThat(terminal.errorCode()).isNull();var answer=SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("answer"));assertThat(answer.path("kind").asString()).isEqualTo("PARTIAL");assertThat(answer.path("text").asString()).isEqualTo("설비 상태 T3[1]\n최신 자료가 아닐 수 있습니다.");assertThat(answer.path("sentences")).hasSize(1);assertThat(answer.path("citations").get(0).path("sourceType").asString()).isEqualTo("LOG");assertThat(terminal.resultPayload()).doesNotContainKey("missingFields");}
                    case "http-fail","non-json","source-http-fail","source-non-json"->{
                        assertThat(chats).isEqualTo(2);boolean data=mode.startsWith("source-");assertThat(terminal.resultStatus()).isEqualTo(data?ResultStatus.PARTIAL:ResultStatus.FAILED);assertThat(terminal.jobStatus()).isEqualTo(data?JobStatus.PARTIAL:JobStatus.FAILED);assertThat(terminal.eventType()).isEqualTo(data?"job.partial":"job.failed");assertThat(terminal.errorCode()).isEqualTo(ErrorCode.LLM_UNAVAILABLE);assertThat(terminal.resultPayload()).containsEntry("missingFields",List.of("answer")).doesNotContainKey("answer");
                        var commit=terminal.commitData().orElseThrow();assertThat(commit.snapshot()).isEmpty();assertThat(commit.assistantMessage().orElseThrow().contentMasked()).isEmpty();
                        var blocks=SchemaContracts.JSON.valueToTree(terminal.resultPayload().get("blocks"));if(data){assertThat(embeddings).isZero();assertThat(blocks).isNotEmpty();assertThat(commit.evidence()).isNotEmpty();}else{assertThat(blocks).isEmpty();assertThat(commit.evidence()).isEmpty();}
                        assertThat(audits.stream().filter(a->a.callType()==CallType.COMPOSER)).hasSize(1);
                    }
                    case "general"->{assertThat(embeddings).isZero();assertThat(chats).isEqualTo(2);}
                    case "verified"->{
                        assertThat(embeddings).isEqualTo(1);assertThat(chats).isEqualTo(2);
                        // 대역 단위 벡터의 내적에서 질문·완전 일치 답변은 1, 질문·검색 질의 방향 답변은 0입니다.
                        assertThat(observedRounds).singleElement().satisfies(round->{assertThat(round.verified()).extracting(DocumentSearch.Verified::id).containsExactly(exactId);assertThat(round.semantic()).singleElement().satisfies(hit->assertThat(hit.child().location().objectKey()).isEqualTo("a.md"));});
                    }
                    case "same-query"->{assertThat(embeddings).isEqualTo(1);assertThat(chats).isEqualTo(2);}
                    default->{assertThat(chats).isEqualTo(1);assertThat(((Map<?,?>)terminal.resultPayload().get("answer")).get("kind")).isEqualTo("NO_EVIDENCE");if(mode.equals("live"))assertThat(embeddings).isZero();else if(mode.equals("insufficient"))assertThat(embeddings).isEqualTo(2);else {assertThat(embeddings).isEqualTo(1);assertThat(terminal.resultStatus()).isEqualTo(ResultStatus.SOURCE_UNAVAILABLE);assertThat(terminal.jobStatus()).isEqualTo(JobStatus.FAILED);assertThat(terminal.errorCode()).isNull();}}
                }
            }finally{server.stop(0);}
        }
    }
    private ToolResultEnvelope staleLog(CapabilityManifest manifest,VerifiedSystemScope scope,Instant at) {
        // 조회 어댑터 이후의 고정 LOG 관측값을 주입하고 선택·Nori 대조·실제 HTTP 작성·종결은 운영 경로를 거칩니다.
        var source=new OwnedReference(scope.boundSystem(),manifest.sourceAllowlistRef());var ref=new OwnedReference(scope.boundSystem(),"data://TC/tool/log-1");
        var content=SchemaContracts.JSON.createObjectNode();content.putArray("items").addObject().put("app","fixture").put("namespace","local").put("occurredAt",at.toString()).put("text","설비 상태 T3");
        var evidence=new Evidence("log-1",scope.boundSystem(),"LOG",source,at,at,null,null,"OBSERVED",scope.actorRef());
        return new ToolResultEnvelope("log-1",manifest.capabilityId(),manifest.contractVersion(),manifest.schemaDigest(),scope.boundSystem(),ResultStatus.STALE,ref,List.of(),at,null,List.of(),List.of(),null,"log-trace",new ToolResultMetadata(source,"LIVE",false,List.of()),null,List.of(new ResultBlock("table",ref,content)),List.of(),List.of(evidence),List.of(),List.of());
    }
    private DocumentCorpus.Document doc(String key,String text,int x) {
        var location=new DocumentLocation("bucket",key,"a".repeat(64),"설정",3,5);var child=new DocumentCorpus.Child(UUID.randomUUID(),0,location,"운영 > 설정",text,List.of("t3"),new float[]{x,0});
        var parent=new DocumentCorpus.Parent(UUID.randomUUID(),location,"운영 > 설정",text,"b".repeat(64),List.of(child));return new DocumentCorpus.Document(UUID.randomUUID(),"bucket",key,"a".repeat(64),"운영",Set.of("READ"),List.of(),List.of(parent));
    }
}

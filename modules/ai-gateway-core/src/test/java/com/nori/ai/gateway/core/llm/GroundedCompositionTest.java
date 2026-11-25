package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.*;
import com.nori.ai.gateway.core.expiry.ExpiryCheckpoint;
import com.nori.ai.gateway.core.job.*;
import com.nori.ai.gateway.core.port.InferenceClient;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.GroundedAnswer;
import com.nori.ai.gateway.core.workflow.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;
import static org.assertj.core.api.Assertions.*;

class GroundedCompositionTest {
    final List<StructuredOutput> constraints=new ArrayList<>();
    final List<List<ChatMessage>> sent=new ArrayList<>();
    final List<LlmDiagnostic> diagnostics=new ArrayList<>();
    final List<LlmCallAudit.Attempt> audits=new ArrayList<>();
    static final String TABLE="[설비 등록 정보]\n조회 조건 > 설비(eqp): CLN_001\n설비(eqpId): CLN_001\n통신 모드(commMode): PASSIVE\n포트(eqpPort): 6100\n생성 일시(createdAt): 2026-04-28T05:02:28.248019Z";
    JobContext job(String question){return new JobContext("job-1","conversation",SCOPE,"delegation",new CancellationToken(),InferenceMode.ENABLED,Map.of("taskType","LIVE_QUERY"),new JobRequestInput("m",1,question,Map.of()));}
    ComposerService service(String response,AnswerTokenCounting tokens) {
        var client=new InferenceClient(){
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max){throw new AssertionError("구조화 호출이 필요합니다");}
            public ChatOutcome chat(CallType t,List<ChatMessage> m,int max,CancellationToken c,StructuredOutput schema){sent.add(List.copyOf(m));constraints.add(schema);return reply(response);}
            public List<float[]> embed(List<String> text){throw new AssertionError();}
        };
        var policy=TokenBudgetPolicy.current();
        var observer=new LlmCallObserver(){public Scope open(String j,CallType t,List<ChatMessage> m){return ()->{};}public void diagnostic(String j,CallType t,LlmDiagnostic reason){diagnostics.add(reason);}};
        // 도구 이름은 설정 입력입니다. TC 식별자를 제품 코드에 심지 않는 것을 확인합니다.
        return new ComposerService(client,new PromptReducer(m->100,policy),policy,tokens,new ExpiryCheckpoint(),observer,audits::add,"일반 도움말",text->List.of(),
            Map.of("asset.empty","사용자 목록 조회","asset.registration","등록 정보 조회"),Set.of("asset.registration"));
    }
    static EvidenceUnit unit(String body,String tool,String status) {
        return new EvidenceUnit("SYSTEM",null,SchemaContracts.JSON.createObjectNode().put("toolId",tool).put("ref","CLN_001").put("observedAt","2026-11-24T01:37:32.987Z"),"",body,List.of("READ"),status,"2026-11-24T01:37:32.987Z",List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());
    }
    static ComposerService.Input input(String question,List<EvidenceUnit> units,Optional<ClassificationService.HistoryPair> history,List<String> targets) {
        return new ComposerService.Input(List.of(),List.of(),"",List.of(),question,List.of(),Optional.of(EvidenceSelection.json(units)),history,targets);
    }
    ComposerService.Result compose(String response,List<EvidenceUnit> units){return service(response,CODEPOINTS).compose(job("CLN_001 정보를 알려 주세요."),input("CLN_001 정보를 알려 주세요.",units,Optional.empty(),List.of("CLN_001")));}
    static String rows(Map<String,Object>... values){return FactCheckTest.rows(values);}
    static Map<String,Object> row(String text,String... ids){return FactCheckTest.row(text,ids);}

    @Test void conversationalSentencesUseSpacesAndSortedUniqueCitationMarkers() {
        var units=List.of(unit("CLN_001 OFFLINE","asset.snapshot","SUCCESS"),unit("CLN_001 DOWN","asset.snapshot","SUCCESS"));
        var result=compose(rows(row("CLN_001의 상태는 OFFLINE이며 DOWN입니다.","e2","e1","e2"),row("연결 상태를 확인했습니다.","e1")),units);
        var answer=result.answer().orElseThrow();assertThat(result.calls()).isOne();assertThat(result.outcome()).isEqualTo(ComposerOutcome.COMPOSED);
        assertThat(answer.kind()).isEqualTo("ANSWER");
        assertThat(answer.text()).isEqualTo("CLN_001의 상태는 OFFLINE이며 DOWN입니다.[1][2] 연결 상태를 확인했습니다.[1]");
        assertThat(answer.sentences()).extracting(GroundedAnswer.Sentence::citations).containsExactly(List.of(1,2),List.of(1));
        assertThat(answer.citations()).extracting(GroundedAnswer.Citation::number).containsExactly(1,2);
        assertThat(answer.citations()).extracting(GroundedAnswer.Citation::evidenceId).containsExactly("e1","e2");
        assertThat(answer.citations().getFirst().location()).isEqualTo(units.getFirst().location());
    }
    @Test void citationTitlesComeFromEachSourcesOwnHeadingHeaderOrLocation() {
        var registration=unit(TABLE,"asset.registration","SUCCESS");
        var result=compose(rows(row("CLN_001의 포트는 6100입니다.","e1"),row("CLN_001 상태는 OFFLINE입니다.","e2")),List.of(registration,unit("CLN_001 OFFLINE","asset.snapshot","SUCCESS")));
        // 업무 시스템 본문은 첫 줄 [제목]을 이름으로 쓰고, 제목 줄이 없으면 첫 줄을 그대로 씁니다.
        assertThat(result.answer().orElseThrow().citations()).extracting(GroundedAnswer.Citation::title).containsExactly("설비 등록 정보","CLN_001 OFFLINE");
        var empty=SchemaContracts.JSON.createObjectNode();
        var document=new EvidenceUnit("DOCUMENT","parent",empty,"운영 안내 > 설비 등록","본문",List.of("READ"),"SUCCESS",null,List.of(),empty,List.of(),List.of());
        var git=new EvidenceUnit("GIT",null,SchemaContracts.JSON.createObjectNode().put("sourceRepo","nori-tc-gateway").put("path","gateway/A.java").put("ref","main"),"","{}",List.of("AI_CODE_READ"),"SUCCESS",null,List.of(),empty,List.of(),List.of());
        var log=new EvidenceUnit("LOG",null,SchemaContracts.JSON.createObjectNode().put("app","tc-gateway").put("namespace","tc").put("occurredAt","2026-11-24T01:37:32Z"),"","오류",List.of("AI_LOG_READ"),"SUCCESS",null,List.of(),empty,List.of(),List.of());
        var trace=new EvidenceUnit("TRACE",null,SchemaContracts.JSON.createObjectNode().put("service","tc-business").put("traceId","t"),"","{}",List.of("AI_TRACE_READ"),"SUCCESS",null,List.of(),empty,List.of(),List.of());
        assertThat(List.of(document,git,log,trace)).extracting(GroundedAnswerAssembly::title).containsExactly("운영 안내 > 설비 등록","gateway/A.java","tc-gateway","tc-business");
        assertThat(GroundedAnswerAssembly.title(unit("[조회 결과 요약]\n도구(toolId): asset.empty","asset.empty","SUCCESS"))).isEqualTo("조회 결과 요약");
    }
    @Test void actualWireUsesWholeFactsKstWithoutValuesAndOptionalMatchedHistory() {
        var units=List.of(unit(TABLE,"asset.registration","SUCCESS").withValues(SchemaContracts.parse("{\"privateValueOnly\":987654}")));
        var history=Optional.of(new ClassificationService.HistoryPair("앞선 질문","앞선 답변"));
        service(rows(),CODEPOINTS).compose(job("마스킹된 질문"),input("마스킹된 질문",units,history,List.of("CLN_001")));
        var data=SchemaContracts.JSON.readTree(sent.getFirst().getLast().content());
        assertThat(data.propertyNames()).containsExactlyInAnyOrder("question","facts","history");assertThat(data.path("question").asString()).isEqualTo("마스킹된 질문");
        assertThat(data.path("history")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of(history.orElseThrow())));
        var fact=data.path("facts").get(0);assertThat(fact.propertyNames()).containsExactlyInAnyOrder("id","sourceType","status","observedAt","missingFields","text");
        assertThat(fact.path("id").asString()).isEqualTo("e1");assertThat(fact.path("sourceType").asString()).isEqualTo("SYSTEM");assertThat(fact.path("status").asString()).isEqualTo("SUCCESS");
        assertThat(fact.path("observedAt").asString()).isEqualTo("2026-11-24 10:37:32 (KST)");assertThat(fact.path("missingFields")).isEmpty();
        assertThat(fact.path("text").asString()).isEqualTo(TABLE.replace("2026-04-28T05:02:28.248019Z","2026-04-28 14:02:28 (KST)"));
        assertThat(data.toString()).doesNotContain("privateValueOnly","987654","[C1@L");
        sent.clear();compose(rows(),units);assertThat(SchemaContracts.JSON.readTree(sent.getFirst().getLast().content()).has("history")).isFalse();
    }
    @Test void documentFactsIncludeTheirHeaderAndMissingFields() {
        var doc=new EvidenceUnit("DOCUMENT","parent",SchemaContracts.JSON.createObjectNode(),"운영 안내","CLN_001 문서",List.of("READ"),"PARTIAL",null,List.of("section"),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());
        compose(rows(),List.of(doc));var fact=SchemaContracts.JSON.readTree(sent.getFirst().getLast().content()).path("facts").get(0);
        assertThat(fact.path("header").asString()).isEqualTo("운영 안내");assertThat(fact.path("missingFields")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("section")));
    }
    @Test void codeFactsCarryRepositoryAndFilePathSoSnippetOwnersAreKnown() {
        var code=FactCheckTest.git("gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java"," * <p>- EquipmentContextProfileProvider: 설비 프로파일 조회</p>\n");
        compose(rows(),List.of(code));var fact=SchemaContracts.JSON.readTree(sent.getFirst().getLast().content()).path("facts").get(0);
        assertThat(fact.propertyNames()).containsExactlyInAnyOrder("id","sourceType","status","observedAt","missingFields","sourceRepo","path","text");
        assertThat(fact.path("sourceRepo").asString()).isEqualTo("nori-tc-gateway");
        assertThat(fact.path("path").asString()).isEqualTo("gateway/adapter/tc-gateway-db-adapter/src/main/java/com/nori/tc/gateway/adapters/db/GatewayEquipmentService.java");
        assertThat(fact.path("text").asString()).isEqualTo(code.body());
    }
    @Test void structuredOutputBoundsAndFactIdsEnumAreBoundToThisRequest() {
        compose(rows(),List.of(unit("CLN_001 OFFLINE","asset.snapshot","SUCCESS"),unit("CLN_001 DOWN","asset.snapshot","SUCCESS")));
        var output=constraints.getFirst();var array=SchemaContracts.JSON.readTree(output.schema()).path("properties").path("sentences");var item=array.path("items");
        assertThat(array.path("maxItems").asInt()).isEqualTo(20);assertThat(item.path("properties").path("text").path("minLength").asInt()).isOne();
        assertThat(item.path("properties").path("text").path("maxLength").asInt()).isEqualTo(1000);
        var ids=item.path("properties").path("factIds");assertThat(ids.path("minItems").asInt()).isOne();assertThat(ids.path("maxItems").asInt()).isEqualTo(6);
        assertThat(ids.path("items").path("enum")).isEqualTo(SchemaContracts.JSON.valueToTree(List.of("e1","e2")));
        assertThat(output.accepts(rows())).isTrue();assertThat(output.accepts(rows(row("확인했습니다.","e1")))).isTrue();
        for(String invalid:List.of(rows(row("확인했습니다.","labels")),rows(row("확인했습니다.")),rows(row("줄\n바꿈","e1")),rows(row("가".repeat(1001),"e1")),rows(Collections.nCopies(21,row("확인했습니다.","e1")).toArray(Map[]::new))))assertThat(output.accepts(invalid)).as(invalid).isFalse();
    }
    static EvidenceUnit summary(Map<String,Object> arguments,boolean incomplete,String status) {
        var missing=incomplete?List.of("completeness"):List.<String>of();
        var values=SchemaContracts.JSON.valueToTree(Map.of("toolId","asset.empty","arguments",arguments,"status",status,"count",0,"missingFields",missing));
        return new EvidenceUnit("SYSTEM",null,SchemaContracts.JSON.createObjectNode().put("toolId","asset.empty").put("ref","asset.empty:result-summary"),"","[조회 결과 요약]\nCLN_001 count: 0",List.of("READ"),status,"2026-11-24T05:33:12Z",missing,SchemaContracts.JSON.createObjectNode(),List.of(),List.of(),values);
    }
    @Test void onlyZeroSummaryUsesNoLlmAndKeepsExactTemplateAndPartialNotice() {
        var result=compose("호출하면 안 됩니다",List.of(summary(Map.of("eqp","CLN_001"),true,"PARTIAL")));
        assertThat(result.calls()).isZero();assertThat(sent).isEmpty();assertThat(constraints).isEmpty();assertThat(result.outcome()).isEqualTo(ComposerOutcome.COMPOSED);
        var answer=result.answer().orElseThrow();assertThat(answer.kind()).isEqualTo("PARTIAL");
        String sentence="CLN_001 사용자 목록 조회 결과는 조회된 보관 범위에서 0건이며, 전체 기간 완전성은 확인되지 않았습니다.";
        assertThat(answer.text()).isEqualTo(sentence+"[1]\n\n일부 자료를 확인할 수 없습니다.");assertThat(answer.sentences()).containsExactly(new GroundedAnswer.Sentence(sentence,List.of(1)));
    }
    @Test void emptySummaryFromATableToolRendersOnlyOneSentenceWithoutATableOrLlmCall() {
        var values=SchemaContracts.JSON.valueToTree(Map.of("toolId","asset.registration","arguments",Map.of("eqp","CLN_001"),"status","EMPTY","count",0,"missingFields",List.of()));
        var summary=new EvidenceUnit("SYSTEM",null,SchemaContracts.JSON.createObjectNode().put("toolId","asset.registration").put("ref","asset.registration:result-summary"),"",
            "[조회 결과 요약]\n도구(toolId): asset.registration\n조회 조건 > eqp: CLN_001\n조회 상태(status): EMPTY\n결과 건수(count): 0\n누락 항목(missingFields): 없음",
            List.of("READ"),"EMPTY","2026-11-24T05:33:12Z",List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of(),values);
        var result=compose("호출하면 안 됩니다",List.of(summary));var answer=result.answer().orElseThrow();
        String text="CLN_001 등록 정보 조회 결과는 0건입니다.";
        assertThat(result.outcome()).isEqualTo(ComposerOutcome.COMPOSED);assertThat(result.calls()).isZero();assertThat(sent).isEmpty();assertThat(constraints).isEmpty();
        assertThat(answer.kind()).isEqualTo("ANSWER");assertThat(answer.text()).isEqualTo(text+"[1]").doesNotContain("[조회 결과 요약]","\n");
        assertThat(answer.sentences()).containsExactly(new GroundedAnswer.Sentence(text,List.of(1)));
        assertThat(answer.citations()).singleElement().satisfies(citation->{assertThat(citation.number()).isEqualTo(1);assertThat(citation.evidenceId()).isEqualTo("e1");assertThat(citation.location()).isEqualTo(summary.location());});
    }
    @Test void completeZeroSummaryOmitsAbsentSubjectAndFormatsOnlyACompletePeriod() {
        for(var arguments:List.of(Map.<String,Object>of(),Map.<String,Object>of("from","2026-11-24T00:00:00Z"),Map.<String,Object>of("from","2026-11-24T00:00:00.123Z","to","2026-11-24T01:00:00Z"))) {
            var result=compose("미호출",List.of(summary(arguments,false,"EMPTY")));assertThat(result.calls()).isZero();assertThat(result.outcome()).isEqualTo(ComposerOutcome.COMPOSED);
            assertThat(result.answer().orElseThrow().kind()).isEqualTo("ANSWER");
            assertThat(result.answer().orElseThrow().text()).isEqualTo("사용자 목록 조회"+(arguments.containsKey("to")?" (기간 2026-11-24 09:00:00 (KST) ~ 2026-11-24 10:00:00 (KST))":"")+" 결과는 0건입니다.[1]");
        }
    }
    @Test void summaryPrecedesLlmAndRemainsInFactsWhenOtherEvidenceNeedsComposition() {
        var result=compose(rows(row("CLN_001의 설비 상태는 DOWN입니다.","e2")),List.of(summary(Map.of("eqp","CLN_001"),false,"EMPTY"),unit("CLN_001 DOWN","asset.snapshot","SUCCESS")));
        assertThat(result.calls()).isOne();assertThat(result.answer().orElseThrow().text()).isEqualTo("CLN_001 사용자 목록 조회 결과는 0건입니다.[1] CLN_001의 설비 상태는 DOWN입니다.[2]");
        assertThat(SchemaContracts.JSON.readTree(sent.getFirst().getLast().content()).path("facts")).hasSize(2);
    }
    @Test void tableOnlyAnswerCitesEveryConfiguredFactAndConvertsOnlyUtcLineValues() {
        var result=compose(rows(),List.of(unit(TABLE,"asset.registration","SUCCESS"),unit(TABLE.replace("CLN_001","CLN_002"),"asset.registration","STALE")));
        var answer=result.answer().orElseThrow();String table=TABLE.replace("[설비 등록 정보]","[설비 등록 정보] [1]").replace("조회 조건 > 설비(eqp): CLN_001\n","").replace("2026-04-28T05:02:28.248019Z","2026-04-28 14:02:28 (KST)");
        assertThat(answer.text()).isEqualTo(table+"\n\n"+table.replace("[1]","[2]").replace("CLN_001","CLN_002")+"\n\n최신 자료가 아닐 수 있습니다.");
        assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.sentences()).isEmpty();assertThat(answer.citations()).extracting(GroundedAnswer.Citation::number).containsExactly(1,2);
    }
    @Test void proseTableAndNoticesOccupyThreeParagraphsAndUnusedStatusesDoNotLeak() {
        var answer=compose(rows(row("CLN_001의 통신 모드는 PASSIVE입니다.","e1")),List.of(unit(TABLE,"asset.registration","PARTIAL"),unit("CLN_001","asset.snapshot","CONFLICT"))).answer().orElseThrow();
        assertThat(answer.text()).startsWith("CLN_001의 통신 모드는 PASSIVE입니다.[1]\n\n[설비 등록 정보] [1]\n").endsWith("\n\n일부 자료를 확인할 수 없습니다.").doesNotContain("조회 조건 >","서로 맞지 않는");
        var statuses=compose(rows(row("CLN_001 정보를 확인했습니다.","e1","e2","e3","e4")),List.of(unit("CLN_001","asset.snapshot","CONFLICT"),unit("CLN_001","asset.snapshot","PARTIAL"),unit("CLN_001","asset.snapshot","STALE"),unit("CLN_001","asset.snapshot","PARTIAL"))).answer().orElseThrow();
        assertThat(statuses.text()).endsWith("\n\n일부 자료를 확인할 수 없습니다.\n최신 자료가 아닐 수 있습니다.\n서로 맞지 않는 자료가 있어 하나로 확정할 수 없습니다.");
    }
    @Test void invalidIndividualSentenceMakesPartialWhileEmptyOrMalformedResultUsesFallbackWithoutRetry() {
        var units=List.of(unit("CLN_001 OFFLINE","asset.snapshot","SUCCESS"));
        var valid=row("CLN_001의 상태는 OFFLINE입니다.","e1");
        var answer=compose(rows(valid,row("CLN_001 999","e1")),units).answer().orElseThrow();assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.sentences()).hasSize(1);
        for(String raw:List.of(rows(),"not-json",rows(row("CLN_001 999","e1")),rows(row("확인했습니다.","labels")))) {
            var result=compose(raw,units);assertThat(result.calls()).isOne();answer=result.answer().orElseThrow();assertThat(answer.kind()).isEqualTo("PARTIAL");assertThat(answer.text()).isEqualTo(FactCheckTest.FALLBACK);assertThat(answer.citations()).hasSize(1);
        }
    }
    @Test void malformedEnvelopeIsNotRepairedEvenIfItContainsAValidSentence() {
        String valid=rows(row("CLN_001의 상태를 확인했습니다.","e1"));
        for(String raw:List.of(valid+" {}",valid.substring(0,valid.length()-1)+",\"extra\":true}",
            "{\"sentences\":[],\"sentences\":[{\"text\":\"확인했습니다.\",\"factIds\":[\"e1\"]}]}",
            "{\"sentences\":[{\"text\":\"확인했습니다.\",\"text\":\"확인했습니다.\",\"factIds\":[\"e1\"]}]}","{\"sentences\":{}}","null")) {
            var result=compose(raw,List.of(unit("CLN_001","asset.snapshot","SUCCESS")));
            assertThat(result.calls()).isOne();assertThat(result.answer().orElseThrow().kind()).as(raw).isEqualTo("PARTIAL");
            assertThat(result.answer().orElseThrow().text()).isEqualTo(FactCheckTest.FALLBACK);
        }
    }
    @Test void shortParaphraseOfALongWholeFactIsAllowedWithoutOriginalSpanSelection() {
        var result=compose(rows(row("CLN_001의 설비 상태는 DOWN입니다.","e1")),List.of(unit("CLN_001 DOWN "+"가".repeat(1001),"asset.snapshot","SUCCESS")));
        assertThat(result.calls()).isOne();assertThat(result.answer().orElseThrow().kind()).isEqualTo("ANSWER");
    }
    @Test void explicitEnglishShapeStillRunsLastAndPlainHelpKeepsUnstructuredContract() {
        String question="영어 출력 문구는 무엇인가요?";
        for(String text:List.of("저장 성공 안내가 나타납니다.","출력 문구는 READY입니다.")) {
            var result=service(rows(row(text,"e1")),CODEPOINTS).compose(job(question),input(question,List.of(unit("CLN_001 READY","asset.snapshot","SUCCESS")),Optional.empty(),List.of()));
            assertThat(result.answer().orElseThrow().kind()).isEqualTo(text.contains("READY")?"ANSWER":"PARTIAL");assertThat(result.calls()).isOne();
            if(!text.contains("READY")){assertThat(result.answer().orElseThrow().text()).isEqualTo(FactCheckTest.FALLBACK);assertThat(result.answer().orElseThrow().citations()).hasSize(1);}

        }
        sent.clear();constraints.clear();var result=service("사용 가능한 기능을 안내합니다.",CODEPOINTS).compose(job("도움말"),new ComposerService.Input(List.of(),List.of(),"",List.of(),"도움말"));
        assertThat(result.narrative()).contains("사용 가능한 기능을 안내합니다.");assertThat(constraints).containsExactly((StructuredOutput)null);
        var legacy=new ScriptedClient(reply(rows(row("확인했습니다.","e1"))));var policy=TokenBudgetPolicy.current();
        var manual=new ComposerService(legacy,new PromptReducer(m->100,policy),policy,CODEPOINTS,new ExpiryCheckpoint(),LlmCallObserver.NONE,a->{},"작성",text->List.of());
        assertThat(manual.compose(job("질문"),input("질문",List.of(unit("CLN_001","asset.snapshot","SUCCESS")),Optional.empty(),List.of())).answer().orElseThrow().kind()).isEqualTo("ANSWER");assertThat(legacy.sent).hasSize(1);
    }
    @Test void schemaAndEvidenceOverflowsStopBeforeInferenceWithoutDroppingConstraints() {
        var result=service("비밀표지",text->text.contains("\"factIds\"")&&text.contains("\"properties\"")?4000:10).compose(job("질문"),input("질문",List.of(unit("CLN_001","asset.snapshot","SUCCESS")),Optional.empty(),List.of()));
        assertThat(result.outcome()).isEqualTo(ComposerOutcome.INPUT_BUDGET_EXCEEDED);assertThat(result.calls()).isZero();assertThat(sent).isEmpty();
        assertThat(diagnostics).contains(LlmDiagnostic.OUTPUT_CONSTRAINT_BUDGET);
        result=service("비밀표지",text->4000).compose(job("질문"),input("질문",List.of(unit("CLN_001","asset.snapshot","SUCCESS")),Optional.empty(),List.of()));assertThat(result.outcome()).isEqualTo(ComposerOutcome.INPUT_BUDGET_EXCEEDED);assertThat(sent).isEmpty();assertThat(audits.toString()).doesNotContain("비밀표지");
    }
}

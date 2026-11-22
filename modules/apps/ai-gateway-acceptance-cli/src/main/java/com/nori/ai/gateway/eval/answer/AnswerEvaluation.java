package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.io.Json;
import java.util.*;

/** 준비 사슬은 각 대상의 새 대화에서 실행하고 다른 문항의 분모·판정을 바꾸지 않습니다. */
public final class AnswerEvaluation {
    public record Item(String id,String systemId,String origin,boolean answerable,boolean correct,String reason,
            List<String> missingFacts,List<EvalMarkdown.Location> missingLocations,String failedPredecessorId) {}
    public record Aggregate(int answerableTotal,int answerableCorrect,int refusalTotal,int refusalCorrect,
            double answerableRate,double refusalRate,boolean passed) {}
    public record Report(List<Item> items,Map<String,Aggregate> systems,JsonNode hashes) {}
    public record Result(int exitCode,Report report) {}
    public Result run(List<QuestionSet.Bound> questions,GatewayClient gateway,JsonNode hashes) {
        var revisions=new DocumentRevisionGuard(questions);
        var byId=new LinkedHashMap<String,QuestionSet.Bound>();for(var question:questions)byId.put(question.question().id(),question);
        var scorer=new AnswerScorer();var items=new ArrayList<Item>();
        for(var target:questions) {
            var chain=new ArrayList<QuestionSet.Bound>();var current=target;
            while(current!=null){chain.add(current);current=current.question().predecessorId()==null?null:byId.get(current.question().predecessorId());}
            Collections.reverse(chain);String conversation=null,failed=null;AnswerScorer.Grade grade=null;
            for(var step:chain) {
                var response=gateway.execute(step.question(),conversation);conversation=response.conversationId();
                if(response.response()!=null)revisions.check(response.response());
                grade=response.failure()==null?scorer.score(step,response.response()):scorer.failure(step,response.failure());
                if(!grade.correct()) {
                    if(step!=target){failed=step.question().id();grade=scorer.failure(target,"선행 문항 실패");}break;
                }
            }
            var q=target.question();items.add(new Item(q.id(),q.systemId(),q.origin(),q.answerable(),grade.correct(),grade.reason(),grade.missingFacts(),grade.missingLocations(),failed));
        }
        var systems=new TreeMap<String,Aggregate>();
        for(String system:items.stream().map(Item::systemId).distinct().toList()) {
            var selected=items.stream().filter(i->i.systemId().equals(system)).toList();
            int yes=(int)selected.stream().filter(Item::answerable).count(),yesCorrect=(int)selected.stream().filter(i->i.answerable()&&i.correct()).count();
            int no=selected.size()-yes,noCorrect=(int)selected.stream().filter(i->!i.answerable()&&i.correct()).count();QuestionSet.require(yes>0&&no>0);
            systems.put(system,new Aggregate(yes,yesCorrect,no,noCorrect,(double)yesCorrect/yes,(double)noCorrect/no,(long)yesCorrect*5>=(long)yes*4&&noCorrect==no));
        }
        return new Result(systems.values().stream().allMatch(Aggregate::passed)?0:1,new Report(List.copyOf(items),Collections.unmodifiableMap(systems),hashes.deepCopy()));
    }
    public static String hash(Object value){return Json.sha(Json.canonical(Json.MAPPER.valueToTree(value)));}
}

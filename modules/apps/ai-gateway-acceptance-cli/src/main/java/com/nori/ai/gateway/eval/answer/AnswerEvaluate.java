package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.*;
import com.nori.ai.gateway.eval.io.Json;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;

/** 환경의 권한·입력 결속과 HTTP 실행을 분리하여 미확정 신원을 만들지 않습니다. */
public final class AnswerEvaluate {
    public record Inputs(QuestionSet.Documents documents,List<String> grantedPermissions,List<String> requiredPermissions,
            String reviewPermission,GatewayClient gateway,JsonNode identity) {}
    @FunctionalInterface public interface InputResolver {
        Inputs resolve(Path root,Map<String,String> env,String system,URI gateway);
    }
    public static final InputResolver FILES=new EvaluationFiles();
    static final List<String> IDENTITY_FIELDS=List.of("codeSha256","generationModelSha256","embeddingModelSha256","configSha256");
    private final InputResolver resolver;
    public AnswerEvaluate(InputResolver resolver){this.resolver=Objects.requireNonNull(resolver);}
    public int execute(Args args,Path root,Map<String,String> env,PrintStream out) {
        args.only("--system","--questions","--gateway");String system=args.required("--system");
        if(!system.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,63}"))throw QuestionSet.invalid();
        URI uri;try{uri=URI.create(args.required("--gateway"));}catch(IllegalArgumentException invalid){throw GatewayClient.environment();}
        var schema=new QuestionSet();var initial=schema.load(root.resolve(args.required("--questions")));
        Inputs inputs=resolver.resolve(root,env,system,uri);
        try(var gateway=inputs.gateway()) {
            if(inputs.reviewPermission()==null||!inputs.grantedPermissions().contains(inputs.reviewPermission())
                    ||!inputs.grantedPermissions().containsAll(inputs.requiredPermissions()))throw GatewayClient.environment();
            var hashes=Json.MAPPER.createObjectNode();
            for(String name:IDENTITY_FIELDS)
                if(!inputs.identity().path(name).isTextual()||!inputs.identity().path(name).asText().matches("[0-9a-f]{64}"))throw GatewayClient.environment();
                else hashes.put(name,inputs.identity().path(name).textValue());
            var active=gateway.activeQuestions(inputs.grantedPermissions(),schema);var questions=schema.merge(initial,active,system);
            for(var question:questions)if(!inputs.grantedPermissions().containsAll(question.permissions()))throw GatewayClient.environment();
            var bound=questions.stream().map(q->schema.bind(q,inputs.documents())).toList();
            var object=hashes;
            object.put("initialSetSha256",Json.sha(Json.bytes(root.resolve(args.required("--questions")))));
            object.put("mergedSetSha256",AnswerEvaluation.hash(questions));
            var revisions=new TreeMap<String,Map<String,String>>();
            for(var question:questions) {
                for(var location:question.evidenceLocations())revision(revisions,location.bucket(),location.objectKey(),location.revision());
                for(var target:question.documentTargets())revision(revisions,target.bucket(),target.objectKey(),target.revision());
            }
            object.set("documentRevisions",Json.MAPPER.valueToTree(revisions.values()));object.put("documentRevisionsSha256",AnswerEvaluation.hash(revisions.values()));
            var result=new AnswerEvaluation().run(bound,gateway,hashes);out.println(Json.canonicalString(Json.MAPPER.valueToTree(result.report())));return result.exitCode();
        }
    }
    private static void revision(Map<String,Map<String,String>> revisions,String bucket,String key,String revision) {
        String id=bucket+"\n"+key;var row=Map.of("bucket",bucket,"objectKey",key,"revision",revision);var previous=revisions.putIfAbsent(id,row);
        QuestionSet.require(previous==null||previous.equals(row));
    }
}

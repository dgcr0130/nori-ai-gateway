package com.nori.ai.gateway.eval.answer;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.io.Json;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Clock;
import java.util.*;

/** 로컬 시험 Gateway의 자격·문서·config만 파일에서 읽고 지문은 측정자가 공급합니다. */
final class EvaluationFiles implements AnswerEvaluate.InputResolver {
    private static final Set<String> SIGNER_FIELDS=Set.of("privateJwk","issuer","subject","permissions",
        "codeSha256","generationModelSha256","embeddingModelSha256","configSha256");
    @Override public AnswerEvaluate.Inputs resolve(Path root,Map<String,String> env,String system,URI gateway) {
        try {
            String token=Files.readString(file(root,env,"AI_GATEWAY_EVAL_SERVICE_TOKEN_FILE"),StandardCharsets.UTF_8).strip();
            if(!token.matches("[A-Za-z0-9._~+/-]+=*"))throw GatewayClient.environment();
            // 문자열 parser를 써서 UTF-16 자동 감지와 자격 본문의 오류 노출을 막습니다.
            JsonNode input=Json.MAPPER.readTree(Files.readString(file(root,env,"AI_GATEWAY_EVAL_SIGNER_FILE"),StandardCharsets.UTF_8));
            QuestionSet.exact(input,SIGNER_FIELDS);
            if(!input.path("privateJwk").isObject())throw GatewayClient.environment();
            var identity=Json.MAPPER.createObjectNode();
            for(String name:AnswerEvaluate.IDENTITY_FIELDS) {
                String value=QuestionSet.text(input,name);if(!value.matches("[0-9a-f]{64}"))throw GatewayClient.environment();identity.put(name,value);
            }
            var permissions=QuestionSet.strings(input.path("permissions"),false);
            String issuer=QuestionSet.text(input,"issuer"),subject=QuestionSet.text(input,"subject");
            var config=system(root.resolve("config/ai-gateway.yaml"),system);
            if(!issuer.equals(literal(config.get("issuer"))))throw GatewayClient.environment();
            String review=permission(config.get("reviewPermission"));var required=new TreeSet<String>();
            for(String name:List.of("tools","documents","apps","traces","repositories")) {
                if(!config.containsKey(name))continue;
                if(!(config.get(name) instanceof List<?> items))throw GatewayClient.environment();
                for(Object item:items) {
                    var source=map(item);if(!(source.get("requiredPermissions") instanceof List<?> codes))throw GatewayClient.environment();
                    for(Object code:codes)required.add(permission(code));
                }
            }
            if(!permissions.contains(review)||!permissions.containsAll(required))throw GatewayClient.environment();
            var signer=new JwkAssertionSigner(input.path("privateJwk"),issuer,system,subject,permissions,Clock.systemUTC());
            return new AnswerEvaluate.Inputs(new LocalDocuments(root.resolve("rag-documents")),List.copyOf(permissions),List.copyOf(required),review,
                new GatewayClient(gateway,token,signer),identity);
        }catch(Exception failure){throw GatewayClient.environment();}
    }
    private static Path file(Path root,Map<String,String> env,String name) {
        String value=env.get(name);if(value==null||value.isBlank())throw GatewayClient.environment();return root.resolve(value).normalize();
    }
    private static Map<?,?> system(Path file,String id) throws java.io.IOException {
        var options=new LoaderOptions();options.setAllowDuplicateKeys(false);options.setMaxAliasesForCollections(0);
        Object value=new Yaml(new SafeConstructor(options)).load(Files.readString(file,StandardCharsets.UTF_8));
        var ai=map(map(value).get("ai"));var gateway=map(ai.get("gateway"));var systems=map(gateway.get("systems"));
        return map(systems.get(id));
    }
    private static Map<?,?> map(Object value){if(!(value instanceof Map<?,?> result))throw GatewayClient.environment();return result;}
    private static String literal(Object value){if(!(value instanceof String text)||text.isBlank()||text.contains("${"))throw GatewayClient.environment();return text;}
    private static String permission(Object value){String text=literal(value);if(!text.matches("[A-Z0-9_]{1,64}"))throw GatewayClient.environment();return text;}
}

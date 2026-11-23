package com.nori.ai.gateway.app.config;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import java.net.URI;
import java.util.*;

/** 시스템별 허용 범위는 기동 시 검증한 불변 설정으로만 공급합니다. */
public record GatewaySystemsProperties(Map<String, SystemConfig> systems) {
    private static final tools.jackson.databind.json.JsonMapper CONFIG_JSON=tools.jackson.databind.json.JsonMapper.builder()
        .enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(tools.jackson.databind.MapperFeature.ALLOW_COERCION_OF_SCALARS)
        .disable(tools.jackson.databind.DeserializationFeature.ACCEPT_FLOAT_AS_INT)
        .withCoercionConfig(tools.jackson.databind.type.LogicalType.Textual, config -> {
            config.setCoercion(tools.jackson.databind.cfg.CoercionInputShape.Integer,tools.jackson.databind.cfg.CoercionAction.Fail);
            config.setCoercion(tools.jackson.databind.cfg.CoercionInputShape.Float,tools.jackson.databind.cfg.CoercionAction.Fail);
            config.setCoercion(tools.jackson.databind.cfg.CoercionInputShape.Boolean,tools.jackson.databind.cfg.CoercionAction.Fail);
        }).build();
    public GatewaySystemsProperties {
        systems = systems == null ? Map.of() : Map.copyOf(systems);
        systems.forEach((id, value) -> { OwnerSystem.valueOf(id); Objects.requireNonNull(value); });
    }
    public static GatewaySystemsProperties load(org.springframework.core.env.Environment environment) {
        if (environment instanceof org.springframework.core.env.ConfigurableEnvironment configurable) {
            for (var source : configurable.getPropertySources()) {
                if (!(source instanceof org.springframework.core.env.EnumerablePropertySource<?> enumerable)) continue;
                for (String name : enumerable.getPropertyNames()) {
                    var origin=org.springframework.boot.origin.OriginLookup.getOrigin(source,name);
                    if (!(origin instanceof org.springframework.boot.origin.TextResourceOrigin text)
                            || !"ai-gateway.yaml".equals(text.getResource().getFilename())) continue;
                    // 일반 Map 바인딩은 schema의 required 배열을 숫자 키 Map으로 바꿉니다. 원본 YAML의 자료형을 보존합니다.
                    var options=new org.yaml.snakeyaml.LoaderOptions();options.setAllowDuplicateKeys(false);options.setMaxAliasesForCollections(0);
                    try(var input=text.getResource().getInputStream()) {
                        Object loaded=new org.yaml.snakeyaml.Yaml(new org.yaml.snakeyaml.constructor.SafeConstructor(options)).load(input);
                        var root=SchemaContracts.JSON.valueToTree(loaded);
                        var systems=root.path("ai").path("gateway").path("systems");
                        if (systems.isMissingNode()) return new GatewaySystemsProperties(Map.of());
                        if (!systems.isObject()) throw invalid();
                        var resolved=resolve(SchemaContracts.JSON.convertValue(systems,new tools.jackson.core.type.TypeReference<Map<String,Object>>() {}),environment);
                        var values=CONFIG_JSON.convertValue(resolved,new tools.jackson.core.type.TypeReference<Map<String,SystemConfig>>() {});
                        return new GatewaySystemsProperties(values);
                    } catch(java.io.IOException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(GatewaySystemsProperties.class.getName()),"GATEWAY_SYSTEMS_PROPERTIES_FAILED",failure); throw invalid(); }
                }
            }
        }
        var values=org.springframework.boot.context.properties.bind.Binder.get(environment).bind("ai.gateway.systems",
            org.springframework.boot.context.properties.bind.Bindable.mapOf(String.class,SystemConfig.class),
            new org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler(org.springframework.boot.context.properties.bind.BindHandler.DEFAULT)).orElse(Map.of());
        return new GatewaySystemsProperties(values);
    }
    private static Object resolve(Object value,org.springframework.core.env.Environment environment) {
        if (value instanceof String text) return environment.resolveRequiredPlaceholders(text);
        if (value instanceof Map<?,?> map) {
            var result=new LinkedHashMap<String,Object>();
            for(var entry:map.entrySet()) {
                if (!(entry.getKey() instanceof String key)) throw invalid();
                result.put(key,resolve(entry.getValue(),environment));
            }
            return result;
        }
        if (value instanceof List<?> list) return list.stream().map(item->resolve(item,environment)).toList();
        return value;
    }
    public record SystemConfig(String issuer, String serviceTokenSha256File, String actorJwksFile,
            String backendUrl, String gatewayTokenFile, List<Tool> tools, List<Document> documents,
            List<App> apps, List<Repository> repositories, List<Trace> traces,
            String reviewPermission, Map<String,String> glossary) {
        public SystemConfig {
            text(issuer, 128); text(serviceTokenSha256File, 4096); text(actorJwksFile, 4096); permission(reviewPermission);
            tools = list(tools); documents = list(documents); apps = list(apps);
            repositories = list(repositories); traces = list(traces); glossary = glossary == null ? Map.of() : Map.copyOf(glossary);
            if (!tools.isEmpty()) { text(backendUrl, 4096); text(gatewayTokenFile, 4096); }
            if (backendUrl != null) {
                URI url = URI.create(backendUrl);
                if (!Set.of("http", "https").contains(url.getScheme()) || url.getHost() == null || url.getRawUserInfo() != null
                        || url.getRawQuery() != null || url.getRawFragment() != null || !url.getRawPath().isEmpty()) throw invalid();
            }
            if (tools.stream().map(Tool::id).distinct().count() != tools.size()) throw invalid();
            for (int i = 0; i < documents.size(); i++) for (int j = i + 1; j < documents.size(); j++) {
                var a = documents.get(i); var b = documents.get(j);
                if (a.bucket().equals(b.bucket()) && (a.prefix().startsWith(b.prefix()) || b.prefix().startsWith(a.prefix()))) throw invalid();
            }
            glossary.forEach((term, expansion) -> { text(term, 64); text(expansion, 256); });
            for (String term : glossary.keySet()) cycle(term, glossary, new HashSet<>());
        }
    }
    public record Tool(String id, String description, Map<String,Object> inputSchema, Map<String,Object> outputSchema,
            List<String> requiredPermissions, Integer timeoutMs) {
        public Tool {
            if (id == null || !id.matches("[A-Za-z0-9_.-]{1,128}")) throw invalid();
            text(description, 1024); requiredPermissions = permissions(requiredPermissions, false);
            timeoutMs = timeoutMs == null ? 10000 : timeoutMs;
            if (timeoutMs < 1 || timeoutMs > 10000 || inputSchema == null || outputSchema == null) throw invalid();
            var input = SchemaContracts.JSON.valueToTree(inputSchema); var output = SchemaContracts.JSON.valueToTree(outputSchema);
            SchemaContracts.validateSchema(input); SchemaContracts.validateSchema(output);
            if (!input.isObject() || !output.isObject() || ("object".equals(input.path("type").asString())
                    && !input.path("additionalProperties").isBoolean()) || ("object".equals(input.path("type").asString())
                    && input.path("additionalProperties").asBoolean())) throw invalid();
            inputSchema = com.nori.ai.gateway.core.job.JobRequestInput.immutableFields(inputSchema);
            outputSchema = com.nori.ai.gateway.core.job.JobRequestInput.immutableFields(outputSchema);
        }
    }
    public record Document(String bucket, String prefix, List<String> requiredPermissions) {
        public Document { text(bucket, 255); if (prefix == null) throw invalid(); requiredPermissions = permissions(requiredPermissions, true); }
    }
    public record App(String name, String namespace, List<String> requiredPermissions) {
        public App { text(name, 256); text(namespace, 256); requiredPermissions = permissions(requiredPermissions, true); }
    }
    public record Trace(String service, String namespace, List<String> requiredPermissions) {
        public Trace { text(service, 256); text(namespace, 256); requiredPermissions = permissions(requiredPermissions, true); }
    }
    public record Repository(String projectId, String ref, String pathPrefix, List<String> requiredPermissions) {
        public Repository { text(projectId, 256); text(ref, 256); if (pathPrefix == null) throw invalid(); requiredPermissions = permissions(requiredPermissions, true); }
    }
    private static <T> List<T> list(List<T> values) { return values == null ? List.of() : List.copyOf(values); }
    private static List<String> permissions(List<String> values, boolean required) {
        var result = list(values); if (required && result.isEmpty()) throw invalid(); result.forEach(GatewaySystemsProperties::permission); return result;
    }
    private static void permission(String value) { if (value == null || !value.matches("[A-Z0-9_]{1,64}")) throw invalid(); }
    private static void text(String value, int max) { if (value == null || value.isBlank() || value.codePointCount(0, value.length()) > max) throw invalid(); }
    private static void cycle(String term, Map<String,String> glossary, Set<String> visiting) {
        if (!visiting.add(term)) throw invalid();
        for (String next : glossary.keySet()) if (glossary.get(term).contains(next)) cycle(next, glossary, visiting);
        visiting.remove(term);
    }
    private static IllegalArgumentException invalid() { return new IllegalArgumentException("시스템 설정이 잘못되었습니다"); }
    @Override public String toString() { return "GatewaySystemsProperties[보호됨]"; }
}

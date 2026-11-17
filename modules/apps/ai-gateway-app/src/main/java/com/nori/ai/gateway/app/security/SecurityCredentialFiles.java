package com.nori.ai.gateway.app.security;

import com.nori.ai.gateway.core.scope.OwnerSystem;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;

public final class SecurityCredentialFiles {
    private static final Set<String> PRIVATE_FIELDS = Set.of("d","p","q","dp","dq","qi","oth","k");
    private final List<TokenBinding> tokens;
    private record KeyId(OwnerSystem system, String kid) {}
    private final Map<KeyId,JWK> keys;
    private final Map<OwnerSystem,String> issuers;
    private record TokenBinding(byte[] digest, OwnerSystem owner) {}

    public SecurityCredentialFiles(com.nori.ai.gateway.app.config.GatewaySystemsProperties properties) {
        try {
            var mapper = JsonMapper.builder(JsonFactory.builder().disable(tools.jackson.core.TokenStreamFactory.Feature.CHARSET_DETECTION)
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build())
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
            var loadedTokens = new ArrayList<TokenBinding>();
            var loadedKeys = new LinkedHashMap<KeyId,JWK>();
            var loadedIssuers = new LinkedHashMap<OwnerSystem,String>();
            var digests = new HashSet<String>();
            for (var entry : properties.systems().entrySet()) {
                var owner = OwnerSystem.valueOf(entry.getKey()); var config = entry.getValue();
                String digest = new String(read(Path.of(config.serviceTokenSha256File())), StandardCharsets.UTF_8).strip();
                if (!digest.matches("[0-9a-f]{64}") || !digests.add(digest)) throw new IllegalArgumentException();
                loadedTokens.add(new TokenBinding(HexFormat.of().parseHex(digest),owner));
                loadedIssuers.put(owner,config.issuer());
                if (config.gatewayTokenFile() != null) {
                    String token = new String(read(Path.of(config.gatewayTokenFile())),StandardCharsets.UTF_8).strip();
                    if (token.isEmpty() || !token.matches("[A-Za-z0-9._~+/-]+=*")) throw new IllegalArgumentException();
                }
                var jwks = mapper.readTree(read(Path.of(config.actorJwksFile()))); rejectPrivate(jwks);
                if (!jwks.isObject() || jwks.get("keys") == null || !jwks.get("keys").isArray() || jwks.get("keys").isEmpty()) throw new IllegalArgumentException();
                for (var node : jwks.get("keys")) {
                    JWK key = JWK.parse(mapper.writeValueAsString(node)); validatePublicKey(key);
                    if (key.getKeyID() == null || key.getKeyID().isBlank()
                            || loadedKeys.putIfAbsent(new KeyId(owner,key.getKeyID()),key) != null) throw new IllegalArgumentException();
                }
            }
            tokens = List.copyOf(loadedTokens); keys = Map.copyOf(loadedKeys); issuers = Map.copyOf(loadedIssuers);
        } catch (Exception failure) {
            // 파서가 읽은 자격 값을 예외 원인으로 공개하지 않습니다.
            throw new IllegalStateException("인입 인증 자격 파일이 잘못되었습니다");
        }
    }
    private static byte[] read(Path path) throws Exception {
        byte[] bytes = Files.readAllBytes(Objects.requireNonNull(path));
        StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes));
        return bytes;
    }
    private static void rejectPrivate(JsonNode node) {
        if (node.isObject()) for (var entry : node.properties()) {
            if (PRIVATE_FIELDS.contains(entry.getKey())) throw new IllegalArgumentException();
            rejectPrivate(entry.getValue());
        }
        else if (node.isArray()) for (var child : node) rejectPrivate(child);
    }
    private static void validatePublicKey(JWK key) throws Exception {
        if (key.isPrivate()) throw new IllegalArgumentException();
        String algorithm = key.getAlgorithm() == null ? null : key.getAlgorithm().getName();
        Set<String> compatible;
        if (key instanceof ECKey ec) {
            ec.toECPublicKey();
            compatible = Curve.P_256.equals(ec.getCurve()) ? Set.of("ES256") : Curve.P_384.equals(ec.getCurve()) ? Set.of("ES384")
                : Curve.P_521.equals(ec.getCurve()) ? Set.of("ES512") : Set.of();
        } else if (key instanceof RSAKey rsa) {
            rsa.toRSAPublicKey(); compatible = Set.of("RS256","RS384","RS512","PS256","PS384","PS512");
        } else if (key instanceof OctetKeyPair okp) {
            okp.toPublicKey(); compatible = Set.of("EdDSA","Ed25519","Ed448");
            if (!Set.of(Curve.Ed25519,Curve.Ed448).contains(okp.getCurve())) throw new IllegalArgumentException();
        } else throw new IllegalArgumentException();
        if (compatible.isEmpty() || algorithm != null && !compatible.contains(algorithm)) throw new IllegalArgumentException();
    }
    Optional<VerifiedServiceIdentity> authenticate(String token) {
        if (token == null || token.isEmpty()) return Optional.empty();
        final byte[] digest;
        try { digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)); }
        catch (java.security.NoSuchAlgorithmException failure) { throw new IllegalStateException("서비스 인증을 처리할 수 없습니다"); }
        OwnerSystem owner = null;
        for (var binding : tokens) if (MessageDigest.isEqual(digest,binding.digest())) owner = binding.owner();
        return owner == null ? Optional.empty() : Optional.of(new VerifiedServiceIdentity(owner,"service:" + owner.name()));
    }
    JWK key(OwnerSystem system, String kid) { return kid == null ? null : keys.get(new KeyId(system,kid)); }
    String issuer(OwnerSystem system) { return issuers.get(system); }
}

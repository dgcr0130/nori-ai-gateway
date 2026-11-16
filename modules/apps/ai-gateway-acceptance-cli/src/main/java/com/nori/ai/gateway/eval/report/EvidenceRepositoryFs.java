package com.nori.ai.gateway.eval.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.EvalException;
import com.nori.ai.gateway.eval.io.Json;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * 수용 결과 정본 저장소. 위치는 acceptance/results/&lt;단계 폴더&gt; 이고, 단계 식별값(F0~F4)은 레코드 데이터로 두며
 * 폴더 이름은 단계 내용으로 정한다(operations/deploy-checks/result_folders.py 와 같은 대응).
 */
public final class EvidenceRepositoryFs {
    public static final String RESULTS_ROOT = "acceptance/results";
    private static final Map<String, String> STAGE_FOLDERS = Map.of(
        "F0", "inference-diagnosis", "F1", "gateway-foundation", "F2", "llm-integration", "F3", "rag-integration", "F4", "tc-integration");
    private final Path root;
    public EvidenceRepositoryFs(Path root) { this.root = root.toAbsolutePath().normalize(); }
    /** 단계 결과 폴더. 알 수 없는 단계는 EVIDENCE_PATH_INVALID 로 거부한다. */
    public Path stageDirectory(String stage) {
        String folder = STAGE_FOLDERS.get(stage);
        if (folder == null) throw new EvalException(2, "EVIDENCE_PATH_INVALID");
        return root.resolve(RESULTS_ROOT).resolve(folder);
    }
    public List<JsonNode> readAll(String stage) {
        if (!STAGE_FOLDERS.containsKey(stage)) return List.of();
        Path directory = stageDirectory(stage);
        if (!Files.isDirectory(directory)) return List.of();
        try (var children = Files.list(directory)) {
            List<Path> records = new ArrayList<>();
            for (Path namespace : children.filter(p -> recordNamespace(p.getFileName().toString()) && Files.isDirectory(p)).toList()) {
                try (var files = Files.walk(namespace)) { files.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".json")).forEach(records::add); }
            }
            return records.stream().sorted().map(Json::read).toList();
        }
        catch (IOException e) { throw new EvalException(5, "EVIDENCE_READ_FAILED"); }
    }
    private static boolean recordNamespace(String namespace) {
        // 운영 원본은 명시 ingest 후 정본만 읽는다. 정본 내부의 손상·미등록 레코드는 기존 검증으로 거부한다.
        return Set.of("runs", "measurements", "perf", "user-run").contains(namespace) || LeafCatalog.validLeafId(namespace);
    }
    public Path store(JsonNode record) {
        String stage = Json.required(record, "stage");
        String kind = Json.required(record, "kind");
        String group = kind.equals("LEAF") ? Json.required(record, "leafId") : kind.equals("MEASUREMENT") ? "measurements/" + Json.required(record, "measurementId").replace(':', '_') : "runs";
        if (!STAGE_FOLDERS.containsKey(stage) || !group.matches("[A-Za-z0-9_/-]+") || group.contains("..")) throw new EvalException(2, "EVIDENCE_PATH_INVALID");
        String hash = Json.required(record, "contentSha256"); if (!hash.matches("[0-9a-f]{64}")) throw new EvalException(2, "EVIDENCE_HASH_MISMATCH");
        Path output = stageDirectory(stage).resolve(group + "/" + hash + ".json").normalize();
        if (!output.startsWith(root)) throw new EvalException(2, "EVIDENCE_PATH_INVALID");
        if (Files.exists(output)) { if (!Arrays.equals(Json.bytes(output), Json.canonical(record))) throw new EvalException(5, "EVIDENCE_IMMUTABLE"); return output; }
        Json.write(output, record); return output;
    }
}

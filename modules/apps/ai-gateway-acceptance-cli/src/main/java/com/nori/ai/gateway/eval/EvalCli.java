package com.nori.ai.gateway.eval;

import com.fasterxml.jackson.databind.JsonNode;
import com.nori.ai.gateway.eval.ci.BuildIdentityManifest;
import com.nori.ai.gateway.eval.ci.CiEvidence;
import com.nori.ai.gateway.eval.f3.EmbeddingProfile;
import com.nori.ai.gateway.eval.io.Json;
import com.nori.ai.gateway.eval.report.*;
import java.io.PrintStream;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public final class EvalCli {
    private final Map<String,String> env;
    private final PrintStream out;
    private final PrintStream err;
    private final Path root;
    public EvalCli(Map<String,String> env, PrintStream out, PrintStream err, Path root) { this.env = Map.copyOf(env); this.out = out; this.err = err; this.root = root.toAbsolutePath().normalize(); }
    public static void main(String[] args) { System.exit(new EvalCli(System.getenv(), System.out, System.err, Path.of(".")).execute(args)); }
    public int execute(String... arguments) {
        try { dispatch(arguments); return 0; }
        catch (EvalException e) { err.println(e.getMessage()); return e.exitCode(); }
        catch (RuntimeException e) { err.println("EVAL_INPUT_INVALID"); return 2; }
    }
    private void dispatch(String[] arguments) {
        if (arguments.length == 0) throw new EvalException(2, "UNKNOWN_COMMAND");
        // F3 profile 은 자체 플래그를 쓰므로 공통 인자 해석 전에 분기합니다.
        if (arguments[0].equals("f3")) { EmbeddingProfile.execute(arguments, root, out); return; }
        String command = arguments[0]; boolean nested = Set.of("test-evidence", "record", "perf", "f3", "ci-evidence").contains(command);
        String operation = nested && arguments.length > 1 ? arguments[1] : "";
        if (nested && operation.startsWith("--")) throw new EvalException(2, "UNKNOWN_COMMAND");
        Args args = new Args(arguments, nested ? 2 : 1, Set.of("--require-executed", "--require-docker"));
        switch (command) {
            case "leaf-extract" -> {
                args.only("--otr-dir", "--task", "--module", "--out", "--current-fingerprint", "--require-executed", "--catalog", "--runtime-classpath-file");
                Path runtimeClasspathFile = args.optional("--runtime-classpath-file") == null ? null : path(args.required("--runtime-classpath-file"));
                new OtrLeafExtractor().extract(path(args.required("--otr-dir")), args.required("--task"), args.required("--module"), path(args.required("--out")), args.required("--current-fingerprint"), args.flag("--require-executed"), catalog(args), env, runtimeClasspathFile);
            }
            case "test-evidence" -> {
                if (!operation.equals("verify")) throw new EvalException(2, "UNKNOWN_COMMAND");
                args.only("--tasks", "--selector", "--required", "--require-docker", "--expect-build-identity");
                BuildIdentityManifest identity = args.optional("--expect-build-identity") == null ? null : BuildIdentityManifest.load(path(args.required("--expect-build-identity")));
                List<String> tasks = new ArrayList<>(); Map<String,List<String>> classes = new LinkedHashMap<>();
                if (args.optional("--tasks") != null) tasks.addAll(List.of(args.required("--tasks").split(",")));
                else {
                    String selector = args.required("--selector");
                    for (String line : lines(path(args.required("--required")))) {
                        if (line.isBlank() || line.startsWith("#")) continue;
                        String[] parts = line.strip().split("[ |]+", 2); String module = parts[0].startsWith(":") ? parts[0] : ":" + parts[0];
                        String task = module + ":" + selector; tasks.add(task);
                        if (parts.length == 2) classes.put(task, List.of(parts[1].split(",")));
                    }
                }
                new TestEvidenceVerifier().verify(root, tasks, args.flag("--require-docker"), classes);
                if (identity != null) identity.verifyTaskSummaries(root, tasks, env.get("CI_COMMIT_SHA"));
                out.println("TEST_EVIDENCE_VERIFIED");
            }
            case "ingest", "gate", "record", "perf" -> {
                args.only("--stage", "--parent-run-id", "--input", "--source", "--leaf", "--run-id", "--field", "--samples", "--criteria", "--out", "--catalog");
                String stage = args.required("--stage"), parent = args.value("--parent-run-id", env.get("AIGW_PARENT_RUN_ID"));
                if (!stage.matches("F[0-4]") || parent == null || !parent.matches("[A-Za-z0-9_.:-]{1,128}")) throw new EvalException(2, "PARENT_RUN_ID_MISSING");
                LeafCatalog catalog = catalog(args); EvidenceRepositoryFs repository = new EvidenceRepositoryFs(root);
                if (command.equals("ingest")) new EvidenceIngestor(repository, catalog).ingest(path(args.required("--input")), args.value("--source", "measurement"), stage, parent);
                else if (command.equals("gate")) {
                    var report = new GateEvaluator().evaluate(stage, parent, repository.readAll(stage), catalog);
                    if (args.optional("--out") == null) new GateReportWriter().write(repository.stageDirectory(stage).resolve("acceptance-report.md"), report);
                    else new GateReportWriter().writeNew(derivedGateReport(path(args.required("--out"))), report);
                    out.println(report.result());
                } else if (command.equals("record")) new ManualRecorder(catalog, repository).record(operation, stage, args.required("--leaf"), args.required("--run-id"), parent, args.all("--field"));
                else {
                    if (!operation.equals("report")) throw new EvalException(2, "UNKNOWN_COMMAND");
                    JsonNode manifest = repository.readAll(stage).stream().filter(n -> n.path("status").asText().equals("PARENT") && n.path("parentRunId").asText().equals(parent)).findFirst().orElseThrow(() -> new EvalException(2, "PARENT_MANIFEST_MISSING"));
                    new PerfReporter().report(stage, args.required("--leaf"), parent, args.all("--samples").stream().map(this::path).toList(), path(args.required("--criteria")), path(args.required("--out")), root, catalog, manifest);
                }
            }
            case "ci-evidence" -> {
                args.only("--out", "--required", "--sha", "--bundle", "--expect-build-identity");
                CiEvidence evidence = new CiEvidence(root, CiEvidence.processes());
                switch (operation) {
                    case "pack" -> { out.println("CI_EVIDENCE_PACKED " + evidence.pack(path(args.required("--out")), path(args.value("--required", "acceptance/required-integration-tests.txt"))).getFileName()); }
                    case "verify" -> {
                        evidence.verify(path(args.required("--bundle")), args.required("--sha"), path(args.required("--required")), BuildIdentityManifest.load(path(args.required("--expect-build-identity"))));
                        out.println("CI_EVIDENCE_VERIFIED");
                    }
                    default -> throw new EvalException(2, "UNKNOWN_COMMAND");
                }
            }
            default -> throw new EvalException(2, "UNKNOWN_COMMAND");
        }
    }
    /** gate --out: 회차별 파생 보고서는 레포 안·수용 결과 정본(acceptance/results) 밖의 .md 파일만 허용합니다. */
    private Path derivedGateReport(Path output) {
        if (!output.startsWith(root) || output.equals(root) || output.startsWith(root.resolve(EvidenceRepositoryFs.RESULTS_ROOT))
            || !output.getFileName().toString().endsWith(".md")) throw new EvalException(2, "GATE_REPORT_PATH_INVALID");
        return output;
    }
    private LeafCatalog catalog(Args args) { return LeafCatalog.load(args.optional("--catalog") == null ? null : path(args.optional("--catalog"))); }
    private Path path(String text) { return root.resolve(text).normalize(); }
    private static List<String> lines(Path path) { try { return Files.readAllLines(path); } catch (IOException e) { throw new EvalException(2, "INPUT_MISSING"); } }
}

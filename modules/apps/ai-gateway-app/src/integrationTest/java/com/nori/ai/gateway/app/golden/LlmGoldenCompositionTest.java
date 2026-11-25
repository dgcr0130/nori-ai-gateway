package com.nori.ai.gateway.app.golden;

import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.result.GroundedAnswer;
import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import static org.junit.jupiter.api.Assertions.*;

/** 운영 fixture를 복제하지 않고 실제 설정·선택 예산·작성기·FactCheck를 통과시킵니다. */
@Tag("llm-golden")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
@Isolated
class LlmGoldenCompositionTest {
    /** query가 있으면 운영 CODE_TERM 계획처럼 검색어만 넘기는 코드 사례입니다. */
    record Case(String id,String fixture,String tool,String question,String observedAt,List<String> targets,String query) {}
    private final Path root=Path.of(Objects.requireNonNull(System.getProperty("aigw.rootDir"))).toAbsolutePath().normalize();
    private final Path report=root.resolve("build/llm-golden/composition-report.json");
    private final List<Map<String,Object>> records=new ArrayList<>();
    private GoldenClassificationAdapter adapter;
    private Throwable setupFailure;
    private boolean initialized;
    @TestFactory Stream<DynamicTest> compositions() throws IOException {
        writeReport();
        return Stream.of(
            new Case("snapshot-103732","business-grounding/snapshot","tc.eqp.getOperationalSnapshot","설비 CLN_001의 현재 운영 스냅샷을 조회해 연결 상태, 제어 상태, 설비 상태를 알려 주세요.","2026-11-24T01:37:32Z",List.of("CLN_001"),null),
            new Case("model-143300","business-grounding/model","tc.model.read","tc.model.read 도구를 사용하여 eqp=CLN_001에 연결된 모델과 메시지 정의를 읽어 주세요.","2026-11-24T05:33:00Z",List.of("CLN_001"),null),
            new Case("dlq-143312","business-grounding/dlq","tc.dlq.list","설비 CLN_001의 DLQ 데드레터 큐 목록을 조회해 실패 메시지와 오류 내역을 알려 주세요.","2026-11-24T05:33:12Z",List.of("CLN_001"),null),
            new Case("wrong-equipment-12","business-grounding/snapshot","tc.eqp.getOperationalSnapshot","PHOTO_001 호기의 등록 정보를 알려 주세요.","2026-11-24T01:37:32Z",List.of("PHOTO_001"),null),
            // 2026-11-25 운영 답변이 GatewayEquipmentService 주석의 역할을 EquipmentContextRegistry에 붙인 질문과 같은 검색 결과입니다.
            new Case("code-usage-20261125","code-grounding/equipment-context-registry","git.searchCode","EquipmentContextRegistry 클래스는 어디서 쓰여?","2026-11-24T23:19:00Z",List.of(),"EquipmentContextRegistry"))
            .map(value->DynamicTest.dynamicTest(value.id(),()->verify(value)));
    }
    private void verify(Case value) throws Exception {
        long started=System.nanoTime();var record=new LinkedHashMap<String,Object>();record.put("case",value);record.put("passed",false);
        try {
            if(!initialized){initialized=true;try{adapter=new GoldenClassificationAdapter(root);}catch(Exception|LinkageError failure){setupFailure=failure;}}
            assertNull(setupFailure,()->"실제 LLM 초기화 실패: "+setupFailure);assertNotNull(adapter);
            var actual=adapter.compose(root,value);record.put("actual",actual);
            var answer=(GroundedAnswer)actual.get("answer");
            assertEquals("COMPOSED",actual.get("outcome"));assertEquals(true,actual.get("fitsSelected"));
            if(value.id().equals("dlq-143312")) {
                String text="CLN_001 DLQ 목록 조회 결과는 조회된 보관 범위에서 0건이며, 전체 기간 완전성은 확인되지 않았습니다.";
                assertEquals(0,actual.get("calls"));assertEquals(0,adapter.calls());assertTrue(adapter.responses().isEmpty());
                assertEquals(List.of(new GroundedAnswer.Sentence(text,List.of(1))),answer.sentences());assertEquals(text+"[1]\n\n일부 자료를 확인할 수 없습니다.",answer.text());
            } else if(value.id().equals("code-usage-20261125")) {
                assertEquals(1,actual.get("calls"));assertFalse(answer.sentences().isEmpty());
                // 두 Provider 역할은 GatewayEquipmentService.java 주석에만 있습니다. 역할·구현을 말하는 자리의 가장 가까운 주어가
                // EquipmentContextRegistry이면 운영 오답("EquipmentContextRegistry는 EquipmentContextProfileProvider 역할을 하여")과 같은 귀속 오류입니다.
                var role=java.util.regex.Pattern.compile("(EquipmentContextProfileProvider|EquipmentInfoProvider)\\s*(역할|로서|으로서|를 구현|을 구현|인터페이스)");
                var subject=java.util.regex.Pattern.compile("([A-Za-z_][A-Za-z0-9_]*)(?:는|은|이|가)(?=\\s)");
                for(var sentence:answer.sentences()) {
                    var roles=role.matcher(sentence.text());
                    while(roles.find()) {
                        String nearest=null;var subjects=subject.matcher(sentence.text().substring(0,roles.start()));
                        while(subjects.find())nearest=subjects.group(1);
                        assertNotEquals("EquipmentContextRegistry",nearest,sentence.text());
                    }
                }
            } else if(value.id().equals("wrong-equipment-12")) {
                assertFalse(answer.text().contains("6100"));assertFalse(answer.text().contains("0.0.0.0"));assertFalse(answer.text().contains("PASSIVE"));
            } else {
                assertEquals(1,actual.get("calls"));assertEquals(1,adapter.calls());assertEquals(0L,((Number)actual.get("deletedSentences")).longValue());assertFalse(answer.sentences().isEmpty());
                if(value.id().equals("snapshot-103732")) {
                    String prose=String.join(" ",answer.sentences().stream().map(GroundedAnswer.Sentence::text).toList());
                    assertTrue(prose.contains("OFFLINE"));assertTrue(prose.contains("DOWN"));var times=java.util.regex.Pattern.compile("\\d{4}-\\d{2}-\\d{2}[^\\s]*[ T]\\d{2}:\\d{2}(?::\\d{2})?(?: \\(KST\\))?").matcher(prose);
                    while(times.find())assertTrue(times.group().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\(KST\\)"),times.group());
                    assertFalse(prose.matches("(?s).*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.*"));assertEquals("PARTIAL",answer.kind());
                } else {assertEquals(25,actual.get("inputCount"));assertTrue(answer.text().contains("DEMO_MAKER_MODEL_T02_admin"));int selected=((Number)actual.get("selectedCount")).intValue();assertTrue(selected>0&&selected<=25);assertEquals(selected<25,actual.get("truncatedItems"));}
            }
            record.put("passed",true);
        } catch(Exception|AssertionError|LinkageError failure) {
            record.put("failureClass",failure.getClass().getName());record.put("failure",String.valueOf(failure.getMessage()));throw failure;
        } finally {
            record.put("llmResponses",adapter==null?List.of():adapter.responses());record.put("llmCalls",adapter==null?0:adapter.calls());
            record.put("elapsedMillis",Duration.ofNanos(System.nanoTime()-started).toMillis());records.add(record);writeReport();
        }
    }
    private void writeReport() throws IOException {
        Files.createDirectories(report.getParent());Files.writeString(report,SchemaContracts.JSON.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of("recordedAt",Instant.now().toString(),"cases",records))+"\n");
    }
    @AfterAll void close() throws IOException {try{if(adapter!=null)adapter.close();}finally{writeReport();}}
}

package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.budget.ChatMessage;
import com.nori.ai.gateway.core.evidence.EvidenceJson;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/** 선택 예산과 작성 전송이 공유하는 전체 사실 직렬화입니다. values는 서버 대조에만 남깁니다. */
public final class GroundedFacts {
    public static final String PROMPT_PATH="prompts/compose-v2.txt";
    private static final String PROMPT=loadPrompt();
    private static final DateTimeFormatter KST=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss '(KST)'",Locale.ROOT)
        .withZone(ZoneId.of("Asia/Seoul"));
    private static final Pattern UTC=Pattern.compile("(?<![a-zA-Z0-9_])\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|\\+00:00)(?![a-zA-Z0-9_])");
    private GroundedFacts() {}
    public static String prompt(){return PROMPT;}
    public static String kst(String timestamp) {
        return timestamp==null?null:KST.format(OffsetDateTime.parse(timestamp).toInstant());
    }
    public static String kstText(String text) {
        var matcher=UTC.matcher(text);var converted=new StringBuilder();
        while(matcher.find()) {
            String replacement=matcher.group();
            try {replacement=kst(replacement);}catch(DateTimeException invalid){/* 실제 날짜가 아닌 본문 토큰은 원문 그대로 대조합니다. */}
            matcher.appendReplacement(converted,java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(converted);return converted.toString();
    }
    public static String kstLine(String line) {
        int separator=line.indexOf(": ");if(separator<0)return line;
        String value=line.substring(separator+2);
        return UTC.matcher(value).matches()?line.substring(0,separator+2)+kstText(value):line;
    }
    public static String data(String question,List<EvidenceUnit> units,Optional<ClassificationService.HistoryPair> history) {
        var user=SchemaContracts.JSON.createObjectNode().put("question",question);var facts=user.putArray("facts");
        for(int i=0;i<units.size();i++) {
            var unit=units.get(i);var fact=facts.addObject().put("id","e"+(i+1)).put("sourceType",unit.sourceType()).put("status",unit.status()).put("observedAt",kst(unit.observedAt()));
            fact.set("missingFields",SchemaContracts.JSON.valueToTree(unit.missingFields()));
            if(unit.sourceType().equals("DOCUMENT"))fact.put("header",unit.header());
            // 코드 검색 조각에는 소유 클래스 선언이 빠지는 일이 많아 파일 경로가 없으면 주석·역할의 주인을 알 수 없습니다.
            if(unit.sourceType().equals("GIT")){fact.put("sourceRepo",unit.location().path("sourceRepo").asString());fact.put("path",unit.location().path("path").asString());}
            fact.put("text",kstText(unit.body()));
        }
        history.ifPresent(pair->user.set("history",SchemaContracts.JSON.valueToTree(List.of(pair))));
        return EvidenceJson.write(user);
    }
    public static List<ChatMessage> messages(String owner,String data) {
        return List.of(new ChatMessage(ChatMessage.Role.SYSTEM,PROMPT+"\nboundSystem: "+owner),new ChatMessage(ChatMessage.Role.USER,data));
    }
    public static StructuredOutput output(int facts) {
        if(facts<0)throw new IllegalArgumentException("근거 개수가 잘못되었습니다");
        var ids=new ArrayList<String>();for(int i=1;i<=facts;i++)ids.add("e"+i);
        var identifier=new LinkedHashMap<String,Object>();identifier.put("type","string");if(facts>0)identifier.put("enum",ids);
        var text=Map.of("type","string","minLength",1,"maxLength",1000,"pattern","^[^\\r\\n\\u0085\\u2028\\u2029]+(?![\\s\\S])");
        var item=Map.of("type","object","properties",Map.of("text",text,"factIds",Map.of("type","array","minItems",1,"maxItems",6,"items",identifier)),"required",List.of("text","factIds"),"additionalProperties",false);
        var schema=Map.of("type","object","properties",Map.of("sentences",Map.of("type","array","maxItems",facts==0?0:20,"items",item)),"required",List.of("sentences"),"additionalProperties",false);
        return new StructuredOutput(SchemaContracts.JSON.writeValueAsString(schema));
    }
    private static String loadPrompt() {
        try(var input=GroundedFacts.class.getClassLoader().getResourceAsStream(PROMPT_PATH)) {
            if(input==null)throw new IllegalStateException("근거 작성 지시문이 없습니다");
            String value=new String(input.readAllBytes(),StandardCharsets.UTF_8);
            if(value.isBlank()||value.indexOf('\r')>=0)throw new IllegalStateException("근거 작성 지시문 형식이 잘못되었습니다");
            return value;
        }catch(java.io.IOException failure){throw new IllegalStateException("근거 작성 지시문을 읽을 수 없습니다",failure);}
    }
}

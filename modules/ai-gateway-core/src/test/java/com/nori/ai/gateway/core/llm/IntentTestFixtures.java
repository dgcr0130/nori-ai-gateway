package com.nori.ai.gateway.core.llm;

import com.nori.ai.gateway.core.registry.*;
import java.util.*;
import static com.nori.ai.gateway.core.llm.LlmTestSupport.*;

/** Green에서 필요한 선언 주입 API입니다. 이 fixture는 계획을 계산하지 않습니다. */
public final class IntentTestFixtures {
    private IntentTestFixtures() {}
    public static final List<ClassificationIntent> INTENTS=List.of(
        intent("registration","설비 등록 정보",List.of("tc.eqp.readRegistration"),true,"NONE",TaskType.LIVE_QUERY,false),
        intent("status","설비 상태",List.of("tc.eqp.getOperationalSnapshot"),true,"NONE",TaskType.LIVE_QUERY,false),
        intent("model","모델",List.of("tc.model.read"),true,"NONE",TaskType.LIVE_QUERY,false),
        intent("dlq","DLQ",List.of("tc.dlq.list"),true,"OPTIONAL",TaskType.LIVE_QUERY,false),
        intent("communication","통신 로그",List.of("tc.commLogs.search"),true,"REQUIRED",TaskType.LIVE_QUERY,false),
        intent("incident","장애 조사",List.of("logs.search","trace.search"),false,"REQUIRED",TaskType.INCIDENT_ANALYSIS,false),
        intent("document","문서",List.of(),false,"NONE",TaskType.KNOWLEDGE_QUERY,true),
        intent("help","도움말",List.of(),false,"NONE",TaskType.GENERAL_QUERY,false),
        intent("unsupported","지원 안 함",List.of(),false,"NONE",TaskType.UNSUPPORTED_REQUEST,false));
    static ClassificationIntent intent(String id,String description,List<String> tools,boolean subject,String period,TaskType task,boolean documents){
        return new ClassificationIntent(id,description,tools,subject,ClassificationIntent.PeriodRequirement.valueOf(period),task,documents);
    }
    public static CapabilityRegistry registry(){
        var manifests=new ArrayList<CapabilityManifest>();
        for(String id:List.of("tc.eqp.readRegistration","tc.eqp.getOperationalSnapshot","tc.model.read","tc.dlq.list","tc.commLogs.search","logs.search","trace.search")){
            var props=new LinkedHashMap<String,Object>();var required=new ArrayList<String>();
            if(id.startsWith("tc.")){props.put("eqp",Map.of("type","string","minLength",1,"maxLength",64));required.add("eqp");}
            if(Set.of("tc.commLogs.search","tc.dlq.list","logs.search","trace.search").contains(id)){
                for(String bound:List.of("from","to"))props.put(bound,Map.of("type","string","format","date-time","maxLength",35));
                if(!id.equals("tc.dlq.list"))required.addAll(List.of("from","to"));
            }
            manifests.add(ConfiguredTool.create(SCOPE.boundSystem(),id,id,id.equals("logs.search")?ToolKind.LOG:id.equals("trace.search")?ToolKind.TRACE:ToolKind.BUSINESS,
                SchemaContracts.JSON.valueToTree(Map.of("type","object","additionalProperties",false,"properties",props,"required",required)),SchemaContracts.parse("{\"type\":\"object\"}"),List.of("EQP_READ"),10000));
        }
        return new CapabilityRegistry(manifests,Set.of(ImplementationPhase.PHASE_1),new ContractVersionPolicy());
    }
    public static Map<String,Object> need(String intent,String subject){var value=new LinkedHashMap<String,Object>();value.put("intent",intent);value.put("subject",subject);return value;}
    public static String wire(String intent,String subject){return SchemaContracts.JSON.writeValueAsString(Map.of("needs",List.of(need(intent,subject))));}
    static ClassificationService.Context context(RegistryView view,List<ClassificationService.HistoryPair> history,int retained){
        return new ClassificationService.Context(view,history,Map.of("DLQ","Dead Letter Queue"),retained,List.of(),INTENTS);
    }
}

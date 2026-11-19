package com.nori.ai.gateway.app.job;
import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.llm.ClassificationService;
import com.nori.ai.gateway.core.registry.CapabilityRegistry;
import com.nori.ai.gateway.persistence.repository.MessageRepository;
import com.nori.ai.gateway.app.web.ResultResponseAssembler;
import java.util.*;
/** 저장된 사용자 대화와 현재 결과 열람 경계를 재사용합니다. 요청자가 history를 공급하지 못합니다. */
public final class ClassificationInputs implements ClassificationService.ContextSource {
    private final com.nori.ai.gateway.core.feedback.ReviewedGlossary glossary;
    private final GatewaySystemsProperties systems;private final CapabilityRegistry registry;private final MessageRepository messages;private final com.nori.ai.gateway.persistence.repository.JobRepository jobs;private final ResultResponseAssembler access;private final int limit;
    public ClassificationInputs(GatewaySystemsProperties systems,CapabilityRegistry registry,MessageRepository messages,ResultResponseAssembler access,int limit,com.nori.ai.gateway.persistence.repository.JobRepository jobs){this(systems,registry,messages,access,limit,jobs,(scope,initial)->initial);}
    public ClassificationInputs(GatewaySystemsProperties systems,CapabilityRegistry registry,MessageRepository messages,ResultResponseAssembler access,int limit,com.nori.ai.gateway.persistence.repository.JobRepository jobs,com.nori.ai.gateway.core.feedback.ReviewedGlossary glossary){if(limit<0||limit>4)throw new IllegalArgumentException("분류 대화 쌍 설정이 잘못되었습니다");this.jobs=jobs;this.systems=systems;this.registry=registry;this.messages=messages;this.access=access;this.limit=limit;this.glossary=glossary;}
    @Override public ClassificationService.Context load(ClassificationService.Request request){
        var system=systems.systems().get(request.scope().boundSystem().name());if(system==null)throw new IllegalArgumentException("비활성 시스템입니다");
        var pairs=new ArrayList<ClassificationService.HistoryPair>();
        if(limit>0){
            var history=messages.findByConversation(request.scope(),request.conversationId());var questions=new HashMap<String,MessageRepository.Message>();
            history.stream().filter(message->"user".equals(message.role())).forEach(message->questions.put(message.id(),message));
            var ordered=new TreeMap<Long,ClassificationService.HistoryPair>();
            for(var message:history){
                if(!"assistant".equals(message.role())||message.responseRef()==null||message.responseRef().equals(request.jobId()))continue;
                final com.nori.ai.gateway.app.web.ResultResponseAssembler.Access value;
                try{value=access.assemble(request.scope(),message.responseRef());}catch(com.nori.ai.gateway.app.web.UniformNotFound removed){continue;}
                if(!value.allowed()||value.job().terminalAt()==null||value.job().seq()>=request.input().jobSeq()||!value.job().conversationId().equals(request.conversationId()))continue;
                var input=jobs.findRequestInput(request.scope(),message.responseRef());if(input.isEmpty())continue;
                var question=questions.get(input.get().messageId());if(question==null||question.id().equals(request.input().messageId()))continue;
                ordered.put(value.job().seq(),new ClassificationService.HistoryPair(question.contentMasked(),message.contentMasked()));
            }
            pairs.addAll(ordered.values());
        }
        while(pairs.size()>limit)pairs.removeFirst();return new ClassificationService.Context(registry.view(request.scope()),pairs,glossary.current(request.scope(),system.glossary()),limit);
    }
}

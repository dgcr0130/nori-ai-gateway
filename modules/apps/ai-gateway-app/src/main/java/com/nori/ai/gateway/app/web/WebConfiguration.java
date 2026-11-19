package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.core.result.*;
import com.nori.ai.gateway.persistence.repository.*;
import com.nori.ai.gateway.persistence.result.JobAuditReader;
import java.time.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods=false)
@Import({QueryController.class,ConversationController.class,AnalysisController.class,JobEventStreamController.class,
    EvidenceSourceController.class,BlockExportController.class,AuditQueryController.class,ApiExceptionHandler.class,
    FeedbackController.class,ReviewController.class,FeedbackExceptionHandler.class})
public class WebConfiguration {
    @Bean ConversationCursor conversationCursor() {return new ConversationCursor();}
    @Bean ReviewCursor reviewCursor(){return new ReviewCursor();}
    @Bean ResultResponseAssembler resultResponseAssembler(JobRepository jobs,ConversationRepository conversations,ResultRepository results,ResultAccessEvaluator evaluator,KnowledgeDocumentStateReader documents) {return new ResultResponseAssembler(jobs,conversations,results,evaluator,documents);}
    @Bean JobAuditReader jobAuditReader(JdbcClient jdbc,Clock clock) {return new JobAuditReader(jdbc,clock);}
    @Bean EventReplayService eventReplayService(JobEventRepository events) {return new EventReplayService(events);}
    @Bean SseHub sseHub(EventReplayService replay,Environment env) {return new SseHub(replay,env.getProperty("ai.gateway.sse.heartbeat",Duration.class,Duration.ofSeconds(15)));}
}

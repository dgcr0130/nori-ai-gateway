package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.core.feedback.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import java.util.Map;

@RestController
public final class FeedbackController {
    private final FeedbackService feedback;
    private final ResultResponseAssembler results;
    public FeedbackController(FeedbackService feedback,ResultResponseAssembler results){this.feedback=feedback;this.results=results;}
    @PutMapping("/v1/answers/{answerId}/feedback") public Map<String,Object> evaluate(HttpServletRequest request,@PathVariable String answerId,@RequestBody JsonNode body) {
        var scope=ApiRequestInput.scope(request);
        try{if(!results.assemble(scope,answerId).allowed())throw FeedbackFailure.missing();}catch(UniformNotFound missing){com.nori.ai.gateway.core.logging.DiagnosticLog.failure(System.getLogger(FeedbackController.class.getName()),"FEEDBACK_CONTROLLER_FAILED",missing);throw FeedbackFailure.missing();}
        var result=response(feedback.evaluate(scope,answerId,input(body)));
        com.nori.ai.gateway.core.logging.DiagnosticLog.body(System.getLogger(getClass().getName()),System.Logger.Level.INFO,"FEEDBACK_EVALUATED",com.nori.ai.gateway.core.registry.SchemaContracts.JSON.writeValueAsString(Map.of("input",input(body),"result",result)),"answerId",answerId);return result;
    }
    static JsonNode input(JsonNode body){var value=body.deepCopy();if(value.isObject())((tools.jackson.databind.node.ObjectNode)value).remove("contextRef");return value;}
    static Map<String,Object> response(FeedbackStore.Feedback value){return Map.of("feedbackId",value.feedbackId(),"version",value.version(),"state",value.state());}
}

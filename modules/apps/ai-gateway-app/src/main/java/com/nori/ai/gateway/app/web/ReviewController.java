package com.nori.ai.gateway.app.web;

import com.nori.ai.gateway.app.config.GatewaySystemsProperties;
import com.nori.ai.gateway.core.feedback.*;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import java.util.*;

@RestController
public final class ReviewController {
    private final FeedbackStore store;private final FeedbackService feedback;private final GatewaySystemsProperties systems;private final ReviewCursor cursor;
    public ReviewController(FeedbackStore store,FeedbackService feedback,GatewaySystemsProperties systems,ReviewCursor cursor){this.store=store;this.feedback=feedback;this.systems=systems;this.cursor=cursor;}
    @GetMapping("/v1/reviews") public Map<String,Object> list(HttpServletRequest request,@RequestParam(defaultValue="OPEN") String state,@RequestParam(defaultValue="") String after,@RequestParam(defaultValue="20") int limit) {
        var scope=authorized(request);if(!Set.of("OPEN","RESOLVED","REJECTED").contains(state)||limit<1||limit>100)throw FeedbackFailure.invalid();
        var position=cursor.decode(scope,state,after);var found=store.list(scope,state,position==null?null:position.time(),position==null?null:position.id(),limit+1);
        var items=found.subList(0,Math.min(limit,found.size()));var response=new LinkedHashMap<String,Object>();response.put("items",items);
        response.put("nextAfter",found.size()>limit?cursor.encode(scope,state,items.getLast().createdAt(),items.getLast().feedbackId()):null);return response;
    }
    @GetMapping("/v1/reviews/{feedbackId}") public FeedbackStore.Detail detail(HttpServletRequest request,@PathVariable String feedbackId){return store.detail(authorized(request),id(feedbackId));}
    @PostMapping("/v1/reviews/{feedbackId}/resolve") public Map<String,Object> resolve(HttpServletRequest request,@PathVariable String feedbackId,@RequestBody JsonNode body) {
        var scope=authorized(request);return FeedbackController.response(feedback.resolve(scope,id(feedbackId),FeedbackController.input(body),systems.systems().get(scope.boundSystem().name()).glossary()));
    }
    private VerifiedSystemScope authorized(HttpServletRequest request){var scope=ApiRequestInput.scope(request);var config=systems.systems().get(scope.boundSystem().name());if(config==null||!scope.perm().contains(config.reviewPermission()))throw new FeedbackFailure(403,"REVIEW_FORBIDDEN");return scope;}
    private static UUID id(String value){try{return UUID.fromString(value);}catch(IllegalArgumentException invalid){throw FeedbackFailure.missing();}}
}

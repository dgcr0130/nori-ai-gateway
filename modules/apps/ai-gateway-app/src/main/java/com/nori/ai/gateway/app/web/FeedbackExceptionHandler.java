package com.nori.ai.gateway.app.web;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.core.feedback.FeedbackFailure;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/** 기존 공개 API의 오류 외피는 유지하고 새 네 경로에만 4.8 외피를 적용합니다. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes={FeedbackController.class,ReviewController.class})
public final class FeedbackExceptionHandler {
    @ExceptionHandler(FeedbackFailure.class) ResponseEntity<?> feedback(FeedbackFailure failure,HttpServletRequest request){failure(System.getLogger(getClass().getName()),failure.status()>=500?ERROR:WARNING,"FEEDBACK_REJECTED",failure);return error(failure.status(),failure.code(),request);}
    @ExceptionHandler({IllegalArgumentException.class,org.springframework.http.converter.HttpMessageNotReadableException.class,org.springframework.web.bind.ServletRequestBindingException.class,org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    ResponseEntity<?> invalid(Exception failure,HttpServletRequest request){failure(System.getLogger(getClass().getName()),WARNING,"FEEDBACK_INVALID",failure);return error(400,"INVALID_INPUT",request);}
    @ExceptionHandler({IllegalStateException.class,org.springframework.dao.DataAccessException.class,org.springframework.transaction.TransactionException.class,com.nori.ai.gateway.core.port.InferenceClient.EmbeddingFailed.class})
    ResponseEntity<?> unavailable(Exception failure,HttpServletRequest request){failure(System.getLogger(getClass().getName()),ERROR,"FEEDBACK_UNAVAILABLE",failure);return error(503,"KNOWLEDGE_UNAVAILABLE",request);}
    private ResponseEntity<?> error(int status,String code,HttpServletRequest request){var response=ResponseEntity.status(status);if(status==503)response.header("Retry-After","1");return response.body(Map.of("code",code,"message","피드백 요청을 처리할 수 없습니다","requestId",ApiRequestInput.scope(request).requestId()));}
}

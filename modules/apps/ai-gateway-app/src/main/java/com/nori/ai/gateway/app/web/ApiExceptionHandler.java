package com.nori.ai.gateway.app.web;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.app.job.JobAdmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice(basePackages="com.nori.ai.gateway.app.web")
public final class ApiExceptionHandler {
    @ExceptionHandler(UniformNotFound.class)
    ResponseEntity<Void> notFound(UniformNotFound failure) { failure(System.getLogger(getClass().getName()),"API_NOT_FOUND",failure); return ResponseEntity.notFound().build(); }
    @ExceptionHandler(JobAdmissionService.Rejected.class)
    ResponseEntity<?> rejected(JobAdmissionService.Rejected failure) {
        failure(System.getLogger(getClass().getName()),"API_ADMISSION_REJECTED",failure,"status",failure.status(),"errorCode",failure.error());
        var response=ResponseEntity.status(failure.status());
        if(failure.retryAfter()>0)response.header("Retry-After",Integer.toString(failure.retryAfter()));
        return failure.error()==null?response.build():response.body(Map.of("error",failure.error()));
    }
    @ExceptionHandler({IllegalArgumentException.class,java.time.DateTimeException.class})
    ResponseEntity<?> invalid(Exception failure) {failure(System.getLogger(getClass().getName()),WARNING,"API_INVALID",failure); return ResponseEntity.badRequest().body(Map.of("message","요청 입력을 확인해 주세요")); }
    @ExceptionHandler({IllegalStateException.class,org.springframework.dao.DataAccessException.class})
    ResponseEntity<?> failed(Exception failure) {failure(System.getLogger(getClass().getName()),ERROR,"API_FAILED",failure); return ResponseEntity.internalServerError().body(Map.of("message","요청을 처리할 수 없습니다")); }
}

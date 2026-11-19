package com.nori.ai.gateway.core.feedback;

/** 원문을 예외 메시지에 싣지 않는 피드백 API의 실패 계약입니다. */
public final class FeedbackFailure extends RuntimeException {
    private final int status;
    private final String code;
    public FeedbackFailure(int status,String code) { super("피드백 요청을 처리할 수 없습니다");this.status=status;this.code=code; }
    public int status(){return status;}
    public String code(){return code;}
    public static FeedbackFailure invalid(){return new FeedbackFailure(400,"INVALID_INPUT");}
    public static FeedbackFailure missing(){return new FeedbackFailure(404,"NOT_FOUND");}
    public static FeedbackFailure conflict(){return new FeedbackFailure(409,"REVIEW_CONFLICT");}
}

package com.nori.ai.gateway.core.authz;
/** 서명 검증 후 Job에 결속한 불투명 위임 식별자입니다. */
public record DelegatedCall(String sub,String dlg) {
    public DelegatedCall { if(sub==null||sub.isBlank()||dlg==null||!dlg.matches("[A-Za-z0-9_-]{22,128}"))throw new IllegalArgumentException("실행 위임 형식이 잘못되었습니다"); }
    @Override public String toString(){return "DelegatedCall[보호됨]";}
}

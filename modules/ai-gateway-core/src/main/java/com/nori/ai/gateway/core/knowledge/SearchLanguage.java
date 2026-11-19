package com.nori.ai.gateway.core.knowledge;

import java.util.List;

/** 형태소 라이브러리를 core 밖에 두고 관측된 품사만 전달합니다. */
public interface SearchLanguage {
    record Term(String text,String tag) {}
    List<Term> analyze(String text);
}

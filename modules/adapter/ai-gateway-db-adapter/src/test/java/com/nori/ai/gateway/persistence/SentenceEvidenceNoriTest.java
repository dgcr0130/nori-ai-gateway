package com.nori.ai.gateway.persistence;

import com.nori.ai.gateway.core.knowledge.SearchLanguage;
import com.nori.ai.gateway.core.llm.SentenceEvidenceCheck;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.workflow.EvidenceUnit;
import com.nori.ai.gateway.persistence.knowledge.MemoryDocumentKeywordIndex;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class SentenceEvidenceNoriTest {
    EvidenceUnit unit(String body,String header){return new EvidenceUnit("DOCUMENT","parent",SchemaContracts.JSON.createObjectNode(),header,body,List.of("READ"),"SUCCESS",null,List.of(),SchemaContracts.JSON.createObjectNode(),List.of(),List.of());}
    String output(String text,int number){return SchemaContracts.JSON.writeValueAsString(Map.of("sentences",List.of(Map.of("text",text,"citations",List.of(number)))));}
    @Test void actualPinnedNoriChecksOnlyCitedNounsAndNamesAndNeverBorrowsOtherBodies() {
        try(var language=new MemoryDocumentKeywordIndex()) {
            assertThat(language.analyze("10개입니다")).containsExactly(new SearchLanguage.Term("10","SN"),new SearchLanguage.Term("개","NNBC"),new SearchLanguage.Term("이","VCP"),new SearchLanguage.Term("ᄇ니다","E"));
            var check=new SentenceEvidenceCheck(language);var units=List.of(unit("설비 상태 T3 S1F2 EQP-001 linger.ms 1.5ms -2 20% 10개","서울 Alpha"),unit("부산 Beta 77 T30",""));
            for(String valid:List.of("설비 상태 T3 S1F2 EQP-001 linger.ms 1.5ms -2 20% 10개","서울 Alpha","설비 10개입니다.","근거 확인 결과 정보 조회 문서 내용 값"))assertThat(check.check(output(valid,1),units).kind()).as(valid).isEqualTo("ANSWER");
            for(String invalid:List.of("부산","Beta","77","T30","EQP-00","15ms","0.0015s","10초"))assertThat(check.check(output(invalid,1),units).kind()).as(invalid).isEqualTo("NO_EVIDENCE");
            assertThat(language.analyze("서울 설비 상태")).containsExactly(new SearchLanguage.Term("서울","NNP"),new SearchLanguage.Term("설비","NNG"),new SearchLanguage.Term("상태","NNG"));
        }
    }
}

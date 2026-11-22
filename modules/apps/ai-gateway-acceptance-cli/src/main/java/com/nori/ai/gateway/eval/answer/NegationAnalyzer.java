package com.nori.ai.gateway.eval.answer;

import com.nori.ai.gateway.eval.EvalException;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.apache.lucene.analysis.ko.dict.UserDictionary;
import org.apache.lucene.analysis.ko.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeFactory;
import java.io.*;
import java.text.Normalizer;
import java.util.*;

/** 부정어를 제거하는 검색용 필터를 쓰지 않고 INFLECT 구성 형태소를 보존합니다. */
public final class NegationAnalyzer {
    public record Morpheme(String text,String tag) {}
    public record Token(String surface,String tag,int start,int end,String type,List<Morpheme> morphemes) {}
    private static final Set<Morpheme> NEGATIVE=Set.of(new Morpheme("아니","VCN"),new Morpheme("않","VX"),
        new Morpheme("안","MAG"),new Morpheme("못","MAG"),new Morpheme("못하","VX"),new Morpheme("없","VA"),new Morpheme("말","VX"));
    public static String normalize(String text) {
        return Normalizer.normalize(text,Normalizer.Form.NFC).replaceAll("(?U)\\s+"," ").strip();
    }
    public List<Token> analyze(String text) {
        var result=new ArrayList<Token>();
        try(var tokenizer=new KoreanTokenizer(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,(UserDictionary)null,KoreanTokenizer.DecompoundMode.NONE,false)) {
            tokenizer.setReader(new StringReader(text));
            var term=tokenizer.addAttribute(CharTermAttribute.class);var pos=tokenizer.addAttribute(PartOfSpeechAttribute.class);
            var offset=tokenizer.addAttribute(OffsetAttribute.class);
            tokenizer.reset();while(tokenizer.incrementToken()) {
                var parts=new ArrayList<Morpheme>();var morphemes=pos.getMorphemes();
                if(morphemes!=null)for(var part:morphemes)parts.add(new Morpheme(part.surfaceForm,part.posTag.name()));
                result.add(new Token(term.toString(),pos.getLeftPOS().name(),offset.startOffset(),offset.endOffset(),pos.getPOSType().name(),List.copyOf(parts)));
            }tokenizer.end();
        }catch(IOException failure){throw new EvalException(2,"형태소 분석에 실패했습니다");}
        return List.copyOf(result);
    }
    public boolean hasOutsideNegation(String sentence,String fact) {
        String text=normalize(sentence),expected=normalize(fact);var ranges=new ArrayList<int[]>();
        if(expected.isEmpty())throw QuestionSet.invalid();
        for(int from=0;from<=text.length()&&(from=text.indexOf(expected,from))>=0;from++)ranges.add(new int[]{from,from+expected.length()});
        for(var token:analyze(text)) {
            if(ranges.stream().anyMatch(r->token.start()<r[1]&&token.end()>r[0]))continue;
            var parts=token.type().equals("INFLECT")&&!token.morphemes().isEmpty()?token.morphemes():List.of(new Morpheme(token.surface(),token.tag()));
            if(parts.stream().anyMatch(NEGATIVE::contains))return true;
        }
        return false;
    }
}

package com.nori.ai.gateway.core.knowledge;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public final class SearchText {
    private SearchText() {}
    private static final Pattern TOKEN=Pattern.compile("[a-z0-9_.-]+");
    public static String normalize(String text) {
        String nfc=Normalizer.normalize(text,Normalizer.Form.NFC);
        var result=new StringBuilder();
        nfc.codePoints().forEach(c->result.appendCodePoint(c>='A'&&c<='Z'?c+32:c));
        return result.toString().replaceAll("(?U)\\s+"," ").strip();
    }
    public static List<String> identifiers(String text, List<String> targets) {
        var values=new LinkedHashSet<String>();var matcher=TOKEN.matcher(normalize(text));
        while(matcher.find())if(matcher.group().chars().anyMatch(Character::isDigit))values.add(matcher.group());
        for(String target:targets)values.add(normalize(target));
        return List.copyOf(values);
    }
    public static boolean containsExact(String body,String term) {
        return Pattern.compile("(?<![a-z0-9_.-])"+Pattern.quote(normalize(term))+"(?![a-z0-9_.-])")
            .matcher(normalize(body)).find();
    }
}

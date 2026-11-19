package com.nori.ai.gateway.integration.storage;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import org.commonmark.node.*;
import org.commonmark.parser.*;
import org.commonmark.ext.gfm.tables.*;
import java.nio.*;
import java.nio.charset.*;
import java.security.*;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import static com.nori.ai.gateway.core.knowledge.DocumentCorpus.*;

/** 원문 위치·길이는 마스킹 전에 판정하고, 반환 직전에 모든 검색 본문을 마스킹합니다. */
public final class MarkdownDocumentExtractor implements DocumentExtractor {
    private final Parser parser = Parser.builder().extensions(List.of(TablesExtension.create()))
        .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES).build();
    private final ContentSanitizer sanitizer;
    public MarkdownDocumentExtractor(ContentSanitizer sanitizer) { this.sanitizer = sanitizer; }
    private static final Set<Class<?>> ALLOWED = Set.of(org.commonmark.node.Document.class, Heading.class, Paragraph.class,
        BulletList.class, OrderedList.class, ListItem.class, FencedCodeBlock.class, Text.class, Code.class, Emphasis.class,
        StrongEmphasis.class, Link.class, SoftLineBreak.class, HardLineBreak.class,
        TableBlock.class, TableHead.class, TableBody.class, TableRow.class, TableCell.class);
    private static final Pattern NUMBER = Pattern.compile("^[ \\t]*([0-9]+)[.)](?:[ \\t]|$)");
    @Override public DocumentCorpus.Document extract(Source source, String key, DocumentObjectStore.Content content, Limits limits) {
        String title = "";
        try {
            if (!key.endsWith(".md")) throw violation(1, "UNSUPPORTED_FORMAT");
            if (content.oversized() || content.bytes().length > limits.maxFileBytes()) throw violation(1, "FILE_TOO_LARGE");
            final String raw;
            try { raw = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(content.bytes())).toString(); }
            catch (CharacterCodingException invalid) { throw violation(1, "INVALID_UTF_EIGHT"); }
            if (raw.startsWith("\ufeff")) throw violation(1, "BOM");
            String[] lines = raw.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].indexOf('\r') >= 0) throw violation(i+1, "CR");
                if (lines[i].indexOf('\0') >= 0) throw violation(i+1, "NUL");
                if (!Normalizer.isNormalized(lines[i], Normalizer.Form.NFC)) throw violation(i+1, "NON_NFC");
            }
            Node root = parser.parse(raw); validateNodes(root);
            Node first = root.getFirstChild();
            if (!(first instanceof Heading h) || h.getLevel() != 1) throw violation(first == null ? 1 : from(first), "FIRST_H_ONE");
            title = inline(first); length(title, limits.headingMaxChars(), from(first), "HEADING_LENGTH");
            var parents = new ArrayList<Parent>(); var sections = new HashSet<String>();
            Node node = first.getNext(); if (meta(node)) node = node.getNext();
            while (node != null) {
                if (node instanceof Heading h1 && h1.getLevel()==1) throw violation(from(node), "MULTIPLE_H_ONE");
                if (!(node instanceof Heading h2) || h2.getLevel() != 2) throw violation(from(node), "SECTION_STRUCTURE");
                String section = inline(node); length(section, limits.headingMaxChars(), from(node), "HEADING_LENGTH");
                if (!sections.add(section)) throw violation(from(node), "DUPLICATE_SECTION");
                int parentFrom = from(node), parentTo = to(node);
                node = node.getNext(); if (meta(node)) { parentTo = to(node); node = node.getNext(); }
                var parts = new ArrayList<Part>();
                while (node != null && !(node instanceof Heading)) {
                    if (meta(node)) throw violation(from(node), "META_POSITION");
                    parts.addAll(parts(node, lines)); parentTo = to(node); node = node.getNext();
                }
                if (node instanceof Heading heading && heading.getLevel() == 1) throw violation(from(node), "MULTIPLE_H_ONE");
                var children = new ArrayList<Child>();
                String header = mask(title + " > " + section);
                for (Part part : parts) {
                    length(part.body(), limits.childMaxChars(), part.from(), "CHILD_LENGTH");
                    children.add(new Child(UUID.randomUUID(), children.size(), location(source,key,content.revision(),mask(section),part.from(),part.to()), header, mask(part.body()), List.of(), new float[0]));
                }
                String body = String.join("\n", parts.stream().map(Part::body).toList());
                length(body, limits.parentMaxChars(), parentFrom, "PARENT_LENGTH");
                String masked = mask(body);
                parents.add(new Parent(UUID.randomUUID(), location(source,key,content.revision(),mask(section),parentFrom,parentTo), header, masked, sha(masked.getBytes(StandardCharsets.UTF_8)), children));
            }
            if (parents.isEmpty()) throw violation(1, "NO_SECTION");
            return new DocumentCorpus.Document(UUID.randomUUID(), source.bucket(), key, content.revision(), mask(title), source.permissions(), List.of(), parents);
        } catch (Violation invalid) {
            return new DocumentCorpus.Document(UUID.randomUUID(), source.bucket(), key, content.revision(), mask(title), source.permissions(), List.of(new Issue(invalid.line, invalid.reason)), List.of());
        }
    }
    private void validateNodes(Node node) {
        if (!ALLOWED.contains(node.getClass())) throw violation(from(node), "FORBIDDEN_NODE");
        if (node instanceof Heading h && (h.getLevel() > 2 || !(node.getParent() instanceof org.commonmark.node.Document))) throw violation(from(node), "FORBIDDEN_HEADING");
        if (meta(node) && !(node.getParent() instanceof org.commonmark.node.Document)) throw violation(from(node), "META_POSITION");
        for (Node child = node.getFirstChild(); child != null; child = child.getNext()) validateNodes(child);
    }
    private record Part(String body, int from, int to) {}
    private List<Part> parts(Node node, String[] lines) {
        if (node instanceof Paragraph) return List.of(new Part(inline(node), from(node), to(node)));
        if (node instanceof FencedCodeBlock code) {
            String body = code.getLiteral(); if (body.endsWith("\n")) body = body.substring(0,body.length()-1);
            return List.of(new Part(body, from(node), to(node)));
        }
        if (node instanceof BulletList || node instanceof OrderedList) {
            var values = new ArrayList<Part>();
            for (Node item = node.getFirstChild(); item != null; item = item.getNext()) {
                String prefix = "- ";
                if (node instanceof OrderedList) {
                    // 같은 줄의 바깥 목록 표지가 아니라 이 항목 자신의 표지에서 번호를 읽습니다.
                    var start = item.getSourceSpans().stream().min(Comparator.comparingInt(SourceSpan::getLineIndex).thenComparingInt(SourceSpan::getColumnIndex))
                        .orElse(null);
                    if (start == null) throw violation(from(item), "LIST_NUMBER");
                    int line = start.getLineIndex(), column = start.getColumnIndex();
                    if (line < 0 || line >= lines.length || column < 0 || column > lines[line].length()) throw violation(from(item), "LIST_NUMBER");
                    var matcher = NUMBER.matcher(lines[line].substring(column));
                    if (!matcher.find()) throw violation(from(item), "LIST_NUMBER"); prefix = matcher.group(1) + ". ";
                }
                var blocks = new ArrayList<String>();
                for (Node child = item.getFirstChild(); child != null; child = child.getNext()) for (Part part : parts(child, lines)) blocks.add(part.body());
                values.add(new Part(prefix + String.join("\n", blocks), from(item), to(item)));
            }
            return values;
        }
        if (node instanceof TableBlock) {
            var rows = new ArrayList<Node>(); collectRows(node, rows);
            if (rows.size() < 2) throw violation(from(node), "TABLE_ROWS");
            var headers = cells(rows.getFirst());
            if (headers.stream().anyMatch(String::isBlank) || new HashSet<>(headers).size() != headers.size()) throw violation(from(rows.getFirst()), "TABLE_HEADER");
            var values = new ArrayList<Part>();
            for (Node row : rows.subList(1,rows.size())) {
                var cells = cells(row);
                if (rawCellCount(lines[from(row)-1]) != headers.size() || cells.size() != headers.size()) throw violation(from(row), "TABLE_COLUMNS");
                if (cells.stream().anyMatch(String::isBlank)) throw violation(from(row), "TABLE_EMPTY_CELL");
                var pairs = new ArrayList<String>(); for (int i=0;i<headers.size();i++) pairs.add(headers.get(i)+"="+cells.get(i));
                values.add(new Part(String.join(" | ",pairs),from(row),to(row)));
            }
            return values;
        }
        throw violation(from(node), "SECTION_STRUCTURE");
    }
    // GFM은 코드 안의 파이프도 칸 경계로 취급합니다. 역슬래시로 이스케이프한 파이프만 제외합니다.
    static int rawCellCount(String line) {
        String text = line.strip(); var delimiters = new ArrayList<Integer>();
        for (int i=0;i<text.length();i++) if (text.charAt(i)=='|') {
            int slashes=0; for (int j=i-1;j>=0 && text.charAt(j)=='\\';j--) slashes++;
            if (slashes%2==0) delimiters.add(i);
        }
        int count=delimiters.size()+1;
        if (!delimiters.isEmpty() && delimiters.getFirst()==0) count--;
        if (!delimiters.isEmpty() && delimiters.getLast()==text.length()-1) count--;
        return count;
    }
    private static void collectRows(Node node, List<Node> rows) {
        if (node instanceof TableRow) rows.add(node);
        else for (Node child=node.getFirstChild();child!=null;child=child.getNext()) collectRows(child,rows);
    }
    private static List<String> cells(Node row) {
        var values=new ArrayList<String>(); for(Node cell=row.getFirstChild();cell!=null;cell=cell.getNext()) values.add(inline(cell)); return values;
    }
    private static String inline(Node node) {
        if (node instanceof Text text) return text.getLiteral();
        if (node instanceof Code code) return code.getLiteral();
        if (node instanceof SoftLineBreak) return " ";
        if (node instanceof HardLineBreak) return "\n";
        var result=new StringBuilder(); for(Node child=node.getFirstChild();child!=null;child=child.getNext()) result.append(inline(child)); return result.toString();
    }
    private static boolean meta(Node node) { return node instanceof FencedCodeBlock block && block.getInfo().equals("rag-meta"); }
    private String mask(String value) { return sanitizer.sanitize(value).text(); }
    private static DocumentLocation location(Source source,String key,String revision,String section,int from,int to) { return new DocumentLocation(source.bucket(),key,revision,section,from,to); }
    private static int from(Node node) { return node.getSourceSpans().stream().mapToInt(span->span.getLineIndex()+1).min().orElse(1); }
    private static int to(Node node) { return node.getSourceSpans().stream().mapToInt(span->span.getLineIndex()+1).max().orElse(from(node)); }
    private static void length(String text,int max,int line,String reason) { int size=text.codePointCount(0,text.length()); if(size<1||size>max) throw violation(line,reason); }
    static String sha(byte[] bytes) { try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); } catch(NoSuchAlgorithmException impossible) { throw new IllegalStateException("해시를 계산할 수 없습니다"); } }
    private static Violation violation(int line,String reason) { return new Violation(line,reason); }
    private static final class Violation extends RuntimeException {
        final int line; final String reason;
        Violation(int line,String reason) { super(reason,null,false,false);this.line=line;this.reason=reason; }
    }
}

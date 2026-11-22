package com.nori.ai.gateway.eval.answer;

import com.nori.ai.gateway.eval.EvalException;
import com.nori.ai.gateway.eval.io.Json;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** 원격 문서 API를 만들지 않고 허가된 로컬 원본만 읽습니다. */
public final class LocalDocuments implements QuestionSet.Documents {
    private final Path root;private final Map<String,EvalMarkdown.Document> read=new TreeMap<>();
    public LocalDocuments(Path root){this.root=root.toAbsolutePath().normalize();}
    @Override public EvalMarkdown.Document read(String bucket,String objectKey) {
        String id=bucket+"\n"+objectKey;
        if(read.containsKey(id))return read.get(id);
        if(bucket.isBlank()||bucket.equals(".")||bucket.equals("..")||bucket.contains("/")||bucket.contains("\\")||bucket.contains(":")
                ||objectKey.contains("\\")||objectKey.startsWith("/")||objectKey.contains(":"))throw QuestionSet.invalid();
        var bucketRoot=root.resolve(bucket);var path=bucketRoot.resolve(objectKey).normalize();
        try {
            if(!path.startsWith(bucketRoot)||!bucketRoot.toRealPath().startsWith(root.toRealPath())
                    ||!path.toRealPath().startsWith(bucketRoot.toRealPath())||!Files.isRegularFile(path)||Files.size(path)>1048576)throw QuestionSet.invalid();
        }catch(IOException invalid){throw new EvalException(2,"평가 원본 문서를 읽을 수 없습니다");}
        var document=new EvalMarkdown().extract(bucket,objectKey,Json.bytes(path));read.put(id,document);return document;
    }
    public List<Map<String,String>> revisions() {
        return read.values().stream().map(d->Map.of("bucket",d.bucket(),"objectKey",d.objectKey(),"revision",d.revision())).toList();
    }
}

package com.nori.ai.gateway.integration.storage;

import com.nori.ai.gateway.core.knowledge.*;
import com.nori.ai.gateway.core.sanitize.ContentSanitizer;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import static org.assertj.core.api.Assertions.*;

class S3DocumentObjectStoreTest {
    @TempDir Path temporary;
    final DocumentCorpus.Source source=new DocumentCorpus.Source("bucket","rag/",Set.of("READ"));
    S3DocumentObjectStore store(HttpServer server)throws Exception {
        var access=temporary.resolve("access");var secret=temporary.resolve("secret");
        Files.writeString(access,"fixture-access");Files.writeString(secret,"fixture-secret");
        return new S3DocumentObjectStore(URI.create("http://127.0.0.1:"+server.getAddress().getPort()),access,secret,List.of(source));
    }
    static byte[] bytes(String value) { return value.getBytes(StandardCharsets.UTF_8); }
    static String page(boolean truncated,String next,String... keys) {
        var xml=new StringBuilder("<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><IsTruncated>"+truncated+"</IsTruncated>");
        if(next!=null)xml.append("<NextContinuationToken>").append(next).append("</NextContinuationToken>");
        for(String key:keys)xml.append("<Contents><Key>").append(key).append("</Key><Size>7</Size></Contents>");
        return xml.append("</ListBucketResult>").toString();
    }
    @Test void actualSdkTraversesPaginationAndUsesBytesInsteadOfEtag() throws Exception {
        var calls=new ArrayList<String>();var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        byte[] raw=bytes("# 제목\n## 절\n본문\n");
        server.createContext("/",exchange->{
            calls.add(exchange.getRequestURI().toString());String query=exchange.getRequestURI().getRawQuery();
            byte[] response=query!=null&&query.contains("list-type")?bytes(query.contains("continuation-token")?page(false,null,"rag/a.md"):page(true,"next","rag/한.md")):raw;
            exchange.getResponseHeaders().add("ETag","not-a-content-hash");exchange.sendResponseHeaders(200,response.length);exchange.getResponseBody().write(response);exchange.close();
        });server.start();
        try(var storage=store(server)) {
            assertThat(storage.list(source)).extracting(DocumentObjectStore.ObjectKey::key).containsExactly("rag/한.md","rag/a.md");
            var content=storage.download(source,new DocumentObjectStore.ObjectKey("rag/a.md",7),1000);
            assertThat(content.bytes()).isEqualTo(raw);assertThat(content.revision()).isEqualTo(MarkdownDocumentExtractor.sha(raw));assertThat(content.oversized()).isFalse();
            assertThat(calls).hasSize(3);assertThat(calls.get(1)).contains("continuation-token=next");
            assertThatThrownBy(()->storage.list(new DocumentCorpus.Source("foreign","rag/",Set.of("READ")))).hasMessage("DOCUMENT_SCOPE_INVALID");assertThat(calls).hasSize(3);
        } finally { server.stop(0); }
    }
    @Test void actualSdkRejectsBrokenPagesAndDownloadWithoutExposingRemoteErrorsOrRetrying() throws Exception {
        var body=new AtomicReference<>(bytes("broken-private-response"));var status=new AtomicInteger(200);var count=new AtomicInteger();
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/",exchange->{count.incrementAndGet();byte[] response=body.get();exchange.sendResponseHeaders(status.get(),response.length);exchange.getResponseBody().write(response);exchange.close();});server.start();
        try(var storage=store(server)) {
            assertThatThrownBy(()->storage.list(source)).hasMessage("DOCUMENT_LIST_FAILED").hasNoCause();assertThat(count.get()).isOne();
            body.set(bytes(page(true,"same","rag/a.md")));count.set(0);
            assertThatThrownBy(()->storage.list(source)).hasMessage("DOCUMENT_LIST_FAILED");assertThat(count.get()).isEqualTo(2);
            body.set(bytes("<Error><Code>AccessDenied</Code><Message>private-secret</Message></Error>"));status.set(403);count.set(0);
            assertThatThrownBy(()->storage.download(source,new DocumentObjectStore.ObjectKey("rag/a.md",0),100)).hasMessage("DOCUMENT_DOWNLOAD_FAILED").hasNoCause();assertThat(count.get()).isOne();
        } finally { server.stop(0); }
    }
    @Test void zeroObjectsCorruptUtf8AndOversizeAreDistinctThroughActualSdk() throws Exception {
        var body=new AtomicReference<>(bytes(page(false,null)));var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/",exchange->{byte[] response=body.get();exchange.sendResponseHeaders(200,response.length);exchange.getResponseBody().write(response);exchange.close();});server.start();
        try(var storage=store(server)) {
            assertThat(storage.list(source)).isEmpty();body.set(new byte[]{(byte)0xff});
            var result=new MarkdownDocumentExtractor(new ContentSanitizer()).extract(source,"rag/a.md",storage.download(source,new DocumentObjectStore.ObjectKey("rag/a.md",1),10),new DocumentCorpus.Limits(10,200,450,60));
            assertThat(result.issues()).singleElement().extracting(DocumentCorpus.Issue::reason).isEqualTo("INVALID_UTF_EIGHT");
            byte[] large=new byte[50000];Arrays.fill(large,(byte)'a');body.set(large);
            var oversized=storage.download(source,new DocumentObjectStore.ObjectKey("rag/a.md",50000),100);
            assertThat(oversized.oversized()).isTrue();assertThat(oversized.bytes()).isEmpty();assertThat(oversized.revision()).isEqualTo(MarkdownDocumentExtractor.sha(large));
        } finally { server.stop(0); }
    }
}

package com.nori.ai.gateway.integration.storage;

import static com.nori.ai.gateway.core.logging.DiagnosticLog.*;
import static java.lang.System.Logger.Level.*;
import com.nori.ai.gateway.core.knowledge.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;

/** 읽기 전용 SDK 호출만 제공합니다. 자격 provider chain·외부 객체 URL·자동 재시도를 사용하지 않습니다. */
public final class S3DocumentObjectStore implements DocumentObjectStore {
    private final S3Client client;
    private final Set<DocumentCorpus.Source> sources;
    public S3DocumentObjectStore(URI endpoint, Path accessKeyFile, Path secretKeyFile, Collection<DocumentCorpus.Source> sources) {
        this.sources = Set.copyOf(sources);
        if (this.sources.isEmpty()) { client=null; return; }
        try {
            if (!Set.of("http","https").contains(endpoint.getScheme()) || endpoint.getHost()==null || endpoint.getRawUserInfo()!=null
                    || endpoint.getRawQuery()!=null || endpoint.getRawFragment()!=null || !Set.of("","/").contains(endpoint.getRawPath())) throw new IllegalArgumentException();
            client = S3Client.builder().endpointOverride(endpoint).region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(credential(accessKeyFile),credential(secretKeyFile))))
                .httpClientBuilder(UrlConnectionHttpClient.builder().connectionTimeout(Duration.ofSeconds(3)).socketTimeout(Duration.ofSeconds(60)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .overrideConfiguration(ClientOverrideConfiguration.builder().retryPolicy(RetryPolicy.none()).apiCallTimeout(Duration.ofSeconds(60)).build()).build();
        } catch (Exception invalid) {com.nori.ai.gateway.core.logging.DiagnosticLog.credentialFailure(System.getLogger(S3DocumentObjectStore.class.getName()),"S3_DOCUMENT_OBJECT_STORE_FAILED",invalid); throw failure("DOCUMENT_CREDENTIALS_INVALID"); }
    }
    private static String credential(Path file) throws IOException {
        byte[] bytes=Files.readAllBytes(file);
        String value=StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString().strip();
        if(value.isEmpty() || value.chars().anyMatch(Character::isISOControl)) throw new IllegalArgumentException(); return value;
    }
    @Override public List<ObjectKey> list(DocumentCorpus.Source source) {
        allowed(source);long logStarted=System.nanoTime();event(System.getLogger(getClass().getName()),INFO,"S3_LIST_REQUEST","bucket",source.bucket(),"prefix",source.prefix());
        try {
            var objects=new ArrayList<ObjectKey>(); var keys=new HashSet<String>(); var tokens=new HashSet<String>(); String token=null;
            do {
                String current=token;
                var page=client.listObjectsV2(request -> request.bucket(source.bucket()).prefix(source.prefix()).continuationToken(current));
                if(page.isTruncated()==null) throw new IllegalStateException();
                for(var item:page.contents()) {
                    if(item.key()==null || !item.key().startsWith(source.prefix()) || item.size()==null || item.size()<0 || !keys.add(item.key())) throw new IllegalStateException();
                    objects.add(new ObjectKey(item.key(),item.size()));
                }
                token=Boolean.TRUE.equals(page.isTruncated())?page.nextContinuationToken():null;
                if(Boolean.TRUE.equals(page.isTruncated()) && (token==null || token.isBlank() || !tokens.add(token))) throw new IllegalStateException();
            } while(token!=null);
            event(System.getLogger(getClass().getName()),INFO,"S3_LIST_COMPLETED","bucket",source.bucket(),"prefix",source.prefix(),"count",objects.size(),"durationMs",(System.nanoTime()-logStarted)/1_000_000);
            return List.copyOf(objects);
        } catch(RuntimeException failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.credentialFailure(System.getLogger(S3DocumentObjectStore.class.getName()),"S3_DOCUMENT_OBJECT_STORE_FAILED",failure); throw failure("DOCUMENT_LIST_FAILED"); }
    }
    @Override public Content download(DocumentCorpus.Source source,ObjectKey key,int maxBytes) {
        allowed(source); if(!key.key().startsWith(source.prefix()) || maxBytes<1) throw failure("DOCUMENT_SCOPE_INVALID");
        long logStarted=System.nanoTime();event(System.getLogger(getClass().getName()),INFO,"S3_DOWNLOAD_REQUEST","bucket",source.bucket(),"objectKey",key.key(),"size",key.size());
        try(var response=client.getObject(request->request.bucket(source.bucket()).key(key.key()))) {
            var digest=MessageDigest.getInstance("SHA-256"); var output=new ByteArrayOutputStream(); byte[] buffer=new byte[8192]; long size=0; int count;
            // 과대·미지원 파일도 실제 바이트 revision과 제외 기록을 남기되 원문을 메모리에 쌓지 않습니다.
            while((count=response.read(buffer))!=-1) {
                digest.update(buffer,0,count); size+=count;
                if(size<=maxBytes) output.write(buffer,0,count); else output.reset();
            }
            if(response.response().contentLength()!=null && response.response().contentLength()!=size) throw new IOException();
            event(System.getLogger(getClass().getName()),INFO,"S3_DOWNLOAD_COMPLETED","bucket",source.bucket(),"objectKey",key.key(),"size",size,"etag",response.response().eTag(),"durationMs",(System.nanoTime()-logStarted)/1_000_000);
            return new Content(HexFormat.of().formatHex(digest.digest()),size>maxBytes?new byte[0]:output.toByteArray(),size>maxBytes);
        } catch(Exception failure) {com.nori.ai.gateway.core.logging.DiagnosticLog.credentialFailure(System.getLogger(S3DocumentObjectStore.class.getName()),"S3_DOCUMENT_OBJECT_STORE_FAILED",failure); throw failure("DOCUMENT_DOWNLOAD_FAILED"); }
    }
    private void allowed(DocumentCorpus.Source source) { if(!sources.contains(source)) throw failure("DOCUMENT_SCOPE_INVALID"); }
    private static IllegalStateException failure(String code) { return new IllegalStateException(code); }
    @Override public void close() { if(client!=null)client.close(); }
}

package com.nori.ai.gateway.core.knowledge;

import com.nori.ai.gateway.core.result.ResultSourceDeps;
import com.nori.ai.gateway.core.scope.OwnerSystem;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class DocumentReferences {
    private DocumentReferences() {}
    public static ResultSourceDeps.DocKey key(OwnerSystem system,DocumentLocation location) {
        return new ResultSourceDeps.DocKey(system.name(),"knowledge://"+system.name()+"/documents/"+encode(location.bucket())+"/"+encode(location.objectKey()),location.revision());
    }
    private static String encode(String value){return URLEncoder.encode(value,StandardCharsets.UTF_8).replace("+","%20");}
}

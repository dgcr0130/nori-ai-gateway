package com.nori.ai.gateway.integration.source;
import java.util.*;
/** 기동 설정에서 만든 시스템별 읽기 위치입니다. */
public record SystemSourceScope(List<App> apps,List<Trace> traces,List<Repository> repositories) {
    public record App(String name,String namespace,List<String> requiredPermissions){public App{requiredPermissions=List.copyOf(requiredPermissions);}}
    public record Trace(String service,String namespace,List<String> requiredPermissions){public Trace{requiredPermissions=List.copyOf(requiredPermissions);}}
    public record Repository(String projectId,String ref,String pathPrefix,List<String> requiredPermissions){public Repository{requiredPermissions=List.copyOf(requiredPermissions);}}
    public SystemSourceScope{apps=List.copyOf(apps);traces=List.copyOf(traces);repositories=List.copyOf(repositories);}
    public static SystemSourceScope empty(){return new SystemSourceScope(List.of(),List.of(),List.of());}
}

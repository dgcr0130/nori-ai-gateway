plugins { `java-library` }

dependencies {
    implementation(project(":ai-gateway-core"))
    implementation("org.springframework:spring-context")
    implementation(platform(libs.awssdk.bom))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:url-connection-client")
    implementation(libs.commonmark)
    implementation(libs.commonmark.tables)
}

// 독립 채점기도 읽을 원문·oracle을 시험 classpath에 복사하여 증거 지문에 포함합니다.
tasks.named<ProcessResources>("processTestResources") {
    from(rootProject.file("acceptance/fixtures/markdown")) { into("markdown") }
}

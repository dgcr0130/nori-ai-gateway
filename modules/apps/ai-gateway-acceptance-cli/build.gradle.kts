plugins { application }
application { mainClass = "com.nori.ai.gateway.eval.EvalCli" }
dependencies {
    implementation(libs.jackson)
    implementation(libs.commonmark)
    implementation(libs.commonmark.tables)
    implementation(libs.lucene.core)
    implementation(libs.lucene.nori)
    implementation("org.yaml:snakeyaml:2.5")
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj)
    testImplementation(gradleTestKit())
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// 문서 adapter와 독립적으로 같은 원문·직렬화 oracle을 검사합니다.
tasks.named<ProcessResources>("processTestResources") {
    from(rootProject.file("acceptance/fixtures/markdown")) { into("markdown") }
}

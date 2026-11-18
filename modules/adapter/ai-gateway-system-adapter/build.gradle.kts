plugins { `java-library`; `java-test-fixtures` }
dependencies {
    testFixturesApi(libs.nimbus.jose.jwt)
    testFixturesApi(project(":ai-gateway-core"))
    implementation(project(":ai-gateway-core"))
    implementation("org.springframework:spring-context")
}

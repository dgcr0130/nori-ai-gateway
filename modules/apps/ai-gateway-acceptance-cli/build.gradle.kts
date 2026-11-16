plugins { application }
application { mainClass = "com.nori.ai.gateway.eval.EvalCli" }
dependencies {
    implementation(libs.jackson)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj)
    testImplementation(gradleTestKit())
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

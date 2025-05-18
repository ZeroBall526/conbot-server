import com.github.gradle.node.npm.task.NpmTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("com.github.node-gradle.node") version "7.1.0"
}

group = "com.zeroball0526"
version = "1.0.0"

val pageDir = "webui"

node{
    version.set("20.12.2")
    npmVersion.set("10.5.0")
    download.set(true)
    distBaseUrl = "https://nodejs.org/dist"
    workDir.set(file("${rootProject.layout.projectDirectory}/.gradle/nodejs"))
    npmWorkDir.set(file("${rootProject.layout.projectDirectory}/.gradle/npm"))
    nodeProjectDir.set(file("${rootProject.layout.projectDirectory}/$pageDir"))
}

application {
    mainClass.set("com.zeroball0526.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

ktor{
    fatJar {
        archiveFileName.set("conbot-server.jar")
    }
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks.register("buildWeb") {
    group = "build"
    description = "콘봇 서버 웹페이지를 빌드하고 백엔드와 통합을 해요"

    dependsOn("npmInstall")
    dependsOn("runBuildPage")
    doLast {
        copy {
            from("$pageDir/build")
            into("src/main/resources/static")
        }
    }
}

tasks.register<NpmTask>("runBuildPage"){
    args = listOf("run","build")
}

tasks.named("buildFatJar"){
    dependsOn("buildWeb")
}
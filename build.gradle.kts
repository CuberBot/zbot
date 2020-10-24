import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.proto
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenLocal()
        maven(url = "http://maven.aliyun.com/nexus/content/groups/public/")
        maven(url = "http://maven.aliyun.com/nexus/content/repositories/jcenter")
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.13")
    }
}

plugins {
    id("com.google.protobuf") version "0.8.13"
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    kotlin("plugin.jpa") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    kotlin("kapt") version "1.4.10"
}

group = "net.lz1998"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    implementation("com.google.protobuf:protobuf-java:3.12.2")
    implementation("com.google.protobuf:protobuf-java-util:3.12.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
    implementation("net.lz1998:pbbot-spring-boot-starter:0.0.14")
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
}
sourceSets {
    main {
        proto {
            include("**/*.proto")
        }
    }
}
protobuf {
    generatedFilesBaseDir = "$projectDir/src"
    println(generatedFilesBaseDir)
    protoc {
        // You still need protoc like in the non-Android case
        artifact = "com.google.protobuf:protoc:3.8.0"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
            }
        }
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

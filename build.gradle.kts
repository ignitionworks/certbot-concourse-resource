import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}



dependencies {
    implementation(platform("com.google.cloud:libraries-bom:24.3.0"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.google.cloud:google-cloud-storage")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}
repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.register("out", JavaExec::class) {
    standardInput = System.`in`
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("works.ignition.certbotresource.OutKt")
}

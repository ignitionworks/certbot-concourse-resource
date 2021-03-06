import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

defaultTasks("build", "createOutScript", "createCheckScript", "createInScript")

version = file("VERSION").readText().trim()

plugins {
    kotlin("jvm") version "1.6.10"
    application
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
    jvmTarget = "11"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}

tasks.register("createOutScript", CreateStartScripts::class.java) {
    applicationName = "out"
    mainClass.set("works.ignition.certbotresource.out.OutKt")
    outputDir = tasks.startScripts.get().outputDir
    classpath = tasks.startScripts.get().classpath
    tasks.startScripts.get().dependsOn(this)
}

tasks.register("createInScript", CreateStartScripts::class.java) {
    applicationName = "in"
    mainClass.set("works.ignition.certbotresource.in.InKt")
    outputDir = tasks.startScripts.get().outputDir
    classpath = tasks.startScripts.get().classpath
    tasks.startScripts.get().dependsOn(this)
}

tasks.register("createCheckScript", CreateStartScripts::class.java) {
    applicationName = "check"
    mainClass.set("works.ignition.certbotresource.check.CheckKt")
    outputDir = tasks.startScripts.get().outputDir
    classpath = tasks.startScripts.get().classpath
    tasks.startScripts.get().dependsOn(this)
}

tasks.startScripts.get().enabled = false

tasks.withType(Test::useJUnitPlatform)

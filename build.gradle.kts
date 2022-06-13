plugins {
    checkstyle
    `java-library`
    `maven-publish`
    idea
    id("io.papermc.paperweight.userdev") version "1.3.7"
}

group = "fr.zcraft"
version = "0.1.0"
description = "quartzlib"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenLocal()
    maven(url = uri("https://jitpack.io"))
    maven(url = uri("https://repo.papermc.io/repository/maven-public/"))
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("org.graalvm.js:js:22.1.0.1")
    implementation("org.graalvm.js:js-scriptengine:22.1.0.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:2.7.0")
    paperDevBundle("1.19-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn("reobfJar")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
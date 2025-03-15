plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }    
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.jetbrains.annotations)
    implementation(libs.spigot.api)
    implementation(libs.abstractmenu)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

project.base.archivesName.set(rootProject.name)
group = "fr.flowsqy.xpbarrel"
version = "1.0.0-SNAPSHOT"

tasks.processResources {
    expand(Pair("version", version))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

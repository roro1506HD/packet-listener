plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "ovh.roro.libraries"
version = "1.21.8-packetevents"

repositories {
    mavenLocal()
    maven {
        name = "codemc-releases"
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }
    maven {
        name = "codemc-snapshots"
        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
    }
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    implementation("com.github.retrooper:packetevents-spigot:2.9.5")
}

tasks.shadowJar {
    relocate("com.github.retrooper.packetevents", "ovh.roro.libraries.packetlistener.libs.com.github.retrooper.packetevents")
    relocate("io.github.retrooper.packetevents", "ovh.roro.libraries.packetlistener.libs.io.github.retrooper.packetevents")

    dependencies {
        exclude(dependency("net.kyori:adventure-api:.*"))
        exclude(dependency("net.kyori:adventure-key:.*"))
        exclude(dependency("net.kyori:adventure-nbt:.*"))
        exclude(dependency("net.kyori:examination-api:.*"))
        exclude(dependency("net.kyori:examination-string:.*"))
        exclude(dependency("com.google.code.gson:gson:.*"))
        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
    }
}

tasks.reobfJar {
    dependsOn(tasks.shadowJar)
}

// Configure reobfJar to run when invoking the build task
tasks.assemble {
    dependsOn(tasks.reobfJar)
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release.set(21)
}

tasks.javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
}

tasks.processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.reobfJar)
            artifact(tasks.shadowJar) {
                classifier = "moj-mapped"
            }
            artifact(tasks["javadocJar"]) {
                classifier = "javadoc"
            }
            artifact(tasks["sourcesJar"]) {
                classifier = "sources"
            }
        }
    }

    repositories {
        maven {
            name = "roro"
            url = uri("https://repo.roro.ovh/repository/libraries/")

            credentials(PasswordCredentials::class)
        }
    }
}
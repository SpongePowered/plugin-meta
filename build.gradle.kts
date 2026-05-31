plugins {
    id("org.spongepowered.gradle.sponge.dev") version "2.1.1"
    id("org.spongepowered.gradle.repository") version "2.1.1"
    id("net.kyori.indra.checkstyle") version "3.1.3"
    id("net.kyori.indra.crossdoc") version "3.1.3"
    id("net.kyori.indra.publishing.sonatype") version "3.1.3"
}

repositories {
    sponge.all()
}

dependencies {
    compileOnlyApi("org.checkerframework:checker-qual:3.26.0")
    api("com.google.code.gson:gson:2.8.9")
    api("org.apache.maven:maven-artifact:3.8.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

spongeConvention {
    repository("plugin-meta") {
        ci(true)
        publishing(true)
    }
    mitLicense()
    licenseParameters {
        this["organization"] = rootProject.property("organization")
    }
}

indra {
    javaVersions().target(21)
}

indraCrossdoc {
    baseUrl(providers.gradleProperty("javadocPublishRoot"))
}

tasks {
    withType(JavaCompile::class).configureEach {
        doFirst {
            options.compilerArgs.addAll(listOf("--module-path", classpath.asPath, "--module-version", project.version.toString()))
        }
    }
    javadoc {
        (options as StandardJavadocDocletOptions).links(
            "https://www.javadoc.io/doc/com.google.code.gson/gson/2.8.9/",
            "https://checkerframework.org/api/",
            "https://maven.apache.org/ref/3.8.6/maven-artifact/apidocs"
        )

        doFirst {
            options.modulePath(classpath.toList())
        }
    }
}

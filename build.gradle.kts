plugins {
    id("org.spongepowered.gradle.sponge.dev") version "2.1.1"
    id("org.spongepowered.gradle.repository") version "2.1.1"
    id("net.kyori.indra.checkstyle") version "3.1.3"
    id("net.kyori.indra.crossdoc") version "3.1.3"
    id("net.kyori.indra.publishing.sonatype") version "3.1.3"
}

dependencies {
    compileOnlyApi("org.checkerframework:checker-qual:3.26.0")
    api("com.google.code.gson:gson:2.8.9")
    api("org.apache.maven:maven-artifact:3.8.6")
}

sourceSets.main {
    multirelease.moduleName("org.spongepowered.plugin.metadata")
}


allprojects {
    apply(plugin = "org.spongepowered.gradle.sponge.dev")
    apply(plugin = "org.spongepowered.gradle.repository")
    apply(plugin = "net.kyori.indra.checkstyle")
    apply(plugin = "net.kyori.indra.crossdoc")
    apply(plugin = "net.kyori.indra.publishing")

    repositories {
        sponge.all()
    }

    indra {
        javaVersions().minimumToolchain(17)
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

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    }
    
    sourceSets.main {
        multirelease {
            alternateVersions(9)
            requireAllPackagesExported()
            applyToJavadoc(true)
        }
    }

    indraCrossdoc {
      baseUrl(providers.gradleProperty("javadocPublishRoot"))
    }
    
    java {
        modularity.inferModulePath.set(false)
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
}

subprojects {
    group = "${rootProject.group}.${rootProject.name}"
}

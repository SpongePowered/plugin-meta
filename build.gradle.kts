plugins {
    id("org.spongepowered.gradle.sponge.dev") version "1.1.0-SNAPSHOT"
    id("net.kyori.indra.checkstyle") version "2.0.1"
    id("net.kyori.indra.publishing.sonatype") version "2.0.1"
}

dependencies {
    compileOnlyApi("org.checkerframework:checker-qual:3.12.0")
    api("com.google.code.gson:gson:2.8.0")
    api("org.apache.maven:maven-artifact:3.8.1")
}

tasks.jar {
    manifest.attributes(
            "Automatic-Module-Name" to "org.spongepowered.plugin.metadata"
    )
}

allprojects {
    apply(plugin = "org.spongepowered.gradle.sponge.dev")
    apply(plugin = "net.kyori.indra.publishing")
    apply(plugin = "net.kyori.indra.checkstyle")

    repositories {
        org.spongepowered.gradle.convention.ConventionConstants.spongeRepo(this)
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

    val sourceOutput by configurations.registering
    val main by sourceSets

    dependencies {
        main.allSource.srcDirs.forEach {
            add(sourceOutput.name, project.files(it.relativeTo(project.projectDir).path))
        }

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    }

    tasks {
        jar {
        }

        javadoc {
            options {
                (this as StandardJavadocDocletOptions).apply {
                    links(
                        "http://www.slf4j.org/apidocs/",
                        "https://google.github.io/guava/releases/21.0/api/docs/",
                        "https://google.github.io/guice/api-docs/4.1/javadoc/"
                    )
                }
            }
        }
    }
}

subprojects {
    group = "${rootProject.group}.${rootProject.name}"

    dependencies {
        implementation(rootProject)
    }
}

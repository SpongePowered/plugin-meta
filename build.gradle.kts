plugins {
    id("org.spongepowered.gradle.sponge.dev") version "2.0.2"
    id("net.kyori.indra.checkstyle") version "2.1.1"
    id("net.kyori.indra.crossdoc") version "2.1.1"
    id("net.kyori.indra.publishing.sonatype") version "2.1.1"
}

dependencies {
    compileOnlyApi("org.checkerframework:checker-qual:3.23.0")
    api("com.google.code.gson:gson:2.8.0")
    api("org.apache.maven:maven-artifact:3.8.6")
}

tasks.jar {
    manifest.attributes(
        "Automatic-Module-Name" to "org.spongepowered.plugin.metadata"
    )
}

allprojects {
    apply(plugin = "org.spongepowered.gradle.sponge.dev")
    apply(plugin = "net.kyori.indra.checkstyle")
    apply(plugin = "net.kyori.indra.crossdoc")
    apply(plugin = "net.kyori.indra.publishing")

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

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    indraCrossdoc {
      baseUrl(providers.gradleProperty("javadocPublishRoot"))
    }

    tasks {
        jar {
        }

        javadoc {
            options {
                (this as StandardJavadocDocletOptions).apply {
                    links(
                        "https://www.slf4j.org/apidocs/",
                        "https://guava.dev/releases/21.0/api/docs/",
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

plugins {
    id("org.spongepowered.gradle.sponge.dev") version "1.0-SNAPSHOT"
    id("net.kyori.indra.checkstyle") version "1.3.1"
    id("net.kyori.indra.publishing.sonatype") version "1.3.1"
}

dependencies {
    compileOnlyApi("org.checkerframework:checker-qual:3.10.0")
    api("com.google.code.gson:gson:2.8.0")
}

allprojects {
    apply(plugin = "org.spongepowered.gradle.sponge.dev")
    apply(plugin = "net.kyori.indra.publishing.sonatype")
    apply(plugin = "net.kyori.indra.checkstyle")

    repositories {
        org.spongepowered.gradle.convention.ConventionConstants.spongeRepo(this)
    }

    spongeConvention {
        repository("plugin-meta") {
            ci = true
            publishing = true
        }
        mitLicense()
        licenseParameters {
            this["organization"] = rootProject.property("organization")
        }
    }
    indra.checkstyle.set("8.40")

    val sourceOutput by configurations.registering
    val main by sourceSets

    dependencies {
        main.allSource.srcDirs.forEach {
            add(sourceOutput.name, project.files(it.relativeTo(project.projectDir).path))
        }

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    }

    tasks {
        jar {
            manifest.attributes(mapOf(
                // todo: drop grgit
                "Git-Commit" to grgit.head().id,
                "Git-Branch" to grgit.branch.current().name
            ))
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

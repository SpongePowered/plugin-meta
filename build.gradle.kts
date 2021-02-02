import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    val indraVersion = "1.2.1"
    checkstyle
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.publishing.sonatype") version indraVersion
    id("net.kyori.indra.license-header") version indraVersion
}

dependencies {
    compileOnlyApi("org.checkerframework:checker-qual:3.10.0")
    api("com.google.code.gson:gson:2.8.0")
}

allprojects {
    apply(plugin = "net.kyori.indra")
    apply(plugin = "net.kyori.indra.license-header")
    apply(plugin = "net.kyori.indra.publishing.sonatype")
    apply(plugin = "checkstyle")

    repositories {
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }

    val spongeSnapshotRepo = project.findProperty("spongeSnapshotRepo") as String?
    val spongeReleaseRepo = project.findProperty("spongeReleaseRepo") as String?
    indra {
        github("SpongePowered", "plugin-meta") {
            ci = true
            publishing = true
        }
        mitLicense()

        if (spongeReleaseRepo != null && spongeSnapshotRepo != null) {
            publishSnapshotsTo("sponge", spongeSnapshotRepo)
            publishReleasesTo("sponge", spongeReleaseRepo)
        }
    }

    val sourceOutput by configurations.registering
    val main by sourceSets

    dependencies {
        main.allSource.srcDirs.forEach {
            add(sourceOutput.name, project.files(it.relativeTo(project.projectDir).path))
        }

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    }

    checkstyle {
        toolVersion = "8.39"
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    }

    tasks {
        jar {
            manifest {
                attributes(mapOf(
                        "Git-Commit" to grgit.head().id,
                        "Git-Branch" to grgit.branch.current().name,
                        "Specification-Title" to "plugin-spi",
                        "Specification-Vendor" to "SpongePowered",
                        "Specification-Version" to archiveVersion.get(), // We are version 1 of ourselves
                        "Created-By" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"
                ))
            }
        }

        javadoc {
            options {
                isFailOnError = false
                (this as StandardJavadocDocletOptions).apply {
                    links(
                        "http://www.slf4j.org/apidocs/",
                        "https://google.github.io/guava/releases/21.0/api/docs/",
                        "https://google.github.io/guice/api-docs/4.1/javadoc/",
                        "http://asm.ow2.org/asm50/javadoc/user/"
                    )

                    if (JavaVersion.current() < JavaVersion.VERSION_12) {
                        addBooleanOption("-no-module-directories", true)
                    }
                }
            }
        }

        test {
            testLogging {
                // Always print full stack trace if something goes wrong in the unit tests
                exceptionFormat = TestExceptionFormat.FULL
                showStandardStreams = true
            }
        }
    }

    license {
        header = rootProject.file("HEADER.txt")
        newLine = false

        ext["name"] = rootProject.name
        ext["organization"] = rootProject.property("organization")
    }

    // Signing, using specified private key file
    signing {
        val spongeSigningKey = project.findProperty("spongeSigningKey") as String?
        val spongeSigningPassword = project.findProperty("spongeSigningPassword") as String?
        if (spongeSigningKey != null && spongeSigningPassword != null) {
            val keyFile = file(spongeSigningKey)
            if (keyFile.exists()) {
                useInMemoryPgpKeys(file(spongeSigningKey).readText(Charsets.UTF_8), spongeSigningPassword)
            } else {
                useInMemoryPgpKeys(spongeSigningKey, spongeSigningPassword)
            }
        } else {
            signatories = PgpSignatoryProvider() // don't use gpg agent
        }
    }
}

subprojects {
    group = "${rootProject.group}.${rootProject.name}"

    dependencies {
        implementation(rootProject)
    }
}

import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java

    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"

    id("net.minecrell.licenser") version "0.3"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.bintray")
    apply(plugin = "net.minecrell.licenser")

    repositories {
        jcenter()
    }

    dependencies {
        compileOnly("org.checkerframework:checker-qual:2.8.1")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    val javadoc = tasks.getByName<Javadoc>("javadoc") {
        (options as? CoreJavadocOptions)?.addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()

        testLogging {
            // Always print full stack trace if something goes wrong in the unit tests
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }

    val sourceJar = task<Jar>("sourceJar") {
        classifier = "sources"
        from(java.sourceSets["main"].allSource)
    }

    val javadocJar = task<Jar>("javadocJar") {
        classifier = "javadoc"
        from(javadoc)
    }

    artifacts {
        add("archives", sourceJar)
        add("archives", javadocJar)
    }

    license {
        header = rootProject.file("HEADER.txt")
        newLine = false

        ext["name"] = rootProject.name
        ext["organization"] = rootProject.property("organization")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                artifact(sourceJar)
                artifact(javadocJar)

                pom {
                    val url: String by project
                    url(url)

                    scm {
                        url(url)
                        connection("scm:git:$url.git")
                        developerConnection.set(connection)
                    }

                    issueManagement {
                        system("GitHub Issues")
                        url("$url/issues")
                    }

                    licenses {
                        license {
                            name("MIT License")
                            url("https://opensource.org/licenses/MIT")
                            distribution("repo")
                        }
                    }
                }
            }
        }

        val spongeRepo: String? by project
        val spongeUsername: String? by project
        val spongePassword: String? by project

        spongeRepo?.let { repo ->
            repositories {
                maven(repo) {
                    if (spongeUsername != null && spongePassword != null) {
                        credentials {
                            username = spongeUsername
                            password = spongePassword
                        }
                    }
                }
            }
        }
    }

    bintray {
        val bintrayUser: String? by project
        val bintrayKey: String? by project
        if (bintrayUser != null && bintrayKey != null) {
            user = bintrayUser
            key = bintrayKey
        }

        pkg.run {
            setPublications("maven")

            repo = "maven"
            name = rootProject.name
            userOrg = "spongepowered"
            desc = project.description
            setLicenses("MIT")

            val url: String by project
            websiteUrl = url
            issueTrackerUrl = "$url/issues"
            vcsUrl = "$url.git"

            setLabels("minecraft", "sponge", "plugin")

            publicDownloadNumbers = true

            version.run {
                name = project.version.toString()
                vcsTag = name
            }
        }
    }
}

dependencies {
    compile("com.google.guava:guava:21.0")
}

subprojects {
    group = "${rootProject.group}.${rootProject.name}"

    dependencies {
        compile(rootProject)
    }
}

operator fun Property<String>.invoke(v: String) = set(v)

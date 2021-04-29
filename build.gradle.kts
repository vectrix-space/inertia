plugins {
  id("signing")
  id("net.kyori.indra") version "1.3.1"
  id("net.kyori.indra.publishing") version "1.3.1" apply false
  id("net.kyori.indra.license-header") version "2.0.1" apply false
  id("de.marcphilipp.nexus-publish") version "0.4.0" apply false
}

group = "space.vectrix.inertia"
version = "0.1.0-SNAPSHOT"
description = "A simple, fast, entity component framework."

subprojects {
  apply(plugin = "net.kyori.indra")
  apply(plugin = "net.kyori.indra.publishing")
  apply(plugin = "net.kyori.indra.license-header")
  apply(plugin = "de.marcphilipp.nexus-publish")

  group = rootProject.group
  version = rootProject.version
  description = rootProject.description

  repositories {
    mavenLocal()
    mavenCentral()
    maven {
      url = uri("https://oss.sonatype.org/content/groups/public/")
    }
  }

  dependencies {
    testImplementation("com.google.guava:guava-testlib:30.1.1-jre")
    testImplementation("com.google.truth:truth:1.1.2")
    testImplementation("com.google.truth.extensions:truth-java8-extension:1.1.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
  }

  signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(configurations.archives.get())
  }

  indra {
    github("vectrix-space", "inertia") {
      ci = true
    }

    mitLicense()

    extensions.configure<de.marcphilipp.gradle.nexus.NexusPublishExtension> {
      repositories.sonatype {
        nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
        snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
      }
    }

    configurePublications {
      pom {
        developers {
          developer {
            id.set("VectrixDevelops")
            name.set("Vectrix")
          }
        }
      }
    }
  }

  tasks.withType<PublishToMavenRepository>().configureEach {
    onlyIf {
      val version: String = project.version.toString()
      System.getenv("CI") == null || version.endsWith("-SNAPSHOT")
    }
  }
}

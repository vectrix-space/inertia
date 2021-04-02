import net.kyori.indra.IndraLicenseHeaderPlugin
import net.kyori.indra.IndraPlugin

plugins {
  id("net.kyori.indra") version "1.3.1"
  id("net.kyori.indra.license-header") version "1.3.1"
}

group = "space.vectrix.inertia"
version = "0.1.0-SNAPSHOT"
description = "A simple, fast, entity component framework."

subprojects {
  apply<IndraPlugin>()
  apply<IndraLicenseHeaderPlugin>()

  repositories {
    mavenLocal()
    mavenCentral()
    maven {
      url = uri("https://oss.sonatype.org/content/groups/public/")
    }
  }

  dependencies {
    testImplementation("com.google.guava:guava-testlib:30.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
  }

  indra {
    github("vectrix-space", "inertia")

    mitLicense()
  }
}

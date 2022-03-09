plugins {
  id("net.kyori.indra")
  id("net.kyori.indra.license-header")
}

repositories {
  mavenLocal()
  mavenCentral()
  maven {
    url = uri("https://s01.oss.sonatype.org/content/groups/public/")
  }
  maven {
    url = uri("https://oss.sonatype.org/content/groups/public/")
  }
}

dependencies {
  testImplementation("com.google.guava:guava-testlib:31.1-jre")
  testImplementation(platform("org.junit:junit-bom:5.8.2"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

indra {
  github("vectrix-space", "inertia") {
    ci(true)
  }

  mitLicense()
}

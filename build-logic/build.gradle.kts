plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("net.kyori", "indra-common", "2.0.6")
  implementation("gradle.plugin.com.github.jengelman.gradle.plugins", "shadow", "7.0.0")
  implementation("de.marcphilipp.gradle", "nexus-publish-plugin", "0.4.0")
}

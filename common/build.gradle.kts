dependencies {
  api(project(":inertia-api"))
  api("org.checkerframework:checker-qual:3.11.0")

  implementation("com.google.guava:guava:30.1.1-jre")
  implementation("it.unimi.dsi:fastutil:8.5.4")
  implementation("org.ow2.asm:asm-debug-all:6.0_BETA")
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "space.vectrix.inertia"
  )
}

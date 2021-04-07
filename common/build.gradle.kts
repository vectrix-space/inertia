dependencies {
  api(project(":inertia-api"))

  api("org.checkerframework:checker-qual:3.12.0")
  api("com.google.guava:guava:30.1.1-jre")
  api("it.unimi.dsi:fastutil:8.5.4")
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "space.vectrix.inertia"
  )
}

dependencies {
  api("org.checkerframework:checker-qual:3.11.0")
  api("com.google.guava:guava:30.1.1-jre")
  api("space.vectrix.flare:flare:0.1.0")
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "space.vectrix.inertia"
  )
}

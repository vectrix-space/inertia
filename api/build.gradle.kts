dependencies {
  api("org.checkerframework:checker-qual:3.11.0")
  api("space.vectrix.flare:flare:0.1.0")
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "space.vectrix.inertia"
  )
}

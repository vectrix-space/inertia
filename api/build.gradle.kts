dependencies {
  api("org.checkerframework:checker-qual:3.12.0")
  api("net.kyori:coffee:1.0.0-SNAPSHOT")
  api("space.vectrix.flare:flare:0.1.0")
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "space.vectrix.inertia"
  )
}

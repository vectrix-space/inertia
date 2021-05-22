dependencies {
  api("com.google.guava:guava:30.1.1-jre")
  api("it.unimi.dsi:fastutil:8.5.4")
  api("net.kyori:coffee:1.0.0-SNAPSHOT")
  api("org.checkerframework:checker-qual:3.13.0")
  api("space.vectrix.flare:flare:0.2.0-SNAPSHOT")
  api("space.vectrix.flare:flare-fastutil:0.2.0-SNAPSHOT")
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "space.vectrix.inertia"
  )
}

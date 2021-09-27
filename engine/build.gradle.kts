plugins {
  id("inertia.shared-conventions")
}

dependencies {
  compileOnlyApi("org.checkerframework:checker-qual:3.18.0")
  api("com.google.guava:guava:31.0-jre")
  api("net.kyori:coffee:1.0.0-SNAPSHOT")
  api("it.unimi.dsi:fastutil:8.5.6")
  api("space.vectrix.flare:flare:1.0.0")
  api("space.vectrix.flare:flare-fastutil:1.0.0")
}

applyJarMetadata("space.vectrix.inertia")

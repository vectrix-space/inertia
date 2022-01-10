plugins {
  id("inertia.shared-conventions")
}

dependencies {
  compileOnlyApi("org.checkerframework:checker-qual:3.21.1")
  api("it.unimi.dsi:fastutil:8.5.6")
  api("space.vectrix.flare:flare:2.0.0")
  api("space.vectrix.flare:flare-fastutil:2.0.0")
}

applyJarMetadata("space.vectrix.inertia")

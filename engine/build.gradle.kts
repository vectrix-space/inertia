plugins {
  id("inertia.shared-conventions")
}

dependencies {
  compileOnlyApi("org.checkerframework:checker-qual:3.48.1")
  api("it.unimi.dsi:fastutil:8.5.8")
  api("space.vectrix.flare:flare:2.0.1")
  api("space.vectrix.flare:flare-fastutil:2.0.1")
}

applyJarMetadata("space.vectrix.inertia")

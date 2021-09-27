plugins {
  id("inertia.shared-conventions")
}

dependencies {
  api(project(":inertia-engine"))

  implementation("org.lanternpowered:lmbda:2.0.0")
}

applyJarMetadata("space.vectrix.inertia.injection")

pluginManagement {
  includeBuild("build-logic")
}

rootProject.name = "inertia-parent"

sequenceOf(
  "engine",
  "injector-asm"
).forEach {
  include("inertia-$it")
  project(":inertia-$it").projectDir = file(it)
}

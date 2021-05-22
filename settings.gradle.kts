rootProject.name = "inertia-parent"

include("engine")
include("injector-asm")

listOf(
  "engine",
  "injector-asm"
).forEach {
  findProject(":$it")?.name = "inertia-$it"
}

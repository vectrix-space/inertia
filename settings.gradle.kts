rootProject.name = "inertia-parent"

include("api")
include("common")
include("injector-asm")

listOf(
  "api",
  "common",
  "injector-asm"
).forEach {
  findProject(":$it")?.name = "inertia-$it"
}

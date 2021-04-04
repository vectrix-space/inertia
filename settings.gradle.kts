rootProject.name = "inertia-parent"

include("api")
include("common")

listOf(
  "api",
  "common"
).forEach {
  findProject(":$it")?.name = "inertia-$it"
}

workspace(name = "io_higherkindness_arktika")

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

http_archive(
    name = "bazel_skylib",
    sha256 = "c0289fef5237c31e8462042b4cc3bdf831a3d3d135bb4a0d493a5072acecb074",
    strip_prefix = "bazel-skylib-2169ae1c374aab4a09aa90e65efe1a3aad4e279b",
    urls = ["https://github.com/bazelbuild/bazel-skylib/archive/2169ae1c374aab4a09aa90e65efe1a3aad4e279b.zip"],
)

http_archive(
    name = "com_google_protobuf",
    sha256 = "2c8f8614fb1be709d68abaab6b4791682aa7db2048012dd4642d3a50b4f67cb3",
    strip_prefix = "protobuf-0038ff49af882463c2af9049356eed7df45c3e8e",
    urls = ["https://github.com/google/protobuf/archive/0038ff49af882463c2af9049356eed7df45c3e8e.zip"],
)

git_repository(
    name = "rules_scala_annex",
    commit = "604e2a9cafcd790740505826f5c0f21c2295b08e",
    remote = "git://github.com/andyscott/rules_scala_annex",
)

load("@rules_scala_annex//rules/scala:workspace.bzl", "scala_register_toolchains", "scala_repositories", "scala_repository")

scala_repositories()

scala_register_toolchains()

scala_repository(
    "scala",
    ("org.scala-lang", "2.12.7"),
    "@compiler_bridge_2_12//:src",
)

load("@rules_scala_annex//rules/scalafmt:workspace.bzl", "scalafmt_default_config", "scalafmt_repositories")

scalafmt_repositories()

scalafmt_default_config()

git_repository(
    name = "io_higherkindness_singularity",
    commit = "768e251b117178e27d4546b754cead5736c32513",
    remote = "git://github.com/andyscott/singularity",
)

load("@io_higherkindness_singularity//repos:repos.bzl", "singularity_scala_repositories")

singularity_scala_repositories()

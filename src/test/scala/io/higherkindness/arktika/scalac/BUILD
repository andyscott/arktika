load("//tools:scala.bzl", "scala_test")

scala_test(
    name = "ArktikaPhaseChecks",
    srcs = [
        "ArktikaPhaseChecks.scala",
    ],
    deps = [
         "//src/main/scala/io/higherkindness/arktika/scalac:plugin",
        "@org_scalacheck_scalacheck",
        "@scala_scala_compiler//jar",
        "@scala_scala_reflect//jar",
    ],    
    visibility = ["//visibility:public"],
)

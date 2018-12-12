load(
    "//tools:scala.bzl",
    "scala_library",
    "scala_test",
)

scala_library(
    name = "test1",
    srcs = [
         "test1.scala",
    ],
    plugins = [
        "//:scalac-plugin",
    ],    
    visibility = ["//visibility:public"],
)

load("//tools:scala.bzl", "scala_library")

scala_library(
    name = "plugin",
    srcs = glob([
        "*.scala",
    ]),
    deps = [
        "//src/main/scala/io/higherkindness/arktika/osiris",
        "@scala_scala_compiler//jar",
        "@scala_scala_reflect//jar",
    ],
    resource_jars = [
        ":resources",
    ],
    visibility = ["//visibility:public"],
)


_gen_plugin_xml_cmd = """
cat > $@ << EOF
<plugin>
  <name>name</name>
  <classname>io.higherkindness.arktika.scalac.ArktikaPlugin</classname>
</plugin>
"""

genrule(
    name = "gen-scalac-plugin.xml",
    outs = ["scalac-plugin.xml"],
    cmd = _gen_plugin_xml_cmd,
)

java_binary(
    name = "resources",
    classpath_resources = [
        ":gen-scalac-plugin.xml",
    ],
    create_executable = False,
)

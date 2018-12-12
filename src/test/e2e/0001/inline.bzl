load("//tools:scala.bzl", "scala_library")

_gen_command = """
cat > $@ << EOF
{blob}
"""

def inline_scala_library(**kwargs):
    _kwargs = dict([
        (key, value)
        for key, value in kwargs.items()
        if key not in ["blob"]
    ])

    if "blob" in kwargs:
        name = kwargs["name"]
        blob = kwargs["blob"]
        out = "_blob_%s.scala" % name
        native.genrule(
            name = "_gen_blob_%s" % name,
            outs = [out],
            cmd = _gen_command.format(blob = blob),
        )
        _kwargs["srcs"] = [":%s" % out]

    scala_library(**_kwargs)

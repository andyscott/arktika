load(":inline.bzl", "inline_scala_library")

_A = """
package A

import B.B

class A {
  new B
}
"""

inline_scala_library(
    name = "A",
    blob = _A,
    deps = [":B"],
)

_B = """
package B

import C.C

class B {
  private def f(): C = {
    new C
  }
}
"""

inline_scala_library(
    name = "B",
    blob = _B,
    deps = [":C"],
)

_C = """
package C

class C {

}
"""

inline_scala_library(
    name = "C",
    blob = _C,
)

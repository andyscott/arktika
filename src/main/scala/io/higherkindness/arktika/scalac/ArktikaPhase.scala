package io.higherkindness.arktika.scalac

import scala.tools.nsc.Global

trait ArktikaPhase[G <: Global] extends ArktikaToolbelt[G] {
  val global: G
  import global._

  def apply(unit: CompilationUnit): Unit = {
    val res = analyze(unit.body)
    print("res: " + res)
    ()
  }

}

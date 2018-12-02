package io.higherkindness.arktika.scalac

import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent


final class ArktikaPlugin(override val global: Global) extends Plugin {

  override val name: String = "лк-60я"
  override val description: String = "breaks your build apart"
  override val components: List[PluginComponent] =
    ArktikaPluginComponent :: Nil

  private object ArktikaPluginComponent extends PluginComponent {
    override val global: Global = ArktikaPlugin.this.global
    override val phaseName: String = "arktika"
    override val runsAfter: List[String] = "typer" :: Nil

    override def newPhase(prev: Phase): Phase =
      new StdPhase(prev) with ArktikaPhase[global.type] {
        val global = ArktikaPluginComponent.this.global
      }
  }

}

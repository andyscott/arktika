package io.higherkindness.arktika.scalac

import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent

import scala.tools.nsc.GlobalSymbolLoaders

abstract class ArktikaGlobalSymbolLoaders extends GlobalSymbolLoaders {

  import global._

  override def enterIfNew(
    owner: Symbol,
    member: Symbol,
    completer: SymbolLoader
  ): Symbol = {
    val res = super.enterIfNew(owner, member, completer)
    println(">>> " + res)
    res
  }


}



final class ArktikaPlugin(override val global: Global) extends Plugin with Hijacking {

  override val name: String = "лк-60я"
  override val description: String = "breaks your build apart"
  override val components: List[PluginComponent] =
    ArktikaPluginComponent :: Nil

  lazy val loaders = new {
    val global: ArktikaPlugin.this.global.type = ArktikaPlugin.this.global
    val platform: ArktikaPlugin.this.global.platform.type = ArktikaPlugin.this.global.platform
  } with ArktikaGlobalSymbolLoaders

  {
    hijackField("loaders", loaders)
  }


  if (global.loaders != loaders)
    sys.error("failed to hijack loaders")

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

package io.higherkindness.arktika
package scalac


import scala.tools.nsc.Global
import scala.tools.nsc.SubComponent
import scala.tools.nsc.plugins.Plugin

/** Hijacking helper for compiler phases. */
trait Hijacking { self: Plugin ⇒

  /** Hijack a field from global */
  protected[this] final def hijackField[T](name: String, newValue: T): T = {
    val field = classOf[Global].getDeclaredField(name)
    field.setAccessible(true)
    val oldValue = field.get(global).asInstanceOf[T]
    field.set(global, newValue)
    oldValue
  }

  /** Hijack a phase from global */
  protected[this] final def hijackPhase(
    name: String, newPhase: SubComponent
  ): Option[SubComponent] = {

    val phasesSet = classOf[Global].getDeclaredMethod("phasesSet")
      .invoke(global).asInstanceOf[scala.collection.mutable.Set[SubComponent]]

    phasesSet.find(_.phaseName == name).map { oldPhase ⇒
      phasesSet -= oldPhase
      phasesSet += newPhase
      oldPhase
    }
  }

}

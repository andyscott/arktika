package io.higherkindness.arktika
package scalac

import scala.tools.nsc.Global

import osiris._

final case class Dep[A](x: A, rel: Dep.Rel, y: A)

object Dep {
  sealed trait Rel
  object Rel {
    case object Extend extends Rel
    case object Select extends Rel
  }

  def extend[A](x: A, y:A): Dep[A] = Dep(x, Rel.Extend, y)
  def select[A](x: A, y:A): Dep[A] = Dep(x, Rel.Select, y)

  implicit val depFunctor: Functor[Dep] = new Functor[Dep] {
    def map[A, B](fa: Dep[A])(f: A => B): Dep[B] =
      Dep(f(fa.x), fa.rel, f(fa.y))
  }
}


trait ArktikaToolbelt[G <: Global] {
  val global: G
  import global._

  def analyze(tree: Tree): List[Dep[Symbol]] = {
    val traverser = new ArktikaTraverser
    traverser.traverse(tree)
    traverser.deps
  }

  final class ArktikaTraverser extends Traverser {
    var deps: List[Dep[Symbol]] = Nil

    override def traverse(tree: Tree): Unit = tree match {
      case template: Template =>

        deps = template.parents
          .map(_.tpe.typeSymbolDirect)
          .map(Dep.extend(currentOwner, _)) ::: deps

        traverseTrees(template.body)

      case select: Select =>
        deps = Dep.select(currentOwner, select.symbol) :: deps
        traverse(select.qualifier)

      case _ =>
        super.traverse(tree)
    }
  }

}

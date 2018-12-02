package io.higherkindness.arktika
package scalac

import scala.tools.nsc.Global

import osiris._

sealed trait Dep[A] {
  def widen: Dep[A] = this
}
object Dep {
  final case class Extends[A](x: A, y: A) extends Dep[A]


  implicit val depFunctor: Functor[Dep] = new Functor[Dep] {
    def map[A, B](fa: Dep[A])(f: A => B): Dep[B] = fa match {
      case Extends(x, y) => Extends(f(x), f(y))
    }
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
      case Template(parents, self, body) =>

        deps = parents
          .map(_.tpe.typeSymbolDirect)
          .map(Dep.Extends(currentOwner, _).widen) ::: deps


        traverseTrees(body)
      case _ =>
        super.traverse(tree)
    }
  }

}

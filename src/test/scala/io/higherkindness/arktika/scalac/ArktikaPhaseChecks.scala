package io.higherkindness.arktika.scalac

import org.scalacheck.Properties
import org.scalacheck.Prop
import org.scalacheck.Prop._

import scala.annotation.tailrec

import scala.tools.nsc.interactive.Global
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter

import java.lang.ClassLoader
import java.net.URLClassLoader

import io.higherkindness.arktika.osiris._

class ArktikaPhaseChecks extends Properties("ArktikaPhase") {

  lazy val toolbelt = new ArktikaToolbelt[TestGlobal.global.type] {
    val global = TestGlobal.global
  }

  import TestGlobal.global._

  def analyze(tree: Tree): List[Dep[_]] = {
    ask { () =>
      try {
        val termTree =
          if (tree.isTerm) tree
          else q"{ $tree }"
        typer.context.initRootContext()
        toolbelt
          .analyze(typer.typed(termTree))
          .map(_.map(_.fullName))
      } catch {
        case t: Throwable =>
          t.printStackTrace()
          throw t
      }
    }
  }

  property("Dep.Extends") = {

    val pairs: List[(Tree, List[Dep[String]])] = List(
      q"class Foo" -> List(
        Dep.Extends("Foo", "scala.AnyRef")
      ),
      q"class Foo; class Bar extends Foo" -> List(
        Dep.Extends("Bar", "Foo"),
        Dep.Extends("Foo", "scala.AnyRef")
      ),
      q"""
        trait Foo
        class Bar
        class Baz extends Bar with Foo
      """ -> List(
        Dep.Extends("Baz", "Bar"),
        Dep.Extends("Baz", "Foo"),
        Dep.Extends("Bar", "scala.AnyRef"),
        Dep.Extends("Foo", "scala.AnyRef"),
      )
    )

    pairs.foldLeft(Prop.proved)((acc, pair) =>
      (analyze(pair._1) ?= pair._2) && acc
    )
  }

}

object TestGlobal {
  lazy val global: Global = {
    @tailrec def classpath(cl: ClassLoader): List[String] = cl match {
      case c: URLClassLoader => c.getURLs.toList.map(_.toString)
      case c if c.getParent() != null  => classpath(c.getParent())
      case c                           => sys.error(s"unknown classloader type $c")
    }

    val settings = new Settings
    settings.usejavacp.value = false
    classpath(Thread.currentThread.getContextClassLoader).distinct.foreach { item =>
      settings.classpath.append(item)
      settings.bootclasspath.append(item)
    }

    new Global(settings, new ConsoleReporter(settings))
  }

}

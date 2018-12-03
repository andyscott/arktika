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
import scala.reflect.internal.util.BatchSourceFile

import io.higherkindness.arktika.osiris._

class ArktikaPhaseChecks extends Properties("ArktikaPhase") {

  lazy val toolbelt = new ArktikaToolbelt[TestGlobal.global.type] {
    val global = TestGlobal.global
  }

  import TestGlobal.global._

  def analyze(tree: Tree): List[Dep[_]] = {
    val termTree =
      if (tree.isTerm) tree
      else q"{ $tree }"
    typer.context.initRootContext()
    toolbelt
      .analyze(ask(() => typer.typed(termTree)))
      .map(_.map(_.fullName))
  }

  def analyze(blobs: List[String]): List[Dep[_]] = {
    askReset()
    blobs
      .zipWithIndex
      .map { case (blob, i) => new BatchSourceFile(s"<blob-$i>", blob) }
      .flatMap { source =>
        val response = new Response[Tree]
        askLoadedTyped(source, false, response)
        val tree = response.get.fold(identity, throw _)
        toolbelt
          .analyze(tree)
          .map(_.map(_.fullName))
      }
  }

  val quotedPairs: List[(Tree, List[Dep[String]])] = List(
    q"class Foo" -> List(
      Dep.select("Foo.<init>", "java.lang.Object.<init>"),
      Dep.extend("Foo", "scala.AnyRef")
    ),
    q"class Foo; class Bar extends Foo" -> List(
      Dep.select("Bar.<init>", "Foo.<init>"),
      Dep.extend("Bar", "Foo"),
      Dep.select("Foo.<init>", "java.lang.Object.<init>"),
      Dep.extend("Foo", "scala.AnyRef")
    ),
    q"""
        trait Foo
        class Bar
        class Baz extends Bar with Foo
      """ -> List(
      Dep.select("Baz.<init>", "Bar.<init>"),
        Dep.extend("Baz", "Bar"),
        Dep.extend("Baz", "Foo"),
        Dep.select("Bar.<init>", "java.lang.Object.<init>"),
        Dep.extend("Bar", "scala.AnyRef"),
        Dep.extend("Foo", "scala.AnyRef"),
      )
  )

  quotedPairs.foreach { case (input, expected) =>
    property("quoted: " + showRaw(input)) =
      analyze(input) ?= expected
  }

  val parsedPairs: List[(List[String], List[Dep[String]])] = List(
    List(
      "trait Foo",
      "class Bar extends Foo",
    ) -> List(
      Dep.extend("Foo", "scala.AnyRef"),
      Dep.select("Bar.<init>", "java.lang.Object.<init>"),
      Dep.extend("Bar", "scala.AnyRef"),
      Dep.extend("Bar", "Foo"),
    ),

    List(
      "object Foo { val foo: Int = 1 }",
      "class Bar { println(Foo.foo) }",
    ) -> List(
      Dep.select("Foo.foo", "Foo.foo"),
      Dep.select("Foo.<init>", "java.lang.Object.<init>"),
      Dep.extend("Foo", "scala.AnyRef"),
      Dep.select("Bar", "Foo.foo"),
      Dep.select("Bar", "scala.Predef"),
      Dep.select("Bar", "scala.Predef.println"),
      Dep.select("Bar.<init>", "java.lang.Object.<init>"),
      Dep.extend("Bar","scala.AnyRef"),
    ),
  )

  parsedPairs.foreach { case (input, expected) =>
    property("parsed: " + input.mkString("\\n")) =
      analyze(input) ?= expected
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

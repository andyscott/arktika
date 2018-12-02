package io.higherkindness.arktika.osiris

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {
  final class Ops[F[_], A](fa: F[A])(implicit F: Functor[F]) {
    def map[B](f: A => B): F[B] = F.map(fa)(f)
  }
}

object `package` {
  implicit def toFunctorOps[F[_]: Functor, A](fa: F[A]): Functor.Ops[F, A] =
    new Functor.Ops[F, A](fa)
}

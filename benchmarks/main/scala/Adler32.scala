package scalax.hash
package benchmark

import org.scalameter.api._

import scalaz.std.stream._
import scalaz.syntax.foldable._

import scodec.bits.ByteVector

object Adler32Benchmark extends PerformanceTest {
  lazy val executor = LocalExecutor(new Executor.Warmer.Default, Aggregator.min, new Measurer.Default)
  lazy val reporter = new LoggingReporter
  lazy val persistor = Persistor.None

  val sizes = Gen.range("MB")(4,32,4)

  val streams = for (size <- sizes) yield
    Stream.fill(size)(Random.MB)

  val scodecs = for (size <- sizes) yield
    Stream.fill(size)(ByteVector(Random.MB))

  implicit val ScalazMonoid = scalaz.contrib.hash.adler32.Adler32Monoid
  val seqop  = (a: Adler32M, chunk: ByteVector) => ScalazMonoid.append(a,Adler32M(chunk))
  val combop = (a: Adler32M, b: Adler32M) => ScalazMonoid.append(a,b)

  val SpireMonoid = spire.contrib.hash.adler32.Adler32Monoid

  performance of "Adler32" in {
    measure method "update" in {
      using(streams) curve("java.util.zip") in { stream =>
        val a = new java.util.zip.Adler32
        for (chunk <- stream)
          a.update(chunk)
        a.getValue
      }

      using(scodecs) curve("scalax.hash with Array") in { stream =>
        var a = Adler32.empty
        for (chunk <- stream)
          a = a.update(chunk)
        a.value
      }

      // using(scodecs) curve("scalax.hash with ByteVector") in { stream =>
      //   var a = Adler32.empty
      //   for (chunk <- stream)
      //     a = a.update(chunk)
      //   a.hash
      // }

      using(scodecs) curve("scalaz Monoid with Stream.foldMap") in { stream =>
        val sa = stream.foldMap(Adler32M(_))
        sa.value
      }

      using(scodecs) curve("scalaz Monoid with Stream.map.suml") in { stream =>
        val sa = stream.map(Adler32M(_)).suml
        sa.value
      }

      using(scodecs) curve("scalaz Monoid with Stream.foldLeft") in { stream =>
        val sa = stream.foldLeft(ScalazMonoid.zero)(seqop)
        sa.value
      }

      using(scodecs) curve("spire Monoid with Stream.foldLeft") in { stream =>
        val sa = stream.foldLeft(SpireMonoid.id)((a,chunk) => SpireMonoid.op(a,Adler32M(chunk)))
        sa.value
      }
    }
  }

  performance of "Adler32" in {
    measure method "parallel update" in {
      using(streams) curve("java.util.zip") in { stream =>
        val a = new java.util.zip.Adler32
        for (chunk <- stream)
          a.update(chunk)
        a.getValue
      }

      using(scodecs) curve("scalaz Monoid with Stream.par.aggregate") in { stream =>
        val sa = stream.par.aggregate(ScalazMonoid.zero)(seqop,combop)
        sa.value
      }

      using(scodecs) curve("spire Monoid with Stream.par.aggregate") in { stream =>
        val sa = stream.par.aggregate(SpireMonoid.id)((a,chunk) => SpireMonoid.op(a,Adler32M(chunk)), SpireMonoid.op)
        sa.value
      }
    }
  }
}

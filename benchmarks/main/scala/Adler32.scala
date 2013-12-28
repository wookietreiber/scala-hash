package scalax.hash
package benchmark

import org.scalameter.api._

object Adler32Benchmark extends PerformanceTest {
  lazy val executor = LocalExecutor(new Executor.Warmer.Default, Aggregator.min, new Measurer.Default)
  lazy val reporter = new LoggingReporter
  lazy val persistor = Persistor.None

  val sizes = Gen.range("MB")(4,32,4)

  val streams = for (size <- sizes) yield
    Stream.fill(size)(Random.MB)

  performance of "java.util.zip.Adler32" in {
    measure method "update" in {
      using(streams) in { stream =>
        val a = new java.util.zip.Adler32
        for (chunk <- stream)
          a.update(chunk)
        a.getValue
      }
    }
  }

  performance of "scalax.hash.Adler32" in {
    measure method "update" in {
      using(streams) in { stream =>
        var a = Adler32.empty
        for (chunk <- stream)
          a = a.update(chunk)
        a.hash
      }
    }
  }

  performance of "scalaz.contrib.hash.adler32" in {
    implicit val monoid = scalaz.contrib.hash.adler32.Adler32Monoid
    val seqop  = (a: Adler32, chunk: Array[Byte]) => monoid.append(a,Adler32(chunk))
    val combop = (a: Adler32, b: Adler32) => monoid.append(a,b)

    measure method "Adler32Monoid with Stream.foldLeft" in {
      using(streams) in { stream =>
        val sa = stream.foldLeft(monoid.zero)(seqop)
        sa.hash
      }
    }

    measure method "Adler32Monoid with Stream.par.aggregate" in {
      using(streams) in { stream =>
        val sa = stream.par.aggregate(monoid.zero)(seqop,combop)
        sa.hash
      }
    }

    import scalaz.std.stream._
    import scalaz.syntax.foldable._

    measure method "Adler32Monoid with Stream.foldMap" in {
      using(streams) in { stream =>
        val sa = stream.foldMap(Adler32.apply)
        sa.hash
      }
    }

    measure method "Adler32Monoid with Stream.map.suml" in {
      using(streams) in { stream =>
        val sa = stream.map(Adler32.apply).suml
        sa.hash
      }
    }
  }

  performance of "spire.contrib.hash.adler32" in {
    val monoid = spire.contrib.hash.adler32.Adler32Monoid

    measure method "Adler32Monoid with Stream.foldLeft" in {
      using(streams) in { stream =>
        val sa = stream.foldLeft(monoid.id)((a,chunk) => monoid.op(a,Adler32(chunk)))
        sa.hash
      }
    }

    measure method "Adler32Monoid with Stream.par.aggregate" in {
      using(streams) in { stream =>
        val sa = stream.par.aggregate(monoid.id)((a,chunk) => monoid.op(a,Adler32(chunk)), monoid.op)
        sa.hash
      }
    }
  }
}

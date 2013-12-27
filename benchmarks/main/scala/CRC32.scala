package scalax.hash
package benchmark

import org.scalameter.api._

object CRC32Benchmark extends PerformanceTest {
  lazy val executor = LocalExecutor(new Executor.Warmer.Default, Aggregator.min, new Measurer.Default)
  lazy val reporter = new LoggingReporter
  lazy val persistor = Persistor.None

  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val datasets = for (size <- sizes) yield {
    val x = 0.toByte
    var a = Array.fill(size)(x)
    util.Random.nextBytes(a)
    a
  }

  performance of "java.util.zip.CRC32" in {
    measure method "update" in {
      using(datasets) in { data ⇒
        val a = new java.util.zip.CRC32
        a.update(data)
        a.getValue
      }
    }
  }

  performance of "scalax.hash.CRC32" in {
    measure method "update" in {
      using(datasets) in { data ⇒
        val a = CRC32(data)
        a.hash
      }
    }
  }
}

package scalax.hash
package benchmark

import org.scalameter.api._

object CRC32Benchmark extends PerformanceTest {
  lazy val executor = LocalExecutor(new Executor.Warmer.Default, Aggregator.min, new Measurer.Default)
  lazy val reporter = new LoggingReporter
  lazy val persistor = Persistor.None

  val sizes = Gen.range("MB")(4,32,4)

  val datasets = for (size <- sizes) yield
    Stream.fill(size)(Random.MB)

  performance of "java.util.zip.CRC32" in {
    measure method "update" in {
      using(datasets) in { data ⇒
        val a = new java.util.zip.CRC32
        for (chunk <- data)
          a.update(chunk)
        a.getValue
      }
    }
  }

  performance of "scalax.hash.CRC32" in {
    measure method "update" in {
      using(datasets) in { data ⇒
        var a = CRC32()
        for (chunk <- data)
          a = a.update(chunk)
        a.hash
      }
    }
  }
}

package scalax.hash
package benchmark

import org.scalameter.api._
import scalaz.contrib.hash._
import scalaz.std.stream._
import scalaz.syntax.foldable._
import scodec.bits.ByteVector

object CRC32Benchmark extends PerformanceTest with comb.CRC32Combination with ScalazHashCombinationModule {
  lazy val executor = LocalExecutor(new Executor.Warmer.Default, Aggregator.min, new Measurer.Default)
  lazy val reporter = Reporter.Composite(new LoggingReporter, ChartReporter(ChartFactory.XYLine()))
  lazy val persistor = Persistor.None

  val sizes = Gen.range("MB")(4,32,4)

  val streams = for (size <- sizes) yield
    Stream.fill(size)(Random.MB)

  val scodecs = for (size <- sizes) yield
    Stream.fill(size)(ByteVector.view(Random.MB))

  import HashCombination.{ combop, seqop }

  performance of "CRC32" in {
    measure method "update" in {
      using(streams) curve "java.util.zip" in { stream =>
        val a = new java.util.zip.CRC32
        for (chunk <- stream)
          a.update(chunk)
        a.getValue
      }

      using(scodecs) curve "scalax.hash" in { stream =>
        var a = Hash.empty
        for (chunk <- stream)
          a = a.update(chunk)
        a.value
      }

      using(scodecs) curve "Stream.foldMap" in { stream =>
        val sa = stream.foldMap(buf => HashCombination(buf))
        sa.value
      }

      using(scodecs) curve "Stream.par.aggregate" in { stream =>
        val sa = stream.par.aggregate(HashCombination.empty)(seqop,combop)
        sa.value
      }
    }
  }
}

package scalax.hash

import org.scalameter.api._
import scodec.bits.ByteVector

object Adler32Regression extends PerformanceTest.OfflineRegressionReport {
  val sizes = Gen.range("megabyte")(2,16,2)

  val streams = for (size <- sizes) yield
    Stream.fill(size)(ByteVector.view(Random.MB))

  performance of "Adler32" in {
    measure method "update" in {
      using(streams) config (
        exec.benchRuns -> 10,
        exec.independentSamples -> 2,
        exec.reinstantiation.frequency -> 1
      ) in { stream =>
        var a = Adler32.Hash.empty
        for (chunk <- stream)
          a = a.update(chunk)
        a.value
      }
    }
  }
}

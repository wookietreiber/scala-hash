package scalax.hash

import org.scalameter.api._
import scodec.bits.ByteVector

object CRC32Regression extends PerformanceTest.OfflineRegressionReport with CRC32 {
  val sizes = Gen.range("megabyte")(2,16,2)

  val streams = for (size <- sizes) yield
    Stream.fill(size)(ByteVector.view(Random.MB))

  performance of "CRC32" in {
    measure method "update" in {
      using(streams) config (
        exec.benchRuns -> 10,
        exec.independentSamples -> 2,
        exec.reinstantiation.frequency -> 1
      ) in { stream =>
        var a = Hash.empty
        for (chunk <- stream)
          a = a.update(chunk)
        a.value
      }
    }
  }
}

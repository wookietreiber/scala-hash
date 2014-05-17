package scalax.hash
package benchmark

import org.scalameter.api._
import scodec.bits.ByteVector

object Adler32Regression extends PerformanceTest.OnlineRegressionReport {
  val sizes = Gen.range("MB")(4,32,4)

  val streams = for (size <- sizes) yield
    Stream.fill(size)(ByteVector(Random.MB))

  performance of "scalax.hash.Adler32" in {
    measure method "update" in {
      using(streams) in { stream =>
        var a = Adler32.empty
        for (chunk <- stream)
          a = a.update(chunk)
        a.value
      }
    }
  }
}

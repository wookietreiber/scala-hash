package scalax.hash
package scalacheck

import org.scalacheck.Arbitrary

object arbitrary {
  implicit val Adler32Arbitrary: Arbitrary[Adler32] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[Array[Byte]]) yield
      Adler32(chunk)
  }

  implicit val CRC32Arbitrary: Arbitrary[CRC32] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[Array[Byte]]) yield
      CRC32(chunk)
  }
}

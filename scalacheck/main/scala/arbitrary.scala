package scalax.hash
package scalacheck

import org.scalacheck.Arbitrary
import scodec.bits.ByteVector

object arbitrary {
  implicit val ByteVectorArbitrary: Arbitrary[ByteVector] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[Array[Byte]]) yield
      ByteVector(chunk)
  }

  implicit val Adler32Arbitrary: Arbitrary[Adler32M] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[ByteVector]) yield
      Adler32M(chunk)
  }

  implicit val CRC32Arbitrary: Arbitrary[CRC32] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[Array[Byte]]) yield
      CRC32(chunk)
  }
}

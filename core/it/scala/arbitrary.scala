package scalax.hash

import org.scalacheck.Arbitrary
import scodec.bits.ByteVector

trait ArbitraryHashModule extends HashCombinationModule {
  implicit def ByteVectorArbitrary: Arbitrary[ByteVector] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[Array[Byte]]) yield
      ByteVector.view(chunk)
  }

  implicit def HashCombinationArbitrary: Arbitrary[HashCombination] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[ByteVector]) yield
      HashCombination(chunk)
  }
}

object arbitrary {
  implicit val ByteVectorArbitrary: Arbitrary[ByteVector] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[Array[Byte]]) yield
      ByteVector.view(chunk)
  }

  implicit val Adler32Arbitrary: Arbitrary[Adler32.HashCombination] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[ByteVector]) yield
      Adler32.HashCombination(chunk)
  }

  implicit val CRC32Arbitrary: Arbitrary[CRC32.HashCombination] = Arbitrary {
    for (chunk <- Arbitrary.arbitrary[ByteVector]) yield
      CRC32.HashCombination(chunk)
  }
}

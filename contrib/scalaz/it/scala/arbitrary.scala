package scalaz.contrib.hash

import org.scalacheck.Arbitrary
import scodec.bits.ByteVector
import scalax.hash.comb.HashCombinationModule

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

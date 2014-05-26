package scalax.hash
package comb

import scodec.bits.ByteVector

/** A stand-alone, concrete $hash combination module. */
object Adler32Combination extends Adler32Combination

/** A concrete $hash combination module.
  *
  * @define hash 32-bit adler
  */
trait Adler32Combination extends HashCombinationModule with Adler32 {

  /** A $hash combination. */
  case class HashCombination private[hash] (private val underlying: Hash, private val fed: Int) extends HashCombinationLike {
    def value: ByteVector =
      underlying.value

    def update(that: HashCombination): HashCombination = {
      val length = that.fed
      val remainder = length % 65521
      var s1 = underlying.a
      var s2 = remainder * s1

      s2 %= 65521
      s1 += that.underlying.a + 65520
      s2 += (underlying.b & 65535) + (that.underlying.b & 65535) + 65521 - remainder

      if (s1 >= 131042)
        s1 -= 131042
      else if (s1 >= 65521)
        s1 -= 65521

      if (s2 >= (65521 << 1))
        s2 -= 65521 << 1

      if (s2 >= 65521)
        s2 -= 65521

      new HashCombination(new Hash(s1, s2), this.fed + that.fed)
    }
  }

  object HashCombination extends HashCombinationCompanion {
    val empty: HashCombination = new HashCombination(Hash.empty, 0)

    def apply(data: ByteVector): HashCombination =
      new HashCombination(Hash(data), data.length)
  }

}

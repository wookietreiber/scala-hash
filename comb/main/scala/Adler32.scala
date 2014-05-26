package scalax.hash
package comb

import scodec.bits.ByteVector

/** A stand-alone, concrete 32-bit adler hash module. */
object Adler32Combination extends Adler32Combination

/** A concrete 32-bit adler hash module.
  *
  * @define hash 32-bit adler
  */
trait Adler32Combination extends HashCombinationModule {

  case class HashCombination private[hash] (private val adler32: Hash, private val fed: Int) extends HashCombinationLike {
    def value: ByteVector =
      adler32.value

    def update(that: HashCombination): HashCombination = {
      val length = that.fed
      val remainder = length % 65521
      var s1 = adler32.a
      var s2 = remainder * s1

      s2 %= 65521
      s1 += that.adler32.a + 65520
      s2 += (adler32.b & 65535) + (that.adler32.b & 65535) + 65521 - remainder

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

  object HashCombination extends HashCombinationFactory {
    val empty: HashCombination = new HashCombination(Hash.empty, 0)

    def apply(data: ByteVector): HashCombination =
      new HashCombination(Hash(data), data.length)

    val seqop  = (a: HashCombination, chunk: ByteVector) => a update HashCombination(chunk)
    val combop = (a: HashCombination, b: HashCombination) => a update b
  }

}

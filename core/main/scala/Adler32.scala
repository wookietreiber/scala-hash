package scalax.hash

import scodec.bits.ByteVector

/** A stand-alone, concrete 32-bit adler hash module. */
object Adler32 extends Adler32

/** A concrete 32-bit adler hash module.
  *
  * @define hash 32-bit adler
  */
trait Adler32 extends HashCombinationModule {

  /** A 32-bit adler hash. */
  case class Hash private[hash] (private[hash] val a: Long, private[hash] val b: Long) extends HashLike {
    def value: ByteVector = ByteVector (
      (b >>> 8).toByte, b.toByte, (a >>> 8).toByte, a.toByte
    )

    def update(data: ByteVector): Hash = {
      val length = data.length
      var s1 = a
      var s2 = b
      var index = 0

      if (length == 1) {
        s1 += data(index) & 255
        s2 += s1
        index += 1
        s1 %= 65521L
        s2 %= 65521L
      } else {
        var l1 = length / 5552L
        var l2 = length % 5552L

        while (l1 > 0) {
          l1 -= 1

          var k = 5552
          while (k > 0) {
            k -= 1
            s1 += data(index) & 255
            s2 += s1
            index += 1
          }

          s1 %= 65521L
          s2 %= 65521L
        }

        while (l2 > 0) {
          l2 -= 1
          s1 += data(index) & 255
          s2 += s1
          index += 1
        }

        s1 %= 65521L
        s2 %= 65521L
      }

      new Hash(s1, s2)
    }
  }

  object Hash extends HashFactory {
    val empty: Hash = new Hash(1L, 0L)
  }

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

    import scalaz._

    implicit def HCMonoid: Monoid[HashCombination] = new Monoid[HashCombination] {
      override val zero = HashCombination.empty
      override def append(x: HashCombination, y: => HashCombination): HashCombination =
        x update y
    }

    implicit def HCEqual: Equal[HashCombination] =
      Equal.equal(_.value == _.value)

    implicit def HCShow: Show[HashCombination] =
      Show.showFromToString
  }

}

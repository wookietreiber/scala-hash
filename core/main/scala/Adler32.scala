package scalax.hash

import scodec.bits.ByteVector

/** A stand-alone, concrete 32-bit adler hash module. */
object Adler32 extends Adler32

/** A concrete 32-bit adler hash module.
  *
  * @define hash 32-bit adler
  */
trait Adler32 extends HashModule {

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

    val seqop  = (a: Hash, chunk: ByteVector) => a update chunk
  }

}

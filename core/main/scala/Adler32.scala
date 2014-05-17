package scalax.hash

import scodec.bits.ByteVector

/** An Adler32 checksum. */
final case class Adler32 private[hash] (private[hash] val a: Long, private[hash] val b: Long) {

  /** Returns the checksum value. */
  def value: Long =
    (a | (b << 16)).toLong

  /** Updates this Adler32 with new data. */
  @inline def update(data: ByteVector): Adler32 = {
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

    new Adler32(s1, s2)
  }

}

/** Adler32 factory. */
object Adler32 {

  /** Returns the empty Adler32. */
  val empty: Adler32 = new Adler32(1L, 0L)

  /** Returns the Adler32 of the given data buffer. */
  @inline def apply(data: ByteVector): Adler32 =
    empty.update(data)

}

final case class Adler32M private (private val adler32: Adler32, private val fed: Int) {
  /** Returns the checksum value. */
  def value: Long = adler32.value

  /** Updates this Adler32 with another one. */
  def update(that: Adler32M): Adler32M = {
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

    new Adler32M(new Adler32(s1, s2), this.fed + that.fed)
  }
}

object Adler32M {
  /** Returns the empty Adler32. */
  val empty: Adler32M = new Adler32M(Adler32.empty, 0)

  /** Returns the Adler32 of the given data buffer. */
  @inline def apply(data: ByteVector): Adler32M =
    new Adler32M(Adler32(data), data.length)
}

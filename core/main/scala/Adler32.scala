package scalax.hash

/** An Adler32 checksum.
  *
  * @param hash Returns the checksum value.
  */
final case class Adler32 private (hash: Long) (private val fed: Long) {

  @inline private def split: (Long,Long) = {
    val s1 = hash & 65535
    val s2 = (hash >> 16) & 65535
    (s1,s2)
  }

  /** Updates this Adler32 with new data.
    *
    * @param data the data to update this Adler32 with
    */
  @inline def update(data: Array[Byte]): Adler32 =
    update(data, 0, data.length)

  /** Updates this Adler32 with new data.
    *
    * @param data the data to update this Adler32 with
    * @param start the start index to use from the data buffer
    * @param length the amount of bytes to use from the data buffer
    */
  def update(data: Array[Byte], start: Int, length: Int): Adler32 = {
    var (s1,s2) = split
    var index = start

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

    Adler32.join(s1, s2, fed + length)
  }

  /** Updates this Adler32 with another one.
    *
    * @param that the Adler32 to update with
    */
  def update(that: Adler32): Adler32 = {
    val length = that.fed
    val h1 = hash
    val h2 = that.hash

    val remainder = length % 65521L
    var s1 = h1 & 65535
    var s2 = remainder * s1

    s2 %= 65521L
    s1 += (h2 & 65535) + 65521L - 1L
    s2 += ((h1 >> 16) & 65535) + ((h2 >> 16) & 65535) + 65521L - remainder

    if (s1 >= 65521L)
      s1 -= 65521L

    if (s1 >= 65521L)
      s1 -= 65521L

    if (s2 >= (65521L << 1))
      s2 -= (65521L << 1)

    if (s2 >= 65521L)
      s2 -= 65521L

    Adler32.join(s1, s2, this.fed + that.fed)
  }

}

/** Adler32 factory. */
object Adler32 {

  /** Returns the empty Adler32. */
  val empty: Adler32 = new Adler32(1L)(0L)

  /** Returns the empty Adler32. */
  @inline def apply(): Adler32 = empty

  /** Returns the Adler32 of the given data buffer.
    *
    * @param data the data buffer from which to build the Adler32
    */
  @inline def apply(data: Array[Byte]): Adler32 =
    empty.update(data)

  @inline private def join(s1: Long, s2: Long, fed: Long): Adler32 =
    new Adler32(s1 | (s2 << 16))(fed)

}

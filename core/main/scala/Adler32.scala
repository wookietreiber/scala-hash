package scalax.hash

/** An Adler32 checksum.
  *
  * @param hash Returns the checksum value.
  */
final case class Adler32 private (hash: Long) {

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

    @inline def bump: Unit = {
      s1 += data(index) & 255
      s2 += s1
      index += 1
    }

    @inline def mod: Unit = {
      s1 %= 65521L
      s2 %= 65521L
    }

    if (length == 1) {
      bump
      mod
    } else {
      var l1 = length / 5552L
      var l2 = length % 5552L

      while (l1 > 0) {
        l1 -= 1

        var k = 5552
        while (k > 0) {
          k -= 1
          bump
        }

        mod
      }

      while (l2 > 0) {
        l2 -= 1
        bump
      }

      mod
    }

    Adler32.join(s1, s2)
  }

  /** Combines this Adler32 with another one.
    *
    * @param that the Adler32 to combine with
    * @param length the amount of data that has been fed to `that` Adler32
    */
  def combine(that: Adler32, length: Int): Adler32 = {
    val h1 = hash
    val h2 = that.hash

    val remainder = length % 65521L
    var sum1 = h1 & 65535
    var sum2 = remainder * sum1

    sum2 %= 65521L
    sum1 += (h2 & 65535) + 65521L - 1L
    sum2 += ((h1 >> 16) & 65535) + ((h2 >> 16) & 65535) + 65521L - remainder

    if (sum1 >= 65521L)
      sum1 -= 65521L

    if (sum1 >= 65521L)
      sum1 -= 65521L

    if (sum2 >= (65521L << 1))
      sum2 -= (65521L << 1)

    if (sum2 >= 65521L)
      sum2 -= 65521L

    Adler32.join(sum1,sum2)
  }

}

/** Adler32 factory. */
object Adler32 {

  /** Returns an empty Adler32. */
  val empty: Adler32 = new Adler32(1L)

  /** Returns an empty Adler32. */
  @inline def apply(): Adler32 = empty

  /** Returns the Adler32 of the given data buffer.
    *
    * @param data the data buffer from which to build the Adler32
    */
  @inline def apply(data: Array[Byte]): Adler32 =
    empty.update(data)

  @inline private def join(s1: Long, s2: Long): Adler32 =
    new Adler32(s1 | (s2 << 16))

}

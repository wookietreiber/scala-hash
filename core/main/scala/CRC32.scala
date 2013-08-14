package scalax.hash

/** A CRC32 cyclic redundancy check.
  *
  * @param hash Returns the CRC value.
  */
final case class CRC32 private (hash: Long) {

  /** Updates this CRC32 with new data.
    *
    * @param data the data to update this CRC32 with
    */
  @inline def update(data: Array[Byte]): CRC32 =
    update(data, 0, data.length)

  /** Updates this CRC32 with new data.
    *
    * @param data the data to update this CRC32 with
    * @param start the start index to use from the data buffer
    * @param length the amount of bytes to use from the data buffer
    */
  def update(data: Array[Byte], start: Int, length: Int): CRC32 = {
    var v = (hash & 4294967295L).toInt
    var c = ~v
    var i = start
    var len = length - 1

    while (len >= 0) {
      c = CRC32.table((c ^ data(i)) & 255) ^ (c >>> 8)
      i += 1
      len -= 1
    }

    new CRC32((~c).toLong & 4294967295L)
  }

  /** Combines this CRC32 with another one.
    *
    * @param that the CRC32 to combine with
    * @param length the amount of data that has been fed to `that` CRC32
    */
  def combine(that: CRC32, length: Long): CRC32 = if (length <= 0) {
    this
  } else {
    var row = 1L

    var crc1 = this.hash
    var crc2 = that.hash
    var len2 = length

    var odd  = Array.tabulate(CRC32.GF2_DIM) { n ⇒
      if (n == 0)
        3988292384L
      else {
        val o = row
        row <<= 1
        o
      }
    }

    var even = CRC32.gf2MatrixSquare(odd)
    odd = CRC32.gf2MatrixSquare(even)

    do {
      even = CRC32.gf2MatrixSquare(odd)

      if ((len2 & 1) != 0)
        crc1 = CRC32.gf2MatrixTimes(even, crc1)

      len2 >>= 1

      if (len2 == 0) {
        crc1 ^= crc2
        return new CRC32(crc1)
      }

      odd = CRC32.gf2MatrixSquare(even)

      if ((len2 & 1) != 0)
        crc1 = CRC32.gf2MatrixTimes(odd, crc1)

      len2 >>= 1
    } while (len2 != 0)

    crc1 ^= crc2
    new CRC32(crc1)
  }

}

/** CRC32 factory. */
object CRC32 {

  /** Returns an empty CRC32. */
  val empty: CRC32 = new CRC32(0L)

  /** Returns an empty CRC32. */
  @inline def apply(): CRC32 = empty

  /** Returns the CRC32 of the given data buffer.
    *
    * @param data the data buffer from which to build the CRC32
    */
  @inline def apply(data: Array[Byte]): CRC32 =
    empty.update(data)

  private val table: Array[Int] = Array.tabulate(256) { n ⇒
    var c = n
    var k = 7

    while (k >= 0) {
      if ((c & 1) != 0)
        c = -306674912 ^ (c >>> 1)
      else
        c = c >>> 1

      k -= 1
    }

    c
  }

  private val GF2_DIM = 32

  @inline private def gf2MatrixTimes(mat: Array[Long], v: Long): Long = {
    var vec = v
    var sum = 0L
    var index = 0

    while (vec != 0) {
      if ((vec & 1) != 0)
        sum ^= mat(index)

      vec >>= 1
      index += 1
    }

    sum
  }

  @inline private def gf2MatrixSquare(mat: Array[Long]): Array[Long] =
    Array.tabulate(GF2_DIM)(i ⇒ gf2MatrixTimes(mat, mat(i)))

}

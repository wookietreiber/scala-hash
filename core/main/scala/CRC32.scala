package scalax.hash

import scodec.bits.ByteVector

/** A stand-alone, concrete 32-bit cyclic redundancy check module. */
final object CRC32 extends CRC32 {
  private val table: Array[Int] = Array.tabulate(256) { n =>
    var c = n
    var k = 7

    while (k >= 0) {
      c = if ((c & 1) != 0)
        (c >>> 1) ^ -306674912
      else
        c >>> 1

      k -= 1
    }

    c
  }
}

/** A concrete 32-bit cyclic redundancy check module.
  *
  * @define hash 32-bit cyclic redundancy check
  */
trait CRC32 extends HashCombinationModule {

  /** A 32-bit cyclic redundancy check. */
  case class Hash private[hash] (private[hash] val hash: Int) extends HashLike {

    def value: ByteVector = {
      val negated = ~hash
      ByteVector (
        (negated >>> 24).toByte, (negated >>> 16).toByte, (negated >>> 8).toByte, negated.toByte
      )
    }

    def update(data: ByteVector): Hash = {
      val len = data.size
      var c = hash
      var i = 0
      var xxx = 0

      while (i < len) {
        c = CRC32.table((c ^ data(i)) & 255) ^ (c >>> 8)
        i += 1
      }

      new Hash(c)
    }
  }

  object Hash extends HashFactory {
    val empty: Hash = new Hash(-1)
  }

  case class HashCombination private[hash] (private val underlying: Hash, private val fed: Int) extends HashCombinationLike {
    def value: ByteVector =
      underlying.value

    def update(that: HashCombination): HashCombination = if (that.fed == 0) {
      this
    } else {
      var row = 1L

      var crc1 = (~this.underlying.hash).toLong & 4294967295L
      var crc2 = (~that.underlying.hash).toLong & 4294967295L
      var length = that.fed

      var odd = Array.tabulate(HashCombination.GF2_DIM) { n =>
        if (n == 0)
          3988292384L
        else {
          val o = row
          row <<= 1
          o
        }
      }

      var even = HashCombination.gf2MatrixSquare(odd)
      odd = HashCombination.gf2MatrixSquare(even)

      do {
        even = HashCombination.gf2MatrixSquare(odd)

        if ((length & 1) != 0)
          crc1 = HashCombination.gf2MatrixTimes(even, crc1)

        length >>= 1

        if (length == 0) {
          crc1 ^= crc2
          val foo = ~((crc1 & 4294967295L).toInt)
          return HashCombination(new Hash(foo), this.fed + that.fed)
        }

        odd = HashCombination.gf2MatrixSquare(even)

        if ((length & 1) != 0)
          crc1 = HashCombination.gf2MatrixTimes(odd, crc1)

        length >>= 1
      } while (length != 0)

      crc1 ^= crc2
      val foo = ~((crc1 & 4294967295L).toInt)
      HashCombination(Hash(foo), this.fed + that.fed)
    }
  }

  object HashCombination extends HashCombinationFactory {
    val empty: HashCombination = new HashCombination(Hash.empty, 0)

    def apply(data: ByteVector): HashCombination =
      new HashCombination(Hash(data), data.length)

    private val GF2_DIM = 32

    private def gf2MatrixTimes(mat: Array[Long], v: Long): Long = {
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

    private def gf2MatrixSquare(mat: Array[Long]): Array[Long] =
      Array.tabulate(GF2_DIM)(i => gf2MatrixTimes(mat, mat(i)))

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

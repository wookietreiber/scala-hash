package scalax.hash
package comb

import scodec.bits.ByteVector

/** A stand-alone, concrete 32-bit cyclic redundancy check module. */
object CRC32Combination extends CRC32Combination

/** A concrete 32-bit cyclic redundancy check module.
  *
  * @define hash 32-bit cyclic redundancy check
  */
trait CRC32Combination extends HashCombinationModule with CRC32 {

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
  }

}

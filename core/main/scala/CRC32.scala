package scalax.hash

import scodec.bits.ByteVector

/** A stand-alone, concrete 32-bit cyclic redundancy check module. */
object CRC32 extends CRC32 {
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
trait CRC32 extends HashModule {

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

    val seqop  = (a: Hash, chunk: ByteVector) => a update chunk
  }

}

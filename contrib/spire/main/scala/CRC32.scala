package spire.contrib
package hash

import spire.algebra._

object crc32 extends CRC32Instances

trait CRC32Instances {

  type CRC32 = scalax.hash.CRC32
  val  CRC32 = scalax.hash.CRC32

  implicit val CRC32Eq: Eq[CRC32] = new Eq[CRC32] {
    override def eqv(x: CRC32, y: CRC32): Boolean =
      x == y
  }

  implicit val CRC32Monoid: Monoid[CRC32] = new Monoid[CRC32] {
    override val id = CRC32.empty
    override def op(x: CRC32, y: CRC32): CRC32 =
      x update y
  }

}

package spire.contrib
package hash

import scalax.hash.CRC32

import spire.algebra.Monoid

object crc32 extends CRC32Instances

trait CRC32Instances {

  implicit val CRC32Monoid: Monoid[CRC32] = new Monoid[CRC32] {
    override val id = CRC32.empty
    override def op(x: CRC32, y: CRC32): CRC32 =
      x update y
  }

}

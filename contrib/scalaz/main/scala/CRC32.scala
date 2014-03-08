package scalaz.contrib
package hash

import scalaz._

object crc32 extends CRC32Instances

trait CRC32Instances {

  type CRC32 = scalax.hash.CRC32
  val  CRC32 = scalax.hash.CRC32

  implicit val CRC32Equal: Equal[CRC32] =
    Equal.equalA

  implicit val CRC32Monoid: Monoid[CRC32] = new Monoid[CRC32] {
    override val zero = CRC32.empty
    override def append(x: CRC32, y: => CRC32): CRC32 =
      x update y
  }

  implicit val CRC32Show: Show[CRC32] =
    Show.showFromToString

}

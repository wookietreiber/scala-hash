package spire.contrib
package hash

import scalax.hash.CRC32

import spire.algebra.Monoid

object crc32 extends CRC32Instances

trait CRC32Instances {

  implicit val CRC32CombinationMonoid: Monoid[(CRC32,Int)] = new Monoid[(CRC32,Int)] {
    override val id = (CRC32(),0)
    override def op(x: (CRC32,Int), y: (CRC32,Int)): (CRC32,Int) =
      (x._1.combine(y._1, y._2), x._2 + y._2)
  }

}

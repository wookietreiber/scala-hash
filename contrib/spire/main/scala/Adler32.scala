package spire.contrib
package hash

import scalax.hash.Adler32

import spire.algebra.Monoid

object adler32 extends Adler32Instances

trait Adler32Instances {

  implicit val Adler32CombinationMonoid: Monoid[(Adler32,Int)] = new Monoid[(Adler32,Int)] {
    override val id = (Adler32(),0)
    override def op(x: (Adler32,Int), y: (Adler32,Int)): (Adler32,Int) =
      (x._1.combine(y._1, y._2), x._2 + y._2)
  }

}

package scalaz.contrib
package hash
package checksum

import scalax.hash.checksum.Adler32

import scalaz.Monoid

object adler32 extends Adler32Instances

trait Adler32Instances {

  implicit val Adler32CombinationMonoid: Monoid[(Adler32,Int)] = new Monoid[(Adler32,Int)] {
    val zero = (Adler32(),0)
    def append(x: (Adler32,Int), y: ⇒ (Adler32,Int)): (Adler32,Int) = {
      (x._1.combine(y._1, y._2), x._2 + y._2)
    }
  }

}

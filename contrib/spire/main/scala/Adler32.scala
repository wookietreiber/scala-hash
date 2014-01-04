package spire.contrib
package hash

import scalax.hash.Adler32

import spire.algebra._

object adler32 extends Adler32Instances

trait Adler32Instances {

  implicit val Adler32Eq: Eq[Adler32] = new Eq[Adler32] {
    override def eqv(x: Adler32, y: Adler32): Boolean =
      x == y
  }

  implicit val Adler32Monoid: Monoid[Adler32] = new Monoid[Adler32] {
    override val id = Adler32.empty
    override def op(x: Adler32, y: Adler32): Adler32 =
      x update y
  }

}

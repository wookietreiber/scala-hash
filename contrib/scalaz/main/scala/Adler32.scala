package scalaz.contrib
package hash

import scalaz._

object adler32 extends Adler32Instances

trait Adler32Instances {

  type Adler32 = scalax.hash.Adler32
  val  Adler32 = scalax.hash.Adler32

  implicit val Adler32Equal: Equal[Adler32] =
    Equal.equalA

  implicit val Adler32Monoid: Monoid[Adler32] = new Monoid[Adler32] {
    override val zero = Adler32.empty
    override def append(x: Adler32, y: => Adler32): Adler32 =
      x update y
  }

  implicit val Adler32Show: Show[Adler32] =
    Show.showFromToString

}

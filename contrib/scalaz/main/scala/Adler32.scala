package scalaz.contrib
package hash

import scalax.hash._
import scalaz._

trait ScalazHashModule extends HashCombinationModule {

  implicit def HashEqual: Equal[Hash] =
    Equal.equal(_.value == _.value)

  implicit def HashCombinationEqual: Equal[HashCombination] =
    Equal.equal(_.value == _.value)

  implicit def HashCombinationMonoid: Monoid[HashCombination] = new Monoid[HashCombination] {
    override val zero = HashCombination.empty
    override def append(x: HashCombination, y: => HashCombination): HashCombination =
      x update y
  }

  implicit def HashShow: Show[Hash] =
    Show.showFromToString

  implicit def HashCombinationShow: Show[HashCombination] =
    Show.showFromToString

}

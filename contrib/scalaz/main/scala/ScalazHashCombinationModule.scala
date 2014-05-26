package scalaz.contrib
package hash

import scalax.hash.comb.HashCombinationModule
import scalaz._

trait ScalazHashCombinationModule extends HashCombinationModule {

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

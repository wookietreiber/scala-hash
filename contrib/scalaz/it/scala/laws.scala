package scalaz.contrib.hash

import org.scalacheck._
import scalax.hash.comb._
import scalaz.scalacheck.ScalazProperties._

trait HashSpec extends ScalazHashCombinationModule with ArbitraryHashModule {
  self: Properties =>

  for ((name, prop) <- equal.laws[HashCombination].properties) yield {
    property(name) = prop
  }

  for ((name, prop) <- monoid.laws[HashCombination].properties) yield {
    property(name) = prop
  }
}

object Adler32Spe extends Properties("Adler32") with HashSpec with Adler32Combination

object CRC32Spec extends Properties("CRC32") with HashSpec with CRC32Combination

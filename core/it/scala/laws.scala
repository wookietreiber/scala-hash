package scalax.hash

import org.scalacheck._
import scalaz.scalacheck.ScalazProperties._

import arbitrary._
import Adler32._

object Adler32Spec extends Properties("Adler32") {
  for ((name, prop) <- equal.laws[HashCombination].properties) yield {
    property(name) = prop
  }

  for ((name, prop) <- monoid.laws[HashCombination].properties) yield {
    property(name) = prop
  }
}

// object CRC32Spec extends Properties("CRC32") with CRC32Instances {
//   for ((name, prop) <- equal.laws[CRC32].properties) yield {
//     property(name) = prop
//   }

//   for ((name, prop) <- monoid.laws[CRC32].properties) yield {
//     property(name) = prop
//   }
// }

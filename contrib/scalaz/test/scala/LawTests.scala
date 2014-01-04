package scalaz.contrib
package hash

import org.scalacheck._
import scalaz.scalacheck.ScalazProperties._

import scalax.hash.scalacheck.arbitrary._

object Adler32Spec extends Properties("Adler32") with Adler32Instances {
  for ((name, prop) <- equal.laws[Adler32].properties) yield {
    property(name) = prop
  }

  for ((name, prop) <- monoid.laws[Adler32].properties) yield {
    property(name) = prop
  }
}

object CRC32Spec extends Properties("CRC32") with CRC32Instances {
  for ((name, prop) <- equal.laws[CRC32].properties) yield {
    property(name) = prop
  }

  for ((name, prop) <- monoid.laws[CRC32].properties) yield {
    property(name) = prop
  }
}

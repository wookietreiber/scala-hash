package spire.contrib
package hash

import org.scalacheck._
import spire.laws._

import scalax.hash.scalacheck.arbitrary._

object Adler32Spec extends Properties("Adler32") with Adler32Instances {
  for ((name, prop) <- GroupLaws[Adler32].monoid.all.properties) yield {
    property(name) = prop
  }
}

object CRC32Spec extends Properties("CRC32") with CRC32Instances {
  for ((name, prop) <- GroupLaws[CRC32].monoid.all.properties) yield {
    property(name) = prop
  }
}

package scalax.hash
package comb

import java.util.zip.{ Adler32 => JAdler32 }
import org.specs2._
import scodec.bits.ByteVector

class Adler32CombinationSpec extends Specification with Adler32Combination { def is = s2"""

  Adler32 Combination Specification

  (init) not feeding with bytes must be equal to the default implementation               $e0
  (checksum) must be equal to the default implementation                                  $e1
                                                                                                 """
  // -----------------------------------------------------------------------------------------------
  // tests
  // -----------------------------------------------------------------------------------------------

  def e0 = compare(HashCombination.empty, new JAdler32)

  def e1 = {
    var a = new Array[Byte](100000)
    var b = new Array[Byte](100000)

    util.Random.nextBytes(a)
    util.Random.nextBytes(b)

    val hca = HashCombination(ByteVector.view(a))
    val hcb = HashCombination(ByteVector.view(b))

    compare(hca update hcb, JAdler32(a,b))
  }

  // -----------------------------------------------------------------------------------------------
  // util
  // -----------------------------------------------------------------------------------------------

  def compare(sa: HashCombination, ja: JAdler32) = compare0(sa.value, ja.getValue)

  def compare0(bv: ByteVector, l: Long) = {
    scodec.codecs.ulong(32).decode(bv.bits).map(_._2 == l).toOption must beSome(true)
  }

  def JAdler32(bufs: Array[Byte]*) = {
    val ja = new JAdler32
    bufs foreach ja.update
    ja
  }

}

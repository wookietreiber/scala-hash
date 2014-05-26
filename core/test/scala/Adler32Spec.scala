package scalax.hash

import java.util.zip.{ Adler32 => JAdler32 }
import org.specs2._
import scodec.bits.ByteVector

class Adler32Spec extends Specification { def is = s2"""

  Adler32 Specification

  (init) not feeding with bytes must be equal to the default implementation               $e0
  (checksum) must be equal to the default implementation (small, i.e. no delay anyway)    $e1
  (checksum) must be equal to the default implementation (large, i.e. delay)              $e2
                                                                                                 """
  // -----------------------------------------------------------------------------------------------
  // tests
  // -----------------------------------------------------------------------------------------------

  def e0 = compare(Adler32.Hash.empty, new JAdler32)

  def e1 = {
    var a = new Array[Byte](1000)
    util.Random.nextBytes(a)
    compare(Adler32.Hash(ByteVector.view(a)), JAdler32(a))
  }

  def e2 = {
    var a = new Array[Byte](100000)
    util.Random.nextBytes(a)
    compare(Adler32.Hash(ByteVector.view(a)), JAdler32(a))
  }

  // -----------------------------------------------------------------------------------------------
  // util
  // -----------------------------------------------------------------------------------------------

  def compare(sa: Adler32.Hash, ja: JAdler32) = compare0(sa.value, ja.getValue)

  def compare0(bv: ByteVector, l: Long) = {
    scodec.codecs.ulong(32).decode(bv.bits).map(_._2 == l).toOption must beSome(true)
  }

  def JAdler32(buf: Array[Byte]) = {
    val ja = new JAdler32
    ja.update(buf)
    ja
  }

  def JAdler32(bufs: scala.collection.GenTraversableOnce[Array[Byte]]) = {
    val ja = new JAdler32
    bufs.seq foreach ja.update
    ja
  }

}

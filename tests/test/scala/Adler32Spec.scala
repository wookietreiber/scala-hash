package scalax.hash

import java.util.zip.{ Adler32 => JAdler32 }
import org.specs2._
import scodec.bits.ByteVector

class Adler32Spec extends Specification with ScalaCheck { def is = s2"""

  Adler32 Specification

  (init) not feeding with bytes must be equal to the default implementation               $e0
  (checksum) must be equal to the default implementation (small, i.e. no delay anyway)    $e1
  (checksum) must be equal to the default implementation (large, i.e. delay)              $e2
  (checksum) combination should be equal to the single run                                $e3
  (scalaz combination monoid) must be equal to the default implementation                 $e4
                                                                                                 """

  // -----------------------------------------------------------------------------------------------
  // tests
  // -----------------------------------------------------------------------------------------------

  def e0 = compare(Adler32.Hash.empty, new JAdler32)

  def e1 = prop { (a: Array[Byte]) =>
    compare(Adler32.Hash(ByteVector.view(a)), JAdler32(a))
  }

  def e2 = prop { (a: Array[Byte]) =>
    compare(Adler32.Hash(ByteVector.view(a)), JAdler32(a))
  }.set(minTestsOk = 20, minSize = 10000, maxSize = 100000)

  def e3 = prop { (a: Array[Byte], b: Array[Byte]) =>
    compare(Adler32.HashCombination(ByteVector.view(a)) update Adler32.HashCombination(ByteVector.view(b)), JAdler32(a ++ b))
  }

  def e4 = prop { (bufs: Stream[Array[Byte]]) =>
    import scalaz.std.stream._
    import scalaz.syntax.foldable._

    val sa = bufs.foldMap(buf => Adler32.HashCombination(ByteVector.view(buf)))

    compare(sa, JAdler32(bufs))
  }

  // -----------------------------------------------------------------------------------------------
  // util
  // -----------------------------------------------------------------------------------------------

  def compare(sa: Adler32.Hash, ja: JAdler32) = compare0(sa.value, ja.getValue)
  def compare(sa: Adler32.HashCombination, ja: JAdler32) = compare0(sa.value, ja.getValue)

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

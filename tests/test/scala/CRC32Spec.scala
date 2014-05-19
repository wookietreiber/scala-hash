package scalax.hash

import java.util.zip.{ CRC32 => JCRC32 }

import org.specs2._
import scodec.bits.ByteVector

class CRC32Spec extends Specification with ScalaCheck { def is = s2"""

  CRC32 Specification

  (init) not feeding with bytes must be equal to the default implementation               $e0
  (checksum) must be equal to the default implementation (small, i.e. no delay anyway)    $e1
  (checksum) must be equal to the default implementation (large, i.e. delay)              $e2
  (checksum) combination should be equal to the single run                                $e3
  (scalaz combination monoid) must be equal to the default implementation                 $e4
                                                                                                 """

  // -----------------------------------------------------------------------------------------------
  // tests
  // -----------------------------------------------------------------------------------------------

  def e0 = compare(CRC32.Hash.empty, new JCRC32)

  def e1 = prop { (a: Array[Byte]) =>
    compare(CRC32.Hash(ByteVector.view(a)), JCRC32(a))
  }

  def e2 = prop { (a: Array[Byte]) =>
    compare(CRC32.Hash(ByteVector.view(a)), JCRC32(a))
  }.set(minTestsOk = 20, minSize = 10000, maxSize = 100000)

  def e3 = prop { (a: Array[Byte], b: Array[Byte]) =>
    compare(CRC32.HashCombination(ByteVector.view(a)) update CRC32.HashCombination(ByteVector.view(b)), JCRC32(a ++ b))
  }

  def e4 = prop { (bufs: Stream[Array[Byte]]) =>
    import scalaz.std.stream._
    import scalaz.syntax.foldable._

    val scrc = bufs.foldMap(buf => CRC32.HashCombination(ByteVector.view(buf)))

    compare(scrc, JCRC32(bufs))
  }

  // -----------------------------------------------------------------------------------------------
  // util
  // -----------------------------------------------------------------------------------------------

  def compare(sa: CRC32.Hash, ja: JCRC32) = compare0(sa.value, ja.getValue)
  def compare(sa: CRC32.HashCombination, ja: JCRC32) = compare0(sa.value, ja.getValue)

  def compare0(bv: ByteVector, l: Long) = {
    scodec.codecs.ulong(32).decode(bv.bits).map(_._2 == l).toOption must beSome(true)
  }

  def JCRC32(buf: Array[Byte]) = {
    val jcrc = new JCRC32
    jcrc.update(buf)
    jcrc
  }

  def JCRC32(bufs: scala.collection.GenTraversableOnce[Array[Byte]]) = {
    val jcrc = new JCRC32
    bufs.seq foreach jcrc.update
    jcrc
  }

}

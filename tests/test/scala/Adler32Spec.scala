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
  (spire combination monoid) must be equal to the default implementation                  $e5
                                                                                                 """

  // -----------------------------------------------------------------------------------------------
  // tests
  // -----------------------------------------------------------------------------------------------

  def e0 = compare(Adler32.empty, new JAdler32)

  def e1 = prop { (a: Array[Byte]) =>
    compare(Adler32(ByteVector(a)), JAdler32(a))
  }

  def e2 = prop { (a: Array[Byte]) =>
    compare(Adler32(ByteVector(a)), JAdler32(a))
  }.set(minTestsOk = 20, minSize = 10000, maxSize = 100000)

  def e3 = prop { (a: Array[Byte], b: Array[Byte]) =>
    compare(Adler32M(ByteVector(a)) update Adler32M(ByteVector(b)), JAdler32(a ++ b))
  }

  def e4 = prop { (bufs: Stream[Array[Byte]]) =>
    import scalaz.contrib.hash.adler32.Adler32Monoid
    import scalaz.std.stream._
    import scalaz.syntax.foldable._

    val sa = bufs.foldMap(buf => Adler32M(ByteVector(buf)))

    compare(sa, JAdler32(bufs))
  }

  def e5 = prop { (bufs: Stream[Array[Byte]]) =>
    val monoid = spire.contrib.hash.adler32.Adler32Monoid

    val sa = bufs.map(buf => Adler32M(ByteVector(buf))).foldLeft(monoid.id)(monoid.op)

    compare(sa, JAdler32(bufs))
  }

  // -----------------------------------------------------------------------------------------------
  // util
  // -----------------------------------------------------------------------------------------------

  def compare(sa: Adler32, ja: JAdler32) = sa.value === ja.getValue
  def compare(sa: Adler32M, ja: JAdler32) = sa.value === ja.getValue

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

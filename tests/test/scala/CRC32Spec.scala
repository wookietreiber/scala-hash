package scalax.hash

import java.util.zip.{ CRC32 ⇒ JCRC32 }

import org.specs2._

class CRC32Spec extends Specification with ScalaCheck { def is = s2"""

  CRC32 Specification

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

  def e0 = compare(CRC32(), new JCRC32)

  def e1 = prop { (a: Array[Byte]) ⇒
    compare(CRC32(a), JCRC32(a))
  }

  def e2 = prop { (a: Array[Byte]) ⇒
    compare(CRC32(a), JCRC32(a))
  }.set(minTestsOk = 20, minSize = 10000, maxSize = 100000)

  def e3 = prop { (a: Array[Byte], b: Array[Byte]) ⇒
    compare(CRC32(a).combine(CRC32(b), b.length), JCRC32(a ++ b))
  }

  def e4 = prop { (bufs: Stream[Array[Byte]]) ⇒
    val monoid = scalaz.contrib.hash.crc32.CRC32CombinationMonoid

    val (scrc,_) = bufs.map(buf ⇒ (CRC32(buf),buf.length)).foldLeft(monoid.zero)((a,b) => monoid.append(a,b))

    compare(scrc, JCRC32(bufs))
  }

  def e5 = prop { (bufs: Stream[Array[Byte]]) ⇒
    val monoid = spire.contrib.hash.crc32.CRC32CombinationMonoid

    val (scrc,_) = bufs.map(buf ⇒ (CRC32(buf),buf.length)).foldLeft(monoid.id)(monoid.op)

    compare(scrc, JCRC32(bufs))
  }

  // -----------------------------------------------------------------------------------------------
  // util
  // -----------------------------------------------------------------------------------------------

  def compare(scrc: CRC32, jcrc: JCRC32) = scrc.hash === jcrc.getValue

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

package scalax.hash

import java.util.zip.{ Adler32 ⇒ JAdler32 }

import org.specs2._

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

  def e0 = compare(Adler32(), new JAdler32)

  def e1 = prop { (a: Array[Byte]) ⇒
    compare(Adler32(a), JAdler32(a))
  }

  def e2 = prop { (a: Array[Byte]) ⇒
    compare(Adler32(a), JAdler32(a))
  }.set(minTestsOk = 20, minSize = 10000, maxSize = 100000)

  def e3 = prop { (a: Array[Byte], b: Array[Byte]) ⇒
    compare(Adler32(a).combine(Adler32(b), b.length), JAdler32(a ++ b))
  }

  def e4 = prop { (bufs: Stream[Array[Byte]]) ⇒
    import scalaz.std.stream._
    import scalaz.syntax.traverse._
    import scalaz.contrib.hash.adler32._

    val (sa,_) = bufs.foldMap(buf ⇒ (Adler32(buf),buf.length))

    compare(sa, JAdler32(bufs))
  }

  def e5 = prop { (bufs: Stream[Array[Byte]]) ⇒
    val acm = spire.contrib.hash.adler32.Adler32CombinationMonoid

    val (sa,_) = bufs.map(buf ⇒ (Adler32(buf),buf.length)).foldLeft(acm.id)(acm.op)

    compare(sa, JAdler32(bufs))
  }

  // -----------------------------------------------------------------------------------------------
  // util
  // -----------------------------------------------------------------------------------------------

  def compare(sa: Adler32, ja: JAdler32) = sa.hash === ja.getValue

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

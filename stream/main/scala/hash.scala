package scalax.hash
package stream

import scalaz.stream._
import scodec.bits.ByteVector

object hash {

  def apply(implicit HM: HashModule): Process1[ByteVector,ByteVector] = {
    def go(prev: HM.Hash): Process1[ByteVector,ByteVector] =
      Process.await1[ByteVector].flatMap { bytes =>
        val next = prev update bytes
        go(next) orElse Process.emitLazy(next.value)
      }

    process1.suspend1(go(HM.Hash.empty))
  }

}

package scalax.hash
package benchmark

object Random {
  def bytes(n: Int) = {
    var a = new Array[Byte](n)
    util.Random.nextBytes(a)
    a
  }

  def MB = bytes(1048576)
}

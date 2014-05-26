package scalax.hash

import scodec.bits.ByteVector

/** An abstract hash module.
  *
  * @define hash hash
  */
trait HashModule {

  /** The hash implementation. */
  type Hash <: HashLike

  /** The $hash companion. */
  def Hash: HashCompanion

  /** The hash interface. */
  protected trait HashLike {

    /** Returns the $hash value. */
    def value: ByteVector

    /** Updates this $hash with new data. */
    def update(data: ByteVector): Hash

    /** Returns a string representation of the $hash value. */
    override final def toString: String =
      value.toString

  }

  /** A hash companion. */
  protected trait HashCompanion {

    /** Returns the $hash that has been fed no data. */
    def empty: Hash

    /** Returns the $hash of the given data. */
    final def apply(data: ByteVector): Hash =
      empty update data

  }

}

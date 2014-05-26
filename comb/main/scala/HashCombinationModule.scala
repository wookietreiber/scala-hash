package scalax.hash
package comb

import scodec.bits.ByteVector

/** An abstract hash combination module.
  *
  * == Rationale ==
  *
  * A combination module provides the ability to ''combine'' independently created hashes. This
  * ability can be used to create a ''Semigroup'' or a ''Monoid'' for a [[HashCombination]] and use
  * them with operators like ''foldMap'' over a ''stream'' of ''bytes''. A ''Monoid'' is provided in
  * the [[scalaz.contrib.hash.ScalazHashCombinationModule scalaz hash combination module]].
  *
  * Also, a combination module allows to create hashes in parallel, e.g. with Scala's Standard
  * Library Collection operator [[scala.collection.GenTraversableOnce.aggregate aggregate]] on a
  * parallel collection.
  *
  * @define hash hash
  */
trait HashCombinationModule extends HashModule {

  /** The hash combination implementation. */
  type HashCombination <: HashCombinationLike

  /** The $hash combination companion. */
  def HashCombination: HashCombinationCompanion

  /** The hash combination interface. */
  protected trait HashCombinationLike {

    /** Returns the $hash value. */
    def value: ByteVector

    /** Updates this $hash combination with new data. */
    def update(that: HashCombination): HashCombination

  }

  /** A hash combination companion.
    *
    * @groupname factory Factory Methods
    * @groupprio factory 0
    *
    * @groupname aggregate Aggregate Operators
    * @groupprio aggregate 1
    */
  protected trait HashCombinationCompanion {

    /** Returns the $hash combination that has been fed no data.
      *
      * @group factory
      */
    def empty: HashCombination

    /** Returns the $hash combination of the given data.
      *
      * @group factory
      */
    def apply(data: ByteVector): HashCombination

    /** Returns the operator to sequentially feed bytes into a $hash combination.
      *
      * @group aggregate
      */
    final def seqop: (HashCombination,ByteVector) => HashCombination =
      (hc, chunk) => hc update HashCombination(chunk)

    /** Returns the associative operator for combining two $hash combination instances.
      *
      * @group aggregate
      */
    final def combop: (HashCombination,HashCombination) => HashCombination =
      (hca, hcb) => hca update hcb

  }

}

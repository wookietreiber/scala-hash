package scalax.hash
package comb

import scodec.bits.ByteVector

trait HashCombinationModule extends HashModule {

  type HashCombination <: HashCombinationLike

  def HashCombination: HashCombinationFactory

  protected trait HashCombinationLike {
    def value: ByteVector
    def update(that: HashCombination): HashCombination
  }

  protected trait HashCombinationFactory {
    def empty: HashCombination

    def apply(data: ByteVector): HashCombination
  }

}

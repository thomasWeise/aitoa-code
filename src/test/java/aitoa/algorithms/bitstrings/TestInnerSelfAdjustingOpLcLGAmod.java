package aitoa.algorithms.bitstrings;

import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.structure.IMetaheuristic;

/** Test a the SelfAdjustingOpLcLGAmod algorithm */
public class TestInnerSelfAdjustingOpLcLGAmod
    extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int pN, final int pUB) {
    return new InnerSelfAdjustingOpLcLGAmod<>(
        new BitStringNullaryOperator());
  }
}

package aitoa.algorithms.bitstrings;

import aitoa.structure.IMetaheuristic;

/** Test a the SelfAdjustingOpLcLGAmodFFA algorithm */
public class TestSelfAdjustingOpLcLGAmodFFA
    extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int pN, final int pUB) {
    return new SelfAdjustingOpLcLGAmodFFA<>(pUB);
  }
}

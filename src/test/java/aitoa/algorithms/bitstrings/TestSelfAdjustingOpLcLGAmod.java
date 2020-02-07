package aitoa.algorithms.bitstrings;

import aitoa.structure.IMetaheuristic;

/** Test a the SelfAdjustingOpLcLGAmod algorithm */
public class TestSelfAdjustingOpLcLGAmod
    extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int n, final int UB) {
    return new SelfAdjustingOpLcLGAmod<>();
  }
}
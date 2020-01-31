package aitoa.algorithms.bitstrings;

import aitoa.structure.IMetaheuristic;

/** Test a the SelfAdjustingOpLcLGAmod algorithm */
public class TestInnerSelfAdjustingOpLcLGAmod
    extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int n, final int UB) {
    return new InnerSelfAdjustingOpLcLGAmod<>();
  }
}

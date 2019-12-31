package aitoa.algorithms.bitstrings;

import aitoa.structure.IMetaheuristic;

/** Test a the Greedy2p1GAmod algorithm */
public class TestGreedy2p1GAmod
    extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int n, final int UB) {
    return new Greedy2p1GAmod<>(1);
  }
}

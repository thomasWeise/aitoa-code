package aitoa.algorithms.bitstrings;

import aitoa.structure.IMetaheuristic;

/** Test a the Greedy2p1GAmodFFA algorithm */
public class TestInnerGreedy2p1GAmodFFA
    extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int pN, final int pUB) {
    return new InnerGreedy2p1GAmodFFA<>(1, pUB);
  }
}

package aitoa.algorithms.bitstrings;

import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.structure.IMetaheuristic;

/** Test a the Greedy2p1GAmod algorithm */
public class TestGreedy2p1GAmod
    extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int pN, final int pUB) {
    return new Greedy2p1GAmod<>(new BitStringNullaryOperator(),
        1);
  }
}

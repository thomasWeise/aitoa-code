package aitoa.algorithms.bitstrings;

import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.structure.IMetaheuristic;

/** Test a the (1+1)-FEA+ algorithm */
public class TestFEA1p1Plus extends TestBitStringMetaheuristic {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int pN, final int pUB) {
    return new FEA1p1Plus<>(new BitStringNullaryOperator(), pUB);
  }
}

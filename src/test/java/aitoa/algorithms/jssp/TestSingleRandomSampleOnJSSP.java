package aitoa.algorithms.jssp;

import aitoa.algorithms.SingleRandomSample;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.SingleRandomSample
 * single random sample algorithm} on the JSSP
 */
public class TestSingleRandomSampleOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic getAlgorithm() {
    return new SingleRandomSample();
  }
}

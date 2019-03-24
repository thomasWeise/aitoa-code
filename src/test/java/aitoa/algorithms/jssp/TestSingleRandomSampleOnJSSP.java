package aitoa.algorithms.jssp;

import aitoa.algorithms.SingleRandomSample;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.SingleRandomSample
 * single random sample algorithm} on the JSSP
 */
public class TestSingleRandomSampleOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic
      getAlgorithm(final JSSPInstance instance) {
    return new SingleRandomSample();
  }
}

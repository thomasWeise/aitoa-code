package aitoa.algorithms.jssp;

import aitoa.algorithms.SingleRandomSample;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.SingleRandomSample
 * single random sample algorithm} on the JSSP
 */
public class TestSingleRandomSampleOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    return new SingleRandomSample<>(
        new JSSPNullaryOperator(instance));
  }
}

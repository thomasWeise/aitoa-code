package aitoa.algorithms.jssp;

import aitoa.algorithms.HillClimber2WithRestarts;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPUnaryOperator1SwapU;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimber2WithRestarts
 * hill climber 2 algorithm with restarts} on the JSSP
 */
public class TestHillClimber2WithRestartsOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    return new HillClimber2WithRestarts<>(
        new JSSPNullaryOperator(instance), //
        new JSSPUnaryOperator1SwapU(instance));
  }
}

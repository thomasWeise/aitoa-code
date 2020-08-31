package aitoa.algorithms.jssp;

import aitoa.algorithms.HillClimber;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPUnaryOperator1Swap;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimber gill climber
 * algorithm} on the JSSP
 */
public class TestHillClimberOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    return new HillClimber<>(new JSSPNullaryOperator(instance), //
        new JSSPUnaryOperator1Swap());
  }
}

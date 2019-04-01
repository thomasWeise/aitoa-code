package aitoa.algorithms.jssp;

import aitoa.algorithms.HillClimber2;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimber2 hill
 * climber 2} algorithm on the JSSP
 */
public class TestHillClimber2OnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    return new HillClimber2<>();
  }
}

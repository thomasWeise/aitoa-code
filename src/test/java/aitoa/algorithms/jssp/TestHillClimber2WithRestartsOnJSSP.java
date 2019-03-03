package aitoa.algorithms.jssp;

import aitoa.algorithms.HillClimber2WithRestarts;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimber2WithRestarts
 * hill climber 2 algorithm with restarts} on the JSSP
 */
public class TestHillClimber2WithRestartsOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic getAlgorithm() {
    return new HillClimber2WithRestarts();
  }
}

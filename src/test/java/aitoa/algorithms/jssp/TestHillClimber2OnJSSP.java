package aitoa.algorithms.jssp;

import aitoa.algorithms.HillClimber2;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimber2 hill
 * climber 2} algorithm on the JSSP
 */
public class TestHillClimber2OnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic getAlgorithm() {
    return new HillClimber2();
  }
}

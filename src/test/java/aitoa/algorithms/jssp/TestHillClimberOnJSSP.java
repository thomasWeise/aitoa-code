package aitoa.algorithms.jssp;

import aitoa.algorithms.HillClimber;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimber gill climber
 * algorithm} on the JSSP
 */
public class TestHillClimberOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic getAlgorithm() {
    return new HillClimber();
  }
}
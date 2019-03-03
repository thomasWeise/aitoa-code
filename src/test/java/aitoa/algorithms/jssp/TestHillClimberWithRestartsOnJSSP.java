package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.HillClimberWithRestarts;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimberWithRestarts
 * hill climber algorithm with restarts} on the JSSP
 */
public class TestHillClimberWithRestartsOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic getAlgorithm() {
    final Random rand = ThreadLocalRandom.current();
    final int rs = 1 + rand.nextInt(512);
    final double f = rand.nextDouble();
    return new HillClimberWithRestarts(rs, "test", f * f); //$NON-NLS-1$
  }
}

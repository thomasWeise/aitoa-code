package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.HillClimberWithRestarts;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimberWithRestarts
 * hill climber algorithm with restarts} on the JSSP
 */
public class TestHillClimberWithRestartsOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int rs = 1 + rand.nextInt(512);
    final double f = rand.nextDouble();
    return new HillClimberWithRestarts<>(rs, "test", f * f); //$NON-NLS-1$
  }
}

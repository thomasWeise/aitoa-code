package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.EA;
import aitoa.examples.jssp.JSSPBinaryOperatorSequence;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPUnaryOperator1Swap;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EA evolutionary
 * algorithm} on the JSSP
 */
public class TestEAOnJSSP extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int mu = 1 + rand.nextInt(64);
    final int lambda = 1 + rand.nextInt(64);
    final double cr = (mu > 1) ? rand.nextDouble() : 0;
    return new EA<>(new JSSPNullaryOperator(instance), //
        new JSSPUnaryOperator1Swap(), //
        new JSSPBinaryOperatorSequence(instance), //
        cr, mu, lambda);
  }
}

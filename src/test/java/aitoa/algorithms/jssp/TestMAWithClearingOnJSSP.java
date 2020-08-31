package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.MAWithClearing;
import aitoa.examples.jssp.JSSPBinaryOperatorSequence;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPUnaryOperator12Swap;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.MAWithClearing memetic
 * algorithm with pruning} on the JSSP
 */
public class TestMAWithClearingOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int mu = 2 + rand.nextInt(64);
    final int lambda = 1 + rand.nextInt(64);
    return new MAWithClearing<>(
        new JSSPNullaryOperator(instance), //
        new JSSPUnaryOperator12Swap(), //
        new JSSPBinaryOperatorSequence(instance), //
        mu, lambda, Integer.MAX_VALUE);
  }
}

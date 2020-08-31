package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.HybridEDA;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPUMDAModel;
import aitoa.examples.jssp.JSSPUnaryOperator1Swap;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EA evolutionary
 * algorithm} on the JSSP
 */
public class TestHybridUMDAPOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int lambda = 1 + rand.nextInt(64);
    final int mu = 1 + rand.nextInt(lambda);

    return new HybridEDA<>(new JSSPNullaryOperator(instance), //
        new JSSPUnaryOperator1Swap(), //
        mu, lambda, Integer.MAX_VALUE,
        new JSSPUMDAModel(instance, 1 + rand.nextInt(2)));
  }
}

package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.EDA;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPUMDAModel;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EDA estimation of
 * distribution algorithm} on the JSSP
 */
public class TestUMDAOnJSSP extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int lambda = 1 + rand.nextInt(64);
    final int mu = 1 + rand.nextInt(lambda);

    return new EDA<>(mu, lambda,
        new JSSPUMDAModel(instance, 1 + rand.nextInt(2)));
  }
}

package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.HybridEDAWithFitness;
import aitoa.algorithms.IntFFA;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPUMDAModel;
import aitoa.examples.jssp.JSSPUnaryOperator1Swap;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EA evolutionary
 * algorithm} on the JSSP
 */
public class TestHybridFFAUMDAOnJSSP11
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int lambda = 1 + rand.nextInt(64);
    final int mu = 1 + rand.nextInt(lambda);

    return new HybridEDAWithFitness<>(
        new JSSPNullaryOperator(instance), //
        new JSSPUnaryOperator1Swap(), //
        mu, lambda, 11,
        new JSSPUMDAModel(instance, 1 + rand.nextInt(2)),
        new IntFFA((int) (0.5d
            + new JSSPMakespanObjectiveFunction(instance)
                .upperBound())));
  }
}

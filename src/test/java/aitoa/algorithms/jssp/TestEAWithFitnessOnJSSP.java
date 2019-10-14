package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.EAWithFitness;
import aitoa.algorithms.IntFFA;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EAWithFitness
 * evolutionary algorithm} with fitness on the JSSP
 */
public class TestEAWithFitnessOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int mu = 1 + rand.nextInt(64);
    final int lambda = 1 + rand.nextInt(64);
    final double cr = (mu > 1) ? rand.nextDouble() : 0;
    return new EAWithFitness<>(cr, mu, lambda,
        new IntFFA(
            (int) (new JSSPMakespanObjectiveFunction(instance)
                .upperBound())));
  }

  /**
   * Run a test
   *
   * @param instance
   *          the jssp instance
   */
  @Override
  protected void runTest(final JSSPInstance instance) {
    this.runTest(instance, 204800L, 200000L);
  }
}

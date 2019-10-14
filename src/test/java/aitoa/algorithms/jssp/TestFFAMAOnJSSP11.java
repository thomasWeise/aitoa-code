package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.IntFFA;
import aitoa.algorithms.MAWithFitness;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.MA memetic algorithm} on
 * the JSSP
 */
public class TestFFAMAOnJSSP11 extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    final int mu = 2 + rand.nextInt(64);
    final int lambda = 1 + rand.nextInt(64);
    return new MAWithFitness<>(mu, lambda, 11, new IntFFA(
        (int) (0.5d + new JSSPMakespanObjectiveFunction(instance)
            .upperBound())));
  }
}

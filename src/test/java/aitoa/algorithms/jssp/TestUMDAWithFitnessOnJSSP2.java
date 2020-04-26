package aitoa.algorithms.jssp;

import aitoa.algorithms.EDAWithFitness;
import aitoa.algorithms.IntFFA;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPUMDAModel;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EDAWithFitness
 * estimation of distribution algorithm} with fitness on the JSSP
 */
public class TestUMDAWithFitnessOnJSSP2
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    return new EDAWithFitness<>(1024, 8192,
        new JSSPUMDAModel(instance, 3),
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

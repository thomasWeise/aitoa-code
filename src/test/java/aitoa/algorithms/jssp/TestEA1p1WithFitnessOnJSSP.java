package aitoa.algorithms.jssp;

import aitoa.algorithms.EA1p1WithFitness;
import aitoa.algorithms.IntFFA;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPUnaryOperator1Swap;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EA1p1WithFitness
 * (1+1)-EA with fitness} on the JSSP
 */
public class TestEA1p1WithFitnessOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    return new EA1p1WithFitness<>(
        new JSSPNullaryOperator(instance),
        new JSSPUnaryOperator1Swap(),
        new IntFFA(
            (int) (new JSSPMakespanObjectiveFunction(instance)
                .upperBound())));
  }
}

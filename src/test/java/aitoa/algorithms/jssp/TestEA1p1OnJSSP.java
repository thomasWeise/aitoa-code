package aitoa.algorithms.jssp;

import aitoa.algorithms.EA1p1;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EA1p1 (1+1)-EA} on the
 * JSSP
 */
public class TestEA1p1OnJSSP extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance) {
    return new EA1p1<>();
  }
}

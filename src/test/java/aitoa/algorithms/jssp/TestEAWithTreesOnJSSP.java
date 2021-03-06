package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.EA;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.TreeBinaryOperator;
import aitoa.searchSpaces.trees.TreeNullaryOperator;
import aitoa.searchSpaces.trees.TreeUnaryOperatorMR;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.EA evolutionary
 * algorithm} using a tree-based search space on the JSSP. This
 * algorithm is commonly called Genetic Programming.
 */
public class TestEAWithTreesOnJSSP
    extends TestTreeMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<Node[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance,
          final TreeNullaryOperator op0,
          final TreeUnaryOperatorMR op1,
          final TreeBinaryOperator op2) {
    final Random rand = ThreadLocalRandom.current();
    final int mu = 1 + rand.nextInt(64);
    final int lambda = 1 + rand.nextInt(64);
    final double cr = (mu > 1) ? rand.nextDouble() : 0;
    return new EA<>(op0, op1, op2, cr, mu, lambda);
  }
}

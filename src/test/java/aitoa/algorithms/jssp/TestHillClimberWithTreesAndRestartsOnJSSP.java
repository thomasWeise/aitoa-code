package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.HillClimberWithRestarts;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.TreeBinaryOperator;
import aitoa.searchSpaces.trees.TreeNullaryOperator;
import aitoa.searchSpaces.trees.TreeUnaryOperator;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.HillClimberWithRestarts
 * hill climber algorithm with restarts} on the JSSP
 */
public class TestHillClimberWithTreesAndRestartsOnJSSP
    extends TestTreeMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<Node[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance,
          final TreeNullaryOperator op0,
          final TreeUnaryOperator op1,
          final TreeBinaryOperator op2) {
    final Random rand = ThreadLocalRandom.current();
    final int rs = 1 + rand.nextInt(512);
    return new HillClimberWithRestarts<>(op0, op1, rs);
  }
}

package aitoa.algorithms.jssp;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPOperatorBinarySequence;
import aitoa.examples.jssp.JSSPRepresentationMapping;
import aitoa.examples.jssp.JSSPSearchSpace;
import aitoa.examples.jssp.JSSPSolutionSpace;
import aitoa.examples.jssp.JSSPUnaryOperator1Swap;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.ISpace;
import aitoa.structure.TestBlackBoxProcessBuilder;

/** Test a metaheuristic on the JSSP */
@Ignore
public abstract class TestMetaheuristicOnJSSP {

  /**
   * Get the algorithm instance
   *
   * @return the algorithm
   */
  protected abstract IMetaheuristic getAlgorithm();

  /**
   * Run a test
   *
   * @param instance
   *          the jssp instance
   */
  protected void runTest(final JSSPInstance instance) {
    final ISpace<int[]> searchSpace =
        new JSSPSearchSpace(instance);
    final ISpace<JSSPCandidateSolution> solutionSpace =
        new JSSPSolutionSpace(instance);

    this.getAlgorithm().solve(//
        new TestBlackBoxProcessBuilder<int[],
            JSSPCandidateSolution>()//
                .setSearchSpace(searchSpace)//
                .setSolutionSpace(solutionSpace)//
                .setObjectiveFunction(
                    new JSSPMakespanObjectiveFunction(instance))//
                .setNullarySearchOperator(
                    new JSSPNullaryOperator(instance))//
                .setUnarySearchOperator(
                    new JSSPUnaryOperator1Swap())//
                .setBinarySearchOperator(
                    new JSSPOperatorBinarySequence(instance))//
                .setRepresentationMapping(
                    new JSSPRepresentationMapping(instance))
                .setMaxFEs(2048L)//
                .setMaxTime(2000L)//
                .get());
  }

  /**
   * test the application of the algorithm to the demo instance
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testDemo() {
    this.runTest(new JSSPInstance("demo")); //$NON-NLS-1$
  }

  /**
   * test the application of the algorithm to the abz7 instance
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testABZ7() {
    this.runTest(new JSSPInstance("abz7")); //$NON-NLS-1$
  }

  /**
   * test the application of the algorithm to the abz7 instance
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testYN3() {
    this.runTest(new JSSPInstance("yn3")); //$NON-NLS-1$
  }
}
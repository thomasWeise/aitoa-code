package aitoa.algorithms.jssp;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.algorithms.TestMetaheuristic;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPRepresentationMapping;
import aitoa.examples.jssp.JSSPSearchSpace;
import aitoa.examples.jssp.JSSPSolutionSpace;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.ISpace;
import aitoa.structure.TestBlackBoxProcessBuilder;

/** Test a metaheuristic on the JSSP */
@Ignore
public abstract class TestMetaheuristicOnJSSP
    extends TestMetaheuristic<int[], JSSPCandidateSolution> {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<int[], JSSPCandidateSolution>
      getInstance() {
    return this.getAlgorithm(new JSSPInstance("demo")); //$NON-NLS-1$
  }

  /**
   * Get the algorithm instance
   *
   * @param instance
   *          the jssp instance
   * @return the algorithm
   */
  protected abstract IMetaheuristic<int[], JSSPCandidateSolution>
      getAlgorithm(final JSSPInstance instance);

  /**
   * Run a test
   *
   * @param instance
   *          the jssp instance
   * @param maxFEs
   *          the maximum FEs
   * @param maxTime
   *          the maximum time
   */
  protected void runTest(final JSSPInstance instance,
      final long maxFEs, final long maxTime) {
    final ISpace<int[]> searchSpace =
        new JSSPSearchSpace(instance);
    final ISpace<JSSPCandidateSolution> solutionSpace =
        new JSSPSolutionSpace(instance);

    try (final IBlackBoxProcess<int[], JSSPCandidateSolution> p =
        new TestBlackBoxProcessBuilder<int[],
            JSSPCandidateSolution>()//
                .setSearchSpace(searchSpace)//
                .setSolutionSpace(solutionSpace)//
                .setObjectiveFunction(
                    new JSSPMakespanObjectiveFunction(instance))//
                .setRepresentationMapping(
                    new JSSPRepresentationMapping(instance))
                .setMaxFEs(maxFEs)//
                .setMaxTime(maxTime)//
                .get()) {
      this.getAlgorithm(instance).solve(//
          p);
    } catch (final IOException ioe) {
      throw new AssertionError(ioe);
    }
  }

  /**
   * Run a test
   *
   * @param instance
   *          the jssp instance
   */
  protected void runTest(final JSSPInstance instance) {
    this.runTest(instance, 2048L, 2000L);
  }

  /**
   * test the application of the algorithm to the demo instance
   */
  @Test(timeout = 3600000)
  public final void testDemo() {
    this.runTest(new JSSPInstance("demo"), //$NON-NLS-1$
        4096L, 4000L);
  }

  /**
   * test the application of the algorithm to the abz7 instance
   */
  @Test(timeout = 3600000)
  public final void testABZ7() {
    this.runTest(new JSSPInstance("abz7")); //$NON-NLS-1$
  }

  /**
   * test the application of the algorithm to the abz7 instance
   */
  @Test(timeout = 3600000)
  public final void testYN3() {
    this.runTest(new JSSPInstance("yn3")); //$NON-NLS-1$
  }
}

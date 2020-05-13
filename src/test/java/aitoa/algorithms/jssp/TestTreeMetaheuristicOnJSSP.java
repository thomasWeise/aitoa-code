package aitoa.algorithms.jssp;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.algorithms.TestMetaheuristic;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPSolutionSpace;
import aitoa.examples.jssp.trees.JSSPTreeRepresentationMapping;
import aitoa.examples.jssp.trees.JobStatistic;
import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeTypeSet;
import aitoa.searchSpaces.trees.NodeTypeSetBuilder;
import aitoa.searchSpaces.trees.TreeBinaryOperator;
import aitoa.searchSpaces.trees.TreeNullaryOperator;
import aitoa.searchSpaces.trees.TreeSpace;
import aitoa.searchSpaces.trees.TreeUnaryOperator;
import aitoa.searchSpaces.trees.math.ATan2;
import aitoa.searchSpaces.trees.math.Add;
import aitoa.searchSpaces.trees.math.Divide;
import aitoa.searchSpaces.trees.math.DoubleConstant;
import aitoa.searchSpaces.trees.math.MathFunction;
import aitoa.searchSpaces.trees.math.Max;
import aitoa.searchSpaces.trees.math.Min;
import aitoa.searchSpaces.trees.math.Multiply;
import aitoa.searchSpaces.trees.math.Subtract;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.ISpace;
import aitoa.structure.TestBlackBoxProcessBuilder;

/**
 * Test a metaheuristic which uses a tree-based search space on
 * the JSSP
 */
@Ignore
public abstract class TestTreeMetaheuristicOnJSSP
    extends TestMetaheuristic<Node[], JSSPCandidateSolution> {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic<Node[], JSSPCandidateSolution>
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
  protected abstract
      IMetaheuristic<Node[], JSSPCandidateSolution>
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

    final TreeSpace searchSpace = new TreeSpace(7);

    final ISpace<JSSPCandidateSolution> solutionSpace =
        new JSSPSolutionSpace(instance);

    final NodeTypeSetBuilder ntsb = new NodeTypeSetBuilder();
    final NodeTypeSetBuilder.Builder nodes =
        ntsb.rootNodeTypeSet();
    nodes.add(Add.class, nodes, nodes);
    nodes.add(ATan2.class, nodes, nodes);
    nodes.add(Divide.class, nodes, nodes);
    nodes.add(DoubleConstant.type());
    nodes.add(Max.class, nodes, nodes);
    nodes.add(Min.class, nodes, nodes);
    nodes.add(Multiply.class, nodes, nodes);
    nodes.add(Subtract.class, nodes, nodes);
    nodes.add(JobStatistic.type());
    final NodeTypeSet<MathFunction<double[][]>> root =
        ntsb.build();

    try (
        final IBlackBoxProcess<Node[], JSSPCandidateSolution> p =
            new TestBlackBoxProcessBuilder<Node[],
                JSSPCandidateSolution>()//
                    .setSearchSpace(searchSpace)//
                    .setSolutionSpace(solutionSpace)//
                    .setObjectiveFunction(
                        new JSSPMakespanObjectiveFunction(
                            instance))//
                    .setNullarySearchOperator(
                        new TreeNullaryOperator(root, 7))//
                    .setUnarySearchOperator(
                        new TreeUnaryOperator(7))//
                    .setBinarySearchOperator(
                        new TreeBinaryOperator(7))//
                    .setRepresentationMapping(
                        new JSSPTreeRepresentationMapping(
                            instance))
                    .setMaxFEs(maxFEs)//
                    .setMaxTime(maxTime)//
                    .get()) {
      this.getAlgorithm(instance).solve(p);
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
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testDemo() {
    this.runTest(new JSSPInstance("demo"), //$NON-NLS-1$
        4096L, 4000L);
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

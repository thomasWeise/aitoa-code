package aitoa.examples.jssp.tree;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import aitoa.bookExamples.jssp.JSSPRepresentationMappingExample;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPSolutionSpace;
import aitoa.examples.jssp.JSSPTestUtils;
import aitoa.examples.jssp.trees.JSSPTreeRepresentationMapping;
import aitoa.examples.jssp.trees.JobStatistic;
import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeTypeSetBuilder;
import aitoa.searchSpaces.trees.TreeNullaryOperator;
import aitoa.searchSpaces.trees.TreeSpace;
import aitoa.searchSpaces.trees.math.ATan2;
import aitoa.searchSpaces.trees.math.Add;
import aitoa.searchSpaces.trees.math.Divide;
import aitoa.searchSpaces.trees.math.DoubleConstant;
import aitoa.searchSpaces.trees.math.Max;
import aitoa.searchSpaces.trees.math.Min;
import aitoa.searchSpaces.trees.math.Multiply;
import aitoa.searchSpaces.trees.math.Subtract;
import aitoa.structure.IRepresentationMapping;
import aitoa.structure.IRepresentationMappingTest;
import aitoa.structure.ISpace;

/** A test of the JSSP representation mapping */
public class TestJSSPTreeRepresentationMapping extends
    IRepresentationMappingTest<Node[], JSSPCandidateSolution> {

  /** the instance */
  private static final JSSPInstance INSTANCE =
      new JSSPInstance("la25"); //$NON-NLS-1$
  /** create the mapping */
  private static final JSSPTreeRepresentationMapping MAP =
      new JSSPTreeRepresentationMapping(
          TestJSSPTreeRepresentationMapping.INSTANCE);

  /** {@inheritDoc} */
  @Override
  protected IRepresentationMapping<Node[], JSSPCandidateSolution>
      getInstance() {
    return TestJSSPTreeRepresentationMapping.MAP;
  }

  /**
   * create the tree nullary operator
   *
   * @param depth
   *          the depth of the trees
   * @return the nullary operator
   */
  private static final TreeNullaryOperator
      __op0(final int depth) {
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
    return new TreeNullaryOperator(ntsb.build(), depth);
  }

  /** {@inheritDoc} */
  @Override
  protected Node[] createValidX() {
    final Node[] dest = new Node[1];
    TestJSSPTreeRepresentationMapping.__op0(7).apply(dest,
        ThreadLocalRandom.current());
    return dest;
  }

  /** {@inheritDoc} */
  @Override
  protected JSSPCandidateSolution createY() {
    return new JSSPCandidateSolution(
        TestJSSPTreeRepresentationMapping.INSTANCE.m,
        TestJSSPTreeRepresentationMapping.INSTANCE.n);
  }

  /** {@inheritDoc} */
  @Override
  protected void assertValid(final JSSPCandidateSolution y) {
    JSSPTestUtils.assertY(y,
        TestJSSPTreeRepresentationMapping.INSTANCE);
  }

  /**
   * test a single instance on random points in the search space
   *
   * @param instance
   *          the instance
   */
  private static final void
      __testInstance(final JSSPInstance instance) {
    final ISpace<JSSPCandidateSolution> solutionSpace =
        new JSSPSolutionSpace(instance);
    final ISpace<Node[]> searchSpace = new TreeSpace(7);
    final Node[] x = searchSpace.create();
    final JSSPCandidateSolution y = solutionSpace.create();
    final JSSPTreeRepresentationMapping mapping =
        new JSSPTreeRepresentationMapping(instance);

    final TreeNullaryOperator op =
        TestJSSPTreeRepresentationMapping.__op0(7);

    final ThreadLocalRandom random = ThreadLocalRandom.current();

    for (int i = 100; (--i) >= 0;) {
      op.apply(x, random);
      searchSpace.check(x);
      mapping.map(random, x, y);
      solutionSpace.check(y);
    }
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_JSSPRepresentationMappingExampleInstance() {
    TestJSSPTreeRepresentationMapping.__testInstance(
        JSSPRepresentationMappingExample.INSTANCE);
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_demo() {
    TestJSSPTreeRepresentationMapping
        .__testInstance(new JSSPInstance("demo")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_abz7() {
    TestJSSPTreeRepresentationMapping
        .__testInstance(new JSSPInstance("abz7")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_la24() {
    TestJSSPTreeRepresentationMapping
        .__testInstance(new JSSPInstance("la24")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_yn4() {
    TestJSSPTreeRepresentationMapping
        .__testInstance(new JSSPInstance("yn4")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_swv15() {
    TestJSSPTreeRepresentationMapping
        .__testInstance(new JSSPInstance("swv15")); //$NON-NLS-1$
  }
}
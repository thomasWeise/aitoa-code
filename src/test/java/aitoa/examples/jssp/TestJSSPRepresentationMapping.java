package aitoa.examples.jssp;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.bookExamples.jssp.JSSPRepresentationMappingExample;
import aitoa.structure.IRepresentationMapping;
import aitoa.structure.IRepresentationMappingTest;
import aitoa.structure.ISpace;

/** A test of the JSSP representation mapping */
public class TestJSSPRepresentationMapping extends
    IRepresentationMappingTest<int[], JSSPCandidateSolution> {

  /** the instance */
  private static final JSSPInstance INSTANCE =
      new JSSPInstance("la22"); //$NON-NLS-1$
  /** create the mapping */
  private static final JSSPRepresentationMapping MAP =
      new JSSPRepresentationMapping(
          TestJSSPRepresentationMapping.INSTANCE);

  /** {@inheritDoc} */
  @Override
  protected IRepresentationMapping<int[], JSSPCandidateSolution>
      getInstance() {
    return TestJSSPRepresentationMapping.MAP;
  }

  /** {@inheritDoc} */
  @Override
  protected int[] createValidX() {
    return JSSPTestUtils
        .createValidX(TestJSSPRepresentationMapping.INSTANCE);
  }

  /** {@inheritDoc} */
  @Override
  protected JSSPCandidateSolution createY() {
    return new JSSPCandidateSolution(
        TestJSSPRepresentationMapping.INSTANCE.m,
        TestJSSPRepresentationMapping.INSTANCE.n);
  }

  /** {@inheritDoc} */
  @Override
  protected void assertValid(final JSSPCandidateSolution y) {
    JSSPTestUtils.assertY(y,
        TestJSSPRepresentationMapping.INSTANCE);
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testJSSPRepresentationMappingExample() {
    final JSSPInstance instance =
        JSSPRepresentationMappingExample.INSTANCE;
    Assert.assertNotNull(instance);
    final int[] point = JSSPRepresentationMappingExample.POINT;
    Assert.assertNotNull(point);
    final JSSPRepresentationMapping mapping =
        JSSPRepresentationMappingExample.MAPPING;
    Assert.assertNotNull(mapping);
    final JSSPCandidateSolution solution =
        JSSPRepresentationMappingExample.SOLUTION;
    Assert.assertNotNull(solution);
    final JSSPMakespanObjectiveFunction f =
        JSSPRepresentationMappingExample.F;
    Assert.assertNotNull(f);

    Assert.assertEquals(f.evaluate(solution), 180, 0);
    Assert.assertArrayEquals(new int[] { 0, 0, 10, 1, 20, 30, 2,
        130, 140, 3, 140, 160 }, solution.schedule[0]);
    Assert.assertArrayEquals(new int[] { 1, 0, 20, 0, 20, 40, 2,
        40, 60, 3, 160, 175 }, solution.schedule[1]);
    Assert.assertArrayEquals(new int[] { 2, 0, 30, 0, 40, 60, 1,
        60, 110, 3, 110, 125 }, solution.schedule[2]);
    Assert.assertArrayEquals(new int[] { 1, 30, 60, 3, 60, 90, 2,
        90, 130, 0, 130, 170 }, solution.schedule[3]);
    Assert.assertArrayEquals(new int[] { 3, 0, 50, 2, 60, 72, 1,
        110, 140, 0, 170, 180 }, solution.schedule[4]);

    JSSPRepresentationMappingExample.SOLUTION_SPACE
        .check(JSSPRepresentationMappingExample.SOLUTION);
    new JSSPSearchSpace(
        JSSPRepresentationMappingExample.INSTANCE)
            .check(JSSPRepresentationMappingExample.POINT);
  }

  /**
   * test a single instance on random points in the search space
   *
   * @param instance
   *          the instance
   */
  private static final void
      testInstance(final JSSPInstance instance) {
    final ISpace<JSSPCandidateSolution> solutionSpace =
        new JSSPSolutionSpace(instance);
    final ISpace<int[]> searchSpace =
        new JSSPSearchSpace(instance);
    final int[] x = searchSpace.create();
    final JSSPCandidateSolution y = solutionSpace.create();
    final JSSPRepresentationMapping mapping =
        new JSSPRepresentationMapping(instance);

    int k = 0;
    for (int i = instance.n; (--i) >= 0;) {
      for (int j = instance.m; (--j) >= 0;) {
        x[k++] = i;
      }
    }

    final ThreadLocalRandom random = ThreadLocalRandom.current();

    for (int i = 1000; (--i) >= 0;) {
      searchSpace.check(x);
      mapping.map(ThreadLocalRandom.current(), x, y);
      solutionSpace.check(y);
      final int a = random.nextInt(x.length);
      final int b = random.nextInt(x.length);
      final int t = x[a];
      x[a] = x[b];
      x[b] = t;
    }
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testJSSPRepresentationMappingExampleInstance() {
    TestJSSPRepresentationMapping
        .testInstance(JSSPRepresentationMappingExample.INSTANCE);
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testDemo() {
    TestJSSPRepresentationMapping
        .testInstance(new JSSPInstance("demo")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testAbz7() {
    TestJSSPRepresentationMapping
        .testInstance(new JSSPInstance("abz7")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testLa24() {
    TestJSSPRepresentationMapping
        .testInstance(new JSSPInstance("la24")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testYn4() {
    TestJSSPRepresentationMapping
        .testInstance(new JSSPInstance("yn4")); //$NON-NLS-1$
  }

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testSwv15() {
    TestJSSPRepresentationMapping
        .testInstance(new JSSPInstance("swv15")); //$NON-NLS-1$
  }
}

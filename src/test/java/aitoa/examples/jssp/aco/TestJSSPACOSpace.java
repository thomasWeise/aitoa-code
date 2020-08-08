package aitoa.examples.jssp.aco;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;

import aitoa.TestTools;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPRepresentationMapping;
import aitoa.examples.jssp.JSSPTestUtils;
import aitoa.structure.ISpace;
import aitoa.structure.ISpaceTest;

/** Test the search space we defined for the JSSP problem */
public class TestJSSPACOSpace
    extends ISpaceTest<JSSPACOIndividual> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("abz5"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPACOSpace INSTANCE =
      new JSSPACOSpace(TestJSSPACOSpace.PROBLEM);

  /** the raw objective */
  private static final JSSPMakespanObjectiveFunction F =
      new JSSPMakespanObjectiveFunction(
          TestJSSPACOSpace.PROBLEM);

  /** the internal representation mapping */
  private static final JSSPRepresentationMapping G =
      new JSSPRepresentationMapping(TestJSSPACOSpace.PROBLEM);

  /** create */
  public TestJSSPACOSpace() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected ISpace<JSSPACOIndividual> getInstance() {
    return TestJSSPACOSpace.INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  protected void assertValid(final JSSPACOIndividual a) {
    JSSPTestUtils.assertY(a.solution, TestJSSPACOSpace.PROBLEM);
    Assert.assertEquals(TestJSSPACOSpace.F.evaluate(a.solution),
        a.makespan, 0d);

    final boolean[] have = new boolean[TestJSSPACOSpace.PROBLEM.m
        * TestJSSPACOSpace.PROBLEM.n];
    for (final int i : a.permutation) {
      TestTools.assertGreaterOrEqual(i, 0);
      TestTools.assertLess(i, have.length);
      Assert.assertTrue(have[i] ^= true);
    }
    for (final boolean b : have) {
      Assert.assertTrue(b);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void
      fillWithRandomData(final JSSPACOIndividual dest) {

    JSSPTestUtils.randomX(dest.permutation,
        TestJSSPACOSpace.PROBLEM);
    TestJSSPACOSpace.G.map(ThreadLocalRandom.current(),
        dest.permutation, dest.solution);

    final int[] count = new int[TestJSSPACOSpace.PROBLEM.n];
    for (int i = 0; i < dest.permutation.length; i++) {
      final int p = dest.permutation[i];
      dest.permutation[i] =
          (p * TestJSSPACOSpace.PROBLEM.m) + (count[p]++);
    }

    dest.makespan =
        (int) (TestJSSPACOSpace.F.evaluate(dest.solution));
  }

  /** {@inheritDoc} */
  @Override
  protected JSSPACOIndividual createValid() {
    final JSSPACOIndividual dest = new JSSPACOIndividual(
        TestJSSPACOSpace.PROBLEM.m, TestJSSPACOSpace.PROBLEM.n);
    this.fillWithRandomData(dest);
    return dest;
  }

  /** {@inheritDoc} */
  @Override
  protected void assertEquals(final JSSPACOIndividual a,
      final JSSPACOIndividual b) {
    if (a != b) {
      final int[][] sa = a.solution.schedule;
      final int[][] sb = b.solution.schedule;
      Assert.assertEquals(sa.length, sb.length);
      for (int i = sa.length; (--i) >= 0;) {
        Assert.assertArrayEquals(sa[i], sb[i]);
      }
      Assert.assertArrayEquals(a.permutation, b.permutation);
      Assert.assertEquals(a.makespan, b.makespan);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected JSSPACOIndividual createInvalid() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final JSSPACOIndividual a = this.createValid();

    switch (random.nextInt(3)) {
      case 0: {
        a.permutation[random.nextInt(a.permutation.length)]++;
        break;
      }
      case 1: {
        final int[] x = a.solution.schedule[random
            .nextInt(a.solution.schedule.length)];
        final int idx = 3 * random.nextInt(1, x.length / 3);
        x[idx + 1] = x[idx - 1] - 1;
        break;
      }
      default: {
        a.makespan = random.nextInt(-100000, 1);
      }
    }
    return a;
  }
}

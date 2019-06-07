package aitoa.examples.jssp;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;

import aitoa.structure.ISpace;
import aitoa.structure.ISpaceTest;

/**
 * Test the solution space we defined for the JSSP problem. This
 * test assumes that the search space and representation mapping
 * we have defined test OK.
 */
public class TestJSSPSolutionSpace
    extends ISpaceTest<JSSPCandidateSolution> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("yn3"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSolutionSpace INSTANCE =
      new JSSPSolutionSpace(TestJSSPSolutionSpace.PROBLEM);

  /** the internal mapping */
  private static final JSSPRepresentationMapping MAPPING =
      new JSSPRepresentationMapping(
          TestJSSPSolutionSpace.PROBLEM);

  /** create */
  public TestJSSPSolutionSpace() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected ISpace<JSSPCandidateSolution> getInstance() {
    return TestJSSPSolutionSpace.INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  protected void assertValid(final JSSPCandidateSolution a) {
    JSSPTestUtils.assertY(a, TestJSSPSolutionSpace.PROBLEM);
  }

  /** {@inheritDoc} */
  @Override
  protected void assertEquals(final JSSPCandidateSolution a,
      final JSSPCandidateSolution b) {
    if (a != b) {
      Assert.assertEquals(a.schedule.length, b.schedule.length);
      for (int i = a.schedule.length; (--i) >= 0;) {
        Assert.assertArrayEquals(a.schedule[i], b.schedule[i]);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void
      fillWithRandomData(final JSSPCandidateSolution dest) {
    TestJSSPSolutionSpace.MAPPING
        .map(
            ThreadLocalRandom.current(), JSSPTestUtils
                .createValidX(TestJSSPSolutionSpace.PROBLEM),
            dest);
  }

  /** {@inheritDoc} */
  @Override
  protected JSSPCandidateSolution createValid() {
    final JSSPCandidateSolution dest = new JSSPCandidateSolution(
        TestJSSPSolutionSpace.PROBLEM.m,
        TestJSSPSolutionSpace.PROBLEM.n);
    this.fillWithRandomData(dest);
    return (dest);
  }

  /** {@inheritDoc} */
  @Override
  protected JSSPCandidateSolution createInvalid() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final JSSPCandidateSolution a = this.createValid();
    boolean need = true;

    while (need) {
      if (random.nextBoolean()) {
        final int z =
            random.nextInt(TestJSSPSolutionSpace.PROBLEM.m);
        final int y =
            random.nextInt(TestJSSPSolutionSpace.PROBLEM.n) * 3;
        int l;
        do {
          l = random.nextInt();
        } while (l == a.schedule[z][y]);
        a.schedule[z][y] = l;
        need = false;
      }
    }
    return a;
  }
}

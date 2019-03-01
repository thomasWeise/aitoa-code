package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.structure.ISpace;
import aitoa.structure.ISpaceTest;

/** Test the search space we defined for the JSSP problem */
public class TestJSSPSearchSpace extends ISpaceTest<int[]> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("abz5"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSearchSpace INSTANCE =
      new JSSPSearchSpace(TestJSSPSearchSpace.PROBLEM);

  /** create */
  public TestJSSPSearchSpace() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getInstance() {
    return TestJSSPSearchSpace.INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  protected void assertValid(final int[] a) {
    JSSPTestUtils.assertX(a, TestJSSPSearchSpace.PROBLEM);
  }

  /** {@inheritDoc} */
  @Override
  protected void fillWithRandomData(final int[] dest) {
    JSSPTestUtils.randomX(dest, TestJSSPSearchSpace.PROBLEM);
  }

  /** {@inheritDoc} */
  @Override
  protected int[] createValid() {
    return JSSPTestUtils
        .createValidX(TestJSSPSearchSpace.PROBLEM);
  }

  /** {@inheritDoc} */
  @Override
  protected int[] createInvalid() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    int[] a = this.createValid();
    boolean need = true;

    while (need) {

      if (random.nextBoolean()) {
        int l;
        do {
          l = random.nextInt(-Math.min(3, a.length - 1), 3);
        } while (l == 0);
        a = Arrays.copyOf(a, a.length + l);
        need = false;
      }

      if (random.nextBoolean()) {
        final int i = random.nextInt(a.length);
        final int k = a[i];
        int l;
        do {
          l = random.nextInt();
        } while (l == k);
        a[i] = l;
        need = false;
      }
    }

    return a;
  }
}

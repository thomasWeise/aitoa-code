package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.IUnarySearchOperatorTest;

/** test the unary n-swap search operator */
public class TestJSSPUnaryOperatorNSwap
    extends IUnarySearchOperatorTest<int[]> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("abz7"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSearchSpace SPACE =
      new JSSPSearchSpace(TestJSSPUnaryOperatorNSwap.PROBLEM);

  /** the operator we use */
  private static final IUnarySearchOperator<int[]> OP =
      new JSSPUnaryOperatorNSwap();

  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getSpace() {
    return TestJSSPUnaryOperatorNSwap.SPACE;
  }

  /** {@inheritDoc} */
  @Override
  protected IUnarySearchOperator<int[]>
      getOperator(final ISpace<int[]> space) {
    return TestJSSPUnaryOperatorNSwap.OP;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean equals(final int[] a, final int[] b) {
    return Arrays.equals(a, b);
  }

  /** {@inheritDoc} */
  @Override
  protected int[] createValid() {
    return JSSPTestUtils
        .createValidX(TestJSSPUnaryOperatorNSwap.PROBLEM);
  }

  /** test the application to the canonical instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCanonical() {
    final Random random = ThreadLocalRandom.current();
    final JSSPUnaryOperatorNSwap op =
        new JSSPUnaryOperatorNSwap();
    for (final JSSPInstance inst : JSSPTestUtils.INSTANCS) {
      final int[] x = new int[inst.m * inst.n];
      final int[] c = new int[inst.m * inst.n];
      JSSPTestUtils.canonicalX(c, inst);
      final int[] c2 = c.clone();
      JSSPTestUtils.assertX(c2, inst);
      for (int i = 1000; (--i) >= 0;) {
        op.apply(c, x, random);
        JSSPTestUtils.assertX(x, inst);
        Assert.assertArrayEquals(c, c2);
      }
    }
  }

  /** test the application to random instances */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testRandom() {
    final Random random = ThreadLocalRandom.current();
    final JSSPUnaryOperatorNSwap op =
        new JSSPUnaryOperatorNSwap();
    for (final JSSPInstance inst : JSSPTestUtils.INSTANCS) {
      final int[] x = new int[inst.m * inst.n];
      final int[] c = new int[inst.m * inst.n];

      for (int i = 1000; (--i) >= 0;) {
        JSSPTestUtils.randomX(c, inst);
        JSSPTestUtils.assertX(c, inst);
        op.apply(c, x, random);
        JSSPTestUtils.assertX(x, inst);
      }
    }
  }
}

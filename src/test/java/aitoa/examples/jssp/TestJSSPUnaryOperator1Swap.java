package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.IUnarySearchOperatorTest;

/** test the unary 1-swap search operator */
public class TestJSSPUnaryOperator1Swap
    extends IUnarySearchOperatorTest<int[]> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("yn2"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSearchSpace SPACE =
      new JSSPSearchSpace(TestJSSPUnaryOperator1Swap.PROBLEM);

  /** the operator we use */
  private static final IUnarySearchOperator<int[]> OP =
      new JSSPUnaryOperator1Swap();

  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getSpace() {
    return TestJSSPUnaryOperator1Swap.SPACE;
  }

  /** {@inheritDoc} */
  @Override
  protected IUnarySearchOperator<int[]>
      getOperator(final ISpace<int[]> space) {
    return TestJSSPUnaryOperator1Swap.OP;
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
        .createValidX(TestJSSPUnaryOperator1Swap.PROBLEM);
  }

  /** test the application to the canonical instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCanonical() {
    final Random random = ThreadLocalRandom.current();
    final JSSPUnaryOperator1Swap op =
        new JSSPUnaryOperator1Swap();
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
    final JSSPUnaryOperator1Swap op =
        new JSSPUnaryOperator1Swap();
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

  /** test the number of unique outcomes */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testNumberOfUniqueOutcomes() {
    final Random random = ThreadLocalRandom.current();
    final JSSPInstance inst = new JSSPInstance("demo"); //$NON-NLS-1$
    final int[] in = new int[inst.n * inst.m];
    final int[] out = new int[in.length];
    final int[][] unique = new int[(inst.n * inst.m
        * (inst.n - 1) * inst.m) >>> 1][out.length];

    new JSSPNullaryOperator(inst).apply(in, random);
    final JSSPUnaryOperator1Swap op =
        new JSSPUnaryOperator1Swap();
    int count = 0;
    outer: for (int i = (50 * unique.length * unique.length);
        (--i) >= 0;) {
      op.apply(in, out, random);
      for (int j = count; (--j) >= 0;) {
        if (Arrays.equals(unique[j], out)) {
          continue outer;
        }
      }
      System.arraycopy(out, 0, unique[count++], 0, out.length);
    }

    Assert.assertEquals(unique.length, count);
  }
}

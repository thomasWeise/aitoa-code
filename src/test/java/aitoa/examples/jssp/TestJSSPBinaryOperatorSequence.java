package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBinarySearchOperatorTest;
import aitoa.structure.ISpace;

/**
 * test the binary sequence crossover search operator for the
 * JSSP
 */
public class TestJSSPBinaryOperatorSequence
    extends IBinarySearchOperatorTest<int[]> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("abz8"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSearchSpace SPACE =
      new JSSPSearchSpace(
          TestJSSPBinaryOperatorSequence.PROBLEM);

  /** the operator we use */
  private static final IBinarySearchOperator<int[]> OP =
      new JSSPBinaryOperatorSequence(
          TestJSSPBinaryOperatorSequence.PROBLEM);

  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getSpace() {
    return TestJSSPBinaryOperatorSequence.SPACE;
  }

  /** {@inheritDoc} */
  @Override
  protected IBinarySearchOperator<int[]>
      getOperator(final ISpace<int[]> space) {
    if (space == TestJSSPBinaryOperatorSequence.SPACE) {
      return TestJSSPBinaryOperatorSequence.OP;
    }
    return new JSSPBinaryOperatorSequence(
        ((JSSPSearchSpace) space).instance);
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
        .createValidX(TestJSSPBinaryOperatorSequence.PROBLEM);
  }

  /** test the application to the canonical instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCanonical() {
    final Random random = ThreadLocalRandom.current();
    for (final JSSPInstance inst : JSSPTestUtils.INSTANCS) {
      final JSSPBinaryOperatorSequence op =
          new JSSPBinaryOperatorSequence(inst);
      final int[] x = new int[inst.m * inst.n];
      final int[] c = new int[inst.m * inst.n];
      JSSPTestUtils.canonicalX(c, inst);
      final int[] c2 = c.clone();
      JSSPTestUtils.assertX(c2, inst);
      for (int i = 1000; (--i) >= 0;) {
        op.apply(c, c, x, random);
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
    for (final JSSPInstance inst : JSSPTestUtils.INSTANCS) {
      final JSSPBinaryOperatorSequence op =
          new JSSPBinaryOperatorSequence(inst);
      final int[] x = new int[inst.m * inst.n];
      final int[] c1 = new int[inst.m * inst.n];
      final int[] c2 = new int[inst.m * inst.n];

      for (int i = 1000; (--i) >= 0;) {
        JSSPTestUtils.randomX(c1, inst);
        JSSPTestUtils.assertX(c1, inst);
        JSSPTestUtils.randomX(c2, inst);
        JSSPTestUtils.assertX(c2, inst);
        op.apply(c1, c2, x, random);
        JSSPTestUtils.assertX(x, inst);
      }
    }
  }
}

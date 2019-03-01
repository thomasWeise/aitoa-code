package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import aitoa.structure.INullarySearchOperator;
import aitoa.structure.INullarySearchOperatorTest;
import aitoa.structure.ISpace;

/** test the nullary search operator */
public class TestJSSPNullaryOperator
    extends INullarySearchOperatorTest<int[]> {
  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getSpace() {
    return new JSSPSearchSpace(new JSSPInstance("abz5")); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected INullarySearchOperator<int[]>
      getOperator(final ISpace<int[]> space) {
    return new JSSPNullaryOperator(
        ((JSSPSearchSpace) space).instance);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean equals(final int[] a, final int[] b) {
    return Arrays.equals(a, b);
  }

  /** test the application of the nullary operator */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testNullaryOperator() {
    final Random random = ThreadLocalRandom.current();
    for (final JSSPInstance inst : JSSPTestUtils.INSTANCS) {
      final JSSPNullaryOperator op =
          new JSSPNullaryOperator(inst);
      final int[] x = new int[inst.m * inst.n];
      for (int i = 100; (--i) >= 0;) {
        op.apply(x, random);
        JSSPTestUtils.assertX(x, inst);
      }
    }
  }
}

package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.IUnarySearchOperatorTest;

/** test the unary 1-swap search operator */
public class TestJSSPUnaryOperator1SwapU
    extends IUnarySearchOperatorTest<int[]> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("yn2"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSearchSpace SPACE =
      new JSSPSearchSpace(TestJSSPUnaryOperator1SwapU.PROBLEM);

  /** the operator we use */
  private static final JSSPUnaryOperator1SwapU OP =
      new JSSPUnaryOperator1SwapU(
          TestJSSPUnaryOperator1SwapU.PROBLEM);

  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getSpace() {
    return TestJSSPUnaryOperator1SwapU.SPACE;
  }

  /** {@inheritDoc} */
  @Override
  protected IUnarySearchOperator<int[]>
      getOperator(final ISpace<int[]> space) {
    return TestJSSPUnaryOperator1SwapU.OP;
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
        .createValidX(TestJSSPUnaryOperator1SwapU.PROBLEM);
  }

  /**
   * check that all index pairs are unique
   *
   * @param op
   *          the operator
   * @param test
   *          the test set
   */
  private static final void checkUnique(
      final JSSPUnaryOperator1SwapU op,
      final HashSet<Long> test) {
    test.clear();
    for (int k = 0; k < op.mIndexes.length;) {
      final long v = op.mIndexes[k++];
      final long w = op.mIndexes[k++];
      final long key =
          (v < w) ? ((v << 32L) | w) : ((w << 32L) | v);
      Assert.assertTrue(test.add(Long.valueOf(key)));
    }
  }

  /** test the application to the canonical instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCanonical() {
    final Random random = ThreadLocalRandom.current();
    for (final JSSPInstance inst : JSSPTestUtils.INSTANCS) {
      final JSSPUnaryOperator1SwapU op =
          new JSSPUnaryOperator1SwapU(inst);
      TestJSSPUnaryOperator1SwapU.checkUnique(op,
          new HashSet<>());
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
    for (final JSSPInstance inst : JSSPTestUtils.INSTANCS) {
      final int[] x = new int[inst.m * inst.n];
      final int[] c = new int[inst.m * inst.n];
      final JSSPUnaryOperator1SwapU op =
          new JSSPUnaryOperator1SwapU(inst);
      TestJSSPUnaryOperator1SwapU.checkUnique(op,
          new HashSet<>());

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
    final JSSPUnaryOperator1SwapU op =
        new JSSPUnaryOperator1SwapU(inst);
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

  /**
   * test that the
   * {@link IUnarySearchOperator#enumerate(java.util.Random, Object, Object, java.util.function.Predicate)}
   * method works correctly and respects the return values of the
   * visitor
   */
  @Test(timeout = 3600000)
  public void testEnumerate2() {
    final JSSPSearchSpace space =
        TestJSSPUnaryOperator1SwapU.SPACE;

    final JSSPUnaryOperator1SwapU op =
        TestJSSPUnaryOperator1SwapU.OP;
    final Random random = ThreadLocalRandom.current();

    final int[] src = this.createValid();
    final int[] dest = space.create();

    final int[] copy = space.create();
    space.copy(src, copy);

    for (int i = 10; (--i) >= 0;) {
      Assert.assertFalse(
          op.enumerate(random, src, dest, x -> false));
      final HashSet<Long> set = new HashSet<>();
      TestJSSPUnaryOperator1SwapU.checkUnique(op, set);
      Assert.assertTrue(this.equals(src, copy));
    }
  }
}

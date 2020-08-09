package aitoa.searchSpaces.bitstrings;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IBinarySearchOperatorTest;
import aitoa.structure.ISpace;

/**
 * test the binary uniform crossover search operator for bit
 * strings
 */
@Ignore
public class TestBitStringBinaryOperatorUniform
    extends IBinarySearchOperatorTest<boolean[]> {

  /** the space */
  private final BitStringSpace mSpace;

  /** the operator */
  private final BitStringBinaryOperatorUniform mOperator;

  /**
   * create
   *
   * @param pLength
   *          the length
   */
  protected TestBitStringBinaryOperatorUniform(
      final int pLength) {
    super();
    this.mSpace = new BitStringSpace(pLength);
    this.mOperator = new BitStringBinaryOperatorUniform();
  }

  /** {@inheritDoc} */
  @Test(timeout = 3600000)
  @Override
  public void testApplyValidAndDifferent() {
    if (this.mSpace.length > 10) {
      super.testApplyValidAndDifferent();
    }
  }

  /** {@inheritDoc} */
  @Override
  protected ISpace<boolean[]> getSpace() {
    return this.mSpace;
  }

  /** {@inheritDoc} */
  @Override
  protected BitStringBinaryOperatorUniform
      getOperator(final ISpace<boolean[]> space) {
    return this.mOperator;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean equals(final boolean[] a,
      final boolean[] b) {
    return Arrays.equals(a, b);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] b = new boolean[this.mSpace.length];
    final Random r = ThreadLocalRandom.current();
    for (int i = b.length; (--i) >= 0;) {
      b[i] = r.nextBoolean();
    }
    return b;
  }

  /**
   * test that the uniform crossover produces all possible
   * results
   */
  @Test(timeout = 3600000)
  public void testAllResultsPossible() {
    if (this.mSpace.length > 12) {
      return;
    }
    final int[] count = new int[1 << this.mSpace.length];
    final boolean[] a = new boolean[this.mSpace.length];
    final boolean[] b = new boolean[this.mSpace.length];
    final Random random = ThreadLocalRandom.current();
    Arrays.fill(random.nextBoolean() ? a : b, true);

    final boolean[] x = new boolean[this.mSpace.length];

    final BitStringBinaryOperatorUniform op = this.mOperator;

    for (int i = 100 * (10 + count.length); (--i) >= 0;) {
      op.apply(a, b, x, random);
      int idx = 0;
      for (final boolean v : x) {
        idx <<= 1;
        if (v) {
          idx |= 1;
        }
      }
      ++count[idx];
    }

    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (final int c : count) {
      if (c < min) {
        min = c;
      }
      if (c > max) {
        max = c;
      }
    }

    TestTools.assertGreater(min, 0);
    TestTools.assertGreaterOrEqual(min * 5, max);
  }
}

package aitoa.searchSpaces.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.INullarySearchOperatorTest;
import aitoa.structure.ISpace;

/** Test the nullary operator for bit strings */
@Ignore
public class TestBitStringNullaryOperator
    extends INullarySearchOperatorTest<boolean[]> {

  /** the space */
  private final BitStringSpace m_space;

  /** the operator */
  private final BitStringNullaryOperator m_operator;

  /**
   * create
   *
   * @param length
   *          the length
   */
  protected TestBitStringNullaryOperator(final int length) {
    super();
    this.m_space = new BitStringSpace(length);
    this.m_operator = new BitStringNullaryOperator();
  }

  /** {@inheritDoc} */
  @Override
  protected ISpace<boolean[]> getSpace() {
    return this.m_space;
  }

  /** {@inheritDoc} */
  @Override
  protected INullarySearchOperator<boolean[]>
      getOperator(final ISpace<boolean[]> space) {
    return this.m_operator;
  }

  /** test whether all options are sampled */
  @Test(timeout = 3600000)
  public final void testRandom() {
    if (this.m_space.length > 12) {
      return;
    }
    final int[] count = new int[1 << this.m_space.length];
    final boolean[] x = new boolean[this.m_space.length];
    final INullarySearchOperator<boolean[]> op =
        this.getInstance();
    final Random random = ThreadLocalRandom.current();

    for (int i = 100 * (10 + count.length); (--i) >= 0;) {
      op.apply(x, random);
      int idx = 0;
      for (final boolean b : x) {
        idx <<= 1;
        if (b) {
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

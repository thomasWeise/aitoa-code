package aitoa.searchSpaces.bitstrings;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

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
  private final BitStringSpace m_space;

  /** the operator */
  private final BitStringBinaryOperatorUniform m_operator;

  /**
   * create
   *
   * @param length
   *          the length
   */
  protected TestBitStringBinaryOperatorUniform(
      final int length) {
    super();
    this.m_space = new BitStringSpace(length);
    this.m_operator = new BitStringBinaryOperatorUniform();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  @Override
  public void testApplyValidAndDifferent() {
    if (this.m_space.length > 10) {
      super.testApplyValidAndDifferent();
    }
  }

  /** {@inheritDoc} */
  @Override
  protected ISpace<boolean[]> getSpace() {
    return this.m_space;
  }

  /** {@inheritDoc} */
  @Override
  protected BitStringBinaryOperatorUniform
      getOperator(final ISpace<boolean[]> space) {
    return this.m_operator;
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
    final boolean[] b = new boolean[this.m_space.length];
    final Random r = ThreadLocalRandom.current();
    for (int i = b.length; (--i) >= 0;) {
      b[i] = r.nextBoolean();
    }
    return b;
  }
}

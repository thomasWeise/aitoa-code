package aitoa.searchSpaces.bitstrings;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;

import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.IUnarySearchOperatorTest;

/** A test for the bit string-based unary operator */
@Ignore
public class TestBitStringUnaryOperator
    extends IUnarySearchOperatorTest<boolean[]> {

  /** the space */
  private final BitStringSpace m_space;
  /** the nullary operator */
  private final BitStringNullaryOperator m_nullary;
  /** the unary operator */
  private final IUnarySearchOperator<boolean[]> m_unary;

  /**
   * create the unary operator test
   *
   * @param length
   *          the dimension of the bit strings
   * @param unary
   *          the unary operator
   */
  protected TestBitStringUnaryOperator(final int length,
      final IUnarySearchOperator<boolean[]> unary) {
    super();

    this.m_space = new BitStringSpace(length);
    this.m_nullary = new BitStringNullaryOperator();
    this.m_unary = Objects.requireNonNull(unary);
  }

  /** {@inheritDoc} */
  @Override
  protected BitStringSpace getSpace() {
    return this.m_space;
  }

  /** {@inheritDoc} */
  @Override
  protected IUnarySearchOperator<boolean[]>
      getOperator(final ISpace<boolean[]> space) {
    return this.m_unary;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] res = new boolean[this.m_space.length];
    this.m_nullary.apply(res, ThreadLocalRandom.current());
    return res;
  }
}

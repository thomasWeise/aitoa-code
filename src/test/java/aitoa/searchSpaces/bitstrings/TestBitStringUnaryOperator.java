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
  private final BitStringSpace mSpace;
  /** the nullary operator */
  private final BitStringNullaryOperator mNullary;
  /** the unary operator */
  private final IUnarySearchOperator<boolean[]> mUnary;

  /**
   * create the unary operator test
   *
   * @param pLength
   *          the dimension of the bit strings
   * @param pUnary
   *          the unary operator
   */
  protected TestBitStringUnaryOperator(final int pLength,
      final IUnarySearchOperator<boolean[]> pUnary) {
    super();

    this.mSpace = new BitStringSpace(pLength);
    this.mNullary = new BitStringNullaryOperator();
    this.mUnary = Objects.requireNonNull(pUnary);
  }

  /** {@inheritDoc} */
  @Override
  protected BitStringSpace getSpace() {
    return this.mSpace;
  }

  /** {@inheritDoc} */
  @Override
  protected IUnarySearchOperator<boolean[]>
      getOperator(final ISpace<boolean[]> space) {
    return this.mUnary;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] res = new boolean[this.mSpace.length];
    this.mNullary.apply(res, ThreadLocalRandom.current());
    return res;
  }
}

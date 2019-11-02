package aitoa.searchSpaces.bitstrings;

import org.junit.Ignore;

/** Test the bit string 1-flip unary operator */
@Ignore
public class TestBitStringUnaryOperatorMOverNFlip
    extends TestBitStringUnaryOperator {
  /**
   * create the unary operator test
   *
   * @param length
   *          the dimension of the bit strings
   * @param unary
   *          the unary operator
   */
  public TestBitStringUnaryOperatorMOverNFlip(final int length,
      final BitStringUnaryOperatorMOverNFlip unary) {
    super(length, unary);
  }
}

package aitoa.searchSpaces.bitstrings;

import org.junit.Ignore;

/** Test the bit string 1-flip unary operator */
@Ignore
public class TestBitStringUnaryOperatorMOverNFlipDist
    extends TestBitStringUnaryOperator {
  /**
   * create the unary operator test
   *
   * @param unary
   *          the unary operator
   */
  public TestBitStringUnaryOperatorMOverNFlipDist(
      final BitStringUnaryOperatorMOverNFlipDist unary) {
    super(unary.n, unary);
  }
}

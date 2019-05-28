package aitoa.searchSpaces.bitstrings;

/** Test the bit string 1-flip unary operator */
public class TestBitStringUnaryOperator1Flip8
    extends TestBitStringUnaryOperator1Flip {
  /** create */
  public TestBitStringUnaryOperator1Flip8() {
    super(8, new BitStringUnaryOperator1Flip(8));
  }
}

package aitoa.searchSpaces.bitstrings;

/** Test the bit string 1-flip unary operator */
public class TestBitStringUnaryOperator1Flip100
    extends TestBitStringUnaryOperator1Flip {

  /** create */
  public TestBitStringUnaryOperator1Flip100() {
    super(100, new BitStringUnaryOperator1Flip(100));
  }
}

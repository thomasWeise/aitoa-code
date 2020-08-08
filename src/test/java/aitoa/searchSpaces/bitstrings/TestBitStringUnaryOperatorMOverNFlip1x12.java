package aitoa.searchSpaces.bitstrings;

/** Test the bit string m-over-n-flip unary operator */
public class TestBitStringUnaryOperatorMOverNFlip1x12
    extends TestBitStringUnaryOperatorMOverNFlip {
  /** create */
  public TestBitStringUnaryOperatorMOverNFlip1x12() {
    super(12, new BitStringUnaryOperatorMOverNFlip(1));
  }
}

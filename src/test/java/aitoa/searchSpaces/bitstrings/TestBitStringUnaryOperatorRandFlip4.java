package aitoa.searchSpaces.bitstrings;

/** A test for the random flip bit string-based unary operator */
public class TestBitStringUnaryOperatorRandFlip4
    extends TestBitStringUnaryOperator {
  /** create */
  public TestBitStringUnaryOperatorRandFlip4() {
    super(4, new BitStringUnaryOperatorRandFlip());
  }
}

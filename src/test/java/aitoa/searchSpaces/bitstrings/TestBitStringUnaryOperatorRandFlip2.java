package aitoa.searchSpaces.bitstrings;

/** A test for the random flip bit string-based unary operator */
public class TestBitStringUnaryOperatorRandFlip2
    extends TestBitStringUnaryOperator {
  /** create */
  public TestBitStringUnaryOperatorRandFlip2() {
    super(2, new BitStringUnaryOperatorRandFlip());
  }
}
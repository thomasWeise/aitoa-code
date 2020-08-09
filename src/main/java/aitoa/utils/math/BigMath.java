package aitoa.utils.math;

import java.math.BigInteger;

/** Some mathematics routines */
public final class BigMath {

  /**
   * compute the factorial
   *
   * @param input
   *          the input value
   * @return the factorial
   */
  public static BigInteger factorial(final BigInteger input) {
    BigInteger result = BigInteger.ONE;
    BigInteger n = input;

    while (!n.equals(BigInteger.ZERO)) {
      result = result.multiply(n);
      n = n.subtract(BigInteger.ONE);
    }

    return result;
  }

  /**
   * Compute the logarithm base 2. See
   * https://stackoverflow.com/questions/739532/
   *
   * @param val
   *          the value
   * @return the logarithm
   */
  public static double ld(final BigInteger val) {
// Get the minimum number of bits necessary to hold this value.
    final int n = val.bitLength();

// Calculate the double-precision fraction of this number; as if
// the binary point was left of the most significant '1' bit.
// (Get the most significant 53 bits and divide by 2^53)
    long mask = 1L << 52; // mantissa is 53 bits (including
                          // hidden bit)
    long mantissa = 0;
    int j = 0;
    for (int i = 1; i < 54; i++) {
      j = n - i;
      if (j < 0) {
        break;
      }

      if (val.testBit(j)) {
        mantissa |= mask;
      }
      mask >>>= 1;
    }
    // Round up if next bit is 1.
    if ((j > 0) && val.testBit(j - 1)) {
      mantissa++;
    }

// Add the logarithm to the number of bits, and subtract 1
// because the number of bits is always higher than necessary for
// a number (ie. log2(val)<n for every val).
    return ((n - 1) + (Math.log(mantissa / ((double) (1L << 52)))
        * 1.44269504088896340735992468100189213742664595415298d));
// Magic number converts from base e to base 2 before adding. For
// other bases, correct the result, NOT this number!
  }

  /** forbidden */
  private BigMath() {
    throw new UnsupportedOperationException();
  }
}

package aitoa.searchSpaces.bitstrings;

import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * A unary operator for flipping each bit with a certain
 * probability.
 */
public final class BitStringUnaryOperatorMOverNFlip
    implements IUnarySearchOperator<boolean[]> {

  /** the multiplier */
  private final int m_m;

  /**
   * create the unary operator
   *
   * @param _m
   *          the multiplier
   */
  public BitStringUnaryOperatorMOverNFlip(final int _m) {
    super();
    if (_m <= 0) {
      throw new IllegalArgumentException(
          "bit flip multiplier must be at least 1, but is " //$NON-NLS-1$
              + _m + //
              ", which would mean a zero probability to flip bits"); //$NON-NLS-1$
    }
    this.m_m = _m;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return this.m_m + "/n-flip"; //$NON-NLS-1$
  }

  /**
   * Sample a point from the neighborhood of {@code x} by
   * flipping each bit inside of {@code x} with probability m/n.
   *
   * @param x
   *          {@inheritDoc}
   * @param dest
   *          {@inheritDoc}
   * @param random
   *          {@inheritDoc}
   */
  @Override
  public final void apply(final boolean[] x,
      final boolean[] dest, final Random random) {
    final int n = x.length;
    boolean done = false;

    System.arraycopy(x, 0, dest, 0, n);
    do {
      for (int i = n; (--i) >= 0;) {
        if (random.nextInt(n) < this.m_m) {
          dest[i] ^= true;
          done = true;
        }
      }
    } while (!done);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean canEnumerate() {
    return false;
  }
}

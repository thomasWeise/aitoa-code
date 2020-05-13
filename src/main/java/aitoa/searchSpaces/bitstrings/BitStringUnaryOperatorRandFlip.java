package aitoa.searchSpaces.bitstrings;

import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * A unary operator for flipping each bit with the same
 * probability. For a bit string {@code x}, the probability to
 * flip any single bit is {@code 1d/x.length}. Any number of bits
 * may be flipped, but at least one bit will always be flipped.
 */
public final class BitStringUnaryOperatorRandFlip
    implements IUnarySearchOperator<boolean[]> {

  /** create the random flip unary operator */
  public BitStringUnaryOperatorRandFlip() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "Rflip"; //$NON-NLS-1$
  }

  /**
   * Sample a point from the neighborhood of {@code x} by
   * flipping exactly each bit inside of {@code x} with the
   * probability {@code 1d/x.length}.
   *
   * @param x
   *          {@inheritDoc}
   * @param dest
   *          {@inheritDoc}
   * @param random
   *          {@inheritDoc}
   */
  @Override
  public void apply(final boolean[] x, final boolean[] dest,
      final Random random) {
    final int len = x.length;
    System.arraycopy(x, 0, dest, 0, len);
    boolean notDone = true;

    while (notDone) {
      for (int i = len; (--i) >= 0;) {
        if (random.nextInt(len) <= 0) {
          notDone = false;
          dest[i] ^= true;
        }
      }
    }
  }
}

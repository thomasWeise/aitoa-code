package aitoa.searchSpaces.bitstrings;

import java.util.Random;
import java.util.function.Predicate;

import aitoa.structure.IUnarySearchOperator;
import aitoa.utils.RandomUtils;

/**
 * A unary operator for flipping single bits.
 */
public final class BitStringUnaryOperator1Flip
    implements IUnarySearchOperator<boolean[]> {
  /** the indexes */
  private final int[] mIndexes;

  /**
   * create the 1-bit flip unary operator
   *
   * @param pLength
   *          the _length
   */
  public BitStringUnaryOperator1Flip(final int pLength) {
    super();

    this.mIndexes = new int[BitStringSpace.checkLength(pLength)];
    for (int i = this.mIndexes.length; (--i) >= 0;) {
      this.mIndexes[i] = i;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "1flip"; //$NON-NLS-1$
  }

  /**
   * Sample a point from the neighborhood of {@code x} by
   * flipping exactly one bit inside of {@code x}.
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
    System.arraycopy(x, 0, dest, 0, x.length);
    dest[random.nextInt(dest.length)] ^= true;
  }

  /**
   * We visit all points in the search space that could possibly
   * be reached by applying one
   * {@linkplain #apply(boolean[], boolean[], Random) search
   * move} to {@code x}. We therefore simply need to iteratively
   * flip every single flip, i.e., test all possible indices
   * {@code i}.
   *
   * @param random
   *          {@inheritDoc}
   * @param x
   *          {@inheritDoc}
   * @param dest
   *          {@inheritDoc}
   * @param visitor
   *          {@inheritDoc}
   */
  @Override
  public boolean enumerate(final Random random,
      final boolean[] x, final boolean[] dest,
      final Predicate<boolean[]> visitor) {
    final int[] indexes = this.mIndexes;
    // randomize the order in which indices are processed
    System.arraycopy(x, 0, dest, 0, x.length); // copy x to dest
    RandomUtils.shuffle(random, indexes, 0, indexes.length);

    for (final int index : indexes) {
      dest[index] ^= true; // flip
      if (visitor.test(dest)) {
        return true; // visitor says: stop -> return true
      } // visitor did not say stop, so we need to
      dest[index] ^= true; // revert the flip
    }
    return false; // we have enumerated the complete neighborhood
  }

  /** {@inheritDoc} */
  @Override
  public boolean canEnumerate() {
    return true;
  }
}

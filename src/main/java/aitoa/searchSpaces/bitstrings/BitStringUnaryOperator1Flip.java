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
  private final int[] m_indexes;

  /**
   * create the 31-bit flip unary operator
   *
   * @param _length
   *          the _length
   */
  public BitStringUnaryOperator1Flip(final int _length) {
    super();

    this.m_indexes =
        new int[BitStringSpace._checkLength(_length)];
    for (int i = this.m_indexes.length; (--i) >= 0;) {
      this.m_indexes[i] = i;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
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
  public final void apply(final boolean[] x,
      final boolean[] dest, final Random random) {
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
  public final boolean enumerate(final Random random,
      final boolean[] x, final boolean[] dest,
      final Predicate<boolean[]> visitor) {
    final int[] indexes = this.m_indexes;
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
  public final boolean canEnumerate() {
    return true;
  }
}

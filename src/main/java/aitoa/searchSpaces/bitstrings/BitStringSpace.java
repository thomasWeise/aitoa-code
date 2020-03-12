package aitoa.searchSpaces.bitstrings;

import java.io.IOException;

import aitoa.structure.ISpace;

/**
 * The space for bit strings. Of course, the most compact
 * implementation of bit strings would be to use arrays of
 * {@code long} and encode 64 bits in each {@code long} value.
 * However, we use the less-compact {@code boolean[]}
 * representation. Such arrays are easier to handle. From my
 * previous experience, I found that using {@code long[]} causes
 * a loss in speed while memory is rarely an issue.
 */
public final class BitStringSpace implements ISpace<boolean[]> {

  /** the string length */
  public final int length;

  /**
   * create the bit string space
   *
   * @param _length
   *          the _length
   */
  public BitStringSpace(final int _length) {
    super();
    this.length = BitStringSpace._checkLength(_length);
  }

  /**
   * check the bit string length
   *
   * @param length
   *          the length
   * @return the length
   */
  static final int _checkLength(final int length) {
    if (length < 1) {
      throw new IllegalArgumentException(
          "bit string length must be at least 1, but is " //$NON-NLS-1$
              + length);
    }
    return length;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean[] create() {
    return new boolean[this.length];
  }

  /** {@inheritDoc} */
  @Override
  public final void copy(final boolean[] from,
      final boolean[] to) {
    System.arraycopy(from, 0, to, 0, this.length);
  }

  /** {@inheritDoc} */
  @Override
  public final void print(final boolean[] z,
      final Appendable out) throws IOException {
    for (final boolean b : z) {
      out.append(b ? '1' : '0');
    }
    out.append(System.lineSeparator());
    out.append(System.lineSeparator());
    out.append("new boolean[] ");//$NON-NLS-1$
    char sep = '{';
    for (final boolean b : z) {
      out.append(sep);
      out.append(' ');
      sep = ',';
      out.append(Boolean.toString(b));
    }
    out.append('}');
  }

  /** {@inheritDoc} */
  @Override
  public final void check(final boolean[] z) {
    if (z.length != this.length) {
      throw new IllegalArgumentException(
          "Boolean array must have length "//$NON-NLS-1$
              + this.length + " but has length "//$NON-NLS-1$
              + z.length);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return (("boolean[" + this.length) + ']');//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final double getScale() {
    return this.length;
  }
}

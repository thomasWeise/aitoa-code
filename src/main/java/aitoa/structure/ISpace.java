package aitoa.structure;

import java.io.IOException;
import java.util.Objects;

/**
 * An interface for a space, e.g., a search or solution space.
 *
 * @param <Z>
 *          the space data structure
 */
// start relevant
public interface ISpace<Z> {

  /**
   * Create a new data structure.
   *
   * @return the new data structure
   */
  public abstract Z create();

  /**
   * Copy the data structure {@code from} to the data structure
   * {@code to}.
   *
   * @param from
   *          the source data structure to be copied
   * @param to
   *          the destination data structure which will be
   *          overwritten with the contents of {@code from}.
   */
  public abstract void copy(final Z from, final Z to);
// end relevant

  /**
   * Print the data structure {@code z} to the
   * {@link java.lang.Appendable} {@code out}.
   *
   * @param z
   *          the data structure
   * @param out
   *          the output destination
   * @throws IOException
   *           if i/o fails
   */
  public default void print(final Z z, final Appendable out)
      throws IOException {
    out.append(String.valueOf(z));
  }

  /**
   * Check whether an element {@code z} is valid, throw an
   * exception if not. Normally, this method throws an
   * {@link java.lang.IllegalArgumentException} if {@code z} is
   * invalid. However, it may throw all sorts of exceptions, say
   * a {@link java.lang.NullPointerException} or a
   * {@link java.lang.ArrayIndexOutOfBoundsException} in special
   * cases of invalidity.
   *
   * @param z
   *          the element
   * @throws IllegalArgumentException
   *           if {@code z} is invalid
   */
  public default void check(final Z z) {
    Objects.requireNonNull(z);
  }

  /**
   * Get an approximate number describing the scale of this
   * space. This could be the dimension.
   *
   * @return the scale of the space
   */
  public default double getScale() {
    throw new UnsupportedOperationException(
        "Must implement method getScale() of interface ISpace."); //$NON-NLS-1$
  }
// start relevant
}
// end relevant

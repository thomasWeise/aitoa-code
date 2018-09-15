package aitoa.structure;

/**
 * An interface for a space, e.g., a search or solution space.
 *
 * @param <Z>
 *          the space data structure
 */
public interface ISpace<Z> {

  /**
   * Create a new data structure.
   *
   * @return the new data structure
   */
  public abstract Z create();

  /**
   * Copy the data structure {@code from} to the data structure {@code to}.
   *
   * @param from
   *          the source data structure to be copied
   * @param to
   *          the destination data structure which will be overwritten with
   *          the contents of {@code from}.
   */
  public abstract void copy(final Z from, final Z to);
}

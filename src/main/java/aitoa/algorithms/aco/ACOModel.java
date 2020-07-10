package aitoa.algorithms.aco;

import aitoa.structure.IModel;

/**
 * A base class for implementing Ant Colony Optimization
 * algorithms as EDAs by providing a specialized model. Here, the
 * model samples will generate integer permutations of some
 * length {@code l}. During the process of sampling one new
 * instance of the space {@code X}, iteratively the elements from
 * {@code 0} to {@code l} are added.
 *
 * @param <X>
 *          the search space
 */
public abstract class ACOModel<X> implements IModel<X> {

  /** the length of the permutation */
  public final int L;

  /**
   * Create the ACO model.
   *
   * @param _L
   *          the length of the permutation
   */
  protected ACOModel(final int _L) {
    super();
    if ((_L < 2) || (_L > 1000_000)) {
      throw new IllegalArgumentException(
          "L should be from 2..1'000'000, but is " //$NON-NLS-1$
              + _L);
    }
    this.L = _L;
  }

  /**
   * The cost (or heuristic value) that we would incur if we
   * appended the value {@code value} to the present permutation
   * (which we would do by calling {@link #append(int, Object)}).
   * Values with smaller costs are more likely to be added. Only
   * one instance of {@code X} is constructed at any point in
   * time. This method here must only be called from inside
   * {@link #apply(Object,java.util.Random)}.
   *
   * @param value
   *          the value
   * @param x
   *          the potential destination
   * @return the cost, which <em>must</em> always be greater than
   *         zero
   * @see #append(int, Object)
   */
  protected double getCostOfAppending(final int value,
      final X x) {
    return 0d;
  }

  /**
   * Extract a permutation from a point in the search space.
   *
   * @param x
   *          the point
   * @return the permutation
   */
  protected int[] permutationFromX(final X x) {
    return (int[]) x;
  }

  /**
   * Perform any auxiliary action needed for appending the given
   * permutation value to the destination object. The value will
   * automatically inserted into the permutation obtained via
   * {@link #permutationFromX(Object)}, but there might be other
   * actions that need to be performed, which then should be
   * encoded in this method. Of course, if {@code X} is the
   * permutation itself, as assumed by default, this method can
   * just do nothing (and does not need to be overridden). Only
   * one instance of {@code X} is constructed at any point in
   * time. This method here must only be called from inside
   * {@link #apply(Object,java.util.Random)}. It will then modify
   * the partial sample {@code dest} by appending {@code value},
   * which would come at the cost
   * {@link #getCostOfAppending(int, Object)}
   *
   * @param value
   *          the value
   * @param dest
   *          the destination object
   * @see #getCostOfAppending(int, Object)
   */
  protected void append(final int value, final X dest) {
    // nothing
  }
}

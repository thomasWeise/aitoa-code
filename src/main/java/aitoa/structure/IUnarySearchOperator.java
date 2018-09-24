package aitoa.structure;

import java.util.Random;

/**
 * This interface encapsulates a unary search operator, which can
 * sample one new point in the search space by using the
 * information of an existing one.
 *
 * @param <X>
 *          the search space
 */
@FunctionalInterface
public interface IUnarySearchOperator<X> {

  /**
   * Apply the search operator to sample a new point in the
   * search space from an existing one.
   *
   * @param x
   *          the source point
   * @param dest
   *          the destination object to be overwritten with the
   *          newly sampled point
   * @param random
   *          a random number generator
   */
  public abstract void apply(final X x, final X dest,
      final Random random);
}

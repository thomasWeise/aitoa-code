package aitoa.structure;

import java.util.Random;

/**
 * This interface encapsulates a binary search operator, which
 * can sample one new point in the search space by combining the
 * information of two existing ones.
 *
 * @param <X>
 *          the search space
 */
@FunctionalInterface
public interface IBinarySearchOperator<X> {

  /**
   * Apply the search operator to sample a new point in the
   * search space by combining two existing points.
   *
   * @param x0
   *          the first source point
   * @param x1
   *          the second source point
   * @param dest
   *          the destination object to be overwritten with the
   *          newly sampled point
   * @param random
   *          a random number generator
   */
  public abstract void apply(final X x0, final X x1,
      final X dest, final Random random);
}

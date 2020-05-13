package aitoa.structure;

import java.util.Random;

/**
 * This interface encapsulates a ternary search operator, which
 * can sample one new point in the search space by combining the
 * information of three existing ones.
 *
 * @param <X>
 *          the search space
 */
@FunctionalInterface
// start relevant
public interface ITernarySearchOperator<X> {

  /**
   * Apply the search operator to sample a new point in the
   * search space by combining three existing points.
   *
   * @param x0
   *          the first source point
   * @param x1
   *          the second source point
   * @param x2
   *          the third source point
   * @param dest
   *          the destination object to be overwritten with the
   *          newly sampled point
   * @param random
   *          a random number generator
   */
  void apply(final X x0, final X x1, final X x2, final X dest,
      final Random random);
}
// end relevant

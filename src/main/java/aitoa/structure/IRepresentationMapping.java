package aitoa.structure;

import java.util.Random;

/**
 * The basic interface for mapping elements from the search space
 * to elements of the solution space
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
@FunctionalInterface
public interface IRepresentationMapping<X, Y> {

  /**
   * Perform the mapping
   *
   * @param random
   *          a random number generator
   * @param x
   *          the point in the search space
   * @param y
   *          the destination: the candidate solution in the
   *          solution space (will be overwritten)
   */
  public abstract void map(final Random random, final X x,
      final Y y);
}

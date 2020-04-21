package aitoa.algorithms;

/**
 * An individual as used by memetic algorithms that perform
 * partial local search and use fitness assignment.
 *
 * @param <X>
 *          the search space
 */
final class LSFitnessIndividual<X> extends FitnessIndividual<X> {

  /** it has been confirmed that this is a local optimum */
  boolean isOptimum;

  /**
   * create the individual record
   *
   * @param _x
   *          the point in the search space
   * @param _q
   *          the quality
   */
  LSFitnessIndividual(final X _x, final double _q) {
    super(_x, _q);
  }
}

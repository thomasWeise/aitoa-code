package aitoa.algorithms;

/**
 * A record as used by memetic algorithms that perform partial
 * local search and use fitness assignment.
 *
 * @param <X>
 *          the search space
 */
final class LSFitnessRecord<X> extends FitnessRecord<X> {

  /** it has been confirmed that this is a local optimum */
  boolean isOptimum;

  /**
   * create the record
   *
   * @param pX
   *          the point in the search space
   * @param pQ
   *          the quality
   */
  LSFitnessRecord(final X pX, final double pQ) {
    super(pX, pQ);
  }
}

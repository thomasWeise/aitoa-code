package aitoa.algorithms;

/**
 * An individual as used by memetic algorithms that perform
 * partial local search
 *
 * @param <X>
 *          the search space
 */
final class LSIndividual<X> extends Individual<X> {

  /** it has been confirmed that this is a local optimum */
  boolean isOptimum;

  /**
   * create the individual record
   *
   * @param pX
   *          the point in the search space
   * @param pQ
   *          the quality
   */
  LSIndividual(final X pX, final double pQ) {
    super(pX, pQ);
  }
}

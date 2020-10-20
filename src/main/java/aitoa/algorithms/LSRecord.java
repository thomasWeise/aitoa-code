package aitoa.algorithms;

import aitoa.structure.Record;

/**
 * A record as used by memetic algorithms that perform partial
 * local search
 *
 * @param <X>
 *          the search space
 */
final class LSRecord<X> extends Record<X> {

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
  LSRecord(final X pX, final double pQ) {
    super(pX, pQ);
  }
}

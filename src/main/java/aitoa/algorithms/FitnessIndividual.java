package aitoa.algorithms;

/**
 * A shared class for individual records. Such a record record
 * holds one point in search space along with its quality.
 *
 * @param <X>
 *          the search space
 */
public class FitnessIndividual<X> extends Individual<X> {
  /** the fitness */
  double fitness;

  /**
   * create the individual record
   *
   * @param _x
   *          the point in the search space
   * @param _q
   *          the quality
   */
  public FitnessIndividual(final X _x, final double _q) {
    super(_x, _q);
  }
}

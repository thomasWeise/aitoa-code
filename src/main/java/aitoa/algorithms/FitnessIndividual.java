package aitoa.algorithms;

import java.util.Comparator;

/**
 * A shared class for individual records. Such a record record
 * holds one point in search space along with its quality.
 *
 * @param <X>
 *          the search space
 */
public class FitnessIndividual<X> extends Individual<X> {

  /** The comparator to be used for sorting according fitness */
  public static final Comparator<
      FitnessIndividual<?>> BY_FITNESS =
          (a, b) -> Double.compare(a.fitness, b.fitness);

  /** the fitness */
  double fitness;

  /**
   * create the individual record
   *
   * @param pX
   *          the point in the search space
   * @param pQ
   *          the quality
   */
  public FitnessIndividual(final X pX, final double pQ) {
    super(pX, pQ);
  }
}

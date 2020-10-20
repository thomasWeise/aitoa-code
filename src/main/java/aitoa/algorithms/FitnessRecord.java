package aitoa.algorithms;

import java.util.Comparator;

import aitoa.structure.Record;

/**
 * A shared class for fitness records. Such a record holds one
 * point in search space along with its quality <em>and</em> a
 * fitness value. The {@link #quality} is the result of the
 * objective function evaluation. The {@link #fitness} is
 * computed by a {@link FitnessAssignmentProcess} and can combine
 * the {@link #quality} with additional information useful for
 * the search, such as the density of the solutions in a
 * population.
 *
 * @param <X>
 *          the search space
 */
public class FitnessRecord<X> extends Record<X> {

  /**
   * The comparator to be used for sorting according fitness:
   * <em>smaller</em> fitness values are better
   */
  public static final Comparator<FitnessRecord<?>> BY_FITNESS =
      (a, b) -> Double.compare(a.fitness, b.fitness);

  /** the fitness */
  double fitness;

  /**
   * create the record
   *
   * @param pX
   *          the point in the search space
   * @param pQ
   *          the quality
   */
  public FitnessRecord(final X pX, final double pQ) {
    super(pX, pQ);
  }
}

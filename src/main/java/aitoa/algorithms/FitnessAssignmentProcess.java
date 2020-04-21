package aitoa.algorithms;

import java.util.Comparator;

/**
 * The abstract base class for fitness assignment processes
 *
 * @param <X>
 *          the search space
 */
public abstract class FitnessAssignmentProcess<X>
    implements Comparator<FitnessIndividual<? extends X>> {

  /**
   * Assign the fitness a set of individuals. This process will
   * fill the {@link FitnessIndividual#fitness} variable with a
   * value based on the set of provided records.
   *
   * @param P
   *          the array of records, each holding a point from the
   *          search space and a quality value.
   */
  public abstract void
      assignFitness(FitnessIndividual<? extends X>[] P);

  /**
   * The comparator routine used by the fitness assignment
   * process.
   *
   * @param a
   *          the first individual
   * @param b
   *          the second individual
   * @return {@code -1} if {@code a} is better than {@code b},
   *         {@code 1} if {@code b} is better than {@code a},
   *         {@code 0} if neither is better
   */
  @Override
  public int compare(final FitnessIndividual<? extends X> a,
      final FitnessIndividual<? extends X> b) {
    return Double.compare(a.fitness, b.fitness);
  }

  /** initialize the fitness assignment process */
  public void initialize() {
    // do nothing
  }
}

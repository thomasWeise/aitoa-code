package aitoa.algorithms;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * The abstract base class for fitness assignment processes
 *
 * @param <X>
 *          the search space
 */
public abstract class FitnessAssignmentProcess<X> {

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

  /** initialize the fitness assignment process */
  public void initialize() {
    // do nothing
  }

  /**
   * A shared class for individual records. Such a record record
   * holds one point in search space along with its quality.
   *
   * @param <X>
   *          the search space
   */
  static final class FitnessIndividual<X>
      implements Comparable<FitnessIndividual<X>>, Supplier<X> {
    /** the point in the search space */
    final X x;
    /** the quality */
    double quality;
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
    FitnessIndividual(final X _x, final double _q) {
      super();
      this.x = Objects.requireNonNull(_x);
      this.quality = _q;
    }

    /**
     * compare two individuals: the one with smaller quality is
     * better.
     *
     * @return -1 if this record is better than {@code o}, 1 if
     *         it is worse, 0 otherwise
     */
    @Override
    public final int compareTo(final FitnessIndividual<X> o) {
      final int r = Double.compare(this.fitness, o.fitness);
      if (r != 0) {
        return r;
      }
      return Double.compare(this.quality, o.quality);
    }

    /** {@inheritDoc} */
    @Override
    public final X get() {
      return this.x;
    }
  }
}

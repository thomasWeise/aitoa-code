package aitoa.algorithms;

import java.util.Arrays;

/**
 * A very simple implementation of frequency fitness assignment,
 * based on the assumption that all objective values are positive
 * integers.
 */
public final class IntFFA
    extends FitnessAssignmentProcess<Object> {
  /** the frequency table **/
  private final long[] mFrequencies;

  /**
   * create the integer FFA table
   *
   * @param pMax
   *          the maximum possible quality value
   */
  public IntFFA(final int pMax) {
    super();
    this.mFrequencies = new long[pMax + 1];
  }

  /** {@inheritDoc} */
  @Override
  public void initialize() {
    Arrays.fill(this.mFrequencies, 0L);
  }

  /** {@inheritDoc} */
  @Override
  public void assignFitness(
      final FitnessIndividual<? extends Object>[] pop) {
    for (final FitnessIndividual<? extends Object> ind : pop) {
      final double d = ind.quality;
      if (Double.isFinite(d)) {
        ++this.mFrequencies[((int) (d))];
      }
    }
    for (final FitnessIndividual<? extends Object> ind : pop) {
      final double d = ind.quality;
      ind.fitness =
          Double.isFinite(d) ? this.mFrequencies[((int) (d))]
              : Double.POSITIVE_INFINITY;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "ffa"; //$NON-NLS-1$
  }
}

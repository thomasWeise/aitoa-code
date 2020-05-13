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
  private final long[] m_frequencies;

  /**
   * create the integer FFA table
   *
   * @param max
   *          the maximum possible quality value
   */
  public IntFFA(final int max) {
    super();
    this.m_frequencies = new long[max + 1];
  }

  /** {@inheritDoc} */
  @Override
  public void initialize() {
    Arrays.fill(this.m_frequencies, 0L);
  }

  /** {@inheritDoc} */
  @Override
  public void assignFitness(
      final FitnessIndividual<? extends Object>[] P) {
    for (final FitnessIndividual<? extends Object> ind : P) {
      ++this.m_frequencies[((int) (ind.quality))];
    }
    for (final FitnessIndividual<? extends Object> ind : P) {
      ind.fitness = this.m_frequencies[((int) (ind.quality))];
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "ffa"; //$NON-NLS-1$
  }
}

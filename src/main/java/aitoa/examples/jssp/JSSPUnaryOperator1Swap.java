package aitoa.examples.jssp;

import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * An implementation of the unary search operator for the JSSP
 * representation where two jobs are swapped. This operator first
 * copies the input point in the search space to the destination
 * {@code dest}. It then tries to find two indices in
 * {@code dest} which have different corresponding jobs. The jobs
 * at these indices are then swapped.
 */
// start relevant
public final class JSSPUnaryOperator1Swap
    implements IUnarySearchOperator<int[]> {
// end relevant

  /** create the representation */
  public JSSPUnaryOperator1Swap() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "1swap"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public final void apply(final int[] x, final int[] dest,
      final Random random) {
// copy the source point in search space to the dest
    System.arraycopy(x, 0, dest, 0, x.length);

// choose the index of the first sub-job to swap
    final int i = random.nextInt(dest.length);
    final int job_i = dest[i]; // remember job id

    for (;;) { // try to find a location j with a different job
      final int j = random.nextInt(dest.length);
      final int job_j = dest[j];
      if (job_i != job_j) { // we found two locations with two
        dest[i] = job_j; // different values
        dest[j] = job_i; // then we swap the values
        return; // and are done
      }
    }
  }
}
// end relevant
    
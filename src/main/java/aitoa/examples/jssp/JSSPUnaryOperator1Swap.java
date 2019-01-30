package aitoa.examples.jssp;

import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * An implementation of the unary search operator for the JSSP
 * representation where two jobs are swapped.
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
// copy the source to the dest
    System.arraycopy(x, 0, dest, 0, x.length);

// choose the first index
    final int i = random.nextInt(dest.length);
    final int ti = dest[i];

    for (;;) { // try to find a location j with a different job
      final int j = random.nextInt(dest.length);
      final int tj = dest[j];
      if (ti != tj) { // we found two locations with two
        dest[i] = tj; // different values
        dest[j] = ti; // then we swap the values
        return; // and are done
      }
    }
  }
}
// end relevant
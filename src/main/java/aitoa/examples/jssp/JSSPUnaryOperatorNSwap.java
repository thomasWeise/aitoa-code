package aitoa.examples.jssp;

import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * An implementation of the unary search operator for the JSSP
 * representation where a random number of jobs are swapped.
 */
public final class JSSPUnaryOperatorNSwap
    implements IUnarySearchOperator<int[]> {
// end relevant

  /** create the representation */
  public JSSPUnaryOperatorNSwap() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "nswap"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final int[] x, final int[] dest,
      final Random random) {
// copy the source to the dest
    System.arraycopy(x, 0, dest, 0, x.length);

// choose the first index
    int i = random.nextInt(dest.length);
    final int ti = dest[i];
    int last = ti;

    boolean hasNext;
    do { // we repeat a binomially distributed number of times
      hasNext = random.nextBoolean();
      inner: for (;;) {// find a location with a different job
        final int j = random.nextInt(dest.length);
        final int tj = dest[j];
        if ((last != tj) && // don't swap job with itself
            (hasNext || (ti != tj))) { // also not at end
          dest[i] = tj; // swap
          last = tj; // don't overwrite with same value
          i = j; // remember index for last copy
          break inner;
        }
      }
    } while (hasNext); // binomial distribution

    dest[i] = ti; // finally, write back ti to last copied index
  }
}
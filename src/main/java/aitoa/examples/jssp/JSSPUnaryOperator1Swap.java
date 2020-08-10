package aitoa.examples.jssp;

import java.util.Random;
import java.util.function.Predicate;

import aitoa.structure.IUnarySearchOperator;

/**
 * An implementation of the unary search operator for the JSSP
 * representation where two jobs are swapped in a single swap
 * move. This operator first copies the input point in the search
 * space to the destination {@code dest}. It then tries to find
 * two indices in {@code dest} which have different corresponding
 * jobs. The jobs at these indices are then swapped.
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
  public String toString() {
    return "1swap"; //$NON-NLS-1$
  }

  /**
   * Sample a point from the neighborhood of {@code x} by
   * swapping two different job-ids inside of {@code x}.
   *
   * @param x
   *          {@inheritDoc}
   * @param dest
   *          {@inheritDoc}
   * @param random
   *          {@inheritDoc}
   */
  @Override
// start relevant
  public void apply(final int[] x, final int[] dest,
      final Random random) {
// copy the source point in search space to the dest
    System.arraycopy(x, 0, dest, 0, x.length);

// choose the index of the first sub-job to swap
    final int i = random.nextInt(dest.length);
    final int jobI = dest[i]; // remember job id

    for (;;) { // try to find a location j with a different job
      final int j = random.nextInt(dest.length);
      final int jobJ = dest[j];
      if (jobI != jobJ) { // we found two locations with two
        dest[i] = jobJ; // different values
        dest[j] = jobI; // then we swap the values
        return; // and are done
      }
    }
  }
// end relevant

  /**
   * We visit all points in the search space that could possibly
   * be reached by applying one
   * {@linkplain #apply(int[], int[], Random) search move} to
   * {@code x}. We therefore simply need to test all possible
   * index pairs {@code i} and {@code j}. Different neighbors can
   * only result if {@code x[i] != x[j]}, for which
   * {@code i != j} must hold. Also, if we swap the jobs at index
   * {@code i=2} and {@code j=5}, we would get the same result as
   * when swapping {@code i=5} and {@code j=2}, so we can skip
   * unnecessary indices by only looking at pairs with
   * {@code i>j}.
   * <p>
   * This operator is very similar to
   * {@link aitoa.examples.jssp.JSSPUnaryOperator1SwapU} and it
   * spans the exactly same neighborhood. Its
   * {@link #apply(int[], int[], Random)} operator thus is
   * identical. Its
   * {@link #enumerate(Random, int[], int[], Predicate)} method,
   * however, applies a strictly deterministic enumeration
   * procedure.
   *
   * @param random
   *          {@inheritDoc}
   * @param x
   *          {@inheritDoc}
   * @param dest
   *          {@inheritDoc}
   * @param visitor
   *          {@inheritDoc}
   */
  @Override
// start enumerate
  public boolean enumerate(final Random random, final int[] x,
      final int[] dest, final Predicate<int[]> visitor) {
    int i = x.length; // get the length
    System.arraycopy(x, 0, dest, 0, i); // copy x to dest
    for (; (--i) > 0;) { // iterate over all indices 1..(n-1)
      final int jobI = dest[i]; // remember job id at index i
      for (int j = i; (--j) >= 0;) { // iterate over 0..(i-1)
        final int jobJ = dest[j]; // remember job at index j
        if (jobI != jobJ) { // both jobs are different
          dest[i] = jobJ; // then we swap the values
          dest[j] = jobI; // and will then call the visitor
          if (visitor.test(dest)) {
            return true; // visitor says: stop -> return true
          } // visitor did not say stop, so we need to
          dest[i] = jobI; // revert the change
          dest[j] = jobJ; // and continue
        } // end of creation of different neighbor
      } // end of iteration via index j
    } // end of iteration via index i
    return false; // we have enumerated the complete neighborhood
  }
// end enumerate

  /** {@inheritDoc} */
  @Override
  public boolean canEnumerate() {
    return true;
  }
// start relevant
}
// end relevant

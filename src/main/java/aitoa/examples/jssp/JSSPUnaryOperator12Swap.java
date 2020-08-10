package aitoa.examples.jssp;

import java.util.Random;
import java.util.function.Predicate;

import aitoa.structure.IUnarySearchOperator;

/**
 * An implementation of the unary search operator for the JSSP
 * representation where two to three jobs are swapped by one or
 * two swap moves. This operator first copies the input point in
 * the search space to the destination {@code dest}. It then
 * tries to find three indices in {@code dest} which have
 * different corresponding jobs. The jobs at these indices are
 * then swapped.
 * <p>
 * This operator is very similar to
 * {@link aitoa.examples.jssp.JSSPUnaryOperator12SwapR} and it
 * spans the exactly same neighborhood. Its
 * {@link #apply(int[], int[], Random)} operator thus is
 * identical. Its
 * {@link #enumerate(Random, int[], int[], Predicate)} method,
 * however, applies a strictly deterministic enumeration
 * procedure.
 */
// start relevant
public final class JSSPUnaryOperator12Swap
    implements IUnarySearchOperator<int[]> {
// end relevant

  /** create the representation */
  public JSSPUnaryOperator12Swap() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "12swap"; //$NON-NLS-1$
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
        if (random.nextBoolean()) { // swap 2 with prob. 0.5
          dest[i] = jobJ; // different values
          dest[j] = jobI; // then we swap the values
          return; // and are done
        } // in 50% of the cases, this was is 1swap
        for (;;) { // find a location k with a different job
          final int k = random.nextInt(dest.length);
          final int jobK = dest[k];
          if ((jobI != jobK) && (jobJ != jobK)) {
            dest[i] = jobJ; // we got three locations with
            dest[j] = jobK; // different jobs
            dest[k] = jobI; // then we swap the values
            return; // and are done
          }
        }
      }
    }
  }
// end relevant

  /**
   * We visit all points in the search space that could possibly
   * be reached by applying one
   * {@linkplain #apply(int[], int[], Random) search move} to
   * {@code x}. We therefore simply need to test all possible
   * index triplets {@code i}, {@code j}, and {@code k}.
   * Different neighbors can only result if {@code x[i] != x[j]},
   * for which {@code i != j} must hold. This is a single-swap
   * and it is tested. Only for {@code x[i] != x[k]} and
   * {@code x[j] != x[k]}, a "double swap" makes sense. In this
   * case, we have two options to put different jobs at each
   * index, which we both test. We can skip unnecessary indices
   * by only looking at pairs with {@code i>j} and triples with
   * {@code i>j>k}.
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
    for (; (--i) > 0;) { // i from 1...n-1
      final int jobI = dest[i];
      for (int j = i; (--j) >= 0;) { // j from 0...i-1
        final int jobJ = dest[j];
        if (jobI != jobJ) {
          for (int k = j; (--k) >= 0;) { // k from 0...j-1
            final int jobK = dest[k];
            if ((jobI != jobK) && (jobJ != jobK)) {
              dest[i] = jobJ;// there are two possible moves
              dest[j] = jobK;// first possible move:
              dest[k] = jobI;// ijk -> jki
              if (visitor.test(dest)) {
                return true; // visitor says: stop -> return true
              } // visitor did not say stop, so we continue
              dest[i] = jobK; // second possible move:
              dest[j] = jobI; // ijk -> kij
              dest[k] = jobJ; // all others leave some unchanged
              if (visitor.test(dest)) {
                return true; // visitor says: stop -> return true
              } // visitor did not say stop, so we continue
              dest[i] = jobI; // so we revert the moves
              dest[j] = jobJ; // by writing back the original
              dest[k] = jobK; // values
            } // end of finding jobK != jobI and jobJ
          } // end of iteration of k over 0...j-1
// do the single swap of jobI and jobJ
          dest[i] = jobJ; // then we swap the values
          dest[j] = jobI; // and will then call the visitor
          if (visitor.test(dest)) {
            return true; // visitor says: stop -> return true
          } // visitor did not say stop, so we need to
          dest[i] = jobI; // revert the change
          dest[j] = jobJ; // and continue
        } // end of finding jobJ != jobI
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

package aitoa.examples.jssp;

import java.util.Random;
import java.util.function.Predicate;

import aitoa.structure.IUnarySearchOperator;
import aitoa.utils.RandomUtils;

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
 * {@link aitoa.examples.jssp.JSSPUnaryOperator12Swap} and it
 * spans the exactly same neighborhood. Its
 * {@link #apply(int[], int[], Random)} operator thus is
 * identical. Its
 * {@link #enumerate(Random, int[], int[], Predicate)} method,
 * however, is randomized: It first chooses a random order of
 * indices. Based on this order, the possible search moves are
 * enumerated.
 */
// start relevant
public final class JSSPUnaryOperator12SwapR
    implements IUnarySearchOperator<int[]> {
  // end relevant
  /** the indexes */
  private final int[] m_indexes;

  /**
   * create the representation
   *
   * @param instance
   *          the jssp instance
   */
  public JSSPUnaryOperator12SwapR(final JSSPInstance instance) {
    super();

    this.m_indexes = new int[instance.m * instance.n];
    for (int i = this.m_indexes.length; (--i) >= 0;) {
      this.m_indexes[i] = i;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "12swapR"; //$NON-NLS-1$
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
    final int job_i = dest[i]; // remember job id

    for (;;) { // try to find a location j with a different job
      final int j = random.nextInt(dest.length);
      final int job_j = dest[j];
      if (job_i != job_j) { // we found two locations with two
        if (random.nextBoolean()) { // swap 2 with prob. 0.5
          dest[i] = job_j; // different values
          dest[j] = job_i; // then we swap the values
          return; // and are done
        } // in 50% of the cases, this was is 1swap
        for (;;) { // find a location k with a different job
          final int k = random.nextInt(dest.length);
          final int job_k = dest[k];
          if ((job_i != job_k) && (job_j != job_k)) {
            dest[i] = job_j; // we got three locations with
            dest[j] = job_k; // different jobs
            dest[k] = job_i; // then we swap the values
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
   * <p>
   * This enumeration uses a randomized order of indices
   * {@code i} and {@code j}.
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
// end enumerate
    final int[] indexes = this.m_indexes;
// start enumerate
    int ii = x.length; // get the length
    // randomize the order in which indices are processed
    RandomUtils.shuffle(random, indexes, 0, ii);
    System.arraycopy(x, 0, dest, 0, ii); // copy x to dest
    for (; (--ii) > 0;) {// ii from 1...n-1
      final int i = indexes[ii]; // get i: random order
      final int job_i = dest[i];
      for (int jj = ii; (--jj) >= 0;) { // jj from 0...ii-1
        final int j = indexes[jj]; // get j: random order
        final int job_j = dest[j];
        if (job_i != job_j) {
          for (int kk = jj; (--kk) >= 0;) { // kk from 0...j-1
            final int k = indexes[kk];
            final int job_k = dest[k];
            if ((job_i != job_k) && (job_j != job_k)) {
              dest[i] = job_j;// there are two possible moves
              dest[j] = job_k;// first possible move:
              dest[k] = job_i;// ijk -> jki
              if (visitor.test(dest)) {
                return true; // visitor says: stop -> return true
              } // visitor did not say stop, so we continue
              dest[i] = job_k; // second possible move:
              dest[j] = job_i; // ijk -> kij
              dest[k] = job_j; // all others leave some unchanged
              if (visitor.test(dest)) {
                return true; // visitor says: stop -> return true
              } // visitor did not say stop, so we continue
              dest[i] = job_i; // so we revert the moves
              dest[j] = job_j; // by writing back the original
              dest[k] = job_k; // values
            } // end of finding job_k != job_i and job_j
          } // end of iteration of k over 0...j-1
// do the single swap of job_i and job_j
          dest[i] = job_j; // then we swap the values
          dest[j] = job_i; // and will then call the visitor
          if (visitor.test(dest)) {
            return true; // visitor says: stop -> return true
          } // visitor did not say stop, so we need to
          dest[i] = job_i; // revert the change
          dest[j] = job_j; // and continue
        } // end of finding job_j != job_i
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

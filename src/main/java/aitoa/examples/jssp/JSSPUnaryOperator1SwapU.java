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
 * <p>
 * This operator is very similar to
 * {@link aitoa.examples.jssp.JSSPUnaryOperator1Swap} and it
 * spans the exactly same neighborhood. Its
 * {@link #apply(int[], int[], Random)} operator thus is
 * identical. Its
 * {@link #enumerate(Random, int[], int[], Predicate)} method,
 * however, is randomized: It first chooses a random order of
 * indices. Based on this order, the possible search moves are
 * enumerated.
 */
public final class JSSPUnaryOperator1SwapU
    implements IUnarySearchOperator<int[]> {

  /** the indexes */
  final int[] mIndexes;

  /**
   * create the representation
   *
   * @param pInstance
   *          the jssp instance
   */
  public JSSPUnaryOperator1SwapU(final JSSPInstance pInstance) {
    super();

    final int length = pInstance.m * pInstance.n;

    this.mIndexes = new int[length * (length - 1)];
    int index = (-1);
    for (int i = length; (--i) > 0;) {
      for (int j = i; (--j) >= 0;) {
        this.mIndexes[++index] = i;
        this.mIndexes[++index] = j;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "1swapU"; //$NON-NLS-1$
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
  public void apply(final int[] x, final int[] dest,
      final Random random) {
// copy the source point in search space to the dest
    System.arraycopy(x, 0, dest, 0, x.length);

// choose the index of the first operation to swap
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
// indexes be the flattened list of unique index pairs and
// pairCount their number.
// end enumerate
    final int[] indexes = this.mIndexes;
    final int pairCount = indexes.length >> 1;
// start enumerate
    System.arraycopy(x, 0, dest, 0, dest.length); // copy x

// We move along the index-pair array and shuffle the indices on
// the way with an iterative version of the Fisher-Yates shuffle.
    for (int i = 0, start = -1; i < pairCount; i++) {
// Get "a" and "b": the next, randomly chosen index pair.
      int swapWith = (i + random.nextInt(pairCount - i)) << 1;
      final int a = indexes[swapWith];
      indexes[swapWith] = indexes[++start];
      indexes[start] = a;
      final int b = indexes[++swapWith];
      indexes[swapWith] = indexes[++start];
      indexes[start] = b;

      final int jobI = dest[a];// the job at first index
      final int jobJ = dest[b];// the job at second index

      if (jobI != jobJ) {
        dest[a] = jobJ; // then we swap the values
        dest[b] = jobI; // and will then call the visitor
        if (visitor.test(dest)) {
          return true; // visitor says: stop -> return true
        } // visitor did not say stop, so we need to
        dest[a] = jobI; // revert the change
        dest[b] = jobJ; // and continue
      }
    }
    return false; // we have enumerated the complete neighborhood
  }
// end enumerate

  /** {@inheritDoc} */
  @Override
  public boolean canEnumerate() {
    return true;
  }
}

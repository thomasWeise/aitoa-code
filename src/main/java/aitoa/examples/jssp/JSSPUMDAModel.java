package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;

import aitoa.structure.IModel;
import aitoa.utils.RandomUtils;

/**
 * A simple univariate model for the use in an EDA for the JSSP.
 * We remember how often each job id occurred at every index. We
 * then try to sample solutions which approximately represent
 * this distribution. This model is univariate, since we consider
 * each index of the schedule representation separately.
 */
public final class JSSPUMDAModel implements IModel<int[]> {

  /** the counters */
  private final long[][] m_counts;
  /**
   * the permutation used for picking indices to fill in a random
   * order
   */
  private final int[] m_perm;

  /** the probability vector */
  private final long[] m_prob;

  /** the jobs we can choose from */
  private final int[] m_jobChoseFrom;

  /** the remaining number of times a job can be scheduled */
  private final int[] m_jobRemainingTimes;
  /** the number of machines */
  private final int m_m;

  /**
   * create a model for the given jssp instance
   *
   * @param instance
   *          the instance
   */
  public JSSPUMDAModel(final JSSPInstance instance) {
    super();

    int n = instance.n;
    this.m_m = instance.m;
    int l = this.m_m * n;
    this.m_counts = new long[l][n];
    this.m_perm = new int[l];
    for (; (--l) >= 0;) {
      this.m_perm[l] = l;
    }

    this.m_jobRemainingTimes = new int[n];
    this.m_jobChoseFrom = new int[n];
    this.m_prob = new long[n];

    for (; (--n) >= 0;) {
      this.m_jobChoseFrom[n] = n;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void initialize() {
    for (final long[] l : this.m_counts) {
      Arrays.fill(l, 1L);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void update(final Iterable<int[]> selected,
      final Iterable<int[]> unselectedEnd) {
    for (final int[] sel : selected) {
      for (int j = sel.length; (--j) >= 0;) {
        ++this.m_counts[j][sel[j]];
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void sample(final int[] dest,
      final Random random) {
    final int[] perm = this.m_perm;
    final int[] jobRemainingTimes = this.m_jobRemainingTimes;
    Arrays.fill(jobRemainingTimes, this.m_m);
    final int[] jobChooseFrom = this.m_jobChoseFrom;
    final long[] prob = this.m_prob;
    final long[][] counts = this.m_counts;
    int jobChooseLength = jobChooseFrom.length;

    RandomUtils.shuffle(random, perm, 0, perm.length);

    for (final int index : perm) {
      long N = 0L;

      for (int j = 0; j < jobChooseLength; ++j) {
        N += counts[index][jobChooseFrom[j]];
        prob[j] = N;
      }

      final int i = JSSPUMDAModel.find(
          JSSPUMDAModel.uniformFrom0ToNminus1(random, N), prob,
          jobChooseLength);

      final int job = jobChooseFrom[i];
      dest[index] = job;
      if ((--jobRemainingTimes[job]) == 0) {
        jobChooseFrom[i] = jobChooseFrom[--jobChooseLength];
        jobChooseFrom[jobChooseLength] = job;
      }
    }
  }

  /**
   * Find the given value in the array. We use
   * {@link java.util.Arrays#binarySearch(long[], int, int, long)}
   * to search for the value.
   * <p>
   * The {@code array} represents a cummulativ sum, i.e.,
   * something like {2, 4, 10 }. In that case, index 0 has
   * probability 20%, index 2 has probability 20%, and index 3
   * has probability 60%. {@code value} can be anything from 0 to
   * 9. For value in 0, 1, we should return 0. If value is in 2,
   * 3, we should return 1. If value is 4, 5, 6, 7, 8, 9, we
   * return 3.
   * <p>
   * {@link java.util.Arrays#binarySearch(long[], int, int, long)}
   * has two cases: it can find the value, then it returns the
   * index. In our case, we may find 2 or 4 at indices 0 or 1, in
   * which we case we would need to return 1 or 2, i.e., index+1.
   * <p>
   * If
   * {@link java.util.Arrays#binarySearch(long[], int, int, long)}
   * does not find the value, it returns
   * {@code -insertion point - 1}. For 0, this gives -0-1=-1, for
   * 1 it gives -0-1=-1, for 3 it gives -1-1=-2, and for 5, 6, 7,
   * 8, 9 it gives -2-1=-3. We would need to return 0, 1, and 2,
   * respectively. So we can return |index+1|, namely |-1+1|=0,
   * |-2+1|=1, and |-3+1|=2.
   * <p>
   * For positive index, |index+1| is the same as index+1, so we
   * can join both cases into |index+1|.
   *
   * @param value
   *          the value
   * @param array
   *          the array
   * @param length
   *          number of values
   * @return the index
   */
  static final int find(final long value, final long[] array,
      final int length) {
    return (Math.abs(//
        Arrays.binarySearch(array, 0, length, value) + 1));
  }

  /**
   * Create a uniformly distributed random {@code long} from
   * 0...(N-1). This is basically a version of
   * {@link java.util.Random#nextInt(int)} translated to
   * {@code long}.
   *
   * @param random
   *          the random number generator
   * @param N
   *          the number
   * @return the long
   */
  static final long uniformFrom0ToNminus1(final Random random,
      final long N) {
    long bits, val;
    do {
      bits = (random.nextLong() << 1) >>> 1;
      val = bits % N;
    } while (((bits - val) + (N - 1L)) < 0L);
    return val;
  }
}

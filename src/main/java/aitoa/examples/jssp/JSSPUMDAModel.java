package aitoa.examples.jssp;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

import aitoa.structure.IModel;
import aitoa.structure.LogFormat;
import aitoa.utils.RandomUtils;

/**
 * A simple univariate model for the use in an EDA for the JSSP.
 * We remember how often each job id occurred at every index. We
 * then try to sample solutions which approximately represent
 * this distribution. This model is univariate, since we consider
 * each index of the schedule representation separately.
 */
public class JSSPUMDAModel implements IModel<int[]> {

  /** the counters, i.e., the model */
  private final long[][] mModel;
  /**
   * the permutation used for picking indices to fill in a random
   * order
   */
  private final int[] mPerm;

  /** the probability vector */
  private final long[] mProb;

  /** the jobs we can choose from */
  private final int[] mJobChoseFrom;

  /** the remaining number of times a job can be scheduled */
  private final int[] mJobRemainingTimes;
  /** the number of machines */
  private final int mMachines;

  /** the probability base */
  public final long base;

  /**
   * create a model for the given jssp instance
   *
   * @param pInstance
   *          the instance
   */
  public JSSPUMDAModel(final JSSPInstance pInstance) {
    this(pInstance, Integer.MAX_VALUE);
  }

  /**
   * create a model for the given jssp instance
   *
   * @param pInstance
   *          the instance
   * @param pBase
   *          the number of probability units assigned for each
   *          occurrence of a given job at a given index
   */
  public JSSPUMDAModel(final JSSPInstance pInstance,
      final long pBase) {
    super();

    if (pBase <= 0L) {
      throw new IllegalArgumentException(
          "Base multiplier must be greater than 0, but is " //$NON-NLS-1$
              + pBase);
    }
    this.base = pBase;

    int n = pInstance.n;
    this.mMachines = pInstance.m;
    int l = this.mMachines * n;
    this.mModel = new long[l][n];
    this.mPerm = new int[l];
    for (; (--l) >= 0;) {
      this.mPerm[l] = l;
    }

    this.mJobRemainingTimes = new int[n];
    this.mJobChoseFrom = new int[n];
    this.mProb = new long[n];

    for (; (--n) >= 0;) {
      this.mJobChoseFrom[n] = n;
    }
  }

  /**
   * create a JSSP umda model from a string
   *
   * @param strings
   *          the strings
   */
  private JSSPUMDAModel(final String[] strings) {
    this(new JSSPInstance(strings[0]), //
        (strings.length > 1) ? Long.parseLong(strings[1])
            : 1024L);
  }

  /** {@inheritDoc} */
  @Override
  public final int minimumSamplesNeededForUpdate() {
    return 2;
  }

  /**
   * create a JSSP umda model from a string
   *
   * @param string
   *          the strings
   */
  public JSSPUMDAModel(final String string) {
    this(string.split("_")); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void initialize() {
    for (final long[] l : this.mModel) {
      Arrays.fill(l, 1L);
    }
  }

  /** {@inheritDoc} */
  @Override
// start update
  public void update(final Iterable<int[]> selected) {
    final int l = this.mModel.length; // == m*n

// Make sure that all values are >= 1
    for (final long[] a : this.mModel) {
      Arrays.fill(a, 1L);
    }

// For each encountered job, add the large value this.base
    for (final int[] sel : selected) { // selected points
      for (int k = l; (--k) >= 0;) { // valid indices
        this.mModel[k][sel[k]] += this.base;
      }
    }
  }
// end update

  /** {@inheritDoc} */
  @Override
// start sampling
  public final void apply(final int[] dest,
      final Random random) {
    final int[] perm = this.mPerm; // all indices
// each job occurs m times
    final int[] jobRemainingTimes = this.mJobRemainingTimes;
    Arrays.fill(jobRemainingTimes, this.mMachines);
// the jobs we can choose from:
    final int[] jobChooseFrom = this.mJobChoseFrom;
    final long[] prob = this.mProb; // used for cumulative sum
    final long[][] model = this.mModel; // the model
// we can choose from n jobs
    int jobChooseLength = jobChooseFrom.length; // = n

// permute the indexes for which we pick jobs
    RandomUtils.shuffle(random, perm, 0, perm.length);

// iterate over the indices into the array (in random order)
    for (final int k : perm) {
      long N = 0L;

// build the cumulative frequency vector, N be the overall sum
      for (int j = 0; j < jobChooseLength; ++j) {
        N += model[k][jobChooseFrom[j]];
        prob[j] = N;
      }

// pick index with probability proportional to cumulative sum via
// modified binary search.
      final int select = JSSPUMDAModel.find(
          RandomUtils.uniformFrom0ToNminus1(random, N), prob,
          jobChooseLength);

      final int job = jobChooseFrom[select]; // get selected job
      dest[k] = job; // store job in result
      if ((--jobRemainingTimes[job]) == 0) { // job completed?
        jobChooseFrom[select] = jobChooseFrom[--jobChooseLength];
        jobChooseFrom[jobChooseLength] = job;
      }
    } // end iteration over array indices
  }
// end sampling

  /** {@inheritDoc} */
  @Override
  public String toString() {
    if (this.base == Integer.MAX_VALUE) {
      return "umda";//$NON-NLS-1$
    }
    return "umda_" + this.base; //$NON-NLS-1$
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
    final int i = Math.abs(//
        Arrays.binarySearch(array, 0, length, value) + 1);
// The code below would be needed if there could be two elements
// in array with the same value. This can only happen if one
// index would have probability 0.
// This is not possible in the current configuration, so it is
// commented out.
// while (array[i] == value) {
// ++i;
// }
    return i;
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    IModel.super.printSetup(output);
    output.write(LogFormat.mapEntry("baseIncrement", this.base)); //$NON-NLS-1$
  }
}

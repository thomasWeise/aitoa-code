package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;

/**
 * An implementation of a binary search operator for the JSSP
 * representation, where jobs are appended to the queue by
 * choosing them uniformly randomly from the jobs at the same
 * location in the parents. If a situation arises where no job
 * can be picked for the given location, another job is randomly
 * chosen.
 */
public final class JSSPBinaryOperatorUniform
    implements IBinarySearchOperator<int[]> {

  /** the array with the machine index of the jobs */
  private final int[] mDone;
  /** the number of machines */
  private final int mMachines;

  /**
   * create the uniform crossover operator
   *
   * @param pInstance
   *          the JSSP instance
   */
  public JSSPBinaryOperatorUniform(
      final JSSPInstance pInstance) {
    super();
    this.mDone = new int[pInstance.n];
    this.mMachines = pInstance.m;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "uniform"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final int[] x0, final int[] x1,
      final int[] dest, final Random random) {
    final int[] done = this.mDone;
    Arrays.fill(done, 0); // we keep track of completed jobs

    int index = (-1);
    for (final int p1 : x0) {
      final int p2 = x1[++index]; // read p2 from src2

      int choice = p1; // by default, we would pick p1
      if (p1 != p2) { // values at same index differ?
        if (done[p1] >= this.mMachines) { // job p1 is
                                          // completed?
          // use p2 instead of p1 (but p2 may also be done)
          choice = p2;
        } else {
          if ((done[p2] < this.mMachines)
              && random.nextBoolean()) {
            // otherwise, use p2 with 50% probability
            choice = p2;
          } // if the job has not yet been completed
        }
      }
      // it could be that our chosen job is already completed

      // the chosen job is completed?
      while (done[choice] >= this.mMachines) {
        // pick random incomplete one
        choice = random.nextInt(done.length);
      }

      ++done[choice]; // got to the next step of the job
      dest[index] = choice; // store the job
    }
  }
}

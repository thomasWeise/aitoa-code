package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;

/**
 * An implementation of a binary search operator for the JSSP
 * representation, where jobs are appended from both parents. We
 * randomly pick one of the two parents, take its first sub-job,
 * and add it to the (initially empty) child. We then mark this
 * sub-job (i.e., the first occurrence of the job id) as done in
 * both parents. We then, in each step, randomly pick one of the
 * parents and take the next, not-yet-done sub-job from it and
 * add it to the child. We mark the sub-job (i.e., the first
 * unmarked occurrence of the job id) as done in both parents. We
 * do this until the child schedule representation has been
 * filled, at which point all sub-jobs from all parents must have
 * been completed.
 */
// start relevant
public final class JSSPBinaryOperatorSequence
    implements IBinarySearchOperator<int[]> {
// end relevant

  /** the done elements from x0 */
  private final boolean[] m_done_x0;
  /** the done elements from x1 */
  private final boolean[] m_done_x1;

  /**
   * create the sequence crossover operator
   *
   * @param instance
   *          the JSSP instance
   */
  public JSSPBinaryOperatorSequence(
      final JSSPInstance instance) {
    super();
    final int length = instance.n * instance.m;
    this.m_done_x0 = new boolean[length];
    this.m_done_x1 = new boolean[length];
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "sequence"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void apply(final int[] x0, final int[] x1,
      final int[] dest, final Random random) {
// omitted: initialization of arrays done_x0 and done_x1 (that
// remember the already-assigned sub-jobs from x0 and x1) of
// length=m*n to all false; and indices desti, x0i, x10 to 0
// end relevant

    final boolean[] done_x0 = this.m_done_x0;
    Arrays.fill(done_x0, false); // nothing used from x0 yet
    final boolean[] done_x1 = this.m_done_x1;
    Arrays.fill(done_x1, false); // nothing used from xy yet

    final int length = done_x0.length; // length = m*n
    int desti = 0, x0i = 0, x1i = 0; // array indexes = 0
// start relevant
    for (;;) { // repeat until dest is filled, i.e., desti=length
// randomly chose a source point and pick next sub-job from it
      final int add = random.nextBoolean() ? x0[x0i] : x1[x1i];
      dest[desti++] = add; // we picked a sub-job and added it
      if (desti >= length) { // if desti==length, we are finished
        return; // in this case, desti is filled and we can exit
      }

      for (int i = x0i;; i++) { // mark the sub-job as done in x0
        if ((x0[i] == add) && (!done_x0[i])) { // find added job
          done_x0[i] = true;// found it and marked it
          break; // quit sub-job finding loop
        }
      }
      while (done_x0[x0i]) { // now we move the index x0i to the
        x0i++; // next, not-yet completed sub-job in x0
      }

      for (int i = x1i;; i++) { // mark the sub-job as done in x1
        if ((x1[i] == add) && (!done_x1[i])) { // find added job
          done_x1[i] = true; // found it and marked it
          break; // quit sub-job finding loop
        }
      }
      while (done_x1[x1i]) { // now we move the index x1i to the
        x1i++; // next, not-yet completed sub-job in x0
      }
    } // loop back to main loop and to add next sub-job
  } // end of function
}
// end relevant

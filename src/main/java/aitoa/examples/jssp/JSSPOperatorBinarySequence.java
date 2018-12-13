package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;

/**
 * An implementation of a binary search operator for the JSSP
 * representation, where jobs are appended from both parents. We
 * randomly pick one of the two parents, take its first sub-job,
 * and add it to the (initially empty) child. We then mark this
 * sub-job as done in both parents. We then, in each step,
 * randomly pick one of the parents and take the next,
 * not-yet-done sub-job from it and add it to the child. We mark
 * the sub-job as done in both parents. We do this until the
 * child schedule representation has been filled, at which point
 * all sub-jobs from all parents must have been completed.
 */
public final class JSSPOperatorBinarySequence
    implements IBinarySearchOperator<int[]> {
// end relevant

  /** the done elements from x0 */
  private final boolean[] m_done_x0;
  /** the done elements from x1 */
  private final boolean[] m_done_x1;

  /**
   * create the representation
   *
   * @param instance
   *          the JSSP instance
   */
  public JSSPOperatorBinarySequence(
      final JSSPInstance instance) {
    super();
    final int length = instance.n * instance.m;
    this.m_done_x0 = new boolean[length];
    this.m_done_x1 = new boolean[length];
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "sequence"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final int[] x0, final int[] x1,
      final int[] dest, final Random random) {

    final boolean[] done_x0 = this.m_done_x0;
    Arrays.fill(done_x0, false); // nothing used from x0 yet
    final boolean[] done_x1 = this.m_done_x1;
    Arrays.fill(done_x1, false); // nothing used from xy yet

    final int length = done_x0.length; // length = m*n
    int desti = 0, x0i = 0, x1i = 0; // array indexes = 0

    for (;;) { // repeat dest has been filled, i.e., desti=length
      final int add = random.nextBoolean() ? x0[x0i] : x1[x1i];
      dest[desti++] = add; // we picked a sub-job and added it
      if (desti >= length) {
        return;
      } // done?

      for (int i = x0i;; i++) { // mark the sub-job as done in x0
        if ((x0[i] == add) && (!done_x0[i])) {
          done_x0[i] = true;// found it and marked it
          break; // quit sub-job finding loop
        }
      }
      while (done_x0[x0i]) { // now we need to find the next
        x0i++; // not-yet completed sub-job in x0
      }

      for (int i = x1i;; i++) { // mark the sub-job as done in x1
        if ((x1[i] == add) && (!done_x1[i])) {
          done_x1[i] = true; // found it and marked it
          break; // quit sub-job finding loop
        }
      }
      while (done_x1[x1i]) { // now we need to find the next
        x1i++; // not-yet completed sub-job in x1
      }
    }
  }
}
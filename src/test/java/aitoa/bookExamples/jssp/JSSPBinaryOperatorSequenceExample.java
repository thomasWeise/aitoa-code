package aitoa.bookExamples.jssp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPRepresentationMapping;
import aitoa.examples.jssp.JSSPSearchSpace;
import aitoa.examples.jssp.JSSPSolutionSpace;
import aitoa.structure.IBinarySearchOperator;

/**
 * An example based on the
 * {@link aitoa.examples.jssp.JSSPBinaryOperatorSequence}
 */
public final class JSSPBinaryOperatorSequenceExample
    implements IBinarySearchOperator<int[]> {

  /** the done elements from x0 */
  private final boolean[] mDoneX0;
  /** the done elements from x1 */
  private final boolean[] mDoneX1;

  /** the parent */
  private final int[] mParent;
  /** the done index for parent 0 */
  private final int[] mDoneP0;
  /** the done index for parent 1 */
  private final int[] mDoneP1;

  /**
   * create the sequence crossover operator
   *
   * @param pInstance
   *          the JSSP instance
   */
  public JSSPBinaryOperatorSequenceExample(
      final JSSPInstance pInstance) {
    super();
    final int length = pInstance.n * pInstance.m;
    this.mDoneX0 = new boolean[length];
    this.mDoneX1 = new boolean[length];
    this.mParent = new int[length];
    this.mDoneP0 = new int[length];
    this.mDoneP1 = new int[length];
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "sequence"; //$NON-NLS-1$
  }

  /**
   * A re-implementation of
   * {@link aitoa.examples.jssp.JSSPBinaryOperatorSequence#apply(int[], int[], int[], Random)}
   */
  @Override
// start relevant
  public void apply(final int[] x0, final int[] x1,
      final int[] dest, final Random random) {
// omitted: initialization of arrays done_x0 and done_x1 (that
// remember the already-assigned sub-jobs from x0 and x1) of
// length=m*n to all false; and indices desti, x0i, x10 to 0
// end relevant

    final boolean[] done_x0 = this.mDoneX0;
    Arrays.fill(done_x0, false); // nothing used from x0 yet
    final boolean[] done_x1 = this.mDoneX1;
    Arrays.fill(done_x1, false); // nothing used from xy yet

    final int length = done_x0.length; // length = m*n
    int desti = 0;
    int x0i = 0;
    int x1i = 0; // array indexes = 0
// start relevant
    for (;;) { // repeat until dest is filled, i.e., desti=length
// randomly chose a source point and pick next sub-job from it
      final int parent = (random.nextBoolean() ? 0 : 1);
      this.mParent[desti] = parent;
      final int add = (parent == 0) ? x0[x0i] : x1[x1i];

      dest[desti++] = add; // we picked a sub-job and added it
      if (desti >= length) { // if desti==length, we are finished
        return; // in this case, desti is filled and we can exit
      }

      for (int i = x0i;; i++) { // mark the sub-job as done in x0
        if ((x0[i] == add) && (!done_x0[i])) { // find added job
          done_x0[i] = true;// found it and marked it
          this.mDoneP0[desti - 1] = i;
          break; // quit sub-job finding loop
        }
      }
      while (done_x0[x0i]) { // now we move the index x0i to the
        x0i++; // next, not-yet completed sub-job in x0
      }

      for (int i = x1i;; i++) { // mark the sub-job as done in x1
        if ((x1[i] == add) && (!done_x1[i])) { // find added job
          done_x1[i] = true; // found it and marked it
          this.mDoneP1[desti - 1] = i;
          break; // quit sub-job finding loop
        }
      }
      while (done_x1[x1i]) { // now we move the index x1i to the
        x1i++; // next, not-yet completed sub-job in x0
      }
    } // loop back to main loop and to add next sub-job
  } // end of function

  /**
   * The main routine
   *
   * @param args
   *          ignore
   * @throws IOException
   *           should not
   */
  public static void main(final String[] args)
      throws IOException {
    final JSSPInstance inst = new JSSPInstance("demo"); //$NON-NLS-1$
    final JSSPBinaryOperatorSequenceExample op2 =
        new JSSPBinaryOperatorSequenceExample(inst);
    final Random random = new Random(8854138855L);
    final JSSPMakespanObjectiveFunction f =
        new JSSPMakespanObjectiveFunction(inst);
    final JSSPRepresentationMapping g =
        new JSSPRepresentationMapping(inst);

    final JSSPSearchSpace X = new JSSPSearchSpace(inst);
    final int[] x0 = X.create();
    final int[] x1 = X.create();
    final int[] x = X.create();

    final JSSPSolutionSpace Y = new JSSPSolutionSpace(inst);
    final JSSPCandidateSolution y0 = Y.create();
    final JSSPCandidateSolution y1 = Y.create();
    final JSSPCandidateSolution y = Y.create();

    final JSSPNullaryOperator op0 =
        new JSSPNullaryOperator(inst);
    final int bound = ((int) (f.lowerBound()));
    final int maxAccept = (bound + 23);
    int z0 = -1;
    int z1 = -1;
    int z = -1;

    main: for (;;) {
      // create first parent
      do {
        op0.apply(x0, random);
        g.map(random, x0, y0);
        z0 = ((int) (f.evaluate(y0)));
      } while ((z0 <= bound) || (z0 > maxAccept));

      // create different second parent
      do {
        op0.apply(x1, random);
        g.map(random, x1, y1);
        z1 = ((int) (f.evaluate(y1)));
      } while ((z1 <= bound) || (z1 > maxAccept) || (z1 == z0));

      if (Math.abs(z1 - z0) < 12) {
        continue;
      }
      final int mid = (z1 + z0) >>> 1;

      // try to create offspring which is not different and not
      // worse than at least one of the parents
      for (int trials = 100; (--trials) >= 0;) {
        op2.apply(x0, x1, x, random);
        g.map(random, x, y);
        z = ((int) (f.evaluate(y)));
        if (z == mid) {
          break main;
        }
      }
    }

// print the result

    System.out.println(
        " ======================== p0 ===================="); //$NON-NLS-1$
    System.out.print("x0: "); //$NON-NLS-1$
    X.print(x0, System.out);
    System.out.println();
    System.out.print("y0: "); //$NON-NLS-1$
    Y.print(y0, System.out);
    System.out.println();
    System.out.print("z0: "); //$NON-NLS-1$
    System.out.println(z0);

    System.out.println(
        " ======================== p1 ===================="); //$NON-NLS-1$
    System.out.print("x1: "); //$NON-NLS-1$
    X.print(x1, System.out);
    System.out.println();
    System.out.print("y1: "); //$NON-NLS-1$
    Y.print(y1, System.out);
    System.out.println();
    System.out.print("z1: "); //$NON-NLS-1$
    System.out.println(z1);

    System.out.println();
    System.out.println();
    System.out.println(
        " ======================== offspring ===================="); //$NON-NLS-1$
    X.print(x, System.out);
    System.out.println();
    Y.print(y, System.out);
    System.out.println();
    System.out.print("z: "); //$NON-NLS-1$
    System.out.println(z);

    System.out.println();
    System.out.println();
    System.out.println(
        " ======================== operator ===================="); //$NON-NLS-1$
    System.out.print("parent: "); //$NON-NLS-1$
    System.out.println(Arrays.toString(op2.mParent));
    System.out.print("p0-marked-per-step: "); //$NON-NLS-1$
    System.out.println(Arrays.toString(op2.mDoneP0));
    System.out.print("p1-marked-per-step: "); //$NON-NLS-1$
    System.out.println(Arrays.toString(op2.mDoneP1));
  }
}

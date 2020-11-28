package aitoa.algorithms.bitstrings;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic0;

/**
 * A (1+1)-EA that uses FFA and normal optimization in parallel.
 * It maintains both an FFA solution and the best solution and
 * toggles between them as parent for the next solution in each
 * step.
 * <p>
 * This algorithm must only be used with objective value-based or
 * FE-based termination criteria and never be terminated based on
 * runtime.
 *
 * @param <Y>
 *          the solution space
 */
public final class FEA1p1Plus<Y>
    extends Metaheuristic0<boolean[], Y> {

  /** the upper bound of the objective value */
  private final int mUB;

  /**
   * create
   *
   * @param pUB
   *          the upper bound of the objective value
   */
  public FEA1p1Plus(final int pUB) {
    this(null, pUB);
  }

  /**
   * create
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUB
   *          the upper bound of the objective value
   */
  public FEA1p1Plus(
      final INullarySearchOperator<boolean[]> pNullary,
      final int pUB) {
    super((pNullary != null) ? pNullary
        : new BitStringNullaryOperator());
    if (pUB <= 0) {
      throw new IllegalArgumentException(
          "UB must be at least 1, but you specified " //$NON-NLS-1$
              + pUB);
    }
    this.mUB = pUB;
  }

  /** {@inheritDoc} */
  @Override
  public void
      solve(final IBlackBoxProcess<boolean[], Y> process) {
    final Random random = process.getRandom();// get random gen
    final ISpace<boolean[]> searchSpace =
        process.getSearchSpace();

// sample the initial point from the search space
    boolean[] xBest = searchSpace.create();
    this.nullary.apply(xBest, random);
    int fBest = ((int) (process.evaluate(xBest)));
    final int n = xBest.length;

// create a copy of the point as current-best
    boolean[] xFFA = xBest.clone();
    int fFFA = fBest;

// allocate the new point in the search space
    boolean[] xNew = new boolean[n];

// should we use FFA? Will be toggles in each step
    boolean useFFA = false;
    final long[] H = new long[this.mUB + 1]; // FFA

// iterate
    while (!process.shouldTerminate()) {

// pick parent, increase corresponding FFA counter, toggle FFA
      final boolean[] xParent = useFFA ? xFFA : xBest;
      ++H[useFFA ? fFFA : fBest];
      useFFA = !useFFA;

// perform mutation
      boolean done = false;
      System.arraycopy(xParent, 0, xNew, 0, n);
      do {
        for (int i = n; (--i) >= 0;) {
          if (random.nextInt(n) < 1) {
            xNew[i] ^= true;
            done = true;
          }
        }
      } while (!done);

      final int fNew = ((int) (process.evaluate(xNew)));
      ++H[fNew];

// update FFA solution?
      boolean canSwap = true;
      if (H[fNew] <= H[fFFA]) {
        fFFA = fNew;
        final boolean[] xTemp = xFFA;
        xFFA = xNew;
        xNew = xTemp;
        canSwap = false;
      }

// update best solution?
      if (fNew <= fBest) {
        fBest = fNew;
        if (canSwap) {
          final boolean[] xTemp = xBest;
          xBest = xNew;
          xNew = xTemp;
        } else { // xFFA is now xNew
          System.arraycopy(xFFA, 0, xBest, 0, n);
        }
      }
    } // end main loop
  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "(1+1)-FEA+"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    super.printSetup(output);
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        "FFA+"));//$NON-NLS-1$
    output.write(System.lineSeparator());
  }
}

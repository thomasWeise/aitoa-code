package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic2;
import aitoa.structure.Record;
import aitoa.utils.Experiment;

/**
 * A variant of the EA where each offspring competes with their
 * directly corresponding parent solution only. It is something
 * like a set of parallel {@link EA1p1}, but the addition of
 * {@linkplain aitoa.structure.IBinarySearchOperator crossover}.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EA2<X, Y> extends Metaheuristic2<X, Y> {
// end relevant

  /** the crossover rate */
  public final double cr;
  /** the number of selected parents */
  public final int mu;

  /**
   * Create a new instance of the evolutionary algorithm
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   * @param pBinary
   *          the binary search operator
   * @param pCr
   *          the crossover rate
   * @param pMu
   *          the number of parents to be selected
   */
  public EA2(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary,
      final IBinarySearchOperator<X> pBinary, final double pCr,
      final int pMu) {
    super(pNullary, pUnary, pBinary);
    if ((pCr < 0d) || (pCr > 1d) || (!(Double.isFinite(pCr)))) {
      throw new IllegalArgumentException(
          "Invalid crossover rate: " + pCr); //$NON-NLS-1$
    }
    this.cr = pCr;
    if ((pMu < 1) || (pMu > 1_000_000)) {
      throw new IllegalArgumentException("Invalid mu: " + pMu); //$NON-NLS-1$
    }
    if ((pMu <= 1) && (pCr > 0d)) {
      throw new IllegalArgumentException(//
          "crossover rate must be 0 if mu is 1, but cr is " //$NON-NLS-1$
              + pCr);
    }
    this.mu = pMu;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// omitted: initialize local variables random, searchSpace, and
// array P of length mu and variable dest for new solution
// end relevant
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    int p2; // to hold index of second selected record

    final Record<X>[] P = new Record[this.mu];
    Record<X> dest =
        new Record<>(searchSpace.create(), Double.NaN);
// start relevant
// first generation: fill population with random solutions
    for (int i = P.length; (--i) >= 0;) {
      final X x = searchSpace.create(); // allocate point
      this.nullary.apply(x, random); // fill with random data
      P[i] = new Record<>(x, process.evaluate(x)); // evaluate
// end relevant
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
// start relevant
    }

    int p1 = -1; // index to iterate over first parent
    while (!process.shouldTerminate()) { // main loop
      p1 = (p1 + 1) % this.mu; // loop over parent population
      final Record<X> s1 = P[p1];
      if (random.nextDouble() <= this.cr) { // crossover!
        do { // find a second, different record
          p2 = random.nextInt(this.mu);
        } while (p2 == p1); // repeat until p1 != p2
// perform recombination of the two selected solutions
        this.binary.apply(s1.x, P[p2].x, dest.x, random);
      } else { // perform unary operation (i.e., mutation)
// create modified copy of parent using unary operator
        this.unary.apply(s1.x, dest.x, random);
      } // end mutation
      dest.quality = process.evaluate(dest.x);
      if (dest.quality <= s1.quality) { // compare dest with s1
        P[p1] = dest;
        dest = s1;
      }
    } // the end of the main loop
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry(//
        LogFormat.SETUP_BASE_ALGORITHM, "ea2")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", this.cr));//$NON-NLS-1$
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge(((("ea2_" + //$NON-NLS-1$
        this.mu) + '@') + this.cr), //
        this.unary, this.binary);
  }
// start relevant
}
// end relevant

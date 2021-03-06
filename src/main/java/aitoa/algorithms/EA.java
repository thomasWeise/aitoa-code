package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
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
import aitoa.utils.RandomUtils;

/**
 * The evolutionary algorithm (EA) is a population-based
 * metaheuristic. Here we implement the (mu+lambda)&nbsp;EA,
 * which begins by generating mu+lambda random candidate
 * solution. In each step (called generation), it preserves the
 * mu best points from the search space. From these mu points, it
 * derives lambda new points. Together with their mu "parents",
 * these form a population of size mu+lambda. In the next
 * iteration, we again preserve the mu best records. There are
 * two ways to derive "offspring" solutions from parents: We can
 * either apply an unary or a binary search operator. The unary
 * operator, in this context often called "mutation", derives one
 * new point in the search space from one existing point by
 * creating a slightly modified copy. This is the same kind of
 * operator applied in the
 * {@linkplain aitoa.algorithms.HillClimber}. The binary operator
 * takes two existing points from the search space to build a new
 * point by combining the characteristics of both "parents". It
 * is often referred to as "recombination" or "crossover" and the
 * idea is as follows: Both parents have been selected, i.e.,
 * they must be good in some way. If they are different, then
 * they must have different positive characteristics. If we are
 * lucky, then maybe we can merge these different characteristics
 * and obtain a new point which represents a combination of
 * different positive traits and is even better.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EA<X, Y> extends Metaheuristic2<X, Y> {
// end relevant

  /** the crossover rate */
  public final double cr;
  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;

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
   * @param pLambda
   *          the number of offspring to be created
   */
  public EA(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary,
      final IBinarySearchOperator<X> pBinary, final double pCr,
      final int pMu, final int pLambda) {
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
    if ((pLambda < 1) || (pLambda > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + pLambda); //$NON-NLS-1$
    }
    this.lambda = pLambda;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// end relevant
// start withoutcrossover
// omitted: initialize local variables random, searchSpace, and
// the array P of length mu+lambda
// end withoutcrossover
// start withcrossover
// omitted: initialize local variables random, searchSpace, and
// array P of length mu+lambda
// end withcrossover
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    int p2; // to hold index of second selected record

    final Record<X>[] P = new Record[this.mu + this.lambda];
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

    for (;;) { // main loop: one iteration = one generation
// sort the population: mu best records at front are selected
      Arrays.sort(P, Record.BY_QUALITY);
// shuffle the first mu solutions to ensure fairness
      RandomUtils.shuffle(random, P, 0, this.mu);
      int p1 = -1; // index to iterate over first parent

// overwrite the worse lambda solutions with new offsprings
      for (int index = P.length; (--index) >= this.mu;) {
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }

        final Record<X> dest = P[index];
        p1 = (p1 + 1) % this.mu; // step the parent 1 index
        final Record<X> sel = P[p1];
// end relevant
// start withcrossover
        if (random.nextDouble() <= this.cr) { // crossover!
          do { // find a second, different record
            p2 = random.nextInt(this.mu);
          } while (p2 == p1); // repeat until p1 != p2
// perform recombination of the two selected records
          this.binary.apply(sel.x, P[p2].x, dest.x, random);
        } else {
// end withcrossover
// start relevant
// create modified copy of parent using unary operator
          this.unary.apply(sel.x, dest.x, random);
// end relevant
// start withcrossover
        }
// end withcrossover
// start relevant
// map to solution/schedule and evaluate quality
        dest.quality = process.evaluate(dest.x);
      } // the end of the offspring generation
    } // the end of the main loop
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry(//
        LogFormat.SETUP_BASE_ALGORITHM, "ea")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", this.cr));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge(((((("ea_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda) + '@') + this.cr), //
        this.unary, this.binary);
  }
// start relevant
}
// end relevant

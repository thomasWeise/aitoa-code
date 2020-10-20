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
 * An {@linkplain aitoa.algorithms.EA evolutionary algorithm}
 * which restarts every couple of generations.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public final class EAWithRestarts<X, Y>
    extends Metaheuristic2<X, Y> {

  /** the crossover rate */
  public final double cr;
  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;
  /**
   * the number of generations without improvement until restart
   */
  public final int generationsUntilRestart;

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
   * @param pGenerationsUntilRestart
   *          the number of generations without improvement until
   *          restart
   */
  public EAWithRestarts(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary,
      final IBinarySearchOperator<X> pBinary, final double pCr,
      final int pMu, final int pLambda,
      final int pGenerationsUntilRestart) {
    super(pNullary, pUnary, pBinary);
    if ((pCr < 0d) || (pCr > 1d) || (!(Double.isFinite(pCr)))) {
      throw new IllegalArgumentException(
          "Invalid crossover rate: " + pCr); //$NON-NLS-1$
    }
    this.cr = pCr;
    if ((pMu < 1) || (pMu > 1_000_000)) {
      throw new IllegalArgumentException(//
          "Invalid mu: " + pMu); //$NON-NLS-1$
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

    if ((pGenerationsUntilRestart < 1)
        || (pGenerationsUntilRestart > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid generationsUntilRestart: " //$NON-NLS-1$
              + pGenerationsUntilRestart);
    }
    this.generationsUntilRestart = pGenerationsUntilRestart;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public void solve(final IBlackBoxProcess<X, Y> process) {
    // create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    int p2;

    final Record<X>[] P = new Record[this.mu + this.lambda];

    while (!process.shouldTerminate()) { // restart
      double bestF = Double.POSITIVE_INFINITY;
      int nonImprovedGen = 0;

// first generation: fill population with random solutions
      for (int i = P.length; (--i) >= 0;) {
        final X x = searchSpace.create();
        this.nullary.apply(x, random);
        P[i] = new Record<>(x, process.evaluate(x));
        if (process.shouldTerminate()) {
          return;
        }
      }

      while (nonImprovedGen < this.generationsUntilRestart) {
// main loop: one iteration = one generation
        ++nonImprovedGen; // assume no improvement

// sort the P: mu best records at front are selected
        Arrays.sort(P, Record.BY_QUALITY);
// shuffle mating pool to ensure fairness if lambda<mu
        RandomUtils.shuffle(random, P, 0, this.mu);
        int p1 = -1; // index to iterate over first parent

// overwrite the worse lambda solutions with new offsprings
        for (int index = P.length; (--index) >= this.mu;) {
          if (process.shouldTerminate()) {
            return; // return best solution
          }

          final Record<X> dest = P[index];
          p1 = (p1 + 1) % this.mu;
          final Record<X> parent1 = P[p1];

          if (random.nextDouble() <= this.cr) { // crossover!
            do { // find a second parent who is different from
              p2 = random.nextInt(this.mu);
            } while (p2 == p1);

            this.binary.apply(parent1.x, P[p2].x, dest.x,
                random);
          } else { // otherwise create modified copy of p1
            this.unary.apply(parent1.x, dest.x, random);
          }

          // map to solution/schedule and evaluate
          dest.quality = process.evaluate(dest.x);
          if (dest.quality < bestF) { // we improved
            bestF = dest.quality; // remember best quality
            nonImprovedGen = 0; // reset non-improved generation
          }
        } // the end of the offspring generation
      } // the end of the generation loop
    } // end of the main loop for independent restarts
  }

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
    output.write(LogFormat.mapEntry("restarts", true)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("generationsUntilRestart", //$NON-NLS-1$
        this.generationsUntilRestart));
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge(((((((("ea_rs_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda) + '@') + this.cr) + '_')
        + this.generationsUntilRestart), this.unary,
        this.binary);
  }
}

package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.Individual;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic2;
import aitoa.utils.Experiment;
import aitoa.utils.RandomUtils;

/**
 * An {@linkplain aitoa.algorithms.EA evolutionary algorithm}
 * which clears the population from candidate solutions with
 * identical objective values before the reproduction step. This
 * ensures that all "parents" from which new points in the search
 * space are derived have a different solution quality. This, in
 * turn, implies that they are different candidate solutions.
 * These, in turn, must be the result of representation mappings
 * applied to different points in the search space.
 * <p>
 * Please notice that the direct, simple comparisons of the
 * objective values applied here in form of
 * {@code ind.quality > P[u - 1].quality} only make sense when
 * the objective values can exactly be represented in the
 * {@code double} range. This is the case in combinatorial
 * problems where these values are integers. In numerical
 * problems, we could, e.g., base the comparisons on some
 * similarity thresholds.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EAWithClearing<X, Y>
    extends Metaheuristic2<X, Y> {
// end relevant

  /** the crossover rate */
  public final double cr;
  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;

  /**
   * Create a new instance of the evolutionary algorithm with
   * clearing
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
  public EAWithClearing(final INullarySearchOperator<X> pNullary,
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
// Omitted: Initialize local variables random, searchSpace, set
// arrays P of length mu+lambda, and array T to null. Fill P with
// random solutions + evaluate.
// end relevant
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    int p2;

    final Individual<X>[] P =
        new Individual[this.mu + this.lambda];

// first generation: fill population with random individuals
// start relevant
    for (int i = P.length; (--i) >= 0;) {
      final X x = searchSpace.create();
      this.nullary.apply(x, random);
      P[i] = new Individual<>(x, process.evaluate(x));
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
    }

    while (!process.shouldTerminate()) { // main loop
      RandomUtils.shuffle(random, P, 0, P.length); // make fair
      final int u = Utils.qualityBasedClearing(P, this.mu);
// Now we have 1 <= u <= mu unique solutions.
      RandomUtils.shuffle(random, P, 0, u); // for fairness
      int p1 = -1; // index to iterate over first parent
// Overwrite the worse (mu + lambda - u) solutions.
      for (int index = P.length; (--index) >= u;) {
// Omitted: Quit loop if process.shouldTerminate()
// end relevant
        if (process.shouldTerminate()) { // Finished.
          return; // The best solution is stored in process.
        }
// start relevant
        final Individual<X> dest = P[index]; // offspring
        p1 = (p1 + 1) % u; // parent 1 index
        final Individual<X> sel = P[p1]; // parent 1
        if ((u >= 2) && (random.nextDouble() <= this.cr)) {
          do { // find a second, different record
            p2 = random.nextInt(u);
          } while (p2 == p1); // Of course, can't be p1.
          this.binary.apply(sel.x, P[p2].x, dest.x, random);
        } else { // Otherwise: Mutation.
          this.unary.apply(sel.x, dest.x, random);
        }
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
    output.write(LogFormat.mapEntry("clearing", true)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge(((((("eac_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda) + '@') + this.cr),
        this.unary, this.binary);
  }
// start relevant
}
// end relevant

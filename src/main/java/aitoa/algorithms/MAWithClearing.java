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
import aitoa.utils.Experiment;
import aitoa.utils.RandomUtils;

/**
 * A {@linkplain aitoa.algorithms.MA memetic algorithm} is a
 * combination of a {@linkplain aitoa.algorithms.EA evolutionary
 * algorithm} with a local search. Our type of memetic algorithm
 * always applies the binary operator to find new points in the
 * search space and then refines them with a
 * {@linkplain aitoa.algorithms.HillClimber2 first-improvement
 * local search} based on a unary operator. This MA with clearing
 * here also, well, clears the population from candidate
 * solutions with identical objective values before the
 * reproduction step.
 * <p>
 * All candidate solutions represented in the population will
 * always be local optima before entering the binary operators.
 * If these operators work well, they may jump close to other
 * local optima. The clearing ensures that all "parents" from
 * which new points in the search space are derived have a
 * different solution quality. This, in turn, implies that they
 * are different candidate solutions. These, in turn, must be the
 * result of representation mappings applied to different points
 * in the search space.
 * <p>
 * Please notice that the direct, simple comparisons of the
 * objective values applied here in form of
 * {@code ind.quality > P[u - 1].quality} only make sense in
 * combinatorial problems where these values are integers. In
 * numerical problems, we could, e.g., base the comparisons on
 * some similarity thresholds.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public final class MAWithClearing<X, Y>
    extends Metaheuristic2<X, Y> {

  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;
  /** the maximum number of local search steps */
  public final int maxLSSteps;

  /**
   * Create a new instance of the memetic algorithm
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   * @param pBinary
   *          the binary search operator
   * @param pMu
   *          the number of parents to be selected
   * @param pLambda
   *          the number of offspring to be created
   * @param pMaxLSSteps
   *          the maximum number of local search steps
   */
  public MAWithClearing(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary,
      final IBinarySearchOperator<X> pBinary, final int pMu,
      final int pLambda, final int pMaxLSSteps) {
    super(pNullary, pUnary, pBinary);
    if ((pMu <= 1) || (pMu > 1_000_000)) {
      throw new IllegalArgumentException("Invalid mu: " + pMu); //$NON-NLS-1$
    }
    this.mu = pMu;
    if ((pLambda < 1) || (pLambda > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + pLambda); //$NON-NLS-1$
    }
    this.lambda = pLambda;
    if (pMaxLSSteps <= 0) {
      throw new IllegalArgumentException(
          "Invalid number of maximum local search steps: " //$NON-NLS-1$
              + pMaxLSSteps);
    }
    this.maxLSSteps = pMaxLSSteps;
    if (!pUnary.canEnumerate()) {
      throw new IllegalArgumentException(//
          "Unary operator cannot enumerate neighborhood."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public void solve(final IBlackBoxProcess<X, Y> process) {
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final X temp = searchSpace.create();
    final LSRecord<X>[] P = new LSRecord[this.mu + this.lambda];
    boolean improved = false;
    int p2 = -1;

    restart: while (!process.shouldTerminate()) {
// first generation: fill population with random solutions
      for (int i = P.length; (--i) >= 0;) {
        final X x = searchSpace.create();
        this.nullary.apply(x, random);
        P[i] = new LSRecord<>(x, process.evaluate(x));
        if (process.shouldTerminate()) {
          return;
        }
      }

      while (!process.shouldTerminate()) { // main loop
        for (final LSRecord<X> ind : P) {
          if (ind.isOptimum) {
            continue;
          }
          int steps = this.maxLSSteps;
          do { // local search in style of HillClimber2
            improved = this.unary.enumerate(random, ind.x, temp, //
                point -> {
                  final double newQuality =
                      process.evaluate(point);
                  if (newQuality < ind.quality) { // better?
                    ind.quality = newQuality; // store quality
                    searchSpace.copy(point, ind.x); // store
                    return true; // exit to next loop
                  } // if we get here, point is not better
                  return process.shouldTerminate();
                }); // repeat until no improvement or time up
            if (process.shouldTerminate()) { // we return
              return; // best solution is stored in process
            }
          } while (improved && ((--steps) > 0));
          ind.isOptimum = !improved; // is it an optimum?
        } // end of 1 ls iteration: we have refined 1 solution

        RandomUtils.shuffle(random, P, 0, P.length); // fair
        final int u = Utils.qualityBasedClearing(P, this.mu);
        if (u <= 1) { // 1 <= u <= mu unique solutions
          continue restart; // if u==1, restart, because
        } // then recombination makes no sense
// now we have 2 <= u <= mu unique solutions
// shuffle the first unique solutions to ensure fairness
        RandomUtils.shuffle(random, P, 0, u);
        int p1 = -1; // index to iterate over first parent

// override the worse (mu+lambda-u) solutions
        for (int index = P.length; (--index) >= u;) {
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
          final LSRecord<X> dest = P[index];
          p1 = (p1 + 1) % u;
          final LSRecord<X> sel = P[p1];
// to hold index of second selected record
          do {
            p2 = random.nextInt(u);
          } while (p2 == p1);
// perform recombination and compute quality
          this.binary.apply(sel.x, P[p2].x, dest.x, random);
          dest.quality = process.evaluate(dest.x);
          dest.isOptimum = false;
        } // the end of the offspring generation
      } // the end of the main loop
    } // end of the restart loop
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry(//
        LogFormat.SETUP_BASE_ALGORITHM, "ma")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", 1d));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", true)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("maxLSSteps", //$NON-NLS-1$
        this.maxLSSteps));
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge((("mac_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda,
        (this.maxLSSteps >= Integer.MAX_VALUE) ? null
            : Integer.toString(this.maxLSSteps),
        this.unary, this.binary);
  }
}

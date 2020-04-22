package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.utils.RandomUtils;

/**
 * A memetic algorithm is a combination of a
 * {@linkplain aitoa.algorithms.EA evolutionary algorithm} with a
 * local search. Our type of memetic algorithm always applies the
 * binary operator to find new points in the search space and
 * then refines them with a
 * {@linkplain aitoa.algorithms.HillClimber2 first-improvement
 * local search} based on a unary operator.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class MA<X, Y> implements IMetaheuristic<X, Y> {
// end relevant

  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;
  /** the maximum number of local search steps */
  public final int maxLSSteps;

  /**
   * Create a new instance of the evolutionary algorithm
   *
   * @param _mu
   *          the number of parents to be selected
   * @param _lambda
   *          the number of offspring to be created
   * @param _maxLSSteps
   *          the maximum number of local search steps
   */
  public MA(final int _mu, final int _lambda,
      final int _maxLSSteps) {
    super();
    if ((_mu <= 1) || (_mu > 1_000_000)) {
      throw new IllegalArgumentException("Invalid mu: " + _mu); //$NON-NLS-1$
    }
    this.mu = _mu;
    if ((_lambda < 1) || (_lambda > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + _lambda); //$NON-NLS-1$
    }
    this.lambda = _lambda;
    if (_maxLSSteps <= 0) {
      throw new IllegalArgumentException(
          "Invalid number of maximum local search steps: " //$NON-NLS-1$
              + _maxLSSteps);
    }
    this.maxLSSteps = _maxLSSteps;
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "ma")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", 1d));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("maxLSSteps", //$NON-NLS-1$
        this.maxLSSteps));
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    final String s = ((("ma_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda);
    if (this.maxLSSteps >= Integer.MAX_VALUE) {
      return s;
    }
    return (s + '_') + this.maxLSSteps;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// the initialization of local variables is omitted for brevity
// end relevant
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator();
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator();
    final IBinarySearchOperator<X> binary =
        process.getBinarySearchOperator();
    boolean improved = false;
    final X temp = searchSpace.create();
    int p2;

    final LSIndividual<X>[] P =
        new LSIndividual[this.mu + this.lambda];
// start relevant
// first generation: fill population with random individuals
    for (int i = P.length; (--i) >= 0;) {
// set P[i] = random individual (code omitted)
// end relevant
      final X x = searchSpace.create();
      nullary.apply(x, random);
      P[i] = new LSIndividual<>(x, process.evaluate(x));
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
// start relevant
    }

    while (!process.shouldTerminate()) { // main loop
      for (final LSIndividual<X> ind : P) {
// If ind is not known to be local optimum, refine it with local
// search à la HillClimber2 for a given number of maximum steps
// (code omitted for brevity).
// end relevant
        if (ind.isOptimum) {
          continue;
        }
        int steps = this.maxLSSteps;
        do { // local search in style of HillClimber2
          improved = unary.enumerate(random, ind.x, temp, //
              (point) -> {
                final double newQuality =
                    process.evaluate(point);
                if (newQuality < ind.quality) { // better?
                  ind.quality = newQuality; // store quality
                  searchSpace.copy(point, ind.x); // store point
                  return true; // exit to next loop
                } // if we get here, point is not better
                return process.shouldTerminate();
              }); // repeat this until no improvement or time up
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
        } while (improved && ((--steps) > 0));
        ind.isOptimum = !improved; // is it a local optimum?
// start relevant
      } // end of 1 ls iteration: we have refined 1 solution
// sort the population: mu best individuals at front are selected
      Arrays.sort(P, Individual.BY_QUALITY);
// shuffle the first mu solutions to ensure fairness
      RandomUtils.shuffle(random, P, 0, this.mu);
      int p1 = -1; // index to iterate over first parent

// override the worse lambda solutions with new offsprings
      for (int index = P.length; (--index) >= this.mu;) {
// end relevant
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }
// start relevant
        final LSIndividual<X> dest = P[index];
        final LSIndividual<X> sel = P[(++p1) % this.mu];

        do { // find a second, different record
          p2 = random.nextInt(this.mu);
        } while (p2 == p1);
// perform recombination of the two selected individuals
        binary.apply(sel.x, P[p2].x, dest.x, random);
        dest.quality = process.evaluate(dest.x);
// end relevant
        dest.isOptimum = false;
// start relevant
      } // the end of the offspring generation
    } // the end of the main loop
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public final String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return IMetaheuristic.getSetupNameWithUnaryAndBinaryOperator(//
        this, builder);
  }
// start relevant
}
// end relevant

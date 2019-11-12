package aitoa.algorithms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.algorithms.FitnessAssignmentProcess.FitnessIndividual;
import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.utils.RandomUtils;

/**
 * A memetic algorithm applying a fitness assignment process.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public class MAWithFitness<X, Y>
    implements IMetaheuristic<X, Y> {
// end relevant

  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;
  /** the maximum number of local search steps */
  public final int maxLSSteps;
  /** the fitness assignment process */
  public final FitnessAssignmentProcess<? super X> fitness;

  /**
   * Create a new instance of the evolutionary algorithm
   *
   * @param _mu
   *          the number of parents to be selected
   * @param _lambda
   *          the number of offspring to be created
   * @param _maxLSSteps
   *          the maximum number of local search steps
   * @param _fitness
   *          the fitness assignment process
   */
  public MAWithFitness(final int _mu, final int _lambda,
      final int _maxLSSteps,
      final FitnessAssignmentProcess<? super X> _fitness) {
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

    this.fitness = Objects.requireNonNull(_fitness);
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final BufferedWriter output)
      throws IOException {

    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "ma")); //$NON-NLS-1$
    output.newLine();
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("cr", 1d));//$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("pruning", false)); //$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("maxLSSteps", //$NON-NLS-1$
        this.maxLSSteps));
    output.newLine();
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        this.fitness));
    output.newLine();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    final String s = ((((("ma_" + //$NON-NLS-1$
        this.fitness.toString()) + '_') + this.mu) + '+')
        + this.lambda);
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

    final FitnessIndividual<X>[] P =
        new FitnessIndividual[this.mu + this.lambda];
    this.fitness.initialize();
// start relevant
// first generation: fill population with random individuals
    for (int i = P.length; (--i) >= 0;) {
// set P[i] = random individual (code omitted)
// end relevant
      final X x = searchSpace.create();
      nullary.apply(x, random);
      P[i] = new FitnessIndividual<>(x, process.evaluate(x));
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
// start relevant
    }
    int localSearchStart = 0; // at first, apply ls to all

    while (!process.shouldTerminate()) { // main loop
      for (int i = P.length; (--i) >= localSearchStart;) {
        final FitnessIndividual<X> ind = P[i];
        int steps = this.maxLSSteps;
// refine P[i] with local search à la HillClimber2 (code omitted)
// end relevant
        do { // local search in style of HillClimber2
          improved = unary.enumerate(random, ind.x, temp, //
              (point) -> {
                final double newQuality =
                    process.evaluate(point);
                if (newQuality < ind.quality) { // better?
                  ind.quality = newQuality; // store quality
                  searchSpace.copy(point, ind.x); // store point
                  return (true); // exit to next loop
                } // if we get here, point is not better
                return process.shouldTerminate();
              }); // repeat this until no improvement or time is
                  // up
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
        } while (improved && ((--steps) > 0));
      } // end of 1 ls iteration: we have refined 1 solution
// start relevant
// sort the population: mu best individuals at front are selected
      this.fitness.assignFitness(P);
      Arrays.sort(P);
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
        final FitnessIndividual<X> dest = P[index];
        final FitnessIndividual<X> sel = P[(++p1) % this.mu];

        do { // find a second, different record
          p2 = random.nextInt(this.mu);
        } while (p2 == p1);
// perform recombination of the two selected individuals
        binary.apply(sel.x, P[p2].x, dest.x, random);
// map to solution/schedule and evaluate quality
        dest.quality = process.evaluate(dest.x);
      } // the end of the offspring generation

      // end relevant
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
      // start relevant
      localSearchStart = this.mu;
    } // the end of the main loop
  }
}
// end relevant

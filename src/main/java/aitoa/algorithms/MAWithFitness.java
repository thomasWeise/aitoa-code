package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
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
 * A memetic algorithm applying a fitness assignment process.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public final class MAWithFitness<X, Y>
    implements IMetaheuristic<X, Y> {

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
   * @param pMu
   *          the number of parents to be selected
   * @param pLambda
   *          the number of offspring to be created
   * @param pMaxLSSteps
   *          the maximum number of local search steps
   * @param pFitness
   *          the fitness assignment process
   */
  public MAWithFitness(final int pMu, final int pLambda,
      final int pMaxLSSteps,
      final FitnessAssignmentProcess<? super X> pFitness) {
    super();
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

    this.fitness = Objects.requireNonNull(pFitness);
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
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
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        this.fitness));
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final String s = ((((("ma_" + //$NON-NLS-1$
        this.fitness.toString()) + '_') + this.mu) + '+')
        + this.lambda);
    if (this.maxLSSteps >= Integer.MAX_VALUE) {
      return s;
    }
    return (s + '_') + this.maxLSSteps;
  }

  /** {@inheritDoc} */
  @Override
  public String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return IMetaheuristic.getSetupNameWithUnaryAndBinaryOperator(//
        this, builder);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public void solve(final IBlackBoxProcess<X, Y> process) {
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

    final LSFitnessIndividual<X>[] P =
        new LSFitnessIndividual[this.mu + this.lambda];
    this.fitness.initialize();
// first generation: fill population with random individuals
    for (int i = P.length; (--i) >= 0;) {
// set P[i] = random individual (code omitted)
      final X x = searchSpace.create();
      nullary.apply(x, random);
      P[i] = new LSFitnessIndividual<>(x, process.evaluate(x));
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
    }

    while (!process.shouldTerminate()) { // main loop
      for (final LSFitnessIndividual<X> ind : P) {
        if (ind.isOptimum) {
          continue;
        }
        int steps = this.maxLSSteps;
// refine ind with local search a la HillClimber2 (code omitted)
        do { // local search in style of HillClimber2
          improved = unary.enumerate(random, ind.x, temp, //
              point -> {
                final double newQuality =
                    process.evaluate(point);
                if (newQuality < ind.quality) { // better?
                  ind.quality = newQuality; // store quality
                  searchSpace.copy(point, ind.x); // store point
                  return (true); // exit to next loop
                } // if we get here, point is not better
                return process.shouldTerminate();
              }); // repeat this until no improvement or time up
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
        } while (improved && ((--steps) > 0));
        ind.isOptimum = !improved; // is it a local optimum?
      } // end of 1 ls iteration: we have refined 1 solution

// sort the population: mu best individuals at front are selected
      this.fitness.assignFitness(P);
      Arrays.sort(P, this.fitness);
// shuffle the first mu solutions to ensure fairness
      RandomUtils.shuffle(random, P, 0, this.mu);
      int p1 = -1; // index to iterate over first parent

// override the worse lambda solutions with new offsprings
      for (int index = P.length; (--index) >= this.mu;) {
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }

        final LSFitnessIndividual<X> dest = P[index];
        final LSFitnessIndividual<X> sel = P[(++p1) % this.mu];

        do { // find a second, different record
          p2 = random.nextInt(this.mu);
        } while (p2 == p1);
// perform recombination of the two selected individuals
        binary.apply(sel.x, P[p2].x, dest.x, random);
// map to solution/schedule and evaluate quality
        dest.quality = process.evaluate(dest.x);
        dest.isOptimum = false;
      } // the end of the offspring generation

      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
    } // the end of the main loop
  }
}

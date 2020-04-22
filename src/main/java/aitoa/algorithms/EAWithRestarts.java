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
 * An {@linkplain aitoa.algorithms.EA evolutionary algorithm}
 * which restarts every couple of generations.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public final class EAWithRestarts<X, Y>
    implements IMetaheuristic<X, Y> {

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
   * @param _cr
   *          the crossover rate
   * @param _mu
   *          the number of parents to be selected
   * @param _lambda
   *          the number of offspring to be created
   * @param _generationsUntilRestart
   *          the number of generations without improvement until
   *          restart
   */
  public EAWithRestarts(final double _cr, final int _mu,
      final int _lambda, final int _generationsUntilRestart) {
    super();
    if ((_cr < 0d) || (_cr > 1d) || (!(Double.isFinite(_cr)))) {
      throw new IllegalArgumentException(
          "Invalid crossover rate: " + _cr); //$NON-NLS-1$
    }
    this.cr = _cr;
    if ((_mu < 1) || (_mu > 1_000_000)) {
      throw new IllegalArgumentException(//
          "Invalid mu: " + _mu); //$NON-NLS-1$
    }
    if ((_mu <= 1) && (_cr > 0d)) {
      throw new IllegalArgumentException(//
          "crossover rate must be 0 if mu is 1, but cr is " //$NON-NLS-1$
              + _cr);
    }
    this.mu = _mu;
    if ((_lambda < 1) || (_lambda > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + _lambda); //$NON-NLS-1$
    }
    this.lambda = _lambda;

    if ((_generationsUntilRestart < 1)
        || (_generationsUntilRestart > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid generationsUntilRestart: " //$NON-NLS-1$
              + _generationsUntilRestart);
    }
    this.generationsUntilRestart = _generationsUntilRestart;
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "ea")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    IMetaheuristic.super.printSetup(output);
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
  public final String toString() {
    return ((((((("ea_rs_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda) + '@') + this.cr) + '_')
        + this.generationsUntilRestart);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public final void solve(final IBlackBoxProcess<X, Y> process) {
    // create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator();
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator();
    final IBinarySearchOperator<X> binary =
        process.getBinarySearchOperator();

    final Individual<X>[] population =
        new Individual[this.mu + this.lambda];

    while (!process.shouldTerminate()) { // restart
      double bestF = Double.POSITIVE_INFINITY;
      int nonImprovedGen = 0;

// first generation: fill population with random individuals
      for (int i = population.length; (--i) >= 0;) {
        final X x = searchSpace.create();
        nullary.apply(x, random);
        population[i] = new Individual<>(x, process.evaluate(x));
        if (process.shouldTerminate()) {
          return;
        }
      }

      while (nonImprovedGen < this.generationsUntilRestart) {
// main loop: one iteration = one generation
        ++nonImprovedGen; // assume no improvement

// sort the population: mu best individuals at front are selected
        Arrays.sort(population, Individual.BY_QUALITY);
// shuffle mating pool to ensure fairness if lambda<mu
        RandomUtils.shuffle(random, population, 0, this.mu);
        int p1 = -1; // index to iterate over first parent

// override the worse lambda solutions with new offsprings
        for (int index = population.length;
            (--index) >= this.mu;) {
          if (process.shouldTerminate()) {
            return; // return best solution
          }

          final Individual<X> dest = population[index];
          p1 = (p1 + 1) % this.mu;
          final Individual<X> parent1 = population[p1]; // parent
                                                        // 1

          if (random.nextDouble() <= this.cr) { // crossover!
            int p2;
            do { // find a second parent who is different from
              p2 = random.nextInt(this.mu);
            } while (p2 == p1);

            binary.apply(parent1.x, population[p2].x, dest.x,
                random); // perform recombination
          } else { // otherwise create modified copy of first
                   // parent
            unary.apply(parent1.x, dest.x, random);
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
  public final String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return IMetaheuristic.getSetupNameWithUnaryAndBinaryOperator(//
        this, builder);
  }
}

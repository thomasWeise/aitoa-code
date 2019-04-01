package aitoa.algorithms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
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
public class EAWithRestarts<X, Y>
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
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("base_algorithm: ea"); //$NON-NLS-1$
    output.newLine();
    IMetaheuristic.super.printSetup(output);
    output.write("mu: "); //$NON-NLS-1$
    output.write(Integer.toString(this.mu));
    output.newLine();
    output.write("lambda: ");//$NON-NLS-1$
    output.write(Integer.toString(this.lambda));
    output.newLine();
    output.write("cr: ");//$NON-NLS-1$
    output.write(Double.toString(this.cr));
    output.newLine();
    output.write("cr(inhex): ");//$NON-NLS-1$
    output.write(Double.toHexString(this.cr));
    output.newLine();
    output.write("generationsUntilRestart: ");//$NON-NLS-1$
    output.write(Integer.toString(this.generationsUntilRestart));
    output.newLine();
    output.write("pruning: false"); //$NON-NLS-1$
    output.newLine();
    output.write("restarts: true"); //$NON-NLS-1$
    output.newLine();
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
// end relevant
        if (process.shouldTerminate()) {
          return;
        }
// start relevant
      }

      while (nonImprovedGen < this.generationsUntilRestart) {
// main loop: one iteration = one generation
        ++nonImprovedGen; // assume no improvement

// sort the population: mu best individuals at front are selected
        Arrays.sort(population);
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
          final Individual<X> parent1 =
              population[(++p1) % this.mu]; // parent 1

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

  /**
   * the individual record: hold one point in search space and
   * its quality
   *
   * @param <X>
   *          the data structure of the search space
   */
  private static final class Individual<X>
      implements Comparable<Individual<X>> {
    /** the point in the search space */
    final X x;
    /** the quality */
    double quality;

    /**
     * create the individual record
     *
     * @param _x
     *          the point in the search space
     * @param _q
     *          the quality
     */
    Individual(final X _x, final double _q) {
      super();
      this.x = Objects.requireNonNull(_x);
      this.quality = _q;
    }

    /**
     * compare two individuals: the one with smaller quality is
     * better.
     *
     * @return -1 if this record is better than {@code o}, 1 if
     *         it is worse, 0 otherwise
     */
    @Override
    public final int compareTo(final Individual<X> o) {
      return Double.compare(this.quality, o.quality);
    }
  }
}

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
import aitoa.utils.RandomUtils;
import aitoa.utils.ReflectionUtils;

/**
 * The evolutionary algorithm (EA) which employs a fitness
 * assignment process.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public class EAWithFitness<X, Y>
    implements IMetaheuristic<X, Y> {
// end relevant

  /** the crossover rate */
  public final double cr;
  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;
  /** the fitness assignment process */
  public final FitnessAssignmentProcess<? super X> fitness;

  /**
   * Create a new instance of the evolutionary algorithm
   *
   * @param _cr
   *          the crossover rate
   * @param _mu
   *          the number of parents to be selected
   * @param _lambda
   *          the number of offspring to be created
   * @param _fitness
   *          the fitness assignment process
   */
  public EAWithFitness(final double _cr, final int _mu,
      final int _lambda,
      final FitnessAssignmentProcess<? super X> _fitness) {
    super();
    if ((_cr < 0d) || (_cr > 1d) || (!(Double.isFinite(_cr)))) {
      throw new IllegalArgumentException(
          "Invalid crossover rate: " + _cr); //$NON-NLS-1$
    }
    this.cr = _cr;
    if ((_mu < 1) || (_mu > 1_000_000)) {
      throw new IllegalArgumentException("Invalid mu: " + _mu); //$NON-NLS-1$
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

    this.fitness = Objects.requireNonNull(_fitness);
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("base_algorithm: fitness_ea"); //$NON-NLS-1$
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
    output.write("pruning: false"); //$NON-NLS-1$
    output.newLine();
    output.write("restarts: false"); //$NON-NLS-1$
    output.newLine();
    output.write("fitness: "); //$NON-NLS-1$
    output.write(this.fitness.toString());
    output.newLine();
    output.write("fitness_class: "); //$NON-NLS-1$
    output.write(ReflectionUtils.className(this.fitness));
    output.newLine();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((((((("ea_" + //$NON-NLS-1$
        this.fitness.toString()) + '_') + this.mu) + '+')
        + this.lambda) + '@') + this.cr);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// omitted: initialize local variables random, searchSpace,
// nullary, unary, binary, and array P of length mu+lambda
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

    final FitnessIndividual<X>[] P =
        new FitnessIndividual[this.mu + this.lambda];
// start relevant

    this.fitness.initialize();

// first generation: fill population with random individuals
    for (int i = P.length; (--i) >= 0;) {
      final X x = searchSpace.create();
      nullary.apply(x, random);
      P[i] = new FitnessIndividual<>(x, process.evaluate(x));
// end relevant
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
// start relevant
    }

    for (;;) { // main loop: one iteration = one generation
// sort the population: mu best individuals at front are selected
      this.fitness.assignFitness(P);
      Arrays.sort(P);
// shuffle the first mu solutions to ensure fairness
      RandomUtils.shuffle(random, P, 0, this.mu);
      int p1 = -1; // index to iterate over first parent

// override the worse lambda solutions with new offsprings
      for (int index = P.length; (--index) >= this.mu;) {
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }

        final FitnessIndividual<X> dest = P[index];
        final FitnessIndividual<X> sel = P[(++p1) % this.mu];
        if (random.nextDouble() <= this.cr) { // crossover!
          int p2; // to hold index of second selected record
          do { // find a second, different record
            p2 = random.nextInt(this.mu);
          } while (p2 == p1);
// perform recombination of the two selected individuals
          binary.apply(sel.x, P[p2].x, dest.x, random);
        } else {
// create modified copy of parent using unary operator
          unary.apply(sel.x, dest.x, random);
        }
// map to solution/schedule and evaluate quality
        dest.quality = process.evaluate(dest.x);
      } // the end of the offspring generation
// end relevant
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
// start relevant
    } // the end of the main loop
  }
}
// end relevant

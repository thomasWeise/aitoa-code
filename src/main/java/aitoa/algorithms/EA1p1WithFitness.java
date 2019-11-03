package aitoa.algorithms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import aitoa.algorithms.FitnessAssignmentProcess.FitnessIndividual;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.utils.ReflectionUtils;

/**
 * The (1+1)-EA is a hill climbing algorithm remembers the
 * current best solution and tries to improve upon it in each
 * step. It does so by applying a modification to this solution
 * and keeps the modified solution if and only if it is better or
 * equal. It is very similar to the {@link HillClimber}. It is
 * slightly different from our {@link EA} implementation with mu
 * and lambda set to 1: In the case that the new solution is
 * equally good as the old one, the (1+1)-EA takes it always,
 * whereas our other EA implementation takes it with 50%
 * probability.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EA1p1WithFitness<X, Y>
    implements IMetaheuristic<X, Y> {
  /** the fitness assignment process */
  public final FitnessAssignmentProcess<? super X> fitness;

// end relevant
  /**
   * create
   *
   * @param _fitness
   *          the fitness assignment process
   */
  public EA1p1WithFitness(
      final FitnessAssignmentProcess<? super X> _fitness) {
    super();
    this.fitness = Objects.requireNonNull(_fitness);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables pop, nullary, unary, random
// end relevant
    final FitnessIndividual<X>[] pop = new FitnessIndividual[] {
        new FitnessIndividual<>(
            process.getSearchSpace().create(),
            Double.POSITIVE_INFINITY),
        new FitnessIndividual<>(
            process.getSearchSpace().create(),
            Double.POSITIVE_INFINITY) };

    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get unary op

    final Random random = process.getRandom();// get random gen
    this.fitness.initialize();
// start relevant
    nullary.apply(pop[0].x, random); // create and evaluate first
    pop[0].quality = process.evaluate(pop[0].x); // individual

    while (!process.shouldTerminate()) {// repeat until budget exhausted
// create a slightly modified copy of x_best and store in x_cur
      unary.apply(pop[0].x, pop[1].x, random);
// map x_cur from X to Y and evaluate candidate solution
      pop[1].quality = process.evaluate(pop[1].x);
      this.fitness.assignFitness(pop); // compute fitness
      if (pop[0].fitness >= pop[1].fitness) {
        final FitnessIndividual<X> temp = pop[0];
        pop[0] = pop[1]; // if new individual has better or
        pop[1] = temp; // equal fitness: accept it
      }
    } // until time is up
  } // process will have remembered the best candidate solution
// end relevant

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "(1+1)-EA_" + //$NON-NLS-1$
        this.fitness.toString();
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("base_algorithm: fitness_1+1_ea"); //$NON-NLS-1$
    output.newLine();
    IMetaheuristic.super.printSetup(output);
    output.write("mu: "); //$NON-NLS-1$
    output.write(Integer.toString(1));
    output.newLine();
    output.write("lambda: ");//$NON-NLS-1$
    output.write(Integer.toString(1));
    output.newLine();
    output.write("cr: ");//$NON-NLS-1$
    output.write(Double.toString(0));
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

// start relevant
}
// end relevant

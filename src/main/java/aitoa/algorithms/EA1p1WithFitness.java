package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;

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
public final class EA1p1WithFitness<X, Y>
    implements IMetaheuristic<X, Y> {
  /** the fitness assignment process */
  public final FitnessAssignmentProcess<? super X> fitness;

  /**
   * create
   *
   * @param pFitness
   *          the fitness assignment process
   */
  public EA1p1WithFitness(
      final FitnessAssignmentProcess<? super X> pFitness) {
    super();
    this.fitness = Objects.requireNonNull(pFitness);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables P, nullary, unary, random
// end relevant
    final FitnessIndividual<X>[] P = new FitnessIndividual[] {
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
    nullary.apply(P[0].x, random); // create and evaluate first
    P[0].quality = process.evaluate(P[0].x); // individual

    while (!process.shouldTerminate()) {
// create a slightly modified copy of xBest and store in xCur
      unary.apply(P[0].x, P[1].x, random);
// map xCur from X to Y and evaluate candidate solution
      P[1].quality = process.evaluate(P[1].x);
      this.fitness.assignFitness(P); // compute fitness
      if (this.fitness.compare(P[0], P[1]) >= 0) {
        final FitnessIndividual<X> temp = P[0];
        P[0] = P[1]; // if new individual has better or
        P[1] = temp; // equal fitness: accept it
      }
    } // until time is up
  } // process will have remembered the best candidate solution
// end relevant

  /** {@inheritDoc} */
  @Override
  public String toString() {
    if (this.fitness instanceof IntFFA) {
      return "(1+1)-FEA";//$NON-NLS-1$
    }
    return "(1+1)-EA_" + //$NON-NLS-1$
        this.fitness.toString();
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "fitness_1+1_ea")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", 1));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", 1));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", 0));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        this.fitness));
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return IMetaheuristic.getSetupNameWithUnaryOperator(//
        this, builder);
  }
// start relevant
}
// end relevant

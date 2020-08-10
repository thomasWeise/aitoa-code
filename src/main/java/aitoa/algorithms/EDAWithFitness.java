package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IModel;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;

/**
 * An {@linkplain aitoa.algorithms.EDA estimation of distribution
 * algorithm} applying a fitness assignment process.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public final class EDAWithFitness<X, Y>
    implements IMetaheuristic<X, Y> {

  /** the number of solution to be selected */
  public final int mu;
  /** the number of new points per generation */
  public final int lambda;
  /** the model */
  public final IModel<X> model;
  /** the fitness assignment process */
  public final FitnessAssignmentProcess<? super X> fitness;

  /**
   * Create a new instance of the estimation of distribution
   *
   * @param pMu
   *          the number of solution to be selected
   * @param pLambda
   *          the number of new points per generation
   * @param pModel
   *          the model
   * @param pFitness
   *          the fitness assignment process
   */
  public EDAWithFitness(final int pMu, final int pLambda,
      final IModel<X> pModel,
      final FitnessAssignmentProcess<? super X> pFitness) {
    super();
    if ((pLambda < 1) || (pLambda > 100_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + pLambda); //$NON-NLS-1$
    }
    this.lambda = pLambda;

    if ((pMu < 1) || (pMu > this.lambda)) {
      throw new IllegalArgumentException("Invalid mu: " + pMu //$NON-NLS-1$
          + " must be in 1..lambda and lambda=" //$NON-NLS-1$
          + this.lambda);
    }
    this.mu = pMu;

    this.model = Objects.requireNonNull(pModel);
    this.fitness = Objects.requireNonNull(pFitness);
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "eda")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("model", this.model));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        this.fitness));
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false));//$NON-NLS-1$
    output.write(System.lineSeparator());
    this.model.printSetup(output);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ((((((("eda_" + //$NON-NLS-1$
        this.model.toString()) + '_') + //
        this.fitness.toString()) + '_')//
        + this.mu) + '+') + this.lambda);
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
    final IModel<X> M = this.model;

    final FitnessIndividual<X>[] P =
        new FitnessIndividual[this.lambda];
    this.fitness.initialize();

// end relevant
    restart: while (!process.shouldTerminate()) {
// start relevant
// local variable initialization omitted for brevity
      M.initialize(); // initialize to uniform distribution
// first generation: fill population with random individuals
      for (int i = P.length; (--i) >= 0;) {
        final X x = searchSpace.create();
        nullary.apply(x, random);
        P[i] = new FitnessIndividual<>(x, process.evaluate(x));
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }
      }

      for (;;) { // each iteration: update model, sample model
// end relevant
        if (this.mu < M.minimumSamplesNeededForUpdate()) {
          continue restart;
        }
// start relevant
        this.fitness.assignFitness(P);
        Arrays.sort(P, this.fitness);
// update model with mu<lambda best solutions
        M.update(IModel.use(P, 0, this.mu));

// sample new population
        for (final FitnessIndividual<X> dest : P) {
          M.apply(dest.x, random); // create new solution
          dest.quality = process.evaluate(dest.x);
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
        } // the end of the solution generation
      } // the end of the main loop
    }
// end relevant
  }
// start relevant
}

package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IModel;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic0;
import aitoa.utils.Experiment;

/**
 * An estimation of distribution algorithm does not apply search
 * operations directly (except for the nullary operator creating
 * the initial points in the search space). Instead, it tries to
 * build a model of what the best solution could look like. The
 * model is basically a statistical distribution describing
 * assigning probabilities to the values of the different
 * decision variables. Such a distribution can be sampled, i.e.,
 * we can create random points from the search space that follow
 * the probabilities prescribed by these distributions. We then
 * use the best sampled points to make their characteristics
 * "more likely" by updating the model. This process is repeated
 * again and again and we can hope that the model, i.e., the
 * distribution that we update, will approach the optimum.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EDA<X, Y> extends Metaheuristic0<X, Y> {
// end relevant

  /** the number of solution to be selected */
  public final int mu;
  /** the number of new points per generation */
  public final int lambda;
  /** the model */
  public final IModel<X> model;

  /**
   * Create a new instance of the estimation of distribution
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pMu
   *          the number of solution to be selected
   * @param pLambda
   *          the number of new points per generation
   * @param pModel
   *          the model
   */
  public EDA(final INullarySearchOperator<X> pNullary,
      final int pMu, final int pLambda, final IModel<X> pModel) {
    super(pNullary);
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
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// end relevant
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final IModel<X> M = this.model;

    final Individual<X>[] P = new Individual[this.lambda];
    restart: while (!process.shouldTerminate()) {
// start relevant
// local variable initialization omitted for brevity
      M.initialize(); // initialize to uniform distribution

// first generation: fill population with random individuals
      for (int i = P.length; (--i) >= 0;) {
        final X x = searchSpace.create();
        this.nullary.apply(x, random);
        P[i] = new Individual<>(x, process.evaluate(x));
// end relevant
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }
// start relevant
      }

      for (;;) { // each iteration: update model, sample model
// end relevant
        if (this.mu < M.minimumSamplesNeededForUpdate()) {
          continue restart;
        }
// start relevant
        Arrays.sort(P, Individual.BY_QUALITY);
// update model with mu<lambda best solutions
        M.update(IModel.use(P, 0, this.mu));

// sample new population
        for (final Individual<X> dest : P) {
          M.apply(dest.x, random); // create new solution
          dest.quality = process.evaluate(dest.x);
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
        } // the end of the solution generation
      } // the end of the main loop
// end relevant
    }
// start relevant
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry(//
        LogFormat.SETUP_BASE_ALGORITHM, "eda")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("model", this.model));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false));//$NON-NLS-1$
    output.write(System.lineSeparator());
    if ((this.model != this.nullary)) {
      this.model.printSetup(output);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge("eda", //$NON-NLS-1$
        this.model, String.valueOf(this.mu) + '+' + this.lambda);
  }

// start relevant
}
// end relevant

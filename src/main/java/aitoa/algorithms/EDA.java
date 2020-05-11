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
public final class EDA<X, Y> implements IMetaheuristic<X, Y> {
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
   * @param _mu
   *          the number of solution to be selected
   * @param _lambda
   *          the number of new points per generation
   * @param _model
   *          the model
   */
  public EDA(final int _mu, final int _lambda,
      final IModel<X> _model) {
    super();
    if ((_lambda < 1) || (_lambda > 100_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + _lambda); //$NON-NLS-1$
    }
    this.lambda = _lambda;

    if ((_mu < 1) || (_mu > this.lambda)) {
      throw new IllegalArgumentException("Invalid mu: " + _mu //$NON-NLS-1$
          + " must be in 1..lambda and lambda=" //$NON-NLS-1$
          + this.lambda);
    }
    this.mu = _mu;

    this.model = Objects.requireNonNull(_model);
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final Writer output)
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
    output.write(LogFormat.mapEntry("clearing", false));//$NON-NLS-1$
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((((("eda_" + //$NON-NLS-1$
        this.model.toString()) + '_') + this.mu) + '+')
        + this.lambda);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// end relevant
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator();
    final IModel<X> Model = this.model;

    final Individual<X>[] P = new Individual[this.lambda];
    restart: while (!process.shouldTerminate()) {
// start relevant
// local variable initialization omitted for brevity
      Model.initialize(); // initialize model

// first generation: fill population with random individuals
      for (int i = P.length; (--i) >= 0;) {
        final X x = searchSpace.create();
        nullary.apply(x, random);
        P[i] = new Individual<>(x, process.evaluate(x));
// end relevant
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }
// start relevant
      }

      for (;;) {// each iteration: update model, sample model
// end relevant
        if (this.mu < Model.minimumSamplesNeededForUpdate()) {
          continue restart;
        }
// start relevant
        Arrays.sort(P, Individual.BY_QUALITY);
// update model with mu<lambda best solutions
        Model.update(IModel.use(P, 0, this.mu));

// sample new population
        for (final Individual<X> dest : P) {
          Model.sample(dest.x, random); // create new solution
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
}

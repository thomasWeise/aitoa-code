package aitoa.algorithms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IModel;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;

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
public class EDA<X, Y> implements IMetaheuristic<X, Y> {
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
    if ((_lambda < 1) || (_lambda > 1_000_000)) {
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
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("base_algorithm: eda"); //$NON-NLS-1$
    output.newLine();
    IMetaheuristic.super.printSetup(output);
    output.write("mu: "); //$NON-NLS-1$
    output.write(Integer.toString(this.mu));
    output.newLine();
    output.write("lambda: ");//$NON-NLS-1$
    output.write(Integer.toString(this.lambda));
    output.newLine();
    output.write("model: ");//$NON-NLS-1$
    output.write(this.model.toString());
    output.newLine();
    output.write("model_class: ");//$NON-NLS-1$
    output.write(this.model.getClass().getCanonicalName());
    output.newLine();
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
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator();
    final IModel<X> Model = this.model;

    final Individual<X>[] P = new Individual[this.lambda];
// start relevant
// local variable initialization omitted for brevity
    Model.initialize(); // initialize model=uniform distribution

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
      Arrays.sort(P); // sort: best solutions at start
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
  }
// end relevant

  /**
   * the individual record: hold one point in search space and
   * its quality
   *
   * @param <X>
   *          the data structure of the search space
   */
  private static final class Individual<X>
      implements Comparable<Individual<X>>, Supplier<X> {
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

    /** {@inheritDoc} */
    @Override
    public final X get() {
      return this.x;
    }
  }
// start relevant
}
// end relevant

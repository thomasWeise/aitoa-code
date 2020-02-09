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
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;

/**
 * A hybrid {@linkplain aitoa.algorithms.EDA estimation of
 * distribution algorithm} combines the EDA with a local search.
 * The concept is the same as in {@linkplain aitoa.algorithms.MA
 * memetic algorithms}, which introduce a local search into an
 * {@linkplain aitoa.algorithms.EA EA}.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class HybridEDA<X, Y>
    implements IMetaheuristic<X, Y> {
// end relevant

  /** the number of solution to be selected */
  public final int mu;
  /** the number of new points per generation */
  public final int lambda;
  /** the maximum number of local search steps */
  public final int maxLSSteps;
  /** the model */
  public final IModel<?> model;

  /**
   * Create a new instance of the estimation of distribution
   *
   * @param _mu
   *          the number of solution to be selected
   * @param _lambda
   *          the number of new points per generation
   * @param _maxLSSteps
   *          the maximum number of local search steps
   * @param _model
   *          the model
   */
  public HybridEDA(final int _mu, final int _lambda,
      final int _maxLSSteps, final IModel<?> _model) {
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
    if (_maxLSSteps <= 0) {
      throw new IllegalArgumentException(
          "Invalid number of maximum local search steps: " //$NON-NLS-1$
              + _maxLSSteps);
    }
    this.maxLSSteps = _maxLSSteps;

    this.model = Objects.requireNonNull(_model);
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "heda")); //$NON-NLS-1$
    output.newLine();
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("model", this.model));//$NON-NLS-1$
    output.newLine();
    output.write(
        LogFormat.mapEntry("maxLSSteps", this.maxLSSteps));//$NON-NLS-1$
    output.newLine();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    final String s = ((((("heda_" + //$NON-NLS-1$
        this.model.toString()) + '_') + this.mu) + '+')
        + this.lambda);
    if (this.maxLSSteps >= Integer.MAX_VALUE) {
      return s;
    }
    return (s + '_') + this.maxLSSteps;
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
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator();
    final IModel<X> Model = ((IModel<X>) (this.model));
    boolean improved;
    final Individual<X>[] P = new Individual[this.lambda];
    final X temp = searchSpace.create();

// start relevant
// the initialization of local variables is omitted for brevity
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

    for (;;) {// each iteration: LS, update model, then sample
      for (final Individual<X> ind : P) {
        int steps = this.maxLSSteps;
        do { // local search in style of HillClimber2
          improved = unary.enumerate(random, ind.x, temp, //
              (point) -> {
                final double newQuality =
                    process.evaluate(point);
                if (newQuality < ind.quality) { // better?
                  ind.quality = newQuality; // store quality
                  searchSpace.copy(point, ind.x); // store point
                  return (true); // exit to next loop
                } // if we get here, point is not better
                return process.shouldTerminate();
              }); // repeat this until no improvement or time is
                  // up
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
        } while (improved && ((--steps) > 0));
      }

      Arrays.sort(P); // sort: best solutions at start
      Model.update(IModel.use(P, 0, this.mu)); // update model

// sample new population
      for (final Individual<X> dest : P) {
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }
        Model.sample(dest.x, random);
        dest.quality = process.evaluate(dest.x);
      } // the end of the new points generation
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

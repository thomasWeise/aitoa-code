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
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic1;
import aitoa.utils.Experiment;

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
public final class HybridEDA<X, Y> extends Metaheuristic1<X, Y> {

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
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   * @param pMu
   *          the number of solution to be selected
   * @param pLambda
   *          the number of new points per generation
   * @param pMaxLSSteps
   *          the maximum number of local search steps
   * @param pModel
   *          the model
   */
  public HybridEDA(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary, final int pMu,
      final int pLambda, final int pMaxLSSteps,
      final IModel<?> pModel) {
    super(pNullary, pUnary);
    if ((pLambda < 1) || (pLambda > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + pLambda); //$NON-NLS-1$
    }
    this.lambda = pLambda;

    if ((pMu < 1) || (pMu > this.lambda)) {
      throw new IllegalArgumentException(//
          "Invalid mu: " + pMu //$NON-NLS-1$
              + " must be in 1..lambda and lambda=" //$NON-NLS-1$
              + this.lambda);
    }
    this.mu = pMu;
    if (pMaxLSSteps <= 0) {
      throw new IllegalArgumentException(
          "Invalid number of maximum local search steps: " //$NON-NLS-1$
              + pMaxLSSteps);
    }
    this.maxLSSteps = pMaxLSSteps;

    this.model = Objects.requireNonNull(pModel);
    if (!(this.unary.canEnumerate())) {
      throw new IllegalArgumentException(//
          "Unary operator cannot enumerate neighborhood."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public void solve(final IBlackBoxProcess<X, Y> process) {
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final IModel<X> M = ((IModel<X>) (this.model));
    boolean improved;
    final Individual<X>[] P = new Individual[this.lambda];
    final X temp = searchSpace.create();

    restart: while (!process.shouldTerminate()) {
// the initialization of local variables is omitted for brevity
      M.initialize(); // initialize as uniform distribution

// first generation: fill population with random individuals
      for (int i = P.length; (--i) >= 0;) {
        final X x = searchSpace.create();
        this.nullary.apply(x, random);
        P[i] = new Individual<>(x, process.evaluate(x));
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }
      }

      for (;;) { // each iteration: LS, update model, then sample
        for (final Individual<X> ind : P) {
          int steps = this.maxLSSteps;
          do { // local search in style of HillClimber2
            improved = this.unary.enumerate(random, ind.x, temp, //
                point -> {
                  final double newQuality =
                      process.evaluate(point);
                  if (newQuality < ind.quality) { // better?
                    ind.quality = newQuality; // store quality
                    searchSpace.copy(point, ind.x); // store
                    return (true); // exit to next loop
                  } // if we get here, point is not better
                  return process.shouldTerminate();
                }); // repeat until no improvement or time up
            if (process.shouldTerminate()) { // we return
              return; // best solution is stored in process
            }
          } while (improved && ((--steps) > 0));
        }

        if (this.mu < M.minimumSamplesNeededForUpdate()) {
          continue restart;
        }
        Arrays.sort(P, Individual.BY_QUALITY);
        M.update(IModel.use(P, 0, this.mu)); // update

// sample new population
        for (final Individual<X> dest : P) {
          if (process.shouldTerminate()) { // we return
            return; // best solution is stored in process
          }
          M.apply(dest.x, random);
          dest.quality = process.evaluate(dest.x);
        } // the end of the new points generation
      } // the end of the main loop
    }
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry(//
        LogFormat.SETUP_BASE_ALGORITHM, "heda")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("model", this.model));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(
        LogFormat.mapEntry("maxLSSteps", this.maxLSSteps));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false));//$NON-NLS-1$
    output.write(System.lineSeparator());
    if ((this.model != this.nullary)
        && (this.model != this.unary)) {
      this.model.printSetup(output);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge("heda", //$NON-NLS-1$
        this.model,
        (Integer.toString(this.mu) + '+') + this.lambda,
        (this.maxLSSteps >= Integer.MAX_VALUE) ? null
            : Integer.toString(this.maxLSSteps),
        this.unary);
  }
}

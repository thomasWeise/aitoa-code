package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.utils.RandomUtils;

/**
 * An {@linkplain aitoa.algorithms.EA evolutionary algorithm}
 * which clears the population from candidate solutions with
 * identical objective values before the reproduction step. This
 * ensures that all "parents" from which new points in the search
 * space are derived have a different solution quality. This, in
 * turn, implies that they are different candidate solutions.
 * These, in turn, must be the result of representation mappings
 * applied to different points in the search space.
 * <p>
 * Please notice that the direct, simple comparisons of the
 * objective values applied here in form of
 * {@code ind.quality > P[u - 1].quality} only make sense when
 * the objective values can exactly be represented in the
 * {@code double} range. This is the case in combinatorial
 * problems where these values are integers. In numerical
 * problems, we could, e.g., base the comparisons on some
 * similarity thresholds.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EAWithClearing<X, Y>
    implements IMetaheuristic<X, Y> {
// end relevant

  /** the crossover rate */
  public final double cr;
  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;

  /**
   * Create a new instance of the evolutionary algorithm
   *
   * @param _cr
   *          the crossover rate
   * @param _mu
   *          the number of parents to be selected
   * @param _lambda
   *          the number of offspring to be created
   */
  public EAWithClearing(final double _cr, final int _mu,
      final int _lambda) {
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
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "ea")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", this.cr));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", true)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((((("eac_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda) + '@') + this.cr);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// Omitted: Initialize local variables random, unary, nullary,
// searchSpace, binary, set arrays P and P2 of length mu+lambda,
// and array T to null. Fill P with random solutions + evaluate.
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

    Individual<X>[] T = null,
        P = new Individual[this.mu + this.lambda],
        P2 = new Individual[P.length];

// first generation: fill population with random individuals
    for (int i = P.length; (--i) >= 0;) {
      final X x = searchSpace.create();
      nullary.apply(x, random);
      P[i] = new Individual<>(x, process.evaluate(x));
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
    }

// start relevant
    while (!process.shouldTerminate()) { // main loop
      RandomUtils.shuffle(random, P, 0, P.length); // make fair
      Arrays.sort(P); // best individuals at front
// Select only the u best solutions of unique quality, 1<=u<=mu.
      int u = 0, done = 0, end = P.length;
      T = P; // First switch the arrays. P2 is sorted. We process
      P = P2; // it from begin to end and copy the unique records
      P2 = T; // to the start of P, the rest to the end of P.
      makeUnique: for (final Individual<X> r : P2) {
        ++done; // Increase number of processed in individuals.
        if ((u <= 0) || (r.quality > P[u - 1].quality)) {
          P[u] = r; // Individual unique -> copy to start
          if ((++u) >= this.mu) { // Got enough? Copy rest.
            System.arraycopy(P2, done, P, u, P.length - done);
            break makeUnique;
          }
        } else { // r has an already-seen quality, so we copy
          P[--end] = r; // it to the end of the array, where
        } // it will eventually be overwritten.
      } // Now we have 1 <= u <= mu unique solutions.
      RandomUtils.shuffle(random, P, 0, u); // for fairness
      int p1 = -1; // index to iterate over first parent
// Overwrite the worse (mu + lambda - u) solutions.
      for (int index = P.length; (--index) >= u;) {
// Omitted: Quit loop if process.shouldTerminate()
// end relevant
        if (process.shouldTerminate()) { // Finished.
          return; // The best solution is stored in process.
        }
// start relevant
        final Individual<X> dest = P[index]; // offspring
        p1 = (p1 + 1) % u; // parent 1 index
        final Individual<X> sel = P[p1]; // parent 1
        if ((u >= 2) && (random.nextDouble() <= this.cr)) {
          int p2; // p2 is the index of second selected parent.
          do { // find a second, different record
            p2 = random.nextInt(u);
          } while (p2 == p1); // Of course, can't be p1.
          binary.apply(sel.x, P[p2].x, dest.x, random);
        } else { // Otherwise: Mutation.
          unary.apply(sel.x, dest.x, random);
        }
        dest.quality = process.evaluate(dest.x);
      } // the end of the offspring generation
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
// start relevant
}
// end relevant

package aitoa.algorithms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.utils.RandomUtils;

/**
 * An {@linkplain aitoa.algorithms.EA evolutionary algorithm}
 * which prunes the population from candidate solutions with
 * identical objective values before the reproduction step. This
 * ensures that all "parents" from which new points in the search
 * space are derived have a different solution quality. This, in
 * turn, implies that they are different candidate solutions.
 * These, in turn, must be the result of representation mappings
 * applied to different points in the search space.
 * <p>
 * Please notice that the direct, simple comparisons of the
 * objective values applied here in form of
 * {@code ind.quality > P[unique - 1].quality} only make sense
 * when the objective values can exactly be represented in the
 * {@code double} range. This is the case in combinatorial
 * problems where these values are integers. In numerical
 * problems, we could, e.g., base the comparisons on some
 * similarity thresholds.
 */
// start relevant
public class EAWithPruning implements IMetaheuristic {
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
  public EAWithPruning(final double _cr, final int _mu,
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
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("base_algorithm: ea"); //$NON-NLS-1$
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
    output.write("pruning: true"); //$NON-NLS-1$
    output.newLine();
    output.write("restarts: false"); //$NON-NLS-1$
    output.newLine();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((((("eap_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda) + '@') + this.cr);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
// omitted: initialize local variables random, searchSpace,
// nullary, unary, binary, and arrays P and P2 of length
// mu+lambda, and array T to null
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
// start relevant
// first generation: fill population with random individuals
    for (int i = P.length; (--i) >= 0;) {
      final X x = searchSpace.create();
      nullary.apply(x, random);
      P[i] = new Individual<>(x, process.evaluate(x));
    }

    while (!process.shouldTerminate()) { // main loop
// shuffle P, so after sorting the order of unique recs is random
      RandomUtils.shuffle(random, P, 0, P.length);
// sort the population: mu best individuals at front are selected
      Arrays.sort(P);
// we now want to keep only the solutions with unique fitness
      int unique = 0, done = 0, end = P.length;
      T = P; // since array P is sorted, so we can do this by
      P = P2; // processing it from begin to end and copying
      P2 = T; // these individuals to the start of another array
// we switch the two arrays here so the rest is the same as EA
      makeUnique: for (final Individual<X> ind : P2) {
        ++done;
        if ((unique <= 0)
            || (ind.quality > P[unique - 1].quality)) {
          P[unique] = ind;
          if ((++unique) >= this.mu) { // we are done and can
            System.arraycopy(P2, done, P, unique, // copy the
                P.length - done); // remaining individuals
            break makeUnique; // directly, they do not need to
          } // be unique, as they will be overwritten anyway
        } else { // ind has an already-seen quality, so we copy
          P[--end] = ind; // it to the end of the array, where
        } // it will eventually be overwritten
      }
// now we have 1 <= unique <= mu unique solutions
// shuffle the first unique solutions to ensure fairness
      RandomUtils.shuffle(random, P, 0, unique);
      int p1 = -1; // index to iterate over first parent

// override the worse (mu+lambda-unique) solutions
      for (int index = P.length; (--index) >= unique;) {
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }

        final Individual<X> dest = P[index];
        final Individual<X> sel = P[(++p1) % unique];
        if ((unique >= 2) && (random.nextDouble() <= this.cr)) {
          int p2; // to hold index of second selected record
          do { // find a second, different record
            p2 = random.nextInt(unique);
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

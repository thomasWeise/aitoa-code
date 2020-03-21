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
 * The evolutionary algorithm (EA) is a population-based
 * metaheuristic. Here we implement the (mu+lambda)&nbsp;EA,
 * which begins by generating mu+lambda random candidate
 * solution. In each step (called generation), it preserves the
 * mu best points from the search space. From these mu points, it
 * derives lambda new points. Together with their mu "parents",
 * these form a population of size mu+lambda. In the next
 * iteration, we again preserve the mu best records. There are
 * two ways to derive "offspring" solutions from parents: We can
 * either apply an unary or a binary search operator. The unary
 * operator, in this context often called "mutation", derives one
 * new point in the search space from one existing point by
 * creating a slightly modified copy. This is the same kind of
 * operator applied in the
 * {@linkplain aitoa.algorithms.HillClimber}. The binary operator
 * takes two existing points from the search space to build a new
 * point by combining the characteristics of both "parents". It
 * is often referred to as "recombination" or "crossover" and the
 * idea is as follows: Both parents have been selected, i.e.,
 * they must be good in some way. If they are different, then
 * they must have different positive characteristics. If we are
 * lucky, then maybe we can merge these different characteristics
 * and obtain a new point which represents a combination of
 * different positive traits and is even better.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EA<X, Y> implements IMetaheuristic<X, Y> {
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
  public EA(final double _cr, final int _mu, final int _lambda) {
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
    output.write(LogFormat.mapEntry("pruning", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((((("ea_" + //$NON-NLS-1$
        this.mu) + '+') + this.lambda) + '@') + this.cr);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// end relevant
// start withoutcrossover
// omitted: initialize local variables random, searchSpace,
// nullary, unary and the array P of length mu+lambda
// end withoutcrossover
// start withcrossover
// omitted: initialize local variables random, searchSpace,
// nullary, unary, binary, and array P of length mu+lambda
// end withcrossover
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator();
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator();
    final IBinarySearchOperator<X> binary =
        process.getBinarySearchOperator();

    final Individual<X>[] P =
        new Individual[this.mu + this.lambda];
// start relevant
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

    for (;;) { // main loop: one iteration = one generation
// sort the population: mu best individuals at front are selected
      Arrays.sort(P);
// shuffle the first mu solutions to ensure fairness
      RandomUtils.shuffle(random, P, 0, this.mu);
      int p1 = -1; // index to iterate over first parent

// override the worse lambda solutions with new offsprings
      for (int index = P.length; (--index) >= this.mu;) {
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }

        final Individual<X> dest = P[index];
        p1 = (p1 + 1) % this.mu;
        final Individual<X> sel = P[p1];
// end relevant
// start withcrossover
        if (random.nextDouble() <= this.cr) { // crossover!
          int p2; // to hold index of second selected record
          do { // find a second, different record
            p2 = random.nextInt(this.mu);
          } while (p2 == p1);
// perform recombination of the two selected individuals
          binary.apply(sel.x, P[p2].x, dest.x, random);
        } else {
// end withcrossover
// start relevant
// create modified copy of parent using unary operator
          unary.apply(sel.x, dest.x, random);
// end relevant
// start withcrossover
        }
// end withcrossover
// start relevant
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

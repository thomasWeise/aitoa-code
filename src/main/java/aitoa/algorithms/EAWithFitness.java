package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic2;
import aitoa.utils.Experiment;
import aitoa.utils.RandomUtils;

/**
 * The evolutionary algorithm (EA) which employs a fitness
 * assignment process.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EAWithFitness<X, Y>
    extends Metaheuristic2<X, Y> {
// end relevant

  /** the crossover rate */
  public final double cr;
  /** the number of selected parents */
  public final int mu;
  /** the number of offsprings per generation */
  public final int lambda;
  /** the fitness assignment process */
  public final FitnessAssignmentProcess<? super X> fitness;

  /**
   * Create a new instance of the evolutionary algorithm
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   * @param pBinary
   *          the binary search operator
   * @param pCr
   *          the crossover rate
   * @param pMu
   *          the number of parents to be selected
   * @param pLambda
   *          the number of offspring to be created
   * @param pFitness
   *          the fitness assignment process
   */
  public EAWithFitness(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary,
      final IBinarySearchOperator<X> pBinary, final double pCr,
      final int pMu, final int pLambda,
      final FitnessAssignmentProcess<? super X> pFitness) {
    super(pNullary, pUnary, pBinary);
    if ((pCr < 0d) || (pCr > 1d) || (!(Double.isFinite(pCr)))) {
      throw new IllegalArgumentException(
          "Invalid crossover rate: " + pCr); //$NON-NLS-1$
    }
    this.cr = pCr;
    if ((pMu < 1) || (pMu > 1_000_000)) {
      throw new IllegalArgumentException("Invalid mu: " + pMu); //$NON-NLS-1$
    }
    if ((pMu <= 1) && (pCr > 0d)) {
      throw new IllegalArgumentException(//
          "crossover rate must be 0 if mu is 1, but cr is " //$NON-NLS-1$
              + pCr);
    }
    this.mu = pMu;
    if ((pLambda < 1) || (pLambda > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid lambda: " + pLambda); //$NON-NLS-1$
    }
    this.lambda = pLambda;

    this.fitness = Objects.requireNonNull(pFitness);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// omitted: initialize local variables random, searchSpace, and
// array P of length mu+lambda
// end relevant
// create local variables
    final Random random = process.getRandom();
    final ISpace<X> searchSpace = process.getSearchSpace();
    int p2; // to hold index of second selected record
    final FitnessRecord<X>[] P =
        new FitnessRecord[this.mu + this.lambda];
    this.fitness.initialize();
// start relevant
// first generation: fill population with random solutions
    for (int i = P.length; (--i) >= 0;) {
      final X x = searchSpace.create();
      this.nullary.apply(x, random);
      P[i] = new FitnessRecord<>(x, process.evaluate(x));
// end relevant
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
// start relevant
    }

    for (;;) { // main loop: one iteration = one generation
// sort the population: mu best records at front are selected
      this.fitness.assignFitness(P);
      Arrays.sort(P, FitnessRecord.BY_FITNESS);
// shuffle the first mu solutions to ensure fairness
      RandomUtils.shuffle(random, P, 0, this.mu);
      int p1 = -1; // index to iterate over first parent

// overwrite the worse lambda solutions with new offsprings
      for (int index = P.length; (--index) >= this.mu;) {
        if (process.shouldTerminate()) { // we return
          return; // best solution is stored in process
        }

        final FitnessRecord<X> dest = P[index];
        p1 = (p1 + 1) % this.mu;
        final FitnessRecord<X> sel = P[p1];
        if (random.nextDouble() <= this.cr) { // crossover!
          do { // find a second, different record
            p2 = random.nextInt(this.mu);
          } while (p2 == p1);
// perform recombination of the two selected records
          this.binary.apply(sel.x, P[p2].x, dest.x, random);
        } else {
// create modified copy of parent using unary operator
          this.unary.apply(sel.x, dest.x, random);
        }
// map to solution/schedule and evaluate quality
        dest.quality = process.evaluate(dest.x);
      } // the end of the offspring generation
// end relevant
      if (process.shouldTerminate()) { // we return
        return; // best solution is stored in process
      }
// start relevant
    } // the end of the main loop
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry(//
        LogFormat.SETUP_BASE_ALGORITHM, "fitness_ea")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", this.mu));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", this.lambda));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", this.cr));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        this.fitness));
    output.write(System.lineSeparator());
    if ((this.fitness != this.nullary)
        && (this.fitness != this.unary)
        && (this.fitness != this.binary)) {
      this.fitness.printSetup(output);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge("ea", //$NON-NLS-1$
        this.fitness,
        ((((Integer.toString(this.mu) + '+') + this.lambda)
            + '@') + this.cr), //
        this.unary, this.binary);
  }
// start relevant
}
// end relevant

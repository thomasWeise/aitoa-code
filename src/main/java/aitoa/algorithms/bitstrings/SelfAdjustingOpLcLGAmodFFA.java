package aitoa.algorithms.bitstrings;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;
import aitoa.utils.math.BinomialDistribution;
import aitoa.utils.math.DiscreteConstant;
import aitoa.utils.math.DiscreteGreaterThanZero;
import aitoa.utils.math.DiscreteRandomDistribution;

/**
 * The Self-Adjusting (1+(lambda,lambda)) GA mod extended with
 * FFA. The {@linkplain SelfAdjustingOpLcLGAmod basic algorithm}
 * is defined in Algorithm 5 of E. Carvalho Pinto and C. Doerr,
 * "Towards a more practice-aware runtime analysis of
 * evolutionary algorithms," July 2017, arXiv:1812.00493v1
 * [cs.NE] 3 Dec 2018. [Online]. Available:
 * http://arxiv.org/pdf/1812.00493.pdf.We here present its
 * version applying Frequency Fitness Assignment (FFA).
 *
 * @param <Y>
 *          the solution space
 */
public final class SelfAdjustingOpLcLGAmodFFA<Y>
    implements IMetaheuristic<boolean[], Y> {

  /** the internal adaptation factor */
  private static final double F = 1.5d;
  /** the inverse adaptation factor */
  private static final double F_BY_1_OVER_4 =
      Math.pow(SelfAdjustingOpLcLGAmodFFA.F, 0.25d);

  /** the upper bound of the objective function */
  private final int mUB;

  /**
   * create
   *
   * @param pUB
   *          the upper bound of the objective function
   */
  public SelfAdjustingOpLcLGAmodFFA(final int pUB) {
    super();
    if (pUB <= 0) {
      throw new IllegalArgumentException(
          "UB must be at least 1, but you specified " //$NON-NLS-1$
              + pUB);
    }
    this.mUB = pUB;
  }

  /** {@inheritDoc} */
  @Override
  public void
      solve(final IBlackBoxProcess<boolean[], Y> process) {
    final Random random = process.getRandom();// get random gen
    final INullarySearchOperator<boolean[]> nullary =
        process.getNullarySearchOperator();
    final ISpace<boolean[]> searchSpace =
        process.getSearchSpace();

    // Line 1: sample x from the search space
    Holder x = new Holder(searchSpace.create());
    nullary.apply(x.x, random);
    x.f = ((int) (process.evaluate(x.x)));
    final int n = x.x.length;

    // FFA: this history table
    final long[] H = new long[this.mUB + 1];
    H[x.f] = 1L; // update for solution sampled first

    // allocate integer array used in mutation
    final int[] indices = new int[n];
    for (int i = n; (--i) >= 0;) {
      indices[i] = i;
    }

    // Line 2: initialize lambda to 1
    int lambda = 1;
    final DiscreteRandomDistribution[] binDistrs =
        new DiscreteRandomDistribution[n + 1];
    final Holder[] xi = new Holder[n + 1];

// pre-allocation: allocate and cache data structures.
// We cache the binomial distribution, we also cache the bit
// string arrays.
    for (int i = xi.length; (--i) >= 0;) {
      if (i > 0) {
        binDistrs[i] = (i < n)
            ? new DiscreteGreaterThanZero(
                new BinomialDistribution(n, ((double) i) / n))
            : new DiscreteConstant(n);
      }
// We ensure that there are actually lambda + 1 boolean arrays
// ready.
// This way, we can later swap in xprime even if all y(i) have
// the same best fitness which is also identical with f(xprime)
// ... unlikely, but possible.
      xi[i] = new Holder(n);
    }
// done with pre-allocation

    Holder xprime = new Holder(n);

    while (!process.shouldTerminate()) { // Line 3

// Line 4: begin of mutation phase
// Line 5: draw number of bits to flip
      final int l = binDistrs[lambda].nextInt(random);

// Line 6: Create lambda mutated offspring
      for (int i = 0; i < lambda; i++) {
        final boolean[] xcur = xi[i].x;
// First copy x to xcur.
        System.arraycopy(x.x, 0, xcur, 0, n);

// Shuffle the first l elements in the index list in a
// Fisher-Yates style.
// This will produce l random, unique, different indices.
        for (int j = 0; j < l; j++) {
          final int k = j + random.nextInt(n - j);
          final int t = indices[k];
          indices[k] = indices[j];
          indices[j] = t;

// In this loop, we also directly toggle the bit.
          xcur[t] ^= true;
        }
// We now have one mutated offspring different from x and it
// differs in exactly l bits, chosen uniformly at random.

        xi[i].f = ((int) (process.evaluate(xcur)));
        if (process.shouldTerminate()) {
          return;
        }
        ++H[xi[i].f]; // FFA update
      } // done loop generating lambda offspring

// We are done with creating lambda offsprings and updating the
// FFA table.
// We now need to find those with the best history value.
      long Hxprime = Long.MAX_VALUE;
      int nbest = 0;
      for (int i = 0; i < lambda; i++) {
        final Holder xcur = xi[i];
        final long Hcur = H[xcur.f];
        if (Hcur <= Hxprime) {
          if (Hcur < Hxprime) {
            Hxprime = Hcur;
            nbest = 0;
          }
          xi[i] = xi[nbest];
          xi[nbest] = xcur;
          ++nbest;
        }
      } // end get Hxprime

// We now have nbest entries of fitness Hxprime at the beginning
// of xi.
// This makes it easy to pick one uniformly at random.
// Line 7: Selection from the mutated offspring.
      final int xprimeIndex = random.nextInt(nbest);
      final Holder t = xi[xprimeIndex];
      xi[xprimeIndex] = xprime;
      xprime = t;
// xprime has been selected, Hxprime is its fitness

// Crossover makes only sense if lambda > 1
      if (lambda > 1) {

// Line 8: Crossover Step
        for (int i = 0; i < lambda; i++) {
          final Holder ycur = xi[i];
          final boolean[] ycurx = ycur.x;
          final boolean[] xprimex = xprime.x;

// Line 9 part 1
// First, copy x so that we later only need to deal with two
// arrays.
          System.arraycopy(x.x, 0, ycurx, 0, n);
          boolean ycurEqualsX = true;
          boolean ycurEqualsXprime = true;

          for (int j = n; (--j) >= 0;) {
            final boolean v = ycurx[j];
            final boolean w = xprimex[j];
// Copy value from xprime with probability 1/lambda.
            if (random.nextInt(lambda) <= 0) {
              ycurx[j] = w;
              ycurEqualsX &= (w == v);
            } else {
// Otherwise, preserve value from x.
              ycurEqualsXprime &= (w == v);
            }
          } // end single crossover

// Line 9 part 2
// We now have one crossovered offspring.
// Evaluate objective function only if offspring is different.
          if (ycurEqualsX) {
            ycur.f = x.f;
          } else {
            if (ycurEqualsXprime) {
              ycur.f = xprime.f;
            } else {
              ycur.f = ((int) (process.evaluate(ycurx)));
              if (process.shouldTerminate()) {
                return;
              }
            }
          }
          ++H[ycur.f]; // FFA update
        } // end create lambda crossover offspring

// choose the best from y(i) offspring
        long Hybest = Long.MAX_VALUE;
        nbest = 0;
        for (int i = 0; i < lambda; i++) {
          final Holder xcur = xi[i];
          final long Hcur = H[xcur.f];
          if (Hcur <= Hybest) {
            if (Hcur < Hybest) {
              Hybest = Hcur;
              nbest = 0;
            }
            xi[i] = xi[nbest];
            xi[nbest] = xcur;
            ++nbest;
          }
        } // end get Hybest

// FFA: Hxprime may have changed, so we need to take it again
        Hxprime = H[xprime.f];

// OK, the array x(i), here also used for y(i) now contains
// lambda offspring at indices 0..lambda-1.
// From these, the nbest > 0 best offspring, all with fitness
// value Hybest, are located at indices 0...nbest-1.
// We also have xprime with fitness Hxprime.
// We now need to pick, uniformly at random, one of the best
// elements from these nbest+1 elements.
// If Hxprime is better than Hybest, we will pick xprime.
// If Hybest is better than Hxprime, we will prick one of the
// first nbest elements from xi.
// If Hybest == Hxprime, we need to swap xprime into the xi array
// and then pick one of the nbest+1 first elements.
// Line 10: Choose the offspring
        if (Hxprime >= Hybest) {
          if (Hybest == Hxprime) {
            final Holder tt = xi[nbest];
            xi[nbest] = xprime;
            xprime = tt;
            ++nbest;
          } else {
            Hxprime = Hybest;
          }

          final int sel = random.nextInt(nbest);
          final Holder ttt = xi[sel];
          xi[sel] = xprime;
          xprime = ttt;
        } // otherwise keep fxprime as is, it is selected
      } // end of crossover (lambda > 1)

// Line 10: We chose xprime = y uniformly at random from the best
// elements and its fitness is Hxbest.

// Should we update H[x.f]?
// I think yes, since we also update it in the other algorithms
// at this point.
      final long Hx = ++H[x.f]; // FFA: need to update fitness
      Hxprime = H[xprime.f]; // FFA: Hxprime may have changed

// Update lambda
      if (Hxprime < Hx) {
// Line 12: If we are successful, decrease lambda.
        lambda = Math.max(1, Math.min(lambda - 1, (int) (Math
            .round(lambda / SelfAdjustingOpLcLGAmodFFA.F))));
      } else {
// Line 13/14: No improvement: increase lambda
        lambda = Math.min(n,
            Math.max(lambda + 1, (int) (Math.round(lambda
                * SelfAdjustingOpLcLGAmodFFA.F_BY_1_OVER_4))));
      }

// Select the better element (Lines 12 and 13)
      if (Hxprime <= Hx) {
        final Holder tt = x;
        x = xprime;
        xprime = tt;
      }

    } // main loop
  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "SelfAdjusting(1+(LcL))GAmodFFA"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", 1));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("F", 1.5));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("c", //$NON-NLS-1$
        "1OverLambda"));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", //$NON-NLS-1$
        "selfAdjustingFrom1ToN"));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("p", //$NON-NLS-1$
        "1OverLambda")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        "FFA"));//$NON-NLS-1$
    output.write(System.lineSeparator());
  }

  /** the holder */
  private static final class Holder {
    /** the x */
    boolean[] x;
    /** the objective value */
    int f;

    /**
     * create the holder
     *
     * @param pX
     *          the value to store
     */
    Holder(final boolean[] pX) {
      this.x = pX;
      this.f = -1;
    }

    /**
     * create the holder
     *
     * @param pN
     *          the bit width
     */
    Holder(final int pN) {
      this(new boolean[pN]);
    }
  }
}

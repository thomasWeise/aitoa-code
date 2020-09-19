package aitoa.algorithms.bitstrings;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic0;
import aitoa.utils.math.BinomialDistribution;
import aitoa.utils.math.DiscreteGreaterThanZero;
import aitoa.utils.math.DiscreteRandomDistribution;

/**
 * The Self-Adjusting (1+(lambda,lambda)) GA mod extended so that
 * it decides automatically whether and when to use FFA. The
 * {@linkplain SelfAdjustingOpLcLGAmod basic algorithm} which not
 * uses any FFA, is defined in Algorithm 5 of E. Carvalho Pinto
 * and C. Doerr, "Towards a more practice-aware runtime analysis
 * of evolutionary algorithms," July 2017, arXiv:1812.00493v1
 * [cs.NE] 3 Dec 2018. [Online]. Available:
 * http://arxiv.org/pdf/1812.00493.pdf. We then developed a
 * {@linkplain SelfAdjustingOpLcLGAmodFFA variant} using
 * Frequency Fitness Assignment (FFA). Here this variant is
 * further extended to use direct optimization as long as it
 * seems to work, to switch to FFA when it seemingly doesn't, and
 * to switch back to direct optimization if there seems to be a
 * chance that it could work again.
 * <p>
 * The original Self-Adjusting (1+(lambda,lambda)) GA mod adapts
 * its parameter lambda based on whether search steps are
 * successful or not. If not, lambda will increase and otherwise
 * decrease. However, lambda is bound to never exceed the problem
 * scale {@code n}. Our algorithm starts with normal, direct
 * optimization. If that works, it will never apply FFA. But if
 * lambda is increased, reaches {@code n}, and the search still
 * makes no progress (i.e., would try to increase lambda further
 * but cannot), then our algorithm switches over to use FFA
 * instead. It will continue to use FFA until it makes an actual
 * improvement, i.e., discovers a new, best-so-far solution based
 * on the objective value. Then it switches back to direct
 * optimization. It is possible that this switching may occur
 * several times back-and-forth. Regardless of whether the
 * algorithm is doing direct or FFA-based optimization, it will
 * always keep updating the FFA-table.
 *
 * @param <Y>
 *          the solution space
 */
public final class SelfAdjustingOpLcLGAmodFFAPlus<Y>
    extends Metaheuristic0<boolean[], Y> {

  /** the internal adaptation factor */
  private static final double F = 1.5d;
  /** the inverse adaptation factor */
  private static final double F_BY_1_OVER_4 =
      Math.pow(SelfAdjustingOpLcLGAmodFFAPlus.F, 0.25d);

  /** the upper bound of the objective function */
  private final int mUB;

  /**
   * create
   *
   * @param pUB
   *          the upper bound of the objective function
   */
  public SelfAdjustingOpLcLGAmodFFAPlus(final int pUB) {
    this(null, pUB);
  }

  /**
   * create
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUB
   *          the upper bound of the objective function
   */
  public SelfAdjustingOpLcLGAmodFFAPlus(
      final INullarySearchOperator<boolean[]> pNullary,
      final int pUB) {
    super((pNullary != null) ? pNullary
        : new BitStringNullaryOperator());
    if (pUB <= 0) {
      throw new IllegalArgumentException(
          "UB must be at least 1, but you specified " //$NON-NLS-1$
              + pUB);
    }
    this.mUB = pUB;
  }

  /**
   * Perform the self-adaptive optimization.
   * <p>
   * The algorithm is annotated with line numbers which point to
   * the paper with the basic algorithm which is extended here,
   * i.e., algorithm 5 of E. Carvalho Pinto and C. Doerr,
   * "Towards a more practice-aware runtime analysis of
   * evolutionary algorithms," July 2017, arXiv:1812.00493v1
   * [cs.NE] 3 Dec 2018. [Online]. Available:
   * http://arxiv.org/pdf/1812.00493.pdf.
   * 
   * @param process
   *          the black box process providing the objective
   *          function and random number generator
   */
  @Override
  public void
      solve(final IBlackBoxProcess<boolean[], Y> process) {
    final Random random = process.getRandom();// get random gen
    final ISpace<boolean[]> searchSpace =
        process.getSearchSpace();

// Do we use FF? Initially not.
    boolean useFFA = false;
    int fBest = Integer.MAX_VALUE;

// Line 1: sample x from the search space
    Holder x = new Holder(searchSpace.create());
    this.nullary.apply(x.x, random);
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

// pre-allocation: allocate and cache data structures.
// We cache the binomial distribution objects, which will later
// allow to generate random numbers in O(1) but need to
// pre-compute some values first in their constructor.
    final DiscreteRandomDistribution[] binDistrs =
        new DiscreteRandomDistribution[n];

// We also pre-allocate the bit string arrays.
// We ensure that there are actually lambda + 1 boolean arrays
// ready.
// This way, we can later swap in xprime even if all y(i) have
// the same best fitness which is also identical with f(xprime)
// ... unlikely, but possible.
    final Holder[] xi = new Holder[n + 1];

// Do the actual pre-allocation work.
    xi[n] = new Holder(n);
    for (int i = n; (--i) > 0;) {
      binDistrs[i] = new DiscreteGreaterThanZero(
          new BinomialDistribution(n, ((double) i) / n));
      xi[i] = new Holder(n);
    }
    xi[0] = new Holder(n);
// Done with pre-allocation.

// This is the holder for the best mutation offspring.
    Holder xprime = new Holder(n);

    while (!process.shouldTerminate()) { // Line 3

      int nbest = 0;
      long Hxprime = Long.MAX_VALUE;

// Line 4: begin of mutation phase
      if (lambda >= n) {
// If lambda = n, then Bin(n, lambda/n) = Bin(n, 1) = n.
// In this case, all bits will be flipped in mutation.
// Then all the lambda mutated offspring will be the same.
// Then we would evaluate n times the same individual, which is a
// waste.
// Thus, we can directly compute the offspring: xprime = !x and
// only need to evaluate it once.
        final boolean[] dst = xprime.x;
        final boolean[] src = x.x;
        for (int i = n; (--i) >= 0;) {
          dst[i] = !src[i];
        }
        xprime.f = (int) (process.evaluate(xprime.x));
        ++H[xprime.f]; // get frequency fitness
        Hxprime = useFFA ? H[xprime.f] : xprime.f;
      } else { // 0 < lambda < n --> create lambda offsprings
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
        for (int i = 0; i < lambda; i++) {
          final Holder xcur = xi[i];
// Use either H (if useFFA) or objective value (otherwise)
          final long Hcur = useFFA ? H[xcur.f] : xcur.f;
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
      }

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
// Use either H (if useFFA) or objective value (otherwise)
          final long Hcur = useFFA ? H[xcur.f] : xcur.f;
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

// Use either H (if useFFA) or objective value (otherwise)
        if (useFFA) {
// FFA: Hxprime may have changed, so we need to take it again
          Hxprime = H[xprime.f];
        } // if we do not use FFA, Hxprime cannot have changed

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
      ++H[x.f]; // FFA: need to update fitness
      final long Hx = useFFA ? H[x.f] : x.f;
// Use either H (if useFFA) or objective value (otherwise)
      if (useFFA) {
// FFA: Hxprime may have changed
        Hxprime = H[xprime.f];
      }
// Update lambda
      if (Hxprime < Hx) {
// Line 12: If we are successful, decrease lambda.
        lambda = Math.max(1, Math.min(lambda - 1, (int) (Math
            .round(lambda / SelfAdjustingOpLcLGAmodFFAPlus.F))));
      } else {
// Line 13/14: No improvement: increase lambda

// If lambda already >= n, we switch to FFA
        useFFA |= (lambda >= n);
// Increase lambda.
        lambda = Math.min(n,
            Math.max(lambda + 1, (int) (Math.round(lambda
                * SelfAdjustingOpLcLGAmodFFAPlus.F_BY_1_OVER_4))));
      }

// Select the better element (Lines 12 and 13)
      if (Hxprime <= Hx) {
        if (xprime.f < fBest) {
// If we found and accept a real improvement, we will always
// continue with direct optimization.
// Notice: If we do direct optimization and found a new best
// solution, we will definitely get here. If we do FFA and found
// a new best objective value, the Hxprime will very likely be
// less than Hx (unless we somehow found the new optimum several
// times at once and more often than the previous solution.)
// Thus, regardless whether we use FFA or not, we will very
// likely get here with a new optimum. We will switch to direct
// optimization if the new optimum is accepted.
          fBest = xprime.f;
          useFFA = false;
        }
// Accept the better element: swap xprime into x
        final Holder tt = x;
        x = xprime;
        xprime = tt;
      }

    } // main loop
  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "SelfAdjusting(1+(LcL))GAmodFFA+"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    super.printSetup(output);
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
        "direct+FFA"));//$NON-NLS-1$
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

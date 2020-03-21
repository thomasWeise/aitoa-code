package aitoa.algorithms.bitstrings;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;

import aitoa.TestTools;
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
 * The Self-Adjusting (1+(lambda,lambda)) GA mod, as defined in
 * Algorithm 5 of E. Carvalho Pinto and C. Doerr, "Towards a more
 * practice-aware runtime analysis of evolutionary algorithms,"
 * July 2017, arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online].
 * Available: http://arxiv.org/pdf/1812.00493.pdf
 *
 * @param <Y>
 *          the solution space
 */
public class InnerSelfAdjustingOpLcLGAmod<Y>
    implements IMetaheuristic<boolean[], Y> {

  /** the internal adaptation factor */
  private static final double F = 1.5d;
  /** the inverse adaptation factor */
  private static final double F_BY_1_OVER_4 =
      Math.pow(InnerSelfAdjustingOpLcLGAmod.F, 0.25d);

  /** create */
  public InnerSelfAdjustingOpLcLGAmod() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final void
      solve(final IBlackBoxProcess<boolean[], Y> process) {
    final Random random = process.getRandom();// get random gen
    final INullarySearchOperator<boolean[]> nullary =
        process.getNullarySearchOperator();
    final ISpace<boolean[]> searchSpace =
        process.getSearchSpace();

    // Line 1: sample x from the search space
    boolean[] x = searchSpace.create();
    nullary.apply(x, random);
    double fx = process.evaluate(x);
    final int n = x.length;

    // allocate integer array used in mutation
    final int[] indices = new int[n];
    for (int i = n; (--i) >= 0;) {
      indices[i] = i;
    }

    // Line 2: initialize lambda to 1
    int lambda = 1;
    final DiscreteRandomDistribution[] binDistrs =
        new DiscreteRandomDistribution[n + 1];
    final boolean[][] xi = new boolean[n + 1][];

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
      xi[i] = new boolean[n];
    }
// done with pre-allocation

    boolean[] xprime = new boolean[n];

    while (!process.shouldTerminate()) {// Line 3
      Assert.assertNotSame(xprime, x);
      for (int i = xi.length; (--i) >= 0;) {
        for (int j = i; (--j) >= 0;) {
          Assert.assertNotSame(xi[i], xi[j]);
        }
        Assert.assertNotSame(xi[i], x);
        Assert.assertNotSame(xi[i], xprime);
      }

// Line 4: begin of mutation phase
// Line 5: draw number of bits to flip
      TestTools.assertGreater(lambda, 0);
      TestTools.assertLessOrEqual(lambda, n);
      final int l = binDistrs[lambda].nextInt(random);
      TestTools.assertGreater(l, 0);
      TestTools.assertLessOrEqual(l, n);
      double fxprime = Double.POSITIVE_INFINITY;
      int nbest = 0;

// Line 6: Create lambda mutated offspring
      for (int i = 0; i < lambda; i++) {
        final boolean[] xcur = xi[i];
        Assert.assertNotSame(xcur, x);
        Assert.assertNotSame(xcur, xprime);
// First copy x to xcur.
        System.arraycopy(x, 0, xcur, 0, n);

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

        for (int j = l; (--j) > 0;) {
          for (int k = j; (--k) >= 0;) {
            Assert.assertNotEquals(indices[k], indices[j]);
          }
        }
// We now have one mutated offspring different from x and it
// differs in exactly l bits, chosen uniformly at random.

        final double fxcur = process.evaluate(xcur);
        if (process.shouldTerminate()) {
          return;
        }
        if (fxcur <= fxprime) {
          if (fxcur < fxprime) {
            nbest = 0;
            fxprime = fxcur;
          }

// Switch the element to the front.
          final boolean[] t = xi[nbest];
          xi[nbest] = xcur;
          xi[i] = t;
          if (i != nbest) {
            Assert.assertNotSame(xcur, t);
          } else {
            Assert.assertSame(t, xcur);
          }
          ++nbest; // Increase number of preserved best
        } // end if fcur <= fxprime
      } // done loop generating lambda offspring
      TestTools.assertGreater(nbest, 0);

// We are done with mutation.
// We now have nbest entries of fitness fxprime at the beginning
// of xi.
// This makes it easy to pick one uniformly at random.
// Line 7: Selection from the mutated offspring.
      final int xprimeIndex = random.nextInt(nbest);
      final boolean[] t = xi[xprimeIndex];
      xi[xprimeIndex] = xprime;
      Assert.assertNotSame(xprime, t);
      xprime = t;
// xprime has been selected, fxprime is its fitness

// Crossover makes only sense if lambda > 1
      if (lambda > 1) {

// Line 8: Crossover Step
        double fybest = Double.POSITIVE_INFINITY;
        nbest = 0;
        for (int i = 0; i < lambda; i++) {
          final boolean[] ycur = xi[i];

// Line 9 part 1
// First, copy x so that we later only need to deal with two
// arrays.
          System.arraycopy(x, 0, ycur, 0, n);
          boolean ycurEqualsX = true;
          boolean ycurEqualsXprime = true;

          for (int j = n; (--j) >= 0;) {
            final boolean v = ycur[j];
            final boolean w = xprime[j];
// Copy value from xprime with probability 1/lambda.
            if (random.nextInt(lambda) <= 0) {
              ycur[j] = w;
              ycurEqualsX &= (w == v);
            } else {
// Otherwise, preserve value from x.
              ycurEqualsXprime &= (w == v);
            }
          } // end single crossover
          Assert
              .assertTrue(ycurEqualsX == Arrays.equals(ycur, x));
          Assert.assertTrue(
              ycurEqualsXprime == Arrays.equals(ycur, xprime));

// Line 9 part 2
// We now have one crossovered offspring.
// Evaluate objective function only if offspring is different.
          final double fycur;
          if (ycurEqualsX) {
            fycur = fx;
          } else {
            if (ycurEqualsXprime) {
              fycur = fxprime;
            } else {
              fycur = process.evaluate(ycur);
              if (process.shouldTerminate()) {
                return;
              }
            }
          }

          if (fycur <= fybest) {
            if (fycur < fybest) {
              nbest = 0;
              fybest = fycur;
            }

// Switch the element to the front.
            final boolean[] tt = xi[nbest];
            xi[nbest] = ycur;
            xi[i] = tt;
            if (nbest == i) {
              Assert.assertSame(tt, ycur);
            } else {
              Assert.assertNotSame(tt, ycur);
            }
            ++nbest; // Increase number of preserved best
          } // end if fcur <= fybest
        } // end create lambda crossover offspring

// OK, the array x(i), here also used for y(i) now contains
// lambda offspring at indices 0..lambda-1.
// From these, the nbest > 0 best offspring, all with objective
// value fybest, are located at indices 0...nbest-1.
// We also have xprime with fitness fxprime.
// We now need to pick, uniformly at random, one of the best
// elements from these nbest+1 elements.
// If fxprime is better than fybest, we will pick xprime.
// If fybest is better than fxprime, we will prick one of the
// first nbest elements from xi.
// If fybest == fxprime, we need to swap xprime into the xi array
// and then pick one of the nbest+1 first elements.
// Line 10: Choose the offspring
        if (fxprime < fybest) {
          // keep fxprime as is, it is selected
        } else {
          if (fybest == fxprime) {
            final boolean[] tt = xi[nbest];
            xi[nbest] = xprime;
            Assert.assertNotSame(tt, xprime);
            xprime = tt;
            ++nbest;
          } else {
            fxprime = fybest;
          }

          final int sel = random.nextInt(nbest);
          final boolean[] ttt = xi[sel];
          xi[sel] = xprime;
          xprime = ttt;
        }
      } // end of crossover (lambda > 1)

// Line 10: We chose xprime = y uniformly at random from the best
// elements and its fitness is fxbest.

// Update lambda
      if (fxprime < fx) {
// Line 12: If we are successful, decrease lambda.
        lambda = Math.max(1, Math.min(lambda - 1, (int) (Math
            .round(lambda / InnerSelfAdjustingOpLcLGAmod.F))));
      } else {
// Line 13/14: No improvement: increase lambda
        lambda = Math.min(n,
            Math.max(lambda + 1, (int) (Math.round(lambda
                * InnerSelfAdjustingOpLcLGAmod.F_BY_1_OVER_4))));
      }
      TestTools.assertGreater(lambda, 0);
      TestTools.assertLessOrEqual(lambda, n);

// Select the better element (Lines 12 and 13)
      if (fxprime <= fx) {
        final boolean[] tt = x;
        Assert.assertNotSame(tt, xprime);
        x = xprime;
        xprime = tt;
        fx = fxprime;
      }

    } // main loop
  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "SelfAdjusting(1+(LcL))GAmod"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final Writer output)
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
  }
}

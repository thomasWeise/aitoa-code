package aitoa.algorithms.bitstrings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;
import aitoa.utils.math.BinomialDistribution;
import aitoa.utils.math.DiscreteGreaterThanZero;

/**
 * The Greedy (2+1) GA mod, extended with FFA. The
 * {@linkplain Greedy2p1GAmod basic algorithm} is defined in
 * Algorithm 6 of E. Carvalho Pinto and C. Doerr, "Towards a more
 * practice-aware runtime analysis of evolutionary algorithms,"
 * July 2017, arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online].
 * Available: http://arxiv.org/pdf/1812.00493.pdf. We here
 * present its version applying Frequency Fitness Assignment
 * (FFA).
 *
 * @param <Y>
 *          the solution space
 */
public class InnerGreedy2p1GAmodFFA<Y>
    implements IMetaheuristic<boolean[], Y> {

  /** the constant above n */
  public final int m;

  /** the upper bound of the objective value */
  private final int UB;

  /**
   * create
   *
   * @param _m
   *          the constant above n to define the mutation
   *          probability
   * @param _UB
   *          the upper bound of the objective value
   */
  public InnerGreedy2p1GAmodFFA(final int _m, final int _UB) {
    super();
    if (_m <= 0) {
      throw new IllegalArgumentException(
          "m must be at least 1, but you specified " //$NON-NLS-1$
              + _m);
    }
    this.m = _m;
    if (_UB <= 0) {
      throw new IllegalArgumentException(
          "UB must be at least 1, but you specified " //$NON-NLS-1$
              + _UB);
    }
    this.UB = _UB;
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

// Line 1: sample x and y from the search space
    boolean[] x = searchSpace.create();
    nullary.apply(x, random);
    int fx = ((int) (process.evaluate(x)));
    if (process.shouldTerminate()) {
      return;
    }

    boolean[] y = searchSpace.create();
    nullary.apply(y, random);
    int fy = ((int) (process.evaluate(x)));
    Assert.assertNotSame(x, y);

// other initialization stuff
    boolean[] znew = searchSpace.create();
    Assert.assertNotSame(x, znew);
    Assert.assertNotSame(y, znew);

    final long[] H = new long[this.UB + 1]; // FFA

    final int n = x.length;

// allocate necessary data structures
    final BinomialDistribution binDist =
        new BinomialDistribution(n, ((double) (this.m)) / n);
    final DiscreteGreaterThanZero dgtzDist =
        new DiscreteGreaterThanZero(binDist);

// allocate integer array used in mutation
    final int[] indices = new int[n];
    for (int i = n; (--i) >= 0;) {
      indices[i] = i;
    }
// done with the initialization

// line 2: the loop
    while (!process.shouldTerminate()) {
// line 3: ensure that H[fx] <= H[fy]
      if (H[fx] > H[fy]) {
        Assert.assertFalse(Arrays.equals(x, y));
        final boolean[] tx = x;
        x = y;
        y = tx;
        final int tf = fx;
        fx = fy;
        fy = tf;
      } // end rename x to y and vice versa

// invariant: H[fx] <= H[fy]

// line 4: if H[fx] == H[fy] do crossover, otherwise just copy
      final boolean[] zprime;
      boolean zIsNew = false;
      if (H[fx] < H[fy]) { // FFA
// copy from x if H[fx] < H[fy]. In this case, zprime is = x,
// i.e., is not different from x
        Assert.assertFalse(Arrays.equals(x, y));
        zprime = x;
      } else {
// do uniform crossover
        zprime = znew;
        Assert.assertNotSame(zprime, x);
        Assert.assertNotSame(zprime, y);

// first, copy everything from x. this allows us to copy only
// from y in the loop, i.e., to handle one variable less, as we
// do no longer need to look at x
        boolean zIsNotx = false;
        boolean zIsNoty = false;
        System.arraycopy(x, 0, zprime, 0, n);
        for (int i = n; (--i) >= 0;) {
          if (random.nextBoolean()) {
// if we copy zprime from x, then zprime != y if, well, at least
// one zprime[i] != y[i]
            zIsNoty = zIsNoty || (zprime[i] != y[i]);
          } else {
// if we copy zprime from y, then zprime != x if, well, at least
// one zprime[i] != x[i] (which means it must be different from
// the value stored at zprime[i] before the copying)
            final boolean o = zprime[i];
            final boolean d = (zprime[i] = y[i]);
            zIsNotx = zIsNotx || (d != o);
          } // end copy from y
        } // end crossover
        zIsNew = zIsNotx && zIsNoty;
        Assert.assertTrue(zIsNotx != Arrays.equals(x, zprime));
        Assert.assertTrue(zIsNoty != Arrays.equals(y, zprime));

        Assert.assertTrue(zIsNew == (!(Arrays.equals(x, zprime)
            || Arrays.equals(y, zprime))));
      } // end fx == fy

// zIsNew is true only if zprime != x and zprime != y
// Line 5: sample number l of bits to flip
      final int l =
          (zIsNew ? binDist : dgtzDist).nextInt(random);
      final boolean[] z;
      if (l <= 0) {
// If no bits will be flipped, we can set z=zprime.
// If we get here, then zprime != x and zprime != y
// (element-wise) and zIsNew must be true, because only if zIsNew
// is true we may have chosen from binDist directly and only then
// we may have gotten l=0.
        z = zprime;
        Assert.assertFalse(Arrays.equals(z, x));
        Assert.assertFalse(Arrays.equals(z, y));
      } else {
// At least one bit needs to be flipped, i.e., we perform the
// mutation.
        if (zprime == x) {
// If zprime == x, i.e., if fx < fy, then we first need to create
// a copy of it.
          z = znew;
          Assert.assertNotSame(z, x);
          Assert.assertNotSame(z, y);
          System.arraycopy(x, 0, z, 0, n);
        } else {
// zprime=znew: We can use it directly, as we do not need to
// preserve the contents of zprime since zprime!=x and zprime=y.
          z = zprime;
          Assert.assertNotSame(z, x);
          Assert.assertNotSame(z, y);
        }

// Shuffle the first l elements in the index list in a
// Fisher-Yates style.
// This will produce l random, unique, different indices.
        for (int i = 0; i < l; i++) {
          final int j = i + random.nextInt(n - i);
          final int t = indices[j];
          indices[j] = indices[i];
          indices[i] = t;
        }

        for (int j = l; (--j) > 0;) {
          for (int k = j; (--k) >= 0;) {
            Assert.assertNotEquals(indices[j], indices[k]);
          }
        }

// Now we perform the flips. This can make the bit string become
// (element-wise) identical to x or y. We need to check both
// options.
        boolean zIsNotx = false;
        boolean zIsNoty = false;
        zIsNew = false;
        for (int i = l; (--i) >= 0;) {
          final int index = indices[i];
// Flip the bit at the index and remember the result.
          final boolean value = (z[index] ^= true);
// Check if we got z!=x or z!=y: If both already hold, we can
// skip this.
          if (zIsNew) {
            Assert.assertFalse(Arrays.equals(z, x));
            Assert.assertFalse(Arrays.equals(z, y));
            continue;
          }
// Update (element-wise) z!=x and z!=y, hope for lazy evaluation
// to speed up.
          zIsNotx = zIsNotx || (value != x[index]);
          zIsNoty = zIsNoty || (value != y[index]);
// If both hold, we are good.
          if (zIsNotx && zIsNoty) {
            zIsNew = true;
          }
        }

        if (!zIsNew) {
          Assert.assertFalse(zIsNotx && zIsNoty);
// No, we are unlucky: The bit sequences toggled now equal either
// those in x or y, so we need to do a full compare.
          check: {
            checkX: {
              if (!zIsNotx) {
// maybe (element-wise) z==x, so compare
                for (int i = n; (--i) >= 0;) {
                  if (z[i] != x[i]) {
                    break checkX; // found mismatch
                  }
                }
                Assert.assertArrayEquals(z, x);
// no mismatch, i.e., (element-wise) z==x, so we can stop here
                break check;
              }
            }
// if we get here, z!=x (otherwise we would have done break)
            checkY: {
              if (!zIsNoty) { // maybe z==y
                for (int i = n; (--i) >= 0;) {
                  if (z[i] != y[i]) {
                    break checkY; // found mismatch
                  }
                }
                Assert.assertArrayEquals(z, y);
// no mismatch, i.e., (element-wise) z==y, so we can stop here
                break check;
              }
            }
// if we get here, z!=x and z!=y (element-wise)
            zIsNew = true;
            Assert.assertFalse(Arrays.equals(z, x));
            Assert.assertFalse(Arrays.equals(z, y));
          } // end check
        } // end !zIsNew
      } // end crossover: l>0

      if (!zIsNew) {
        Assert.assertTrue(
            Arrays.equals(z, x) || Arrays.equals(z, y));
        continue; // z is not new, so do not evaluate
      }
      // result: z!=y, z!=x (element-wise)

      // line 8
      final int fz = ((int) (process.evaluate(z)));
// update FFA table
      ++H[fx]; // FFA: preserves H[fx] <= H[fy]
      ++H[fy]; // FFA: preserves H[fx] <= H[fy]
      ++H[fz]; // FFA

      if (H[fz] > H[fy]) {
        Assert.assertFalse(Arrays.equals(z, x));
        Assert.assertFalse(Arrays.equals(z, y));
        Assert.assertNotEquals(fz, fy);
// H[fz] > H[fy] and thus also H[fz] > H[fx]: discard!
        continue;
      }

      if ((H[fy] > H[fx])// line 9: replace y with z
          || random.nextBoolean()) {// line 10: randomly chose y
// line 9: replace y with z since z cannot be x or y, we can swap
// pointers instead of copying
        fy = fz;
        final boolean[] ty = y;
        y = z;
        znew = ty;
        Assert.assertNotSame(y, znew);
        Assert.assertNotSame(x, znew);
        continue;
      }

// H[fy] == H[fx], because H[fx] cannot be less than H[fy]
// AND due to random choice above, replace x -> Line 10
      fx = fz;
      final boolean[] tx = x;
      x = z;
      znew = tx;
      Assert.assertNotSame(y, znew);
      Assert.assertNotSame(x, znew);
    } // end main loop
  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final String s = "Greedy(2+1)GAmodFFA"; //$NON-NLS-1$
    if (this.m != 1) {
      return s + this.m;
    }
    return s;
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", 2));///$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("lambda", 1));//$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("cr", 1));//$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("pruning", true)); //$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.newLine();
    output.write(LogFormat.mapEntry("fitness", //$NON-NLS-1$
        "FFA"));//$NON-NLS-1$
    output.newLine();
  }
}

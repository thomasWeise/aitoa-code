package aitoa.algorithms.bitstrings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;
import aitoa.utils.math.BinomialDistribution;
import aitoa.utils.math.DiscreteGreaterThanZero;

/**
 * The Greedy (2+1) GA mod, as defined in Algorithm 6 of E.
 * Carvalho Pinto and C. Doerr, "Towards a more practice-aware
 * runtime analysis of evolutionary algorithms," July 2017,
 * arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online]. Available:
 * http://arxiv.org/pdf/1812.00493.pdf
 *
 * @param <Y>
 *          the solution space
 */
public class Greedy2p1GAmod<Y>
    implements IMetaheuristic<boolean[], Y> {

  /** the constant above n */
  public final int m;

  /**
   * create
   *
   * @param _m
   *          the constant above n to define the mutation
   *          probability
   */
  public Greedy2p1GAmod(final int _m) {
    super();
    if (_m <= 0) {
      throw new IllegalArgumentException(
          "m must be at least 1, but you specified " //$NON-NLS-1$
              + _m);
    }
    this.m = _m;
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
    double fx = process.evaluate(x);
    if (process.shouldTerminate()) {
      return;
    }

    boolean[] y = searchSpace.create();
    nullary.apply(y, random);
    double fy = process.evaluate(x);

    // other initialization stuff
    boolean[] znew = searchSpace.create();

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
      // line 3: ensure that fx <= fy
      if (fx > fy) {
        final boolean[] tx = x;
        x = y;
        y = tx;
        final double tf = fx;
        fx = fy;
        fy = tf;
      } // end rename x to y and vice versa

      // invariant: fx <= fy

      // line 4: if fx == fy do crossover, otherwise just copy
      final boolean[] zprime;
      boolean zIsNew = false;
      if (fx < fy) {
// copy from x if fx < fy. In this case, zprime is = x, i.e., is
// not different from x
        zprime = x;
      } else {
        // do uniform crossover
        zprime = znew;
// first, copy everything from x. this allows us to copy only
// from y in the loop, i.e., to handle one variable less, as we
// do no longer need to look at x
        System.arraycopy(x, 0, zprime, 0, n);
        for (int i = n; (--i) >= 0;) {
          if (random.nextBoolean()) {
// if we copy zprime from x, then zprime != y if, well, at least
// one zprime[i] != y[i]
            zIsNew = zIsNew || (zprime[i] != y[i]);
          } else {
// if we copy zprime from y, then zprime != x if, well, at least
// one zprime[i] != x[i] (which means it must be different from
// the value stored at zprime[i] before the copying)
            final boolean o = zprime[i];
            final boolean d = (zprime[i] = y[i]);
            zIsNew = zIsNew || (d != o);
          } // end copy from y
        } // end mutation
      } // end fx == fy

// zprimeIsNew is true only if zprime != x and zprime != y
// Line 5: sample number l of bits to flip
      final int l =
          (zIsNew ? binDist : dgtzDist).nextInt(random);
      final boolean[] z;
      if (l <= 0) {
// If no bits will be flipped, we can set z=zprime.
// If we get here, then zprime != x and zprime != y
// (element-wise) and zIsNew
// must be true, because only if zIsNew is true we may have
// chosen from binDist directly and only then we may have gotten
// l=0.
        z = zprime;
      } else {
// At least one bit needs to be flipped, i.e., we perform the
// mutation.
        if (zprime == x) {
// If zprime == x, i.e., if fx < fy, then we first need to create
// a copy of it.
          z = znew;
          System.arraycopy(x, 0, z, 0, n);
        } else {
// zprime=znew: We can use it directly, as we do not need to
// preserve the contents of zprime since zprime!=x and zprime=y.
          z = zprime;
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

// Now we perform the flips. This can make the bit string become
// (element-wise) identical to x or y. We need to check both
// options.
        boolean zNEQx = false;
        boolean zNEQy = false;
        zIsNew = false;
        for (int i = l; (--i) >= 0;) {
          final int index = indices[i];
// Flip the bit at the index and remember the result.
          final boolean value = (z[index] ^= true);
// Check if we got z!=x or z!=y: If both already hold, we can
// skip this.
          if (zIsNew) {
            continue;
          }
// Update (element-wise) z!=x and z!=y, hope for lazy evaluation
// to speed up.
          zNEQx = zNEQx || (value != x[index]);
          zNEQy = zNEQy || (value != y[index]);
// If both hold, we are good.
          if (zNEQx && zNEQy) {
            zIsNew = true;
          }
        }

        if (!zIsNew) {
// No, we are unlucky: The bit sequences toggled now equal either
// those in x or y, so we need to do a full compare.
          check: {
            checkX: {
              if (!zNEQx) {
// maybe (element-wise) z==x, so compare
                for (int i = n; (--i) >= 0;) {
                  if (z[i] != x[i]) {
                    break checkX; // found mismatch
                  }
                }
// no mismatch, i.e., (element-wise) z==x, so we can stop here
                break check;
              }
            }
// if we get here, z!=x (otherwise we would have done break)
            checkY: {
              if (!zNEQy) { // maybe z==y
                for (int i = n; (--i) >= 0;) {
                  if (z[i] != y[i]) {
                    break checkY; // found mismatch
                  }
                }
// no mismatch, i.e., (element-wise) z==y, so we can stop here
                break check;
              }
            }
// if we get here, z!=x and z!=y (element-wise)
            zIsNew = true;
          } // end check
        } // end !zIsNew
      } // end crossover: l>0

      if (!zIsNew) {
        continue; // z is not new, so do not evaluate
      }
      // result: z!=y, z!=x (element-wise)

      // line 8
      final double fz = process.evaluate(z);

      if (fz > fy) {
// fz > fy and thus also fz > fx: discard!
        continue;
      }

      if ((fy > fx)// line 9: replace y with z
          || random.nextBoolean()// line 10: randomly chose y
      ) {
// line 9: replace y with z
// since z cannot be x or y, we can swap pointers instead of
// copying
        fy = fz;
        final boolean[] ty = y;
        y = z;
        znew = ty;
        continue;
      }

// fy == fx, because fx cannot be less than fy
// AND due to random choice above, replace x -> Line 10
      fx = fz;
      final boolean[] tx = x;
      x = z;
      znew = tx;
    } // end main loop
  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final String s = "Greedy(2+1)GAmod"; //$NON-NLS-1$
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
  }
}

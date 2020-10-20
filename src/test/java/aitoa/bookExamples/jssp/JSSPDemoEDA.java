package aitoa.bookExamples.jssp;

import java.util.Arrays;
import java.util.Random;

import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPRepresentationMapping;
import aitoa.examples.jssp.JSSPSearchSpace;
import aitoa.structure.Individual;
import aitoa.utils.RandomUtils;

/**
 * This class is used to generate an example for the UMDA-style
 * EDA for the JSSP. The example does not need to be very
 * realistic or exactly represent the probability-based behavior,
 * but it should be easy-to-understand and correct.
 */
public final class JSSPDemoEDA {

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */
  @SuppressWarnings("unchecked")
  public static void main(final String[] args) {

    final JSSPInstance instance = new JSSPInstance("demo"); //$NON-NLS-1$
    final JSSPCandidateSolution y =
        new JSSPCandidateSolution(instance.m, instance.n);
    final JSSPNullaryOperator op =
        new JSSPNullaryOperator(instance);
    final JSSPRepresentationMapping map =
        new JSSPRepresentationMapping(instance);
    final JSSPSearchSpace X = new JSSPSearchSpace(instance);
    final Random random = new Random();
    final JSSPMakespanObjectiveFunction f =
        new JSSPMakespanObjectiveFunction(instance);

    final int[] x = X.create();

    final Individual<int[]>[] keep = new Individual[10];
    for (int i = keep.length; (--i) >= 0;) {
      keep[i] =
          new Individual<>(X.create(), Double.POSITIVE_INFINITY);
    }

    final int[][] M = new int[x.length][instance.n];
    final int[] counts = new int[x.length];
    long seed = 5051947261419604236L;

    findExample: for (;;) {
      random.setSeed(seed);

      for (final Individual<int[]> k : keep) {
        k.quality = Double.POSITIVE_INFINITY;
      }

      int got = 0;
      findSolutions: while (got < keep.length) {
        if ((got <= 0) || (random.nextInt(3) > 0)) {
          op.apply(x, random);
        } else {
          System.arraycopy(keep[random.nextInt(got)].x, 0, x, 0,
              x.length);
          do {
            final int v = random.nextInt(x.length);
            final int a = x[v];
            int w;
            do {
              w = random.nextInt(x.length);
            } while ((w == v) || (x[w] == a));
            final int b = x[w];
            x[w] = a;
            x[v] = b;
          } while (random.nextInt(5) > 0);
        }
        map.map(random, x, y);
        final int z = ((int) (Math.round(f.evaluate(y))));
        if (z >= 195) {
          continue findSolutions;
        }

        int count = 0;
        for (int kk = got; (--kk) >= 0;) {
          final Individual<int[]> k = keep[kk];
          int nonequal = x.length;
          for (int uu = x.length; (--uu) >= 0;) {
            if (k.x[uu] == x[uu]) {
              if ((--nonequal) < 5) {
                continue findSolutions;
              }
            }
          }
          if (z == k.quality) {
            if ((++count) > 3) {
              continue findSolutions;
            }
          }
        }

        keep[got].quality = z;
        System.arraycopy(x, 0, keep[got].x, 0, x.length);
        ++got;
      }

      // got enough solutions
      for (final int[] k : M) {
        Arrays.fill(k, 0);
      }
      for (final Individual<int[]> k : keep) {
        for (int j = k.x.length; (--j) >= 0;) {
          ++M[j][k.x[j]];
        }
      }

      // check if nice
      Arrays.fill(counts, 0);
      int has1Zero = 0;
      int has2Zero = 0;
      for (final int[] found : M) {
        int zeros = 0;
        for (final int k : found) {
          if (k <= 0) {
            ++zeros;
          }
          for (int z = k; z >= 0; --z) {
            ++counts[z];
          }
        }
        if (zeros >= 1) {
          ++has1Zero;
          if (zeros >= 2) {
            ++has2Zero;
          }
        }
      }

      if ((has1Zero >= x.length) && (has2Zero >= (x.length / 3))
          && //
          ((counts[9] > 3) && (counts[8] > 5)
              && (counts[7] >= 8))) {
        break findExample;
      }
      seed = random.nextLong();
    }

    System.out.println(("seed: " + seed) + 'L'); //$NON-NLS-1$
    System.out.println();

    System.out.print(' ');
    System.out.print(' ');
    System.out.print(' ');
    for (int i = 0; i < x.length; i++) {
      System.out.print(' ');
      if (i < 10) {
        System.out.print(' ');
      }
      System.out.print(i);
    }
    System.out.print(" f(x)"); //$NON-NLS-1$

    System.out.println();
    System.out.println();

    // print the solutions
    RandomUtils.shuffle(random, keep, 0, keep.length);
    for (int i = 0; i < keep.length; i++) {
      final Individual<int[]> k = keep[i];
      if (i < 9) {
        System.out.print(' ');
      }
      System.out.print(Integer.toString(i + 1));
      System.out.print(':');
      for (final int j : k.x) {
        System.out.print(' ');
        System.out.print(' ');
        System.out.print(Integer.toString(j));
      }
      System.out.print(' ');
      System.out.println((int) (Math.round(k.quality)));
    }

    // print model
    System.out.println();
    for (int i = 0; i < instance.n; i++) {
      System.out.println();
      System.out.print(Integer.toString(i));
      System.out.print(':');
      for (final int[] element : M) {
        final int z = element[i];
        System.out.print(' ');
        if (z < 10) {
          System.out.print(' ');
        }
        System.out.print(z);
      }
    }

    System.out.println();

    // now sampling
    final int[] available = new int[instance.n];
    final int[] indices = x.clone();
    for (int k = indices.length; (--k) >= 0;) {
      indices[k] = k;
    }

    int makespan = -1;
    final int[][] done = new int[x.length][3];
    final int[] order = new int[instance.n];
    for (int i = order.length; (--i) >= 0;) {
      order[i] = i;
    }

    sample: for (;;) {
      Arrays.fill(available, instance.m);

      looper: for (int j = 0; j < indices.length; j++) {
        final int k = j + random.nextInt(indices.length - j);
        final int index = indices[k];
        indices[k] = indices[j];
        indices[j] = index;

        do {
          final int v = random.nextInt(order.length);
          final int Ov = order[v];
          final int Mv = M[index][Ov];
          int w;
          do {
            w = random.nextInt(order.length);
          } while (w == v);
          final int Ow = order[w];
          final int Mw = M[index][Ow];

          if ((v < w) != (Mv > Mw)) {
            order[v] = Ow;
            order[w] = Ov;
          }
        } while (random.nextInt(3) > 0);

        for (final int sel : order) {
          if ((M[index][sel] > 0) && (available[sel] > 0)) {
            done[j][2] = (((--available[sel]) <= 0) ? -1 : 1);
            x[index] = sel;
            done[j][0] = index;
            done[j][1] = sel;
            continue looper;
          }
        }
        continue sample;
      }

      for (final Individual<int[]> sel : keep) {
        if (Arrays.equals(sel.x, x)) {
          continue sample;
        }
      }

      map.map(random, x, y);
      makespan = ((int) (Math.round(f.evaluate(y))));
      if (makespan > 185) {
        continue;
      }

      break sample;
    }

    // print progress
    int ij = 0;
    for (final int[] yy : done) {
      if (ij > 0) {
        System.out.print(',');
      }
      if (((ij++) % 10) == 0) {
        System.out.println();
      } else {
        System.out.print(' ');
      }
      final String print = Integer.toString(yy[1]) + '\u2192'
          + Integer.toString(yy[0]);
      if (print.length() < 4) {
        System.out.print(' ');
      }
      System.out.print(print);
      if (yy[2] <= 0) {
        System.out.print('/');
        System.out.print(yy[1]);
      }
    }

    System.out.println();
    System.out.println();
    System.out.print('x');
    System.out.print(':');
    for (final int i : x) {
      System.out.print(' ');
      System.out.print(' ');
      System.out.print(i);
    }
    System.out.print(' ');
    System.out.println(makespan);
    System.out.println();
  }

  /** forbidden */
  private JSSPDemoEDA() {
    throw new UnsupportedOperationException();
  }
}

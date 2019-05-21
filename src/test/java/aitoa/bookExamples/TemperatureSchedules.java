package aitoa.bookExamples;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;

import aitoa.algorithms.TemperatureSchedule;

/** Examples for the temperature schedules. */
public class TemperatureSchedules {

  /** the maximum FEs */
  private static final int MAX_FEs = 1024 * 1024 * 16;

  /**
   * add the value
   *
   * @param set
   *          the set
   * @param val
   *          the value
   */
  private static final void __add(final HashSet<Integer> set,
      final int val) {
    if ((val > 0) && (val <= TemperatureSchedules.MAX_FEs)) {
      set.add(Integer.valueOf(val));
    }
  }

  /**
   * make a double array of unique values
   *
   * @param val
   *          the values
   * @return the array
   */
  private static final double[] __make(final double... val) {
    final HashSet<Double> set = new HashSet<>(val.length);
    for (final double d : val) {
      set.add(Double.valueOf(d));
    }
    return set.stream().mapToDouble((d) -> d.doubleValue())
        .sorted().toArray();
  }

  /**
   * the main routine
   *
   * @param args
   *          the arguments
   */
  public static final void main(final String[] args) {
    final double Ts = 10d;
    final TemperatureSchedule[] sched =
        new TemperatureSchedule[] {
            new TemperatureSchedule.Logarithmic(Ts, 0.1d),
            new TemperatureSchedule.Logarithmic(Ts, 1d),
            new TemperatureSchedule.Logarithmic(Ts, 10d),
            new TemperatureSchedule.Exponential(Ts, 0.0000005d),
            new TemperatureSchedule.Exponential(Ts, 0.000001d),
            new TemperatureSchedule.Exponential(Ts,
                0.000002d), };

    final HashSet<Integer> points =
        new HashSet<>(TemperatureSchedules.MAX_FEs);

    for (int i = 1; i <= 128; i++) {
      TemperatureSchedules.__add(points, i);
      TemperatureSchedules.__add(points,
          TemperatureSchedules.MAX_FEs / i);
    }

    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i <<= 1) {
      TemperatureSchedules.__add(points, i);
      TemperatureSchedules.__add(points, i / 3);
      TemperatureSchedules.__add(points, i * 3);
      TemperatureSchedules.__add(points, i / 5);
      TemperatureSchedules.__add(points, i * 5);
      TemperatureSchedules.__add(points, (3 * i) / 5);
      TemperatureSchedules.__add(points, 3 * i * 5);
    }

    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 3) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 5) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 6) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 7) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 9) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 10) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 11) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 13) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 15) {
      TemperatureSchedules.__add(points, i);
    }
    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i *= 20) {
      TemperatureSchedules.__add(points, i);
    }

    final double[] critTS = TemperatureSchedules
        .__make(new double[] { Ts, 0, (7 * Ts) / 8, (6 * Ts) / 8,
            (5 * Ts) / 8, (4 * Ts) / 8, (3 * Ts) / 8,
            (2 * Ts) / 8, (1 * Ts) / 8, (9 * Ts) / 10,
            (8 * Ts) / 10, (7 * Ts) / 10, (6 * Ts) / 10,
            (5 * Ts) / 10, (4 * Ts) / 10, (3 * Ts) / 10,
            (2 * Ts) / 10, (1 * Ts) / 10, (1 * Ts) / 6,
            (2 * Ts) / 6, (4 * Ts) / 6, (3 * Ts) / 6,
            (5 * Ts) / 6, (1 * Ts) / 7, (2 * Ts) / 7,
            (3 * Ts) / 7, (4 * Ts) / 7, (5 * Ts) / 7,
            (6 * Ts) / 7, (1 * Ts) / 9, (2 * Ts) / 9,
            (3 * Ts) / 9, (4 * Ts) / 9, (5 * Ts) / 9,
            (6 * Ts) / 9, (7 * Ts) / 9, (8 * Ts) / 9, 0, 1, 2, 3,
            4, 5, 6, 7, 8, 9, 10, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6,
            0.7, 0.8, 0.9, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7,
            1.8, 1.9, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07,
            0.08, 0.09 });
    final int[][] idxs = new int[sched.length][critTS.length];
    final double[][] vdxs =
        new double[sched.length][critTS.length];
    for (final double[] dd : vdxs) {
      Arrays.fill(dd, Double.POSITIVE_INFINITY);
    }

    for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i++) {
      for (int j = sched.length; (--j) >= 0;) {
        final double v = sched[j].temperature(i);
        for (int k = critTS.length; (--k) >= 0;) {
          final double dif = Math.abs(critTS[k] - v);
          if (dif <= vdxs[j][k]) {
            vdxs[j][k] = dif;
            idxs[j][k] = i;
          }
        }
      }
    }

    for (final int[] is : idxs) {
      for (final int k : is) {
        TemperatureSchedules.__add(points, k);
      }
    }

    final int[] targets = { 1, 2, 3, 5 };
    final double[] critProb = TemperatureSchedules.__make(
        new double[] { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07,
            0.08, 0.09, 0.01, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4,
            0.5, 0.6, 0.7, 0.8, 0.9, 1, 0.25, 0.75, 1 / 8d,
            3d / 8d, 7d / 8d, 7d / 8d, 1 / 3d, 2d / 3d, 1 / 6d,
            5d / 6d, 1 / 9d, 2 / 9d, 3 / 9d, 4 / 9d, 5 / 9d,
            6 / 9d, 7 / 9d, 8 / 9d, 1 / 7d, 2 / 7d, 3 / 7d,
            4 / 7d, 5 / 7d, 6 / 7d, 1 / 20d, 2 / 20d, 3 / 20d,
            4 / 20d, 5 / 20d, 6 / 20d, 7 / 20d, 89 / 20d,
            10 / 20d, 11 / 20d, 12 / 20d, 13 / 20d, 14 / 20d,
            15 / 20d, 16 / 20d, 17 / 20d, 18 / 20d, 19 / 20d, });

    for (final int target : targets) {
      final int[][] nidxs =
          new int[sched.length][critProb.length];
      final double[][] nvdxs =
          new double[sched.length][critProb.length];
      for (final double[] dd : nvdxs) {
        Arrays.fill(dd, Double.POSITIVE_INFINITY);
      }

      for (int i = 1; i <= TemperatureSchedules.MAX_FEs; i++) {
        for (int j = sched.length; (--j) >= 0;) {
          final double v =
              Math.exp(-target / sched[j].temperature(i));
          for (int k = critProb.length; (--k) >= 0;) {
            final double dif = Math.abs(critProb[k] - v);
            if (dif <= nvdxs[j][k]) {
              nvdxs[j][k] = dif;
              nidxs[j][k] = i;
            }
          }
        }
      }

      for (final int[] is : nidxs) {
        for (final int k : is) {
          TemperatureSchedules.__add(points, k);
        }
      }
    }

    try (
        final FileOutputStream fos =
            new FileOutputStream("temperatureSchedules.txt"); //$NON-NLS-1$
        final PrintStream ps = new PrintStream(fos)) {

      ps.print("tau"); //$NON-NLS-1$
      for (final TemperatureSchedule s : sched) {
        ps.print(',');
        ps.print(s.toString());
        for (final int target : targets) {
          ps.print(',');
          ps.print('P');
          ps.print(target);
          ps.print('_');
          ps.print(s.toString());
        }
      }
      ps.println();

      for (final int tau : points.stream()
          .mapToInt((x) -> x.intValue()).sorted().toArray()) {
        ps.print(tau);
        for (final TemperatureSchedule s : sched) {
          ps.print(',');
          ps.print(s.temperature(tau));
          for (final int target : targets) {
            ps.print(',');
            ps.print(Math.exp(-target / s.temperature(tau)));
          }
        }
        ps.println();
      }
    } catch (final Throwable error) {
      error.printStackTrace();
    }
  }
}

package aitoa.utils.math;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleUnaryOperator;

/** A class with quick internal optimization routines */
public final class QuickOptimizers {

  /**
   * the golden cut used by
   * {@link #unimodal1Dminimization(DoubleUnaryOperator, double, double, double[], boolean)}
   */
  private static final double CUT_UNI1D =
      (0.5d * (3d - Math.sqrt(5d)));

  /**
   * invoke the objective function
   *
   * @param f
   *          the function
   * @param x
   *          the argument
   * @return the result
   */
  private static double call(final DoubleUnaryOperator f,
      final double x) {
    final double r = f.applyAsDouble(x);
    if (Double.isFinite(r)) {
      return r;
    }
    throw new IllegalArgumentException("f(" + x + //$NON-NLS-1$
        ")=" + r); //$NON-NLS-1$
  }

  /**
   * Perform the minimization of an unimodal 1-dimensional
   * function. This is done via Golden Section Search (see, e.g.,
   * https://www.cs.ccu.edu.tw/~wtchu/courses/2014s_OPT/Lectures/Chapter%207%20One-Dimensional%20Search%20Methods.pdf)
   * followed by a fine-tuning phase. If the function is
   * uni-modal, then this function should yield the optimal value
   * within {@code [lower, upper]} to the available system
   * precision.
   *
   * @param f
   *          the function
   * @param shouldTerminate
   *          should we terminate? This function is checked after
   *          ever call to {@code f}
   * @param lower
   *          the lower end of the search interval
   * @param upper
   *          the upper end of the search interval
   * @param dest
   *          an array of length two which will receive the
   *          optimal argument and its corresponding objective
   *          value
   * @param fineAdjust
   *          should we perform fine-adjustment?
   */
  public static void unimodal1Dminimization(
      final DoubleUnaryOperator f,
      final BooleanSupplier shouldTerminate, final double lower,
      final double upper, final double[] dest,
      final boolean fineAdjust) {

    dest[0] = Double.NaN;
    dest[1] = Double.NaN;

    if (!(Double.isFinite(lower) && //
        Double.isFinite(upper) && //
        (upper >= lower))) {
      throw new IllegalArgumentException((((//
      "Invalid bounds [" //$NON-NLS-1$
          + lower) + ',') + upper) + ']');
    }

    double a0 = lower;
    double b0 = upper;

    // Check boundaries
    double a1 = a0;
    double fa1 = QuickOptimizers.call(f, a1);
    double bestX = a1;
    double bestF = fa1;

    compute: {
      if ((lower >= upper) || shouldTerminate.getAsBoolean()) {
        break compute;
      }
      double fa0 = fa1;

      double b1 = b0;
      double fb1 = QuickOptimizers.call(f, b1);
      double fb0 = fb1;

      if (fb1 < fa1) {
        bestX = b1;
        bestF = fb1;
      }
      if (shouldTerminate.getAsBoolean()) {
        break compute;
      }

      // perform Golden Section Search
      while (a0 < b0) {
        final double range =
            QuickOptimizers.CUT_UNI1D * (b0 - a0);

// We always compute next lower point to investigate to avoid
// numerical imprecision. If if the point coincides with a0, we
// move it up one step. This also indicates that our range has
// collapsed.
        double t = a0 + range;
        if (t <= a0) {
          t = Math.nextUp(a0);
        }
        if (t != a1) {
// If the point is not equal to a1, we need to compute its
// objective value.
          a1 = t;
          if (a1 > a0) {
            fa1 = QuickOptimizers.call(f, a1);
            if (fa1 < bestF) {
              bestF = fa1;
              bestX = a1;
            }
            if (shouldTerminate.getAsBoolean()) {
              break compute;
            }
          } else {
            fa1 = fa0;
          }
        }

// We always compute higher lower point to investigate to avoid
// numerical imprecision. If if the point coincides with b0, we
// move it up one step. This also indicates that our range has
// collapsed.
        t = b0 - range;
        if (t >= b0) {
          t = Math.nextDown(b0);
        }
        if (t != b1) {
// If the point is not equal to b1, we need to compute its
// objective value.
          b1 = t;
          if (b1 < b0) {
            fb1 = QuickOptimizers.call(f, b1);
            if (fb1 < bestF) {
              bestF = fb1;
              bestX = b1;
            }
            if (shouldTerminate.getAsBoolean()) {
              break compute;
            }
          } else {
            fb1 = fb0;
          }
        }

// Set the new interval boundaries.
        if (fa1 < fb1) {
          b0 = b1;
          fb0 = fb1;
          b1 = a1;
          fb1 = fa1;
        } else {
          a0 = a1;
          fa0 = fa1;
          a1 = b1;
          fa1 = fb1;
        }
      }

      if (!fineAdjust) {
        break compute;
      }

      b1 = bestX;
      a1 = bestX;

// move down if this means improving
      for (;;) {
        a1 = Math.nextDown(a1);
        if (a1 > lower) {
          fa1 = QuickOptimizers.call(f, a1);
          if (fa1 < bestF) {
            bestF = fa1;
            bestX = a1;
            if (shouldTerminate.getAsBoolean()) {
              break compute;
            }
            continue;
          }
          if (shouldTerminate.getAsBoolean()) {
            break compute;
          }
        }
        break;
      }

// move up if this means improving
      for (;;) {
        b1 = Math.nextUp(b1);
        if (b1 < upper) {
          fb1 = QuickOptimizers.call(f, b1);
          if (fb1 < bestF) {
            bestF = fb1;
            bestX = b1;
            if (shouldTerminate.getAsBoolean()) {
              break compute;
            }
            continue;
          }
          if (shouldTerminate.getAsBoolean()) {
            break compute;
          }
        }
        break;
      }
    } // end compute

    dest[0] = bestX;
    dest[1] = bestF;
  }

  /**
   * Perform the minimization of an unimodal 1-dimensional
   * function. This is done via Golden Section Search (see, e.g.,
   * https://www.cs.ccu.edu.tw/~wtchu/courses/2014s_OPT/Lectures/Chapter%207%20One-Dimensional%20Search%20Methods.pdf)
   * followed by a fine-tuning phase. If the function is
   * uni-modal, then this function should yield the optimal value
   * within {@code [lower, upper]} to the available system
   * precision.
   *
   * @param f
   *          the function
   * @param lower
   *          the lower end of the search interval
   * @param upper
   *          the upper end of the search interval
   * @param dest
   *          an array of length two which will receive the
   *          optimal argument and its corresponding objective
   *          value
   * @param fineAdjust
   *          should we perform fine-adjustment?
   */
  public static void unimodal1Dminimization(
      final DoubleUnaryOperator f, final double lower,
      final double upper, final double[] dest,
      final boolean fineAdjust) {
    QuickOptimizers.unimodal1Dminimization(f, () -> false, lower,
        upper, dest, fineAdjust);
  }

  /** forbidden */
  private QuickOptimizers() {
    throw new UnsupportedOperationException();
  }
}

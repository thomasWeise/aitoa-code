package aitoa.utils.math;

import java.math.BigInteger;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;

/**
 * Some statistical utility methods. The goal here is to have
 * high precision, not speed. Actually, I just coded all this
 * stuff in one go. It is probably terribly slow and inefficient.
 * But at least, the results should be relatively exact.
 */
public final class Statistics {

  /**
   * The quantile value for the "q159" quantile. This is the
   * quantile of the standard normal distribution equivalent to
   * drawing a value less than -1, i.e., less than "mean-standard
   * deviation"
   */
  public static final double GAUSSIAN_QUANTILE_159 =
      0.1586552539314570514147674543679620775220870332734d;

  /**
   * The quantile value for the "q841" quantile. This is the
   * quantile of the standard normal distribution equivalent to
   * drawing a value greater than 1, i.e., less than
   * "mean+standard deviation"
   */
  public static final double GAUSSIAN_QUANTILE_841 =
      0.8413447460685429485852325456320379224779129667266d;

  /**
   * Tries to convert an array of double to an array of long.
   * This will only succeed if all the doubles are integers in
   * the valid range for long. If so, a new long array will be
   * created and returned. If not, null will be returned. This
   * method throws an exception if an infinite value is
   * encountered.
   *
   * @param data
   *          the data array
   * @return the long array with the same data, or null if
   *         conversion is not possible
   */
  public static long[]
      tryConvertDoublesToLongs(final double[] data) {
    int i = data.length;
    if (i <= 0) {
      return new long[0];
    }
    final long[] res = new long[i];
    for (; (--i) >= 0;) {
      final double d = data[i];
      if (Double.isFinite(d)) {
        if ((d < Long.MIN_VALUE) || (d > Long.MAX_VALUE)) {
          return null;
        }
        final long l = ((long) d);
        if (d != l) {
          return null;
        }
        res[i] = l;
      } else {
        throw new ArithmeticException(d + " at index " + i); //$NON-NLS-1$
      }
    }
    return res;
  }

  /**
   * Check the quantile value parameters
   *
   * @param p
   *          the quantile
   * @param length
   *          the length
   */
  private static void quantileCheck(final double p,
      final int length) {
    if ((!Double.isFinite(p)) || (p < 0d) || (p > 1d)) {
      throw new IllegalArgumentException(
          "invalid quantile value: " + p); //$NON-NLS-1$
    }
    if (length <= 0) {
      throw new IllegalArgumentException(
          "length must be greater than 0."); //$NON-NLS-1$
    }
  }

  /**
   * Compute a quantile index. This function is equivalent to
   * what Apache Commons Math does for estimation method
   * {@code R-8} and is also equivalent to the {@code R} quantile
   * function with {@code type=8}.
   *
   * @param p
   *          the quantile
   * @param length
   *          the length
   * @return the index
   */
  private static double quantileIndex(final double p,
      final int length) {

    final double minLimit =
        (2d * (1d / 3d)) / (length + (1d / 3d));
    final double maxLimit =
        (length - (1d / 3d)) / (length + (1d / 3d));
    final double pos = (Double.compare(p, minLimit) < 0) ? 0d
        : (Double.compare(p, maxLimit) >= 0) ? length
            : (((length + (1d / 3d)) * p) + (1d / 3d));

    if ((pos < 0d) || (pos > length)) {
      throw new ArithmeticException("computed invalid pos " //$NON-NLS-1$
          + pos + " for p=" //$NON-NLS-1$
          + p + " and length=" + length); //$NON-NLS-1$
    }

    return pos;
  }

  /**
   * convert a double to a number
   *
   * @param d
   *          the double
   * @return the number
   */
  private static Number doubleToNumber(final double d) {
    if (!Double.isFinite(d)) {
      throw new ArithmeticException(String.valueOf(d));
    }
    if ((d >= Long.MIN_VALUE) && (d <= Long.MAX_VALUE)) {
      final long lres = ((long) d);
      if (lres == d) {
        return Long.valueOf(lres);
      }
    }
    return Double.valueOf(d);
  }

  /**
   * try to simplify a big integer number
   *
   * @param bi
   *          the big integer
   * @return the simplified version
   */
  private static Number simplifyInteger(final BigInteger bi) {
    try {
      return Long.valueOf(bi.longValueExact());
    } catch (@SuppressWarnings("unused") //
    final ArithmeticException ignore) {
      return bi;
    }
  }

  /**
   * Compute the {@code p} quantile (with {@code 0<=p<=1}) of the
   * given SORTED data array. This function is equivalent to what
   * Apache Commons Math would do for estimation method
   * {@code R-8} and is also equivalent to the {@code R} quantile
   * function with {@code type=8}.
   *
   * @param p
   *          the quantile value, must be in {@code [0,1]}
   * @param data
   *          the SORTED data array
   * @return a number trying to be precise about the quantile
   */
  public static Number quantile(final double p,
      final long[] data) {
    final int length = data.length;
    Statistics.quantileCheck(p, length);

    if (length == 1) {
      return Long.valueOf(data[0]);
    }

    final double pos = Statistics.quantileIndex(p, length);

    if (pos < 1) {
      return Long.valueOf(data[0]);
    }
    if (pos >= length) {
      return Long.valueOf(data[length - 1]);
    }

    long min = Long.MAX_VALUE;
    long max = Long.MIN_VALUE;
    for (final long d : data) {
      min = Math.min(d, min);
      max = Math.max(d, max);
    }
    if (min >= max) {
      return Long.valueOf(min);
    }

    final double fpos = Math.floor(pos);
    final int intPos = (int) fpos;
    final double dif = (pos - fpos);
    if (dif < 0d) {
      throw new ArithmeticException("invalid dif: " //$NON-NLS-1$
          + dif);
    }

    if ((fpos == pos) || (dif <= 0d)) {
      return Long.valueOf(
          Math.max(min, Math.min(max, data[intPos - 1])));
    }

    final long lower = data[intPos - 1];
    final long upper = data[intPos];
    if (lower == upper) {
      return Long.valueOf(Math.max(min, Math.min(max, lower)));
    }

    // for accurate computation, we try to stay in the realm of
    // longs for as long as we can
    accurate: {
      final long ldif;
      try {
        ldif = Math.subtractExact(upper, lower);
      } catch (@SuppressWarnings("unused") //
      final ArithmeticException ignore) {
        break accurate;
      }

      final double ldifmul = dif * ldif;
      if ((ldifmul >= Long.MIN_VALUE)
          && (ldifmul <= Long.MAX_VALUE)) {
        final long ll = ((long) ldifmul);
        if (ll == ldifmul) {
          try {
            return Long.valueOf(Math.max(min,
                Math.min(max, Math.addExact(ll, lower))));
          } catch (@SuppressWarnings("unused") //
          final ArithmeticException ignore) {
            //
          }
        }
      }

      return Statistics.doubleToNumber(
          Math.max(min, Math.min(max, lower + ldifmul)));
    }

    return Statistics.doubleToNumber(Math.max(min, Math.min(max,
        lower + (dif * (((double) upper) - lower)))));
  }

  /**
   * Compute the {@code p} quantile (with {@code 0<=p<=1}) of the
   * given SORTED data array. This function is equivalent to what
   * Apache Commons Math would do for estimation method
   * {@code R-8} and is also equivalent to the {@code R} quantile
   * function with {@code type=8}.
   *
   * @param p
   *          the quantile value, must be in {@code [0,1]}
   * @param data
   *          the SORTED data array, containing only finite
   *          values
   * @return a number trying to be precise about the quantile
   */
  public static Number quantile(final double p,
      final double[] data) {
    final int length = data.length;
    Statistics.quantileCheck(p, length);

    if (length == 1) {
      return Statistics.doubleToNumber(data[0]);
    }

    final double pos = Statistics.quantileIndex(p, length);

    if (pos < 1) {
      return Statistics.doubleToNumber(data[0]);
    }
    if (pos >= length) {
      return Statistics.doubleToNumber(data[length - 1]);
    }

    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (final double d : data) {
      min = Math.min(d, min);
      max = Math.max(d, max);
    }
    if (Double.isFinite(min) && Double.isFinite(max)) {
      if (min >= max) {
        return Statistics.doubleToNumber(min);
      }
    } else {
      throw new IllegalArgumentException("Minimum " + min //$NON-NLS-1$
          + " and maximumg " + max //$NON-NLS-1$
          + " must both be finite."); //$NON-NLS-1$
    }

    final double fpos = Math.floor(pos);
    final int intPos = (int) fpos;
    final double dif = (pos - fpos);
    if (dif < 0d) {
      throw new ArithmeticException("invalid dif: " //$NON-NLS-1$
          + dif);
    }

    if ((fpos == pos) || (dif <= 0d)) {
      return Statistics.doubleToNumber(
          Math.max(min, Math.min(max, data[intPos - 1])));
    }

    final double lower = data[intPos - 1];
    final double upper = data[intPos];
    if (lower == upper) {
      return Statistics
          .doubleToNumber(Math.max(min, Math.min(max, lower)));
    }

    return Statistics.doubleToNumber(//
        Math.max(min, Math.min(max, //
            lower + (dif * (upper - lower)))));
  }

  /**
   * Try to divide as exactly as possible. This code does not
   * make much sense, I guess, but I think it should get as close
   * to the closest double to the result as can be done.
   *
   * @param a
   *          the number above the dash
   * @param b
   *          the number below
   * @return the result
   */
  private static Number divideExact(final long a, final int b) {
    // first we compute the greatest common divisor of a and b
    long x = a;
    long y = b;
    long gcd = 1L;
    while (x != 0) {
      gcd = x;
      x = y % x;
      y = gcd;
    }

    // now we can divide both a and b by it
    final long aa = a / gcd;
    final long bb = b / gcd;

    // compute the result with long arithmetic
    final long result = aa / bb;
    final long rest = aa % bb;
    if (rest == 0L) { // cool, we could compute it exactly
      return Long.valueOf(result);
    }

    // no, we need double arithmetic
    return Statistics
        .doubleToNumber(result + (rest / ((double) bb)));
  }

  /**
   * Try to divide as exactly as possible. This code does not
   * make much sense, I guess, but I think it should get as close
   * to the closest double to the result as can be done.
   *
   * @param a
   *          the number above the dash
   * @param b
   *          the number below
   * @return the result
   */
  public static Number divideExact(final Number a, final int b) {
    if (a instanceof Long) {
      return Statistics.divideExact(a.longValue(), b);
    }
    if (a instanceof BigInteger) {
      return Statistics.divideExact((BigInteger) a, b);
    }
    final double d = a.doubleValue();
    if ((d >= Long.MIN_VALUE) && (d <= Long.MAX_VALUE)) {
      final long ld = ((long) d);
      if (ld == d) {
        return Statistics.divideExact(ld, b);
      }
    }
    return Statistics.doubleToNumber(d / b);
  }

  /**
   * Try to divide as exactly as possible. This code does not
   * make much sense, I guess, but I think it should get as close
   * to the closest double to the result as can be done.
   *
   * @param a
   *          the number above the dash
   * @param b
   *          the number below
   * @return the result
   */
  private static Number divideExact(final BigInteger a,
      final int b) {
    final BigInteger bib = BigInteger.valueOf(b);
    final BigInteger gcd = a.gcd(bib);
    final BigInteger useA = a.divide(gcd);
    final BigInteger useB = bib.divide(gcd);

    final Number z = Statistics.simplifyInteger(useA);
    if (z instanceof Long) {
      return Statistics.divideExact(z.longValue(),
          useB.intValue());
    }

    // compute the result with long arithmetic
    final BigInteger result = useA.divide(useB);
    final BigInteger rest = useA.mod(useB);
    if (useB.equals(BigInteger.ZERO)) {
      // cool, we could compute it exactly
      return Statistics.simplifyInteger(result);
    }

    // no, we need double arithmetic
    return Statistics.doubleToNumber(result.doubleValue()
        + (rest.doubleValue() / useB.intValue()));
  }

  /**
   * Compute the sum of some data as exactly as possible.
   *
   * @param data
   *          the data
   * @return the sum
   */
  public static Number sum(final long[] data) {
    // compute exact sums
    asLong: {
      long lsum = 0L;
      try {
        for (final long l : data) {
          lsum = Math.addExact(lsum, l);
        }
      } catch (@SuppressWarnings("unused") //
      final ArithmeticException ignore) {
        break asLong;
      }
      return Long.valueOf(lsum);
    }

    BigInteger last = BigInteger.valueOf(0L);
    BigInteger bsum = last;
    long lastL = 0L;
    for (final long l : data) {
      if (l != lastL) {
        lastL = l;
        last = BigInteger.valueOf(l);
      }
      bsum = bsum.add(last);
    }
    return Statistics.simplifyInteger(bsum);
  }

  /**
   * Compute the sum of some data as exactly as possible.
   *
   * @param data
   *          the data
   * @param transLong
   *          a transformation to be applied to all {@code long}
   *          values before adding them up
   * @param transBigInt
   *          a transformation to be applied to all big integer
   *          values before adding them up
   * @return the sum
   */
  public static Number sum(final long[] data,
      final LongUnaryOperator transLong,
      final Function<BigInteger, BigInteger> transBigInt) {
    // compute exact sums
    asLong: {
      long lsum = 0L;
      try {
        for (final long l : data) {
          lsum = Math.addExact(lsum, transLong.applyAsLong(l));
        }
      } catch (@SuppressWarnings("unused") //
      final ArithmeticException ignore) {
        break asLong;
      }
      return Long.valueOf(lsum);
    }

    BigInteger last = BigInteger.valueOf(0L);
    BigInteger bsum = last;
    long lastL = 0L;
    for (final long l : data) {
      if (l != lastL) {
        lastL = l;
        last = transBigInt.apply(BigInteger.valueOf(l));
      }
      bsum = bsum.add(last);
    }
    return Statistics.simplifyInteger(bsum);
  }

  /**
   * Compute the mean and the standard deviation of a data array
   * in one go
   *
   * @param values
   *          the values
   * @return an array containing two number objects, first the
   *         one with the mean, then the one with the standard
   *         deviation
   */
  public static Number[]
      sampleMeanAndStandardDeviation(final long[] values) {

    // handle simple cases
    if (values.length <= 0) {
      throw new IllegalArgumentException(
          "need at least one value."); //$NON-NLS-1$
    }

    allSame: { // are all values the same?
      final long l1 = values[0];
      for (final long v : values) {
        if (v != l1) {
          break allSame;
        }
      }

      return new Number[] { Long.valueOf(l1), Long.valueOf(0) };
    }

    // no, they are not
    final Number sum = Statistics.sum(values);
    final Number mean =
        Statistics.divideExact(sum, values.length);
    final Number sumOfSquares = Statistics.sum(values,
        l -> Math.multiplyExact(l, l), l -> l.multiply(l));

    // ok, we got some exact sums and exact sums of squares
    final BigInteger sumBI =
        ((sum instanceof BigInteger) ? ((BigInteger) sum)
            : BigInteger.valueOf(sum.longValue()));
    final Number sumSquared =
        Statistics.simplifyInteger(sumBI.multiply(sumBI));
    final Number sumSquaredOverN =
        Statistics.divideExact(sumSquared, values.length);

    final double sd =
        Math.sqrt(Statistics.divideExact(
            Statistics.doubleToNumber(sumOfSquares.doubleValue()
                - sumSquaredOverN.doubleValue()),
            values.length - 1).doubleValue());

    if ((!Double.isFinite(sd)) || (sd < 0d)) {
      throw new ArithmeticException(
          "standard deviation must be positive and finite, but is " //$NON-NLS-1$
              + sd);
    }

    return new Number[] { mean, Statistics.doubleToNumber(sd) };
  }

  /**
   * Compute the sum over a set of {@code double} values
   *
   * @param data
   *          the data
   * @return the sum
   */
  public static Number sum(final double[] data) {
    return Statistics
        .doubleToNumber(Statistics.destructiveSum(data.clone()));
  }

  /**
   * Compute the sum over a set of {@code double} values
   *
   * @param data
   *          the data
   * @param trans
   *          a transformation to be applied to each value before
   *          adding
   * @return the sum
   */
  public static Number sum(final double[] data,
      final DoubleUnaryOperator trans) {
    final double[] tmp = data.clone();
    for (int i = tmp.length; (--i) >= 0;) {
      tmp[i] = trans.applyAsDouble(tmp[i]);
    }
    return Statistics
        .doubleToNumber(Statistics.destructiveSum(tmp));
  }

  /**
   * Compute the exact sum of the values in the given array
   * {@code summands} while destroying the contents of said
   * array.
   * <h2>Inspiration</h2>
   * <p>
   * Python provides a function called {@code msum} whose
   * {@code C} source code is the inspiration of the
   * {@link #destructiveSum(double[])} method used internally
   * here. (Well, I basically translated it to {@code Java} and
   * modified it a bit.)
   * </p>
   * <h2>Original Python Method</h2>
   * <p>
   * Python is open source and licensed under the <i>PYTHON
   * SOFTWARE FOUNDATION LICENSE VERSION 2</i>, PSF, which is GPL
   * compatible.
   * </p>
   * <p>
   * Based on the source (https://www.python.org/getit/source/)
   * of Python 3.5.1rc1 - 2015-11-23,) a stable sum of {@code n}
   * numbers can be computed as:
   * </p>
   *
   * <pre>
     def msum(iterable):
         partials = []  # sorted, non-overlapping partial sums
         for x in iterable:
             i = 0
             for y in partials:
                 if abs(x) &lt; abs(y):
                     x, y = y, x
                 hi = x + y
                 lo = y - (hi - x)
                 if lo:
                     partials[i] = lo
                     i += 1
                 x = hi
             partials[i:] = [x]
         return sum_exact(partials)
   * </pre>
   * <p>
   * The {@code C} source code behind that method is:
   * </p>
   *
   * <pre>
  static PyObject*
  math_fsum(PyObject *self, PyObject *seq)
  {
      PyObject *item, *iter, *sum = NULL;
      Py_ssize_t i, j, n = 0, m = NUM_PARTIALS;
      double x, y, t, ps[NUM_PARTIALS], *p = ps;
      double xsave, special_sum = 0.0, inf_sum = 0.0;
      volatile double hi, yr, lo;
      iter = PyObject_GetIter(seq);
      if (iter == NULL)
          return NULL;
      PyFPE_START_PROTECT("fsum", Py_DECREF(iter); return NULL)
      for(;;) {
          assert(0 &lt;= n &amp;&amp; n &lt;= m);
          assert((m == NUM_PARTIALS &amp;&amp; p == ps) ||
                 (m &gt;  NUM_PARTIALS &amp;&amp; p != NULL));
          item = PyIter_Next(iter);
          if (item == NULL) {
              if (PyErr_Occurred())
                  goto _fsum_error;
              break;
          }
          x = PyFloat_AsDouble(item);
          Py_DECREF(item);
          if (PyErr_Occurred())
              goto _fsum_error;
          xsave = x;
          for (i = j = 0; j &lt; n; j++) {
              y = p[j];
              if (fabs(x) &lt; fabs(y)) {
                  t = x; x = y; y = t;
              }
              hi = x + y;
              yr = hi - x;
              lo = y - yr;
              if (lo != 0.0)
                  p[i++] = lo;
              x = hi;
          }
          n = i;
          if (x != 0.0) {
              if (! Py_IS_FINITE(x)) {
                  if (Py_IS_FINITE(xsave)) {
                      PyErr_SetString(PyExc_OverflowError,
                            "intermediate overflow in fsum");
                      goto _fsum_error;
                  }
                  if (Py_IS_INFINITY(xsave))
                      inf_sum += xsave;
                  special_sum += xsave;
                  n = 0;
              }
              else if (n &gt;= m &amp;&amp; _fsum_realloc(&amp;p, n, ps, &amp;m))
                  goto _fsum_error;
              else
                  p[n++] = x;
          }
      }
      if (special_sum != 0.0) {
          if (Py_IS_NAN(inf_sum))
              PyErr_SetString(PyExc_ValueError,
                              "-inf + inf in fsum");
          else
              sum = PyFloat_FromDouble(special_sum);
          goto _fsum_error;
      }
      hi = 0.0;
      if (n &gt; 0) {
          hi = p[--n];
          while (n &gt; 0) {
              x = hi;
              y = p[--n];
              assert(fabs(y) &lt; fabs(x));
              hi = x + y;
              yr = hi - x;
              lo = y - yr;
              if (lo != 0.0)
                  break;
          }
          if (n &gt; 0 &amp;&amp; ((lo &lt; 0.0 &amp;&amp; p[n-1] &lt; 0.0) ||
                        (lo &gt; 0.0 &amp;&amp; p[n-1] &gt; 0.0))) {
              y = lo * 2.0;
              x = hi + y;
              yr = x - hi;
              if (y == yr)
                  hi = x;
          }
      }
      sum = PyFloat_FromDouble(hi);
  _fsum_error:
      PyFPE_END_PROTECT(hi)
      Py_DECREF(iter);
      if (p != ps)
          PyMem_Free(p);
      return sum;
  }
   * </pre>
   *
   * <h2>Modifications</h2>
   * <p>
   * I translated the above code to {@code Java} and applied a
   * few changes, namely:
   * </p>
   * <ol>
   * <li>The method {@link #destructiveSum(double[])} takes the
   * summands to be added as input array and overrides this array
   * in the process of summation with the compensation values.
   * Since &ndash; differently from the original method &ndash;
   * it does not allocate any additional memory on the heap, it
   * may be useful for frequent calls in performance-critical
   * code.</li>
   * <li>I did not understand how the original method handles
   * infinities, overflows, and NaNs. So I made own code there. I
   * hope I did it right.</li>
   * <li>The method can also be used to accurately add
   * {@code long} values into a {@code double} sum by splitting
   * each {@code long} into two values (in order to deal with the
   * fact that {@code double} has a 52 bit mantissa and thus can
   * only represent a subset of the 64 bit long values
   * accurately).</li>
   * </ol>
   * <h2>Seel Also</h2>
   * <ol>
   * <li>http://code.activestate.com/recipes/393090-binary-floating-point-
   * summation-accurate-to-full-p/</li>
   * <li>https://www.python.org/getit/source/ for Python 3.5.1rc1
   * - 2015-11-23</li>
   * <li>http://stackoverflow.com/questions/33866563/</li>
   * </ol>
   *
   * @param summands
   *          the summand array &ndash; will be summed up and
   *          destroyed
   * @return the accurate sum of the elements of {@code summands}
   */
  static double destructiveSum(final double[] summands) {
    int index = 0;

    int n = 0;
    double lo = 0d;
    boolean ninf = false;
    boolean pinf = false;

    main: {
      allIsOK: {
// the main summation routine
        for (index = 0; index < summands.length; index++) {
          final double xsave = summands[index];
          double summand = xsave;
          int i = 0;
          int j = 0;
          for (; j < n; j++) {
            double y = summands[j];
            if (Math.abs(summand) < Math.abs(y)) {
              final double t = summand;
              summand = y;
              y = t;
            }
            final double hi = summand + y;
            final double yr = hi - summand;
            lo = y - yr;
            if (lo != 0.0) {
              summands[i++] = lo;
            }
            summand = hi;
          }

          n = i;
          if (summand != 0d) {
            if ((summand > Double.NEGATIVE_INFINITY)
                && (summand < Double.POSITIVE_INFINITY)) {
              summands[n++] = summand;// all finite: continue
            } else {
              summands[index] = xsave;
              break allIsOK;
            }
          }
        }
        break main;
      }

// we have error'ed: either due to overflow or because there was
// an infinity or NaN value in the data
      for (; index < summands.length; index++) {
        final double summand = summands[index];

        if (summand <= Double.NEGATIVE_INFINITY) {
          if (pinf) {
            return Double.NaN;
          }
          ninf = true;
        } else {
          if (summand >= Double.POSITIVE_INFINITY) {
            if (ninf) {
              return Double.NaN;
            }
            pinf = true;
          }
        }
      }

      if (pinf) {
        return Double.POSITIVE_INFINITY;
      }
      if (ninf) {
        return Double.NEGATIVE_INFINITY;
      }

// just a simple overflow. return NaN
      return Double.NaN;
    }

    double hi = 0d;
    if (n > 0) {
      hi = summands[--n];
// sum_exact(ps, hi) from the top, stop when the sum becomes
// inexact.
      while (n > 0) {
        final double x = hi;
        final double y = summands[--n];
        hi = x + y;
        final double yr = hi - x;
        lo = y - yr;
        if (lo != 0d) {
          break;
        }
      }
// Make half-even rounding work across multiple partials. Needed
// so that sum([1e-16, 1, 1e16]) will round-up the last digit to
// two instead of down to zero (the 1e-16 makes the 1 slightly
// closer to two). With a potential 1 ULP rounding error
// fixed-up, math.fsum() can guarantee commutativity.
      if ((n > 0) && (((lo < 0d) && (summands[n - 1] < 0d)) || //
          ((lo > 0d) && (summands[n - 1] > 0d)))) {
        final double y = lo * 2d;
        final double x = hi + y;
        final double yr = x - hi;
        if (y == yr) {
          hi = x;
        }
      }
    }
    return hi;
  }

  /**
   * Compute the mean and the standard deviation of a data array
   * in one go
   *
   * @param values
   *          the values
   * @return an array containing two number objects, first the
   *         one with the mean, then the one with the standard
   *         deviation
   */
  public static Number[]
      sampleMeanAndStandardDeviation(final double[] values) {
    // handle simple cases
    if (values.length <= 0) {
      throw new IllegalArgumentException(
          "need at least one value."); //$NON-NLS-1$
    }

    allSame: { // are all values the same?
      final double l1 = values[0];
      for (final double v : values) {
        if (!Double.isFinite(v)) {
          throw new IllegalArgumentException(Double.toString(v));
        }
        if (v != l1) {
          break allSame;
        }
      }

      return new Number[] { Statistics.doubleToNumber(l1),
          Long.valueOf(0) };
    }

    // ok, no trivial case
    // no, they are not
    final Number sum = Statistics.sum(values);
    final Number mean =
        Statistics.divideExact(sum, values.length);
    final Number sumOfSquares =
        Statistics.sum(values, l -> l * l);

    // ok, we got some exact sums and exact sums of squares
    final Number sumSquared;
    if (sum instanceof Long) {
      final BigInteger sumBI =
          BigInteger.valueOf(sum.longValue());
      sumSquared =
          Statistics.simplifyInteger(sumBI.multiply(sumBI));
    } else {
      final double d = sum.doubleValue();
      sumSquared = Statistics.doubleToNumber(d * d);
    }

    final Number sumSquaredOverN =
        Statistics.divideExact(sumSquared, values.length);

    final double sd =
        Math.sqrt(Statistics.divideExact(
            Statistics.doubleToNumber(sumOfSquares.doubleValue()
                - sumSquaredOverN.doubleValue()),
            values.length - 1).doubleValue());

    if ((!Double.isFinite(sd)) || (sd < 0d)) {
      throw new ArithmeticException(
          "standard deviation must be positive and finite, but is " //$NON-NLS-1$
              + sd);
    }

    return new Number[] { mean, Statistics.doubleToNumber(sd) };
  }

  /** forbidden */
  private Statistics() {
    throw new UnsupportedOperationException();
  }
}

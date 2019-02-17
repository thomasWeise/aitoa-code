package aitoa.examples.jssp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/** Print the solution space sizes for the JSSP instances */
public class JSSPSolutionSpaceSize {

  /**
   * compute the factorial
   *
   * @param input
   *          the input value
   * @return the factorial
   */
  static final BigInteger factorial(final BigInteger input) {
    BigInteger result = BigInteger.ONE;
    BigInteger n = input;

    while (!n.equals(BigInteger.ZERO)) {
      result = result.multiply(n);
      n = n.subtract(BigInteger.ONE);
    }

    return result;
  }

  /**
   * compute the search space size as string
   *
   * @param m
   *          the m
   * @param n
   *          the n
   * @return the size
   */
  static final BigInteger solutionSpaceSizeUpper(final int m,
      final int n) {
    return factorial(BigInteger.valueOf(n)).pow(m);
  }

  /**
   * the bounds, computed via
   * {@link JSSPSolutionSpaceSizeEnumerate#main(String[])}
   */
  private static final long[][] BOUNDS = new long[][] { { 1L, 1L, 1L },
      { 2L, 1L, 1L }, { 1L, 2L, 2L }, { 3L, 1L, 1L }, { 1L, 3L, 6L },
      { 2L, 2L, 3L }, { 4L, 1L, 1L }, { 1L, 4L, 24L }, { 5L, 1L, 1L },
      { 1L, 5L, 120L }, { 3L, 2L, 4L }, { 2L, 3L, 22L }, { 6L, 1L, 1L },
      { 1L, 6L, 720L }, { 7L, 1L, 1L }, { 1L, 7L, 5040L }, { 4L, 2L, 5L },
      { 2L, 4L, 244L }, { 8L, 1L, 1L }, { 1L, 8L, 40320L },
      { 3L, 3L, 63L }, { 9L, 1L, 1L }, { 1L, 9L, 362880L }, { 5L, 2L, 6L },
      { 2L, 5L, 4548L }, { 10L, 1L, 1L }, { 1L, 10L, 3628800L },
      { 4L, 3L, 147L }, { 3L, 4L, 1630L }, { 6L, 2L, 7L },
      { 2L, 6L, 108828L }, { 11L, 1L, 1L }, { 1L, 11L, 39916800L },
      { 12L, 1L, 1L }, { 1L, 12L, 479001600L }, { 7L, 2L, 8L },
      { 2L, 7L, 3771792L }, { 5L, 3L, 317L }, { 3L, 5L, 91461L },
      { 13L, 1L, 1L }, { 1L, 13L, 6227020800L }, };

  /**
   * compute the search space size as string
   *
   * @param m
   *          the m
   * @param n
   *          the n
   * @return the size
   */
  static final BigInteger solutionSpaceSizeLower(final int m,
      final int n) {
    for (long[] bound : BOUNDS) {
      if (bound[0] == m) {
        if (bound[1] == n) {
          return (BigInteger.valueOf(bound[2]));
        }
      }
    }
    return null;
  }

  /**
   * format a big integer to a string
   *
   * @param v
   *          the big integer
   * @return the value
   */
  static final String toString(final BigInteger v) {
    if (v == null) {
      return "";//$NON-NLS-1$
    }
    final String s = v.toString();
    final StringBuilder sb = new StringBuilder();

    final int length = s.length();

    if (length > 15) {
      final BigDecimal bd = new BigDecimal(v);
      final String vv[] = new DecimalFormat("0.000E00")//$NON-NLS-1$
          .format(bd).split("E"); //$NON-NLS-1$

      return ("$\\approx$&nbsp;" + //$NON-NLS-1$
          vv[0] + "*10^" + //$NON-NLS-1$
          Integer.parseInt(vv[1]) + "^");//$NON-NLS-1$
    }

    for (int i = length, j = 0; (--i) >= 0;) {
      sb.insert(0, s.charAt(i));
      if (((++j) % 3) == 0) {
        if (j < length) {
          sb.insert(0, '\'');
        }
      }
    }
    return sb.toString();
  }

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */

  public static final void main(final String[] args) {
    final ArrayList<String> printFor = new ArrayList<>();
    printFor.add("demo"); //$NON-NLS-1$
    printFor.addAll(Arrays.asList(JSSPExperiment.INSTANCES));

    System.out.println(
        "|name|$\\jsspJobs$|$\\jsspMachines$|$\\lowerBound(\\#\\textnormal{feasible})$|$\\left|\\solutionSpace\\right|$|"); //$NON-NLS-1$
    System.out.println("|:--|--:|--:|--:|--:|"); //$NON-NLS-1$
    for (final int n : new int[] { 2, 3, 4, 5 }) {
      for (final int m : new int[] { 2, 3, 4, 5 }) {
        System.out.print('|');
        System.out.print('|');
        System.out.print(n);
        System.out.print('|');
        System.out.print(m);
        System.out.print('|');
        System.out.print(JSSPSolutionSpaceSize
            .toString(JSSPSolutionSpaceSize.solutionSpaceSizeLower(m, n)));
        System.out.print('|');
        System.out.print(JSSPSolutionSpaceSize
            .toString(JSSPSolutionSpaceSize.solutionSpaceSizeUpper(m, n)));
        System.out.println();
      }
    }

    for (final String s : printFor) {
      final JSSPInstance inst = new JSSPInstance(s);

      System.out.print(inst.id);
      System.out.print('|');
      System.out.print(inst.n);
      System.out.print('|');
      System.out.print(inst.m);
      System.out.print('|');
      System.out.print(JSSPSolutionSpaceSize.toString(
          JSSPSolutionSpaceSize.solutionSpaceSizeLower(inst.m, inst.n)));
      System.out.print('|');
      System.out.print(JSSPSolutionSpaceSize.toString(
          JSSPSolutionSpaceSize.solutionSpaceSizeUpper(inst.m, inst.n)));
      System.out.println();
    }
  }
}

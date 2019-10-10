package aitoa.bookExamples.jssp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import aitoa.TestTools;
import aitoa.bookExamples.Tools;
import aitoa.examples.jssp.JSSPExperiment;
import aitoa.examples.jssp.JSSPInstance;

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
    return JSSPSolutionSpaceSize.factorial(BigInteger.valueOf(n))
        .pow(m);
  }

  /**
   * the bounds, computed via
   * {@link JSSPSolutionSpaceSizeEnumerate#main(String[])}
   */
  private static final long[][] BOUNDS = new long[][] {
      { 1L, 1L, 1L }, { 2L, 1L, 1L }, { 1L, 2L, 2L },
      { 3L, 1L, 1L }, { 1L, 3L, 6L }, { 2L, 2L, 3L },
      { 4L, 1L, 1L }, { 1L, 4L, 24L }, { 5L, 1L, 1L },
      { 1L, 5L, 120L }, { 3L, 2L, 4L }, { 2L, 3L, 22L },
      { 6L, 1L, 1L }, { 1L, 6L, 720L }, { 7L, 1L, 1L },
      { 1L, 7L, 5040L }, { 4L, 2L, 5L }, { 2L, 4L, 244L },
      { 8L, 1L, 1L }, { 1L, 8L, 40320L }, { 3L, 3L, 63L },
      { 9L, 1L, 1L }, { 1L, 9L, 362880L }, { 5L, 2L, 6L },
      { 2L, 5L, 4548L }, { 10L, 1L, 1L }, { 1L, 10L, 3628800L },
      { 4L, 3L, 147L }, { 3L, 4L, 1630L }, { 6L, 2L, 7L },
      { 2L, 6L, 108828L }, { 11L, 1L, 1L },
      { 1L, 11L, 39916800L }, { 12L, 1L, 1L },
      { 1L, 12L, 479001600L }, { 7L, 2L, 8L },
      { 2L, 7L, 3771792L }, { 5L, 3L, 317L }, { 3L, 5L, 91461L },
      { 13L, 1L, 1L }, { 1L, 13L, 6227020800L },
      { 4L, 4L, 7451L }, { 2L, 8L, 156073536L }, };

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
    TestTools.assertGreater(m, 0);
    TestTools.assertGreater(n, 0);
    if (n == 1) {
      return BigInteger.valueOf(1);
    }
    if (m == 1) {
      return JSSPSolutionSpaceSize
          .factorial(BigInteger.valueOf(n));
    }
    if (n == 2) {
      return BigInteger.valueOf(m + 1);
    }
    for (final long[] bound : JSSPSolutionSpaceSize.BOUNDS) {
      if (bound[0] == m) {
        if (bound[1] == n) {
          return (BigInteger.valueOf(bound[2]));
        }
      }
    }
    return null;
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
        Tools.printLongNumber(
            JSSPSolutionSpaceSize.solutionSpaceSizeLower(m, n),
            15);
        System.out.print('|');
        Tools.printLongNumber(
            JSSPSolutionSpaceSize.solutionSpaceSizeUpper(m, n),
            15);
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
      Tools.printLongNumber(JSSPSolutionSpaceSize
          .solutionSpaceSizeLower(inst.m, inst.n), 15);
      System.out.print('|');
      Tools.printLongNumber(JSSPSolutionSpaceSize
          .solutionSpaceSizeUpper(inst.m, inst.n), 15);
      System.out.println();
    }
  }
}

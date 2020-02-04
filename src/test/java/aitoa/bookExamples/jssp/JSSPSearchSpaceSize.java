package aitoa.bookExamples.jssp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;

import aitoa.bookExamples.Tools;
import aitoa.examples.jssp.EJSSPExperimentStage;
import aitoa.examples.jssp.JSSPInstance;

/** Print the search space sizes for the JSSP instances */
public class JSSPSearchSpaceSize {

  /**
   * compute the search space size as string
   *
   * @param m
   *          the m
   * @param n
   *          the n
   * @return the size
   */
  static final BigInteger searchSpaceSize(final int m,
      final int n) {
    final BigInteger mm = BigInteger.valueOf(m);
    final BigInteger nn = BigInteger.valueOf(n);

    final BigInteger upper =
        JSSPSolutionSpaceSize.factorial(mm.multiply(nn));
    final BigInteger lower =
        JSSPSolutionSpaceSize.factorial(mm).pow(n);
    Assert.assertEquals(upper.mod(lower), BigInteger.ZERO);
    return upper.divide(lower);
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
    printFor.addAll(//
        Arrays.asList(EJSSPExperimentStage.INSTANCES));

    System.out.println(
        "|name|$\\jsspJobs$|$\\jsspMachines$|$\\left|\\solutionSpace\\right|$|$\\left|\\searchSpace\\right|$|"); //$NON-NLS-1$
    System.out.println("|:--|--:|--:|--:|--:|"); //$NON-NLS-1$
    for (final int n : new int[] { 3, 4, 5 }) {
      for (final int m : new int[] { 2, 3, 4, 5 }) {
        System.out.print('|');
        System.out.print('|');
        System.out.print(n);
        System.out.print('|');
        System.out.print(m);
        System.out.print('|');
        Tools.printLongNumber(
            JSSPSolutionSpaceSize.solutionSpaceSizeUpper(m, n),
            15);
        System.out.print('|');
        Tools.printLongNumber(
            JSSPSearchSpaceSize.searchSpaceSize(m, n), 15);
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
          .solutionSpaceSizeUpper(inst.m, inst.n), 15);
      System.out.print('|');

      Tools.printLongNumber(
          JSSPSearchSpaceSize.searchSpaceSize(inst.m, inst.n),
          15);
      System.out.println();
    }
  }
}

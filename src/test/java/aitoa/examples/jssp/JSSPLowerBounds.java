package aitoa.examples.jssp;

import java.util.ArrayList;
import java.util.Arrays;

/** Print the lower bounds for the JSSP instances */
public class JSSPLowerBounds {
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
        "|name|$\\jsspJobsn$|$\\jsspMachines$|$\\lowerBound{\\objF}$|"); //$NON-NLS-1$
    System.out.println("|:--|--:|--:|--:|"); //$NON-NLS-1$
    for (final String s : printFor) {
      final JSSPInstance inst = new JSSPInstance(s);

      System.out.print(inst.id);
      System.out.print('|');
      System.out.print(inst.n);
      System.out.print('|');
      System.out.print(inst.m);
      System.out.print('|');
      System.out.print(
          new JSSPMakespanObjectiveFunction(inst).lowerBound());
      System.out.println();
    }
  }
}

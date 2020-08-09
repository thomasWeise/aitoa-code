package aitoa.bookExamples.jssp;

import java.io.IOException;
import java.util.Random;

import aitoa.TestTools;
import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPRepresentationMapping;
import aitoa.examples.jssp.JSSPSearchSpace;
import aitoa.examples.jssp.JSSPSolutionSpace;

/** Create the demo gantt chart */
public final class JSSPDemoGanttChart {

  /**
   * The main routine
   *
   * @param args
   *          ignore
   * @throws IOException
   *           should not
   */
  public static void main(final String[] args)
      throws IOException {

    final JSSPInstance instance = new JSSPInstance("demo"); //$NON-NLS-1$
    final JSSPCandidateSolution y =
        new JSSPCandidateSolution(instance.m, instance.n);
    final JSSPNullaryOperator op =
        new JSSPNullaryOperator(instance);
    final JSSPRepresentationMapping map =
        new JSSPRepresentationMapping(instance);
    final JSSPSearchSpace X = new JSSPSearchSpace(instance);
    final Random random = new Random(2116447548726917664L);
    final JSSPMakespanObjectiveFunction f =
        new JSSPMakespanObjectiveFunction(instance);
    final int[] x = X.create();
    final JSSPSolutionSpace Y = new JSSPSolutionSpace(instance);

    op.apply(x, random);
    X.check(x);
    map.map(random, x, y);
    Y.check(y);
    final int z = ((int) (Math.round(f.evaluate(y))));
    TestTools.assertGreaterOrEqual(z, 180);

    X.print(x, System.out);
    System.out.println();
    System.out.println();
    Y.print(y, System.out);
    System.out.println();
    System.out.println();
    System.out.println("f(y): " + z); //$NON-NLS-1$
  }

  /** forbidden */
  private JSSPDemoGanttChart() {
    throw new UnsupportedOperationException();
  }
}

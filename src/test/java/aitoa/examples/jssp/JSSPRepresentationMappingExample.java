package aitoa.examples.jssp;

/** an example for the JSSP representation */
public class JSSPRepresentationMappingExample {

  /** the example instance */
  public static final JSSPInstance INSTANCE =
      new JSSPInstance("demo"); //$NON-NLS-1$

  /** the point in the search space */
  public static final int[] POINT = { 0, 2, 1, 0, 3, 1, 0, 1, 2,
      3, 2, 1, 1, 2, 3, 0, 2, 0, 3, 3 };

  /** the representation mapping */
  public static final JSSPRepresentationMapping MAPPING =
      new JSSPRepresentationMapping(
          JSSPRepresentationMappingExample.INSTANCE);

  /** the resulting solution */
  public static final JSSPCandidateSolution SOLUTION;

  static {
    SOLUTION = new JSSPCandidateSolution(
        JSSPRepresentationMappingExample.INSTANCE.m,
        JSSPRepresentationMappingExample.INSTANCE.n);
    JSSPRepresentationMappingExample.MAPPING.map(
        JSSPRepresentationMappingExample.POINT,
        JSSPRepresentationMappingExample.SOLUTION);
  }

  /** the objective function */
  public static final JSSPMakespanObjectiveFunction F =
      new JSSPMakespanObjectiveFunction();

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */
  public static final void main(final String[] args) {
    JSSPRepresentationMappingExample.SOLUTION
        .printGanttData(System.out);
    System.out.println();
    System.out.print("# makespan: "); //$NON-NLS-1$
    System.out.println(
        Math.round(JSSPRepresentationMappingExample.F.evaluate(
            JSSPRepresentationMappingExample.SOLUTION)));
  }
}

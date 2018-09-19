// start relevant
package aitoa.examples.jssp;

import aitoa.structure.IObjectiveFunction;

/**
 * The objective function for a candidate solution to the Job
 * Shop Scheduling problem: minimize the makespan
 */
public final class JSSPMakespanObjectiveFunction
    implements IObjectiveFunction<JSSPCandidateSolution> {

// end relevant
  /** create */
  public JSSPMakespanObjectiveFunction() {
    super();
  }

// start relevant
  /**
   * Compute the makespan of a Gantt chart
   *
   * @param x
   *          the Gantt chart / candidate solution
   */
  @Override
  public final double evaluate(final JSSPCandidateSolution x) {
    int makespan = 0;
    // look at the schedule for each machine
    for (final int[] machine : x.schedule) {
      // the end time of the last job on the machine is the last
      // number in the array
      final int end = machine[machine.length - 1];
      if (end > makespan) {
        makespan = end; // remember biggest end time
      }
    }
    return makespan;
  }

}
// end relevant
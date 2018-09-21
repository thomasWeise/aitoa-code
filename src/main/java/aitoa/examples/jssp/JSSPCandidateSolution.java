// start relevant
package aitoa.examples.jssp;

import java.util.Objects;

/**
 * A candidate solution for the JSSP is a complete Gantt diagram
 */
public final class JSSPCandidateSolution {

  /**
   * the schedule: for each machine, the sequence of jobs. for
   * each job at a machine, three numbers: the job id, the start
   * time, end the end time
   */
  public final int[][] schedule;
// end relevant

  /**
   * create a blank candidate solution
   *
   * @param m
   *          the number of machines
   * @param n
   *          the number of jobs
   */
  public JSSPCandidateSolution(final int m, final int n) {
    super();
// allocate one array for each of the m machine, big enough to
// hold the IDs of all n jobs and their start- and end-times at
// the machine
    this.schedule = new int[m][3 * n];
  }

  /**
   * create a candidate solution initialized from an array
   *
   * @param _schedule
   *          the array
   */
  public JSSPCandidateSolution(final int[][] _schedule) {
    super();
    this.schedule = Objects.requireNonNull(_schedule);
  }

// start relevant
}
// end relevant

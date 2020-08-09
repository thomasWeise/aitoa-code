package aitoa.examples.jssp;

import java.util.Objects;

/**
 * A candidate solution for the JSSP is a complete Gantt diagram
 */
// start relevant
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
   * @param pM
   *          the number of machines
   * @param pN
   *          the number of jobs
   */
  public JSSPCandidateSolution(final int pM, final int pN) {
    super();
// allocate one array for each of the m machine, big enough to
// hold the IDs of all n jobs and their start- and end-times at
// the machine
    this.schedule = new int[pM][3 * pN];
  }

  /**
   * create a candidate solution initialized from an array
   *
   * @param pSchedule
   *          the array
   */
  public JSSPCandidateSolution(final int[][] pSchedule) {
    super();
    this.schedule = Objects.requireNonNull(pSchedule);
  }

// start relevant
}
// end relevant

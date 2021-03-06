package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Random;

import aitoa.structure.IRepresentationMapping;

/**
 * The representation mapping: translate a one-dimensional
 * integer array to a candidate solution for the JSSP.
 */
// start relevant
public final class JSSPRepresentationMapping implements
    IRepresentationMapping<int[], JSSPCandidateSolution> {
// end relevant

  /** the current time at a given machine */
  final int[] mMachineTime;
  /** the current step index at a given machine */
  final int[] mMachineState;
  /** the step index of the current job */
  final int[] mJobState;
  /** the time of the current job */
  final int[] mJobTime;

  /**
   * the instance data: for each job, the sequence of machines
   * and times
   */
  private final int[][] mJobs;

  /**
   * create the representation
   *
   * @param pInstance
   *          the problem instance
   */
  public JSSPRepresentationMapping(
      final JSSPInstance pInstance) {
    super();
    this.mJobs = pInstance.jobs;
    this.mJobState = new int[pInstance.n];
    this.mJobTime = new int[pInstance.n];
    this.mMachineTime = new int[pInstance.m];
    this.mMachineState = new int[pInstance.m];
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ("jssp:int[]-to-Gantt"); //$NON-NLS-1$
  }

// start relevant
  /**
   * Map a point {@code x} from the search space, i.e., an
   * {@code int[]} representing the sub-job priorities, to a
   * candidate solution {@code y} in the solution space, here a
   * Gantt chart.
   *
   * @param random
   *          the random number generator (here: ignored)
   * @param x
   *          the point in the search space
   * @param y
   *          the solution record, i.e., the Gantt chart
   */
  @Override
  public void map(final Random random, final int[] x,
      final JSSPCandidateSolution y) {
// create variables machineState, machineTime of length m and
// jobState, jobTime of length n, filled with 0 [omitted brevity]
// end relevant
    final int[] machineState = this.mMachineState;
    final int[] machineTime = this.mMachineTime;
    final int[] jobState = this.mJobState;
    final int[] jobTime = this.mJobTime;
    Arrays.fill(machineState, 0);
    Arrays.fill(jobState, 0);
    Arrays.fill(machineTime, 0);
    Arrays.fill(jobTime, 0);
// start relevant
// iterate over the jobs in the solution
    for (final int nextJob : x) {
// get the definition of the steps that we need to take for
// nextJob from the instance data stored in this.m_jobs
      final int[] jobSteps = this.mJobs[nextJob];
// jobState tells us the index in this list for the next step to
// do, but since the list contains machine/time pairs, we
// multiply by 2 (by left-shifting by 1)
      final int jobStep = (jobState[nextJob]++) << 1;

// so we know the machine where the job needs to go next
      final int machine = jobSteps[jobStep]; // get machine

// start time is maximum of the next time when the machine
// becomes idle and the time we have already spent on the job
      final int start =
          Math.max(machineTime[machine], jobTime[nextJob]);
// the end time is simply the start time plus the time the job
// needs to spend on the machine
      final int end = start + jobSteps[jobStep + 1]; // end time
// it holds for both the machine (it will become idle after end)
// and the job (it can go to the next station after end)
      jobTime[nextJob] = machineTime[machine] = end;

// update the schedule with the data we have just computed
      final int[] schedule = y.schedule[machine];
      schedule[machineState[machine]++] = nextJob;
      schedule[machineState[machine]++] = start;
      schedule[machineState[machine]++] = end;
    }
  }
}
// end relevant

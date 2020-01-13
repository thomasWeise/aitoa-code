package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Objects;

import aitoa.structure.IObjectiveFunction;

/**
 * The makespan objective function working directly on the
 * order-based representation.
 */
// start relevant
public final class JSSPMakespanObjectiveFunction2
    implements IObjectiveFunction<int[]> {
// end relevant
  /** the instance */
  public final JSSPInstance instance;

  /** the current time at a given machine */
  private final int[] m_machineTime;
  /** the current step index at a given machine */
  private final int[] m_machineState;
  /** the step index of the current job */
  private final int[] m_jobState;
  /** the time of the current job */
  private final int[] m_jobTime;

  /**
   * the instance data: for each job, the sequence of machines
   * and times
   */
  private final int[][] m_jobs;

  /**
   * create the representation
   *
   * @param _instance
   *          the problem instance
   */
  public JSSPMakespanObjectiveFunction2(
      final JSSPInstance _instance) {
    super();
    this.instance = Objects.requireNonNull(_instance);
    this.m_jobs = _instance.jobs;
    this.m_jobState = new int[_instance.n];
    this.m_jobTime = new int[_instance.n];
    this.m_machineTime = new int[_instance.m];
    this.m_machineState = new int[_instance.m];
  }

  /**
   * create
   *
   * @param _instance
   *          the instance
   */
  public JSSPMakespanObjectiveFunction2(final String _instance) {
    this(new JSSPInstance(_instance));
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return this.getClass().getSimpleName();
  }

// start relevant
  /** {@inheritDoc} */
  @Override
  public double evaluate(final int[] y) {
// create variables machineState, machineTime of length m and
// jobState, jobTime of length n, filled with 0 [omitted brevity]
// end relevant
    final int[] machineState = this.m_machineState;
    final int[] machineTime = this.m_machineTime;
    final int[] jobState = this.m_jobState;
    final int[] jobTime = this.m_jobTime;
    Arrays.fill(machineState, 0);
    Arrays.fill(jobState, 0);
    Arrays.fill(machineTime, 0);
    Arrays.fill(jobTime, 0);
// start relevant
// iterate over the jobs in the solution

    int end = -1;
    for (final int nextJob : y) {
// jobState tells us the index in this list for the next step to
// do, but since the list contains machine/time pairs, we
// multiply by 2 (by left-shifting by 1)
      final int jobStep = (jobState[nextJob]++) << 1;
// get the definition of the steps that we need to take for
// nextJob from the instance data stored in this.m_jobs
      final int[] jobSteps = this.m_jobs[nextJob];
// so we know the machine where the job needs to go next
      final int machine = jobSteps[jobStep]; // get machine
// start time is maximum of the next time when the machine
// becomes idle and the time we have already spent on the job
      final int start =
          Math.max(machineTime[machine], jobTime[nextJob]);
// the end time is simply the start time plus the time the job
// needs to spend on the machine
      end = start + jobSteps[jobStep + 1]; // end time
// it holds for both the machine (it will become idle after end)
// and the job (it can go to the next station after end)
      jobTime[nextJob] = machineTime[machine] = end;
    }

// compute the makespan
    for (final int v : (machineTime.length > jobTime.length)
        ? jobTime : machineTime) {
      if (v > end) {
        end = v;
      }
    }
    return end;
  }
}
// end relevant

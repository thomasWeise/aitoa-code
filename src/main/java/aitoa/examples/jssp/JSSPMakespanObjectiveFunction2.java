package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Objects;

import aitoa.structure.IObjectiveFunction;

/**
 * The makespan objective function working directly on the
 * order-based representation.
 */
public final class JSSPMakespanObjectiveFunction2
    implements IObjectiveFunction<int[]> {
  /** the instance */
  public final JSSPInstance instance;

  /** the current time at a given machine */
  private final int[] mMachineTime;
  /** the current step index at a given machine */
  private final int[] mMachineState;
  /** the step index of the current job */
  private final int[] mJobState;
  /** the time of the current job */
  private final int[] mJobTime;

  /**
   * the instance data: for each job, the sequence of machines
   * and times
   */
  private final int[][] mJobs;

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
    this.mJobs = _instance.jobs;
    this.mJobState = new int[_instance.n];
    this.mJobTime = new int[_instance.n];
    this.mMachineTime = new int[_instance.m];
    this.mMachineState = new int[_instance.m];
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
  public String toString() {
    return this.instance.toString();
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final int[] y) {
    final int[] machineState = this.mMachineState;
    final int[] machineTime = this.mMachineTime;
    final int[] jobState = this.mJobState;
    final int[] jobTime = this.mJobTime;
    Arrays.fill(machineState, 0);
    Arrays.fill(jobState, 0);
    Arrays.fill(machineTime, 0);
    Arrays.fill(jobTime, 0);
    
// iterate over the jobs in the solution
    int end = -1;
    for (final int nextJob : y) {
// jobState tells us the index in this list for the next step to
// do, but since the list contains machine/time pairs, we
// multiply by 2 (by left-shifting by 1)
      final int jobStep = (jobState[nextJob]++) << 1;
// get the definition of the steps that we need to take for
// nextJob from the instance data stored in this.m_jobs
      final int[] jobSteps = this.mJobs[nextJob];
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

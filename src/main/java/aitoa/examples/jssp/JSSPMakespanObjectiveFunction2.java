package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Objects;

import aitoa.structure.IObjectiveFunction;

/**
 * A variant of the {@linkplain JSSPMakespanObjectiveFunction
 * makespan objective function} working directly on the
 * order-based representation, i.e., {@code int[]}.
 */
public final class JSSPMakespanObjectiveFunction2
    implements IObjectiveFunction<int[]> {
  /** the instance */
  public final JSSPInstance instance;

  /** the current time at a given machine */
  private final int[] mMachineTime;
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
   * @param pinstance
   *          the problem instance
   */
  public JSSPMakespanObjectiveFunction2(
      final JSSPInstance pinstance) {
    super();
    this.instance = Objects.requireNonNull(pinstance);
    this.mJobs = pinstance.jobs;
    this.mJobState = new int[pinstance.n];
    this.mJobTime = new int[pinstance.n];
    this.mMachineTime = new int[pinstance.m];
  }

  /**
   * create
   *
   * @param pinstance
   *          the instance
   */
  public JSSPMakespanObjectiveFunction2(final String pinstance) {
    this(new JSSPInstance(pinstance));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.instance.toString();
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final int[] y) {
    final int[] machineTime = this.mMachineTime;
    final int[] jobState = this.mJobState;
    final int[] jobTime = this.mJobTime;
    Arrays.fill(jobState, 0);
    Arrays.fill(machineTime, 0);
    Arrays.fill(jobTime, 0);

// iterate over the jobs in the solution
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
// The start time is maximum of the next time when the machine
// becomes idle and the time we have already spent on the job.
// The end time is simply the start time plus the time the job
// needs to spend on the machine.
// It holds for both the machine (it will become idle after end)
// and the job (it can go to the next station after end)
      jobTime[nextJob] = machineTime[machine] = //
          Math.max(machineTime[machine], jobTime[nextJob]) //
              + jobSteps[jobStep + 1]; // end time
    }

// compute the makespan
    int end = -1;
    for (final int v : (machineTime.length > jobTime.length)
        ? jobTime : machineTime) {
      if (v > end) {
        end = v;
      }
    }
    return end;
  }

  /**
   * Compute the lower bound of the objective value. See E. D.
   * Taillard. Benchmarks for basic scheduling problems. European
   * Journal of Operational Research, 64.2: 278-285, 1993. doi:
   * 10.1016/0377-2217(93)90182-M
   *
   * @return the lower bound
   */
  @Override
  public double lowerBound() {
    return JSSPMakespanObjectiveFunction.lowerBound(//
        this.instance);
  }

  /**
   * Compute the upper bound of the instance in a very sloppy
   * way. This is just a placeholder for now. The idea is that I
   * can use this in unit tests to check whether results are
   * sane.
   */
  @Override
  public double upperBound() {
    return JSSPMakespanObjectiveFunction.upperBound(//
        this.instance);
  }

}

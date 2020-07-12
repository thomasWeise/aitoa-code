package aitoa.examples.jssp.aco;

import java.util.Arrays;
import java.util.Random;

import aitoa.algorithms.aco.PACOModelAge;
import aitoa.examples.jssp.JSSPInstance;

/**
 * A Population-based Ant Colony Optimization (PACO) model with
 * age-based pruning for the JSSP. This model samples solutions
 * and performs a GPM at the same time.
 */
public final class JSSPPACOModelAge
    extends PACOModelAge<JSSPACOIndividual> {
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

  /** the number of machines */
  private final int m_m;

  /** the current makespan */
  private int m_currentMakespan;

  /**
   * create the representation
   *
   * @param instance
   *          the problem instance
   * @param _K
   *          the size of the population
   * @param _q0
   *          the fraction of edges to be chosen directly based
   *          on the heuristic
   * @param _beta
   *          the power to be applied to the heuristic value
   * @param _tauMax
   *          the maximum pheromone that can be assigned to any
   *          edge
   */
  public JSSPPACOModelAge(final JSSPInstance instance,
      final int _K, final double _q0, final double _beta,
      final double _tauMax) {
    super(instance.m * instance.n, _K, _q0, _beta, _tauMax);
    this.m_jobs = instance.jobs;
    this.m_jobState = new int[instance.n];
    this.m_jobTime = new int[instance.n];
    this.m_machineTime = new int[instance.m];
    this.m_machineState = new int[instance.m];
    this.m_m = instance.m;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "jssp" + super.toString(); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected int[] permutationFromX(final JSSPACOIndividual x) {
    return x.permutation;
  }

  /**
   * Map a point {@code x} from the search space, i.e., an
   * {@code int[]} representing the sub-job priorities, to a
   * candidate solution {@code y} in the solution space, here a
   * Gantt chart.
   *
   * @param random
   *          the random number generator (here: ignored)
   * @param dest
   *          the point in the search space
   */
  @Override
  public void apply(final JSSPACOIndividual dest,
      final Random random) {
    Arrays.fill(this.m_machineState, 0);
    Arrays.fill(this.m_machineTime, 0);
    Arrays.fill(this.m_jobState, 0);
    Arrays.fill(this.m_jobTime, 0);
    this.m_currentMakespan = 0;
    super.apply(dest, random);
    dest.makespan = this.m_currentMakespan;
  }

  /**
   * The cost for appending a certain job is how much it will
   * increase the makespan and whether it causes a machine to
   * idle plus 1.
   *
   * @param value
   *          the permutation index
   * @param x
   *          the solution structure
   * @return the cost
   */
  @Override
  protected double getCostOfAppending(final int value,
      final JSSPACOIndividual x) {
    final int nextJob = value / this.m_m;

// get the definition of the steps that we need to take for
// nextJob from the instance data stored in this.m_jobs
    final int[] jobSteps = this.m_jobs[nextJob];

// jobState tells us the index of the next step to do.
    int jobStep = this.m_jobState[nextJob];
    if (jobStep != (value % this.m_m)) {
// If the permutation suggests jumping over job steps, we
// discourage this in the hope of getting better permutations.
      return Integer.MAX_VALUE;
    }

// Since the list contains machine/time pairs, we multiply by 2
// (by left-shifting by 1).
    jobStep <<= 1;

// so we know the machine where the job needs to go next
    final int machine = jobSteps[jobStep]; // get machine

// The start time is maximum of the next time when the machine
// becomes idle and the time we have already spent on the job.
// The end time is simply the start time plus the time the job
// needs to spend on the machine.
    final int machineStart = this.m_machineTime[machine];
    final int start =
        Math.max(machineStart, this.m_jobTime[nextJob]);
    final int end = start + jobSteps[jobStep + 1];

// Compute how much this add to the makespan and machine idle
// time (then add 1)
    return 2d //
        + Math.max(0, end - this.m_currentMakespan) // makespan
        - (1d / (1d + (start - machineStart))); // idle time
  }

  /** {@inheritDoc} */
  @Override
  protected void append(final int value,
      final JSSPACOIndividual dest) {
    final int[] machineState = this.m_machineState;
    final int[] machineTime = this.m_machineTime;
    final int[] jobTime = this.m_jobTime;

    final int nextJob = value / this.m_m;
// get the definition of the steps that we need to take for
// nextJob from the instance data stored in this.m_jobs
    final int[] jobSteps = this.m_jobs[nextJob];

// jobState tells us the index in this list for the next step to
// do, but since the list contains machine/time pairs, we
// multiply by 2 (by left-shifting by 1)
    final int jobStep = (this.m_jobState[nextJob]++) << 1;

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
    final int[] schedule = dest.solution.schedule[machine];
    schedule[machineState[machine]++] = nextJob;
    schedule[machineState[machine]++] = start;
    schedule[machineState[machine]++] = end;

// update the current makespan
    if (end > this.m_currentMakespan) {
      this.m_currentMakespan = end;
    }
  }
}
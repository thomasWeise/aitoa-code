package aitoa.examples.jssp.aco;

import java.util.Arrays;
import java.util.Random;

import aitoa.algorithms.PACOModelAge;
import aitoa.examples.jssp.JSSPInstance;

/**
 * A Population-based Ant Colony Optimization (PACO) model with
 * age-based pruning for the JSSP. This model samples solutions
 * and performs a GPM at the same time.
 * <p>
 * We treat the solutions to the JSSP as special permutations.
 * The length {@code L} of these permutations is {@code m*n},
 * where {@code m} is the number of machines and {@code n} is the
 * number of jobs. The value {@code i} then stands for the job
 * {@code i/m} and the {@code i%m}<sup>th</sup> operation of that
 * job. These values form the nodes in a "network" and the ants
 * can walk from node to node. When an ant arrives at a node
 * {@code i}, the corresponding operation of the corresponding
 * job is added to the solution, i.e., {@code i} is appended to
 * the permutation and the {@code i%m}<sup>th</sup> operation of
 * job {@code i/m} is scheduled to its corresponding machine,
 * i.e., inserted into the Gantt chart.
 * <p>
 * This, of course, means that the "nodes" where an ant can go to
 * change. For instance, when an ant starts, it can only pick
 * among the <em>first operations</em> of any job, i.e., the
 * nodes {@code 0, m, 2m, 3m, ..., (n-1)m}. After one such first
 * operation of a job is done, the second operation of the
 * corresponding job becomes available, and so on. "Becomes
 * available" here means that it is added to the set of nodes in
 * the PACOModel. More generally: If an ant has picked a value
 * {@code i} among these available nodes, the operation
 * {@code i%m} of the corresponding job is done and node
 * {@code i} is removed from the network. If the job has more
 * operations, i.e., {@code i%m < m-1}, then the node {@code i+1}
 * will be added to the network, as it marks the next operation
 * (as then {@code i/m=(i+1)/m} and {@code 1+i%m = (i+1)%m}). and
 * so on. Of course, eventually all nodes will disappear from the
 * network, as the jobs are completed.
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
   * Add the first operation of each job to the node set
   *
   * @param random
   *          the random number generator
   */
  @Override
  protected void initNodeSet(final Random random) {
    for (int i = this.m_jobState.length; (--i) >= 0;) {
      this.m_nodes.add(i * this.m_m);
    }
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
// extract job id
    final int nextJob = value / this.m_m;
// get the definition of the steps that we need to take for
// nextJob from the instance data stored in this.m_jobs
    final int[] jobSteps = this.m_jobs[nextJob];
// jobState tells us the index of the next step to do.
    final int jobStep = this.m_jobState[nextJob] << 1;
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

    return (2 // ensure > 0
        + Math.max(end - this.m_currentMakespan, 0)) // makespan
        - (1d / ((start - machineStart) + 1));// idle time
  }

  /** {@inheritDoc} */
  @Override
  protected void append(final int value,
      final JSSPACOIndividual dest) {
    final int[] machineState = this.m_machineState;
    final int[] machineTime = this.m_machineTime;
    final int[] jobTime = this.m_jobTime;

    final int nextJob = value / this.m_m;
    int jobStep = value % this.m_m;
// get the definition of the steps that we need to take for
// nextJob from the instance data stored in this.m_jobs
    final int[] jobSteps = this.m_jobs[nextJob];

// jobState tells us the index in this list for the next step to
// do, but since the list contains machine/time pairs, we later
// multiply by 2 (by left-shifting by 1)
    if (jobStep != ((this.m_jobState[nextJob]++))) {
      throw new IllegalStateException("Invalid step" //$NON-NLS-1$
          + jobStep + " of job " + nextJob);//$NON-NLS-1$
    }
    if (jobStep < (this.m_m - 1)) {
      // make next job step available, if any
      this.m_nodes.add(value + 1);
    }
    jobStep <<= 1;

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
    jobTime[nextJob] = end;
    machineTime[machine] = end;

// update the schedule with the data we have just computed
    final int[] schedule = dest.solution.schedule[machine];
    schedule[machineState[machine]++] = nextJob;
    schedule[machineState[machine]++] = start;
    schedule[machineState[machine]++] = end;

    if (end > this.m_currentMakespan) {
      this.m_currentMakespan = end;// update the current makespan
    }
  }
}

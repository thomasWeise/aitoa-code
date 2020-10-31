package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.Objects;

import aitoa.structure.IObjectiveFunction;

/**
 * The makespan as objective function for a candidate solution to
 * the Job Shop Scheduling Problem, subject to minimization
 */
// start relevant
public final class JSSPMakespanObjectiveFunction
    implements IObjectiveFunction<JSSPCandidateSolution> {

// end relevant
  /** the JSSP instance */
  public final JSSPInstance instance;

  /**
   * create
   *
   * @param pInstance
   *          the instance
   */
  public JSSPMakespanObjectiveFunction(
      final JSSPInstance pInstance) {
    super();
    this.instance = Objects.requireNonNull(pInstance);
  }

  /**
   * create
   *
   * @param pInstance
   *          the instance
   */
  public JSSPMakespanObjectiveFunction(final String pInstance) {
    this(new JSSPInstance(pInstance));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.instance.toString();
  }

// start relevant
  /**
   * Compute the makespan of a Gantt chart
   *
   * @param y
   *          the Gantt chart / candidate solution
   */
  @Override
  public double evaluate(final JSSPCandidateSolution y) {
    int makespan = 0;
// look at the schedule for each machine
    for (final int[] machine : y.schedule) {
// the end time of the last job on the machine is the last number
// in the array, as array machine consists of "flattened" tuples
// of the form ((job, start, end), (job, start, end), ...)
      final int end = machine[machine.length - 1];
      if (end > makespan) {
        makespan = end; // remember biggest end time
      }
    }
    return makespan;
  }
// end relevant

  /**
   * Compute the lower bound of the objective value. See E. D.
   * Taillard. Benchmarks for basic scheduling problems. European
   * Journal of Operational Research, 64.2: 278-285, 1993. doi:
   * 10.1016/0377-2217(93)90182-M
   *
   * @param inst
   *          the instance
   * @return the lower bound
   */
  static int lowerBound(final JSSPInstance inst) {
    final int[] a = new int[inst.m]; // lb inactive time at start
    final int[] b = new int[inst.m]; // lb inactive time at end
    final int[] T = new int[inst.m]; // time of machine
    Arrays.fill(a, Integer.MAX_VALUE);
    Arrays.fill(b, Integer.MAX_VALUE);
// start lowerBound
// a, b: int[m] filled with MAX_VALUE, T: int[m] filled with 0
    int lowerBound = 0; // overall lower bound

    for (int n = inst.n; (--n) >= 0;) {
      final int[] job = inst.jobs[n];

// for each job, first compute the total job runtime
      int jobTimeTotal = 0; // total time
      for (int m = 1; m < job.length; m += 2) {
        jobTimeTotal += job[m];
      }
// lower bound of the makespan must be >= total job time
      lowerBound = Math.max(lowerBound, jobTimeTotal);

      // now compute machine values
      int jobTimeSoFar = 0;
      for (int m = 0; m < job.length;) {
        final int machine = job[m++];

// if the sub-job for machine m starts at jobTimeSoFar, the
// smallest machine start idle time cannot be bigger than that
        a[machine] = Math.min(a[machine], jobTimeSoFar);

        final int time = job[m++];
// add the sub-job execution time to the machine total time
        T[machine] += time;

        jobTimeSoFar += time;
// compute the remaining time of the job and check if this is
// less than the smallest-so-far machine end idle time
        b[machine] =
            Math.min(b[machine], jobTimeTotal - jobTimeSoFar);
      }
    }

// For each machine, we now know the smallest possible initial
// idle time and the smallest possible end idle time and the
// total execution time. The lower bound of the makespan cannot
// be less than their sum.
    for (int m = inst.m; (--m) >= 0;) {
      lowerBound = Math.max(lowerBound, a[m] + T[m] + b[m]);
    }
// end lowerBound
    return lowerBound;
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
   * Compute the upper bound for the instance
   *
   * @param inst
   *          the instance
   * @return the upper bound
   */
  static int upperBound(final JSSPInstance inst) {
    int sum = 0;
    for (final int[] job : inst.jobs) {
      for (int i = job.length - 1; i > 0; i -= 2) {
        sum = Math.addExact(sum, job[i]);
      }
    }
    if ((sum <= 0) || (sum >= 100_000_000)) {
      throw new IllegalStateException(//
          "Invalid upper bound: " //$NON-NLS-1$
              + sum);
    }
    return sum;
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

// start relevant
}
// end relevant

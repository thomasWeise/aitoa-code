// start relevant
package aitoa.examples.jssp;

// end relevant
import java.util.Arrays;
import java.util.Objects;

// start relevant
import aitoa.structure.IObjectiveFunction;

/**
 * The makespan as objective function for a candidate solution to
 * the Job Shop Scheduling Problem, subject to minimization
 */
public final class JSSPMakespanObjectiveFunction
    implements IObjectiveFunction<JSSPCandidateSolution> {

// end relevant
  /** the JSSP instance */
  private final JSSPInstance m_instance;

  /**
   * create
   *
   * @param instance
   *          the instance
   */
  public JSSPMakespanObjectiveFunction(
      final JSSPInstance instance) {
    super();
    this.m_instance = Objects.requireNonNull(instance);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ("jssp:makespan:" + //$NON-NLS-1$
        this.getClass().getCanonicalName());
  }

// start relevant
  /**
   * Compute the makespan of a Gantt chart
   *
   * @param y
   *          the Gantt chart / candidate solution
   */
  @Override
  public final double evaluate(final JSSPCandidateSolution y) {
    int makespan = 0;
// look at the schedule for each machine
    for (final int[] machine : y.schedule) {
// the end time of the last job on the machine is the last number
// in the array
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
   * @return the lower bound
   */
  public final int lowerBound() {
    final JSSPInstance inst = this.m_instance;

    final int[] a = new int[inst.m]; // lb inactive time at start
    final int[] b = new int[inst.m]; // lb inactive time at end
    final int[] T = new int[inst.m]; // time of machine
    Arrays.fill(a, Integer.MAX_VALUE);
    Arrays.fill(b, Integer.MAX_VALUE);

    int lowerBound = 0; // overall lower bound
    for (int n = inst.n; (--n) >= 0;) {
      final int[] job = inst.jobs[n];

// for each job, first compute the total job runtime
      int jobTimeTotal = 0; // total time
      for (int m = 1; m < job.length; m += 2) {
        jobTimeTotal += job[m];
      }

// lower bound of the makespan must be >= total job time
      if (jobTimeTotal > lowerBound) {
        lowerBound = jobTimeTotal;
      }

      // now compute machine values
      int jobTimeSoFar = 0;
      for (int m = 0; m < job.length;) {
        final int machine = job[m++];

// if the sub-job for machine m starts at jobTimeSoFar, the
// smallest machine start idle time cannot be bigger than that
        if (jobTimeSoFar < a[machine]) {
          a[machine] = jobTimeSoFar;
        }

        final int time = job[m++];
// add the sub-job execution time to the machine total time
        T[machine] += time;

// compute the remaining time of the job and check if this is
// less than the smallest-so-far machine end idle time
        jobTimeSoFar += time;
        final int idle = (jobTimeTotal - jobTimeSoFar);
        if (idle < b[machine]) {
          b[machine] = idle;
        }
      }
    }

// For each machine, we now know the smallest possible initial
// idle time and the smallest possible end idle time and the
// total execution time. The lower bound of the makespan cannot
// be less than their sum.
    for (int m = inst.m; (--m) >= 0;) {
      final int machineLower = a[m] + T[m] + b[m];
      if (machineLower > lowerBound) {
        lowerBound = machineLower;
      }
    }

    return lowerBound;
  }
// start relevant
}
// end relevant
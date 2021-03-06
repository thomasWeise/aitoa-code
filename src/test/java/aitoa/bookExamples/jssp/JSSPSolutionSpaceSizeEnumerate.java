package aitoa.bookExamples.jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/** Print the solution space sizes for the JSSP instances */
public final class JSSPSolutionSpaceSizeEnumerate {

  /**
   * make an {@code n*m} array
   *
   * @param m
   *          the second dimension
   * @param n
   *          n the first dimensions
   * @return the array
   */
  private static int[][] makeArray(final int m, final int n) {
    final int[][] a = new int[n][];
    a[0] = new int[m];

    // setup: put numbers from 0 to m-1 into the first array
    for (int i = m; (--i) >= 0;) {
      a[0][i] = i;
    }
    // copy that first array
    for (int i = n; (--i) > 0;) {
      a[i] = a[0].clone();
    }
    return (a);
  }

  /**
   * enumerate all possible feasible schedules for m machines and
   * n jobs
   *
   * @param m
   *          the number of machines
   * @param n
   *          the number of jobs
   * @return the number
   */
  static long enumerate(final int m, final int n) {
    // for each of the n jobs, the order machines
    final int[][] jobMachines =
        JSSPSolutionSpaceSizeEnumerate.makeArray(m, n);
    // a gantt chart
    final int[][] gantt =
        JSSPSolutionSpaceSizeEnumerate.makeArray(n, m);

    final InstanceProcessor proc = new InstanceProcessor(gantt,
        new ValidGanttCounter(jobMachines));

    JSSPSolutionSpaceSizeEnumerate.permute(jobMachines, proc);
    return (proc.mMin);
  }

  /**
   * swap the elements indexes i and j
   *
   * @param a
   *          the array
   * @param i
   *          the first index
   * @param j
   *          the second index
   */
  private static void swap(final int[] a, final int i,
      final int j) {
    final int t = a[i];
    a[i] = a[j];
    a[j] = t;
  }

  /**
   * permute: enumerate all gantt diagrams
   *
   * @param a
   *          the array
   * @param consumer
   *          the consumer
   * @return true if aborted, false if not
   */
  static boolean permute(final int[][] a,
      final Predicate<int[][]> consumer) {
    return JSSPSolutionSpaceSizeEnumerate.permute(a, a[0].length,
        a.length - 1, consumer);
  }

  /**
   * permute
   *
   * @param a
   *          the array
   * @param n1
   *          the second-level index
   * @param n2
   *          the first-level index
   * @param consumer
   *          the consumer
   * @return true if aborted, false if not
   */
  private static boolean permute(final int[][] a, final int n1,
      final int n2, final Predicate<int[][]> consumer) {
    if (n1 <= 1) {
      if (n2 <= 0) {
        return consumer.test(a);
      }
      return JSSPSolutionSpaceSizeEnumerate.permute(a,
          a[0].length, n2 - 1, consumer);
    }
    for (int i = 0; i < n1; i++) {
      JSSPSolutionSpaceSizeEnumerate.swap(a[n2], i, n1 - 1);
      if (JSSPSolutionSpaceSizeEnumerate.permute(a, n1 - 1, n2,
          consumer)) {
        return true;
      }
      JSSPSolutionSpaceSizeEnumerate.swap(a[n2], i, n1 - 1);
    }
    return false;
  }

  /** the checker */
  private static final class InstanceProcessor
      implements Predicate<int[][]> {

    /** the minimum number of valid Gantt charts */
    long mMin;

    /** the gantt charts */
    private final int[][] mGantt;

    /** the counter */
    private final ValidGanttCounter mCounter;

    /**
     * create
     *
     * @param pGantt
     *          the gantt chart
     * @param pCounter
     *          the counter
     */
    InstanceProcessor(final int[][] pGantt,
        final ValidGanttCounter pCounter) {
      super();
      this.mGantt = pGantt;
      this.mMin = Long.MAX_VALUE;
      this.mCounter = pCounter;
    }

    /** {@inheritDoc} */
    @Override
    public boolean test(final int[][] jobMachines) {
      // then test all possible gantt diagrams and count
      // those which are deadlock-free
      this.mCounter.mCounter = 0L;
      this.mCounter.mBound = this.mMin;
      JSSPSolutionSpaceSizeEnumerate.permute(this.mGantt,
          this.mCounter);

      final long res = this.mCounter.mCounter;
      if (res < this.mMin) {
        this.mMin = res;
      }
      return false;
    }
  }

  /** the checker */
  private static final class ValidGanttCounter
      implements Predicate<int[][]> {
    /** the counter */
    long mCounter;
    /** the interesting bound */
    long mBound;

    /** the job machines */
    private final int[][] mJobMachines;

    /** the index of the next sub-job for each job */
    private final int[] mJobStage;
    /** the index of the next sub-job of each machine */
    private final int[] mGanttStage;

    /**
     * the job machines
     *
     * @param pJobMachines
     *          the assignments of jobs to machines
     */
    ValidGanttCounter(final int[][] pJobMachines) {
      super();
      this.mJobMachines = pJobMachines;
      this.mJobStage = new int[this.mJobMachines.length];
      this.mGanttStage = new int[this.mJobMachines[0].length];
    }

    /** {@inheritDoc} */
    @Override
    public boolean test(final int[][] gantt) {
      // set all job and machine indices to 0
      Arrays.fill(this.mJobStage, 0);
      Arrays.fill(this.mGanttStage, 0);

      // jobCount=length
      final int jobCount = this.mJobMachines.length;
      final int machineCount = gantt.length;

      // found: did we find a possible way to continue
      boolean found = false;
      do {
        found = false;
        // check each machine on the gantt diagram
        for (int machineIndex = machineCount;
            (--machineIndex) >= 0;) {
          final int nextStepinGanttForMachine =
              this.mGanttStage[machineIndex];
          if (nextStepinGanttForMachine < jobCount) {
            final int nextJobForMachine =
                gantt[machineIndex][nextStepinGanttForMachine];
            final int nextStepForJob =
                this.mJobStage[nextJobForMachine];
            final int nextMachineForJob =
                this.mJobMachines[nextJobForMachine][nextStepForJob];
            if (nextMachineForJob == machineIndex) {
              this.mGanttStage[machineIndex] =
                  (nextStepinGanttForMachine + 1);
              this.mJobStage[nextJobForMachine] =
                  (nextStepForJob + 1);
              found = true;
            }
          }
        }
      } while (found);

      for (final int completedJobs : this.mGanttStage) {
        if (completedJobs < jobCount) {
          return false; // we had a deadlock
        }
      }
      // do deadlock: Gantt chart is valid
      return ((++this.mCounter) >= this.mBound);
    }
  }

  /**
   * compute the number of possible gantt charts: computes (n!)^m
   *
   * @param m
   *          the number of machines
   * @param n
   *          the number of jobs
   * @return the number, or -1 on integer overflow
   */
  static long numberOf(final int m, final int n) {
    long res = n;
    for (long i = n; (--i) > 1L;) {
      final long old = res;
      res *= i;
      if ((res <= 0L) || ((res / i) != old)) {
        return (-1L);
      }
    }
    long res2 = res;
    for (int i = m; (--i) >= 1;) {
      final long old = res2;
      res2 *= res;
      if ((res2 <= 0L) || ((res2 / res) != old)) {
        return (-1L);
      }
    }
    return (res2);
  }

  /** the computer */
  private static final class Compute implements Runnable {
    /** the m */
    private final int mM;
    /** the n */
    private final int mN;

    /**
     * create
     *
     * @param pM
     *          the m
     * @param pN
     *          the n
     */
    Compute(final int pM, final int pN) {
      super();
      this.mM = pM;
      this.mN = pN;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
      final long res = JSSPSolutionSpaceSizeEnumerate
          .enumerate(this.mM, this.mN);
      synchronized (System.out) {
        System.out.print('{');
        System.out.print(this.mM);
        System.out.print('L');
        System.out.print(',');
        System.out.print(' ');
        System.out.print(this.mN);
        System.out.print('L');
        System.out.print(',');
        System.out.print(' ');
        System.out.print(res);
        System.out.print('L');
        System.out.print('}');
        System.out.println(',');
        System.out.flush();
      }
    }
  }

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */
  public static void main(final String[] args) {
    final ArrayList<long[]> list = new ArrayList<>();

    // try to sort the m*n combinations in order of the steps
    // needed for
    // enumerating all scenarios, only keep those that can be
    // enumerated
    for (int m = 2; m < 40; m++) {
      for (int n = 2; n < 40; n++) {
        final long instances =
            JSSPSolutionSpaceSizeEnumerate.numberOf(n, m);
        if (instances <= 0L) {
          continue;
        }
        final long gantt =
            JSSPSolutionSpaceSizeEnumerate.numberOf(m, n);
        if (gantt <= 0L) {
          continue;
        }
        final long steps = (instances * gantt);
        if ((steps < instances) || (steps < gantt)
            || ((steps / instances) != gantt)
            || ((steps / gantt) != instances)) {
          continue;
        }
        list.add(new long[] { m, n, steps });
      }
    }

    synchronized (System.out) {
      System.out.println("Found " + list.size()//$NON-NLS-1$
          + " potentially computable configurations.");//$NON-NLS-1$
      System.out.println(
          "We will compute them in a fastest-first fashion.");//$NON-NLS-1$
      System.out.println(
          "The values are given as long arrays of the form {m, n, LB}.");//$NON-NLS-1$
      System.out.println(
          "Eventually, you will have to kill this process, as it will probably run on forever.");//$NON-NLS-1$
      System.out.flush();
    }

    list.sort((a, b) -> {
      for (int i = 3; (--i) >= 0;) {
        final int r = Long.compare(a[i], b[i]);
        if (r != 0) {
          return r;
        }
      }
      return 0;
    });

    // We will try to compute in parallel to get more results
    // earlier
    final ExecutorService service =
        Executors.newFixedThreadPool(Math.max(1,
            Runtime.getRuntime().availableProcessors() - 1));

    for (final long[] pair : list) {
      final int m = ((int) (pair[0]));
      final int n = ((int) (pair[1]));
      service.execute(new Compute(m, n));
    }
    list.clear();

    service.shutdown();
    try {
      service.awaitTermination(365, TimeUnit.DAYS);
    } catch (final Throwable error) {
      error.printStackTrace();
    }
  }

  /** forbidden */
  private JSSPSolutionSpaceSizeEnumerate() {
    throw new UnsupportedOperationException();
  }
}

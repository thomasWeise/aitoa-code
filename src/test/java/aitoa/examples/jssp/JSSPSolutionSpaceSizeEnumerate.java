package aitoa.examples.jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/** Print the solution space sizes for the JSSP instances */
public class JSSPSolutionSpaceSizeEnumerate {

  /**
   * make an {@code n*m} array
   * 
   * @param m
   *          the second dimension
   * @param n
   *          n the first dimensions
   * @return the array
   */
  private static final int[][] makeArray(final int m, final int n) {
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
   * enumerate all possible feasible schedules for m machines and n jobs
   * 
   * @param m
   *          the number of machines
   * @param n
   *          the number of jobs
   * @return the number
   */
  private static final long[][] enumerate(final int m, final int n) {
    // for each of the n jobs, the order machines
    final int[][] jobMachines = makeArray(m, n);
    // a gantt chart
    final int[][] gantt = makeArray(n, m);

    final __InstanceProcessor proc = new __InstanceProcessor(gantt,
        new __ValidGanttCounter(jobMachines));

    permute(jobMachines, proc);
    return (proc.get());
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
  private static final void swap(int[] a, int i, int j) {
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
   */
  private static void permute(int[][] a, Consumer<int[][]> consumer) {
    permute(a, a[0].length, a.length - 1, consumer);
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
   */
  private static void permute(int[][] a, int n1, int n2,
      Consumer<int[][]> consumer) {
    if (n1 <= 1) {
      if (n2 <= 0) {
        consumer.accept(a);
        return;
      }
      permute(a, a[0].length, n2 - 1, consumer);
      return;
    }
    for (int i = 0; i < n1; i++) {
      swap(a[n2], i, n1 - 1);
      permute(a, n1 - 1, n2, consumer);
      swap(a[n2], i, n1 - 1);
    }
  }

  /** the checker */
  private static final class __InstanceProcessor
      implements Consumer<int[][]> {

    /** the minimum number of valid Gantt charts */
    private long m_min;
    /** the number of times the minimum number was encounterd */
    private long m_minCount;

    /** the maximum number of valid Gantt charts */
    private long m_max;
    /** the number of times the maximum number was encounterd */
    private long m_maxCount;

    /** the gantt charts */
    private final int[][] m_gantt;

    /** the counter */
    private final __ValidGanttCounter m_counter;

    /**
     * create
     * 
     * @param gantt
     *          the gantt chart
     * @param counter
     *          the counter
     */
    __InstanceProcessor(final int[][] gantt,
        final __ValidGanttCounter counter) {
      super();
      this.m_gantt = gantt;
      this.m_min = Long.MAX_VALUE;
      this.m_max = Long.MIN_VALUE;
      this.m_counter = counter;
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final int[][] jobMachines) {
      // then test all possible gantt diagrams and count
      // those which are deadlock-free
      this.m_counter.counter = 0L;
      permute(this.m_gantt, this.m_counter);

      final long res = this.m_counter.counter;
      if (res >= this.m_max) {
        if (res > this.m_max) {
          this.m_maxCount = 0L;
          this.m_max = res;
        }
        this.m_maxCount++;
      }
      if (res <= this.m_min) {
        if (res < this.m_min) {
          this.m_minCount = 0L;
          this.m_min = res;
        }
        this.m_minCount++;
      }
    }

    /** get the result */
    final long[][] get() {
      return (new long[][] { { this.m_min, this.m_minCount },
          { this.m_max, this.m_maxCount } });
    }
  }

  /** the checker */
  private static final class __ValidGanttCounter
      implements Consumer<int[][]> {
    /** the counter */
    long counter;

    /** the job machines */
    private final int[][] m_jobMachines;

    /** the index of the next sub-job for each job */
    private final int[] m_jobStage;
    /** the index of the next sub-job of each machine */
    private final int[] m_ganttStage;

    /** the job machines */
    __ValidGanttCounter(final int[][] jobMachines) {
      super();
      this.m_jobMachines = jobMachines;
      this.m_jobStage = new int[this.m_jobMachines.length];
      this.m_ganttStage = new int[this.m_jobMachines[0].length];
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final int[][] gantt) {
      // set all job and machine indices to 0
      Arrays.fill(this.m_jobStage, 0);
      Arrays.fill(this.m_ganttStage, 0);

      // jobCount=length
      final int jobCount = this.m_jobMachines.length;
      final int machineCount = gantt.length;

      // found: did we find a possible way to continue
      boolean found = false;
      do {
        found = false;
        // check each machine on the gantt diagram
        for (int machineIndex = machineCount; (--machineIndex) >= 0;) {
          final int nextStepinGanttForMachine = this.m_ganttStage[machineIndex];
          if (nextStepinGanttForMachine < jobCount) {
            final int nextJobForMachine = gantt[machineIndex][nextStepinGanttForMachine];
            final int nextStepForJob = this.m_jobStage[nextJobForMachine];
            final int nextMachineForJob = this.m_jobMachines[nextJobForMachine][nextStepForJob];
            if (nextMachineForJob == machineIndex) {
              this.m_ganttStage[machineIndex] = (nextStepinGanttForMachine
                  + 1);
              this.m_jobStage[nextJobForMachine] = (nextStepForJob + 1);
              found = true;
            }
          }
        }
      } while (found);

      for (final int completedJobs : this.m_ganttStage) {
        if (completedJobs < jobCount) {
          return; // we had a deadlock
        }
      }
      // do deadlock: Gantt chart is valid
      ++this.counter;
    }
  }

  /**
   * compute the number of possible gantt charts
   * 
   * @param m
   *          the number of machines
   * @param n
   *          the number of jobs
   * @return the number, or -1 on error
   */
  static final long numberOf(final int m, final int n) {
    long res = n;
    for (int i = n; (--i) > 1;) {
      res *= i;
      if (res <= 0L) {
        return (-1L);
      }
    }
    long res2 = res;
    for (int i = m; (--i) >= 1;) {
      res2 *= res;
      if (res2 <= 0L) {
        return (-1L);
      }
    }
    return (res2);
  }

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */
  public static final void main(final String[] args) {
    ArrayList<long[]> list = new ArrayList<long[]>();

    // try to sort the m*n combinations in order of the steps needed for
    // enumerating all scenarios, only keep those that can be enumerated
    for (int m = 1; m < 40; m++) {
      for (int n = 1; n < 40; n++) {
        final long instances = numberOf(n, m);
        if (instances <= 0L) {
          continue;
        }
        final long gantt = numberOf(m, n);
        if (gantt <= 0L) {
          continue;
        }
        final long steps = (instances * gantt);
        if ((steps < instances) || (steps < gantt)) {
          continue;
        }
        list.add(new long[] { m, n, steps });
      }
    }

    System.out.println("Found " + list.size()//$NON-NLS-1$
        + " potentially computable configurations.");//$NON-NLS-1$
    System.out.println("We will compute them in a fastest-first fashion.");//$NON-NLS-1$
    System.out.println("The values are given as long arrays of the form {m, n, LB}.");//$NON-NLS-1$

    list.sort((a, b) -> {
      for (int i = 3; (--i) >= 0;) {
        int r = Long.compare(a[i], b[i]);
        if (r != 0) {
          return r;
        }
      }
      return 0;
    });

    for (long[] pair : list) {
      final int m = (int) pair[0];
      final int n = (int) pair[1];
      System.out.print('{');
      System.out.print(m);
      System.out.print('L');
      System.out.print(',');
      System.out.print(' ');
      System.out.print(n);
      System.out.print('L');
      System.out.print(',');
      System.out.print(' ');
      System.out.print(enumerate(m, n)[0][0]);
      System.out.print('L');
      System.out.print('}');
      System.out.println(',');
    }
  }
}

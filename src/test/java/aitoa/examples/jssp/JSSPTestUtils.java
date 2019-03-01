package aitoa.examples.jssp;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;

import aitoa.TestTools;
import aitoa.utils.RandomUtils;

/** A base class for testing JSSP operators */
@Ignore
public class JSSPTestUtils {

  /** the instances */
  public static final JSSPInstance[] INSTANCS = { //
      new JSSPInstance("abz7"), //$NON-NLS-1$
      new JSSPInstance("la24"), //$NON-NLS-1$
      new JSSPInstance("yn4"), //$NON-NLS-1$
      new JSSPInstance("swv15") };//$NON-NLS-1$

  /**
   * assert a point in the search space
   *
   * @param x
   *          the point in the search space
   * @param inst
   *          the problem instance
   */
  public static final void assertX(final int[] x,
      final JSSPInstance inst) {
    Assert.assertNotNull(x);
    Assert.assertEquals(inst.m * inst.n, x.length);

    final int[] count = new int[inst.n];
    for (final int i : x) {
      TestTools.assertValidIndex(i, count.length);
      ++count[i];
    }
    TestTools.assertAllEquals(inst.m, count);
  }

  /**
   * assert a point in the solution space
   *
   * @param y
   *          the point in the solution space
   * @param inst
   *          the problem instance
   */
  public static final void assertY(final JSSPCandidateSolution y,
      final JSSPInstance inst) {
    Assert.assertNotNull(y);

    final int[][] schedule = y.schedule;

    Assert.assertEquals(inst.m, schedule.length);
    final boolean[] done = new boolean[inst.n];

    final int[][] jobPerspective = new int[inst.n][2 * inst.m];

    for (int mach = 0; mach < inst.m; mach++) {
      final int[] sched = schedule[mach];
      Assert.assertEquals(3 * inst.n, sched.length);
      Arrays.fill(done, false);
      int time = 0;
      for (int i = 0; i < sched.length;) {
        final int jobid = sched[i++];
        final int start = sched[i++];
        final int end = sched[i++];
        TestTools.assertGreaterOrEqual(start, time);
        TestTools.assertGreater(end, start);
        time = end;
        TestTools.assertValidIndex(jobid, inst.n);
        Assert.assertFalse(done[jobid]);
        done[jobid] = true;
        TestTools.assertValidIndex(jobid, inst.jobs.length);
        final int[] job = inst.jobs[jobid];
        findJob: for (int jj = 0; jj < job.length;) {
          final int ma = job[jj++];
          final int t = job[jj++];
          if (ma == mach) {
            TestTools.assertLessOrEqual(t, end - start);
            jobPerspective[jobid][jj - 2] = start;
            jobPerspective[jobid][jj - 1] = end;
            break findJob;
          }
        }
      }
      TestTools.assertAllEquals(true, done);
    }

    for (final int job[] : jobPerspective) {
      int prev = 0;
      for (final int i : job) {
        TestTools.assertGreaterOrEqual(i, prev);
        prev = i;
      }
    }
  }

  /**
   * create a valid point in the search space for the JSSP
   * instance
   *
   * @param problem
   *          the problem
   * @return the instance
   */
  public static final int[]
      createValidX(final JSSPInstance problem) {
    final int[] a = new int[problem.m * problem.n];
    JSSPTestUtils.randomX(a, problem);
    return a;
  }

  /**
   * create a canonical point in the search space
   *
   * @param x
   *          the destination point in the search space
   * @param inst
   *          the point in the solution space
   */
  public static final void canonicalX(final int[] x,
      final JSSPInstance inst) {
    int i = 0;
    for (int m = 0; m < inst.m; m++) {
      for (int n = 0; n < inst.n; n++) {
        x[i++] = n;
      }
    }
    Assert.assertEquals(i, x.length);
  }

  /**
   * create a random point in the search space
   *
   * @param x
   *          the destination point in the search space
   * @param inst
   *          the point in the solution space
   */
  public static final void randomX(final int[] x,
      final JSSPInstance inst) {
    JSSPTestUtils.canonicalX(x, inst);
    RandomUtils.shuffle(ThreadLocalRandom.current(), x, 0,
        x.length);
  }
}

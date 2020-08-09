package aitoa.examples.jssp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;

import aitoa.utils.math.BigMath;

/**
 * <p>
 * An instance of the Job Shop Scheduling Problem (JSSP). This
 * class provides the instances taken from
 * http://people.brunel.ac.uk/~mastjjb/jeb/orlib/jobshopinfo.html
 * plus one small "demo" instance.
 * </p>
 * <p>
 * The problem instance holds all the data that describes one
 * concrete JSSP. A JSSP consists of {@link #m m} machines and
 * {@link #n n} that need to be executed on the machines. Each
 * job consists of also exactly {@code m} sub-jobs: it will
 * utilize each machine. The order in which the sub-jobs must be
 * executed is defined, as well as the time they need. The goal
 * is to assign jobs to machines such in a way that the makespan,
 * i.e., the total time needed to complete all jobs, is
 * minimized.
 * </p>
 */
// start relevant
public class JSSPInstance {

  /** the number of machines */
  public final int m;

  /** the number of jobs */
  public final int n;

  /** for each job, the sequence of machines and times */
  public final int[][] jobs;
// end relevant
  /** the instance name */
  public final String id;

  /**
   * Create a Job Shop Scheduling Problem instance from raw data
   * and a name
   *
   * @param pData
   *          the data
   * @param pInstance
   *          the instance
   */
  public JSSPInstance(final int[][] pData,
      final String pInstance) {
    super();

    this.n = pData.length;
    this.m = (pData[0].length >>> 1);
    final int cmp = (this.m << 1);
    for (final int[] job : pData) {
      if (job.length != cmp) {
        throw new IllegalArgumentException(//
            "Job length " + job.length + //$NON-NLS-1$
                " should be " + cmp); //$NON-NLS-1$
      }
    }
    this.jobs = pData;

    this.id = pInstance.trim();
    if (pInstance.length() <= 0) {
      throw new IllegalArgumentException(//
          "Instance name cannot be empty.");//$NON-NLS-1$
    }

    int totalTimeSum = 0;
    for (final int[] job : this.jobs) {
      int jobTimeSum = 0;
      if (job.length != (2 * this.m)) {
        throw new IllegalArgumentException(//
            "Job length invalid, is " //$NON-NLS-1$
                + job.length + //
                ", but should be " //$NON-NLS-1$
                + (2 * this.m));
      }
      for (int i = 0; i < job.length;) {
        final int machine = job[i++];
        if ((machine < 0) || (machine >= this.m)) {
          throw new IllegalArgumentException(//
              "Invalid machine " //$NON-NLS-1$
                  + machine + //
                  ", must be in 0.." //$NON-NLS-1$
                  + (this.m - 1));
        }
        final int time = job[i++];
        if ((time < 0) || (time > 1_000_000)) {
          throw new IllegalArgumentException(//
              "Invalid time " //$NON-NLS-1$
                  + time + //
                  ", must be in 0..1_000_000"); //$NON-NLS-1$
        }
        jobTimeSum = Math.addExact(jobTimeSum, time);
        totalTimeSum = Math.addExact(totalTimeSum, time);
      }
      if ((jobTimeSum < 1) || (jobTimeSum > 10_000_000)) {
        throw new IllegalArgumentException(//
            "Invalid job time sum " //$NON-NLS-1$
                + totalTimeSum + //
                ", must be in 1..10_000_000"); //$NON-NLS-1$
      }
    }

    if ((totalTimeSum < this.n)
        || (totalTimeSum > 100_000_000)) {
      throw new IllegalArgumentException(//
          "Invalid time sum " //$NON-NLS-1$
              + totalTimeSum + //
              ", must be in " //$NON-NLS-1$
              + this.n + "..100_000_000"); //$NON-NLS-1$
    }
  }

  /**
   * Load the specified instance from a text resource.
   *
   * @param pInstance
   *          the name of the instance
   */
  public JSSPInstance(final String pInstance) {
    this(JSSPInstance.loadDataFromResource(pInstance),
        pInstance);
  }

  /**
   * Load the instance data from a resource
   *
   * @param instance
   *          the instance id
   * @return the data
   */
  @SuppressWarnings("null")
  private static final int[][]
      loadDataFromResource(final String instance) {
    int njobs = -1;
    int nmachines = -1;
    int[][] data = null;
    int dataIndex = 0;

// load the complete orlib.txt file until we find the target
// instance this is inefficient, but well
    try (final InputStream is =
        JSSPInstance.class.getResourceAsStream(//
            (instance.equalsIgnoreCase("demo") ? //$NON-NLS-1$
                "demo.txt" : //$NON-NLS-1$
                "instance_data.txt")); //$NON-NLS-1$
        final InputStreamReader isr = new InputStreamReader(is);
        final BufferedReader br = new BufferedReader(isr)) {

// this string indicates that we have the instance
      final String seek = "instance " + instance;//$NON-NLS-1$
      int found = 0;
      String line = null;

      loop: while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.length() <= 0) {
          continue; // empty line: ignore
        }
        switch (found) {
          case 0: {
            if (seek.equalsIgnoreCase(line)) {
              found = 1; // found instance begin
            }
            break;
          }

          case 3: {
// found line with the instance size, split at white space
            final String[] s = line.split("\\s+"); //$NON-NLS-1$
            njobs = Integer.parseInt(s[0]);
            nmachines = Integer.parseInt(s[1]);
            if ((njobs <= 0) || (nmachines <= 0)) {
              throw new IllegalArgumentException(//
                  "Invalid instance size: " + //$NON-NLS-1$
                      njobs + ", " + nmachines);//$NON-NLS-1$
            }
            data = new int[njobs][2 * nmachines];
          }

          //$FALL-THROUGH$
          case 1: // ++++
          case 2: { // instance description
            found++; // can ignore instance description
            break; // and ++++ even more so
          }

          case 4: { // instance data: split at white space
            final String[] s = line.split("\\s+"); //$NON-NLS-1$
            if (s.length != data[dataIndex].length) {
              throw new IllegalArgumentException(
                  "Wrong length of job line " + //$NON-NLS-1$
                      (dataIndex + 1));
            }
            for (int i = s.length; (--i) >= 0;) {
              if ((data[dataIndex][i] =
                  Integer.parseInt(s[i])) < 0) {
                throw new IllegalArgumentException(
                    "Illegal machine index/machine time in job line "//$NON-NLS-1$
                        + (dataIndex + 1));
              }
            }
            if ((++dataIndex) >= njobs) {
              break loop;
            }
            break;
          }

          default: {
            throw new IllegalStateException(//
                "Premature end of file.");//$NON-NLS-1$
          }
        }
      }

    } catch (final IOException error) {
      throw new IllegalStateException(error);
    }

// did we find the instance?
    if ((njobs <= 0) || (nmachines <= 0) || (data == null)
        || (dataIndex != data.length)) {
      throw new IllegalArgumentException(//
          "Instance '" + instance + //$NON-NLS-1$
              "' not found."); //$NON-NLS-1$
    }

    return data;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return this.id;
  }

  /**
   * Compute an approximation of the scale of this instance. A
   * JSSP instance with {@code n} jobs and {@code m} machines
   * will have a search space containing {@code (n!)^m} possible
   * solutions, some of which might be deadlocks. Normally, when
   * looking at the scale of a problem, we will not use the size
   * of the search space directly. For example, for bit strings
   * of length {@code n}, we will not say the scale is
   * {@code 2^n} although this is the size of the search space.
   * Instead, we will say the scale is {@code n}. So in order to
   * somehow norm the size of the instances, we here return the
   * logarithm base 2 of the solution space size.
   *
   * @return the logarithm base 2 of the solution space size
   */
  public final double getScale() {
    return Math.max(1d, BigMath.ld(//
        BigMath.factorial(BigInteger.valueOf(this.n))
            .pow(this.m)));
  }

// start relevant
}
// end relevant

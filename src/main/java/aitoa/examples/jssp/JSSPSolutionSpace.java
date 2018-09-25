// start relevant
package aitoa.examples.jssp;

// end relevant
import java.io.IOException;
// start relevant
import java.util.Arrays;
import java.util.Objects;

import aitoa.structure.ISpace;

/**
 * An implementation of the space interface for the solution
 * space of our JSSP
 */
public final class JSSPSolutionSpace
    implements ISpace<JSSPCandidateSolution> {

  /** the problem instance */
  public final JSSPInstance instance;

  /**
   * create
   *
   * @param _instance
   *          the problem instance
   */
  public JSSPSolutionSpace(final JSSPInstance _instance) {
    super();
    this.instance = Objects.requireNonNull(_instance);
  }

  /**
   * create an empty instance
   *
   * @return the empty instance
   */
  @Override
  public final JSSPCandidateSolution create() {
    return new JSSPCandidateSolution(this.instance.m,
        this.instance.n);
  }

  /** {@inheritDoc} */
  @Override
  public final void copy(final JSSPCandidateSolution from,
      final JSSPCandidateSolution to) {
    final int n = this.instance.n * 3;
    int i = 0;
    for (final int[] s : from.schedule) {
      System.arraycopy(s, 0, to.schedule[i++], 0, n);
    }
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public final void print(final JSSPCandidateSolution z,
      final Appendable out) {
    try {
      JSSPSolutionSpace.__printJava(z, out);
      out.append('\n');
      out.append('\n');
      JSSPSolutionSpace.__printGanttData(z, out);
    } catch (final IOException ioe) {
      throw new RuntimeException(
          "Error when writing Gantt data.", //$NON-NLS-1$
          ioe);
    }
  }

  /**
   * Print a solution as raw data for a Gantt chart. The
   * generated code will create a R code for invoking the plotteR
   * package for drawing a Gantt chart.
   *
   * @param z
   *          the candidate solution
   * @param out
   *          the destination
   * @throws IOException
   *           if i/o fails
   */
  private static final void __printGanttData(
      final JSSPCandidateSolution z, final Appendable out)
      throws IOException {
    out.append("if(!(require(\"plotteR\"))){");//$NON-NLS-1$
    out.append('\n');
    out.append("if(!(require(\"devtools\"))){");//$NON-NLS-1$
    out.append('\n');
    out.append("install.packages(\"devtools\");");//$NON-NLS-1$
    out.append('\n');
    out.append("library(\"devtools\");");//$NON-NLS-1$
    out.append('\n');
    out.append("};");//$NON-NLS-1$
    out.append('\n');
    out.append(
        "devtools::install_github(\"thomasWeise/plotteR\");");//$NON-NLS-1$
    out.append('\n');
    out.append("library(\"plotteR\");");//$NON-NLS-1$
    out.append('\n');
    out.append("};");//$NON-NLS-1$
    out.append('\n');

    out.append("plot.gantt(list("); //$NON-NLS-1$
    out.append('\n');

    char next1 = ' ';
    for (final int[] sched : z.schedule) {
      out.append(next1);
      next1 = ',';
      out.append("list(");//$NON-NLS-1$
      char next2 = ' ';
      for (int i = 0; i < sched.length;) {
        out.append(next2);
        next2 = ',';
        out.append("list(job=");//$NON-NLS-1$
        out.append(Integer.toString(sched[i++]));
        out.append("L,start=");//$NON-NLS-1$
        out.append(Integer.toString(sched[i++]));
        out.append("L,end=");//$NON-NLS-1$
        out.append(Integer.toString(sched[i++]));
        out.append("L)");//$NON-NLS-1$
        out.append('\n');
      }

      out.append(')');
      out.append('\n');
    }

    out.append("), prefix.job=\"\");"); //$NON-NLS-1$
  }

  /**
   * Print a solution as Java source code
   *
   * @param z
   *          the candidate solution
   * @param out
   *          the destination
   * @throws IOException
   *           if i/o fails
   */
  private static final void __printJava(
      final JSSPCandidateSolution z, final Appendable out)
      throws IOException {
    out.append("new ");//$NON-NLS-1$
    out.append(JSSPCandidateSolution.class.getCanonicalName());
    out.append("(new int[][] ");//$NON-NLS-1$
    char ch1 = '{';
    for (final int[] schedule : z.schedule) {
      out.append(ch1);
      ch1 = ',';
      out.append('\n');
      char ch2 = '{';
      for (final int i : schedule) {
        out.append(ch2);
        ch2 = ',';
        out.append(' ');
        out.append(Integer.toString(i));
      }
      out.append('}');
    }
    out.append("})");//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void check(final JSSPCandidateSolution z) {
    if (z.schedule.length != this.instance.m) {
      throw new IllegalArgumentException(//
          "Schedule for " + //$NON-NLS-1$
              z.schedule.length + //
              " machines, but there are " + //$NON-NLS-1$
              this.instance.m);
    }

    final int goalLen = this.instance.n * 3;
    final int[] jobs = new int[this.instance.n];
    final boolean[] jobsOnMachine = new boolean[this.instance.n];
    int machine = -1;

    // check schedule
    for (final int[] schedule : z.schedule) {
      if (schedule.length != goalLen) {
        throw new IllegalArgumentException(//
            "Invalid array length " + //$NON-NLS-1$
                schedule.length + //
                ", should be " + //$NON-NLS-1$
                goalLen);
      }
      ++machine;
      Arrays.fill(jobsOnMachine, false);
      int prevEnd = 0;
      for (int i = 0; i < goalLen;) {
        final int job = schedule[i++];
        final int start = schedule[i++];
        final int end = schedule[i++];

        jobs[job]++;

        if (start < prevEnd) {
          throw new IllegalArgumentException(//
              "Overlapping jobs, previous end is " + //$NON-NLS-1$
                  prevEnd + //
                  ", but current start is " + //$NON-NLS-1$
                  start);
        }
        if (jobsOnMachine[job]) {
          throw new IllegalArgumentException(//
              "jobs " + //$NON-NLS-1$
                  job + //
                  " occurs twice on machine " + //$NON-NLS-1$
                  machine);
        }
        jobsOnMachine[job] = true;

        final int[] sel = this.instance.jobs[job];
        checker: {
          for (int v = 0; v < sel.length; v += 2) {
            if (sel[v] == machine) {
              if ((end - start) != sel[++v]) {
                throw new IllegalArgumentException(//
                    "wrong duration of " + //$NON-NLS-1$
                        job + //
                        " on machine " + //$NON-NLS-1$
                        machine + " should be " + //$NON-NLS-1$
                        sel[v] + " but is " + //$NON-NLS-1$
                        (end - start));
              }
              break checker;
            }
          }
          throw new IllegalArgumentException(//
              "job " + //$NON-NLS-1$
                  job + //
                  " cannot be on machine " + //$NON-NLS-1$
                  machine);
        }
        prevEnd = end;
      }
    }

// check if each job occured m times
    for (final int i : jobs) {
      if (i != this.instance.m) {
        throw new IllegalArgumentException(//
            "Some jobs do not occur " + //$NON-NLS-1$
                this.instance.m + //
                " times."); //$NON-NLS-1$
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((("jssp:gantt:" //$NON-NLS-1$
        + this.instance.toString()) + ':')
        + this.getClass().getCanonicalName());
  }

// start relevant
}
// end relevant

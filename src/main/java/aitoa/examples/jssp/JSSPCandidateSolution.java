// start relevant
package aitoa.examples.jssp;

import java.io.IOException;

/**
 * A candidate solution for the JSSP is a complete Gantt diagram
 */
public final class JSSPCandidateSolution {

  /**
   * the schedule: for each machine, the sequence of jobs. for
   * each job at a machine, three numbers: the job id, the start
   * time, end the end time
   */
  public final int[][] schedule;
// end relevant

  /**
   * create a blank candidate solution
   *
   * @param m
   *          the number of machines
   * @param n
   *          the number of jobs
   */
  public JSSPCandidateSolution(final int m, final int n) {
    super();
// allocate one array for each of the m machine, big enough to
// hold the IDs of all n jobs and their start- and end-times at
// the machine
    this.schedule = new int[m][3 * n];
  }

  /**
   * Print a solution as raw data for a Gantt chart. The
   * generated code will create a R code for invoking the plotteR
   * package for drawing a Gantt chart.
   *
   * @param dest
   *          the destination
   */
  public void printGanttData(final Appendable dest) {
    try {
      dest.append("if(!(require(\"plotteR\"))){");//$NON-NLS-1$
      dest.append('\n');
      dest.append("if(!(require(\"devtools\"))){");//$NON-NLS-1$
      dest.append('\n');
      dest.append("install.packages(\"devtools\");");//$NON-NLS-1$
      dest.append('\n');
      dest.append("library(\"devtools\");");//$NON-NLS-1$
      dest.append('\n');
      dest.append("};");//$NON-NLS-1$
      dest.append('\n');
      dest.append(
          "devtools::install_github(\"thomasWeise/plotteR\");");//$NON-NLS-1$
      dest.append('\n');
      dest.append("library(\"plotteR\");");//$NON-NLS-1$
      dest.append('\n');
      dest.append("};");//$NON-NLS-1$
      dest.append('\n');

      dest.append("plot.gantt(list("); //$NON-NLS-1$
      dest.append('\n');

      char next1 = ' ';
      for (final int[] sched : this.schedule) {
        dest.append(next1);
        next1 = ',';
        dest.append("list(");//$NON-NLS-1$
        char next2 = ' ';
        for (int i = 0; i < sched.length;) {
          dest.append(next2);
          next2 = ',';
          dest.append("list(job=");//$NON-NLS-1$
          dest.append(Integer.toString(sched[i++]));
          dest.append("L,start=");//$NON-NLS-1$
          dest.append(Integer.toString(sched[i++]));
          dest.append("L,end=");//$NON-NLS-1$
          dest.append(Integer.toString(sched[i++]));
          dest.append("L)");//$NON-NLS-1$
          dest.append('\n');
        }

        dest.append(')');
        dest.append('\n');
      }

      dest.append("), prefix.job=\"\");"); //$NON-NLS-1$
    } catch (final IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

// start relevant
}
// end relevant

package aitoa.examples.jssp;

import java.io.IOException;

import aitoa.structure.ISpace;

/**
 * An implementation of the space interface for the solution
 * space of our JSSP
 */
public final class JSSPSolutionSpace
    implements ISpace<JSSPCandidateSolution> {

  /** the number of machines */
  private final int m_m;
  /** the number of jobs */
  private final int m_n;

  /**
   * create
   *
   * @param inst
   *          the problem instance
   */
  public JSSPSolutionSpace(final JSSPInstance inst) {
    super();
    this.m_m = inst.m;
    this.m_n = inst.n;
  }

  /**
   * create an empty instance
   *
   * @return the empty instance
   */
  @Override
  public final JSSPCandidateSolution create() {
    return new JSSPCandidateSolution(this.m_m, this.m_n);
  }

  /** {@inheritDoc} */
  @Override
  public final void copy(final JSSPCandidateSolution from,
      final JSSPCandidateSolution to) {
    for (int i = this.m_m; (--i) >= 0;) {
      System.arraycopy(from.schedule[i], 0, to.schedule[i], 0,
          this.m_n);
    }
  }

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
}

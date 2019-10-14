package aitoa.examples.jssp.trees;

import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;
import aitoa.searchSpaces.trees.NodeTypeSet;
import aitoa.searchSpaces.trees.math.NullaryFunction;

/**
 * a constant number in {@code double} format
 */
public final class JobStatistic
    extends NullaryFunction<double[]> {

  /** create the names */
  private static final String[] NAMES =
      new String[JSSPTreeRepresentationMapping.DIM_VALUES];

  static {
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.JOB_ID] =
        "job"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.JOB_COMPLETED_SUBJOBS] =
        "jobCompletedSubJobs"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.JOB_NEXT_SUBJOB_WORK_TIME] =
        "jobNextSubJobWorkTime"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.JOB_LAST_SUBJOB_FINISHED_TIME] =
        "obLastSubJobFinishedTime"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.JOB_FINISHED_WORKTIME] =
        "jobFinishedWorkTime"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.JOB_TOTAL_WORKTIME] =
        "jobTotalWorkTime"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.MACHINE_ID] =
        "machine"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.MACHINE_LAST_SUBJOB_FINISHED_TIME] =
        "machineLastSubJobFinishedTime"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.MACHINE_COMPLETED_SUBJOBS] =
        "machineCompletedSubJobs"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.MACHINE_FINISHED_WORKTIME] =
        "machineFinishedWorkTime"; //$NON-NLS-1$
    JobStatistic.NAMES[JSSPTreeRepresentationMapping.MACHINE_TOTAL_WORKTIME] =
        "machineTotalWorkTime"; //$NON-NLS-1$

    for (final String n : JobStatistic.NAMES) {
      if (n == null) {
        throw new IllegalStateException(
            "Missing statistic key.");//$NON-NLS-1$
      }
    }
  }

  /** the statistic value */
  final int m_statValue;

  /**
   * Create a double constant node
   *
   * @param type
   *          the node type
   * @param statValue
   *          the statistics value
   */
  public JobStatistic(final NodeType<JobStatistic> type,
      final int statValue) {
    super(type);
    this.m_statValue = statValue;
    if ((statValue < 0)
        || (statValue >= JSSPTreeRepresentationMapping.DIM_VALUES)) {
      throw new IllegalArgumentException("invalid stat value: "//$NON-NLS-1$
          + statValue);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final double applyAsDouble(final double[] param) {
    return param[this.m_statValue];
  }

  /** {@inheritDoc} */
  @Override
  public final long applyAsLong(final double[] param) {
    return Math.round(param[this.m_statValue]);
  }

  /** {@inheritDoc} */
  @Override
  public final int applyAsInt(final double[] param) {
    final long value = Math.round(param[this.m_statValue]);
    if (value >= Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if (value <= Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    return ((int) (value));
  }

  /** {@inheritDoc} */
  @Override
  public final void asText(final Appendable out)
      throws IOException {
    out.append(JobStatistic.NAMES[this.m_statValue]);
  }

  /** {@inheritDoc} */
  @Override
  public final void asJavaPrintParameters(final Appendable out)
      throws IOException {
    out.append(',').append(' ').append(',').append(' ')
        .append(Integer.toString(this.m_statValue));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o instanceof JobStatistic) {
      final JobStatistic q = ((JobStatistic) o);
      return (q.m_statValue == this.m_statValue);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (0x28947517 ^ Integer.hashCode(this.m_statValue));
  }

  /**
   * Create a node type for job statistics
   *
   * @return the node type
   */
  public static final Function<NodeTypeSet<?>[], NodeType<?>>
      type() {
    return (a) -> new __JobStatisticNodeType(a);
  }

  /** a factory for job statistic node types */
  private final static class __JobStatisticNodeType
      extends NodeType<JobStatistic> {

    /** a set of statistics */
    private final JobStatistic[] m_statistics;

    /**
     * create the constant node factory
     *
     * @param children
     *          the child node types
     */
    __JobStatisticNodeType(final NodeTypeSet<?>[] children) {
      super(children);

      this.m_statistics =
          new JobStatistic[JSSPTreeRepresentationMapping.DIM_VALUES];
      for (int i = this.m_statistics.length; (--i) >= 0;) {
        this.m_statistics[i] = new JobStatistic(this, i);
      }
    }

    /** {@inheritDoc} */
    @Override
    public final JobStatistic instantiate(final Node[] children,
        final Random random) {
      if ((children != null) && (children.length > 0)) {
        throw new IllegalArgumentException(
            "job statistics cannot have children, but you provided " //$NON-NLS-1$
                + children.length);
      }
      final int i = random.nextInt(this.m_statistics.length);
      return (this.m_statistics[i]);
    }

    /** {@inheritDoc} */
    @Override
    public final JobStatistic createModifiedCopy(
        final JobStatistic node, final Random random) {
      return this.instantiate(null, random);
    }

    /** {@inheritDoc} */
    @Override
    public final JobStatistic replaceChild(
        final JobStatistic original, final Node child,
        final int index) {
      throw new UnsupportedOperationException(
          "job statistics have no children."); //$NON-NLS-1$
    }
  }
}

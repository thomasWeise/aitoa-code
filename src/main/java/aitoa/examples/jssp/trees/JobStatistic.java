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
    extends NullaryFunction<double[][]> {

  /** the statistic type */
  final int m_statType;
  /** the statistic value */
  final int m_statValue;

  /**
   * Create a double constant node
   *
   * @param type
   *          the node type
   * @param statType
   *          the statistics type
   * @param statValue
   *          the statistics value
   */
  public JobStatistic(final NodeType<JobStatistic> type,
      final int statType, final int statValue) {
    super(type);
    this.m_statType = statType;
    this.m_statValue = statValue;
  }

  /** {@inheritDoc} */
  @Override
  public final double applyAsDouble(final double[][] param) {
    return param[this.m_statType][this.m_statValue];
  }

  /** {@inheritDoc} */
  @Override
  public final long applyAsLong(final double[][] param) {
    return Math.round(param[this.m_statType][this.m_statValue]);
  }

  /** {@inheritDoc} */
  @Override
  public final int applyAsInt(final double[][] param) {
    final long value =
        Math.round(param[this.m_statType][this.m_statValue]);
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

    char end = ')';
    switch (this.m_statType) {
      case JSSPTreeRepresentationMapping.CURRENT: {
        end = 0;
        break;
      }
      case JSSPTreeRepresentationMapping.MIN: {
        out.append("min("); //$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.MEAN: {
        out.append("mean("); //$NON-NLS-1$
        break;
      }
      // case JSSPTreeRepresentationMapping.MAX:
      default: {
        out.append("max("); //$NON-NLS-1$
        break;
      }
    }

    switch (this.m_statValue) {
      case JSSPTreeRepresentationMapping.JOB_COMPLETED_SUBJOBS: {
        out.append("jobCompletedSubjobs");//$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.JOB_NEXT_SUBJOB_WORK_TIME: {
        out.append("subjobWorkTime");//$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.JOB_LAST_SUBJOB_FINISHED_TIME: {
        out.append("jobLastSubjobFinishedTime");//$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.JOB_FINISHED_WORKTIME: {
        out.append("jobFinishedWorkTime");//$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.JOB_TOTAL_WORKTIME: {
        out.append("jobTotalWorkTime");//$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.MACHINE_LAST_SUBJOB_FINISHED_TIME: {
        out.append("machineLastSubjobFinishedTime");//$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.MACHINE_COMPLETED_SUBJOBS: {
        out.append("machineCompletedSubjobs");//$NON-NLS-1$
        break;
      }
      case JSSPTreeRepresentationMapping.MACHINE_FINISHED_WORKTIME: {
        out.append("machineFinishedWorkTime");//$NON-NLS-1$
        break;
      }
      // case
      // JSSPTreeRepresentationMapping.MACHINE_TOTAL_WORKTIME:
      default: {
        out.append("machineTotalWorkTime");//$NON-NLS-1$
        break;
      }
    }

    if (end != 0) {
      out.append(end);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void asJavaPrintParameters(final Appendable out)
      throws IOException {
    out.append(',').append(' ')
        .append(Integer.toString(this.m_statType)).append(',')
        .append(' ').append(Integer.toString(this.m_statValue));
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
      return ((q.m_statType == this.m_statType)
          && (q.m_statValue == this.m_statValue));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (0x28947517
        ^ ((Integer.hashCode(this.m_statType) * 0xfffff)
            + Integer.hashCode(this.m_statValue)));
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
    private final JobStatistic[][] m_statistics;

    /**
     * create the constant node factory
     *
     * @param children
     *          the child node types
     */
    __JobStatisticNodeType(final NodeTypeSet<?>[] children) {
      super(children);

      this.m_statistics =
          new JobStatistic[JSSPTreeRepresentationMapping.DIM_STAT][JSSPTreeRepresentationMapping.DIM_VALUES];
      for (int i = this.m_statistics.length; (--i) >= 0;) {
        for (int j = this.m_statistics[i].length; (--j) >= 0;) {
          this.m_statistics[i][j] = new JobStatistic(this, i, j);
        }
      }
    }

    /** {@inheritDoc} */
    @Override
    public final JobStatistic instantiate(final Node[] children,
        final Random random) {
      final int i = random.nextInt(this.m_statistics.length);
      final int j = random.nextInt(this.m_statistics[i].length);
      return (this.m_statistics[i][j]);
    }

    /** {@inheritDoc} */
    @Override
    public final JobStatistic createModifiedCopy(
        final JobStatistic node, final Random random) {
      final int type = node.m_statType;
      final int value = node.m_statValue;

      int newType = type, newValue = value;

      do {
        if (random.nextBoolean()) {
          newType = random
              .nextInt(JSSPTreeRepresentationMapping.DIM_STAT);
        }
        if (random.nextBoolean()) {
          newValue = random
              .nextInt(JSSPTreeRepresentationMapping.DIM_VALUES);
        }
      } while ((newType == type) && (newValue == value));

      return this.m_statistics[newType][newValue];
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

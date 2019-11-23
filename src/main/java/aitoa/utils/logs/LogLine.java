package aitoa.utils.logs;

/**
 * A line from the log file
 */
public final class LogLine implements Comparable<LogLine> {

  /** the last improvement FE */
  public final long fe_last_improvement;
  /** the total number of consumed FEs */
  public final long fe_max;
  /** the time when the last improvement took place */
  public final long time_last_improvement;
  /** the total time consumed so far */
  public final long time_max;
  /** the total number of improvements */
  public final long improvements;
  /** best objective value reached so far */
  public final double f_min;
  /** was this line an improvement? */
  public final boolean is_improvement;

  /**
   * create
   *
   * @param _fe_last_improvement
   *          the FE where the last improvement took place (will
   *          be this one, if {@code is_improvement} is
   *          {@code true})
   * @param _fe_max
   *          the total consumed function evaluations
   * @param _time_last_improvement
   *          the time where the last improvement took place
   *          (will be this one, if {@code is_improvement} is
   *          {@code true})
   * @param _time_max
   *          the total consumed runtime
   * @param _improvements
   *          the total number of improvements (including this
   *          one, if {@code is_improvement} is {@code true})
   * @param _f_min
   *          the best-so-far objective value
   * @param _is_improvement
   *          {@code true} if this log point has a better
   *          {@code f_min} value than the one before,
   *          {@code false} otherwise
   */
  public LogLine(final long _fe_last_improvement,
      final long _fe_max, final long _time_last_improvement,
      final long _time_max, final long _improvements,
      final double _f_min, final boolean _is_improvement) {
    super();

    this.f_min = _f_min;
    if (!Double.isFinite(this.f_min)) {
      throw new IllegalArgumentException("Invalid best.f value: " //$NON-NLS-1$
          + this.f_min);
    }

    this.fe_max = _fe_max;
    if ((this.fe_max <= 0L)
        || (this.fe_max >= 1_000_000_000_000_000L)) {
      throw new IllegalArgumentException(
          "Invalid total FEs: " + this.fe_max); //$NON-NLS-1$
    }
    this.fe_last_improvement = _fe_last_improvement;
    if ((this.fe_last_improvement <= 0)
        || (this.fe_last_improvement > this.fe_max)) {
      throw new IllegalArgumentException(
          "Invalid last improvement FE: "//$NON-NLS-1$
              + this.fe_last_improvement + " for total FEs:  "//$NON-NLS-1$
              + this.fe_max);
    }

    this.time_max = _time_max;
    if ((this.time_max < 0L)
        || (this.time_max >= 0x19C6F8000000000L)) {
      throw new IllegalArgumentException(
          "Invalid total time: " + this.time_max); //$NON-NLS-1$
    }
    this.time_last_improvement = _time_last_improvement;
    if ((this.time_last_improvement < 0)
        || (this.time_last_improvement > this.time_max)) {
      throw new IllegalArgumentException(
          "Invalid last improvement time: "//$NON-NLS-1$
              + this.time_last_improvement + " for total time:  "//$NON-NLS-1$
              + this.time_max);
    }

    this.improvements = _improvements;
    if (this.improvements <= 0L) {
      throw new IllegalArgumentException(
          "Invalid number of improvements: "//$NON-NLS-1$
              + this.improvements);
    }

    this.is_improvement = _is_improvement;
    if ((this.fe_max == 1L) && (!this.is_improvement)) {
      throw new IllegalArgumentException(
          "First FE must be an improvement.");//$NON-NLS-1$
    }
    if (this.is_improvement
        && (this.fe_last_improvement != this.fe_max)) {
      throw new IllegalArgumentException(
          "If FE led to an improvmenet, then last improvement FE ("//$NON-NLS-1$
              + this.fe_last_improvement
              + ") must equal total FEs ("//$NON-NLS-1$
              + this.fe_max + ") but does not.");//$NON-NLS-1$
    }

    if (this.is_improvement
        && (this.time_last_improvement != this.time_max)) {
      throw new IllegalArgumentException(
          "If FE led to an improvmenet, then last improvement time ("//$NON-NLS-1$
              + this.time_last_improvement
              + ") must equal total time ("//$NON-NLS-1$
              + this.time_max + ") but does not.");//$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    int hc = Long.hashCode(this.fe_last_improvement);
    hc = (31 * hc) + Long.hashCode(this.fe_max);
    hc = (31 * hc) + Long.hashCode(this.time_last_improvement);
    hc = (31 * hc) + Long.hashCode(this.time_max);
    hc = (31 * hc) + Long.hashCode(this.improvements);
    hc = (31 * hc) + Double.hashCode(this.f_min);
    hc = (31 * hc) + Boolean.hashCode(this.is_improvement);
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof LogLine) {
      final LogLine e = ((LogLine) o);
      return ((this.fe_last_improvement == e.fe_last_improvement)//
          && (this.fe_max == e.fe_max)//
          && (this.time_last_improvement == e.time_last_improvement)//
          && (this.time_max == e.time_max)//
          && (this.improvements == e.improvements)//
          && (Double.compare(this.f_min, e.f_min) == 0)//
          && (this.is_improvement == e.is_improvement));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final LogLine o) {
    if (o == this) {
      return 0;
    }

    int r = Double.compare(this.f_min, o.f_min);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.fe_last_improvement,
        o.fe_last_improvement);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.time_last_improvement,
        o.time_last_improvement);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.fe_max, o.fe_max);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.time_max, o.time_max);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.improvements, o.improvements);
    if (r != 0) {
      return r;
    }
    return Boolean.compare(o.is_improvement,
        this.is_improvement);
  }
}
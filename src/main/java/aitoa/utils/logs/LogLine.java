package aitoa.utils.logs;

/**
 * A line from the log file
 */
public final class LogLine implements Comparable<LogLine> {

  /** the last improvement FE */
  public final long feLastImprovement;
  /** the total number of consumed FEs */
  public final long feMax;
  /** the time when the last improvement took place */
  public final long timeLastImprovement;
  /** the total time consumed so far */
  public final long timeMax;
  /** the total number of improvements */
  public final long improvements;
  /** best objective value reached so far */
  public final double fMin;
  /** was this line an improvement? */
  public final boolean isImprovement;

  /**
   * create
   *
   * @param pFeLastImprovement
   *          the FE where the last improvement took place (will
   *          be this one, if {@code is_improvement} is
   *          {@code true})
   * @param pFeMax
   *          the total consumed function evaluations
   * @param pTimeLastImprovement
   *          the time where the last improvement took place
   *          (will be this one, if {@code is_improvement} is
   *          {@code true})
   * @param pTimeMax
   *          the total consumed runtime
   * @param pImprovements
   *          the total number of improvements (including this
   *          one, if {@code is_improvement} is {@code true})
   * @param pFMin
   *          the best-so-far objective value
   * @param pIsImprovement
   *          {@code true} if this log point has a better
   *          {@code f_min} value than the one before,
   *          {@code false} otherwise
   */
  public LogLine(final long pFeLastImprovement,
      final long pFeMax, final long pTimeLastImprovement,
      final long pTimeMax, final long pImprovements,
      final double pFMin, final boolean pIsImprovement) {
    this(pFeLastImprovement, pFeMax, pTimeLastImprovement,
        pTimeMax, pImprovements, pFMin, pIsImprovement, true);
  }

  /**
   * create
   *
   * @param pFeLastImprovement
   *          the FE where the last improvement took place (will
   *          be this one, if {@code is_improvement} is
   *          {@code true})
   * @param pFeMax
   *          the total consumed function evaluations
   * @param pTimeLastImprovement
   *          the time where the last improvement took place
   *          (will be this one, if {@code is_improvement} is
   *          {@code true})
   * @param pTimeMax
   *          the total consumed runtime
   * @param pImprovements
   *          the total number of improvements (including this
   *          one, if {@code is_improvement} is {@code true})
   * @param pFMin
   *          the best-so-far objective value
   * @param pIsImprovement
   *          {@code true} if this log point has a better
   *          {@code f_min} value than the one before,
   *          {@code false} otherwise
   * @param pEnforceFirstMustBeImprovement
   *          must we enforce that the first FE must be an
   *          improvement?
   */
  LogLine(final long pFeLastImprovement, final long pFeMax,
      final long pTimeLastImprovement, final long pTimeMax,
      final long pImprovements, final double pFMin,
      final boolean pIsImprovement,
      final boolean pEnforceFirstMustBeImprovement) {
    super();

    this.fMin = pFMin;
    if (!Double.isFinite(this.fMin)) {
      throw new IllegalArgumentException("Invalid best.f value: " //$NON-NLS-1$
          + this.fMin);
    }

    this.feMax = pFeMax;
    if ((this.feMax <= 0L)
        || (this.feMax >= 1_000_000_000_000_000L)) {
      throw new IllegalArgumentException(
          "Invalid total FEs: " + this.feMax); //$NON-NLS-1$
    }
    this.feLastImprovement = pFeLastImprovement;
    if ((this.feLastImprovement <= 0)
        || (this.feLastImprovement > this.feMax)) {
      throw new IllegalArgumentException(
          "Invalid last improvement FE: "//$NON-NLS-1$
              + this.feLastImprovement + " for total FEs:  "//$NON-NLS-1$
              + this.feMax);
    }

    this.timeMax = pTimeMax;
    if ((this.timeMax < 0L)
        || (this.timeMax >= 0x19C6F8000000000L)) {
      throw new IllegalArgumentException(
          "Invalid total time: " + this.timeMax); //$NON-NLS-1$
    }
    this.timeLastImprovement = pTimeLastImprovement;
    if ((this.timeLastImprovement < 0)
        || (this.timeLastImprovement > this.timeMax)) {
      throw new IllegalArgumentException(
          "Invalid last improvement time: "//$NON-NLS-1$
              + this.timeLastImprovement + " for total time:  "//$NON-NLS-1$
              + this.timeMax);
    }

    this.improvements = pImprovements;
    if (this.improvements <= 0L) {
      throw new IllegalArgumentException(
          "Invalid number of improvements: "//$NON-NLS-1$
              + this.improvements);
    }

    this.isImprovement = pIsImprovement;
    if (pEnforceFirstMustBeImprovement && (this.feMax == 1L)
        && (!this.isImprovement)) {
      throw new IllegalArgumentException(
          "First FE must be an improvement.");//$NON-NLS-1$
    }
    if (this.isImprovement
        && (this.feLastImprovement != this.feMax)) {
      throw new IllegalArgumentException(
          "If FE led to an improvmenet, then last improvement FE ("//$NON-NLS-1$
              + this.feLastImprovement
              + ") must equal total FEs ("//$NON-NLS-1$
              + this.feMax + ") but does not.");//$NON-NLS-1$
    }

    if (this.isImprovement
        && (this.timeLastImprovement != this.timeMax)) {
      throw new IllegalArgumentException(
          "If FE led to an improvmenet, then last improvement time ("//$NON-NLS-1$
              + this.timeLastImprovement
              + ") must equal total time ("//$NON-NLS-1$
              + this.timeMax + ") but does not.");//$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int hc = Long.hashCode(this.feLastImprovement);
    hc = (31 * hc) + Long.hashCode(this.feMax);
    hc = (31 * hc) + Long.hashCode(this.timeLastImprovement);
    hc = (31 * hc) + Long.hashCode(this.timeMax);
    hc = (31 * hc) + Long.hashCode(this.improvements);
    hc = (31 * hc) + Double.hashCode(this.fMin);
    hc = (31 * hc) + Boolean.hashCode(this.isImprovement);
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof LogLine) {
      final LogLine e = ((LogLine) o);
      return ((this.feLastImprovement == e.feLastImprovement)//
          && (this.feMax == e.feMax)//
          && (this.timeLastImprovement == e.timeLastImprovement)//
          && (this.timeMax == e.timeMax)//
          && (this.improvements == e.improvements)//
          && (Double.compare(this.fMin, e.fMin) == 0)//
          && (this.isImprovement == e.isImprovement));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(final LogLine o) {
    if (o == this) {
      return 0;
    }

    int r = Double.compare(this.fMin, o.fMin);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.feLastImprovement,
        o.feLastImprovement);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.timeLastImprovement,
        o.timeLastImprovement);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.feMax, o.feMax);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.timeMax, o.timeMax);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.improvements, o.improvements);
    if (r != 0) {
      return r;
    }
    return Boolean.compare(o.isImprovement, this.isImprovement);
  }
}

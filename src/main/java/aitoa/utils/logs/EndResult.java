package aitoa.utils.logs;

import java.util.Objects;

import aitoa.structure.LogFormat;

/**
 * A line of the end results table, as created by
 * {@link EndResults}.
 */
public final class EndResult extends Setup {

  /** the best objective value achieved by the run */
  public final double bestF;
  /** the total time consumed by the run */
  public final long totalTime;
  /** the total FEs consumed by the run */
  public final long totalFEs;
  /**
   * the last time at which an improvement was achieved
   */
  public final long lastImprovementTime;
  /** the last FE at which an improvement was achieved */
  public final long lastImprovementFE;
  /**
   * the total number of times the run improved its result
   */
  public final long numberOfImprovements;
  /** the time budget */
  public final long budgetTime;
  /** the FE budget */
  public final long budgetFEs;
  /** the goal objective value */
  public final double goalF;

  /**
   * create
   *
   * @param _algorithm
   *          the algorithm id
   * @param _instance
   *          the instance id
   * @param _seed
   *          the seed
   * @param _bestF
   *          the best objective value achieved by the run
   * @param _totalTime
   *          the total time consumed by the run
   * @param _totalFEs
   *          the total FEs consumed by the run
   * @param _lastImprovementTime
   *          the last time at which an improvement was achieved
   * @param _lastImprovementFE
   *          the last FE at which an improvement was achieved
   * @param _numberOfImprovements
   *          the total number of times the run improved its
   *          result
   * @param _budgetTime
   *          the time budget
   * @param _budgetFEs
   *          the FE budget
   * @param _goalF
   *          the goal objective value
   */
  public EndResult(final String _algorithm,
      final String _instance, final String _seed,
      final double _bestF, final long _totalTime,
      final long _totalFEs, final long _lastImprovementTime,
      final long _lastImprovementFE,
      final long _numberOfImprovements, final long _budgetTime,
      final long _budgetFEs, final double _goalF) {
    super(_algorithm, _instance, _seed);

    this.bestF = _bestF;
    if (!Double.isFinite(this.bestF)) {
      throw new IllegalArgumentException(
          "Invalid f.best: " + this.bestF);//$NON-NLS-1$
    }

    this.totalTime = _totalTime;
    if ((this.totalTime < 0L)
        || (this.totalTime > 315360000000000L)) {
      throw new IllegalArgumentException(
          "Invalid total time: " + this.totalTime);//$NON-NLS-1$
    }

    this.totalFEs = _totalFEs;
    if ((this.totalFEs < 1L)
        || (this.totalFEs > 315360000000000L)) {
      throw new IllegalArgumentException(
          "Invalid total FEs: " + this.totalFEs);//$NON-NLS-1$
    }

    this.lastImprovementTime = _lastImprovementTime;
    if ((this.lastImprovementTime < 0L)
        || (this.lastImprovementTime > this.totalTime)) {
      throw new IllegalArgumentException(
          "Invalid last improvement time: " //$NON-NLS-1$
              + this.lastImprovementTime + " for total time " //$NON-NLS-1$
              + this.totalTime);
    }

    this.lastImprovementFE = _lastImprovementFE;
    if ((this.lastImprovementFE <= 0L)
        || (this.lastImprovementFE > this.totalFEs)) {
      throw new IllegalArgumentException(
          "Invalid last improvement FE: " //$NON-NLS-1$
              + this.lastImprovementFE + " for total FEs " //$NON-NLS-1$
              + this.totalFEs);
    }

    this.budgetTime = _budgetTime;
    if (this.budgetTime < 0L) {
      throw new IllegalArgumentException(
          "Invalid time budget: " + this.budgetTime);//$NON-NLS-1$
    }
    LogParser._checkTime(this.totalTime, this.budgetTime);

    this.budgetFEs = _budgetFEs;
    if ((this.budgetFEs < 1L)
        || (this.budgetFEs < this.totalFEs)) {
      throw new IllegalArgumentException(
          "Invalid budget FEs: " + this.budgetFEs + //$NON-NLS-1$
              " for total FEs: " + this.totalFEs);//$NON-NLS-1$
    }

    this.numberOfImprovements = _numberOfImprovements;
    if ((this.numberOfImprovements <= 0)
        || (this.numberOfImprovements > this.lastImprovementFE)) {
      throw new IllegalArgumentException(
          "Invalid number of improvements " //$NON-NLS-1$
              + this.numberOfImprovements
              + " for last improvement FE " + //$NON-NLS-1$
              this.lastImprovementFE);
    }

    this.goalF = _goalF;
    if ((!Double.isFinite(this.goalF))
        && (!(this.goalF <= Double.NEGATIVE_INFINITY))) {
      throw new IllegalArgumentException(
          "Invalid goal objective value: "//$NON-NLS-1$
              + _goalF);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    int hc = this.algorithm.hashCode();
    hc = (31 * hc) + this.instance.hashCode();
    hc = (31 * hc) + Long.hashCode(this.seed);
    hc = (31 * hc) + Double.hashCode(this.bestF);
    hc = (31 * hc) + Long.hashCode(this.totalTime);
    hc = (31 * hc) + Long.hashCode(this.totalFEs);
    hc = (31 * hc) + Long.hashCode(this.lastImprovementTime);
    hc = (31 * hc) + Long.hashCode(this.lastImprovementFE);
    hc = (31 * hc) + Long.hashCode(this.numberOfImprovements);
    hc = (31 * hc) + Long.hashCode(this.budgetTime);
    hc = (31 * hc) + Long.hashCode(this.budgetFEs);
    hc = (31 * hc) + Double.hashCode(this.goalF);
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof EndResult) {
      final EndResult e = ((EndResult) o);
      return (Objects.equals(this.algorithm, e.algorithm) && //
          Objects.equals(this.instance, e.instance) && //
          (this.seed == e.seed) && //
          (Double.compare(this.bestF, e.bestF) == 0) && //
          (this.totalTime == e.totalTime) && //
          (this.totalFEs == e.totalFEs) && //
          (this.lastImprovementTime == e.lastImprovementTime) && //
          (this.lastImprovementFE == e.lastImprovementFE) && //
          (this.numberOfImprovements == e.numberOfImprovements)
          && //
          (this.budgetTime == e.budgetTime) && //
          (this.budgetFEs == e.budgetFEs) && //
          (Double.compare(this.goalF, e.goalF) == 0));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final Setup o) {
    if (o == this) {
      return 0;
    }

    if (o instanceof EndResult) {
      final EndResult e = ((EndResult) o);
      int r = this.algorithm.compareTo(e.algorithm);
      if (r != 0) {
        return r;
      }
      r = this.instance.compareTo(e.instance);
      if (r != 0) {
        return r;
      }
      r = Long.compareUnsigned(this.seed, e.seed);
      if (r != 0) {
        return r;
      }
      r = Double.compare(this.bestF, e.bestF);
      if (r != 0) {
        return r;
      }
      r = Long.compare(this.lastImprovementFE,
          e.lastImprovementFE);
      if (r != 0) {
        return r;
      }
      r = Long.compare(this.lastImprovementTime,
          e.lastImprovementTime);
      if (r != 0) {
        return r;
      }
      r = Long.compare(e.numberOfImprovements,
          this.numberOfImprovements);
      if (r != 0) {
        return r;
      }
      r = Long.compare(this.totalFEs, e.totalFEs);
      if (r != 0) {
        return r;
      }
      r = Long.compare(this.totalTime, e.totalTime);
      if (r != 0) {
        return r;
      }
      r = Long.compare(this.budgetFEs, e.budgetFEs);
      if (r != 0) {
        return r;
      }
      r = Long.compare(this.budgetTime, e.budgetTime);
      if (r != 0) {
        return r;
      }
      return Double.compare(this.goalF, e.goalF);
    }
    return super.compareTo(o);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return super.toString() + ':' + this.bestF
        + LogFormat.CSV_SEPARATOR_CHAR + this.lastImprovementFE
        + LogFormat.CSV_SEPARATOR_CHAR
        + this.lastImprovementTime;
  }
}
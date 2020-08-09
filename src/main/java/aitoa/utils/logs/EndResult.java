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
   * @param pAlgorithm
   *          the algorithm id
   * @param pInstance
   *          the instance id
   * @param pSeed
   *          the seed
   * @param pBestF
   *          the best objective value achieved by the run
   * @param pTotalTime
   *          the total time consumed by the run
   * @param pTotalFEs
   *          the total FEs consumed by the run
   * @param pLastImprovementTime
   *          the last time at which an improvement was achieved
   * @param pLastImprovementFE
   *          the last FE at which an improvement was achieved
   * @param pNumberOfImprovements
   *          the total number of times the run improved its
   *          result
   * @param pBudgetTime
   *          the time budget
   * @param pBudgetFEs
   *          the FE budget
   * @param pGoalF
   *          the goal objective value
   */
  public EndResult(final String pAlgorithm,
      final String pInstance, final long pSeed,
      final double pBestF, final long pTotalTime,
      final long pTotalFEs, final long pLastImprovementTime,
      final long pLastImprovementFE,
      final long pNumberOfImprovements, final long pBudgetTime,
      final long pBudgetFEs, final double pGoalF) {
    super(pAlgorithm, pInstance, pSeed);

    this.bestF = pBestF;
    if (!Double.isFinite(this.bestF)) {
      throw new IllegalArgumentException(
          "Invalid f.best: " + this.bestF);//$NON-NLS-1$
    }

    this.totalTime = pTotalTime;
    if ((this.totalTime < 0L)
        || (this.totalTime > 315360000000000L)) {
      throw new IllegalArgumentException(
          "Invalid total time: " + this.totalTime);//$NON-NLS-1$
    }

    this.totalFEs = pTotalFEs;
    if ((this.totalFEs < 1L)
        || (this.totalFEs > 315360000000000L)) {
      throw new IllegalArgumentException(
          "Invalid total FEs: " + this.totalFEs);//$NON-NLS-1$
    }

    this.lastImprovementTime = pLastImprovementTime;
    if ((this.lastImprovementTime < 0L)
        || (this.lastImprovementTime > this.totalTime)) {
      throw new IllegalArgumentException(
          "Invalid last improvement time: " //$NON-NLS-1$
              + this.lastImprovementTime + " for total time " //$NON-NLS-1$
              + this.totalTime);
    }

    this.lastImprovementFE = pLastImprovementFE;
    if ((this.lastImprovementFE <= 0L)
        || (this.lastImprovementFE > this.totalFEs)) {
      throw new IllegalArgumentException(
          "Invalid last improvement FE: " //$NON-NLS-1$
              + this.lastImprovementFE + " for total FEs " //$NON-NLS-1$
              + this.totalFEs);
    }

    this.budgetTime = pBudgetTime;
    if (this.budgetTime < 0L) {
      throw new IllegalArgumentException(
          "Invalid time budget: " + this.budgetTime);//$NON-NLS-1$
    }
    LogParser.checkTime(this.totalTime, this.budgetTime);

    this.budgetFEs = pBudgetFEs;
    if ((this.budgetFEs < 1L)
        || (this.budgetFEs < this.totalFEs)) {
      throw new IllegalArgumentException(
          "Invalid budget FEs: " + this.budgetFEs + //$NON-NLS-1$
              " for total FEs: " + this.totalFEs);//$NON-NLS-1$
    }

    this.numberOfImprovements = pNumberOfImprovements;
    if ((this.numberOfImprovements <= 0)
        || (this.numberOfImprovements > this.lastImprovementFE)) {
      throw new IllegalArgumentException(
          "Invalid number of improvements " //$NON-NLS-1$
              + this.numberOfImprovements
              + " for last improvement FE " + //$NON-NLS-1$
              this.lastImprovementFE);
    }

    this.goalF = pGoalF;
    if ((!Double.isFinite(this.goalF))
        && (!(this.goalF <= Double.NEGATIVE_INFINITY))) {
      throw new IllegalArgumentException(
          "Invalid goal objective value: "//$NON-NLS-1$
              + pGoalF);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
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
  public boolean equals(final Object o) {
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
  public int compareTo(final Setup o) {
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
  public String toString() {
    return super.toString() + ':' + this.bestF
        + LogFormat.CSV_SEPARATOR_CHAR + this.lastImprovementFE
        + LogFormat.CSV_SEPARATOR_CHAR
        + this.lastImprovementTime;
  }
}

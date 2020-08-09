package aitoa.utils.logs;

import java.util.Objects;

/**
 * A line of the end result statistics table, as created by
 * {@link EndResultStatistics}.
 */
public final class EndResultStatistic
    implements Comparable<EndResultStatistic> {
  /** the algorithm id */
  public final String algorithm;
  /** the instance id */
  public final String instance;
  /** the number of runs whose data was collected */
  public final int runs;
  /**
   * the best objective value achieved by the run
   */
  public final DoubleStatisticsBig bestF;
  /** the total time consumed by the run */
  public final IntStatisticsBig totalTime;
  /** the total FEs consumed by the run */
  public final IntStatisticsBig totalFEs;
  /**
   * the last time at which an improvement was achieved
   */
  public final IntStatisticsBig lastImprovementTime;
  /**
   * the last FE at which an improvement was achieved
   */
  public final IntStatisticsBig lastImprovementFE;
  /** the number of improvements */
  public final IntStatisticsBig numberOfImprovements;
  /** the time budget of the run */
  public final IntStatisticsSmall budgetTime;
  /** the FE budget the run */
  public final IntStatisticsSmall budgetFEs;

  /** the number of runs which were "successful" */
  public final int successes;
  /** the empirical expected time to success */
  public final double ertTime;
  /** the empirical expected FEs to success */
  public final double ertFEs;
  /**
   * statistics regarding the last improvement time of successful
   * runs, or {@code null} if no run was successful
   */
  public final IntStatisticsSmallWithSetups successTime;
  /**
   * statistics regarding the last improvement FE of successful
   * runs, or {@code null} if no run was successful
   */
  public final IntStatisticsSmallWithSetups successFEs;

  /**
   * create
   *
   * @param pAlgorithm
   *          the algorithm name
   * @param pInstance
   *          the instance name
   * @param pRuns
   *          the number of runs
   * @param pBestF
   *          the best f record
   * @param pTotalTime
   *          the total time record
   * @param pTotalFEs
   *          the total fes record
   * @param pLastImprovementTime
   *          the last improvement time record
   * @param pLastImprovementFE
   *          the last improvement fe record
   * @param pNumberOfImprovements
   *          the number of improvements record
   * @param pBudgetTime
   *          the budget time record
   * @param pBudgetFEs
   *          the budget fes record
   * @param pSuccesses
   *          the number of successes
   * @param pErtTime
   *          the expected running time in ms
   * @param pErtFEs
   *          the expected running time in FEs
   * @param pSuccessTime
   *          statistics regarding the last improvement time of
   *          successful runs, or {@code null} if no run was
   *          successful
   * @param pSuccessFEs
   *          statistics regarding the last improvement FEs of
   *          successful runs, or {@code null} if no run was
   *          successful
   */
  public EndResultStatistic(final String pAlgorithm,
      final String pInstance, final int pRuns,
      final DoubleStatisticsBig pBestF,
      final IntStatisticsBig pTotalTime,
      final IntStatisticsBig pTotalFEs,
      final IntStatisticsBig pLastImprovementTime,
      final IntStatisticsBig pLastImprovementFE,
      final IntStatisticsBig pNumberOfImprovements,
      final IntStatisticsSmall pBudgetTime,
      final IntStatisticsSmall pBudgetFEs, final int pSuccesses, //
      final double pErtTime, //
      final double pErtFEs, //
      final IntStatisticsSmallWithSetups pSuccessTime, //
      final IntStatisticsSmallWithSetups pSuccessFEs) {
    super();

    this.algorithm = pAlgorithm.trim();
    if (this.algorithm.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have '" + pAlgorithm + //$NON-NLS-1$
              "' as algorithm name.");//$NON-NLS-1$
    }

    this.instance = pInstance.trim();
    if (this.instance.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have '" + pInstance + //$NON-NLS-1$
              "' as instance name.");//$NON-NLS-1$
    }

    this.runs = pRuns;
    if (this.runs <= 0) {
      throw new IllegalArgumentException(
          "Number of runs must be positive, but is "//$NON-NLS-1$
              + this.runs);
    }

    this.bestF = Objects.requireNonNull(pBestF);
    this.totalTime = Objects.requireNonNull(pTotalTime);
    this.totalFEs = Objects.requireNonNull(pTotalFEs);
    this.lastImprovementTime =
        Objects.requireNonNull(pLastImprovementTime);
    this.lastImprovementFE =
        Objects.requireNonNull(pLastImprovementFE);
    this.numberOfImprovements =
        Objects.requireNonNull(pNumberOfImprovements);
    this.budgetTime = Objects.requireNonNull(pBudgetTime);
    this.budgetFEs = Objects.requireNonNull(pBudgetFEs);

    this.successes = pSuccesses;
    if ((this.successes < 0) || (this.successes > this.runs)) {
      throw new IllegalArgumentException(
          "Invalid number of successes " + this.successes //$NON-NLS-1$
              + " for number of runs " + this.runs);//$NON-NLS-1$
    }

    this.ertTime = pErtTime;
    if ((Double.isFinite(this.ertTime)
        ^ (this.ertTime < Double.POSITIVE_INFINITY))
        || (this.ertTime < 0d)) {
      throw new IllegalArgumentException(
          "Invalid time ERT: " + this.ertTime); //$NON-NLS-1$
    }
    if ((this.successes == 0) != (this.ertTime >= Double.POSITIVE_INFINITY)) {
      throw new IllegalArgumentException(
          "Invalid time ERT " + this.ertTime //$NON-NLS-1$
              + " for number of successes " + this.successes); //$NON-NLS-1$
    }

    this.ertFEs = pErtFEs;
    if ((Double.isFinite(this.ertFEs)
        ^ (this.ertFEs < Double.POSITIVE_INFINITY))
        || (this.ertFEs < 1d)) {
      throw new IllegalArgumentException(
          "Invalid FE ERT: " + this.ertFEs); //$NON-NLS-1$
    }
    if ((this.successes == 0) != (this.ertFEs >= Double.POSITIVE_INFINITY)) {
      throw new IllegalArgumentException(
          "Invalid FE ERT " + this.ertFEs //$NON-NLS-1$
              + " for number of successes " + this.successes); //$NON-NLS-1$
    }

    if (this.successes > 0) {
      this.successFEs = Objects.requireNonNull(pSuccessFEs);
      this.successTime = Objects.requireNonNull(pSuccessTime);

      if (this.successFEs.max > this.lastImprovementFE.max) {
        throw new IllegalArgumentException(//
            "The maximum FE until success (" //$NON-NLS-1$
                + this.successFEs.max//
                + ") cannot be more than the maximum last improvement FE ("//$NON-NLS-1$
                + this.lastImprovementFE.max//
                + ").");//$NON-NLS-1$
      }
      if (this.successTime.max > this.lastImprovementTime.max) {
        throw new IllegalArgumentException(//
            "The maximum time until success (" //$NON-NLS-1$
                + this.successTime.max//
                + ") cannot be more than the maximum last improvement time ("//$NON-NLS-1$
                + this.lastImprovementTime.max//
                + ").");//$NON-NLS-1$
      }
    } else {
      if ((pSuccessTime != null) || (pSuccessFEs != null)) {
        throw new IllegalArgumentException(
            "If no run is successful, we also cannot have statistics about the time to success."); //$NON-NLS-1$
      }
      this.successFEs = null;
      this.successTime = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(final EndResultStatistic o) {
    if (o == this) {
      return 0;
    }

    int res = this.algorithm.compareTo(o.algorithm);
    if (res != 0) {
      return res;
    }
    res = this.instance.compareTo(o.algorithm);
    if (res != 0) {
      return res;
    }
    res = this.bestF.compareTo(o.bestF);
    if (res != 0) {
      return res;
    }
    res = this.totalFEs.compareTo(o.totalFEs);
    if (res != 0) {
      return res;
    }
    res = this.totalTime.compareTo(o.totalTime);
    if (res != 0) {
      return res;
    }
    res = this.lastImprovementFE.compareTo(o.lastImprovementFE);
    if (res != 0) {
      return res;
    }
    res = this.lastImprovementTime
        .compareTo(o.lastImprovementTime);
    if (res != 0) {
      return res;
    }
    res = this.budgetFEs.compareTo(o.budgetFEs);
    if (res != 0) {
      return res;
    }
    res = this.budgetTime.compareTo(o.budgetTime);
    if (res != 0) {
      return res;
    }
    res = Double.compare(this.ertFEs, o.ertFEs);
    if (res != 0) {
      return res;
    }
    res = Double.compare(this.ertTime, o.ertTime);
    if (res != 0) {
      return res;
    }
    res = o.numberOfImprovements
        .compareTo(this.numberOfImprovements);
    if (res != 0) {
      return res;
    }
    res = Double.compare(((o.successes) / ((double) o.runs)),
        this.successes / ((double) this.runs));
    if (res != 0) {
      return res;
    }
    res = Integer.compare(this.successes, o.successes);
    if (res != 0) {
      return res;
    }
    return Integer.compare(o.runs, this.runs);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int hc = this.algorithm.hashCode();
    hc = (31 * hc) + this.instance.hashCode();
    hc = (31 * hc) + Integer.hashCode(this.runs);
    hc = (31 * hc) + this.bestF.hashCode();
    hc = (31 * hc) + this.totalTime.hashCode();
    hc = (31 * hc) + this.totalFEs.hashCode();
    hc = (31 * hc) + this.lastImprovementTime.hashCode();
    hc = (31 * hc) + this.lastImprovementTime.hashCode();
    hc = (31 * hc) + this.numberOfImprovements.hashCode();
    hc = (31 * hc) + this.budgetTime.hashCode();
    hc = (31 * hc) + this.budgetFEs.hashCode();
    hc = (31 * hc) + Integer.hashCode(this.successes);
    hc = (31 * hc) + Double.hashCode(this.ertTime);
    hc = (31 * hc) + Double.hashCode(this.ertFEs);
    hc = (31 * hc) + Objects.hashCode(this.successTime);
    hc = (31 * hc) + Objects.hashCode(this.successFEs);
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof EndResultStatistic) {
      final EndResultStatistic e = ((EndResultStatistic) o);
      return (Objects.equals(this.algorithm, e.algorithm) && //
          Objects.equals(this.instance, e.instance) && //
          (this.runs == e.runs) && //
          DoubleStatisticsBig
              .equalsDoubleStatisticsBig(this.bestF, e.bestF)
          && //
          IntStatisticsBig.equalsIntStatisticsBig(this.totalTime,
              e.totalTime)
          && //
          IntStatisticsBig.equalsIntStatisticsBig(this.totalFEs,
              e.totalFEs)
          && //
          IntStatisticsBig.equalsIntStatisticsBig(
              this.lastImprovementTime, e.lastImprovementTime)
          && //
          IntStatisticsBig.equalsIntStatisticsBig(
              this.lastImprovementFE, e.lastImprovementFE)
          && //
          IntStatisticsBig.equalsIntStatisticsBig(
              this.numberOfImprovements, e.numberOfImprovements)
          && //
          IntStatisticsSmall.equalsIntStatisticsSmall(
              this.budgetTime, e.budgetTime)
          && //
          IntStatisticsSmall.equalsIntStatisticsSmall(
              this.budgetFEs, e.budgetFEs)
          && //
          (this.successes == e.successes) && //
          (Double.compare(this.ertTime, e.ertTime) == 0) && //
          (Double.compare(this.ertFEs, e.ertFEs) == 0));
    }
    return false;
  }

  /** the statistic base */
  private static class StatBase {
    /** the arithmetic mean */
    public final double mean;
    /** the standard deviation */
    public final double sd;
    /** the median */
    public final double median;

    /**
     * create
     *
     * @param pMean
     *          the mean
     * @param pSd
     *          the standard deviation
     * @param pMedian
     *          the median
     */
    StatBase(final double pMean, final double pSd,
        final double pMedian) {
      super();

      this.sd = pSd;
      this.median = pMedian;
      this.mean = ((this.sd != 0d) ? pMean : this.median);

      if (!Double.isFinite(this.mean)) {
        throw new IllegalArgumentException(
            "Invalid mean: " + this.mean); //$NON-NLS-1$
      }

      if ((!Double.isFinite(this.sd)) || (pSd < 0d)) {
        throw new IllegalArgumentException(
            "Invalid standard deviation: " + this.sd); //$NON-NLS-1$
      }

      if (!Double.isFinite(this.median)) {
        throw new IllegalArgumentException(
            "Invalid median: " + this.median); //$NON-NLS-1$
      }

      if (this.sd <= 0d) {
        if (pMean != pMedian) {
          throw new IllegalArgumentException(//
              "If sd=0, then mean "//$NON-NLS-1$
                  + pMean + " should equal median "//$NON-NLS-1$
                  + pMedian + "; difference " + //$NON-NLS-1$
                  (pMean - pMedian));
        }
      }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
      int hc = Double.hashCode(this.mean);
      hc = (31 * hc) + Double.hashCode(this.sd);
      hc = (31 * hc) + Double.hashCode(this.median);
      return hc;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
      return (o != null)
          && (Objects.equals(this.getClass(), o.getClass()))
          && StatBase.equalsStatBase(this, (StatBase) o);
    }

    /**
     * check for equality
     *
     * @param a
     *          the first object
     * @param b
     *          the other object
     * @return {@code true} if they are equal, {@code false} if
     *         not
     */
    static final boolean equalsStatBase(final StatBase a,
        final StatBase b) {
      return (Double.compare(a.mean, b.mean) == 0)
          && (Double.compare(a.sd, b.sd) == 0)
          && (Double.compare(a.median, b.median) == 0);
    }
  }

  /** the statistic quantiles */
  private static class StatQuantiles extends StatBase {
    /** the 5% quantile */
    public final double q050;
    /** the 15.9% quantile */
    public final double q159;
    /** the 25% quantile */
    public final double q250;
    /** the 75% quantile */
    public final double q750;
    /** the 84.1% quantile */
    public final double q841;
    /** the 95% quantile */
    public final double q950;

    /**
     * create
     *
     * @param pQ050
     *          the 5% quantile
     * @param pQ159
     *          the 15.9% quantile
     * @param pQ250
     *          the 25% quantile
     * @param pQ750
     *          the 75% quantile
     * @param pQ841
     *          the 84.1% quantile
     * @param pQ950
     *          the 95% quantile
     * @param pMean
     *          the mean
     * @param pSd
     *          the standard deviation
     * @param pMedian
     *          the median
     */
    StatQuantiles(final double pQ050, final double pQ159,
        final double pQ250, final double pMedian,
        final double pQ750, final double pQ841,
        final double pQ950, final double pMean,
        final double pSd) {
      super(pMean, pSd, pMedian);

      this.q050 = pQ050;
      if (!Double.isFinite(pQ050)) {
        throw new IllegalArgumentException(
            "Invalid q050: " + this.q050); //$NON-NLS-1$
      }

      this.q159 = pQ159;
      if ((!Double.isFinite(this.q159))
          || (this.q159 < this.q050)) {
        throw new IllegalArgumentException(
            "Invalid q159: " + this.q159 //$NON-NLS-1$
                + " for q050: " + this.q050);//$NON-NLS-1$
      }

      this.q250 = pQ250;
      if ((!Double.isFinite(this.q250))
          || (this.q250 < this.q159)) {
        throw new IllegalArgumentException(
            "Invalid q250: " + this.q250 //$NON-NLS-1$
                + " for q159: " + this.q159);//$NON-NLS-1$
      }

      if (this.median < this.q250) {
        throw new IllegalArgumentException(
            "Invalid median: " + this.median //$NON-NLS-1$
                + " for q250: " + this.q250);//$NON-NLS-1$
      }

      this.q750 = pQ750;
      if ((!Double.isFinite(this.q750))
          || (this.q750 < this.median)) {
        throw new IllegalArgumentException(
            "Invalid q750: " + this.q750 //$NON-NLS-1$
                + " for median: " + this.median);//$NON-NLS-1$
      }

      this.q841 = pQ841;
      if ((!Double.isFinite(this.q841))
          || (this.q841 < this.q159)) {
        throw new IllegalArgumentException(
            "Invalid q841: " + this.q841 //$NON-NLS-1$
                + " for q750: " + this.q750);//$NON-NLS-1$
      }

      this.q950 = pQ950;
      if ((!Double.isFinite(this.q950))
          || (this.q950 < this.q841)) {
        throw new IllegalArgumentException(
            "Invalid q950: " + this.q950 //$NON-NLS-1$
                + " for q841: " + this.q841);//$NON-NLS-1$
      }

      if ((this.sd <= 0d) && (this.q950 != this.q050)) {
        throw new IllegalArgumentException(//
            "If sd=0, then q050 "//$NON-NLS-1$
                + this.q050 + " should equal q950 "//$NON-NLS-1$
                + this.q950);
      }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + Double.hashCode(this.q050);
      hc = (31 * hc) + Double.hashCode(this.q159);
      hc = (31 * hc) + Double.hashCode(this.q250);
      hc = (31 * hc) + Double.hashCode(this.q750);
      hc = (31 * hc) + Double.hashCode(this.q841);
      hc = (31 * hc) + Double.hashCode(this.q950);
      return hc;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
      return (o != null)
          && (Objects.equals(this.getClass(), o.getClass()))
          && StatQuantiles.equalsStatQuantiles(this,
              (StatQuantiles) o);
    }

    /**
     * check for equality
     *
     * @param a
     *          the first object
     * @param b
     *          the other object
     * @return {@code true} if they are equal, {@code false} if
     *         not
     */
    static final boolean equalsStatQuantiles(
        final StatQuantiles a, final StatQuantiles b) {
      return StatBase.equalsStatBase(a, b)
          && (Double.compare(a.q050, b.q050) == 0)
          && (Double.compare(a.q159, b.q159) == 0)
          && (Double.compare(a.q250, b.q250) == 0)
          && (Double.compare(a.q750, b.q750) == 0)
          && (Double.compare(a.q841, b.q841) == 0)
          && (Double.compare(a.q950, b.q950) == 0);
    }
  }

  /** the integer statistics */
  public static final class IntStatisticsBig extends
      StatQuantiles implements Comparable<IntStatisticsBig> {
    /** the minimum */
    public final long min;
    /** the maximum */
    public final long max;

    /**
     * create
     *
     * @param pMin
     *          the minimum
     * @param pQ050
     *          the 5% quantile
     * @param pQ159
     *          the 15.9% quantile
     * @param pQ250
     *          the 25% quantile
     * @param pQ750
     *          the 75% quantile
     * @param pQ841
     *          the 84.1% quantile
     * @param pQ950
     *          the 95% quantile
     * @param pMax
     *          the maximum
     * @param pMean
     *          the mean
     * @param pSd
     *          the standard deviation
     * @param pMedian
     *          the median
     */
    public IntStatisticsBig(final long pMin, final double pQ050,
        final double pQ159, final double pQ250,
        final double pMedian, final double pQ750,
        final double pQ841, final double pQ950, final long pMax,
        final double pMean, final double pSd) {
      super(pQ050, pQ159, pQ250, pMedian, pQ750, pQ841, pQ950,
          pMean, pSd);

      this.min = pMin;
      if (this.min > this.q050) {
        throw new IllegalArgumentException(
            "Invalid minimum: " + this.min //$NON-NLS-1$
                + " for q050: " + this.q050);//$NON-NLS-1$
      }

      this.max = pMax;
      if (this.max < this.q950) {
        throw new IllegalArgumentException(
            "Invalid maximum: " + this.max //$NON-NLS-1$
                + " for q950: " + this.q950);//$NON-NLS-1$
      }

      if ((this.sd <= 0d) != (this.max <= this.min)) {
        throw new IllegalArgumentException(
            (((("Invalid min/max/sd: "//$NON-NLS-1$
                + this.min) + '/') + this.max) + '/') + this.sd);
      }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + Long.hashCode(this.min);
      hc = (31 * hc) + Long.hashCode(this.max);
      return hc;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final IntStatisticsBig o) {
      if (o == this) {
        return 0;
      }
      int res = Double.compare(this.median, o.median);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.mean, o.mean);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q750, o.q750);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q841, o.q841);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q950, o.q950);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q250, o.q250);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q159, o.q159);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q050, o.q050);
      if (res != 0) {
        return res;
      }
      res = Long.compare(this.max, o.max);
      if (res != 0) {
        return res;
      }
      res = Long.compare(this.min, o.min);
      if (res != 0) {
        return res;
      }
      return Double.compare(this.sd, o.sd);
    }

    /**
     * check for equality
     *
     * @param a
     *          the first object
     * @param b
     *          the other object
     * @return {@code true} if they are equal, {@code false} if
     *         not
     */
    static boolean equalsIntStatisticsBig(
        final IntStatisticsBig a, final IntStatisticsBig b) {
      return StatQuantiles.equalsStatQuantiles(a, b)
          && (a.min == b.min) && (a.max == b.max);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof IntStatisticsBig) {
        return IntStatisticsBig.equalsIntStatisticsBig(this,
            ((IntStatisticsBig) o));
      }
      return false;
    }
  }

  /** the double statistics */
  public static final class DoubleStatisticsBig extends
      StatQuantiles implements Comparable<DoubleStatisticsBig> {

    /** the minimum */
    public final double min;
    /** the maximum */
    public final double max;

    /** the minimum setup */
    public final Setup minSetup;
    /** the 5% quantile setup */
    public final Setup q050Setup;
    /** the 15.9% quantile setup */
    public final Setup q159Setup;
    /** the 25% quantile setup */
    public final Setup q250Setup;
    /** the median setup */
    public final Setup medianSetup;
    /** the 75% quantile setup */
    public final Setup q750Setup;
    /** the 84.1% quantile setup */
    public final Setup q841Setup;
    /** the 95% quantile setup */
    public final Setup q950Setup;
    /** the minimum setup setup */
    public final Setup maxSetup;
    /** the mean setup setup */
    public final Setup meanSetup;

    /**
     * create
     *
     * @param pMin
     *          the minimum
     * @param pMinSetup
     *          the minimum setup
     * @param pQ050
     *          the 5% quantile
     * @param pQ050Setup
     *          the 5% quantile setup
     * @param pQ159
     *          the 15.9% quantile
     * @param pQ159Setup
     *          the 15.9% quantile setup
     * @param pQ250
     *          the 25% quantile
     * @param pQ250Setup
     *          the 25% quantile setup
     * @param pMedianSetup
     *          the median setup
     * @param pQ750
     *          the 75% quantile
     * @param pQ750Setup
     *          the 75% quantile setup
     * @param pQ841
     *          the 84.1% quantile
     * @param pQ841Setup
     *          the 84.1% quantile setup
     * @param pQ950
     *          the 95% quantile
     * @param pQ950Setup
     *          the 95% quantile setup
     * @param pMax
     *          the maximum
     * @param pMaxSetup
     *          the maximum setup
     * @param pMean
     *          the mean
     * @param pMeanSetup
     *          the mean setup
     * @param pSd
     *          the standard deviation
     * @param pMedian
     *          the median
     */
    public DoubleStatisticsBig(//
        final double pMin, //
        final Setup pMinSetup, //
        final double pQ050, //
        final Setup pQ050Setup, //
        final double pQ159, //
        final Setup pQ159Setup, //
        final double pQ250, //
        final Setup pQ250Setup, //
        final double pMedian, //
        final Setup pMedianSetup, //
        final double pQ750, //
        final Setup pQ750Setup, //
        final double pQ841, //
        final Setup pQ841Setup, //
        final double pQ950, //
        final Setup pQ950Setup, //
        final double pMax, //
        final Setup pMaxSetup, //
        final double pMean, //
        final Setup pMeanSetup, //
        final double pSd) {
      super(pQ050, pQ159, pQ250, pMedian, pQ750, pQ841, pQ950,
          pMean, pSd);

      this.min = pMin;
      if ((!Double.isFinite(this.min))
          || (this.min > this.q050)) {
        throw new IllegalArgumentException(
            "Invalid minimum: " + this.min //$NON-NLS-1$
                + " for q050: " + this.q050);//$NON-NLS-1$
      }

      this.max = pMax;
      if ((!Double.isFinite(this.max))
          || (this.max < this.q950)) {
        throw new IllegalArgumentException(
            "Invalid maximum: " + this.max //$NON-NLS-1$
                + " for q950: " + this.q950);//$NON-NLS-1$
      }

      if ((this.sd <= 0d) != (this.max <= this.min)) {
        throw new IllegalArgumentException(
            (((("Invalid min/max/sd: "//$NON-NLS-1$
                + this.min) + '/') + this.max) + '/') + this.sd);
      }

      this.minSetup = Objects.requireNonNull(pMinSetup);
      this.q050Setup = Objects.requireNonNull(pQ050Setup);
      this.q159Setup = Objects.requireNonNull(pQ159Setup);
      this.q250Setup = Objects.requireNonNull(pQ250Setup);
      this.medianSetup = Objects.requireNonNull(pMedianSetup);
      this.q750Setup = Objects.requireNonNull(pQ750Setup);
      this.q841Setup = Objects.requireNonNull(pQ841Setup);
      this.q950Setup = Objects.requireNonNull(pQ950Setup);
      this.maxSetup = Objects.requireNonNull(pMaxSetup);
      this.meanSetup = Objects.requireNonNull(pMeanSetup);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + Double.hashCode(this.min);
      hc = (31 * hc) + Double.hashCode(this.max);
      hc = (31 * hc) + this.minSetup.hashCode();
      hc = (31 * hc) + this.q050Setup.hashCode();
      hc = (31 * hc) + this.q159Setup.hashCode();
      hc = (31 * hc) + this.q250Setup.hashCode();
      hc = (31 * hc) + this.medianSetup.hashCode();
      hc = (31 * hc) + this.q750Setup.hashCode();
      hc = (31 * hc) + this.q841Setup.hashCode();
      hc = (31 * hc) + this.q950Setup.hashCode();
      hc = (31 * hc) + this.maxSetup.hashCode();
      hc = (31 * hc) + this.meanSetup.hashCode();
      return hc;
    }

    /**
     * check for equality
     *
     * @param a
     *          the first object
     * @param b
     *          the other object
     * @return {@code true} if they are equal, {@code false} if
     *         not
     */
    static boolean equalsDoubleStatisticsBig(
        final DoubleStatisticsBig a,
        final DoubleStatisticsBig b) {
      return StatQuantiles.equalsStatQuantiles(a, b)//
          && (Double.compare(a.min, b.min) == 0)//
          && (Double.compare(a.max, b.max) == 0) //
          && a.minSetup.equals(b.minSetup)//
          && a.q050Setup.equals(b.q050Setup)//
          && a.q159Setup.equals(b.q159Setup)//
          && a.q250Setup.equals(b.q250Setup)//
          && a.medianSetup.equals(b.medianSetup)//
          && a.q750Setup.equals(b.q750Setup)//
          && a.q841Setup.equals(b.q841Setup)//
          && a.q950Setup.equals(b.q950Setup)//
          && a.maxSetup.equals(b.maxSetup)//
          && a.meanSetup.equals(b.meanSetup);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof DoubleStatisticsBig) {
        return DoubleStatisticsBig.equalsDoubleStatisticsBig(
            this, ((DoubleStatisticsBig) o));
      }
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final DoubleStatisticsBig o) {
      if (o == this) {
        return 0;
      }
      int res = Double.compare(this.median, o.median);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.mean, o.mean);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q750, o.q750);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q841, o.q841);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q950, o.q950);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q250, o.q250);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q159, o.q159);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.q050, o.q050);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.max, o.max);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.min, o.min);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.sd, o.sd);
      if (res != 0) {
        return res;
      }

      res = this.minSetup.compareTo(o.minSetup);
      if (res != 0) {
        return res;
      }
      res = this.q050Setup.compareTo(o.q050Setup);
      if (res != 0) {
        return res;
      }
      res = this.q159Setup.compareTo(o.q159Setup);
      if (res != 0) {
        return res;
      }
      res = this.q250Setup.compareTo(o.q250Setup);
      if (res != 0) {
        return res;
      }
      res = this.medianSetup.compareTo(o.medianSetup);
      if (res != 0) {
        return res;
      }
      res = this.q750Setup.compareTo(o.q750Setup);
      if (res != 0) {
        return res;
      }
      res = this.q841Setup.compareTo(o.q841Setup);
      if (res != 0) {
        return res;
      }
      res = this.q950Setup.compareTo(o.q950Setup);
      if (res != 0) {
        return res;
      }
      res = this.maxSetup.compareTo(o.maxSetup);
      if (res != 0) {
        return res;
      }
      return this.meanSetup.compareTo(o.meanSetup);
    }
  }

  /** the integer statistics */
  public static class IntStatisticsSmall extends StatBase
      implements Comparable<IntStatisticsSmall> {
    /** the minimum */
    public final long min;
    /** the maximum */
    public final long max;

    /**
     * create
     *
     * @param pMin
     *          the minimum
     * @param pMedian
     *          the median
     * @param pMax
     *          the maximum
     * @param pMean
     *          the mean
     * @param pSd
     *          the standard deviation
     */
    public IntStatisticsSmall(final long pMin,
        final double pMedian, final long pMax,
        final double pMean, final double pSd) {
      super(pMean, pSd, pMedian);

      this.min = pMin;
      if (this.min > this.median) {
        throw new IllegalArgumentException(
            "Invalid minimum: " + this.min //$NON-NLS-1$
                + " for median: " + this.median);//$NON-NLS-1$
      }

      this.max = pMax;
      if (this.max < this.median) {
        throw new IllegalArgumentException(
            "Invalid maximum: " + this.max //$NON-NLS-1$
                + " for median: " + this.median);//$NON-NLS-1$
      }

      if ((this.sd <= 0d) != (this.max <= this.min)) {
        throw new IllegalArgumentException(
            (((("Invalid min/max/sd: "//$NON-NLS-1$
                + this.min) + '/') + this.max) + '/') + this.sd);
      }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + Long.hashCode(this.min);
      hc = (31 * hc) + Long.hashCode(this.max);
      return hc;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final IntStatisticsSmall o) {
      if (o == this) {
        return 0;
      }
      int res = Double.compare(this.median, o.median);
      if (res != 0) {
        return res;
      }
      res = Double.compare(this.mean, o.mean);
      if (res != 0) {
        return res;
      }

      res = Long.compare(this.max, o.max);
      if (res != 0) {
        return res;
      }
      res = Long.compare(this.min, o.min);
      if (res != 0) {
        return res;
      }
      return Double.compare(this.sd, o.sd);
    }

    /**
     * check for equality
     *
     * @param a
     *          the first object
     * @param b
     *          the other object
     * @return {@code true} if they are equal, {@code false} if
     *         not
     */
    static final boolean equalsIntStatisticsSmall(
        final IntStatisticsSmall a, final IntStatisticsSmall b) {
      return StatBase.equalsStatBase(a, b) && (a.min == b.min)
          && (a.max == b.max);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof IntStatisticsSmall) {
        return IntStatisticsSmall.equalsIntStatisticsSmall(this,
            ((IntStatisticsSmall) o));
      }
      return false;
    }
  }

  /** the integer statistics */
  public static class IntStatisticsSmallWithSetups
      extends IntStatisticsSmall {
    /** the setup where the minimum value was achieved */
    public final Setup minSetup;
    /** the setup where the maximum value was achieved */
    public final Setup maxSetup;

    /**
     * create
     *
     * @param pMin
     *          the minimum
     * @param pMinSetup
     *          the setup where the minimum value was achieved
     * @param pMedian
     *          the median
     * @param pMax
     *          the maximum
     * @param pMaxSetup
     *          the setup where the maximum value was achieved
     * @param pMean
     *          the mean
     * @param pSd
     *          the standard deviation
     */
    public IntStatisticsSmallWithSetups(final long pMin,
        final Setup pMinSetup, final double pMedian,
        final long pMax, final Setup pMaxSetup,
        final double pMean, final double pSd) {
      super(pMin, pMedian, pMax, pMean, pSd);
      this.minSetup = Objects.requireNonNull(pMinSetup);
      this.maxSetup = Objects.requireNonNull(pMaxSetup);
    }

    /** {@inheritDoc} */
    @Override
    public final int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + this.minSetup.hashCode();
      hc = (31 * hc) + this.maxSetup.hashCode();
      return hc;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final IntStatisticsSmall o) {
      if (o == this) {
        return 0;
      }
      int res = super.compareTo(o);
      if ((res != 0)
          || (!(o instanceof IntStatisticsSmallWithSetups))) {
        return res;
      }

      final IntStatisticsSmallWithSetups x =
          ((IntStatisticsSmallWithSetups) o);
      res = this.minSetup.compareTo(x.minSetup);
      if (res != 0) {
        return res;
      }

      return this.maxSetup.compareTo(x.maxSetup);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof IntStatisticsSmallWithSetups) {
        final IntStatisticsSmallWithSetups x =
            ((IntStatisticsSmallWithSetups) o);
        return IntStatisticsSmall.equalsIntStatisticsSmall(this,
            x)//
            && this.minSetup.equals(x.minSetup) //
            && this.maxSetup.equals(x.maxSetup);
      }
      return false;
    }
  }
}

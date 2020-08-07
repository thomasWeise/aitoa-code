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
   * @param _algorithm
   *          the algorithm name
   * @param _instance
   *          the instance name
   * @param _runs
   *          the number of runs
   * @param _bestF
   *          the best f record
   * @param _totalTime
   *          the total time record
   * @param _totalFEs
   *          the total fes record
   * @param _lastImprovementTime
   *          the last improvement time record
   * @param _lastImprovementFE
   *          the last improvement fe record
   * @param _numberOfImprovements
   *          the number of improvements record
   * @param _budgetTime
   *          the budget time record
   * @param _budgetFEs
   *          the budget fes record
   * @param _successes
   *          the number of successes
   * @param _ertTime
   *          the expected running time in ms
   * @param _ertFEs
   *          the expected running time in FEs
   * @param _successTime
   *          statistics regarding the last improvement time of
   *          successful runs, or {@code null} if no run was
   *          successful
   * @param _successFEs
   *          statistics regarding the last improvement FEs of
   *          successful runs, or {@code null} if no run was
   *          successful
   */
  public EndResultStatistic(final String _algorithm,
      final String _instance, final int _runs,
      final DoubleStatisticsBig _bestF,
      final IntStatisticsBig _totalTime,
      final IntStatisticsBig _totalFEs,
      final IntStatisticsBig _lastImprovementTime,
      final IntStatisticsBig _lastImprovementFE,
      final IntStatisticsBig _numberOfImprovements,
      final IntStatisticsSmall _budgetTime,
      final IntStatisticsSmall _budgetFEs, final int _successes, //
      final double _ertTime, //
      final double _ertFEs, //
      final IntStatisticsSmallWithSetups _successTime, //
      final IntStatisticsSmallWithSetups _successFEs) {
    super();

    this.algorithm = _algorithm.trim();
    if (this.algorithm.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have '" + _algorithm + //$NON-NLS-1$
              "' as algorithm name.");//$NON-NLS-1$
    }

    this.instance = _instance.trim();
    if (this.instance.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have '" + _instance + //$NON-NLS-1$
              "' as instance name.");//$NON-NLS-1$
    }

    this.runs = _runs;
    if (this.runs <= 0) {
      throw new IllegalArgumentException(
          "Number of runs must be positive, but is "//$NON-NLS-1$
              + this.runs);
    }

    this.bestF = Objects.requireNonNull(_bestF);
    this.totalTime = Objects.requireNonNull(_totalTime);
    this.totalFEs = Objects.requireNonNull(_totalFEs);
    this.lastImprovementTime =
        Objects.requireNonNull(_lastImprovementTime);
    this.lastImprovementFE =
        Objects.requireNonNull(_lastImprovementFE);
    this.numberOfImprovements =
        Objects.requireNonNull(_numberOfImprovements);
    this.budgetTime = Objects.requireNonNull(_budgetTime);
    this.budgetFEs = Objects.requireNonNull(_budgetFEs);

    this.successes = _successes;
    if ((this.successes < 0) || (this.successes > this.runs)) {
      throw new IllegalArgumentException(
          "Invalid number of successes " + this.successes //$NON-NLS-1$
              + " for number of runs " + this.runs);//$NON-NLS-1$
    }

    this.ertTime = _ertTime;
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

    this.ertFEs = _ertFEs;
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
      this.successFEs = Objects.requireNonNull(_successFEs);
      this.successTime = Objects.requireNonNull(_successTime);

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
      if ((_successTime != null) || (_successFEs != null)) {
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
              ._equalsDoubleStatisticsBig(this.bestF, e.bestF)
          && //
          IntStatisticsBig._equalsIntStatisticsBig(
              this.totalTime, e.totalTime)
          && //
          IntStatisticsBig._equalsIntStatisticsBig(this.totalFEs,
              e.totalFEs)
          && //
          IntStatisticsBig._equalsIntStatisticsBig(
              this.lastImprovementTime, e.lastImprovementTime)
          && //
          IntStatisticsBig._equalsIntStatisticsBig(
              this.lastImprovementFE, e.lastImprovementFE)
          && //
          IntStatisticsBig._equalsIntStatisticsBig(
              this.numberOfImprovements, e.numberOfImprovements)
          && //
          IntStatisticsSmall._equalsIntStatisticsSmall(
              this.budgetTime, e.budgetTime)
          && //
          IntStatisticsSmall._equalsIntStatisticsSmall(
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
     * @param _mean
     *          the mean
     * @param _sd
     *          the standard deviation
     * @param _median
     *          the median
     */
    StatBase(final double _mean, final double _sd,
        final double _median) {
      super();

      this.sd = _sd;
      this.median = _median;
      this.mean = ((this.sd != 0d) ? _mean : this.median);

      if (!Double.isFinite(this.mean)) {
        throw new IllegalArgumentException(
            "Invalid mean: " + this.mean); //$NON-NLS-1$
      }

      if ((!Double.isFinite(this.sd)) || (_sd < 0d)) {
        throw new IllegalArgumentException(
            "Invalid standard deviation: " + this.sd); //$NON-NLS-1$
      }

      if (!Double.isFinite(this.median)) {
        throw new IllegalArgumentException(
            "Invalid median: " + this.median); //$NON-NLS-1$
      }

      if (this.sd <= 0d) {
        if (_mean != _median) {
          throw new IllegalArgumentException(//
              "If sd=0, then mean "//$NON-NLS-1$
                  + _mean + " should equal median "//$NON-NLS-1$
                  + _median + "; difference " + //$NON-NLS-1$
                  (_mean - _median));
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
    static final boolean _equalsStatBase(final StatBase a,
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
     * @param _q050
     *          the 5% quantile
     * @param _q159
     *          the 15.9% quantile
     * @param _q250
     *          the 25% quantile
     * @param _q750
     *          the 75% quantile
     * @param _q841
     *          the 84.1% quantile
     * @param _q950
     *          the 95% quantile
     * @param _mean
     *          the mean
     * @param _sd
     *          the standard deviation
     * @param _median
     *          the median
     */
    StatQuantiles(final double _q050, final double _q159,
        final double _q250, final double _median,
        final double _q750, final double _q841,
        final double _q950, final double _mean,
        final double _sd) {
      super(_mean, _sd, _median);

      this.q050 = _q050;
      if (!Double.isFinite(_q050)) {
        throw new IllegalArgumentException(
            "Invalid q050: " + this.q050); //$NON-NLS-1$
      }

      this.q159 = _q159;
      if ((!Double.isFinite(this.q159))
          || (this.q159 < this.q050)) {
        throw new IllegalArgumentException(
            "Invalid q159: " + this.q159 //$NON-NLS-1$
                + " for q050: " + this.q050);//$NON-NLS-1$
      }

      this.q250 = _q250;
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

      this.q750 = _q750;
      if ((!Double.isFinite(this.q750))
          || (this.q750 < this.median)) {
        throw new IllegalArgumentException(
            "Invalid q750: " + this.q750 //$NON-NLS-1$
                + " for median: " + this.median);//$NON-NLS-1$
      }

      this.q841 = _q841;
      if ((!Double.isFinite(this.q841))
          || (this.q841 < this.q159)) {
        throw new IllegalArgumentException(
            "Invalid q841: " + this.q841 //$NON-NLS-1$
                + " for q750: " + this.q750);//$NON-NLS-1$
      }

      this.q950 = _q950;
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
    static final boolean _equalsStatQuantiles(
        final StatQuantiles a, final StatQuantiles b) {
      return StatBase._equalsStatBase(a, b)
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
     * @param _min
     *          the minimum
     * @param _q050
     *          the 5% quantile
     * @param _q159
     *          the 15.9% quantile
     * @param _q250
     *          the 25% quantile
     * @param _q750
     *          the 75% quantile
     * @param _q841
     *          the 84.1% quantile
     * @param _q950
     *          the 95% quantile
     * @param _max
     *          the maximum
     * @param _mean
     *          the mean
     * @param _sd
     *          the standard deviation
     * @param _median
     *          the median
     */
    public IntStatisticsBig(final long _min, final double _q050,
        final double _q159, final double _q250,
        final double _median, final double _q750,
        final double _q841, final double _q950, final long _max,
        final double _mean, final double _sd) {
      super(_q050, _q159, _q250, _median, _q750, _q841, _q950,
          _mean, _sd);

      this.min = _min;
      if (this.min > this.q050) {
        throw new IllegalArgumentException(
            "Invalid minimum: " + this.min //$NON-NLS-1$
                + " for q050: " + this.q050);//$NON-NLS-1$
      }

      this.max = _max;
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
    static boolean _equalsIntStatisticsBig(
        final IntStatisticsBig a, final IntStatisticsBig b) {
      return StatQuantiles._equalsStatQuantiles(a, b)
          && (a.min == b.min) && (a.max == b.max);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof IntStatisticsBig) {
        return IntStatisticsBig._equalsIntStatisticsBig(this,
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
     * @param _min
     *          the minimum
     * @param _minSetup
     *          the minimum setup
     * @param _q050
     *          the 5% quantile
     * @param _q050Setup
     *          the 5% quantile setup
     * @param _q159
     *          the 15.9% quantile
     * @param _q159Setup
     *          the 15.9% quantile setup
     * @param _q250
     *          the 25% quantile
     * @param _q250Setup
     *          the 25% quantile setup
     * @param _medianSetup
     *          the median setup
     * @param _q750
     *          the 75% quantile
     * @param _q750Setup
     *          the 75% quantile setup
     * @param _q841
     *          the 84.1% quantile
     * @param _q841Setup
     *          the 84.1% quantile setup
     * @param _q950
     *          the 95% quantile
     * @param _q950Setup
     *          the 95% quantile setup
     * @param _max
     *          the maximum
     * @param _maxSetup
     *          the maximum setup
     * @param _mean
     *          the mean
     * @param _meanSetup
     *          the mean setup
     * @param _sd
     *          the standard deviation
     * @param _median
     *          the median
     */
    public DoubleStatisticsBig(//
        final double _min, //
        final Setup _minSetup, //
        final double _q050, //
        final Setup _q050Setup, //
        final double _q159, //
        final Setup _q159Setup, //
        final double _q250, //
        final Setup _q250Setup, //
        final double _median, //
        final Setup _medianSetup, //
        final double _q750, //
        final Setup _q750Setup, //
        final double _q841, //
        final Setup _q841Setup, //
        final double _q950, //
        final Setup _q950Setup, //
        final double _max, //
        final Setup _maxSetup, //
        final double _mean, //
        final Setup _meanSetup, //
        final double _sd) {
      super(_q050, _q159, _q250, _median, _q750, _q841, _q950,
          _mean, _sd);

      this.min = _min;
      if ((!Double.isFinite(this.min))
          || (this.min > this.q050)) {
        throw new IllegalArgumentException(
            "Invalid minimum: " + this.min //$NON-NLS-1$
                + " for q050: " + this.q050);//$NON-NLS-1$
      }

      this.max = _max;
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

      this.minSetup = Objects.requireNonNull(_minSetup);
      this.q050Setup = Objects.requireNonNull(_q050Setup);
      this.q159Setup = Objects.requireNonNull(_q159Setup);
      this.q250Setup = Objects.requireNonNull(_q250Setup);
      this.medianSetup = Objects.requireNonNull(_medianSetup);
      this.q750Setup = Objects.requireNonNull(_q750Setup);
      this.q841Setup = Objects.requireNonNull(_q841Setup);
      this.q950Setup = Objects.requireNonNull(_q950Setup);
      this.maxSetup = Objects.requireNonNull(_maxSetup);
      this.meanSetup = Objects.requireNonNull(_meanSetup);
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
    static boolean _equalsDoubleStatisticsBig(
        final DoubleStatisticsBig a,
        final DoubleStatisticsBig b) {
      return StatQuantiles._equalsStatQuantiles(a, b)//
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
        return DoubleStatisticsBig._equalsDoubleStatisticsBig(
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
     * @param _min
     *          the minimum
     * @param _median
     *          the median
     * @param _max
     *          the maximum
     * @param _mean
     *          the mean
     * @param _sd
     *          the standard deviation
     */
    public IntStatisticsSmall(final long _min,
        final double _median, final long _max,
        final double _mean, final double _sd) {
      super(_mean, _sd, _median);

      this.min = _min;
      if (this.min > this.median) {
        throw new IllegalArgumentException(
            "Invalid minimum: " + this.min //$NON-NLS-1$
                + " for median: " + this.median);//$NON-NLS-1$
      }

      this.max = _max;
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
    static final boolean _equalsIntStatisticsSmall(
        final IntStatisticsSmall a, final IntStatisticsSmall b) {
      return StatBase._equalsStatBase(a, b) && (a.min == b.min)
          && (a.max == b.max);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof IntStatisticsSmall) {
        return IntStatisticsSmall._equalsIntStatisticsSmall(this,
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
     * @param _min
     *          the minimum
     * @param _minSetup
     *          the setup where the minimum value was achieved
     * @param _median
     *          the median
     * @param _max
     *          the maximum
     * @param _maxSetup
     *          the setup where the maximum value was achieved
     * @param _mean
     *          the mean
     * @param _sd
     *          the standard deviation
     */
    public IntStatisticsSmallWithSetups(final long _min,
        final Setup _minSetup, final double _median,
        final long _max, final Setup _maxSetup,
        final double _mean, final double _sd) {
      super(_min, _median, _max, _mean, _sd);
      this.minSetup = Objects.requireNonNull(_minSetup);
      this.maxSetup = Objects.requireNonNull(_maxSetup);
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
        return IntStatisticsSmall._equalsIntStatisticsSmall(this,
            x)//
            && this.minSetup.equals(x.minSetup) //
            && this.maxSetup.equals(x.maxSetup);
      }
      return false;
    }
  }
}

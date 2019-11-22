package aitoa.utils.logs;

import java.util.Objects;

/**
 * A line of the end result statistics table, as created by
 * {@link EndResultStatistics}.
 */
public final class EndResultStatistic {
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
      final IntStatisticsSmall _budgetFEs, final int _successes,
      final double _ertTime, final double _ertFEs) {
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
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
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
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
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
  private static class __StatBase {
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
    __StatBase(final double _mean, final double _sd,
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
    static final boolean _equalsStatBase(final __StatBase a,
        final __StatBase b) {
      return (Double.compare(a.mean, b.mean) == 0)
          && (Double.compare(a.sd, b.sd) == 0)
          && (Double.compare(a.median, b.median) == 0);
    }
  }

  /** the statistic quantiles */
  private static class __StatQuantiles extends __StatBase {
    /** the 5% quantile */
    public double q050;
    /** the 15.9% quantile */
    public double q159;
    /** the 25% quantile */
    public double q250;
    /** the 75% quantile */
    public double q750;
    /** the 84.1% quantile */
    public double q841;
    /** the 95% quantile */
    public double q950;

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
    __StatQuantiles(final double _q050, final double _q159,
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
        final __StatQuantiles a, final __StatQuantiles b) {
      return __StatBase._equalsStatBase(a, b)
          && (Double.compare(a.q050, b.q050) == 0)
          && (Double.compare(a.q159, b.q159) == 0)
          && (Double.compare(a.q250, b.q250) == 0)
          && (Double.compare(a.q750, b.q750) == 0)
          && (Double.compare(a.q841, b.q841) == 0)
          && (Double.compare(a.q950, b.q950) == 0);
    }
  }

  /** the integer statistics */
  public static final class IntStatisticsBig
      extends __StatQuantiles {
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
    public final int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + Long.hashCode(this.min);
      hc = (31 * hc) + Long.hashCode(this.max);
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
    static final boolean _equalsIntStatisticsBig(
        final IntStatisticsBig a, final IntStatisticsBig b) {
      return __StatQuantiles._equalsStatQuantiles(a, b)
          && (a.min == b.min) && (a.max == b.max);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
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
  public static final class DoubleStatisticsBig
      extends __StatQuantiles {

    /** the minimum */
    public final double min;
    /** the maximum */
    public final double max;

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
    public DoubleStatisticsBig(final double _min,
        final double _q050, final double _q159,
        final double _q250, final double _median,
        final double _q750, final double _q841,
        final double _q950, final double _max,
        final double _mean, final double _sd) {
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
    }

    /** {@inheritDoc} */
    @Override
    public final int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + Double.hashCode(this.min);
      hc = (31 * hc) + Double.hashCode(this.max);
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
    static final boolean _equalsDoubleStatisticsBig(
        final DoubleStatisticsBig a,
        final DoubleStatisticsBig b) {
      return __StatQuantiles._equalsStatQuantiles(a, b)
          && (Double.compare(a.min, b.min) == 0)
          && (Double.compare(a.max, b.max) == 0);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof DoubleStatisticsBig) {
        return DoubleStatisticsBig._equalsDoubleStatisticsBig(
            this, ((DoubleStatisticsBig) o));
      }
      return false;
    }
  }

  /** the integer statistics */
  public static final class IntStatisticsSmall
      extends __StatBase {
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
    public final int hashCode() {
      int hc = super.hashCode();
      hc = (31 * hc) + Long.hashCode(this.min);
      hc = (31 * hc) + Long.hashCode(this.max);
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
    static final boolean _equalsIntStatisticsSmall(
        final IntStatisticsSmall a, final IntStatisticsSmall b) {
      return __StatBase._equalsStatBase(a, b) && (a.min == b.min)
          && (a.max == b.max);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
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
}
package aitoa.utils.logs;

import java.util.Objects;

/**
 * A line of the end result statistics table, as created by
 * {@link EndResultStatistics}. Notice: Instances of this class,
 * as passed to the consumers by the
 * {@link EndResultStatistics#parseEndResultStatisticsTable(java.nio.file.Path, java.util.function.Consumer, boolean)},
 * are generally mutable and will be re-used by the parser. If
 * you want to store them, you should {@linkplain #clone() copy}
 * them first.
 */
public final class EndResultStatistic implements Cloneable {
  /** the algorithm id */
  public String algorithm;
  /** the instance id */
  public String instance;
  /** the number of runs whose data was collected */
  public int runs;
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
  public int successes;
  /** the empirical expected time to success */
  public double ertTime;
  /** the empirical expected FEs to success */
  public double ertFEs;

  /**
   * create
   *
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
   */
  EndResultStatistic(final DoubleStatisticsBig _bestF,
      final IntStatisticsBig _totalTime,
      final IntStatisticsBig _totalFEs,
      final IntStatisticsBig _lastImprovementTime,
      final IntStatisticsBig _lastImprovementFE,
      final IntStatisticsBig _numberOfImprovements,
      final IntStatisticsSmall _budgetTime,
      final IntStatisticsSmall _budgetFEs) {
    super();
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
  }

  /** create */
  EndResultStatistic() {
    this(new DoubleStatisticsBig(), new IntStatisticsBig(),
        new IntStatisticsBig(), new IntStatisticsBig(),
        new IntStatisticsBig(), new IntStatisticsBig(),
        new IntStatisticsSmall(), new IntStatisticsSmall());
  }

  /** {@inheritDoc} */
  @Override
  public final EndResultStatistic clone() {
    final EndResultStatistic n = new EndResultStatistic(
        this.bestF.clone(), this.totalTime.clone(),
        this.totalFEs.clone(), this.lastImprovementTime.clone(),
        this.lastImprovementFE.clone(),
        this.numberOfImprovements.clone(),
        this.budgetTime.clone(), this.budgetFEs.clone());
    n.algorithm = this.algorithm;
    n.instance = this.instance;
    n.runs = this.runs;
    n.successes = this.successes;
    n.ertTime = this.ertTime;
    n.ertFEs = this.ertFEs;
    return n;
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
    public double mean;
    /** the standard deviation */
    public double sd;
    /** the median */
    public double median;

    /** create */
    __StatBase() {
      super();
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

    /** create */
    __StatQuantiles() {
      super();
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
    public long min;
    /** the maximum */
    public long max;

    /** create */
    IntStatisticsBig() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    public final IntStatisticsBig clone() {
      try {
        return ((IntStatisticsBig) (super.clone()));
      } catch (final Throwable error) {
        throw new RuntimeException(error);
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
      extends __StatQuantiles implements Cloneable {
    /** the minimum */
    public double min;
    /** the maximum */
    public double max;

    /** create */
    DoubleStatisticsBig() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    public final DoubleStatisticsBig clone() {
      try {
        return ((DoubleStatisticsBig) (super.clone()));
      } catch (final Throwable error) {
        throw new RuntimeException(error);
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
    public long min;
    /** the maximum */
    public long max;

    /** create */
    IntStatisticsSmall() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    public final IntStatisticsSmall clone() {
      try {
        return ((IntStatisticsSmall) (super.clone()));
      } catch (final Throwable error) {
        throw new RuntimeException(error);
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
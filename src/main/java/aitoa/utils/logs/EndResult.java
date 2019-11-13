package aitoa.utils.logs;

import java.util.Objects;

/**
 * A line of the end results table, as created by
 * {@link EndResults}. Notice: Instances of this class, as passed
 * to the consumers by the
 * {@link EndResults#parseEndResultsTable(java.nio.file.Path, java.util.function.Consumer, boolean)},
 * are generally mutable and will be re-used by the parser. If
 * you want to store them, you should {@linkplain #clone() copy}
 * them first.
 */
public final class EndResult
    implements Cloneable, Comparable<EndResult> {
  /** create */
  EndResult() {
    super();
  }

  /** the algorithm id */
  public String algorithm;
  /** the instance id */
  public String instance;
  /** the seed */
  public String seed;
  /** the best objective value achieved by the run */
  public double bestF;
  /** the total time consumed by the run */
  public long totalTime;
  /** the total FEs consumed by the run */
  public long totalFEs;
  /**
   * the last time at which an improvement was achieved
   */
  public long lastImprovementTime;
  /** the last FE at which an improvement was achieved */
  public long lastImprovementFE;
  /**
   * the total number of times the run improved its result
   */
  public long numberOfImprovements;
  /** the time budget */
  public long budgetTime;
  /** the FE budget */
  public long budgetFEs;
  /** the goal objective value */
  public double goalF;

  /** {@inheritDoc} */
  @Override
  public final EndResult clone() {
    try {
      return ((EndResult) (super.clone()));
    } catch (final Throwable error) {
      throw new RuntimeException(error);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    int hc = this.algorithm.hashCode();
    hc = (31 * hc) + this.instance.hashCode();
    hc = (31 * hc) + this.seed.hashCode();
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
          Objects.equals(this.seed, e.seed) && //
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
  public final int compareTo(final EndResult o) {
    if (o == this) {
      return 0;
    }

    int r = this.algorithm.compareTo(o.algorithm);
    if (r != 0) {
      return r;
    }
    r = this.instance.compareTo(o.instance);
    if (r != 0) {
      return r;
    }
    r = this.seed.compareTo(o.seed);
    if (r != 0) {
      return r;
    }
    r = Double.compare(this.bestF, o.bestF);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.lastImprovementFE,
        o.lastImprovementFE);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.lastImprovementTime,
        o.lastImprovementTime);
    if (r != 0) {
      return r;
    }
    r = Long.compare(o.numberOfImprovements,
        this.numberOfImprovements);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.totalFEs, o.totalFEs);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.totalTime, o.totalTime);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.budgetFEs, o.budgetFEs);
    if (r != 0) {
      return r;
    }
    r = Long.compare(this.budgetTime, o.budgetTime);
    if (r != 0) {
      return r;
    }
    r = Double.compare(this.goalF, o.goalF);
    if (r != 0) {
      return r;
    }

    return 0;
  }
}
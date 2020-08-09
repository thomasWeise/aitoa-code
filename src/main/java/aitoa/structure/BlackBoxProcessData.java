package aitoa.structure;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.Objects;

import aitoa.utils.RandomUtils;

/**
 * the base class for black box problems
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
abstract class BlackBoxProcessData<X, Y> {
  /** the session start */
  private static final Instant SESSION_START;

  static {
    SESSION_START = Instant.now();
  }

  /** the random number generator seed */
  long m_randSeed;
  /** the search space */
  ISpace<X> m_searchSpace;
  /** the solution space */
  ISpace<Y> m_solutionSpace;
  /** the representation mapping */
  IRepresentationMapping<X, Y> m_mapping;
  /** the objective function */
  IObjectiveFunction<Y> m_f;
  /** the maximum FEs */
  long m_maxFEs;
  /** the maximum time */
  long m_maxTime;
  /** the goal objective value */
  double m_goalF;
  /** the nullary search operator */
  INullarySearchOperator<X> m_nullary;
  /** the unary search operator */
  IUnarySearchOperator<X> m_unary;
  /** the binary search operator */
  IBinarySearchOperator<X> m_binary;
  /** the ternary search operator */
  ITernarySearchOperator<X> m_ternary;

  /** Create the base class of the black box problem */
  BlackBoxProcessData() {
    super();
    this.m_goalF = Double.NEGATIVE_INFINITY;
    this.m_maxFEs = Long.MAX_VALUE;
    this.m_maxTime = Long.MAX_VALUE;
  }

  /**
   * Create the base class of the black box problem by copying
   * another instance
   *
   * @param copy
   *          the instance to copy
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  BlackBoxProcessData(final BlackBoxProcessData<X, Y> copy) {
    super();
    this.m_searchSpace =
        Objects.requireNonNull(copy.m_searchSpace);
    this.m_solutionSpace = (((copy.m_solutionSpace == null)
        || (copy.m_solutionSpace.equals(this.m_searchSpace)))
            ? ((ISpace) this.m_searchSpace)
            : copy.m_solutionSpace);
    if (this.m_searchSpace.equals(this.m_solutionSpace)) {
      this.m_mapping = copy.m_mapping;
    } else {
      this.m_mapping = Objects.requireNonNull(copy.m_mapping);
    }
    this.m_f = Objects.requireNonNull(copy.m_f);
    this.m_maxFEs =
        BlackBoxProcessData.checkMaxFEs(copy.m_maxFEs);
    this.m_maxTime =
        BlackBoxProcessData.checkMaxTime(copy.m_maxTime);
    this.m_goalF = BlackBoxProcessData.checkGoalF(copy.m_goalF);
    this.m_nullary = copy.m_nullary;
    this.m_unary = copy.m_unary;
    this.m_binary = copy.m_binary;
    this.m_ternary = copy.m_ternary;
    this.m_randSeed = copy.m_randSeed;
  }

  /**
   * Check the maximum FEs
   *
   * @param maxFEs
   *          the maximum FEs
   * @return the maximum FEs
   */
  static final long checkMaxFEs(final long maxFEs) {
    if (maxFEs <= 0L) {
      throw new IllegalArgumentException(
          "Maximum FEs must be positive, but is " //$NON-NLS-1$
              + maxFEs);
    }
    return maxFEs;
  }

  /**
   * Check the maximum time
   *
   * @param maxTime
   *          the maximum time
   * @return the maximum time
   */
  static final long checkMaxTime(final long maxTime) {
    if (maxTime <= 0L) {
      throw new IllegalArgumentException(
          "Maximum time must be positive, but is "//$NON-NLS-1$
              + maxTime);
    }
    return maxTime;
  }

  /**
   * Check the goal objective value
   *
   * @param goalF
   *          the goal objective value
   * @return the goal objective value
   */
  static final double checkGoalF(final double goalF) {
    if (Double.isFinite(goalF)
        || (goalF <= Double.NEGATIVE_INFINITY)) {
      return (goalF);
    }
    throw new IllegalArgumentException(
        "Goal objective value invalid: " //$NON-NLS-1$
            + goalF);
  }

  /**
   * Print the information
   *
   * @param out
   *          the output
   * @throws IOException
   *           if i/o fails
   */
  void printInfos(final Writer out) throws IOException {
    out.write(LogFormat.asComment(LogFormat.BEGIN_SETUP));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.SEARCH_SPACE,
        this.m_searchSpace));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.NULLARY_OP,
        this.m_nullary));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.UNARY_OP, this.m_unary));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.BINARY_OP, this.m_binary));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.TERNARY_OP,
        this.m_ternary));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.SOLUTION_SPACE,
        this.m_solutionSpace));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(
        LogFormat.REPRESENTATION_MAPPING, this.m_mapping));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.OBJECTIVE_FUNCTION,
        this.m_f));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.MAX_FES, this.m_maxFEs));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.MAX_TIME, this.m_maxTime));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.GOAL_F,
        LogFormat.doubleToStringForLog(this.m_goalF)));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.RANDOM_SEED,
        RandomUtils.randSeedToString(this.m_randSeed)));
    out.write(System.lineSeparator());
    out.write(LogFormat.asComment(LogFormat.END_SETUP));
    out.write(System.lineSeparator());

    // print the system information
    out.write(SystemData.getSystemData());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    try (final StringWriter sw = new StringWriter()) {
      this.printInfos(sw);
      return sw.toString();
    } catch (final IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Get the search space
   *
   * @return the search space
   */
  public final ISpace<X> getSearchSpace() {
    return this.m_searchSpace;
  }

  /**
   * Get the nullary search operator
   *
   * @return the nullary search operator
   */
  public final INullarySearchOperator<X>
      getNullarySearchOperator() {
    return this.m_nullary;
  }

  /**
   * Get the unary search operator
   *
   * @return the unary search operator
   */
  public final IUnarySearchOperator<X> getUnarySearchOperator() {
    return this.m_unary;
  }

  /**
   * Get the binary search operator
   *
   * @return the binary search operator
   */
  public final IBinarySearchOperator<X>
      getBinarySearchOperator() {
    return this.m_binary;
  }

  // end relevant
  /**
   * Get the ternary search operator
   *
   * @return the ternary search operator
   */
  public final ITernarySearchOperator<X>
      getTernarySearchOperator() {
    return this.m_ternary;
  }

  /**
   * Get the goal objective value,
   * {@link Double#NEGATIVE_INFINITY} if no goal is specified
   *
   * @return the goal objective value,
   *         {@link Double#NEGATIVE_INFINITY} if no goal is
   *         specified
   */
  public final double getGoalF() {
    return this.m_goalF;
  }

  /**
   * Get the maximum allowed FEs, {@link Long#MAX_VALUE} for
   * unlimited
   *
   * @return the maximum allowed FEs, {@link Long#MAX_VALUE} for
   *         unlimited
   */
  public final long getMaxFEs() {
    return this.m_maxFEs;
  }

  /**
   * Get the maximum allowed runtime in milliseconds,
   * {@link Long#MAX_VALUE} for unlimited
   *
   * @return the maximum allowed runtime in milliseconds,
   *         {@link Long#MAX_VALUE} for unlimited
   */
  public final long getMaxTime() {
    return this.m_maxTime;
  }

  /**
   * Get the objective function
   *
   * @return the objective function
   */
  public final IObjectiveFunction<Y> getObjectiveFunction() {
    return this.m_f;
  }

  /**
   * Get the solution space
   *
   * @return the solution space
   */
  public final ISpace<Y> getSolutionSpace() {
    return this.m_solutionSpace;
  }

  /**
   * get the session start in milliseconds
   *
   * @return the session start time in milliseconds
   */
  static final Instant getSessionStart() {
    return BlackBoxProcessData.SESSION_START;
  }
}
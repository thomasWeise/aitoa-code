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
  long mRandSeed;
  /** the search space */
  ISpace<X> mSearchSpace;
  /** the solution space */
  ISpace<Y> mSolutionSpace;
  /** the representation mapping */
  IRepresentationMapping<X, Y> mMapping;
  /** the objective function */
  IObjectiveFunction<Y> mF;
  /** the maximum FEs */
  long mMaxFEs;
  /** the maximum time */
  long mMaxTime;
  /** the goal objective value */
  double mGoalF;
  /** the nullary search operator */
  INullarySearchOperator<X> mNullary;
  /** the unary search operator */
  IUnarySearchOperator<X> mUnary;
  /** the binary search operator */
  IBinarySearchOperator<X> mBinary;
  /** the ternary search operator */
  ITernarySearchOperator<X> mTernary;

  /** Create the base class of the black box problem */
  BlackBoxProcessData() {
    super();
    this.mGoalF = Double.NEGATIVE_INFINITY;
    this.mMaxFEs = Long.MAX_VALUE;
    this.mMaxTime = Long.MAX_VALUE;
  }

  /**
   * Create the base class of the black box problem by copying
   * another instance
   *
   * @param pCopy
   *          the instance to copy
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  BlackBoxProcessData(final BlackBoxProcessData<X, Y> pCopy) {
    super();
    this.mSearchSpace =
        Objects.requireNonNull(pCopy.mSearchSpace);
    this.mSolutionSpace = (((pCopy.mSolutionSpace == null)
        || (pCopy.mSolutionSpace.equals(this.mSearchSpace)))
            ? ((ISpace) this.mSearchSpace)
            : pCopy.mSolutionSpace);
    if (this.mSearchSpace.equals(this.mSolutionSpace)) {
      this.mMapping = pCopy.mMapping;
    } else {
      this.mMapping = Objects.requireNonNull(pCopy.mMapping);
    }
    this.mF = Objects.requireNonNull(pCopy.mF);
    this.mMaxFEs =
        BlackBoxProcessData.checkMaxFEs(pCopy.mMaxFEs);
    this.mMaxTime =
        BlackBoxProcessData.checkMaxTime(pCopy.mMaxTime);
    this.mGoalF = BlackBoxProcessData.checkGoalF(pCopy.mGoalF);
    this.mNullary = pCopy.mNullary;
    this.mUnary = pCopy.mUnary;
    this.mBinary = pCopy.mBinary;
    this.mTernary = pCopy.mTernary;
    this.mRandSeed = pCopy.mRandSeed;
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
        this.mSearchSpace));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.NULLARY_OP, this.mNullary));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.UNARY_OP, this.mUnary));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.BINARY_OP, this.mBinary));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.TERNARY_OP, this.mTernary));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.SOLUTION_SPACE,
        this.mSolutionSpace));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(
        LogFormat.REPRESENTATION_MAPPING, this.mMapping));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.OBJECTIVE_FUNCTION,
        this.mF));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.MAX_FES, this.mMaxFEs));
    out.write(System.lineSeparator());
    out.write(
        LogFormat.mapEntry(LogFormat.MAX_TIME, this.mMaxTime));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.GOAL_F,
        LogFormat.doubleToStringForLog(this.mGoalF)));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.RANDOM_SEED,
        RandomUtils.randSeedToString(this.mRandSeed)));
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
    return this.mSearchSpace;
  }

  /**
   * Get the nullary search operator
   *
   * @return the nullary search operator
   */
  public final INullarySearchOperator<X>
      getNullarySearchOperator() {
    return this.mNullary;
  }

  /**
   * Get the unary search operator
   *
   * @return the unary search operator
   */
  public final IUnarySearchOperator<X> getUnarySearchOperator() {
    return this.mUnary;
  }

  /**
   * Get the binary search operator
   *
   * @return the binary search operator
   */
  public final IBinarySearchOperator<X>
      getBinarySearchOperator() {
    return this.mBinary;
  }

  // end relevant
  /**
   * Get the ternary search operator
   *
   * @return the ternary search operator
   */
  public final ITernarySearchOperator<X>
      getTernarySearchOperator() {
    return this.mTernary;
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
    return this.mGoalF;
  }

  /**
   * Get the maximum allowed FEs, {@link Long#MAX_VALUE} for
   * unlimited
   *
   * @return the maximum allowed FEs, {@link Long#MAX_VALUE} for
   *         unlimited
   */
  public final long getMaxFEs() {
    return this.mMaxFEs;
  }

  /**
   * Get the maximum allowed runtime in milliseconds,
   * {@link Long#MAX_VALUE} for unlimited
   *
   * @return the maximum allowed runtime in milliseconds,
   *         {@link Long#MAX_VALUE} for unlimited
   */
  public final long getMaxTime() {
    return this.mMaxTime;
  }

  /**
   * Get the objective function
   *
   * @return the objective function
   */
  public final IObjectiveFunction<Y> getObjectiveFunction() {
    return this.mF;
  }

  /**
   * Get the solution space
   *
   * @return the solution space
   */
  public final ISpace<Y> getSolutionSpace() {
    return this.mSolutionSpace;
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

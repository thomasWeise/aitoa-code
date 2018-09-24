package aitoa.structure;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * A builder for black-box process instances
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public final class BlackBoxProcessBuilder<X, Y>
    extends _BlackBoxProcessData<X, Y>
    implements Supplier<IBlackBoxProcess<X, Y>> {

  /** the log path */
  private Path m_logPath;

  /** the expected log length */
  private int m_expectedLogLength;

  /** Create the base class of the black box problem */
  public BlackBoxProcessBuilder() {
    super();
    this.setRandomRandSeed();
    this.m_expectedLogLength = 1024;
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
   * Set the solution space
   *
   * @param space
   *          the solution space
   * @return this instance
   */
  public final BlackBoxProcessBuilder<X, Y>
      setSolutionSpace(final ISpace<Y> space) {
    this.m_solutionSpace = Objects.requireNonNull(space);
    return this;
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
   * Set the objective function
   *
   * @param f
   *          the objective function
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setObjectiveFunction(final IObjectiveFunction<Y> f) {
    this.m_f = Objects.requireNonNull(f);
    return this;
  }

  /**
   * Set the representation mapping
   *
   * @param mapping
   *          the representation mapping
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setRepresentationMapping(
          final IRepresentationMapping<X, Y> mapping) {
    this.m_mapping = Objects.requireNonNull(mapping);
    return this;
  }

  /**
   * Get the random seed
   *
   * @return the random seed
   */
  public final long getRandSeed() {
    return this.m_randSeed;
  }

  /**
   * Set the random seed
   *
   * @param seed
   *          the random seed
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setRandSeed(final long seed) {
    this.m_randSeed = seed;
    return this;
  }

  /**
   * Set a randomly chosen random seed
   *
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y> setRandomRandSeed() {
    return this.setRandSeed(//
        ThreadLocalRandom.current().nextLong());
  }

  /**
   * Set the search space
   *
   * @param space
   *          the search space
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setSearchSpace(final ISpace<X> space) {
    this.m_searchSpace = Objects.requireNonNull(space);
    return this;
  }

  /**
   * Set the nullary search operator
   *
   * @param op
   *          the nullary search operator
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setNullarySearchOperator(
          final INullarySearchOperator<X> op) {
    this.m_nullary = Objects.requireNonNull(op);
    return this;
  }

  /**
   * Set the unary search operator
   *
   * @param op
   *          the unary search operator
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setUnarySearchOperator(final IUnarySearchOperator<X> op) {
    this.m_unary = Objects.requireNonNull(op);
    return this;
  }

  /**
   * Set the binary search operator
   *
   * @param op
   *          the binary search operator
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setBinarySearchOperator(
          final IBinarySearchOperator<X> op) {
    this.m_binary = Objects.requireNonNull(op);
    return this;
  }

  /**
   * Set the ternary search operator
   *
   * @param op
   *          the ternary search operator
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setTernarySearchOperator(
          final ITernarySearchOperator<X> op) {
    this.m_ternary = Objects.requireNonNull(op);
    return (this);
  }

  /**
   * Set the goal objective value,
   * {@link Double#NEGATIVE_INFINITY} if no goal is specified
   *
   * @param goal
   *          the goal objective value,
   *          {@link Double#NEGATIVE_INFINITY} if no goal is
   *          specified
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setGoalF(final double goal) {
    this.m_goalF = _BlackBoxProcessData._checkGoalF(goal);
    return this;
  }

  /**
   * Set the maximum allowed FEs, {@link Long#MAX_VALUE} for
   * unlimited
   *
   * @param max
   *          the maximum allowed FEs, {@link Long#MAX_VALUE} for
   *          unlimited
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setMaxFEs(final long max) {
    this.m_maxFEs = _BlackBoxProcessData._checkMaxFEs(max);
    return this;
  }

  /**
   * Set the maximum allowed runtime in milliseconds,
   * {@link Long#MAX_VALUE} for unlimited
   *
   * @param max
   *          the maximum allowed runtime in milliseconds,
   *          {@link Long#MAX_VALUE} for unlimited
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      getMaxTime(final long max) {
    this.m_maxTime = _BlackBoxProcessData._checkMaxTime(max);
    return this;
  }

  /**
   * Set the log path
   *
   * @param path
   *          the log path
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setLogPath(final Path path) {
    if (path != null) {
      this.m_logPath = path.toAbsolutePath();
    } else {
      this.m_logPath = null;
    }
    return this;
  }

  /**
   * Get the log path
   *
   * @return the log path
   */
  public final Path getLogPath() {
    return this.m_logPath;
  }

  /**
   * Get the expected log length
   *
   * @return the expected log length
   */
  public final int getExpectedLogLength() {
    return this.m_expectedLogLength;
  }

  /**
   * Set the expected log length
   *
   * @param length
   *          the length, <=0 for default
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setExpectedLogLength(final int length) {
    if (length > 0) {
      Math.multiplyExact(length, 3);// test
      this.m_expectedLogLength = length;
    } else {
      this.m_expectedLogLength = 1024;
    }
    return this;
  }

  /**
   * create the log
   *
   * @return the log
   */
  final BufferedWriter _createLogWriter() {
    // create log writer now, to a) spot potential errors and b)
    // make sure the file exists, so other threads may skip over
    // the problem
    try {
      return Files.newBufferedWriter(this.m_logPath);
    } catch (final IOException ioe) {
      throw new IllegalArgumentException(
          "File '" //$NON-NLS-1$
              + this.m_logPath + //
              "' cannot be created.", //$NON-NLS-1$
          ioe);
    }
  }

  /**
   * Create a data structure for logs which is at most twice as
   * long as 3 times the expected log length
   *
   * @return the long array
   */
  final long[] _createLog() {
    return new long[Math.multiplyExact(
        Integer.highestOneBit(this.m_expectedLogLength * 3), 2)];
  }

  /**
   * Create the instance of the black box problem.
   *
   * @return the problem instance
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public final IBlackBoxProcess<X, Y> get() {
    if (this.m_mapping == null) {
      // search space == solution space
      if (this.m_logPath == null) {
        return new _BlackBoxProcess1NoLog(this);
      }
      return new _BlackBoxProcess1Log(this);
    }

    // search and solution space are different
    if (this.m_logPath == null) {
      return new _BlackBoxProcess2NoLog(this);
    }
    return new _BlackBoxProcess2Log(this);
  }
}
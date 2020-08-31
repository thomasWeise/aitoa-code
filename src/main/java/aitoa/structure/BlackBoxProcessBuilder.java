package aitoa.structure;

import java.io.IOException;
import java.io.Writer;
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
public class BlackBoxProcessBuilder<X, Y>
    extends BlackBoxProcessData<X, Y>
    implements Supplier<IBlackBoxProcess<X, Y>> {

  /** the log path */
  private Path mLogPath;

  /** the expected log length */
  private int mExpectedLogLength;

  /** should we log all data? */
  private boolean mLogAll;

  /** Create the base class of the black box problem */
  public BlackBoxProcessBuilder() {
    super();
    this.setRandomRandSeed();
    this.mExpectedLogLength = 1024;
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
    this.mSolutionSpace = Objects.requireNonNull(space);
    return this;
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
    this.mF = Objects.requireNonNull(f);
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
    this.mMapping = Objects.requireNonNull(mapping);
    return this;
  }

  /**
   * Get the random seed
   *
   * @return the random seed
   */
  public final long getRandSeed() {
    return this.mRandSeed;
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
    this.mRandSeed = seed;
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
    this.mSearchSpace = Objects.requireNonNull(space);
    return this;
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
    this.mGoalF = BlackBoxProcessData.checkGoalF(goal);
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
    this.mMaxFEs = BlackBoxProcessData.checkMaxFEs(max);
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
      setMaxTime(final long max) {
    this.mMaxTime = BlackBoxProcessData.checkMaxTime(max);
    return this;
  }

  /**
   * The internal version of the log-path setter. This method is
   * overridden by the test version of the black-box process
   * builder.
   *
   * @param path
   *          the log path
   * @return this
   */
  BlackBoxProcessBuilder<X, Y> doSetLogPath(final Path path) {
    if (path != null) {
      this.mLogPath = path.toAbsolutePath();
    } else {
      this.mLogPath = null;
    }
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
    return (this.doSetLogPath(path));
  }

  /**
   * Get the log path
   *
   * @return the log path
   */
  public final Path getLogPath() {
    return this.mLogPath;
  }

  /**
   * Get the expected log length
   *
   * @return the expected log length
   */
  public final int getExpectedLogLength() {
    return this.mExpectedLogLength;
  }

  /**
   * Set the expected log length
   *
   * @param length
   *          the length, &lt;=0 for default
   * @return this
   */
  public final BlackBoxProcessBuilder<X, Y>
      setExpectedLogLength(final int length) {
    if (length > 0) {
      Math.multiplyExact(length, 3);// test
      this.mExpectedLogLength = length;
    } else {
      this.mExpectedLogLength = 1024;
    }
    return this;
  }

  /**
   * create the log
   *
   * @return the log
   */
  final Writer createLogWriter() {
    // create log writer now, to a) spot potential errors and b)
    // make sure the file exists, so other threads may skip over
    // the problem
    try {
      return Files.newBufferedWriter(this.mLogPath);
    } catch (final IOException ioe) {
      throw new IllegalArgumentException("File '" //$NON-NLS-1$
          + this.mLogPath + //
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
  final long[] createLog() {
    return new long[Math.multiplyExact(
        Integer.highestOneBit(this.mExpectedLogLength * 3), 2)];
  }

  /**
   * Will the generated black box process log every single FE?
   *
   * @return {@code true} if every single FE is logged,
   *         {@code false} otherwise.
   */
  public final boolean isLoggingAll() {
    return this.mLogAll;
  }

  /**
   * Set whether or not this builder should lock every single FE
   *
   * @param logAll
   *          {@code true} if every single FE should be logged,
   *          {@code false} otherwise
   * @return this builder
   */
  public final BlackBoxProcessBuilder<X, Y>
      setLogAll(final boolean logAll) {
    this.mLogAll = logAll;
    return this;
  }

  /**
   * The internal version used to create the instance of the
   * black box problem. This method is overridden by the test
   * version of the black-box process builder.
   *
   * @return the problem instance
   */
  @SuppressWarnings({ "unchecked", "rawtypes", "resource" })
  IBlackBoxProcess<X, Y> doGet() {
    if (this.mLogAll && (this.mLogPath == null)) {
      throw new IllegalArgumentException(
          "No log path is provided, while logging is set to ALL."); //$NON-NLS-1$
    }

    if (this.mLogPath != null) {
// Try to pre-load the system data to avoid any timing issues
// later. If we load the system data while flushing the first log
// file after the first run, this might have an impact on other
// runs running in parallel... ...better to load the system data
// now. (The problem which can cause delays is the resolution of
// the Graphics Adapater via the PCI ID which works via a web
// page.)
      if (SystemData.getSystemData().length < 256) {
        throw new IllegalStateException(
            "Could not load system data?"); //$NON-NLS-1$
      }
    }

    if (this.mMapping == null) {
      // search space == solution space
      if (this.mLogPath == null) {
        return new BlackBoxProcess1NoLog(this);
      }
      return this.mLogAll //
          ? new BlackBoxProcess1LogAll(this)//
          : new BlackBoxProcess1Log(this);
    }

    // search and solution space are different
    if (this.mLogPath == null) {
      return new BlackBoxProcess2NoLog(this);
    }
    return this.mLogAll //
        ? new BlackBoxProcess2LogAll(this)//
        : new BlackBoxProcess2Log(this);
  }

  /**
   * Create the instance of the black box problem.
   *
   * @return the problem instance
   */
  @Override
  public final IBlackBoxProcess<X, Y> get() {
    return this.doGet();
  }
}

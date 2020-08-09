package aitoa.structure;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

/**
 * the abstract base class for black box problems
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
abstract class BlackBoxProcessBase<X, Y> extends
    BlackBoxProcessData<X, Y> implements IBlackBoxProcess<X, Y> {
  /** the random number generator */
  final Random mRandom;
  /**
   * the end time, i.e., the time when the process should
   * terminate
   */
  final long mEndTime;
  /** the start time */
  final long mStartTime;
  /** did we terminate ? */
  volatile boolean mTerminated;
  /** the consumed FEs */
  long mConsumedFEs;
  /** the last improvement FE */
  long mLastImprovementFE;
  /** the last improvement time */
  long mLastImprovementTime;
  /** the best x */
  final X mBestX;
  /** the best objective value */
  double mBestF;
  /**
   * the time when the process was actually terminated:
   * <em>must</em> be set by subclasses in their overwritten
   * {@link #close()} method (and before doing any
   * synchronization stuff)!
   */
  long mTerminationTime;

  /** a linked list link */
  volatile transient BlackBoxProcessBase<?, ?> mNext;

  /**
   * Create the base class of the black box problem
   *
   * @param pBuilder
   *          the builder to copy the data from
   */
  BlackBoxProcessBase(
      final BlackBoxProcessBuilder<X, Y> pBuilder) {
    super(pBuilder);

    this.mBestX = this.mSearchSpace.create();
    this.mBestF = Double.POSITIVE_INFINITY;

    // compute time limits
    this.mLastImprovementTime =
        this.mStartTime = System.currentTimeMillis();
    if (this.mMaxTime >= Long.MAX_VALUE) {
      this.mEndTime = Long.MAX_VALUE;
    } else {
      this.mEndTime =
          Math.addExact(this.mStartTime, this.mMaxTime);
      if (this.mEndTime >= Long.MAX_VALUE) {
        throw new IllegalArgumentException(//
            "Invalid end time " //$NON-NLS-1$
                + this.mEndTime + //
                " due to long overflow.");//$NON-NLS-1$
      }
    }

    this.mRandom = new Random();
    this.mRandom.setSeed(this.mRandSeed);
  }

  /** terminate this problem */
  final void terminate() {
    final boolean term = this.mTerminated;
    this.mTerminated = true;
    if (!term) {
      if (this.mMaxTime < Long.MAX_VALUE) {
        TerminationThread.dequeue(this);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final double getBestF() {
    return this.mBestF;
  }

  /** {@inheritDoc} */
  @Override
  public final void getBestX(final X dest) {
    if (this.mConsumedFEs > 0L) {
      this.mSearchSpace.copy(this.mBestX, dest);
    } else {
      throw new IllegalStateException(//
          "No FE consumed yet."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public final long getConsumedFEs() {
    return this.mConsumedFEs;
  }

  /** {@inheritDoc} */
  @Override
  public final long getLastImprovementFE() {
    return this.mLastImprovementFE;
  }

  /** {@inheritDoc} */
  @Override
  public final long getConsumedTime() {
    final long time = System.currentTimeMillis();
    if (time >= this.mEndTime) {
      this.terminate();
    }
    return (time - this.mStartTime);
  }

  /** {@inheritDoc} */
  @Override
  public final long getLastImprovementTime() {
    return (this.mLastImprovementTime - this.mStartTime);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean shouldTerminate() {
    return this.mTerminated;
  }

  /** {@inheritDoc} */
  @Override
  public void close() throws IOException {
    if (this.mTerminationTime <= 0L) {
      this.mTerminationTime = System.currentTimeMillis();
    }
    this.terminate();
  }

  /** {@inheritDoc} */
  @Override
  public final Random getRandom() {
    return this.mRandom;
  }

  /**
   * grow a log
   *
   * @param log
   *          the log array
   * @return the new log array
   */
  static final long[] growLog(final long[] log) {
    return Arrays.copyOf(log,
        Math.addExact(log.length, log.length));
  }

  /** the log header */
  private static final char[] LOG_HEADER =
      LogFormat.asComment(LogFormat.joinLogLine("fbest", //$NON-NLS-1$
          "consumedFEs", //$NON-NLS-1$
          "consumedTimeMS")).toCharArray();//$NON-NLS-1$

  /** the begin log string */
  private static final char[] BEGIN_LOG =
      LogFormat.asComment(LogFormat.BEGIN_LOG).toCharArray();

  /** the end log string */
  private static final char[] END_OF_LOG =
      LogFormat.asComment(LogFormat.END_OF_LOG).toCharArray();

  /**
   * flush the log information to a file. The format of each log
   * point must be: bestF, FEs, time
   *
   * @param log
   *          the log
   * @param size
   *          the size
   * @param startTime
   *          the start time
   * @param out
   *          the buffered writer
   * @throws IOException
   *           if an i/o error occurs
   */
  static final void writeLog(final long[] log, final int size,
      final long startTime, final Writer out)
      throws IOException {
    out.write(BlackBoxProcessBase.BEGIN_LOG);
    out.write(System.lineSeparator());
    out.write(BlackBoxProcessBase.LOG_HEADER);
    out.write(System.lineSeparator());
    for (int i = 0; i < size;) {
      final double f = Double.longBitsToDouble(log[i++]);
      final long fes = log[i++];
      final long time = log[i++] - startTime;

      writeF: {
        if (Double.isFinite(f)//
            && (f >= Long.MIN_VALUE)//
            && (f <= Long.MAX_VALUE)) {
          final long lf = ((long) f);
          if (lf == f) {
            out.write(Long.toString(lf));
            break writeF;
          }
        }
        out.write(Double.toString(f));
      }

      out.write(LogFormat.CSV_SEPARATOR_CHAR);
      out.write(Long.toString(fes));
      out.write(LogFormat.CSV_SEPARATOR_CHAR);
      out.write(Long.toString(time));
      out.write(System.lineSeparator());
    }
    out.write(BlackBoxProcessBase.END_OF_LOG);
    out.write(System.lineSeparator());
  }

  /** the state begin */
  private static final char[] BEGIN_STATE =
      (LogFormat.asComment(LogFormat.BEGIN_STATE)
          + System.lineSeparator()).toCharArray();

  /** the state end */
  private static final char[] END_STATE = (System.lineSeparator()
      + LogFormat.asComment(LogFormat.END_STATE)
      + System.lineSeparator()).toCharArray();

  /** {@inheritDoc} */
  @Override
  void printInfos(final Writer out) throws IOException {
    if ((this.mTerminationTime <= 0L) || //
        (this.mTerminationTime > System.currentTimeMillis())) {
      throw new IllegalStateException(//
          "Invalid termination time: " + //$NON-NLS-1$
              this.mTerminationTime);
    }
    super.printInfos(out);
    out.write(BlackBoxProcessBase.BEGIN_STATE);
    out.write(LogFormat.mapEntry(LogFormat.CONSUMED_FES,
        this.mConsumedFEs));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.LAST_IMPROVEMENT_FE,
        this.mLastImprovementFE));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.CONSUMED_TIME,
        this.mTerminationTime - this.mStartTime));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.LAST_IMPROVEMENT_TIME,
        this.getLastImprovementTime()));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.BEST_F,
        LogFormat.doubleToStringForLog(this.mBestF)));
    out.write(BlackBoxProcessBase.END_STATE);
  }

  /** {@inheritDoc} */
  @Override
  public final double lowerBound() {
    return this.mF.lowerBound();
  }
}

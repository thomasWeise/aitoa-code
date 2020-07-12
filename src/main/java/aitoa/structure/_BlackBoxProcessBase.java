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
abstract class _BlackBoxProcessBase<X, Y>
    extends _BlackBoxProcessData<X, Y>
    implements IBlackBoxProcess<X, Y> {
  /** the random number generator */
  final Random m_random;
  /**
   * the end time, i.e., the time when the process should
   * terminate
   */
  final long m_endTime;
  /** the start time */
  final long m_startTime;
  /** did we terminate ? */
  volatile boolean m_terminated;
  /** the consumed FEs */
  long m_consumedFEs;
  /** the last improvement FE */
  long m_lastImprovementFE;
  /** the last improvement time */
  long m_lastImprovementTime;
  /** the best x */
  final X m_bestX;
  /** the best objective value */
  double m_bestF;
  /**
   * the time when the process was actually terminated:
   * <em>must</em> be set by subclasses in their overwritten
   * {@link #close()} method (and before doing any
   * synchronization stuff)!
   */
  long m_terminationTime;

  /** a linked list link */
  volatile transient _BlackBoxProcessBase<?, ?> m_next;

  /**
   * Create the base class of the black box problem
   *
   * @param builder
   *          the builder to copy the data from
   */
  _BlackBoxProcessBase(
      final BlackBoxProcessBuilder<X, Y> builder) {
    super(builder);

    this.m_bestX = this.m_searchSpace.create();
    this.m_bestF = Double.POSITIVE_INFINITY;

    // compute time limits
    this.m_lastImprovementTime =
        this.m_startTime = System.currentTimeMillis();
    if (this.m_maxTime >= Long.MAX_VALUE) {
      this.m_endTime = Long.MAX_VALUE;
    } else {
      this.m_endTime =
          Math.addExact(this.m_startTime, this.m_maxTime);
      if (this.m_endTime >= Long.MAX_VALUE) {
        throw new IllegalArgumentException(//
            "Invalid end time " //$NON-NLS-1$
                + this.m_endTime + //
                " due to long overflow.");//$NON-NLS-1$
      }
    }

    this.m_random = new Random();
    this.m_random.setSeed(this.m_randSeed);
  }

  /** terminate this problem */
  final void _terminate() {
    final boolean term = this.m_terminated;
    this.m_terminated = true;
    if (!term) {
      if (this.m_maxTime < Long.MAX_VALUE) {
        _TerminationThread._dequeue(this);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final double getBestF() {
    return this.m_bestF;
  }

  /** {@inheritDoc} */
  @Override
  public final void getBestX(final X dest) {
    if (this.m_consumedFEs > 0L) {
      this.m_searchSpace.copy(this.m_bestX, dest);
    } else {
      throw new IllegalStateException(//
          "No FE consumed yet."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public final long getConsumedFEs() {
    return this.m_consumedFEs;
  }

  /** {@inheritDoc} */
  @Override
  public final long getLastImprovementFE() {
    return this.m_lastImprovementFE;
  }

  /** {@inheritDoc} */
  @Override
  public final long getConsumedTime() {
    final long time = System.currentTimeMillis();
    if (time >= this.m_endTime) {
      this._terminate();
    }
    return (time - this.m_startTime);
  }

  /** {@inheritDoc} */
  @Override
  public final long getLastImprovementTime() {
    return (this.m_lastImprovementTime - this.m_startTime);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean shouldTerminate() {
    return this.m_terminated;
  }

  /** {@inheritDoc} */
  @Override
  public void close() throws IOException {
    if (this.m_terminationTime <= 0L) {
      this.m_terminationTime = System.currentTimeMillis();
    }
    this._terminate();
  }

  /** {@inheritDoc} */
  @Override
  public final Random getRandom() {
    return this.m_random;
  }

  /**
   * grow a log
   *
   * @param log
   *          the log array
   * @return the new log array
   */
  static final long[] _growLog(final long[] log) {
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
  static final void _writeLog(final long[] log, final int size,
      final long startTime, final Writer out)
      throws IOException {
    out.write(_BlackBoxProcessBase.BEGIN_LOG);
    out.write(System.lineSeparator());
    out.write(_BlackBoxProcessBase.LOG_HEADER);
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
    out.write(_BlackBoxProcessBase.END_OF_LOG);
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
  void _printInfos(final Writer out) throws IOException {
    if ((this.m_terminationTime <= 0L) || //
        (this.m_terminationTime > System.currentTimeMillis())) {
      throw new IllegalStateException(//
          "Invalid termination time: " + //$NON-NLS-1$
              this.m_terminationTime);
    }
    super._printInfos(out);
    out.write(_BlackBoxProcessBase.BEGIN_STATE);
    out.write(LogFormat.mapEntry(LogFormat.CONSUMED_FES,
        this.m_consumedFEs));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.LAST_IMPROVEMENT_FE,
        this.m_lastImprovementFE));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.CONSUMED_TIME,
        this.m_terminationTime - this.m_startTime));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.LAST_IMPROVEMENT_TIME,
        this.getLastImprovementTime()));
    out.write(System.lineSeparator());
    out.write(LogFormat.mapEntry(LogFormat.BEST_F,
        LogFormat.doubleToStringForLog(this.m_bestF)));
    out.write(_BlackBoxProcessBase.END_STATE);
  }

  /** {@inheritDoc} */
  @Override
  public final double lowerBound() {
    return this.m_f.lowerBound();
  }
}
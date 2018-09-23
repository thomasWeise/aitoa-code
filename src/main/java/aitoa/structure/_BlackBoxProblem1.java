package aitoa.structure;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * the abstract base class for black box problems
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
abstract class _BlackBoxProblem1<X, Y>
    implements IBlackBoxProblem<X, Y> {
  /** the search space */
  final ISpace<X> m_searchSpace;
  /** the objective function */
  final IObjectiveFunction<Y> m_f;
  /** the maximum FEs */
  final long m_maxFEs;
  /** the end time */
  final long m_endTime;
  /** the goal objective value */
  final double m_goalF;
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

  /** a linked list link */
  volatile transient _BlackBoxProblem1<?, ?> m_next;

  /**
   * Create the base class of the black box problem
   *
   * @param searchSpace
   *          the search space
   * @param f
   *          the objective function
   * @param maxFEs
   *          the maximum permitted FEs, use
   *          {@link Long#MAX_VALUE} for unlimited
   * @param maxTime
   *          the maximum permitted runtime in milliseconds, use
   *          {@link Long#MAX_VALUE} for unlimited
   * @param goalF
   *          the goal objective value
   */
  _BlackBoxProblem1(final ISpace<X> searchSpace,
      final IObjectiveFunction<Y> f, final long maxFEs,
      final long maxTime, final double goalF) {
    super();
    this.m_searchSpace = Objects.requireNonNull(searchSpace);
    this.m_f = Objects.requireNonNull(f);
    this.m_bestX = this.m_searchSpace.create();
    this.m_bestF = Double.POSITIVE_INFINITY;

    this.m_maxFEs = maxFEs;
    if (this.m_maxFEs <= 0L) {
      throw new IllegalArgumentException("Invalid max FEs: " //$NON-NLS-1$
          + maxFEs);
    }

    // setup the goal objective value
    if (goalF <= Double.NEGATIVE_INFINITY) {
      this.m_goalF = Double.NEGATIVE_INFINITY;
    } else {
      if (Double.isFinite(goalF)) {
        this.m_goalF = goalF;
      } else {
        throw new IllegalArgumentException(
            "Invalid goal objective value " //$NON-NLS-1$
                + goalF);
      }
    }

    // compute time limits
    this.m_lastImprovementTime =
        this.m_startTime = System.currentTimeMillis();
    if (maxTime >= Long.MAX_VALUE) {
      this.m_endTime = Long.MAX_VALUE;
    } else {
      if (maxTime <= 0L) {
        throw new IllegalArgumentException(//
            "Invalid max time: " //$NON-NLS-1$
                + maxTime);
      }
      this.m_endTime = Math.addExact(this.m_startTime, maxTime);
      if (this.m_endTime >= Long.MAX_VALUE) {
        throw new IllegalArgumentException(//
            "Invalid end time: " //$NON-NLS-1$
                + this.m_endTime);
      }
    }
  }

  /** terminate this problem */
  final void _terminate() {
    final boolean term = this.m_terminated;
    this.m_terminated = true;
    if (!term) {
      if (this.m_endTime < Long.MAX_VALUE) {
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
  @SuppressWarnings("unchecked")
  @Override
  public void getBestY(final Y dest) {
    this.getBestX((X) dest);
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
  public void close() {
    this._terminate();
  }

  /**
   * Create a data structure for logs which is at most twice as
   * long as 3 times the expected log length
   *
   * @param expectedLogLength
   *          the expected log length
   * @return the long array
   */
  static final long[] _createLog(final int expectedLogLength) {
    return new long[Math.multiplyExact(
        Integer.highestOneBit(
            (expectedLogLength <= 0) ? 1024 : expectedLogLength),
        2)];
  }

  /**
   * grow a log
   *
   * @param log
   *          the log array
   * @return the new log array
   */
  static final long[] _growLog(final long[] log) {
    return Arrays.copyOf(log, Math.multiplyExact(log.length, 2));
  }

  /**
   * flush the log information to a file. The format of each log
   * point must be: bestF, FEs, time
   *
   * @param log
   *          the log
   * @param size
   *          the size
   * @param out
   *          the buffered writer
   * @throws IOException
   *           if an i/o error occurs
   */
  static final void _writeLog(final long[] log, final int size,
      final BufferedWriter out) throws IOException {
    out.write("# fbest;consumedFEs;consumedTimeMS"); //$NON-NLS-1$
    out.newLine();
    for (int i = 0; i < size;) {
      final double f = Double.longBitsToDouble(log[i++]);
      final long fes = log[i++];
      final long time = log[i++];

      writeF: {
        if (Double.isFinite(f) && (f >= Long.MIN_VALUE)
            && (f <= Long.MAX_VALUE)) {
          final long lf = ((long) f);
          if (lf == f) {
            out.write(Long.toString(lf));
            break writeF;
          }
        }
        out.write(Double.toString(f));
      }

      out.write(';');
      out.write(Long.toString(fes));
      out.write(';');
      out.write(Long.toString(time));
      out.newLine();
    }
    out.write("# END_OF_LOG");//$NON-NLS-1$
    out.newLine();
  }
}
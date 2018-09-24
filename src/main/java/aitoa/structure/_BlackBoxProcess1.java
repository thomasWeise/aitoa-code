package aitoa.structure;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * the abstract base class for black box problems
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
abstract class _BlackBoxProcess1<X, Y>
    implements IBlackBoxProcess<X, Y> {
  /** the random number generator */
  private final Random m_random;
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
  volatile transient _BlackBoxProcess1<?, ?> m_next;

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
   * @param randSeed
   *          the random generator's random seed
   */
  _BlackBoxProcess1(final ISpace<X> searchSpace,
      final IObjectiveFunction<Y> f, final long maxFEs,
      final long maxTime, final double goalF,
      final long randSeed) {
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

    this.m_random = new Random();
    this.m_random.setSeed(randSeed);
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

  /** {@inheritDoc} */
  @Override
  public final Random getRandom() {
    return this.m_random;
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
   * @param startTime
   *          the start time
   * @param out
   *          the buffered writer
   * @throws IOException
   *           if an i/o error occurs
   */
  static final void _writeLog(final long[] log, final int size,
      final long startTime, final BufferedWriter out)
      throws IOException {
    out.write("# BEGIN_LOG"); //$NON-NLS-1$
    out.newLine();
    out.write("# fbest;consumedFEs;consumedTimeMS"); //$NON-NLS-1$
    out.newLine();
    for (int i = 0; i < size;) {
      final double f = Double.longBitsToDouble(log[i++]);
      final long fes = log[i++];
      final long time = log[i++] - startTime;

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

  /**
   * begin writing the log
   *
   * @param searchSpace
   *          the search space
   * @param solutionSpace
   *          the solution space
   * @param mapping
   *          the representation mapping
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
   * @param randSeed
   *          the random generator's random seed
   * @param out
   *          the output destination
   * @throws IOException
   *           on failure
   */
  static final void _beginLog(final ISpace<?> searchSpace,
      final ISpace<?> solutionSpace,
      final IRepresentationMapping<?, ?> mapping,
      final IObjectiveFunction<?> f, final long maxFEs,
      final long maxTime, final double goalF,
      final long randSeed, final BufferedWriter out)
      throws IOException {
    out.write("# BEGIN_META"); //$NON-NLS-1$
    out.newLine();
    out.write("SEARCH_SPACE: ");//$NON-NLS-1$
    out.write((searchSpace == null) //
        ? "null" : //$NON-NLS-1$
        searchSpace.toString());
    out.newLine();
    out.write("SOLUTION_SPACE: ");//$NON-NLS-1$
    out.write((solutionSpace == null) //
        ? "null" : //$NON-NLS-1$
        solutionSpace.toString());
    out.newLine();
    out.write("REPRESENTATION_MAPPING: ");//$NON-NLS-1$
    out.write((mapping == null) //
        ? "null" : //$NON-NLS-1$
        mapping.toString());
    out.newLine();
    out.write("OBJECTIVE_FUNCTION: ");//$NON-NLS-1$
    out.write((f == null) //
        ? "null" : //$NON-NLS-1$
        f.toString());
    out.newLine();
    out.write("MAX_FES: ");//$NON-NLS-1$
    out.write(Long.toString(maxFEs));
    out.newLine();
    out.write("MAX_TIME: ");//$NON-NLS-1$
    out.write(Long.toString(maxTime));
    out.newLine();
    out.write("GOAL_F: ");//$NON-NLS-1$
    out.write(Double.toString(goalF));
    out.newLine();
    out.write("RANDOM_SEED: ");//$NON-NLS-1$
    out.write(Long.toString(randSeed));
    out.newLine();
    out.write("JAVA_VERSION: ");//$NON-NLS-1$
    out.write(System.getProperty("java.version"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_VENDOR: ");//$NON-NLS-1$
    out.write(System.getProperty("java.vendor"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_VM_VERSION: ");//$NON-NLS-1$
    out.write(System.getProperty("java.vm.version"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_VM_VENDOR: ");//$NON-NLS-1$
    out.write(System.getProperty("java.vm.vendor"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_VM_NAME: ");//$NON-NLS-1$
    out.write(System.getProperty("java.vm.name"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_SPECIFICATION_VERSION: ");//$NON-NLS-1$
    out.write(System.getProperty("java.specification.version"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_SPECIFICATION_VENDOR: ");//$NON-NLS-1$
    out.write(System.getProperty("java.specification.vendor"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_SPECIFICATION_NAME: ");//$NON-NLS-1$
    out.write(System.getProperty("java.specification.name"));//$NON-NLS-1$
    out.newLine();
    out.write("JAVA_COMPILER: ");//$NON-NLS-1$
    out.write(System.getProperty("java.compiler"));//$NON-NLS-1$
    out.newLine();
    out.write("# END_META");//$NON-NLS-1$
    out.newLine();
  }
}
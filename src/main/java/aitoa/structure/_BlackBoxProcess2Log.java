package aitoa.structure;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * the black-box problem class for black box problems where the
 * search and solution space are different and logging takes
 * place
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
final class _BlackBoxProcess2Log<X, Y>
    extends _BlackBoxProcess2<X, Y> {

  /** the log file */
  private final BufferedWriter m_logWriter;
  /** the log */
  private long[] m_log;
  /** the log size */
  private int m_logSize;

  /**
   * Instantiate the black box problem
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
   * @param logFile
   *          the log file
   * @param expectedLogLength
   *          the expected maximum number of points to enter the
   *          log
   */
  _BlackBoxProcess2Log(final ISpace<X> searchSpace,
      final ISpace<Y> solutionSpace,
      final IRepresentationMapping<X, Y> mapping,
      final IObjectiveFunction<Y> f, final long maxFEs,
      final long maxTime, final double goalF,
      final long randSeed, final Path logFile,
      final int expectedLogLength) {
    super(searchSpace, solutionSpace, mapping, f, maxFEs,
        maxTime, goalF, randSeed);

    // create log writer now, to a) spot potential errors and b)
    // make sure the file exists, so other threads may skip over
    // the problem
    try {
      this.m_logWriter = Files.newBufferedWriter(logFile);
      _BlackBoxProcess1._beginLog(searchSpace, solutionSpace,
          mapping, f, maxFEs, maxTime, goalF, randSeed,
          this.m_logWriter);
      this.m_logWriter.flush();
    } catch (final IOException ioe) {
      throw new IllegalArgumentException(
          "File '" //$NON-NLS-1$
              + logFile + "' cannot be created.", //$NON-NLS-1$
          ioe);
    }

    this.m_log = _BlackBoxProcess1._createLog(expectedLogLength);

    // enqueue into terminator thread if needed only after
    // initialization is complete
    if (maxTime < Long.MAX_VALUE) {
      _TerminationThread._enqueue(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void close() {
    // make sure we are dequeued from terminator
    this._terminate();

    // write the log information and then close log
    try (final BufferedWriter out = this.m_logWriter) {
      _BlackBoxProcess1._writeLog(this.m_log, this.m_logSize,
          this.m_startTime, out);
      this.m_log = null;

      out.newLine();
      out.write("# BEST_X"); //$NON-NLS-1$
      out.newLine();
      this.m_searchSpace.print(this.m_bestX, out);
      out.newLine();
      out.write("# END_BEST_X"); //$NON-NLS-1$
      out.newLine();

      out.newLine();
      out.write("# BEST_Y"); //$NON-NLS-1$
      out.newLine();
      this.m_solutionSpace.print(this.m_bestY, out);
      out.newLine();
      out.write("# END_BEST_Y"); //$NON-NLS-1$
      out.newLine();
    } catch (final IOException ioe) {
      throw new RuntimeException(//
          "Error when writing log.", //$NON-NLS-1$
          ioe);
    }

    // validate result: throw error if invalid
    this.m_searchSpace.check(this.m_bestX);
    this.m_solutionSpace.check(this.m_bestY);
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final X y) {
    if (this.m_terminated) {
      // if we have already terminated, straight quit
      return Double.POSITIVE_INFINITY;
    }
    final long fes = ++this.m_consumedFEs; // increase fes
    // map and evaluate
    this.m_mapping.map(y, this.m_current);
    final double result = this.m_f.evaluate(this.m_current);

    // did we improve
    if (result < this.m_bestF) {// yes, we did
      // so remember a copy of this best solution
      this.m_searchSpace.copy(y, this.m_bestX);
      this.m_solutionSpace.copy(this.m_current, this.m_bestY);
      this.m_lastImprovementFE = fes; // and the current FE
      // and the time when the improvement was made
      this.m_lastImprovementTime = System.currentTimeMillis();

      // check if we have exhausted the granted runtime or
      // reached the quality goal
      if ((this.m_lastImprovementTime >= this.m_endTime)
          || (result <= this.m_goalF)) {
        this._terminate();// terminate: we are finished
      }

      // store the log information
      final int size = this.m_logSize;
      final int newSize = Math.addExact(size, 3);
      if (size > this.m_log.length) { // grow log
        this.m_log = _BlackBoxProcess1._growLog(this.m_log);
      }
      // store log point
      this.m_log[size] = Double.doubleToLongBits(result);
      this.m_log[size + 1] = fes;
      this.m_log[size + 2] = this.m_lastImprovementTime;
      this.m_logSize = newSize;
    }

    // check if we have exhausted the granted FEs
    if (fes >= this.m_maxFEs) {
      this._terminate();// terminate: no more FEs
    }
    // return result
    return result;
  }
}

package aitoa.structure;

import java.io.BufferedWriter;
import java.io.IOException;

import aitoa.utils.IOUtils;
import aitoa.utils.IOUtils.IOConsumer;

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
    extends _BlackBoxProcessBase<X, Y> {
  /** the current candidate solution */
  final Y m_current;
  /** the best-so-far candidate solution */
  final Y m_bestY;
  /** the log file */
  private final BufferedWriter m_logWriter;
  /** the log */
  private long[] m_log;
  /** the log size */
  private int m_logSize;

  /**
   * Instantiate the black box problem of the black box problem
   *
   * @param builder
   *          the builder to copy the data from
   */
  _BlackBoxProcess2Log(
      final BlackBoxProcessBuilder<X, Y> builder) {
    super(builder);
    this.m_bestY = this.m_solutionSpace.create();
    this.m_current = this.m_solutionSpace.create();

    this.m_logWriter = builder._createLogWriter();
    this.m_log = builder._createLog();

    // enqueue into terminator thread if needed only after
    // initialization is complete
    if (this.m_maxTime < Long.MAX_VALUE) {
      _TerminationThread._enqueue(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void close() throws IOException {
    // make sure we are dequeued from terminator
    this._terminate();

    // write the log information and then close log
    IOUtils.synchronizedIO(() -> {
      try (final BufferedWriter out = this.m_logWriter) {
        _BlackBoxProcessBase._writeLog(this.m_log,
            this.m_logSize, this.m_startTime, out);
        this.m_log = null;
        out.newLine();
        this._printInfos(out);
        if (this.m_consumedFEs > 0L) {
          out.newLine();
          out.write("# BEST_X"); //$NON-NLS-1$
          out.newLine();
          this.m_searchSpace.print(this.m_bestX, out);
          out.newLine();
          out.write("# END_BEST_X");//$NON-NLS-1$
          out.newLine();
          out.write("# BEST_Y"); //$NON-NLS-1$
          out.newLine();
          this.m_solutionSpace.print(this.m_bestY, out);
          out.newLine();
          out.write("# END_BEST_Y"); //$NON-NLS-1$
        }
      }
    });

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
    this.m_mapping.map(this.m_random, y, this.m_current);
    final double result = this.m_f.evaluate(this.m_current);

    // did we improve
    if (result < this.m_bestF) {// yes, we did
      // so remember a copy of this best solution
      this.m_bestF = result;
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
      if (newSize > this.m_log.length) { // grow log
        this.m_log = _BlackBoxProcessBase._growLog(this.m_log);
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

  /** {@inheritDoc} */
  @Override
  public final void getBestY(final Y dest) {
    if (this.m_consumedFEs > 0L) {
      this.m_solutionSpace.copy(this.m_bestY, dest);
    } else {
      throw new IllegalStateException(//
          "No FE consumed yet."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void printLogSection(final String sectionName,
      final IOConsumer<BufferedWriter> printer)
      throws IOException {
    IOUtils.synchronizedIO(() -> {
      this.m_logWriter.newLine();
      this.m_logWriter.write(LogFormat.COMMENT_CHAR);
      this.m_logWriter.write(' ');
      this.m_logWriter.write(sectionName);
      this.m_logWriter.newLine();
      printer.accept(this.m_logWriter);
      this.m_logWriter.write(LogFormat.COMMENT_CHAR);
      this.m_logWriter.write(" END_"); //$NON-NLS-1$
      this.m_logWriter.write(sectionName);
      this.m_logWriter.newLine();
      this.m_logWriter.newLine();
    });
  }
}

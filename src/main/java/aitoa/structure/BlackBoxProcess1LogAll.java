package aitoa.structure;

import java.io.IOException;
import java.io.Writer;

import aitoa.utils.IOUtils;
import aitoa.utils.IOUtils.IOConsumer;

/**
 * The black-box problem class for black box problems where the
 * search and solution space are the same and logging takes
 * place. Here, however, <em>all</em> data is logged.
 *
 * @param <X>
 *          the search and solution space
 */
final class BlackBoxProcess1LogAll<X>
    extends BlackBoxProcessBase<X, X> {

  /** the log file */
  private final Writer m_logWriter;
  /** the log */
  private long[] m_log;
  /** the log size */
  private int m_logSize;

  /**
   * Instantiate the black box problem
   *
   * @param builder
   *          the builder to copy the data from
   */
  BlackBoxProcess1LogAll(
      final BlackBoxProcessBuilder<X, X> builder) {
    super(builder);

    this.m_logWriter = builder.createLogWriter();
    this.m_log = builder.createLog();

    // enqueue into terminator thread if needed only after
    // initialization is complete
    if (this.m_maxTime < Long.MAX_VALUE) {
      TerminationThread.enqueue(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void close() throws IOException {
    if (this.m_terminationTime <= 0L) {
      this.m_terminationTime = System.currentTimeMillis();
    }
    // make sure we are dequeued from terminator
    this.terminate();

    // write the log information and then close log
    IOUtils.synchronizedIO(() -> {
      try (final Writer out = this.m_logWriter) {
        BlackBoxProcessBase.writeLog(this.m_log, this.m_logSize,
            this.m_startTime, out);
        this.m_log = null;
        this.printInfos(out);
        if (this.m_consumedFEs > 0L) {
          out.write("# BEST_X"); //$NON-NLS-1$
          out.write(System.lineSeparator());
          this.m_searchSpace.print(this.m_bestX, out);
          out.write(System.lineSeparator());
          out.write("# END_BEST_X"); //$NON-NLS-1$
          out.write(System.lineSeparator());
        }
      }
    });

    // validate result: throw error if invalid
    this.m_searchSpace.check(this.m_bestX);
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final X y) {
    if (this.m_terminated) {
      // if we have already terminated, straight quit
      return Double.POSITIVE_INFINITY;
    }
    final long fes = ++this.m_consumedFEs; // increase fes
    // evaluate
    final double result = this.m_f.evaluate(y);

    final long time = System.currentTimeMillis();
    // store the log information
    final int size = this.m_logSize;
    final int newSize = Math.addExact(size, 3);
    if (newSize > this.m_log.length) { // grow log
      this.m_log = BlackBoxProcessBase.growLog(this.m_log);
    }
    // store log point
    this.m_log[size] = Double.doubleToLongBits(result);
    this.m_log[size + 1] = fes;
    this.m_log[size + 2] = time;
    this.m_logSize = newSize;

    // did we improve
    if (result < this.m_bestF) {// yes, we did
      // so remember a copy of this best solution
      this.m_bestF = result;
      this.m_searchSpace.copy(y, this.m_bestX);
      this.m_lastImprovementFE = fes; // and the current FE
      // and the time when the improvement was made
      this.m_lastImprovementTime = time;

      // check if we have exhausted the granted runtime or
      // reached the quality goal
      if ((this.m_lastImprovementTime >= this.m_endTime)
          || (result <= this.m_goalF)) {
        this.terminate();// terminate: we are finished
      }
    }

    // check if we have exhausted the granted FEs
    if (fes >= this.m_maxFEs) {
      this.terminate();// terminate: no more FEs
    }
    // return result
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public void getBestY(final X dest) {
    if (this.m_consumedFEs > 0L) {
      this.m_searchSpace.copy(this.m_bestX, dest);
    } else {
      throw new IllegalStateException(//
          "No FE consumed yet."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public void printLogSection(final String sectionName,
      final IOConsumer<Writer> printer) throws IOException {
    IOUtils.synchronizedIO(() -> {
      this.m_logWriter.write(LogFormat.COMMENT_CHAR);
      this.m_logWriter.write(' ');
      this.m_logWriter.write(sectionName);
      this.m_logWriter.write(System.lineSeparator());
      printer.accept(this.m_logWriter);
      this.m_logWriter.write(LogFormat.COMMENT_CHAR);
      this.m_logWriter.write(" END_"); //$NON-NLS-1$
      this.m_logWriter.write(sectionName);
      this.m_logWriter.write(System.lineSeparator());
    });
  }
}

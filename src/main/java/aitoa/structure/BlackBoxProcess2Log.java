package aitoa.structure;

import java.io.IOException;
import java.io.Writer;

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
final class BlackBoxProcess2Log<X, Y>
    extends BlackBoxProcessBase<X, Y> {
  /** the current candidate solution */
  final Y mCurrent;
  /** the best-so-far candidate solution */
  final Y mBestY;
  /** the log file */
  private final Writer mLogWriter;
  /** the log */
  private long[] mLog;
  /** the log size */
  private int mLogSize;

  /**
   * Instantiate the black box problem of the black box problem
   *
   * @param pBuilder
   *          the builder to copy the data from
   */
  BlackBoxProcess2Log(
      final BlackBoxProcessBuilder<X, Y> pBuilder) {
    super(pBuilder);
    this.mBestY = this.mSolutionSpace.create();
    this.mCurrent = this.mSolutionSpace.create();

    this.mLogWriter = pBuilder.createLogWriter();
    this.mLog = pBuilder.createLog();

    // enqueue into terminator thread if needed only after
    // initialization is complete
    if (this.mMaxTime < Long.MAX_VALUE) {
      TerminationThread.enqueue(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void close() throws IOException {
    if (this.mTerminationTime <= 0L) {
      this.mTerminationTime = System.currentTimeMillis();
    }
    // make sure we are dequeued from terminator
    this.terminate();

    // write the log information and then close log
    IOUtils.synchronizedIO(() -> {
      try (final Writer out = this.mLogWriter) {
        BlackBoxProcessBase.writeLog(this.mLog, this.mLogSize,
            this.mStartTime, out);
        this.mLog = null;
        this.printInfos(out);
        if (this.mConsumedFEs > 0L) {
          out.write("# BEST_X"); //$NON-NLS-1$
          out.write(System.lineSeparator());
          this.mSearchSpace.print(this.mBestX, out);
          out.write(System.lineSeparator());
          out.write("# END_BEST_X");//$NON-NLS-1$
          out.write(System.lineSeparator());
          out.write("# BEST_Y"); //$NON-NLS-1$
          out.write(System.lineSeparator());
          this.mSolutionSpace.print(this.mBestY, out);
          out.write(System.lineSeparator());
          out.write("# END_BEST_Y"); //$NON-NLS-1$
          out.write(System.lineSeparator());
        }
      }
    });

    // validate result: throw error if invalid
    this.mSearchSpace.check(this.mBestX);
    this.mSolutionSpace.check(this.mBestY);
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final X y) {
    if (this.mTerminated) {
      // if we have already terminated, straight quit
      return Double.POSITIVE_INFINITY;
    }
    final long fes = ++this.mConsumedFEs; // increase fes
    // map and evaluate
    this.mMapping.map(this.mRandom, y, this.mCurrent);
    final double result = this.mF.evaluate(this.mCurrent);

    // did we improve
    if (result < this.mBestF) { // yes, we did
      // so remember a copy of this best solution
      this.mBestF = result;
      this.mSearchSpace.copy(y, this.mBestX);
      this.mSolutionSpace.copy(this.mCurrent, this.mBestY);
      this.mLastImprovementFE = fes; // and the current FE
      // and the time when the improvement was made
      this.mLastImprovementTime = System.currentTimeMillis();

      // check if we have exhausted the granted runtime or
      // reached the quality goal
      if ((this.mLastImprovementTime >= this.mEndTime)
          || (result <= this.mGoalF)) {
        this.terminate();// terminate: we are finished
      }

      // store the log information
      final int size = this.mLogSize;
      final int newSize = Math.addExact(size, 3);
      if (newSize > this.mLog.length) { // grow log
        this.mLog = BlackBoxProcessBase.growLog(this.mLog);
      }
      // store log point
      this.mLog[size] = Double.doubleToLongBits(result);
      this.mLog[size + 1] = fes;
      this.mLog[size + 2] = this.mLastImprovementTime;
      this.mLogSize = newSize;
    }

    // check if we have exhausted the granted FEs
    if (fes >= this.mMaxFEs) {
      this.terminate();// terminate: no more FEs
    }
    // return result
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public void getBestY(final Y dest) {
    if (this.mConsumedFEs > 0L) {
      this.mSolutionSpace.copy(this.mBestY, dest);
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
      this.mLogWriter.write(LogFormat.COMMENT_CHAR);
      this.mLogWriter.write(' ');
      this.mLogWriter.write(sectionName);
      this.mLogWriter.write(System.lineSeparator());
      printer.accept(this.mLogWriter);
      this.mLogWriter.write(LogFormat.COMMENT_CHAR);
      this.mLogWriter.write(" END_"); //$NON-NLS-1$
      this.mLogWriter.write(sectionName);
      this.mLogWriter.write(System.lineSeparator());
    });
  }
}

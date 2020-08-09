package aitoa.structure;

/**
 * the black-box problem class for black box problems where the
 * search and solution space are different and no logging takes
 * place
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
final class BlackBoxProcess2NoLog<X, Y>
    extends BlackBoxProcessBase<X, Y> {
  /** the current candidate solution */
  final Y mCurrent;
  /** the best-so-far candidate solution */
  final Y mBestY;

  /**
   * Instantiate the black box problem of the black box problem
   *
   * @param pBuilder
   *          the builder to copy the data from
   */
  BlackBoxProcess2NoLog(
      final BlackBoxProcessBuilder<X, Y> pBuilder) {
    super(pBuilder);
    this.mBestY = this.mSolutionSpace.create();
    this.mCurrent = this.mSolutionSpace.create();
    // enqueue into terminator thread if needed only after
    // initialization is complete
    if (this.mMaxTime < Long.MAX_VALUE) {
      TerminationThread.enqueue(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    if (this.mTerminationTime <= 0L) {
      this.mTerminationTime = System.currentTimeMillis();
    }
    // make sure we are dequeued from terminator
    this.terminate();
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
    if (result < this.mBestF) {// yes, we did
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
}

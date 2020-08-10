package aitoa.structure;

/**
 * the black-box problem class for black box problems where the
 * search and solution space are the same and no logging takes
 * place
 *
 * @param <X>
 *          the search and solution space
 */
final class BlackBoxProcess1NoLog<X>
    extends BlackBoxProcessBase<X, X> {

  /**
   * Instantiate the black box problem
   *
   * @param pBuilder
   *          the builder to copy the data from
   */
  BlackBoxProcess1NoLog(
      final BlackBoxProcessBuilder<X, X> pBuilder) {
    super(pBuilder);
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
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final X y) {
    if (this.mTerminated) {
      // if we have already terminated, straight quit
      return Double.POSITIVE_INFINITY;
    }
    final long fes = ++this.mConsumedFEs; // increase fes
    // evaluate
    final double result = this.mF.evaluate(y);

    // did we improve
    if (result < this.mBestF) { // yes, we did
      // so remember a copy of this best solution
      this.mBestF = result;
      this.mSearchSpace.copy(y, this.mBestX);
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
  public void getBestY(final X dest) {
    if (this.mConsumedFEs > 0L) {
      this.mSearchSpace.copy(this.mBestX, dest);
    } else {
      throw new IllegalStateException(//
          "No FE consumed yet."); //$NON-NLS-1$
    }
  }
}

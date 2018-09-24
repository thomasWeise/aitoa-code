package aitoa.structure;

/**
 * the black-box problem class for black box problems where the
 * search and solution space are the same and no logging takes
 * place
 *
 * @param <X>
 *          the search and solution space
 */
final class _BlackBoxProcess1NoLog<X>
    extends _BlackBoxProcessBase<X, X> {

  /**
   * Instantiate the black box problem
   *
   * @param builder
   *          the builder to copy the data from
   */
  _BlackBoxProcess1NoLog(
      final BlackBoxProcessBuilder<X, X> builder) {
    super(builder);
    // enqueue into terminator thread if needed only after
    // initialization is complete
    if (this.m_maxTime < Long.MAX_VALUE) {
      _TerminationThread._enqueue(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void close() {
    // make sure we are dequeued from terminator
    this._terminate();

    // validate result: throw error if invalid
    this.m_searchSpace.check(this.m_bestX);
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final X y) {
    if (this.m_terminated) {
      // if we have already terminated, straight quit
      return Double.POSITIVE_INFINITY;
    }
    final long fes = ++this.m_consumedFEs; // increase fes
    // evaluate
    final double result = this.m_f.evaluate(y);

    // did we improve
    if (result < this.m_bestF) {// yes, we did
      // so remember a copy of this best solution
      this.m_searchSpace.copy(y, this.m_bestX);
      this.m_lastImprovementFE = fes; // and the current FE
      // and the time when the improvement was made
      this.m_lastImprovementTime = System.currentTimeMillis();

      // check if we have exhausted the granted runtime or
      // reached the quality goal
      if ((this.m_lastImprovementTime >= this.m_endTime)
          || (result <= this.m_goalF)) {
        this._terminate();// terminate: we are finished
      }
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
  public final void getBestY(final X dest) {
    if (this.m_consumedFEs > 0L) {
      this.m_searchSpace.copy(this.m_bestX, dest);
    } else {
      throw new IllegalStateException(//
          "No FE consumed yet."); //$NON-NLS-1$
    }
  }
}

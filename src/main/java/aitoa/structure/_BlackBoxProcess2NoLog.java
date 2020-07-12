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
final class _BlackBoxProcess2NoLog<X, Y>
    extends _BlackBoxProcessBase<X, Y> {
  /** the current candidate solution */
  final Y m_current;
  /** the best-so-far candidate solution */
  final Y m_bestY;

  /**
   * Instantiate the black box problem of the black box problem
   *
   * @param builder
   *          the builder to copy the data from
   */
  _BlackBoxProcess2NoLog(
      final BlackBoxProcessBuilder<X, Y> builder) {
    super(builder);
    this.m_bestY = this.m_solutionSpace.create();
    this.m_current = this.m_solutionSpace.create();
    // enqueue into terminator thread if needed only after
    // initialization is complete
    if (this.m_maxTime < Long.MAX_VALUE) {
      _TerminationThread._enqueue(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    if (this.m_terminationTime <= 0L) {
      this.m_terminationTime = System.currentTimeMillis();
    }
    // make sure we are dequeued from terminator
    this._terminate();
    // validate result: throw error if invalid
    this.m_searchSpace.check(this.m_bestX);
    this.m_solutionSpace.check(this.m_bestY);
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final X y) {
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
  public void getBestY(final Y dest) {
    if (this.m_consumedFEs > 0L) {
      this.m_solutionSpace.copy(this.m_bestY, dest);
    } else {
      throw new IllegalStateException(//
          "No FE consumed yet."); //$NON-NLS-1$
    }
  }
}

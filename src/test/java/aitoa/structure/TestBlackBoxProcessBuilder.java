package aitoa.structure;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;

import org.junit.Assert;

import aitoa.TestTools;

/**
 * A version of the
 * {@link aitoa.structure.BlackBoxProcessBuilder} for creating an
 * instance that can test the behavior of optimization
 * algorithms. It assumes that all
 * {@link aitoa.structure.ISpace#check(Object)} functions are
 * correctly implemented. The idea is that this builder provides
 * a wrapper around our internal implementations of
 * {@link aitoa.structure.IBlackBoxProcess} which,
 * <ol>
 * <li>in each FE, invokes the checker routines of the
 * {@link aitoa.structure.ISpace} interfaces for the search- and
 * solution space,</li>
 * <li>checks the computed objective values against potential
 * {@linkplain aitoa.structure.IObjectiveFunction#lowerBound()
 * lower bounds} of the
 * {@linkplain aitoa.structure.IObjectiveFunction objective
 * function}, and
 * <li>checks whether the optimization algorithm has properly
 * called
 * {@link aitoa.structure.ITerminationCriterion#shouldTerminate()}
 * and adhered to it, i.e., stopped when it was supposed to
 * stop.</li>
 * </ol>
 * Hence, if we have tested the search- and solution space
 * implementations as well as the objective function well, then
 * we this "test" black-box process will be able validate every
 * single search step that an algorithm conducts. If the
 * algorithm somehow has invalidated its data or ignores the
 * termination indicator, the test will fail. Of course, if the
 * algorithm is a proper black-box metaheuristic, it should not
 * be able to produce invalid data anyway, since it should be
 * using the search operations and representation mapping that we
 * provide, which, in turn, should be correct if we have tested
 * them well.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public class TestBlackBoxProcessBuilder<X, Y>
    extends BlackBoxProcessBuilder<X, Y> {

  /**
   * create the test black box process builder
   */
  public TestBlackBoxProcessBuilder() {
    super();
    this.setMaxFEs(4096L);
    this.setMaxTime(2000L);
  }

  /** {@inheritDoc} */
  @Override
  final BlackBoxProcessBuilder<X, Y>
      doSetLogPath(final Path path) {
    throw new UnsupportedOperationException(//
        "Test instances cannot create logs."); //$NON-NLS-1$
  }

  /**
   * The internal version used to create the instance of the
   * black box problem. This method is overridden by the test
   * version of the black-box process builder.
   *
   * @return the problem instance
   */
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes", "resource" })
  final IBlackBoxProcess<X, Y> doGet() {
    if (this.mMapping == null) {
      return new BlackBoxProcessWrapper1(
          new BlackBoxProcess1NoLog(this));
    }

    return new BlackBoxProcessWrapper2(
        new BlackBoxProcess2NoLog(this));
  }

  /**
   * the internal base class for the actual black-box process
   * wrappers
   *
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   * @param <P>
   *          the base type
   */
  private static class BlackBoxProcessWrapper<X, Y,
      P extends BlackBoxProcessBase<X, Y>>
      implements IBlackBoxProcess<X, Y> {

    /** the wrapped process */
    final P mProcess;

    /**
     * does the calling algorithm know that it should have
     * terminated?
     */
    boolean mKnowsThatTerminated;

    /** does the caller know that we are closed? */
    boolean mKnowsThatClosed;

    /** the lower bound */
    final double mLb;

    /** the upper bound */
    final double mUb;

    /**
     * create
     *
     * @param pProcess
     *          the wrapped process
     */
    BlackBoxProcessWrapper(final P pProcess) {
      super();
      this.mProcess = Objects.requireNonNull(pProcess);
      this.mLb = pProcess.mF.lowerBound();
      this.mUb = pProcess.mF.upperBound();
    }

    /** fail if we are terminated */
    final void checkTerminated() {
      if (this.mKnowsThatTerminated) {
        Assert.fail(//
            "Algorithm performs actions after being informed that it should have terminated!"); //$NON-NLS-1$
      }
    }

    /** {@inheritDoc} */
    @Override
    public double evaluate(final X y) {
      this.checkTerminated();
      return this.mProcess.evaluate(y);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean shouldTerminate() {
      final boolean st = this.mProcess.mTerminated;
      if (st) {
        this.mKnowsThatTerminated = true;
      } else {
        if (this.mKnowsThatTerminated) {
          Assert.fail("Inconsistent termination state"); //$NON-NLS-1$
        }
      }
      return st;
    }

    /** {@inheritDoc} */
    @Override
    public final Random getRandom() {
      final Random r = this.mProcess.getRandom();
      Assert.assertNotNull(r);
      return r;
    }

    /** {@inheritDoc} */
    @Override
    public final ISpace<X> getSearchSpace() {
      final ISpace<X> s = this.mProcess.getSearchSpace();
      Assert.assertNotNull(s);
      return s;
    }

    /** {@inheritDoc} */
    @Override
    public final double getBestF() {
      final double f = this.mProcess.mBestF;
      if (this.mProcess.mConsumedFEs > 0L) {
        TestTools.assertFinite(f);
        TestTools.assertGreaterOrEqual(f, this.mLb);
        TestTools.assertLessOrEqual(f, this.mUb);
      } else {
        Assert.assertTrue(f >= Double.POSITIVE_INFINITY);
      }
      return f;
    }

    /** {@inheritDoc} */
    @Override
    public final double getGoalF() {
      return this.mProcess.mGoalF;
    }

    /** {@inheritDoc} */
    @Override
    public final void getBestX(final X dest) {
      this.mProcess.getBestX(dest);
    }

    /** {@inheritDoc} */
    @Override
    public final void getBestY(final Y dest) {
      this.mProcess.getBestY(dest);
    }

    /** {@inheritDoc} */
    @Override
    public final long getConsumedFEs() {
      final long f;
      f = this.mProcess.mConsumedFEs;
      TestTools.assertGreaterOrEqual(f, 0L);
      return f;
    }

    /** {@inheritDoc} */
    @Override
    public final long getLastImprovementFE() {
      final long i = this.mProcess.mLastImprovementFE;
      final long f = this.mProcess.mConsumedFEs;
      TestTools.assertGreaterOrEqual(f, 0L);
      TestTools.assertGreaterOrEqual(f, i);
      return i;
    }

    /** {@inheritDoc} */
    @Override
    public final long getMaxFEs() {
      final long f = this.mProcess.mConsumedFEs;
      final long m = this.mProcess.mMaxFEs;
      TestTools.assertGreaterOrEqual(f, 0L);
      TestTools.assertGreaterOrEqual(m, f);
      return m;
    }

    /** {@inheritDoc} */
    @Override
    public final long getConsumedTime() {
      final long t = this.mProcess.getConsumedTime();
      TestTools.assertGreaterOrEqual(t, 0L);
      return t;
    }

    /** {@inheritDoc} */
    @Override
    public final long getLastImprovementTime() {
      final long t = this.mProcess.getConsumedTime();
      final long i = this.mProcess.getLastImprovementTime();
      TestTools.assertGreaterOrEqual(t, 0L);
      TestTools.assertGreaterOrEqual(t, i);
      return i;
    }

    /** {@inheritDoc} */
    @Override
    public final long getMaxTime() {
      final long t = this.mProcess.getConsumedTime();
      final long m = this.mProcess.getMaxTime();
      TestTools.assertGreaterOrEqual(t, 0L);
      TestTools.assertGreaterOrEqual(m, t);
      return m;
    }

    /** {@inheritDoc} */
    @Override
    public final void close() throws IOException {
      if (this.mKnowsThatClosed) {
        Assert.fail("closed process twice?"); //$NON-NLS-1$
      }
      this.mKnowsThatClosed = true;
      this.mProcess.close();
      this.mKnowsThatTerminated = true;
      final long fesc = this.mProcess.mConsumedFEs;
      final long fesi = this.mProcess.mLastImprovementFE;
      final long fesm = this.mProcess.mMaxFEs;
      TestTools.assertGreater(fesi, 0L);
      TestTools.assertGreaterOrEqual(fesc, fesi);
      TestTools.assertGreaterOrEqual(fesm, fesc);

      final long tc = this.mProcess.getConsumedTime();
      final long ti = this.mProcess.getLastImprovementTime();
      final long tm = this.mProcess.getMaxTime();
      TestTools.assertGreaterOrEqual(ti, 0L);
      TestTools.assertGreaterOrEqual(tc, ti);
      TestTools.assertGreaterOrEqual(tm + 30, tc);

      TestTools.assertFinite(this.mProcess.mBestF);
      TestTools.assertGreaterOrEqual(this.mProcess.mBestF,
          this.mLb);
    }
  }

  /**
   * the internal base class for the actual black-box process
   * wrappers
   *
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   */
  private static class BlackBoxProcessWrapper2<X, Y> extends
      BlackBoxProcessWrapper<X, Y, BlackBoxProcess2NoLog<X, Y>> {

    /**
     * create
     *
     * @param pProcess
     *          the wrapped process
     */
    BlackBoxProcessWrapper2(
        final BlackBoxProcess2NoLog<X, Y> pProcess) {
      super(pProcess);
    }

    /** {@inheritDoc} */
    @Override
    public final double evaluate(final X y) {
      this.checkTerminated();
      this.mProcess.mSearchSpace.check(y);

      if (this.mProcess.mTerminated) {
        this.mKnowsThatTerminated = true;
        return Double.POSITIVE_INFINITY;
      }

      final long fes = ++this.mProcess.mConsumedFEs;
      // map and evaluate
      this.mProcess.mMapping.map(this.mProcess.mRandom, y,
          this.mProcess.mCurrent);

      this.mProcess.mSolutionSpace.check(this.mProcess.mCurrent);

      final double result =
          this.mProcess.mF.evaluate(this.mProcess.mCurrent);

      TestTools.assertFinite(result);
      TestTools.assertGreaterOrEqual(result, this.mLb);
      TestTools.assertLessOrEqual(result, this.mUb);

      // did we improve
      if (result < this.mProcess.mBestF) { // yes, we did
        // so remember a copy of this best solution
        this.mProcess.mBestF = result;
        this.mProcess.mSearchSpace.copy(y, this.mProcess.mBestX);
        this.mProcess.mSolutionSpace.copy(this.mProcess.mCurrent,
            this.mProcess.mBestY);
        this.mProcess.mLastImprovementFE = fes; // and the
                                                // current FE
        // and the time when the improvement was made
        this.mProcess.mLastImprovementTime =
            System.currentTimeMillis();

        // check if we have exhausted the granted runtime or
        // reached the quality goal
        if ((this.mProcess.mLastImprovementTime >= this.mProcess.mEndTime)
            || (result <= this.mProcess.mGoalF)) {
          this.mProcess.terminate();// terminate
        }
      }

      // check if we have exhausted the granted FEs
      if (fes >= this.mProcess.mMaxFEs) {
        this.mProcess.terminate();// terminate: no more FEs
      }
      // return result
      return result;
    }
  }

  /**
   * the internal base class for the actual black-box process
   * wrappers
   *
   * @param <X>
   *          the search- and solution space
   */
  private static class BlackBoxProcessWrapper1<X> extends
      BlackBoxProcessWrapper<X, X, BlackBoxProcess1NoLog<X>> {
    /**
     * create
     *
     * @param pProcess
     *          the wrapped process
     */
    BlackBoxProcessWrapper1(
        final BlackBoxProcess1NoLog<X> pProcess) {
      super(pProcess);
    }

    /** {@inheritDoc} */
    @Override
    public final double evaluate(final X y) {
      this.checkTerminated();

      this.mProcess.mSearchSpace.check(y);

      if (this.mProcess.mTerminated) {
        return Double.POSITIVE_INFINITY;
      }

      final long fes = ++this.mProcess.mConsumedFEs;
      final double result = this.mProcess.mF.evaluate(y);

      TestTools.assertFinite(result);
      TestTools.assertGreaterOrEqual(result, this.mLb);

      // did we improve
      if (result < this.mProcess.mBestF) { // yes, we did
        // so remember a copy of this best solution
        this.mProcess.mBestF = result;
        this.mProcess.mSearchSpace.copy(y, this.mProcess.mBestX);
        this.mProcess.mLastImprovementFE = fes;
        // and the time when the improvement was made
        this.mProcess.mLastImprovementTime =
            System.currentTimeMillis();

        // check if we have exhausted the granted runtime or
        // reached the quality goal
        if ((this.mProcess.mLastImprovementTime >= this.mProcess.mEndTime)
            || (result <= this.mProcess.mGoalF)) {
          this.mProcess.terminate();// terminate: we are
                                    // finished
        }
      }

      // check if we have exhausted the granted FEs
      if (fes >= this.mProcess.mMaxFEs) {
        this.mProcess.terminate();// terminate: no more FEs
      }
      // return result
      return result;
    }
  }
}

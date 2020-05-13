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
      _setLogPath(final Path path) {
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
  final IBlackBoxProcess<X, Y> _get() {
    if (this.m_mapping == null) {
      return new __BlackBoxProcessWrapper1(
          new _BlackBoxProcess1NoLog(this));
    }

    return new __BlackBoxProcessWrapper2(
        new _BlackBoxProcess2NoLog(this));
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
  private static class __BlackBoxProcessWrapper<X, Y,
      P extends _BlackBoxProcessBase<X, Y>>
      implements IBlackBoxProcess<X, Y> {

    /** the wrapped process */
    final P m_process;

    /**
     * does the calling algorithm know that it should have
     * terminated?
     */
    boolean m_knowsThatTerminated;

    /** does the caller know that we are closed? */
    boolean m_knowsThatClosed;

    /** the lower bound */
    final double m_lb;

    /** the upper bound */
    final double m_ub;

    /**
     * create
     *
     * @param process
     *          the wrapped process
     */
    __BlackBoxProcessWrapper(final P process) {
      super();
      this.m_process = Objects.requireNonNull(process);
      this.m_lb = process.m_f.lowerBound();
      this.m_ub = process.m_f.upperBound();
    }

    /** fail if we are terminated */
    final void _checkTerminated() {
      if (this.m_knowsThatTerminated) {
        Assert.fail(//
            "Algorithm performs actions after being informed that it should have terminated!"); //$NON-NLS-1$
      }
    }

    /** {@inheritDoc} */
    @Override
    public double evaluate(final X y) {
      this._checkTerminated();
      return this.m_process.evaluate(y);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean shouldTerminate() {
      final boolean st = this.m_process.m_terminated;
      if (st) {
        this.m_knowsThatTerminated = true;
      } else {
        if (this.m_knowsThatTerminated) {
          Assert.fail("Inconsistent termination state"); //$NON-NLS-1$
        }
      }
      return st;
    }

    /** {@inheritDoc} */
    @Override
    public final Random getRandom() {
      final Random r = this.m_process.getRandom();
      Assert.assertNotNull(r);
      return r;
    }

    /** {@inheritDoc} */
    @Override
    public final ISpace<X> getSearchSpace() {
      final ISpace<X> s = this.m_process.getSearchSpace();
      Assert.assertNotNull(s);
      return s;
    }

    /** {@inheritDoc} */
    @Override
    public final INullarySearchOperator<X>
        getNullarySearchOperator() {
      this._checkTerminated();
      Assert.assertNotNull(this.m_process.m_nullary);
      return this.m_process.m_nullary;
    }

    /** {@inheritDoc} */
    @Override
    public final IUnarySearchOperator<X>
        getUnarySearchOperator() {
      this._checkTerminated();
      Assert.assertNotNull(this.m_process.m_unary);
      return this.m_process.m_unary;
    }

    /** {@inheritDoc} */
    @Override
    public final IBinarySearchOperator<X>
        getBinarySearchOperator() {
      this._checkTerminated();
      Assert.assertNotNull(this.m_process.m_binary);
      return this.m_process.m_binary;
    }

    /** {@inheritDoc} */
    @Override
    public final ITernarySearchOperator<X>
        getTernarySearchOperator() {
      this._checkTerminated();
      Assert.assertNotNull(this.m_process.m_ternary);
      return this.m_process.m_ternary;
    }

    /** {@inheritDoc} */
    @Override
    public final double getBestF() {
      final double f = this.m_process.m_bestF;
      if (this.m_process.m_consumedFEs > 0L) {
        TestTools.assertFinite(f);
        TestTools.assertGreaterOrEqual(f, this.m_lb);
        TestTools.assertLessOrEqual(f, this.m_ub);
      } else {
        Assert.assertTrue(f >= Double.POSITIVE_INFINITY);
      }
      return f;
    }

    /** {@inheritDoc} */
    @Override
    public final double getGoalF() {
      return this.m_process.m_goalF;
    }

    /** {@inheritDoc} */
    @Override
    public final void getBestX(final X dest) {
      this.m_process.getBestX(dest);
    }

    /** {@inheritDoc} */
    @Override
    public final void getBestY(final Y dest) {
      this.m_process.getBestY(dest);
    }

    /** {@inheritDoc} */
    @Override
    public final long getConsumedFEs() {
      final long f;
      f = this.m_process.m_consumedFEs;
      TestTools.assertGreaterOrEqual(f, 0L);
      return f;
    }

    /** {@inheritDoc} */
    @Override
    public final long getLastImprovementFE() {
      final long i = this.m_process.m_lastImprovementFE;
      final long f = this.m_process.m_consumedFEs;
      TestTools.assertGreaterOrEqual(f, 0L);
      TestTools.assertGreaterOrEqual(f, i);
      return i;
    }

    /** {@inheritDoc} */
    @Override
    public final long getMaxFEs() {
      final long f = this.m_process.m_consumedFEs;
      final long m = this.m_process.m_maxFEs;
      TestTools.assertGreaterOrEqual(f, 0L);
      TestTools.assertGreaterOrEqual(m, f);
      return m;
    }

    /** {@inheritDoc} */
    @Override
    public final long getConsumedTime() {
      final long t = this.m_process.getConsumedTime();
      TestTools.assertGreaterOrEqual(t, 0L);
      return t;
    }

    /** {@inheritDoc} */
    @Override
    public final long getLastImprovementTime() {
      final long t = this.m_process.getConsumedTime();
      final long i = this.m_process.getLastImprovementTime();
      TestTools.assertGreaterOrEqual(t, 0L);
      TestTools.assertGreaterOrEqual(t, i);
      return i;
    }

    /** {@inheritDoc} */
    @Override
    public final long getMaxTime() {
      final long t = this.m_process.getConsumedTime();
      final long m = this.m_process.getMaxTime();
      TestTools.assertGreaterOrEqual(t, 0L);
      TestTools.assertGreaterOrEqual(m, t);
      return m;
    }

    @Override
    public final void close() throws IOException {
      if (this.m_knowsThatClosed) {
        Assert.fail("closed process twice?"); //$NON-NLS-1$
      }
      this.m_knowsThatClosed = true;
      this.m_process.close();
      this.m_knowsThatTerminated = true;
      final long fesc = this.m_process.m_consumedFEs;
      final long fesi = this.m_process.m_lastImprovementFE;
      final long fesm = this.m_process.m_maxFEs;
      TestTools.assertGreater(fesi, 0L);
      TestTools.assertGreaterOrEqual(fesc, fesi);
      TestTools.assertGreaterOrEqual(fesm, fesc);

      final long tc = this.m_process.getConsumedTime();
      final long ti = this.m_process.getLastImprovementTime();
      final long tm = this.m_process.getMaxTime();
      TestTools.assertGreaterOrEqual(ti, 0L);
      TestTools.assertGreaterOrEqual(tc, ti);
      TestTools.assertGreaterOrEqual(tm + 30, tc);

      TestTools.assertFinite(this.m_process.m_bestF);
      TestTools.assertGreaterOrEqual(this.m_process.m_bestF,
          this.m_lb);
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
  private static class __BlackBoxProcessWrapper2<X, Y>
      extends __BlackBoxProcessWrapper<X, Y,
          _BlackBoxProcess2NoLog<X, Y>> {

    /**
     * create
     *
     * @param process
     *          the wrapped process
     */
    __BlackBoxProcessWrapper2(
        final _BlackBoxProcess2NoLog<X, Y> process) {
      super(process);
    }

    /** {@inheritDoc} */
    @Override
    public final double evaluate(final X y) {
      this._checkTerminated();
      this.m_process.m_searchSpace.check(y);

      if (this.m_process.m_terminated) {
        this.m_knowsThatTerminated = true;
        return Double.POSITIVE_INFINITY;
      }

      final long fes = ++this.m_process.m_consumedFEs;
      // map and evaluate
      this.m_process.m_mapping.map(this.m_process.m_random, y,
          this.m_process.m_current);

      this.m_process.m_solutionSpace
          .check(this.m_process.m_current);

      final double result =
          this.m_process.m_f.evaluate(this.m_process.m_current);

      TestTools.assertFinite(result);
      TestTools.assertGreaterOrEqual(result, this.m_lb);
      TestTools.assertLessOrEqual(result, this.m_ub);

      // did we improve
      if (result < this.m_process.m_bestF) {// yes, we did
        // so remember a copy of this best solution
        this.m_process.m_bestF = result;
        this.m_process.m_searchSpace.copy(y,
            this.m_process.m_bestX);
        this.m_process.m_solutionSpace.copy(
            this.m_process.m_current, this.m_process.m_bestY);
        this.m_process.m_lastImprovementFE = fes; // and the
                                                  // current FE
        // and the time when the improvement was made
        this.m_process.m_lastImprovementTime =
            System.currentTimeMillis();

        // check if we have exhausted the granted runtime or
        // reached the quality goal
        if ((this.m_process.m_lastImprovementTime >= this.m_process.m_endTime)
            || (result <= this.m_process.m_goalF)) {
          this.m_process._terminate();// terminate
        }
      }

      // check if we have exhausted the granted FEs
      if (fes >= this.m_process.m_maxFEs) {
        this.m_process._terminate();// terminate: no more FEs
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
  private static class __BlackBoxProcessWrapper1<X> extends
      __BlackBoxProcessWrapper<X, X, _BlackBoxProcess1NoLog<X>> {
    /**
     * create
     *
     * @param process
     *          the wrapped process
     */
    __BlackBoxProcessWrapper1(
        final _BlackBoxProcess1NoLog<X> process) {
      super(process);
    }

    /** {@inheritDoc} */
    @Override
    public final double evaluate(final X y) {
      this._checkTerminated();

      this.m_process.m_searchSpace.check(y);

      if (this.m_process.m_terminated) {
        return Double.POSITIVE_INFINITY;
      }

      final long fes = ++this.m_process.m_consumedFEs;
      final double result = this.m_process.m_f.evaluate(y);

      TestTools.assertFinite(result);
      TestTools.assertGreaterOrEqual(result, this.m_lb);

      // did we improve
      if (result < this.m_process.m_bestF) {// yes, we did
        // so remember a copy of this best solution
        this.m_process.m_bestF = result;
        this.m_process.m_searchSpace.copy(y,
            this.m_process.m_bestX);
        this.m_process.m_lastImprovementFE = fes;
        // and the time when the improvement was made
        this.m_process.m_lastImprovementTime =
            System.currentTimeMillis();

        // check if we have exhausted the granted runtime or
        // reached the quality goal
        if ((this.m_process.m_lastImprovementTime >= this.m_process.m_endTime)
            || (result <= this.m_process.m_goalF)) {
          this.m_process._terminate();// terminate: we are
                                      // finished
        }
      }

      // check if we have exhausted the granted FEs
      if (fes >= this.m_process.m_maxFEs) {
        this.m_process._terminate();// terminate: no more FEs
      }
      // return result
      return result;
    }
  }
}

package aitoa.structure;

import java.util.Objects;

/**
 * the abstract base class for black box problems with two
 * different spaces
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
abstract class _BlackBoxProblem2<X, Y>
    extends _BlackBoxProblem1<X, Y> {
  /** the solution space */
  final ISpace<Y> m_solutionSpace;
  /** the representation mapping */
  final IRepresentationMapping<X, Y> m_mapping;
  /** the current candidate solution */
  final Y m_current;
  /** the best-so-far candidate solution */
  final Y m_bestY;

  /**
   * Create the base class of the black box problem
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
   */
  _BlackBoxProblem2(final ISpace<X> searchSpace,
      final ISpace<Y> solutionSpace,
      final IRepresentationMapping<X, Y> mapping,
      final IObjectiveFunction<Y> f, final long maxFEs,
      final long maxTime, final double goalF) {
    super(searchSpace, f, maxFEs, maxTime, goalF);
    this.m_solutionSpace = Objects.requireNonNull(solutionSpace);
    this.m_mapping = Objects.requireNonNull(mapping);
    this.m_bestY = this.m_solutionSpace.create();
    this.m_current = this.m_solutionSpace.create();
  }
}

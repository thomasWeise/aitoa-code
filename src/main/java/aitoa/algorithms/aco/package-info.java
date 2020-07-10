/**
 * This package provides an implementation of Ant Colony
 * Optimization (ACO). The idea is to implement ACO as an
 * Estimation of Distribution Algorithm (EDA), where the model
 * sampling is biased by the heuristic information. We therefore
 * develop the base class {@link aitoa.algorithms.aco.ACOModel},
 * which is an EDA-style model and has additional methods
 * ({@link aitoa.algorithms.aco.ACOModel#permutationFromX(Object)},
 * {@link aitoa.algorithms.aco.ACOModel#getCostOfAppending(int, Object)},
 * and
 * {@link aitoa.algorithms.aco.ACOModel#append(int, Object)}),
 * which allow for a mapping between whatever space we are
 * working in and permutations and for implementing this bias.
 */
package aitoa.algorithms.aco;

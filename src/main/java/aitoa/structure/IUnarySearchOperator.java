package aitoa.structure;

import java.util.Random;
import java.util.function.Predicate;

/**
 * This interface encapsulates a unary search operator, which can
 * sample one new point in the search space by using the
 * information of an existing one.
 *
 * @param <X>
 *          the search space
 */
@FunctionalInterface
// start relevant
public interface IUnarySearchOperator<X> {

  /**
   * Apply the search operator to sample a new point in the
   * search space from an existing one.
   *
   * @param x
   *          the source point
   * @param dest
   *          the destination object to be overwritten with the
   *          newly sampled point
   * @param random
   *          a random number generator
   */
  public abstract void apply(final X x, final X dest,
      final Random random);
// end relevant

  /**
   * Exhaustively enumerate the complete neighborhood of the
   * point {@code x}.
   * <p>
   * Each of the neighbor elements, one by one, is written to the
   * destination {@code dest}, which is then passed to the method
   * {@link java.util.function.Predicate#test(Object)}. If this
   * predicate function returns {@code true}, the enumeration is
   * aborted and this function returns {@code true} as well. If
   * the predicate {@code visitor} returns {@code false}, the
   * enumeration is continued. If there are no neighbors of
   * {@code x} anymore to enumerate, this function stops and
   * returns {@code false}.
   * <p>
   * In other words, you can enumerate a sub-set of the
   * neighborhood of {@code x} by returning {@code true}
   * somewhere along the way. For example, if an improving move
   * was found, we can simply stop. Or if the
   * {@linkplain aitoa.structure.ITerminationCriterion
   * termination criterion} returned {@code true}. Otherwise, we
   * may just continue enumerating the neighbors. If
   * {@code visitor} returned {@code true} <em>and</em> the
   * enumeration of the neighborhood of {@code x} has finished at
   * the same time, {@code true} is returned.
   * <p>
   * The {@code visitor} can, for instance, perform the
   * {@linkplain aitoa.structure.IRepresentationMapping
   * representation mapping} and evaluate the resulting candidate
   * solution using the
   * {@linkplain aitoa.structure.IObjectiveFunction objective
   * function}.
   * <p>
   * Notice that this function may be implemented in a
   * deterministic way. However, there is no guarantee about the
   * order in which the neighboring points of {@code x} are
   * enumerated. In order to allow for a potential randomized
   * (though still necessarily exhaustive) enumeration, the
   * parameter {@code random} provides a random number generator.
   * <p>
   * Notice that the variable {@code dest} <em>must not be</em>
   * modified by the predicate receiving it. Otherwise, the
   * behavior of this function is unspecified.
   * <p>
   * This is an optional operation that does not need to be
   * implemented by a unary search operator. If exhaustive
   * enumeration is not supported, a
   * {@link java.lang.UnsupportedOperationException} should be
   * thrown, as done by this default implementation. In this
   * case, {@link #canEnumerate()} should return {@code false}.
   * If {@link #canEnumerate()} is implemented to return
   * {@code true}, then
   * {@link #enumerate(java.util.Random, Object, Object, Predicate)}
   * must be implemented in a reasonable way as well and must not
   * throw an @link java.lang.UnsupportedOperationException}.
   *
   * @param random
   *          a random number generator
   * @param x
   *          the point from the search space whose neighborhood
   *          we want to enumerate
   * @param dest
   *          the destination record overwritten with the
   *          neighbors and passed to the predicate
   *          {@code visitor}
   * @param visitor
   *          the predicate deciding whether to stop (return
   *          {@code true}) or continue (return {@code false})
   *          the exhaustive enumeration of the neighborhood of
   *          {@code x}
   * @return {@code false} if the neighborhood of {@code x} has
   *         been enumerated exhaustively/completely, (and
   *         {@code visitor} never returned {@code true}),
   *         {@code true} if {@code visitor} returned
   *         {@code true}
   * @see #canEnumerate()
   * @throws java.lang.UnsupportedOperationException
   *           if the neighborhood defined by this operator
   *           cannot be enumerated in a reasonable way
   */
// start enumerate
  public default boolean enumerate(final Random random,
      final X x, final X dest, final Predicate<X> visitor) {
    throw new UnsupportedOperationException("The operator " + //$NON-NLS-1$
        this.getClass().getName() + //
        " does not support exhaustive enumeration of neighborhoods.");//$NON-NLS-1$
  }
// end enumerate

  /**
   * This method allows an algorithm to query whether
   * {@link #enumerate(java.util.Random, Object, Object, Predicate)}
   * can be called.
   *
   * @return {@code true} if
   *         {@link #enumerate(java.util.Random, Object, Object, Predicate)}
   *         can be used, {@code false} if and only if
   *         {@link #enumerate(java.util.Random, Object, Object, Predicate)}
   *         will throw a
   *         {@link java.lang.UnsupportedOperationException}
   * @see #enumerate(java.util.Random, Object, Object, Predicate)
   */
  public default boolean canEnumerate() {
    return false;
  }

// start relevant
}
// end relevant

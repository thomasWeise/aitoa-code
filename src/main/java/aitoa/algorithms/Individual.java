package aitoa.algorithms;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * the individual record: hold one point in search space and its
 * quality
 *
 * @param <X>
 *          the data structure of the search space
 */
public class Individual<X> implements Supplier<X> {

  /** The comparator to be used for sorting according quality */
  public static final Comparator<Individual<?>> BY_QUALITY =
      (a, b) -> Double.compare(a.quality, b.quality);

  /** the point in the search space */
  public final X x;
  /** the quality */
  public double quality;

  /**
   * create the individual record
   *
   * @param _x
   *          the point in the search space
   * @param _q
   *          the quality
   */
  public Individual(final X _x, final double _q) {
    super();
    this.x = Objects.requireNonNull(_x);
    this.quality = _q;
  }

  /** {@inheritDoc} */
  @Override
  public final X get() {
    return this.x;
  }
}

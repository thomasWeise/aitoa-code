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
   * @param pX
   *          the point in the search space
   * @param pQ
   *          the quality
   */
  public Individual(final X pX, final double pQ) {
    super();
    this.x = Objects.requireNonNull(pX);
    this.quality = pQ;
  }

  /** {@inheritDoc} */
  @Override
  public final X get() {
    return this.x;
  }
}

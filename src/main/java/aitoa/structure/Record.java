package aitoa.structure;

import java.util.Comparator;
import java.util.Objects;

/**
 * A re-usable record that can hold {@linkplain #x one point in
 * search space} and its {@link #quality}, i.e., the result of
 * applying the objective function to {@link #x}. More formally,
 * the point {@link #x} will be passed to the
 * {@link IBlackBoxProcess#evaluate(Object)} function, which may
 * internally perform a {@linkplain IRepresentationMapping
 * representation mapping} and then invoke the
 * {@linkplain IObjectiveFunction objective function}. The result
 * of this call can then be stored in {@link #x}. This allows for
 * better book-keeping if we need to handle multiple solutions.
 * Neither {@link #equals(Object)} nor {@link #hashCode()} are
 * overridden, since this contain is explicitly designed to be
 * mutable.
 *
 * @param <X>
 *          the data structure of the search space
 */
public class Record<X> {

  /** The comparator to be used for sorting according quality */
  public static final Comparator<Record<?>> BY_QUALITY =
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
  public Record(final X pX, final double pQ) {
    super();
    this.x = Objects.requireNonNull(pX);
    this.quality = pQ;
  }
}
